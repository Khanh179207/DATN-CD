import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useChatStore = defineStore('chat', () => {
  // Người đang chat hiện tại (Object)
  const activeChat = ref(null) 
  
  // Trạng thái đóng/mở dropdown tin nhắn ở Header
  const isMessengerOpen = ref(false)

  // Tổng số tin nhắn chưa đọc (Hiển thị badge đỏ ở Header)
  const totalUnreadCount = ref(0)

  // Hàm mở khung chat nhỏ
  const openChat = (user) => {
    // Đảm bảo user truyền vào có ID để MiniChatBox gọi API chính xác
    activeChat.value = user
    isMessengerOpen.value = false 
  }

  // Hàm đóng khung chat
  const closeChat = () => {
    activeChat.value = null
  }

  // Hàm cập nhật số tin nhắn chưa đọc (Gọi từ API hoặc Socket)
  const setUnreadCount = (count) => {
    totalUnreadCount.value = count
  }

  return { 
    activeChat, 
    isMessengerOpen, 
    totalUnreadCount, 
    openChat, 
    closeChat,
    setUnreadCount 
  }
})