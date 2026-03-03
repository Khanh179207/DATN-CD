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
          <button class="btn-icon" @click.stop="isMinimized = !isMinimized" :title="isMinimized ? 'Phóng to' : 'Thu nhỏ'">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line v-if="!isMinimized" x1="5" y1="12" x2="19" y2="12"></line>
              <polyline v-else points="18 15 12 9 6 15"></polyline>
            </svg>
          </button>
          <button class="btn-icon close" @click.stop="closeChat" title="Đóng">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
      </div>

      <div v-show="!isMinimized" class="chat-body-wrapper">
        <div class="chat-messages scroll-body" ref="msgContainer">
          <div class="intro">
            <img :src="chatStore.activeChat.avatar || 'https://ui-avatars.com/api/?name=User&background=random'" alt="Avatar">
            <h5>{{ chatStore.activeChat.name }}</h5>
            <p>{{ $t('chat.friends_on_gomet', 'Các bạn đã trở thành bạn bè trên Gomet') }}</p>
          </div>
          
          <div 
            v-for="(msg, i) in messages" 
            :key="i" 
            class="msg-item" 
            :class="{ 
              'mine': msg.isMine, 
              'is-last': i === messages.length - 1 || messages[i+1]?.isMine !== msg.isMine 
            }"
          >
            <div class="msg-content">
              <div class="msg-avatar-container" v-if="!msg.isMine">
                <img 
                  v-if="i === messages.length - 1 || messages[i+1]?.isMine" 
                  :src="chatStore.activeChat.avatar || 'https://ui-avatars.com/api/?name=User'" 
                  class="msg-avatar"
                >
              </div>
              <div class="bubble" :title="msg.timeStr">{{ msg.text }}</div>
            </div>

            <div class="msg-time" v-if="i === messages.length - 1 || messages[i+1]?.isMine !== msg.isMine">
              {{ msg.timeStr }}
            </div>
          </div>

          <div v-if="isTyping" class="msg-item not-mine is-last">
            <div class="msg-content">
              <div class="msg-avatar-container">
                <img :src="chatStore.activeChat.avatar" class="msg-avatar">
              </div>
              <div class="bubble typing-bubble">
                <span class="dot"></span><span class="dot"></span><span class="dot"></span>
              </div>
            </div>
          </div>
        </div>

        <div class="chat-footer">
          <div class="input-actions">
            <button class="btn-action-icon" title="Emoji"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><path d="M8 14s1.5 2 4 2 4-2 4-2"></path><line x1="9" y1="9" x2="9.01" y2="9"></line><line x1="15" y1="9" x2="15.01" y2="9"></line></svg></button>
            <button class="btn-action-icon" title="Gửi ảnh"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg></button>
          </div>
          <div class="input-wrapper">
            <input 
              v-model="inputMsg" 
              placeholder="Nhập tin nhắn..." 
              @keyup.enter="sendMsg"
              :disabled="isTyping"
              ref="chatInput"
            >
          </div>
          <button class="btn-send" @click="sendMsg" :disabled="isTyping || !inputMsg.trim()" title="Gửi">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"></path></svg>
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch, nextTick, computed, onUnmounted } from 'vue'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth' 
import { chatWithAIChef } from '@/services/aiService'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import axios from 'axios'

const chatStore = useChatStore()
const authStore = useAuthStore()
const isMinimized = ref(false)
const inputMsg = ref('')
const msgContainer = ref(null)
const chatInput = ref(null)
const isTyping = ref(false)
const messages = ref([])
const stompClient = ref(null)

// Lấy ID người dùng hiện tại (ép kiểu Number để so sánh chính xác)
const currentUserId = computed(() => {
  const id = authStore.user?.accountID || authStore.user?.id;
  return id ? Number(id) : null;
})

const isAiChat = computed(() => chatStore.activeChat?.id === 'gomet-ai')

const formatTime = (dateInput) => {
  if (!dateInput) return 'vừa xong'
  const date = new Date(dateInput)
  if (isNaN(date.getTime())) return 'vừa xong'
  return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
}

/**
 * HÀM QUAN TRỌNG: Map dữ liệu từ Backend Entity sang format hiển thị của Vue
 */
// Trong MiniChatBox.vue
const mapMessage = (msg) => {
  return {
    text: msg.content, // Phải là .content vì Message Entity của sếp dùng content
    isMine: Number(msg.sender?.accountID) === currentUserId.value,
    timeStr: formatTime(msg.createdAt)
  };
}

const connectWebSocket = (conversationId) => {
  if (stompClient.value) stompClient.value.disconnect()

  const socket = new SockJS('http://localhost:8080/ws-chat')
  stompClient.value = Stomp.over(socket)
  stompClient.value.debug = null 

  stompClient.value.connect({}, () => {
    stompClient.value.subscribe(`/topic/${conversationId}`, (payload) => {
      const receivedMsg = JSON.parse(payload.body)
      
      const senderId = Number(receivedMsg.sender?.accountID || receivedMsg.senderID)
      // Chỉ push nếu là tin nhắn của đối phương (vì mình đã tự push local khi nhấn gửi)
      if (senderId !== currentUserId.value) {
        messages.value.push(mapMessage(receivedMsg))
        scrollToBottom()
      }
    })
  })
}

/**
 * HÀM LẤY LỊCH SỬ: Đã thêm log để debug
 */
const fetchHistory = async (convId) => {
  if (!convId || convId === 'undefined' || convId === 'gomet-ai') return;

  try {
    const res = await axios.get(`http://localhost:8080/api/messages/${convId}`);
    console.log("Dữ liệu lịch sử từ Backend:", res.data); // Xem dữ liệu thô ở F12

    // Chuyển đổi toàn bộ mảng tin nhắn cũ sang format hiển thị
    messages.value = res.data.map(msg => mapMessage(msg));
    
    scrollToBottom();
  } catch (err) {
    console.error("Lỗi khi tải lịch sử chat:", err);
  }
}

// Theo dõi thay đổi của Chat đang chọn
watch(() => chatStore.activeChat, async (newVal) => {
  if (newVal) {
    isMinimized.value = false;
    messages.value = []; // Reset tin nhắn cũ để tránh bị "râu ông nọ cắm cằm bà kia"
    
    const convId = newVal.id || newVal.conversationID;

    if (convId === 'gomet-ai') {
      messages.value = [{ text: '👋 Xin chào! Tôi là Gomet AI, tôi có thể giúp gì cho bạn?', isMine: false, timeStr: formatTime(new Date()) }]
    } else if (convId) {
      // Gọi API lấy lịch sử trước, sau đó mới kết nối socket
      await fetchHistory(convId);
      connectWebSocket(convId);
    }
    
    nextTick(() => { if(chatInput.value) chatInput.value.focus() });
  }
}, { immediate: true })

const sendMsg = async () => {
  const text = inputMsg.value.trim()
  if (!text || !chatStore.activeChat) return

  const conversationId = chatStore.activeChat.id || chatStore.activeChat.conversationID;
  
  // Hiển thị tin nhắn của mình ngay lập tức lên giao diện (Optimistic UI)
  messages.value.push({ text, isMine: true, timeStr: formatTime(new Date()) })
  inputMsg.value = ''
  scrollToBottom()

  if (isAiChat.value) {
    isTyping.value = true
    try {
      const reply = await chatWithAIChef(messages.value.slice(1, -1), text)
      messages.value.push({ text: reply, isMine: false, timeStr: formatTime(new Date()) })
    } catch (err) { 
      messages.value.push({ text: 'Có lỗi xảy ra với AI!', isMine: false, timeStr: formatTime(new Date()) })
    } finally { 
      isTyping.value = false
      scrollToBottom() 
    }
  } else if (stompClient.value?.connected) {
    const chatMessage = {
      content: text,
      conversation: { conversationID: conversationId },
      sender: { accountID: currentUserId.value }
    }
    stompClient.value.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage))
  }
}

const closeChat = () => {
  if (stompClient.value) stompClient.value.disconnect()
  chatStore.closeChat()
}

const scrollToBottom = async () => {
  await nextTick()
  if (msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}

onUnmounted(() => { if (stompClient.value) stompClient.value.disconnect() })
</script>

<style scoped lang="scss" src="./MiniChatBox.scss"></style>