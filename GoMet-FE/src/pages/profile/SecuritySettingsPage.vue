<template>
  <div class="security-page">
    <div class="sec-header">
      <div class="sec-header-icon">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
        </svg>
      </div>
      <div>
        <h1 class="sec-title">Bảo mật tài khoản</h1>
        <p class="sec-subtitle">Quản lý phiên đăng nhập, thiết bị tin cậy và xác thực 2 lớp</p>
      </div>
    </div>

    <!-- ── TWO-FACTOR AUTH ───────────────────────────────────────── -->
    <section class="sec-card mfa-card">
      <div class="sec-card-head">
        <div class="head-left">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V7a4 4 0 0 1 8 0v4"/>
          </svg>
          <h2>Xác thực 2 lớp (2FA)</h2>
        </div>
        <span class="badge" :class="mfaStatus.mfaEnabled ? 'badge-on' : 'badge-off'">
          {{ mfaStatus.mfaEnabled ? '✓ Đã bật' : '✗ Chưa bật' }}
        </span>
      </div>
      <div class="sec-card-body">
        <template v-if="mfaStatus.mfaEnabled">
          <p class="info-text">
            2FA đang hoạt động. Bạn còn <strong>{{ mfaStatus.remainingBackupCodes }}</strong> mã dự phòng.
          </p>
          <button @click="showDisableMfa = true" class="btn-danger">Tắt 2FA</button>
        </template>
        <template v-else>
          <p class="info-text">
            Bảo vệ tài khoản bằng ứng dụng xác thực TOTP (Google Authenticator, Authy…).
            Mỗi lần đăng nhập cần nhập mã 6 số thay đổi theo thời gian.
          </p>
          <button @click="showSetupMfa = true" class="btn-primary">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
            </svg>
            Bật 2FA ngay
          </button>
        </template>
      </div>
    </section>

    <!-- ── ACTIVE SESSIONS ───────────────────────────────────────── -->
    <section class="sec-card">
      <div class="sec-card-head">
        <div class="head-left">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/>
          </svg>
          <h2>Phiên đăng nhập</h2>
        </div>
        <button @click="revokeAllSessions" :disabled="loadingSessions" class="btn-danger-sm">
          Thu hồi tất cả
        </button>
      </div>
      <div class="sec-card-body no-padding">
        <div v-if="loadingSessions" class="loading-wrap">
          <div class="spinner"/>
        </div>
        <ul v-else class="item-list">
          <li v-for="s in sessions" :key="s.id" class="item-row">
            <div class="item-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="2" y="3" width="20" height="14" rx="2"/>
              </svg>
            </div>
            <div class="item-info">
              <p class="item-name">
                {{ s.userAgent || 'Thiết bị không xác định' }}
                <span v-if="s.current" class="current-tag">Hiện tại</span>
              </p>
              <p class="item-meta">{{ s.ipAddress }} · {{ formatTime(s.lastUsedAt) }}</p>
            </div>
            <button v-if="!s.current" @click="revokeSession(s.id)" class="btn-danger-sm">
              Thu hồi
            </button>
          </li>
          <li v-if="sessions.length === 0" class="empty-msg">Không có phiên đăng nhập nào.</li>
        </ul>
      </div>
    </section>

    <!-- ── TRUSTED DEVICES ───────────────────────────────────────── -->
    <section class="sec-card">
      <div class="sec-card-head">
        <div class="head-left">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><polyline points="9 12 11 14 15 10"/>
          </svg>
          <h2>Thiết bị tin cậy</h2>
        </div>
        <button @click="revokeAllDevices" :disabled="loadingDevices" class="btn-danger-sm">
          Xoá tất cả
        </button>
      </div>
      <div class="sec-card-body no-padding">
        <div v-if="loadingDevices" class="loading-wrap">
          <div class="spinner"/>
        </div>
        <ul v-else class="item-list">
          <li v-for="d in devices" :key="d.id" class="item-row">
            <div class="item-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="5" y="2" width="14" height="20" rx="2"/><line x1="12" y1="18" x2="12.01" y2="18"/>
              </svg>
            </div>
            <div class="item-info">
              <p class="item-name">{{ d.deviceName || d.deviceFingerprint }}</p>
              <p class="item-meta">Tin cậy từ {{ formatDate(d.trustedAt) }} · Hết hạn {{ formatDate(d.expiresAt) }}</p>
            </div>
            <button @click="revokeDevice(d.id)" class="btn-danger-sm">Xoá</button>
          </li>
          <li v-if="devices.length === 0" class="empty-msg">Không có thiết bị tin cậy nào.</li>
        </ul>
      </div>
    </section>

    <!-- ── CHANGE PASSWORD ───────────────────────────────────────── -->
    <section class="sec-card">
      <div class="sec-card-head">
        <div class="head-left">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0 3 3L22 7l-3-3m-3.5 3.5L19 4"/>
          </svg>
          <h2>Đổi mật khẩu</h2>
        </div>
      </div>
      <div class="sec-card-body">
        <form @submit.prevent="submitChangePassword" class="pw-form">
          <div class="field-group">
            <label>Mật khẩu hiện tại</label>
            <input v-model="pwForm.currentPassword" type="password" placeholder="Nhập mật khẩu hiện tại" required />
          </div>
          <div class="field-group">
            <label>Mật khẩu mới</label>
            <input v-model="pwForm.newPassword" type="password" placeholder="Tối thiểu 12 ký tự" required minlength="12" />
          </div>
          <div class="field-group">
            <label>Xác nhận mật khẩu mới</label>
            <input v-model="pwForm.confirmPassword" type="password" placeholder="Nhập lại mật khẩu mới" required />
          </div>
          <p v-if="pwError" class="msg-error">{{ pwError }}</p>
          <p v-if="pwSuccess" class="msg-success">{{ pwSuccess }}</p>
          <button type="submit" :disabled="changingPw" class="btn-primary">
            {{ changingPw ? 'Đang lưu…' : 'Cập nhật mật khẩu' }}
          </button>
        </form>
      </div>
    </section>

    <!-- ── MODALS ────────────────────────────────────────────────── -->
    <Teleport to="body">
      <!-- MFA Setup wizard -->
      <div v-if="showSetupMfa" class="overlay" @click.self="closeMfaSetup">
        <div class="overlay-box overlay-lg" @click.stop>
          <button @click="closeMfaSetup" class="overlay-close">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
          <TwoFactorSetup @done="onMfaEnabled" />
        </div>
      </div>

      <!-- Disable MFA confirmation -->
      <div v-if="showDisableMfa" class="overlay" @click.self="showDisableMfa = false">
        <div class="overlay-box overlay-sm" @click.stop>
          <h3 class="modal-title">Tắt xác thực 2 lớp</h3>
          <p class="modal-desc">Nhập mã từ ứng dụng xác thực (hoặc mã dự phòng) để xác nhận.</p>
          <input v-model="disableMfaCode" type="text" inputmode="numeric" maxlength="8"
                 placeholder="Mã 6 số" class="modal-input" />
          <p v-if="disableMfaError" class="msg-error">{{ disableMfaError }}</p>
          <div class="modal-actions">
            <button @click="showDisableMfa = false" class="btn-secondary">Huỷ</button>
            <button @click="submitDisableMfa" :disabled="!disableMfaCode" class="btn-danger">Tắt 2FA</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as authService from '@/services/authService'
import TwoFactorSetup from '@/components/modals/TwoFactorSetup.vue'
import { toast } from '@/composables/useToast'

// ── Sessions ──────────────────────────────────────────────────────────────────
const sessions        = ref([])
const loadingSessions = ref(false)

const fetchSessions = async () => {
  loadingSessions.value = true
  try {
    const refreshToken = localStorage.getItem('refreshToken') || null
    sessions.value = await authService.getSessions(refreshToken)
  } catch { /* silent */ } finally {
    loadingSessions.value = false
  }
}

const revokeSession = async (id) => {
  try {
    await authService.revokeSession(id)
    sessions.value = sessions.value.filter(s => s.id !== id)
    toast.success('Đã thu hồi phiên đăng nhập.')
  } catch { toast.error('Thao tác thất bại.') }
}

const revokeAllSessions = async () => {
  if (!confirm('Thu hồi tất cả phiên khác? Bạn vẫn đăng nhập trên thiết bị này.')) return
  loadingSessions.value = true
  try {
    await authService.revokeAllSessions()
    await fetchSessions()
    toast.success('Đã thu hồi tất cả phiên khác.')
  } catch { toast.error('Thao tác thất bại.') } finally {
    loadingSessions.value = false
  }
}

// ── Devices ───────────────────────────────────────────────────────────────────
const devices        = ref([])
const loadingDevices = ref(false)

const fetchDevices = async () => {
  loadingDevices.value = true
  try {
    devices.value = await authService.getTrustedDevices()
  } catch { /* silent */ } finally {
    loadingDevices.value = false
  }
}

const revokeDevice = async (id) => {
  try {
    await authService.revokeDevice(id)
    devices.value = devices.value.filter(d => d.id !== id)
    toast.success('Đã xoá thiết bị.')
  } catch { toast.error('Thao tác thất bại.') }
}

const revokeAllDevices = async () => {
  if (!confirm('Xoá tất cả thiết bị tin cậy?')) return
  loadingDevices.value = true
  try {
    await authService.revokeAllDevices()
    devices.value = []
    toast.success('Đã xoá tất cả thiết bị.')
  } catch { toast.error('Thao tác thất bại.') } finally {
    loadingDevices.value = false
  }
}

// ── MFA ───────────────────────────────────────────────────────────────────────
const mfaStatus       = reactive({ mfaEnabled: false, remainingBackupCodes: 0 })
const showSetupMfa    = ref(false)
const showDisableMfa  = ref(false)
const disableMfaCode  = ref('')
const disableMfaError = ref('')

const fetchMfaStatus = async () => {
  try {
    const s = await authService.getMfaStatus()
    mfaStatus.mfaEnabled = s.mfaEnabled
    mfaStatus.remainingBackupCodes = s.remainingBackupCodes
  } catch { /* silent */ }
}

const closeMfaSetup = async () => {
  showSetupMfa.value = false
  await fetchMfaStatus()          // always sync badge when modal closes
}

const onMfaEnabled = async () => {
  showSetupMfa.value = false
  await fetchMfaStatus()
  toast.success('2FA đã được kích hoạt!')
}

const submitDisableMfa = async () => {
  disableMfaError.value = ''
  const code = disableMfaCode.value.trim()
  try {
    await authService.disableMfa(code.length > 6 ? { backupCode: code } : { totpCode: code })
    showDisableMfa.value = false
    disableMfaCode.value = ''
    mfaStatus.mfaEnabled = false
    mfaStatus.remainingBackupCodes = 0
    toast.success('2FA đã được tắt.')
  } catch (e) {
    disableMfaError.value = e.response?.data?.message || 'Mã không đúng. Thử lại.'
  }
}

// ── Password ──────────────────────────────────────────────────────────────────
const pwForm    = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
const pwError   = ref('')
const pwSuccess = ref('')
const changingPw = ref(false)

const submitChangePassword = async () => {
  pwError.value = ''
  pwSuccess.value = ''
  if (pwForm.newPassword !== pwForm.confirmPassword) {
    pwError.value = 'Mật khẩu mới không khớp.'
    return
  }
  changingPw.value = true
  try {
    await authService.changePassword(pwForm.currentPassword, pwForm.newPassword)
    pwSuccess.value = 'Mật khẩu đã được cập nhật!'
    Object.assign(pwForm, { currentPassword: '', newPassword: '', confirmPassword: '' })
  } catch (e) {
    pwError.value = e.response?.data?.message || 'Cập nhật thất bại.'
  } finally {
    changingPw.value = false
  }
}

// ── Helpers ───────────────────────────────────────────────────────────────────
const formatTime = (iso) => iso ? new Date(iso).toLocaleString('vi-VN') : '—'
const formatDate = (iso) => iso ? new Date(iso).toLocaleDateString('vi-VN') : '—'

onMounted(() => {
  fetchSessions()
  fetchDevices()
  fetchMfaStatus()
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.security-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 32px 20px 64px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

// ── Header ────────────────────────────────────────────────────────────────────
.sec-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 8px;
}
.sec-header-icon {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  background: linear-gradient(135deg, $primary-500, $primary-600);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}
.sec-title    { font-size: 1.5rem; font-weight: 700; color: $neutral-900; margin: 0 0 2px; }
.sec-subtitle { font-size: 0.875rem; color: $neutral-500; margin: 0; }

// ── Card ──────────────────────────────────────────────────────────────────────
.sec-card {
  background: #fff;
  border: 1px solid $neutral-200;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,.04);
}
.mfa-card { border-color: $primary-200; }

.sec-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: $neutral-50;
  border-bottom: 1px solid $neutral-200;

  .head-left {
    display: flex;
    align-items: center;
    gap: 8px;
    color: $neutral-700;

    svg { color: $primary-500; flex-shrink: 0; }
    h2 { font-size: 0.95rem; font-weight: 600; margin: 0; }
  }
}

.sec-card-body {
  padding: 18px 20px;

  &.no-padding { padding: 0; }
}

// ── Badge ─────────────────────────────────────────────────────────────────────
.badge {
  font-size: 0.75rem;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 20px;
}
.badge-on  { background: $success-bg; color: $success; }
.badge-off { background: $neutral-100; color: $neutral-500; }

// ── Info text ─────────────────────────────────────────────────────────────────
.info-text {
  font-size: 0.875rem;
  color: $neutral-600;
  margin: 0 0 14px;
  line-height: 1.6;
}

// ── Item list ─────────────────────────────────────────────────────────────────
.item-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.item-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  border-bottom: 1px solid $neutral-100;

  &:last-child { border-bottom: none; }
}
.item-icon {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  background: $neutral-100;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $neutral-500;
  flex-shrink: 0;
}
.item-info   { flex: 1; min-width: 0; }
.item-name   {
  font-size: 0.875rem;
  font-weight: 500;
  color: $neutral-800;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0 0 2px;
}
.item-meta   { font-size: 0.8rem; color: $neutral-500; margin: 0; }
.current-tag {
  display: inline-block;
  margin-left: 6px;
  font-size: 0.7rem;
  font-weight: 600;
  background: $success-bg;
  color: $success;
  border-radius: 10px;
  padding: 1px 7px;
  vertical-align: middle;
}
.empty-msg {
  padding: 20px;
  text-align: center;
  color: $neutral-400;
  font-size: 0.875rem;
}
.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 28px;
}

// ── Password form ─────────────────────────────────────────────────────────────
.pw-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
  max-width: 400px;
}
.field-group {
  display: flex;
  flex-direction: column;
  gap: 5px;

  label {
    font-size: 0.8rem;
    font-weight: 600;
    color: $neutral-700;
  }
  input {
    border: 1px solid $neutral-300;
    border-radius: 10px;
    padding: 9px 13px;
    font-size: 0.875rem;
    color: $neutral-900;
    outline: none;
    transition: border-color 0.2s;

    &:focus { border-color: $primary-500; box-shadow: 0 0 0 3px rgba($primary-500, .12); }
  }
}

// ── Buttons ───────────────────────────────────────────────────────────────────
.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: $primary-500;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px 18px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;

  &:hover:not(:disabled) { background: $primary-600; }
  &:disabled { background: $neutral-300; cursor: not-allowed; }
}
.btn-danger {
  background: #ef4444;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px 18px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;

  &:hover:not(:disabled) { background: #dc2626; }
  &:disabled { background: $neutral-300; cursor: not-allowed; }
}
.btn-danger-sm {
  background: transparent;
  color: #ef4444;
  border: 1px solid #fca5a5;
  border-radius: 8px;
  padding: 5px 12px;
  font-size: 0.78rem;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
  transition: all 0.2s;

  &:hover:not(:disabled) { background: #fef2f2; border-color: #ef4444; }
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}
.btn-secondary {
  background: #fff;
  color: $neutral-700;
  border: 1px solid $neutral-300;
  border-radius: 10px;
  padding: 10px 18px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;

  &:hover { background: $neutral-50; }
}

// ── Messages ──────────────────────────────────────────────────────────────────
.msg-error   { font-size: 0.8rem; color: $error;   margin: 0; }
.msg-success { font-size: 0.8rem; color: $success; margin: 0; }

// ── Spinner ───────────────────────────────────────────────────────────────────
.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid $primary-200;
  border-top-color: $primary-500;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

// ── Overlay / Modal ───────────────────────────────────────────────────────────
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,.55);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 16px;
}
.overlay-box {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0,0,0,.18);
  width: 100%;
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
}
.overlay-lg  { max-width: 560px; }
.overlay-sm  { max-width: 400px; padding: 28px; }
.overlay-close {
  position: absolute;
  top: 14px;
  right: 14px;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: none;
  background: $neutral-100;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: $neutral-600;
  z-index: 1;

  &:hover { background: $neutral-200; }
}
.modal-title   { font-size: 1.1rem; font-weight: 700; color: $neutral-900; margin: 0 0 8px; }
.modal-desc    { font-size: 0.875rem; color: $neutral-500; margin: 0 0 16px; line-height: 1.5; }
.modal-input {
  width: 100%;
  border: 1px solid $neutral-300;
  border-radius: 10px;
  padding: 10px 13px;
  font-size: 0.9rem;
  outline: none;
  margin-bottom: 12px;
  box-sizing: border-box;

  &:focus { border-color: $primary-500; box-shadow: 0 0 0 3px rgba($primary-500, .12); }
}
.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 6px;
}

@media (max-width: 768px) {
  .security-page {
    max-width: 100%;
    padding: 20px 12px 40px;
    gap: 14px;
  }

  .sec-header {
    align-items: flex-start;
    gap: 10px;
  }

  .sec-title {
    font-size: 1.2rem;
  }

  .sec-card-head {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .sec-card-body,
  .item-row {
    padding-left: 14px;
    padding-right: 14px;
  }

  .btn-danger-sm,
  .btn-primary,
  .btn-danger,
  .btn-secondary {
    width: 100%;
    justify-content: center;
  }

  .modal-actions {
    flex-direction: column;
  }
}
</style>
