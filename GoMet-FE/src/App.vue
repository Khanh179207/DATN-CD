<template>
  <div id="app">
    <router-view />
    <ToastContainer />
    
    <IncomingCallModal 
      v-if="incomingCallData" 
      :callData="incomingCallData" 
      @accept="handleAcceptCall" 
      @decline="handleDeclineCall"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import ToastContainer from '@/components/common/ToastContainer.vue'
import IncomingCallModal from '@/components/modals/IncomingCallModal.vue'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()
const incomingCallData = ref(null)
let stompClient = null
let tempCallSub = null

// --- 📡 SIGNALING LISTENER ---
const initCallSignaling = () => {
  if (stompClient) return; // Tránh tạo nhiều kết nối

  const socket = new SockJS('http://localhost:8080/ws-chat')
  stompClient = Stomp.over(socket)
  stompClient.debug = null // Tắt log cho sạch console

  stompClient.connect({ 'Authorization': `Bearer ${authStore.user?.token}` }, () => {
    // 🚀 ĐĂNG KÝ KÊNH RIÊNG: Lắng nghe cuộc gọi gửi tới mình
    const myID = authStore.user?.accountID || authStore.user?.id;
    
    stompClient.subscribe(`/topic/user/${myID}`, (msg) => {
      const signal = JSON.parse(msg.body);
      
      // Chỉ hiện rung chuông nếu là lời mời (invite) và mình không trong trang gọi
      if (signal.type === 'invite' && route.name !== 'VideoCall') {
        incomingCallData.value = signal;
        
        // Lắng nghe thêm kênh của phòng xem người gọi có cúp máy giữa chừng không
        if (tempCallSub) tempCallSub.unsubscribe();
        tempCallSub = stompClient.subscribe(`/topic/${signal.conversationId}`, (roomMsg) => {
          const roomSignal = JSON.parse(roomMsg.body);
          if (roomSignal.type === 'hangup' && incomingCallData.value) {
             incomingCallData.value = null; // Tự động đóng modal nếu bên kia gác máy
          }
        });
      }
    });
  });
}

onMounted(() => {
  authStore.refreshProfile()
  if (authStore.user) initCallSignaling()
})

// Theo dõi khi login xong thì bật ăng-ten ngay
watch(() => authStore.user, (newVal) => {
  if (newVal) initCallSignaling()
})

const handleAcceptCall = () => {
  const roomID = incomingCallData.value.conversationId;
  incomingCallData.value = null; // Tắt modal
  if (tempCallSub) { tempCallSub.unsubscribe(); tempCallSub = null; }
  
  // Chuyển hướng vào phòng gọi
  router.push({ path: `/call/${roomID}`, query: { role: 'receiver' } });
}

const handleDeclineCall = () => {
  if (stompClient && incomingCallData.value) {
    const declineSignal = {
      type: 'decline',
      conversationId: incomingCallData.value.conversationId
    };
    stompClient.send("/app/call.signaling", { 'Authorization': `Bearer ${authStore.user?.token}` }, JSON.stringify(declineSignal));
  }
  incomingCallData.value = null;
  if (tempCallSub) { tempCallSub.unsubscribe(); tempCallSub = null; }
}
</script>