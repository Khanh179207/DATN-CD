import { reactive } from 'vue'

let _id = 0

const state = reactive({ toasts: [] })

const ICONS = {
  success: '✓',
  error:   '✕',
  warn:    '⚠',
  info:    'ℹ',
}

/**
 * Add a toast. Returns the id so callers can dismiss manually.
 * @param {'success'|'error'|'warn'|'info'} type
 * @param {string} message
 * @param {number} [duration=5000]  ms until auto-dismiss (0 = persistent)
 */
function push(type, message, duration = 5000) {
  const id = ++_id
  state.toasts.push({ id, type, message, icon: ICONS[type] ?? 'ℹ', visible: true })
  if (duration > 0) {
    setTimeout(() => dismiss(id), duration)
  }
  return id
}

function dismiss(id) {
  const t = state.toasts.find(t => t.id === id)
  if (t) t.visible = false
  // remove from DOM after exit animation
  setTimeout(() => {
    const idx = state.toasts.findIndex(t => t.id === id)
    if (idx !== -1) state.toasts.splice(idx, 1)
  }, 400)
}

export function useToast() {
  return {
    toasts: state.toasts,
    success: (msg, dur)  => push('success', msg, dur),
    error:   (msg, dur)  => push('error',   msg, dur),
    warn:    (msg, dur)  => push('warn',     msg, dur),
    info:    (msg, dur)  => push('info',     msg, dur),
    dismiss,
  }
}

/** Singleton helper — import and call anywhere without setup() */
export const toast = {
  success: (msg, dur) => push('success', msg, dur),
  error:   (msg, dur) => push('error',   msg, dur),
  warn:    (msg, dur) => push('warn',     msg, dur),
  info:    (msg, dur) => push('info',     msg, dur),
  dismiss,
}
