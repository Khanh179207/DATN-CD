<template>
  <section class="hero-section" ref="heroSection">
    
    <div class="cinematic-lens gsap-lens"></div>

    <div class="magic-dust-container">
      <div v-for="n in 30" :key="'dust-'+n" class="magic-dust gsap-dust"></div>
    </div>

    <div class="marquee-camera-rig" ref="marqueeWrapper">
      <div class="marquee-container">
        
        <div class="marquee-row scroll-left row-1 gsap-img-row">
          <div class="marquee-track">
            <img v-for="n in 6" :key="'r1-1-'+n" :src="getImage(n)" alt="">
            <img v-for="n in 6" :key="'r1-2-'+n" :src="getImage(n)" alt="">
            <img v-for="n in 6" :key="'r1-3-'+n" :src="getImage(n)" alt="">
          </div>
        </div>
        
        <div class="marquee-row scroll-right row-2 gsap-img-row">
          <div class="marquee-track">
            <img v-for="n in 6" :key="'r2-1-'+n" :src="getImage(n+6)" alt="">
            <img v-for="n in 6" :key="'r2-2-'+n" :src="getImage(n+6)" alt="">
            <img v-for="n in 6" :key="'r2-3-'+n" :src="getImage(n+6)" alt="">
          </div>
        </div>
        
        <div class="marquee-row scroll-left row-3 gsap-img-row">
          <div class="marquee-track">
            <img v-for="n in 6" :key="'r3-1-'+n" :src="getImage(n+12)" alt="">
            <img v-for="n in 6" :key="'r3-2-'+n" :src="getImage(n+12)" alt="">
            <img v-for="n in 6" :key="'r3-3-'+n" :src="getImage(n+12)" alt="">
          </div>
        </div>

      </div>
    </div>

    <div class="light-effects-wrapper">
      <div class="light-orb orb-1 gsap-orb"></div>
      <div class="light-orb orb-2 gsap-orb"></div>
      <div class="light-orb orb-center gsap-orb"></div>
    </div>

    <div class="hero-mask" ref="heroMask"></div>

    <div class="hero-content" ref="contentWrapper">
      <div class="reveal-wrapper">
        <div class="badge-pill gsap-badge">
          <span class="badge-glow"></span>
          🌟 {{ $t('landing.badge', 'Mạng xã hội chuyên về ẩm thực GOMET') }}
        </div>
      </div>
      
      <h1 class="hero-title">
        <div class="reveal-wrapper perspective-box">
          <span class="gsap-reveal-text block text-shadow-glow">{{ $t('landing.title_1', 'Nâng tầm giá trị') }}</span>
        </div>
        <div class="reveal-wrapper perspective-box">
          <span class="text-gradient gsap-reveal-text block shine-effect">
            {{ $t('landing.title_hl', 'ẩm thực Việt Nam') }}
          </span>
        </div>
      </h1>
      
      <div class="reveal-wrapper">
        <p class="hero-desc gsap-desc">
          {{ $t('landing.desc_lead', 'Bước vào không gian nghệ thuật của') }}
          <strong class="hero-desc-strong glow-text">{{ $t('landing.desc_highlight', 'GoMet') }}</strong>
          {{ $t('landing.desc_tail', '— Nơi lưu giữ những công thức nấu ăn độc đáo và kết nối những người đam mê ẩm thực thực thụ.') }}
        </p>
      </div>

      <div class="reveal-wrapper" style="overflow: visible;">
        <div class="cta-group gsap-cta">
          <a href="#" @click.prevent="handleExploreClick" class="btn-primary-xl shine-btn pulse-hover">
            <span class="btn-text">{{ $t('landing.cta', 'Khám phá ngay !') }}</span>
            <span class="arrow-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
            </span>
          </a>
          
          <div class="social-proof">
            <div class="avatars">
              <img src="https://i.pravatar.cc/100?img=12" alt="user">
              <img src="https://i.pravatar.cc/100?img=32" alt="user">
              <img src="https://i.pravatar.cc/100?img=43" alt="user">
              <div class="more-avt">+100k</div>
            </div>
            <span class="proof-text">{{ $t('landing.members', 'Đầu bếp & Chuyên gia') }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="scroll-mouse gsap-mouse" @click="scrollToSignup" ref="scrollMouse">
      <div class="wheel"></div>
    </div>

  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router' // 👈 Thêm Router
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

const { t } = useI18n()
const router = useRouter() // 👈 Khởi tạo Router

const heroSection = ref(null)
const marqueeWrapper = ref(null)
const contentWrapper = ref(null)
const scrollMouse = ref(null)

// ❌ KHÔNG dùng const ctx = ref(null) để tránh tràn RAM
let ctx; // ✅ Khai báo biến let bình thường

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

// --- 🔥 HÀM XỬ LÝ KHÁM PHÁ NGAY ---
const handleExploreClick = () => {
  // Cắm cờ bật Loading cho MainLayout
  sessionStorage.setItem('just_logged_in', 'true')
  // Chuyển trang
  router.push('/home')
}

onMounted(() => {
  ctx = gsap.context(() => { // ✅ Bỏ .value đi

    // ==========================================
    // 🎬 HIỆU ỨNG MỞ MÀN ĐIỆN ẢNH (CINEMATIC INTRO)
    // ==========================================
    const tl = gsap.timeline();

    tl.to(".gsap-lens", {
      autoAlpha: 0,
      duration: 1.5,
      ease: "power2.inOut"
    }, 0);
    
    tl.fromTo(marqueeWrapper.value, 
      { scale: 2.5, filter: "blur(20px)", rotation: -12 },
      { scale: 1, filter: "blur(0px)", rotation: 0, duration: 3.5, ease: "expo.out" }
    , 0);

    gsap.utils.toArray(".gsap-dust").forEach(dust => {
      gsap.fromTo(dust, 
        { x: "random(-100vw, 100vw)", y: "random(-100vh, 100vh)", scale: "random(0.2, 1.5)", opacity: 0 },
        { y: "-=300", opacity: "random(0.3, 0.8)", duration: "random(10, 20)", repeat: -1, yoyo: true, ease: "sine.inOut", delay: "random(0, 5)" }
      );
    });

    tl.from(".gsap-orb", { scale: 0, opacity: 0, duration: 2.5, stagger: 0.2, ease: "power2.out" }, 0.5);
    tl.from(".gsap-badge", { y: -60, autoAlpha: 0, rotationX: 90, transformOrigin: "top center", duration: 1.2, ease: "back.out(1.7)" }, 1);
    tl.fromTo(".gsap-reveal-text", 
      { y: "120%", autoAlpha: 0, rotationX: -80, transformOrigin: "50% 100% -50px" },
      { y: "0%", autoAlpha: 1, rotationX: 0, stagger: 0.15, duration: 1.8, ease: "power4.out" }
    , 1.2);
    tl.from(".gsap-desc", { y: 30, autoAlpha: 0, filter: "blur(10px)", duration: 1.5, ease: "power3.out" }, 1.8);
    tl.from(".gsap-cta", { y: 50, scale: 0.8, autoAlpha: 0, duration: 1.5, ease: "elastic.out(1, 0.5)" }, 2.1);
    tl.from(".gsap-mouse", { autoAlpha: 0, y: -20, duration: 1 }, 2.5);

    // ==========================================
    // 🎢 PARALLAX TRƯỢT KHI CUỘN CHUỘT
    // ==========================================
    const scrollTl = gsap.timeline({
      scrollTrigger: {
        trigger: heroSection.value, start: "top top", end: "+=120%", scrub: 1, pin: true, anticipatePin: 1 
      }
    });

    scrollTl.to(marqueeWrapper.value, { scale: 1.5, opacity: 0, filter: "blur(15px)", ease: "power1.inOut" }, 0)
            .to(".row-1, .row-3", { yPercent: -30, ease: "none" }, 0)
            .to(".row-2", { yPercent: 30, ease: "none" }, 0)
            .to(contentWrapper.value, { y: -200, autoAlpha: 0, scale: 0.8, ease: "power2.in" }, 0)
            .to(".gsap-orb", { scale: 3, opacity: 0, duration: 0.5 }, 0) 
            .to(heroSection.value, { backgroundColor: "#ffffff", ease: "none" }, 0); 

    // IDLE ORBS
    gsap.to(".orb-1", { x: "25vw", y: "15vh", rotation: 45, duration: 20, repeat: -1, yoyo: true, ease: "sine.inOut" });
    gsap.to(".orb-2", { x: "-25vw", y: "-15vh", rotation: -45, duration: 25, repeat: -1, yoyo: true, ease: "sine.inOut" });
    gsap.to(".orb-center", { scale: 1.2, opacity: 0.5, duration: 8, repeat: -1, yoyo: true, ease: "sine.inOut" });

  }, heroSection.value);
});

onUnmounted(() => {
  if (ctx) ctx.revert(); // ✅ Bỏ .value đi
});
</script>

<style scoped lang="scss" src="./LandingHero.scss"></style>