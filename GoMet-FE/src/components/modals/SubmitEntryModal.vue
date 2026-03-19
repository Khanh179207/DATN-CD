<template>
  <transition name="fade">
    <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
      <div class="submit-modal">
        
        <div class="modal-header">
          <h2>{{ step === 1 ? 'Tham gia sự kiện' : 'Chọn công thức của bạn' }}</h2>
          <button class="btn-close" @click="closeModal">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6L6 18M6 6l12 12"></path></svg>
          </button>
        </div>
        
        <div v-if="step === 1" class="modal-body step-1">
          <p class="modal-desc">Vui lòng chọn hình thức nộp bài dự thi</p>
          
          <div class="action-cards">
            <div class="action-card" @click="goToCreatePost">
              <div class="icon-box">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
              </div>
              <div class="text-box">
                <h4>Tạo công thức mới</h4>
                <p>Viết một bài mới hoàn toàn dành riêng cho sự kiện này.</p>
              </div>
              <div class="arrow">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"></path></svg>
              </div>
            </div>

            <div class="action-card" @click="goToSelectPost">
              <div class="icon-box">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="3" y1="9" x2="21" y2="9"></line><line x1="9" y1="21" x2="9" y2="9"></line></svg>
              </div>
              <div class="text-box">
                <h4>Chọn từ kho công thức</h4>
                <p>Sử dụng công thức sếp đã đăng trước đây để nộp.</p>
              </div>
              <div class="arrow">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"></path></svg>
              </div>
            </div>
          </div>
        </div>

        <div v-if="step === 2" class="modal-body step-2">
          <div v-if="isLoading" class="mini-loader">
            <svg class="spinner" viewBox="0 0 50 50"><circle class="path" cx="25" cy="25" r="20" fill="none" stroke-width="5"></circle></svg>
            <span>Đang tải kho công thức...</span>
          </div>
          
          <div v-else-if="myPosts.length > 0" class="my-posts-list">
            <label v-for="p in myPosts" :key="p.postID" class="my-post-item" :class="{ selected: selectedPostId === p.postID }">
              <input type="radio" :value="p.postID" v-model="selectedPostId" name="post_select">
              <img :src="p.media || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=150'" alt="img">
              <div class="post-info">
                <h4>{{ p.title }}</h4>
                <span>{{ formatDate(p.createdAt) }}</span>
              </div>
              <div class="check-circle">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M20 6L9 17l-5-5"></path></svg>
              </div>
            </label>
          </div>
          
          <div v-else class="empty-my-posts">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="8" y1="12" x2="16" y2="12"></line></svg>
            <p>Sếp chưa có công thức nào cả!</p>
            <button class="btn-outline-dark" @click="goToCreatePost">Tạo mới ngay</button>
          </div>
        </div>

        <div v-if="step === 2" class="modal-footer">
          <button class="btn-cancel" @click="step = 1">Quay lại</button>
          <button class="btn-submit" :disabled="!selectedPostId || isSubmitting" @click="confirmSubmit">
            {{ isSubmitting ? 'Đang gửi...' : 'Xác nhận Nộp Bài' }}
          </button>
        </div>

      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'

const props = defineProps({
  isOpen: Boolean,
  eventId: [String, Number]
})

const emit = defineEmits(['close', 'submit-success'])

const router = useRouter()
const authStore = useAuthStore()

const step = ref(1)
const isLoading = ref(false)
const isSubmitting = ref(false)
const myPosts = ref([])
const selectedPostId = ref(null)

// Reset state mỗi khi mở modal
watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    step.value = 1
    selectedPostId.value = null
  }
})

const closeModal = () => emit('close')

const goToCreatePost = () => {
  closeModal()
  router.push({ path: '/create-post', query: { eventId: props.eventId } })
}

const goToSelectPost = async () => {
  step.value = 2
  isLoading.value = true
  try {
    const uid = authStore.user?.accountID || authStore.user?.id
    const res = await api.get(`/api/posts/account/${uid}`)
    myPosts.value = res.data
  } catch (e) {
    toast.error('Lỗi tải danh sách công thức của sếp')
  } finally {
    isLoading.value = false
  }
}

// 🔥 ĐÃ FIX HÀM SUBMIT TẠI ĐÂY
const confirmSubmit = async () => {
  if (!selectedPostId.value) return;
  isSubmitting.value = true;
  try {
    // Ép kiểu sang số và gửi đúng tên key mà Java mong đợi
    const payload = {
      eventID: Number(props.eventId),
      postID: Number(selectedPostId.value)
    };

    const res = await api.post(`/api/events/submit`, payload);
    
    toast.success(res.data.message || 'Nộp bài thành công!');
    emit('submit-success');
    closeModal();
  } catch (e) {
    toast.error(e.response?.data?.message || 'Lỗi khi nộp bài!');
  } finally {
    isSubmitting.value = false;
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}
</script>

<style scoped>
/* Tổng quan: Monochrome, sạch sẽ, tối giản */
.modal-overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.6); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 20px; }
.submit-modal { background: #FFFFFF; width: 100%; max-width: 480px; border-radius: 16px; box-shadow: 0 20px 40px rgba(0,0,0,0.15); overflow: hidden; display: flex; flex-direction: column; max-height: 85vh; font-family: 'Inter', sans-serif; }

.modal-header { padding: 20px 24px; border-bottom: 1px solid #E5E7EB; display: flex; justify-content: space-between; align-items: center; }
.modal-header h2 { margin: 0; font-size: 1.25rem; font-weight: 700; color: #111827; }
.btn-close { background: none; border: none; color: #6B7280; cursor: pointer; transition: 0.2s; padding: 4px; display: flex; align-items: center; justify-content: center; border-radius: 50%; }
.btn-close:hover { color: #111827; background: #F3F4F6; }

.modal-body { padding: 24px; overflow-y: auto; background: #FFFFFF; }
.modal-desc { margin: 0 0 20px 0; color: #6B7280; font-size: 0.95rem; }

/* Action Cards - Monochrome Style */
.action-cards { display: flex; flex-direction: column; gap: 12px; }
.action-card { display: flex; align-items: center; gap: 16px; background: #F9FAFB; padding: 16px; border-radius: 12px; border: 1px solid #E5E7EB; cursor: pointer; transition: all 0.2s ease; }
.action-card:hover { border-color: #111827; background: #FFFFFF; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); }
.icon-box { width: 44px; height: 44px; border-radius: 10px; background: #111827; color: #FFFFFF; display: flex; align-items: center; justify-content: center; }
.text-box { flex: 1; }
.text-box h4 { margin: 0 0 4px 0; color: #111827; font-size: 1rem; font-weight: 600; }
.text-box p { margin: 0; color: #6B7280; font-size: 0.85rem; line-height: 1.4; }
.arrow { color: #9CA3AF; transition: 0.2s; display: flex; align-items: center; }
.action-card:hover .arrow { color: #111827; transform: translateX(4px); }

/* Step 2: Danh sách bài viết */
.my-posts-list { display: flex; flex-direction: column; gap: 12px; }
.my-post-item { display: flex; align-items: center; gap: 16px; padding: 12px; border-radius: 12px; border: 1px solid #E5E7EB; cursor: pointer; transition: 0.2s; position: relative; }
.my-post-item:hover { border-color: #9CA3AF; }
.my-post-item input { display: none; }
.my-post-item.selected { border-color: #111827; background: #F9FAFB; }
.my-post-item img { width: 56px; height: 56px; border-radius: 8px; object-fit: cover; border: 1px solid #F3F4F6; }
.post-info h4 { margin: 0 0 4px 0; font-size: 0.95rem; font-weight: 600; color: #111827; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.3; }
.post-info span { font-size: 0.8rem; color: #6B7280; }
.check-circle { width: 20px; height: 20px; border-radius: 50%; background: #111827; color: white; display: flex; align-items: center; justify-content: center; position: absolute; right: 16px; opacity: 0; transform: scale(0.5); transition: 0.2s; }
.my-post-item.selected .check-circle { opacity: 1; transform: scale(1); }

/* Loader & Empty State */
.mini-loader { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 40px 0; gap: 12px; color: #6B7280; font-size: 0.9rem; }
.spinner { animation: rotate 2s linear infinite; width: 30px; height: 30px; }
.spinner .path { stroke: #111827; stroke-linecap: round; animation: dash 1.5s ease-in-out infinite; }
@keyframes rotate { 100% { transform: rotate(360deg); } }
@keyframes dash { 0% { stroke-dasharray: 1, 150; stroke-dashoffset: 0; } 50% { stroke-dasharray: 90, 150; stroke-dashoffset: -35; } 100% { stroke-dasharray: 90, 150; stroke-dashoffset: -124; } }

.empty-my-posts { text-align: center; padding: 30px 0; display: flex; flex-direction: column; align-items: center; gap: 12px; }
.empty-my-posts p { margin: 0; color: #6B7280; font-size: 0.95rem; }
.btn-outline-dark { border: 1px solid #111827; color: #111827; background: transparent; padding: 8px 20px; border-radius: 8px; font-weight: 600; cursor: pointer; transition: 0.2s; }
.btn-outline-dark:hover { background: #111827; color: #FFFFFF; }

/* Footer */
.modal-footer { padding: 16px 24px; border-top: 1px solid #E5E7EB; background: #F9FAFB; display: flex; justify-content: space-between; align-items: center; }
.btn-cancel { padding: 10px 16px; border: none; background: transparent; font-weight: 600; color: #6B7280; cursor: pointer; transition: 0.2s; border-radius: 8px; }
.btn-cancel:hover { background: #E5E7EB; color: #111827; }
.btn-submit { padding: 10px 24px; border-radius: 8px; border: none; background: #111827; color: white; font-weight: 600; cursor: pointer; transition: 0.2s; }
.btn-submit:not(:disabled):hover { background: #374151; }
.btn-submit:disabled { opacity: 0.5; cursor: not-allowed; }

/* Transitions */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
.fade-enter-active .submit-modal { animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
@keyframes slideUp { from { transform: translateY(20px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
</style>