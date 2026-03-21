<template>
  <div class="gomet-collection hide-scrollbar">
    
    <div class="bg-layer">
      <div class="ambient-glow"></div>
    </div>

    <div class="collection-wrapper fade-up">
      
      <aside class="collection-sidebar">
        <div class="sidebar-sticky">
          
          <div class="profile-summary premium-card">
            <div class="avatar-ring">
              <img v-if="authStore.user?.avatar" :src="authStore.user.avatar" class="user-avt" />
              <span v-else class="user-initial">{{ (authStore.user?.name || 'U').charAt(0).toUpperCase() }}</span>
            </div>
            <div class="user-text">
              <span class="u-label">{{ $t('storage.owner') }}</span>
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
              <span class="stat-label">{{ $t('storage.stats_saved') }}</span>
              <strong class="stat-value">{{ savedPosts.length }}</strong>
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

        <div class="gallery-grid" v-if="filteredPosts.length > 0">
          <RecipeCard
            v-for="post in filteredPosts"
            :key="post.id"
            :post="post"
            @unsaved="handleUnsaveClick(post.id)"
          />
        </div>

        <div v-else class="empty-state premium-card">
          <div class="empty-icon">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5">
              <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path>
            </svg>
          </div>
          <h3>{{ $t('common.no_data') }}</h3>
          <p>Chưa có công thức nào trong bộ sưu tập này.</p>
          <button class="btn-explore" @click="$router.push('/search')">
            KHÁM PHÁ NGAY
          </button>
        </div>

      </main>
    </div>

    <div v-if="showConfirm" class="popup-overlay" @click.self="showConfirm = false">
      <div class="popup-box fade-scale">
        <div class="popup-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#EA580C" stroke-width="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path>
          </svg>
        </div>
        <h3>{{ $t('storage.confirm_unsave') }}</h3>
        <p>{{ $t('storage.confirm_unsave_desc') }}</p>

        <div class="popup-actions">
          <button class="btn-cancel" @click="showConfirm = false">
            {{ $t('common.cancel') }}
          </button>
          <button class="btn-delete" @click="confirmUnsave" :disabled="isRemoving">
            {{ isRemoving ? 'Đang xử lý...' : $t('storage.unsave') }}
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getFavorites, removeFavorite } from '@/services/socialService'
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

/**
 * 🔥 CHỖ NÀY LÀ CỐT LÕI ĐỂ FIX LỖI LIKE VÀ THỜI GIAN
 * Normalize dữ liệu từ FavoriteDTO sang RecipeCard props
 */
const normalizePost = (fav) => {
  return {
    id: fav.postID,
    title: fav.title ?? 'No title',
    category: fav.categoryName ?? 'Uncategorized',
    image: fav.media || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c',
    time: fav.cookingTime ? `${fav.cookingTime} min` : '30 min',
    
    // FIX Rating:
    rating: fav.rating ?? 0,
    
    // 🔥 FIX LIKE: Lấy đúng số lượng Like và trạng thái Like từ Backend
    likes: fav.favoriteCount ?? 0, 
    isLiked: fav.isLiked ?? false, 

    // 🔥 FIX THỜI GIAN: Truyền createdAt để RecipeCard tính "X phút trước"
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
    const data = await getFavorites(authStore.user.accountID)
    // Map dữ liệu qua hàm normalize đã fix
    savedPosts.value = Array.isArray(data)
      ? data.map(normalizePost)
      : []
    
    console.log("Storage Loaded:", savedPosts.value)
  } catch (err) {
    console.warn('StoragePage load error:', err)
    toast.error(t('toast.load_error'))
  } finally {
    loading.value = false
  }
})

// Logic Filter
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

// Logic Popup xóa
const showConfirm = ref(false)
const selectedPost = ref(null)
const isRemoving = ref(false)

const handleUnsaveClick = (postId) => {
  selectedPost.value = postId
  showConfirm.value = true
}

const confirmUnsave = async () => {
  if (!selectedPost.value) return
  isRemoving.value = true
  try {
    // Gọi API xóa thực tế
    await removeFavorite(authStore.user.accountID, selectedPost.value)
    
    // Cập nhật local state ngay lập tức
    savedPosts.value = savedPosts.value.filter(p => p.id !== selectedPost.value)
    showConfirm.value = false
    toast.success('Đã xóa khỏi bộ sưu tập!')
  } catch (error) {
    toast.error('Không thể xóa bài viết, vui lòng thử lại!')
  } finally {
    isRemoving.value = false
    selectedPost.value = null
  }
}
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
  background: linear-gradient(135deg, #ea580c, #f59e0b);
  display: flex; align-items: center; justify-content: center;
}
.user-avt { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; border: 2px solid #fff; }
.user-initial { font-size: 1.5rem; font-weight: 700; color: #fff; }
.user-text { display: flex; flex-direction: column; }
.u-label { font-size: 0.75rem; font-weight: 700; color: #94a3b8; text-transform: uppercase; letter-spacing: 1px; }
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

.stat-row { display: flex; justify-content: space-between; align-items: center; }
.stat-label { font-size: 0.9rem; color: #64748b; font-weight: 600; }
.stat-value { font-size: 1.2rem; font-weight: 800; color: #0f172a; }

/* MAIN CONTENT */
.collection-content { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.collection-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px;
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

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 32px;
}

/* POPUP */
.popup-overlay {
  position: fixed; inset: 0; background: rgba(15, 23, 42, 0.6); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center; z-index: 1000;
}
.popup-box {
  background: #fff; padding: 40px; border-radius: 24px; width: 360px; text-align: center;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
}
.popup-icon { display: inline-flex; padding: 16px; background: #fff7ed; border-radius: 50%; margin-bottom: 20px; }
.popup-box h3 { font-size: 1.4rem; font-weight: 800; color: #0f172a; margin-bottom: 12px; }
.popup-box p { font-size: 0.95rem; color: #64748b; margin-bottom: 32px; }
.popup-actions { display: flex; gap: 12px; }
.btn-cancel, .btn-delete { flex: 1; padding: 14px; border-radius: 100px; font-weight: 700; cursor: pointer; border: none; }
.btn-cancel { background: #f1f5f9; color: #475569; }
.btn-delete { background: #ea580c; color: #fff; }

/* Empty state */
.empty-state { text-align: center; padding: 80px 40px; }
.empty-icon { font-size: 3rem; margin-bottom: 20px; opacity: 0.5; }
.btn-explore { 
  background: #0f172a; color: #fff; border: none; padding: 14px 32px; 
  border-radius: 100px; font-weight: 700; cursor: pointer; transition: 0.3s; 
}
.btn-explore:hover { background: #ea580c; }

/* Animations */
.fade-up { animation: fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.fade-scale { animation: fadeScale 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
@keyframes fadeScale { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }
</style>