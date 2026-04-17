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
            <span class="status-text" :class="{'text-emerald-500': partnerIsTyping}">
              {{ partnerIsTyping ? 'Đang soạn tin nhắn...' : (chatStore.activeChat.online ? $t('chat.active_now') : 'Hoạt động gần đây') }}
            </span>
          </div>
        </div>

        <div class="actions">
          <button class="btn-icon call-btn" :class="{ 'is-locked': !isMutualFollow }" @click.stop="handleInitiateCall" :title="isMutualFollow ? 'Gọi video' : 'Theo dõi nhau để gọi video'">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M23 7l-7 5 7 5V7z"></path><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
            <span v-if="!isMutualFollow" class="lock-badge">🔒</span>
          </button>
          <button class="btn-icon" @click.stop="isMinimized = !isMinimized"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line v-if="!isMinimized" x1="5" y1="12" x2="19" y2="12"></line><polyline v-else points="18 15 12 9 6 15"></polyline></svg></button>
          <button class="btn-icon close" @click.stop="closeChat"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg></button>
        </div>
      </div>

      <div v-show="!isMinimized" class="chat-body-wrapper">
        <div class="chat-messages scroll-body" ref="msgContainer">
          <div class="intro">
            <img :src="chatStore.activeChat.avatar || 'https://ui-avatars.com/api/?name=User'" alt="Avatar">
            <h5>{{ chatStore.activeChat.name }}</h5>
            <p v-if="isMutualFollow">{{ $t('chat.friends_on_gomet') }}</p>
            <p v-else style="color: #ea580c; font-size: 0.8rem;">Hãy theo dõi nhau để mở khóa gọi video!</p>
          </div>
          
          <div v-for="(msg, i) in messages" :key="msg.messageID || i" class="msg-item" 
               :class="{ 'mine': msg.isMine, 'is-last': i === messages.length - 1 || messages[i+1]?.isMine !== msg.isMine || isCallSystemMsg(messages[i+1]?.text), 'sys-wrap': isCallSystemMsg(msg.text) }">
            
            <div v-if="isCallSystemMsg(msg.text)" class="sys-call-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path></svg>
              Cuộc gọi video kết thúc ({{ getCallDuration(msg.text) }})
            </div>

            <template v-else>
              <div class="bubble-container">
                <div class="msg-content">
                  
                  <div class="msg-avatar-container" v-if="!msg.isMine">
                    <img v-if="i === messages.length - 1 || messages[i+1]?.isMine !== msg.isMine" :src="chatStore.activeChat.avatar || 'https://ui-avatars.com/api/?name=User'" class="msg-avatar">
                  </div>

                  <div class="bubble-wrapper">
                    <div v-if="msg.parent" class="replied-preview" @click="scrollToBottom">
                      <span class="replied-author">{{ msg.parent.sender?.username || 'Người dùng' }}</span>
                      <span class="replied-text" :class="{'is-italic': msg.parent.content === '[UNSENT]'}">
                        {{ msg.parent.content === '[UNSENT]' ? 'Tin nhắn đã thu hồi' : formatPreviewMessage(msg.parent.content || msg.parent.text) }}
                      </span>
                    </div>

                    <div class="bubble" :class="{ 'has-reply': msg.parent, 'is-image-msg': isImageMsg(msg.text) && msg.text !== '[UNSENT]', 'is-unsent': msg.text === '[UNSENT]' }">
                      
                      <template v-if="msg.text === '[UNSENT]'">
                        <span style="font-style: italic; color: #94a3b8; font-size: 0.9em;">Tin nhắn đã được thu hồi</span>
                      </template>
                      
                      <template v-else-if="isPostShare(msg.text)"><MiniPostCard :postID="getPostId(msg.text)" /></template>
                      <template v-else-if="isImageMsg(msg.text)"><img :src="getImageUrl(msg.text)" class="chat-sent-image" @click.stop="openImagePreview(getImageUrl(msg.text))" /></template>
                      <template v-else>{{ msg.text }}</template>

                      <div v-if="msg.reactions && msg.reactions.length > 0 && msg.text !== '[UNSENT]'" class="reaction-badges">
                        <div v-for="r in getReactionSummary(msg.reactions)" :key="r.emoji" class="r-badge" :class="{'reacted-by-me': r.me}">
                          {{ r.emoji }} <span>{{ r.count > 1 ? r.count : '' }}</span>
                        </div>
                      </div>
                    </div>

                    <div v-if="msg.text !== '[UNSENT]'" class="reaction-picker" :class="{ 'show': activeReactionMsgId === (msg.messageID || i) }">
                      <span v-for="emo in AVAILABLE_EMOJIS" :key="emo" @click="handleSendReaction(msg, emo)">{{ emo }}</span>
                    </div>
                  </div>

                  <div v-if="msg.text !== '[UNSENT]'" class="msg-hover-actions">
                    <button class="btn-action" @click.stop="toggleReactionPicker(msg.messageID || i)" title="Bày tỏ cảm xúc">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><path d="M8 14s1.5 2 4 2 4-2 4-2"></path></svg>
                    </button>
                    <button class="btn-action" @click="setReply(msg)" title="Trả lời">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="9 17 4 12 9 7"></polyline><path d="M20 18v-2a4 4 0 0 0-4-4H4"></path></svg>
                    </button>
                    <button v-if="msg.isMine" class="btn-action btn-unsend" @click="handleUnsend(msg)" title="Thu hồi tin nhắn">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2.5"><path d="M3 6h18"></path><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path></svg>
                    </button>
                  </div>

                </div>
                
                <div class="msg-time" v-if="i === messages.length - 1 || messages[i+1]?.isMine !== msg.isMine">
                   {{ msg.timeStr }}
                   <span v-if="msg.isMine && i === messages.length - 1 && msg.isRead" class="read-status">
                     <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg> Đã xem
                   </span>
                </div>
              </div>
            </template>
          </div>

          <div v-if="partnerIsTyping" class="msg-item not-mine is-last">
            <div class="msg-content">
              <div class="msg-avatar-container"><img :src="chatStore.activeChat.avatar || 'https://ui-avatars.com/api/?name=User'" class="msg-avatar"></div>
              <div class="bubble typing-bubble"><span class="dot"></span><span class="dot"></span><span class="dot"></span></div>
            </div>
          </div>
        </div>

        <transition name="slide-fade">
          <div v-if="replyingTo" class="reply-preview-bar">
            <div class="reply-info">
              <span class="reply-label">Đang trả lời <b>{{ replyingTo.isMine ? 'Chính bạn' : (replyingTo.sender?.username || chatStore.activeChat.name) }}</b></span>
              <span class="reply-text">{{ formatPreviewMessage(replyingTo.text || replyingTo.content) }}</span>
            </div>
            <button class="btn-cancel-reply" @click="cancelReply">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
          </div>
        </transition>

        <div v-if="showEmojiPicker" class="emoji-popup"><EmojiPicker :native="true" @select="onSelectEmoji" /></div>
        
        <div class="chat-footer">
          <div class="input-actions">
            <button class="btn-action-icon" @click="showEmojiPicker = !showEmojiPicker"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><path d="M8 14s1.5 2 4 2 4-2 4-2"></path></svg></button>
            <button class="btn-action-icon" @click="triggerImageUpload"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle></svg></button>
            <input type="file" ref="fileInput" @change="handleImageUpload" style="display: none" accept="image/*" />
          </div>
          <div class="input-wrapper">
            <input :value="inputMsg" :placeholder="isUploading ? 'Đang gửi...' : 'Nhập tin nhắn...'" @keydown.enter.prevent="sendMsg" @input="onInputMsg" @blur="handleStopTyping" ref="chatInput" :disabled="isUploading">
          </div>
          <button class="btn-send" @click.prevent="sendMsg" :disabled="inputMsg.trim().length === 0 || isUploading">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"></path></svg>
          </button>
        </div>
      </div>

      <transition name="fade">
        <div v-if="previewImageUrl" class="image-preview-overlay" @click="closeImagePreview">
          <button class="btn-close-preview" @click="closeImagePreview">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
          <img :src="previewImageUrl" class="image-preview-full" @click.stop />
        </div>
      </transition>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch, nextTick, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth' 
import { toast } from '@/composables/useToast'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import api from '@/services/api'
import { ensureBrowserNotificationPermission, showBrowserNotification } from '@/services/browserNotificationService'
import MiniPostCard from './MiniPostCard.vue'
import EmojiPicker from 'vue3-emoji-picker'
import 'vue3-emoji-picker/css'

const chatStore = useChatStore()
const authStore = useAuthStore()
const router = useRouter()

const isMinimized = ref(false)
const inputMsg = ref('')
const msgContainer = ref(null)
const chatInput = ref(null)
const messages = ref([])
const stompClient = ref(null)

const showEmojiPicker = ref(false)
const fileInput = ref(null)
const isUploading = ref(false)
const previewImageUrl = ref(null) 

const activeReactionMsgId = ref(null);
const toggleReactionPicker = (id) => { activeReactionMsgId.value = activeReactionMsgId.value === id ? null : id; }

const partnerIsTyping = ref(false)
let typingTimeout = null
let partnerTypingTimeout = null
let isThrottlingTyping = false; 

const followStatus = ref({ following: false, followed: false })
const isMutualFollow = computed(() => followStatus.value.following === true && followStatus.value.followed === true)
const currentUserId = computed(() => {
  const id = authStore.user?.accountID || authStore.user?.id;
  return id ? Number(id) : null;
})
const getAuthHeaders = () => ({ 'Authorization': `Bearer ${authStore.user?.token}` })

const replyingTo = ref(null)
const AVAILABLE_EMOJIS = ['❤️', '👍', '😂', '😮', '😢']

const setReply = (msg) => { replyingTo.value = msg; nextTick(() => chatInput.value?.focus()); }
const cancelReply = () => { replyingTo.value = null; }

const formatPreviewMessage = (text) => {
  if (!text) return '';
  if (text === '[UNSENT]') return 'Tin nhắn đã thu hồi';
  if (isImageMsg(text)) return '📸 Hình ảnh';
  if (isCallSystemMsg(text)) return '📞 Cuộc gọi video';
  if (isPostShare(text)) return '📌 Đã chia sẻ bài viết';
  return text.length > 30 ? text.substring(0, 30) + '...' : text;
}

const getReactionSummary = (reactions) => {
  if (!reactions || reactions.length === 0) return [];
  const map = {};
  reactions.forEach(r => {
    if (!map[r.emoji]) map[r.emoji] = { emoji: r.emoji, count: 0, me: false };
    map[r.emoji].count++;
    if (Number(r.user?.accountID) === currentUserId.value) map[r.emoji].me = true;
  });
  return Object.values(map).sort((a, b) => b.count - a.count);
}

const upsertReaction = (targetMsg, emoji, senderId, senderName) => {
  if (!targetMsg.reactions) targetMsg.reactions = [];
  const existingIdx = targetMsg.reactions.findIndex(r => Number(r.user?.accountID) === Number(senderId));
  if (existingIdx >= 0) {
    if (targetMsg.reactions[existingIdx].emoji === emoji) { targetMsg.reactions.splice(existingIdx, 1); } 
    else { targetMsg.reactions[existingIdx].emoji = emoji; }
  } else {
    targetMsg.reactions.push({ emoji: emoji, user: { accountID: senderId, username: senderName } });
  }
}

const handleSendReaction = (msg, emoji) => {
  if (msg.messageID > 1000000000000) {
    toast.warn("Vui lòng chờ tin nhắn gửi xong để thả cảm xúc!");
    activeReactionMsgId.value = null; 
    return;
  }
  if (!stompClient.value?.connected) return;
  const conversationId = chatStore.activeChat.id || chatStore.activeChat.conversationID;
  const payload = { type: 'reaction', messageId: msg.messageID, emoji: emoji, senderId: currentUserId.value, senderName: authStore.user?.username, conversationId: conversationId };
  stompClient.value.send("/app/call.signaling", getAuthHeaders(), JSON.stringify(payload));
  activeReactionMsgId.value = null;
  upsertReaction(msg, emoji, currentUserId.value, authStore.user?.username); 
}

const handleUnsend = (msg) => {
  if (msg.messageID > 1000000000000) {
    toast.warn("Chờ hệ thống xử lý tin nhắn rồi hãy gỡ nhé!");
    return;
  }
  if (!confirm("Bạn có chắc chắn muốn thu hồi tin nhắn này?")) return;
  
  if (stompClient.value?.connected) {
    const conversationId = chatStore.activeChat.id || chatStore.activeChat.conversationID;
    const payload = { type: 'unsend', messageId: msg.messageID, conversationId: conversationId, senderId: currentUserId.value };
    stompClient.value.send("/app/call.signaling", getAuthHeaders(), JSON.stringify(payload));
    
    msg.text = '[UNSENT]';
    msg.content = '[UNSENT]'; 
    msg.reactions = []; 
    messages.value.forEach(childMsg => {
      if (childMsg.parent && childMsg.parent.messageID === msg.messageID) {
        childMsg.parent.content = '[UNSENT]';
      }
    });
  }
}

const onInputMsg = (e) => { inputMsg.value = e.target.value; handleTyping(); }
const handleTyping = () => {
  if (!inputMsg.value.trim()) { handleStopTyping(); return; }
  if (stompClient.value?.connected && !isThrottlingTyping) {
    const convId = chatStore.activeChat.id || chatStore.activeChat.conversationID;
    stompClient.value.send("/app/call.signaling", getAuthHeaders(), JSON.stringify({ type: 'typing', conversationId: convId, senderId: currentUserId.value }));
    isThrottlingTyping = true; setTimeout(() => { isThrottlingTyping = false; }, 2000);
  }
  clearTimeout(typingTimeout); typingTimeout = setTimeout(() => { handleStopTyping(); }, 2500);
}
const handleStopTyping = () => {
  if (stompClient.value?.connected) {
    const convId = chatStore.activeChat.id || chatStore.activeChat.conversationID;
    stompClient.value.send("/app/call.signaling", getAuthHeaders(), JSON.stringify({ type: 'stop_typing', conversationId: convId, senderId: currentUserId.value }));
  }
}

const markAsRead = async (convId) => {
  if (!convId) return;
  try {
    await api.put(`/api/messages/read/${convId}?myId=${currentUserId.value}`, null, { headers: getAuthHeaders() });
    if (stompClient.value?.connected) {
      stompClient.value.send("/app/call.signaling", getAuthHeaders(), JSON.stringify({ type: 'read_receipt', conversationId: convId, senderId: currentUserId.value }));
    }
  } catch (err) {}
}

const handleInitiateCall = () => {
  if (!isMutualFollow.value) { toast.warn("Cần theo dõi nhau để gọi video!"); return; }
  const conversationId = chatStore.activeChat.id || chatStore.activeChat.conversationID;
  const partnerName = chatStore.activeChat.name; 
  if (stompClient.value?.connected) {
    const inviteSignal = { type: 'invite', conversationId, senderId: currentUserId.value, senderName: authStore.user.username, senderAvatar: authStore.user.avatar };
    stompClient.value.send("/app/call.signaling", getAuthHeaders(), JSON.stringify(inviteSignal));
    closeChat(); 
    const routeData = router.resolve({ path: `/call/${conversationId}`, query: { role: 'caller', partnerName } });
    window.open(routeData.href, 'GoMetVideoCall', 'width=1100,height=750,menubar=no,toolbar=no,location=no,status=no,resizable=yes');
  } else { toast.error("Mất kết nối server!"); }
}

const onSelectEmoji = (emoji) => { inputMsg.value += emoji.i; showEmojiPicker.value = false; chatInput.value.focus(); handleTyping(); }
const triggerImageUpload = () => { fileInput.value.click(); }
const handleImageUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  if (file.size > 5 * 1024 * 1024) { toast.error("Ảnh quá lớn! Vui lòng chọn ảnh dưới 5MB."); return; }
  isUploading.value = true;
  const formData = new FormData(); formData.append("file", file);
  try {
    const res = await api.post('/api/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
    const imageUrl = res.data.url; const imageMessage = `[IMAGE:${imageUrl}]`;
    const conversationId = chatStore.activeChat.id || chatStore.activeChat.conversationID;
    messages.value.push({ messageID: Date.now(), text: imageMessage, isMine: true, timeStr: formatTime(new Date()), isRead: false, reactions: [] });
    scrollToBottom();
    if (stompClient.value?.connected) {
      stompClient.value.send("/app/chat.sendMessage", getAuthHeaders(), JSON.stringify({ content: imageMessage, conversation: { conversationID: conversationId }, sender: { accountID: currentUserId.value } }));
    }
  } catch (error) { toast.error("Lỗi tải ảnh lên!"); } finally { isUploading.value = false; fileInput.value.value = ""; }
}

const isImageMsg = (text) => text && text.startsWith('[IMAGE:') && text.endsWith(']');
const getImageUrl = (text) => text.match(/\[IMAGE:(.+)\]/) ? text.match(/\[IMAGE:(.+)\]/)[1] : '';
const openImagePreview = (url) => { previewImageUrl.value = url; }
const closeImagePreview = () => { previewImageUrl.value = null; }

const connectWebSocket = (conversationId) => {
  if (stompClient.value) stompClient.value.disconnect()
  const socket = new SockJS('http://localhost:8080/ws-chat')
  stompClient.value = Stomp.over(socket)
  stompClient.value.debug = null 
  stompClient.value.connect(getAuthHeaders(), () => {
    processSpecialQueue();
    stompClient.value.subscribe(`/topic/${conversationId}`, (payload) => {
      const receivedMsg = JSON.parse(payload.body)
      
      if (receivedMsg.type === 'unsend') {
        const targetMsg = messages.value.find(m => m.messageID === receivedMsg.messageId);
        if (targetMsg) {
          targetMsg.text = '[UNSENT]';
          targetMsg.content = '[UNSENT]';
          targetMsg.reactions = []; 
        }
        messages.value.forEach(childMsg => {
          if (childMsg.parent && childMsg.parent.messageID === receivedMsg.messageId) {
            childMsg.parent.content = '[UNSENT]';
          }
        });
        return;
      }

      if (receivedMsg.type === 'reaction') {
        if (Number(receivedMsg.senderId) === currentUserId.value) return; 
        const targetMsg = messages.value.find(m => m.messageID === receivedMsg.messageId);
        if (targetMsg) { upsertReaction(targetMsg, receivedMsg.emoji, receivedMsg.senderId, receivedMsg.senderName); }
        return;
      }

      if (receivedMsg.type === 'read_receipt' && Number(receivedMsg.senderId) !== currentUserId.value) {
        messages.value.forEach(m => { if (m.isMine) m.isRead = true }); return;
      }

      if (receivedMsg.type === 'typing' && Number(receivedMsg.senderId) !== currentUserId.value) {
        partnerIsTyping.value = true; scrollToBottom();
        clearTimeout(partnerTypingTimeout); partnerTypingTimeout = setTimeout(() => { partnerIsTyping.value = false; }, 3000); return;
      }

      if (receivedMsg.type === 'stop_typing' && Number(receivedMsg.senderId) !== currentUserId.value) {
        partnerIsTyping.value = false; clearTimeout(partnerTypingTimeout); return;
      }

      if (!receivedMsg.type) { 
        const senderId = Number(receivedMsg.sender?.accountID || receivedMsg.senderID)
        if (senderId === currentUserId.value) {
          const tempMsg = messages.value.find(m => m.isMine && m.messageID > 1000000000000 && m.text === receivedMsg.content);
          if (tempMsg) { tempMsg.messageID = receivedMsg.messageID; }
        } else {
          messages.value.push(mapMessage(receivedMsg));
          partnerIsTyping.value = false; 
          playChatTingSound();
          showIncomingMessageNotificationUnified(receivedMsg);
          scrollToBottom();
          if (!isMinimized.value) markAsRead(conversationId);
        }
      }
    })
  })
}

const showIncomingMessageNotificationUnified = (message) => {
  const conversationId = Number(message?.conversation?.conversationID || chatStore.activeChat?.id || chatStore.activeChat?.conversationID)
  const messageId = Number(message?.messageID)
  const senderName = message?.sender?.username || chatStore.activeChat?.name || 'Tin nhắn mới'
  const senderAvatar = message?.sender?.avatar || chatStore.activeChat?.avatar || 'https://ui-avatars.com/api/?name=User&background=EA580C&color=fff'
  const messageContent = message?.content || 'Bạn có tin nhắn mới'

  showBrowserNotification({
    title: senderName,
    body: messageContent,
    icon: senderAvatar,
    tag: messageId ? `chat-${messageId}` : `chat-${conversationId}`,
    onClick: () => {
      if (!conversationId) return
      chatStore.openChat({ id: conversationId, conversationID: conversationId, name: senderName, avatar: senderAvatar, online: true })
      isMinimized.value = false
    }
  })
}

const playChatTingSound = () => {
  try {
    const audio = new Audio('/sounds/ting.mp3')
    audio.volume = 0.6
    audio.play().catch(() => {})
  } catch (error) {}
}

const fetchHistory = async (convId) => {
  if (!convId || convId === 'undefined') return;
  try {
    const res = await api.get(`/api/messages/${convId}`);
    const rawData = res.data?.data || res.data;
    if (Array.isArray(rawData)) {
      messages.value = rawData.map(msg => mapMessage(msg));
      scrollToBottom();
      if (!isMinimized.value) markAsRead(convId);
    }
  } catch (err) {}
}

watch(() => chatStore.activeChat, async (newVal, oldVal) => {
  if (newVal) {
    isMinimized.value = false; showEmojiPicker.value = false; partnerIsTyping.value = false; 
    const myId = currentUserId.value; const partnerId = Number(newVal.partnerID); 
    const convId = newVal.id || newVal.conversationID; const oldConvId = oldVal?.id || oldVal?.conversationID;
    followStatus.value = { following: newVal.following === true || newVal.isFollowing === true, followed: newVal.followed === true || newVal.isFollowed === true };
    if (partnerId && myId) {
      try {
        const res = await api.get(`/api/follows/check-mutual/${partnerId}`, { params: { myId } });
        followStatus.value = { following: res.data.following === true, followed: res.data.followed === true };
      } catch (err) {}
    }
    if (convId !== oldConvId) { messages.value = []; if (convId) { connectWebSocket(convId); fetchHistory(convId); } }
    nextTick(() => { if(chatInput.value) chatInput.value.focus() });
  }
}, { immediate: true })

watch(isMinimized, (minimized) => { if (!minimized && chatStore.activeChat) { const convId = chatStore.activeChat.id || chatStore.activeChat.conversationID; markAsRead(convId); } })

const formatTime = (dateInput) => {
  if (!dateInput) return 'vừa xong'
  const date = new Date(dateInput)
  return isNaN(date.getTime()) ? 'vừa xong' : date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
}

const mapMessage = (msg) => {
  let parentSenderName = 'Người dùng';
  let parentContent = msg.parent ? (msg.parent.content || msg.parent.text) : '';
  if (msg.parent) {
    if (msg.parent.sender) {
      parentSenderName = Number(msg.parent.sender.accountID) === currentUserId.value ? 'Bạn' : (msg.parent.sender.username || 'Người dùng');
    } else if (msg.parent.messageID) {
      const localParentMsg = messages.value.find(m => m.messageID === msg.parent.messageID);
      if (localParentMsg) {
        parentContent = localParentMsg.content;
        parentSenderName = localParentMsg.isMine ? 'Bạn' : (localParentMsg.sender?.username || chatStore.activeChat.name);
      }
    }
  }
  return {
    messageID: msg.messageID || msg.id, 
    text: msg.content || msg.text, 
    content: msg.content || msg.text, 
    isMine: Number(msg.sender?.accountID || msg.senderID) === currentUserId.value,
    timeStr: formatTime(msg.createdAt),
    isRead: msg.isRead === true || msg.isRead === 1,
    sender: msg.sender,
    parent: msg.parent ? { 
      messageID: msg.parent.messageID, 
      content: parentContent, 
      sender: { username: parentSenderName } 
    } : null,
    reactions: msg.reactions || [] 
  };
}

const isCallSystemMsg = (text) => text && text.startsWith('[SYS_CALL:') && text.endsWith(']');
const getCallDuration = (text) => text.match(/\[SYS_CALL:(.+)\]/) ? text.match(/\[SYS_CALL:(.+)\]/)[1] : '00:00';
const isPostShare = (text) => text && text.startsWith('[POST_SHARE_ID:') && text.endsWith(']');
const getPostId = (text) => text.match(/\[POST_SHARE_ID:(\d+)\]/) ? text.match(/\[POST_SHARE_ID:(\d+)\]/)[1] : null;

const scrollToBottom = async () => {
  await nextTick();
  if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight;
};

const sendMsg = () => {
  const text = inputMsg.value.trim()
  if (text.length === 0 || !chatStore.activeChat) return
  const conversationId = chatStore.activeChat.id || chatStore.activeChat.conversationID;
  const msgPayload = { content: text, conversation: { conversationID: conversationId }, sender: { accountID: currentUserId.value } };
  const locallyOptimisticMsg = { messageID: Date.now(), text, content: text, isMine: true, timeStr: formatTime(new Date()), isRead: false, reactions: [], sender: { username: authStore.user?.username } };

  if (replyingTo.value) {
    msgPayload.parent = { messageID: replyingTo.value.messageID };
    locallyOptimisticMsg.parent = replyingTo.value;
  }

  messages.value.push(locallyOptimisticMsg);
  inputMsg.value = ''; showEmojiPicker.value = false; cancelReply(); handleStopTyping(); scrollToBottom()
  playChatTingSound(); // Play sound on send
  if (stompClient.value?.connected) { stompClient.value.send("/app/chat.sendMessage", getAuthHeaders(), JSON.stringify(msgPayload)); }
  nextTick(() => { if (chatInput.value) chatInput.value.focus(); });
}

const closeChat = () => { handleStopTyping(); if (stompClient.value) stompClient.value.disconnect(); chatStore.closeChat(); showEmojiPicker.value = false; previewImageUrl.value = null; replyingTo.value = null; }

const processSpecialQueue = () => {
  if (!chatStore.specialMessageQueue || chatStore.specialMessageQueue.length === 0) return;
  const activeConvId = chatStore.activeChat?.id || chatStore.activeChat?.conversationID;
  chatStore.specialMessageQueue.forEach(msg => {
    if (msg.conversation.conversationID === activeConvId && stompClient.value?.connected) {
      messages.value.push({ messageID: Date.now(), text: msg.content, content: msg.content, isMine: true, timeStr: formatTime(new Date()), isRead: false, reactions: [] });
      scrollToBottom();
      stompClient.value.send("/app/chat.sendMessage", getAuthHeaders(), JSON.stringify(msg));
      chatStore.clearSpecialMessage(msg.timestamp);
    }
  });
};

watch(() => chatStore.specialMessageQueue, () => { processSpecialQueue(); }, { deep: true });

onMounted(() => { ensureBrowserNotificationPermission(); });
onUnmounted(() => { handleStopTyping(); if (stompClient.value) stompClient.value.disconnect(); clearTimeout(typingTimeout); clearTimeout(partnerTypingTimeout); })
</script>

<style scoped lang="scss">
@import './MiniChatBox.scss';

.btn-unsend {
  background: white; border: 1px solid #fee2e2; width: 30px; height: 30px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; cursor: pointer; box-shadow: 0 4px 15px rgba(0,0,0,0.08); transition: all 0.2s ease;
  &:hover { background: #fef2f2; border-color: #ef4444; transform: scale(1.1); }
}

.bubble.is-unsent {
  background: transparent !important;
  border: 1px solid #e2e8f0 !important;
  color: #94a3b8 !important;
  box-shadow: none !important;
}
.mine .bubble.is-unsent {
  border-color: #ffedd5 !important;
  color: #ffedd5 !important;
}
</style>
