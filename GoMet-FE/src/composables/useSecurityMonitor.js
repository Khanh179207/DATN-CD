import { computed, onUnmounted, reactive, ref } from 'vue'
import * as authService from '@/services/authService'

export function parseSecurityEventMeta(eventMetaJson) {
  if (!eventMetaJson) return {}
  try {
    return JSON.parse(eventMetaJson)
  } catch {
    return {}
  }
}

export function useSecurityMonitor (options = {}) {
  const eventPageSize = options.eventPageSize ?? 12
  const onLiveEvent = typeof options.onLiveEvent === 'function' ? options.onLiveEvent : null
  const getCurrentRefreshToken = typeof options.getCurrentRefreshToken === 'function'
    ? options.getCurrentRefreshToken
    : () => localStorage.getItem('refreshToken') || null

  const sessions = ref([])
  const devices = ref([])
  const securityEvents = ref([])
  const securityRisk = ref(null)
  const securityFeedLoading = ref(false)
  const snapshotSyncing = ref(false)
  const snapshotUpdatedAt = ref('')
  const streamStatus = ref('idle')
  const newestLiveEventId = ref(null)
  const mfaStatus = reactive({ mfaEnabled: false, remainingBackupCodes: null })

  let streamAbortController = null
  let streamRetryTimer = null
  let liveEventFlashTimer = null

  const trustedDeviceIds = computed(() => new Set(devices.value.map(device => device.deviceId).filter(Boolean)))

  const suspiciousSessions = computed(() => sessions.value.filter(session => {
    if (session.current) return false
    const trusted = Boolean(session.deviceId && trustedDeviceIds.value.has(session.deviceId))
    const ipChanged = Boolean(securityRisk.value?.latestIp && session.ip && session.ip !== securityRisk.value.latestIp)
    return !trusted || ipChanged
  }))

  const revokableSessions = computed(() => sessions.value.filter(session => !session.current))

  function clearState() {
    sessions.value = []
    devices.value = []
    securityEvents.value = []
    securityRisk.value = null
    securityFeedLoading.value = false
    snapshotSyncing.value = false
    snapshotUpdatedAt.value = ''
    newestLiveEventId.value = null
    mfaStatus.mfaEnabled = false
    mfaStatus.remainingBackupCodes = null
  }

  async function fetchSessions() {
    sessions.value = await authService.getSessions(getCurrentRefreshToken())
    return sessions.value
  }

  async function fetchDevices() {
    devices.value = await authService.getTrustedDevices()
    return devices.value
  }

  async function fetchMfaStatus() {
    const status = await authService.getMfaStatus()
    mfaStatus.mfaEnabled = Boolean(status?.mfaEnabled)
    mfaStatus.remainingBackupCodes = typeof status?.remainingBackupCodes === 'number'
      ? status.remainingBackupCodes
      : null
    return status
  }

  async function fetchSecurityEvents() {
    securityFeedLoading.value = true
    try {
      securityEvents.value = await authService.getSecurityEvents(0, eventPageSize)
      return securityEvents.value
    } finally {
      securityFeedLoading.value = false
    }
  }

  async function fetchSecurityRisk() {
    securityRisk.value = await authService.getSecurityRisk()
    return securityRisk.value
  }

  async function refreshSecurityState() {
    snapshotSyncing.value = true
    try {
      await Promise.allSettled([
        fetchSessions(),
        fetchDevices(),
        fetchMfaStatus(),
        fetchSecurityEvents(),
        fetchSecurityRisk()
      ])
      snapshotUpdatedAt.value = new Date().toISOString()
    } finally {
      snapshotSyncing.value = false
    }
  }

  function upsertSecurityEvent(event) {
    if (!event?.id) return
    securityEvents.value = [event, ...securityEvents.value.filter(item => item.id !== event.id)].slice(0, eventPageSize)
  }

  function clearLiveEventFlash() {
    if (liveEventFlashTimer) {
      clearTimeout(liveEventFlashTimer)
      liveEventFlashTimer = null
    }
    newestLiveEventId.value = null
  }

  function flashEvent(eventId) {
    clearLiveEventFlash()
    newestLiveEventId.value = eventId
    liveEventFlashTimer = window.setTimeout(() => {
      newestLiveEventId.value = null
      liveEventFlashTimer = null
    }, 7000)
  }

  function applyStreamState(event, risk) {
    if (risk) {
      securityRisk.value = risk
    }

    const meta = parseSecurityEventMeta(event?.eventMetaJson)

    if (event?.eventType === 'SESSION_REVOKE' && meta.sessionId) {
      sessions.value = sessions.value.filter(session => String(session.id) !== String(meta.sessionId))
    }
    if (event?.eventType === 'SESSION_REVOKE_ALL') {
      sessions.value = sessions.value.filter(session => session.current)
    }
    if (event?.eventType === 'DEVICE_REVOKED' && meta.deviceDbId) {
      devices.value = devices.value.filter(device => String(device.id) !== String(meta.deviceDbId))
    }
    if (event?.eventType === 'DEVICE_REVOKE_ALL' || event?.eventType === 'THIS_WASNT_ME') {
      devices.value = []
    }
    if (event?.eventType === 'MFA_ENABLED') {
      mfaStatus.mfaEnabled = true
    }
    if (event?.eventType === 'MFA_DISABLED') {
      mfaStatus.mfaEnabled = false
    }

    if (event?.eventType === 'LOGIN_SUCCESS' || event?.eventType === 'LOGIN_SUSPICIOUS' || event?.eventType === 'THIS_WASNT_ME') {
      void fetchSessions()
    }
    if (event?.eventType === 'DEVICE_TRUSTED') {
      void fetchDevices()
    }
  }

  function scheduleReconnect() {
    if (streamRetryTimer) return
    streamStatus.value = 'reconnecting'
    streamRetryTimer = window.setTimeout(() => {
      streamRetryTimer = null
      startLiveStream()
    }, 5000)
  }

  function startLiveStream() {
    if (streamAbortController) return

    streamAbortController = new AbortController()
    streamStatus.value = 'reconnecting'

    authService.subscribeSecurityEventsStream({
      signal: streamAbortController.signal,
      onConnected: () => {
        streamStatus.value = 'live'
        snapshotUpdatedAt.value = new Date().toISOString()
      },
      onEvent: ({ event, data, risk }) => {
        if (event !== 'security-event' || !data?.id) {
          return
        }

        upsertSecurityEvent(data)
        applyStreamState(data, risk)
        flashEvent(data.id)
        snapshotUpdatedAt.value = new Date().toISOString()
        onLiveEvent?.(data)
      },
      onError: () => {
        streamAbortController = null
        scheduleReconnect()
      }
    })
  }

  function stopLiveStream() {
    if (streamAbortController) {
      streamAbortController.abort()
      streamAbortController = null
    }
    if (streamRetryTimer) {
      clearTimeout(streamRetryTimer)
      streamRetryTimer = null
    }
    streamStatus.value = 'idle'
    clearLiveEventFlash()
  }

  async function revokeSession(sessionId) {
    const response = await authService.revokeSession(sessionId)
    sessions.value = sessions.value.filter(session => String(session.id) !== String(sessionId))
    return response
  }

  async function revokeAllSessions() {
    const response = await authService.revokeAllSessions()
    sessions.value = sessions.value.filter(session => session.current)
    return response
  }

  async function revokeDevice(deviceId) {
    const response = await authService.revokeDevice(deviceId)
    devices.value = devices.value.filter(device => String(device.id) !== String(deviceId))
    return response
  }

  async function revokeAllDevices() {
    const response = await authService.revokeAllDevices()
    devices.value = []
    return response
  }

  async function sendEventFeedback(eventId, action) {
    const response = await authService.sendSecurityEventFeedback(eventId, action)
    await Promise.allSettled([fetchSessions(), fetchDevices(), fetchSecurityRisk(), fetchSecurityEvents()])
    snapshotUpdatedAt.value = new Date().toISOString()
    return response
  }

  onUnmounted(() => {
    stopLiveStream()
  })

  return {
    sessions,
    devices,
    securityEvents,
    securityRisk,
    securityFeedLoading,
    snapshotSyncing,
    snapshotUpdatedAt,
    streamStatus,
    newestLiveEventId,
    mfaStatus,
    trustedDeviceIds,
    suspiciousSessions,
    revokableSessions,
    fetchSessions,
    fetchDevices,
    fetchMfaStatus,
    fetchSecurityEvents,
    fetchSecurityRisk,
    refreshSecurityState,
    startLiveStream,
    stopLiveStream,
    clearState,
    revokeSession,
    revokeAllSessions,
    revokeDevice,
    revokeAllDevices,
    sendEventFeedback
  }
}