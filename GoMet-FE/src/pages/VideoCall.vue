<template>
  <div class="webrtc-wrapper">
    <div class="remote-view">
      <video ref="remoteVideo" autoplay playsinline class="video-stream remote-video"></video>
      
      <div v-if="!remoteStreamActive" class="waiting-state">
        <div class="avatar-ripple-container">
          <img :src="partnerAvatar" class="waiting-avatar" alt="Avatar">
          <div class="ripple-ring ring-1"></div>
          <div class="ripple-ring ring-2"></div>
        </div>
        <h2 class="waiting-title">{{ partnerName }}</h2>
        <p class="waiting-subtitle" v-if="isCaller">Đang đổ chuông...</p>
        <p class="waiting-subtitle" v-else>Đang kết nối tín hiệu...</p>
      </div>

      <div v-if="remoteStreamActive" class="name-badge remote-badge">
        <span class="pulse-dot"></span> {{ partnerName }}
      </div>
    </div>

    <div class="local-view shadow-2xl" :class="{'is-connected': remoteStreamActive}">
      <video ref="localVideo" autoplay playsinline muted class="video-stream local-video"></video>
      <div class="name-badge local-badge">Bạn</div>
    </div>

    <div class="controls-dock">
      <div class="controls-glass-panel">
        <button @click="toggleMic" class="ctrl-btn" :class="isMicOn ? 'btn-active' : 'btn-muted'" :title="isMicOn ? 'Tắt Micro' : 'Bật Micro'">
          <svg v-if="isMicOn" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"></path><path d="M19 10v2a7 7 0 0 1-14 0v-2"></path><line x1="12" y1="19" x2="12" y2="23"></line><line x1="8" y1="23" x2="16" y2="23"></line></svg>
          <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="1" y1="1" x2="23" y2="23"></line><path d="M9 9v3a3 3 0 0 0 5.12 2.12M15 9.34V4a3 3 0 0 0-5.94-.6"></path><path d="M17 16.95A7 7 0 0 1 5 12v-2m14 0v2a7 7 0 0 1-.11 1.23"></path><line x1="12" y1="19" x2="12" y2="23"></line><line x1="8" y1="23" x2="16" y2="23"></line></svg>
        </button>

        <button @click="endCall" class="ctrl-btn btn-hangup" title="Kết thúc cuộc gọi">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M10.68 13.31a16 16 0 0 0 3.41 2.6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7 2 2 0 0 1 1.72 2v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.42 19.42 0 0 1-3.33-2.67m-2.67-3.34a19.79 19.79 0 0 1-3.07-8.63A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91"></path><line x1="23" y1="1" x2="1" y2="23"></line></svg>
        </button>

        <button @click="toggleCam" class="ctrl-btn" :class="isCamOn ? 'btn-active' : 'btn-muted'" :title="isCamOn ? 'Tắt Camera' : 'Bật Camera'">
          <svg v-if="isCamOn" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M23 7l-7 5 7 5V7z"></path><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
          <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 16v1a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h2m5.66 0H14a2 2 0 0 1 2 2v3.34l1 1L23 7v10"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { toast } from '@/composables/useToast';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

// --- STATE ---
const localVideo = ref(null);
const remoteVideo = ref(null);
const isMicOn = ref(true);
const isCamOn = ref(true);
const remoteStreamActive = ref(false);
const isStarted = ref(false);

let localStream = null;
let peerConnection = null;
let stompClient = null;
let callStartTime = null; 
let pendingCandidates = [];

let ringtoneAudio = null;

const conversationId = route.params.roomID;
const currentUserId = Number(authStore.user?.accountID || authStore.user?.id);

const isCaller = computed(() => route.query.role === 'caller');
const partnerName = computed(() => route.query.partnerName || 'Đối phương');

const partnerAvatar = computed(() => {
  return route.query.partnerAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(partnerName.value)}&background=random&color=fff&size=150`;
});

const rtcConfig = {
  iceServers: [
    { urls: 'stun:stun.l.google.com:19302' },
    { urls: 'stun:stun1.l.google.com:19302' },
  ]
};

const playRingtone = () => {
  if (isCaller.value) {
    ringtoneAudio = new Audio('/audio/ringtone.mp3');
    ringtoneAudio.loop = true; 
    ringtoneAudio.play().catch(err => console.log("Trình duyệt chặn tự động phát âm thanh:", err));
  }
};

const stopRingtone = () => {
  if (ringtoneAudio) {
    ringtoneAudio.pause();
    ringtoneAudio.currentTime = 0;
    ringtoneAudio = null;
  }
};

// 🚀 HÀM BẮT SỰ KIỆN TẮT TRÌNH DUYỆT (DẤU X) HOẶC F5
const handleWindowClose = (event) => {
  // 1. Nếu đang kết nối, bắn ngay tín hiệu cúp máy
  if (stompClient && stompClient.connected) {
    sendSignal('hangup', {});
    
    // 2. Lưu lại lịch sử cuộc gọi nếu mình là người gọi
    if (callStartTime && isCaller.value) {
      const durationSec = Math.floor((Date.now() - callStartTime) / 1000);
      const m = Math.floor(durationSec / 60).toString().padStart(2, '0');
      const s = (durationSec % 60).toString().padStart(2, '0');
      const content = `[SYS_CALL:${m}:${s}]`;

      const chatMessage = { 
        content: content, 
        conversation: { conversationID: parseInt(conversationId) }, 
        sender: { accountID: currentUserId } 
      };
      
      // Sử dụng API thô (fetch/beacon) nếu cần gửi gấp trước khi tab chết hẳn
      stompClient.send("/app/chat.sendMessage", { 'Authorization': `Bearer ${authStore.user?.token}` }, JSON.stringify(chatMessage));
    }
  }
  
  // Trình duyệt sẽ tự dọn dẹp biến, nên không cần gỡ kỹ như onBeforeUnmount
};

onMounted(async () => {
  await initLocalStream();
  connectSignalingServer();
  playRingtone();

  // 🚀 Lắng nghe sự kiện tab bị tắt ngang
  window.addEventListener('beforeunload', handleWindowClose);
});

const initLocalStream = async () => {
  try {
    localStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
    if (localVideo.value) localVideo.value.srcObject = localStream;
  } catch (err) {
    console.error("Lỗi cam:", err);
    toast.error("Không thể truy cập Camera/Micro!");
  }
};

const connectSignalingServer = () => {
  const socket = new SockJS('http://localhost:8080/ws-chat');
  stompClient = Stomp.over(socket);
  stompClient.debug = null; 

  stompClient.connect({ 'Authorization': `Bearer ${authStore.user?.token}` }, () => {
    
    stompClient.subscribe(`/topic/${conversationId}`, async (message) => {
      const signal = JSON.parse(message.body);
      
      if (Number(signal.senderId) === currentUserId) return;

      console.log("📩 Nhận tín hiệu:", signal.type);

      switch (signal.type) {
        case 'joined':
          if (isCaller.value && !isStarted.value) {
            startCall();
          }
          break;
        case 'offer':
          await handleOffer(signal.data);
          break;
        case 'answer':
          await handleAnswer(signal.data);
          stopRingtone();
          break;
        case 'candidate':
          await handleCandidate(signal.data);
          break;
        case 'hangup':
        case 'decline':
          toast.info("Cuộc gọi đã kết thúc.");
          closeAndExit();
          break;
      }
    });

    if (!isCaller.value) {
      setTimeout(() => sendSignal('joined', {}), 500); 
    }
  });
};

const sendSignal = (type, data) => {
  if (stompClient && stompClient.connected) {
    const payload = { type, conversationId, senderId: currentUserId, data };
    stompClient.send("/app/call.signaling", {}, JSON.stringify(payload));
  }
};

const createPeerConnection = () => {
  if (peerConnection) return;

  peerConnection = new RTCPeerConnection(rtcConfig);

  peerConnection.ontrack = (event) => {
    if (remoteVideo.value && event.streams && event.streams[0]) {
      remoteVideo.value.srcObject = event.streams[0];
      remoteStreamActive.value = true;
      
      stopRingtone();

      if (!callStartTime) {
        callStartTime = Date.now();
      }
    }
  };

  peerConnection.onicecandidate = (event) => {
    if (event.candidate) sendSignal('candidate', event.candidate);
  };

  if (localStream) {
    localStream.getTracks().forEach(track => peerConnection.addTrack(track, localStream));
  }
};

const processPendingCandidates = async () => {
  while (pendingCandidates.length > 0) {
    const candidate = pendingCandidates.shift();
    try {
      await peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
    } catch (e) { console.error("Lỗi ghép IP:", e); }
  }
};

const startCall = async () => {
  if (isStarted.value) return;
  isStarted.value = true;
  createPeerConnection();
  const offer = await peerConnection.createOffer();
  await peerConnection.setLocalDescription(offer);
  sendSignal('offer', offer);
};

const handleOffer = async (offer) => {
  if (isStarted.value) return;
  isStarted.value = true;
  createPeerConnection();
  
  await peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
  await processPendingCandidates(); 
  
  const answer = await peerConnection.createAnswer();
  await peerConnection.setLocalDescription(answer);
  sendSignal('answer', answer);
};

const handleAnswer = async (answer) => {
  await peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
  await processPendingCandidates(); 
};

const handleCandidate = async (candidate) => {
  try {
    if (peerConnection && peerConnection.remoteDescription && peerConnection.remoteDescription.type) {
      await peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
    } else {
      pendingCandidates.push(candidate);
    }
  } catch (e) { console.error("Lỗi Candidate:", e); }
};

const toggleMic = () => {
  if (localStream) {
    isMicOn.value = !isMicOn.value;
    localStream.getAudioTracks()[0].enabled = isMicOn.value;
  }
};

const toggleCam = () => {
  if (localStream) {
    isCamOn.value = !isCamOn.value;
    localStream.getVideoTracks()[0].enabled = isCamOn.value;
  }
};

const endCall = () => {
  sendSignal('hangup', {});
  closeAndExit();
};

const closeAndExit = () => {
  stopRingtone();

  if (callStartTime && isCaller.value && stompClient?.connected) {
    const durationSec = Math.floor((Date.now() - callStartTime) / 1000);
    const m = Math.floor(durationSec / 60).toString().padStart(2, '0');
    const s = (durationSec % 60).toString().padStart(2, '0');
    const content = `[SYS_CALL:${m}:${s}]`;

    const chatMessage = { 
      content: content, 
      conversation: { conversationID: parseInt(conversationId) }, 
      sender: { accountID: currentUserId } 
    };
    stompClient.send("/app/chat.sendMessage", { 'Authorization': `Bearer ${authStore.user?.token}` }, JSON.stringify(chatMessage));
  }

  if (stompClient) stompClient.disconnect();
  if (peerConnection) {
    peerConnection.close();
    peerConnection = null;
  }
  if (localStream) {
    localStream.getTracks().forEach(track => track.stop());
  }

  window.close();
  setTimeout(() => { router.push('/home'); }, 300);
};

onBeforeUnmount(() => {
  // 🚀 Nhớ dọn dẹp event listener kẻo rò rỉ bộ nhớ
  window.removeEventListener('beforeunload', handleWindowClose);

  if (stompClient?.connected) endCall();
  else closeAndExit();
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.webrtc-wrapper {
  height: 100vh;
  width: 100vw;
  background: radial-gradient(circle at 50% 30%, #2a2a35 0%, #08080a 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Inter', sans-serif;
  display: flex;
  justify-content: center;
  align-items: center;
}

.video-stream {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remote-view {
  position: absolute;
  inset: 16px; 
  border-radius: 20px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #111114; 
  box-shadow: 0 10px 40px rgba(0,0,0,0.5);
}

.remote-video {
  filter: brightness(0.95); 
  transition: opacity 0.5s ease;
}

/* =========================================
   🚀 2. SỬA LẠI: GIAO DIỆN CHỜ (AVATAR + SÓNG ÂM)
   ========================================= */
.waiting-state {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 10;
}

.avatar-ripple-container {
  position: relative;
  width: 140px;
  height: 140px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 24px;
}

.waiting-avatar {
  width: 110px;
  height: 110px;
  border-radius: 50%;
  object-fit: cover;
  z-index: 5;
  border: 4px solid #ffffff;
  box-shadow: 0 10px 30px rgba(0,0,0,0.4);
}

/* Hai vòng sóng âm lan tỏa */
.ripple-ring {
  position: absolute;
  width: 110px;
  height: 110px;
  border-radius: 50%;
  border: 2px solid #ea580c;
  animation: rippleEffect 2.5s cubic-bezier(0.21, 0.53, 0.56, 0.8) infinite;
}

.ring-2 {
  animation-delay: 1.25s; /* Trễ nhịp để tạo ra 2 sóng liên tiếp */
}

.waiting-title {
  color: #ffffff;
  font-size: 1.6rem;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.5);
}

.waiting-subtitle {
  color: #a1a1aa;
  font-size: 1rem;
  margin: 0;
  font-weight: 500;
}

/* =========================================
   3. CAMERA CỦA MÌNH (LOCAL VIEW)
   ========================================= */
.local-view {
  position: absolute;
  top: 32px;
  right: 32px;
  width: 240px;
  height: 160px; 
  background: #1e1e24;
  border-radius: 16px;
  overflow: hidden;
  z-index: 20;
  box-shadow: 0 24px 50px rgba(0, 0, 0, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.5s cubic-bezier(0.25, 1, 0.5, 1);
}

.local-video {
  transform: scaleX(-1);
}

.local-view.is-connected {
  width: 180px;
  height: 120px;
  top: auto;
  bottom: 120px; 
  right: 32px;
  border-color: rgba(234, 88, 12, 0.3);
  box-shadow: 0 10px 30px rgba(234, 88, 12, 0.15), 0 20px 40px rgba(0,0,0,0.5);
}

/* =========================================
   4. THẺ TÊN (NAME BADGES)
   ========================================= */
.name-badge {
  position: absolute;
  background: rgba(15, 15, 20, 0.65);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: #f8fafc;
  padding: 6px 14px;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid rgba(255,255,255,0.05);
}

.remote-badge {
  bottom: 24px;
  left: 24px;
  z-index: 10;
}

.local-badge {
  bottom: 8px;
  right: 8px;
  padding: 4px 10px;
  font-size: 0.75rem;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  background-color: #22c55e;
  border-radius: 50%;
  box-shadow: 0 0 0 rgba(34, 197, 94, 0.4);
  animation: pulseDot 2s infinite;
}

/* =========================================
   5. THANH ĐIỀU KHIỂN (CONTROLS DOCK)
   ========================================= */
.controls-dock {
  position: absolute;
  bottom: 32px;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  z-index: 100;
}

.controls-glass-panel {
  background: rgba(20, 20, 24, 0.8);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  padding: 12px 24px;
  border-radius: 100px;
  display: flex;
  align-items: center;
  gap: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4), inset 0 1px 0 rgba(255,255,255,0.1);
}

.ctrl-btn {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  outline: none;
}

.btn-active {
  background: rgba(255, 255, 255, 0.15);
  color: #ffffff;
}

.btn-active:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-4px) scale(1.05);
}

.btn-muted {
  background: #ffffff;
  color: #111114;
}

.btn-muted:hover {
  background: #f1f5f9;
  transform: translateY(-4px) scale(1.05);
}

.btn-hangup {
  background: #ef4444;
  color: white;
  width: 60px; 
  height: 60px;
  margin: 0 8px; 
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.4);
}

.btn-hangup:hover {
  background: #dc2626;
  transform: translateY(-4px) scale(1.1);
  box-shadow: 0 8px 25px rgba(239, 68, 68, 0.6);
}

/* =========================================
   6. HIỆU ỨNG CHUYỂN ĐỘNG (ANIMATIONS)
   ========================================= */

/* 🚀 Hiệu ứng sóng âm lan tỏa */
@keyframes rippleEffect {
  0% {
    transform: scale(1);
    opacity: 1;
    border-width: 4px;
  }
  100% {
    transform: scale(2.5);
    opacity: 0;
    border-width: 0px;
  }
}

@keyframes pulseDot {
  0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(34, 197, 94, 0.7); }
  70% { transform: scale(1.2); box-shadow: 0 0 0 8px rgba(34, 197, 94, 0); }
  100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(34, 197, 94, 0); }
}

@media (max-width: 768px) {
  .remote-view { inset: 0; border-radius: 0; }
  .local-view { width: 120px; height: 180px; top: 16px; right: 16px; }
  .local-view.is-connected { width: 100px; height: 150px; bottom: 110px; right: 16px; }
  .controls-glass-panel { padding: 10px 20px; gap: 12px; }
  .ctrl-btn { width: 44px; height: 44px; }
  .btn-hangup { width: 52px; height: 52px; }
}
</style>