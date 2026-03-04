<template>
  <div class="app-container" :class="{ 'is-dark-theme': route.meta?.isDark }">
    
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
        <router-view v-slot="{ Component, route: currentRoute }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="currentRoute.fullPath" />
          </transition>
        </router-view>
      </div>

      <TheFooter />
    </div>

    <ChatSidebar />
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
       />
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router' // 🔥 Import useRoute
import { useChatStore } from '@/stores/chat' 

import Sidebar from '@/components/sidebar/Sidebar.vue'
import Header from '@/components/topbar/Header.vue' 
import AuthModal from '@/components/modals/AuthModal.vue'
import PremiumModal from '@/components/modals/PremiumModal.vue'
import MiniChatBox from '@/components/chat/MiniChatBox.vue'
import ChatSidebar from '@/components/chat/ChatSidebar.vue'
import TheFooter from '@/components/footer/TheFooter.vue'
import CompareFloatingBar from '@/components/common/CompareFloatingBar.vue'

const router = useRouter()
const route = useRoute() // 🔥 Khởi tạo để theo dõi meta.isDark
const chatStore = useChatStore() 

const showAuthModal = ref(false)
const showPremium = ref(false)
const modalTab = ref('login')

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
  transition: background-color 0.4s ease; /* Chuyển màu nền mượt mà */
}

/* 🔥 Trạng thái trang Premium/Dark */
.app-container.is-dark-theme {
  background-color: #000000 !important; /* Biến nền layout thành đen hoàn toàn */
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

/* 🔥 Ép nội dung tràn lên dưới Header khi ở Dark Theme */
.is-dark-theme .page-body {
  margin-top: calc(-1 * var(--header-height, 80px)); /* Kéo nội dung lên trên */
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

/* ─── Floating UI ─── */
.float-ai-btn {
  position: fixed;
  bottom: var(--space-8);
  right: var(--space-8);
  z-index: 99; 
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

/* Đổi màu nút Assistant khi ở nền tối để không bị quá chói */
.is-dark-theme .float-ai-btn {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}
.is-dark-theme .float-ai-btn .label {
  color: #FFF;
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
</style>