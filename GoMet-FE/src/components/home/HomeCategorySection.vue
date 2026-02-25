<template>
  <section class="category-section" ref="sectionRef">
    
    <div class="section-header fade-in-up">
      <div class="header-left">
        <span class="sub-label">{{ $t('home.explore') }}</span>
        <h2 class="section-heading">{{ $t('home.categories') }}</h2>
      </div>
      
      <div class="nav-controls">
        <button class="nav-btn prev" @click="scroll('left')">❮</button>
        <button class="nav-btn next" @click="scroll('right')">❯</button>
      </div>
    </div>

    <div 
      class="carousel-wrapper" 
      ref="carouselRef"
      @scroll="handleScroll"
    >
      <div class="carousel-track">
        
        <div 
          class="cat-card" 
          v-for="(cat, index) in categories" 
          :key="index"
          :class="{ 'active': activeIndex === index }"
          @click="handleFilter(cat.slug)"
          @mouseenter="activeIndex = index"
        >
          <div class="card-inner">
            <div class="card-image-wrapper">
              <img 
                :src="cat.image" 
                loading="lazy" 
                :alt="cat.name"
                class="parallax-img"
              >
              <div class="overlay-gradient"></div>
            </div>
            
            <div class="glass-tag count-tag">{{ cat.count }}</div>

            <div class="glass-pill name-pill">
              <span class="cat-name">{{ cat.name }}</span>
            </div>
          </div>
        </div>

        <div class="cat-card view-more-card" @click="goToSearch">
          <div class="card-inner view-more-inner">
            <div class="view-more-content">
              <div class="icon-pulse-box">➜</div>
              <h3>{{ $t('home.view_all') }}</h3>
            </div>
          </div>
        </div>

      </div>
    </div>

  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getCategories } from '@/services/categoryService'

const router = useRouter()
const { t } = useI18n()
const carouselRef = ref(null)
const activeIndex = ref(0)
const apiCategories = ref([])

// Fallback image map for known category names
const fallbackImages = {
  'dessert': 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?q=80&w=600&fit=crop',
  'drinks': 'https://images.unsplash.com/photo-1544145945-f90425340c7e?q=80&w=600&fit=crop',
  'healthy': 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?q=80&w=600&fit=crop',
  'main': 'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?q=80&w=600&fit=crop',
  'snack': 'https://images.unsplash.com/photo-1526230427044-d092040d48dc?q=80&w=600&fit=crop',
  'veg': 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=600&fit=crop',
  'breakfast': 'https://images.unsplash.com/photo-1533089860892-a7c6f0a88666?q=80&w=600&fit=crop',
  'soup': 'https://images.unsplash.com/photo-1547592166-23ac45744acd?q=80&w=600&fit=crop',
  'default': 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=600&fit=crop',
}

const getCatImage = (name) => {
  const key = name.toLowerCase()
  for (const [k, v] of Object.entries(fallbackImages)) {
    if (key.includes(k)) return v
  }
  return fallbackImages.default
}

onMounted(async () => {
  try {
    const cats = await getCategories()
    apiCategories.value = cats
  } catch { /* keep empty */ }
})

const handleFilter = (slug) => { router.push({ name: 'Search', query: { cat: slug } }) }
const goToSearch = () => { router.push({ name: 'Search' }) }

const scroll = (direction) => {
  if (!carouselRef.value) return;
  const cardWidth = 240;
  const scrollAmount = direction === 'left' ? -cardWidth : cardWidth;
  carouselRef.value.scrollBy({ left: scrollAmount, behavior: 'smooth' });
}

const handleScroll = () => {
  if (!carouselRef.value) return;
  const scrollLeft = carouselRef.value.scrollLeft;
  const cardWidth = 240; 
  activeIndex.value = Math.round(scrollLeft / cardWidth);
}

const categories = computed(() => {
  if (apiCategories.value.length > 0) {
    return apiCategories.value.map(c => ({
      name: c.categoryName,
      slug: c.categoryName.toLowerCase().replace(/\s+/g, '-'),
      count: (c.postCount != null ? c.postCount : '?') + '+',
      image: getCatImage(c.categoryName)
    }))
  }
  // Fallback static
  return [
    { name: t('home.cat_dessert'), slug: 'dessert', count: '150+', image: fallbackImages.dessert },
    { name: t('home.cat_drinks'),  slug: 'drinks',  count: '60+',  image: fallbackImages.drinks },
    { name: t('home.cat_healthy'), slug: 'healthy', count: '85+',  image: fallbackImages.healthy },
    { name: t('home.cat_main'),    slug: 'main-course', count: '300+', image: fallbackImages.main },
    { name: t('home.cat_snacks'),  slug: 'snacks', count: '200+',  image: fallbackImages.snack },
    { name: t('home.cat_veg'),     slug: 'vegetarian', count: '120+', image: fallbackImages.veg },
  ]
})
</script>

<style scoped>
/* --- CONTAINER CHÍNH --- */
.category-section {
  background: transparent;
  margin: 20px 0;
  padding: 10px 0;
  width: 100%;
  font-family: var(--font-ui);
  position: relative;
}

/* --- HEADER --- */
.section-header {
  display: flex; align-items: flex-end; justify-content: space-between;
  padding: 0 var(--space-10); margin-bottom: var(--space-6);
}
.sub-label {
  display: block; font-size: var(--text-xs); color: var(--color-primary-600); font-weight: var(--font-extrabold);
  text-transform: uppercase; margin-bottom: var(--space-1); letter-spacing: 1.5px;
}
.section-heading {
  font-family: var(--font-display);
  font-size: var(--text-3xl); color: var(--color-neutral-900); margin: 0; line-height: var(--leading-none);
}

/* Navigation Buttons */
.nav-controls { display: flex; gap: var(--space-3); }
.nav-btn {
  width: 40px; height: 40px; border-radius: var(--radius-full);
  border: 1px solid var(--color-neutral-200); background: var(--color-neutral-0);
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  font-size: var(--text-sm); color: var(--color-neutral-900);
  box-shadow: var(--shadow-xs);
  /* Use composited transform only, not box-shadow */
  transition: background var(--duration-fast) var(--ease-out),
              transform  var(--duration-fast) var(--ease-spring),
              color      var(--duration-fast) var(--ease-out);
}
.nav-btn:hover {
  background: var(--color-primary-600); color: var(--color-neutral-0); border-color: var(--color-primary-600);
  transform: scale(1.1);
}

/* --- CAROUSEL --- */
.carousel-wrapper {
  overflow-x: auto;
  scroll-behavior: smooth;
  scrollbar-width: none;
  /* Padding 40px để chứa hiệu ứng zoom không bị cắt */
  padding: 20px 40px 50px 40px; 
  scroll-snap-type: x mandatory; 
}
.carousel-wrapper::-webkit-scrollbar { display: none; }

.carousel-track { display: flex; gap: 24px; }

/* --- CARD VIP PRO --- */
.cat-card {
  min-width: 220px; height: 300px;
  position: relative; cursor: pointer;
  scroll-snap-align: start;
  perspective: 1000px;
  /* Promote to own layer so hover changes don’t repaint the whole page */
  will-change: transform;
  transition: transform var(--duration-normal) var(--ease-spring);
}

.card-inner {
  width: 100%; height: 100%;
  border-radius: var(--radius-xl); overflow: hidden;
  position: relative;
  background: var(--color-neutral-0);
  box-shadow: var(--shadow-md);
  /* Explicit props only — never 'all' */
  transition: transform  var(--duration-normal) var(--ease-out),
              box-shadow var(--duration-normal) var(--ease-out),
              border     var(--duration-normal) var(--ease-out);
}

/* Ảnh Parallax */
.card-image-wrapper { width: 100%; height: 100%; overflow: hidden; position: relative; }
.parallax-img {
  width: 100%; height: 100%; object-fit: cover;
  transition: transform var(--duration-slower) var(--ease-out);
}
.overlay-gradient {
  position: absolute; inset: 0;
  /* Gradient đậm hơn ở đáy để làm nổi bật chữ */
  background: linear-gradient(180deg, rgba(0,0,0,0) 40%, rgba(0,0,0,0.7) 100%);
  z-index: 1;
}

/* --- GLASSMORPHISM ELEMENTS --- */
/* Tag số lượng */
.glass-tag {
  position: absolute; top: 15px; right: 15px; z-index: 2;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white; font-size: 0.75rem; font-weight: 800;
  padding: 6px 12px; border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  text-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

/* Tên danh mục (Pill) */
.glass-pill {
  position: absolute; bottom: 25px; left: 50%; transform: translateX(-50%); z-index: 2;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(15px); -webkit-backdrop-filter: blur(15px);
  padding: var(--space-3) var(--space-6); border-radius: var(--radius-full);
  box-shadow: var(--shadow-lg);
  white-space: nowrap;
  border: 1px solid rgba(255, 255, 255, 0.8);
  /* Only transition composited properties + background */
  transition: background var(--duration-normal) var(--ease-out),
              transform  var(--duration-normal) var(--ease-spring),
              border-color var(--duration-normal) var(--ease-out);
}
.cat-name { font-size: var(--text-base); font-weight: var(--font-extrabold); color: var(--color-neutral-900); letter-spacing: -0.5px; }

/* --- INTERACTIVE STATES (HOVER) --- */
.cat-card:hover .card-inner {
  transform: translateY(-12px);
  box-shadow: var(--shadow-primary-lg);
  border: 1px solid rgba(234, 88, 12, 0.2);
}

.cat-card:hover .parallax-img { transform: scale(1.1); }

.cat-card:hover .glass-pill {
  background: var(--color-primary-600);
  transform: translateX(-50%) scale(1.05);
  border-color: var(--color-primary-600);
}
.cat-card:hover .cat-name { color: var(--color-neutral-0); }

/* --- VIEW MORE CARD --- */
.view-more-inner {
  background: var(--color-primary-50);
  border: 2px dashed var(--color-primary-300);
  display: flex; align-items: center; justify-content: center;
  box-shadow: none !important;
}
.view-more-content { text-align: center; }
.icon-pulse-box {
  width: 50px; height: 50px; border-radius: var(--radius-full);
  background: var(--color-neutral-0); color: var(--color-primary-600);
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto var(--space-3); font-size: var(--text-xl);
  box-shadow: var(--shadow-primary-sm);
  /* Composited pulse: scale only, no box-shadow animation */
  animation: pulseIcon 2.5s var(--ease-in-out) infinite;
}

/* ⚡ Composited-only pulse using scale + opacity */
@keyframes pulseIcon {
  0%, 100% { transform: scale(1);    opacity: 1; }
  50%       { transform: scale(1.12); opacity: 0.85; }
}

.view-more-card h3 { font-family: var(--font-ui); font-size: var(--text-base); font-weight: var(--font-extrabold); color: var(--color-primary-700); margin: 0; }

.view-more-card:hover .view-more-inner {
  background: var(--color-primary-100); border-color: var(--color-primary-600); 
  transform: translateY(-5px);
}
.view-more-card:hover .icon-pulse-box { background: var(--color-primary-600); color: var(--color-neutral-0); }

@media (max-width: 768px) {
  .section-header { padding: 0 var(--space-5); margin-bottom: var(--space-4); }
  .carousel-wrapper { padding: var(--space-3) var(--space-5) var(--space-8) var(--space-5); }
  .cat-card { min-width: 160px; height: 240px; }
  .nav-controls { display: none; }
}
</style>