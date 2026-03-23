<template>
  <div class="page-container">
    
    <div class="page-header anim-fade-down">
      <div class="header-content">
        <div class="title-wrapper">
          <div class="icon-box">
            <FileText :size="24" stroke-width="2.5" />
          </div>
          <div>
            <h2 class="title">Quản lý Bài viết</h2>
            <p class="subtitle">Quản lý và kiểm duyệt nội dung cộng đồng</p>
          </div>
        </div>
      </div>
      
      <div class="header-actions">
         <div class="sort-box-lux">
          <select v-model="sortOption" class="sort-select" title="Sắp xếp bài viết">
            <option value="newest">Ngày đăng (Mới nhất)</option>
            <option value="oldest">Ngày đăng (Cũ nhất)</option>
            <option value="views_desc">Lượt xem (Nhiều nhất)</option>
            <option value="alphabetical">Tên món (A-Z)</option>
          </select>
        </div>

        <div class="search-box-lux">
          <Search :size="18" class="search-icon" />
          <input v-model="searchQuery" type="text" placeholder="Tìm tên món, tác giả..." />
        </div>
        <router-link to="/admin/blacklist" class="btn-open-blacklist">
          <ShieldAlert :size="18" />
          <span>Bộ lọc Từ khóa</span>
        </router-link>
      </div>
    </div>

    <div class="tabs-lux-wrapper anim-fade-up">
      <div class="tabs-filter">
        <button 
          v-for="tab in tabs" :key="tab.key"
          :class="['tab-btn-lux', currentTab === tab.key ? 'active' : '']"
          @click="currentTab = tab.key"
        >
          {{ tab.label }}
          <span v-if="tab.key === 'pending' && pendingCount > 0" class="badge-pulse">{{ pendingCount }}</span>
        </button>
      </div>
    </div>

    <div v-if="loading" class="empty-state-lux"><Loader2 :size="32" class="spin-icon" /> <p>Đang đồng bộ dữ liệu...</p></div>
    <div v-else-if="error" class="empty-state-lux error"><AlertTriangle :size="32" /> <p>{{ error }}</p></div>

    <div v-else class="table-lux-wrapper anim-fade-up" style="--delay: 0.2s">
      <table class="data-table-lux">
        <thead>
          <tr>
            <th width="8%">MÃ</th>
            <th width="24%">MÓN ĂN</th>
            <th width="15%">DANH MỤC</th>
            <th width="15%">ĐẦU BẾP</th>
            <th width="10%">LƯỢT XEM</th>
            <th width="13%">TRẠNG THÁI</th>
            <th width="15%" class="text-right">THAO TÁC</th>
          </tr>
        </thead>
        <TransitionGroup tag="tbody" name="list-anim">
          <tr v-for="post in filteredPosts" :key="post.postID" class="table-row-lux">
            <td class="font-bold text-gray-500">#{{ post.postID }}</td>
            <td>
              <div class="post-info">
                <div class="thumb-wrapper">
                  <img :src="post.media || 'https://placehold.co/100x100?text=Food'" class="thumb" alt="Food Image">
                </div>
                <div class="info-text">
                  <h4 @click="openDetail(post)" class="post-title">{{ post.title }}</h4>
                  <small class="time-text">{{ formatDate(post.createdAt) }}</small>
                </div>
              </div>
            </td>
            <td><span class="cat-tag">{{ post.categoryName || 'Chưa phân loại' }}</span></td>
            <td>
              <div class="author-info">
                <div class="avatar-wrapper-lux">
                   <img :src="post.accountAvatar || post.authorAvatar || `https://ui-avatars.com/api/?name=${post.username || post.authorName || 'U'}&background=f1f5f9`" 
                        :class="['avatar-xs', isPostAdmin(post) ? 'border-admin' : (isPostPremium(post) ? 'border-premium' : '')]">
                   <div v-if="isPostAdmin(post)" class="vip-mini-badge" title="Quản trị viên">🛡️</div>
                   <div v-else-if="isPostPremium(post)" class="vip-mini-badge" title="Tài khoản Premium">👑</div>
                </div>
                <span :class="['author-name', isPostAdmin(post) ? 'text-admin' : (isPostPremium(post) ? 'text-premium' : '')]">
                  {{ post.username || post.authorName || 'Ẩn danh' }}
                </span>
              </div>
            </td>
            <td>
              <span class="view-count-badge">
                👁️ {{ post.views || post.viewCount || 0 }}
              </span>
            </td>
            <td>
              <span :class="['status-badge', post._status]">
                <span class="status-dot"></span>
                {{ getStatusLabel(post._status) }}
              </span>
            </td>
            <td class="text-right">
              <div class="action-group">
                <button @click="openDetail(post)" class="btn-action view" title="Xem chi tiết"><Eye :size="16" /></button>

                <template v-if="post._status === 'pending'">
                  <button @click="approvePost(post.postID)" class="btn-action approve" title="Duyệt bài"><CheckCircle :size="16" /></button>
                </template>

                <template v-if="post._status === 'active'">
                  <button @click="deactivatePost(post.postID)" class="btn-action ban" title="Gỡ bài"><Ban :size="16" /></button>
                </template>

                <template v-if="post._status === 'deactivated'">
                  <button @click="reactivatePost(post.postID)" class="btn-action restore" title="Khôi phục bài"><RotateCcw :size="16" /></button>
                </template>

                <button @click="deletePost(post.postID)" class="btn-action delete" title="Xóa vĩnh viễn"><Trash2 :size="16" /></button>
              </div>
            </td>
          </tr>

          <tr v-if="filteredPosts.length === 0">
            <td colspan="6">
              <div class="empty-state-lux">
                <div class="empty-icon-box"><Search :size="32" /></div>
                <p>Không tìm thấy bài viết nào trong mục này.</p>
              </div>
            </td>
          </tr>
        </TransitionGroup>
      </table>
    </div>

    <Transition name="fade-scale">
      <div v-if="showModal" class="modal-overlay-lux" @click.self="closeDetail">
        <div class="modal-card-lux">
          <button class="btn-close-lux" @click="closeDetail"><XCircle :size="24" /></button>
          
          <div class="modal-cover-wrapper">
            <img :src="selectedPost.media || 'https://placehold.co/800x400?text=No+Image'" class="modal-cover">
            <div class="cover-gradient"></div>
            <span :class="['status-badge absolute-badge', selectedPost._status]">{{ getStatusLabel(selectedPost._status) }}</span>
          </div>

          <div class="modal-body-lux">
            <div class="modal-meta-row">
              <span class="cat-tag">{{ selectedPost.categoryName || 'Chưa phân loại' }}</span>
              <span class="meta-dot">•</span>
              <span class="meta-time">{{ formatDate(selectedPost.createdAt) }}</span>
              <span class="meta-dot">•</span>
              <div class="author-info">
                <div class="avatar-wrapper-lux">
                   <img :src="selectedPost.accountAvatar || selectedPost.authorAvatar || `https://ui-avatars.com/api/?name=${selectedPost.username || selectedPost.authorName || 'U'}`" 
                        :class="['avatar-xs', isPostAdmin(selectedPost) ? 'border-admin' : (isPostPremium(selectedPost) ? 'border-premium' : '')]">
                   <div v-if="isPostAdmin(selectedPost)" class="vip-mini-badge" title="Quản trị viên">🛡️</div>
                   <div v-else-if="isPostPremium(selectedPost)" class="vip-mini-badge" title="Tài khoản Premium">👑</div>
                </div>
                <b :class="[isPostAdmin(selectedPost) ? 'text-admin' : (isPostPremium(selectedPost) ? 'text-premium' : '')]">
                  {{ selectedPost.username || selectedPost.authorName || 'Ẩn danh' }}
                </b>
              </div>
            </div>
            
            <h1 class="modal-title">{{ selectedPost.title }}</h1>
            <p class="modal-desc">{{ selectedPost.description || 'Bài viết này không có mô tả chi tiết.' }}</p>

            <div class="modal-stats-box">
              <div class="stat-item">
                <span class="stat-icon">👁️</span>
                <span class="stat-value">{{ selectedPost.views || selectedPost.viewCount || 0 }}</span>
                <span class="stat-label">Lượt xem</span>
              </div>
              <div class="stat-item">
                <span class="stat-icon">❤️</span>
                <span class="stat-value">{{ selectedPost.likes || selectedPost.favoriteCount || selectedPost.likeCount || 0 }}</span>
                <span class="stat-label">Lượt thích</span>
              </div>
              <div class="stat-item">
                <span class="stat-icon">⭐</span>
                <span class="stat-value">{{ (Number(selectedPost.rating || selectedPost.avgRating || selectedPost.averageRating) || 0).toFixed(1) }}</span>
                <span class="stat-label">Đánh giá</span>
              </div>
            </div>

            <div class="modal-action-zone" v-if="selectedPost._status === 'pending'">
              <div class="alert-ribbon">
                <AlertCircle :size="18" /> Bài viết đang chờ duyệt. Bạn quyết định sao?
              </div>
              <div class="btn-grid-lux">
                <button @click="approvePost(selectedPost.postID); closeDetail()" class="btn-lux-primary">
                  <CheckCircle :size="18" /> Duyệt bài ngay
                </button>
                <button @click="deactivatePost(selectedPost.postID); closeDetail()" class="btn-lux-secondary">
                  <Ban :size="18" /> Từ chối & Gỡ
                </button>
              </div>
            </div>

            <div class="modal-action-zone" v-if="selectedPost._status === 'deactivated'" style="background: #f0f9ff; border-color: #e0f2fe;">
              <div class="alert-ribbon" style="color: #0369a1;">
                <AlertCircle :size="18" /> Bài viết đã bị gỡ. Bạn có muốn khôi phục lại không?
              </div>
              <div class="btn-grid-lux" style="grid-template-columns: 1fr;">
                <button @click="reactivatePost(selectedPost.postID); closeDetail()" class="btn-lux-primary" style="background: linear-gradient(135deg, #0284c7, #0ea5e9); box-shadow: 0 10px 20px -5px rgba(2, 132, 199, 0.4);">
                  <RotateCcw :size="18" /> Khôi phục bài viết
                </button>
              </div>
            </div>

            <div class="modal-footer-zone">
              <router-link :to="`/post/${selectedPost.postID}`" target="_blank" class="btn-lux-view-post">
                <span>Xem chi tiết trên trang</span>
                <ExternalLink :size="18" />
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <transition name="toast-anim">
      <div v-if="toast.show" :class="['toast-lux', toast.type]">
        <CheckCircle v-if="toast.type === 'success'" :size="20" />
        <AlertTriangle v-else :size="20" />
        <span>{{ toast.msg }}</span>
      </div>
    </transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { FileText, Search, Loader2, AlertTriangle, Eye, Trash2, Ban, CheckCircle, XCircle, AlertCircle, ShieldAlert, RotateCcw, ExternalLink } from 'lucide-vue-next'
import api from '@/services/api'

// --- STATE ---
const posts = ref([])
const loading = ref(false)
const error = ref('')
const searchQuery = ref('')
const sortOption = ref('newest')
const currentTab = ref('all')
const showModal = ref(false)
const selectedPost = ref({})

const tabs = [
  { key: 'all', label: 'Tất cả' },
  { key: 'pending', label: 'Chờ duyệt' },
  { key: 'active', label: 'Đã duyệt' },
  { key: 'deactivated', label: 'Bị gỡ' }
]

// --- LOGIC PHÂN LOẠI TRẠNG THÁI ---
const getStatus = (post) => {
  if (post.isActive === 0) return 'deactivated'
  if (post.isApproved === 1) return 'active'
  return 'pending'
}

const getStatusLabel = (status) => {
  const map = { pending: 'Chờ duyệt', active: 'Đã duyệt', deactivated: 'Đã gỡ' }
  return map[status] || status
}

// --- LOGIC VAI TRÒ (VÉT CẠN CÁC TRƯỜNG HỢP BACKEND CÓ THỂ TRẢ VỀ) ---
const isPostAdmin = (p) => {
  if (!p) return false;
  const r = String(p.role || p.accountRole || p.authorRole || '').toLowerCase();
  return r === 'admin' || p.isAdmin === true || p.isAdmin === 1;
}
const isPostPremium = (p) => {
  if (!p) return false;
  const r = String(p.role || p.accountRole || p.authorRole || '').toLowerCase();
  return r === 'premium' || p.isPremium === true || p.isPremium === 1;
}

const formatDate = (d) => {
  if (!d) return ''
  const dt = new Date(d)
  return dt.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute:'2-digit' })
}

// --- FETCH API ---
const fetchPosts = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await api.get('/api/admin/posts')
    
    // 🔥 BƯỚC 1: In thử data bài viết đầu tiên ra F12 (Console) để xem Backend thiếu biến gì
    if (res.data && res.data.length > 0) {
      console.log("👀 Dữ liệu API Backend trả về cho 1 bài viết:", res.data[0]);
    }

    posts.value = res.data.map((p, index) => {
      // 🔥 BƯỚC 2: GIẢ LẬP GIAO DIỆN (Test thử xem UI hoạt động chưa)
      // Bỏ comment 2 dòng dưới đây để ép bài 1 thành Admin, bài 2 thành Premium
      // if (index === 0) p.isAdmin = true;
      // if (index === 1) p.isPremium = true;
      
      return { ...p, _status: getStatus(p) }
    })
  } catch (e) {
    error.value = 'Không thể tải dữ liệu: ' + (e.response?.data?.message || e.message)
  } finally {
    loading.value = false
  }
}

onMounted(fetchPosts)

// --- COMPUTED ---
const pendingCount = computed(() => posts.value.filter(p => p._status === 'pending').length)

const filteredPosts = computed(() => {
  let result = posts.value.filter(post => {
    const matchTab = currentTab.value === 'all' || post._status === currentTab.value
    const q = searchQuery.value.toLowerCase()
    const matchSearch = !q ||
      (post.title || '').toLowerCase().includes(q) ||
      (post.username || '').toLowerCase().includes(q) ||
      (post.categoryName || '').toLowerCase().includes(q)
    return matchTab && matchSearch
  })

  // Áp dụng bộ lọc sắp xếp
  if (sortOption.value === 'newest') {
    result.sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
  } else if (sortOption.value === 'oldest') {
    result.sort((a, b) => new Date(a.createdAt || 0) - new Date(b.createdAt || 0))
  } else if (sortOption.value === 'views_desc') {
    result.sort((a, b) => (b.views || b.viewCount || 0) - (a.views || a.viewCount || 0))
  } else if (sortOption.value === 'alphabetical') {
    result.sort((a, b) => (a.title || '').localeCompare(b.title || ''))
  }

  return result
})

// --- ACTIONS API ---
const approvePost = async (id) => {
  try {
    await api.put(`/api/admin/posts/approve/${id}`)
    const p = posts.value.find(p => p.postID === id)
    if (p) { p.isApproved = 1; p.isActive = 1; p._status = 'active' }
    showToast('Đã duyệt bài viết thành công!')
  } catch (e) { showToast('Lỗi: ' + (e.response?.data?.message || e.message), 'error') }
}

const deactivatePost = async (id) => {
  if (!confirm('Bạn có chắc muốn gỡ bài viết này khỏi trang chủ?')) return
  try {
    await api.put(`/api/admin/posts/deactive/${id}`)
    const p = posts.value.find(p => p.postID === id)
    if (p) { p.isActive = 0; p._status = 'deactivated' }
    showToast('Đã gỡ bài viết khỏi hệ thống.')
  } catch (e) { showToast('Lỗi: ' + (e.response?.data?.message || e.message), 'error') }
}

const reactivatePost = async (id) => {
  if (!confirm('Bạn có chắc muốn khôi phục bài viết này lên trang chủ?')) return
  try {
    // Sử dụng lại API approve để duyệt lại bài
    await api.put(`/api/admin/posts/approve/${id}`)
    const p = posts.value.find(p => p.postID === id)
    if (p) { p.isApproved = 1; p.isActive = 1; p._status = 'active' }
    showToast('Đã khôi phục bài viết thành công!')
  } catch (e) { showToast('Lỗi: ' + (e.response?.data?.message || e.message), 'error') }
}

const deletePost = async (id) => {
  if (!confirm('Hành động này sẽ XÓA VĨNH VIỄN bài viết khỏi Database. Tiếp tục?')) return
  try {
    await api.delete(`/api/admin/posts/${id}`)
    posts.value = posts.value.filter(p => p.postID !== id)
    showModal.value = false
    showToast('Đã xóa vĩnh viễn bài viết.')
  } catch (e) { showToast('Lỗi: ' + (e.response?.data?.message || e.message), 'error') }
}

// --- MODAL & TOAST ---
const openDetail = async (post) => { 
  // 1. Gán ngay dữ liệu cơ bản để hiển thị Modal không bị giật lag
  selectedPost.value = { ...post }
  showModal.value = true 

  // 2. Truy vấn API chi tiết để lấy chính xác số lượng Like, View, Rating
  try {
    const res = await api.get(`/api/posts/${post.postID}`)
    if (res.data) {
      // Đè dữ liệu chi tiết lên trên dữ liệu cơ bản hiện tại
      selectedPost.value = { ...selectedPost.value, ...res.data }
    }
  } catch (err) {
    console.warn('Không thể tải dữ liệu thống kê chi tiết:', err)
  }
}
const closeDetail = () => { showModal.value = false }

const toast = ref({ show: false, msg: '', type: 'success' })
const showToast = (msg, type = 'success') => {
  toast.value = { show: true, msg, type }
  setTimeout(() => toast.value.show = false, 3000)
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Playfair+Display:wght@800&display=swap');

/* --- KHUNG CHÍNH --- */
.page-container {
  padding: 32px 40px;
  font-family: 'Inter', sans-serif;
  background-color: #f8fafc;
  min-height: 100vh;
}

/* --- HEADER VIPRO --- */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}
.header-content { display: flex; align-items: center; }
.title-wrapper { display: flex; align-items: center; gap: 16px; }
.icon-box { 
  width: 52px; height: 52px; border-radius: 14px; 
  background: linear-gradient(135deg, #ea580c, #f59e0b); 
  color: white; display: flex; align-items: center; justify-content: center;
  box-shadow: 0 10px 20px -5px rgba(234, 88, 12, 0.4);
}
.title { font-family: 'Playfair Display', serif; font-size: 2.2rem; font-weight: 800; color: #0f172a; margin: 0; letter-spacing: -0.5px; }
.subtitle { color: #64748b; margin: 4px 0 0; font-size: 1rem; font-weight: 500; }

.header-actions { display: flex; align-items: center; gap: 16px; }

.search-box-lux {
  display: flex; align-items: center; background: white; padding: 12px 20px; 
  border-radius: 100px; border: 1px solid #e2e8f0; width: 320px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.03); transition: 0.3s;
}
.search-box-lux:focus-within { border-color: #ea580c; box-shadow: 0 4px 20px rgba(234,88,12,0.1); }
.search-icon { color: #94a3b8; }
.search-box-lux input { border: none; outline: none; margin-left: 12px; width: 100%; font-family: inherit; font-size: 0.95rem; color: #0f172a; }

.sort-box-lux { display: flex; align-items: center; background: white; padding: 12px 16px; border-radius: 100px; border: 1px solid #e2e8f0; box-shadow: 0 4px 15px rgba(0,0,0,0.03); transition: 0.3s; }
.sort-box-lux:focus-within { border-color: #ea580c; box-shadow: 0 4px 20px rgba(234,88,12,0.1); }
.sort-select { border: none; outline: none; background: transparent; font-family: inherit; font-size: 0.95rem; font-weight: 600; color: #475569; cursor: pointer; }
.view-count-badge { font-size: 0.85rem; font-weight: 600; color: #64748b; display: flex; align-items: center; gap: 6px; }

/* 🔥 NÚT MỞ BLACKLIST */
.btn-open-blacklist {
  display: flex; align-items: center; gap: 8px;
  background: #fef2f2; color: #dc2626; border: 1px solid #fecaca;
  padding: 12px 20px; border-radius: 100px; font-weight: 700;
  text-decoration: none; transition: 0.3s;
}
.btn-open-blacklist:hover {
  background: #dc2626; color: white; box-shadow: 0 8px 20px -5px rgba(220, 38, 38, 0.4);
}

/* --- TABS BỘ LỌC --- */
.tabs-lux-wrapper { margin-bottom: 24px; }
.tabs-filter { display: inline-flex; background: white; padding: 6px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.02); border: 1px solid #f1f5f9; }
.tab-btn-lux { 
  background: transparent; border: none; padding: 10px 20px; font-weight: 600; color: #64748b; 
  cursor: pointer; border-radius: 8px; transition: 0.3s; position: relative; font-size: 0.95rem;
}
.tab-btn-lux:hover { color: #0f172a; }
.tab-btn-lux.active { background: #fff7ed; color: #ea580c; box-shadow: 0 2px 8px rgba(234,88,12,0.1); }
.badge-pulse { 
  position: absolute; top: -6px; right: -6px; background: #ef4444; color: white; 
  font-size: 0.7rem; font-weight: 800; padding: 2px 8px; border-radius: 100px; 
  box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7); animation: pulseRed 2s infinite;
}
@keyframes pulseRed { 70% { box-shadow: 0 0 0 6px rgba(239,68,68,0); } 100% { box-shadow: 0 0 0 0 rgba(239,68,68,0); } }

/* --- BẢNG DỮ LIỆU --- */
.table-lux-wrapper { background: white; border-radius: 20px; box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05); border: 1px solid rgba(0,0,0,0.03); overflow: hidden; }
.data-table-lux { width: 100%; border-collapse: separate; border-spacing: 0; }
.data-table-lux th { text-align: left; padding: 18px 24px; background: #f8fafc; color: #64748b; font-weight: 700; font-size: 0.8rem; letter-spacing: 1px; border-bottom: 1px solid #e2e8f0; }
.data-table-lux td { padding: 16px 24px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.table-row-lux { transition: 0.2s; }
.table-row-lux:hover { background: #fafafa; }

/* Thành phần trong bảng */
.post-info { display: flex; gap: 16px; align-items: center; }
.thumb-wrapper { width: 64px; height: 64px; border-radius: 12px; overflow: hidden; flex-shrink: 0; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
.thumb { width: 100%; height: 100%; object-fit: cover; }
.post-title { margin: 0 0 4px 0; font-size: 1.05rem; font-weight: 700; color: #0f172a; cursor: pointer; transition: 0.2s; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.post-title:hover { color: #ea580c; }
.time-text { color: #94a3b8; font-size: 0.85rem; font-weight: 500; }

.cat-tag { background: #f1f5f9; padding: 6px 12px; border-radius: 8px; font-size: 0.85rem; font-weight: 600; color: #475569; }
.author-info { display: flex; align-items: center; gap: 10px; }
.avatar-xs { width: 32px; height: 32px; border-radius: 50%; border: 2px solid #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.1); object-fit: cover; }
.avatar-wrapper-lux { position: relative; width: 32px; height: 32px; flex-shrink: 0; }
.avatar-xs { width: 100%; height: 100%; border-radius: 50%; border: 2px solid #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.1); object-fit: cover; transition: 0.3s; }
.avatar-xs.border-admin { border-color: #3b82f6; box-shadow: 0 0 8px rgba(59, 130, 246, 0.4); }
.avatar-xs.border-premium { border-color: #f59e0b; box-shadow: 0 0 8px rgba(245, 158, 11, 0.4); }
.vip-mini-badge { position: absolute; bottom: -4px; right: -6px; font-size: 11px; background: white; border-radius: 50%; padding: 2px; box-shadow: 0 2px 4px rgba(0,0,0,0.2); line-height: 1; z-index: 2; }
.author-name { font-weight: 600; color: #1e293b; font-size: 0.95rem; }
.text-admin { color: #2563eb !important; font-weight: 800; }
.text-premium { color: #d97706 !important; font-weight: 800; }

/* Status Badges */
.status-badge { padding: 6px 14px; border-radius: 100px; font-size: 0.8rem; font-weight: 700; display: inline-flex; align-items: center; gap: 6px; }
.status-dot { width: 6px; height: 6px; border-radius: 50%; }
.status-badge.pending { background: #fffbeb; color: #d97706; } .status-badge.pending .status-dot { background: #d97706; }
.status-badge.active { background: #f0fdf4; color: #16a34a; } .status-badge.active .status-dot { background: #16a34a; }
.status-badge.deactivated { background: #fef2f2; color: #dc2626; } .status-badge.deactivated .status-dot { background: #dc2626; }

/* Action Buttons */
.action-group { display: flex; justify-content: flex-end; gap: 8px; }
.btn-action { width: 36px; height: 36px; border-radius: 10px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.view { background: #f1f5f9; color: #475569; } .view:hover { background: #e2e8f0; color: #0f172a; }
.approve { background: #dcfce7; color: #16a34a; } .approve:hover { background: #16a34a; color: white; }
.ban { background: #ffedd5; color: #ea580c; } .ban:hover { background: #ea580c; color: white; }
.delete { background: #fee2e2; color: #dc2626; } .delete:hover { background: #dc2626; color: white; }
.restore { background: #e0f2fe; color: #0284c7; } .restore:hover { background: #0284c7; color: white; }

/* --- MODAL LUXURY --- */
.modal-overlay-lux { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.7); display: flex; align-items: center; justify-content: center; z-index: 9999; backdrop-filter: blur(8px); }
.modal-card-lux { background: white; width: 700px; max-height: 90vh; overflow-y: auto; border-radius: 24px; position: relative; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.5); }
.modal-card-lux::-webkit-scrollbar { width: 6px; }
.modal-card-lux::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.btn-close-lux { position: absolute; top: 20px; right: 20px; background: rgba(0,0,0,0.4); color: white; border: none; border-radius: 50%; padding: 6px; cursor: pointer; z-index: 10; transition: 0.2s; backdrop-filter: blur(4px); }
.btn-close-lux:hover { background: #ef4444; transform: rotate(90deg); }

.modal-cover-wrapper { position: relative; height: 280px; }
.modal-cover { width: 100%; height: 100%; object-fit: cover; }
.cover-gradient { position: absolute; inset: 0; background: linear-gradient(0deg, rgba(255,255,255,1) 0%, transparent 60%); }
.absolute-badge { position: absolute; top: 20px; left: 20px; box-shadow: 0 4px 15px rgba(0,0,0,0.2); }

.modal-body-lux { padding: 0 32px 32px 32px; position: relative; z-index: 2; margin-top: -20px; }
.modal-meta-row { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; color: #64748b; font-size: 0.9rem; font-weight: 500; }
.meta-dot { color: #cbd5e1; }
.modal-title { font-family: 'Playfair Display', serif; font-size: 2rem; font-weight: 800; color: #0f172a; margin: 0 0 16px 0; line-height: 1.3; }
.modal-desc { color: #475569; line-height: 1.7; font-size: 1.05rem; margin-bottom: 24px; }

.modal-stats-box { display: flex; gap: 16px; margin-bottom: 24px; }
.stat-item { flex: 1; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 16px; padding: 16px; display: flex; flex-direction: column; align-items: center; justify-content: center; transition: 0.3s; }
.stat-item:hover { background: #fff7ed; border-color: #ffedd5; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(234, 88, 12, 0.05); }
.stat-icon { font-size: 1.5rem; margin-bottom: 8px; filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1)); }
.stat-value { font-size: 1.25rem; font-weight: 800; color: #0f172a; margin-bottom: 4px; line-height: 1; }
.stat-label { font-size: 0.75rem; color: #64748b; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }

.modal-action-zone { background: #fff7ed; border: 1px solid #ffedd5; border-radius: 16px; padding: 24px; margin-bottom: 24px; }
.alert-ribbon { color: #c2410c; font-weight: 700; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.btn-grid-lux { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.btn-lux-primary, .btn-lux-secondary { padding: 14px; border-radius: 12px; font-weight: 700; font-size: 1rem; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px; transition: 0.3s; border: none; }
.btn-lux-primary { background: linear-gradient(135deg, #16a34a, #22c55e); color: white; box-shadow: 0 10px 20px -5px rgba(22, 163, 74, 0.4); }
.btn-lux-primary:hover { transform: translateY(-2px); box-shadow: 0 15px 25px -5px rgba(22, 163, 74, 0.5); }
.btn-lux-secondary { background: white; color: #ea580c; border: 1px solid #fdba74; }
.btn-lux-secondary:hover { background: #fff7ed; border-color: #ea580c; }

.modal-footer-zone { text-align: right; border-top: 1px dashed #e2e8f0; padding-top: 24px; }
.btn-lux-view-post {
  display: inline-flex; align-items: center; gap: 8px; background: #f8fafc; color: #3b82f6; 
  border: 1px solid #e2e8f0; padding: 12px 24px; border-radius: 12px; font-weight: 700; 
  text-decoration: none; transition: 0.3s;
}
.btn-lux-view-post:hover {
  background: #eff6ff; color: #2563eb; border-color: #bfdbfe; 
  transform: translateY(-2px); box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

/* --- TOAST & EMPTY STATES --- */
.empty-state-lux { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 60px 20px; color: #94a3b8; font-weight: 500; }
.empty-icon-box { background: #f1f5f9; padding: 20px; border-radius: 50%; margin-bottom: 16px; color: #cbd5e1; }
.empty-state-lux.error { color: #ef4444; }

.toast-lux { position: fixed; bottom: 32px; right: 32px; padding: 16px 24px; border-radius: 16px; font-weight: 600; z-index: 10000; box-shadow: 0 20px 40px -10px rgba(0,0,0,0.2); display: flex; align-items: center; gap: 12px; font-size: 1rem; color: white; }
.toast-lux.success { background: #1f2937; border-left: 4px solid #10b981; }
.toast-lux.error { background: #7f1d1d; border-left: 4px solid #ef4444; }

/* ANIMATIONS */
.anim-fade-down { animation: fadeDown 0.6s cubic-bezier(0.16, 1, 0.3, 1); }
.anim-fade-up { animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) both; animation-delay: var(--delay, 0s); }
@keyframes fadeDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.list-anim-enter-active, .list-anim-leave-active { transition: all 0.4s ease; }
.list-anim-enter-from, .list-anim-leave-to { opacity: 0; transform: translateX(-20px); }

.fade-scale-enter-active, .fade-scale-leave-active { transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; transform: scale(0.95); }

.toast-anim-enter-active, .toast-anim-leave-active { transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); }
.toast-anim-enter-from, .toast-anim-leave-to { opacity: 0; transform: translateY(30px) scale(0.9); }
</style>