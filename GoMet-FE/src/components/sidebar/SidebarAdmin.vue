<template>
  <aside class="sidebar-vipro-admin" :class="{ 'is-collapsed': isCollapsed }">
    <div class="bg-vipro-engine">
      <div class="mesh-glow"></div>
      <div class="sidebar-noise"></div>
      <div class="laser-edge"></div>
    </div>

    <div class="sidebar-header">
      <div class="brand-vipro" v-if="!isCollapsed">
        <div class="logo-artifact">
          <img src="@/assets/images/gomet.jpg" alt="Logo" class="logo-img" />
          <div class="sheen-layer"></div>
        </div>
        <div class="brand-info">
          <h1 class="brand-name">GOMET</h1>
          <div class="admin-badge-lux">
            <span class="pulse-dot"></span>
            <span class="badge-text">ADMIN SYSTEM</span>
          </div>
        </div>
      </div>

      <div class="brand-vipro-collapsed" v-else>
        <img src="@/assets/images/gomet.jpg" alt="Logo" class="logo-img-sm" />
      </div>

      <button class="btn-toggle-sidebar" @click="toggleSidebar" :title="isCollapsed ? 'Mở rộng' : 'Thu gọn'">
        <svg v-if="!isCollapsed" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
          stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
        <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
          stroke-linecap="round" stroke-linejoin="round">
          <polyline points="9 18 15 12 9 6"></polyline>
        </svg>
      </button>
    </div>

    <nav class="nav-scroll-area custom-scrollbar">
      <div class="nav-engine-fixed">

        <div class="nav-section anim-stagger" style="--d: 0.1s">
          <label class="section-tag" v-if="!isCollapsed">Tổng quan</label>
          <div class="section-divider" v-else></div>

          <router-link to="/admin/dashboard" class="nav-link-lux" active-class="active"
            :title="isCollapsed ? 'Dashboard' : ''">
            <div class="icon-orb"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <rect x="3" y="3" width="7" height="7" rx="1.5"></rect>
                <rect x="14" y="3" width="7" height="7" rx="1.5"></rect>
                <rect x="14" y="14" width="7" height="7" rx="1.5"></rect>
                <rect x="3" y="14" width="7" height="7" rx="1.5"></rect>
              </svg></div>
            <span class="link-text" v-if="!isCollapsed">Dashboard</span>
          </router-link>

          <router-link to="/admin/statistics" class="nav-link-lux" active-class="active"
            :title="isCollapsed ? 'Thống kê hệ thống' : ''">
            <div class="icon-orb"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <path d="M12 20V10"></path>
                <path d="M18 20V4"></path>
                <path d="M6 20v-4"></path>
              </svg></div>
            <span class="link-text" v-if="!isCollapsed">Thống kê hệ thống</span>
          </router-link>
        </div>

        <div class="nav-section anim-stagger" style="--d: 0.2s">
          <label class="section-tag">Cộng đồng</label>
          <router-link to="/admin/users" class="nav-link-lux" active-class="active">
            <div class="icon-orb"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
              </svg></div>
            <span class="link-text">Quản lý Hội Viên</span>
          </router-link>
          <router-link to="/admin/achievements" class="nav-link-lux" active-class="active">
            <div class="icon-orb"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <circle cx="12" cy="8" r="7"></circle>
                <polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"></polyline>
              </svg></div>
            <span class="link-text">Vinh Danh Danh Hiệu</span>
          </router-link>
        </div>

        <div class="nav-section anim-stagger" style="--d: 0.3s">
          <label class="section-tag">Nội dung</label>
          <router-link to="/admin/posts" class="nav-link-lux" active-class="active">
            <div class="icon-orb"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
              </svg></div>
            <span class="link-text">Duyệt Sáng Tạo</span>
          </router-link>
          <router-link to="/admin/categories" class="nav-link-lux" active-class="active">
            <div class="icon-orb"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <line x1="8" y1="6" x2="21" y2="6"></line>
                <line x1="8" y1="12" x2="21" y2="12"></line>
                <line x1="8" y1="18" x2="21" y2="18"></line>
                <circle cx="3" cy="6" r="1.5"></circle>
                <circle cx="3" cy="12" r="1.5"></circle>
                <circle cx="3" cy="18" r="1.5"></circle>
              </svg></div>
            <span class="link-text">Cấu trúc Danh Mục</span>
          </router-link>
          <router-link to="/admin/comments" class="nav-link-lux" active-class="active">
            <div class="icon-orb"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7">
                </path>
              </svg></div>
            <span class="link-text">Kiểm duyệt Tương Tác</span>
          </router-link>
        </div>

        <div class="nav-section anim-stagger" style="--d: 0.4s">
          <label class="section-tag">Hệ thống</label>
          <router-link to="/admin/appeals" class="nav-link-lux" active-class="active">
            <div class="icon-orb"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2m1 15h-2v-2h2v2m0-4h-2V7h2v6z"></path>
              </svg></div>
            <span class="link-text">Khiếu nại ban nhầm</span>
          </router-link>
          <router-link to="/admin/tickets" class="nav-link-lux warning-vibe" active-class="active">
            <div class="icon-orb"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
              </svg></div>
            <span class="link-text">Trung tâm Phản hồi</span>
            <span class="count-badge">5</span>
          </router-link>
          <router-link to="/admin/notifications" class="nav-link-lux" active-class="active">
            <div class="icon-orb"><svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
              </svg></div>
            <span class="link-text">Thông Báo Chung</span>
          </router-link>
        </div>

        <div class="nav-section anim-stagger" style="--d: 0.5s">
          <label class="section-tag" v-if="!isCollapsed">Chiến dịch</label>
          <div class="section-divider" v-else></div>

          <router-link to="/admin/events" class="nav-link-lux" active-class="active"
            :title="isCollapsed ? 'Quản lý Sự Kiện' : ''">
            <div class="icon-orb"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
              </svg></div>
            <span class="link-text" v-if="!isCollapsed">Quản lý Sự Kiện</span>
          </router-link>

          <router-link to="/admin/transactions" class="nav-link-lux" active-class="active"
            :title="isCollapsed ? 'Doanh thu & Hóa đơn' : ''">
            <div class="icon-orb"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2.5">
                <rect x="1" y="4" width="22" height="16" rx="2" ry="2"></rect>
                <line x1="1" y1="10" x2="23" y2="10"></line>
              </svg></div>
            <span class="link-text" v-if="!isCollapsed">Doanh thu & Hóa đơn</span>
          </router-link>
        </div>
      </div>
    </nav>

    <div class="sidebar-footer">
      <button class="btn-exit-admin" @click="$router.push('/home')" :title="isCollapsed ? 'Về Trang Khách' : ''">
        <div class="btn-content">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
            stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
            <polyline points="16 17 21 12 16 7"></polyline>
            <line x1="21" y1="12" x2="9" y2="12"></line>
          </svg>
          <span v-if="!isCollapsed">VỀ TRANG KHÁCH</span>
        </div>
        <div class="btn-shine"></div>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// Khai báo sự kiện để báo cho Layout cha (AdminLayout) biết Sidebar đang ở trạng thái nào
const emit = defineEmits(['toggle-collapse']);

// Đọc trạng thái từ LocalStorage để giữ nguyên khi F5 trang
const isCollapsed = ref(localStorage.getItem('gomet_admin_sidebar_collapsed') === 'true');

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value;
  localStorage.setItem('gomet_admin_sidebar_collapsed', isCollapsed.value);
  emit('toggle-collapse', isCollapsed.value); // Báo ra ngoài cha
};

// Khi mới vào, bắn tín hiệu ngay lập tức để Layout căn chỉnh lề
onMounted(() => {
  emit('toggle-collapse', isCollapsed.value);
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800;900&family=Playfair+Display:wght@900&display=swap');

/* --- ✨ 1. CORE CONTAINER (PREMIUM PITCH BLACK) --- */
.sidebar-vipro-admin {
  width: 270px;
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  background: #030303;
  color: white;
  font-family: 'Inter', sans-serif;
  overflow: hidden;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
  box-shadow: none;
  /* Xóa bóng đen bên phải */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.bg-vipro-engine {
  position: absolute;
  inset: 0;
  z-index: -1;
}

.mesh-glow {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 0% 0%, rgba(234, 88, 12, 0.16) 0%, transparent 45%),
    radial-gradient(circle at 100% 100%, rgba(234, 88, 12, 0.08) 0%, transparent 45%);
}

.sidebar-noise {
  position: absolute;
  inset: 0;
  opacity: 0.02;
  pointer-events: none;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.6'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
}

.laser-edge {
  position: absolute;
  right: 0;
  top: 0;
  width: 1px;
  height: 100%;
  background: linear-gradient(to bottom, transparent, rgba(234, 88, 12, 0.3), transparent);
}

/* --- 🏷️ 2. HEADER: BIỂU TƯỢNG HOÀNG GIA --- */
.sidebar-header {
  height: 90px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.brand-vipro {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-vipro-collapsed {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.logo-img-sm {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  object-fit: cover;
}

.logo-artifact {
  width: 40px;
  height: 40px;
  background: white;
  border-radius: 10px;
  padding: 3px;
  position: relative;
  overflow: hidden;
}

.logo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 7px;
}

.sheen-layer {
  position: absolute;
  inset: 0;
  background: linear-gradient(45deg, transparent 35%, rgba(255, 255, 255, 0.8) 50%, transparent 65%);
  animation: sheenLoop 6s infinite;
  transform: translateX(-100%);
}

@keyframes sheenLoop {
  0% {
    transform: translateX(-110%);
  }

  15%,
  100% {
    transform: translateX(110%);
  }
}

.brand-name {
  font-family: 'Playfair Display', serif;
  font-weight: 900;
  font-size: 1.5rem;
  margin: 0;
  line-height: 1;
  background: linear-gradient(to bottom, #FFD07F 0%, #FFFFFF 50%, #FF9500 100%);
  -webkit-text-fill-color: transparent;
  filter: drop-shadow(0 2px 10px rgba(234, 88, 12, 0.4));
}

.admin-badge-lux {
  font-size: 8px;
  font-weight: 900;
  color: #fb923c;
  letter-spacing: 2.5px;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 5px;
}

.pulse-dot {
  width: 6px;
  height: 6px;
  background: #fb923c;
  border-radius: 50%;
  animation: pulseLight 3s infinite;
  box-shadow: 0 0 12px #fb923c;
}

@keyframes pulseLight {

  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }

  50% {
    opacity: 0.4;
    transform: scale(1.4);
  }
}

/* NÚT THU GỌN */
.btn-toggle-sidebar {
  background: transparent;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 6px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.3s;
}

.btn-toggle-sidebar:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

/* KHI COLLAPSE, ÉP NÚT TOGGLE RA GIỮA (NẾU MUỐN) */
.is-collapsed .sidebar-header {
  flex-direction: column;
  padding: 15px 0;
  height: auto;
  gap: 15px;
}

.is-collapsed .btn-toggle-sidebar {
  margin: 0 auto;
}


/* --- 📜 3. NAVIGATION (SMART CENTER & ADAPTIVE) --- */
.nav-scroll-area {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 0 14px;
}

.nav-engine-fixed {
  margin: auto 0;
  display: flex;
  flex-direction: column;
  gap: clamp(14px, 2.2vh, 28px);
  padding: 25px 0;
  width: 100%;
}

.nav-section {
  flex-shrink: 0;
}

.section-tag {
  display: block;
  font-size: 9px;
  font-weight: 900;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.25);
  margin: 0 0 12px 14px;
  letter-spacing: 2px;
}

/* NAV LINK SOVEREIGN DESIGN */
.nav-link-lux {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 16px;
  border-radius: 14px;
  color: rgba(255, 255, 255, 0.5);
  text-decoration: none;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1);
  margin-bottom: 2px;
  position: relative;
}

.nav-link-lux:hover {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
  transform: translateX(6px);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.05);
}

.sidebar-vipro-admin:not(.is-collapsed) .nav-link-lux:hover {
  transform: translateX(5px);
}

/* 🔥 ACTIVE AMBIENT GLOW */
.nav-link-lux.active {
  background: linear-gradient(90deg, rgba(234, 88, 12, 0.16) 0%, transparent 100%);
  color: #fb923c;
  font-weight: 800;
  box-shadow: 10px 0 40px rgba(0, 0, 0, 0.4);
}

.nav-link-lux.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 15%;
  height: 70%;
  width: 4px;
  background: #fb923c;
  border-radius: 0 4px 4px 0;
  box-shadow: 0 0 20px #fb923c, 0 0 40px rgba(234, 88, 12, 0.4);
}

.nav-link-lux.active .icon-orb {
  color: #fb923c;
  filter: drop-shadow(0 0 8px rgba(234, 88, 12, 0.6));
  transform: scale(1.1);
}

.icon-orb {
  transition: 0.4s;
  flex-shrink: 0;
  opacity: 0.8;
}

/* BADGES: GEM STYLE */
.count-badge {
  margin-left: auto;
  background: linear-gradient(135deg, #ea580c, #9a3412);
  color: #fff;
  font-size: 10px;
  font-weight: 900;
  padding: 2px 8px;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(234, 88, 12, 0.3);
}

.lux-alert-dot {
  width: 8px;
  height: 8px;
  background: #fb923c;
  border-radius: 50%;
  margin-left: auto;
  box-shadow: 0 0 12px #fb923c;
}

/* --- 👑 4. FOOTER: CHỈ THỊ THOÁT --- */
.sidebar-footer {
  padding: 20px 18px 35px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  flex-shrink: 0;
}

.btn-exit-admin {
  width: 100%;
  height: 50px;
  border-radius: 16px;
  border: 1px solid rgba(234, 88, 12, 0.25);
  background: rgba(234, 88, 12, 0.04);
  color: #fb923c;
  font-weight: 800;
  font-size: 11px;
  letter-spacing: 1.8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: 0.4s;
}

.btn-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.btn-exit-admin:hover {
  background: #ea580c;
  color: white;
  transform: translateY(-3px);
  box-shadow: 0 15px 35px rgba(234, 88, 12, 0.4);
  border-color: transparent;
}

.btn-shine {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
  transform: translateX(-100%);
}

.btn-exit-admin:hover .btn-shine {
  animation: shineRun 1.8s infinite;
}

@keyframes shineRun {
  100% {
    transform: translateX(100%);
  }
}

/* --- 🏆 5. CUSTOM SCROLLBAR (GOLDEN FILAMENT) --- */
.nav-scroll-area,
.custom-scroll {
  scrollbar-width: thin;
  scrollbar-color: rgba(234, 88, 12, 0.5) transparent;
}

.nav-scroll-area::-webkit-scrollbar,
.custom-scroll::-webkit-scrollbar {
  width: 3px;
}

.nav-scroll-area::-webkit-scrollbar-track,
.custom-scroll::-webkit-scrollbar-track {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.02), rgba(0, 0, 0, 0.1));
}

.nav-scroll-area::-webkit-scrollbar-thumb,
.custom-scroll::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, transparent, rgba(234, 88, 12, 0.45), transparent);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.nav-scroll-area:hover::-webkit-scrollbar-thumb,
.custom-scroll:hover::-webkit-scrollbar-thumb {
  background: rgba(234, 88, 12, 0.65);
}

.nav-scroll-area:hover,
.custom-scroll:hover {
  scrollbar-color: rgba(234, 88, 12, 0.8) transparent;
}

/* --- 🎐 6. ENTRANCE STAGGER ANIMATION --- */
.anim-stagger {
  opacity: 0;
  transform: translateY(15px);
  animation: slideUpAdmin 0.6s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
  animation-delay: var(--d);
}

@keyframes slideUpAdmin {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* --- 📱 7. MEDIA ADAPTATION --- */
@media (max-height: 780px) {
  .nav-engine-fixed {
    margin: 0;
    gap: 12px;
    padding: 20px 0;
  }

  .sidebar-header {
    height: 80px;
  }
}
</style>