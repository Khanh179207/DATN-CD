<template>
  <transition name="modal-fade">
    <div v-if="show" class="edit-modal-overlay" @click.self="$emit('close')">
      <div class="edit-modal-card">
        <div class="edit-modal-header">
          <h2>Chỉnh sửa Hồ sơ</h2>
          <button class="btn-close" @click="$emit('close')">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>

        <div class="edit-modal-body">
          <div class="edit-avatar-section">
            <div class="edit-avatar-wrap">
              <img :src="form.avatarPreview || user.avatar" class="edit-avatar-img" alt="Avatar">
              <label class="edit-avatar-overlay" title="Thay đổi ảnh">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
                <input type="file" accept="image/*" class="file-hidden" @change="(e) => $emit('avatar-change', e)">
              </label>
            </div>
            <p class="avatar-hint">Nhấn vào ảnh để thay đổi</p>
          </div>

          <div class="edit-field">
            <label>Tên hiển thị</label>
            <input v-model="form.username" type="text" placeholder="Tên hiển thị của bạn" maxlength="50">
          </div>

          <div class="edit-field">
            <label>Giới thiệu bản thân</label>
            <textarea v-model="form.bio" placeholder="Chia sẻ một vài điều thú vị về bạn..." rows="4" maxlength="300"></textarea>
            <span class="char-count">{{ form.bio.length }} / 300</span>
          </div>

          <div class="danger-zone-mini">
            <p class="zone-info"> Bạn cần tạm nghỉ ngơi một thời gian?</p>
            <button class="btn-deactivate-trigger" @click="$emit('open-deactivate')">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 4H8l-7 8 7 8h13a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2z"></path><line x1="18" y1="9" x2="12" y2="15"></line><line x1="12" y1="9" x2="18" y2="15"></line></svg>
              <span>Xóa tài khoản này</span>
            </button>
          </div>
        </div>

        <div class="edit-modal-footer">
          <button class="btn-cancel" @click="$emit('close')">Hủy bỏ</button>
          <button class="btn-save" :disabled="saving" @click="$emit('save')">
            <span v-if="saving" class="spinner-sm"></span>
            <span>{{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}</span>
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
defineProps({ show: Boolean, form: Object, user: Object, saving: Boolean })
defineEmits(['close', 'save', 'avatar-change', 'open-deactivate'])
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
  max-width: 500px;
  max-height: 90vh; /* Ngăn modal quá cao */
  display: flex;
  flex-direction: column; /* Để body cuộn được */
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.edit-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 32px 20px;
  border-bottom: 1px solid #F1F5F9;
  flex-shrink: 0;

  h2 {
    font-size: 1.4rem;
    margin: 0;
    color: #0F172A;
    font-weight: 800;
  }
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
  transition: 0.2s;

  &:hover {
    background: #E2E8F0;
    color: #1E293B;
  }
}

.edit-modal-body {
  padding: 24px 32px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  overflow-y: auto; /* Cho phép cuộn */
  overscroll-behavior: contain;

  /* Custom Scrollbar */
  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }
}

.edit-avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.edit-avatar-wrap {
  position: relative;
  width: 110px;
  height: 110px;
  padding: 4px;
  background: linear-gradient(135deg, #E2E8F0, #F8FAFC);
  border-radius: 50%;
  transition: all 0.3s ease;

  &:hover {
    background: linear-gradient(135deg, #EA580C, #F59E0B);
    transform: translateY(-2px);
    box-shadow: 0 10px 20px -5px rgba(234, 88, 12, 0.3);

    .edit-avatar-overlay { opacity: 1; }
  }

  .edit-avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 50%;
    border: 3px solid #fff;
  }

  .edit-avatar-overlay {
    position: absolute;
    inset: 4px;
    background: rgba(15, 23, 42, 0.5);
    backdrop-filter: blur(2px);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    opacity: 0;
    cursor: pointer;
    transition: 0.3s;
  }
}

.file-hidden { position: absolute; opacity: 0; width: 0; height: 0; pointer-events: none; }
.avatar-hint { font-size: 12px; color: #94A3B8; margin: 0; }

.edit-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;

  label {
    font-size: 0.9rem;
    font-weight: 700;
    color: #334155;
  }

  input, textarea {
    border: 1.5px solid #E2E8F0;
    border-radius: 12px;
    padding: 14px 16px;
    font-family: inherit;
    font-size: 0.95rem;
    color: #0F172A;
    background: #F8FAFC;
    outline: none;
    transition: all 0.3s ease;
    resize: none;

    &:focus {
      border-color: #EA580C;
      background: #fff;
      box-shadow: 0 4px 15px -3px rgba(234, 88, 12, 0.1), 0 0 0 4px rgba(234, 88, 12, 0.1);
    }
  }

  textarea {
    min-height: 100px;
    line-height: 1.5;
  }
}

.char-count {
  position: absolute;
  bottom: 10px;
  right: 14px;
  font-size: 11px;
  color: #94A3B8;
  pointer-events: none;
}

.danger-zone-mini {
  margin-top: 10px;
  padding: 20px;
  background: #FEF2F2;
  border: 1px solid #FEE2E2;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;

  .zone-info {
    font-size: 0.85rem;
    font-weight: 600;
    color: #991B1B;
    margin: 0;
    flex: 1;
  }

  .btn-deactivate-trigger {
    flex-shrink: 0;
    background: #fff;
    color: #DC2626;
    border: 1.5px solid #FECACA;
    padding: 8px 16px;
    border-radius: 10px;
    font-size: 0.85rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 6px;
    cursor: pointer;
    transition: 0.3s;

    &:hover {
      background: #DC2626;
      color: #fff;
      border-color: #DC2626;
      box-shadow: 0 4px 12px rgba(220, 38, 38, 0.2);
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

.btn-save {
  flex: 2;
  background: linear-gradient(135deg, #EA580C, #F59E0B);
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
  box-shadow: 0 8px 20px -6px rgba(234, 88, 12, 0.4);
  transition: 0.3s;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 12px 25px -6px rgba(234, 88, 12, 0.5);
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
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* --- Animations --- */
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.25s ease;
  .edit-modal-card { transition: transform 0.25s, opacity 0.25s; }
}

.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
  .edit-modal-card { transform: scale(0.94) translateY(16px); opacity: 0; }
}

/* ==========================================
   📱 TỐI ƯU RESPONSIVE MOBILE
   ========================================== */
@media (max-width: 576px) {
  .edit-modal-overlay {
    padding: 16px;
  }
  
  .edit-modal-card {
    border-radius: 20px;
  }

  .edit-modal-header {
    padding: 20px 24px 16px;
    h2 { font-size: 1.25rem; }
  }

  .edit-modal-body {
    padding: 20px 24px;
    gap: 20px;
  }

  .edit-avatar-wrap {
    width: 90px;
    height: 90px;
  }

  .danger-zone-mini {
    flex-direction: column;
    align-items: flex-start;
    padding: 16px;
    gap: 12px;

    .btn-deactivate-trigger {
      width: 100%;
      justify-content: center;
    }
  }

  .edit-modal-footer {
    padding: 16px 24px 20px;
    flex-direction: column-reverse; /* Đảo nút Lưu lên trên nút Hủy để dễ bấm */
    gap: 10px;
  }

  .btn-cancel, .btn-save {
    width: 100%;
    flex: none;
  }
}
</style>