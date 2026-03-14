<template>
  <transition name="fade">
    <div v-if="isOpen" class="expired-overlay" @click.stop>
      <div class="expired-card">
        
        <transition name="slide-fade" mode="out-in">
          
          <div v-if="!isThankYouScreen" key="expired" class="card-content">
            <div class="icon-box">⚠️</div>
            <h3 class="expired-title">Gói Premium đã hết hạn!</h3>
            <p class="expired-desc">Phiên trải nghiệm Premium của bạn đã kết thúc. Hãy nâng cấp để tiếp tục sử dụng các đặc quyền không giới hạn.</p>
            
            <div class="action-buttons">
              <button class="btn-renew" @click="handleRenew">Gia hạn ngay</button>
              <button class="btn-cancel" @click="handleCancelClick">Quay về bản Free</button>
            </div>
          </div>

          <div v-else key="thankyou" class="card-content">
            <div class="icon-box">💖</div>
            <h3 class="expired-title">Cảm ơn bạn!</h3>
            <p class="expired-desc">Hy vọng bạn đã có những giây phút trải nghiệm tuyệt vời. Tài khoản của bạn đã được chuyển về phiên bản miễn phí.</p>
            
            <div class="action-buttons">
              <button class="btn-renew" @click="handleCloseFinal">Tiếp tục sử dụng GoMet</button>
            </div>
          </div>
          
        </transition>

      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({ isOpen: Boolean });
const emit = defineEmits(['renew', 'cancel']);

const authStore = useAuthStore()
const router = useRouter()

const isThankYouScreen = ref(false)

watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    isThankYouScreen.value = false; 
    console.log(">>> Gói hết hạn! Đang đồng bộ giao diện Frontend về bản Free...");
    
    if (authStore.user) {
      authStore.user.isPremium = 0;
      if (authStore.user.role === 'premium') authStore.user.role = 'user';
    }

    try {
      const userStr = localStorage.getItem('user');
      if (userStr) {
        const localUser = JSON.parse(userStr);
        localUser.isPremium = 0;
        if (localUser.role === 'premium') localUser.role = 'user';
        localStorage.setItem('user', JSON.stringify(localUser));
      }
    } catch (e) {
      console.error(e);
    }
  }
});

const handleRenew = () => {
  emit('renew');
};

const handleCancelClick = () => {
  isThankYouScreen.value = true;
};

const handleCloseFinal = () => {
  emit('cancel');
  router.push('/home'); 
};
</script>

<style scoped>
.expired-overlay {
  position: fixed; 
  inset: 0; 
  z-index: 9999999; 
  background: rgba(15, 23, 42, 0.95);
  backdrop-filter: blur(15px);
  display: flex; 
  align-items: center; 
  justify-content: center;
  pointer-events: auto; 
}
.expired-card {
  background: #fff; 
  padding: 40px; 
  border-radius: 32px; 
  text-align: center;
  width: 90%; /* Thêm width để nó co giãn tốt trên mobile */
  max-width: 450px; 
  box-shadow: 0 30px 60px rgba(0,0,0,0.6);
  border: 1px solid rgba(255,255,255,0.1);
  min-height: 420px; /* Chỉnh lại một chút cho nó ôm vừa nội dung */
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden; /* Tránh content bị tràn ra ngoài lúc animation */
}

/* Thêm class này để bọc nội dung, giúp Flexbox căn giữa tốt hơn */
.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.icon-box { font-size: 64px; margin-bottom: 20px; }
.expired-title { color: #111827; font-weight: 900; font-size: 1.6rem; margin-bottom: 15px; }
.expired-desc { color: #64748b; line-height: 1.6; margin-bottom: 35px; font-size: 1.05rem; padding: 0 10px; }
.action-buttons { display: flex; flex-direction: column; gap: 15px; width: 100%; }
.btn-renew { 
  padding: 18px; 
  background: linear-gradient(135deg, #EA580C, #C2410C); 
  color: #fff; 
  border: none; 
  border-radius: 16px; 
  font-weight: 800; 
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
}
.btn-renew:hover { transform: translateY(-3px); box-shadow: 0 12px 24px rgba(234, 88, 12, 0.4); }
.btn-cancel { 
  padding: 16px; 
  background: #f1f5f9; 
  color: #475569; 
  border: none; 
  border-radius: 16px; 
  font-weight: 700; 
  cursor: pointer;
  transition: 0.2s;
  width: 100%;
}
.btn-cancel:hover { background: #e2e8f0; color: #1e293b; }

/* 🔥 HIỆU ỨNG TỔNG TỔNG THỂ CỦA MODAL */
.fade-enter-active, .fade-leave-active { transition: opacity 0.4s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* 🔥 HIỆU ỨNG CHUYỂN NỘI DUNG (SLIDE NGANG CHUẨN APP) */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.slide-fade-enter-from {
  opacity: 0;
  transform: translateX(30px); /* Nội dung mới trượt từ phải sang */
}
.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-30px); /* Nội dung cũ trượt sang trái rồi biến mất */
}
</style>