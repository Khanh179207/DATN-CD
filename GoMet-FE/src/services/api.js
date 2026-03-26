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
// Interceptor cho Response: Xử lý thành công & lỗi toàn cục
api.interceptors.response.use(
  res => {
    // Log thành công
    console.log('[API Success]:', res.config?.url, res.status);
    return res
  },
  err => {
    const status = err.response?.status;
    const data = err.response?.data || {}; // 🔥 Bắt nguyên cục data của Backend
    const message = data.message;

    // Log chi tiết lỗi (Cực kỳ hữu ích)
    console.error('[API Error Details]:', {
      status,
      message,
      fullData: data, // Sếp F12 lên sẽ thấy trọn bộ data ở đây
      url: err.config?.url,
      method: err.config?.method
    });

    // logic CHỐT CỦA SẾP: Chỉ ép Logout nếu Token hết hạn (401) hoặc Bị Ban (403 + ACCOUNT_BANNED)
    if (status === 401 || (status === 403 && message === 'ACCOUNT_BANNED')) {
      const user = JSON.parse(localStorage.getItem('user') || 'null')
      
      // Dù có token cũ hay không, nếu đã bị BAN thì cứ bắn Event cho chắc cốp
      if (user?.token || message === 'ACCOUNT_BANNED') {
        localStorage.removeItem('user')
        
        const isBanned = message === 'ACCOUNT_BANNED';

        // 🔥 NHỒI FULL DỮ LIỆU KHÓA VÀO EVENT ĐỂ THẰNG STORE NÓ HỨNG ĐƯỢC
        window.dispatchEvent(new CustomEvent('auth:force-logout', {
          detail: { 
            isBanned: isBanned,
            banReason: data.banReason,
            bannedByName: data.bannedByName,
            bannedByEmail: data.bannedByEmail,
            bannedAt: data.bannedAt
          }
        }))
      }
    }
    
    // Trả lỗi về để các Component tự xử lý
    return Promise.reject(err)
  }
)

export default api;