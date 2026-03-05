package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.PasswordResetTokenDAO;
import poly.edu.entity.Account;
import poly.edu.entity.PasswordResetToken;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Handles the full forgot-password / reset-password lifecycle:
 *  - Cryptographically secure token generation
 *  - SHA-256 token hashing (only hash stored in DB)
 *  - DB-backed single-use + expiry enforcement
 *  - BCrypt hashing of the new password
 *  - DB-level rate limiting (per account + per IP)
 *  - Revoke all active sessions after successful reset (Phase 5)
 *  - Delegate password policy to PasswordPolicyService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int  TOKEN_BYTES        = 64;   // 64 random bytes → 128 hex chars
    private static final long TOKEN_TTL_MINUTES  = 15;   // 15-minute token (tighter than before)
    private static final int  RATE_WINDOW_MIN    = 15;
    private static final int  RATE_MAX_PER_ACC   = 3;
    private static final int  RATE_MAX_PER_IP    = 10;

    private final AccountDAO             accountDAO;
    private final PasswordResetTokenDAO  tokenDAO;
    private final EmailService           emailService;
    private final BCryptPasswordEncoder  passwordEncoder;
    private final PasswordPolicyService  policyService;
    private final TokenService           tokenService;
    private final AuditLogService        auditLogService;

    // ─── PUBLIC API ──────────────────────────────────────────────────────────

    /**
     * @return true  – reset email queued successfully
     *         false – account not found (caller should return 404)
     *         throws RuntimeException – rate-limited (caller should return 429)
     */
    @Transactional
    public boolean processForgotPassword(String identifier, String requestIp, String userAgent) {
        if (identifier == null || identifier.isBlank()) return false;
        String id = identifier.trim().toLowerCase();

        Instant windowStart = Instant.now().minusSeconds(RATE_WINDOW_MIN * 60L);
        if (tokenDAO.countRecentByIp(requestIp, windowStart) >= RATE_MAX_PER_IP) {
            log.warn("Forgot-password rate limit hit for IP={}", requestIp);
            throw new RuntimeException("RATE_LIMITED");
        }

        Optional<Account> accountOpt = accountDAO.findByEmail(id)
                .or(() -> accountDAO.findByUsernameIgnoreCase(id));

        if (accountOpt.isEmpty()) {
            log.debug("No account found for identifier={}", id);
            return false;
        }

        Account account = accountOpt.get();

        if (tokenDAO.countRecentByAccount(account.getAccountID(), windowStart) >= RATE_MAX_PER_ACC) {
            log.warn("Forgot-password rate limit hit for accountId={}", account.getAccountID());
            throw new RuntimeException("RATE_LIMITED");
        }

        String rawToken  = generateSecureToken();
        String tokenHash = sha256Hex(rawToken);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .accountId(account.getAccountID())
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plusSeconds(TOKEN_TTL_MINUTES * 60L))
                .createdAt(Instant.now())
                .requestIp(requestIp)
                .createdUaHash(sha256Hex(userAgent != null ? userAgent : ""))
                .build();

        tokenDAO.save(resetToken);

        try {
            emailService.sendResetPasswordEmail(account.getEmail(), account.getUsername(), rawToken);
            auditLogService.log(account.getAccountID(), AuditLogService.PASSWORD_RESET_REQUEST,
                    requestIp, userAgent, java.util.Map.of("identifier", id));
        } catch (Exception e) {
            log.error("Failed to send reset email to {}: {}", account.getEmail(), e.getMessage());
        }
        return true;
    }

    /** Backward-compatible overload for callers without userAgent. */
    @Transactional
    public boolean processForgotPassword(String identifier, String requestIp) {
        return processForgotPassword(identifier, requestIp, null);
    }

    @Transactional
    public boolean resetPassword(String rawToken, String newPassword) {
        return resetPasswordAndReturnAccount(rawToken, newPassword) != null;
    }

    @Transactional
    public Account resetPasswordAndReturnAccount(String rawToken, String newPassword) {
        // Delegate full policy validation (12 chars, special char, common list etc.)
        policyService.validate(newPassword);

        String hash = sha256Hex(rawToken);
        Optional<PasswordResetToken> tokenOpt = tokenDAO.findValid(hash, Instant.now());

        if (tokenOpt.isEmpty()) {
            return null;
        }

        PasswordResetToken token = tokenOpt.get();

        token.setUsedAt(Instant.now());
        tokenDAO.save(token);

        tokenDAO.invalidateAllForAccount(token.getAccountId(), Instant.now());

        Account account = accountDAO.findById(token.getAccountId())
                .orElseThrow(() -> new IllegalStateException("Account not found"));

        account.setPassword(passwordEncoder.encode(newPassword));
        account.setPasswordUpdatedAt(Instant.now());
        accountDAO.save(account);

        // Revoke ALL active sessions (Phase 5 requirement)
        tokenService.revokeAllSessions(account.getAccountID());

        auditLogService.log(account.getAccountID(), AuditLogService.PASSWORD_RESET_SUCCESS,
                token.getRequestIp(), null, java.util.Map.of());

        log.info("Password reset for accountId={}", account.getAccountID());
        return account;
    }

    // ─── PRIVATE HELPERS ─────────────────────────────────────────────────────

    private String generateSecureToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    public static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
