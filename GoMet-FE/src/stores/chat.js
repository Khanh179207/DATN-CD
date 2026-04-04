import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useChatStore = defineStore('chat', () => {
  // Người đang chat hiện tại (Object)
  const activeChat = ref(null) 
  
  // Trạng thái đóng/mở dropdown tin nhắn ở Header
  const isMessengerOpen = ref(false)

  // Tổng số tin nhắn chưa đọc (Hiển thị badge đỏ ở Header)
  const totalUnreadCount = ref(0)

  // Hàng chờ tin nhắn đặc biệt (VD: Chia sẻ bài viết) để tránh Race Condition
  const specialMessageQueue = ref([])

  // Hàm mở khung chat nhỏ
  const openChat = (user) => {
    // Đảm bảo user truyền vào có ID để MiniChatBox gọi API chính xác
    activeChat.value = user
    isMessengerOpen.value = false 
  }

  // Thêm tin nhắn vào hàng chờ để MiniChatBox bốc đi xử lý
  const addSpecialMessage = (msg) => {
    specialMessageQueue.value.push({
      ...msg,
      timestamp: Date.now() // Để định danh duy nhất
    })
  }

  // Xóa tin nhắn khỏi hàng chờ sau khi đã xử lý xong
  const clearSpecialMessage = (timestamp) => {
    specialMessageQueue.value = specialMessageQueue.value.filter(m => m.timestamp !== timestamp)
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
    specialMessageQueue,
    openChat, 
    closeChat,
    setUnreadCount,
    addSpecialMessage,
    clearSpecialMessage
  }
})