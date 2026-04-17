<template>
  <div class="page-container animate-enter">
    
    <div class="page-header">
      <div class="title-group">
        <h2 class="title">Quản Lý Giao Dịch</h2>
        <p class="subtitle">Lịch sử thanh toán và hóa đơn điện tử</p>
      </div>
      <div class="header-actions">
        <button class="btn-export" @click="exportToExcel" title="Tải xuống file Excel">
          <i class="fa-solid fa-file-excel"></i> Xuất Excel
        </button>
        <button class="btn-refresh" @click="fetchTransactions" :disabled="isLoading">
          <i class="fa-solid fa-rotate-right" :class="{ 'fa-spin': isLoading }"></i> Làm mới
        </button>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card stat-revenue">
        <div class="stat-icon bg-emerald-light"><i class="fa-solid fa-sack-dollar text-emerald"></i></div>
        <div class="stat-info">
          <span class="stat-value text-emerald">{{ formatCurrency(totalRevenue) }}</span>
          <span class="stat-label">Tổng doanh thu thực tế</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-blue-light"><i class="fa-solid fa-money-bill-transfer text-blue"></i></div>
        <div class="stat-info">
          <span class="stat-value">{{ transactions.length }}</span>
          <span class="stat-label">Tổng số giao dịch</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-red-light"><i class="fa-solid fa-triangle-exclamation text-red"></i></div>
        <div class="stat-info">
          <span class="stat-value">{{ failedCount }}</span>
          <span class="stat-label">Giao dịch bị hủy/lỗi</span>
        </div>
      </div>
    </div>

    <div class="filter-bar">
      <div class="tabs">
        <button v-for="tab in filterTabs" :key="tab.value" 
                :class="['filter-tab', currentFilter === tab.value ? 'active' : '']"
                @click="currentFilter = tab.value">
          {{ tab.label }}
        </button>
      </div>
      
      <div class="search-box">
        <i class="fa-solid fa-search search-icon"></i>
        <input v-model="searchQuery" type="text" placeholder="Tìm theo mã giao dịch, email..." class="search-input" />
        <button v-if="searchQuery" @click="searchQuery = ''" class="clear-search">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>
    </div>

    <div class="table-wrapper">
      <div v-if="isLoading" class="loading-state">
        <div class="spinner-modern"></div>
        <span>Đang tải dữ liệu từ máy chủ...</span>
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th width="15%">MÃ GIAO DỊCH</th>
            <th width="25%">KHÁCH HÀNG</th>
            <th width="15%">SỐ TIỀN</th>
            <th width="18%">GÓI ĐĂNG KÝ</th>
            <th width="15%">TRẠNG THÁI</th>
            <th width="12%" class="text-right">CHỨNG TỪ</th>
          </tr>
        </thead>
        <TransitionGroup tag="tbody" name="list">
          <tr v-for="txn in filteredTransactions" :key="txn.transactionID" class="table-row">
            <td>
              <span class="txn-code">{{ txn.orderCode }}</span>
              <div class="txn-time">{{ formatDate(txn.createdAt) }}</div>
            </td>
            <td>
              <div class="user-cell">
                <img :src="txn.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(txn.username || 'U')}&background=f8fafc&color=0f172a`" class="avatar">
                <div class="u-info">
                  <span class="name">{{ txn.username }}</span>
                  <span class="sub-name">{{ txn.email }}</span>
                </div>
              </div>
            </td>
            <td>
              <strong class="amount-text" :class="getAmountColor(txn.status)">
                {{ txn.status === 'PAID' ? '+' : '' }}{{ formatCurrency(txn.amount) }}
              </strong>
            </td>
            <td>
              <div class="payment-method">
                <div class="method-badge">{{ txn.planName }}</div>
              </div>
            </td>
            <td>
              <div class="status-pill" :class="getStatusClass(txn.status)">
                <span class="status-dot"></span> {{ getStatusLabel(txn.status) }}
              </div>
            </td>
            <td>
              <div class="actions">
                <button @click="openReceipt(txn)" class="btn-action view" title="Xem Biên Lai">
                  <i class="fa-solid fa-file-invoice"></i> Chi tiết
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="filteredTransactions.length === 0">
            <td colspan="6" class="empty-state">
              <div class="empty-icon"><i class="fa-solid fa-box-open"></i></div>
              <p>Không có dữ liệu giao dịch nào.</p>
            </td>
          </tr>
        </TransitionGroup>
      </table>
    </div>

    <Transition name="modal-fade">
      <div v-if="receiptModal.show" class="modal-overlay" @click.self="receiptModal.show = false">
        <div class="receipt-card">
          <button class="detail-close" @click="receiptModal.show = false"><i class="fa-solid fa-xmark"></i></button>

          <div class="receipt-top">
            <div class="r-logo-wrap">
              <div class="r-logo-circle"><i class="fa-solid fa-check"></i></div>
              <h3 class="r-title">Giao dịch thành công</h3>
              <span class="r-time">{{ formatDate(receiptModal.txn.paidAt || receiptModal.txn.createdAt) }}</span>
            </div>
            <div class="r-amount-big">
              {{ formatCurrency(receiptModal.txn.amount) }}
            </div>
            <div class="r-plan-name">{{ receiptModal.txn.planName }}</div>
          </div>

          <div class="r-divider"></div>

          <div class="receipt-body">
            <div class="r-info-row">
              <span class="r-label">Mã giao dịch</span>
              <span class="r-value monospace">{{ receiptModal.txn.orderCode }}</span>
            </div>
            <div class="r-info-row">
              <span class="r-label">Người chuyển</span>
              <span class="r-value">{{ receiptModal.txn.username }}</span>
            </div>
            <div class="r-info-row">
              <span class="r-label">Tài khoản</span>
              <span class="r-value">{{ receiptModal.txn.email }}</span>
            </div>
            <div class="r-info-row">
              <span class="r-label">Trạng thái</span>
              <span class="r-value" :class="getAmountColor(receiptModal.txn.status)">{{ getStatusLabel(receiptModal.txn.status) }}</span>
            </div>
            <div class="r-info-row">
              <span class="r-label">Nội dung</span>
              <span class="r-value">Thanh toán dịch vụ GoMet Premium</span>
            </div>
          </div>

        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'
import { toast } from '@/composables/useToast'
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'

// --- STATE ---
const transactions = ref([])
const isLoading = ref(true)
const searchQuery = ref('')
const currentFilter = ref('ALL')

const filterTabs = [
  { label: 'Tất cả', value: 'ALL' },
  { label: 'Đã thu tiền', value: 'PAID' },
  { label: 'Đang chờ', value: 'PENDING' },
  { label: 'Đã hủy', value: 'FAILED' }
]

const receiptModal = ref({ show: false, txn: null })

// --- FORMATTERS ---
const formatCurrency = (val) => {
  if (!val && val !== 0) return '0 VNĐ'
  return new Intl.NumberFormat('vi-VN').format(val) + ' VNĐ'
}

const formatDate = (dateString) => {
  if (!dateString) return '—'
  const d = new Date(dateString)
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

// --- UI HELPERS ---
const getStatusLabel = (status) => {
  const map = { 'PAID': 'Thành công', 'PENDING': 'Chờ xử lý', 'FAILED': 'Thất bại' }
  return map[status] || status
}

const getStatusClass = (status) => {
  const map = { 'PAID': 'active', 'PENDING': 'warning', 'FAILED': 'banned' }
  return map[status] || 'banned'
}

const getAmountColor = (status) => {
  if (status === 'PAID') return 'text-emerald font-bold'
  if (status === 'FAILED') return 'text-gray-400 line-through'
  return 'text-gray-800 font-bold'
}

// --- COMPUTED ---
const totalRevenue = computed(() => {
  return transactions.value
    .filter(t => t.status === 'PAID')
    .reduce((sum, t) => sum + (t.amount || 0), 0)
})

const failedCount = computed(() => transactions.value.filter(t => t.status === 'FAILED').length)

const filteredTransactions = computed(() => {
  let result = transactions.value

  if (currentFilter.value !== 'ALL') {
    result = result.filter(t => t.status === currentFilter.value)
  }

  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(t => 
      String(t.orderCode).toLowerCase().includes(q) ||
      t.username?.toLowerCase().includes(q) ||
      t.email?.toLowerCase().includes(q)
    )
  }
  return result
})

// --- API ACTIONS ---
const fetchTransactions = async () => {
  isLoading.value = true
  try {
const res = await api.get('/api/admin/transactions')
    transactions.value = res.data.map(t => ({
      ...t,
      status: t.status ? String(t.status).toUpperCase() : 'PENDING'
    }))
  } catch (err) {
    toast.error('Không thể kết nối đến máy chủ dữ liệu!')
  } finally {
    isLoading.value = false
  }
}

const openReceipt = (txn) => {
  receiptModal.value = { show: true, txn }
}

// --- XUẤT EXCEL (REAL) ---
const exportToExcel = () => {
  if (filteredTransactions.value.length === 0) {
    toast.warn('Không có dữ liệu để xuất!')
    return
  }

  // 1. Chẩn bị dữ liệu để xuất
  const dataToExport = filteredTransactions.value.map((txn, index) => ({
    'STT': index + 1,
    'Mã Giao Dịch': txn.orderCode,
    'Khách Hàng': txn.username,
    'Email': txn.email,
    'Gói Dịch Vụ': txn.planName,
    'Số Tiền (VNĐ)': txn.amount,
    'Trạng Thái': getStatusLabel(txn.status),
    'Ngày Tạo': formatDate(txn.createdAt),
    'Ngày Thanh Toán': formatDate(txn.paidAt)
  }))

  // 2. Tạo file Excel
  const worksheet = XLSX.utils.json_to_sheet(dataToExport)
  
  // Tự động căn chỉnh độ rộng cột
  const wscols = [ {wch:5}, {wch:20}, {wch:25}, {wch:30}, {wch:20}, {wch:15}, {wch:15}, {wch:20}, {wch:20} ]
  worksheet['!cols'] = wscols

  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, 'Sao_Ke_Giao_Dich')

  // 3. Tải xuống
  const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' })
  const dataBlob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' })
  saveAs(dataBlob, `GOMET_SAOKE_${new Date().getTime()}.xlsx`)
  
  toast.success('Đã tải xuống file báo cáo Excel!')
}

onMounted(fetchTransactions)
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=JetBrains+Mono:wght@500;700&display=swap');

.page-container { padding: 32px; font-family: 'Inter', sans-serif; background: #f8fafc; min-height: 100vh; color: #0f172a; }

/* ── HEADER ── */
.page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 24px; }
.title { font-size: 1.5rem; font-weight: 700; color: #0f172a; margin: 0 0 4px; }
.subtitle { color: #64748b; margin: 0; font-size: 0.9rem; }
.header-actions { display: flex; gap: 12px; }
.btn-refresh { background: white; border: 1px solid #e2e8f0; padding: 8px 16px; border-radius: 8px; font-weight: 600; font-size: 0.85rem; color: #475569; cursor: pointer; transition: 0.2s; display: flex; align-items: center; gap: 8px; }
.btn-refresh:hover:not(:disabled) { background: #f1f5f9; color: #0f172a; }
.btn-export { background: #16a34a; border: 1px solid #15803d; padding: 8px 16px; border-radius: 8px; font-weight: 600; font-size: 0.85rem; color: white; cursor: pointer; transition: 0.2s; display: flex; align-items: center; gap: 8px; }
.btn-export:hover { background: #15803d; }

/* ── THỐNG KÊ ── */
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 24px; }
.stat-card { background: white; padding: 20px; border-radius: 12px; display: flex; align-items: center; gap: 16px; border: 1px solid #e2e8f0; }
.stat-revenue { border: 1px solid #a7f3d0; }
.stat-icon { width: 48px; height: 48px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 1.3rem; }
.bg-blue-light { background: #eff6ff; } .text-blue { color: #3b82f6; }
.bg-emerald-light { background: #d1fae5; } .text-emerald { color: #10b981; }
.bg-red-light { background: #fef2f2; } .text-red { color: #ef4444; }
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 1.5rem; font-weight: 700; color: #0f172a; line-height: 1.2; }
.stat-label { font-size: 0.85rem; color: #64748b; font-weight: 500; }

/* ── BỘ LỌC ── */
.filter-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.tabs { display: flex; gap: 4px; background: #e2e8f0; padding: 4px; border-radius: 8px; }
.filter-tab { background: transparent; border: none; padding: 6px 16px; border-radius: 6px; font-weight: 600; color: #475569; cursor: pointer; transition: 0.2s; font-size: 0.85rem; }
.filter-tab.active { background: white; color: #0f172a; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }

.search-box { position: relative; display: flex; align-items: center; width: 280px; }
.search-icon { position: absolute; left: 12px; color: #94a3b8; font-size: 0.9rem; }
.search-input { width: 100%; padding: 8px 32px 8px 36px; border: 1px solid #cbd5e1; border-radius: 8px; outline: none; font-size: 0.9rem; font-family: inherit; }
.search-input:focus { border-color: #3b82f6; }
.clear-search { position: absolute; right: 8px; background: none; border: none; color: #94a3b8; cursor: pointer; }

/* ── BẢNG GIAO DỊCH ── */
.table-wrapper { background: white; border-radius: 12px; border: 1px solid #e2e8f0; overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { background: #f8fafc; padding: 12px 20px; text-align: left; font-size: 0.75rem; font-weight: 600; color: #475569; text-transform: uppercase; border-bottom: 1px solid #e2e8f0; }
.data-table td { padding: 12px 20px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.table-row:hover { background: #f8fafc; }

.txn-code { font-family: 'JetBrains Mono', monospace; font-weight: 600; color: #0f172a; font-size: 0.9rem; }
.txn-time { font-size: 0.8rem; color: #64748b; margin-top: 4px; }

.user-cell { display: flex; align-items: center; gap: 12px; }
.avatar { width: 36px; height: 36px; border-radius: 50%; border: 1px solid #e2e8f0; object-fit: cover; }
.u-info { display: flex; flex-direction: column; }
.name { font-weight: 600; font-size: 0.9rem; color: #0f172a; }
.sub-name { font-size: 0.8rem; color: #64748b; }

.amount-text { font-size: 1rem; }
.text-emerald { color: #16a34a; }
.text-gray-800 { color: #1e293b; }
.line-through { text-decoration: line-through; opacity: 0.5; }

.method-badge { display: inline-block; padding: 4px 10px; border-radius: 6px; font-size: 0.8rem; font-weight: 600; background: #f1f5f9; color: #475569; border: 1px solid #e2e8f0; }

.status-pill { display: inline-flex; align-items: center; gap: 6px; padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 600; }
.status-dot { width: 6px; height: 6px; border-radius: 50%; }
.status-pill.active { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; } .status-pill.active .status-dot { background: #16a34a; }
.status-pill.banned { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; } .status-pill.banned .status-dot { background: #dc2626; }
.status-pill.warning { background: #fffbeb; color: #d97706; border: 1px solid #fde68a; } .status-pill.warning .status-dot { background: #f59e0b; }

.actions { display: flex; justify-content: flex-end; }
.btn-action { padding: 6px 12px; border-radius: 6px; border: 1px solid #e2e8f0; cursor: pointer; display: flex; align-items: center; gap: 6px; font-size: 0.85rem; background: white; color: #475569; font-weight: 500; }
.btn-action:hover { background: #f1f5f9; color: #0f172a; }

/* ── RECEIPT MODAL (THỰC TẾ NHƯ APP NGÂN HÀNG) ── */
.modal-overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.6); display: flex; align-items: center; justify-content: center; z-index: 9999; backdrop-filter: blur(2px); }
.receipt-card { background: white; width: 380px; border-radius: 16px; position: relative; box-shadow: 0 20px 40px rgba(0,0,0,0.2); padding: 32px 24px; }
.detail-close { position: absolute; top: 12px; right: 12px; width: 28px; height: 28px; border-radius: 6px; background: transparent; color: #94a3b8; border: none; cursor: pointer; font-size: 1.2rem; }
.detail-close:hover { color: #0f172a; background: #f1f5f9; }

.receipt-top { text-align: center; margin-bottom: 20px; }
.r-logo-wrap { display: flex; flex-direction: column; align-items: center; gap: 8px; margin-bottom: 16px; }
.r-logo-circle { width: 48px; height: 48px; border-radius: 50%; background: #16a34a; color: white; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }
.r-title { font-size: 1.2rem; color: #0f172a; margin: 0; font-weight: 700; }
.r-time { font-size: 0.85rem; color: #64748b; }
.r-amount-big { font-size: 2.2rem; font-weight: 800; color: #0f172a; margin-bottom: 4px; }
.r-plan-name { font-size: 0.9rem; color: #16a34a; font-weight: 600; background: #f0fdf4; display: inline-block; padding: 4px 12px; border-radius: 20px; }

.r-divider { border-top: 1px dashed #cbd5e1; margin: 20px 0; }

.receipt-body { display: flex; flex-direction: column; gap: 12px; }
.r-info-row { display: flex; justify-content: space-between; align-items: flex-start; font-size: 0.9rem; }
.r-label { color: #64748b; }
.r-value { color: #0f172a; font-weight: 500; text-align: right; max-width: 60%; }
.monospace { font-family: 'JetBrains Mono', monospace; font-size: 0.85rem; color: #0f172a; }

</style>