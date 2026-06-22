import api from './api'

// ── User notifications ────────────────────────────────────────────────────

export const resolveNotificationLink = (notification = {}) => {
  const type = String(notification.type || '').toUpperCase()

  if (type === 'TICKET_UPDATE') {
    return null
  }

  const rawLink = notification.link ? String(notification.link).trim() : ''
  if (!rawLink) {
    return null
  }

  // Giữ nguyên link admin nhập; nếu dữ liệu cũ lỡ lưu kèm origin local thì bóc origin ra.
  if (rawLink.startsWith('http://localhost:5173') || rawLink.startsWith('https://localhost:5173')) {
    const withoutOrigin = rawLink.replace(/^https?:\/\/localhost:5173/i, '')
    return withoutOrigin || '/'
  }

  if (typeof window !== 'undefined' && window.location?.origin && rawLink.startsWith(window.location.origin)) {
    const withoutCurrentOrigin = rawLink.slice(window.location.origin.length)
    return withoutCurrentOrigin || '/'
  }

  return rawLink
}

export const getNotificationId = (notification = {}) =>
  notification.notificationID ?? notification.notificationId ?? notification.id ?? null

/** Get all notifications for a user (newest first). */
export const getNotifications = (accountID) =>
  api.get(`/api/notifications/${accountID}`).then(r => r.data)

/** Mark a single notification as read. */
export const markNotificationRead = (notificationID) =>
  api.put(`/api/notifications/${notificationID}/read`).then(r => r.data)

/** Mark ALL notifications as read for a user. */
export const markAllNotificationsRead = (accountID) =>
  api.put(`/api/notifications/${accountID}/read-all`).then(r => r.data)

/** Delete all read notifications for a user. */
export const deleteReadNotifications = (accountID) =>
  api.delete(`/api/notifications/${accountID}/read`).then(r => r.data)

/** Delete a specific notification. */
export const deleteNotification = (notificationID) =>
  api.delete(`/api/notifications/${notificationID}`).then(r => r.data)

// ── Admin notification management ─────────────────────────────────────────

export const getAdminNotifications = () =>
  api.get('/api/admin/notifications').then(r => r.data)

export const getAdminNotificationDetail = (notificationID) =>
  api.get(`/api/admin/notifications/${notificationID}`).then(r => r.data)

export const deleteAdminNotification = (notificationID) =>
  api.delete(`/api/admin/notifications/${notificationID}`).then(r => r.data)

export const sendAdminNotificationToAll = (payload) =>
  api.post('/api/admin/notifications/all', payload).then(r => r.data)

export const sendAdminNotificationToUser = (accountID, payload) =>
  api.post(`/api/admin/notifications/user/${accountID}`, payload).then(r => r.data)
