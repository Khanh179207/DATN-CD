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
            SOVEREIGN SYSTEM
          </div>
        </div>
      </div>
    </div>

    <nav class="nav-engine-fixed">
      
      <div class="nav-section anim-stagger" style="--d: 0.1s">
        <label class="section-tag">Tổng quan</label>
        <router-link to="/admin/dashboard" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg></div>
          <span>Dashboard</span>
        </router-link>
        <router-link to="/admin/statistics" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M12 20V10"></path><path d="M18 20V4"></path><path d="M6 20v-4"></path></svg></div>
          <span>Thống kê hệ thống</span>
        </router-link>
      </div>

      <div class="nav-section anim-stagger" style="--d: 0.2s">
        <label class="section-tag">Cộng đồng</label>
        <router-link to="/admin/users" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle></svg></div>
          <span>Quản lý Hội Viên</span>
        </router-link>
        <router-link to="/admin/achievements" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><circle cx="12" cy="8" r="7"></circle><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"></polyline></svg></div>
          <span>Vinh Danh Danh Hiệu</span>
        </router-link>
      </div>

      <div class="nav-section anim-stagger" style="--d: 0.3s">
        <label class="section-tag">Nội dung</label>
        <router-link to="/admin/posts" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg></div>
          <span>Duyệt Sáng Tạo</span>
        </router-link>
        <router-link to="/admin/categories" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><line x1="8" y1="6" x2="21" y2="6"></line><line x1="8" y1="12" x2="21" y2="12"></line><line x1="8" y1="18" x2="21" y2="18"></line><circle cx="3" cy="6" r="1.5"></circle><circle cx="3" cy="12" r="1.5"></circle><circle cx="3" cy="18" r="1.5"></circle></svg></div>
          <span>Cấu trúc Danh Mục</span>
        </router-link>
        <router-link to="/admin/comments" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7"></path></svg></div>
          <span>Kiểm duyệt Tương Tác</span>
        </router-link>
      </div>

      <div class="nav-section anim-stagger" style="--d: 0.4s">
        <label class="section-tag">Hệ thống</label>
        <router-link to="/admin/tickets" class="nav-link-lux warning-vibe" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg></div>
          <span>Trung tâm Phản hồi</span>
          <span class="count-badge">5</span>
        </router-link>
        <router-link to="/admin/notifications" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path></svg></div>
          <span>Thông Báo Chung</span>
          <div class="lux-alert-dot"></div>
        </router-link>
      </div>

      <div class="nav-section anim-stagger" style="--d: 0.5s">
        <label class="section-tag">Chiến dịch</label>
        <router-link to="/admin/events" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line></svg></div>
          <span>Quản lý Sự Kiện</span>
        </router-link>
      </div>

      <div class="nav-section anim-stagger" style="--d: 0.6s">
        <label class="section-tag">Tài chính</label>
        <router-link to="/admin/transactions" class="nav-link-lux" active-class="active">
          <div class="icon-orb"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"></rect><line x1="1" y1="10" x2="23" y2="10"></line></svg></div>
          <span>Doanh thu & Hóa đơn</span>
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
/* Đồng bộ Font xịn từ Web chính (Inter & Mulish) */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800;900&family=Playfair+Display:wght@900&display=swap');

/* --- ✨ CORE CONTAINER: NO SCROLL --- */
.sidebar-vipro-sovereign {
  width: 270px; height: 100vh;
  position: fixed; top: 0; left: 0; z-index: 1000;
  display: flex; flex-direction: column;
  color: white; font-family: 'Inter', sans-serif; /* Chuyển sang Inter cho chuyên nghiệp */
  overflow: hidden; /* DỨT ĐIỂM THANH CUỘN */
  box-shadow: 15px 0 50px rgba(0, 0, 0, 0.4); /* Bóng tối hơn */
}

/* 🎭 PHÔNG NỀN DYNAMICS - DEEP CHARCOAL BASE (MÀU THAN CHÌ CỰC TRẦM) */
.bg-vipro-engine { position: absolute; inset: 0; z-index: -1; background: #1a110d; overflow: hidden; }

/* HIỆU ỨNG ÁNH SÁNG HỔ PHÁCH (WARM AMBER MESH) */
.mesh-glow {
  position: absolute; inset: 0;
  background: 
    radial-gradient(circle at 10% 10%, rgba(251, 146, 60, 0.08) 0%, transparent 40%),
    radial-gradient(circle at 90% 90%, rgba(180, 63, 10, 0.1) 0%, transparent 40%),
    #130a06; /* Màu nền than chì tối hơn nữa */
}
.sidebar-noise { 
  position: absolute; inset: 0; opacity: 0.02; 
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E"); 
}

/* 🏷️ HEADER LUXURY (RÚT NGẮN CHIỀU CAO) */
.sidebar-header { height: 90px; padding: 0 24px; display: flex; align-items: center; border-bottom: 1px solid rgba(255, 255, 255, 0.05); flex-shrink: 0; }
.brand-vipro { display: flex; align-items: center; gap: 14px; }

.logo-artifact {
  width: 42px; height: 42px; background: white; border-radius: 10px; 
  padding: 3px; position: relative; overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.3);
}
.logo-img { width: 100%; height: 100%; object-fit: cover; border-radius: 7px; }
.sheen-effect {
  position: absolute; top: -100%; left: -100%; width: 300%; height: 300%;
  background: linear-gradient(45deg, transparent 45%, rgba(255,255,255,0.7) 50%, transparent 55%);
  animation: sheenLoop 7s infinite; /* Chậm lại tí */
}
@keyframes sheenLoop { 0% { transform: translate(0, 0); } 15%, 100% { transform: translate(45%, 45%); } }

.brand-name { font-family: 'Playfair Display', serif; font-weight: 900; font-size: 1.5rem; line-height: 1; margin: 0; letter-spacing: -0.5px; color: #fff; }

.admin-badge-lux {
  display: flex; align-items: center; gap: 6px; font-size: 0.55rem; font-weight: 800;
  background: linear-gradient(90deg, #130a06, #1a110d);
  padding: 4px 10px; border-radius: 5px; border: 1px solid rgba(251, 146, 60, 0.2);
  margin-top: 5px; color: #fb923c; letter-spacing: 1.2px; text-transform: uppercase;
}
.pulse-dot { width: 5px; height: 5px; background: #fb923c; border-radius: 50%; animation: pulse 2.5s infinite; box-shadow: 0 0 10px #fb923c; }
@keyframes pulse { 0% { transform: scale(1); opacity: 1; } 50% { transform: scale(1.6); opacity: 0.4; } 100% { transform: scale(1); opacity: 1; } }

/* 📜 NAVIGATION (DÀN ĐỀU CỰC KỲ CHI TIẾT) */
.nav-engine-fixed {
  flex: 1; 
  display: flex; 
  flex-direction: column; 
  justify-content: space-evenly; /* TỰ ĐỘNG DÀN ĐỀU */
  padding: 5px 15px; /* Giảm padding tí */
}

.nav-section { margin-bottom: 0px; }
.section-tag {
  display: block; font-size: 0.58rem; font-weight: 800; text-transform: uppercase;
  color: rgba(255, 255, 255, 0.4); margin-bottom: 5px; padding-left: 12px;
  letter-spacing: 1.8px; font-family: 'Inter', sans-serif;
}

/* NAV LINK SOVEREIGN (Vẻ đẹp huyền bí) */
.nav-link-lux {
  display: flex; align-items: center; gap: 11px; padding: 8px 12px; border-radius: 10px;
  color: rgba(255, 255, 255, 0.8); text-decoration: none; font-weight: 600; font-size: 0.8rem;
  transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1); margin-bottom: 2px;
  position: relative;
  letter-spacing: 0.2px;
}

/* Hover mượt mà (Glow mờ hổ phách) */
.nav-link-lux:hover { 
  background: rgba(251, 146, 60, 0.03); /* Cam hổ phách cực loãng */
  color: white; 
  transform: translateX(4px); 
  box-shadow: inset 0 0 10px rgba(251, 146, 60, 0.05);
}

/* ACTIVE STATE ĐẲNG CẤP (SATIN GLASS & GOLDEN EDGE) */
.nav-link-lux.active {
  /* Hiệu ứng nền kính satin mờ */
  background: rgba(255, 255, 255, 0.03); 
  backdrop-filter: blur(5px);
  color: #fb923c; /* Màu hổ phách hổ虎 hổ hổ虎 tiger */
  font-weight: 800;
  box-shadow: 10px 10px 30px rgba(0, 0, 0, 0.2);
}

/* Dải lụa Vàng hổ phách dọc bên trái */
.nav-link-lux.active::before {
  content: ''; position: absolute; left: 0; top: 15%; height: 70%; width: 4px;
  background: #fb923c; border-radius: 0 4px 4px 0;
  box-shadow: 0 0 15px #fb923c;
}

.icon-orb { color: rgba(255, 255, 255, 0.6); transition: 0.4s; }
.nav-link-lux:hover .icon-orb { color: #fb923c; }
.nav-link-lux.active .icon-orb { 
  color: #fb923c; 
  filter: drop-shadow(0 0 6px rgba(251, 146, 60, 0.5)); 
}

/* Alert dot standard */
.lux-alert-dot { width: 6px; height: 6px; background: #fff; border-radius: 50%; margin-left: auto; box-shadow: 0 0 10px white; opacity: 0.6; }
.nav-link-lux.active .lux-alert-dot { background: #fb923c; box-shadow: 0 0 10px #fb923c; opacity: 1; }
.count-badge { margin-left: auto; background: rgba(0, 0, 0, 0.3); color: #fff; font-size: 0.6rem; font-weight: 800; padding: 2px 7px; border-radius: 6px; border: 1px solid rgba(255,255,255,0.05); }

/* 👑 FOOTER TỐI ƯU (CỰC THẤP) */
.sidebar-footer { height: 65px; padding: 0 18px; border-top: 1px solid rgba(255, 255, 255, 0.05); flex-shrink: 0; display: flex; align-items: center; }

/* NÚT THOÁT - GOLD AMBER BLOCK (KHỐI HỔ PHÁCH VÀNG) */
.btn-exit-lux {
  width: 100%; height: 38px; border-radius: 10px; border: none;
  /* Chuyển sang tông vàng虎虎 hổ hổ hổ */
  background: #ea580c; 
  color: white; cursor: pointer;
  display: flex; align-items: center; justify-content: center; gap: 9px;
  font-weight: 800; font-size: 0.75rem; letter-spacing: 1px;
  position: relative; overflow: hidden; 
  transition: 0.4s cubic-bezier(0.19, 1, 0.22, 1);
  box-shadow: 0 4px 15px rgba(234, 88, 12, 0.3);
}
.btn-exit-lux:hover { 
  background: white; color: #ea580c; 
  transform: translateY(-3px) scale(1.02); 
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}
.btn-glow {
  position: absolute; inset: 0; background: linear-gradient(135deg, transparent, rgba(255,255,255,0.3), transparent);
  transform: translateX(-100%); transition: 0.7s;
}
.btn-exit-lux:hover .btn-glow { transform: translateX(100%); }

/* 🎐 ANIMATIONS */
.anim-stagger {
  opacity: 0; transform: translateX(-15px);
  animation: slideInLux 0.7s cubic-bezier(0.19, 1, 0.22, 1) forwards;
  animation-delay: var(--d);
}
@keyframes slideInLux { to { opacity: 1; transform: translateX(0); } }
</style>