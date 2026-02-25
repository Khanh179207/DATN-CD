<template>
  <section class="signup-section" id="section-dang-ky">
    
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

      <div class="bg-column col-fast offset-down mobile-hidden">
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

    <div class="container main-layout">
      
      <div class="info-side fade-in-left">
        <div class="brand-header">
          <div class="logo-text">GOMET</div>
          <div class="logo-dot"></div>
        </div>
        
        <h1 class="main-title">
          Gia nhập cộng đồng <br> 
          <span class="highlight-text">Ẩm thực lớn nhất</span>
        </h1>
        
        <p class="description">
          Hơn <strong>50.000+ thành viên</strong> đang chia sẻ công thức, mẹo vặt và niềm đam mê nấu nướng mỗi ngày. Đừng bỏ lỡ cơ hội tỏa sáng!
        </p>
        
        <div class="stats-grid">
          <div class="stat-item">
            <strong>10K+</strong>
            <span>Công thức</span>
          </div>
          <div class="stat-item">
            <strong>1M+</strong>
            <span>Lượt yêu thích</span>
          </div>
          <div class="stat-item">
            <strong>24/7</strong>
            <span>Hỗ trợ</span>
          </div>
        </div>
      </div>

      <div class="form-side fade-in-right">
        <div class="auth-card push-down">
          
          <div class="auth-tabs">
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'login' }" 
              @click="activeTab = 'login'"
            >
              Đăng Nhập
            </button>
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'register' }" 
              @click="activeTab = 'register'"
            >
              Đăng Ký
            </button>
          </div>

          <div class="form-content">
            <h3 class="form-title">
              {{ activeTab === 'login' ? 'Chào mừng trở lại!' : 'Tạo tài khoản mới' }}
            </h3>

            <form @submit.prevent="handleSubmit">
              
              <div v-if="activeTab === 'register'" class="fade-in-anim">
                <div class="input-group">
                  <label>Tên đăng nhập</label>
                  <input v-model="registerForm.username" type="text" placeholder="VD: khanhnguyen_dev" class="input-field" required />
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
                  <label>Email hoặc Tên đăng nhập</label>
                  <input type="text" placeholder="name@example.com" class="input-field" />
                </div>
                <div class="input-group">
                  <label>Mật khẩu</label>
                  <input type="password" placeholder="Nhập mật khẩu..." class="input-field" />
                </div>
                <div class="form-actions">
                  <label class="remember">
                    <input type="checkbox"> Ghi nhớ tôi
                  </label>
                  <a href="#" class="forgot-pass">Quên mật khẩu?</a>
                </div>
              </div>

              <button type="submit" class="btn-submit">
                {{ activeTab === 'login' ? 'Đăng Nhập Ngay' : 'Đăng Ký Miễn Phí' }}
              </button>

              <div class="divider"><span>Hoặc tiếp tục với</span></div>
              
              <div class="social-buttons">
                <button type="button" class="social-btn google" @click="loginWithGoogle">
                  <svg class="social-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M21.35 11.1h-9.17v2.73h6.51c-.33 3.81-3.5 5.44-6.5 5.44C8.36 19.27 5 16.25 5 12c0-4.1 3.2-7.27 7.2-7.27 3.09 0 4.9 1.97 4.9 1.97L19 4.72S16.56 2 12.1 2C6.42 2 2.03 6.8 2.03 12c0 5.05 4.13 10 10.22 10 5.38 0 9.25-4.04 9.25-9.51 0-.48-.07-1.11-.15-1.39z"/>
                  </svg>
                  Đăng nhập với Google
                </button>
              </div>

            </form>
          </div>
        </div>
        
        <div class="form-footer">
          <p>© 2026 Gomet. Bảo mật tuyệt đối.</p>
        </div>
      </div>

    </div>

    <Teleport to="body">
      <OtpModal 
        v-if="showOtpModal"
        :email="registerForm.email"
        v-model="otpCode"
        @close="showOtpModal = false"
        @verify="handleVerifyOtp"
      />
    </Teleport>

  </section>
</template>

<script setup>
import { ref, reactive } from 'vue'
import OtpModal from '@/components/modals/OtpModal.vue' 

const activeTab = ref('login')
const showOtpModal = ref(false)
const otpCode = ref('')

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const handleSubmit = () => {
  if (activeTab.value === 'login') {
    alert('Đang xử lý đăng nhập...')
  } else {
    if (registerForm.password !== registerForm.confirmPassword) {
      alert('Mật khẩu xác nhận không khớp!')
      return
    }
    showOtpModal.value = true
  }
}

const loginWithGoogle = () => {
  alert('Đang chuyển hướng đến Google Login...')
}

const handleVerifyOtp = () => {
  if (otpCode.value.length < 6) {
    alert('Mã OTP chưa đủ 6 số!')
    return
  }
  alert('Đăng ký thành công! Chào mừng ' + registerForm.username)
  showOtpModal.value = false
  activeTab.value = 'login'
  otpCode.value = ''
}
</script>

<style scoped>
/* --- LAYOUT CHÍNH --- */
.signup-section {
  min-height: 100vh;
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden;
  padding: 60px 20px;
  font-family: 'Quicksand', sans-serif;
  background-color: #1C1917; /* Màu nền dự phòng khi ảnh chưa load */
}

/* --- BACKGROUND LAYER (ẢNH CHẠY) --- */
.background-layer {
  position: absolute; /* Thay fixed bằng absolute để chỉ nằm trong section này */
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 0;
  display: flex; gap: 20px; padding: 0 20px;
  /* Mask làm mờ 2 đầu */
  -webkit-mask-image: linear-gradient(to bottom, transparent, black 15%, black 85%, transparent);
  mask-image: linear-gradient(to bottom, transparent, black 15%, black 85%, transparent);
}

.bg-overlay {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.85); /* Tăng độ tối lên 0.85 để nền chìm hẳn xuống */
  backdrop-filter: blur(4px);
  z-index: 1;
}

.bg-column { flex: 1; position: relative; overflow: hidden; }
.offset-down { margin-top: -100px; }

.scroll-track { display: flex; flex-direction: column; gap: 20px; animation: scrollUp 40s linear infinite; }
.scroll-track.reverse { animation: scrollDown 45s linear infinite; }

.col-slow .scroll-track { animation-duration: 60s; }
.col-medium .scroll-track { animation-duration: 50s; }
.col-fast .scroll-track { animation-duration: 40s; }

.scroll-track img {
  width: 100%; border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.3); opacity: 0.6; display: block;
}

@keyframes scrollUp { 0% { transform: translateY(0); } 100% { transform: translateY(-50%); } }
@keyframes scrollDown { 0% { transform: translateY(-50%); } 100% { transform: translateY(0); } }

/* --- CONTENT CHÍNH --- */
.container { 
  width: 100%; max-width: 1100px; margin: 0 auto; 
  position: relative; z-index: 2; /* Nổi lên trên background */
}
.main-layout { display: flex; align-items: center; justify-content: space-between; gap: 80px; }

/* --- INFO SIDE (LEFT) --- */
.info-side { flex: 1; color: white; }
.brand-header { display: flex; align-items: baseline; gap: 5px; margin-bottom: 20px; }
.logo-text { font-family: 'Playfair Display', serif; font-size: 1.5rem; font-weight: 700; letter-spacing: 2px; }
.logo-dot { width: 8px; height: 8px; background: #F97316; border-radius: 50%; }

.main-title {
  font-family: 'Playfair Display', serif; font-size: 3.5rem; line-height: 1.4; margin-bottom: 20px;
}
.highlight-text { color: #F97316; font-style: italic; }

.description { font-size: 1.1rem; color: #D6D3D1; line-height: 1.6; margin-bottom: 40px; max-width: 450px; }
.stats-grid { display: flex; gap: 40px; border-top: 1px solid #57534E; padding-top: 30px; }
.stat-item strong { display: block; font-size: 2rem; color: #F97316; font-family: 'Playfair Display', serif; }
.stat-item span { color: #A8A29E; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 1px; }

/* --- FORM SIDE (RIGHT) --- */
.form-side { flex: 0 0 450px; width: 450px; }
.push-down { margin-top: 50px; }

.auth-card { background: white; border-radius: 24px; padding: 24px 20px; box-shadow: 0 20px 60px rgba(0,0,0,0.5); }

.auth-tabs { display: flex; background: #F5F5F4; border-radius: 12px; padding: 4px; margin-bottom: 18px; }
.tab-btn { flex: 1; border: none; background: transparent; padding: 8px; font-weight: 600; color: #78716C; cursor: pointer; border-radius: 10px; transition: 0.2s; font-family: 'Quicksand', sans-serif; font-size: 0.9rem; }
.tab-btn.active { background: white; color: #1C1917; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.form-title { font-size: 1.25rem; color: #1C1917; margin-bottom: 18px; font-weight: 700; text-align: center; }

/* Inputs & Buttons giữ nguyên như cũ */
.input-group { margin-bottom: 12px; }
.input-group label { display: block; font-size: 0.8rem; color: #44403C; margin-bottom: 5px; font-weight: 600; }
.input-field { width: 100%; padding: 10px 12px; border: 1px solid #E7E5E4; border-radius: 10px; outline: none; font-size: 0.9rem; color: #1C1917; transition: 0.2s; background: #FAFAF9; }
.input-field:focus { border-color: #F97316; background: white; box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1); }

.form-actions { display: flex; justify-content: space-between; align-items: center; font-size: 0.8rem; margin-bottom: 18px; }
.forgot-pass { color: #F97316; text-decoration: none; font-weight: 600; }
.remember { color: #57534E; cursor: pointer; display: flex; align-items: center; gap: 5px; }

.btn-submit { width: 100%; padding: 12px; border: none; border-radius: 12px; background: #1C1917; color: white; font-weight: 700; font-size: 0.95rem; cursor: pointer; transition: 0.3s; }
.btn-submit:hover { background: #F97316; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(249, 115, 22, 0.3); }

.divider { display: flex; align-items: center; text-align: center; color: #A8A29E; font-size: 0.75rem; margin: 18px 0; }
.divider::before, .divider::after { content: ''; flex: 1; border-bottom: 1px solid #E7E5E4; }
.divider span { padding: 0 8px; }

.social-buttons { display: flex; gap: 15px; }
.social-btn { width: 100%; padding: 10px; background: white; border: 1px solid #E7E5E4; border-radius: 12px; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px; transition: 0.2s; color: #1C1917; font-weight: 600; font-size: 0.9rem; font-family: 'Quicksand', sans-serif; }
.social-icon { width: 18px; height: 18px; color: #1C1917; transition: 0.2s; }
.social-btn:hover { background: #1C1917; color: white; border-color: #1C1917; }
.social-btn:hover .social-icon { color: white; }

.form-footer { text-align: center; margin-top: 15px; font-size: 0.75rem; color: #E7E5E4; opacity: 0.8; }

/* Responsive */
@media (max-width: 900px) {
  .main-layout { flex-direction: column; gap: 50px; }
  .info-side { text-align: center; }
  .stats-grid { justify-content: center; }
  .description { margin: 0 auto 40px; }
  .form-side { width: 100%; max-width: 450px; }
  .push-down { margin-top: 0; }
  .mobile-hidden { display: none; }
}
</style>