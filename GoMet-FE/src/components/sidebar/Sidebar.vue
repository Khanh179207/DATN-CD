<template>
  <aside class="sidebar-wrapper" :class="{ 'collapsed': isCollapsed }">
    
    <div class="sidebar-header">
      <div class="logo-area" v-if="!isCollapsed">
        <img src="@/assets/images/gomet.jpg" alt="Gomet" class="brand-logo" />
        <span class="brand-text">GOMET</span>
      </div>
      <button class="btn-toggle" @click="toggleSidebar">
        <svg v-if="isCollapsed" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 18l6-6-6-6"/></svg>
        <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 18l-6-6 6-6"/></svg>
      </button>
    </div>

    <nav class="sidebar-nav">
      <div class="nav-section">
        <span class="section-label" v-if="!isCollapsed">Menu chính</span>

        <router-link to="/home" class="nav-item" active-class="active">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>
          <span class="nav-text">Trang chủ</span>
        </router-link>

        <router-link to="/search" class="nav-item" active-class="active">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
          <span class="nav-text">Tìm kiếm</span>
        </router-link>

        <router-link to="/my-kitchen" class="nav-item" active-class="active">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 13.87A8 8 0 0 1 6 10a6 6 0 0 1 12 0c0 3.87-4 10-6 10-2 0-6-6.13-6-10Z"></path><circle cx="12" cy="10" r="2"></circle></svg>
          <span class="nav-text">Bếp của tôi</span>
        </router-link>
      </div>

      <div class="nav-section">
        <span class="section-label" v-if="!isCollapsed">Khám phá</span>
        
        <router-link to="/events" class="nav-item" active-class="active">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
          <span class="nav-text">Sự kiện</span>
        </router-link>

        <router-link to="/storage" class="nav-item" active-class="active">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path><polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline><line x1="12" y1="22.08" x2="12" y2="12"></line></svg>
          <span class="nav-text">Khu lưu trữ</span>
        </router-link>
      </div>

      <div class="spacer"></div>

      <router-link to="/admin" class="nav-item admin-item" active-class="active">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
        <span class="nav-text">Trang quản trị</span>
      </router-link>
    </nav>

    <div class="pro-card" v-if="!isCollapsed">
      <div class="card-bg-pattern"></div>
      <div class="pro-content">
        <div class="icon-circle">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"></path></svg>
        </div>
        <div class="pro-text">
          <h4>Nâng cấp VIP</h4>
          <p>Mở khóa tính năng</p>
        </div>
      </div>
      
      <button class="btn-upgrade" @click="$emit('open-premium')">
        Go Premium
      </button>
    </div>

  </aside>
</template>

<script setup>
import { ref } from 'vue'

// 👇 QUAN TRỌNG: KHAI BÁO SỰ KIỆN
defineEmits(['open-premium'])

const isCollapsed = ref(false)
const toggleSidebar = () => { isCollapsed.value = !isCollapsed.value }
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;800&family=Quicksand:wght@500;600;700&display=swap');

/* --- Giữ nguyên toàn bộ CSS VIP PRO của bạn --- */
.sidebar-wrapper { width: 270px; height: 100vh; background: #FFFFFF; border-right: 1px solid #F3F4F6; display: flex; flex-direction: column; padding: 24px; position: sticky; top: 0; z-index: 50; transition: width 0.4s cubic-bezier(0.25, 0.8, 0.25, 1); box-shadow: 2px 0 20px rgba(0,0,0,0.01); }
.sidebar-wrapper.collapsed { width: 88px; padding: 24px 14px; }
.sidebar-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 40px; height: 48px; }
.logo-area { display: flex; align-items: center; gap: 12px; overflow: hidden; white-space: nowrap; }
.brand-logo { width: 40px; height: 40px; border-radius: 12px; object-fit: cover; box-shadow: 0 4px 10px rgba(249, 115, 22, 0.2); }
.brand-text { font-family: 'Playfair Display', serif; font-size: 22px; font-weight: 800; color: #1C1917; letter-spacing: 0.5px; }
.btn-toggle { width: 32px; height: 32px; border-radius: 8px; border: 1px solid #E5E7EB; background: white; color: #9CA3AF; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.2s ease; }
.btn-toggle:hover { border-color: #F97316; color: #F97316; background: #FFF7ED; }
.collapsed .btn-toggle { margin: 0 auto; }
.sidebar-nav { flex: 1; display: flex; flex-direction: column; gap: 8px; overflow-y: auto; }
.nav-section { display: flex; flex-direction: column; gap: 4px; margin-bottom: 24px; }
.section-label { font-size: 0.7rem; font-weight: 700; color: #A8A29E; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 12px; padding-left: 16px; }
.nav-item { display: flex; align-items: center; padding: 12px 16px; color: #57534E; text-decoration: none; border-radius: 12px; transition: all 0.2s ease; position: relative; font-family: 'Quicksand', sans-serif; font-weight: 600; font-size: 0.95rem; }
.nav-icon { width: 20px; height: 20px; min-width: 20px; stroke-width: 2px; transition: 0.2s; }
.nav-text { margin-left: 12px; white-space: nowrap; transition: 0.2s; }
.nav-item:hover { background-color: #F5F5F4; color: #1C1917; }
.nav-item:hover .nav-icon { stroke: #F97316; }
.nav-item.active { background-color: #1C1917; color: white; box-shadow: 0 8px 20px -6px rgba(28, 25, 23, 0.3); }
.nav-item.active .nav-icon { stroke: #F97316; }
.spacer { flex: 1; }
.pro-card { background: #1C1917; border-radius: 20px; padding: 20px; color: white; position: relative; overflow: hidden; box-shadow: 0 15px 30px rgba(0,0,0,0.15); }
.card-bg-pattern { position: absolute; inset: 0; opacity: 0.1; background-image: radial-gradient(white 1px, transparent 1px); background-size: 10px 10px; }
.pro-content { display: flex; align-items: center; gap: 12px; margin-bottom: 15px; position: relative; z-index: 2; }
.icon-circle { width: 40px; height: 40px; border-radius: 12px; background: linear-gradient(135deg, #F97316 0%, #EA580C 100%); display: flex; align-items: center; justify-content: center; color: white; box-shadow: 0 4px 10px rgba(249, 115, 22, 0.3); }
.pro-text h4 { font-size: 0.95rem; font-weight: 700; margin: 0; }
.pro-text p { font-size: 0.75rem; color: #A8A29E; margin: 2px 0 0; }
.btn-upgrade { width: 100%; padding: 10px; border: none; border-radius: 12px; background: rgba(255,255,255,0.1); color: white; font-weight: 600; font-size: 0.85rem; cursor: pointer; transition: 0.2s; position: relative; z-index: 2; border: 1px solid rgba(255,255,255,0.1); }
.btn-upgrade:hover { background: white; color: #1C1917; }
.collapsed .nav-item { justify-content: center; padding: 12px 0; }
.collapsed .nav-text, .collapsed .section-label, .collapsed .brand-text { display: none; }
</style>