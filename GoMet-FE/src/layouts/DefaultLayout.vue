<template>
  <div class="app-container">
    <Sidebar />

    <div class="main-content">
      <header class="content-header">
        
        <div class="header-search">
          <span class="search-icon">🔍</span>
          <input type="text" placeholder="Tìm kiếm công thức, nguyên liệu..." />
        </div>

        <div class="header-right">
          <button class="btn-login" @click="showAuthModal = true">
            Đăng nhập
          </button>
          
          <button class="btn-signup" @click="showAuthModal = true">
            Đăng ký
          </button>
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
import { ref } from 'vue' // 👈 QUAN TRỌNG: Phải có dòng này
import Sidebar from '@/components/sidebar/Sidebar.vue'
import AuthModal from '@/components/auth/AuthModal.vue'

// 👇 QUAN TRỌNG: Khai báo biến điều khiển Modal
const showAuthModal = ref(false)

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Quicksand:wght@500;600;700&display=swap');

.app-container { 
  display: flex; 
  min-height: 100vh; 
  background-color: #fff; 
  font-family: 'Quicksand', sans-serif;
}

.main-content { 
  flex: 1; 
  display: flex; 
  flex-direction: column; 
}

/* Header Style */
.content-header {
  padding: 15px 30px;
  display: flex; align-items: center; justify-content: space-between;
  background: white;
  position: sticky; top: 0; z-index: 100;
  gap: 20px;
}

/* Thanh tìm kiếm to bản */
.header-search {
  flex: 1; /* Chiếm hết khoảng trống ở giữa */
  max-width: 800px; /* Giới hạn độ rộng tối đa */
  background: #f1f1f1;
  border-radius: 24px;
  padding: 12px 20px;
  display: flex; align-items: center; gap: 10px;
  transition: 0.2s;
}
.header-search:focus-within { background: #e0e0e0; box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.1); }
.header-search input {
  border: none; background: transparent; width: 100%; outline: none;
  font-size: 1rem; color: #333; font-family: inherit;
}

/* Buttons */
.header-right { display: flex; gap: 10px; }

.btn-login {
  padding: 10px 20px; border-radius: 24px; font-weight: 700; cursor: pointer; border: none;
  background: #F97316; color: white; transition: 0.2s;
}
.btn-login:hover { background: #ea580c; }

.btn-signup {
  padding: 10px 20px; border-radius: 24px; font-weight: 700; cursor: pointer;
  background: #f3f4f6; color: #111; border: none; transition: 0.2s;
}
.btn-signup:hover { background: #e5e7eb; }

.page-body {
  padding: 20px;
}
</style>