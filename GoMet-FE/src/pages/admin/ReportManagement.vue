ời bỏ <template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2 class="page-title"><AlertOctagon :size="20" style="vertical-align:middle;margin-right:6px" /> Report Management</h2>
        <p class="subtitle">Handle violation reports from users</p>
      </div>
      <div class="header-stats">
        <span class="stat-chip red">{{ reports.length }} Open Reports</span>
      </div>
    </div>

    <!-- Loading / Error -->
    <div v-if="loading" class="empty-state"><Loader2 :size="16" class="spin-icon" /> Loading reports...</div>
    <div v-else-if="error" class="empty-state error-msg"><AlertTriangle :size="16" /> {{ error }}</div>

    <div v-else>
      <div v-if="reports.length === 0" class="empty-state"><CheckCircle :size="16" /> No open reports. System is clean!</div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th width="5%">#</th>
            <th width="30%">Reported Post</th>
            <th width="20%">Reason</th>
            <th width="15%">Reporter</th>
            <th width="15%">Date</th>
            <th width="15%" class="text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in reports" :key="r.reportID">
            <td>{{ r.reportID }}</td>
            <td>
              <div class="post-cell">
                <span class="post-title">{{ r.post?.title || r.postTitle || '(Deleted post)' }}</span>
                <small v-if="r.post?.postID">ID: #{{ r.post.postID }}</small>
              </div>
            </td>
            <td><span class="reason-tag">{{ r.reason || 'Violation' }}</span></td>
            <td>
              <div class="reporter-cell">
                <span>{{ r.account?.username || r.reporterName || 'Unknown' }}</span>
              </div>
            </td>
            <td><small>{{ formatDate(r.createdAt) }}</small></td>
            <td class="text-right">
              <div class="action-group">
                <button @click="dismissReport(r.reportID)" class="btn-sm dismiss" title="Dismiss report (keep post)">
                  ✓ Dismiss
                </button>
                <button @click="deleteReportedPost(r)" class="btn-sm delete-post" title="Delete the reported post"
                  v-if="r.post?.postID || r.postID">
                  <Trash2 :size="13" /> Del Post
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Toast -->
    <transition name="toast">
      <div v-if="toast.show" :class="['toast-msg', toast.type]">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { AlertOctagon, Loader2, AlertTriangle, CheckCircle, Trash2 } from 'lucide-vue-next'
import api from '@/services/api'

const route = useRoute()
const reports = ref([])
const loading = ref(false)
const error = ref('')

const toast = ref({ show: false, msg: '', type: 'success' })
const showToast = (msg, type = 'success') => {
  toast.value = { show: true, msg, type }
  setTimeout(() => toast.value.show = false, 3500)
}

const formatDate = (d) => {
  if (!d) return '—'
  return new Date(d).toLocaleDateString('en-GB')
}

const fetchReports = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await api.get('/api/admin/reports')
    reports.value = res.data || []
  } catch (e) {
    error.value = 'Failed to load reports. ' + (e.response?.data?.message || e.message)
  } finally {
    loading.value = false
  }
}

// Dismiss = just delete the report record, keep the post
const dismissReport = async (id) => {
  if (!confirm('Dismiss this report? The post will remain.')) return
  try {
    await api.delete(`/api/admin/reports/${id}`)
    reports.value = reports.value.filter(r => r.reportID !== id)
    showToast('Report dismissed ✓')
  } catch (e) {
    showToast('Error: ' + (e.response?.data?.message || e.message), 'error')
  }
}

// Delete the reported post AND all its reports
const deleteReportedPost = async (report) => {
  const postId = report.post?.postID || report.postID
  if (!postId) return
  if (!confirm(`Delete the post "${report.post?.title || report.postTitle || 'this post'}" and all its reports? This cannot be undone.`)) return
  try {
    await api.delete(`/api/admin/reports/post/${postId}`)
    reports.value = reports.value.filter(r => (r.post?.postID || r.postID) !== postId)
    showToast('Post and all reports deleted')
  } catch (e) {
    showToast('Error: ' + (e.response?.data?.message || e.message), 'error')
  }
}

onMounted(fetchReports)
</script>

<style scoped>
.page-container { padding: 25px; font-family: 'Quicksand', sans-serif; color: #334155; }

.page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 25px; }
.page-title { font-size: 1.8rem; font-weight: 800; color: #1E293B; margin: 0; }
.subtitle { color: #64748B; margin: 5px 0 0; }
.header-stats { display: flex; gap: 10px; }
.stat-chip { padding: 8px 16px; border-radius: 20px; font-weight: 700; font-size: 0.85rem; }
.stat-chip.red { background: #FEF2F2; color: #B91C1C; border: 1px solid #FECACA; }

.empty-state { text-align: center; padding: 50px; color: #94A3B8; font-style: italic; font-size: 1.05rem; background: white; border-radius: 12px; }
.error-msg { color: #DC2626 !important; }

.data-table { width: 100%; border-collapse: collapse; background: white; border-radius: 14px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.04); border: 1px solid #F1F5F9; }
.data-table th { padding: 14px 18px; background: #F8FAFC; color: #64748B; font-weight: 700; font-size: 0.82rem; text-align: left; border-bottom: 1px solid #E2E8F0; }
.data-table td { padding: 14px 18px; border-bottom: 1px solid #F8FAFC; vertical-align: middle; font-size: 0.9rem; }
.text-right { text-align: right; }

.post-cell { display: flex; flex-direction: column; }
.post-title { font-weight: 700; color: #0F172A; }
.post-cell small { color: #94A3B8; font-size: 0.75rem; }

.reason-tag { background: #FEF2F2; color: #B91C1C; padding: 4px 10px; border-radius: 6px; font-weight: 700; font-size: 0.78rem; }

.reporter-cell { display: flex; align-items: center; gap: 8px; font-weight: 600; }

.action-group { display: flex; justify-content: flex-end; gap: 8px; flex-wrap: wrap; }
.btn-sm { padding: 6px 12px; border-radius: 8px; border: none; cursor: pointer; font-weight: 700; font-size: 0.8rem; transition: 0.2s; font-family: inherit; }
.dismiss { background: #F0FDF4; color: #15803D; border: 1px solid #86EFAC; }
.dismiss:hover { background: #15803D; color: white; }
.delete-post { background: #FEE2E2; color: #B91C1C; border: 1px solid #FECACA; }
.delete-post:hover { background: #B91C1C; color: white; }

/* Toast */
.toast-msg { position: fixed; bottom: 30px; right: 30px; padding: 14px 24px; border-radius: 12px; font-weight: 700; z-index: 9999; box-shadow: 0 10px 30px rgba(0,0,0,0.12); }
.toast-msg.success { background: #DCFCE7; color: #15803D; border: 1px solid #86EFAC; }
.toast-msg.error { background: #FEE2E2; color: #B91C1C; border: 1px solid #FCA5A5; }
.toast-enter-active, .toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateY(20px); }
</style>