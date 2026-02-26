<template>
  <header class="content-header" :class="{ 'is-scrolled': isScrolled }">
    
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
                  <span 
                    v-if="shoppingStore.items.some(i => i.checked)" 
                    class="action-link" 
                    @click="shoppingStore.removeCheckedItems()" 
                    style="color: #EF4444;"
                  >
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
                <button @click="openGoogleMaps" style="width: 100%; padding: 12px; background: #EA580C; color: white; border: none; border-radius: 8px; font-weight: 700; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px; transition: 0.2s;" onmouseover="this.style.background='#C2410C'" onmouseout="this.style.background='#EA580C'">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                  Tìm Siêu thị / Chợ
                </button>
              </div>

            </div>
          </transition>
        </div>
        <div class="action-wrapper" @click.stop>
          <button class="btn-icon" :class="{ active: showChat }" title="Messages" @click="toggleChat">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            <span v-if="unreadMessages > 0" class="badge-count">{{ unreadMessages }}</span>
          </button>

          <transition name="dropdown-anim">
            <div v-if="showChat" class="common-dropdown chat-width">
              <div class="dropdown-header">
                <h3>{{ $t('header.messages') }}</h3>
                <span class="action-icon">•••</span>
              </div>
              <div class="dropdown-body scroll-body">
                <div v-for="c in conversations" :key="c.id" class="list-item" @click="openMiniChat(c)">
                  <div class="avatar-wrap">
                    <img :src="c.avatar">
                    <div v-if="c.online" class="online-status"></div>
                  </div>
                  <div class="item-info">
                    <div class="top-line">
                      <span class="name">{{ c.name }}</span>
                      <span class="time">{{ c.time }}</span>
                    </div>
                    <div class="preview" :class="{ unread: !c.read }">{{ c.lastMessage }}</div>
                  </div>
                  <div v-if="!c.read" class="unread-dot"></div>
                </div>
              </div>
              <div class="dropdown-footer">
                <a href="#">{{ $t('header.see_all_messages') }}</a>
              </div>
            </div>
          </transition>
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
                <div 
                  v-for="n in notifications" 
                  :key="n.id" 
                  class="list-item noti-item" 
                  :class="{ unread: !n.isRead }"
                  @click="handleNotiClick(n)" 
                >
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
        <div class="avatar-trigger" @click.stop="toggleDropdown">
          <img :src="authStore.user.avatar || 'https://i.pravatar.cc/300'" class="user-avt">
        </div>
        
        <transition name="dropdown-anim">
          <div v-if="isDropdownOpen" class="user-dropdown" @click.stop>
            <div class="user-header">
              <img :src="authStore.user.avatar || 'https://i.pravatar.cc/300'" class="header-avt">
              <div class="header-info">
                <div class="name">{{ authStore.user.name }}</div>
                <div class="handle">@cook_{{ authStore.user.id }}</div>
              </div>
            </div>
            
            <ul class="menu-list">
              <li @click="goToAdmin" class="admin-link">
                <span class="icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
                </span>
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
      
      <div v-if="isDropdownOpen || showNoti || showChat || showShopping" class="click-outside-header" @click="closeAllDropdowns"></div>
    </div>

    <Teleport to="body">
      <transition name="fade">
        <div v-if="showBugReport" class="modal-backdrop" @click.self="showBugReport = false">
          <div class="bug-modal-content">
            <div class="modal-head">
              <h3>{{ $t('header.bug_modal_title') }}</h3>
              <button class="btn-close" @click="showBugReport = false">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
              </button>
            </div>
            <div class="modal-body-form">
              <div class="form-group">
                <label>{{ $t('header.issue_type') }}</label>
                <select v-model="bugForm.type" class="form-select">
                  <option value="ui">{{ $t('header.bug_ui') }}</option>
                  <option value="func">{{ $t('header.bug_func') }}</option>
                  <option value="other">{{ $t('header.bug_other') }}</option>
                </select>
              </div>
              <div class="form-group">
                <label>{{ $t('header.description') }}</label>
                <textarea v-model="bugForm.desc" class="form-textarea" placeholder="Describe the issue you encountered..."></textarea>
              </div>
            </div>
            <div class="modal-foot">
              <button class="btn-cancel" @click="showBugReport = false">{{ $t('common.cancel') }}</button>
              <button class="btn-submit" @click="submitBug">{{ $t('header.submit_report') }}</button>
            </div>
          </div>
        </div>
      </transition>

      <MapModal v-if="showMapModal" @close="showMapModal = false" />
    </Teleport>

  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import { useShoppingStore } from '@/stores/shopping'
import LangSwitcher from '@/components/common/LangSwitcher.vue'
import MapModal from '@/components/modals/MapModal.vue'
// 🌟 Nhập file Component SearchBox vào đây (nhớ điều chỉnh đường dẫn nếu cần)
import SearchBox from '@/components/common/SearchBox.vue'
import { toast } from '@/composables/useToast'
import { useI18n } from 'vue-i18n'
import { getNotifications, markAllNotificationsRead, markNotificationRead } from '@/services/notificationService'

const { t } = useI18n()
const authStore = useAuthStore()
const chatStore = useChatStore()
const shoppingStore = useShoppingStore()
const router = useRouter()

// 🌟 Đã xóa hết biến searchKeyword, isSearchFocused, handleSearch vì SearchBox đã lo liệu

// State
const showShopping = ref(false)
const showChat = ref(false)
const showNoti = ref(false)
const isDropdownOpen = ref(false)
const isScrolled = ref(false)
const showBugReport = ref(false)
const showMapModal = ref(false)
const bugForm = ref({ type: 'ui', desc: '' })

// Mock Data
const conversations = ref([
  { id: 1, name: 'Bếp Trưởng Gomet', avatar: 'https://i.pravatar.cc/150?u=chef', lastMessage: 'Công thức này tuyệt lắm!', time: '5p', read: false, online: true },
  { id: 2, name: 'Hội Yêu Bếp', avatar: 'https://ui-avatars.com/api/?name=H&background=random', lastMessage: 'Mai: Cảm ơn nhé', time: '1h', read: true, online: false },
])
const notifications = ref([])

const unreadMessages = computed(() => conversations.value.filter(c => !c.read).length)
const unreadNotiCount = computed(() => notifications.value.filter(n => !n.isRead).length)

// Load real notifications from API
const loadNotifications = async () => {
  const user = authStore.currentUser
  if (!user?.accountID) return
  try {
    const data = await getNotifications(user.accountID)
    notifications.value = data.map(n => ({
      id:       n.notificationID,
      user:     n.title,
      action:   n.content,
      time:     n.createdAt ? new Date(n.createdAt).toLocaleDateString('vi-VN') : '',
      isRead:   n.isRead === 1,
      type:     n.type || 'general',
      targetId: n.postID || 0,
      avatar:   `https://ui-avatars.com/api/?name=${encodeURIComponent(n.title)}&background=EA580C&color=fff`
    }))
  } catch { /* silent */ }
}

// Actions
const closeAllDropdowns = () => { isDropdownOpen.value = false; showChat.value = false; showNoti.value = false; showShopping.value = false }

const toggleShopping = () => { 
  const s = !showShopping.value; 
  closeAllDropdowns(); 
  showShopping.value = s;
  if (s && authStore.isAuthenticated) {
    shoppingStore.fetchCart(); 
  }
}

const toggleChat = () => { const s = !showChat.value; closeAllDropdowns(); showChat.value = s }
const toggleNoti = () => {
  const s = !showNoti.value
  closeAllDropdowns()
  showNoti.value = s
  if (s) loadNotifications()
}
const toggleDropdown = () => { const s = !isDropdownOpen.value; closeAllDropdowns(); isDropdownOpen.value = s }

const openBugModal = () => { closeAllDropdowns(); showBugReport.value = true }
const submitBug = () => { toast.success(t('toast.bug_sent')); showBugReport.value = false; bugForm.value.desc = '' }

const openGoogleMaps = () => {
  showMapModal.value = true 
  closeAllDropdowns()       
}

const openMiniChat = (user) => { 
  chatStore.openChat(user)
  showChat.value = false 
}
const handleNotiClick = async (noti) => {
  if (!noti.isRead) {
    noti.isRead = true
    markNotificationRead(noti.id).catch(() => {})
  }
  showNoti.value = false
  if (noti.targetId) router.push({ name: 'PostDetail', params: { id: noti.targetId } })
}

const goToAdmin = () => {
  closeAllDropdowns()
  router.push('/admin/dashboard') 
}

const handleLogout = async () => { 
  closeAllDropdowns(); 
  localStorage.removeItem('user'); 
  localStorage.removeItem('token'); 
  authStore.user = null; 
  authStore.isAuthenticated = false; 
  await router.push({ path: '/', hash: '#sectionsigninlanding' }); 
}
const handleCreatePost = () => authStore.isAuthenticated ? router.push('/create-post') : emit('open-login')
const goToProfile = () => { closeAllDropdowns(); router.push('/profile') }
const openPremium = () => { closeAllDropdowns(); emit('open-premium') }
const markAllRead = async () => {
  notifications.value.forEach(n => { n.isRead = true })
  const user = authStore.currentUser
  if (user?.accountID) {
    markAllNotificationsRead(user.accountID).catch(() => {})
  }
}

const handleScroll = () => { isScrolled.value = window.scrollY > 10 }

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