<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content fade-in-anim">
      <div class="icon-wrapper">
        <svg xmlns="http://www.w3.org/2000/svg" class="mono-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
        </svg>
      </div>

      <h3 class="modal-title">{{ $t('auth.otp_title') }}</h3>
      <p class="modal-desc">
        {{ $t('auth.otp_sub') }} <strong>{{ email }}</strong>.<br>
        {{ $t('auth.otp_check') }}
      </p>

      <div class="input-wrapper">
        <input 
          :value="modelValue" 
          @input="$emit('update:modelValue', $event.target.value)"
          type="text" 
          maxlength="6" 
          placeholder="000 000" 
          class="otp-input"
        />
      </div>

      <div class="action-buttons">
        <button @click="$emit('close')" class="btn-secondary">{{ $t('auth.otp_back') }}</button>
        <button @click="$emit('verify')" class="btn-primary">{{ $t('common.confirm') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  email: String,
  modelValue: String // Used for two-way v-model binding
})
defineEmits(['close', 'verify', 'update:modelValue'])
</script>

<style scoped>
/* Styles synchronized with the main auth form */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(28, 25, 23, 0.8); /* Darker backdrop for focus */
  backdrop-filter: blur(5px);
  display: flex; align-items: center; justify-content: center;
  z-index: 100;
}

.modal-content {
  background: white; padding: 40px 30px; 
  border-radius: 24px; /* Rounded corners matching the login card */
  width: 90%; max-width: 400px; text-align: center;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  font-family: 'Quicksand', sans-serif;
}

.icon-wrapper {
  width: 60px; height: 60px; margin: 0 auto 20px;
  background: #F5F5F4; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
}
.mono-icon { width: 30px; height: 30px; color: #1C1917; } /* Monochrome black icon */

.modal-title {
  font-family: 'Playfair Display', serif; font-size: 1.5rem; 
  color: #1C1917; margin-bottom: 10px; font-weight: 700;
}
.modal-desc { color: #78716C; font-size: 0.95rem; line-height: 1.5; margin-bottom: 25px; }

.otp-input {
  width: 100%; padding: 15px; font-size: 1.5rem; text-align: center; letter-spacing: 8px;
  border: 1px solid #E7E5E4; border-radius: 12px; outline: none; background: #FAFAF9;
  color: #1C1917; font-weight: 700; transition: 0.2s; margin-bottom: 25px;
}
.otp-input:focus { border-color: #1C1917; background: white; }

.action-buttons { display: flex; gap: 15px; }
.btn-primary, .btn-secondary {
  flex: 1; padding: 12px; border-radius: 10px; font-weight: 700; cursor: pointer; transition: 0.2s;
}
.btn-primary { background: #1C1917; color: white; border: none; }
.btn-primary:hover { background: #F97316; }

.btn-secondary { background: white; color: #57534E; border: 1px solid #E7E5E4; }
.btn-secondary:hover { background: #F5F5F4; color: #1C1917; }

.fade-in-anim { animation: zoomIn 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
@keyframes zoomIn { from { opacity: 0; transform: scale(0.9); } to { opacity: 1; transform: scale(1); } }
</style>