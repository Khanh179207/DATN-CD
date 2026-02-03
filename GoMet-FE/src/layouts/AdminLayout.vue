<template>
  <div class="admin-container">
    
    <SidebarAdmin />

    <main class="main-wrapper">
      <header class="admin-header">
        <div class="header-search">
          <span class="search-icon">🔍</span>
          <input type="text" placeholder="Tìm kiếm người dùng, bài viết..." />
        </div>

        <div class="header-actions">
          <button class="action-btn">🔔 <span class="dot"></span></button>
          <div class="admin-profile">
            <div class="text-right">
              <div class="name">Khánh Admin</div>
              <div class="role">Super Admin</div>
            </div>
            <img src="https://ui-avatars.com/api/?name=Khanh+Nguyen&background=F97316&color=fff" alt="Admin">
          </div>
        </div>
      </header>

      <div class="content-body">
        <router-view />
      </div>
    </main>

  </div>
</template>

<script setup>
// IMPORT ĐÚNG TÊN FILE MỚI
import SidebarAdmin from '@/components/sidebar/SidebarAdmin.vue'
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Quicksand:wght@500;600;700;800&display=swap');

.admin-container {
  display: flex; min-height: 100vh;
  background: #F8FAFC;
  font-family: 'Quicksand', sans-serif;
}

.main-wrapper { 
  flex: 1; 
  display: flex; 
  flex-direction: column; 
  margin-left: 260px; /* Đẩy sang phải để tránh SidebarAdmin che mất */
  width: calc(100% - 260px);
}

.admin-header {
  height: 80px; background: white; padding: 0 40px;
  display: flex; align-items: center; justify-content: space-between;
  border-bottom: 1px solid #F1F5F9;
  position: sticky; top: 0; z-index: 90;
}

.header-search {
  background: #F1F5F9; padding: 10px 20px; border-radius: 30px;
  display: flex; align-items: center; gap: 10px; width: 400px;
}
.header-search input { border: none; background: transparent; outline: none; width: 100%; font-family: inherit; font-weight: 600; color: #334155; }

.header-actions { display: flex; align-items: center; gap: 24px; }
.action-btn { background: white; border: 1px solid #E2E8F0; width: 40px; height: 40px; border-radius: 50%; cursor: pointer; position: relative; font-size: 1.1rem; }
.action-btn .dot { position: absolute; top: 8px; right: 10px; width: 8px; height: 8px; background: #EF4444; border-radius: 50%; border: 2px solid white; }

.admin-profile { display: flex; align-items: center; gap: 12px; cursor: pointer; }
.admin-profile .name { font-weight: 700; color: #0F172A; font-size: 0.95rem; }
.admin-profile .role { font-size: 0.75rem; color: #64748B; }
.admin-profile img { width: 42px; height: 42px; border-radius: 50%; border: 2px solid #F1F5F9; }

.content-body { flex: 1; padding: 30px 40px; overflow-y: auto; }
</style>