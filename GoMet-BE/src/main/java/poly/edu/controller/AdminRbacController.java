package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.RoleDTO;
import poly.edu.service.RbacService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Admin RBAC endpoints: roles, permissions, role assignment.
 */
@RestController
@RequestMapping("/api/admin/rbac")
@RequiredArgsConstructor
@CrossOrigin
public class AdminRbacController {

    private final RbacService rbac;

    /**
     * GET /api/admin/rbac/roles — list all roles with their permissions.
     */
    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> listRoles() {
        rbac.requirePermission("ROLE_MANAGE");
        return ResponseEntity.ok(rbac.getAllRoles());
    }

    /**
     * GET /api/admin/rbac/users/{accountId}/roles
     */
    @GetMapping("/users/{accountId}/roles")
    public ResponseEntity<List<RoleDTO>> getUserRoles(@PathVariable Integer accountId) {
        rbac.requirePermission("ROLE_MANAGE");
        return ResponseEntity.ok(rbac.getRolesForAccount(accountId));
    }

    /**
     * POST /api/admin/rbac/users/{accountId}/roles/{roleId}
     */
    @PostMapping("/users/{accountId}/roles/{roleId}")
    public ResponseEntity<?> assignRole(
            @PathVariable Integer accountId,
            @PathVariable Integer roleId,
            @AuthenticationPrincipal Integer adminId) {

        rbac.requirePermission("ROLE_MANAGE");
        rbac.assignRole(accountId, roleId, adminId);
        return ResponseEntity.ok(Map.of("message", "Role assigned"));
    }

    /**
     * DELETE /api/admin/rbac/users/{accountId}/roles/{roleId}
     */
    @DeleteMapping("/users/{accountId}/roles/{roleId}")
    public ResponseEntity<?> revokeRole(
            @PathVariable Integer accountId,
            @PathVariable Integer roleId) {

        rbac.requirePermission("ROLE_MANAGE");
        rbac.revokeRole(accountId, roleId);
        return ResponseEntity.ok(Map.of("message", "Role revoked"));
    }

    /**
     * GET /api/admin/rbac/my-permissions — returns permissions for the current caller.
     */
    @GetMapping("/my-permissions")
    public ResponseEntity<Set<String>> myPermissions() {
        return ResponseEntity.ok(rbac.currentPermissions());
    }
}
