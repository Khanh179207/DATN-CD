<template>
  <transition name="slide-right">
    <div class="chat-sidebar" v-if="chatStore.isMessengerOpen" ref="sidebarRef">
      
      <div class="sidebar-header">
        <div class="header-title">
          <h3>{{ $t('header.messages', 'Tin nhắn') }}</h3>
          <span class="unread-count" v-if="unreadTotal > 0">{{ unreadTotal }}</span>
        </div>
        <div class="header-actions">
          <button class="action-btn" title="Cài đặt">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
          </button>
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
import axios from 'axios'

const chatStore = useChatStore()
const authStore = useAuthStore()

const conversations = ref([])
const loading = ref(false)
const searchQuery = ref('')
const sidebarRef = ref(null)

const unreadTotal = computed(() => conversations.value.filter(c => !c.read).length)

// LOGIC TÌM KIẾM THEO TÊN NGƯỜI DÙNG
const filteredConversations = computed(() => {
  if (!searchQuery.value.trim()) return conversations.value
  
  const query = searchQuery.value.toLowerCase().trim()
  return conversations.value.filter(c => 
    c.name.toLowerCase().includes(query)
  )
})

const loadConversations = async () => {
  loading.value = true
  try {
    const userId = authStore.user?.accountID || 1;
    const res = await axios.get(`http://localhost:8080/api/conversations/user/${userId}`);
    
    console.log("Dữ liệu Chat nhận được:", res.data);

    conversations.value = res.data.map(item => ({
      id: item.id, 
      name: item.name, 
      avatar: item.avatar,
      lastMessage: item.lastMessage,
      time: item.time || "vừa xong",
      read: item.read === true,
      online: item.online === true
    }));
  } catch (err) {
    console.error("Lỗi tải danh sách chat:", err);
  } finally {
    setTimeout(() => { loading.value = false }, 300);
  }
}

const selectChat = (conv) => {
  chatStore.openChat(conv)
  conv.read = true
  // Tùy chọn: Xóa nội dung tìm kiếm sau khi đã click chọn người chat
  // searchQuery.value = ''
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

watch(() => chatStore.isMessengerOpen, (newVal) => {
  if (newVal) loadConversations()
})

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped lang="scss" src="./ChatSidebar.scss"></style>