<template>
  <transition name="modal-fade">
    <div v-if="modelValue" class="deactivate-overlay" @click.self="close">
      <div class="deactivate-card">
        <div class="header">
          <div class="icon-box warning">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
              <line x1="12" y1="9" x2="12" y2="13"></line>
              <line x1="12" y1="17" x2="12.01" y2="17"></line>
            </svg>
          </div>
          <h2>Xác nhận xóa tài khoản</h2>
          <p class="subtitle">Hành động này sẽ ẩn tài khoản và tất cả bài viết của bạn. Bạn có thể khôi phục lại bất kỳ lúc nào bằng cách đăng nhập lại.</p>
        </div>

        <div class="body">
          <!-- Chỉ một bước duy nhất: Nhập OTP -->
          <div class="form-step">
            <div class="otp-hint">
              Mã xác thực đã được gửi tự động đến Email của bạn. <br>
              Vui lòng nhập mã gồm 6 chữ số để xác nhận xóa.
            </div>
            
            <div class="otp-input-container">
              <input 
                v-model="otp" 
                type="text" 
                placeholder="000000" 
                maxlength="6" 
                class="otp-input-lux"
                @keyup.enter="handleDeactivate"
                autofocus
              >
              <div class="input-focus-glow"></div>
            </div>

            <div class="actions">
              <button class="btn-cancel-lux" @click="close">Hủy bỏ</button>
              <button class="btn-confirm-delete-lux" :disabled="otp.length < 6 || loading" @click="handleDeactivate">
                <span v-if="loading" class="spinner-sm"></span>
                <span>Xác nhận Xóa tài khoản</span>
              </button>
            </div>

            <div class="resend-section">
              <button class="btn-resend" :disabled="loading" @click="handleRequestOTP">
                Chưa nhận được mã? <span>Gửi lại ngay</span>
              </button>
            </div>
          </div>
        </div>

        <button class="btn-close-abs" @click="close">✕</button>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch } from 'vue'
import { sendDeactivateOtp } from '@/services/authService'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

const authStore = useAuthStore()
const otp = ref('')
const loading = ref(false)

// 🔥 TỰ ĐỘNG GỬI OTP KHI MỞ MODAL
watch(() => props.modelValue, (isOpen) => {
  if (isOpen) {
    handleRequestOTP()
  }
})

const close = () => {
  if (loading.value) return
  emit('update:modelValue', false)
  otp.value = ''
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
  if (otp.value.length < 6) return
  loading.value = true
  try {
    await authStore.deactivateAccount(otp.value)
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
.deactivate-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.8);
  backdrop-filter: blur(15px);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.deactivate-card {
  background: #121212;
  border: 1px solid rgba(234, 88, 12, 0.2);
  border-radius: 32px;
  width: 100%;
  max-width: 460px;
  padding: 48px;
  position: relative;
  box-shadow: 0 40px 100px -20px rgba(0,0,0,0.8), 0 0 40px -10px rgba(234, 88, 12, 0.1);
  animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;

  &::before {
    content: "";
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(234, 88, 12, 0.05) 0%, transparent 70%);
    pointer-events: none;
  }
}

@keyframes slideUp {
  from { transform: translateY(40px) scale(0.95); opacity: 0; }
  to { transform: translateY(0) scale(1); opacity: 1; }
}

.header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;

  .icon-box {
    width: 80px;
    height: 80px;
    border-radius: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 24px;
    background: linear-gradient(135deg, rgba(234, 88, 12, 0.2) 0%, rgba(239, 68, 68, 0.1) 100%);
    color: #EA580C;
    border: 1px solid rgba(234, 88, 12, 0.3);
    box-shadow: 0 10px 20px rgba(234, 88, 12, 0.1);
    
    svg {
      filter: drop-shadow(0 0 8px rgba(234, 88, 12, 0.4));
    }
  }

  h2 {
    color: #fff;
    font-size: 28px;
    font-weight: 900;
    margin-bottom: 16px;
    letter-spacing: -1px;
    background: linear-gradient(to right, #fff, #A8A29E);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .subtitle {
    color: #78716C;
    font-size: 15px;
    line-height: 1.6;
    margin: 0 auto;
    max-width: 320px;
  }
}

.body {
  position: relative;
  
  .form-step {
    display: flex;
    flex-direction: column;
    gap: 24px;

    .otp-input-container {
      position: relative;
      
      .otp-input-lux {
        background: rgba(255,255,255,0.03);
        border: 2px solid rgba(255,255,255,0.08);
        border-radius: 20px;
        padding: 24px;
        text-align: center;
        font-size: 40px;
        font-weight: 900;
        letter-spacing: 16px;
        color: #EA580C;
        width: 100%;
        transition: all 0.3s;
        text-shadow: 0 0 15px rgba(234, 88, 12, 0.3);

        &:focus {
          border-color: #EA580C;
          outline: none;
          background: rgba(255,255,255,0.06);
          box-shadow: 0 0 0 8px rgba(234, 88, 12, 0.05);
        }
      }

      .input-focus-glow {
        position: absolute;
        inset: -2px;
        border-radius: 22px;
        background: linear-gradient(90deg, #EA580C, #EF4444);
        opacity: 0;
        transition: opacity 0.3s;
        z-index: -1;
      }
      
      .otp-input-lux:focus + .input-focus-glow {
        opacity: 0.3;
      }
    }

    .otp-hint {
      background: rgba(234, 88, 12, 0.08);
      border-radius: 16px;
      color: #FDBA74;
      padding: 18px;
      font-size: 14px;
      text-align: center;
      line-height: 1.6;
      border: 1px solid rgba(234, 88, 12, 0.15);
    }

    .btn-confirm-delete-lux {
      width: 100%;
      height: 60px;
      border-radius: 20px;
      font-weight: 800;
      font-size: 17px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
      cursor: pointer;
      transition: all 0.3s;
      background: linear-gradient(135deg, #EF4444 0%, #B91C1C 100%);
      color: #fff;
      border: none;
      box-shadow: 0 15px 30px rgba(239, 68, 68, 0.2);

      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 20px 40px rgba(239, 68, 68, 0.3);
      }

      &:disabled { opacity: 0.4; cursor: not-allowed; }
    }

    .btn-cancel-lux {
      background: rgba(255,255,255,0.05);
      color: #A8A29E;
      border: 1px solid rgba(255,255,255,0.1);
      border-radius: 20px;
      font-weight: 700;
      font-size: 16px;
      height: 60px;
      cursor: pointer;
      &:hover { background: rgba(255,255,255,0.1); color: #fff; }
    }

    .actions {
      display: grid;
      grid-template-columns: 1fr 2fr;
      gap: 16px;
    }

    .resend-section {
      text-align: center;
      .btn-resend {
        background: none;
        border: none;
        color: #78716C;
        font-size: 14px;
        cursor: pointer;
        span { color: #EA580C; font-weight: 700; &:hover { text-decoration: underline; } }
      }
    }
  }
}

.btn-close-abs {
  position: absolute;
  top: 24px;
  right: 24px;
  background: rgba(255,255,255,0.05);
  border: none;
  color: #78716C;
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  &:hover { background: rgba(255,255,255,0.1); color: #fff; transform: rotate(90deg); }
}

.spinner-sm {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: rotate 0.8s linear infinite;
}

@keyframes rotate { to { transform: rotate(360deg); } }

.modal-fade-enter-active, .modal-fade-leave-active { transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; backdrop-filter: blur(0); }
</style>
