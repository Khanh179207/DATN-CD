package poly.edu.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.AuditLog;

import java.time.Instant;
import java.util.Collection;

public interface AuditLogDAO extends JpaRepository<AuditLog, Long> {

    /** Paginated user-scoped audit log view. */
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    /** Find logs by event type (admin view). */
    Page<AuditLog> findByEventTypeOrderByCreatedAtDesc(String eventType, Pageable pageable);

    /** User-scoped filtered security event timeline. */
    Page<AuditLog> findByUserIdAndEventTypeInOrderByCreatedAtDesc(Integer userId,
                                                                  Collection<String> eventTypes,
                                                                  Pageable pageable);

    /** Cleanup old audit logs (configured retention period). */
    @Modifying
    @Transactional
    @Query("DELETE FROM AuditLog l WHERE l.createdAt < :before")
    void deleteOlderThan(Instant before);
}
