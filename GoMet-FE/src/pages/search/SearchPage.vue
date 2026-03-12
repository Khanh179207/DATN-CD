<template>
  <div class="search-page-wrapper">
    
    <section class="hero-depth">
      <div class="blob blob-1"></div>
      <div class="blob blob-2"></div>
      
      <div class="hero-content">
        <span class="hero-subtitle">Premium Collection</span>
        <h1 class="hero-title">Awaken <br> <span class="highlight">Your Palate</span></h1>
        <p class="hero-text">
          Over <strong>{{ totalResults }}+</strong> exclusive recipes waiting to be explored.
        </p>
        
        <div class="trending-pills">
          <span class="label">{{ $t('search.trending_label') }}</span>
          <div class="pills-scroll">
            <button class="pill-item" @click="quickFilter('healthy')">🥑 Eat Clean</button>
            <button class="pill-item" @click="quickFilter('vietnam')">🇻🇳 Vietnamese</button>
            <button class="pill-item" @click="quickFilter('cake')">🍰 Desserts</button>
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
            {{ $t('search.filters') }}
          </button>
          <div class="sort-select-wrapper">
            <select v-model="filters.sort">
              <option value="newest">{{ $t('search.sort_newest') }}</option>
              <option value="popular">{{ $t('search.sort_popular') }}</option>
              <option value="rating">{{ $t('search.sort_rating') }}</option>
            </select>
          </div>
        </div>
      </div>

      <transition name="expand">
        <div v-if="showAdvanced" class="advanced-panel-depth">
          <div class="panel-grid">
            <div class="filter-col">
              <h4>{{ $t('search.difficulty') }}</h4>
              <div class="chip-group">
                <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value=""><span>{{ $t('common.category_all') }}</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Easy"><span>{{ $t('common.easy') }}</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Medium"><span>{{ $t('common.medium') }}</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.difficulty" value="Hard"><span>{{ $t('common.hard') }}</span></label>
              </div>
            </div>
            <div class="filter-col">
              <h4>{{ $t('search.time') }}</h4>
              <div class="chip-group">
                <label class="radio-chip"><input type="radio" v-model="filters.time" value=""><span>{{ $t('common.category_all') }}</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.time" value="short"><span>{{ $t('search.short_time') }}</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.time" value="medium"><span>{{ $t('search.medium_time') }}</span></label>
                <label class="radio-chip"><input type="radio" v-model="filters.time" value="long"><span>{{ $t('search.long_time') }}</span></label>
              </div>
            </div>
            <div class="filter-col reset-area">
              <button class="btn-reset-text" @click="resetFilters">{{ $t('search.clear_filters') }}</button>
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
          <div class="icon-3d">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2"><path d="M3 11l19-9-9 19-2-8-8-2z"/><circle cx="12" cy="12" r="3"/></svg>
          </div>
          <h3>{{ $t('search.no_results') }}</h3>
          <p>{{ $t('search.no_results_sub') }}</p>
          <button class="btn-back-all" @click="resetFilters">{{ $t('search.view_all_btn') }}</button>
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
import { useI18n } from 'vue-i18n'
import RecipeCard from '@/components/common/RecipeCard.vue'
import { normalizePost } from '@/services/postService'
import { getCategories } from '@/services/categoryService'
import { toast } from '@/composables/useToast'
import api from '@/services/api'

const { t } = useI18n()

const router = useRouter()
const route = useRoute()

const showAdvanced = ref(false)
const currentPage = ref(1)
const itemsPerPage = 8
const isStuck = ref(false)
const loading = ref(false)

const categories = ref([{ id: null, name: 'All' }])
const allPosts = ref([])
const filters = ref({ category: null, difficulty: '', time: '', sort: 'newest' })

const diffLevelMap = { 'Easy': 1, 'Medium': 2, 'Hard': 3 }

async function fetchPostsRaw() {
  loading.value = true
  try {
    const keyword = route.query.q || ''
    const sortMap = { newest: 'newest', popular: 'views', rating: 'rating' }
    const res = await api.get('/api/posts/search', { params: {
      keyword,
      categoryID: filters.value.category || undefined,
      sort: sortMap[filters.value.sort] || 'newest'
    }})
    let mapped = res.data.map(dto => ({
      ...normalizePost(dto),
      _level: dto.level,
      _cookingTime: dto.cookingTime || 0,
      _categoryID: dto.categoryID
    }))
    if (filters.value.difficulty) {
      const level = diffLevelMap[filters.value.difficulty]
      mapped = mapped.filter(p => p._level === level)
    }
    if (filters.value.time === 'short') mapped = mapped.filter(p => p._cookingTime <= 30)
    if (filters.value.time === 'medium') mapped = mapped.filter(p => p._cookingTime > 30 && p._cookingTime <= 60)
    if (filters.value.time === 'long') mapped = mapped.filter(p => p._cookingTime > 60)
    allPosts.value = mapped
  } catch (err) {
    toast.warn(t('toast.search_error'))
    allPosts.value = []
  } finally {
    loading.value = false
  }
}

const totalResults = computed(() => allPosts.value.length)
const totalPages = computed(() => Math.ceil(totalResults.value / itemsPerPage))
const paginatedPosts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return allPosts.value.slice(start, start + itemsPerPage)
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

const quickFilter = (keyword) => { router.push({ query: { q: keyword } }) }
const resetFilters = () => { filters.value = { category: null, difficulty: '', time: '', sort: 'newest' }; router.push({ query: {} }) }
const goToPage = (p) => { if (p !== '...') { currentPage.value = p; window.scrollTo({ top: 400, behavior: 'smooth' }) } }
const goToDetail = (id) => router.push(`/home/post/${id}`)
const handleSave = (id) => console.log('Saved', id)

watch([filters, () => route.query.q], () => { currentPage.value = 1; fetchPostsRaw() }, { deep: true })

onMounted(async () => {
  window.addEventListener('scroll', () => { isStuck.value = window.scrollY > 350 })
  const cats = await getCategories().catch(() => [])
  categories.value = [{ id: null, name: t('common.category_all') }, ...cats.map(c => ({ id: c.categoryID, name: c.categoryName }))]
  await fetchPostsRaw()
})
</script>

<style scoped>
.search-page-container { width: 100%; min-height: 100vh; background-color: #FAFAF9; font-family: 'Mulish', sans-serif; }

/* 1. HERO DEPTH (AURORA) */
.hero-depth {
  position: relative; padding: 100px 20px 140px; text-align: center;
  background: #FFF; overflow: hidden;
}
/* Soft blurry blob background effect */
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