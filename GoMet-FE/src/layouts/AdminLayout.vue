<template>
  <div class="admin-container">
    
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <span class="logo-icon">🛡️</span>
        <div class="logo-text">
          <span>GOMET</span>
          <span class="badge-admin">ADMIN</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <div class="nav-section-label">TỔNG QUAN</div>
        <router-link to="/admin" class="nav-item">
          <span class="icon">📊</span> Dashboard
        </router-link>

        <div class="nav-section-label">QUẢN LÝ NỘI DUNG</div>
        <router-link to="/admin/posts" class="nav-item">
          <span class="icon">📝</span> Bài Đăng
        </router-link>
        <router-link to="/admin/categories" class="nav-item">
          <span class="icon">📂</span> Danh Mục
        </router-link>
        <router-link to="/admin/events" class="nav-item">
          <span class="icon">🎉</span> Sự Kiện & Campaign
        </router-link>

        <div class="nav-section-label">NGƯỜI DÙNG</div>
        <router-link to="/admin/users" class="nav-item">
          <span class="icon">👥</span> Tài Khoản
        </router-link>
        <router-link to="/admin/reports" class="nav-item warning-item">
          <span class="icon">⚠️</span> Báo Cáo Vi Phạm
          <span class="count-badge">5</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <button @click="$router.push('/home')" class="btn-back">
          <span class="icon">🏠</span> Về trang chủ
        </button>
      </div>
    </aside>

    <main class="main-wrapper">
      <header class="admin-header">
        <div class="header-search">
          <span class="search-icon">🔍</span>
          <input type="text" placeholder="Tìm kiếm người dùng, bài viết..." />
        </div>

        <div class="header-actions">
          <button class="action-btn">🔔 <span class="dot"></span></button>
          <div class="admin-profile">
            <div class="text-right">
              <div class="name">Khánh Admin</div>
              <div class="role">Super Admin</div>
            </div>
            <img src="https://ui-avatars.com/api/?name=Khanh+Nguyen&background=F97316&color=fff" alt="Admin">
          </div>
        </div>
      </header>

      <div class="content-body">
        <router-view />
      </div>
    </main>

  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Quicksand:wght@500;600;700;800&display=swap');

.admin-container {
  display: flex; min-height: 100vh;
  background: #F8FAFC; /* Màu nền xám xanh nhạt đồng bộ web */
  font-family: 'Quicksand', sans-serif;
}

/* SIDEBAR */
.admin-sidebar {
  width: 260px; background: #0F172A; /* Slate 900 - Tối và sang */
  color: #94A3B8; display: flex; flex-direction: column; flex-shrink: 0;
  transition: 0.3s;
}

.sidebar-header {
  height: 80px; display: flex; align-items: center; padding: 0 24px; gap: 12px;
  border-bottom: 1px solid #1E293B;
}
.logo-icon { font-size: 28px; }
.logo-text { display: flex; flex-direction: column; line-height: 1; }
.logo-text span:first-child { color: white; font-weight: 800; font-size: 1.2rem; letter-spacing: 1px; }
.badge-admin { font-size: 0.65rem; background: #F97316; color: white; padding: 2px 6px; border-radius: 4px; width: fit-content; margin-top: 4px; font-weight: 700; }

.sidebar-nav { flex: 1; padding: 24px 16px; overflow-y: auto; }
.nav-section-label { font-size: 0.75rem; font-weight: 700; color: #475569; margin: 20px 0 10px 10px; letter-spacing: 0.5px; }

.nav-item {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 16px; border-radius: 12px; /* Bo góc mềm mại */
  color: #CBD5E1; text-decoration: none; font-weight: 600;
  margin-bottom: 4px; transition: all 0.2s;
}
.nav-item:hover { background: #1E293B; color: white; transform: translateX(5px); }
.nav-item.router-link-active { background: #F97316; color: white; box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3); }

.warning-item .count-badge { background: #EF4444; color: white; font-size: 0.7rem; padding: 2px 6px; border-radius: 10px; margin-left: auto; }

.sidebar-footer { padding: 20px; border-top: 1px solid #1E293B; }
.btn-back { width: 100%; padding: 12px; background: #1E293B; border: none; color: white; border-radius: 12px; font-weight: 600; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px; transition: 0.2s; }
.btn-back:hover { background: #334155; }

/* MAIN CONTENT */
.main-wrapper { flex: 1; display: flex; flex-direction: column; overflow: hidden; }

.admin-header {
  height: 80px; background: white; padding: 0 40px;
  display: flex; align-items: center; justify-content: space-between;
  border-bottom: 1px solid #F1F5F9;
}

.header-search {
  background: #F1F5F9; padding: 10px 20px; border-radius: 30px;
  display: flex; align-items: center; gap: 10px; width: 400px;
}
.header-search input { border: none; background: transparent; outline: none; width: 100%; font-family: inherit; font-weight: 600; color: #334155; }

.header-actions { display: flex; align-items: center; gap: 24px; }
.action-btn { background: white; border: 1px solid #E2E8F0; width: 40px; height: 40px; border-radius: 50%; cursor: pointer; position: relative; font-size: 1.1rem; }
.action-btn .dot { position: absolute; top: 8px; right: 10px; width: 8px; height: 8px; background: #EF4444; border-radius: 50%; border: 2px solid white; }

.admin-profile { display: flex; align-items: center; gap: 12px; cursor: pointer; }
.admin-profile .name { font-weight: 700; color: #0F172A; font-size: 0.95rem; }
.admin-profile .role { font-size: 0.75rem; color: #64748B; }
.admin-profile img { width: 42px; height: 42px; border-radius: 50%; border: 2px solid #F1F5F9; }

.content-body { flex: 1; overflow-y: auto; padding: 30px 40px; }
</style>