<template>
  <section class="author-section fade-in">
    <div class="author-flat-container">
      
      <div class="auth-avatar-col" @click="goToChefProfile" :title="$t('profile.view_profile')">
        <img :src="post.authorAvatar" class="auth-img" alt="Author Avatar">
      </div>

      <div class="auth-info-col">
        <div class="auth-top-row">
          <div class="name-group" @click="goToChefProfile">
            <h3 class="auth-name">{{ post.author }}</h3>
            <span class="verified-dot" title="Đầu bếp uy tín">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="4"><polyline points="20 6 9 17 4 12"></polyline></svg>
            </span>
          </div>

          <div class="action-buttons">
            <button class="btn-icon-monochrome" :class="{ 'is-saved': isFavorite }" @click="toggleFavorite" :title="isFavorite ? 'Bỏ lưu' : 'Lưu bài viết'">
              <svg width="18" height="18" viewBox="0 0 24 24" :fill="isFavorite ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2.5"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path></svg>
            </button>

            <button class="btn-icon-monochrome" @click="handleContactChef" title="Nhắn tin cho tác giả">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            </button>

            <button class="btn-follow-black" :class="{ 'is-following': isFollowing }" @click="toggleFollow">
              <span v-if="isFollowing">Đang theo dõi</span>
              <span v-else>Theo dõi</span>
            </button>
          </div>
        </div>

        <p class="auth-bio-text">{{ realBio || 'Đầu bếp này rất ngại ngùng, chưa để lại tiểu sử nào...' }}</p>

        <div class="auth-stats-inline">
          <div class="s-item">
            <span class="s-val">{{ authorStats.posts }}</span>
            <span class="s-label">Công thức</span>
          </div>
          <div class="s-divider"></div>
          <div class="s-item">
            <span class="s-val">{{ authorStats.followers }}</span>
            <span class="s-label">Người theo dõi</span>
          </div>
          <div class="s-divider"></div>
          <div class="s-item highlight">
            <span class="s-val">{{ authorStats.totalLikes }}</span>
            <span class="s-label">Đã được thích</span>
          </div>
        </div>

      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue' // 🔥 Đã gom import lên đầu
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import api from '@/services/api'
import { toast } from '@/composables/useToast'
import { checkFollow, follow, unfollow, checkFavorite, addFavorite, removeFavorite, getFavorites } from '@/services/socialService'
import { getUserStats } from '@/services/userService'

const props = defineProps({ post: { type: Object, required: true } })
const { t } = useI18n()
const router = useRouter()
const authStore = useAuthStore()
const chatStore = useChatStore()

const isFollowing = ref(false)
const isFavorite = ref(false)
const authorStats = ref({ posts: 0, followers: 0, totalLikes: 0 })
const isSaving = ref(false) 
const realBio = ref('')

const loadSocialState = async (postData) => {
  if (!postData) return
  
  realBio.value = postData.authorBio || postData.bio || ''
  const uid = authStore.user?.accountID || authStore.user?.id
  const authorID = postData.authorID || postData.authorId

  if (authorID) {
    try {
      const stats = await getUserStats(authorID)
      authorStats.value = { 
        posts: stats.postCount || stats.posts || 0, 
        followers: stats.followerCount || stats.followers || 0,
        totalLikes: stats.totalLikes || stats.likes || 0
      }
      if (stats.bio || stats.authorBio) {
        realBio.value = stats.bio || stats.authorBio
      }
    } catch { /* ignore */ }
  }

  // 🔥 ĐÃ SỬA TỪ ĐÂY: Tách riêng logic check Lưu bài và Follow
  if (uid) {
    try {
      // 1. Ai đăng nhập cũng được check xem đã Lưu bài này chưa (kể cả tác giả)
      const isFav = await checkFavorite(uid, postData.postID || postData.id)
      isFavorite.value = !!isFav

      // 2. Chỉ check Follow nếu người đang xem KHÔNG PHẢI là tác giả
      if (uid !== authorID) {
        const isFoll = await checkFollow(uid, authorID)
        isFollowing.value = !!isFoll
      } else {
        isFollowing.value = false // Tác giả thì không tự follow chính mình
      }
    } catch { /* ignore */ }
  }
}

watch(() => props.post, (newPost) => { loadSocialState(newPost) }, { immediate: true })

const handleContactChef = async () => {
  if (!authStore.isAuthenticated) { toast.warn(t('toast.need_login') || 'Vui lòng đăng nhập!'); return }
  const currentUserId = authStore.user?.accountID || authStore.user?.id
  const chefId = props.post?.authorID || props.post?.authorId

  if (currentUserId === chefId) { 
    toast.info('Sếp không thể tự nhắn tin cho chính mình đâu nha!'); return 
  }
  
  try {
    const res = await api.post('/api/conversations/access', { user1Id: currentUserId, user2Id: chefId })
    chatStore.openChat({ id: res.data.conversationID, name: props.post.author, avatar: props.post.authorAvatar, online: true })
    chatStore.isMessengerOpen = true
    toast.success(`Đang kết nối với ${props.post.author}...`)
  } catch (err) { toast.error('Lỗi kết nối chat!') }
}

const goToChefProfile = () => { 
  const authorID = props.post?.authorID || props.post?.authorId
  if (authorID) router.push(`/profile/${authorID}`) 
}

const toggleFollow = async () => {
  if (!authStore.isAuthenticated) { toast.warn(t('toast.need_login') || 'Vui lòng đăng nhập!'); return }
  const uid = authStore.user.accountID || authStore.user.id
  const authorID = props.post?.authorID || props.post?.authorId
  if (!authorID || uid === authorID) return
  
  try {
    if (isFollowing.value) {
      await unfollow(uid, authorID); 
      isFollowing.value = false; 
      authorStats.value.followers = Math.max(0, authorStats.value.followers - 1);
    } else {
      await follow(uid, authorID); 
      isFollowing.value = true; 
      authorStats.value.followers++;
    }
  } catch (err) { toast.error('Lỗi Follow!') }
}

// 🔥 HÀM TOGGLE FAVORITE (ĐÃ ĐÓNG NGOẶC CHUẨN)
const toggleFavorite = async () => {
  if (!authStore.isAuthenticated) { 
    return toast.warn(t('toast.need_login') || 'Vui lòng đăng nhập để lưu bài viết!'); 
  }
  if (isSaving.value) return; 

  const uid = authStore.user.accountID || authStore.user.id
  const postID = props.post?.postID || props.post?.id
  if (!postID) return

  const isPremiumUser = authStore.user?.isPremium || authStore.user?.role === 'PREMIUM' || authStore.user?.IsPremium;
  const isAdmin = authStore.user?.isAdmin || authStore.user?.role === 'ADMIN' || authStore.user?.role === 'admin';
  const hasUnlimitedSave = isPremiumUser || isAdmin;

  isSaving.value = true;
  try {
    if (isFavorite.value) {
      await removeFavorite(uid, postID); 
      isFavorite.value = false;
      toast.success("Đã bỏ lưu công thức!");
      window.dispatchEvent(new CustomEvent('sync-favorite', { detail: { id: postID, status: false } }));
    } else {
      if (!hasUnlimitedSave) {
        const currentFavorites = await getFavorites(uid);
        if (currentFavorites && currentFavorites.length >= 5) {
          toast.warn("Bộ sưu tập đã đầy! Nâng cấp Premium để lưu không giới hạn sếp nhé.");
          window.dispatchEvent(new CustomEvent('ui:open-premium'));
          isSaving.value = false;
          return; 
        }
      }
      await addFavorite(uid, postID); 
      isFavorite.value = true;
      toast.success("Đã lưu công thức thành công!");
      window.dispatchEvent(new CustomEvent('sync-favorite', { detail: { id: postID, status: true } }));
    }
  } catch (err) { 
    console.error("Save error:", err);
    toast.error('Lỗi yêu thích, vui lòng thử lại sau!') 
  } finally {
    isSaving.value = false;
  }
}

// 🔥 CÁC HÀM ĐỒNG BỘ (NẰM NGOÀI TOGGLEFAVORITE)
const handleSync = (e) => {
  const currentPostId = props.post?.postID || props.post?.id;
  if (e.detail.id === currentPostId) {
    isFavorite.value = e.detail.status;
  }
}

onMounted(() => {
  window.addEventListener('sync-favorite', handleSync);
});

onUnmounted(() => {
  window.removeEventListener('sync-favorite', handleSync);
});
</script>

<style scoped lang="scss">
.author-section { 
  margin-bottom: 40px; 
  padding-bottom: 40px;
  border-bottom: 1px solid var(--color-border, #F1F5F9);
}

.author-flat-container {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

/* --- TRÁI: AVATAR --- */
.auth-avatar-col {
  flex-shrink: 0;
  width: 70px; height: 70px;
  border-radius: var(--radius-circle, 50%);
  cursor: pointer;
  overflow: hidden;
  box-shadow: var(--shadow-sm, 0 4px 12px rgba(0,0,0,0.05));
  border: 1px solid var(--color-border, #F1F5F9);
  
  .auth-img {
    width: 100%; height: 100%; object-fit: cover;
    transition: transform 0.4s var(--ease-exec, ease);
  }
  
  &:hover .auth-img { transform: scale(1.08); }
}

/* --- PHẢI: INFO & ACTIONS --- */
.auth-info-col {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* HEADER (Tên + Nút) */
.auth-top-row {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 8px; flex-wrap: wrap; gap: 16px;
}

.name-group {
  display: flex; align-items: center; gap: 8px; cursor: pointer;
  .auth-name {
    font-family: var(--font-serif, 'Playfair Display', serif);
    font-size: 1.6rem; font-weight: 900; color: var(--color-neutral-900, #0F172A);
    margin: 0; transition: color 0.2s;
  }
  .verified-dot {
    width: 18px; height: 18px; border-radius: 50%;
    background: var(--color-primary-500, #ea580c);
    display: flex; align-items: center; justify-content: center;
  }
  &:hover .auth-name { color: var(--color-primary-500, #ea580c); }
}

/* NÚT TƯƠNG TÁC (Monochrome) */
.action-buttons {
  display: flex; align-items: center; gap: 12px;
}

.btn-icon-monochrome {
  width: 38px; height: 38px; border-radius: var(--radius-circle, 50%);
  border: 1.5px solid var(--color-border, #f1f5f9);
  background: transparent; color: var(--color-neutral-500, #64748b);
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: 0.2s;

  &:hover { border-color: var(--color-neutral-900, #0f172a); color: var(--color-neutral-900, #0f172a); background: var(--color-neutral-50, #f8fafc); }
  &.is-saved { color: var(--color-primary-500, #ea580c); border-color: var(--color-primary-500, #ea580c); background: var(--color-primary-50, #fff4ed); }
}

/* Nút Theo dõi chuẩn X/Threads */
.btn-follow-black {
  padding: 8px 24px; border-radius: var(--radius-pill, 100px);
  font-weight: 800; font-size: 0.85rem; cursor: pointer; transition: 0.2s;
  background: var(--color-neutral-900, #0F172A); color: #fff; border: 1.5px solid var(--color-neutral-900, #0F172A);
  
  &:hover { background: var(--color-black-deep, #030712); transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
  
  &.is-following {
    background: transparent; color: var(--color-neutral-900, #0F172A);
    &:hover { background: #fee2e2; color: #dc2626; border-color: #dc2626; }
    &:hover span { display: none; }
    &:hover::after { content: "Hủy theo dõi"; }
  }
}

/* BIO */
.auth-bio-text {
  font-size: 1rem; line-height: 1.6; color: var(--color-neutral-500, #64748B);
  margin: 0 0 16px 0; font-style: italic; max-width: 90%;
}

/* THỐNG KÊ (INLINE) */
.auth-stats-inline {
  display: flex; align-items: center; gap: 16px; flex-wrap: wrap;
}

.s-item {
  display: flex; align-items: baseline; gap: 6px;
  .s-val { font-size: 1.1rem; font-weight: 900; color: var(--color-neutral-900, #0F172A); }
  .s-label { font-size: 0.75rem; color: var(--color-neutral-500, #64748B); text-transform: uppercase; font-weight: 800; letter-spacing: 0.5px; }
  &.highlight .s-val, &.highlight .s-label { color: var(--color-primary-500, #ea580c); }
}

.s-divider {
  width: 1px; height: 14px; background: var(--color-neutral-300, #cbd5e1);
}

/* ANIMATION */
.fade-in { animation: fadeIn 0.6s var(--ease-exec, ease-out); }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

/* RESPONSIVE */
@media (max-width: 768px) {
  .author-flat-container { flex-direction: column; gap: 16px; }
  .auth-top-row { flex-direction: column; align-items: flex-start; }
  .action-buttons { width: 100%; }
  .btn-follow-black { flex: 1; }
}
</style>