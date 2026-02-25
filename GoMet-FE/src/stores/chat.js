import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useChatStore = defineStore('chat', () => {
  // Currently active chat person (Object)
  const activeChat = ref(null) 
  
  // Open/close state of the header dropdown
  const isMessengerOpen = ref(false)

  // Open chat function (called from Header)
  const openChat = (user) => {
    activeChat.value = user
    isMessengerOpen.value = false // Close the header dropdown
  }

  // Close chat function
  const closeChat = () => {
    activeChat.value = null
  }

  return { activeChat, isMessengerOpen, openChat, closeChat }
})