<template>
  <div class="auth-page">
    <div class="auth-box">
      <h2>Đăng Ký</h2>
      <p>Tạo tài khoản để tham gia cộng đồng</p>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>Tên hiển thị</label>
          <input type="text" placeholder="Ví dụ: Khánh Nguyễn" required>
        </div>
        <div class="form-group">
          <label>Email</label>
          <input type="email" placeholder="email@example.com" required>
        </div>
        <div class="form-group">
          <label>Mật khẩu</label>
          <input type="password" required>
        </div>
        <button type="submit" class="btn-submit">Đăng Ký</button>
      </form>

      <p class="switch-link">
        Đã có tài khoản? <router-link to="/auth/login">Đăng nhập</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

const handleRegister = () => {
  alert('Đăng ký thành công! Vui lòng đăng nhập.')
  router.push('/auth/login')
}
</script>

<style scoped>
/* Dùng chung CSS với Login cho nhanh */
.auth-page { display: flex; justify-content: center; align-items: center; height: 100vh; background: #F1F5F9; }
.auth-box { background: white; padding: 40px; border-radius: 16px; width: 400px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }
h2 { text-align: center; color: #1E293B; margin-bottom: 10px; }
p { text-align: center; color: #64748B; margin-bottom: 20px; }
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 5px; font-weight: 600; }
.form-group input { width: 100%; padding: 10px; border: 1px solid #CBD5E1; border-radius: 8px; }
.btn-submit { width: 100%; padding: 12px; background: #F97316; color: white; border: none; border-radius: 8px; font-weight: 700; cursor: pointer; }
.btn-submit:hover { background: #EA580C; }
.switch-link { margin-top: 15px; font-size: 0.9rem; }
.switch-link a { color: #F97316; text-decoration: none; font-weight: 700; }
</style>