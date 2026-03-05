package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.AccountRoleDAO;
import poly.edu.dao.PermissionDAO;
import poly.edu.dao.RoleDAO;
import poly.edu.dto.RoleDTO;
import poly.edu.entity.Account;
import poly.edu.entity.AccountRole;
import poly.edu.entity.Role;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RBAC — Role Based Access Control.
 *
 * Current caller's permissions are resolved from the security context
 * via account_roles → role_permissions. Results are NOT cached (DB is the source
 * of truth; permissioned changes need instant effect).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RbacService {

    private final PermissionDAO permissionDAO;
    private final RoleDAO       roleDAO;
    private final AccountRoleDAO accountRoleDAO;
    private final AccountDAO    accountDAO;

    // ── Current user helpers ─────────────────────────────────────────────────

    /** Returns the ID of the currently authenticated admin from the security context. */
    public Integer currentAdminId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Integer id) return id;
        throw new SecurityException("No authenticated admin found");
    }

    /** Returns all permission codes for the currently authenticated user. */
    public Set<String> currentPermissions() {
        return permissionDAO.findPermissionCodesForAccount(currentAdminId());
    }

    /**
     * Returns true if the current user has ALL of the given permissions.
     * Accounts with isAdmin=1 automatically pass all permission checks (they are
     * super-administrators regardless of their account_roles rows).
     */
    public boolean hasPermission(String... codes) {
        Integer adminId = currentAdminId();
        // isAdmin=1 accounts bypass RBAC — they have full system access
        Account account = accountDAO.findById(adminId).orElse(null);
        if (account != null && account.isAdminAccount()) return true;

        Set<String> perms = permissionDAO.findPermissionCodesForAccount(adminId);
        for (String code : codes) {
            if (!perms.contains(code)) return false;
        }
        return true;
    }

    /** Throws 403 if the current user lacks the required permission. */
    public void requirePermission(String... codes) {
        if (!hasPermission(codes)) {
            throw new AccessDeniedException("You don't have permission: " + Arrays.toString(codes));
        }
    }

    // ── Role management ──────────────────────────────────────────────────────

    public List<RoleDTO> getAllRoles() {
        return roleDAO.findAll().stream().map(r -> {
            Set<String> permCodes = r.getPermissions().stream()
                .map(p -> p.getCode())
                .collect(Collectors.toSet());
            return RoleDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .permissions(permCodes)
                .build();
        }).toList();
    }

    public List<RoleDTO> getRolesForAccount(Integer accountId) {
        List<AccountRole> accountRoles = accountRoleDAO.findByAccountId(accountId);
        List<Integer> roleIds = accountRoles.stream().map(AccountRole::getRoleId).toList();
        if (roleIds.isEmpty()) return List.of();
        return roleDAO.findAllWithPermissions(roleIds).stream()
            .map(r -> RoleDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .permissions(r.getPermissions().stream().map(p -> p.getCode()).collect(Collectors.toSet()))
                .build())
            .toList();
    }

    @Transactional
    public void assignRole(Integer targetAccountId, Integer roleId, Integer assignedBy) {
        requirePermission("ROLE_MANAGE");
        if (!accountDAO.existsById(targetAccountId))
            throw new IllegalArgumentException("Account not found: " + targetAccountId);
        if (!roleDAO.existsById(roleId))
            throw new IllegalArgumentException("Role not found: " + roleId);
        if (!accountRoleDAO.existsByAccountIdAndRoleId(targetAccountId, roleId)) {
            accountRoleDAO.save(AccountRole.builder()
                .accountId(targetAccountId)
                .roleId(roleId)
                .assignedBy(assignedBy)
                .build());
        }
    }

    @Transactional
    public void revokeRole(Integer targetAccountId, Integer roleId) {
        requirePermission("ROLE_MANAGE");
        accountRoleDAO.deleteByAccountIdAndRoleId(targetAccountId, roleId);
    }

    public Set<String> getPermissionsForAccount(Integer accountId) {
        return permissionDAO.findPermissionCodesForAccount(accountId);
    }

    /** Exception marker for RBAC failures (maps to 403 in global handler). */
    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String msg) { super(msg); }
    }
}
