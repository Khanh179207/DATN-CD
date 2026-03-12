package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.MagicLinkToken;

import java.time.Instant;
import java.util.Optional;

public interface MagicLinkTokenDAO extends JpaRepository<MagicLinkToken, Long> {

    @Query("SELECT t FROM MagicLinkToken t WHERE t.tokenHash = :hash AND t.usedAt IS NULL AND t.expiresAt > :now")
    Optional<MagicLinkToken> findValid(String hash, Instant now);

    /** Count recent magic links for a user (rate limit). */
    @Query("SELECT COUNT(t) FROM MagicLinkToken t WHERE t.userId = :userId AND t.purpose = :purpose AND t.createdAt > :since")
    long countRecentByUserAndPurpose(Integer userId, String purpose, Instant since);

    /** Invalidate all unused tokens for a user (after suspicious event resolution). */
    @Modifying
    @Transactional
    @Query("UPDATE MagicLinkToken t SET t.usedAt = :now WHERE t.userId = :userId AND t.usedAt IS NULL")
    void invalidateAllForUser(Integer userId, Instant now);

    /** Cleanup expired tokens. */
    @Modifying
    @Transactional
    @Query("DELETE FROM MagicLinkToken t WHERE t.expiresAt < :before")
    void deleteExpiredBefore(Instant before);
}
