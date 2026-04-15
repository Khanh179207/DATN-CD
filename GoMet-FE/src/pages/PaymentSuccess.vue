<template>
  <div class="payment-premium-success-pro">
    <div class="ambient-glow"></div>
    <div class="bg-dots-soft"></div>

    <transition name="fade-up-pro" appear mode="out-in">
      <div v-if="status === 'success'" class="executive-container" key="success">
        
        <div class="branding-section">
          <div class="brand-header">
            <div class="logo-badge-premium">
              <svg class="crown-svg" viewBox="0 0 24 24">
                <path d="M5 15l-3-8 5 2 5-7 5 7 5-2-3 8H5z" fill="url(#goldGrad)"/>
                <defs>
                  <linearGradient id="goldGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" style="stop-color:#F59E0B" />
                    <stop offset="100%" style="stop-color:#EA580C" />
                  </linearGradient>
                </defs>
              </svg>
              <div class="logo-glow"></div>
            </div>
            <span class="premium-tag">{{ t('payment_success.success_tag') }}</span>
          </div>
          
          <h1 class="serif-title">{{ t('payment_success.success_heading') }}</h1>
          <p class="summary-text">{{ t('payment_success.success_summary') }}</p>
          
          <div class="benefits-list">
            <div class="b-item">
              <div class="check-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg></div>
              <span>{{ t('payment_success.benefit_ad_free') }}</span>
            </div>
            <div class="b-item">
              <div class="check-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg></div>
              <span>{{ t('payment_success.benefit_ai') }}</span>
            </div>
            <div class="b-item">
              <div class="check-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg></div>
              <span>{{ t('payment_success.benefit_storage') }}</span>
            </div>
          </div>
          
          <div class="redirect-status">
            <span class="label">{{ t('payment_success.redirect_prefix') }} <b>{{ countdown }}s</b></span>
            <div class="progress-line">
              <div class="fill" :style="{ width: (countdown / 10) * 100 + '%' }"></div>
            </div>
          </div>
        </div>

        <div class="statement-section">
          <div class="statement-card-wrapper">
            <div class="statement-card">
              
              <div class="ticket-top">
                <div class="success-seal">
                  <div class="seal-bg"></div>
                  <svg viewBox="0 0 52 52">
                    <path d="M14.1 27.2l7.1 7.2 16.7-16.8" fill="none" stroke="#16A34A" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <h3>{{ t('payment_success.receipt_title') }}</h3>
                <p class="timestamp">{{ currentTime }}</p>
              </div>

              <div class="ticket-divider">
                <div class="notch left"></div>
                <div class="notch right"></div>
              </div>

              <div class="ticket-bottom">
                <div class="field">
                  <span class="f-label">{{ t('payment_success.reference_code') }}</span>
                  <span class="f-value font-mono">{{ displayTxnRef }}</span>
                </div>
                <div class="field">
                  <span class="f-label">{{ t('payment_success.plan_label') }}</span>
                  <span class="f-value">{{ displayPlanName }}</span>
                </div>
                <div class="field">
                  <span class="f-label">{{ t('payment_success.account_owner') }}</span>
                  <span class="f-value">{{ userName }}</span>
                </div>
                <div class="field">
                  <span class="f-label">{{ t('payment_success.status_label') }}</span>
                  <span class="f-value text-success">{{ t('payment_success.status_paid') }}</span>
                </div>
                
                <div class="total-box">
                  <span class="total-label">{{ t('payment_success.total_paid') }}</span>
                  <span class="total-val">{{ displayAmount }}</span>
                </div>
              </div>

            </div>
            
            <div class="statement-footer">
              <p>{{ t('payment_success.support_label') }}: <b>support@gomet.vn</b></p>
            </div>
          </div>

          <div class="action-footer">
            <button class="btn-publish-lux" @click="goHome">{{ t('payment_success.start_now') }}</button>
            <button class="btn-cancel-lux" @click="downloadReceipt" :disabled="isDownloading">
              {{ isDownloading ? t('payment_success.exporting_pdf') : t('payment_success.download_pdf') }}
            </button>
          </div>
        </div>

      </div>

      <div v-else class="executive-container cancel-state" key="cancel">
        <div class="branding-section">
          <div class="brand-header">
            <div class="logo-badge-cancel">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="15" y1="9" x2="9" y2="15"></line>
                <line x1="9" y1="9" x2="15" y2="15"></line>
              </svg>
              <div class="logo-glow-cancel"></div>
            </div>
              <span class="cancel-tag">{{ t('payment_success.cancel_tag') }}</span>
          </div>
          
            <h1 class="serif-title text-slate">{{ t('payment_success.cancel_heading') }}</h1>
            <p class="summary-text">{{ t('payment_success.cancel_summary') }}</p>
          
          <div class="redirect-status">
              <span class="label">{{ t('payment_success.redirect_prefix') }} <b>{{ countdown }}s</b></span>
            <div class="progress-line">
              <div class="fill bg-slate" :style="{ width: (countdown / 10) * 100 + '%' }"></div>
            </div>
          </div>

          <div class="action-footer">
              <button class="btn-cancel-lux" style="width: 100%" @click="goHome">{{ t('payment_success.back_home') }}</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { getDateLocale } from '@/i18n'
import { toast } from '@/composables/useToast'
import { jsPDF } from 'jspdf'
import QRCode from 'qrcode'

let pdfFontPromise = null

const arrayBufferToBase64 = (buffer) => {
  const bytes = new Uint8Array(buffer)
  const chunkSize = 0x8000
  let binary = ''

  for (let index = 0; index < bytes.length; index += chunkSize) {
    binary += String.fromCharCode(...bytes.subarray(index, index + chunkSize))
  }

  return btoa(binary)
}

const ensureVietnamesePdfFont = async (pdf) => {
  if (!pdfFontPromise) {
    pdfFontPromise = Promise.all([
      fetch('/fonts/NotoSans-Regular.ttf'),
      fetch('/fonts/NotoSans-Bold.ttf'),
    ]).then(async ([regularResponse, boldResponse]) => {
      if (!regularResponse.ok || !boldResponse.ok) {
        throw new Error('Unable to load PDF fonts')
      }

      const [regularBuffer, boldBuffer] = await Promise.all([
        regularResponse.arrayBuffer(),
        boldResponse.arrayBuffer(),
      ])

      return {
        regular: arrayBufferToBase64(regularBuffer),
        bold: arrayBufferToBase64(boldBuffer),
      }
    })
  }

  const fonts = await pdfFontPromise
  pdf.addFileToVFS('NotoSans-Regular.ttf', fonts.regular)
  pdf.addFont('NotoSans-Regular.ttf', 'NotoSans', 'normal')
  pdf.addFileToVFS('NotoSans-Bold.ttf', fonts.bold)
  pdf.addFont('NotoSans-Bold.ttf', 'NotoSans', 'bold')

  const originalSetFont = pdf.setFont.bind(pdf)
  pdf.setFont = (fontName, fontStyle, fontWeight) => originalSetFont(
    fontName === 'helvetica' ? 'NotoSans' : fontName,
    fontStyle,
    fontWeight,
  )
  pdf.setFont('NotoSans', 'normal')
}

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { t, locale } = useI18n()

const status = ref(String(route.query.status || 'success').toLowerCase())
const vnpAmount = route.query.vnp_Amount
const vnpTxnRef = route.query.vnp_TxnRef
const dateLocale = computed(() => getDateLocale(locale.value))

const planTypeFromUrl = route.query.planType

const displayPlanName = computed(() => {
  const pType = parseInt(planTypeFromUrl)
  switch (pType) {
    case 0: return t('payment_success.plans.test')
    case 1: return t('payment_success.plans.monthly')
    case 2: return t('payment_success.plans.yearly')
    case 3: return t('payment_success.plans.lifetime')
    default: return t('payment_success.plans.default')
  }
})

const userName = computed(() => authStore.user?.fullName || authStore.user?.username || t('payment_success.guest_name'))
const displayTxnRef = computed(() => vnpTxnRef ? vnpTxnRef : `TXN-${Math.floor(100000 + Math.random() * 899999)}`)
const amountValue = computed(() => (vnpAmount ? Number(vnpAmount) / 100 : 0))
const displayAmount = computed(() => {
  if (amountValue.value) {
    return `${new Intl.NumberFormat(dateLocale.value).format(amountValue.value)} ${t('admin.transactions.currency')}`
  }
  return t('payment_success.paid_fallback')
})

const currentTime = ref('')
const countdown = ref(10)
const isDownloading = ref(false)
let timer = null

onMounted(async () => {
  const now = new Date()
  currentTime.value = now.toLocaleString(dateLocale.value, {
    hour: '2-digit', minute: '2-digit', second: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric'
  })

  if (status.value === 'success') {
    const localUser = JSON.parse(localStorage.getItem('user')) || {}
    localUser.isPremium = 1
    localUser.role = 'premium'
    localStorage.setItem('user', JSON.stringify(localUser))
    
    if (authStore.user) {
      authStore.user.isPremium = 1
      authStore.user.role = 'premium'
    }
    
    toast.success(t('payment_success.premium_activated'))
  }

  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) goHome()
  }, 1000)
})

onUnmounted(() => { if (timer) clearInterval(timer) })

const goHome = () => {
  if (timer) clearInterval(timer)
  router.push('/home')
}

const downloadReceipt = async () => {
  isDownloading.value = true
  try {
    const pdf = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' })
    await ensureVietnamesePdfFont(pdf)
    const pageWidth = pdf.internal.pageSize.getWidth()
    const pageHeight = pdf.internal.pageSize.getHeight()
    const sideMargin = 18
    const contentWidth = pageWidth - sideMargin * 2
    const bottomMargin = 18
    const qrPayload = JSON.stringify({
      referenceCode: displayTxnRef.value,
      plan: displayPlanName.value,
      customer: userName.value,
      amount: amountValue.value,
      issuedAt: currentTime.value,
      status: t('payment_success.status_paid')
    })
    const qrDataUrl = await QRCode.toDataURL(qrPayload, {
      margin: 1,
      width: 220,
      color: {
        dark: '#0F172A',
        light: '#FFFFFF'
      }
    })

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
      pdf.text(t('payment_success.badge'), 18, 20)

      pdf.setTextColor(15, 23, 42)
      pdf.setFontSize(22)
      pdf.text(t('payment_success.invoice_title'), 18, 31)

      pdf.setFont('helvetica', 'normal')
      pdf.setFontSize(10)
      pdf.setTextColor(71, 85, 105)
      pdf.text(pdf.splitTextToSize(t('payment_success.invoice_summary'), 115), 18, 39)
    }

    const ensureSpace = (requiredHeight) => {
      if (currentY + requiredHeight <= pageHeight - bottomMargin) {
        return
      }

      pdf.addPage()
      drawPageChrome()
      currentY = 62
    }

    const measureLines = (text, width) => pdf.splitTextToSize(String(text ?? ''), width)

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

    const getBenefitRows = (items, width) => items.map((item) => {
      const lines = measureLines(item, width)
      return {
        lines,
        height: Math.max(10, lines.length * 4.8 + 4),
      }
    })

    const drawInfoCard = ({ title, body, x, y, width, fill = [255, 247, 237], titleColor = [194, 65, 12], bodyColor = [120, 53, 15] }) => {
      const bodyLines = measureLines(body, width - 12)
      const height = Math.max(24, 14 + bodyLines.length * 4.5 + 8)
      pdf.setFillColor(...fill)
      pdf.roundedRect(x, y, width, height, 5, 5, 'F')
      pdf.setFont('helvetica', 'bold')
      pdf.setFontSize(10)
      pdf.setTextColor(...titleColor)
      pdf.text(title, x + 6, y + 9)
      pdf.setFont('helvetica', 'normal')
      pdf.setFontSize(8.5)
      pdf.setTextColor(...bodyColor)
      pdf.text(bodyLines, x + 6, y + 15)
      return height
    }

    drawPageChrome()

    let currentY = 62

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
    pdf.text(t('payment_success.invoice_number'), sideMargin + 44, currentY + 13)
    pdf.setFont('helvetica', 'bold')
    pdf.setFontSize(12)
    pdf.setTextColor(15, 23, 42)
    pdf.text(`INV-${displayTxnRef.value}`, sideMargin + 44, currentY + 21)

    const amountLines = measureLines(displayAmount.value, 32)
    pdf.setFillColor(255, 247, 237)
    pdf.roundedRect(pageWidth - sideMargin - 58, currentY + 5, 48, 24, 6, 6, 'F')
    pdf.setFont('helvetica', 'normal')
    pdf.setFontSize(8)
    pdf.setTextColor(120, 53, 15)
    pdf.text(t('payment_success.total_paid'), pageWidth - sideMargin - 52, currentY + 13)
    pdf.setFont('helvetica', 'bold')
    pdf.setFontSize(10.5)
    pdf.setTextColor(194, 65, 12)
    pdf.text(amountLines, pageWidth - sideMargin - 52, currentY + 19)

    currentY += 46

    const detailRows = [
      [t('payment_success.reference_code'), displayTxnRef.value],
      [t('payment_success.account_owner'), userName.value],
      [t('payment_success.plan_label'), displayPlanName.value],
      [t('payment_success.status_label'), t('payment_success.status_paid')],
      [t('payment_success.payment_method'), t('payment_success.payment_method_value')],
      [t('payment_success.issued_at'), currentTime.value],
      [t('payment_success.amount_label'), displayAmount.value],
      [t('payment_success.tax_label'), t('payment_success.tax_value')],
    ]

    const estimatedDetailHeight = 92
    ensureSpace(estimatedDetailHeight)
    pdf.setFont('helvetica', 'bold')
    pdf.setFontSize(12)
    pdf.setTextColor(15, 23, 42)
    pdf.text(t('payment_success.invoice_details_title'), sideMargin + 10, currentY)
    currentY += 6
    currentY += drawDetailTable(detailRows, sideMargin + 10, currentY, contentWidth - 20)
    currentY += 12

    ensureSpace(34)
    currentY += drawInfoCard({
      title: t('payment_success.company_block_title'),
      body: t('payment_success.company_block_body'),
      x: sideMargin + 10,
      y: currentY,
      width: contentWidth - 20,
    })
    currentY += 12

    const qrMetaLines = measureLines(`${displayTxnRef.value} - ${t('payment_success.status_paid')}`, contentWidth - 74)
    const qrBlockHeight = Math.max(42, 18 + qrMetaLines.length * 4.6)
    ensureSpace(qrBlockHeight + 6)
    pdf.setFillColor(248, 250, 252)
    pdf.roundedRect(sideMargin + 10, currentY, contentWidth - 20, qrBlockHeight, 5, 5, 'F')
    pdf.addImage(qrDataUrl, 'PNG', sideMargin + 16, currentY + 6, 24, 24)
    pdf.setFont('helvetica', 'bold')
    pdf.setFontSize(10)
    pdf.setTextColor(15, 23, 42)
    pdf.text(t('payment_success.qr_caption'), sideMargin + 48, currentY + 14)
    pdf.setFont('helvetica', 'normal')
    pdf.setFontSize(8.5)
    pdf.setTextColor(100, 116, 139)
    pdf.text(qrMetaLines, sideMargin + 48, currentY + 22)
    currentY += qrBlockHeight + 12

    const benefits = [
      t('payment_success.benefit_ad_free'),
      t('payment_success.benefit_ai'),
      t('payment_success.benefit_storage'),
    ]
    const benefitRows = getBenefitRows(benefits, contentWidth - 36)
    const benefitsHeight = benefitRows.reduce((sum, row) => sum + row.height, 14)
    ensureSpace(benefitsHeight + 20)
    pdf.setFont('helvetica', 'bold')
    pdf.setFontSize(12)
    pdf.setTextColor(15, 23, 42)
    pdf.text(t('payment_success.invoice_benefits_title'), sideMargin + 10, currentY)
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
    pdf.text(measureLines(t('payment_success.support_line'), contentWidth), sideMargin, currentY)
    currentY += 8
    pdf.text(measureLines(t('payment_success.invoice_footer_note'), contentWidth), sideMargin, currentY)

    pdf.save(`${t('payment_success.file_name')}-${displayTxnRef.value}.pdf`)
    toast.success(t('payment_success.download_ok'))
  } catch (e) { 
    toast.error(t('payment_success.download_failed'))
  } finally { 
    isDownloading.value = false 
  }
}
</script>

<style scoped lang="scss">
/* --- NỀN TẢNG THIẾT KẾ --- */
.payment-premium-success-pro {
  min-height: 100vh;
  background-color: #F8FAFC; 
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  font-family: var(--font-ui, 'Inter', 'Mulish', sans-serif);
  position: relative;
  overflow: hidden;
}

.ambient-glow {
  position: absolute;
  width: 60vw; height: 60vw;
  background: radial-gradient(circle, rgba(234, 88, 12, 0.05) 0%, transparent 60%);
  top: -10%; left: -10%;
  z-index: 0;
  pointer-events: none;
}

.bg-dots-soft {
  position: absolute; inset: 0; z-index: 0; pointer-events: none;
  background-image: radial-gradient(#CBD5E1 1px, transparent 1px);
  background-size: 32px 32px; opacity: 0.4;
}

/* CONTAINER CHÍNH */
.executive-container {
  z-index: 10;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  background: #FFFFFF;
  border-radius: 32px;
  max-width: 1000px;
  width: 100%;
  min-height: 560px;
  box-shadow: 0 25px 60px -20px rgba(15, 23, 42, 0.08), 0 0 0 1px rgba(226, 232, 240, 0.5);
  overflow: hidden;
}

/* CỘT TRÁI - BRANDING */
.branding-section {
  padding: 72px 64px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: #FFFFFF;
  position: relative;

  .brand-header {
    display: flex; align-items: center; gap: 16px; margin-bottom: 32px;
  }

  .premium-tag {
    font-size: 0.75rem; font-weight: 800; letter-spacing: 1.5px;
    color: #EA580C; background: #FFF7ED; padding: 6px 12px; border-radius: 100px;
  }

  .logo-badge-premium {
    width: 48px; height: 48px; position: relative;
    .crown-svg { width: 100%; height: 100%; filter: drop-shadow(0 4px 8px rgba(234, 88, 12, 0.25)); position: relative; z-index: 2; }
    .logo-glow { position: absolute; inset: -15px; background: radial-gradient(circle, rgba(245,158,11,0.2) 0%, transparent 60%); border-radius: 50%; z-index: 1; animation: pulseGlow 3s infinite alternate; }
  }

  .serif-title {
    font-family: var(--font-serif, 'Playfair Display', serif);
    font-size: 3.5rem;
    line-height: 1.1;
    margin-bottom: 20px;
    font-weight: 800;
    letter-spacing: -1.5px;
    color: #0F172A;
  }

  .summary-text {
    color: #64748B;
    font-size: 1.05rem;
    line-height: 1.6;
    max-width: 90%;
    margin-bottom: 40px;
  }
}

.benefits-list {
  display: flex; flex-direction: column; gap: 14px; margin-bottom: 48px;
  .b-item {
    display: flex; align-items: center; gap: 14px;
    color: #334155; font-weight: 600; font-size: 0.95rem;
    .check-icon { 
      width: 28px; height: 28px; border-radius: 50%; background: #F0FDF4; color: #16A34A; 
      display: flex; align-items: center; justify-content: center; flex-shrink: 0;
      box-shadow: 0 0 0 4px #F8FAFC;
      svg { width: 14px; height: 14px; }
    }
  }
}

.redirect-status {
  margin-top: auto;
  .label { font-size: 0.8rem; color: #94A3B8; font-weight: 500; display: block; margin-bottom: 12px; b { color: #0F172A; } }
  .progress-line {
    width: 100%; max-width: 240px; height: 4px; background: #F1F5F9; border-radius: 10px; overflow: hidden;
    .fill { height: 100%; background: linear-gradient(90deg, #F59E0B, #EA580C); transition: width 1s linear; border-radius: 10px; }
  }
}

/* CỘT PHẢI - STATEMENT TICKET */
.statement-section {
  padding: 64px 40px;
  background: #F8FAFC;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-left: 1px solid #F1F5F9;
}

.statement-card-wrapper {
  width: 100%; max-width: 340px;
}

.statement-card {
  background: #FFFFFF;
  border-radius: 20px;
  box-shadow: 0 20px 40px -15px rgba(15, 23, 42, 0.1);
  filter: drop-shadow(0 0 1px rgba(15, 23, 42, 0.05));
  overflow: hidden;
}

.ticket-top {
  padding: 32px 32px 24px;
  text-align: center;
  
  .success-seal { 
    position: relative; width: 64px; height: 64px; margin: 0 auto 16px; 
    display: flex; align-items: center; justify-content: center;
    .seal-bg { position: absolute; inset: 0; background: #DCFCE7; border-radius: 50%; animation: popIn 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards; }
    svg { width: 28px; height: 28px; position: relative; z-index: 1; opacity: 0; animation: checkDraw 0.4s 0.3s ease forwards; }
  }
  
  h3 { font-size: 0.85rem; letter-spacing: 1.5px; color: #0F172A; font-weight: 800; margin-bottom: 8px; }
  .timestamp { font-size: 0.75rem; color: #94A3B8; font-weight: 500; }
}

.ticket-divider {
  position: relative; height: 24px; display: flex; align-items: center;
  &::before {
    content: ''; position: absolute; left: 24px; right: 24px; height: 0;
    border-top: 2px dashed #E2E8F0;
  }
  .notch {
    position: absolute; width: 24px; height: 24px; background: #F8FAFC; border-radius: 50%;
    box-shadow: inset 0 0 1px rgba(15, 23, 42, 0.05);
    &.left { left: -12px; }
    &.right { right: -12px; }
  }
}

.ticket-bottom {
  padding: 24px 32px 32px;
  .field {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; font-size: 0.85rem;
    .f-label { color: #64748B; font-weight: 500; }
    .f-value { color: #0F172A; font-weight: 700; text-align: right; }
    .text-success { color: #16A34A; }
    .font-mono { font-family: 'SFMono-Regular', Consolas, 'Courier New', monospace; letter-spacing: -0.5px; }
  }
  
  .total-box {
    margin-top: 24px; padding-top: 20px; border-top: 1px solid #F1F5F9;
    display: flex; flex-direction: column; gap: 4px;
    .total-label { font-weight: 700; font-size: 0.75rem; color: #94A3B8; }
    .total-val { font-size: 1.5rem; font-weight: 800; color: #0F172A; letter-spacing: -0.5px; }
  }
}

.statement-footer {
  margin-top: 20px; text-align: center;
  p { font-size: 0.75rem; color: #94A3B8; font-weight: 500; b { color: #64748B; font-weight: 600; } }
}

/* NÚT BẤM CẢI TIẾN */
.action-footer {
  width: 100%; max-width: 340px; margin-top: 32px; display: flex; flex-direction: column; gap: 12px;
}
.btn-publish-lux {
  padding: 16px; background: linear-gradient(135deg, #EA580C, #F59E0B); 
  color: white; border: none; border-radius: 16px;
  font-weight: 700; font-size: 0.95rem; cursor: pointer; transition: all 0.2s ease;
  box-shadow: 0 4px 15px -3px rgba(234, 88, 12, 0.3);
  &:hover { transform: translateY(-2px); box-shadow: 0 8px 20px -4px rgba(234, 88, 12, 0.4); filter: brightness(1.05); }
}
.btn-cancel-lux {
  padding: 16px; background: transparent; color: #64748B; border: 1px solid #CBD5E1;
  border-radius: 16px; font-weight: 600; font-size: 0.9rem; cursor: pointer; transition: all 0.2s ease;
  &:hover { background: #FFFFFF; color: #0F172A; border-color: #94A3B8; }
}

/* ANIMATIONS */
.fade-up-pro-enter-active { transition: all 0.8s cubic-bezier(0.16, 1, 0.3, 1); }
.fade-up-pro-enter-from { opacity: 0; transform: translateY(30px); }
@keyframes pulseGlow { 0% { transform: scale(0.95); opacity: 0.4; } 100% { transform: scale(1.15); opacity: 0.7; } }
@keyframes popIn { 0% { transform: scale(0.5); opacity: 0; } 70% { transform: scale(1.1); } 100% { transform: scale(1); opacity: 1; } }
@keyframes checkDraw { 0% { opacity: 0; transform: scale(0.5); } 100% { opacity: 1; transform: scale(1); } }

/* --- CANCEL STATE STYLES --- */
.executive-container.cancel-state {
  grid-template-columns: 1fr;
  max-width: 600px;
  min-height: auto;
  text-align: center;
  
  .branding-section { padding: 64px 40px; align-items: center; }
  .brand-header { justify-content: center; }
  .summary-text { margin: 0 auto 40px; max-width: 100%; }
  .action-footer { margin: 32px auto 0; }
  .redirect-status { margin-top: 32px; align-items: center; }
}

.logo-badge-cancel {
  width: 48px; height: 48px; position: relative; color: #ef4444;
  svg { width: 100%; height: 100%; filter: drop-shadow(0 4px 8px rgba(239, 68, 68, 0.25)); position: relative; z-index: 2; }
  .logo-glow-cancel { position: absolute; inset: -15px; background: radial-gradient(circle, rgba(239,68,68,0.15) 0%, transparent 60%); border-radius: 50%; z-index: 1; animation: pulseGlowCancel 3s infinite alternate; }
}

.cancel-tag {
  font-size: 0.75rem; font-weight: 800; letter-spacing: 1.5px;
  color: #475569; background: #F1F5F9; padding: 6px 12px; border-radius: 100px;
}

.text-slate { color: #1E293B !important; }
.bg-slate { background: linear-gradient(90deg, #64748B, #475569) !important; }

@keyframes pulseGlowCancel { 0% { transform: scale(0.95); opacity: 0.4; } 100% { transform: scale(1.15); opacity: 0.7; } }

@media (max-width: 992px) {
  .executive-container { grid-template-columns: 1fr; }
  .branding-section { padding: 48px 32px; text-align: center; align-items: center; }
  .brand-header { justify-content: center; }
  .serif-title { font-size: 2.8rem; }
  .summary-text { max-width: 100%; }
  .benefits-list { width: 100%; max-width: 400px; text-align: left; }
  .statement-section { padding: 48px 24px; border-left: none; border-top: 1px solid #F1F5F9; }
}
</style>