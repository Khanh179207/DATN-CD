<template>
  <header class="hero-wrapper" @mousemove="handleMouseMove" @mouseenter="pauseSlide" @mouseleave="resumeSlide"
    @touchstart.passive="handleTouchStart" @touchend.passive="handleTouchEnd">
    
    <div class="bg-overlay" :style="{ background: currentSlideData.bgGradient, opacity: 0.9 }"></div>
    
    <div class="bg-pattern"></div>

    <transition name="slide-fade" mode="out-in">
      <div class="container hero-content" :key="currentIndex">
        
        <button class="nav-btn prev" @click="prevSlide">❮</button>
        <button class="nav-btn next" @click="nextSlide">❯</button>

        <div class="text-side">
          <div class="pill-badge" :style="{ color: currentSlideData.themeColor, borderColor: currentSlideData.themeColor + '40' }">
            <span class="dot-pulse" :style="{ background: currentSlideData.themeColor }"></span>
            <span>{{ currentSlideData.badge }}</span>
          </div>
          
          <h1 class="hero-title">
            {{ currentSlideData.titleTop }} <br>
            <span class="text-highlight" :style="highlightStyle">
              {{ currentSlideData.titleHighlight }}
            </span>
          </h1>
          
          <div class="meta-info-line">
            <div class="meta-item">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
              <span>{{ currentSlideData.time }}</span>
            </div>
            <span class="meta-divider">•</span>
            <div class="meta-item">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"></path></svg>
              <span>{{ currentSlideData.difficulty }}</span>
            </div>
            <span class="meta-divider">•</span>
            <div class="meta-item views" :style="{ color: currentSlideData.themeColor }">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
              <span>{{ currentSlideData.views }} Views</span>
            </div>
          </div>

          <p class="hero-desc">
            {{ currentSlideData.desc }}
          </p>

          <div class="cta-group">
            <button class="btn-primary" :style="{ background: currentSlideData.themeColor }" @click="$emit('view-detail', currentSlideData.id)">
              View Recipe
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
            </button>
            <button class="btn-secondary" :style="secondaryBtnStyle">
              <div class="play-icon" :style="{ color: currentSlideData.themeColor, background: currentSlideData.themeColor + '20' }">▶</div>
              <span>Tutorial Video</span>
            </button>
          </div>
        </div>

        <div class="visual-side">
          <div class="composition-container" :style="parallaxStyle(20)">
            <div class="plate-wrapper">
              <div class="plate-border">
                <img :src="currentSlideData.mainImg" class="main-img" :alt="currentSlideData.titleHighlight">
                <div class="sauce-gloss"></div>
              </div>
              <div class="plate-shadow"></div>
            </div>

            <div class="float-placeholder obj-1" :style="parallaxStyle(35)">
               <div class="placeholder-content">
                   <img v-if="currentSlideData.float1.isImg" :src="currentSlideData.float1.src" class="float-img-real" alt="">
                   <span v-else>{{ currentSlideData.float1.text }}</span>
               </div>
            </div>

            <img v-if="currentSlideData.float2.isImg" :src="currentSlideData.float2.src" class="float-obj obj-2" :style="parallaxStyle(-25)" alt="Decoration">
             <div v-else class="float-placeholder obj-2" :style="parallaxStyle(-25)">
                <span>{{ currentSlideData.float2.text }}</span>
             </div>

            <div class="float-placeholder obj-3" :style="parallaxStyle(50)">
                <div class="placeholder-content">
                   <img v-if="currentSlideData.float3.isImg" :src="currentSlideData.float3.src" class="float-img-real" alt="">
                   <span v-else>{{ currentSlideData.float3.text }}</span>
               </div>
            </div>

            <div class="particle p1" :style="{ backgroundColor: currentSlideData.themeColor, ...parallaxStyle(60) }"></div>
            <div class="particle p2" :style="{ backgroundColor: '#333', ...parallaxStyle(40) }"></div>

            <div class="review-card-pro" :style="parallaxStyle(15)">
              <div class="user-info">
                <img :src="currentSlideData.review.avatar" alt="User">
                <div>
                  <div class="u-name">{{ currentSlideData.review.name }}</div>
                  <div class="u-role">VIP Member</div>
                </div>
              </div>
              <p class="u-comment">"{{ currentSlideData.review.comment }}"</p>
              <div class="stars" :style="{ color: currentSlideData.themeColor }">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <div class="dots-nav">
      <span v-for="(slide, index) in slides" :key="slide.id" class="dot" :class="{ active: index === currentIndex }" :style="{ '--dot-bg': index === currentIndex ? slide.themeColor : '#D1D5DB' }" @click="goToSlide(index)"></span>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { getLatestPosts, normalizePost } from '@/services/postService'

defineEmits(['view-detail'])

// Design templates per slot — only visual styling, content overridden from API
const slideTemplates = [
  {
    badge: "Recipe of the Week",
    titleTop: "Masterpiece",
    themeColor: "#EA580C",
    bgGradient: "linear-gradient(120deg, #FFFFFF 0%, #FFF7ED 100%)",
    fallbackImg: "https://images.unsplash.com/photo-1612874742237-6526221588e3?q=80&w=1000&auto=format&fit=crop",
    float1: { isImg: false, text: "Garlic" },
    float2: { isImg: true, src: "https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Basil.png/800px-Basil.png" },
    float3: { isImg: false, text: "Herb" },
    review: { name: "Khanh Nguyen", avatar: "https://ui-avatars.com/api/?name=KN&background=F97316&color=fff", comment: "Absolutely delicious! Every single person in my family loved it." }
  },
  {
    badge: "Weekend Favourite",
    titleTop: "Premium",
    themeColor: "#DC2626",
    bgGradient: "linear-gradient(120deg, #FFFFFF 0%, #FEF2F2 100%)",
    fallbackImg: "https://images.unsplash.com/photo-1600891964092-4316c288032e?q=80&w=1000&auto=format&fit=crop",
    float1: { isImg: false, text: "Spice" },
    float2: { isImg: false, text: "Herb" },
    float3: { isImg: false, text: "Sauce" },
    review: { name: "Minh Anh", avatar: "https://ui-avatars.com/api/?name=MA&background=DC2626&color=fff", comment: "Tender as butter — the sauce recipe is spot on!" }
  },
  {
    badge: "Eat Clean Menu",
    titleTop: "Refreshing",
    themeColor: "#16A34A",
    bgGradient: "linear-gradient(120deg, #FFFFFF 0%, #F0FDF4 100%)",
    fallbackImg: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=1000&auto=format&fit=crop",
    float1: { isImg: false, text: "Fresh" },
    float2: { isImg: false, text: "Greens" },
    float3: { isImg: false, text: "Healthy" },
    review: { name: "Lan Phuong", avatar: "https://ui-avatars.com/api/?name=LP&background=16A34A&color=fff", comment: "Light and delicious — perfect for any meal!" }
  }
]

const slides = ref(slideTemplates.map(t => ({
  id: null,
  ...t,
  titleHighlight: t.badge,
  desc: "Discover amazing recipes crafted with passion.",
  time: "30 Min", difficulty: "Easy", views: "0",
  mainImg: t.fallbackImg,
})))

// --- LOGIC ---
const currentIndex = ref(0)
const autoPlayInterval = ref(null)
const currentSlideData = computed(() => slides.value[currentIndex.value])

const highlightStyle = computed(() => ({
  color: currentSlideData.value.themeColor,
  filter: `drop-shadow(0 2px 10px ${currentSlideData.value.themeColor}33)`
}))

const secondaryBtnStyle = computed(() => ({ borderColor: '#F3F4F6' }))

const nextSlide = () => { currentIndex.value = (currentIndex.value + 1) % slides.value.length }
const prevSlide = () => { currentIndex.value = (currentIndex.value - 1 + slides.value.length) % slides.value.length }
const goToSlide = (index) => { currentIndex.value = index }

const startAutoPlay = () => { autoPlayInterval.value = setInterval(nextSlide, 5000) }
const stopAutoPlay = () => { clearInterval(autoPlayInterval.value) }
const pauseSlide = () => stopAutoPlay()
const resumeSlide = () => startAutoPlay()

onMounted(async () => {
  startAutoPlay()
  try {
    const posts = await getLatestPosts(3)
    if (posts && posts.length > 0) {
      slides.value = posts.slice(0, 3).map((dto, i) => {
        const p = normalizePost(dto)
        const tpl = slideTemplates[i]
        return {
          id: p.id,
          badge: tpl.badge,
          titleTop: tpl.titleTop,
          titleHighlight: p.title || tpl.badge,
          desc: p.description || 'Discover this amazing recipe.',
          time: p.cookTime ? p.cookTime + ' Min' : '30 Min',
          difficulty: p.difficulty || 'Easy',
          views: p.views ? (p.views > 999 ? (p.views / 1000).toFixed(1) + 'K' : String(p.views)) : '0',
          themeColor: tpl.themeColor,
          bgGradient: tpl.bgGradient,
          mainImg: p.image || tpl.fallbackImg,
          float1: tpl.float1,
          float2: tpl.float2,
          float3: tpl.float3,
          review: { name: p.author?.name || tpl.review.name, avatar: p.author?.avatar || tpl.review.avatar, comment: tpl.review.comment }
        }
      })
    }
  } catch { /* fallback already set */ }
})
onUnmounted(() => { stopAutoPlay() })

const mouseX = ref(0)
const mouseY = ref(0)
const handleMouseMove = (e) => {
  const { innerWidth, innerHeight } = window
  mouseX.value = (e.clientX / innerWidth) - 0.5
  mouseY.value = (e.clientY / innerHeight) - 0.5
}
const parallaxStyle = (factor) => ({ transform: `translate(${mouseX.value * factor * 0.5}px, ${mouseY.value * factor * 0.5}px)` })

// --- TOUCH SWIPE ---
const touchStartX = ref(0)
const touchStartY = ref(0)
const handleTouchStart = (e) => {
  touchStartX.value = e.touches[0].clientX
  touchStartY.value = e.touches[0].clientY
  stopAutoPlay()
}
const handleTouchEnd = (e) => {
  const dx = e.changedTouches[0].clientX - touchStartX.value
  const dy = e.changedTouches[0].clientY - touchStartY.value
  if (Math.abs(dx) > 50 && Math.abs(dx) > Math.abs(dy)) {
    dx < 0 ? nextSlide() : prevSlide()
  }
  startAutoPlay()
}
</script>

<style scoped>
.hero-wrapper {
  position: relative;
  min-height: 650px;
  height: auto; 
  padding: 0;
  display: flex; align-items: center; 
  overflow: hidden;
  font-family: var(--font-ui);
  color: #1C1917;
  background: transparent; 
  transition: background var(--duration-normal) var(--ease-out);
  /* touch-action: pan-y ensures vertical scroll isn't blocked */
  touch-action: pan-y;
}

.bg-overlay { position: absolute; inset: 0; z-index: -1; transition: background 0.5s ease; }
.bg-pattern { position: absolute; inset: 0; background-image: radial-gradient(#9CA3AF 1px, transparent 1px); background-size: 30px 30px; opacity: 0.2; z-index: 0; }

.container {
  width: 100%; max-width: 1300px; 
  margin: 0px auto; padding: 0 40px;
  display: flex; align-items: center; justify-content: space-between; gap: 60px;
  position: relative; z-index: 2;
  height: 100%; 
}

/* --- TRANSITION --- */
.slide-fade-enter-active, .slide-fade-leave-active {
  transition: opacity var(--duration-normal) var(--ease-out),
              transform var(--duration-normal) var(--ease-out);
}
.slide-fade-enter-from { opacity: 0; transform: translateX(30px); }
.slide-fade-leave-to { opacity: 0; transform: translateX(-30px); }

/* --- TEXT SIDE --- */
.text-side { flex: 1; }
.pill-badge { display: inline-flex; align-items: center; gap: 10px; padding: 8px 18px; background: white; border-radius: 100px; border: 1px solid; font-size: 0.9rem; font-weight: 700; margin-bottom: 20px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); transition: color var(--duration-fast) var(--ease-out); }
.dot-pulse { width: 8px; height: 8px; border-radius: 50%; animation: pulse 2s ease-in-out infinite; will-change: opacity, transform; }
.hero-title { font-family: var(--font-display); font-size: 4.5rem; line-height: 1.15; font-weight: 800; margin-bottom: 16px; letter-spacing: -1px; color: #0F172A; }
.text-highlight { display: inline-block; font-style: italic; transition: background var(--duration-normal) var(--ease-out), filter var(--duration-normal) var(--ease-out); }
.meta-info-line { display: flex; align-items: center; gap: 15px; margin-bottom: 24px; color: #64748B; font-weight: 600; font-size: 0.95rem; }
.meta-item { display: flex; align-items: center; gap: 8px; }
.meta-item svg { color: #94A3B8; }
.meta-divider { color: #CBD5E1; }
.meta-item.views svg { transition: color 0.3s; }
.hero-desc { font-size: 1.1rem; line-height: 1.6; color: #4B5563; max-width: 520px; margin-bottom: 32px; font-weight: 500; }
.cta-group { display: flex; gap: 16px; }
.btn-primary { color: white; border: none; padding: 16px 32px; border-radius: 14px; font-weight: 700; font-size: 1rem; cursor: pointer; display: flex; align-items: center; gap: 10px; transition: transform var(--duration-fast) var(--ease-out), filter var(--duration-fast) var(--ease-out), box-shadow var(--duration-fast) var(--ease-out); box-shadow: 0 10px 25px -5px rgba(0,0,0,0.2); will-change: transform; }
.btn-primary:hover { transform: translateY(-3px); filter: brightness(1.1); }
.btn-primary:active { transform: translateY(-1px) scale(0.98); }
.btn-secondary { background: white; border: 2px solid #F3F4F6; color: #1C1917; padding: 16px 24px; border-radius: 14px; font-weight: 700; font-size: 1rem; cursor: pointer; display: flex; align-items: center; gap: 10px; transition: border-color var(--duration-fast) var(--ease-out), background var(--duration-fast) var(--ease-out); }
.btn-secondary:hover { border-color: #D1D5DB; background: #F9FAFB; }
.play-icon { width: 24px; height: 24px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.7rem; }

/* --- VISUAL SIDE --- */
.visual-side { flex: 1.2; height: 700px; display: flex; align-items: center; justify-content: center; }
.composition-container { position: relative; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }
.plate-wrapper { position: relative; z-index: 2; transition: transform 0.1s ease; }
.plate-border { width: 450px; height: 450px; border-radius: 50%; overflow: hidden; border: 10px solid #FFFFFF; position: relative; z-index: 2; box-shadow: 0 20px 50px rgba(0,0,0,0.1); }
.main-img { width: 100%; height: 100%; object-fit: cover; transform: scale(1.15); }
.sauce-gloss { position: absolute; inset: 0; pointer-events: none; background: radial-gradient(circle at 30% 30%, rgba(255,255,255,0.2) 0%, transparent 60%); }
.plate-shadow { position: absolute; bottom: 10px; left: 50%; transform: translateX(-50%); width: 380px; height: 35px; background: rgba(0,0,0,0.3); filter: blur(25px); z-index: 1; border-radius: 50%; }
.float-obj { position: absolute; z-index: 3; pointer-events: none; filter: drop-shadow(0 15px 25px rgba(0,0,0,0.15)); }
.obj-2 { width: 100px; top: 5%; right: 5%; transform: rotate(30deg); }
.float-placeholder { position: absolute; z-index: 3; pointer-events: none; background: rgba(255, 255, 255, 0.6); border: 2px dashed #CBD5E1; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #94A3B8; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
.placeholder-content { display: flex; flex-direction: column; align-items: center; gap: 5px; }
.placeholder-content span { font-weight: 700; font-size: 0.75rem; }
.float-img-real { width: 100%; height: 100%; object-fit: contain; transform: scale(1.2); }
.obj-1 { width: 90px; height: 70px; bottom: 10%; left: 5%; transform: rotate(-15deg); }
.obj-2.float-placeholder { width: 100px; height: 60px; top: 5%; right: 5%; transform: rotate(30deg); }
.obj-3 { width: 80px; height: 80px; bottom: 25%; right: 0%; transform: rotate(10deg); }
.review-card-pro { position: absolute; bottom: 15%; right: -30px; z-index: 4; background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(20px); padding: 16px 20px; border-radius: 18px; border: 1px solid white; width: 250px; box-shadow: 0 20px 50px -10px rgba(0,0,0,0.15); animation: floatCard 6s ease-in-out infinite; will-change: transform; }
.user-info { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.user-info img { width: 36px; height: 36px; border-radius: 50%; border: 2px solid white; }
.u-name { font-size: 0.9rem; font-weight: 700; color: #0F172A; }
.u-role { font-size: 0.7rem; color: #64748B; font-weight: 600; }
.u-comment { font-size: 0.85rem; color: #4B5563; font-style: italic; margin-bottom: 8px; line-height: 1.5; }
.stars { font-size: 0.85rem; letter-spacing: 2px; }

/* NAVIGATION BUTTONS */
.nav-btn {
  position: absolute; 
  top: 50%; transform: translateY(-50%);
  width: 50px; height: 50px; border-radius: 50%; 
  background: white; border: 1px solid #E5E7EB; color: #4B5563;
  cursor: pointer; 
  z-index: 1000;
  font-size: 1.2rem;
  display: flex; align-items: center; justify-content: center;
  min-width: 44px; min-height: 44px; /* touch target */
  transition: background var(--duration-fast) var(--ease-out),
              color     var(--duration-fast) var(--ease-out),
              transform var(--duration-fast) var(--ease-out);
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}
.nav-btn:hover { background: #1C1917; color: white; border-color: #1C1917; transform: translateY(-50%) scale(1.08); }
.prev { left: 10px; } 
.next { right: 10px; }

@media (max-width: 768px) { .nav-btn { display: none; } }

.dots-nav { position: absolute; bottom: 20px; left: 50%; transform: translateX(-50%); display: flex; gap: 2px; z-index: 10; align-items: center; }
/* Visual dot is 10px but touch area is 44×44 via padding + size */
.dot {
  width: 44px; height: 44px; display: flex; align-items: center; justify-content: center;
  cursor: pointer; background: transparent; border-radius: 50%;
}
.dot::after {
  content: ''; width: 10px; height: 10px; border-radius: 50%;
  background: var(--dot-bg, #D1D5DB);
  transition: transform var(--duration-fast) var(--ease-out), background var(--duration-fast) var(--ease-out);
  display: block;
}
.dot.active::after { transform: scale(1.4); }
@keyframes pulse { 0%, 100% { opacity: 1; transform: scale(1); } 50% { opacity: 0.6; transform: scale(1.2); } }
@keyframes floatCard { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-12px); } }

/* Respect reduced motion preference */
@media (prefers-reduced-motion: reduce) {
  .review-card-pro { animation: none; }
  .dot-pulse { animation: none; }
  .slide-fade-enter-active, .slide-fade-leave-active { transition: opacity var(--duration-fast) linear; }
  .slide-fade-enter-from, .slide-fade-leave-to { opacity: 0; transform: none; }
}

@media (max-width: 1200px) { .hero-title { font-size: 3.8rem; } .plate-border { width: 380px; height: 380px; } .visual-side { height: 500px; } }
@media (max-width: 900px) { 
  .hero-wrapper { height: auto; padding: 50px 0; flex-direction: column; } 
  .container { flex-direction: column; gap: 40px; } 
  .text-side { text-align: center; display: flex; flex-direction: column; align-items: center; } 
  .visual-side { width: 100%; height: 450px; } 
  .plate-border { width: 300px; height: 300px; } 
  .review-card-pro, .float-placeholder { display: none; } 
  .nav-btn { display: none; } 
}
</style>