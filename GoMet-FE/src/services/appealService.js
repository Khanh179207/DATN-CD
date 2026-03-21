import api from './api'

/**
 * Create a new appeal for banned account
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
 * Get appeal by ID
 * @param {number} appealID
 * @returns {Promise<Object>}
 */
export const getAppealById = async (appealID) => {
  const response = await api.get(`/api/appeals/${appealID}`)
  return response.data
}

/**
 * Get all appeals (admin only)
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
    const response = await api.get('/api/admin/appeals', { params })
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
 * Update appeal status (admin only)
 * @param {number} appealID
 * @param {Object} updateData { status, note }
 * @returns {Promise<Object>}
 */
export const updateAppeal = async (appealID, updateData) => {
  const response = await api.put(`/api/admin/appeals/${appealID}`, updateData)
  return response.data
}

/**
 * Unban account by appeal (admin only)
 * @param {number} appealID
 * @returns {Promise<Object>}
 */
export const unbanAccountByAppeal = async (appealID) => {
  const response = await api.post(`/api/admin/appeals/${appealID}/unban`, {})
  return response.data
}

/**
 * Get appeal tracking status (user-facing)
 * @param {string} appealEmail
 * @returns {Promise<Object>}
 */
export const getAppealStatus = async (appealEmail) => {
  const response = await api.get('/api/appeals/status', {
    params: { email: appealEmail }
  })
  return response.data
}
