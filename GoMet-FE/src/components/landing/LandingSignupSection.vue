<template>
  <section class="signup-section" id="section-dang-ky">
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
        <div class="auth-card">
          
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

            <form @submit.prevent>
              
              <div v-if="activeTab === 'register'" class="fade-in-anim">
                <div class="input-group">
                  <label>Tên hiển thị</label>
                  <input type="text" placeholder="VD: Khánh Nguyễn" class="input-field" />
                </div>
                <div class="input-group">
                  <label>Email</label>
                  <input type="email" placeholder="name@example.com" class="input-field" />
                </div>
                <div class="input-group">
                  <label>Mật khẩu</label>
                  <input type="password" placeholder="Tối thiểu 6 ký tự" class="input-field" />
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

              <button class="btn-submit">
                {{ activeTab === 'login' ? 'Đăng Nhập Ngay' : 'Đăng Ký Miễn Phí' }}
              </button>

              <div class="divider"><span>Hoặc tiếp tục với</span></div>
              
              <div class="social-buttons">
                <button class="social-btn google">
                  <img src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg" width="20"/> Google
                </button>
                <button class="social-btn facebook">
                  <img src="https://upload.wikimedia.org/wikipedia/commons/b/b8/2021_Facebook_icon.svg" width="20"/> Facebook
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
  </section>
</template>

<script setup>
import { ref } from 'vue'
const activeTab = ref('login')
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;800&family=Quicksand:wght@500;600;700&display=swap');

.signup-section {
  min-height: 100vh;
  display: flex; align-items: center; justify-content: center;
  background-color: #1C1917; /* Nền tối sang trọng */
  padding: 60px 20px;
  font-family: 'Quicksand', sans-serif;
  position: relative; overflow: hidden;
}

/* Background Effect (Optional) */
.signup-section::before {
  content: ''; position: absolute; top: -100px; right: -100px; width: 400px; height: 400px;
  background: radial-gradient(circle, rgba(249,115,22,0.15) 0%, transparent 70%);
  border-radius: 50%; pointer-events: none;
}

.container { width: 100%; max-width: 1100px; margin: 0 auto; }
.main-layout { display: flex; align-items: center; justify-content: space-between; gap: 80px; }

/* --- INFO SIDE (LEFT) --- */
.info-side { flex: 1; color: white; }

.brand-header { display: flex; align-items: baseline; gap: 5px; margin-bottom: 20px; }
.logo-text { font-family: 'Playfair Display', serif; font-size: 1.5rem; font-weight: 700; letter-spacing: 2px; }
.logo-dot { width: 8px; height: 8px; background: #F97316; border-radius: 50%; }

.main-title {
  font-family: 'Playfair Display', serif; font-size: 3.5rem; line-height: 1.1; margin-bottom: 20px;
}
.highlight-text { color: #F97316; font-style: italic; }

.description { font-size: 1.1rem; color: #A8A29E; line-height: 1.6; margin-bottom: 40px; max-width: 450px; }

.stats-grid { display: flex; gap: 40px; border-top: 1px solid #44403C; padding-top: 30px; }
.stat-item strong { display: block; font-size: 2rem; color: #F97316; font-family: 'Playfair Display', serif; }
.stat-item span { color: #78716C; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 1px; }

/* --- FORM SIDE (RIGHT) --- */
.form-side { flex: 0 0 450px; width: 450px; }

.auth-card {
  background: white; border-radius: 24px; padding: 30px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.4);
}

.auth-tabs {
  display: flex; background: #F5F5F4; border-radius: 12px; padding: 4px; margin-bottom: 25px;
}
.tab-btn {
  flex: 1; border: none; background: transparent; padding: 10px; font-weight: 600; color: #78716C;
  cursor: pointer; border-radius: 10px; transition: 0.2s; font-family: 'Quicksand', sans-serif;
}
.tab-btn.active { background: white; color: #1C1917; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }

.form-title { font-size: 1.5rem; color: #1C1917; margin-bottom: 25px; font-weight: 700; text-align: center; }

/* Inputs */
.input-group { margin-bottom: 18px; }
.input-group label { display: block; font-size: 0.85rem; color: #44403C; margin-bottom: 6px; font-weight: 600; }
.input-field {
  width: 100%; padding: 12px 15px; border: 1px solid #E7E5E4; border-radius: 10px; outline: none;
  font-size: 0.95rem; color: #1C1917; transition: 0.2s; background: #FAFAF9;
}
.input-field:focus { border-color: #F97316; background: white; box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1); }

.form-actions { display: flex; justify-content: space-between; align-items: center; font-size: 0.85rem; margin-bottom: 25px; }
.forgot-pass { color: #F97316; text-decoration: none; font-weight: 600; }
.remember { color: #57534E; cursor: pointer; display: flex; align-items: center; gap: 5px; }

/* Submit Button */
.btn-submit {
  width: 100%; padding: 14px; border: none; border-radius: 12px;
  background: #1C1917; color: white; font-weight: 700; font-size: 1rem;
  cursor: pointer; transition: 0.3s;
}
.btn-submit:hover { background: #F97316; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(249, 115, 22, 0.3); }

/* Divider */
.divider { display: flex; align-items: center; text-align: center; color: #A8A29E; font-size: 0.8rem; margin: 25px 0; }
.divider::before, .divider::after { content: ''; flex: 1; border-bottom: 1px solid #E7E5E4; }
.divider span { padding: 0 10px; }

/* Social Buttons */
.social-buttons { display: flex; gap: 15px; }
.social-btn {
  flex: 1; padding: 10px; background: white; border: 1px solid #E7E5E4; border-radius: 10px;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px;
  transition: 0.2s; color: #44403C; font-weight: 600; font-size: 0.9rem;
}
.social-btn:hover { border-color: #D6D3D1; background: #FAFAF9; }

.form-footer { text-align: center; margin-top: 20px; font-size: 0.8rem; color: #57534E; opacity: 0.6; }

/* Animation */
.fade-in-left { animation: fadeInLeft 1s ease-out; }
.fade-in-right { animation: fadeInRight 1s ease-out; }
.fade-in-anim { animation: fadeIn 0.3s ease-out; }

@keyframes fadeInLeft { from { opacity: 0; transform: translateX(-30px); } to { opacity: 1; transform: translateX(0); } }
@keyframes fadeInRight { from { opacity: 0; transform: translateX(30px); } to { opacity: 1; transform: translateX(0); } }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }

/* Responsive */
@media (max-width: 900px) {
  .main-layout { flex-direction: column; gap: 50px; }
  .info-side { text-align: center; }
  .stats-grid { justify-content: center; }
  .description { margin: 0 auto 40px; }
  .form-side { width: 100%; max-width: 450px; }
}
</style>