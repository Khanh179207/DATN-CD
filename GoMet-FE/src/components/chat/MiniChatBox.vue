<template>
  <transition name="slide-up">
    <div v-if="chatStore.activeChat" class="mini-chat-box" :class="{ 'is-minimized': isMinimized }">
      
      <div class="chat-head" @click="isMinimized = !isMinimized">
        <div class="user-info">
          <div class="avt-wrap">
            <img :src="chatStore.activeChat.avatar || 'https://ui-avatars.com/api/?name=User&background=random'" alt="Avatar">
            <span class="dot-status" :class="{ offline: !chatStore.activeChat.online }"></span>
          </div>
          <div class="name-col">
            <h4>{{ chatStore.activeChat.name }}</h4>
            <span class="status-text">{{ chatStore.activeChat.online ? $t('chat.active_now') : 'Hoạt động gần đây' }}</span>
          </div>
        </div>

        <div class="actions">
          <button 
            class="btn-icon call-btn" 
            :class="{ 'is-locked': !isMutualFollow }"
            @click.stop="handleInitiateCall"
            :title="isMutualFollow ? 'Gọi video' : 'Theo dõi nhau để gọi video'"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
              <path d="M23 7l-7 5 7 5V7z"></path>
              <rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect>
            </svg>
            <span v-if="!isMutualFollow" class="lock-badge">🔒</span>
          </button>

          <button class="btn-icon" @click.stop="isMinimized = !isMinimized">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line v-if="!isMinimized" x1="5" y1="12" x2="19" y2="12"></line>
              <polyline v-else points="18 15 12 9 6 15"></polyline>
            </svg>
          </button>
          <button class="btn-icon close" @click.stop="closeChat">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
      </div>

      <div v-show="!isMinimized" class="chat-body-wrapper">
        <div class="chat-messages scroll-body" ref="msgContainer">
          <div class="intro">
            <img :src="chatStore.activeChat.avatar || 'https://ui-avatars.com/api/?name=User'" alt="Avatar">
            <h5>{{ chatStore.activeChat.name }}</h5>
            <p v-if="isMutualFollow">{{ $t('chat.friends_on_gomet', 'Các bạn đã trở thành bạn bè trên Gomet') }}</p>
            <p v-else style="color: #ea580c; font-size: 0.8rem;">Hãy theo dõi nhau để mở khóa gọi video!</p>
          </div>
          
          <div 
            v-for="(msg, i) in messages" 
            :key="i" 
            class="msg-item" 
            :class="{ 
              'mine': msg.isMine, 
              'is-last': i === messages.length - 1 || messages[i+1]?.isMine !== msg.isMine,
              'sys-wrap': isCallSystemMsg(msg.text) 
            }"
          >
            <div v-if="isCallSystemMsg(msg.text)" class="sys-call-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
              </svg>
              Cuộc gọi video kết thúc ({{ getCallDuration(msg.text) }})
            </div>

            <template v-else>
              <div class="msg-content">
                <div class="msg-avatar-container" v-if="!msg.isMine">
                  <img v-if="i === messages.length - 1 || messages[i+1]?.isMine" :src="chatStore.activeChat.avatar || 'https://ui-avatars.com/api/?name=User'" class="msg-avatar">
                </div>
                <div class="bubble" :title="msg.timeStr">
                  <template v-if="isPostShare(msg.text)">
                    <MiniPostCard :postID="getPostId(msg.text)" />
                  </template>
                  <template v-else>{{ msg.text }}</template>
                </div>
              </div>
              <div class="msg-time" v-if="i === messages.length - 1 || messages[i+1]?.isMine !== msg.isMine">{{ msg.timeStr }}</div>
            </template>
          </div>
        </div>

        <div class="chat-footer">
          <div class="input-actions">
            <button class="btn-action-icon"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><path d="M8 14s1.5 2 4 2 4-2 4-2"></path></svg></button>
            <button class="btn-action-icon"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle></svg></button>
          </div>
          <div class="input-wrapper">
            <input v-model="inputMsg" placeholder="Nhập tin nhắn..." @keyup.enter="sendMsg" ref="chatInput">
          </div>
          <button class="btn-send" @click="sendMsg" :disabled="!inputMsg.trim()">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"></path></svg>
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch, nextTick, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth' 
import { toast } from '@/composables/useToast'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import api from '@/services/api'
import MiniPostCard from './MiniPostCard.vue'

const chatStore = useChatStore()
const authStore = useAuthStore()
const router = useRouter()

const isMinimized = ref(false)
const inputMsg = ref('')
const msgContainer = ref(null)
const chatInput = ref(null)
const messages = ref([])
const stompClient = ref(null)

const followStatus = ref({ following: false, followed: false })

const isMutualFollow = computed(() => {
  return followStatus.value.following === true && followStatus.value.followed === true;
})

const currentUserId = computed(() => {
  const id = authStore.user?.accountID || authStore.user?.id;
  return id ? Number(id) : null;
})

const getAuthHeaders = () => ({ 'Authorization': `Bearer ${authStore.user?.token}` })

// --- GỌI VIDEO ---
const handleInitiateCall = () => {
  if (!isMutualFollow.value) {
    toast.warn("Cần theo dõi nhau để gọi video!");
    return;
  }
  const conversationId = chatStore.activeChat.id || chatStore.activeChat.conversationID;
  
  if (stompClient.value?.connected) {
    const inviteSignal = {
      type: 'invite',
      conversationId: conversationId,
      senderId: currentUserId.value,
      senderName: authStore.user.username,
      senderAvatar: authStore.user.avatar
    };
    stompClient.value.send("/app/call.signaling", getAuthHeaders(), JSON.stringify(inviteSignal));
    
    closeChat(); 
    
    router.push({ path: `/call/${conversationId}`, query: { role: 'caller' } });
  } else {
    toast.error("Mất kết nối server!");
  }
}

// --- KẾT NỐI WEBSOCKET & HISTORY ---
const connectWebSocket = (conversationId) => {
  if (stompClient.value) stompClient.value.disconnect()
  const socket = new SockJS('http://localhost:8080/ws-chat')
  stompClient.value = Stomp.over(socket)
  stompClient.value.debug = null 
  stompClient.value.connect(getAuthHeaders(), () => {
    processSpecialQueue();
    stompClient.value.subscribe(`/topic/${conversationId}`, (payload) => {
      const receivedMsg = JSON.parse(payload.body)
      if (!receivedMsg.type) { 
        const senderId = Number(receivedMsg.sender?.accountID || receivedMsg.senderID)
        if (senderId !== currentUserId.value) {
          messages.value.push(mapMessage(receivedMsg));
          scrollToBottom();
        }
      }
    })
  })
}

const fetchHistory = async (convId) => {
  if (!convId || convId === 'undefined') return;
  try {
    const res = await api.get(`/api/messages/${convId}`);
    const rawData = res.data?.data || res.data;
    if (Array.isArray(rawData)) {
      messages.value = rawData.map(msg => mapMessage(msg));
      scrollToBottom();
    }
  } catch (err) { console.error("Lỗi lịch sử:", err); }
}

watch(() => chatStore.activeChat, async (newVal, oldVal) => {
  if (newVal) {
    isMinimized.value = false;
    
    const myId = currentUserId.value;
    const partnerId = Number(newVal.partnerID); 
    const convId = newVal.id || newVal.conversationID;
    const oldConvId = oldVal?.id || oldVal?.conversationID;

    followStatus.value = { following: false, followed: false };

    followStatus.value = {
      following: newVal.following === true || newVal.isFollowing === true,
      followed: newVal.followed === true || newVal.isFollowed === true
    };

    if (partnerId && myId) {
      try {
        const res = await api.get(`/api/follows/check-mutual/${partnerId}`, { params: { myId } });
        followStatus.value = {
          following: res.data.following === true,
          followed: res.data.followed === true
        };
      } catch (err) {
        console.error("❌ Lỗi check follow:", err);
      }
    }

    if (convId !== oldConvId) {
      messages.value = []; 
      if (convId) { connectWebSocket(convId); fetchHistory(convId); }
    }
    nextTick(() => { if(chatInput.value) chatInput.value.focus() });
  }
}, { immediate: true })

// --- CÁC HÀM PHỤ TRỢ ---
const formatTime = (dateInput) => {
  if (!dateInput) return 'vừa xong'
  const date = new Date(dateInput)
  return isNaN(date.getTime()) ? 'vừa xong' : date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
}

const mapMessage = (msg) => ({
  text: msg.content, 
  isMine: Number(msg.sender?.accountID) === currentUserId.value,
  timeStr: formatTime(msg.createdAt)
})

// 🚀 KIỂM TRA VÀ LẤY DỮ LIỆU TIN NHẮN HỆ THỐNG
const isCallSystemMsg = (text) => text && text.startsWith('[SYS_CALL:') && text.endsWith(']');
const getCallDuration = (text) => {
  const match = text.match(/\[SYS_CALL:(.+)\]/);
  return match ? match[1] : '00:00';
};

const isPostShare = (text) => text && text.startsWith('[POST_SHARE_ID:') && text.endsWith(']')
const getPostId = (text) => {
  const match = text.match(/\[POST_SHARE_ID:(\d+)\]/)
  return match ? match[1] : null
}

const sendMsg = () => {
  const text = inputMsg.value.trim()
  if (!text || !chatStore.activeChat) return
  const conversationId = chatStore.activeChat.id || chatStore.activeChat.conversationID;
  messages.value.push({ text, isMine: true, timeStr: formatTime(new Date()) })
  inputMsg.value = ''
  scrollToBottom()
  if (stompClient.value?.connected) {
    const chatMessage = { content: text, conversation: { conversationID: conversationId }, sender: { accountID: currentUserId.value } }
    stompClient.value.send("/app/chat.sendMessage", getAuthHeaders(), JSON.stringify(chatMessage));
  }
}

const scrollToBottom = async () => {
  await nextTick();
  if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight;
}

const closeChat = () => { if (stompClient.value) stompClient.value.disconnect(); chatStore.closeChat() }

const processSpecialQueue = () => {
    if (!chatStore.specialMessageQueue || chatStore.specialMessageQueue.length === 0) return;
    const activeConvId = chatStore.activeChat?.id || chatStore.activeChat?.conversationID;
    chatStore.specialMessageQueue.forEach(msg => {
        if (msg.conversation.conversationID === activeConvId && stompClient.value?.connected) {
            messages.value.push({ text: msg.content, isMine: true, timeStr: formatTime(new Date()) });
            scrollToBottom();
            stompClient.value.send("/app/chat.sendMessage", getAuthHeaders(), JSON.stringify(msg));
            chatStore.clearSpecialMessage(msg.timestamp);
        }
    });
};

watch(() => chatStore.specialMessageQueue, () => { processSpecialQueue(); }, { deep: true });
onUnmounted(() => { if (stompClient.value) stompClient.value.disconnect(); })
</script>

<style scoped>
/* 🚀 THÊM CSS CHO LABEL CUỘC GỌI VÀO ĐÂY NẾU CHƯA CÓ TRONG FILE SCSS */
.sys-wrap {
  justify-content: center !important;
  margin: 15px 0 !important;
}

.sys-call-label {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 138, 0, 0.1);
  color: #ff8a00;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  margin: 0 auto;
}
</style>
<style scoped lang="scss" src="./MiniChatBox.scss"></style>