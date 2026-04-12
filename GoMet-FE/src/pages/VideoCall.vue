<template>
  <div class="webrtc-container">
    <div class="remote-video-container">
      <video ref="remoteVideo" autoplay playsinline class="remote-video"></video>
      
      <div v-if="!remoteStreamActive" class="waiting-overlay">
        <div class="spinner"></div>
        <p>Đang thiết lập kết nối an toàn...</p>
        <span class="calling-text" v-if="isCaller">Đang đợi đối phương nhấc máy...</span>
      </div>
      <span class="user-label">Đối phương</span>
    </div>

    <div class="local-video-container shadow-lg">
      <video ref="localVideo" autoplay playsinline muted class="local-video"></video>
      <span class="user-label-small">Bạn</span>
    </div>

    <div class="controls-bar">
      <div class="controls-pill">
        <button @click="toggleMic" :class="{ 'btn-off': !isMicOn }" class="control-btn" title="Micro">
          <svg v-if="isMicOn" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"></path><path d="M19 10v2a7 7 0 0 1-14 0v-2"></path><line x1="12" y1="19" x2="12" y2="23"></line><line x1="8" y1="23" x2="16" y2="23"></line></svg>
          <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="1" y1="1" x2="23" y2="23"></line><path d="M9 9v3a3 3 0 0 0 5.12 2.12M15 9.34V4a3 3 0 0 0-5.94-.6"></path><path d="M17 16.95A7 7 0 0 1 5 12v-2m14 0v2a7 7 0 0 1-.11 1.23"></path><line x1="12" y1="19" x2="12" y2="23"></line><line x1="8" y1="23" x2="16" y2="23"></line></svg>
        </button>

        <button @click="toggleCam" :class="{ 'btn-off': !isCamOn }" class="control-btn" title="Camera">
          <svg v-if="isCamOn" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 7l-7 5 7 5V7z"></path><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
          <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 16v1a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h2m5.66 0H14a2 2 0 0 1 2 2v3.34l1 1L23 7v10"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
        </button>

        <button @click="endCall" class="control-btn btn-end" title="Gác máy">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M10.68 13.31a16 16 0 0 0 3.41 2.6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7 2 2 0 0 1 1.72 2v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.42 19.42 0 0 1-3.33-2.67m-2.67-3.34a19.79 19.79 0 0 1-3.07-8.63A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91"></path><line x1="23" y1="1" x2="1" y2="23"></line></svg>
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

// --- TRẠNG THÁI ---
const localVideo = ref(null);
const remoteVideo = ref(null);
const isMicOn = ref(true);
const isCamOn = ref(true);
const remoteStreamActive = ref(false);
const isStarted = ref(false);

let localStream = null;
let peerConnection = null;
let stompClient = null;
let callStartTime = null; // 🚀 THÊM: Biến lưu thời gian bắt đầu cuộc gọi

const conversationId = route.params.roomID;
const currentUserId = Number(authStore.user?.accountID || authStore.user?.id);

const isCaller = computed(() => route.query.role === 'caller');

const rtcConfig = {
  iceServers: [
    { urls: 'stun:stun.l.google.com:19302' },
    { urls: 'stun:stun1.l.google.com:19302' },
  ]
};

// --- KHỞI CHẠY ---
onMounted(async () => {
  await initLocalStream();
  connectSignalingServer();
});

// --- LẤY LUỒNG CAMERA/MICRO ---
const initLocalStream = async () => {
  try {
    localStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
    if (localVideo.value) localVideo.value.srcObject = localStream;
  } catch (err) {
    console.error("Lỗi cam:", err);
    toast.error("Không thể truy cập Camera/Micro!");
  }
};

// --- KẾT NỐI SERVER TÍN HIỆU (SIGNALING) ---
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
            console.log("🎯 Đối phương đã nhấc máy, tiến hành gửi Offer...");
            startCall();
          }
          break;
        case 'offer':
          await handleOffer(signal.data);
          break;
        case 'answer':
          await handleAnswer(signal.data);
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
      setTimeout(() => {
        console.log("👋 Đã kết nối STOMP xong, gửi tín hiệu Joined...");
        sendSignal('joined', {});
      }, 500); 
    } else {
      console.log("⏳ Đang kiên nhẫn đợi đối phương nhấc máy...");
    }
  });
};

const sendSignal = (type, data) => {
  if (stompClient && stompClient.connected) {
    const payload = { type, conversationId, senderId: currentUserId, data };
    stompClient.send("/app/call.signaling", {}, JSON.stringify(payload));
  }
};

// --- WEBRTC CORE ---
const createPeerConnection = () => {
  if (peerConnection) return;

  peerConnection = new RTCPeerConnection(rtcConfig);

  peerConnection.ontrack = (event) => {
    if (remoteVideo.value) {
      remoteVideo.value.srcObject = event.streams[0];
      remoteStreamActive.value = true;
      
      // 🚀 BẮT ĐẦU TÍNH GIỜ KHI THẤY HÌNH ĐỐI PHƯƠNG
      if (!callStartTime) {
        callStartTime = Date.now();
        console.log("⏱️ Bắt đầu tính giờ cuộc gọi...");
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

// NGƯỜI GỌI TẠO OFFER
const startCall = async () => {
  if (isStarted.value) return;
  isStarted.value = true;
  console.log("🚀 Đang gửi Offer...");
  createPeerConnection();
  const offer = await peerConnection.createOffer();
  await peerConnection.setLocalDescription(offer);
  sendSignal('offer', offer);
};

// NGƯỜI NHẬN NHẬN OFFER VÀ TẠO ANSWER
const handleOffer = async (offer) => {
  if (isStarted.value) return;
  isStarted.value = true;
  console.log("📥 Nhận Offer, đang gửi Answer...");
  createPeerConnection();
  
  await peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
  const answer = await peerConnection.createAnswer();
  await peerConnection.setLocalDescription(answer);
  sendSignal('answer', answer);
};

// NGƯỜI GỌI NHẬN ANSWER
const handleAnswer = async (answer) => {
  console.log("📥 Nhận Answer, thiết lập kết nối...");
  await peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
};

const handleCandidate = async (candidate) => {
  try {
    if (peerConnection && peerConnection.remoteDescription) {
      await peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
    }
  } catch (e) { console.error("Lỗi Candidate:", e); }
};

// --- ĐIỀU KHIỂN & CLEANUP ---
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
  // 🚀 LOGIC LƯU LOG CUỘC GỌI VÀO DATABASE
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

    console.log("📝 Gửi lưu log cuộc gọi:", content);
    stompClient.send("/app/chat.sendMessage", { 'Authorization': `Bearer ${authStore.user?.token}` }, JSON.stringify(chatMessage));
  }

  // --- DỌN DẸP KẾT NỐI ---
  if (stompClient) stompClient.disconnect();
  if (peerConnection) {
    peerConnection.close();
    peerConnection = null;
  }
  if (localStream) {
    localStream.getTracks().forEach(track => track.stop());
  }
  router.push('/home');
};

onBeforeUnmount(() => {
  if (stompClient?.connected) endCall();
  else closeAndExit();
});
</script>

<style scoped>
.webrtc-container {
  height: 100vh; width: 100vw; background-color: #0b0b0b;
  position: relative; overflow: hidden; font-family: 'Inter', 'Segoe UI', sans-serif;
}

.remote-video-container {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background-color: #000; display: flex; justify-content: center; align-items: center;
}
.remote-video { width: 100%; height: 100%; object-fit: cover; }

.local-video-container {
  position: absolute; top: 25px; right: 25px; width: 260px; height: 160px;
  background: #1e1e1e; border-radius: 18px; overflow: hidden;
  border: 2px solid rgba(255, 138, 0, 0.4); z-index: 10; box-shadow: 0 12px 40px rgba(0,0,0,0.6);
}
.local-video { width: 100%; height: 100%; object-fit: cover; transform: scaleX(-1); }

.controls-bar {
  position: absolute; bottom: 40px; width: 100%; display: flex; justify-content: center; z-index: 100;
}
.controls-pill {
  background: rgba(28, 28, 28, 0.8); backdrop-filter: blur(15px);
  padding: 14px 28px; border-radius: 50px; display: flex; gap: 24px; border: 1px solid rgba(255, 255, 255, 0.1);
}
.control-btn {
  width: 54px; height: 54px; border-radius: 50%; border: none; background: #3a3a3a;
  color: white; display: flex; justify-content: center; align-items: center; cursor: pointer;
  transition: all 0.3s ease;
}
.control-btn:hover { background: #4a4a4a; transform: translateY(-5px); }
.control-btn:not(.btn-end):not(.btn-off) { color: #ff8a00; }
.btn-off { background: rgba(255, 255, 255, 0.15); color: #777 !important; }
.btn-end { background: #ff3b30; }
.btn-end:hover { background: #ff453a; }

.user-label, .user-label-small {
  position: absolute; bottom: 12px; left: 15px; background: rgba(0,0,0,0.6);
  padding: 4px 12px; border-radius: 6px; font-size: 13px; color: white;
}

.waiting-overlay { position: absolute; text-align: center; color: #ff8a00; z-index: 5; }
.spinner {
  width: 50px; height: 50px; border: 4px solid rgba(255, 138, 0, 0.1);
  border-top: 4px solid #ff8a00; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto 15px;
}
.calling-text { display: block; margin-top: 10px; font-size: 0.9rem; color: #aaa; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

@media (max-width: 768px) {
  .local-video-container { width: 140px; height: 200px; bottom: 120px; top: auto; right: 20px; }
}
</style>