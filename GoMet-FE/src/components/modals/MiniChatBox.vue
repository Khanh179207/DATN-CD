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
            <span class="status-text">{{ $t('chat.active_now') }}</span>
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
            <p>{{ $t('chat.friends_on_gomet') }}</p>
          </div>
          
          <div v-for="(msg, i) in messages" :key="i" class="msg-row" :class="{ mine: msg.isMine }">
            <div class="bubble">{{ msg.text }}</div>
          </div>

          <!-- AI typing indicator -->
          <div v-if="isTyping" class="msg-row">
            <div class="bubble typing-bubble">
              <span class="dot"></span><span class="dot"></span><span class="dot"></span>
            </div>
          </div>
        </div>

        <div class="chat-footer">
          <input 
            v-model="inputMsg" 
            placeholder="Aa" 
            @keyup.enter="sendMsg"
            :disabled="isTyping"
          >
          <button class="btn-send" @click="sendMsg" :disabled="isTyping">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
          </button>
        </div>
      </div>

    </div>
  </transition>
</template>

<script setup>
import { ref, watch, nextTick, computed } from 'vue'
import { useChatStore } from '@/stores/chat'
import { chatWithAIChef } from '@/services/aiService'

const chatStore = useChatStore()
const isMinimized = ref(false)
const inputMsg = ref('')
const msgContainer = ref(null)
const isTyping = ref(false)

const isAiChat = computed(() => chatStore.activeChat?.id === 'gomet-ai')

const messages = ref([])

// Reset messages when active chat changes
watch(() => chatStore.activeChat, (newVal) => {
  if (newVal) {
    isMinimized.value = false
    if (newVal.id === 'gomet-ai') {
      messages.value = [
        { text: '👋 Xin chào! Tôi là Gomet AI 🤖 — trợ lý ẩm thực của bạn. Hỏi tôi bất cứ điều gì về công thức nấu ăn, nguyên liệu hoặc gợi ý món ăn nhé!', isMine: false }
      ]
    } else {
      messages.value = [
        { text: 'Hello!', isMine: false },
        { text: 'Hey, that dish looks amazing!', isMine: true }
      ]
    }
    scrollToBottom()
  }
})

const sendMsg = async () => {
  const text = inputMsg.value.trim()
  if (!text) return

  messages.value.push({ text, isMine: true })
  inputMsg.value = ''
  scrollToBottom()

  if (isAiChat.value) {
    isTyping.value = true
    scrollToBottom()
    try {
      const history = messages.value.slice(1, -1)
      const reply = await chatWithAIChef(history, text)
      messages.value.push({ text: reply, isMine: false })
    } catch (err) {
      const errMsg = err?.message ?? ''
      const msg = errMsg === 'OPENAI_API_KEY_MISSING'
        ? 'Tính năng AI chưa được cấu hình. Vui lòng thêm VITE_OPENAI_API_KEY vào file .env để sử dụng.'
        : (errMsg.includes('429') || errMsg.toLowerCase().includes('rate limit') || errMsg.toLowerCase().includes('too many'))
          ? 'Gomet AI đang bận quá! Vui lòng đợi vài giây rồi thử lại nhé.'
          : 'Xin lỗi, tôi đang gặp sự cố. Vui lòng thử lại sau nhé!'
      messages.value.push({ text: msg, isMine: false })
    } finally {
      isTyping.value = false
      scrollToBottom()
    }
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight
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

/* Typing indicator */
.typing-bubble {
  display: flex; align-items: center; gap: 4px; padding: 10px 16px; min-width: 54px;
}
.dot {
  width: 7px; height: 7px; border-radius: 50%; background: #9CA3AF;
  animation: typing-bounce 1.2s infinite ease-in-out;
}
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes typing-bounce {
  0%, 80%, 100% { transform: translateY(0); opacity: 0.5; }
  40% { transform: translateY(-6px); opacity: 1; }
}
</style>