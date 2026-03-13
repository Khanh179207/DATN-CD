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
          
          <div class="auth-tabs">
            <button class="tab-btn" :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">Đăng Nhập</button>
            <button class="tab-btn" :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">Đăng Ký</button>
          </div>

          <div class="form-content">
            <h3 class="form-title">
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
              </div>

              <div v-else class="fade-in-anim">
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
                  <a href="#" class="forgot-pass" @click.prevent="toast.info('Tính năng đang phát triển!')">Quên mật khẩu?</a>
                </div>
              </div>

              <button type="submit" class="btn-submit" :disabled="isLoading">
                <span v-if="isLoading" class="spinner-border" role="status" aria-hidden="true"></span>
                <span v-else>{{ activeTab === 'login' ? 'Đăng Nhập Ngay' : 'Đăng Ký Miễn Phí' }}</span>
              </button>

              <div class="divider"><span>Hoặc tiếp tục với</span></div>
              
              <div class="social-buttons">
                <GoogleLogin :callback="handleGoogleCallback" prompt :auto-login="false" :use-fedcm="false" class="google-wrapper">
                  <button type="button" class="social-btn google">
                    <img src="https://cdn-icons-png.flaticon.com/512/2991/2991148.png" width="20" alt="Google">
                    {{ activeTab === 'login' ? 'Google' : 'Google' }}
                  </button>
                </GoogleLogin>
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
const registerForm = reactive({ username: '', email: '', password: '', confirmPassword: '' })

// Event Listener cho header
const switchTabListener = (event) => {
  if (event.detail === 'login' || event.detail === 'register') {
    activeTab.value = event.detail
  }
}

onMounted(() => {
  window.addEventListener('switch-auth-tab', switchTabListener)
  
  // GSAP Animation đồng bộ
  let ctx = gsap.context(() => {
    gsap.from(".gsap-signup-left", {
      scrollTrigger: { trigger: signupSection.value, start: "top 80%" },
      x: -50, opacity: 0, duration: 1, ease: "power3.out"
    });
    gsap.from(".gsap-signup-right", {
      scrollTrigger: { trigger: signupSection.value, start: "top 80%" },
      x: 50, opacity: 0, duration: 1, ease: "power3.out", delay: 0.2
    });
    // Orbs ẩn hiện chậm
    gsap.to(".orb-1", { opacity: 0.2, x: "10vw", duration: 8, repeat: -1, yoyo: true, ease: "sine.inOut" });
    gsap.to(".orb-2", { opacity: 0.1, x: "-10vw", duration: 10, repeat: -1, yoyo: true, ease: "sine.inOut" });
  }, signupSection.value);
})

onUnmounted(() => {
  window.removeEventListener('switch-auth-tab', switchTabListener)
})


// --- CÁC HÀM XỬ LÝ AUTH GIỮ NGUYÊN BẢN CŨ CỦA SẾP ---
const handleSubmit = async () => {
  if (activeTab.value === 'login') {
    isLoading.value = true
    try {
      const role = await authStore.login(loginForm.email, loginForm.password)
      toast.success('Đăng nhập thành công!')
      router.push(role === 'admin' ? '/admin' : '/home')
    } catch (err) {
      const raw = err.message || ''
      if (raw === 'ACCOUNT_BANNED') {
        toast.error('Tài khoản của bạn đã bị khóa bởi quản trị viên.')
      } else {
        toast.error(raw || 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.')
      }
    } finally {
      isLoading.value = false
    }

  } else {
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
    router.push('/home')
  } catch (err) {
    toast.error(err.response?.data?.message || 'Mã xác nhận không đúng hoặc đã hết hạn.')
  }
}

const handleGoogleCallback = async (response) => {
  try {
    if (!response || !response.credential) {
      throw new Error("Không nhận được token từ Google")
    }

    const idToken = response.credential
    const data = await authService.googleLogin(idToken)

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
    router.push(authStore.user.role === 'admin' ? '/admin' : '/home')

  } catch (err) {
    console.error("Google Login Error:", err)
    toast.error(err.response?.data?.message || err.message || 'Lỗi đăng nhập bằng Google. Vui lòng thử lại.')
  }
}
</script>

<style scoped lang="scss" src="./LandingSignupSection.scss"></style>