<template>
  <div v-if="isOpen" class="modal-backdrop" @click.self="closeModal">
    <div class="store-modal-content">
      
      <!-- HEADER SANG TRỌNG -->
      <div class="store-header">
        <div class="header-title">
          <h2 class="brand-logo">GoMet<span class="highlight">Store</span></h2>
          <p>Dùng điểm GoMetCoin để nâng cấp trải nghiệm của bạn</p>
        </div>
        <div class="current-coin">
          <span class="coin-label">Số dư khả dụng</span>
          <div class="coin-badge">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="#f59e0b" stroke="#f59e0b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
            <span class="amount">{{ authStore.user?.point || 0 }}</span>
          </div>
        </div>
        <button class="btn-close" @click="closeModal" title="Đóng">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>
      </div>

      <div class="store-body">
        <!-- BANNER THÔNG TIN -->
        <div class="info-banner">
          <div class="banner-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><path d="M12 16v-4"></path><path d="M12 8h.01"></path></svg>
          </div>
          <div class="banner-text">
            <strong>Cách nhận xu:</strong> Tích cực chia sẻ công thức (được duyệt) hoặc chiến thắng trong các sự kiện (Event) để nhận phần thưởng!
          </div>
        </div>

        <h3 class="section-title">Đặc quyền Premium (Không giới hạn & Trợ lý AI)</h3>
        
        <!-- DANH SÁCH GÓI PREMIUM -->
        <div class="packages-grid">
          
          <!-- Gói Tiêu Chuẩn -->
          <div class="package-card standard">
            <div class="card-head">
              <h4>Gói 1 Tháng</h4>
            </div>
            <div class="card-price">
              <span class="val">50</span>
              <span class="unit">GoMetCoin</span>
            </div>
            <ul class="features">
              <li><i class="icon-check"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg></i> Mở khóa toàn bộ công thức</li>
              <li><i class="icon-check"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg></i> Trợ lý Gomet AI siêu tốc</li>
              <li><i class="icon-check"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg></i> Xem bài viết không mất lượt</li>
            </ul>
            <button class="btn-buy" :disabled="authStore.user?.point < 50 || isPremium" @click="buyPremium(1)">
              {{ isPremium ? 'Đã kích hoạt' : 'Mua Gói Ngay' }}
            </button>
          </div>

          <!-- Gói Cao Cấp -->
          <div class="package-card best-value">
            <div class="popular-badge">TIẾT KIỆM 65%</div>
            <div class="card-head">
              <h4>Gói 1 Năm</h4>
            </div>
            <div class="card-price text-orange">
              <span class="val">200</span>
              <span class="unit">GoMetCoin</span>
            </div>
            <ul class="features">
              <li><i class="icon-check orange"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg></i> Mọi quyền lợi của gói 1 tháng</li>
              <li><i class="icon-star"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg></i> Chi phí tối ưu nhất</li>
              <li><i class="icon-crown"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polygon points="2 4 5 13 12 8 19 13 22 4 22 20 2 20 2 4"></polygon></svg></i> Nhãn Premium đặc biệt</li>
            </ul>
            <button class="btn-buy highlight" :disabled="authStore.user?.point < 200 || isPremium" @click="buyPremium(12)">
              {{ isPremium ? 'Đã kích hoạt' : 'Nâng Cấp Ngay' }}
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
/* BACKGROUND VÀ HIỆU ỨNG CHUNG */
.modal-backdrop {
  position: fixed; inset: 0; z-index: 100000;
  background: rgba(15, 23, 42, 0.75); backdrop-filter: blur(12px);
  display: flex; justify-content: center; align-items: center; padding: 20px;
}

.store-modal-content {
  background: #ffffff; width: 100%; max-width: 720px;
  max-height: 90vh; display: flex; flex-direction: column;
  border-radius: 28px; overflow: hidden;
  box-shadow: 0 30px 60px -15px rgba(0,0,0,0.4);
  animation: modal-enter 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* HEADER */
.store-header {
  flex-shrink: 0;
  padding: 30px 40px; 
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%); 
  color: white;
  display: flex; justify-content: space-between; align-items: center;
  position: relative;
  border-bottom: 4px solid #ea580c;
}

.header-title { padding-right: 40px; } /* Tránh text bị đè bởi nút đóng (absolute) */

.header-title .brand-logo { 
  margin: 0 0 6px; 
  font-size: 2rem; 
  font-weight: 900; 
  font-family: 'Playfair Display', serif; /* Font thương hiệu */
  letter-spacing: -0.5px;
}
.header-title .highlight { color: #ea580c; }
.header-title p { margin: 0; color: #94a3b8; font-size: 0.95rem; font-weight: 500;}

/* HIỂN THỊ SỐ DƯ */
.current-coin {
  background: linear-gradient(135deg, rgba(255,255,255,0.1), rgba(255,255,255,0.03));
  padding: 8px 12px 8px 20px; 
  border-radius: 100px;
  display: flex; flex-direction: row; align-items: center; gap: 16px; 
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 20px rgba(0,0,0,0.15), inset 0 2px 10px rgba(255,255,255,0.05);
  margin-right: 48px; /* Đẩy lùi vào để không bị đè bởi nút X */
  backdrop-filter: blur(10px);
}
.coin-label { font-size: 0.8rem; color: #e2e8f0; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px;}
.coin-badge { display: flex; align-items: center; gap: 8px; background: rgba(245, 158, 11, 0.15); padding: 6px 16px; border-radius: 100px; border: 1px solid rgba(245, 158, 11, 0.3);}
.coin-badge .amount { font-size: 1.3rem; font-weight: 900; color: #fbbf24; text-shadow: 0 2px 12px rgba(245, 158, 11, 0.5);}

/* NÚT ĐÓNG */
.btn-close {
  position: absolute; top: 24px; right: 24px;
  background: rgba(255,255,255,0.1); color: #cbd5e1; border: none; 
  width: 36px; height: 36px; border-radius: 50%;
  display: flex; justify-content: center; align-items: center;
  cursor: pointer; transition: all 0.25s ease;
}
.btn-close:hover { 
  background: rgba(255,255,255,0.2); color: white; transform: rotate(90deg); 
}

/* BODY CỬA HÀNG */
.store-body { 
  padding: 40px; 
  overflow-y: auto;
  flex: 1;
}
/* Custom Scrollbar cho Modal */
.store-body::-webkit-scrollbar { width: 6px; }
.store-body::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.store-body::-webkit-scrollbar-thumb:hover { background: #94a3b8; }

.info-banner {
  background: #fff7ed; border: 1px solid #fed7aa; padding: 16px 20px;
  border-radius: 16px; display: flex; gap: 16px; align-items: flex-start;
  color: #9a3412; margin-bottom: 36px;
  box-shadow: 0 4px 15px rgba(234, 88, 12, 0.05);
}
.banner-icon { color: #ea580c; margin-top: 2px; }
.banner-text { font-size: 0.95rem; line-height: 1.6; }

.section-title { margin: 0 0 24px; font-size: 1.25rem; font-weight: 800; color: #0f172a; text-align: center; }

/* LƯỚI GÓI PREMIUM */
.packages-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; }

.package-card {
  border: 2px solid #e2e8f0; border-radius: 24px; padding: 32px 24px;
  text-align: center; position: relative; transition: all 0.3s ease;
  background: #ffffff;
}
.package-card:hover { transform: translateY(-6px); box-shadow: 0 20px 40px -10px rgba(0,0,0,0.1); }

/* GÓI TIẾT KIỆM NHẤT (NỔI BẬT) */
.package-card.best-value { 
  border: 2px solid #ea580c; 
  background: linear-gradient(180deg, #fffcf8 0%, #ffffff 100%);
  box-shadow: 0 10px 30px -10px rgba(234, 88, 12, 0.2);
}

.popular-badge {
  position: absolute; top: -14px; left: 50%; transform: translateX(-50%);
  background: linear-gradient(135deg, #ea580c, #f97316); color: white; 
  font-size: 0.75rem; font-weight: 800; letter-spacing: 1px;
  padding: 6px 16px; border-radius: 100px;
  box-shadow: 0 4px 10px rgba(234, 88, 12, 0.3);
  white-space: nowrap;
}

.card-head h4 { margin: 0; font-size: 1.2rem; font-weight: 800; color: #334155; }
.best-value .card-head h4 { color: #ea580c; }

/* THÔNG TIN GIÁ (SỐ XU) */
.card-price { margin: 24px 0; display: flex; flex-direction: column; align-items: center; }
.val { font-size: 3.5rem; font-weight: 900; color: #0f172a; line-height: 1; font-family: 'Playfair Display', serif;}
.text-orange .val { color: #ea580c; }
.unit { font-size: 0.9rem; color: #64748b; font-weight: 700; margin-top: 8px; text-transform: uppercase; letter-spacing: 1px;}

/* DANH SÁCH QUYỀN LỢI */
.features { list-style: none; padding: 0; margin: 0 0 32px; text-align: left; }
.features li { 
  font-size: 0.95rem; color: #334155; margin-bottom: 14px; 
  display: flex; gap: 10px; align-items: flex-start; font-weight: 500;
}
.features i { 
  display: flex; align-items: center; justify-content: center; 
  width: 22px; height: 22px; flex-shrink: 0;
}
.icon-check { color: #10b981; }
.icon-check.orange, .icon-star { color: #ea580c; }
.icon-crown { color: #eab308; }

/* NÚT BẤM */
.btn-buy {
  width: 100%; padding: 16px; border-radius: 16px;
  background: #f1f5f9; color: #0f172a; border: none;
  font-size: 1rem; font-weight: 800; cursor: pointer; 
  transition: all 0.3s ease;
}
.btn-buy:not(:disabled):hover { background: #e2e8f0; transform: scale(1.02); }

.btn-buy.highlight { 
  background: linear-gradient(135deg, #ea580c, #f59e0b); 
  color: white; 
  box-shadow: 0 8px 20px -6px rgba(234, 88, 12, 0.4);
}
.btn-buy.highlight:not(:disabled):hover { 
  box-shadow: 0 12px 25px -6px rgba(234, 88, 12, 0.6); 
  transform: translateY(-2px);
}
.btn-buy:disabled { 
  opacity: 0.6; cursor: not-allowed; background: #e2e8f0; color: #94a3b8; box-shadow: none; transform: none;
}

/* ANIMATION HIỂN THỊ MODAL */
@keyframes modal-enter { 
  0% { transform: scale(0.9) translateY(20px); opacity: 0; } 
  100% { transform: scale(1) translateY(0); opacity: 1; } 
}

/* =======================================================
   🔥 RESPONSIVE CHO MỌI THIẾT BỊ
======================================================= */

/* Tablet (Dưới 768px) */
@media (max-width: 768px) {
  .store-header { padding: 24px 30px; }
  .store-body { padding: 30px; }
  .packages-grid { gap: 20px; }
  .package-card { padding: 24px 16px; }
  .val { font-size: 2.8rem; }
  .current-coin { margin-right: 36px; padding: 6px 10px 6px 16px; }
}

/* Mobile Ngang & Dọc lớn (Dưới 640px) */
@media (max-width: 640px) {
  .store-modal-content { border-radius: 24px; }
  .store-header { padding: 24px 20px; flex-direction: column; align-items: flex-start; gap: 16px;}
  .btn-close { top: 20px; right: 20px; }
  .current-coin { align-self: flex-start; width: 100%; flex-direction: row; justify-content: space-between; margin-right: 0; padding: 12px 20px;}
  .store-body { padding: 24px 20px; }
  .packages-grid { grid-template-columns: 1fr; gap: 24px; }
  .package-card { padding: 30px 24px; }
  .val { font-size: 3.2rem; }
}

/* Mobile Nhỏ (Dưới 480px) */
@media (max-width: 480px) {
  .modal-backdrop { padding: 12px; }
  .store-modal-content { border-radius: 20px; }
  .header-title .brand-logo { font-size: 1.6rem; }
  .header-title p { font-size: 0.85rem; }
  .current-coin { padding: 10px 16px; gap: 10px; }
  .coin-badge { padding: 4px 12px; }
  .coin-badge .amount { font-size: 1.15rem; }
  .info-banner { padding: 12px 16px; font-size: 0.85rem; flex-direction: column; gap: 8px;}
  .package-card { padding: 24px 16px; }
  .val { font-size: 2.8rem; }
  .unit { font-size: 0.8rem; }
  .features li { font-size: 0.9rem; }
  .btn-buy { padding: 14px; font-size: 0.95rem; }
}
</style>