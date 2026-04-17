<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content fade-in-anim">
      
      <div class="icon-ring">
        <svg xmlns="http://www.w3.org/2000/svg" class="mail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
        </svg>
      </div>

      <h3 class="modal-title">{{ $t('auth.otp_title', 'Xác thực Email') }}</h3>
      <p class="modal-desc">
        {{ $t('auth.otp_sub', 'Mã gồm 6 chữ số đã được gửi tới') }}<br>
        <strong class="highlight-email">{{ email }}</strong>
      </p>

      <div class="otp-group">
        <input
          v-for="(n, i) in 6"
          :key="i"
          ref="inputRefs"
          v-model="digits[i]"
          type="text"
          inputmode="numeric"
          pattern="[0-9]*"
          maxlength="1"
          class="otp-input-box"
          @input="handleInput($event, i)"
          @keydown="handleKeydown($event, i)"
          @paste="handlePaste"
          @keyup.enter="handleEnter"
        />
      </div>

      <div class="modal-footer-text">
        <p>{{ $t('auth.otp_check', 'Vui lòng kiểm tra cả hộp thư rác.') }}</p>
      </div>

      <div class="action-buttons">
        <button @click="$emit('close')" class="btn-secondary">{{ $t('auth.otp_back', 'Quay lại') }}</button>
        
        <button 
          @click="$emit('verify')" 
          class="btn-primary" 
          :disabled="isSubmitDisabled"
        >
          {{ $t('common.confirm', 'Xác nhận OTP') }}
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick, computed } from 'vue'

const props = defineProps({
  email: String,
  modelValue: String // Dùng để binding v-model với parent (otpCode)
})

const emit = defineEmits(['close', 'verify', 'update:modelValue'])

const digits = ref(['', '', '', '', '', ''])
const inputRefs = ref([])

// Trạng thái nút submit (chỉ cho bấm khi đủ 6 số)
const isSubmitDisabled = computed(() => digits.value.join('').length < 6)

// Khi người dùng gõ vào các ô, gộp lại thành 1 chuỗi và gửi ra ngoài (v-model)
watch(digits, (newVal) => {
  const code = newVal.join('')
  emit('update:modelValue', code)
}, { deep: true })

// Nếu parent reset otpCode = '', tự động làm sạch 6 ô
watch(() => props.modelValue, (newVal) => {
  if (!newVal) {
    digits.value = ['', '', '', '', '', '']
  }
})

// Auto-focus vào ô đầu tiên khi mở modal
onMounted(() => {
  nextTick(() => {
    if (inputRefs.value[0]) {
      inputRefs.value[0].focus()
    }
  })
})

// Chuyển ô khi nhập
const handleInput = (event, index) => {
  const val = event.target.value
  // Chỉ cho phép nhập số
  if (!/^\d*$/.test(val)) {
    digits.value[index] = ''
    return
  }
  if (val && index < 5) {
    inputRefs.value[index + 1].focus()
  }
}

// Xóa lùi mượt mà
const handleKeydown = (event, index) => {
  if (event.key === 'Backspace' && !digits.value[index] && index > 0) {
    inputRefs.value[index - 1].focus()
  }
}

// Enter để submit
const handleEnter = () => {
  if (!isSubmitDisabled.value) {
    emit('verify')
  }
}

// Hỗ trợ người dùng Copy - Paste mã OTP thẳng vào 1 ô (sẽ tự điền 6 ô)
const handlePaste = (event) => {
  event.preventDefault()
  const pasteData = event.clipboardData.getData('text').slice(0, 6)
  
  if (/^\d+$/.test(pasteData)) {
    const arr = pasteData.split('')
    for (let i = 0; i < arr.length; i++) {
      digits.value[i] = arr[i]
    }
    // Chuyển focus đến ô trống tiếp theo hoặc ô cuối cùng
    const nextIndex = Math.min(arr.length, 5)
    inputRefs.value[nextIndex]?.focus()
  }
}
</script>

<style scoped>
/* --- OVERLAY --- */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(0, 0, 0, 0.75); 
  backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center;
  z-index: 10000; /* Nằm trên cả Header Landing */
}

/* --- MODAL BOX --- */
.modal-content {
  background: white; padding: 40px 30px; 
  border-radius: 24px; /* Bo góc chuẩn form auth */
  width: 90%; max-width: 420px; text-align: center;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.4);
  font-family: 'Quicksand', sans-serif;
}

/* --- ICON TÔNG CAM GOMET --- */
.icon-ring {
  width: 70px; height: 70px; margin: 0 auto 20px;
  background: #FFF7ED; /* Cam rất nhạt */
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 0 20px rgba(249, 115, 22, 0.15);
}
.mail-icon { width: 34px; height: 34px; color: #F97316; } /* Cam Gomet */

/* --- TEXT --- */
.modal-title {
  font-family: 'Playfair Display', serif; font-size: 1.8rem; 
  color: #1C1917; margin-bottom: 10px; font-weight: 800;
}
.modal-desc { color: #78716C; font-size: 0.95rem; line-height: 1.6; margin-bottom: 25px; }
.highlight-email { color: #F97316; font-weight: 700; letter-spacing: 0.5px; }

/* --- 6 Ô NHẬP OTP --- */
.otp-group { 
  display: flex; gap: 10px; justify-content: center; margin-bottom: 20px; 
}
.otp-input-box {
  width: 48px; height: 56px; font-size: 1.5rem; text-align: center;
  border: 2px solid #E7E5E4; border-radius: 12px; outline: none; 
  background: #FAFAF9; color: #1C1917; font-weight: 700; 
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: 'Quicksand', sans-serif;
}
.otp-input-box:focus { 
  border-color: #F97316; background: white; 
  box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.15); 
  transform: translateY(-2px); 
}

.modal-footer-text { font-size: 0.85rem; color: #A8A29E; margin-bottom: 30px; font-style: italic; }

/* --- BUTTONS --- */
.action-buttons { display: flex; gap: 15px; }

.btn-secondary, .btn-primary {
  flex: 1; padding: 14px; border-radius: 12px; font-weight: 700; 
  cursor: pointer; transition: 0.3s; font-family: 'Quicksand', sans-serif;
  font-size: 0.95rem;
}

.btn-secondary { background: white; color: #57534E; border: 1px solid #E7E5E4; }
.btn-secondary:hover { background: #F5F5F4; color: #1C1917; }

.btn-primary { background: #1C1917; color: white; border: none; }
.btn-primary:hover:not(:disabled) { 
  background: #F97316; 
  transform: translateY(-2px); 
  box-shadow: 0 8px 20px rgba(249, 115, 22, 0.3); 
}
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }

/* --- ANIMATION --- */
.fade-in-anim { animation: zoomIn 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
@keyframes zoomIn { 
  from { opacity: 0; transform: scale(0.95) translateY(10px); } 
  to { opacity: 1; transform: scale(1) translateY(0); } 
}

@media (max-width: 768px) {
  .modal-content {
    width: 85%;
    max-width: 400px;
    padding: 35px 25px;
    border-radius: 20px;
  }
  
  .icon-ring {
    width: 60px; height: 60px;
    margin-bottom: 15px;
  }
  .mail-icon { width: 28px; height: 28px; }
  
  .modal-title { font-size: 1.6rem; }
  .modal-desc { font-size: 0.9rem; margin-bottom: 20px; }
  
  .otp-group { gap: 8px; margin-bottom: 15px; }
  .otp-input-box {
    width: 44px; height: 52px;
    font-size: 1.4rem;
    border-radius: 10px;
  }
  
  .modal-footer-text { margin-bottom: 25px; font-size: 0.8rem; }
  
  .btn-secondary, .btn-primary { padding: 12px; font-size: 0.9rem; }
}

/* --- 2. Màn hình Mobile lớn (Dưới 480px) --- */
@media (max-width: 480px) {
  .modal-overlay { padding: 15px; }
  
  .modal-content {
    width: 100%;
    padding: 30px 20px;
    border-radius: 16px;
  }
  
  .icon-ring { width: 50px; height: 50px; }
  .mail-icon { width: 24px; height: 24px; }
  
  .modal-title { font-size: 1.4rem; }
  .modal-desc { font-size: 0.85rem; line-height: 1.5; }
  
  .otp-group { gap: 6px; }
  .otp-input-box {
    width: 40px; height: 48px;
    font-size: 1.25rem;
    border-radius: 8px;
  }
  
  /* Ép 2 nút Quay lại & Xác nhận xếp chồng lên nhau trên mobile để dễ bấm */
  .action-buttons { 
    flex-direction: column-reverse; /* Nút Xác nhận nằm trên, Quay lại nằm dưới */
    gap: 10px; 
  }
  
  .btn-secondary, .btn-primary {
    width: 100%;
    padding: 12px;
    font-size: 0.95rem;
  }
}

/* --- 3. Màn hình Mobile siêu nhỏ (Dưới 360px - vd iPhone SE) --- */
@media (max-width: 360px) {
  .modal-content { padding: 20px 15px; }
  .modal-title { font-size: 1.25rem; margin-bottom: 5px; }
  .modal-desc { font-size: 0.8rem; margin-bottom: 15px; }
  
  /* Nếu màn hình quá hẹp, thu nhỏ tiếp ô OTP */
  .otp-group { gap: 4px; }
  .otp-input-box {
    width: 36px; height: 44px;
    font-size: 1.1rem;
    border-radius: 6px;
  }
}

/* --- 4. Xử lý khi xoay ngang điện thoại (Landscape Mobile) --- */
@media (max-height: 500px) and (orientation: landscape) {
  .modal-content {
    padding: 20px;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .icon-ring { display: none; /* Ẩn icon để lấy không gian cho form nhập */ }
  .modal-title { font-size: 1.3rem; margin-bottom: 5px; }
  .modal-desc { margin-bottom: 10px; }
  .otp-group { margin-bottom: 10px; }
  .modal-footer-text { display: none; /* Ẩn text phụ */ }
  
  .action-buttons { flex-direction: row; gap: 10px; }
  .btn-secondary, .btn-primary { padding: 10px; }
}
</style>