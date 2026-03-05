package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import poly.edu.entity.LoginAttempt;

import java.time.Instant;

public interface LoginAttemptDAO extends JpaRepository<LoginAttempt, Long> {

    /** Count failed attempts for an account in a sliding time window. */
    @Query("SELECT COUNT(a) FROM LoginAttempt a WHERE a.userId = :userId AND a.success = false AND a.createdAt > :since")
    long countFailedByUser(Integer userId, Instant since);

    /** Count failed attempts for an identifier (email/username) in a window. */
    @Query("SELECT COUNT(a) FROM LoginAttempt a WHERE a.identifier = :identifier AND a.success = false AND a.createdAt > :since")
    long countFailedByIdentifier(String identifier, Instant since);

    /** Count all attempts (any type) from an IP in a window. */
    @Query("SELECT COUNT(a) FROM LoginAttempt a WHERE a.ip = :ip AND a.createdAt > :since")
    long countByIp(String ip, Instant since);

    /** Count failed attempts from an IP in a window. */
    @Query("SELECT COUNT(a) FROM LoginAttempt a WHERE a.ip = :ip AND a.success = false AND a.createdAt > :since")
    long countFailedByIp(String ip, Instant since);

    /** Delete old attempts to keep the table lean (called by scheduled cleanup). */
    @Query("DELETE FROM LoginAttempt a WHERE a.createdAt < :before")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteOlderThan(Instant before);
}
