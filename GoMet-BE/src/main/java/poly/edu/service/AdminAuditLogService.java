package poly.edu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.AdminAuditLogDAO;
import poly.edu.dto.AdminAuditLogDTO;
import poly.edu.entity.AdminAuditLog;

import java.time.Instant;
import java.util.Map;

/**
 * Append-only admin audit trail.
 * All write methods are @Async to avoid blocking request threads.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuditLogService {

    private final AdminAuditLogDAO           auditLogDAO;
    private final AccountDAO                 accountDAO;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ── Write ────────────────────────────────────────────────────────────────

    @Async
    public void log(Integer actorId,
                    String  actionType,
                    String  targetType,
                    String  targetId,
                    Map<String, Object> meta,
                    HttpServletRequest  request) {
        try {
            String metaJson = meta != null ? MAPPER.writeValueAsString(meta) : null;
            String ip       = resolveIp(request);
            String ua       = request != null ? request.getHeader("User-Agent") : null;

            AdminAuditLog entry = AdminAuditLog.builder()
                .actorId(actorId)
                .actionType(actionType)
                .targetType(targetType)
                .targetId(targetId)
                .metaJson(metaJson)
                .ip(ip)
                .userAgent(ua)
                .createdAt(Instant.now())
                .build();

            auditLogDAO.save(entry);
        } catch (Exception e) {
            log.warn("Failed to write admin audit log: {}", e.getMessage());
        }
    }

    @Async
    public void log(Integer actorId, String actionType, String targetType, String targetId,
                    Map<String, Object> meta) {
        log(actorId, actionType, targetType, targetId, meta, null);
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    public org.springframework.data.domain.Page<AdminAuditLogDTO> search(
            Integer actorId, String action, Instant from, Instant to,
            org.springframework.data.domain.Pageable pageable) {

        return auditLogDAO.search(actorId, action, from, to, pageable)
            .map(log -> {
                AdminAuditLogDTO dto = AdminAuditLogDTO.fromEntity(log);
                try {
                    accountDAO.findById(log.getActorId()).ifPresent(a -> dto.setActorName(a.getUsername()));
                } catch (Exception ignored) {}
                return dto;
            });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String resolveIp(HttpServletRequest req) {
        if (req == null) return null;
        String clientIp = req.getHeader("X-Client-IP");
        if (clientIp != null && !clientIp.isBlank()) return clientIp.trim();
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
