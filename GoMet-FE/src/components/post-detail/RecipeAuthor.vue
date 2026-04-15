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
            <span class="verified-dot" :title="t('recipe.trusted_chef')">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="4"><polyline points="20 6 9 17 4 12"></polyline></svg>
            </span>
          </div>

          <div class="action-buttons">
            <button class="btn-icon-monochrome hide-on-mobile" @click="handleContactChef" :title="t('post_detail.contact_author')">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            </button>

            <button class="btn-follow-black" :class="{ 'is-following': isFollowing }" @click="toggleFollow">
              <span v-if="isFollowing">{{ t('post_detail.following') }}</span>
              <span v-else>{{ t('post_detail.follow') }}</span>
            </button>
          </div>
        </div>

        <p class="auth-bio-text">{{ realBio || t('post_detail.author_bio_fallback') }}</p>

        <div class="auth-stats-inline">
          <div class="s-item">
            <span class="s-val">{{ authorStats.posts }}</span>
            <span class="s-label">{{ t('recipe.recipes_count') }}</span>
          </div>
          <div class="s-divider"></div>
          <div class="s-item">
            <span class="s-val">{{ authorStats.followers }}</span>
            <span class="s-label">{{ t('post_detail.followers_count') }}</span>
          </div>
          <div class="s-divider"></div>
          <div class="s-item highlight">
            <span class="s-val">{{ authorStats.totalLikes }}</span>
            <span class="s-label">{{ t('recipe.liked_total') }}</span>
          </div>
        </div>

      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, watch } from 'vue' 
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import api from '@/services/api'
import { toast } from '@/composables/useToast'
import { checkFollow, follow, unfollow } from '@/services/socialService'
import { getUserStats } from '@/services/userService'

const props = defineProps({ post: { type: Object, required: true } })
const { t } = useI18n()
const router = useRouter()
const authStore = useAuthStore()
const chatStore = useChatStore()

const isFollowing = ref(false)
const authorStats = ref({ posts: 0, followers: 0, totalLikes: 0 })
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

  if (uid) {
    try {
      if (uid !== authorID) {
        const isFoll = await checkFollow(uid, authorID)
        isFollowing.value = !!isFoll
      } else {
        isFollowing.value = false 
      }
    } catch { /* ignore */ }
  }
}

watch(() => props.post, (newPost) => { loadSocialState(newPost) }, { immediate: true })

const handleContactChef = async () => {
  // Ngăn chặn nếu người dùng cố tình click (dù nút đã ẩn CSS) trên thiết bị mobile
  if (window.innerWidth <= 1024) {
    toast.info(t('post_detail.desktop_chat_only'));
    return;
  }

  if (!authStore.isAuthenticated) { toast.warn(t('toast.need_login')); return }
  const currentUserId = authStore.user?.accountID || authStore.user?.id
  const chefId = props.post?.authorID || props.post?.authorId

  if (currentUserId === chefId) { 
    toast.info(t('post_detail.self_chat_blocked')); return 
  }
  
  try {
    const res = await api.post('/api/conversations/access', { user1Id: currentUserId, user2Id: chefId })
    chatStore.openChat({ id: res.data.conversationID, name: props.post.author, avatar: props.post.authorAvatar, online: true })
    chatStore.isMessengerOpen = true
    toast.success(t('post_detail.connecting_with', { name: props.post.author }))
  } catch (err) { toast.error(t('post_detail.chat_connect_error')) }
}

const goToChefProfile = () => { 
  const authorID = props.post?.authorID || props.post?.authorId
  if (authorID) router.push(`/profile/${authorID}`) 
}

const toggleFollow = async () => {
  if (!authStore.isAuthenticated) { toast.warn(t('toast.need_login')); return }
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
  } catch (err) { toast.error(t('post_detail.follow_error')) }
}
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
    &:hover::after { content: "Unfollow"; }
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

/* =======================================================
   🔥 HỆ THỐNG RESPONSIVE (TỐI ƯU MỌI THIẾT BỊ)
   ======================================================= */

/* --- 1. Màn hình Laptop nhỏ & Tablet ngang (Dưới 1024px) --- */
@media (max-width: 1024px) {
  .author-flat-container { gap: 20px; }
  .name-group .auth-name { font-size: 1.4rem; }
  .auth-bio-text { font-size: 0.95rem; max-width: 100%; }
  
  /* ẨN NÚT NHẮN TIN TRÊN TABLET & MOBILE */
  .hide-on-mobile { display: none !important; }
}

/* --- 2. Tablet dọc & Mobile ngang (Dưới 768px) --- */
@media (max-width: 768px) {
  .author-flat-container { 
    flex-direction: column; 
    align-items: center; 
    text-align: center;
    gap: 16px; 
  }
  
  .auth-avatar-col { width: 80px; height: 80px; margin: 0 auto; }
  
  .auth-info-col { width: 100%; align-items: center; }
  
  .auth-top-row { 
    flex-direction: column; 
    align-items: center; 
    width: 100%;
    gap: 12px;
    margin-bottom: 12px;
  }
  
  .name-group { justify-content: center; }
  .name-group .auth-name { font-size: 1.5rem; }
  
  .action-buttons { 
    width: 100%; 
    justify-content: center; 
    margin-bottom: 8px;
  }
  
  .btn-follow-black { 
    flex: 1; 
    max-width: 250px; 
    padding: 10px 24px;
  }
  
  .auth-bio-text { margin-bottom: 20px; }

  .auth-stats-inline { 
    justify-content: center; 
    gap: 12px;
  }
  .s-item { flex-direction: column; align-items: center; gap: 4px; }
  .s-divider { display: none; }
}

/* --- 3. Mobile Lớn (Dưới 600px) --- */
@media (max-width: 600px) {
  .author-section { padding-bottom: 30px; margin-bottom: 30px; }
  .auth-bio-text { font-size: 0.9rem; line-height: 1.5; }
  
  .auth-stats-inline { gap: 20px; }
  .s-item .s-val { font-size: 1.2rem; }
  .s-item .s-label { font-size: 0.7rem; }
}

/* --- 4. Mobile Nhỏ (Dưới 400px) --- */
@media (max-width: 400px) {
  .name-group .auth-name { font-size: 1.3rem; }
  
  .auth-stats-inline { 
    display: grid; 
    grid-template-columns: 1fr 1fr; 
    width: 100%; 
    gap: 15px; 
  }
  .s-item.highlight { grid-column: span 2; }
  
  .btn-follow-black { max-width: 100%; } 
}
</style>