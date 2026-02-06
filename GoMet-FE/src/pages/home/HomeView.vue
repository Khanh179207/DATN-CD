<template>
  <div class="home-wrapper">
    
    <Teleport to="body">
      <AdPopup 
        :isVisible="showAdPopup"
        :imageUrl="currentAdImage"
        @close="showAdPopup = false"
      />
    </Teleport>
    
    <div class="bg-decor-wrapper">
      <div class="glow-orb orb-1"></div>
      <div class="glow-orb orb-2"></div>
      <div class="glow-orb orb-3"></div>
    </div>

    <HomeHero @view-detail="handleHeroAction" />

    <div class="content-body">
      <HomeCategorySection />
      <HomeLatestRecipes />
    </div> 

    <section class="newsletter-section fade-in-scroll">
      <div class="newsletter-content">
        <div class="nl-text">
          <h2>Gia nhập cộng đồng GOMET</h2>
          <p>Nhận ngay 50+ công thức độc quyền và mẹo vặt nhà bếp hàng tuần.</p>
        </div>
        <div class="nl-form">
          <input type="email" placeholder="Email của bạn..." />
          <button>Gửi Ngay</button>
        </div>
        <div class="nl-decor-circle"></div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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

const pickRandomAd = () => {
  const randomIndex = Math.floor(Math.random() * adImages.length)
  currentAdImage.value = adImages[randomIndex]
}

onMounted(() => {
  pickRandomAd()
  setTimeout(() => { showAdPopup.value = true }, 1500)
})

const handleHeroAction = (id) => {
  router.push({ name: 'PostDetail', params: { id: id } })
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;500;600;700;800&family=Playfair+Display:wght@700;800&display=swap');

.home-wrapper {
  /* 👇 FIX CHUẨN: Dùng width 100% thay vì margin âm */
  width: 100%;
  
  /* 👇 QUAN TRỌNG: Để chiều cao tự động theo nội dung -> Không sinh ra thanh cuộn riêng */
  height: auto;
  min-height: 100%; 
  overflow: visible; 

  position: relative;
  background-color: #FAFAF9;
  background-image: radial-gradient(#E5E7EB 1px, transparent 1px);
  background-size: 30px 30px;
  font-family: 'Mulish', sans-serif;
  z-index: 1;
  
  /* Nếu Layout cha có padding, ta có thể dùng margin âm nhẹ để tràn viền NẾU CẦN THIẾT */
  /* Nhưng tốt nhất là sửa Layout cha bỏ padding cho trang Home, hoặc giữ margin âm này nếu Sếp thích full màn */
  margin: -30px -40px; 
  width: calc(100% + 80px);
}

/* Background Decor */
.bg-decor-wrapper {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  overflow: hidden; pointer-events: none; z-index: 0;
}
.glow-orb {
  position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.6;
  animation: floatOrb 10s infinite ease-in-out alternate;
}
.orb-1 { width: 600px; height: 600px; background: radial-gradient(circle, rgba(234, 88, 12, 0.15) 0%, rgba(234, 88, 12, 0) 70%); top: -100px; left: -100px; animation-duration: 12s; }
.orb-2 { width: 500px; height: 500px; background: radial-gradient(circle, rgba(251, 191, 36, 0.15) 0%, rgba(251, 191, 36, 0) 70%); top: 40%; right: -150px; animation-duration: 15s; animation-delay: 2s; }
.orb-3 { width: 700px; height: 700px; background: radial-gradient(circle, rgba(244, 63, 94, 0.08) 0%, rgba(244, 63, 94, 0) 70%); bottom: 10%; left: -200px; animation-duration: 18s; animation-delay: 1s; }
@keyframes floatOrb { 0% { transform: translate(0, 0) scale(1); } 100% { transform: translate(30px, 50px) scale(1.1); } }

.content-body { 
  max-width: 1400px; margin: 0 auto;
  padding: 40px 20px 0px; 
  position: relative; 
  z-index: 2;
  display: flex; 
  flex-direction: column; 
  gap: 30px; 
}

/* Newsletter */
.newsletter-section { max-width: 1200px; margin: 0 auto -80px; padding: 0 40px; position: relative; z-index: 10; }
.newsletter-content { background: #1C1917; border-radius: 35px; padding: 70px 40px; display: flex; align-items: center; justify-content: space-between; position: relative; overflow: hidden; box-shadow: 0 30px 60px -10px rgba(0,0,0,0.4); }
.nl-text h2 { font-family: 'Playfair Display', serif; color: white; font-size: 2.5rem; margin-bottom: 15px; }
.nl-text p { color: #D6D3D1; font-size: 1.1rem; max-width: 450px; line-height: 1.6; }
.nl-form { display: flex; gap: 12px; background: rgba(255,255,255,0.08); padding: 10px; border-radius: 60px; border: 1px solid rgba(255,255,255,0.15); backdrop-filter: blur(20px); z-index: 2; }
.nl-form input { background: transparent; border: none; outline: none; color: white; padding: 10px 25px; font-size: 1rem; width: 280px; }
.nl-form button { background: #EA580C; color: white; border: none; padding: 14px 32px; border-radius: 40px; font-weight: 800; cursor: pointer; transition: 0.3s; }
.nl-form button:hover { background: #F97316; transform: scale(1.05); }
.nl-decor-circle { position: absolute; top: -40%; right: -15%; width: 500px; height: 500px; border-radius: 50%; background: radial-gradient(circle, rgba(234, 88, 12, 0.25) 0%, transparent 70%); z-index: 1; pointer-events: none; }
.fade-in-scroll { animation: fadeInUp 1s ease-out; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(40px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 1024px) {
  .newsletter-content { flex-direction: column; text-align: center; padding: 60px 30px; }
  .nl-form { width: 100%; flex-direction: column; background: transparent; border: none; padding: 0; }
  .nl-form input { width: 100%; background: rgba(255,255,255,0.1); border-radius: 15px; margin-bottom: 15px; text-align: center; }
  .nl-form button { width: 100%; }
  .newsletter-section { margin-bottom: -40px; } 
}
</style>