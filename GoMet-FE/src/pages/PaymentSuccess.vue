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
            <span class="premium-tag">GOMET PREMIUM</span>
          </div>
          
          <h1 class="serif-title">Trải nghiệm<br>đỉnh cao bắt đầu.</h1>
          <p class="summary-text">Tài khoản của bạn đã được nâng cấp. Các đặc quyền ưu tiên tối thượng đã chính thức được kích hoạt trên toàn hệ thống.</p>
          
          <div class="benefits-list">
            <div class="b-item">
              <div class="check-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg></div>
              <span>Không gian làm việc không quảng cáo</span>
            </div>
            <div class="b-item">
              <div class="check-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg></div>
              <span>Quyền truy cập Trợ lý AI Cook</span>
            </div>
            <div class="b-item">
              <div class="check-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg></div>
              <span>Mở khóa toàn bộ Kho thư viện V.I.P</span>
            </div>
          </div>
          
          <div class="redirect-status">
            <span class="label">Hệ thống tự động chuyển hướng sau <b>{{ countdown }}s</b></span>
            <div class="progress-line">
              <div class="fill" :style="{ width: (countdown / 10) * 100 + '%' }"></div>
            </div>
          </div>
        </div>

        <div class="statement-section">
          <div class="statement-card-wrapper" ref="receiptRef">
            <div class="statement-card">
              
              <div class="ticket-top">
                <div class="success-seal">
                  <div class="seal-bg"></div>
                  <svg viewBox="0 0 52 52">
                    <path d="M14.1 27.2l7.1 7.2 16.7-16.8" fill="none" stroke="#16A34A" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <h3>XÁC NHẬN GIAO DỊCH</h3>
                <p class="timestamp">{{ currentTime }}</p>
              </div>

              <div class="ticket-divider">
                <div class="notch left"></div>
                <div class="notch right"></div>
              </div>

              <div class="ticket-bottom">
                <div class="field">
                  <span class="f-label">Mã tham chiếu</span>
                  <span class="f-value font-mono">{{ displayTxnRef }}</span>
                </div>
                <div class="field">
                  <span class="f-label">Gói dịch vụ</span>
                  <span class="f-value">{{ displayPlanName }}</span>
                </div>
                <div class="field">
                  <span class="f-label">Chủ tài khoản</span>
                  <span class="f-value">{{ userName }}</span>
                </div>
                <div class="field">
                  <span class="f-label">Trạng thái</span>
                  <span class="f-value text-success">Thành công</span>
                </div>
                
                <div class="total-box">
                  <span class="total-label">TỔNG THANH TOÁN</span>
                  <span class="total-val">{{ displayAmount }}</span>
                </div>
              </div>

            </div>
            
            <div class="statement-footer">
              <p>Hỗ trợ: <b>support@gomet.vn</b></p>
            </div>
          </div>

          <div class="action-footer">
            <button class="btn-publish-lux" @click="goHome">BẮT ĐẦU TRẢI NGHIỆM</button>
            <button class="btn-cancel-lux" @click="downloadReceipt" :disabled="isDownloading">
              {{ isDownloading ? 'ĐANG XUẤT FILE...' : 'TẢI CHỨNG TỪ (PNG)' }}
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
            <span class="cancel-tag">GIAO DỊCH ĐÃ HỦY</span>
          </div>
          
          <h1 class="serif-title text-slate">Thanh toán<br>chưa hoàn tất.</h1>
          <p class="summary-text">Quá trình giao dịch đã bị hủy. Không có khoản phí nào được trừ từ tài khoản của bạn và gói Premium chưa được kích hoạt. Bạn có thể thử lại bất cứ lúc nào khi sẵn sàng.</p>
          
          <div class="redirect-status">
            <span class="label">Hệ thống tự động chuyển hướng sau <b>{{ countdown }}s</b></span>
            <div class="progress-line">
              <div class="fill bg-slate" :style="{ width: (countdown / 10) * 100 + '%' }"></div>
            </div>
          </div>

          <div class="action-footer">
            <button class="btn-cancel-lux" style="width: 100%" @click="goHome">QUAY LẠI TRANG CHỦ</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import html2canvas from 'html2canvas'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const status = ref(route.query.status || 'success')
const vnpAmount = route.query.vnp_Amount
const vnpTxnRef = route.query.vnp_TxnRef

// 1. LẤY PLANTYPE THẬT TỪ URL (DO BACKEND GỬI VỀ)
const planTypeFromUrl = route.query.planType

const displayPlanName = computed(() => {
  const pType = parseInt(planTypeFromUrl)
  switch (pType) {
    case 0: return 'Premium (Gói Test 10s)'
    case 1: return 'Premium (1 Tháng)'
    case 2: return 'Premium (1 Năm)'
    case 3: return 'Premium (Vĩnh viễn)'
    default: return 'Premium Account'
  }
})

const userName = computed(() => authStore.user?.fullName || authStore.user?.username || 'Quý khách')
const displayTxnRef = computed(() => vnpTxnRef ? vnpTxnRef : `TXN-${Math.floor(100000 + Math.random() * 899999)}`)
const displayAmount = computed(() => {
  if (vnpAmount) return (Number(vnpAmount) / 100).toLocaleString('vi-VN') + ' đ'
  return 'Đã thanh toán'
})

const currentTime = ref('')
const countdown = ref(10)
const receiptRef = ref(null)
const isDownloading = ref(false)
let timer = null

onMounted(async () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('vi-VN', {
    hour: '2-digit', minute: '2-digit', second: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric'
  })

  if (status.value === 'success') {
    // 2. CẬP NHẬT QUYỀN TRONG LOCAL VÀ STORE
    const localUser = JSON.parse(localStorage.getItem('user')) || {}
    localUser.isPremium = 1
    localUser.role = 'premium'
    localStorage.setItem('user', JSON.stringify(localUser))
    
    if (authStore.user) {
      authStore.user.isPremium = 1
      authStore.user.role = 'premium'
    }
    
    toast.success("Nâng cấp Premium thành công! 🎉")
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
  if (!receiptRef.value) return
  isDownloading.value = true
  try {
    const canvas = await html2canvas(receiptRef.value, { 
      scale: 3, 
      backgroundColor: '#ffffff',
      useCORS: true 
    })
    const link = document.createElement('a')
    link.href = canvas.toDataURL("image/png")
    link.download = `GoMet_Receipt_${displayTxnRef.value}.png`
    link.click()
    toast.success("Đã lưu chứng từ!")
  } catch (e) { 
    toast.error("Lỗi khi tạo ảnh!") 
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