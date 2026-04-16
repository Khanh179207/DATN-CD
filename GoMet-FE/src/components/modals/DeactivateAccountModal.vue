<template>
  <transition name="modal-fade">
    <div v-if="modelValue" class="edit-modal-overlay" @click.self="close">
      <div class="edit-modal-card deactivate-card-lux">
        <div class="edit-modal-header">
          <div class="header-left-icon">
            <div class="icon-box-danger">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                <line x1="12" y1="9" x2="12" y2="13"></line>
                <line x1="12" y1="17" x2="12.01" y2="17"></line>
              </svg>
            </div>
          </div>
          <button class="btn-close" @click="close">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>

        <div class="edit-modal-body">
          <h2 class="danger-title">Xác nhận xóa tài khoản</h2>
          <p class="danger-subtitle">Hành động này sẽ ẩn tài khoản và tất cả bài viết của bạn khỏi hệ thống. Bạn có thể khôi phục lại bất kỳ lúc nào bằng cách đăng nhập lại.</p>

          <div class="form-step mt-4">
            
            <div v-if="!otpSent">
              <div v-if="hasPassword" class="password-group">
                <label>Xác nhận mật khẩu hiện tại</label>
                <div class="input-wrap">
                  <input 
                    :type="showPassword ? 'text' : 'password'" 
                    v-model="password" 
                    placeholder="Nhập mật khẩu để tiếp tục"
                    @keydown.enter="handleRequestOTP"
                  >
                  <button type="button" class="eye-btn" @click="showPassword = !showPassword" tabindex="-1">
                    <svg v-if="showPassword" viewBox="0 0 24 24" fill="none" class="eye-icon"><path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19m-6.72-1.07a3 3 0 11-4.24-4.24M1 1l22 22" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
                    <svg v-else viewBox="0 0 24 24" fill="none" class="eye-icon"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="1.5"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5"/></svg>
                  </button>
                </div>
              </div>
              
              <div v-else class="google-auth-notice">
                <i class="fas fa-info-circle"></i>
                <p>Tài khoản của bạn đăng nhập bằng Google. Vui lòng bấm gửi mã OTP để tiếp tục.</p>
              </div>
            </div>

            <div v-else>
              <div class="otp-hint-box">
                Mã xác thực đã được gửi đến Email của bạn. <br>
                Vui lòng nhập mã gồm <strong>6 chữ số</strong> để xác nhận.
              </div>
              
              <div class="otp-group-lux">
                <input 
                  v-for="(n, i) in 6" 
                  :key="i"
                  v-model="otpDigits[i]" 
                  type="text" 
                  inputmode="numeric"
                  maxlength="1" 
                  class="otp-input-box"
                  @input="focusNext($event, i)"
                  @keydown="handleKeyDown($event, i)"
                  @paste="handlePaste($event)" 
                />
              </div>

              <div class="resend-section">
                <button class="btn-resend-text" :disabled="loading" @click="handleRequestOTP">
                  Chưa nhận được mã? <span>Gửi lại ngay</span>
                </button>
              </div>
            </div>

          </div>
        </div>

        <div class="edit-modal-footer">
          <button class="btn-cancel" @click="close" :disabled="loading">Hủy bỏ</button>
          
          <button v-if="!otpSent" class="btn-danger-fill" :disabled="(hasPassword && !password) || loading" @click="handleRequestOTP">
            <span v-if="loading" class="spinner-sm"></span>
            <span>{{ loading ? 'Đang gửi...' : 'Gửi mã OTP' }}</span>
          </button>

          <button v-else class="btn-danger-fill" :disabled="otpCode.length < 6 || loading" @click="handleDeactivate">
            <span v-if="loading" class="spinner-sm"></span>
            <span>{{ loading ? 'Đang xử lý...' : 'Xóa tài khoản' }}</span>
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, nextTick, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const props = defineProps(['modelValue', 'user'])
const emit = defineEmits(['update:modelValue'])

const authStore = useAuthStore()
const router = useRouter()

const loading = ref(false)
const otpSent = ref(false)
const password = ref('')
const showPassword = ref(false)
const otpDigits = ref(['', '', '', '', '', ''])

const otpCode = computed(() => otpDigits.value.join(''))

const hasPassword = computed(() => {
  return props.user?.provider !== 'google';
});

const isRequestDisabled = computed(() => {
  if (loading.value) return true;
  if (hasPassword.value && !password.value) return true;
  return false;
});

const resetState = () => {
  otpSent.value = false;
  password.value = '';
  showPassword.value = false;
  otpDigits.value = ['', '', '', '', '', ''];
}

watch(() => props.modelValue, (isOpen) => {
  if (!isOpen) resetState();
});

const focusNext = (e, index) => {
  const val = e.target.value.replace(/[^0-9]/g, '')
  otpDigits.value[index] = val
  if (val && index < 5) {
    const nextInput = e.target.nextElementSibling
    if (nextInput) nextInput.focus()
  }
}

const handlePaste = (e) => {
  e.preventDefault() 
  const pasteData = e.clipboardData.getData('text').slice(0, 6).replace(/[^0-9]/g, '')
  if (!pasteData) return
  const digits = pasteData.split('')
  digits.forEach((char, index) => {
    if (index < 6) otpDigits.value[index] = char
  })
  nextTick(() => {
    const inputs = e.target.parentElement.querySelectorAll('.otp-input-box')
    const nextIdx = Math.min(digits.length, 5)
    inputs[nextIdx]?.focus()
  })
}

const handleKeyDown = (e, index) => {
  if (e.key === 'Backspace') {
    if (!otpDigits.value[index] && index > 0) {
      const prevInput = e.target.previousElementSibling
      if (prevInput) prevInput.focus()
    }
  } else if (e.key === 'Enter' && otpCode.value.length === 6) {
    handleDeactivate()
  }
}

const close = () => {
  if (loading.value) return
  emit('update:modelValue', false)
}

// BƯỚC 1: GỬI YÊU CẦU LẤY MÃ OTP
const handleRequestOTP = async () => {
  const userId = authStore.user?.accountID || authStore.user?.id;
  if (!userId || isRequestDisabled.value) return;

  loading.value = true
  try {
    await api.post(`/api/users/${userId}/send-deactivate-otp`, {
      password: hasPassword.value ? password.value : null,
    });
    otpSent.value = true;
    toast.success('Mã OTP đã được gửi về Email của bạn!')
  } catch (err) {
    // Backend báo lỗi mật khẩu sai sẽ nhảy vào đây
    toast.error(err.response?.data?.message || 'Có lỗi xảy ra, Bạn kiểm tra lại mật khẩu nhé.');
    password.value = ''; // Reset pass nếu sai để nhập lại
  } finally {
    loading.value = false
  }
}

// BƯỚC 2: XÁC NHẬN XÓA
const handleDeactivate = async () => {
  if (otpCode.value.length < 6) return
  const userId = authStore.user?.accountID || authStore.user?.id;
  if (!userId) return;

  loading.value = true
  try {
    await api.post(`/api/users/${userId}/deactivate`, {
      password: hasPassword.value ? password.value : null,
      otp: otpCode.value
    });

    toast.success('Tài khoản đã ẩn thành công. GoMet sẽ rất nhớ Bạn!');
    close();
    authStore.logout();
    router.push('/');
  } catch (err) {
    toast.error(err.response?.data?.message || 'Mã OTP không chính xác.');
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.edit-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(6px);
  z-index: 9000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.edit-modal-card {
  background: #fff;
  border-radius: 24px;
  width: 100%;
  max-width: 480px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
}

.edit-modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 24px 32px 10px;
  flex-shrink: 0;
}

.icon-box-danger {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: #FEF2F2;
  color: #DC2626;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #FEE2E2;
}

.btn-close {
  background: #F8FAFC;
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748B;
  transition: all 0.2s;

  &:hover {
    background: #E2E8F0;
    color: #1E293B;
  }
}

.edit-modal-body {
  padding: 10px 32px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow-y: auto;
  text-align: center;
  
  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }
}

.danger-title {
  font-size: 1.4rem;
  margin: 0 0 8px;
  color: #0F172A;
  font-weight: 800;
}

.danger-subtitle {
  font-size: 0.95rem;
  color: #64748B;
  line-height: 1.5;
  margin: 0;
}

/* Styles cho Mật khẩu */
.password-group {
  text-align: left;
  margin-bottom: 20px;
  
  label {
    display: block;
    font-size: 0.9rem;
    font-weight: 700;
    color: #334155;
    margin-bottom: 8px;
  }
  
  .input-wrap {
    position: relative;
    input {
      width: 100%;
      background: #F8FAFC;
      border: 1.5px solid #E2E8F0;
      border-radius: 12px;
      padding: 14px 16px;
      font-size: 1rem;
      font-weight: 500;
      color: #0F172A;
      transition: all 0.2s ease;
      box-sizing: border-box;
      
      &:focus {
        background: #fff;
        border-color: #EA580C;
        outline: none;
        box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1);
      }
    }
  }

  .eye-btn {
    position: absolute; right: 14px; top: 50%; transform: translateY(-50%);
    background: none; border: none; cursor: pointer; color: #A8A29E;
    display: flex; align-items: center; padding: 0; transition: color 0.2s;
    &:hover { color: #EA580C; }
    .eye-icon { width: 18px; height: 18px; }
  }
}

.google-auth-notice { 
  background: #EFF6FF; border: 1px solid #BFDBFE; border-radius: 12px; 
  padding: 12px 16px; display: flex; gap: 12px; align-items: flex-start; 
  color: #1E3A8A; font-size: 0.85rem; line-height: 1.5; text-align: left;
  margin-bottom: 20px;
  i { margin-top: 3px; color: #3B82F6; font-size: 1.1rem; } 
  p { margin: 0; } 
}


/* Styles cho OTP */
.form-step { margin-top: 10px; }

.otp-hint-box {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: 16px;
  padding: 16px;
  font-size: 0.9rem;
  color: #475569;
  line-height: 1.5;
  margin-bottom: 20px;
  strong { color: #0F172A; }
}

.otp-group-lux {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 20px;
  margin-top: 10px;
}

.otp-input-box {
  width: 54px;
  height: 64px;
  background: #F8FAFC;
  border: 2px solid #E2E8F0;
  border-radius: 14px;
  text-align: center;
  font-size: 1.75rem;
  font-weight: 800;
  color: #0F172A;
  transition: all 0.3s ease;
  font-family: monospace;

  &:focus {
    border-color: #EA580C;
    background: #fff;
    outline: none;
    box-shadow: 0 4px 20px -5px rgba(234, 88, 12, 0.2), 0 0 0 4px rgba(234, 88, 12, 0.1);
    transform: translateY(-2px);
  }
}

.resend-section {
  .btn-resend-text {
    background: none; border: none; color: #64748B; font-size: 0.9rem;
    cursor: pointer; font-weight: 500;
    span { color: #EA580C; font-weight: 700; transition: 0.2s; }
    &:hover span { text-decoration: underline; }
    &:disabled { opacity: 0.6; cursor: not-allowed; }
  }
}

/* Footer */
.edit-modal-footer {
  display: flex; gap: 12px; padding: 20px 32px 24px;
  border-top: 1px solid #F1F5F9; background: #F8FAFC; flex-shrink: 0;
}

.btn-cancel {
  flex: 1; background: #fff; border: 1.5px solid #E2E8F0; padding: 14px;
  border-radius: 12px; font-weight: 700; font-size: 0.95rem; color: #475569;
  cursor: pointer; transition: 0.3s;
  &:hover { background: #F1F5F9; color: #0F172A; }
}

.btn-danger-fill {
  flex: 1.5; background: linear-gradient(135deg, #EF4444, #DC2626); border: none;
  padding: 14px; border-radius: 12px; font-weight: 700; font-size: 0.95rem; color: #fff;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px;
  box-shadow: 0 8px 20px -6px rgba(220, 38, 38, 0.4); transition: 0.3s;
  &:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 12px 25px -6px rgba(220, 38, 38, 0.5); }
  &:disabled { opacity: 0.7; cursor: not-allowed; filter: grayscale(50%); box-shadow: none; transform: none; }
}

.spinner-sm {
  width: 16px; height: 16px; border: 2px solid rgba(255, 255, 255, 0.4);
  border-top-color: #fff; border-radius: 50%; animation: rotate 0.8s linear infinite;
}

@keyframes rotate { to { transform: rotate(360deg); } }

/* --- Animations --- */
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.25s ease;
  .edit-modal-card { transition: transform 0.25s, opacity 0.25s; }
}
.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
  .edit-modal-card { transform: scale(0.94) translateY(16px); opacity: 0; }
}

/* --- Mobile Responsive --- */
@media (max-width: 576px) {
  .edit-modal-overlay { padding: 16px; }
  .edit-modal-card { border-radius: 20px; }
  .edit-modal-header { padding: 20px 24px 10px; }
  .edit-modal-body { padding: 10px 24px 20px; }
  .edit-modal-footer { padding: 16px 24px 20px; flex-direction: column-reverse; gap: 10px; }
  .btn-cancel, .btn-danger-fill { width: 100%; flex: none; }
  
  .otp-group-lux { gap: 6px; }
  .otp-input-box { width: 44px; height: 54px; font-size: 1.4rem; border-radius: 10px; }
}
</style>