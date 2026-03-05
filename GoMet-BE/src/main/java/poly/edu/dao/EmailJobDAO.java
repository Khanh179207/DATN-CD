package poly.edu.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.EmailJob;

import java.time.Instant;
import java.util.List;

public interface EmailJobDAO extends JpaRepository<EmailJob, Long> {

    Page<EmailJob> findBySenderAdminIdOrderByCreatedAtDesc(Integer adminId, Pageable pageable);

    @Query("SELECT j FROM EmailJob j WHERE j.status = :status ORDER BY j.queuedAt ASC")
    List<EmailJob> findByStatus(@Param("status") String status);

    /**
     * Atomically claim one QUEUED job → SENDING.
     * Returns number of rows updated (0 or 1).
     */
    @Modifying
    @Transactional
    @Query("""
        UPDATE EmailJob j SET j.status = 'SENDING', j.updatedAt = :now
        WHERE j.id = :id AND j.status = 'QUEUED'
        """)
    int claimJob(@Param("id") Long id, @Param("now") Instant now);

    @Modifying
    @Transactional
    @Query("""
        UPDATE EmailJob j
        SET j.status        = :status,
            j.updatedAt     = :now,
            j.sentAt        = CASE WHEN :status = 'SENT' THEN :now ELSE j.sentAt END,
            j.sentCount     = :sent,
            j.failedCount   = :failed,
            j.failReason    = :reason
        WHERE j.id = :id
        """)
    void updateJobResult(
        @Param("id")     Long    id,
        @Param("status") String  status,
        @Param("sent")   int     sent,
        @Param("failed") int     failed,
        @Param("reason") String  reason,
        @Param("now")    Instant now
    );
}
