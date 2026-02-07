<template>
  <section class="category-section" ref="sectionRef">
    
    <div class="section-header fade-in-up">
      <div class="header-left">
        <span class="sub-label">Khám Phá</span>
        <h2 class="section-heading">Danh Mục</h2>
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
              <h3>Xem Tất Cả</h3>
            </div>
          </div>
        </div>

      </div>
    </div>

  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const carouselRef = ref(null)
const activeIndex = ref(0)

const handleFilter = (slug) => { router.push({ name: 'Search', query: { cat: slug } }) }
const goToSearch = () => { router.push({ name: 'Search' }) }

const scroll = (direction) => {
  if (!carouselRef.value) return;
  const cardWidth = 240; // Card width + gap
  const scrollAmount = direction === 'left' ? -cardWidth : cardWidth;
  carouselRef.value.scrollBy({ left: scrollAmount, behavior: 'smooth' });
}

const handleScroll = () => {
  if (!carouselRef.value) return;
  const scrollLeft = carouselRef.value.scrollLeft;
  const cardWidth = 240; 
  activeIndex.value = Math.round(scrollLeft / cardWidth);
}

const categories = ref([
  { name: 'Bánh Ngọt', slug: 'dessert', count: '150+', image: 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?q=80&w=600&fit=crop' },
  { name: 'Đồ Uống', slug: 'drinks', count: '60+', image: 'https://images.unsplash.com/photo-1544145945-f90425340c7e?q=80&w=600&fit=crop' },
  { name: 'Eat Clean', slug: 'healthy', count: '85+', image: 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?q=80&w=600&fit=crop' },
  { name: 'Món Chính', slug: 'main-course', count: '300+', image: 'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?q=80&w=600&fit=crop' },
  { name: 'Ăn Vặt', slug: 'snacks', count: '200+', image: 'https://images.unsplash.com/photo-1526230427044-d092040d48dc?q=80&w=600&fit=crop' },
  { name: 'Món Chay', slug: 'vegetarian', count: '120+', image: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=600&fit=crop' },
])
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;600;700;800&family=Playfair+Display:wght@700;800&display=swap');

/* --- CONTAINER CHÍNH --- */
.category-section {
  background: transparent; /* Trong suốt để ăn nhập với nền cha */
  margin: 20px 0;
  padding: 10px 0 10px 0;
  width: 100%;
  font-family: 'Mulish', sans-serif;
  position: relative;
}

/* --- HEADER --- */
.section-header {
  display: flex; align-items: flex-end; justify-content: space-between;
  padding: 0 40px; margin-bottom: 25px;
}
.sub-label {
  display: block; font-size: 0.75rem; color: #EA580C; font-weight: 800; 
  text-transform: uppercase; margin-bottom: 6px; letter-spacing: 1.5px;
}
.section-heading {
  font-family: 'Playfair Display', serif;
  font-size: 2.2rem; color: #1C1917; margin: 0; line-height: 1;
}

/* Navigation Buttons */
.nav-controls { display: flex; gap: 12px; }
.nav-btn {
  width: 40px; height: 40px; border-radius: 50%;
  border: 1px solid rgba(0,0,0,0.1); background: white;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 0.9rem; color: #1C1917;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
}
.nav-btn:hover { 
  background: #EA580C; color: white; border-color: #EA580C; 
  transform: scale(1.1); box-shadow: 0 8px 20px rgba(234, 88, 12, 0.3);
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
  min-width: 220px; height: 300px; /* Thẻ to hơn, sang hơn */
  position: relative; cursor: pointer;
  scroll-snap-align: start;
  perspective: 1000px;
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.card-inner {
  width: 100%; height: 100%;
  border-radius: 24px; overflow: hidden;
  position: relative;
  background: white;
  /* Bóng đổ nhẹ lúc bình thường */
  box-shadow: 0 10px 25px -5px rgba(0,0,0,0.05), 0 8px 10px -6px rgba(0,0,0,0.01);
  transition: all 0.4s ease;
  transform-style: preserve-3d;
}

/* Ảnh Parallax */
.card-image-wrapper { width: 100%; height: 100%; overflow: hidden; position: relative; }
.parallax-img {
  width: 100%; height: 100%; object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
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
  backdrop-filter: blur(15px);
  padding: 10px 24px; border-radius: 40px;
  box-shadow: 0 8px 25px rgba(0,0,0,0.2);
  white-space: nowrap;
  transition: all 0.3s ease;
  border: 1px solid rgba(255,255,255,0.8);
}
.cat-name { font-size: 1rem; font-weight: 800; color: #1C1917; letter-spacing: -0.5px; }

/* --- INTERACTIVE STATES (HOVER) --- */
.cat-card:hover .card-inner {
  transform: translateY(-12px); /* Bay lên cao hơn */
  box-shadow: 0 25px 50px -12px rgba(234, 88, 12, 0.25); /* Bóng cam lan tỏa */
  border: 1px solid rgba(234, 88, 12, 0.2);
}

.cat-card:hover .parallax-img { transform: scale(1.1); }

.cat-card:hover .glass-pill {
  background: #EA580C; /* Đổi màu cam */
  transform: translateX(-50%) scale(1.05);
  box-shadow: 0 10px 30px rgba(234, 88, 12, 0.4);
  border-color: #EA580C;
}
.cat-card:hover .cat-name { color: white; }

/* --- VIEW MORE CARD --- */
.view-more-inner {
  background: #FFF7ED; /* Nền cam nhạt */
  border: 2px dashed #FDBA74; /* Viền đứt đoạn */
  display: flex; align-items: center; justify-content: center;
  box-shadow: none !important;
}
.view-more-content { text-align: center; }
.icon-pulse-box {
  width: 50px; height: 50px; border-radius: 50%;
  background: white; color: #EA580C;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 12px; font-size: 1.2rem;
  box-shadow: 0 5px 15px rgba(234, 88, 12, 0.15);
  animation: pulseIcon 2s infinite;
}
@keyframes pulseIcon { 
  0% { box-shadow: 0 0 0 0 rgba(234, 88, 12, 0.4); } 
  70% { box-shadow: 0 0 0 15px rgba(234, 88, 12, 0); } 
  100% { box-shadow: 0 0 0 0 rgba(234, 88, 12, 0); } 
}

.view-more-card h3 { font-family: 'Mulish', sans-serif; font-size: 1rem; font-weight: 800; color: #C2410C; margin: 0; }

.view-more-card:hover .view-more-inner {
  background: #FFEDD5; border-color: #EA580C; transform: translateY(-5px);
}
.view-more-card:hover .icon-pulse-box { background: #EA580C; color: white; }

/* Responsive */
@media (max-width: 768px) {
  .section-header { padding: 0 20px; margin-bottom: 15px; }
  .carousel-wrapper { padding: 10px 20px 30px 20px; }
  .cat-card { min-width: 160px; height: 240px; }
  .nav-controls { display: none; }
}
</style>