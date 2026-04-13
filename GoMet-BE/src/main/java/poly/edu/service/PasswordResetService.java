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
 *  - cryptographically secure token generation
 *  - SHA-256 token hashing (only hash stored in DB)
 *  - DB-backed single-use + expiry enforcement
 *  - BCrypt hashing of the new password
 *  - DB-level rate limiting (per account + per IP)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int TOKEN_BYTES       = 64;          // 64 random bytes → 128 hex chars
    private static final long TOKEN_TTL_MINUTES = 30;         // token valid for 30 minutes
    private static final int  RATE_WINDOW_MIN  = 15;          // rate-limit window
    private static final int  RATE_MAX_PER_ACC = 3;           // max 3 requests per account in window
    private static final int  RATE_MAX_PER_IP  = 10;          // max 10 requests per IP  in window

    // Password policy constants
    private static final int  PW_MIN_LENGTH    = 8;

    private final AccountDAO            accountDAO;
    private final PasswordResetTokenDAO tokenDAO;
    private final EmailService          emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    // ─── PUBLIC API ──────────────────────────────────────────────────────────

    /**
     * Step 1: Look up account by email or username, generate token, store hash, send email.
     * Always returns without revealing whether the account actually exists.
     *
     * @param identifier email or username (case-insensitive)
     * @param requestIp  caller's IP for rate limiting
     */
    @Transactional
    public void processForgotPassword(String identifier, String requestIp) {
        if (identifier == null || identifier.isBlank()) return;
        String id = identifier.trim().toLowerCase();

        // Rate-limit by IP first (before any DB account lookup)
        Instant windowStart = Instant.now().minusSeconds(RATE_WINDOW_MIN * 60L);
        if (tokenDAO.countRecentByIp(requestIp, windowStart) >= RATE_MAX_PER_IP) {
            log.warn("Rate limit hit for IP={}", requestIp);
            return; // still silent
        }

        Optional<Account> accountOpt = accountDAO.findByEmail(id)
                .or(() -> accountDAO.findByUsernameIgnoreCase(id));

        // Do NOT reveal non-existence — just return silently if not found
        if (accountOpt.isEmpty()) {
            log.debug("No account found for identifier={}", id);
            return;
        }

        Account account = accountOpt.get();

        // Rate-limit by account
        if (tokenDAO.countRecentByAccount(account.getAccountID(), windowStart) >= RATE_MAX_PER_ACC) {
            log.warn("Rate limit hit for accountId={}", account.getAccountID());
            return;
        }

        // Generate token
        String rawToken   = generateSecureToken();
        String tokenHash  = sha256Hex(rawToken);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .accountId(account.getAccountID())
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plusSeconds(TOKEN_TTL_MINUTES * 60L))
                .createdAt(Instant.now())
                .requestIp(requestIp)
                .build();

        tokenDAO.save(resetToken);

        try {
            emailService.sendResetPasswordEmail(account.getEmail(), account.getUsername(), rawToken);
        } catch (Exception e) {
            log.error("Failed to send reset email to {}: {}", account.getEmail(), e.getMessage());
            // Don't propagate — caller gets the generic success message regardless
        }
    }

    /**
     * Step 2: Validate token, update password, mark token used, invalidate all other tokens.
     *
     * @param rawToken   the raw token from the URL query string
     * @param newPassword plain-text new password
     * @return true on success, false if token invalid/expired
     */
    @Transactional
    public boolean resetPassword(String rawToken, String newPassword) {
        validatePassword(newPassword); // throws IllegalArgumentException if bad

        String hash = sha256Hex(rawToken);
        Optional<PasswordResetToken> tokenOpt = tokenDAO.findValid(hash, Instant.now());

        if (tokenOpt.isEmpty()) {
            return false; // expired, used, or fake token
        }

        PasswordResetToken token = tokenOpt.get();

        // Mark this token used
        token.setUsedAt(Instant.now());
        tokenDAO.save(token);

        // Invalidate all other pending tokens for this account
        tokenDAO.invalidateAllForAccount(token.getAccountId(), Instant.now());

        // Update password with BCrypt hash
        Account account = accountDAO.findById(token.getAccountId())
                .orElseThrow(() -> new IllegalStateException("Account not found for id=" + token.getAccountId()));

        account.setPassword(passwordEncoder.encode(newPassword));
        accountDAO.save(account);

        log.info("Password reset for accountId={}", account.getAccountID());
        return true;
    }

    // ─── PRIVATE HELPERS ─────────────────────────────────────────────────────

    private String generateSecureToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    /** SHA-256 of input string, returned as lowercase hex. */
    public static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Basic password policy:
     * - min 8 chars
     * - at least 1 uppercase
     * - at least 1 lowercase
     * - at least 1 digit
     */
    private void validatePassword(String pw) {
        if (pw == null || pw.length() < PW_MIN_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + PW_MIN_LENGTH + " characters");
        }
        if (!pw.chars().anyMatch(Character::isUpperCase)) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!pw.chars().anyMatch(Character::isLowerCase)) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!pw.chars().anyMatch(Character::isDigit)) {
            throw new IllegalArgumentException("Password must contain at least one number");
        }
    }
}
