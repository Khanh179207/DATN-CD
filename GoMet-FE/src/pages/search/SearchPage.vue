<template>
  <div class="search-page-wrapper">
    <div class="main-body-container">
      
      <div class="sticky-filter-glass" :class="{ 'is-stuck': isStuck }">
        <div class="filter-header-row">
          <div class="filter-left">
            <button 
              class="tab-link" 
              :class="{ active: currentTab === 'recipes' }"
              @click="switchTab('recipes')"
            >
              <svg class="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M2 12h20"></path>
                <path d="M20 12v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-8"></path>
                <path d="M10 8l2 -5"></path>
                <path d="M14 8l2 -5"></path>
                <path d="M6 8h12"></path>
              </svg>
              Món ăn
            </button>
            <button 
              class="tab-link" 
              :class="{ active: currentTab === 'users' }"
              @click="switchTab('users')"
            >
              <svg class="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
              Người dùng
            </button>
          </div>

          <div class="filter-right" v-if="currentTab === 'recipes'">
            <button class="btn-filter-trigger" @click="toggleAdvancedFilter" :class="{ active: showAdvanced }">
              <span class="icon-filter">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="4" y1="21" x2="4" y2="14"></line><line x1="4" y1="10" x2="4" y2="3"></line>
                  <line x1="12" y1="21" x2="12" y2="12"></line><line x1="12" y1="8" x2="12" y2="3"></line>
                  <line x1="20" y1="21" x2="20" y2="16"></line><line x1="20" y1="12" x2="20" y2="3"></line>
                  <line x1="1" y1="14" x2="7" y2="14"></line><line x1="9" y1="8" x2="15" y2="8"></line>
                  <line x1="17" y1="16" x2="23" y2="16"></line>
                </svg>
              </span>
              Bộ lọc nâng cao
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

<div class="category-showcase-bar" v-if="currentTab === 'recipes'">
  <div class="category-scroll-container">
    <div 
      class="cat-card" 
      :class="{ active: filters.category === null }"
      @click="filters.category = null"
    >
      <div class="cat-content">
        <div class="cat-icon-wrapper all-cat">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect>
            <rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect>
          </svg>
        </div>
        <span class="cat-name">Tất cả</span>
      </div>
    </div>

    <div 
      v-for="cat in categories.filter(c => c.id !== null)" 
      :key="cat.id" 
      class="cat-card"
      :class="{ active: filters.category === cat.id }"
      @click="filters.category = cat.id"
    >
      <div class="cat-content">
        <div class="cat-icon-wrapper">
          <img :src="cat.image" :alt="cat.name" class="cat-img">
        </div>
        <span class="cat-name">{{ cat.name }}</span>
      </div>
    </div>
  </div>
</div>
      </div>

      <transition name="expand">
        <div v-if="showAdvanced && currentTab === 'recipes'" class="advanced-panel-depth">
          <div class="panel-header">
            <h3 class="premium-title">
              <span class="premium-icon">👑</span> Bộ lọc Nâng cao
            </h3>
            <button class="btn-reset-text" @click="resetFilters">Xóa bộ lọc</button>
          </div>

          <div class="panel-layout">
            <div class="filter-row-bottom">
              <div class="filter-col">
                <h4>Độ khó</h4>
                <div class="chip-group">
                  <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value=""><span>Tất cả</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Easy"><span>Dễ</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Medium"><span>Trung bình</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Hard"><span>Khó</span></label>
                </div>
              </div>

              <div class="filter-col">
                <h4>Thời gian nấu</h4>
                <div class="chip-group">
                  <label class="radio-chip"><input type="radio" v-model="filters.time" value=""><span>Tất cả</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.time" value="short"><span>&lt; 30p</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.time" value="medium"><span>30-60p</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.time" value="long"><span>> 60p</span></label>
                </div>
              </div>
            </div>

            <div class="filter-row-bottom premium-exclusive-row">
              <div class="filter-col">
                <h4>✨ Chỉ hiện có Video</h4>
                <label class="premium-toggle">
                  <input type="checkbox" v-model="filters.hasVideo">
                  <div class="toggle-slider"></div>
                </label>
              </div>

              <div class="filter-col">
                <h4>🥕 Có chứa nguyên liệu</h4>
                <div class="ingredient-include-wrapper">
                  <div class="input-with-icon">
                    <input 
                      type="text" 
                      v-model="ingredientInput" 
                      @keydown.enter.prevent="addIncludedIngredient" 
                      placeholder="Nhập nguyên liệu..." 
                    />
                    <button class="btn-add-ing" @click="addIncludedIngredient">+</button>
                  </div>
                  <div class="included-tags">
                    <transition-group name="fade">
                      <span v-for="ing in filters.includedIngredients" :key="ing" class="include-tag">
                        {{ ing }} <button @click="removeIncludedIngredient(ing)">×</button>
                      </span>
                    </transition-group>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>

      <div class="results-grid-container" :class="{ 'is-loading': loading }">
        <transition-group v-if="currentTab === 'recipes'" name="staggered-fade" tag="div" class="grid-layout">
          <RecipeCard 
            v-for="(post, index) in paginatedData" 
            :key="'post-'+post.id" 
            :post="post"
            class="grid-item"
            :style="{ '--delay': index * 0.05 + 's' }" 
            @click="goToDetail(post.id)"
          />
        </transition-group>

        <transition-group v-else name="staggered-fade" tag="div" class="grid-layout user-grid">
          <UserCard 
            v-for="(user, index) in paginatedData" 
            :key="'user-' + user.id" 
            :user="user"
            class="grid-item"
            :style="{ '--delay': index * 0.05 + 's' }"
          />
        </transition-group>

        <div v-if="loading" class="loading-overlay">
          <div class="loading-spinner"></div>
        </div>

        <div v-if="paginatedData.length === 0 && !loading" class="empty-state-depth">
          <div class="icon-3d">🕵️‍♂️</div>
          <h3>Không tìm thấy kết quả</h3>
          <p>Thử sử dụng từ khóa khác hoặc xóa bộ lọc nhé!</p>
          <button class="btn-back-all" @click="resetFilters">Xóa hết bộ lọc</button>
        </div>
      </div>

      <div class="pagination-floating" v-if="totalPages > 1">
        <button class="page-nav" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">←</button>
        <div class="page-dots">
          <span 
            v-for="page in visiblePages" :key="page" 
            class="dot" :class="{ active: currentPage === page, spacer: page === '...' }"
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
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import api from '@/services/api'
import { normalizePost } from '@/services/postService'
import { getCategories } from '@/services/categoryService'

// Components
import RecipeCard from '@/components/common/RecipeCard.vue'
import UserCard from '@/components/common/UserCard.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// --- STATES ---
const currentTab = ref('recipes')
const showAdvanced = ref(false)
const currentPage = ref(1)
const itemsPerPage = 8
const isStuck = ref(false)
const loading = ref(false)
const ingredientInput = ref('')

const categories = ref([{ id: null, name: 'Tất cả' }])
const allPosts = ref([])
const allUsers = ref([])

const filters = ref({ 
  category: null, 
  difficulty: '', 
  time: '', 
  sort: 'newest',
  hasVideo: false,
  includedIngredients: []
})

const diffLevelMap = { 'Easy': 1, 'Medium': 2, 'Hard': 3 }

// --- UTILS ---
const removeTones = (str) => {
  if (!str) return '';
  return String(str).normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/đ/g, 'd').replace(/Đ/g, 'D').toLowerCase();
}

// --- LOGIC: DANH MỤC & LỌC ---
const isPremiumUser = computed(() => {
  const user = authStore.user;
  if (!user) return false;
  return [true, "true", 1, "1"].includes(user.isPremium) || user.role?.toLowerCase() === 'premium' || user.isAdmin;
});

const toggleAdvancedFilter = () => {
  if (!authStore.isAuthenticated) {
    toast.warn('Vui lòng đăng nhập để lọc nâng cao!');
    return;
  }
  if (!isPremiumUser.value) {
    toast.warn('Tính năng này dành cho thành viên Premium! 👑');
    window.dispatchEvent(new CustomEvent('ui:open-premium'));
    return;
  }
  showAdvanced.value = !showAdvanced.value;
};

const addIncludedIngredient = () => {
  const val = ingredientInput.value.trim();
  if (val && !filters.value.includedIngredients.includes(val)) {
    filters.value.includedIngredients.push(val);
  }
  ingredientInput.value = '';
}

const removeIncludedIngredient = (ing) => {
  filters.value.includedIngredients = filters.value.includedIngredients.filter(i => i !== ing)
}

const resetFilters = () => { 
  filters.value = { category: null, difficulty: '', time: '', sort: 'newest', hasVideo: false, includedIngredients: [] }
  ingredientInput.value = '';
}

// --- DATA FETCHING ---
async function fetchPostsRaw() {
  loading.value = true
  try {
    const keyword = route.query.q || ''
    const sortMap = { newest: 'newest', popular: 'views', rating: 'rating' }
    
    const res = await api.get('/api/posts/search', { 
      params: { keyword, categoryID: filters.value.category || undefined, sort: sortMap[filters.value.sort] }
    })

    let raw = res.data?.data || res.data?.content || res.data || [];
    
    // 1. Lọc bỏ ngay các bài viết đang bị ẩn (isActive = 0) khỏi danh sách tìm kiếm
    raw = raw.filter(p => String(p.isActive) === '1' || p.isActive === true || String(p.IsActive) === '1' || p.IsActive === true || p.isActive === undefined);
    
    let mapped = raw.map(dto => {
      const norm = normalizePost(dto);
      const cleanIngText = removeTones(Array.isArray(dto.ingredients) ? dto.ingredients.join(' ') : String(dto.ingredients || ''));

      return {
        ...norm,
        _level: dto.level || norm.level,
        _cookingTime: dto.cookingTime || norm.cookingTime || 0,
        _videoUrl: dto.videoUrl || norm.video,
        _ingredientText: cleanIngText
      };
    })

    // Filter Logic ở Client cho các field nâng cao
    if (filters.value.difficulty) mapped = mapped.filter(p => p._level === diffLevelMap[filters.value.difficulty])
    if (filters.value.time === 'short') mapped = mapped.filter(p => p._cookingTime <= 30)
    else if (filters.value.time === 'medium') mapped = mapped.filter(p => p._cookingTime > 30 && p._cookingTime <= 60)
    else if (filters.value.time === 'long') mapped = mapped.filter(p => p._cookingTime > 60)
    if (filters.value.hasVideo) mapped = mapped.filter(p => p._videoUrl || p.hasVideo)
    if (filters.value.includedIngredients.length > 0) {
      mapped = mapped.filter(p => filters.value.includedIngredients.every(ing => p._ingredientText.includes(removeTones(ing))))
    }

    allPosts.value = mapped
  } catch (err) {
    allPosts.value = []
  } finally { loading.value = false }
}

async function fetchUsersRaw() {
  loading.value = true
  try {
    const res = await api.get('/api/users/search', { params: { keyword: route.query.q || '' } })
    let raw = res.data?.data || res.data?.content || res.data || [];
    allUsers.value = raw.map(u => ({
      ...u,
      id: u.accountID || u.id,
      name: u.fullName || u.username,
      avatar: u.avatar || ''
    }))
  } catch (err) { allUsers.value = [] }
  finally { loading.value = false }
}

const switchTab = (tab) => {
  currentTab.value = tab
  currentPage.value = 1
  tab === 'users' ? fetchUsersRaw() : fetchPostsRaw()
}

// --- PAGINATION ---
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return (currentTab.value === 'recipes' ? allPosts.value : allUsers.value).slice(start, start + itemsPerPage)
})
const totalPages = computed(() => Math.ceil((currentTab.value === 'recipes' ? allPosts.value.length : allUsers.value.length) / itemsPerPage))
const visiblePages = computed(() => {
  const pages = [];
  for (let i = 1; i <= totalPages.value; i++) pages.push(i);
  return pages;
})

const goToPage = (p) => { currentPage.value = p; window.scrollTo({ top: 0, behavior: 'smooth' }); }
const goToDetail = (id) => router.push(`/post/${id}`)

// --- WATCHERS ---
let timer;
watch([filters, () => route.query.q], () => {
  clearTimeout(timer);
  timer = setTimeout(() => {
    currentPage.value = 1;
    currentTab.value === 'recipes' ? fetchPostsRaw() : fetchUsersRaw();
  }, 300);
}, { deep: true });

// --- LIFECYCLE ---
const handleScroll = () => { isStuck.value = window.scrollY > 50 }

onMounted(async () => {
  window.addEventListener('scroll', handleScroll)
  try {
    const cats = await getCategories()
    categories.value = [{ id: null, name: 'Tất cả' }, ...cats.map(c => ({
      id: c.categoryID,
      name: c.categoryName,
      image: c.categoryImage,
    }))]
  } catch (e) { console.error(e) }
  fetchPostsRaw()
})

onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<style scoped lang="scss" src="./SearchPage.scss"></style>