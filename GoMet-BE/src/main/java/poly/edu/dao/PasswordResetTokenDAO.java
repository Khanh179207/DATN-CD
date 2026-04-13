package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.PasswordResetToken;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenDAO extends JpaRepository<PasswordResetToken, Long> {

    /** Find a valid (unused, not expired) token by its hash. */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.tokenHash = :hash AND t.usedAt IS NULL AND t.expiresAt > :now")
    Optional<PasswordResetToken> findValid(String hash, Instant now);

    /** Count recent requests for a given account (rate limiting). */
    @Query("SELECT COUNT(t) FROM PasswordResetToken t WHERE t.accountId = :accountId AND t.createdAt > :since")
    long countRecentByAccount(Integer accountId, Instant since);

    /** Count recent requests from an IP (rate limiting). */
    @Query("SELECT COUNT(t) FROM PasswordResetToken t WHERE t.requestIp = :ip AND t.createdAt > :since")
    long countRecentByIp(String ip, Instant since);

    /** Invalidate all unused tokens for an account after a successful reset. */
    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken t SET t.usedAt = :now WHERE t.accountId = :accountId AND t.usedAt IS NULL")
    void invalidateAllForAccount(Integer accountId, Instant now);
}
