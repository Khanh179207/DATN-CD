<template>
  <div class="auth-page">
    <div class="bg-blob bg-blob-1"></div>
    <div class="bg-blob bg-blob-2"></div>
    <div class="bg-grid"></div>

    <div class="auth-card">
      <div class="brand">
        <span class="brand-name">GOMET<span class="dot">.</span></span>
        <span class="brand-sub">CULINARY COMMUNITY</span>
      </div>

      <div class="status-chip" :class="`chip-${state}`">
        {{ stateLabel }}
      </div>

      <div class="content-wrap">
        <div v-if="state === 'loading'" class="state-block">
          <div class="icon-shell icon-loading">
            <div class="spinner"></div>
          </div>
          <h2>Đang xác minh yêu cầu</h2>
          <p class="message">
            Hệ thống đang kiểm tra liên kết xác minh thiết bị. Quá trình này chỉ mất vài giây.
          </p>
          <div class="hint-box">Vui lòng giữ trang này mở cho đến khi hoàn tất.</div>
        </div>

        <div v-else-if="state === 'verified'" class="state-block">
          <div class="icon-shell icon-verified">
            <svg xmlns="http://www.w3.org/2000/svg" class="icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
            </svg>
          </div>
          <h2>Xác minh thiết bị thành công</h2>
          <p class="message">
            Thiết bị này đã được đánh dấu là tin cậy. Bạn sẽ được chuyển về trang chủ sau
            <strong>{{ redirectSeconds }} giây</strong>.
          </p>
          <div class="actions">
            <button @click="goHome" class="btn-primary">Vào trang chủ ngay</button>
          </div>
        </div>

        <div v-else-if="state === 'secured'" class="state-block">
          <div class="icon-shell icon-secured">
            <svg xmlns="http://www.w3.org/2000/svg" class="icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
          </svg>
          </div>
          <h2>Tài khoản đã được bảo vệ</h2>
          <p class="message">
            Tất cả phiên đăng nhập đã được thu hồi để đảm bảo an toàn. Bạn nên đặt lại mật khẩu nếu nghi ngờ tài khoản bị lộ.
          </p>
          <div class="actions two-col">
            <button @click="goForgotPassword" class="btn-primary">Đặt lại mật khẩu</button>
            <button @click="goHome" class="btn-secondary">Về trang chủ</button>
          </div>
        </div>

        <div v-else-if="state === 'error'" class="state-block">
          <div class="icon-shell icon-error">
            <svg xmlns="http://www.w3.org/2000/svg" class="icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </div>
          <h2>Liên kết không hợp lệ hoặc đã hết hạn</h2>
          <p class="message">{{ errorMessage }}</p>
          <div class="hint-box error-hint">
            Liên kết xác minh chỉ dùng một lần. Vui lòng đăng nhập lại để nhận email mới.
          </div>
          <div class="actions two-col">
            <button @click="goHome" class="btn-primary">Quay về trang chủ</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'

const route  = useRoute()
const router = useRouter()

const state        = ref('loading')   // loading | verified | secured | error
const errorMessage = ref('Liên kết này có thể đã hết hạn hoặc đã được sử dụng trước đó.')
const redirectSeconds = ref(3)

let redirectTimer = null

const stateLabel = computed(() => {
  if (state.value === 'loading') return 'Đang xử lý'
  if (state.value === 'verified') return 'Đã xác minh'
  if (state.value === 'secured') return 'Đã bảo mật'
  return 'Lỗi xác minh'
})

function clearRedirectTimer () {
  if (redirectTimer) {
    clearInterval(redirectTimer)
    redirectTimer = null
  }
}

function startAutoRedirect () {
  clearRedirectTimer()
  redirectSeconds.value = 3
  redirectTimer = setInterval(() => {
    if (redirectSeconds.value <= 1) {
      clearRedirectTimer()
      goHome()
      return
    }
    redirectSeconds.value -= 1
  }, 1000)
}

onMounted(async () => {
  const token  = route.query.token
  const action = route.query.action   // 'verify' | 'this-wasnt-me'

  if (!token) {
    state.value = 'error'
    return
  }

  try {
    const res = await api.get('/api/auth/verify-login', { params: { token, action } })
    const resultAction = res.data?.action || action

    if (resultAction === 'this-wasnt-me') {
      state.value = 'secured'
    } else {
      state.value = 'verified'
      startAutoRedirect()
    }
  } catch (e) {
    state.value = 'error'
    errorMessage.value =
      e.response?.data?.message ||
      'Liên kết xác minh không hợp lệ, đã hết hạn hoặc đã được sử dụng.'
  }
})

onBeforeUnmount(() => {
  clearRedirectTimer()
})

const goHome           = () => router.push('/home')
const goForgotPassword = () => router.push('/forgot-password')
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at top right, #ffedd5 0%, #fff7ed 42%, #f8fafc 100%);
  position: relative;
  overflow: hidden;
  padding: 2rem;
}

.bg-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.35;
  pointer-events: none;
}

.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148, 163, 184, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 163, 184, 0.08) 1px, transparent 1px);
  background-size: 28px 28px;
  mask-image: radial-gradient(circle at center, #000 30%, transparent 90%);
  pointer-events: none;
}

.bg-blob-1 {
  width: 400px; height: 400px;
  background: radial-gradient(circle, #fb923c 0%, #f97316 100%);
  top: -150px; left: -150px;
}

.bg-blob-2 {
  width: 300px; height: 300px;
  background: radial-gradient(circle, #fbbf24 0%, #f59e0b 100%);
  bottom: -100px; right: -100px;
}

.auth-card {
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(255, 255, 255, 0.65);
  border-radius: 24px;
  box-shadow: 0 28px 80px rgba(15, 23, 42, 0.14);
  max-width: 540px;
  width: 100%;
  position: relative;
  z-index: 10;
  padding: 2rem;
  backdrop-filter: blur(10px);
}

.brand {
  text-align: center;
  margin-bottom: 16px;
}

.brand-name { font-weight: 900; font-size: 1.5rem; letter-spacing: .1em; }
.dot { color: #ea580c; }
.brand-sub { display: block; font-size: .65rem; letter-spacing: .2em; color: #9ca3af; margin-top: -4px; }

.status-chip {
  display: inline-flex;
  align-items: center;
  padding: 5px 12px;
  border-radius: 999px;
  font-weight: 700;
  font-size: 12px;
  letter-spacing: 0.4px;
  margin: 0 auto 16px;
}

.chip-loading {
  background: #ffedd5;
  color: #c2410c;
}

.chip-verified {
  background: #dcfce7;
  color: #166534;
}

.chip-secured {
  background: #dbeafe;
  color: #1d4ed8;
}

.chip-error {
  background: #fee2e2;
  color: #991b1b;
}

.content-wrap {
  text-align: center;
}

.state-block h2 {
  margin: 0 0 10px;
  font-size: 28px;
  line-height: 1.2;
  color: #1f2937;
}

.message {
  margin: 0;
  color: #64748b;
  line-height: 1.65;
  font-size: 15px;
}

.icon-shell {
  width: 76px;
  height: 76px;
  border-radius: 999px;
  margin: 0 auto 14px;
  display: grid;
  place-items: center;
}

.icon {
  width: 34px;
  height: 34px;
}

.icon-loading {
  background: #fff7ed;
  color: #ea580c;
}

.icon-verified {
  background: #ecfdf5;
  color: #16a34a;
}

.icon-secured {
  background: #eff6ff;
  color: #2563eb;
}

.icon-error {
  background: #fef2f2;
  color: #dc2626;
}

.spinner {
  width: 36px;
  height: 36px;
  border-radius: 999px;
  border: 4px solid rgba(249, 115, 22, 0.22);
  border-top-color: #f97316;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.hint-box {
  margin-top: 14px;
  border-radius: 12px;
  background: #fff7ed;
  color: #9a3412;
  border: 1px solid #fed7aa;
  padding: 10px 12px;
  font-size: 13px;
  line-height: 1.45;
}

.error-hint {
  background: #fef2f2;
  color: #991b1b;
  border-color: #fecaca;
}

.actions {
  margin-top: 18px;
  display: grid;
  gap: 10px;
}

.actions.two-col {
  grid-template-columns: 1fr;
}

.btn-secondary {
  background: #fff;
  color: #334155;
  font-weight: 700;
  padding: 12px 20px;
  border-radius: 12px;
  border: 1px solid #cbd5e1;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #f8fafc;
  border-color: #94a3b8;
}

.btn-primary {
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
  font-weight: 700;
  padding: 12px 20px;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 8px 18px rgba(234, 88, 12, 0.28);
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(234, 88, 12, 0.34);
}

.btn-primary:disabled {
  background: #d6d3d1;
  cursor: not-allowed;
}

@media (max-width: 600px) {
  .auth-page {
    padding: 14px;
  }

  .auth-card {
    padding: 20px 16px;
    border-radius: 18px;
  }

  .state-block h2 {
    font-size: 22px;
  }

  .message {
    font-size: 14px;
  }
}
</style>
