<template>
  <header class="mobile-header" :class="[
    { 'is-scrolled': isScrolled },
    { 'is-dark-theme': isDark }
  ]">
    <button class="m-btn-icon hamburger-trigger" @click="openSidebar">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
        <line x1="3" y1="12" x2="21" y2="12"></line>
        <line x1="3" y1="6" x2="21" y2="6"></line>
        <line x1="3" y1="18" x2="21" y2="18"></line>
      </svg>
    </button>

    <div class="m-search-area">
      <SearchBox />
    </div>

    <div class="m-header-right">
      <button class="m-btn-create" @click="handleCreatePost">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
      </button>

      <div class="m-icon-group" v-if="authStore.isAuthenticated">
        <button class="m-btn-icon" @click="toggleShopping">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
            <line x1="3" y1="6" x2="21" y2="6"></line>
            <path d="M16 10a4 4 0 0 1-8 0"></path>
          </svg>
          <span v-if="shoppingStore.items.length > 0" class="m-badge">{{ shoppingStore.items.length }}</span>
        </button>

        <button class="m-btn-icon" @click="toggleNoti">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
          </svg>
          <span v-if="unreadNotiCount > 0" class="m-badge">{{ unreadNotiCount }}</span>
        </button>
      </div>

      <UserMenu v-if="authStore.isAuthenticated" class="m-user-menu" />
      <button v-else class="m-btn-login" @click="emit('open-login')">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
      </button>
    </div>

    <Teleport to="body">
       <transition name="m-slide">
          <div v-if="showShopping || showNoti" class="m-dropdown-overlay" @click="closeAll">
             <div class="m-dropdown-content" @click.stop>
                
                <div v-if="showShopping" class="m-drop-pane">
                   <div class="m-drop-head">
                      <div class="m-head-title">
                        <h3>Giỏ đi chợ</h3>
                        <div class="m-head-links">
                           <button v-if="shoppingStore.items.some(i => i.checked)" class="m-link danger" @click="shoppingStore.removeCheckedItems()">Xóa đã tick</button>
                           <button v-if="shoppingStore.items.length > 0" class="m-link" @click="shoppingStore.clearItems()">Xóa hết</button>
                        </div>
                      </div>
                      <button class="btn-close-m" @click="closeAll">✖</button>
                   </div>
                   <div class="m-drop-body custom-scroll">
                      <div v-if="shoppingStore.items.length === 0" class="m-empty-state">
                        <p>Giỏ trống!</p>
                      </div>
                      <div v-else v-for="(item, idx) in shoppingStore.items" :key="idx" 
                           class="m-list-item" :class="{ 'checked': item.checked }"
                           @click="shoppingStore.toggleItem(idx)">
                        <div class="m-check-box">
                           <svg v-if="item.checked" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="4"><polyline points="20 6 9 17 4 12"></polyline></svg>
                        </div>
                        <div class="m-item-info">
                           <span class="m-item-name">{{ item.name }}</span>
                           <span class="m-item-qty">Bài viết: {{ item.quantity }}</span>
                        </div>
                      </div>
                   </div>
                </div>

                <div v-if="showNoti" class="m-drop-pane">
                   <div class="m-drop-head">
                      <div class="m-head-title">
                        <h3>Thông báo</h3>
                        <div class="m-head-links">
                          <button v-if="unreadNotiCount > 0" class="m-link" @click="handleMarkAllRead">Đánh dấu đã đọc</button>
                          <button v-if="readNotiCount > 0" class="m-link danger" @click="handleDeleteRead">Xóa đã đọc</button>
                        </div>
                      </div>
                      <button class="btn-close-m" @click="closeAll">✖</button>
                   </div>
                   <div class="m-drop-body custom-scroll">
                      <p v-if="notifications.length === 0" class="m-empty-state">Chưa có thông báo nào.</p>
                      <div v-else v-for="n in notifications" :key="n.id" 
                           class="m-noti-item" :class="{ 'unread': !n.isRead }"
                           @click="handleNotiClick(n)">
                        <img :src="n.avatar" class="m-noti-avt">
                        <div class="m-noti-content">
                           <p><strong>{{ n.user }}</strong> {{ n.action }}</p>
                           <span>{{ n.time }}</span>
                        </div>
                        <div v-if="!n.isRead" class="m-unread-dot"></div>
                      </div>
                   </div>
                </div>

             </div>
          </div>
       </transition>
    </Teleport>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useShoppingStore } from '@/stores/shopping'
import SearchBox from '@/components/common/SearchBox.vue'
import UserMenu from './UserMenu.vue'
import {
  getNotifications,
  markNotificationRead,
  markAllNotificationsRead as apiMarkAllRead,
  deleteReadNotifications as apiDeleteReadNotifications
} from '@/services/notificationService'
import { toast } from '@/composables/useToast'

const emit = defineEmits(['open-login', 'open-premium'])
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const shoppingStore = useShoppingStore()

const isScrolled = ref(false)
const showShopping = ref(false)
const showNoti = ref(false)
const notifications = ref([])

const isDark = computed(() => route.meta?.isDark || route.path.startsWith('/admin'))
const unreadNotiCount = computed(() => notifications.value.filter(n => !n.isRead).length)
const readNotiCount = computed(() => notifications.value.filter(n => n.isRead).length)

const openSidebar = () => {
  window.dispatchEvent(new CustomEvent('ui:toggle-sidebar'));
}

const toggleShopping = () => {
  if (!authStore.isAuthenticated) {
    emit('open-login');
    return;
  }
  const role = String(authStore.user?.role || '').toLowerCase();
  const isPremiumUser = role === 'premium' || [1, "1", true].includes(authStore.user?.isPremium);
  const isAdmin = role === 'admin' || [1, "1", true].includes(authStore.user?.isAdmin);
  
  if (!isPremiumUser && !isAdmin) {
    toast.warn('Giỏ đi chợ là đặc quyền dành cho tài khoản Premium!');
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

const handleMarkAllRead = async () => {
  if (!authStore.user?.accountID) return
  try {
    await apiMarkAllRead(authStore.user.accountID);
    notifications.value.forEach(n => n.isRead = true);
    toast.success("Đã đánh dấu tất cả là đã đọc");
  } catch (err) { }
}

const handleDeleteRead = async () => {
  if (!authStore.user?.accountID) return
  try {
    await apiDeleteReadNotifications(authStore.user.accountID)
    notifications.value = notifications.value.filter(n => !n.isRead)
    toast.success("Đã xóa các thông báo đã đọc")
  } catch (err) {
    toast.error("Không thể xóa thông báo đã đọc")
  }
}

const loadNotifications = async () => {
  if (!authStore.user?.accountID) return;
  try {
    const data = await getNotifications(authStore.user.accountID);
    notifications.value = data.map(n => ({
      id: n.notificationID, 
      user: n.title, 
      action: n.content,
      time: n.createdAt ? new Date(n.createdAt).toLocaleDateString('vi-VN') : '',
      isRead: n.isRead === 1,
      avatar: `https://ui-avatars.com/api/?name=${encodeURIComponent(n.title)}&background=EA580C&color=fff`,
      link: n.link
    }));
  } catch (err) { }
}

const handleNotiClick = async (n) => {
  if (!n.isRead) {
    n.isRead = true;
    await markNotificationRead(n.id).catch(() => {});
  }
  closeAll();
  if (n.link) router.push(n.link);
}

const closeAll = () => { showShopping.value = false; showNoti.value = false }
const handleScroll = () => { isScrolled.value = window.scrollY > 10 }
const handleCreatePost = () => authStore.isAuthenticated ? router.push('/create-post') : emit('open-login')

onMounted(() => { 
  window.addEventListener('scroll', handleScroll);
  if (authStore.isAuthenticated) {
    loadNotifications();
    shoppingStore.fetchCart();
  }
})
onUnmounted(() => { window.removeEventListener('scroll', handleScroll) })

const checkAdminAccess = () => {
  const isAdmin = authStore.user?.isAdmin === 1 || authStore.user?.role === 'admin';
  if (isAdmin && window.innerWidth < 1024 && route.path.startsWith('/admin')) {
    router.push('/home');
  }
}
watch(() => route.path, checkAdminAccess);
</script>

<style scoped>
/* CODE GIỮ NGUYÊN TỪ CŨ */
.mobile-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 12px; height: 60px; gap: 8px;
  position: sticky; top: 0; z-index: 1500;
  background: #FFFFFF; border-bottom: 1px solid #F1F5F9;
}

.hamburger-trigger { color: #1C1917; padding-right: 4px; }
.m-search-area { flex: 1; min-width: 0; }
.m-search-area :deep(.btn-search-action),
.m-search-area :deep(.icon-prefix) { display: none !important; }

.m-header-right { display: flex; align-items: center; gap: 4px; flex-shrink: 0; }
.m-icon-group { display: flex; align-items: center; gap: 2px; }

.m-btn-icon {
  background: none; border: none; padding: 6px;
  color: #1C1917; position: relative;
  display: flex; align-items: center; justify-content: center;
}

.m-btn-create {
  width: 34px; height: 34px; border-radius: 50%;
  background: #EA580C; color: white; border: none;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 10px rgba(234, 88, 12, 0.3); margin-right: 4px;
}

.m-badge {
  position: absolute; top: 2px; right: 2px;
  background: #EF4444; color: white; font-size: 8px;
  min-width: 14px; height: 14px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  border: 1.5px solid white;
}

.m-dropdown-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 2000;
  display: flex; align-items: flex-end; backdrop-filter: blur(4px);
}
.m-dropdown-content {
  width: 100%; max-height: 75vh; background: white;
  border-radius: 28px 28px 0 0; overflow: hidden;
  box-shadow: 0 -10px 30px rgba(0,0,0,0.1);
}
.m-drop-pane { display: flex; flex-direction: column; height: 100%; }

.m-drop-head { 
  display: flex; justify-content: space-between; align-items: flex-start; 
  padding: 20px; border-bottom: 1px solid #f1f5f9;
}
.m-head-title { display: flex; flex-direction: column; gap: 4px; }
.m-head-title h3 { font-size: 1.1rem; font-weight: 800; color: #0f172a; margin: 0; }
.m-head-links { display: flex; gap: 12px; }
.m-link { 
  background: none; border: none; padding: 0; 
  font-size: 0.75rem; font-weight: 700; color: #ea580c; cursor: pointer;
}
.m-link.danger { color: #ef4444; }

.btn-close-m { background: #f1f5f9; border: none; width: 30px; height: 30px; border-radius: 50%; font-size: 12px; }

.m-drop-body { padding: 10px 0; overflow-y: auto; flex: 1; }

/* 🔥 FIX HIỂN THỊ GIỎ ĐI CHỢ MOBILE */
.m-list-item {
  display: flex; align-items: flex-start; gap: 14px; padding: 14px 20px;
  transition: 0.2s; border-bottom: 1px solid #f8fafc;
}
.m-list-item.checked { opacity: 0.5; }

.m-check-box { 
  width: 24px; height: 24px; border-radius: 50%; border: 2px solid #e2e8f0; 
  display: flex; align-items: center; justify-content: center; color: #ea580c;
  flex-shrink: 0; margin-top: 2px;
}
.m-list-item.checked .m-check-box { background: #ea580c; border-color: #ea580c; color: white; }

/* Tách biệt Tên nguyên liệu và Tên bài viết */
.m-item-info {
  display: flex;
  flex-direction: column;
  gap: 4px; /* Tạo khoảng cách giữa 2 dòng */
  flex: 1;
}

.m-item-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: #1e293b;
}

.m-item-qty {
  font-size: 0.8rem;
  font-weight: 500;
  color: #ea580c; /* Màu cam nổi bật để phân biệt bài viết */
}

/* THÔNG BÁO */
.m-noti-item { display: flex; align-items: center; gap: 12px; padding: 12px 20px; border-bottom: 1px solid #f8fafc; }
.m-noti-item.unread { background: #fff7ed; }
.m-noti-avt { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; }
.m-noti-content p { margin: 0; font-size: 0.85rem; line-height: 1.4; color: #334155; }
.m-noti-content span { font-size: 0.75rem; color: #94a3b8; }
.m-unread-dot { width: 8px; height: 8px; background: #ea580c; border-radius: 50%; margin-left: auto; }

.m-empty-state { padding: 40px; text-align: center; color: #94a3b8; font-weight: 600; }

.is-dark-theme.mobile-header { background: #11100F; border-bottom-color: #262626; }
.is-dark-theme .m-btn-icon, .is-dark-theme .hamburger-trigger { color: white; }

.m-slide-enter-active, .m-slide-leave-active { transition: transform 0.4s cubic-bezier(0.165, 0.84, 0.44, 1); }
.m-slide-enter-from, .m-slide-leave-to { transform: translateY(100%); }

.custom-scroll::-webkit-scrollbar { width: 0; }
</style>
