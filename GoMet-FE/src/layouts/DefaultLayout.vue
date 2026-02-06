<template>
  <div class="app-container">
    
    <Sidebar 
      class="fixed-sidebar" 
      @open-premium="showPremium = true" 
      @logout="handleLogout" 
    />

    <div class="main-content">
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
      title="Chat với Gomet AI"
    >
      <div class="ai-icon-bg">
        <span class="icon">✨</span>
      </div>
      <span class="label">Trợ lý GoMet</span>
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
import { useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chat' 

import Sidebar from '@/components/sidebar/Sidebar.vue'
import Header from '@/components/topbar/Header.vue' 
import AuthModal from '@/components/modals/AuthModal.vue'
import PremiumModal from '@/components/modals/PremiumModal.vue'
import MiniChatBox from '@/components/modals/MiniChatBox.vue'
// 👇 IMPORT FOOTER
import TheFooter from '@/components/footer/TheFooter.vue'
import CompareFloatingBar from '@/components/common/CompareFloatingBar.vue'

const router = useRouter()
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
    lastMessage: 'Chào bạn, tôi có thể giúp gì?'
  }
  chatStore.openChat(aiBot)
}

const openAuth = (tab) => { 
  modalTab.value = tab
  showAuthModal.value = true 
}

const handleLogout = async () => {
  console.log("Logout triggered");
  localStorage.removeItem('user')
  localStorage.removeItem('token')
  await router.push({ path: '/', hash: '#sectionsigninlanding' });
}
</script>

<style scoped>
.app-container { display: flex; height: 100vh; overflow: hidden; background-color: #FAFAF9; font-family: 'Quicksand', sans-serif; color: #1C1917; position: relative; }
.fixed-sidebar { flex-shrink: 0; z-index: 2000; }
.main-content { flex: 1; display: flex; flex-direction: column; height: 100%; overflow-y: auto; scroll-behavior: smooth; }
.page-body { padding: 30px 40px; flex: 1; position: relative; }
.page-fade-enter-active, .page-fade-leave-active { transition: opacity 0.3s ease, transform 0.3s ease; }
.page-fade-enter-from { opacity: 0; transform: translateY(10px); }
.page-fade-leave-to { opacity: 0; transform: translateY(-10px); }

/* === STYLE NÚT GOMET AI LƠ LỬNG === */
.float-ai-btn {
  position: fixed;
  bottom: 30px;
  right: 30px; 
  z-index: 1900; 
  
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 20px 8px 8px;
  
  background: white;
  border: 1px solid #E5E7EB;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.float-ai-btn:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 12px 30px rgba(234, 88, 12, 0.25);
  border-color: #FED7AA;
}

.ai-icon-bg {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #EA580C, #F59E0B);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.2rem;
  box-shadow: 0 4px 10px rgba(234, 88, 12, 0.3);
}

.label {
  font-weight: 800;
  color: #1C1917;
  font-size: 1rem;
  background: linear-gradient(to right, #C2410C, #EA580C);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
</style>