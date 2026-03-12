<template>
  <div class="verify-page">
    <!-- Background decoration -->
    <div class="bg-blob bg-blob-1"></div>
    <div class="bg-blob bg-blob-2"></div>
    <div class="bg-grain"></div>

    <div class="verify-card" :class="{ 'card--success': state === 'success' }">

      <!-- ── SUCCESS STATE ─────────────────────────────────────── -->
      <Transition name="fade-slide" mode="out-in">
        <div v-if="state === 'success'" key="success" class="success-pane">
          <div class="success-ring">
            <svg viewBox="0 0 52 52" class="checkmark" xmlns="http://www.w3.org/2000/svg">
              <circle class="checkmark__circle" cx="26" cy="26" r="25" fill="none"/>
              <path class="checkmark__check" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
            </svg>
          </div>
          <h2 class="s-title">{{ $t('verify_email.success_title') }}</h2>
          <p class="s-sub">{{ $t('verify_email.success_sub') }}</p>
          <button class="btn-primary btn-full" @click="router.push('/home')">
            {{ $t('verify_email.success_btn') }}
          </button>
        </div>

        <!-- ── MAIN VERIFY PANE ────────────────────────────────── -->
        <div v-else key="verify" class="main-pane">

          <!-- Icon -->
          <div class="mail-icon-wrap">
            <div class="mail-icon-glow"></div>
            <svg class="mail-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="2" y="4" width="20" height="16" rx="3" stroke="currentColor" stroke-width="1.5"/>
              <path d="M2 8l8.615 5.301a2.25 2.25 0 002.77 0L22 8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </div>

          <!-- Heading -->
          <div class="card-text">
            <h1 class="card-title">{{ $t('verify_email.title') }}</h1>
            <p class="card-sub">
              {{ $t('verify_email.subtitle') }}<br>
              <strong class="email-highlight">{{ emailProp }}</strong>
            </p>
            <p class="card-note">{{ $t('verify_email.link_note') }}</p>
          </div>

          <!-- 6-digit OTP boxes -->
          <div class="otp-row" role="group" :aria-label="$t('verify_email.enter_code')">
            <input
              v-for="(digit, idx) in digits"
              :key="idx"
              :ref="el => { if(el) inputs[idx] = el }"
              class="otp-box"
              :class="{ 'otp-box--error': hasError, 'otp-box--filled': !!digit }"
              type="text"
              inputmode="numeric"
              maxlength="1"
              autocomplete="one-time-code"
              :value="digit"
              @keydown="onKeydown($event, idx)"
              @input="onInput($event, idx)"
              @paste.prevent="onPaste($event)"
              @focus="$event.target.select()"
            />
          </div>

          <!-- Error message -->
          <Transition name="fade-slide">
            <p v-if="errorMsg" class="error-msg">
              <svg viewBox="0 0 20 20" fill="currentColor" class="err-icon"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/></svg>
              {{ errorMsg }}
            </p>
          </Transition>

          <!-- Verify button -->
          <button
            class="btn-primary btn-full"
            :disabled="fullCode.length < 6 || state === 'loading'"
            @click="handleVerify"
          >
            <span v-if="state === 'loading'" class="spinner"></span>
            <span v-else>{{ $t('verify_email.verify_btn') }}</span>
          </button>

          <!-- Resend + back row -->
          <div class="card-footer">
            <button
              class="btn-ghost"
              :disabled="resendCooldown > 0"
              @click="handleResend"
            >
              <span v-if="resendCooldown > 0">
                {{ $t('verify_email.resend_in') }} {{ resendCooldown }}s
              </span>
              <span v-else>{{ $t('verify_email.resend') }}</span>
            </button>

            <span class="divider-dot">·</span>

            <button class="btn-ghost" @click="router.back()">
              ← {{ $t('verify_email.back_to_login') }}
            </button>
          </div>
        </div>
      </Transition>

    </div>

    <!-- GoMet brand stamp -->
    <p class="brand-stamp">GoMet &nbsp;·&nbsp; Culinary Community</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { verifyOtp } from '@/services/authService'
import { useAuthStore } from '@/stores/auth'

const route     = useRoute()
const router    = useRouter()
const { t }     = useI18n()
const authStore = useAuthStore()

// ── props from query string ──────────────────────────────────────
const emailProp = computed(() => route.query.email || '')
const tokenProp = computed(() => route.query.token || '')  // pre-fill if email link clicked

// ── 6-digit state ────────────────────────────────────────────────
const digits = ref(['', '', '', '', '', ''])
const inputs = ref([])
const fullCode = computed(() => digits.value.join(''))

// ── ui state ─────────────────────────────────────────────────────
const state    = ref('idle')   // 'idle' | 'loading' | 'success'
const hasError = ref(false)
const errorMsg = ref('')

// ── resend cooldown ──────────────────────────────────────────────
const resendCooldown = ref(60)
let cooldownTimer = null

function startCooldown (sec = 60) {
  resendCooldown.value = sec
  clearInterval(cooldownTimer)
  cooldownTimer = setInterval(() => {
    if (--resendCooldown.value <= 0) clearInterval(cooldownTimer)
  }, 1000)
}

// ── auto-fill when token comes from email link ───────────────────
onMounted(() => {
  startCooldown()
  if (tokenProp.value && /^\d{6}$/.test(tokenProp.value)) {
    tokenProp.value.split('').forEach((c, i) => { digits.value[i] = c })
    // Auto submit after a tiny delay so the user sees the filled boxes
    setTimeout(handleVerify, 500)
  } else {
    // Focus first box
    inputs.value[0]?.focus()
  }
})

onUnmounted(() => clearInterval(cooldownTimer))

// ── digit input handlers ─────────────────────────────────────────
function onInput (evt, idx) {
  const val = evt.target.value.replace(/\D/g, '').slice(-1)
  digits.value[idx] = val
  errorMsg.value = ''
  hasError.value  = false
  if (val && idx < 5) inputs.value[idx + 1]?.focus()
}

function onKeydown (evt, idx) {
  if (evt.key === 'Backspace') {
    if (!digits.value[idx] && idx > 0) {
      digits.value[idx - 1] = ''
      inputs.value[idx - 1]?.focus()
    } else {
      digits.value[idx] = ''
    }
  } else if (evt.key === 'ArrowLeft' && idx > 0) {
    inputs.value[idx - 1]?.focus()
  } else if (evt.key === 'ArrowRight' && idx < 5) {
    inputs.value[idx + 1]?.focus()
  } else if (evt.key === 'Enter' && fullCode.value.length === 6) {
    handleVerify()
  }
}

function onPaste (evt) {
  const text = evt.clipboardData.getData('text/plain').replace(/\D/g, '').slice(0, 6)
  if (!text) return
  text.split('').forEach((c, i) => { digits.value[i] = c })
  inputs.value[Math.min(text.length, 5)]?.focus()
}

// ── verify ───────────────────────────────────────────────────────
async function handleVerify () {
  if (!emailProp.value) { errorMsg.value = t('verify_email.error_no_email'); return }
  if (fullCode.value.length < 6) return

  state.value    = 'loading'
  errorMsg.value = ''
  hasError.value  = false

  try {
    const data = await verifyOtp(emailProp.value, fullCode.value)
    authStore.setAuthFromResponse(data)
    state.value = 'success'
    // auto redirect after 2 s
    setTimeout(() => router.push('/home'), 2000)
  } catch {
    state.value    = 'idle'
    hasError.value  = true
    errorMsg.value  = t('verify_email.error_invalid')
    digits.value    = ['', '', '', '', '', '']
    inputs.value[0]?.focus()
  }
}

// ── resend ───────────────────────────────────────────────────────
// Note: full resend requires sending credentials again; for now we restart the
// cooldown and clear digits so the user can re-enter a new code they request
// through the original registration flow.
function handleResend () {
  if (resendCooldown.value > 0) return
  startCooldown(60)
  digits.value = ['', '', '', '', '', '']
  errorMsg.value = ''
  hasError.value = false
  inputs.value[0]?.focus()
}
</script>

<style scoped>
/* ── page shell ─────────────────────────────────────────────────── */
.verify-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #faf9f7;
  position: relative;
  overflow: hidden;
  padding: 24px 16px;
  font-family: 'Quicksand', sans-serif;
}

/* Blobs */
.bg-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.35;
  pointer-events: none;
}
.bg-blob-1 {
  width: 500px; height: 500px;
  background: radial-gradient(circle, #f97316 0%, #fb923c 60%, transparent 100%);
  top: -180px; left: -120px;
}
.bg-blob-2 {
  width: 400px; height: 400px;
  background: radial-gradient(circle, #fbbf24 0%, #f59e0b 60%, transparent 100%);
  bottom: -160px; right: -100px;
}
.bg-grain {
  position: absolute; inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)' opacity='0.04'/%3E%3C/svg%3E");
  pointer-events: none; opacity: 0.5;
}

/* ── card ───────────────────────────────────────────────────────── */
.verify-card {
  position: relative;
  background: #ffffff;
  border-radius: 28px;
  padding: 48px 40px;
  width: 100%;
  max-width: 440px;
  box-shadow:
    0 4px 6px -1px rgba(0,0,0,.06),
    0 20px 60px -10px rgba(0,0,0,.12),
    0 0 0 1px rgba(0,0,0,.04);
  transition: transform 0.4s ease;
}
.card--success {
  box-shadow:
    0 4px 6px -1px rgba(34,197,94,.08),
    0 20px 60px -10px rgba(34,197,94,.18),
    0 0 0 1px rgba(34,197,94,.12);
}

/* ── mail icon ──────────────────────────────────────────────────── */
.mail-icon-wrap {
  position: relative;
  width: 72px; height: 72px;
  margin: 0 auto 28px;
}
.mail-icon-glow {
  position: absolute; inset: -8px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(249,115,22,0.2) 0%, transparent 70%);
}
.mail-icon {
  width: 72px; height: 72px;
  color: #f97316;
  background: #fff7ed;
  border-radius: 22px;
  padding: 16px;
  box-sizing: border-box;
  border: 1.5px solid #fed7aa;
}

/* ── card text ──────────────────────────────────────────────────── */
.card-text { text-align: center; margin-bottom: 32px; }
.card-title {
  font-family: 'Playfair Display', serif;
  font-size: 1.75rem;
  font-weight: 700;
  color: #1c1917;
  margin-bottom: 10px;
  line-height: 1.2;
}
.card-sub {
  font-size: 0.95rem;
  color: #78716c;
  line-height: 1.6;
  margin-bottom: 8px;
}
.email-highlight {
  color: #1c1917;
  font-weight: 700;
}
.card-note {
  font-size: 0.82rem;
  color: #a8a29e;
  line-height: 1.5;
}

/* ── OTP boxes ──────────────────────────────────────────────────── */
.otp-row {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-bottom: 16px;
}
.otp-box {
  width: 52px; height: 60px;
  border: 2px solid #e7e5e4;
  border-radius: 14px;
  background: #fafaf9;
  font-size: 1.5rem;
  font-weight: 700;
  color: #1c1917;
  text-align: center;
  outline: none;
  transition: border-color 0.18s, background 0.18s, transform 0.12s, box-shadow 0.18s;
  caret-color: #f97316;
  font-family: 'Quicksand', monospace;
}
.otp-box:focus {
  border-color: #f97316;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(249,115,22,0.12);
  transform: translateY(-2px);
}
.otp-box--filled {
  border-color: #fdba74;
  background: #fff7ed;
}
.otp-box--error {
  border-color: #f87171 !important;
  background: #fff1f2 !important;
  animation: shake 0.35s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%       { transform: translateX(-6px); }
  40%       { transform: translateX(6px); }
  60%       { transform: translateX(-4px); }
  80%       { transform: translateX(4px); }
}

/* ── error msg ──────────────────────────────────────────────────── */
.error-msg {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  color: #ef4444;
  background: #fff1f2;
  border: 1px solid #fecaca;
  border-radius: 10px;
  padding: 10px 14px;
  margin-bottom: 16px;
}
.err-icon { width: 16px; height: 16px; flex-shrink: 0; }

/* ── buttons ────────────────────────────────────────────────────── */
.btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 15px;
  background: #1c1917;
  color: #fff;
  border: none;
  border-radius: 14px;
  font-family: 'Quicksand', sans-serif;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.15s, background 0.2s;
  letter-spacing: 0.5px;
}
.btn-primary:hover:not(:disabled) {
  background: #292524;
  transform: translateY(-1px);
}
.btn-primary:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}
.btn-full { width: 100%; }

.btn-ghost {
  background: none;
  border: none;
  font-family: 'Quicksand', sans-serif;
  font-size: 0.875rem;
  color: #a8a29e;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: color 0.18s;
}
.btn-ghost:hover:not(:disabled) { color: #1c1917; }
.btn-ghost:disabled { opacity: 0.5; cursor: not-allowed; }

/* ── spinner ────────────────────────────────────────────────────── */
.spinner {
  display: inline-block;
  width: 18px; height: 18px;
  border: 2.5px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── footer ─────────────────────────────────────────────────────── */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 20px;
}
.divider-dot { color: #d6d3d1; font-size: 1.1rem; line-height: 1; }

/* ── success pane ───────────────────────────────────────────────── */
.success-pane {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 12px;
}
.success-ring {
  width: 80px; height: 80px;
  background: #f0fdf4;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  border: 2px solid #bbf7d0;
}

/* SVG animated checkmark */
.checkmark { width: 52px; height: 52px; }
.checkmark__circle {
  stroke: #22c55e;
  stroke-width: 2;
  stroke-dasharray: 166;
  stroke-dashoffset: 166;
  animation: stroke 0.6s cubic-bezier(0.65, 0, 0.45, 1) forwards;
}
.checkmark__check {
  stroke: #22c55e;
  stroke-width: 2.5;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-dasharray: 48;
  stroke-dashoffset: 48;
  animation: stroke 0.3s cubic-bezier(0.65, 0, 0.45, 1) 0.5s forwards;
}
@keyframes stroke {
  100% { stroke-dashoffset: 0; }
}

.s-title {
  font-family: 'Playfair Display', serif;
  font-size: 1.6rem;
  font-weight: 700;
  color: #15803d;
}
.s-sub {
  font-size: 0.95rem;
  color: #6b7280;
  line-height: 1.6;
  margin-bottom: 12px;
}

/* ── transitions ────────────────────────────────────────────────── */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.fade-slide-enter-from { opacity: 0; transform: translateY(12px); }
.fade-slide-leave-to   { opacity: 0; transform: translateY(-8px); }

/* ── brand stamp ────────────────────────────────────────────────── */
.brand-stamp {
  position: relative;
  margin-top: 24px;
  font-size: 0.78rem;
  color: #a8a29e;
  letter-spacing: 0.8px;
}

/* ── responsive ─────────────────────────────────────────────────── */
@media (max-width: 480px) {
  .verify-card { padding: 36px 24px; }
  .otp-box { width: 44px; height: 54px; font-size: 1.3rem; }
}
</style>
