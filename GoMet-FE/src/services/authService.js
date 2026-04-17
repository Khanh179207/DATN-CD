import api from './api'

export const login = (email, password) =>
  api.post('/api/auth/login', { email, password }).then(r => r.data)

/** Step 1: validate + send OTP email. Does NOT create the account yet. */
export const sendOtp = (username, email, password) =>
  api.post('/api/auth/send-otp', { username, email, password }).then(r => r.data)

/** Step 2: verify OTP and create the account. Returns auth data on success. */
export const verifyOtp = (email, otp) =>
  api.post('/api/auth/verify-otp', { email, otp }).then(r => r.data)

export const getMe = () =>
  api.get('/api/auth/me').then(r => r.data)

/** Send a password-reset link to the email / username (silent — never reveals account existence). */
export const forgotPassword = (identifier) =>
  api.post('/api/auth/forgot-password', { identifier }).then(r => r.data)

/** Submit the reset token (from email link) and the new password. */
export const resetPassword = (token, newPassword) =>
  api.post('/api/auth/reset-password', { token, newPassword }).then(r => r.data)

/** Google Login: Gửi ID Token của Google xuống Backend để xác thực và lấy thông tin user */
export const googleLogin = (idToken) =>
  api.post('/api/auth/google', { token: idToken }).then(r => r.data)

// ─── NEW: QUẢN LÝ TÀI KHOẢN (XÓA MỀM & KHÔI PHỤC) ───────────────────────────

/** Gửi OTP xác nhận xóa tài khoản (Yêu cầu Token) */
export const sendDeactivateOtp = (userId) =>
  api.post(`/api/users/${userId}/send-deactivate-otp`).then(r => r.data)

/** Thực hiện xóa mềm tài khoản (Yêu cầu Token + Pass + OTP) */
export const deactivateAccount = (userId, password, otp) =>
  api.post(`/api/users/${userId}/deactivate`, { password, otp }).then(r => r.data)

/** Gửi OTP khôi phục tài khoản (Không cần Token) */
export const sendRestoreOtp = (email) =>
  api.post('/api/auth/send-restore-otp', { email }).then(r => r.data)

/** Thực hiện khôi phục tài khoản (Không cần Token + Pass + OTP) */
export const restoreAccount = (email, password, otp) =>
  api.post('/api/auth/restore-account', { email, password, otp }).then(r => r.data)