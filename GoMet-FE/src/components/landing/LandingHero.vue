<template>
  <section class="hero-section" ref="heroSection">
    
    <div class="marquee-container" ref="marqueeWrapper">
      <div class="marquee-row scroll-left row-1">
        <div class="marquee-track">
          <img v-for="n in 6" :key="'r1-'+n" :src="getImage(n)" alt="" aria-hidden="true" loading="lazy" decoding="async">
          <img v-for="n in 6" :key="'r1-dup-'+n" :src="getImage(n)" alt="" aria-hidden="true" loading="lazy" decoding="async">
        </div>
      </div>
      <div class="marquee-row scroll-right row-2">
        <div class="marquee-track">
          <img v-for="n in 6" :key="'r2-'+n" :src="getImage(n+6)" alt="" aria-hidden="true" loading="lazy" decoding="async">
          <img v-for="n in 6" :key="'r2-dup-'+n" :src="getImage(n+6)" alt="" aria-hidden="true" loading="lazy" decoding="async">
        </div>
      </div>
      <div class="marquee-row scroll-left row-3">
        <div class="marquee-track">
          <img v-for="n in 6" :key="'r3-'+n" :src="getImage(n+12)" alt="" aria-hidden="true" loading="lazy" decoding="async">
          <img v-for="n in 6" :key="'r3-dup-'+n" :src="getImage(n+12)" alt="" aria-hidden="true" loading="lazy" decoding="async">
        </div>
      </div>
    </div>

    <div class="light-effects-wrapper">
      <div class="light-orb orb-1 gsap-orb"></div>
      <div class="light-orb orb-2 gsap-orb"></div>
    </div>

    <div class="hero-mask" ref="heroMask"></div>

    <div class="hero-content" ref="contentWrapper">
      <div class="reveal-wrapper">
        <div class="badge-pill gsap-reveal">
          <span class="badge-glow"></span>
          🌟 {{ $t('landing.badge', 'GoMet Premium Ecosystem') }}
        </div>
      </div>
      
      <h1 class="hero-title">
        <div class="reveal-wrapper">
          <span class="gsap-reveal-text block text-shadow-glow">{{ $t('landing.title_1', 'Tôn vinh kiệt tác') }}</span>
        </div>
        <div class="reveal-wrapper">
          <span class="text-gradient gsap-reveal-text block shine-effect">
            {{ $t('landing.title_hl', 'ẩm thực cùng GoMet') }}
          </span>
        </div>
      </h1>
      
      <div class="reveal-wrapper">
        <p class="hero-desc gsap-reveal">
          {{ $t('landing.desc_lead', 'Bước vào không gian nghệ thuật của') }}
          <strong class="hero-desc-strong glow-text">{{ $t('landing.desc_highlight', 'GoMet') }}</strong>
          {{ $t('landing.desc_tail', '— Nơi kết nối giới mộ điệu, biến mỗi công thức thành một di sản ẩm thực độc bản.') }}
        </p>
      </div>

      <div class="reveal-wrapper">
        <div class="cta-group gsap-elastic">
          <router-link to="/home" class="btn-primary-xl shine-btn">
            {{ $t('landing.cta', 'Khám phá đặc quyền') }}
            <span class="arrow-icon">→</span>
          </router-link>
          
          <div class="social-proof">
            <div class="avatars">
              <img src="https://i.pravatar.cc/100?img=12" alt="user">
              <img src="https://i.pravatar.cc/100?img=32" alt="user">
              <img src="https://i.pravatar.cc/100?img=43" alt="user">
              <div class="more-avt">+100k</div>
            </div>
            <span class="proof-text">{{ $t('landing.members', 'Đầu bếp & Chuyên gia ẩm thực') }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="scroll-mouse gsap-fade-in" @click="scrollToSignup" ref="scrollMouse">
      <div class="wheel"></div>
    </div>

  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

const { t } = useI18n()

const heroSection = ref(null)
const marqueeWrapper = ref(null)
const contentWrapper = ref(null)
const heroMask = ref(null)
const scrollMouse = ref(null)
const ctx = ref(null) 

const foodImages = [
  'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&q=80',
  'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400&q=80',
  'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&q=80',
  'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400&q=80',
  'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&q=80',
  'https://images.unsplash.com/photo-1482049016688-2d3e1b311543?w=400&q=80',
  'https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=400&q=80',
  'https://images.unsplash.com/photo-1473093295043-cdd812d0e601?w=400&q=80',
  'https://images.unsplash.com/photo-1476224203421-9ac39bcb3327?w=400&q=80',
  'https://images.unsplash.com/photo-1499028344343-cd173ffc68a9?w=400&q=80',
  'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400&q=80',
  'https://images.unsplash.com/photo-1455619452474-d2be8b1e70cd?w=400&q=80',
  'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&q=80',
  'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&q=80',
  'https://images.unsplash.com/photo-1467003909585-2f8a7270028d?w=400&q=80'
];

const getImage = (i) => foodImages[i % foodImages.length];

const scrollToSignup = () => {
  const element = document.getElementById("section-dang-ky");
  if (element) {
    gsap.to(window, { duration: 1.2, scrollTo: element, ease: "power4.inOut" });
  } else {
    gsap.to(window, { duration: 1.2, scrollTo: window.innerHeight * 1.5, ease: "power4.inOut" });
  }
};

onMounted(() => {
  ctx.value = gsap.context(() => {
    
    // ==========================================
    // 🎭 1. TIMELINE INTRO LÚC VỪA LOAD TRANG
    // ==========================================
    const tl = gsap.timeline();

    tl.from(marqueeWrapper.value, {
      scale: 1.3,
      opacity: 0,
      rotation: -2,
      duration: 2.5,
      ease: "power3.out"
    }, 0);

    // Chữ Title trồi lên lộng lẫy
    tl.from(".gsap-reveal-text", {
      y: "150%",
      autoAlpha: 0,
      rotation: 12,
      transformOrigin: "left bottom",
      stagger: 0.2,
      duration: 1.5,
      ease: "back.out(1.5)"
    }, 0.3);

    // Ánh sáng bừng lên
    tl.from(".gsap-orb", {
      scale: 0,
      opacity: 0,
      duration: 2,
      stagger: 0.4,
      ease: "power2.out"
    }, 0.4);

    tl.from(".gsap-reveal", {
      y: 40,
      opacity: 0,
      stagger: 0.15,
      duration: 1.2,
      ease: "power3.out"
    }, 0.8);

    tl.from(".gsap-elastic", {
      scale: 0.8,
      opacity: 0,
      duration: 1.5,
      ease: "elastic.out(1, 0.5)"
    }, 1.2);

    tl.from(".gsap-fade-in", {
      opacity: 0,
      y: -20,
      duration: 1
    }, 1.8);

    // ==========================================
    // 🎢 2. PARALLAX TRƯỢT KHI CUỘN CHUỘT (PIN)
    // ==========================================
    const scrollTl = gsap.timeline({
      scrollTrigger: {
        trigger: heroSection.value,
        start: "top top",
        end: "+=100%", 
        scrub: 1, 
        pin: true, 
        anticipatePin: 1 
      }
    });

    scrollTl.to(".row-1, .row-2, .row-3", { xPercent: (i) => (i % 2 === 0 ? -25 : 25), opacity: 0.2, ease: "none" }, 0)
            .to(contentWrapper.value, { y: -120, opacity: 0, scale: 0.9, filter: "blur(5px)", ease: "none" }, 0)
            .to(".gsap-orb", { scale: 1.5, opacity: 0, duration: 0.5 }, 0) // Ánh sáng phai dần khi cuộn
            .to(heroSection.value, { backgroundColor: "#ffffff", ease: "none" }, 0); 

    // ==========================================
    // ✨ 3. IDLE ANIMATION CHO LIGHT ORBS
    // ==========================================
    gsap.to(".orb-1", {
      x: "15vw", y: "5vh", rotation: 20, duration: 15, repeat: -1, yoyo: true, ease: "sine.inOut"
    });
    gsap.to(".orb-2", {
      x: "-15vw", y: "-5vh", rotation: -20, duration: 20, repeat: -1, yoyo: true, ease: "sine.inOut"
    });

  }, heroSection.value);
});

onUnmounted(() => {
  if (ctx.value) ctx.value.revert();
});
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
  background-color: #FFF7ED;
  font-family: 'Quicksand', sans-serif;
}

/* --- LIGHT ORBS (Hiệu ứng ánh sáng nền) --- */
.light-effects-wrapper {
  position: absolute;
  inset: 0;
  z-index: 2; /* Nằm trên Marquee, dưới chữ */
  pointer-events: none;
  overflow: hidden;
}

.light-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(90px);
  opacity: 0.65;
  will-change: transform;
}

.orb-1 {
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(249, 115, 22, 0.4) 0%, transparent 70%);
  top: 10%; left: 15%;
}

.orb-2 {
  width: 600px; height: 600px;
  background: radial-gradient(circle, rgba(234, 88, 12, 0.25) 0%, transparent 70%);
  bottom: 5%; right: 10%;
}

/* GSAP Reveal */
.reveal-wrapper {
  overflow: hidden;
  display: flex;
  justify-content: center;
  padding: 5px; /* Tránh bị cắt mất bóng (shadow) */
}
.block {
  display: block;
  will-change: transform, opacity; 
}

/* --- MARQUEE BACKGROUND --- */
.marquee-container {
  position: absolute;
  inset: -15% -15%; 
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 20px;
  transform: rotate(-6deg) scale(1.1);
  opacity: 0.7; /* Tăng nhẹ độ sáng ảnh nền */
  z-index: 0;
  pointer-events: none;
}

.marquee-row { display: flex; overflow: hidden; width: 100%; }
.marquee-track { display: flex; gap: 20px; }

.marquee-track img {
  width: 280px; height: 180px; object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.15);
  filter: grayscale(15%) contrast(1.1); /* Tăng contrast để ảnh sang hơn */
}

.scroll-left .marquee-track { animation: scrollLeft 45s linear infinite; }
.scroll-right .marquee-track { animation: scrollRight 50s linear infinite; }
@keyframes scrollLeft { 0% { transform: translateX(0); } 100% { transform: translateX(-50%); } }
@keyframes scrollRight { 0% { transform: translateX(-50%); } 100% { transform: translateX(0); } }

/* --- HERO MASK (Tâm sáng, viền tối dần để tôn chữ) --- */
.hero-mask {
  position: absolute; inset: 0; z-index: 1;
  background: radial-gradient(
    circle at center, 
    rgba(255, 247, 237, 0.85) 0%, 
    rgba(255, 247, 237, 0.95) 45%, 
    rgba(255, 247, 237, 0.5) 100%
  );
}

/* --- CONTENT --- */
.hero-content {
  position: relative; z-index: 10;
  text-align: center;
  max-width: 900px;
  padding: 0 20px;
}

.badge-pill {
  position: relative;
  display: inline-flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  color: #EA580C;
  font-weight: 800;
  padding: 8px 24px;
  border-radius: 50px;
  margin-bottom: 25px;
  box-shadow: 0 8px 25px rgba(234, 88, 12, 0.15);
  font-size: 0.9rem;
  border: 1px solid rgba(255, 237, 213, 0.8);
  overflow: hidden;
}

/* Tia sáng lướt qua Badge */
.badge-glow {
  position: absolute;
  top: 0; left: -100%;
  width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.8), transparent);
  animation: badgeSweep 3s infinite ease-in-out;
}
@keyframes badgeSweep { 0% { left: -100%; } 20% { left: 200%; } 100% { left: 200%; } }

.hero-title {
  font-family: 'Playfair Display', serif;
  font-size: 5.5rem;
  font-weight: 900;
  line-height: 1.1;
  color: #1C1917;
  margin-bottom: 25px;
  letter-spacing: -2px;
}

/* Chữ thường có bóng đổ phát sáng nhẹ */
.text-shadow-glow {
  text-shadow: 0 10px 40px rgba(255, 255, 255, 0.8), 0 2px 10px rgba(234, 88, 12, 0.1);
}

/* Chữ Gradient Lấp Lánh */
.text-gradient {
  background: linear-gradient(to right, #EA580C 20%, #FDBA74 40%, #FDBA74 60%, #EA580C 80%);
  background-size: 200% auto;
  color: #EA580C;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: shineText 5s linear infinite;
  display: inline-block;
  padding-bottom: 10px; /* Tránh cắt đuôi chữ g,y */
}
@keyframes shineText { to { background-position: 200% center; } }

.hero-desc {
  font-size: 1.3rem;
  color: #57534E;
  margin: 0 auto 45px;
  max-width: 650px;
  line-height: 1.6;
}

/* Tôn vinh tên Gomet */
.glow-text {
  color: #EA580C;
  font-weight: 900;
  text-shadow: 0 0 20px rgba(234, 88, 12, 0.3);
  font-size: 1.4rem;
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
  transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
  box-shadow: 0 15px 35px rgba(0,0,0,0.2);
  position: relative;
  overflow: hidden;
}

.btn-primary-xl::before {
  content: ''; position: absolute; top: 0; left: -100%; width: 100%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: 0.5s;
}

.btn-primary-xl:hover {
  background: #EA580C;
  transform: translateY(-5px) scale(1.02);
  box-shadow: 0 20px 45px rgba(234, 88, 12, 0.4);
}
.btn-primary-xl:hover::before { left: 100%; }

.arrow-icon { transition: 0.3s; }
.btn-primary-xl:hover .arrow-icon { transform: translateX(5px); }

/* Social Proof */
.social-proof { display: flex; align-items: center; gap: 15px; }
.avatars { display: flex; align-items: center; }
.avatars img, .more-avt {
  width: 42px; height: 42px;
  border-radius: 50%;
  border: 3px solid #FFF7ED;
  margin-left: -15px;
  object-fit: cover;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}
.avatars img:first-child { margin-left: 0; }
.more-avt {
  background: #FED7AA; color: #9A3412; font-size: 0.8rem; font-weight: 800;
  display: flex; align-items: center; justify-content: center;
}
.proof-text { font-weight: 700; color: #57534E; font-size: 0.95rem; }

/* Scroll Mouse */
.scroll-mouse {
  position: absolute; bottom: 30px; left: 50%; transform: translateX(-50%); z-index: 10;
  width: 26px; height: 42px; border: 2px solid #A8A29E; border-radius: 20px;
  display: flex; justify-content: center; cursor: pointer; opacity: 0.6; transition: 0.3s;
}
.scroll-mouse:hover { opacity: 1; border-color: #EA580C; }
.wheel {
  width: 4px; height: 8px; background: #EA580C; border-radius: 2px;
  margin-top: 6px; animation: scrollWheel 2s infinite;
}

@keyframes scrollWheel { 0% { opacity: 1; transform: translateY(0); } 100% { opacity: 0; transform: translateY(12px); } }

/* Responsive */
@media (max-width: 768px) {
  .hero-title { font-size: 3.8rem; }
  .marquee-track img { width: 200px; height: 130px; }
  .hero-mask {
    background: radial-gradient(circle, rgba(255,247,237,0.9) 30%, rgba(255,247,237,0.7) 100%);
  }
}
</style>