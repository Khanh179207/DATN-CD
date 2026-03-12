<template>
  <div class="page-container">
    
    <div class="page-header">
      <div>
        <h2 class="title"><FileText :size="20" style="vertical-align:middle;margin-right:6px" /> Post Management</h2>
        <p class="subtitle">Moderate and manage all user content</p>
      </div>
      <div class="search-box">
        <Search :size="16" />
        <input v-model="searchQuery" type="text" placeholder="Search title, author..." />
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

    <!-- Loading / Error -->
    <div v-if="loading" class="empty-state"><Loader2 :size="16" class="spin-icon" /> Loading posts...</div>
    <div v-else-if="error" class="empty-state error-msg"><AlertTriangle :size="16" /> {{ error }}</div>

    <div v-else class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th width="5%">ID</th>
            <th width="35%">Post info</th>
            <th width="12%">Category</th>
            <th width="15%">Author</th>
            <th width="13%">Status</th>
            <th width="20%" class="text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="post in filteredPosts" :key="post.postID">
            <td>#{{ post.postID }}</td>
            <td>
              <div class="post-info">
                <img :src="post.image || 'https://placehold.co/60x60?text=IMG'" class="thumb" alt="Thumbnail">
                <div class="info-text">
                  <h4 @click="openDetail(post)" class="post-title">{{ post.title }}</h4>
                  <small>{{ formatDate(post.createdAt) }}</small>
                </div>
              </div>
            </td>
            <td><span class="cat-tag">{{ post.categoryID || '—' }}</span></td>
            <td>
              <div class="author-info">
                <img :src="post.accountAvatar || 'https://placehold.co/28x28?text=U'" class="avatar-xs">
                <span>{{ post.accountName || 'Unknown' }}</span>
              </div>
            </td>
            <td>
              <span :class="['status-badge', post._status]">
                {{ getStatusLabel(post._status) }}
              </span>
            </td>
            <td class="text-right">
              <div class="action-group">
                <button @click="openDetail(post)" class="btn-icon view" title="View detail"><Eye :size="14" /></button>

                <template v-if="post._status === 'pending'">
                  <button @click="approvePost(post.postID)" class="btn-icon check" title="Approve post">✓</button>
                </template>

                <template v-if="post._status === 'active'">
                  <button @click="deactivatePost(post.postID)" class="btn-icon cross" title="Deactivate">⛔</button>
                </template>

                <button @click="deletePost(post.postID)" class="btn-icon trash" title="Delete post"><Trash2 :size="14" /></button>
              </div>
            </td>
          </tr>

          <tr v-if="filteredPosts.length === 0">
            <td colspan="6" class="empty-state">
              No matching posts found.
            </td>
          </tr>
        </tbody>
      </table>

      <div class="pager" v-if="totalPages > 1">
        <button class="pager-btn" :disabled="page === 0 || loading" @click="changePage(page - 1)">Prev</button>
        <span class="pager-info">Page {{ page + 1 }} / {{ totalPages }} • {{ totalElements }} posts</span>
        <button class="pager-btn" :disabled="page >= totalPages - 1 || loading" @click="changePage(page + 1)">Next</button>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeDetail">
      <div class="modal-content">
        <button class="btn-close" @click="closeDetail">✕</button>
        <div class="modal-body">
          <img :src="selectedPost.image || 'https://placehold.co/600x250?text=No+Image'" class="modal-cover">
          <div class="modal-text">
            <div class="modal-meta">
              <span class="cat-tag">{{ selectedPost.categoryID || 'Uncategorized' }}</span>
              <span>• {{ formatDate(selectedPost.createdAt) }} • By <b>{{ selectedPost.accountName || 'Unknown' }}</b></span>
            </div>
            <h1>{{ selectedPost.title }}</h1>
            <p class="post-desc">{{ selectedPost.description || '(No description)' }}</p>

            <div class="modal-actions" v-if="selectedPost._status === 'pending'">
              <div class="alert-box"><AlertCircle :size="15" /> This post is pending review. What's your decision?</div>
              <div class="btn-row">
                <button @click="approvePost(selectedPost.postID); closeDetail()" class="btn-approve"><CheckCircle :size="15" /> Approve</button>
                <button @click="deactivatePost(selectedPost.postID)" class="btn-reject"><Ban :size="15" /> Deactivate</button>
              </div>
            </div>

            <div class="modal-footer-actions">
              <button @click="deletePost(selectedPost.postID)" class="btn-danger"><Trash2 :size="15" /> Delete Permanently</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <transition name="toast">
      <div v-if="toast.show" :class="['toast-msg', toast.type]">{{ toast.msg }}</div>
    </transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { FileText, Search, Loader2, AlertTriangle, Eye, Trash2, Ban, CheckCircle, XCircle, AlertCircle } from 'lucide-vue-next'
import api from '@/services/api'

// --- STATE ---
const posts = ref([])
const loading = ref(false)
const error = ref('')
const searchQuery = ref('')
const currentTab = ref('all')
const showModal = ref(false)
const selectedPost = ref({})
const page = ref(0)
const size = ref(20)
const totalPages = ref(0)
const totalElements = ref(0)

const tabs = [
  { key: 'all', label: 'All' },
  { key: 'pending', label: 'Pending' },
  { key: 'active', label: 'Approved' },
  { key: 'deactivated', label: 'Deactivated' }
]

// --- STATUS LOGIC ---
// isApproved=0 & isActive=1 → pending | isApproved=1 & isActive=1 → active | isActive=0 → deactivated
const getStatus = (post) => {
  if (post.isActive === 0) return 'deactivated'
  if (post.isApproved === 1) return 'active'
  return 'pending'
}

const getStatusLabel = (status) => {
  const map = { pending: 'Pending', active: 'Active', deactivated: 'Deactivated' }
  return map[status] || status
}

const formatDate = (d) => {
  if (!d) return ''
  const dt = new Date(d)
  return dt.toLocaleDateString('en-GB')
}

// --- FETCH ---
const fetchPosts = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await api.get('/api/admin/posts', { params: { page: page.value, size: size.value } })
    const content = Array.isArray(res.data?.content) ? res.data.content : []
    posts.value = content.map(p => ({
      ...p,
      accountName: p.accountName || p.username || 'Unknown',
      image: p.image || p.media || null,
      _status: getStatus(p)
    }))
    totalPages.value = Number(res.data?.totalPages || 0)
    totalElements.value = Number(res.data?.totalElements || 0)
  } catch (e) {
    error.value = 'Failed to load posts. ' + (e.response?.data?.message || e.message)
  } finally {
    loading.value = false
  }
}

onMounted(fetchPosts)

// --- COMPUTED ---
const pendingCount = computed(() => posts.value.filter(p => p._status === 'pending').length)

const filteredPosts = computed(() => {
  return posts.value.filter(post => {
    const matchTab = currentTab.value === 'all' || post._status === currentTab.value
    const q = searchQuery.value.toLowerCase()
    const matchSearch = !q ||
      (post.title || '').toLowerCase().includes(q) ||
      (post.accountName || '').toLowerCase().includes(q)
    return matchTab && matchSearch
  })
})

// --- ACTIONS ---
const approvePost = async (id) => {
  try {
    await api.put(`/api/admin/posts/approve/${id}`)
    const p = posts.value.find(p => p.postID === id)
    if (p) { p.isApproved = 1; p._status = 'active' }
    showToast('Post approved successfully')
  } catch (e) { showToast('Error: ' + (e.response?.data?.message || e.message), 'error') }
}

const deactivatePost = async (id) => {
  if (!confirm('Deactivate this post?')) return
  try {
    await api.put(`/api/admin/posts/deactive/${id}`)
    const p = posts.value.find(p => p.postID === id)
    if (p) { p.isActive = 0; p._status = 'deactivated' }
    showToast('Post deactivated ⛔')
  } catch (e) { showToast('Error: ' + (e.response?.data?.message || e.message), 'error') }
}

const deletePost = async (id) => {
  if (!confirm('Permanently delete this post? This cannot be undone.')) return
  try {
    await api.delete(`/api/admin/posts/${id}`)
    posts.value = posts.value.filter(p => p.postID !== id)
    showModal.value = false
    showToast('Post deleted')
  } catch (e) { showToast('Error: ' + (e.response?.data?.message || e.message), 'error') }
}

const openDetail = (post) => { selectedPost.value = post; showModal.value = true }
const closeDetail = () => { showModal.value = false }

const changePage = (nextPage) => {
  if (nextPage < 0 || nextPage >= totalPages.value) return
  page.value = nextPage
  fetchPosts()
}

// --- TOAST ---
const toast = ref({ show: false, msg: '', type: 'success' })
const showToast = (msg, type = 'success') => {
  toast.value = { show: true, msg, type }
  setTimeout(() => toast.value.show = false, 3000)
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
.pager { display: flex; align-items: center; justify-content: space-between; gap: 12px; padding: 14px 16px; border-top: 1px solid #E2E8F0; background: #F8FAFC; }
.pager-btn { background: #fff; border: 1px solid #CBD5E1; color: #334155; padding: 6px 12px; border-radius: 8px; cursor: pointer; font-weight: 600; }
.pager-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.pager-info { color: #475569; font-size: 0.9rem; font-weight: 600; }
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
.status-badge.pending { background: #FEF3C7; color: #D97706; } /* Yellow */
.status-badge.active { background: #DCFCE7; color: #16A34A; } /* Green */
.status-badge.rejected { background: #FEE2E2; color: #DC2626; } /* Red */

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

.modal-footer-actions { margin-top: 15px; text-align: right; }
.btn-danger { background: #FEE2E2; color: #DC2626; border: 1px solid #DC2626; padding: 8px 16px; border-radius: 8px; cursor: pointer; font-weight: 700; transition: 0.2s; }
.btn-danger:hover { background: #DC2626; color: white; }

.error-msg { color: #DC2626; }

/* Toast */
.toast-msg { position: fixed; bottom: 30px; right: 30px; padding: 14px 24px; border-radius: 12px; font-weight: 700; z-index: 9999; box-shadow: 0 10px 30px rgba(0,0,0,0.12); }
.toast-msg.success { background: #DCFCE7; color: #15803D; border: 1px solid #86EFAC; }
.toast-msg.error { background: #FEE2E2; color: #B91C1C; border: 1px solid #FCA5A5; }
.toast-enter-active, .toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateY(20px); }

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
</style>