<template>
  <header class="content-header" :class="[
    { 'is-scrolled': isScrolled },
    { 'is-dark-theme': isDark }
  ]">
    <SearchBox />

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
        <div class="action-wrapper" @click.stop>
          <button class="btn-icon" :class="{ active: showShopping }" :title="$t('header.shopping_list')" @click="toggleShopping">
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
                  <span class="count-pill">{{ $t('header.items_count', { count: shoppingStore.items.length }) }}</span>
                </div>
                <div class="header-right">
                  <button v-if="shoppingStore.items.some(i => i.checked)" class="action-link danger-link"
                    @click="shoppingStore.removeCheckedItems()">
                    🗑️ {{ $t('header.remove_checked') }}
                  </button>
                  <button class="action-link" @click="shoppingStore.clearItems()">{{ $t('header.clear_all') }}</button>
                </div>
              </div>

              <div class="dropdown-body scroll-body custom-scroll">
                <div v-if="shoppingStore.items.length === 0" class="empty-state-container">
                  <div class="empty-cart-icon">🛒</div>
                  <p>{{ $t('header.shopping_empty') }}</p>
                  <button class="btn-go-shop" @click="closeAllDropdowns">{{ $t('header.save_ingredients_now') }}</button>
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
                      <span class="item-qty-text">{{ $t('header.quantity_label') }}: {{ item.quantity }}</span>
                    </div>
                    <button class="btn-remove-item" @click.stop="shoppingStore.removeItem(idx)"><svg width="14"
                        height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                        <line x1="18" y1="6" x2="6" y2="18"></line>
                        <line x1="6" y1="6" x2="18" y2="18"></line>
                      </svg></button>
                  </div>
                </div>
              </div>
              <div class="dropdown-footer-v3" v-if="shoppingStore.items.length > 0">
                <button @click="openGoogleMaps" class="btn-map-luxury">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                    <circle cx="12" cy="10" r="3"></circle>
                  </svg>
                  <span>{{ $t('header.nearby_markets') }}</span>
                </button>
              </div>
            </div>
          </transition>
        </div>

        <div class="action-wrapper">
          <button class="btn-icon" :class="{ active: chatStore.isMessengerOpen }" @click.stop="toggleChat">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
            <span v-if="chatStore.totalUnreadCount > 0" class="badge-count">{{ chatStore.totalUnreadCount }}</span>
          </button>
        </div>

        <div class="action-wrapper" @click.stop>
          <button class="btn-icon" :class="{ active: showNoti }" @click="toggleNoti">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
              <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
            </svg>
            <span v-if="unreadNotiCount > 0" class="badge-count">{{ unreadNotiCount }}</span>
          </button>
          <transition name="dropdown-anim">
            <div v-if="showNoti" class="common-dropdown noti-width">
              <div class="dropdown-header">
                <h3>{{ $t('header.notifications') }}</h3>
                <span class="action-link" @click="handleMarkAllRead">{{ $t('header.mark_all_read') }}</span>
              </div>
              <div class="dropdown-body scroll-body">
                <div v-for="n in notifications" :key="n.id" class="list-item noti-item" :class="{ unread: !n.isRead }"
                  @click="handleNotiClick(n)">
                  <div class="avatar-wrap">
                    <img :src="n.avatar || 'https://ui-avatars.com/api/?name=User&background=EA580C&color=fff'"
                      :alt="$t('common.user_avatar')">
                    <div class="noti-type-icon" :class="n.type">
                      <svg v-if="n.type === 'like'" width="10" height="10" viewBox="0 0 24 24" fill="white"
                        stroke="white">
                        <path
                          d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z">
                        </path>
                      </svg>
                      <svg v-else width="10" height="10" viewBox="0 0 24 24" fill="white" stroke="white">
                        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                      </svg>
                    </div>
                  </div>
                  <div class="item-info">
                    <p class="noti-text"><b>{{ n.username }}</b> {{ n.content }}</p>
                    <span class="time">{{ n.time }}</span>
                  </div>
                  <div v-if="!n.isRead" class="unread-dot"></div>
                </div>
              </div>
            </div>
          </transition>
        </div>
      </div>

      <div class="divider-vertical"></div>
      <LangSwitcher />

      <UserMenu v-if="authStore.isAuthenticated" @open-premium="emit('open-premium')"
        @open-support="showFeedbackModal = true" />
      <div v-else class="guest-actions">
        <button class="btn-login" @click="emit('open-login')">{{ $t('auth.login') }}</button>
        <button class="btn-signup" @click="emit('open-register')">{{ $t('auth.register') }}</button>
      </div>

      <div v-if="showNoti || showShopping" class="click-outside-header" @click="closeAllDropdowns"></div>
    </div>

    <Teleport to="body">
      <MapModal v-if="showMapModal" @close="showMapModal = false" />
      <FeedbackModal v-if="showFeedbackModal" @close="showFeedbackModal = false" :form="feedbackForm" />
      <audio id="notificationSound" src="/sounds/notification.mp3" preload="auto"></audio>
    </Teleport>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import { useShoppingStore } from '@/stores/shopping'
import UserMenu from './UserMenu.vue'
import LangSwitcher from '@/components/common/LangSwitcher.vue'
import MapModal from '@/components/modals/MapModal.vue'
import FeedbackModal from '@/components/modals/FeedbackModal.vue'
import SearchBox from '@/components/common/SearchBox.vue'
import { getNotifications, markNotificationRead, markAllNotificationsRead as apiMarkAllRead } from '@/services/notificationService'
import webSocketService from '@/services/webSocketService'
import { formatLocaleDate } from '@/i18n'

const emit = defineEmits(['open-login', 'open-register', 'open-premium'])

const authStore = useAuthStore();
const chatStore = useChatStore();
const shoppingStore = useShoppingStore();
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

// 🔥 LOGIC QUYẾT ĐỊNH MÀU HEADER: ĐEN HAY TRẮNG
const isDark = computed(() => {
  // 1. Nếu router cố tình đánh dấu là Dark Theme
  if (route.meta?.isDark) return true;

  // 2. Nếu là trang Admin (luôn dùng Header đen/đậm)
  if (route.path.startsWith('/admin')) return true;

  // 3. Các trang có nền màu/dark theme (ĐÃ XÓA '/storage' KHỎI DANH SÁCH NÀY)
  const premiumRoutes = ['/leaderboard', '/meal-plan', '/suggestions'];
  return premiumRoutes.some(path => route.path.startsWith(path));
});
const unreadNotiCount = computed(() => notifications.value.filter(n => !n.isRead).length)

const increaseBadge = () => {
  // Force update the computed property by triggering reactivity
  notifications.value = [...notifications.value];
};

const addNotificationToDropdown = (notification) => {
  const newNotification = {
    id: notification.notificationId,
    title: notification.title,
    content: notification.content,
    username: notification.username,
    avatar: notification.avatarUrl,
    time: notification.createdAt ? formatLocaleDate(notification.createdAt) : formatLocaleDate(new Date()),
    isRead: notification.isRead === 1,
    type: notification.type || 'like',
    link: notification.link
  };
  console.log('New notification added:', newNotification);
  notifications.value.unshift(newNotification);
};

const playNotificationSound = () => {
  const sound = document.getElementById("notificationSound");
  if (sound) {
    sound.currentTime = 0;
    sound.play().catch(err => console.log('Sound play failed:', err));
  }
};

const updateTabTitle = () => {
  const count = unreadNotiCount.value;
  if (count > 0) {
    document.title = "(" + count + ") " + originalTitle.value;
  } else {
    document.title = originalTitle.value;
  }
};

const resetBadge = () => {
  notifications.value.forEach(n => n.isRead = true);
  document.title = originalTitle.value;
};

const handleMarkAllRead = async () => {
  if (!authStore.user?.accountID) return
  try {
    await apiMarkAllRead(authStore.user.accountID);
    resetBadge();
  } catch (err) { }
}

const toggleShopping = () => { showNoti.value = false; showShopping.value = !showShopping.value; if (showShopping.value) shoppingStore.fetchCart(); }
const toggleNoti = () => { showShopping.value = false; showNoti.value = !showNoti.value; if (showNoti.value) loadNotifications(); }
const toggleChat = () => { chatStore.isMessengerOpen = !chatStore.isMessengerOpen; closeAllDropdowns(); }
const closeAllDropdowns = () => { showNoti.value = false; showShopping.value = false; }

const loadNotifications = async () => {
  if (!authStore.user?.accountID) return
  try {
    const data = await getNotifications(authStore.user.accountID)
    console.log("Notification API:", data);
    notifications.value = data.map(n => ({
      id: n.notificationID,
      title: n.title,
      content: n.content,
      username: n.username,
      avatar: n.avatarUrl,
      time: n.createdAt ? formatLocaleDate(n.createdAt) : '',
      isRead: n.isRead === 1,
      type: n.type || 'like',
      link: n.link
    }))
    console.log("Mapped notifications:", notifications.value);
  } catch (err) { }
}

const handleNotiClick = async (n) => {
  if (!n.isRead) {
    n.isRead = true;
    await markNotificationRead(n.id).catch(() => { });
  }
  showNoti.value = false;
  if (n.link) {
    router.push(n.link);
  }
}
const handleScroll = () => { isScrolled.value = window.scrollY > 10 }
const handleCreatePost = () => { authStore.isAuthenticated ? router.push('/create-post') : emit('open-login') }
const openGoogleMaps = () => { showMapModal.value = true; closeAllDropdowns(); }

// Handle real-time notifications
const handleRealtimeNotification = (event) => {
  const notificationDTO = event.detail;

  addNotificationToDropdown(notificationDTO);
  playNotificationSound();
  increaseBadge();
  updateTabTitle();

  // Send to other tabs
  if (notificationChannel.value) {
    notificationChannel.value.postMessage(notificationDTO);
  }

  // Optionally show a toast or browser notification
  if ('Notification' in window && Notification.permission === 'granted') {
    new Notification(notificationDTO.title, {
      body: notificationDTO.content,
      icon: `https://ui-avatars.com/api/?name=${encodeURIComponent(notificationDTO.title)}&background=EA580C&color=fff`
    });
  }
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  handleScroll();

  if (authStore.isAuthenticated) {
    shoppingStore.fetchCart();
    loadNotifications(); // Load notifications on mount
    // Connect to WebSocket for real-time notifications
    webSocketService.connect();
    // Listen for real-time notifications
    window.addEventListener('realtime-notification', handleRealtimeNotification);
    // Request notification permission
    requestNotificationPermission();
    // Setup BroadcastChannel for multi-tab sync
    notificationChannel.value = new BroadcastChannel("notifications");
    notificationChannel.value.onmessage = (event) => {
      const notification = event.data;
      addNotificationToDropdown(notification);
      playNotificationSound();
      updateTabTitle();
    };
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  // Disconnect WebSocket and remove event listener
  webSocketService.disconnect();
  window.removeEventListener('realtime-notification', handleRealtimeNotification);
  // Close BroadcastChannel
  if (notificationChannel.value) {
    notificationChannel.value.close();
  }
})

// Watch for authentication changes
watch(() => authStore.isAuthenticated, (isAuthenticated) => {
  if (isAuthenticated) {
    webSocketService.connect();
    window.addEventListener('realtime-notification', handleRealtimeNotification);
    requestNotificationPermission();
    // Setup BroadcastChannel
    notificationChannel.value = new BroadcastChannel("notifications");
    notificationChannel.value.onmessage = (event) => {
      const notification = event.data;
      addNotificationToDropdown(notification);
      playNotificationSound();
      updateTabTitle();
    };
  } else {
    webSocketService.disconnect();
    window.removeEventListener('realtime-notification', handleRealtimeNotification);
    // Close BroadcastChannel
    if (notificationChannel.value) {
      notificationChannel.value.close();
    }
  }
});

// Request browser notification permission
const requestNotificationPermission = () => {
  if ('Notification' in window && Notification.permission === 'default') {
    Notification.requestPermission();
  }
};
</script>

<style scoped lang="scss" src="./Header.scss"></style>