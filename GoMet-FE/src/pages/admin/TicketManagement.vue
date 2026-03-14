<template>
  <div class="ticket-management">
    <div class="page-header">
      <h1>Quản lý Góp ý Hệ thống</h1>
      <p>Quản lý các góp ý và phản hồi từ người dùng</p>
    </div>

    <div class="content-wrapper">
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ tickets.length }}</div>
            <div class="stat-label">Góp ý chưa đọc</div>
          </div>
        </div>
      </div>

      <div class="table-container">
        <div class="table-header">
          <h3>Danh sách góp ý</h3>
        </div>

        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>Đang tải dữ liệu...</p>
        </div>

        <div v-else-if="tickets.length === 0" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          </svg>
          <h4>Không có góp ý nào</h4>
          <p>Tất cả góp ý đã được xử lý</p>
        </div>

        <div v-else class="table-wrapper">
          <table class="tickets-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Người dùng</th>
                <th>Tiêu đề</th>
                <th>Nội dung</th>
                <th>Đính kèm</th>
                <th>Thời gian</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="ticket in tickets" :key="ticket.ticketID" class="ticket-row">
                <td class="ticket-id">{{ ticket.ticketID }}</td>
                <td class="user-info">
                  <div class="user-details">
                    <div class="username">{{ ticket.username }}</div>
                    <div class="email">{{ ticket.email }}</div>
                  </div>
                </td>
                <td class="title">{{ ticket.title }}</td>
                <td class="description">
                  <div class="description-text">{{ truncateText(ticket.description, 100) }}</div>
                  <button v-if="ticket.description.length > 100" @click="showFullDescription(ticket)" class="show-more-btn">
                    Xem thêm
                  </button>
                </td>
                <td class="attachment">
                  <span v-if="ticket.attachment" class="attachment-link">
                    📎 File đính kèm
                  </span>
                  <span v-else class="no-attachment">Không có</span>
                </td>
                <td class="created-at">{{ formatDate(ticket.createdAt) }}</td>
                <td class="actions">
                  <button @click="markAsRead(ticket.ticketID)" class="btn-mark-read" :disabled="markingAsRead === ticket.ticketID">
                    {{ markingAsRead === ticket.ticketID ? 'Đang xử lý...' : 'Đã đọc' }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Full Description Modal -->
    <div v-if="showDescriptionModal" class="modal-backdrop" @click="closeDescriptionModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ selectedTicket?.title }}</h3>
          <button @click="closeDescriptionModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <p>{{ selectedTicket?.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { toast } from '@/composables/useToast'

const tickets = ref([])
const loading = ref(true)
const markingAsRead = ref(null)
const showDescriptionModal = ref(false)
const selectedTicket = ref(null)

const fetchTickets = async () => {
  try {
    loading.value = true
    const response = await fetch('/api/admin/tickets')
    if (response.ok) {
      tickets.value = await response.json()
    } else {
      throw new Error('Failed to fetch tickets')
    }
  } catch (error) {
    console.error('Error fetching tickets:', error)
    toast.error('Không thể tải danh sách góp ý')
  } finally {
    loading.value = false
  }
}

const markAsRead = async (ticketId) => {
  try {
    markingAsRead.value = ticketId
    const response = await fetch(`/api/admin/tickets/${ticketId}/read`, {
      method: 'PUT'
    })

    if (response.ok) {
      // Remove the ticket from the list
      tickets.value = tickets.value.filter(ticket => ticket.ticketID !== ticketId)
      toast.success('Đã đánh dấu góp ý là đã đọc')
    } else {
      throw new Error('Failed to mark as read')
    }
  } catch (error) {
    console.error('Error marking ticket as read:', error)
    toast.error('Không thể đánh dấu đã đọc')
  } finally {
    markingAsRead.value = null
  }
}

const truncateText = (text, maxLength) => {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

const showFullDescription = (ticket) => {
  selectedTicket.value = ticket
  showDescriptionModal.value = true
}

const closeDescriptionModal = () => {
  showDescriptionModal.value = false
  selectedTicket.value = null
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchTickets()
})
</script>

<style scoped>
.ticket-management {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #111827;
  margin: 0 0 8px 0;
}

.page-header p {
  color: #6b7280;
  margin: 0;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  background: #ea580c;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.stat-label {
  color: #6b7280;
  font-size: 0.875rem;
  margin: 4px 0 0 0;
}

.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.table-header {
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.table-header h3 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 24px;
  text-align: center;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top: 3px solid #ea580c;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state svg {
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state h4 {
  margin: 0 0 8px 0;
  color: #6b7280;
  font-size: 1.125rem;
}

.empty-state p {
  margin: 0;
  color: #9ca3af;
}

.table-wrapper {
  overflow-x: auto;
}

.tickets-table {
  width: 100%;
  border-collapse: collapse;
}

.tickets-table th,
.tickets-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.tickets-table th {
  background: #f9fafb;
  font-weight: 600;
  color: #374151;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.ticket-row:hover {
  background: #f9fafb;
}

.ticket-id {
  font-weight: 600;
  color: #ea580c;
}

.user-info {
  min-width: 200px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-weight: 500;
  color: #111827;
}

.email {
  font-size: 0.875rem;
  color: #6b7280;
}

.title {
  font-weight: 500;
  color: #111827;
  max-width: 200px;
}

.description {
  max-width: 300px;
}

.description-text {
  margin-bottom: 4px;
}

.show-more-btn {
  background: none;
  border: none;
  color: #ea580c;
  cursor: pointer;
  font-size: 0.875rem;
  padding: 0;
  text-decoration: underline;
}

.attachment-link {
  color: #ea580c;
  font-size: 0.875rem;
}

.no-attachment {
  color: #9ca3af;
  font-size: 0.875rem;
}

.created-at {
  color: #6b7280;
  font-size: 0.875rem;
  white-space: nowrap;
}

.actions {
  text-align: center;
}

.btn-mark-read {
  background: #ea580c;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-mark-read:hover:not(:disabled) {
  background: #c2410c;
}

.btn-mark-read:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Modal Styles */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
}

.modal-body {
  padding: 24px;
}

.modal-body p {
  margin: 0;
  line-height: 1.6;
  color: #374151;
}
</style>