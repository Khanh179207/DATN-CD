<template>
  <div class="intro-page">
    <div class="content-scroll-wrapper">
      <LandingHero />
      <LandingIdeaSection />   
      <LandingIdeaSection2 />  
      <LandingIdeaSection3 /> 
      
      <section id="sectionsigninlanding">
        <LandingSignupSection />
      </section>
      
      <LandingFooter />
    </div>
  </div>
</template>

<script setup>
// Keep script for handling scroll to hash
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import LandingHero from '@/components/landing/LandingHero.vue'
import LandingSignupSection from '@/components/landing/LandingSignupSection.vue'
import LandingFooter from '@/components/landing/LandingFooter.vue'
import LandingIdeaSection from '@/components/landing/LandingIdeaSection.vue'
import LandingIdeaSection2 from '@/components/landing/LandingIdeaSection2.vue'
import LandingIdeaSection3 from '@/components/landing/LandingIdeaSection3.vue'

const route = useRoute()

const scrollToSection = (hash) => {
  if (!hash) return
  setTimeout(() => {
    const element = document.getElementById(hash.replace('#', ''))
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }, 300)
}

onMounted(() => {
  if (route.hash) {
    scrollToSection(route.hash)
  }
})
</script>

<style scoped>
/* No complex styles needed here since background has been moved */
.intro-page {
  width: 100%;
}
</style>