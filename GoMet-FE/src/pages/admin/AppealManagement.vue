<template>
  <div class="ticket-sovereign-wrapper">
    
    <div class="page-header-lux">
      <div>
        <h1 class="page-title">Quản lý Khiếu nại</h1>
        <p class="page-subtitle">Hệ thống xử lý khiếu nại và gỡ ban tài khoản cộng đồng GOMET</p>
      </div>
      <button class="btn-refresh-lux" @click="loadAppeals" :disabled="loading">
        <svg :class="{ 'spinning': loading }" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M23 4v6h-6M1 20v-6h6M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
        </svg>
        {{ loading ? 'Đang đồng bộ...' : 'Làm mới dữ liệu' }}
      </button>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="icon-wrap all">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="3" y1="9" x2="21" y2="9"></line><line x1="9" y1="21" x2="9" y2="9"></line></svg>
        </div>
        <div class="stat-info">
          <span class="label">Tổng khiếu nại</span>
          <h3 class="value">{{ appeals.length }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon-wrap bug">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg>
        </div>
        <div class="stat-info">
          <span class="label">Chờ xử lý</span>
          <h3 class="value">{{ getPendingCount }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon-wrap feedback">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
        </div>
        <div class="stat-info">
          <span class="label">Đã chấp thuận</span>
          <h3 class="value">{{ getResolvedCount }}</h3>
        </div>
      </div>
      <div class="stat-card danger-highlight">
        <div class="icon-wrap report">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>
        </div>
        <div class="stat-info">
          <span class="label">Đã Từ chối</span>
          <h3 class="value">{{ getRejectedCount }}</h3>
        </div>
      </div>
    </div>

    <div class="data-engine-lux">
      <div class="filter-bar">
        <div class="tabs-lux">
          <button v-for="tab in statusTabs" :key="tab.value" 
                  class="tab-btn" :class="{ active: filterStatus === tab.value }"
                  @click="filterStatus = tab.value">
            {{ tab.label }}
          </button>
        </div>
        
        <div class="filter-right-actions">
          <div class="search-box-lux">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
            <input v-model="searchEmail" type="email" placeholder="Tìm kiếm theo email...">
            <button v-if="searchEmail" @click="searchEmail = ''" class="clear-search" style="background: none; border: none; cursor: pointer; color: #94a3b8;">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
          </div>
        </div>
      </div>

      <div class="table-responsive">
        <div v-if="loading" class="loading-state-lux">
          <svg class="spinning" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#ea580c" stroke-width="3"><path d="M21 12a9 9 0 1 1-6.219-8.56"></path></svg>
          <span>Đang tải dữ liệu...</span>
        </div>

        <table v-else class="lux-table">
          <thead>
            <tr>
              <th width="10%">Mã Đơn</th>
              <th width="30%">Tài Khoản Yêu Cầu</th>
              <th width="25%">Lý Do (Trích lục)</th>
              <th width="15%">Thời Gian Gửi</th>
              <th width="10%">Trạng Thái</th>
              <th width="10%" class="text-right">Thao Tác</th>
            </tr>
          </thead>
          <transition-group name="list-slide" tag="tbody">
            <tr v-for="appeal in filteredAppeals" :key="appeal.appealID" class="lux-row" @click="openDetail(appeal)">
              <td class="id-col">#{{ appeal.appealID }}</td>
              <td class="user-name">{{ appeal.email }}</td>
              <td class="title-cell">{{ truncateText(appeal.reason, 45) }}</td>
              <td class="time-cell">{{ formatDateTime(appeal.createdAt) }}</td>
              <td>
                <span class="badge-status" :class="'status-' + appeal.status.toLowerCase()">
                  {{ getStatusLabel(appeal.status) }}
                </span>
              </td>
              <td class="text-right">
                <button class="btn-refresh-lux btn-sm-action" @click.stop="openDetail(appeal)">Xử lý</button>
              </td>
            </tr>
          </transition-group>
        </table>

        <div v-if="!loading && filteredAppeals.length === 0" class="empty-state-lux">
          <div class="empty-icon">📭</div>
          <h3>Không tìm thấy Khiếu nại nào!</h3>
          <p>Không có dữ liệu phù hợp với bộ lọc hiện tại của sếp.</p>
        </div>
      </div>
    </div>

    <Teleport to="body">
      
      <transition name="fade-glass">
        <div v-if="showConfirmApprove" class="modal-glass-backdrop" @click="showConfirmApprove = false">
          <div class="confirm-dialog-card" @click.stop>
            <div class="icon-success-circle">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#10b981" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
            </div>
            <h3 class="confirm-title">Phê Duyệt & Gỡ Ban?</h3>
            <p class="confirm-desc">
              Bạn đang chuẩn bị gỡ ban cho tài khoản <strong>{{ selectedAppeal?.email }}</strong>.<br><br>Hệ thống sẽ lập tức mở khóa và gửi Email thông báo tin vui này tới người dùng.
            </p>
            <div class="confirm-actions">
              <button class="btn-cancel-action" @click="showConfirmApprove = false" :disabled="isUpdating">Hủy bỏ</button>
              <button class="btn-success-action" @click="confirmApprove" :disabled="isUpdating">
                {{ isUpdating ? 'Đang xử lý...' : 'Xác nhận Gỡ Ban' }}
              </button>
            </div>
          </div>
        </div>
      </transition>

      <transition name="fade-glass">
        <div v-if="showConfirmReject" class="modal-glass-backdrop" @click="showConfirmReject = false">
          <div class="confirm-dialog-card" @click.stop>
            <div class="icon-danger-circle">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>
            </div>
            <h3 class="confirm-title">Từ chối Khiếu nại</h3>
            <p class="confirm-desc">Bạn đang từ chối đơn của <strong>{{ selectedAppeal?.email }}</strong>.</p>
            
            <div class="note-input-wrapper">
              <label>Lý do từ chối (Bắt buộc nhập) <span style="color: #ef4444">*</span></label>
              <textarea v-model="updateNote" class="note-input-lux required-lux" 
                placeholder="Ghi rõ lý do (Ví dụ: Bằng chứng chưa thuyết phục)..." rows="3"></textarea>
              <span class="note-hint">Lý do này sẽ được gửi thẳng vào Email của người dùng.</span>
            </div>

            <div class="confirm-actions">
              <button class="btn-cancel-action" @click="showConfirmReject = false" :disabled="isUpdating">Hủy bỏ</button>
              <button class="btn-danger-action" @click="confirmReject" :disabled="isUpdating">
                {{ isUpdating ? 'Đang gửi Email...' : 'Từ chối & Gửi Mail' }}
              </button>
            </div>
          </div>
        </div>
      </transition>

      <transition name="fade-glass">
        <div v-if="selectedAppeal && !showConfirmApprove && !showConfirmReject" class="modal-glass-backdrop" @click="closeDetail">
          <div class="modal-lux-content" @click.stop>
            <div class="modal-header-lux">
              <h3>Hồ Sơ Khiếu Nại #{{ selectedAppeal.appealID }}</h3>
              <button class="btn-x" @click="closeDetail">×</button>
            </div>

            <div class="modal-body lux-split-layout custom-scroll">
              <div class="split-left">
                <div class="desc-box-lux highlight">
                  <label>Nội dung khiếu nại (User trình bày)</label>
                  <div class="detail-text" style="font-style: italic; font-size: 1rem;">"{{ selectedAppeal.reason }}"</div>
                </div>

                <div class="timeline-logs">
                  <div class="timeline-title">Nhật ký xử lý</div>
                  <div class="log-item">
                    <div class="log-dot gray"></div>
                    <div class="log-content">
                      <span class="log-time">{{ formatDateTime(selectedAppeal.createdAt) }}</span>
                      <span class="log-action">Hệ thống ghi nhận đơn từ <span class="log-actor">{{ selectedAppeal.email }}</span>.</span>
                    </div>
                  </div>
                  
                  <div class="log-item" v-if="selectedAppeal.status !== 'Pending'">
                    <div class="log-dot" :class="selectedAppeal.status === 'Resolved' ? 'green' : 'red'"></div>
                    <div class="log-content">
                      <span class="log-time">{{ formatDateTime(selectedAppeal.resolvedAt) }}</span>
                      <span class="log-action">
                        Phiếu được đóng bởi <span class="log-actor">{{ selectedAppeal.adminName || 'Admin' }}</span>.
                        Kết quả: <strong :style="{ color: selectedAppeal.status === 'Resolved' ? '#16a34a' : '#dc2626'}">{{ selectedAppeal.status === 'Resolved' ? 'Đồng ý Gỡ Ban' : 'Từ chối Gỡ Ban' }}</strong>.
                      </span>
                      <div v-if="selectedAppeal.note" class="admin-note-reply mt-2">
                        <strong>Phản hồi:</strong> {{ selectedAppeal.note }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="split-right">
                <div class="meta-card-lux">
                  <div class="meta-item">
                    <label>Email Liên Hệ</label>
                    <p>📧 {{ selectedAppeal.email }}</p>
                  </div>
                  <div class="meta-item">
                    <label>Trạng Thái Hiện Tại</label>
                    <p>
                      <span class="badge-status" :class="'status-' + selectedAppeal.status.toLowerCase()" style="margin-top: 5px;">
                        {{ getStatusLabel(selectedAppeal.status) }}
                      </span>
                    </p>
                  </div>
                  <div class="meta-item">
                    <label>Thời Gian Gửi</label>
                    <p>🕒 {{ formatDateTime(selectedAppeal.createdAt) }}</p>
                  </div>
                </div>

                <div v-if="selectedAppeal.status === 'Pending'" class="violation-card-lux">
                  <div class="v-header">
                    <span>⚡ Bảng Quyết Định</span>
                  </div>
                  <div class="v-body">
                    <span class="lbl">Thao tác xử lý:</span>
                    <div class="quick-mod-bar-vertical">
                      <button class="btn-mod btn-ban-user" @click="openAction('REJECT', selectedAppeal)" :disabled="isUpdating">
                        ❌ Bác Bỏ / Từ chối
                      </button>
                      <button class="btn-mod btn-success-mod" @click="openAction('APPROVE', selectedAppeal)" :disabled="isUpdating">
                        ✅ Phê Duyệt & Gỡ Ban
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="modal-footer-lux">
              <button @click="closeDetail" class="btn-lux btn-close-modal" style="margin-left: auto;">Đóng hồ sơ</button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { toast } from '@/composables/useToast'
import { getAppeals, updateAppeal, unbanAccountByAppeal } from '@/services/appealService'

// ─────────────────────────────────────────────────────────────────
// STATE MANAGEMENT
// ─────────────────────────────────────────────────────────────────
const appeals = ref([])
const selectedAppeal = ref(null)
const loading = ref(false)
const isUpdating = ref(false)

const searchEmail = ref('')
const filterStatus = ref('')
const updateNote = ref('')

const showConfirmApprove = ref(false)
const showConfirmReject = ref(false)

const statusTabs = [
  { value: '', label: 'Tất cả' },
  { value: 'Pending', label: 'Chờ xử lý' },
  { value: 'Resolved', label: 'Đã Gỡ Ban' },
  { value: 'Rejected', label: 'Đã Từ chối' }
]

// ─────────────────────────────────────────────────────────────────
// COMPUTED & STATS
// ─────────────────────────────────────────────────────────────────
const getPendingCount = computed(() => appeals.value.filter(a => a.status === 'Pending').length)
const getResolvedCount = computed(() => appeals.value.filter(a => a.status === 'Resolved').length)
const getRejectedCount = computed(() => appeals.value.filter(a => a.status === 'Rejected').length)

const filteredAppeals = computed(() => {
  return appeals.value.filter(appeal => {
    const matchesEmail = appeal.email.toLowerCase().includes(searchEmail.value.toLowerCase())
    const matchesStatus = !filterStatus.value || appeal.status === filterStatus.value
    return matchesEmail && matchesStatus
  }).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

// ─────────────────────────────────────────────────────────────────
// ACTIONS & API
// ─────────────────────────────────────────────────────────────────
const loadAppeals = async () => {
  loading.value = true
  try {
    const data = await getAppeals()
    appeals.value = Array.isArray(data) ? data : (data.data || data.appeals || [])
  } catch (error) {
    toast.error('Lỗi khi tải dữ liệu khiếu nại!')
  } finally {
    loading.value = false
  }
}

const openAction = (type, appeal = null) => {
  if (appeal) selectedAppeal.value = appeal
  if (!selectedAppeal.value) return

  if (type === 'APPROVE') {
    showConfirmApprove.value = true
  } else if (type === 'REJECT') {
    updateNote.value = '' 
    showConfirmReject.value = true
  }
}

const confirmApprove = async () => {
  if (!selectedAppeal.value) return
  isUpdating.value = true
  try {
    await unbanAccountByAppeal(selectedAppeal.value.appealID)
    toast.success('Đã GỠ BAN thành công! Hệ thống đang gửi Email.')
    await loadAppeals()
    resetState()
  } catch (error) {
    toast.error(error.response?.data?.message || 'Lỗi xử lý!')
  } finally {
    isUpdating.value = false
  }
}

const confirmReject = async () => {
  if (!selectedAppeal.value) return
  if (!updateNote.value || updateNote.value.trim() === '') {
    toast.error('Sếp phải nhập Lý do từ chối để gửi Email cho User nhé!')
    return
  }

  isUpdating.value = true
  try {
    await updateAppeal(selectedAppeal.value.appealID, {
      status: 'Rejected',
      note: updateNote.value.trim()
    })
    toast.success('Đã từ chối đơn & Gửi Email thành công.')
    await loadAppeals()
    resetState()
  } catch (error) {
    toast.error(error.response?.data?.message || 'Lỗi xử lý!')
  } finally {
    isUpdating.value = false
  }
}

const openDetail = (appeal) => { selectedAppeal.value = { ...appeal } }
const closeDetail = () => { resetState() }
const resetState = () => {
  selectedAppeal.value = null
  showConfirmApprove.value = false
  showConfirmReject.value = false
  updateNote.value = ''
}

// ─────────────────────────────────────────────────────────────────
// UTILITIES
// ─────────────────────────────────────────────────────────────────
const truncateText = (t, l) => t?.length > l ? t.substring(0, l) + '...' : t

const getStatusLabel = status => {
  const labels = { Pending: 'Chờ xử lý', Resolved: 'Đã Gỡ Ban', Rejected: 'Từ chối' }
  return labels[status] || status
}

const formatDateTime = date => {
  if (!date) return '—'
  return new Date(date).toLocaleString('vi-VN', { hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric' })
}

onMounted(() => {
  loadAppeals()
})
</script>

<style scoped lang="scss">
// ==========================================
// 🎨 GOMET ADMIN - APPEALS (ULTRA LUXURY FULL-WIDTH)
// ==========================================

$orange: #ea580c;
$orange-hover: #c2410c;
$orange-light: #fff7ed;
$orange-gradient: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
$text-main: #0f172a;
$text-sub: #64748b;
$white: #ffffff;
$bg-light: #f8fafc;
$border-soft: rgba(0, 0, 0, 0.05);
$shadow-lux: 0 15px 40px rgba(15, 23, 42, 0.06);

.ticket-sovereign-wrapper {
  width: 100%; max-width: 100%; box-sizing: border-box; padding: 25px 35px; overflow-x: hidden; 
  font-family: 'Inter', -apple-system, sans-serif; color: $text-main; min-height: 100vh; 
  background: $bg-light;
  background-image: radial-gradient(circle at 95% 5%, rgba(234, 88, 12, 0.05), transparent 40%), radial-gradient(circle at 5% 95%, rgba(59, 130, 246, 0.02), transparent 30%);

  .page-header-lux {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 35px; 
    .page-title { font-size: 2rem; font-weight: 950; margin: 0; letter-spacing: -1px; background: linear-gradient(to right, #0f172a, #334155); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
    .page-subtitle { color: $text-sub; margin: 6px 0 0; font-weight: 600; font-size: 0.95rem; }
  }
}

.btn-refresh-lux {
  display: inline-flex; align-items: center; justify-content: center; gap: 6px; padding: 10px 24px; 
  background: $orange-gradient; border: none; border-radius: 14px; font-weight: 800; color: $white; cursor: pointer; white-space: nowrap; font-size: 0.85rem;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.25); transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  &:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(234, 88, 12, 0.45); }
  .spinning { animation: spin 1s linear infinite; }
  &:disabled { opacity: 0.6; cursor: not-allowed; }
  &.btn-sm-action { padding: 6px 14px; border-radius: 10px; font-size: 0.8rem; box-shadow: 0 3px 8px rgba(234, 88, 12, 0.2); }
}

.stats-grid {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 25px; margin-bottom: 40px; 
  .stat-card {
    background: $white; padding: 25px; border-radius: 24px; display: flex; align-items: center; gap: 15px;
    border: 1px solid $border-soft; box-shadow: $shadow-lux; transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); position: relative; overflow: hidden;
    &::after { content: ''; position: absolute; bottom: 0; left: 0; width: 100%; height: 4px; background: transparent; transition: 0.3s; }
    &:hover { transform: translateY(-4px); box-shadow: 0 10px 30px rgba(0,0,0,0.05); border-color: $orange; &::after { background: $orange; } }
    &.danger-highlight::after { background: #ef4444; }
    
    .icon-wrap { width: 55px; height: 55px; border-radius: 16px; display: flex; align-items: center; justify-content: center; font-size: 1.3rem; transition: 0.3s; flex-shrink: 0;}
    &:hover .icon-wrap { transform: scale(1.1) rotate(-5deg); }
    .icon-wrap.all { background: #f1f5f9; color: #475569; }
    .icon-wrap.bug { background: $orange-light; color: $orange; }
    .icon-wrap.report { background: #fef2f2; color: #ef4444; }
    .icon-wrap.feedback { background: #ecfdf5; color: #10b981; }
    
    .stat-info { .label { font-size: 0.8rem; font-weight: 800; color: $text-sub; text-transform: uppercase; letter-spacing: 0.5px; } .value { font-size: 2rem; font-weight: 950; margin: 4px 0 0; color: $text-main; line-height: 1; } }
  }
}

.data-engine-lux {
  background: $white; border-radius: 30px; border: 1px solid #f1f5f9; box-shadow: $shadow-lux; width: 100%; box-sizing: border-box; overflow: hidden; display: flex; flex-direction: column;
  
  .filter-bar {
    padding: 15px 35px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; background: rgba(255,255,255,0.8);
    .tabs-lux { display: flex; gap: 5px; flex-wrap: wrap;}
    .tab-btn {
      padding: 10px 20px; border: none; background: transparent; font-weight: 800; color: $text-sub; cursor: pointer; border-radius: 12px; transition: 0.3s; white-space: nowrap; font-size: 0.9rem;
      &.active { background: $orange-gradient; color: $white; box-shadow: 0 6px 15px rgba(234, 88, 12, 0.2); }
      &:hover:not(.active) { background: $orange-light; color: $orange; }
    }
    
    .filter-right-actions { display: flex; gap: 12px; align-items: center; flex-wrap: wrap;}
    .search-box-lux {
      display: flex; align-items: center; gap: 8px; background: #f8fafc; padding: 10px 15px; border-radius: 14px; border: 2px solid transparent; transition: 0.3s;
      svg { color: #94a3b8; flex-shrink: 0; width: 16px; height: 16px;}
      input { background: transparent; border: none; outline: none; font-weight: 600; font-size: 0.9rem; color: $text-main; width: 200px; transition: 0.3s; }
      &:focus-within { border-color: $orange; background: white; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); svg { color: $orange; } input { width: 250px; } }
    }
  }
  .table-responsive { width: 100%; overflow-x: auto; padding: 0 35px;}
}

.loading-state-lux { padding: 60px; text-align: center; color: $orange; font-weight: 700; display: flex; flex-direction: column; align-items: center; gap: 10px; }

.lux-table {
  width: 100%; min-width: 950px; border-collapse: separate; border-spacing: 0 8px; padding: 10px 0 25px;
  th { padding: 12px 20px; text-align: left; color: #94a3b8; font-size: 0.75rem; text-transform: uppercase; font-weight: 900; letter-spacing: 1px; white-space: nowrap; border-bottom: 2px solid #f1f5f9; }
  .text-right { text-align: right !important; }

  .lux-row {
    background: #ffffff; transition: all 0.3s ease; cursor: pointer;
    &:hover { background: #f8fafc; transform: scale(1.002); box-shadow: 0 8px 25px rgba(0,0,0,0.03); }
    td { padding: 15px 20px; border-top: 1px solid #f8fafc; border-bottom: 1px solid #f8fafc;
      &:first-child { border-radius: 16px 0 0 16px; border-left: 1px solid #f8fafc; font-weight: 900; color: $orange; font-size: 0.9rem;} 
      &:last-child { border-radius: 0 16px 16px 0; border-right: 1px solid #f8fafc; } 
    }
    .title-cell { color: $text-sub; font-size: 0.9rem; font-style: italic;}
    .user-name { font-weight: 800; font-size: 0.9rem; color: $text-main;}
    .time-cell { color: $text-main; font-size: 0.85rem; font-weight: 700; }
  }

  .badge-status {
    display: inline-flex; align-items: center; justify-content: center; padding: 6px 15px; border-radius: 10px; font-weight: 900; font-size: 0.75rem; white-space: nowrap; min-width: 100px; text-transform: uppercase; letter-spacing: 0.5px;
    &.status-pending { color: #c2410c; background: #fff7ed; border: 1.5px solid #ffedd5; }
    &.status-resolved { color: #15803d; background: #f0fdf4; border: 1.5px solid #dcfce7; }
    &.status-rejected { color: #b91c1c; background: #fef2f2; border: 1.5px solid #fee2e2; }
  }
}

.empty-state-lux {
  padding: 60px 30px; text-align: center; background: white;
  .empty-icon { font-size: 3.5rem; margin-bottom: 15px; animation: float 3s ease-in-out infinite; }
  h3 { font-size: 1.3rem; font-weight: 900; color: $text-main; margin: 0 0 8px; }
  p { color: $text-sub; font-size: 0.9rem; margin: 0 0 25px; font-weight: 500; }
}

.list-slide-enter-active, .list-slide-leave-active { transition: all 0.4s ease; }
.list-slide-enter-from { opacity: 0; transform: translateX(-20px); }
.list-slide-leave-to { opacity: 0; transform: translateX(20px); }

/* MODALS LUXURY */
.modal-glass-backdrop { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.65); backdrop-filter: blur(12px); z-index: 999999; display: flex; align-items: center; justify-content: center; padding: 20px; }
.modal-lux-content { background: $white; width: 100%; max-width: 1000px; border-radius: 24px; box-shadow: 0 30px 80px rgba(0,0,0,0.3); display: flex; flex-direction: column; max-height: 88vh; overflow: hidden; border: 1px solid rgba(255,255,255,0.2); margin: auto; }
.modal-header-lux { padding: 20px 35px; border-bottom: 1px solid #f1f5f9; background: #ffffff; display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; h3 { margin: 0; font-weight: 950; font-size: 1.4rem; color: #0f172a; letter-spacing: -0.5px; } .btn-x { background: #f1f5f9; border: none; width: 36px; height: 36px; border-radius: 50%; font-size: 1.1rem; cursor: pointer; color: $text-sub; transition: 0.3s; display: flex; align-items: center; justify-content: center; &:hover { background: #fee2e2; color: #ef4444; transform: rotate(90deg); } } }

.lux-split-layout { 
  display: flex; gap: 30px; padding: 25px 35px !important; overflow-y: auto; flex: 1;
  .split-left { flex: 0 0 62%; display: flex; flex-direction: column; gap: 20px; } 
  .split-right { flex: 0 0 calc(38% - 30px); display: flex; flex-direction: column; gap: 20px; align-self: flex-start;} 
}

.desc-box-lux { 
  background: #f8fafc; border: 1px solid #f1f5f9; border-radius: 18px; padding: 15px 20px; 
  &.highlight { border-left: 4px solid $orange; background: white; box-shadow: 0 3px 12px rgba(0,0,0,0.03); } 
  label { display: block; font-size: 0.7rem; font-weight: 900; color: #94a3b8; text-transform: uppercase; margin-bottom: 6px; letter-spacing: 0.5px; } 
  .detail-text { font-size: 0.95rem; color: #334155; line-height: 1.6; white-space: pre-wrap; font-weight: 500; } 
}

.timeline-logs { 
  padding-top: 5px; 
  .timeline-title { font-size: 0.7rem; font-weight: 900; color: #94a3b8; text-transform: uppercase; margin-bottom: 12px; } 
  .log-item { 
    display: flex; gap: 12px; margin-bottom: 12px; position: relative; 
    &::before { content: ''; position: absolute; left: 6px; top: 18px; bottom: -8px; width: 2px; background: #e2e8f0; } 
    &:last-child::before { display: none; } 
    .log-dot { width: 14px; height: 14px; border-radius: 50%; border: 3px solid white; box-shadow: 0 0 0 1px #cbd5e1; z-index: 1; flex-shrink: 0;} 
    .log-dot.gray { background: #94a3b8; } .log-dot.red { background: #ef4444; } .log-dot.green { background: #16a34a; } 
    .log-content { flex: 1; .log-action { font-size: 0.9rem; color: $text-main; .log-actor { font-weight: 800; color: $orange; } } .log-time { font-size: 0.75rem; color: #94a3b8; display: block; margin-bottom: 2px;} } 
    .admin-note-reply { background: #eff6ff; padding: 10px; border-radius: 8px; border-left: 3px solid #3b82f6; font-size: 0.85rem; color: #1e3a8a;}
  } 
}

.meta-card-lux { 
  background: white; border: 2px solid #f1f5f9; border-radius: 20px; padding: 20px; 
  .meta-item { 
    margin-bottom: 15px; &:last-child { margin-bottom: 0; } 
    label { font-size: 0.65rem; font-weight: 900; color: #94a3b8; text-transform: uppercase; display: block; margin-bottom: 4px;} 
    p { font-size: 1rem; font-weight: 800; color: $text-main; margin: 0; display: flex; align-items: center; gap: 8px; } 
  } 
}

.violation-card-lux { 
  background: linear-gradient(145deg, #f8fafc, #f1f5f9); border: 1.5px solid #e2e8f0; border-radius: 20px; padding: 20px; 
  .v-header { display: flex; align-items: center; gap: 8px; color: #334155; font-weight: 900; font-size: 0.95rem; margin-bottom: 12px; } 
  .v-body { 
    .lbl { font-size: 0.75rem; font-weight: 800; color: #64748b; display: block;} 
  } 
  .quick-mod-bar-vertical { 
    display: flex; flex-direction: column; gap: 8px; margin-top: 12px; 
    .btn-mod { width: 100%; padding: 10px 15px; border-radius: 10px; font-weight: 900; cursor: pointer; transition: 0.2s; border: 1px solid transparent; text-align: left; display: flex; align-items: center; gap: 8px; font-size: 0.85rem;} 
    .btn-ban-user { background: #fef2f2; color: #ef4444; border-color: #fca5a5; } 
    .btn-success-mod { background: #ecfdf5; color: #10b981; border-color: #86efac; }
    .btn-mod:hover { filter: brightness(0.95); transform: translateX(3px); } 
  } 
}

.modal-footer-lux { padding: 15px 35px; border-top: 1px solid #f1f5f9; background: #ffffff; display: flex; align-items: center; flex-shrink: 0;}
.btn-lux { padding: 10px 20px; border-radius: 12px; font-weight: 900; cursor: pointer; transition: 0.3s; font-size: 0.9rem; border: none; white-space: nowrap;} 
.btn-close-modal { background: #f1f5f9; color: $text-sub; &:hover { background: #e2e8f0; color: $text-main;} }

/* CONFIRM DIALOGS */
.confirm-dialog-card { background: white; width: 100%; max-width: 380px; border-radius: 26px; padding: 35px 25px; text-align: center; box-shadow: 0 25px 60px rgba(0, 0, 0, 0.2); transform: scale(0.95); animation: popIn 0.35s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards; } 
.icon-danger-circle { width: 60px; height: 60px; background: #fff1f2; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 15px; border: 5px solid #fff5f5; } 
.icon-success-circle { width: 60px; height: 60px; background: #ecfdf5; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 15px; border: 5px solid #f0fdf4; }
.confirm-title { font-size: 1.3rem; font-weight: 950; color: #1e293b; margin-bottom: 8px; } 
.confirm-desc { font-size: 0.9rem; color: #64748b; line-height: 1.5; margin-bottom: 25px; } 

.note-input-wrapper {
  text-align: left; margin-bottom: 25px;
  label { font-size: 0.8rem; font-weight: 800; color: #1e293b; display: block; margin-bottom: 8px;}
  .note-input-lux { width: 100%; padding: 12px; border-radius: 12px; border: 2px solid #e2e8f0; background: #f8fafc; font-family: inherit; font-size: 0.9rem; transition: 0.3s; resize: vertical; outline: none;}
  .note-input-lux:focus { border-color: #ef4444; background: white; box-shadow: 0 0 0 4px rgba(239,68,68,0.1);}
  .required-lux { border-left: 4px solid #ef4444; }
  .note-hint { font-size: 0.75rem; color: #94a3b8; display: block; margin-top: 6px; font-style: italic;}
}

.confirm-actions { display: flex; gap: 10px; .btn-cancel-action { flex: 1; padding: 10px; border-radius: 10px; border: none; background: #f1f5f9; font-weight: 800; cursor: pointer; color: $text-main; font-size: 0.9rem;} .btn-danger-action { flex: 1; padding: 10px; border-radius: 10px; border: none; background: #ef4444; color: white; font-weight: 800; cursor: pointer; transition: 0.2s; font-size: 0.9rem; &:hover { transform: translateY(-2px); box-shadow: 0 8px 15px rgba(239, 68, 68, 0.3); } } .btn-success-action { flex: 1; padding: 10px; border-radius: 10px; border: none; background: #10b981; color: white; font-weight: 800; cursor: pointer; transition: 0.2s; font-size: 0.9rem; &:hover { transform: translateY(-2px); box-shadow: 0 8px 15px rgba(16, 185, 129, 0.3); } } }

/* ANIMATIONS & RESPONSIVE */
@keyframes spin { to { transform: rotate(360deg); } }
@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-10px); } }
@keyframes popIn { from { opacity: 0; transform: scale(0.9); } to { opacity: 1; transform: scale(1); } }
.fade-glass-enter-active { animation: fadeIn 0.3s ease; .modal-lux-content, .confirm-dialog-card { animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1); } }
.fade-glass-leave-active { transition: opacity 0.2s ease; opacity: 0; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes slideUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 1024px) { 
  .lux-split-layout { flex-direction: column !important; padding: 20px 25px !important; gap: 15px; .split-left, .split-right { width: 100%; } } 
  .modal-lux-content { max-width: 90%; margin: 15px; max-height: 85vh;} 
}
@media (max-width: 768px) { 
  .ticket-sovereign-wrapper { padding: 15px; }
  .stats-grid { grid-template-columns: 1fr; }
  .filter-bar { padding: 12px 15px !important; flex-direction: column; align-items: stretch !important;}
  .search-box-lux input { width: 100% !important; } 
  .lux-table { min-width: 850px;} 
}
</style>