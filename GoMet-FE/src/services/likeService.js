import axios from 'axios'

// Giả sử sếp đã cấu hình axios instance, nếu chưa thì import axios thường và thay base url
const API_URL = 'http://localhost:8080/api/likes'

export const togglePostLike = async (accountId, postId) => {
  try {
    const response = await axios.post(`${API_URL}/toggle`, null, {
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
    const response = await axios.get(`${API_URL}/check`, {
      params: { accountId, postId }
    })
    return response.data // Trả về true/false
  } catch (error) {
    console.error('Error checking like status:', error)
    return false
  }
}