<template>
  <div id="app">
    <router-view />
    <ToastContainer />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import ToastContainer from '@/components/common/ToastContainer.vue'

const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()

onMounted(() => {
  authStore.refreshProfile()

  // 🚀 LOGIC ĐIỀU HƯỚNG THÔNG MINH
  // Ngưỡng 1024px bao gồm cả Mobile và các dòng Tablet (iPad Air/Pro...)
  const isMobileOrTablet = window.innerWidth < 1024

  // Nếu đang ở trang Landing (/) mà dùng thiết bị nhỏ -> Vào thẳng Home
  if (isMobileOrTablet && route.path === '/') {
    router.push('/home')
  }
})
</script>

<style>
/* Global styles are now handled by:
   - assets/styles/design-tokens.css (CSS custom properties)
   - assets/styles/base.css (resets, scrollbar, utilities)
   Both imported in main.js
*/
</style>