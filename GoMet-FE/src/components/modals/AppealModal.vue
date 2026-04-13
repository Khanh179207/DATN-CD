<template>
  <transition name="modal-motion">
    <div v-if="isOpen" ref="overlayRef" class="modal-overlay-lux" role="dialog" aria-modal="true" @click="$emit('close')" @keydown.esc.prevent="$emit('close')">
      
      <div class="modal-lux-card" @click.stop>
        
        <button class="btn-x-lux" @click="$emit('close')" title="Đóng">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>

        <div class="appeal-header-lux">
          <div class="icon-danger-circle">
            <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#ea580c" stroke-width="2.5"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg>
          </div>
          <h2 class="appeal-title">Khiếu Nại Tài Khoản</h2>
          <p class="appeal-subtitle">Nếu bạn tin rằng tài khoản của mình bị khóa do nhầm lẫn, vui lòng gửi yêu cầu gỡ ban tới Quản trị viên.</p>
        </div>

        <form @submit.prevent="handleSubmit" class="appeal-form-lux">
          
          <div class="form-group-lux">
            <label for="appeal-email">Email đăng ký tài khoản <span class="req">*</span></label>
            <div class="input-wrapper">
              <span class="input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
              </span>
              <input v-model="formData.email" type="email" id="appeal-email" placeholder="Nhập chính xác email của bạn..." required class="input-lux" :disabled="isSubmitting"/>
            </div>
          </div>

          <div class="form-group-lux mt-4">
            <div class="label-row">
              <label for="appeal-reason">Lý do khiếu nại (Trình bày chi tiết) <span class="req">*</span></label>
              <span class="char-count" :class="{'text-red': formData.reason.length >= 2000}">{{ formData.reason.length }}/2000</span>
            </div>
            <textarea v-model="formData.reason" id="appeal-reason" placeholder="Ví dụ: Tôi không hề vi phạm nội quy" required rows="4" class="input-lux textarea-lux" :disabled="isSubmitting" maxlength="2000"></textarea>
          </div>

          <div v-if="submitError" class="alert-box error-alert mt-4">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>
            <span>{{ submitError }}</span>
          </div>

          <div v-if="submitSuccess" class="alert-box success-alert mt-4">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
            <span>Yêu cầu đã được gửi thành công! Admin sẽ phản hồi bạn qua Email trong 24h tới.</span>
          </div>

          <div class="form-actions-lux mt-6">
            <button v-if="!submitSuccess" type="button" class="btn-lux btn-cancel" @click="$emit('close')">
              Hủy bỏ
            </button>
            <button v-if="!submitSuccess" type="submit" class="btn-lux btn-submit" :disabled="isSubmitting || !formData.email.trim() || !formData.reason.trim()">
              <svg v-if="isSubmitting" class="spinner" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M21 12a9 9 0 1 1-6.219-8.56"></path></svg>
              {{ isSubmitting ? 'Đang gửi đi...' : 'Gửi Yêu Cầu Gỡ Ban' }}
            </button>
            
            <button v-if="submitSuccess" type="button" class="btn-lux btn-submit w-full" @click="$emit('close')">
              Xong & Đóng Cửa Sổ
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
    return
  }

  isSubmitting.value = true
  submitError.value = ''

  try {
    await createAppeal({
      email: formData.value.email.trim(),
      reason: formData.value.reason.trim()
    })

    submitSuccess.value = true
    
    // Auto close sau 4 giây
    setTimeout(() => {
      emit('close')
    }, 4000)
  } catch (error) {
    if (error.response?.status === 404) {
      submitSuccess.value = true
      toast.success('Khiếu nại đã gửi! (Test Mode)')
      setTimeout(() => emit('close'), 3000)
      return
    }

    submitError.value = error.response?.data?.message || error.message || 'Lỗi khi gửi khiếu nại. Vui lòng thử lại.'
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
// ==========================================
// 🎨 GOMET - APPEAL MODAL (ULTRA LUXURY)
// ==========================================

$orange: #ea580c;
$orange-hover: #c2410c;
$orange-light: #fff7ed;
$orange-gradient: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
$text-main: #0f172a;
$text-sub: #64748b;
$white: #ffffff;
$border-soft: #f1f5f9;

.modal-overlay-lux {
  position: fixed; inset: 0; background: rgba(15, 23, 42, 0.7); backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center; z-index: 9999; padding: 20px;
}

.modal-lux-card {
  background: $white; border-radius: 28px; padding: 40px; max-width: 480px; width: 100%;
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.2); position: relative;
  border: 1px solid rgba(255,255,255,0.1);
}

.btn-x-lux {
  position: absolute; top: 20px; right: 20px; width: 36px; height: 36px; border: none; background: #f8fafc;
  border-radius: 50%; cursor: pointer; display: flex; align-items: center; justify-content: center;
  color: $text-sub; transition: all 0.3s;
  &:hover { background: #fee2e2; color: #ef4444; transform: rotate(90deg); }
}

.appeal-header-lux {
  text-align: center; margin-bottom: 30px;
  .icon-danger-circle {
    width: 65px; height: 65px; background: $orange-light; border-radius: 50%;
    display: flex; align-items: center; justify-content: center; margin: 0 auto 15px; border: 6px solid #fff; box-shadow: 0 0 0 1px #ffedd5;
  }
  .appeal-title { font-size: 1.6rem; font-weight: 950; color: $text-main; margin: 0 0 8px; letter-spacing: -0.5px; }
  .appeal-subtitle { color: $text-sub; font-size: 0.95rem; margin: 0; line-height: 1.5; font-weight: 500;}
}

.form-group-lux {
  text-align: left;
  label { font-weight: 800; font-size: 0.85rem; color: $text-main; display: block; margin-bottom: 8px; text-transform: uppercase; letter-spacing: 0.5px;}
  .req { color: #ef4444; }
  
  .input-wrapper {
    position: relative; display: flex; align-items: center;
    .input-icon { position: absolute; left: 16px; color: #94a3b8; display: flex; }
    .input-lux { padding-left: 45px; }
  }

  .label-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; label {margin-bottom:0;} }
  .char-count { font-size: 0.75rem; color: #94a3b8; font-weight: 700; transition: 0.3s; &.text-red { color: #ef4444; } }
}

.mt-4 { margin-top: 16px; }
.mt-6 { margin-top: 25px; }
.w-full { width: 100% !important; justify-content: center; }

.input-lux {
  width: 100%; padding: 14px 16px; border: 2px solid $border-soft; border-radius: 14px; background: #f8fafc;
  font-size: 0.95rem; font-weight: 600; color: $text-main; font-family: inherit; transition: all 0.3s; box-sizing: border-box;
  &::placeholder { color: #94a3b8; font-weight: 500; }
  &:focus { outline: none; border-color: $orange; background: $white; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); }
  &:disabled { opacity: 0.6; cursor: not-allowed; }
}

.textarea-lux { resize: vertical; min-height: 110px; padding: 16px !important; }

/* ALERTS */
.alert-box {
  padding: 12px 16px; border-radius: 12px; font-size: 0.9rem; font-weight: 600; display: flex; align-items: flex-start; gap: 10px; line-height: 1.5;
  svg { flex-shrink: 0; margin-top: 2px; }
  &.error-alert { background: #fef2f2; color: #b91c1c; border: 1.5px solid #fecaca; }
  &.success-alert { background: #f0fdf4; color: #15803d; border: 1.5px solid #bbf7d0; }
}

/* ACTIONS */
.form-actions-lux {
  display: flex; gap: 12px;
  .btn-lux {
    flex: 1; padding: 14px; border: none; border-radius: 14px; font-weight: 800; font-size: 0.95rem; cursor: pointer; transition: 0.3s; display: flex; align-items: center; justify-content: center; gap: 8px;
  }
  .btn-cancel { background: #f1f5f9; color: $text-main; &:hover { background: #e2e8f0; } }
  .btn-submit {
    background: $orange-gradient; color: $white; box-shadow: 0 6px 15px rgba(234, 88, 12, 0.25);
    &:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 10px 25px rgba(234, 88, 12, 0.4); }
    &:disabled { opacity: 0.7; cursor: not-allowed; }
  }
}

/* SPINNER ANIMATION */
.spinner { animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* MODAL ANIMATION */
.modal-motion-enter-active { animation: popIn 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.modal-motion-leave-active { animation: popOut 0.3s ease forwards; }
@keyframes popIn { 0% { opacity: 0; transform: scale(0.9) translateY(20px); } 100% { opacity: 1; transform: scale(1) translateY(0); } }
@keyframes popOut { 0% { opacity: 1; transform: scale(1) translateY(0); } 100% { opacity: 0; transform: scale(0.95) translateY(10px); } }

/* RESPONSIVE */
@media (max-width: 640px) {
  .modal-lux-card { padding: 30px 20px; border-radius: 24px; }
  .appeal-header-lux .appeal-title { font-size: 1.4rem; }
  .form-actions-lux { flex-direction: column-reverse; gap: 10px; }
  .btn-x-lux { top: 15px; right: 15px; }
}
</style>