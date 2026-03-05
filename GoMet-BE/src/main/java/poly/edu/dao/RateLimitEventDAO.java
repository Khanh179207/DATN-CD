package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.RateLimitEvent;

import java.time.Instant;

public interface RateLimitEventDAO extends JpaRepository<RateLimitEvent, Long> {

    @Query("SELECT COUNT(r) FROM RateLimitEvent r " +
           "WHERE r.user.accountID = :userId AND r.actionType = :type AND r.createdAt >= :since")
    long countByUserAndTypeAfter(@Param("userId") Integer userId,
                                  @Param("type") String type,
                                  @Param("since") Instant since);

    @Query("SELECT COUNT(r) FROM RateLimitEvent r " +
           "WHERE r.ip = :ip AND r.actionType = :type AND r.createdAt >= :since")
    long countByIpAndTypeAfter(@Param("ip") String ip,
                               @Param("type") String type,
                               @Param("since") Instant since);

    /** Purge old events to keep the table lean (called by SecurityCleanupJob). */
    void deleteByCreatedAtBefore(Instant cutoff);
}
