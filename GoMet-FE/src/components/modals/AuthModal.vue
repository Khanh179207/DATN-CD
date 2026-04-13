<template>
  <transition name="modal-motion">
    <div
      v-if="isOpen"
      ref="overlayRef"
      class="modal-overlay premium-modal-overlay"
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
      tabindex="-1"
      @click="$emit('close')"
      @keydown.esc.prevent="$emit('close')"
    >
      <div class="modal-container-artistic glass-panel" @click.stop>
        
        <div class="form-panel-interactive">
          <div class="bg-visuals">
            <div class="orb orb-1 gsap-orb"></div>
            <div class="orb orb-2 gsap-orb"></div>
          </div>

          <button class="btn-close-art" @click="$emit('close')">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>

          <div class="form-scroll-wrapper">
            <transition name="view-slide" mode="out-in">
              
              <div v-if="currentView === 'login'" :key="'login'" class="form-content-wrap">
                <div class="form-header stagger-item" style="--delay: 0.1s">
                  <div class="brand-header">
                    <img :src="groupLogoUrl" :alt="$t('common.gomet_logo')" class="brand-logo">
                    <span class="brand-text">GOMET</span>
                    <div class="logo-dot"></div>
                  </div>
                  <h2 id="modal-title" class="art-title">{{ $t('auth.welcome_back') }}</h2>
                  <p class="art-desc">{{ $t('auth.welcome_sub') }}</p>
                </div>

                <form @submit.prevent="handleLogin" class="art-form">
                  <div class="input-field-art stagger-item" style="--delay: 0.2s">
                    <label for="login-email">{{ $t('auth.email') }}</label>
                    <input v-model="email" type="email" id="login-email" required :placeholder="$t('auth.forgot_email_placeholder')" />
                  </div>

                  <div class="input-field-art stagger-item" style="--delay: 0.3s">
                    <label for="login-pass">{{ $t('auth.password') }}</label>
                    <div class="password-wrapper">
                      <input v-model="password" :type="showPassword ? 'text' : 'password'" id="login-pass" required :placeholder="$t('auth.login_password_placeholder')" />
                      <button type="button" class="eye-toggle-btn" @click="showPassword = !showPassword">
                        <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                        <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
                      </button>
                    </div>
                  </div>

                  <div class="form-actions stagger-item" style="--delay: 0.4s">
                    <label class="remember-me">
                      <input type="checkbox"><span class="checkmark"></span> {{ $t('auth.remember') }}
                    </label>
                    <a href="#" class="forgot-link" @click.prevent="switchView('forgot-password')">{{ $t('auth.forgot') }}</a>
                  </div>

                  <!-- BANNED ALERT BOX LUXURY -->
                  <transition name="fade-slide" mode="out-in">
                    <div v-if="isBannedError" key="banned-box" class="banned-alert-box stagger-item" style="--delay: 0.45s">
                      <button type="button" class="btn-close-alert" @click="closeBannedAlert" :title="$t('common.close')">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                      </button>
                      <div class="banned-header">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                        <h4>{{ $t('auth.banned_title') }}</h4>
                      </div>
                      <div class="banned-body">
                        <template v-if="bannedDetails">
                          <div class="b-row" v-if="bannedDetails.time"><span class="b-lbl">{{ $t('auth.banned_time') }}:</span> <span class="b-val">{{ bannedDetails.time }}</span></div>
                          <div class="b-row"><span class="b-lbl">{{ $t('auth.banned_reason') }}:</span> <span class="b-val reason-text">{{ bannedDetails.reason }}</span></div>
                        </template>
                        <template v-else>
                          <p class="banned-msg">{{ loginError }}</p>
                        </template>
                      </div>
                      <div class="banned-footer">
                        <button type="button" class="btn-appeal-lux" @click="openAppealAction">
                          {{ $t('auth.banned_cta_label') }} <span>{{ $t('auth.banned_cta_action') }}</span>
                        </button>
                      </div>
                    </div>

                    <div v-else-if="loginError" key="error-box" class="auth-error-msg stagger-item" style="--delay: 0.45s">
                      {{ loginError }}
                    </div>
                  </transition>
                  <button class="btn-submit-art stagger-item" style="--delay: 0.5s">
                    <span>{{ $t('auth.sign_in_btn') }}</span>
                  </button>
                </form>

                <div class="social-section stagger-item" style="--delay: 0.6s">
                  <div class="divider"><span>{{ $t('common.or') }}</span></div>
                  
                  <div class="social-buttons custom-google-wrapper">
                    <button type="button" class="btn-google-art custom-ui-btn">
                      <img src="https://cdn-icons-png.flaticon.com/512/2991/2991148.png" width="20" :alt="$t('common.google_logo')"> 
                      {{ $t('auth.login_google') }}
                    </button>
                    <div class="invisible-google-btn">
                      <GoogleLogin :callback="handleGoogleCallback" prompt />
                    </div>
                  </div>
                </div>
                
                <p class="footer-prompt stagger-item" style="--delay: 0.7s">{{ $t('auth.new_here') }} <a href="#" @click.prevent="switchView('register')">{{ $t('auth.join_now') }}</a></p>
              </div>

              <div v-else-if="currentView === 'register'" :key="'register'" class="form-content-wrap">
                <div class="form-header stagger-item" style="--delay: 0.1s">
                  <div class="brand-header">
                    <img :src="groupLogoUrl" :alt="$t('common.gomet_logo')" class="brand-logo">
                    <span class="brand-text">GOMET</span>
                    <div class="logo-dot"></div>
                  </div>
                  <h2 class="art-title">{{ $t('auth.create_account') }}</h2>
                </div>

                <form @submit.prevent="handleRegisterRequest" class="art-form">
                  <div class="input-field-art stagger-item" style="--delay: 0.2s">
                    <label for="reg-name">{{ $t('auth.fullname') }}</label>
                    <input v-model="regForm.name" type="text" id="reg-name" required :placeholder="$t('auth.register_name_placeholder')" />
                  </div>
                  <div class="input-field-art stagger-item" style="--delay: 0.3s">
                    <label for="reg-email">{{ $t('auth.email_reg') }}</label>
                    <input v-model="regForm.email" type="email" id="reg-email" required :placeholder="$t('auth.forgot_email_placeholder')" />
                  </div>
                  <div class="input-row stagger-item" style="--delay: 0.4s">
                    <div class="input-field-art">
                      <label for="reg-pass">{{ $t('auth.password') }}</label>
                      <input v-model="regForm.password" type="password" id="reg-pass" required :placeholder="$t('auth.register_password_placeholder')" />
                    </div>
                    <div class="input-field-art">
                      <label for="reg-confirm">{{ $t('auth.confirm_pw') }}</label>
                      <input v-model="regForm.confirmPassword" type="password" id="reg-confirm" required :placeholder="$t('auth.register_confirm_placeholder')" />
                    </div>
                  </div>

                  <div class="form-actions stagger-item" style="--delay: 0.45s; justify-content: flex-start; margin-top: 10px; margin-bottom: 20px;">
                    <label class="remember-me" style="font-size: 0.85rem; width: 100%; white-space: normal; display: flex; align-items: flex-start;">
                      <input type="checkbox" v-model="regForm.agreeTerms" required>
                      <span class="checkmark" style="margin-top: 2px;"></span>
                      <span style="margin-left: 28px; line-height: 1.5; color: #64748b;">
                        {{ $t('auth.agree_terms') }} <router-link to="/terms-and-policy" @click="$emit('close')" style="color: #ea580c; text-decoration: none; font-weight: 700;">{{ $t('auth.terms') }}</router-link> và <router-link to="/terms-and-policy" @click="$emit('close')" style="color: #ea580c; text-decoration: none; font-weight: 700;">{{ $t('auth.privacy_policy') }}</router-link>
                      </span>
                    </label>
                  </div>

                  <div v-if="regError" class="auth-error-msg">{{ regError }}</div>

                  <button class="btn-submit-art stagger-item" :disabled="sendingOtp || !regForm.agreeTerms" style="--delay: 0.5s">
                    <span v-if="sendingOtp" class="spinner-border" role="status" aria-hidden="true"></span>
                    <span v-else>{{ $t('auth.register_btn') }}</span>
                  </button>
                </form>
                
                <div class="social-section stagger-item" style="--delay: 0.55s">
                  <div class="divider"><span>{{ $t('common.or') }}</span></div>
                  
                  <div class="social-buttons custom-google-wrapper">
                    <button type="button" class="btn-google-art custom-ui-btn">
                      <img src="https://cdn-icons-png.flaticon.com/512/2991/2991148.png" width="20" :alt="$t('common.google_logo')"> 
                      {{ $t('auth.register_google') }}
                    </button>
                    <div class="invisible-google-btn">
                      <GoogleLogin :callback="handleGoogleCallback" prompt />
                    </div>
                  </div>
                </div>

                <p class="footer-prompt stagger-item" style="--delay: 0.6s">{{ $t('auth.have_account') }} <a href="#" @click.prevent="switchView('login')">{{ $t('auth.sign_in') }}</a></p>
              </div>

              <div v-else-if="currentView === 'otp'" :key="'otp'" class="form-content-wrap">
                 <div class="form-header stagger-item" style="--delay: 0.1s">
                  <div class="brand-header">
                    <img :src="groupLogoUrl" :alt="$t('common.gomet_logo')" class="brand-logo">
                    <span class="brand-text">GOMET</span>
                    <div class="logo-dot"></div>
                  </div>
                  <h2 class="art-title">{{ $t('auth.otp_title') }}</h2>
                  <p class="art-desc">{{ $t('auth.otp_sub') }} <br><b>{{ regForm.email }}</b></p>
                  <p class="art-desc" style="margin-top:6px;font-size:0.85rem;">{{ $t('auth.otp_check') }}</p>
                </div>
                <form @submit.prevent="handleOtpVerify" class="art-form">
                  <div class="otp-group stagger-item" style="--delay: 0.2s">
                    <input v-for="(n, i) in 6" :key="i" v-model="otpDigits[i]" type="text" inputmode="numeric" pattern="[0-9]*" autocomplete="one-time-code" maxlength="1" class="otp-input" @input="focusNext($event, i)">
                  </div>
                  <div v-if="otpError" class="auth-error-msg">{{ otpError }}</div>
                  <button class="btn-submit-art stagger-item" style="--delay: 0.3s">
                    <span>{{ $t('auth.otp_verify_btn') }}</span>
                  </button>
                </form>
                <button class="btn-back stagger-item" style="--delay: 0.4s" @click="switchView('register')">{{ $t('auth.otp_back') }}</button>
              </div>

              <div v-else-if="currentView === 'forgot-password'" :key="'forgot'" class="form-content-wrap">
                <div v-if="forgotState === 'sent'" class="forgot-success stagger-item" style="--delay: 0.1s">
                  <h2 class="art-title" style="margin-top:16px">{{ $t('auth.forgot_sent_title') }}</h2>
                  <button class="btn-submit-art" @click="switchView('login')" style="margin-top: 12px">
                    <span>{{ $t('auth.forgot_back_login') }}</span>
                  </button>
                </div>
                <div v-else>
                  <div class="form-header stagger-item" style="--delay: 0.1s">
                    <div class="brand-header">
                      <img :src="groupLogoUrl" :alt="$t('common.gomet_logo')" class="brand-logo">
                      <span class="brand-text">GOMET</span>
                      <div class="logo-dot"></div>
                    </div>
                    <h2 class="art-title">{{ $t('auth.forgot_title') }}</h2>
                    <p class="art-desc">{{ $t('auth.forgot_sub') }}</p>
                  </div>
                  <form @submit.prevent="handleForgotPassword" class="art-form">
                    <div class="input-field-art stagger-item" style="--delay: 0.2s">
                      <label for="forgot-id">{{ $t('auth.forgot_identifier') }}</label>
                      <input v-model="forgotIdentifier" type="email" id="forgot-id" required :placeholder="$t('auth.forgot_email_placeholder')" :disabled="forgotState === 'loading'" />
                    </div>
                    <div v-if="forgotError" class="auth-error-msg">{{ forgotError }}</div>
                    <button class="btn-submit-art stagger-item" style="--delay: 0.3s" :disabled="!forgotIdentifier.trim() || forgotState === 'loading'">
                      <span v-if="forgotState === 'loading'" class="spinner-border"></span>
                      <span v-else>{{ $t('auth.forgot_btn') }}</span>
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

  <!-- Appeal Modal Teleport -->
  <Teleport to="body">
    <AppealModal v-if="showAppealModal" @close="handleAppealClose" />
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
import { GoogleLogin } from 'vue3-google-login'
import AppealModal from '@/components/modals/AppealModal.vue'
import { formatLocaleDateTime } from '@/i18n'

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
const showAppealModal = ref(false)

const authStore = useAuthStore()
const router = useRouter()

const email = ref('')
const password = ref('')
const loginError = ref('')
const regError = ref('')
const otpError = ref('')
const wrongPasswordCount = ref(0) // Counter cho lần nhập sai mật khẩu
const bannedDetails = ref(null) // Lưu trữ đối tượng thông tin ban

// 🌟 ĐÃ CẬP NHẬT regForm ĐỂ THÊM agreeTerms 🌟
const regForm = reactive({ 
  name: '', 
  email: '', 
  password: '', 
  confirmPassword: '',
  agreeTerms: false 
})

const forgotIdentifier = ref('')
const forgotState      = ref('idle')
const forgotError      = ref('')

watch(() => props.initialView, (val) => { currentView.value = val })

// Debug watch for wrongPasswordCount
watch(() => wrongPasswordCount.value, (newVal) => {
  console.log('[Watch] wrongPasswordCount updated:', { newVal, shouldShowAppeal: newVal >= 3 })
})

const switchView = (name) => {
  currentView.value = name
  loginError.value = ''
  regError.value = ''
  otpError.value = ''
  forgotError.value = ''
  bannedDetails.value = null
  // ❌ KHÔNG reset wrongPasswordCount ở đây - nó phải persist khi user back to login
  // wrongPasswordCount.value = 0
  if (name !== 'forgot-password') {
    forgotState.value = 'idle'
    forgotIdentifier.value = ''
  }
}

const openAppealAction = () => {
  isOpen.value = false // Ẩn giao diện AuthModal hiện tại mượt mà
  showAppealModal.value = true // Gọi AppealModal xuất hiện
}

const handleAppealClose = () => {
  showAppealModal.value = false
  emit('close') // Đóng Modal khiếu nại xong thì phát lệnh cho component cha đóng hẳn AuthModal
}

const isBannedError = computed(() => Boolean(bannedDetails.value))

const handleLogin = async () => {
  loginError.value = ''
  try {
    const role = await authStore.login(email.value, password.value)
    toast.success(t('toast.login_ok'))
    wrongPasswordCount.value = 0 
    emit('close')
    router.push(role === 'admin' ? '/admin' : '/home')
  } catch (err) {
    // 🔥 Lấy data trực tiếp từ cục lỗi Axios gốc
    const errData = err.response?.data || {}
    const errorMessage = errData.message || err.message || String(err)
    const errorString = errorMessage.toUpperCase()

    console.log('[Login Error]', { errorMessage, errorString, errData })

    if (errorString.includes('ACCOUNT_BANNED')) {
      const banReason = errData.banReason || errData.reason || t('auth.banned_reason_default');
      let rawTimeStr = '';
      
      if (errData.bannedAt) {
         rawTimeStr = formatLocaleDateTime(errData.bannedAt);
      }

      bannedDetails.value = { reason: banReason, time: rawTimeStr };
      loginError.value = rawTimeStr
        ? `${t('auth.account_locked')} (${rawTimeStr}). ${t('auth.banned_reason')}: ${banReason}`
        : `${t('auth.account_locked')}. ${t('auth.banned_reason')}: ${banReason}`;
      toast.error(loginError.value)
    } else if (errorString.includes('INCORRECT') || errorString.includes('PASSWORD') || errorString.includes('CREDENTIALS')) {
      wrongPasswordCount.value++
      loginError.value = t('auth.incorrect_password')
      toast.error(`${loginError.value} (${wrongPasswordCount.value}/3)`)
    } else {
      loginError.value = t('auth.error_login')
      toast.error(loginError.value)
    }
  }
}
const handleGoogleCallback = async (response) => {
  loginError.value = ''
  try {
    if (!response || !response.credential) {
      throw new Error(t('auth.google_token_missing'))
    }
    const idToken = response.credential
    const data = await authService.googleLogin(idToken)
    authStore.setUser(data)
    toast.success(t('toast.login_ok'))
    emit('close')
    router.push(authStore.user.role === 'admin' ? '/admin' : '/home')
  } catch (err) {
    console.error("Google Login Error:", err)
    
    // 🔥 CHẶN ĐỨNG LỖI TỪ GOOGLE
    const errorString = String(err.response?.data?.message || err.message || err).toUpperCase()

    if (errorString.includes('ACCOUNT_BANNED') || errorString.includes('BANNED')) {
      const banReason = err.response?.data?.banReason || err.response?.data?.reason || t('auth.banned_reason_default');
      let rawTimeStr = '';
      if (err.response?.data?.bannedAt) {
         rawTimeStr = formatLocaleDateTime(err.response.data.bannedAt);
      }
      
      bannedDetails.value = { reason: banReason, time: rawTimeStr };
      loginError.value = rawTimeStr
        ? `${t('auth.account_locked')} (${rawTimeStr}). ${t('auth.banned_reason')}: ${banReason}`
        : `${t('auth.account_locked')}. ${t('auth.banned_reason')}: ${banReason}`
      toast.error(loginError.value)
    } else {
      loginError.value = t('auth.google_login_error')
      toast.error(loginError.value)
    }
  }
}

const handleRegisterRequest = async () => {
  regError.value = ''
  
  if (!regForm.agreeTerms) {
    regError.value = t('auth.agree_terms_required')
    toast.warn(regError.value)
    return
  }

  if (!regForm.name || !regForm.email || !regForm.password) {
    regError.value = t('auth.error_required')
    toast.warn(regError.value)
    return
  }
  if (regForm.password !== regForm.confirmPassword) {
    regError.value = t('auth.error_pw_match')
    toast.warn(regError.value)
    return
  }
  sendingOtp.value = true
  try {
    await authService.sendOtp(regForm.name, regForm.email, regForm.password)
    toast.success(t('toast.otp_sent'))
    otpDigits.value = ['', '', '', '', '', '']
    currentView.value = 'otp'
  } catch (err) {
    regError.value = t('toast.error_generic')
    toast.error(regError.value)
  } finally {
    sendingOtp.value = false
  }
}

const handleOtpVerify = async () => {
  otpError.value = ''
  const code = otpDigits.value.join('')
  if (code.length < 6) {
    const msg = t('auth.otp_incomplete')
    toast.warn(msg)
    return
  }
  try {
    const data = await authService.verifyOtp(regForm.email, code)
    authStore.setUser(data)
    toast.success(t('toast.register_ok'))
    emit('close')
    router.push('/home')
  } catch (err) {
    otpError.value = t('auth.error_otp')
    toast.error(otpError.value)
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
    toast.success(t('auth.forgot_sent_title'))
  } catch (err) {
    forgotState.value = 'idle'
    forgotError.value = t('auth.forgot_generic_error')
    toast.error(forgotError.value)
  }
}

const handleBannedPrompt = (e) => {
  isOpen.value = true
  currentView.value = 'login'
  loginError.value = e.detail?.msg || t('auth.account_locked')
  if (e.detail?.details) {
    bannedDetails.value = e.detail.details
  }
}

onMounted(() => {
  isOpen.value = true
  document.body.style.overflow = 'hidden'
  nextTick(() => { overlayRef.value?.focus() })
  window.addEventListener('auth:banned-login-prompt', handleBannedPrompt)
})

onUnmounted(() => {
  document.body.style.overflow = ''
  // Reset counter khi modal đóng
  wrongPasswordCount.value = 0
  window.removeEventListener('auth:banned-login-prompt', handleBannedPrompt)
})
</script>

<style scoped lang="scss" src="./AuthModal.scss"></style>
