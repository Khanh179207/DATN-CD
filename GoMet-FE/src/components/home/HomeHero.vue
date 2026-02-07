<template>
  <header class="hero-wrapper" @mousemove="handleMouseMove" @mouseenter="pauseSlide" @mouseleave="resumeSlide">
    
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
              <span>{{ currentSlideData.views }} Lượt xem</span>
            </div>
          </div>

          <p class="hero-desc">
            {{ currentSlideData.desc }}
          </p>

          <div class="cta-group">
            <button class="btn-primary" :style="{ background: currentSlideData.themeColor }" @click="$emit('view-detail', currentSlideData.id)">
              Xem Công Thức
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
            </button>
            <button class="btn-secondary" :style="secondaryBtnStyle">
              <div class="play-icon" :style="{ color: currentSlideData.themeColor, background: currentSlideData.themeColor + '20' }">▶</div>
              <span>Video Hướng Dẫn</span>
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
                  <div class="u-role">Thành viên VIP</div>
                </div>
              </div>
              <p class="u-comment">"{{ currentSlideData.review.comment }}"</p>
              <div class="stars" :style="{ color: currentSlideData.themeColor }">⭐⭐⭐⭐⭐</div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <div class="dots-nav">
      <span v-for="(slide, index) in slides" :key="slide.id" class="dot" :class="{ active: index === currentIndex }" :style="{ background: index === currentIndex ? slide.themeColor : '#D1D5DB' }" @click="goToSlide(index)"></span>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

defineEmits(['view-detail'])

// --- DỮ LIỆU SLIDES (ĐÃ CẬP NHẬT MÀU DỄ NHÌN HƠN) ---
const slides = [
  {
    id: 1,
    badge: "Công thức nổi bật nhất tuần",
    titleTop: "Tuyệt Tác",
    titleHighlight: "Mỳ Ý Carbonara",
    desc: "Sự hòa quyện hoàn hảo giữa vị béo ngậy của kem trứng, phô mai Parmesan thượng hạng và hương thơm nồng nàn của nấm Truffle. Chuẩn vị Ý.",
    time: "30 Phút",
    difficulty: "Dễ",
    views: "12.5K",
    themeColor: "#EA580C", // Cam
    // 👇 Màu nền mềm mại hơn (Trắng -> Cam nhạt)
    bgGradient: "linear-gradient(120deg, #FFFFFF 0%, #FFF7ED 100%)", 
    mainImg: "https://images.unsplash.com/photo-1612874742237-6526221588e3?q=80&w=1000&auto=format&fit=crop",
    float1: { isImg: false, text: "Ảnh Tỏi" },
    float2: { isImg: true, src: "https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Basil.png/800px-Basil.png" },
    float3: { isImg: false, text: "Ảnh Nấm" },
    review: { name: "Khánh Nguyễn", avatar: "https://ui-avatars.com/api/?name=Khanh+Nguyen&background=F97316&color=fff", comment: "Món này ngon tuyệt đỉnh! Cả nhà mình ai cũng khen." }
  },
  {
    id: 2,
    badge: "Món ngon cuối tuần",
    titleTop: "Đẳng Cấp",
    titleHighlight: "Bò Wagyu Áp Chảo",
    desc: "Thịt bò Wagyu mềm tan trong miệng, kết hợp với sốt tiêu đen đậm đà và hương thảo (Rosemary). Một bữa tối lãng mạn sang trọng.",
    time: "45 Phút",
    difficulty: "Trung bình",
    views: "8.2K",
    themeColor: "#DC2626", // Đỏ
    // 👇 Màu nền mềm mại hơn (Trắng -> Đỏ hồng nhạt)
    bgGradient: "linear-gradient(120deg, #FFFFFF 0%, #FEF2F2 100%)", 
    mainImg: "https://images.unsplash.com/photo-1600891964092-4316c288032e?q=80&w=1000&auto=format&fit=crop",
    float1: { isImg: true, src: "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Garlic.png/640px-Garlic.png" },
    float2: { isImg: false, text: "Hương Thảo" },
    float3: { isImg: false, text: "Hạt Tiêu" },
    review: { name: "Minh Anh", avatar: "https://ui-avatars.com/api/?name=Minh+Anh&background=DC2626&color=fff", comment: "Thịt mềm như bơ, công thức sốt rất chuẩn!" }
  },
  // 👇👇👇 SLIDE 3 MỚI (XANH LÁ) 👇👇👇
  {
    id: 3,
    badge: "Thực đơn Eat Clean",
    titleTop: "Thanh Mát",
    titleHighlight: "Salad Ức Gà",
    desc: "Sự kết hợp tươi mát giữa rau xanh, ức gà nướng mềm và sốt chanh leo chua ngọt. Bữa ăn nhẹ nhàng, đầy đủ dinh dưỡng.",
    time: "20 Phút",
    difficulty: "Dễ",
    views: "15.3K",
    themeColor: "#16A34A", // Xanh lá (Green 600)
    // 👇 Màu nền mềm mại hơn (Trắng -> Xanh bạc hà nhạt)
    bgGradient: "linear-gradient(120deg, #FFFFFF 0%, #F0FDF4 100%)", 
    mainImg: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=1000&auto=format&fit=crop",
    float1: { isImg: false, text: "Cà Chua" }, // Có thể thay bằng ảnh thật
    float2: { isImg: false, text: "Chanh Leo" },
    float3: { isImg: true, src: "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f4/Avocado_half.png/600px-Avocado_half.png" }, // Quả bơ
    review: { name: "Lan Phương", avatar: "https://ui-avatars.com/api/?name=Lan+Phuong&background=16A34A&color=fff", comment: "Sốt chanh leo cực cuốn, ăn hoài không chán!" }
  }
]

// --- LOGIC ---
const currentIndex = ref(0)
const autoPlayInterval = ref(null)
const currentSlideData = computed(() => slides[currentIndex.value])

const highlightStyle = computed(() => ({
  background: `linear-gradient(120deg, ${currentSlideData.value.themeColor}, #F59E0B)`,
  WebkitBackgroundClip: 'text',
  WebkitTextFillColor: 'transparent',
  filter: `drop-shadow(0 2px 10px ${currentSlideData.value.themeColor}33)`
}))

const secondaryBtnStyle = computed(() => ({ borderColor: '#F3F4F6' }))

const nextSlide = () => { currentIndex.value = (currentIndex.value + 1) % slides.length }
const prevSlide = () => { currentIndex.value = (currentIndex.value - 1 + slides.length) % slides.length }
const goToSlide = (index) => { currentIndex.value = index }

const startAutoPlay = () => { autoPlayInterval.value = setInterval(nextSlide, 5000) }
const stopAutoPlay = () => { clearInterval(autoPlayInterval.value) }
const pauseSlide = () => stopAutoPlay()
const resumeSlide = () => startAutoPlay()

onMounted(() => { startAutoPlay() })
onUnmounted(() => { stopAutoPlay() })

const mouseX = ref(0)
const mouseY = ref(0)
const handleMouseMove = (e) => {
  const { innerWidth, innerHeight } = window
  mouseX.value = (e.clientX / innerWidth) - 0.5
  mouseY.value = (e.clientY / innerHeight) - 0.5
}
const parallaxStyle = (factor) => ({ transform: `translate(${mouseX.value * factor * 0.5}px, ${mouseY.value * factor * 0.5}px)` })
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;500;600;700&family=Playfair+Display:ital,wght@0,700;1,700&display=swap');

.hero-wrapper {
  position: relative;
  min-height: 650px;
  height: auto; 
  padding: 0;
  display: flex; align-items: center; 
  overflow: hidden;
  font-family: 'Mulish', sans-serif;
  color: #1C1917;
  background: transparent; 
  transition: background 0.5s ease;
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
.slide-fade-enter-active, .slide-fade-leave-active { transition: all 0.5s ease; }
.slide-fade-enter-from { opacity: 0; transform: translateX(30px); }
.slide-fade-leave-to { opacity: 0; transform: translateX(-30px); }

/* --- TEXT SIDE --- */
.text-side { flex: 1; }
.pill-badge { display: inline-flex; align-items: center; gap: 10px; padding: 8px 18px; background: white; border-radius: 100px; border: 1px solid; font-size: 0.9rem; font-weight: 700; margin-bottom: 20px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); transition: all 0.3s; }
.dot-pulse { width: 8px; height: 8px; border-radius: 50%; animation: pulse 2s infinite; }
.hero-title { font-family: 'Playfair Display', serif; font-size: 4.5rem; line-height: 1.15; font-weight: 800; margin-bottom: 16px; letter-spacing: -1px; color: #0F172A; }
.text-highlight { display: inline-block; font-style: italic; transition: all 0.5s; }
.meta-info-line { display: flex; align-items: center; gap: 15px; margin-bottom: 24px; color: #64748B; font-weight: 600; font-size: 0.95rem; }
.meta-item { display: flex; align-items: center; gap: 8px; }
.meta-item svg { color: #94A3B8; }
.meta-divider { color: #CBD5E1; }
.meta-item.views svg { transition: color 0.3s; }
.hero-desc { font-size: 1.1rem; line-height: 1.6; color: #4B5563; max-width: 520px; margin-bottom: 32px; font-weight: 500; }
.cta-group { display: flex; gap: 16px; }
.btn-primary { color: white; border: none; padding: 16px 32px; border-radius: 14px; font-weight: 700; font-size: 1rem; cursor: pointer; display: flex; align-items: center; gap: 10px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); box-shadow: 0 10px 25px -5px rgba(0,0,0,0.2); }
.btn-primary:hover { transform: translateY(-3px); filter: brightness(1.1); }
.btn-secondary { background: white; border: 2px solid #F3F4F6; color: #1C1917; padding: 16px 24px; border-radius: 14px; font-weight: 700; font-size: 1rem; cursor: pointer; display: flex; align-items: center; gap: 10px; transition: 0.3s; }
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
.review-card-pro { position: absolute; bottom: 15%; right: -30px; z-index: 4; background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(20px); padding: 16px 20px; border-radius: 18px; border: 1px solid white; width: 250px; box-shadow: 0 20px 50px -10px rgba(0,0,0,0.15); animation: floatCard 6s ease-in-out infinite; }
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
  transition: all 0.2s; box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}
.nav-btn:hover { background: #1C1917; color: white; border-color: #1C1917; }
.prev { left: 10px; } 
.next { right: 10px; }

@media (max-width: 768px) { .nav-btn { display: none; } }

.dots-nav { position: absolute; bottom: 30px; left: 50%; transform: translateX(-50%); display: flex; gap: 10px; z-index: 10; }
.dot { width: 10px; height: 10px; border-radius: 50%; cursor: pointer; transition: all 0.3s; }
.dot.active { transform: scale(1.3); }
@keyframes pulse { 0%, 100% { opacity: 1; transform: scale(1); } 50% { opacity: 0.6; transform: scale(1.2); } }
@keyframes floatCard { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-12px); } }

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