import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// ⚠️ QUAN TRỌNG: Phải có chữ "export" ở đầu dòng này
export const useCompareStore = defineStore('compare', () => {
  const items = ref([]) 

  const count = computed(() => items.value.length)

  const toggleItem = (post) => {
    // Tìm xem món này đã có trong list chưa
    const index = items.value.findIndex(i => i.id === post.id)
    
    if (index > -1) {
      items.value.splice(index, 1) // Có rồi thì xóa
    } else {
      if (items.value.length >= 3) {
        alert('Chỉ được so sánh tối đa 3 món!')
        return
      }
      items.value.push(post) // Chưa có thì thêm
    }
  }

  const isSelected = (id) => items.value.some(i => i.id === id)
  const clearAll = () => items.value = []

  return { items, count, toggleItem, isSelected, clearAll }
})