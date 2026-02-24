<template>
  <transition name="modal-fade">
    <div v-if="isOpen" class="modal-backdrop" @click.self="closeModal">
      <div class="modal-container">
        
        <button class="btn-close-simple" @click="closeModal">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>

        <header class="modal-header-simple">
          <div class="header-bg">
            <img src="https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=1000" class="img-fluid">
            <div class="header-overlay"></div>
          </div>
          <div class="header-text">
            <div class="badge-premium">GOMET PREMIUM</div>
            <h2 class="title">Đánh thức tiềm năng đầu bếp</h2>
            <p class="subtitle">Nâng cấp đặc quyền - Tận hưởng trọn bộ tính năng thông minh nhất</p>
          </div>
        </header>

        <div class="modal-scroll-body">
          <div class="premium-grid">
            
            <section class="info-section">
              <h3 class="label-heading">Đặc quyền dành riêng cho bạn</h3>
              <div class="feature-table">
                <div class="t-row t-header">
                  <span>TÍNH NĂNG</span>
                  <span class="text-center">FREE</span>
                  <span class="text-center highlight-gold-text">PREMIUM</span>
                </div>

                <div class="t-row" v-for="(feat, i) in features" :key="i">
                  <div class="feat-box">
                    <span class="n-main">{{ feat.name }}</span>
                    <span class="n-sub">{{ feat.sub }}</span>
                  </div>
                  <div class="text-center val-txt">
                    <span v-if="feat.free === 'max3'">Tối đa 3</span>
                    <svg v-else-if="feat.free === true" class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                    <span v-else class="dash">—</span>
                  </div>
                  <div class="text-center val-txt premium-bg-cell">
                    <span v-if="feat.pro === 'unlimited'" class="unlimited-tag">KHÔNG GIỚI HẠN</span>
                    <svg v-else class="icon-v gold-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
                  </div>
                </div>
              </div>
            </section>

            <section class="pay-section">
              <h3 class="label-heading">Lựa chọn gói đăng ký</h3>
              <div class="plan-cards">
                <div 
                  v-for="plan in plans" :key="plan.id"
                  class="plan-card" :class="{ 'active': selectedPlan === plan.id }"
                  @click="selectedPlan = plan.id"
                >
                  <div v-if="plan.id === 'yearly'" class="save-pill">TIẾT KIỆM 20%</div>
                  <div class="plan-content">
                    <div class="radio-ui"><div class="dot" v-if="selectedPlan === plan.id"></div></div>
                    <div class="plan-info">
                      <span class="p-name">{{ plan.name }}</span>
                      <span class="p-desc">{{ plan.desc }}</span>
                    </div>
                  </div>
                  <div class="plan-price">
                    <span class="p-price">{{ plan.price }}đ</span>
                    <span class="p-unit">/{{ plan.unit }}</span>
                  </div>
                </div>
              </div>

              <div class="pay-form">
                <div class="pay-tabs">
                  <button class="v-tab active">💳 Thẻ tín dụng</button>
                  <button class="v-tab">🧧 MoMo</button>
                </div>
                <div class="inputs-container">
                   <input type="text" placeholder="Số thẻ" class="v-input">
                   <div class="row-inputs">
                     <input type="text" placeholder="MM/YY" class="v-input">
                     <input type="password" placeholder="CVC" class="v-input">
                   </div>
                </div>
                <button class="btn-submit-premium" @click="handlePay">
                  KÍCH HOẠT PREMIUM ({{ activePrice }}đ)
                </button>
                <p class="footer-note">🔒 Bảo mật SSL 256-bit</p>
              </div>
            </section>

          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed } from 'vue'

defineProps({ isOpen: Boolean })
const emit = defineEmits(['close'])
const closeModal = () => emit('close')

const selectedPlan = ref('yearly')
const plans = [
  { id: 'monthly', name: 'Gói Tháng', desc: 'Trải nghiệm linh hoạt', price: '25.000', unit: 'tháng' },
  { id: 'yearly', name: 'Gói Năm', desc: 'Sử dụng bền vững', price: '240.000', unit: 'năm' }
]

const activePrice = computed(() => plans.find(p => p.id === selectedPlan.value).price)

const features = [
  { name: 'Không quảng cáo', sub: 'Trải nghiệm liền mạch', free: true, pro: true },
  { name: 'Chat AI (ChefBot)', sub: 'Trợ lý gợi ý 24/7', free: false, pro: true },
  { name: 'Bộ sưu tập Món-Tủ', sub: 'Lưu trữ tinh hoa', free: 'max3', pro: 'unlimited' },
  { name: 'Kế hoạch ăn uống', sub: 'Thực đơn tự động', free: false, pro: true },
  { name: 'Danh sách đi chợ', sub: 'Tự động báo giá', free: false, pro: true },
  { name: 'Bảng xếp hạng Hot', sub: 'Top món xu hướng', free: false, pro: true },
]

const handlePay = () => alert('Đang kết nối thanh toán...')
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Quicksand:wght@500;600;700;800&display=swap');

/* Reset toàn bộ font về Quicksand cho hiện đại */
* { font-family: 'Quicksand', sans-serif; }

.modal-backdrop {
  position: fixed; inset: 0; z-index: 9999;
  background: rgba(0, 0, 0, 0.7); backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center; padding: 20px;
}

.modal-container {
  width: 100%; max-width: 880px; background: #fff; border-radius: 20px;
  position: relative; overflow: hidden; max-height: 85vh;
  display: flex; flex-direction: column; box-shadow: 0 25px 50px rgba(0,0,0,0.3);
}

.modal-scroll-body { overflow-y: auto; flex: 1; }

.btn-close-simple {
  position: absolute; top: 15px; right: 15px; z-index: 100;
  background: rgba(255,255,255,0.2); color: #fff; border: none;
  width: 32px; height: 32px; border-radius: 50%; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
}

/* Header */
.modal-header-simple { height: 180px; position: relative; display: flex; align-items: center; justify-content: center; text-align: center; }
.header-bg { position: absolute; inset: 0; z-index: 1; }
.img-fluid { width: 100%; height: 100%; object-fit: cover; }
.header-overlay { position: absolute; inset: 0; background: linear-gradient(to bottom, transparent, #1c1917); }
.header-text { position: relative; z-index: 2; color: #fff; padding-top: 20px; }
.badge-premium { background: #f59e0b; font-size: 0.65rem; font-weight: 800; padding: 3px 10px; border-radius: 10px; display: inline-block; margin-bottom: 8px; }
.title { font-size: 1.8rem; font-weight: 800; margin: 0; }
.subtitle { font-size: 0.9rem; opacity: 0.8; margin-top: 5px; }

/* Grid */
.premium-grid { padding: 30px 40px; display: grid; grid-template-columns: 1.1fr 0.9fr; gap: 40px; }
.label-heading { font-size: 1.1rem; font-weight: 800; color: #1c1917; margin-bottom: 20px; }

/* Table */
.feature-table { width: 100%; border-right: 1px solid #f3f4f6; padding-right: 20px; }
.t-row { display: grid; grid-template-columns: 2.2fr 1fr 1.2fr; padding: 12px 0; border-bottom: 1px solid #f9fafb; align-items: center; }
.t-header { font-weight: 800; font-size: 0.7rem; color: #9ca3af; border-bottom: 1px solid #1c1917; }
.n-main { font-weight: 700; font-size: 0.9rem; color: #1c1917; display: block; }
.n-sub { font-size: 0.75rem; color: #6b7280; }
.val-txt { color: #d1d5db; }
.premium-bg-cell { background: #fffbeb; border-radius: 8px; padding: 8px 0; }
.highlight-gold-text { color: #d97706; }
.unlimited-tag { color: #b45309; font-weight: 800; font-size: 0.65rem; }
.icon-v { width: 16px; height: 16px; }
.gold-v { color: #d97706; }

/* Checkout */
.plan-card {
  border: 1.5px solid #e5e7eb; border-radius: 12px; padding: 14px;
  display: flex; justify-content: space-between; align-items: center;
  cursor: pointer; margin-bottom: 12px; position: relative; transition: 0.2s;
}
.plan-card.active { border-color: #f59e0b; background: #fffbeb; }
.save-pill { position: absolute; top: -10px; right: 15px; background: #10b981; color: #fff; font-size: 0.6rem; font-weight: 800; padding: 2px 8px; border-radius: 5px; }
.plan-content { display: flex; align-items: center; gap: 10px; }
.radio-ui { width: 16px; height: 16px; border: 1.5px solid #d1d5db; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.active .radio-ui { border-color: #f59e0b; }
.dot { width: 8px; height: 8px; background: #f59e0b; border-radius: 50%; }
.p-name { font-weight: 700; font-size: 0.9rem; display: block; }
.p-desc { font-size: 0.75rem; color: #6b7280; }
.p-price { font-weight: 800; font-size: 1.1rem; }
.p-unit { font-size: 0.75rem; color: #9ca3af; }

.pay-tabs { display: flex; gap: 8px; margin-top: 20px; }
.v-tab { flex: 1; padding: 8px; border-radius: 8px; border: 1.5px solid #e5e7eb; font-weight: 700; font-size: 0.8rem; cursor: pointer; background: #fff; }
.v-tab.active { background: #1c1917; color: #fff; border-color: #1c1917; }
.inputs-container { background: #f9fafb; padding: 15px; border-radius: 10px; margin-top: 15px; }
.v-input { width: 100%; padding: 10px; border: 1.5px solid #e5e7eb; border-radius: 6px; margin-bottom: 8px; font-size: 0.85rem; outline: none; }
.row-inputs { display: flex; gap: 10px; }
.btn-submit-premium { width: 100%; padding: 15px; background: #1c1917; color: #fff; border: none; border-radius: 10px; margin-top: 15px; font-weight: 700; cursor: pointer; transition: 0.2s; }
.btn-submit-premium:hover { background: #f97316; transform: translateY(-2px); }
.footer-note { text-align: center; font-size: 0.65rem; color: #9ca3af; margin-top: 10px; }

@media (max-width: 800px) { .premium-grid { grid-template-columns: 1fr; padding: 20px; } .feature-table { border-right: none; } }
</style>