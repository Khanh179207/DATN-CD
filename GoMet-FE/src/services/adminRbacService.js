/**
 * adminRbacService.js
 * API client for Admin RBAC endpoints + local permission-check helpers.
 */
import api from '@/services/api'

const BASE = '/api/admin/rbac'

// ─── API CALLS ────────────────────────────────────────────────────────────────

/** Get all roles (with their permission codes). */
export function getAllRoles () {
  return api.get(`${BASE}/roles`)
}

/** Get roles assigned to a specific account. */
export function getUserRoles (accountId) {
  return api.get(`${BASE}/users/${accountId}/roles`)
}

/** Assign a role to an account. Requires ROLE_MANAGE permission. */
export function assignRole (accountId, roleId) {
  return api.post(`${BASE}/users/${accountId}/roles/${roleId}`)
}

/** Revoke a role from an account. Requires ROLE_MANAGE permission. */
export function revokeRole (accountId, roleId) {
  return api.delete(`${BASE}/users/${accountId}/roles/${roleId}`)
}

/** Fetch the current admin's permission codes from the server. */
export function fetchMyPermissions () {
  return api.get(`${BASE}/my-permissions`)
}

// ─── LOCAL HELPERS ────────────────────────────────────────────────────────────

/** Well-known permission codes (mirrors Permission.java constants). */
export const PERM = {
  USER_READ:           'USER_READ',
  USER_WRITE:          'USER_WRITE',
  USER_BAN:            'USER_BAN',
  USER_BAN_PERMANENT:  'USER_BAN_PERMANENT',
  POST_MODERATE:       'POST_MODERATE',
  REPORT_HANDLE:       'REPORT_HANDLE',
  EMAIL_SEND:          'EMAIL_SEND',
  AUDIT_READ:          'AUDIT_READ',
  ROLE_MANAGE:         'ROLE_MANAGE',
  SETTINGS_WRITE:      'SETTINGS_WRITE',
  STATS_VIEW:          'STATS_VIEW',
}

/**
 * Check whether a permissions array contains ALL requested codes.
 * @param {string[]} perms  — array from fetchMyPermissions()
 * @param {...string} codes — one or more permission codes to check
 */
export function hasPermission (perms, ...codes) {
  if (!Array.isArray(perms)) return false
  return codes.every(code => perms.includes(code))
}
