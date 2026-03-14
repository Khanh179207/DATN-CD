<template>
  <div class="ticket-sovereign-wrapper">
    <div class="page-header-lux">
      <div>
        <h1 class="page-title">Trung tâm Phản hồi</h1>
        <p class="page-subtitle">Quản lý khiếu nại, báo lỗi và góp ý từ người dùng hệ thống</p>
      </div>
      <button class="btn-export" @click="fetchTickets">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
        Làm mới dữ liệu
      </button>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="icon-wrap all"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="3" y1="9" x2="21" y2="9"></line><line x1="9" y1="21" x2="9" y2="9"></line></svg></div>
        <div class="stat-info">
          <span class="label">Tổng Ticket Chờ</span>
          <h3 class="value">{{ pendingTotal }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon-wrap bug"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"></path></svg></div>
        <div class="stat-info">
          <span class="label">Báo Lỗi (Bug)</span>
          <h3 class="value">{{ pendingBugs }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon-wrap report"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg></div>
        <div class="stat-info">
          <span class="label">Vi phạm (Report)</span>
          <h3 class="value">{{ pendingReports }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon-wrap feedback"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7"></path></svg></div>
        <div class="stat-info">
          <span class="label">Góp ý (Feedback)</span>
          <h3 class="value">{{ pendingFeedbacks }}</h3>
        </div>
      </div>
    </div>

    <div class="data-engine-lux">
      <div class="filter-bar">
        <div class="tabs-lux">
          <button v-for="tab in filterTabs" :key="tab.value" 
                  class="tab-btn" :class="{ active: currentStatusFilter === tab.value }"
                  @click="currentStatusFilter = tab.value">
            {{ tab.label }}
          </button>
        </div>
        <div class="search-filter">
          <select v-model="currentTypeFilter" class="select-lux">
            <option value="ALL">Tất cả phân loại</option>
            <option value="BUG">Báo Lỗi (Bug)</option>
            <option value="REPORT">Báo cáo (Report)</option>
            <option value="FEEDBACK">Góp ý (Feedback)</option>
          </select>
        </div>
      </div>

      <div class="table-responsive">
        <div v-if="loading" class="loading-state">
          <div class="spinner-lux"></div>
          <p>Đang đồng bộ dữ liệu...</p>
        </div>

        <div v-else-if="filteredTickets.length === 0" class="empty-state">
          <div class="empty-icon">📭</div>
          <h4>Không có dữ liệu</h4>
          <p>Không tìm thấy Ticket nào khớp với bộ lọc hiện tại.</p>
        </div>

        <table v-else class="lux-table">
          <thead>
            <tr>
              <th>Mã ID</th>
              <th>Người Gửi</th>
              <th>Phân Loại</th>
              <th>Tiêu đề</th>
              <th>Thời gian</th>
              <th>Trạng thái</th>
              <th class="text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="ticket in filteredTickets" :key="ticket.ticketID" class="lux-row" @click="viewDetail(ticket)">
              <td class="id-col">#{{ ticket.ticketID }}</td>
              <td>
                <div class="user-cell">
                  <span class="user-name">{{ ticket.username || 'Người dùng ẩn' }}</span>
                </div>
              </td>
              <td>
                <span class="badge-type" :class="ticket.ticketType.toLowerCase()">
                  {{ formatType(ticket.ticketType) }}
                </span>
              </td>
              <td class="title-col">{{ truncateText(ticket.title, 40) }}</td>
              <td class="time-col">{{ formatDate(ticket.createdAt) }}</td>
              <td>
                <span class="badge-status" :class="'status-' + ticket.status">
                  {{ formatStatus(ticket.status) }}
                </span>
              </td>
              <td class="text-right">
                <button class="btn-action" @click.stop="viewDetail(ticket)">Chi tiết</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <transition name="fade-glass">
      <div v-if="showModal" class="modal-glass-backdrop" @click="closeModal">
        <div class="modal-lux-content" @click.stop>
          
          <div class="modal-header">
            <div class="header-tags">
              <span class="badge-type" :class="selectedTicket?.ticketType.toLowerCase()">
                {{ formatType(selectedTicket?.ticketType) }}
              </span>
              <span class="badge-status" :class="'status-' + selectedTicket?.status">
                {{ formatStatus(selectedTicket?.status) }}
              </span>
            </div>
            <button class="btn-close" @click="closeModal">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
          </div>

          <div class="modal-body custom-scroll">
            <h2 class="ticket-title">{{ selectedTicket?.title }}</h2>
            
            <div class="ticket-meta">
              <div class="meta-item">
                <label>Người gửi:</label>
                <span>{{ selectedTicket?.account?.username || 'N/A' }}</span>
              </div>
              <div class="meta-item">
                <label>Thời gian gửi:</label>
                <span>{{ formatDate(selectedTicket?.createdAt) }}</span>
              </div>
              <div class="meta-item" v-if="selectedTicket?.targetPostID">
                <label>ID Bài Viết bị Report:</label>
                <a href="#" class="link-post">#{{ selectedTicket.targetPostID }} (Nhấn để xem)</a>
              </div>
            </div>

            <div class="ticket-description">
              <label>Nội dung chi tiết:</label>
              <div class="desc-box">
                {{ selectedTicket?.description }}
              </div>
            </div>

            <div class="ticket-attachment" v-if="selectedTicket?.attachment">
              <label>Tệp đính kèm (Bằng chứng):</label>
              <div class="img-wrapper">
                <img :src="selectedTicket.attachment" alt="Attachment" />
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <div class="footer-left">
              <span class="resolved-time" v-if="selectedTicket?.resolvedAt">
                Giải quyết lúc: {{ formatDate(selectedTicket.resolvedAt) }}
              </span>
            </div>
            <div class="footer-actions">
              <button v-if="selectedTicket?.status === 0" class="btn-lux process" @click="updateStatus(1)">
                Tiếp nhận xử lý
              </button>
              <button v-if="selectedTicket?.status === 0 || selectedTicket?.status === 1" class="btn-lux reject" @click="updateStatus(3)">
                Từ chối
              </button>
              <button v-if="selectedTicket?.status === 1" class="btn-lux resolve" @click="updateStatus(2)">
                Đánh dấu Đã Giải Quyết
              </button>
            </div>
          </div>

        </div>
      </div>
    </transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { toast } from '@/composables/useToast'
import { useAuthStore } from '@/stores/auth' // Gọi store auth để lấy Token gửi cho BE

const authStore = useAuthStore()

// States
const tickets = ref([])
const loading = ref(true)
const showModal = ref(false)
const selectedTicket = ref(null)

// Filters
const currentStatusFilter = ref(-1) // -1: All, 0: Pending, 1: Processing, 2: Resolved, 3: Rejected
const currentTypeFilter = ref('ALL')

const filterTabs = [
  { label: 'Tất cả', value: -1 },
  { label: 'Chờ duyệt', value: 0 },
  { label: 'Đang xử lý', value: 1 },
  { label: 'Đã hoàn thành', value: 2 },
]

// 🚀 API 1: LẤY DỮ LIỆU THẬT TỪ DATABASE
const fetchTickets = async () => {
  try {
    loading.value = true
    const response = await fetch('/api/admin/tickets', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authStore.token || ''}` // Bắn token Admin lên
      }
    })
    
    if (response.ok) {
      tickets.value = await response.json()
    } else {
      toast.error('Không thể lấy dữ liệu từ máy chủ!')
    }
  } catch (error) {
    console.error("Fetch Tickets Error:", error)
    toast.error('Mất kết nối với Backend!')
  } finally {
    loading.value = false
  }
}

// Stats Computed
const pendingTotal = computed(() => tickets.value.filter(t => t.status === 0).length)
const pendingBugs = computed(() => tickets.value.filter(t => t.status === 0 && t.ticketType === 'BUG').length)
const pendingReports = computed(() => tickets.value.filter(t => t.status === 0 && t.ticketType === 'REPORT').length)
const pendingFeedbacks = computed(() => tickets.value.filter(t => t.status === 0 && t.ticketType === 'FEEDBACK').length)

// Table Filter Logic
const filteredTickets = computed(() => {
  return tickets.value.filter(t => {
    const matchStatus = currentStatusFilter.value === -1 || t.status === currentStatusFilter.value;
    const matchType = currentTypeFilter.value === 'ALL' || t.ticketType === currentTypeFilter.value;
    return matchStatus && matchType;
  }).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

// Actions
const viewDetail = (ticket) => {
  selectedTicket.value = ticket
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  setTimeout(() => { selectedTicket.value = null }, 300)
}

// 🚀 API 2: CẬP NHẬT TRẠNG THÁI THẬT XUỐNG DATABASE
const updateStatus = async (newStatus) => {
  try {
    const ticketId = selectedTicket.value.ticketID
    
    const response = await fetch(`/api/admin/tickets/${ticketId}/status?status=${newStatus}`, { 
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authStore.token || ''}`
      }
    })
    
    if(!response.ok) throw new Error('API Error')

    // Cập nhật giao diện mượt mà không cần load lại trang
    const target = tickets.value.find(t => t.ticketID === ticketId)
    if(target) {
      target.status = newStatus
      if(newStatus === 2) target.resolvedAt = new Date().toISOString()
    }
    
    toast.success('Cập nhật trạng thái thành công!')
    closeModal()
  } catch (error) {
    console.error("Update Status Error:", error)
    toast.error('Có lỗi xảy ra khi lưu vào Database!')
  }
}

// Formatters
const truncateText = (text, maxLength) => text?.length > maxLength ? text.substring(0, maxLength) + '...' : text
const formatDate = (dateString) => {
  if(!dateString) return ''
  return new Date(dateString).toLocaleString('vi-VN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}
const formatType = (type) => {
  const map = { 'BUG': 'Báo Lỗi', 'REPORT': 'Vi Phạm', 'FEEDBACK': 'Góp Ý' }
  return map[type] || type
}
const formatStatus = (status) => {
  const map = { 0: 'Chờ duyệt', 1: 'Đang xử lý', 2: 'Đã giải quyết', 3: 'Từ chối' }
  return map[status] || 'Unknown'
}

onMounted(() => { fetchTickets() })
</script>
<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

.ticket-sovereign-wrapper {
  padding: 30px 40px;
  font-family: 'Inter', sans-serif;
  color: #1a110d;
  min-height: calc(100vh - 90px);
  background: #fafaf9; /* Nền xám tro rất nhạt tạo cảm giác sạch sẽ */
}

/* HEADER */
.page-header-lux {
  display: flex; justify-content: space-between; align-items: flex-end;
  margin-bottom: 30px;
}
.page-title { font-size: 1.8rem; font-weight: 800; margin: 0 0 5px 0; color: #1a110d; letter-spacing: -0.5px; }
.page-subtitle { color: #64748b; margin: 0; font-size: 0.95rem; }

.btn-export {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 18px; border-radius: 10px;
  background: white; border: 1px solid #e2e8f0;
  color: #1a110d; font-weight: 600; cursor: pointer;
  transition: all 0.2s; box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}
.btn-export:hover { border-color: #cbd5e1; box-shadow: 0 4px 10px rgba(0,0,0,0.05); transform: translateY(-2px); }

/* STATS GRID */
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 30px; }
.stat-card {
  background: white; padding: 20px; border-radius: 16px;
  display: flex; align-items: center; gap: 18px;
  border: 1px solid rgba(0,0,0,0.03);
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
}
.icon-wrap { width: 52px; height: 52px; border-radius: 14px; display: flex; align-items: center; justify-content: center; }
.icon-wrap.all { background: #f1f5f9; color: #475569; }
.icon-wrap.bug { background: #fff7ed; color: #ea580c; }
.icon-wrap.report { background: #fef2f2; color: #e11d48; }
.icon-wrap.feedback { background: #eff6ff; color: #2563eb; }

.stat-info .label { display: block; font-size: 0.85rem; color: #64748b; font-weight: 600; margin-bottom: 4px; }
.stat-info .value { margin: 0; font-size: 1.6rem; font-weight: 800; color: #0f172a; }

/* DATA TABLE AREA */
.data-engine-lux {
  background: white; border-radius: 16px;
  border: 1px solid rgba(0,0,0,0.04);
  box-shadow: 0 15px 35px rgba(0,0,0,0.02);
  overflow: hidden;
}

.filter-bar {
  padding: 16px 24px; border-bottom: 1px solid #f1f5f9;
  display: flex; justify-content: space-between; align-items: center;
  background: #fff;
}
.tabs-lux { display: flex; gap: 10px; }
.tab-btn {
  padding: 8px 16px; border: none; background: transparent;
  color: #64748b; font-weight: 600; font-size: 0.9rem; border-radius: 8px;
  cursor: pointer; transition: 0.2s;
}
.tab-btn:hover { color: #1a110d; background: #f8fafc; }
.tab-btn.active { background: #1a110d; color: white; }

.select-lux {
  padding: 9px 16px; border-radius: 10px; border: 1px solid #e2e8f0;
  color: #1a110d; font-weight: 600; outline: none; cursor: pointer;
}

/* TABLE STYLES */
.table-responsive { width: 100%; overflow-x: auto; }
.lux-table { width: 100%; border-collapse: collapse; }
.lux-table th {
  padding: 14px 24px; text-align: left; background: #fafaf9;
  font-size: 0.75rem; text-transform: uppercase; letter-spacing: 1px;
  color: #64748b; font-weight: 700; border-bottom: 1px solid #f1f5f9;
}
.lux-table td { padding: 16px 24px; border-bottom: 1px solid #f1f5f9; font-size: 0.9rem; vertical-align: middle; }

.lux-row { cursor: pointer; transition: 0.2s; }
.lux-row:hover { background: #f8fafc; }

.id-col { font-weight: 700; color: #94a3b8; }
.user-name { font-weight: 600; color: #0f172a; }
.title-col { font-weight: 600; color: #1a110d; }
.time-col { color: #64748b; font-size: 0.85rem; }
.text-right { text-align: right !important; }

/* BADGES */
.badge-type { padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 700; }
.badge-type.bug { background: #fff7ed; color: #ea580c; border: 1px solid #ffedd5; }
.badge-type.report { background: #fef2f2; color: #e11d48; border: 1px solid #ffe4e6; }
.badge-type.feedback { background: #eff6ff; color: #2563eb; border: 1px solid #dbeafe; }

.badge-status { display: inline-flex; align-items: center; gap: 6px; padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; }
.badge-status::before { content: ''; width: 6px; height: 6px; border-radius: 50%; }
.status-0 { background: #f1f5f9; color: #475569; } .status-0::before { background: #94a3b8; } /* Pending */
.status-1 { background: #eff6ff; color: #2563eb; } .status-1::before { background: #3b82f6; } /* Processing */
.status-2 { background: #f0fdf4; color: #16a34a; } .status-2::before { background: #22c55e; } /* Resolved */
.status-3 { background: #fef2f2; color: #e11d48; } .status-3::before { background: #ef4444; } /* Rejected */

.btn-action {
  padding: 6px 14px; background: white; border: 1px solid #e2e8f0;
  border-radius: 6px; font-weight: 600; color: #1a110d; cursor: pointer; transition: 0.2s;
}
.lux-row:hover .btn-action { border-color: #cbd5e1; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }

/* MODAL LUXURY */
.modal-glass-backdrop {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(15, 23, 42, 0.6); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center; z-index: 1000;
}
.modal-lux-content {
  background: white; width: 650px; max-width: 90%; border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0,0,0,0.25); overflow: hidden;
  display: flex; flex-direction: column; max-height: 85vh;
}
.modal-header {
  padding: 20px 24px; border-bottom: 1px solid #f1f5f9;
  display: flex; justify-content: space-between; align-items: center;
  background: #fafaf9;
}
.header-tags { display: flex; gap: 10px; }
.btn-close { background: none; border: none; color: #94a3b8; cursor: pointer; transition: 0.2s; }
.btn-close:hover { color: #0f172a; transform: rotate(90deg); }

.modal-body { padding: 24px; overflow-y: auto; }
.ticket-title { margin: 0 0 20px 0; font-size: 1.4rem; font-weight: 800; color: #1a110d; line-height: 1.3; }

.ticket-meta { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 25px; padding: 15px; background: #f8fafc; border-radius: 12px; }
.meta-item label { display: block; font-size: 0.75rem; color: #64748b; font-weight: 700; text-transform: uppercase; margin-bottom: 4px; }
.meta-item span { font-weight: 600; color: #0f172a; }
.link-post { color: #ea580c; font-weight: 700; text-decoration: none; }
.link-post:hover { text-decoration: underline; }

.ticket-description label, .ticket-attachment label {
  display: block; font-size: 0.85rem; font-weight: 700; color: #475569; margin-bottom: 10px;
}
.desc-box { padding: 16px; background: white; border: 1px solid #e2e8f0; border-radius: 12px; color: #334155; line-height: 1.6; font-size: 0.95rem; margin-bottom: 25px; }

.img-wrapper img { width: 100%; max-height: 300px; object-fit: contain; border-radius: 12px; border: 1px solid #e2e8f0; background: #f8fafc; }

.modal-footer {
  padding: 16px 24px; border-top: 1px solid #f1f5f9; background: #fafaf9;
  display: flex; justify-content: space-between; align-items: center;
}
.resolved-time { font-size: 0.85rem; color: #16a34a; font-weight: 600; }
.footer-actions { display: flex; gap: 10px; }
.btn-lux { padding: 10px 18px; border-radius: 10px; font-weight: 700; cursor: pointer; border: none; transition: 0.2s; }
.btn-lux.process { background: #eff6ff; color: #2563eb; } .btn-lux.process:hover { background: #dbeafe; }
.btn-lux.reject { background: #fef2f2; color: #e11d48; } .btn-lux.reject:hover { background: #ffe4e6; }
.btn-lux.resolve { background: #1a110d; color: white; } .btn-lux.resolve:hover { background: #ea580c; box-shadow: 0 4px 15px rgba(234,88,12,0.3); }

/* MISC */
.empty-state { text-align: center; padding: 60px 20px; }
.empty-icon { font-size: 3rem; margin-bottom: 10px; opacity: 0.5; }
.spinner-lux { width: 30px; height: 30px; border: 3px solid #f1f5f9; border-top-color: #ea580c; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto 15px; }
@keyframes spin { 100% { transform: rotate(360deg); } }

/* TRANSITIONS */
.fade-glass-enter-active, .fade-glass-leave-active { transition: opacity 0.3s ease; }
.fade-glass-enter-from, .fade-glass-leave-to { opacity: 0; }
.fade-glass-enter-active .modal-lux-content { animation: slideUp 0.3s cubic-bezier(0.19, 1, 0.22, 1); }
@keyframes slideUp { from { transform: translateY(30px) scale(0.95); opacity: 0; } to { transform: translateY(0) scale(1); opacity: 1; } }

.custom-scroll::-webkit-scrollbar { width: 6px; }
.custom-scroll::-webkit-scrollbar-track { background: transparent; }
.custom-scroll::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
</style>