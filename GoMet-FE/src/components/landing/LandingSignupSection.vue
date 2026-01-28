<template>
  <section class="signup-section">
    <div class="container main-layout">
      <div class="info-side">
        <div class="brand-header">
          <div class="logo-box">🍳</div>
          <div class="brand-text">
            <h2 class="brand-name">Gomet</h2>
            <span class="brand-tagline">Nền tảng nấu ăn hiện đại</span>
          </div>
        </div>
        <h1 class="main-title">Khám phá hàng ngàn công thức món ăn</h1>
        <p class="description">Tham gia cộng đồng yêu ẩm thực, chia sẻ công thức và học hỏi từ các đầu bếp tài năng.</p>
        <div class="stats-row">
          <div class="stat-box"><strong>10K+</strong><span>Công thức món ăn</span></div>
          <div class="stat-box"><strong>50K+</strong><span>Thành viên</span></div>
        </div>
      </div>

      <div class="form-side">
        <div class="auth-card">
          <div class="auth-tabs">
            <button class="tab-btn" :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">Đăng Nhập</button>
            <button class="tab-btn" :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">Đăng Ký</button>
          </div>

          <div class="form-content">
            <h3 class="form-title">{{ activeTab === 'login' ? 'Đăng Nhập' : 'Đăng Ký Tài Khoản' }}</h3>

            <form @submit.prevent>
              
              <div v-if="activeTab === 'register'" class="fade-in">
                <div class="input-group">
                  <label>Tên đăng nhập</label>
                  <div class="input-wrapper">
                    <span class="icon">👤</span>
                    <input type="text" placeholder="Ví dụ: nguyenkhanh" />
                  </div>
                </div>

                <div class="input-group">
                  <label>Email</label>
                  <div class="input-wrapper">
                    <span class="icon">✉️</span>
                    <input type="email" placeholder="your@email.com" />
                  </div>
                </div>

                <div class="input-group">
                  <label>Mật khẩu</label>
                  <div class="input-wrapper">
                    <span class="icon">🔒</span>
                    <input type="password" placeholder="Mật khẩu của bạn" />
                  </div>
                </div>

                <div class="input-group">
                  <label>Xác nhận mật khẩu</label>
                  <div class="input-wrapper">
                    <span class="icon">🛡️</span>
                    <input type="password" placeholder="Nhập lại mật khẩu" />
                  </div>
                </div>
              </div>

              <div v-else class="fade-in">
                <div class="input-group">
                  <label>Email / Tên đăng nhập</label>
                  <div class="input-wrapper">
                    <span class="icon">✉️</span>
                    <input type="text" placeholder="your@email.com" />
                  </div>
                </div>

                <div class="input-group">
                  <label>Mật khẩu</label>
                  <div class="input-wrapper">
                    <span class="icon">🔒</span>
                    <input type="password" placeholder="........" />
                    <span class="eye-icon">👁️</span>
                  </div>
                </div>

                <div class="form-actions">
                  <label class="remember"><input type="checkbox"> Ghi nhớ</label>
                  <a href="#" class="forgot-pass">Quên mật khẩu?</a>
                </div>
              </div>

              <button class="btn-submit">{{ activeTab === 'login' ? 'Đăng Nhập' : 'Đăng Ký Ngay' }}</button>

              <div class="divider"><span>Hoặc đăng nhập với</span></div>
              <div class="social-buttons">
                <button class="social-btn"><img src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg" width="20"/></button>
                <button class="social-btn"><img src="https://upload.wikimedia.org/wikipedia/commons/b/b8/2021_Facebook_icon.svg" width="20"/></button>
              </div>
            </form>
          </div>
        </div>
        
        <div class="form-footer">
          <p>© 2026 Gomet. All rights reserved.</p>
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
/* Giữ nguyên toàn bộ CSS xịn xò của phần trước */
/* ... (Copy lại CSS cũ vào đây, không cần sửa gì ở CSS vì layout vẫn thế) ... */
/* Nếu cần mình sẽ gửi lại CSS full, nhưng bạn có thể dùng CSS của câu trả lời trước là đẹp rồi */

.signup-section {
  min-height: 100vh;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #fffbf7 0%, #fff 100%);
  padding: 40px 20px;
}
.container { width: 100%; max-width: 1100px; margin: 0 auto; }
.main-layout { display: flex; align-items: center; justify-content: space-between; gap: 60px; }
.info-side { flex: 1; padding-right: 20px; }
.brand-header { display: flex; align-items: center; gap: 15px; margin-bottom: 25px; }
.logo-box { width: 50px; height: 50px; background: #F97316; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; color: white; }
.brand-name { color: #F97316; margin: 0; font-size: 1.5rem; font-weight: 800; line-height: 1.2; }
.brand-tagline { color: #666; font-size: 0.9rem; font-weight: 600; }
.main-title { font-size: 2.8rem; color: #1f2937; line-height: 1.2; margin-bottom: 20px; font-weight: 700; }
.description { font-size: 1.1rem; color: #4b5563; line-height: 1.6; margin-bottom: 40px; max-width: 450px; }
.stats-row { display: flex; gap: 20px; }
.stat-box { background: white; border: 1px solid #e5e7eb; padding: 15px 25px; border-radius: 12px; min-width: 140px; box-shadow: 0 4px 6px rgba(0,0,0,0.02); }
.stat-box strong { display: block; font-size: 1.5rem; color: #F97316; font-weight: 800; }
.stat-box span { font-size: 0.9rem; color: #666; }

.form-side { flex: 0 0 450px; width: 450px; }
.auth-card { background: white; border-radius: 24px; padding: 10px; box-shadow: 0 20px 50px rgba(0,0,0,0.08); border: 1px solid #f3f4f6; }
.auth-tabs { display: flex; background: #f9fafb; border-radius: 16px; padding: 5px; margin-bottom: 20px; }
.tab-btn { flex: 1; border: none; background: transparent; padding: 10px; font-weight: 600; color: #666; cursor: pointer; border-radius: 12px; transition: 0.2s; }
.tab-btn.active { background: white; color: #F97316; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.form-content { padding: 10px 20px 20px; }
.form-title { font-size: 1.5rem; color: #111; margin-bottom: 20px; font-weight: 700; }
.input-group { margin-bottom: 15px; }
.input-group label { display: block; font-size: 0.85rem; color: #374151; margin-bottom: 6px; font-weight: 600; }
.input-wrapper { display: flex; align-items: center; background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 10px; padding: 0 12px; transition: 0.2s; }
.input-wrapper:focus-within { border-color: #F97316; background: white; box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1); }
.input-wrapper input { flex: 1; border: none; background: transparent; padding: 12px 10px; outline: none; font-size: 0.95rem; color: #333; }
.icon { color: #9ca3af; font-size: 1.1rem; }
.form-actions { display: flex; justify-content: space-between; align-items: center; font-size: 0.85rem; margin-bottom: 20px; }
.forgot-pass { color: #F97316; text-decoration: none; font-weight: 600; }
.btn-submit { width: 100%; padding: 14px; background: #F97316; color: white; border: none; border-radius: 12px; font-weight: 700; font-size: 1rem; cursor: pointer; transition: 0.2s; box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3); }
.btn-submit:hover { background: #ea580c; transform: translateY(-1px); }
.divider { display: flex; align-items: center; text-align: center; color: #9ca3af; font-size: 0.8rem; margin: 20px 0; }
.divider::before, .divider::after { content: ''; flex: 1; border-bottom: 1px solid #e5e7eb; }
.divider span { padding: 0 10px; }
.social-buttons { display: flex; gap: 12px; justify-content: center; margin-bottom: 10px; }
.social-btn { flex: 1; padding: 10px; background: white; border: 1px solid #e5e7eb; border-radius: 10px; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.social-btn:hover { border-color: #d1d5db; background: #f9fafb; }
.form-footer { text-align: center; margin-top: 30px; font-size: 0.8rem; color: #9ca3af; }
.fade-in { animation: fadeIn 0.3s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 900px) {
  .main-layout { flex-direction: column; gap: 40px; }
  .info-side { text-align: center; padding: 0; }
  .stats-row { justify-content: center; }
  .description { margin-left: auto; margin-right: auto; }
  .form-side { width: 100%; max-width: 450px; }
}
</style>