<template>
  <div class="intro-page">
    
    <div class="sliding-background">
      <div class="bg-column slow down">
              <div class="image-track">
          <img src="@/assets/images/intro/bun.jpg" alt="Food">
          <img src="@/assets/images/intro/bunbo.jpg" alt="Food">
          <img src="@/assets/images/intro/banhmi.jpg" alt="Food">
          <img src="@/assets/images/intro/com.jpg" alt="Food">
          <img src="@/assets/images/intro/banh.jpg" alt="Food">
          <img src="@/assets/images/intro/goi.jpg" alt="Food">
        </div>
      </div>

      <div class="bg-column fast up offset-1">
        <div class="image-track">
                   <img src="@/assets/images/intro/bun.jpg" alt="Food">
          <img src="@/assets/images/intro/bunbo.jpg" alt="Food">
          <img src="@/assets/images/intro/banhmi.jpg" alt="Food">
          <img src="@/assets/images/intro/com.jpg" alt="Food">
          <img src="@/assets/images/intro/banh.jpg" alt="Food">
          <img src="@/assets/images/intro/goi.jpg" alt="Food">
        </div>
      </div>

      <div class="bg-column medium down">
        <div class="image-track">
                   <img src="@/assets/images/intro/bun.jpg" alt="Food">
          <img src="@/assets/images/intro/bunbo.jpg" alt="Food">
          <img src="@/assets/images/intro/banhmi.jpg" alt="Food">
          <img src="@/assets/images/intro/com.jpg" alt="Food">
          <img src="@/assets/images/intro/banh.jpg" alt="Food">
          <img src="@/assets/images/intro/goi.jpg" alt="Food">
        </div>
      </div>

       <div class="bg-column fast up offset-2">
        <div class="image-track">
                   <img src="@/assets/images/intro/bun.jpg" alt="Food">
          <img src="@/assets/images/intro/bunbo.jpg" alt="Food">
          <img src="@/assets/images/intro/banhmi.jpg" alt="Food">
          <img src="@/assets/images/intro/com.jpg" alt="Food">
          <img src="@/assets/images/intro/banh.jpg" alt="Food">
          <img src="@/assets/images/intro/goi.jpg" alt="Food">
        </div>
      </div>

      <div class="bg-column slow down">
        <div class="image-track">
                   <img src="@/assets/images/intro/bun.jpg" alt="Food">
          <img src="@/assets/images/intro/bunbo.jpg" alt="Food">
          <img src="@/assets/images/intro/banhmi.jpg" alt="Food">
          <img src="@/assets/images/intro/com.jpg" alt="Food">
          <img src="@/assets/images/intro/banh.jpg" alt="Food">
          <img src="@/assets/images/intro/goi.jpg" alt="Food">
        </div>
      </div>
    </div>

    <div class="content-scroll-wrapper">
      <LandingHero />
      <LandingSignupSection />
      <LandingFooter />
    </div>

  </div>
</template>

<script setup>
import LandingHero from '@/components/landing/LandingHero.vue'
import LandingSignupSection from '@/components/landing/LandingSignupSection.vue'
import LandingFooter from '@/components/landing/LandingFooter.vue'
</script>

<style scoped>
/* Thiết lập nền cố định */
.sliding-background {
  position: fixed; /* 🔥 Cố định, không cuộn theo trang */
  top: 0; left: 0; width: 100%; height: 100vh;
  z-index: 0; /* Nằm dưới cùng */
  display: flex; gap: 20px; padding: 0 20px;
  background: rgba(255,255,255,0.2); 
  backdrop-filter: blur(5px); /* Làm mờ nhẹ để nội dung dễ đọc */
}

/* Các class animation giữ nguyên */
.bg-column { flex: 1; position: relative; overflow: hidden; }
.offset-1 { margin-top: -100px; }
.offset-2 { margin-top: -50px; }
.image-track { display: flex; flex-direction: column; gap: 20px; will-change: transform; }
.image-track img { width: 100%; border-radius: 20px; object-fit: cover; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }

@keyframes slideUp { 0% { transform: translateY(0); } 100% { transform: translateY(-50%); } }
@keyframes slideDown { 0% { transform: translateY(-50%); } 100% { transform: translateY(0%); } }
.up .image-track { animation: slideUp linear infinite; }
.down .image-track { animation: slideDown linear infinite; }
.slow .image-track { animation-duration: 45s; }
.medium .image-track { animation-duration: 35s; }
.fast .image-track { animation-duration: 25s; }

/* Wrapper cho nội dung cuộn */
.content-scroll-wrapper {
  position: relative;
  z-index: 10; /* Nổi lên trên nền */
  /* Không set background để nhìn xuyên thấu xuống dưới */
}
</style>