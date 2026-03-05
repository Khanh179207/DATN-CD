import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import { useAuthStore } from './auth'

export const useShoppingStore = defineStore('shopping', () => {
  const items = ref([])
  const authStore = useAuthStore()

  const fetchCart = async () => {
    if (!authStore.isAuthenticated) { items.value = []; return }
    try {
      const accountId = authStore.currentUser.accountID
      const res = await axios.get(`http://localhost:8080/api/shopping/${accountId}`)
      
      items.value = res.data.map(item => ({
        id: item.shoppingId,
        name: item.ingredientName,
        // Backend đã JOIN bảng và gửi postTitle lên, ta lấy ra hiển thị
        quantity: item.postTitle || 'Tùy chỉnh', 
        checked: item.isBought === 1
      }))
    } catch (error) { console.error('Lỗi tải giỏ hàng:', error) }
  }

  const addItems = async (newIngredients, postId) => {
    if (!authStore.isAuthenticated) return

    try {
      const accountId = authStore.currentUser.accountID
      const ingredientNames = newIngredients.map(i => i.name)

      // Gửi POST ID xuống DB để lưu chuẩn hóa
      await axios.post('http://localhost:8080/api/shopping/add', {
        accountId: accountId,
        postId: postId, 
        ingredients: ingredientNames
      })
      
      await fetchCart() // Gọi lại DB để lấy danh sách mới kèm Tên Món Ăn
    } catch (error) { console.error('Lỗi thêm giỏ hàng:', error) }
  }

  const toggleItem = async (index) => {
    const item = items.value[index]
    item.checked = !item.checked 
    try {
      await axios.put(`http://localhost:8080/api/shopping/toggle/${item.id}`)
    } catch (error) {
      item.checked = !item.checked 
      console.error('Lỗi cập nhật trạng thái:', error)
    }
  }

  const removeCheckedItems = async () => {
    if (!authStore.isAuthenticated) return
    try {
      const accountId = authStore.currentUser.accountID
      await axios.delete(`http://localhost:8080/api/shopping/remove-bought/${accountId}`)
      await fetchCart() 
    } catch (error) { console.error('Lỗi xóa món đã mua:', error) }
  }

  const clearItems = async () => {
    if (!authStore.isAuthenticated) return
    try {
      const accountId = authStore.currentUser.accountID
      await axios.delete(`http://localhost:8080/api/shopping/clear-all/${accountId}`)
      items.value = []
    } catch (error) { console.error('Lỗi làm sạch giỏ hàng:', error) }
  }

  const unreadCount = computed(() => items.value.filter(i => !i.checked).length)

  return { items, fetchCart, addItems, toggleItem, removeCheckedItems, clearItems, unreadCount }
})