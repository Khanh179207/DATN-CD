<template>
  <transition name="modal-fade">
    <div v-if="isOpen" ref="backdropRef" class="modal-backdrop" @click.self="closeModal" @keydown.esc.prevent="closeModal"
         role="dialog" aria-modal="true" tabindex="-1">
      <div class="modal-container">
        
        <button class="btn-close-simple" @click="closeModal" title="Đóng">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>

        <header class="modal-header-elite">
          <div class="header-bg">
            <img src="https://images.unsplash.com/photo-1556910103-1c02745aae4d?q=80&w=1000" class="img-fluid" alt="Premium">
            <div class="header-overlay"></div>
          </div>
          <div class="header-text">
            <div class="badge-premium">
              <svg viewBox="0 0 24 24" fill="currentColor" width="14" height="14" style="margin-right: 4px; display: inline-block; vertical-align: text-bottom;"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
              GOMET PREMIUM
            </div>
            <h2 class="title" id="premium-title">Đánh Thức Tiềm Năng Đầu Bếp</h2>
            <p class="subtitle">Không giới hạn tính năng - Trải nghiệm ẩm thực đỉnh cao</p>
          </div>
        </header>

        <div class="modal-scroll-body">
          <div class="premium-grid">
            
            <section class="info-section">
              <h3 class="label-heading">Đặc Quyền Dành Riêng Cho Bạn</h3>
              <div class="feature-table">
                <div class="t-row t-header">
                  <span>TÍNH NĂNG</span>
                  <span class="text-center text-muted">FREE</span>
                  <span class="text-center highlight-gold-text">PREMIUM</span>
                </div>
                <div class="t-row" v-for="(feat, i) in features" :key="i">
                  <div class="feat-box">
                    <span class="n-main">{{ feat.name }}</span>
                    <span class="n-sub">{{ feat.sub }}</span>
                  </div>
                  <div class="text-center val-txt text-muted">
                    <span v-if="feat.free === 'max3'" class="badge-free">Tối đa 3</span>
                    <svg v-else-if="feat.free === true" class="icon-v text-muted" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
                    <span v-else class="dash">—</span>
                  </div>
                  <div class="text-center val-txt premium-bg-cell">
                    <span v-if="feat.pro === 'unlimited'" class="unlimited-tag">VÔ HẠN</span>
                    <svg v-else class="icon-v gold-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
                  </div>
                </div>
              </div>
            </section>

            <section class="pay-section">
              <transition name="slide-up" mode="out-in">
                
                <div v-if="paymentStep === 'manage'" class="step-container manage-step" key="step-manage">
                  <h3 class="label-heading">Trạng Thái Gói Cước</h3>
                  
                  <div class="manage-card">
                    <div class="status-badge">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                      ĐANG HOẠT ĐỘNG
                    </div>
                    <h4>GoMet Premium</h4>
                    
                    <div class="expiry-info-box">
                      <p v-if="isLifetimePlan">
                        Thời hạn sử dụng: <strong class="lifetime-text">Vĩnh viễn (∞)</strong>
                      </p>
                      <p v-else>
                        Ngày hết hạn: <strong>{{ realExpiryDate }}</strong>
                      </p>
                    </div>
                  </div>

                  <div class="manage-benefits">
                    <b>Bạn đang tận hưởng:</b>
                    <ul>
                      <li><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg> Duyệt web không quảng cáo</li>
                      <li><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg> Trợ lý nấu ăn AI thông minh 24/7</li>
                      <li><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg> Lưu trữ công thức không giới hạn</li>
                    </ul>
                  </div>

                  <button v-if="!isLifetimePlan" class="btn-submit-premium" @click="paymentStep = 'select'">
                    GIA HẠN THÊM
                  </button>
                </div>

                <div v-else-if="paymentStep === 'select'" class="step-container" key="step-select">
                  <h3 class="label-heading">Lựa Chọn Gói Đăng Ký</h3>
                  <div class="plan-cards">
                    <div 
                      v-for="plan in plans" :key="plan.id"
                      class="plan-card" :class="{ 'active': selectedPlan === plan.id }"
                      @click="selectedPlan = plan.id"
                    >
                      <div v-if="plan.tag" class="save-pill">{{ plan.tag }}</div>
                      <div class="plan-content">
                        <div class="radio-ui">
                          <div class="radio-inner" v-if="selectedPlan === plan.id"></div>
                        </div>
                        <div class="plan-info">
                          <span class="p-name">{{ plan.name }}</span>
                          <span class="p-desc">{{ plan.desc }}</span>
                        </div>
                        <div class="plan-price-wrap">
                          <span class="p-price">{{ formatPrice(plan.price) }}<small>đ</small></span>
                          <span class="p-unit" v-if="plan.unit">/{{ plan.unit }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <div class="vnpay-action-wrapper">
                    <button class="btn-vnpay-pro" @click="handleVNPayPayment" :disabled="isLoading">
                      <div class="btn-content" v-if="!isLoading">
                        <svg class="wallet-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"></rect><line x1="1" y1="10" x2="23" y2="10"></line></svg>
                        <span>Thanh toán an toàn qua</span>
                        <div class="css-logo-vnpay">
                          <span class="txt-vn">VN</span><span class="txt-pay">PAY</span>
                        </div>
                      </div>
                      <div class="btn-content spinner-text" v-else>
                        <svg class="spin-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 12a9 9 0 1 1-6.219-8.56"></path></svg>
                        Đang kết nối hệ thống...
                      </div>
                    </button>

                    <button class="btn-dev-mock" @click="simulatePaymentSuccess" :disabled="isLoading">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4"/></svg>
                      {{ isLoading ? 'Đang xử lý...' : '[Dev] Giả lập nhận tiền thành công' }}
                    </button>
                  </div>

                  <button v-if="isPremiumUser" class="btn-text-back" @click="paymentStep = 'manage'">
                    &larr; Quay lại Quản lý
                  </button>
                  
                  <div class="secure-badge">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                    Giao dịch an toàn qua cổng thanh toán VNPay 
                  </div>
                </div>

                <div v-else-if="paymentStep === 'success'" class="step-container success-step" key="step-success">
                  <div class="success-icon-wrap">
                    <div class="success-glow"></div>
                    <svg class="checkmark-svg" viewBox="0 0 52 52">
                      <circle class="checkmark-circle" cx="26" cy="26" r="25" fill="none"/>
                      <path class="checkmark-check" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
                    </svg>
                  </div>
                  <h3 class="success-title">Nâng Cấp Thành Công!</h3>
                  <p class="success-desc">Cảm ơn bạn. Trải nghiệm <b>GoMet Premium</b> của bạn đã sẵn sàng sử dụng.</p>
                  <button class="btn-submit-premium" @click="finishUpgrade">
                    HOÀN TẤT & BẮT ĐẦU
                  </button>
                </div>

              </transition>
            </section>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import api from '@/services/api' 

const props = defineProps({ isOpen: Boolean })
const emit = defineEmits(['close', 'upgraded', 'start-test-timer'])

const backdropRef = ref(null)
const userStore = useAuthStore()

const paymentStep = ref('select') 
const isLoading = ref(false)

const isPremiumUser = computed(() => {
  return userStore.user && (userStore.user.isPremium === 1 || userStore.user.role === 'premium')
})

const realExpiryDate = ref('Đang tải...')
const isLifetimePlan = ref(false)

const fetchExpiryDate = async () => {
  if (!userStore.user?.accountID) return;
  
  // 1. Reset về trạng thái chờ để tránh hiện dữ liệu cũ của gói trước
  realExpiryDate.value = 'Đang cập nhật...';
  isLifetimePlan.value = false;
  
  try {
    const res = await api.get(`/api/payments/check-expiry/${userStore.user.accountID}`);
    
    // LOG ĐỂ KIỂM TRA (Sếp mở F12 xem cái này nhé)
    console.log("Dữ liệu ngày hết hạn từ Server:", res.data);

    if (res.data && res.data.success && res.data.endAt) {
      const dateObj = new Date(res.data.endAt);
      const year = dateObj.getFullYear();
      const currentYear = new Date().getFullYear();
      
      // 2. Logic kiểm tra vĩnh viễn chuẩn hơn: 
      // Thường là cộng 99 năm nên năm sẽ > năm hiện tại + 50
      if (year > currentYear + 50) { 
        isLifetimePlan.value = true;
        realExpiryDate.value = 'Vĩnh viễn';
      } else {
        isLifetimePlan.value = false;
        // Định dạng ngày giờ Việt Nam cho gói tháng/năm
        realExpiryDate.value = dateObj.toLocaleString('vi-VN', {
          day: '2-digit', 
          month: '2-digit', 
          year: 'numeric',
          hour: '2-digit', 
          minute: '2-digit'
        });
      }
    } else {
      realExpiryDate.value = 'Hết hạn hoặc chưa đăng ký';
      isLifetimePlan.value = false;
    }
  } catch (error) {
    console.error("Lỗi lấy ngày hết hạn:", error);
    realExpiryDate.value = 'Không thể tải dữ liệu';
  }
}

watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    fetchDynamicPricing();
    if (isPremiumUser.value) {
      paymentStep.value = 'manage';
      fetchExpiryDate();
    } else {
      paymentStep.value = 'select';
    }
  }
})

const closeModal = () => {
  paymentStep.value = isPremiumUser.value ? 'manage' : 'select'
  emit('close')
}

onMounted(() => {
  document.body.style.overflow = 'hidden'
  nextTick(() => backdropRef.value?.focus())
})

onUnmounted(() => {
  document.body.style.overflow = ''
})

const selectedPlan = ref('yearly')
const plans = ref([
  { id: 'test', name: 'Gói Test 10s', desc: 'Dành cho Dev test luồng', price: 1000, unit: '10 giây', tag: 'DEV MODE' },
  { id: 'monthly', name: 'Gói 1 Tháng', desc: 'Trải nghiệm linh hoạt', price: 49000, unit: 'tháng' },
  { id: 'yearly', name: 'Gói 1 Năm', desc: 'Sử dụng bền vững', price: 399000, unit: 'năm', tag: 'TIẾT KIỆM' },
  { id: 'lifetime', name: 'Gói Vĩnh Viễn', desc: 'Mua 1 lần dùng mãi mãi', price: 999000, unit: 'vĩnh viễn', tag: 'PREMIUM VIP' },
])

const fetchDynamicPricing = async () => {
  try {
    const res = await api.get('/api/system-config')
    const configs = Array.isArray(res.data) ? res.data : (res.data?.data || [])

    const getPrice = (key, defaultPrice) => {
      const item = configs.find(c => (c.configKey || c.ConfigKey) === key)
      return item && !isNaN(item.configValue || item.ConfigValue) ? Number(item.configValue || item.ConfigValue) : defaultPrice
    }

    plans.value[0].price = getPrice('PREMIUM_PRICE_TEST', 1000);
    plans.value[1].price = getPrice('PREMIUM_PRICE_1_MONTH', 49000);
    plans.value[2].price = getPrice('PREMIUM_PRICE_12_MONTHS', 399000);
    plans.value[3].price = getPrice('PREMIUM_PRICE_LIFETIME', 999000);
  } catch (error) {
    console.warn("Lấy bảng giá động thất bại, dùng giá mặc định.", error);
  }
}

const formatPrice = (price) => price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".")

const getPlanTypeInt = (id) => {
  if (id === 'test') return 0;
  if (id === 'monthly') return 1;
  if (id === 'yearly') return 2;
  if (id === 'lifetime') return 3;
  return 2;
}

const features = [
  { name: 'Không quảng cáo', sub: 'Trải nghiệm liền mạch', free: true, pro: true },
  { name: 'Chat AI (GoMet Assistant)', sub: 'Trợ lý gợi ý 24/7', free: false, pro: true },
  { name: 'Bộ sưu tập Món-Tủ', sub: 'Lưu trữ tinh hoa', free: 'max3', pro: 'unlimited' },
  { name: 'Kế hoạch ăn uống', sub: 'Thực đơn tự động', free: false, pro: true },
  { name: 'Tính năng so sánh', sub: 'Đánh giá dinh dưỡng', free: false, pro: true },
]

const handleVNPayPayment = async () => {
  if (!userStore.isAuthenticated || !userStore.user?.accountID) {
    toast.warn("Vui lòng đăng nhập để nâng cấp!");
    return;
  }
  
  isLoading.value = true;
  try {
    const payload = {
      accountId: userStore.user.accountID,
      planType: getPlanTypeInt(selectedPlan.value)
    };
    
    const res = await api.post('/api/payments/create-vnpay-payment', payload);
    
    if (res.data && res.data.url) {
      const paymentUrl = res.data.url;
      window.location.href = paymentUrl; 
    } else {
      toast.error(res.data?.message || "Không lấy được đường dẫn thanh toán. Vui lòng thử lại!");
    }
  } catch (error) {
    console.error("Lỗi khởi tạo VNPay:", error);
    toast.error(error.response?.data?.message || "Lỗi kết nối đến cổng thanh toán!");
  } finally {
    isLoading.value = false;
  }
}

const simulatePaymentSuccess = async () => {
  if (isLoading.value) return;
  isLoading.value = true;
  
  try {
    const res = await api.post('/api/payments/force-upgrade', {
      accountId: userStore.user.accountID,
      planType: getPlanTypeInt(selectedPlan.value)
    });
    
    if (res.data && res.data.success) {
      toast.success("Đã nâng cấp Premium thành công!");

      const localUser = JSON.parse(localStorage.getItem('user'));
      if (localUser) {
        localUser.isPremium = 1;
        localUser.role = 'premium'; 
        localStorage.setItem('user', JSON.stringify(localUser));
      }

      userStore.user = { 
        ...userStore.user, 
        isPremium: 1,
        role: 'premium'
      };

      paymentStep.value = 'success';
    } 
  } catch (error) {
    toast.error("Lỗi cập nhật quyền hạn!");
  } finally {
    isLoading.value = false;
  }
}

const finishUpgrade = () => {
  if (selectedPlan.value === 'test') {
    emit('start-test-timer');
  }
  emit('upgraded')
  paymentStep.value = 'manage'
  closeModal()
}
</script>

<style scoped lang="scss">
/* ----- BACKDROP & CONTAINER ----- */
.modal-backdrop {
  position: fixed; inset: 0;
  background: rgba(15, 23, 42, 0.75); backdrop-filter: blur(10px);
  display: flex; justify-content: center; align-items: center;
  z-index: 9999; padding: 20px;
}

.modal-container {
  background: #ffffff; border-radius: 28px;
  width: 100%; max-width: 1050px; max-height: 92vh;
  display: flex; flex-direction: column; position: relative;
  overflow: hidden; box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.4);
}

.btn-close-simple {
  position: absolute; top: 20px; right: 20px;
  width: 44px; height: 44px; border-radius: 50%; border: none;
  background: rgba(255, 255, 255, 0.15); color: white;
  z-index: 20; display: flex; justify-content: center; align-items: center;
  cursor: pointer; backdrop-filter: blur(8px); transition: all 0.3s ease;
  &:hover { background: rgba(255, 255, 255, 0.3); transform: rotate(90deg) scale(1.1); }
}

/* ----- HEADER SANG TRỌNG ----- */
.modal-header-elite {
  position: relative; padding: 50px 48px; color: white;
  .header-bg {
    position: absolute; inset: 0; z-index: 1;
    img { width: 100%; height: 100%; object-fit: cover; }
    .header-overlay {
      position: absolute; inset: 0;
      background: linear-gradient(90deg, #0f172a 0%, rgba(15, 23, 42, 0.85) 40%, rgba(15, 23, 42, 0.1) 100%);
    }
  }
  .header-text { position: relative; z-index: 2; max-width: 60%; }
  .badge-premium {
    display: inline-flex; align-items: center; gap: 6px;
    background: linear-gradient(135deg, #f59e0b, #ea580c);
    color: white; font-size: 0.75rem; font-weight: 800;
    padding: 8px 16px; border-radius: 100px; letter-spacing: 1.5px;
    margin-bottom: 20px; box-shadow: 0 4px 15px rgba(234, 88, 12, 0.4);
  }
  .title {
    font-family: 'Playfair Display', serif; font-size: 2.4rem; font-weight: 800;
    line-height: 1.2; margin: 0 0 12px 0; text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  }
  .subtitle { font-size: 1.05rem; color: #e2e8f0; opacity: 0.95; margin: 0; }
}

/* ----- BODY & GRID ----- */
.modal-scroll-body { overflow-y: auto; overflow-x: hidden; background: #f8fafc; }
.premium-grid { display: grid; grid-template-columns: 1.15fr 1fr; gap: 0; }
.info-section { padding: 48px; background: #ffffff; border-right: 1px solid #f1f5f9; }
.pay-section { padding: 48px; position: relative; }
.label-heading { font-size: 1.2rem; font-weight: 800; color: #0f172a; margin: 0 0 28px 0; }

/* ----- BẢNG TÍNH NĂNG ----- */
.feature-table {
  display: flex; flex-direction: column; gap: 10px;
  .t-row {
    display: grid; grid-template-columns: 2fr 0.8fr 1fr; align-items: center;
    padding: 16px 20px; border-radius: 16px; border: 1px solid transparent;
    transition: all 0.2s;
  }
  .t-row:not(.t-header):hover { background: #f8fafc; border-color: #e2e8f0; transform: translateX(4px); }
  .t-header { font-size: 0.8rem; font-weight: 800; color: #94a3b8; letter-spacing: 1.5px; padding-bottom: 12px; border-bottom: 2px solid #f1f5f9; }
  .text-center { text-align: center; }
  .feat-box { display: flex; flex-direction: column; gap: 4px; }
  .n-main { font-weight: 700; color: #1e293b; font-size: 1rem; }
  .n-sub { font-size: 0.85rem; color: #64748b; }
  .badge-free { font-size: 0.75rem; background: #e2e8f0; padding: 4px 10px; border-radius: 6px; font-weight: 700; color: #475569; }
  .icon-v { width: 22px; height: 22px; margin: 0 auto; }
  .premium-bg-cell { background: linear-gradient(180deg, #fff7ed 0%, #ffedd5 100%); padding: 14px 0; border-radius: 12px; box-shadow: inset 0 2px 4px rgba(255,255,255,0.5); }
  .gold-v { color: #ea580c; }
  .unlimited-tag { font-size: 0.75rem; font-weight: 800; color: #ea580c; background: white; padding: 6px 12px; border-radius: 100px; box-shadow: 0 2px 8px rgba(234, 88, 12, 0.15); }
}

/* ----- THẺ GÓI CƯỚC ----- */
.plan-cards { display: flex; flex-direction: column; gap: 16px; margin-bottom: 28px; }
.plan-card {
  position: relative; background: #ffffff; border: 2px solid #e2e8f0; border-radius: 18px;
  padding: 18px 24px; cursor: pointer; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex; justify-content: space-between; align-items: center;
  &:hover { border-color: #cbd5e1; background: #f8fafc; }
  
  &.active {
    border-color: #ea580c; background: #fff7ed;
    box-shadow: 0 0 0 1px #ea580c, 0 10px 25px -5px rgba(234, 88, 12, 0.2);
    transform: scale(1.02); z-index: 2;
  }
  
  .save-pill {
    position: absolute; top: -12px; right: 24px; background: linear-gradient(135deg, #ef4444, #dc2626);
    color: white; font-size: 0.7rem; font-weight: 800; padding: 4px 12px; border-radius: 100px;
    box-shadow: 0 4px 10px rgba(239, 68, 68, 0.4); letter-spacing: 0.5px;
  }
  .plan-content { display: flex; align-items: center; gap: 20px; width: 100%; }
  .radio-ui {
    width: 24px; height: 24px; border-radius: 50%; border: 2px solid #cbd5e1;
    display: flex; align-items: center; justify-content: center; background: white; transition: 0.2s;
  }
  &.active .radio-ui { border-color: #ea580c; .radio-inner { width: 12px; height: 12px; background: #ea580c; border-radius: 50%; } }
  .plan-info { display: flex; flex-direction: column; flex-grow: 1; }
  .p-name { font-weight: 800; color: #0f172a; font-size: 1.05rem; margin-bottom: 2px; }
  .p-desc { font-size: 0.85rem; color: #64748b; }
  .plan-price-wrap { text-align: right; }
  .p-price { font-weight: 800; font-size: 1.35rem; color: #0f172a; small { font-size: 0.85rem; margin-left: 2px; color: #64748b; } }
  &.active .p-price { color: #ea580c; small { color: #ea580c; } }
  .p-unit { display: block; font-size: 0.8rem; color: #94a3b8; font-weight: 600; margin-top: -2px; }
}

/* ----- NÚT VNPAY MỚI (CHỐNG LỖI ẢNH) ----- */
.vnpay-action-wrapper { display: flex; flex-direction: column; gap: 14px; }

.btn-vnpay-pro {
  width: 100%; padding: 16px; border: none; border-radius: 16px; cursor: pointer;
  background: linear-gradient(135deg, #00508E 0%, #0068B3 100%);
  color: white; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 20px -6px rgba(0, 80, 142, 0.5);
  position: relative; overflow: hidden;
  
  &::after {
    content: ''; position: absolute; top: 0; left: -100%; width: 50%; height: 100%;
    background: linear-gradient(to right, rgba(255,255,255,0) 0%, rgba(255,255,255,0.2) 50%, rgba(255,255,255,0) 100%);
    transform: skewX(-25deg); transition: 0.5s;
  }
  
  &:hover:not(:disabled) {
    transform: translateY(-3px); box-shadow: 0 12px 25px -6px rgba(0, 80, 142, 0.6);
    &::after { left: 150%; }
  }
  
  &:disabled { opacity: 0.8; cursor: not-allowed; filter: grayscale(50%); }
  
  .btn-content { display: flex; align-items: center; justify-content: center; gap: 12px; font-weight: 700; font-size: 1rem; }
  .wallet-icon { width: 22px; height: 22px; opacity: 0.9; }
  
  .css-logo-vnpay {
    background: #ffffff; padding: 4px 10px; border-radius: 6px;
    font-family: 'Arial', sans-serif; font-weight: 900; font-size: 1.1rem; letter-spacing: -0.5px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1); display: inline-flex;
    .txt-vn { color: #00508E; } 
    .txt-pay { color: #ED1B24; }
  }
  .spin-icon { width: 22px; height: 22px; animation: spin 1s linear infinite; }
}

.btn-dev-mock {
  width: 100%; padding: 14px; background: #f8fafc; border: 2px dashed #cbd5e1;
  border-radius: 14px; color: #64748b; font-weight: 700; font-size: 0.9rem;
  display: flex; align-items: center; justify-content: center; gap: 8px; cursor: pointer; transition: 0.2s;
  &:hover { background: #f1f5f9; color: #334155; border-color: #94a3b8; transform: translateY(-1px); }
  svg { width: 18px; height: 18px; }
}

.btn-submit-premium {
  width: 100%; padding: 18px; background: linear-gradient(135deg, #ea580c, #f59e0b);
  color: white; border: none; border-radius: 16px; font-weight: 800; font-size: 1.05rem; letter-spacing: 0.5px;
  cursor: pointer; transition: 0.3s; box-shadow: 0 10px 25px -6px rgba(234, 88, 12, 0.5);
  &:hover { transform: translateY(-3px); box-shadow: 0 15px 30px -6px rgba(234, 88, 12, 0.6); filter: brightness(1.05); }
}

.btn-text-back {
  background: transparent; border: none; color: #64748b; font-weight: 700; font-size: 0.95rem;
  width: 100%; margin-top: 20px; cursor: pointer; transition: 0.2s;
  &:hover { color: #0f172a; transform: translateX(-4px); }
}

.secure-badge {
  margin-top: 28px; display: flex; align-items: center; justify-content: center; gap: 8px;
  font-size: 0.8rem; color: #64748b; font-weight: 600; background: #f1f5f9; padding: 10px; border-radius: 10px;
  svg { width: 16px; height: 16px; color: #10b981; }
}

/* ----- CÁC TRẠNG THÁI QUẢN LÝ / THÀNH CÔNG ----- */
.manage-card { background: #fff7ed; border: 2px solid #ffedd5; border-radius: 20px; padding: 28px; margin-bottom: 28px; }
.status-badge { display: inline-flex; align-items: center; gap: 6px; background: #dcfce7; color: #16a34a; padding: 6px 12px; border-radius: 100px; font-size: 0.8rem; font-weight: 800; margin-bottom: 16px; }
.manage-card h4 { margin: 0 0 20px 0; font-size: 1.6rem; color: #0f172a; font-weight: 800; }
.expiry-info-box { border-top: 1px solid rgba(234, 88, 12, 0.15); padding-top: 20px; p { margin: 0; font-size: 0.95rem; color: #64748b; } strong { color: #0f172a; font-size: 1.1rem; margin-left: 6px; } .lifetime-text { color: #ea580c; font-size: 1.25rem; } }
.manage-benefits { margin-bottom: 36px; b { display: block; margin-bottom: 16px; color: #0f172a; font-size: 1.05rem; } ul { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 14px; } li { display: flex; align-items: center; gap: 12px; font-size: 1rem; color: #475569; font-weight: 600; } svg { width: 20px; height: 20px; color: #10b981; } }

.success-step { text-align: center; padding: 60px 20px; }
.success-icon-wrap { position: relative; width: 90px; height: 90px; margin: 0 auto 32px; display: flex; align-items: center; justify-content: center; }
.success-glow { position: absolute; inset: -15px; background: radial-gradient(circle, rgba(16, 185, 129, 0.25) 0%, transparent 70%); border-radius: 50%; animation: pulseGlow 2s infinite alternate; }
.checkmark-svg { width: 100%; height: 100%; position: relative; z-index: 2; }
.checkmark-circle { stroke: #10b981; stroke-width: 3; stroke-dasharray: 166; stroke-dashoffset: 166; animation: stroke 0.6s cubic-bezier(0.65, 0, 0.45, 1) forwards; }
.checkmark-check { stroke: #10b981; stroke-width: 4; stroke-linecap: round; stroke-dasharray: 48; stroke-dashoffset: 48; animation: stroke 0.3s cubic-bezier(0.65, 0, 0.45, 1) 0.6s forwards; }
.success-title { font-size: 2rem; font-weight: 800; color: #0f172a; margin-bottom: 16px; }
.success-desc { font-size: 1.1rem; color: #64748b; margin-bottom: 48px; line-height: 1.6; }

/* ----- ANIMATIONS & RESPONSIVE ----- */
@keyframes spin { 100% { transform: rotate(360deg); } }
@keyframes pulseGlow { 0% { transform: scale(0.8); opacity: 0.5; } 100% { transform: scale(1.2); opacity: 0.8; } }
@keyframes stroke { 100% { stroke-dashoffset: 0; } }
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.4s ease; .modal-container { transition: transform 0.4s cubic-bezier(0.16, 1, 0.3, 1); } }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; .modal-container { transform: scale(0.95) translateY(30px); } }
.slide-up-enter-active, .slide-up-leave-active { transition: all 0.3s ease; }
.slide-up-enter-from { opacity: 0; transform: translateX(30px); }
.slide-up-leave-to { opacity: 0; transform: translateX(-30px); }

@media (max-width: 900px) {
  .premium-grid { grid-template-columns: 1fr; }
  .modal-header-elite { padding: 40px 24px; text-align: center; .header-text { max-width: 100%; margin: 0 auto; } .title { font-size: 2rem; } }
  .btn-close-simple { background: rgba(0,0,0,0.5); top: 16px; right: 16px; }
  .info-section, .pay-section { padding: 32px 24px; }
}
</style>