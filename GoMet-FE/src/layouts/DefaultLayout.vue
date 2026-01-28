<template>
  <div class="app-container">
    <Sidebar />

    <div class="main-content">
      
      <header class="content-header">
        <div class="header-right">
          <button class="btn-login-header" @click="showAuthModal = true">
            👤 Đăng nhập
          </button>

          <button class="btn-write">📝 Viết món mới</button>
        </div>
      </header>

      <div class="page-body">
        <router-view />
      </div>

    </div>

    <Teleport to="body">
      <AuthModal v-if="showAuthModal" @close="showAuthModal = false" />
    </Teleport>

  </div>
</template>

<script setup>
import { ref } from 'vue'
// Import Sidebar cũ
import Sidebar from '@/components/sidebar/Sidebar.vue'
// 👇 Import Component Modal mới tạo
import AuthModal from '@/components/auth/AuthModal.vue'

// Biến trạng thái điều khiển việc ẩn/hiện Popup (mặc định là false - ẩn)
const showAuthModal = ref(false)
</script>

<style scoped>
/* Giữ nguyên style cũ của Layout */
.app-container { display: flex; min-height: 100vh; background-color: white; }
.main-content { flex: 1; display: flex; flex-direction: column; }
.content-header { padding: 10px 30px; display: flex; justify-content: flex-end; align-items: center; border-bottom: 1px solid #f3f4f6; }
.header-right { display: flex; align-items: center; gap: 15px; }
.btn-write { background-color: #F97316; color: white; border: none; padding: 8px 16px; border-radius: 6px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-write:hover { background-color: #ea580c; }

/* Style cho nút Đăng nhập mới trên Header */
.btn-login-header {
  background: transparent;
  border: 1px solid #F97316;
  color: #F97316;
  padding: 8px 16px;
  border-radius: 20px; /* Bo tròn kiểu nút */
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.2s;
}
.btn-login-header:hover {
  background: #fff7ed;
}
</style>