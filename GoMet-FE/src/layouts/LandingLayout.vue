<template>
  <div class="landing-layout" ref="layoutWrapper">
    <div class="custom-cursor" ref="cursor"></div>

    <div class="scroll-progress-container">
      <div class="scroll-progress-bar" :style="{ width: scrollProgress + '%' }"></div>
    </div>

    <LandingHeader @open-auth="handleOpenAuth" />

    <main class="landing-content">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <LandingFooter />

    <div 
      class="progress-wrap" 
      :class="{ 'active-progress': showBackToTop }"
      @click="scrollToTop"
    >
      <svg class="progress-circle svg-content" width="100%" height="100%" viewBox="-1 -1 102 102">
        <path 
          d="M50,1 a49,49 0 0,1 0,98 a49,49 0 0,1 0,-98" 
          stroke="#EA580C"
          stroke-width="4"
          fill="none"
          style="transition: stroke-dashoffset 10ms linear 0s; stroke-dasharray: 307.919, 307.919;"
          :style="{ strokeDashoffset: 307.919 - (scrollProgress * 307.919) / 100 }"
        ></path>
      </svg>
      <div class="progress-icon">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M18 15l-6-6-6 6"/></svg>
      </div>
    </div>

    <Teleport to="body">
      <AuthModal v-if="showModal" :initialView="currentAuthView" @close="showModal = false" />
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import gsap from 'gsap'
import Lenis from 'lenis'
import LandingHeader from '@/components/landing/LandingHeader.vue'
import LandingFooter from '@/components/landing/LandingFooter.vue'
import AuthModal from '@/components/modals/AuthModal.vue'

const showModal = ref(false)
const currentAuthView = ref('login')
const scrollProgress = ref(0)
const showBackToTop = ref(false)
const cursor = ref(null)
let lenis = null

// --- XỬ LÝ CURSOR ---
const initCursor = () => {
  const moveCursor = (e) => {
    gsap.to(cursor.value, { x: e.clientX, y: e.clientY, duration: 0.3, ease: 'power2.out' })
  }
  window.addEventListener('mousemove', moveCursor)
  
  document.addEventListener('mouseover', (e) => {
    if (e.target.closest('a, button, .nav-item')) cursor.value?.classList.add('cursor-hover')
    else cursor.value?.classList.remove('cursor-hover')
  })
}

// --- XỬ LÝ SCROLL (Sử dụng e.progress của Lenis cho chuẩn) ---
const handleScroll = (e) => {
  scrollProgress.value = (e.progress || 0) * 100;
  showBackToTop.value = e.animatedScroll > 300;
}

const scrollToTop = () => {
  lenis?.scrollTo(0, { duration: 1.5 })
}

const handleOpenAuth = (v) => { currentAuthView.value = v; showModal.value = true; }

onMounted(() => {
  // 🔥 MỞ KHÓA CSS TOÀN CỤC CHO RIÊNG LANDING PAGE 🔥
  // Ép HTML và Body dùng lại thanh cuộn mặc định, tắt giới hạn chiều cao
  document.documentElement.style.setProperty('overflow', 'auto', 'important');
  document.documentElement.style.setProperty('height', 'auto', 'important');
  document.documentElement.style.setProperty('scroll-behavior', 'auto', 'important'); // Tắt mâu thuẫn với Lenis

  document.body.style.setProperty('overflow', 'auto', 'important');
  document.body.style.setProperty('height', 'auto', 'important');

  // Khởi tạo Lenis bám vào window gốc
  lenis = new Lenis({ duration: 1.2, smooth: true })
  lenis.on('scroll', handleScroll)
  gsap.ticker.add((time) => lenis.raf(time * 1000))
  
  initCursor()
})

onUnmounted(() => {
  // 🔥 DỌN DẸP TRẢ LẠI GIAO DIỆN APP KHI THOÁT LANDING 🔥
  document.documentElement.style.removeProperty('overflow');
  document.documentElement.style.removeProperty('height');
  document.documentElement.style.removeProperty('scroll-behavior');

  document.body.style.removeProperty('overflow');
  document.body.style.removeProperty('height');

  lenis?.destroy() 
})
</script>

<style scoped>
/* --- 1. PROGRESS CIRCLE (SỐ 3) --- */
.progress-wrap {
  position: fixed; right: 30px; bottom: 30px; height: 46px; width: 46px;
  cursor: pointer; display: block; border-radius: 50%; z-index: 1000;
  opacity: 0; visibility: hidden; transform: translateY(15px);
  transition: all 400ms cubic-bezier(0.22, 1, 0.36, 1);
  background: white;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}
.progress-wrap.active-progress { opacity: 1; visibility: visible; transform: translateY(0); }
.progress-circle { transform: rotate(-90deg); }
.progress-icon {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center; color: #EA580C;
}

/* --- 2. CUSTOM CURSOR --- */
.custom-cursor {
  position: fixed; width: 30px; height: 30px; border: 2px solid #EA580C;
  border-radius: 50%; pointer-events: none; z-index: 9999; transform: translate(-50%, -50%);
  transition: width 0.3s, height 0.3s, background 0.3s;
}
.custom-cursor.cursor-hover { width: 60px; height: 60px; background: rgba(234, 88, 12, 0.1); }

/* --- 3. SCROLL PROGRESS BAR --- */
.scroll-progress-container { position: fixed; top: 0; left: 0; width: 100%; height: 3px; z-index: 2000; }
.scroll-progress-bar { height: 100%; background: #EA580C; box-shadow: 0 0 10px rgba(234, 88, 12, 0.5); }

/* --- 4. PAGE TRANSITION --- */
.page-fade-enter-active { transition: all 0.6s ease-out; }
.page-fade-enter-from { opacity: 0; transform: translateY(10px); }

@media (max-width: 768px) { .custom-cursor { display: none; } }
</style>