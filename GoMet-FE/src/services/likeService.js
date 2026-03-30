import api from '@/services/api' // 🔥 Gọi api xịn xò vào, đá bay axios

export const togglePostLike = async (accountId, postId) => {
  try {
    // 🔥 Dùng api.post và gọi thẳng đường dẫn tương đối
    const response = await api.post('/api/likes/toggle', null, {
      params: { accountId, postId }
    })
    return response.data // Trả về true (liked) hoặc false (unliked)
  } catch (error) {
    console.error('Error toggling like:', error)
    throw error
  }
}

export const checkPostLiked = async (accountId, postId) => {
  try {
    // 🔥 Dùng api.get
    const response = await api.get('/api/likes/check', {
      params: { accountId, postId }
    })
    return response.data // Trả về true/false
  } catch (error) {
    console.error('Error checking like status:', error)
    return false
  }
}