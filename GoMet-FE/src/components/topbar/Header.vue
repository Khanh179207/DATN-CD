<template>
  <header class="content-header">
    <div class="search-wrapper">
      <div class="search-input-group">
        <span class="search-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
        </span>
        <input 
          type="text" 
          v-model="searchKeyword"
          placeholder="Tìm kiếm công thức, nguyên liệu..." 
          @keyup.enter="handleSearch"
        />
      </div>
    </div>

    <div class="header-right">
      <button class="btn-create-post" @click="handleCreatePost">
        <span class="plus-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
        </span> 
        Viết bài
      </button>

      <div class="icon-actions">
        <button class="btn-icon" title="Tin nhắn">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
        </button>
        <button class="btn-icon" title="Thông báo">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
          <span v-if="authStore.isAuthenticated" class="badge-count">5</span>
        </button>
      </div>
      
      <div class="divider-vertical"></div>

      <div v-if="authStore.isAuthenticated" class="user-menu-container">
        <div class="avatar-wrapper" @click.stop="toggleDropdown">
          <img :src="authStore.user.avatar || 'https://i.pravatar.cc/300'" class="trigger-avatar">
        </div>
        
        <transition name="dropdown-anim">
          <div v-if="isDropdownOpen" class="profile-dropdown" @click.stop>
            <div class="dropdown-header">
              <img :src="authStore.user.avatar || 'https://i.pravatar.cc/300'" class="avatar-large">
              <div class="user-info">
                <div class="d-name">{{ authStore.user.name }}</div>
                <div class="d-handle">@cook_{{ authStore.user.id }}</div>
              </div>
            </div>
            <ul class="dropdown-list">
              <li @click="goToProfile">
                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                Bếp cá nhân
              </li>
              <li @click="openPremium">
                <svg class="icon gold" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"></path></svg>
                Nâng cấp VIP
              </li>
              <li class="divider-line"></li>
              <li class="logout-item" @click="handleLogout">
                <svg class="icon red" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
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
      
      <div v-if="isDropdownOpen" class="click-outside-header" @click="isDropdownOpen = false"></div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const emit = defineEmits(['open-premium', 'open-login', 'open-register'])
const authStore = useAuthStore()
const router = useRouter()

const isDropdownOpen = ref(false)
const searchKeyword = ref('')

const toggleDropdown = () => isDropdownOpen.value = !isDropdownOpen.value

const goToProfile = () => { 
  isDropdownOpen.value = false
  router.push('/profile') 
}

const openPremium = () => {
  isDropdownOpen.value = false
  emit('open-premium')
}

const handleLogout = () => { 
  isDropdownOpen.value = false
  if(confirm('Bạn có chắc muốn đăng xuất?')) authStore.logout() 
}

const handleCreatePost = () => {
  if (authStore.isAuthenticated) router.push('/create-post')
  else emit('open-login')
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ name: 'Search', query: { q: searchKeyword.value } })
  }
}
</script>

<style scoped>
/* CSS giành riêng cho Header */
.content-header {
  flex-shrink: 0; height: 80px; padding: 0 40px; display: flex; 
  align-items: center; justify-content: space-between;
  background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(12px);
  position: sticky; top: 0; z-index: 900; border-bottom: 1px solid rgba(0,0,0,0.03);
}

.search-wrapper { flex: 1; max-width: 450px; margin-right: 30px; }
.search-input-group { 
  display: flex; align-items: center; background: white; border: 1px solid #E7E5E4; 
  border-radius: 30px; padding: 10px 18px; transition: all 0.3s ease; box-shadow: 0 2px 10px rgba(0,0,0,0.02);
}
.search-input-group:focus-within { border-color: #F97316; box-shadow: 0 4px 15px rgba(249, 115, 22, 0.1); transform: translateY(-1px); }
.search-icon { color: #A8A29E; margin-right: 10px; display: flex; }
.search-input-group input { border: none; background: transparent; width: 100%; outline: none; font-size: 0.95rem; color: #44403C; font-weight: 600; }

.header-right { display: flex; align-items: center; gap: 20px; }
.btn-create-post { background: #1C1917; color: white; border: none; padding: 10px 24px; border-radius: 30px; font-weight: 700; cursor: pointer; display: flex; align-items: center; gap: 8px; font-size: 0.9rem; transition: all 0.3s ease; }
.btn-create-post:hover { background: #F97316; transform: translateY(-2px); box-shadow: 0 8px 20px rgba(249, 115, 22, 0.25); }

.icon-actions { display: flex; gap: 10px; }
.btn-icon { width: 44px; height: 44px; border-radius: 50%; border: 1px solid transparent; background: white; color: #57534E; display: flex; align-items: center; justify-content: center; cursor: pointer; position: relative; transition: 0.3s; box-shadow: 0 2px 8px rgba(0,0,0,0.03); }
.btn-icon:hover { color: #F97316; background: #FFF7ED; }
.badge-count { position: absolute; top: -2px; right: -2px; color: white; font-size: 0.65rem; font-weight: 700; width: 18px; height: 18px; border-radius: 50%; display: flex; align-items: center; justify-content: center; border: 2px solid white; background: #EF4444; }

.divider-vertical { width: 1px; height: 24px; background: #E5E5E5; }
.guest-actions { display: flex; gap: 12px; }
.btn-login { padding: 10px 24px; border-radius: 30px; font-weight: 700; cursor: pointer; border: 1px solid #E5E5E5; background: white; transition: 0.2s; }
.btn-signup { padding: 10px 24px; border-radius: 30px; font-weight: 700; cursor: pointer; background: #F97316; color: white; border: none; transition: 0.2s; }

.user-menu-container { position: relative; z-index: 1001; }
.trigger-avatar { width: 42px; height: 42px; border-radius: 50%; object-fit: cover; cursor: pointer; border: 2px solid white; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.profile-dropdown { position: absolute; top: 55px; right: 0; width: 280px; background: white; border-radius: 20px; box-shadow: 0 20px 50px -10px rgba(0,0,0,0.15); border: 1px solid #F3F4F6; overflow: hidden; }
.dropdown-header { display: flex; align-items: center; gap: 15px; padding: 20px; background: #FAFAF9; }
.avatar-large { width: 50px; height: 50px; border-radius: 50%; object-fit: cover; }
.d-name { font-weight: 700; font-size: 1rem; }
.d-handle { font-size: 0.85rem; color: #78716C; }

.dropdown-list { list-style: none; padding: 10px; margin: 0; }
.dropdown-list li { display: flex; align-items: center; gap: 12px; padding: 12px 18px; border-radius: 12px; color: #57534E; font-weight: 600; cursor: pointer; transition: 0.2s; }
.dropdown-list li:hover { background: #FFF7ED; color: #F97316; transform: translateX(5px); }
.dropdown-list .icon { width: 20px; height: 20px; stroke-width: 2px; }
.dropdown-list .icon.gold { color: #F59E0B; }
.dropdown-list .icon.red { color: #EF4444; }
.divider-line { height: 1px; background: #F3F4F6; margin: 5px 15px; }

.click-outside-header { position: fixed; inset: 0; z-index: 1000; }

.dropdown-anim-enter-active, .dropdown-anim-leave-active { transition: all 0.2s ease-out; }
.dropdown-anim-enter-from, .dropdown-anim-leave-to { opacity: 0; transform: translateY(10px); }
</style>