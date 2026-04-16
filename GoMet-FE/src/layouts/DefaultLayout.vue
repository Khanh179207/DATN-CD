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
       <MealplanModal v-if="showMealplanModal" :post-data="mealplanData" @close="showMealplanModal = false" />
       <StoreModal v-if="uiStore.isStoreOpen" @close="uiStore.closeStore" />
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import { useUIStore } from '@/stores/ui'
import { toast } from '@/composables/useToast' 
import gsap from 'gsap' 
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import webSocketService from '@/services/webSocketService'
import { ensureBrowserNotificationPermission } from '@/services/browserNotificationService'

import Sidebar from '@/components/sidebar/Sidebar.vue'
import Header from '@/components/topbar/Header.vue' 
import MobileHeader from '@/components/topbar/MobileHeader.vue' 
import MobileMenu from '@/components/sidebar/MobileMenu.vue' 
import AuthModal from '@/components/modals/AuthModal.vue'
import PremiumModal from '@/components/modals/PremiumModal.vue'
import ExpiredModal from '@/components/modals/ExpiredModal.vue'
import MealplanModal from '@/components/modals/MealplanModal.vue'
import StoreModal from '@/components/modals/StoreModal.vue'
import MiniChatBox from '@/components/chat/MiniChatBox.vue'
import ChatSidebar from '@/components/chat/ChatSidebar.vue'
import TheFooter from '@/components/footer/TheFooter.vue'
import CompareFloatingBar from '@/components/common/CompareFloatingBar.vue'
import GometAiChat from '@/components/chat/GometAiChat.vue'

gsap.registerPlugin(ScrollTrigger)

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const chatStore = useChatStore()
const uiStore = useUIStore()

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
  checkScreenSize();
  window.addEventListener('resize', checkScreenSize);
  
  if (sessionStorage.getItem('just_logged_in') === 'true') startLoadingAnimation();
  
  ensureBrowserNotificationPermission()
  
  window.addEventListener('ui:open-premium', () => { showPremium.value = true; })
  window.addEventListener('ui:open-store', () => { uiStore.openStore(); })
  window.addEventListener('ui:open-mealplan', handleOpenMealplan)
  window.addEventListener('auth:restore-login-prompt', handleRestorePrompt)

  if (authStore.isAuthenticated) {
    webSocketService.connect();
  }
})

onUnmounted(() => {
  clearTimeout(safetyTimer);
  if (ctx) ctx.revert();
  window.removeEventListener('resize', checkScreenSize);
  window.removeEventListener('ui:open-mealplan', handleOpenMealplan);
  window.removeEventListener('auth:restore-login-prompt', handleRestorePrompt);
  webSocketService.disconnect();
})

watch(() => route.fullPath, () => {
  document.body.style.overflow = '';
  document.documentElement.style.overflow = '';
  ScrollTrigger.refresh();
  const mainScroll = document.getElementById('main-scroll-container');
  if (mainScroll) mainScroll.scrollTo({ top: 0 });
  if (sessionStorage.getItem('just_logged_in') === 'true') startLoadingAnimation();
})

watch(() => authStore.isAuthenticated, (val) => {
  if (val) webSocketService.connect();
  else webSocketService.disconnect();
})

const openAiChat = () => { 
  const isPremiumUser = authStore.user?.isPremium || authStore.user?.IsPremium || authStore.user?.role === 'PREMIUM' || authStore.user?.role === 'ADMIN';
  if (!isPremiumUser) { 
    showPremium.value = true; 
    toast.warn("Gomet Assistant dành cho Premium sếp nhé!"); 
    return; 
  }
  if (aiChatRef.value) aiChatRef.value.openChat();
};

const openAuth = (tab) => { modalTab.value = tab; showAuthModal.value = true; };
const handleLogout = async () => { authStore.logout(); await router.push('/'); };
</script>

<style scoped lang="scss">
@import '@/assets/styles/main.scss';

.app-container { 
  display: flex; height: 100vh; width: 100vw; overflow: hidden;
  background-color: var(--color-neutral-0); position: relative; 
}

.fixed-sidebar { flex-shrink: 0; height: 100vh; z-index: 100; }
.main-content { 
  flex: 1; display: flex; flex-direction: column; height: 100vh;
  overflow-y: auto; overflow-x: hidden; position: relative;
  background-color: inherit;
}
.page-body { flex: 1 0 auto; width: 100%; position: relative; }

.float-ai-btn { 
  position: fixed; bottom: 32px; right: 32px; z-index: 99; display: flex; align-items: center; 
  padding: 8px; background: white; border: 1px solid #e2e8f0; border-radius: 50px; 
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1); cursor: pointer; transition: all 0.3s; 
}
.ai-icon-bg { width: 44px; height: 44px; background: linear-gradient(135deg, #EA580C, #F59E0B); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; }
.label { font-weight: 800; color: #9A3412; max-width: 0; opacity: 0; white-space: nowrap; overflow: hidden; transition: all 0.3s; }
.float-ai-btn:hover .label { max-width: 200px; opacity: 1; margin-left: 12px; margin-right: 12px; }

.app-container.is-dark-theme { background-color: #000000 !important; }
.page-fade-enter-active, .page-fade-leave-active { transition: opacity 0.3s ease; }
.page-fade-enter-from, .page-fade-leave-to { opacity: 0; }

.app-preloader { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background-color: #FFF7ED; z-index: 99999; display: flex; justify-content: center; align-items: center; flex-direction: column; overflow: hidden; }
.is-dark-theme .app-preloader { background-color: #050505; }
.loader-logo { font-family: 'Playfair Display', serif; font-size: 6rem; font-weight: 900; letter-spacing: 12px; color: #EA580C; }
</style>
