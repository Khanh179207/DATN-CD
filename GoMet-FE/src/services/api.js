import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// Attach auth token if present
api.interceptors.request.use(config => {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  if (user?.token) {
    config.headers.Authorization = `Bearer ${user.token}`
  }
  return config
})

// Global error handler
// Trong file api.js - Đoạn xử lý Interceptor Response
api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401 || err.response?.status === 403) {
      const user = JSON.parse(localStorage.getItem('user') || 'null')
      if (user?.token) {
        localStorage.removeItem('user')
        
        // 🔥 SỬA DÒNG NÀY: Check đúng cái KEY mà Backend gửi (ACCOUNT_BANNED)
        const isBanned = err.response?.data?.message === 'ACCOUNT_BANNED' || err.response?.status === 403;

        window.dispatchEvent(new CustomEvent('auth:force-logout', {
          detail: { isBanned: isBanned }
        }))
      }
    }
    return Promise.reject(err)
  }
)

export default api