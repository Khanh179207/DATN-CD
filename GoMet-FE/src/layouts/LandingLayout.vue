<template>
  <div class="landing-layout">
    <div class="scroll-progress-container">
      <div class="scroll-progress-bar" :style="{ width: scrollProgress + '%' }"></div>
    </div>

    <LandingHeader @open-auth="handleOpenAuth" />

    <main class="landing-content">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <button 
      class="back-to-top" 
      :class="{ 'is-visible': showBackToTop }" 
      @click="scrollToTop"
    >
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M18 15l-6-6-6 6"/></svg>
    </button>

    <Teleport to="body">
      <AuthModal v-if="showModal" :initialView="currentAuthView" @close="showModal = false" />
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import LandingHeader from '@/components/landing/LandingHeader.vue'
import AuthModal from '@/components/modals/AuthModal.vue'

const showModal = ref(false)
const currentAuthView = ref('login')
const scrollProgress = ref(0)
const showBackToTop = ref(false)

const handleOpenAuth = (viewName) => {
  currentAuthView.value = viewName
  showModal.value = true
}

const handleScroll = () => {
  // Use the most accurate scroll position for the browser
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  const scrollHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
  
  if (scrollHeight > 0) {
    scrollProgress.value = (scrollTop / scrollHeight) * 100;
  }
  showBackToTop.value = scrollTop > 400;
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

onMounted(() => {
  // Ensure scroll event is listened on the entire window
  window.addEventListener('scroll', handleScroll, { passive: true });
  // Run once to update initial state
  handleScroll();
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
})
</script>

<style scoped>
/* --- ✨ STRATEGY: UNLOCK SCROLLING BUT HIDE SCROLLBAR ✨ --- */

:global(html) {
  /* Allow smooth vertical scrolling */
  overflow-y: auto !important;
  overflow-x: hidden !important;
  height: auto !important;
}

:global(body) {
  /* Ensure body height is not blocked */
  min-height: 100vh !important;
  height: auto !important;
  overflow-y: visible !important;
  background-color: #FFFFFF;
  margin: 0;
  
  /* Hide scrollbar Firefox */
  scrollbar-width: none !important;
  /* Hide scrollbar IE/Edge */
  -ms-overflow-style: none !important;
}

/* Hide scrollbar Chrome/Safari for EVERYTHING */
:global(*::-webkit-scrollbar) {
  display: none !important;
}

.landing-layout { 
  display: flex; 
  flex-direction: column;
  width: 100%;
  position: relative;
}

/* Decorative styles kept as-is */
.scroll-progress-container { position: fixed; top: 0; left: 0; width: 100%; height: 3px; z-index: 2000; }
.scroll-progress-bar { height: 100%; background: linear-gradient(to right, #F97316, #EA580C); width: 0%; transition: width 0.1s ease; }
.back-to-top { position: fixed; bottom: 30px; right: 30px; width: 46px; height: 46px; background: #1C1917; color: white; border-radius: 50%; border: none; z-index: 999; opacity: 0; transform: translateY(20px); transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1); display: flex; align-items: center; justify-content: center; cursor: pointer; box-shadow: 0 10px 25px rgba(0,0,0,0.2); }
.back-to-top.is-visible { opacity: 1; transform: translateY(0); }
.back-to-top:hover { background: #F97316; transform: translateY(-5px); }

.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.4s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(15px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-15px); }
</style>