<template>
  <section class="signup-section" id="sectionsigninlanding" ref="signupSection">
    
    <div class="background-layer">
      <div class="bg-overlay"></div>
      
      <div class="bg-column col-slow">
        <div class="scroll-track">
          <img src="@/assets/images/intro/bun.jpg" alt="Food">
          <img src="@/assets/images/intro/bunbo.jpg" alt="Food">
          <img src="@/assets/images/intro/banhmi.jpg" alt="Food">
          <img src="@/assets/images/intro/com.jpg" alt="Food">
          <img src="@/assets/images/intro/bun.jpg" alt="Food">
          <img src="@/assets/images/intro/bunbo.jpg" alt="Food">
          <img src="@/assets/images/intro/banhmi.jpg" alt="Food">
          <img src="@/assets/images/intro/com.jpg" alt="Food">
        </div>
      </div>

      <div class="bg-column col-fast offset-down">
        <div class="scroll-track reverse">
          <img src="@/assets/images/intro/banh.jpg" alt="Food">
          <img src="@/assets/images/intro/goi.jpg" alt="Food">
          <img src="@/assets/images/intro/bun.jpg" alt="Food">
          <img src="@/assets/images/intro/bunbo.jpg" alt="Food">
          <img src="@/assets/images/intro/banh.jpg" alt="Food">
          <img src="@/assets/images/intro/goi.jpg" alt="Food">
          <img src="@/assets/images/intro/bun.jpg" alt="Food">
          <img src="@/assets/images/intro/bunbo.jpg" alt="Food">
        </div>
      </div>

      <div class="bg-column col-medium">
        <div class="scroll-track">
          <img src="@/assets/images/intro/banhmi.jpg" alt="Food">
          <img src="@/assets/images/intro/com.jpg" alt="Food">
          <img src="@/assets/images/intro/banh.jpg" alt="Food">
          <img src="@/assets/images/intro/goi.jpg" alt="Food">
          <img src="@/assets/images/intro/banhmi.jpg" alt="Food">
          <img src="@/assets/images/intro/com.jpg" alt="Food">
          <img src="@/assets/images/intro/banh.jpg" alt="Food">
          <img src="@/assets/images/intro/goi.jpg" alt="Food">
        </div>
      </div>

      <div class="bg-column col-fast mobile-hidden">
        <div class="scroll-track reverse">
          <img src="@/assets/images/intro/bun.jpg" alt="Food">
          <img src="@/assets/images/intro/goi.jpg" alt="Food">
          <img src="@/assets/images/intro/banh.jpg" alt="Food">
          <img src="@/assets/images/intro/com.jpg" alt="Food">
          <img src="@/assets/images/intro/bun.jpg" alt="Food">
          <img src="@/assets/images/intro/goi.jpg" alt="Food">
          <img src="@/assets/images/intro/banh.jpg" alt="Food">
          <img src="@/assets/images/intro/com.jpg" alt="Food">
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
            <button class="tab-btn" :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">Đăng Nhập</button>
            <button class="tab-btn" :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">Đăng Ký</button>
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
                  <p class="description" style="text-align: center; margin-bottom: 20px; font-size: 0.9rem; color: #1C1917">Vui lòng kiểm tra email của bạn để lấy link đặt lại mật khẩu.</p>
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

                  <button type="button" class="btn-back" @click="activeTab = 'login'">
                    ← Quay lại
                  </button>
                </div>
              </div>

<button 
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
    </Teleport>

  </section>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import * as authService from '@/services/authService'
import { GoogleLogin } from 'vue3-google-login'
import OtpModal from '@/components/modals/OtpModal.vue'
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

const switchTabListener = (event) => {
  if (event.detail === 'login' || event.detail === 'register') {
    activeTab.value = event.detail
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
      forgotError.value = err.response?.data?.message || 'Có lỗi xảy ra khi gửi yêu cầu.'
      toast.error(forgotError.value)
    }
    return
  }

  // Xử lý Đăng Nhập
  if (activeTab.value === 'login') {
    isLoading.value = true
    try {
      const role = await authStore.login(loginForm.email, loginForm.password)
      toast.success('Đăng nhập thành công!')
      
      // 🔥 BẮT BUỘC THÊM DÒNG NÀY: Báo cho MainLayout biết là tao vừa login xong, hiện Loading đi!
      sessionStorage.setItem('just_logged_in', 'true')
      
      router.push(role === 'admin' ? '/admin' : '/home')
    } catch (err) {
      const backendMsg = err.response?.data?.message || err.message || ''
      if (backendMsg.includes('ACCOUNT_BANNED')) {
        toast.error('🚨 TÀI KHOẢN BỊ KHÓA: Bạn đã bị cấm vĩnh viễn do vi phạm tiêu chuẩn cộng đồng GOMET!', { timeout: 8000 })
      } else {
        toast.error(backendMsg || 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.')
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
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra khi gửi OTP.')
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
    
    authStore.user = {
      id:        data.accountID,
      accountID: data.accountID,
      name:      data.username,
      username:  data.username,
      email:     data.email,
      avatar:    data.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(data.username)}&background=EA580C&color=fff`,
      isAdmin:   data.isAdmin,
      isPremium: data.isPremium,
      token:     data.token,
      role:      data.isAdmin ? 'admin' : 'user'
    }
    localStorage.setItem('user', JSON.stringify(authStore.user))
    
    toast.success('Đăng ký thành công! Chào mừng bạn đến với GoMet.')
    showOtpModal.value = false
    
    // 🔥 THÊM Ở ĐÂY LUÔN CHO CHẮC: Đăng ký thành công cũng coi như vừa login
    sessionStorage.setItem('just_logged_in', 'true')
    
    router.push('/home')
  } catch (err) {
    toast.error(err.response?.data?.message || 'Mã xác nhận không đúng hoặc đã hết hạn.')
  }
}

const handleGoogleCallback = async (response) => {
  try {
    if (!response || !response.credential) {
      throw new Error("Không nhận được token từ Google");
    }

    const idToken = response.credential;
    const data = await authService.googleLogin(idToken);

    authStore.user = {
      id:        data.accountID,
      accountID: data.accountID,
      name:      data.username,
      username:  data.username,
      email:     data.email,
      avatar:    data.avatar,
      isAdmin:   data.isAdmin,
      isPremium: data.isPremium,
      token:     data.token,
      role:      data.isAdmin ? 'admin' : 'user'
    }

    localStorage.setItem('user', JSON.stringify(authStore.user))

    toast.success('Đăng nhập Google thành công!')
    
    // 🔥 VÀ THÊM Ở ĐÂY NỮA (Google Login)
    sessionStorage.setItem('just_logged_in', 'true')
    
    router.push(authStore.user.role === 'admin' ? '/admin' : '/home')

  } catch (err) {
    console.error("Google Login Error:", err)
    const backendMsg = err.response?.data?.message || err.message || ''
    
    if (backendMsg.includes('ACCOUNT_BANNED')) {
      toast.error('🚨 TÀI KHOẢN BỊ KHÓA: Bạn không thể đăng nhập bằng Google vì tài khoản này đã bị Ban!', { timeout: 8000 })
    } else {
      toast.error(backendMsg || 'Lỗi đăng nhập bằng Google. Vui lòng thử lại.')
    }
  }
}
</script>

<style scoped lang="scss" src="./LandingSignupSection.scss"></style>