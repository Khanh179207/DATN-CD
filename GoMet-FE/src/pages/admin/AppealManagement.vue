<template>
  <div class="page-container animate-enter">
    
    <div class="page-header">
      <div class="title-group">
        <h2 class="title">{{ $t('admin.appeals.title') }}</h2>
        <p class="subtitle">{{ $t('admin.appeals.subtitle') }}</p>
      </div>
      <div class="header-actions">
        <button class="btn-refresh" @click="loadAppeals" :disabled="loading">
          <i class="fa-solid fa-rotate-right" :class="{ 'fa-spin': loading }"></i> {{ $t('admin.common.sync') }}
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
          <span class="stat-label">{{ $t('admin.appeals.total') }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon-wrapper bg-yellow-light">
          <i class="fa-solid fa-hourglass-start text-yellow"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ getPendingCount }}</span>
          <span class="stat-label">{{ $t('admin.appeals.pending') }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon-wrapper bg-green-light">
          <i class="fa-solid fa-check-circle text-green"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ getResolvedCount }}</span>
          <span class="stat-label">{{ $t('admin.appeals.resolved') }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon-wrapper bg-red-light">
          <i class="fa-solid fa-xmark-circle text-red"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ getRejectedCount }}</span>
          <span class="stat-label">{{ $t('admin.appeals.rejected') }}</span>
        </div>
      </div>
    </div>

    <div class="filter-bar">
      <div class="tabs">
        <button v-for="tab in statusTabs" :key="tab.value" 
                :class="['filter-tab', filterStatus === tab.value ? 'active' : '']"
                @click="filterStatus = tab.value">
          {{ tab.label }}
        </button>
      </div>
      
      <div class="search-box">
        <i class="fa-solid fa-search search-icon"></i>
        <input v-model="searchEmail" type="email" :placeholder="$t('admin.appeals.search_email')" class="search-input" />
        <button v-if="searchEmail" @click="searchEmail = ''" class="clear-search">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>
    </div>

    <div class="table-wrapper">
      <div v-if="loading" class="loading-state">
        <div class="spinner-modern"></div>
        <span>{{ $t('admin.common.loading_data') }}</span>
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th width="12%">ID</th>
            <th width="28%">EMAIL</th>
            <th width="20%">{{ $t('admin.appeals.status_col') }}</th>
            <th width="18%">{{ $t('admin.appeals.created_col') }}</th>
            <th width="22%" class="text-center">{{ $t('admin.appeals.actions_col') }}</th>
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
                <button @click="openDetail(appeal)" class="btn-action view" :title="t('admin.appeals.view_detail')">
                  <i class="fa-solid fa-eye"></i>
                </button>
                
                <button v-if="appeal.status !== 'Resolved'" @click="handleUnban" class="btn-action approve" :title="t('admin.appeals.unban_and_approve')">
                  <i class="fa-solid fa-check"></i>
                </button>

                <button @click="closeDetail" class="btn-action" v-if="selectedAppeal?.appealID === appeal.appealID" :title="t('admin.common.close')">
                  <i class="fa-solid fa-xmark"></i>
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="filteredAppeals.length === 0">
            <td colspan="5" class="empty-state">
              <div class="empty-icon"><i class="fa-solid fa-inbox"></i></div>
              <p>{{ $t('admin.appeals.empty') }}</p>
            </td>
          </tr>
        </TransitionGroup>
      </table>
    </div>

    <!-- Confirmation Modal for Unban -->
    <transition name="modal-fade">
      <div v-if="showConfirmUnban" class="modal-overlay" @click="cancelUnban">
        <div class="modal-confirm" @click.stop>
          <div class="confirm-header">
            <div class="confirm-icon">⚠️</div>
            <h3>{{ $t('admin.appeals.confirm_unban_title') }}</h3>
          </div>
          <p class="confirm-message">
            {{ $t('admin.appeals.confirm_unban_message', { email: selectedAppeal?.email }) }}
          </p>
          <div class="confirm-actions">
            <button class="btn-cancel-confirm" @click="cancelUnban" :disabled="isUnbanning">
              {{ $t('admin.common.cancel') }}
            </button>
            <button class="btn-confirm-unban" @click="confirmUnban" :disabled="isUnbanning">
              <span v-if="isUnbanning"><i class="fa-solid fa-spinner fa-spin"></i> {{ $t('admin.appeals.unbanning') }}</span>
              <span v-else><i class="fa-solid fa-check"></i> {{ $t('admin.appeals.confirm_unban_button') }}</span>
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- Detail Modal -->
    <transition name="modal-fade">
      <div v-if="selectedAppeal && !showConfirmUnban" class="modal-overlay" @click="closeDetail">
        <div class="modal-detail" @click.stop>
          <div class="modal-header">
            <h3>{{ $t('admin.appeals.detail_title') }}</h3>
            <button class="btn-close-modal" @click="closeDetail">
              <i class="fa-solid fa-xmark"></i>
            </button>
          </div>

          <div class="modal-body">
            <div class="detail-section">
              <div class="detail-row">
                <label class="detail-label">{{ $t('admin.appeals.appeal_id') }}</label>
                <p class="detail-value">#{{ selectedAppeal.appealID }}</p>
              </div>

              <div class="detail-row">
                <label class="detail-label">{{ $t('admin.common.email') }}</label>
                <p class="detail-value">{{ selectedAppeal.email }}</p>
              </div>

              <div class="detail-row">
                <label class="detail-label">{{ $t('admin.appeals.status_col') }}</label>
                <div class="status-pill" :class="['status-' + selectedAppeal.status.toLowerCase()]">
                  <span class="status-dot"></span> {{ getStatusLabel(selectedAppeal.status) }}
                </div>
              </div>

              <div class="detail-row">
                <label class="detail-label">{{ $t('admin.appeals.appeal_reason') }}</label>
                <p class="detail-value reason-text">{{ selectedAppeal.reason }}</p>
              </div>

              <div class="detail-row">
                <label class="detail-label">{{ $t('admin.appeals.created_at') }}</label>
                <p class="detail-value">{{ formatDateTime(selectedAppeal.createdAt) }}</p>
              </div>

              <div v-if="selectedAppeal.note" class="detail-row">
                <label class="detail-label">{{ $t('admin.appeals.admin_note') }}</label>
                <p class="detail-value note-text">{{ selectedAppeal.note }}</p>
              </div>
            </div>

            <div v-if="selectedAppeal.status !== 'Resolved'" class="edit-section">
              <div class="detail-row">
                <label class="detail-label">{{ $t('admin.appeals.update_status') }}</label>
                <select v-model="updateStatus" class="status-input">
                  <option value="Pending">{{ $t('admin.appeals.status_pending') }}</option>
                  <option value="Review">{{ $t('admin.appeals.status_review') }}</option>
                  <option value="Rejected">{{ $t('admin.appeals.status_rejected') }}</option>
                </select>
              </div>

              <div class="detail-row">
                <label class="detail-label">{{ $t('admin.appeals.note') }}</label>
                <textarea
                  v-model="updateNote"
                  class="note-input"
                  :placeholder="t('admin.appeals.note_placeholder')"
                  rows="4"
                ></textarea>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button v-if="selectedAppeal.status !== 'Resolved'" class="btn-primary" @click="handleUpdate" :disabled="isUpdating">
              <i class="fa-solid fa-save"></i> {{ isUpdating ? $t('admin.appeals.updating') : $t('admin.appeals.update') }}
            </button>

            <button v-if="selectedAppeal.status !== 'Resolved'" class="btn-success" @click="handleUnban" :disabled="isUnbanning">
              <i class="fa-solid fa-check"></i> {{ isUnbanning ? $t('admin.appeals.unbanning') : $t('admin.appeals.unban_and_approve') }}
            </button>

            <button class="btn-secondary" @click="closeDetail">
              <i class="fa-solid fa-xmark"></i> {{ $t('admin.common.close') }}
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
import { useI18n } from 'vue-i18n'
import { formatLocaleDate, formatLocaleDateTime } from '@/i18n'

const { t } = useI18n()

const appeals = ref([])
const selectedAppeal = ref(null)
const loading = ref(false)
const isUpdating = ref(false)
const isUnbanning = ref(false)
const showConfirmUnban = ref(false)

const searchEmail = ref('')
const filterStatus = ref('')
const updateStatus = ref('Pending')
const updateNote = ref('')

const statusTabs = [
  { value: '', label: t('admin.common.all') },
  { value: 'Pending', label: t('admin.appeals.status_pending') },
  { value: 'Review', label: t('admin.appeals.status_review') },
  { value: 'Resolved', label: t('admin.appeals.status_resolved') },
  { value: 'Rejected', label: t('admin.appeals.status_rejected') }
]

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

const loadAppeals = async () => {
  loading.value = true
  console.log('[AppealManagement] Loading appeals...', {
    timestamp: new Date().toISOString()
  })

  try {
    const data = await getAppeals()
    console.log('[AppealManagement] Appeals loaded successfully:', {
      rawData: data,
      dataType: typeof data,
      isArray: Array.isArray(data),
      length: Array.isArray(data) ? data.length : data?.data?.length || 0
    })
    
    appeals.value = Array.isArray(data) ? data : (data.data || data.appeals || [])
    console.log('[AppealManagement] Appeals set to:', {
      count: appeals.value.length,
      appeals: appeals.value
    })
  } catch (error) {
    console.error('[AppealManagement] Error loading appeals:', {
      status: error.response?.status,
      message: error.response?.data?.message,
      statusText: error.response?.statusText,
      url: error.config?.url,
      fullError: error.message
    })
    toast.error(t('admin.appeals.load_failed'))
  } finally {
    loading.value = false
  }
}

const openDetail = (appeal) => {
  selectedAppeal.value = { ...appeal }
  updateStatus.value = appeal.status
  updateNote.value = appeal.note || ''
}

const closeDetail = () => {
  selectedAppeal.value = null
}

const handleUpdate = async () => {
  if (!selectedAppeal.value) return

  isUpdating.value = true
  try {
    await updateAppeal(selectedAppeal.value.appealID, {
      status: updateStatus.value,
      note: updateNote.value
    })

    toast.success(t('admin.appeals.update_ok'))

    // Reload appeals
    await loadAppeals()
    closeDetail()
  } catch (error) {
    toast.error(t('admin.appeals.update_failed'))
  } finally {
    isUpdating.value = false
  }
}

const handleUnban = async () => {
  if (!selectedAppeal.value) return
  showConfirmUnban.value = true
}

const confirmUnban = async () => {
  if (!selectedAppeal.value) return

  isUnbanning.value = true
  try {
    await unbanAccountByAppeal(selectedAppeal.value.appealID)
    toast.success(t('admin.appeals.unban_ok'))

    // Reload appeals
    await loadAppeals()
    closeDetail()
    showConfirmUnban.value = false
  } catch (error) {
    toast.error(t('admin.appeals.unban_failed'))
  } finally {
    isUnbanning.value = false
  }
}

const cancelUnban = () => {
  if (!isUnbanning.value) {
    showConfirmUnban.value = false
  }
}

const getStatusLabel = (status) => {
  const labels = {
    Pending: `⏳ ${t('admin.appeals.status_pending')}`,
    Review: `👀 ${t('admin.appeals.status_review')}`,
    Resolved: `✅ ${t('admin.appeals.status_resolved')}`,
    Rejected: `❌ ${t('admin.appeals.status_rejected')}`
  }
  return labels[status] || status
}

const formatDate = (date) => {
  return formatLocaleDate(date, t.locale?.value)
}

const formatDateTime = (date) => {
  return formatLocaleDateTime(date, t.locale?.value)
}

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
  from { opacity: 0; }
  to { opacity: 1; }
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

/* Page Header */
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

/* Stats Grid */
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

/* Filter Bar */
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

/* Table Styles */
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

/* Email Cell */
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

/* Status Pill */
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

  &.status-review {
    background: rgba(59, 130, 246, 0.1);
    color: #1e40af;

    .status-dot {
      background: #3b82f6;
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

/* Action Buttons */
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

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
}

/* Modal Styles */
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

/* Confirmation Modal */
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

  .confirm-actions {
    display: flex;
    gap: 12px;

    .btn-cancel-confirm,
    .btn-confirm-unban {
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

    .btn-confirm-unban {
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
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

/* Detail Modal */
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

    .btn-primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;

      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 8px 16px rgba(102, 126, 234, 0.4);
      }

      &:disabled {
        opacity: 0.6;
      }
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

    .btn-secondary {
      background: #e2e8f0;
      color: #475569;

      &:hover {
        background: #cbd5e1;
      }
    }
  }
}

/* Detail Row */
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

/* Form Inputs */
.status-input,
.note-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.3s;

  &:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }
}

.note-input {
  resize: vertical;
  min-height: 100px;
}

/* Animations */
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

/* Responsive */
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
