<template>
  <transition name="modal-premium-fade">
    <div v-if="show" class="edit-modal-overlay" @click.self="$emit('close')">
      <div class="edit-modal-card">
        
        <div class="edit-modal-header">
          <div class="header-titles">
            <h2>Chỉnh sửa Hồ sơ</h2>
            <p class="sub-heading">Quản lý thông tin cá nhân của bạn</p>
          </div>
          <button class="btn-close-premium" @click="$emit('close')">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>

        <div class="edit-modal-body">
          
          <div class="edit-avatar-section">
            <div class="avatar-wrapper">
              <img :src="form.avatarPreview || user.avatar" class="avatar-img" alt="Avatar">
              <label class="avatar-upload-badge" title="Đổi ảnh đại diện">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
                <input type="file" accept="image/*" class="file-hidden" @change="(e) => $emit('avatar-change', e)">
              </label>
            </div>
          </div>

          <div class="account-summary-box">
            <div class="summary-item">
              <div class="s-icon mail-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
              </div>
              <div class="s-text">
                <span class="s-label">Email liên kết</span>
                <span class="s-value truncate-text" :title="user.email">{{ user.email || 'Chưa cập nhật email' }}</span>
              </div>
            </div>
            <div class="summary-divider"></div>
            <div class="summary-item">
              <div class="s-icon calendar-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
              </div>
              <div class="s-text">
                <span class="s-label">Ngày tham gia</span>
                <span class="s-value">{{ user.createdAt || user.createdDate || 'Thành viên mới' }}</span>
              </div>
            </div>
          </div>

          <div class="premium-field">
            <label for="username">Tên hiển thị</label>
            <input 
              v-model="form.username" 
              type="text" 
              id="username" 
              placeholder="Nhập tên hiển thị của bạn" 
              maxlength="50"
            >
          </div>

          <div class="premium-field">
            <div class="label-row">
              <label for="bio">Giới thiệu bản thân</label>
              <span class="char-count" :class="{'limit-reached': form.bio.length >= 300}">
                {{ form.bio.length }}/300
              </span>
            </div>
            <textarea 
              v-model="form.bio" 
              id="bio" 
              placeholder="Chia sẻ một chút về đam mê ẩm thực của bạn..." 
              rows="3" 
              maxlength="300"
            ></textarea>
          </div>

          <div class="password-accordion">
            <div class="accordion-header" @click="togglePasswordForm">
              <div class="header-left">
                <div class="s-icon lock-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                </div>
                <span>{{ hasPassword ? 'Đổi mật khẩu bảo mật' : 'Thiết lập mật khẩu mới' }}</span>
              </div>
              <svg class="chevron" :class="{'rotated': showPasswordForm}" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="6 9 12 15 18 9"></polyline></svg>
            </div>

            <div v-if="showPasswordForm" class="accordion-body anim-dropdown">
              
              <div v-if="hasPassword" class="premium-field">
                <label>Mật khẩu hiện tại</label>
                <div class="input-wrap">
                  <input :type="showCurrent ? 'text' : 'password'" v-model="pwdForm.current" placeholder="Nhập mật khẩu đang dùng">
                  <button type="button" class="eye-btn" @click="showCurrent = !showCurrent" tabindex="-1">
                    <svg v-if="showCurrent" viewBox="0 0 24 24" fill="none" class="eye-icon"><path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19m-6.72-1.07a3 3 0 11-4.24-4.24M1 1l22 22" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
                    <svg v-else viewBox="0 0 24 24" fill="none" class="eye-icon"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="1.5"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5"/></svg>
                  </button>
                </div>
              </div>

              <div v-else class="google-auth-notice">
                <i class="fas fa-info-circle"></i>
                <p>Tài khoản của bạn đăng nhập bằng Google. Đặt mật khẩu dưới đây để có thể đăng nhập bằng Email và Mật khẩu nhé.</p>
              </div>

              <div class="premium-field">
                <label>Mật khẩu mới</label>
                <div class="input-wrap">
                  <input :type="showNew ? 'text' : 'password'" v-model="pwdForm.new" placeholder="Tối thiểu 8 ký tự, 1 hoa, 1 số">
                  <button type="button" class="eye-btn" @click="showNew = !showNew" tabindex="-1">
                    <svg v-if="showNew" viewBox="0 0 24 24" fill="none" class="eye-icon"><path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19m-6.72-1.07a3 3 0 11-4.24-4.24M1 1l22 22" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
                    <svg v-else viewBox="0 0 24 24" fill="none" class="eye-icon"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="1.5"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5"/></svg>
                  </button>
                </div>
                
                <div v-if="pwdForm.new" class="strength-wrap">
                  <div class="strength-bar">
                    <div class="strength-fill" :class="`strength-fill--${strengthLevel}`" :style="{ width: strengthWidth }"></div>
                  </div>
                  <span class="strength-label" :class="`strength-label--${strengthLevel}`">{{ strengthText }}</span>
                </div>
              </div>

              <div class="premium-field">
                <label>Xác nhận mật khẩu mới</label>
                <div class="input-wrap">
                  <input :type="showConfirm ? 'text' : 'password'" v-model="pwdForm.confirm" placeholder="Nhập lại mật khẩu mới">
                  <button type="button" class="eye-btn" @click="showConfirm = !showConfirm" tabindex="-1">
                    <svg v-if="showConfirm" viewBox="0 0 24 24" fill="none" class="eye-icon"><path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19m-6.72-1.07a3 3 0 11-4.24-4.24M1 1l22 22" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
                    <svg v-else viewBox="0 0 24 24" fill="none" class="eye-icon"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="1.5"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5"/></svg>
                  </button>
                </div>
                <span v-if="pwdMismatch" class="error-text">Mật khẩu xác nhận không khớp!</span>
              </div>

              <div v-if="!otpSent" class="pwd-actions">
                <button class="btn-send-otp" @click.prevent="handleSendOtp" :disabled="!isPwdFormValid || sendingOtp">
                  <span v-if="sendingOtp" class="spinner-sm dark"></span>
                  <span>{{ sendingOtp ? 'Đang gửi mã...' : 'Gửi mã OTP về Email' }}</span>
                </button>
              </div>

              <div v-else class="otp-verification-area">
                <div class="premium-field">
                  <label>Mã xác thực (OTP)</label>
                  <input type="text" v-model="pwdForm.otp" placeholder="Nhập mã 6 số từ Email" maxlength="6">
                </div>
                <button class="btn-confirm-pwd" @click.prevent="handleChangePassword" :disabled="!pwdForm.otp || changingPwd">
                  <span v-if="changingPwd" class="spinner-sm"></span>
                  <span>{{ changingPwd ? 'Đang xử lý...' : (hasPassword ? 'Xác nhận Đổi mật khẩu' : 'Xác nhận Thiết lập mật khẩu') }}</span>
                </button>
              </div>

            </div>
          </div>

          <div class="danger-zone-clean">
            <div class="dz-info">
              <h4>Vùng Nguy Hiểm</h4>
              <p>Bạn muốn tạm nghỉ ngơi? Hành động này sẽ khóa tài khoản.</p>
            </div>
            <button class="btn-danger-outline" @click="$emit('open-deactivate')">
              Xóa tài khoản
            </button>
          </div>

        </div>

        <div class="edit-modal-footer">
          <button class="btn-secondary" @click="$emit('close')">Hủy bỏ</button>
          <button class="btn-primary" :disabled="saving" @click="$emit('save')">
            <span v-if="saving" class="spinner-sm"></span>
            <span>{{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}</span>
          </button>
        </div>
        
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, watch } from 'vue';

const props = defineProps({ 
  show: Boolean, 
  form: Object, 
  user: Object, 
  saving: Boolean,
  sendingOtp: Boolean, 
  changingPwd: Boolean 
});

const emit = defineEmits(['close', 'save', 'avatar-change', 'open-deactivate', 'send-pwd-otp', 'change-password']);

// 🔥 ĐÃ CẬP NHẬT: Tránh lỗi undefined nếu user chưa load kịp
const hasPassword = computed(() => {
  return props.user?.provider !== 'google';
});

// State đổi mật khẩu
const showPasswordForm = ref(false);
const otpSent = ref(false);
const pwdForm = ref({ current: '', new: '', confirm: '', otp: '' });

// Toggles mắt
const showCurrent = ref(false);
const showNew = ref(false);
const showConfirm = ref(false);

// Reset form khi modal đóng
watch(() => props.show, (newVal) => {
  if (!newVal) {
    showPasswordForm.value = false;
    otpSent.value = false;
    pwdForm.value = { current: '', new: '', confirm: '', otp: '' };
    showCurrent.value = false; showNew.value = false; showConfirm.value = false;
  }
});

// Computed Mật khẩu
const pwdMismatch = computed(() => pwdForm.value.confirm.length > 0 && pwdForm.value.new !== pwdForm.value.confirm);

const isPwdFormValid = computed(() => {
  const pw = pwdForm.value.new;
  const hasUppercase = /[A-Z]/.test(pw);
  const hasLowercase = /[a-z]/.test(pw);
  const hasNumber = /\d/.test(pw);
  
  // Nếu user đã có pass, bắt buộc nhập current pass. Nếu đăng nhập bằng Google (chưa có pass), bỏ qua điều kiện current pass.
  const isCurrentPassValid = hasPassword.value ? pwdForm.value.current.length > 0 : true;

  return isCurrentPassValid && 
         pw.length >= 8 && 
         hasUppercase && hasLowercase && hasNumber &&
         pw === pwdForm.value.confirm;
});

// Thanh đo độ mạnh
const strengthLevel = computed(() => {
  const pw = pwdForm.value.new;
  if (!pw) return 'none';
  let score = 0;
  if (pw.length >= 8) score++;
  if (/[A-Z]/.test(pw)) score++;
  if (/[a-z]/.test(pw)) score++;
  if (/\d/.test(pw)) score++;
  if (/[^A-Za-z0-9]/.test(pw)) score++;
  
  if (score <= 1) return 'weak';
  if (score === 2) return 'fair';
  if (score === 3) return 'good';
  return 'strong';
});

const strengthText = computed(() => ({ weak: 'Yếu', fair: 'Trung bình', good: 'Khá', strong: 'Mạnh' }[strengthLevel.value] || ''));
const strengthWidth = computed(() => ({ weak: '25%', fair: '50%', good: '75%', strong: '100%' }[strengthLevel.value] || '0%'));

// Methods
const togglePasswordForm = () => {
  showPasswordForm.value = !showPasswordForm.value;
  if (!showPasswordForm.value) {
    otpSent.value = false;
    pwdForm.value = { current: '', new: '', confirm: '', otp: '' };
  }
};

const handleSendOtp = () => {
  // Đóng gói payload gửi lên. Nếu là Google user, ta có thể gửi current = null
  const payload = {
    ...pwdForm.value,
    isGoogleUser: !hasPassword.value // Để Backend biết mà không check pass cũ
  };
  emit('send-pwd-otp', payload);
  otpSent.value = true;
};

const handleChangePassword = () => {
  const payload = {
    ...pwdForm.value,
    isGoogleUser: !hasPassword.value
  };
  emit('change-password', payload);
};
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

/* Overlay & Modal Card */
.edit-modal-overlay { 
  position: fixed; 
  inset: 0; 
  background: rgba(15, 23, 42, 0.4); 
  backdrop-filter: blur(8px); 
  z-index: 9000; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  padding: 16px; 
  font-family: 'Inter', sans-serif; 
}

.edit-modal-card { 
  background: #ffffff; 
  border-radius: 24px; 
  width: 100%; 
  max-width: 480px; 
  max-height: 90vh; 
  display: flex; 
  flex-direction: column; 
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25); 
  overflow: hidden; 
}

/* Header */
.edit-modal-header { 
  display: flex; 
  align-items: center; 
  justify-content: space-between; 
  padding: 24px 32px 16px; 
  border-bottom: 1px solid #F1F5F9; 
  flex-shrink: 0; 
}
.header-titles h2 { font-size: 1.4rem; margin: 0 0 4px 0; color: #0F172A; font-weight: 800; letter-spacing: -0.5px; }
.header-titles .sub-heading { margin: 0; font-size: 0.9rem; color: #64748B; }
.btn-close-premium { background: #F8FAFC; border: none; width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; color: #64748B; transition: 0.2s; &:hover { background: #E2E8F0; color: #0F172A; transform: scale(1.05); } }

/* Body Scroll */
.edit-modal-body { 
  padding: 24px 32px; 
  display: flex; 
  flex-direction: column; 
  gap: 24px; 
  overflow-y: auto; 
  flex-grow: 1; 

  &::-webkit-scrollbar { width: 6px; } 
  &::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; } 
  &::-webkit-scrollbar-track { background: transparent; }
}

/* Avatar */
.edit-avatar-section { display: flex; justify-content: center; margin-bottom: 4px; }
.avatar-wrapper { position: relative; width: 100px; height: 100px; border-radius: 50%; box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1); }
.avatar-img { width: 100%; height: 100%; object-fit: cover; border-radius: 50%; border: 3px solid #ffffff; background: #F1F5F9; }
.avatar-upload-badge { position: absolute; bottom: 0; right: 0; background: #EA580C; color: #ffffff; width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; border: 3px solid #ffffff; cursor: pointer; transition: 0.2s; box-shadow: 0 4px 10px rgba(234, 88, 12, 0.3); &:hover { background: #C2410C; transform: scale(1.1); } }
.file-hidden { display: none; }

/* Summary Box */
.account-summary-box { background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 16px; padding: 4px 16px; }
.summary-item { display: flex; align-items: center; gap: 16px; padding: 12px 0; }
.s-icon { width: 38px; height: 38px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; &.mail-icon { background: #E0F2FE; color: #0284C7; } &.calendar-icon { background: #FEF3C7; color: #D97706; } &.lock-icon { background: #F1F5F9; color: #475569; } }
.s-text { display: flex; flex-direction: column; min-width: 0; }
.s-label { font-size: 0.75rem; font-weight: 700; color: #64748B; text-transform: uppercase; letter-spacing: 0.5px; }
.s-value { font-size: 0.95rem; font-weight: 600; color: #0F172A; margin-top: 2px; }
.truncate-text { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.summary-divider { height: 1px; background: #E2E8F0; margin: 0; }

/* Basic Inputs */
.premium-field { display: flex; flex-direction: column; gap: 8px; }
.premium-field .label-row { display: flex; justify-content: space-between; align-items: center; }
.premium-field label { font-size: 0.9rem; font-weight: 700; color: #334155; }
.premium-field .char-count { font-size: 0.75rem; color: #94A3B8; font-weight: 600; &.limit-reached { color: #DC2626; } }
.premium-field input, .premium-field textarea { width: 100%; background: #F1F5F9; border: 1.5px solid transparent; border-radius: 12px; padding: 14px 16px; font-size: 1rem; font-weight: 500; color: #0F172A; font-family: inherit; transition: all 0.2s ease; resize: none; box-sizing: border-box; }
.premium-field input:focus, .premium-field textarea:focus { background: #ffffff; border-color: #EA580C; outline: none; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); }
.premium-field .error-text { font-size: 0.8rem; color: #DC2626; font-weight: 600; margin-top: 4px; }

/* Input Wrap & Eye Icon */
.input-wrap { position: relative; }
.eye-btn { position: absolute; right: 14px; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; color: #A8A29E; display: flex; align-items: center; padding: 0; transition: color 0.2s; }
.eye-btn:hover { color: #EA580C; }
.eye-icon { width: 18px; height: 18px; }

/* Strength Bar */
.strength-wrap { display: flex; align-items: center; gap: 10px; margin-top: 4px; }
.strength-bar { flex: 1; height: 4px; background: #E2E8F0; border-radius: 99px; overflow: hidden; }
.strength-fill { height: 100%; border-radius: 99px; transition: width 0.35s ease, background-color 0.35s; }
.strength-fill--weak { background: #EF4444; } .strength-fill--fair { background: #F97316; } .strength-fill--good { background: #EAB308; } .strength-fill--strong { background: #10B981; }
.strength-label { font-size: 0.75rem; font-weight: 700; white-space: nowrap; }
.strength-label--weak { color: #EF4444; } .strength-label--fair { color: #F97316; } .strength-label--good { color: #EAB308; } .strength-label--strong { color: #10B981; }

/* Google Notice */
.google-auth-notice { background: #EFF6FF; border: 1px solid #BFDBFE; border-radius: 12px; padding: 12px 16px; display: flex; gap: 12px; align-items: flex-start; color: #1E3A8A; font-size: 0.85rem; line-height: 1.5; i { margin-top: 3px; color: #3B82F6; font-size: 1.1rem; } p { margin: 0; } }

/* Accordion */
.password-accordion { border: 1px solid #E2E8F0; border-radius: 16px; overflow: hidden; background: #ffffff; flex-shrink: 0;}
.accordion-header { display: flex; justify-content: space-between; align-items: center; padding: 16px; cursor: pointer; background: #F8FAFC; transition: background 0.2s; &:hover { background: #F1F5F9; } }
.accordion-header .header-left { display: flex; align-items: center; gap: 12px; font-weight: 700; color: #0F172A; }
.chevron { color: #64748B; transition: transform 0.3s ease; &.rotated { transform: rotate(180deg); } }
.accordion-body { padding: 20px 16px; display: flex; flex-direction: column; gap: 16px; border-top: 1px solid #E2E8F0; }
.anim-dropdown { animation: slideDown 0.3s ease-out; }
@keyframes slideDown { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }

/* Buttons OTP */
.btn-send-otp { width: 100%; background: #F8FAFC; color: #0F172A; border: 1.5px solid #CBD5E1; padding: 12px; border-radius: 12px; font-weight: 700; cursor: pointer; display: flex; justify-content: center; align-items: center; gap: 8px; transition: 0.2s; &:hover:not(:disabled) { background: #F1F5F9; border-color: #94A3B8; } &:disabled { opacity: 0.5; cursor: not-allowed; } }
.otp-verification-area { display: flex; flex-direction: column; gap: 16px; background: #FEFCE8; padding: 16px; border-radius: 12px; border: 1px dashed #FCD34D; }
.btn-confirm-pwd { width: 100%; background: #10B981; color: #ffffff; border: none; padding: 14px; border-radius: 12px; font-weight: 700; cursor: pointer; display: flex; justify-content: center; align-items: center; gap: 8px; box-shadow: 0 4px 10px rgba(16, 185, 129, 0.2); transition: 0.2s; &:hover:not(:disabled) { background: #059669; transform: translateY(-2px); } &:disabled { background: #A7F3D0; cursor: not-allowed; transform: none; box-shadow: none; } }

/* Danger Zone */
.danger-zone-clean { margin-top: 8px; padding: 16px 20px; border: 1px solid #FECACA; border-radius: 16px; background: #FEF2F2; display: flex; align-items: center; justify-content: space-between; gap: 16px; flex-shrink: 0; }
.danger-zone-clean .dz-info h4 { margin: 0 0 4px 0; font-size: 0.95rem; color: #991B1B; font-weight: 700; }
.danger-zone-clean .dz-info p { margin: 0; font-size: 0.8rem; color: #B91C1C; line-height: 1.4; }
.btn-danger-outline { flex-shrink: 0; background: #ffffff; color: #DC2626; border: 1px solid #FCA5A5; padding: 8px 16px; border-radius: 10px; font-size: 0.85rem; font-weight: 700; cursor: pointer; transition: 0.2s; &:hover { background: #DC2626; color: #ffffff; border-color: #DC2626; } }

/* Footer */
.edit-modal-footer { display: flex; gap: 12px; padding: 20px 32px; border-top: 1px solid #F1F5F9; background: #ffffff; flex-shrink: 0; }
.btn-secondary { flex: 1; background: #F8FAFC; border: 1px solid #E2E8F0; padding: 14px; border-radius: 14px; font-weight: 700; font-size: 0.95rem; color: #475569; cursor: pointer; transition: 0.2s; &:hover { background: #E2E8F0; color: #0F172A; } }
.btn-primary { flex: 2; background: #EA580C; border: none; padding: 14px; border-radius: 14px; font-weight: 700; font-size: 0.95rem; color: #ffffff; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px; box-shadow: 0 4px 12px rgba(234, 88, 12, 0.2); transition: 0.2s; &:hover:not(:disabled) { background: #C2410C; transform: translateY(-2px); box-shadow: 0 6px 16px rgba(234, 88, 12, 0.3); } &:disabled { opacity: 0.6; cursor: not-allowed; box-shadow: none; } }

/* Spinners & Transitions */
.spinner-sm { width: 18px; height: 18px; border: 2px solid rgba(255, 255, 255, 0.4); border-top-color: #fff; border-radius: 50%; animation: spin 0.8s linear infinite; }
.spinner-sm.dark { border: 2px solid rgba(15, 23, 42, 0.2); border-top-color: #0F172A; }
@keyframes spin { to { transform: rotate(360deg); } }
.modal-premium-fade-enter-active, .modal-premium-fade-leave-active { transition: opacity 0.25s ease; .edit-modal-card { transition: transform 0.25s cubic-bezier(0.175, 0.885, 0.32, 1.275); } }
.modal-premium-fade-enter-from, .modal-premium-fade-leave-to { opacity: 0; .edit-modal-card { transform: scale(0.95) translateY(10px); } }

/* Responsive */
@media (max-width: 576px) {
  .edit-modal-overlay { padding: 12px; }
  .edit-modal-card { border-radius: 20px; }
  .edit-modal-header { padding: 20px 24px 16px; .header-titles h2 { font-size: 1.25rem; } }
  .edit-modal-body { padding: 16px 24px 20px; gap: 20px; }
  .danger-zone-clean { flex-direction: column; align-items: stretch; .btn-danger-outline { margin-top: 8px; } }
  .edit-modal-footer { padding: 16px 24px 20px; flex-direction: column-reverse; }
}
</style>