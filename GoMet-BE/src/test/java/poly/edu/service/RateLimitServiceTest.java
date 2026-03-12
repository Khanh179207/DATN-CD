package poly.edu.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.LoginAttemptDAO;
import poly.edu.entity.Account;
import poly.edu.entity.LoginAttempt;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RateLimitService}.
 *
 * Covers:
 *  - IP-based blocking threshold
 *  - Identifier rate limiting
 *  - Account lockout after N failures
 *  - Progressive lockout duration formula
 *  - recordSuccess resets the counter
 *  - sha256 static helper contract
 */
@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @Mock private LoginAttemptDAO loginAttemptDAO;
    @Mock private AccountDAO      accountDAO;

    @InjectMocks
    private RateLimitService rateLimitService;

    @BeforeEach
    void injectValues() {
        // Inject @Value fields (default values from application.properties)
        ReflectionTestUtils.setField(rateLimitService, "maxFailedAttempts",    5);
        ReflectionTestUtils.setField(rateLimitService, "lockoutBaseSeconds",  30L);
        ReflectionTestUtils.setField(rateLimitService, "maxLockoutSeconds", 3600L);
    }

    // ─── IP Blocking ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("isIpBlocked returns false when failures < 20 in 15 min")
    void isIpBlocked_belowThreshold_returnsFalse() {
        when(loginAttemptDAO.countFailedByIp(eq("1.2.3.4"), any(Instant.class)))
                .thenReturn(19L);
        assertThat(rateLimitService.isIpBlocked("1.2.3.4")).isFalse();
    }

    @Test
    @DisplayName("isIpBlocked returns true when failures == 20 in 15 min")
    void isIpBlocked_atThreshold_returnsTrue() {
        when(loginAttemptDAO.countFailedByIp(eq("1.2.3.4"), any(Instant.class)))
                .thenReturn(20L);
        assertThat(rateLimitService.isIpBlocked("1.2.3.4")).isTrue();
    }

    @Test
    @DisplayName("isIpBlocked returns true when failures > 20 in 15 min")
    void isIpBlocked_aboveThreshold_returnsTrue() {
        when(loginAttemptDAO.countFailedByIp(eq("10.0.0.1"), any(Instant.class)))
                .thenReturn(50L);
        assertThat(rateLimitService.isIpBlocked("10.0.0.1")).isTrue();
    }

    // ─── Identifier Rate Limiting ─────────────────────────────────────────────

    @Test
    @DisplayName("isIdentifierRateLimited returns false below the 10-attempt threshold")
    void isIdentifierRateLimited_belowThreshold_returnsFalse() {
        when(loginAttemptDAO.countFailedByIdentifier(eq("user@example.com"), any(Instant.class)))
                .thenReturn(9L);
        assertThat(rateLimitService.isIdentifierRateLimited("user@example.com")).isFalse();
    }

    @Test
    @DisplayName("isIdentifierRateLimited returns true at and above the 10-attempt threshold")
    void isIdentifierRateLimited_atThreshold_returnsTrue() {
        when(loginAttemptDAO.countFailedByIdentifier(any(), any(Instant.class)))
                .thenReturn(10L);
        assertThat(rateLimitService.isIdentifierRateLimited("user@example.com")).isTrue();
    }

    // ─── Account Lockout ──────────────────────────────────────────────────────

    @Test
    @DisplayName("isAccountLocked returns false for an account with no lockUntil")
    void isAccountLocked_notLocked_returnsFalse() {
        Account account = stubAccount(0, null);
        assertThat(rateLimitService.isAccountLocked(account)).isFalse();
    }

    @Test
    @DisplayName("isAccountLocked returns false when lockUntil is in the past")
    void isAccountLocked_lockExpired_returnsFalse() {
        Account account = stubAccount(5, Instant.now().minusSeconds(60));
        assertThat(rateLimitService.isAccountLocked(account)).isFalse();
    }

    @Test
    @DisplayName("isAccountLocked returns true when lockUntil is in the future")
    void isAccountLocked_locked_returnsTrue() {
        Account account = stubAccount(6, Instant.now().plusSeconds(300));
        assertThat(rateLimitService.isAccountLocked(account)).isTrue();
    }

    // ─── recordFailure progressive lockout ────────────────────────────────────

    @Test
    @DisplayName("recordFailure does NOT set lockUntil before reaching maxFailedAttempts(=5)")
    void recordFailure_belowMax_noLockout() {
        Account account = stubAccount(3, null); // 4th failure (3 → 4)

        rateLimitService.recordFailure(account, "1.2.3.4", "Mozilla", "PASSWORD", "BAD_PW");

        assertThat(account.getFailedLoginCount()).isEqualTo(4);
        assertThat(account.getLockUntil()).isNull();
        verify(accountDAO).save(account);
    }

    @Test
    @DisplayName("recordFailure sets lockUntil on the 5th failure (first lockout = 30 s)")
    void recordFailure_atMax_setsLockout() {
        Account account = stubAccount(4, null); // 5th failure triggers lockout

        rateLimitService.recordFailure(account, "1.2.3.4", "Mozilla", "PASSWORD", "BAD_PW");

        assertThat(account.getFailedLoginCount()).isEqualTo(5);
        assertThat(account.getLockUntil()).isNotNull();
        // lockUntil should be ~30 seconds from now
        assertThat(account.getLockUntil()).isAfter(Instant.now().plusSeconds(25));
        assertThat(account.getLockUntil()).isBefore(Instant.now().plusSeconds(35));
    }

    @Test
    @DisplayName("recordFailure caps lockout duration at maxLockoutSeconds (3600 s)")
    void recordFailure_highCount_capsAt3600() {
        // After very many failures, the cap kicks in
        Account account = stubAccount(200, null);

        rateLimitService.recordFailure(account, "1.2.3.4", "Mozilla", "PASSWORD", "BAD_PW");

        assertThat(account.getLockUntil()).isAfter(Instant.now().plusSeconds(3598));
        assertThat(account.getLockUntil()).isBefore(Instant.now().plusSeconds(3602));
    }

    // ─── recordSuccess ────────────────────────────────────────────────────────

    @Test
    @DisplayName("recordSuccess resets failedLoginCount and clears lockUntil")
    void recordSuccess_resetsCounterAndLock() {
        Account account = stubAccount(7, Instant.now().plusSeconds(120));

        rateLimitService.recordSuccess(account, "1.2.3.4", "Mozilla", "PASSWORD");

        assertThat(account.getFailedLoginCount()).isZero();
        assertThat(account.getLockUntil()).isNull();
        verify(accountDAO).save(account);
    }

    // ─── sha256 static helper ─────────────────────────────────────────────────

    @Test
    @DisplayName("sha256(null) returns null")
    void sha256_null_returnsNull() {
        assertThat(RateLimitService.sha256(null)).isNull();
    }

    @Test
    @DisplayName("sha256 produces a 64-character lowercase hex string")
    void sha256_knownInput_correct() {
        // sha256("") = e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
        String result = RateLimitService.sha256("");
        assertThat(result).hasSize(64).matches("[0-9a-f]+");
    }

    @Test
    @DisplayName("sha256 is deterministic for the same input")
    void sha256_deterministic() {
        String a = RateLimitService.sha256("GoMet-security");
        String b = RateLimitService.sha256("GoMet-security");
        assertThat(a).isEqualTo(b);
    }

    @Test
    @DisplayName("sha256 produces different results for different inputs")
    void sha256_differentInputs_differentResults() {
        assertThat(RateLimitService.sha256("abc"))
                .isNotEqualTo(RateLimitService.sha256("ABC"));
    }

    // ─── lockoutRemainingSeconds ──────────────────────────────────────────────

    @Test
    @DisplayName("lockoutRemainingSeconds returns 0 for an unlocked account")
    void lockoutRemaining_noLock_zero() {
        Account account = stubAccount(0, null);
        assertThat(rateLimitService.lockoutRemainingSeconds(account)).isZero();
    }

    @Test
    @DisplayName("lockoutRemainingSeconds returns 0 when lock has expired")
    void lockoutRemaining_expired_zero() {
        Account account = stubAccount(5, Instant.now().minusSeconds(10));
        assertThat(rateLimitService.lockoutRemainingSeconds(account)).isZero();
    }

    @Test
    @DisplayName("lockoutRemainingSeconds returns a positive value when account is locked")
    void lockoutRemaining_locked_positive() {
        Account account = stubAccount(5, Instant.now().plusSeconds(300));
        long remaining = rateLimitService.lockoutRemainingSeconds(account);
        assertThat(remaining).isGreaterThan(0).isLessThanOrEqualTo(300);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Build a minimal Account stub for testing without touching the database.
     * We mock {@link LoginAttemptDAO#save} to prevent actual DB writes.
     */
    private Account stubAccount(int failedCount, Instant lockUntil) {
        Account account = new Account();
        account.setAccountID(1);
        account.setEmail("test@gomet.vn");
        account.setFailedLoginCount(failedCount);
        account.setLockUntil(lockUntil);
        // Return persisted entity shape without touching DB
        lenient().when(loginAttemptDAO.save(any(LoginAttempt.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        return account;
    }
}
