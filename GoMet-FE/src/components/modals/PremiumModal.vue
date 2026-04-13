<template>
  <transition name="modal-fade">
    <div v-if="isOpen" ref="backdropRef" class="modal-backdrop" @click.self="closeModal" @keydown.esc.prevent="closeModal"
        role="dialog" aria-modal="true" tabindex="-1">
      <div class="modal-container">
        
        <button class="btn-close-simple" @click="closeModal" :title="t('common.close')">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>

        <header class="modal-header-elite">
          <div class="header-bg">
            <img src="https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=1000" class="img-fluid" :alt="t('common.premium_image')">
            <div class="header-overlay"></div>
          </div>
          <div class="header-text">
            <div class="badge-premium">{{ t('premium_modal.badge') }}</div>
            <h2 class="title" id="premium-title">{{ t('premium_modal.title') }}</h2>
            <p class="subtitle">{{ t('premium_modal.subtitle') }}</p>
          </div>
        </header>

        <div class="modal-scroll-body">
          <div class="premium-grid">
            
            <section class="info-section">
              <h3 class="label-heading">{{ t('premium_modal.benefits_heading') }}</h3>
              <div class="feature-table">
                <div class="t-row t-header">
                  <span>{{ t('premium_modal.feature_col') }}</span>
                  <span class="text-center">{{ t('premium_modal.free_col') }}</span>
                  <span class="text-center highlight-gold-text">{{ t('premium_modal.premium_col') }}</span>
                </div>
                <div class="t-row" v-for="(feat, i) in features" :key="i">
                  <div class="feat-box">
                    <span class="n-main">{{ feat.name }}</span>
                    <span class="n-sub">{{ feat.sub }}</span>
                  </div>
                  <div class="text-center val-txt">
                    <span v-if="feat.free === 'max3'">{{ t('premium_modal.max_three') }}</span>
                    <svg v-else-if="feat.free === true" class="icon-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                    <span v-else class="dash">—</span>
                  </div>
                  <div class="text-center val-txt premium-bg-cell">
                    <span v-if="feat.pro === 'unlimited'" class="unlimited-tag">{{ t('premium_modal.unlimited') }}</span>
                    <svg v-else class="icon-v gold-v" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
                  </div>
                </div>
              </div>
            </section>

            <section class="pay-section">
              <transition name="slide-up" mode="out-in">
                
                <div v-if="paymentStep === 'manage'" class="step-container manage-step" key="step-manage">
                  <h3 class="label-heading">{{ t('premium_modal.manage_heading') }}</h3>
                  
                  <div class="manage-card">
                    <div class="status-badge">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                      {{ t('premium_modal.active_status') }}
                    </div>
                    <h4>{{ t('premium_modal.plan_name') }}</h4>
                    
                    <p v-if="isLifetimePlan" class="expiry-info">
                      {{ t('premium_modal.expiry_label') }} <strong style="font-size: 1.3rem;">{{ t('premium_modal.lifetime') }}</strong>
                    </p>
                    
                    <p v-else class="expiry-info">
                      {{ t('premium_modal.expiry_date') }} <strong>{{ realExpiryDate }}</strong>
                    </p>
                  </div>

                  <div class="manage-benefits">
                    <b>{{ t('premium_modal.enjoying_label') }}</b>
                    <ul>
                      <li>{{ t('premium_modal.enjoying_no_ads') }}</li>
                      <li>{{ t('premium_modal.enjoying_ai') }}</li>
                      <li>{{ t('premium_modal.enjoying_storage') }}</li>
                    </ul>
                  </div>

                  <button v-if="!isLifetimePlan" class="btn-submit-premium" @click="paymentStep = 'select'">
                    {{ t('premium_modal.renew_more') }}
                  </button>
                </div>

                <div v-else-if="paymentStep === 'select'" class="step-container" key="step-select">
                  <h3 class="label-heading">{{ t('premium_modal.select_heading') }}</h3>
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
                    {{ t('premium_modal.pay_qr') }}
                  </button>
                  <button v-if="isPremiumUser" class="btn-outline-premium" @click="paymentStep = 'manage'">
                    {{ t('premium_modal.back_to_manage') }}
                  </button>
                  <p class="footer-note">{{ t('premium_modal.secure_note') }}</p>
                </div>

                <div v-else-if="paymentStep === 'qr'" class="step-container qr-step" key="step-qr">
                  <div class="qr-header-compact">
                    <button class="btn-back-step" @click="cancelPayment">❮ {{ t('recipe.back') }}</button>
                    <h3 class="step-title">{{ t('premium_modal.transfer_title') }}</h3>
                  </div>

                  <div class="qr-payment-card">
                    <div class="time-warning-box">
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                      <span>{{ t('premium_modal.pay_within') }}</span>
                      <strong class="countdown-timer" :class="{ 'time-danger': isTimeDanger }">{{ formattedTime }}</strong>
                    </div>

                    <p class="qr-instruction">{{ t('premium_modal.qr_instruction') }}</p>
                    
                    <div class="qr-code-wrapper">
                      <div class="qr-code-box" :class="{ 'qr-expired': isExpired }">
                        <img :src="vietQRUrl" :alt="t('common.payment_qr')" class="qr-img" />
                        <div v-if="isExpired" class="expired-qr-overlay">
                          <p>{{ t('premium_modal.qr_expired') }}</p>
                          <button class="btn-retry" @click="resetTimer">{{ t('common.retry') }}</button>
                        </div>
                      </div>
                    </div>

                    <div class="payment-amount">
                      <span class="amount">{{ formatPrice(activePlanData.price) }}</span>
                      <span class="currency">VNĐ</span>
                    </div>

                    <div class="payment-details-box">
                      <div class="detail-item">
                        <span class="label">{{ t('premium_modal.bank_label') }}</span>
                        <strong class="value">{{ BANK_NAME }}</strong>
                      </div>
                      <div class="detail-divider"></div>
                      <div class="detail-item">
                        <span class="label">{{ t('premium_modal.account_label') }}</span>
                        <div class="value-with-copy">
                          <strong>{{ ACCOUNT_NO }}</strong>
                          <button class="btn-copy" @click="copyText(ACCOUNT_NO)" :title="t('premium_modal.copy_account')">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg>
                          </button>
                        </div>
                      </div>
                      <div class="detail-divider"></div>
                      <div class="detail-item">
                        <span class="label">{{ t('premium_modal.transfer_content_label') }}</span>
                        <div class="value-with-copy">
                          <strong class="highlight-content">GOMET_PREMIUM</strong>
                        </div>
                      </div>
                    </div>

                    <div class="polling-status active" v-if="!isExpired">
                      <div class="ripple-loader"><div></div><div></div></div>
                      <span>{{ t('premium_modal.waiting_payment') }}</span>
                    </div>

                    <button class="btn-dev-mock" @click="simulatePaymentSuccess" :disabled="isLoading || isExpired">
                      {{ isLoading ? t('premium_modal.processing') : t('premium_modal.dev_success') }}
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
                  <h3 class="success-title">{{ t('premium_modal.success_title') }}</h3>
                  <p class="success-desc">{{ t('premium_modal.success_desc') }} <b>{{ t('premium_modal.plan_name') }}</b> {{ t('premium_modal.success_desc_suffix') }}</p>
                  <button class="btn-submit-premium" @click="finishUpgrade">
                    {{ t('premium_modal.finish') }}
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
import { useI18n } from 'vue-i18n'
import { formatLocaleDateTime } from '@/i18n'

const props = defineProps({ isOpen: Boolean })
const emit = defineEmits(['close', 'upgraded', 'start-test-timer'])
const { t } = useI18n()

const backdropRef = ref(null)
const userStore = useAuthStore()

// --- CẤU HÌNH NGÂN HÀNG ---
const BANK_ID = 'TCB' 
const BANK_NAME = 'Techcombank'
const ACCOUNT_NO = '9686179207' 
const ACCOUNT_NAME = 'TEN NGUOI NHAN'

const paymentStep = ref('select') 
const isLoading = ref(false)

// KKiểm tra xem User có đang là Premium không
const isPremiumUser = computed(() => {
  return userStore.user && (userStore.user.isPremium === 1 || userStore.user.role === 'premium')
})

// 🔥 BIẾN LƯU NGÀY HẾT HẠN THỰC TẾ VÀ TRẠNG THÁI TRỌN ĐỜI
const realExpiryDate = ref(t('common.loading'))
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
        realExpiryDate.value = t('premium_modal.lifetime_short');
      } else {
        isLifetimePlan.value = false;
        realExpiryDate.value = formatLocaleDateTime(dateObj, {
          day: '2-digit', month: '2-digit', year: 'numeric',
          hour: '2-digit', minute: '2-digit', second: '2-digit'
        });
      }
    } else {
      realExpiryDate.value = t('premium_modal.expiry_unknown');
    }
  } catch (error) {
    console.error("Lỗi lấy ngày hết hạn:", error);
    realExpiryDate.value = t('premium_modal.expiry_error');
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
const plans = computed(() => ([
  { id: 'test', name: t('premium_modal.plans.test.name'), desc: t('premium_modal.plans.test.desc'), price: 1000, unit: t('premium_modal.plans.test.unit'), tag: t('premium_modal.plans.test.tag') },
  { id: 'monthly', name: t('premium_modal.plans.monthly.name'), desc: t('premium_modal.plans.monthly.desc'), price: 25000, unit: t('premium_modal.plans.monthly.unit') },
  { id: 'yearly', name: t('premium_modal.plans.yearly.name'), desc: t('premium_modal.plans.yearly.desc'), price: 100000, unit: t('premium_modal.plans.yearly.unit'), tag: t('premium_modal.plans.yearly.tag') },
  { id: 'lifetime', name: t('premium_modal.plans.lifetime.name'), desc: t('premium_modal.plans.lifetime.desc'), price: 1000000, tag: t('premium_modal.plans.lifetime.tag') }
]))

const activePlanData = computed(() => plans.value.find(p => p.id === selectedPlan.value))
const formatPrice = (price) => price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".")

const getPlanTypeInt = (id) => {
  if (id === 'test') return 0;
  if (id === 'monthly') return 1;
  if (id === 'yearly') return 2;
  return 3;
}

const features = computed(() => ([
  { name: t('premium_modal.features.no_ads.name'), sub: t('premium_modal.features.no_ads.sub'), free: true, pro: true },
  { name: t('premium_modal.features.ai_chat.name'), sub: t('premium_modal.features.ai_chat.sub'), free: false, pro: true },
  { name: t('premium_modal.features.storage.name'), sub: t('premium_modal.features.storage.sub'), free: 'max3', pro: 'unlimited' },
  { name: t('premium_modal.features.mealplan.name'), sub: t('premium_modal.features.mealplan.sub'), free: false, pro: true },
  { name: t('premium_modal.features.compare.name'), sub: t('premium_modal.features.compare.sub'), free: false, pro: true },
]))

const vietQRUrl = computed(() => {
  const amount = activePlanData.value.price;
  const name = encodeURIComponent(ACCOUNT_NAME);
  return `https://img.vietqr.io/image/${BANK_ID}-${ACCOUNT_NO}-qr_only.png?amount=${amount}&addInfo=GOMET_PREMIUM&accountName=${name}`;
})

const generateQR = () => {
  if (!userStore.isAuthenticated) {
    toast.warn(t('toast.premium_upgrade_login'));
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
      toast.success(t('toast.premium_upgrade_ok'));

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
    toast.error(t('toast.premium_upgrade_fail'));
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
    toast.success(t('toast.copied_text', { text }));
  } catch (err) {
    toast.error(t('toast.copy_failed'));
  }
}
</script>

<style scoped lang="scss" src="./PremiumModal.scss"></style>