<template>
  <div class="premium-page">
    <div class="bg-decor"></div>

    <!-- Hero -->
    <section class="hero-section">
      <div class="hero-inner">
        <div class="badge-wrap">
          <span class="badge-premium">👑 GOMET PREMIUM</span>
        </div>
        <h1 class="hero-title">Đánh thức tiềm năng <span class="gold-text">đầu bếp</span> của bạn</h1>
        <p class="hero-sub">Nâng cấp đặc quyền — Tận hưởng trọn bộ tính năng thông minh nhất của GoMet.</p>
        <button v-if="!alreadyPremium" class="btn-hero" @click="scrollToPlans">Xem gói Premium ↓</button>
        <div v-else class="already-premium-badge">
          <span>✓ Bạn đang sử dụng GoMet Premium</span>
        </div>
      </div>
      <div class="hero-img-wrap">
        <img src="https://images.unsplash.com/photo-1556910103-1c02745aae4d?q=80&w=800" alt="Premium cooking" />
      </div>
    </section>

    <!-- Features comparison table -->
    <section class="features-section">
      <h2 class="section-title">Đặc quyền dành riêng cho bạn</h2>
      <div class="feature-table">
        <div class="t-row t-head">
          <span class="feat-label">TÍNH NĂNG</span>
          <span class="col-val">FREE</span>
          <span class="col-val gold-col">PREMIUM ✦</span>
        </div>
        <div class="t-row" v-for="(feat, i) in features" :key="i">
          <div class="feat-box">
            <span class="f-name">{{ feat.name }}</span>
            <span class="f-sub">{{ feat.sub }}</span>
          </div>
          <span class="col-val muted">
            <span v-if="feat.free === 'max3'">Tối đa 3</span>
            <span v-else-if="feat.free === true" class="check green">✓</span>
            <span v-else class="dash">—</span>
          </span>
          <span class="col-val">
            <span v-if="feat.pro === 'unlimited'" class="unlimited">KHÔNG GIỚI HẠN</span>
            <span v-else class="check gold">✓</span>
          </span>
        </div>
      </div>
    </section>

    <!-- Plan selection -->
    <section class="plans-section" ref="plansRef" id="plans">
      <h2 class="section-title">Chọn gói đăng ký</h2>
      <div class="plan-cards">
        <div
          v-for="plan in plans" :key="plan.id"
          class="plan-card"
          :class="{ active: selectedPlan === plan.id, recommended: plan.id === 'yearly' }"
          @click="selectedPlan = plan.id"
        >
          <div v-if="plan.id === 'yearly'" class="badge-save">TIẾT KIỆM 20%</div>
          <div class="radio-row">
            <div class="radio-dot" :class="{ selected: selectedPlan === plan.id }"><div class="dot-inner"></div></div>
            <div class="plan-info">
              <span class="plan-name">{{ plan.name }}</span>
              <span class="plan-desc">{{ plan.desc }}</span>
            </div>
          </div>
          <div class="plan-price">
            <span class="price">{{ plan.price }}đ</span>
            <span class="unit">/{{ plan.unit }}</span>
          </div>
        </div>
      </div>

      <!-- Payment form -->
      <div class="pay-box">
        <div class="pay-tabs">
          <button class="tab active">💳 Thẻ tín dụng</button>
          <button class="tab">🧧 MoMo</button>
        </div>
        <div class="pay-inputs">
          <input type="text" placeholder="Số thẻ (4242 4242 4242 4242)" />
          <div class="row-2">
            <input type="text" placeholder="MM/YY" />
            <input type="password" placeholder="CVC" />
          </div>
        </div>
        <button class="btn-pay" :disabled="paying || alreadyPremium" @click="handlePay">
          <span v-if="paying">⏳ Đang xử lý...</span>
          <span v-else-if="alreadyPremium">✓ Bạn đã là Premium</span>
          <span v-else>🚀 KÍCH HOẠT PREMIUM — {{ activePlanPrice }}đ</span>
        </button>
        <p v-if="payError" class="pay-error">{{ payError }}</p>
        <p class="pay-note">🔒 Bảo mật SSL 256-bit · Hủy bất kỳ lúc nào</p>
      </div>
    </section>

    <!-- Success overlay -->
    <transition name="fade">
      <div v-if="successMsg" class="success-overlay">
        <div class="success-card">
          <div class="crown-icon">👑</div>
          <h2>Chào mừng đến GoMet Premium!</h2>
          <p>{{ successMsg }}</p>
          <button @click="$router.push('/home')">Khám phá ngay →</button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'

const authStore = useAuthStore()
const plansRef = ref(null)

const alreadyPremium = computed(() => authStore.user?.isPremium === 1 || authStore.user?.isPremium === true)

const selectedPlan = ref('yearly')
const paying = ref(false)
const payError = ref('')
const successMsg = ref('')

const plans = [
  { id: 'monthly', name: 'Gói Tháng', desc: 'Trải nghiệm linh hoạt', price: '25.000', unit: 'tháng', planType: 1 },
  { id: 'yearly',  name: 'Gói Năm',   desc: 'Sử dụng bền vững',     price: '240.000', unit: 'năm',   planType: 2 }
]

const activePlanPrice = computed(() => plans.find(p => p.id === selectedPlan.value)?.price || '')
const activePlanType  = computed(() => plans.find(p => p.id === selectedPlan.value)?.planType || 1)

const features = [
  { name: 'Không quảng cáo',       sub: 'Trải nghiệm liền mạch',   free: true,    pro: true       },
  { name: 'Chat AI (ChefBot)',      sub: 'Trợ lý gợi ý 24/7',       free: false,   pro: true       },
  { name: 'Bộ sưu tập Món-Tủ',     sub: 'Lưu trữ tinh hoa',        free: 'max3',  pro: 'unlimited'},
  { name: 'Kế hoạch ăn uống',      sub: 'Thực đơn tự động',        free: false,   pro: true       },
  { name: 'Danh sách đi chợ',      sub: 'Tự động báo giá',         free: false,   pro: true       },
  { name: 'Bảng xếp hạng Hot',     sub: 'Top món xu hướng',        free: false,   pro: true       },
  { name: 'Khung avatar Premium',   sub: 'Nổi bật giữa cộng đồng', free: false,   pro: true       },
  { name: 'Danh hiệu đặc quyền',   sub: 'Huy hiệu & tiêu đề VIP', free: false,   pro: true       },
]

function scrollToPlans() {
  plansRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

async function handlePay() {
  const accountID = authStore.user?.accountID || authStore.user?.id
  if (!accountID) {
    payError.value = 'Vui lòng đăng nhập trước khi nâng cấp!'
    return
  }
  paying.value = true
  payError.value = ''
  try {
    const res = await api.post('/api/subscription/upgrade', {
      accountID,
      plan: activePlanType.value
    })
    if (authStore.user) authStore.user.isPremium = 1
    const endDate = res.data?.endAt ? new Date(res.data.endAt).toLocaleDateString('vi-VN') : ''
    successMsg.value = `Premium đã được kích hoạt${endDate ? ' đến ' + endDate : ''}. Chúc bạn trải nghiệm vui vẻ! 🎉`
  } catch (e) {
    payError.value = e.response?.data?.message || 'Lỗi kích hoạt. Vui lòng thử lại!'
  } finally {
    paying.value = false
  }
}
</script>

<style scoped>
.premium-page { min-height: 100vh; background: #FFFBF5; font-family: 'Quicksand', sans-serif; position: relative; overflow: hidden; }
.bg-decor { position: fixed; inset: 0; background: radial-gradient(ellipse at 80% 0%, rgba(251,191,36,0.12) 0%, transparent 60%), radial-gradient(ellipse at 20% 100%, rgba(234,88,12,0.08) 0%, transparent 60%); pointer-events: none; z-index: 0; }

/* Hero */
.hero-section { display: grid; grid-template-columns: 1fr 1fr; align-items: center; gap: 60px; padding: 80px 80px 60px; max-width: 1200px; margin: 0 auto; position: relative; z-index: 1; }
.hero-inner { display: flex; flex-direction: column; gap: 20px; }
.badge-wrap { }
.badge-premium { background: linear-gradient(135deg, #F59E0B, #D97706); color: white; font-size: 0.75rem; font-weight: 800; padding: 6px 16px; border-radius: 20px; letter-spacing: 1px; }
.hero-title { font-size: 3rem; font-weight: 800; color: #0F172A; line-height: 1.2; margin: 0; }
.gold-text { color: #D97706; }
.hero-sub { color: #64748B; font-size: 1.1rem; margin: 0; line-height: 1.6; }
.btn-hero { background: linear-gradient(135deg, #EA580C, #D97706); color: white; border: none; padding: 16px 36px; border-radius: 14px; font-size: 1.1rem; font-weight: 800; cursor: pointer; font-family: inherit; transition: 0.2s; box-shadow: 0 8px 24px rgba(234,88,12,0.3); align-self: flex-start; }
.btn-hero:hover { transform: translateY(-3px); box-shadow: 0 12px 32px rgba(234,88,12,0.4); }
.already-premium-badge { background: linear-gradient(135deg, #10B981, #059669); color: white; padding: 14px 24px; border-radius: 12px; font-weight: 800; align-self: flex-start; }
.hero-img-wrap img { width: 100%; border-radius: 24px; object-fit: cover; height: 400px; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }

/* Features */
.features-section { padding: 60px 80px; max-width: 900px; margin: 0 auto; position: relative; z-index: 1; }
.section-title { font-size: 1.8rem; font-weight: 800; color: #0F172A; text-align: center; margin-bottom: 40px; }
.feature-table { background: white; border-radius: 20px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.06); }
.t-row { display: grid; grid-template-columns: 2fr 1fr 1fr; padding: 16px 24px; border-bottom: 1px solid #F8FAFC; align-items: center; }
.t-head { background: #0F172A; color: white; font-size: 0.75rem; font-weight: 800; letter-spacing: 1px; }
.gold-col { color: #F59E0B; }
.feat-label { }
.col-val { text-align: center; }
.feat-box { }
.f-name { display: block; font-weight: 700; font-size: 0.95rem; color: #1E293B; }
.f-sub { font-size: 0.8rem; color: #94A3B8; }
.check { font-size: 1.1rem; font-weight: 900; }
.check.green { color: #10B981; }
.check.gold { color: #D97706; }
.dash { color: #CBD5E1; }
.muted { color: #94A3B8; }
.unlimited { color: #D97706; font-weight: 800; font-size: 0.7rem; letter-spacing: 0.5px; }

/* Plans */
.plans-section { padding: 60px 80px 100px; max-width: 700px; margin: 0 auto; position: relative; z-index: 1; }
.plan-cards { display: flex; flex-direction: column; gap: 14px; margin-bottom: 30px; }
.plan-card { border: 2px solid #E2E8F0; border-radius: 16px; padding: 20px 24px; cursor: pointer; display: flex; justify-content: space-between; align-items: center; background: white; position: relative; transition: 0.2s; }
.plan-card:hover { border-color: #D97706; }
.plan-card.active { border-color: #F59E0B; background: #FFFBEB; box-shadow: 0 4px 20px rgba(245,158,11,0.15); }
.badge-save { position: absolute; top: -11px; right: 20px; background: #10B981; color: white; font-size: 0.65rem; font-weight: 800; padding: 3px 10px; border-radius: 10px; }
.radio-row { display: flex; align-items: center; gap: 14px; }
.radio-dot { width: 20px; height: 20px; border: 2px solid #CBD5E1; border-radius: 50%; display: flex; align-items: center; justify-content: center; transition: 0.2s; flex-shrink: 0; }
.radio-dot.selected { border-color: #F59E0B; }
.dot-inner { width: 10px; height: 10px; background: #F59E0B; border-radius: 50%; display: none; }
.selected .dot-inner { display: block; }
.plan-name { display: block; font-weight: 800; font-size: 1rem; color: #1E293B; }
.plan-desc { font-size: 0.82rem; color: #64748B; }
.price { font-size: 1.3rem; font-weight: 800; color: #0F172A; }
.unit { font-size: 0.8rem; color: #94A3B8; }

/* Payment box */
.pay-box { background: white; border-radius: 20px; padding: 30px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); }
.pay-tabs { display: flex; gap: 10px; margin-bottom: 20px; }
.tab { flex: 1; padding: 10px; border: 1.5px solid #E2E8F0; border-radius: 10px; font-weight: 700; font-size: 0.85rem; cursor: pointer; background: white; font-family: inherit; transition: 0.2s; }
.tab.active { background: #0F172A; color: white; border-color: #0F172A; }
.pay-inputs { display: flex; flex-direction: column; gap: 12px; }
.pay-inputs input { padding: 12px 16px; border: 1.5px solid #E2E8F0; border-radius: 10px; font-size: 0.95rem; font-family: inherit; outline: none; transition: 0.2s; }
.pay-inputs input:focus { border-color: #F59E0B; box-shadow: 0 0 0 3px rgba(245,158,11,0.1); }
.row-2 { display: flex; gap: 12px; }
.row-2 input { flex: 1; }
.btn-pay { width: 100%; margin-top: 20px; padding: 18px; background: linear-gradient(135deg, #EA580C, #D97706); color: white; border: none; border-radius: 14px; font-size: 1.05rem; font-weight: 800; cursor: pointer; font-family: inherit; transition: 0.2s; box-shadow: 0 8px 24px rgba(234,88,12,0.3); }
.btn-pay:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 12px 32px rgba(234,88,12,0.4); }
.btn-pay:disabled { opacity: 0.7; cursor: not-allowed; transform: none; }
.pay-error { color: #EF4444; text-align: center; font-size: 0.85rem; margin-top: 10px; font-weight: 600; }
.pay-note { text-align: center; color: #94A3B8; font-size: 0.75rem; margin-top: 12px; }

/* Success overlay */
.success-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); backdrop-filter: blur(6px); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.success-card { background: white; border-radius: 24px; padding: 50px; text-align: center; max-width: 440px; box-shadow: 0 30px 80px rgba(0,0,0,0.2); }
.crown-icon { font-size: 4rem; margin-bottom: 20px; }
.success-card h2 { font-size: 1.6rem; font-weight: 800; color: #0F172A; margin-bottom: 12px; }
.success-card p { color: #64748B; margin-bottom: 24px; }
.success-card button { background: linear-gradient(135deg, #EA580C, #D97706); color: white; border: none; padding: 14px 30px; border-radius: 12px; font-size: 1rem; font-weight: 800; cursor: pointer; font-family: inherit; }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

@media (max-width: 900px) {
  .hero-section { grid-template-columns: 1fr; padding: 40px; gap: 30px; }
  .hero-img-wrap { display: none; }
  .hero-title { font-size: 2rem; }
  .features-section, .plans-section { padding: 40px 20px; }
}
</style>
