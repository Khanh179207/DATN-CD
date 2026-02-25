import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import * as authService from '@/services/authService'
import { toast } from '@/composables/useToast'

export const useAuthStore = defineStore('auth', () => {
  const router = useRouter()

  // 1. STATE — persist to localStorage
  const user = ref(JSON.parse(localStorage.getItem('user')) || null)

  // Cross-tab sync: when another tab logs in/out, update this tab too
  window.addEventListener('storage', (e) => {
    if (e.key === 'user') {
      user.value = e.newValue ? JSON.parse(e.newValue) : null
      if (!user.value) {
        router.push('/').catch(() => {})
      }
    }
  })

  // Force-logout event fired by api.js interceptor on 401
  window.addEventListener('auth:force-logout', () => {
    user.value = null
    router.push('/').catch(() => {})
  })

  // 2. GETTERS
  const isAuthenticated = computed(() => !!user.value)
  const isAdmin = computed(() => user.value?.isAdmin === true || user.value?.role === 'admin')
  const currentUser = computed(() => user.value)

  // 3. ACTIONS
  async function login(email, password) {
    try {
      const data = await authService.login(email, password)
      user.value = {
        id:        data.accountID,
        accountID: data.accountID,
        name:      data.username,
        username:  data.username,
        email:     data.email,
        avatar:    data.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(data.username)}&background=EA580C&color=fff`,
        isAdmin:   data.isAdmin,
        isPremium: data.isPremium,
        token:     data.token,
        role:      data.isAdmin ? 'admin' : 'user'
      }
      localStorage.setItem('user', JSON.stringify(user.value))
      return user.value.role
    } catch (err) {
      const msg = err.response?.data?.message || 'Incorrect email or password'
      throw new Error(msg)
    }
  }

  async function register(username, email, password) {
    try {
      const data = await authService.register(username, email, password)
      user.value = {
        id:        data.accountID,
        accountID: data.accountID,
        name:      data.username,
        username:  data.username,
        email:     data.email,
        avatar:    data.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(data.username)}&background=EA580C&color=fff`,
        isAdmin:   data.isAdmin,
        isPremium: data.isPremium,
        token:     data.token,
        role:      'user'
      }
      localStorage.setItem('user', JSON.stringify(user.value))
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
      user.value = {
        ...user.value,
        name:      data.username,
        username:  data.username,
        email:     data.email,
        avatar:    data.avatar || user.value.avatar,
        isAdmin:   data.isAdmin,
        isPremium: data.isPremium
      }
      localStorage.setItem('user', JSON.stringify(user.value))
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

  return { user, isAuthenticated, isAdmin, currentUser, login, register, logout, refreshProfile }
})