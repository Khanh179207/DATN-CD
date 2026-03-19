<template>
  <transition name="modal-fade">
    <div v-if="isOpen" ref="backdropRef" class="modal-backdrop" @click.self="closeModal" @keydown.esc.prevent="closeModal"
        role="dialog" aria-modal="true" tabindex="-1">
      <div class="modal-container">
        
        <button class="btn-close-simple" @click="closeModal" title="Đóng">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>

        <header class="modal-header-elite">
          <div class="header-bg">
            <img src="https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=1000" class="img-fluid" alt="Premium">
            <div class="header-overlay"></div>
          </div>
          <div class="header-text">
            <div class="badge-premium">GOMET PREMIUM</div>
            <h2 class="title" id="premium-title">Đánh thức tiềm năng đầu bếp</h2>
            <p class="subtitle">Không giới hạn tính năng - Trải nghiệm ẩm thực đỉnh cao</p>
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
              <transition name="slide-up" mode="out-in">
                
                <div v-if="paymentStep === 'manage'" class="step-container manage-step" key="step-manage">
                  <h3 class="label-heading">Trạng thái gói cước</h3>
                  
                  <div class="manage-card">
                    <div class="status-badge">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                      ĐANG HOẠT ĐỘNG
                    </div>
                    <h4>GoMet Premium</h4>
                    
                    <p v-if="isLifetimePlan" class="expiry-info">
                      Thời hạn sử dụng: <strong style="font-size: 1.3rem;">Vĩnh viễn (∞)</strong>
                    </p>
                    
                    <p v-else class="expiry-info">
                      Ngày hết hạn dự kiến: <strong>{{ realExpiryDate }}</strong>
                    </p>
                  </div>

                  <div class="manage-benefits">
                    <b>Bạn đang tận hưởng:</b>
                    <ul>
                      <li>Trải nghiệm duyệt web không quảng cáo</li>
                      <li>Trợ lý nấu ăn AI thông minh 24/7</li>
                      <li>Lưu trữ công thức không giới hạn</li>
                    </ul>
                  </div>

                  <button v-if="!isLifetimePlan" class="btn-submit-premium" @click="paymentStep = 'select'">
                    GIA HẠN THÊM
                  </button>
                </div>

                <div v-else-if="paymentStep === 'select'" class="step-container" key="step-select">
                  <h3 class="label-heading">Lựa chọn gói đăng ký</h3>
                  <div class="plan-cards">
                    <div 
                      v-for="plan in plans" :key="plan.id"
                      class="plan-card" :class="{ 'active': selectedPlan === plan.id }"
                      @click="selectedPlan = plan.id"
                    >
                      <div v-if="plan.tag" class="save-pill">{{ plan.tag }}</div>
                      <div class="plan-content">
                        <div class="radio-ui"><div class="dot" v-if="selectedPlan === plan.id"></div></div>
                        <div class="plan-info">
                          <span class="p-name">{{ plan.name }}</span>
                          <span class="p-desc">{{ plan.desc }}</span>
                        </div>
                      </div>
                      <div class="plan-price">
                        <span class="p-price">{{ formatPrice(plan.price) }}đ</span>
                        <span class="p-unit" v-if="plan.unit">/{{ plan.unit }}</span>
                      </div>
                    </div>
                  </div>
                  <button class="btn-submit-premium" @click="generateQR">
                    THANH TOÁN BẰNG QR CODE
                  </button>
                  <button v-if="isPremiumUser" class="btn-outline-premium" @click="paymentStep = 'manage'">
                    Quay lại Quản lý
                  </button>
                  <p class="footer-note">🔒 Giao dịch an toàn qua hệ thống Napas 24/7</p>
                </div>

                <div v-else-if="paymentStep === 'qr'" class="step-container qr-step" key="step-qr">
                  <div class="qr-header-compact">
                    <button class="btn-back-step" @click="cancelPayment">❮ Quay lại</button>
                    <h3 class="step-title">Chuyển khoản tự động</h3>
                  </div>

                  <div class="qr-payment-card">
                    <div class="time-warning-box">
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                      <span>Vui lòng thanh toán trong:</span>
                      <strong class="countdown-timer" :class="{ 'time-danger': isTimeDanger }">{{ formattedTime }}</strong>
                    </div>

                    <p class="qr-instruction">Mở ứng dụng ngân hàng và quét mã dưới đây</p>
                    
                    <div class="qr-code-wrapper">
                      <div class="qr-code-box" :class="{ 'qr-expired': isExpired }">
                        <img :src="vietQRUrl" alt="VietQR Code" class="qr-img" />
                        <div v-if="isExpired" class="expired-qr-overlay">
                          <p>Mã QR đã hết hạn</p>
                          <button class="btn-retry" @click="resetTimer">Làm mới mã</button>
                        </div>
                      </div>
                    </div>

                    <div class="payment-amount">
                      <span class="amount">{{ formatPrice(activePlanData.price) }}</span>
                      <span class="currency">VNĐ</span>
                    </div>

                    <div class="payment-details-box">
                      <div class="detail-item">
                        <span class="label">Ngân hàng</span>
                        <strong class="value">{{ BANK_NAME }}</strong>
                      </div>
                      <div class="detail-divider"></div>
                      <div class="detail-item">
                        <span class="label">Số tài khoản</span>
                        <div class="value-with-copy">
                          <strong>{{ ACCOUNT_NO }}</strong>
                          <button class="btn-copy" @click="copyText(ACCOUNT_NO)" title="Copy STK">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg>
                          </button>
                        </div>
                      </div>
                      <div class="detail-divider"></div>
                      <div class="detail-item">
                        <span class="label">Nội dung CK</span>
                        <div class="value-with-copy">
                          <strong class="highlight-content">GOMET_PREMIUM</strong>
                        </div>
                      </div>
                    </div>

                    <div class="polling-status active" v-if="!isExpired">
                      <div class="ripple-loader"><div></div><div></div></div>
                      <span>Hệ thống đang chờ nhận tiền...</span>
                    </div>

                    <button class="btn-dev-mock" @click="simulatePaymentSuccess" :disabled="isLoading || isExpired">
                      {{ isLoading ? 'Đang xử lý...' : '[Dev] Giả lập nhận tiền thành công' }}
                    </button>
                  </div>
                </div>

                <div v-else-if="paymentStep === 'success'" class="step-container success-step" key="step-success">
                  <div class="success-icon-wrap">
                    <svg class="checkmark-svg" viewBox="0 0 52 52">
                      <circle class="checkmark-circle" cx="26" cy="26" r="25" fill="none"/>
                      <path class="checkmark-check" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
                    </svg>
                  </div>
                  <h3 class="success-title">Nâng cấp thành công!</h3>
                  <p class="success-desc">Cảm ơn bạn. Trải nghiệm <b>GoMet Premium</b> của bạn đã sẵn sàng.</p>
                  <button class="btn-submit-premium" @click="finishUpgrade">
                    HOÀN TẤT
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

// --- CẤU HÌNH NGÂN HÀNG ---
const BANK_ID = 'TCB' 
const BANK_NAME = 'Techcombank'
const ACCOUNT_NO = '9686179207' 
const ACCOUNT_NAME = 'TEN CUA SEP' // Nhớ điền tên sếp nhé

const paymentStep = ref('select') 
const isLoading = ref(false)

// KKiểm tra xem User có đang là Premium không
const isPremiumUser = computed(() => {
  return userStore.user && (userStore.user.isPremium === 1 || userStore.user.role === 'premium')
})

// 🔥 BIẾN LƯU NGÀY HẾT HẠN THỰC TẾ VÀ TRẠNG THÁI TRỌN ĐỜI
const realExpiryDate = ref('Đang tải...')
const isLifetimePlan = ref(false)

// 🔥 HÀM GỌI API LẤY NGÀY HẾT HẠN
const fetchExpiryDate = async () => {
  if (!userStore.user?.accountID) return;
  
  try {
    const res = await api.get(`/api/payments/check-expiry/${userStore.user.accountID}`);
    if (res.data && res.data.success && res.data.endAt) {
      const dateObj = new Date(res.data.endAt);
      const year = dateObj.getFullYear();
      
      // Nếu năm hết hạn lớn hơn 2100 -> Coi như là gói Trọn đời
      if (year > 2100) {
        isLifetimePlan.value = true;
        realExpiryDate.value = 'Vĩnh viễn';
      } else {
        isLifetimePlan.value = false;
        realExpiryDate.value = dateObj.toLocaleString('vi-VN', {
          day: '2-digit', month: '2-digit', year: 'numeric',
          hour: '2-digit', minute: '2-digit', second: '2-digit'
        });
      }
    } else {
      realExpiryDate.value = 'Chưa xác định';
    }
  } catch (error) {
    console.error("Lỗi lấy ngày hết hạn:", error);
    realExpiryDate.value = 'Không thể tải dữ liệu';
  }
}

// 🔥 THEO DÕI KHI MỞ MODAL ĐỂ ĐIỀU HƯỚNG VÀ LẤY DỮ LIỆU
watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    if (isPremiumUser.value) {
      paymentStep.value = 'manage';
      fetchExpiryDate(); // Lấy ngày thật khi mở bảng quản lý
    } else {
      paymentStep.value = 'select';
    }
  } else {
    stopTimer();
  }
})

const closeModal = () => {
  cancelPayment()
  emit('close')
}

// --- LOGIC ĐẾM NGƯỢC 10 PHÚT ---
const TIMER_MINUTES = 10;
const timeLeft = ref(TIMER_MINUTES * 60); 
const isExpired = ref(false);
let timerInterval = null;

const formattedTime = computed(() => {
  const m = Math.floor(timeLeft.value / 60);
  const s = timeLeft.value % 60;
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
});

const isTimeDanger = computed(() => timeLeft.value <= 60 && timeLeft.value > 0);

const startTimer = () => {
  clearInterval(timerInterval); 
  isExpired.value = false;
  timeLeft.value = TIMER_MINUTES * 60;
  
  timerInterval = setInterval(() => {
    if (timeLeft.value > 0) {
      timeLeft.value--;
    } else {
      clearInterval(timerInterval);
      isExpired.value = true;
    }
  }, 1000);
};

const resetTimer = () => startTimer();
const stopTimer = () => clearInterval(timerInterval);

onMounted(() => {
  document.body.style.overflow = 'hidden'
  nextTick(() => backdropRef.value?.focus())
})

onUnmounted(() => {
  document.body.style.overflow = ''
  stopTimer() 
})

const selectedPlan = ref('yearly')
const plans = [
  { id: 'test', name: 'Gói Test 10s', desc: 'Dành cho Dev test luồng', price: 1000, unit: '10 giây', tag: 'DEV MODE' },
  { id: 'monthly', name: 'Gói Tháng', desc: 'Trải nghiệm linh hoạt', price: 25000, unit: 'tháng' },
  { id: 'yearly', name: 'Gói Năm', desc: 'Sử dụng bền vững', price: 100000, unit: 'năm', tag: 'TIẾTKIỆM' },
  { id: 'lifetime', name: 'Trọn Đời', desc: 'Mua 1 lần, dùng mãi mãi', price: 1000000, tag: 'BEST VALUE' }
]

const activePlanData = computed(() => plans.find(p => p.id === selectedPlan.value))
const formatPrice = (price) => price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".")

const getPlanTypeInt = (id) => {
  if (id === 'test') return 0;
  if (id === 'monthly') return 1;
  if (id === 'yearly') return 2;
  return 3;
}

const features = [
  { name: 'Không quảng cáo', sub: 'Trải nghiệm liền mạch', free: true, pro: true },
  { name: 'Chat AI (GoMet Assistant)', sub: 'Trợ lý gợi ý 24/7', free: false, pro: true },
  { name: 'Bộ sưu tập Món-Tủ', sub: 'Lưu trữ tinh hoa', free: 'max3', pro: 'unlimited' },
  { name: 'Kế hoạch ăn uống', sub: 'Thực đơn tự động', free: false, pro: true },
  { name: 'Tính năng so sánh', sub: 'Đánh giá dinh dưỡng', free: false, pro: true },
]

const vietQRUrl = computed(() => {
  const amount = activePlanData.value.price;
  const name = encodeURIComponent(ACCOUNT_NAME);
  return `https://img.vietqr.io/image/${BANK_ID}-${ACCOUNT_NO}-qr_only.png?amount=${amount}&addInfo=GOMET_PREMIUM&accountName=${name}`;
})

const generateQR = () => {
  if (!userStore.isAuthenticated) {
    toast.warn("Vui lòng đăng nhập để nâng cấp!");
    return;
  }
  paymentStep.value = 'qr';
  startTimer(); 
}

const cancelPayment = () => {
  paymentStep.value = 'select'
  stopTimer(); 
}

const simulatePaymentSuccess = async () => {
  if (isLoading.value || isExpired.value) return;
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
      stopTimer(); 
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
  // Sau khi nâng cấp xong, mở lại modal sẽ vào thẳng trang Quản lý
  paymentStep.value = 'manage'
  closeModal()
}

const copyText = async (text) => {
  try {
    await navigator.clipboard.writeText(text);
    toast.success("Đã sao chép: " + text);
  } catch (err) {
    toast.error("Không thể sao chép. Vui lòng thử lại!");
  }
}
</script>

<style scoped lang="scss" src="./PremiumModal.scss"></style>