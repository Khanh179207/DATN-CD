<template>
  <div class="dashboard-container">
    
    <div class="dashboard-head">
      <div>
        <h2 class="page-title">Tổng Quan Hệ Thống</h2>
        <p class="page-subtitle">Trung tâm điều khiển Gomet Admin</p>
      </div>
      <div class="quick-actions">
        <button class="btn-action">📢 Tạo thông báo</button>
        <button class="btn-action primary">🎉 Thêm sự kiện</button>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card revenue-card">
        <div class="card-icon">💰</div>
        <div class="card-info">
          <span class="label">Tổng Doanh Thu</span>
          <h3 class="value">125.4M ₫</h3>
          <span class="trend up">↗ +12%</span>
        </div>
      </div>
      <div class="stat-card user-card">
        <div class="card-icon">👥</div>
        <div class="card-info">
          <span class="label">Người dùng</span>
          <h3 class="value">5,420</h3>
          <span class="trend up">↗ +35</span>
        </div>
      </div>
      <div class="stat-card post-card">
        <div class="card-icon">📝</div>
        <div class="card-info">
          <span class="label">Bài viết</span>
          <h3 class="value">12,890</h3>
          <span class="trend">Active</span>
        </div>
      </div>
      <div class="stat-card report-card">
        <div class="card-icon">⚠️</div>
        <div class="card-info">
          <span class="label">Vi phạm</span>
          <h3 class="value text-red">15</h3>
          <span class="trend down">Cần xử lý</span>
        </div>
      </div>
    </div>

    <div class="content-grid-2x2">
      
      <div class="section-card">
        <div class="card-header">
          <h3>⏳ Bài Viết Chờ Duyệt</h3>
          <button class="btn-link">Xem tất cả</button>
        </div>
        <div class="list-wrapper">
          <div class="pending-item" v-for="i in 3" :key="i">
            <img :src="`https://source.unsplash.com/random/50x50?food&sig=${i}`" class="thumb">
            <div class="info">
              <h4>Món ngon ngày tết {{ i }}</h4>
              <p>User_{{ i }} • 20 phút trước</p>
            </div>
            <div class="actions">
              <button class="btn-icon check" title="Duyệt">✓</button>
              <button class="btn-icon cross" title="Từ chối">✕</button>
            </div>
          </div>
        </div>
      </div>

      <div class="section-card">
        <div class="card-header">
          <h3>💬 Bình Luận Mới Nhất</h3>
          <button class="btn-link">Quản lý</button>
        </div>
        <div class="list-wrapper">
          <div class="comment-item" v-for="j in 3" :key="j">
            <div class="avatar-circle">{{ String.fromCharCode(65+j) }}</div>
            <div class="info">
              <div class="user-row">
                <span class="username">Nguyễn Văn {{ String.fromCharCode(65+j) }}</span>
                <span class="time">5p trước</span>
              </div>
              <p class="comment-text">Bài viết này rất hữu ích, cảm ơn ad...</p>
            </div>
            <button class="btn-icon delete-sm" title="Xóa bình luận">🗑️</button>
          </div>
        </div>
      </div>

      <div class="section-card">
        <div class="card-header">
          <h3>🏆 Thành Tích Hệ Thống</h3>
          <button class="btn-add-mini">+ Thêm mới</button>
        </div>
        <div class="achievement-grid">
          <div class="achieve-item">
            <span class="achieve-icon">🥇</span>
            <div class="achieve-info">
              <h4>Siêu Đầu Bếp</h4>
              <p>Đăng 50 bài viết</p>
            </div>
            <button class="btn-edit">✏️</button>
          </div>
          <div class="achieve-item">
            <span class="achieve-icon">🔥</span>
            <div class="achieve-info">
              <h4>Hot Creator</h4>
              <p>1000 lượt like</p>
            </div>
            <button class="btn-edit">✏️</button>
          </div>
           <div class="achieve-item">
            <span class="achieve-icon">🌱</span>
            <div class="achieve-info">
              <h4>Tân Binh</h4>
              <p>Tạo tài khoản mới</p>
            </div>
            <button class="btn-edit">✏️</button>
          </div>
        </div>
      </div>

      <div class="section-card">
        <div class="card-header">
          <h3>🚨 Báo Cáo Vi Phạm</h3>
          <span class="badge-alert">Cần xử lý</span>
        </div>
        <table class="simple-table">
          <tbody>
            <tr>
              <td><span class="tag post">Bài viết</span></td>
              <td>Nội dung nhạy cảm</td>
              <td><button class="btn-resolve">Xử lý</button></td>
            </tr>
            <tr>
              <td><span class="tag user">User</span></td>
              <td>Ngôn từ đả kích</td>
              <td><button class="btn-resolve">Xử lý</button></td>
            </tr>
             <tr>
              <td><span class="tag comment">Comment</span></td>
              <td>Quảng cáo spam</td>
              <td><button class="btn-resolve">Xử lý</button></td>
            </tr>
          </tbody>
        </table>
      </div>

    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Quicksand:wght@500;600;700;800&display=swap');

.dashboard-container { font-family: 'Quicksand', sans-serif; }

/* HEADER & STATS (Giữ nguyên style cũ cho đẹp) */
.dashboard-head { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 30px; }
.page-title { font-size: 1.8rem; font-weight: 800; color: #1E293B; margin: 0; }
.page-subtitle { color: #64748B; font-size: 0.95rem; margin-top: 5px; }

.quick-actions { display: flex; gap: 12px; }
.btn-action { padding: 10px 20px; border-radius: 10px; font-weight: 700; border: 1px solid #E2E8F0; background: white; color: #475569; cursor: pointer; transition: 0.2s; }
.btn-action.primary { background: #F97316; color: white; border: none; }
.btn-action:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }

.stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 30px; }
.stat-card { background: white; padding: 20px; border-radius: 16px; display: flex; align-items: center; gap: 15px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid #F1F5F9; }
.card-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; flex-shrink: 0; }
.revenue-card .card-icon { background: #ECFDF5; color: #10B981; }
.user-card .card-icon { background: #EFF6FF; color: #3B82F6; }
.post-card .card-icon { background: #FFF7ED; color: #F97316; }
.report-card .card-icon { background: #FEF2F2; color: #EF4444; }
.card-info .label { font-size: 0.8rem; font-weight: 600; color: #64748B; display: block; margin-bottom: 2px; }
.card-info .value { font-size: 1.4rem; font-weight: 800; color: #0F172A; margin: 0; line-height: 1.2; }
.trend { font-size: 0.75rem; font-weight: 700; }
.trend.up { color: #10B981; } .trend.down { color: #EF4444; } .text-red { color: #EF4444; }

/* --- GRID 2x2 CHO 4 USE CASE --- */
.content-grid-2x2 {
  display: grid; 
  grid-template-columns: 1fr 1fr; /* Chia 2 cột đều nhau */
  gap: 24px;
}
@media (max-width: 1000px) { .content-grid-2x2 { grid-template-columns: 1fr; } } /* Mobile xuống 1 cột */

.section-card { background: white; border-radius: 16px; padding: 20px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid #F1F5F9; display: flex; flex-direction: column; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; padding-bottom: 10px; border-bottom: 1px solid #F1F5F9; }
.card-header h3 { font-size: 1rem; font-weight: 700; color: #1E293B; margin: 0; }
.btn-link { background: none; border: none; color: #3B82F6; font-weight: 600; cursor: pointer; font-size: 0.85rem; }

/* STYLE BÀI VIẾT (Pending Items) */
.list-wrapper { display: flex; flex-direction: column; gap: 12px; }
.pending-item { display: flex; align-items: center; gap: 10px; }
.thumb { width: 40px; height: 40px; border-radius: 8px; object-fit: cover; }
.info { flex: 1; }
.info h4 { font-size: 0.9rem; font-weight: 700; color: #334155; margin: 0; }
.info p { font-size: 0.75rem; color: #94A3B8; margin: 2px 0 0; }
.btn-icon { width: 28px; height: 28px; border-radius: 6px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; font-weight: 700; transition: 0.2s; }
.btn-icon.check { background: #F0FDF4; color: #16A34A; }
.btn-icon.cross { background: #FEF2F2; color: #EF4444; }

/* STYLE BÌNH LUẬN (Comments) */
.comment-item { display: flex; gap: 10px; align-items: flex-start; padding-bottom: 10px; border-bottom: 1px dashed #F1F5F9; }
.comment-item:last-child { border: none; }
.avatar-circle { width: 32px; height: 32px; background: #E0E7FF; color: #4F46E5; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.8rem; flex-shrink: 0; }
.user-row { display: flex; justify-content: space-between; margin-bottom: 2px; }
.username { font-size: 0.85rem; font-weight: 700; color: #334155; }
.time { font-size: 0.7rem; color: #94A3B8; }
.comment-text { font-size: 0.8rem; color: #64748B; margin: 0; line-height: 1.3; }
.delete-sm { background: transparent; color: #94A3B8; }
.delete-sm:hover { background: #FEF2F2; color: #EF4444; }

/* STYLE THÀNH TÍCH (Achievements) */
.btn-add-mini { background: #F97316; color: white; border: none; padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 700; cursor: pointer; }
.achievement-grid { display: flex; flex-direction: column; gap: 10px; }
.achieve-item { display: flex; align-items: center; gap: 10px; background: #F8FAFC; padding: 8px 12px; border-radius: 10px; border: 1px solid #E2E8F0; }
.achieve-icon { font-size: 1.2rem; }
.achieve-info h4 { font-size: 0.85rem; font-weight: 700; margin: 0; color: #334155; }
.achieve-info p { font-size: 0.7rem; color: #64748B; margin: 0; }
.btn-edit { margin-left: auto; background: none; border: none; cursor: pointer; color: #94A3B8; }
.btn-edit:hover { color: #3B82F6; }

/* STYLE BÁO CÁO (Table) */
.badge-alert { background: #FEF2F2; color: #EF4444; font-size: 0.7rem; font-weight: 700; padding: 2px 8px; border-radius: 10px; }
.simple-table { width: 100%; border-collapse: collapse; }
.simple-table td { padding: 8px 0; font-size: 0.85rem; color: #334155; border-bottom: 1px dashed #F1F5F9; }
.tag { font-size: 0.7rem; font-weight: 700; padding: 2px 6px; border-radius: 4px; }
.tag.post { background: #E0F2FE; color: #0284C7; }
.tag.user { background: #F3E8FF; color: #9333EA; }
.tag.comment { background: #FFEDD5; color: #C2410C; }
.btn-resolve { background: #F1F5F9; border: 1px solid #CBD5E1; color: #475569; padding: 2px 8px; border-radius: 4px; font-size: 0.75rem; cursor: pointer; font-weight: 600; }
</style>