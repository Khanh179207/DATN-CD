<template>
  <div class="search-page-wrapper">
    
    <div class="main-body-container">
      
      <div class="sticky-filter-glass" :class="{ 'is-stuck': isStuck }">
        <div class="filter-left">
          <button 
            class="tab-link" 
            :class="{ active: currentTab === 'recipes' }"
            @click="switchTab('recipes')"
          >
            <svg class="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
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
            <svg class="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
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
            {{ $t('search.filters', 'Bộ lọc') }}
          </button>
          <div class="sort-select-wrapper">
            <select v-model="filters.sort">
              <option value="newest">{{ $t('search.sort_newest', 'Mới nhất') }}</option>
              <option value="popular">{{ $t('search.sort_popular', 'Phổ biến') }}</option>
              <option value="rating">{{ $t('search.sort_rating', 'Đánh giá cao') }}</option>
            </select>
          </div>
        </div>
      </div>

      <transition name="expand">
        <div v-if="showAdvanced && currentTab === 'recipes'" class="advanced-panel-depth">
          
          <div class="panel-header">
            <h3 class="premium-title">
              <svg class="crown-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>
              Bộ lọc nâng cao
            </h3>
            <button class="btn-reset-text" @click="resetFilters">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18"></path><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
              Xóa bộ lọc
            </button>
          </div>

          <div class="panel-layout">
            
            <div class="filter-section full-width reveal-item" style="--delay: 0.1s">
              <h4>Danh mục món ăn</h4>
              <div class="category-scroll-box">
                <div class="chip-group">
                  <label 
                    class="radio-chip" 
                    v-for="cat in categories" 
                    :key="cat.id || 'all'"
                  >
                    <input type="radio" v-model="filters.category" :value="cat.id">
                    <span>{{ cat.name }}</span>
                  </label>
                </div>
              </div>
            </div>

            <div class="filter-row-bottom">
              <div class="filter-col reveal-item" style="--delay: 0.2s">
                <h4>{{ $t('search.difficulty', 'Độ khó') }}</h4>
                <div class="chip-group">
                  <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value=""><span>Tất cả</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Easy"><span>Dễ</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Medium"><span>Trung bình</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Hard"><span>Khó</span></label>
                </div>
              </div>

              <div class="filter-col reveal-item" style="--delay: 0.3s">
                <h4>{{ $t('search.time', 'Thời gian') }}</h4>
                <div class="chip-group">
                  <label class="radio-chip"><input type="radio" v-model="filters.time" value=""><span>Tất cả</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.time" value="short"><span>< 30 phút</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.time" value="medium"><span>30–60 phút</span></label>
                  <label class="radio-chip"><input type="radio" v-model="filters.time" value="long"><span>> 60 phút</span></label>
                </div>
              </div>

            </div>

            <div class="filter-row-bottom premium-exclusive-row">
              
              <div class="filter-col reveal-item" style="--delay: 0.4s">
                <h4><span class="premium-icon">✨</span> Bộ lọc Đặc biệt</h4>
                <label class="premium-toggle">
                  <input type="checkbox" v-model="filters.hasVideo">
                  <div class="toggle-slider"></div>
                  <span class="toggle-label">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <polygon points="23 7 16 12 23 17 23 7"></polygon>
                      <rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect>
                    </svg>
                    Chỉ hiện bài có Video
                  </span>
                </label>
              </div>

              <div class="filter-col reveal-item" style="--delay: 0.5s">
                <h4><span class="premium-icon">🥕</span> Có chứa nguyên liệu</h4>
                <div class="ingredient-include-wrapper">
                  <div class="input-with-icon">
                    <input 
                      type="text" 
                      v-model="ingredientInput" 
                      @keydown.enter.prevent="addIncludedIngredient"
                      placeholder="Nhập nguyên liệu (vd: thịt bò, trứng)..."
                    />
                    <button class="btn-add-ing" @click="addIncludedIngredient">+</button>
                  </div>
                  <div class="included-tags" v-if="filters.includedIngredients.length > 0">
                    <span v-for="ing in filters.includedIngredients" :key="ing" class="include-tag">
                      {{ ing }} 
                      <button @click="removeIncludedIngredient(ing)">
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                      </button>
                    </span>
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
            @save-to-plan="handleOpenPlanModal"
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
          <div class="icon-3d">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2"><path d="M3 11l19-9-9 19-2-8-8-2z"/><circle cx="12" cy="12" r="3"/></svg>
          </div>
          <h3>Không tìm thấy {{ currentTab === 'recipes' ? 'món ăn' : 'người dùng' }} nào</h3>
          <p>Thử sử dụng từ khóa khác hoặc xóa các bộ lọc hiện tại.</p>
          <button v-if="currentTab === 'recipes'" class="btn-back-all" @click="resetFilters">Xóa bộ lọc</button>
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

    <MealPlanModal 
      :is-open="isPlanModalOpen" 
      :recipe="selectedRecipeForPlan" 
      @close="isPlanModalOpen = false"
      @success="onPlanSaved"
    />

  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'

// Components
import RecipeCard from '@/components/common/RecipeCard.vue'
import MealPlanModal from '@/components/modals/MealPlanModal.vue'
import UserCard from '@/components/common/UserCard.vue'

// Services
import { normalizePost } from '@/services/postService'
import { getCategories } from '@/services/categoryService'
import { toast } from '@/composables/useToast'
import api from '@/services/api'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// --- UI STATES ---
const currentTab = ref('recipes')
const showAdvanced = ref(false)
const currentPage = ref(1)
const itemsPerPage = 8
const isStuck = ref(false)
const loading = ref(false)

// --- DATA & FILTERS ---
const categories = ref([{ id: null, name: 'Tất cả danh mục' }])
const allPosts = ref([])
const allUsers = ref([])

// State bộ lọc
const filters = ref({ 
  category: null, 
  difficulty: '', 
  time: '', 
  sort: 'newest',
  hasVideo: false,
  includedIngredients: []
})
const diffLevelMap = { 'Easy': 1, 'Medium': 2, 'Hard': 3 }

const ingredientInput = ref('')

// CHUẨN HÓA CHUỖI: Bỏ dấu, chuyển chữ thường (Giúp tìm "bò" ra "Bò Mỹ")
const removeTones = (str) => {
  if (!str) return '';
  return String(str)
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'D')
    .toLowerCase();
}

// --- HÀM THÊM/XÓA NGUYÊN LIỆU ---
const addIncludedIngredient = () => {
  const val = ingredientInput.value.trim();
  if (val) {
    const exists = filters.value.includedIngredients.some(i => i.toLowerCase() === val.toLowerCase());
    if (!exists) {
      filters.value.includedIngredients.push(val);
    }
  }
  ingredientInput.value = '';
}

const removeIncludedIngredient = (ing) => {
  filters.value.includedIngredients = filters.value.includedIngredients.filter(i => i !== ing)
}

// --- MEAL PLAN MODAL LOGIC ---
const isPlanModalOpen = ref(false)
const selectedRecipeForPlan = ref(null)

const handleOpenPlanModal = (recipeData) => {
  selectedRecipeForPlan.value = recipeData
  isPlanModalOpen.value = true
}

const onPlanSaved = () => {
  toast.success(t('mealPlan.save_success', 'Đã lưu vào kế hoạch'))
}

// --- KIỂM TRA QUYỀN PREMIUM ---
const isPremiumUser = computed(() => {
  const user = authStore.user;
  if (!user) return false;
  return String(user.isPremium) === "true" || 
         String(user.isPremium) === "1" || 
         user.role === 'premium' || 
         String(user.isAdmin) === "true" || 
         String(user.isAdmin) === "1";
});

const toggleAdvancedFilter = () => {
  if (!authStore.isAuthenticated) {
    toast.warn('Vui lòng đăng nhập để sử dụng Bộ lọc nâng cao!');
    return;
  }
  if (!isPremiumUser.value) {
    toast.warn('Tính năng Bộ lọc nâng cao chỉ dành cho thành viên Premium! 👑');
    return;
  }
  showAdvanced.value = !showAdvanced.value;
};

// --- FETCHING LOGIC ---
const switchTab = (tab) => {
  currentTab.value = tab
  currentPage.value = 1
  if (tab === 'users' && allUsers.value.length === 0) {
    fetchUsersRaw()
  } else if (tab === 'recipes' && allPosts.value.length === 0) {
    fetchPostsRaw()
  }
}

let filterTimeout = null;

async function fetchPostsRaw() {
  loading.value = true
  try {
    const keyword = route.query.q || ''
    const sortMap = { newest: 'newest', popular: 'views', rating: 'rating' }
    
    const res = await api.get('/api/posts/search', { 
      params: {
        keyword,
        categoryID: filters.value.category || undefined,
        sort: sortMap[filters.value.sort] || 'newest'
      }
    })

    let rawPostData = [];
    if (Array.isArray(res.data)) {
      rawPostData = res.data;
    } else if (res.data && Array.isArray(res.data.data)) {
      rawPostData = res.data.data;
    } else if (res.data && Array.isArray(res.data.content)) {
      rawPostData = res.data.content;
    }

    let mapped = rawPostData.map(dto => {
      const normPost = normalizePost(dto) || {};
      
      // 🔥 CHỈ LẤY NGUYÊN LIỆU ĐỂ TÌM KIẾM
      const rawIng = dto.Ingredients || dto.ingredients || dto.ingredient || dto.recipeIngredients || normPost.ingredients || '';
      const ingText = Array.isArray(rawIng) ? rawIng.join(' ') : String(rawIng);
      const cleanIngredientText = removeTones(ingText);

      return {
        ...normPost,
        _level: dto.level || dto.Level || normPost.level,
        _cookingTime: dto.cookingTime || dto.CookingTime || normPost.cookingTime || 0,
        _categoryID: dto.categoryID || dto.CategoryID || normPost.categoryID,
        // ĐỒNG BỘ CÁC TRƯỜNG DỮ LIỆU ĐỂ CARD HIỂN THỊ ĐẸP NHƯ TRANG CHỦ
        authorID: dto.authorID || dto.accountID || normPost.authorID, 
        isPremium: dto.isPremium || dto.IsPremium || normPost.isPremium || false, 
        createdAt: dto.createdAt || dto.CreatedAt || dto.date || dto.Date || new Date().toISOString(), 
        likes: dto.likes ?? dto.Likes ?? dto.likeCount ?? dto.favoriteCount ?? 0,
        _videoUrl: dto.videoUrl || dto.VideoUrl || dto.video || dto.Video || normPost.video || null,
        _ingredientText: cleanIngredientText
      };
    })

    // Lọc theo Độ khó & Thời gian
    if (filters.value.difficulty) {
      const level = diffLevelMap[filters.value.difficulty]
      mapped = mapped.filter(p => p._level === level)
    }
    if (filters.value.time === 'short') mapped = mapped.filter(p => p._cookingTime <= 30)
    else if (filters.value.time === 'medium') mapped = mapped.filter(p => p._cookingTime > 30 && p._cookingTime <= 60)
    else if (filters.value.time === 'long') mapped = mapped.filter(p => p._cookingTime > 60)

    // Lọc Video
    if (filters.value.hasVideo) {
      mapped = mapped.filter(p => p._videoUrl || p.hasVideo); 
    }

    // 🔥 LỌC NGUYÊN LIỆU (Chỉ quét trong trường Nguyên Liệu)
    if (filters.value.includedIngredients.length > 0) {
      mapped = mapped.filter(p => {
        return filters.value.includedIngredients.every(requiredIng => {
          const searchKey = removeTones(requiredIng).trim();
          return p._ingredientText.includes(searchKey);
        });
      });
    }

    allPosts.value = mapped
  } catch (err) {
    console.error(err)
    allPosts.value = []
  } finally {
    loading.value = false
  }
}

async function fetchUsersRaw() {
  loading.value = true
  try {
    const keyword = route.query.q || ''
    const requestParams = {}
    if (keyword.trim()) requestParams.keyword = keyword.trim()

    const res = await api.get('/api/users/search', { params: requestParams })

    let rawData = []
    if (Array.isArray(res.data)) rawData = res.data
    else if (res.data && Array.isArray(res.data.data)) rawData = res.data.data
    else if (res.data && Array.isArray(res.data.content)) rawData = res.data.content

    allUsers.value = rawData.map(user => ({
      ...user, 
      id: user.accountID || user.accountId || user.id,
      name: user.fullName || user.username || user.name || 'Người dùng ẩn danh',
      handle: user.username || user.handle || 'user',
      avatar: user.avatarUrl || user.avatar || ''
    }))
    
  } catch (err) {
    console.error('Lỗi tìm kiếm người dùng:', err)
    allUsers.value = []
  } finally {
    loading.value = false
  }
}

// --- COMPUTED PROPERTIES ---
const activeDataList = computed(() => currentTab.value === 'recipes' ? allPosts.value : allUsers.value)
const totalResults = computed(() => activeDataList.value.length)
const totalPages = computed(() => Math.ceil(totalResults.value / itemsPerPage))

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return activeDataList.value.slice(start, start + itemsPerPage)
})

const visiblePages = computed(() => {
  const pages = []
  if (totalPages.value <= 7) {
    for (let i = 1; i <= totalPages.value; i++) pages.push(i)
  } else {
    pages.push(1)
    if (currentPage.value > 3) pages.push('...')
    const start = Math.max(2, currentPage.value - 1)
    const end = Math.min(totalPages.value - 1, currentPage.value + 1)
    for (let i = start; i <= end; i++) pages.push(i)
    if (currentPage.value < totalPages.value - 2) pages.push('...')
    pages.push(totalPages.value)
  }
  return pages
})

// --- ACTIONS ---
const quickFilter = (keyword) => { router.push({ query: { q: keyword } }) }

// Reset Filters
const resetFilters = () => { 
  filters.value = { 
    category: null, difficulty: '', time: '', sort: 'newest', 
    hasVideo: false, includedIngredients: [] 
  }
  ingredientInput.value = ''
  router.push({ query: {} }) 
}

const goToPage = (p) => { 
  if (p !== '...') { 
    currentPage.value = p
    window.scrollTo({ top: 0, behavior: 'smooth' }) 
  } 
}
const goToDetail = (id) => router.push(`/post/${id}`)

// --- WATCHERS ---
watch([filters, () => route.query.q], () => { 
  if(filterTimeout) clearTimeout(filterTimeout);
  filterTimeout = setTimeout(() => {
    currentPage.value = 1
    if (currentTab.value === 'recipes') fetchPostsRaw()
    if (currentTab.value === 'users') fetchUsersRaw()
  }, 150); 
}, { deep: true })

// --- LIFECYCLE ---
const handleScroll = () => { isStuck.value = window.scrollY > 50 }

onMounted(async () => {
  window.addEventListener('scroll', handleScroll)
  try {
    const cats = await getCategories()
    categories.value = [
      { id: null, name: 'Tất cả danh mục' }, 
      ...cats.map(c => ({ id: c.categoryID, name: c.categoryName }))
    ]
  } catch (e) {
    categories.value = [{ id: null, name: 'Tất cả danh mục' }]
  }
  if (currentTab.value === 'recipes') fetchPostsRaw()
  else fetchUsersRaw()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  if(filterTimeout) clearTimeout(filterTimeout);
})
</script>

<style scoped src="./SearchPage.scss"></style>