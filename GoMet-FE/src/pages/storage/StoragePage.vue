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
              <span class="user-initial">K</span>
            </div>
            <div class="user-text">
              <span class="u-label">CHỦ SỞ HỮU</span>
              <span class="u-name">Khánh Nguyên</span>
            </div>
          </div>

          <div class="divider"></div>

          <div class="filter-group">
            <h3 class="group-title">DANH MỤC</h3>
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
              <span>Đã lưu</span>
              <strong>{{ savedPosts.length }}</strong>
            </div>
            <div class="stat-row">
              <span>Gần đây</span>
              <strong>+2</strong>
            </div>
          </div>

        </div>
      </aside>

      <main class="collection-content">
        
        <header class="collection-header">
          <div class="header-left">
            <div class="brand-tag">GOMET ARCHIVE /// V.3.0</div>
            <h1 class="main-title">Bộ Sưu Tập <span class="text-serif">Cá Nhân</span></h1>
          </div>
          
          <div class="header-right">
            <div class="search-bar">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
              <input type="text" placeholder="Tìm kiếm món đã lưu..." v-model="searchQuery" />
            </div>
          </div>
        </header>

        <div class="gallery-grid" v-if="filteredPosts.length > 0">
          <div 
            v-for="(post, index) in filteredPosts" 
            :key="post.id" 
            class="archive-card"
            @click="goToDetail(post.id)"
          >
            <div class="card-visual">
              <img :src="post.image" loading="lazy" />
              <div class="visual-overlay">
                <span class="cat-tag">{{ post.category }}</span>
              </div>
            </div>

            <div class="card-body">
              <div class="meta-row">
                <span class="date-saved">Lưu ngày: {{ post.savedDate }}</span>
                <button class="btn-unsave" @click.stop="unsavePost(post.id)" title="Xóa khỏi bộ sưu tập">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" stroke="none"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path></svg>
                </button>
              </div>
              
              <h3 class="card-title">{{ post.title }}</h3>
              
              <div class="specs-row">
                <div class="spec">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                  {{ post.time }}
                </div>
                <div class="spec">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path></svg>
                  {{ post.difficulty }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <div class="empty-icon">📂</div>
          <h3>Chưa có dữ liệu</h3>
          <p>Bạn chưa lưu món ăn nào thuộc danh mục này.</p>
          <button class="btn-explore" @click="$router.push('/search')">KHÁM PHÁ NGAY</button>
        </div>

      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeFilter = ref('Tất cả')
const searchQuery = ref('')

// Dữ liệu giả lập (Giả sử lấy từ LocalStorage hoặc API Store)
const savedPosts = ref([
  { id: 1, title: 'Bò Wellington Thượng Hạng', category: 'Món Âu', image: 'https://images.unsplash.com/photo-1544025162-d76694265947?w=600', time: '2 giờ', difficulty: 'Khó', savedDate: '08/02/2026' },
  { id: 2, title: 'Sashimi Cá Hồi', category: 'Món Nhật', image: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600', time: '15 phút', difficulty: 'Dễ', savedDate: '07/02/2026' },
  { id: 3, title: 'Phở Bò Hà Nội', category: 'Món Việt', image: 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=600', time: '4 giờ', difficulty: 'Trung bình', savedDate: '05/02/2026' },
  { id: 4, title: 'Mỳ Ý Carbonara', category: 'Món Âu', image: 'https://images.unsplash.com/photo-1612874742237-6526221588e3?w=600', time: '30 phút', difficulty: 'Dễ', savedDate: '05/02/2026' },
  { id: 5, title: 'Cơm Tấm Sườn Bì', category: 'Món Việt', image: 'https://images.unsplash.com/photo-1595295333158-4742f28fbd85?w=600', time: '1 giờ', difficulty: 'Trung bình', savedDate: '02/02/2026' },
])

// Tạo danh sách Filter động
const categories = computed(() => {
  const cats = new Set(savedPosts.value.map(p => p.category))
  return ['Tất cả', ...Array.from(cats)]
})

// Tính số lượng cho mỗi filter
const getCount = (cat) => {
  if (cat === 'Tất cả') return savedPosts.value.length
  return savedPosts.value.filter(p => p.category === cat).length
}

// Logic lọc bài viết
const filteredPosts = computed(() => {
  let posts = savedPosts.value
  
  // Lọc theo Category
  if (activeFilter.value !== 'Tất cả') {
    posts = posts.filter(p => p.category === activeFilter.value)
  }

  // Lọc theo Search
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    posts = posts.filter(p => p.title.toLowerCase().includes(query))
  }

  return posts
})

// Chức năng
const unsavePost = (id) => {
  if(confirm('Xóa món này khỏi bộ sưu tập?')) {
    savedPosts.value = savedPosts.value.filter(p => p.id !== id)
  }
}

const goToDetail = (id) => {
  router.push(`/post/${id}`)
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,700;1,700&family=Manrope:wght@300;400;500;600;700;800&display=swap');

/* --- 1. CORE LAYOUT (Đồng bộ Planner/Arcade) --- */
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

/* --- 2. SIDEBAR (Phong cách kỹ thuật số) --- */
.collection-sidebar { width: 260px; flex-shrink: 0; }
.sidebar-sticky { position: sticky; top: 30px; display: flex; flex-direction: column; gap: 30px; }

/* Profile Card */
.profile-summary { display: flex; align-items: center; gap: 15px; }
.avatar-ring {
  width: 48px; height: 48px; border-radius: 50%; border: 2px solid #EA580C;
  display: flex; align-items: center; justify-content: center; background: #FFF;
}
.user-initial { font-family: 'Playfair Display', serif; font-size: 1.5rem; font-weight: 700; color: #EA580C; }
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
.gallery-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 30px;
}

/* Card Design */
.archive-card {
  background: #FFF; border-radius: 16px; overflow: hidden; cursor: pointer;
  border: 1px solid rgba(0,0,0,0.05); transition: 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  display: flex; flex-direction: column;
}
.archive-card:hover {
  transform: translateY(-8px); box-shadow: 0 20px 40px rgba(0,0,0,0.05); border-color: #EA580C;
}

.card-visual { position: relative; height: 180px; overflow: hidden; }
.card-visual img { width: 100%; height: 100%; object-fit: cover; transition: 0.5s; }
.archive-card:hover .card-visual img { transform: scale(1.05); }
.visual-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.6), transparent); padding: 15px; display: flex; align-items: flex-end; }
.cat-tag { background: #EA580C; color: #FFF; font-size: 0.65rem; font-weight: 700; padding: 4px 10px; border-radius: 4px; text-transform: uppercase; }

.card-body { padding: 20px; display: flex; flex-direction: column; flex: 1; }
.meta-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.date-saved { font-size: 0.7rem; color: #94A3B8; font-weight: 600; }
.btn-unsave {
  background: transparent; border: none; color: #EA580C; cursor: pointer; padding: 5px;
  border-radius: 50%; transition: 0.2s; display: flex; align-items: center;
}
.btn-unsave:hover { background: #FFF7ED; transform: scale(1.1); }

.card-title { font-family: 'Playfair Display', serif; font-size: 1.2rem; margin: 0 0 15px; color: #1E293B; line-height: 1.3; }

.specs-row { display: flex; gap: 15px; margin-top: auto; border-top: 1px solid #F1F5F9; padding-top: 15px; }
.spec { display: flex; align-items: center; gap: 6px; font-size: 0.75rem; font-weight: 600; color: #64748B; }

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
</style>