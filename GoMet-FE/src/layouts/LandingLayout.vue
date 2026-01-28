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
import AuthModal from '@/components/auth/AuthModal.vue'

// Biến điều khiển Popup
const showModal = ref(false)
const currentAuthView = ref('login') // Mặc định mở login

// Hàm xử lý khi bấm nút trên Header
const handleOpenAuth = (viewName) => {
  currentAuthView.value = viewName // Cập nhật muốn mở view nào (login/register)
  showModal.value = true           // Bật popup lên
}
</script>

<style scoped>
/* (CSS giữ nguyên) */
.landing-layout { min-height: 100vh; display: flex; flex-direction: column; overflow-x: hidden; }
.landing-content { flex: 1; width: 100%; }
</style>