<template>
  <div class="app-container">
    
    <Sidebar class="fixed-sidebar" @open-premium="showPremium = true" />

    <div class="main-content">
      
      <Header 
        @open-premium="showPremium = true" 
        @open-login="openLogin" 
        @open-register="openRegister" 
      />

      <div class="page-body">
        <router-view />
      </div>
    </div>

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
import { ref } from 'vue'
import Sidebar from '@/components/sidebar/Sidebar.vue'
import Header from '@/components/topbar/Header.vue' // Import từ thư mục topbar
import AuthModal from '@/components/modals/AuthModal.vue'
import PremiumModal from '@/components/modals/PremiumModal.vue'

// --- STATES ---
const showAuthModal = ref(false)
const showPremium = ref(false)
const modalTab = ref('login')

// --- METHODS ---
const openLogin = () => { 
  modalTab.value = 'login'
  showAuthModal.value = true 
}

const openRegister = () => { 
  modalTab.value = 'register'
  showAuthModal.value = true 
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Quicksand:wght@500;600;700;800&display=swap');

.app-container {
  display: flex; height: 100vh; width: 100vw; overflow: hidden;
  background-color: #FAFAF9;
  font-family: 'Quicksand', sans-serif; color: #1C1917;
}

.fixed-sidebar { flex-shrink: 0; z-index: 1000; }

.main-content {
  flex: 1; display: flex; flex-direction: column;
  height: 100%; overflow-y: auto; position: relative;
}

.page-body { padding: 30px 40px; }
</style>