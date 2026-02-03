<template>
  <div class="auth-page">
    <div class="auth-box">
      <h2>Đăng Nhập</h2>
      <p>Chào mừng bạn quay trở lại!</p>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Email</label>
          <input v-model="email" type="email" placeholder="admin@gmail.com" required>
        </div>
        <div class="form-group">
          <label>Mật khẩu</label>
          <input v-model="password" type="password" placeholder="123456" required>
        </div>
        <button type="submit" class="btn-submit">Đăng Nhập</button>
      </form>

      <p class="switch-link">
        Chưa có tài khoản? <router-link to="/auth/register">Đăng ký ngay</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')

const handleLogin = () => {
  const role = authStore.login(email.value, password.value)
  if (role === 'admin') {
    router.push('/admin/dashboard')
  } else {
    router.push('/home')
  }
}
</script>

<style scoped>
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