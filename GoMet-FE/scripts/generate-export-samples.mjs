import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { jsPDF } from 'jspdf'
import ExcelJS from 'exceljs'
import QRCode from 'qrcode'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
const projectRoot = path.resolve(__dirname, '..')
const outputDir = path.join(projectRoot, 'sample-exports')
const fontDir = path.join(projectRoot, 'public', 'fonts')

fs.mkdirSync(outputDir, { recursive: true })

const currency = 'VND'
const formatCurrency = (value) => `${new Intl.NumberFormat('vi-VN').format(value)} ${currency}`
const formatDate = (value) => new Date(value).toLocaleString('vi-VN', {
  day: '2-digit',
  month: '2-digit',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
})

const sampleReceipt = {
  txRef: 'TXN-DEMO-20260415',
  invoiceNumber: 'INV-GM-20260415-01',
  planName: 'Premium (1 năm)',
  accountOwner: 'Nguyen Van A',
  email: 'nguyenvana@gomet.vn',
  status: 'Thành công',
  totalPaid: 499000,
  time: '15/04/2026, 22:10:00',
  paymentMethod: 'Cổng thanh toán VNPAY',
  supportLine: 'support@gomet.vn | Hotline: 1900 6868',
}

const sampleTransactions = [
  {
    stt: 1,
    maGiaoDich: 'GM-20260415-001',
    khachHang: 'Nguyen Van A',
    email: 'nguyenvana@gomet.vn',
    goi: 'Premium 1 thang',
    soTien: 99000,
    trangThai: 'Da thanh toan',
    taoLuc: '15/04/2026, 08:30',
    thanhToanLuc: '15/04/2026, 08:31',
  },
  {
    stt: 2,
    maGiaoDich: 'GM-20260415-002',
    khachHang: 'Tran Thi B',
    email: 'tranthib@gomet.vn',
    goi: 'Premium 1 nam',
    soTien: 499000,
    trangThai: 'Da thanh toan',
    taoLuc: '15/04/2026, 10:15',
    thanhToanLuc: '15/04/2026, 10:18',
  },
  {
    stt: 3,
    maGiaoDich: 'GM-20260415-003',
    khachHang: 'Le Minh C',
    email: 'leminhc@gomet.vn',
    goi: 'Premium tron doi',
    soTien: 999000,
    trangThai: 'Dang cho',
    taoLuc: '15/04/2026, 11:00',
    thanhToanLuc: '—',
  },
  {
    stt: 4,
    maGiaoDich: 'GM-20260415-004',
    khachHang: 'Pham Thu D',
    email: 'phamthud@gomet.vn',
    goi: 'Premium 1 thang',
    soTien: 99000,
    trangThai: 'Da huy',
    taoLuc: '15/04/2026, 13:45',
    thanhToanLuc: '—',
  },
]

const totalTransactions = sampleTransactions.length
const paidCount = sampleTransactions.filter((item) => item.trangThai === 'Da thanh toan').length
const cancelledCount = sampleTransactions.filter((item) => item.trangThai === 'Da huy').length
const pendingCount = sampleTransactions.filter((item) => item.trangThai === 'Dang cho').length
const totalRevenue = sampleTransactions
  .filter((item) => item.trangThai === 'Da thanh toan')
  .reduce((sum, item) => sum + item.soTien, 0)
const planRevenue = sampleTransactions.reduce((accumulator, item) => {
  if (item.trangThai !== 'Da thanh toan') {
    return accumulator
  }

  accumulator[item.goi] = (accumulator[item.goi] || 0) + item.soTien
  return accumulator
}, {})
const topPlans = Object.entries(planRevenue)
  .sort((left, right) => right[1] - left[1])
  .slice(0, 3)

const pdf = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' })
const pageWidth = pdf.internal.pageSize.getWidth()
const pageHeight = pdf.internal.pageSize.getHeight()
const sideMargin = 18
const contentWidth = pageWidth - sideMargin * 2
const bottomMargin = 18

const registerVietnamesePdfFont = (doc) => {
  const regularFont = fs.readFileSync(path.join(fontDir, 'NotoSans-Regular.ttf')).toString('base64')
  const boldFont = fs.readFileSync(path.join(fontDir, 'NotoSans-Bold.ttf')).toString('base64')

  doc.addFileToVFS('NotoSans-Regular.ttf', regularFont)
  doc.addFont('NotoSans-Regular.ttf', 'NotoSans', 'normal')
  doc.addFileToVFS('NotoSans-Bold.ttf', boldFont)
  doc.addFont('NotoSans-Bold.ttf', 'NotoSans', 'bold')

  const originalSetFont = doc.setFont.bind(doc)
  doc.setFont = (fontName, fontStyle, fontWeight) => originalSetFont(
    fontName === 'helvetica' ? 'NotoSans' : fontName,
    fontStyle,
    fontWeight,
  )
  doc.setFont('NotoSans', 'normal')
}

registerVietnamesePdfFont(pdf)

const measureLines = (text, width) => pdf.splitTextToSize(String(text ?? ''), width)

const drawPageChrome = () => {
  pdf.setFillColor(255, 250, 245)
  pdf.rect(0, 0, pageWidth, pageHeight, 'F')

  pdf.setFillColor(255, 237, 213)
  pdf.rect(0, 0, pageWidth, 54, 'F')
  pdf.setFillColor(234, 88, 12)
  pdf.circle(pageWidth - 18, 16, 20, 'F')
  pdf.setFillColor(245, 158, 11)
  pdf.circle(pageWidth - 7, 34, 10, 'F')

  pdf.setTextColor(234, 88, 12)
  pdf.setFont('helvetica', 'bold')
  pdf.setFontSize(12)
  pdf.text('GOMET PREMIUM', 18, 20)

  pdf.setTextColor(15, 23, 42)
  pdf.setFontSize(22)
  pdf.text('HÓA ĐƠN GOMET PREMIUM', 18, 31)
  pdf.setFont('helvetica', 'normal')
  pdf.setFontSize(10)
  pdf.setTextColor(71, 85, 105)
  pdf.text(measureLines('Mẫu này mô phỏng đúng bố cục PDF đã được sửa lỗi chồng lấp nội dung và mã QR.', 115), 18, 39)
}

const drawDetailTable = (rows, x, y, width) => {
  const labelWidth = 46
  const valueWidth = width - labelWidth - 20
  const lineHeight = 4.6
  const parsedRows = rows.map(([label, value]) => {
    const labelLines = measureLines(label, labelWidth)
    const valueLines = measureLines(value, valueWidth)
    const contentLines = Math.max(labelLines.length, valueLines.length)
    return {
      labelLines,
      valueLines,
      height: 8 + contentLines * lineHeight,
    }
  })

  const totalHeight = parsedRows.reduce((sum, row) => sum + row.height, 6)
  pdf.setFillColor(248, 250, 252)
  pdf.roundedRect(x, y, width, totalHeight, 4, 4, 'F')

  let rowY = y + 8
  parsedRows.forEach((row, index) => {
    pdf.setFont('helvetica', 'normal')
    pdf.setFontSize(9)
    pdf.setTextColor(100, 116, 139)
    pdf.text(row.labelLines, x + 6, rowY)

    pdf.setFont('helvetica', 'bold')
    pdf.setFontSize(10)
    pdf.setTextColor(15, 23, 42)
    pdf.text(row.valueLines, x + 6 + labelWidth + 8, rowY)

    if (index < parsedRows.length - 1) {
      pdf.setDrawColor(226, 232, 240)
      pdf.line(x + 6, rowY + row.height - 4, x + width - 6, rowY + row.height - 4)
    }

    rowY += row.height
  })

  return totalHeight
}

const drawInfoCard = ({ title, body, x, y, width }) => {
  const bodyLines = measureLines(body, width - 12)
  const height = Math.max(24, 14 + bodyLines.length * 4.5 + 8)
  pdf.setFillColor(255, 247, 237)
  pdf.roundedRect(x, y, width, height, 5, 5, 'F')
  pdf.setFont('helvetica', 'bold')
  pdf.setFontSize(10)
  pdf.setTextColor(194, 65, 12)
  pdf.text(title, x + 6, y + 9)
  pdf.setFont('helvetica', 'normal')
  pdf.setFontSize(8.5)
  pdf.setTextColor(120, 53, 15)
  pdf.text(bodyLines, x + 6, y + 15)
  return height
}

const getBenefitRows = (items, width) => items.map((item) => {
  const lines = measureLines(item, width)
  return {
    lines,
    height: Math.max(10, lines.length * 4.8 + 4),
  }
})

const qrPayload = [
  `invoice=${sampleReceipt.invoiceNumber}`,
  `ref=${sampleReceipt.txRef}`,
  `customer=${sampleReceipt.accountOwner}`,
  `email=${sampleReceipt.email}`,
  `plan=${sampleReceipt.planName}`,
  `amount=${sampleReceipt.totalPaid}`,
  `issuedAt=${sampleReceipt.time}`,
  'status=PAID',
].join('&')

const qrImage = await QRCode.toDataURL(qrPayload, {
  margin: 1,
  width: 220,
  color: {
    dark: '#0f172a',
    light: '#ffffff',
  },
})

drawPageChrome()

let currentY = 62

const ensureSpace = (requiredHeight) => {
  if (currentY + requiredHeight <= pageHeight - bottomMargin) {
    return
  }

  pdf.addPage()
  drawPageChrome()
  currentY = 62
}

pdf.setFillColor(255, 255, 255)
pdf.roundedRect(sideMargin, currentY, contentWidth, 34, 8, 8, 'F')
pdf.setDrawColor(226, 232, 240)
pdf.roundedRect(sideMargin, currentY, contentWidth, 34, 8, 8, 'S')

pdf.setFillColor(15, 23, 42)
pdf.roundedRect(sideMargin + 10, currentY + 8, 26, 18, 5, 5, 'F')
pdf.setFont('helvetica', 'bold')
pdf.setFontSize(12)
pdf.setTextColor(255, 255, 255)
pdf.text('GM', sideMargin + 23, currentY + 19, { align: 'center' })

pdf.setFont('helvetica', 'normal')
pdf.setFontSize(8)
pdf.setTextColor(100, 116, 139)
pdf.text('Số hóa đơn', sideMargin + 44, currentY + 13)
pdf.setFont('helvetica', 'bold')
pdf.setFontSize(12)
pdf.setTextColor(15, 23, 42)
pdf.text(sampleReceipt.invoiceNumber, sideMargin + 44, currentY + 21)

const amountLines = measureLines(formatCurrency(sampleReceipt.totalPaid), 32)
pdf.setFillColor(255, 247, 237)
pdf.roundedRect(pageWidth - sideMargin - 58, currentY + 5, 48, 24, 6, 6, 'F')
pdf.setFont('helvetica', 'normal')
pdf.setFontSize(8)
pdf.setTextColor(120, 53, 15)
pdf.text('Tổng thanh toán', pageWidth - sideMargin - 52, currentY + 13)
pdf.setFont('helvetica', 'bold')
pdf.setFontSize(10.5)
pdf.setTextColor(194, 65, 12)
pdf.text(amountLines, pageWidth - sideMargin - 52, currentY + 19)

currentY += 46

const detailRows = [
  ['Mã tham chiếu', sampleReceipt.txRef],
  ['Chủ tài khoản', sampleReceipt.accountOwner],
  ['Gói dịch vụ', sampleReceipt.planName],
  ['Trạng thái', sampleReceipt.status],
  ['Phương thức thanh toán', sampleReceipt.paymentMethod],
  ['Thời điểm xuất hóa đơn', sampleReceipt.time],
  ['Giá trị thanh toán', formatCurrency(sampleReceipt.totalPaid)],
  ['Thuế VAT', 'Đã bao gồm'],
]

ensureSpace(92)
pdf.setFont('helvetica', 'bold')
pdf.setFontSize(12)
pdf.setTextColor(15, 23, 42)
pdf.text('Thông tin giao dịch', sideMargin + 10, currentY)
currentY += 6
currentY += drawDetailTable(detailRows, sideMargin + 10, currentY, contentWidth - 20)
currentY += 12

ensureSpace(34)
currentY += drawInfoCard({
  title: 'Thông tin xuất hóa đơn',
  body: 'GoMet Premium | MST: 0312345678 | Địa chỉ: 12 Nguyễn Huệ, Quận 1, TP.HCM | accounting@gomet.vn | Hotline: 1900 6868',
  x: sideMargin + 10,
  y: currentY,
  width: contentWidth - 20,
})
currentY += 12

const qrMetaLines = measureLines(`${sampleReceipt.txRef} - ${sampleReceipt.status}`, contentWidth - 74)
const qrBlockHeight = Math.max(42, 18 + qrMetaLines.length * 4.6)
ensureSpace(qrBlockHeight + 6)
pdf.setFillColor(248, 250, 252)
pdf.roundedRect(sideMargin + 10, currentY, contentWidth - 20, qrBlockHeight, 5, 5, 'F')
pdf.addImage(qrImage, 'PNG', sideMargin + 16, currentY + 6, 24, 24)
pdf.setFont('helvetica', 'bold')
pdf.setFontSize(10)
pdf.setTextColor(15, 23, 42)
pdf.text('Mã tra cứu QR', sideMargin + 48, currentY + 14)
pdf.setFont('helvetica', 'normal')
pdf.setFontSize(8.5)
pdf.setTextColor(100, 116, 139)
pdf.text(qrMetaLines, sideMargin + 48, currentY + 22)
currentY += qrBlockHeight + 12

const benefitRows = getBenefitRows([
  'Trải nghiệm không quảng cáo',
  'Trợ lý AI Cook sẵn sàng',
  'Kho lưu công thức mở rộng',
], contentWidth - 36)
const benefitsHeight = benefitRows.reduce((sum, row) => sum + row.height, 14)
ensureSpace(benefitsHeight + 20)
pdf.setFont('helvetica', 'bold')
pdf.setFontSize(12)
pdf.setTextColor(15, 23, 42)
pdf.text('Quyền lợi đã kích hoạt', sideMargin + 10, currentY)
currentY += 6
pdf.setFillColor(255, 255, 255)
pdf.roundedRect(sideMargin + 10, currentY, contentWidth - 20, benefitsHeight, 5, 5, 'F')
pdf.setDrawColor(226, 232, 240)
pdf.roundedRect(sideMargin + 10, currentY, contentWidth - 20, benefitsHeight, 5, 5, 'S')

let benefitY = currentY + 12
benefitRows.forEach((benefit) => {
  pdf.setFillColor(22, 163, 74)
  pdf.circle(sideMargin + 16, benefitY - 1, 1.8, 'F')
  pdf.setFont('helvetica', 'normal')
  pdf.setFontSize(10)
  pdf.setTextColor(51, 65, 85)
  pdf.text(benefit.lines, sideMargin + 22, benefitY)
  benefitY += benefit.height
})

currentY += benefitsHeight + 12
ensureSpace(20)
pdf.setDrawColor(226, 232, 240)
pdf.line(sideMargin, currentY, pageWidth - sideMargin, currentY)
currentY += 9
pdf.setFont('helvetica', 'normal')
pdf.setFontSize(9)
pdf.setTextColor(100, 116, 139)
pdf.text(measureLines(`Hỗ trợ: ${sampleReceipt.supportLine}`, contentWidth), sideMargin, currentY)
currentY += 8
pdf.text(measureLines('Mẫu này được tạo tự động để preview file PDF sau khi sửa lỗi chồng lấp bố cục.', contentWidth), sideMargin, currentY)

const pdfPath = path.join(outputDir, 'mau-hoa-don-gomet.pdf')
fs.writeFileSync(pdfPath, Buffer.from(pdf.output('arraybuffer')))

const workbook = new ExcelJS.Workbook()
const overviewSheet = workbook.addWorksheet('Tong_Quan_Bao_Cao', {
  views: [{ showGridLines: false }],
  properties: { defaultRowHeight: 22 }
})
const worksheet = workbook.addWorksheet('Chi_Tiet_Giao_Dich', {
  views: [{ state: 'frozen', ySplit: 11 }],
  properties: { defaultRowHeight: 22 }
})

overviewSheet.columns = [
  { width: 24 },
  { width: 20 },
  { width: 20 },
  { width: 22 },
  { width: 22 },
  { width: 22 },
]

overviewSheet.mergeCells('A1:F1')
overviewSheet.getCell('A1').value = 'TONG QUAN BAO CAO GIAO DICH GOMET'
overviewSheet.getCell('A1').font = { bold: true, size: 18, color: { argb: 'FFFFFFFF' } }
overviewSheet.getCell('A1').alignment = { horizontal: 'center', vertical: 'middle' }
overviewSheet.getCell('A1').fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFEA580C' } }
overviewSheet.getRow(1).height = 30

overviewSheet.mergeCells('A2:F2')
overviewSheet.getCell('A2').value = 'Trang tong quan gom KPI, trang thai giao dich va goi Premium co doanh thu cao nhat.'
overviewSheet.getCell('A2').font = { size: 10, color: { argb: 'FF64748B' } }
overviewSheet.getCell('A2').alignment = { horizontal: 'center' }

overviewSheet.mergeCells('A4:F4')
overviewSheet.getCell('A4').value = `Thoi diem xuat bao cao: ${formatDate(Date.now())}`
overviewSheet.getCell('A4').font = { italic: true, size: 10, color: { argb: 'FF64748B' } }

overviewSheet.mergeCells('A5:F5')
overviewSheet.getCell('A5').value = 'Bo loc dang ap dung: Tat ca giao dich'
overviewSheet.getCell('A5').font = { size: 10, color: { argb: 'FF64748B' } }

;['A7:B7', 'A8:B9', 'C7:D7', 'C8:D9', 'E7:F7', 'E8:F9', 'A11:B11', 'A12:B13', 'C11:D11', 'C12:D13'].forEach((range) => overviewSheet.mergeCells(range))

const overviewCards = [
  { labelCell: 'A7', valueCell: 'A8', label: 'Tong doanh thu', value: formatCurrency(totalRevenue), fill: 'FFF0FDF4', text: 'FF166534' },
  { labelCell: 'C7', valueCell: 'C8', label: 'Tong giao dich', value: totalTransactions, fill: 'FFEFF6FF', text: 'FF1D4ED8' },
  { labelCell: 'E7', valueCell: 'E8', label: 'Giao dich thanh cong', value: paidCount, fill: 'FFFFF7ED', text: 'FFEA580C' },
  { labelCell: 'A11', valueCell: 'A12', label: 'Giao dich dang cho', value: pendingCount, fill: 'FFFFFBEB', text: 'FFD97706' },
  { labelCell: 'C11', valueCell: 'C12', label: 'Giao dich da huy', value: cancelledCount, fill: 'FFFEF2F2', text: 'FFDC2626' },
]

overviewCards.forEach((card) => {
  const labelCell = overviewSheet.getCell(card.labelCell)
  const valueCell = overviewSheet.getCell(card.valueCell)
  labelCell.value = card.label
  valueCell.value = card.value
  labelCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: card.fill } }
  valueCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: card.fill } }
  labelCell.font = { bold: true, size: 10, color: { argb: card.text } }
  valueCell.font = { bold: true, size: 16, color: { argb: card.text } }
  labelCell.alignment = { horizontal: 'center', vertical: 'middle' }
  valueCell.alignment = { horizontal: 'center', vertical: 'middle' }
  labelCell.border = {
    top: { style: 'thin', color: { argb: 'FFE2E8F0' } },
    left: { style: 'thin', color: { argb: 'FFE2E8F0' } },
    bottom: { style: 'thin', color: { argb: 'FFE2E8F0' } },
    right: { style: 'thin', color: { argb: 'FFE2E8F0' } },
  }
  valueCell.border = labelCell.border
})

overviewSheet.mergeCells('A16:C16')
overviewSheet.getCell('A16').value = 'Thong ke theo trang thai'
overviewSheet.getCell('A16').font = { bold: true, size: 12, color: { argb: 'FF0F172A' } }

overviewSheet.mergeCells('D16:F16')
overviewSheet.getCell('D16').value = 'Top goi Premium theo doanh thu'
overviewSheet.getCell('D16').font = { bold: true, size: 12, color: { argb: 'FF0F172A' } }

const statusHeader = overviewSheet.getRow(17)
statusHeader.getCell(1).value = 'Trang thai'
statusHeader.getCell(2).value = 'So luong'
statusHeader.getCell(3).value = 'Ty le'
;['A17', 'B17', 'C17', 'D17', 'E17', 'F17'].forEach((address) => {
  const cell = overviewSheet.getCell(address)
  cell.font = { bold: true, color: { argb: 'FFFFFFFF' } }
  cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0F172A' } }
  cell.alignment = { horizontal: 'center', vertical: 'middle' }
})
overviewSheet.getCell('D17').value = 'Goi'
overviewSheet.getCell('E17').value = 'Doanh thu'
overviewSheet.getCell('F17').value = 'Ty trong'

const statusRows = [
  ['Da thanh toan', paidCount],
  ['Dang cho', pendingCount],
  ['Da huy', cancelledCount],
]
statusRows.forEach(([label, value], index) => {
  const rowIndex = 18 + index
  overviewSheet.getCell(`A${rowIndex}`).value = label
  overviewSheet.getCell(`B${rowIndex}`).value = value
  overviewSheet.getCell(`C${rowIndex}`).value = `${Math.round((value / totalTransactions) * 100)}%`
})

topPlans.forEach(([label, value], index) => {
  const rowIndex = 18 + index
  overviewSheet.getCell(`D${rowIndex}`).value = label
  overviewSheet.getCell(`E${rowIndex}`).value = formatCurrency(value)
  overviewSheet.getCell(`F${rowIndex}`).value = `${Math.round((value / totalRevenue) * 100)}%`
})

for (let rowIndex = 18; rowIndex <= 20; rowIndex += 1) {
  for (let columnIndex = 1; columnIndex <= 6; columnIndex += 1) {
    const cell = overviewSheet.getRow(rowIndex).getCell(columnIndex)
    cell.border = {
      top: { style: 'thin', color: { argb: 'FFE2E8F0' } },
      left: { style: 'thin', color: { argb: 'FFE2E8F0' } },
      bottom: { style: 'thin', color: { argb: 'FFE2E8F0' } },
      right: { style: 'thin', color: { argb: 'FFE2E8F0' } },
    }
    cell.alignment = { horizontal: columnIndex === 1 || columnIndex === 4 ? 'left' : 'center', vertical: 'middle' }
    if (rowIndex % 2 === 0) {
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFAFAFA' } }
    }
  }
}

worksheet.columns = [
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

worksheet.mergeCells('A1:I1')
worksheet.getCell('A1').value = 'BAO CAO GIAO DICH GOMET'
worksheet.getCell('A1').font = { bold: true, size: 18, color: { argb: 'FFFFFFFF' } }
worksheet.getCell('A1').alignment = { horizontal: 'center' }
worksheet.getCell('A1').fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFEA580C' } }
worksheet.getRow(1).height = 28

worksheet.mergeCells('A2:I2')
worksheet.getCell('A2').value = 'Sheet chi tiet mo phong workbook xuat Excel da duoc nang cap voi tong quan rieng, dinh dang trang thai va tong doanh thu cuoi bang.'
worksheet.getCell('A2').font = { size: 11, color: { argb: 'FF475569' } }
worksheet.getCell('A2').alignment = { horizontal: 'center' }

worksheet.mergeCells('A4:I4')
worksheet.getCell('A4').value = `Thoi diem xuat bao cao: ${formatDate(Date.now())}`
worksheet.getCell('A4').font = { italic: true, size: 10, color: { argb: 'FF64748B' } }

worksheet.mergeCells('A5:I5')
worksheet.getCell('A5').value = 'Bo loc dang ap dung: Tat ca giao dich'
worksheet.getCell('A5').font = { size: 10, color: { argb: 'FF64748B' } }

;['A6:C6','A7:C7','D6:F6','D7:F7','G6:I6','G7:I7','G8:I8','G9:I9'].forEach((range) => worksheet.mergeCells(range))

const summaryBlocks = [
  { labelCell: 'A6', valueCell: 'A7', label: 'Tong doanh thu', value: formatCurrency(totalRevenue), fill: 'FFF0FDF4', text: 'FF166534' },
  { labelCell: 'D6', valueCell: 'D7', label: 'Tong giao dich', value: sampleTransactions.length, fill: 'FFEFF6FF', text: 'FF1D4ED8' },
  { labelCell: 'G6', valueCell: 'G7', label: 'Giao dich da huy', value: cancelledCount, fill: 'FFFEF2F2', text: 'FFDC2626' },
  { labelCell: 'G8', valueCell: 'G9', label: 'Giao dich cho xu ly', value: pendingCount, fill: 'FFFFFBEB', text: 'FFD97706' },
]

summaryBlocks.forEach((block) => {
  const labelCell = worksheet.getCell(block.labelCell)
  const valueCell = worksheet.getCell(block.valueCell)
  labelCell.value = block.label
  valueCell.value = block.value
  labelCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: block.fill } }
  valueCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: block.fill } }
  labelCell.font = { bold: true, size: 10, color: { argb: block.text } }
  valueCell.font = { bold: true, size: 15, color: { argb: block.text } }
  labelCell.alignment = { horizontal: 'center', vertical: 'middle' }
  valueCell.alignment = { horizontal: 'center', vertical: 'middle' }
})

const headerRow = worksheet.getRow(11)
headerRow.values = ['STT', 'Ma Giao Dich', 'Khach Hang', 'Email', 'Goi Dich Vu', 'So Tien (VND)', 'Trang Thai', 'Ngay Tao', 'Ngay Thanh Toan']
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

sampleTransactions.forEach((item) => {
  const row = worksheet.addRow([
    item.stt,
    item.maGiaoDich,
    item.khachHang,
    item.email,
    item.goi,
    item.soTien,
    item.trangThai,
    item.taoLuc,
    item.thanhToanLuc,
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
  row.getCell(6).numFmt = '#,##0 "VND"'
  const statusCell = row.getCell(7)
  const statusFill = item.trangThai === 'Da thanh toan' ? 'FFE8FFF3' : item.trangThai === 'Dang cho' ? 'FFFFFBEB' : 'FFFEF2F2'
  const statusText = item.trangThai === 'Da thanh toan' ? 'FF15803D' : item.trangThai === 'Dang cho' ? 'FFD97706' : 'FFDC2626'
  statusCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: statusFill } }
  statusCell.font = { bold: true, color: { argb: statusText } }
  statusCell.alignment = { horizontal: 'center', vertical: 'middle' }

  if (row.number % 2 === 0) {
    row.eachCell((cell, columnNumber) => {
      if (columnNumber !== 7) {
        cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFCFCFD' } }
      }
    })
  }
})

const totalRow = worksheet.addRow(['', '', '', '', 'Tong doanh thu thanh cong', totalRevenue, '', '', ''])
totalRow.eachCell((cell) => {
  cell.font = { bold: true, color: { argb: 'FF0F172A' } }
  cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFFF7ED' } }
  cell.border = {
    top: { style: 'thin', color: { argb: 'FFE2E8F0' } },
    left: { style: 'thin', color: { argb: 'FFE2E8F0' } },
    bottom: { style: 'thin', color: { argb: 'FFE2E8F0' } },
    right: { style: 'thin', color: { argb: 'FFE2E8F0' } },
  }
})
totalRow.getCell(6).numFmt = '#,##0 "VND"'

worksheet.autoFilter = {
  from: { row: 11, column: 1 },
  to: { row: 11, column: 9 },
}

const excelPath = path.join(outputDir, 'mau-bao-cao-giao-dich-gomet.xlsx')
await workbook.xlsx.writeFile(excelPath)

const htmlRows = sampleTransactions.map((item) => `
  <tr>
    <td>${item.stt}</td>
    <td>${item.maGiaoDich}</td>
    <td>${item.khachHang}</td>
    <td>${item.email}</td>
    <td>${item.goi}</td>
    <td>${formatCurrency(item.soTien)}</td>
    <td>${item.trangThai}</td>
    <td>${item.taoLuc}</td>
    <td>${item.thanhToanLuc}</td>
  </tr>`).join('')

const htmlPreview = `<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mau Excel GoMet</title>
  <style>
    body { font-family: Segoe UI, Arial, sans-serif; margin: 24px; background: #f8fafc; color: #0f172a; }
    .wrap { max-width: 1200px; margin: 0 auto; }
    .hero { background: linear-gradient(135deg, #fff7ed, #ffffff); border: 1px solid #fed7aa; border-radius: 20px; padding: 24px; margin-bottom: 20px; }
    h1 { margin: 0 0 8px; font-size: 30px; }
    p { margin: 0 0 20px; color: #64748b; }
    .meta { margin-bottom: 16px; font-size: 14px; color: #475569; }
    .summary { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 20px; }
    .card { background: #fff; border-radius: 16px; padding: 16px; border: 1px solid #e2e8f0; }
    .card-label { font-size: 12px; text-transform: uppercase; color: #64748b; margin-bottom: 6px; }
    .card-value { font-size: 22px; font-weight: 800; }
    .insights { display: grid; grid-template-columns: 1.1fr 1fr; gap: 16px; margin-bottom: 20px; }
    .panel { background: #fff; border-radius: 18px; border: 1px solid #e2e8f0; padding: 18px; }
    .panel h2 { margin: 0 0 12px; font-size: 18px; }
    .pill-grid { display: grid; gap: 10px; }
    .pill { display: flex; justify-content: space-between; align-items: center; padding: 12px 14px; border-radius: 14px; background: #f8fafc; border: 1px solid #e2e8f0; }
    .pill strong { font-size: 15px; }
    table { width: 100%; border-collapse: collapse; background: #fff; border: 1px solid #e2e8f0; border-radius: 16px; overflow: hidden; }
    th, td { padding: 12px 14px; border-bottom: 1px solid #e2e8f0; text-align: left; font-size: 14px; }
    th { background: #0f172a; color: #fff; font-weight: 700; }
    tr:nth-child(even) { background: #fcfcfd; }
    .ok { color: #16a34a; font-weight: 700; }
    .pending { color: #d97706; font-weight: 700; }
    .cancel { color: #dc2626; font-weight: 700; }
    .note { margin-top: 14px; font-size: 13px; }
    @media (max-width: 900px) { .summary, .insights { grid-template-columns: 1fr; } }
  </style>
</head>
<body>
  <div class="wrap">
    <div class="hero">
      <h1>Preview mau Excel giao dich</h1>
      <p>Bang nay mo phong workbook xuat Excel sau khi nang cap task 2, gom mot sheet tong quan KPI va mot sheet chi tiet giao dich duoc dinh dang ro rang hon.</p>
      <div class="meta">Thoi diem xuat bao cao: ${formatDate(Date.now())}</div>
      <div class="summary">
        <div class="card"><div class="card-label">Tong doanh thu</div><div class="card-value">${formatCurrency(totalRevenue)}</div></div>
        <div class="card"><div class="card-label">Tong giao dich</div><div class="card-value">${sampleTransactions.length}</div></div>
        <div class="card"><div class="card-label">Thanh cong</div><div class="card-value">${paidCount}</div></div>
        <div class="card"><div class="card-label">Dang cho</div><div class="card-value">${pendingCount}</div></div>
      </div>
    </div>
    <div class="insights">
      <div class="panel">
        <h2>Thong ke theo trang thai</h2>
        <div class="pill-grid">
          <div class="pill"><span>Da thanh toan</span><strong>${paidCount} giao dich</strong></div>
          <div class="pill"><span>Dang cho</span><strong>${pendingCount} giao dich</strong></div>
          <div class="pill"><span>Da huy</span><strong>${cancelledCount} giao dich</strong></div>
        </div>
      </div>
      <div class="panel">
        <h2>Top goi Premium theo doanh thu</h2>
        <div class="pill-grid">
          ${topPlans.map(([label, value]) => `<div class="pill"><span>${label}</span><strong>${formatCurrency(value)}</strong></div>`).join('')}
        </div>
      </div>
    </div>
    <table>
      <thead>
        <tr>
          <th>STT</th>
          <th>Ma giao dich</th>
          <th>Khach hang</th>
          <th>Email</th>
          <th>Goi Premium</th>
          <th>So tien</th>
          <th>Trang thai</th>
          <th>Tao luc</th>
          <th>Thanh toan luc</th>
        </tr>
      </thead>
      <tbody>
        ${htmlRows}
      </tbody>
    </table>
    <p class="note">File XLSX di kem: ${path.basename(excelPath)}. Workbook gom 2 sheet: Tong_Quan_Bao_Cao va Chi_Tiet_Giao_Dich.</p>
  </div>
</body>
</html>`

const previewPath = path.join(outputDir, 'mau-preview-excel.html')
fs.writeFileSync(previewPath, htmlPreview, 'utf8')

console.log(JSON.stringify({
  pdfPath,
  excelPath,
  previewPath,
  generatedAt: formatDate(Date.now()),
}, null, 2))