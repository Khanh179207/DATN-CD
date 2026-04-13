import api from './api'

// ── Admin System Alerts ────────────────────────────────────────────────────

/** Get all open/pending support tickets for admin */
export const getOpenTickets = () =>
    api.get('/api/admin/tickets/pending').then(r => r.data)

/** Get all pending posts for admin approval */
export const getPendingPosts = () =>
    api.get('/api/admin/posts/approved/0').then(r => r.data)

/** Mark ticket as resolved */
export const resolveTicket = (ticketId) =>
    api.put(`/api/admin/tickets/${ticketId}/status?status=2`).then(r => r.data)

/** Approve pending post */
export const approvePost = (postId) =>
    api.put(`/api/admin/posts/approve/${postId}`).then(r => r.data)

/** Reject pending post */
export const rejectPost = (postId, reason) =>
    api.put(`/api/admin/posts/${postId}/reject`, { reason }).then(r => r.data)