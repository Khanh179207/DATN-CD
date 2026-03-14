<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="isVisible" class="modal-backdrop" @click="closeModal">
        <div class="modal-content feedback-modal" @click.stop>
          <div class="modal-header">
            <h3>Góp ý hệ thống</h3>
            <button class="close-btn" @click="closeModal">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
          </div>

          <div class="modal-body">
            <form @submit.prevent="submitFeedback" class="feedback-form">
              <div class="form-group">
                <label for="title">Tiêu đề <span class="required">*</span></label>
                <input
                  id="title"
                  v-model="formData.title"
                  type="text"
                  placeholder="Nhập tiêu đề góp ý..."
                  required
                />
              </div>

              <div class="form-group">
                <label for="description">Nội dung góp ý <span class="required">*</span></label>
                <textarea
                  id="description"
                  v-model="formData.description"
                  placeholder="Mô tả chi tiết vấn đề hoặc góp ý của bạn..."
                  rows="6"
                  required
                ></textarea>
              </div>

              <div class="form-group">
                <label for="attachment">Đính kèm file (tùy chọn)</label>
                <input
                  id="attachment"
                  type="file"
                  @change="handleFileChange"
                  accept="image/*,.pdf,.doc,.docx"
                />
                <small class="file-hint">Chấp nhận: hình ảnh, PDF, Word (tối đa 10MB)</small>
              </div>
            </form>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn-cancel" @click="closeModal">Hủy</button>
            <button type="button" class="btn-submit" @click="submitFeedback" :disabled="isSubmitting">
              {{ isSubmitting ? 'Đang gửi...' : 'Gửi góp ý' }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import apiClient from '@/services/api' // Đảm bảo sếp dùng đúng cấu hình axios/fetch

const props = defineProps({
  form: {
    type: Object,
    default: () => ({ title: '', description: '', attachment: null })
  }
})

const emit = defineEmits(['close'])

const authStore = useAuthStore()
const isVisible = ref(false)
const isSubmitting = ref(false)
const formData = ref({ ...props.form })

// Watch for prop changes
watch(() => props.form, (newForm) => {
  formData.value = { ...newForm }
}, { deep: true })

const closeModal = () => {
  isVisible.value = false
  emit('close')
  // Reset form
  formData.value = { title: '', description: '', attachment: null }
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    // Check file size (10MB limit)
    if (file.size > 10 * 1024 * 1024) {
      toast.error('File quá lớn! Vui lòng chọn file dưới 10MB.')
      event.target.value = ''
      return
    }
    formData.value.attachment = file
  }
}

const submitFeedback = async () => {
  if (!formData.value.title.trim() || !formData.value.description.trim()) {
    toast.error('Vui lòng điền đầy đủ thông tin!')
    return
  }

  if (!authStore.isAuthenticated || !authStore.user?.accountID) {
    toast.error('Vui lòng đăng nhập để gửi góp ý!')
    return
  }

  isSubmitting.value = true

  try {
    const formDataToSend = new FormData()
    
    // Gửi các tham số theo đúng yêu cầu của BE
    formDataToSend.append('accountId', authStore.user.accountID)
    formDataToSend.append('ticketType', 'FEEDBACK') // Ép cứng loại là FEEDBACK
    formDataToSend.append('title', formData.value.title)
    formDataToSend.append('description', formData.value.description)

    if (formData.value.attachment) {
      formDataToSend.append('attachment', formData.value.attachment)
    }

    // Tùy theo project sếp cấu hình API thế nào (fetch hoặc axios)
    const response = await fetch('/api/tickets', {
      method: 'POST',
      headers: {
        // ĐỪNG set Content-Type là 'multipart/form-data' khi dùng fetch + FormData
        // Trình duyệt sẽ tự động thêm boundary cho multipart
        'Authorization': `Bearer ${authStore.token || localStorage.getItem('token')}` // Nhớ truyền token nếu sếp yêu cầu đăng nhập
      },
      body: formDataToSend
    })

    if (response.ok) {
      toast.success('Cảm ơn bạn đã gửi góp ý! Chúng tôi sẽ xem xét sớm nhất.')
      closeModal()
    } else {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || 'Failed to submit feedback')
    }
  } catch (error) {
    console.error('Error submitting feedback:', error)
    toast.error('Có lỗi xảy ra khi gửi góp ý. Vui lòng thử lại sau!')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  isVisible.value = true
})
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  color: #6b7280;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.modal-body {
  padding: 24px;
}

.feedback-form {
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
  font-weight: 500;
  color: #374151;
  font-size: 0.875rem;
}

.required {
  color: #ef4444;
}

.form-group input,
.form-group textarea {
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #ea580c;
  box-shadow: 0 0 0 3px rgba(234, 88, 12, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 120px;
}

.file-hint {
  color: #6b7280;
  font-size: 0.75rem;
}

.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-cancel,
.btn-submit {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-cancel {
  background: #f3f4f6;
  color: #374151;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

.btn-submit {
  background: #ea580c;
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: #c2410c;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Modal animations */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-content,
.modal-fade-leave-active .modal-content {
  transition: transform 0.3s ease;
}

.modal-fade-enter-from .modal-content,
.modal-fade-leave-to .modal-content {
  transform: scale(0.9);
}
</style>