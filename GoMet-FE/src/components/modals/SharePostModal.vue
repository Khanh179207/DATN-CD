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
const contacts = ref([])
const isLoadingContacts = ref(false)
const searchContact = ref('')
const showEmailForm = ref(false)
const gmailForm = ref({
  email: '',
  message: 'Chào bạn, mình thấy công thức này rất hay và muốn chia sẻ với bạn!'
})
const isSendingEmail = ref(false)

// Computed for Share URL
const shareUrl = computed(() => window.location.origin + '/post/' + (props.post.postID || props.post.id))

// URL Sharing
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
  } finally {
    isLoadingContacts.value = false
  }
}

const filteredContacts = computed(() => {
  if (!searchContact.value) return contacts.value
  return contacts.value.filter(c => 
    c.username.toLowerCase().includes(searchContact.value.toLowerCase())
  )
})

const shareToChat = async (contact) => {
  if (!authStore.isAuthenticated) return
  
  const myId = authStore.user.accountID || authStore.user.id
  const targetId = contact.accountID
  // 🚀 [MỚI]: Trích xuất ID bài viết siêu linh hoạt (PostID (DTO), id (Normalized), post_id (Legacy))
  const postId = props.post.postID || props.post.id || props.post.post_id

  try {
    const convRes = await api.post('/api/conversations/access', { 
        user1Id: myId, 
        user2Id: targetId 
    })
    const convId = convRes.data.conversationID
    
    // Mở khung chat ngay lập tức
    chatStore.openChat({
      id: convId,
      name: contact.username,
      avatar: contact.avatar,
      online: true
    })

    // 🚀 [SỬA]: Không dùng Event nữa mà đưa vào Hàng chờ trong Pinia
    // Điều này đảm bảo tin nhắn không bao giờ bị "rơi" dù mạng lag hay chat chưa load xong
    const socketMessage = {
        conversation: { conversationID: convId },
        sender: { accountID: myId },
        content: `[POST_SHARE_ID:${postId}]`
    }
    
    chatStore.addSpecialMessage(socketMessage);
    toast.success(`Đã chuẩn bị chia sẻ cho ${contact.username}`);
    emit('close');

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
  fetchContacts()
})
</script>

<template>
  <Teleport to="body">
    <Transition name="fade-scale">
      <div v-if="show" class="share-modal-backdrop" @click.self="emit('close')">
        <div class="share-modal-container">
          <!-- Close Button -->
          <button class="btn-close-abs" @click="emit('close')">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>

          <div class="modal-inner">
            <h2 class="modal-title">Chia sẻ công thức</h2>

            <!-- Top Action Cards -->
            <div class="action-grid" v-if="!showEmailForm">
              <div class="action-card" @click="copyLink">
                <div class="icon-wrap blue">
                   <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"></path><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"></path></svg>
                </div>
                <span>Sao chép liên kết</span>
              </div>
              <div class="action-card" @click="showEmailForm = true">
                <div class="icon-wrap red">
                   <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                </div>
                <span>Gửi qua Gmail</span>
              </div>
            </div>

            <!-- Gmail Form (Appears when clicked) -->
            <Transition name="slide">
              <div v-if="showEmailForm" class="email-form-section">
                <div class="form-header">
                  <button @click="showEmailForm = false" class="btn-back">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
                  </button>
                  <span>Gửi qua Email</span>
                </div>
                <div class="inputs-stack">
                  <input v-model="gmailForm.email" type="email" placeholder="Nhập địa chỉ email người nhận...">
                  <textarea v-model="gmailForm.message" rows="2" placeholder="Lời nhắn..."></textarea>
                  <button class="btn-send-now" :disabled="isSendingEmail" @click="sendGmail">
                    {{ isSendingEmail ? 'Đang gửi...' : 'Gửi ngay' }}
                  </button>
                </div>
              </div>
            </Transition>

            <!-- Friend Section -->
            <div class="friends-section">
              <div class="section-label">
                <span class="bar"></span>
                <span class="text">GỬI CHO BẠN BÈ</span>
              </div>

              <div class="search-wrap">
                <span class="search-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                </span>
                <input v-model="searchContact" placeholder="Tìm bạn bè...">
              </div>

              <div class="contacts-scroll custom-scroll">
                <div v-if="isLoadingContacts" class="loading-contacts">Đang tải...</div>
                <template v-else-if="filteredContacts.length > 0">
                  <div v-for="c in filteredContacts" :key="c.accountID" class="contact-row">
                    <div class="row-left">
                      <div class="avatar-box">
                         <img :src="c.avatar || `https://ui-avatars.com/api/?name=${c.username}`" alt="avt">
                         <span class="status-dot"></span>
                      </div>
                      <div class="names">
                        <span class="uname">{{ c.username }}</span>
                        <span class="status-text"><span class="dot"></span> Online</span>
                      </div>
                    </div>
                    <button class="btn-send-chat" @click="shareToChat(c)">Gửi</button>
                  </div>
                </template>
                <div v-else class="empty-mini">Chưa có người nhắn tin hoặc theo dõi</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped lang="scss">
.share-modal-backdrop {
  position: fixed; inset: 0; z-index: 9999;
  background: rgba(0, 0, 0, 0.2); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
}

.share-modal-container {
  background: #f1f3f5;
  width: 440px; max-width: 95%;
  border-radius: 32px;
  position: relative;
  box-shadow: 0 30px 60px rgba(0,0,0,0.15);
  overflow: hidden;
  animation: modalPop 0.4s cubic-bezier(0.17, 0.89, 0.32, 1.28);
}

@keyframes modalPop {
  from { opacity: 0; transform: translateY(20px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.btn-close-abs {
  position: absolute; right: 24px; top: 24px;
  background: none; border: none; color: #adb5bd; cursor: pointer;
  transition: 0.2s; z-index: 10;
  &:hover { color: #f03e3e; transform: rotate(90deg); }
}

.modal-inner {
  padding: 32px;
}

.modal-title {
  font-family: 'Playfair Display', serif;
  font-weight: 800; color: #212529;
  margin: 0 0 28px 4px; font-size: 1.6rem;
}

/* top actions */
.action-grid {
  display: grid; grid-template-columns: 1fr 1fr; gap: 16px;
  margin-bottom: 32px;
}

.action-card {
  background: white; border-radius: 24px; padding: 24px 12px;
  display: flex; flex-direction: column; align-items: center; gap: 12px;
  cursor: pointer; transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0,0,0,0.03);
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0,0,0,0.08);
  }

  .icon-wrap {
    width: 56px; height: 56px; border-radius: 18px;
    display: flex; align-items: center; justify-content: center;
    
    &.blue { background: #eef2ff; color: #4361ee; }
    &.red { background: #fff5f5; color: #fa5252; }
  }

  span { font-size: 0.85rem; font-weight: 700; color: #495057; }
}

/* Gmail Form section */
.email-form-section {
    background: white; border-radius: 24px; padding: 20px;
    margin-bottom: 32px; box-shadow: 0 4px 12px rgba(0,0,0,0.03);

    .form-header {
        display: flex; align-items: center; gap: 12px; margin-bottom: 16px;
        span { font-weight: 800; color: #212529; font-size: 0.95rem; }
        .btn-back {
            background: #f1f3f5; border: none; border-radius: 10px; width: 32px; height: 32px;
            display: flex; align-items: center; justify-content: center; cursor: pointer; color: #495057;
            &:hover { background: #e9ecef; }
        }
    }

    .inputs-stack {
        display: flex; flex-direction: column; gap: 12px;
        input, textarea {
            width: 100%; padding: 12px 14px; border: 1.5px solid #f1f3f5; border-radius: 14px;
            font-size: 0.9rem; outline: none; transition: 0.2s;
            &:focus { border-color: #fa5252; }
        }
        .btn-send-now {
            background: #212529; color: white; border: none; padding: 12px;
            border-radius: 14px; font-weight: 700; cursor: pointer;
            &:hover:not(:disabled) { background: #000; }
            &:disabled { opacity: 0.6; }
        }
    }
}

/* Friend Section */
.section-label {
    display: flex; align-items: center; gap: 10px; margin-bottom: 16px;
    .bar { width: 4px; height: 18px; background: #e67e22; border-radius: 2px; }
    .text { font-size: 0.75rem; font-weight: 800; color: #adb5bd; letter-spacing: 0.5px; }
}

.search-wrap {
    position: relative; margin-bottom: 20px;
    .search-icon { position: absolute; left: 18px; top: 50%; transform: translateY(-50%); color: #adb5bd; }
    input {
        width: 100%; background: white; border: none; padding: 14px 14px 14px 46px;
        border-radius: 16px; font-size: 0.95rem; color: #212529;
        &::placeholder { color: #ced4da; }
    }
}

.contacts-scroll {
    max-height: 280px; overflow-y: auto; padding-right: 6px;

    .loading-contacts, .empty-mini { text-align: center; color: #adb5bd; padding: 20px 0; font-size: 0.85rem; }

    .contact-row {
        display: flex; align-items: center; justify-content: space-between;
        margin-bottom: 12px; background: transparent; padding: 4px 0;
        
        .row-left {
            display: flex; align-items: center; gap: 14px;
            .avatar-box {
                position: relative;
                img { width: 48px; height: 48px; border-radius: 50%; object-fit: cover; border: 2px solid white; }
                .status-dot {
                    position: absolute; right: 2px; bottom: 2px; width: 10px; height: 10px;
                    background: #51cf66; border: 2px solid white; border-radius: 50%;
                }
            }
            .names {
                display: flex; flex-direction: column;
                .uname { font-weight: 700; color: #343a40; font-size: 0.95rem; }
                .status-text {
                    font-size: 0.7rem; color: #51cf66; display: flex; align-items: center; gap: 4px;
                    .dot { width: 4px; height: 4px; background: currentColor; border-radius: 50%; }
                }
            }
        }

        .btn-send-chat {
            background: #212529; color: white; border: none; padding: 8px 20px;
            border-radius: 12px; font-weight: 700; cursor: pointer; font-size: 0.85rem;
            transition: 0.2s;
            &:hover { background: #000; transform: scale(1.05); }
            &:active { transform: scale(0.95); }
        }
    }
}

/* Scroll Custom */
.custom-scroll::-webkit-scrollbar { width: 4px; }
.custom-scroll::-webkit-scrollbar-thumb { background: #dee2e6; border-radius: 10px; }

/* Transitions */
.slide-enter-active, .slide-leave-active { transition: all 0.3s ease; }
.slide-enter-from { opacity: 0; transform: translateY(-10px); }
.slide-leave-to { opacity: 0; transform: translateY(-10px); }

.fade-scale-enter-active, .fade-scale-leave-active { transition: opacity 0.3s ease; }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; }

@media (max-width: 768px) {
  .share-modal-container {
    width: 90%; 
    max-width: 500px;
    border-radius: 28px;
  }
  
  .modal-inner { padding: 24px; }
  .modal-title { font-size: 1.4rem; margin-bottom: 24px; }
  
  .btn-close-abs { top: 16px; right: 16px; }

  /* Top Action Cards */
  .action-grid { gap: 12px; margin-bottom: 24px; }
  .action-card { 
    padding: 16px 10px; border-radius: 20px;
    .icon-wrap { width: 48px; height: 48px; border-radius: 14px; svg { width: 24px; height: 24px; } }
    span { font-size: 0.8rem; }
  }

  /* Gmail Form */
  .email-form-section {
    padding: 16px; margin-bottom: 24px; border-radius: 20px;
    .inputs-stack input, .inputs-stack textarea { font-size: 0.85rem; padding: 10px 12px; }
    .inputs-stack .btn-send-now { padding: 10px; font-size: 0.9rem; }
  }

  /* Contact List */
  .contacts-scroll {
    max-height: 250px;
    .contact-row .row-left .avatar-box img { width: 40px; height: 40px; }
    .contact-row .row-left .names .uname { font-size: 0.9rem; }
    .contact-row .btn-send-chat { padding: 6px 16px; font-size: 0.8rem; }
  }
}

/* --- 2. Màn hình Mobile lớn (Dưới 480px) --- */
@media (max-width: 480px) {
  .share-modal-container { width: 95%; border-radius: 24px; }
  
  .modal-inner { padding: 20px; }
  .modal-title { font-size: 1.25rem; margin-bottom: 20px; }
  
  .btn-close-abs { 
    top: 12px; right: 12px; 
    svg { width: 20px; height: 20px; }
  }

  /* Chuyển 2 ô chọn (Copy / Email) thành xếp chồng dọc để nút đủ to dễ bấm */
  .action-grid { grid-template-columns: 1fr; gap: 10px; margin-bottom: 20px; }
  .action-card { 
    flex-direction: row; /* Chuyển thành dạng thanh ngang */
    justify-content: flex-start;
    padding: 12px 16px; 
    border-radius: 16px;
    
    .icon-wrap { width: 40px; height: 40px; border-radius: 12px; svg { width: 20px; height: 20px; } }
    span { font-size: 0.9rem; }
  }

  .email-form-section { padding: 14px; margin-bottom: 20px; border-radius: 16px; }

  /* Thu gọn ô tìm kiếm */
  .search-wrap { 
    margin-bottom: 16px;
    input { padding: 12px 12px 12px 40px; font-size: 0.9rem; border-radius: 12px; }
    .search-icon { left: 14px; svg { width: 16px; height: 16px; } }
  }

  /* Thu gọn list bạn bè */
  .contacts-scroll {
    max-height: 200px;
    .contact-row {
      margin-bottom: 10px;
      .row-left { gap: 10px; }
      .row-left .avatar-box img { width: 36px; height: 36px; }
      .row-left .names .uname { font-size: 0.85rem; }
      .btn-send-chat { padding: 6px 12px; font-size: 0.75rem; border-radius: 8px; }
    }
  }
}

/* --- 3. Màn hình Mobile siêu nhỏ (Dưới 360px - vd iPhone SE) --- */
@media (max-width: 360px) {
  .modal-inner { padding: 16px; }
  .modal-title { font-size: 1.15rem; }
  .action-card { padding: 10px 12px; .icon-wrap { width: 36px; height: 36px; } span { font-size: 0.85rem; } }
  .contacts-scroll .contact-row .row-left .names .uname { font-size: 0.8rem; }
}
</style>
