<template>
  <section class="section-block fade-in-up">
    
    <div class="section-header">
      <div class="header-left">
        <span class="sub-label">Cập nhật mỗi ngày</span>
        <h2 class="section-heading">Món Ngon Mới Nhất</h2>
      </div>

      <div class="tabs-scroll-wrapper">
        <div class="tabs-scroll">
          <div 
            class="tab-pill" 
            v-for="tab in tabs" 
            :key="tab"
            :class="{ active: activeTab === tab }"
            @click="activeTab = tab"
          >
            {{ tab }}
          </div>
        </div>
        <div class="scroll-fade"></div>
      </div>
    </div>

    <div class="recipe-grid-wrapper">
      <div v-if="loading" class="recipe-grid">
        <div v-for="n in 8" :key="n" class="skeleton-card">
          <div class="sk-img"></div>
          <div class="sk-text w-80"></div>
          <div class="sk-text w-60"></div>
        </div>
      </div>

      <div v-else class="recipe-grid">
        <RecipeCard 
          v-for="post in filteredPosts" 
          :key="post.id" 
          :post="post" 
          @click="goToDetail(post.id)"
        />
      </div>
    </div>

    <div class="center-btn">
      <button class="btn-load-more" @click="goToSearch">
        Xem tất cả công thức
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"></path><path d="M12 5l7 7-7 7"></path></svg>
      </button>
    </div>

  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import RecipeCard from '@/components/common/RecipeCard.vue' // Import Card chuẩn

const router = useRouter()
const activeTab = ref('Tất cả')
const tabs = ['Tất cả', 'Món ăn sáng', 'Healthy', 'Món nhậu', 'Tráng miệng', 'Ăn vặt', 'Món chay']
const loading = ref(true)

const goToDetail = (id) => router.push({ name: 'PostDetail', params: { id } })
const goToSearch = () => router.push('/search')

// Data giả lập
const posts = ref([
  { id: 1, title: 'Bò Beefsteak Sốt Tiêu Đen', image: 'https://images.unsplash.com/photo-1600891964092-4316c288032e?w=600&q=80', time: '45p', difficulty: 'Khó', likes: 1200, author: { name: 'Chef Ramsay', avatar: 'https://ui-avatars.com/api/?name=G+R&background=EA580C&color=fff' }, category: 'Món Âu' },
  { id: 2, title: 'Salad Bơ Trứng Lòng Đào', image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&q=80', time: '15p', difficulty: 'Dễ', likes: 850, author: { name: 'Hana Giang', avatar: 'https://ui-avatars.com/api/?name=H+G&background=10B981&color=fff' }, category: 'Healthy' },
  { id: 3, title: 'Pancake Dâu Tây Mật Ong', image: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=600&q=80', time: '30p', difficulty: 'Trung bình', likes: 2000, author: { name: 'Nino Home', avatar: 'https://ui-avatars.com/api/?name=N+H&background=EC4899&color=fff' }, category: 'Tráng miệng' },
  { id: 4, title: 'Cà Ri Gà Ấn Độ Cay Nồng', image: 'https://images.unsplash.com/photo-1631292784640-2b24be784d5d?w=600&q=80', time: '60p', difficulty: 'Khó', likes: 560, author: { name: 'Uncle Roger', avatar: 'https://ui-avatars.com/api/?name=U+R&background=F59E0B&color=fff' }, category: 'Món Á' },
  { id: 5, title: 'Mỳ Ý Sốt Kem Nấm', image: 'https://images.unsplash.com/photo-1626844131082-256783844137?w=600&q=80', time: '25p', difficulty: 'Dễ', likes: 3200, author: { name: 'Gomet Chef', avatar: '' }, category: 'Món Âu' },
  { id: 6, title: 'Sinh Tố Xoài Chuối', image: 'https://images.unsplash.com/photo-1623065422902-30a2d299bbe4?w=600&q=80', time: '10p', difficulty: 'Dễ', likes: 150, author: { name: 'Healthy Life', avatar: '' }, category: 'Healthy' },
  { id: 7, title: 'Gà Nướng Mật Ong', image: 'https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=600&q=80', time: '50p', difficulty: 'Trung bình', likes: 900, author: { name: 'BBQ King', avatar: '' }, category: 'Món nhậu' },
  { id: 8, title: 'Bánh Mì Chảo Hà Nội', image: 'https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=600&q=80', time: '20p', difficulty: 'Dễ', likes: 4500, author: { name: 'Street Food', avatar: '' }, category: 'Món ăn sáng' },
])

const filteredPosts = computed(() => {
  if (activeTab.value === 'Tất cả') return posts.value
  return posts.value.filter(p => p.category === activeTab.value || p.category === 'Món Âu') // Demo logic lọc
})

// Giả lập loading
onMounted(() => {
  setTimeout(() => loading.value = false, 1000)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap');

.section-block { 
  margin: 0 0 80px 0; padding: 0 40px; 
  font-family: 'Mulish', sans-serif; width: 100%;
}

/* HEADER & TABS */
.section-header { 
  display: flex; justify-content: space-between; align-items: flex-end; 
  margin-bottom: 40px; flex-wrap: wrap; gap: 20px;
}
.sub-label {
  display: block; font-size: 0.8rem; color: #EA580C; font-weight: 800; 
  text-transform: uppercase; margin-bottom: 8px; letter-spacing: 2px;
}
.section-heading { 
  font-family: 'Playfair Display', serif; font-size: 2.5rem; 
  margin: 0; color: #1C1917; line-height: 1.1;
}

/* Tabs Scroll */
.tabs-scroll-wrapper { position: relative; max-width: 600px; overflow: hidden; }
.tabs-scroll { 
  display: flex; gap: 10px; overflow-x: auto; padding: 5px; 
  scrollbar-width: none; -ms-overflow-style: none; /* Ẩn scrollbar */
}
.tabs-scroll::-webkit-scrollbar { display: none; }

.tab-pill { 
  padding: 10px 24px; border-radius: 30px; 
  background: white; border: 1px solid #E5E7EB;
  color: #57534E; font-weight: 700; font-size: 0.9rem;
  cursor: pointer; transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); 
  white-space: nowrap; user-select: none;
}
.tab-pill:hover { 
  border-color: #D6D3D1; background: #F5F5F4; transform: translateY(-2px); 
}
.tab-pill.active { 
  background: #1C1917; color: white; border-color: #1C1917;
  box-shadow: 0 4px 15px rgba(28, 25, 23, 0.3); transform: translateY(-2px);
}

.scroll-fade {
  position: absolute; right: 0; top: 0; bottom: 0; width: 40px;
  background: linear-gradient(to right, transparent, #FAFAF9);
  pointer-events: none;
}

/* GRID LAYOUT */
.recipe-grid { 
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 30px; 
}

/* SKELETON LOADING */
.skeleton-card {
  background: white; border-radius: 20px; height: 340px; overflow: hidden;
  box-shadow: 0 4px 10px rgba(0,0,0,0.03);
}
.sk-img { width: 100%; height: 200px; background: #E5E7EB; animation: pulse 1.5s infinite; }
.sk-text { height: 14px; background: #E5E7EB; border-radius: 6px; margin: 15px; animation: pulse 1.5s infinite; }
.w-80 { width: 80%; } .w-60 { width: 60%; }
@keyframes pulse { 0% { opacity: 0.6; } 50% { opacity: 1; } 100% { opacity: 0.6; } }

/* BUTTON LOAD MORE */
.center-btn { text-align: center; margin-top: 60px; }
.btn-load-more {
  display: inline-flex; align-items: center; gap: 10px;
  background: white; border: 1px solid #E5E7EB; color: #57534E;
  padding: 14px 40px; border-radius: 50px; font-weight: 800; font-size: 0.95rem;
  cursor: pointer; transition: all 0.3s;
}
.btn-load-more:hover { 
  border-color: #1C1917; color: #1C1917; 
  box-shadow: 0 4px 15px rgba(0,0,0,0.05); transform: translateY(-2px);
}

/* RESPONSIVE */
@media (max-width: 1280px) { .recipe-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 1024px) { 
  .recipe-grid { grid-template-columns: repeat(2, 1fr); } 
  .section-header { flex-direction: column; align-items: flex-start; }
  .tabs-scroll-wrapper { max-width: 100%; width: 100%; }
}
@media (max-width: 640px) { 
  .recipe-grid { grid-template-columns: 1fr; } 
  .section-block { padding: 0 20px; }
  .section-heading { font-size: 2rem; }
}
</style>