<template>
  <div class="gomet-admin-pro">
    
    <div class="header-zone">
      <div class="header-content">
        <h1 class="page-title">Quản Lý Sự Kiện & Workshop</h1>
        <p class="sub-text">Kiến tạo sân chơi ẩm thực đẳng cấp</p>
      </div>
      <button class="btn-create-mega">
        <span class="icon-plus">+</span> Tạo Sự Kiện Mới
      </button>
    </div>

    <div class="stats-bar animate-slide-down">
      <div class="stat-item">
        <span class="s-label">Tổng sự kiện</span>
        <span class="s-val">12</span>
      </div>
      <div class="divider"></div>
      <div class="stat-item active">
        <span class="s-label">Đang diễn ra</span>
        <span class="s-val text-green">03</span>
      </div>
      <div class="divider"></div>
      <div class="stat-item">
        <span class="s-label">Sắp tới</span>
        <span class="s-val text-orange">05</span>
      </div>
    </div>

    <div class="toolbar glass-effect">
      <div class="search-wrapper">
        <svg class="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
        <input type="text" placeholder="Tìm kiếm tên sự kiện..." />
      </div>
      <div class="filter-group">
        <select>
          <option>Tất cả trạng thái</option>
          <option>Đang diễn ra</option>
          <option>Đã kết thúc</option>
        </select>
      </div>
    </div>

    <div class="event-list-container hide-scrollbar">
      <div v-for="ev in events" :key="ev.id" class="event-row-card animate-fade-up">
        
        <div class="card-main">
          <div class="img-frame">
            <img :src="ev.cover" alt="cover" />
            <div class="id-tag">#{{ ev.id }}</div>
          </div>
          <div class="info-block">
            <h3 class="ev-name">{{ ev.name }}</h3>
            <div class="ev-location">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
              {{ ev.location }}
            </div>
            <div class="ev-meta"><span><i class="dot"></i> {{ ev.type }}</span></div>
          </div>
        </div>

        <div class="card-stats">
          <div class="time-box">
            <span class="lbl">Thời gian</span>
            <span class="val">{{ ev.startDate }}</span>
            <span class="val end">➝ {{ ev.endDate }}</span>
          </div>
          <div class="participant-box">
            <span class="lbl">Bài dự thi</span>
            <div class="avatar-group">
              <div class="avatars"><span class="avt" v-for="n in 3" :key="n"></span></div>
              <span class="count">+{{ ev.participantCount }}</span>
            </div>
          </div>
        </div>

        <div class="card-status">
          <div class="status-pill" :class="ev.status">{{ getStatusText(ev) }}</div>
        </div>

        <div class="card-actions">
          <button class="action-btn view" @click="openModal(ev)" title="Chi tiết">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
          </button>
          <button class="action-btn edit" title="Chỉnh sửa">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
          </button>
          <button class="action-btn delete" @click="deleteEvent(ev.id)" title="Xóa sự kiện">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
          </button>
        </div>
      </div>
    </div>

    <Transition name="zoom-in">
      <div v-if="isModalOpen" class="modal-overlay" @click.self="closeModal">
        <div class="modal-card-ultra">
          
          <div class="modal-cover" :style="{ backgroundImage: `url(${selectedEvent.cover})` }">
            <div class="cover-gradient"></div>
            <button class="btn-close-abs" @click="closeModal">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
            <div class="cover-info">
              <span class="status-badge-hero" :class="selectedEvent.status">
                <span class="dot"></span> {{ getStatusText(selectedEvent) }}
              </span>
              <h2 class="hero-title">{{ selectedEvent.name }}</h2>
              <div class="hero-meta">
                <span><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg> {{ selectedEvent.startDate }} - {{ selectedEvent.endDate }}</span>
                <span><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg> {{ selectedEvent.location }}</span>
              </div>
            </div>
          </div>

          <div class="modal-body-ultra scroll-y">
            <div class="stats-grid">
              <div class="stat-box">
                <label>QUÁN QUÂN</label>
                <div class="s-val text-gold">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"></path><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"></path><path d="M4 22h16"></path><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"></path></svg>
                  {{ selectedEvent.winner || 'Chưa có' }}
                </div>
              </div>
              <div class="stat-box">
                <label>BÀI THI</label>
                <div class="s-val">{{ selectedEvent.participantCount }} bài</div>
              </div>
              <div class="stat-box">
                <label>LOẠI HÌNH</label>
                <div class="s-val">{{ selectedEvent.type }}</div>
              </div>
            </div>

            <div class="section-divider"></div>

            <div class="entries-section">
              <div class="sec-header">
                <h3>🏆 Bài Dự Thi Nổi Bật</h3>
                <button class="btn-text">Xem tất cả</button>
              </div>

              <div class="entries-list">
                <div v-for="post in selectedEvent.posts" :key="post.id" class="entry-item">
                  <img :src="post.image || 'https://via.placeholder.com/50'" class="entry-thumb" />
                  <div class="entry-info">
                    <h4>{{ post.title }}</h4>
                    <span class="author">bởi {{ post.author }}</span>
                  </div>
                  
                  <div class="entry-likes">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="#EF4444" stroke="#EF4444" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
                    {{ post.likes }}
                  </div>

                  <button class="btn-view-post" @click="viewPostDetail(post)">
                    Xem ngay
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"></path><polyline points="15 3 21 3 21 9"></polyline><line x1="10" y1="14" x2="21" y2="3"></line></svg>
                  </button>

                </div>
                
                <div v-if="selectedEvent.posts.length === 0" class="empty-state">
                  Chưa có bài dự thi nào nổi bật.
                </div>
              </div>
            </div>

          </div>
          
          <div class="modal-footer-ultra">
            <button class="btn-sec" @click="closeModal">Đóng</button>
            <button class="btn-pri">Chỉnh sửa sự kiện</button>
          </div>
        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router' // 1. IMPORT ROUTER

// 2. KHỞI TẠO ROUTER
const router = useRouter()

const events = ref([
  { 
    id: '01', name: 'Top Chef: Hương Vị Việt', type: 'Cuộc thi',
    startDate: '01/02/2026', endDate: '20/02/2026', location: 'Hội trường A', 
    status: 'ended', participantCount: 24, winner: 'Nguyễn Văn A',
    cover: 'https://images.unsplash.com/photo-1556910103-1c02745a30bf?w=600',
    posts: [
      { id: 1, title: 'Phở Thìn Tái Lăn', author: 'Chef Tùng', likes: 1200, image: 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=100' },
      { id: 2, title: 'Bún Chả Hà Nội', author: 'Mẹ Nấu', likes: 980, image: 'https://images.unsplash.com/photo-1580476262716-6c869f454721?w=100' }
    ]
  },
  { 
    id: '02', name: 'Sweet Art: Bánh Ngọt', type: 'Thử thách',
    startDate: '25/02/2026', endDate: '05/03/2026', location: 'Sảnh chính', 
    status: 'active', participantCount: 15, winner: null,
    cover: 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600',
    posts: [{ id: 3, title: 'Macaron Cầu Vồng', author: 'Sweetie', likes: 450, image: 'https://images.unsplash.com/photo-1558326567-98ae2405596b?w=100' }]
  },
  { 
    id: '03', name: 'Workshop: Coffee Master', type: 'Workshop',
    startDate: '10/03/2026', endDate: '12/03/2026', location: 'Phòng Lab 2', 
    status: 'upcoming', participantCount: 42, winner: null,
    cover: 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=600',
    posts: []
  }
])

const isModalOpen = ref(false)
const selectedEvent = ref({})

const openModal = (ev) => {
  selectedEvent.value = ev
  isModalOpen.value = true
}
const closeModal = () => isModalOpen.value = false

const deleteEvent = (id) => {
  if(confirm('Bạn có chắc chắn muốn xóa sự kiện này không?')) {
    events.value = events.value.filter(e => e.id !== id)
  }
}

// 3. HÀM ĐIỀU HƯỚNG ROUTER
const viewPostDetail = (post) => {
  // Chuyển hướng sang trang chi tiết bài viết, ví dụ: /post/1
  router.push(`/post/${post.id}`)
}

const getStatusText = (ev) => {
  if (ev.status === 'ended') return 'Đã kết thúc'
  if (ev.status === 'active') return 'Đang diễn ra'
  return 'Sắp tới'
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap');

.gomet-admin-pro { padding: 30px 40px; background: #FFF7ED; min-height: 100vh; font-family: 'Manrope', sans-serif; color: #1E293B; }
.header-zone { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 25px; }
.page-title { font-size: 2rem; font-weight: 800; color: #0F172A; margin: 0; letter-spacing: -0.5px; }
.sub-text { color: #EA580C; font-weight: 600; margin-top: 5px; opacity: 0.8; }
.btn-create-mega { background: linear-gradient(135deg, #EA580C, #C2410C); color: white; border: none; padding: 12px 28px; border-radius: 12px; font-weight: 700; font-size: 1rem; cursor: pointer; box-shadow: 0 8px 20px rgba(234, 88, 12, 0.25); transition: 0.3s; display: flex; align-items: center; gap: 8px; }
.btn-create-mega:hover { transform: translateY(-2px); box-shadow: 0 12px 25px rgba(234, 88, 12, 0.35); }

/* STATS */
.stats-bar { display: flex; gap: 20px; margin-bottom: 30px; background: white; padding: 15px 25px; border-radius: 16px; width: fit-content; box-shadow: 0 4px 15px rgba(0,0,0,0.02); }
.stat-item { display: flex; flex-direction: column; padding: 0 10px; }
.s-label { font-size: 0.75rem; color: #64748B; font-weight: 600; text-transform: uppercase; }
.s-val { font-size: 1.2rem; font-weight: 800; color: #0F172A; }
.text-green { color: #16A34A; } .text-orange { color: #F97316; }
.divider { width: 1px; background: #E2E8F0; }

/* LIST */
.toolbar { display: flex; justify-content: space-between; margin-bottom: 20px; }
.search-wrapper { position: relative; width: 350px; }
.search-icon { position: absolute; left: 12px; top: 50%; transform: translateY(-50%); color: #94A3B8; }
.search-wrapper input { width: 100%; padding: 12px 12px 12px 40px; border: 1px solid transparent; background: white; border-radius: 12px; font-size: 0.95rem; box-shadow: 0 4px 10px rgba(0,0,0,0.02); transition: 0.2s; }
.search-wrapper input:focus { outline: none; box-shadow: 0 4px 15px rgba(234, 88, 12, 0.1); border-color: #FED7AA; }
.event-list-container { display: flex; flex-direction: column; gap: 16px; }
.event-row-card { display: grid; grid-template-columns: 2.5fr 1.5fr 1fr 1fr; gap: 20px; align-items: center; background: white; padding: 16px; border-radius: 20px; box-shadow: 0 4px 6px rgba(0,0,0,0.01); border: 1px solid white; transition: all 0.3s ease; position: relative; overflow: hidden; }
.event-row-card:hover { transform: translateY(-3px); box-shadow: 0 15px 30px rgba(0,0,0,0.05); border-color: #FFEDD5; z-index: 10; }
.card-main { display: flex; gap: 16px; align-items: center; }
.img-frame { position: relative; width: 80px; height: 80px; border-radius: 12px; overflow: hidden; flex-shrink: 0; }
.img-frame img { width: 100%; height: 100%; object-fit: cover; }
.id-tag { position: absolute; top: 0; left: 0; background: rgba(0,0,0,0.6); color: white; font-size: 0.6rem; padding: 2px 6px; border-bottom-right-radius: 8px; backdrop-filter: blur(4px); }
.info-block { display: flex; flex-direction: column; justify-content: center; }
.ev-name { margin: 0 0 5px 0; font-size: 1rem; font-weight: 800; color: #1E293B; }
.ev-location { font-size: 0.8rem; color: #64748B; display: flex; align-items: center; gap: 4px; }
.ev-meta .dot { display: inline-block; width: 6px; height: 6px; background: #EA580C; border-radius: 50%; margin-right: 4px; }
.ev-meta { font-size: 0.75rem; color: #EA580C; font-weight: 700; margin-top: 4px; }
.card-stats { display: flex; gap: 20px; }
.time-box, .participant-box { display: flex; flex-direction: column; justify-content: center; }
.lbl { font-size: 0.7rem; color: #94A3B8; font-weight: 600; text-transform: uppercase; margin-bottom: 2px; }
.val { font-size: 0.85rem; font-weight: 700; color: #334155; }
.val.end { color: #94A3B8; font-size: 0.75rem; }
.avatars { display: flex; padding-left: 8px; }
.avt { width: 24px; height: 24px; background: #CBD5E1; border-radius: 50%; border: 2px solid white; margin-left: -8px; }
.status-pill { padding: 6px 14px; border-radius: 30px; font-size: 0.8rem; font-weight: 700; text-align: center; }
.status-pill.active { background: #DCFCE7; color: #15803D; } .status-pill.upcoming { background: #FFEDD5; color: #C2410C; } .status-pill.ended { background: #F1F5F9; color: #64748B; }
.card-actions { display: flex; gap: 10px; justify-content: flex-end; }
.action-btn { width: 40px; height: 40px; border-radius: 12px; border: none; background: #F8FAFC; color: #64748B; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.2s; }
.action-btn:hover { transform: scale(1.1); }
.action-btn.view:hover { background: #E0F2FE; color: #0284C7; } .action-btn.edit:hover { background: #FFEDD5; color: #EA580C; } .action-btn.delete:hover { background: #FEE2E2; color: #EF4444; }

/* --- 💎 ULTRA MODAL --- */
.modal-overlay { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.6); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-card-ultra { width: 600px; max-width: 90%; background: white; border-radius: 24px; overflow: hidden; box-shadow: 0 30px 60px -15px rgba(0,0,0,0.3); display: flex; flex-direction: column; max-height: 85vh; }
.modal-cover { height: 180px; background-size: cover; background-position: center; position: relative; color: white; display: flex; flex-direction: column; justify-content: flex-end; padding: 25px; }
.cover-gradient { position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.9) 0%, rgba(0,0,0,0.2) 60%, transparent 100%); }
.btn-close-abs { position: absolute; top: 15px; right: 15px; background: rgba(0,0,0,0.25); border: 1px solid rgba(255,255,255,0.2); color: white; width: 32px; height: 32px; border-radius: 50%; cursor: pointer; backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; transition: 0.2s; z-index: 10; }
.btn-close-abs:hover { background: #EF4444; border-color: #EF4444; transform: rotate(90deg); }
.cover-info { position: relative; z-index: 2; }
.status-badge-hero { display: inline-flex; align-items: center; gap: 6px; padding: 4px 10px; border-radius: 20px; font-size: 0.7rem; font-weight: 800; text-transform: uppercase; margin-bottom: 8px; backdrop-filter: blur(10px); }
.status-badge-hero.active { background: rgba(34, 197, 94, 0.9); color: white; }
.status-badge-hero.ended { background: rgba(100, 116, 139, 0.9); color: white; }
.status-badge-hero.upcoming { background: rgba(249, 115, 22, 0.9); color: white; }
.hero-title { margin: 0 0 6px 0; font-size: 1.6rem; letter-spacing: -0.5px; text-shadow: 0 2px 4px rgba(0,0,0,0.3); }
.hero-meta { display: flex; gap: 15px; font-size: 0.85rem; opacity: 0.9; }
.hero-meta span { display: flex; align-items: center; gap: 5px; }
.modal-body-ultra { padding: 25px; overflow-y: auto; }
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; }
.stat-box { background: #F8FAFC; padding: 12px; border-radius: 12px; border: 1px solid #F1F5F9; }
.stat-box label { font-size: 0.65rem; color: #94A3B8; font-weight: 700; letter-spacing: 0.5px; display: block; margin-bottom: 4px; }
.s-val { font-size: 0.95rem; font-weight: 700; color: #334155; display: flex; align-items: center; gap: 5px; }
.text-gold { color: #D97706; }
.section-divider { height: 1px; background: #E2E8F0; margin: 25px 0; }
.sec-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.sec-header h3 { font-size: 1rem; color: #1E293B; margin: 0; }
.btn-text { background: none; border: none; color: #EA580C; font-weight: 600; font-size: 0.8rem; cursor: pointer; }
.entries-list { display: flex; flex-direction: column; gap: 10px; }

/* ENTRY ROW & NEW BUTTON */
.entry-item { display: flex; align-items: center; gap: 12px; padding: 10px; border-radius: 12px; background: white; border: 1px solid #E2E8F0; transition: 0.2s; }
.entry-item:hover { border-color: #FED7AA; background: #FFF7ED; }
.entry-thumb { width: 40px; height: 40px; border-radius: 8px; object-fit: cover; }
.entry-info { flex: 1; }
.entry-info h4 { margin: 0; font-size: 0.9rem; color: #1E293B; }
.entry-info .author { font-size: 0.75rem; color: #64748B; }
.entry-likes { font-size: 0.85rem; font-weight: 700; color: #EF4444; display: flex; align-items: center; gap: 4px; background: #FEF2F2; padding: 4px 8px; border-radius: 8px; }

/* ✨ NÚT XEM NGAY (NEW) */
.btn-view-post { display: flex; align-items: center; gap: 4px; background: white; border: 1px solid #E2E8F0; color: #475569; padding: 6px 12px; border-radius: 8px; font-size: 0.75rem; font-weight: 700; cursor: pointer; transition: 0.2s; margin-left: 10px; }
.btn-view-post:hover { border-color: #EA580C; color: #EA580C; background: white; box-shadow: 0 2px 5px rgba(234, 88, 12, 0.15); }

.modal-footer-ultra { padding: 15px 25px; border-top: 1px solid #E2E8F0; display: flex; justify-content: flex-end; gap: 10px; background: #F8FAFC; }
.btn-sec { padding: 8px 16px; border: 1px solid #CBD5E1; background: white; border-radius: 8px; font-weight: 600; color: #475569; cursor: pointer; }
.btn-pri { padding: 8px 16px; border: none; background: #0F172A; border-radius: 8px; font-weight: 600; color: white; cursor: pointer; }
.zoom-in-enter-active, .zoom-in-leave-active { transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.zoom-in-enter-from, .zoom-in-leave-to { opacity: 0; transform: scale(0.9); }
</style>