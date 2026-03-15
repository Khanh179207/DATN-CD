<template>
  <header class="header-admin-global" :class="{ 'is-scrolled': isScrolled }">
    
    <div class="h-left">
      <div class="breadcrumb-executive">
        <div class="root-node">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
        </div>
        <svg class="path-arrow" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M9 18l6-6-6-6"></path></svg>
        <span class="current-path">{{ route.name || 'Control Center' }}</span>
      </div>
    </div>

    <div class="h-right">
      
      <div class="action-wrapper" v-click-outside="closeNoti">
        <button class="btn-command" :class="{ active: showNoti }" @click="toggleNoti">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
          <div class="indicator-pulse">3</div>
        </button>

        <transition name="spring-panel">
          <div v-if="showNoti" class="dropdown-lux noti-panel">
            <div class="lux-h">
              <span class="lux-t">System Alerts</span>
              <button class="lux-link">Clear all</button>
            </div>
            <div class="lux-b custom-scroll">
              <div class="lux-card unread">
                <div class="c-avatar alert">!</div>
                <div class="c-content">
                  <p>Security Alert: New login from unknown IP</p>
                  <span class="c-time">Just now</span>
                </div>
              </div>
              <div class="lux-card">
                <div class="c-avatar success">✓</div>
                <div class="c-content">
                  <p>System Update: Version 2.5 is now live</p>
                  <span class="c-time">2 hours ago</span>
                </div>
              </div>
            </div>
            <div class="lux-f">
              <button @click="$router.push('/admin/notifications')">View Audit Log</button>
            </div>
          </div>
        </transition>
      </div>

      <div class="action-wrapper" v-click-outside="closeUser">
        <div class="user-capsule" @click="toggleUser" :class="{ active: showUser }">
          <div class="u-avatar-wrap">
             <img :src="adminAvatar" :alt="adminName">
             <div class="u-status"></div>
          </div>
          <div class="u-details">
            <span class="u-mail">{{ adminEmail }}</span>
            <span class="u-tag">Super Admin</span>
          </div>
          <svg class="u-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M6 9l6 6 6-6"></path></svg>
        </div>

        <transition name="spring-panel">
          <div v-if="showUser" class="dropdown-lux user-panel">
            <div class="user-hero">
               <div class="hero-avatar">
                 <img :src="adminAvatar" :alt="adminName">
               </div>
               <strong>{{ adminName }}</strong>
               <span>{{ adminEmail }}</span>
            </div>
            <div class="user-nav">
              <button @click="goToProfile"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg> Profile Account</button>
              <button @click="goToSettings"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg> Preferences</button>
              <div class="nav-sep"></div>
              <button @click="handleLogout" class="danger"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg> Sign Out System</button>
            </div>
          </div>
        </transition>
      </div>

    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route  = useRoute()
const router = useRouter()
const auth   = useAuthStore()

const showNoti = ref(false)
const showUser = ref(false)
const isScrolled = ref(false)

const handleScroll = () => { isScrolled.value = window.scrollY > 20 }
onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))

const adminName  = computed(() => auth.currentUser?.username || 'Gomet Master')
const adminEmail = computed(() => auth.currentUser?.email    || 'admin@gomet.system')
const adminAvatar = computed(() =>
  auth.currentUser?.avatar ||
  `https://ui-avatars.com/api/?name=${encodeURIComponent(adminName.value)}&background=0f172a&color=fb923c&size=128`
)

const toggleNoti = () => { showNoti.value = !showNoti.value; showUser.value = false }
const closeNoti  = () => { showNoti.value = false }
const toggleUser = () => { showUser.value = !showUser.value; showNoti.value = false }
const closeUser  = () => { showUser.value = false }

const handleLogout = () => { auth.logout() }
const goToProfile = () => { router.push('/profile'); closeUser() }
const goToSettings = () => { closeUser() }

const vClickOutside = {
  mounted(el, binding) {
    el.clickOutsideEvent = (event) => {
      if (!(el === event.target || el.contains(event.target))) binding.value(event, el)
    }
    document.body.addEventListener('click', el.clickOutsideEvent)
  },
  unmounted(el) { document.body.removeEventListener('click', el.clickOutsideEvent) }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

/* --- ✨ 1. HEADER: FROSTED GLASS MASTER --- */
.header-admin-global {
  height: 90px;
  padding: 0 48px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(25px) saturate(180%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  display: flex; align-items: center; justify-content: space-between;
  position: sticky; top: 0; z-index: 900;
  font-family: 'Inter', sans-serif;
  transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

.header-admin-global.is-scrolled {
  height: 68px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.04);
  padding: 0 40px;
}

/* --- 🧭 2. BREADCRUMB: EXECUTIVE PATH --- */
.breadcrumb-executive { display: flex; align-items: center; gap: 16px; }
.root-node {
  width: 40px; height: 40px; border-radius: 12px;
  background: rgba(234, 88, 12, 0.08); color: #ea580c;
  display: flex; align-items: center; justify-content: center;
  box-shadow: inset 0 0 0 1px rgba(234, 88, 12, 0.1);
}
.path-arrow { color: #cbd5e1; }
.current-path { color: #0f172a; font-weight: 800; font-size: 1.1rem; letter-spacing: -0.02em; }

/* --- ⚡ 3. ACTIONS HUB --- */
.h-right { display: flex; align-items: center; gap: 28px; }
.action-wrapper { position: relative; }

.btn-command {
  width: 46px; height: 46px; border: none; background: transparent;
  border-radius: 14px; color: #64748b;
  display: flex; align-items: center; justify-content: center; cursor: pointer;
  position: relative; transition: all 0.3s ease;
}
.btn-command:hover { background: #f1f5f9; color: #0f172a; transform: translateY(-1px); }
.btn-command.active { background: #0f172a; color: #fff; }

.indicator-pulse {
  position: absolute; top: 8px; right: 8px;
  background: #ea580c; color: white;
  font-size: 10px; font-weight: 900;
  width: 18px; height: 18px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  border: 3px solid #fff;
  box-shadow: 0 0 15px rgba(234, 88, 12, 0.4);
}

/* --- 💊 4. USER CAPSULE --- */
.user-capsule {
  display: flex; align-items: center; gap: 14px; cursor: pointer;
  padding: 6px 18px 6px 8px; border-radius: 100px;
  background: #ffffff; border: 1px solid #e2e8f0;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}
.user-capsule:hover, .user-capsule.active {
  border-color: #ea580c; background: #fff;
  box-shadow: 0 10px 25px -5px rgba(234, 88, 12, 0.12);
  transform: translateY(-1px);
}

.u-avatar-wrap { position: relative; width: 36px; height: 36px; }
.u-avatar-wrap img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; border: 2px solid #fff; box-shadow: 0 0 0 1px #e2e8f0; }
.u-status { position: absolute; bottom: 0; right: 0; width: 10px; height: 10px; background: #10b981; border: 2px solid #fff; border-radius: 50%; }

.u-details { display: flex; flex-direction: column; }
.u-mail { font-size: 13px; font-weight: 700; color: #1e293b; max-width: 160px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.u-tag { font-size: 9px; color: #ea580c; font-weight: 800; text-transform: uppercase; letter-spacing: 0.05em; margin-top: -1px; }
.u-icon { color: #94a3b8; transition: 0.3s; }
.active .u-icon { transform: rotate(180deg); color: #ea580c; }

/* --- 🏛️ 5. DROPDOWN LUX DESIGN --- */
.dropdown-lux {
  position: absolute; top: calc(100% + 15px); right: 0;
  background: white; width: 320px;
  border-radius: 20px; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.15);
  border: 1px solid rgba(0,0,0,0.04); overflow: hidden; z-index: 1000;
  transform-origin: top right;
}

.lux-h { padding: 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; }
.lux-t { font-weight: 800; color: #0f172a; font-size: 14px; text-transform: uppercase; letter-spacing: 0.05em; }
.lux-link { font-size: 11px; color: #ea580c; background: none; border: none; cursor: pointer; font-weight: 700; }

.lux-card { display: flex; gap: 14px; padding: 16px 20px; border-bottom: 1px solid #f8fafc; transition: 0.2s; cursor: pointer; }
.lux-card:hover { background: #f8fafc; }
.lux-card.unread { background: rgba(234, 88, 12, 0.03); }
.c-avatar { width: 34px; height: 34px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-weight: 900; }
.c-avatar.alert { background: #fee2e2; color: #ef4444; }
.c-avatar.success { background: #dcfce7; color: #22c55e; }

.c-content p { margin: 0; font-size: 13px; color: #334155; line-height: 1.5; font-weight: 500; }
.c-time { font-size: 11px; color: #94a3b8; margin-top: 4px; display: block; }

.lux-f button { width: 100%; padding: 16px; border: none; background: #f8fafc; color: #64748b; font-weight: 700; font-size: 12px; cursor: pointer; transition: 0.3s; }
.lux-f button:hover { background: #f1f5f9; color: #0f172a; }

/* USER HERO */
.user-hero { padding: 32px 20px; background: linear-gradient(180deg, #f8fafc 0%, #fff 100%); text-align: center; border-bottom: 1px solid #f1f5f9; }
.hero-avatar { width: 72px; height: 72px; border-radius: 24px; margin: 0 auto 16px; padding: 4px; background: linear-gradient(135deg, #fb923c, #ea580c); }
.hero-avatar img { width: 100%; height: 100%; border-radius: 20px; border: 4px solid #fff; object-fit: cover; }
.user-hero strong { display: block; font-size: 17px; color: #0f172a; letter-spacing: -0.01em; }
.user-hero span { font-size: 13px; color: #64748b; font-weight: 500; }

.user-nav { padding: 12px; }
.user-nav button {
  width: 100%; display: flex; align-items: center; gap: 12px;
  padding: 12px 16px; border: none; background: transparent;
  color: #475569; font-weight: 600; font-size: 14px; border-radius: 12px;
  cursor: pointer; transition: 0.2s;
}
.user-nav button:hover { background: #f1f5f9; color: #0f172a; transform: translateX(4px); }
.user-nav button.danger { color: #ef4444; }
.user-nav button.danger:hover { background: #fef2f2; }
.nav-sep { height: 1px; background: #f1f5f9; margin: 8px 12px; }

/* 🌪️ SPRING PHYSICS ANIMATION */
.spring-panel-enter-active {
  animation: spring-in 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.spring-panel-leave-active {
  animation: spring-in 0.25s cubic-bezier(0.16, 1, 0.3, 1) reverse;
}
@keyframes spring-in {
  0% { opacity: 0; transform: translateY(20px) scale(0.92); }
  100% { opacity: 1; transform: translateY(0) scale(1); }
}

/* CUSTOM SCROLL */
.custom-scroll::-webkit-scrollbar { width: 4px; }
.custom-scroll::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
</style>