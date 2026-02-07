<template>
  <transition name="modal-fade">
    <div class="modal-overlay" @click="$emit('close')">
      <div class="modal-content-vip" @click.stop>
        
        <button class="btn-close-vip" @click="$emit('close')">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>

        <div class="modal-scroll-area">
          <div v-if="currentView === 'login'" class="auth-view fade-in">
            <div class="modal-header-vip">
              <div class="logo-wrapper">
                <div class="ripple-effect"></div>
                <div class="logo-circle-vip">
                  <img :src="groupLogoUrl" alt="Group Logo" class="group-logo-img" />
                </div>
              </div>
              
              <h2 class="title-vip">Chào mừng trở lại!</h2>
              <p class="subtitle-vip">Tiếp tục hành trình ẩm thực cùng Gomet</p>
              <p class="subtitle-vip" style="opacity: 0.6; font-size: 0.75rem">(Test: admin@gmail.com / 123456)</p>
            </div>

            <form class="auth-form-vip" @submit.prevent="handleLogin">
              <div class="input-group-vip">
                <label>Email</label>
                <div class="input-wrapper-vip">
                  <svg class="icon-v-mono" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                  <input v-model="email" type="email" placeholder="admin@gmail.com" required />
                </div>
              </div>

              <div class="input-group-vip">
                <label>Mật khẩu</label> 
                <div class="input-wrapper-vip">
                  <svg class="icon-v-mono" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                  <input v-model="password" :type="showPassword ? 'text' : 'password'" placeholder="••••••••" required />
                  <button type="button" class="eye-btn-v" @click="showPassword = !showPassword">
                    <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                    <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
                  </button>
                </div>
              </div>

              <div class="form-options-vip">
                <label class="remember-me-v">
                  <input type="checkbox" /> Ghi nhớ tôi
                </label>
                <a href="#" class="forgot-link-v" @click.prevent="switchView('forgot-password')">Quên mật khẩu?</a>
              </div>

              <button class="btn-submit-vip">Đăng Nhập Ngay</button>
            </form>

            <div class="modal-footer-vip">
              <div class="divider-v"><span>Hoặc đăng nhập với</span></div>
              <div class="social-row-v">
                <button class="s-btn-v google-btn">
                  <svg class="google-icon" viewBox="0 0 24 24"><text x="50%" y="50%" text-anchor="middle" dominant-baseline="central" font-family="Arial" font-weight="800" font-size="14" fill="currentColor">G</text></svg>
                </button>
              </div>
              <p class="switch-text-v">
                Bạn mới đến? <a href="#" @click.prevent="switchView('register')">Tham gia ngay</a>
              </p>
            </div>
          </div>

          <div v-else-if="currentView === 'register'" class="auth-view fade-in">
            <div class="modal-header-vip">
              <div class="logo-wrapper">
                <div class="ripple-effect"></div>
                <div class="logo-circle-vip">
                  <img :src="groupLogoUrl" alt="Group Logo" class="group-logo-img" />
                </div>
              </div>

              <h2 class="title-vip">Tạo tài khoản</h2>
              <p class="subtitle-vip">Mở khóa toàn bộ tính năng cao cấp</p>
            </div>

            <form class="auth-form-vip" @submit.prevent="handleRegister">
              <div class="input-group-vip">
                <label>Họ và Tên</label>
                <div class="input-wrapper-vip"><input type="text" placeholder="Nguyễn Khánh" required /></div>
              </div>
              <div class="input-group-vip">
                <label>Email</label>
                <div class="input-wrapper-vip"><input type="email" placeholder="your@email.com" required /></div>
              </div>
              <div class="input-group-vip">
                <label>Mật khẩu</label>
                <div class="input-wrapper-vip"><input type="password" placeholder="••••••••" required /></div>
              </div>
              <button class="btn-submit-vip register-btn-v">Đăng Ký Thành Viên</button>
            </form>

            <div class="modal-footer-vip">
              <p class="switch-text-v">Đã có tài khoản? <a href="#" @click.prevent="switchView('login')">Đăng nhập</a></p>
            </div>
          </div>

          <div v-else-if="currentView === 'forgot-password'" class="auth-view fade-in">
            <div class="modal-header-vip">
              <div class="logo-wrapper">
                <div class="ripple-effect"></div>
                <div class="logo-circle-vip">
                  <img :src="groupLogoUrl" alt="Group Logo" class="group-logo-img" />
                </div>
              </div>

              <h2 class="title-vip">Quên mật khẩu?</h2>
              <p class="subtitle-vip">Nhập email để nhận hướng dẫn khôi phục</p>
            </div>
            <form class="auth-form-vip" @submit.prevent>
              <div class="input-group-vip">
                <label>Email đăng ký</label>
                <div class="input-wrapper-vip"><input type="email" placeholder="Nhập email của bạn" /></div>
              </div>
              <button class="btn-submit-vip">Gửi Yêu Cầu</button>
            </form>
            <div class="modal-footer-vip">
              <a href="#" class="back-link-v" @click.prevent="switchView('login')">← Quay lại đăng nhập</a>
            </div>
          </div>
        </div>

      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 👇👇👇 QUAN TRỌNG: Import ảnh trực tiếp để Vite xử lý 👇👇👇
import logoGroup from '@/assets/images/gomet.jpg' 
const groupLogoUrl = logoGroup
// 👆👆👆 Nếu file ảnh tên khác hoặc đuôi .png thì sửa lại nhé

const props = defineProps({ initialView: { type: String, default: 'login' } })
const emit = defineEmits(['close'])

const currentView = ref(props.initialView)
const email = ref('admin@gmail.com') // Pre-fill email test
const password = ref('123456') // Pre-fill password test
const showPassword = ref(false)

const authStore = useAuthStore()
const router = useRouter()

watch(() => props.initialView, (newVal) => { currentView.value = newVal })
const switchView = (name) => { currentView.value = name }

const handleLogin = () => {
  const role = authStore.login(email.value, password.value)
  emit('close')
  if (role === 'admin') router.push('/admin/dashboard')
}

const handleRegister = () => {
  alert('Đăng ký thành công!')
  switchView('login')
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Quicksand:wght@500;600;700;800&display=swap');

* { font-family: 'Quicksand', sans-serif; box-sizing: border-box; }

.modal-overlay {
  position: fixed; inset: 0; z-index: 9999;
  background: rgba(12, 10, 9, 0.85); backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center; padding: 20px;
}

.modal-content-vip {
  background: #ffffff;
  width: 100%; max-width: 400px;
  border-radius: 28px;
  position: relative; overflow: hidden;
  max-height: 90vh;
  display: flex; flex-direction: column;
  box-shadow: 0 40px 100px rgba(0,0,0,0.5);
}

.modal-scroll-area { 
  overflow-y: auto; padding: 30px 25px; 
  &::-webkit-scrollbar { display: none; }
}

.btn-close-vip {
  position: absolute; top: 12px; right: 12px; z-index: 100;
  background: #f4f4f5; border: none; width: 28px; height: 28px;
  border-radius: 50%; color: #71717a; cursor: pointer;
  display: flex; align-items: center; justify-content: center; transition: 0.3s;
}
.btn-close-vip:hover { background: #fee2e2; color: #ef4444; transform: rotate(90deg); }

.modal-header-vip { text-align: center; margin-bottom: 18px; }

/* --- STYLE LOGO & HIỆU ỨNG (MỚI) --- */
.logo-wrapper {
  position: relative;
  width: 70px; height: 70px;
  margin: 0 auto 12px;
  display: flex; align-items: center; justify-content: center;
}

/* Khung tròn chứa ảnh */
.logo-circle-vip {
  width: 100%; height: 100%;
  border-radius: 50%;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 5px 15px rgba(249, 115, 22, 0.3);
  position: relative; z-index: 2;
  animation: floatLogo 3s ease-in-out infinite; /* Hiệu ứng trôi */
}

/* Ảnh logo */
.group-logo-img {
  width: 100%; height: 100%;
  object-fit: cover; /* Đảm bảo ảnh luôn full khung tròn */
  display: block;
  transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.logo-circle-vip:hover .group-logo-img {
  transform: scale(1.15); /* Phóng to khi hover */
}

/* Hiệu ứng sóng lan tỏa (Ripple) */
.ripple-effect {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  width: 100%; height: 100%;
  border-radius: 50%;
  background: rgba(249, 115, 22, 0.4);
  z-index: 1;
  animation: rippleAnim 2s infinite;
}

/* Định nghĩa Animation */
@keyframes floatLogo {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

@keyframes rippleAnim {
  0% { width: 100%; height: 100%; opacity: 0.6; }
  100% { width: 160%; height: 160%; opacity: 0; }
}
/* --------------------------------- */

.title-vip { color: #1c1917; font-size: 1.35rem; font-weight: 800; margin: 0 0 3px; }
.subtitle-vip { color: #71717a; font-size: 0.8rem; font-weight: 500; }

.input-group-vip { margin-bottom: 14px; }
.input-group-vip label { display: block; font-size: 0.7rem; font-weight: 800; color: #44403c; margin-bottom: 5px; text-transform: uppercase; }

.input-wrapper-vip {
  display: flex; align-items: center; background: #fafaf9;
  border: 1.5px solid #e7e5e4; border-radius: 12px; padding: 0 12px; height: 44px; transition: 0.2s;
}
.input-wrapper-vip:focus-within { border-color: #f97316; background: #fff; box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.1); }
.input-wrapper-vip input { flex: 1; border: none; background: transparent; font-size: 0.9rem; font-weight: 600; outline: none; padding-left: 8px; }

.icon-v-mono { width: 16px; height: 16px; color: #a1a1aa; }
.input-wrapper-vip:focus-within .icon-v-mono { color: #f97316; }

.eye-btn-v { background: none; border: none; cursor: pointer; color: #a1a1aa; padding: 0; display: flex; align-items: center; }
.eye-btn-v:hover { color: #1c1917; }

.form-options-vip { display: flex; justify-content: space-between; align-items: center; margin: 12px 0 16px; font-size: 0.8rem; }
.remember-me-v { display: flex; align-items: center; gap: 5px; color: #57534e; font-weight: 600; cursor: pointer; }
.forgot-link-v { color: #f97316; font-weight: 700; text-decoration: none; }

.btn-submit-vip {
  width: 100%; height: 46px; background: #1c1917; color: #fff;
  border: none; border-radius: 12px; font-size: 0.9rem; font-weight: 800;
  cursor: pointer; transition: 0.3s;
}
.btn-submit-vip:hover { background: #f97316; transform: translateY(-2px); }
.register-btn-v { background: #f97316; }

.divider-v { position: relative; margin: 16px 0; text-align: center; }
.divider-v::before { content: ""; position: absolute; left: 0; top: 50%; width: 100%; height: 1px; background: #e7e5e4; }
.divider-v span { position: relative; background: #fff; padding: 0 8px; color: #a8a29e; font-size: 0.65rem; font-weight: 700; text-transform: uppercase; }

.social-row-v { display: flex; gap: 12px; justify-content: center; margin-bottom: 16px; }
.s-btn-v { width: 40px; height: 40px; border-radius: 50%; border: 1.5px solid #e7e5e4; background: #fff; cursor: pointer; transition: 0.2s; display: flex; align-items: center; justify-content: center; }
.s-btn-v:hover { border-color: #1c1917; transform: scale(1.1); }
.s-btn-v.google-btn { width: 50px; height: 50px; border-radius: 50%; }
.s-btn-v.google-btn .google-icon { width: 24px; height: 24px; color: #1c1917; display: block; }
.s-btn-v.google-btn:hover { transform: scale(1.07); border-color: #1c1917; }

.switch-text-v { color: #71717a; font-size: 0.8rem; text-align: center; }
.switch-text-v a { color: #f97316; font-weight: 800; text-decoration: none; }
.back-link-v { color: #71717a; font-weight: 700; text-decoration: none; display: block; text-align: center; font-size: 0.8rem; }

.fade-in { animation: fadeInEffect 0.4s ease; }
@keyframes fadeInEffect { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.3s; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
</style>