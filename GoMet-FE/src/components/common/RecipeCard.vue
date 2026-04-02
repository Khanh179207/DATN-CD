<template>
  <div
    v-if="post"
    class="recipe-card-wrapper"
    tabindex="0"
    role="article"
    :aria-label="post?.title"
    :class="{ 'is-skeleton': loading, 'is-premium-card': post?.isPremium, 'is-hidden-p': isManagement && post?.isActive === 0 }"
    @click="goToDetail"
    @keydown.enter.prevent="goToDetail"
  >
    <div class="card-media">
      <img :src="post?.image" :alt="post?.title" loading="lazy" class="main-img"
           draggable="false" @contextmenu.prevent @dragstart.prevent>
      
      <div v-if="isManagement && post?.isActive === 0" class="hidden-overlay-badge">
        <div class="glass-inner">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle><line x1="1" y1="1" x2="23" y2="23"></line></svg>
          <span>BÀI VIẾT ĐÃ ẨN</span>
        </div>
      </div>

      <div class="media-overlay"></div>
      <span class="category-tag" v-if="post?.category">{{ post?.category }}</span>

      <div class="quick-actions">
        <button class="btn-action" @click.stop="toggleSave" :title="isSaved ? $t('post.saved') : $t('post.unsaved')" :disabled="isSaving" :class="{ saving: isSaving }">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" :class="{ 'filled': isSaved }">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path>
          </svg>
        </button>

        <button class="btn-action btn-compare" :class="{ active: compareStore.isSelected(post?.id) }" 
                @click.stop="handleCompareClick" title="So sánh món ăn (Chỉ dành cho Premium)">
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

        <!-- Management Actions -->
        <template v-if="isManagement">
          <button class="btn-action btn-manage-edit" @click.stop="$emit('manage-edit', post)" title="Chỉnh sửa bài viết">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
          </button>
          <button class="btn-action btn-manage-hide" :class="{ 'is-hidden-active': post?.isActive === 0 }" @click.stop="$emit('manage-toggle-visibility', post)" :title="post?.isActive === 0 ? 'Hiện bài viết' : 'Ẩn bài viết'">
            <svg v-if="post?.isActive === 1" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
              <line x1="1" y1="1" x2="23" y2="23"></line>
            </svg>
            <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
              <circle cx="12" cy="12" r="3"></circle>
            </svg>
          </button>
        </template>
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

      <h3 class="card-title">{{ post?.title }}</h3>

      <div class="card-footer">
        <div class="author-info">
          <div class="author-avt">
            <img v-if="post?.author && post?.author?.avatar" :src="post?.author?.avatar" alt="Author">
            <div v-else class="avt-placeholder">{{ getInitials(post?.author?.name) }}</div>
          </div>
          
          <div class="author-name-wrapper">
            <span class="author-name">
              {{ post?.author?.name || 'Gomet Chef' }}
            </span>
            <div class="beautiful-tooltip">
              {{ post?.author?.name || 'Gomet Chef' }}
            </div>
          </div>
        </div>

        <div class="card-stats-wrapper">
          <div class="stats-group" title="Lượt xem">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" class="stat-icon">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
              <circle cx="12" cy="12" r="3"></circle>
            </svg>
            <span class="stat-text">{{ formatNumber(post?.views || post?.viewCount || 0) }}</span>
          </div>

          <div class="stats-group like-action">
            <button 
              class="btn-icon-like" 
              @click.stop="toggleLike" 
              :class="{ 'is-liked': isLiked, 'animating': isLikeAnimating }"
              :disabled="isLikeLoading"
            >
              <svg width="15" height="15" viewBox="0 0 24 24" :fill="isLiked ? 'currentColor' : 'none'" :stroke="isLiked ? 'none' : 'currentColor'" stroke-width="2.5">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
              </svg>
            </button>
            <span 
              class="stat-text clickable" 
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
import { addFavorite, removeFavorite, checkFavorite, getFavorites } from '@/services/socialService'
import { togglePostLike, checkPostLiked } from '@/services/likeService'
import api from '@/services/api'
import { toast } from '@/composables/useToast'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  post: { type: Object, required: true, default: () => ({}) },
  loading: { type: Boolean, default: false },
  isManagement: { type: Boolean, default: false }
})

const emit = defineEmits(['save-to-plan', 'unsaved', 'like-changed', 'manage-edit', 'manage-toggle-visibility'])

const { t } = useI18n()
const router = useRouter()
const compareStore = useCompareStore()
const authStore = useAuthStore()

// --- COMPACT DATA ---
const displayRating = computed(() => props.post.rating || props.post.avgRating || 0)
const displayReviewCount = computed(() => props.post.reviews || props.post.ratingCount || 0)

// --- HÀM TÍNH THỜI GIAN ĐĂNG BÀI ---
const formattedTimeAgo = computed(() => {
  let dateStr = props.post.createdAt || props.post.date || props.post.savedDate;
  if (!dateStr) return 'Mới đây';

  if (Array.isArray(dateStr)) {
    dateStr = new Date(dateStr[0], dateStr[1] - 1, dateStr[2], dateStr[3] || 0, dateStr[4] || 0);
  } else if (typeof dateStr === 'string' && dateStr.includes('-')) {
    dateStr = new Date(dateStr);
  } else if (typeof dateStr === 'number') {
    dateStr = new Date(dateStr);
  }

  const date = new Date(dateStr);
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
  
  window.addEventListener('sync-favorite', (e) => {
    if (e.detail.id === props.post?.id) {
      isSaved.value = e.detail.status;
    }
  });
})

// --- HELPERS ---
const getInitials = (n) => (n && typeof n === 'string') ? n.substring(0, 2).toUpperCase() : 'GM'
const formatNumber = (n) => n >= 1000 ? (n / 1000).toFixed(1) + 'k' : (n || 0)
const goToDetail = () => { if (props.post.id) router.push({ name: 'PostDetail', params: { id: props.post.id } }) }

// --- ACTIONS ---
const toggleLike = async () => {
  if (!authStore.isAuthenticated) {
    window.dispatchEvent(new CustomEvent('ui:open-login'))
    return
  }
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
    window.dispatchEvent(new CustomEvent('ui:open-login'))
    return
  }
  emit('save-to-plan', props.post);
};

const toggleSave = async () => {
  if (!authStore.isAuthenticated) {
    window.dispatchEvent(new CustomEvent('ui:open-login'))
    return
  }
  if (isSaving.value) return;

  const uid = authStore.user?.accountID || authStore.user?.id;
  const pid = props.post.id;

  // 🔥 1. ĐỊNH DANH ĐẠI GIA: Kiểm tra xem user có phải Premium hoặc Admin không
  const isPremiumUser = authStore.user?.isPremium || authStore.user?.role === 'PREMIUM' || authStore.user?.IsPremium;
  const isAdmin = authStore.user?.isAdmin || authStore.user?.role === 'ADMIN' || authStore.user?.role === 'admin';
  const hasUnlimitedSave = isPremiumUser || isAdmin;

  isSaving.value = true;
  try {
    if (isSaved.value) {
      // 🟢 XÓA KHỎI BỘ SƯU TẬP (Ai cũng xóa được)
      await removeFavorite(uid, pid);
      isSaved.value = false;
      toast.success("Đã bỏ lưu công thức!");
      emit('unsaved', pid); 
      window.dispatchEvent(new CustomEvent('sync-favorite', { detail: { id: pid, status: false } }));
    } else {
      // 🔴 THÊM VÀO BỘ SƯU TẬP
      
      // 2. NẾU LÀ USER THƯỜNG -> Mới cần gọi API check số lượng
      if (!hasUnlimitedSave) {
        const currentFavorites = await getFavorites(uid);
        
        if (currentFavorites && currentFavorites.length >= 5) {
          toast.warn("Bộ sưu tập đã đầy! Nâng cấp Premium để lưu không giới hạn sếp nhé.");
          // Bật luôn popup chèo kéo mua Premium
          window.dispatchEvent(new CustomEvent('ui:open-premium'));
          isSaving.value = false;
          return; 
        }
      }

      // 3. Nếu là VIP/Admin HOẶC user thường chưa đủ 5 bài -> Cho lưu thoải mái
      await addFavorite(uid, pid);
      isSaved.value = true;
      toast.success("Đã lưu công thức thành công!");
      window.dispatchEvent(new CustomEvent('sync-favorite', { detail: { id: pid, status: true } }));
    }
  } catch (error) {
    console.error("Save error:", error);
    toast.error("Thao tác thất bại, Sếp kiểm tra lại kết nối nhé!");
  } finally {
    isSaving.value = false;
  }
};

const handleCompareClick = () => {
  if (!authStore.isAuthenticated) {
    return toast.warn("Vui lòng đăng nhập để sử dụng tính năng So sánh Sếp nhé!");
  }

  const isPremiumUser = authStore.user?.isPremium || authStore.user?.role === 'PREMIUM' || authStore.user?.IsPremium;
  if (!isPremiumUser) {
    toast.warn('Tính năng So sánh Công thức đặc quyền chỉ dành cho tài khoản Premium!');
    window.dispatchEvent(new CustomEvent('ui:open-premium'));
    return;
  }
  compareStore.toggleItem(props.post);
};
</script>

<style scoped lang="scss">
/* Import toàn bộ giao diện từ file SCSS đã gộp */
@import './RecipeCard.scss';
</style>