<template>
  <div class="events-page-premium">
    
    <section class="events-hero-slider">
      <div v-if="loading" class="slider-loading">
        <div class="spinner-ring"></div>
      </div>
      
      <div v-else class="slider-container">
        <transition-group name="fade-slide" tag="div" class="slides-wrapper">
          <div 
            v-for="(slide, index) in topThreeEvents" 
            :key="slide.id"
            v-show="currentSlide === index"
            class="slide-item"
            :style="{ backgroundImage: `url(${slide.image})` }"
          >
            <div class="hero-overlay"></div>
            
            <div class="hero-content glass-panel">
              <div class="status-badge" :class="slide.category">
                <span class="pulse-dot"></span>
                {{ slide.category === 'active' ? $t('events.hero_active') : (slide.category === 'upcoming' ? $t('events.hero_upcoming') : $t('events.hero_ended')) }}
              </div>
              
              <h1 class="hero-title">{{ slide.title }}</h1>
              <p class="hero-desc">{{ $t('events.hero_desc') }}</p>
              
              <div class="hero-meta">
                <div class="meta-item"><span class="icon">📅</span> {{ slide.time }}</div>
                <div class="meta-item"><span class="icon">👥</span> {{ $t('events.entries_count', { count: slide.totalAttendees }) }}</div>
              </div>
              
              <button class="btn-hero-primary" v-if="slide.category !== 'ended'">
                {{ $t('events.register') }}
                <svg class="arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
              </button>
            </div>
          </div>
        </transition-group>

        <button class="nav-btn prev-btn" @click="prevSlide">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"></polyline></svg>
        </button>
        <button class="nav-btn next-btn" @click="nextSlide">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"></polyline></svg>
        </button>

        <div class="slider-indicators">
          <button 
            v-for="(_, index) in topThreeEvents" 
            :key="index" 
            class="indicator-dot" 
            :class="{ active: currentSlide === index }"
            @click="goToSlide(index)"
            :aria-label="t('common.go_to_slide', { number: index + 1 })"
          >
            <span class="progress-bar"></span>
          </button>
        </div>
      </div>
    </section>

    <div class="events-toolbar-sticky">
      <div class="toolbar-inner">
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

        <div class="search-box-modern">
          <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
          <input type="text" v-model="searchQuery" :placeholder="$t('events.find_events')">
        </div>
      </div>
    </div>

    <div class="events-container">
      <transition-group name="stagger-list" tag="div" class="events-grid">
        <EventCard 
          v-for="(evt, index) in filteredEvents" 
          :key="evt.id" 
          :event="evt" 
          class="grid-item"
          :style="{ transitionDelay: `${index * 0.05}s` }"
        />
      </transition-group>

      <div v-if="filteredEvents.length === 0 && !loading" class="empty-state-modern">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
        </div>
        <h3>{{ $t('events.empty_title') }}</h3>
        <p>{{ $t('events.empty_desc') }}</p>
        <button class="btn-clear-filter" @click="currentFilter = 'all'; searchQuery = ''">{{ $t('events.clear_filter') }}</button>
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

const { t, locale } = useI18n()
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

// Slider States
const currentSlide = ref(0)
let slideInterval = null

const fallbackImages = [
  'https://images.unsplash.com/photo-1556761175-5973dc0f32e7?q=80&w=2032',
  'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?q=80&w=2000',
  'https://images.unsplash.com/photo-1528605105345-5344ea20e269?q=80&w=2000'
]

const formatMonthBadge = (dateObj) => {
  if (!dateObj) return '??'
  return new Intl.DateTimeFormat(locale.value === 'vi' ? 'vi-VN' : 'en-US', { month: 'short' }).format(dateObj).toUpperCase()
}

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

  // Thời gian kết thúc cuối cùng của sự kiện là lúc ĐÓNG VOTE
  const finalEndDate = voteEnd && voteEnd > end ? voteEnd : end;

  // Lấy cờ force end (phòng trường hợp backend trả về tên khác)
  const isForceEnded = e.isForceEnded ?? e.forceEnded ?? e.IsForceEnded ?? 0;

  // Xử lý trạng thái hiển thị
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
    month: formatMonthBadge(start),
    day: start ? String(start.getDate()).padStart(2, '0') : '??',
    time: start && finalEndDate ? `${formatDate(start)} → ${formatDate(finalEndDate)}` : t('events.tba'),
    rawEndAt: finalEndDate, 
    location: t('events.online_location'),
    type: 'online',
    typeLabel: t('events.online_type'),
    isJoined: false,
    attendees: [],
    totalAttendees: Number(e.postCount) || 0, 
    category: currentStatus,
    winner: e.winner || null
  }
}

// Logic Slider thông minh
const resetSliderInterval = () => {
  if (slideInterval) clearInterval(slideInterval)
  slideInterval = setInterval(nextSlide, 6000) 
}

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % topThreeEvents.value.length
  resetSliderInterval()
}
const prevSlide = () => {
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
    
    let rawData = [];
    if (Array.isArray(response)) rawData = response;
    else if (response && Array.isArray(response.content)) rawData = response.content;
    else if (response && Array.isArray(response.data)) rawData = response.data;
    
    // 🔥 LƯỚI LỌC CÚ CHỐT: Bắt ép kiểu Number và soi bằng F12
    const validData = rawData.filter(e => {
      let activeVal = e.isActive !== undefined ? e.isActive : (e.active !== undefined ? e.active : e.IsActive);
      
      // MÁY QUÉT: Báo cáo ra Console cho sếp xem
      console.log(`[DEBUG EVENT] Tên: ${e.eventName || e.title} | Giá trị isActive nhận được từ API:`, activeVal);

      if (activeVal !== undefined && activeVal !== null) {
        // Ép sang số: Nếu bằng 0 thì ẩn đi (return false)
        return Number(activeVal) !== 0;
      }
      
      // Nếu nhảy xuống đây nghĩa là API trả thiếu biến isActive
      return true; 
    })
    
    events.value = validData.map((e, index) => normalizeEvent(e, index))
    
    if (topThreeEvents.value.length > 1) {
      slideInterval = setInterval(nextSlide, 6000)
    }
  } catch (err) {
    console.warn('EventList: load error', err)
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  if (slideInterval) clearInterval(slideInterval)
})

const topThreeEvents = computed(() => {
  const validEvents = events.value.filter(e => e.category !== 'ended')
  return validEvents.length > 0 ? validEvents.slice(0, 3) : events.value.slice(0, 3)
})

const filteredEvents = computed(() => {
  let list = events.value
  if (currentFilter.value !== 'all') {
    list = list.filter(e => e.category === currentFilter.value)
  }
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(e => e.title?.toLowerCase().includes(q) || e.location?.toLowerCase().includes(q))
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
}

.events-hero-slider {
  position: relative;
  height: 480px; 
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
  display: flex; align-items: flex-end; padding: 60px;
}

.hero-overlay {
  position: absolute; inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.9) 0%, rgba(0,0,0,0.4) 40%, rgba(0,0,0,0.1) 100%);
  z-index: 1;
}

.glass-panel {
  position: relative; z-index: 2;
  max-width: 650px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 40px;
  border-radius: 24px;
  color: white;
  transform: translateY(0);
  animation: slideUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.status-badge {
  display: inline-flex; align-items: center; gap: 8px;
  background: rgba(0,0,0,0.4); padding: 6px 14px; border-radius: 20px;
  font-size: 0.75rem; font-weight: 800; letter-spacing: 1px; margin-bottom: 20px;
}
.status-badge.active .pulse-dot { width: 8px; height: 8px; border-radius: 50%; background: #22C55E; box-shadow: 0 0 8px #22C55E; animation: pulse 2s infinite; }
.status-badge.upcoming .pulse-dot { width: 8px; height: 8px; border-radius: 50%; background: #3B82F6; }

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(34, 197, 94, 0.7); }
  70% { box-shadow: 0 0 0 6px rgba(34, 197, 94, 0); }
  100% { box-shadow: 0 0 0 0 rgba(34, 197, 94, 0); }
}

.hero-title { font-family: 'Playfair Display', serif; font-size: 2.8rem; line-height: 1.2; margin: 0 0 15px; font-weight: 800; }
.hero-desc { font-size: 1.1rem; color: #D1D5DB; line-height: 1.6; margin-bottom: 25px; }

.hero-meta { display: flex; gap: 24px; font-weight: 700; margin-bottom: 30px; font-size: 0.95rem; color: #FFF; }
.meta-item { display: flex; align-items: center; gap: 8px; }

.btn-hero-primary {
  display: inline-flex; align-items: center; gap: 10px;
  background: #EA580C; color: white; border: none;
  padding: 14px 32px; border-radius: 30px; font-weight: 800; font-size: 1rem;
  cursor: pointer; transition: all 0.3s;
}
.btn-hero-primary:hover { background: #C2410C; transform: scale(1.05); }
.btn-hero-primary .arrow { width: 18px; height: 18px; transition: transform 0.3s; }
.btn-hero-primary:hover .arrow { transform: translateX(5px); }

.nav-btn {
  position: absolute; top: 50%; transform: translateY(-50%); z-index: 10;
  width: 50px; height: 50px; border-radius: 50%; border: 1px solid rgba(255,255,255,0.3);
  background: rgba(0,0,0,0.3); color: white; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  backdrop-filter: blur(8px); transition: 0.3s; opacity: 0; 
}
.events-hero-slider:hover .nav-btn { opacity: 1; }
.nav-btn:hover { background: white; color: #111827; }
.prev-btn { left: 30px; }
.next-btn { right: 30px; }

.slider-indicators {
  position: absolute; bottom: 30px; right: 60px; z-index: 10;
  display: flex; gap: 10px;
}
.indicator-dot {
  width: 40px; height: 4px; background: rgba(255,255,255,0.3); border: none; border-radius: 4px;
  cursor: pointer; padding: 0; position: relative; overflow: hidden;
}
.indicator-dot.active .progress-bar {
  position: absolute; inset: 0; background: #EA580C;
  animation: progressFill 6s linear forwards;
}
@keyframes progressFill { from { transform: scaleX(0); transform-origin: left; } to { transform: scaleX(1); transform-origin: left; } }

.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.8s ease-in-out; }
.fade-slide-enter-from { opacity: 0; transform: scale(1.05); }
.fade-slide-leave-to { opacity: 0; }

.slider-loading { display: flex; justify-content: center; align-items: center; height: 100%; }
.spinner-ring { width: 50px; height: 50px; border: 4px solid rgba(255,255,255,0.1); border-top-color: #EA580C; border-radius: 50%; animation: spin 1s infinite linear; }
@keyframes spin { 100% { transform: rotate(360deg); } }

.events-toolbar-sticky {
  position: sticky; top: 0; z-index: 50;
  background: rgba(250, 250, 250, 0.9); backdrop-filter: blur(12px);
  padding: 20px; margin-bottom: 20px; border-bottom: 1px solid #F3F4F6;
}

.toolbar-inner {
  max-width: 1400px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 20px;
}

.segmented-control {
  display: flex; background: #E5E7EB; padding: 6px; border-radius: 100px;
}
.segment-btn {
  background: transparent; border: none; padding: 10px 28px; border-radius: 100px;
  font-weight: 800; color: #6B7280; font-size: 0.95rem; cursor: pointer; transition: 0.3s;
}
.segment-btn.active { background: #FFFFFF; color: #111827; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }

.search-box-modern {
  display: flex; align-items: center; gap: 12px;
  background: #FFFFFF; border: 1px solid #E5E7EB; border-radius: 100px;
  padding: 10px 24px; width: 350px; transition: 0.3s; box-shadow: inset 0 2px 4px rgba(0,0,0,0.02);
}
.search-box-modern:focus-within { border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234,88,12,0.1); }
.search-icon { width: 18px; height: 18px; color: #9CA3AF; }
.search-box-modern input { border: none; outline: none; width: 100%; font-size: 1rem; color: #111827; background: transparent; }

.events-container { max-width: 1400px; margin: 0 auto; padding: 0 20px; }
.events-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 30px; }

.stagger-list-move { transition: transform 0.5s ease; }
.stagger-list-enter-active, .stagger-list-leave-active { transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.stagger-list-enter-from { opacity: 0; transform: translateY(30px) scale(0.95); }
.stagger-list-leave-to { opacity: 0; transform: scale(0.9); position: absolute; }

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

@media (max-width: 1024px) {
  .events-hero-slider { height: 400px; }
  .hero-title { font-size: 2.2rem; }
  .slider-indicators { right: 30px; }
}
@media (max-width: 768px) {
  .events-hero-slider { height: auto; min-height: 450px; margin: 10px; border-radius: 20px; }
  .slide-item { padding: 30px 20px; align-items: flex-end; }
  .glass-panel { padding: 25px; border-radius: 16px; }
  .hero-title { font-size: 1.8rem; }
  .events-toolbar-sticky { position: relative; padding: 15px 20px; }
  .toolbar-inner { flex-direction: column; align-items: stretch; }
  .segmented-control { overflow-x: auto; white-space: nowrap; }
  .search-box-modern { width: 100%; }
  .nav-btn { display: none; }
  .slider-indicators { left: 50%; right: auto; transform: translateX(-50%); bottom: 15px; }
}
</style>