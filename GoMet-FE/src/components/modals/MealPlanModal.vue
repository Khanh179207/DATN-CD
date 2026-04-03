<template>
  <transition name="fade-modal" appear>
    <div class="mealplan-modal-overlay" @click.self="closeModal">
      <div class="mealplan-modal">
        
        <div class="modal-header">
          <h3>Lên Kế Hoạch Nấu Ăn</h3>
          <button class="btn-close" @click="closeModal" title="Đóng">&times;</button>
        </div>

        <div class="modal-body">
          
          <div class="preview-recipe" v-if="postData && (postData.id || postData.postID)">
            <div class="preview-img-wrapper">
              <img 
                :src="postData.image || postData.thumbnail || 'https://ui-avatars.com/api/?name=Food&background=f1f5f9&color=94a3b8'" 
                alt="Recipe Preview" 
                class="preview-img"
              >
            </div>
            <div class="preview-info">
              <span class="preview-title">{{ postData.title || postData.name || 'Món ăn Gomet' }}</span>
              <div class="preview-meta">
                <span class="author-tag">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                  {{ postData.author?.name || postData.author || 'Đầu bếp' }}
                </span>
                <span v-if="postData.time" class="time-tag">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                  {{ postData.time }}
                </span>
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>Chọn ngày ăn</label>
            <input type="date" v-model="planForm.date" class="input-date" :min="todayDateStr">
          </div>

          <div class="form-group">
            <label>Chọn buổi</label>
            <div class="meal-type-selector">
              <label 
                v-for="type in mealTypes" 
                :key="type.value" 
                class="type-btn" 
                :class="{ active: planForm.type === type.value }"
              >
                <input type="radio" v-model="planForm.type" :value="type.value" hidden>
                {{ type.label }}
              </label>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="closeModal">Hủy</button>
          <button class="btn-confirm" @click="submitMealPlan" :disabled="isSubmitting">
            {{ isSubmitting ? 'Đang lưu...' : 'Lưu kế hoạch' }}
          </button>
        </div>

      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'

const props = defineProps({
  postData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close', 'success'])
const authStore = useAuthStore()

// Lấy ngày hôm nay định dạng YYYY-MM-DD an toàn
const today = new Date();
const todayDateStr = today.toISOString().split('T')[0];

const isSubmitting = ref(false)

const mealTypes = [
  { label: 'Sáng', value: 'BREAKFAST' },
  { label: 'Trưa', value: 'LUNCH' },
  { label: 'Tối', value: 'DINNER' }
]

const planForm = ref({
  date: todayDateStr,
  type: 'LUNCH'
})

const closeModal = () => {
  emit('close')
}

const submitMealPlan = async () => {
  const user = authStore.user
  const accountId = user?.accountID || user?.id
  const postId = props.postData?.id || props.postData?.postID

  if (!accountId) {
    toast.error('Vui lòng đăng nhập để lưu kế hoạch!')
    return
  }
  
  if (!postId) {
    toast.error('Lỗi: Không tìm thấy thông tin món ăn!')
    return
  }

  isSubmitting.value = true
  try {
    const payload = {
      postId: postId,
      planDate: planForm.value.date,
      mealType: planForm.value.type
    }
    
    await api.post(`/api/meal-plans/user/${accountId}`, payload)
    
    toast.success(`Đã thêm món "${props.postData.title || 'ăn'}" vào kế hoạch!`)
    emit('success')
    closeModal() 
  } catch (error) {
    console.error("Lỗi khi lưu Mealplan:", error)
    toast.error("Thao tác thất bại, vui lòng thử lại sau!")
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.mealplan-modal-overlay { 
  position: fixed; inset: 0; 
  background: rgba(15, 23, 42, 0.6); 
  backdrop-filter: blur(5px); 
  display: flex; align-items: center; justify-content: center; 
  z-index: 999999; 
}

.mealplan-modal { 
  background: #FFFFFF; width: 90%; max-width: 440px; 
  border-radius: 24px; overflow: hidden; 
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.3); 
  font-family: 'Mulish', sans-serif; 
}

.modal-header { 
  padding: 20px 24px; border-bottom: 1px solid #F1F5F9; 
  display: flex; justify-content: space-between; align-items: center; 
}
.modal-header h3 { margin: 0; font-size: 1.25rem; font-weight: 900; color: #0F172A; }
.btn-close { 
  background: #F1F5F9; border: none; width: 32px; height: 32px; border-radius: 50%;
  color: #64748B; cursor: pointer; transition: 0.2s; 
  display: flex; align-items: center; justify-content: center; font-size: 1.2rem; line-height: 1;
}
.btn-close:hover { background: #FEE2E2; color: #EF4444; transform: rotate(90deg); }

.modal-body { padding: 24px; display: flex; flex-direction: column; gap: 24px; }

/* NÂNG CẤP KHỐI PREVIEW RECIPE */
.preview-recipe { 
  display: flex; align-items: stretch; gap: 16px; 
  padding: 12px; background: #FFFFFF; 
  border-radius: 16px; border: 1px solid #E2E8F0; 
  box-shadow: 0 4px 15px rgba(0,0,0,0.03);
}
.preview-img-wrapper {
  width: 72px; height: 72px; flex-shrink: 0;
  border-radius: 12px; overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.preview-img { width: 100%; height: 100%; object-fit: cover; }
.preview-info { display: flex; flex-direction: column; justify-content: center; gap: 6px; flex: 1; min-width: 0; }
.preview-title { font-weight: 800; color: #0F172A; font-size: 1.05rem; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.preview-meta { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.author-tag, .time-tag { display: flex; align-items: center; gap: 4px; font-size: 0.8rem; color: #64748B; font-weight: 700; }
.author-tag svg, .time-tag svg { color: #CBD5E1; }

/* FORM CHỌN NGÀY VÀ BỮA */
.form-group { display: flex; flex-direction: column; gap: 10px; }
.form-group label { font-size: 0.85rem; font-weight: 800; color: #475569; text-transform: uppercase; letter-spacing: 0.5px; }

.input-date { 
  padding: 14px 16px; border: 1px solid #CBD5E1; 
  border-radius: 12px; font-size: 1rem; color: #0F172A; font-family: inherit; font-weight: 700; outline: none; transition: 0.3s; cursor: pointer;
  background: #F8FAFC;
}
.input-date:focus { background: #FFFFFF; border-color: #EA580C; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); }

.meal-type-selector { display: flex; gap: 12px; }
.type-btn { 
  flex: 1; padding: 12px 0; text-align: center; 
  border: 1.5px solid #E2E8F0; border-radius: 12px; 
  font-weight: 800; color: #64748B; cursor: pointer; transition: 0.2s; 
  background: #FFFFFF;
}
.type-btn:hover { background: #F8FAFC; border-color: #CBD5E1; }
.type-btn.active { 
  background: #FFF7ED; border-color: #EA580C; color: #EA580C; 
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.15); transform: translateY(-1px);
}

.modal-footer { 
  padding: 16px 24px; border-top: 1px solid #F1F5F9; 
  display: flex; justify-content: flex-end; gap: 12px; background: #F8FAFC; 
}
.btn-cancel { 
  padding: 12px 24px; border: none; background: transparent; 
  font-weight: 800; color: #64748B; cursor: pointer; transition: 0.2s; border-radius: 100px;
}
.btn-cancel:hover { background: #E2E8F0; color: #0F172A; }

.btn-confirm { 
  padding: 12px 28px; border: none; background: #0F172A; color: white; 
  font-weight: 800; border-radius: 100px; cursor: pointer; transition: 0.3s; 
}
.btn-confirm:hover:not(:disabled) { background: #EA580C; transform: translateY(-2px); box-shadow: 0 6px 15px rgba(234, 88, 12, 0.3); }
.btn-confirm:disabled { background: #94A3B8; cursor: not-allowed; transform: none; box-shadow: none; }

/* Hiệu ứng Fade mượt mà */
.fade-modal-enter-active, .fade-modal-leave-active { transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.fade-modal-enter-from, .fade-modal-leave-to { opacity: 0; }
.fade-modal-enter-from .mealplan-modal, .fade-modal-leave-to .mealplan-modal { transform: scale(0.95) translateY(20px); }
</style>