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
        <svg v-if="!isCollapsed" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
        <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="9 18 15 12 9 6"></polyline>
        </svg>
      </button>
    </div>

    <nav class="nav-scroll-area custom-scrollbar">
      <div class="nav-engine-fixed">
        
        <div class="nav-section anim-stagger" style="--d: 0.1s">
          <label class="section-tag" v-if="!isCollapsed">TỔNG QUAN</label>
          <div class="section-divider" v-else></div>

          <router-link to="/admin/dashboard" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Dashboard' : ''">
            <div class="icon-orb">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <rect x="3" y="3" width="7" height="7" rx="1.5"></rect>
                <rect x="14" y="3" width="7" height="7" rx="1.5"></rect>
                <rect x="14" y="14" width="7" height="7" rx="1.5"></rect>
                <rect x="3" y="14" width="7" height="7" rx="1.5"></rect>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Bảng điều khiển</span>
          </router-link>

          <router-link to="/admin/statistics" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Thống kê' : ''">
            <div class="icon-orb">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M12 20V10"></path><path d="M18 20V4"></path><path d="M6 20v-4"></path>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Thống kê</span>
          </router-link>
        </div>

        <div class="nav-section anim-stagger" style="--d: 0.2s">
          <label class="section-tag" v-if="!isCollapsed">Nội dung</label>
          <div class="section-divider" v-else></div>

          <router-link to="/admin/categories" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Quản lý Danh mục' : ''">
            <div class="icon-orb">
              <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <polygon points="12 2 2 7 12 12 22 7 12 2"></polygon>
                <polyline points="2 12 12 17 22 12"></polyline>
                <polyline points="2 17 12 22 22 17"></polyline>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Quản lý Danh mục</span>
          </router-link>

          <router-link to="/admin/posts" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Quản lý Bài viết' : ''">
            <div class="icon-orb">
              <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Quản lý Bài viết</span>
          </router-link>

          <router-link to="/admin/comments" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Quản lý Bình luận' : ''">
            <div class="icon-orb">
              <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7"></path>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Quản lý Bình luận</span>
          </router-link>

          <router-link to="/admin/events" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Quản lý Sự kiện' : ''">
            <div class="icon-orb">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Quản lý Sự kiện</span>
          </router-link>
        </div>

        <div class="nav-section anim-stagger" style="--d: 0.3s">
          <label class="section-tag" v-if="!isCollapsed">CỘNG ĐỒNG</label>
          <div class="section-divider" v-else></div>

          <router-link to="/admin/users" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Quản lý Người dùng' : ''">
            <div class="icon-orb">
              <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Quản lý Người dùng</span>
          </router-link>

          <router-link to="/admin/tickets" class="nav-link-lux warning-vibe" active-class="active" :title="isCollapsed ? 'Hỗ trợ & Phản hồi (5)' : ''">
            <div class="icon-orb">
              <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Hỗ trợ & Phản hồi</span>
            <span class="count-badge" v-if="!isCollapsed">5</span>
            <span class="count-badge-dot" v-else></span>
          </router-link>

          <router-link to="/admin/appeals" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Xử lý Khiếu nại' : ''">
            <div class="icon-orb">
              <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2m1 15h-2v-2h2v2m0-4h-2V7h2v6z"></path>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Xử lý Khiếu nại</span>
          </router-link>

          <router-link to="/admin/notifications" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Thông báo Hệ thống' : ''">
            <div class="icon-orb">
              <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Thông báo Hệ thống</span>
            <div class="lux-alert-dot"></div>
          </router-link>
        </div>

        <div v-if="isSuperAdmin" class="nav-section anim-stagger" style="--d: 0.4s">
          <label class="section-tag" v-if="!isCollapsed">HỆ THỐNG & BẢO MẬT</label>
          <div class="section-divider" v-else></div>

          <router-link to="/admin/transactions" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Quản lý Giao dịch' : ''">
            <div class="icon-orb">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <rect x="1" y="4" width="22" height="16" rx="2" ry="2"></rect>
                <line x1="1" y1="10" x2="23" y2="10"></line>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Quản lý Giao dịch</span>
          </router-link>

          <router-link to="/admin/blacklist" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Bộ lọc Từ khóa' : ''">
            <div class="icon-orb">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                <line x1="2" y1="2" x2="22" y2="22"></line>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Bộ lọc Từ khóa</span>
          </router-link>

          <router-link to="/admin/system-logs" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Nhật ký Hệ thống' : ''">
            <div class="icon-orb">
              <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10 9 9 9 8 9"></polyline>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Nhật ký Hệ thống</span>
          </router-link>

          <router-link to="/admin/system-settings" class="nav-link-lux" active-class="active" :title="isCollapsed ? 'Vận Hành Hệ Thống' : ''">
            <div class="icon-orb">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <circle cx="12" cy="12" r="3"></circle>
                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
              </svg>
            </div>
            <span class="link-text" v-if="!isCollapsed">Vận Hành Hệ Thống</span>
          </router-link>

        </div>
      </div> 
    </nav> 
    
    <div class="sidebar-footer">
      <button class="btn-exit-admin" @click="$router.push('/home')" :title="isCollapsed ? 'Về Trang Chủ' : ''">
        <div class="btn-content">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
            <polyline points="16 17 21 12 16 7"></polyline>
            <line x1="21" y1="12" x2="9" y2="12"></line>
          </svg>
          <span v-if="!isCollapsed">VỀ TRANG CHỦ</span>
        </div>
        <div class="btn-shine"></div>
      </button>
    </div>
  </aside>
</template>
<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';

// Khai báo sự kiện để báo cho Layout cha (AdminLayout) biết Sidebar đang ở trạng thái nào
const emit = defineEmits(['toggle-collapse']);

const authStore = useAuthStore();

const isSuperAdmin = computed(() => {
  return authStore.user && (String(authStore.user.accountID) === '1' || String(authStore.user.id) === '1');
});

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
  transition: width 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

/* KHI THU GỌN SIDEBAR */
.sidebar-vipro-admin.is-collapsed {
  width: 85px;
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
  0% { transform: translateX(-110%); }
  15%, 100% { transform: translateX(110%); }
}

.brand-name {
  font-family: 'Playfair Display', serif;
  font-weight: 900;
  font-size: 1.5rem;
  margin: 0;
  line-height: 1;
  background: linear-gradient(to bottom, #FFD07F 0%, #FFFFFF 50%, #FF9500 100%);
  -webkit-background-clip: text; 
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
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.4; transform: scale(1.4); }
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
  display: flex; 
  flex-direction: column; 
  gap: 4px;
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

.section-divider {
  height: 2px; background: rgba(255,255,255,0.08); width: 30px; margin: 0 auto 10px; border-radius: 2px;
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
  overflow: hidden;
}

.is-collapsed .nav-link-lux {
  justify-content: center; padding: 12px 0; border-radius: 12px;
}

.nav-link-lux:hover {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
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

.is-collapsed .nav-link-lux.active {
  background: rgba(234, 88, 12, 0.16); box-shadow: none;
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
  display: flex; 
  align-items: center; 
  justify-content: center;
}

.link-text { white-space: nowrap; transition: opacity 0.3s; }

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

.count-badge-dot {
  position: absolute; top: 8px; right: 12px; width: 8px; height: 8px; background: #ef4444; border-radius: 50%; box-shadow: 0 0 10px #ef4444;
}

.lux-alert-dot {
  width: 8px;
  height: 8px;
  background: #fb923c;
  border-radius: 50%;
  margin-left: auto;
  box-shadow: 0 0 12px #fb923c;
}

.is-collapsed .lux-alert-dot { position: absolute; top: 10px; right: 12px; margin: 0; }

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
  display: flex; 
  align-items: center; 
  justify-content: center;
}

.is-collapsed .btn-exit-admin { padding: 0; width: 46px; margin: 0 auto; border-radius: 12px;}

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