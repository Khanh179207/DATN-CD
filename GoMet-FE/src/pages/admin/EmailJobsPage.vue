<template>
  <div class="page-container animate-enter">

    <div class="page-header">
      <div class="title-group">
        <h2 class="title">Email Jobs</h2>
        <p class="subtitle">Quản lý hàng đợi email admin</p>
      </div>
      <div class="header-actions">
        <!-- Status filter -->
        <div class="filter-tabs">
          <button
            v-for="tab in statusTabs"
            :key="tab.value"
            class="tab-btn"
            :class="{ 'tab-active': currentStatus === tab.value }"
            @click="setStatus(tab.value)"
          >
            {{ tab.label }}
          </button>
        </div>
        <label class="toggle-all">
          <input type="checkbox" v-model="showAll" @change="fetchJobs" />
          Tất cả admin
        </label>
        <button class="btn-refresh" @click="fetchJobs">
          <i class="fa-solid fa-rotate-right" :class="{ 'fa-spin': loading }"></i>
        </button>
      </div>
    </div>

    <!-- Main grid -->
    <div class="em-grid">

      <!-- Job list -->
      <div class="job-list glass-panel">
        <div v-if="loading && jobs.length === 0" class="loading-state">
          <div class="spinner"></div><span>Đang tải...</span>
        </div>

        <div v-else-if="jobs.length === 0" class="empty-state">
          <i class="fa-regular fa-envelope"></i>
          <p>Không có email job nào.</p>
        </div>

        <div
          v-for="job in jobs"
          :key="job.id"
          class="job-item"
          :class="{ 'job-selected': selectedJob?.id === job.id }"
          @click="selectJob(job)"
        >
          <div class="ji-top">
            <span class="status-badge" :class="`status-${job.status?.toLowerCase()}`">
              <i :class="statusIcon(job.status)"></i> {{ job.status }}
            </span>
            <span class="ji-date">{{ fmtDate(job.createdAt) }}</span>
          </div>
          <div class="ji-subject">{{ job.subject }}</div>
          <div class="ji-meta">
            <span><i class="fa-solid fa-users"></i> {{ job.totalRecipients ?? 0 }} người nhận</span>
            <span v-if="job.sentCount > 0" class="meta-sent"><i class="fa-solid fa-check"></i> {{ job.sentCount }}</span>
            <span v-if="job.failedCount > 0" class="meta-fail"><i class="fa-solid fa-xmark"></i> {{ job.failedCount }}</span>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination">
          <button :disabled="page === 0" @click="changePage(page - 1)" class="pg-btn">
            <i class="fa-solid fa-chevron-left"></i>
          </button>
          <span class="pg-info">{{ page + 1 }} / {{ totalPages }}</span>
          <button :disabled="page >= totalPages - 1" @click="changePage(page + 1)" class="pg-btn">
            <i class="fa-solid fa-chevron-right"></i>
          </button>
        </div>
      </div>

      <!-- Detail panel -->
      <div class="detail-panel glass-panel">
        <div v-if="!selectedJob" class="detail-empty">
          <i class="fa-regular fa-envelope-open"></i>
          <p>Chọn một job để xem chi tiết</p>
        </div>

        <template v-else>
          <div class="dp-header">
            <div class="dp-status-row">
              <span class="status-badge lg" :class="`status-${selectedJob.status?.toLowerCase()}`">
                <i :class="statusIcon(selectedJob.status)"></i> {{ selectedJob.status }}
              </span>
              <button
                v-if="selectedJob.status === 'QUEUED' || selectedJob.status === 'DRAFT'"
                class="btn-cancel-job"
                @click="doCancelJob(selectedJob.id)"
              >
                <i class="fa-solid fa-ban"></i> Hủy
              </button>
            </div>
            <h3 class="dp-subject">{{ selectedJob.subject }}</h3>
            <div class="dp-meta-row">
              <span><i class="fa-solid fa-calendar"></i> {{ fmtDate(selectedJob.createdAt) }}</span>
              <span><i class="fa-solid fa-users"></i> {{ selectedJob.totalRecipients ?? 0 }} người nhận</span>
              <span class="meta-sent" v-if="selectedJob.sentCount"><i class="fa-solid fa-check-circle"></i> {{ selectedJob.sentCount }} OK</span>
              <span class="meta-fail" v-if="selectedJob.failedCount"><i class="fa-solid fa-times-circle"></i> {{ selectedJob.failedCount }} Lỗi</span>
            </div>
          </div>

          <!-- Body preview -->
          <div class="dp-section">
            <div class="section-title"><i class="fa-solid fa-file-lines"></i> Nội dung</div>
            <div class="body-preview" v-html="sanitizeBody(selectedJob.bodyHtml)"></div>
          </div>

          <!-- Recipients -->
          <div class="dp-section">
            <div class="section-title"><i class="fa-solid fa-list"></i> Danh sách người nhận ({{ detailRecipients.length }})</div>
            <div v-if="detailLoading" class="loading-state sm">
              <div class="spinner sm-spinner"></div>
            </div>
            <div v-else class="recipient-table-wrap">
              <table class="recipient-table">
                <thead>
                  <tr>
                    <th>Email</th>
                    <th>Trạng thái</th>
                    <th>Lỗi</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="r in detailRecipients" :key="r.id">
                    <td class="re-email">{{ r.email }}</td>
                    <td>
                      <span class="rec-status-badge" :class="`rec-${r.status?.toLowerCase()}`">
                        {{ r.status }}
                      </span>
                    </td>
                    <td class="re-error">{{ r.errorMessage || '—' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </template>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { listJobs, getJobDetail, cancelJob } from '@/services/adminEmailService'
import { toast } from '@/composables/useToast'

const loading      = ref(false)
const detailLoading = ref(false)
const jobs         = ref([])
const selectedJob  = ref(null)
const detailRecipients = ref([])
const page         = ref(0)
const pageSize     = ref(20)
const totalItems   = ref(0)
const currentStatus = ref('')
const showAll      = ref(false)

const totalPages = computed(() => Math.max(1, Math.ceil(totalItems.value / pageSize.value)))

const statusTabs = [
  { label: 'Tất cả',    value: '' },
  { label: 'Nháp',      value: 'DRAFT' },
  { label: 'Chờ gửi',   value: 'QUEUED' },
  { label: 'Đang gửi',  value: 'SENDING' },
  { label: 'Đã gửi',    value: 'SENT' },
  { label: 'Lỗi',       value: 'FAILED' },
  { label: 'Đã hủy',    value: 'CANCELED' },
]

async function fetchJobs () {
  loading.value = true
  try {
    const res = await listJobs({
      status: currentStatus.value || undefined,
      page: page.value,
      size: pageSize.value,
      all: showAll.value
    })
    const data = res.data
    // Support both paginated ({content, totalElements}) and plain array
    if (Array.isArray(data)) {
      jobs.value = data
      totalItems.value = data.length
    } else {
      jobs.value = data.content ?? data
      totalItems.value = data.totalElements ?? jobs.value.length
    }
  } catch (err) {
    toast.error('Không thể tải danh sách email jobs!')
    console.error(err)
  } finally {
    loading.value = false
  }
}

async function selectJob (job) {
  selectedJob.value = job
  detailRecipients.value = []
  detailLoading.value = true
  try {
    const res = await getJobDetail(job.id)
    const d = res.data
    selectedJob.value = d
    detailRecipients.value = d.recipients ?? []
  } catch (err) {
    console.error(err)
  } finally {
    detailLoading.value = false
  }
}

async function doCancelJob (id) {
  if (!confirm('Hủy email job này?')) return
  try {
    await cancelJob(id)
    toast.success('Đã hủy email job.')
    selectedJob.value = null
    fetchJobs()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Không thể hủy job!')
  }
}

function setStatus (val) {
  currentStatus.value = val
  page.value = 0
  fetchJobs()
}

function changePage (p) {
  page.value = p
  fetchJobs()
}

function statusIcon (status) {
  const map = {
    DRAFT: 'fa-regular fa-file',
    QUEUED: 'fa-solid fa-clock',
    SENDING: 'fa-solid fa-spinner fa-spin',
    SENT: 'fa-solid fa-check-circle',
    FAILED: 'fa-solid fa-circle-exclamation',
    CANCELED: 'fa-solid fa-ban',
  }
  return map[status] || 'fa-solid fa-envelope'
}

function fmtDate (iso) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('vi-VN', { dateStyle: 'short', timeStyle: 'short' })
}

function sanitizeBody (html) {
  if (!html) return ''
  return html.replace(/<script[\s\S]*?<\/script>/gi, '').replace(/on\w+="[^"]*"/gi, '')
}

onMounted(fetchJobs)
</script>

<style scoped>
.page-container { padding: 30px 40px; font-family: 'Mulish', sans-serif; color: #1E293B; }
.page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 22px; flex-wrap: wrap; gap: 14px; }
.title { font-size: 1.8rem; font-weight: 800; color: #0F172A; margin: 0 0 4px; letter-spacing: -0.5px; }
.subtitle { font-size: 0.88rem; color: #64748B; margin: 0; }
.header-actions { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }

/* Filter tabs */
.filter-tabs { display: flex; gap: 4px; background: #F1F5F9; border-radius: 10px; padding: 4px; }
.tab-btn {
  padding: 5px 12px; border: none; background: none;
  border-radius: 7px; font-size: 0.78rem; font-weight: 700;
  color: #64748B; cursor: pointer; font-family: 'Mulish', sans-serif;
  transition: 0.2s; white-space: nowrap;
}
.tab-btn:hover { background: white; color: #374151; }
.tab-active { background: white !important; color: #EA580C !important; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }

.toggle-all { display: flex; align-items: center; gap: 6px; font-size: 0.82rem; font-weight: 700; color: #64748B; cursor: pointer; }
.toggle-all input { accent-color: #EA580C; }

.btn-refresh { width: 36px; height: 36px; border: 1px solid #E2E8F0; border-radius: 10px; background: white; display: flex; align-items: center; justify-content: center; color: #64748B; cursor: pointer; font-size: 0.9rem; transition: 0.2s; }
.btn-refresh:hover { border-color: #EA580C; color: #EA580C; }

/* Main grid */
.em-grid { display: grid; grid-template-columns: 360px 1fr; gap: 20px; align-items: start; }
@media (max-width: 900px) { .em-grid { grid-template-columns: 1fr; } }

.glass-panel { background: rgba(255,255,255,0.85); backdrop-filter: blur(20px); border-radius: 16px; border: 1px solid rgba(255,255,255,0.9); box-shadow: 0 8px 24px rgba(0,0,0,0.04); overflow: hidden; }

/* Job list */
.job-list { display: flex; flex-direction: column; max-height: 76vh; overflow-y: auto; }
.job-item { padding: 14px 18px; border-bottom: 1px solid #F1F5F9; cursor: pointer; transition: background 0.15s; }
.job-item:hover { background: #FFF7ED; }
.job-selected { background: rgba(255,237,213,0.5) !important; border-left: 3px solid #EA580C; }
.ji-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.ji-date { font-size: 0.72rem; color: #94A3B8; font-weight: 600; }
.ji-subject { font-weight: 800; color: #0F172A; font-size: 0.92rem; margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.ji-meta { display: flex; gap: 12px; font-size: 0.75rem; font-weight: 600; color: #64748B; }
.meta-sent { color: #16A34A; }
.meta-fail { color: #DC2626; }

/* Status badges */
.status-badge { display: inline-flex; align-items: center; gap: 5px; padding: 3px 10px; border-radius: 20px; font-size: 0.72rem; font-weight: 800; }
.status-badge.lg { font-size: 0.82rem; padding: 5px 14px; }
.status-draft    { background: #F8FAFC; color: #475569; border: 1px solid #E2E8F0; }
.status-queued   { background: #EFF6FF; color: #2563EB; border: 1px solid #BFDBFE; }
.status-sending  { background: #FFF7ED; color: #C2410C; border: 1px solid #FED7AA; }
.status-sent     { background: #F0FDF4; color: #16A34A; border: 1px solid #DCFCE7; }
.status-failed   { background: #FEF2F2; color: #DC2626; border: 1px solid #FEE2E2; }
.status-canceled { background: #F8FAFC; color: #94A3B8; border: 1px solid #E2E8F0; }

/* Pagination */
.pagination { display: flex; justify-content: center; align-items: center; gap: 12px; padding: 14px; border-top: 1px solid #F1F5F9; }
.pg-btn { width: 32px; height: 32px; border: 1px solid #E2E8F0; border-radius: 8px; background: white; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 0.8rem; color: #475569; transition: 0.2s; }
.pg-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.pg-btn:hover:not(:disabled) { border-color: #EA580C; color: #EA580C; }
.pg-info { font-size: 0.8rem; font-weight: 700; color: #64748B; }

/* Detail panel */
.detail-panel { padding: 0; display: flex; flex-direction: column; max-height: 76vh; overflow-y: auto; }
.detail-empty { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 12px; color: #94A3B8; padding: 60px; }
.detail-empty i { font-size: 3rem; opacity: 0.4; }
.detail-empty p { font-weight: 600; font-size: 0.9rem; }

.dp-header { padding: 22px 24px; border-bottom: 1px solid #F1F5F9; background: linear-gradient(135deg, #FFF7ED, #FFFBF5); }
.dp-status-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.btn-cancel-job { display: flex; align-items: center; gap: 6px; padding: 6px 14px; background: #FEF2F2; border: 1px solid #FEE2E2; border-radius: 8px; color: #DC2626; font-weight: 700; font-size: 0.8rem; cursor: pointer; font-family: 'Mulish', sans-serif; transition: 0.2s; }
.btn-cancel-job:hover { background: #EF4444; color: white; border-color: #EF4444; }
.dp-subject { font-size: 1.2rem; font-weight: 800; color: #0F172A; margin: 0 0 10px; }
.dp-meta-row { display: flex; gap: 16px; flex-wrap: wrap; font-size: 0.8rem; font-weight: 600; color: #64748B; }
.dp-meta-row i { margin-right: 4px; }

.dp-section { padding: 18px 24px; border-bottom: 1px solid #F1F5F9; }
.section-title { font-size: 0.75rem; font-weight: 800; color: #94A3B8; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 12px; display: flex; align-items: center; gap: 6px; }
.body-preview { font-size: 0.88rem; line-height: 1.7; color: #374151; background: #FAFAFA; border: 1px solid #F1F5F9; border-radius: 8px; padding: 14px 16px; max-height: 200px; overflow-y: auto; word-break: break-word; }

/* Recipient table */
.recipient-table-wrap { overflow-x: auto; }
.recipient-table { width: 100%; border-collapse: collapse; font-size: 0.82rem; }
.recipient-table th { background: #F8FAFC; padding: 8px 12px; text-align: left; font-weight: 700; color: #64748B; font-size: 0.72rem; text-transform: uppercase; letter-spacing: 0.3px; border-bottom: 1px solid #E2E8F0; }
.recipient-table td { padding: 8px 12px; border-bottom: 1px solid #F8FAFC; vertical-align: middle; }
.re-email { font-weight: 700; color: #1E293B; }
.re-error { color: #DC2626; font-size: 0.78rem; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.rec-status-badge { padding: 2px 8px; border-radius: 6px; font-size: 0.7rem; font-weight: 800; }
.rec-queued  { background: #EFF6FF; color: #2563EB; }
.rec-sent    { background: #F0FDF4; color: #16A34A; }
.rec-failed  { background: #FEF2F2; color: #DC2626; }
.rec-skipped { background: #F8FAFC; color: #94A3B8; }

/* Loading states */
.loading-state { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 40px; color: #94A3B8; font-weight: 600; font-size: 0.9rem; }
.loading-state.sm { padding: 16px; }
.spinner { width: 28px; height: 28px; border: 3px solid #E2E8F0; border-top-color: #EA580C; border-radius: 50%; animation: spin 1s linear infinite; }
.sm-spinner { width: 22px; height: 22px; border-width: 2px; }
@keyframes spin { to { transform: rotate(360deg); } }
.empty-state { display: flex; flex-direction: column; align-items: center; gap: 10px; padding: 50px; color: #94A3B8; }
.empty-state i { font-size: 2.5rem; opacity: 0.4; }
.empty-state p { font-weight: 600; font-size: 0.9rem; }

/* Animate */
.animate-enter { animation: fadeIn 0.4s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
</style>
