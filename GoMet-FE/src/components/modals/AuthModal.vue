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

                  <transition name="fade-slide" mode="out-in">
                    <div v-if="isBannedError" key="banned-box" class="banned-alert-box stagger-item" style="--delay: 0.45s">
                      <button type="button" class="btn-close-alert" @click="closeBannedAlert" title="Đóng">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                      </button>
                      <div class="banned-header">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                        <h4>TÀI KHOẢN BỊ KHÓA</h4>
                      </div>
                      <div class="banned-body">
                        <template v-if="bannedDetails">
                          <div class="b-row" v-if="bannedDetails.time"><span class="b-lbl">Thời gian:</span> <span class="b-val">{{ bannedDetails.time }}</span></div>
                          <div class="b-row"><span class="b-lbl">Lý do khóa:</span> <span class="b-val reason-text">{{ bannedDetails.reason }}</span></div>
                        </template>
                        <template v-else>
                          <p class="banned-msg">{{ loginError }}</p>
                        </template>
                      </div>
                      <div class="banned-footer">
                        <button type="button" class="btn-appeal-lux" @click="openAppealAction">
                          Phát hiện nhầm lẫn? <span>Gửi khiếu nại ngay</span>
                        </button>
                      </div>
                    </div>

                    <div v-else-if="currentView === 'login' && isDeactivatedError" key="deactivated-box" class="deactivated-alert-box stagger-item" style="--delay: 0.45s">
                      <div class="deactivated-header">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path></svg>
                        <h4>TÀI KHOẢN ĐÃ XÓA MỀM</h4>
                      </div>
                      <div class="deactivated-body">
                        <p class="deactivated-msg">
                          Tài khoản này đã được yêu cầu xóa vào ngày <b>{{ deactivatedDetails?.time }}</b>. 
                          Bạn có muốn khôi phục lại không?
                        </p>
                      </div>
                      <div class="deactivated-footer">
                        <button type="button" class="btn-restore-trigger" @click="switchView('restore-account')">
                          Khôi phục tài khoản ngay
                        </button>
                      </div>
                    </div>

                    <div v-else-if="loginError" key="error-box" class="auth-error-msg stagger-item" style="--delay: 0.45s">
                      {{ loginError }}
                    </div>
                  </transition>

                  <transition name="fade-slide">
                    <div v-if="!isBannedError && wrongPasswordCount >= 3" class="appeal-hint stagger-item" style="--delay: 0.45s">
                      <button type="button" class="btn-appeal-link" @click.prevent="switchView('forgot-password')">
                        Quên mật khẩu? → Lấy lại mật khẩu
                      </button>
                    </div>
                  </transition>

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

                  <div class="form-actions stagger-item" style="--delay: 0.45s; justify-content: flex-start; margin-top: 10px; margin-bottom: 20px;">
                    <label class="remember-me" style="font-size: 0.85rem; width: 100%; white-space: normal; display: flex; align-items: flex-start;">
                      <input type="checkbox" v-model="regForm.agreeTerms" required>
                      <span class="checkmark" style="margin-top: 2px;"></span>
                      <span style="margin-left: 28px; line-height: 1.5; color: #64748b;">
                        Tôi đồng ý với <router-link to="/terms-and-policy" @click="$emit('close')" style="color: #ea580c; text-decoration: none; font-weight: 700;">Điều khoản</router-link> và <router-link to="/terms-and-policy" @click="$emit('close')" style="color: #ea580c; text-decoration: none; font-weight: 700;">Chính sách bảo mật</router-link>
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

              <div v-else-if="currentView === 'restore-account'" :key="'restore'" class="form-content-wrap">
                <div class="form-header stagger-item" style="--delay: 0.1s">
                  <div class="brand-header">
                    <img :src="groupLogoUrl" alt="GoMet Logo" class="brand-logo">
                    <span class="brand-text">GOMET</span>
                    <div class="logo-dot"></div>
                  </div>
                  <h2 class="art-title">Khôi phục tài khoản</h2>
                  <p class="art-desc">Chào mừng sếp trở lại! <br> Hãy nhập mã OTP đã được gửi tới email <b>{{ email }}</b> để tiếp tục.</p>
                </div>

                <form @submit.prevent="handleRestoreVerify" class="art-form">
                  <div v-if="sendingRestoreOtp || otpSentSuccessfully" class="otp-hint-restore stagger-item" style="--delay: 0.2s">
                    {{ sendingRestoreOtp ? 'Đang gửi mã xác thực...' : 'Mã xác thực đã được gửi tự động. Vui lòng kiểm tra hộp thư của bạn.' }}
                  </div>

                  <div class="otp-section stagger-item" style="--delay: 0.3s">
                    <div class="otp-group-lux">
                      <input v-for="(n, i) in 6" :key="i" v-model="restoreOtpDigits[i]" type="text" inputmode="numeric" maxlength="1" class="otp-input-lux" @input="focusRestoreNext($event, i)">
                    </div>
                    <div class="resend-container">
                      <button type="button" class="btn-resend-otp-lux" @click="handleRequestRestoreOtp" :disabled="sendingRestoreOtp">
                        {{ sendingRestoreOtp ? 'Đang gửi...' : 'Chưa nhận được mã? Gửi lại ngay' }}
                      </button>
                    </div>
                  </div>


                  <div v-if="restoreError" class="auth-error-msg" style="margin-top: 15px;">{{ restoreError }}</div>

                  <button class="btn-submit-art stagger-item" :disabled="restoring" style="--delay: 0.4s; margin-top: 25px;">
                    <span v-if="restoring" class="spinner-border spinner-border-sm"></span>
                    <span v-else>Xác nhận Khôi phục ngay</span>
                  </button>
                </form>
                <button class="btn-back stagger-item" style="margin-top: 16px; --delay: 0.5s" @click="switchView('login')">
                  ← Quay lại Đăng nhập
                </button>
              </div>

            </transition>
          </div>
        </div>
      </div>
    </div>
  </transition>

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
const wrongPasswordCount = ref(0)
const bannedDetails = ref(null)
const deactivatedDetails = ref(null)
const restoreOtpDigits = ref(['', '', '', '', '', ''])
const sendingRestoreOtp = ref(false)
const restoring = ref(false)
const restoreError = ref('')
const otpSentSuccessfully = ref(false)

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
  otpError.value = ''
  forgotError.value = ''
  restoreError.value = ''
  bannedDetails.value = null
  deactivatedDetails.value = null
  
  if (name !== 'forgot-password') {
    forgotState.value = 'idle'
    forgotIdentifier.value = ''
  }
}

watch([currentView, email, isOpen], ([newView, newEmail, open]) => {
  if (open && newView === 'restore-account' && newEmail) {
    if (!sendingRestoreOtp.value && !restoring.value && !otpSentSuccessfully.value) {
        handleRequestRestoreOtp();
    }
  }
}, { immediate: true })

const openAppealAction = () => {
  isOpen.value = false
  showAppealModal.value = true
}

const handleAppealClose = () => {
  showAppealModal.value = false
  emit('close')
}

const isBannedError = computed(() => {
  if (!loginError.value) return false
  const err = loginError.value.toUpperCase()
  return err.includes('BỊ KHÓA') || err.includes('KHÓA') || err.includes('BANNED')
})

const isDeactivatedError = computed(() => {
  if (!loginError.value) return false
  const err = loginError.value.toUpperCase()
  return err.includes('XÓA MỀM') || err.includes('DEACTIVATED')
})

// 🔥 LOGIC ĐIỀU HƯỚNG THÔNG MINH CHO ADMIN
const handleRedirection = (role) => {
  const isMobileOrTablet = window.innerWidth < 1024;
  
  if (role === 'admin') {
    if (isMobileOrTablet) {
      toast.info('Hệ thống chuyển bạn về giao diện người dùng để có trải nghiệm tốt nhất trên di động nhé! 📱');
      router.push('/home');
    } else {
      router.push('/admin');
    }
  } else {
    router.push('/home');
  }
}

const handleLogin = async () => {
  loginError.value = ''
  try {
    const role = await authStore.login(email.value, password.value)
    toast.success(t('toast.login_ok', 'Đăng nhập thành công!'))
    wrongPasswordCount.value = 0 
    emit('close')
    
    // 🔥 SỬ DỤNG HÀM ĐIỀU HƯỚNG MỚI
    handleRedirection(role);
    
  } catch (err) {
    const errData = err.response?.data || {}
    const errorMessage = errData.message || err.message || String(err)
    const errorString = errorMessage.toUpperCase()

    if (errorString.includes('ACCOUNT_BANNED')) {
      const banReason = errData.banReason || errData.reason || 'Vi phạm tiêu chuẩn cộng đồng GOMET';
      const bannedBy = errData.bannedByName || errData.bannedBy || 'Quản trị viên hệ thống';
      let timeStr = '';
      let rawTimeStr = '';
      
      if (errData.bannedAt) {
         const d = new Date(errData.bannedAt);
         rawTimeStr = `${d.toLocaleTimeString('vi-VN', {hour: '2-digit', minute:'2-digit'})} ngày ${d.toLocaleDateString('vi-VN')}`;
         timeStr = ` vào lúc ${rawTimeStr}`;
      }

      bannedDetails.value = { reason: banReason, by: bannedBy, time: rawTimeStr };
      loginError.value = `Tài khoản của bạn đã bị khóa${timeStr}. Lý do: ${banReason}`;
      toast.error(`🚨 TÀI KHOẢN BỊ KHÓA: Đăng nhập thất bại do vi phạm!`, { timeout: 8000 })
      
    } else if (errorString.includes('ACCOUNT_DEACTIVATED')) {
      let timeStr = 'Vừa qua';
      if (errData.deletedAt) {
        const d = new Date(errData.deletedAt);
        timeStr = d.toLocaleDateString('vi-VN');
      }
      deactivatedDetails.value = { time: timeStr };
      if (errData.email) email.value = errData.email;
      loginError.value = 'TÀI KHOẢN ĐÃ XÓA MỀM';
      toast.info('Tài khoản này đang trong trạng thái xóa mềm.')

    } else if (errorString.includes('INCORRECT') || errorString.includes('PASSWORD') || errorString.includes('CREDENTIALS')) {
      wrongPasswordCount.value++
      loginError.value = 'Mật khẩu không đúng'
      toast.error(`${loginError.value} (${wrongPasswordCount.value}/3)`)
    } else {
      loginError.value = errorMessage || t('auth.error_login', 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.')
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
    authStore.setUser(data)

    toast.success(t('toast.login_ok', 'Đăng nhập Google thành công!'))
    emit('close')
    
    // 🔥 SỬ DỤNG HÀM ĐIỀU HƯỚNG MỚI
    handleRedirection(authStore.user.role);

  } catch (err) {
    const errorString = String(err.response?.data?.message || err.message || err).toUpperCase()

    if (errorString.includes('ACCOUNT_BANNED') || errorString.includes('BANNED')) {
      const banReason = err.response?.data?.banReason || err.response?.data?.reason || 'Vi phạm tiêu chuẩn cộng đồng';
      const bannedBy = err.response?.data?.bannedByName || err.response?.data?.bannedBy || 'Quản trị viên';
      let timeStr = '';
      let rawTimeStr = '';
      if (err.response?.data?.bannedAt) {
         const d = new Date(err.response.data.bannedAt);
         rawTimeStr = `${d.toLocaleTimeString('vi-VN', {hour: '2-digit', minute:'2-digit'})} ngày ${d.toLocaleDateString('vi-VN')}`;
         timeStr = ` vào lúc ${rawTimeStr}`;
      }
      
      bannedDetails.value = { reason: banReason, by: bannedBy, time: rawTimeStr };
      loginError.value = `Tài khoản của bạn đã bị khóa${timeStr}. Lý do: ${banReason}.`
      toast.error(`🚨 TÀI KHOẢN BỊ KHÓA: Bạn không thể đăng nhập bằng Google vì ${loginError.value}`, { timeout: 8000 })
      
    } else if (errorString.includes('ACCOUNT_DEACTIVATED')) {
       let timeStr = 'Vừa qua';
      if (err.response?.data?.deletedAt) {
        const d = new Date(err.response.data.deletedAt);
        timeStr = d.toLocaleDateString('vi-VN');
      }
      deactivatedDetails.value = { time: timeStr };
      email.value = err.response?.data?.email || '';
      loginError.value = 'TÀI KHOẢN ĐÃ XÓA MỀM';
      toast.info('Tài khoản Google này đang trong trạng thái xóa mềm.')

    } else {
      loginError.value = err.response?.data?.message || err.message || 'Lỗi đăng nhập bằng Google. Vui lòng thử lại.'
      toast.error(loginError.value)
    }
  }
}

const handleRegisterRequest = async () => {
  regError.value = ''
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
    authStore.setUser(data)
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

const handleRequestRestoreOtp = async () => {
  if (!email.value) {
    toast.error('Không tìm thấy Email để gửi mã. Vui lòng thử lại.');
    return;
  }
  sendingRestoreOtp.value = true;
  otpSentSuccessfully.value = false;
  try {
    await authService.sendRestoreOtp(email.value)
    otpSentSuccessfully.value = true;
    toast.success('Mã OTP đã được gửi về Email của bạn!')
  } catch (err) {
    toast.error(err.response?.data?.message || 'Không thể gửi OTP.')
  } finally {
    sendingRestoreOtp.value = false
  }
}

const handleRestoreVerify = async () => {
  const code = restoreOtpDigits.value.join('')
  if (code.length < 6) {
    toast.warn('Vui lòng nhập đủ 6 số OTP')
    return
  }
  restoring.value = true
  restoreError.value = ''
  try {
    const role = await authStore.restoreAccount(email.value, code)
    toast.success('Khôi phục thành công! Chào mừng sếp trở lại.')
    emit('close')
    
    // 🔥 SỬ DỤNG HÀM ĐIỀU HƯỚNG MỚI
    handleRedirection(role);
    
  } catch (err) {
    restoreError.value = err.response?.data?.message || 'Mã OTP không chính xác hoặc đã hết hạn.'
    toast.error(restoreError.value)
  } finally {
    restoring.value = false
  }
}

const focusRestoreNext = (e, index) => {
  if (e.target.value && index < 5) e.target.parentElement.children[index + 1].focus()
}

const handleRestorePrompt = (e) => {
  isOpen.value = true
  currentView.value = 'restore-account'
  if (e.detail?.email) email.value = e.detail.email
  if (e.detail?.deletedAt) {
    const d = new Date(e.detail.deletedAt)
    deactivatedDetails.value = { time: d.toLocaleDateString('vi-VN') }
  }
}

const handleBannedPrompt = (e) => {
  isOpen.value = true
  currentView.value = 'login'
  const msg = e.detail?.msg || 'Tài khoản của bạn đã bị khóa.'
  loginError.value = msg
  if (e.detail?.details) {
    bannedDetails.value = e.detail.details
  }
}

onMounted(() => {
  isOpen.value = true
  document.body.style.overflow = 'hidden'
  nextTick(() => { overlayRef.value?.focus() })
  window.addEventListener('auth:banned-login-prompt', handleBannedPrompt)
  window.addEventListener('auth:restore-login-prompt', handleRestorePrompt)
  window.addEventListener('auth:restore-login-data', handleRestorePrompt)
})

onUnmounted(() => {
  document.body.style.overflow = ''
  wrongPasswordCount.value = 0
  window.removeEventListener('auth:banned-login-prompt', handleBannedPrompt)
  window.removeEventListener('auth:restore-login-prompt', handleRestorePrompt)
  window.removeEventListener('auth:restore-login-data', handleRestorePrompt)
})
</script>

<style scoped lang="scss" src="./AuthModal.scss"></style>