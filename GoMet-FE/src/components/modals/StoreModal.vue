<template>
  <div v-if="isOpen" class="modal-backdrop" @click.self="closeModal">
    <div class="store-modal-content">
      <div class="store-header">
        <div class="header-title">
          <h2>GoMet<span class="highlight">Store</span></h2>
          <p>Dùng điểm GoMetCoin để nâng cấp trải nghiệm của bạn</p>
        </div>
        <div class="current-coin">
          <span>Khả dụng:</span>
          <div class="coin-badge">
            <span class="icon">✨</span>
            <span class="amount">{{ authStore.user?.point || 0 }}</span>
          </div>
        </div>
        <button class="btn-close" @click="closeModal">×</button>
      </div>

      <div class="store-body">
        <div class="info-banner">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="16" x2="12" y2="12"></line>
            <line x1="12" y1="8" x2="12.01" y2="8"></line>
          </svg>
          <div class="banner-text">
            <strong>Cách nhận GoMetCoin:</strong> Tích cực chia sẻ công thức (được duyệt) hoặc chiến thắng trong các Sự Kiện (Event) để nhận thưởng!
          </div>
        </div>

        <h3 class="section-title">Gói Premium (Xem không giới hạn & Trợ lý AI)</h3>
        <div class="packages-grid">
          
          <div class="package-card standard">
            <div class="card-head">
              <h4>1 Tháng</h4>
            </div>
            <div class="card-price">
              <span class="val">50</span>
              <span class="unit">GoMetCoin</span>
            </div>
            <ul class="features">
              <li>✅ Mở khóa Mọi Công Thức</li>
              <li>✅ Trợ lý Gomet AI KHÔNG lưu trễ</li>
              <li>✅ Xem bài viết không mất lượt</li>
            </ul>
            <button class="btn-buy" :disabled="authStore.user?.point < 50 || isPremium" @click="buyPremium(1)">
              {{ isPremium ? 'Đã kích hoạt' : 'Mua Gói' }}
            </button>
          </div>

          <div class="package-card best-value">
            <div class="popular-badge">TIẾT KIỆM NHẤT</div>
            <div class="card-head">
              <h4>1 Năm</h4>
            </div>
            <div class="card-price">
              <span class="val">200</span>
              <span class="unit">GoMetCoin</span>
            </div>
            <ul class="features">
              <li>✅ Mọi quyền lợi của Gói 1 Tháng</li>
              <li>🔥 Tiết kiệm tới 65% số xu</li>
              <li>💎 Nhãn Premium đặc biệt</li>
            </ul>
            <button class="btn-buy highlight" :disabled="authStore.user?.point < 200 || isPremium" @click="buyPremium(12)">
              {{ isPremium ? 'Đã kích hoạt' : 'Mua Gói' }}
            </button>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { toast } from '@/composables/useToast'

const props = defineProps({
  isOpen: { type: Boolean, default: false }
})

const emit = defineEmits(['close'])

const authStore = useAuthStore()
const isPremium = computed(() => {
  const role = String(authStore.user?.role || '').toLowerCase();
  return role === 'premium' || ['true', '1', 1, true].includes(authStore.user?.isPremium);
});

const closeModal = () => emit('close')

const buyPremium = async (months) => {
  try {
    const res = await api.post(`/api/users/${authStore.user.accountID}/buy-premium`, { months })
    toast.success(res.data.message || 'Mở khóa Premium thành công!')
    authStore.user.point = res.data.pointRemaining
    authStore.user.isPremium = res.data.isPremium
    authStore.setUser(authStore.user)
    closeModal()
  } catch (err) {
    if (err.response?.data?.code === 'INSUFFICIENT_POINT') {
      toast.error('Bạn không đủ GoMetCoin để thao tác!')
    } else {
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra, thử lại sau.')
    }
  }
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed; inset: 0; z-index: 100000;
  background: rgba(15, 23, 42, 0.7); backdrop-filter: blur(8px);
  display: flex; justify-content: center; align-items: center; padding: 20px;
}

.store-modal-content {
  background: #ffffff; width: 100%; max-width: 700px;
  border-radius: 24px; overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25);
  animation: scale-up 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.store-header {
  padding: 24px 32px; background: #0f172a; color: white;
  display: flex; justify-content: space-between; align-items: flex-start;
  position: relative;
}

.header-title h2 { margin: 0 0 8px; font-size: 28px; font-weight: 900; }
.header-title .highlight { color: #ea580c; }
.header-title p { margin: 0; color: #94a3b8; font-size: 14px; }

.current-coin {
  background: rgba(255,255,255,0.1); padding: 8px 16px; border-radius: 100px;
  display: flex; flex-direction: column; align-items: center; gap: 4px; border: 1px solid rgba(255,255,255,0.2);
}
.current-coin span { font-size: 12px; color: #cbd5e1; }
.coin-badge { display: flex; align-items: center; gap: 6px; }
.coin-badge .icon { font-size: 18px; }
.coin-badge .amount { font-size: 20px; font-weight: 900; color: #f59e0b; }

.btn-close {
  position: absolute; top: 20px; right: 20px;
  background: transparent; color: #94a3b8; border: none; font-size: 28px;
  cursor: pointer; transition: 0.2s;
}
.btn-close:hover { color: white; transform: rotate(90deg); }

.store-body { padding: 32px; }

.info-banner {
  background: #fff7ed; border: 1px solid #fed7aa; padding: 16px;
  border-radius: 12px; display: flex; gap: 16px; align-items: flex-start;
  color: #9a3412; margin-bottom: 32px;
}
.info-banner svg { flex-shrink: 0; width: 20px; height: 20px; margin-top: 2px; }
.banner-text { font-size: 14px; line-height: 1.5; }

.section-title { margin: 0 0 20px; font-size: 18px; font-weight: 800; color: #1e293b; }

.packages-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }

.package-card {
  border: 2px solid #e2e8f0; border-radius: 20px; padding: 24px;
  text-align: center; position: relative; transition: 0.25s; overflow: hidden;
}
.package-card:hover { transform: translateY(-4px); box-shadow: 0 10px 25px rgba(0,0,0,0.1); }

.package-card.best-value { border-color: #ea580c; background: #fffcf8; }

.card-head h4 { margin: 0; font-size: 18px; font-weight: 700; color: #475569; }
.popular-badge {
  position: absolute; top: 0; left: 0; width: 100%; height: 28px;
  background: #ea580c; color: white; font-size: 12px; font-weight: 800;
  display: flex; align-items: center; justify-content: center;
}
.best-value .card-head { margin-top: 16px; }

.card-price { margin: 24px 0; }
.val { font-size: 42px; font-weight: 900; color: #0f172a; line-height: 1; }
.unit { display: block; font-size: 14px; color: #64748b; font-weight: 600; margin-top: 4px; }

.features {
  list-style: none; padding: 0; margin: 0 0 24px; text-align: left;
}
.features li { font-size: 14px; color: #334155; margin-bottom: 12px; display: flex; gap: 8px; }

.btn-buy {
  width: 100%; padding: 14px; border-radius: 12px;
  background: #f1f5f9; color: #0f172a; border: none;
  font-size: 15px; font-weight: 800; cursor: pointer; transition: 0.2s;
}
.btn-buy:not(:disabled):hover { background: #e2e8f0; }

.btn-buy.highlight { background: #ea580c; color: white; }
.btn-buy.highlight:not(:disabled):hover { background: #c2410c; }
.btn-buy:disabled { opacity: 0.6; cursor: not-allowed; background: #e2e8f0; color: #94a3b8; }

@keyframes scale-up { 0% { transform: scale(0.95); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }

@media (max-width: 640px) {
  .packages-grid { grid-template-columns: 1fr; }
}
</style>
