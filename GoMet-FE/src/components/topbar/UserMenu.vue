<template>
  <div class="user-menu-wrapper" ref="menuWrapper">
    <div 
      class="nav-avatar-circle-gsap" 
      :class="{ 'is-vip': isPremiumUser, 'is-admin': isAdminUser }"
      @mouseenter="playHover"
      @mouseleave="reverseHover"
      @click.stop="handleToggle"
    >
      <div class="avatar-container" ref="avatarCircle">
        <div v-if="isAdminUser" class="badge-dot-admin"></div>
        <img :src="displayAvatar" @error="handleAvatarError" class="user-photo">
      </div>
    </div>

    <div v-show="isOpen && authStore.user" class="luxury-compact-dropdown" ref="dropdownPanel">
      
      <div class="user-header-luxury" :class="{ 'header-premium': isPremiumUser, 'header-admin': isAdminUser }">
        <div class="header-visual-effect"></div> 
        <div class="header-info-content">
          <div class="header-avt-mini-wrap" :class="{ 'vip-border': isPremiumUser }">
            <img :src="displayAvatar" class="header-avt-mini">
            <div v-if="isAdminUser" class="admin-label-mini">ADMIN</div>
          </div>

          <div class="header-text-data">
            <div class="user-name-luxury">
              <span class="name-truncate">{{ authStore.user?.name || authStore.user?.username }}</span>
              <svg v-if="isPremiumUser" class="vip-verify" viewBox="0 0 24 24" fill="#FCD34D">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14l-5-4.87 6.91-1.01L12 2z"/>
              </svg>
            </div>
            <div class="user-email-pro">{{ authStore.user?.email || 'Chưa cập nhật email' }}</div>
            <div class="user-id-badge">ID: {{ authStore.user?.id || authStore.user?.accountID }}</div>
            
            <div v-if="!isPremiumUser && !isAdminUser" class="daily-view-limit-info">
              <span class="limit-label">Lượt xem hôm nay:</span>
              <span class="limit-count" :class="{ 'text-danger': remainingViews === 0 }">{{ remainingViews }}/{{ maxViews }}</span>
              
              <div class="demo-actions">
                <button @click.stop="fetchViewLimits" class="btn-refresh-views" title="Làm mới">
                  <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="23 4 23 10 17 10"></polyline>
                    <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"></path>
                  </svg>
                </button>
                <button @click.stop="handleResetDemo" class="btn-reset-demo" title="Reset Lượt Xem (Demo)">
                  <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"></path>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <ul class="luxury-menu-list">
        <li v-if="isAdminUser" @click="navigate('/admin/dashboard')" class="menu-item-gsap admin-text lock-desktop-only">
          <div class="icon-sq admin-bg">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/>
              <rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/>
            </svg>
          </div>
          <span>Trang quản trị</span>
        </li>

        <li @click="navigate('/profile')" class="menu-item-gsap">
          <div class="icon-sq">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <span>Trang cá nhân</span>
        </li>

        <li @click="emitAction('open-premium')" class="menu-item-gsap vip-text">
          <div class="icon-sq vip-bg">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
            </svg>
          </div>
          <span>Nâng cấp VIP</span>
        </li>

        <div class="menu-divider-pro"></div>

        <li @click="emitAction('open-support')" class="menu-item-gsap support-text">
          <div class="icon-sq">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <path d="M4.93 4.93l4.24 4.24"></path>
              <path d="M14.83 9.17l4.24-4.24"></path>
              <path d="M14.83 14.83l4.24 4.24"></path>
              <path d="M9.17 14.83l-4.24 4.24"></path>
              <circle cx="12" cy="12" r="4"></circle>
            </svg>
          </div>
          <span>Hỗ trợ & Góp ý</span>
        </li>
      </ul>

      <div class="menu-divider-pro"></div>

      <div class="luxury-footer">
        <button @click="handleLogout" class="btn-logout-gsap">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          Đăng xuất
        </button>
      </div>
    </div>

    <div v-if="isOpen" class="gsap-overlay" @click="handleToggle"></div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import api from '@/services/api'
import { usePostViewLimit } from '@/composables/usePostViewLimit'
import gsap from 'gsap'

const authStore = useAuthStore()
const router = useRouter()
const isOpen = ref(false)

// 🔥 KẾT HỢP: Lấy logic ngày lễ từ develop và logic đồng bộ của sếp
const { isHolidayEventActive, checkGlobalHolidayStatus } = usePostViewLimit()
const remainingViews = ref(3)
const maxViews = ref(3)

const dropdownPanel = ref(null)
const avatarCircle = ref(null)

const emit = defineEmits(['open-premium', 'open-support', 'switch-account'])

const fetchViewLimits = async () => {
  if (authStore.user?.accountID) {
    try {
      // 🔥 ĐỒNG BỘ LUÔN POINT VÀ PREMIUM TỪ SERVER (Tránh cache localStorage khi sếp sửa SQL)
      await authStore.refreshProfile();

      const res = await api.get(`/api/users/${authStore.user.accountID}/view-limits`);
      remainingViews.value = res.data.remainingViews;
      maxViews.value = res.data.maxViews;
    } catch(e) { }
  }
}

const handleResetDemo = async () => {
  if (authStore.user?.accountID) {
    try {
      await api.post(`/api/users/${authStore.user.accountID}/reset-today-views`);
      toast.success('Đã reset lượt xem hôm nay (Demo)');
      fetchViewLimits(); // Lấy lại số lượt xem mới sau khi reset
    } catch(e) {
      toast.error('Lỗi khi reset lượt xem');
    }
  }
}

onMounted(async () => {
  // Check trạng thái ngày lễ trước
  await checkGlobalHolidayStatus();
  
  if (authStore.user?.accountID) {
    fetchViewLimits();
  }
  // 🔥 LẮNG NGHE SỰ KIỆN CẬP NHẬT LƯỢT XEM TỪ CÁC COMPONENT KHÁC
  window.addEventListener('ui:view-limits-updated', fetchViewLimits);
})

onUnmounted(() => {
  window.removeEventListener('ui:view-limits-updated', fetchViewLimits);
})

const handleToggle = async () => {
  if (!isOpen.value) {
    isOpen.value = true
    await nextTick()
    gsap.fromTo(dropdownPanel.value, 
      { opacity: 0, y: 15, scale: 0.95, transformOrigin: 'top right' },
      { opacity: 1, y: 0, scale: 1, duration: 0.4, ease: 'back.out(1.7)' }
    )
    gsap.fromTo(".menu-item-gsap", 
      { opacity: 0, x: 15 },
      { opacity: 1, x: 0, stagger: 0.03, duration: 0.3, ease: 'power2.out', delay: 0.1 }
    )
  } else {
    gsap.to(dropdownPanel.value, {
      opacity: 0, y: 10, scale: 0.98, duration: 0.2, ease: 'power2.in',
      onComplete: () => { isOpen.value = false }
    })
  }
}

const playHover = () => { gsap.to(avatarCircle.value, { scale: 1.08, duration: 0.3, ease: 'power2.out' }) }
const reverseHover = () => { gsap.to(avatarCircle.value, { scale: 1, duration: 0.3 }) }

const isPremiumUser = computed(() => authStore.user && (String(authStore.user.isPremium) === "1" || authStore.user.role === 'premium'));
const isAdminUser = computed(() => authStore.user && (authStore.user.isAdmin === 1 || authStore.user.role === 'admin'));
const displayAvatar = computed(() => {
  const user = authStore.user;
  if (!user) return `https://ui-avatars.com/api/?name=G&background=EA580C&color=fff`;
  return user.avatar?.startsWith('http') ? user.avatar : `https://ui-avatars.com/api/?name=${encodeURIComponent(user.name || user.username)}&background=EA580C&color=fff&bold=true`;
});

const handleAvatarError = (e) => { e.target.src = `https://ui-avatars.com/api/?name=G&background=EA580C&color=fff&bold=true`; };

// 🔥 CẬP NHẬT NAVIGATE: Chặn Admin Panel trên Mobile/Tablet
const navigate = (path) => {
  isOpen.value = false;
  
  if (path.startsWith('/admin') && window.innerWidth < 1024) {
    toast.warn('Trang quản trị yêu cầu màn hình máy tính để thao tác tốt nhất sếp nhé! 🖥️');
    return;
  }
  
  router.push(path);
}

const emitAction = (event) => { isOpen.value = false; emit(event); }

const handleLogout = async () => {
  isOpen.value = false;
  localStorage.removeItem('user');
  localStorage.removeItem('token');
  sessionStorage.removeItem('just_logged_in'); 
  authStore.user = null;
  authStore.isAuthenticated = false;
  toast.success('Đăng xuất thành công. Hẹn gặp lại sếp nhé! 👋');
  const isMobileOrTablet = window.innerWidth < 1024;
  const redirectPath = isMobileOrTablet ? '/home' : '/';
  await router.push(redirectPath);
}
</script>

<style scoped lang="scss">
@use '../../assets/styles/variables' as *;

.user-menu-wrapper { position: relative; font-family: 'Inter', sans-serif; }

.nav-avatar-circle-gsap {
  cursor: pointer; padding: 3px; border-radius: 50%; position: relative; z-index: 100;
  .avatar-container {
    width: 38px; height: 38px; border-radius: 50%; border: 2px solid #e2e8f0;
    position: relative; background: #fff;
    .user-photo { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; }
  }
  &.is-vip .avatar-container { border-color: #F59E0B; box-shadow: 0 0 10px rgba(245, 158, 11, 0.2); }
  &.is-admin .avatar-container { border-color: #6366F1; box-shadow: 0 0 10px rgba(99, 102, 241, 0.2); }
}

.badge-dot-admin {
  position: absolute; bottom: -1px; right: -1px; width: 12px; height: 12px;
  background: #6366F1; border: 2px solid white; border-radius: 50%; z-index: 10;
}

.luxury-compact-dropdown {
  position: absolute; top: calc(100% + 12px); right: 0;
  width: 280px; max-width: calc(100vw - 32px); background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(15px); border-radius: 20px;
  border: 1px solid rgba(0,0,0,0.08); box-shadow: 0 25px 50px -12px rgba(0,0,0,0.18);
  z-index: 1000; overflow: hidden;
}

.user-header-luxury {
  position: relative; padding: 22px 18px; overflow: hidden; background: #f8fafc;
  .header-visual-effect { position: absolute; inset: 0; z-index: 1; pointer-events: none; }
  .header-info-content { position: relative; z-index: 5; display: flex; align-items: center; gap: 14px; }

  .header-avt-mini-wrap {
    position: relative; width: 48px; height: 48px; flex-shrink: 0;
    .header-avt-mini { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; border: 2px solid white; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
    &.vip-border { padding: 2px; background: linear-gradient(45deg, #f59e0b, #fff); border-radius: 50%; }
  }

  .admin-label-mini {
    position: absolute; bottom: -5px; left: 50%; transform: translateX(-50%);
    background: #6366f1; color: white; font-size: 7px; font-weight: 900;
    padding: 1px 5px; border-radius: 4px; border: 1px solid white;
  }

  .header-text-data {
    flex: 1;
    min-width: 0; 

    .user-name-luxury { 
      font-weight: 800; font-size: 15px; color: #0f172a; 
      display: flex; align-items: center; gap: 4px;
      .name-truncate { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
      .vip-verify { width: 14px; height: 14px; flex-shrink: 0; }
    }
    
    .user-email-pro { font-size: 11px; color: #64748b; margin-top: 1px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-weight: 500; }
    .user-id-badge { font-size: 9px; color: #94a3b8; font-weight: 700; margin-top: 4px; text-transform: uppercase; letter-spacing: 0.5px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    
    .daily-view-limit-info {
      margin-top: 8px; padding: 4px 8px; background: rgba(234, 88, 12, 0.1);
      border-radius: 6px; display: inline-flex; align-items: center; gap: 6px;
      border: 1px solid rgba(234, 88, 12, 0.2); max-width: 100%; overflow: hidden;
      .limit-label { font-size: 10px; color: #64748b; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
      .limit-count { font-size: 11px; color: #EA580C; font-weight: 800; white-space: nowrap; flex-shrink: 0; }
      .limit-count.text-pink { color: #db2777; font-size: 12px; }
      .demo-actions {
        display: flex;
        align-items: center;
        gap: 4px;
        margin-left: 4px;
      }

      .btn-refresh-views, .btn-reset-demo {
        background: none; border: none; padding: 2px;
        color: #94a3b8; cursor: pointer; display: flex; align-items: center;
        transition: color 0.2s, transform 0.2s;
        &:hover { color: #EA580C; transform: rotate(180deg); }
      }
      
      .btn-reset-demo {
        color: #ef4444; /* Màu đỏ cho nút demo reset */
        &:hover { color: #f59e0b; transform: scale(1.2) rotate(0deg); }
      }
    }
  }

  &.header-premium {
    background: linear-gradient(135deg, #78350f 0%, #f59e0b 100%);
    .header-visual-effect {
      background: radial-gradient(circle at 10% 10%, rgba(255, 255, 255, 0.2) 0%, transparent 50%);
      &::after { content: ""; position: absolute; inset: -100%; background: linear-gradient(45deg, transparent 45%, rgba(255, 255, 255, 0.2) 50%, transparent 55%); animation: shimmer-pro 8s infinite linear; }
    }
    .user-name-luxury, .user-email-pro { color: #ffffff !important; }
    .user-id-badge { color: rgba(255,255,255,0.6) !important; }
  }

  &.header-admin {
    background: #020617;
    .header-visual-effect { background-image: linear-gradient(rgba(99, 102, 241, 0.15) 1px, transparent 1px), linear-gradient(90deg, rgba(99, 102, 241, 0.15) 1px, transparent 1px); background-size: 12px 12px; }
    .user-name-luxury { color: #f8fafc !important; }
    .user-email-pro { color: #94a3b8 !important; }
    .user-id-badge { color: #4338ca !important; }
  }

  &.header-holiday {
    background: linear-gradient(135deg, #fdf2f8 0%, #fff1f2 100%);
    border-bottom: 2px solid #fbcfe8;
    .header-visual-effect { background-image: radial-gradient(#f9a8d4 1px, transparent 1px); background-size: 10px 10px; opacity: 0.3; }
    .user-name-luxury { color: #be185d !important; text-shadow: 0 0 10px rgba(219, 39, 119, 0.2); }
    .daily-view-limit-info { background: white; border-color: #fbcfe8; box-shadow: 0 4px 10px rgba(219, 39, 119, 0.1); }
  }
}

@keyframes shimmer-pro { 0% { transform: translate(-30%, -30%); } 100% { transform: translate(30%, 30%); } }

.luxury-menu-list {
  list-style: none; padding: 8px; margin: 0;
  .menu-item-gsap {
    display: flex; align-items: center; gap: 12px; padding: 10px 14px;
    border-radius: 12px; cursor: pointer; transition: all 0.2s ease;
    color: #475569; font-weight: 600; font-size: 13.5px;
    &:hover {
      background: #f1f5f9; color: #EA580C;
      .icon-sq { transform: scale(1.1); background: white; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
    }
    .icon-sq { width: 32px; height: 32px; border-radius: 10px; background: #f8fafc; border: 1px solid #e2e8f0; display: flex; align-items: center; justify-content: center; transition: 0.2s; svg { width: 18px; height: 18px; } }
    &.admin-text { color: #4338ca; &:hover { background: #eef2ff; } }
    &.vip-text { color: #92400e; &:hover { background: #fffbeb; } }
  }
}

.menu-divider-pro { height: 1px; background: rgba(0,0,0,0.04); margin: 4px 16px; }

.luxury-footer {
  padding: 10px 14px 16px;
  .btn-logout-gsap {
    width: 100%; padding: 12px; border: none; border-radius: 14px;
    background: transparent; color: #64748b; font-weight: 700; font-size: 13.5px;
    display: flex; align-items: center; justify-content: center; gap: 10px;
    cursor: pointer; transition: 0.2s; border: 1px solid #f1f5f9;
    svg { width: 18px !important; height: 18px !important; flex-shrink: 0; }
    &:hover { background: #fef2f2; color: #ef4444; border-color: #fee2e2; }
  }
}

.gsap-overlay { position: fixed; inset: 0; z-index: 90; }

/* =======================================================
   🔥 HỆ THỐNG RESPONSIVE (TỐI ƯU MỌI THIẾT BỊ)
   ======================================================= */

/* --- 1. Màn hình Tablet & Mobile (Dưới 1024px) --- */
@media (max-width: 1024px) {
  /* Khóa mục quản trị trên giao diện Mobile */
  .lock-desktop-only {
    display: none !important;
  }

  .luxury-compact-dropdown {
    width: 260px;
    right: -10px; /* Nhích nhẹ để tránh dính lề */
    top: calc(100% + 15px);
  }

  .user-header-luxury {
    padding: 18px 14px;
    .header-avt-mini-wrap { width: 42px; height: 42px; }
    .user-name-luxury { font-size: 14px; }
  }

  .luxury-menu-list .menu-item-gsap {
    padding: 12px; /* Tăng diện tích chạm (touch target) */
    font-size: 13px;
    .icon-sq { width: 30px; height: 30px; }
  }
}

/* --- 2. Màn hình Mobile nhỏ (Dưới 480px) --- */
@media (max-width: 480px) {
  .luxury-compact-dropdown {
    width: 240px;
    border-radius: 16px;
  }
  
  .user-header-luxury {
    .user-email-pro { font-size: 10px; }
    .daily-view-limit-info .limit-count { font-size: 10px; }
  }
}

/* --- DARK MODE THEME --- */
:deep(.theme-dark) {
  .luxury-compact-dropdown { background: #0f172a; border-color: rgba(255,255,255,0.1); }
  .user-header-luxury:not(.header-premium):not(.header-admin) { background: #1e293b; .user-name-luxury { color: #f1f5f9; } }
  .luxury-menu-list .menu-item-gsap {
    color: #94a3b8;
    &:hover { background: rgba(255,255,255,0.05); color: #fb923c; .icon-sq { background: #0f172a; border-color: #334155; } }
    .icon-sq { background: #1e293b; border-color: #334155; }
  }
  .menu-divider-pro { background: rgba(255,255,255,0.08); }
}
</style>