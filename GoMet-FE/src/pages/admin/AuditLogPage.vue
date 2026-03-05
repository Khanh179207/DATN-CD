<template>
  <div class="page-container animate-enter">

    <div class="page-header">
      <div class="title-group">
        <h2 class="title">Audit Log</h2>
        <p class="subtitle">Nhật ký hành động của admin</p>
      </div>
      <button class="btn-refresh" @click="fetchLogs">
        <i class="fa-solid fa-rotate-right" :class="{ 'fa-spin': loading }"></i> Làm mới
      </button>
    </div>

    <!-- Filters -->
    <div class="filters glass-panel">
      <div class="filter-row">
        <div class="filter-field">
          <label>Actor ID</label>
          <input v-model="filters.actorId" type="number" placeholder="ID admin..." class="filter-input" min="1" />
        </div>
        <div class="filter-field filter-action">
          <label>Loại hành động</label>
          <select v-model="filters.action" class="filter-input">
            <option value="">Tất cả</option>
            <option v-for="act in actionTypes" :key="act" :value="act">{{ act }}</option>
          </select>
        </div>
        <div class="filter-field">
          <label>Từ ngày</label>
          <input v-model="filters.from" type="datetime-local" class="filter-input" />
        </div>
        <div class="filter-field">
          <label>Đến ngày</label>
          <input v-model="filters.to" type="datetime-local" class="filter-input" />
        </div>
        <button class="btn-search" @click="doSearch">
          <i class="fa-solid fa-search"></i> Tìm kiếm
        </button>
        <button class="btn-reset" @click="resetFilters">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-wrapper glass-panel">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div><span>Đang tải...</span>
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Admin</th>
            <th>Hành động</th>
            <th>Đối tượng</th>
            <th>IP</th>
            <th>Thời gian</th>
            <th class="text-center">Chi tiết</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="log in logs" :key="log.id">
            <tr class="log-row" @click="toggleExpand(log.id)" :class="{ 'row-expanded': expanded.has(log.id) }">
              <td class="col-id">#{{ log.id }}</td>
              <td>
                <div class="actor-cell">
                  <span class="actor-name">{{ log.actorName || log.actorId }}</span>
                  <span class="actor-id">#{{ log.actorId }}</span>
                </div>
              </td>
              <td>
                <span class="action-badge" :class="actionClass(log.actionType)">
                  {{ log.actionType }}
                </span>
              </td>
              <td>
                <div class="target-cell">
                  <span class="target-type">{{ log.targetType }}</span>
                  <span v-if="log.targetId" class="target-id">#{{ log.targetId }}</span>
                </div>
              </td>
              <td class="col-ip">{{ log.ip || '—' }}</td>
              <td class="col-date">{{ fmtDate(log.createdAt) }}</td>
              <td class="text-center">
                <button class="expand-btn" :class="{ rotated: expanded.has(log.id) }">
                  <i class="fa-solid fa-chevron-down"></i>
                </button>
              </td>
            </tr>
            <!-- Expanded meta row -->
            <Transition name="expand">
              <tr v-if="expanded.has(log.id)" class="meta-row">
                <td colspan="7">
                  <div class="meta-content">
                    <div class="meta-row-inner">
                      <div class="meta-section">
                        <span class="meta-label">User Agent</span>
                        <span class="meta-val ua">{{ log.userAgent || '—' }}</span>
                      </div>
                      <div class="meta-section" v-if="log.metaJson">
                        <span class="meta-label">Meta JSON</span>
                        <pre class="meta-json">{{ fmtJson(log.metaJson) }}</pre>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
            </Transition>
          </template>

          <tr v-if="logs.length === 0 && !loading">
            <td colspan="7" class="empty-state">
              <i class="fa-regular fa-folder-open"></i>
              <p>Không tìm thấy nhật ký nào.</p>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <button :disabled="page === 0" @click="changePage(page - 1)" class="pg-btn">
          <i class="fa-solid fa-chevron-left"></i>
        </button>
        <span class="pg-info">Trang {{ page + 1 }} / {{ totalPages }}</span>
        <button :disabled="page >= totalPages - 1" @click="changePage(page + 1)" class="pg-btn">
          <i class="fa-solid fa-chevron-right"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'
import { toast } from '@/composables/useToast'

const loading  = ref(false)
const logs     = ref([])
const page     = ref(0)
const pageSize = ref(30)
const totalItems = ref(0)
const expanded = ref(new Set())

const filters = ref({ actorId: '', action: '', from: '', to: '' })

const totalPages = computed(() => Math.max(1, Math.ceil(totalItems.value / pageSize.value)))

const actionTypes = [
  'EMAIL_JOB_CREATED', 'EMAIL_JOB_QUEUED', 'EMAIL_JOB_CANCELED',
  'USER_BANNED', 'USER_UNBANNED', 'USER_FORCE_LOGOUT', 'USER_HARD_DELETED',
  'POST_DELETED', 'POST_APPROVED', 'POST_REJECTED',
  'ROLE_ASSIGNED', 'ROLE_REVOKED',
  'REPORT_RESOLVED', 'COMMENT_DELETED',
]

async function fetchLogs () {
  loading.value = true
  try {
    const params = new URLSearchParams()
    if (filters.value.actorId) params.set('actorId', filters.value.actorId)
    if (filters.value.action)  params.set('action',  filters.value.action)
    if (filters.value.from)    params.set('from',    filters.value.from)
    if (filters.value.to)      params.set('to',      filters.value.to)
    params.set('page', page.value)
    params.set('size', pageSize.value)

    const res = await api.get(`/api/admin/audit?${params.toString()}`)
    const data = res.data
    if (Array.isArray(data)) {
      logs.value = data
      totalItems.value = data.length
    } else {
      logs.value = data.content ?? data
      totalItems.value = data.totalElements ?? logs.value.length
    }
  } catch (err) {
    toast.error('Không thể tải audit log!')
    console.error(err)
  } finally {
    loading.value = false
  }
}

function doSearch () {
  page.value = 0
  fetchLogs()
}

function resetFilters () {
  filters.value = { actorId: '', action: '', from: '', to: '' }
  page.value = 0
  fetchLogs()
}

function changePage (p) {
  page.value = p
  fetchLogs()
}

function toggleExpand (id) {
  if (expanded.value.has(id)) {
    expanded.value.delete(id)
  } else {
    expanded.value.add(id)
  }
  expanded.value = new Set(expanded.value) // trigger reactivity
}

function fmtDate (iso) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('vi-VN', { dateStyle: 'short', timeStyle: 'short' })
}

function fmtJson (jsonStr) {
  if (!jsonStr) return ''
  try { return JSON.stringify(JSON.parse(jsonStr), null, 2) } catch { return jsonStr }
}

function actionClass (action) {
  if (!action) return ''
  if (action.includes('EMAIL'))   return 'ac-email'
  if (action.includes('BAN'))     return 'ac-warn'
  if (action.includes('DELETE') || action.includes('HARD')) return 'ac-danger'
  if (action.includes('ROLE'))    return 'ac-role'
  if (action.includes('REPORT') || action.includes('POST') || action.includes('COMMENT')) return 'ac-mod'
  return 'ac-default'
}

onMounted(fetchLogs)
</script>

<style scoped>
/* Layout */
.page-container { padding: 30px 40px; font-family: 'Mulish', sans-serif; color: #1E293B; display: flex; flex-direction: column; gap: 18px; }
.page-header { display: flex; justify-content: space-between; align-items: flex-end; flex-wrap: wrap; gap: 12px; }
.title { font-size: 1.8rem; font-weight: 800; color: #0F172A; margin: 0 0 4px; letter-spacing: -0.5px; }
.subtitle { font-size: 0.88rem; color: #64748B; margin: 0; }

.glass-panel { background: rgba(255,255,255,0.85); backdrop-filter: blur(20px); border-radius: 16px; border: 1px solid rgba(255,255,255,0.9); box-shadow: 0 8px 24px rgba(0,0,0,0.04); overflow: hidden; }

/* Filters */
.filters { padding: 16px 20px; }
.filter-row { display: flex; gap: 12px; align-items: flex-end; flex-wrap: wrap; }
.filter-field { display: flex; flex-direction: column; gap: 4px; }
.filter-field.filter-action { min-width: 180px; }
.filter-field label { font-size: 0.72rem; font-weight: 800; color: #64748B; text-transform: uppercase; letter-spacing: 0.4px; }
.filter-input {
  padding: 7px 12px;
  border: 1.5px solid #E2E8F0; border-radius: 9px;
  font-size: 0.85rem; font-family: 'Mulish', sans-serif;
  color: #1E293B; background: white; outline: none;
  transition: 0.2s; min-width: 130px;
}
.filter-input:focus { border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234,88,12,0.1); }

.btn-search {
  display: flex; align-items: center; gap: 7px;
  background: linear-gradient(135deg, #F97316, #EA580C);
  color: white; border: none; border-radius: 10px;
  padding: 8px 18px; font-weight: 800; font-size: 0.875rem;
  cursor: pointer; font-family: 'Mulish', sans-serif; transition: 0.2s;
  align-self: flex-end; box-shadow: 0 3px 8px rgba(249,115,22,0.3);
}
.btn-search:hover { transform: translateY(-1px); }
.btn-reset {
  width: 36px; height: 36px; border: 1px solid #E2E8F0; border-radius: 9px;
  background: white; color: #94A3B8; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  font-size: 0.9rem; transition: 0.2s; align-self: flex-end;
}
.btn-reset:hover { background: #FEE2E2; color: #EF4444; border-color: #FEE2E2; }
.btn-refresh {
  display: flex; align-items: center; gap: 7px;
  background: white; border: 1px solid #E2E8F0; border-radius: 10px;
  padding: 8px 16px; font-weight: 700; font-size: 0.875rem;
  color: #475569; cursor: pointer; font-family: 'Mulish', sans-serif; transition: 0.2s;
}
.btn-refresh:hover { border-color: #EA580C; color: #EA580C; }

/* Table */
.table-wrapper { }
.data-table { width: 100%; border-collapse: collapse; font-size: 0.875rem; }
.data-table th { background: rgba(248,250,252,0.8); padding: 13px 18px; text-align: left; font-weight: 700; color: #64748B; font-size: 0.72rem; text-transform: uppercase; letter-spacing: 0.4px; border-bottom: 1px solid #E2E8F0; }
.data-table td { padding: 12px 18px; border-bottom: 1px solid #F1F5F9; vertical-align: middle; }
.text-center { text-align: center !important; }
.log-row { cursor: pointer; transition: background 0.15s; }
.log-row:hover { background: rgba(255,247,237,0.4); }
.row-expanded { background: rgba(255,237,213,0.3) !important; }

.col-id { font-weight: 700; color: #94A3B8; font-size: 0.8rem; }
.col-ip { font-size: 0.8rem; color: #64748B; font-family: monospace; }
.col-date { font-size: 0.8rem; color: #64748B; white-space: nowrap; }

.actor-cell { display: flex; flex-direction: column; gap: 1px; }
.actor-name { font-weight: 800; color: #0F172A; font-size: 0.9rem; }
.actor-id { font-size: 0.72rem; color: #94A3B8; }

.action-badge { padding: 3px 10px; border-radius: 20px; font-size: 0.72rem; font-weight: 800; white-space: nowrap; }
.ac-email   { background: #EFF6FF; color: #2563EB; border: 1px solid #BFDBFE; }
.ac-warn    { background: #FEF3C7; color: #B45309; border: 1px solid #FDE68A; }
.ac-danger  { background: #FEF2F2; color: #DC2626; border: 1px solid #FEE2E2; }
.ac-role    { background: #F5F3FF; color: #7C3AED; border: 1px solid #DDD6FE; }
.ac-mod     { background: #F0FDF4; color: #16A34A; border: 1px solid #DCFCE7; }
.ac-default { background: #F8FAFC; color: #475569; border: 1px solid #E2E8F0; }

.target-cell { display: flex; flex-direction: column; gap: 1px; }
.target-type { font-weight: 700; color: #374151; font-size: 0.85rem; }
.target-id { font-size: 0.72rem; color: #94A3B8; }

.expand-btn { width: 28px; height: 28px; border: 1px solid #E2E8F0; border-radius: 7px; background: white; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 0.75rem; color: #94A3B8; transition: 0.2s; }
.expand-btn.rotated { transform: rotate(180deg); background: #FFF7ED; border-color: #FED7AA; color: #EA580C; }

/* Meta expanded row */
.meta-row td { padding: 0 !important; border-bottom: 1px solid #F1F5F9; }
.meta-content { background: #FAFAFA; border-top: 1px solid #F1F5F9; }
.meta-row-inner { display: flex; gap: 0; padding: 14px 24px; flex-wrap: wrap; gap: 20px; }
.meta-section { display: flex; flex-direction: column; gap: 5px; flex: 1; min-width: 200px; }
.meta-label { font-size: 0.7rem; font-weight: 800; color: #94A3B8; text-transform: uppercase; letter-spacing: 0.4px; }
.meta-val { font-size: 0.8rem; color: #475569; }
.meta-val.ua { font-size: 0.75rem; word-break: break-all; }
.meta-json { font-size: 0.78rem; background: white; border: 1px solid #E2E8F0; border-radius: 8px; padding: 10px 12px; margin: 0; overflow-x: auto; max-height: 180px; color: #374151; font-family: 'Cascadia Code', 'Fira Code', monospace; }

/* Expand transition */
.expand-enter-active, .expand-leave-active { transition: all 0.2s ease; }
.expand-enter-from, .expand-leave-to { opacity: 0; }

/* Pagination */
.pagination { display: flex; justify-content: center; align-items: center; gap: 14px; padding: 16px; border-top: 1px solid #F1F5F9; }
.pg-btn { width: 34px; height: 34px; border: 1px solid #E2E8F0; border-radius: 9px; background: white; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 0.82rem; color: #475569; transition: 0.2s; }
.pg-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.pg-btn:hover:not(:disabled) { border-color: #EA580C; color: #EA580C; }
.pg-info { font-size: 0.82rem; font-weight: 700; color: #64748B; }

/* Loading & empty */
.loading-state { display: flex; flex-direction: column; align-items: center; gap: 14px; padding: 50px; color: #94A3B8; font-weight: 600; }
.spinner { width: 28px; height: 28px; border: 3px solid #E2E8F0; border-top-color: #EA580C; border-radius: 50%; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.empty-state { text-align: center; padding: 50px !important; color: #94A3B8; }
.empty-state i { font-size: 2.8rem; display: block; margin-bottom: 10px; opacity: 0.4; }
.empty-state p { font-weight: 600; font-size: 0.9rem; }

.animate-enter { animation: fadeIn 0.4s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
</style>
