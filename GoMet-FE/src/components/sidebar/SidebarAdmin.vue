<template>
  <aside class="admin-sidebar">
    <div class="sidebar-header">
      <div class="logo-area">
        <img src="@/assets/images/gomet.jpg" alt="Gomet" class="brand-logo" />
        <div class="logo-text">
          <span class="brand-text">GOMET</span>
          <span class="badge-admin">ADMIN</span>
        </div>
      </div>
    </div>

    <nav class="sidebar-nav">
      <router-link to="/admin/dashboard" class="nav-item" active-class="active">
        <svg class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
        Dashboard
      </router-link>

      <router-link to="/admin/statistics" class="nav-item" active-class="active">
        <svg class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline></svg>
        Thống Kê
      </router-link>

      <div class="section-label">QUẢN LÝ NỘI DUNG</div>
      
      <router-link to="/admin/posts" class="nav-item" active-class="active">
        <svg class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 1 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
        Bài Đăng
      </router-link>
      
      <router-link to="/admin/comments" class="nav-item" active-class="active">
        <svg class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
        Bình Luận
      </router-link>
      
      <router-link to="/admin/categories" class="nav-item" active-class="active">
        <svg class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path></svg>
        Danh Mục
      </router-link>

      <div class="section-label">VẬN HÀNH</div>

      <router-link to="/admin/events" class="nav-item" active-class="active">
        <svg class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
        Sự Kiện
      </router-link>

      <router-link to="/admin/achievements" class="nav-item" active-class="active">
        <svg class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"></path><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"></path><path d="M4 22h16"></path><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"></path><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"></path><path d="M18 2H6v7a6 6 0 0 0 12 0V2z"></path></svg>
        Thành Tích
      </router-link>

      <div class="section-label">NGƯỜI DÙNG & HỖ TRỢ</div>

      <router-link to="/admin/users" class="nav-item" active-class="active">
        <svg class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
        Tài Khoản
      </router-link>

      <router-link to="/admin/reports" class="nav-item warning-item" active-class="active">
        <svg class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
        Báo Cáo & Hòm Thư
        <span class="count-badge">5</span>
      </router-link>

      <div class="section-label">HỆ THỐNG</div>

      <router-link to="/admin/notifications" class="nav-item" active-class="active">
        <svg class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
        Thông Báo
      </router-link>
    </nav>

    <div class="sidebar-footer">
      <button @click="$router.push('/home')" class="btn-back">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>
        Về trang chủ
      </button>
    </div>
  </aside>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;800&family=Quicksand:wght@500;600;700;800&display=swap');

.admin-sidebar { 
  width: 270px; height: 100vh; background: #FFFFFF; border-right: 1px solid #F3F4F6; 
  display: flex; flex-direction: column; padding: 24px; position: fixed; 
  left: 0; top: 0; z-index: 100; font-family: 'Quicksand', sans-serif; color: #57534E; 
}

/* --- LOGO AREA --- */
.sidebar-header { margin-bottom: 30px; height: 56px; display: flex; align-items: center; }
.logo-area { display: flex; align-items: center; gap: 12px; }
.brand-logo { width: 42px; height: 42px; border-radius: 12px; object-fit: cover; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.brand-text { font-family: 'Playfair Display', serif; font-size: 20px; font-weight: 800; color: #1C1917; letter-spacing: 0.5px; }
.badge-admin { font-size: 0.6rem; background: #F97316; color: white; padding: 3px 8px; border-radius: 6px; font-weight: 800; margin-top: 4px; letter-spacing: 1px; }

/* --- NỔI BẬT TIÊU ĐỀ (SECTION LABELS) --- */
.section-label { 
  font-size: 0.75rem; 
  font-weight: 800; 
  color: #1C1917; /* Đổi từ xám sang đen đậm để nổi bật */
  text-transform: uppercase; 
  letter-spacing: 1.5px; /* Giãn chữ tạo cảm giác sang trọng */
  margin: 28px 0 12px 0; /* Tăng khoảng cách để phân đoạn rõ ràng */
  padding-left: 12px;
  position: relative;
  display: flex;
  align-items: center;
}

/* Thêm vạch kẻ nhỏ bên cạnh tiêu đề để tạo điểm nhấn VIP */
.section-label::before {
  content: "";
  width: 3px;
  height: 12px;
  background: #F97316; /* Màu cam Gomet */
  border-radius: 10px;
  margin-right: 8px;
}

/* --- NAVIGATION ITEMS --- */
.sidebar-nav { flex: 1; display: flex; flex-direction: column; gap: 4px; overflow-y: auto; }
.nav-item { 
  display: flex; align-items: center; gap: 12px; padding: 12px 16px; 
  color: #57534E; text-decoration: none; border-radius: 12px; 
  transition: all 0.25s ease; font-weight: 600; font-size: 0.95rem; 
}
.icon-v { width: 18px; height: 18px; color: #A8A29E; transition: all 0.25s; }

.nav-item:hover { background-color: #F9FAFB; color: #1C1917; }
.nav-item:hover .icon-v { color: #F97316; transform: translateX(2px); }

/* TRẠNG THÁI ACTIVE ĐẲNG CẤP */
.nav-item.active { 
  background-color: #1C1917; 
  color: #FFFFFF; 
  box-shadow: 0 10px 25px -8px rgba(28,25,23,0.35); 
}
.nav-item.active .icon-v { color: #F97316; }

/* --- FOOTER & BADGE --- */
.warning-item .count-badge { background: #EF4444; color: white; font-size: 0.7rem; padding: 2px 8px; border-radius: 20px; margin-left: auto; font-weight: 800; }
.sidebar-footer { padding-top: 20px; border-top: 1px solid #F3F4F6; }
.btn-back { 
  width: 100%; padding: 12px; border-radius: 12px; border: 1.5px solid #E7E5E4; 
  background: #FFFFFF; color: #1C1917; font-weight: 700; cursor: pointer; 
  display: flex; align-items: center; justify-content: center; gap: 8px; transition: 0.2s; 
}
.btn-back:hover { border-color: #F97316; color: #F97316; background: #FFF7ED; }

/* Ẩn thanh cuộn */
.sidebar-nav::-webkit-scrollbar { display: none; }
.sidebar-nav { -ms-overflow-style: none; scrollbar-width: none; }
</style>