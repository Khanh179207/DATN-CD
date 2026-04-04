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
          <p class="danger-subtitle">Hành động này sẽ ẩn tài khoản và tất cả bài viết của bạn. Bạn có thể khôi phục lại bất kỳ lúc nào bằng cách đăng nhập lại.</p>

          <div class="form-step mt-4">
            <div class="otp-hint-box">
              Mã xác thực đã được gửi tự động đến Email của bạn. <br>
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

        <div class="edit-modal-footer">
          <button class="btn-cancel" @click="close">Hủy bỏ</button>
          <button class="btn-danger-fill" :disabled="otpCode.length < 6 || loading" @click="handleDeactivate">
            <span v-if="loading" class="spinner-sm"></span>
            <span>{{ loading ? 'Đang xử lý...' : 'Xóa tài khoản' }}</span>
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch, computed, nextTick } from 'vue'
import { sendDeactivateOtp } from '@/services/authService'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

const authStore = useAuthStore()
const loading = ref(false)
const otpDigits = ref(['', '', '', '', '', ''])
const otpCode = computed(() => otpDigits.value.join(''))

// 🔥 TỰ ĐỘNG GỬI OTP KHI MỞ MODAL
watch(() => props.modelValue, (isOpen) => {
  if (isOpen) {
    handleRequestOTP()
  }
})

const focusNext = (e, index) => {
  const val = e.target.value.replace(/[^0-9]/g, '')
  otpDigits.value[index] = val

  if (val && index < 5) {
    // Nhảy sang ô tiếp theo
    const nextInput = e.target.nextElementSibling
    if (nextInput) nextInput.focus()
  }
}

const handlePaste = (e) => {
  e.preventDefault() // Chặn hành vi dán mặc định của trình duyệt
  const pasteData = e.clipboardData.getData('text').slice(0, 6).replace(/[^0-9]/g, '')
  if (!pasteData) return

  const digits = pasteData.split('')
  digits.forEach((char, index) => {
    if (index < 6) otpDigits.value[index] = char
  })

  // Tự động focus vào ô cuối cùng sau khi paste
  nextTick(() => {
    const inputs = e.target.parentElement.querySelectorAll('.otp-input-box')
    const nextIdx = Math.min(digits.length, 5)
    inputs[nextIdx].focus()
  })
}

const handleKeyDown = (e, index) => {
  if (e.key === 'Backspace') {
    if (!otpDigits.value[index] && index > 0) {
      // Nếu ô hiện tại trống, quay về ô trước đó
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
  otpDigits.value = ['', '', '', '', '', '']
}

const handleRequestOTP = async () => {
  loading.value = true
  try {
    const userId = authStore.user?.accountID || authStore.user?.id
    await sendDeactivateOtp(userId)
    toast.success('Mã OTP đã được gửi tự động về Email của bạn!')
  } catch (err) {
    toast.error(err.response?.data?.message || 'Lỗi gửi mã xác thực.')
  } finally {
    loading.value = false
  }
}

const handleDeactivate = async () => {
  if (otpCode.value.length < 6) return
  loading.value = true
  try {
    await authStore.deactivateAccount(otpCode.value)
    toast.success('Tài khoản đã được xóa mềm. Hẹn gặp lại sếp!')
    close()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Mã OTP không chính xác hoặc đã hết hạn.')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.edit-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
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
  border: 1px solid rgba(255, 255, 255, 0.8);
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
  background: #F1F5F9;
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
  overscroll-behavior: contain;
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

.form-step {
  margin-top: 10px;
}

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
    background: none;
    border: none;
    color: #64748B;
    font-size: 0.9rem;
    cursor: pointer;
    font-weight: 500;
    
    span {
      color: #EA580C;
      font-weight: 700;
      transition: 0.2s;
    }
    
    &:hover span {
      text-decoration: underline;
    }
    
    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }
}

.edit-modal-footer {
  display: flex;
  gap: 12px;
  padding: 20px 32px 24px;
  border-top: 1px solid #F1F5F9;
  background: #F8FAFC;
  flex-shrink: 0;
}

.btn-cancel {
  flex: 1;
  background: #fff;
  border: 1.5px solid #E2E8F0;
  padding: 14px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.95rem;
  color: #475569;
  cursor: pointer;
  transition: 0.3s;

  &:hover {
    background: #F1F5F9;
    color: #0F172A;
  }
}

.btn-danger-fill {
  flex: 1.5;
  background: linear-gradient(135deg, #EF4444, #DC2626);
  border: none;
  padding: 14px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.95rem;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 8px 20px -6px rgba(220, 38, 38, 0.4);
  transition: 0.3s;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 12px 25px -6px rgba(220, 38, 38, 0.5);
  }

  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    filter: grayscale(50%);
    box-shadow: none;
    transform: none;
  }
}

.spinner-sm {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top-color: #fff;
  border-radius: 50%;
  animation: rotate 0.8s linear infinite;
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
  .otp-input-box {
    width: 44px;
    height: 54px;
    font-size: 1.4rem;
    border-radius: 10px;
  }
}
</style>
