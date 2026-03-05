package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.RefreshToken;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenDAO extends JpaRepository<RefreshToken, Long> {

    /** Find an active (non-revoked, non-replaced) token by its hash. */
    @Query("SELECT t FROM RefreshToken t WHERE t.tokenHash = :hash")
    Optional<RefreshToken> findByHash(String hash);

    /** List all valid (non-revoked) sessions for a user — used by "active sessions" UI. */
    @Query("SELECT t FROM RefreshToken t WHERE t.userId = :userId AND t.revokedAt IS NULL AND t.expiresAt > :now ORDER BY t.lastUsedAt DESC")
    List<RefreshToken> findActiveSessions(Integer userId, Instant now);

    /** Revoke a single token. */
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken t SET t.revokedAt = :now WHERE t.id = :id")
    void revokeById(Long id, Instant now);

    /** Revoke ALL tokens for a user (logout all / password change). */
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken t SET t.revokedAt = :now WHERE t.userId = :userId AND t.revokedAt IS NULL")
    void revokeAllForUser(Integer userId, Instant now);

    /** Revoke all tokens for a user issued before a certain time (password reset window). */
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken t SET t.revokedAt = :now WHERE t.userId = :userId AND t.createdAt < :before AND t.revokedAt IS NULL")
    void revokeAllCreatedBefore(Integer userId, Instant before, Instant now);

    /** Cleanup expired/revoked tokens older than retention window. */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :before OR (t.revokedAt IS NOT NULL AND t.revokedAt < :before)")
    void deleteExpiredBefore(Instant before);
}
