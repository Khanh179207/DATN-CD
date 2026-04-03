<template>
  <section class="signup-section" id="sectionsigninlanding" ref="signupSection">
    
    <div class="background-layer">
      <div class="bg-overlay"></div>
      
      <div class="bg-column col-slow">
        <div class="scroll-track">
          <img src="@/assets/images/intro/bun.webp" alt="Food">
          <img src="@/assets/images/intro/bunbo.webp" alt="Food">
          <img src="@/assets/images/intro/banhmi.webp" alt="Food">
          <img src="@/assets/images/intro/com.webp" alt="Food">
          <img src="@/assets/images/intro/bun.webp" alt="Food">
          <img src="@/assets/images/intro/bunbo.webp" alt="Food">
          <img src="@/assets/images/intro/banhmi.webp" alt="Food">
          <img src="@/assets/images/intro/com.webp" alt="Food">
        </div>
      </div>

      <div class="bg-column col-fast offset-down">
        <div class="scroll-track reverse">
          <img src="@/assets/images/intro/banh.webp" alt="Food">
          <img src="@/assets/images/intro/goi.webp" alt="Food">
          <img src="@/assets/images/intro/bun.webp" alt="Food">
          <img src="@/assets/images/intro/bunbo.webp" alt="Food">
          <img src="@/assets/images/intro/banh.webp" alt="Food">
          <img src="@/assets/images/intro/goi.webp" alt="Food">
          <img src="@/assets/images/intro/bun.webp" alt="Food">
          <img src="@/assets/images/intro/bunbo.webp" alt="Food">
        </div>
      </div>

      <div class="bg-column col-medium">
        <div class="scroll-track">
          <img src="@/assets/images/intro/banhmi.webp" alt="Food">
          <img src="@/assets/images/intro/com.webp" alt="Food">
          <img src="@/assets/images/intro/banh.webp" alt="Food">
          <img src="@/assets/images/intro/goi.webp" alt="Food">
          <img src="@/assets/images/intro/banhmi.webp" alt="Food">
          <img src="@/assets/images/intro/com.webp" alt="Food">
          <img src="@/assets/images/intro/banh.webp" alt="Food">
          <img src="@/assets/images/intro/goi.webp" alt="Food">
        </div>
      </div>

      <div class="bg-column col-fast mobile-hidden">
        <div class="scroll-track reverse">
          <img src="@/assets/images/intro/bun.webp" alt="Food">
          <img src="@/assets/images/intro/goi.webp" alt="Food">
          <img src="@/assets/images/intro/banh.webp" alt="Food">
          <img src="@/assets/images/intro/com.webp" alt="Food">
          <img src="@/assets/images/intro/bun.webp" alt="Food">
          <img src="@/assets/images/intro/goi.webp" alt="Food">
          <img src="@/assets/images/intro/banh.webp" alt="Food">
          <img src="@/assets/images/intro/com.webp" alt="Food">
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
          Gia nhập cộng đồng <br> 
          <span class="text-gradient shine-effect">Ẩm Thực Tinh Hoa</span>
        </h1>
        
        <p class="description">
          Hơn <strong>100.000+ thành viên</strong> đang chia sẻ đặc quyền, công thức và niềm đam mê nấu nướng mỗi ngày. Đừng bỏ lỡ cơ hội tỏa sáng!
        </p>
        
        <div class="stats-grid">
          <div class="stat-item">
            <strong>15K+</strong>
            <span>Công thức</span>
          </div>
          <div class="stat-sep"></div>
          <div class="stat-item">
            <strong>1M+</strong>
            <span>Lượt yêu thích</span>
          </div>
          <div class="stat-sep"></div>
          <div class="stat-item">
            <strong>24/7</strong>
            <span>Hỗ trợ VIP</span>
          </div>
        </div>
      </div>

      <div class="form-side gsap-signup-right">
        <div class="auth-card glass-panel">
          
          <div class="auth-tabs" v-if="activeTab !== 'forgot-password'">
            <button class="tab-btn" :class="{ active: activeTab === 'login' }" @click="switchTab('login')">Đăng Nhập</button>
            <button class="tab-btn" :class="{ active: activeTab === 'register' }" @click="switchTab('register')">Đăng Ký</button>
          </div>

          <div class="form-content">
            <h3 class="form-title" v-if="activeTab !== 'forgot-password'">
              {{ activeTab === 'login' ? 'Chào mừng trở lại!' : 'Tạo tài khoản mới' }}
            </h3>

            <form @submit.prevent="handleSubmit">
              
              <div v-if="activeTab === 'register'" class="fade-in-anim">
                <div class="input-group">
                  <label>Tên đăng nhập</label>
                  <input v-model="registerForm.username" type="text" placeholder="VD: masterchef_vn" class="input-field" required />
                </div>
                <div class="input-group">
                  <label>Email</label>
                  <input v-model="registerForm.email" type="email" placeholder="name@example.com" class="input-field" required />
                </div>
                <div class="input-group">
                  <label>Mật khẩu</label>
                  <input v-model="registerForm.password" type="password" placeholder="Tối thiểu 6 ký tự" class="input-field" required />
                </div>
                <div class="input-group">
                  <label>Xác nhận mật khẩu</label>
                  <input v-model="registerForm.confirmPassword" type="password" placeholder="Nhập lại mật khẩu" class="input-field" required />
                </div>

                <div class="input-group-checkbox">
                  <label class="checkbox-container">
                    <input type="checkbox" v-model="registerForm.agreeTerms" required>
                    <span class="checkmark"></span>
                    <span class="label-text">
                      Tôi đồng ý với <router-link to="/terms-and-policy">Điều khoản</router-link> và <router-link to="/terms-and-policy">Chính sách bảo mật</router-link>
                    </span>
                  </label>
                </div>
              </div>

              <div v-else-if="activeTab === 'login'" class="fade-in-anim">
                <div class="input-group">
                  <label>Email</label>
                  <input v-model="loginForm.email" type="email" placeholder="name@example.com" class="input-field" required />
                </div>
                <div class="input-group">
                  <label>Mật khẩu</label>
                  <input v-model="loginForm.password" type="password" placeholder="Nhập mật khẩu..." class="input-field" required />
                </div>
                <div class="form-actions">
                  <label class="remember"><input type="checkbox"> Ghi nhớ tôi</label>
                  <a href="#" class="forgot-pass" @click.prevent="activeTab = 'forgot-password'; forgotState = 'idle'; forgotError = ''; forgotIdentifier = '';">Quên mật khẩu?</a>
                </div>

                <transition name="fade-slide" mode="out-in">
                  <div v-if="isBannedBoxVisible" key="banned-local" class="banned-alert-box">
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
                  <div v-else-if="loginError" key="error-local" class="auth-error-msg">
                    {{ loginError }}
                  </div>
                </transition>
                <transition name="fade-slide">
                  <div v-if="!isBannedBoxVisible && wrongPasswordCount >= 3" class="appeal-hint">
                    <button type="button" class="btn-appeal-link" @click.prevent="activeTab = 'forgot-password'; forgotState = 'idle'; forgotError = ''; forgotIdentifier = '';">
                      Quên mật khẩu? → Lấy lại mật khẩu
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
                  <h3 class="form-title" style="margin-top: 15px">Đã gửi Email</h3>
                  <p class="description" style="text-align: center; margin-bottom: 20px; font-size: 0.9rem; color: #1C1917">Nếu tài khoản có tồn tại thì chúng tôi sẽ gửi link đặt lại mật khẩu.</p>
                  <button type="button" class="btn-submit" @click="activeTab = 'login'">Quay lại Đăng nhập</button>
                </div>

                <div v-else>
                  <h3 class="form-title">Khôi phục mật khẩu</h3>
                  <p class="description" style="text-align: center; margin-bottom: 20px; font-size: 0.9rem; color: #78716C">Nhập email của bạn, chúng tôi sẽ gửi link khôi phục.</p>

                  <div class="input-group">
                    <label>Email của bạn</label>
                    <input v-model="forgotIdentifier" type="email" placeholder="name@example.com" class="input-field" required :disabled="forgotState === 'loading'" />
                  </div>

                  <div v-if="forgotError" class="auth-error-msg">{{ forgotError }}</div>

                  <button type="submit" class="btn-submit" :disabled="!forgotIdentifier.trim() || forgotState === 'loading'">
                    <span v-if="forgotState === 'loading'" class="spinner-border" role="status" aria-hidden="true"></span>
                    <span v-else>Gửi yêu cầu</span>
                  </button>

                  <button type="button" class="btn-back" @click="switchTab('login')">
                    ← Quay lại
                  </button>
                </div>
              </div>

              <button 
                v-if="activeTab !== 'forgot-password'"
                type="submit" 
                class="btn-submit" 
                :disabled="isLoading || (activeTab === 'register' && !registerForm.agreeTerms)"
              >
                <span v-if="isLoading" class="spinner-border" role="status" aria-hidden="true"></span>
                <span v-else>{{ activeTab === 'login' ? 'Đăng Nhập Ngay' : 'Đăng Ký Miễn Phí' }}</span>
              </button>

              <div class="divider" v-if="activeTab !== 'forgot-password'"><span>Hoặc tiếp tục với</span></div>
              
              <div class="social-buttons custom-google-wrapper" v-if="activeTab !== 'forgot-password'">
                <button type="button" class="social-btn google custom-ui-btn">
                  <img src="https://cdn-icons-png.flaticon.com/512/2991/2991148.png" width="20" alt="Google">
                  {{ activeTab === 'login' ? 'Đăng nhập với Google' : 'Đăng ký với Google' }}
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
          <p>© 2026 GoMet Elite. Privacy & Security First.</p>
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
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import * as authService from '@/services/authService'
import { GoogleLogin } from 'vue3-google-login'
import OtpModal from '@/components/modals/OtpModal.vue'
import AppealModal from '@/components/modals/AppealModal.vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

const router = useRouter()
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
  
  // 🔥 Đã FIX: Reset đếm lỗi sai pass khi chuyển tab
  if (tab === 'login') {
    wrongPasswordCount.value = 0;
  }
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

// Tự dọn dẹp lỗi khi khách hàng gõ chữ mới
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
  closeBannedAlert(); 
}

const processBannedError = (errData, errorMessage) => {
  isBannedBoxVisible.value = true;
  loginError.value = ''; // 🔥 Đã FIX: Ép rỗng để nhường hoàn toàn cho Banned Box

  const banReason = errData.banReason || 'Vi phạm chính sách và tiêu chuẩn cộng đồng GOMET.';
  const bannedBy = errData.bannedByName || 'Quản trị viên hệ thống';
  let timeStr = '';
  let rawTimeStr = '';
  
  if (errData.bannedAt) {
     const d = new Date(errData.bannedAt);
     rawTimeStr = `${d.toLocaleTimeString('vi-VN', {hour: '2-digit', minute:'2-digit'})} ngày ${d.toLocaleDateString('vi-VN')}`;
     timeStr = ` vào lúc ${rawTimeStr}`;
  }
  bannedDetails.value = { reason: banReason, by: bannedBy, time: rawTimeStr };
  toast.error(`🚨 Đăng nhập thất bại do tài khoản đã bị khóa!`, { timeout: 8000 })
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
      toast.success('Đã gửi Email khôi phục!')
    } catch (err) {
      forgotState.value = 'idle'
      // 🔥 Đã FIX: Móc đúng fullData
      forgotError.value = err.fullData?.message || err.response?.data?.message || 'Có lỗi xảy ra khi gửi yêu cầu.'
      toast.error(forgotError.value)
    }
    return
  }

  // Xử lý Đăng Nhập
  if (activeTab.value === 'login') {
    isLoading.value = true
    isBannedBoxVisible.value = false // 🔥 Đã FIX: Đảm bảo dọn hộp cũ trước khi gọi API mới
    
    try {
      const role = await authStore.login(loginForm.email, loginForm.password)
      toast.success('Đăng nhập thành công!')
      wrongPasswordCount.value = 0 // 🔥 Đã FIX: Đăng nhập thành công thì reset đếm
      sessionStorage.setItem('just_logged_in', 'true')
      router.push(role === 'admin' ? '/admin' : '/home')
    } catch (err) {
      // 🔥 Đã FIX: Móc đúng fullData
      const errData = err.fullData || err.response?.data || {}
      const backendMsg = errData.message || err.message || ''
      const errorString = backendMsg.toUpperCase()
      
      if (errorString.includes('ACCOUNT_BANNED') || errorString.includes('BANNED') || err.status === 403) {
        processBannedError(errData, backendMsg)
      } else {
        wrongPasswordCount.value++
        loginError.value = backendMsg || 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.'
        toast.error(loginError.value)
      }
    } finally {
      isLoading.value = false
    }

  // Xử lý Đăng Ký
  } else if (activeTab.value === 'register') {
    if (!registerForm.agreeTerms) {
      toast.warn('Vui lòng đồng ý với điều khoản dịch vụ!')
      return
    }
    if (registerForm.password !== registerForm.confirmPassword) {
      toast.warn('Mật khẩu xác nhận không khớp!')
      return
    }
    isLoading.value = true
    try {
      await authService.sendOtp(registerForm.username, registerForm.email, registerForm.password)
      toast.success('Mã xác nhận đã được gửi vào email của bạn!')
      otpCode.value = ''
      showOtpModal.value = true
    } catch (err) {
      // 🔥 Đã FIX: Móc đúng fullData
      toast.error(err.fullData?.message || err.response?.data?.message || 'Có lỗi xảy ra khi gửi OTP.')
    } finally {
      isLoading.value = false
    }
  }
}

const handleVerifyOtp = async () => {
  if (otpCode.value.length < 6) {
    toast.warn('Vui lòng nhập đủ 6 số OTP!')
    return
  }
  try {
    const data = await authService.verifyOtp(registerForm.email, otpCode.value)
    authStore.setUser(data)
    
    toast.success('Đăng ký thành công! Chào mừng bạn đến với GoMet.')
    showOtpModal.value = false
    sessionStorage.setItem('just_logged_in', 'true')
    router.push('/home')
  } catch (err) {
    // 🔥 Đã FIX: Móc đúng fullData
    toast.error(err.fullData?.message || err.response?.data?.message || 'Mã xác nhận không đúng hoặc đã hết hạn.')
  }
}

const handleGoogleCallback = async (response) => {
  loginError.value = ''
  isBannedBoxVisible.value = false // 🔥 Đã FIX: Dọn dẹp trước khi gọi
  
  try {
    if (!response || !response.credential) {
      throw new Error("Không nhận được token từ Google");
    }

    const idToken = response.credential;
    const data = await authService.googleLogin(idToken);
    authStore.setUser(data);

    toast.success('Đăng nhập Google thành công!')
    sessionStorage.setItem('just_logged_in', 'true')
    router.push(authStore.user.role === 'admin' ? '/admin' : '/home')

  } catch (err) {
    console.error("Google Login Error:", err)
    
    // 🔥 Đã FIX: Móc đúng fullData
    const errData = err.fullData || err.response?.data || err?.data || err || {}
    const errorMessage = errData.message || err.message || String(err)
    const errorString = errorMessage.toUpperCase()

    if (errorString.includes('ACCOUNT_BANNED') || errorString.includes('BANNED') || err.status === 403 || errData.status === 403) {
      processBannedError(errData, errorMessage);
    } else {
      loginError.value = errorMessage || 'Lỗi đăng nhập bằng Google. Vui lòng thử lại.'
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