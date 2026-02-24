<template>
  <aside class="gomet-sovereign-sidebar" :class="{ 'is-collapsed': isCollapsed }">
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
          <label class="group-tag" v-if="!isCollapsed">Hành trình</label>
          <router-link to="/home" class="nav-link" active-class="is-active">
            <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/></svg></div>
            <span class="label">Trang chủ</span>
            <span class="tooltip" v-if="isCollapsed">Trang chủ</span>
          </router-link>
          <router-link to="/search" class="nav-link" active-class="is-active">
            <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg></div>
            <span class="label">Tìm kiếm</span>
            <span class="tooltip" v-if="isCollapsed">Tìm kiếm</span>
          </router-link>
        </div>

        <div class="nav-group premium-vibe animate-stagger" style="--d: 0.2s">
          <label class="group-tag gold-txt" v-if="!isCollapsed">Đặc quyền Premium</label>
          <router-link to="/leaderboard" class="nav-link vip-glow" active-class="is-active">
            <div class="icon-box"><svg class="icon gold" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6M18 9h1.5a2.5 2.5 0 0 0 0-5H18M4 22h16M10 14.66V17c0 .55.47.98.97 1.21l1.03.46 1.03-.46c.5-.23.97-.66.97-1.21v-2.34M12 11.63a3.5 3.5 0 0 1 4.54-3.32"/></svg></div>
            <span class="label">Bảng xếp hạng</span>
            <span class="tooltip" v-if="isCollapsed">BXH Thượng hạng</span>
          </router-link>
          <router-link to="/suggestions" class="nav-link vip-glow" active-class="is-active">
            <div class="icon-box"><svg class="icon gold" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 3l1.9 5.8a2 2 0 0 0 1.3 1.3L21 12l-5.8 1.9a2 2 0 0 0-1.3 1.3L12 21l-1.9-5.8a2 2 0 0 0-1.3-1.3L3 12l5.8-1.9a2 2 0 0 0 1.3-1.3L12 3z"/></svg></div>
            <span class="label">Gợi ý hôm nay</span>
            <span class="tooltip" v-if="isCollapsed">Gợi ý VIP</span>
          </router-link>
          <router-link to="/meal-plan" class="nav-link vip-glow" active-class="is-active">
            <div class="icon-box"><svg class="icon gold" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18M9 16l2 2 4-4"/></svg></div>
            <span class="label">Lịch ăn uống</span>
            <span class="tooltip" v-if="isCollapsed">Lịch ăn VIP</span>
          </router-link>
        </div>

        <div class="nav-group animate-stagger" style="--d: 0.3s">
          <label class="group-tag" v-if="!isCollapsed">Cá nhân</label>
          <router-link to="/events" class="nav-link" active-class="is-active">
            <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8 2v4M16 2v4M3 10h18M5 4h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z"/></svg></div>
            <span class="label">Sự kiện</span>
          </router-link>
          <router-link to="/profile" class="nav-link" active-class="is-active">
            <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></div>
            <span class="label">Bếp của tôi</span>
          </router-link>
          <router-link to="/storage" class="nav-link" active-class="is-active">
            <div class="icon-box"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><path d="M3.27 6.96L12 12.01l8.73-5.05M12 22.08V12"/></svg></div>
            <span class="label">Khu lưu trữ</span>
            <span class="tooltip" v-if="isCollapsed">Kho dữ liệu</span>
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
          UNLOCK PRIVILEGES
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
defineEmits(['open-premium'])

const isCollapsed = ref(true)
const toggleSidebar = () => { isCollapsed.value = !isCollapsed.value }
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@900&family=Mulish:wght@600;700;800;900&display=swap');

/* --- ✨ SOVEREIGN ANIMATIONS ✨ --- */

/* 1. Logo Pulse & Sheen */
@keyframes sovereignPulse {
  0% { transform: scale(1); filter: drop-shadow(0 0 0px rgba(234, 88, 12, 0.4)); }
  50% { transform: scale(1.05); filter: drop-shadow(0 0 15px rgba(234, 88, 12, 0.6)); }
  100% { transform: scale(1); filter: drop-shadow(0 0 0px rgba(234, 88, 12, 0.4)); }
}
.sovereign-pulse { animation: sovereignPulse 3s infinite ease-in-out; }

.logo-container-premium { position: relative; overflow: hidden; border-radius: 10px; border: 2px solid #EA580C; }
.logo-container-premium::after {
  content: ''; position: absolute; top: -100%; left: -100%; width: 300%; height: 300%;
  background: linear-gradient(45deg, transparent 45%, rgba(255,255,255,0.6) 50%, transparent 55%);
  animation: sheen 5s infinite;
}
@keyframes sheen { 0% { transform: translate(-30%, -30%); } 100% { transform: translate(30%, 30%); } }

/* --- 👑 HIỆU ỨNG NÚT PREMIUM ĐỈNH NÓC --- */

/* Trạng thái thu gọn: Mini Artifact */
.mini-vip-stage { display: flex; justify-content: center; align-items: center; height: 80px; }

.artifact-button {
  position: relative; width: 58px; height: 58px; border-radius: 18px;
  background: #000; border: 1.5px solid #D4AF37; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.5s cubic-bezier(0.19, 1, 0.22, 1);
  z-index: 10;
}

.artifact-icon { font-size: 24px; z-index: 5; transition: 0.4s; }

/* Hào quang đa tầng */
.aura-layer {
  position: absolute; inset: -2px; border-radius: inherit;
  border: 1px solid rgba(212, 175, 55, 0.3);
  pointer-events: none;
}
.layer-1 { animation: aura-pulse 3s infinite; }
.layer-2 { animation: aura-pulse 3s infinite 1s; opacity: 0.5; }
.layer-3 { animation: aura-pulse 3s infinite 2s; opacity: 0.2; }

@keyframes aura-pulse {
  0% { transform: scale(1); opacity: 0.8; }
  100% { transform: scale(1.6); opacity: 0; }
}

.artifact-button:hover {
  transform: translateY(-5px) scale(1.1);
  box-shadow: 0 15px 30px rgba(212, 175, 55, 0.4);
  background: #D4AF37;
}
.artifact-button:hover .artifact-icon { transform: scale(1.2) rotate(10deg); filter: brightness(0); }

/* Trạng thái mở rộng: Premium Card */
.premium-core-card {
  background: #080808; border-radius: 24px; padding: 20px;
  border: 1px solid rgba(212, 175, 55, 0.2); position: relative; overflow: hidden;
}
.liquid-bg {
  position: absolute; inset: 0;
  background: radial-gradient(circle at top right, rgba(212, 175, 55, 0.1), transparent 70%);
}

.card-head { display: flex; align-items: center; gap: 15px; margin-bottom: 20px; position: relative; z-index: 2; }
.crown-badge { font-size: 20px; filter: drop-shadow(0 0 8px gold); }
.text h3 { color: #D4AF37; font-family: 'Playfair Display', serif; font-size: 16px; margin: 0; }
.text p { color: #666; font-size: 10px; margin: 2px 0 0; font-weight: 700; }

.btn-sovereign-unlock {
  width: 100%; padding: 12px; border: none; border-radius: 12px;
  background: linear-gradient(135deg, #D4AF37 0%, #B8860B 100%);
  color: #000; font-weight: 900; font-size: 10px; letter-spacing: 1.5px;
  cursor: pointer; transition: 0.4s; position: relative; overflow: hidden;
}
.btn-sovereign-unlock:hover { transform: translateY(-3px); box-shadow: 0 10px 25px rgba(212, 175, 55, 0.4); }

/* Vệt sáng chạy ngang nút */
.flare-sweep {
  position: absolute; top: 0; left: -100%; width: 50%; height: 100%;
  background: linear-gradient(to right, transparent, rgba(255,255,255,0.4), transparent);
  transform: skewX(-25deg); animation: btn-sheen 4s infinite;
}
@keyframes btn-sheen { 0%, 80% { left: -100%; } 100% { left: 150%; } }

/* --- CẤU TRÚC SIDEBAR (GIỮ NGUYÊN TINH GỌN) --- */
.gomet-sovereign-sidebar {
  width: 260px; height: 100vh; background: #ffffff; border-right: 1px solid #f2f2f2;
  display: flex; flex-direction: column; position: sticky; top: 0; z-index: 1000;
  transition: width 0.6s cubic-bezier(0.19, 1, 0.22, 1); font-family: 'Mulish', sans-serif; overflow: hidden;
}
.gomet-sovereign-sidebar.is-collapsed { width: 85px; }

.sidebar-header { height: 90px; display: flex; align-items: center; justify-content: space-between; padding: 0 22px; flex-shrink: 0; }
.is-collapsed .sidebar-header { justify-content: center; padding: 0; }
.brand-wrapper { display: flex; align-items: center; gap: 10px; }
.brand-logo { width: 34px; height: 34px; object-fit: cover; border-radius: 9px; }
.brand-text .main { display: block; font-family: 'Playfair Display', serif; font-weight: 900; font-size: 19px; color: #111; letter-spacing: 0.3px; line-height: 1; }
.brand-text .sub { font-size: 7px; font-weight: 900; color: #EA580C; letter-spacing: 2px; }

.logo-mini { width: 44px; height: 44px; border-radius: 12px; border: 2px solid #EA580C; padding: 2px; transition: 0.4s; cursor: pointer; }

.minimal-toggle { background: #f9fafb; border: 1px solid #f0f0f0; color: #d1d5db; width: 28px; height: 28px; border-radius: 50%; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.3s; }
.minimal-toggle:hover { color: #EA580C; border-color: #EA580C; }

.nav-container-vipro { flex: 1; overflow-y: auto; padding: 10px 15px; scrollbar-width: none; }
.nav-container-vipro::-webkit-scrollbar { display: none; }
.nav-group { margin-bottom: 22px; }
.group-tag { display: block; font-size: 0.58rem; font-weight: 900; color: #d1d1d1; letter-spacing: 2px; margin-bottom: 12px; padding-left: 12px; text-transform: uppercase; }
.gold-txt { color: #D4AF37 !important; }

.nav-link { display: flex; align-items: center; padding: 12px 14px; color: #7a7a7a; text-decoration: none; border-radius: 14px; transition: 0.4s cubic-bezier(0.19, 1, 0.22, 1); margin-bottom: 4px; position: relative; }
.nav-link::before { content: ""; position: absolute; left: 0; top: 20%; height: 60%; width: 3px; background: #EA580C; border-radius: 0 4px 4px 0; opacity: 0; transition: 0.3s; }
.nav-link:hover { background: #fdfdfd; color: #111; transform: translateX(6px); }
.nav-link:hover::before { opacity: 1; }
.nav-link.is-active { background: #111; color: #fff !important; box-shadow: 0 10px 25px rgba(0,0,0,0.1); }
.nav-link.is-active .icon { color: #EA580C; stroke-width: 2.2px; }

.vip-glow:hover { background: #000 !important; color: #D4AF37 !important; box-shadow: 0 15px 30px rgba(212, 175, 55, 0.2); }
.vip-glow.is-active { border: 1px solid rgba(212, 175, 55, 0.3); }

.icon-box { width: 22px; height: 22px; display: flex; align-items: center; justify-content: center; }
.label { margin-left: 14px; font-weight: 700; font-size: 0.88rem; white-space: nowrap; }

.sidebar-footer { padding: 15px 15px 25px; flex-shrink: 0; }
.is-collapsed .sidebar-footer { padding: 25px 0; display: flex; justify-content: center; }

/* TOOLTIP & UTILS */
.is-collapsed .label, .is-collapsed .group-tag { display: none; }
.is-collapsed .nav-link { justify-content: center; padding: 15px 0; }
.tooltip { position: absolute; left: 100%; top: 50%; transform: translateY(-50%); background: #000; color: #fff; padding: 6px 12px; border-radius: 8px; font-size: 10px; font-weight: 800; white-space: nowrap; opacity: 0; visibility: hidden; transition: 0.3s; margin-left: 15px; z-index: 100; border: 1px solid rgba(212, 175, 55, 0.3); }
.nav-link:hover .tooltip { opacity: 1; visibility: visible; transform: translateY(-50%) translateX(5px); }
</style>