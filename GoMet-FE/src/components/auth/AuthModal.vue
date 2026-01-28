<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      
      <button class="btn-close" @click="$emit('close')">×</button>

<div v-if="currentView === 'login'" class="auth-view fade-in">
        <div class="modal-header">
          <div class="logo-icon">🍳</div>
          <h2>Chào mừng trở lại!</h2>
          <p>Nhập thông tin để truy cập vào bếp của bạn</p>
        </div>

        <form class="auth-form" @submit.prevent>
          <div class="input-group">
            <label>Email hoặc Tên đăng nhập</label>
            <div class="input-wrapper">
              <span class="icon">✉️</span>
              <input type="text" placeholder="user@example.com" />
            </div>
          </div>

          <div class="input-group">
            <label>Mật khẩu</label> <div class="input-wrapper">
              <span class="icon">🔒</span>
              <input type="password" placeholder="........" />
              <span class="eye-icon">👁️</span>
            </div>
          </div>

          <div class="form-options">
            <label class="remember-me">
              <input type="checkbox" /> Ghi nhớ đăng nhập
            </label>
            <a href="#" class="forgot-link" @click.prevent="switchView('forgot-password')">
              Quên mật khẩu?
            </a>
          </div>

          <button class="btn-submit">Đăng Nhập</button>
        </form>

        <div class="modal-footer">
          <div class="divider"><span>Hoặc đăng nhập với</span></div>
          <div class="social-row">
            <button class="social-btn"><img src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg" width="20"/></button>
            <button class="social-btn"><img src="https://upload.wikimedia.org/wikipedia/commons/b/b8/2021_Facebook_icon.svg" width="20"/></button>
          </div>
          <p class="switch-text">
            Chưa có tài khoản? <a href="#" @click.prevent="switchView('register')">Đăng ký ngay</a>
          </p>
        </div>
      </div>

      <div v-else-if="currentView === 'register'" class="auth-view fade-in">
        <div class="modal-header">
          <div class="logo-icon">🚀</div>
          <h2>Tạo tài khoản mới</h2>
          <p>Tham gia cộng đồng GOMET ngay hôm nay</p>
        </div>

        <form class="auth-form" @submit.prevent>
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
              <input type="password" placeholder="Tạo mật khẩu" />
            </div>
          </div>
          <div class="input-group">
            <label>Xác nhận mật khẩu</label>
            <div class="input-wrapper">
              <span class="icon">🛡️</span>
              <input type="password" placeholder="Nhập lại mật khẩu" />
            </div>
          </div>
          <button class="btn-submit register-btn">Đăng Ký Tài Khoản</button>
        </form>

        <div class="modal-footer">
          <p class="switch-text">
            Đã có tài khoản? <a href="#" @click.prevent="switchView('login')">Đăng nhập</a>
          </p>
        </div>
      </div>

      <div v-else-if="currentView === 'forgot-password'" class="auth-view fade-in">
        <div class="modal-header">
          <div class="logo-icon">🔑</div>
          <h2>Quên mật khẩu?</h2>
          <p>Đừng lo, hãy nhập email để lấy lại mật khẩu nhé</p>
        </div>

        <form class="auth-form" @submit.prevent>
          <div class="input-group">
            <label>Email đăng ký</label>
            <div class="input-wrapper">
              <span class="icon">✉️</span>
              <input type="email" placeholder="Nhập email của bạn" />
            </div>
          </div>

          <button class="btn-submit">Gửi hướng dẫn</button>
        </form>

        <div class="modal-footer">
          <p class="switch-text">
            <a href="#" class="back-link" @click.prevent="switchView('login')">
              ← Quay lại Đăng nhập
            </a>
          </p>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  initialView: { type: String, default: 'login' }
})

defineEmits(['close'])

const currentView = ref(props.initialView)

// Hàm chuyển màn hình (login / register / forgot-password)
const switchView = (viewName) => {
  currentView.value = viewName
}
</script>

<style scoped>
/* (Giữ nguyên toàn bộ CSS cũ của bạn, không cần sửa gì thêm) */
/* Copy lại CSS từ câu trả lời trước vào đây */

/* ... CSS cũ ... */

/* Thêm một chút style cho nút quay lại nếu cần */
.back-link {
  color: #666; font-weight: 600; text-decoration: none;
}
.back-link:hover { color: #111; }

/* Các style cơ bản cần có (mình để lại để bạn tiện copy nếu lỡ xóa) */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.6); backdrop-filter: blur(5px); display: flex; justify-content: center; align-items: center; z-index: 1000; animation: fadeInOverlay 0.2s; }
.modal-content { background: white; width: 420px; max-width: 90%; border-radius: 24px; padding: 40px 30px; position: relative; animation: slideUp 0.3s; }
.btn-close { position: absolute; top: 15px; right: 20px; background: none; border: none; font-size: 2rem; color: #ccc; cursor: pointer; }
.modal-header { text-align: center; margin-bottom: 30px; }
.logo-icon { font-size: 3rem; margin-bottom: 10px; display: inline-block; }
.modal-header h2 { color: #111; margin: 0 0 5px; font-weight: 800; font-size: 1.8rem; }
.modal-header p { color: #666; font-size: 0.95rem; margin: 0; }
.input-group { margin-bottom: 18px; }
.input-group label { display: block; font-size: 0.85rem; font-weight: 600; color: #374151; margin-bottom: 6px; }
.input-wrapper { display: flex; align-items: center; background: #fff; border: 1px solid #e5e7eb; border-radius: 12px; padding: 0 15px; height: 48px; transition: all 0.2s; }
.input-wrapper:focus-within { border-color: #F97316; box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.1); }
.input-wrapper input { flex: 1; border: none; background: transparent; height: 100%; outline: none; font-size: 1rem; padding-left: 10px; color: #111; }
.btn-submit { width: 100%; height: 50px; background: #111; color: white; border: none; border-radius: 12px; font-size: 1rem; font-weight: 700; cursor: pointer; margin-top: 10px; }
.register-btn { background: #F97316; }
.modal-footer { margin-top: 25px; text-align: center; }
.switch-text a { color: #F97316; text-decoration: none; font-weight: 700; margin-left: 5px; }
.label-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.forgot-link { font-size: 0.85rem; color: #F97316; text-decoration: none; font-weight: 600; }
.divider { display: flex; align-items: center; color: #9ca3af; font-size: 0.8rem; margin: 20px 0; }
.divider::before, .divider::after { content: ""; flex: 1; height: 1px; background: #e5e7eb; }
.divider span { padding: 0 10px; }
.social-row { display: flex; justify-content: center; gap: 15px; margin-bottom: 25px; }
.social-btn { width: 46px; height: 46px; border-radius: 50%; border: 1px solid #e5e7eb; background: white; display: flex; align-items: center; justify-content: center; cursor: pointer; }
/* ... Các CSS cũ giữ nguyên ... */

/* 👇 THÊM ĐOẠN NÀY VÀO CUỐI */

.form-options {
  display: flex;
  justify-content: space-between; /* Đẩy 2 phần tử ra 2 mép */
  align-items: center;
  margin-bottom: 20px; /* Tạo khoảng cách với nút Đăng nhập */
  margin-top: -10px;   /* Kéo lên gần ô mật khẩu hơn một chút */
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px; /* Khoảng cách giữa checkbox và chữ */
  font-size: 0.9rem;
  color: #555;
  cursor: pointer;
  font-weight: 500;
}

.remember-me input {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: #F97316; /* Màu checkbox khi tích vào (Màu cam) */
}

.forgot-link {
  font-size: 0.9rem;
  color: #F97316;
  text-decoration: none;
  font-weight: 600;
  transition: 0.2s;
}

.forgot-link:hover {
  text-decoration: underline;
  color: #ea580c;
}
/* Animations */
@keyframes slideUp { from { transform: translateY(20px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
@keyframes fadeInOverlay { from { opacity: 0; } to { opacity: 1; } }
.fade-in { animation: fadeInView 0.3s ease-in-out; }
@keyframes fadeInView { from { opacity: 0; transform: translateX(10px); } to { opacity: 1; transform: translateX(0); } }
</style>
