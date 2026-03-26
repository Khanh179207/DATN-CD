<template>
  <div class="dashboard-operational">
    <div class="ambient-glow glow-1"></div>
    <div class="ambient-glow glow-2"></div>

    <div class="dash-header animate-fade-down">
      <div class="header-left">
        <h1 class="page-title">Control Center</h1>
        <p class="page-subtitle">Quản lý và điều hành hệ thống GoMet theo thời gian thực <span class="live-dot"></span></p>
      </div>
      <button class="btn-refresh" @click="fetchAll" :disabled="loading">
        <i class="fa-solid fa-rotate-right" :class="{'fa-spin': loading}"></i> Đồng bộ dữ liệu
      </button>
    </div>

    <div class="bento-grid">
      
      <div class="bento-card col-span-2 post-card animate-stagger" style="--i:1">
        <div class="card-header">
          <div class="h-title"><div class="icon-wrap bg-orange"><i class="fa-solid fa-file-pen"></i></div> Chờ phê duyệt</div>
          <router-link to="/admin/posts" class="btn-pill">Quản lý ({{ stats.pendingPosts }})</router-link>
        </div>
        <div class="card-body">
          <div v-if="loading" class="loading-state">Đang tải dữ liệu...</div>
          <div v-else-if="data.posts.length === 0" class="empty-state">Không có bài viết nào chờ duyệt</div>
          
          <div v-for="post in data.posts" :key="post.id" class="list-item-row">
            <img :src="post.image" class="item-thumb" />
            <div class="item-details">
              <span class="i-title">{{ post.title }}</span>
              <span class="i-sub">Tác giả: <b class="text-dark">{{ post.author }}</b> • {{ post.time }}</span>
            </div>
            <div class="item-actions">
              <button @click="approvePost(post.id)" class="btn-act check" title="Duyệt bài"><i class="fa-solid fa-check"></i></button>
              <button @click="rejectPost(post.id)" class="btn-act cross" title="Từ chối"><i class="fa-solid fa-xmark"></i></button>
            </div>
          </div>
        </div>
      </div>

      <div class="bento-card ticket-card border-red animate-stagger" style="--i:2">
        <div class="card-header">
          <div class="h-title text-red"><div class="icon-wrap bg-red"><i class="fa-solid fa-ticket"></i></div> Tickets / Khiếu nại</div>
          <span class="count-badge">{{ stats.totalTickets }}</span>
        </div>
        <div class="card-body">
          <div v-if="data.tickets.length === 0" class="empty-state-sm">Không có Ticket nào đang mở</div>
          <div v-for="tk in data.tickets" :key="tk.id" class="report-row">
            <div class="rp-info">
              <span class="tag-reason">{{ tk.reason }}</span>
              <div class="rp-target">{{ tk.target }}</div>
            </div>
            <router-link to="/admin/tickets" class="btn-arrow-right"><i class="fa-solid fa-arrow-right"></i></router-link>
          </div>
        </div>
      </div>

      <div class="bento-card event-card animate-stagger" style="--i:3">
        <div class="card-header">
          <div class="h-title"><div class="icon-wrap bg-blue"><i class="fa-regular fa-calendar-check"></i></div> Sự kiện đang chạy</div>
          <router-link to="/admin/events" class="icon-link"><i class="fa-solid fa-up-right-from-square"></i></router-link>
        </div>
        <div class="card-body">
          <div v-if="data.events.length === 0" class="empty-state-sm">Không có sự kiện</div>
          <div v-for="ev in data.events" :key="ev.id" class="event-block">
            <div class="date-box">
              <span class="d">{{ ev.date.d }}</span><span class="m">{{ ev.date.m }}</span>
            </div>
            <div class="ev-info">
              <b>{{ ev.title }}</b>
              <div class="progress-bar"><div class="fill" :style="{width: ev.participants + '%'}"></div></div>
            </div>
          </div>
        </div>
      </div>

      <div class="bento-card user-card animate-stagger" style="--i:4">
        <div class="card-header">
          <div class="h-title"><div class="icon-wrap bg-green"><i class="fa-solid fa-user-plus"></i></div> Hội viên mới</div>
          <router-link to="/admin/users" class="icon-link"><i class="fa-solid fa-up-right-from-square"></i></router-link>
        </div>
        <div class="card-body row-flex">
          <div v-if="data.users.length === 0" class="empty-state-sm">Chưa có người dùng mới</div>
          <div v-for="u in data.users" :key="u.id" class="user-chip">
            <img :src="u.avatar" class="avatar-sm" />
            <div class="u-info">
              <span class="u-name">{{ u.name }}</span>
              <span class="u-role" :class="u.role.toLowerCase()">{{ u.role }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="bento-card comment-card animate-stagger" style="--i:5">
        <div class="card-header">
          <div class="h-title"><div class="icon-wrap bg-purple"><i class="fa-regular fa-comments"></i></div> Bình luận mới</div>
          <router-link to="/admin/comments" class="btn-pill-xs">Xem tất cả</router-link>
        </div>
        <div class="card-body">
          <div v-if="data.comments.length === 0" class="empty-state-sm">Chưa có bình luận</div>
          <div v-for="cmt in data.comments" :key="cmt.id" class="chat-bubble">
            <div class="c-head"><span class="c-user">{{ cmt.user }}</span></div>
            <div class="c-text">"{{ cmt.content }}"</div>
          </div>
        </div>
      </div>

      <div class="bento-card noti-card col-span-3 animate-stagger" style="--i:6">
        <div class="card-header">
          <div class="h-title"><div class="icon-wrap bg-slate"><i class="fa-regular fa-bell"></i></div> Thông báo hệ thống</div>
          <router-link to="/admin/notifications" class="link-text">Lịch sử hoạt động</router-link>
        </div>
        <div class="card-body h-flex">
          <div v-if="data.notifications.length === 0" class="empty-state-sm">Không có thông báo</div>
          <div v-for="notif in data.notifications" :key="notif.id" class="noti-pill">
            <span class="dot"></span> 
            <span class="n-text">{{ notif.content }}</span>
            <span class="time">{{ notif.time }}</span>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import api from '@/services/api'
import { toast } from '@/composables/useToast'

const stats = reactive({ pendingPosts: 0, totalTickets: 0 })
const data = reactive({ posts: [], tickets: [], events: [], users: [], comments: [], notifications: [] })
const loading = ref(true)

const fetchAll = async () => {
  loading.value = true
  try {
    // 🚀 GỌI ĐỒNG LOẠT 6 API LẤY DỮ LIỆU THẬT TỪ BACKEND
    const [postsRes, eventsRes, usersRes, ticketsRes, commentsRes, notifRes] = await Promise.allSettled([
      api.get('/api/admin/posts'),
      api.get('/api/events'),
      api.get('/admin/accounts'),
      api.get('/api/admin/tickets'), // API Tickets (Đã gộp Report)
      api.get('/api/admin/comments'),
      api.get('/api/admin/notifications')
    ])

    // ==========================================
    // MAP DỮ LIỆU TỪ API THẬT
    // ==========================================

    // 1. BÀI VIẾT (POSTS)
    if (postsRes.status === 'fulfilled') {
      const allPosts = postsRes.value.data || []
      const pendings = allPosts.filter(p => p.isApproved === 0 && p.isActive === 1)
      stats.pendingPosts = pendings.length
      data.posts = pendings.slice(0, 4).map(p => ({
        id: p.postID, 
        title: p.title, 
        author: p.accountName || p.account?.username || 'Ẩn danh',
        time: p.createdAt ? new Date(p.createdAt).toLocaleDateString('vi-VN') : 'Mới đây',
        image: p.image || `https://ui-avatars.com/api/?name=${encodeURIComponent(p.title.charAt(0))}&background=f8fafc`
      }))
    }

    // 2. TICKETS / KHIẾU NẠI (Thay cho Reports)
    if (ticketsRes.status === 'fulfilled') {
      const allTickets = ticketsRes.value.data || []
      stats.totalTickets = allTickets.filter(t => t.status === 'PENDING' || t.status === 'OPEN').length || allTickets.length
      data.tickets = allTickets.slice(0, 3).map(t => ({
        id: t.ticketID || t.id, 
        reason: t.issueType || t.category || t.title || 'Hỗ trợ', 
        target: t.description || t.content || `Ticket #${t.ticketID || t.id}`
      }))
    }

    // 3. SỰ KIỆN (EVENTS)
    if (eventsRes.status === 'fulfilled') {
      const monthAbbr = ['Th1','Th2','Th3','Th4','Th5','Th6','Th7','Th8','Th9','Th10','Th11','Th12']
      const now = new Date()
      data.events = (eventsRes.value.data || []).slice(0, 3).map(e => {
        const start = e.startAt ? new Date(e.startAt) : null
        const end   = e.endAt   ? new Date(e.endAt)   : null
        const isActive = start && end && now >= start && now <= end
        return {
          id: e.eventID, title: e.eventName,
          date: start ? { d: String(start.getDate()).padStart(2,'0'), m: monthAbbr[start.getMonth()] } : { d: '--', m: '--' },
          participants: isActive ? Math.floor(Math.random() * 40) + 40 : 100 // Tạm mock % progress nếu API ko trả về số người tham gia
        }
      })
    }

    // 4. NGƯỜI DÙNG (USERS)
    if (usersRes.status === 'fulfilled') {
      data.users = (usersRes.value.data || []).slice(-4).reverse().map(u => ({
        id: u.accountID, name: u.username, 
        role: u.isAdmin ? 'Admin' : (u.isPremium ? 'Premium' : 'Member'),
        avatar: u.avatar || `https://ui-avatars.com/api/?name=${u.username}&background=ea580c&color=fff`
      }))
    }

    // 5. BÌNH LUẬN (COMMENTS)
    if (commentsRes.status === 'fulfilled') {
      data.comments = (commentsRes.value.data || []).slice(-3).reverse().map(c => ({
        id: c.commentID || c.id, 
        user: c.accountName || c.account?.username || 'Thành viên', 
        content: c.content || c.text
      }))
    }

    // 6. THÔNG BÁO (NOTIFICATIONS)
    if (notifRes.status === 'fulfilled') {
      data.notifications = (notifRes.value.data || []).slice(0, 3).map(n => ({
        id: n.notificationID || n.id, 
        content: n.content || n.message, 
        time: n.createdAt ? new Date(n.createdAt).toLocaleDateString('vi-VN') : 'Gần đây'
      }))
    }

  } catch (e) {
    console.error('Lỗi tải Dashboard:', e)
  } finally {
    loading.value = false
  }
}

const approvePost = async (id) => {
  try {
    await api.put(`/api/admin/posts/approve/${id}`)
    data.posts = data.posts.filter(p => p.id !== id)
    stats.pendingPosts = Math.max(0, stats.pendingPosts - 1)
    toast.success('Đã duyệt bài viết')
  } catch (e) { toast.error('Lỗi duyệt bài: ' + (e.response?.data?.message || e.message)) }
}

const rejectPost = async (id) => {
  if(!confirm('Xác nhận từ chối bài viết này?')) return;
  try {
    await api.put(`/api/admin/posts/deactive/${id}`)
    data.posts = data.posts.filter(p => p.id !== id)
    stats.pendingPosts = Math.max(0, stats.pendingPosts - 1)
    toast.info('Đã từ chối bài viết')
  } catch (e) { toast.error('Lỗi: ' + (e.response?.data?.message || e.message)) }
}

onMounted(fetchAll)
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap');

.dashboard-operational {
  padding: 32px; font-family: 'Inter', sans-serif;
  background: transparent; position: relative; overflow: hidden;
}

/* Ambient Background: Để overflow: hidden ở đây để chặn scroll ngang của Grid/Orb */
.ambient-glow { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.15; pointer-events: none; z-index: 0; }
.glow-1 { width: 500px; height: 500px; background: #EA580C; top: -150px; left: -100px; }
.glow-2 { width: 400px; height: 400px; background: #3B82F6; bottom: -100px; right: -100px; }

/* Header */
.dash-header { position: relative; z-index: 2; display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 32px; }
.page-title { font-size: 2rem; font-weight: 900; color: #0F172A; margin: 0 0 6px; letter-spacing: -0.5px; }
.page-subtitle { color: #64748B; font-size: 0.95rem; margin: 0; font-weight: 500; display: flex; align-items: center; gap: 8px;}
.live-dot { width: 8px; height: 8px; background: #10B981; border-radius: 50%; box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.2); animation: pulse 2s infinite; }
@keyframes pulse { 0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4); } 70% { box-shadow: 0 0 0 8px rgba(16, 185, 129, 0); } 100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); } }

.btn-refresh { background: white; border: 1px solid #E2E8F0; padding: 10px 18px; border-radius: 10px; font-weight: 600; color: #475569; cursor: pointer; transition: 0.2s; box-shadow: 0 2px 4px rgba(0,0,0,0.02); display: flex; align-items: center; gap: 8px; font-family: inherit;}
.btn-refresh:hover:not(:disabled) { background: #F1F5F9; color: #0F172A; border-color: #CBD5E1; transform: translateY(-1px); }

/* BENTO GRID LUXURY */
.bento-grid { position: relative; z-index: 2; display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; grid-auto-rows: minmax(100px, auto); }
.col-span-2 { grid-column: span 2; }
.col-span-3 { grid-column: span 3; }

.bento-card { 
  background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.9); border-radius: 20px; 
  padding: 24px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03); 
  display: flex; flex-direction: column; transition: 0.3s; 
}
.bento-card:hover { transform: translateY(-4px); box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06); background: white; }

/* Card Headers */
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.h-title { font-weight: 800; font-size: 1.05rem; color: #1E293B; display: flex; align-items: center; gap: 12px; }
.icon-wrap { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 1.1rem; }
.bg-orange { background: #FFF7ED; color: #EA580C; }
.bg-red { background: #FEF2F2; color: #EF4444; }
.bg-blue { background: #EFF6FF; color: #3B82F6; }
.bg-green { background: #F0FDF4; color: #10B981; }
.bg-purple { background: #FAF5FF; color: #8B5CF6; }
.bg-slate { background: #F1F5F9; color: #64748B; }

/* Buttons & Links */
.btn-pill { background: #0F172A; color: white; padding: 6px 14px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; text-decoration: none; transition: 0.2s; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
.btn-pill:hover { background: #EA580C; box-shadow: 0 4px 10px rgba(234,88,12,0.2); }
.btn-pill-xs { background: #F1F5F9; color: #475569; padding: 4px 12px; border-radius: 10px; font-size: 0.75rem; text-decoration: none; font-weight: 700; transition: 0.2s; }
.btn-pill-xs:hover { background: #E2E8F0; color: #0F172A; }
.icon-link { color: #64748B; transition: 0.2s; background: #F8FAFC; border: 1px solid #E2E8F0; width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; }
.icon-link:hover { color: #0F172A; background: white; border-color: #CBD5E1; }
.link-text { font-size: 0.8rem; color: #3B82F6; text-decoration: none; font-weight: 700; transition: 0.2s; }
.link-text:hover { color: #2563EB; text-decoration: underline; }

/* 1. Pending Posts */
.list-item-row { display: flex; align-items: center; gap: 16px; padding: 12px 16px; border-radius: 14px; margin-bottom: 12px; background: #F8FAFC; transition: 0.2s; border: 1px solid transparent; }
.list-item-row:last-child { margin-bottom: 0; }
.list-item-row:hover { background: white; border-color: #E2E8F0; box-shadow: 0 4px 12px rgba(0,0,0,0.02); }
.item-thumb { width: 48px; height: 48px; border-radius: 10px; object-fit: cover; border: 1px solid #E2E8F0;}
.item-details { flex: 1; display: flex; flex-direction: column; }
.i-title { font-weight: 800; font-size: 0.95rem; color: #0F172A; margin-bottom: 4px; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.i-sub { font-size: 0.75rem; color: #64748B; }
.text-dark { color: #334155; }
.item-actions { display: flex; gap: 6px; }
.btn-act { width: 34px; height: 34px; border-radius: 8px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; font-size: 0.95rem; }
.check { background: #DCFCE7; color: #16A34A; } .check:hover { background: #16A34A; color: white; transform: scale(1.05); }
.cross { background: #FEE2E2; color: #DC2626; } .cross:hover { background: #DC2626; color: white; transform: scale(1.05); }

/* 2. Tickets (Reports) */
.border-red { border-top: 4px solid #EF4444; }
.text-red { color: #EF4444 !important; }
.count-badge { background: #EF4444; color: white; font-size: 0.75rem; font-weight: 800; width: 28px; height: 28px; display: flex; align-items: center; justify-content: center; border-radius: 8px; box-shadow: 0 4px 10px rgba(239, 68, 68, 0.2); }
.report-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; background: white; padding: 12px; border-radius: 12px; border: 1px solid #FEE2E2; }
.rp-info { display: flex; flex-direction: column; gap: 6px; }
.tag-reason { align-self: flex-start; background: #FEF2F2; color: #B91C1C; padding: 4px 8px; border-radius: 6px; font-weight: 800; font-size: 0.7rem; }
.rp-target { font-weight: 600; font-size: 0.85rem; color: #475569; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden;}
.btn-arrow-right { width: 32px; height: 32px; background: #FEF2F2; color: #EF4444; border-radius: 8px; display: flex; align-items: center; justify-content: center; text-decoration: none; transition: 0.2s; flex-shrink: 0; }
.btn-arrow-right:hover { background: #EF4444; color: white; transform: translateX(3px); }

/* 3. Events */
.event-block { display: flex; align-items: center; gap: 14px; margin-bottom: 14px; padding: 12px; border-radius: 14px; border: 1px solid #E2E8F0; transition: 0.2s;}
.event-block:hover { background: #F8FAFC; border-color: #CBD5E1; }
.date-box { background: white; border: 1px solid #E2E8F0; padding: 8px; border-radius: 10px; text-align: center; color: #EA580C; width: 55px; }
.date-box .d { font-weight: 900; font-size: 1.1rem; display: block; line-height: 1.1; }
.date-box .m { font-size: 0.65rem; font-weight: 800; text-transform: uppercase;}
.ev-info { flex: 1; }
.ev-info b { font-size: 0.9rem; display: block; margin-bottom: 8px; color: #0F172A; }
.progress-bar { height: 6px; background: #F1F5F9; border-radius: 3px; overflow: hidden; }
.fill { height: 100%; background: linear-gradient(90deg, #3B82F6, #60A5FA); border-radius: 3px; }

/* 4. Users */
.row-flex { display: flex; flex-direction: column; gap: 14px; }
.user-chip { display: flex; align-items: center; gap: 14px; padding: 8px 0; border-bottom: 1px dashed #F1F5F9; }
.user-chip:last-child { border-bottom: none; }
.avatar-sm { width: 42px; height: 42px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.08); object-fit: cover; }
.u-info { display: flex; flex-direction: column; }
.u-name { font-weight: 800; font-size: 0.9rem; color: #0F172A; margin-bottom: 2px; }
.u-role { font-size: 0.7rem; font-weight: 700; padding: 2px 6px; border-radius: 4px; align-self: flex-start; text-transform: uppercase; }
.u-role.admin { background: #EFF6FF; color: #2563EB; }
.u-role.premium { background: #FFF7ED; color: #EA580C; }
.u-role.member { background: #F1F5F9; color: #64748B; }

/* 5. Comments */
.chat-bubble { background: #F8FAFC; padding: 12px 16px; border-radius: 0 16px 16px 16px; font-size: 0.85rem; color: #334155; margin-bottom: 14px; border: 1px solid #E2E8F0; transition: 0.2s; position: relative; }
.chat-bubble::before { content: ''; position: absolute; top: 0; left: -6px; width: 0; height: 0; border-top: 8px solid #F8FAFC; border-left: 8px solid transparent; }
.chat-bubble:hover { background: white; border-color: #CBD5E1; box-shadow: 0 4px 12px rgba(0,0,0,0.03); }
.c-head { margin-bottom: 4px; }
.c-user { font-weight: 800; color: #0F172A; font-size: 0.8rem; }
.c-text { font-style: italic; color: #475569; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;}

/* 6. Notifications */
.h-flex { display: flex; gap: 16px; flex-wrap: wrap; }
.noti-pill { background: white; padding: 12px 18px; border-radius: 12px; font-size: 0.85rem; color: #1E293B; display: flex; align-items: center; gap: 12px; border: 1px solid #E2E8F0; flex: 1; min-width: 300px; font-weight: 600; box-shadow: 0 2px 8px rgba(0,0,0,0.02); transition: 0.2s;}
.noti-pill:hover { border-color: #CBD5E1; transform: translateY(-2px); }
.dot { width: 8px; height: 8px; background: #3B82F6; border-radius: 50%; box-shadow: 0 0 6px rgba(59, 130, 246, 0.4); flex-shrink: 0; }
.n-text { flex: 1; }
.noti-pill .time { font-size: 0.75rem; color: #94A3B8; font-weight: 500; white-space: nowrap;}

/* Utilities & Animations */
.empty-state { padding: 30px; text-align: center; color: #94A3B8; font-weight: 500; font-size: 0.9rem; background: #F8FAFC; border-radius: 12px; }
.empty-state-sm { padding: 20px; text-align: center; color: #94A3B8; font-size: 0.85rem; }
.loading-state { padding: 30px; text-align: center; color: #3B82F6; font-weight: 600; animation: pulse-text 1.5s infinite; }
@keyframes pulse-text { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }

.animate-fade-down { animation: fadeDown 0.6s ease-out; }
.animate-stagger { opacity: 0; animation: slideUpFade 0.5s ease-out forwards; animation-delay: calc(var(--i) * 0.08s); }

@keyframes fadeDown { from { opacity: 0; transform: translateY(-15px); } to { opacity: 1; transform: translateY(0); } }
@keyframes slideUpFade { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

/* Responsive */
@media (max-width: 1200px) { .bento-grid { grid-template-columns: repeat(2, 1fr); } .col-span-2 { grid-column: span 2; } .col-span-3 { grid-column: span 2; } }
@media (max-width: 768px) { .bento-grid { grid-template-columns: 1fr; } .col-span-2, .col-span-3 { grid-column: span 1; } .dash-header { flex-direction: column; align-items: flex-start; gap: 16px; } }
</style>