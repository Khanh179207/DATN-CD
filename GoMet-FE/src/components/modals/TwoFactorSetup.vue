<template>
  <div class="mfa-setup">

    <!-- â”€â”€ SUCCESS â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ -->
    <div v-if="enabled" class="mfa-success">
      <div class="success-icon">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          <polyline points="9 12 11 14 15 10"/>
        </svg>
      </div>
      <h2 class="mfa-done-title">2FA đã kích hoạt!</h2>
      <p class="mfa-done-desc">Tài khoản của bạn được bảo vệ bằng xác thực 2 lớp.</p>
      <p class="mfa-done-note">Còn <strong>{{ remainingCodes }}</strong> mã dự phòng. Hãy lưu chúng ở nơi an toàn.</p>
      <button @click="$emit('done')" class="btn-primary w-full">Hoàn tất</button>
    </div>

    <!-- â”€â”€ SETUP WIZARD â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ -->
    <template v-else>

      <!-- Step indicator -->
      <div class="step-indicator">
        <div class="step" :class="{ active: step >= 1, done: step > 1 }">
          <span class="step-num">{{ step > 1 ? '✓' : '1' }}</span>
          <span class="step-label">Quét mã</span>
        </div>
        <div class="step-line"/>
        <div class="step" :class="{ active: step >= 2, done: step > 2 }">
          <span class="step-num">{{ step > 2 ? '✓' : '2' }}</span>
          <span class="step-label">Xác minh</span>
        </div>
        <div class="step-line"/>
        <div class="step" :class="{ active: step >= 3 }">
          <span class="step-num">3</span>
          <span class="step-label">Mã dự phòng</span>
        </div>
      </div>

      <!-- â”€â”€ STEP 1: Scan QR â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ -->
      <div v-if="step === 1" class="mfa-step">
        <h2 class="step-title">Bật xác thực 2 lớp</h2>
        <p class="step-desc">
          Mở ứng dụng <strong>Google Authenticator</strong>, <strong>Authy</strong> hoặc ứng dụng tương tự,
          rồi quét mã QR bên dưới.
        </p>

        <div v-if="loadingSetup" class="loading-center">
          <div class="spinner"/>
          <p>Đang tạo mã xác thực…</p>
        </div>

        <div v-if="setupError" class="alert-error">{{ setupError }}</div>

        <div v-if="setupData && !loadingSetup" class="qr-section">
          <div class="qr-wrap">
            <img :src="setupData.qrCodeBase64" alt="QR Code xác thực" class="qr-img" />
          </div>

          <details class="manual-entry">
            <summary>Không quét được? Nhập mã thủ công</summary>
            <div class="secret-box">{{ setupData.secret }}</div>
          </details>

          <button @click="step = 2" class="btn-primary w-full">
            Tiếp theo – Nhập mã xác minh
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- â”€â”€ STEP 2: Verify TOTP â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ -->
      <div v-if="step === 2" class="mfa-step">
        <h2 class="step-title">Nhập mã xác minh</h2>
        <p class="step-desc">Nhập mã <strong>6 chữ số</strong> hiện trên ứng dụng xác thực để hoàn tất kích hoạt.</p>

        <div v-if="verifyError" class="alert-error">{{ verifyError }}</div>

        <div class="otp-wrap">
          <input
            v-model="totpCode"
            type="text"
            inputmode="numeric"
            maxlength="6"
            placeholder="000 000"
            class="otp-input"
            @keyup.enter="verify"
            autofocus
          />
        </div>

        <div class="step-actions two-col">
          <button @click="step = 1" class="btn-secondary">← Quay lại</button>
          <button @click="verify" :disabled="totpCode.length !== 6 || verifyLoading" class="btn-primary">
            <span v-if="verifyLoading" class="spinner-xs"/>
            {{ verifyLoading ? 'Đang xác minh…' : 'Kích hoạt 2FA' }}
          </button>
        </div>
      </div>

      <!-- â”€â”€ STEP 3: Backup codes â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ -->
      <div v-if="step === 3" class="mfa-step">
        <h2 class="step-title">Lưu mã dự phòng</h2>
        <p class="step-desc">
          Mỗi mã chỉ dùng được <strong>một lần</strong>. Dùng khi mất điện thoại hoặc ứng dụng.
          Hãy lưu chúng ngay bây giờ – sau này bạn sẽ không xem lại được.
        </p>

        <div class="backup-grid">
          <div v-for="code in setupData.backupCodes" :key="code" class="backup-code">{{ code }}</div>
        </div>

        <button @click="copyBackupCodes" class="copy-btn">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="9" y="9" width="13" height="13" rx="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
          </svg>
          Sao chép tất cả mã
        </button>

        <div class="warn-box">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
          <span>Các mã này chỉ hiển thị <strong>một lần duy nhất</strong>. Hãy chụp ảnh hoặc lưu vào nơi an toàn trước khi tiếp tục.</span>
        </div>

        <label class="confirm-check">
          <input type="checkbox" v-model="savedConfirm" />
          <span>Tôi đã lưu mã dự phòng của mình</span>
        </label>

        <button @click="finish" :disabled="!savedConfirm" class="btn-primary w-full">
          Hoàn tất cài đặt
        </button>
      </div>

    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMfaSetup, enableMfa } from '@/services/authService'
import { toast } from '@/composables/useToast'

const emit = defineEmits(['done'])

const step          = ref(1)
const loadingSetup  = ref(false)
const verifyLoading = ref(false)
const verifyError   = ref('')
const setupError    = ref('')
const totpCode      = ref('')
const setupData     = ref(null)
const enabled       = ref(false)
const remainingCodes = ref(0)
const savedConfirm  = ref(false)

onMounted(async () => {
  loadingSetup.value = true
  try {
    setupData.value = await getMfaSetup()
  } catch (e) {
    setupError.value = e.response?.data?.message || 'Không thể tải cấu hình. Vui lòng thử lại.'
  } finally {
    loadingSetup.value = false
  }
})

async function verify () {
  if (totpCode.value.length !== 6) return
  verifyError.value = ''
  verifyLoading.value = true
  try {
    const result = await enableMfa({
      secret:      setupData.value.secret,
      totpCode:    totpCode.value,
      backupCodes: setupData.value.backupCodes
    })
    remainingCodes.value = result.remainingBackupCodes ?? setupData.value.backupCodes.length
    step.value = 3
  } catch (e) {
    verifyError.value = e.response?.data?.message || 'Mã không đúng. Kiểm tra lại ứng dụng và thử lại.'
    totpCode.value = ''
  } finally {
    verifyLoading.value = false
  }
}

function copyBackupCodes () {
  navigator.clipboard.writeText(setupData.value.backupCodes.join('\n'))
  toast.success('Đã sao chép mã dự phòng!')
}

function finish () {
  if (!savedConfirm.value) return
  enabled.value = true
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.mfa-setup {
  padding: 28px 28px 24px;
}

// â”€â”€ Success â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
.mfa-success {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 12px;
  padding: 12px 0 8px;
}
.success-icon {
  width: 68px;
  height: 68px;
  border-radius: 50%;
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #059669;
  margin-bottom: 4px;
}
.mfa-done-title { font-size: 1.4rem; font-weight: 700; color: $neutral-900; margin: 0; }
.mfa-done-desc  { font-size: 0.9rem; color: $neutral-500; margin: 0; }
.mfa-done-note  { font-size: 0.85rem; color: $primary-600; margin: 0; }

// â”€â”€ Step indicator â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
.step-indicator {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}
.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;

  .step-num {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    background: $neutral-200;
    color: $neutral-500;
    font-size: 0.75rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
  }
  .step-label {
    font-size: 0.7rem;
    color: $neutral-400;
    white-space: nowrap;
  }

  &.active .step-num {
    background: $primary-500;
    color: #fff;
  }
  &.active .step-label { color: $primary-600; font-weight: 600; }
  &.done .step-num {
    background: $success;
    color: #fff;
  }
}
.step-line {
  flex: 1;
  height: 2px;
  background: $neutral-200;
  margin: 0 6px;
  margin-bottom: 14px;
}

// â”€â”€ Step body â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
.mfa-step { display: flex; flex-direction: column; gap: 16px; }
.step-title { font-size: 1.2rem; font-weight: 700; color: $neutral-900; margin: 0; }
.step-desc  { font-size: 0.875rem; color: $neutral-600; margin: 0; line-height: 1.6; }

// â”€â”€ Loading â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
.loading-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px 0;
  color: $neutral-400;
  font-size: 0.875rem;
}

// â”€â”€ QR â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
.qr-section { display: flex; flex-direction: column; gap: 14px; }
.qr-wrap {
  display: flex;
  justify-content: center;
}
.qr-img {
  width: 200px;
  height: 200px;
  border: 2px solid $neutral-200;
  border-radius: 16px;
  padding: 8px;
  background: #fff;
}
.manual-entry {
  font-size: 0.825rem;
  color: $neutral-500;

  summary {
    cursor: pointer;
    color: $primary-500;
    font-weight: 500;
    padding: 4px 0;
    &:hover { color: $primary-600; }
  }
}
.secret-box {
  margin-top: 8px;
  padding: 10px 12px;
  background: $neutral-100;
  border-radius: 8px;
  font-family: monospace;
  font-size: 0.8rem;
  word-break: break-all;
  color: $neutral-800;
  user-select: all;
}

// â”€â”€ OTP input â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
.otp-wrap { display: flex; justify-content: center; }
.otp-input {
  width: 200px;
  text-align: center;
  font-size: 2rem;
  font-family: monospace;
  letter-spacing: 0.4em;
  border: 2px solid $neutral-300;
  border-radius: 14px;
  padding: 14px 12px;
  outline: none;
  transition: border-color 0.2s;

  &:focus { border-color: $primary-500; box-shadow: 0 0 0 3px rgba($primary-500, .12); }
  &::placeholder { color: $neutral-300; letter-spacing: 0.2em; }
}

// â”€â”€ Backup codes â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
.backup-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}
.backup-code {
  background: $neutral-50;
  border: 1px solid $neutral-200;
  border-radius: 8px;
  padding: 9px 12px;
  font-family: monospace;
  font-size: 0.875rem;
  text-align: center;
  color: $neutral-800;
  user-select: all;
}
.copy-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  color: $primary-500;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  padding: 4px 0;
  text-decoration: underline;
  text-underline-offset: 3px;

  &:hover { color: $primary-600; }
}
.warn-box {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  background: #fef3c7;
  border: 1px solid #fde68a;
  border-radius: 10px;
  padding: 12px;
  font-size: 0.825rem;
  color: #92400e;
  line-height: 1.5;

  svg { margin-top: 1px; flex-shrink: 0; }
}
.confirm-check {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.875rem;
  color: $neutral-700;
  cursor: pointer;

  input[type=checkbox] {
    width: 16px;
    height: 16px;
    accent-color: $primary-500;
    flex-shrink: 0;
  }
}

// â”€â”€ Alerts â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
.alert-error {
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 10px;
  padding: 10px 14px;
  font-size: 0.85rem;
  color: #dc2626;
}

// â”€â”€ Buttons â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: $primary-500;
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 12px 20px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;

  &:hover:not(:disabled) { background: $primary-600; }
  &:disabled { background: $neutral-300; cursor: not-allowed; }
}
.btn-secondary {
  background: #fff;
  color: $neutral-700;
  border: 1px solid $neutral-300;
  border-radius: 12px;
  padding: 12px 20px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;

  &:hover { background: $neutral-50; }
}
.w-full { width: 100%; box-sizing: border-box; }
.step-actions {
  display: flex;
  gap: 10px;
  margin-top: 4px;

  &.two-col > * { flex: 1; }
}

// â”€â”€ Spinner â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid $primary-200;
  border-top-color: $primary-500;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
.spinner-xs {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255,255,255,.4);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  flex-shrink: 0;
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>
