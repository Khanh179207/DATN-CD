<template>
  <div class="intro-page">
    <div class="content-scroll-wrapper">
      
      <LandingHero />
      
      <LandingIdeaSection />    
      
      <section id="sectionsigninlanding">
        <LandingSignupSection />
      </section>

      </div>
  </div>
</template>

<script setup>
import LandingHero from '@/components/landing/LandingHero.vue'
import LandingIdeaSection from '@/components/landing/LandingIdeaSection.vue'
import LandingSignupSection from '@/components/landing/LandingSignupSection.vue'

// Không cần Lenis, không cần GSAP ở file này nữa.
// Mọi thứ đã được LandingLayout lo!
</script>

<style scoped>
.intro-page {
  width: 100%;
}
/* Scrollbar CSS đã chuyển sang LandingLayout */
</style>