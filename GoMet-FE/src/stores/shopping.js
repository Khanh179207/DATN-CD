import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api' // 🔥 Import api đã cấu hình JWT (Tự động gắn Token)
import { useAuthStore } from './auth'

export const useShoppingStore = defineStore('shopping', () => {
  const items = ref([])
  const authStore = useAuthStore()

  const fetchCart = async () => {
    if (!authStore.isAuthenticated) { items.value = []; return }
    try {
      const accountId = authStore.currentUser.accountID
      // 🔥 Dùng api.get và bỏ cái http://localhost:8080 đi
      const res = await api.get(`/api/shopping/${accountId}`)

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

      // 🔥 Dùng api.post
      await api.post('/api/shopping/add', {
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
      // 🔥 Dùng api.put
      await api.put(`/api/shopping/toggle/${item.id}`)
    } catch (error) {
      item.checked = !item.checked
      console.error('Lỗi cập nhật trạng thái:', error)
    }
  }

  const removeCheckedItems = async () => {
    if (!authStore.isAuthenticated) return
    try {
      const accountId = authStore.currentUser.accountID
      // 🔥 Dùng api.delete
      await api.delete(`/api/shopping/remove-bought/${accountId}`)
      await fetchCart()
    } catch (error) { console.error('Lỗi xóa món đã mua:', error) }
  }

  const clearItems = async () => {
    if (!authStore.isAuthenticated) return
    try {
      const accountId = authStore.currentUser.accountID
      // 🔥 Dùng api.delete
      await api.delete(`/api/shopping/clear-all/${accountId}`)
      items.value = []
    } catch (error) { console.error('Lỗi làm sạch giỏ hàng:', error) }
  }

  const unreadCount = computed(() => items.value.filter(i => !i.checked).length)

  return { items, fetchCart, addItems, toggleItem, removeCheckedItems, clearItems, unreadCount }
})