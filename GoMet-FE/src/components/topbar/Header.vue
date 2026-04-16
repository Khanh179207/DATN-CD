<template>
  <header class="content-header" :class="[
    { 'is-scrolled': isScrolled },
    { 'is-dark-theme': isDark }
  ]">
    <div class="header-left-side">
      <!-- GoMetCoin Point Display -->
      <button v-if="authStore.isAuthenticated" class="btn-gomet-coin" @click="uiStore.openStore()">
        <span class="coin-icon">✨</span>
        <span class="coin-amount">{{ authStore.user?.point || 0 }}</span>
        <span class="coin-label">GoMetCoin</span>
      </button>

      <SearchBox />
    </div>

    <div class="header-right">
      <button class="btn-create-post" @click="handleCreatePost">
        <span class="plus-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
            stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
        </span>
        {{ $t('header.write_post') }}
      </button>

      <div class="icon-actions" v-if="authStore.isAuthenticated">
        <!-- Shopping List Dropdown -->
        <div class="action-wrapper" @click.stop>
          <button class="btn-icon" :class="{ active: showShopping }" title="Shopping List" @click="toggleShopping">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
              <line x1="3" y1="6" x2="21" y2="6"></line>
              <path d="M16 10a4 4 0 0 1-8 0"></path>
            </svg>
            <span v-if="shoppingStore.items.length > 0" class="badge-count">{{ shoppingStore.items.length }}</span>
          </button>

          <transition name="dropdown-anim">
            <div v-if="showShopping" class="common-dropdown shopping-width modern-shopping-dropdown">
              <div class="dropdown-header">
                <div class="header-left">
                  <h3>{{ $t('header.shopping_list') }}</h3>
                  <span class="count-pill">{{ shoppingStore.items.length }} món</span>
                </div>
                <div class="header-right">
                  <button v-if="shoppingStore.items.some(i => i.checked)" class="action-link danger-link"
                    @click="shoppingStore.removeCheckedItems()">
                    🗑️ Xóa đã tick
                  </button>
                  <button class="action-link" @click="shoppingStore.clearItems()">Xóa hết</button>
                </div>
              </div>

              <div class="dropdown-body scroll-body custom-scroll">
                <div v-if="shoppingStore.items.length === 0" class="empty-state-container">
                  <div class="empty-cart-icon">🛒</div>
                  <p>{{ $t('header.shopping_empty') }}</p>
                  <button class="btn-go-shop" @click="closeAllDropdowns">Lưu nguyên liệu ngay</button>
                </div>

                <div v-else class="shopping-cards-stack">
                  <div v-for="(item, idx) in shoppingStore.items" :key="idx" class="shopping-card-v3"
                    :class="{ 'item-done': item.checked }">
                    <div class="card-checkbox-area" @click="shoppingStore.toggleItem(idx)">
                      <div class="custom-check-circle">
                        <svg v-if="item.checked" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="4">
                          <polyline points="20 6 9 17 4 12"></polyline>
                        </svg>
                      </div>
                    </div>
                    <div class="card-info-area" @click="shoppingStore.toggleItem(idx)">
                      <span class="item-name-text">{{ item.name }}</span>
                      <span class="item-qty-text">Bài viết: {{ item.quantity }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="dropdown-footer-v3" v-if="shoppingStore.items.length > 0">
                <button @click="openGoogleMaps" class="btn-map-luxury">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                    <circle cx="12" cy="10" r="3"></circle>
                  </svg>
                  <span>Tìm Siêu thị / Chợ gần đây</span>
                </button>
              </div>
            </div>
          </transition>
        </div>

        <!-- Chat Button with Blinking Effect -->
        <div class="action-wrapper">
          <button class="btn-icon" :class="{ active: chatStore.isMessengerOpen, 'is-blinking': isChatBlinking }" @click.stop="toggleChat">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
            <span v-if="chatStore.totalUnreadCount > 0" class="badge-count">{{ chatStore.totalUnreadCount }}</span>
          </button>
        </div>

        <!-- Notification Dropdown -->
        <div class="action-wrapper" @click.stop v-click-outside="closeNotiDropdown">
          <button class="btn-icon" :class="{ active: showNoti }" @click="toggleNoti">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
              <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
            </svg>
            <span v-if="unreadNotiCount > 0" class="badge-count">{{ unreadNotiCount }}</span>
          </button>
          
          <transition name="dropdown-anim">
            <div v-if="showNoti" class="common-dropdown noti-width modern-noti-dropdown">
              <div class="dropdown-header">
                <div class="header-left">
                  <h3>{{ $t('header.notifications') }}</h3>
                  <span v-if="unreadNotiCount > 0" class="count-pill">{{ unreadNotiCount }} mới</span>
                </div>
                <button v-if="unreadNotiCount > 0" class="action-link" @click="handleMarkAllRead">{{ $t('header.mark_all_read') }}</button>
              </div>

              <div class="dropdown-body scroll-body custom-scroll">
                <div v-if="notifications.length === 0" class="empty-state-noti">
                  <div class="empty-noti-icon">🔕</div>
                  <p>Bạn không có thông báo nào mới.</p>
                </div>
                <div v-else>
                  <div v-for="n in notifications" :key="n.id" class="noti-item-v2" :class="{ 'is-unread': !n.isRead }" @click="handleNotiClick(n)">
                    <div class="item-avatar">
                      <img :src="n.avatar || 'https://ui-avatars.com/api/?name=User&background=EA580C&color=fff'" alt="User avatar">
                      <div class="type-icon" :class="n.type">
                        <svg v-if="n.type === 'like' || n.type === 'event_vote'" width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
                        <svg v-else-if="n.type === 'comment' || n.type === 'rating'" width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
                        <svg v-else-if="n.type === 'follow'" width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><line x1="20" y1="8" x2="20" y2="14"></line><line x1="17" y1="11" x2="23" y2="11"></line></svg>
                        <svg v-else width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
                      </div>
                    </div>
                    <div class="item-content">
                      <p class="noti-title" v-html="formatNotificationContent(n)"></p>
                      <span class="noti-time">{{ n.time }}</span>
                    </div>
                    <div v-if="!n.isRead" class="unread-dot-pulse"></div>
                  </div>
                </div>
              </div>
            </div>
          </transition>
        </div>
      </div>

      <div class="divider-vertical"></div>
      <LangSwitcher />

      <UserMenu v-if="authStore.isAuthenticated" @open-premium="emit('open-premium')" @open-support="showFeedbackModal = true" />
      <div v-else class="guest-actions">
        <button class="btn-login" @click="emit('open-login')">{{ $t('auth.login') }}</button>
        <button class="btn-signup" @click="emit('open-register')">{{ $t('auth.register') }}</button>
      </div>

      <div v-if="showNoti || showShopping" class="click-outside-header" @click="closeAllDropdowns"></div>
    </div>

    <!-- Modals & Audio -->
    <Teleport to="body">
      <MapModal v-if="showMapModal" @close="showMapModal = false" />
      <FeedbackModal v-if="showFeedbackModal" @close="showFeedbackModal = false" :form="feedbackForm" />
    </Teleport>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import { useShoppingStore } from '@/stores/shopping'
import { useUIStore } from '@/stores/ui'
import UserMenu from './UserMenu.vue'
import LangSwitcher from '@/components/common/LangSwitcher.vue'
import MapModal from '@/components/modals/MapModal.vue'
import FeedbackModal from '@/components/modals/FeedbackModal.vue'
import SearchBox from '@/components/common/SearchBox.vue'
import {
  getNotifications,
  getNotificationId,
  markNotificationRead,
  markAllNotificationsRead as apiMarkAllRead,
  resolveNotificationLink
} from '@/services/notificationService'
import webSocketService from '@/services/webSocketService'
import { ensureBrowserNotificationPermission, showBrowserNotification } from '@/services/browserNotificationService'
import { toast } from '@/composables/useToast'

const emit = defineEmits(['open-login', 'open-register', 'open-premium', 'open-store'])

const authStore = useAuthStore();
const chatStore = useChatStore();
const shoppingStore = useShoppingStore();
const uiStore = useUIStore();
const router = useRouter();
const route = useRoute();

const showShopping = ref(false);
const showNoti = ref(false);
const isScrolled = ref(false);
const showMapModal = ref(false);
const showFeedbackModal = ref(false);
const notifications = ref([]);
const feedbackForm = ref({ title: '', description: '', attachment: null });
const originalTitle = ref(document.title);
const notificationChannel = ref(null);

// 🚀 TRẠNG THÁI NHẤP NHÁY NÚT CHAT
const isChatBlinking = ref(false);
let blinkTimeout = null;

const isDark = computed(() => {
  if (route.meta?.isDark) return true;
  if (route.path.startsWith('/admin')) return true;
  const premiumRoutes = ['/leaderboard', '/meal-plan', '/suggestions'];
  return premiumRoutes.some(path => route.path.startsWith(path));
});

const unreadNotiCount = computed(() => notifications.value.filter(n => !n.isRead).length);

const normalizeNotification = (notification = {}) => {
  const id = getNotificationId(notification)
  return {
    id,
    title: notification.title,
    content: notification.content,
    username: notification.username || 'Hệ thống GoMet',
    avatar: notification.avatarUrl || notification.avatar || '/assets/images/logogoc.jpg',
    time: notification.createdAt
      ? new Date(notification.createdAt).toLocaleString()
      : new Date().toLocaleString(),
    isRead: notification.isRead === 1 || notification.isRead === true,
    type: String(notification.type || 'GENERAL').toLowerCase(),
    link: resolveNotificationLink(notification)
  }
}

const addNotificationToDropdown = (notification) => {
  const newNotification = normalizeNotification(notification)
  if (newNotification.id && notifications.value.some(item => item.id === newNotification.id)) {
    return false
  }
  notifications.value.unshift(newNotification);
  updateTabTitle();
  return true
};

const updateTabTitle = () => {
  const count = unreadNotiCount.value;
  document.title = count > 0 ? `(${count}) ${originalTitle.value}` : originalTitle.value;
};

const formatNotificationContent = (n) => {
  return `<strong>${n.username}</strong> ${n.content}`;
}

const handleMarkAllRead = async () => {
  if (!authStore.user?.accountID) return
  try { 
    await apiMarkAllRead(authStore.user.accountID); 
    notifications.value.forEach(n => n.isRead = true);
    updateTabTitle();
  } catch (err) { }
}

const toggleShopping = () => { 
  if (!authStore.isAuthenticated) { emit('open-login'); return; }
  const role = String(authStore.user?.role || '').toLowerCase();
  const isPremiumUser = ( role === 'premium' || [1, '1', true].includes(authStore.user?.isPremium));
  const isAdmin = ( role === 'admin' || [1, '1', true].includes(authStore.user?.isAdmin));
  
  if (!isPremiumUser && !isAdmin) {
    toast.warn('Tính năng Giỏ đi chợ là đặc quyền chỉ dành cho tài khoản Premium.');
    window.dispatchEvent(new CustomEvent('ui:open-premium'));
    return;
  }
  showNoti.value = false; 
  showShopping.value = !showShopping.value; 
  if (showShopping.value) shoppingStore.fetchCart(); 
}

const toggleNoti = () => { 
  showShopping.value = false; 
  showNoti.value = !showNoti.value; 
  if (showNoti.value) loadNotifications(); 
}

const toggleChat = () => { 
  chatStore.isMessengerOpen = !chatStore.isMessengerOpen; 
  isChatBlinking.value = false;
  closeAllDropdowns(); 
}

const closeAllDropdowns = () => { 
  showNoti.value = false; 
  showShopping.value = false; 
}

const closeNotiDropdown = () => { showNoti.value = false; }

const loadNotifications = async () => {
  if (!authStore.user?.accountID) return
  try {
    const data = await getNotifications(authStore.user.accountID)
    notifications.value = data.map(normalizeNotification)
    updateTabTitle();
  } catch (err) { }
}

const handleNotiClick = async (n) => {
  if (!n.isRead && n.id) {
    n.isRead = true;
    await markNotificationRead(n.id).catch(() => { });
    updateTabTitle();
  }
  showNoti.value = false;
  if (n.link) { router.push(n.link); }
}

const handleScroll = () => { isScrolled.value = window.scrollY > 10 }
const handleCreatePost = () => { authStore.isAuthenticated ? router.push('/create-post') : emit('open-login') }
const openGoogleMaps = () => { showMapModal.value = true; closeAllDropdowns(); }

// Xử lý thông báo Real-time
const handleRealtimeNotification = (event) => {
  const notificationDTO = event.detail;
  const added = addNotificationToDropdown(notificationDTO);
  if (!added) return

  // Gửi qua BroadcastChannel cho các tab khác
  if (notificationChannel.value) {
    notificationChannel.value.postMessage(notificationDTO);
  }

  // Hiển thị thông báo trình duyệt
  showBrowserNotification({
    title: notificationDTO.title,
    body: notificationDTO.content,
    icon: notificationDTO.avatarUrl || '/assets/images/logogoc.jpg',
    onClick: () => {
      const link = resolveNotificationLink(notificationDTO)
      if (link) router.push(link)
    }
  })
};

// Xử lý nháy nút Chat
const handleGlobalChatAlert = () => {
  if (chatStore.isMessengerOpen) return;
  isChatBlinking.value = true;
  clearTimeout(blinkTimeout);
  blinkTimeout = setTimeout(() => { isChatBlinking.value = false; }, 4000); 
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  handleScroll();

  if (authStore.isAuthenticated) {
    shoppingStore.fetchCart();
    loadNotifications(); 
    webSocketService.connect();
    
    window.addEventListener('realtime-notification', handleRealtimeNotification);
    window.addEventListener('global-chat-alert', handleGlobalChatAlert);

    ensureBrowserNotificationPermission();
    notificationChannel.value = new BroadcastChannel("notifications");
    notificationChannel.value.onmessage = (event) => {
      addNotificationToDropdown(event.data);
    };
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('realtime-notification', handleRealtimeNotification);
  window.removeEventListener('global-chat-alert', handleGlobalChatAlert);
  clearTimeout(blinkTimeout);
  if (notificationChannel.value) { notificationChannel.value.close(); }
})

watch(() => authStore.isAuthenticated, (val) => {
  if (val) {
    webSocketService.connect();
    loadNotifications();
  } else {
    webSocketService.disconnect();
    notifications.value = [];
  }
});

const vClickOutside = {
  mounted(el, binding) {
    el.__clickOutsideHandler__ = (event) => {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value(event)
      }
    }
    document.body.addEventListener('click', el.__clickOutsideHandler__)
  },
  unmounted(el) {
    document.body.removeEventListener('click', el.__clickOutsideHandler__)
  }
}
</script>

<style scoped lang="scss" src="./Header.scss"></style>

<style scoped>
/* FIX LAYOUT & MODERN UI */
.empty-state-container {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 40px 20px; text-align: center; min-height: 200px;
}
.empty-cart-icon { font-size: 3rem; margin-bottom: 12px; opacity: 0.5; }
.empty-state-container p { color: #64748b; margin-bottom: 20px; font-weight: 600; }
.btn-go-shop {
  padding: 10px 24px; background: #f8fafc; border: 1px solid #e2e8f0;
  border-radius: 100px; font-weight: 700; color: #0f172a; cursor: pointer; transition: all 0.2s ease;
}
.btn-go-shop:hover { background: #ea580c; color: white; border-color: #ea580c; }

/* 🚀 HIỆU ỨNG NHÁY NÚT CHAT */
@keyframes softRipple {
  0% { box-shadow: 0 0 0 0 rgba(234, 88, 12, 0.4); }
  70% { box-shadow: 0 0 0 8px rgba(234, 88, 12, 0); }
  100% { box-shadow: 0 0 0 0 rgba(234, 88, 12, 0); }
}

.btn-icon.is-blinking {
  animation: softRipple 1.5s infinite cubic-bezier(0.66, 0, 0, 1);
  color: #ea580c;
}

.modern-noti-dropdown .dropdown-header {
  padding: 16px 20px; display: flex; justify-content: space-between; align-items: center;
}
.modern-noti-dropdown .header-left { display: flex; align-items: center; gap: 10px; }
.modern-noti-dropdown .header-left h3 { font-size: 1.1rem; font-weight: 700; }
.modern-noti-dropdown .count-pill {
  background-color: #fff1e9; color: #ea580c; padding: 2px 8px;
  border-radius: 100px; font-size: 0.75rem; font-weight: 700;
}

.empty-state-noti {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 40px 20px; text-align: center; min-height: 200px;
}
.empty-noti-icon { font-size: 2.5rem; margin-bottom: 12px; opacity: 0.4; }
.empty-state-noti p { color: #64748b; font-weight: 600; }

.noti-item-v2 {
  display: flex; gap: 14px; padding: 12px 20px; cursor: pointer;
  transition: background-color 0.2s ease; position: relative;
}
.noti-item-v2:hover { background-color: #f8fafc; }
.noti-item-v2.is-unread { background-color: #fff7ed; }

.item-avatar { position: relative; flex-shrink: 0; }
.item-avatar img { width: 48px; height: 48px; border-radius: 50%; object-fit: cover; }
.type-icon {
  position: absolute; bottom: -2px; right: -2px; width: 22px; height: 22px;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  color: white; border: 2px solid white;
}
.type-icon.like, .type-icon.event_vote { background-color: #ef4444; }
.type-icon.comment, .type-icon.rating { background-color: #3b82f6; }
.type-icon.follow { background-color: #16a34a; }

.item-content { flex: 1; min-width: 0; }
.noti-title { margin: 0 0 4px; font-size: 0.9rem; color: #334155; line-height: 1.4; }
.noti-title :deep(strong) { font-weight: 700; color: #0f172a; }
.noti-time { font-size: 0.8rem; color: #94a3b8; font-weight: 500; }

.unread-dot-pulse {
  width: 8px; height: 8px; background: #ea580c; border-radius: 50%;
  align-self: center; margin-left: auto; flex-shrink: 0; animation: softRipple 1.8s infinite;
}
</style>
