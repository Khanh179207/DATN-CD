<template>
  <div class="dashboard-operational">
    <div class="ambient-glow glow-1"></div>
    <div class="ambient-glow glow-2"></div>

    <div class="dash-header animate-fade-down">
      <div class="header-left">
        <h1 class="page-title">Trạm Điều Hành</h1>
        <p class="page-subtitle">Giám sát hoạt động và vận hành hệ thống GoMet <span class="live-dot"></span></p>
      </div>
      <button class="btn-refresh" @click="fetchAll" :disabled="loading">
        <RefreshCcw :size="16" :class="{'spinning': loading}" /> Đồng bộ dữ liệu
      </button>
    </div>

    <div class="bento-grid">
      
      <div class="bento-card col-span-2 post-card animate-stagger" style="--i:1">
        <div class="card-header">
          <div class="h-title"><div class="icon-wrap bg-orange"><FileSignature :size="18" /></div> Chờ phê duyệt</div>
          <router-link to="/admin/posts" class="btn-pill">Quản lý ({{ stats.pendingPosts }})</router-link>
        </div>
        <div class="card-body scrollable">
          <div v-if="loading" class="loading-state"><Loader2 :size="18" class="spinning" style="margin-right: 8px;" /> Đang tải dữ liệu...</div>
          <div v-else-if="data.posts.length === 0" class="empty-state">Không có bài viết nào chờ duyệt</div>
          
          <div v-for="post in data.posts" :key="post.id" class="list-item-row">
            <img :src="post.image" class="item-thumb" />
            <div class="item-details">
              <span class="i-title">{{ post.title }}</span>
              <div class="i-author">
                <img :src="post.authorAvatar" class="avatar-xs" />
                <span class="i-sub">Bởi <b class="text-dark">{{ post.author }}</b> • {{ post.time }}</span>
              </div>
            </div>
            <div class="item-actions">
              <button @click="approvePost(post.id)" class="btn-act check" title="Duyệt bài"><Check :size="16" /></button>
              <button @click="askRejectPost(post)" class="btn-act cross" title="Từ chối"><X :size="16" /></button>
            </div>
          </div>
        </div>
      </div>

      <div class="bento-card border-red animate-stagger" style="--i:2">
        <div class="card-header">
          <div class="h-title text-red"><div class="icon-wrap bg-red"><Flag :size="18" /></div> Khiếu Nại</div>
          <span class="count-badge red">{{ stats.pendingAppeals }}</span>
        </div>
        <div class="card-body scrollable">
          <div v-if="data.appeals.length === 0" class="empty-state-sm">Không có khiếu nại</div>
          <div v-for="ap in data.appeals" :key="ap.id" class="report-row">
            <div class="rp-info">
              <div class="rp-top-line">
                <span v-if="ap.statusLabel !== 'Không rõ'" class="tag-reason" :class="getAppealStatusClass(ap.statusRaw)">{{ ap.statusLabel }}</span>
                <span class="rp-time">{{ ap.time }}</span>
              </div>
              <div class="rp-target" :title="ap.email">{{ ap.email }}</div>
            </div>
            <router-link to="/admin/appeals" class="btn-arrow-right red-arrow"><ArrowRight :size="16" /></router-link>
          </div>
        </div>
      </div>

      <div class="bento-card border-cyan animate-stagger" style="--i:3">
        <div class="card-header">
          <div class="h-title text-cyan"><div class="icon-wrap bg-cyan"><Headset :size="18" /></div> Hỗ Trợ (Tickets)</div>
          <span class="count-badge cyan">{{ stats.openTickets }}</span>
        </div>
        <div class="card-body scrollable">
          <div v-if="data.tickets.length === 0" class="empty-state-sm">Hộp thư rỗng</div>
          <div v-for="tk in data.tickets" :key="tk.id" class="report-row cyan-row">
            <div class="rp-info">
              <span class="tag-reason cyan-tag">{{ tk.typeLabel }}</span>
              <div class="rp-target">{{ tk.title }}</div>
              <div class="rp-author"><User :size="12" /> {{ tk.sender }}</div>
            </div>
            <router-link to="/admin/tickets" class="btn-arrow-right cyan-arrow"><ArrowRight :size="16" /></router-link>
          </div>
        </div>
      </div>

      <div class="bento-card comment-card animate-stagger" style="--i:4">
        <div class="card-header">
          <div class="h-title"><div class="icon-wrap bg-purple"><MessageSquare :size="18" /></div> Bình luận mới</div>
          <router-link to="/admin/comments" class="icon-link"><ExternalLink :size="16" /></router-link>
        </div>
        <div class="card-body scrollable">
          <div v-if="data.comments.length === 0" class="empty-state-sm">Chưa có bình luận</div>
          <div v-for="cmt in data.comments" :key="cmt.id" class="chat-bubble-wrap">
            <img :src="cmt.avatar" class="chat-avatar" />
            <div class="chat-bubble">
              <div class="c-head"><span class="c-user">{{ cmt.user }}</span> <span class="c-time">{{ cmt.time }}</span></div>
              <div class="c-text">"{{ cmt.content }}"</div>
            </div>
          </div>
        </div>
      </div>

      <div class="bento-card user-card animate-stagger" style="--i:5">
        <div class="card-header">
          <div class="h-title"><div class="icon-wrap bg-green"><UserPlus :size="18" /></div> Hội viên mới</div>
          <router-link to="/admin/users" class="icon-link"><ExternalLink :size="16" /></router-link>
        </div>
        <div class="card-body scrollable row-flex">
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

      <div class="bento-card event-card animate-stagger" style="--i:6">
        <div class="card-header">
          <div class="h-title"><div class="icon-wrap bg-blue"><CalendarCheck :size="18" /></div> Sự kiện</div>
          <router-link to="/admin/events" class="icon-link"><ExternalLink :size="16" /></router-link>
        </div>
        <div class="card-body scrollable">
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

      <div class="bento-card noti-card col-span-2 animate-stagger" style="--i:7">
        <div class="card-header">
          <div class="h-title"><div class="icon-wrap bg-slate"><Zap :size="18" /></div> Hoạt động gần đây</div>
          <router-link to="/admin/notifications" class="link-text">Xem tất cả</router-link>
        </div>
        <div class="card-body h-flex scrollable">
          <div v-if="data.notifications.length === 0" class="empty-state-sm">Hệ thống đang yên tĩnh</div>
          <div v-for="notif in data.notifications" :key="notif.id" class="noti-pill">
            <div class="dot"></div> 
            <div class="n-content">
              <span class="n-text">{{ notif.content }}</span>
              <span class="time">{{ notif.time }}</span>
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- Modal Từ chối bài viết -->
    <Transition name="modal-fade">
      <div v-if="rejectModal.show" class="modal-overlay" @click.self="rejectModal.show = false">
        <div class="action-modal">
          <div class="action-icon bg-red-light text-red">
            <Ban :size="28" />
          </div>
          <h3>Từ chối bài viết?</h3>
          <p>Xác nhận từ chối bài viết <strong>"{{ rejectModal.title }}"</strong>?</p>
          
          <div class="reason-input-group">
            <label for="rejectReason">Lý do từ chối (bắt buộc):</label>
            <textarea 
              id="rejectReason" 
              v-model="rejectModal.reason" 
              placeholder="Vui lòng nhập lý do từ chối..." 
              rows="3" 
              class="reason-textarea"
            ></textarea>
            <span v-if="rejectModal.showError" class="error-msg">Bạn phải nhập lý do!</span>
          </div>

          <div class="action-btns">
            <button class="btn-cancel" @click="rejectModal.show = false">Hủy bỏ</button>
            <button class="btn-confirm btn-danger" @click="confirmRejectPost">
              Xác nhận
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import api from '@/services/api'
import { toast } from '@/composables/useToast'
import {
  Activity, RefreshCcw, FileSignature, Check, X, Flag, ArrowRight,
  Headset, User, MessageSquare, ExternalLink, UserPlus, CalendarCheck, Zap, Loader2, Ban
} from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'

const stats = reactive({ pendingPosts: 0, pendingAppeals: 0, openTickets: 0 })
const data = reactive({ posts: [], appeals: [], tickets: [], events: [], users: [], comments: [], notifications: [] })
const loading = ref(true)
const authStore = useAuthStore()

const rejectModal = ref({ show: false, postId: null, title: '', reason: '', showError: false })

// Hàm helper dịch Status Khiếu nại (Appeals)
const getAppealStatusClass = (status) => {
  const s = String(status).toUpperCase();
  if (s === 'PENDING' || s === '0') return 'tag-pending';
  if (s === 'APPROVED' || s === '1') return 'tag-approved';
  if (s === 'REJECTED' || s === '2' || s === '-1') return 'tag-rejected';
  return 'tag-default';
}

const getAppealStatusLabel = (status) => {
  const s = String(status).toUpperCase();
  if (s === 'PENDING' || s === '0') return 'Chờ duyệt';
  if (s === 'APPROVED' || s === '1') return 'Đã mở khóa';
  if (s === 'REJECTED' || s === '2' || s === '-1') return 'Từ chối';
  return 'Không rõ';
}

const fetchAll = async () => {
  loading.value = true
  try {
    const [postsRes, eventsRes, usersRes, ticketsRes, appealsRes, commentsRes, notifRes] = await Promise.allSettled([
      api.get('/api/admin/posts'),
      api.get('/api/events'),
      api.get('/api/admin/member-accounts'),
      api.get('/api/admin/tickets'),
      api.get('/api/admin/appeals'),
      api.get('/api/admin/comments'),
      api.get('/api/admin/notifications')
    ])

    // 1. BÀI VIẾT (POSTS) - KÈM AVATAR
    if (postsRes.status === 'fulfilled') {
      const allPosts = postsRes.value.data || []
      const pendings = allPosts.filter(p => p.isApproved === 0 && p.isActive === 1)
      stats.pendingPosts = pendings.length
      data.posts = pendings.slice(0, 4).map(p => {
        // 🔥 Cập nhật ánh xạ trường tên tác giả triệt để
        const authorName = p.username || p.authorName || p.accountName || p.account?.username || 'Ẩn danh';
        const authorAvatar = p.accountAvatar || p.authorAvatar || p.account?.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(authorName)}&background=10B981&color=fff`;
        return {
          id: p.postID || p.id, 
          title: p.title, 
          author: authorName,
          authorAvatar: authorAvatar,
          time: p.createdAt ? new Date(p.createdAt).toLocaleDateString('vi-VN') : 'Mới đây',
          image: p.image || p.media || `https://ui-avatars.com/api/?name=${encodeURIComponent(p.title?.charAt(0) || 'P')}&background=f8fafc`
        }
      })
    }

    // 2. KHIẾU NẠI (APPEALS) - ĐA TRẠNG THÁI
    if (appealsRes.status === 'fulfilled') {
      const resData = appealsRes.value.data;
      const allAppeals = Array.isArray(resData) ? resData : (resData?.content || resData?.data || []);
      
      // Lấy 4 cái mới nhất (Không bắt buộc phải là PENDING)
      data.appeals = allAppeals.slice(0, 4).map(a => ({
        id: a.appealID || a.id,
        email: a.email || 'Không rõ',
        statusRaw: a.status,
        statusLabel: getAppealStatusLabel(a.status),
        time: a.createdAt ? new Date(a.createdAt).toLocaleDateString('vi-VN') : 'Gần đây'
      }));

      // Đếm riêng số PENDING cho cái Badge màu đỏ trên đỉnh card
      stats.pendingAppeals = allAppeals.filter(a => String(a.status).toUpperCase() === 'PENDING' || a.status === 0 || a.status === '0').length;
    }

    // 3. TICKETS HỖ TRỢ - KÈM TÊN NGƯỜI GỬI
    if (ticketsRes.status === 'fulfilled') {
      const allTickets = ticketsRes.value.data || []
      
      // Đếm số lượng Ticket PENDING
      stats.openTickets = allTickets.filter(t => t.status === 0 || t.status === 'OPEN' || t.status === 'PENDING').length
      
      // Hiển thị 3 cái mới nhất
      data.tickets = allTickets.slice(0, 3).map(t => ({
        id: t.ticketID || t.id, 
        typeLabel: t.ticketType || t.issueType || 'Hỗ trợ', 
        title: t.title || t.description || `Ticket #${t.ticketID || t.id}`,
        sender: t.username || t.accountName || t.account?.username || 'Khách'
      }))
    }

    // 4. BÌNH LUẬN (COMMENTS)
    if (commentsRes.status === 'fulfilled') {
      const allCmt = commentsRes.value.data || []
      data.comments = allCmt.slice(0, 4).map(c => {
        const username = c.username || c.authorName || c.accountName || c.account?.username || 'Ẩn danh'
        const avatar = c.avatar || c.authorAvatar || c.accountAvatar || c.account?.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(username)}&background=8B5CF6&color=fff`
        return {
          id: c.commentID || c.id, 
          user: username, 
          content: c.content || c.text,
          time: c.createdAt ? new Date(c.createdAt).toLocaleDateString('vi-VN') : '',
          avatar: avatar
        }
      })
    }

    // 5. NGƯỜI DÙNG (USERS)
    if (usersRes.status === 'fulfilled') {
      data.users = (usersRes.value.data || []).slice(-4).reverse().map(u => ({
        id: u.accountID, name: u.username, 
        role: u.isAdmin ? 'Admin' : (u.isPremium ? 'Premium' : 'Member'),
        avatar: u.avatar || `https://ui-avatars.com/api/?name=${u.username}&background=10B981&color=fff`
      }))
    }

    // 6. SỰ KIỆN (EVENTS)
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
          participants: isActive ? Math.floor(Math.random() * 40) + 40 : 100
        }
      })
    }

    // 7. THÔNG BÁO (NOTIFICATIONS)
    if (notifRes.status === 'fulfilled') {
      data.notifications = (notifRes.value.data || []).slice(0, 4).map(n => ({
        id: n.notificationID || n.id, 
        content: n.content || n.title || n.message, 
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
    const payload = {
      adminId: authStore.user?.accountID || authStore.user?.id || 0,
      adminName: authStore.user?.username || authStore.user?.fullName || 'Admin'
    };
    await api.put(`/api/admin/posts/approve/${id}`, payload)
    data.posts = data.posts.filter(p => p.id !== id)
    stats.pendingPosts = Math.max(0, stats.pendingPosts - 1)
    toast.success('Đã duyệt bài viết')
  } catch (e) { toast.error('Lỗi duyệt bài: ' + (e.response?.data?.message || e.message)) }
}

const askRejectPost = (post) => {
  rejectModal.value = { show: true, postId: post.id, title: post.title, reason: '', showError: false }
}

const confirmRejectPost = async () => {
  if (!rejectModal.value.reason.trim()) {
    rejectModal.value.showError = true;
    return;
  }

  try {
    const payload = {
      adminId: authStore.user?.accountID || authStore.user?.id || 0,
      adminName: authStore.user?.username || authStore.user?.fullName || 'Admin',
      reason: rejectModal.value.reason.trim()
    };
    
    await api.put(`/api/admin/posts/${rejectModal.value.postId}/reject`, payload)
    data.posts = data.posts.filter(p => p.id !== rejectModal.value.postId)
    stats.pendingPosts = Math.max(0, stats.pendingPosts - 1)
    toast.info('Đã từ chối bài viết')
    rejectModal.value.show = false
  } catch (e) { toast.error('Lỗi từ chối: ' + (e.response?.data?.message || e.message)) }
}

onMounted(fetchAll)
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap');

.dashboard-operational {
  padding: 32px; font-family: 'Inter', sans-serif;
  background: transparent; position: relative; overflow: hidden;
  min-height: 100vh;
}

/* Ambient Background */
.ambient-glow { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.15; pointer-events: none; z-index: 0; }
.glow-1 { width: 500px; height: 500px; background: #EA580C; top: -150px; left: -100px; }
.glow-2 { width: 400px; height: 400px; background: #3B82F6; bottom: -100px; right: -100px; }

/* Header */
.dash-header { position: relative; z-index: 2; display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 24px; }
.page-title { font-size: 2rem; font-weight: 900; color: #0F172A; margin: 0 0 6px; letter-spacing: -0.5px; }
.page-subtitle { color: #64748B; font-size: 0.95rem; margin: 0; font-weight: 500; display: flex; align-items: center; gap: 8px;}
.live-dot { width: 8px; height: 8px; background: #10B981; border-radius: 50%; box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.2); animation: pulse 2s infinite; }
@keyframes pulse { 0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4); } 70% { box-shadow: 0 0 0 8px rgba(16, 185, 129, 0); } 100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); } }

.spinning { animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.btn-refresh { background: white; border: 1px solid #E2E8F0; padding: 10px 18px; border-radius: 10px; font-weight: 600; color: #475569; cursor: pointer; transition: 0.2s; box-shadow: 0 2px 4px rgba(0,0,0,0.02); display: flex; align-items: center; gap: 8px; font-family: inherit;}
.btn-refresh:hover:not(:disabled) { background: #F1F5F9; color: #0F172A; border-color: #CBD5E1; transform: translateY(-1px); }

/* BENTO GRID LUXURY */
.bento-grid { position: relative; z-index: 2; display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.col-span-2 { grid-column: span 2; }

.bento-card { 
  background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.9); border-radius: 20px; 
  padding: 24px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03); 
  display: flex; flex-direction: column; transition: 0.3s; 
  height: 380px; 
}
.bento-card:hover { transform: translateY(-4px); box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06); background: white; }

/* Scrollable Body */
.card-body.scrollable { flex: 1; overflow-y: auto; padding-right: 4px; }
.card-body.scrollable::-webkit-scrollbar { width: 4px; }
.card-body.scrollable::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }

/* Card Headers */
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-shrink: 0;}
.h-title { font-weight: 800; font-size: 1.05rem; color: #1E293B; display: flex; align-items: center; gap: 12px; }
.icon-wrap { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 1.1rem; }
.bg-orange { background: #FFF7ED; color: #EA580C; }
.bg-red { background: #FEF2F2; color: #EF4444; }
.bg-cyan { background: #ECFEFF; color: #06B6D4; }
.bg-blue { background: #EFF6FF; color: #3B82F6; }
.bg-green { background: #F0FDF4; color: #10B981; }
.bg-purple { background: #FAF5FF; color: #8B5CF6; }
.bg-slate { background: #F1F5F9; color: #64748B; }

/* Buttons & Links */
.btn-pill { background: #0F172A; color: white; padding: 6px 14px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; text-decoration: none; transition: 0.2s; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
.btn-pill:hover { background: #EA580C; box-shadow: 0 4px 10px rgba(234,88,12,0.2); }
.icon-link { color: #64748B; transition: 0.2s; background: #F8FAFC; border: 1px solid #E2E8F0; width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; }
.icon-link:hover { color: #0F172A; background: white; border-color: #CBD5E1; }
.link-text { font-size: 0.85rem; color: #3B82F6; text-decoration: none; font-weight: 700; transition: 0.2s; }
.link-text:hover { color: #2563EB; text-decoration: underline; }

/* 1. Pending Posts */
.list-item-row { display: flex; align-items: center; gap: 16px; padding: 12px 16px; border-radius: 14px; margin-bottom: 12px; background: #F8FAFC; transition: 0.2s; border: 1px solid transparent; }
.list-item-row:hover { background: white; border-color: #E2E8F0; box-shadow: 0 4px 12px rgba(0,0,0,0.02); }
.item-thumb { width: 48px; height: 48px; border-radius: 10px; object-fit: cover; border: 1px solid #E2E8F0;}
.item-details { flex: 1; display: flex; flex-direction: column; }
.i-title { font-weight: 800; font-size: 0.95rem; color: #0F172A; margin-bottom: 4px; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.i-author { display: flex; align-items: center; gap: 6px; }
.avatar-xs { width: 18px; height: 18px; border-radius: 50%; object-fit: cover; border: 1px solid #E2E8F0; }
.i-sub { font-size: 0.75rem; color: #64748B; }
.text-dark { color: #334155; }
.item-actions { display: flex; gap: 6px; }
.btn-act { width: 34px; height: 34px; border-radius: 8px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; font-size: 0.95rem; }
.check { background: #DCFCE7; color: #16A34A; } .check:hover { background: #16A34A; color: white; transform: scale(1.05); }
.cross { background: #FEE2E2; color: #DC2626; } .cross:hover { background: #DC2626; color: white; transform: scale(1.05); }

/* 2 & 3. Appeals & Tickets */
.border-red { border-top: 4px solid #EF4444; }
.border-cyan { border-top: 4px solid #06B6D4; }
.text-red { color: #EF4444 !important; }
.text-cyan { color: #06B6D4 !important; }
.count-badge { color: white; font-size: 0.75rem; font-weight: 800; width: 28px; height: 28px; display: flex; align-items: center; justify-content: center; border-radius: 8px; }
.count-badge.red { background: #EF4444; box-shadow: 0 4px 10px rgba(239, 68, 68, 0.2); }
.count-badge.cyan { background: #06B6D4; box-shadow: 0 4px 10px rgba(6, 182, 212, 0.2); }

.report-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; background: white; padding: 12px; border-radius: 12px; border: 1px solid #E2E8F0; }
.report-row.cyan-row { border-color: #CFFAFE; }
.rp-info { display: flex; flex-direction: column; gap: 4px; flex: 1; min-width:0;}
.rp-top-line { display: flex; align-items: center; gap: 8px; }
.rp-time { font-size: 0.75rem; color: #94A3B8; font-weight: 500; white-space: nowrap; }

/* Dynamic Tags */
.tag-reason { align-self: flex-start; padding: 4px 8px; border-radius: 6px; font-weight: 800; font-size: 0.7rem; text-transform: uppercase; }
.tag-pending { background: #FEF2F2; color: #B91C1C; } /* Đỏ */
.tag-approved { background: #F0FDF4; color: #15803D; } /* Xanh lá */
.tag-rejected { background: #F1F5F9; color: #475569; } /* Xám */
.tag-default { background: #FFF7ED; color: #C2410C; } /* Cam */
.cyan-tag { background: #ECFEFF; color: #0891B2; }

.rp-target { font-weight: 700; font-size: 0.85rem; color: #1E293B; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden;}
.rp-author { font-size: 0.75rem; color: #64748B; display: flex; align-items: center; gap: 4px; }

.btn-arrow-right { width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; text-decoration: none; transition: 0.2s; flex-shrink: 0; }
.red-arrow { background: #FEF2F2; color: #EF4444; } .red-arrow:hover { background: #EF4444; color: white; transform: translateX(3px); }
.cyan-arrow { background: #ECFEFF; color: #06B6D4; } .cyan-arrow:hover { background: #06B6D4; color: white; transform: translateX(3px); }

/* 4. Comments */
.chat-bubble-wrap { display: flex; gap: 12px; margin-bottom: 16px; }
.chat-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; flex-shrink: 0; border: 2px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.05);}
.chat-bubble { flex: 1; background: #F8FAFC; padding: 10px 14px; border-radius: 0 16px 16px 16px; font-size: 0.85rem; color: #334155; border: 1px solid #E2E8F0; transition: 0.2s; }
.chat-bubble:hover { background: white; border-color: #CBD5E1; box-shadow: 0 4px 12px rgba(0,0,0,0.03); }
.c-head { margin-bottom: 4px; display: flex; justify-content: space-between; align-items: center;}
.c-user { font-weight: 800; color: #0F172A; font-size: 0.8rem; }
.c-time { font-size: 0.7rem; color: #94A3B8; }
.c-text { color: #475569; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.4;}

/* 5. Users */
.row-flex { display: flex; flex-direction: column; gap: 10px; }
.user-chip { display: flex; align-items: center; gap: 14px; padding: 10px 12px; border-radius: 12px; background: #F8FAFC; border: 1px solid transparent; transition: 0.2s;}
.user-chip:hover { background: white; border-color: #E2E8F0; transform: translateX(4px);}
.avatar-sm { width: 42px; height: 42px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.08); object-fit: cover; }
.u-info { display: flex; flex-direction: column; }
.u-name { font-weight: 800; font-size: 0.9rem; color: #0F172A; margin-bottom: 2px; }
.u-role { font-size: 0.7rem; font-weight: 700; padding: 2px 6px; border-radius: 4px; align-self: flex-start; text-transform: uppercase; }
.u-role.admin { background: #EFF6FF; color: #2563EB; }
.u-role.premium { background: #FFF7ED; color: #EA580C; }
.u-role.member { background: #F1F5F9; color: #64748B; }

/* 6. Events */
.event-block { display: flex; align-items: center; gap: 14px; margin-bottom: 12px; padding: 12px; border-radius: 14px; border: 1px solid #E2E8F0; transition: 0.2s;}
.event-block:hover { background: #F8FAFC; border-color: #CBD5E1; }
.date-box { background: white; border: 1px solid #E2E8F0; padding: 8px; border-radius: 10px; text-align: center; color: #3B82F6; width: 55px; }
.date-box .d { font-weight: 900; font-size: 1.1rem; display: block; line-height: 1.1; }
.date-box .m { font-size: 0.65rem; font-weight: 800; text-transform: uppercase;}
.ev-info { flex: 1; }
.ev-info b { font-size: 0.9rem; display: block; margin-bottom: 8px; color: #0F172A; }
.progress-bar { height: 6px; background: #F1F5F9; border-radius: 3px; overflow: hidden; }
.fill { height: 100%; background: linear-gradient(90deg, #3B82F6, #60A5FA); border-radius: 3px; }

/* 7. Notifications */
.h-flex { display: flex; flex-direction: column; gap: 12px; }
.noti-pill { background: white; padding: 14px 18px; border-radius: 12px; font-size: 0.85rem; color: #1E293B; display: flex; align-items: center; gap: 14px; border: 1px solid #E2E8F0; transition: 0.2s;}
.noti-pill:hover { border-color: #CBD5E1; transform: translateX(4px); }
.dot { width: 10px; height: 10px; background: #3B82F6; border-radius: 50%; box-shadow: 0 0 6px rgba(59, 130, 246, 0.4); flex-shrink: 0; }
.n-content { display: flex; justify-content: space-between; align-items: center; width: 100%; gap: 16px;}
.n-text { font-weight: 600; color: #334155; }
.noti-pill .time { font-size: 0.75rem; color: #94A3B8; font-weight: 500; white-space: nowrap;}

/* Utilities & Animations */
.empty-state { padding: 30px; text-align: center; color: #94A3B8; font-weight: 500; font-size: 0.9rem; background: #F8FAFC; border-radius: 12px; }
.empty-state-sm { padding: 20px; text-align: center; color: #94A3B8; font-size: 0.85rem; }
.loading-state { padding: 30px; text-align: center; color: #3B82F6; font-weight: 600; }

.animate-fade-down { animation: fadeDown 0.6s ease-out; }
.animate-stagger { opacity: 0; animation: slideUpFade 0.5s ease-out forwards; animation-delay: calc(var(--i) * 0.08s); }
@keyframes fadeDown { from { opacity: 0; transform: translateY(-15px); } to { opacity: 1; transform: translateY(0); } }
@keyframes slideUpFade { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

/* Modal Từ chối */
.modal-overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.6); display: flex; align-items: center; justify-content: center; z-index: 9999; backdrop-filter: blur(2px); }
.action-modal { background: white; padding: 28px; border-radius: 16px; width: 380px; max-width: 95vw; text-align: center; box-shadow: 0 25px 50px rgba(0,0,0,0.15); }
.action-icon { width: 56px; height: 56px; border-radius: 50%; margin: 0 auto 16px; display: flex; align-items: center; justify-content: center; }
.bg-red-light { background: #fef2f2; } .text-red { color: #dc2626; }
.action-modal h3 { font-size: 1.2rem; color: #0f172a; margin: 0 0 8px; font-weight: 700; }
.action-modal p { color: #64748b; font-size: 0.95rem; margin: 0 0 24px; }

.reason-input-group { text-align: left; margin-bottom: 20px; }
.reason-input-group label { display: block; font-size: 0.85rem; font-weight: 600; color: #475569; margin-bottom: 6px; }
.reason-textarea { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 8px; outline: none; font-family: inherit; font-size: 0.9rem; resize: none; background: #f8fafc; transition: 0.2s; }
.reason-textarea:focus { border-color: #ef4444; background: white; box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1); }
.error-msg { display: block; color: #dc2626; font-size: 0.8rem; font-weight: 500; margin-top: 4px; }

.action-btns { display: flex; gap: 12px; }
.btn-cancel, .btn-confirm { flex: 1; padding: 10px; border-radius: 8px; font-weight: 600; cursor: pointer; border: none; font-size: 0.95rem; transition: 0.2s; }
.btn-cancel { background: #f1f5f9; color: #475569; }
.btn-danger { background: #ef4444; color: white; }
.btn-danger:hover { background: #dc2626; }
.btn-cancel:hover { background: #e2e8f0; }

.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.3s, transform 0.3s; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; transform: scale(0.95); }

/* Responsive */
@media (max-width: 1200px) { .bento-grid { grid-template-columns: repeat(2, 1fr); } .col-span-2 { grid-column: span 2; } }
@media (max-width: 900px) { .bento-grid { grid-template-columns: 1fr; } .col-span-2 { grid-column: span 1; } .dash-header { flex-direction: column; align-items: flex-start; gap: 16px; } .bento-card {height: 320px;}}
</style>