<template>
  <transition name="slide-right">
    <div class="chat-sidebar" v-if="chatStore.isMessengerOpen" ref="sidebarRef">
      
      <div class="sidebar-header">
        <div class="header-title">
          <h3>{{ $t('header.messages', 'Tin nhắn') }}</h3>
          <span class="unread-count" v-if="unreadTotal > 0">{{ unreadTotal }}</span>
        </div>
        <div class="header-actions">
          <button class="action-btn close-btn" @click="closeSidebar">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
      </div>

      <div class="search-box">
        <div class="input-wrap">
          <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
          <input 
            type="text" 
            :placeholder="$t('chat.search_placeholder', 'Tìm kiếm trên Gomet...')" 
            v-model="searchQuery"
          >
          <button v-if="searchQuery" class="clear-search" @click="searchQuery = ''">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
      </div>
      
      <div class="sidebar-body scroll-body">
        <div v-if="loading" class="skeleton-list">
          <div v-for="i in 6" :key="i" class="skeleton-item">
            <div class="sk-avatar"></div>
            <div class="sk-info">
              <div class="sk-name"></div>
              <div class="sk-msg"></div>
            </div>
          </div>
        </div>
        
        <div v-else-if="filteredConversations.length === 0" class="empty-state">
          <div class="icon-wrap">🔍</div>
          <p v-if="searchQuery">Không tìm thấy "{{ searchQuery }}"</p>
          <p v-else>{{ $t('chat.no_conversations', 'Bạn chưa có tin nhắn nào') }}</p>
        </div>

        <div v-else class="chat-list">
          <div 
            v-for="c in filteredConversations" 
            :key="c.id" 
            class="chat-item" 
            :class="{ 'unread-item': !c.read, 'active': chatStore.activeChat?.id === c.id }"
            @click="selectChat(c)"
          >
            <div class="avatar-wrap">
              <img :src="c.avatar || 'https://ui-avatars.com/api/?name=User&background=random'" alt="Avatar">
              <div v-if="c.online" class="online-status"></div>
            </div>

            <div class="item-info">
              <div class="top-line">
                <span class="name">{{ c.name }}</span>
                <span class="time">{{ c.time }}</span>
              </div>
              <div class="preview-row">
                <p class="preview" :class="{ unread: !c.read }">{{ c.lastMessage }}</p>
                <div v-if="!c.read" class="unread-dot"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      </div>
  </transition>
</template>

<script setup>
import { ref, onMounted, computed, watch, onUnmounted } from 'vue'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

const chatStore = useChatStore()
const authStore = useAuthStore()

const conversations = ref([])
const loading = ref(false)
const searchQuery = ref('')
const sidebarRef = ref(null)

let stompClient = null;
let activeSubscriptions = []; 

const currentUserId = computed(() => Number(authStore.user?.accountID || authStore.user?.id))
const unreadTotal = computed(() => conversations.value.filter(c => !c.read).length)

// 🚀 HÀM FORMAT THỜI GIAN CHUYÊN NGHIỆP
const formatTime = (timeString) => {
  if (!timeString) return '';
  const date = new Date(timeString);
  if (isNaN(date.getTime())) return timeString; // Nếu nó đã là chữ "vừa xong" thì để nguyên

  const now = new Date();
  const diffInSeconds = Math.floor((now - date) / 1000);
  const diffInMinutes = Math.floor(diffInSeconds / 60);
  const diffInHours = Math.floor(diffInMinutes / 60);
  const diffInDays = Math.floor(diffInHours / 24);

  if (diffInMinutes < 1) return 'vừa xong';
  if (diffInHours < 24) {
    return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }); // Ví dụ: 14:30
  }
  if (diffInDays < 7) {
    const days = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
    return days[date.getDay()]; // Ví dụ: T2, T3
  }
  return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' }); // Ví dụ: 15/04
}

const formatPreviewMessage = (text) => {
  if (!text) return 'Chưa có tin nhắn';
  if (text === '[UNSENT]') return 'Tin nhắn đã thu hồi'; // Hỗ trợ luôn tính năng gỡ tin nhắn vừa làm
  if (text.startsWith('[IMAGE:')) return '📸 Đã gửi một hình ảnh';
  if (text.startsWith('[SYS_CALL:')) return '📞 Cuộc gọi video';
  if (text.startsWith('[POST_SHARE_ID:')) return '📌 Đã chia sẻ bài viết';
  return text;
}

const filteredConversations = computed(() => {
  if (!searchQuery.value.trim()) return conversations.value
  const query = searchQuery.value.toLowerCase().trim()
  return conversations.value.filter(c => c.name.toLowerCase().includes(query))
})

const loadConversations = async () => {
  loading.value = true
  try {
    const res = await api.get(`/api/conversations/user/${currentUserId.value}`);
    
    conversations.value = res.data.map(item => ({
      id: item.id, 
      name: item.name, 
      avatar: item.avatar,
      lastMessage: formatPreviewMessage(item.lastMessage), 
      time: formatTime(item.time), // 🚀 DÙNG HÀM MỚI ĐỂ FORMAT THỜI GIAN
      read: item.read === true,
      online: item.online === true,
      partnerID: item.partnerID,
      following: item.following === true,
      followed: item.followed === true
    }));

    connectSidebarWebSocket();

  } catch (err) {
    console.error("Lỗi tải danh sách chat:", err);
  } finally {
    setTimeout(() => { loading.value = false }, 300);
  }
}

const connectSidebarWebSocket = () => {
  if (stompClient?.connected) return;

  const socket = new SockJS('http://localhost:8080/ws-chat');
  stompClient = Stomp.over(socket);
  stompClient.debug = null; 

  stompClient.connect({ 'Authorization': `Bearer ${authStore.user?.token}` }, () => {
    conversations.value.forEach(c => {
      const sub = stompClient.subscribe(`/topic/${c.id}`, (payload) => {
        handleIncomingMessage(JSON.parse(payload.body), c.id);
      });
      activeSubscriptions.push(sub);
    });
  });
}

const handleIncomingMessage = (msg, convId) => {
  if (msg.type) {
    if (msg.type === 'read_receipt' && Number(msg.senderId) !== currentUserId.value) {
      const conv = conversations.value.find(c => c.id === convId);
      if (conv) conv.read = true;
    }
    // Cập nhật preview nếu tin nhắn bị thu hồi
    if (msg.type === 'unsend') {
       const conv = conversations.value.find(c => c.id === convId);
       if (conv) conv.lastMessage = 'Tin nhắn đã thu hồi';
    }
    return;
  }

  const idx = conversations.value.findIndex(c => c.id === convId);
  if (idx !== -1) {
    const conv = conversations.value[idx];
    
    conv.lastMessage = formatPreviewMessage(msg.content);
    conv.time = "vừa xong"; // Tin nhắn mới đến thì chắc chắn là vừa xong
    
    const senderId = Number(msg.sender?.accountID || msg.senderID);
    const isMine = senderId === currentUserId.value;

    // 🚀 LÔGIC UNREAD ĐÃ CHUẨN:
    // Nếu mình đang nhắn với người đó ở chatbox -> read = true
    // Nếu tin nhắn là do mình gửi (đồng bộ tab) -> read = true
    // Chỉ in đậm nếu người khác gửi và chatbox chưa mở
    if (chatStore.activeChat?.id === convId || isMine) {
      conv.read = true;
    } else {
      conv.read = false; 
    }

    conversations.value.splice(idx, 1);
    conversations.value.unshift(conv);
  }
}

const disconnectSidebarWebSocket = () => {
  activeSubscriptions.forEach(sub => sub.unsubscribe());
  activeSubscriptions = [];
  if (stompClient) stompClient.disconnect();
}

const selectChat = (conv) => {
  chatStore.openChat(conv)
  conv.read = true
}

const closeSidebar = () => { chatStore.isMessengerOpen = false }

const handleClickOutside = (event) => {
  if (
    chatStore.isMessengerOpen && 
    sidebarRef.value && 
    !sidebarRef.value.contains(event.target) &&
    !event.target.closest('.action-wrapper button')
  ) {
    closeSidebar()
  }
}

watch(() => chatStore.isMessengerOpen, (isOpen) => {
  if (isOpen) {
    loadConversations();
  } else {
    disconnectSidebarWebSocket();
  }
})

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  if (chatStore.isMessengerOpen) loadConversations()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  disconnectSidebarWebSocket();
})
</script>

<style scoped lang="scss" src="./ChatSidebar.scss"></style>