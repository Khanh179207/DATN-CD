import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
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
    if (err.response?.status === 401) {
      const user = JSON.parse(localStorage.getItem('user') || 'null')
      // Only force logout if the user was actually logged in (not a public-endpoint 401)
      if (user?.token) {
        localStorage.removeItem('user')
        // Notify the auth store + other tabs
        window.dispatchEvent(new CustomEvent('auth:force-logout'))
      }
    }
    return Promise.reject(err)
  }
)

export default api
