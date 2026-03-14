<template>
  <header class="header-admin-sovereign" :class="{ 'is-scrolled': isScrolled }">
    
    <div class="h-left">
      <div class="breadcrumb-lux">
        <span class="crumb-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
        </span>
        <span class="divider">/</span>
        <span class="current-route">{{ route.name || 'Dashboard' }}</span>
      </div>
    </div>

    <div class="h-right">
      
      <div class="action-item" v-click-outside="closeNoti">
        <button class="icon-btn-lux" :class="{ active: showNoti }" @click="toggleNoti">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
          <span class="badge-pulse">3</span>
        </button>

        <transition name="zoom-fade">
          <div v-if="showNoti" class="dropdown-panel noti-panel">
            <div class="panel-header">
              <span class="title">Thông báo hệ thống</span>
              <button class="mark-read">Đã đọc hết</button>
            </div>
            <div class="panel-body custom-scroll">
              <div class="noti-card unread" @click="handleNotiClick(1)">
                <div class="status-icon warning">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                </div>
                <div class="content">
                  <p class="msg">Có báo cáo vi phạm từ User #882</p>
                  <span class="time">5 phút trước</span>
                </div>
              </div>
              <div class="noti-card" @click="handleNotiClick(2)">
                <div class="status-icon success">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                </div>
                <div class="content">
                  <p class="msg">Bài viết "Mỳ Ý" đã được duyệt</p>
                  <span class="time">1 giờ trước</span>
                </div>
              </div>
            </div>
            <div class="panel-footer">
              <button @click="$router.push('/admin/notifications')">Xem tất cả hoạt động</button>
            </div>
          </div>
        </transition>
      </div>

      <div class="action-item" v-click-outside="closeUser">
        <div class="user-trigger-lux" @click="toggleUser" :class="{ active: showUser }">
          <div class="text-info">
            <span class="name">{{ adminName }}</span>
            <span class="role">Sovereign Admin</span>
          </div>
          <div class="avatar-ring">
             <img :src="adminAvatar" :alt="adminName">
          </div>
        </div>

        <transition name="zoom-fade">
          <div v-if="showUser" class="dropdown-panel user-panel">
            <div class="user-cover-header">
               <div class="avatar-display">
                 <img :src="adminAvatar" :alt="adminName">
               </div>
               <div class="u-detail">
                 <strong>{{ adminName }}</strong>
                 <span>{{ adminEmail }}</span>
               </div>
            </div>
            <div class="menu-list">
              <button class="menu-link" @click="goToProfile">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                Hồ sơ cá nhân
              </button>
              
              <button class="menu-link" @click="goToSettings">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
                Cài đặt hệ thống
              </button>

              <div class="divider-hor"></div>

              <button class="menu-link danger" @click="handleLogout">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
                Đăng xuất an toàn
              </button>
            </div>
          </div>
        </transition>
      </div>

    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route  = useRoute()
const router = useRouter()
const auth   = useAuthStore()

const showNoti = ref(false)
const showUser = ref(false)
const isScrolled = ref(false)

// Bắt sự kiện cuộn chuột để làm gọn Header
const handleScroll = () => { isScrolled.value = window.scrollY > 20 }

onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))

// Real user data from auth store
const adminName   = computed(() => auth.currentUser?.username || 'Admin')
const adminEmail  = computed(() => auth.currentUser?.email    || '')
const adminAvatar = computed(() =>
  auth.currentUser?.avatar ||
  `https://ui-avatars.com/api/?name=${encodeURIComponent(adminName.value)}&background=1a110d&color=fb923c&size=128`
)

const vClickOutside = {
  mounted(el, binding) {
    el.clickOutsideEvent = (event) => {
      if (!(el === event.target || el.contains(event.target))) binding.value(event, el)
    }
    document.body.addEventListener('click', el.clickOutsideEvent)
  },
  unmounted(el) { document.body.removeEventListener('click', el.clickOutsideEvent) }
}

const toggleNoti = () => { showNoti.value = !showNoti.value; showUser.value = false }
const closeNoti  = () => { showNoti.value = false }
const toggleUser = () => { showUser.value = !showUser.value; showNoti.value = false }
const closeUser  = () => { showUser.value = false }

const goToProfile = () => { router.push('/profile'); closeUser() }
const goToSettings = () => { closeUser() }

const handleLogout = () => {
  closeUser()
  auth.logout()
}

const handleNotiClick = (id) => { router.push('/admin/notifications'); closeNoti() }
</script>

<style scoped>
/* Đồng bộ Font Inter xịn */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

/* --- 🏛️ HEADER: SẠCH & ĐẲNG CẤP --- */
.header-admin-sovereign {
  height: 90px;
  padding: 0 40px;
  background: rgba(255, 255, 255, 0.85); 
  backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  display: flex; align-items: center; justify-content: space-between;
  position: sticky; top: 0; z-index: 50;
  font-family: 'Inter', sans-serif; /* Đổi sang Inter */
  transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1);
}

.header-admin-sovereign.is-scrolled {
  height: 70px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border-bottom-color: transparent;
}

/* --- LEFT: BREADCRUMB --- */
.breadcrumb-lux { display: flex; align-items: center; gap: 12px; font-size: 1rem; color: #64748B; }
.crumb-icon { color: #ea580c; display: flex; }
.divider { color: #CBD5E1; font-weight: 300; }
.current-route { color: #1a110d; font-weight: 800; letter-spacing: 0.2px; }

/* --- RIGHT: ACTIONS --- */
.h-right { display: flex; align-items: center; gap: 20px; }
.action-item { position: relative; }

/* NOTIFICATION BUTTON (NÂNG CẤP HỔ PHÁCH) */
.icon-btn-lux {
  width: 44px; height: 44px;
  border: 1px solid transparent; background: transparent;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #64748B; cursor: pointer; position: relative;
  transition: all 0.3s cubic-bezier(0.19, 1, 0.22, 1);
}
.icon-btn-lux:hover, .icon-btn-lux.active { 
  background: rgba(251, 146, 60, 0.08); color: #ea580c; 
}

/* Badge Pulse Animation Sovereign */
.badge-pulse {
  position: absolute; top: 4px; right: 4px;
  background: linear-gradient(135deg, #fb923c, #ea580c); color: white;
  font-size: 0.6rem; font-weight: 800;
  width: 18px; height: 18px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 0 0 2px white;
  animation: pulseAmber 2s infinite;
}
@keyframes pulseAmber { 
  0% { box-shadow: 0 0 0 0 rgba(234, 88, 12, 0.5), 0 0 0 2px white; } 
  70% { box-shadow: 0 0 0 6px rgba(234, 88, 12, 0), 0 0 0 2px white; } 
  100% { box-shadow: 0 0 0 0 rgba(234, 88, 12, 0), 0 0 0 2px white; } 
}

/* USER TRIGGER (NÂNG CẤP VỚI VIỀN HỔ PHÁCH) */
.user-trigger-lux {
  display: flex; align-items: center; gap: 12px; cursor: pointer;
  padding: 4px 6px 4px 16px; border-radius: 40px; transition: 0.3s;
  background: transparent;
}
.user-trigger-lux:hover, .user-trigger-lux.active { 
  background: #f8fafc; 
}

.text-info { text-align: right; display: flex; flex-direction: column; }
.name { font-weight: 700; color: #1a110d; font-size: 0.9rem; }
.role { font-size: 0.7rem; color: #ea580c; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }

.avatar-ring img {
  width: 40px; height: 40px; border-radius: 50%;
  border: 2px solid white; box-shadow: 0 0 0 1px #e2e8f0;
  object-fit: cover; transition: 0.3s;
}
.user-trigger-lux:hover .avatar-ring img, .user-trigger-lux.active .avatar-ring img { 
  box-shadow: 0 0 0 2px #fb923c; 
}

/* --- DROPDOWN PANELS (NÂNG CẤP BÓNG ĐỔ VÀ BO GÓC) --- */
.dropdown-panel {
  position: absolute; top: calc(100% + 15px); right: 0;
  background: white; width: 340px;
  border-radius: 16px; box-shadow: 0 20px 40px rgba(0,0,0,0.08);
  border: 1px solid rgba(0,0,0,0.05); overflow: hidden; z-index: 100;
  transform-origin: top right;
}
.user-panel { width: 260px; }

/* Header Panel Noti */
.panel-header {
  padding: 18px 20px; border-bottom: 1px solid #f1f5f9;
  display: flex; justify-content: space-between; align-items: center;
  background: #fff;
}
.panel-header .title { font-weight: 700; color: #1a110d; font-size: 0.95rem; }
.mark-read { font-size: 0.75rem; color: #ea580c; background: none; border: none; cursor: pointer; font-weight: 600; transition: 0.2s; }
.mark-read:hover { color: #b43f0a; }

/* NOTI CARD */
.panel-body { max-height: 350px; overflow-y: auto; }
.noti-card {
  display: flex; gap: 14px; padding: 16px 20px; cursor: pointer;
  border-bottom: 1px solid #f8fafc; transition: 0.2s;
}
.noti-card:hover { background: #fafafa; }
.noti-card.unread { background: rgba(251, 146, 60, 0.04); }

.status-icon { 
  background: #fff; width: 34px; height: 34px; border-radius: 10px; 
  display: flex; align-items: center; justify-content: center; 
  box-shadow: 0 4px 10px rgba(0,0,0,0.04); 
}
.status-icon.warning { color: #ea580c; }
.status-icon.success { color: #10b981; }

.msg { margin: 0; font-size: 0.85rem; font-weight: 600; color: #334155; line-height: 1.4; }
.noti-card.unread .msg { color: #1a110d; font-weight: 700; }
.time { font-size: 0.75rem; color: #94a3b8; font-weight: 500; margin-top: 4px; display: block; }

.panel-footer button {
  width: 100%; padding: 14px; border: none; background: #f8fafc;
  font-weight: 600; color: #64748B; cursor: pointer; font-size: 0.8rem;
  transition: 0.2s;
}
.panel-footer button:hover { color: #ea580c; background: #fff7ed; }

/* USER MENU - SOVEREIGN THEME */
.user-cover-header {
  padding: 24px 20px; 
  display: flex; flex-direction: column; align-items: center; text-align: center;
  background: #1a110d; /* Chuyển nền thành Than chì */
  color: white;
}
.avatar-display {
  width: 64px; height: 64px; border-radius: 50%;
  padding: 3px; background: linear-gradient(135deg, #fb923c, #ea580c);
  margin-bottom: 12px;
}
.avatar-display img { width: 100%; height: 100%; border-radius: 50%; border: 2px solid #1a110d; object-fit: cover; }

.u-detail strong { display: block; font-size: 1rem; font-weight: 700; margin-bottom: 4px; }
.u-detail span { font-size: 0.8rem; color: rgba(255,255,255,0.6); }

.menu-list { padding: 12px; background: white; }
.menu-link {
  width: 100%; display: flex; align-items: center; gap: 12px;
  padding: 10px 14px; border: none; background: transparent;
  color: #475569; font-weight: 600; font-size: 0.85rem; border-radius: 10px;
  cursor: pointer; transition: 0.2s; text-align: left;
}
.menu-link:hover { background: #f8fafc; color: #1a110d; transform: translateX(4px); }
.menu-link.danger { color: #ef4444; }
.menu-link.danger:hover { background: #fef2f2; transform: translateX(4px); }

.divider-hor { height: 1px; background: #f1f5f9; margin: 6px 0; }

/* ANIMATION ZOOM (MƯỢT HƠN) */
.zoom-fade-enter-active, .zoom-fade-leave-active { transition: all 0.3s cubic-bezier(0.19, 1, 0.22, 1); }
.zoom-fade-enter-from, .zoom-fade-leave-to { opacity: 0; transform: translateY(15px) scale(0.95); }

/* --- SCROLLBAR CUSTOM --- */
.custom-scroll::-webkit-scrollbar { width: 4px; }
.custom-scroll::-webkit-scrollbar-track { background: transparent; }
.custom-scroll::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.custom-scroll::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
</style>