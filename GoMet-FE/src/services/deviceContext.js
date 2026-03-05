    const DEVICE_ID_KEY = 'deviceId'

function fallbackId () {
  return `dev-${Date.now()}-${Math.random().toString(16).slice(2, 10)}`
}

export function getOrCreateDeviceId () {
  let deviceId = localStorage.getItem(DEVICE_ID_KEY)
  if (!deviceId) {
    deviceId = globalThis.crypto?.randomUUID?.() || fallbackId()
    localStorage.setItem(DEVICE_ID_KEY, deviceId)
  }
  return deviceId
}

export function getDeviceName () {
  const ua = navigator.userAgent || ''
  if (ua.includes('Windows')) return ua.includes('Chrome') ? 'Chrome on Windows' : 'Browser on Windows'
  if (ua.includes('Macintosh') || ua.includes('Mac OS X')) return ua.includes('Chrome') ? 'Chrome on Mac' : 'Browser on Mac'
  if (ua.includes('Android')) return 'Mobile (Android)'
  if (ua.includes('iPhone') || ua.includes('iPad')) return 'Mobile (iOS)'
  if (ua.includes('Linux')) return 'Browser on Linux'
  return 'Unknown device'
}

export function getDeviceContext () {
  return {
    deviceId: getOrCreateDeviceId(),
    deviceName: getDeviceName()
  }
}
