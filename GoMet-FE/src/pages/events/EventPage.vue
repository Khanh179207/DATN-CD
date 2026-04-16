<template>
  <div class="events-page-premium">
    
    <section class="events-hero-slider">
      <div v-if="loading" class="slider-loading">
        <div class="spinner-ring"></div>
      </div>
      
      <div v-else-if="topThreeEvents.length" class="slider-container">
        <transition-group name="fade-slide" tag="div" class="slides-wrapper">
          <div 
            v-for="(slide, index) in topThreeEvents" 
            :key="slide.id"
            v-show="currentSlide === index"
            class="slide-item"
            :style="{ backgroundImage: `url(${slide.image})` }"
          >
            <div class="hero-overlay"></div>
            
            <div class="hero-content-wrapper">
              <div class="glass-panel">
                <div class="status-badge" :class="slide.category">
                  <span class="pulse-dot"></span>
                  {{ slide.category === 'active' ? 'ĐANG DIỄN RA' : (slide.category === 'upcoming' ? 'SẮP DIỄN RA' : 'ĐÃ KẾT THÚC') }}
                </div>
                
                <h1 class="hero-title">{{ slide.title }}</h1>
                <p class="hero-desc">Tham gia ngay để kết nối và chia sẻ công thức tuyệt đỉnh của bạn cùng cộng đồng GoMet.</p>
                
                <div class="hero-meta">
                  <div class="meta-item"><span class="icon">📅</span> {{ slide.time }}</div>
                  <div class="meta-item"><span class="icon">👥</span> {{ slide.totalAttendees }} Bài dự thi</div>
                </div>
                
                <button class="btn-hero-primary" v-if="slide.category !== 'ended'">
                  {{ $t('events.register') }}
                  <svg class="arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
                </button>
              </div>
            </div>
          </div>
        </transition-group>

        <button class="nav-btn prev-btn" @click="prevSlide">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="15 18 9 12 15 6"></polyline></svg>
        </button>
        <button class="nav-btn next-btn" @click="nextSlide">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="9 18 15 12 9 6"></polyline></svg>
        </button>

        <div class="slider-indicators">
          <button v-for="(_, index) in topThreeEvents" :key="index" class="indicator-dot" :class="{ active: currentSlide === index }" @click="goToSlide(index)">
            <span class="progress-bar"></span>
          </button>
        </div>
      </div>
    </section>

    <div class="events-toolbar-sticky">
      <div class="toolbar-inner">
        <div class="segmented-wrapper">
          <div class="segmented-control">
            <button 
              v-for="tab in filters" :key="tab.id" 
              class="segment-btn" 
              :class="{ active: currentFilter === tab.id }" 
              @click="currentFilter = tab.id"
            >
              {{ tab.label }}
            </button>
          </div>
        </div>

        <div class="search-box-modern">
          <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
          <input type="text" v-model="searchQuery" :placeholder="$t('events.find_events')">
        </div>
      </div>
    </div>

    <div class="events-container">
      <transition-group name="stagger-list" tag="div" class="events-grid">
        <EventCard v-for="(evt, index) in filteredEvents" :key="evt.id" :event="evt" class="grid-item" :style="{ transitionDelay: `${index * 0.05}s` }" />
      </transition-group>
      
      <div v-if="filteredEvents.length === 0 && !loading" class="empty-state-modern">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
        </div>
        <h3>Không tìm thấy sự kiện</h3>
        <p>Thử thay đổi từ khóa hoặc bộ lọc xem sao nhé!</p>
        <button class="btn-clear-filter" @click="currentFilter = 'all'; searchQuery = ''">Xóa bộ lọc</button>
      </div>

      <div class="load-more-section" v-if="filteredEvents.length > 0">
        <button class="btn-load-more">{{ $t('events.load_more') }}</button>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import EventCard from '@/components/common/EventCard.vue'
import { getEvents } from '@/services/eventService'

const { t } = useI18n()
const currentFilter = ref('all')
const filters = computed(() => [
  { id: 'all',      label: t('common.category_all') },
  { id: 'upcoming', label: t('events.upcoming') },
  { id: 'active',   label: t('events.ongoing') },
  { id: 'ended',    label: t('events.ended') },
])

const events = ref([])
const loading = ref(true)
const searchQuery = ref('')
const currentSlide = ref(0)
let slideInterval = null

const MONTH_VN = ['TH1','TH2','TH3','TH4','TH5','TH6','TH7','TH8','TH9','TH10','TH11','TH12']
const fallbackImages = [
  'https://images.unsplash.com/photo-1556761175-5973dc0f32e7?q=80&w=2032',
  'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?q=80&w=2000',
  'https://images.unsplash.com/photo-1528605105345-5344ea20e269?q=80&w=2000'
]

const formatDate = (dateObj) => {
  if (!dateObj) return ''
  return `${String(dateObj.getDate()).padStart(2, '0')}/${String(dateObj.getMonth() + 1).padStart(2, '0')}/${dateObj.getFullYear()}`
}

const getImageUrl = (path, index) => {
  if (!path) return fallbackImages[index % fallbackImages.length];
  if (path.startsWith('/uploads')) {
    return `http://localhost:8080${encodeURI(path)}`; 
  }
  return path;
}

const normalizeEvent = (e, index) => {
  const start = e.startAt ? new Date(e.startAt) : null
  const end = e.endAt ? new Date(e.endAt) : null
  const voteEnd = e.voteEndAt ? new Date(e.voteEndAt) : null
  const now = new Date()
  const finalEndDate = voteEnd && voteEnd > end ? voteEnd : end;
  const isForceEnded = e.isForceEnded ?? e.forceEnded ?? e.IsForceEnded ?? 0;

  let currentStatus = 'upcoming'
  if (Number(isForceEnded) === 1) {
      currentStatus = 'ended'
  } else if (start && finalEndDate) {
    if (now > finalEndDate) {
        currentStatus = 'ended'
    } else if (now >= start && now <= finalEndDate) {
        currentStatus = 'active'
    }
  }

  return {
    id: e.eventID ?? e.eventId ?? e.id,
    title: e.eventName ?? e.title,
    image: getImageUrl(e.bannerImage, index), 
    month: start ? MONTH_VN[start.getMonth()] : '??',
    day: start ? String(start.getDate()).padStart(2, '0') : '??',
    time: start && finalEndDate ? `${formatDate(start)} → ${formatDate(finalEndDate)}` : 'TBA',
    rawEndAt: finalEndDate, 
    location: 'Trực tuyến',
    type: 'online',
    totalAttendees: Number(e.postCount) || 0, 
    category: currentStatus,
  }
}

const resetSliderInterval = () => {
  if (slideInterval) clearInterval(slideInterval)
  slideInterval = setInterval(nextSlide, 6000) 
}

const nextSlide = () => {
  if (topThreeEvents.value.length === 0) return;
  currentSlide.value = (currentSlide.value + 1) % topThreeEvents.value.length
  resetSliderInterval()
}
const prevSlide = () => {
  if (topThreeEvents.value.length === 0) return;
  currentSlide.value = (currentSlide.value - 1 + topThreeEvents.value.length) % topThreeEvents.value.length
  resetSliderInterval()
}
const goToSlide = (index) => {
  currentSlide.value = index
  resetSliderInterval()
}

onMounted(async () => {
  try {
    const response = await getEvents()
    let rawData = Array.isArray(response) ? response : (response?.content || response?.data || []);
    const validData = rawData.filter(e => {
      let activeVal = e.isActive ?? e.active ?? e.IsActive;
      return activeVal !== undefined ? Number(activeVal) !== 0 : true;
    })
    events.value = validData.map((e, index) => normalizeEvent(e, index))
    if (topThreeEvents.value.length > 1) slideInterval = setInterval(nextSlide, 6000)
  } catch (err) {
  } finally { loading.value = false }
})

onUnmounted(() => { if (slideInterval) clearInterval(slideInterval) })

const topThreeEvents = computed(() => {
  const validEvents = events.value.filter(e => e.category !== 'ended')
  return validEvents.length > 0 ? validEvents.slice(0, 3) : events.value.slice(0, 3)
})

const filteredEvents = computed(() => {
  let list = events.value
  if (currentFilter.value !== 'all') list = list.filter(e => e.category === currentFilter.value)
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(e => e.title?.toLowerCase().includes(q))
  }
  return list
})
</script>

<style scoped>
.events-page-premium {
  font-family: 'Mulish', sans-serif;
  background-color: #FAFAFA;
  min-height: 100vh;
  padding-bottom: 80px;
  width: 100%;
  overflow-x: hidden; /* Chặn cuộn ngang vĩnh viễn toàn trang */
}

/* =======================================================
   2. HERO SLIDER
   ======================================================= */
.events-hero-slider {
  position: relative;
  height: 500px;
  margin: 20px;
  border-radius: 32px;
  overflow: hidden;
  background: #111827;
  box-shadow: 0 25px 50px -12px rgba(0,0,0,0.15);
}

.slider-container, .slides-wrapper { width: 100%; height: 100%; position: relative; }

.slide-item {
  position: absolute; inset: 0;
  background-size: cover; background-position: center;
  width: 100%; height: 100%;
  display: flex; align-items: flex-end;
}

.hero-overlay {
  position: absolute; inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.9) 0%, rgba(0,0,0,0.3) 50%, rgba(0,0,0,0.1) 100%);
  z-index: 1;
}

.hero-content-wrapper {
  position: relative; z-index: 2;
  width: 100%; padding: 40px;
}

.glass-panel {
  max-width: 650px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 40px;
  border-radius: 24px;
  color: white;
  animation: slideUp 0.8s forwards;
}

@keyframes slideUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }

/* Badge & Status Colors */
.status-badge {
  display: inline-flex; align-items: center; gap: 8px;
  background: rgba(0,0,0,0.4); padding: 6px 14px; border-radius: 20px;
  font-size: 0.75rem; font-weight: 800; letter-spacing: 1px; margin-bottom: 20px;
}
.status-badge.active .pulse-dot { width: 8px; height: 8px; border-radius: 50%; background: #22C55E; box-shadow: 0 0 8px #22C55E; animation: pulse 2s infinite; }
.status-badge.upcoming .pulse-dot { width: 8px; height: 8px; border-radius: 50%; background: #3B82F6; }
.status-badge.ended .pulse-dot { width: 8px; height: 8px; border-radius: 50%; background: #6B7280; }

@keyframes pulse { 0% { box-shadow: 0 0 0 0 rgba(34, 197, 94, 0.7); } 70% { box-shadow: 0 0 0 6px rgba(34, 197, 94, 0); } 100% { box-shadow: 0 0 0 0 rgba(34, 197, 94, 0); } }

.hero-title { font-family: 'Playfair Display', serif; font-size: 2.8rem; line-height: 1.2; margin: 0 0 15px; font-weight: 800; }
.hero-desc { font-size: 1.1rem; color: #D1D5DB; line-height: 1.6; margin-bottom: 25px; }

.hero-meta { display: flex; gap: 24px; font-weight: 700; margin-bottom: 30px; font-size: 0.95rem; color: #FFF; }
.meta-item { display: flex; align-items: center; gap: 8px; }

/* 🟠 PHỤC HỒI NÚT ĐĂNG KÝ THAM GIA ĐẸP MẮT NHƯ CŨ */
.btn-hero-primary {
  display: inline-flex; align-items: center; gap: 10px;
  background: #EA580C; /* Màu cam gốc */
  color: white; border: none;
  padding: 14px 32px; border-radius: 30px; 
  font-weight: 800; font-size: 1rem;
  cursor: pointer; transition: all 0.3s;
}
.btn-hero-primary:hover { background: #C2410C; transform: translateY(-3px); box-shadow: 0 10px 20px rgba(234, 88, 12, 0.3); }
.btn-hero-primary .arrow { width: 18px; height: 18px; transition: transform 0.3s; }
.btn-hero-primary:hover .arrow { transform: translateX(5px); }

/* Điều hướng Slider */
.nav-btn {
  position: absolute; top: 50%; transform: translateY(-50%); z-index: 10;
  width: 50px; height: 50px; border-radius: 50%; border: 1px solid rgba(255,255,255,0.3);
  background: rgba(0,0,0,0.3); color: white; cursor: pointer; transition: 0.3s;
  display: flex; align-items: center; justify-content: center; backdrop-filter: blur(8px);
  opacity: 0;
}
.events-hero-slider:hover .nav-btn { opacity: 1; }
.nav-btn:hover { background: white; color: #111827; }
.prev-btn { left: 30px; } .next-btn { right: 30px; }
.slider-indicators { position: absolute; bottom: 30px; right: 60px; z-index: 10; display: flex; gap: 10px; }
.indicator-dot { width: 40px; height: 4px; background: rgba(255,255,255,0.3); border: none; border-radius: 4px; cursor: pointer; overflow: hidden; position: relative;}
.indicator-dot.active .progress-bar { position: absolute; inset: 0; background: #EA580C; animation: progressFill 6s linear forwards; }
@keyframes progressFill { from { transform: scaleX(0); transform-origin: left; } to { transform: scaleX(1); transform-origin: left; } }

.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.8s ease-in-out; }
.fade-slide-enter-from { opacity: 0; transform: scale(1.05); }
.fade-slide-leave-to { opacity: 0; }

.slider-loading { display: flex; justify-content: center; align-items: center; height: 100%; }
.spinner-ring { width: 50px; height: 50px; border: 4px solid rgba(255,255,255,0.1); border-top-color: #EA580C; border-radius: 50%; animation: spin 1s infinite linear; }
@keyframes spin { 100% { transform: rotate(360deg); } }

/* =======================================================
   3. TOOLBAR & SEARCH
   ======================================================= */
.events-toolbar-sticky {
  position: sticky; top: 60px; z-index: 50;
  background: rgba(250, 250, 250, 0.9); backdrop-filter: blur(12px);
  padding: 20px; border-bottom: 1px solid #F3F4F6; margin-bottom: 20px;
}

.toolbar-inner {
  max-width: 1400px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; gap: 20px;
}

/* KHỐI TABS PHÂN LOẠI */
.segmented-wrapper { 
  flex: 1; min-width: 0; 
}
.segmented-control {
  display: inline-flex; background: #E5E7EB; padding: 5px; border-radius: 100px;
}
.segment-btn {
  background: transparent; border: none; padding: 10px 28px; border-radius: 100px;
  font-weight: 800; color: #6B7280; font-size: 0.95rem; cursor: pointer; transition: all 0.3s ease;
  white-space: nowrap; /* Quan trọng: Không cho phép rớt dòng */
}
.segment-btn.active {
  background: #FFFFFF; color: #111827; box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.search-box-modern {
  display: flex; align-items: center; gap: 12px;
  background: #FFFFFF; border: 1px solid #E5E7EB; border-radius: 100px;
  padding: 8px 12px 8px 24px; /* Chỉnh lại padding để chứa nút vuông */
  width: 350px; transition: 0.3s; box-shadow: inset 0 2px 4px rgba(0,0,0,0.02);
}
.search-box-modern:focus-within { border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234,88,12,0.1); }
.search-icon { width: 18px; height: 18px; color: #9CA3AF; }
.search-box-modern input { border: none; outline: none; width: 100%; font-size: 1rem; color: #111827; background: transparent; }

/* Nút "Tìm kiếm" màu cam nằm gọn bên trong Input */
.btn-search-submit {
  background: #EA580C;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 100px;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  transition: 0.3s;
  white-space: nowrap;
}
.btn-search-submit:hover { background: #C2410C; }


/* =======================================================
   4. GRID SỰ KIỆN 
   ======================================================= */
.events-container { max-width: 1400px; margin: 0 auto; padding: 0 20px; }
.events-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 30px; }

.stagger-list-move { transition: transform 0.5s ease; }
.stagger-list-enter-active, .stagger-list-leave-active { transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.stagger-list-enter-from { opacity: 0; transform: translateY(30px) scale(0.95); }
.stagger-list-leave-to { opacity: 0; transform: scale(0.9); position: absolute; }

/* Empty state & Load more */
.empty-state-modern { text-align: center; padding: 100px 0; }
.empty-icon { width: 80px; height: 80px; background: #F3F4F6; color: #9CA3AF; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 20px; }
.empty-icon svg { width: 40px; height: 40px; }
.empty-state-modern h3 { font-size: 1.5rem; color: #111827; margin-bottom: 10px; }
.empty-state-modern p { color: #6B7280; margin-bottom: 24px; }
.btn-clear-filter { background: #111827; color: white; border: none; padding: 12px 30px; border-radius: 30px; font-weight: 700; cursor: pointer; transition: 0.3s; }
.btn-clear-filter:hover { background: #EA580C; }

.load-more-section { text-align: center; margin-top: 60px; }
.btn-load-more { background: transparent; border: 2px solid #E5E7EB; color: #4B5563; padding: 14px 40px; border-radius: 30px; font-weight: 800; font-size: 1rem; cursor: pointer; transition: 0.3s; }
.btn-load-more:hover { border-color: #111827; color: #111827; background: white; }

/* =======================================================
   🌟 HỆ THỐNG RESPONSIVE (KHẮC PHỤC DỒN CHỮ & ẨN HERO)
   ======================================================= */

@media (min-width: 1440px) {
  .events-hero-slider { height: 560px; }
  .hero-title { font-size: 3.5rem; }
  .events-grid { grid-template-columns: repeat(4, 1fr); } 
}

@media (max-width: 1024px) {
  /* ❌ ẨN HERO SLIDER TRÊN TABLET (Dưới 1024px) */
  .events-hero-slider { 
    display: none !important; 
  }
  
  /* Đẩy padding lên để bù đắp khoảng trống khi mất Hero */
  .events-page-premium {
    padding-top: 20px;
  }
  
  .events-grid { grid-template-columns: repeat(2, 1fr); }
  
  /* Giữ cho Toolbar đẹp trên Tablet */
  .events-toolbar-sticky { 
    top: 60px; /* Cân đối với Header hệ thống */
  }
}

@media (max-width: 768px) {
  /* ❌ ĐẢM BẢO ẨN HERO SLIDER TRÊN MOBILE */
  .events-hero-slider { 
    display: none !important; 
  }

  /* FIX TOOLBAR MOBILE: Chống dồn chữ */
  .events-toolbar-sticky { 
    position: relative; top: 0; padding: 15px; border-radius: 12px; margin: 10px;
  }
  .toolbar-inner { flex-direction: column; align-items: stretch; gap: 15px; }
  
  /* Thanh nút phân loại cuộn ngang */
  .segmented-wrapper {
    width: 100%;
    overflow-x: auto; 
    -ms-overflow-style: none; scrollbar-width: none; 
  }
  .segmented-wrapper::-webkit-scrollbar { display: none; }
  
  .segmented-control { 
    display: inline-flex; 
    width: max-content; /* Ép dài ra theo chữ */
    min-width: 100%;
  }
  .segment-btn { 
    flex: 1; 
    padding: 10px 20px; 
    font-size: 0.9rem; 
    text-align: center;
  }
  
  .search-box-modern { width: 100%; padding: 8px 16px; }
  
  .events-grid { grid-template-columns: 1fr; gap: 20px; padding: 0 10px; }
}

@media (max-width: 480px) {
  .segment-btn { padding: 8px 16px; font-size: 0.85rem; }
  .search-box-modern { padding: 6px 10px 6px 16px; }
  .btn-search-submit { padding: 8px 14px; font-size: 0.85rem; }
}
</style>