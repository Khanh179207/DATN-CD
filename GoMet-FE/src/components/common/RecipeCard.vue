<template>
  <div
    class="recipe-card-wrapper"
    tabindex="0"
    role="article"
    :aria-label="post.title"
    :class="{ 'is-skeleton': loading }"
    @click="goToDetail"
    @keydown.enter.prevent="goToDetail"
  >
    <div class="card-media">
      <img :src="post.image" :alt="post.title" loading="lazy" class="main-img"
           draggable="false" @contextmenu.prevent @dragstart.prevent>
      
      <div class="media-overlay"></div>
      <span class="category-tag" v-if="post.category">{{ post.category }}</span>

      <div class="quick-actions">
        <button class="btn-action" @click.stop="toggleSave" :title="isSaved ? $t('post.saved') : $t('post.unsaved')" :disabled="isSaving" :class="{ saving: isSaving }">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" :class="{ 'filled': isSaved }">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path>
          </svg>
        </button>

        <button class="btn-action btn-compare" :class="{ active: compareStore.isSelected(post.id) }" 
                @click.stop="compareStore.toggleItem(post)" title="Compare">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M16 3h5v5"></path><path d="M4 20L21 3"></path><path d="M21 16v5h-5"></path><path d="M15 15l5 5"></path><path d="M4 4l5 5"></path>
          </svg>
        </button>

        <button class="btn-action btn-mealplan" @click.stop="handleSaveToPlan" title="Lưu vào Kế hoạch">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line><line x1="12" y1="14" x2="12" y2="18"></line>
            <line x1="10" y1="16" x2="14" y2="16"></line>
          </svg>
        </button>
      </div>
    </div>

    <div class="card-content">
      <div class="meta-top">
        <div class="rating-box">
          <span class="star">★</span>
          <span class="score">{{ post.rating || 4.5 }}</span>
          <span class="count">({{ post.reviews || 0 }})</span>
        </div>
        <div class="time-badge">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
          {{ post.time || '30p' }}
        </div>
      </div>

      <h3 class="card-title">{{ post.title }}</h3>

      <div class="card-footer">
        <div class="author-info">
          <div class="author-avt">
            <img v-if="post.author && post.author.avatar" :src="post.author.avatar" alt="Author">
            <div v-else class="avt-placeholder">{{ getInitials(post.author?.name) }}</div>
          </div>
          <span class="author-name">{{ post.author?.name || 'Gomet Chef' }}</span>
        </div>

        <div class="stats-like-group">
          <button 
            class="btn-icon-like" 
            @click.stop="toggleLike" 
            :class="{ 'is-liked': isLiked, 'animating': isLikeAnimating }"
            :disabled="isLikeLoading"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" :fill="isLiked ? 'currentColor' : 'none'" :stroke="isLiked ? 'none' : 'currentColor'" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
            </svg>
          </button>
          
          <span 
            class="like-count-text" 
            @click.stop="openLikesModal" 
            title="Xem ai đã thích"
          >
            {{ formatNumber(localLikeCount) }}
          </span>
        </div>
      </div>
    </div>

    <teleport to="body">
      <transition name="pop-in">
        <div v-if="showLikesModal" class="likes-popup-overlay" @click.stop="showLikesModal = false">
          <div class="likes-popup-container" @click.stop>
            <div class="popup-header">
              <span>Tương tác</span>
              <button class="btn-close-mini" @click="showLikesModal = false">✕</button>
            </div>
            <div class="popup-scroll-area">
              <div v-if="isLoadingLikesList" class="popup-state">Đang tải...</div>
              <div v-else-if="likesList.length === 0" class="popup-state">Chưa có ai thích.</div>
              <div v-else class="liker-item-row" v-for="user in likesList" :key="user.likeID">
                <img :src="user.avatar || `https://ui-avatars.com/api/?name=${user.username}`" class="liker-avt-mini">
                <span class="liker-name-mini">{{ user.username }}</span>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </teleport>

  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useCompareStore } from '@/stores/compare'
import { useAuthStore } from '@/stores/auth'
import { addFavorite, removeFavorite, checkFavorite } from '@/services/socialService'
import { togglePostLike, checkPostLiked } from '@/services/likeService'
import api from '@/services/api'
import { toast } from '@/composables/useToast'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  post: { type: Object, required: true, default: () => ({}) },
  loading: { type: Boolean, default: false }
})

const emit = defineEmits(['save-to-plan', 'unsaved', 'like-changed'])

const { t } = useI18n()
const router = useRouter()
const compareStore = useCompareStore()
const authStore = useAuthStore()

// --- STATES ---
const isSaved = ref(false)
const isSaving = ref(false)
const isLiked = ref(false)
const isLikeLoading = ref(false)
const isLikeAnimating = ref(false)

// Fix lỗi -1: Luôn đảm bảo giá trị khởi tạo >= 0
const localLikeCount = ref(Math.max(0, Number(props.post.likes || 0)))

const showLikesModal = ref(false)
const likesList = ref([])
const isLoadingLikesList = ref(false)

// Watch để đồng bộ từ component cha
watch(() => props.post.likes, (newVal) => {
  localLikeCount.value = Math.max(0, Number(newVal || 0))
})

onMounted(async () => {
  if (!authStore.isAuthenticated || !props.post.id) return
  const uid = authStore.user?.accountID || authStore.user?.id
  try {
    const [savedStatus, likedStatus] = await Promise.all([
      checkFavorite(uid, props.post.id),
      checkPostLiked(uid, props.post.id)
    ])
    isSaved.value = savedStatus
    isLiked.value = likedStatus
  } catch (err) { console.error(err) }
})

// --- HELPERS ---
const getInitials = (n) => (n && typeof n === 'string') ? n.substring(0, 2).toUpperCase() : 'GM'
const formatNumber = (n) => n >= 1000 ? (n / 1000).toFixed(1) + 'k' : (n || 0)
const goToDetail = () => { if (props.post.id) router.push({ name: 'PostDetail', params: { id: props.post.id } }) }

// --- ACTIONS ---
const toggleLike = async () => {
  if (!authStore.isAuthenticated) return toast.warn("Vui lòng đăng nhập để thích bài viết!")
  if (isLikeLoading.value) return
  
  const uid = authStore.user?.accountID || authStore.user?.id
  isLikeLoading.value = true
  
  const previousState = isLiked.value
  const previousCount = localLikeCount.value

  // Optimistic UI: Cập nhật ngay lập tức
  isLiked.value = !previousState
  localLikeCount.value = isLiked.value ? localLikeCount.value + 1 : Math.max(0, localLikeCount.value - 1)
  
  isLikeAnimating.value = true
  setTimeout(() => isLikeAnimating.value = false, 300)

  try {
    const result = await togglePostLike(uid, props.post.id)
    isLiked.value = result // Đồng bộ lại từ server
  } catch (error) {
    // Rollback nếu lỗi
    isLiked.value = previousState
    localLikeCount.value = previousCount
    toast.error("Có lỗi xảy ra!")
  } finally {
    isLikeLoading.value = false
  }
}

const openLikesModal = async () => {
  if (localLikeCount.value === 0) return
  showLikesModal.value = true
  isLoadingLikesList.value = true
  try {
    const res = await api.get(`/api/likes/post/${props.post.id}`)
    likesList.value = res.data
  } catch {
    toast.error("Không thể tải danh sách!")
  } finally {
    isLoadingLikesList.value = false
  }
}
const handleSaveToPlan = () => {
  if (!authStore.isAuthenticated) {
    return toast.warn("Vui lòng đăng nhập để sử dụng tính năng này!");
  }
  // Gửi sự kiện 'save-to-plan' kèm thông tin bài viết này lên cho trang Home hoặc trang Profile xử lý
  emit('save-to-plan', props.post);
};

// --- ACTIONS LƯU BÀI VIẾT ---
const toggleSave = async () => {
  // 1. Kiểm tra đăng nhập
  if (!authStore.isAuthenticated) {
    return toast.warn("Vui lòng đăng nhập để lưu bài viết Sếp nhé!");
  }

  // 2. Chặn spam click
  if (isSaving.value) return;

  const uid = authStore.user?.accountID || authStore.user?.id;
  const pid = props.post.id;

  isSaving.value = true;
  try {
    if (isSaved.value) {
      // Nếu đã lưu thì gọi xóa
      await removeFavorite(uid, pid);
      isSaved.value = false;
      toast.success("Đã bỏ lưu công thức!");
      emit('unsaved', pid); // Báo cho cha nếu cần (ví dụ trang Profile cần xóa card này)
    } else {
      // Nếu chưa lưu thì gọi thêm
      await addFavorite(uid, pid);
      isSaved.value = true;
      toast.success("Đã lưu công thức thành công!");
    }
  } catch (error) {
    console.error("Save error:", error);
    toast.error("Thao tác thất bại, Sếp kiểm tra lại kết nối nhé!");
  } finally {
    isSaving.value = false;
  }
};

</script>

<style scoped lang="scss">
@import './RecipeCard.scss';

/* --- UI LIKE GROUP --- */
.stats-like-group {
  display: flex;
  align-items: center;
  background: #f1f3f5;
  border-radius: 20px;
  padding: 2px 10px 2px 2px;
  border: 1px solid transparent;
  transition: all 0.2s;

  &:hover {
    background: #e9ecef;
    border-color: #dee2e6;
  }
}

.btn-icon-like {
  background: none; border: none; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  width: 30px; height: 30px; border-radius: 50%;
  color: #adb5bd; transition: all 0.2s;

  &:hover:not(:disabled) { background: #fff; color: #ff4081; }
  &.is-liked { color: #ff4081; }
  &.animating svg { animation: heartPop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
}

.like-count-text {
  font-size: 0.85rem; font-weight: 800; color: #495057;
  margin-left: 5px; cursor: pointer;
  &:hover { color: #212529; text-decoration: underline; }
}

@keyframes heartPop {
  0% { transform: scale(1); }
  50% { transform: scale(1.4); }
  100% { transform: scale(1); }
}

/* --- POPUP STYLING (MONOCHROME) --- */
.likes-popup-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.3);
  backdrop-filter: blur(4px); z-index: 9999;
  display: flex; align-items: center; justify-content: center;
}

.likes-popup-container {
  background: white; width: 280px; max-height: 380px;
  border-radius: 16px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  display: flex; flex-direction: column; overflow: hidden;
  border: 1px solid #eee;
}

.popup-header {
  padding: 12px 16px; border-bottom: 1px solid #f1f3f5;
  display: flex; justify-content: space-between; align-items: center;
  span { font-weight: 800; font-size: 0.9rem; color: #212529; }
  .btn-close-mini { background: none; border: none; font-size: 1.1rem; color: #adb5bd; cursor: pointer; &:hover { color: #000; } }
}

.popup-scroll-area {
  flex: 1; overflow-y: auto; padding: 8px;
  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-thumb { background: #ddd; border-radius: 10px; }
}

.liker-item-row {
  display: flex; align-items: center; gap: 12px;
  padding: 8px 12px; border-radius: 10px; transition: 0.2s;
  &:hover { background: #f8f9fa; }
  
  .liker-avt-mini { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; border: 1px solid #eee; }
  .liker-name-mini { font-size: 0.85rem; font-weight: 600; color: #495057; }
}

.popup-state { padding: 20px; text-align: center; color: #868e96; font-size: 0.85rem; }

/* Transitions */
.pop-in-enter-active, .pop-in-leave-active { transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
.pop-in-enter-from, .pop-in-leave-to { opacity: 0; transform: scale(0.9) translateY(10px); }
</style>