<template>
  <aside class="sidebar-vipro-sovereign">
    <div class="bg-vipro-engine">
      <div class="mesh-glow"></div>
      <div class="sidebar-noise"></div>
    </div>

    <div class="sidebar-header">
      <div class="brand-vipro">
        <div class="logo-artifact">
          <img src="@/assets/images/gomet.jpg" alt="Logo" class="logo-img" />
          <div class="sheen-effect"></div>
        </div>
        <div class="brand-info">
          <h1 class="brand-name">GOMET</h1>
          <div class="admin-badge-lux">
            <span class="pulse-dot"></span>
            ADMIN SYSTEM
          </div>
        </div>
      </div>
    </div>

    <nav class="nav-engine-fixed">
      
      <div class="nav-section anim-stagger" style="--d: 0.1s">
        <label class="section-tag">Tổng quan</label>
        <router-link to="/admin/dashboard" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg></div>
          <span>Dashboard</span>
        </router-link>
        <router-link to="/admin/statistics" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 20V10"></path><path d="M18 20V4"></path><path d="M6 20v-4"></path></svg></div>
          <span>Thống kê hệ thống</span>
        </router-link>
      </div>

      <div class="nav-section anim-stagger" style="--d: 0.2s">
        <label class="section-tag">Nội dung</label>
        <router-link to="/admin/posts" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg></div>
          <span>Quản lý Bài Viết</span>
        </router-link>
        <router-link to="/admin/categories" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="8" y1="6" x2="21" y2="6"></line><line x1="8" y1="12" x2="21" y2="12"></line><line x1="8" y1="18" x2="21" y2="18"></line><circle cx="3" cy="6" r="1.5"></circle><circle cx="3" cy="12" r="1.5"></circle><circle cx="3" cy="18" r="1.5"></circle></svg></div>
          <span>Quản lý Danh Mục</span>
        </router-link>
        <router-link to="/admin/comments" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7"></path></svg></div>
          <span>Quản lý Bình luận</span>
        </router-link>
      </div>

      <div class="nav-section anim-stagger" style="--d: 0.3s">
        <label class="section-tag">Vận hành</label>
        <router-link to="/admin/events" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line></svg></div>
          <span>Quản lý sự kiện</span>
        </router-link>
        <router-link to="/admin/notifications" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path></svg></div>
          <span>Quản lý Thông Báo</span>
          <div class="lux-alert-dot"></div>
        </router-link>
        <router-link to="/admin/achievements" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="8" r="7"></circle><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"></polyline></svg></div>
          <span>Quản lý Danh Hiệu</span>
        </router-link>
      </div>

      <div class="nav-section anim-stagger" style="--d: 0.4s">
        <label class="section-tag">Hệ thống</label>
        <router-link to="/admin/users" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle></svg></div>
          <span>Quản lý Người Dùng</span>
        </router-link>
        <router-link to="/admin/reports" class="nav-link-lux warning-vibe" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path></svg></div>
          <span>Quản lý Khiếu Nại</span>
          <span class="count-badge">5</span>
        </router-link>
      </div>

    </nav>

    <div class="sidebar-footer">
      <button class="btn-exit-lux" @click="$router.push('/home')">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path></svg>
        <span>VỀ TRANG KHÁCH</span>
        <div class="btn-glow"></div>
      </button>
    </div>
  </aside>
</template>

<style scoped>
/* 1. ĐỒNG BỘ FONT ĐẲNG CẤP TỪ WEB CHỦ */
/* --- ✨ CORE CONTAINER: NO SCROLL --- */
.sidebar-vipro-sovereign {
  width: 270px; height: 100vh;
  position: fixed; top: 0; left: 0; z-index: 1000;
  display: flex; flex-direction: column;
  color: white; font-family: 'Mulish', sans-serif;
  overflow: hidden; /* DỨT ĐIỂM THANH CUỘN */
  box-shadow: 12px 0 45px rgba(0, 0, 0, 0.15);
}

/* 🎭 PHÔNG NỀN DYNAMICS */
.bg-vipro-engine { position: absolute; inset: 0; z-index: -1; background: #EA580C; overflow: hidden; }
.mesh-glow {
  position: absolute; inset: 0;
  background: linear-gradient(165deg, #FB923C 0%, #EA580C 40%, #B43F0A 100%);
}
.sidebar-noise { 
  position: absolute; inset: 0; opacity: 0.04; 
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.7'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E"); 
}

/* 🏷️ HEADER NGHỆ THUẬT */
.sidebar-header { height: 105px; padding: 0 26px; display: flex; align-items: center; border-bottom: 1px solid rgba(255, 255, 255, 0.08); flex-shrink: 0; }
.brand-vipro { display: flex; align-items: center; gap: 15px; }

.logo-artifact {
  width: 48px; height: 48px; background: white; border-radius: 14px; 
  padding: 3px; position: relative; overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}
.logo-img { width: 100%; height: 100%; object-fit: cover; border-radius: 10px; }
.sheen-effect {
  position: absolute; top: -100%; left: -100%; width: 300%; height: 300%;
  background: linear-gradient(45deg, transparent 45%, rgba(255,255,255,0.7) 50%, transparent 55%);
  animation: sheenLoop 6s infinite;
}
@keyframes sheenLoop { 0% { transform: translate(0, 0); } 15%, 100% { transform: translate(45%, 45%); } }

.brand-name { font-family: 'Playfair Display', serif; font-weight: 900; font-size: 1.6rem; line-height: 1; margin: 0; letter-spacing: 0.5px; }
.admin-badge-lux {
  display: flex; align-items: center; gap: 6px; font-size: 0.6rem; font-weight: 900;
  background: rgba(0, 0, 0, 0.2); padding: 4px 10px; border-radius: 6px;
  margin-top: 6px; color: rgba(255, 255, 255, 0.85); letter-spacing: 1.5px;
}
.pulse-dot { width: 6px; height: 6px; background: #fff; border-radius: 50%; animation: pulse 2s infinite; }
@keyframes pulse { 0% { transform: scale(1); opacity: 1; } 50% { transform: scale(1.6); opacity: 0.4; } 100% { transform: scale(1); opacity: 1; } }

/* 📜 NAVIGATION (DÀN ĐỀU THÔNG MINH) */
.nav-engine-fixed {
  flex: 1; 
  display: flex; 
  flex-direction: column; 
  justify-content: space-evenly; /* TỰ ĐỘNG DÀN ĐỀU */
  padding: 10px 18px;
}

.nav-section { margin-bottom: 2px; }
.section-tag {
  display: block; font-size: 0.65rem; font-weight: 900; text-transform: uppercase;
  color: rgba(255, 255, 255, 0.45); margin-bottom: 10px; padding-left: 14px;
  letter-spacing: 2px;
}

/* NAV LINK LUXURY */
.nav-link-lux {
  display: flex; align-items: center; gap: 14px; padding: 11px 16px; border-radius: 14px;
  color: rgba(255, 255, 255, 0.85); text-decoration: none; font-weight: 700; font-size: 0.9rem;
  transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1); margin-bottom: 3px;
  position: relative;
}

/* Hover mượt mà */
.nav-link-lux:hover { background: rgba(255, 255, 255, 0.1); color: white; transform: translateX(6px); }

/* ACTIVE STATE ĐẲNG CẤP */
.nav-link-lux.active {
  background: #FFFFFF; color: #EA580C; font-weight: 900;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}
.nav-link-lux.active .icon-orb { color: #EA580C; filter: drop-shadow(0 2px 4px rgba(234, 88, 12, 0.2)); }

.lux-alert-dot { width: 7px; height: 7px; background: #fff; border-radius: 50%; margin-left: auto; box-shadow: 0 0 10px white; }
.nav-link-lux.active .lux-alert-dot { background: #EA580C; box-shadow: none; }
.count-badge { margin-left: auto; background: #000; color: #fff; font-size: 0.65rem; font-weight: 900; padding: 2px 8px; border-radius: 8px; }

/* 👑 FOOTER TỐI ƯU */
.sidebar-footer { height: 90px; padding: 0 24px; border-top: 1px solid rgba(255, 255, 255, 0.08); flex-shrink: 0; display: flex; align-items: center; }

.btn-exit-lux {
  width: 100%; height: 48px; border-radius: 15px; border: none;
  background: rgba(0, 0, 0, 0.2); color: white; cursor: pointer;
  display: flex; align-items: center; justify-content: center; gap: 10px;
  font-weight: 800; font-size: 0.85rem; letter-spacing: 1px;
  position: relative; overflow: hidden; transition: 0.4s;
}
.btn-exit-lux:hover { background: #fff; color: #EA580C; transform: translateY(-4px); }
.btn-glow {
  position: absolute; inset: 0; background: linear-gradient(135deg, transparent, rgba(255,255,255,0.15), transparent);
  transform: translateX(-100%); transition: 0.6s;
}
.btn-exit-lux:hover .btn-glow { transform: translateX(100%); }

/* 🎐 ANIMATIONS */
.anim-stagger {
  opacity: 0; transform: translateX(-20px);
  animation: slideInLux 0.7s cubic-bezier(0.19, 1, 0.22, 1) forwards;
  animation-delay: var(--d);
}
@keyframes slideInLux { to { opacity: 1; transform: translateX(0); } }
</style>