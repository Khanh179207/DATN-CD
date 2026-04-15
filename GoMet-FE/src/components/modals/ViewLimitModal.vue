<template>
  <div v-if="isOpen" class="modal-backdrop" @click.self="closeModal">
    <div class="limit-modal-content">
      <div class="limit-icon-container">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
          <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
        </svg>
      </div>
      
      <h3 class="limit-title">Giới hạn xem hàng ngày</h3>
      <p class="limit-desc">
        Bạn đã hết {{ maxViews }} lượt xem miễn phí trong ngày hôm nay. 
        <br/><br/>
        Bài viết này cần <strong>1 GoMetCoin</strong> để mở ra. Bài viết sau khi mở sẽ xem thoải mái đến hết ngày mà không mất thêm điểm!
      </p>

      <div class="coin-status">
        <span class="label">Khả dụng của bạn:</span>
        <span class="val">✨ {{ authStore.user?.point || 0 }} GoMetCoin</span>
      </div>

      <div class="actions">
        <!-- Nếu đủ điểm -->
        <template v-if="(authStore.user?.point || 0) >= 1">
          <button class="btn-cancel" @click="closeModal">Bỏ qua</button>
          <button class="btn-unlock" :disabled="isUnlocking" @click="handleUnlock">
            {{ isUnlocking ? 'Đang mở...' : 'Mở Khóa (1 Coin)' }}
          </button>
        </template>
        
        <!-- Nếu KHÔNG đủ điểm -->
        <template v-else>
          <button class="btn-cancel" @click="closeModal">Đóng</button>
          <button class="btn-unlock" @click="openStore">
            Mua Gói Premium
          </button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useUIStore } from '@/stores/ui'
import api from '@/services/api'
import { toast } from '@/composables/useToast'

const props = defineProps({
  isOpen: { type: Boolean, default: false },
  postId: { type: Number, required: true }
})

const emit = defineEmits(['close', 'unlocked', 'open-store'])
const authStore = useAuthStore()
const uiStore = useUIStore()
const isUnlocking = ref(false)
const maxViews = ref(3)

const fetchLimit = async () => {
  if (authStore.user?.accountID) {
    try {
      const res = await api.get(`/api/users/${authStore.user.accountID}/view-limits`)
      maxViews.value = res.data.maxViews
    } catch (e) {}
  }
}

fetchLimit() // Gọi ngay khi component được khởi tạo

const closeModal = () => emit('close')

const openStore = () => {
  emit('close'); // Đóng popup này
  uiStore.openStore(); // Mở Store bằng Pinia
}

const handleUnlock = async () => {
  if (!authStore.user?.accountID) return;
  isUnlocking.value = true;
  try {
    const res = await api.post(`/api/users/${authStore.user.accountID}/unlock-view`, { postId: props.postId })
    toast.success('Mở khóa thành công!')
    
    // Cập nhật điểm ngay trong frontend
    authStore.user.point = res.data.pointRemaining;
    authStore.setUser(authStore.user);

    emit('unlocked') // Báo cho trang bài viết để load lại bài
  } catch (err) {
    if (err.response?.data?.code === 'INSUFFICIENT_POINT') {
      toast.error('Bạn không đủ GoMetCoin để thao tác!')
    } else {
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra, thử lại sau.')
    }
  } finally {
    isUnlocking.value = false;
  }
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed; inset: 0; z-index: 100000;
  background: rgba(15, 23, 42, 0.7); backdrop-filter: blur(8px);
  display: flex; justify-content: center; align-items: center; padding: 20px;
}

.limit-modal-content {
  background: #ffffff; width: 100%; max-width: 420px;
  border-radius: 20px; padding: 32px; text-align: center;
  box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25);
  animation: pop-in 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.limit-icon-container {
  width: 72px; height: 72px; background: #fff7ed;
  border-radius: 50%; display: flex; justify-content: center; align-items: center;
  color: #ea580c; margin: 0 auto 24px;
}

.limit-title { margin: 0 0 12px; font-size: 22px; font-weight: 800; color: #0f172a; }
.limit-desc { font-size: 15px; color: #475569; line-height: 1.5; margin: 0 0 24px; }
.limit-desc strong { color: #ea580c; }

.coin-status {
  background: #f8fafc; padding: 12px 16px; border-radius: 12px;
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 24px; border: 1px solid #e2e8f0;
}
.coin-status .label { font-size: 14px; color: #64748b; font-weight: 600; }
.coin-status .val { font-size: 16px; font-weight: 800; color: #f59e0b; }

.actions { display: flex; gap: 12px; }

.btn-cancel, .btn-unlock {
  flex: 1; padding: 12px; border-radius: 12px;
  font-size: 15px; font-weight: 700; cursor: pointer; transition: 0.2s; border: none;
}

.btn-cancel { background: #f1f5f9; color: #475569; }
.btn-cancel:hover { background: #e2e8f0; }

.btn-unlock { background: #ea580c; color: white; }
.btn-unlock:hover { background: #c2410c; }
.btn-unlock:disabled { opacity: 0.6; cursor: not-allowed; }

@keyframes pop-in { 0% { transform: scale(0.95); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }
</style>
