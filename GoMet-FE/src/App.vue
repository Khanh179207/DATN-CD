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

// --- 📡 VIDEO CALL STATE & SIGNALLING ---
const incomingCallData = ref(null)
let stompClient = null
let tempCallSub = null

const initCallSignaling = () => {
  if (stompClient) return;

  const socket = new SockJS('http://localhost:8080/ws-chat')
  stompClient = Stomp.over(socket)
  stompClient.debug = null

  stompClient.connect({ 'Authorization': `Bearer ${authStore.user?.token}` }, () => {
    const myID = authStore.user?.accountID || authStore.user?.id;
    
    stompClient.subscribe(`/topic/user/${myID}`, (msg) => {
      const signal = JSON.parse(msg.body);
      
      if (signal.type === 'invite' && route.name !== 'VideoCall') {
        incomingCallData.value = signal;
        
        if (tempCallSub) tempCallSub.unsubscribe();
        tempCallSub = stompClient.subscribe(`/topic/${signal.conversationId}`, (roomMsg) => {
          const roomSignal = JSON.parse(roomMsg.body);
          if (roomSignal.type === 'hangup' && incomingCallData.value) {
             incomingCallData.value = null;
          }
        });
      }
    });
  });
}

onMounted(() => {
  authStore.refreshProfile()

  // 🚀 1. LOGIC ĐIỀU HƯỚNG THÔNG MINH (Giữ lại bản mới của develop)
  const isMobileOrTablet = window.innerWidth < 1024
  if (isMobileOrTablet && route.path === '/') {
    router.push('/home')
  }

  if (authStore.user) initCallSignaling()
})

// 🚀 SỬA: Theo dõi đăng nhập/đăng xuất để Bật/Tắt ăng-ten
watch(() => authStore.user, (newVal) => {
  if (newVal) {
    initCallSignaling();
  } else {
    if (stompClient) {
      stompClient.disconnect();
      stompClient = null;
    }
  }
}, { immediate: true })

const handleAcceptCall = () => {
  const roomID = incomingCallData.value.conversationId;
  const callerName = incomingCallData.value.senderName;

  incomingCallData.value = null;
  if (tempCallSub) { tempCallSub.unsubscribe(); tempCallSub = null; }
  
  const routeData = router.resolve({ 
    path: `/call/${roomID}`, 
    query: { 
      role: 'receiver',
      partnerName: callerName
    } 
  });
  
  window.open(routeData.href, 'GoMetVideoCall', 'width=1100,height=750,menubar=no,toolbar=no,location=no,status=no,resizable=yes');
}

const handleDeclineCall = () => {
  if (stompClient && incomingCallData.value) {
    const declineSignal = {
      type: 'decline',
      conversationId: incomingCallData.value.conversationId,
      senderId: authStore.user?.accountID || authStore.user?.id 
    };
    stompClient.send("/app/call.signaling", { 'Authorization': `Bearer ${authStore.user?.token}` }, JSON.stringify(declineSignal));
  }
  
  incomingCallData.value = null;
  if (tempCallSub) { tempCallSub.unsubscribe(); tempCallSub = null; }
}
</script>