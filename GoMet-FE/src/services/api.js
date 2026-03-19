import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  withCredentials: false
  // Không set Content-Type mặc định - let axios tự detect
})

// Attach auth token if present (skip for OPTIONS requests)
api.interceptors.request.use(config => {
  // Skip authorization header for CORS preflight requests
  if (config.method?.toUpperCase() === 'OPTIONS') {
    console.log('[API Request] Skipping auth for OPTIONS request:', config.url)
    return config
  }

  const user = JSON.parse(localStorage.getItem('user') || 'null')
  if (user?.token) {
    config.headers.Authorization = `Bearer ${user.token}`
    console.log('[API Request] Auth token attached:', {
      url: config.url,
      method: config.method,
      hasToken: !!user.token
    })
  } else {
    console.log('[API Request] No auth token available:', {
      url: config.url,
      method: config.method,
      userInStorage: !!user
    })
  }
  return config
})

// Global error handler
api.interceptors.response.use(
  res => {
    console.log('[API Response] Success:', {
      status: res.status,
      statusText: res.statusText,
      url: res.config?.url,
      method: res.config?.method,
      dataType: typeof res.data
    })
    return res
  },
  err => {
    // Log detailed error info for debugging
    console.error('[API Error] Full Details:', {
      status: err.response?.status,
      statusText: err.response?.statusText,
      message: err.response?.data?.message,
      url: err.config?.url,
      method: err.config?.method,
      requestHeaders: err.config?.headers,
      responseData: err.response?.data,
      errorCode: err.code,
      errorMessage: err.message
    })

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