<template>
  <div class="logs-page anim-fade-in">
    
    <div class="page-header anim-fade-down">
      <div class="header-left">
        <div class="title-icon">
          <i class="fa-solid fa-clipboard-list"></i>
        </div>
        <div>
          <h1 class="page-title">{{ $t('admin.logs.title') }}</h1>
          <p class="page-subtitle">{{ $t('admin.logs.subtitle') }}</p>
        </div>
      </div>
      <div class="header-actions">
        <button class="btn-refresh" @click="fetchLogs" :disabled="loading">
          <i class="fa-solid fa-rotate-right" :class="{'fa-spin': loading}"></i> {{ $t('admin.logs.refresh') }}
        </button>
      </div>
    </div>

    <div class="filter-bar anim-fade-up">
      <div class="search-box">
        <i class="fa-solid fa-magnifying-glass search-icon"></i>
        <input type="text" v-model="searchQuery" :placeholder="$t('admin.logs.search_placeholder')" />
        <button v-if="searchQuery" @click="searchQuery = ''" class="clear-search">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>
      
      <div class="filter-group">
        <div class="select-wrapper">
          <i class="fa-solid fa-layer-group select-icon"></i>
          <select v-model="filterType" class="filter-select">
            <option value="">{{ $t('admin.logs.all_entities') }}</option>
            <option value="ACCOUNT">{{ $t('admin.logs.account') }}</option>
            <option value="POST">{{ $t('admin.logs.post') }}</option>
            <option value="COMMENT">{{ $t('admin.logs.comment') }}</option>
          </select>
        </div>

        <div class="select-wrapper">
          <i class="fa-solid fa-bolt select-icon"></i>
          <select v-model="filterAction" class="filter-select">
            <option value="">{{ $t('admin.logs.all_actions') }}</option>
            <option value="BAN">{{ $t('admin.logs.ban') }}</option>
            <option value="UNBAN">{{ $t('admin.logs.unban') }}</option>
            <option value="APPROVE">{{ $t('admin.logs.approve') }}</option>
            <option value="REJECT">{{ $t('admin.logs.reject') }}</option>
            <option value="HIDE">{{ $t('admin.logs.hide') }}</option>
            <option value="DELETE">{{ $t('admin.logs.delete') }}</option>
            <option value="RESTORE">{{ $t('admin.logs.restore') }}</option>
          </select>
        </div>
      </div>
    </div>

    <div class="table-container anim-fade-up" style="--delay: 0.1s">
      <div v-if="loading" class="loading-state">
        <div class="spinner-modern"></div>
        <p>{{ $t('admin.logs.loading') }}</p>
      </div>
      
      <div v-else-if="filteredLogs.length === 0" class="empty-state">
        <div class="empty-icon"><i class="fa-regular fa-folder-open"></i></div>
        <p>{{ $t('admin.logs.empty') }}</p>
      </div>
      
      <table v-else class="logs-table">
        <thead>
          <tr>
            <th width="15%"><i class="fa-regular fa-clock"></i> {{ $t('admin.logs.time_col') }}</th>
            <th width="20%"><i class="fa-solid fa-user-shield"></i> {{ $t('admin.logs.admin_col') }}</th>
            <th width="15%"><i class="fa-solid fa-bolt"></i> {{ $t('admin.logs.action_col') }}</th>
            <th width="12%"><i class="fa-solid fa-cube"></i> {{ $t('admin.logs.target_col') }}</th>
            <th width="10%"><i class="fa-solid fa-fingerprint"></i> {{ $t('admin.logs.id_col') }}</th>
            <th width="28%"><i class="fa-solid fa-comment-dots"></i> {{ $t('admin.logs.reason_col') }}</th>
          </tr>
        </thead>
        <TransitionGroup tag="tbody" name="list-anim">
          <tr v-for="log in filteredLogs" :key="log.logID" class="table-row">
            <td class="col-time">{{ formatDate(log.createdAt) }}</td>
            
            <td class="col-admin">
              <div class="admin-badge">
                <i class="fa-solid fa-shield-halved"></i> 
                <span>{{ log.adminName || t('admin.logs.admin_fallback', { id: log.adminID }) }}</span>
              </div>
            </td>
            
            <td class="col-action">
              <span class="action-tag" :class="getActionClass(log.action)">
                {{ getActionLabel(log.action) }}
              </span>
            </td>
            
            <td class="col-type">
              <span class="type-tag" :class="log.targetType ? log.targetType.toLowerCase() : ''">
                {{ getTypeIcon(log.targetType) }} {{ log.targetType }}
              </span>
            </td>
            
            <td class="col-id">
              <span class="id-badge">#{{ log.targetID }}</span>
            </td>
            
            <td class="col-reason">
              <div class="reason-text" :title="log.reason">
                {{ log.reason || $t('admin.logs.no_note') }}
              </div>
            </td>
          </tr>
        </TransitionGroup>
      </table>
      
      <div v-if="!loading && filteredLogs.length > 0" class="table-footer">
        {{ $t('admin.logs.footer', { count: filteredLogs.length }) }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api' 
import { toast } from '@/composables/useToast' 
import { useI18n } from 'vue-i18n'
import { formatLocaleDateTime } from '@/i18n'

const { t } = useI18n()

const logs = ref([])
const loading = ref(false)
const searchQuery = ref('')
const filterType = ref('')
const filterAction = ref('')

const fetchLogs = async () => {
  loading.value = true
  try {
    const res = await api.get('/api/admin/moderation-logs')
    logs.value = res.data || []
  } catch (error) {
    toast.error(t('admin.logs.load_failed'))
    console.error(error)
  } finally {
    loading.value = false
  }
}

// Logic Lọc Dữ Liệu
const filteredLogs = computed(() => {
  let result = logs.value;

  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(log => 
      (log.adminName && log.adminName.toLowerCase().includes(q)) || 
      (log.reason && log.reason.toLowerCase().includes(q)) ||
      String(log.targetID).includes(q)
    )
  }

  if (filterType.value) {
    result = result.filter(log => log.targetType === filterType.value)
  }

  if (filterAction.value) {
    result = result.filter(log => log.action === filterAction.value)
  }

  return result
})

// Format Ngày Giờ
const formatDate = (dateString) => {
  if (!dateString) return t('admin.common.empty_value')
  return formatLocaleDateTime(dateString, t.locale?.value)
}

const getActionLabel = (action) => {
  if (!action) return t('admin.logs.unknown_action');
  const actionMap = {
    'BAN': t('admin.logs.ban'),
    'UNBAN': t('admin.logs.unban'),
    'DELETE': t('admin.logs.delete'),
    'RESTORE': t('admin.logs.restore'),
    'APPROVE': t('admin.logs.approve'),
    'REJECT': t('admin.logs.reject'),
    'HIDE': t('admin.logs.hide')
  };
  return actionMap[action.toUpperCase()] || action;
}

// 🔥 MÀU SẮC CHUẨN THEO MỨC ĐỘ NGHIÊM TRỌNG
const getActionClass = (action) => {
  if (!action) return 'bg-gray';
  const classMap = {
    'BAN': 'bg-red',
    'UNBAN': 'bg-green',
    'DELETE': 'bg-red',
    'RESTORE': 'bg-blue',
    'APPROVE': 'bg-green',
    'REJECT': 'bg-orange',
    'HIDE': 'bg-orange'
  };
  return classMap[action.toUpperCase()] || 'bg-gray';
}

const getTypeIcon = (type) => {
  if (!type) return '';
  const map = { 'ACCOUNT': '👤', 'POST': '📝', 'COMMENT': '💬' };
  return map[type.toUpperCase()] || '📌';
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

/* Animations */
.anim-fade-in { animation: fadeIn 0.4s ease-out; }
.anim-fade-down { animation: fadeDown 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.anim-fade-up { animation: fadeUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) both; animation-delay: var(--delay, 0s); }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes fadeDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.list-anim-enter-active, .list-anim-leave-active { transition: all 0.3s ease; }
.list-anim-enter-from, .list-anim-leave-to { opacity: 0; transform: translateX(-15px); }

/* Bố cục trang */
.logs-page {
  padding: 32px 40px;
  font-family: 'Inter', sans-serif;
  background: #f8fafc;
  min-height: 100vh;
  color: #0f172a;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title-icon {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.5rem;
  box-shadow: 0 10px 20px -5px rgba(59, 130, 246, 0.4);
}

.page-title {
  font-size: 1.75rem;
  font-weight: 800;
  margin: 0 0 4px;
  letter-spacing: -0.5px;
}

.page-subtitle {
  color: #64748b;
  font-size: 0.95rem;
  margin: 0;
  font-weight: 500;
}

/* Nút bấm */
.btn-refresh {
  background: white;
  border: 1px solid #e2e8f0;
  padding: 10px 20px;
  border-radius: 10px;
  font-weight: 600;
  color: #475569;
  cursor: pointer;
  transition: 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}
.btn-refresh:hover:not(:disabled) {
  background: #f1f5f9;
  color: #0f172a;
  border-color: #cbd5e1;
  transform: translateY(-1px);
}

/* Thanh lọc */
.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  align-items: center;
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 300px;
  max-width: 450px;
}
.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
}
.search-box input {
  width: 100%;
  padding: 12px 36px 12px 44px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  outline: none;
  font-family: inherit;
  font-size: 0.95rem;
  color: #0f172a;
  transition: 0.3s;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}
.search-box input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}
.clear-search {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  color: #94a3b8;
  cursor: pointer;
}

.filter-group {
  display: flex;
  gap: 12px;
}
.select-wrapper {
  position: relative;
}
.select-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #64748b;
  font-size: 0.9rem;
  pointer-events: none;
}
.filter-select {
  padding: 12px 16px 12px 38px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  outline: none;
  background: white;
  font-family: inherit;
  font-weight: 500;
  color: #334155;
  cursor: pointer;
  transition: 0.3s;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
  appearance: none;
  padding-right: 32px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%2364748B'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px;
}
.filter-select:focus { border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15); }

/* Bảng Dữ Liệu */
.table-container {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05);
  overflow: hidden;
}

.logs-table {
  width: 100%;
  border-collapse: collapse;
}

.logs-table th {
  background: #f8fafc;
  padding: 16px 20px;
  font-size: 0.8rem;
  font-weight: 700;
  color: #64748b;
  border-bottom: 1px solid #e2e8f0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.logs-table th i { margin-right: 6px; }

.logs-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
}

.table-row { transition: 0.2s; }
.table-row:hover { background: #f8fafc; }

/* Cột Thời Gian */
.col-time {
  color: #64748b !important;
  font-size: 0.9rem !important;
  font-weight: 500;
  white-space: nowrap;
}

/* Cột Admin */
.admin-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #eff6ff;
  color: #2563eb;
  padding: 6px 12px;
  border-radius: 100px;
  font-weight: 700;
  font-size: 0.85rem;
  border: 1px solid #bfdbfe;
}

/* Cột ID */
.id-badge {
  font-family: 'Courier New', Courier, monospace;
  background: #f1f5f9;
  padding: 4px 8px;
  border-radius: 6px;
  font-weight: 700;
  color: #475569;
  font-size: 0.9rem;
}

/* Cột Hành Động (Badge Mới) */
.action-tag {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 800;
  letter-spacing: 0.5px;
}
.bg-red { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
.bg-green { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; }
.bg-orange { background: #fff7ed; color: #ea580c; border: 1px solid #fed7aa; }
.bg-blue { background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; }
.bg-gray { background: #f1f5f9; color: #475569; border: 1px solid #e2e8f0; }

/* Cột Thực thể (Type) */
.type-tag {
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 700;
  background: white;
}
.type-tag.account { color: #8b5cf6; border: 1px solid #e9d5ff; background: #faf5ff; }
.type-tag.post { color: #0284c7; border: 1px solid #bae6fd; background: #f0f9ff; }
.type-tag.comment { color: #0d9488; border: 1px solid #a5f3fc; background: #ecfeff; }

/* Cột Lý do */
.reason-text {
  color: #475569;
  font-size: 0.95rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
  max-width: 400px;
}

/* Footer bảng */
.table-footer {
  padding: 16px 20px;
  background: #f8fafc;
  color: #64748b;
  font-size: 0.9rem;
  text-align: right;
  border-top: 1px solid #e2e8f0;
}

/* Loading & Empty State */
.loading-state, .empty-state {
  padding: 80px 20px;
  text-align: center;
  color: #64748b;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}
.empty-icon {
  font-size: 3rem;
  color: #cbd5e1;
}
.spinner-modern { 
  width: 36px; height: 36px; 
  border: 3px solid #e2e8f0; 
  border-top-color: #3b82f6; 
  border-radius: 50%; 
  animation: spin 1s linear infinite; 
}
@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 1024px) {
  .filter-bar { flex-direction: column; align-items: stretch; }
  .search-box { max-width: 100%; }
  .filter-group { display: grid; grid-template-columns: 1fr 1fr; }
}
</style>