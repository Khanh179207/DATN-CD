<template>
  <section class="hero-section-clean fade-in">
    <div class="hero-container-inner">
      
      <div class="hero-info-col">
        
        <div class="top-nav-bar">
          <div class="nav-left">
            <button @click="router.push('/home')" class="btn-back-clean">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
              <span>Quay lại</span>
            </button>
            <span class="nav-sep">/</span>
            <span class="category-tag">{{ displayCategory }}</span>
          </div>

          <button class="btn-icon-minimal" @click="openReportModal" title="Báo cáo vi phạm">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"></path><line x1="4" y1="22" x2="4" y2="15"></line></svg>
          </button>
        </div>

        <h1 class="recipe-title-display">{{ post.title }}</h1>
        <p class="recipe-desc-text">{{ post.description || 'Chưa có mô tả chi tiết cho công thức này.' }}</p>

        <div class="recipe-stats-group">
          <div class="stat-card-clean">
            <div class="icon-box time-box">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
            </div>
            <div class="stat-info">
              <span class="s-label">THỜI GIAN</span>
              <span class="s-val">{{ post.time || '30 min' }}</span>
            </div>
          </div>

          <div class="stat-card-clean">
            <div class="icon-box fire-box">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M8.5 14.5A2.5 2.5 0 0011 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 2.4 5.6a4.5 4.5 0 01-1.5 2.3A4.5 4.5 0 0110 21c-2.5 0-4-2-4-4s1.5-3 2.5-2.5z"></path></svg>
            </div>
            <div class="stat-info">
              <span class="s-label">ĐỘ KHÓ</span>
              <span class="s-val">{{ post.difficulty || 'Medium' }}</span>
            </div>
          </div>

          <div class="stat-card-clean highlight">
            <div class="icon-box star-box">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" stroke="none"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
            </div>
            <div class="stat-info">
              <span class="s-label">ĐÁNH GIÁ</span>
              <span class="s-val">{{ displayAvgRating }} ({{ displayRatingCount }})</span>
            </div>
          </div>
        </div>

        <div class="hero-footer-actions">
          
          <div class="author-info-block" @click="router.push(`/profile/${post.authorID}`)">
            <div class="avatar-container">
              <img :src="post.authorAvatar" alt="Author">
              <div class="tick-badge"><svg width="8" height="8" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="4"><polyline points="20 6 9 17 4 12"></polyline></svg></div>
            </div>
            <div class="auth-text">
              <span class="auth-sub">Công thức bởi</span>
              <span class="auth-name">{{ post.author }}</span>
            </div>
          </div>
          
          <div class="social-actions-row">
            
            <div class="action-btn-group">
              <button class="btn-like-clean" :class="{ 'is-liked': isLiked, 'animating': isLikeAnimating }" @click="handleLike" :disabled="isLikeLoading">
                <svg width="18" height="18" viewBox="0 0 24 24" :fill="isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
                <span>{{ isLiked ? 'Đã thích' : 'Thích' }}</span>
              </button>

              <button class="btn-save-clean" :class="{ 'active': isFavorite }" @click="toggleFavorite">
                <svg width="18" height="18" viewBox="0 0 24 24" :fill="isFavorite ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2.5"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path></svg>
              </button>

              <button class="btn-share-clean" @click="copyLink" title="Chia sẻ">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="5" r="3"></circle><circle cx="6" cy="12" r="3"></circle><circle cx="18" cy="19" r="3"></circle><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"></line><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"></line></svg>
              </button>
            </div>

            <div class="liked-by-stack" v-if="likedUsersList.length > 0" @click="showLikesModal = true">
              <div class="avatar-stack">
                <img v-for="(u, idx) in likedUsersList.slice(0, 3)" :key="idx" :src="u.avatar" :alt="u.name">
                <div class="more-avt" v-if="localLikeCount > 3">+{{ localLikeCount - 3 }}</div>
              </div>
              <span class="liked-text">
                Được thích bởi <b>{{ likedUsersList[0]?.name }}</b>
                <template v-if="localLikeCount > 1"> và <b>{{ formatNumber(localLikeCount - 1) }} người khác</b></template>
              </span>
            </div>

          </div>

        </div>
      </div>

      <div class="hero-image-col">
        <div class="clean-image-frame">
          <img :src="post.image" :alt="post.title" class="img-cover">
          
          <div v-if="isNewToday" class="badge-new-top-left">NEW</div>

          <div class="bottom-glass-stats">
            <div class="stat-item">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
              <span>{{ formattedDate }}</span>
            </div>
            <div class="stat-item">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
              <span>{{ formatNumber(post.views) }} xem</span>
            </div>
          </div>
        </div>
      </div>

    </div>

    <Teleport to="body">
      <Transition name="fade-scale">
        <div v-if="showLikesModal" class="likes-modal-backdrop" @click.self="showLikesModal = false">
          <div class="likes-modal-content">
            <div class="modal-header">
              <h3>Lượt thích ({{ localLikeCount }})</h3>
              <button class="btn-close-modal" @click="showLikesModal = false">&times;</button>
            </div>
            <div class="modal-body custom-scroll">
              <div v-if="isFetchingLikes" class="loading-state">Đang tải danh sách...</div>
              <ul v-else class="likes-list">
                <li v-for="user in likedUsersList" :key="user.id" class="like-user-item" @click="goToProfile(user.id)">
                  <img :src="user.avatar" alt="avt" class="u-avt">
                  <span class="u-name">{{ user.name }}</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <FeedbackModal v-if="showReportModal" :form="reportForm" @close="showReportModal = false" />
  </section>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import api from '@/services/api'
import { addFavorite, removeFavorite, checkFavorite } from '@/services/socialService'
import { togglePostLike, checkPostLiked } from '@/services/likeService' 
import FeedbackModal from '@/components/modals/FeedbackModal.vue'

const props = defineProps({ 
  post: { type: Object, required: true },
  displayAvgRating: {
    type: [String, Number],
    default: 'Mới'
  },
  displayRatingCount: {
    type: Number,
    default: 0
  }
})
const router = useRouter()
const authStore = useAuthStore()

// --- STATES CƠ BẢN ---
const isFavorite = ref(false)
const isLiked = ref(false)
const isLikeLoading = ref(false)
const isLikeAnimating = ref(false)
const localLikeCount = ref(0)
const showReportModal = ref(false)
const reportForm = ref({ ticketType: 'REPORT', title: '', description: '', attachment: null, targetPostID: null })

// --- STATES MODAL LIKE ---
const showLikesModal = ref(false)
const likedUsersList = ref([])
const isFetchingLikes = ref(false)

// --- COMPUTED LOGIC ---
const displayCategory = computed(() => (props.post.categoryName || props.post.category || 'MÓN NGON').toUpperCase())
const formattedDate = computed(() => {
  const ds = props.post.publishDate || props.post.createdAt;
  return ds ? new Date(ds).toLocaleDateString('vi-VN') : new Date().toLocaleDateString('vi-VN');
})
const isNewToday = computed(() => {
  const ds = props.post.publishDate || props.post.createdAt;
  if (!ds) return true;
  return new Date(ds).toDateString() === new Date().toDateString();
})
const formatNumber = (n) => n >= 1000 ? (n / 1000).toFixed(1) + 'k' : (n || 0)

// --- ACTIONS ---
const copyLink = () => { navigator.clipboard.writeText(window.location.href); toast.success("Đã sao chép liên kết!"); }
const goToProfile = (id) => { showLikesModal.value = false; router.push(`/profile/${id}`); }
const openReportModal = () => {
  if (!authStore.isAuthenticated) {
    window.dispatchEvent(new CustomEvent('ui:open-login'))
    return
  }
  showReportModal.value = true
}

// --- API LẤY DANH SÁCH LIKE THẬT ---
const fetchLikesList = async () => {
  const pid = props.post.postID || props.post.id;
  if (!pid) return;
  isFetchingLikes.value = true;
  try {
    // SẾP THAY ĐỔI ENDPOINT NÀY THEO BACKEND CỦA SẾP NHÉ
    // VD: API trả về mảng [{ accountID, fullName, avatar }, ...]
    const response = await api.get(`/api/likes/post/${pid}`);
    
    if (response.data && Array.isArray(response.data)) {
      likedUsersList.value = response.data.map(u => ({
        id: u.accountID || u.userId || u.id,
        name: u.fullName || u.username || u.name || 'Người dùng',
        avatar: u.avatar || `https://ui-avatars.com/api/?name=${u.fullName || 'U'}&background=ea580c&color=fff`
      }));
      // Đồng bộ số lượng thực tế từ DB
      localLikeCount.value = likedUsersList.value.length;
    }
  } catch (error) {
    console.log("Không thể tải danh sách Like:", error);
    // Fallback: Nếu API chưa viết xong, giữ nguyên số lượng cũ
    localLikeCount.value = Math.max(0, Number(props.post.LikeCount || props.post.likes || 0));
  } finally {
    isFetchingLikes.value = false;
  }
}

// --- INIT DATA KHỞI TẠO ---
const initData = async () => {
  reportForm.value.targetPostID = props.post.postID || props.post.id;
  
  // Tải danh sách những người đã thả tim
  await fetchLikesList();

  if (authStore.isAuthenticated) {
    const uid = authStore.user.accountID || authStore.user.id;
    const pid = props.post.postID || props.post.id;
    try {
      const [favStatus, likeStatus] = await Promise.all([checkFavorite(uid, pid), checkPostLiked(uid, pid)])
      isFavorite.value = favStatus; 
      isLiked.value = likeStatus;
    } catch (e) { console.error(e) }
  }
}

// --- XỬ LÝ NÚT LIKE (TỐI ƯU GIAO DIỆN & LOGIC) ---
const handleLike = async () => {
  if (!authStore.isAuthenticated) {
    window.dispatchEvent(new CustomEvent('ui:open-login'))
    return
  }
  if (isLikeLoading.value) return;
  
  const pid = props.post.postID || props.post.id; 
  const currentUser = authStore.user;
  const myId = currentUser.accountID || currentUser.id;

  isLikeLoading.value = true;
  const prevLiked = isLiked.value; 
  isLiked.value = !prevLiked;
  
  // Cập nhật giao diện (Optimistic Update)
  if (isLiked.value) {
    localLikeCount.value += 1;
    // Tự nhét mình vào đầu danh sách
    likedUsersList.value.unshift({
      id: myId,
      name: currentUser.fullName || currentUser.name || 'Bạn',
      avatar: currentUser.avatar || `https://ui-avatars.com/api/?name=U`
    });
  } else {
    localLikeCount.value = Math.max(0, localLikeCount.value - 1);
    // Rút mình ra khỏi danh sách
    likedUsersList.value = likedUsersList.value.filter(u => u.id !== myId);
  }

  isLikeAnimating.value = true; 
  setTimeout(() => isLikeAnimating.value = false, 300);
  
  try { 
    // Gọi API lưu vào Database
    await togglePostLike(myId, pid);
  } catch (e) { 
    // Rollback nếu thất bại
    isLiked.value = prevLiked; 
    if (prevLiked) {
      localLikeCount.value += 1;
      likedUsersList.value.unshift({ id: myId, name: currentUser.fullName || 'Bạn', avatar: currentUser.avatar });
    } else {
      localLikeCount.value = Math.max(0, localLikeCount.value - 1);
      likedUsersList.value = likedUsersList.value.filter(u => u.id !== myId);
    }
    toast.error("Thao tác thất bại!"); 
  } finally { 
    isLikeLoading.value = false 
  }
}

const toggleFavorite = async () => {
  if (!authStore.isAuthenticated) {
    window.dispatchEvent(new CustomEvent('ui:open-login'))
    return
  }
  const pid = props.post.postID || props.post.id
  try {
    if (isFavorite.value) { await removeFavorite(authStore.user.accountID, pid); isFavorite.value = false; } 
    else { await addFavorite(authStore.user.accountID, pid); isFavorite.value = true; toast.success("Đã lưu vào bộ sưu tập!"); }
  } catch (e) { toast.error("Lỗi hệ thống!"); }
}

watch(() => props.post.postID || props.post.id, initData, { immediate: true })
</script>

<style scoped lang="scss">
.hero-section-clean {
  width: 100%;
  background: var(--color-neutral-0, #ffffff);
  padding: 40px 0 80px;
  font-family: var(--font-ui, 'Mulish', sans-serif);
}

.hero-container-inner {
  max-width: 1280px; margin: 0 auto; padding: 0 24px;
  display: grid; grid-template-columns: 1.15fr 0.85fr; gap: 80px; align-items: center;
}

/* --- NAVIGATION --- */
.top-nav-bar {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px;
  .nav-left {
    display: flex; align-items: center; gap: 12px;
    .btn-back-clean { display: flex; align-items: center; gap: 6px; background: none; border: none; color: var(--color-neutral-500, #64748b); font-weight: 700; font-size: 0.95rem; cursor: pointer; transition: 0.2s; &:hover { color: var(--color-primary-500, #ea580c); } }
    .nav-sep { color: var(--color-neutral-300, #cbd5e1); font-weight: 300; }
    .category-tag { color: var(--color-primary-500, #ea580c); font-weight: 900; font-size: 0.8rem; letter-spacing: 1px; }
  }
  .btn-icon-minimal {
    width: 36px; height: 36px; border-radius: var(--radius-circle, 50%); border: 1px solid var(--color-border, #f1f5f9);
    background: transparent; color: var(--color-neutral-300, #cbd5e1); display: flex; align-items: center; justify-content: center;
    cursor: pointer; transition: 0.2s; &:hover { border-color: var(--color-error, #ef4444); color: var(--color-error, #ef4444); }
  }
}

/* --- TYPOGRAPHY --- */
.recipe-title-display { font-family: var(--font-serif, 'Playfair Display', serif); font-size: 3.5rem; line-height: 1.15; color: var(--color-neutral-900, #0f172a); margin: 0 0 16px 0; font-weight: 900; letter-spacing: -0.5px; }
.recipe-desc-text { font-size: 1.1rem; line-height: 1.6; color: var(--color-neutral-500, #64748b); margin-bottom: 40px; max-width: 90%; }

/* --- STATS PILLS --- */
.recipe-stats-group { display: flex; gap: 16px; margin-bottom: 40px; flex-wrap: wrap; }
.stat-card-clean {
  display: flex; align-items: center; gap: 12px; padding: 10px 20px 10px 10px; background: var(--color-neutral-0, #ffffff); border-radius: var(--radius-pill, 100px); border: 1px solid var(--color-neutral-100, #e2e8f0); box-shadow: var(--shadow-sm, 0 2px 8px rgba(0,0,0,0.02));
  .icon-box { width: 34px; height: 34px; border-radius: var(--radius-circle, 50%); display: flex; align-items: center; justify-content: center; &.time-box { background: #f3e8ff; color: #9333ea; } &.fire-box { background: var(--color-primary-50, #ffedd5); color: var(--color-primary-600, #ea580c); } &.star-box { background: #fefce8; color: var(--color-warning, #f59e0b); } }
  .stat-info { display: flex; flex-direction: column; justify-content: center; .s-label { font-size: 0.6rem; font-weight: 800; color: var(--color-neutral-500, #64748b); text-transform: uppercase; letter-spacing: 0.5px; } .s-val { font-size: 0.95rem; font-weight: 800; color: var(--color-neutral-900, #0f172a); } }
  &.highlight { border-color: #fde047; background: #fefce8; }
}

/* --- AUTHOR & ACTIONS --- */
.hero-footer-actions { display: flex; flex-direction: column; gap: 24px; padding-top: 30px; border-top: 1px solid var(--color-border, #f1f5f9); }

.author-info-block {
  display: flex; align-items: center; gap: 12px; cursor: pointer; width: fit-content;
  .avatar-container { position: relative; img { width: 46px; height: 46px; border-radius: var(--radius-circle, 50%); object-fit: cover; border: 2px solid var(--color-neutral-0, #fff); box-shadow: var(--shadow-sm, 0 2px 6px rgba(0,0,0,0.1)); } .tick-badge { position: absolute; bottom: 0; right: 0; background: var(--color-primary-500, #ea580c); width: 14px; height: 14px; border-radius: var(--radius-circle, 50%); display: flex; align-items: center; justify-content: center; border: 2px solid var(--color-neutral-0, #fff); } }
  .auth-text { display: flex; flex-direction: column; .auth-sub { font-size: 0.75rem; color: var(--color-neutral-500, #64748b); } .auth-name { font-weight: 800; color: var(--color-neutral-900, #0f172a); font-size: 1rem; transition: var(--duration-fast, 0.2s); } }
  &:hover .auth-name { color: var(--color-primary-500, #ea580c); }
}

/* --- SOCIAL ROW (LIKE + AVATAR STACK) --- */
.social-actions-row {
  display: flex; align-items: center; gap: 24px; flex-wrap: wrap; width: 100%;
}

.action-btn-group { display: flex; gap: 12px; align-items: center; }

.btn-like-clean {
  height: 44px; padding: 0 20px; border-radius: var(--radius-pill, 100px); border: 1.5px solid var(--color-neutral-100, #e2e8f0); background: var(--color-neutral-0, #fff); color: var(--color-neutral-800, #1e293b); display: flex; align-items: center; gap: 8px; font-weight: 700; font-size: 0.9rem; cursor: pointer; transition: all var(--duration-fast, 0.2s) ease;
  &:hover { border-color: var(--color-error, #f43f5e); color: var(--color-error, #f43f5e); }
  &.is-liked { background: #fff1f2; border-color: var(--color-error, #f43f5e); color: var(--color-error, #f43f5e); }
}

.btn-save-clean {
  width: 44px; height: 44px; border-radius: var(--radius-circle, 50%); border: 1.5px solid var(--color-neutral-100, #e2e8f0); background: var(--color-neutral-0, #fff); color: var(--color-neutral-500, #64748b); display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all var(--duration-fast, 0.2s) ease;
  &:hover { border-color: var(--color-primary-500, #ea580c); color: var(--color-primary-500, #ea580c); }
  &.active { background: var(--color-primary-500, #ea580c); color: #fff; border-color: var(--color-primary-500, #ea580c); box-shadow: var(--shadow-neon, 0 4px 12px rgba(234, 88, 12, 0.3)); }
}

.btn-share-clean {
  width: 44px; height: 44px; border-radius: var(--radius-circle, 50%); border: 1.5px solid var(--color-neutral-100, #e2e8f0); background: var(--color-neutral-0, #fff); color: var(--color-neutral-500, #64748b); display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all var(--duration-fast, 0.2s) ease;
  &:hover { border-color: var(--color-neutral-900, #0f172a); color: var(--color-neutral-900, #0f172a); background: var(--color-neutral-50, #f8fafc); }
}

.liked-by-stack {
  display: flex; align-items: center; gap: 10px; cursor: pointer; padding: 6px 12px; border-radius: 100px; transition: 0.2s;
  &:hover { background: var(--color-neutral-50, #f8fafc); }
  
  .avatar-stack {
    display: flex; flex-direction: row-reverse; /* Để ảnh sau đè lên ảnh trước */
    img { width: 28px; height: 28px; border-radius: 50%; border: 2px solid #fff; margin-left: -10px; object-fit: cover; }
    .more-avt { width: 28px; height: 28px; border-radius: 50%; border: 2px solid #fff; margin-left: -10px; background: var(--color-neutral-100, #f1f5f9); color: var(--color-neutral-600, #475569); font-size: 10px; font-weight: 800; display: flex; align-items: center; justify-content: center; z-index: 1; }
  }
  .liked-text { font-size: 0.85rem; color: var(--color-neutral-500, #64748b); b { color: var(--color-neutral-800, #1e293b); } }
}

/* --- IMAGE COLUMN --- */
.hero-image-col { width: 100%; }

.clean-image-frame {
  position: relative; height: 600px; border-radius: var(--radius-card, 40px); background: var(--color-neutral-50, #f8fafc); overflow: hidden; box-shadow: var(--shadow-exec, 0 20px 40px -10px rgba(0,0,0,0.08));
  .img-cover { width: 100%; height: 100%; object-fit: cover; transition: transform 0.6s ease; }
  &:hover .img-cover { transform: scale(1.02); }
}

.badge-new-top-left { position: absolute; top: 24px; left: 24px; z-index: 10; background: var(--color-primary-500, #ea580c); color: var(--color-neutral-0, #fff); padding: 6px 16px; border-radius: var(--radius-sm, 8px); font-weight: 900; font-size: 0.75rem; letter-spacing: 1px; box-shadow: var(--shadow-neon, 0 4px 12px rgba(234, 88, 12, 0.4)); }

.bottom-glass-stats {
  position: absolute; bottom: 24px; left: 50%; transform: translateX(-50%); background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); padding: 10px 24px; border-radius: var(--radius-pill, 100px); display: flex; gap: 20px; align-items: center; box-shadow: var(--shadow-md, 0 10px 20px rgba(0,0,0,0.08));
  .stat-item { display: flex; align-items: center; gap: 8px; font-size: 0.85rem; font-weight: 800; color: var(--color-neutral-900, #0f172a); svg { color: var(--color-neutral-500, #64748b); } }
}

/* --- MODAL LIKES --- */
.likes-modal-backdrop {
  position: fixed; inset: 0; z-index: var(--z-modal, 2000);
  background: rgba(15, 23, 42, 0.4); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
}

.likes-modal-content {
  background: var(--color-neutral-0, #fff);
  width: 400px; max-width: 90%;
  border-radius: var(--radius-lg, 16px);
  box-shadow: var(--shadow-exec, 0 20px 40px -12px rgba(0,0,0,0.15));
  overflow: hidden;
  
  .modal-header {
    display: flex; justify-content: space-between; align-items: center;
    padding: 16px 20px; border-bottom: 1px solid var(--color-border, #f1f5f9);
    h3 { margin: 0; font-size: 1.1rem; font-weight: 800; color: var(--color-neutral-900, #0f172a); }
    .btn-close-modal { background: none; border: none; font-size: 1.5rem; color: var(--color-neutral-500, #64748b); cursor: pointer; &:hover { color: var(--color-error, #ef4444); } }
  }

  .modal-body {
    max-height: 400px; overflow-y: auto; padding: 12px 0;
    
    .loading-state { text-align: center; padding: 20px; color: var(--color-neutral-500, #64748b); font-style: italic; }
    
    .likes-list {
      list-style: none; padding: 0; margin: 0;
      .like-user-item {
        display: flex; align-items: center; gap: 12px; padding: 10px 20px; cursor: pointer; transition: 0.2s;
        &:hover { background: var(--color-neutral-50, #f8fafc); }
        .u-avt { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; }
        .u-name { font-weight: 700; color: var(--color-neutral-800, #1e293b); }
      }
    }
  }
}

/* Animations */
.fade-scale-enter-active, .fade-scale-leave-active { transition: all 0.3s var(--ease-exec, ease-out); }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; transform: scale(0.95) translateY(10px); }
@keyframes heartPop { 0% { transform: scale(1); } 50% { transform: scale(1.3); } 100% { transform: scale(1); } }
.animating svg { animation: heartPop 0.4s var(--ease-exec, cubic-bezier(0.175, 0.885, 0.32, 1.275)); }

/* Custom Scroll cho modal */
.custom-scroll::-webkit-scrollbar { width: 6px; }
.custom-scroll::-webkit-scrollbar-thumb { background: var(--color-neutral-300, #cbd5e1); border-radius: 10px; }

@media (max-width: 1024px) {
  .hero-container-inner { grid-template-columns: 1fr; gap: 40px; }
  .recipe-title-display { font-size: 2.8rem; }
  .clean-image-frame { height: 400px; border-radius: var(--radius-xl, 24px); }
}
</style>