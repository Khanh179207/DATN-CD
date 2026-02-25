import api from './api'

// ── User notifications ────────────────────────────────────────────────────

/** Get all notifications for a user (newest first). */
export const getNotifications = (accountID) =>
  api.get(`/api/notifications/${accountID}`).then(r => r.data)

/** Mark a single notification as read. */
export const markNotificationRead = (notificationID) =>
  api.put(`/api/notifications/${notificationID}/read`).then(r => r.data)

/** Mark ALL notifications as read for a user. */
export const markAllNotificationsRead = (accountID) =>
  api.put(`/api/notifications/${accountID}/read-all`).then(r => r.data)

/** Delete a specific notification. */
export const deleteNotification = (notificationID) =>
  api.delete(`/api/notifications/${notificationID}`).then(r => r.data)
