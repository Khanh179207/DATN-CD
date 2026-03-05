/**
 * adminEmailService.js
 * API client for Admin Email Job CRUD — wraps /api/admin/email/jobs endpoints.
 */
import api from '@/services/api'

const BASE = '/api/admin/email/jobs'

function buildAuthConfig () {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  const accessToken = localStorage.getItem('accessToken') || user?.accessToken
  const legacyToken = user?.token
  const bearer = accessToken || legacyToken
  return bearer ? { headers: { Authorization: `Bearer ${bearer}` } } : {}
}

/**
 * Create a draft email job (does NOT queue it yet).
 * @param {{ subject: string, bodyHtml: string, bodyText?: string, recipientUserIds?: number[], recipientEmails?: string[] }} req
 */
export function createDraft (req) {
  return api.post(BASE, req, buildAuthConfig())
}

/**
 * Create AND immediately queue an email job for sending.
 * @param {{ subject: string, bodyHtml: string, bodyText?: string, recipientUserIds?: number[], recipientEmails?: string[] }} req
 */
export function createAndSend (req) {
  return api.post(`${BASE}/send`, req, buildAuthConfig())
}

/**
 * Queue an existing DRAFT job for sending.
 * @param {number} jobId
 */
export function queueJob (jobId) {
  return api.post(`${BASE}/${jobId}/queue`, null, buildAuthConfig())
}

/**
 * Cancel a QUEUED or DRAFT job.
 * @param {number} jobId
 */
export function cancelJob (jobId) {
  return api.post(`${BASE}/${jobId}/cancel`, null, buildAuthConfig())
}

/**
 * List email jobs for the current admin (or all if SUPER_ADMIN using /all).
 * @param {{ status?: string, page?: number, size?: number, all?: boolean }} params
 */
export function listJobs ({ status, page = 0, size = 20, all = false } = {}) {
  const url = all ? `${BASE}/all` : BASE
  const query = new URLSearchParams()
  if (status) query.set('status', status)
  query.set('page', page)
  query.set('size', size)
  return api.get(`${url}?${query.toString()}`, buildAuthConfig())
}

/**
 * Get full detail of a single email job including recipient status list.
 * @param {number} jobId
 */
export function getJobDetail (jobId) {
  return api.get(`${BASE}/${jobId}`, buildAuthConfig())
}
