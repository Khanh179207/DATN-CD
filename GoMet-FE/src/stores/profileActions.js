import { defineStore } from 'pinia'
import { ref } from 'vue'
import { follow, unfollow, checkFollow } from '@/services/socialService'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import { toast } from '@/composables/useToast'
import api from '@/services/api'
import i18n from '@/i18n'

export const useProfileActionsStore = defineStore('profileActions', () => {
  const authStore = useAuthStore()
  const chatStore = useChatStore()
  
  const loading = ref(false)
  const followLoading = ref(false)

  // Hàm xử lý Follow/Unfollow
  const toggleFollow = async (targetUserId, currentStatus) => {
    const myId = authStore.user?.accountID || authStore.user?.id
    if (!myId) {
      toast.info(i18n.global.t('toast.need_login_follow'))
      return currentStatus
    }
    
    if (followLoading.value) return currentStatus
    followLoading.value = true
    
    try {
      if (currentStatus) {
        await unfollow(myId, targetUserId)
        toast.success(i18n.global.t('toast.unfollow_ok'))
        return false
      } else {
        await follow(myId, targetUserId)
        toast.success(i18n.global.t('toast.follow_ok'))
        return true
      }
    } catch (err) {
      toast.error(i18n.global.t('toast.follow_error'))
      return currentStatus
    } finally {
      followLoading.value = false
    }
  }

  // Hàm mở Chat ngay lập tức (Requirement 2)
  const startConversation = async (targetUser) => {
    const myId = authStore.user?.accountID || authStore.user?.id
    if (!myId) {
      toast.info(i18n.global.t('toast.need_login_chat'))
      return
    }

    if (loading.value) return
    loading.value = true

    try {
      // 1. Lấy hoặc tạo Conversation ID từ Backend
      const res = await api.post('/api/conversations/access', { 
        user1Id: myId, 
        user2Id: parseInt(targetUser.id || targetUser.accountID) 
      })
      
      const convData = res.data
      if (convData && convData.conversationID) {
        // 2. Cập nhật Pinia ChatStore để MiniChatBox tự hiện ra
        chatStore.openChat({
          id: convData.conversationID,
          name: targetUser.username || targetUser.name,
          avatar: targetUser.avatar,
          online: true,
          partnerID: targetUser.accountID || targetUser.id
        })
      } else {
        toast.error(i18n.global.t('toast.chat_connect_fail'))
      }
    } catch (err) {
      console.error('Chat error:', err)
      // 🔥 HIỆN LỖI CẶP NHẬT TỪ BACKEND (VD: TÀI KHOẢN ĐÃ XÓA)
      const msg = err.response?.data?.message || i18n.global.t('toast.chat_connect_fail')
      toast.error(msg)
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    followLoading,
    toggleFollow,
    startConversation
  }
})
