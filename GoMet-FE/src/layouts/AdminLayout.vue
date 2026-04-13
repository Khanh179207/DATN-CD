<template>
  <div class="admin-layout-sovereign">
    
    <Transition name="fade-loader">
      <div v-show="isLoading" class="admin-preloader">
        <div class="hearth-fire"></div>
        <div class="ambient-orb ambient-1"></div>
        <div class="ambient-orb ambient-2"></div>
        <div class="magic-dust-container">
          <div v-for="i in 12" :key="'dust-'+i" class="magic-dust"></div>
        </div>
        <div class="loader-content">
          <div class="logo-wrapper">
            <h2 class="loader-logo shine-text">GOMET ADMIN</h2>
          </div>
          <p class="loader-text">{{ t('auth.topbar.control_center') }}</p>
          <div class="progress-wrapper">
            <div class="progress-track">
              <div class="loader-progress" ref="progressBarRef">
                <div class="progress-glow-tip"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <AdminSidebar class="fixed-sidebar" @toggle-collapse="handleToggleCollapse" />

    <main class="main-content-engine" :class="{ 'expanded-mode': isSidebarCollapsed }">
      
      <div class="ambient-backdrop">
        <div class="glow-source-orange"></div>
        <div class="glow-source-white"></div>
        <div class="noise-texture"></div>
      </div>

      <div class="header-dock">
        <HeaderAdmin />
      </div>

      <div class="content-viewport custom-scrollbar">
        <router-view v-slot="{ Component, route }">
          <transition name="page-slide" mode="out-in">
            <component :is="Component" :key="route.fullPath" />
          </transition>
        </router-view>
      </div>

    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
import gsap from 'gsap'
import AdminSidebar from '@/components/sidebar/SidebarAdmin.vue'
import HeaderAdmin from '@/components/topbar/HeaderAdmin.vue'

const { t } = useI18n()

// --- 🚀 LOGIC COLLAPSE SIDEBAR ---
const isSidebarCollapsed = ref(localStorage.getItem('gomet_admin_sidebar_collapsed') === 'true');

const handleToggleCollapse = (val) => {
  isSidebarCollapsed.value = val;
}

// --- 🚀 LOGIC LOADING ADMIN (CHỐNG ĐƠ WEB) ---
const isLoading = ref(true)
const progressBarRef = ref(null)

let ctx; // Gom GSAP vào context
let safetyTimer; // Bộ đếm an toàn

onMounted(() => {
  // Xóa cờ từ login để ra MainLayout không bị load lại
  sessionStorage.removeItem('just_logged_in')

  // LƯỚI AN TOÀN
  safetyTimer = setTimeout(() => {
    if (isLoading.value) isLoading.value = false;
  }, 3500);

  nextTick(() => {
    ctx = gsap.context(() => {
      if (progressBarRef.value) {
        gsap.to(progressBarRef.value, {
          width: '100%',
          duration: 1.8, 
          ease: 'power3.inOut',
          onComplete: () => {
            clearTimeout(safetyTimer);
            isLoading.value = false;
          }
        });
      }
    });
  });
})

onUnmounted(() => {
  clearTimeout(safetyTimer);
  if (ctx) ctx.revert();
})
</script>

<style scoped>
/* =======================================================
   🌟 MÀN HÌNH LOADING ADMIN (ELITE GOMET - PITCH BLACK)
======================================================= */
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@900&family=Inter:wght@400;700;800&display=swap');

.admin-preloader {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; z-index: 99999;
  display: flex; justify-content: center; align-items: center; flex-direction: column; overflow: hidden;
  background-color: #030303; 
  background-image: radial-gradient(circle at center, rgba(60, 30, 10, 0.9) 0%, #030303 100%);
}

.hearth-fire { position: absolute; bottom: -20vh; left: 0; width: 100%; height: 40vh; background: radial-gradient(ellipse at bottom, rgba(234, 88, 12, 0.4) 0%, transparent 70%); filter: blur(40px); animation: firePulse 3s infinite alternate ease-in-out; z-index: 0; }
.ambient-orb { position: absolute; border-radius: 50%; filter: blur(120px); opacity: 0.3; z-index: 1; animation: floatOrb 8s infinite alternate ease-in-out; }
.ambient-1 { width: 50vw; height: 50vw; background: radial-gradient(circle, rgba(234, 88, 12, 0.5) 0%, transparent 70%); top: -20%; left: -10%; }
.ambient-2 { width: 60vw; height: 60vw; background: radial-gradient(circle, rgba(245, 158, 11, 0.3) 0%, transparent 70%); bottom: -30%; right: -15%; animation-delay: -4s; }

.magic-dust-container { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 2; pointer-events: none; }
.magic-dust { position: absolute; bottom: -20px; width: 4px; height: 4px; background-color: #ffffff; border-radius: 50%; box-shadow: 0 0 15px 5px rgba(253, 186, 116, 0.9); opacity: 0; animation: magicFly 4s infinite cubic-bezier(0.4, 0, 0.2, 1); }
.magic-dust:nth-child(1) { left: 10%; animation-duration: 3.5s; animation-delay: 0.2s; } .magic-dust:nth-child(2) { left: 25%; animation-duration: 4.8s; animation-delay: 1.1s; } .magic-dust:nth-child(3) { left: 40%; animation-duration: 3.9s; animation-delay: 0.5s; } .magic-dust:nth-child(4) { left: 55%; animation-duration: 5.2s; animation-delay: 1.8s; } .magic-dust:nth-child(5) { left: 70%; animation-duration: 4.5s; animation-delay: 0.9s; } .magic-dust:nth-child(6) { left: 85%; animation-duration: 3.7s; animation-delay: 2.5s; }

.loader-content { position: relative; z-index: 10; text-align: center; display: flex; flex-direction: column; align-items: center; gap: 25px; transform: translateY(-5vh); }
.loader-logo { font-family: 'Playfair Display', serif; font-size: 7rem; font-weight: 900; letter-spacing: 20px; margin: 0; background: linear-gradient(to bottom, #FFD07F 0%, #FFFFFF 45%, #F59E0B 55%, #78350F 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; -webkit-text-stroke: 1px rgba(255, 255, 255, 0.4); filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 1)) drop-shadow(0 0 15px rgba(234, 88, 12, 0.8)) drop-shadow(0 0 40px rgba(234, 88, 12, 0.3)); position: relative; animation: logoEntrance 1.5s cubic-bezier(0.19, 1, 0.22, 1) forwards; }
.shine-text::after { content: "GOMET ADMIN"; position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: linear-gradient(110deg, transparent 35%, rgba(255, 255, 255, 0.9) 45%, rgba(255, 255, 255, 1) 50%, rgba(255, 255, 255, 0.9) 55%, transparent 65%); background-size: 200% auto; -webkit-background-clip: text; -webkit-text-fill-color: transparent; animation: shineSweep 3.5s linear infinite; }
.loader-text { color: #fff; font-family: 'Inter', sans-serif; font-size: 0.85rem; font-weight: 800; letter-spacing: 12px; text-transform: uppercase; margin-top: 5px; opacity: 0.8; position: relative; padding-bottom: 12px; }
.loader-text::after { content: ""; position: absolute; bottom: 0; left: 20%; width: 60%; height: 1px; background: linear-gradient(90deg, transparent, #FCD34D, transparent); box-shadow: 0 0 10px #EA580C; }

.progress-wrapper { margin-top: 30px; width: 320px; }
.progress-track { width: 100%; height: 2px; background: rgba(255, 255, 255, 0.05); border-radius: 10px; position: relative; overflow: visible; }
.loader-progress { height: 100%; width: 0%; background: linear-gradient(90deg, #EA580C, #FCD34D, #ffffff); border-radius: 10px; position: relative; box-shadow: 0 0 20px rgba(234, 88, 12, 1); }
.progress-glow-tip { position: absolute; right: -5px; top: 50%; transform: translateY(-50%); width: 12px; height: 12px; background-color: #ffffff; border-radius: 50%; box-shadow: 0 0 15px 2px #ffffff, 0 0 30px 8px #fb923c; }

.fade-loader-enter-active, .fade-loader-leave-active { transition: opacity 0.8s cubic-bezier(0.19, 1, 0.22, 1), transform 0.8s cubic-bezier(0.19, 1, 0.22, 1); }
.fade-loader-leave-to { opacity: 0; transform: scale(1.15); }

@keyframes firePulse { 0% { transform: scaleY(1); opacity: 0.5; } 100% { transform: scaleY(1.4); opacity: 0.9; } }
@keyframes floatOrb { 0% { transform: translate(0, 0) scale(1); } 100% { transform: translate(30px, -20px) scale(1.1); } }
@keyframes shineSweep { 0% { background-position: -200% center; } 100% { background-position: 200% center; } }
@keyframes logoEntrance { 0% { opacity: 0; transform: scale(0.9) translateY(20px); filter: blur(15px); } 100% { opacity: 1; transform: scale(1) translateY(0); filter: blur(0px); } }
@keyframes magicFly { 0% { transform: translateY(0) scale(0.5); opacity: 0; } 20% { opacity: 1; } 100% { transform: translateY(-45vh) translateX(25px) scale(1.5); opacity: 0; } }

/* =======================================================
   🏛️ CORE LAYOUT ADMIN
======================================================= */
.admin-layout-sovereign { 
  display: flex; 
  min-height: 100vh; 
  width: 100vw; 
  max-width: 100vw; 
  background: #F8F9FB; 
  overflow: hidden; 
  font-family: 'Inter', sans-serif; 
}

.fixed-sidebar { 
  position: fixed; top: 0; left: 0; bottom: 0; 
  /* Chiều rộng thật của Sidebar được quyết định bên trong component SidebarAdmin.vue */
  z-index: 100; flex-shrink: 0; 
}

/* 🔥 TRẠNG THÁI MỞ RỘNG (MẶC ĐỊNH) */
.main-content-engine { 
  flex: 1; 
  margin-left: 270px; 
  width: calc(100vw - 270px); 
  display: flex; flex-direction: column; height: 100vh; position: relative; z-index: 1; 
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); /* Hiệu ứng co giãn mượt */
}

/* 🔥 TRẠNG THÁI THU GỌN SIDEBAR */
.main-content-engine.expanded-mode {
  margin-left: 85px;
  width: calc(100vw - 85px);
}

.ambient-backdrop { position: absolute; inset: 0; pointer-events: none; z-index: -1; overflow: hidden; background: #F8F9FB; }
.glow-source-orange { position: absolute; top: -150px; left: -100px; width: 800px; height: 800px; background: radial-gradient(circle, rgba(234, 88, 12, 0.06) 0%, transparent 60%); filter: blur(100px); }

.header-dock { flex-shrink: 0; position: sticky; top: 0; z-index: 50; backdrop-filter: blur(15px); background: rgba(248, 249, 251, 0.8); border-bottom: 1px solid rgba(0,0,0,0.03); }

/* KHU VỰC CUỘN DỌC CHÍNH CỦA APP */
.content-viewport { 
  flex: 1; overflow-y: auto; overflow-x: hidden; padding: 0; position: relative; scroll-behavior: smooth; 
}

/* Custom Scrollbar */
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(203, 213, 225, 0.5); border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(148, 163, 184, 0.8); }

/* Page Transitions */
.page-slide-enter-active, .page-slide-leave-active { transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1); }
.page-slide-enter-from { opacity: 0; transform: translateY(20px) scale(0.98); }
.page-slide-leave-to { opacity: 0; transform: translateY(-10px); }

/* --- RESPONSIVE --- */
@media (max-width: 1024px) {
  .main-content-engine, .main-content-engine.expanded-mode {
    margin-left: 0; width: 100vw; 
  }
}
</style>