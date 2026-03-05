package poly.edu.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import poly.edu.service.AuditLogService;
import poly.edu.service.TokenService;
import poly.edu.service.TrustedDeviceService;

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

    private final TokenService         tokenService;
    private final TrustedDeviceService trustedDeviceService;
    private final AuditLogService      auditLogService;

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
}
