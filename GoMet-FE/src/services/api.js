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
api.interceptors.response.use(
  res => res,
  err => {
    const status = err.response?.status;
    const message = err.response?.data?.message;

    // Chỉ ép Logout nếu:
    // 1. Token hết hạn (401)
    // 2. Tài khoản bị khóa (403 và message là ACCOUNT_BANNED)
    if (status === 401 || (status === 403 && message === 'ACCOUNT_BANNED')) {
      const user = JSON.parse(localStorage.getItem('user') || 'null')
      if (user?.token) {
        localStorage.removeItem('user')
        
        const isBanned = message === 'ACCOUNT_BANNED';

        window.dispatchEvent(new CustomEvent('auth:force-logout', {
          detail: { isBanned: isBanned }
        }))
      }
    }
    
    // Nếu là lỗi 403 khác (Ví dụ: VIEW_LIMIT_REACHED), ta trả lỗi về để Component hiện Modal nạp tiền
    return Promise.reject(err)
  }
)

export default api