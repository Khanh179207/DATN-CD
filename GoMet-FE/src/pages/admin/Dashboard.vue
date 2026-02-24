<template>
  <div class="dashboard-zenith-master">
    
    <div class="ambient-canvas">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="grid-texture"></div>
    </div>

    <div class="dash-hero animate-enter">
      <div class="hero-left">
        <h2 class="page-title">Trung Tâm Điều Khiển</h2>
        <p class="sub-title">Tổng quan thời gian thực hệ thống Gomet • <span class="live-pulse">LIVE</span></p>
      </div>
      <div class="hero-stats glass-card-solid">
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
      
      <div class="bento-card col-span-2 post-card glass-card animate-stagger" style="--i:1">
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

      <div class="bento-card report-card border-red glass-card animate-stagger" style="--i:2">
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

      <div class="bento-card event-card glass-card animate-stagger" style="--i:3">
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

      <div class="bento-card user-card glass-card animate-stagger" style="--i:4">
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

      <div class="bento-card comment-card glass-card animate-stagger" style="--i:5">
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

      <div class="bento-card cat-card glass-card animate-stagger" style="--i:6">
        <div class="card-header">
          <div class="h-title"><i class="fa-solid fa-list"></i> Danh Mục</div>
          <router-link to="/admin/categories" class="icon-link"><i class="fa-solid fa-pen"></i></router-link>
        </div>
        <div class="card-body tag-cloud">
          <span v-for="cat in data.categories" :key="cat.id" class="tag-pill">{{ cat.name }}</span>
          <span class="tag-pill more">+5</span>
        </div>
      </div>

      <div class="bento-card ach-card glass-card animate-stagger" style="--i:7">
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

      <div class="bento-card noti-card col-span-2 glass-card animate-stagger" style="--i:8">
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
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;600;700;800;900&display=swap');

.dashboard-zenith-master {
  padding: 30px 40px; font-family: 'Mulish', sans-serif;
  color: #1E293B; min-height: 100vh; position: relative;
  overflow: hidden;
}

/* --- 🌌 AMBIENT CANVAS (Nền động) --- */
.ambient-canvas { position: absolute; inset: 0; z-index: 0; pointer-events: none; }
.orb { position: absolute; border-radius: 50%; filter: blur(90px); opacity: 0.5; animation: floatOrb 20s infinite alternate ease-in-out; }
.orb-1 { width: 500px; height: 500px; background: rgba(251, 146, 60, 0.25); top: -100px; left: -100px; } /* Cam nhạt */
.orb-2 { width: 400px; height: 400px; background: rgba(147, 197, 253, 0.25); bottom: -100px; right: -100px; animation-duration: 25s; } /* Xanh nhạt */
.grid-texture {
  position: absolute; inset: 0; opacity: 0.2;
  background-image: linear-gradient(rgba(0,0,0,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(0,0,0,0.03) 1px, transparent 1px);
  background-size: 30px 30px;
}
@keyframes floatOrb { from { transform: translate(0, 0); } to { transform: translate(40px, 40px); } }

/* HERO SECTION */
.dash-hero { position: relative; z-index: 2; display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 35px; }
.page-title { font-size: 2.2rem; font-weight: 900; color: #0F172A; margin: 0; letter-spacing: -0.5px; }
.sub-title { color: #64748B; margin-top: 4px; font-weight: 600; }
.live-pulse { color: #EF4444; font-weight: 800; animation: blink 2s infinite; }
@keyframes blink { 50% { opacity: 0.5; } }

.glass-card-solid {
  display: flex; gap: 20px; align-items: center; 
  background: rgba(255, 255, 255, 0.7); backdrop-filter: blur(20px);
  padding: 12px 25px; border-radius: 16px; border: 1px solid rgba(255,255,255,0.8);
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
}
.mini-stat { display: flex; flex-direction: column; text-align: right; }
.mini-stat .lbl { font-size: 0.7rem; color: #94A3B8; text-transform: uppercase; font-weight: 800; }
.mini-stat .val { font-size: 1.2rem; font-weight: 900; }
.text-green { color: #10B981; }
.text-orange { color: #F97316; }
.divider-v { width: 1px; height: 30px; background: rgba(0,0,0,0.08); }

/* BENTO GRID LAYOUT */
.bento-grid {
  position: relative; z-index: 2;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  grid-auto-rows: minmax(180px, auto);
}

/* GLASS CARD COMMON */
.glass-card {
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(25px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 20px; padding: 22px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
  display: flex; flex-direction: column;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.glass-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.85);
}

.col-span-2 { grid-column: span 2; }

/* Headers */
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
.h-title { font-weight: 800; font-size: 1.05rem; color: #1E293B; display: flex; align-items: center; gap: 10px; }
.h-title i { color: #94A3B8; }

/* BUTTONS & LINKS */
.btn-pill { background: #EA580C; color: white; padding: 6px 14px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; text-decoration: none; transition: 0.2s; box-shadow: 0 4px 10px rgba(234, 88, 12, 0.2); }
.btn-pill:hover { background: #C2410C; transform: translateY(-2px); }
.btn-pill-xs { background: #F1F5F9; color: #475569; padding: 4px 12px; border-radius: 12px; font-size: 0.75rem; text-decoration: none; font-weight: 700; transition: 0.2s; }
.btn-pill-xs:hover { background: #E2E8F0; color: #0F172A; }
.icon-link { color: #94A3B8; transition: 0.2s; background: rgba(0,0,0,0.03); width: 30px; height: 30px; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.icon-link:hover { color: #EA580C; background: rgba(234, 88, 12, 0.1); }
.link-text { font-size: 0.8rem; color: #3B82F6; text-decoration: none; font-weight: 700; transition: 0.2s; }
.link-text:hover { color: #2563EB; text-decoration: underline; }

/* 1. POSTS CARD */
.list-item-row { display: flex; align-items: center; gap: 14px; padding: 12px; border-radius: 12px; margin-bottom: 10px; background: rgba(255,255,255,0.5); transition: 0.2s; border: 1px solid transparent; }
.list-item-row:hover { background: white; border-color: rgba(0,0,0,0.05); }
.item-thumb { width: 50px; height: 50px; border-radius: 10px; object-fit: cover; }
.item-details { flex: 1; display: flex; flex-direction: column; }
.i-title { font-weight: 800; font-size: 0.95rem; color: #0F172A; margin-bottom: 4px; }
.i-sub { font-size: 0.75rem; color: #64748B; }
.btn-icon-sm { width: 32px; height: 32px; border-radius: 8px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; margin-left: 5px; }
.check { background: #DCFCE7; color: #16A34A; } .check:hover { background: #16A34A; color: white; }
.cross { background: #FEE2E2; color: #DC2626; } .cross:hover { background: #DC2626; color: white; }

/* 2. REPORTS CARD */
.border-red { border-left: 4px solid #EF4444; }
.text-red { color: #EF4444 !important; }
.count-badge { background: #EF4444; color: white; font-size: 0.75rem; font-weight: 800; width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; border-radius: 50%; box-shadow: 0 4px 10px rgba(239, 68, 68, 0.3); }
.report-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; font-size: 0.85rem; padding-bottom: 10px; border-bottom: 1px dashed rgba(0,0,0,0.05); }
.tag-reason { background: #FEF2F2; color: #B91C1C; padding: 3px 8px; border-radius: 6px; font-weight: 800; font-size: 0.7rem; }
.rp-target { font-weight: 700; color: #475569; }
.link-arrow { font-size: 0.75rem; color: #EF4444; text-decoration: none; font-weight: 800; transition: 0.2s; }
.link-arrow:hover { transform: translateX(3px); }

/* 3. EVENTS CARD */
.event-block { display: flex; align-items: center; gap: 14px; margin-bottom: 14px; background: rgba(255, 247, 237, 0.6); padding: 10px; border-radius: 12px; border: 1px solid rgba(255, 237, 213, 0.8); }
.date-box { background: white; padding: 4px 10px; border-radius: 8px; text-align: center; color: #EA580C; box-shadow: 0 2px 5px rgba(0,0,0,0.02); line-height: 1.1; }
.date-box .d { font-weight: 900; font-size: 1.1rem; display: block; }
.date-box .m { font-size: 0.65rem; font-weight: 800; }
.ev-info { flex: 1; }
.ev-info b { font-size: 0.9rem; display: block; margin-bottom: 6px; color: #1E293B; }
.progress-bar { height: 5px; background: rgba(234, 88, 12, 0.1); border-radius: 3px; overflow: hidden; }
.fill { height: 100%; background: linear-gradient(90deg, #F97316, #FB923C); border-radius: 3px; }

/* 4. USERS CARD */
.row-flex { display: flex; flex-direction: column; gap: 12px; }
.user-chip { display: flex; align-items: center; gap: 12px; }
.avatar-sm { width: 36px; height: 36px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.u-info { display: flex; flex-direction: column; line-height: 1.3; }
.u-name { font-weight: 800; font-size: 0.9rem; color: #0F172A; }
.u-role { font-size: 0.75rem; color: #64748B; font-weight: 600; }

/* 5. COMMENTS CARD */
.chat-bubble { background: rgba(241, 245, 249, 0.6); padding: 10px 14px; border-radius: 12px; font-size: 0.85rem; color: #334155; margin-bottom: 10px; border-left: 3px solid #94A3B8; transition: 0.2s; }
.chat-bubble:hover { background: white; border-color: #EA580C; box-shadow: 0 4px 10px rgba(0,0,0,0.02); }
.c-user { font-weight: 800; color: #0F172A; }

/* 6. CATEGORIES CARD */
.tag-cloud { display: flex; flex-wrap: wrap; gap: 8px; }
.tag-pill { background: rgba(255,255,255,0.8); border: 1px solid rgba(0,0,0,0.05); padding: 5px 12px; border-radius: 20px; font-size: 0.75rem; color: #475569; font-weight: 700; transition: 0.2s; }
.tag-pill:hover { background: #EA580C; color: white; border-color: #EA580C; }
.tag-pill.more { background: transparent; border-style: dashed; color: #94A3B8; }

/* 7. ACHIEVEMENTS CARD */
.medal-list { font-size: 0.85rem; display: flex; flex-direction: column; gap: 8px; }
.medal-row { display: flex; justify-content: space-between; align-items: center; padding: 8px 12px; background: rgba(254, 249, 195, 0.5); border-radius: 10px; border: 1px solid rgba(254, 240, 138, 0.8); }
.medal-row b { color: #854D0E; }
.medal-row small { color: #D97706; font-weight: 800; }

/* 8. NOTIFICATIONS CARD */
.h-flex { display: flex; gap: 15px; flex-wrap: wrap; }
.noti-pill { background: rgba(239, 246, 255, 0.6); padding: 8px 16px; border-radius: 12px; font-size: 0.85rem; color: #1E40AF; display: flex; align-items: center; gap: 10px; border: 1px solid rgba(219, 234, 254, 0.8); flex: 1; font-weight: 600; }
.dot { width: 8px; height: 8px; background: #3B82F6; border-radius: 50%; box-shadow: 0 0 5px rgba(59, 130, 246, 0.5); }
.noti-pill .time { font-size: 0.75rem; opacity: 0.7; margin-left: auto; }

/* ANIMATIONS */
.animate-enter { animation: fadeInDown 0.6s ease-out; }
.animate-stagger { opacity: 0; animation: slideUpFade 0.5s ease-out forwards; animation-delay: calc(var(--i) * 0.1s); }

@keyframes fadeInDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes slideUpFade { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }

/* RESPONSIVE */
@media (max-width: 1200px) { .bento-grid { grid-template-columns: repeat(2, 1fr); } .col-span-2 { grid-column: span 2; } }
@media (max-width: 768px) { .bento-grid { grid-template-columns: 1fr; } .col-span-2 { grid-column: span 1; } }
</style>