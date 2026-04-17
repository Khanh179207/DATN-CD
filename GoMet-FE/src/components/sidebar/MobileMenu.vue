<template>
  <Teleport to="body">
    <transition name="fade">
      <div v-if="isOpen" class="mobile-menu-overlay" @click="closeMenu"></div>
    </transition>

    <aside class="mobile-drawer" :class="{ 'is-open': isOpen, 'theme-dark': route.meta?.isDark }">
      
      <div class="drawer-header">
        <div class="brand-wrapper">
          <img src="@/assets/images/gomet.jpg" alt="Logo" class="brand-logo" />
          <div class="brand-text">
            <span class="main">GOMET</span>
            <span class="sub">PREMIUM SYSTEM</span>
          </div>
        </div>
        <button class="btn-close" @click="closeMenu">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>
      </div>

      <div class="drawer-body">
        <nav class="mobile-nav">
          
          <div class="nav-group">
            <label class="group-tag">{{ $t('sidebar.group_journey') }}</label>
            <router-link to="/home" class="nav-link" active-class="is-active">
              <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/></svg></div>
              <span class="label">{{ $t('nav.home') }}</span>
            </router-link>
            <router-link to="/search" class="nav-link" active-class="is-active">
              <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg></div>
              <span class="label">{{ $t('nav.search') }}</span>
            </router-link>
          </div>

          <div class="nav-group premium-vibe">
            <label class="group-tag gold-txt">{{ $t('sidebar.group_premium') }}</label>
            <router-link to="/leaderboard" class="nav-link vip-glow" active-class="is-active">
              <div class="icon-box"><svg class="icon gold" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6M18 9h1.5a2.5 2.5 0 0 0 0-5H18M4 22h16M10 14.66V17c0 .55.47.98.97 1.21l1.03.46 1.03-.46c.5-.23.97-.66.97-1.21v-2.34M12 11.63a3.5 3.5 0 0 1 4.54-3.32"/></svg></div>
              <span class="label">{{ $t('nav.leaderboard') }}</span>
            </router-link>
            <router-link to="/suggestions" class="nav-link vip-glow" active-class="is-active">
              <div class="icon-box"><svg class="icon gold" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 3l1.9 5.8a2 2 0 0 0 1.3 1.3L21 12l-5.8 1.9a2 2 0 0 0-1.3 1.3L12 21l-1.9-5.8a2 2 0 0 0-1.3-1.3L3 12l5.8-1.9a2 2 0 0 0 1.3-1.3L12 3z"/></svg></div>
              <span class="label">{{ $t('sidebar.suggest_today') }}</span>
            </router-link>
            <router-link to="/meal-plan" class="nav-link vip-glow" active-class="is-active">
              <div class="icon-box"><svg class="icon gold" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18M9 16l2 2 4-4"/></svg></div>
              <span class="label">{{ $t('sidebar.meal_calendar') }}</span>
            </router-link>
          </div>

          <div class="nav-group">
            <label class="group-tag">{{ $t('sidebar.group_personal') }}</label>
            <router-link to="/events" class="nav-link" active-class="is-active">
              <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8 2v4M16 2v4M3 10h18M5 4h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z"/></svg></div>
              <span class="label">{{ $t('nav.events') }}</span>
            </router-link>
            <router-link to="/profile" class="nav-link" active-class="is-active">
              <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></div>
              <span class="label">{{ $t('sidebar.kitchen') }}</span>
            </router-link>
            <router-link to="/storage" class="nav-link" active-class="is-active">
              <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><path d="M3.27 6.96L12 12.01l8.73-5.05M12 22.08V12"/></svg></div>
              <span class="label">{{ $t('sidebar.storage_label') }}</span>
            </router-link>
          </div>

        </nav>
      </div>

      <div class="drawer-footer">
        <div class="premium-core-card">
          <div class="liquid-bg"></div>
          <div class="card-head">
            <span class="crown-badge">👑</span>
            <div class="text">
              <h3>GOMET CLUB</h3>
              <p>Sovereign Access</p>
            </div>
          </div>
          <button class="btn-sovereign-unlock" @click="handleOpenPremium">
            {{ $t('sidebar.unlock') }}
            <div class="flare-sweep"></div>
          </button>
        </div>
      </div>

    </aside>
  </Teleport>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'

const emit = defineEmits(['open-premium'])
const route = useRoute()
const isOpen = ref(false)

const openMenu = () => { isOpen.value = true; document.body.style.overflow = 'hidden'; }
const closeMenu = () => { isOpen.value = false; document.body.style.overflow = ''; }

const handleOpenPremium = () => {
  closeMenu();
  emit('open-premium');
}

// 🌟 Lắng nghe sự kiện từ Header (Nút Hamburger)
onMounted(() => {
  window.addEventListener('ui:toggle-sidebar', openMenu)
})

onUnmounted(() => {
  window.removeEventListener('ui:toggle-sidebar', openMenu)
})

// 🌟 Tự động đóng Menu khi bấm chuyển trang thành công
watch(route, () => {
  if (isOpen.value) closeMenu();
})
</script>

<style scoped>
/* ==================================================== */
/* LỚP PHỦ MỜ BÊN DƯỚI (OVERLAY) */
/* ==================================================== */
.mobile-menu-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  z-index: 9998;
}
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* ==================================================== */
/* KHUNG NGĂN KÉO (DRAWER MAIN) */
/* ==================================================== */
.mobile-drawer {
  position: fixed;
  top: 0;
  left: 0;
  width: 280px;
  height: 100vh;
  background: #FFFFFF;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  transform: translateX(-100%); /* Mặc định giấu đi */
  transition: transform 0.4s cubic-bezier(0.19, 1, 0.22, 1);
  box-shadow: 20px 0 50px rgba(0,0,0,0.1);
  font-family: 'Mulish', sans-serif;
}
.mobile-drawer.is-open {
  transform: translateX(0); /* Trượt ra khi mở */
}

/* --- HEADER DRAWER --- */
.drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-bottom: 1px solid rgba(0,0,0,0.05);
}
.brand-wrapper { display: flex; align-items: center; gap: 10px; }
.brand-logo { width: 34px; height: 34px; object-fit: cover; border-radius: 9px; }
.brand-text .main { display: block; font-family: 'Playfair Display', serif; font-weight: 900; font-size: 19px; color: #111; line-height: 1; }
.brand-text .sub { font-size: 7px; font-weight: 900; color: #EA580C; letter-spacing: 2px; }
.btn-close {
  background: #f3f4f6; border: none; width: 32px; height: 32px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; cursor: pointer; color: #4b5563;
}

/* --- BODY DRAWER (CHỨA MENU) --- */
.drawer-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px 15px;
}
.nav-group { margin-bottom: 25px; }
.group-tag { display: block; font-size: 0.65rem; font-weight: 900; color: #a1a1aa; letter-spacing: 2px; margin-bottom: 12px; padding-left: 12px; text-transform: uppercase; }
.gold-txt { color: #D4AF37 !important; }

.nav-link {
  display: flex; align-items: center; padding: 12px 14px; color: #64748b;
  text-decoration: none; border-radius: 12px;
  transition: all 0.3s ease; margin-bottom: 4px;
}
.nav-link:active { transform: scale(0.98); }
.nav-link.is-active {
  background: rgba(234, 88, 12, 0.1);
  color: #EA580C; font-weight: 800;
}
.icon-box { width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; }
.nav-link.is-active .icon { color: #EA580C; }
.label { margin-left: 14px; font-weight: 700; font-size: 0.95rem; }

/* Premium link */
.vip-glow.is-active { background: rgba(212,175,55,0.1) !important; color: #D4AF37 !important; }

/* --- FOOTER DRAWER (PREMIUM CARD) --- */
.drawer-footer { padding: 20px; border-top: 1px solid rgba(0,0,0,0.05); }
.premium-core-card {
  background: rgba(0,0,0,0.02);
  border-radius: 20px; padding: 20px;
  border: 1px solid rgba(212, 175, 55, 0.3); position: relative; overflow: hidden;
}
.card-head { display: flex; align-items: center; gap: 15px; margin-bottom: 15px; }
.crown-badge { font-size: 20px; filter: drop-shadow(0 0 8px gold); }
.text h3 { color: #D4AF37; font-family: 'Playfair Display', serif; font-size: 16px; margin: 0; font-weight: 900; }
.text p { color: #666; font-size: 10px; margin: 2px 0 0; font-weight: 700; }
.btn-sovereign-unlock {
  width: 100%; padding: 12px; border: none; border-radius: 12px;
  background: linear-gradient(135deg, #D4AF37 0%, #B8860B 100%);
  color: #000; font-weight: 900; font-size: 11px; letter-spacing: 1px; cursor: pointer;
}

/* ==================================================== */
/* 🔥 THEME DARK CHO MOBILE MENU */
/* ==================================================== */
.mobile-drawer.theme-dark {
  background: #11100F;
  border-right: 1px solid rgba(255,255,255,0.1);
}
.mobile-drawer.theme-dark .brand-text .main { color: #FFFFFF; }
.mobile-drawer.theme-dark .btn-close { background: rgba(255,255,255,0.1); color: #FFF; }
.mobile-drawer.theme-dark .nav-link { color: #9CA3AF; }
.mobile-drawer.theme-dark .nav-link.is-active { background: rgba(234,88,12,0.2); color: #EA580C; }
.mobile-drawer.theme-dark .premium-core-card { background: rgba(212,175,55,0.05); }
.mobile-drawer.theme-dark .text p { color: #9CA3AF; }
</style>