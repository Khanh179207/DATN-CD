import api from './api'

export const toggleCommentLike = async (commentID) => {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  const accountID = user?.accountID
  if (!accountID) throw new Error('User not logged in')
  
  const res = await api.post(`/api/comments/${commentID}/like`, { accountID })
  return res.data // { likeCount: N }
}

export const getComments = (postID) =>
  api.get(`/api/comments/post/${postID}`).then(r => r.data)

export const addComment = (postID, accountID, content, cmtid = null, imageUrls = []) =>
  api.post('/api/comments', { postID, accountID, content, cmtid, imageUrls }).then(r => r.data)

export const deleteComment = (commentID) =>
  api.delete(`/api/comments/${commentID}`)

export const ratePost = (accountID, postID, rate) =>
  api.post('/api/ratings', { accountID, postID, rate }).then(r => r.data)

export const submitReview = (accountID, postID, rate, comment, imageUrls = []) =>
  api.post('/api/ratings', { accountID, postID, rate, comment, imageUrls }).then(r => r.data)

export const getRatingSummary = (postID) =>
  api.get(`/api/ratings/post/${postID}/summary`).then(r => r.data)

export const getRatings = (postID, page = 0, size = 10, filters = {}) =>
  api.get(`/api/ratings/post/${postID}`, { params: { page, size, ...filters } }).then(r => r.data)

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
