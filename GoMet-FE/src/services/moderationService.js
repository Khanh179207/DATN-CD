import api from './api'

const BASE = '/api/admin/moderation'

/**
 * Fetch the moderation queue with optional filtering.
 * @param {object} params  { status, keyword, page, size }
 */
export const getModerationQueue = (params = {}) =>
  api.get(`${BASE}/posts`, { params })

/**
 * Approve a post.
 * @param {number} id
 * @param {string} [reason]
 */
export const approvePost = (id, reason = '') =>
  api.post(`${BASE}/posts/${id}/approve`, { reason })

/**
 * Reject a post.
 * @param {number} id
 * @param {string} code   Rejection code (SPAM / INAPPROPRIATE / OFF_TOPIC / DUPLICATE / OTHER)
 * @param {string} reason Human-readable reason shown to author
 */
export const rejectPost = (id, code, reason) =>
  api.post(`${BASE}/posts/${id}/reject`, { code, reason })

/**
 * Hide an approved post (soft moderator hide).
 */
export const hidePost = (id, reason = '') =>
  api.post(`${BASE}/posts/${id}/hide`, { reason })

/**
 * Unhide a hidden post (restore to APPROVED).
 */
export const unhidePost = (id, reason = '') =>
  api.post(`${BASE}/posts/${id}/unhide`, { reason })

/**
 * Flag a post as spam (sets status HIDDEN, spamScore=100).
 */
export const flagSpam = (id, reason = '') =>
  api.post(`${BASE}/posts/${id}/flag-spam`, { reason })

/**
 * Fetch the action timeline for a specific post.
 * @returns {Promise<ModerationActionDTO[]>}
 */
export const getPostActions = (postId) =>
  api.get(`${BASE}/actions`, { params: { postId } })
