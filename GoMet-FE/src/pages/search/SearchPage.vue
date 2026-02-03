<template>
  <div class="search-page-container">
    
    <div class="search-header-area">
      <div class="header-left">
        <h2 class="page-title">Khám Phá Công Thức</h2>
        <p class="sub-text">Tìm thấy <strong>{{ totalResults }}</strong> kết quả phù hợp</p>
      </div>
      <button class="btn-toggle-filter" @click="showAdvanced = !showAdvanced" :class="{ active: showAdvanced }">
        <span class="icon">⚡</span>
        {{ showAdvanced ? 'Ẩn bộ lọc' : 'Bộ lọc nâng cao' }}
      </button>
    </div>

    <transition name="slide-fade">
      <div v-if="showAdvanced" class="advanced-filter-panel">
        <div class="filter-grid">
          <div class="filter-group">
            <label>Độ khó</label>
            <select v-model="filters.difficulty">
              <option value="">Tất cả</option>
              <option value="easy">Dễ làm</option>
              <option value="medium">Trung bình</option>
              <option value="hard">Cầu kỳ</option>
            </select>
          </div>

          <div class="filter-group">
            <label>Thời gian</label>
            <select v-model="filters.time">
              <option value="">Tất cả</option>
              <option value="short">Dưới 30 phút</option>
              <option value="medium">30 - 60 phút</option>
              <option value="long">Trên 1 tiếng</option>
            </select>
          </div>

          <div class="filter-group">
            <label>Sắp xếp theo</label>
            <select v-model="filters.sort">
              <option value="newest">Mới nhất</option>
              <option value="popular">Phổ biến nhất</option>
              <option value="rating">Đánh giá cao</option>
            </select>
          </div>

          <div class="filter-group reset-box">
            <label>&nbsp;</label>
            <button class="btn-reset" @click="resetFilters">Đặt lại</button>
          </div>
        </div>
      </div>
    </transition>

    <div class="filter-sticky">
      <div class="filter-scroll">
        <button 
          class="chip" 
          :class="{ active: filters.category === 'all' }" 
          @click="filters.category = 'all'"
        >🔥 Tất cả</button>
        <button class="chip" :class="{ active: filters.category === 'healthy' }" @click="filters.category = 'healthy'">🥬 Eat Clean</button>
        <button class="chip" :class="{ active: filters.category === 'noodle' }" @click="filters.category = 'noodle'">🍜 Món nước</button>
        <button class="chip" :class="{ active: filters.category === 'cake' }" @click="filters.category = 'cake'">🍰 Bánh ngọt</button>
        <button class="chip" :class="{ active: filters.category === 'vegan' }" @click="filters.category = 'vegan'">🥗 Đồ chay</button>
        <button class="chip" :class="{ active: filters.category === 'vietnam' }" @click="filters.category = 'vietnam'">🇻🇳 Món Việt</button>
      </div>
    </div>

    <section class="posts-section">
      <transition-group name="list" tag="div" class="grid-layout">
        <div class="post-card" v-for="post in paginatedPosts" :key="post.id" @click="goToDetail(post.id)">
          
          <div class="card-thumb">
            <img :src="post.image" loading="lazy" :alt="post.title" />
            <div class="overlay-gradient"></div>
            <span class="category-tag">{{ post.category }}</span>
            <button class="btn-action save-btn" @click.stop title="Lưu">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path></svg>
            </button>
          </div>

          <div class="card-content">
            <h4 class="post-title">{{ post.title }}</h4>
            <div class="meta-tags">
              <span class="meta-pill">⏱️ {{ post.time }}p</span>
              <span class="meta-pill">🔥 {{ post.calories }} Kcal</span>
            </div>
            
            <div class="difficulty-bar">
              <span class="label">Độ khó:</span>
              <div class="dots">
                <span class="dot" :class="{ active: true }"></span>
                <span class="dot" :class="{ active: post.difficulty !== 'easy' }"></span>
                <span class="dot" :class="{ active: post.difficulty === 'hard' }"></span>
              </div>
            </div>

            <div class="card-footer">
              <div class="author-box">
                <div class="avt-ring"><img :src="post.authorAvatar" class="avt-tiny"></div>
                <span class="author-name">{{ post.author }}</span>
              </div>
              <div class="like-box">❤️ <span>{{ post.likes }}</span></div>
            </div>
          </div>

        </div>
      </transition-group>

      <div v-if="paginatedPosts.length === 0" class="empty-state">
        <div class="empty-icon">🍽️</div>
        <h3>Không tìm thấy công thức nào</h3>
        <p>Hãy thử thay đổi bộ lọc hoặc từ khóa tìm kiếm nhé!</p>
      </div>
    </section>

    <div class="pagination-container" v-if="totalPages > 1">
      <button 
        class="page-btn prev" 
        :disabled="currentPage === 1" 
        @click="currentPage--"
      >
        ←
      </button>
      
      <div class="page-numbers">
        <button 
          v-for="page in totalPages" 
          :key="page"
          class="page-num" 
          :class="{ active: currentPage === page }"
          @click="currentPage = page"
        >
          {{ page }}
        </button>
      </div>

      <button 
        class="page-btn next" 
        :disabled="currentPage === totalPages" 
        @click="currentPage++"
      >
        →
      </button>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 1. State
const showAdvanced = ref(false)
const currentPage = ref(1)
const itemsPerPage = 8

// 2. Filters State
const filters = ref({
  category: 'all',
  difficulty: '',
  time: '',
  sort: 'newest'
})

// 3. Mock Data (Nhiều hơn để test phân trang)
const allPosts = ref([
  { id: 1, title: 'Sườn Heo Nấu Khoai Tây', image: 'https://images.unsplash.com/photo-1544025162-d76694265947?w=600&q=80', category: 'Món Mặn', time: 45, calories: 650, difficulty: 'medium', author: 'Rose', authorAvatar: 'https://i.pravatar.cc/150?u=1', likes: 125 },
  { id: 2, title: 'Salad Cầu Vồng Ức Gà', image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&q=80', category: 'Healthy', time: 15, calories: 320, difficulty: 'easy', author: 'Eat Clean', authorAvatar: 'https://i.pravatar.cc/150?u=2', likes: 89 },
  { id: 3, title: 'Bánh Mousse Chanh Leo', image: 'https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=600&q=80', category: 'cake', time: 60, calories: 450, difficulty: 'hard', author: 'Bếp Bánh', authorAvatar: 'https://i.pravatar.cc/150?u=3', likes: 210 },
  { id: 4, title: 'Mỳ Ý Sốt Kem Nấm', image: 'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=600&q=80', category: 'Món Âu', time: 30, calories: 580, difficulty: 'medium', author: 'Chef Hung', authorAvatar: 'https://i.pravatar.cc/150?u=4', likes: 156 },
  { id: 5, title: 'Sinh Tố Bơ Chuối', image: 'https://images.unsplash.com/photo-1577805947697-89e18249d767?w=600&q=80', category: 'Đồ Uống', time: 10, calories: 220, difficulty: 'easy', author: 'Healthy', authorAvatar: 'https://i.pravatar.cc/150?u=5', likes: 95 },
  { id: 6, title: 'Cơm Chiên Dương Châu', image: 'https://images.unsplash.com/photo-1603133872878-684f5714302e?w=600&q=80', category: 'Cơm', time: 25, calories: 510, difficulty: 'medium', author: 'Mẹ Nấu', authorAvatar: 'https://i.pravatar.cc/150?u=6', likes: 180 },
  // Duplicate data để test phân trang
  { id: 7, title: 'Bún Bò Huế', image: 'https://images.unsplash.com/photo-1587314168485-3236d6710814?w=600&q=80', category: 'noodle', time: 90, calories: 700, difficulty: 'hard', author: 'Hue Food', authorAvatar: 'https://i.pravatar.cc/150?u=7', likes: 300 },
  { id: 8, title: 'Phở Bò Nam Định', image: 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=600&q=80', category: 'vietnam', time: 120, calories: 600, difficulty: 'hard', author: 'Pho King', authorAvatar: 'https://i.pravatar.cc/150?u=8', likes: 500 },
  { id: 9, title: 'Gỏi Cuốn Tôm Thịt', image: 'https://images.unsplash.com/photo-1530260626688-d48233003fe9?w=600&q=80', category: 'vietnam', time: 20, calories: 250, difficulty: 'easy', author: 'Rolls', authorAvatar: 'https://i.pravatar.cc/150?u=9', likes: 110 },
  { id: 10, title: 'Bánh Mì Pate', image: 'https://images.unsplash.com/photo-1549488391-584332296305?w=600&q=80', category: 'vietnam', time: 10, calories: 400, difficulty: 'easy', author: 'Street Food', authorAvatar: 'https://i.pravatar.cc/150?u=10', likes: 150 },
])

// 4. Logic Lọc & Sắp xếp
const filteredResults = computed(() => {
  let result = allPosts.value

  // Lọc theo Category
  if (filters.value.category !== 'all') {
    result = result.filter(p => p.category.toLowerCase().includes(filters.value.category))
  }

  // Lọc theo Độ khó
  if (filters.value.difficulty) {
    result = result.filter(p => p.difficulty === filters.value.difficulty)
  }

  // Lọc theo Thời gian
  if (filters.value.time) {
    if (filters.value.time === 'short') result = result.filter(p => p.time <= 30)
    else if (filters.value.time === 'medium') result = result.filter(p => p.time > 30 && p.time <= 60)
    else if (filters.value.time === 'long') result = result.filter(p => p.time > 60)
  }

  // Lọc theo Search Query từ Header
  if (route.query.q) {
    const keyword = route.query.q.toLowerCase()
    result = result.filter(p => p.title.toLowerCase().includes(keyword))
  }

  // Sắp xếp
  if (filters.value.sort === 'newest') result.sort((a, b) => b.id - a.id)
  else if (filters.value.sort === 'popular') result.sort((a, b) => b.likes - a.likes)

  return result
})

// 5. Logic Phân trang
const totalResults = computed(() => filteredResults.value.length)
const totalPages = computed(() => Math.ceil(totalResults.value / itemsPerPage))

const paginatedPosts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredResults.value.slice(start, end)
})

// Reset khi filter thay đổi
watch(filters, () => { currentPage.value = 1 }, { deep: true })
watch(() => route.query.q, () => { currentPage.value = 1 })

const resetFilters = () => {
  filters.value = { category: 'all', difficulty: '', time: '', sort: 'newest' }
}

const goToDetail = (id) => {
  try { router.push({ name: 'PostDetail', params: { id: id } }) } 
  catch (e) { router.push(`/post/${id}`) }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700&family=Quicksand:wght@500;600;700&display=swap');

.search-page-container { 
  padding: 30px 40px; background-color: #FAFAF9; min-height: 100vh; font-family: 'Quicksand', sans-serif; 
}

/* HEADER & TOGGLE */
.search-header-area { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 20px; }
.page-title { font-family: 'Playfair Display', serif; font-size: 2.2rem; margin: 0; color: #1C1917; }
.sub-text { color: #78716C; margin-top: 5px; }

.btn-toggle-filter {
  background: white; border: 1px solid #E5E5E5; padding: 10px 20px; border-radius: 30px;
  font-weight: 700; color: #44403C; cursor: pointer; display: flex; align-items: center; gap: 8px;
  transition: 0.3s;
}
.btn-toggle-filter:hover, .btn-toggle-filter.active { border-color: #F97316; color: #F97316; background: #FFF7ED; }

/* ADVANCED FILTER PANEL */
.advanced-filter-panel {
  background: white; padding: 25px; border-radius: 20px; margin-bottom: 30px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05); border: 1px solid #F3F4F6;
}
.filter-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; align-items: end; }
.filter-group label { display: block; margin-bottom: 8px; font-weight: 700; color: #44403C; font-size: 0.9rem; }
.filter-group select {
  width: 100%; padding: 10px 15px; border-radius: 12px; border: 1px solid #E5E5E5;
  outline: none; font-family: 'Quicksand', sans-serif; font-weight: 600; color: #57534E; cursor: pointer;
}
.filter-group select:focus { border-color: #F97316; }
.btn-reset {
  width: 100%; padding: 10px; background: #F3F4F6; color: #78716C; border: none; border-radius: 12px;
  font-weight: 700; cursor: pointer; transition: 0.2s;
}
.btn-reset:hover { background: #E5E5E5; color: #1C1917; }

/* STICKY CHIPS */
.filter-sticky { position: sticky; top: 0; z-index: 10; background: #FAFAF9; padding: 10px 0; margin-bottom: 20px; }
.filter-scroll { display: flex; gap: 10px; overflow-x: auto; padding-bottom: 5px; scrollbar-width: none; }
.chip { 
  white-space: nowrap; padding: 8px 18px; border-radius: 20px; border: 1px solid #E7E5E4;
  background: white; color: #57534E; font-weight: 600; cursor: pointer; transition: 0.2s;
}
.chip:hover { border-color: #F97316; color: #F97316; transform: translateY(-2px); }
.chip.active { background: #1C1917; color: white; border-color: #1C1917; }

/* GRID & CARD */
.grid-layout { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 30px; }
.post-card { 
  background: white; border-radius: 20px; overflow: hidden; border: 1px solid transparent; 
  transition: all 0.3s ease; position: relative; cursor: pointer; box-shadow: 0 4px 15px rgba(0,0,0,0.03);
}
.post-card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.08); border-color: #FDBA74; }

.card-thumb { height: 180px; position: relative; overflow: hidden; }
.card-thumb img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.post-card:hover .card-thumb img { transform: scale(1.08); }
.category-tag { position: absolute; top: 12px; left: 12px; background: rgba(255,255,255,0.95); padding: 4px 10px; border-radius: 8px; font-size: 0.7rem; font-weight: 700; color: #F97316; }
.save-btn { position: absolute; top: 12px; right: 12px; width: 34px; height: 34px; border-radius: 50%; background: white; border: none; display: flex; align-items: center; justify-content: center; cursor: pointer; color: #A8A29E; transition: 0.2s; }
.save-btn:hover { color: #F97316; }

.card-content { padding: 18px; }
.post-title { font-size: 1.05rem; font-weight: 700; color: #1C1917; margin: 0 0 12px 0; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.meta-tags { display: flex; gap: 8px; margin-bottom: 12px; }
.meta-pill { font-size: 0.75rem; color: #57534E; background: #F5F5F4; padding: 4px 8px; border-radius: 6px; font-weight: 600; }

.difficulty-bar { display: flex; align-items: center; gap: 8px; font-size: 0.75rem; color: #A8A29E; margin-bottom: 15px; }
.dots { display: flex; gap: 4px; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: #E5E5E5; }
.dot.active { background: #F97316; }

.card-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #F5F5F4; padding-top: 12px; }
.author-box { display: flex; align-items: center; gap: 8px; }
.avt-tiny { width: 24px; height: 24px; border-radius: 50%; object-fit: cover; border: 1px solid #F97316; }
.author-name { font-size: 0.8rem; font-weight: 600; color: #57534E; }
.like-box { font-size: 0.8rem; color: #57534E; font-weight: 600; }

/* PAGINATION */
.pagination-container { display: flex; justify-content: center; align-items: center; gap: 15px; margin-top: 50px; }
.page-btn {
  width: 40px; height: 40px; border-radius: 50%; border: 1px solid #E5E5E5;
  background: white; color: #1C1917; cursor: pointer; transition: 0.2s;
}
.page-btn:hover:not(:disabled) { border-color: #F97316; color: #F97316; }
.page-btn:disabled { opacity: 0.5; cursor: not-allowed; }

.page-numbers { display: flex; gap: 8px; }
.page-num {
  width: 40px; height: 40px; border-radius: 12px; border: none; background: transparent;
  font-weight: 700; color: #57534E; cursor: pointer; transition: 0.2s;
}
.page-num:hover { background: #F5F5F4; }
.page-num.active { background: #1C1917; color: white; box-shadow: 0 4px 10px rgba(0,0,0,0.2); }

/* Transitions */
.slide-fade-enter-active, .slide-fade-leave-active { transition: all 0.3s ease; }
.slide-fade-enter-from, .slide-fade-leave-to { transform: translateY(-10px); opacity: 0; }
.list-move, .list-enter-active, .list-leave-active { transition: all 0.5s ease; }
.list-enter-from, .list-leave-to { opacity: 0; transform: translateY(30px); }
.list-leave-active { position: absolute; }

/* Empty State */
.empty-state { text-align: center; padding: 60px; grid-column: 1 / -1; }
.empty-icon { font-size: 4rem; margin-bottom: 20px; }
</style>