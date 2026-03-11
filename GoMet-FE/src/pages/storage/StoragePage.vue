<template>
  <div class="gomet-collection hide-scrollbar">
    
    <div class="bg-layer">
      <div class="grid-mesh"></div>
      <div class="noise-texture"></div>
    </div>

    <div class="collection-wrapper">
      
      <aside class="collection-sidebar">
        <div class="sidebar-sticky">
          
          <div class="profile-summary">
            <div class="avatar-ring">
              <img v-if="authStore.user?.avatar" :src="authStore.user.avatar" class="user-avt" />
              <span v-else class="user-initial">{{ (authStore.user?.name || 'U').charAt(0).toUpperCase() }}</span>
            </div>
            <div class="user-text">
              <span class="u-label">{{ $t('storage.owner') }}</span>
              <span class="u-name">{{ authStore.user?.name || 'Guest' }}</span>
            </div>
          </div>

          <div class="divider"></div>

          <div class="filter-group">
            <h3 class="group-title">{{ $t('storage.categories') }}</h3>
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

          <div class="stats-box">
            <div class="stat-row">
              <span>{{ $t('storage.stats_saved') }}</span>
              <strong>{{ savedPosts.length }}</strong>
            </div>
            <div class="stat-row">
              <span>{{ $t('storage.stats_recent') }}</span>
              <strong>+2</strong>
            </div>
          </div>

        </div>
      </aside>

      <main class="collection-content">
        
        <header class="collection-header">
          <div class="header-left">
            <div class="brand-tag">GOMET ARCHIVE /// V.3.0</div>
            <h1 class="main-title">
              {{ $t('storage.title').split(' ').slice(0, 2).join(' ') }}
              <span class="text-serif">
                {{ $t('storage.title').split(' ').slice(2).join(' ') }}
              </span>
            </h1>
          </div>
          
          <div class="header-right">
            <div class="search-bar">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
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
          @unsaved="removeCard"
          />

        </div>

        <div v-else class="empty-state">
          <div class="empty-icon">📂</div>
          <h3>{{ $t('common.no_data') }}</h3>
          <p>No recipes saved in this category yet.</p>
          <button class="btn-explore" @click="$router.push('/search')">
            EXPLORE NOW
          </button>
        </div>

      </main>
    </div>

    <!-- Confirm Unsave Popup (MOVED INSIDE ROOT) -->
    <div v-if="showConfirm" class="popup-overlay">
      <div class="popup-box">
        
        <h3>{{ $t('storage.confirm_unsave') }}</h3>
        <p>{{ $t('storage.confirm_unsave_desc') }}</p>

        <div class="popup-actions">
          <button class="btn-cancel" @click="showConfirm = false">
            {{ $t('common.cancel') }}
          </button>

          <button class="btn-delete" @click="confirmUnsave">
            {{ $t('storage.unsave') }}
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

const normalizePost = (fav) => {
  return {
    id: fav.postID,

    title: fav.title ?? 'No title',

    category: fav.categoryName ?? 'Uncategorized',

    image:
      fav.media ||
      'https://images.unsplash.com/photo-1546069901-ba9599a7e63c',

    time: fav.cookingTime ? `${fav.cookingTime} min` : '30 min',

    rating: fav.rating ?? 4.5,

    reviews: fav.reviewCount ?? 0,

    likes: fav.likeCount ?? 0,

    author: {
      name: fav.userName ?? "Gomet Chef",
      avatar: fav.avatar ?? null
    },

    difficulty: levelMap[fav.level] ?? 'Easy',

    savedDate: new Date().toLocaleDateString()
  }
}

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    loading.value = false
    return
  }

  try {
    const data = await getFavorites(authStore.user.accountID)

    console.log("Favorites API:", data)

    savedPosts.value = Array.isArray(data)
      ? data.map(normalizePost).filter(Boolean)
      : []

  } catch (err) {
    console.warn('StoragePage: load error', err)
    toast.warn(t('toast.load_error'))
  } finally {
    loading.value = false
  }
})

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


const showConfirm = ref(false)

const selectedPost = ref(null)

const removeCard = (postId) => {
  savedPosts.value = savedPosts.value.filter(
    p => p.id !== postId
  )
}
</script>

<style scoped>
/* --- 1. CORE LAYOUT (Synced with Planner/Arcade) --- */
.gomet-collection {
  width: 100%; min-height: 100vh; background: #FBF6F1; color: #1E293B;
  font-family: 'Manrope', sans-serif; position: relative; overflow-x: hidden;
}

/* Background */
.bg-layer { position: absolute; inset: 0; pointer-events: none; z-index: 0; }
.grid-mesh {
  position: absolute; inset: 0;
  background-image: linear-gradient(rgba(0,0,0,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(0,0,0,0.03) 1px, transparent 1px);
  background-size: 40px 40px;
}
.noise-texture {
  position: absolute; inset: 0; opacity: 0.03;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
}

/* Wrapper */
.collection-wrapper {
  display: flex; width: 100%; max-width: 1800px; margin: 0 auto; padding: 30px; gap: 40px;
  position: relative; z-index: 10;
}

/* --- 2. SIDEBAR (Digital style) --- */
.collection-sidebar { width: 260px; flex-shrink: 0; }
.sidebar-sticky { position: sticky; top: 30px; display: flex; flex-direction: column; gap: 30px; }

/* Profile Card */
.profile-summary { display: flex; align-items: center; gap: 15px; }
.avatar-ring {
  width: 48px; height: 48px; border-radius: 50%; border: 2px solid #EA580C;
  display: flex; align-items: center; justify-content: center; background: #FFF;
}
.user-initial { font-family: 'Playfair Display', serif; font-size: 1.5rem; font-weight: 700; color: #EA580C; }
.user-avt { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; }
.user-text { display: flex; flex-direction: column; }
.u-label { font-size: 0.6rem; font-weight: 800; color: #94A3B8; letter-spacing: 1px; }
.u-name { font-size: 1rem; font-weight: 800; color: #1E293B; }

.divider { height: 1px; background: rgba(0,0,0,0.05); width: 100%; }

/* Filters */
.filter-group { display: flex; flex-direction: column; gap: 8px; }
.group-title { font-size: 0.7rem; font-weight: 800; color: #CBD5E1; margin-bottom: 10px; letter-spacing: 2px; }

.btn-filter {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 16px; border: 1px solid transparent; background: transparent;
  border-radius: 10px; cursor: pointer; transition: 0.2s;
}
.filter-name { font-size: 0.9rem; font-weight: 600; color: #64748B; }
.filter-count { font-size: 0.75rem; font-weight: 700; color: #CBD5E1; background: #F1F5F9; padding: 2px 8px; border-radius: 10px; }

.btn-filter:hover { background: #FFF; box-shadow: 0 4px 10px rgba(0,0,0,0.02); }
.btn-filter.active { background: #FFF; border-color: #EA580C; box-shadow: 0 4px 15px rgba(234,88,12,0.1); }
.btn-filter.active .filter-name { color: #EA580C; }
.btn-filter.active .filter-count { background: #EA580C; color: #FFF; }

/* Stats Box */
.stats-box { background: #FFF; padding: 20px; border-radius: 16px; border: 1px solid rgba(0,0,0,0.05); }
.stat-row { display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 0.85rem; color: #64748B; }
.stat-row:last-child { margin-bottom: 0; }
.stat-row strong { color: #1E293B; }

/* --- 3. MAIN CONTENT --- */
.collection-content { flex: 1; display: flex; flex-direction: column; }

/* Header */
.collection-header {
  display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 40px;
  background: #FFF; padding: 25px 35px; border-radius: 20px; border: 1px solid rgba(0,0,0,0.05);
}
.brand-tag { font-size: 0.7rem; font-weight: 800; color: #EA580C; letter-spacing: 2px; margin-bottom: 5px; }
.main-title { font-family: 'Manrope', sans-serif; font-size: 2.2rem; font-weight: 800; color: #1E293B; margin: 0; line-height: 1; }
.text-serif { font-family: 'Playfair Display', serif; font-style: italic; color: #EA580C; }

.search-bar {
  display: flex; align-items: center; gap: 10px; background: #F8FAFC; padding: 10px 20px; border-radius: 50px; border: 1px solid #E2E8F0; width: 300px;
}
.search-bar input { border: none; background: transparent; outline: none; font-size: 0.9rem; color: #1E293B; width: 100%; font-family: 'Manrope', sans-serif; }
.search-bar svg { color: #94A3B8; }

/* Gallery Grid */
.gallery-grid{
  display:grid;
  grid-template-columns:repeat(auto-fill,minmax(300px,1fr));
  gap:24px;
}

.archive-card:hover .card-visual img { transform: scale(1.05); }
.visual-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.6), transparent); padding: 15px; display: flex; align-items: flex-end; }
.cat-tag { background: #EA580C; color: #FFF; font-size: 0.65rem; font-weight: 700; padding: 4px 10px; border-radius: 4px; text-transform: uppercase; }

.card-body { padding: 20px; display: flex; flex-direction: column; flex: 1; }
.date-saved { font-size: 0.7rem; color: #94A3B8; font-weight: 600; }

/* Empty State */
.empty-state { text-align: center; padding: 80px 0; color: #94A3B8; }
.empty-icon { font-size: 3rem; margin-bottom: 20px; opacity: 0.5; }
.btn-explore { margin-top: 20px; background: #1E293B; color: #FFF; border: none; padding: 12px 30px; border-radius: 8px; font-weight: 700; cursor: pointer; transition: 0.3s; }
.btn-explore:hover { background: #EA580C; }

/* Responsive */
@media (max-width: 1024px) {
  .collection-wrapper { flex-direction: column; }
  .collection-sidebar { width: 100%; }
  .sidebar-sticky { flex-direction: row; flex-wrap: wrap; align-items: center; }
  .filter-group { flex: 1; display: flex; flex-direction: row; overflow-x: auto; padding-bottom: 5px; }
  .btn-filter { flex-shrink: 0; }
  .stats-box { display: none; }
}

.popup-overlay{
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.35);
  display:flex;
  align-items:center;
  justify-content:center;
  z-index:1000;
}

.popup-box{
  background:#fff;
  padding:30px;
  border-radius:16px;
  width:320px;
  text-align:center;
  box-shadow:0 20px 40px rgba(0,0,0,0.15);
}

.popup-box h3{
  margin-bottom:10px;
  font-size:1.2rem;
}

.popup-box p{
  font-size:0.9rem;
  color:#64748B;
  margin-bottom:20px;
}

.popup-actions{
  display:flex;
  gap:10px;
  justify-content:center;
}

.btn-cancel{
  background:#E2E8F0;
  border:none;
  padding:10px 16px;
  border-radius:8px;
  cursor:pointer;
}

.btn-delete{
  background:#EA580C;
  color:#fff;
  border:none;
  padding:10px 16px;
  border-radius:8px;
  cursor:pointer;
}

.btn-delete:hover{
  background:#c2410c;
}
</style>