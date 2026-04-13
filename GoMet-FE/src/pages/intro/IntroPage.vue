<template>
  <div class="intro-page">
    <div class="content-scroll-wrapper">
      <section class="section-wrapper hero-wrapper">
        <LandingHero />
      </section>

      <section class="section-wrapper idea-wrapper">
        <LandingIdeaSection />    
      </section>
      
      <section id="sectionsigninlanding" class="section-wrapper signup-wrapper">
        <LandingSignupSection />
      </section>
    </div>
  </div>
</template>

<script setup>
import LandingHero from '@/components/landing/LandingHero.vue'
import LandingIdeaSection from '@/components/landing/LandingIdeaSection.vue'
import LandingSignupSection from '@/components/landing/LandingSignupSection.vue'
</script>

<style scoped>
.intro-page {
  width: 100%;
  position: relative;
  overflow-x: hidden;
  background-color: #FAF9F6;
}

.content-scroll-wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: stretch; 
}

.section-wrapper {
  position: relative;
  width: 100%;
  z-index: 1;
  display: block;
}
.signup-wrapper {
  z-index: 2;
  margin-top: 0 !important;
}

@media (max-width: 1024px) {
  .intro-page {
    height: auto; 
    min-height: 100vh;
  }
}
</style>