<template>
  <transition name="modal-motion">
    <div
      v-if="isOpen"
      ref="overlayRef"
      class="modal-overlay"
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
      tabindex="-1"
      @click="$emit('close')"
      @keydown.esc.prevent="$emit('close')"
    >
      <div class="modal-container-artistic" @click.stop>
        
        <div class="side-art-panel">
          <div class="art-overlay"></div>
          <img src="https://images.unsplash.com/photo-1559339352-11d035aa65de?q=80&w=1974&auto=format&fit=crop" alt="Luxury Gourmet" class="art-img ken-burns">
          
          <div class="art-content stagger-item" style="--delay: 0.2s">
            <div class="logo-area-art">
              <img :src="groupLogoUrl" alt="Logo" class="mini-logo">
              <span class="logo-text-art">GOMET.</span>
            </div>
            <div class="quote-wrap">
              <h3 class="quote-text" v-html="$t('auth.quote')"></h3>
              <div class="quote-decor"></div>
            </div>
          </div>
        </div>

        <div class="form-panel-interactive">
          <button class="btn-close-art" @click="$emit('close')">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>

          <div class="form-scroll-wrapper">
            <transition name="view-slide" mode="out-in">
              
              <div v-if="currentView === 'login'" :key="'login'" class="form-content-wrap">
                <div class="form-header stagger-item" style="--delay: 0.1s">
                  <h2 id="modal-title" class="art-title">{{ $t('auth.welcome_back') }}</h2>
                  <p class="art-desc">{{ $t('auth.welcome_sub') }}</p>
                </div>

                <div v-if="maintenanceMode" class="auth-error-msg auth-error-banned" style="margin-bottom: 12px;">
                  🔧 {{ maintenanceMessage }}
                </div>

                <form @submit.prevent="handleLogin" class="art-form">
                  <div class="input-field-art stagger-item" style="--delay: 0.2s">
                    <input v-model="email" type="email" id="login-email" required placeholder=" " />
                    <label for="login-email">{{ $t('auth.email') }}</label>
                    <span class="input-highlight"></span>
                  </div>

                  <div class="input-field-art stagger-item" style="--delay: 0.3s">
                    <input v-model="password" :type="showPassword ? 'text' : 'password'" id="login-pass" required placeholder=" " />
                    <label for="login-pass">{{ $t('auth.password') }}</label>
                    <button type="button" class="eye-toggle-btn" @click="showPassword = !showPassword">
                      <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                      <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
                    </button>
                    <span class="input-highlight"></span>
                  </div>

                  <div class="form-actions stagger-item" style="--delay: 0.4s">
                    <label class="remember-me">
                      <input type="checkbox"><span class="checkmark"></span> {{ $t('auth.remember') }}
                    </label>
                    <a href="#" class="forgot-link" @click.prevent="switchView('forgot-password')">{{ $t('auth.forgot') }}</a>
                  </div>

                  <div v-if="loginError" :class="['auth-error-msg', { 'auth-error-banned': isBannedError }]">
                    <span v-if="isBannedError">🔒 </span>{{ loginError }}
                  </div>

                  <button class="btn-submit-art stagger-item" style="--delay: 0.5s" :disabled="loginLoading || maintenanceMode">
                    <span>{{ loginLoading ? $t('common.loading') : $t('auth.sign_in_btn') }}</span>
                    <div class="btn-shine"></div>
                  </button>
                </form>

                <div class="social-section stagger-item" style="--delay: 0.6s">
                  <div class="divider"><span>{{ $t('common.or', 'Or') }}</span></div>
                  <button class="btn-google-art" @click="handleGoogleLogin" :disabled="googleLoading || maintenanceMode">
                    <img v-if="!googleLoading" src="https://cdn-icons-png.flaticon.com/512/2991/2991148.png" width="20">
                    <span v-if="googleLoading" class="btn-spinner" style="width:20px;height:20px;display:inline-block;"></span>
                    {{ googleLoading ? $t('common.loading') : $t('auth.login_google') }}
                  </button>
                </div>
                <p class="footer-prompt stagger-item" style="--delay: 0.7s">{{ $t('auth.new_here') }} <a href="#" @click.prevent="switchView('register')">{{ $t('auth.join_now') }}</a></p>
              </div>

              <div v-else-if="currentView === 'register'" :key="'register'" class="form-content-wrap">
                <div class="form-header stagger-item" style="--delay: 0.1s">
                  <h2 class="art-title">{{ $t('auth.create_account') }}</h2>
                  <p class="art-desc">{{ $t('auth.create_sub') }}</p>
                </div>

                <div v-if="maintenanceMode" class="auth-error-msg auth-error-banned" style="margin-bottom: 12px;">
                  🔧 {{ maintenanceMessage }}
                </div>

                <form @submit.prevent="handleRegisterRequest" class="art-form">
                  <div class="input-field-art stagger-item" style="--delay: 0.2s">
                    <input v-model="regForm.name" type="text" id="reg-name" required placeholder=" " />
                    <label for="reg-name">{{ $t('auth.fullname') }}</label>
                    <span class="input-highlight"></span>
                  </div>
                  <div class="input-field-art stagger-item" style="--delay: 0.3s">
                    <input v-model="regForm.email" type="email" id="reg-email" required placeholder=" " />
                    <label for="reg-email">{{ $t('auth.email_reg') }}</label>
                    <span class="input-highlight"></span>
                  </div>
                  <div class="input-row stagger-item" style="--delay: 0.4s">
                    <div class="input-field-art">
                      <input v-model="regForm.password" type="password" id="reg-pass" required placeholder=" " />
                      <label for="reg-pass">{{ $t('auth.password') }}</label>
                      <span class="input-highlight"></span>
                    </div>
                    <div class="input-field-art">
                      <input v-model="regForm.confirmPassword" type="password" id="reg-confirm" required placeholder=" " />
                      <label for="reg-confirm">{{ $t('auth.confirm_pw') }}</label>
                      <span class="input-highlight"></span>
                    </div>
                  </div>
                  <div v-if="regError" class="auth-error-msg">{{ regError }}</div>

                  <button class="btn-submit-art btn-orange stagger-item" :disabled="sendingOtp || maintenanceMode" style="--delay: 0.5s">
                    <span>{{ sendingOtp ? $t('auth.sending_otp') : $t('auth.register_btn') }}</span>
                    <div class="btn-shine"></div>
                  </button>
                </form>
                <p class="footer-prompt stagger-item" style="--delay: 0.6s">{{ $t('auth.have_account') }} <a href="#" @click.prevent="switchView('login')">{{ $t('auth.sign_in') }}</a></p>
              </div>

              <div v-else-if="currentView === 'otp'" :key="'otp'" class="form-content-wrap">
                 <div class="form-header stagger-item" style="--delay: 0.1s">
                  <h2 class="art-title">{{ $t('auth.otp_title') }}</h2>
                  <p class="art-desc">{{ $t('auth.otp_sub') }} <b>{{ regForm.email }}</b></p>
                  <p class="art-desc" style="margin-top:6px;font-size:0.85rem;">{{ $t('auth.otp_check') }}</p>
                </div>
                <form @submit.prevent="handleOtpVerify" class="art-form">
                  <div class="otp-group stagger-item" style="--delay: 0.2s">
                    <input
                      v-for="(n, i) in 6"
                      :key="i"
                      v-model="otpDigits[i]"
                      type="text"
                      inputmode="numeric"
                      pattern="[0-9]*"
                      autocomplete="one-time-code"
                      maxlength="1"
                      class="otp-input"
                      :aria-label="`OTP digit ${i + 1}`"
                      @input="focusNext($event, i)"
                    >
                  </div>
                  <div v-if="otpError" class="auth-error-msg">{{ otpError }}</div>
                  <button class="btn-submit-art btn-orange stagger-item" style="--delay: 0.3s">
                    <span>{{ $t('auth.otp_verify_btn') }}</span>
                  </button>
                </form>
                <button class="btn-back stagger-item" style="--delay: 0.4s" @click="switchView('register')">{{ $t('auth.otp_back') }}</button>
              </div>

              <!-- ── FORGOT PASSWORD VIEW ─────────────────────────────── -->
              <div v-else-if="currentView === 'forgot-password'" :key="'forgot'" class="form-content-wrap">
                <!-- Sent state -->
                <div v-if="forgotState === 'sent'" class="forgot-success stagger-item" style="--delay: 0.1s">
                  <div class="forgot-icon-ring">
                    <svg viewBox="0 0 52 52" class="check-anim" xmlns="http://www.w3.org/2000/svg">
                      <circle class="check-circle" cx="26" cy="26" r="25" fill="none"/>
                      <path class="check-path" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
                    </svg>
                  </div>
                  <h2 class="art-title" style="margin-top:16px">{{ $t('auth.forgot_sent_title') }}</h2>
                  <p class="art-desc">{{ $t('auth.forgot_sent_sub') }}</p>
                  <button class="btn-submit-art" @click="switchView('login')" style="margin-top: 12px">
                    <span>{{ $t('auth.forgot_back_login') }}</span>
                    <div class="btn-shine"></div>
                  </button>
                </div>

                <!-- Form state -->
                <div v-else>
                  <div class="form-header stagger-item" style="--delay: 0.1s">
                    <h2 class="art-title">{{ $t('auth.forgot_title') }}</h2>
                    <p class="art-desc">{{ $t('auth.forgot_sub') }}</p>
                  </div>

                  <form @submit.prevent="handleForgotPassword" class="art-form">
                    <div class="input-field-art stagger-item" style="--delay: 0.2s">
                      <input
                        v-model="forgotIdentifier"
                        type="text"
                        id="forgot-id"
                        required
                        placeholder=" "
                        autocomplete="username"
                        :disabled="forgotState === 'loading'"
                      />
                      <label for="forgot-id">{{ $t('auth.forgot_identifier') }}</label>
                      <span class="input-highlight"></span>
                    </div>

                    <div v-if="forgotError" class="auth-error-msg">{{ forgotError }}</div>

                    <button
                      class="btn-submit-art stagger-item"
                      style="--delay: 0.3s"
                      :disabled="!forgotIdentifier.trim() || forgotState === 'loading'"
                    >
                      <span v-if="forgotState === 'loading'" class="btn-spinner"></span>
                      <span>{{ forgotState === 'loading' ? $t('auth.forgot_sending') : $t('auth.forgot_btn') }}</span>
                      <div class="btn-shine"></div>
                    </button>
                  </form>

                  <button class="btn-back stagger-item" style="margin-top: 16px; --delay: 0.4s" @click="switchView('login')">
                    ← {{ $t('auth.forgot_back') }}
                  </button>
                </div>
              </div>

            </transition>
          </div>
        </div>
      </div>
    </div>
  </transition>

  <!-- MFA Challenge — shown on top when login requires 2FA -->
  <Teleport to="body">
    <transition name="mfa-fade" appear>
      <MfaChallengeModal
        v-if="showMfaModal"
        @success="onMfaSuccess"
        @cancel="onMfaCancel"
      />
    </transition>
  </Teleport>
</template>

<script setup>
import { ref, reactive, watch, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'
import logoGroup from '@/assets/images/gomet.jpg'
import { toast } from '@/composables/useToast'
import * as authService from '@/services/authService'
import { forgotPassword } from '@/services/authService'
import MfaChallengeModal from '@/components/modals/MfaChallengeModal.vue'
import { getDeviceContext } from '@/services/deviceContext'
import { useSystemSettingsStore } from '@/stores/systemSettings'

const { t } = useI18n()
const props = defineProps({ initialView: { type: String, default: 'login' } })
const emit = defineEmits(['close'])

const isOpen = ref(false)
const overlayRef = ref(null)
const groupLogoUrl = logoGroup
const currentView = ref(props.initialView)
const showPassword = ref(false)
const otpDigits = ref(['', '', '', '', '', ''])
const sendingOtp = ref(false)

const authStore = useAuthStore()
const systemSettingsStore = useSystemSettingsStore()
const router = useRouter()

const email = ref('')
const password = ref('')
const loginLoading = ref(false)
const loginError = ref('')
const regError = ref('')
const otpError = ref('')
const regForm = reactive({ name: '', email: '', password: '', confirmPassword: '' })

const forgotIdentifier = ref('')
const forgotState      = ref('idle')   // 'idle' | 'loading' | 'sent'
const forgotError      = ref('')
const googleLoading    = ref(false)
const maintenanceMode = computed(() => systemSettingsStore.maintenanceMode)
const maintenanceMessage = computed(() => systemSettingsStore.message || 'Hệ thống đang bảo trì. Vui lòng quay lại sau.')

// ── MFA challenge ──────────────────────────────────────────────────────────────
const showMfaModal = ref(false)
const hasAutoShownMfa = ref(false)

watch(() => props.initialView, (val) => { currentView.value = val })
const switchView = (name) => {
  currentView.value = name
  loginError.value = ''
  regError.value = ''
  otpError.value = ''
  forgotError.value = ''
  if (name !== 'forgot-password') {
    forgotState.value = 'idle'
    forgotIdentifier.value = ''
  }
}

const isBannedError = computed(() => loginError.value === 'Tài khoản của bạn đã bị khóa bởi quản trị viên.')

const handleLogin = async () => {
  if (maintenanceMode.value) {
    loginError.value = maintenanceMessage.value
    return
  }
  if (loginLoading.value) return
  loginError.value = ''
  loginLoading.value = true
  try {
    const { deviceId, deviceName } = getDeviceContext()
    const result = await authStore.login(email.value, password.value, deviceId, deviceName)
    if (result.status === 'mfa') {
      showMfaModal.value = true
      return
    }
    if (result.status === 'device') {
      toast.info('Đã gửi link xác minh thiết bị qua email. Vui lòng kiểm tra hộp thư.')
      return
    }
    // status === 'ok'
    toast.success(t('toast.login_ok'))
    emit('close')
    router.push(result.role === 'admin' ? '/admin' : '/home')
  } catch (err) {
    const raw = err.message || ''
    if (raw === 'ACCOUNT_BANNED') {
      loginError.value = 'T\u00e0i kho\u1ea3n c\u1ee7a b\u1ea1n \u0111\u00e3 b\u1ecb kh\u00f3a b\u1edfi qu\u1ea3n tr\u1ecb vi\u00ean.'
    } else {
      loginError.value = raw || t('auth.error_login')
    }
  } finally {
    loginLoading.value = false
  }
}

const onMfaSuccess = (result) => {
  showMfaModal.value = false
  toast.success(t('toast.login_ok'))
  emit('close')
  router.push(result?.role === 'admin' ? '/admin' : '/home')
}

const onMfaCancel = () => {
  showMfaModal.value = false
  hasAutoShownMfa.value = false
  authStore.clearPendingMfa()
}

const handleRegisterRequest = async () => {
  if (maintenanceMode.value) {
    regError.value = maintenanceMessage.value
    return
  }
  regError.value = ''
  if (!regForm.name || !regForm.email || !regForm.password) {
    regError.value = t('auth.error_required')
    return
  }
  if (regForm.password !== regForm.confirmPassword) {
    regError.value = t('auth.error_pw_match')
    return
  }
  sendingOtp.value = true
  try {
    await authService.sendOtp(regForm.name, regForm.email, regForm.password)
    toast.success(t('toast.otp_sent'))
    otpDigits.value = ['', '', '', '', '', '']
    currentView.value = 'otp'
  } catch (err) {
    regError.value = err.response?.data?.message || t('toast.error_generic')
  } finally {
    sendingOtp.value = false
  }
}

const handleOtpVerify = async () => {
  otpError.value = ''
  const code = otpDigits.value.join('')
  if (code.length < 6) {
    toast.warn(t('auth.error_otp'))
    return
  }
  try {
    const data = await authService.verifyOtp(regForm.email, code)
    authStore.setAuthFromResponse(data)
    toast.success(t('toast.register_ok'))
    emit('close')
    router.push('/home')
  } catch (err) {
    otpError.value = err.response?.data?.message || t('auth.error_otp')
  }
}

const focusNext = (e, index) => {
  if (e.target.value && index < 5) e.target.parentElement.children[index + 1].focus()
}

const handleForgotPassword = async () => {
  forgotError.value = ''
  if (!forgotIdentifier.value.trim()) return
  forgotState.value = 'loading'
  try {
    await forgotPassword(forgotIdentifier.value.trim())
    forgotState.value = 'sent'
  } catch (err) {
    const msg = err?.response?.data?.message
    forgotError.value = msg || 'Something went wrong. Please try again.'
    forgotState.value = 'idle'
  }
}

const handleGoogleLogin = async () => {
  if (maintenanceMode.value) {
    loginError.value = maintenanceMessage.value
    return
  }
  if (googleLoading.value) return
  loginError.value = ''
  googleLoading.value = true
  try {
    const { deviceId } = getDeviceContext()
    const result = await authStore.loginGoogle(deviceId)
    toast.success(t('toast.login_ok'))
    emit('close')
    router.push(result?.role === 'admin' ? '/admin' : '/home')
  } catch (err) {
    loginError.value = err.message || t('auth.error_login')
  } finally {
    googleLoading.value = false
  }
}

watch(
  () => [isOpen.value, currentView.value, authStore.isMfaPending],
  ([open, view, pending]) => {
    if (!open || view !== 'login' || !pending) return
    if (hasAutoShownMfa.value || showMfaModal.value) return
    hasAutoShownMfa.value = true
    showMfaModal.value = true
    toast.info('Vui lòng hoàn tất xác thực 2 lớp để tiếp tục.')
  },
  { immediate: true }
)

onMounted(() => {
  systemSettingsStore.fetchPublicSettings().catch(() => {})
  isOpen.value = true
  document.body.style.overflow = 'hidden'
  nextTick(() => { overlayRef.value?.focus() })
})

onUnmounted(() => {
  document.body.style.overflow = ''
})
</script>

<style scoped lang="scss" src="./AuthModal.scss"></style>
