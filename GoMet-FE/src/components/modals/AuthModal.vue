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
                    <img :src="groupLogoUrl" alt="GoMet Logo" class="brand-logo">
                    <span class="brand-text">GOMET</span>
                    <div class="logo-dot"></div>
                  </div>
                  <h2 id="modal-title" class="art-title">{{ $t('auth.welcome_back', 'Chào mừng trở lại!') }}</h2>
                  <p class="art-desc">{{ $t('auth.welcome_sub', 'Vui lòng đăng nhập để tiếp tục') }}</p>
                </div>

                <form @submit.prevent="handleLogin" class="art-form">
                  <div class="input-field-art stagger-item" style="--delay: 0.2s">
                    <label for="login-email">{{ $t('auth.email', 'Email') }}</label>
                    <input v-model="email" type="email" id="login-email" required placeholder="name@example.com" />
                  </div>

                  <div class="input-field-art stagger-item" style="--delay: 0.3s">
                    <label for="login-pass">{{ $t('auth.password', 'Mật khẩu') }}</label>
                    <div class="password-wrapper">
                      <input v-model="password" :type="showPassword ? 'text' : 'password'" id="login-pass" required placeholder="Nhập mật khẩu..." />
                      <button type="button" class="eye-toggle-btn" @click="showPassword = !showPassword">
                        <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                        <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
                      </button>
                    </div>
                  </div>

                  <div class="form-actions stagger-item" style="--delay: 0.4s">
                    <label class="remember-me">
                      <input type="checkbox"><span class="checkmark"></span> {{ $t('auth.remember', 'Ghi nhớ tôi') }}
                    </label>
                    <a href="#" class="forgot-link" @click.prevent="switchView('forgot-password')">{{ $t('auth.forgot', 'Quên mật khẩu?') }}</a>
                  </div>

                  <div v-if="loginError" :class="['auth-error-msg', { 'auth-error-banned': isBannedError }]">
                    <span v-if="isBannedError">🔒 </span>{{ loginError }}
                  </div>

                  <button class="btn-submit-art stagger-item" style="--delay: 0.5s">
                    <span>{{ $t('auth.sign_in_btn', 'Đăng Nhập Ngay') }}</span>
                  </button>
                </form>

                <div class="social-section stagger-item" style="--delay: 0.6s">
                  <div class="divider"><span>{{ $t('common.or', 'Hoặc tiếp tục với') }}</span></div>
                  
                  <div class="social-buttons custom-google-wrapper">
                    <button type="button" class="btn-google-art custom-ui-btn">
                      <img src="https://cdn-icons-png.flaticon.com/512/2991/2991148.png" width="20" alt="Google"> 
                      {{ $t('auth.login_google', 'Google') }}
                    </button>
                    <div class="invisible-google-btn">
                      <GoogleLogin :callback="handleGoogleCallback" prompt />
                    </div>
                  </div>
                </div>
                
                <p class="footer-prompt stagger-item" style="--delay: 0.7s">{{ $t('auth.new_here', 'Chưa có tài khoản?') }} <a href="#" @click.prevent="switchView('register')">{{ $t('auth.join_now', 'Đăng ký ngay') }}</a></p>
              </div>

              <div v-else-if="currentView === 'register'" :key="'register'" class="form-content-wrap">
                <div class="form-header stagger-item" style="--delay: 0.1s">
                  <div class="brand-header">
                    <img :src="groupLogoUrl" alt="GoMet Logo" class="brand-logo">
                    <span class="brand-text">GOMET</span>
                    <div class="logo-dot"></div>
                  </div>
                  <h2 class="art-title">{{ $t('auth.create_account', 'Tạo tài khoản mới') }}</h2>
                </div>

                <form @submit.prevent="handleRegisterRequest" class="art-form">
                  <div class="input-field-art stagger-item" style="--delay: 0.2s">
                    <label for="reg-name">{{ $t('auth.fullname', 'Tên đăng nhập') }}</label>
                    <input v-model="regForm.name" type="text" id="reg-name" required placeholder="VD: masterchef_vn" />
                  </div>
                  <div class="input-field-art stagger-item" style="--delay: 0.3s">
                    <label for="reg-email">{{ $t('auth.email_reg', 'Email') }}</label>
                    <input v-model="regForm.email" type="email" id="reg-email" required placeholder="name@example.com" />
                  </div>
                  <div class="input-row stagger-item" style="--delay: 0.4s">
                    <div class="input-field-art">
                      <label for="reg-pass">{{ $t('auth.password', 'Mật khẩu') }}</label>
                      <input v-model="regForm.password" type="password" id="reg-pass" required placeholder="Tối thiểu 6 ký tự" />
                    </div>
                    <div class="input-field-art">
                      <label for="reg-confirm">{{ $t('auth.confirm_pw', 'Xác nhận mật khẩu') }}</label>
                      <input v-model="regForm.confirmPassword" type="password" id="reg-confirm" required placeholder="Nhập lại mật khẩu" />
                    </div>
                  </div>

                  <div class="input-group-checkbox stagger-item" style="--delay: 0.45s">
                    <label class="checkbox-container">
                      <input type="checkbox" v-model="regForm.agreeTerms" required>
                      <span class="checkmark"></span>
                      <span class="label-text">
                        Tôi đồng ý với <router-link to="/terms" @click="$emit('close')">Điều khoản</router-link> và <router-link to="/policy" @click="$emit('close')">Chính sách bảo mật</router-link>
                      </span>
                    </label>
                  </div>

                  <div v-if="regError" class="auth-error-msg">{{ regError }}</div>

                  <button class="btn-submit-art stagger-item" :disabled="sendingOtp || !regForm.agreeTerms" style="--delay: 0.5s">
                    <span v-if="sendingOtp" class="spinner-border" role="status" aria-hidden="true"></span>
                    <span v-else>{{ $t('auth.register_btn', 'Đăng Ký Miễn Phí') }}</span>
                  </button>
                </form>
                
                <div class="social-section stagger-item" style="--delay: 0.55s">
                  <div class="divider"><span>{{ $t('common.or', 'Hoặc tiếp tục với') }}</span></div>
                  
                  <div class="social-buttons custom-google-wrapper">
                    <button type="button" class="btn-google-art custom-ui-btn">
                      <img src="https://cdn-icons-png.flaticon.com/512/2991/2991148.png" width="20" alt="Google"> 
                      {{ $t('auth.register_google', 'Google') }}
                    </button>
                    <div class="invisible-google-btn">
                      <GoogleLogin :callback="handleGoogleCallback" prompt />
                    </div>
                  </div>
                </div>

                <p class="footer-prompt stagger-item" style="--delay: 0.6s">{{ $t('auth.have_account', 'Đã có tài khoản?') }} <a href="#" @click.prevent="switchView('login')">{{ $t('auth.sign_in', 'Đăng nhập') }}</a></p>
              </div>

              <div v-else-if="currentView === 'otp'" :key="'otp'" class="form-content-wrap">
                 <div class="form-header stagger-item" style="--delay: 0.1s">
                  <div class="brand-header">
                    <img :src="groupLogoUrl" alt="GoMet Logo" class="brand-logo">
                    <span class="brand-text">GOMET</span>
                    <div class="logo-dot"></div>
                  </div>
                  <h2 class="art-title">{{ $t('auth.otp_title', 'Xác thực Email') }}</h2>
                  <p class="art-desc">{{ $t('auth.otp_sub', 'Mã gồm 6 chữ số đã được gửi tới') }} <br><b>{{ regForm.email }}</b></p>
                  <p class="art-desc" style="margin-top:6px;font-size:0.85rem;">{{ $t('auth.otp_check', 'Vui lòng kiểm tra cả hộp thư rác.') }}</p>
                </div>
                <form @submit.prevent="handleOtpVerify" class="art-form">
                  <div class="otp-group stagger-item" style="--delay: 0.2s">
                    <input v-for="(n, i) in 6" :key="i" v-model="otpDigits[i]" type="text" inputmode="numeric" pattern="[0-9]*" autocomplete="one-time-code" maxlength="1" class="otp-input" @input="focusNext($event, i)">
                  </div>
                  <div v-if="otpError" class="auth-error-msg">{{ otpError }}</div>
                  <button class="btn-submit-art stagger-item" style="--delay: 0.3s">
                    <span>{{ $t('auth.otp_verify_btn', 'Xác nhận OTP') }}</span>
                  </button>
                </form>
                <button class="btn-back stagger-item" style="--delay: 0.4s" @click="switchView('register')">{{ $t('auth.otp_back', 'Quay lại') }}</button>
              </div>

              <div v-else-if="currentView === 'forgot-password'" :key="'forgot'" class="form-content-wrap">
                <div v-if="forgotState === 'sent'" class="forgot-success stagger-item" style="--delay: 0.1s">
                  <h2 class="art-title" style="margin-top:16px">{{ $t('auth.forgot_sent_title', 'Đã gửi Email') }}</h2>
                  <button class="btn-submit-art" @click="switchView('login')" style="margin-top: 12px">
                    <span>{{ $t('auth.forgot_back_login', 'Quay lại Đăng nhập') }}</span>
                  </button>
                </div>
                <div v-else>
                  <div class="form-header stagger-item" style="--delay: 0.1s">
                    <div class="brand-header">
                      <img :src="groupLogoUrl" alt="GoMet Logo" class="brand-logo">
                      <span class="brand-text">GOMET</span>
                      <div class="logo-dot"></div>
                    </div>
                    <h2 class="art-title">{{ $t('auth.forgot_title', 'Khôi phục mật khẩu') }}</h2>
                    <p class="art-desc">{{ $t('auth.forgot_sub', 'Nhập email của bạn, chúng tôi sẽ gửi link khôi phục.') }}</p>
                  </div>
                  <form @submit.prevent="handleForgotPassword" class="art-form">
                    <div class="input-field-art stagger-item" style="--delay: 0.2s">
                      <label for="forgot-id">{{ $t('auth.forgot_identifier', 'Email của bạn') }}</label>
                      <input v-model="forgotIdentifier" type="email" id="forgot-id" required placeholder="name@example.com" :disabled="forgotState === 'loading'" />
                    </div>
                    <div v-if="forgotError" class="auth-error-msg">{{ forgotError }}</div>
                    <button class="btn-submit-art stagger-item" style="--delay: 0.3s" :disabled="!forgotIdentifier.trim() || forgotState === 'loading'">
                      <span v-if="forgotState === 'loading'" class="spinner-border"></span>
                      <span v-else>{{ $t('auth.forgot_btn', 'Gửi yêu cầu') }}</span>
                    </button>
                  </form>
                  <button class="btn-back stagger-item" style="margin-top: 16px; --delay: 0.4s" @click="switchView('login')">
                    ← {{ $t('auth.forgot_back', 'Quay lại') }}
                  </button>
                </div>
              </div>

            </transition>
          </div>
        </div>
      </div>
    </div>
  </transition>
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
const router = useRouter()

const email = ref('')
const password = ref('')
const loginError = ref('')
const regError = ref('')
const otpError = ref('')

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
  loginError.value = ''
  try {
    const role = await authStore.login(email.value, password.value)
    toast.success(t('toast.login_ok', 'Đăng nhập thành công!'))
    emit('close')
    router.push(role === 'admin' ? '/admin' : '/home')
  } catch (err) {
    const raw = err.message || ''
    if (raw === 'ACCOUNT_BANNED') {
      loginError.value = 'Tài khoản của bạn đã bị khóa bởi quản trị viên.'
      toast.error('Tài khoản của bạn đã bị khóa!')
    } else {
      loginError.value = raw || t('auth.error_login', 'Đăng nhập thất bại')
      toast.error(loginError.value)
    }
  }
}

const handleGoogleCallback = async (response) => {
  loginError.value = ''
  try {
    if (!response || !response.credential) {
      throw new Error("Không nhận được token từ Google")
    }
    const idToken = response.credential
    const data = await authService.googleLogin(idToken)

    authStore.user = {
      id:         data.accountID,
      accountID: data.accountID,
      name:       data.username,
      username:   data.username,
      email:      data.email,
      avatar:     data.avatar,
      isAdmin:    data.isAdmin,
      isPremium: data.isPremium,
      token:      data.token,
      role:       data.isAdmin ? 'admin' : 'user'
    }
    localStorage.setItem('user', JSON.stringify(authStore.user))
    toast.success(t('toast.login_ok', 'Đăng nhập Google thành công!'))
    emit('close')
    router.push(authStore.user.role === 'admin' ? '/admin' : '/home')
  } catch (err) {
    console.error("Google Login Error:", err)
    loginError.value = err.response?.data?.message || err.message || 'Lỗi đăng nhập bằng Google. Vui lòng thử lại.'
    toast.error(loginError.value)
  }
}

const handleRegisterRequest = async () => {
  regError.value = ''
  
  // 🌟 THÊM VALIDATE NÚT TICK Ở ĐÂY 🌟
  if (!regForm.agreeTerms) {
    regError.value = "Bạn cần đồng ý với điều khoản dịch vụ"
    toast.warn(regError.value)
    return
  }

  if (!regForm.name || !regForm.email || !regForm.password) {
    regError.value = t('auth.error_required', 'Vui lòng điền đầy đủ thông tin')
    toast.warn(regError.value)
    return
  }
  if (regForm.password !== regForm.confirmPassword) {
    regError.value = t('auth.error_pw_match', 'Mật khẩu không khớp')
    toast.warn(regError.value)
    return
  }
  sendingOtp.value = true
  try {
    await authService.sendOtp(regForm.name, regForm.email, regForm.password)
    toast.success(t('toast.otp_sent', 'Mã xác nhận đã được gửi!'))
    otpDigits.value = ['', '', '', '', '', '']
    currentView.value = 'otp'
  } catch (err) {
    regError.value = err.response?.data?.message || t('toast.error_generic', 'Có lỗi xảy ra')
    toast.error(regError.value)
  } finally {
    sendingOtp.value = false
  }
}

const handleOtpVerify = async () => {
  otpError.value = ''
  const code = otpDigits.value.join('')
  if (code.length < 6) {
    const msg = t('auth.error_otp', 'Vui lòng nhập đủ 6 số')
    toast.warn(msg)
    return
  }
  try {
    const data = await authService.verifyOtp(regForm.email, code)
    authStore.user = {
      id:         data.accountID,
      accountID: data.accountID,
      name:       data.username,
      username:   data.username,
      email:      data.email,
      avatar:     data.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(data.username)}&background=EA580C&color=fff`,
      isAdmin:    data.isAdmin,
      isPremium: data.isPremium,
      token:      data.token,
      role:       data.isAdmin ? 'admin' : 'user'
    }
    localStorage.setItem('user', JSON.stringify(authStore.user))
    toast.success(t('toast.register_ok', 'Đăng ký thành công!'))
    emit('close')
    router.push('/home')
  } catch (err) {
    otpError.value = err.response?.data?.message || t('auth.error_otp', 'Mã xác nhận không đúng')
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
    toast.success(t('auth.forgot_sent_title', 'Đã gửi Email khôi phục!'))
  } catch (err) {
    forgotState.value = 'idle'
    forgotError.value = err.response?.data?.message || 'Có lỗi xảy ra khi gửi yêu cầu.'
    toast.error(forgotError.value)
  }
}

onMounted(() => {
  isOpen.value = true
  document.body.style.overflow = 'hidden'
  nextTick(() => { overlayRef.value?.focus() })
})

onUnmounted(() => {
  document.body.style.overflow = ''
})
</script>

<style scoped lang="scss" src="./AuthModal.scss"></style>