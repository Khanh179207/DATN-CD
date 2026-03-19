<template>
  <transition name="modal-motion">
    <div
      v-if="isOpen"
      ref="overlayRef"
      class="modal-overlay"
      role="dialog"
      aria-modal="true"
      @click="$emit('close')"
      @keydown.esc.prevent="$emit('close')"
    >
      <div class="modal-container appeal-modal" @click.stop>
        <button class="btn-close-modal" @click="$emit('close')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>

        <div class="appeal-header">
          <div class="appeal-icon">⚠️</div>
          <h2 class="appeal-title">Khiếu nại ban nhầm</h2>
          <p class="appeal-subtitle">Nếu bạn cho rằng tài khoản bị khóa nhầm, vui lòng gửi khiếu nại kèm bằng chứng</p>
        </div>

        <form @submit.prevent="handleSubmit" class="appeal-form">
          <div class="form-group">
            <label for="appeal-email">Email liên hệ *</label>
            <input
              v-model="formData.email"
              type="email"
              id="appeal-email"
              placeholder="your@email.com"
              required
              class="input-field"
              :disabled="isSubmitting"
            />
            <small class="help-text">Email này sẽ được dùng để xác thực tài khoản của bạn</small>
          </div>

          <div class="form-group">
            <label for="appeal-reason">Lý do bạn nghĩ bị ban nhầm *</label>
            <textarea
              v-model="formData.reason"
              id="appeal-reason"
              placeholder="Mô tả chi tiết lý do bạn tin tài khoản bị ban nhầm..."
              required
              rows="5"
              class="input-field textarea-field"
              :disabled="isSubmitting"
              maxlength="2000"
            ></textarea>
            <small class="help-text">{{ formData.reason.length }}/2000</small>
          </div>

          <div v-if="submitError" class="error-message">
            ❌ {{ submitError }}
          </div>

          <div v-if="submitSuccess" class="success-message">
            ✅ Khiếu nại đã được gửi thành công! Quản trị viên sẽ xem xét trong vòng 24-48 giờ.
          </div>

          <div class="form-actions">
            <button
              v-if="!submitSuccess"
              type="submit"
              class="btn-submit"
              :disabled="isSubmitting || !formData.email.trim() || !formData.reason.trim()"
            >
              <span v-if="isSubmitting" class="spinner"></span>
              <span v-else>Gửi khiếu nại</span>
            </button>
            <button
              v-else
              type="button"
              class="btn-close"
              @click="$emit('close')"
            >
              Đóng
            </button>
          </div>
        </form>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { toast } from '@/composables/useToast'
import { createAppeal } from '@/services/appealService'

const emit = defineEmits(['close'])
const overlayRef = ref(null)

const isOpen = ref(false)
const isSubmitting = ref(false)
const submitError = ref('')
const submitSuccess = ref(false)

const formData = ref({
  email: '',
  reason: ''
})

const handleSubmit = async () => {
  if (!formData.value.email.trim() || !formData.value.reason.trim()) {
    submitError.value = 'Vui lòng điền đầy đủ thông tin'
    console.warn('[AppealModal] Validation failed:', formData.value)
    return
  }

  isSubmitting.value = true
  submitError.value = ''

  console.log('[AppealModal] Starting submit...', {
    email: formData.value.email,
    reasonLength: formData.value.reason.length,
    timestamp: new Date().toISOString()
  })

  try {
    const result = await createAppeal({
      email: formData.value.email.trim(),
      reason: formData.value.reason.trim()
    })

    console.log('[AppealModal] Submit successful!', { result })
    submitSuccess.value = true
    toast.success('Khiếu nại đã được gửi thành công!')

    // Auto close sau 3 giây
    setTimeout(() => {
      emit('close')
    }, 3000)
  } catch (error) {
    console.error('[AppealModal] Submit error caught:', {
      status: error.response?.status,
      message: error.response?.data?.message,
      statusText: error.response?.statusText,
      url: error.config?.url,
      method: error.config?.method,
      fullError: error.message
    })

    submitError.value = error.response?.data?.message || error.message || 'Lỗi khi gửi khiếu nại. Vui lòng thử lại.'
    console.error('[AppealModal] Final error message:', submitError.value)
    toast.error(submitError.value)
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  isOpen.value = true
  nextTick(() => overlayRef.value?.focus())
})
</script>

<style scoped lang="scss">
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 20px;
}

.modal-container {
  background: white;
  border-radius: 16px;
  padding: 40px;
  max-width: 500px;
  width: 100%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  position: relative;
  animation: modalSlideIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes modalSlideIn {
  0% {
    opacity: 0;
    transform: scale(0.9) translateY(20px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.btn-close-modal {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 40px;
  height: 40px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: all 0.3s;

  &:hover {
    background: #ea580c;
    color: white;
    transform: rotate(90deg);
  }
}

.appeal-header {
  text-align: center;
  margin-bottom: 32px;
}

.appeal-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.appeal-title {
  font-size: 24px;
  font-weight: 800;
  color: #1c1917;
  margin: 0 0 8px;
  font-family: 'Playfair Display', serif;
}

.appeal-subtitle {
  color: #78716c;
  font-size: 14px;
  margin: 0;
  line-height: 1.5;
}

.appeal-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
  font-size: 14px;
  color: #1c1917;
}

.input-field {
  padding: 12px 16px;
  border: 2px solid #e7e5e4;
  border-radius: 10px;
  font-size: 14px;
  font-family: 'Quicksand', sans-serif;
  transition: all 0.3s;

  &:focus {
    outline: none;
    border-color: #ea580c;
    box-shadow: 0 0 0 3px rgba(234, 88, 12, 0.1);
  }

  &:disabled {
    background: #f5f5f5;
    cursor: not-allowed;
  }
}

.textarea-field {
  resize: vertical;
  min-height: 120px;
}

.help-text {
  font-size: 12px;
  color: #a8a29e;
}

.error-message {
  padding: 12px 16px;
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
}

.success-message {
  padding: 12px 16px;
  background: rgba(34, 197, 94, 0.1);
  color: #16a34a;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 8px;
}

.btn-submit,
.btn-close {
  padding: 12px 28px;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-submit {
  background: #ea580c;
  color: white;

  &:hover:not(:disabled) {
    background: #c2410c;
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(234, 88, 12, 0.3);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.btn-close {
  background: #e7e5e4;
  color: #1c1917;

  &:hover {
    background: #d6d3d1;
  }
}

.spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.modal-motion-enter-active {
  animation: modalSlideIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.modal-motion-leave-active {
  animation: modalSlideIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) reverse;
}

@media (max-width: 640px) {
  .modal-container {
    padding: 28px 20px;
    border-radius: 12px;
  }

  .appeal-title {
    font-size: 20px;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .btn-submit,
  .btn-close {
    width: 100%;
    justify-content: center;
  }
}
</style>
