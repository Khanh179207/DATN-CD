<template>
  <aside class="sidebar-wrapper">
    <div class="sidebar-logo">
      <span style="font-size: 24px;">🍳</span>
      <span style="font-weight: 800; font-size: 22px; color: #F97316;">GOMET</span>
    </div>

    <nav class="sidebar-nav">
      <div class="nav-item active">🔍 Tìm kiếm</div>
      <div class="nav-item">👑 Premium</div>
      <div class="nav-item">📊 Thống Kê Bếp</div>
      <div class="nav-item">🏆 Thử Thách</div>
      <div class="nav-item">📖 Kho Món Ngon</div>
    </nav>
  </aside>
</template>

<style scoped>
.sidebar-wrapper {
  width: 250px; /* Chiều rộng cố định của menu trái */
  height: 100vh;
  position: sticky;
  top: 0;
  border-right: 1px solid #eee;
  padding: 20px;
  background: white;
  flex-shrink: 0; /* Không bị co lại */
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 30px;
  padding-left: 10px;
}

.nav-item {
  padding: 12px 15px;
  color: #4b5563;
  font-weight: 600;
  cursor: pointer;
  border-radius: 8px;
  margin-bottom: 5px;
}

.nav-item:hover, .nav-item.active {
  background-color: #fff7ed;
  color: #F97316;
}
</style>