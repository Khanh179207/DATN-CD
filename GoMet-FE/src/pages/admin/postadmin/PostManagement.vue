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
    const res = await api.get(`/api/posts/${post.postID}`, { params: { accountId: authStore.user?.accountID || authStore.user?.id } })
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

<style scoped lang="scss" src="./PostManagement.scss"></style>