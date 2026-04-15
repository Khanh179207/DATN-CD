import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import i18n from '@/i18n'

// ⚠️ IMPORTANT: This line must start with "export"
export const useCompareStore = defineStore('compare', () => {
  const items = ref([]) 

  const count = computed(() => items.value.length)

  const toggleItem = (post) => {
    // Check if this item is already in the list
    const index = items.value.findIndex(i => i.id === post.id)
    
    if (index > -1) {
      items.value.splice(index, 1) // Already exists, remove it
    } else {
      if (items.value.length >= 3) {
        alert(i18n.global.t('compare.limit_alert'))
        return
      }
      items.value.push(post) // Not in list yet, add it
    }
  }

  const isSelected = (id) => items.value.some(i => i.id === id)
  const clearAll = () => items.value = []

  return { items, count, toggleItem, isSelected, clearAll }
})