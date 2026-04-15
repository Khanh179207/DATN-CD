<template>
  <div class="page-container animate-enter">
    
    <div class="page-header">
      <div class="title-group">
        <h2 class="title">{{ t('admin.transactions.title') }}</h2>
        <p class="subtitle">{{ t('admin.transactions.subtitle') }}</p>
      </div>
      <div class="header-actions">
        <button class="btn-export" @click="exportToExcel" :title="t('admin.transactions.export_excel')">
          <i class="fa-solid fa-file-excel"></i> {{ t('admin.transactions.export_excel') }}
        </button>
        <button class="btn-refresh" @click="fetchTransactions" :disabled="isLoading">
          <i class="fa-solid fa-rotate-right" :class="{ 'fa-spin': isLoading }"></i> {{ t('admin.transactions.refresh') }}
        </button>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card stat-revenue">
        <div class="stat-icon bg-emerald-light"><i class="fa-solid fa-sack-dollar text-emerald"></i></div>
        <div class="stat-info">
          <span class="stat-value text-emerald">{{ formatCurrency(totalRevenue) }}</span>
          <span class="stat-label">{{ t('admin.transactions.revenue') }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-blue-light"><i class="fa-solid fa-money-bill-transfer text-blue"></i></div>
        <div class="stat-info">
          <span class="stat-value">{{ transactions.length }}</span>
          <span class="stat-label">{{ t('admin.transactions.total_transactions') }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-red-light"><i class="fa-solid fa-triangle-exclamation text-red"></i></div>
        <div class="stat-info">
          <span class="stat-value">{{ failedCount }}</span>
          <span class="stat-label">{{ t('admin.transactions.failed_transactions') }}</span>
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
        <input v-model="searchQuery" type="text" :placeholder="t('admin.common.search_transactions')" class="search-input" />
        <button v-if="searchQuery" @click="searchQuery = ''" class="clear-search">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>
    </div>

    <div class="table-wrapper">
      <div v-if="isLoading" class="loading-state">
        <div class="spinner-modern"></div>
        <span>{{ t('admin.common.loading_data') }}</span>
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th width="15%">{{ t('admin.transactions.columns.code') }}</th>
            <th width="25%">{{ t('admin.transactions.columns.customer') }}</th>
            <th width="15%">{{ t('admin.transactions.columns.amount') }}</th>
            <th width="18%">{{ t('admin.transactions.columns.plan') }}</th>
            <th width="15%">{{ t('admin.transactions.columns.status') }}</th>
            <th width="12%" class="text-right">{{ t('admin.transactions.columns.receipt') }}</th>
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
                <button @click="openReceipt(txn)" class="btn-action view" :title="t('admin.transactions.view_receipt')">
                  <i class="fa-solid fa-file-invoice"></i> {{ t('admin.transactions.detail') }}
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="filteredTransactions.length === 0">
            <td colspan="6" class="empty-state">
              <div class="empty-icon"><i class="fa-solid fa-box-open"></i></div>
              <p>{{ t('admin.transactions.empty') }}</p>
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
              <h3 class="r-title">{{ getStatusLabel(receiptModal.txn.status) }}</h3>
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
              <span class="r-label">{{ t('admin.transactions.receipt.code') }}</span>
              <span class="r-value monospace">{{ receiptModal.txn.orderCode }}</span>
            </div>
            <div class="r-info-row">
              <span class="r-label">{{ t('admin.transactions.receipt.sender') }}</span>
              <span class="r-value">{{ receiptModal.txn.username }}</span>
            </div>
            <div class="r-info-row">
              <span class="r-label">{{ t('admin.transactions.receipt.account') }}</span>
              <span class="r-value">{{ receiptModal.txn.email }}</span>
            </div>
            <div class="r-info-row">
              <span class="r-label">{{ t('admin.transactions.columns.status') }}</span>
              <span class="r-value" :class="getAmountColor(receiptModal.txn.status)">{{ getStatusLabel(receiptModal.txn.status) }}</span>
            </div>
            <div class="r-info-row">
              <span class="r-label">{{ t('admin.transactions.receipt.content') }}</span>
              <span class="r-value">{{ t('admin.transactions.receipt.premium_payment') }}</span>
            </div>
          </div>

          <div class="r-divider"></div>

          <div class="receipt-footer">
            <img :src="`https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=${receiptModal.txn.orderCode}`" :alt="t('admin.transactions.receipt.qr_alt')" class="qr-code">
            <div class="r-footer-text">
              <strong>{{ t('admin.transactions.receipt.company_name') }}</strong>
              <p>{{ t('admin.transactions.receipt.qr_hint') }}</p>
            </div>
          </div>

          <button class="btn-print" @click="printReceipt">
            <i class="fa-solid fa-print"></i> {{ t('admin.transactions.print_invoice') }}
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import api from '@/services/api'
import { getDateLocale } from '@/i18n'
import { toast } from '@/composables/useToast'
import ExcelJS from 'exceljs'

const { t, locale } = useI18n()

const transactions = ref([])
const isLoading = ref(true)
const searchQuery = ref('')
const currentFilter = ref('ALL')
const dateLocale = computed(() => getDateLocale(locale.value))

const filterTabs = computed(() => [
  { label: t('admin.common.all'), value: 'ALL' },
  { label: t('admin.transactions.filter_paid'), value: 'PAID' },
  { label: t('admin.transactions.filter_pending'), value: 'PENDING' },
  { label: t('admin.transactions.filter_cancelled'), value: 'CANCELLED' },
])

const receiptModal = ref({ show: false, txn: null })

const formatCurrency = (val) => {
  if (!val && val !== 0) return `0 ${t('admin.transactions.currency')}`
  return `${new Intl.NumberFormat(dateLocale.value).format(val)} ${t('admin.transactions.currency')}`
}

const formatDate = (dateString) => {
  if (!dateString) return '—'
  const d = new Date(dateString)
  return d.toLocaleString(dateLocale.value, { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const normalizeStatus = (status) => {
  const normalized = String(status || 'PENDING').toUpperCase()

  if (normalized === 'FAILED' || normalized === 'ERROR') {
    return 'CANCELLED'
  }

  return normalized
}

const getStatusLabel = (status) => {
  const normalized = normalizeStatus(status)
  const map = {
    PAID: t('admin.transactions.status.paid'),
    PENDING: t('admin.transactions.status.pending'),
    CANCELLED: t('admin.transactions.status.cancelled'),
  }
  return map[normalized] || normalized
}

const getStatusClass = (status) => {
  const map = { PAID: 'active', PENDING: 'warning', CANCELLED: 'banned' }
  return map[normalizeStatus(status)] || 'banned'
}

const getAmountColor = (status) => {
  const normalized = normalizeStatus(status)
  if (normalized === 'PAID') return 'text-emerald font-bold'
  if (normalized === 'CANCELLED') return 'text-gray-400 line-through'
  return 'text-gray-800 font-bold'
}

const totalRevenue = computed(() => {
  return transactions.value
    .filter(t => t.status === 'PAID')
    .reduce((sum, t) => sum + (t.amount || 0), 0)
})

const failedCount = computed(() => transactions.value.filter(t => t.status === 'CANCELLED').length)
const pendingCount = computed(() => transactions.value.filter(t => t.status === 'PENDING').length)
const paidCount = computed(() => transactions.value.filter(t => t.status === 'PAID').length)

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

const fetchTransactions = async () => {
  isLoading.value = true
  try {
    const res = await api.get('/api/admin/transactions')
    transactions.value = res.data.map(t => ({
      ...t,
      status: normalizeStatus(t.status)
    }))
  } catch (err) {
    toast.error(t('admin.transactions.load_failed'))
  } finally {
    isLoading.value = false
  }
}

const openReceipt = (txn) => {
  receiptModal.value = { show: true, txn }
}

const printReceipt = () => window.print() 

const exportToExcel = async () => {
  if (filteredTransactions.value.length === 0) {
    toast.warn(t('admin.transactions.no_export_data'))
    return
  }

  try {
    const workbook = new ExcelJS.Workbook()
    workbook.creator = 'GoMet'
    workbook.lastModifiedBy = 'GoMet'
    workbook.created = new Date()
    workbook.modified = new Date()

    const overviewSheet = workbook.addWorksheet(t('admin.transactions.export.overview_sheet_name'), {
      views: [{ state: 'frozen', ySplit: 8 }],
      properties: { defaultRowHeight: 22 }
    })

    const sheet = workbook.addWorksheet(t('admin.transactions.export.sheet_name'), {
      views: [{ state: 'frozen', ySplit: 11 }],
      properties: { defaultRowHeight: 22 }
    })

    overviewSheet.columns = [
      { width: 18 },
      { width: 20 },
      { width: 18 },
      { width: 20 },
      { width: 18 },
      { width: 20 },
    ]

    overviewSheet.mergeCells('A1:F1')
    overviewSheet.getCell('A1').value = t('admin.transactions.export.report_title')
    overviewSheet.getCell('A1').font = { bold: true, size: 20, color: { argb: 'FFFFFFFF' } }
    overviewSheet.getCell('A1').alignment = { horizontal: 'center', vertical: 'middle' }
    overviewSheet.getCell('A1').fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0F172A' } }
    overviewSheet.getRow(1).height = 30

    overviewSheet.mergeCells('A2:F2')
    overviewSheet.getCell('A2').value = t('admin.transactions.export.report_subtitle')
    overviewSheet.getCell('A2').alignment = { horizontal: 'center' }
    overviewSheet.getCell('A2').font = { size: 11, color: { argb: 'FF475569' } }

    overviewSheet.mergeCells('A4:F4')
    overviewSheet.getCell('A4').value = `${t('admin.transactions.export.generated_at')}: ${formatDate(new Date())}`
    overviewSheet.getCell('A4').font = { italic: true, size: 10, color: { argb: 'FF64748B' } }

    overviewSheet.mergeCells('A5:F5')
    overviewSheet.getCell('A5').value = `${t('admin.transactions.export.active_filter')}: ${currentFilter.value === 'ALL' ? t('admin.transactions.export.filter_all') : getStatusLabel(currentFilter.value)}`
    overviewSheet.getCell('A5').font = { size: 10, color: { argb: 'FF64748B' } }

    const overviewRanges = ['A7:B7','A8:B8','C7:D7','C8:D8','E7:F7','E8:F8','E10:F10','E11:F11']
    overviewRanges.forEach(range => overviewSheet.mergeCells(range))

    const overviewBlocks = [
      { labelCell: 'A7', valueCell: 'A8', label: t('admin.transactions.revenue'), value: formatCurrency(totalRevenue.value), fill: 'FFF0FDF4', text: 'FF166534' },
      { labelCell: 'C7', valueCell: 'C8', label: t('admin.transactions.total_transactions'), value: filteredTransactions.value.length, fill: 'FFEFF6FF', text: 'FF1D4ED8' },
      { labelCell: 'E7', valueCell: 'E8', label: t('admin.transactions.failed_transactions'), value: failedCount.value, fill: 'FFFEF2F2', text: 'FFDC2626' },
      { labelCell: 'E10', valueCell: 'E11', label: t('admin.transactions.export.summary_pending'), value: pendingCount.value, fill: 'FFFFFBEB', text: 'FFD97706' },
    ]

    overviewBlocks.forEach((block) => {
      const labelCell = overviewSheet.getCell(block.labelCell)
      const valueCell = overviewSheet.getCell(block.valueCell)
      labelCell.value = block.label
      valueCell.value = block.value
      labelCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: block.fill } }
      valueCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: block.fill } }
      labelCell.font = { bold: true, size: 10, color: { argb: block.text } }
      valueCell.font = { bold: true, size: 16, color: { argb: block.text } }
      labelCell.alignment = { horizontal: 'center', vertical: 'middle' }
      valueCell.alignment = { horizontal: 'center', vertical: 'middle' }
    })

    overviewSheet.mergeCells('A13:C13')
    overviewSheet.getCell('A13').value = t('admin.transactions.export.status_breakdown_title')
    overviewSheet.getCell('A13').font = { bold: true, size: 12, color: { argb: 'FF0F172A' } }

    overviewSheet.getRow(14).values = [
      t('admin.transactions.export.metric_label'),
      '',
      t('admin.transactions.export.metric_value')
    ]
    ;['A14','C14'].forEach((address) => {
      const cell = overviewSheet.getCell(address)
      cell.font = { bold: true, color: { argb: 'FFFFFFFF' } }
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFEA580C' } }
      cell.alignment = { horizontal: 'center' }
    })
    overviewSheet.mergeCells('A15:B15')
    overviewSheet.mergeCells('A16:B16')
    overviewSheet.mergeCells('A17:B17')
    overviewSheet.getCell('A15').value = t('admin.transactions.status.paid')
    overviewSheet.getCell('C15').value = paidCount.value
    overviewSheet.getCell('A16').value = t('admin.transactions.status.pending')
    overviewSheet.getCell('C16').value = pendingCount.value
    overviewSheet.getCell('A17').value = t('admin.transactions.status.cancelled')
    overviewSheet.getCell('C17').value = failedCount.value

    overviewSheet.mergeCells('D13:F13')
    overviewSheet.getCell('D13').value = t('admin.transactions.export.plan_breakdown_title')
    overviewSheet.getCell('D13').font = { bold: true, size: 12, color: { argb: 'FF0F172A' } }

    overviewSheet.getRow(14).getCell(4).value = t('admin.transactions.export.metric_label')
    overviewSheet.getRow(14).getCell(6).value = t('admin.transactions.export.metric_value')
    ;['D14','F14'].forEach((address) => {
      const cell = overviewSheet.getCell(address)
      cell.font = { bold: true, color: { argb: 'FFFFFFFF' } }
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0F172A' } }
      cell.alignment = { horizontal: 'center' }
    })
    overviewSheet.mergeCells('D15:E15')
    overviewSheet.mergeCells('D16:E16')
    overviewSheet.mergeCells('D17:E17')

    const topPlans = [...filteredTransactions.value].reduce((acc, txn) => {
      acc[txn.planName] = (acc[txn.planName] || 0) + 1
      return acc
    }, {})
    const planEntries = Object.entries(topPlans).sort((a, b) => b[1] - a[1]).slice(0, 3)
    while (planEntries.length < 3) {
      planEntries.push(['—', 0])
    }
    ;[['D15','F15'], ['D16','F16'], ['D17','F17']].forEach(([labelCell, valueCell], index) => {
      overviewSheet.getCell(labelCell).value = planEntries[index][0]
      overviewSheet.getCell(valueCell).value = planEntries[index][1]
    })

    for (const rowNumber of [15, 16, 17]) {
      ;['A','C','D','F'].forEach((col) => {
        const cell = overviewSheet.getCell(`${col}${rowNumber}`)
        cell.border = {
          top: { style: 'thin', color: { argb: 'FFE2E8F0' } },
          left: { style: 'thin', color: { argb: 'FFE2E8F0' } },
          bottom: { style: 'thin', color: { argb: 'FFE2E8F0' } },
          right: { style: 'thin', color: { argb: 'FFE2E8F0' } },
        }
        cell.alignment = { horizontal: col === 'C' || col === 'F' ? 'center' : 'left', vertical: 'middle' }
      })
    }

    sheet.columns = [
      { width: 8 },
      { width: 22 },
      { width: 24 },
      { width: 30 },
      { width: 22 },
      { width: 16 },
      { width: 18 },
      { width: 22 },
      { width: 22 },
    ]

    sheet.mergeCells('A1:I1')
    sheet.getCell('A1').value = t('admin.transactions.export.report_title')
    sheet.getCell('A1').font = { bold: true, size: 18, color: { argb: 'FFFFFFFF' } }
    sheet.getCell('A1').alignment = { vertical: 'middle', horizontal: 'center' }
    sheet.getCell('A1').fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFEA580C' } }
    sheet.getRow(1).height = 28

    sheet.mergeCells('A2:I2')
    sheet.getCell('A2').value = t('admin.transactions.export.report_subtitle')
    sheet.getCell('A2').font = { size: 11, color: { argb: 'FF475569' } }
    sheet.getCell('A2').alignment = { horizontal: 'center' }

    sheet.mergeCells('A4:I4')
    sheet.getCell('A4').value = `${t('admin.transactions.export.generated_at')}: ${formatDate(new Date())}`
    sheet.getCell('A4').font = { italic: true, size: 10, color: { argb: 'FF64748B' } }

    sheet.mergeCells('A5:I5')
    sheet.getCell('A5').value = `${t('admin.transactions.export.active_filter')}: ${currentFilter.value === 'ALL' ? t('admin.transactions.export.filter_all') : getStatusLabel(currentFilter.value)}`
    sheet.getCell('A5').font = { size: 10, color: { argb: 'FF64748B' } }

    const mergeRanges = ['A6:C6', 'A7:C7', 'D6:F6', 'D7:F7', 'G6:I6', 'G7:I7', 'G8:I8', 'G9:I9']
    mergeRanges.forEach(range => sheet.mergeCells(range))

    const summaryBlocks = [
      { labelCell: 'A6', valueCell: 'A7', label: t('admin.transactions.revenue'), value: formatCurrency(totalRevenue.value), fill: 'FFF0FDF4', text: 'FF166534' },
      { labelCell: 'D6', valueCell: 'D7', label: t('admin.transactions.total_transactions'), value: filteredTransactions.value.length, fill: 'FFEFF6FF', text: 'FF1D4ED8' },
      { labelCell: 'G6', valueCell: 'G7', label: t('admin.transactions.failed_transactions'), value: failedCount.value, fill: 'FFFEF2F2', text: 'FFDC2626' },
      { labelCell: 'G8', valueCell: 'G9', label: t('admin.transactions.export.summary_pending'), value: pendingCount.value, fill: 'FFFFFBEB', text: 'FFD97706' },
    ]

    summaryBlocks.forEach((block) => {
      const labelCell = sheet.getCell(block.labelCell)
      const valueCell = sheet.getCell(block.valueCell)
      labelCell.value = block.label
      valueCell.value = block.value
      labelCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: block.fill } }
      valueCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: block.fill } }
      labelCell.font = { bold: true, size: 10, color: { argb: block.text } }
      valueCell.font = { bold: true, size: 15, color: { argb: block.text } }
      labelCell.alignment = { horizontal: 'center', vertical: 'middle' }
      valueCell.alignment = { horizontal: 'center', vertical: 'middle' }
    })

    const headerRowIndex = 11
    const headerRow = sheet.getRow(headerRowIndex)
    headerRow.values = [
      t('admin.transactions.export.index'),
      t('admin.transactions.export.code'),
      t('admin.transactions.export.customer'),
      t('admin.transactions.export.email'),
      t('admin.transactions.export.plan'),
      t('admin.transactions.export.amount'),
      t('admin.transactions.export.status'),
      t('admin.transactions.export.created_at'),
      t('admin.transactions.export.paid_at'),
    ]
    headerRow.height = 24
    headerRow.eachCell((cell) => {
      cell.font = { bold: true, color: { argb: 'FFFFFFFF' } }
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0F172A' } }
      cell.alignment = { horizontal: 'center', vertical: 'middle' }
      cell.border = {
        top: { style: 'thin', color: { argb: 'FFE2E8F0' } },
        left: { style: 'thin', color: { argb: 'FFE2E8F0' } },
        bottom: { style: 'thin', color: { argb: 'FFE2E8F0' } },
        right: { style: 'thin', color: { argb: 'FFE2E8F0' } },
      }
    })

    filteredTransactions.value.forEach((txn, index) => {
      const row = sheet.addRow([
        index + 1,
        txn.orderCode,
        txn.username,
        txn.email,
        txn.planName,
        txn.amount || 0,
        getStatusLabel(txn.status),
        formatDate(txn.createdAt),
        formatDate(txn.paidAt),
      ])

      row.eachCell((cell) => {
        cell.border = {
          top: { style: 'thin', color: { argb: 'FFF1F5F9' } },
          left: { style: 'thin', color: { argb: 'FFF1F5F9' } },
          bottom: { style: 'thin', color: { argb: 'FFF1F5F9' } },
          right: { style: 'thin', color: { argb: 'FFF1F5F9' } },
        }
        cell.alignment = { vertical: 'middle' }
      })

      if (index % 2 === 0) {
        row.eachCell((cell) => {
          cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFCFCFD' } }
        })
      }

      row.getCell(1).alignment = { horizontal: 'center', vertical: 'middle' }
      row.getCell(6).numFmt = '#,##0 "VND"'
      row.getCell(6).font = { bold: true, color: { argb: txn.status === 'PAID' ? 'FF166534' : txn.status === 'CANCELLED' ? 'FF94A3B8' : 'FF0F172A' } }

      const statusCell = row.getCell(7)
      const statusFill = txn.status === 'PAID' ? 'FFE8FFF3' : txn.status === 'PENDING' ? 'FFFFFBEB' : 'FFFEF2F2'
      const statusText = txn.status === 'PAID' ? 'FF15803D' : txn.status === 'PENDING' ? 'FFD97706' : 'FFDC2626'
      statusCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: statusFill } }
      statusCell.font = { bold: true, color: { argb: statusText } }
      statusCell.alignment = { horizontal: 'center', vertical: 'middle' }
    })

    const totalRow = sheet.addRow([
      '',
      '',
      '',
      t('admin.transactions.export.total_row_label'),
      '',
      filteredTransactions.value
        .filter(txn => txn.status === 'PAID')
        .reduce((sum, txn) => sum + (txn.amount || 0), 0),
      '',
      '',
      '',
    ])
    totalRow.eachCell((cell) => {
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFFF7ED' } }
      cell.font = { bold: true, color: { argb: 'FF9A3412' } }
      cell.border = {
        top: { style: 'thin', color: { argb: 'FFFED7AA' } },
        left: { style: 'thin', color: { argb: 'FFFED7AA' } },
        bottom: { style: 'thin', color: { argb: 'FFFED7AA' } },
        right: { style: 'thin', color: { argb: 'FFFED7AA' } },
      }
    })
    totalRow.getCell(6).numFmt = '#,##0 "VND"'

    sheet.autoFilter = {
      from: { row: headerRowIndex, column: 1 },
      to: { row: headerRowIndex, column: 9 },
    }

    const buffer = await workbook.xlsx.writeBuffer()
    const blob = new Blob([buffer], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${t('admin.transactions.export.file_name')}-${new Date().getTime()}.xlsx`
    link.click()
    URL.revokeObjectURL(url)

    toast.success(t('admin.transactions.export_ok'))
  } catch (error) {
    toast.error(t('admin.transactions.load_failed'))
  }
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

.receipt-footer { display: flex; align-items: center; gap: 16px; background: #f8fafc; padding: 12px; border-radius: 8px; margin-bottom: 20px; }
.qr-code { width: 64px; height: 64px; border-radius: 4px; }
.r-footer-text strong { display: block; color: #0f172a; font-size: 0.9rem; margin-bottom: 4px; }
.r-footer-text p { margin: 0; font-size: 0.75rem; color: #64748b; line-height: 1.4; }

.btn-print { width: 100%; padding: 12px; border-radius: 8px; background: #f1f5f9; color: #0f172a; border: none; font-weight: 600; font-size: 0.95rem; cursor: pointer; transition: 0.2s; display: flex; justify-content: center; align-items: center; gap: 8px; }
.btn-print:hover { background: #e2e8f0; }

/* PRINT STYLES */
@media print {
  body * { visibility: hidden; }
  .receipt-card, .receipt-card * { visibility: visible; }
  .receipt-card { position: absolute; left: 0; top: 0; width: 100%; box-shadow: none; border: none; padding: 0; }
  .detail-close, .btn-print { display: none; }
}
</style>