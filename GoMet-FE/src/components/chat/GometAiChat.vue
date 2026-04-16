<template>
  <div class="gomet-ai-wrapper">
    <transition name="slide-up">
      <div v-if="isOpen" class="mini-chat-box ai-chat-box" :class="{ 'is-minimized': isMinimized }">
        
        <div class="chat-head ai-head" @click="isMinimized = !isMinimized">
          <div class="user-info">
            <div class="avt-wrap">
              <div class="ai-avatar-ring">
                <img src="https://api.dicebear.com/7.x/shapes/svg?seed=GometChef&backgroundColor=ea580c&shape1Color=ffffff&shape2Color=ffffff&shape3Color=ffffff" alt="AI Avatar">
              </div>
              <span class="dot-status active"></span>
            </div>
            <div class="name-col">
              <h4>Gomet AI <span class="sparkle-icon">✨</span></h4>
              <span class="status-text text-orange">Siêu đầu bếp ảo</span>
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
            <div class="intro ai-intro">
              <div class="ai-avatar-large">
                <img src="https://api.dicebear.com/7.x/shapes/svg?seed=GometChef&backgroundColor=ea580c&shape1Color=ffffff&shape2Color=ffffff&shape3Color=ffffff" alt="Avatar">
              </div>
              <h5>Xin chào, tôi là Gomet AI!</h5>
              <p>Trợ lý bếp núc cá nhân của bạn. Cần gợi ý món ăn hay công thức gì, cứ hỏi tôi nhé!</p>
            </div>
            
            <div 
              v-for="(msg, i) in messages" 
              :key="i" 
              class="msg-item" 
              :class="{ 'mine': msg.isMine, 'is-last': i === messages.length - 1 || messages[i+1]?.isMine !== msg.isMine }"
            >
              <div class="bubble-container">
                <div class="msg-content">
                  <div class="msg-avatar-container" v-if="!msg.isMine">
                    <img v-if="i === messages.length - 1 || messages[i+1]?.isMine" src="https://api.dicebear.com/7.x/shapes/svg?seed=GometChef&backgroundColor=ea580c&shape1Color=ffffff&shape2Color=ffffff&shape3Color=ffffff" class="msg-avatar">
                  </div>
                  
                  <div class="bubble-wrapper">
                    <div class="bubble" :class="{ 'ai-bubble': !msg.isMine }">
                      <span v-html="formatMessageText(parseMsg(msg.text).cleanText)"></span>

                      <div v-if="parseMsg(msg.text).linkId" 
                           @click="goToRecipe(parseMsg(msg.text).linkId)"
                           class="recipe-modern-card">
                        <div class="card-tag">✨ Gợi ý từ Gomet</div>
                        <div class="card-content">
                          <div class="icon-box">👨‍🍳</div>
                          <div class="text-box">
                            <p class="title">Xem công thức chi tiết</p>
                            <p class="subtitle">Bấm để khám phá ngay <span class="arrow">→</span></p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="msg-time" v-if="i === messages.length - 1 || messages[i+1]?.isMine !== msg.isMine">
                  {{ msg.timeStr }}
                </div>
              </div>
            </div>

            <div v-if="isTyping" class="msg-item not-mine is-last">
              <div class="msg-content">
                <div class="msg-avatar-container">
                  <img src="https://api.dicebear.com/7.x/shapes/svg?seed=GometChef&backgroundColor=ea580c&shape1Color=ffffff&shape2Color=ffffff&shape3Color=ffffff" class="msg-avatar">
                </div>
                <div class="bubble ai-bubble typing-bubble">
                  <span class="dot"></span><span class="dot"></span><span class="dot"></span>
                </div>
              </div>
            </div>
          </div>

          <div class="chat-footer ai-footer">
            <div class="input-wrapper">
              <input v-model="inputMsg" placeholder="Hỏi tôi cách nấu món..." @keyup.enter="sendMsg" :disabled="isTyping" ref="chatInput">
            </div>
<button class="btn-send ai-send-btn" @click="sendMsg" :disabled="isTyping || inputMsg.trim().length === 0" title="Gửi">
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import { chatWithAIChef } from '@/services/aiService'

const router = useRouter();
const isOpen = ref(false);
const isMinimized = ref(false);
const inputMsg = ref('');
const msgContainer = ref(null);
const chatInput = ref(null);
const isTyping = ref(false);

const formatTime = () => new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
const messages = ref([{ text: '👋 Chào sếp! Sếp cần tìm món gì hôm nay?', isMine: false, timeStr: formatTime() }])

const parseMsg = (content) => {
  const regex = /\[LINK:(\d+)\]/g;
  const matches = [...content.matchAll(regex)];
  return {
    cleanText: content.replace(regex, '').trim(),
    linkId: matches.length > 0 ? matches[0][1] : null
  };
};

// 🚀 NÂNG CẤP: Dịch Markdown siêu chuẩn cho AI
const formatMessageText = (text) => {
  if (!text) return '';
  let formatted = text;
  
  // 1. In đậm (**text**)
  formatted = formatted.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
  
  // 2. Xuống dòng (\n thành <br>)
  formatted = formatted.replace(/\n/g, '<br/>');
  
  // 3. Gạch đầu dòng (- Món 1)
  formatted = formatted.replace(/^- (.*?)(?=<br\/>|$)/gm, '<span class="ai-list-item">🔸 $1</span>');
  formatted = formatted.replace(/^\* (.*?)(?=<br\/>|$)/gm, '<span class="ai-list-item">🔸 $1</span>');

  return formatted;
}

const goToRecipe = (id) => { router.push(`/post/${id}`); isOpen.value = false; };
const openChat = async () => { isOpen.value = true; isMinimized.value = false; await nextTick(); if(chatInput.value) chatInput.value.focus(); scrollToBottom(); }
const closeChat = () => { isOpen.value = false }
const scrollToBottom = async () => { await nextTick(); if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight }

const sendMsg = async () => {
  const text = inputMsg.value.trim()
  // 🚀 Đổi !text thành text.length === 0
  if (text.length === 0 || isTyping.value) return

  messages.value.push({ text, isMine: true, timeStr: formatTime() })
  // ... (giữ nguyên phần dưới)
  inputMsg.value = ''; scrollToBottom(); isTyping.value = true

  try {
    const coreKeyword = text.toLowerCase().replace(/(vậy|món|cho|mình|hỏi|về|cách|nấu|định|làm|tìm|kiếm|trên|web|có|gì)/g, '').trim();
    const dbRes = await api.get(`/api/posts/search-mini?q=${coreKeyword || text}`);
    
    if (dbRes.data && dbRes.data.length > 0) {
      messages.value.push({ 
        text: `Tôi có tìm thấy công thức này cực kỳ hợp lý cho sếp nhé! [LINK:${dbRes.data[0].id}]`, 
        isMine: false, 
        timeStr: formatTime() 
      });
      isTyping.value = false; scrollToBottom();
      return; 
    }

    const fallbackRes = await api.get(`/api/posts/latest?limit=5`);
    const dbData = fallbackRes.data.map(p => ({ id: p.postID, title: p.title }));
    
    const reply = await chatWithAIChef(messages.value.slice(-6), text, dbData);
    messages.value.push({ text: reply, isMine: false, timeStr: formatTime() });
  } catch (err) {
    messages.value.push({ text: '⚠️ Hệ thống đang quá tải, sếp pha tách trà chờ tí rồi hỏi lại nhé!', isMine: false, timeStr: formatTime() });
  } finally { isTyping.value = false; scrollToBottom(); }
}
defineExpose({ isOpen, openChat })
</script>

<style scoped lang="scss">
@import './MiniChatBox.scss';

.gomet-ai-wrapper { 
  position: fixed; 
  bottom: 85px; 
  right: 24px; 
  z-index: 9999; 
}

/* ==========================================
   GIAO DIỆN ĐẶC TRƯNG CỦA AI (GHI ĐÈ MINICHATBOX)
   ========================================== */

.ai-chat-box {
  border: 1px solid rgba(234, 88, 12, 0.2);
  box-shadow: 0 12px 40px rgba(234, 88, 12, 0.15); 
}

/* Header AI */
.ai-head {
  background: linear-gradient(135deg, #ffffff 0%, #fff7ed 100%); 
  border-bottom: 1px solid #e2e8f0;

  .text-orange {
    color: #ea580c !important; 
    font-weight: 600;
  }

  .sparkle-icon {
    font-size: 0.9rem;
    animation: sparkle 2s infinite ease-in-out;
  }
}

/* 🚀 FIX LỖI AVATAR BỊ NHÂN 3: Cấp đủ size và display */
.ai-avatar-ring {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  padding: 2px;
  background: linear-gradient(45deg, #ea580c, #ff9d00); 
  box-shadow: 0 0 15px rgba(234, 88, 12, 0.4), inset 0 0 8px rgba(255, 157, 0, 0.5); /* Glow rực rỡ */
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden; /* Cắt phần lồi ra */

  img {
    width: 100% !important;
    height: 100% !important;
    border-radius: 50%;
    border: 2px solid #ffffff !important;
    background: #ffffff; 
    object-fit: cover;
  }
}

/* Intro AI */
.ai-intro {
  padding: 30px 0 20px !important;
  
  .ai-avatar-large {
    width: 86px;
    height: 86px;
    border-radius: 50%;
    padding: 3px;
    background: linear-gradient(45deg, #ea580c, #ff9d00);
    box-shadow: 0 0 25px rgba(234, 88, 12, 0.3); /* Glow to cho Avatar intro */
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    
    img {
      width: 100% !important;
      height: 100% !important;
      border-radius: 50%;
      border: 3px solid #ffffff !important;
      background: #ffffff;
      object-fit: cover;
    }
  }

  h5 { color: #0f172a !important; font-size: 1.25rem !important; }
}

/* Bóng Chat của AI */
.ai-bubble {
  background: #ffffff !important; /* Trắng muốt */
  color: #0f172a !important;
  border-bottom-left-radius: 4px !important;
  line-height: 1.6 !important;
  border: 1px solid rgba(234, 88, 12, 0.15) !important; 
  box-shadow: 0 4px 15px rgba(234, 88, 12, 0.08) !important; /* Bóng cam cực nhạt */

  :deep(strong) {
    color: #ea580c; 
    font-weight: 700;
  }
  
  :deep(.ai-list-item) {
    display: block;
    margin-left: 4px;
    margin-top: 4px;
  }
}

/* Bóng Chat của Sếp (Mình nhắn) - Giữ màu Cam Gomet */
.msg-item.mine .bubble { 
  background: linear-gradient(135deg, #ea580c, #f97316) !important; /* Cam Gradient xịn xò */
  color: #ffffff !important; 
  border-bottom-right-radius: 4px !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(234, 88, 12, 0.2) !important;
}

/* Footer AI */
.ai-footer {
  .input-wrapper {
    margin-left: 0;
    
    &:focus-within {
      border-color: #ea580c !important; /* Cam khi focus */
      box-shadow: 0 0 0 3px rgba(234, 88, 12, 0.1) !important;
    }
  }

  .ai-send-btn {
    color: #ea580c !important;
    
    &:hover:not(:disabled) { 
      background: #fff7ed !important; 
      color: #c2410c !important;
      transform: translateY(-2px) rotate(15deg) !important; 
    }
  }
}

/* ==========================================
   🌟 CARD GỢI Ý MÓN ĂN (AI RECIPE CARD)
   ========================================== */
.recipe-modern-card {
  margin-top: 14px;
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 20px -8px rgba(234, 88, 12, 0.3);
    border-color: #fb923c;

    .arrow {
      transform: translateX(4px);
      color: #ea580c;
    }
  }

  .card-tag {
    background: linear-gradient(90deg, #ea580c, #fb923c);
    color: #ffffff;
    font-size: 0.65rem;
    font-weight: 700;
    padding: 6px 12px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  .card-content {
    padding: 12px;
    display: flex;
    align-items: center;
    gap: 14px;

    .icon-box { 
      font-size: 24px; 
      background: #fff7ed; 
      border-radius: 10px; 
      width: 44px; 
      height: 44px; 
      display: flex; 
      align-items: center; 
      justify-content: center; 
    }

    .title { 
      font-weight: 700; 
      font-size: 0.9rem; 
      color: #0f172a; 
      margin: 0; 
    }

    .subtitle { 
      font-size: 0.75rem; 
      color: #64748b; 
      margin: 4px 0 0 0; 
      font-weight: 500;
      display: flex;
      align-items: center;
    }

    .arrow {
      display: inline-block;
      margin-left: 4px;
      transition: transform 0.3s ease;
      font-weight: bold;
    }
  }
}

/* ==========================================
   ANIMATIONS DÀNH RIÊNG CHO AI
   ========================================== */
@keyframes sparkle {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}

.typing-bubble {
  display: flex !important;
  gap: 6px !important;
  padding: 16px 20px !important;
  align-items: center !important;
  min-height: 44px;
}

.dot {
  width: 6px !important;
  height: 6px !important;
  background-color: #ea580c !important; /* Chấm màu cam AI */
  border-radius: 50% !important;
  animation: typingBounce 1.4s infinite ease-in-out both;
  
  &:nth-child(1) { animation-delay: -0.32s; }
  &:nth-child(2) { animation-delay: -0.16s; }
}

@keyframes typingBounce {
  0%, 80%, 100% { transform: scale(0); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}
</style>