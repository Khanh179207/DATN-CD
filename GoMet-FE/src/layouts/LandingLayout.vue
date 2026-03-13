<template>
  <div class="landing-layout" ref="layoutWrapper">
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

    <LandingFooter />

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
import { useRoute } from 'vue-router'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import Lenis from 'lenis'

import LandingHeader from '@/components/landing/LandingHeader.vue'
import LandingFooter from '@/components/landing/LandingFooter.vue'
import AuthModal from '@/components/modals/AuthModal.vue'

gsap.registerPlugin(ScrollTrigger)

const route = useRoute()
const showModal = ref(false)
const currentAuthView = ref('login')
const scrollProgress = ref(0)
const showBackToTop = ref(false)

let lenis = null // Biến lưu trữ Lenis toàn cục cho Layout

// --- XỬ LÝ MODAL AUTH ---
const handleOpenAuth = (viewName) => {
  currentAuthView.value = viewName
  showModal.value = true
}

// --- XỬ LÝ LOGIC CUỘN (SCROLL) ---
const handleScrollState = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  const scrollHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
  
  if (scrollHeight > 0) {
    scrollProgress.value = (scrollTop / scrollHeight) * 100;
  }
  showBackToTop.value = scrollTop > 500;
}

const scrollToTop = () => {
  if (lenis) {
    lenis.scrollTo(0, { duration: 1.5, easing: (t) => Math.min(1, 1.001 - Math.pow(2, -10 * t)) })
  } else {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}

const scrollToHash = (hash) => {
  if (!hash) return
  setTimeout(() => {
    const target = document.getElementById(hash.replace('#', ''))
    if (target && lenis) {
      lenis.scrollTo(target, { offset: 0, duration: 1.5 })
    } else if (target) {
      target.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }, 300) // Đợi DOM render xong
}

// --- KHỞI TẠO ---
onMounted(() => {
  // 1. Cài đặt Lenis Smooth Scroll cho TOÀN BỘ LAYOUT
  lenis = new Lenis({
    duration: 1.2, 
    easing: (t) => Math.min(1, 1.001 - Math.pow(2, -10 * t)), 
    direction: 'vertical',
    gestureDirection: 'vertical',
    smooth: true,
    smoothTouch: false, // Mobile mặc định đã mượt
  })

  // 2. Đồng bộ Lenis với GSAP và Vue State
  lenis.on('scroll', ScrollTrigger.update)
  lenis.on('scroll', handleScrollState) // Thay thế native event listener

  gsap.ticker.add((time) => {
    lenis.raf(time * 1000)
  })
  gsap.ticker.lagSmoothing(0) 

  // 3. Cuộn đến phần tử nếu có Hash URL ban đầu
  if (route.hash) {
    scrollToHash(route.hash)
  }
})

onUnmounted(() => {
  if (lenis) {
    lenis.destroy()
    gsap.ticker.remove((time) => lenis.raf(time * 1000))
  }
})
</script>

<style scoped>
/* --- TỐI ƯU CSS TOÀN CỤC CHO LANDING --- */
:global(html) {
  overflow-y: auto !important;
  overflow-x: hidden !important;
  height: auto !important;
}

:global(body) {
  min-height: 100vh !important;
  height: auto !important;
  overflow-y: visible !important;
  background-color: #FFFFFF;
  margin: 0;
  
  /* 🌟 TÙY CHỈNH THANH CUỘN (Gộp từ IntroPage sang đây để đồng nhất) */
  scrollbar-width: thin;
  scrollbar-color: #EA580C #FFF7ED;
}

/* Thanh cuộn cho Chrome/Safari/Edge */
:global(*::-webkit-scrollbar) { width: 8px; }
:global(*::-webkit-scrollbar-track) { background: #FFF7ED; }
:global(*::-webkit-scrollbar-thumb) { background: #EA580C; border-radius: 10px; }
:global(*::-webkit-scrollbar-thumb:hover) { background: #C2410C; }


.landing-layout { 
  display: flex; 
  flex-direction: column;
  width: 100%;
  position: relative;
  min-height: 100vh;
}

.landing-content {
  flex: 1; /* Đẩy footer xuống đáy nếu nội dung ngắn */
}

/* UI Tiến trình & Nút BackToTop */
.scroll-progress-container { position: fixed; top: 0; left: 0; width: 100%; height: 3px; z-index: 2000; }
.scroll-progress-bar { height: 100%; background: linear-gradient(to right, #F97316, #EA580C); width: 0%; transition: width 0.1s ease; }

.back-to-top { 
  position: fixed; bottom: 30px; right: 30px; width: 46px; height: 46px; 
  background: #1C1917; color: white; border-radius: 50%; border: none; 
  z-index: 999; opacity: 0; transform: translateY(20px); 
  transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1); 
  display: flex; align-items: center; justify-content: center; 
  cursor: pointer; box-shadow: 0 10px 25px rgba(0,0,0,0.2); 
}
.back-to-top.is-visible { opacity: 1; transform: translateY(0); }
.back-to-top:hover { background: #EA580C; transform: translateY(-5px); }

/* Transition Router */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.4s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(15px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-15px); }
</style>