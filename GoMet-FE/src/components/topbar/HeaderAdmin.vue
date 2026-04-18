<template>
  <header class="header-admin-zenith" :class="{ 'is-scrolled': isScrolled }">
    <div class="h-left">
      <div class="breadcrumb-zenith">
        <div class="cube-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <rect x="3" y="3" width="7" height="7"></rect>
            <rect x="14" y="3" width="7" height="7"></rect>
            <rect x="14" y="14" width="7" height="7"></rect>
            <rect x="3" y="14" width="7" height="7"></rect>
          </svg>
        </div>
        <div class="arrow-divider">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
            <path d="M9 18l6-6-6-6"></path>
          </svg>
        </div>

        <div class="status-indicator">
          <span class="route-title">{{ route.name || 'Trung tâm điều khiển' }}</span>
        </div>
      </div>
    </div>

    <div class="h-right">
      <!-- Admin Radar Notification -->
      <div class="action-wrap" v-click-outside="closeNoti">
        <button class="btn-radar" :class="{ active: showNoti }" @click="toggleNoti">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
          </svg>
          <div v-if="unreadCount > 0" class="notification-badge">{{ unreadCount }}</div>
          <div class="radar-dot" v-if="unreadCount > 0"></div>
          <div class="radar-waves" v-if="unreadCount > 0"></div>
        </button>

        <transition name="zenith-drop">
          <div v-if="showNoti" class="panel-zenith noti-panel">
            <div class="z-head">
              <span class="z-title">Thông báo hệ thống</span>
              <button class="z-mark-read" @click="markAllAsRead" :disabled="unreadCount === 0">
                Đã đọc hết
              </button>
            </div>
            <div class="z-body custom-scroll" v-if="notifications.length > 0">
              <div v-for="notification in notifications" :key="notification.notificationID" class="z-card"
                :class="{ unread: notification.isRead === 0 }" @click="handleNotificationClick(notification)">
                <img v-if="notification.avatar" class="card-avatar" :src="notification.avatar"
                  :alt="notification.username" />
                <div v-else class="card-avatar-placeholder" :class="notification.type">
                  {{ notification.type === 'alert' ? '!' : notification.type === 'post' ? '📝' : '🔔' }}
                </div>
                <div class="card-desc">
                  <div class="title-row">
                    <p><b>{{ notification.username }}</b> {{ notification.content }}</p>
                    <span v-if="notification.isRead === 0" class="badge-new">MỚI</span>
                  </div>
                  <span class="time-txt">{{ timeAgo(notification.createdAt) }}</span>
                </div>
              </div>

            </div>
            <div v-else class="z-body custom-scroll empty-state">
              <p style="text-align: center; color: #999; padding: 40px 20px;">
                Không có thông báo nào mới cho Quản trị viên.
              </p>
            </div>
            <div class="z-foot">
              <button @click="$router.push('/admin/notifications')">Xem tất cả thông báo</button>
            </div>
          </div>
        </transition>
      </div>

      <!-- User Admin Capsule -->
      <div class="action-wrap" v-click-outside="closeUser">
        <div class="capsule-trigger" @click="toggleUser" :class="{ active: showUser }">
          <div class="cap-avatar">
            <img :src="adminAvatar" :alt="adminName">
            <span class="cap-status"></span>
          </div>
          <div class="cap-meta">
            <span class="cap-mail">{{ adminEmail }}</span>
            <span class="cap-role">Quản trị viên</span>
          </div>
          <div class="cap-chevron">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
              <path d="M6 9l6 6 6-6"></path>
            </svg>
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
import webSocketService from '@/services/webSocketService'
import {
  getNotifications,
  getNotificationId,
  markNotificationRead,
  markAllNotificationsRead,
  resolveNotificationLink
} from '@/services/notificationService'
import { ensureBrowserNotificationPermission, showBrowserNotification as pushBrowserNotification } from '@/services/browserNotificationService'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const showNoti = ref(false)
const showUser = ref(false)
const isScrolled = ref(false)
const notifications = ref([])

const unreadCount = computed(() => notifications.value.filter(n => n.isRead === 0).length)

const updatePageTitle = () => {
  document.title = unreadCount.value > 0 ? `(${unreadCount.value}) Quản trị - GoMet` : 'Quản trị - GoMet'
}

const getLinkForNotificationType = (notification) => {
  const type = String(notification.type || '').toUpperCase()
  if (type === 'TICKET') return '/admin/tickets'
  if (type === 'POST_PENDING_APPROVAL') return '/admin/posts'
  if (type === 'FEEDBACK') return '/admin/feedback'
  if (type === 'REPORT') return '/admin/reports'
  return resolveNotificationLink(notification)
}

const normalizeNotification = (notification = {}) => ({
  notificationID: getNotificationId(notification),
  title: notification.title || 'Thông báo hệ thống',
  content: notification.content || '',
  username: notification.username || 'System',
  avatar: notification.avatarUrl || notification.avatar || '/logogoc.jpg',
  type: notification.type || 'ADMIN',
  isRead: notification.isRead === 1 || notification.isRead === true ? 1 : 0,
  createdAt: notification.createdAt || new Date().toISOString(),
  link: getLinkForNotificationType(notification)
})

const loadNotifications = async () => {
  if (!auth.user?.accountID) return
  try {
    const data = await getNotifications(auth.user.accountID)
    notifications.value = data.map(normalizeNotification)
    updatePageTitle()
  } catch (error) {
    console.error('❌ Failed to load admin notifications:', error)
  }
}

const handleRealtimeAlert = (event) => {
  const alertData = event.detail
  const newNotification = normalizeNotification({ ...alertData, isRead: 0 })
  if (newNotification.notificationID) {
    if (!notifications.value.some(n => n.notificationID === newNotification.notificationID)) {
      notifications.value.unshift(newNotification)
      playNotificationSound()
      updatePageTitle()
      showBrowserNotification(newNotification)
    }
  }
}

const handleRealtimeAdminNotification = (event) => {
  const notificationData = event.detail
  const newNotification = normalizeNotification({ ...notificationData, isRead: 0 })
  if (newNotification.notificationID) {
    if (!notifications.value.some(n => n.notificationID === newNotification.notificationID)) {
      notifications.value.unshift(newNotification)
      playNotificationSound()
      updatePageTitle()
      showBrowserNotification(newNotification)
    }
  }
}

const startRealtimeSystem = () => {
  webSocketService.connect()
  window.addEventListener('admin-alert', handleRealtimeAlert)
  window.addEventListener('admin-notification', handleRealtimeAdminNotification)
}

const stopRealtimeSystem = () => {
  window.removeEventListener('admin-alert', handleRealtimeAlert)
  window.removeEventListener('admin-notification', handleRealtimeAdminNotification)
}

const markAsRead = async (id) => {
  try {
    await markNotificationRead(id)
    const notification = notifications.value.find(n => n.notificationID === id)
    if (notification) notification.isRead = 1
    updatePageTitle()
  } catch (error) { }
}

const markAllAsRead = async () => {
  if (!auth.user?.accountID) return
  try {
    await markAllNotificationsRead(auth.user.accountID)
    notifications.value.forEach(n => n.isRead = 1)
    updatePageTitle()
  } catch (error) { }
}

const handleNotificationClick = async (n) => {
  if (n.isRead === 0) await markAsRead(n.notificationID)
  if (n.link) router.push(n.link)
  showNoti.value = false
}

const playNotificationSound = () => {
  const sound = document.getElementById("adminNotificationSound")
  if (sound) {
    sound.currentTime = 0
    sound.play().catch(() => {})
  }
}

const showBrowserNotification = (n) => {
  pushBrowserNotification({
    title: n.title,
    body: n.content,
    icon: n.avatar || '/logogoc.jpg',
    onClick: () => { if (n.link) router.push(n.link) }
  })
}

const timeAgo = (dateString) => {
  const diff = Math.floor((new Date() - new Date(dateString)) / 1000)
  if (diff < 60) return 'Vừa xong'
  if (diff < 3600) return `${Math.floor(diff / 60)}m trước`
  if (diff < 86400) return `${Math.floor(diff / 3600)}h trước`
  return `${Math.floor(diff / 86400)}d trước`
}

onMounted(async () => {
  window.addEventListener('scroll', handleScroll)
  startRealtimeSystem()
  await loadNotifications()
  ensureBrowserNotificationPermission()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  stopRealtimeSystem()
})

const handleScroll = () => { isScrolled.value = window.scrollY > 15 }

const adminName = computed(() => auth.user?.username || 'Admin')
const adminEmail = computed(() => auth.user?.email || 'admin@gomet.vn')
const adminAvatar = computed(() => auth.user?.avatar || `https://ui-avatars.com/api/?name=Admin&background=f1f5f9&color=ea580c`)

const toggleNoti = () => { showNoti.value = !showNoti.value; showUser.value = false; }
const closeNoti = () => { showNoti.value = false }
const toggleUser = () => { showUser.value = !showUser.value; showNoti.value = false; }
const closeUser = () => { showUser.value = false }

const handleLogout = () => { auth.logout(); router.push('/login'); }
const goToProfile = () => { router.push('/profile'); closeUser(); }
const goToSettings = () => { router.push('/admin/settings'); closeUser(); }

const vClickOutside = {
  mounted(el, binding) {
    el.clickOutsideEvent = (e) => { if (!(el === e.target || el.contains(e.target))) binding.value() }
    document.body.addEventListener('click', el.clickOutsideEvent)
  },
  unmounted(el) { document.body.removeEventListener('click', el.clickOutsideEvent) }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap');

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

  height: 85px; padding: 0 45px; background: var(--z-bg);
  backdrop-filter: blur(25px) saturate(200%); border-bottom: 1px solid var(--z-border);
  display: flex; align-items: center; justify-content: space-between;
  position: sticky; top: 0; z-index: 900; font-family: 'Inter', sans-serif; transition: all 0.4s ease;
}

.header-admin-zenith.is-scrolled { height: 65px; background: var(--z-bg-scrolled); box-shadow: var(--z-shadow); }

.breadcrumb-zenith { display: flex; align-items: center; gap: 14px; }
.cube-icon {
  width: 36px; height: 36px; border-radius: 10px; color: #ea580c;
  background: linear-gradient(135deg, rgba(234, 88, 12, 0.1), rgba(251, 146, 60, 0.05));
  display: flex; align-items: center; justify-content: center; border: 1px solid rgba(234, 88, 12, 0.15);
}
.arrow-divider { color: var(--z-text-sub); display: flex; opacity: 0.5; }
.route-title { color: var(--z-text-main); font-weight: 800; font-size: 1.05rem; letter-spacing: -0.01em; }

.h-right { display: flex; align-items: center; gap: 20px; }
.action-wrap { position: relative; }
.btn-radar {
  width: 44px; height: 44px; border: 1px solid var(--z-border); background: var(--z-btn-bg);
  border-radius: 14px; color: var(--z-text-sub); display: flex; align-items: center; justify-content: center;
  cursor: pointer; position: relative; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.btn-radar:hover, .btn-radar.active { background: var(--z-btn-bg-hover); color: #ea580c; box-shadow: 0 10px 20px -5px rgba(234, 88, 12, 0.15); transform: translateY(-2px); }

.radar-dot { position: absolute; top: 10px; right: 10px; width: 6px; height: 6px; background: #ea580c; border-radius: 50%; z-index: 2; }
.radar-waves { position: absolute; top: 10px; right: 10px; width: 6px; height: 6px; border-radius: 50%; background: #fb923c; z-index: 1; animation: radarPulse 2s infinite; }

.notification-badge {
  position: absolute; top: -6px; right: -6px; background: linear-gradient(135deg, #ea580c, #fb923c);
  color: white; font-size: 11px; font-weight: 700; padding: 2px 6px; border-radius: 50%;
  min-width: 20px; height: 20px; display: flex; align-items: center; justify-content: center;
  border: 2px solid var(--z-bg-scrolled); box-shadow: 0 2px 8px rgba(234, 88, 12, 0.3); z-index: 3;
}

.capsule-trigger {
  display: flex; align-items: center; gap: 12px; cursor: pointer; padding: 5px 14px 5px 5px;
  border-radius: 50px; background: var(--z-bg-scrolled); border: 1px solid var(--z-panel-border);
  transition: all 0.4s ease; margin-left: 8px;
}
.capsule-trigger:hover, .capsule-trigger.active { border-color: #ea580c; box-shadow: 0 12px 24px -8px rgba(234, 88, 12, 0.2); }

.cap-avatar { position: relative; width: 38px; height: 38px; }
.cap-avatar img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; border: 2px solid var(--z-bg-scrolled); }
.cap-status { position: absolute; bottom: 0; right: 0; width: 12px; height: 12px; background: #10b981; border: 2px solid var(--z-bg-scrolled); border-radius: 50%; }
.cap-mail { font-size: 13px; font-weight: 700; color: var(--z-text-main); }
.cap-role { font-size: 10px; color: #ea580c; font-weight: 800; text-transform: uppercase; margin-top: -1px; }

.panel-zenith {
  position: absolute; top: calc(100% + 16px); right: 0; background: var(--z-panel-bg);
  backdrop-filter: blur(20px); border-radius: 20px; box-shadow: 0 0 0 1px var(--z-border), var(--z-shadow);
  overflow: hidden; z-index: 1000; transform-origin: top right;
}
.user-panel { width: 300px; }
.noti-panel { width: 360px; }

.z-head { padding: 20px; border-bottom: 1px solid var(--z-panel-border); display: flex; justify-content: space-between; align-items: center; }
.z-title { font-weight: 800; color: var(--z-text-main); font-size: 15px; }
.z-mark-read { font-size: 12px; color: #ea580c; font-weight: 700; cursor: pointer; border:none; background:none; }
.z-mark-read:disabled { color: #ccc; cursor: not-allowed; }

.z-body { max-height: 400px; overflow-y: auto; }
.z-card { display: flex; gap: 14px; padding: 16px 20px; cursor: pointer; transition: 0.2s; border-bottom: 1px solid rgba(0,0,0,0.02); }
.z-card:hover { background: var(--z-item-hover); }
.z-card.unread { background: rgba(234, 88, 12, 0.03); }

.card-avatar { width: 44px; height: 44px; border-radius: 12px; object-fit: cover; }
.card-avatar-placeholder { width: 44px; height: 44px; border-radius: 12px; background: #f1f5f9; display: flex; align-items: center; justify-content: center; font-size: 1.2rem; }

.card-desc { flex: 1; min-width: 0; }
.title-row { display: flex; justify-content: space-between; margin-bottom: 4px; }
.title-row p { font-size: 13px; line-height: 1.4; color: var(--z-text-main); margin: 0; }
.badge-new { font-size: 9px; font-weight: 900; color: #ea580c; background: #fff1f2; padding: 2px 5px; border-radius: 4px; }
.time-txt { font-size: 11px; color: var(--z-text-sub); }

.z-foot { padding: 15px; text-align: center; border-top: 1px solid var(--z-panel-border); }
.z-foot button { font-size: 13px; font-weight: 700; color: var(--z-text-sub); background: none; border: none; cursor: pointer; }

.u-cover { padding: 30px 20px; text-align: center; background: linear-gradient(180deg, rgba(234, 88, 12, 0.05), transparent); }
.u-avatar-epic { width: 80px; height: 80px; margin: 0 auto 15px; position: relative; }
.u-avatar-epic img { width: 100%; height: 100%; border-radius: 50%; border: 4px solid white; box-shadow: 0 10px 20px -5px rgba(0,0,0,0.1); }
.u-name { font-weight: 800; font-size: 1.1rem; color: var(--z-text-main); margin: 0; }
.u-email { font-size: 13px; color: var(--z-text-sub); margin: 4px 0 0; }

.u-menu-grid { padding: 10px; display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.u-menu-item { padding: 15px 10px; border-radius: 15px; border: 1px solid var(--z-panel-border); background: white; display: flex; flex-direction: column; align-items: center; gap: 8px; cursor: pointer; transition: 0.3s; }
.u-menu-item:hover { transform: translateY(-3px); border-color: #ea580c; box-shadow: 0 8px 15px -5px rgba(234, 88, 12, 0.1); }
.i-box { width: 32px; height: 32px; border-radius: 10px; background: #f8fafc; color: #64748b; display: flex; align-items: center; justify-content: center; }
.u-menu-item:hover .i-box { background: #fff1e9; color: #ea580c; }
.u-menu-item span { font-size: 11px; font-weight: 700; color: var(--z-text-main); }

.u-footer { padding: 15px; border-top: 1px solid var(--z-panel-border); }
.btn-logout { width: 100%; padding: 12px; border-radius: 12px; border: none; background: #fef2f2; color: #ef4444; font-weight: 700; font-size: 13px; display: flex; align-items: center; justify-content: center; gap: 8px; cursor: pointer; transition: 0.3s; }
.btn-logout:hover { background: #fee2e2; transform: translateY(-2px); }

@keyframes radarPulse {
  0% { transform: scale(1); opacity: 0.8; }
  100% { transform: scale(4); opacity: 0; }
}

.zenith-drop-enter-active { animation: zenithDropIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1); }
.zenith-drop-leave-active { animation: zenithDropIn 0.3s reverse; }
@keyframes zenithDropIn { from { opacity: 0; transform: translateY(10px) scale(0.95); } to { opacity: 1; transform: translateY(0) scale(1); } }
</style>
