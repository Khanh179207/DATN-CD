package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.LoginAttemptDAO;
import poly.edu.entity.Account;
import poly.edu.entity.LoginAttempt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Phase 1 — Brute-Force Protection.
 *
 * Two-layer rate limiting:
 *   1. Per-IP:         > 20 failures in 15 min  → block IP
 *   2. Per-identifier: > 10 failures in 15 min  → challenge (CAPTCHA hint returned)
 *   3. Per-account:    > N failures              → progressive lockout
 *
 * Lockout schedule (per account):
 *   Attempt 5  →  30 s
 *   Attempt 6  →  60 s
 *   Attempt 7  →  120 s
 *   … up to MAX_LOCKOUT_SECONDS
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    // ── Config ────────────────────────────────────────────────────────────────
    @Value("${security.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${security.lockout-base-seconds:30}")
    private long lockoutBaseSeconds;

    @Value("${security.max-lockout-seconds:3600}")
    private long maxLockoutSeconds;

    // Sliding window
    private static final Duration RATE_WINDOW     = Duration.ofMinutes(15);
    private static final int      MAX_PER_IP      = 20;
    private static final int      MAX_PER_IDENT   = 10;

    private final LoginAttemptDAO loginAttemptDAO;
    private final AccountDAO      accountDAO;

    // ── Rate-check API ────────────────────────────────────────────────────────

    /** Returns true if the IP should be blocked (too many failures in window). */
    public boolean isIpBlocked(String ip) {
        Instant since = Instant.now().minus(RATE_WINDOW);
        long count = loginAttemptDAO.countFailedByIp(ip, since);
        return count >= MAX_PER_IP;
    }

    /** Returns true if the identifier (email/username) is being hammered. */
    public boolean isIdentifierRateLimited(String identifier) {
        Instant since = Instant.now().minus(RATE_WINDOW);
        long count = loginAttemptDAO.countFailedByIdentifier(identifier.toLowerCase(), since);
        return count >= MAX_PER_IDENT;
    }

    /** Returns true if the account is currently locked out. */
    public boolean isAccountLocked(Account account) {
        return account.isLocked();
    }

    // ── Record attempts ───────────────────────────────────────────────────────

    @Transactional
    public void recordSuccess(Account account, String ip, String userAgent, String attemptType) {
        saveAttempt(account.getAccountID(), account.getEmail(), ip, userAgent, attemptType, true, null);

        // Reset failure counter + lockout
        account.setFailedLoginCount(0);
        account.setLockUntil(null);
        accountDAO.save(account);

        log.debug("Login success recorded for accountId={}", account.getAccountID());
    }

    @Transactional
    public void recordFailure(Account account, String ip, String userAgent,
                              String attemptType, String reason) {
        saveAttempt(account.getAccountID(), account.getEmail(), ip, userAgent, attemptType, false, reason);

        int newCount = account.getFailedLoginCount() + 1;
        account.setFailedLoginCount(newCount);

        if (newCount >= maxFailedAttempts) {
            long lockSeconds = Math.min(lockoutBaseSeconds * (newCount - maxFailedAttempts + 1),
                                        maxLockoutSeconds);
            Instant lockUntil = Instant.now().plusSeconds(lockSeconds);
            account.setLockUntil(lockUntil);
            log.info("Account {} locked for {} seconds (failCount={})",
                     account.getAccountID(), lockSeconds, newCount);
        }

        accountDAO.save(account);
    }

    public void recordAnonymousFailure(String identifier, String ip, String userAgent, String reason) {
        saveAttempt(null, identifier, ip, userAgent, "PASSWORD", false, reason);
    }

    // ── Lockout remaining seconds ─────────────────────────────────────────────

    public long lockoutRemainingSeconds(Account account) {
        if (account.getLockUntil() == null) return 0;
        long remaining = Duration.between(Instant.now(), account.getLockUntil()).getSeconds();
        return Math.max(0, remaining);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void saveAttempt(Integer userId, String identifier, String ip,
                             String userAgent, String attemptType, boolean success, String reason) {
        LoginAttempt attempt = LoginAttempt.builder()
                .userId(userId)
                .identifier(identifier != null ? identifier.toLowerCase() : "")
                .ip(ip)
                .userAgentHash(sha256(userAgent))
                .attemptType(attemptType)
                .success(success)
                .failureReason(reason)
                .createdAt(Instant.now())
                .build();
        loginAttemptDAO.save(attempt);
    }

    public static String sha256(String input) {
        if (input == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /** Resolve client IP, respecting common proxy headers.
     *  Priority: X-Client-IP (set by FE via ipify) → X-Forwarded-For → X-Real-IP → remoteAddr */
    public static String resolveIp(jakarta.servlet.http.HttpServletRequest req) {
        // 1. Trust IP the browser reported via ipify (real public IPv4/IPv6)
        String clientIp = req.getHeader("X-Client-IP");
        if (clientIp != null && !clientIp.isBlank()) return clientIp.trim();
        // 2. Reverse-proxy headers
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        String realIp = req.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) return realIp.trim();
        return req.getRemoteAddr();
    }

    /** Derive a device name from a User-Agent string. */
    public static String inferDeviceName(String userAgent) {
        if (userAgent == null) return "Unknown device";
        if (userAgent.contains("Windows")) return userAgent.contains("Chrome") ? "Chrome on Windows"
                : userAgent.contains("Firefox") ? "Firefox on Windows" : "Browser on Windows";
        if (userAgent.contains("Macintosh") || userAgent.contains("Mac OS X"))
            return userAgent.contains("Chrome") ? "Chrome on Mac" : "Browser on Mac";
        if (userAgent.contains("Android")) return "Mobile (Android)";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "Mobile (iOS)";
        if (userAgent.contains("Linux")) return "Browser on Linux";
        return "Unknown device";
    }
}
