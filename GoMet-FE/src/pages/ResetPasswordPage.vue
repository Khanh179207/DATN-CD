<template>
  <div class="auth-page">
    <div class="bg-blob bg-blob-1"></div>
    <div class="bg-blob bg-blob-2"></div>
    <div class="bg-grain"></div>

    <div class="auth-card">
      <div class="brand">
        <span class="brand-name">GOMET<span class="dot">.</span></span>
        <span class="brand-sub">CỘNG ĐỒNG ẨM THỰC TINH HOA</span>
      </div>

      <Transition name="fade-slide" mode="out-in">

        <div v-if="state === 'invalid'" key="invalid" class="result-pane">
          <div class="result-ring result-ring--error">
            <svg viewBox="0 0 24 24" fill="none" class="result-icon">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5"/>
              <path d="M15 9l-6 6M9 9l6 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </div>
          <h2 class="r-title">Đường dẫn không hợp lệ</h2>
          <p class="r-sub">Link khôi phục mật khẩu này đã hết hạn, không hợp lệ hoặc đã được sử dụng. Vui lòng yêu cầu một link mới.</p>
          <button class="btn-primary btn-full" @click="goForgot">Yêu cầu Link Mới</button>
        </div>

        <div v-else-if="state === 'success'" key="success" class="result-pane">
          <div class="result-ring result-ring--success">
            <svg viewBox="0 0 52 52" class="checkmark" xmlns="http://www.w3.org/2000/svg">
              <circle class="checkmark__circle" cx="26" cy="26" r="25" fill="none"/>
              <path class="checkmark__check" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
            </svg>
          </div>
          <h2 class="r-title">Đổi mật khẩu thành công!</h2>
          <p class="r-sub">Tài khoản của bạn đã được cập nhật an toàn. Bây giờ bạn có thể đăng nhập bằng mật khẩu mới.</p>
          <button class="btn-primary btn-full" @click="goLogin">Quay lại Đăng nhập</button>
        </div>

        <div v-else key="form" class="form-pane">
          <div class="icon-wrap">
            <div class="icon-glow"></div>
            <svg class="icon" viewBox="0 0 24 24" fill="none">
              <path d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>

          <h1 class="card-title">Thiết Lập Mật Khẩu Mới</h1>
          <p class="card-sub">Vui lòng chọn một mật khẩu mạnh. Tối thiểu 8 ký tự, bao gồm chữ hoa, chữ thường và số.</p>

          <div v-if="newPassword" class="strength-wrap">
            <div class="strength-bar">
              <div class="strength-fill" :class="`strength-fill--${strengthLevel}`" :style="{ width: strengthWidth }"></div>
            </div>
            <span class="strength-label" :class="`strength-label--${strengthLevel}`">{{ strengthText }}</span>
          </div>

          <form @submit.prevent="handleReset" novalidate>
            <div class="field-group">
              <label class="field-label">Mật khẩu mới</label>
              <div class="input-wrap">
                <input
                  v-model="newPassword"
                  :type="showNew ? 'text' : 'password'"
                  class="field-input"
                  :class="{ 'input--error': fieldErrors.newPassword }"
                  placeholder="Tối thiểu 8 ký tự"
                  autocomplete="new-password"
                  :disabled="state === 'loading'"
                  @input="fieldErrors.newPassword = ''"
                />
                <button type="button" class="eye-btn" @click="showNew = !showNew" tabindex="-1">
                  <svg v-if="showNew" viewBox="0 0 24 24" fill="none" class="eye-icon">
                    <path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19m-6.72-1.07a3 3 0 11-4.24-4.24M1 1l22 22" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none" class="eye-icon">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="1.5"/>
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                </button>
              </div>
              <Transition name="fade-slide">
                <p v-if="fieldErrors.newPassword" class="error-msg">
                  <svg viewBox="0 0 20 20" fill="currentColor" class="err-icon"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/></svg>
                  {{ fieldErrors.newPassword }}
                </p>
              </Transition>
            </div>

            <div class="field-group">
              <label class="field-label">Xác nhận mật khẩu</label>
              <div class="input-wrap">
                <input
                  v-model="confirmPassword"
                  :type="showConfirm ? 'text' : 'password'"
                  class="field-input"
                  :class="{ 'input--error': fieldErrors.confirmPassword }"
                  placeholder="Nhập lại mật khẩu mới"
                  autocomplete="new-password"
                  :disabled="state === 'loading'"
                  @input="fieldErrors.confirmPassword = ''"
                />
                <button type="button" class="eye-btn" @click="showConfirm = !showConfirm" tabindex="-1">
                  <svg v-if="showConfirm" viewBox="0 0 24 24" fill="none" class="eye-icon">
                    <path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19m-6.72-1.07a3 3 0 11-4.24-4.24M1 1l22 22" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none" class="eye-icon">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="1.5"/>
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                </button>
              </div>
              <Transition name="fade-slide">
                <p v-if="fieldErrors.confirmPassword" class="error-msg">
                  <svg viewBox="0 0 20 20" fill="currentColor" class="err-icon"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 00-1-1z" clip-rule="evenodd"/></svg>
                  {{ fieldErrors.confirmPassword }}
                </p>
              </Transition>
            </div>

            <Transition name="fade-slide">
              <div v-if="serverError" class="server-error">
                <svg viewBox="0 0 20 20" fill="currentColor" class="err-icon"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/></svg>
                {{ serverError }}
              </div>
            </Transition>

            <button
              type="submit"
              class="btn-primary btn-full"
              :disabled="!canSubmit || state === 'loading'"
            >
              <span v-if="state === 'loading'" class="spinner"></span>
              <span>{{ state === 'loading' ? 'Đang cập nhật...' : 'Đổi Mật Khẩu' }}</span>
            </button>
          </form>

          <div class="footer-links">
            <button class="link-btn" @click="goForgot">← Yêu cầu link khôi phục mới</button>
          </div>
        </div>

      </Transition>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { resetPassword } from '@/services/authService'

const route  = useRoute()
const router = useRouter()

const token           = ref('')
const newPassword     = ref('')
const confirmPassword = ref('')
const showNew         = ref(false)
const showConfirm     = ref(false)
const state           = ref('idle')   // 'idle' | 'loading' | 'success' | 'invalid'
const fieldErrors     = ref({ newPassword: '', confirmPassword: '' })
const serverError     = ref('')

// ── On mount: check token presence ───────────────────────────────────────
onMounted(() => {
  const t = route.query.token
  if (!t || typeof t !== 'string' || t.trim().length < 10) {
    state.value = 'invalid'
  } else {
    token.value = t.trim()
  }
})

// ── Password strength ─────────────────────────────────────────────────────
const strengthLevel = computed(() => {
  const pw = newPassword.value
  if (!pw) return 'none'
  let score = 0
  if (pw.length >= 8)  score++
  if (/[A-Z]/.test(pw)) score++
  if (/[a-z]/.test(pw)) score++
  if (/\d/.test(pw))    score++
  if (/[^A-Za-z0-9]/.test(pw)) score++
  if (score <= 1) return 'weak'
  if (score === 2) return 'fair'
  if (score === 3) return 'good'
  return 'strong'
})
const strengthText  = computed(() => ({ weak: 'Yếu', fair: 'Trung bình', good: 'Khá', strong: 'Mạnh' }[strengthLevel.value] || ''))
const strengthWidth = computed(() => ({ weak: '25%', fair: '50%', good: '75%', strong: '100%' }[strengthLevel.value] || '0%'))

// ── Submit guard ──────────────────────────────────────────────────────────
const canSubmit = computed(() => newPassword.value.trim().length >= 8 && confirmPassword.value.trim().length >= 1)

const validateFields = () => {
  let ok = true
  const pw = newPassword.value
  
  // Tối ưu UX: Gộp báo lỗi thành 1 câu rõ ràng thay vì rớt từng lỗi lắt nhắt
  if (!pw || pw.length < 8 || !/[A-Z]/.test(pw) || !/[a-z]/.test(pw) || !/\d/.test(pw)) {
    fieldErrors.value.newPassword = 'Mật khẩu phải từ 8 ký tự, gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 số.'
    ok = false
  }
  
  if (pw !== confirmPassword.value) {
    fieldErrors.value.confirmPassword = 'Mật khẩu xác nhận không khớp.'
    ok = false
  }
  return ok
}

const handleReset = async () => {
  serverError.value = ''
  if (!validateFields()) return

  state.value = 'loading'
  try {
    await resetPassword(token.value, newPassword.value)
    state.value = 'success'
  } catch (err) {
    const msg = err?.response?.data?.message
    if (msg && (msg.toLowerCase().includes('invalid') || msg.toLowerCase().includes('expired') || msg.toLowerCase().includes('used'))) {
      state.value = 'invalid'
    } else {
      serverError.value = msg || 'Có lỗi xảy ra. Vui lòng thử lại sau.'
      state.value = 'idle'
    }
  }
}

const goLogin  = () => router.push({ path: '/', hash: '#sectionsigninlanding' })
const goForgot = () => router.push({ path: '/', hash: '#sectionsigninlanding' }) 
</script>

<style scoped>
/* ── Page shell ──────────────────────────────────────────────────────────── */
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F9F5F0;
  font-family: 'Mulish', sans-serif;
  padding: 24px;
  position: relative;
  overflow: hidden;
}
.bg-blob {
  position: absolute; border-radius: 50%;
  filter: blur(80px); opacity: 0.18; pointer-events: none;
}
.bg-blob-1 {
  width: 500px; height: 500px;
  background: radial-gradient(circle, #EA580C 0%, transparent 70%);
  top: -120px; left: -120px;
}
.bg-blob-2 {
  width: 400px; height: 400px;
  background: radial-gradient(circle, #F97316 0%, transparent 70%);
  bottom: -100px; right: -80px;
}
.bg-grain {
  position: absolute; inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='200' height='200' filter='url(%23n)' opacity='0.04'/%3E%3C/svg%3E");
  pointer-events: none;
}

/* ── Card ────────────────────────────────────────────────────────────────── */
.auth-card {
  background: #fff;
  border-radius: 24px;
  padding: 48px 44px;
  width: 100%;
  max-width: 460px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.10);
  position: relative;
  z-index: 1;
}

/* ── Brand ───────────────────────────────────────────────────────────────── */
.brand { display: flex; flex-direction: column; align-items: center; margin-bottom: 36px; }
.brand-name { font-size: 1.8rem; font-weight: 900; letter-spacing: 4px; color: #1C1917; }
.brand-name .dot { color: #EA580C; }
.brand-sub { font-size: 0.65rem; letter-spacing: 3px; color: #A8A29E; margin-top: 2px; }

/* ── Icon ────────────────────────────────────────────────────────────────── */
.icon-wrap {
  position: relative; width: 72px; height: 72px;
  margin: 0 auto 24px;
  display: flex; align-items: center; justify-content: center;
}
.icon-glow {
  position: absolute; inset: -8px;
  background: radial-gradient(circle, rgba(234,88,12,0.2) 0%, transparent 70%);
  border-radius: 50%;
}
.icon { width: 44px; height: 44px; color: #EA580C; position: relative; z-index: 1; }

/* ── Headings ────────────────────────────────────────────────────────────── */
.card-title {
  font-family: 'Playfair Display', serif;
  font-size: 1.7rem; font-weight: 700;
  color: #1C1917; margin: 0 0 10px; text-align: center;
}
.card-sub {
  font-size: 0.88rem; color: #78716C;
  line-height: 1.6; text-align: center; margin: 0 0 24px;
}

/* ── Strength bar ────────────────────────────────────────────────────────── */
.strength-wrap {
  display: flex; align-items: center; gap: 10px;
  margin-bottom: 20px;
}
.strength-bar {
  flex: 1; height: 5px; background: #F5F5F4; border-radius: 99px; overflow: hidden;
}
.strength-fill {
  height: 100%; border-radius: 99px;
  transition: width 0.35s ease, background-color 0.35s;
}
.strength-fill--weak   { background: #EF4444; }
.strength-fill--fair   { background: #F97316; }
.strength-fill--good   { background: #EAB308; }
.strength-fill--strong { background: #22C55E; }
.strength-label { font-size: 0.75rem; font-weight: 700; white-space: nowrap; }
.strength-label--weak   { color: #EF4444; }
.strength-label--fair   { color: #F97316; }
.strength-label--good   { color: #EAB308; }
.strength-label--strong { color: #22C55E; }

/* ── Form fields ─────────────────────────────────────────────────────────── */
.field-group { margin-bottom: 20px; }
.field-label {
  display: block; font-size: 0.78rem; font-weight: 700;
  color: #57534E; letter-spacing: 1px; text-transform: uppercase; margin-bottom: 8px;
}
.input-wrap { position: relative; }
.field-input {
  width: 100%; padding: 14px 48px 14px 16px;
  border: 1.5px solid #E7E5E4; border-radius: 12px;
  font-size: 0.95rem; color: #1C1917; background: #FAFAF9;
  outline: none; transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box; font-family: 'Mulish', sans-serif;
}
.field-input:focus {
  border-color: #EA580C;
  box-shadow: 0 0 0 3px rgba(234,88,12,0.12);
  background: #fff;
}
.field-input.input--error { border-color: #EF4444; }
.field-input:disabled { opacity: 0.5; cursor: not-allowed; }

.eye-btn {
  position: absolute; right: 14px; top: 50%; transform: translateY(-50%);
  background: none; border: none; cursor: pointer;
  color: #A8A29E; display: flex; align-items: center;
  padding: 0; transition: color 0.2s;
}
.eye-btn:hover { color: #EA580C; }
.eye-icon { width: 18px; height: 18px; }

/* ── Button ──────────────────────────────────────────────────────────────── */
.btn-primary {
  display: flex; align-items: center; justify-content: center; gap: 10px;
  padding: 14px 24px; background: #1C1917; color: #fff;
  border: none; border-radius: 12px; font-size: 0.9rem; font-weight: 700;
  letter-spacing: 0.5px; cursor: pointer; transition: background 0.2s, transform 0.15s;
}
.btn-primary:hover:not(:disabled) { background: #EA580C; }
.btn-primary:active:not(:disabled) { transform: translateY(1px); }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-full { width: 100%; }

/* ── Spinner ─────────────────────────────────────────────────────────────── */
.spinner {
  width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff;
  border-radius: 50%; animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── Errors ──────────────────────────────────────────────────────────────── */
.error-msg {
  display: flex; align-items: center; gap: 6px;
  color: #EF4444; font-size: 0.82rem; margin: 8px 0 0;
}
.err-icon { width: 14px; height: 14px; flex-shrink: 0; }
.server-error {
  display: flex; align-items: center; gap: 8px;
  background: #FEF2F2; border: 1px solid #FECACA;
  border-radius: 10px; padding: 12px 14px;
  color: #DC2626; font-size: 0.85rem;
  margin-bottom: 16px;
}

/* ── Footer links ────────────────────────────────────────────────────────── */
.footer-links { text-align: center; margin-top: 24px; }
.link-btn {
  background: none; border: none; color: #EA580C;
  font-size: 0.88rem; font-weight: 700; cursor: pointer; letter-spacing: 0.3px;
}
.link-btn:hover { text-decoration: underline; }

/* ── Result panes ────────────────────────────────────────────────────────── */
.result-pane {
  display: flex; flex-direction: column; align-items: center; text-align: center; padding: 8px 0;
}
.result-ring {
  width: 80px; height: 80px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; margin-bottom: 24px;
}
.result-ring--success { background: linear-gradient(135deg, #D1FAE5, #A7F3D0); }
.result-ring--error   { background: linear-gradient(135deg, #FEE2E2, #FECACA); }
.result-icon { width: 40px; height: 40px; }
.result-ring--success .result-icon { color: #10B981; }
.result-ring--error   .result-icon { color: #EF4444; }

.checkmark { width: 52px; height: 52px; }
.checkmark__circle { stroke: #10B981; stroke-width: 2; stroke-dasharray: 166; stroke-dashoffset: 166; animation: stroke 0.6s ease forwards; }
.checkmark__check  { stroke: #10B981; stroke-width: 2; stroke-dasharray: 48;  stroke-dashoffset: 48;  animation: stroke 0.4s 0.5s ease forwards; }
@keyframes stroke { to { stroke-dashoffset: 0; } }

.r-title {
  font-family: 'Playfair Display', serif;
  font-size: 1.5rem; font-weight: 700; color: #1C1917; margin: 0 0 12px;
}
.r-sub {
  color: #78716C; font-size: 0.92rem; line-height: 1.6; margin: 0 0 28px;
}

/* ── Transitions ─────────────────────────────────────────────────────────── */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.25s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(6px); }
.fade-slide-leave-to  { opacity: 0; transform: translateY(-6px); }

/* ── Mobile responsive ───────────────────────────────────────────────────── */
@media (max-width: 480px) {
  .auth-card {
    padding: 28px 20px;
    border-radius: 18px;
  }
  .card-title { font-size: 1.4rem; }
  .brand-name  { font-size: 1.5rem; }
}
</style>