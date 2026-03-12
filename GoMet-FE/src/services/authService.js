import api, { getStoredTokens } from './api'

// ─── Core Auth ─────────────────────────────────────────────────────────────

export const login = (email, password, deviceId, deviceName) =>
  api.post('/api/auth/login', { email, password, deviceId, deviceName }).then(r => r.data)

/** Complete TOTP / backup-code verification after a /login that returned requiresMfa. */
export const verifyMfa = ({ mfaSessionToken, totpCode, backupCode, deviceId, deviceName }) =>
  api.post('/api/auth/mfa/verify', { mfaSessionToken, totpCode, backupCode, deviceId, deviceName })
     .then(r => r.data)

/** Rotate a JWT refresh token to get a fresh access + refresh pair. */
export const refreshTokens = (refreshToken, deviceId) =>
  api.post('/api/auth/refresh', { refreshToken, deviceId }).then(r => r.data)

export const logout = (refreshToken) =>
  api.post('/api/auth/logout', refreshToken ? { refreshToken } : {}).then(r => r.data)

// ─── Registration (OTP flow) ───────────────────────────────────────────────

/** Step 1: validate + send OTP email. Does NOT create the account yet. */
export const sendOtp = (username, email, password) =>
  api.post('/api/auth/send-otp', { username, email, password }).then(r => r.data)

/** Step 2: verify OTP and create the account. Returns auth data on success. */
export const verifyOtp = (email, otp) =>
  api.post('/api/auth/verify-otp', { email, otp }).then(r => r.data)

// ─── Profile ──────────────────────────────────────────────────────────────

export const getMe = () =>
  api.get('/api/auth/me').then(r => r.data)

// ─── Password ─────────────────────────────────────────────────────────────

/** Send a password-reset link (silent — never reveals account existence). */
export const forgotPassword = (identifier) =>
  api.post('/api/auth/forgot-password', { identifier }).then(r => r.data)

/** Submit the reset token and optionally request secure auto-login. */
export const resetPassword = ({ token, newPassword, autoLogin = false, deviceId, deviceName }) =>
  api.post('/api/auth/reset-password', { token, newPassword, autoLogin, deviceId, deviceName }).then(r => r.data)

/** Change password while authenticated (requires current password). */
export const changePassword = (currentPassword, newPassword) =>
  api.post('/api/auth/change-password', { currentPassword, newPassword }).then(r => r.data)

// ─── Google Sign-In ───────────────────────────────────────────────────────

export const loginWithGoogle = (idToken, deviceId) =>
  api.post('/api/auth/google', { idToken, deviceId }).then(r => r.data)

// ─── MFA / TOTP ───────────────────────────────────────────────────────────

/** Get a new TOTP secret + QR code (not saved until /mfa/enable is called). */
export const getMfaSetup = () =>
  api.get('/api/auth/mfa/setup').then(r => r.data)

/** Enable TOTP after the user verifies the first code. */
export const enableMfa = ({ secret, totpCode, backupCodes }) =>
  api.post('/api/auth/mfa/enable', { secret, totpCode, backupCodes }).then(r => r.data)

/** Disable TOTP (requires confirmation code). */
export const disableMfa = ({ totpCode, backupCode }) =>
  api.delete('/api/auth/mfa/disable', { data: { totpCode, backupCode } }).then(r => r.data)

/** Current MFA status + remaining backup codes count. */
export const getMfaStatus = () =>
  api.get('/api/auth/mfa/status').then(r => r.data)

// ─── Sessions & Devices ───────────────────────────────────────────────────

/** List all active sessions. Pass currentRefreshToken to mark the current session. */
export const getSessions = (currentRefreshToken) =>
  api.get('/api/sessions', {
    headers: currentRefreshToken ? { 'X-Refresh-Token': currentRefreshToken } : {}
  }).then(r => r.data)

export const revokeSession = (sessionId) =>
  api.delete(`/api/sessions/${sessionId}`).then(r => r.data)

export const revokeAllSessions = () =>
  api.delete('/api/sessions').then(r => r.data)

/** List trusted devices. */
export const getTrustedDevices = () =>
  api.get('/api/sessions/devices').then(r => r.data)

export const getSecurityEvents = (page = 0, size = 12) =>
  api.get('/api/sessions/events', { params: { page, size } }).then(r => r.data)

export const getSecurityRisk = () =>
  api.get('/api/sessions/risk').then(r => r.data)

export const sendSecurityEventFeedback = (eventId, action) => {
  const refreshToken = localStorage.getItem('refreshToken') || null
  return api.post(
    `/api/sessions/events/${eventId}/feedback`,
    { action },
    {
      headers: refreshToken ? { 'X-Refresh-Token': refreshToken } : {}
    }
  ).then(r => r.data)
}

export function subscribeSecurityEventsStream ({ signal, onEvent, onConnected, onError }) {
  const { accessToken, legacyToken } = getStoredTokens()
  const bearer = accessToken || legacyToken

  if (!bearer) {
    throw new Error('Missing auth token for security event stream')
  }

  const baseUrl = api.defaults.baseURL || 'http://localhost:8080'

  ;(async () => {
    try {
      const response = await fetch(`${baseUrl}/api/sessions/events/stream`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${bearer}`,
          Accept: 'text/event-stream'
        },
        signal,
        credentials: 'include'
      })

      if (!response.ok || !response.body) {
        throw new Error(`Security stream failed: ${response.status}`)
      }

      onConnected?.()

      const reader = response.body.getReader()
      const decoder = new TextDecoder('utf-8')
      let buffer = ''

      while (true) {
        const { value, done } = await reader.read()
        if (done) break
        buffer += decoder.decode(value, { stream: true })

        const chunks = buffer.split('\n\n')
        buffer = chunks.pop() || ''

        for (const chunk of chunks) {
          const lines = chunk.split(/\r?\n/)
          let eventName = 'message'
          const dataLines = []

          for (const line of lines) {
            if (line.startsWith('event:')) {
              eventName = line.slice(6).trim()
            } else if (line.startsWith('data:')) {
              dataLines.push(line.slice(5).trim())
            }
          }

          if (!dataLines.length) {
            continue
          }

          const payloadText = dataLines.join('\n')
          let payload = payloadText
          try {
            payload = JSON.parse(payloadText)
          } catch {
            // Keep raw text payload when server sends plain text.
          }

          const streamEvent = payload?.event || payload
          const streamRisk = payload?.risk || null

          onEvent?.({ event: eventName, data: streamEvent, risk: streamRisk, raw: payload })
        }
      }
    } catch (error) {
      if (signal?.aborted) {
        return
      }
      onError?.(error)
    }
  })()
}

export const revokeDevice = (deviceId) =>
  api.delete(`/api/sessions/devices/${deviceId}`).then(r => r.data)

export const revokeAllDevices = () =>
  api.delete('/api/sessions/devices').then(r => r.data)

// ─── Account Status (ban detection polling) ───────────────────────────────

/**
 * Lightweight probe to detect if the current account has been banned.
 * Returns { isActive: boolean } derived from GET /api/auth/me.
 * isActive=false means the account has been banned (isActive field = 0).
 * Used by DefaultLayout to poll every 30 s while the user is logged in.
 */
export const checkAccountStatus = () =>
  api.get('/api/auth/me').then(r => ({ isActive: r.data?.isActive !== 0 }))

// ─── Legacy alias ─────────────────────────────────────────────────────────
export const register = (username, email, password) => sendOtp(username, email, password)
