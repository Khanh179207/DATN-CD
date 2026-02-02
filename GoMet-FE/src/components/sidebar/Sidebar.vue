<template>
  <aside class="sidebar-wrapper" :class="{ 'collapsed': isCollapsed }">
    
    <div class="sidebar-header">
      <div class="logo-area" v-if="!isCollapsed">
        <span class="logo-icon">🍳</span>
        <span class="logo-text">GOMET</span>
      </div>
      <button class="btn-toggle" @click="toggleSidebar">
        {{ isCollapsed ? '➤' : '◀' }}
      </button>
    </div>

    <nav class="sidebar-nav">
      
      <div class="nav-item active">
        <span class="icon">🏠</span>
        <span class="text" v-if="!isCollapsed">Trang chủ</span>
      </div>

      <div class="nav-item">
        <span class="icon">👤</span>
        <span class="text" v-if="!isCollapsed">Trang của bạn</span>
      </div>

      <div class="nav-item">
        <span class="icon">🎉</span>
        <span class="text" v-if="!isCollapsed">Sự kiện</span>
      </div>

      <div class="nav-item">
        <span class="icon">📦</span>
        <span class="text" v-if="!isCollapsed">Khu lưu trữ</span>
      </div>
      
      <div class="nav-divider"></div>

      <div class="nav-item admin-item" @click="router.push('/admin')">
        <span class="icon">⚙️</span>
        <span class="text" v-if="!isCollapsed">Quản trị</span>
      </div>

    </nav>
  </aside>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isCollapsed = ref(false) // Trạng thái mặc định: Mở

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}
</script>

<style scoped>
/* Sidebar cơ bản */
.sidebar-wrapper {
  width: 260px; /* Rộng mặc định */
  height: 100vh;
  position: sticky; top: 0;
  background: white;
  border-right: 1px solid #f3f4f6;
  padding: 20px;
  display: flex; flex-direction: column;
  transition: width 0.3s ease; /* Hiệu ứng trượt mượt */
  flex-shrink: 0;
  z-index: 99;
}

/* Khi bị thu gọn */
.sidebar-wrapper.collapsed {
  width: 80px; /* Thu nhỏ lại */
  padding: 20px 10px;
}

/* Header logo */
.sidebar-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 30px; height: 40px;
}
.logo-area { display: flex; align-items: center; gap: 10px; }
.logo-icon { font-size: 24px; }
.logo-text { font-weight: 800; font-size: 22px; color: #F97316; }

/* Nút toggle */
.btn-toggle {
  background: #f3f4f6; border: none; width: 30px; height: 30px;
  border-radius: 50%; cursor: pointer; color: #666;
  display: flex; align-items: center; justify-content: center;
  transition: 0.2s;
}
.btn-toggle:hover { background: #e5e7eb; color: #F97316; }

/* Menu Items */
.sidebar-nav { display: flex; flex-direction: column; gap: 8px; }

.nav-item {
  display: flex; align-items: center;
  padding: 12px 16px;
  color: #4b5563; font-weight: 600;
  border-radius: 12px; cursor: pointer; transition: 0.2s;
  white-space: nowrap; /* Chống vỡ dòng khi thu nhỏ */
  overflow: hidden;
}

.nav-item .icon { font-size: 1.2rem; min-width: 30px; } /* Icon luôn cố định */
.nav-item .text { margin-left: 10px; }

/* Hover & Active */
.nav-item:hover, .nav-item.active { background-color: #fff7ed; color: #F97316; }

.nav-divider { height: 1px; background: #e5e7eb; margin: 10px; }
.admin-item:hover { background-color: #f3f4f6; color: #111827; }

/* Căn giữa icon khi thu nhỏ */
.collapsed .nav-item { justify-content: center; padding: 12px 0; }
.collapsed .logo-area, .collapsed .text { display: none; }
.collapsed .btn-toggle { margin: 0 auto; }
</style>