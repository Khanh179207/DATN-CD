<template>
  <div id="app">
    <router-view />
    <ToastContainer />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import ToastContainer from '@/components/common/ToastContainer.vue'

const authStore = useAuthStore()

onMounted(() => {
  // 🔥 LƯỚI TRỜI: Mỗi khi User mở web hoặc nhấn F5
  // Hàm này sẽ tự động chạy ngầm API /api/auth/me để kiểm tra Token
  // Nếu Backend phát hiện acc đã bị BAN (isActive = 0), nó sẽ ném lỗi 403
  // Và Interceptor của sếp sẽ tự động làm nốt phần việc: Xóa data + Đá văng ra Login + Báo lỗi đỏ!
  authStore.refreshProfile()
})
</script>

<style>
/* Global styles are now handled by:
   - assets/styles/design-tokens.css (CSS custom properties)
   - assets/styles/base.css (resets, scrollbar, utilities)
   Both imported in main.js
*/
</style>