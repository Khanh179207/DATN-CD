package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminAccountDTO;
import poly.edu.entity.AdminAuditLog;
import poly.edu.service.AccountService;
import poly.edu.service.AdminAuditLogService;
import poly.edu.service.TokenService;
import poly.edu.service.TrustedDeviceService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
@CrossOrigin
public class AdminAccountController {

    private final AccountService accountService;
    private final TokenService tokenService;
    private final TrustedDeviceService trustedDeviceService;
    private final AdminAuditLogService adminAuditLogService;

    @GetMapping
    public List<AdminAccountDTO> getAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public AdminAccountDTO getOne(@PathVariable Integer id) {
        return accountService.findById(id);
    }

    @PostMapping
    public AdminAccountDTO create(@RequestBody AdminAccountDTO dto) {
        return accountService.save(dto);
    }

    @PutMapping("/{id}")
    public AdminAccountDTO update(@PathVariable Integer id,
                                  @RequestBody AdminAccountDTO dto) {
        dto.setAccountID(id);
        return accountService.save(dto);
    }

    /** Ban an account (set isActive = 0) */
    @PatchMapping("/{id}/ban")
    public ResponseEntity<?> ban(@PathVariable Integer id) {
        accountService.ban(id);
        return ResponseEntity.ok(Map.of("message", "Account banned successfully"));
    }

    /** Unban an account (set isActive = 1) */
    @PatchMapping("/{id}/unban")
    public ResponseEntity<?> unban(@PathVariable Integer id) {
        accountService.unban(id);
        return ResponseEntity.ok(Map.of("message", "Account unbanned successfully"));
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable Integer id) {
        accountService.delete(id);
    }

    @DeleteMapping("/hard/{id}")
    public void hardDelete(@PathVariable Integer id) {
        accountService.hardDelete(id);
    }

    /**
     * Force logout all sessions for a target user.
     * Optional body: { "revokeDevices": true|false } (default: true)
     */
    @PatchMapping("/{id}/force-logout")
    public ResponseEntity<?> forceLogout(@PathVariable Integer id,
                                         @RequestBody(required = false) Map<String, Object> body,
                                         HttpServletRequest request) {
        Integer actorId = resolveAccountId();
        if (actorId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated."));
        }

        if (actorId.equals(id)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không thể force logout chính tài khoản admin hiện tại."));
        }

        boolean revokeDevices = true;
        if (body != null && body.containsKey("revokeDevices")) {
            Object flag = body.get("revokeDevices");
            if (flag instanceof Boolean b) {
                revokeDevices = b;
            }
        }

        tokenService.revokeAllSessions(id);
        if (revokeDevices) {
            trustedDeviceService.revokeAllDevices(id);
        }

        adminAuditLogService.log(
            actorId,
            AdminAuditLog.USER_FORCE_LOGOUT,
            AdminAuditLog.TARGET_USER,
            String.valueOf(id),
            Map.of("revokeDevices", revokeDevices),
            request
        );

        return ResponseEntity.ok(Map.of(
            "message", revokeDevices
                ? "Đã force logout toàn bộ phiên và thu hồi trusted devices của user."
                : "Đã force logout toàn bộ phiên của user.",
            "targetUserId", id,
            "revokeDevices", revokeDevices
        ));
    }

    private Integer resolveAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        return principal instanceof Integer ? (Integer) principal : null;
    }
}
