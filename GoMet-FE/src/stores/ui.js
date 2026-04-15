import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUIStore = defineStore('ui', () => {
  const isStoreOpen = ref(false)

  const openStore = () => {
    isStoreOpen.value = true
  }

  const closeStore = () => {
    isStoreOpen.value = false
  }

  const toggleStore = () => {
    isStoreOpen.value = !isStoreOpen.value
  }

  return {
    isStoreOpen,
    openStore,
    closeStore,
    toggleStore
  }
})
