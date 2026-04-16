<template>
  <div class="gomet-collection hide-scrollbar">
    
    <div class="bg-layer">
      <div class="ambient-glow"></div>
    </div>

    <div class="collection-wrapper fade-up">
      
      <aside class="collection-sidebar">
        <div class="sidebar-sticky">
          
          <div class="profile-summary premium-card">
            <div class="avatar-ring" :class="{ 'is-vip': hasUnlimitedSave }">
              <img v-if="authStore.user?.avatar" :src="authStore.user.avatar" class="user-avt" />
              <span v-else class="user-initial">{{ (authStore.user?.name || 'U').charAt(0).toUpperCase() }}</span>
            </div>
            <div class="user-text">
              <span class="u-label">
                {{ hasUnlimitedSave ? '👑 TÀI KHOẢN PREMIUM' : $t('storage.owner') }}
              </span>
              <span class="u-name">{{ authStore.user?.name || 'Guest' }}</span>
            </div>
          </div>

          <div class="filter-group premium-card">
            <h3 class="group-title">{{ $t('storage.categories') }}</h3>
            <div class="filter-list">
              <button 
                v-for="cat in categories" 
                :key="cat"
                class="btn-filter"
                :class="{ active: activeFilter === cat }"
                @click="activeFilter = cat"
              >
                <span class="filter-name">{{ cat }}</span>
                <span class="filter-count">{{ getCount(cat) }}</span>
              </button>
            </div>
          </div>

          <div class="stats-box premium-card">
            <div class="stat-row">
              <span class="stat-label">Đã lưu trữ</span>
              <strong class="stat-value" :class="{ 'text-error': isOverLimit }">
                {{ savedPosts.length }}
                <span v-if="!hasUnlimitedSave" class="limit-suffix">/ 5</span>
                <span v-else class="limit-suffix">/ ∞</span>
              </strong>
            </div>
            
            <div v-if="!hasUnlimitedSave" class="limit-bar-wrapper">
              <div class="limit-bar-bg">
                <div class="limit-bar-fill" :class="{ 'is-full': isOverLimit }" :style="{ width: Math.min((savedPosts.length / 5) * 100, 100) + '%' }"></div>
              </div>
            </div>
          </div>

        </div>
      </aside>

      <main class="collection-content">
        
        <header class="collection-header premium-card">
          <div class="header-left">
            <div class="brand-tag">KHU LƯU TRỮ</div>
            <h1 class="main-title">
              {{ $t('storage.title').split(' ').slice(0, 2).join(' ') }}
              <span class="text-serif">
                {{ $t('storage.title').split(' ').slice(2).join(' ') }}
              </span>
            </h1>
          </div>
          
          <div class="header-right">
            <div class="search-bar">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
              <input type="text" :placeholder="$t('storage.search_placeholder')" v-model="searchQuery" />
            </div>
          </div>
        </header>

        <div v-if="isOverLimit" class="over-limit-banner premium-card fade-scale">
          <div class="icon-warning">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
          </div>
          <div class="banner-content">
            <h4>Bộ sưu tập đang vượt hạn mức ({{ savedPosts.length }}/5 bài)</h4>
            <p>Bạn vẫn có thể xem và nấu các công thức cũ, nhưng không thể lưu thêm bài mới. Vui lòng xóa bớt hoặc nâng cấp Premium để trải nghiệm không giới hạn.</p>
          </div>
          <button class="btn-upgrade" @click="openPremiumModal">Nâng cấp VIP</button>
        </div>

        <div class="gallery-grid" v-if="filteredPosts.length > 0">
          <RecipeCard
            v-for="post in filteredPosts"
            :key="post.id"
            :post="post"
            @unsaved="handleUnsaveClick(post.id)"
          />
        </div>

        <div v-else-if="!loading" class="empty-state premium-card">
          <div class="empty-icon">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5">
              <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path>
            </svg>
          </div>
          <h3>{{ $t('common.no_data') }}</h3>
          <p>Chưa có công thức nào phù hợp trong bộ sưu tập.</p>
          <button class="btn-explore" @click="$router.push('/search')">
            KHÁM PHÁ NGAY
          </button>
        </div>

      </main>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getFavorites } from '@/services/socialService'
import { toast } from '@/composables/useToast'
import RecipeCard from '@/components/common/RecipeCard.vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const router = useRouter()
const authStore = useAuthStore()
const activeFilter = ref('All')
const searchQuery = ref('')
const savedPosts = ref([])
const loading = ref(true)

const levelMap = { 1: 'Easy', 2: 'Medium', 3: 'Hard' }

// --- LOGIC VIP ---
const hasUnlimitedSave = computed(() => {
  const role = String(authStore.user?.role || '').toUpperCase()
  const isPremiumUser = authStore.user?.isPremium === true || authStore.user?.IsPremium === true || role === 'PREMIUM'
  const isAdmin = authStore.user?.isAdmin === true || role === 'ADMIN'
  return isPremiumUser || isAdmin
})

const isOverLimit = computed(() => !hasUnlimitedSave.value && savedPosts.value.length > 5)

const openPremiumModal = () => {
  window.dispatchEvent(new CustomEvent('ui:open-premium'))
}

// --- XỬ LÝ DỮ LIỆU ---
const normalizePost = (fav) => {
  return {
    id: fav.postID,
    title: fav.title ?? 'No title',
    category: fav.categoryName ?? 'Uncategorized',
    image: fav.media || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c',
    time: fav.cookingTime ? `${fav.cookingTime} min` : '30 min',
    rating: fav.rating ?? 0,
    likes: fav.favoriteCount ?? 0, 
    isLiked: fav.isLiked ?? false, 
    createdAt: fav.createdAt,
    author: {
      name: fav.userName ?? "Gomet Chef",
      avatar: fav.avatar ?? null
    },
    difficulty: levelMap[fav.level] ?? 'Easy'
  }
}

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    loading.value = false
    return
  }

  try {
    const res = await getFavorites(authStore.user.accountID)
    
    // Bóc tách API chuẩn
    const data = res?.data?.data || res?.data || res || []
    
    savedPosts.value = Array.isArray(data) ? data.map(normalizePost) : []
  } catch (err) {
    toast.error(t('toast.load_error'))
  } finally {
    loading.value = false
  }

  // Lắng nghe sự kiện bỏ lưu từ các component con để đồng bộ mượt mà
  window.addEventListener('sync-favorite', handleSyncEvent)
})

onUnmounted(() => {
  window.removeEventListener('sync-favorite', handleSyncEvent)
})

// --- ĐỒNG BỘ DỮ LIỆU MƯỢT MÀ ---
// Khi RecipeCard báo đã unfavorite, ta chỉ việc xóa khỏi mảng nội bộ (không cần hiện popup hỏi lại)
const handleUnsaveClick = (postId) => {
  savedPosts.value = savedPosts.value.filter(p => p.id !== postId)
}

const handleSyncEvent = (e) => {
  if (!e.detail.status) { // Nếu status = false (Bỏ lưu)
    handleUnsaveClick(e.detail.id)
  }
}

// --- LOGIC BỘ LỌC ---
const categories = computed(() => {
  const cats = new Set(savedPosts.value.map(p => p.category))
  return ['All', ...Array.from(cats)]
})

const getCount = (cat) => {
  if (cat === 'All') return savedPosts.value.length
  return savedPosts.value.filter(p => p.category === cat).length
}

const filteredPosts = computed(() => {
  let posts = savedPosts.value
  if (activeFilter.value !== 'All') posts = posts.filter(p => p.category === activeFilter.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    posts = posts.filter(p => p.title.toLowerCase().includes(q))
  }
  return posts
})
</script>

<style scoped>
/* --- CORE LAYOUT --- */
.gomet-collection {
  width: 100%; min-height: 100vh; 
  background: #FFFFFF; 
  color: #0f172a;
  font-family: 'Mulish', sans-serif; 
  position: relative; overflow-x: hidden;
}

.bg-layer { position: absolute; inset: 0; pointer-events: none; z-index: 0; overflow: hidden; }
.ambient-glow {
  position: absolute; top: -200px; right: -200px; width: 600px; height: 600px;
  background: radial-gradient(circle, rgba(234, 88, 12, 0.05) 0%, rgba(255, 255, 255, 0) 70%);
  border-radius: 50%;
}

.collection-wrapper {
  display: flex; width: 100%; max-width: 1340px; margin: 0 auto; padding: 40px 24px; gap: 40px;
  position: relative; z-index: 10;
}

.premium-card {
  background: #FFFFFF;
  border: 1px solid rgba(0, 0, 0, 0.04);
  border-radius: 20px;
  box-shadow: 0 10px 40px -10px rgba(0,0,0,0.03);
  padding: 24px;
}

/* SIDEBAR */
.collection-sidebar { width: 300px; flex-shrink: 0; }
.sidebar-sticky { position: sticky; top: 100px; display: flex; flex-direction: column; gap: 24px; }

.profile-summary { display: flex; align-items: center; gap: 16px; }
.avatar-ring {
  width: 56px; height: 56px; border-radius: 50%; padding: 3px;
  background: #e2e8f0; /* Default */
  display: flex; align-items: center; justify-content: center;
}
.avatar-ring.is-vip { background: linear-gradient(135deg, #ea580c, #f59e0b); }

.user-avt { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; border: 2px solid #fff; }
.user-initial { font-size: 1.5rem; font-weight: 700; color: #fff; }
.user-text { display: flex; flex-direction: column; }
.u-label { font-size: 0.75rem; font-weight: 800; color: #ea580c; text-transform: uppercase; letter-spacing: 1px; }
.u-name { font-size: 1.1rem; font-weight: 800; color: #0f172a; }

.filter-group { padding: 20px 24px; }
.group-title { font-size: 0.75rem; font-weight: 800; color: #94a3b8; margin-bottom: 16px; text-transform: uppercase; letter-spacing: 1px; }
.filter-list { display: flex; flex-direction: column; gap: 6px; }

.btn-filter {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 16px; background: transparent; border: none;
  border-radius: 12px; cursor: pointer; transition: all 0.3s ease;
}
.filter-name { font-size: 0.95rem; font-weight: 600; color: #475569; }
.filter-count { font-size: 0.8rem; font-weight: 700; color: #94a3b8; background: #f1f5f9; padding: 2px 10px; border-radius: 100px; }

.btn-filter:hover { background: #f8fafc; }
.btn-filter.active { background: #fff7ed; }
.btn-filter.active .filter-name { color: #ea580c; font-weight: 800; }
.btn-filter.active .filter-count { background: #ea580c; color: #fff; }

/* Thống kê hạn mức */
.stat-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.stat-label { font-size: 0.9rem; color: #64748b; font-weight: 700; }
.stat-value { font-size: 1.4rem; font-weight: 900; color: #0f172a; transition: color 0.3s; }
.stat-value.text-error { color: #ef4444; }
.limit-suffix { font-size: 0.9rem; font-weight: 600; color: #94a3b8; margin-left: 2px; }

.limit-bar-wrapper { width: 100%; margin-top: 8px; }
.limit-bar-bg { width: 100%; height: 8px; background: #f1f5f9; border-radius: 10px; overflow: hidden; }
.limit-bar-fill { height: 100%; background: linear-gradient(90deg, #ea580c, #f59e0b); border-radius: 10px; transition: width 0.5s ease; }
.limit-bar-fill.is-full { background: #ef4444; }

/* MAIN CONTENT */
.collection-content { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.collection-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;
  padding: 32px;
}
.brand-tag { font-size: 0.75rem; font-weight: 800; color: #ea580c; letter-spacing: 2px; margin-bottom: 8px; text-transform: uppercase; }
.main-title { font-family: 'Playfair Display', serif; font-size: 2.5rem; font-weight: 900; color: #0f172a; margin: 0; letter-spacing: -0.5px; }
.text-serif { font-style: italic; color: #ea580c; }

.search-bar {
  display: flex; align-items: center; gap: 12px; background: #f8fafc; padding: 14px 24px; 
  border-radius: 100px; border: 1px solid transparent; width: 320px; transition: 0.3s;
}
.search-bar:focus-within { background: #fff; border-color: rgba(234, 88, 12, 0.3); }
.search-bar input { border: none; background: transparent; outline: none; font-size: 0.95rem; width: 100%; }

/* Banner Cảnh báo */
.over-limit-banner {
  display: flex; align-items: center; gap: 20px; background: #fef2f2; border: 1px solid #fecaca; margin-bottom: 24px; padding: 20px 24px;
}
.icon-warning { color: #ef4444; }
.banner-content h4 { margin: 0 0 6px 0; color: #991b1b; font-size: 1.05rem; font-weight: 800; }
.banner-content p { margin: 0; color: #b91c1c; font-size: 0.9rem; line-height: 1.5; }
.btn-upgrade { margin-left: auto; background: #ef4444; color: #fff; border: none; padding: 10px 20px; border-radius: 100px; font-weight: 700; cursor: pointer; transition: 0.2s; white-space: nowrap; box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3); }
.btn-upgrade:hover { background: #dc2626; transform: translateY(-2px); }

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 32px;
}

/* Empty state */
.empty-state { text-align: center; padding: 80px 40px; }
.empty-icon { font-size: 3rem; margin-bottom: 20px; opacity: 0.5; }
.btn-explore { 
  background: #0f172a; color: #fff; border: none; padding: 14px 32px; 
  border-radius: 100px; font-weight: 700; cursor: pointer; transition: 0.3s; 
  margin-top: 20px;
}
.btn-explore:hover { background: #ea580c; }

/* Animations */
.fade-up { animation: fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.fade-scale { animation: fadeScale 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
@keyframes fadeScale { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }

/* =======================================================
   🔥 HỆ THỐNG RESPONSIVE (ĐÃ FIX LỖI TRỒNG CHỮ DANH MỤC)
   ======================================================= */

@media (max-width: 1200px) {
  .collection-wrapper { padding: 30px 20px; gap: 24px; }
  .collection-sidebar { width: 260px; }
  
  .main-title { font-size: 2rem; }
  .collection-header { padding: 24px; }
  
  .gallery-grid { grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 24px; }
}

@media (max-width: 992px) {
  .collection-wrapper { flex-direction: column; padding: 20px; gap: 20px; }
  
  .collection-sidebar { width: 100%; }
  .sidebar-sticky { 
    position: relative; top: 0; 
    flex-direction: row; flex-wrap: wrap; gap: 16px; 
    align-items: center; 
  }
  
  .profile-summary { flex: 1; min-width: 250px; padding: 16px 20px; margin: 0; }
  .stats-box { flex: 1; min-width: 200px; padding: 16px 20px; margin: 0; }
  
  /* 🔥 FIX DANH MỤC TRỒNG LÊN NHAU */
  .filter-group { 
    width: 100%; padding: 12px 16px; margin: 0; 
    display: flex; align-items: center; gap: 16px;
  }
  .group-title { margin: 0; white-space: nowrap; flex-shrink: 0; } /* Không cho chữ tiêu đề bị ép */
  .filter-list { 
    display: flex; /* Bắt buộc để cuộn ngang hoạt động */
    flex-direction: row; 
    overflow-x: auto; 
    -ms-overflow-style: none; scrollbar-width: none; 
    gap: 8px; width: 100%;
    padding-bottom: 2px; /* Tránh cắt mất viền/bóng của nút khi cuộn */
  }
  .filter-list::-webkit-scrollbar { display: none; }
  .btn-filter { 
    padding: 8px 16px; 
    white-space: nowrap; /* Ép chữ trên 1 hàng */
    flex-shrink: 0; /* Không cho phép nút bị móp lại */
    border: 1px solid #E2E8F0; 
  }
  
  .collection-header { flex-direction: column; align-items: flex-start; gap: 16px; }
  .header-right, .search-bar { width: 100%; }
  
  .over-limit-banner { flex-direction: column; align-items: flex-start; gap: 16px; }
  .btn-upgrade { margin-left: 0; width: 100%; text-align: center; }
}

@media (max-width: 768px) {
  .collection-wrapper { padding: 15px; }
  
  .sidebar-sticky { flex-direction: column; align-items: stretch; }
  .profile-summary, .stats-box, .filter-group { min-width: 100%; }
  
  /* Đẩy danh mục xuống dưới tiêu đề trên điện thoại để đỡ rối */
  .filter-group { flex-direction: column; align-items: flex-start; gap: 10px; }
  
  .main-title { font-size: 1.6rem; }
  .brand-tag { font-size: 0.65rem; }
  
  .gallery-grid { grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
}

@media (max-width: 480px) {
  .collection-wrapper { padding: 10px; }
  .premium-card { padding: 16px; border-radius: 16px; }
  
  .avatar-ring { width: 48px; height: 48px; }
  .u-name { font-size: 1rem; }
  
  .btn-filter { padding: 6px 12px; font-size: 0.85rem; }
  .filter-count { padding: 2px 6px; font-size: 0.75rem; }
  
  .stat-value { font-size: 1.2rem; }
  
  .gallery-grid { grid-template-columns: 1fr; }
  
  .empty-state { padding: 40px 20px; }
  .btn-explore { width: 100%; font-size: 0.9rem; padding: 12px 20px; }
}

@media (max-height: 500px) and (orientation: landscape) {
  .collection-sidebar { display: none; }
  .gallery-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>