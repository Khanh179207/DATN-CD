<template>
  <div class="app-container" :class="{ 'is-dark-theme': route.meta?.isDark }">
    
    <Transition name="fade-loader">
      <div v-if="isLoading" class="app-preloader">
        <div class="hearth-fire"></div>
        <div class="ambient-orb ambient-1"></div>
        <div class="ambient-orb ambient-2"></div>
        <div class="magic-dust-container">
          <div v-for="i in 12" :key="'dust-'+i" class="magic-dust"></div>
        </div>
        <div class="loader-content">
          <div class="logo-wrapper"><h2 class="loader-logo shine-text">GOMET</h2></div>
          <p class="loader-text">CHÀO MỪNG TỚI VỚI GOMET</p>
          <div class="progress-wrapper">
            <div class="progress-track">
              <div class="loader-progress" ref="progressBarRef"><div class="progress-glow-tip"></div></div>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <Sidebar 
      v-if="!isMobileView"
      class="fixed-sidebar" 
      @open-premium="showPremium = true" 
      @logout="handleLogout" 
    />

    <div class="main-content" id="main-scroll-container">
      
      <Header 
        v-if="!isMobileView"
        @open-premium="showPremium = true" 
        @open-login="openAuth('login')" 
        @open-register="openAuth('register')" 
        @logout="handleLogout"
      />
      <MobileHeader 
        v-else
        @open-premium="showPremium = true" 
        @open-login="openAuth('login')" 
        @open-register="openAuth('register')" 
        @open-store="showStoreModal = true"
        @logout="handleLogout"
      />

      <main class="page-body">
        <router-view v-slot="{ Component, route: currentRoute }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="currentRoute.fullPath" />
          </transition>
        </router-view>
      </main>

      <TheFooter />
    </div>

    <MobileMenu @open-premium="showPremium = true" />
    <ChatSidebar />
    <MiniChatBox />
    <CompareFloatingBar />
    <GometAiChat ref="aiChatRef" />

    <button class="float-ai-btn" @click="openAiChat" title="Chat with Gomet AI">
      <div class="ai-icon-bg">
        <span class="icon">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
        </span>
      </div>
      <span class="label">GOMET AI</span>
    </button>

    <Teleport to="body">
       <AuthModal v-if="showAuthModal" :initial-view="modalTab" @close="showAuthModal = false" />
       <PremiumModal :is-open="showPremium" @close="handleClosePremium" @upgraded="handleUpgraded" @start-test-timer="handleStartTestTimer" />
       <ExpiredModal :is-open="showExpired" @renew="handleRenew" @cancel="handleCancel" />
       <MealplanModal 
         v-if="showMealplanModal" 
         :post-data="mealplanData" 
         @close="showMealplanModal = false" 
       />
       
       <!-- NEW: GOMET STORE MODAL (Mạng lưới Pinia) -->
       <StoreModal v-if="uiStore.isStoreOpen" :is-open="uiStore.isStoreOpen" @close="uiStore.closeStore" />
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useChatStore } from '@/stores/chat' 
import { toast } from '@/composables/useToast' 
import gsap from 'gsap' 
import { ScrollTrigger } from 'gsap/ScrollTrigger'

import Sidebar from '@/components/sidebar/Sidebar.vue'
import Header from '@/components/topbar/Header.vue' 
import MobileHeader from '@/components/topbar/MobileHeader.vue' 
import MobileMenu from '@/components/sidebar/MobileMenu.vue' 

import AuthModal from '@/components/modals/AuthModal.vue'
import PremiumModal from '@/components/modals/PremiumModal.vue'
import ExpiredModal from '@/components/modals/ExpiredModal.vue'
import MealplanModal from '@/components/modals/MealplanModal.vue'
// 🔥 IMPORT STORE MODAL & UI STORE
import StoreModal from '@/components/modals/StoreModal.vue'
import { useUIStore } from '@/stores/ui'

import MiniChatBox from '@/components/chat/MiniChatBox.vue'
import ChatSidebar from '@/components/chat/ChatSidebar.vue'
import TheFooter from '@/components/footer/TheFooter.vue'
import CompareFloatingBar from '@/components/common/CompareFloatingBar.vue'
import GometAiChat from '@/components/chat/GometAiChat.vue'

gsap.registerPlugin(ScrollTrigger)

const router = useRouter()
const route = useRoute()
const chatStore = useChatStore() 

const isMobileView = ref(false)
const checkScreenSize = () => { isMobileView.value = window.innerWidth <= 1024; }

const isLoading = ref(false)
const progressBarRef = ref(null)
let ctx; 
let safetyTimer;

const startLoadingAnimation = () => {
  isLoading.value = true;
  sessionStorage.removeItem('just_logged_in'); 
  clearTimeout(safetyTimer);
  safetyTimer = setTimeout(() => { if (isLoading.value) isLoading.value = false; }, 4000);
  nextTick(() => {
    if (ctx) ctx.revert(); 
    ctx = gsap.context(() => {
      gsap.to(progressBarRef.value, { 
        width: '100%', duration: 2.2, ease: 'power2.inOut',
        onComplete: () => { clearTimeout(safetyTimer); setTimeout(() => { isLoading.value = false; }, 400); }
      });
    });
  });
};

const showAuthModal = ref(false); 
const showPremium = ref(false); 
const showExpired = ref(false); 
const isEnforcingRenewal = ref(false); 
const modalTab = ref('login'); 
const aiChatRef = ref(null);
const uiStore = useUIStore();

// 🔥 TRẠNG THÁI CHO MEALPLAN MODAL
const showMealplanModal = ref(false);
const mealplanData = ref(null);

const handleStartTestTimer = () => { setTimeout(() => { showExpired.value = true; isEnforcingRenewal.value = true; }, 12000); };
const handleRenew = () => { showExpired.value = false; showPremium.value = true; };
const handleClosePremium = () => { showPremium.value = false; if (isEnforcingRenewal.value) { showExpired.value = true; toast.error("Bạn cần gia hạn Premium!"); } };
const handleUpgraded = () => { isEnforcingRenewal.value = false; showPremium.value = false; showExpired.value = false; };
const handleCancel = () => { showExpired.value = false; isEnforcingRenewal.value = false; };

const handleOpenMealplan = (event) => { mealplanData.value = event.detail?.post || null; showMealplanModal.value = true; };
const handleRestorePrompt = (e) => {
  modalTab.value = 'restore-account'; showAuthModal.value = true;
  nextTick(() => { window.dispatchEvent(new CustomEvent('auth:restore-login-data', { detail: e.detail })); });
};

onMounted(() => {
  if (sessionStorage.getItem('just_logged_in') === 'true') startLoadingAnimation();
  window.addEventListener('ui:open-premium', () => { showPremium.value = true; })
  window.addEventListener('ui:open-store', () => { uiStore.openStore(); })
  
  // 🔥 LẮNG NGHE CÁC SỰ KIỆN TỪ HỆ THỐNG
  window.addEventListener('ui:open-mealplan', handleOpenMealplan)
  window.addEventListener('auth:restore-login-prompt', handleRestorePrompt)
  checkScreenSize();
  window.addEventListener('resize', checkScreenSize);
})

watch(() => route.fullPath, () => {
  document.body.style.overflow = '';
  document.documentElement.style.overflow = '';
  ScrollTrigger.refresh();
  const mainScroll = document.getElementById('main-scroll-container');
  if (mainScroll) mainScroll.scrollTo({ top: 0 });
  if (sessionStorage.getItem('just_logged_in') === 'true') startLoadingAnimation();
})

onUnmounted(() => {
  clearTimeout(safetyTimer);
  if (ctx) ctx.revert();
  window.removeEventListener('ui:open-mealplan', handleOpenMealplan);
  window.removeEventListener('auth:restore-login-prompt', handleRestorePrompt);
  window.removeEventListener('ui:open-store', () => { uiStore.openStore(); }); 
  window.removeEventListener('resize', checkScreenSize);
})

const openAiChat = () => { 
  const userStr = localStorage.getItem('user');
  let isPremiumUser = false;
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      isPremiumUser = user?.isPremium || user?.IsPremium || user?.role === 'PREMIUM' || user?.role === 'ADMIN';
    } catch (e) {}
  }
  if (!isPremiumUser) { showPremium.value = true; toast.warn("Gomet Assistant dành cho Premium sếp nhé!"); return; }
  if (aiChatRef.value) aiChatRef.value.openChat();
};

const openAuth = (tab) => { modalTab.value = tab; showAuthModal.value = true; };
const handleLogout = async () => { localStorage.clear(); sessionStorage.clear(); await router.push('/'); };
</script>

<style scoped>
/* PRELOADER CSS (GIỮ NGUYÊN) */
.app-preloader { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background-color: #FFF7ED; z-index: 99999; display: flex; justify-content: center; align-items: center; flex-direction: column; overflow: hidden; }
.is-dark-theme .app-preloader { background-color: #050505; }
.hearth-fire { position: absolute; bottom: -20vh; left: 0; width: 100%; height: 40vh; background: radial-gradient(ellipse at bottom, rgba(234, 88, 12, 0.4) 0%, transparent 70%); filter: blur(40px); animation: firePulse 3s infinite alternate; z-index: 0; }
.ambient-orb { position: absolute; border-radius: 50%; filter: blur(120px); opacity: 0.6; z-index: 1; animation: floatOrb 8s infinite alternate ease-in-out; }
.ambient-1 { width: 50vw; height: 50vw; background: radial-gradient(circle, rgba(234, 88, 12, 0.35) 0%, transparent 70%); top: -20%; left: -10%; }
.ambient-2 { width: 60vw; height: 60vw; background: radial-gradient(circle, rgba(245, 158, 11, 0.25) 0%, transparent 70%); bottom: -30%; right: -15%; animation-delay: -4s; }
.magic-dust-container { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 2; pointer-events: none; }
.magic-dust { position: absolute; bottom: -20px; width: 5px; height: 5px; background-color: #ffffff; border-radius: 50%; box-shadow: 0 0 15px 5px rgba(253, 186, 116, 0.9); opacity: 0; animation: magicFly 4s infinite cubic-bezier(0.4, 0, 0.2, 1); }
.loader-content { display: flex; flex-direction: column; align-items: center; justify-content: center; z-index: 10; position: relative; }
.loader-logo { font-family: 'Playfair Display', serif; font-size: 6rem; font-weight: 900; letter-spacing: 12px; margin: 0 0 15px -12px; color: #EA580C; position: relative; }
.shine-text::after { content: "GOMET"; position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: linear-gradient(100deg, #EA580C 20%, #F59E0B 40%, #FCD34D 60%, #EA580C 80%); background-size: 200% auto; -webkit-background-clip: text; -webkit-text-fill-color: transparent; animation: shineText 3.5s linear infinite; }
.loader-text { color: #9A3412; font-size: 1.1rem; font-weight: 700; letter-spacing: 4px; text-transform: uppercase; margin: 0 0 25px -4px; animation: breathe 2s infinite alternate; text-align: center; }
.progress-wrapper { width: 320px; padding: 10px 0; margin: 0 auto; }
.progress-track { width: 100%; height: 4px; background: #FFEDD5; border-radius: 10px; position: relative; }
.loader-progress { height: 100%; width: 0%; background: linear-gradient(90deg, #EA580C, #FCD34D); border-radius: 10px; position: relative; box-shadow: 0 0 15px rgba(234, 88, 12, 0.8); }
.progress-glow-tip { position: absolute; right: -6px; top: 50%; transform: translateY(-50%); width: 14px; height: 14px; background-color: #ffffff; border-radius: 50%; box-shadow: 0 0 12px 3px #ffffff, 0 0 25px 8px #FCD34D; }

@keyframes firePulse { 0% { transform: scaleY(1); opacity: 0.6; } 100% { transform: scaleY(1.3); opacity: 1; } }
@keyframes floatOrb { 0% { transform: translate(0, 0) scale(1); } 100% { transform: translate(30px, -20px) scale(1.1); } }
@keyframes magicFly { 0% { transform: translateY(0) scale(0.5); opacity: 0; } 100% { transform: translateY(-40vh) translateX(20px) scale(1.5); opacity: 0; } }
@keyframes shineText { to { background-position: 200% center; } }
@keyframes breathe { 0% { opacity: 0.5; } 100% { opacity: 1; } }

/* ====================================================
   🔥 FIX LAYOUT CƠ BẢN: ĐẢM BẢO KHÔNG MẤT TRANG HOME
   ==================================================== */

.app-container { 
  display: flex; 
  height: 100vh; 
  width: 100vw; 
  overflow: hidden; /* Chỉ cho phép container chứa Sidebar + Content */
  background-color: var(--color-neutral-0); 
  position: relative; 
}

.fixed-sidebar { flex-shrink: 0; height: 100vh; z-index: 100; }

.main-content { 
  flex: 1; 
  display: flex; 
  flex-direction: column; 
  height: 100vh; /* Quan trọng: Main content phải cao bằng màn hình */
  overflow-y: auto; /* CUỘN TRÊN MỤC NÀY */
  overflow-x: hidden; 
  position: relative;
  background-color: inherit;
}

/* NƠI CHỨA CÁC TRANG (HOME, SEARCH...) */
.page-body { 
  flex: 1 0 auto; /* ĐẢM BẢO PAGE BODY LUÔN DÃN NỞ THEO NỘI DUNG */
  width: 100%;
  position: relative;
}

@media (max-width: 1024px) {
  .main-content { margin-left: 0 !important; width: 100vw; }
}

.app-container.is-dark-theme { background-color: #000000 !important; }
.is-dark-theme .page-body { margin-top: 0; } /* Xóa bỏ negative margin nếu gây lỗi */

/* TRUYỀN HIỆU ỨNG TRANG (PAGE FADE) */
.page-fade-enter-active, .page-fade-leave-active { transition: opacity 0.3s ease; }
.page-fade-enter-from, .page-fade-leave-to { opacity: 0; }

/* FLOAT AI BUTTON */
.float-ai-btn { position: fixed; bottom: 32px; right: 32px; z-index: 99; display: flex; align-items: center; padding: 8px; background: white; border: 1px solid #e2e8f0; border-radius: 50px; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1); cursor: pointer; transition: all 0.3s; }
.ai-icon-bg { width: 44px; height: 44px; background: linear-gradient(135deg, #EA580C, #F59E0B); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; }
.label { font-weight: 800; color: #9A3412; max-width: 0; opacity: 0; white-space: nowrap; overflow: hidden; transition: all 0.3s; }
.float-ai-btn:hover .label { max-width: 200px; opacity: 1; margin-left: 12px; margin-right: 12px; }
</style>