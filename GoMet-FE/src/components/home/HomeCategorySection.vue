<template>
  <section class="category-section" ref="sectionRef">
    
    <div class="section-header fade-in-up">
      <div class="header-left">
        <span class="sub-label">{{ $t('home.explore') }}</span>
        <h2 class="section-heading">{{ $t('home.categories') }}</h2>
      </div>
      
      <div class="nav-controls" v-if="!loading">
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
        
        <template v-if="loading">
          <div class="cat-card" v-for="n in 5" :key="'skel-' + n">
            <div class="card-inner skeleton-loading"></div>
          </div>
        </template>

        <template v-else>
          <div 
            class="cat-card" 
            v-for="(cat, index) in displayCategories" 
            :key="cat.id"
            :class="{ 'active': activeIndex === index }"
            @click="handleFilter(cat.id, cat.name)"
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
        </template>

      </div>
    </div>

  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import api from '@/services/api.js'

const router = useRouter()
const { t } = useI18n()
const carouselRef = ref(null)
const activeIndex = ref(0)
const loading = ref(true)
const apiCategories = ref([])

const fallbackImages = {
  'dessert': 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?q=80&w=600&fit=crop',
  'drinks': 'https://images.unsplash.com/photo-1544145945-f90425340c7e?q=80&w=600&fit=crop',
  'healthy': 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?q=80&w=600&fit=crop',
  'main': 'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?q=80&w=600&fit=crop',
  'veg': 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=600&fit=crop',
  'default': 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=600&fit=crop',
}

const getCatImage = (name, apiImg) => {
  if (apiImg) return apiImg; 
  const key = name.toLowerCase()
  if (key.includes('chay') || key.includes('rau')) return fallbackImages.veg
  if (key.includes('uống') || key.includes('nước')) return fallbackImages.drinks
  if (key.includes('tráng miệng') || key.includes('ngọt')) return fallbackImages.dessert
  return fallbackImages.default
}

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await api.get('/api/categories')
    apiCategories.value = res.data
  } catch (error) {
    console.error("GOMET System: Lỗi lấy danh mục", error)
  } finally {
    loading.value = false
  }
}

onMounted(fetchCategories)

const displayCategories = computed(() => {
  return apiCategories.value.map(c => ({
    id: c.categoryID,
    name: c.categoryName,
    count: (c.postCount || 0) + '+',
    image: getCatImage(c.categoryName, c.categoryImage)
  }))
})

const handleFilter = (id, name) => { 
  router.push({ name: 'Search', query: { categoryID: id, categoryName: name } }) 
}

const goToSearch = () => { router.push({ name: 'Search' }) }

// UPDATED: Logic scroll động theo kích thước thẻ thực tế
const scroll = (direction) => {
  if (!carouselRef.value) return;
  const firstCard = carouselRef.value.querySelector('.cat-card');
  if (!firstCard) return;
  
  const cardWidth = firstCard.offsetWidth + 24; // Width + gap
  const scrollAmount = direction === 'left' ? -cardWidth : cardWidth;
  carouselRef.value.scrollBy({ left: scrollAmount, behavior: 'smooth' });
}

const handleScroll = () => {
  if (!carouselRef.value) return;
  const firstCard = carouselRef.value.querySelector('.cat-card');
  if (!firstCard) return;
  
  const cardWidth = firstCard.offsetWidth + 24;
  const scrollLeft = carouselRef.value.scrollLeft;
  activeIndex.value = Math.round(scrollLeft / cardWidth);
}
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
  padding: 20px 40px 50px 40px; 
  scroll-snap-type: x mandatory; 
}
.carousel-wrapper::-webkit-scrollbar { display: none; }

.carousel-track { display: flex; gap: 24px; }

/* --- CARD --- */
.cat-card {
  min-width: 220px; height: 300px;
  position: relative; cursor: pointer;
  scroll-snap-align: start;
  perspective: 1000px;
  will-change: transform;
  transition: transform var(--duration-normal) var(--ease-spring);
}

.card-inner {
  width: 100%; height: 100%;
  border-radius: var(--radius-xl); overflow: hidden;
  position: relative;
  background: var(--color-neutral-0);
  box-shadow: var(--shadow-md);
  transition: transform  var(--duration-normal) var(--ease-out),
              box-shadow var(--duration-normal) var(--ease-out),
              border     var(--duration-normal) var(--ease-out);
}

/* Skeleton Effect */
.skeleton-loading {
  background: linear-gradient(90deg, #f5f5f4 25%, #e7e5e4 50%, #f5f5f4 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}
@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* Ảnh Parallax */
.card-image-wrapper { width: 100%; height: 100%; overflow: hidden; position: relative; }
.parallax-img {
  width: 100%; height: 100%; object-fit: cover;
  transition: transform var(--duration-slower) var(--ease-out);
}
.overlay-gradient {
  position: absolute; inset: 0;
  background: linear-gradient(180deg, rgba(0,0,0,0) 40%, rgba(0,0,0,0.7) 100%);
  z-index: 1;
}

/* --- GLASSMORPHISM ELEMENTS --- */
.glass-tag {
  position: absolute; top: 15px; right: 15px; z-index: 2;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white; font-size: 0.75rem; font-weight: 800;
  padding: 6px 12px; border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.glass-pill {
  position: absolute; bottom: 25px; left: 50%; transform: translateX(-50%); z-index: 2;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(15px); -webkit-backdrop-filter: blur(15px);
  padding: var(--space-3) var(--space-6); border-radius: var(--radius-full);
  box-shadow: var(--shadow-lg);
  white-space: nowrap;
  border: 1px solid rgba(255, 255, 255, 0.8);
  transition: background var(--duration-normal) var(--ease-out),
              transform  var(--duration-normal) var(--ease-spring),
              border-color var(--duration-normal) var(--ease-out);
}
.cat-name { font-size: var(--text-base); font-weight: var(--font-extrabold); color: var(--color-neutral-900); letter-spacing: -0.5px; }

/* --- INTERACTIVE STATES --- */
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
}
.view-more-content { text-align: center; }
.icon-pulse-box {
  width: 50px; height: 50px; border-radius: var(--radius-full);
  background: var(--color-neutral-0); color: var(--color-primary-600);
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto var(--space-3); font-size: var(--text-xl);
  box-shadow: var(--shadow-primary-sm);
  animation: pulseIcon 2.5s var(--ease-in-out) infinite;
}

@keyframes pulseIcon {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.12); opacity: 0.85; }
}

.view-more-card h3 { font-family: var(--font-ui); font-size: var(--text-base); font-weight: var(--font-extrabold); color: var(--color-primary-700); margin: 0; }

/* ==========================================================================
   HỆ THỐNG RESPONSIVE TOÀN DIỆN (NEW)
   ========================================================================== */

/* 1. Màn hình máy tính lớn (Dưới 1440px) */
@media (max-width: 1440px) {
  .section-header { padding: 0 40px; }
  .carousel-wrapper { padding: 20px 40px 50px 40px; }
}

/* 2. Laptop và Tablet ngang (Dưới 1200px) */
@media (max-width: 1200px) {
  .cat-card { min-width: 200px; height: 280px; }
  .section-heading { font-size: var(--text-2xl); }
  .carousel-track { gap: 20px; }
}

/* 3. Tablet dọc (Dưới 1024px) */
@media (max-width: 1024px) {
  .cat-card { min-width: 180px; height: 260px; }
  .glass-pill { padding: var(--space-2) var(--space-4); bottom: 15px; }
  .cat-name { font-size: var(--text-sm); }
}

/* 4. Điện thoại (Dưới 768px) */
@media (max-width: 768px) {
  .section-header { padding: 0 var(--space-5); align-items: center; }
  .section-heading { font-size: 1.5rem; }
  .sub-label { font-size: 0.65rem; letter-spacing: 1px; }
  
  /* Ẩn điều hướng trên mobile vì dùng tay vuốt sướng hơn */
  .nav-controls { display: none; }
  
  .carousel-wrapper { padding: var(--space-3) var(--space-5) var(--space-8) var(--space-5); }
  .cat-card { min-width: 160px; height: 240px; }
  
  /* Tắt hiệu ứng hover nặng để mượt hơn trên mobile */
  .cat-card:hover .card-inner { transform: translateY(-5px); }
  
  .glass-tag { top: 10px; right: 10px; font-size: 0.65rem; padding: 4px 8px; }
}

/* 5. Điện thoại nhỏ / SE (Dưới 480px) */
@media (max-width: 480px) {
  .carousel-track { gap: 16px; }
  .cat-card { min-width: 145px; height: 210px; }
  .cat-name { font-size: 0.85rem; }
  .icon-pulse-box { width: 40px; height: 40px; font-size: 1rem; }
}

/* 6. Màn hình cực nhỏ (Dưới 360px) */
@media (max-width: 360px) {
  .cat-card { min-width: 130px; height: 190px; }
}
</style>