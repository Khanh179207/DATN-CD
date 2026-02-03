<template>
  <div class="page-container">
    
    <div class="page-header">
      <div>
        <h2 class="title">📝 Quản Lý Bài Đăng</h2>
        <p class="subtitle">Kiểm duyệt và quản lý toàn bộ nội dung người dùng</p>
      </div>
      
      <div class="search-box">
        <span class="icon">🔍</span>
        <input v-model="searchQuery" type="text" placeholder="Tìm tiêu đề, tác giả..." />
      </div>
    </div>

    <div class="tabs-filter">
      <button 
        v-for="tab in tabs" :key="tab.key"
        :class="['tab-btn', currentTab === tab.key ? 'active' : '']"
        @click="currentTab = tab.key"
      >
        {{ tab.label }}
        <span v-if="tab.key === 'pending' && pendingCount > 0" class="badge-count">{{ pendingCount }}</span>
      </button>
    </div>

    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th width="5%">ID</th>
            <th width="35%">Thông tin bài viết</th>
            <th width="15%">Danh mục</th>
            <th width="15%">Tác giả</th>
            <th width="15%">Trạng thái</th>
            <th width="15%" class="text-right">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="post in filteredPosts" :key="post.id">
            <td>#{{ post.id }}</td>
            <td>
              <div class="post-info">
                <img :src="post.image" class="thumb" alt="Thumbnail">
                <div class="info-text">
                  <h4 @click="openDetail(post)" class="post-title">{{ post.title }}</h4>
                  <small>{{ post.date }}</small>
                </div>
              </div>
            </td>
            <td><span class="cat-tag">{{ post.category }}</span></td>
            <td>
              <div class="author-info">
                <img :src="post.authorAvatar" class="avatar-xs">
                <span>{{ post.author }}</span>
              </div>
            </td>
            <td>
              <span :class="['status-badge', post.status]">
                {{ getStatusLabel(post.status) }}
              </span>
            </td>
            <td class="text-right">
              <div class="action-group">
                <button @click="openDetail(post)" class="btn-icon view" title="Xem chi tiết">👁️</button>
                
                <template v-if="post.status === 'pending'">
                  <button @click="approvePost(post.id)" class="btn-icon check" title="Duyệt bài">✓</button>
                  <button @click="rejectPost(post.id)" class="btn-icon cross" title="Từ chối">✕</button>
                </template>

                <button @click="deletePost(post.id)" class="btn-icon trash" title="Xóa bài">🗑️</button>
              </div>
            </td>
          </tr>
          
          <tr v-if="filteredPosts.length === 0">
            <td colspan="6" class="empty-state">
              🚫 Không tìm thấy bài viết nào phù hợp.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeDetail">
      <div class="modal-content">
        <button class="btn-close" @click="closeDetail">✕</button>
        
        <div class="modal-body">
          <img :src="selectedPost.image" class="modal-cover">
          <div class="modal-text">
            <div class="modal-meta">
              <span class="cat-tag">{{ selectedPost.category }}</span>
              <span>• {{ selectedPost.date }} • Bởi <b>{{ selectedPost.author }}</b></span>
            </div>
            <h1>{{ selectedPost.title }}</h1>
            <p class="post-desc">
              (Đây là nội dung mô tả giả lập của bài viết...) <br>
              Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. 
              Món ăn này rất ngon và dễ làm, phù hợp cho dịp tết...
            </p>

            <div class="modal-actions" v-if="selectedPost.status === 'pending'">
              <div class="alert-box">⚠️ Bài viết này đang chờ duyệt. Bạn quyết định sao?</div>
              <div class="btn-row">
                <button @click="approvePost(selectedPost.id); closeDetail()" class="btn-approve">✅ Duyệt ngay</button>
                <button @click="rejectPost(selectedPost.id); closeDetail()" class="btn-reject">⛔ Từ chối</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// --- 1. DỮ LIỆU GIẢ (MOCK DATA) ---
const posts = ref([
  { id: 101, title: 'Cách làm bánh chưng ngày Tết', author: 'Nguyễn Văn A', authorAvatar: 'https://i.pravatar.cc/150?img=11', category: 'Ẩm thực', status: 'pending', date: '10:30 20/02/2026', image: 'https://source.unsplash.com/random/400x300?cake' },
  { id: 102, title: 'Top 5 địa điểm du lịch Đà Lạt', author: 'Trần Thị B', authorAvatar: 'https://i.pravatar.cc/150?img=5', category: 'Du lịch', status: 'active', date: '08:00 19/02/2026', image: 'https://source.unsplash.com/random/400x300?travel' },
  { id: 103, title: 'Lộ trình học VueJS cho người mới', author: 'Dev Dạo', authorAvatar: 'https://i.pravatar.cc/150?img=3', category: 'Công nghệ', status: 'pending', date: '14:20 18/02/2026', image: 'https://source.unsplash.com/random/400x300?code' },
  { id: 104, title: 'Review quán cà phê Chill', author: 'Lê C', authorAvatar: 'https://i.pravatar.cc/150?img=8', category: 'Đời sống', status: 'rejected', date: '09:00 15/02/2026', image: 'https://source.unsplash.com/random/400x300?coffee' },
  { id: 105, title: 'Mâm cơm gia đình Việt', author: 'Mẹ Đảm', authorAvatar: 'https://i.pravatar.cc/150?img=9', category: 'Ẩm thực', status: 'active', date: '18:30 14/02/2026', image: 'https://source.unsplash.com/random/400x300?food' },
])

// --- 2. QUẢN LÝ TRẠNG THÁI ---
const searchQuery = ref('')
const currentTab = ref('all') // all, pending, active, rejected
const showModal = ref(false)
const selectedPost = ref({})

const tabs = [
  { key: 'all', label: 'Tất cả' },
  { key: 'pending', label: 'Chờ duyệt' },
  { key: 'active', label: 'Đã duyệt' },
  { key: 'rejected', label: 'Đã từ chối' }
]

// --- 3. COMPUTED & LOGIC ---

// Đếm số bài chờ duyệt
const pendingCount = computed(() => posts.value.filter(p => p.status === 'pending').length)

// Lọc bài viết theo Tab & Tìm kiếm
const filteredPosts = computed(() => {
  return posts.value.filter(post => {
    // 1. Lọc theo Tab
    const matchTab = currentTab.value === 'all' || post.status === currentTab.value
    // 2. Lọc theo Search
    const matchSearch = post.title.toLowerCase().includes(searchQuery.value.toLowerCase()) || 
                        post.author.toLowerCase().includes(searchQuery.value.toLowerCase())
    return matchTab && matchSearch
  })
})

const getStatusLabel = (status) => {
  const map = { pending: 'Chờ duyệt', active: 'Hoạt động', rejected: 'Từ chối' }
  return map[status] || status
}

// --- 4. HÀNH ĐỘNG (ACTIONS) ---
const approvePost = (id) => {
  const post = posts.value.find(p => p.id === id)
  if (post) post.status = 'active'
  // alert(`Đã duyệt bài viết #${id}`)
}

const rejectPost = (id) => {
  const post = posts.value.find(p => p.id === id)
  if (post) post.status = 'rejected'
}

const deletePost = (id) => {
  if(confirm('Bạn có chắc muốn xóa bài này vĩnh viễn?')) {
    posts.value = posts.value.filter(p => p.id !== id)
  }
}

const openDetail = (post) => {
  selectedPost.value = post
  showModal.value = true
}

const closeDetail = () => {
  showModal.value = false
}
</script>

<style scoped>
.page-container { padding: 25px; font-family: 'Quicksand', sans-serif; color: #334155; }

/* HEADER */
.page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 25px; }
.title { font-size: 1.8rem; font-weight: 800; color: #1E293B; margin: 0; }
.subtitle { color: #64748B; margin: 5px 0 0; }
.search-box { display: flex; align-items: center; background: white; padding: 10px 15px; border-radius: 30px; border: 1px solid #E2E8F0; width: 300px; }
.search-box input { border: none; outline: none; margin-left: 10px; width: 100%; font-family: inherit; }

/* TABS FILTER */
.tabs-filter { display: flex; gap: 10px; margin-bottom: 20px; border-bottom: 1px solid #E2E8F0; padding-bottom: 10px; }
.tab-btn { background: none; border: none; padding: 8px 16px; font-weight: 600; color: #64748B; cursor: pointer; border-radius: 8px; transition: 0.2s; position: relative; }
.tab-btn:hover { background: #F1F5F9; color: #334155; }
.tab-btn.active { background: #E0F2FE; color: #0284C7; }
.badge-count { position: absolute; top: -5px; right: -5px; background: #EF4444; color: white; font-size: 0.65rem; padding: 2px 6px; border-radius: 10px; }

/* TABLE */
.table-wrapper { background: white; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); overflow: hidden; border: 1px solid #F1F5F9; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { text-align: left; padding: 15px 20px; background: #F8FAFC; color: #64748B; font-weight: 600; font-size: 0.85rem; border-bottom: 1px solid #E2E8F0; }
.data-table td { padding: 15px 20px; border-bottom: 1px solid #F1F5F9; vertical-align: middle; }
.text-right { text-align: right; }

/* Post Info in Table */
.post-info { display: flex; gap: 15px; align-items: center; }
.thumb { width: 60px; height: 60px; border-radius: 8px; object-fit: cover; border: 1px solid #E2E8F0; }
.post-title { margin: 0; font-size: 0.95rem; font-weight: 700; color: #1E293B; cursor: pointer; transition: 0.2s; }
.post-title:hover { color: #3B82F6; text-decoration: underline; }
.info-text small { color: #94A3B8; }
.cat-tag { background: #F1F5F9; padding: 4px 10px; border-radius: 6px; font-size: 0.8rem; font-weight: 600; color: #475569; }

.author-info { display: flex; align-items: center; gap: 8px; font-weight: 600; font-size: 0.9rem; }
.avatar-xs { width: 28px; height: 28px; border-radius: 50%; }

/* Status Badges */
.status-badge { padding: 5px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; display: inline-block; }
.status-badge.pending { background: #FEF3C7; color: #D97706; } /* Vàng */
.status-badge.active { background: #DCFCE7; color: #16A34A; } /* Xanh */
.status-badge.rejected { background: #FEE2E2; color: #DC2626; } /* Đỏ */

/* Action Buttons */
.action-group { display: flex; justify-content: flex-end; gap: 8px; }
.btn-icon { width: 32px; height: 32px; border-radius: 6px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; font-size: 1rem; }
.view { background: #EFF6FF; color: #3B82F6; }
.check { background: #DCFCE7; color: #16A34A; }
.cross { background: #FFE4E6; color: #F43F5E; }
.trash { background: #F1F5F9; color: #64748B; }
.btn-icon:hover { transform: translateY(-2px); filter: brightness(0.95); }

.empty-state { text-align: center; padding: 40px; color: #94A3B8; font-style: italic; }

/* MODAL (POPUP) */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; backdrop-filter: blur(2px); }
.modal-content { background: white; width: 600px; max-width: 90%; max-height: 90vh; border-radius: 16px; overflow-y: auto; position: relative; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); animation: slideUp 0.3s ease; }
.btn-close { position: absolute; top: 15px; right: 15px; background: rgba(0,0,0,0.1); border: none; width: 30px; height: 30px; border-radius: 50%; cursor: pointer; font-weight: bold; z-index: 10; }

.modal-cover { width: 100%; height: 250px; object-fit: cover; }
.modal-body { padding: 25px; }
.modal-meta { font-size: 0.85rem; color: #64748B; margin-bottom: 10px; display: flex; align-items: center; gap: 10px; }
.modal-text h1 { margin: 0 0 15px 0; font-size: 1.5rem; color: #1E293B; }
.post-desc { line-height: 1.6; color: #475569; font-size: 1rem; margin-bottom: 25px; }

.modal-actions { background: #FFF7ED; padding: 20px; border-radius: 12px; border: 1px solid #FFEDD5; }
.alert-box { color: #C2410C; font-weight: 600; margin-bottom: 15px; font-size: 0.9rem; }
.btn-row { display: flex; gap: 15px; }
.btn-approve, .btn-reject { flex: 1; padding: 12px; border: none; border-radius: 8px; font-weight: 700; cursor: pointer; font-size: 1rem; transition: 0.2s; }
.btn-approve { background: #16A34A; color: white; }
.btn-approve:hover { background: #15803D; }
.btn-reject { background: white; border: 1px solid #DC2626; color: #DC2626; }
.btn-reject:hover { background: #DC2626; color: white; }

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
</style>