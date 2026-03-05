<template>
  <section class="hero-section">
    
    <div class="marquee-container">
      <div class="marquee-row scroll-left">
        <div class="marquee-track">
          <img v-for="n in 6" :key="'r1-'+n" :src="getImage(n)" alt="Food">
          <img v-for="n in 6" :key="'r1-dup-'+n" :src="getImage(n)" alt="Food">
        </div>
      </div>
      <div class="marquee-row scroll-right">
        <div class="marquee-track">
          <img v-for="n in 6" :key="'r2-'+n" :src="getImage(n+6)" alt="Food">
          <img v-for="n in 6" :key="'r2-dup-'+n" :src="getImage(n+6)" alt="Food">
        </div>
      </div>
      <div class="marquee-row scroll-left">
        <div class="marquee-track">
          <img v-for="n in 6" :key="'r3-'+n" :src="getImage(n+12)" alt="Food">
          <img v-for="n in 6" :key="'r3-dup-'+n" :src="getImage(n+12)" alt="Food">
        </div>
      </div>
    </div>

    <div class="hero-mask"></div>

    <div class="hero-content">
      <div class="badge-pill">{{ $t('landing.badge') }}</div>
      
      <h1 class="hero-title">
        {{ $t('landing.title_1') }} <br>
        <span class="text-gradient">{{ $t('landing.title_hl') }}</span>
      </h1>
      
      <p class="hero-desc" v-html="$t('landing.desc')"></p>

      <div class="cta-group">
        <router-link to="/home" class="btn-primary-xl">
          {{ $t('landing.cta') }}
          <span class="arrow-icon">→</span>
        </router-link>
        
        <div class="social-proof">
          <div class="avatars">
            <img src="https://i.pravatar.cc/100?img=12">
            <img src="https://i.pravatar.cc/100?img=32">
            <img src="https://i.pravatar.cc/100?img=43">
            <div class="more-avt">+50k</div>
          </div>
          <span class="proof-text">{{ $t('landing.members') }}</span>
        </div>
      </div>
    </div>

    <div class="scroll-mouse" @click="scrollToSignup">
      <div class="wheel"></div>
    </div>

  </section>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

// Danh sách ảnh món ăn chất lượng cao (Đã chọn lọc)
const foodImages = [
  'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&q=80', // Món thịt
  'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400&q=80', // Salad
  'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&q=80', // Pizza
  'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400&q=80', // Pancake
  'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&q=80', // Healthy
  'https://images.unsplash.com/photo-1482049016688-2d3e1b311543?w=400&q=80', // Sandwich
  'https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=400&q=80', // Toast
  'https://images.unsplash.com/photo-1473093295043-cdd812d0e601?w=400&q=80', // Pasta
  'https://images.unsplash.com/photo-1476224203421-9ac39bcb3327?w=400&q=80', // Sushi
  'https://images.unsplash.com/photo-1499028344343-cd173ffc68a9?w=400&q=80', // Burger
  'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400&q=80', // BBQ
  'https://images.unsplash.com/photo-1455619452474-d2be8b1e70cd?w=400&q=80', // Curry
  'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&q=80', // Salad 2
  'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&q=80', // Cake
  'https://images.unsplash.com/photo-1467003909585-2f8a7270028d?w=400&q=80'  // Healthy 3
];

// Hàm lấy ảnh an toàn (tránh lỗi undefined nếu index quá lớn)
const getImage = (i) => {
  return foodImages[i % foodImages.length];
}

const scrollToSignup = () => {
  const element = document.getElementById("section-dang-ky");
  if (element) element.scrollIntoView({ behavior: "smooth", block: "start" });
};
</script>

<style scoped>
.hero-section {
  position: relative;
  height: 100vh;
  width: 100%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #FFF7ED; /* Nền kem rất nhạt */
  font-family: 'Quicksand', sans-serif;
}

/* --- 1. MARQUEE BACKGROUND --- */
.marquee-container {
  position: absolute;
  inset: -10% -10%; /* Mở rộng ra ngoài để khi xoay không bị hở */
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 20px;
  transform: rotate(-6deg) scale(1.1); /* Xoay nghiêng tạo nghệ thuật */
  opacity: 0.6; /* Làm mờ ảnh để không tranh chấp với chữ */
  z-index: 0;
  pointer-events: none;
}

.marquee-row {
  display: flex;
  overflow: hidden;
  width: 100%;
}

.marquee-track {
  display: flex;
  gap: 20px;
  /* Animation sẽ được gán bên dưới */
}

.marquee-track img {
  width: 280px;
  height: 180px;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
  filter: grayscale(20%); /* Giảm màu một chút cho sang */
}

/* Animation chạy vô tận */
.scroll-left .marquee-track { animation: scrollLeft 40s linear infinite; }
.scroll-right .marquee-track { animation: scrollRight 45s linear infinite; }

@keyframes scrollLeft { 0% { transform: translateX(0); } 100% { transform: translateX(-50%); } }
@keyframes scrollRight { 0% { transform: translateX(-50%); } 100% { transform: translateX(0); } }

/* --- 2. HERO MASK (CỰC KỲ QUAN TRỌNG) --- */
.hero-mask {
  position: absolute; inset: 0; z-index: 1;
  /* Gradient tỏa tròn: Ở giữa màu đặc (che ảnh), ra ngoài trong suốt (thấy ảnh) */
  background: radial-gradient(
    circle, 
    rgba(255, 247, 237, 1) 0%, 
    rgba(255, 247, 237, 0.95) 40%, 
    rgba(255, 247, 237, 0.4) 80%,
    transparent 100%
  );
}

/* --- 3. CONTENT --- */
.hero-content {
  position: relative; z-index: 10;
  text-align: center;
  max-width: 900px;
  padding: 0 20px;
  animation: fadeUp 1s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.badge-pill {
  display: inline-block;
  background: #FFFFFF;
  color: #F97316;
  font-weight: 700;
  padding: 8px 24px;
  border-radius: 50px;
  margin-bottom: 25px;
  box-shadow: 0 4px 15px rgba(249, 115, 22, 0.15);
  font-size: 0.9rem;
  border: 1px solid #FFEDD5;
}

.hero-title {
  font-family: 'Playfair Display', serif;
  font-size: 5.5rem;
  font-weight: 900;
  line-height: 1.1;
  color: #1C1917;
  margin-bottom: 25px;
  letter-spacing: -2px;
}

.text-gradient {
  color: #EA580C;
  display: inline-block;
}

.hero-desc {
  font-size: 1.25rem;
  color: #57534E;
  margin: 0 auto 45px;
  max-width: 600px;
  line-height: 1.6;
}

/* CTA Group */
.cta-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
}

.btn-primary-xl {
  background: #1C1917;
  color: white;
  text-decoration: none;
  font-weight: 700;
  font-size: 1.2rem;
  padding: 18px 60px;
  border-radius: 50px;
  display: inline-flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 10px 30px rgba(0,0,0,0.15);
}

.btn-primary-xl:hover {
  background: #F97316;
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(249, 115, 22, 0.3);
}

.arrow-icon { transition: 0.3s; }
.btn-primary-xl:hover .arrow-icon { transform: translateX(5px); }

/* Social Proof */
.social-proof { display: flex; align-items: center; gap: 15px; }
.avatars { display: flex; align-items: center; }
.avatars img, .more-avt {
  width: 40px; height: 40px;
  border-radius: 50%;
  border: 3px solid #FFF7ED;
  margin-left: -12px;
  object-fit: cover;
}
.avatars img:first-child { margin-left: 0; }
.more-avt {
  background: #FED7AA; color: #9A3412; font-size: 0.75rem; font-weight: 800;
  display: flex; align-items: center; justify-content: center;
}
.proof-text { font-weight: 600; color: #78716C; font-size: 0.95rem; }

/* Scroll Mouse */
.scroll-mouse {
  position: absolute; bottom: 30px; left: 50%; transform: translateX(-50%); z-index: 10;
  width: 26px; height: 42px; border: 2px solid #A8A29E; border-radius: 20px;
  display: flex; justify-content: center; cursor: pointer; opacity: 0.6; transition: 0.3s;
}
.scroll-mouse:hover { opacity: 1; border-color: #F97316; }
.wheel {
  width: 4px; height: 8px; background: #F97316; border-radius: 2px;
  margin-top: 6px; animation: scrollWheel 2s infinite;
}

@keyframes fadeUp { from { opacity: 0; transform: translateY(40px); } to { opacity: 1; transform: translateY(0); } }
@keyframes scrollWheel { 0% { opacity: 1; transform: translateY(0); } 100% { opacity: 0; transform: translateY(12px); } }

/* Responsive */
@media (max-width: 768px) {
  .hero-title { font-size: 3.5rem; }
  .marquee-track img { width: 200px; height: 130px; }
  .hero-mask {
    background: radial-gradient(circle, rgba(255,247,237,1) 40%, rgba(255,247,237,0.7) 100%);
  }
}
</style>