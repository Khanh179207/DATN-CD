package poly.edu.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminAuditLogDTO;
import poly.edu.service.AdminAuditLogService;
import poly.edu.service.RbacService;

import java.time.Instant;

/**
 * Admin audit log viewer endpoints.
 * Requires AUDIT_READ permission.
 */
@RestController
@RequestMapping("/api/admin/audit")
@RequiredArgsConstructor
@CrossOrigin
public class AdminAuditController {

    private final AdminAuditLogService auditLogService;
    private final RbacService          rbac;

    /**
     * GET /api/admin/audit?actorId=&action=&from=&to=&page=&size=
     */
    @GetMapping
    public ResponseEntity<Page<AdminAuditLogDTO>> search(
            @RequestParam(required = false) Integer actorId,
            @RequestParam(required = false) String  action,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        rbac.requirePermission("AUDIT_READ");
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(auditLogService.search(actorId, action, from, to, pageable));
    }
}
