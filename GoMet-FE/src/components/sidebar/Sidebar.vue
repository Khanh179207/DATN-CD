<template>
  <aside class="gomet-sovereign-sidebar" :class="[{ 'is-collapsed': isCollapsed }, { 'theme-dark': route.meta?.isDark }]">
    <div class="sidebar-header">
      <div class="brand-wrapper anim-fade-in" v-if="!isCollapsed">
        <div class="logo-container-premium">
          <img src="@/assets/images/gomet.jpg" alt="Logo" class="brand-logo" />
        </div>
        <div class="brand-text">
          <span class="main">GOMET</span>
          <span class="sub">PREMIUM SYSTEM</span>
        </div>
      </div>
      <div class="brand-mini-trigger" v-else @click="toggleSidebar">
        <img src="@/assets/images/gomet.jpg" alt="G" class="logo-mini sovereign-pulse" />
      </div>
      <button v-if="!isCollapsed" class="minimal-toggle" @click="toggleSidebar">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M15 18l-6-6 6-6" /></svg>
      </button>
    </div>

    <div class="nav-container-vipro">
      <nav class="editorial-nav">
        <div class="nav-group animate-stagger" style="--d: 0.1s">
          <label class="group-tag" v-if="!isCollapsed">{{ $t('sidebar.group_journey') }}</label>
          <router-link to="/home" class="nav-link" active-class="is-active">
            <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/></svg></div>
            <span class="label">{{ $t('nav.home') }}</span>
            <span class="tooltip" v-if="isCollapsed">{{ $t('nav.home') }}</span>
          </router-link>
          <router-link to="/search" class="nav-link" active-class="is-active">
            <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg></div>
            <span class="label">{{ $t('nav.search') }}</span>
            <span class="tooltip" v-if="isCollapsed">{{ $t('nav.search') }}</span>
          </router-link>
        </div>

        <div class="nav-group premium-vibe animate-stagger" style="--d: 0.2s">
          <label class="group-tag gold-txt" v-if="!isCollapsed">{{ $t('sidebar.group_premium') }}</label>
          <router-link to="/leaderboard" class="nav-link vip-glow" active-class="is-active">
            <div class="icon-box"><svg class="icon gold" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6M18 9h1.5a2.5 2.5 0 0 0 0-5H18M4 22h16M10 14.66V17c0 .55.47.98.97 1.21l1.03.46 1.03-.46c.5-.23.97-.66.97-1.21v-2.34M12 11.63a3.5 3.5 0 0 1 4.54-3.32"/></svg></div>
            <span class="label">{{ $t('nav.leaderboard') }}</span>
            <span class="tooltip" v-if="isCollapsed">{{ $t('sidebar.tt_leaderboard') }}</span>
          </router-link>
          <router-link to="/suggestions" class="nav-link vip-glow" active-class="is-active">
            <div class="icon-box"><svg class="icon gold" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 3l1.9 5.8a2 2 0 0 0 1.3 1.3L21 12l-5.8 1.9a2 2 0 0 0-1.3 1.3L12 21l-1.9-5.8a2 2 0 0 0-1.3-1.3L3 12l5.8-1.9a2 2 0 0 0 1.3-1.3L12 3z"/></svg></div>
            <span class="label">{{ $t('sidebar.suggest_today') }}</span>
            <span class="tooltip" v-if="isCollapsed">{{ $t('sidebar.tt_suggest') }}</span>
          </router-link>
          <router-link to="/meal-plan" class="nav-link vip-glow" active-class="is-active">
            <div class="icon-box"><svg class="icon gold" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18M9 16l2 2 4-4"/></svg></div>
            <span class="label">{{ $t('sidebar.meal_calendar') }}</span>
            <span class="tooltip" v-if="isCollapsed">{{ $t('sidebar.tt_meal') }}</span>
          </router-link>
        </div>

        <div class="nav-group animate-stagger" style="--d: 0.3s">
          <label class="group-tag" v-if="!isCollapsed">{{ $t('sidebar.group_personal') }}</label>
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
            <span class="tooltip" v-if="isCollapsed">{{ $t('nav.storage') }}</span>
          </router-link>
        </div>
      </nav>
    </div>

    <div class="sidebar-footer">
      <div class="premium-core-card animate-up" v-if="!isCollapsed">
        <div class="liquid-bg"></div>
        <div class="card-head">
          <span class="crown-badge">👑</span>
          <div class="text">
            <h3>GOMET CLUB</h3>
            <p>Sovereign Access</p>
          </div>
        </div>
        <button class="btn-sovereign-unlock" @click="$emit('open-premium')">
          {{ $t('sidebar.unlock') }}
          <div class="flare-sweep"></div>
        </button>
      </div>
      
      <div v-else class="mini-vip-stage">
        <button class="artifact-button" @click="$emit('open-premium')">
          <span class="artifact-icon">👑</span>
          <div class="aura-layer layer-1"></div>
          <div class="aura-layer layer-2"></div>
          <div class="aura-layer layer-3"></div>
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router' // 🔥 Import useRoute

defineEmits(['open-premium'])

const route = useRoute() // 🔥 Khởi tạo route
const isCollapsed = ref(true)
const toggleSidebar = () => { isCollapsed.value = !isCollapsed.value }
</script>

<style scoped>
/* --- ✨ SOVEREIGN ANIMATIONS ✨ --- */

/* 1. Logo Pulse */
@keyframes sovereignPulse {
  0%   { filter: drop-shadow(0 0 0px  rgba(234, 88, 12, 0.4)); }
  50%  { filter: drop-shadow(0 0 12px rgba(234, 88, 12, 0.6)); }
  100% { filter: drop-shadow(0 0 0px  rgba(234, 88, 12, 0.4)); }
}
.sovereign-pulse {
  animation: sovereignPulse 4s ease-in-out infinite;
  will-change: filter;
}

/* 2. Sheen — hover only (was constant, causing huge repaint every frame) */
.logo-container-premium { position: relative; overflow: hidden; border-radius: 10px; border: 2px solid var(--color-primary-600); }
.logo-container-premium::after {
  content: '';
  position: absolute; top: -100%; left: -100%; width: 300%; height: 300%;
  background: linear-gradient(45deg, transparent 45%, rgba(255,255,255,0.65) 50%, transparent 55%);
  opacity: 0;
  pointer-events: none;
  transition: opacity var(--duration-fast) var(--ease-out);
}
.logo-container-premium:hover::after { opacity: 1; }  /* sheen on hover only */
@keyframes sheen { 0% { transform: translate(-30%, -30%); } 100% { transform: translate(30%, 30%); } }

/* --- 👑 HIỆU ỨNG NÚT PREMIUM ĐỈNH NÓC --- */

/* Trạng thái thu gọn: Mini Artifact */
.mini-vip-stage { display: flex; justify-content: center; align-items: center; height: 80px; }

.artifact-button {
  position: relative; width: 58px; height: 58px; border-radius: 18px;
  background: rgba(255,255,255,0.06); backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px);
  border: 1.5px solid #D4AF37; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: transform var(--duration-slow) var(--ease-spring),
              box-shadow var(--duration-slow) var(--ease-out);
  z-index: 10;
  will-change: transform;
}

.artifact-icon { font-size: 24px; z-index: 5; transition: transform var(--duration-normal) var(--ease-spring); }

/* Hào quang đa tầng */
.aura-layer {
  position: absolute; inset: -2px; border-radius: inherit;
  border: 1px solid rgba(212, 175, 55, 0.3);
  pointer-events: none;
  will-change: transform, opacity;
}
.layer-1 { animation: aura-pulse 4s   ease-in-out infinite; }
.layer-2 { animation: aura-pulse 4s   ease-in-out infinite 1.3s; opacity: 0.5; }
.layer-3 { animation: aura-pulse 4s   ease-in-out infinite 2.6s; opacity: 0.2; }

@keyframes aura-pulse {
  0% { transform: scale(1); opacity: 0.8; }
  100% { transform: scale(1.6); opacity: 0; }
}

.artifact-button:hover {
  transform: translateY(-5px) scale(1.1);
  box-shadow: 0 15px 30px rgba(212, 175, 55, 0.4);
  background: rgba(212,175,55,0.85);
}
.artifact-button:hover .artifact-icon { transform: scale(1.2) rotate(10deg); filter: brightness(0); }

/* Trạng thái mở rộng: Premium Card */
.premium-core-card {
  background: rgba(255,255,255,0.07);
  backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
  border-radius: 24px; padding: 20px;
  border: 1px solid rgba(212, 175, 55, 0.2); position: relative; overflow: hidden;
}
.liquid-bg {
  position: absolute; inset: 0;
  background: radial-gradient(circle at top right, rgba(212, 175, 55, 0.1), transparent 70%);
}

.card-head { display: flex; align-items: center; gap: 15px; margin-bottom: 20px; position: relative; z-index: 2; }
.crown-badge { font-size: 20px; filter: drop-shadow(0 0 8px gold); }
.text h3 { color: #D4AF37; font-family: var(--font-display); font-size: 16px; margin: 0; }
.text p { color: #666; font-size: 10px; margin: 2px 0 0; font-weight: 700; }

.btn-sovereign-unlock {
  width: 100%; padding: 12px; border: none; border-radius: 12px;
  background: linear-gradient(135deg, #D4AF37 0%, #B8860B 100%);
  color: #000; font-weight: 900; font-size: 10px; letter-spacing: 1.5px;
  cursor: pointer;
  transition: transform var(--duration-fast) var(--ease-out),
              box-shadow var(--duration-fast) var(--ease-out);
  position: relative; overflow: hidden; will-change: transform;
}
.btn-sovereign-unlock:hover { transform: translateY(-3px); box-shadow: 0 10px 25px rgba(212, 175, 55, 0.4); }
.btn-sovereign-unlock:active { transform: translateY(-1px) scale(0.98); }

/* Vệt sáng — chỉ chạy khi hover nút (không còn loop liên tục) */
.flare-sweep {
  position: absolute; top: 0; left: -100%; width: 50%; height: 100%;
  background: linear-gradient(to right, transparent, rgba(255,255,255,0.4), transparent);
  transform: skewX(-25deg);
  animation: btn-sheen 0.7s var(--ease-out) forwards;
  animation-play-state: paused;  /* paused by default — no constant repaint */
  will-change: transform;
}
.btn-sovereign-unlock:hover .flare-sweep { animation-play-state: running; }
@keyframes btn-sheen { 0% { left: -100%; } 100% { left: 150%; } }

/* --- CẤU TRÚC SIDEBAR (GIỮ NGUYÊN TINH GỌN) --- */
.gomet-sovereign-sidebar {
  width: var(--sidebar-width); height: 100vh;
  background: rgba(255, 255, 255, 0.82); backdrop-filter: blur(24px); -webkit-backdrop-filter: blur(24px);
  border-right: 1px solid rgba(255, 255, 255, 0.4);
  display: flex; flex-direction: column; position: sticky; top: 0; z-index: var(--z-sticky);
  transition: width var(--duration-slower) cubic-bezier(0.19, 1, 0.22, 1),
              background-color 0.4s ease, border-color 0.4s ease; /* Thêm transition đổi màu */
  font-family: var(--font-ui); overflow: hidden;
}
.gomet-sovereign-sidebar.is-collapsed { width: var(--sidebar-collapsed); }

.sidebar-header { height: 90px; display: flex; align-items: center; justify-content: space-between; padding: 0 22px; flex-shrink: 0; }
.is-collapsed .sidebar-header { justify-content: center; padding: 0; }
.brand-wrapper { display: flex; align-items: center; gap: 10px; }
.brand-logo { width: 34px; height: 34px; object-fit: cover; border-radius: 9px; }
.brand-text .main { display: block; font-family: 'Playfair Display', serif; font-weight: 900; font-size: 19px; color: #111; letter-spacing: 0.3px; line-height: 1; transition: color 0.4s ease; }
.brand-text .sub { font-size: 7px; font-weight: 900; color: #EA580C; letter-spacing: 2px; }

.logo-mini { width: 44px; height: 44px; border-radius: 12px; border: 2px solid #EA580C; padding: 2px; transition: 0.4s; cursor: pointer; }

.minimal-toggle { background: #f9fafb; border: 1px solid #f0f0f0; color: #d1d5db; width: 28px; height: 28px; border-radius: 50%; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.3s; }
.minimal-toggle:hover { color: #EA580C; border-color: #EA580C; }

.nav-container-vipro { flex: 1; overflow-y: auto; padding: 10px 15px; scrollbar-width: none; }
.nav-container-vipro::-webkit-scrollbar { display: none; }
.nav-group { margin-bottom: 22px; }
.group-tag { display: block; font-size: 0.58rem; font-weight: 900; color: #d1d1d1; letter-spacing: 2px; margin-bottom: 12px; padding-left: 12px; text-transform: uppercase; transition: color 0.4s ease; }
.gold-txt { color: #D4AF37 !important; }

.nav-link {
  display: flex; align-items: center; padding: 12px 14px; color: var(--color-neutral-500);
  text-decoration: none; border-radius: var(--radius-md);
  transition: background     var(--duration-fast) var(--ease-out),
              color          var(--duration-fast) var(--ease-out),
              transform      var(--duration-fast) var(--ease-out);
  margin-bottom: var(--space-1); position: relative;
  min-height: 44px; /* touch target */
}
.nav-link::before {
  content: ""; position: absolute; left: 0; top: 15%; height: 70%; width: 3px;
  background: linear-gradient(to bottom, var(--color-primary-400), var(--color-primary-700));
  border-radius: 0 4px 4px 0; opacity: 0;
  transform: scaleY(0.6);
  transition: opacity var(--duration-fast) var(--ease-out), transform var(--duration-fast) var(--ease-out);
}
.nav-link:hover { background: var(--color-neutral-50); color: var(--color-neutral-900); transform: translateX(4px); }
.nav-link:hover::before { opacity: 1; transform: scaleY(1); }
.nav-link.is-active {
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(24px); -webkit-backdrop-filter: blur(24px);
  color: var(--color-primary-600) !important;
  border: 1px solid rgba(234, 88, 12, 0.14);
  box-shadow: 0 4px 20px rgba(234, 88, 12, 0.10), inset 0 1px 0 rgba(255, 255, 255, 0.7);
}
.nav-link.is-active::before { opacity: 1; transform: scaleY(1); }
.nav-link.is-active .icon { color: var(--color-primary-500); stroke-width: 2.2px; }
.nav-link.is-active .label { color: var(--color-primary-600); }

.vip-glow:hover { background: rgba(212,175,55,0.10) !important; color: #D4AF37 !important; box-shadow: 0 8px 24px rgba(212, 175, 55, 0.18); backdrop-filter: blur(8px); -webkit-backdrop-filter: blur(8px); }
.vip-glow.is-active { border: 1px solid rgba(212, 175, 55, 0.3); }

.icon-box {
  width: 24px; height: 24px; display: flex; align-items: center; justify-content: center;
  border-radius: 7px; transition: background var(--duration-fast) var(--ease-out);
}
.nav-link:hover .icon-box { background: rgba(234, 88, 12, 0.07); }
.nav-link.is-active .icon-box { background: rgba(234, 88, 12, 0.10); backdrop-filter: blur(8px); -webkit-backdrop-filter: blur(8px); border: 1px solid rgba(234, 88, 12, 0.12); }
.label { margin-left: 14px; font-weight: 700; font-size: 0.88rem; white-space: nowrap; }

.sidebar-footer { padding: 15px 15px 25px; flex-shrink: 0; }
.is-collapsed .sidebar-footer { padding: 25px 0; display: flex; justify-content: center; }

/* TOOLTIP & UTILS */
.is-collapsed .label, .is-collapsed .group-tag { display: none; }
.is-collapsed .nav-link { justify-content: center; padding: 15px 0; }
.tooltip { position: absolute; left: 100%; top: 50%; transform: translateY(-50%); background: rgba(20,12,5,0.72); backdrop-filter: blur(14px); -webkit-backdrop-filter: blur(14px); color: #fff; padding: 6px 12px; border-radius: 8px; font-size: 10px; font-weight: 800; white-space: nowrap; opacity: 0; visibility: hidden; transition: 0.3s; margin-left: 15px; z-index: 100; border: 1px solid rgba(212, 175, 55, 0.3); }
.nav-link:hover .tooltip { opacity: 1; visibility: visible; transform: translateY(-50%) translateX(5px); }


/* ==================================================== */
/* 🔥 GIAO DIỆN MÀU ĐEN (THEME DARK) CHO SIDEBAR        */
/* ==================================================== */

.gomet-sovereign-sidebar.theme-dark {
  background: rgba(3, 7, 18, 0.95); /* Màu nền đen sâu */
  border-right-color: rgba(255, 255, 255, 0.05);
}

/* Đổi màu text Logo */
.gomet-sovereign-sidebar.theme-dark .brand-text .main {
  color: #FFFFFF;
}

/* Đổi màu nút thu gọn (Toggle button) */
.gomet-sovereign-sidebar.theme-dark .minimal-toggle {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  color: #9CA3AF;
}
.gomet-sovereign-sidebar.theme-dark .minimal-toggle:hover {
  color: #EA580C;
  border-color: #EA580C;
  background: rgba(234, 88, 12, 0.1);
}

/* Đổi màu chữ nhóm Menu (Journey, Personal) */
.gomet-sovereign-sidebar.theme-dark .group-tag:not(.gold-txt) {
  color: #6B7280;
}

/* Đổi màu Link Menu thường */
.gomet-sovereign-sidebar.theme-dark .nav-link {
  color: #9CA3AF;
}
.gomet-sovereign-sidebar.theme-dark .nav-link:hover {
  background: rgba(255, 255, 255, 0.05);
  color: #FFFFFF;
}
.gomet-sovereign-sidebar.theme-dark .nav-link:hover .icon-box {
  background: rgba(255, 255, 255, 0.1);
}

/* Đổi màu Link Menu Đang Active */
.gomet-sovereign-sidebar.theme-dark .nav-link.is-active {
  background: rgba(234, 88, 12, 0.15); /* Nền cam nhạt */
  border-color: rgba(234, 88, 12, 0.3);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5); /* Đổ bóng tối */
  color: #EA580C !important;
}
.gomet-sovereign-sidebar.theme-dark .nav-link.is-active .label {
  color: #EA580C;
}

/* Hiệu chỉnh lại Card Premium cho hợp nền tối */
.gomet-sovereign-sidebar.theme-dark .premium-core-card {
  background: rgba(212, 175, 55, 0.05);
  border-color: rgba(212, 175, 55, 0.2);
}
.gomet-sovereign-sidebar.theme-dark .text p {
  color: #9CA3AF;
}
</style>