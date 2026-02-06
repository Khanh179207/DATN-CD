<template>
  <aside class="sidebar-container">
    
    <div class="sidebar-bg"></div>
    <div class="sidebar-noise"></div>

    <div class="sidebar-header">
      <div class="brand-wrapper">
        <div class="logo-box">
          <img src="@/assets/images/gomet.jpg" alt="Logo" class="logo-img" />
        </div>
        <div class="brand-text">
          <h1 class="name">GOMET</h1>
          <span class="badge">ADMIN</span>
        </div>
      </div>
    </div>

    <nav class="sidebar-nav custom-scroll">
      
      <div class="nav-section">
        <div class="section-label">Tổng quan</div>
        <router-link to="/admin/dashboard" class="nav-item" active-class="active">
          <div class="icon-wrap"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg></div>
          <span>Dashboard</span>
        </router-link>
        <router-link to="/admin/statistics" class="nav-item" active-class="active">
          <div class="icon-wrap"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20V10"></path><path d="M18 20V4"></path><path d="M6 20v-4"></path></svg></div>
          <span>Thống kê</span>
        </router-link>
      </div>

      <div class="nav-section">
        <div class="section-label">Quản lý</div>
        <router-link to="/admin/posts" class="nav-item" active-class="active">
          <div class="icon-wrap"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line></svg></div>
          <span>Bài viết</span>
        </router-link>
        <router-link to="/admin/categories" class="nav-item" active-class="active">
          <div class="icon-wrap"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="8" y1="6" x2="21" y2="6"></line><line x1="8" y1="12" x2="21" y2="12"></line><line x1="8" y1="18" x2="21" y2="18"></line><line x1="3" y1="6" x2="3.01" y2="6"></line><line x1="3" y1="12" x2="3.01" y2="12"></line><line x1="3" y1="18" x2="3.01" y2="18"></line></svg></div>
          <span>Danh mục</span>
        </router-link>
        <router-link to="/admin/comments" class="nav-item" active-class="active">
          <div class="icon-wrap"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path></svg></div>
          <span>Bình luận</span>
        </router-link>
      </div>

      <div class="nav-section">
        <div class="section-label">Hệ thống</div>
        <router-link to="/admin/users" class="nav-item" active-class="active">
          <div class="icon-wrap"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg></div>
          <span>Người dùng</span>
        </router-link>
        <router-link to="/admin/reports" class="nav-item warning-item" active-class="active">
          <div class="icon-wrap"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg></div>
          <span>Khiếu nại</span>
          <span class="count-pill">5</span>
        </router-link>
      </div>

    </nav>

    <div class="sidebar-footer">
      <button class="btn-home" @click="$router.push('/home')">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>
        Về trang chủ
      </button>
    </div>

  </aside>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;700;800&family=Plus+Jakarta+Sans:wght@500;600;700&display=swap');

/* --- MAIN CONTAINER (VIBRANT ORANGE) --- */
.sidebar-container {
  width: 270px; height: 100vh;
  position: fixed; top: 0; left: 0; z-index: 100;
  display: flex; flex-direction: column;
  color: white; font-family: 'Plus Jakarta Sans', sans-serif;
  overflow: hidden;
  box-shadow: 4px 0 20px rgba(234, 88, 12, 0.15); /* Bóng đổ cam nhẹ ra ngoài */
}

/* Background Gradient Đặc Trưng */
.sidebar-bg {
  position: absolute; inset: 0; z-index: -2;
  /* Gradient từ Cam sáng đến Cam đỏ đậm */
  background: linear-gradient(160deg, #FB923C 0%, #EA580C 50%, #C2410C 100%);
}
/* Hiệu ứng noise nhẹ (nghệ thuật) */
.sidebar-noise {
  position: absolute; inset: 0; z-index: -1; opacity: 0.03;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)'/%3E%3C/svg%3E");
}

/* --- HEADER --- */
.sidebar-header {
  padding: 30px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.brand-wrapper { display: flex; align-items: center; gap: 12px; }
.logo-box {
  width: 44px; height: 44px; background: white; 
  border-radius: 12px; padding: 3px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}
.logo-img { width: 100%; height: 100%; object-fit: cover; border-radius: 8px; }
.brand-text { display: flex; flex-direction: column; }
.name { 
  font-family: 'Outfit', sans-serif; font-weight: 800; font-size: 1.4rem; 
  line-height: 1; margin: 0; text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.badge {
  font-size: 0.65rem; background: rgba(255,255,255,0.2); 
  padding: 2px 6px; border-radius: 4px; width: fit-content;
  font-weight: 700; margin-top: 4px; letter-spacing: 1px;
}

/* --- NAVIGATION --- */
.sidebar-nav { flex: 1; overflow-y: auto; padding: 20px 16px; }
.nav-section { margin-bottom: 24px; }
.section-label {
  font-size: 0.75rem; font-weight: 800; text-transform: uppercase;
  color: rgba(255, 255, 255, 0.6); margin-bottom: 8px; padding-left: 12px;
  letter-spacing: 1px;
}

/* Nav Item */
.nav-item {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 16px; border-radius: 14px;
  color: rgba(255, 255, 255, 0.9); text-decoration: none;
  font-weight: 600; font-size: 0.95rem;
  transition: all 0.2s cubic-bezier(0.25, 0.8, 0.25, 1);
  margin-bottom: 4px;
}
.icon-wrap { display: flex; align-items: center; }

/* Hover */
.nav-item:hover {
  background: rgba(255, 255, 255, 0.15);
  color: white; transform: translateX(3px);
}

/* ACTIVE STATE (ĐẢO MÀU: Nền Trắng - Chữ Cam) */
.nav-item.active {
  background: #FFFFFF;
  color: #C2410C; /* Màu chữ cam đậm */
  box-shadow: 0 4px 15px rgba(0,0,0,0.15); /* Bóng nổi */
  font-weight: 700;
}
.nav-item.active .icon-wrap { color: #EA580C; } /* Icon cam tươi */

/* Warning/Report Item */
.warning-item:hover { background: rgba(255, 200, 200, 0.2); }
.count-pill {
  margin-left: auto; background: #EF4444; color: white;
  font-size: 0.7rem; font-weight: 800; padding: 2px 8px; 
  border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.2);
}

/* --- FOOTER --- */
.sidebar-footer {
  padding: 20px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}
.btn-home {
  width: 100%; padding: 12px; border-radius: 12px;
  background: rgba(0,0,0,0.2); border: 1px solid rgba(255,255,255,0.1);
  color: white; font-weight: 700; font-family: 'Plus Jakarta Sans', sans-serif;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px;
  transition: 0.2s;
}
.btn-home:hover {
  background: rgba(0,0,0,0.4); border-color: rgba(255,255,255,0.3);
}

/* Scrollbar */
.custom-scroll::-webkit-scrollbar { width: 4px; }
.custom-scroll::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.3); border-radius: 4px; }
.custom-scroll::-webkit-scrollbar-track { background: transparent; }
</style>