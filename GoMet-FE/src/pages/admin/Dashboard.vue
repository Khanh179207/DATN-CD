<template>
  <div class="dashboard-zenith">
    <div class="dash-hero">
      <div class="hero-left">
        <h2 class="page-title">Trung Tâm Điều Khiển</h2>
        <p class="sub-title">Tổng quan thời gian thực hệ thống Gomet</p>
      </div>
      <div class="hero-stats">
        <div class="mini-stat">
          <span class="lbl">Doanh thu</span>
          <span class="val text-green">125.4M ₫</span>
        </div>
        <div class="divider-v"></div>
        <div class="mini-stat">
          <span class="lbl">Online</span>
          <span class="val text-orange">542</span>
        </div>
      </div>
    </div>

    <div class="bento-grid">
      
      <div class="bento-card col-span-2 post-card">
        <div class="card-header">
          <div class="h-title"><i class="fa-solid fa-file-pen"></i> Bài Viết Chờ Duyệt</div>
          <router-link to="/admin/posts" class="btn-pill">Quản lý ({{ data.posts.length }})</router-link>
        </div>
        <div class="card-body">
          <div v-for="post in data.posts" :key="post.id" class="list-item-row">
            <img :src="post.image" class="item-thumb" />
            <div class="item-details">
              <span class="i-title">{{ post.title }}</span>
              <span class="i-sub">Đăng bởi <b>{{ post.author }}</b> • {{ post.time }}</span>
            </div>
            <div class="item-actions">
              <button class="btn-icon-sm check" title="Duyệt"><i class="fa-solid fa-check"></i></button>
              <button class="btn-icon-sm cross" title="Từ chối"><i class="fa-solid fa-xmark"></i></button>
            </div>
          </div>
        </div>
      </div>

      <div class="bento-card report-card border-red">
        <div class="card-header">
          <div class="h-title text-red"><i class="fa-solid fa-triangle-exclamation"></i> Khiếu Nại</div>
          <span class="count-badge red">{{ data.reports.length }}</span>
        </div>
        <div class="card-body">
          <div v-for="rp in data.reports" :key="rp.id" class="report-row">
            <span class="tag-reason">{{ rp.reason }}</span>
            <div class="rp-target">{{ rp.target }}</div>
            <router-link to="/admin/reports" class="link-arrow">Xử lý →</router-link>
          </div>
        </div>
      </div>

      <div class="bento-card event-card">
        <div class="card-header">
          <div class="h-title"><i class="fa-regular fa-calendar-check"></i> Sự Kiện</div>
          <router-link to="/admin/events" class="icon-link"><i class="fa-solid fa-arrow-right"></i></router-link>
        </div>
        <div class="card-body scroll-y">
          <div v-for="ev in data.events" :key="ev.id" class="event-block">
            <div class="date-box">
              <span class="d">{{ ev.day }}</span><span class="m">{{ ev.month }}</span>
            </div>
            <div class="ev-info">
              <b>{{ ev.name }}</b>
              <div class="progress-bar"><div class="fill" :style="{width: ev.progress + '%'}"></div></div>
            </div>
          </div>
        </div>
      </div>

      <div class="bento-card user-card">
        <div class="card-header">
          <div class="h-title"><i class="fa-solid fa-users"></i> Người Dùng Mới</div>
          <router-link to="/admin/users" class="icon-link"><i class="fa-solid fa-arrow-right"></i></router-link>
        </div>
        <div class="card-body row-flex">
          <div v-for="u in data.users" :key="u.id" class="user-chip">
            <img :src="u.avatar" class="avatar-sm" />
            <div class="u-info">
              <span class="u-name">{{ u.name }}</span>
              <span class="u-role">{{ u.role }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="bento-card comment-card">
        <div class="card-header">
          <div class="h-title"><i class="fa-regular fa-comments"></i> Bình Luận Mới</div>
          <router-link to="/admin/comments" class="btn-pill-xs">Duyệt</router-link>
        </div>
        <div class="card-body">
          <div v-for="cmt in data.comments" :key="cmt.id" class="chat-bubble">
            <span class="c-user">{{ cmt.user }}:</span> {{ cmt.content }}
          </div>
        </div>
      </div>

      <div class="bento-card cat-card">
        <div class="card-header">
          <div class="h-title"><i class="fa-solid fa-list"></i> Danh Mục</div>
          <router-link to="/admin/categories" class="icon-link"><i class="fa-solid fa-pen"></i></router-link>
        </div>
        <div class="card-body tag-cloud">
          <span v-for="cat in data.categories" :key="cat.id" class="tag-pill">{{ cat.name }}</span>
          <span class="tag-pill more">+5</span>
        </div>
      </div>

      <div class="bento-card ach-card">
        <div class="card-header">
          <div class="h-title"><i class="fa-solid fa-medal"></i> Danh Hiệu</div>
          <router-link to="/admin/achievements" class="icon-link"><i class="fa-solid fa-trophy"></i></router-link>
        </div>
        <div class="card-body medal-list">
          <div v-for="ac in data.achievements" :key="ac.id" class="medal-row">
            <span>🥇 {{ ac.user }}</span> <small>{{ ac.title }}</small>
          </div>
        </div>
      </div>

      <div class="bento-card noti-card col-span-2">
        <div class="card-header">
          <div class="h-title"><i class="fa-regular fa-bell"></i> Thông Báo Hệ Thống</div>
          <router-link to="/admin/notifications" class="link-text">Xem lịch sử</router-link>
        </div>
        <div class="card-body h-flex">
          <div v-for="notif in data.notifications" :key="notif.id" class="noti-pill">
            <span class="dot"></span> {{ notif.content }} <span class="time">{{ notif.time }}</span>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const data = reactive({
  posts: [
    { id: 1, title: 'Bò Wellington Thượng Hạng', author: 'Chef Ramsay', time: '10p trước', image: 'https://images.unsplash.com/photo-1544025162-d76694265947?w=100' },
    { id: 2, title: 'Sashimi Cá Hồi Tươi', author: 'SushiMaster', time: '45p trước', image: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=100' },
    { id: 3, title: 'Kỹ thuật làm bánh Macaron', author: 'SweetLife', time: '2h trước', image: 'https://images.unsplash.com/photo-1558326567-98ae2405596b?w=100' }
  ],
  reports: [
    { id: 1, reason: 'Spam QC', target: '@hacker123' },
    { id: 2, reason: 'Ngôn từ', target: 'Cmt #882' }
  ],
  events: [
    { id: 1, name: 'Top Chef 2026', day: '20', month: 'FEB', progress: 70 },
    { id: 2, name: 'Workshop Coffee', day: '25', month: 'FEB', progress: 30 }
  ],
  users: [
    { id: 1, name: 'Minh Tuấn', role: 'Member', avatar: 'https://ui-avatars.com/api/?name=Minh+Tuan&background=random' },
    { id: 2, name: 'Lan Anh', role: 'Chef', avatar: 'https://ui-avatars.com/api/?name=Lan+Anh&background=random' }
  ],
  comments: [
    { id: 1, user: 'An Nhiên', content: 'Công thức tuyệt vời!' },
    { id: 2, user: 'Bình Minh', content: 'Cần hướng dẫn chi tiết hơn...' }
  ],
  categories: [
    { id: 1, name: 'Món Âu' }, { id: 2, name: 'Món Á' }, { id: 3, name: 'Healthy' }, { id: 4, name: 'Bánh ngọt' }
  ],
  achievements: [
    { id: 1, user: 'Tùng', title: 'Bếp Vàng' },
    { id: 2, user: 'Cúc', title: 'Thợ Ảnh' }
  ],
  notifications: [
    { id: 1, content: 'Server bảo trì lúc 02:00 AM', time: 'Hôm nay' },
    { id: 2, content: 'Đã cập nhật chính sách bảo mật', time: 'Hôm qua' }
  ]
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;600;700;800&display=swap');

.dashboard-zenith {
  padding: 25px 40px; font-family: 'Mulish', sans-serif;
  color: #1E293B; min-height: 100vh;
}

/* HERO SECTION */
.dash-hero { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 30px; }
.page-title { font-size: 1.8rem; font-weight: 800; color: #0F172A; margin: 0; }
.sub-title { color: #64748B; margin-top: 4px; font-weight: 500; }
.hero-stats { display: flex; gap: 20px; align-items: center; background: white; padding: 10px 20px; border-radius: 12px; border: 1px solid #E2E8F0; }
.mini-stat { display: flex; flex-direction: column; text-align: right; }
.mini-stat .lbl { font-size: 0.7rem; color: #94A3B8; text-transform: uppercase; font-weight: 700; }
.mini-stat .val { font-size: 1.1rem; font-weight: 800; }
.text-green { color: #10B981; }
.text-orange { color: #F97316; }
.divider-v { width: 1px; height: 30px; background: #E2E8F0; }

/* BENTO GRID LAYOUT */
.bento-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 3 Cột chuẩn Bento */
  gap: 20px;
  grid-auto-rows: minmax(160px, auto);
}

/* CARD COMMON STYLE */
.bento-card {
  background: white; border-radius: 20px; padding: 20px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 4px 20px rgba(0,0,0,0.02);
  display: flex; flex-direction: column;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  position: relative; overflow: hidden;
}
.bento-card:hover { transform: translateY(-3px); box-shadow: 0 10px 30px rgba(0,0,0,0.06); }

/* Column Spans */
.col-span-2 { grid-column: span 2; }

/* Headers */
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.h-title { font-weight: 800; font-size: 1rem; color: #334155; display: flex; align-items: center; gap: 8px; }
.h-title i { color: #94A3B8; }

/* BUTTONS & LINKS */
.btn-pill { background: #EA580C; color: white; padding: 5px 12px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; text-decoration: none; transition: 0.2s; }
.btn-pill:hover { background: #C2410C; }
.btn-pill-xs { background: #F1F5F9; color: #475569; padding: 3px 10px; border-radius: 12px; font-size: 0.7rem; text-decoration: none; font-weight: 700; }
.btn-pill-xs:hover { background: #E2E8F0; color: #0F172A; }
.icon-link { color: #94A3B8; transition: 0.2s; }
.icon-link:hover { color: #EA580C; }
.link-text { font-size: 0.8rem; color: #3B82F6; text-decoration: none; font-weight: 600; }

/* 1. POSTS CARD */
.list-item-row { display: flex; align-items: center; gap: 12px; padding-bottom: 12px; margin-bottom: 12px; border-bottom: 1px dashed #F1F5F9; }
.list-item-row:last-child { border: none; margin: 0; padding: 0; }
.item-thumb { width: 45px; height: 45px; border-radius: 8px; object-fit: cover; }
.item-details { flex: 1; display: flex; flex-direction: column; }
.i-title { font-weight: 700; font-size: 0.92rem; color: #0F172A; }
.i-sub { font-size: 0.75rem; color: #64748B; }
.btn-icon-sm { width: 28px; height: 28px; border-radius: 6px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; margin-left: 5px; }
.check { background: #DCFCE7; color: #16A34A; } .check:hover { background: #16A34A; color: white; }
.cross { background: #FEE2E2; color: #DC2626; } .cross:hover { background: #DC2626; color: white; }

/* 2. REPORTS CARD */
.border-red { border-top: 4px solid #EF4444; }
.text-red { color: #EF4444 !important; }
.count-badge { background: #EF4444; color: white; font-size: 0.7rem; font-weight: 800; padding: 2px 8px; border-radius: 10px; }
.report-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; font-size: 0.85rem; }
.tag-reason { background: #FEF2F2; color: #B91C1C; padding: 2px 6px; border-radius: 4px; font-weight: 700; font-size: 0.7rem; }
.rp-target { font-weight: 600; color: #475569; }
.link-arrow { font-size: 0.75rem; color: #EF4444; text-decoration: none; font-weight: 700; }

/* 3. EVENTS CARD */
.event-block { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; background: #FFF7ED; padding: 8px; border-radius: 10px; }
.date-box { background: white; padding: 2px 8px; border-radius: 6px; text-align: center; color: #EA580C; border: 1px solid #FFEDD5; line-height: 1; }
.date-box .d { font-weight: 800; font-size: 1rem; }
.date-box .m { font-size: 0.6rem; font-weight: 700; display: block; }
.ev-info { flex: 1; }
.ev-info b { font-size: 0.85rem; display: block; margin-bottom: 4px; }
.progress-bar { height: 4px; background: #FFEDD5; border-radius: 2px; overflow: hidden; }
.fill { height: 100%; background: #EA580C; border-radius: 2px; }

/* 4. USERS CARD */
.row-flex { display: flex; flex-direction: column; gap: 10px; }
.user-chip { display: flex; align-items: center; gap: 10px; }
.avatar-sm { width: 32px; height: 32px; border-radius: 50%; }
.u-info { display: flex; flex-direction: column; line-height: 1.2; }
.u-name { font-weight: 700; font-size: 0.85rem; }
.u-role { font-size: 0.7rem; color: #64748B; }

/* 5. COMMENTS CARD */
.chat-bubble { background: #F1F5F9; padding: 8px 12px; border-radius: 10px; font-size: 0.8rem; color: #334155; margin-bottom: 8px; border-left: 3px solid #CBD5E1; }
.c-user { font-weight: 700; color: #0F172A; }

/* 6. CATEGORIES CARD */
.tag-cloud { display: flex; flex-wrap: wrap; gap: 6px; }
.tag-pill { background: #F8FAFC; border: 1px solid #E2E8F0; padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; color: #475569; font-weight: 600; }
.tag-pill.more { background: white; border-style: dashed; color: #94A3B8; }

/* 7. ACHIEVEMENTS CARD */
.medal-list { font-size: 0.85rem; }
.medal-row { display: flex; justify-content: space-between; margin-bottom: 6px; padding: 4px 0; border-bottom: 1px solid #F8FAFC; }
.medal-row small { color: #F59E0B; font-weight: 700; }

/* 8. NOTIFICATIONS CARD */
.h-flex { display: flex; gap: 15px; flex-wrap: wrap; }
.noti-pill { background: #EFF6FF; padding: 6px 12px; border-radius: 8px; font-size: 0.8rem; color: #1E40AF; display: flex; align-items: center; gap: 8px; border: 1px solid #DBEAFE; flex: 1; }
.dot { width: 6px; height: 6px; background: #3B82F6; border-radius: 50%; }
.noti-pill .time { font-size: 0.7rem; opacity: 0.7; margin-left: auto; }

/* RESPONSIVE */
@media (max-width: 1200px) {
  .bento-grid { grid-template-columns: repeat(2, 1fr); }
  .col-span-2 { grid-column: span 2; }
}
@media (max-width: 768px) {
  .bento-grid { grid-template-columns: 1fr; }
  .col-span-2 { grid-column: span 1; }
}
</style>