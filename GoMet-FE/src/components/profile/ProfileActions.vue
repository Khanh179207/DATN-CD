<template>
  <div class="profile-actions-premium">
    <button 
      class="btn-follow-premium" 
      :class="{ 'is-following': isFollowing, 'is-loading': store.followLoading }" 
      @click="handleFollow"
      :disabled="store.followLoading"
    >
      <div class="inner-wrap">
        <span class="icon" v-if="!store.followLoading">
          <svg v-if="isFollowing" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
          <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
        </span>
        <span v-else class="spinner-sm"></span>
        <span class="text">{{ isFollowing ? 'Đang theo dõi' : 'Theo dõi' }}</span>
      </div>
      <div class="glow-effect"></div>
    </button>

    <button 
      class="btn-message-premium" 
      @click="handleMessage"
      :disabled="store.loading"
    >
      <div class="inner-wrap">
        <span class="icon" v-if="!store.loading">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
        </span>
        <span v-else class="spinner-sm"></span>
        <span class="text">Nhắn tin</span>
      </div>
    </button>
  </div>
</template>

<script setup>
import { useProfileActionsStore } from '@/stores/profileActions'

const props = defineProps({
  targetUser: { type: Object, required: true },
  isFollowing: { type: Boolean, default: false }
})

const emit = defineEmits(['update:isFollowing', 'follow-changed'])
const store = useProfileActionsStore()

const handleFollow = async () => {
  const newStatus = await store.toggleFollow(props.targetUser.id || props.targetUser.accountID, props.isFollowing)
  emit('update:isFollowing', newStatus)
  emit('follow-changed', newStatus)
}

const handleMessage = () => {
  store.startConversation(props.targetUser)
}
</script>

<style scoped lang="scss">
.profile-actions-premium {
  display: flex;
  gap: 12px;
  margin-top: 25px;
  width: 100%;

  button {
    flex: 1;
    position: relative;
    border: none;
    border-radius: 14px;
    padding: 12px 20px;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    // Chống người dùng bôi đen text khi click liên tục
    user-select: none;
    -webkit-tap-highlight-color: transparent;

    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }

    .inner-wrap {
      display: flex;
      align-items: center;
      gap: 8px;
      z-index: 2;
      
      .text {
        white-space: nowrap; // Ép chữ trên 1 dòng
      }
    }
  }

  // ── Follow Button - Premium Orange Gradient ──
  .btn-follow-premium {
    background: linear-gradient(135deg, #EA580C 0%, #F59E0B 100%);
    color: white;
    box-shadow: 0 8px 20px -6px rgba(234, 88, 12, 0.4);

    &.is-following {
      background: #F1F5F9;
      color: #475569;
      border: 1px solid #E2E8F0;
      box-shadow: none;

      &:hover {
        background: #E2E8F0;
        color: #0F172A;
        transform: translateY(-2px);
      }
    }

    &:hover:not(.is-following):not(:disabled) {
      transform: translateY(-3px);
      box-shadow: 0 12px 25px -6px rgba(234, 88, 12, 0.5);

      .glow-effect { opacity: 1; }
    }

    .glow-effect {
      position: absolute;
      top: 0; left: 0; width: 100%; height: 100%;
      background: radial-gradient(circle at center, rgba(255, 255, 255, 0.4) 0%, transparent 70%);
      opacity: 0;
      transition: opacity 0.5s;
      z-index: 1;
      pointer-events: none; // Tránh chặn click
    }
  }

  // ── Message Button - Glassmorphism Refined ──
  .btn-message-premium {
    background: #FFFFFF;
    border: 1.5px solid #E2E8F0;
    color: #475569;

    &:hover:not(:disabled) {
      background: #F8FAFC;
      border-color: #CBD5E1;
      color: #1E293B;
      transform: translateY(-3px);
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.04);
    }

    &:active { transform: scale(0.96); }
  }
}

// Micro-animations
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.spinner-sm {
  width: 16px;
  height: 16px;
  border: 2px solid currentColor;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

/* ==========================================
   📱 TỐI ƯU RESPONSIVE
   ========================================== */

// 1. Tablet & Mobile ngang (Dưới 768px)
@media (max-width: 768px) {
  .profile-actions-premium {
    gap: 10px;
    margin-top: 15px;
    
    button {
      padding: 8px 12px;
      font-size: clamp(11px, 3.5vw, 14px);
      border-radius: 12px;
      flex-shrink: 1; // Cho phép nút co lại khi không gian bị bóp nghẹt
      min-width: 0; // Ngăn chặn flex item bị tràn (overflow) chữ
      
      .inner-wrap { gap: 6px; }
      
      // Tắt hover effect giật cục trên thiết bị cảm ứng
      @media (hover: none) {
        &:hover { transform: none !important; box-shadow: none !important; }
        &:active { transform: scale(0.96) !important; }
      }
    }
  }
}

// 2. Điện thoại dọc (Dưới 480px - Màn hình hẹp)
@media (max-width: 480px) {
  .profile-actions-premium {
    gap: 6px;
    margin-top: 12px;
    
    button {
      padding: 8px 4px; // Ép padding xuống tối thiểu để ưu tiên hiển thị nội dung
      
      .inner-wrap {
        .icon {
          svg { width: 14px; height: 14px; } // Thu nhỏ icon thay vì ẩn đi
        }
        
        .spinner-sm {
          width: 14px;
          height: 14px;
        }
      }
    }
  }
}

// Dark mode adaptation
[data-theme='dark'] {
  .profile-actions-premium .btn-message-premium {
    background: rgba(255, 255, 255, 0.1);
    color: white;
    border-color: rgba(255, 255, 255, 0.2);
    
    &:hover { background: rgba(255, 255, 255, 0.2); }
  }
}
</style>