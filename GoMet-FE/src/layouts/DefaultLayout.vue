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

    <GometAiChat ref="aiChatRef" />

    <button 
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
         @close="handleClosePremium" 
         @upgraded="handleUpgraded"
         @start-test-timer="handleStartTestTimer"
       />

       <ExpiredModal 
         :is-open="showExpired" 
         @renew="handleRenew" 
         @cancel="handleCancel"
       />
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useChatStore } from '@/stores/chat' 
import { toast } from '@/composables/useToast' 

import Sidebar from '@/components/sidebar/Sidebar.vue'
import Header from '@/components/topbar/Header.vue' 
import AuthModal from '@/components/modals/AuthModal.vue'
import PremiumModal from '@/components/modals/PremiumModal.vue'
import ExpiredModal from '@/components/modals/ExpiredModal.vue'
import MiniChatBox from '@/components/chat/MiniChatBox.vue'
import ChatSidebar from '@/components/chat/ChatSidebar.vue'
import TheFooter from '@/components/footer/TheFooter.vue'
import CompareFloatingBar from '@/components/common/CompareFloatingBar.vue'
import GometAiChat from '@/components/chat/GometAiChat.vue'

const router = useRouter()
const route = useRoute()
const chatStore = useChatStore() 

const showAuthModal = ref(false)
const showPremium = ref(false)
const showExpired = ref(false) 
const isEnforcingRenewal = ref(false) // Biến đánh dấu đang trong luồng ép lựa chọn
const modalTab = ref('login')
const aiChatRef = ref(null)

// --- LOGIC XỬ LÝ PREMIUM (ÉP GIA HẠN HOẶC VỀ FREE) ---

// 1. Khi sếp mua gói 10s thành công, bắt đầu đếm ngược
const handleStartTestTimer = () => {
  console.log("Hệ thống bắt đầu rình rập đếm ngược...");
  setTimeout(() => {
    showExpired.value = true;
    isEnforcingRenewal.value = true; // Kích hoạt trạng thái "Ép lựa chọn"
  }, 12000); 
}

// 2. Khi người dùng bấm "Gia hạn ngay" trên Popup đòi tiền
const handleRenew = () => {
  showExpired.value = false;
  showPremium.value = true; 
}

// 3. Khi người dùng bấm "X" hoặc click ra ngoài để tắt bảng chọn gói Premium
const handleClosePremium = () => {
  showPremium.value = false;
  
  // NẾU đang bị ép lựa chọn mà dám tắt bảng đi...
  if (isEnforcingRenewal.value) {
    // ...thì hiện lại popup ngay lập tức!
    showExpired.value = true;
    toast.error("Bạn cần gia hạn Premium để tiếp tục sử dụng các tính năng cao cấp!");
  }
}

// 4. Khi người dùng thực sự thanh toán thành công (Bấm nút Dev ok)
const handleUpgraded = () => {
  isEnforcingRenewal.value = false; // Giải lời nguyền
  showPremium.value = false;
  showExpired.value = false;
}

// 5. Khi người dùng hoàn tất ở màn hình "Cảm ơn" và chọn tiếp tục dùng Free
const handleCancel = () => {
  showExpired.value = false;
  isEnforcingRenewal.value = false; // Tắt trạng thái ép buộc để user dùng web bình thường
}

// --- LOGIC CŨ CỦA SẾP ---

const openAiChat = () => {
  if (aiChatRef.value) aiChatRef.value.openChat()
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
/* Giữ nguyên 100% CSS của sếp */
.app-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: var(--color-neutral-0);
  font-family: var(--font-body);
  color: var(--color-neutral-900);
  position: relative;
  transition: background-color 0.4s ease;
}

.app-container.is-dark-theme {
  background-color: #000000 !important;
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

.is-dark-theme .page-body {
  margin-top: calc(-1 * var(--header-height, 80px));
}

.page-body {
  padding: 0;
  flex: 1;
  position: relative;
  width: 100%;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity var(--duration-normal) var(--ease-out),
              transform var(--duration-normal) var(--ease-out);
}
.page-fade-enter-from { opacity: 0; transform: translateY(10px); }
.page-fade-leave-to   { opacity: 0; transform: translateY(-10px); }

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
