import api from './api'

export const getComments = (postID) =>
  api.get(`/api/comments/post/${postID}`).then(r => r.data)

export const addComment = (postID, accountID, content, cmtid = null) =>
  api.post('/api/comments', { postID, accountID, content, cmtid }).then(r => r.data)

export const deleteComment = (commentID) =>
  api.delete(`/api/comments/${commentID}`)

export const ratePost = (accountID, postID, rate) =>
  api.post('/api/ratings', { accountID, postID, rate }).then(r => r.data)

export const checkRating = (accountID, postID) =>
  api.get('/api/ratings/check', { params: { accountID, postID } }).then(r => r.data)

// ── Browsing History ──────────────────────────────────────────────────
/** Record a post view (creates or updates timestamp). */
export const recordHistory = (accountID, postID) =>
  api.post('/api/history', { accountID, postID }).then(r => r.data)

/** Get recent browsing history for a user (newest first). */
export const getHistory = (accountID, limit = 20) =>
  api.get(`/api/history/${accountID}`, { params: { limit } }).then(r => r.data)

/** Clear all browsing history for a user. */
export const clearHistory = (accountID) =>
  api.delete(`/api/history/${accountID}/clear`).then(r => r.data)
