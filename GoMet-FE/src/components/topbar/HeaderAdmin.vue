<template>
  <header class="header-admin-sovereign">
    
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
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
          <span class="badge-pulse">3</span>
        </button>

        <transition name="zoom-fade">
          <div v-if="showNoti" class="dropdown-panel noti-panel">
            <div class="panel-header">
              <span class="title">Thông báo mới</span>
              <button class="mark-read">Đã đọc hết</button>
            </div>
            <div class="panel-body custom-scroll">
              <div class="noti-card unread" @click="handleNotiClick(1)">
                <div class="status-icon warning">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                </div>
                <div class="content">
                  <p class="msg">Có báo cáo vi phạm từ User #882</p>
                  <span class="time">5 phút trước</span>
                </div>
              </div>
              <div class="noti-card" @click="handleNotiClick(2)">
                <div class="status-icon success">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>
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
        <div class="user-trigger-lux" @click="toggleUser">
          <div class="text-info">
            <span class="name">{{ adminName }}</span>
            <span class="role">Super Admin</span>
          </div>
          <div class="avatar-ring">
             <img :src="adminAvatar" :alt="adminName">
          </div>
        </div>

        <transition name="zoom-fade">
          <div v-if="showUser" class="dropdown-panel user-panel">
            <div class="user-cover-header">
               <img :src="adminAvatar" :alt="adminName">
               <div class="u-detail">
                 <strong>{{ adminName }}</strong>
                 <span>{{ adminEmail }}</span>
               </div>
            </div>
            <div class="menu-list">
              <button class="menu-link" @click="goToProfile">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                Hồ sơ cá nhân
              </button>
              
              <button class="menu-link" @click="goToSettings">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
                Cài đặt hệ thống
              </button>

              <div class="divider"></div>

              <button class="menu-link danger" @click="handleLogout">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
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
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route  = useRoute()
const router = useRouter()
const auth   = useAuthStore()

const showNoti = ref(false)
const showUser = ref(false)

// Real user data from auth store
const adminName   = computed(() => auth.currentUser?.username || 'Admin')
const adminEmail  = computed(() => auth.currentUser?.email    || '')
const adminAvatar = computed(() =>
  auth.currentUser?.avatar ||
  `https://ui-avatars.com/api/?name=${encodeURIComponent(adminName.value)}&background=EA580C&color=fff&size=128`
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
  // Use the store's logout which clears state and navigates to '/'
  auth.logout()
}

const handleNotiClick = (id) => { router.push('/admin/notifications'); closeNoti() }
</script>

<style scoped>
/* --- 🏛️ HEADER: GLASSMORPHISM --- */
.header-admin-sovereign {
  height: 85px; /* Tăng chiều cao để thoáng */
  padding: 0 40px;
  /* Kính mờ thay vì nền trắng đục */
  background: rgba(255, 255, 255, 0.85); 
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(234, 88, 12, 0.1); /* Viền cam rất nhạt */
  display: flex; align-items: center; justify-content: space-between;
  /* Giữ nguyên sticky */
  position: sticky; top: 0; z-index: 50;
  font-family: 'Mulish', sans-serif;
  transition: all 0.3s ease;
}

/* --- LEFT: BREADCRUMB --- */
.breadcrumb-lux { display: flex; align-items: center; gap: 10px; font-size: 1.1rem; color: #64748B; }
.crumb-icon { color: #EA580C; display: flex; }
.divider { color: #CBD5E1; font-weight: 300; }
.current-route { color: #0F172A; font-weight: 800; letter-spacing: 0.5px; }

/* --- RIGHT: ACTIONS --- */
.h-right { display: flex; align-items: center; gap: 25px; }
.action-item { position: relative; }

/* NOTIFICATION BUTTON (NÂNG CẤP) */
.icon-btn-lux {
  width: 48px; height: 48px;
  border: 1px solid #E2E8F0; background: white;
  border-radius: 14px; /* Bo góc vuông mềm thay vì tròn */
  display: flex; align-items: center; justify-content: center;
  color: #64748B; cursor: pointer; position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 10px rgba(0,0,0,0.03);
}
.icon-btn-lux:hover, .icon-btn-lux.active { 
  background: #FFF7ED; color: #EA580C; border-color: #FDBA74; transform: translateY(-2px);
}

/* Badge Pulse Animation */
.badge-pulse {
  position: absolute; top: -5px; right: -5px;
  background: #EF4444; color: white;
  font-size: 0.65rem; font-weight: 800;
  width: 20px; height: 20px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  border: 2px solid white;
  animation: pulseRed 2s infinite;
}
@keyframes pulseRed { 0% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.4); } 70% { box-shadow: 0 0 0 6px rgba(239, 68, 68, 0); } 100% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0); } }

/* USER TRIGGER (NÂNG CẤP) */
.user-trigger-lux {
  display: flex; align-items: center; gap: 14px; cursor: pointer;
  padding: 6px 8px; border-radius: 40px; transition: 0.3s;
  border: 1px solid transparent;
}
.user-trigger-lux:hover { background: white; border-color: #E2E8F0; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }

.text-info { text-align: right; display: flex; flex-direction: column; }
.name { font-weight: 800; color: #0F172A; font-size: 0.95rem; }
.role { font-size: 0.75rem; color: #EA580C; font-weight: 700; letter-spacing: 0.5px; }

.avatar-ring img {
  width: 45px; height: 45px; border-radius: 50%;
  border: 2px solid white; box-shadow: 0 0 0 2px #E2E8F0; /* Vòng ring kép */
  object-fit: cover; transition: 0.3s;
}
.user-trigger-lux:hover .avatar-ring img { box-shadow: 0 0 0 2px #EA580C; }

/* --- DROPDOWN PANELS (NÂNG CẤP) --- */
.dropdown-panel {
  position: absolute; top: 65px; right: 0;
  background: white; width: 340px;
  border-radius: 16px; box-shadow: 0 20px 50px rgba(0,0,0,0.15); /* Bóng đổ sâu hơn */
  border: 1px solid #F1F5F9; overflow: hidden; z-index: 100;
  transform-origin: top right;
}
.user-panel { width: 280px; }

/* Header Panel */
.panel-header {
  padding: 16px 20px; border-bottom: 1px solid #F1F5F9;
  display: flex; justify-content: space-between; align-items: center;
  background: #FAFAFA;
}
.panel-header .title { font-weight: 800; color: #334155; font-size: 0.95rem; }
.mark-read { font-size: 0.75rem; color: #EA580C; background: none; border: none; cursor: pointer; font-weight: 700; }

/* NOTI CARD */
.panel-body { max-height: 350px; overflow-y: auto; }
.noti-card {
  display: flex; gap: 14px; padding: 16px 20px; cursor: pointer;
  border-bottom: 1px solid #F8FAFC; transition: 0.2s;
}
.noti-card:hover { background: #FFF7ED; }
.noti-card.unread { background: #FFFCF5; border-left: 3px solid #EA580C; }

.status-icon { font-size: 1.2rem; background: #fff; width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.msg { margin: 0; font-size: 0.9rem; font-weight: 700; color: #1E293B; line-height: 1.4; }
.time { font-size: 0.75rem; color: #94A3B8; font-weight: 500; }

.panel-footer button {
  width: 100%; padding: 14px; border: none; background: white;
  font-weight: 800; color: #64748B; cursor: pointer; font-size: 0.85rem;
  transition: 0.2s;
}
.panel-footer button:hover { color: #EA580C; background: #F8FAFC; letter-spacing: 0.5px; }

/* USER MENU HEADER */
.user-cover-header {
  padding: 25px; border-bottom: 1px solid #F1F5F9;
  display: flex; flex-direction: column; align-items: center; text-align: center;
  background: linear-gradient(135deg, #FFF7ED 0%, #FFF 100%);
}
.user-cover-header img { width: 70px; height: 70px; border-radius: 50%; margin-bottom: 12px; border: 4px solid white; box-shadow: 0 5px 15px rgba(234, 88, 12, 0.15); }
.u-detail strong { display: block; font-size: 1.1rem; color: #0F172A; font-weight: 800; }
.u-detail span { font-size: 0.85rem; color: #64748B; font-weight: 500; }

.menu-list { padding: 10px; }
.menu-link {
  width: 100%; display: flex; align-items: center; gap: 12px;
  padding: 12px 16px; border: none; background: transparent;
  color: #475569; font-weight: 600; font-size: 0.92rem; border-radius: 10px;
  cursor: pointer; transition: 0.2s; text-align: left;
}
.menu-link:hover { background: #F1F5F9; color: #0F172A; transform: translateX(5px); }
.menu-link.danger { color: #EF4444; }
.menu-link.danger:hover { background: #FEF2F2; transform: translateX(5px); }

/* ANIMATION ZOOM */
.zoom-fade-enter-active, .zoom-fade-leave-active { transition: all 0.25s cubic-bezier(0.19, 1, 0.22, 1); }
.zoom-fade-enter-from, .zoom-fade-leave-to { opacity: 0; transform: translateY(10px) scale(0.95); }
</style>