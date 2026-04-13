<template>
  <div class="gomet-ai-wrapper">
    <transition name="slide-up">
      <div v-if="isOpen" class="mini-chat-box" :class="{ 'is-minimized': isMinimized }">
        
        <div class="chat-head" @click="isMinimized = !isMinimized">
          <div class="user-info">
            <div class="avt-wrap">
              <img src="https://api.dicebear.com/7.x/bottts/svg?seed=Gomet&backgroundColor=059669" :alt="$t('common.ai_avatar')">
              <span class="dot-status active"></span>
            </div>
            <div class="name-col">
              <h4>{{ $t('chat.ai_name') }} 🤖</h4>
              <span class="status-text">{{ $t('chat.ai_status') }}</span>
            </div>
          </div>
          <div class="actions">
            <button class="btn-icon" @click.stop="isMinimized = !isMinimized" :title="isMinimized ? $t('common.expand') : $t('common.collapse')">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line v-if="!isMinimized" x1="5" y1="12" x2="19" y2="12"></line>
                <polyline v-else points="18 15 12 9 6 15"></polyline>
              </svg>
            </button>
            <button class="btn-icon close" @click.stop="closeChat" :title="$t('common.close')">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
          </div>
        </div>

        <div v-show="!isMinimized" class="chat-body-wrapper">
          <div class="chat-messages scroll-body" ref="msgContainer">
            <div class="intro">
              <img src="https://api.dicebear.com/7.x/bottts/svg?seed=Gomet&backgroundColor=059669" :alt="$t('common.ai_avatar')">
              <h5>{{ $t('chat.ai_name') }}</h5>
              <p>{{ $t('chat.ai_intro') }}</p>
            </div>
            
            <div v-for="(msg, i) in messages" :key="i" class="msg-item" :class="{ 'mine': msg.isMine, 'is-last': i === messages.length - 1 || messages[i+1]?.isMine !== msg.isMine }">
              <div class="msg-content">
                <div class="msg-avatar-container" v-if="!msg.isMine">
                  <img v-if="i === messages.length - 1 || messages[i+1]?.isMine" src="https://api.dicebear.com/7.x/bottts/svg?seed=Gomet&backgroundColor=059669" class="msg-avatar">
                </div>
                
                <div class="bubble-wrapper">
                  <div class="bubble shadow-sm" style="white-space: pre-wrap;">
                    {{ parseMsg(msg.text).cleanText }}

                    <div v-if="parseMsg(msg.text).linkId" 
                         @click="goToRecipe(parseMsg(msg.text).linkId)"
                         class="recipe-modern-card shadow-md">
                      <div class="card-tag">{{ $t('chat.ai_card_tag') }}</div>
                      <div class="card-content">
                        <div class="icon-box">🥘</div>
                        <div class="text-box">
                          <p class="title">{{ $t('chat.ai_view_recipe') }}</p>
                          <p class="subtitle">{{ $t('chat.ai_card_subtitle') }} →</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="msg-time" v-if="i === messages.length - 1 || messages[i+1]?.isMine !== msg.isMine">{{ msg.timeStr }}</div>
            </div>

            <div v-if="isTyping" class="msg-item not-mine is-last">
              <div class="msg-content"><div class="bubble typing-bubble"><span class="dot"></span><span class="dot"></span><span class="dot"></span></div></div>
            </div>
          </div>

          <div class="chat-footer">
            <div class="input-wrapper" style="margin-left: 10px;">
              <input v-model="inputMsg" :placeholder="$t('chat.ai_placeholder')" @keyup.enter="sendMsg" :disabled="isTyping" ref="chatInput">
            </div>
            <button class="btn-send" @click="sendMsg" :disabled="isTyping || !inputMsg.trim()" :title="$t('common.send')">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"></path></svg>
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
import { useI18n } from 'vue-i18n'
import axios from 'axios'
import { chatWithAIChef } from '@/services/aiService'
import { formatLocaleTime } from '@/i18n'

const router = useRouter();
const { t } = useI18n()
const isOpen = ref(false);
const isMinimized = ref(false);
const inputMsg = ref('');
const msgContainer = ref(null);
const chatInput = ref(null);
const isTyping = ref(false);

const formatTime = () => formatLocaleTime(new Date())
const messages = ref([{ text: `👋 ${t('chat.ai_greeting')}`, isMine: false, timeStr: formatTime() }])
const keywordStopWords = new Set([
  'vậy', 'món', 'cho', 'mình', 'hỏi', 'về', 'cách', 'nấu', 'định', 'làm', 'tìm', 'kiếm', 'trên', 'web', 'có', 'gì',
  'what', 'how', 'cook', 'make', 'find', 'search', 'dish', 'recipe', 'recipes', 'for', 'me', 'about', 'on', 'the', 'a', 'an', 'to'
])

// ✅ Hàm parseMsg chuẩn: Xóa sạch mã link để text hiển thị đẹp
const parseMsg = (content) => {
  const regex = /\[LINK:(\d+)\]/g;
  const matches = [...content.matchAll(regex)];
  return {
    cleanText: content.replace(regex, '').trim(),
    linkId: matches.length > 0 ? matches[0][1] : null
  };
};

const goToRecipe = (id) => { router.push(`/post/${id}`); isOpen.value = false; };
const openChat = async () => { isOpen.value = true; isMinimized.value = false; await nextTick(); if(chatInput.value) chatInput.value.focus(); scrollToBottom(); }
const closeChat = () => { isOpen.value = false }
const scrollToBottom = async () => { await nextTick(); if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight }

const sendMsg = async () => {
  const text = inputMsg.value.trim()
  if (!text || isTyping.value) return

  messages.value.push({ text, isMine: true, timeStr: formatTime() })
  inputMsg.value = ''; scrollToBottom(); isTyping.value = true

  try {
    const normalizedKeyword = text
      .toLowerCase()
      .replace(/[^\p{L}\p{N}\s]/gu, ' ')
      .split(/\s+/)
      .filter(token => token && !keywordStopWords.has(token))
      .join(' ')
      .trim()
    const coreKeyword = normalizedKeyword || text

    const dbRes = await axios.get(`http://localhost:8080/api/posts/search-mini?q=${coreKeyword}`);
    
    if (dbRes.data && dbRes.data.length > 0) {
      messages.value.push({ 
        text: t('chat.ai_found_recipe', { id: dbRes.data[0].id }), 
        isMine: false, 
        timeStr: formatTime() 
      });
      isTyping.value = false; scrollToBottom();
      return;
    }

    const fallbackRes = await axios.get(`http://localhost:8080/api/posts/latest?limit=5`);
    const dbData = fallbackRes.data.map(p => ({ id: p.postID, title: p.title }));
    
    const reply = await chatWithAIChef(messages.value.slice(-6), text, dbData);
    messages.value.push({ text: reply, isMine: false, timeStr: formatTime() });
  } catch (err) {
    messages.value.push({ text: `⚠️ ${t('chat.ai_busy')}`, isMine: false, timeStr: formatTime() });
  } finally { isTyping.value = false; scrollToBottom(); }
}
defineExpose({ isOpen, openChat })
</script>

<style scoped lang="scss">
@import './MiniChatBox.scss';
.gomet-ai-wrapper { position: fixed; bottom: 85px; right: 24px; z-index: 9999; }

/* 🌟 CSS LABEL/CARD MÓN ĂN SIÊU ĐẸP */
.recipe-modern-card {
  margin-top: 12px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 15px -3px rgba(251, 146, 60, 0.2);
    border-color: #fb923c;
  }

  .card-tag {
    background: #fff7ed;
    color: #ea580c;
    font-size: 10px;
    font-weight: bold;
    padding: 4px 10px;
    text-transform: uppercase;
    border-bottom: 1px solid #fed7aa;
  }

  .card-content {
    padding: 10px;
    display: flex;
    align-items: center;
    gap: 12px;

    .icon-box { font-size: 22px; background: #fff7ed; border-radius: 8px; width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; }
    .title { font-weight: bold; font-size: 13px; color: #1e293b; margin: 0; }
    .subtitle { font-size: 11px; color: #64748b; margin: 2px 0 0 0; }
  }
}

.msg-item.mine .bubble { background-color: #f97316; color: white; }
</style>