package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.EmailJobRecipient;

import java.time.Instant;
import java.util.List;

public interface EmailJobRecipientDAO extends JpaRepository<EmailJobRecipient, Long> {

    List<EmailJobRecipient> findByJobIdAndStatus(Long jobId, String status);

    List<EmailJobRecipient> findByJobId(Long jobId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE EmailJobRecipient r
        SET r.status = :status, r.sentAt = :sentAt, r.failReason = :reason
        WHERE r.id = :id
        """)
    void updateRecipientStatus(
        @Param("id")     Long    id,
        @Param("status") String  status,
        @Param("sentAt") Instant sentAt,
        @Param("reason") String  reason
    );

    /** Count for admin rate-limit events within window (using admin_email_rate_events). */
    @Query(value = """
        SELECT COUNT(*) FROM admin_email_rate_events
        WHERE admin_id = :adminId AND created_at >= :since
        """, nativeQuery = true)
    long countRateEvents(@Param("adminId") Integer adminId, @Param("since") Instant since);

    /** Insert a rate event */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO admin_email_rate_events (admin_id) VALUES (:adminId)", nativeQuery = true)
    void insertRateEvent(@Param("adminId") Integer adminId);
}
