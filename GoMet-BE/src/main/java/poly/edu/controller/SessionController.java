package poly.edu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import poly.edu.entity.AuditLog;
import poly.edu.service.AuditLogService;
import poly.edu.service.SecurityRiskService;
import poly.edu.service.SecurityEventStreamService;
import poly.edu.service.TokenService;
import poly.edu.service.TrustedDeviceService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Manages active sessions and trusted devices for the authenticated user.
 *
 * All endpoints require a valid JWT (enforced by Spring Security).
 *
 * Sessions
 *   GET    /api/sessions                — list active refresh-token sessions
 *   DELETE /api/sessions/{id}           — revoke one session by DB id
 *   DELETE /api/sessions                — revoke ALL sessions (full sign-out everywhere)
 *
 * Trusted Devices
 *   GET    /api/sessions/devices        — list trusted devices
 *   DELETE /api/sessions/devices/{id}   — revoke trust for one device
 *   DELETE /api/sessions/devices        — revoke ALL trusted devices
 */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    private final TokenService         tokenService;
    private final TrustedDeviceService trustedDeviceService;
    private final AuditLogService      auditLogService;
    private final SecurityEventStreamService securityEventStreamService;
    private final SecurityRiskService  securityRiskService;

    // ─── SESSIONS ────────────────────────────────────────────────────────────

    /** List all active (non-revoked, non-expired) refresh-token sessions.
     *  Optionally pass the current refresh token via {@code X-Refresh-Token} header
     *  so the response can flag which entry is the caller's own session. */
    @GetMapping
    public ResponseEntity<?> listSessions(
            @RequestHeader(value = "X-Refresh-Token", required = false) String currentRefreshToken) {
        Integer accountId = resolveAccountId();
        if (accountId == null)
            return unauthorized();
        return ResponseEntity.ok(tokenService.listActiveSessions(accountId, currentRefreshToken));
    }

    /** Revoke a specific session. The caller must own the session (checked inside service). */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> revokeSession(@PathVariable long id, HttpServletRequest httpReq) {
        Integer accountId = resolveAccountId();
        if (accountId == null)
            return unauthorized();

        boolean revoked = tokenService.revokeSession(id, accountId);
        if (!revoked)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(err("Session not found or already revoked."));

        auditLogService.log(accountId, AuditLogService.SESSION_REVOKE, httpReq,
                Map.of("sessionId", String.valueOf(id)));
        return ResponseEntity.ok(Map.of("message", "Session revoked."));
    }

    /** Revoke ALL sessions for the current account (sign-out everywhere). */
    @DeleteMapping
    public ResponseEntity<?> revokeAllSessions(HttpServletRequest httpReq) {
        Integer accountId = resolveAccountId();
        if (accountId == null)
            return unauthorized();

        tokenService.revokeAllSessions(accountId);
        auditLogService.log(accountId, AuditLogService.SESSION_REVOKE_ALL, httpReq, Map.of());
        return ResponseEntity.ok(Map.of("message", "All sessions revoked."));
    }

    /** Recent user-facing security events for the profile security timeline. */
    @GetMapping("/events")
    public ResponseEntity<?> listSecurityEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Integer accountId = resolveAccountId();
        if (accountId == null)
            return unauthorized();

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 30);
        List<?> events = auditLogService.getSecurityTimelineForUser(accountId, safePage, safeSize);
        return ResponseEntity.ok(events);
    }

    @GetMapping(path = "/events/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamSecurityEvents() {
        Integer accountId = resolveAccountId();
        if (accountId == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated.");
        }
        return securityEventStreamService.subscribe(accountId);
    }

    @GetMapping("/risk")
    public ResponseEntity<?> getCurrentRisk() {
        Integer accountId = resolveAccountId();
        if (accountId == null)
            return unauthorized();

        return securityRiskService.getCurrentRiskForUser(accountId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(err("Account not found.")));
    }

    @PostMapping("/events/{id}/feedback")
    public ResponseEntity<?> submitSecurityEventFeedback(
            @PathVariable long id,
            @RequestBody(required = false) Map<String, String> request,
            @RequestHeader(value = "X-Refresh-Token", required = false) String currentRefreshToken,
            HttpServletRequest httpReq) {
        Integer accountId = resolveAccountId();
        if (accountId == null)
            return unauthorized();

        AuditLog event = auditLogService.getSecurityTimelineEvent(accountId, id).orElse(null);
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err("Security event not found."));
        }

        String action = request != null && request.get("action") != null
                ? request.get("action").trim().toUpperCase(Locale.ROOT)
                : "";

        if ("THIS_IS_ME".equals(action)) {
            if (!AuditLogService.LOGIN_SUSPICIOUS.equals(event.getEventType())) {
                return ResponseEntity.badRequest().body(err("This action is only available for suspicious login events."));
            }

            String deviceId = parseMeta(event).getOrDefault("deviceId", "");
            if (deviceId.isBlank()) {
                return ResponseEntity.badRequest().body(err("This event does not contain a device fingerprint to trust."));
            }

            trustedDeviceService.trustDevice(accountId, deviceId);
            auditLogService.log(accountId, AuditLogService.DEVICE_TRUSTED, httpReq,
                    Map.of(
                            "deviceId", deviceId,
                            "source", "timeline_feedback",
                            "eventId", String.valueOf(id)
                    ));
            return ResponseEntity.ok(Map.of(
                    "message", "Device marked as trusted.",
                    "action", "THIS_IS_ME"
            ));
        }

        if ("THIS_WASNT_ME".equals(action)) {
            if (currentRefreshToken != null && !currentRefreshToken.isBlank()) {
                tokenService.revokeOtherSessions(accountId, currentRefreshToken);
            } else {
                tokenService.revokeAllSessions(accountId);
            }
            trustedDeviceService.revokeAllDevices(accountId);
            auditLogService.log(accountId, AuditLogService.THIS_WASNT_ME, httpReq,
                    Map.of(
                            "source", "timeline_feedback",
                            "eventId", String.valueOf(id),
                            "eventType", event.getEventType()
                    ));
            return ResponseEntity.ok(Map.of(
                    "message", "Other sessions were revoked and trusted devices were cleared.",
                    "action", "THIS_WASNT_ME",
                    "passwordResetRecommended", true
            ));
        }

        return ResponseEntity.badRequest().body(err("Unsupported security feedback action."));
    }

    // ─── TRUSTED DEVICES ─────────────────────────────────────────────────────

    /** List all trusted devices. */
    @GetMapping("/devices")
    public ResponseEntity<?> listDevices() {
        Integer accountId = resolveAccountId();
        if (accountId == null)
            return unauthorized();
        return ResponseEntity.ok(trustedDeviceService.listTrustedDevices(accountId));
    }

    /** Remove trust from a specific device (user must re-verify on next login from that device). */
    @DeleteMapping("/devices/{id}")
    public ResponseEntity<?> revokeDevice(@PathVariable long id, HttpServletRequest httpReq) {
        Integer accountId = resolveAccountId();
        if (accountId == null)
            return unauthorized();

        boolean revoked = trustedDeviceService.revokeDevice(id, accountId);
        if (!revoked)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(err("Device not found or already revoked."));

        auditLogService.log(accountId, AuditLogService.DEVICE_REVOKED, httpReq,
                Map.of("deviceDbId", String.valueOf(id)));
        return ResponseEntity.ok(Map.of("message", "Device trust revoked."));
    }

    /** Revoke trust for ALL devices. */
    @DeleteMapping("/devices")
    public ResponseEntity<?> revokeAllDevices(HttpServletRequest httpReq) {
        Integer accountId = resolveAccountId();
        if (accountId == null)
            return unauthorized();

        trustedDeviceService.revokeAllDevices(accountId);
        auditLogService.log(accountId, AuditLogService.DEVICE_REVOKE_ALL, httpReq, Map.of());
        return ResponseEntity.ok(Map.of("message", "All trusted devices revoked."));
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private Integer resolveAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        return principal instanceof Integer ? (Integer) principal : null;
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("Not authenticated."));
    }

    private Map<String, String> err(String message) {
        return Map.of("message", message);
    }

    private Map<String, String> parseMeta(AuditLog event) {
        if (event.getEventMetaJson() == null || event.getEventMetaJson().isBlank()) {
            return Map.of();
        }

        try {
            return OBJECT_MAPPER.readValue(event.getEventMetaJson(), new TypeReference<>() {});
        } catch (JsonProcessingException ignored) {
            return Map.of();
        }
    }
}
