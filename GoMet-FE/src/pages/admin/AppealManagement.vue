<template>
  <section class="appeals-page">
    <div class="page-header">
      <h1>Khiếu nại ban nhầm</h1>
      <p>Quản lý các khiếu nại từ người dùng bị ban</p>
    </div>

    <div class="filters-bar">
      <input
        v-model="searchEmail"
        type="email"
        placeholder="Tìm kiếm theo email..."
        class="search-input"
      />
      <select v-model="filterStatus" class="filter-select">
        <option value="">Tất cả trạng thái</option>
        <option value="Pending">Chờ xử lý</option>
        <option value="Review">Đang xem xét</option>
        <option value="Resolved">Đã giải quyết</option>
        <option value="Rejected">Từ chối</option>
      </select>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Đang tải dữ liệu...</p>
    </div>

    <div v-else-if="appeals.length === 0" class="empty-state">
      <div class="empty-icon">📋</div>
      <p>Không có khiếu nại nào</p>
    </div>

    <div v-else class="appeals-grid">
      <div v-for="appeal in filteredAppeals" :key="appeal.appealID" class="appeal-card">
        <div class="card-header">
          <div class="email-badge">{{ appeal.email }}</div>
          <div :class="['status-badge', appeal.status.toLowerCase()]">
            {{ getStatusLabel(appeal.status) }}
          </div>
        </div>

        <div class="card-body">
          <p class="reason-preview">{{ appeal.reason.substring(0, 150) }}...</p>
          <div class="meta-info">
            <span class="date">{{ formatDate(appeal.createdAt) }}</span>
          </div>
        </div>

        <div class="card-footer">
          <button class="btn-detail" @click="openDetail(appeal)">
            Xem chi tiết
          </button>
        </div>
      </div>
    </div>

    <!-- Detail Modal -->
    <transition name="modal-motion">
      <div v-if="selectedAppeal" class="modal-overlay" @click="closeDetail">
        <div class="modal-container" @click.stop>
          <button class="btn-close" @click="closeDetail">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>

          <div class="detail-header">
            <h2>Chi tiết khiếu nại</h2>
            <div :class="['status-badge', selectedAppeal.status.toLowerCase()]">
              {{ getStatusLabel(selectedAppeal.status) }}
            </div>
          </div>

          <div class="detail-content">
            <div class="detail-row">
              <label>Email:</label>
              <p>{{ selectedAppeal.email }}</p>
            </div>

            <div class="detail-row">
              <label>Lý do:</label>
              <p class="reason-text">{{ selectedAppeal.reason }}</p>
            </div>

            <div class="detail-row">
              <label>Ngày gửi:</label>
              <p>{{ formatDateTime(selectedAppeal.createdAt) }}</p>
            </div>

            <div v-if="selectedAppeal.note" class="detail-row">
              <label>Ghi chú admin:</label>
              <p>{{ selectedAppeal.note }}</p>
            </div>

            <div v-if="selectedAppeal.status !== 'Resolved'" class="detail-row">
              <label>Cập nhật trạng thái:</label>
              <div class="status-actions">
                <select v-model="updateStatus" class="status-select">
                  <option value="Pending">Chờ xử lý</option>
                  <option value="Review">Đang xem xét</option>
                  <option value="Rejected">Từ chối</option>
                </select>
              </div>
            </div>

            <div class="detail-row">
              <label>Ghi chú:</label>
              <textarea
                v-model="updateNote"
                class="note-textarea"
                placeholder="Thêm ghi chú admin..."
              ></textarea>
            </div>
          </div>

          <div class="detail-actions">
            <button
              v-if="selectedAppeal.status !== 'Resolved'"
              class="btn-update"
              @click="handleUpdate"
              :disabled="isUpdating"
            >
              <span v-if="isUpdating">Đang cập nhật...</span>
              <span v-else>Cập nhật</span>
            </button>

            <button
              v-if="selectedAppeal.status !== 'Resolved'"
              class="btn-unban"
              @click="handleUnban"
              :disabled="isUnbanning"
            >
              <span v-if="isUnbanning">Đang gỡ ban...</span>
              <span v-else>Gỡ ban & Phê duyệt</span>
            </button>

            <button class="btn-cancel" @click="closeDetail">
              Đóng
            </button>
          </div>
        </div>
      </div>
    </transition>
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { toast } from '@/composables/useToast'
import { getAppeals, updateAppeal, unbanAccountByAppeal } from '@/services/appealService'

const appeals = ref([])
const selectedAppeal = ref(null)
const loading = ref(false)
const isUpdating = ref(false)
const isUnbanning = ref(false)

const searchEmail = ref('')
const filterStatus = ref('')
const updateStatus = ref('Pending')
const updateNote = ref('')

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
    toast.error('Lỗi khi tải khiếu nại')
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

    toast.success('Cập nhật thành công')

    // Reload appeals
    await loadAppeals()
    closeDetail()
  } catch (error) {
    toast.error(error.response?.data?.message || 'Lỗi khi cập nhật')
  } finally {
    isUpdating.value = false
  }
}

const handleUnban = async () => {
  if (!selectedAppeal.value) return

  if (!confirm(`Bạn chắc chắn muốn gỡ ban cho email ${selectedAppeal.value.email}?`)) {
    return
  }

  isUnbanning.value = true
  try {
    await unbanAccountByAppeal(selectedAppeal.value.appealID)
    toast.success('Tài khoản đã được gỡ ban thành công')

    // Reload appeals
    await loadAppeals()
    closeDetail()
  } catch (error) {
    toast.error(error.response?.data?.message || 'Lỗi khi gỡ ban')
  } finally {
    isUnbanning.value = false
  }
}

const getStatusLabel = (status) => {
  const labels = {
    Pending: '⏳ Chờ xử lý',
    Review: '👀 Đang xem xét',
    Resolved: '✅ Đã giải quyết',
    Rejected: '❌ Từ chối'
  }
  return labels[status] || status
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN')
}

const formatDateTime = (date) => {
  return new Date(date).toLocaleString('vi-VN')
}

onMounted(() => {
  loadAppeals()
})
</script>

<style scoped lang="scss">
.appeals-page {
  padding: 32px 24px;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'Quicksand', sans-serif;
}

.page-header {
  margin-bottom: 32px;

  h1 {
    font-family: 'Playfair Display', serif;
    font-size: 32px;
    font-weight: 800;
    margin: 0 0 8px;
    color: #1c1917;
  }

  p {
    color: #78716c;
    margin: 0;
  }
}

.filters-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.search-input,
.filter-select {
  padding: 10px 16px;
  border: 1px solid #e7e5e4;
  border-radius: 8px;
  font-family: 'Quicksand', sans-serif;
  font-size: 14px;

  &:focus {
    outline: none;
    border-color: #ea580c;
    box-shadow: 0 0 0 3px rgba(234, 88, 12, 0.1);
  }
}

.search-input {
  flex: 1;
  min-width: 250px;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e7e5e4;
  border-top-color: #ea580c;
  border-radius: 50%;
  margin: 0 auto 16px;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.appeals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.appeal-card {
  background: white;
  border: 1px solid #e7e5e4;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
    transform: translateY(-4px);
  }
}

.card-header {
  padding: 16px;
  border-bottom: 1px solid #e7e5e4;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.email-badge {
  font-weight: 600;
  font-size: 14px;
  color: #1c1917;
  word-break: break-all;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;

  &.pending {
    background: #fef3c7;
    color: #92400e;
  }

  &.review {
    background: #bfdbfe;
    color: #1e40af;
  }

  &.resolved {
    background: #d1fae5;
    color: #065f46;
  }

  &.rejected {
    background: #fee2e2;
    color: #991b1b;
  }
}

.card-body {
  padding: 16px;
}

.reason-preview {
  color: #78716c;
  font-size: 13px;
  line-height: 1.5;
  margin: 0 0 12px;
}

.meta-info {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #a8a29e;
}

.card-footer {
  padding: 12px 16px;
  border-top: 1px solid #e7e5e4;
}

.btn-detail {
  width: 100%;
  padding: 10px;
  background: #ea580c;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: #c2410c;
  }
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-container {
  background: white;
  border-radius: 16px;
  padding: 32px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  position: relative;
}

.btn-close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 40px;
  height: 40px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;

  &:hover {
    background: #ea580c;
    color: white;
  }
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  h2 {
    font-size: 22px;
    font-weight: 800;
    margin: 0;
    font-family: 'Playfair Display', serif;
  }
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 24px;
}

.detail-row {
  display: flex;
  flex-direction: column;
  gap: 8px;

  label {
    font-weight: 600;
    font-size: 14px;
    color: #1c1917;
  }

  p {
    margin: 0;
    color: #78716c;
    font-size: 14px;
    line-height: 1.6;
    word-break: break-word;
  }
}

.reason-text {
  white-space: pre-wrap;
}

.status-actions {
  display: flex;
  gap: 8px;
}

.status-select {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #e7e5e4;
  border-radius: 6px;
  font-size: 14px;
  font-family: 'Quicksand', sans-serif;

  &:focus {
    outline: none;
    border-color: #ea580c;
  }
}

.note-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e7e5e4;
  border-radius: 6px;
  font-family: 'Quicksand', sans-serif;
  font-size: 14px;
  resize: vertical;
  min-height: 100px;

  &:focus {
    outline: none;
    border-color: #ea580c;
  }
}

.detail-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.btn-update,
.btn-unban,
.btn-cancel {
  flex: 1;
  min-width: 120px;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.btn-update {
  background: #3b82f6;
  color: white;

  &:hover:not(:disabled) {
    background: #2563eb;
  }
}

.btn-unban {
  background: #10b981;
  color: white;

  &:hover:not(:disabled) {
    background: #059669;
  }
}

.btn-cancel {
  background: #e7e5e4;
  color: #1c1917;

  &:hover {
    background: #d6d3d1;
  }
}

.btn-update:disabled,
.btn-unban:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.modal-motion-enter-active {
  animation: slideIn 0.3s ease;
}

.modal-motion-leave-active {
  animation: slideIn 0.3s ease reverse;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@media (max-width: 768px) {
  .appeals-page {
    padding: 20px 16px;
  }

  .appeals-grid {
    grid-template-columns: 1fr;
  }

  .modal-container {
    padding: 24px 16px;
  }
}
</style>
