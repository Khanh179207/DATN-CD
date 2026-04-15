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

      <div class="action-wrap" v-click-outside="closeNoti">
        <button class="btn-radar" :class="{ active: showNoti }" @click="toggleNoti">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
          </svg>
          <div v-if="unreadCount > 0" class="notification-badge">{{ unreadCount }}</div>
          <div class="radar-dot"></div>
          <div class="radar-waves"></div>
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
                Không có thông báo nào
              </p>
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
                <div class="i-box"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                    stroke-width="2.5">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                    <circle cx="12" cy="7" r="4"></circle>
                  </svg></div>
                <span>Hồ sơ cá nhân</span>
              </button>
              <button class="u-menu-item" @click="goToSettings">
                <div class="i-box"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                    stroke-width="2.5">
                    <circle cx="12" cy="12" r="3"></circle>
                    <path
                      d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z">
                    </path>
                  </svg></div>
                <span>Cấu hình hệ thống</span>
              </button>
            </div>

            <div class="u-footer">
              <button class="btn-logout" @click="handleLogout">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                  <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                  <polyline points="16 17 21 12 16 7"></polyline>
                  <line x1="21" y1="12" x2="9" y2="12"></line>
                </svg>
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
import { getNotifications, markNotificationRead, markAllNotificationsRead } from '@/services/notificationService'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const showNoti = ref(false)
const showUser = ref(false)
const isScrolled = ref(false)
const notifications = ref([])

// Computed: unread notification count
const unreadCount = computed(() =>
  notifications.value.filter(n => n.isRead === 0).length
)

/**
 * Update page title to show unread notification count
 */
const updatePageTitle = () => {
  if (unreadCount.value > 0) {
    document.title = `(${unreadCount.value}) Admin - GoMet`
  } else {
    document.title = 'Admin - GoMet'
  }
}

/**
 * Determine the correct link for a notification based on type
 */
const getLinkForNotificationType = (notification) => {
  const type = notification.type?.toUpperCase() || ''

  if (type === 'TICKET') return '/admin/tickets'
  if (type === 'POST_PENDING_APPROVAL') return '/admin/posts'
  if (type === 'FEEDBACK') return '/admin/feedback'
  if (type === 'REPORT') return '/admin/reports'

  // Default to the link from notification or admin page
  return notification.link || '/admin'
}

/**
 * Load notifications from API
 */
const loadNotifications = async () => {
  if (!auth.currentUser?.accountID) return

  try {
    const data = await getNotifications(auth.currentUser.accountID)
    console.log('Notification API:', data);
    notifications.value = data.map(n => ({
      notificationID: n.notificationID,
      title: n.title,
      content: n.content,
      username: n.username,
      avatar: n.avatarUrl,
      type: n.type || 'admin',
      isRead: n.isRead === 1 ? 1 : 0,
      createdAt: n.createdAt,
      link: getLinkForNotificationType(n)
    }))
    console.log('✅ Loaded admin notifications:', notifications.value);
    updatePageTitle()
  } catch (error) {
    console.error('❌ Failed to load notifications:', error)
  }
}

/**
 * Handle real-time admin alert updates (from WebSocket broadcast)
 */
const handleRealtimeAlert = (event) => {
  const alertData = event.detail

  const newNotification = {
    notificationID: alertData.notificationID,
    title: alertData.title || 'System Alert',
    content: alertData.content || alertData.message || '',
    type: alertData.type || 'alert',
    isRead: 0,
    createdAt: alertData.createdAt || new Date().toISOString(),
    link: getLinkForNotificationType(alertData)
  }

  if (newNotification.notificationID) {
    const exists = notifications.value.some(n => n.notificationID === newNotification.notificationID)
    if (!exists) {
      notifications.value.unshift(newNotification)
      playNotificationSound()
      updatePageTitle()
      showBrowserNotification(newNotification)
      console.log('🚨 Real-time admin alert received:', newNotification)
    }
  } else {
    console.warn('⚠️ Received admin alert without notificationID:', alertData)
  }
}

/**
 * Handle real-time admin notifications (user-specific)
 */
const handleRealtimeAdminNotification = (event) => {
  const notificationData = event.detail

  const newNotification = {
    notificationID: notificationData.notificationID,
    title: notificationData.title || 'Notification',
    content: notificationData.content || '',
    type: notificationData.type || 'notification',
    isRead: 0,
    createdAt: notificationData.createdAt || new Date().toISOString(),
    link: getLinkForNotificationType(notificationData)
  }

  if (newNotification.notificationID) {
    const exists = notifications.value.some(n => n.notificationID === newNotification.notificationID)
    if (!exists) {
      notifications.value.unshift(newNotification)
      playNotificationSound()
      updatePageTitle()
      showBrowserNotification(newNotification)
      console.log('📬 Real-time admin notification received:', newNotification)
    }
  } else {
    console.warn('⚠️ Received admin notification without notificationID:', notificationData)
  }
}

/**
 * Start real-time alert system (WebSocket)
 */
const startRealtimeSystem = () => {
  webSocketService.connect()
  window.addEventListener('admin-alert', handleRealtimeAlert)
  window.addEventListener('admin-notification', handleRealtimeAdminNotification)
  console.log('🔌 Real-time admin notification system started')
}

/**
 * Stop real-time alert system
 */
const stopRealtimeSystem = () => {
  window.removeEventListener('admin-alert', handleRealtimeAlert)
  window.removeEventListener('admin-notification', handleRealtimeAdminNotification)
  console.log('🔌 Real-time admin notification system stopped')
}

/**
 * Mark a single notification as read
 */
const markAsRead = async (notificationID) => {
  try {
    await markNotificationRead(notificationID)

    const notification = notifications.value.find(n => n.notificationID === notificationID)
    if (notification) {
      notification.isRead = 1
    }

    updatePageTitle()
    console.log('✅ Marked notification as read:', notificationID)
  } catch (error) {
    console.error('❌ Failed to mark notification as read:', error)
  }
}

/**
 * Mark all notifications as read
 */
const markAllAsRead = async () => {
  if (!auth.currentUser?.accountID) return

  try {
    await markAllNotificationsRead(auth.currentUser.accountID)

    notifications.value.forEach(n => {
      n.isRead = 1
    })

    updatePageTitle()
    console.log('✅ Marked all notifications as read')
  } catch (error) {
    console.error('❌ Failed to mark all notifications as read:', error)
  }
}

/**
 * Handle notification click
 */
const handleNotificationClick = async (notification) => {
  if (notification.isRead === 0) {
    await markAsRead(notification.notificationID)
  }

  if (notification.link) {
    router.push(notification.link)
  }

  showNoti.value = false
}

/**
 * Play notification sound
 */
const playNotificationSound = () => {
  const sound = document.getElementById("adminNotificationSound")
  if (sound) {
    sound.currentTime = 0
    sound.play().catch(err => console.log('Admin sound play failed:', err))
  }
}

const requestNotificationPermission = () => {
  if ('Notification' in window && Notification.permission === 'default') {
    Notification.requestPermission();
  }
};

const showBrowserNotification = (notification) => {
  if ('Notification' in window && Notification.permission === 'granted') {
    new Notification(notification.title, {
      body: notification.content,
      icon: notification.avatar ||
        `https://ui-avatars.com/api/?name=${encodeURIComponent(notification.title)}&background=EA580C&color=fff`
    });
  }
};

/**
 * Format time ago (relative time display)
 */
const timeAgo = (dateString) => {
  const now = new Date()
  const date = new Date(dateString)
  const diffInSeconds = Math.floor((now - date) / 1000)

  if (diffInSeconds < 60) return 'Vừa xong'
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m trước`
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h trước`
  return `${Math.floor(diffInSeconds / 86400)}d trước`
}

// --- 🏗️ LIFECYCLE ---
onMounted(async () => {
  window.addEventListener('scroll', handleScroll)
  await loadNotifications()
  startRealtimeSystem()
  updatePageTitle()
  requestNotificationPermission()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  stopRealtimeSystem()
})

const handleScroll = () => { isScrolled.value = window.scrollY > 15 }

// --- 👤 USER DATA ---
const adminName = computed(() => auth.currentUser?.username || 'Gomet Admin')
const adminEmail = computed(() => auth.currentUser?.email || 'admin@gomet.vn')
const adminAvatar = computed(() =>
  auth.currentUser?.avatar ||
  `https://ui-avatars.com/api/?name=${encodeURIComponent(adminName.value)}&background=f1f5f9&color=ea580c&size=128`
)

// --- 🕹️ UI CONTROL LOGIC ---
const toggleNoti = () => { showNoti.value = !showNoti.value; showUser.value = false; }
const closeNoti = () => { showNoti.value = false }

const toggleUser = () => { showUser.value = !showUser.value; showNoti.value = false; }
const closeUser = () => { showUser.value = false }

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
========================================= */
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
  height: 85px;
  padding: 0 45px;
  background: var(--z-bg);
  backdrop-filter: blur(25px) saturate(200%);
  border-bottom: 1px solid var(--z-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 900;
  font-family: 'Inter', sans-serif;
  transition: all 0.4s ease;
}

.header-admin-zenith.is-scrolled {
  height: 65px;
  background: var(--z-bg-scrolled);
  box-shadow: var(--z-shadow);
}

/* --- 🧭 2. BREADCRUMB --- */
.breadcrumb-zenith {
  display: flex;
  align-items: center;
  gap: 14px;
}

.cube-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(234, 88, 12, 0.1), rgba(251, 146, 60, 0.05));
  color: #ea580c;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(234, 88, 12, 0.15);
}

.arrow-divider {
  color: var(--z-text-sub);
  display: flex;
  opacity: 0.5;
}

.status-indicator {
  display: flex;
  flex-direction: column;
}

.route-title {
  color: var(--z-text-main);
  font-weight: 800;
  font-size: 1.05rem;
  letter-spacing: -0.01em;
  transition: 0.3s;
}

/* --- ⚡ 3. ACTIONS HUB --- */
.h-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-wrap {
  position: relative;
}

.btn-radar {
  width: 44px;
  height: 44px;
  border: 1px solid var(--z-border);
  background: var(--z-btn-bg);
  border-radius: 14px;
  color: var(--z-text-sub);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.btn-radar:hover,
.btn-radar.active {
  background: var(--z-btn-bg-hover);
  color: #ea580c;
  box-shadow: 0 10px 20px -5px rgba(234, 88, 12, 0.15), inset 0 0 0 1px rgba(254, 215, 170, 0.3);
  transform: translateY(-2px);
}

.radar-dot {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 6px;
  height: 6px;
  background: #ea580c;
  border-radius: 50%;
  box-shadow: 0 0 0 2px var(--z-bg-scrolled);
  z-index: 2;
}

.radar-waves {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #fb923c;
  z-index: 1;
  animation: radarPulse 2s infinite;
}

/* Notification badge on bell icon */
.notification-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background: linear-gradient(135deg, #ea580c, #fb923c);
  color: white;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 50%;
  min-width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--z-bg-scrolled);
  box-shadow: 0 2px 8px rgba(234, 88, 12, 0.3);
  z-index: 3;
  animation: badgePulse 2s infinite;
}

/* Identity Capsule */
.capsule-trigger {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 5px 14px 5px 5px;
  border-radius: 50px;
  background: var(--z-bg-scrolled);
  border: 1px solid var(--z-panel-border);
  transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
  margin-left: 8px;
}

.capsule-trigger:hover,
.capsule-trigger.active {
  border-color: #ea580c;
  box-shadow: 0 12px 24px -8px rgba(234, 88, 12, 0.2);
}

.cap-avatar {
  position: relative;
  width: 38px;
  height: 38px;
}

.cap-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--z-bg-scrolled);
}

.cap-status {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  background: #10b981;
  border: 2px solid var(--z-bg-scrolled);
  border-radius: 50%;
}

.cap-meta {
  display: flex;
  flex-direction: column;
  justify-content: center;
  transition: 0.3s;
}

.cap-mail {
  font-size: 13px;
  font-weight: 700;
  color: var(--z-text-main);
  letter-spacing: -0.01em;
}

.cap-role {
  font-size: 10px;
  color: #ea580c;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-top: -1px;
}

.cap-chevron {
  color: var(--z-text-sub);
  transition: 0.3s;
  display: flex;
  margin-left: 4px;
}

.active .cap-chevron {
  transform: rotate(180deg);
  color: #ea580c;
}

/* --- 🏛️ 4. ZENITH PANELS --- */
.panel-zenith {
  position: absolute;
  top: calc(100% + 16px);
  right: 0;
  background: var(--z-panel-bg);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 0 0 1px var(--z-border), var(--z-shadow);
  overflow: hidden;
  z-index: 1000;
  transform-origin: top right;
}

.user-panel {
  width: 300px;
}

.noti-panel {
  width: 360px;
}

/* Cấu trúc dùng chung Panel */
.z-head {
  padding: 20px;
  border-bottom: 1px solid var(--z-panel-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.z-title {
  font-weight: 800;
  color: var(--z-text-main);
  font-size: 15px;
}

.z-mark-read {
  font-size: 12px;
  color: #ea580c;
  background: none;
  border: none;
  cursor: pointer;
  font-weight: 700;
  transition: 0.2s;
}

.z-mark-read:hover:not(:disabled) {
  color: #c2410c;
}

.z-mark-read:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.z-body {
  max-height: 400px;
  overflow-y: auto;
  overflow-x: hidden;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.z-card {
  display: flex;
  gap: 16px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--z-panel-border);
  cursor: pointer;
  transition: 0.3s;
}

.z-card:hover {
  background: var(--z-item-hover);
  transform: translateX(4px);
}

.z-card.unread {
  background: rgba(234, 88, 12, 0.05);
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

.card-icon {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 18px;
  flex-shrink: 0;
  background: rgba(234, 88, 12, 0.1);
  color: #ea580c;
}

.card-icon.alert {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.card-icon.post {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.card-icon.success {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.card-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  background: var(--z-btn-bg);
  border: 2px solid var(--z-panel-border);
}

.card-avatar-placeholder {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 18px;
  flex-shrink: 0;
  background: rgba(234, 88, 12, 0.1);
  color: #ea580c;
}

.card-avatar-placeholder.alert {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.card-avatar-placeholder.post {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.card-desc {
  flex: 1;
  min-width: 0;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 4px;
}

.title-row p {
  margin: 0;
  font-weight: 600;
  color: var(--z-text-sub);
  font-size: 14px;
  line-height: 1.35;
  overflow: hidden;
  text-overflow: ellipsis;
}

.title-row p b {
  color: var(--z-text-main);
}

.badge-new {
  background: #ea580c;
  color: #fff;
  font-size: 9px;
  font-weight: 900;
  padding: 2px 6px;
  border-radius: 4px;
}

.sub-txt {
  display: block;
  font-size: 13px;
  color: var(--z-text-sub);
  line-height: 1.4;
  margin-bottom: 6px;
}

.time-txt {
  display: block;
  font-size: 11px;
  color: var(--z-text-sub);
  opacity: 0.8;
  font-weight: 500;
}

.z-foot {
  border-top: 1px solid var(--z-panel-border);
}

.z-foot button {
  width: 100%;
  padding: 16px;
  border: none;
  background: transparent;
  color: var(--z-text-sub);
  font-weight: 700;
  font-size: 13px;
  cursor: pointer;
  transition: 0.3s;
}

.z-foot button:hover {
  color: #ea580c;
  background: var(--z-item-hover);
}

/* User Panel Styles */
.u-cover {
  padding: 32px 20px;
  text-align: center;
  border-bottom: 1px solid var(--z-panel-border);
}

.u-avatar-epic {
  position: relative;
  width: 76px;
  height: 76px;
  margin: 0 auto 16px;
  border-radius: 22px;
  padding: 3px;
  background: var(--z-panel-bg);
  box-shadow: 0 10px 25px rgba(234, 88, 12, 0.15);
}

.u-avatar-epic img {
  width: 100%;
  height: 100%;
  border-radius: 18px;
  object-fit: cover;
  position: relative;
  z-index: 2;
}

.epic-ring {
  position: absolute;
  inset: 0;
  border-radius: 22px;
  background: linear-gradient(135deg, #fb923c, #ea580c);
  z-index: 1;
}

.u-name {
  margin: 0 0 4px;
  font-size: 18px;
  color: var(--z-text-main);
  font-weight: 800;
  letter-spacing: -0.02em;
}

.u-email {
  margin: 0;
  font-size: 13px;
  color: var(--z-text-sub);
  font-weight: 500;
}

.u-menu-grid {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.u-menu-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  border: none;
  background: transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: 0.3s;
  text-align: left;
}

.u-menu-item:hover {
  background: var(--z-item-hover);
  transform: translateX(6px);
}

.i-box {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--z-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--z-text-sub);
  border: 1px solid var(--z-panel-border);
  transition: 0.3s;
}

.u-menu-item:hover .i-box {
  color: #ea580c;
  border-color: rgba(234, 88, 12, 0.3);
}

.u-menu-item span {
  font-weight: 600;
  color: var(--z-text-main);
  font-size: 14px;
}

.u-footer {
  padding: 12px;
  border-top: 1px dashed var(--z-panel-border);
}

.btn-logout {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 12px;
  border: none;
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  font-weight: 700;
  font-size: 13px;
  border-radius: 12px;
  cursor: pointer;
  transition: 0.3s;
}

.btn-logout:hover {
  background: #ef4444;
  color: #fff;
  box-shadow: 0 8px 20px rgba(239, 68, 68, 0.25);
}

/* --- 🌪️ 5. ANIMATIONS --- */
.zenith-drop-enter-active {
  animation: springIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.zenith-drop-leave-active {
  animation: springIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) reverse;
}

@keyframes springIn {
  0% {
    opacity: 0;
    transform: scale(0.9) translateY(20px);
    filter: blur(4px);
  }

  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
    filter: blur(0);
  }
}

@keyframes radarPulse {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }

  100% {
    transform: scale(3.5);
    opacity: 0;
  }
}

@keyframes badgePulse {
  0% {
    transform: scale(1);
    box-shadow: 0 2px 8px rgba(234, 88, 12, 0.3);
  }

  50% {
    transform: scale(1.1);
    box-shadow: 0 2px 12px rgba(234, 88, 12, 0.5);
  }

  100% {
    transform: scale(1);
    box-shadow: 0 2px 8px rgba(234, 88, 12, 0.3);
  }
}

@keyframes blink {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.4;
  }
}

.custom-scroll::-webkit-scrollbar {
  width: 0;
  height: 0;
  display: none;
}
</style>