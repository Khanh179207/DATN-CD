<template>
  <transition name="fade">
    <div v-if="callData" class="incoming-call-overlay">
      <div class="call-card shadow-2xl">
        <div class="avatar-animation">
          <img :src="callData.senderAvatar || 'https://ui-avatars.com/api/?name=User'" alt="Caller">
          <div class="ring-circle"></div>
        </div>
        
        <div class="call-info">
          <h3>{{ callData.senderName }}</h3>
          <p>Đang gọi video cho bạn...</p>
        </div>

        <div class="call-actions">
          <button @click="declineCall" class="btn-decline">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="white"><path d="M10.68 13.31a16 16 0 0 0 3.41 2.6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7 2 2 0 0 1 1.72 2v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.42 19.42 0 0 1-3.33-2.67m-2.67-3.34a19.79 19.79 0 0 1-3.07-8.63A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91"></path><line x1="23" y1="1" x2="1" y2="23" stroke="white" stroke-width="2"></line></svg>
          </button>
          
          <button @click="acceptCall" class="btn-accept">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="white"><path d="M23 7l-7 5 7 5V7z"></path><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
          </button>
        </div>
        
        <audio autoplay loop>
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
  emit('decline'); // Khớp với App.vue
};
</script>

<style scoped>
.incoming-call-overlay {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(0,0,0,0.8); z-index: 9999; display: flex; justify-content: center; align-items: center;
}
.call-card {
  background: #1e1e1e; padding: 40px; border-radius: 24px; text-align: center; color: white; width: 320px;
}
.avatar-animation { position: relative; width: 100px; height: 100px; margin: 0 auto 20px; }
.avatar-animation img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; border: 3px solid #ff8a00; }
.ring-circle {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  border: 2px solid #ff8a00; border-radius: 50%; animation: pulse 1.5s infinite;
}
.call-actions { display: flex; justify-content: space-around; margin-top: 30px; }
.btn-accept { width: 60px; height: 60px; border-radius: 50%; background: #4cd964; border: none; cursor: pointer; }
.btn-decline { width: 60px; height: 60px; border-radius: 50%; background: #ff3b30; border: none; cursor: pointer; }
@keyframes pulse { 0% { transform: scale(1); opacity: 1; } 100% { transform: scale(1.5); opacity: 0; } }
</style>