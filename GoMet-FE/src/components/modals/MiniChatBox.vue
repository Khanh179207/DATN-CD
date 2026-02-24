<template>
  <transition name="slide-up">
    <div v-if="chatStore.activeChat" class="mini-chat-box">
      
      <div class="chat-head" @click="isMinimized = !isMinimized">
        <div class="user-info">
          <div class="avt-wrap">
            <img :src="chatStore.activeChat.avatar">
            <span class="dot-status"></span>
          </div>
          <div class="name-col">
            <h4>{{ chatStore.activeChat.name }}</h4>
            <span class="status-text">Đang hoạt động</span>
          </div>
        </div>
        <div class="actions">
          <button class="btn-icon" @click.stop="isMinimized = !isMinimized">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"></line></svg>
          </button>
          <button class="btn-icon close" @click.stop="chatStore.closeChat">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
      </div>

      <div v-show="!isMinimized" class="chat-body-wrapper">
        <div class="chat-messages" ref="msgContainer">
          <div class="intro">
            <img :src="chatStore.activeChat.avatar">
            <p>Các bạn là bạn bè trên GOMET</p>
          </div>
          
          <div v-for="(msg, i) in messages" :key="i" class="msg-row" :class="{ mine: msg.isMine }">
            <div class="bubble">{{ msg.text }}</div>
          </div>
        </div>

        <div class="chat-footer">
          <input 
            v-model="inputMsg" 
            placeholder="Aa" 
            @keyup.enter="sendMsg"
          >
          <button class="btn-send" @click="sendMsg">🚀</button>
        </div>
      </div>

    </div>
  </transition>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { useChatStore } from '@/stores/chat'

const chatStore = useChatStore()
const isMinimized = ref(false)
const inputMsg = ref('')
const msgContainer = ref(null)

const messages = ref([
  { text: 'Xin chào!', isMine: false },
  { text: 'Chào bạn, món ăn ngon quá', isMine: true }
])

// Khi đổi người chat, reset tin nhắn demo
watch(() => chatStore.activeChat, (newVal) => {
  if (newVal) {
    isMinimized.value = false
    scrollToBottom()
  }
})

const sendMsg = () => {
  if(!inputMsg.value.trim()) return
  messages.value.push({ text: inputMsg.value, isMine: true })
  inputMsg.value = ''
  scrollToBottom()
}

const scrollToBottom = async () => {
  await nextTick()
  if(msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight
}
</script>

<style scoped>
.mini-chat-box {
  position: fixed; bottom: 0; right: 80px; width: 330px; 
  background: white; border-radius: 16px 16px 0 0;
  box-shadow: 0 5px 40px rgba(0,0,0,0.15); z-index: 10000;
  border: 1px solid #E5E7EB; display: flex; flex-direction: column;
  font-family: 'Mulish', sans-serif;
}

/* Header */
.chat-head {
  padding: 10px 15px; border-bottom: 1px solid #E5E7EB;
  display: flex; justify-content: space-between; align-items: center;
  cursor: pointer; background: white; border-radius: 16px 16px 0 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.02);
}
.chat-head:hover { background: #F9FAFB; }

.user-info { display: flex; align-items: center; gap: 10px; }
.avt-wrap { position: relative; }
.avt-wrap img { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; }
.dot-status { position: absolute; bottom: 0; right: 0; width: 10px; height: 10px; background: #22C55E; border: 2px solid white; border-radius: 50%; }
.name-col h4 { margin: 0; font-size: 0.95rem; font-weight: 700; color: #1C1917; }
.status-text { font-size: 0.75rem; color: #6B7280; }

.actions { display: flex; gap: 5px; }
.btn-icon { background: none; border: none; color: #EA580C; cursor: pointer; padding: 4px; border-radius: 50%; display: flex; }
.btn-icon:hover { background: #FFF7ED; }
.btn-icon.close:hover { color: #EF4444; background: #FEF2F2; }

/* Body */
.chat-body-wrapper { height: 400px; display: flex; flex-direction: column; background: white; }
.chat-messages { flex: 1; overflow-y: auto; padding: 15px; display: flex; flex-direction: column; gap: 10px; }

.intro { text-align: center; margin-top: 20px; margin-bottom: 30px; opacity: 0.7; }
.intro img { width: 60px; height: 60px; border-radius: 50%; margin-bottom: 10px; }
.intro p { font-size: 0.8rem; margin: 0; }

.msg-row { display: flex; }
.msg-row.mine { justify-content: flex-end; }
.bubble { padding: 8px 14px; border-radius: 18px; font-size: 0.9rem; max-width: 80%; }
.msg-row:not(.mine) .bubble { background: #F3F4F6; color: #1C1917; }
.msg-row.mine .bubble { background: #EA580C; color: white; }

/* Footer */
.chat-footer { padding: 12px; border-top: 1px solid #E5E7EB; display: flex; gap: 10px; }
.chat-footer input { flex: 1; border: none; background: #F3F4F6; padding: 8px 15px; border-radius: 20px; outline: none; font-size: 0.9rem; }
.btn-send { border: none; background: none; font-size: 1.2rem; cursor: pointer; }

/* Animation */
.slide-up-enter-active, .slide-up-leave-active { transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.slide-up-enter-from, .slide-up-leave-to { transform: translateY(100%); opacity: 0; }
</style>