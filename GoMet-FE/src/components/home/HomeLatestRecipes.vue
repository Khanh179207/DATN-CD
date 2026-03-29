<template>
  <section class="section-block fade-in-up">
    
    <div class="section-header">
      <div class="header-left">
        <span class="sub-label">{{ $t('home.updated_daily') }}</span>
        <h2 class="section-heading">{{ $t('home.latest_dishes') }}</h2>
      </div>

      <div class="tabs-scroll-wrapper">
        <div class="tabs-scroll">
          <div 
            class="tab-pill" 
            v-for="tab in tabs" 
            :key="tab.id"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            {{ tab.name }}
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
        {{ $t('home.view_all_recipes') }}
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"></path><path d="M12 5l7 7-7 7"></path></svg>
      </button>
    </div>

  </section>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue' // Thêm watch để theo dõi đăng nhập
import { useRouter } from 'vue-router'
import RecipeCard from '@/components/common/RecipeCard.vue'
import { getLatestPosts, normalizePost } from '@/services/postService'
import { getMyFollows } from '@/services/socialService'
import { useAuthStore } from '@/stores/auth' // Import store của bạn

const router = useRouter()
const authStore = useAuthStore() // Khởi tạo store

const activeTab = ref('discover') 
const loading = ref(true)
const posts = ref([])
const myFollowedAccountIDs = ref([]) 

// Lấy ID người dùng hiện tại từ store một cách an toàn
const currentUserId = computed(() => {
  return authStore.currentUser ? authStore.currentUser.accountID : null;
})

const tabs = computed(() => [
  { id: 'discover', name: 'Khám phá' },
  { id: 'following', name: 'Người theo dõi' }
])

const filteredPosts = computed(() => {
  let result = [];

  if (activeTab.value === 'discover') {
    const allSorted = [...posts.value].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    
    const premiums = allSorted.filter(p => p.isPremium);
    const normals = allSorted.filter(p => !p.isPremium);

    const guaranteedPremiums = premiums.slice(0, 2);
    const remainingPool = [...premiums.slice(2), ...normals].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    const neededSlots = 8 - guaranteedPremiums.length;
    const additionalPosts = remainingPool.slice(0, neededSlots);

    result = [...guaranteedPremiums, ...additionalPosts].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

  } else if (activeTab.value === 'following') {
    result = posts.value
      .filter(p => myFollowedAccountIDs.value.includes(p.authorID)) 
      .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  }

  return result.slice(0, 8);
})

const goToDetail = (id) => router.push({ name: 'PostDetail', params: { id } })
const goToSearch = () => router.push('/search')

// Tách riêng hàm tải dữ liệu để có thể gọi lại khi đăng nhập/đăng xuất
const loadData = async () => {
  loading.value = true;
  try {
    const [rawPosts, realFollowedIds] = await Promise.all([
      getLatestPosts(50),
      currentUserId.value ? getMyFollows(currentUserId.value) : [] 
    ]);
    
    myFollowedAccountIDs.value = realFollowedIds || [];

    posts.value = rawPosts.map(dto => ({
      ...normalizePost(dto),
      authorID: dto.authorID, 
      isPremium: dto.isPremium || dto.IsPremium || false, 
      createdAt: dto.createdAt || dto.date || new Date().toISOString(),
      likes: dto.likes ?? dto.likeCount ?? dto.favoriteCount ?? 0
    }))
  } catch (err) {
    console.warn('HomeLatestRecipes: API error', err)
  } finally {
    loading.value = false
  }
}

// Chạy lần đầu khi load trang
onMounted(() => {
  loadData();
})

// [TÙY CHỌN NÂNG CAO]: Tự động tải lại danh sách follow khi người dùng đăng nhập hoặc đăng xuất
watch(currentUserId, (newId, oldId) => {
  if (newId !== oldId) {
    loadData();
  }
})
</script>

<style scoped>
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
  scrollbar-width: none; -ms-overflow-style: none; /* Hide scrollbar */
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
  background: #EA580C; /* Đổi nền thành màu cam */
  color: white; 
  border-color: #EA580C; /* Đổi viền thành màu cam */
  box-shadow: 0 4px 15px rgba(234, 88, 12, 0.3); /* Đổi bóng đổ (shadow) sang tông cam nhạt */
  transform: translateY(-2px);
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