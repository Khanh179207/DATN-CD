import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import * as authService from '@/services/authService'
import { toast } from '@/composables/useToast'

export const useAuthStore = defineStore('auth', () => {
  const router = useRouter()

  // 1. STATE — persist to localStorage (có migration cho dữ liệu cũ)
  const user = ref((() => {
    const saved = localStorage.getItem('user')
    if (!saved) return null
    const userData = JSON.parse(saved)
    // Nếu dữ liệu cũ thiếu trường mới, gán mặc định để tránh NaN
    if (userData && userData.remainingPostViews === undefined) {
      const now = new Date()
      const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
      userData.remainingPostViews = (userData.isPremium || userData.isAdmin) ? Infinity : 3
      userData.lastViewDate = today
      userData.viewedPostIds = []
    }
    return userData
  })())

  // Cross-tab sync: when another tab logs in/out, update this tab too
  window.addEventListener('storage', (e) => {
    if (e.key === 'user') {
      user.value = e.newValue ? JSON.parse(e.newValue) : null
      if (!user.value) {
        router.push('/').catch(() => {})
      }
    }
  })

// 🔥 Force-logout event fired by api.js interceptor on 401/403
  window.addEventListener('auth:force-logout', (event) => {
    // 1. Dọn dẹp state
    user.value = null
    localStorage.removeItem('user') // Đảm bảo clear sạch local storage

    // 2. Phân loại lý do để báo lỗi
    const isBanned = event.detail?.isBanned

    if (isBanned) {
      // Truy xuất thông tin khóa tài khoản (Nếu API có gửi kèm theo)
      const banReason = event.detail?.banReason || event.detail?.reason || 'Vi phạm tiêu chuẩn cộng đồng';
      const bannedBy = event.detail?.bannedByName || event.detail?.bannedBy || 'Quản trị viên';
      let timeStr = '';
      let rawTimeStr = '';
      if (event.detail?.bannedAt) {
         const d = new Date(event.detail.bannedAt);
         rawTimeStr = `${d.toLocaleTimeString('vi-VN', {hour: '2-digit', minute:'2-digit'})} ngày ${d.toLocaleDateString('vi-VN')}`;
         timeStr = ` vào lúc ${rawTimeStr}`;
      }
      const fullMsg = `Tài khoản của bạn đã bị khóa${timeStr} bởi ${bannedBy}. Lý do: ${banReason}.`;
      
      setTimeout(() => {
        // Thử kích hoạt mở UI Đăng nhập trước
        window.dispatchEvent(new CustomEvent('ui:open-login')) 
        setTimeout(() => {
          window.dispatchEvent(new CustomEvent('auth:banned-login-prompt', { detail: { msg: fullMsg, details: { reason: banReason, by: bannedBy, time: rawTimeStr } } }))
        }, 200)
      }, 300)
    } else {
      toast.error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.')
      // Nếu lỗi token bình thường thì mới cho đẩy về trang chủ
      router.push('/').catch(() => {})
    }
  })

  // 2. GETTERS
  const isAuthenticated = computed(() => !!user.value)
  const isAdmin = computed(() => user.value?.isAdmin === true || user.value?.role === 'admin')
  const currentUser = computed(() => user.value)


  // 🌟 NEW ACTION: Centralized user initialization (Handles login, google, register)
  function setUser(data) {
    if (!data) return

    // Logic tính toán số lượt xem (ưu tiên từ persistent cache)
    const now = new Date()
    const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
    
    const limitsCache = JSON.parse(localStorage.getItem('gomet_view_limits') || '{}')
    const uid = String(data.accountID || data.id)
    const cache = limitsCache[uid]

    let views = 3
    let ids = []

    if (cache && cache.date === today) {
      views = Number(cache.count)
      ids = cache.ids || []
    } else {
      // Nếu không có cache hôm nay, lấy từ backend hoặc mặc định 3
      views = Number(data.remainingPostViews ?? ((String(data.isPremium) === '1' || data.isAdmin) ? Infinity : 3))
    }

    user.value = {
      id:        data.accountID,
      accountID: data.accountID,
      name:      data.username,
      username:  data.username,
      email:     data.email,
      avatar:    data.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(data.username)}&background=EA580C&color=fff`,
      isAdmin:   data.isAdmin,
      isPremium: data.isPremium,
      token:     data.token || user.value?.token, // Giữ token cũ nếu refresh
      role:      data.isAdmin ? 'admin' : 'user',
      point:     data.point !== undefined ? data.point : (user.value?.point || 0),
      /* START: Daily View Limit Persistence Logic */
      remainingPostViews: views,
      lastViewDate: today,
      viewedPostIds: ids
      /* END: Daily View Limit Persistence Logic */
    }
    
    localStorage.setItem('user', JSON.stringify(user.value))
  }

async function login(email, password) {
    try {
      const data = await authService.login(email, password)
      setUser(data)
      return user.value.role
    } catch (err) {
      // 🔥 SỬA CHỖ NÀY: Ném nguyên object lỗi ra ngoài để AuthModal còn hứng được data
      throw err 
    }
  }

  async function register(username, email, password) {
    try {
      const data = await authService.register(username, email, password)
      setUser(data)
      return user.value
    } catch (err) {
      const msg = err.response?.data?.message || 'Registration failed'
      throw new Error(msg)
    }
  }

  async function refreshProfile() {
    if (!user.value?.token) return
    try {
      const data = await authService.getMe()
      setUser(data)
    } catch {
      logout()
    }
  }

  function logout() {
    user.value = null
    localStorage.removeItem('user')
    toast.info('You have been signed out.')
    router.push('/')
  }

  // ─── NEW: ACCOUNT LIFECYCLE ACTIONS ──────────────────────────

  async function deactivateAccount(otp, password = '') {
    const userId = user.value?.accountID || user.value?.id
    if (!userId) throw new Error('User not identified')
    
    try {
      await authService.deactivateAccount(userId, password, otp)
      logout()
      return true
    } catch (err) {
      throw err
    }
  }

  async function restoreAccount(email, otp, password = '') {
    try {
      const data = await authService.restoreAccount(email, password, otp)
      // Sau khi khôi phục thành công, backend đã trả về luôn Token + User
      // Chúng ta chỉ việc nạp vào state và vào thẳng trang chủ!
      setUser(data)
      return user.value.role
    } catch (err) {
      throw err
    }
  }

  // 🌟 NEW ACTION: Reset views for demo purposes
  function resetViews() {
    if (!user.value) return
    
    const now = new Date()
    const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
    
    // Reset trong state
    user.value.remainingPostViews = (user.value.isPremium || user.value.isAdmin) ? Infinity : 3
    user.value.viewedPostIds = []
    user.value.lastViewDate = today
    
    // Cập nhật localStorage 'user'
    localStorage.setItem('user', JSON.stringify(user.value))
    
    // Cập nhật Persistent Cache
    const limitsCache = JSON.parse(localStorage.getItem('gomet_view_limits') || '{}')
    const uid = String(user.value.accountID || user.value.id)
    limitsCache[uid] = {
      count: user.value.remainingPostViews,
      ids: [],
      date: today
    }
    localStorage.setItem('gomet_view_limits', JSON.stringify(limitsCache))
    
    toast.success('Đã reset lượt xem (Demo Mode)')
  }

  return { 
    user, isAuthenticated, isAdmin, currentUser, 
    login, register, logout, refreshProfile, setUser, resetViews,
    deactivateAccount, restoreAccount 
  }
})