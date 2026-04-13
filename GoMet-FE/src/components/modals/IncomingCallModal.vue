<template>
  <transition name="fade">
    <div v-if="callData" class="incoming-call-overlay">
      <div class="call-card">
        <div class="avatar-ripple-container">
          <img :src="callData.senderAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(callData.senderName)}&background=ea580c&color=fff`" alt="Caller" class="caller-avatar">
          <div class="ripple-ring ring-1"></div>
          <div class="ripple-ring ring-2"></div>
        </div>
        
        <div class="call-info">
          <h3 class="caller-name">{{ callData.senderName }}</h3>
          <p class="call-status">
            <span class="dot-pulse"></span> Đang gọi video cho bạn...
          </p>
        </div>

        <div class="call-actions">
          <div class="action-wrap">
            <button @click="declineCall" class="btn-action btn-decline" title="Từ chối">
              <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M10.68 13.31a16 16 0 0 0 3.41 2.6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7 2 2 0 0 1 1.72 2v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.42 19.42 0 0 1-3.33-2.67m-2.67-3.34a19.79 19.79 0 0 1-3.07-8.63A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91"></path><line x1="23" y1="1" x2="1" y2="23"></line></svg>
            </button>
            <span class="action-label">Từ chối</span>
          </div>
          
          <div class="action-wrap">
            <button @click="acceptCall" class="btn-action btn-accept" title="Trả lời">
              <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M23 7l-7 5 7 5V7z"></path><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
            </button>
            <span class="action-label text-green">Trả lời</span>
          </div>
        </div>
        
        <audio autoplay loop class="hidden-audio">
          <source src="https://assets.mixkit.co/active_storage/sfx/1359/1359-preview.mp3" type="audio/mpeg">
        </audio>
      </div>
    </div>
  </transition>
</template>

<script setup>
const props = defineProps(['callData']);
const emit = defineEmits(['accept', 'decline']); 

const acceptCall = () => {
  emit('accept');
};

const declineCall = () => {
  emit('decline'); 
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

/* Lớp phủ đen mờ sang trọng */
.incoming-call-overlay {
  position: fixed; 
  top: 0; 
  left: 0; 
  width: 100vw; 
  height: 100vh;
  background: rgba(15, 23, 42, 0.75); /* Đen ánh xanh Navy */
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  z-index: 99999; 
  display: flex; 
  justify-content: center; 
  align-items: center;
  font-family: 'Inter', sans-serif;
}

/* Thẻ (Card) hiển thị cuộc gọi */
.call-card {
  background: #ffffff; 
  padding: 40px 30px; 
  border-radius: 28px; 
  text-align: center; 
  width: 340px;
  box-shadow: 0 25px 50px -12px rgba(234, 88, 12, 0.25); /* Bóng đổ màu cam */
  border-top: 6px solid #ea580c; /* Vạch viền cam GoMet ở trên cùng */
  position: relative;
  overflow: hidden;
  animation: slideDown 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes slideDown {
  from { transform: translateY(-50px) scale(0.9); opacity: 0; }
  to { transform: translateY(0) scale(1); opacity: 1; }
}

/* =========================================
   HIỆU ỨNG AVATAR & SÓNG ÂM
   ========================================= */
.avatar-ripple-container {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.caller-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  z-index: 5;
  border: 4px solid #ffffff;
  box-shadow: 0 10px 25px rgba(234, 88, 12, 0.3);
}

.ripple-ring {
  position: absolute;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 2px solid #ea580c; /* Màu cam GoMet */
  animation: rippleEffect 2s cubic-bezier(0.21, 0.53, 0.56, 0.8) infinite;
}

.ring-2 {
  animation-delay: 1s; 
}

@keyframes rippleEffect {
  0% { transform: scale(1); opacity: 0.8; border-width: 4px; }
  100% { transform: scale(2.2); opacity: 0; border-width: 0px; }
}

/* =========================================
   THÔNG TIN CUỘC GỌI
   ========================================= */
.call-info {
  margin-bottom: 32px;
}

.caller-name {
  color: #0f172a;
  font-size: 1.5rem;
  font-weight: 800;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.call-status {
  color: #ea580c; /* Chữ trạng thái màu cam */
  font-size: 0.95rem;
  font-weight: 600;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* Chấm tròn nhấp nháy cạnh chữ Đang gọi */
.dot-pulse {
  width: 8px;
  height: 8px;
  background-color: #ea580c;
  border-radius: 50%;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(234, 88, 12, 0.6); }
  70% { box-shadow: 0 0 0 6px rgba(234, 88, 12, 0); }
  100% { box-shadow: 0 0 0 0 rgba(234, 88, 12, 0); }
}

/* =========================================
   NÚT BẤM (HÀNH ĐỘNG)
   ========================================= */
.call-actions {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.action-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.action-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #64748b;
}

.action-label.text-green {
  color: #10b981;
}

.btn-action {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.btn-decline {
  background: #ef4444; /* Đỏ từ chối */
  box-shadow: 0 10px 20px rgba(239, 68, 68, 0.3);
}

.btn-decline:hover {
  background: #dc2626;
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 14px 25px rgba(239, 68, 68, 0.4);
}

.btn-accept {
  background: #10b981; /* Xanh lá nhấc máy - Vẫn giữ màu này vì là chuẩn UX quốc tế */
  box-shadow: 0 10px 20px rgba(16, 185, 129, 0.3);
  animation: shake 2s infinite ease-in-out; /* Nút nhấc máy hơi rung nhẹ để lôi kéo */
}

.btn-accept:hover {
  background: #059669;
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 14px 25px rgba(16, 185, 129, 0.4);
  animation: none; /* Dừng rung khi hover */
}

@keyframes shake {
  0%, 100% { transform: rotate(0deg); }
  10%, 30%, 50%, 70%, 90% { transform: rotate(-8deg); }
  20%, 40%, 60%, 80% { transform: rotate(8deg); }
}

.hidden-audio {
  display: none;
}

/* Transition Mờ dần */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>