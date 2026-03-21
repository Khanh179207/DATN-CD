import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  withCredentials: false
  // Để axios tự detect Content-Type (tốt cho việc up ảnh/khiếu nại)
})

// Interceptor cho Request: Đính kèm Token
api.interceptors.request.use(config => {
  // Bỏ qua header auth cho các request OPTIONS (CORS preflight) - Theo bản của team
  if (config.method?.toUpperCase() === 'OPTIONS') {
    return config
  }

  const user = JSON.parse(localStorage.getItem('user') || 'null')
  if (user?.token) {
    config.headers.Authorization = `Bearer ${user.token}`
  }
  return config
})

// Interceptor cho Response: Xử lý thành công & lỗi toàn cục
api.interceptors.response.use(
  res => {
    // Log thành công để sếp dễ theo dõi (Theo bản của team)
    console.log('[API Success]:', res.config?.url, res.status);
    return res
  },
  err => {
    const status = err.response?.status;
    const message = err.response?.data?.message;

    // Log chi tiết lỗi để sếp bắt bệnh (Cực kỳ hữu ích từ bản của team)
    console.error('[API Error Details]:', {
      status,
      message,
      url: err.config?.url,
      method: err.config?.method
    });

    // logic CHỐT CỦA SẾP: Chỉ ép Logout nếu Token hết hạn (401) hoặc Bị Ban (403 + ACCOUNT_BANNED)
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
    
    // Trả lỗi về để các Component (như PostDetail hay Search) tự xử lý logic riêng
    return Promise.reject(err)
  }
)

export default api