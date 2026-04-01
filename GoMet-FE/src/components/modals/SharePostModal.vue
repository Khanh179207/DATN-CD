<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import api from '@/services/api'
import { useChatStore } from '@/stores/chat'

const props = defineProps({
  post: {
    type: Object,
    required: true
  },
  show: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close'])

const authStore = useAuthStore()
const chatStore = useChatStore()
const activeTab = ref('url') // 'url', 'chat', 'gmail'
const contacts = ref([])
const isLoadingContacts = ref(false)
const searchContact = ref('')
const gmailForm = ref({
  email: '',
  message: 'Chào bạn, mình thấy công thức này rất hay và muốn chia sẻ với bạn!'
})
const isSendingEmail = ref(false)

// URL Sharing
const shareUrl = computed(() => window.location.origin + '/post/' + (props.post.postID || props.post.id))

const copyLink = () => {
  navigator.clipboard.writeText(shareUrl.value)
  toast.success("Đã sao chép liên kết!")
}

// Contact Sharing (Chat)
const fetchContacts = async () => {
  if (!authStore.isAuthenticated) return
  isLoadingContacts.value = true
  try {
    const userId = authStore.user.accountID || authStore.user.id
    const response = await api.get(`/api/share/contacts/${userId}`)
    contacts.value = response.data
  } catch (error) {
    console.error("Lỗi khi tải danh sách liên hệ:", error)
    toast.error("Không thể tải danh sách liên hệ")
  } finally {
    isLoadingContacts.value = false
  }
}

const filteredContacts = computed(() => {
  if (!searchContact.value) return contacts.value
  return contacts.value.filter(c => 
    c.username.toLowerCase().includes(searchContact.value.toLowerCase()) ||
    (c.email && c.email.toLowerCase().includes(searchContact.value.toLowerCase()))
  )
})

const shareToChat = async (contact) => {
  if (!authStore.isAuthenticated) return
  
  const myId = authStore.user.accountID || authStore.user.id
  const targetId = contact.accountID
  const postId = props.post.postID || props.post.id

  try {
    // 1. Get or Create Conversation
    const convRes = await api.post('/api/conversations/access', { 
        user1Id: myId, 
        user2Id: targetId 
    })
    const convId = convRes.data.conversationID
    
    // 2. Add to chat store and open chat box
    chatStore.openChat({
      id: convId,
      name: contact.username,
      avatar: contact.avatar,
      online: true // Assume online for now
    })

    // 3. Send MiniPost message
    // Note: The websocket connection might take a moment, 
    // we can either send it via a separate REST API or wait for socket.
    // Here, we'll use a special REST endpoint for sharing if available, 
    // or just let the user send it manually.
    // IMPROVEMENT: We will send via REST API to ensure it's delivered even if socket isn't ready.
    // Since we don't have a specific share-to-chat REST endpoint yet, we'll try to find if ChatController has a POST message.
    // ChatController only has @MessageMapping (WebSocket). 
    // For simplicity, we'll just set the input message or wait 500ms to send via socket.
    
    setTimeout(() => {
        const socketMessage = {
            conversation: { conversationID: convId },
            sender: { accountID: myId },
            content: `[POST_SHARE_ID:${postId}]`
        }
        // Access the stompClient from the MiniChatBox or window object
        // This is a bit hacky, but works if the socket is globally accessible.
        // Better: Open the chat and trigger an event for it to send.
        window.dispatchEvent(new CustomEvent('chat:send-special', { detail: socketMessage }));
        toast.success(`Đã chia sẻ cho ${contact.username}`);
        emit('close');
    }, 500);

  } catch (error) {
    console.error("Lỗi khi chia sẻ qua chat:", error)
    toast.error("Không thể chia sẻ qua chat")
  }
}

// Gmail Sharing
const sendGmail = async () => {
  if (!gmailForm.value.email) {
    toast.error("Vui lòng nhập địa chỉ email")
    return
  }
  
  isSendingEmail.value = true
  try {
    await api.post('/api/share/email', {
      toEmail: gmailForm.value.email,
      postID: props.post.postID || props.post.id,
      senderName: authStore.user?.username || 'Một người dùng GoMet',
      customMessage: gmailForm.value.message
    })
    toast.success("Đã gửi email chia sẻ!")
    emit('close')
  } catch (error) {
    console.error("Lỗi khi gửi email:", error)
    toast.error("Không thể gửi email")
  } finally {
    isSendingEmail.value = false
  }
}

onMounted(() => {
  if (activeTab.value === 'chat') fetchContacts()
})

const changeTab = (tab) => {
  activeTab.value = tab
  if (tab === 'chat' && contacts.value.length === 0) fetchContacts()
}
</script>

<template>
  <Teleport to="body">
    <Transition name="fade-scale">
      <div v-if="show" class="share-modal-backdrop" @click.self="emit('close')">
        <div class="share-modal-content">
          <!-- Modal Header -->
          <div class="modal-header">
            <div class="header-title">
              <div class="icon-wrap">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="18" cy="5" r="3"></circle><circle cx="6" cy="12" r="3"></circle><circle cx="18" cy="19" r="3"></circle><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"></line><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"></line></svg>
              </div>
              <h3>Chia sẻ bài viết</h3>
            </div>
            <button class="btn-close" @click="emit('close')">&times;</button>
          </div>

          <!-- Tabs -->
          <div class="modal-tabs">
            <button :class="{ active: activeTab === 'url' }" @click="changeTab('url')">Link URL</button>
            <button :class="{ active: activeTab === 'chat' }" @click="changeTab('chat')">Đoạn chat</button>
            <button :class="{ active: activeTab === 'gmail' }" @click="changeTab('gmail')">Gmail</button>
          </div>

          <!-- Modal Body -->
          <div class="modal-body">
            <!-- URL Tab -->
            <div v-if="activeTab === 'url'" class="tab-pane url-pane">
              <p class="pane-desc">Chia sẻ liên kết trực tiếp đến bài viết này cho bạn bè của bạn.</p>
              <div class="copy-box">
                <input readonly :value="shareUrl">
                <button @click="copyLink" class="btn-copy">Sao chép</button>
              </div>
              <div class="preview-card-mini">
                <img :src="post.image || post.media || 'https://via.placeholder.com/150'" alt="preview">
                <div class="card-text">
                  <span class="card-title">{{ post.title }}</span>
                  <span class="card-link">gomet.vn/post/{{ post.postID || post.id }}</span>
                </div>
              </div>
            </div>

            <!-- Chat Tab -->
            <div v-if="activeTab === 'chat'" class="tab-pane chat-pane">
              <div class="search-box">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                <input v-model="searchContact" placeholder="Tìm kiếm bạn bè...">
              </div>
              
              <div class="contacts-list custom-scroll">
                <div v-if="isLoadingContacts" class="loading-state">
                  <div class="spinner"></div>
                  <span>Đang tải danh sách...</span>
                </div>
                
                <template v-else-if="filteredContacts.length > 0">
                  <div v-for="c in filteredContacts" :key="c.accountID" class="contact-item" @click="shareToChat(c)">
                    <img :src="c.avatar || `https://ui-avatars.com/api/?name=${c.username}`" alt="avt" class="c-avt">
                    <div class="c-info">
                      <span class="c-name">{{ c.username }}</span>
                      <span class="c-sub">Nhấn để gửi ngay</span>
                    </div>
                    <div class="share-btn-icon">
                       <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"></path></svg>
                    </div>
                  </div>
                </template>
                
                <div v-else class="empty-state">
                  <p>Không tìm thấy liên hệ nào.</p>
                </div>
              </div>
            </div>

            <!-- Gmail Tab -->
            <div v-if="activeTab === 'gmail'" class="tab-pane gmail-pane">
              <div class="form-group">
                <label>Email người nhận</label>
                <input v-model="gmailForm.email" type="email" placeholder="example@gmail.com">
              </div>
              <div class="form-group">
                <label>Lời nhắn</label>
                <textarea v-model="gmailForm.message" rows="3" placeholder="Nhập lời nhắn chia sẻ..."></textarea>
              </div>
              <button class="btn-send-email" :disabled="isSendingEmail" @click="sendGmail">
                <span v-if="!isSendingEmail">Gửi Email Chia Sẻ</span>
                <span v-else>Đang gửi...</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped lang="scss">
.share-modal-backdrop {
  position: fixed; inset: 0; z-index: 3000;
  background: rgba(15, 23, 42, 0.4); backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center;
}

.share-modal-content {
  background: rgba(255, 255, 255, 0.95);
  width: 450px; max-width: 95%;
  border-radius: 28px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.5);
  animation: modalEnter 0.4s cubic-bezier(0.19, 1, 0.22, 1);
}

@keyframes modalEnter {
  from { opacity: 0; transform: scale(0.9) translateY(20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 24px 32px 16px;
  
  .header-title {
    display: flex; align-items: center; gap: 12px;
    .icon-wrap {
      width: 40px; height: 40px; background: #fff1f2; color: #ea580c;
      border-radius: 12px; display: flex; align-items: center; justify-content: center;
    }
    h3 { margin: 0; font-size: 1.25rem; font-weight: 900; color: #0f172a; }
  }
  
  .btn-close {
    background: none; border: none; font-size: 2rem; color: #94a3b8;
    cursor: pointer; transition: 0.2s;
    &:hover { color: #f43f5e; transform: rotate(90deg); }
  }
}

.modal-tabs {
  display: flex; padding: 0 32px; border-bottom: 1px solid #f1f5f9; gap: 24px;
  
  button {
    background: none; border: none; padding: 12px 0; font-weight: 700;
    color: #94a3b8; cursor: pointer; position: relative; transition: 0.2s;
    font-size: 0.95rem;
    
    &::after {
      content: ''; position: absolute; bottom: 0; left: 0; right: 0;
      height: 3px; background: #ea580c; transform: scaleX(0); transition: 0.3s;
      border-radius: 3px 3px 0 0;
    }
    
    &.active {
      color: #ea580c;
      &::after { transform: scaleX(1); }
    }
    
    &:hover:not(.active) { color: #64748b; }
  }
}

.modal-body {
  padding: 32px;
  min-height: 280px;
}

.pane-desc { font-size: 0.9rem; color: #64748b; margin-bottom: 20px; line-height: 1.5; }

/* URL Pane */
.copy-box {
  display: flex; gap: 10px; margin-bottom: 24px;
  input {
    flex: 1; padding: 12px 16px; border-radius: 12px; border: 1.5px solid #e2e8f0;
    background: #f8fafc; color: #475569; font-size: 0.9rem; outline: none;
  }
  .btn-copy {
    background: #1e293b; color: white; border: none; padding: 0 20px;
    border-radius: 12px; font-weight: 700; cursor: pointer; transition: 0.2s;
    &:hover { background: #0f172a; transform: translateY(-1px); }
    &:active { transform: translateY(0); }
  }
}

.preview-card-mini {
  display: flex; gap: 16px; padding: 16px; background: #fff;
  border-radius: 16px; border: 1px solid #f1f5f9;
  img { width: 60px; height: 60px; border-radius: 10px; object-fit: cover; }
  .card-text {
    display: flex; flex-direction: column; justify-content: center;
    .card-title { font-weight: 800; color: #1e293b; font-size: 0.95rem; margin-bottom: 4px; }
    .card-link { font-size: 0.75rem; color: #94a3b8; }
  }
}

/* Chat Pane */
.search-box {
  position: relative; margin-bottom: 20px;
  svg { position: absolute; left: 16px; top: 50%; transform: translateY(-50%); color: #94a3b8; }
  input {
    width: 100%; padding: 12px 16px 12px 42px; border-radius: 14px; border: 1.5px solid #e2e8f0;
    outline: none; font-size: 0.9rem; transition: 0.2s;
    &:focus { border-color: #ea580c; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); }
  }
}

.contacts-list {
  max-height: 250px; overflow-y: auto; padding-right: 4px;
  
  .loading-state, .empty-state {
    text-align: center; padding: 40px 0; color: #94a3b8; font-size: 0.9rem;
  }
  
  .contact-item {
    display: flex; align-items: center; gap: 12px; padding: 10px;
    border-radius: 16px; cursor: pointer; transition: 0.2s;
    &:hover {
      background: #fff7ed;
      .share-btn-icon { opacity: 1; transform: translateX(0); }
    }
    
    .c-avt { width: 44px; height: 44px; border-radius: 50%; object-fit: cover; }
    .c-info {
        flex: 1; display: flex; flex-direction: column;
        .c-name { font-weight: 700; color: #1e293b; font-size: 0.95rem; }
        .c-sub { font-size: 0.75rem; color: #94a3b8; }
    }
    
    .share-btn-icon {
        color: #ea580c; opacity: 0; transform: translateX(-5px); transition: 0.3s;
    }
  }
}

/* Gmail Pane */
.form-group {
  margin-bottom: 20px;
  label { display: block; font-size: 0.85rem; font-weight: 800; color: #475569; margin-bottom: 8px; text-transform: uppercase; letter-spacing: 0.5px; }
  input, textarea {
    width: 100%; padding: 12px 16px; border-radius: 12px; border: 1.5px solid #e2e8f0;
    font-size: 0.95rem; outline: none; transition: 0.2s;
    &:focus { border-color: #ea580c; }
  }
  textarea { resize: none; }
}

.btn-send-email {
  width: 100%; padding: 14px; background: #ea580c; color: white; border: none;
  border-radius: 14px; font-weight: 800; font-size: 1rem; cursor: pointer;
  transition: 0.3s; box-shadow: 0 4px 12px rgba(234, 88, 12, 0.2);
  &:hover:not(:disabled) { background: #d9480f; transform: translateY(-2px); box-shadow: 0 8px 20px rgba(234, 88, 12, 0.3); }
  &:disabled { opacity: 0.7; cursor: not-allowed; }
}

/* Scroll Custom */
.custom-scroll::-webkit-scrollbar { width: 5px; }
.custom-scroll::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }

/* Spinner */
.spinner {
  width: 24px; height: 24px; border: 3px solid rgba(234, 88, 12, 0.1);
  border-top-color: #ea580c; border-radius: 50%;
  animation: spin 0.8s linear infinite; margin: 0 auto 10px;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* Transition */
.fade-scale-enter-active, .fade-scale-leave-active { transition: all 0.3s ease; }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; }
</style>
