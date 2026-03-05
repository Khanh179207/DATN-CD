package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import poly.edu.dao.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Scheduled cleanup job — removes expired/used security tokens and old audit logs.
 *
 * Retention policy (configurable via constants):
 *   - Expired refresh tokens / magic links / password reset tokens → 7 days after expiry
 *   - Login attempts → 90 days
 *   - Audit logs → 365 days
 *   - Used backup codes → 30 days after use
 *
 * Runs daily at 03:00 UTC.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityCleanupJob {

    private static final int REFRESH_TOKEN_RETAIN_DAYS = 7;
    private static final int LOGIN_ATTEMPT_RETAIN_DAYS  = 90;
    private static final int AUDIT_LOG_RETAIN_DAYS      = 365;
    private static final int BACKUP_CODE_RETAIN_DAYS    = 30;
    private static final int MAGIC_LINK_RETAIN_DAYS     = 7;
    private static final int RESET_TOKEN_RETAIN_DAYS    = 7;

    private static final int RATE_LIMIT_EVENT_RETAIN_DAYS = 1;  // only needed for real-time checks

    private final RefreshTokenDAO       refreshTokenDAO;
    private final LoginAttemptDAO       loginAttemptDAO;
    private final AuditLogDAO           auditLogDAO;
    private final BackupCodeDAO         backupCodeDAO;
    private final MagicLinkTokenDAO     magicLinkTokenDAO;
    private final PasswordResetTokenDAO passwordResetTokenDAO;
    private final RateLimitEventDAO     rateLimitEventDAO;

    /** Runs daily at 03:00 UTC. */
    @Scheduled(cron = "0 0 3 * * *", zone = "UTC")
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        log.info("Starting nightly security cleanup...");

        try {
            refreshTokenDAO.deleteExpiredBefore(now.minus(REFRESH_TOKEN_RETAIN_DAYS, ChronoUnit.DAYS));
            log.debug("Cleaned up expired refresh tokens");

            loginAttemptDAO.deleteOlderThan(now.minus(LOGIN_ATTEMPT_RETAIN_DAYS, ChronoUnit.DAYS));
            log.debug("Cleaned up old login attempts");

            auditLogDAO.deleteOlderThan(now.minus(AUDIT_LOG_RETAIN_DAYS, ChronoUnit.DAYS));
            log.debug("Cleaned up old audit logs");

            backupCodeDAO.deleteUsedBefore(now.minus(BACKUP_CODE_RETAIN_DAYS, ChronoUnit.DAYS));
            log.debug("Cleaned up used backup codes");

            magicLinkTokenDAO.deleteExpiredBefore(now.minus(MAGIC_LINK_RETAIN_DAYS, ChronoUnit.DAYS));
            log.debug("Cleaned up expired magic link tokens");

            // PasswordResetToken cleanup
            passwordResetTokenDAO.deleteExpiredBefore(now.minus(RESET_TOKEN_RETAIN_DAYS, ChronoUnit.DAYS));
            log.debug("Cleaned up expired password reset tokens");

            // Rate-limit events (only relevant within the last window; purge after 1 day)
            rateLimitEventDAO.deleteByCreatedAtBefore(now.minus(RATE_LIMIT_EVENT_RETAIN_DAYS, ChronoUnit.DAYS));
            log.debug("Cleaned up old rate-limit events");

            log.info("Security cleanup complete");
        } catch (Exception e) {
            log.error("Security cleanup failed: {}", e.getMessage(), e);
        }
    }
}
