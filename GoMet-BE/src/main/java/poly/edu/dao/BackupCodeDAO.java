package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.BackupCode;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BackupCodeDAO extends JpaRepository<BackupCode, Long> {

    /** Find all unused backup codes for a user. */
    @Query("SELECT c FROM BackupCode c WHERE c.userId = :userId AND c.usedAt IS NULL")
    List<BackupCode> findUnusedByUser(Integer userId);

    /** Find an unused backup code by its hash (constant-time lookup by DB). */
    @Query("SELECT c FROM BackupCode c WHERE c.userId = :userId AND c.codeHash = :codeHash AND c.usedAt IS NULL")
    Optional<BackupCode> findUnusedByUserAndHash(Integer userId, String codeHash);

    /** Count remaining unused backup codes. */
    @Query("SELECT COUNT(c) FROM BackupCode c WHERE c.userId = :userId AND c.usedAt IS NULL")
    int countUnusedByUser(Integer userId);

    /** Delete all backup codes for a user (when regenerating or disabling MFA). */
    @Modifying
    @Transactional
    @Query("DELETE FROM BackupCode c WHERE c.userId = :userId")
    void deleteAllForUser(Integer userId);

    /** Cleanup old used codes (retention). */
    @Modifying
    @Transactional
    @Query("DELETE FROM BackupCode c WHERE c.usedAt IS NOT NULL AND c.usedAt < :before")
    void deleteUsedBefore(Instant before);
}
