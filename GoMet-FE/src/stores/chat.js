import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useChatStore = defineStore('chat', () => {
  // Người đang chat hiện tại (Object)
  const activeChat = ref(null) 
  
  // Trạng thái mở/đóng dropdown trên header
  const isMessengerOpen = ref(false)

  // Hàm mở chat (Gọi từ Header)
  const openChat = (user) => {
    activeChat.value = user
    isMessengerOpen.value = false // Đóng dropdown trên header lại
  }

  // Hàm đóng chat
  const closeChat = () => {
    activeChat.value = null
  }

  return { activeChat, isMessengerOpen, openChat, closeChat }
})