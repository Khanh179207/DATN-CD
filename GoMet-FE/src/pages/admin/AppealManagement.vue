<template>
  <div class="page-container animate-enter">

    <div class="page-header">
      <div class="title-group">
        <h2 class="title">Khiếu nại ban nhầm</h2>
        <p class="subtitle">Quản lý và xử lý các khiếu nại từ người dùng bị ban</p>
      </div>
      <div class="header-actions">
        <button class="btn-refresh" @click="loadAppeals" :disabled="loading">
          <i class="fa-solid fa-rotate-right" :class="{ 'fa-spin': loading }"></i> Đồng bộ dữ liệu
        </button>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon-wrapper bg-orange-light">
          <i class="fa-solid fa-inbox text-orange"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ appeals.length }}</span>
          <span class="stat-label">Tổng khiếu nại</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon-wrapper bg-yellow-light">
          <i class="fa-solid fa-hourglass-start text-yellow"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ getPendingCount }}</span>
          <span class="stat-label">Chờ xử lý</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon-wrapper bg-green-light">
          <i class="fa-solid fa-check-circle text-green"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ getResolvedCount }}</span>
          <span class="stat-label">Đã giải quyết</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon-wrapper bg-red-light">
          <i class="fa-solid fa-xmark-circle text-red"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ getRejectedCount }}</span>
          <span class="stat-label">Đã Từ chối</span>
        </div>
      </div>
    </div>

    <div class="filter-bar">
      <div class="tabs">
        <button v-for="tab in statusTabs" :key="tab.value"
          :class="['filter-tab', filterStatus === tab.value ? 'active' : '']" @click="filterStatus = tab.value">
          {{ tab.label }}
        </button>
      </div>

      <div class="search-box">
        <i class="fa-solid fa-search search-icon"></i>
        <input v-model="searchEmail" type="email" placeholder="Tìm kiếm theo email..." class="search-input" />
        <button v-if="searchEmail" @click="searchEmail = ''" class="clear-search">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>
    </div>

    <!-- DATA TABLE -->
    <div class="table-wrapper">
      <div v-if="loading" class="loading-state">
        <div class="spinner-modern"></div>
        <span>Đang tải dữ liệu...</span>
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th width="12%">ID</th>
            <th width="28%">EMAIL</th>
            <th width="20%">TRẠNG THÁI</th>
            <th width="18%">NGÀY GỬI</th>
            <th width="22%" class="text-center">THAO TÁC</th>
          </tr>
        </thead>
        <TransitionGroup tag="tbody" name="list">
          <tr v-for="appeal in filteredAppeals" :key="appeal.appealID" class="table-row">
            <td class="col-id">#{{ appeal.appealID }}</td>
            <td>
              <div class="email-cell">
                <div class="email-icon"><i class="fa-solid fa-envelope"></i></div>
                <div class="email-info">
                  <span class="email-main">{{ appeal.email }}</span>
                  <span class="email-reason">{{ appeal.reason.substring(0, 50) }}...</span>
                </div>
              </div>
            </td>
            <td>
              <div class="status-pill" :class="['status-' + appeal.status.toLowerCase()]">
                <span class="status-dot"></span> {{ getStatusLabel(appeal.status) }}
              </div>
            </td>
            <td>
              <span class="date-text">{{ formatDate(appeal.createdAt) }}</span>
            </td>
            <td>
              <div class="actions">
                <button @click="openDetail(appeal)" class="btn-action view" title="Xem chi tiết">
                  <i class="fa-solid fa-eye"></i>
                </button>

                <button v-if="appeal.status === 'Pending'" @click="openAction('APPROVE', appeal)"
                  class="btn-action approve" title="Phê duyệt">
                  <i class="fa-solid fa-check"></i>
                </button>

                <button v-if="appeal.status === 'Pending'" @click="openAction('REJECT', appeal)"
                  class="btn-action reject" title="Từ chối đơn">
                  <i class="fa-solid fa-ban"></i>
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="filteredAppeals.length === 0">
            <td colspan="5" class="empty-state">
              <div class="empty-icon"><i class="fa-solid fa-inbox"></i></div>
              <p>Không có khiếu nại nào.</p>
            </td>
          </tr>
        </TransitionGroup>
      </table>
    </div>

    <!-- CONFIRM APPROVE MODAL -->
    <transition name="modal-fade">
      <div v-if="showConfirmApprove" class="modal-overlay" @click="showConfirmApprove = false">
        <div class="modal-confirm" @click.stop>
          <div class="confirm-header">
            <div class="confirm-icon">✅</div>
            <h3>Xác nhận phê duyệt</h3>
          </div>

          <p class="confirm-message">
            Bạn chắc chắn muốn phê duyệt đơn của <br>
            <strong>{{ selectedAppeal?.email }}</strong>? <br>
            Tài khoản sẽ được mở lại.
          </p>

          <div class="confirm-actions">
            <button class="btn-cancel-confirm" @click="showConfirmApprove = false">
              Hủy
            </button>

            <button class="btn-confirm-approve" @click="confirmApprove" :disabled="isUpdating">
              {{ isUpdating ? 'Đang xử lý...' : 'Xác nhận phê duyệt' }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- CONFIRM REJECT MODAL -->
    <transition name="modal-fade">
      <div v-if="showConfirmReject" class="modal-overlay" @click="showConfirmReject = false">
        <div class="modal-confirm" @click.stop>
          <div class="confirm-header">
            <div class="confirm-icon">❌</div>
            <h3>Xác nhận từ chối</h3>
          </div>

          <p class="confirm-message">
            Bạn chắc chắn muốn từ chối đơn của
            <strong>{{ selectedAppeal?.email }}</strong>?
          </p>

          <div class="edit-section">
            <div class="detail-row">
              <label class="detail-label">Ghi chú từ chối</label>
              <textarea v-model="updateNote" class="note-input" placeholder="Thêm ghi chú admin (tùy chọn)..."
                rows="3"></textarea>
            </div>
          </div>

          <div class="confirm-actions">
            <button class="btn-cancel-confirm" @click="showConfirmReject = false">
              Hủy
            </button>

            <button class="btn-confirm-reject" @click="confirmReject" :disabled="isUpdating">
              {{ isUpdating ? 'Đang xử lý...' : 'Xác nhận từ chối' }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- DETAIL MODAL -->
    <transition name="modal-fade">
      <div v-if="selectedAppeal && !showConfirmApprove && !showConfirmReject" class="modal-overlay"
        @click="closeDetail">
        <div class="modal-detail" @click.stop>
          <div class="modal-header">
            <h3>Chi tiết khiếu nại</h3>
            <button class="btn-close-modal" @click="closeDetail">
              <i class="fa-solid fa-xmark"></i>
            </button>
          </div>

          <div class="modal-body">
            <div class="detail-section">
              <div class="detail-row">
                <label class="detail-label">ID Khiếu nại</label>
                <p class="detail-value">#{{ selectedAppeal.appealID }}</p>
              </div>

              <div class="detail-row">
                <label class="detail-label">Email</label>
                <p class="detail-value">{{ selectedAppeal.email }}</p>
              </div>

              <div class="detail-row">
                <label class="detail-label">Trạng thái</label>
                <div class="status-pill" :class="['status-' + selectedAppeal.status.toLowerCase()]">
                  <span class="status-dot"></span> {{ getStatusLabel(selectedAppeal.status) }}
                </div>
              </div>

              <div class="detail-row">
                <label class="detail-label">Lý do khiếu nại</label>
                <p class="detail-value reason-text">{{ selectedAppeal.reason }}</p>
              </div>

              <div class="detail-row">
                <label class="detail-label">Ngày gửi</label>
                <p class="detail-value">{{ formatDateTime(selectedAppeal.createdAt) }}</p>
              </div>

            </div>

            <div v-if="selectedAppeal.status === 'Pending'" class="edit-section">
              <div class="detail-row">
                <label class="detail-label">Ghi chú mới</label>
                <textarea v-model="updateNote" class="note-input" placeholder="Thêm ghi chú admin (tùy chọn)..."
                  rows="4"></textarea>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button v-if="selectedAppeal.status === 'Pending'" class="btn-danger" @click="openAction('REJECT')"
              :disabled="isUpdating">
              <i class="fa-solid fa-ban"></i>
              {{ isUpdating ? 'Đang xử lý...' : 'Từ chối' }}
            </button>

            <button v-if="selectedAppeal.status === 'Pending'" class="btn-success" @click="openAction('APPROVE')"
              :disabled="isUpdating">
              <i class="fa-solid fa-check"></i> {{ isUpdating ? 'Đang xử lý...' : 'Phê duyệt' }}
            </button>

            <button class="btn-secondary" @click="closeDetail">
              <i class="fa-solid fa-xmark"></i> Đóng
            </button>
          </div>
        </div>
      </div>
    </transition>
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
  { value: 'Resolved', label: 'Đã giải quyết' },
  { value: 'Rejected', label: 'Đã Từ chối' }
]

// ─────────────────────────────────────────────────────────────────
// COMPUTED PROPERTIES
// ─────────────────────────────────────────────────────────────────

const getPendingCount = computed(() => appeals.value.filter(a => a.status === 'Pending').length)
const getResolvedCount = computed(() => appeals.value.filter(a => a.status === 'Resolved').length)
const getRejectedCount = computed(() => appeals.value.filter(a => a.status === 'Rejected').length)

const filteredAppeals = computed(() => {
  return appeals.value.filter(appeal => {
    const matchesEmail = appeal.email.toLowerCase().includes(searchEmail.value.toLowerCase())
    const matchesStatus = !filterStatus.value || appeal.status === filterStatus.value
    return matchesEmail && matchesStatus
  })
})

// ─────────────────────────────────────────────────────────────────
// DATA LOADING
// ─────────────────────────────────────────────────────────────────

const loadAppeals = async () => {
  loading.value = true
  console.log('[AppealManagement] Loading appeals...')

  try {
    const data = await getAppeals()
    appeals.value = Array.isArray(data) ? data : (data.data || data.appeals || [])
    console.log('[AppealManagement] Appeals loaded:', appeals.value.length)
  } catch (error) {
    console.error('[AppealManagement] Error loading appeals:', error.message)
    toast.error('Lỗi khi tải khiếu nại')
  } finally {
    loading.value = false
  }
}

// ─────────────────────────────────────────────────────────────────
// SINGLE ENTRY POINT FOR ACTIONS
// ─────────────────────────────────────────────────────────────────

/**
 * Single function to handle both approve and reject actions.
 * Flow: Click button → set selectedAppeal → decide based on type → open modal
 */
const openAction = (type, appeal = null) => {
  // If appeal passed from table → set it
  if (appeal) {
    selectedAppeal.value = appeal
    updateNote.value = appeal.note || ''
  }

  // If no selectedAppeal → return (safety check)
  if (!selectedAppeal.value) return

  // Open appropriate modal based on type
  if (type === 'APPROVE') {
    updateNote.value = selectedAppeal.value.note || 'Đã được gỡ ban'
    showConfirmApprove.value = true
  } else if (type === 'REJECT') {
    updateNote.value = selectedAppeal.value.note || 'Đơn bị từ chối'
    showConfirmReject.value = true
  }
}

// ─────────────────────────────────────────────────────────────────
// CONFIRM ACTIONS
// ─────────────────────────────────────────────────────────────────

/**
 * Confirm approve: Call unbanAccountByAppeal, reload, close modals
 */
const confirmApprove = async () => {
  if (!selectedAppeal.value) return

  isUpdating.value = true
  try {
    await unbanAccountByAppeal(selectedAppeal.value.appealID)
    toast.success('Đơn khiếu nại đã được phê duyệt. Email thông báo đã gửi.')

    // Reload and close
    await loadAppeals()
    resetState()
  } catch (error) {
    toast.error(error.response?.data?.message || 'Lỗi khi phê duyệt')
  } finally {
    isUpdating.value = false
  }
}

/**
 * Confirm reject: Call updateAppeal with Rejected status, reload, close modals
 */
const confirmReject = async () => {
  if (!selectedAppeal.value) return

  isUpdating.value = true
  try {
    await updateAppeal(selectedAppeal.value.appealID, {
      status: 'Rejected',
      note: updateNote.value || 'Đơn bị từ chối'
    })
    toast.success('Đơn khiếu nại đã bị từ chối. Email thông báo đã gửi.')

    // Reload and close
    await loadAppeals()
    resetState()
  } catch (error) {
    toast.error(error.response?.data?.message || 'Lỗi khi từ chối')
  } finally {
    isUpdating.value = false
  }
}

// ─────────────────────────────────────────────────────────────────
// DETAIL MODAL MANAGEMENT
// ─────────────────────────────────────────────────────────────────

const openDetail = (appeal) => {
  selectedAppeal.value = { ...appeal }
  updateNote.value = appeal.note || ''
}

const closeDetail = () => {
  resetState()
}

/**
 * Reset all modal and state variables
 */
const resetState = () => {
  selectedAppeal.value = null
  showConfirmApprove.value = false
  showConfirmReject.value = false
  updateNote.value = ''
}

// ─────────────────────────────────────────────────────────────────
// UTILITY FUNCTIONS
// ─────────────────────────────────────────────────────────────────

const getStatusLabel = status => {
  const labels = {
    Pending: '⏳ Chờ xử lý',
    Resolved: '✅ Đã phê duyệt',
    Rejected: '❌ Đã từ chối'
  }
  return labels[status] || status
}

const formatDate = date => {
  return new Date(date).toLocaleDateString('vi-VN')
}

const formatDateTime = date => {
  return new Date(date).toLocaleString('vi-VN')
}

// ─────────────────────────────────────────────────────────────────
// LIFECYCLE
// ─────────────────────────────────────────────────────────────────

onMounted(() => {
  loadAppeals()
})
</script>

<style scoped lang="scss">
.page-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

.animate-enter {
  animation: slideInDown 0.5s ease;
}

@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* PAGE HEADER */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;

  .title-group {
    .title {
      font-size: 28px;
      font-weight: 800;
      margin: 0 0 8px;
      color: #1c1917;
      font-family: 'Playfair Display', serif;
    }

    .subtitle {
      color: #78716c;
      font-size: 14px;
      margin: 0;
    }
  }

  .header-actions {
    .btn-refresh {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 16px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s;

      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 8px 16px rgba(102, 126, 234, 0.4);
      }

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }

      i {
        font-size: 14px;
      }
    }
  }
}

/* STATS GRID */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 32px;

  .stat-card {
    display: flex;
    gap: 16px;
    padding: 20px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.5));
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 12px;
    backdrop-filter: blur(10px);
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
    }

    .stat-icon-wrapper {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 56px;
      height: 56px;
      border-radius: 12px;
      font-size: 24px;

      &.bg-orange-light {
        background: rgba(234, 88, 12, 0.1);

        i {
          color: #ea580c;
        }
      }

      &.bg-yellow-light {
        background: rgba(251, 191, 36, 0.1);

        i {
          color: #fbbf24;
        }
      }

      &.bg-green-light {
        background: rgba(16, 185, 129, 0.1);

        i {
          color: #10b981;
        }
      }

      &.bg-red-light {
        background: rgba(239, 68, 68, 0.1);

        i {
          color: #ef4444;
        }
      }
    }

    .stat-info {
      display: flex;
      flex-direction: column;
      justify-content: center;

      .stat-value {
        font-size: 24px;
        font-weight: 800;
        color: #1c1917;
        line-height: 1;
      }

      .stat-label {
        font-size: 12px;
        color: #78716c;
        margin-top: 4px;
      }
    }
  }
}

/* FILTER BAR */
.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  align-items: center;

  .tabs {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;

    .filter-tab {
      padding: 8px 16px;
      background: rgba(226, 232, 240, 0.5);
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      color: #475569;
      font-weight: 600;
      font-size: 13px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: rgba(226, 232, 240, 0.8);
        border-color: #cbd5e1;
      }

      &.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-color: #667eea;
      }
    }
  }

  .search-box {
    flex: 1;
    min-width: 250px;
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 0 12px;
    background: white;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    transition: all 0.3s;

    &:focus-within {
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    .search-icon {
      color: #94a3b8;
      font-size: 14px;
    }

    .search-input {
      flex: 1;
      border: none;
      background: transparent;
      outline: none;
      padding: 10px 0;
      font-size: 14px;
      color: #1c1917;

      &::placeholder {
        color: #cbd5e1;
      }
    }

    .clear-search {
      background: none;
      border: none;
      color: #cbd5e1;
      cursor: pointer;
      padding: 4px;
      display: flex;
      align-items: center;
      transition: all 0.3s;

      &:hover {
        color: #ea580c;
      }
    }
  }
}

/* TABLE */
.table-wrapper {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);

  .loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 16px;
    padding: 60px 20px;
    color: #94a3b8;

    .spinner-modern {
      width: 40px;
      height: 40px;
      border: 3px solid rgba(226, 232, 240, 0.8);
      border-top-color: #667eea;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }
  }

  .data-table {
    width: 100%;
    border-collapse: collapse;

    thead {
      background: linear-gradient(135deg, rgba(226, 232, 240, 0.5), rgba(226, 232, 240, 0.3));
      border-bottom: 2px solid #e2e8f0;

      th {
        padding: 16px;
        text-align: left;
        font-size: 12px;
        font-weight: 700;
        color: #475569;
        text-transform: uppercase;
        letter-spacing: 0.5px;

        &.text-center {
          text-align: center;
        }
      }
    }

    tbody {
      tr {
        border-bottom: 1px solid #e2e8f0;
        transition: all 0.2s;

        &:hover {
          background: rgba(102, 126, 234, 0.04);
        }

        &.empty-state {
          &:hover {
            background: transparent;
          }

          td {
            padding: 60px 20px;
            text-align: center;

            .empty-icon {
              font-size: 48px;
              margin-bottom: 12px;
              opacity: 0.5;
            }

            p {
              color: #94a3b8;
              font-size: 14px;
              margin: 0;
            }
          }
        }
      }
    }

    td {
      padding: 16px;
      color: #1c1917;
      font-size: 14px;

      &.text-center {
        text-align: center;
      }

      .col-id {
        font-weight: 600;
        color: #667eea;
        font-family: 'Courier New', monospace;
      }
    }
  }
}

/* EMAIL CELL */
.email-cell {
  display: flex;
  gap: 12px;
  align-items: flex-start;

  .email-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    background: rgba(234, 88, 12, 0.1);
    border-radius: 8px;
    color: #ea580c;
    font-size: 14px;
    flex-shrink: 0;
  }

  .email-info {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .email-main {
      font-weight: 600;
      color: #1c1917;
      word-break: break-all;
    }

    .email-reason {
      font-size: 12px;
      color: #94a3b8;
    }
  }
}

/* STATUS PILL */
.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;

  .status-dot {
    display: inline-block;
    width: 6px;
    height: 6px;
    border-radius: 50%;
  }

  &.status-pending {
    background: rgba(251, 191, 36, 0.1);
    color: #b45309;

    .status-dot {
      background: #fbbf24;
    }
  }

  &.status-resolved {
    background: rgba(16, 185, 129, 0.1);
    color: #065f46;

    .status-dot {
      background: #10b981;
    }
  }

  &.status-rejected {
    background: rgba(239, 68, 68, 0.1);
    color: #7f1d1d;

    .status-dot {
      background: #ef4444;
    }
  }
}

.date-text {
  color: #78716c;
  font-size: 13px;
}

/* ACTION BUTTONS */
.actions {
  display: flex;
  gap: 8px;
  justify-content: center;

  .btn-action {
    width: 36px;
    height: 36px;
    border: 1px solid #e2e8f0;
    background: white;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s;

    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }

    &.view {
      color: #667eea;

      &:hover {
        background: rgba(102, 126, 234, 0.1);
        border-color: #667eea;
      }
    }

    &.approve {
      color: #10b981;

      &:hover {
        background: rgba(16, 185, 129, 0.1);
        border-color: #10b981;
      }
    }

    &.reject {
      color: #ef4444;

      &:hover {
        background: rgba(239, 68, 68, 0.1);
        border-color: #ef4444;
      }
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
}

/* MODALS */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  animation: fadeIn 0.3s ease;
}

.modal-confirm,
.modal-detail {
  background: white;
  border-radius: 16px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* CONFIRM MODAL */
.modal-confirm {
  max-width: 400px;
  width: 100%;
  text-align: center;
  padding: 40px 32px;

  .confirm-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 24px;

    .confirm-icon {
      font-size: 48px;
      margin-bottom: 16px;
      animation: bounce 0.6s ease infinite;
    }

    h3 {
      font-family: 'Playfair Display', serif;
      font-size: 22px;
      font-weight: 800;
      margin: 0;
      color: #1c1917;
    }
  }

  .confirm-message {
    color: #78716c;
    font-size: 15px;
    line-height: 1.6;
    margin: 0 0 32px;

    strong {
      color: #1c1917;
      word-break: break-all;
    }
  }

  .edit-section {
    padding: 0 0 24px 0;
    margin: 0 0 24px 0;
    border-bottom: 1px solid #e2e8f0;
  }

  .confirm-actions {
    display: flex;
    gap: 12px;

    button {
      flex: 1;
      padding: 12px 16px;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s;
      font-size: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    }

    .btn-cancel-confirm {
      background: #e2e8f0;
      color: #475569;

      &:hover:not(:disabled) {
        background: #cbd5e1;
      }

      &:disabled {
        opacity: 0.5;
      }
    }

    .btn-confirm-approve {
      background: linear-gradient(135deg, #10b981 0%, #059669 100%);
      color: white;

      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 8px 16px rgba(16, 185, 129, 0.4);
      }

      &:disabled {
        opacity: 0.6;
      }
    }

    .btn-confirm-reject {
      background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
      color: white;

      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 8px 16px rgba(239, 68, 68, 0.4);
      }

      &:disabled {
        opacity: 0.6;
      }
    }
  }
}

@keyframes bounce {

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-8px);
  }
}

/* DETAIL MODAL */
.modal-detail {
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24px;
    border-bottom: 1px solid #e2e8f0;
    flex-shrink: 0;

    h3 {
      font-family: 'Playfair Display', serif;
      font-size: 20px;
      font-weight: 800;
      margin: 0;
      color: #1c1917;
    }

    .btn-close-modal {
      background: none;
      border: none;
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      color: #94a3b8;
      border-radius: 6px;
      transition: all 0.3s;

      &:hover {
        background: #f1f5f9;
        color: #ea580c;
      }
    }
  }

  .modal-body {
    flex: 1;
    overflow-y: auto;
    padding: 24px;

    .detail-section {
      margin-bottom: 24px;
    }

    .edit-section {
      padding-top: 24px;
      border-top: 1px solid #e2e8f0;
      margin-top: 24px;
    }
  }

  .modal-footer {
    display: flex;
    gap: 12px;
    padding: 24px;
    border-top: 1px solid #e2e8f0;
    flex-shrink: 0;

    button {
      flex: 1;
      min-height: 40px;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    }

    .btn-success {
      background: linear-gradient(135deg, #10b981 0%, #059669 100%);
      color: white;

      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 8px 16px rgba(16, 185, 129, 0.4);
      }

      &:disabled {
        opacity: 0.6;
      }
    }

    .btn-danger {
      background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
      color: white;

      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 8px 16px rgba(239, 68, 68, 0.4);
      }

      &:disabled {
        opacity: 0.6;
      }
    }

    .btn-secondary {
      background: #e2e8f0;
      color: #475569;

      &:hover {
        background: #cbd5e1;
      }
    }
  }
}

/* DETAIL ROW */
.detail-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;

  .detail-label {
    font-weight: 600;
    font-size: 13px;
    color: #475569;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  .detail-value {
    color: #1c1917;
    font-size: 14px;
    line-height: 1.6;
    word-break: break-word;
    margin: 0;

    &.reason-text {
      white-space: pre-wrap;
      background: #f1f5f9;
      padding: 12px;
      border-radius: 6px;
    }

    &.note-text {
      background: #fef3c7;
      padding: 12px;
      border-radius: 6px;
    }
  }

  .status-pill {
    width: fit-content;
  }
}

/* FORM INPUTS */
.note-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.3s;
  resize: vertical;
  min-height: 80px;

  &:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }
}

/* ANIMATIONS */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: all 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;

  .modal-confirm,
  .modal-detail {
    transform: translateY(20px);
  }
}

.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.list-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* RESPONSIVE */
@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-bar {
    flex-direction: column;

    .tabs {
      width: 100%;
      overflow-x: auto;
    }

    .search-box {
      width: 100%;
    }
  }

  .modal-detail {
    max-height: 100vh;
  }

  .actions {
    flex-direction: column;

    .btn-action {
      width: 100%;
    }
  }
}
</style>
