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
        <div class="rating-box" :class="{ 'is-new': displayRating === 0 }">
          <span class="star">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor" stroke="none"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
          </span>
          <span class="score">{{ displayRating > 0 ? Number(displayRating).toFixed(1) : 'Mới' }}</span>
          <span v-if="displayReviewCount > 0" class="count">({{ displayReviewCount }})</span>
        </div>
        
        <div class="time-badge" title="Thời gian đăng">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
          {{ formattedTimeAgo }}
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

        <div class="card-stats-wrapper">
          
          <div class="stats-view-group" title="Lượt xem">
            <span class="icon-view">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
            </span>
            <span class="view-count-text">{{ formatNumber(post.views || post.viewCount || 0) }}</span>
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
import { ref, onMounted, watch, computed } from 'vue'
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

// --- COMPACT DATA (Đồng bộ Rating Real) ---
const displayRating = computed(() => props.post.rating || props.post.avgRating || 0)
const displayReviewCount = computed(() => props.post.reviews || props.post.ratingCount || 0)

// --- HÀM TÍNH THỜI GIAN ĐĂNG BÀI (Time Ago - Đã Fix Lỗi Date) ---
const formattedTimeAgo = computed(() => {
  let dateStr = props.post.createdAt || props.post.date || props.post.savedDate;
  if (!dateStr) return 'Mới đây';

  // FIX: Xử lý trường hợp Backend trả về mảng LocalDate [YYYY, MM, DD]
  if (Array.isArray(dateStr)) {
    // Array tháng của Java là 1-12, js là 0-11 nên phải -1 ở tháng
    dateStr = new Date(dateStr[0], dateStr[1] - 1, dateStr[2], dateStr[3] || 0, dateStr[4] || 0);
  } else if (typeof dateStr === 'string' && dateStr.includes('-')) {
    // FIX: Đảm bảo parse chuẩn ISO string
    dateStr = new Date(dateStr);
  } else if (typeof dateStr === 'number') {
    // FIX: Trường hợp trả về Timestamp
    dateStr = new Date(dateStr);
  }

  const date = new Date(dateStr);
  
  // Kiểm tra xem parse có bị lỗi "Invalid Date" không
  if (isNaN(date.getTime())) return 'Mới đây';

  const now = new Date();
  const seconds = Math.floor((now - date) / 1000);

  if (seconds < 0) return 'Vừa xong';

  let interval = Math.floor(seconds / 31536000);
  if (interval >= 1) return interval + " năm trước";
  
  interval = Math.floor(seconds / 2592000);
  if (interval >= 1) return interval + " tháng trước";
  
  interval = Math.floor(seconds / 86400);
  if (interval >= 1) return interval + " ngày trước";
  
  interval = Math.floor(seconds / 3600);
  if (interval >= 1) return interval + " giờ trước";
  
  interval = Math.floor(seconds / 60);
  if (interval >= 1) return interval + " phút trước";
  
  return "Vừa xong";
})

// --- STATES ---
const isSaved = ref(false)
const isSaving = ref(false)
const isLiked = ref(false)
const isLikeLoading = ref(false)
const isLikeAnimating = ref(false)
const localLikeCount = ref(Math.max(0, Number(props.post.likes || props.post.likeCount || 0)))

const showLikesModal = ref(false)
const likesList = ref([])
const isLoadingLikesList = ref(false)

// Watch đồng bộ lượt Like
watch(() => props.post.likes, (newVal) => {
  localLikeCount.value = Math.max(0, Number(newVal || props.post.likeCount || 0))
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

  // Optimistic UI
  isLiked.value = !previousState
  localLikeCount.value = isLiked.value ? localLikeCount.value + 1 : Math.max(0, localLikeCount.value - 1)
  
  isLikeAnimating.value = true
  setTimeout(() => isLikeAnimating.value = false, 300)

  try {
    const result = await togglePostLike(uid, props.post.id)
    isLiked.value = result 
  } catch (error) {
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
  emit('save-to-plan', props.post);
};

const toggleSave = async () => {
  if (!authStore.isAuthenticated) {
    return toast.warn("Vui lòng đăng nhập để lưu bài viết Sếp nhé!");
  }
  if (isSaving.value) return;

  const uid = authStore.user?.accountID || authStore.user?.id;
  const pid = props.post.id;

  isSaving.value = true;
  try {
    if (isSaved.value) {
      await removeFavorite(uid, pid);
      isSaved.value = false;
      toast.success("Đã bỏ lưu công thức!");
      emit('unsaved', pid); 
    } else {
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

/* --- UI META TOP (RATING & TIME AGO) --- */
.meta-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.rating-box {
  display: flex;
  align-items: center;
  gap: 4px;
  background: #fff7ed;
  color: #ea580c;
  padding: 4px 10px;
  border-radius: 100px;
  font-weight: 800;
  font-size: 0.85rem;

  .star { display: flex; align-items: center; }
  .count { color: #fdba74; font-size: 0.8rem; font-weight: 600; margin-left: 2px; }

  &.is-new {
    background: #f1f5f9;
    color: #64748b;
    .star { display: none; }
  }
}

.time-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8rem;
  font-weight: 700;
  color: #94a3b8;
  background: #f8fafc;
  padding: 4px 10px;
  border-radius: 100px;
}

/* --- UI STATS (VIEW & LIKE) --- */
.card-stats-wrapper {
  display: flex;
  align-items: center;
  gap: 8px; /* Khoảng cách giữa nút View và nút Like */
}

/* Style cho Lượt xem */
.stats-view-group {
  display: flex;
  align-items: center;
  background: #f8fafc;
  border-radius: 20px;
  padding: 6px 12px; /* Padding đều cho đẹp */
  border: 1px solid transparent;
  transition: all 0.2s;
  color: #94a3b8;

  &:hover {
    background: #f1f5f9;
  }

  .icon-view {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .view-count-text {
    font-size: 0.85rem;
    font-weight: 800;
    color: #475569;
    margin-left: 6px;
  }
}

/* Style cho Lượt thích (Giữ nguyên) */
.stats-like-group {
  display: flex;
  align-items: center;
  background: #f8fafc;
  border-radius: 20px;
  padding: 2px 10px 2px 2px;
  border: 1px solid transparent;
  transition: all 0.2s;

  &:hover {
    background: #f1f5f9;
  }
}

.btn-icon-like {
  background: none; border: none; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  width: 32px; height: 32px; border-radius: 50%;
  color: #94a3b8; transition: all 0.2s;

  &:hover:not(:disabled) { background: #fff; color: #ea580c; box-shadow: 0 2px 5px rgba(0,0,0,0.05);}
  &.is-liked { color: #ea580c; }
  &.animating svg { animation: heartPop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
}

.like-count-text {
  font-size: 0.85rem; font-weight: 800; color: #475569;
  margin-left: 5px; cursor: pointer;
  &:hover { color: #0f172a; }
}

@keyframes heartPop {
  0% { transform: scale(1); }
  50% { transform: scale(1.4); }
  100% { transform: scale(1); }
}

/* --- POPUP LIKES --- */
.likes-popup-overlay {
  position: fixed; inset: 0; background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(4px); z-index: 9999;
  display: flex; align-items: center; justify-content: center;
}

.likes-popup-container {
  background: white; width: 300px; max-height: 400px;
  border-radius: 24px; box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  display: flex; flex-direction: column; overflow: hidden;
}

.popup-header {
  padding: 16px 20px; border-bottom: 1px solid #f1f5f9;
  display: flex; justify-content: space-between; align-items: center;
  span { font-weight: 800; font-size: 1rem; color: #0f172a; }
  .btn-close-mini { background: #f1f5f9; border: none; width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #64748b; cursor: pointer; font-size: 0.8rem; font-weight: bold; &:hover { background: #e2e8f0; color: #0f172a; } }
}

.popup-scroll-area {
  flex: 1; overflow-y: auto; padding: 12px;
  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
}

.liker-item-row {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 12px; border-radius: 12px; transition: 0.2s;
  &:hover { background: #f8fafc; }
  
  .liker-avt-mini { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; border: 2px solid #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
  .liker-name-mini { font-size: 0.9rem; font-weight: 700; color: #334155; }
}

.popup-state { padding: 30px; text-align: center; color: #94a3b8; font-size: 0.9rem; font-weight: 600; }

/* Transitions */
.pop-in-enter-active, .pop-in-leave-active { transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.pop-in-enter-from, .pop-in-leave-to { opacity: 0; transform: scale(0.95) translateY(10px); }
</style>