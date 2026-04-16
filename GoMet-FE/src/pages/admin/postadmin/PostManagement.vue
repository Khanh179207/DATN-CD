<template>
  <div class="page-container">
    
    <div class="page-header anim-fade-down">
      <div class="header-content">
        <div class="title-group">
          <div class="icon-box-lux post-icon">
            <FileText :size="26" stroke-width="2.5" />
          </div>
          <div>
            <h2 class="title">Quản lý Bài viết</h2>
            <p class="subtitle">Kiểm duyệt và duy trì chất lượng cộng đồng Gomet</p>
          </div>
        </div>
      </div>
      
      <div class="header-actions">
         <div class="sort-box-lux">
          <select v-model="selectedCategory" class="sort-select" title="Lọc theo danh mục">
            <option value="all">Tất cả danh mục</option>
            <option v-for="cat in categories" :key="cat.categoryID" :value="cat.categoryID">
              {{ cat.categoryName }}
            </option>
          </select>
        </div>
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

    <div v-if="loading" class="empty-state-lux"><Loader2 :size="36" class="spin-icon" /> <p>Đang đồng bộ dữ liệu...</p></div>
    <div v-else-if="error" class="empty-state-lux error"><AlertTriangle :size="36" /> <p>{{ error }}</p></div>

    <div v-else class="table-lux-wrapper anim-fade-up" style="--delay: 0.1s">
      <table class="data-table-lux">
        <thead>
          <tr>
            <th width="8%">MÃ</th>
            <th width="26%">MÓN ĂN</th>
            <th width="14%">DANH MỤC</th>
            <th width="16%">ĐẦU BẾP</th>
            <th width="10%">LƯỢT XEM</th>
            <th width="13%">TRẠNG THÁI</th>
            <th width="13%" class="text-right">THAO TÁC</th>
          </tr>
        </thead>
        <TransitionGroup tag="tbody" name="list-anim">
          <tr v-for="post in filteredPosts" :key="post.postID" class="table-row-lux">
            <td class="font-bold text-gray-500">#{{ post.postID }}</td>
            <td>
              <div class="post-info">
                <div class="thumb-wrapper">
                  <img :src="post.media || 'https://placehold.co/100x100?text=Gomet'" class="thumb" alt="Food Image">
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
                <Eye :size="14" /> {{ post.views || post.viewCount || 0 }}
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

                <template v-if="['active', 'pending', 'user_deactivated', 'hidden_pending', 'hidden_active'].includes(post._status)">
                  <button @click="askRejectAction(post)" class="btn-action ban" title="Từ chối/Gỡ bài"><Ban :size="16" /></button>
                </template>

              <template v-if="post._status === 'banned' || post._status === 'rejected'">
                  <button @click="reactivatePost(post.postID)" class="btn-action restore" title="Khôi phục bài"><RotateCcw :size="16" /></button>
                </template>
                
                </div>
            </td>
          </tr>

          <tr v-if="filteredPosts.length === 0">
            <td colspan="7">
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
            <img :src="selectedPost.media || 'https://placehold.co/800x400?text=Gomet'" class="modal-cover">
            <div class="cover-gradient"></div>
            <span :class="['status-badge absolute-badge', selectedPost._status]">
                <span class="status-dot"></span> {{ getStatusLabel(selectedPost._status) }}
            </span>
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

            <div v-if="(selectedPost._status === 'banned' || selectedPost._status === 'rejected') && selectedPost.rejectReason" class="p-detail-ban-reason">
              <div class="ban-header">
                <AlertTriangle :size="16" /> Thông tin gỡ/từ chối bài viết
              </div>
              <div class="ban-info-grid">
                <div class="ban-info-item">
                  <span class="info-lbl">Lý do vi phạm:</span>
                  <span class="info-val reason-text">{{ selectedPost.rejectReason }}</span>
                </div>
                <div v-if="selectedPost.rejectedAt" class="ban-info-item">
                  <span class="info-lbl">Thời gian xử lý:</span>
                  <span class="info-val time-text">{{ formatDate(selectedPost.rejectedAt) }}</span>
                </div>
              </div>
            </div>

            <div class="modal-action-zone" v-if="['pending', 'active', 'user_deactivated', 'hidden_pending', 'hidden_active'].includes(selectedPost._status)">
              <div class="alert-ribbon">
                <AlertCircle :size="18" />
                <span v-if="selectedPost._status === 'pending'">Bài viết đang chờ duyệt. Bạn quyết định sao?</span>
                <span v-else-if="selectedPost._status === 'active'">Bài viết đang hiển thị. Bạn có muốn gỡ bài này?</span>
                <span v-else-if="selectedPost._status === 'user_deactivated'">Bài viết này đang bị ẩn theo tài khoản. Bạn có muốn gỡ vĩnh viễn?</span>
                <span v-else>Bài viết này đang được tác giả tự ẩn. Bạn có muốn gỡ vĩnh viễn?</span>
              </div>
              <div class="btn-grid-lux" :class="{ 'single-action': selectedPost._status !== 'pending' }">
                <button v-if="selectedPost._status === 'pending'" @click="approvePost(selectedPost.postID); closeDetail()" class="btn-lux-primary">
                  <CheckCircle :size="18" /> Duyệt bài ngay
                </button>
                <button @click="askRejectAction(selectedPost); closeDetail()" class="btn-lux-secondary">
                  <Ban :size="18" /> 
                  <span v-if="selectedPost._status === 'pending'">Từ chối & Gỡ</span>
                  <span v-else>Gỡ bài viết</span>
                </button>
              </div>
            </div>

            <div 
              class="modal-action-zone" 
              v-if="selectedPost._status === 'banned' || selectedPost._status === 'rejected'" 
              style="background: #f0f9ff; border-color: #e0f2fe; margin-top: 24px;">
              <div class="alert-ribbon" style="color: #0369a1;">
                <AlertCircle :size="18" /> Bài viết đã bị gỡ. Bạn có muốn khôi phục lại không?
              </div>
              <div class="btn-grid-lux" style="grid-template-columns: 1fr;">
                <button @click="reactivatePost(selectedPost.postID)" class="btn-lux-primary" style="background: linear-gradient(135deg, #0284c7, #0ea5e9); box-shadow: 0 10px 20px -5px rgba(2, 132, 199, 0.4);">
                  <RotateCcw :size="18" /> Khôi phục bài viết
                </button>
              </div>
            </div>

            <div class="modal-footer-zone">
              <router-link :to="`/post/${selectedPost.postID}`" target="_blank" class="btn-lux-view-post">
                <span>Xem giao diện người dùng</span>
                <ExternalLink :size="18" />
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <Transition name="fade-scale">
      <div v-if="rejectModal.show" class="modal-overlay-lux" @click.self="rejectModal.show = false">
        <div class="reject-modal-card">
          <div class="action-icon">
            <Ban :size="28" />
          </div>
          <h3>Từ chối / Gỡ bài viết?</h3>
          <p>Xác nhận gỡ bài viết <strong>"{{ rejectModal.title }}"</strong> khỏi hệ thống?</p>
          
          <div class="reason-input-group">
            <label for="rejectReason">Lý do từ chối (bắt buộc):</label>
            <textarea 
              id="rejectReason" 
              v-model="rejectModal.reason" 
              placeholder="Ví dụ: Hình ảnh mờ, vi phạm tiêu chuẩn cộng đồng..." 
              rows="3" 
              class="reason-textarea"
            ></textarea>
            <span v-if="rejectModal.showError" class="error-msg">Bạn phải nhập lý do vi phạm!</span>
          </div>

          <div class="action-btns">
            <button class="btn-cancel" @click="rejectModal.show = false">Hủy bỏ</button>
            <button class="btn-confirm btn-danger" @click="confirmRejectAction">Xác nhận Gỡ</button>
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
import { useRoute } from 'vue-router'
import { FileText, Search, Loader2, AlertTriangle, Eye, Ban, CheckCircle, XCircle, AlertCircle, ShieldAlert, RotateCcw, ExternalLink } from 'lucide-vue-next'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const route = useRoute()

// --- STATE ---
const posts = ref([])
const categories = ref([])
const loading = ref(false)
const error = ref('')
const searchQuery = ref('')
const sortOption = ref('newest')
const selectedCategory = ref('all')
const currentTab = ref('all')
const showModal = ref(false)
const selectedPost = ref({})

const rejectModal = ref({ show: false, postId: null, title: '', reason: '', showError: false })

const tabs = [
  { key: 'all', label: 'Tất cả' },
  { key: 'pending', label: 'Chờ duyệt' },
  { key: 'active', label: 'Đang hiển thị' },
  { key: 'hidden', label: 'User tự ẩn' },
  { key: 'deactivated', label: 'Bị Admin gỡ' },
  { key: 'user_deactivated', label: 'User ẩn tài khoản' }
]

// --- LOGIC ---
const getStatus = (post) => {
  const a = post.isActive;
  const ap = post.isApproved;

  if (a === -2) return 'user_deactivated';
  if (a === 1 && ap === 1) return 'active';             
  if (a === 1 && ap === 0) return 'pending';            
  if (a === 0 && ap === 0) return 'hidden_pending';     
  if (a === 0 && ap === 1) return 'hidden_active';      
  if (a === -1 && ap === 1) return 'banned';            
  if (a === -1 && ap === 0) return 'rejected';          
  
  return 'unknown';
}

const getStatusLabel = (status) => {
  const map = { 
    active: 'Đang hiển thị', 
    pending: 'Chờ duyệt', 
    hidden_pending: 'User tự ẩn (Đang chờ)', 
    hidden_active: 'User tự ẩn', 
    banned: 'Bị Admin gỡ', 
    rejected: 'Bị từ chối',
    user_deactivated: 'User ẩn tài khoản'
  }
  return map[status] || 'Không xác định'
}

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

// --- FETCH DATA ---
const fetchPosts = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await api.get('/api/admin/posts')
    posts.value = res.data.map(p => ({ ...p, _status: getStatus(p) }))
  } catch (e) {
    error.value = 'Không thể tải dữ liệu: ' + (e.response?.data?.message || e.message)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await api.get('/api/admin/categories')
    categories.value = res.data
  } catch (e) {
    console.warn('Lỗi tải danh mục:', e)
  }
}

onMounted(async () => {
  // Tải danh mục trước
  await fetchCategories()
  // Nếu URL có tham số categoryID (chuyển từ trang danh mục sang), tự động chọn danh mục đó
  if (route.query.categoryID) {
    selectedCategory.value = Number(route.query.categoryID) || route.query.categoryID
  }
  fetchPosts()
})

// --- COMPUTED DATA ---
const pendingCount = computed(() => posts.value.filter(p => p._status === 'pending').length)

const filteredPosts = computed(() => {
  let result = posts.value.filter(post => {
    const isHiddenTab = currentTab.value === 'hidden' && (post._status === 'hidden_pending' || post._status === 'hidden_active');
    const isDeactivatedTab = currentTab.value === 'deactivated' && (post._status === 'banned' || post._status === 'rejected');
    const isExactMatch = post._status === currentTab.value;
    
    const matchTab = currentTab.value === 'all' || isHiddenTab || isDeactivatedTab || isExactMatch;
    
    const matchCategory = selectedCategory.value === 'all' || 
                          post.categoryID === selectedCategory.value || 
                          post.categoryId === selectedCategory.value || 
                          post.categoryName === categories.value.find(c => c.categoryID === selectedCategory.value)?.categoryName;

    const q = searchQuery.value.toLowerCase()
    const matchSearch = !q ||
      (post.title || '').toLowerCase().includes(q) ||
      (post.username || '').toLowerCase().includes(q) ||
      (post.categoryName || '').toLowerCase().includes(q)
    return matchTab && matchCategory && matchSearch
  })

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

// --- ACTIONS ---
const approvePost = async (id) => {
  try {
    const payload = {
      adminId: authStore.user?.accountID || authStore.user?.id || 0,
      adminName: authStore.user?.username || authStore.user?.fullName || 'Admin'
    };
    await api.put(`/api/admin/posts/approve/${id}`, payload)
    
    const p = posts.value.find(p => p.postID === id)
    if (p) { 
        p.isApproved = 1; p.isActive = 1; 
        p._status = getStatus(p); p.rejectReason = null; 
    }
    showToast('Đã duyệt bài viết thành công!')
  } catch (e) { showToast('Lỗi: ' + (e.response?.data?.message || e.message), 'error') }
}

const askRejectAction = (post) => {
  rejectModal.value = { show: true, postId: post.postID, title: post.title, reason: '', showError: false }
}

const confirmRejectAction = async () => {
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
    await api.put(`/api/admin/posts/${rejectModal.value.postId}/reject`, payload);
    
    const p = posts.value.find(p => p.postID === rejectModal.value.postId)
    if (p) { 
      p.isActive = -1;
      p._status = getStatus(p);
      p.rejectReason = payload.reason; p.rejectedAt = new Date().toISOString(); 
    }
    
    rejectModal.value.show = false;
    showToast('Đã gỡ bài viết thành công!')
  } catch (e) { showToast('Lỗi: ' + (e.response?.data?.message || e.message), 'error') }
}

const reactivatePost = async (id) => {
  if (!confirm('Bạn có chắc muốn khôi phục bài viết này lên hệ thống?')) return;
  
  try {
    const payload = {
      adminId: authStore.user?.accountID || authStore.user?.id || 0,
      adminName: authStore.user?.username || authStore.user?.fullName || 'Admin'
    };
    
    await api.put(`/api/admin/posts/${id}/restore`, payload);
    
    const p = posts.value.find(post => post.postID === id);
    if (p) {
      p.isActive = 1;
      p.isApproved = 1;
      p.rejectReason = null;
      p.rejectedAt = null;
      p._status = getStatus(p);
    }
    
    if (selectedPost.value?.postID === id) closeDetail();
    
    showToast('Đã khôi phục bài viết bị gỡ thành công!');
  } catch (e) {
    showToast('Lỗi: ' + (e.response?.data?.message || e.message), 'error');
  }
}

const openDetail = async (post) => { 
  selectedPost.value = { ...post }
  showModal.value = true 
  try {
    const res = await api.get(`/api/posts/${post.postID}`)
    if (res.data) {
      selectedPost.value = { ...selectedPost.value, ...res.data, rejectReason: selectedPost.value.rejectReason, rejectedAt: selectedPost.value.rejectedAt }
    }
  } catch (err) { console.warn('Không thể tải chi tiết:', err) }
}
const closeDetail = () => { showModal.value = false }

// --- TOAST ---
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
  padding: 40px 48px;
  font-family: 'Inter', sans-serif;
  background-color: #F8F9FA;
  min-height: 100vh;
}

/* --- HEADER VIPRO --- */
.page-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px;
}
.header-content { display: flex; align-items: center; }
.title-group { display: flex; align-items: center; gap: 20px; }
.icon-box-lux { 
  width: 56px; height: 56px; border-radius: 16px; 
  display: flex; align-items: center; justify-content: center; font-size: 1.4rem;
}
.post-icon {
  background: linear-gradient(135deg, #EA580C, #FF7300); 
  color: white;
  box-shadow: 0 10px 20px -5px rgba(234, 88, 12, 0.4);
}
.title { font-family: 'Playfair Display', serif; font-size: 2.2rem; font-weight: 800; color: #0F172A; margin: 0; }
.subtitle { color: #64748B; margin: 4px 0 0; font-size: 1rem; font-weight: 500; }

.header-actions { display: flex; align-items: center; gap: 16px; }

.search-box-lux {
  display: flex; align-items: center; background: white; padding: 12px 20px; 
  border-radius: 14px; border: 1px solid #E2E8F0; width: 340px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.02); transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}
.search-box-lux:focus-within { border-color: #EA580C; box-shadow: 0 4px 20px rgba(234, 88, 12, 0.15); }
.search-icon { color: #94A3B8; transition: 0.3s; }
.search-box-lux:focus-within .search-icon { color: #EA580C; }
.search-box-lux input { border: none; outline: none; margin-left: 12px; width: 100%; font-family: inherit; font-size: 0.95rem; color: #0F172A; font-weight: 500; }

.sort-box-lux { 
  display: flex; align-items: center; background: white; padding: 12px 16px; 
  border-radius: 14px; border: 1px solid #E2E8F0; box-shadow: 0 4px 15px rgba(0,0,0,0.02); transition: 0.3s; 
}
.sort-box-lux:hover { border-color: #CBD5E1; }
.sort-select { border: none; outline: none; background: transparent; font-family: inherit; font-size: 0.95rem; font-weight: 600; color: #475569; cursor: pointer; }

/* NÚT MỞ BLACKLIST */
.btn-open-blacklist {
  display: flex; align-items: center; gap: 8px;
  background: #FFF7ED; color: #EA580C; border: 1px solid #FED7AA;
  padding: 12px 20px; border-radius: 14px; font-weight: 700; font-size: 0.95rem;
  text-decoration: none; transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}
.btn-open-blacklist:hover {
  background: #EA580C; color: white; box-shadow: 0 8px 20px -5px rgba(234, 88, 12, 0.4); transform: translateY(-2px);
}

/* --- TABS --- */
.tabs-lux-wrapper { margin-bottom: 28px; }
.tabs-filter { display: inline-flex; background: white; padding: 6px; border-radius: 14px; box-shadow: 0 4px 10px rgba(0,0,0,0.02); border: 1px solid #F1F5F9; }
.tab-btn-lux { 
  background: transparent; border: none; padding: 10px 24px; font-weight: 600; color: #64748B; 
  cursor: pointer; border-radius: 10px; transition: 0.3s; position: relative; font-size: 0.95rem;
}
.tab-btn-lux:hover { color: #0F172A; }
.tab-btn-lux.active { background: linear-gradient(135deg, #FF7300, #EA580C); color: white; box-shadow: 0 4px 12px rgba(234,88,12,0.25); }
.badge-pulse { 
  position: absolute; top: -6px; right: -6px; background: #EF4444; color: white; 
  font-size: 0.75rem; font-weight: 800; padding: 2px 8px; border-radius: 100px; 
  animation: pulseRed 2s infinite; border: 2px solid white;
}
@keyframes pulseRed { 70% { box-shadow: 0 0 0 6px rgba(239,68,68,0); } 100% { box-shadow: 0 0 0 0 rgba(239,68,68,0); } }

/* --- BẢNG DỮ LIỆU --- */
.table-lux-wrapper { background: white; border-radius: 24px; box-shadow: 0 10px 40px -10px rgba(0,0,0,0.06); border: 1px solid #E2E8F0; overflow-x: auto; }
.data-table-lux { width: 100%; min-width: 900px; border-collapse: separate; border-spacing: 0; }
.data-table-lux th { text-align: left; padding: 20px 24px; background: #F8FAFC; color: #64748B; font-weight: 800; font-size: 0.8rem; letter-spacing: 1px; border-bottom: 1px solid #E2E8F0; }
.data-table-lux td { padding: 18px 24px; border-bottom: 1px solid #F1F5F9; vertical-align: middle; }
.table-row-lux { transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); }
.table-row-lux:hover { background: #FAFAFA; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(0,0,0,0.02); }

.post-info { display: flex; gap: 16px; align-items: center; }
.thumb-wrapper { width: 68px; height: 68px; border-radius: 14px; overflow: hidden; flex-shrink: 0; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
.thumb { width: 100%; height: 100%; object-fit: cover; transition: 0.3s; }
.table-row-lux:hover .thumb { transform: scale(1.05); }
.post-title { margin: 0 0 6px 0; font-size: 1.05rem; font-weight: 700; color: #0F172A; cursor: pointer; transition: 0.2s; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.4; }
.post-title:hover { color: #EA580C; }
.time-text { color: #94A3B8; font-size: 0.85rem; font-weight: 600; }

.cat-tag { background: #F1F5F9; padding: 6px 12px; border-radius: 8px; font-size: 0.85rem; font-weight: 700; color: #475569; }
.author-info { display: flex; align-items: center; gap: 12px; }
.avatar-wrapper-lux { position: relative; width: 36px; height: 36px; flex-shrink: 0; }
.avatar-xs { width: 100%; height: 100%; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.1); object-fit: cover; }
.avatar-xs.border-admin { border-color: #3B82F6; }
.avatar-xs.border-premium { border-color: #F59E0B; }
.vip-mini-badge { position: absolute; bottom: -4px; right: -6px; font-size: 11px; background: white; border-radius: 50%; padding: 2px; box-shadow: 0 2px 4px rgba(0,0,0,0.2); z-index: 2; }
.author-name { font-weight: 700; color: #1E293B; font-size: 0.95rem; }
.text-admin { color: #2563EB !important; }
.text-premium { color: #D97706 !important; }

.view-count-badge { font-size: 0.9rem; font-weight: 700; color: #64748B; display: flex; align-items: center; gap: 6px; }

/* Status Badges */
.status-badge { padding: 8px 16px; border-radius: 100px; font-size: 0.85rem; font-weight: 700; display: inline-flex; align-items: center; gap: 8px; }
.status-dot { width: 8px; height: 8px; border-radius: 50%; }

/* (1 1) Đang hiển thị -> Xanh lá */
.status-badge.active { background: #F0FDF4; color: #16A34A; } 
.status-badge.active .status-dot { background: #16A34A; }

/* (1 0) Chờ duyệt -> Cam */
.status-badge.pending { background: #FFFBEB; color: #D97706; } 
.status-badge.pending .status-dot { background: #D97706; }

/* (0 0) (0 1) User tự ẩn -> Xám Nhạt */
.status-badge.hidden_pending, .status-badge.hidden_active { background: #F1F5F9; color: #64748B; } 
.status-badge.hidden_pending .status-dot, .status-badge.hidden_active .status-dot { background: #94A3B8; }

/* (-1 0) (-1 1) Admin gỡ -> Đỏ */
.status-badge.banned, .status-badge.rejected { background: #FEF2F2; color: #DC2626; } 
.status-badge.banned .status-dot, .status-badge.rejected .status-dot { background: #DC2626; }

/* (-2) User ẩn tài khoản -> Tím */
.status-badge.user_deactivated { background: #F5F3FF; color: #7C3AED; }
.status-badge.user_deactivated .status-dot { background: #7C3AED; }

.btn-grid-lux.single-action { grid-template-columns: 1fr; }

/* Action Buttons */
.action-group { display: flex; justify-content: flex-end; gap: 10px; }
.btn-action { width: 38px; height: 38px; border-radius: 12px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 1rem; transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); background: #F1F5F9; color: #64748B; }
.btn-action:hover { transform: translateY(-2px); }
.btn-action:active { transform: scale(0.9); }
.view:hover { background: #0F172A; color: white; box-shadow: 0 4px 10px rgba(15, 23, 42, 0.2); }
.approve:hover { background: #16A34A; color: white; box-shadow: 0 4px 10px rgba(22, 163, 74, 0.3); }
.ban:hover { background: #EA580C; color: white; box-shadow: 0 4px 10px rgba(234, 88, 12, 0.3); }
.restore:hover { background: #0284C7; color: white; box-shadow: 0 4px 10px rgba(2, 132, 199, 0.3); }

/* --- MODAL LUXURY --- */
.modal-overlay-lux { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.75); display: flex; align-items: center; justify-content: center; z-index: 9999; backdrop-filter: blur(10px); }
.modal-card-lux { background: white; width: 750px; max-height: 90vh; overflow-y: auto; border-radius: 28px; position: relative; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.5); }
.modal-card-lux::-webkit-scrollbar { width: 6px; }
.modal-card-lux::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }
.btn-close-lux { position: absolute; top: 20px; right: 20px; background: rgba(0,0,0,0.5); color: white; border: none; border-radius: 50%; padding: 8px; cursor: pointer; z-index: 10; transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); backdrop-filter: blur(4px); }
.btn-close-lux:hover { background: #EF4444; transform: rotate(90deg) scale(1.1); }

.modal-cover-wrapper { position: relative; height: 300px; }
.modal-cover { width: 100%; height: 100%; object-fit: cover; }
.cover-gradient { position: absolute; inset: 0; background: linear-gradient(0deg, rgba(255,255,255,1) 0%, transparent 70%); }
.absolute-badge { position: absolute; top: 24px; left: 24px; box-shadow: 0 4px 15px rgba(0,0,0,0.2); }

.modal-body-lux { padding: 0 40px 40px 40px; position: relative; z-index: 2; margin-top: -30px; }
.modal-meta-row { display: flex; align-items: center; gap: 14px; margin-bottom: 20px; color: #64748B; font-size: 0.95rem; font-weight: 600; }
.meta-dot { color: #CBD5E1; }
.modal-title { font-family: 'Playfair Display', serif; font-size: 2.2rem; font-weight: 800; color: #0F172A; margin: 0 0 16px 0; line-height: 1.3; }
.modal-desc { color: #475569; line-height: 1.7; font-size: 1.1rem; margin-bottom: 28px; }

.modal-stats-box { display: flex; gap: 16px; margin-bottom: 28px; }
.stat-item { flex: 1; background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 18px; padding: 20px; display: flex; flex-direction: column; align-items: center; justify-content: center; transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); }
.stat-item:hover { background: #FFF7ED; border-color: #FED7AA; transform: translateY(-4px); box-shadow: 0 10px 20px rgba(234, 88, 12, 0.08); }
.stat-icon { font-size: 1.8rem; margin-bottom: 8px; filter: drop-shadow(0 4px 6px rgba(0,0,0,0.1)); }
.stat-value { font-size: 1.4rem; font-weight: 800; color: #0F172A; margin-bottom: 4px; line-height: 1; }
.stat-label { font-size: 0.8rem; color: #64748B; font-weight: 800; text-transform: uppercase; letter-spacing: 0.5px; }

.modal-action-zone { background: #FFF7ED; border: 1.5px solid #FFEDD5; border-radius: 20px; padding: 24px; margin-bottom: 24px; }
.alert-ribbon { color: #C2410C; font-weight: 800; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; font-size: 1.05rem; }
.btn-grid-lux { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.btn-lux-primary, .btn-lux-secondary { padding: 16px; border-radius: 14px; font-weight: 800; font-size: 1.05rem; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px; transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); border: none; }
.btn-lux-primary { background: linear-gradient(135deg, #16A34A, #22C55E); color: white; box-shadow: 0 10px 20px -5px rgba(22, 163, 74, 0.4); }
.btn-lux-primary:hover { transform: translateY(-2px); box-shadow: 0 15px 25px -5px rgba(22, 163, 74, 0.5); }
.btn-lux-secondary { background: white; color: #EA580C; border: 1.5px solid #FDBA74; }
.btn-lux-secondary:hover { background: #FFF7ED; border-color: #EA580C; transform: translateY(-2px); box-shadow: 0 10px 20px rgba(234, 88, 12, 0.1); }
.btn-lux-primary:active, .btn-lux-secondary:active { transform: scale(0.96); }

.modal-footer-zone { text-align: right; border-top: 1px dashed #E2E8F0; padding-top: 24px; }
.btn-lux-view-post { display: inline-flex; align-items: center; gap: 8px; background: #F1F5F9; color: #0F172A; padding: 14px 28px; border-radius: 14px; font-weight: 800; text-decoration: none; transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); }
.btn-lux-view-post:hover { background: #0F172A; color: white; transform: translateY(-2px); box-shadow: 0 8px 20px rgba(15, 23, 42, 0.2); }

/* LÝ DO VI PHẠM */
.p-detail-ban-reason { margin-bottom: 24px; background: #FFF5F5; border-radius: 16px; border: 1.5px solid #FECACA; overflow: hidden; }
.ban-header { background: #FEF2F2; padding: 14px 20px; color: #DC2626; font-weight: 800; font-size: 0.95rem; display: flex; align-items: center; gap: 8px; border-bottom: 1px solid #FEE2E2; }
.ban-info-grid { padding: 20px; display: flex; flex-direction: column; gap: 16px; }
.info-lbl { font-size: 0.85rem; color: #991B1B; font-weight: 800; text-transform: uppercase; }
.reason-text { font-style: italic; background: white; padding: 12px 16px; border-radius: 10px; border: 1px dashed #FCA5A5; color: #7F1D1D; font-weight: 600; margin-top: 6px; display: block; }
.time-text { font-weight: 700; color: #991B1B; font-size: 0.95rem; margin-left: 8px; }

/* POPUP TỪ CHỐI BÀI VIẾT */
.reject-modal-card { background: white; padding: 32px; border-radius: 24px; width: 420px; max-width: 95vw; text-align: center; box-shadow: 0 25px 50px rgba(0,0,0,0.2); }
.action-icon { width: 64px; height: 64px; border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center; font-size: 1.6rem; background: #FEF2F2; color: #DC2626; }
.reject-modal-card h3 { font-size: 1.3rem; color: #0F172A; margin: 0 0 10px; font-weight: 800; }
.reject-modal-card p { color: #64748B; font-size: 1rem; margin: 0 0 28px; line-height: 1.5; }
.reason-input-group { text-align: left; margin-bottom: 24px; }
.reason-input-group label { display: block; font-size: 0.9rem; font-weight: 700; color: #475569; margin-bottom: 8px; }
.reason-textarea { width: 100%; padding: 14px; border: 1.5px solid #CBD5E1; border-radius: 12px; outline: none; font-family: inherit; font-size: 0.95rem; resize: none; background: #F8FAFC; transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); }
.reason-textarea:focus { border-color: #EA580C; background: white; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); }
.error-msg { display: block; color: #DC2626; font-size: 0.85rem; font-weight: 600; margin-top: 6px; }
.action-btns { display: flex; gap: 16px; }
.btn-cancel, .btn-confirm { flex: 1; padding: 14px; border-radius: 12px; font-weight: 700; font-size: 1rem; cursor: pointer; border: none; transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); }
.btn-cancel { background: #F1F5F9; color: #475569; }
.btn-cancel:hover { background: #E2E8F0; color: #0F172A; }
.btn-danger { background: linear-gradient(135deg, #EF4444, #DC2626); color: white; box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3); }
.btn-danger:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(239, 68, 68, 0.4); }

/* --- TOAST & EMPTY STATES --- */
.empty-state-lux { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 80px 20px; color: #94A3B8; font-weight: 600; font-size: 1.1rem; }
.empty-icon-box { background: #F1F5F9; padding: 24px; border-radius: 50%; margin-bottom: 20px; color: #CBD5E1; }
.empty-state-lux.error { color: #EF4444; }

.toast-lux { position: fixed; bottom: 40px; right: 40px; padding: 18px 28px; border-radius: 16px; font-weight: 700; z-index: 10000; box-shadow: 0 20px 40px -10px rgba(0,0,0,0.2); display: flex; align-items: center; gap: 14px; font-size: 1.05rem; color: white; }
.toast-lux.success { background: #0F172A; border-left: 5px solid #10B981; }
.toast-lux.error { background: #7F1D1D; border-left: 5px solid #EF4444; }

/* ANIMATIONS */
.anim-fade-down { animation: fadeDown 0.6s cubic-bezier(0.2, 0.8, 0.2, 1); }
.anim-fade-up { animation: fadeUp 0.6s cubic-bezier(0.2, 0.8, 0.2, 1) both; animation-delay: var(--delay, 0s); }
@keyframes fadeDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.list-anim-enter-active, .list-anim-leave-active { transition: all 0.5s cubic-bezier(0.2, 0.8, 0.2, 1); }
.list-anim-enter-from, .list-anim-leave-to { opacity: 0; transform: translateY(20px) scale(0.98); }

.fade-scale-enter-active, .fade-scale-leave-active { transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1); }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; transform: scale(0.95); }

.toast-anim-enter-active, .toast-anim-leave-active { transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1); }
.toast-anim-enter-from, .toast-anim-leave-to { opacity: 0; transform: translateY(40px) scale(0.9); }

/* --- RESPONSIVE --- */
@media (max-width: 1024px) {
  .page-container { padding: 24px; }
  .page-header { flex-direction: column; align-items: flex-start; gap: 20px; }
  .header-actions { width: 100%; flex-wrap: wrap; justify-content: flex-start; }
  .search-box-lux { flex: 1; min-width: 250px; }
}

@media (max-width: 768px) {
  .page-container { padding: 16px; }
  .header-actions { flex-direction: column; width: 100%; align-items: stretch; }
  .search-box-lux, .sort-box-lux, .btn-open-blacklist { width: 100%; justify-content: center; }
  .tabs-filter { width: 100%; overflow-x: auto; white-space: nowrap; justify-content: flex-start; }
  .tabs-filter::-webkit-scrollbar { height: 4px; }
  .tabs-filter::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 4px; }
  
  .modal-card-lux { width: 95vw; }
  .modal-body-lux { padding: 0 20px 20px; }
  .modal-stats-box { flex-direction: column; gap: 10px; }
  .btn-grid-lux { grid-template-columns: 1fr; }
  .modal-cover-wrapper { height: 200px; }
  .modal-title { font-size: 1.6rem; }
  
  .reject-modal-card { padding: 24px 20px; }
  .action-btns { flex-direction: column; }
}

@media (max-width: 480px) {
  .title { font-size: 1.8rem; }
  .icon-box-lux { width: 48px; height: 48px; }
  .data-table-lux th, .data-table-lux td { padding: 12px 16px; }
  .thumb-wrapper { width: 50px; height: 50px; }
}
</style>