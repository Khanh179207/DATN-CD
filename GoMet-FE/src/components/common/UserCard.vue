<template>
  <div class="user-card" @click="goToProfile">
    <div class="avatar-wrapper">
      <img :src="displayAvatar" :alt="user.name" class="user-avatar" />
      <div v-if="user.isVerified" class="verify-badge">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
          <polyline points="20 6 9 17 4 12"></polyline>
        </svg>
      </div>
    </div>
    
    <div class="user-info">
      <h3 class="user-name">{{ user.name || user.username || 'Người dùng' }}</h3>
      <p class="user-handle">{{ displayHandle }}</p>
    </div>

    <div class="user-actions">
      <button class="btn-icon-chat" @click.stop="handleContactUser" title="Nhắn tin">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
        </svg>
      </button>

      <button 
        class="btn-follow-black" 
        :class="{ 'is-following': isFollowing }" 
        @click.stop="toggleFollow"
      >
        <span v-if="isFollowing">Đang theo dõi</span>
        <span v-else>Theo dõi</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import { toast } from '@/composables/useToast'
import { checkFollow, follow, unfollow } from '@/services/socialService'
import axios from 'axios'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const router = useRouter()
const authStore = useAuthStore()
const chatStore = useChatStore()

// Props
const props = defineProps({
  user: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

// States
const isFollowing = ref(false)

// Lấy ID mục tiêu chuẩn xác
const targetId = computed(() => props.user.id || props.user.accountID)

// Kiểm tra trạng thái Follow ban đầu
onMounted(async () => {
  const currentUserId = authStore.user?.accountID || authStore.user?.id
  if (currentUserId && targetId.value && currentUserId !== targetId.value) {
    try {
      const isFav = await checkFollow(currentUserId, targetId.value)
      isFollowing.value = !!isFav
    } catch {
      // Bỏ qua lỗi nếu có
    }
  }
})

// --- XỬ LÝ GIAO DIỆN TEXT ---
const displayAvatar = computed(() => {
  if (props.user.avatar) return props.user.avatar
  const nameToUse = props.user.name || props.user.username || 'U'
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(nameToUse)}&background=EA580C&color=fff`
})

const displayHandle = computed(() => {
  if (props.user.handle) return `@${props.user.handle.replace('@', '')}`
  const nameToUse = props.user.name || props.user.username || 'user'
  return '@' + nameToUse.toLowerCase().replace(/\s+/g, '_')
})

// --- ACTIONS ---
const goToProfile = () => {
  if (targetId.value) {
    router.push(`/profile/${targetId.value}`)
  }
}

const handleContactUser = async () => {
  if (!authStore.isAuthenticated) { 
    toast.warn(t('toast.need_login') || 'Vui lòng đăng nhập!')
    return 
  }
  
  const currentUserId = authStore.user?.accountID || authStore.user?.id

  if (currentUserId === targetId.value) { 
    toast.info('Sếp không thể tự nhắn tin cho chính mình đâu nha!')
    return 
  }
  
  try {
    const res = await axios.post('http://localhost:8080/api/conversations/access', { 
      user1Id: currentUserId, 
      user2Id: targetId.value 
    })
    
    chatStore.openChat({ 
      id: res.data.conversationID, 
      name: props.user.name || props.user.username, 
      avatar: displayAvatar.value, 
      online: true 
    })
    chatStore.isMessengerOpen = true
    toast.success(`Đang kết nối với ${props.user.name || props.user.username}...`)
  } catch (err) { 
    toast.error('Lỗi kết nối chat!') 
  }
}

const toggleFollow = async () => {
  if (!authStore.isAuthenticated) { 
    toast.warn(t('toast.need_login') || 'Vui lòng đăng nhập!')
    return 
  }
  
  const currentUserId = authStore.user?.accountID || authStore.user?.id

  if (!targetId.value || currentUserId === targetId.value) {
    toast.info('Bạn không thể theo dõi chính mình!')
    return
  }
  
  try {
    if (isFollowing.value) {
      await unfollow(currentUserId, targetId.value)
      isFollowing.value = false
    } else {
      await follow(currentUserId, targetId.value)
      isFollowing.value = true
    }
  } catch (err) { 
    toast.error('Lỗi thao tác, vui lòng thử lại!') 
  }
}
</script>

<style scoped lang="scss">
.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  background: #ffffff;
  border-radius: 20px;
  border: 1px solid #F3F4F6;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
  cursor: pointer;
  text-align: center;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  overflow: hidden;

  // Hiệu ứng khi hover
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
    border-color: #E2E8F0;
  }
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 16px;
}

.user-avatar {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #FFF7ED;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
}

.verify-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #3B82F6; // Màu xanh dương
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid white;
}

.user-info {
  margin-bottom: 24px;
  width: 100%;
}

.user-name {
  font-size: 1.15rem;
  font-weight: 800;
  color: #1C1917;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-handle {
  font-size: 0.9rem;
  color: #A8A29E;
  margin: 0;
}

/* --- NHÓM NÚT ACTION MỚI --- */
.user-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  width: 100%;
}

/* Nút Chat (Icon vuông bo viền mỏng giống ảnh) */
.btn-icon-chat {
  width: 40px; 
  height: 40px; 
  border-radius: 50%;
  border: 1.5px solid #E2E8F0;
  background: transparent; 
  color: #64748B;
  display: flex; 
  align-items: center; 
  justify-content: center;
  cursor: pointer; 
  transition: 0.2s;

  &:hover { 
    border-color: #0F172A; 
    color: #0F172A; 
    background: #F8FAFC; 
    transform: scale(1.05);
  }
}

/* Nút Follow Đen Bo Góc */
.btn-follow-black {
  flex: 1;
  padding: 10px 0; 
  border-radius: 100px;
  font-weight: 800; 
  font-size: 0.9rem; 
  cursor: pointer; 
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  background: #1C1917; /* Màu đen */
  color: #fff; 
  border: 1.5px solid #1C1917;
  
  &:hover { 
    background: #030712; 
    transform: translateY(-2px); 
    box-shadow: 0 4px 12px rgba(0,0,0,0.15); 
  }
  
  /* Trạng thái đã follow */
  &.is-following {
    background: transparent; 
    color: #1C1917;
    
    &:hover { 
      background: #FEE2E2; /* Nền đỏ nhạt */
      color: #DC2626;      /* Chữ đỏ đậm */
      border-color: #DC2626; 
    }
    
    &:hover span { display: none; }
    &:hover::after { content: "Hủy theo dõi"; }
  }
}
</style>