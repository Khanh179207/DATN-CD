import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// ─── CLIENT IP (ipify — supports IPv4 + IPv6) ──────────────────────────────
let _clientIp = null
let _ipFetchPromise = null

/** Fetch client's public IP once (cached). Supports IPv6 via api64.ipify.org. */
export async function getClientIp () {
  if (_clientIp) return _clientIp
  if (_ipFetchPromise) return _ipFetchPromise
  _ipFetchPromise = axios.get('https://api64.ipify.org?format=json', { timeout: 4000 })
    .then(r => { _clientIp = r.data?.ip || null; return _clientIp })
    .catch(() => null)
  return _ipFetchPromise
}

// Prefetch IP on app start (fire-and-forget)
getClientIp()

// ─── TOKEN HELPERS ─────────────────────────────────────────────────────────
export function getStoredTokens () {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  return {
    accessToken:  localStorage.getItem('accessToken')  || user?.accessToken,
    // Fallback: refreshToken may be stored in the user object if the dedicated
    // key was missing (e.g. cross-tab race, manual clear, or legacy session).
    refreshToken: localStorage.getItem('refreshToken') || user?.refreshToken,
    legacyToken:  user?.token
  }
}

export function saveTokens ({ accessToken, refreshToken }) {
  if (accessToken)  localStorage.setItem('accessToken',  accessToken)
  if (refreshToken) localStorage.setItem('refreshToken', refreshToken)
}

export function clearTokens () {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
}

// ─── REQUEST INTERCEPTOR ───────────────────────────────────────────────────
api.interceptors.request.use(config => {
  const { accessToken, refreshToken, legacyToken } = getStoredTokens()
  // If refresh token is missing but legacy token exists, prefer legacy token.
  // This prevents getting stuck with an expired access token that cannot refresh.
  const bearer = (!refreshToken && legacyToken)
    ? legacyToken
    : (accessToken || legacyToken)
  if (bearer) {
    config.headers.Authorization = `Bearer ${bearer}`
  }
  // Attach client IP so BE can record real public IP (incl. IPv6)
  if (_clientIp) {
    config.headers['X-Client-IP'] = _clientIp
  }
  return config
})

// ─── RESPONSE INTERCEPTOR (silent refresh) ────────────────────────────────
let isRefreshing = false
let refreshQueue = []   // requests waiting while refresh is in progress

function processQueue (error, token = null) {
  refreshQueue.forEach(cb => error ? cb.reject(error) : cb.resolve(token))
  refreshQueue = []
}

function hasAuthState () {
  return !!(
    localStorage.getItem('accessToken') ||
    localStorage.getItem('refreshToken') ||
    localStorage.getItem('user')
  )
}

api.interceptors.response.use(
  res => res,
  async err => {
    const original = err.config

    // Maintenance mode signal from backend
    if (err.response?.status === 503 && err.response?.data?.code === 'MAINTENANCE') {
      window.dispatchEvent(new CustomEvent('system:maintenance-on', {
        detail: { message: err.response?.data?.message || '' }
      }))
      return Promise.reject(err)
    }

    if (err.response?.status === 503 && err.response?.data?.code === 'MAINTENANCE_MODULE') {
      window.dispatchEvent(new CustomEvent('system:maintenance-module-on', {
        detail: {
          module: err.response?.data?.module || '',
          message: err.response?.data?.message || ''
        }
      }))
      return Promise.reject(err)
    }

    // If 401 and we haven't already tried to refresh for this request
    if (err.response?.status === 401 && !original._retried) {
      const { refreshToken, legacyToken, accessToken } = getStoredTokens()

      // Compatibility fallback: if request used access token, but refresh token
      // is missing and a legacy token exists, retry once using legacy token.
      if (!refreshToken && legacyToken && accessToken && !original._retriedLegacy) {
        original._retriedLegacy = true
        original.headers = original.headers || {}
        original.headers.Authorization = `Bearer ${legacyToken}`
        return api(original)
      }

      if (!refreshToken) {
        const hadAuthState = hasAuthState()
        clearTokens()
        localStorage.removeItem('user')
        if (hadAuthState) {
          window.dispatchEvent(new CustomEvent('auth:force-logout'))
        }
        return Promise.reject(err)
      }

      if (isRefreshing) {
        // Queue subsequent requests until refresh completes
        return new Promise((resolve, reject) => {
          refreshQueue.push({
            resolve: token => { original.headers.Authorization = `Bearer ${token}`; resolve(api(original)) },
            reject
          })
        })
      }

      original._retried = true
      isRefreshing = true

      try {
        const deviceId = localStorage.getItem('deviceId') || undefined
        const response = await axios.post(
          `${api.defaults.baseURL}/api/auth/refresh`,
          { refreshToken, deviceId },
          { headers: { 'Content-Type': 'application/json' } }
        )
        const { accessToken: newAccess, refreshToken: newRefresh } = response.data

        // Guard: if the refresh endpoint didn't return a token, treat as failure
        if (!newAccess) {
          throw new Error('Refresh response missing accessToken')
        }

        saveTokens({ accessToken: newAccess, refreshToken: newRefresh })

        // Keep the user object in localStorage in sync so other tabs
        // can find the refreshToken via the fallback in getStoredTokens().
        const user = JSON.parse(localStorage.getItem('user') || 'null')
        if (user) {
          user.accessToken = newAccess
          if (newRefresh) user.refreshToken = newRefresh
          localStorage.setItem('user', JSON.stringify(user))
        }

        processQueue(null, newAccess)
        original.headers.Authorization = `Bearer ${newAccess}`
        return api(original)
      } catch (refreshErr) {
        const hadAuthState = hasAuthState()
        const status = refreshErr?.response?.status
        const shouldForceLogout = status === 401 || status === 403
        processQueue(refreshErr, null)
        if (shouldForceLogout) {
          clearTokens()
          localStorage.removeItem('user')
          if (hadAuthState) {
            window.dispatchEvent(new CustomEvent('auth:force-logout'))
          }
        }
        return Promise.reject(refreshErr)
      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(err)
  }
)

export default api
