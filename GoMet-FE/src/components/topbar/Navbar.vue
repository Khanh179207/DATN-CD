<template>
  <div class="header-container">
    <nav class="main-navbar">
      <div class="container">
        <router-link to="/" class="logo">
          <span class="text-orange">GOMET</span>
        </router-link>

        <div class="search-box">
          <span class="search-icon">🔍</span>
          <input type="text" placeholder="Tìm kiếm công thức..." />
        </div>

        <div class="auth-actions">
          <router-link to="/auth/login" class="btn-login">
            ➜ Đăng nhập / Đăng ký
          </router-link>
        </div>
      </div>
    </nav>

    <div class="sub-navbar">
      <div class="container menu-items">
        <router-link to="/" class="menu-link active">🏠 Trang chủ</router-link>
        <router-link to="/recipes" class="menu-link">🍲 Danh mục</router-link>
        <router-link to="/trending" class="menu-link">📈 Trending</router-link>
        <router-link to="/meal-plan" class="menu-link premium">📅 Meal Planner 👑</router-link>
        <router-link to="/suggestions" class="menu-link premium">💡 Gợi ý món ăn</router-link>
        <router-link to="/ranking" class="menu-link">🏆 Bảng xếp hạng</router-link>
        <router-link to="/admin" class="menu-link admin">⚙️ Admin</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Container chung */
.header-container {
  background: white;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* --- STYLE MAIN NAVBAR --- */
.main-navbar {
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.main-navbar .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 1.8rem;
  font-weight: 900;
  text-decoration: none;
}
.text-orange { color: #F97316; }

.search-box {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 25px;
  padding: 8px 15px;
  width: 40%;
}
.search-icon { margin-right: 10px; color: #aaa; }
.search-box input {
  border: none;
  background: transparent;
  outline: none;
  width: 100%;
  font-size: 14px;
}

.btn-login {
  background-color: #F97316;
  color: white;
  padding: 10px 24px;
  border-radius: 25px;
  text-decoration: none;
  font-weight: bold;
  font-size: 14px;
  transition: background 0.2s;
}
.btn-login:hover { background-color: #ea580c; }

/* --- STYLE SUB-NAVBAR --- */
.sub-navbar {
  padding: 10px 0;
  background: white;
}
.menu-items {
  display: flex;
  gap: 25px;
  overflow-x: auto; /* Cho phép cuộn ngang trên mobile */
}
.menu-link {
  text-decoration: none;
  color: #555;
  font-weight: 500;
  font-size: 14px;
  white-space: nowrap;
  padding-bottom: 5px;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
}
.menu-link:hover, .menu-link.active {
  color: #F97316;
  border-bottom-color: #F97316;
}
.menu-link.premium { color: #d97706; } /* Màu vàng cho Premium */
.menu-link.admin { color: #333; }
</style>