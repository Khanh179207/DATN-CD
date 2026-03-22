<template>
  <Teleport to="body">
    <transition name="gomet-fade">
      <div v-if="isVisible" class="gomet-modal-overlay" @click="closeModal">
        <div 
          class="gomet-modal-card" 
          @click.stop 
          :class="(formData.ticketType || 'FEEDBACK').toLowerCase()"
        >
          <div class="gomet-modal-header">
            <div class="header-left">
              <div class="icon-orb">
                <svg v-if="formData.ticketType === 'BUG'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 2v2m0 16v2M4.93 4.93l1.41 1.41m11.32 11.32l1.41 1.41M2 12h2m16 0h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/></svg>
                <svg v-else-if="formData.ticketType === 'REPORT'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                <svg v-else width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
              </div>
              <div class="header-text">
                <h3>GOMET Support</h3>
                <p>Trung tâm hỗ trợ người dùng</p>
              </div>
            </div>
            <button class="btn-close-minimal" @click="closeModal">×</button>
          </div>

          <div class="gomet-modal-body custom-scroll-orange">
            <div v-if="!formData.targetPostID" class="user-menu-notice">
              <div class="notice-icon">💡</div>
              <div class="notice-text">
                Để <strong>báo cáo vi phạm</strong>, vui lòng nhấn nút báo cáo trực tiếp trong bài viết đó nhé!
              </div>
            </div>

            <form @submit.prevent="submitFeedback" class="gomet-form-layout">
              <div class="gomet-field">
                <label>Vấn đề bạn gặp phải? <span class="star">*</span></label>
                <div class="select-box-wrap">
                  <select v-model="formData.ticketType" class="gomet-input gomet-select">
                    <option value="FEEDBACK">💡 Góp ý hệ thống</option>
                    <option value="BUG">🛠️ Báo cáo lỗi (Bug)</option>
                    <option value="REPORT" :disabled="!formData.targetPostID">
                      🚩 Báo cáo vi phạm {{ !formData.targetPostID ? '(Chỉ dành cho bài viết)' : '' }}
                    </option>
                  </select>
                </div>
              </div>

              <div v-if="formData.ticketType === 'REPORT' && formData.targetPostID" class="report-info-box">
                <div class="info-content">
                  <div class="info-text">
                    <span class="info-label">Đang báo cáo bài viết:</span>
                    <span class="info-value">#{{ formData.targetPostID }}</span>
                  </div>
                  <div class="info-icon">🚩</div>
                </div>
              </div>

              <div class="gomet-field">
                <label>Tiêu đề (Chọn mẫu hoặc tự nhập) <span class="star">*</span></label>
                <div class="combobox-wrap">
                  <input
                    v-model="formData.title"
                    type="text"
                    placeholder="Ví dụ: Báo lỗi nạp tiền, Tố cáo lừa đảo..."
                    required
                    class="gomet-input combobox-input"
                    @focus="showTitleSuggestions = true"
                    @blur="hideTitleSuggestions"
                  />
                  <button type="button" class="combobox-arrow" @click.stop="toggleTitleSuggestions">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M6 9l6 6 6-6"/></svg>
                  </button>
                  
                  <transition name="dropdown-fade">
                    <ul v-if="showTitleSuggestions" class="combobox-list">
                      <li v-for="(suggestion, index) in titleSuggestions" :key="index" @mousedown.prevent="selectTitle(suggestion)">
                        {{ suggestion }}
                      </li>
                    </ul>
                  </transition>
                </div>
              </div>

              <div class="gomet-field">
                <label>Nội dung chi tiết <span class="star">*</span></label>
                <textarea
                  v-model="formData.description"
                  placeholder="Mô tả chi tiết để chúng mình hỗ trợ bạn nhanh nhất nhé..."
                  rows="4"
                  required
                  class="gomet-input gomet-textarea"
                ></textarea>
              </div>

              <div class="gomet-field">
                <label>Ảnh minh họa (nếu có)</label>
                <div class="upload-container">
                  <input id="file-support" type="file" @change="handleFileChange" accept="image/*" class="hide-input" />
                  <label for="file-support" class="upload-zone" :class="{ 'has-file': filePreview }">
                    <div v-if="!filePreview" class="upload-placeholder">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ea580c" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
                      <span>Nhấn để tải ảnh từ thiết bị</span>
                    </div>
                    <div v-else class="upload-preview-wrap">
                      <img :src="filePreview" class="img-thumb" />
                      <div class="file-meta">
                        <span class="fname">{{ formData.attachment?.name }}</span>
                        <button type="button" @click.stop="removeFile" class="btn-remove-file">Xóa ảnh</button>
                      </div>
                    </div>
                  </label>
                </div>
              </div>
            </form>
          </div>

          <div class="gomet-modal-footer">
            <button type="button" class="btn-cancel-text" @click="closeModal">Đóng</button>
            <button 
              type="button" 
              class="btn-submit-orange" 
              @click="submitFeedback" 
              :disabled="isSubmitting || (formData.ticketType === 'REPORT' && !formData.targetPostID)"
            >
              <div v-if="!isSubmitting" class="btn-content">
                <span>Gửi phản hồi</span>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
              </div>
              <div v-else class="gomet-loader"></div>
            </button>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import api from '@/services/api.js' // 🔥 Dùng api instance chuẩn
import { uploadMedia } from '@/services/uploadService' // 🔥 Thêm hàm vạn năng

const props = defineProps({
  form: {
    type: Object,
    default: () => ({ title: '', description: '', attachment: null, ticketType: 'FEEDBACK', targetPostID: null })
  }
})

const emit = defineEmits(['close'])
const authStore = useAuthStore()
const isVisible = ref(false)
const isSubmitting = ref(false)
const filePreview = ref(null)

const formData = ref({
  ticketType: 'FEEDBACK',
  title: '',
  description: '',
  attachment: null,
  targetPostID: null,
  ...props.form
})

// --- LOGIC COMBOBOX TIÊU ĐỀ (GIỮ NGUYÊN) ---
const showTitleSuggestions = ref(false)
const titleSuggestions = computed(() => {
  if (formData.value.ticketType === 'BUG') {
    return ['Lỗi không tải được hình ảnh', 'Lỗi không đăng nhập được tài khoản', 'Nút thanh toán không hoạt động', 'Trang bị văng khi đang xem bài viết']
  } else if (formData.value.ticketType === 'REPORT') {
    return ['Nội dung có chứa ngôn từ phản cảm', 'Bài viết mang tính chất lừa đảo/Spam', 'Xâm phạm bản quyền hình ảnh', 'Quảng cáo trái phép']
  } else {
    return ['Đề xuất thêm tính năng mới', 'Góp ý cải thiện giao diện trang chủ', 'Màu sắc chữ hơi khó đọc']
  }
})
const hideTitleSuggestions = () => { setTimeout(() => { showTitleSuggestions.value = false }, 150) }
const toggleTitleSuggestions = () => { showTitleSuggestions.value = !showTitleSuggestions.value }
const selectTitle = (text) => { formData.value.title = text; showTitleSuggestions.value = false }
// -----------------------------

watch(() => props.form, (newVal) => {
  if (newVal) {
    formData.value = { ...formData.value, ...newVal };
    if (!newVal.targetPostID && formData.value.ticketType === 'REPORT') {
      formData.value.ticketType = 'FEEDBACK';
    }
  }
}, { deep: true, immediate: true })

const closeModal = () => {
  isVisible.value = false
  setTimeout(() => { emit('close'); filePreview.value = null; }, 400)
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    if (file.size > 10 * 1024 * 1024) { toast.error('Ảnh to quá 10MB rồi!'); return; }
    formData.value.attachment = file
    filePreview.value = URL.createObjectURL(file)
  }
}

const removeFile = () => { formData.value.attachment = null; filePreview.value = null; }

// 🔥 HÀM SUBMIT THEO LUỒNG ĐỒNG BỘ 2 BƯỚC (CHUẨN GOMET)
const submitFeedback = async () => {
  if (!formData.value.title.trim() || !formData.value.description.trim()) {
    toast.error('Vui lòng điền đầy đủ thông tin!'); 
    return;
  }
  
  isSubmitting.value = true
  try {
    let attachmentUrl = null;

    // BƯỚC 1: Nếu có ảnh đính kèm, ném lên mây trước (Vào folder 'tickets')
    if (formData.value.attachment) {
      try {
        attachmentUrl = await uploadMedia(formData.value.attachment, 'tickets');
      } catch (uploadErr) {
        toast.error('Lỗi khi tải ảnh lên, vui lòng thử lại!');
        isSubmitting.value = false;
        return;
      }
    }

    // BƯỚC 2: Chuẩn bị JSON Payload (Khớp hoàn toàn với TicketDTO ở Backend)
    const payload = {
      accountId: authStore.user.accountID || authStore.user.id,
      ticketType: formData.value.ticketType,
      title: formData.value.title,
      description: formData.value.description,
      attachment: attachmentUrl, // Link HTTPS xịn từ Cloudinary
      targetPostId: formData.value.targetPostID || null
    };

    // Gửi JSON sạch về Backend (Sử dụng API instance để tự kèm Token)
    const response = await api.post('/api/tickets/create', payload);

    if (response.data) {
      toast.success('GOMET đã nhận được phản hồi! Cảm ơn bạn nhiều.');
      closeModal();
    }
  } catch (e) {
    console.error("Submit Error:", e);
    toast.error(e.response?.data?.message || 'Lỗi hệ thống, vui lòng thử lại!');
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => { isVisible.value = true })
</script>

<style scoped lang="scss">
$gomet-orange: #ea580c;
$gomet-orange-hover: #c2410c;
$gomet-bg: #fff7ed;
$text-main: #1e293b;
$text-sub: #64748b;

.gomet-modal-overlay {
  position: fixed; inset: 0; background: rgba(15, 23, 42, 0.5);
  backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center;
  z-index: 9999; padding: 15px;
}

.gomet-modal-card {
  background: white; width: 100%; max-width: 500px;
  border-radius: 24px; overflow: hidden; /* Trả lại hidden cho toàn bộ card */
  box-shadow: 0 25px 50px -12px rgba(234, 88, 12, 0.2);
  font-family: 'Inter', sans-serif;
  display: flex; flex-direction: column;
}

.user-menu-notice {
  background: #f0f9ff; border: 1px solid #bae6fd;
  padding: 12px 16px; border-radius: 14px;
  margin-bottom: 20px; display: flex; gap: 12px; align-items: center;
  .notice-icon { font-size: 1.2rem; }
  .notice-text { font-size: 0.8rem; color: #0369a1; font-weight: 500; line-height: 1.4; }
}

.report-info-box {
  background: $gomet-bg; border-left: 4px solid $gomet-orange;
  padding: 12px 18px; border-radius: 12px; margin-bottom: 22px;
  .info-content {
    display: flex; justify-content: space-between; align-items: center;
    .info-text {
      display: flex; flex-direction: column;
      .info-label { font-size: 0.75rem; font-weight: 700; color: $text-sub; }
      .info-value { font-size: 1rem; font-weight: 900; color: $gomet-orange; }
    }
  }
}

.gomet-modal-header {
  padding: 22px 28px; display: flex; justify-content: space-between; align-items: center;
  border-bottom: 1px solid #f1f5f9; z-index: 10;
  .header-left { display: flex; align-items: center; gap: 12px; }
  .icon-orb {
    width: 44px; height: 44px; background: $gomet-bg; border-radius: 12px;
    display: flex; align-items: center; justify-content: center; color: $gomet-orange;
  }
  .header-text {
    h3 { margin: 0; font-size: 1.15rem; font-weight: 800; color: $text-main; letter-spacing: -0.5px; }
    p { margin: 0; font-size: 0.75rem; color: $text-sub; font-weight: 600; }
  }
}

.btn-close-minimal {
  background: #f8fafc; border: none; width: 32px; height: 32px;
  border-radius: 10px; color: #94a3b8; cursor: pointer; transition: 0.3s;
  &:hover { background: $gomet-bg; color: $gomet-orange; transform: rotate(90deg); }
}

/* 🔥 FIX LỖI SCROLL Ở ĐÂY */
.gomet-modal-body { 
  padding: 25px 28px; 
  max-height: 65vh; 
  overflow-y: auto; /* Bật cuộn dọc */
  overflow-x: hidden; 
  position: relative;
}

.gomet-form-layout {
  position: relative; /* Giữ ngữ cảnh chứa cho dropdown */
}

.gomet-field {
  margin-bottom: 22px;
  label { display: block; margin-bottom: 8px; font-weight: 700; color: $text-main; font-size: 0.85rem; }
  .star { color: $gomet-orange; }
}

.gomet-input {
  width: 100%; padding: 12px 16px; border-radius: 14px;
  border: 1.5px solid #e2e8f0; background: #fff;
  font-size: 0.95rem; font-weight: 500; color: $text-main; transition: 0.2s;
  &:focus { outline: none; border-color: $gomet-orange; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); }
}

.gomet-textarea {
  resize: vertical;
  min-height: 100px;
}

.gomet-select {
  appearance: none; cursor: pointer;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23ea580c' stroke-width='2.5'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E");
  background-repeat: no-repeat; background-position: right 16px center; background-size: 16px;
  &:disabled { cursor: not-allowed; opacity: 0.6; }
}

/* 🔥 CSS CHO COMBOBOX TIÊU ĐỀ */
.combobox-wrap { position: relative; width: 100%; }
.combobox-input { padding-right: 40px; }
.combobox-arrow {
  position: absolute; right: 10px; top: 50%; transform: translateY(-50%);
  background: none; border: none; color: #94a3b8; cursor: pointer;
  padding: 5px; border-radius: 8px; transition: 0.2s;
  display: flex; align-items: center; justify-content: center;
  &:hover { background: #f1f5f9; color: #ea580c; }
}

/* 🔥 FIX DROPDOWN BỊ CẮT BỞI OVERFLOW-Y: AUTO */
.combobox-list {
  position: absolute; top: calc(100% + 5px); left: 0; right: 0;
  background: white; border: 1px solid #e2e8f0; border-radius: 12px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.15);
  margin: 0; padding: 5px 0; list-style: none; z-index: 9999;
  max-height: 180px; overflow-y: auto;
}
.combobox-list li {
  padding: 10px 16px; cursor: pointer; font-size: 0.85rem;
  font-weight: 500; color: #1e293b; transition: 0.2s;
  &:hover { background: #fff7ed; color: #ea580c; font-weight: 700; padding-left: 20px; }
}
.dropdown-fade-enter-active, .dropdown-fade-leave-active { transition: all 0.2s ease; }
.dropdown-fade-enter-from, .dropdown-fade-leave-to { opacity: 0; transform: translateY(-10px); }

/* UPLOAD ZONE */
.upload-container { width: 100%; }
.upload-zone {
  display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px;
  border: 2px dashed #cbd5e1; border-radius: 16px; min-height: 120px;
  padding: 18px; text-align: center; cursor: pointer; transition: 0.3s; background: #f8fafc;
  &:hover { border-color: $gomet-orange; background: #fff; }
  &.has-file { border-color: $gomet-orange; border-style: solid; background: white; padding: 12px;}
  .upload-placeholder { display: flex; flex-direction: column; align-items: center; gap: 8px; }
  .upload-placeholder span { color: $text-sub; font-size: 0.85rem; font-weight: 700; }
}

.upload-preview-wrap {
  display: flex; align-items: center; gap: 15px; width: 100%;
  .img-thumb { width: 60px; height: 60px; border-radius: 10px; object-fit: cover; border: 1px solid #e2e8f0; }
  .file-meta {
    flex: 1; display: flex; flex-direction: column; align-items: flex-start; gap: 4px; overflow: hidden;
    .fname { font-size: 0.85rem; font-weight: 700; color: $text-main; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 100%; }
    .btn-remove-file { background: #fee2e2; border: none; color: #ef4444; font-size: 0.75rem; font-weight: 700; cursor: pointer; padding: 4px 10px; border-radius: 6px; transition: 0.2s; }
    .btn-remove-file:hover { background: #fca5a5; color: white;}
  }
}

.hide-input { display: none; }

.gomet-modal-footer {
  padding: 20px 28px; display: flex; gap: 12px; justify-content: flex-end;
  background: #f8fafc; border-top: 1px solid #f1f5f9; border-radius: 0 0 24px 24px; z-index: 10;
}

.btn-cancel-text {
  padding: 12px 20px; border: none; background: transparent;
  color: $text-sub; font-weight: 700; cursor: pointer; border-radius: 12px; transition: 0.2s;
  &:hover { background: #e2e8f0; color: $text-main; }
}

.btn-submit-orange {
  padding: 12px 28px; border: none; border-radius: 12px;
  background: $gomet-orange; color: white; font-weight: 800; cursor: pointer; transition: 0.3s;
  .btn-content { display: flex; align-items: center; gap: 10px; }
  &:hover:not(:disabled) { background: $gomet-orange-hover; transform: translateY(-2px); box-shadow: 0 8px 15px rgba(234, 88, 12, 0.3); }
  &:disabled { opacity: 0.6; cursor: not-allowed; }
}

.gomet-fade-enter-active, .gomet-fade-leave-active { transition: all 0.45s cubic-bezier(0.165, 0.84, 0.44, 1); }
.gomet-fade-enter-from, .gomet-fade-leave-to { opacity: 0; }
.gomet-fade-enter-from .gomet-modal-card { transform: scale(0.9) translateY(30px); }

.gomet-loader { width: 18px; height: 18px; border: 3px solid rgba(255,255,255,0.3); border-radius: 50%; border-top-color: #fff; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* Tùy chỉnh thanh cuộn */
.custom-scroll-orange::-webkit-scrollbar { width: 6px; }
.custom-scroll-orange::-webkit-scrollbar-track { background: transparent; }
.custom-scroll-orange::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.custom-scroll-orange::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
</style>