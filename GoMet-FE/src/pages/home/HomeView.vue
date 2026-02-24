<template>
  <div class="home-page-container">
    
    <Teleport to="body">
      <AdPopup 
        :isVisible="showAdPopup"
        :imageUrl="currentAdImage"
        @close="showAdPopup = false"
      />
    </Teleport>
    
    <div class="bg-texture"></div>

    <div class="main-content">
      
      <div class="hero-wrapper reveal-on-scroll">
        <HomeHero @view-detail="goToDetail" />
      </div>

      <div class="marquee-slim reveal-on-scroll delay-100">
        <div class="marquee-track">
          <div class="marquee-group">
            <span class="marquee-item">TINH HOA BẾP VIỆT <span class="star">✦</span></span>
            <span class="marquee-item">KHƠI NGUỒN CẢM HỨNG <span class="star">✦</span></span>
            <span class="marquee-item">HƯƠNG VỊ ĐỘC BẢN <span class="star">✦</span></span>
            <span class="marquee-item">TINH HOA BẾP VIỆT <span class="star">✦</span></span>
            <span class="marquee-item">KHƠI NGUỒN CẢM HỨNG <span class="star">✦</span></span>
            <span class="marquee-item">HƯƠNG VỊ ĐỘC BẢN <span class="star">✦</span></span>
          </div>
        </div>
      </div>

      <section class="section-wrapper compact-section reveal-on-scroll">
        <div class="bg-typo-decor" :style="{ transform: `translate(-50%, ${scrollY * 0.15}px)` }">CATEGORY</div>
        
        <div class="section-header text-center">
          <span class="premium-tag">KHÁM PHÁ</span>
          <h2 class="section-title">Thực Đơn <span class="highlight-text">Đa Dạng</span></h2>
        </div>
        <HomeCategorySection />
      </section>

      <div class="slogan-strip reveal-zoom">
        <div class="slogan-content">
          <h3 class="slogan-text">
            “Nấu ăn là nghệ thuật, người đầu bếp là <span class="glow-text">nghệ sĩ</span>.”
          </h3>
        </div>
      </div>

      <section class="section-wrapper compact-section reveal-on-scroll">
        <div class="section-header text-center">
          <span class="premium-tag">CẬP NHẬT MỖI NGÀY</span>
          <h2 class="section-title">Tác Phẩm Mới Nhất</h2>
        </div>
        <HomeLatestRecipes />
      </section>

      <section class="newsletter-immersive reveal-on-scroll">
        <svg class="wave-top" viewBox="0 0 1440 80" preserveAspectRatio="none" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M0,20 C320,90 420,-10 740,30 C1060,70 1120,10 1440,20 V80 H0 V20 Z" fill="#1C1917"/>
        </svg>

        <div class="nl-content">
            <div class="nl-row">
                <div class="nl-info">
                    <h2 class="nl-heading">Gia Nhập <br> <span class="orange-txt">Bếp Nhà Gomet</span></h2>
                    <p class="nl-desc">Nhận ngay công thức bí truyền và mẹo vặt nhà bếp độc quyền mỗi tuần.</p>
                </div>
                <div class="nl-action">
                    <div class="arty-input">
                        <input type="email" placeholder="Email của bạn..." />
                        <button class="btn-submit magnetic-btn">
                            GỬI NGAY
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M5 12H19M12 5L19 12L12 19"/></svg>
                        </button>
                    </div>
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
import imgAd1 from '@/assets/images/ads/ad1.jpg'
import imgAd2 from '@/assets/images/ads/ad2.jpg'
import imgAd3 from '@/assets/images/ads/ad3.jpg'

const router = useRouter()
const showAdPopup = ref(false)
const currentAdImage = ref('')
const adImages = [imgAd1, imgAd2, imgAd3]
const scrollY = ref(0) 

const pickRandomAd = () => {
  if (adImages.length > 0) {
    const randomIndex = Math.floor(Math.random() * adImages.length)
    currentAdImage.value = adImages[randomIndex]
  }
}

const goToDetail = (id) => {
  router.push({ name: 'PostDetail', params: { id: id } })
}

// --- ✨ ANIMATION LOGIC (LẮNG NGHE TỪ LAYOUT) ✨ ---
const handleScroll = (event) => {
  scrollY.value = event.target.scrollTop;
}

const setupIntersectionObserver = () => {
  const scrollContainer = document.getElementById('main-scroll-container');
  
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible');
        observer.unobserve(entry.target);
      }
    });
  }, { 
    threshold: 0.15,
    root: scrollContainer 
  });

  document.querySelectorAll('.reveal-on-scroll, .reveal-zoom').forEach(el => {
    observer.observe(el);
  });
}

onMounted(() => {
  pickRandomAd()
  setTimeout(() => { showAdPopup.value = true }, 2000)

  const scrollContainer = document.getElementById('main-scroll-container');
  if (scrollContainer) {
    scrollContainer.addEventListener('scroll', handleScroll);
    setupIntersectionObserver();
  } else {
    // Fallback phòng hờ
    window.addEventListener('scroll', () => scrollY.value = window.scrollY);
  }
})

onUnmounted(() => {
  const scrollContainer = document.getElementById('main-scroll-container');
  if (scrollContainer) {
    scrollContainer.removeEventListener('scroll', handleScroll);
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;0,600;0,700;1,400&family=Mulish:wght@300;400;600;800&family=Montserrat:wght@800;900&display=swap');

.home-page-container {
  width: 100%; 
  background-color: #FFFFFF; 
  font-family: 'Mulish', sans-serif;
  color: #1C1917;
  /* Không set height/overflow để tránh 2 thanh cuộn */
}

.bg-texture {
  position: fixed; inset: 0; pointer-events: none; z-index: 0; opacity: 0.4;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
  mix-blend-mode: soft-light;
}

.main-content { position: relative; z-index: 2; }
.hero-wrapper { padding-bottom: 0px; }

/* ANIMATION CLASSES */
.reveal-on-scroll { opacity: 0; transform: translateY(40px); transition: all 1s cubic-bezier(0.2, 0.8, 0.2, 1); }
.reveal-on-scroll.is-visible { opacity: 1; transform: translateY(0); }
.delay-100 { transition-delay: 0.1s; }
.reveal-zoom { opacity: 0; transform: scale(0.95); transition: all 1.2s ease-out; }
.reveal-zoom.is-visible { opacity: 1; transform: scale(1); }

/* SECTIONS */
.section-wrapper { max-width: 1280px; margin: 0 auto; position: relative; }
.compact-section { padding: 40px 24px; } 
.text-center { text-align: center; margin-bottom: 30px; position: relative; z-index: 2; }
.premium-tag { font-size: 0.7rem; font-weight: 800; letter-spacing: 3px; color: #EA580C; margin-bottom: 8px; display: block; text-transform: uppercase; }
.section-title { font-family: 'Playfair Display', serif; font-size: 2.5rem; color: #1C1917; margin: 0; line-height: 1.1; }
.highlight-text { font-style: italic; color: #EA580C; }

/* MARQUEE */
.marquee-slim {
  padding: 15px 0; margin: 20px 0 30px; 
  border-top: 1px solid rgba(28, 25, 23, 0.08); border-bottom: 1px solid rgba(28, 25, 23, 0.08);
  background: #FFFFFF; overflow: hidden; white-space: nowrap; user-select: none;
}
.marquee-track { display: flex; width: fit-content; animation: scroll 45s linear infinite; }
.marquee-group { display: flex; align-items: center; gap: 60px; padding-right: 60px; }
.marquee-item { font-family: 'Montserrat', sans-serif; font-size: 1.1rem; font-weight: 800; color: #A8A29E; letter-spacing: 2px; display: flex; align-items: center; gap: 30px; transition: 0.3s; }
.marquee-item:hover { color: #EA580C; transform: scale(1.02); }
.star { color: #EA580C; font-size: 1rem; }

/* DECOR */
.bg-typo-decor {
    position: absolute; top: 0; left: 50%;
    font-family: 'Montserrat', sans-serif; font-weight: 900; font-size: 8rem;
    color: transparent; -webkit-text-stroke: 2px rgba(28, 25, 23, 0.03);
    white-space: nowrap; z-index: 0; pointer-events: none;
    transition: transform 0.1s linear;
}

/* SLOGAN */
.slogan-strip {
  background: #1C1917; padding: 50px 24px; text-align: center; margin: 30px 0;
  background-image: radial-gradient(rgba(255,255,255,0.1) 1px, transparent 1px); background-size: 30px 30px;
}
.slogan-text { font-family: 'Playfair Display', serif; font-size: 1.8rem; font-weight: 400; color: #F5F5F4; max-width: 800px; margin: 0 auto; line-height: 1.4; }
.glow-text { color: #EA580C; font-style: italic; text-shadow: 0 0 15px rgba(234, 88, 12, 0.4); }

/* NEWSLETTER */
.newsletter-immersive { margin-top: 40px; position: relative; }
.wave-top { display: block; width: 100%; height: 60px; margin-bottom: -1px; }
.nl-content { background: #1C1917; padding: 30px 24px 80px; }
.nl-row { max-width: 1100px; margin: 0 auto; display: flex; align-items: center; justify-content: space-between; gap: 40px; }
.nl-heading { font-family: 'Playfair Display', serif; font-size: 2.8rem; color: white; line-height: 1.1; margin-bottom: 10px; }
.orange-txt { color: #EA580C; font-style: italic; }
.nl-desc { color: #A8A29E; font-size: 1rem; max-width: 400px; }
.arty-input { display: flex; align-items: center; border-bottom: 2px solid #57534E; padding-bottom: 5px; width: 450px; transition: 0.3s; }
.arty-input:focus-within { border-color: #EA580C; }
.arty-input input { background: transparent; border: none; outline: none; flex: 1; color: white; font-size: 1.1rem; padding: 10px 0; }
.arty-input input::placeholder { color: #57534E; }
.btn-submit { background: none; border: none; color: #EA580C; font-weight: 800; letter-spacing: 1px; cursor: pointer; display: flex; align-items: center; gap: 8px; transition: 0.3s; }
.btn-submit:hover { color: white; gap: 12px; }

@keyframes scroll { 0% { transform: translateX(0); } 100% { transform: translateX(-50%); } }

@media (max-width: 1024px) {
  .section-title { font-size: 2rem; }
  .slogan-text { font-size: 1.4rem; }
  .nl-row { flex-direction: column; text-align: center; }
  .arty-input { width: 100%; }
  .bg-typo-decor { font-size: 4rem; top: 10px; }
}
</style>