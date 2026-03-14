<template>
  <header class="content-header" :class="[{ 'is-scrolled': isScrolled }, { 'theme-dark': route.meta?.isDark }]">
    
    <SearchBox />

    <div class="header-right">
      <button class="btn-create-post" @click="handleCreatePost">
        <span class="plus-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
        </span> 
        {{ $t('header.write_post') }}
      </button>

      <div class="icon-actions" v-if="authStore.isAuthenticated">
        
        <div class="action-wrapper" @click.stop>
          <button class="btn-icon" :class="{ active: showShopping }" title="Shopping List" @click="toggleShopping">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg>
            <span v-if="shoppingStore.unreadCount > 0" class="badge-dot"></span>
          </button>

          <transition name="dropdown-anim">
            <div v-if="showShopping" class="common-dropdown shopping-width">
              <div class="dropdown-header">
                <h3>{{ $t('header.shopping_list') }}</h3>
                <div style="display: flex; gap: 15px;">
                  <span v-if="shoppingStore.items.some(i => i.checked)" class="action-link" @click="shoppingStore.removeCheckedItems()" style="color: #EF4444;">
                    🗑️ Xóa món đã tick
                  </span>
                  <span class="action-link" @click="shoppingStore.clearItems()">{{ $t('header.clear_all') }}</span>
                </div>
              </div>
              <div class="dropdown-body scroll-body">
                <div v-if="shoppingStore.items.length === 0" class="empty-state">
                  <p>{{ $t('header.shopping_empty') }}</p>
                </div>
                <div v-else v-for="(item, idx) in shoppingStore.items" :key="idx" class="shop-item" :class="{ checked: item.checked }" @click="shoppingStore.toggleItem(idx)">
                  <div class="checkbox-circle">
                    <svg v-if="item.checked" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                  </div>
                  <div class="item-text">
                    <span class="name">{{ item.name }}</span>
                    <span class="qty" style="color: #EA580C; font-size: 0.75rem;">🍲 {{ item.quantity }}</span> 
                  </div>
                </div>
              </div>
              <div class="dropdown-footer" v-if="shoppingStore.items.length > 0" style="padding: 12px; border-top: 1px solid #F3F4F6;">
                <button @click="openGoogleMaps" class="btn-map-action">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                  Tìm Siêu thị / Chợ
                </button>
              </div>
            </div>
          </transition>
        </div>

        <div class="action-wrapper">
          <button class="btn-icon" :class="{ active: chatStore.isMessengerOpen }" title="Messages" @click.stop="toggleChat">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            <span v-if="chatStore.totalUnreadCount > 0" class="badge-count">{{ chatStore.totalUnreadCount }}</span>
          </button>
        </div>
        
        <div class="action-wrapper" @click.stop>
          <button class="btn-icon" :class="{ active: showNoti }" title="Notifications" @click="toggleNoti">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
            <span v-if="unreadNotiCount > 0" class="badge-count">{{ unreadNotiCount }}</span>
          </button>

          <transition name="dropdown-anim">
            <div v-if="showNoti" class="common-dropdown noti-width">
              <div class="dropdown-header">
                <h3>{{ $t('header.notifications') }}</h3>
                <span class="action-link" @click="markAllRead">{{ $t('header.mark_all_read') }}</span>
              </div>
              <div class="dropdown-body scroll-body">
                <div v-for="n in notifications" :key="n.id" class="list-item noti-item" :class="{ unread: !n.isRead }" @click="handleNotiClick(n)">
                  <div class="avatar-wrap">
                    <img :src="n.avatar">
                    <div class="noti-type-icon" :class="n.type">
                      <svg v-if="n.type === 'like'" width="10" height="10" viewBox="0 0 24 24" fill="white" stroke="white"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
                      <svg v-else width="10" height="10" viewBox="0 0 24 24" fill="white" stroke="white"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
                    </div>
                  </div>
                  <div class="item-info">
                    <p class="noti-text"><b>{{n.user}}</b> {{n.action}}</p>
                    <span class="time">{{n.time}}</span>
                  </div>
                  <div v-if="!n.isRead" class="unread-dot"></div>
                </div>
              </div>
              <div class="dropdown-footer">
                <a href="#">{{ $t('header.see_all_noti') }}</a>
              </div>
            </div>
          </transition>
        </div>
      </div>
      
      <div class="divider-vertical"></div>

      <LangSwitcher />

      <div v-if="authStore.isAuthenticated" class="user-menu-container">
        <div class="avatar-trigger" 
             :class="{ 'is-premium-wrap': isPremiumUser, 'is-admin-wrap': isAdminUser }" 
             @click.stop="toggleDropdown">
          
          <div v-if="isAdminUser" class="role-badge-admin" title="Administrator">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="white"><path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z"></path></svg>
          </div>

          <img :src="displayAvatar" @error="handleAvatarError" class="user-avt" alt="User">
        </div>
        
        <transition name="dropdown-anim">
          <div v-if="isDropdownOpen" class="user-dropdown" @click.stop>
            <div class="user-header">
              <div class="header-avt-container" :class="{ 'premium-border': isPremiumUser }">
                <img :src="displayAvatar" @error="handleAvatarError" class="header-avt" alt="User">
                <div v-if="isAdminUser" class="admin-tag-mini">ADMIN</div>
              </div>
              <div class="header-info">
                <div class="name">
                  {{ authStore.user.name || authStore.user.username }}
                  <svg v-if="isPremiumUser" width="14" height="14" viewBox="0 0 24 24" fill="#F59E0B" style="margin-left: 4px;"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14l-5-4.87 6.91-1.01L12 2z"></path></svg>
                  <svg v-if="isAdminUser" width="14" height="14" viewBox="0 0 24 24" fill="#6366F1" style="margin-left: 2px;"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"></path></svg>
                </div>
                <div class="handle">@cook_{{ authStore.user.id || authStore.user.accountID }}</div>
              </div>
            </div>
            
            <ul class="menu-list">
              <li @click="goToAdmin" class="admin-link">
                <span class="icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg></span>
                {{ $t('header.admin_panel') }}
              </li>
              <li class="divider"></li>
              <li @click="goToProfile">
                <span class="icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg></span>
                {{ $t('header.my_profile') }}
              </li>
              <li @click="openPremium" class="highlight">
                <span class="icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg></span>
                {{ $t('header.upgrade_vip') }}
              </li>
              <li class="divider"></li>
              <li @click="openBugModal">
                <span class="icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg></span>
                {{ $t('header.report_bug') }}
              </li>
              <li @click="handleLogout" class="danger">
                <span class="icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg></span>
                {{ $t('auth.logout') }}
              </li>
            </ul>
          </div>
        </transition>
      </div>

      <div v-else class="guest-actions">
        <button class="btn-login" @click="$emit('open-login')">{{ $t('auth.login') }}</button>
        <button class="btn-signup" @click="$emit('open-register')">{{ $t('auth.register') }}</button>
      </div>
      
      <div v-if="isDropdownOpen || showNoti || showShopping" class="click-outside-header" @click="closeAllDropdowns"></div>
    </div>

    <Teleport to="body">
      <transition name="fade">
        <div v-if="showBugReport" class="modal-backdrop" @click.self="showBugReport = false">
            </div>
      </transition>
      <MapModal v-if="showMapModal" @close="showMapModal = false" />
    </Teleport>

  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import { useShoppingStore } from '@/stores/shopping'
import LangSwitcher from '@/components/common/LangSwitcher.vue'
import MapModal from '@/components/modals/MapModal.vue'
import SearchBox from '@/components/common/SearchBox.vue'
import { toast } from '@/composables/useToast'
import { useI18n } from 'vue-i18n'
import { getNotifications, markAllNotificationsRead, markNotificationRead } from '@/services/notificationService'

const { t } = useI18n()
const authStore = useAuthStore()
const chatStore = useChatStore()
const shoppingStore = useShoppingStore()
const router = useRouter()
const route = useRoute() 

// State
const showShopping = ref(false)
const showNoti = ref(false)
const isDropdownOpen = ref(false)
const isScrolled = ref(false)
const showBugReport = ref(false)
const showMapModal = ref(false)
const bugForm = ref({ type: 'ui', desc: '' })

// Notifications State
const notifications = ref([])
const unreadNotiCount = computed(() => notifications.value.filter(n => !n.isRead).length)

// 🔥 KIỂM TRA QUYỀN VIP
const isPremiumUser = computed(() => {
  const user = authStore.user;
  return !!user && (
    String(user.isPremium) === "1" || 
    String(user.isPremium) === "true" || 
    user.role === 'premium'
  );
});

// 🔥 KIỂM TRA QUYỀN ADMIN
const isAdminUser = computed(() => {
  const user = authStore.user;
  return !!user && (
    user.isAdmin === 1 || 
    user.role === 'admin'
  );
});

// 🔥 COMPUTED: Tính toán Avatar
const displayAvatar = computed(() => {
  const avt = authStore.user?.avatar;
  if (avt && avt.startsWith('http')) return avt;
  const name = encodeURIComponent(authStore.user?.name || authStore.user?.username || 'G');
  return `https://ui-avatars.com/api/?name=${name}&background=EA580C&color=fff&bold=true`;
});

const handleAvatarError = (e) => {
  const name = encodeURIComponent(authStore.user?.name || authStore.user?.username || 'G');
  e.target.src = `https://ui-avatars.com/api/?name=${name}&background=EA580C&color=fff&bold=true`;
};

// Actions (Giữ nguyên logic cũ)
const closeAllDropdowns = () => { 
  isDropdownOpen.value = false; 
  showNoti.value = false; 
  showShopping.value = false 
}

const toggleShopping = () => { 
  const s = !showShopping.value; 
  closeAllDropdowns(); 
  showShopping.value = s;
  if (s && authStore.isAuthenticated) shoppingStore.fetchCart(); 
}

const toggleChat = () => { 
  chatStore.isMessengerOpen = !chatStore.isMessengerOpen;
  closeAllDropdowns(); 
}

const toggleNoti = () => {
  const s = !showNoti.value
  closeAllDropdowns()
  showNoti.value = s
  if (s) loadNotifications()
}

const toggleDropdown = () => { 
  const s = !isDropdownOpen.value; 
  closeAllDropdowns(); 
  isDropdownOpen.value = s 
}

const loadNotifications = async () => {
  const user = authStore.user
  if (!user?.accountID) return
  try {
    const data = await getNotifications(user.accountID)
    notifications.value = data.map(n => ({
      id: n.notificationID,
      user: n.title,
      action: n.content,
      time: n.createdAt ? new Date(n.createdAt).toLocaleDateString('vi-VN') : '',
      isRead: n.isRead === 1,
      type: n.type || 'general',
      targetId: n.postID || 0,
      avatar: `https://ui-avatars.com/api/?name=${encodeURIComponent(n.title)}&background=EA580C&color=fff`
    }))
  } catch { /* silent */ }
}

const handleNotiClick = async (noti) => {
  if (!noti.isRead) {
    noti.isRead = true
    markNotificationRead(noti.id).catch(() => {})
  }
  showNoti.value = false
  if (noti.targetId) router.push({ name: 'PostDetail', params: { id: noti.targetId } })
}

const handleLogout = async () => { 
  closeAllDropdowns(); 
  localStorage.removeItem('user'); 
  localStorage.removeItem('token'); 
  authStore.user = null; 
  authStore.isAuthenticated = false; 
  await router.push({ path: '/', hash: '#sectionsigninlanding' }); 
}

const handleScroll = () => { isScrolled.value = window.scrollY > 10 }
const openBugModal = () => { closeAllDropdowns(); showBugReport.value = true }
const openGoogleMaps = () => { showMapModal.value = true; closeAllDropdowns() }
const goToAdmin = () => { closeAllDropdowns(); router.push('/admin/dashboard') }
const handleCreatePost = () => authStore.isAuthenticated ? router.push('/create-post') : emit('open-login')
const goToProfile = () => { closeAllDropdowns(); router.push('/profile') }
const openPremium = () => { closeAllDropdowns(); emit('open-premium') }

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  if (authStore.isAuthenticated) {
    loadNotifications()
    shoppingStore.fetchCart() 
  }
})
onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<style scoped lang="scss" src="./Header.scss"></style>
