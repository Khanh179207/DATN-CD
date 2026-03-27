import api from './api'
import { useAuthStore } from '@/stores/auth' // 🔥 IMPORT STORE LẤY TOKEN

// 🔥 HELPER: Tự động trích xuất Token và gắn vào Header
const getAuthConfig = () => {
  const authStore = useAuthStore()
  return {
    headers: {
      'Authorization': `Bearer ${authStore.token}`
    }
  }
}

/**
 * Create a new appeal for banned account (PUBLIC)
 * @param {Object} appealData { email, reason }
 * @returns {Promise<Object>}
 */
export const createAppeal = async (appealData) => {
  console.log('[AppealService] Creating appeal...', { 
    endpoint: '/api/appeals',
    payload: appealData,
    timestamp: new Date().toISOString()
  })
  
  try {
    const response = await api.post('/api/appeals', appealData)
    console.log('[AppealService] Appeal created successfully:', {
      status: response.status,
      data: response.data
    })
    return response.data
  } catch (error) {
    console.error('[AppealService] Failed to create appeal:', {
      status: error.response?.status,
      message: error.response?.data?.message,
      endpoint: error.config?.url,
      fullError: error
    })
    throw error
  }
}

/**
 * Get appeal by ID (ADMIN ONLY)
 * @param {number} appealID
 * @returns {Promise<Object>}
 */
export const getAppealById = async (appealID) => {
  // 🔥 Đã thêm /admin vào URL cho khớp BE và gắn Token
  const response = await api.get(`/api/admin/appeals/${appealID}`, getAuthConfig())
  return response.data
}

/**
 * Get all appeals (ADMIN ONLY)
 * @param {Object} params { page, limit, status, email }
 * @returns {Promise<Object>}
 */
export const getAppeals = async (params = {}) => {
  console.log('[AppealService] Fetching all appeals...', {
    endpoint: '/api/admin/appeals',
    params,
    timestamp: new Date().toISOString()
  })

  try {
    // 🔥 Gộp params và Token header vào chung 1 object config
    const config = {
      params,
      ...getAuthConfig()
    }
    const response = await api.get('/api/admin/appeals', config)
    
    console.log('[AppealService] Appeals fetched successfully:', {
      status: response.status,
      count: response.data?.length || response.data?.appeals?.length || 0,
      data: response.data
    })
    return response.data
  } catch (error) {
    console.error('[AppealService] Failed to fetch appeals:', {
      status: error.response?.status,
      message: error.response?.data?.message,
      endpoint: error.config?.url,
      fullError: error
    })
    throw error
  }
}

/**
 * Update appeal status (ADMIN ONLY)
 * @param {number} appealID
 * @param {Object} updateData { status, note }
 * @returns {Promise<Object>}
 */
export const updateAppeal = async (appealID, updateData) => {
  // 🔥 Gắn Token vào tham số thứ 3
  const response = await api.put(`/api/admin/appeals/${appealID}`, updateData, getAuthConfig())
  return response.data
}

/**
 * Unban account by appeal (ADMIN ONLY)
 * @param {number} appealID
 * @returns {Promise<Object>}
 */
export const unbanAccountByAppeal = async (appealID) => {
  // 🔥 Gắn Token vào tham số thứ 3 (tham số thứ 2 là body rỗng {})
  const response = await api.post(`/api/admin/appeals/${appealID}/unban`, {}, getAuthConfig())
  return response.data
}

/**
 * Get appeal tracking status (PUBLIC - USER FACING)
 * @param {string} appealEmail
 * @returns {Promise<Object>}
 */
export const getAppealStatus = async (appealEmail) => {
  const response = await api.get('/api/appeals/status', {
    params: { email: appealEmail }
  })
  return response.data
}