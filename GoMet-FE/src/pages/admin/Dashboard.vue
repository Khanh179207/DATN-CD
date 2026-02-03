<template>
  <div class="dashboard-container">
    <div class="dash-header">
      <h2 class="page-title">Tổng Quan Hệ Thống</h2>
      <p class="sub-title">Trung tâm điều khiển Gomet Admin</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon money">💰</div>
        <div class="stat-info"><div class="stat-value">125.4M ₫</div><div class="stat-label">Doanh thu</div></div>
      </div>
      <div class="stat-card">
        <div class="stat-icon users">👥</div>
        <div class="stat-info"><div class="stat-value">5,420</div><div class="stat-label">Người dùng</div></div>
      </div>
      <div class="stat-card">
        <div class="stat-icon posts">📄</div>
        <div class="stat-info"><div class="stat-value">12,890</div><div class="stat-label">Bài viết</div></div>
      </div>
      <div class="stat-card warning">
        <div class="stat-icon warning">⚠️</div>
        <div class="stat-info"><div class="stat-value text-red">15</div><div class="stat-label">Báo cáo</div></div>
      </div>
    </div>

    <div class="main-layout">
      
      <div class="left-column">
        
        <div class="group-box border-blue">
          <div class="group-header">
            <h3>🛡️ Trung Tâm Kiểm Duyệt</h3>
            <span class="badge-pill">Ưu tiên cao</span>
          </div>
          
          <div class="group-content">
            <div class="sub-panel">
              <div class="panel-head">
                <h4>📝 Bài Viết Chờ Duyệt ({{ data.posts.length }})</h4> 
                <router-link to="/admin/posts" class="btn-action-outline">Quản lý bài đăng →</router-link>
              </div>
              <div class="list-vertical">
                <div v-for="item in data.posts" :key="item.id" class="item-row">
                  <span class="status-dot pending"></span>
                  <div class="item-info">
                    <span class="item-title">{{ item.title }}</span>
                    <small>Bởi {{ item.author }} • {{ item.time }}</small>
                  </div>
                  <div class="actions"><button class="btn-icon check">✓</button><button class="btn-icon cross">✕</button></div>
                </div>
              </div>
            </div>

            <div class="divider"></div>

            <div class="sub-panel">
              <div class="panel-head">
                <h4 class="text-red">🚨 Báo Cáo Vi Phạm ({{ data.reports.length }})</h4> 
                <router-link to="/admin/reports" class="btn-action-outline red">Xử lý báo cáo →</router-link>
              </div>
              <table class="simple-table">
                <thead><tr><th>Lý do</th><th>Đối tượng</th><th>Hành động</th></tr></thead>
                <tbody>
                  <tr v-for="rp in data.reports" :key="rp.id">
                    <td><span class="tag-reason">{{ rp.reason }}</span></td>
                    <td>{{ rp.target }}</td>
                    <td><span class="text-link">Xem</span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="group-box border-orange">
          <div class="group-header">
            <h3>⚙️ Vận Hành Hệ Thống</h3>
          </div>
          <div class="group-content">
            
            <div class="sub-panel mb-3">
              <div class="panel-head">
                <h4>🎉 Sự Kiện Sắp Tới</h4>
                <router-link to="/admin/events" class="btn-action-outline orange">Quản lý sự kiện →</router-link>
              </div>
              <div class="grid-2">
                <div v-for="ev in data.events" :key="ev.id" class="event-card">
                   <div class="date-box"><span>{{ ev.day }}</span><small>{{ ev.month }}</small></div>
                   <span>{{ ev.name }}</span>
                </div>
              </div>
            </div>

            <div class="grid-2-equal">
              
              <div class="sub-panel border-box">
                <div class="panel-head-mini">
                  <h4>📂 Danh Mục</h4>
                  <router-link to="/admin/categories" class="link-mini">Chi tiết</router-link>
                </div>
                <div class="tags-cloud">
                   <span v-for="cat in data.categories" :key="cat.id" class="tag-pill">{{ cat.name }}</span>
                </div>
              </div>

              <div class="sub-panel border-box">
                <div class="panel-head-mini">
                  <h4>🏆 Thành Tích</h4>
                  <router-link to="/admin/achievements" class="link-mini">Chi tiết</router-link>
                </div>
                <div class="medal-list">
                   <div v-for="ach in data.achievements" :key="ach.id" class="medal-row">🥇 <b>{{ ach.user }}</b> - {{ ach.medal }}</div>
                </div>
              </div>

            </div>
          </div>
        </div>

      </div>

      <div class="right-column">
        
        <div class="group-box border-purple full-height">
          <div class="group-header">
            <h3>💬 Hub Tương Tác</h3>
          </div>
          
          <div class="group-content">
            
            <div class="sub-section bg-gradient">
              <div class="flex-head">
                <span class="text-white">🔔 Thông Báo</span> 
                <router-link to="/admin/notifications" class="btn-white-xs">Quản lý</router-link>
              </div>
              <div v-for="notif in data.notifications" :key="notif.id" class="notif-row">
                 <span>{{ notif.title }}</span> <small>{{ notif.time }}</small>
              </div>
            </div>

            <div class="sub-section">
              <div class="panel-head">
                <h4>💌 Hòm Thư ({{ data.feedbacks.length }})</h4> 
                <router-link to="/admin/reports?tab=feedback" class="link-sm">Mở hòm thư →</router-link>
              </div>
              <div v-for="fb in data.feedbacks" :key="fb.id" class="mail-row">
                 <div class="mail-icon">✉️</div>
                 <div class="mail-info"><span>{{ fb.subject }}</span><small>{{ fb.user }}</small></div>
              </div>
            </div>

            <div class="divider-dashed"></div>

            <div class="sub-section">
               <div class="panel-head">
                 <h4>💬 Bình Luận Mới</h4>
                 <router-link to="/admin/comments" class="link-sm">Kiểm duyệt →</router-link>
               </div>
               <div v-for="cmt in data.comments" :key="cmt.id" class="chat-bubble">
                  <b>{{ cmt.user }}:</b> "{{ cmt.content }}"
               </div>
            </div>

            <div class="divider-dashed"></div>

            <div class="sub-section">
               <div class="panel-head">
                 <h4>👥 User Mới</h4>
                 <router-link to="/admin/users" class="link-sm">Danh sách →</router-link>
               </div>
               <div v-for="u in data.users" :key="u.id" class="user-mini">
                  <img :src="u.avatar" class="avatar-xs"> <span>{{ u.name }}</span>
               </div>
            </div>

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
    { id: 1, title: 'Món ngon ngày tết 2026', time: '10p trước', author: 'User_1' },
    { id: 2, title: 'Review du lịch Đà Lạt', time: '30p trước', author: 'User_2' },
    { id: 3, title: 'Lập trình VueJS cơ bản', time: '1h trước', author: 'User_3' }
  ],
  reports: [
    { id: 1, reason: 'Nội dung 18+', target: 'Bài viết #99' },
    { id: 2, reason: 'Spam QC', target: 'User: Hacker123' },
    { id: 3, reason: 'Ngôn từ thù ghét', target: 'Bài viết #102' }
  ],
  events: [
    { id: 1, name: 'Cuộc thi Top Chef', day: '20', month: 'FEB' },
    { id: 2, name: 'Workshop Coding', day: '25', month: 'FEB' }
  ],
  notifications: [
    { id: 1, title: 'Bảo trì hệ thống', time: 'Hôm qua' },
    { id: 2, title: 'Chúc mừng năm mới', time: '1/1' }
  ],
  feedbacks: [
    { id: 1, user: 'Minh Tuấn', subject: 'Lỗi đăng nhập' },
    { id: 2, user: 'Lan Anh', subject: 'Góp ý giao diện' }
  ],
  comments: [
    { id: 1, user: 'Minh An', content: 'Bài viết rất hữu ích!' },
    { id: 2, user: 'Thái Bình', content: 'Cần sửa lại code...' }
  ],
  users: [
    { id: 1, name: 'Nguyễn Văn A', avatar: 'https://i.pravatar.cc/150?img=11' },
    { id: 2, name: 'Trần Thị B', avatar: 'https://i.pravatar.cc/150?img=5' }
  ],
  categories: [ { id: 1, name: 'Ẩm thực' }, { id: 2, name: 'Du lịch' }, { id: 3, name: 'Công nghệ' }, { id: 4, name: 'Đời sống' } ],
  achievements: [ { id: 1, user: 'Tùng', medal: 'Đầu bếp' }, { id: 2, user: 'Cúc', medal: 'Thợ săn ảnh' } ]
})
</script>

<style scoped>
.dashboard-container { padding: 25px; font-family: 'Quicksand', sans-serif; color: #334155; background-color: #F8FAFC; min-height: 100vh; }
.dash-header { margin-bottom: 25px; }
.page-title { font-size: 1.8rem; font-weight: 800; color: #1E293B; margin: 0; }
.sub-title { color: #64748B; margin-top: 5px; }

/* STATS GRID */
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 30px; }
.stat-card { background: white; padding: 20px; border-radius: 12px; display: flex; align-items: center; gap: 15px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); border: 1px solid #E2E8F0; }
.stat-icon { width: 45px; height: 45px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }
.stat-icon.money { background: #ECFDF5; color: #10B981; }
.stat-icon.users { background: #EFF6FF; color: #3B82F6; }
.stat-icon.posts { background: #FFF7ED; color: #F97316; }
.stat-icon.warning { background: #FEF2F2; color: #EF4444; }
.stat-value { font-size: 1.4rem; font-weight: 800; color: #0F172A; }
.stat-label { font-size: 0.8rem; color: #64748B; }

/* LAYOUT 2 CỘT */
.main-layout { display: grid; grid-template-columns: 2.5fr 1fr; gap: 25px; align-items: start; }
.left-column { display: flex; flex-direction: column; gap: 25px; }
.right-column { display: flex; flex-direction: column; }

/* GROUP BOX STYLE */
.group-box { background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.03); display: flex; flex-direction: column; }
.group-box.border-blue { border-top: 4px solid #3B82F6; }
.group-box.border-orange { border-top: 4px solid #F97316; }
.group-box.border-purple { border-top: 4px solid #8B5CF6; }
.full-height { height: 100%; }

.group-header { padding: 15px 20px; border-bottom: 1px solid #F1F5F9; display: flex; justify-content: space-between; align-items: center; background: #FAFCFF; }
.group-header h3 { margin: 0; font-size: 1.1rem; font-weight: 700; color: #1E293B; }
.badge-pill { background: #DBEAFE; color: #1E40AF; padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 600; }

.group-content { padding: 20px; display: flex; flex-direction: column; gap: 20px; }

/* BUTTON STYLES (New) */
.btn-action-outline { border: 1px solid #3B82F6; color: #3B82F6; padding: 5px 12px; border-radius: 20px; font-size: 0.75rem; font-weight: 600; text-decoration: none; transition: 0.2s; }
.btn-action-outline:hover { background: #3B82F6; color: white; }
.btn-action-outline.red { border-color: #EF4444; color: #EF4444; }
.btn-action-outline.red:hover { background: #EF4444; color: white; }
.btn-action-outline.orange { border-color: #F97316; color: #F97316; }
.btn-action-outline.orange:hover { background: #F97316; color: white; }
.link-sm { font-size: 0.8rem; color: #6366F1; text-decoration: none; font-weight: 600; }
.btn-white-xs { background: rgba(255,255,255,0.2); color: white; padding: 3px 10px; border-radius: 12px; font-size: 0.7rem; text-decoration: none; }
.btn-white-xs:hover { background: rgba(255,255,255,0.4); }

/* SUB PANELS */
.sub-panel h4 { margin: 0; font-size: 0.95rem; font-weight: 600; color: #334155; }
.panel-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.text-red { color: #EF4444; }
.mb-3 { margin-bottom: 15px; }

/* GRID for Separated Modules */
.grid-2-equal { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.border-box { border: 1px solid #F1F5F9; border-radius: 12px; padding: 15px; }
.panel-head-mini { display: flex; justify-content: space-between; margin-bottom: 10px; align-items: center; }
.link-mini { font-size: 0.75rem; color: #94A3B8; text-decoration: none; }
.link-mini:hover { color: #3B82F6; text-decoration: underline; }

/* Items Styling */
.item-row { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; padding-bottom: 10px; border-bottom: 1px dashed #F1F5F9; }
.item-row:last-child { border: none; }
.status-dot { width: 8px; height: 8px; border-radius: 50%; background: #F59E0B; }
.item-info { flex: 1; display: flex; flex-direction: column; }
.item-title { font-weight: 600; font-size: 0.9rem; }
.item-info small { font-size: 0.75rem; color: #94A3B8; }
.btn-icon { width: 28px; height: 28px; border-radius: 50%; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.check { background: #DCFCE7; color: #16A34A; margin-right: 5px; }
.cross { background: #FEE2E2; color: #DC2626; }

.simple-table { width: 100%; border-collapse: collapse; font-size: 0.85rem; }
.simple-table th { text-align: left; color: #94A3B8; font-weight: 500; padding-bottom: 8px; }
.simple-table td { padding: 8px 0; border-top: 1px solid #F1F5F9; }
.tag-reason { background: #FEF2F2; color: #B91C1C; padding: 2px 6px; border-radius: 4px; font-weight: 600; font-size: 0.75rem; }
.text-link { color: #3B82F6; cursor: pointer; font-weight: 600; }

.grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.event-card { display: flex; align-items: center; gap: 10px; background: #FFF7ED; padding: 8px; border-radius: 8px; font-size: 0.9rem; font-weight: 600; color: #C2410C; }
.date-box { background: white; padding: 2px 8px; border-radius: 6px; text-align: center; color: #F97316; border: 1px solid #FFEDD5; line-height: 1; }
.date-box span { font-weight: 800; }
.date-box small { font-size: 0.6rem; text-transform: uppercase; display: block; }

.medal-list { font-size: 0.85rem; }
.tags-cloud { display: flex; gap: 5px; flex-wrap: wrap; }
.tag-pill { background: #F1F5F9; padding: 4px 8px; border-radius: 12px; font-size: 0.75rem; color: #475569; }
.divider { height: 1px; background: #E2E8F0; margin: 0 0 20px 0; }
.divider-dashed { border-bottom: 1px dashed #F1F5F9; margin: 15px 0; }

/* SIDEBAR STYLING */
.bg-gradient { background: linear-gradient(135deg, #6366F1 0%, #4F46E5 100%); padding: 15px; border-radius: 12px; color: white; margin-bottom: 20px; }
.flex-head { display: flex; justify-content: space-between; margin-bottom: 10px; font-weight: 700; align-items: center; }
.notif-row { border-bottom: 1px solid rgba(255,255,255,0.2); padding: 5px 0; font-size: 0.85rem; display: flex; justify-content: space-between; }
.notif-row:last-child { border: none; }

.sub-section { margin-bottom: 0; }
.mail-row { display: flex; gap: 10px; align-items: center; margin-bottom: 8px; }
.mail-icon { width: 32px; height: 32px; background: #EFF6FF; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.mail-info span { font-weight: 600; font-size: 0.9rem; display: block; }
.mail-info small { color: #94A3B8; font-size: 0.75rem; }

.chat-bubble { background: #F1F5F9; padding: 8px 12px; border-radius: 8px; font-size: 0.85rem; color: #334155; font-style: italic; }
.user-mini { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; font-weight: 600; font-size: 0.9rem; }
.avatar-xs { width: 28px; height: 28px; border-radius: 50%; }

/* Responsive */
@media (max-width: 1024px) {
  .main-layout { grid-template-columns: 1fr; }
  .grid-2, .grid-2-equal { grid-template-columns: 1fr; }
  .stats-grid { grid-template-columns: 1fr 1fr; }
}
</style>