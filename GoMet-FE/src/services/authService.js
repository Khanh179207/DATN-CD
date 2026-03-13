import api, { getStoredTokens } from './api'

// --- Core Auth -------------------------------------------------------------

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

// --- Registration (OTP flow) -----------------------------------------------

/** Step 1: validate + send OTP email. Does NOT create the account yet. */
export const sendOtp = (username, email, password) =>
  api.post('/api/auth/send-otp', { username, email, password }).then(r => r.data)

/** Step 2: verify OTP and create the account. Returns auth data on success. */
export const verifyOtp = (email, otp) =>
  api.post('/api/auth/verify-otp', { email, otp }).then(r => r.data)

// --- Profile --------------------------------------------------------------

export const getMe = () =>
  api.get('/api/auth/me').then(r => r.data)

// --- Password -------------------------------------------------------------

/** Send a password-reset link (silent — never reveals account existence). */
export const forgotPassword = (identifier) =>
  api.post('/api/auth/forgot-password', { identifier }).then(r => r.data)

/** Submit the reset token (from email link) and the new password. */
export const resetPassword = (token, newPassword) =>
  api.post('/api/auth/reset-password', { token, newPassword }).then(r => r.data)

/** Google Login: Send Google ID Token to backend for verification and user info */
export const googleLogin = (idToken) =>
  api.post('/api/auth/google', { token: idToken }).then(r => r.data)

// ─── MFA / TOTP ───────────────────────────────────────────────────────────

export const getMfaSetup = () =>
  api.get('/api/auth/mfa/setup').then(r => r.data)

export const enableMfa = ({ secret, totpCode, backupCodes }) =>
  api.post('/api/auth/mfa/enable', { secret, totpCode, backupCodes }).then(r => r.data)

// ─── Legacy aliases ─────────────────────────────────────────────────────────
export const register = (username, email, password) => sendOtp(username, email, password)
export const loginWithGoogle = (idToken, deviceId) => googleLogin(idToken)
export const checkAccountStatus = () =>
  api.get('/api/auth/me').then(r => ({ isActive: r.data?.isActive !== 0 }))