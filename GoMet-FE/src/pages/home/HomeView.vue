<template>
  <div class="home-page-container">
    
    <Teleport to="body">
      <AdPopup 
        :isVisible="showAdPopup"
        @close="showAdPopup = false"
      />
    </Teleport>
    
    <div class="bg-texture"></div>

    <div class="main-content">
      
      <div class="hero-wrapper">
        <HomeHero @view-detail="goToDetail" />
      </div>

      <div class="marquee-slim reveal-fade">
        <div class="marquee-track">
          <div class="marquee-group">
            <span class="marquee-item">TINH HOA ẨM THỰC VIỆT <span class="star">✦</span></span>
            <span class="marquee-item">KHƠI NGUỒN CẢM HỨNG <span class="star">✦</span></span>
            <span class="marquee-item">HƯƠNG VỊ BẢN SẮC <span class="star">✦</span></span>
            <span class="marquee-item">TINH HOA ẨM THỰC VIỆT <span class="star">✦</span></span>
            <span class="marquee-item">KHƠI NGUỒN CẢM HỨNG <span class="star">✦</span></span>
            <span class="marquee-item">HƯƠNG VỊ BẢN SẮC <span class="star">✦</span></span>
          </div>
        </div>
      </div>

      <section class="section-wrapper compact-section">
        <div class="bg-typo-decor" :style="{ transform: `translate(-50%, ${scrollY * 0.15}px)` }">DANH MỤC</div>
        
        <div class="section-header text-center reveal-up">
          <span class="premium-tag">KHÁM PHÁ</span>
          <h2 class="section-title">Thực Đơn <span class="highlight-text">Đa Dạng</span></h2>
        </div>
        <div class="reveal-up delay-200"><HomeCategorySection /></div>
      </section>

      <div class="slogan-strip reveal-scale">
        <div class="slogan-content">
          <h3 class="slogan-text">
            "Nấu ăn là một nghệ thuật, và đầu bếp chính là một <span class="glow-text">nghệ sĩ</span>."
          </h3>
        </div>
      </div>

      <section class="section-wrapper compact-section">
        <div class="section-header text-center reveal-up">
          <span class="premium-tag">CẬP NHẬT MỖI NGÀY</span>
          <h2 class="section-title">Tuyệt Tác Mới Nhất</h2>
        </div>
        <div class="reveal-up delay-200"><HomeLatestRecipes /></div>
      </section>

      <section class="newsletter-immersive reveal-up">
        <svg class="wave-top" viewBox="0 0 1440 80" preserveAspectRatio="none" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M0,20 C320,90 420,-10 740,30 C1060,70 1120,10 1440,20 V80 H0 V20 Z" fill="#1C1917"/>
        </svg>

        <div class="nl-content">
            <div class="nl-row">
                <div class="nl-info">
                    <h2 class="nl-heading">Hợp Tác Cùng <br> <span class="orange-txt">GoMet</span></h2>
                    <p class="nl-desc">Bạn là đầu bếp, chuyên gia ẩm thực hay nhãn hàng? Hãy kết nối với chúng tôi để cùng nhau lan tỏa đam mê.</p>
                </div>
                <div class="nl-action">
                    <a href="mailto:contact@gomet.vn?subject=Yêu cầu hợp tác cùng GoMet" class="btn-partner">
                        GỬI EMAIL LIÊN HỆ
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                          <line x1="22" y1="2" x2="11" y2="13"></line>
                          <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
                        </svg>
                    </a>
                </div>
            </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import AdPopup from '@/components/modals/AdPopup.vue'
import HomeHero from '@/components/home/HomeHero.vue'
import HomeCategorySection from '@/components/home/HomeCategorySection.vue'
import HomeLatestRecipes from '@/components/home/HomeLatestRecipes.vue'

const router = useRouter()
const showAdPopup = ref(false)
const scrollY = ref(0) 

const goToDetail = (id) => {
  router.push({ name: 'PostDetail', params: { id: id } })
}

const handleScroll = (event) => {
  scrollY.value = event.target.scrollTop
}

// Cấu hình hiệu ứng xuất hiện khi cuộn trang
const setupIntersectionObserver = () => {
  const scrollContainer = document.getElementById('main-scroll-container')
  const ANIM_CLASSES = '.reveal-up, .reveal-fade, .reveal-scale'

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const delay = entry.target.dataset.delay || 0
          setTimeout(() => {
            entry.target.classList.add('is-visible')
          }, delay)
          observer.unobserve(entry.target)
        }
      })
    },
    {
      threshold: 0.08,
      root: scrollContainer || null,
      rootMargin: '0px 0px -60px 0px'
    }
  )

  document.querySelectorAll(ANIM_CLASSES).forEach((el) => {
    observer.observe(el)
  })
}

onMounted(() => {
  setTimeout(() => { showAdPopup.value = true }, 2000)

  const scrollContainer = document.getElementById('main-scroll-container')
  if (scrollContainer) {
    scrollContainer.addEventListener('scroll', handleScroll, { passive: true })
  } else {
    window.addEventListener('scroll', () => { scrollY.value = window.scrollY }, { passive: true })
  }

  requestAnimationFrame(() => {
    setupIntersectionObserver()
  })
})

onUnmounted(() => {
  const scrollContainer = document.getElementById('main-scroll-container')
  if (scrollContainer) {
    scrollContainer.removeEventListener('scroll', handleScroll)
  }
})
</script>

<style scoped>
.home-page-container {
  width: 100%; 
  background-color: #FFFFFF; 
  font-family: 'Mulish', sans-serif;
  color: #1C1917;
}

.bg-texture {
  position: fixed; inset: 0; pointer-events: none; z-index: 0; opacity: 0.4;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
  mix-blend-mode: soft-light;
}

.main-content { position: relative; z-index: 2; }
.hero-wrapper { padding-bottom: 0px; }

/* ─────────────────────────────────────────────
    ANIMATION SYSTEM 
───────────────────────────────────────────── */
:root { --anim-duration: 0.75s; --anim-ease: cubic-bezier(0.22, 1, 0.36, 1); }

.reveal-up {
  opacity: 0;
  transform: translateY(36px);
  transition: opacity var(--anim-duration) var(--anim-ease),
              transform var(--anim-duration) var(--anim-ease);
  will-change: opacity, transform;
}
.reveal-up.is-visible { opacity: 1; transform: translateY(0); }

.reveal-fade {
  opacity: 0;
  transition: opacity 0.9s ease;
  will-change: opacity;
}
.reveal-fade.is-visible { opacity: 1; }

.reveal-scale {
  opacity: 0;
  transform: scaleY(0.88);
  transform-origin: top center;
  transition: opacity 0.8s var(--anim-ease),
              transform 0.8s var(--anim-ease);
  will-change: opacity, transform;
}
.reveal-scale.is-visible { opacity: 1; transform: scaleY(1); }

.delay-100 { transition-delay: 0.10s; }
.delay-150 { transition-delay: 0.15s; }
.delay-200 { transition-delay: 0.20s; }
.delay-300 { transition-delay: 0.30s; }

/* SECTIONS */
.section-wrapper { max-width: 1280px; margin: 0 auto; position: relative; }
.compact-section { padding: 40px 24px; } 
.text-center { text-align: center; margin-bottom: 30px; position: relative; z-index: 2; }
.premium-tag {
  font-size: 0.7rem; font-weight: 800; letter-spacing: 3px; color: #EA580C;
  margin-bottom: 8px; display: inline-flex; align-items: center; gap: 10px;
  text-transform: uppercase;
}
.premium-tag::before,
.premium-tag::after { content: ""; display: block; width: 20px; height: 1px; background: currentColor; opacity: 0.5; }
.section-title { font-family: 'Playfair Display', serif; font-size: 2.6rem; color: #1C1917; margin: 0; line-height: 1.08; letter-spacing: -0.02em; }
.highlight-text { font-style: italic; color: #EA580C; }

/* MARQUEE */
.marquee-slim {
  padding: 16px 0; margin: 20px 0 30px;
  border-top: 1px solid rgba(28, 25, 23, 0.08); border-bottom: 1px solid rgba(28, 25, 23, 0.08);
  background: #FFFFFF; overflow: hidden; white-space: nowrap; user-select: none;
  -webkit-mask-image: linear-gradient(to right, transparent 0%, black 7%, black 93%, transparent 100%);
  mask-image: linear-gradient(to right, transparent 0%, black 7%, black 93%, transparent 100%);
}
.marquee-track { display: flex; width: fit-content; animation: scroll 45s linear infinite; }
.marquee-group { display: flex; align-items: center; gap: 60px; padding-right: 60px; }
.marquee-item { font-family: 'Montserrat', sans-serif; font-size: 1.1rem; font-weight: 800; color: #A8A29E; letter-spacing: 2px; display: flex; align-items: center; gap: 30px; transition: 0.3s; }
.marquee-item:hover { color: #EA580C; transform: scale(1.02); }
.star { color: #EA580C; font-size: 1rem; }

/* DECOR CHỮ CHÌM */
.bg-typo-decor {
    position: absolute; top: 0; left: 50%;
    font-family: 'Montserrat', sans-serif; font-weight: 900; font-size: 8rem;
    color: transparent; -webkit-text-stroke: 2px rgba(28, 25, 23, 0.03);
    white-space: nowrap; z-index: 0; pointer-events: none;
    transition: transform 0.1s linear;
}

/* SLOGAN */
.slogan-strip {
  padding: 60px 24px; text-align: center; margin: 40px 0;
  background-color: #1C1917;
  background-image:
    radial-gradient(circle at 15% 50%, rgba(234, 88, 12, 0.12) 0%, transparent 35%),
    radial-gradient(circle at 85% 50%, rgba(234, 88, 12, 0.10) 0%, transparent 30%),
    radial-gradient(rgba(255,255,255,0.06) 1px, transparent 1px);
  background-size: auto, auto, 28px 28px;
}
.slogan-text { font-family: 'Playfair Display', serif; font-size: 2rem; font-weight: 400; color: #F5F5F4; max-width: 850px; margin: 0 auto; line-height: 1.4; letter-spacing: -0.01em; }
.glow-text { color: #EA580C; font-style: italic; text-shadow: 0 0 15px rgba(234, 88, 12, 0.4); }

/* PHẦN HỢP TÁC (NÂNG CẤP TỪ NEWSLETTER) */
.newsletter-immersive { margin-top: 40px; position: relative; }
.wave-top { display: block; width: 100%; height: 60px; margin-bottom: -1px; }
.nl-content { background: #1C1917; padding: 30px 24px 80px; }
.nl-row { max-width: 1100px; margin: 0 auto; display: flex; align-items: center; justify-content: space-between; gap: 40px; }
.nl-heading { font-family: 'Playfair Display', serif; font-size: 3rem; color: white; line-height: 1.08; letter-spacing: -0.02em; margin-bottom: 15px; }
.orange-txt { color: #EA580C; font-style: italic; }
.nl-desc { color: #A8A29E; font-size: 1.05rem; max-width: 450px; line-height: 1.6; }

/* Nút Gửi Email Hợp Tác */
.btn-partner {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #EA580C, #f97316);
  color: white;
  padding: 18px 45px;
  border-radius: 50px;
  font-size: 1rem;
  font-weight: 800;
  letter-spacing: 2px;
  text-transform: uppercase;
  text-decoration: none;
  transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1);
  box-shadow: 0 10px 30px rgba(234, 88, 12, 0.3);
}

.btn-partner:hover {
  transform: translateY(-5px) scale(1.02);
  box-shadow: 0 15px 40px rgba(234, 88, 12, 0.5);
  color: white;
}
.btn-partner svg { transition: transform 0.3s; }
.btn-partner:hover svg { transform: translate(4px, -4px); } /* Mũi tên bay chéo lên */

@keyframes scroll { 0% { transform: translateX(0); } 100% { transform: translateX(-50%); } }

/* ─────────────────────────────────────────────
    RESPONSIVE SYSTEM
───────────────────────────────────────────── */

@media (max-width: 1280px) {
  .section-wrapper { padding: 0 40px; }
  .nl-heading { font-size: 2.5rem; }
}

@media (max-width: 1024px) {
  .section-title { font-size: 2.2rem; }
  .slogan-text { font-size: 1.6rem; padding: 0 20px; }
  
  .nl-row { flex-direction: column; text-align: center; gap: 30px; }
  .nl-desc { margin: 0 auto; }
  .bg-typo-decor { font-size: 5rem; top: 20px; }
}

@media (max-width: 768px) {
  .section-wrapper { padding: 0 20px; }
  .compact-section { padding: 40px 16px; }
  
  .section-title { font-size: 1.8rem; }
  .premium-tag { font-size: 0.6rem; letter-spacing: 2px; }
  
  .marquee-item { font-size: 0.9rem; gap: 15px; }
  .marquee-group { gap: 30px; padding-right: 30px; }
  
  .slogan-strip { padding: 40px 16px; margin: 20px 0; }
  .slogan-text { font-size: 1.25rem; }
  
  .nl-heading { font-size: 2.2rem; }
  .wave-top { height: 40px; }
  .nl-content { padding-bottom: 60px; }
  
  .btn-partner { padding: 16px 30px; font-size: 0.9rem; width: 100%; justify-content: center;}
  .bg-typo-decor { opacity: 0.01; } 
}

@media (max-width: 480px) {
  .section-title { font-size: 1.6rem; }
  .nl-heading { font-size: 1.8rem; }
  .nl-desc { font-size: 0.95rem; }
  .marquee-slim { margin: 10px 0 20px; padding: 10px 0; }
}
</style>