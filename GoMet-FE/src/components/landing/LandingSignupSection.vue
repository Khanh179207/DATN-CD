<template>
  <section class="signup-section" id="sectionsigninlanding" ref="signupSection">
    
    <div class="background-layer">
      <div class="bg-overlay"></div>
      
      <div class="bg-column col-slow">
        <div class="scroll-track">
          <img src="@/assets/images/intro/bun.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/bunbo.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/banhmi.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/com.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/bun.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/bunbo.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/banhmi.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/com.jpg" :alt="$t('common.food_image')">
        </div>
      </div>

      <div class="bg-column col-fast offset-down">
        <div class="scroll-track reverse">
          <img src="@/assets/images/intro/banh.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/goi.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/bun.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/bunbo.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/banh.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/goi.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/bun.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/bunbo.jpg" :alt="$t('common.food_image')">
        </div>
      </div>

      <div class="bg-column col-medium">
        <div class="scroll-track">
          <img src="@/assets/images/intro/banhmi.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/com.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/banh.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/goi.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/banhmi.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/com.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/banh.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/goi.jpg" :alt="$t('common.food_image')">
        </div>
      </div>

      <div class="bg-column col-fast mobile-hidden">
        <div class="scroll-track reverse">
          <img src="@/assets/images/intro/bun.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/goi.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/banh.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/com.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/bun.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/goi.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/banh.jpg" :alt="$t('common.food_image')">
          <img src="@/assets/images/intro/com.jpg" :alt="$t('common.food_image')">
        </div>
      </div>
    </div>
    
    <div class="bg-visuals">
      <div class="orb orb-1 gsap-orb"></div>
      <div class="orb orb-2 gsap-orb"></div>
    </div>

    <div class="container main-layout">
      
      <div class="info-side gsap-signup-left">
        <div class="brand-header">
          <div class="logo-text">GOMET</div>
          <div class="logo-dot"></div>
        </div>
        
        <h1 class="main-title">
          {{ $t('landing.signup_title_line1') }} <br>
          <span class="text-gradient shine-effect">{{ $t('landing.signup_title_highlight') }}</span>
        </h1>
        
        <p class="description">
          {{ $t('landing.signup_desc') }}
        </p>
        
        <div class="stats-grid">
          <div class="stat-item">
            <strong>15K+</strong>
            <span>{{ $t('landing.signup_stat_recipes') }}</span>
          </div>
          <div class="stat-sep"></div>
          <div class="stat-item">
            <strong>1M+</strong>
            <span>{{ $t('landing.signup_stat_favorites') }}</span>
          </div>
          <div class="stat-sep"></div>
          <div class="stat-item">
            <strong>24/7</strong>
            <span>{{ $t('landing.signup_stat_support') }}</span>
          </div>
        </div>
      </div>

      <div class="form-side gsap-signup-right">
        <div class="auth-card glass-panel">
          
          <div class="auth-tabs" v-if="activeTab !== 'forgot-password'">
            <button class="tab-btn" :class="{ active: activeTab === 'login' }" @click="switchTab('login')">{{ $t('auth.login') }}</button>
            <button class="tab-btn" :class="{ active: activeTab === 'register' }" @click="switchTab('register')">{{ $t('auth.register') }}</button>
          </div>

          <div class="form-content">
            <h3 class="form-title" v-if="activeTab !== 'forgot-password'">
              {{ activeTab === 'login' ? $t('auth.welcome_back') : $t('auth.create_account') }}
            </h3>

            <form @submit.prevent="handleSubmit">
              
<div v-if="activeTab === 'register'" class="fade-in-anim">
  <div class="input-group">
    <label>{{ $t('auth.fullname') }}</label>
    <input v-model="registerForm.username" type="text" :placeholder="$t('auth.register_name_placeholder')" class="input-field" required />
  </div>
  <div class="input-group">
    <label>{{ $t('auth.email') }}</label>
    <input v-model="registerForm.email" type="email" :placeholder="$t('auth.forgot_email_placeholder')" class="input-field" required />
  </div>
  <div class="input-group">
    <label>{{ $t('auth.password') }}</label>
    <input v-model="registerForm.password" type="password" :placeholder="$t('auth.register_password_placeholder')" class="input-field" required />
  </div>
  <div class="input-group">
    <label>{{ $t('auth.confirm_pw') }}</label>
    <input v-model="registerForm.confirmPassword" type="password" :placeholder="$t('auth.register_confirm_placeholder')" class="input-field" required />
  </div>

<div class="input-group-checkbox">
  <label class="checkbox-container">
    <input type="checkbox" v-model="registerForm.agreeTerms" required>
    <span class="checkmark"></span>
    <span class="label-text">
      {{ $t('auth.agree_terms') }} <router-link to="/terms-and-policy">{{ $t('auth.terms') }}</router-link> và <router-link to="/terms-and-policy">{{ $t('auth.privacy_policy') }}</router-link>
    </span>
  </label>
</div>
</div>

              <div v-else-if="activeTab === 'login'" class="fade-in-anim">
                <div class="input-group">
                  <label>{{ $t('auth.email') }}</label>
                  <input v-model="loginForm.email" type="email" :placeholder="$t('auth.forgot_email_placeholder')" class="input-field" required />
                </div>
                <div class="input-group">
                  <label>{{ $t('auth.password') }}</label>
                  <input v-model="loginForm.password" type="password" :placeholder="$t('auth.login_password_placeholder')" class="input-field" required />
                </div>
                <div class="form-actions">
                  <label class="remember"><input type="checkbox"> {{ $t('auth.remember') }}</label>
                  <a href="#" class="forgot-pass" @click.prevent="activeTab = 'forgot-password'; forgotState = 'idle'; forgotError = ''; forgotIdentifier = ''">{{ $t('auth.forgot') }}</a>
                </div>

                <!-- BANNED ALERT BOX LUXURY -->
                <transition name="fade-slide" mode="out-in">
                  <div v-if="isBannedBoxVisible" key="banned-local" class="banned-alert-box">
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
                  <div v-else-if="loginError" key="error-local" class="auth-error-msg">
                    {{ loginError }}
                  </div>
                </transition>
                <transition name="fade-slide">
                  <div v-if="loginError && !isBannedBoxVisible && wrongPasswordCount >= 3" class="appeal-hint">
                    <button type="button" class="btn-appeal-link" @click="openAppealAction">
                      {{ $t('auth.banned_cta_label') }} → {{ $t('auth.banned_cta_action') }}
                    </button>
                  </div>
                </transition>
              </div>

              <div v-else-if="activeTab === 'forgot-password'" class="fade-in-anim">
                
                <div v-if="forgotState === 'sent'" class="forgot-success">
                  <div class="forgot-icon-ring">
                    <svg viewBox="0 0 52 52" class="check-anim" xmlns="http://www.w3.org/2000/svg">
                      <circle class="check-circle" cx="26" cy="26" r="25" fill="none"/>
                      <path class="check-path" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
                    </svg>
                  </div>
                  <h3 class="form-title" style="margin-top: 15px">{{ $t('auth.forgot_sent_title') }}</h3>
                  <p class="description" style="text-align: center; margin-bottom: 20px; font-size: 0.9rem; color: #1C1917">{{ $t('auth.forgot_sent_sub') }}</p>
                  <button type="button" class="btn-submit" @click="activeTab = 'login'">{{ $t('auth.forgot_back_login') }}</button>
                </div>

                <div v-else>
                  <h3 class="form-title">{{ $t('auth.forgot_title') }}</h3>
                  <p class="description" style="text-align: center; margin-bottom: 20px; font-size: 0.9rem; color: #78716C">{{ $t('auth.forgot_sub') }}</p>

                  <div class="input-group">
                    <label>{{ $t('auth.forgot_identifier') }}</label>
                    <input v-model="forgotIdentifier" type="email" :placeholder="$t('auth.forgot_email_placeholder')" class="input-field" required :disabled="forgotState === 'loading'" />
                  </div>

                  <div v-if="forgotError" class="auth-error-msg">{{ forgotError }}</div>

                  <button type="submit" class="btn-submit" :disabled="!forgotIdentifier.trim() || forgotState === 'loading'">
                    <span v-if="forgotState === 'loading'" class="spinner-border" role="status" aria-hidden="true"></span>
                    <span v-else>{{ $t('auth.forgot_btn') }}</span>
                  </button>

                  <button type="button" class="btn-back" @click="switchTab('login')">
                    ← {{ $t('auth.forgot_back') }}
                  </button>
                </div>
              </div>

<button 
  type="submit" 
  class="btn-submit" 
  :disabled="isLoading || (activeTab === 'register' && !registerForm.agreeTerms)"
>
  <span v-if="isLoading" class="spinner-border" role="status" aria-hidden="true"></span>
  <span v-else>{{ activeTab === 'login' ? $t('auth.sign_in_btn') : $t('auth.register_btn') }}</span>
</button>
              <div class="divider" v-if="activeTab !== 'forgot-password'"><span>{{ $t('common.or') }}</span></div>
              
              <div class="social-buttons custom-google-wrapper" v-if="activeTab !== 'forgot-password'">
                <button type="button" class="social-btn google custom-ui-btn">
                  <img src="https://cdn-icons-png.flaticon.com/512/2991/2991148.png" width="20" :alt="$t('common.google_logo')">
                  {{ activeTab === 'login' ? $t('auth.login_google') : $t('auth.register_google') }}
                </button>

                <div class="invisible-google-btn">
                  <GoogleLogin 
                    :callback="handleGoogleCallback" 
                    prompt
                  />
                </div>
              </div>

            </form>
          </div>
        </div>
        
        <div class="form-footer">
          <p>{{ $t('landing.signup_footer_note') }}</p>
        </div>
      </div>

    </div>

    <Teleport to="body">
      <OtpModal v-if="showOtpModal" :email="registerForm.email" v-model="otpCode" @close="showOtpModal = false" @verify="handleVerifyOtp" />
      <AppealModal v-if="showAppealModal" @close="showAppealModal = false" />
    </Teleport>

  </section>
</template>

<script setup>
import { ref, reactive, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import * as authService from '@/services/authService'
import { GoogleLogin } from 'vue3-google-login'
import OtpModal from '@/components/modals/OtpModal.vue'
import AppealModal from '@/components/modals/AppealModal.vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { formatLocaleDateTime } from '@/i18n'

gsap.registerPlugin(ScrollTrigger)

const router = useRouter()
const { t } = useI18n()
const authStore = useAuthStore()

const activeTab = ref('login')
const showOtpModal = ref(false)
const otpCode = ref('')
const isLoading = ref(false)
const signupSection = ref(null)
const showAppealModal = ref(false)
const isBannedBoxVisible = ref(false)
const bannedDetails = ref(null)
const loginError = ref('')
const wrongPasswordCount = ref(0)

const loginForm = reactive({ email: '', password: '' })
const registerForm = reactive({ 
  username: '', 
  email: '', 
  password: '', 
  confirmPassword: '',
  agreeTerms: false 
})

// State cho Quên mật khẩu
const forgotIdentifier = ref('')
const forgotState      = ref('idle') 
const forgotError      = ref('')

const switchTab = (tab) => {
  activeTab.value = tab;
  loginError.value = '';
  isBannedBoxVisible.value = false;
  bannedDetails.value = null;
  forgotError.value = '';
}

const switchTabListener = (event) => {
  if (event.detail === 'login' || event.detail === 'register') {
    switchTab(event.detail)
  }
}

onMounted(() => {
  window.addEventListener('switch-auth-tab', switchTabListener)
  
  let ctx = gsap.context(() => {
    gsap.from(".gsap-signup-left", {
      scrollTrigger: { trigger: signupSection.value, start: "top 80%" },
      x: -50, opacity: 0, duration: 1, ease: "power3.out"
    });
    gsap.from(".gsap-signup-right", {
      scrollTrigger: { trigger: signupSection.value, start: "top 80%" },
      x: 50, opacity: 0, duration: 1, ease: "power3.out", delay: 0.2
    });
    gsap.to(".orb-1", { opacity: 0.2, x: "10vw", duration: 8, repeat: -1, yoyo: true, ease: "sine.inOut" });
    gsap.to(".orb-2", { opacity: 0.1, x: "-10vw", duration: 10, repeat: -1, yoyo: true, ease: "sine.inOut" });
  }, signupSection.value);
})

onUnmounted(() => {
  window.removeEventListener('switch-auth-tab', switchTabListener)
})

watch([() => loginForm.email, () => loginForm.password], () => {
  if (loginError.value || isBannedBoxVisible.value) {
    loginError.value = ''
    isBannedBoxVisible.value = false
    bannedDetails.value = null
  }
})

const closeBannedAlert = () => {
  isBannedBoxVisible.value = false;
  loginError.value = '';
  bannedDetails.value = null;
}

const openAppealAction = () => {
  showAppealModal.value = true;
  closeBannedAlert(); // Chủ động dọn dẹp lỗi đỏ để UI thông thoáng khi mở Modal
}

const processBannedError = (errData, errorMessage) => {
  isBannedBoxVisible.value = true;
  const banReason = errData.banReason || t('auth.banned_reason_default');
  let rawTimeStr = '';
  
  if (errData.bannedAt) {
     rawTimeStr = formatLocaleDateTime(errData.bannedAt);
  }
  bannedDetails.value = { reason: banReason, time: rawTimeStr };
  loginError.value = rawTimeStr
    ? `${t('auth.account_locked')} (${rawTimeStr}). ${t('auth.banned_reason')}: ${banReason}`
    : `${t('auth.account_locked')}. ${t('auth.banned_reason')}: ${banReason}`;
  toast.error(loginError.value)
}

const handleSubmit = async () => {
  // Xử lý Gửi Quên Mật Khẩu
  if (activeTab.value === 'forgot-password') {
    forgotError.value = ''
    if (!forgotIdentifier.value.trim()) return
    forgotState.value = 'loading'
    try {
      await authService.forgotPassword(forgotIdentifier.value.trim())
      forgotState.value = 'sent'
      toast.success(t('auth.forgot_sent_title'))
    } catch (err) {
      forgotState.value = 'idle'
      forgotError.value = t('auth.forgot_generic_error')
      toast.error(forgotError.value)
    }
    return
  }

  // Xử lý Đăng Nhập
  if (activeTab.value === 'login') {
    isLoading.value = true
    try {
      const role = await authStore.login(loginForm.email, loginForm.password)
      toast.success(t('toast.login_ok'))
      
      // 🔥 BẮT BUỘC THÊM DÒNG NÀY: Báo cho MainLayout biết là tao vừa login xong, hiện Loading đi!
      sessionStorage.setItem('just_logged_in', 'true')
      
      router.push(role === 'admin' ? '/admin' : '/home')
    } catch (err) {
      const errData = err.response?.data || {}
      const backendMsg = errData.message || err.message || ''
      const errorString = backendMsg.toUpperCase()
      
      if (errorString.includes('ACCOUNT_BANNED')) {
        processBannedError(errData, backendMsg)
      } else {
        wrongPasswordCount.value++
        loginError.value = t('auth.error_login')
        toast.error(loginError.value)
      }
    } finally {
      isLoading.value = false
    }

  // Xử lý Đăng Ký
  } else if (activeTab.value === 'register') {
    if (!registerForm.agreeTerms) {
    toast.warn(t('auth.agree_terms_required'))
    return
  }
    if (registerForm.password !== registerForm.confirmPassword) {
      toast.warn(t('auth.error_pw_match'))
      return
    }
    isLoading.value = true
    try {
      await authService.sendOtp(registerForm.username, registerForm.email, registerForm.password)
      toast.success(t('toast.otp_sent'))
      otpCode.value = ''
      showOtpModal.value = true
    } catch (err) {
      toast.error(t('toast.error_generic'))
    } finally {
      isLoading.value = false
    }
  }
}

const handleVerifyOtp = async () => {
  if (otpCode.value.length < 6) {
    toast.warn(t('auth.otp_incomplete'))
    return
  }
  try {
    const data = await authService.verifyOtp(registerForm.email, otpCode.value)
    authStore.setUser(data)
    
    toast.success(t('toast.register_ok'))
    showOtpModal.value = false
    
    // 🔥 THÊM Ở ĐÂY LUÔN CHO CHẮC: Đăng ký thành công cũng coi như vừa login
    sessionStorage.setItem('just_logged_in', 'true')
    
    router.push('/home')
  } catch (err) {
    toast.error(t('auth.error_otp'))
  }
}

const handleGoogleCallback = async (response) => {
  loginError.value = ''
  isBannedBoxVisible.value = false
  try {
    if (!response || !response.credential) {
      throw new Error(t('auth.google_token_missing'));
    }

    const idToken = response.credential;
    const data = await authService.googleLogin(idToken);
    authStore.setUser(data);

    toast.success(t('toast.login_ok'))
    
    // 🔥 VÀ THÊM Ở ĐÂY NỮA (Google Login)
    sessionStorage.setItem('just_logged_in', 'true')
    
    router.push(authStore.user.role === 'admin' ? '/admin' : '/home')

  } catch (err) {
    console.error("Google Login Error:", err)
    const errData = err.response?.data || err?.data || err || {}
    const errorMessage = errData.message || err.message || String(err)
    const errorString = errorMessage.toUpperCase()

    if (errorString.includes('ACCOUNT_BANNED') || errorString.includes('BANNED') || errData.status === 403) {
      processBannedError(errData, errorMessage);
    } else {
      loginError.value = t('auth.google_login_error')
      toast.error(loginError.value)
    }
  }
}
</script>

<style scoped lang="scss" src="./LandingSignupSection.scss"></style>
<style scoped>
/* --- BANNED ALERT BOX LUXURY (LOCAL) --- */
.banned-alert-box {
  background: #fff5f5; border: 1px solid #fecaca; border-radius: 16px; padding: 16px; margin: 15px 0 20px;
  text-align: left; box-shadow: 0 8px 20px rgba(220, 38, 38, 0.08); animation: shake 0.4s ease-in-out; position: relative;
}
@keyframes shake { 0%, 100% { transform: translateX(0); } 20%, 60% { transform: translateX(-4px); } 40%, 80% { transform: translateX(4px); } }
.btn-close-alert { position: absolute; top: 10px; right: 10px; background: transparent; border: none; color: #fca5a5; cursor: pointer; padding: 4px; border-radius: 50%; display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.btn-close-alert:hover { background: #fee2e2; color: #dc2626; transform: scale(1.1); }
.banned-header { display: flex; align-items: center; gap: 10px; color: #dc2626; margin-bottom: 12px; border-bottom: 1px dashed #fca5a5; padding-bottom: 12px; }
.banned-header h4 { margin: 0; font-size: 0.95rem; font-weight: 800; letter-spacing: 0.5px; }
.banned-body { display: flex; flex-direction: column; gap: 12px; margin-bottom: 16px; }
.b-row { display: flex; flex-direction: column; gap: 4px; font-size: 0.9rem; }
.b-lbl { color: #dc2626; font-weight: 700; text-transform: uppercase; font-size: 0.7rem; letter-spacing: 0.5px; }
.b-val { color: #0f172a; font-weight: 600; line-height: 1.4; }
.b-val.reason-text { font-style: italic; background: white; padding: 10px 12px; border-radius: 8px; border: 1px solid #fecaca; box-shadow: inset 0 2px 4px rgba(0,0,0,0.02); font-size: 0.85rem; color: #7f1d1d;}
.banned-msg { color: #991b1b; font-size: 0.9rem; line-height: 1.5; margin: 0; font-weight: 500; }
.banned-footer { text-align: center; }
.btn-appeal-lux { background: #dc2626; color: white; border: none; padding: 12px 20px; border-radius: 100px; font-size: 0.9rem; font-weight: 600; cursor: pointer; transition: 0.3s; width: 100%; display: flex; align-items: center; justify-content: center; gap: 6px; box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3); }
.btn-appeal-lux span { font-weight: 800; text-decoration: underline; }
.btn-appeal-lux:hover { background: #b91c1c; transform: translateY(-2px); box-shadow: 0 6px 15px rgba(220, 38, 38, 0.4); }

.auth-error-msg { background: #FEF2F2; color: #DC2626; border: 1px solid #FCA5A5; border-radius: 8px; padding: 10px; font-size: 0.85rem; font-weight: 600; margin-bottom: 15px; text-align: center; }

.appeal-hint { margin-bottom: 15px; }
.btn-appeal-link { width: 100%; padding: 12px; background: linear-gradient(135deg, rgba(234, 88, 12, 0.15), rgba(251, 146, 60, 0.1)); color: #ea580c; border: 1px solid #fbbf24; border-radius: 8px; font-weight: 700; font-size: 0.9rem; cursor: pointer; transition: all 0.3s; text-align: center; }
.btn-appeal-link:hover { background: linear-gradient(135deg, rgba(234, 88, 12, 0.25), rgba(251, 146, 60, 0.15)); border-color: #ea580c; box-shadow: 0 6px 16px rgba(234, 88, 12, 0.2); transform: translateY(-2px); }

.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.3s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(-10px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-10px); }
</style>