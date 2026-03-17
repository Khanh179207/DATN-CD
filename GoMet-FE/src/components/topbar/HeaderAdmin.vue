<template>
  <header class="header-admin-zenith" :class="{ 'is-scrolled': isScrolled }">
    
    <div class="h-left">
      <div class="breadcrumb-zenith">
        <div class="cube-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
        </div>
        <div class="arrow-divider">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M9 18l6-6-6-6"></path></svg>
        </div>
        
        <div class="status-indicator">
          <span class="route-title">{{ route.name || 'Trung tâm điều khiển' }}</span>
        </div>
      </div>
    </div>

    <div class="h-right">

      <div class="action-wrap" v-click-outside="closeNoti">
        <button class="btn-radar" :class="{ active: showNoti }" @click="toggleNoti">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
          <div class="radar-dot"></div>
          <div class="radar-waves"></div>
        </button>

        <transition name="zenith-drop">
          <div v-if="showNoti" class="panel-zenith noti-panel">
            <div class="z-head">
              <span class="z-title">Thông báo hệ thống</span>
              <button class="z-mark-read">Đã đọc hết</button>
            </div>
            <div class="z-body custom-scroll">
              
              <div class="z-card unread">
                <div class="card-icon alert">!</div>
                <div class="card-desc">
                  <div class="title-row">
                    <p>Cảnh báo bảo mật</p>
                    <span class="badge-new">MỚI</span>
                  </div>
                  <span class="sub-txt">Phát hiện đăng nhập từ IP lạ (Hà Nội)</span>
                  <span class="time-txt">Vừa xong</span>
                </div>
              </div>
              
              <div class="z-card">
                <div class="card-icon success">✓</div>
                <div class="card-desc">
                  <div class="title-row">
                    <p>Cập nhật hệ thống</p>
                  </div>
                  <span class="sub-txt">Phiên bản Gomet v2.5 đã khởi chạy</span>
                  <span class="time-txt">2 giờ trước</span>
                </div>
              </div>

            </div>
            <div class="z-foot">
              <button @click="$router.push('/admin/notifications')">Xem tất cả thông báo</button>
            </div>
          </div>
        </transition>
      </div>

      <div class="action-wrap" v-click-outside="closeUser">
        <div class="capsule-trigger" @click="toggleUser" :class="{ active: showUser }">
          <div class="cap-avatar">
             <img :src="adminAvatar" :alt="adminName">
             <span class="cap-status"></span>
          </div>
          <div class="cap-meta">
            <span class="cap-mail">{{ adminEmail }}</span>
            <span class="cap-role">Quản trị cấp cao</span>
          </div>
          <div class="cap-chevron">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M6 9l6 6 6-6"></path></svg>
          </div>
        </div>

        <transition name="zenith-drop">
          <div v-if="showUser" class="panel-zenith user-panel">
            <div class="u-cover">
               <div class="u-avatar-epic">
                 <img :src="adminAvatar" :alt="adminName">
                 <div class="epic-ring"></div>
               </div>
               <h3 class="u-name">{{ adminName }}</h3>
               <p class="u-email">{{ adminEmail }}</p>
            </div>
            
            <div class="u-menu-grid">
              <button class="u-menu-item" @click="goToProfile">
                <div class="i-box"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg></div>
                <span>Hồ sơ cá nhân</span>
              </button>
              <button class="u-menu-item" @click="goToSettings">
                <div class="i-box"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg></div>
                <span>Cấu hình hệ thống</span>
              </button>
            </div>
            
            <div class="u-footer">
              <button class="btn-logout" @click="handleLogout">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
                Đăng xuất an toàn
              </button>
            </div>
          </div>
        </transition>
      </div>

    </div>
  </header>
  <audio id="adminNotificationSound" src="/sounds/notification.mp3" preload="auto"></audio>
</template>
<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getOpenTickets, getPendingPosts } from '@/services/adminService'
import webSocketService from '@/services/webSocketService'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const showNoti = ref(false)
const showUser = ref(false)
const isScrolled = ref(false)
const alerts = ref([])
const alertInterval = ref(null)

// --- 🏗️ LIFECYCLE ---
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleScroll = () => { isScrolled.value = window.scrollY > 15 }

// --- 👤 USER DATA ---
const adminName  = computed(() => auth.currentUser?.username || 'Gomet Admin')
const adminEmail = computed(() => auth.currentUser?.email    || 'admin@gomet.vn')
const adminAvatar = computed(() =>
  auth.currentUser?.avatar ||
  `https://ui-avatars.com/api/?name=${encodeURIComponent(adminName.value)}&background=f1f5f9&color=ea580c&size=128`
)

// --- 🕹️ UI CONTROL LOGIC ---
const toggleNoti = () => { showNoti.value = !showNoti.value; showUser.value = false; }
const closeNoti  = () => { showNoti.value = false }

const toggleUser = () => { showUser.value = !showUser.value; showNoti.value = false; }
const closeUser  = () => { showUser.value = false }

// --- 🚀 ACTIONS ---
const handleLogout = () => { 
  closeUser()
  auth.logout() 
}

const goToProfile = () => { router.push('/profile'); closeUser() }
const goToSettings = () => { router.push('/admin/settings'); closeUser() }

// --- 🖱️ DIRECTIVE: CLICK OUTSIDE ---
const vClickOutside = {
  mounted(el, binding) {
    el.clickOutsideEvent = (event) => {
      if (!(el === event.target || el.contains(event.target))) binding.value()
    }
    document.body.addEventListener('click', el.clickOutsideEvent)
  },
  unmounted(el) { document.body.removeEventListener('click', el.clickOutsideEvent) }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap');

/* =========================================
   🎨 COLOR VARIABLES
.header-admin-zenith {
  --z-bg: rgba(255, 255, 255, 0.65);
  --z-bg-scrolled: rgba(255, 255, 255, 0.95);
  --z-border: rgba(0, 0, 0, 0.03);
  --z-text-main: #0f172a;
  --z-text-sub: #64748b;
  --z-btn-bg: #f8fafc;
  --z-btn-bg-hover: #ffffff;
  --z-panel-bg: rgba(255, 255, 255, 0.98);
  --z-panel-border: #f1f5f9;
  --z-item-hover: #f8fafc;
  --z-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
}

/* --- ✨ 1. HEADER: ZENITH GLASS --- */
.header-admin-zenith {
  height: 85px; padding: 0 45px;
  background: var(--z-bg); backdrop-filter: blur(25px) saturate(200%);
  border-bottom: 1px solid var(--z-border);
  display: flex; align-items: center; justify-content: space-between;
  position: sticky; top: 0; z-index: 900; font-family: 'Inter', sans-serif;
  transition: all 0.4s ease;
}

.header-admin-zenith.is-scrolled {
  height: 65px; background: var(--z-bg-scrolled); box-shadow: var(--z-shadow);
}

/* --- 🧭 2. BREADCRUMB --- */
.breadcrumb-zenith { display: flex; align-items: center; gap: 14px; }
.cube-icon {
  width: 36px; height: 36px; border-radius: 10px;
  background: linear-gradient(135deg, rgba(234, 88, 12, 0.1), rgba(251, 146, 60, 0.05));
  color: #ea580c; display: flex; align-items: center; justify-content: center;
  border: 1px solid rgba(234, 88, 12, 0.15);
}
.arrow-divider { color: var(--z-text-sub); display: flex; opacity: 0.5; }
.status-indicator { display: flex; flex-direction: column; }
.route-title { color: var(--z-text-main); font-weight: 800; font-size: 1.05rem; letter-spacing: -0.01em; transition: 0.3s; }

/* --- ⚡ 3. ACTIONS HUB --- */
.h-right { display: flex; align-items: center; gap: 20px; }
.action-wrap { position: relative; }

.btn-radar {
  width: 44px; height: 44px; border: 1px solid var(--z-border); 
  background: var(--z-btn-bg); border-radius: 14px; color: var(--z-text-sub);
  display: flex; align-items: center; justify-content: center; cursor: pointer;
  position: relative; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.btn-radar:hover, .btn-radar.active { 
  background: var(--z-btn-bg-hover); color: #ea580c; 
  box-shadow: 0 10px 20px -5px rgba(234, 88, 12, 0.15), inset 0 0 0 1px rgba(254, 215, 170, 0.3);
  transform: translateY(-2px);
}

.radar-dot { position: absolute; top: 10px; right: 10px; width: 6px; height: 6px; background: #ea580c; border-radius: 50%; box-shadow: 0 0 0 2px var(--z-bg-scrolled); z-index: 2; }
.radar-waves { position: absolute; top: 10px; right: 10px; width: 6px; height: 6px; border-radius: 50%; background: #fb923c; z-index: 1; animation: radarPulse 2s infinite; }

/* Identity Capsule */
.capsule-trigger {
  display: flex; align-items: center; gap: 12px; cursor: pointer;
  padding: 5px 14px 5px 5px; border-radius: 50px;
  background: var(--z-bg-scrolled); border: 1px solid var(--z-panel-border);
  transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1); margin-left: 8px;
}
.capsule-trigger:hover, .capsule-trigger.active {
  border-color: #ea580c; box-shadow: 0 12px 24px -8px rgba(234, 88, 12, 0.2);
}

.cap-avatar { position: relative; width: 38px; height: 38px; }
.cap-avatar img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; border: 2px solid var(--z-bg-scrolled); }
.cap-status { position: absolute; bottom: 0; right: 0; width: 12px; height: 12px; background: #10b981; border: 2px solid var(--z-bg-scrolled); border-radius: 50%; }

.cap-meta { display: flex; flex-direction: column; justify-content: center; transition: 0.3s; }
.cap-mail { font-size: 13px; font-weight: 700; color: var(--z-text-main); letter-spacing: -0.01em; }
.cap-role { font-size: 10px; color: #ea580c; font-weight: 800; text-transform: uppercase; letter-spacing: 0.05em; margin-top: -1px; }
.cap-chevron { color: var(--z-text-sub); transition: 0.3s; display: flex; margin-left: 4px; }
.active .cap-chevron { transform: rotate(180deg); color: #ea580c; }

/* --- 🏛️ 4. ZENITH PANELS --- */
.panel-zenith {
  position: absolute; top: calc(100% + 16px); right: 0;
  background: var(--z-panel-bg); backdrop-filter: blur(20px);
  border-radius: 20px; box-shadow: 0 0 0 1px var(--z-border), var(--z-shadow);
  overflow: hidden; z-index: 1000; transform-origin: top right;
}
.user-panel { width: 300px; }
.noti-panel { width: 360px; }

/* Cấu trúc dùng chung Panel */
.z-head { padding: 20px; border-bottom: 1px solid var(--z-panel-border); display: flex; justify-content: space-between; align-items: center; }
.z-title { font-weight: 800; color: var(--z-text-main); font-size: 15px; }
.z-mark-read { font-size: 12px; color: #ea580c; background: none; border: none; cursor: pointer; font-weight: 700; transition: 0.2s; }
.z-mark-read:hover { color: #c2410c; }

.z-body { max-height: 400px; overflow-y: auto; }
.z-card { display: flex; gap: 16px; padding: 18px 20px; border-bottom: 1px solid var(--z-panel-border); cursor: pointer; transition: 0.3s; }
.z-card:hover { background: var(--z-item-hover); }
.z-card.unread { background: rgba(234, 88, 12, 0.05); }

.card-icon { width: 38px; height: 38px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-weight: 900; font-size: 16px; flex-shrink: 0; }
.card-icon.alert { background: rgba(239, 68, 68, 0.1); color: #ef4444; }
.card-icon.success { background: rgba(16, 185, 129, 0.1); color: #10b981; }

.card-desc { flex: 1; }
.title-row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 4px; }
.title-row p { margin: 0; font-weight: 700; color: var(--z-text-main); font-size: 14px; }
.badge-new { background: #ea580c; color: #fff; font-size: 9px; font-weight: 900; padding: 2px 6px; border-radius: 4px; }

.sub-txt { display: block; font-size: 13px; color: var(--z-text-sub); line-height: 1.4; margin-bottom: 6px; }
.time-txt { display: block; font-size: 11px; color: var(--z-text-sub); opacity: 0.8; font-weight: 500; }

.z-foot { border-top: 1px solid var(--z-panel-border); }
.z-foot button { width: 100%; padding: 16px; border: none; background: transparent; color: var(--z-text-sub); font-weight: 700; font-size: 13px; cursor: pointer; transition: 0.3s; }
.z-foot button:hover { color: #ea580c; background: var(--z-item-hover); }

/* User Panel Styles */
.u-cover { padding: 32px 20px; text-align: center; border-bottom: 1px solid var(--z-panel-border); }
.u-avatar-epic { position: relative; width: 76px; height: 76px; margin: 0 auto 16px; border-radius: 22px; padding: 3px; background: var(--z-panel-bg); box-shadow: 0 10px 25px rgba(234, 88, 12, 0.15); }
.u-avatar-epic img { width: 100%; height: 100%; border-radius: 18px; object-fit: cover; position: relative; z-index: 2; }
.epic-ring { position: absolute; inset: 0; border-radius: 22px; background: linear-gradient(135deg, #fb923c, #ea580c); z-index: 1; }

.u-name { margin: 0 0 4px; font-size: 18px; color: var(--z-text-main); font-weight: 800; letter-spacing: -0.02em; }
.u-email { margin: 0; font-size: 13px; color: var(--z-text-sub); font-weight: 500; }

.u-menu-grid { padding: 12px; display: flex; flex-direction: column; gap: 4px; }
.u-menu-item { display: flex; align-items: center; gap: 14px; padding: 12px; border: none; background: transparent; border-radius: 12px; cursor: pointer; transition: 0.3s; text-align: left; }
.u-menu-item:hover { background: var(--z-item-hover); transform: translateX(6px); }
.i-box { width: 32px; height: 32px; border-radius: 8px; background: var(--z-bg); display: flex; align-items: center; justify-content: center; color: var(--z-text-sub); border: 1px solid var(--z-panel-border); transition: 0.3s; }
.u-menu-item:hover .i-box { color: #ea580c; border-color: rgba(234, 88, 12, 0.3); }
.u-menu-item span { font-weight: 600; color: var(--z-text-main); font-size: 14px; }

.u-footer { padding: 12px; border-top: 1px dashed var(--z-panel-border); }
.btn-logout { width: 100%; display: flex; align-items: center; justify-content: center; gap: 10px; padding: 12px; border: none; background: rgba(239, 68, 68, 0.1); color: #ef4444; font-weight: 700; font-size: 13px; border-radius: 12px; cursor: pointer; transition: 0.3s; }
.btn-logout:hover { background: #ef4444; color: #fff; box-shadow: 0 8px 20px rgba(239, 68, 68, 0.25); }

/* --- 🌪️ 5. ANIMATIONS --- */
.zenith-drop-enter-active { animation: springIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1); }
.zenith-drop-leave-active { animation: springIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) reverse; }
@keyframes springIn { 0% { opacity: 0; transform: scale(0.9) translateY(20px); filter: blur(4px); } 100% { opacity: 1; transform: scale(1) translateY(0); filter: blur(0); } }

@keyframes radarPulse { 0% { transform: scale(1); opacity: 0.8; } 100% { transform: scale(3.5); opacity: 0; } }
@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0.4; } }

.custom-scroll::-webkit-scrollbar { width: 4px; }
.custom-scroll::-webkit-scrollbar-thumb { background: var(--z-panel-border); border-radius: 10px; }
</style> ok vậy thì tôi bỏ ý tưởng darkmode trên mấy file .scss và file màu  _admin-tokens.scss luôn
