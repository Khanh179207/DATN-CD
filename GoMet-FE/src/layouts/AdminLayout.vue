<template>
  <div class="admin-layout-sovereign">
    <AdminSidebar class="fixed-sidebar" />

    <main class="main-content-engine">
      
      <div class="ambient-backdrop">
        <div class="glow-source-orange"></div>
        <div class="glow-source-white"></div>
        <div class="noise-texture"></div>
      </div>

      <div class="header-dock">
        <HeaderAdmin />
      </div>

      <div class="content-viewport custom-scrollbar">
        <router-view v-slot="{ Component }">
          <transition name="page-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>

    </main>
  </div>
</template>

<script setup>
import AdminSidebar from '@/components/sidebar/SidebarAdmin.vue'
import HeaderAdmin from '@/components/topbar/HeaderAdmin.vue'
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;500;600;700;800&display=swap');

/* --- 🏛️ CORE LAYOUT --- */
.admin-layout-sovereign {
  display: flex;
  min-height: 100vh;
  width: 100vw;
  background: #F8F9FB;
  overflow: hidden; /* Ngăn cuộn trang chính */
  font-family: 'Mulish', sans-serif;
}

/* Sidebar cố định (Giả định component Sidebar có width 270px) */
.fixed-sidebar {
  position: fixed;
  top: 0; left: 0; bottom: 0;
  width: 270px;
  z-index: 100;
  /* Nếu component Sidebar của bạn chưa có style fixed thì thêm vào đây */
}

.main-content-engine {
  flex: 1;
  margin-left: 270px; /* Đẩy nội dung sang phải bằng chiều rộng Sidebar */
  display: flex;
  flex-direction: column;
  height: 100vh; /* Full chiều cao màn hình */
  position: relative;
  z-index: 1;
}

/* --- ✨ AMBIENT BACKDROP (NỀN ĐỘNG) --- */
.ambient-backdrop {
  position: absolute; inset: 0; pointer-events: none; z-index: -1;
  overflow: hidden; background: #F8F9FB;
}

.glow-source-orange {
  position: absolute; top: -150px; left: -100px;
  width: 800px; height: 800px;
  background: radial-gradient(circle, rgba(234, 88, 12, 0.06) 0%, transparent 60%);
  filter: blur(100px);
  animation: float 15s infinite alternate ease-in-out;
}

.glow-source-white {
  position: absolute; bottom: -200px; right: -200px;
  width: 900px; height: 900px;
  background: radial-gradient(circle, rgba(255, 255, 255, 1) 0%, rgba(200, 210, 230, 0.15) 50%, transparent 80%);
  filter: blur(80px);
}

.noise-texture {
  position: absolute; inset: 0; opacity: 0.03;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.7'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
  pointer-events: none;
}

@keyframes float { 
  0% { transform: translate(0, 0); opacity: 0.5; } 
  100% { transform: translate(30px, 30px); opacity: 0.8; } 
}

/* --- 🧊 HEADER DOCK --- */
.header-dock {
  flex-shrink: 0;
  position: sticky; top: 0; z-index: 50;
  backdrop-filter: blur(15px);
  background: rgba(248, 249, 251, 0.8);
  border-bottom: 1px solid rgba(0,0,0,0.03);
  /* width: 100% tự động theo flex cha */
}

/* --- 📜 CONTENT VIEWPORT (TRÀN VIỀN) --- */
.content-viewport {
  flex: 1;
  overflow-y: overlay; /* Chrome/Edge: Thanh cuộn đè lên nội dung (đẹp hơn) */
  overflow-x: hidden;
  
  /* QUAN TRỌNG: Xóa padding để content con tràn viền */
  padding: 0; 
  
  position: relative;
  scroll-behavior: smooth;
}

/* Custom Scrollbar (Tinh tế) */
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(203, 213, 225, 0.5); 
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(148, 163, 184, 0.8); }

/* --- 🎬 PAGE TRANSITIONS --- */
.page-slide-enter-active, .page-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
}
.page-slide-enter-from { opacity: 0; transform: translateY(20px) scale(0.98); }
.page-slide-leave-to { opacity: 0; transform: translateY(-10px); }
</style>