<template>
  <div class="landing-layout">
    
    <LandingHeader @open-auth="handleOpenAuth" />

    <main class="landing-content">
      <router-view />
    </main>

    <Teleport to="body">
      <AuthModal 
        v-if="showModal" 
        :initialView="currentAuthView"
        @close="showModal = false" 
      />
    </Teleport>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import LandingHeader from '@/components/landing/LandingHeader.vue'
import AuthModal from '@/components/modals/AuthModal.vue'

const showModal = ref(false)
const currentAuthView = ref('login')

const handleOpenAuth = (viewName) => {
  currentAuthView.value = viewName
  showModal.value = true
}
</script>

<style scoped>
.landing-layout { 
  /* 👇 1. Đảm bảo layout có thể cuộn tự nhiên (Quan trọng để bám section) */
  min-height: 100vh; 
  display: flex; 
  flex-direction: column; 
  overflow-x: hidden; 
  background-color: #fdfdfd;
  
  /* 👇 2. Cưỡng chế cuộn mượt ở cấp độ layout */
  scroll-behavior: smooth; 
}

.landing-content { 
  flex: 1; 
  width: 100%; 
  /* 👇 3. Đảm bảo không có thuộc tính nào chặn (block) việc cuộn của con */
  position: relative;
}
</style>