<template>
  <div class="admin-layout">
    
    <AdminSidebar />

    <main class="main-content">
      
      <HeaderAdmin />

      <div class="content-body">
        <router-view v-slot="{ Component }">
          <transition name="fade-page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>

    </main>
  </div>
</template>

<script setup>
import AdminSidebar from '@/components/sidebar/SidebarAdmin.vue'
import HeaderAdmin from '@/components/topbar/HeaderAdmin.vue' // Import
</script>

<style scoped>
.admin-layout {
  display: flex; min-height: 100vh;
  background: #F8FAFC; /* Màu nền xám lạnh chuyên nghiệp */
}

.main-content {
  flex: 1; margin-left: 270px; /* Né sidebar */
  display: flex; flex-direction: column; min-width: 0;
}

.content-body { padding: 30px; }

/* Page Transition nhẹ nhàng */
.fade-page-enter-active, .fade-page-leave-active { transition: opacity 0.2s ease; }
.fade-page-enter-from, .fade-page-leave-to { opacity: 0; }
</style>