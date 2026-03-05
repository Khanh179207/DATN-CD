package poly.edu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import poly.edu.dao.AuditLogDAO;
import poly.edu.dto.AuditLogDTO;
import poly.edu.entity.AuditLog;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Append-only audit log writer.
 * All public methods are @Async to avoid blocking the request thread.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogDAO   auditLogDAO;
    // ObjectMapper is thread-safe; instantiate directly to avoid Spring Boot 4 bean lookup issues
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    // ─── Event type constants ─────────────────────────────────────────────────
    public static final String LOGIN_SUCCESS              = "LOGIN_SUCCESS";
    public static final String LOGIN_FAILURE              = "LOGIN_FAILURE";
    public static final String LOGIN_SUSPICIOUS           = "LOGIN_SUSPICIOUS";
    public static final String LOGIN_BLOCKED_LOCKED       = "LOGIN_BLOCKED_LOCKED";
    public static final String LOGIN_BLOCKED_RATE_LIMIT   = "LOGIN_BLOCKED_RATE_LIMIT";
    public static final String DEVICE_TRUSTED             = "DEVICE_TRUSTED";
    public static final String DEVICE_REVOKED             = "DEVICE_REVOKED";
    public static final String DEVICE_VERIFICATION_SENT   = "DEVICE_VERIFICATION_SENT";
    public static final String PASSWORD_CHANGE            = "PASSWORD_CHANGE";
    public static final String PASSWORD_RESET_REQUEST     = "PASSWORD_RESET_REQUEST";
    public static final String PASSWORD_RESET_SUCCESS     = "PASSWORD_RESET_SUCCESS";
    public static final String MFA_ENABLED                = "MFA_ENABLED";
    public static final String MFA_DISABLED               = "MFA_DISABLED";
    public static final String MFA_CHALLENGE_SUCCESS      = "MFA_CHALLENGE_SUCCESS";
    public static final String MFA_CHALLENGE_FAILURE      = "MFA_CHALLENGE_FAILURE";
    public static final String MFA_BACKUP_CODE_USED       = "MFA_BACKUP_CODE_USED";
    public static final String SESSION_REVOKE             = "SESSION_REVOKE";
    public static final String SESSION_REVOKE_ALL         = "SESSION_REVOKE_ALL";
    public static final String ACCOUNT_LOCKED             = "ACCOUNT_LOCKED";
    public static final String ACCOUNT_UNLOCKED           = "ACCOUNT_UNLOCKED";
    public static final String REFRESH_TOKEN_REUSE        = "REFRESH_TOKEN_REUSE_DETECTED";
    public static final String THIS_WASNT_ME              = "THIS_WASNT_ME";
    public static final String DEVICE_REVOKE_ALL          = "DEVICE_REVOKE_ALL";
    public static final String MAGIC_LINK_VERIFIED        = "MAGIC_LINK_VERIFIED";

    // ─── Post moderation events ───────────────────────────────────────────────
    public static final String POST_APPROVED              = "POST_APPROVED";
    public static final String POST_REJECTED              = "POST_REJECTED";
    public static final String POST_HIDDEN                = "POST_HIDDEN";
    public static final String POST_UNHIDDEN              = "POST_UNHIDDEN";
    public static final String POST_FLAGGED_SPAM          = "POST_FLAGGED_SPAM";
    public static final String POST_CREATED_PENDING       = "POST_CREATED_PENDING";
    public static final String POST_SPAM_BLOCKED          = "POST_SPAM_BLOCKED";
    public static final String POST_RATE_LIMITED          = "POST_RATE_LIMITED";

    // ─── Write API ────────────────────────────────────────────────────────────

    @Async
    public void log(Integer userId, String eventType, String ip, String userAgent,
                    Map<String, Object> meta) {
        try {
            String metaJson = meta != null ? objectMapper.writeValueAsString(meta) : null;
            AuditLog entry = AuditLog.builder()
                    .userId(userId)
                    .eventType(eventType)
                    .eventMetaJson(metaJson)
                    .ip(ip)
                    .userAgent(userAgent)
                    .createdAt(Instant.now())
                    .build();
            auditLogDAO.save(entry);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit log meta for event={}: {}", eventType, e.getMessage());
        }
    }

    @Async
    public void log(Integer userId, String eventType, HttpServletRequest request,
                    Map<String, Object> meta) {
        log(userId, eventType, extractIp(request), request.getHeader("User-Agent"), meta);
    }

    // ─── Read API ─────────────────────────────────────────────────────────────

    public List<AuditLogDTO> getLogsForUser(Integer userId, int page, int size) {
        return auditLogDAO
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private AuditLogDTO toDto(AuditLog l) {
        return AuditLogDTO.builder()
                .id(l.getId())
                .eventType(l.getEventType())
                .eventMetaJson(l.getEventMetaJson())
                .ip(l.getIp())
                .createdAt(l.getCreatedAt())
                .build();
    }

    public static String extractIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        String realIp = req.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) return realIp.trim();
        return req.getRemoteAddr();
    }
}
