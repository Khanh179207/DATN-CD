<template>
  <div class="search-page-wrapper">
    
    <section class="hero-depth">
      <div class="blob blob-1"></div>
      <div class="blob blob-2"></div>
      
      <div class="hero-content">
        <span class="hero-subtitle">Bộ Sưu Tập Premium</span>
        <h1 class="hero-title">Đánh Thức <br> <span class="highlight">Vị Giác Của Bạn</span></h1>
        <p class="hero-text">
          Hơn <strong>{{ totalResults }}+</strong> công thức độc quyền đang chờ được khám phá.
        </p>
        
        <div class="trending-pills">
          <span class="label">Xu hướng:</span>
          <div class="pills-scroll">
            <button class="pill-item" @click="quickFilter('healthy')">🥑 Eat Clean</button>
            <button class="pill-item" @click="quickFilter('vietnam')">🇻🇳 Món Việt</button>
            <button class="pill-item" @click="quickFilter('cake')">🍰 Bánh Ngọt</button>
            <button class="pill-item" @click="quickFilter('drink')">🍹 Detox</button>
          </div>
        </div>
      </div>
    </section>

    <div class="main-body-container">
      
      <div class="sticky-filter-glass" :class="{ 'is-stuck': isStuck }">
        <div class="filter-left">
          <button 
            v-for="cat in categories" 
            :key="cat.id"
            class="tab-link" 
            :class="{ active: filters.category === cat.id }"
            @click="filters.category = cat.id"
          >
            {{ cat.name }}
          </button>
        </div>

        <div class="filter-right">
          <button class="btn-filter-trigger" @click="showAdvanced = !showAdvanced" :class="{ active: showAdvanced }">
            <span class="icon-filter"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="4" y1="21" x2="4" y2="14"></line><line x1="4" y1="10" x2="4" y2="3"></line><line x1="12" y1="21" x2="12" y2="12"></line><line x1="12" y1="8" x2="12" y2="3"></line><line x1="20" y1="21" x2="20" y2="16"></line><line x1="20" y1="12" x2="20" y2="3"></line><line x1="1" y1="14" x2="7" y2="14"></line><line x1="9" y1="8" x2="15" y2="8"></line><line x1="17" y1="16" x2="23" y2="16"></line></svg></span>
            Bộ Lọc
          </button>
          <div class="sort-select-wrapper">
            <select v-model="filters.sort">
              <option value="newest">Mới nhất</option>
              <option value="popular">Phổ biến</option>
              <option value="rating">Đánh giá cao</option>
            </select>
          </div>
        </div>
      </div>

      <transition name="expand">
        <div v-if="showAdvanced" class="advanced-panel-depth">
          <div class="panel-grid">
            <div class="filter-col">
              <h4>Độ khó</h4>
              <div class="chip-group">
                <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value=""><span>Tất cả</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Dễ"><span>Dễ</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Trung bình"><span>Trung bình</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Khó"><span>Khó</span></label>
              </div>
            </div>
            <div class="filter-col">
              <h4>Thời gian</h4>
              <div class="chip-group">
                <label class="radio-chip"><input type="radio" v-model="filters.time" value=""><span>Tất cả</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.time" value="short"><span>&lt; 30p</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.time" value="medium"><span>30-60p</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.time" value="long"><span>&gt; 60p</span></label>
              </div>
            </div>
            <div class="filter-col reset-area">
              <button class="btn-reset-text" @click="resetFilters">Xóa bộ lọc</button>
            </div>
          </div>
        </div>
      </transition>

      <div class="results-grid-container">
        <transition-group name="staggered-fade" tag="div" class="grid-layout">
          <RecipeCard 
            v-for="(post, index) in paginatedPosts" 
            :key="post.id" 
            :post="post"
            class="grid-item"
            :style="{ '--delay': index * 0.08 + 's' }" 
            @click="goToDetail(post.id)"
            @save="handleSave(post.id)"
          />
        </transition-group>

        <div v-if="paginatedPosts.length === 0" class="empty-state-depth">
          <div class="icon-3d">🍳</div>
          <h3>Không tìm thấy công thức</h3>
          <p>Hãy thử từ khóa khác xem sao nhé!</p>
          <button class="btn-back-all" @click="resetFilters">Xem tất cả</button>
        </div>
      </div>

      <div class="pagination-floating" v-if="totalPages > 1">
        <button class="page-nav" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">←</button>
        <div class="page-dots">
          <span 
            v-for="page in visiblePages" 
            :key="page" 
            class="dot" 
            :class="{ active: currentPage === page, spacer: page === '...' }"
            @click="goToPage(page)"
          >
            {{ page === '...' ? '•' : page }}
          </span>
        </div>
        <button class="page-nav" :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)">→</button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import RecipeCard from '@/components/common/RecipeCard.vue'

const router = useRouter()
const route = useRoute()

// State
const showAdvanced = ref(false)
const currentPage = ref(1)
const itemsPerPage = 8
const isStuck = ref(false)

// Config Categories
const categories = [
  { id: 'all', name: 'Tất cả' },
  { id: 'món chính', name: 'Món Chính' },
  { id: 'ăn sáng', name: 'Ăn Sáng' },
  { id: 'tráng miệng', name: 'Tráng Miệng' },
  { id: 'healthy', name: 'Healthy' },
  { id: 'đồ uống', name: 'Đồ Uống' },
]

// Data
const allPosts = ref([
  { id: 1, title: 'Bò Beefsteak Sốt Tiêu Đen Chuẩn Vị Âu', image: 'https://images.unsplash.com/photo-1600891964092-4316c288032e?w=600&q=80', category: 'Món Chính', time: '45p', difficulty: 'Trung bình', author: 'Chef Ramsay', likes: 120 },
  { id: 2, title: 'Salad Cầu Vồng Ức Gà Sốt Mè Rang', image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&q=80', category: 'Healthy', time: '15p', difficulty: 'Dễ', author: 'Eat Clean', likes: 89 },
  { id: 3, title: 'Bánh Mousse Chanh Leo Mềm Mịn', image: 'https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=600&q=80', category: 'Tráng Miệng', time: '60p', difficulty: 'Khó', author: 'Bếp Bánh', likes: 210 },
  { id: 4, title: 'Mỳ Ý Spaghetti Carbonara Truyền Thống', image: 'https://images.unsplash.com/photo-1612874742237-98280d207487?w=600&q=80', category: 'Món Chính', time: '30p', difficulty: 'Trung bình', author: 'Luca', likes: 156 },
  { id: 5, title: 'Sinh Tố Bơ Chuối Hạt Chia', image: 'https://images.unsplash.com/photo-1577805947697-89e18249d767?w=600&q=80', category: 'Đồ Uống', time: '10p', difficulty: 'Dễ', author: 'Green Life', likes: 95 },
  { id: 6, title: 'Cơm Tấm Sườn Bì Chả Sài Gòn', image: 'https://images.unsplash.com/photo-1595295333158-4742f28fbd85?w=600&q=80', category: 'Món Chính', time: '50p', difficulty: 'Trung bình', author: 'SaiGon Food', likes: 300 },
  { id: 7, title: 'Phở Bò Tái Nạm Gầu Gân', image: 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=600&q=80', category: 'Ăn Sáng', time: '120p', difficulty: 'Khó', author: 'Pho King', likes: 500 },
  { id: 8, title: 'Gỏi Cuốn Tôm Thịt Chấm Tương Đen', image: 'https://images.unsplash.com/photo-1530260626688-d48233003fe9?w=600&q=80', category: 'Ăn Sáng', time: '20p', difficulty: 'Dễ', author: 'Rolls', likes: 110 },
  { id: 9, title: 'Bánh Mì Pate Chả Lụa Giòn Rụm', image: 'https://images.unsplash.com/photo-1549488391-584332296305?w=600&q=80', category: 'Ăn Sáng', time: '10p', difficulty: 'Dễ', author: 'Banh Mi', likes: 150 },
  { id: 10, title: 'Canh Chua Cá Lóc Miền Tây', image: 'https://images.unsplash.com/photo-1604908177453-7462950a6a3b?w=600&q=80', category: 'Món Chính', time: '35p', difficulty: 'Trung bình', author: 'Mẹ Nấu', likes: 140 },
  { id: 11, title: 'Bánh Xèo Tôm Nhảy Bình Định', image: 'https://images.unsplash.com/photo-1519624029237-7c0ac7297e68?w=600&q=80', category: 'Món Chính', time: '40p', difficulty: 'Trung bình', author: 'Binh Dinh', likes: 180 },
  { id: 12, title: 'Lẩu Thái Tom Yum Chua Cay', image: 'https://images.unsplash.com/photo-1546241072-48010ad2862c?w=600&q=80', category: 'Món Chính', time: '45p', difficulty: 'Trung bình', author: 'Thai Food', likes: 220 },
  { id: 13, title: 'Chè Khúc Bạch Hạnh Nhân', image: 'https://images.unsplash.com/photo-1623065422902-30a2d299bbe4?w=600&q=80', category: 'Tráng Miệng', time: '60p', difficulty: 'Trung bình', author: 'Sweet', likes: 130 },
  { id: 14, title: 'Pizza Hải Sản Phô Mai Mozzarella', image: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=600&q=80', category: 'Món Chính', time: '45p', difficulty: 'Khó', author: 'Pizza Home', likes: 310 },
  { id: 15, title: 'Sushi Cá Hồi & Sashimi Tươi Sống', image: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=600&q=80', category: 'Món Chính', time: '50p', difficulty: 'Khó', author: 'Japan Food', likes: 280 },
  { id: 16, title: 'Trà Đào Cam Sả Mát Lạnh', image: 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600&q=80', category: 'Đồ Uống', time: '15p', difficulty: 'Dễ', author: 'Highland', likes: 170 }
])

const filters = ref({ category: 'all', difficulty: '', time: '', sort: 'newest' })

// Computed Logic
const filteredResults = computed(() => {
  let result = allPosts.value
  if (filters.value.category !== 'all') result = result.filter(p => p.category.toLowerCase().includes(filters.value.category.toLowerCase()))
  if (filters.value.difficulty) result = result.filter(p => p.difficulty === filters.value.difficulty)
  if (filters.value.time === 'short') result = result.filter(p => parseInt(p.time) <= 30)
  if (filters.value.time === 'medium') result = result.filter(p => parseInt(p.time) > 30 && parseInt(p.time) <= 60)
  if (filters.value.time === 'long') result = result.filter(p => parseInt(p.time) > 60)
  if (route.query.q) result = result.filter(p => p.title.toLowerCase().includes(route.query.q.toLowerCase()))
  if (filters.value.sort === 'newest') result.sort((a, b) => b.id - a.id)
  if (filters.value.sort === 'popular') result.sort((a, b) => b.likes - a.likes)
  return result
})

const totalResults = computed(() => filteredResults.value.length)
const totalPages = computed(() => Math.ceil(totalResults.value / itemsPerPage))
const paginatedPosts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return filteredResults.value.slice(start, start + itemsPerPage)
})

const visiblePages = computed(() => {
  const pages = []
  if (totalPages.value <= 7) {
    for (let i = 1; i <= totalPages.value; i++) pages.push(i)
  } else {
    pages.push(1)
    if (currentPage.value > 3) pages.push('...')
    let start = Math.max(2, currentPage.value - 1)
    let end = Math.min(totalPages.value - 1, currentPage.value + 1)
    for (let i = start; i <= end; i++) pages.push(i)
    if (currentPage.value < totalPages.value - 2) pages.push('...')
    pages.push(totalPages.value)
  }
  return pages
})

// Actions
const quickFilter = (cat) => filters.value.category = cat
const resetFilters = () => { filters.value = { category: 'all', difficulty: '', time: '', sort: 'newest' }; router.push({ query: {} }) }
const goToPage = (p) => { if (p !== '...') { currentPage.value = p; window.scrollTo({ top: 400, behavior: 'smooth' }) } }
const goToDetail = (id) => router.push(`/home/post/${id}`)
const handleSave = (id) => console.log('Saved', id)

// Hooks
watch(filters, () => { currentPage.value = 1 }, { deep: true })
watch(() => route.query.q, () => { currentPage.value = 1 })
onMounted(() => { window.addEventListener('scroll', () => { isStuck.value = window.scrollY > 350 }) })
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;500;600;700;800&family=Playfair+Display:wght@700;800&display=swap');

.search-page-container { width: 100%; min-height: 100vh; background-color: #FAFAF9; font-family: 'Mulish', sans-serif; }

/* 1. HERO DEPTH (AURORA) */
.hero-depth {
  position: relative; padding: 100px 20px 140px; text-align: center;
  background: #FFF; overflow: hidden;
}
/* Hiệu ứng Blob mờ ảo nền */
.blob { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.6; z-index: 0; animation: float 10s infinite alternate; }
.blob-1 { width: 300px; height: 300px; background: #FFEDD5; top: -50px; left: -50px; }
.blob-2 { width: 400px; height: 400px; background: #E0F2FE; bottom: -100px; right: -50px; animation-delay: 5s; }
@keyframes float { 0% { transform: translate(0, 0); } 100% { transform: translate(30px, 50px); } }

.hero-content { position: relative; z-index: 2; max-width: 800px; margin: 0 auto; }
.hero-subtitle { font-size: 0.85rem; letter-spacing: 3px; text-transform: uppercase; color: #EA580C; font-weight: 800; display: block; margin-bottom: 20px; }
.hero-title { font-family: 'Playfair Display', serif; font-size: 4.5rem; line-height: 1.1; color: #1C1917; margin-bottom: 25px; }
.hero-title .highlight { background: linear-gradient(120deg, transparent 0%, transparent 60%, #FED7AA 60%, #FED7AA 100%); }
.hero-text { font-size: 1.25rem; color: #57534E; line-height: 1.6; margin-bottom: 50px; }

/* Trending Pills */
.trending-pills { display: flex; flex-direction: column; align-items: center; gap: 10px; }
.trending-pills .label { font-size: 0.85rem; color: #A8A29E; font-weight: 700; text-transform: uppercase; letter-spacing: 1px; }
.pills-scroll { display: flex; gap: 12px; flex-wrap: wrap; justify-content: center; }
.pill-item {
  background: rgba(255,255,255,0.8); border: 1px solid #E7E5E4; backdrop-filter: blur(4px);
  padding: 10px 24px; border-radius: 50px; font-size: 0.95rem; font-weight: 700; color: #44403C;
  cursor: pointer; transition: all 0.3s ease; box-shadow: 0 4px 10px rgba(0,0,0,0.03);
}
.pill-item:hover { transform: translateY(-3px); border-color: #EA580C; color: #EA580C; box-shadow: 0 10px 20px rgba(234, 88, 12, 0.15); }

/* 2. MAIN BODY */
.main-body-container { max-width: 1240px; margin: -60px auto 0; padding: 0 20px 100px; position: relative; z-index: 10; }

/* STICKY FILTER GLASS */
.sticky-filter-glass {
  background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.5); border-radius: 20px; padding: 15px 25px;
  display: flex; justify-content: space-between; align-items: center;
  box-shadow: 0 20px 40px -10px rgba(0,0,0,0.05); margin-bottom: 50px;
  transition: all 0.3s ease;
}
.sticky-filter-glass.is-stuck { position: sticky; top: 20px; box-shadow: 0 20px 50px -10px rgba(0,0,0,0.1); border-color: #E5E5E5; }

.filter-left { display: flex; gap: 30px; overflow-x: auto; padding-bottom: 5px; scrollbar-width: none; }
.tab-link {
  background: none; border: none; font-size: 1rem; font-weight: 600; color: #78716C;
  cursor: pointer; padding: 5px 0; position: relative; white-space: nowrap; transition: 0.3s;
}
.tab-link:hover { color: #1C1917; }
.tab-link.active { color: #1C1917; font-weight: 800; }
.tab-link.active::after { content: ''; position: absolute; bottom: 0; left: 0; right: 0; height: 3px; background: #EA580C; border-radius: 3px; }

.filter-right { display: flex; align-items: center; gap: 20px; }
.btn-filter-trigger {
  display: flex; align-items: center; gap: 8px; padding: 10px 20px; border-radius: 12px;
  background: #F5F5F4; border: none; font-weight: 700; color: #44403C; cursor: pointer; transition: 0.3s;
}
.btn-filter-trigger:hover, .btn-filter-trigger.active { background: #1C1917; color: white; }
.sort-select-wrapper select { padding: 10px; border-radius: 12px; border: 1px solid #E7E5E4; background: white; font-weight: 600; cursor: pointer; outline: none; }

/* ADVANCED PANEL */
.advanced-panel-depth {
  background: white; border-radius: 20px; margin-top: -30px; margin-bottom: 50px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.05); overflow: hidden; border: 1px solid #F3F4F6;
}
.panel-grid { display: grid; grid-template-columns: 1fr 1fr auto; gap: 40px; padding: 40px; }
.filter-col h4 { font-family: 'Playfair Display', serif; font-size: 1.2rem; margin: 0 0 15px; color: #1C1917; }
.chip-group { display: flex; flex-wrap: wrap; gap: 10px; }
.radio-chip { cursor: pointer; }
.radio-chip input { display: none; }
.radio-chip span {
  display: inline-block; padding: 8px 16px; border-radius: 20px; border: 1px solid #E7E5E4;
  font-size: 0.9rem; color: #57534E; transition: 0.2s; background: white;
}
.radio-chip input:checked + span { background: #FFF7ED; border-color: #FED7AA; color: #C2410C; font-weight: 700; }
.btn-reset-text { background: none; border: none; text-decoration: underline; color: #78716C; cursor: pointer; font-weight: 600; transition: 0.2s; }
.btn-reset-text:hover { color: #EF4444; }

/* RESULTS GRID */
.results-grid-container { min-height: 400px; }
.grid-layout { display: grid; grid-template-columns: repeat(4, 1fr); gap: 30px; }

/* Pagination Floating */
.pagination-floating {
  display: inline-flex; align-items: center; gap: 10px; background: white;
  padding: 10px 20px; border-radius: 50px; box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  position: absolute; bottom: 0; left: 50%; transform: translate(-50%, 50%);
  border: 1px solid #F3F4F6;
}
.page-nav { width: 40px; height: 40px; border-radius: 50%; border: 1px solid #E5E5E5; background: white; cursor: pointer; font-weight: 700; transition: 0.2s; }
.page-nav:hover:not(:disabled) { border-color: #EA580C; color: #EA580C; }
.page-dots { display: flex; gap: 8px; }
.dot { width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; border-radius: 50%; cursor: pointer; font-weight: 600; color: #57534E; transition: 0.2s; }
.dot.active { background: #1C1917; color: white; }
.dot:hover:not(.active):not(.spacer) { background: #F5F5F4; }

/* Transitions */
.grid-item { opacity: 0; animation: fadeInUp 0.6s ease-out forwards; animation-delay: var(--delay); }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
.expand-enter-active, .expand-leave-active { transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); max-height: 500px; }
.expand-enter-from, .expand-leave-to { max-height: 0; opacity: 0; }

/* Responsive */
@media (max-width: 1024px) {
  .hero-title { font-size: 3rem; }
  .grid-layout { grid-template-columns: repeat(2, 1fr); }
  .panel-grid { grid-template-columns: 1fr; gap: 20px; }
}
@media (max-width: 640px) {
  .grid-layout { grid-template-columns: 1fr; }
  .sticky-filter-glass { flex-direction: column; align-items: flex-start; gap: 15px; }
  .filter-right { width: 100%; justify-content: space-between; }
  .filter-left { width: 100%; padding-bottom: 10px; border-bottom: 1px solid #F3F4F6; }
}
</style>