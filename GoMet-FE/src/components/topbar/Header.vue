<template>
  <header class="content-header" :class="{ 'is-scrolled': isScrolled }">
    
    <div class="search-wrapper-centered">
      <div class="search-box-vip" :class="{ 'is-focused': isSearchFocused }">
        <span class="search-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
        </span>
        <input 
          type="text" 
          v-model="searchKeyword"
          placeholder="Tìm kiếm món ăn..." 
          @focus="isSearchFocused = true"
          @blur="isSearchFocused = false"
          @keyup.enter="handleSearch"
        />
        <button class="btn-search-submit" @click="handleSearch">Tìm kiếm</button>
      </div>
    </div>

    <div class="header-right">
      <button class="btn-create-post" @click="handleCreatePost">
        <span class="plus-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
        </span> 
        Viết bài
      </button>

      <div class="icon-actions" v-if="authStore.isAuthenticated">
        
        <div class="action-wrapper" @click.stop>
          <button class="btn-icon" :class="{ active: showShopping }" title="Đi chợ" @click="toggleShopping">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg>
            <span v-if="shoppingItems.length > 0" class="badge-dot"></span>
          </button>

          <transition name="dropdown-anim">
            <div v-if="showShopping" class="common-dropdown shopping-width">
              <div class="dropdown-header">
                <h3>Danh sách đi chợ</h3>
                <span class="action-link" @click="clearShoppingList">Xóa hết</span>
              </div>
              <div class="dropdown-body scroll-body">
                <div v-if="shoppingItems.length === 0" class="empty-state">
                  <p>Danh sách trống.</p>
                </div>
                <div v-else v-for="(item, idx) in shoppingItems" :key="idx" class="shop-item" :class="{ checked: item.checked }" @click="item.checked = !item.checked">
                  <div class="checkbox-circle">
                    <svg v-if="item.checked" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                  </div>
                  <div class="item-text">
                    <span class="name">{{ item.name }}</span>
                    <span class="qty">{{ item.quantity }}</span>
                  </div>
                </div>
              </div>
            </div>
          </transition>
        </div>

        <div class="action-wrapper" @click.stop>
          <button class="btn-icon" :class="{ active: showChat }" title="Tin nhắn" @click="toggleChat">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            <span v-if="unreadMessages > 0" class="badge-count">{{ unreadMessages }}</span>
          </button>

          <transition name="dropdown-anim">
            <div v-if="showChat" class="common-dropdown chat-width">
              <div class="dropdown-header">
                <h3>Tin nhắn</h3>
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
                <a href="#">Xem tất cả trong Messenger</a>
              </div>
            </div>
          </transition>
        </div>
        
        <div class="action-wrapper" @click.stop>
          <button class="btn-icon" :class="{ active: showNoti }" title="Thông báo" @click="toggleNoti">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
            <span v-if="unreadNotiCount > 0" class="badge-count">{{ unreadNotiCount }}</span>
          </button>

          <transition name="dropdown-anim">
            <div v-if="showNoti" class="common-dropdown noti-width">
              <div class="dropdown-header">
                <h3>Thông báo</h3>
                <span class="action-link" @click="markAllRead">Đánh dấu đã đọc</span>
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
                <a href="#">Xem tất cả thông báo</a>
              </div>
            </div>
          </transition>
        </div>
      </div>
      
      <div class="divider-vertical"></div>

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
                Trang quản trị
              </li>
              <li class="divider"></li>
              <li @click="goToProfile">
                <span class="icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg></span>
                Trang cá nhân
              </li>
              <li @click="openPremium" class="highlight">
                <span class="icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg></span>
                Nâng cấp VIP
              </li>
              <li class="divider"></li>
              <li @click="openBugModal">
                <span class="icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg></span>
                Báo lỗi hệ thống
              </li>
              <li @click="handleLogout" class="danger">
                <span class="icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg></span>
                Đăng xuất
              </li>
            </ul>
          </div>
        </transition>
      </div>

      <div v-else class="guest-actions">
        <button class="btn-login" @click="$emit('open-login')">Đăng nhập</button>
        <button class="btn-signup" @click="$emit('open-register')">Đăng ký</button>
      </div>
      
      <div v-if="isDropdownOpen || showNoti || showChat || showShopping" class="click-outside-header" @click="closeAllDropdowns"></div>
    </div>

    <Teleport to="body">
      <transition name="fade">
        <div v-if="showBugReport" class="modal-backdrop" @click.self="showBugReport = false">
          <div class="bug-modal-content">
            <div class="modal-head">
              <h3>🐞 Báo lỗi hệ thống</h3>
              <button class="btn-close" @click="showBugReport = false">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
              </button>
            </div>
            <div class="modal-body-form">
              <div class="form-group">
                <label>Loại vấn đề</label>
                <select v-model="bugForm.type" class="form-select">
                  <option value="ui">Giao diện bị lỗi</option>
                  <option value="func">Chức năng không hoạt động</option>
                  <option value="other">Góp ý cải thiện</option>
                </select>
              </div>
              <div class="form-group">
                <label>Mô tả chi tiết</label>
                <textarea v-model="bugForm.desc" class="form-textarea" placeholder="Mô tả lỗi bạn gặp phải..."></textarea>
              </div>
            </div>
            <div class="modal-foot">
              <button class="btn-cancel" @click="showBugReport = false">Hủy</button>
              <button class="btn-submit" @click="submitBug">Gửi báo cáo</button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'

const emit = defineEmits(['open-premium', 'open-login', 'open-register'])
const authStore = useAuthStore()
const chatStore = useChatStore()
const router = useRouter()

// State
const showShopping = ref(false)
const showChat = ref(false)
const showNoti = ref(false)
const isDropdownOpen = ref(false)
const isSearchFocused = ref(false)
const searchKeyword = ref('')
const isScrolled = ref(false)
const showBugReport = ref(false)
const bugForm = ref({ type: 'ui', desc: '' })

// Mock Data
const shoppingItems = ref([
  { name: 'Ba chỉ bò Mỹ', quantity: '500g', checked: false },
  { name: 'Nấm kim châm', quantity: '2 gói', checked: true },
])
const conversations = ref([
  { id: 1, name: 'Bếp Trưởng Gomet', avatar: 'https://i.pravatar.cc/150?u=chef', lastMessage: 'Công thức này tuyệt lắm!', time: '5p', read: false, online: true },
  { id: 2, name: 'Hội Yêu Bếp', avatar: 'https://ui-avatars.com/api/?name=H&background=random', lastMessage: 'Mai: Cảm ơn nhé', time: '1h', read: true, online: false },
])
const notifications = ref([
  { id: 1, user: 'Minh Tú', avatar: 'https://i.pravatar.cc/150?u=tu', action: 'đã bình luận bài viết của bạn', time: '20p', isRead: false, type: 'comment', targetId: 1 },
  { id: 2, user: 'Hoàng Anh', avatar: 'https://i.pravatar.cc/150?u=ha', action: 'đã thích bài viết của bạn', time: '1h', isRead: true, type: 'like', targetId: 1 },
])

const unreadMessages = computed(() => conversations.value.filter(c => !c.read).length)
const unreadNotiCount = computed(() => notifications.value.filter(n => !n.isRead).length)

// Actions
const closeAllDropdowns = () => { isDropdownOpen.value = false; showChat.value = false; showNoti.value = false; showShopping.value = false }
const toggleShopping = () => { const s = !showShopping.value; closeAllDropdowns(); showShopping.value = s }
const toggleChat = () => { const s = !showChat.value; closeAllDropdowns(); showChat.value = s }
const toggleNoti = () => { const s = !showNoti.value; closeAllDropdowns(); showNoti.value = s }
const toggleDropdown = () => { const s = !isDropdownOpen.value; closeAllDropdowns(); isDropdownOpen.value = s }

const clearShoppingList = () => shoppingItems.value = []
const openBugModal = () => { closeAllDropdowns(); showBugReport.value = true }
const submitBug = () => { alert('Đã gửi báo lỗi thành công!'); showBugReport.value = false; bugForm.value.desc = '' }

// === LOGIC MỞ CHAT & BÀI VIẾT ===
const openMiniChat = (user) => { 
  chatStore.openChat(user)
  showChat.value = false 
}
const handleNotiClick = (noti) => {
  noti.isRead = true
  showNoti.value = false
  // Chuyển hướng đến bài viết chi tiết
  router.push({ name: 'PostDetail', params: { id: noti.targetId || 1 } })
}

// [MỚI] Hàm chuyển hướng sang Admin
const goToAdmin = () => {
  closeAllDropdowns()
  router.push('/admin/dashboard') 
}

const handleLogout = async () => { closeAllDropdowns(); localStorage.removeItem('user'); localStorage.removeItem('token'); authStore.user = null; authStore.isAuthenticated = false; await router.push('/') }
const handleCreatePost = () => authStore.isAuthenticated ? router.push('/create-post') : emit('open-login')
const handleSearch = () => { if (searchKeyword.value.trim()) router.push({ name: 'Search', query: { q: searchKeyword.value } }) }
const goToProfile = () => { closeAllDropdowns(); router.push('/profile') }
const openPremium = () => { closeAllDropdowns(); emit('open-premium') }
const markAllRead = () => { notifications.value.forEach(n => n.isRead = true) }

const handleScroll = () => { isScrolled.value = window.scrollY > 10 }
onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;500;600;700;800&display=swap');

/* --- HEADER CONTAINER --- */
.content-header {
  flex-shrink: 0; height: 72px; padding: 0 32px; display: flex; 
  align-items: center; justify-content: space-between;
  background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(20px);
  position: sticky; top: 0; z-index: 900; 
  border-bottom: 1px solid rgba(0,0,0,0.05);
  font-family: 'Mulish', sans-serif; transition: 0.3s;
}
.content-header.is-scrolled { box-shadow: 0 4px 20px rgba(0,0,0,0.05); }

/* --- SEARCH --- */
.search-wrapper-centered { flex: 1; display: flex; justify-content: center; margin: 0 20px; }
.search-box-vip {
  display: flex; align-items: center; width: 100%; max-width: 480px;
  background: #F3F4F6; border-radius: 50px; padding: 6px 6px 6px 20px;
  border: 1px solid transparent; transition: 0.2s;
}
.search-box-vip.is-focused { background: white; border-color: #EA580C; box-shadow: 0 4px 12px rgba(234, 88, 12, 0.1); }
.search-icon { color: #9CA3AF; margin-right: 10px; display: flex; }
.search-box-vip input { border: none; background: transparent; flex: 1; outline: none; font-size: 0.9rem; font-weight: 600; color: #1C1917; }
/* Nút tìm kiếm hiện hình */
.btn-search-submit { 
  padding: 8px 20px; border-radius: 30px; font-weight: 700; cursor: pointer; 
  background: #1C1917; color: white; border: none; font-size: 0.8rem; margin-left: 8px;
  transition: 0.2s;
}
.btn-search-submit:hover { background: #EA580C; }

/* --- RIGHT ACTIONS --- */
.header-right { display: flex; align-items: center; gap: 16px; }
.btn-create-post {
  background: #1C1917; color: white; border: none; padding: 9px 20px;
  border-radius: 30px; font-weight: 700; cursor: pointer;
  display: flex; align-items: center; gap: 8px; font-size: 0.85rem;
  transition: 0.2s; box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}
.btn-create-post:hover { background: #EA580C; transform: translateY(-1px); }

.icon-actions { display: flex; gap: 8px; position: relative; }
.action-wrapper { position: relative; }

.btn-icon {
  width: 42px; height: 42px; border-radius: 50%; border: none;
  background: transparent; color: #57534E;
  display: flex; align-items: center; justify-content: center; cursor: pointer;
  transition: 0.2s; position: relative;
}
.btn-icon:hover { background: #F3F4F6; color: #1C1917; }
.btn-icon.active { background: #FFF7ED; color: #EA580C; }

.badge-count {
  position: absolute; top: 0px; right: 0px;
  background: #EF4444; color: white; font-size: 0.6rem; font-weight: 800;
  min-width: 16px; height: 16px; border-radius: 50%; padding: 0 4px;
  display: flex; align-items: center; justify-content: center;
  border: 2px solid white;
}
.badge-dot {
  position: absolute; top: 8px; right: 8px; width: 8px; height: 8px;
  background: #22C55E; border-radius: 50%; border: 1px solid white;
}

/* --- COMMON DROPDOWN --- */
.common-dropdown {
  position: absolute; top: 55px; right: -80px; 
  background: white; border-radius: 16px;
  box-shadow: 0 10px 40px -5px rgba(0,0,0,0.15);
  border: 1px solid #F3F4F6; width: 340px; overflow: hidden;
  z-index: 1000; transform-origin: top right;
  display: flex; flex-direction: column; max-height: 80vh;
}
.shopping-width { right: -20px; width: 300px; }
.chat-width, .noti-width { right: -50px; width: 360px; }

.dropdown-header {
  padding: 15px 20px; border-bottom: 1px solid #F3F4F6;
  display: flex; justify-content: space-between; align-items: center;
}
.dropdown-header h3 { margin: 0; font-size: 1rem; font-weight: 800; color: #1C1917; }
.action-link, .action-icon { font-size: 0.8rem; color: #6B7280; font-weight: 600; cursor: pointer; }
.action-link:hover { color: #EA580C; text-decoration: underline; }

.scroll-body { overflow-y: auto; flex: 1; padding: 5px 0; }

/* List Item Styles */
.list-item {
  display: flex; gap: 12px; padding: 12px 20px; cursor: pointer; 
  transition: 0.2s; position: relative; border-bottom: 1px solid #F9FAFB;
}
.list-item:hover { background: #FAFAF9; }
.list-item.unread { background: #FFF7ED; }

.avatar-wrap { position: relative; width: 48px; height: 48px; flex-shrink: 0; }
.avatar-wrap img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; }
.online-status { position: absolute; bottom: 2px; right: 2px; width: 10px; height: 10px; background: #22C55E; border: 2px solid white; border-radius: 50%; }

.noti-type-icon {
  position: absolute; bottom: -2px; right: -2px; width: 20px; height: 20px;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  border: 2px solid white;
}
.noti-type-icon.like { background: #EF4444; }
.noti-type-icon.comment { background: #3B82F6; }

.item-info { flex: 1; overflow: hidden; display: flex; flex-direction: column; justify-content: center; }
.top-line { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
.name { font-weight: 700; color: #1C1917; font-size: 0.95rem; }
.time { font-size: 0.75rem; color: #9CA3AF; }
.preview, .noti-text { font-size: 0.85rem; color: #6B7280; line-height: 1.4; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.preview.unread { font-weight: 700; color: #1C1917; }
.noti-text { white-space: normal; }
.noti-text b { color: #1C1917; }

.unread-dot { width: 8px; height: 8px; background: #EA580C; border-radius: 50%; align-self: center; }

.dropdown-footer { padding: 12px; text-align: center; border-top: 1px solid #F3F4F6; }
.dropdown-footer a { text-decoration: none; color: #0084FF; font-weight: 700; font-size: 0.9rem; }

/* Shopping Items */
.shop-item { display: flex; align-items: center; gap: 12px; padding: 12px 20px; cursor: pointer; transition: 0.2s; }
.shop-item:hover { background: #FAFAF9; }
.shop-item.checked .item-text { opacity: 0.5; text-decoration: line-through; }
.checkbox-circle { width: 18px; height: 18px; border: 2px solid #D6D3D1; border-radius: 4px; display: flex; align-items: center; justify-content: center; }
.shop-item.checked .checkbox-circle { background: #22C55E; border-color: #22C55E; color: white; }
.item-text { display: flex; flex-direction: column; }
.item-text .name { font-weight: 700; font-size: 0.9rem; }
.item-text .qty { font-size: 0.8rem; color: #EA580C; }

/* --- USER MENU --- */
.user-menu-container { margin-left: 10px; position: relative; }
.avatar-trigger {
  width: 44px; height: 44px; border-radius: 50%; cursor: pointer;
  border: 2px solid transparent; transition: 0.2s;
}
.avatar-trigger:hover { border-color: #EA580C; transform: scale(1.05); }
.user-avt { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; }

.user-dropdown {
  position: absolute; top: 60px; right: 0; width: 260px;
  background: white; border-radius: 16px; 
  box-shadow: 0 10px 40px -10px rgba(0,0,0,0.15);
  border: 1px solid #F3F4F6; overflow: hidden; z-index: 1000;
}
.user-header {
  padding: 20px; border-bottom: 1px solid #F3F4F6;
  display: flex; align-items: center; gap: 15px; background: #FAFAF9;
}
.header-avt { width: 50px; height: 50px; border-radius: 50%; object-fit: cover; border: 2px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.header-info .name { font-weight: 800; color: #1C1917; font-size: 1rem; line-height: 1.2; }
.header-info .handle { font-size: 0.85rem; color: #6B7280; }

.menu-list { list-style: none; padding: 8px; margin: 0; }
.menu-list li {
  display: flex; align-items: center; gap: 12px; padding: 12px 20px;
  color: #57534E; font-weight: 600; font-size: 0.9rem;
  cursor: pointer; transition: 0.2s; border-radius: 8px; margin: 0 4px;
}
.menu-list li:hover { background: #F3F4F6; color: #1C1917; }
.menu-list li .icon { color: #9CA3AF; display: flex; }
.menu-list li:hover .icon { color: #1C1917; }
.menu-list li.highlight { color: #D97706; }
.menu-list li.highlight .icon { color: #F59E0B; }
.menu-list li.danger:hover { background: #FEF2F2; color: #EF4444; }
.menu-list li.danger:hover .icon { color: #EF4444; }
.divider { height: 1px; background: #F3F4F6; margin: 4px 10px; }

/* [MỚI] Style riêng cho nút Admin để nổi bật hơn */
.admin-link {
  color: #1C1917 !important; 
  font-weight: 800 !important; /* Chữ đậm hơn */
}
.admin-link .icon {
  color: #2563EB !important; /* Icon màu xanh dương (Blue) */
}
.admin-link:hover {
  background: #EFF6FF !important; /* Nền xanh nhạt khi hover */
}

/* Guest Actions */
.guest-actions { display: flex; gap: 10px; }
.btn-login { padding: 8px 18px; border-radius: 28px; font-weight: 700; border: 1.5px solid #E5E5E5; background: white; color: #1C1917; font-size: 0.85rem; cursor: pointer; }
.btn-signup { padding: 8px 18px; border-radius: 28px; font-weight: 700; background: #EA580C; color: white; border: none; font-size: 0.85rem; cursor: pointer; }

/* Common */
.empty-state { padding: 30px; text-align: center; color: #9CA3AF; font-size: 0.9rem; }
.click-outside-header { position: fixed; inset: 0; z-index: 899; }
.dropdown-anim-enter-active, .dropdown-anim-leave-active { transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1); }
.dropdown-anim-enter-from, .dropdown-anim-leave-to { opacity: 0; transform: translateY(10px) scale(0.98); }
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* BUG MODAL */
.modal-backdrop {
  position: fixed; inset: 0; z-index: 9999;
  background: rgba(0,0,0,0.5); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
}
.bug-modal-content {
  background: white; width: 500px; border-radius: 20px;
  box-shadow: 0 20px 60px -10px rgba(0,0,0,0.2); overflow: hidden;
}
.modal-head {
  padding: 20px 24px; border-bottom: 1px solid #F3F4F6;
  display: flex; justify-content: space-between; align-items: center;
}
.modal-head h3 { margin: 0; font-size: 1.2rem; font-weight: 800; color: #1C1917; }
.btn-close { background: none; border: none; cursor: pointer; color: #9CA3AF; transition: 0.2s; }
.btn-close:hover { color: #EF4444; transform: rotate(90deg); }

.modal-body-form { padding: 24px; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 700; font-size: 0.9rem; color: #44403C; }
.form-select, .form-textarea {
  width: 100%; padding: 12px; border-radius: 12px; border: 1px solid #E5E5E5;
  font-family: inherit; font-size: 0.95rem; outline: none; transition: 0.2s;
}
.form-select:focus, .form-textarea:focus { border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234, 88, 12, 0.1); }
.form-textarea { min-height: 120px; resize: vertical; }

.modal-foot {
  padding: 20px 24px; background: #FAFAF9; border-top: 1px solid #F3F4F6;
  display: flex; justify-content: flex-end; gap: 12px;
}
.btn-cancel { padding: 10px 20px; border-radius: 8px; border: 1px solid #E5E5E5; background: white; font-weight: 700; color: #57534E; cursor: pointer; }
.btn-submit { padding: 10px 24px; border-radius: 8px; border: none; background: #EA580C; color: white; font-weight: 700; cursor: pointer; box-shadow: 0 4px 10px rgba(234, 88, 12, 0.2); }
.btn-submit:hover { background: #C2410C; }

@media (max-width: 768px) {
  .content-header { padding: 0 15px; }
  .search-wrapper-centered { display: none; }
  .btn-create-post span svg { display: none; }
  .common-dropdown { right: -50px; width: 300px; }
  .bug-modal-content { width: 90%; }
}
</style>