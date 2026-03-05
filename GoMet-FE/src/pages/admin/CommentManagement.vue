<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2 class="title">Comment Management</h2>
        <p class="sub-text">{{ totalCount }} comments total</p>
      </div>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <div class="search-wrap">
        <svg class="s-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <input v-model="searchQuery" @input="onSearch" placeholder="Search comments, users..." />
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="skel-wrap">
      <div class="skel-row" v-for="n in 8" :key="n">
        <div class="skel-avatar"></div>
        <div class="skel-lines"><div class="skel-line"></div><div class="skel-line short"></div></div>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="error-banner"><AlertTriangle :size="15" /> {{ error }} <button @click="loadComments">Retry</button></div>

    <!-- Table -->
    <div v-else class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th>#</th>
            <th>User</th>
            <th>Comment</th>
            <th>Post</th>
            <th>Date</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(cmt, i) in filteredComments" :key="cmt.commentID">
            <td class="idx">{{ i + 1 }}</td>
            <td>
              <div class="user-cell">
                <img :src="cmt.authorAvatar || 'https://ui-avatars.com/api/?name=' + encodeURIComponent(cmt.authorName || 'U') + '&background=EA580C&color=fff'" class="user-avatar">
                <span class="user-name">{{ cmt.authorName || '—' }}</span>
              </div>
            </td>
            <td class="content-cell">
              <span class="comment-text">{{ cmt.content }}</span>
            </td>
            <td>
              <a class="post-link" :href="`/post/${cmt.postID}`" target="_blank">#{{ cmt.postID }}</a>
            </td>
            <td class="date-cell">{{ formatDate(cmt.createdAt) }}</td>
            <td>
              <div class="action-cell">
                <a class="btn-view" :href="`/post/${cmt.postID}#comment-${cmt.commentID}`" target="_blank" title="View comment in post">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/></svg>
                  View
                </a>
                <button class="btn-delete" @click="deleteComment(cmt.commentID)" title="Delete comment">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                  Delete
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="filteredComments.length === 0" class="empty-state">No comments found.</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { AlertTriangle } from 'lucide-vue-next'
import api from '@/services/api.js'

const comments = ref([])
const loading = ref(true)
const error = ref(null)
const searchQuery = ref('')

const totalCount = computed(() => comments.value.length)

const filteredComments = computed(() => {
  const q = searchQuery.value.toLowerCase().trim()
  if (!q) return comments.value
  return comments.value.filter(c =>
    (c.content || '').toLowerCase().includes(q) ||
    (c.authorName || '').toLowerCase().includes(q)
  )
})

const loadComments = async () => {
  loading.value = true
  error.value = null
  try {
    const res = await api.get('/api/admin/comments')
    comments.value = res.data
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to load comments'
  } finally {
    loading.value = false
  }
}

onMounted(loadComments)

let searchTimer = null
const onSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {}, 300)
}

const deleteComment = async (id) => {
  if (!confirm('Delete this comment permanently?')) return
  try {
    await api.delete(`/api/admin/comments/${id}`)
    comments.value = comments.value.filter(c => c.commentID !== id)
  } catch (e) {
    alert(e.response?.data?.message || 'Delete failed')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}
</script>

<style scoped>
.page-container { padding: 30px 40px; font-family: 'Manrope', sans-serif; background: #FFF7ED; min-height: 100vh; }
.page-header { margin-bottom: 24px; }
.title { font-size: 1.8rem; font-weight: 800; color: #0F172A; margin: 0; }
.sub-text { color: #EA580C; font-size: 0.9rem; font-weight: 600; margin: 4px 0 0; }

.toolbar { margin-bottom: 20px; }
.search-wrap { position: relative; max-width: 400px; }
.s-icon { position: absolute; left: 12px; top: 50%; transform: translateY(-50%); color: #94A3B8; }
.search-wrap input { width: 100%; padding: 11px 11px 11px 40px; border: 1.5px solid #E2E8F0; border-radius: 12px; background: white; font-size: 0.95rem; box-sizing: border-box; transition: 0.2s; }
.search-wrap input:focus { outline: none; border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234,88,12,0.1); }

/* Skeleton */
.skel-wrap { display: flex; flex-direction: column; gap: 8px; }
.skel-row { display: flex; gap: 12px; align-items: center; background: white; padding: 14px 16px; border-radius: 12px; }
.skel-avatar { width: 36px; height: 36px; border-radius: 50%; background: linear-gradient(90deg, #F1F5F9 25%, #E2E8F0 50%, #F1F5F9 75%); background-size: 200% 100%; animation: shimmer 1.4s infinite; flex-shrink: 0; }
.skel-lines { flex: 1; display: flex; flex-direction: column; gap: 8px; }
.skel-line { height: 12px; background: linear-gradient(90deg, #F1F5F9 25%, #E2E8F0 50%, #F1F5F9 75%); background-size: 200% 100%; animation: shimmer 1.4s infinite; border-radius: 6px; }
.skel-line.short { width: 40%; }
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }

.error-banner { background: #FEF2F2; border: 1px solid #FECACA; border-radius: 12px; padding: 16px; color: #DC2626; font-weight: 600; display: flex; gap: 12px; align-items: center; }
.error-banner button { background: #DC2626; color: white; border: none; padding: 6px 14px; border-radius: 8px; cursor: pointer; font-weight: 700; }

.table-wrapper { background: white; border-radius: 16px; border: 1px solid #F1F5F9; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.03); }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { padding: 14px 16px; background: #F8FAFC; color: #64748B; font-weight: 700; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; text-align: left; border-bottom: 1px solid #F1F5F9; }
.data-table td { padding: 14px 16px; border-bottom: 1px solid #F8FAFC; vertical-align: middle; }
.data-table tr:last-child td { border-bottom: none; }
.data-table tr:hover td { background: #FFFBF7; }

.idx { color: #CBD5E1; font-size: 0.85rem; font-weight: 600; }
.user-cell { display: flex; align-items: center; gap: 10px; }
.user-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; }
.user-name { font-weight: 700; color: #334155; font-size: 0.9rem; }
.content-cell { max-width: 300px; }
.comment-text { color: #475569; font-size: 0.9rem; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.post-link { color: #EA580C; font-weight: 700; text-decoration: none; font-size: 0.85rem; }
.post-link:hover { text-decoration: underline; }
.date-cell { color: #94A3B8; font-size: 0.82rem; white-space: nowrap; }
.action-cell { display: flex; gap: 8px; align-items: center; }
.btn-view { display: inline-flex; align-items: center; gap: 5px; background: #EFF6FF; color: #2563EB; border: 1px solid #BFDBFE; padding: 6px 12px; border-radius: 8px; font-size: 0.8rem; font-weight: 700; text-decoration: none; cursor: pointer; transition: 0.2s; }
.btn-view:hover { background: #2563EB; color: white; }

.btn-delete { display: flex; align-items: center; gap: 6px; background: #FEF2F2; color: #EF4444; border: 1px solid #FECACA; padding: 7px 14px; border-radius: 8px; cursor: pointer; font-size: 0.82rem; font-weight: 700; transition: 0.2s; }
.btn-delete:hover { background: #EF4444; color: white; border-color: #EF4444; }

.empty-state { text-align: center; padding: 60px; color: #94A3B8; }
</style>