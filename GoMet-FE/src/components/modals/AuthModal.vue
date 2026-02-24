<template>
  <transition name="modal-motion">
    <div v-if="isOpen" class="modal-overlay" @click="$emit('close')">
      <div class="modal-container-artistic" @click.stop>
        
        <div class="side-art-panel">
          <div class="art-overlay"></div>
          <img src="https://images.unsplash.com/photo-1559339352-11d035aa65de?q=80&w=1974&auto=format&fit=crop" alt="Luxury Gourmet" class="art-img ken-burns">
          
          <div class="art-content stagger-item" style="--delay: 0.2s">
            <div class="logo-area-art">
              <img :src="groupLogoUrl" alt="Logo" class="mini-logo">
              <span class="logo-text-art">GOMET.</span>
            </div>
            <div class="quote-wrap">
              <h3 class="quote-text">“Nơi khơi nguồn cảm hứng <br> cho những bữa ăn hạnh phúc.”</h3>
              <div class="quote-decor"></div>
            </div>
          </div>
        </div>

        <div class="form-panel-interactive">
          <button class="btn-close-art" @click="$emit('close')">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>

          <div class="form-scroll-wrapper">
            <transition name="view-slide" mode="out-in">
              
              <div v-if="currentView === 'login'" :key="'login'" class="form-content-wrap">
                <div class="form-header stagger-item" style="--delay: 0.1s">
                  <h2 class="art-title">Chào mừng trở lại</h2>
                  <p class="art-desc">Tiếp tục hành trình ẩm thực đầy cảm hứng.</p>
                </div>

                <form @submit.prevent="handleLogin" class="art-form">
                  <div class="input-field-art stagger-item" style="--delay: 0.2s">
                    <input v-model="email" type="email" id="login-email" required placeholder=" " />
                    <label for="login-email">Email</label>
                    <span class="input-highlight"></span>
                  </div>

                  <div class="input-field-art stagger-item" style="--delay: 0.3s">
                    <input v-model="password" :type="showPassword ? 'text' : 'password'" id="login-pass" required placeholder=" " />
                    <label for="login-pass">Mật khẩu</label>
                    <button type="button" class="eye-toggle-btn" @click="showPassword = !showPassword">
                      <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                      <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
                    </button>
                    <span class="input-highlight"></span>
                  </div>

                  <div class="form-actions stagger-item" style="--delay: 0.4s">
                    <label class="remember-me">
                      <input type="checkbox"><span class="checkmark"></span> Ghi nhớ
                    </label>
                    <a href="#" class="forgot-link" @click.prevent="switchView('forgot-password')">Quên mật khẩu?</a>
                  </div>

                  <button class="btn-submit-art stagger-item" style="--delay: 0.5s">
                    <span>ĐĂNG NHẬP</span>
                    <div class="btn-shine"></div>
                  </button>
                </form>

                <div class="social-section stagger-item" style="--delay: 0.6s">
                  <div class="divider"><span>Hoặc</span></div>
                  <button class="btn-google-art">
                    <img src="https://cdn-icons-png.flaticon.com/512/2991/2991148.png" width="20"> Đăng nhập với Google
                  </button>
                </div>
                <p class="footer-prompt stagger-item" style="--delay: 0.7s">Bạn mới đến? <a href="#" @click.prevent="switchView('register')">Tham gia ngay</a></p>
              </div>

              <div v-else-if="currentView === 'register'" :key="'register'" class="form-content-wrap">
                <div class="form-header stagger-item" style="--delay: 0.1s">
                  <h2 class="art-title">Tạo tài khoản</h2>
                  <p class="art-desc">Mở khóa không giới hạn công thức chuẩn vị.</p>
                </div>

                <form @submit.prevent="handleRegisterRequest" class="art-form">
                  <div class="input-field-art stagger-item" style="--delay: 0.2s">
                    <input v-model="regForm.name" type="text" id="reg-name" required placeholder=" " />
                    <label for="reg-name">Họ và Tên</label>
                    <span class="input-highlight"></span>
                  </div>
                  <div class="input-field-art stagger-item" style="--delay: 0.3s">
                    <input v-model="regForm.email" type="email" id="reg-email" required placeholder=" " />
                    <label for="reg-email">Email đăng ký</label>
                    <span class="input-highlight"></span>
                  </div>
                  <div class="input-row stagger-item" style="--delay: 0.4s">
                    <div class="input-field-art">
                      <input v-model="regForm.password" type="password" id="reg-pass" required placeholder=" " />
                      <label for="reg-pass">Mật khẩu</label>
                      <span class="input-highlight"></span>
                    </div>
                    <div class="input-field-art">
                      <input v-model="regForm.confirmPassword" type="password" id="reg-confirm" required placeholder=" " />
                      <label for="reg-confirm">Xác nhận</label>
                      <span class="input-highlight"></span>
                    </div>
                  </div>
                  <button class="btn-submit-art btn-orange stagger-item" style="--delay: 0.5s">
                    <span>ĐĂNG KÝ NGAY</span>
                    <div class="btn-shine"></div>
                  </button>
                </form>
                <p class="footer-prompt stagger-item" style="--delay: 0.6s">Đã có tài khoản? <a href="#" @click.prevent="switchView('login')">Đăng nhập</a></p>
              </div>

              <div v-else-if="currentView === 'otp'" :key="'otp'" class="form-content-wrap">
                 <div class="form-header stagger-item" style="--delay: 0.1s">
                  <h2 class="art-title">Xác thực OTP</h2>
                  <p class="art-desc">Mã đã được gửi đến <b>{{ regForm.email }}</b></p>
                </div>
                <form @submit.prevent="handleOtpVerify" class="art-form">
                  <div class="otp-group stagger-item" style="--delay: 0.2s">
                    <input v-for="(n, i) in 6" :key="i" v-model="otpDigits[i]" type="text" maxlength="1" class="otp-input" @input="focusNext($event, i)">
                  </div>
                  <button class="btn-submit-art btn-orange stagger-item" style="--delay: 0.3s">
                    <span>XÁC NHẬN</span>
                  </button>
                </form>
                <button class="btn-back stagger-item" style="--delay: 0.4s" @click="switchView('register')">← Quay lại</button>
              </div>

            </transition>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import logoGroup from '@/assets/images/gomet.jpg' 

const props = defineProps({ initialView: { type: String, default: 'login' } })
const emit = defineEmits(['close'])

const isOpen = ref(false)
const groupLogoUrl = logoGroup
const currentView = ref(props.initialView)
const showPassword = ref(false)
const otpDigits = ref(['', '', '', '', '', ''])

const authStore = useAuthStore()
const router = useRouter()

const email = ref('admin@gmail.com')
const password = ref('123456')
const regForm = reactive({ name: '', email: '', password: '', confirmPassword: '' })

watch(() => props.initialView, (val) => { currentView.value = val })
const switchView = (name) => { currentView.value = name }

const handleLogin = () => {
  authStore.login(email.value, password.value)
  emit('close')
  router.push('/home')
}

const handleRegisterRequest = () => {
  if (regForm.password !== regForm.confirmPassword) {
    alert('Mật khẩu không khớp, vui lòng kiểm tra lại!')
    return
  }
  currentView.value = 'otp'
}

const handleOtpVerify = () => {
  if (otpDigits.value.join('').length < 6) {
    alert('Vui lòng nhập đủ 6 số OTP!')
    return
  }
  alert('Đăng ký thành công! Chào mừng bạn.')
  emit('close')
  router.push('/home')
}

const focusNext = (e, index) => {
  if (e.target.value && index < 5) e.target.parentElement.children[index + 1].focus()
}

onMounted(() => { isOpen.value = true })
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,700;0,800;1,700&family=Mulish:wght@400;600;700;800&display=swap');

/* --- ✨ CORE CONTAINER ✨ --- */
.modal-overlay {
  position: fixed; inset: 0; z-index: 9999;
  background: rgba(12, 10, 9, 0.9); backdrop-filter: blur(10px);
  display: flex; align-items: center; justify-content: center; padding: 20px;
}

.modal-container-artistic {
  background: #fff; width: 100%; max-width: 980px; height: 650px;
  display: flex; border-radius: 24px; overflow: hidden;
  box-shadow: 0 50px 100px -20px rgba(0,0,0,0.6);
  font-family: 'Mulish', sans-serif;
}

/* --- ✨ SIDE ART PANEL (LEFT) ✨ --- */
.side-art-panel { flex: 1.2; position: relative; overflow: hidden; background: #1C1917; }
.art-img { width: 100%; height: 100%; object-fit: cover; opacity: 0.9; }
.ken-burns { animation: kenBurnsEffect 30s ease-in-out infinite alternate; }
@keyframes kenBurnsEffect { from { transform: scale(1); } to { transform: scale(1.18); } }

.art-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.9) 10%, transparent 70%); z-index: 1; }
.art-content { position: absolute; inset: 0; z-index: 2; padding: 60px; display: flex; flex-direction: column; justify-content: space-between; color: white; }
.logo-area-art { display: flex; align-items: center; gap: 15px; }
.mini-logo { width: 42px; height: 42px; border-radius: 50%; border: 2px solid #F97316; }
.logo-text-art { font-family: 'Playfair Display', serif; font-weight: 800; font-size: 1.6rem; letter-spacing: 2px; color: #F97316; }
.quote-text { font-family: 'Playfair Display', serif; font-size: 2.2rem; line-height: 1.3; font-style: italic; text-shadow: 0 2px 10px rgba(0,0,0,0.3); }
.quote-decor { width: 60px; height: 3px; background: #F97316; margin-top: 25px; }

/* --- ✨ INTERACTIVE FORM PANEL (RIGHT) ✨ --- */
.form-panel-interactive { flex: 1; position: relative; background: #fff; display: flex; flex-direction: column; }
.btn-close-art {
  position: absolute; top: 25px; right: 25px; z-index: 10; background: transparent; border: none; 
  color: #A8A29E; cursor: pointer; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.btn-close-art:hover { color: #1C1917; transform: rotate(90deg) scale(1.1); }

.form-scroll-wrapper { flex: 1; overflow-y: auto; padding: 70px 60px; scrollbar-width: none; display: flex; align-items: center; }
.form-scroll-wrapper::-webkit-scrollbar { display: none; }
.form-content-wrap { width: 100%; }

.art-title { font-family: 'Playfair Display', serif; font-size: 2.4rem; font-weight: 800; color: #1C1917; margin: 0; }
.art-desc { color: #78716C; font-size: 1rem; margin-top: 10px; font-weight: 500; }

/* --- ✨ EDITORIAL INPUT FIELDS ✨ --- */
.art-form { margin-top: 40px; text-align: left; }
.input-field-art { position: relative; margin-bottom: 30px; }
.input-field-art input {
  width: 100%; border: none; border-bottom: 2px solid #E7E5E4; padding: 12px 0;
  font-size: 1.05rem; font-weight: 700; color: #1C1917; outline: none; background: transparent;
  transition: all 0.3s ease; font-family: 'Mulish', sans-serif;
}
.input-field-art label {
  position: absolute; top: 12px; left: 0; font-size: 1rem; color: #A8A29E; font-weight: 600;
  pointer-events: none; transition: all 0.3s ease;
}
.input-field-art input:focus ~ label,
.input-field-art input:not(:placeholder-shown) ~ label {
  top: -20px; font-size: 0.8rem; color: #F97316; font-weight: 800; letter-spacing: 1px;
}
.input-highlight { position: absolute; bottom: 0; left: 0; width: 0; height: 2px; background: #F97316; transition: width 0.4s ease; }
.input-field-art input:focus ~ .input-highlight { width: 100%; }

.eye-toggle-btn { position: absolute; right: 0; top: 10px; background: none; border: none; color: #A8A29E; cursor: pointer; }
.input-row { display: flex; gap: 25px; } .input-row .input-field-art { flex: 1; }

/* --- ✨ ACTIONS & BUTTONS ✨ --- */
.form-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 35px; font-size: 0.9rem; }
.remember-me { display: flex; align-items: center; gap: 10px; color: #57534E; font-weight: 700; cursor: pointer; }
.remember-me input { display: none; }
.checkmark { width: 20px; height: 20px; border: 2px solid #D6D3D1; border-radius: 6px; transition: 0.2s; position: relative; }
.remember-me input:checked ~ .checkmark { background: #F97316; border-color: #F97316; }
.remember-me input:checked ~ .checkmark::after { content: '✓'; position: absolute; color: white; font-size: 14px; top: 50%; left: 50%; transform: translate(-50%, -50%); }
.forgot-link { color: #F97316; font-weight: 800; text-decoration: none; transition: 0.3s; }

.btn-submit-art {
  width: 100%; height: 60px; background: #1C1917; color: white; border: none; font-family: 'Mulish', sans-serif;
  font-weight: 800; font-size: 0.95rem; letter-spacing: 2px; cursor: pointer; 
  position: relative; overflow: hidden; transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
}
.btn-submit-art:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.25); }
.btn-orange { background: #F97316; }
.btn-shine {
  position: absolute; top: 0; left: -100%; width: 50%; height: 100%;
  background: linear-gradient(to right, transparent, rgba(255,255,255,0.3), transparent);
  transform: skewX(-25deg); transition: 0.5s;
}
.btn-submit-art:hover .btn-shine { left: 150%; transition: 0.7s ease-in-out; }

.divider { position: relative; margin: 35px 0 25px; text-align: center; }
.divider::before { content: ""; position: absolute; top: 50%; left: 0; width: 100%; height: 1px; background: #E7E5E4; }
.divider span { background: #fff; position: relative; padding: 0 20px; color: #A8A29E; font-size: 0.8rem; font-weight: 700; text-transform: uppercase; letter-spacing: 1px; }

.btn-google-art {
  width: 100%; height: 54px; border: 2px solid #E7E5E4; background: #fff; color: #1C1917;
  display: flex; align-items: center; justify-content: center; gap: 12px;
  font-weight: 700; cursor: pointer; transition: 0.3s;
}
.btn-google-art:hover { border-color: #1C1917; background: #FAFAF9; transform: scale(1.02); }
.footer-prompt { text-align: center; margin-top: 30px; font-size: 0.95rem; color: #78716C; font-weight: 600; }
.footer-prompt a { color: #F97316; font-weight: 800; text-decoration: none; transition: 0.2s; }

/* OTP STYLES */
.otp-group { display: flex; gap: 15px; justify-content: center; margin: 40px 0; }
.otp-input {
  width: 55px; height: 65px; border: 2px solid #E7E5E4; text-align: center; font-size: 1.8rem;
  font-weight: 800; color: #1C1917; outline: none; transition: 0.3s; background: #FAFAF9;
}
.otp-input:focus { border-color: #F97316; background: #fff; transform: translateY(-3px); box-shadow: 0 10px 20px rgba(249, 115, 22, 0.15); }
.btn-back { background: none; border: none; color: #A8A29E; font-weight: 700; cursor: pointer; margin-top: 25px; width: 100%; }

/* --- ✨ ANIMATIONS ✨ --- */
.modal-motion-enter-active, .modal-motion-leave-active { transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1); }
.modal-motion-enter-from, .modal-motion-leave-to { opacity: 0; transform: scale(0.95); }

.view-slide-enter-active, .view-slide-leave-active { transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1); }
.view-slide-enter-from { opacity: 0; transform: translateX(30px); }
.view-slide-leave-to { opacity: 0; transform: translateX(-30px); }

.stagger-item { opacity: 0; transform: translateY(20px); animation: slideUpFade 0.6s cubic-bezier(0.25, 1, 0.5, 1) forwards; animation-delay: var(--delay, 0s); }
@keyframes slideUpFade { to { opacity: 1; transform: translateY(0); } }

@media (max-width: 900px) { .side-art-panel { display: none; } .modal-container-artistic { max-width: 460px; height: auto; min-height: 600px; } .form-scroll-wrapper { padding: 50px 35px; } }
</style>