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
            <div v-if="isAdminUser" class="admin-label-mini">{{ t('admin.posts.admin_badge') }}</div>
          </div>

          <div class="header-text-data">
            <div class="user-name-luxury">
              {{ authStore.user?.name || authStore.user?.username }}
              <svg v-if="isPremiumUser" class="vip-verify" viewBox="0 0 24 24" fill="#FCD34D">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14l-5-4.87 6.91-1.01L12 2z"/>
              </svg>
            </div>
            <div class="user-email-pro">{{ authStore.user?.email || t('header.email_not_updated') }}</div>
            <div class="user-id-badge">ID: {{ authStore.user?.id || authStore.user?.accountID }}</div>
            
            <!-- START: Daily View Limit -->
            <div v-if="!isPremiumUser && !isAdminUser" class="daily-view-limit-info">
              <span class="limit-label">{{ t('header.daily_views_today') }}</span>
              <span class="limit-count">{{ remainingViews }}/3</span>
              <!-- Nút Reset dành cho Demo -->
              <button @click.stop="authStore.resetViews" class="btn-reset-views-demo" :title="t('header.reset_demo')">
                <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M23 4v6h-6"></path><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"></path></svg>
              </button>
            </div>
            <!-- END: Daily View Limit -->
          </div>
        </div>
      </div>

      <ul class="luxury-menu-list">
        <li v-if="isAdminUser" @click="navigate('/admin/dashboard')" class="menu-item-gsap admin-text">
          <div class="icon-sq admin-bg">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/>
              <rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/>
            </svg>
          </div>
          <span>{{ t('header.admin_panel') }}</span>
        </li>

        <li @click="navigate('/profile')" class="menu-item-gsap">
          <div class="icon-sq">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <span>{{ t('header.my_profile') }}</span>
        </li>

        <li @click="emitAction('open-premium')" class="menu-item-gsap vip-text">
          <div class="icon-sq vip-bg">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
            </svg>
          </div>
          <span>{{ t('header.upgrade_vip') }}</span>
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
          <span>{{ $t('header.report_bug') }}</span>
        </li>
      </ul>

      <div class="menu-divider-pro"></div>

      <div class="luxury-footer">
        <button @click="handleLogout" class="btn-logout-gsap">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          {{ $t('common.logout') }}
        </button>
      </div>
    </div>

    <div v-if="isOpen" class="gsap-overlay" @click="handleToggle"></div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast' // 👈 Thêm import toast
import { useI18n } from 'vue-i18n'
/* START: Daily View Limit */
import { usePostViewLimit } from '@/composables/usePostViewLimit'
/* END: Daily View Limit */
import gsap from 'gsap'

const authStore = useAuthStore()
const router = useRouter()
const { t } = useI18n()
const isOpen = ref(false)

/* START: Daily View Limit */
const { remainingViews } = usePostViewLimit()
/* END: Daily View Limit */

const dropdownPanel = ref(null)
const avatarCircle = ref(null)

// --- EMITS ---
const emit = defineEmits(['open-premium', 'open-support', 'switch-account'])

// --- GSAP ANIMATIONS ---
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

// --- AUTH COMPUTED DATA ---
const isPremiumUser = computed(() => authStore.user && (String(authStore.user.isPremium) === "1" || authStore.user.role === 'premium'));
const isAdminUser = computed(() => authStore.user && (authStore.user.isAdmin === 1 || authStore.user.role === 'admin'));
const displayAvatar = computed(() => {
  const user = authStore.user;
  if (!user) return `https://ui-avatars.com/api/?name=G&background=EA580C&color=fff`;
  return user.avatar?.startsWith('http') ? user.avatar : `https://ui-avatars.com/api/?name=${encodeURIComponent(user.name || user.username)}&background=EA580C&color=fff&bold=true`;
});

// --- HELPER FUNCTIONS ---
const handleAvatarError = (e) => { e.target.src = `https://ui-avatars.com/api/?name=G&background=EA580C&color=fff&bold=true`; };
const navigate = (path) => { isOpen.value = false; router.push(path); }
const emitAction = (event) => { isOpen.value = false; emit(event); }

// --- 🔥 XỬ LÝ ĐĂNG XUẤT ELITE ---
const handleLogout = async () => {
  isOpen.value = false;
  
  // 1. Xóa dữ liệu cũ
  localStorage.removeItem('user');
  localStorage.removeItem('token');
  sessionStorage.removeItem('just_logged_in'); 
  
  // 2. Reset store
  authStore.user = null;
  authStore.isAuthenticated = false;

  // 3. Hiện thông báo "Chia tay" ngọt ngào
  toast.success(t('toast.logout_ok'));

  // 4. Đẩy ra Landing (Không nhắm vào Login, Land ở đầu trang)
  await router.push('/');
}
</script>

<style scoped lang="scss">
@use '../../assets/styles/variables' as *;

.user-menu-wrapper { position: relative; font-family: 'Inter', sans-serif; }

// --- Avatar TRIGGER Style ---
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

// --- DROPDOWN Luxury Panel ---
.luxury-compact-dropdown {
  position: absolute; top: calc(100% + 12px); right: 0;
  width: 260px; background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(15px); border-radius: 20px;
  border: 1px solid rgba(0,0,0,0.08); box-shadow: 0 25px 50px -12px rgba(0,0,0,0.18);
  z-index: 1000; overflow: hidden;
}

// --- Header Themes (Admin/VIP/Standard) ---
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
    .user-name-luxury { 
      font-weight: 800; font-size: 15px; color: #0f172a; 
      display: flex; align-items: center; gap: 4px;
      .vip-verify { width: 14px; height: 14px; }
    }
    .user-email-pro { font-size: 11px; color: #64748b; margin-top: 1px; word-break: break-all; font-weight: 500; }
    .user-id-badge { font-size: 9px; color: #94a3b8; font-weight: 700; margin-top: 4px; text-transform: uppercase; letter-spacing: 0.5px; }
    
    /* START: Daily View Limit Styles */
    .daily-view-limit-info {
      margin-top: 8px;
      padding: 4px 8px;
      background: rgba(234, 88, 12, 0.1);
      border-radius: 6px;
      display: inline-flex;
      align-items: center;
      gap: 6px;
      border: 1px solid rgba(234, 88, 12, 0.2);
      
      .limit-label { font-size: 10px; color: #64748b; font-weight: 600; }
      .limit-count { font-size: 11px; color: #EA580C; font-weight: 800; }
      
      .btn-reset-views-demo {
        background: none; border: none; padding: 2px; margin-left: 4px;
        color: #94a3b8; cursor: pointer; display: flex; align-items: center;
        transition: color 0.2s, transform 0.2s;
        &:hover { color: #EA580C; transform: rotate(30deg); }
      }
    }
    /* END: Daily View Limit Styles */
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
}

@keyframes shimmer-pro { 0% { transform: translate(-30%, -30%); } 100% { transform: translate(30%, 30%); } }

// --- List Items ---
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
    .icon-sq {
      width: 32px; height: 32px; border-radius: 10px; background: #f8fafc; border: 1px solid #e2e8f0;
      display: flex; align-items: center; justify-content: center;
      transition: 0.2s; svg { width: 18px; height: 18px; }
    }
    &.admin-text { color: #4338ca; &:hover { background: #eef2ff; } }
    &.vip-text { color: #92400e; &:hover { background: #fffbeb; } }
  }
}

.menu-divider-pro { height: 1px; background: rgba(0,0,0,0.04); margin: 4px 16px; }

// --- Footer Button ---
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

// --- DARK MODE THEME ---
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