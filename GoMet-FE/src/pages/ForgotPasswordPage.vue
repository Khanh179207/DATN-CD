<!-- <template>
  <div class="auth-page">
    <div class="bg-blob bg-blob-1"></div>
    <div class="bg-blob bg-blob-2"></div>
    <div class="bg-grain"></div>

    <div class="auth-card">
      <!-- Brand -->
      <div class="brand">
        <span class="brand-name">GOMET<span class="dot">.</span></span>
        <span class="brand-sub">CULINARY COMMUNITY</span>
      </div>

      <!-- SUCCESS STATE -->
      <Transition name="fade-slide" mode="out-in">
        <div v-if="state === 'sent'" key="sent" class="success-pane">
          <div class="success-ring">
            <svg viewBox="0 0 52 52" class="checkmark" xmlns="http://www.w3.org/2000/svg">
              <circle class="checkmark__circle" cx="26" cy="26" r="25" fill="none"/>
              <path class="checkmark__check" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
            </svg>
          </div>
          <h2 class="s-title">Check your inbox!</h2>
          <p class="s-sub">
            If an account with that email or username exists, we've sent a password reset link.
            The link is valid for <strong>30 minutes</strong>.
          </p>
          <button class="btn-primary btn-full" @click="goBack">← Back to Login</button>
        </div>

        <!-- FORM STATE -->
        <div v-else key="form" class="form-pane">
          <div class="icon-wrap">
            <div class="icon-glow"></div>
            <svg class="icon" viewBox="0 0 24 24" fill="none">
              <rect x="3" y="11" width="18" height="11" rx="2" stroke="currentColor" stroke-width="1.5"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </div>

          <h1 class="card-title">Forgot Password</h1>
          <p class="card-sub">Enter your email address or username. We'll send you a secure reset link.</p>

          <form @submit.prevent="handleSubmit" novalidate>
            <div class="field-group">
              <label class="field-label">Email or Username</label>
              <input
                v-model="identifier"
                type="text"
                class="field-input"
                :class="{ 'input--error': errorMsg }"
                placeholder="e.g. chef@gomet.com or chefkhanh"
                autocomplete="username"
                :disabled="state === 'loading'"
                @input="errorMsg = ''"
              />
              <Transition name="fade-slide">
                <p v-if="errorMsg" class="error-msg">
                  <svg viewBox="0 0 20 20" fill="currentColor" class="err-icon">
                    <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                  </svg>
                  {{ errorMsg }}
                </p>
              </Transition>
            </div>

            <button
              type="submit"
              class="btn-primary btn-full"
              :disabled="!identifier.trim() || state === 'loading'"
            >
              <span v-if="state === 'loading'" class="spinner"></span>
              <span>{{ state === 'loading' ? 'Sending...' : 'Send Reset Link' }}</span>
            </button>
          </form>

          <div class="footer-links">
            <button class="link-btn" @click="goBack">← Back to Login</button>
          </div>
        </div>
      </Transition>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { forgotPassword } from '@/services/authService'

const router = useRouter()

const identifier = ref('')
const state      = ref('idle')   // 'idle' | 'loading' | 'sent'
const errorMsg   = ref('')

const handleSubmit = async () => {
  if (!identifier.value.trim()) return
  state.value    = 'loading'
  errorMsg.value = ''

  try {
    await forgotPassword(identifier.value.trim())
    state.value = 'sent'
  } catch (err) {
    // Even on server errors, show generic message (don't leak info)
    const msg = err?.response?.data?.message
    errorMsg.value = msg || 'Something went wrong. Please try again.'
    state.value = 'idle'
  }
}

const goBack = () => {
  router.push({ path: '/', hash: '#sectionsigninlanding' })
}
</script>

<style scoped>
/* ── Page shell ─────────────────────────────────────────────────────────── */
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
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.18;
  pointer-events: none;
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

/* ── Card ───────────────────────────────────────────────────────────────── */
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

/* ── Brand ──────────────────────────────────────────────────────────────── */
.brand {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 36px;
}
.brand-name {
  font-size: 1.8rem;
  font-weight: 900;
  letter-spacing: 4px;
  color: #1C1917;
}
.brand-name .dot { color: #EA580C; }
.brand-sub {
  font-size: 0.65rem;
  letter-spacing: 3px;
  color: #A8A29E;
  margin-top: 2px;
}

/* ── Icon ───────────────────────────────────────────────────────────────── */
.icon-wrap {
  position: relative;
  width: 72px; height: 72px;
  margin: 0 auto 24px;
  display: flex; align-items: center; justify-content: center;
}
.icon-glow {
  position: absolute; inset: -8px;
  background: radial-gradient(circle, rgba(234,88,12,0.2) 0%, transparent 70%);
  border-radius: 50%;
}
.icon {
  width: 44px; height: 44px;
  color: #EA580C;
  position: relative; z-index: 1;
}

/* ── Headings ───────────────────────────────────────────────────────────── */
.card-title {
  font-family: 'Playfair Display', serif;
  font-size: 1.7rem;
  font-weight: 700;
  color: #1C1917;
  margin: 0 0 10px;
  text-align: center;
}
.card-sub {
  font-size: 0.9rem;
  color: #78716C;
  line-height: 1.6;
  text-align: center;
  margin: 0 0 28px;
}

/* ── Form ───────────────────────────────────────────────────────────────── */
.field-group { margin-bottom: 20px; }
.field-label {
  display: block;
  font-size: 0.78rem;
  font-weight: 700;
  color: #57534E;
  letter-spacing: 1px;
  text-transform: uppercase;
  margin-bottom: 8px;
}
.field-input {
  width: 100%;
  padding: 14px 16px;
  border: 1.5px solid #E7E5E4;
  border-radius: 12px;
  font-size: 0.95rem;
  color: #1C1917;
  background: #FAFAF9;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
  font-family: 'Mulish', sans-serif;
}
.field-input:focus {
  border-color: #EA580C;
  box-shadow: 0 0 0 3px rgba(234,88,12,0.12);
  background: #fff;
}
.field-input.input--error { border-color: #EF4444; }
.field-input:disabled { opacity: 0.5; cursor: not-allowed; }

/* ── Button ─────────────────────────────────────────────────────────────── */
.btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px 24px;
  background: #1C1917;
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  cursor: pointer;
  transition: background 0.2s, transform 0.15s;
}
.btn-primary:hover:not(:disabled) { background: #EA580C; }
.btn-primary:active:not(:disabled) { transform: translateY(1px); }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-full { width: 100%; }

/* ── Spinner ────────────────────────────────────────────────────────────── */
.spinner {
  width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── Error ──────────────────────────────────────────────────────────────── */
.error-msg {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #EF4444;
  font-size: 0.82rem;
  margin: 8px 0 0;
}
.err-icon { width: 14px; height: 14px; flex-shrink: 0; }

/* ── Footer links ───────────────────────────────────────────────────────── */
.footer-links {
  text-align: center;
  margin-top: 24px;
}
.link-btn {
  background: none;
  border: none;
  color: #EA580C;
  font-size: 0.88rem;
  font-weight: 700;
  cursor: pointer;
  letter-spacing: 0.3px;
}
.link-btn:hover { text-decoration: underline; }

/* ── Success Pane ───────────────────────────────────────────────────────── */
.success-pane {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 8px 0;
}
.success-ring {
  width: 80px; height: 80px;
  background: linear-gradient(135deg, #D1FAE5, #A7F3D0);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}
.checkmark { width: 52px; height: 52px; }
.checkmark__circle { stroke: #10B981; stroke-width: 2; stroke-dasharray: 166; stroke-dashoffset: 166; animation: stroke 0.6s ease forwards; }
.checkmark__check  { stroke: #10B981; stroke-width: 2; stroke-dasharray: 48; stroke-dashoffset: 48; animation: stroke 0.4s 0.5s ease forwards; }
@keyframes stroke { to { stroke-dashoffset: 0; } }

.s-title {
  font-family: 'Playfair Display', serif;
  font-size: 1.5rem;
  font-weight: 700;
  color: #1C1917;
  margin: 0 0 12px;
}
.s-sub {
  color: #78716C;
  font-size: 0.92rem;
  line-height: 1.6;
  margin: 0 0 28px;
}

/* ── Transitions ────────────────────────────────────────────────────────── */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.25s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(6px); }
.fade-slide-leave-to  { opacity: 0; transform: translateY(-6px); }

/* ── Mobile responsive ──────────────────────────────────────────────────── */
@media (max-width: 480px) {
  .auth-card {
    padding: 28px 20px;
    border-radius: 18px;
  }
  .card-title { font-size: 1.4rem; }
  .brand-name  { font-size: 1.5rem; }
}
</style> -->
