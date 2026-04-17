const RECENT_NOTIFICATION_WINDOW_MS = 5000
const recentNotifications = new Map()

const cleanupRecentNotifications = () => {
  const now = Date.now()
  for (const [key, timestamp] of recentNotifications.entries()) {
    if (now - timestamp > RECENT_NOTIFICATION_WINDOW_MS) {
      recentNotifications.delete(key)
    }
  }
}

export const ensureBrowserNotificationPermission = async () => {
  if (typeof window === 'undefined' || !('Notification' in window)) {
    return 'unsupported'
  }

  if (Notification.permission === 'granted') {
    return 'granted'
  }

  if (Notification.permission === 'default') {
    try {
      return await Notification.requestPermission()
    } catch {
      return Notification.permission
    }
  }

  return Notification.permission
}

export const showBrowserNotification = async ({
  title,
  body,
  icon,
  tag,
  dedupeKey,
  onClick,
  requireInteraction = false
} = {}) => {
  if (!title) return null
  if (typeof window === 'undefined' || !('Notification' in window)) return null

  cleanupRecentNotifications()

  const key = dedupeKey || tag || `${title}:${body || ''}`
  const now = Date.now()

  if (key && recentNotifications.has(key) && now - recentNotifications.get(key) < RECENT_NOTIFICATION_WINDOW_MS) {
    return null
  }

  const permission = await ensureBrowserNotificationPermission()
  if (permission !== 'granted') {
    return null
  }

  if (key) {
    recentNotifications.set(key, now)
  }

  const notification = new Notification(title, {
    body: body || '',
    icon,
    tag: tag || key,
    renotify: false,
    requireInteraction
  })

  notification.onclick = () => {
    try {
      window.focus()
    } catch {}

    if (typeof onClick === 'function') {
      onClick()
    }

    notification.close()
  }

  return notification
}
