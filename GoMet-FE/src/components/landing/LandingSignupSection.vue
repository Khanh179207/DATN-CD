<template>
  <section class="signup-section" id="section-dang-ky">
    <div class="bg-overlay"></div>

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
import OtpModal from '@/components/modals/OtpModal.vue' // Đảm bảo đường dẫn đúng

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
    // Chỉ hiện OTP khi nhấn nút "Đăng Ký Miễn Phí" (Submit Form)
    showOtpModal.value = true
  }
}

// Hàm xử lý riêng cho Google
const loginWithGoogle = () => {
  // Logic đăng nhập Google (Không cần OTP)
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
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;800&family=Quicksand:wght@500;600;700&display=swap');

.signup-section {
  min-height: 100vh;
  display: flex; align-items: center; justify-content: center;
  background-image: url('https://images.unsplash.com/photo-1556910103-1c02745a30bf?q=80&w=2070&auto=format&fit=crop'); 
  background-size: cover;
  background-position: center;
  padding: 60px 20px;
  font-family: 'Quicksand', sans-serif;
  position: relative; overflow: hidden;
}

.bg-overlay {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.75);
  z-index: 1;
}

.container { 
  width: 100%; max-width: 1100px; margin: 0 auto; 
  position: relative; z-index: 2;
}
.main-layout { display: flex; align-items: center; justify-content: space-between; gap: 80px; }

/* --- INFO SIDE (LEFT) --- */
.info-side { flex: 1; color: white; }
.brand-header { display: flex; align-items: baseline; gap: 5px; margin-bottom: 20px; }
.logo-text { font-family: 'Playfair Display', serif; font-size: 1.5rem; font-weight: 700; letter-spacing: 2px; }
.logo-dot { width: 8px; height: 8px; background: #F97316; border-radius: 50%; }

/* CẬP NHẬT 1: Tăng line-height lên 1.4 để tách dòng */
.main-title {
  font-family: 'Playfair Display', serif; 
  font-size: 3.5rem; 
  line-height: 1.4; /* Đã tăng từ 1.1 lên 1.4 */
  margin-bottom: 20px;
}
.highlight-text { color: #F97316; font-style: italic; }

.description { font-size: 1.1rem; color: #D6D3D1; line-height: 1.6; margin-bottom: 40px; max-width: 450px; }
.stats-grid { display: flex; gap: 40px; border-top: 1px solid #57534E; padding-top: 30px; }
.stat-item strong { display: block; font-size: 2rem; color: #F97316; font-family: 'Playfair Display', serif; }
.stat-item span { color: #A8A29E; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 1px; }

/* --- FORM SIDE (RIGHT) --- */
.form-side { flex: 0 0 450px; width: 450px; }

/* CẬP NHẬT 2: Đẩy form xuống thấp hơn */
.push-down {
  margin-top: 50px; /* Kéo khung xuống thấp 50px */
}

.auth-card { background: white; border-radius: 24px; padding: 24px 20px; box-shadow: 0 20px 60px rgba(0,0,0,0.5); }

.auth-tabs { display: flex; background: #F5F5F4; border-radius: 12px; padding: 4px; margin-bottom: 18px; }
.tab-btn { flex: 1; border: none; background: transparent; padding: 8px; font-weight: 600; color: #78716C; cursor: pointer; border-radius: 10px; transition: 0.2s; font-family: 'Quicksand', sans-serif; font-size: 0.9rem; }
.tab-btn.active { background: white; color: #1C1917; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.form-title { font-size: 1.25rem; color: #1C1917; margin-bottom: 18px; font-weight: 700; text-align: center; }

/* Inputs */
.input-group { margin-bottom: 12px; }
.input-group label { display: block; font-size: 0.8rem; color: #44403C; margin-bottom: 5px; font-weight: 600; }
.input-field { width: 100%; padding: 10px 12px; border: 1px solid #E7E5E4; border-radius: 10px; outline: none; font-size: 0.9rem; color: #1C1917; transition: 0.2s; background: #FAFAF9; }
.input-field:focus { border-color: #F97316; background: white; box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1); }

.form-actions { display: flex; justify-content: space-between; align-items: center; font-size: 0.8rem; margin-bottom: 18px; }
.forgot-pass { color: #F97316; text-decoration: none; font-weight: 600; }
.remember { color: #57534E; cursor: pointer; display: flex; align-items: center; gap: 5px; }

/* Submit Button */
.btn-submit { width: 100%; padding: 12px; border: none; border-radius: 12px; background: #1C1917; color: white; font-weight: 700; font-size: 0.95rem; cursor: pointer; transition: 0.3s; }
.btn-submit:hover { background: #F97316; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(249, 115, 22, 0.3); }

/* Divider */
.divider { display: flex; align-items: center; text-align: center; color: #A8A29E; font-size: 0.75rem; margin: 18px 0; }
.divider::before, .divider::after { content: ''; flex: 1; border-bottom: 1px solid #E7E5E4; }
.divider span { padding: 0 8px; }

/* Social Buttons */
.social-buttons { display: flex; gap: 15px; }
/* Nút Google full width vì chỉ còn 1 nút */
.social-btn {
  width: 100%; 
  padding: 10px; background: white; 
  border: 1px solid #E7E5E4; border-radius: 12px;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px;
  transition: 0.2s; 
  color: #1C1917; font-weight: 600; font-size: 0.9rem; font-family: 'Quicksand', sans-serif;
}
.social-icon { width: 18px; height: 18px; color: #1C1917; transition: 0.2s; }

.social-btn:hover { background: #1C1917; color: white; border-color: #1C1917; }
.social-btn:hover .social-icon { color: white; }

.form-footer { text-align: center; margin-top: 15px; font-size: 0.75rem; color: #E7E5E4; opacity: 0.8; }

/* Animation và Responsive giữ nguyên */
.fade-in-left { animation: fadeInLeft 1s ease-out; }
.fade-in-right { animation: fadeInRight 1s ease-out; }
.fade-in-anim { animation: fadeIn 0.3s ease-out; }
@keyframes fadeInLeft { from { opacity: 0; transform: translateX(-30px); } to { opacity: 1; transform: translateX(0); } }
@keyframes fadeInRight { from { opacity: 0; transform: translateX(30px); } to { opacity: 1; transform: translateX(0); } }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 900px) {
  .main-layout { flex-direction: column; gap: 50px; }
  .info-side { text-align: center; }
  .stats-grid { justify-content: center; }
  .description { margin: 0 auto 40px; }
  .form-side { width: 100%; max-width: 450px; }
  .push-down { margin-top: 0; } /* Mobile thì không cần đẩy xuống */
}
</style>