<template>
  <div class="app-container">
    
    <Sidebar 
      class="fixed-sidebar" 
      @open-premium="showPremium = true" 
      @logout="handleLogout" 
    />

    <div class="main-content" id="main-scroll-container">
      <Header 
        @open-premium="showPremium = true" 
        @open-login="openAuth('login')" 
        @open-register="openAuth('register')" 
        @logout="handleLogout"
      />

      <div class="page-body">
        <router-view v-slot="{ Component, route }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="route.fullPath" />
          </transition>
        </router-view>
      </div>

      <TheFooter />
    </div>

    <MiniChatBox />
    <CompareFloatingBar />

    <button 
      v-if="!isAiChatting"
      class="float-ai-btn" 
      @click="openAiChat"
      title="Chat with Gomet AI"
    >
      <div class="ai-icon-bg">
        <span class="icon">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
        </span>
      </div>
      <span class="label">GoMet Assistant</span>
    </button>

    <Teleport to="body">
       <AuthModal 
         v-if="showAuthModal" 
         :initial-view="modalTab"
         @close="showAuthModal = false" 
       />
       <PremiumModal 
         :is-open="showPremium" 
         @close="showPremium = false"
         @upgraded="showPremium = false"
       />

       <!-- Ban notification modal — non-dismissable -->
       <div v-if="showBannedModal" class="banned-overlay">
         <div class="banned-box">
           <div class="banned-icon">🔒</div>
           <h2 class="banned-title">Tài khoản bị khóa</h2>
           <p class="banned-msg">Tài khoản bạn đã bị khóa. Vui lòng liên hệ admin để biết thêm chi tiết.</p>
           <button class="banned-btn" @click="handleBannedLogout">Đóng &amp; đăng xuất</button>
         </div>
       </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth'
import { checkAccountStatus } from '@/services/authService'

import Sidebar from '@/components/sidebar/Sidebar.vue'
import Header from '@/components/topbar/Header.vue' 
import AuthModal from '@/components/modals/AuthModal.vue'
import PremiumModal from '@/components/modals/PremiumModal.vue'
import MiniChatBox from '@/components/modals/MiniChatBox.vue'
import TheFooter from '@/components/footer/TheFooter.vue'
import CompareFloatingBar from '@/components/common/CompareFloatingBar.vue'

const router = useRouter()
const chatStore = useChatStore()
const authStore = useAuthStore()

const showAuthModal  = ref(false)
const showPremium    = ref(false)
const modalTab       = ref('login')
const showBannedModal = ref(false)

// ── Ban detection polling ────────────────────────────────────────────────────
let banPollTimer = null

const startBanPolling = () => {
  if (banPollTimer) return
  banPollTimer = setInterval(async () => {
    if (!authStore.isAuthenticated) return
    try {
      const { isActive } = await checkAccountStatus()
      if (!isActive) {
        clearInterval(banPollTimer)
        banPollTimer = null
        showBannedModal.value = true
      }
    } catch {
      // network error — silently ignore, retry next tick
    }
  }, 30_000)
}

const handleBannedLogout = () => {
  showBannedModal.value = false
  authStore._clearAll()
  router.push('/').catch(() => {})
}

onMounted(() => {
  if (authStore.isAuthenticated) startBanPolling()
})

onUnmounted(() => {
  if (banPollTimer) { clearInterval(banPollTimer); banPollTimer = null }
})

const isAiChatting = computed(() => chatStore.activeChat?.id === 'gomet-ai')

const openAiChat = () => {
  const aiBot = {
    id: 'gomet-ai',
    name: 'Gomet AI 🤖',
    avatar: 'https://cdn-icons-png.flaticon.com/512/4712/4712027.png', 
    isOnline: true,
    lastMessage: 'Hello, how can I help you?'
  }
  chatStore.openChat(aiBot)
}

const openAuth = (tab) => { 
  modalTab.value = tab
  showAuthModal.value = true 
}

const handleLogout = async () => {
  localStorage.removeItem('user')
  localStorage.removeItem('token')
  await router.push({ path: '/', hash: '#sectionsigninlanding' });
}
</script>

<style scoped>
/* ─── Layout Shell ─── */
.app-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: var(--color-neutral-0);
  font-family: var(--font-body);
  color: var(--color-neutral-900);
  position: relative;
}

.fixed-sidebar {
  flex-shrink: 0;
  z-index: var(--z-toast);
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow-y: auto;
  scroll-behavior: smooth;
  position: relative;
}

.page-body {
  padding: 0;
  flex: 1;
  position: relative;
  width: 100%;
}

/* ─── Page Transition ─── */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity var(--duration-normal) var(--ease-out),
              transform var(--duration-normal) var(--ease-out);
}
.page-fade-enter-from { opacity: 0; transform: translateY(10px); }
.page-fade-leave-to   { opacity: 0; transform: translateY(-10px); }

/* ─── Floating AI Button ─── */
.float-ai-btn {
  position: fixed;
  bottom: var(--space-8);
  right: var(--space-8);
  z-index: var(--z-modal);
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-2) var(--space-5) var(--space-2) var(--space-2);
  background: var(--color-neutral-0);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-full);
  box-shadow: var(--shadow-lg);
  cursor: pointer;
  transition: var(--transition-spring);
}

.float-ai-btn:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: var(--shadow-primary-lg);
  border-color: var(--color-primary-200);
}

.ai-icon-bg {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-warning));
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-neutral-0);
  font-size: var(--text-lg);
  box-shadow: var(--shadow-primary-md);
}

.label {
  font-weight: var(--font-extrabold);
  font-size: var(--text-base);
  color: var(--color-primary-700);
}

/* ─── Banned Modal ─── */
.banned-overlay {
  position: fixed;
  inset: 0;
  z-index: 99999;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
}

.banned-box {
  background: var(--color-neutral-0, #fff);
  border-radius: var(--radius-2xl, 16px);
  padding: var(--space-12, 48px) var(--space-10, 40px);
  max-width: 420px;
  width: 90%;
  text-align: center;
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.35);
}

.banned-icon {
  font-size: 3rem;
  margin-bottom: var(--space-4, 16px);
}

.banned-title {
  font-size: var(--text-xl, 1.25rem);
  font-weight: var(--font-bold, 700);
  color: var(--color-error, #dc2626);
  margin: 0 0 var(--space-3, 12px);
}

.banned-msg {
  font-size: var(--text-base, 1rem);
  color: var(--color-neutral-600, #4b5563);
  line-height: 1.6;
  margin: 0 0 var(--space-6, 24px);
}

.banned-btn {
  background: var(--color-error, #dc2626);
  color: #fff;
  border: none;
  border-radius: var(--radius-lg, 8px);
  padding: var(--space-3, 12px) var(--space-8, 32px);
  font-size: var(--text-base, 1rem);
  font-weight: var(--font-semibold, 600);
  cursor: pointer;
  transition: opacity 0.15s;
}

.banned-btn:hover { opacity: 0.85; }
</style>