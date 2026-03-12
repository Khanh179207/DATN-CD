package poly.edu.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.AdminAuditLog;

import java.time.Instant;

public interface AdminAuditLogDAO extends JpaRepository<AdminAuditLog, Long> {

    Page<AdminAuditLog> findByActorIdOrderByCreatedAtDesc(Integer actorId, Pageable pageable);

    Page<AdminAuditLog> findByActionTypeOrderByCreatedAtDesc(String actionType, Pageable pageable);

        @Query("SELECT l FROM AdminAuditLog l " +
                     "WHERE (:actorId IS NULL OR l.actorId = :actorId) " +
                     "AND (:action IS NULL OR l.actionType = :action) " +
                     "AND (:from IS NULL OR l.createdAt >= :from) " +
                     "AND (:to IS NULL OR l.createdAt <= :to)")
    Page<AdminAuditLog> search(
        @Param("actorId") Integer actorId,
        @Param("action")  String  action,
        @Param("from")    Instant from,
        @Param("to")      Instant to,
        Pageable pageable
    );
}
