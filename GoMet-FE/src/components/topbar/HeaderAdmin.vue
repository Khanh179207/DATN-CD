<template>
  <header class="admin-header">
    
    <div class="h-left">
      <div class="breadcrumb">
        <span class="crumb-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
        </span>
        <span class="divider">/</span>
        <span class="current-route">{{ route.name || 'Dashboard' }}</span>
      </div>
    </div>

    <div class="h-right">
      
      <div class="action-item" v-click-outside="closeNoti">
        <button class="icon-btn" :class="{ active: showNoti }" @click="toggleNoti">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
          <span class="badge">3</span>
        </button>

        <transition name="fade-down">
          <div v-if="showNoti" class="dropdown-menu noti-menu">
            <div class="dd-header">
              <span class="title">Thông báo</span>
              <button class="mark-read">Đã đọc tất cả</button>
            </div>
            <div class="dd-body custom-scroll">
              <div class="noti-item unread" @click="handleNotiClick(1)">
                <div class="icon-box warning">⚠️</div>
                <div class="content">
                  <p class="msg">Có báo cáo vi phạm mới</p>
                  <span class="time">5 phút trước</span>
                </div>
              </div>
              <div class="noti-item" @click="handleNotiClick(2)">
                <div class="icon-box success">✅</div>
                <div class="content">
                  <p class="msg">Bài viết #920 được duyệt</p>
                  <span class="time">1 giờ trước</span>
                </div>
              </div>
            </div>
            <div class="dd-footer">
              <button @click="$router.push('/admin/notifications')">Xem tất cả</button>
            </div>
          </div>
        </transition>
      </div>

      <div class="action-item" v-click-outside="closeUser">
        <div class="user-trigger" @click="toggleUser">
          <div class="text-info">
            <span class="name">Khánh Admin</span>
            <span class="role">Super Admin</span>
          </div>
          <div class="avatar-wrapper">
             <img src="https://ui-avatars.com/api/?name=Khanh+Nguyen&background=EA580C&color=fff&size=128" alt="Admin">
          </div>
        </div>

        <transition name="fade-down">
          <div v-if="showUser" class="dropdown-menu user-menu">
            <div class="user-header">
               <img src="https://ui-avatars.com/api/?name=Khanh+Nguyen&background=EA580C&color=fff&size=128" alt="Admin">
               <div class="u-detail">
                 <strong>Khánh Admin</strong>
                 <span>admin@gomet.vn</span>
               </div>
            </div>
            
            <div class="menu-list">
              <button class="menu-link" @click="goToProfile">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                Trang cá nhân
              </button>
              
              <button class="menu-link" @click="goToSettings">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
                Cài đặt
              </button>

              <div class="divider"></div>

              <button class="menu-link danger" @click="handleLogout">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
                Đăng xuất
              </button>
            </div>
          </div>
        </transition>
      </div>

    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const showNoti = ref(false)
const showUser = ref(false)

// Click Outside Directive
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
const closeNoti = () => { showNoti.value = false }
const toggleUser = () => { showUser.value = !showUser.value; showNoti.value = false }
const closeUser = () => { showUser.value = false }

// Actions
const goToProfile = () => {
  router.push('/admin/profile')
  closeUser()
}
const goToSettings = () => {
  // router.push('/admin/settings') 
  closeUser()
}
const handleLogout = () => {
  if(confirm('Bạn có chắc muốn đăng xuất?')) {
    localStorage.removeItem('token') // Xóa token
    router.push('/login')
  }
}
const handleNotiClick = (id) => {
  alert(`Đã click thông báo ${id}`)
  closeNoti()
}
</script>

<style scoped>
/* CONFIG */
.admin-header {
  height: 70px; /* Chiều cao chuẩn */
  padding: 0 30px;
  background: white;
  border-bottom: 1px solid #E2E8F0;
  display: flex; align-items: center; justify-content: space-between;
  position: sticky; top: 0; z-index: 50;
  box-shadow: 0 2px 10px rgba(0,0,0,0.02); /* Bóng nhẹ giống header thường */
  font-family: 'Quicksand', sans-serif;
}

/* LEFT */
.h-left { display: flex; align-items: center; }
.breadcrumb { display: flex; align-items: center; gap: 8px; font-size: 1rem; color: #64748B; font-weight: 600; }
.crumb-icon { color: #94A3B8; display: flex; }
.divider { color: #CBD5E1; }
.current-route { color: #0F172A; font-weight: 700; }

/* RIGHT */
.h-right { display: flex; align-items: center; gap: 20px; }
.action-item { position: relative; }

/* NOTIFICATION BUTTON */
.icon-btn {
  width: 44px; height: 44px;
  border: 1px solid #F1F5F9; background: white;
  border-radius: 50%; /* Tròn giống header user */
  display: flex; align-items: center; justify-content: center;
  color: #64748B; cursor: pointer; position: relative;
  transition: all 0.2s ease;
}
.icon-btn:hover, .icon-btn.active { background: #FFF7ED; color: #EA580C; border-color: #FFEDD5; }
.badge {
  position: absolute; top: 0; right: 0;
  background: #EF4444; color: white;
  font-size: 0.7rem; font-weight: 700;
  width: 18px; height: 18px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  border: 2px solid white;
}

/* USER TRIGGER */
.user-trigger {
  display: flex; align-items: center; gap: 12px; cursor: pointer;
  padding: 4px; border-radius: 30px; transition: 0.2s;
}
.user-trigger:hover { background: #F8FAFC; }
.text-info { text-align: right; display: flex; flex-direction: column; }
.name { font-weight: 700; color: #0F172A; font-size: 0.9rem; }
.role { font-size: 0.75rem; color: #64748B; }

.avatar-wrapper img {
  width: 40px; height: 40px; border-radius: 50%;
  border: 2px solid #F1F5F9; object-fit: cover;
}

/* DROPDOWN MENUS */
.dropdown-menu {
  position: absolute; top: 55px; right: 0;
  background: white; width: 260px;
  border-radius: 12px; box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  border: 1px solid #F1F5F9; overflow: hidden; z-index: 100;
}
.noti-menu { width: 320px; }

/* Header của Dropdown */
.dd-header {
  padding: 12px 16px; border-bottom: 1px solid #F1F5F9;
  display: flex; justify-content: space-between; align-items: center;
}
.dd-header .title { font-weight: 700; }
.mark-read { font-size: 0.75rem; color: #EA580C; background: none; border: none; cursor: pointer; font-weight: 600; }

.dd-body { max-height: 300px; overflow-y: auto; }
.noti-item {
  display: flex; gap: 12px; padding: 12px 16px; cursor: pointer;
  border-bottom: 1px solid #F8FAFC; transition: 0.2s;
}
.noti-item:hover { background: #F8FAFC; }
.noti-item.unread { background: #FFFBF7; }
.icon-box { font-size: 1.2rem; }
.msg { margin: 0; font-size: 0.9rem; font-weight: 600; color: #334155; }
.time { font-size: 0.75rem; color: #94A3B8; }
.dd-footer button {
  width: 100%; padding: 12px; border: none; background: white;
  font-weight: 700; color: #64748B; cursor: pointer;
}
.dd-footer button:hover { color: #EA580C; background: #F8FAFC; }

/* USER MENU STYLES */
.user-menu .user-header {
  padding: 20px; border-bottom: 1px solid #F1F5F9;
  display: flex; flex-direction: column; align-items: center; text-align: center;
  background: #FFF7ED; /* Nền cam nhạt */
}
.user-menu .user-header img { width: 60px; height: 60px; border-radius: 50%; margin-bottom: 8px; border: 3px solid white; }
.u-detail strong { display: block; font-size: 1rem; color: #0F172A; }
.u-detail span { font-size: 0.8rem; color: #64748B; }

.menu-list { padding: 8px; display: flex; flex-direction: column; gap: 2px; }
.menu-link {
  width: 100%; display: flex; align-items: center; gap: 10px;
  padding: 10px 12px; border: none; background: transparent;
  color: #475569; font-weight: 600; font-size: 0.9rem; border-radius: 8px;
  cursor: pointer; transition: 0.2s; text-align: left;
}
.menu-link:hover { background: #F1F5F9; color: #0F172A; }
.menu-link.danger { color: #EF4444; }
.menu-link.danger:hover { background: #FEF2F2; }
.divider { height: 1px; background: #E2E8F0; margin: 4px 0; }

/* ANIMATION */
.fade-down-enter-active, .fade-down-leave-active { transition: all 0.2s ease; }
.fade-down-enter-from, .fade-down-leave-to { opacity: 0; transform: translateY(10px); }
</style>