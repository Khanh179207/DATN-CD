import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import * as authService from '@/services/authService'
import { toast } from '@/composables/useToast'
import { saveTokens, clearTokens, getStoredTokens } from '@/services/api'
import { getDeviceContext } from '@/services/deviceContext'

// Singleton Google script loader
let googleScriptLoaded = false
const PENDING_MFA_KEY = 'pendingMfaToken'
function loadGoogleScript () {
  if (googleScriptLoaded || document.getElementById('google-gsi-script')) {
    googleScriptLoaded = true
    return Promise.resolve()
  }
  return new Promise((resolve, reject) => {
    const s = document.createElement('script')
    s.id = 'google-gsi-script'
    s.src = 'https://accounts.google.com/gsi/client'
    s.async = true
    s.defer = true
    s.onload = () => { googleScriptLoaded = true; resolve() }
    s.onerror = reject
    document.head.appendChild(s)
  })
}

export const useAuthStore = defineStore('auth', () => {
  const router = useRouter()

  // ─── STATE ──────────────────────────────────────────────────────────────
  const user = ref(JSON.parse(localStorage.getItem('user')) || null)

  /**
   * Holds a pending MFA session token while the user is filling in the TOTP
   * challenge.  Cleared after verification completes.
   */
  const pendingMfaToken = ref(sessionStorage.getItem(PENDING_MFA_KEY) || null)

  // Cross-tab sync
  window.addEventListener('storage', (e) => {
    if (e.key === 'user') {
      user.value = e.newValue ? JSON.parse(e.newValue) : null
      if (!user.value) router.push('/').catch(() => {})
    }
  })

  // Force-logout fired by api.js interceptor on failed token refresh
  window.addEventListener('auth:force-logout', () => {
    _clearAll({ keepPendingMfa: true })
    router.push('/').catch(() => {})
  })

  // ─── GETTERS ────────────────────────────────────────────────────────────
  const isAuthenticated = computed(() => !!user.value)
  const isAdmin         = computed(() => user.value?.isAdmin === true || user.value?.role === 'admin')
  const currentUser     = computed(() => user.value)
  const isMfaPending    = computed(() => !!pendingMfaToken.value)

  // ─── HELPERS ────────────────────────────────────────────────────────────
  function _persistUser (data) {
    user.value = {
      id:           data.accountID,
      accountID:    data.accountID,
      name:         data.username,
      username:     data.username,
      email:        data.email,
      avatar:       data.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(data.username)}&background=EA580C&color=fff`,
      isAdmin:      data.isAdmin,
      isPremium:    data.isPremium,
      /** legacy UUID token kept for backwards compat with old interceptor */
      token:        data.token,
      accessToken:  data.accessToken,
      /** Also persisted here so other tabs can find it as a fallback */
      refreshToken: data.refreshToken,
      role:         data.isAdmin ? 'admin' : 'user'
    }
    localStorage.setItem('user', JSON.stringify(user.value))

    if (data.accessToken || data.refreshToken) {
      saveTokens({ accessToken: data.accessToken, refreshToken: data.refreshToken })
    }
  }

  function _persistPendingMfaToken (token) {
    pendingMfaToken.value = token || null
    if (pendingMfaToken.value) {
      sessionStorage.setItem(PENDING_MFA_KEY, pendingMfaToken.value)
    } else {
      sessionStorage.removeItem(PENDING_MFA_KEY)
    }
  }

  function _clearAll ({ keepPendingMfa = false } = {}) {
    user.value = null
    if (!keepPendingMfa) {
      _persistPendingMfaToken(null)
    }
    localStorage.removeItem('user')
    clearTokens()
  }

  function setAuthFromResponse (data) {
    _persistUser(data)
  }

  function setPendingMfaToken (token) {
    _persistPendingMfaToken(token)
  }

  // ─── ACTIONS ────────────────────────────────────────────────────────────

  /**
   * Step 1 login.  Returns one of:
   *   { status: 'ok', role }                — Normal login complete
   *   { status: 'mfa', mfaSessionToken }    — TOTP challenge needed
   *   { status: 'device' }                  — Magic link sent to email
   */
  async function login (email, password, deviceId, deviceName) {
    try {
      const ctx = getDeviceContext()
      const data = await authService.login(
        email,
        password,
        deviceId || ctx.deviceId,
        deviceName || ctx.deviceName
      )

      if (data.requiresMfa) {
        const mfaSessionToken = data.mfaSessionToken || data.mfaToken || data.sessionToken || null
        if (!mfaSessionToken) {
          throw new Error('MFA session could not be started. Please sign in again.')
        }
        setPendingMfaToken(mfaSessionToken)
        return { status: 'mfa', mfaSessionToken }
      }

      if (data.requiresDeviceVerification) {
        return { status: 'device' }
      }

      _persistUser(data)
      return { status: 'ok', role: user.value.role }
    } catch (err) {
      const msg = err.response?.data?.message || 'Incorrect email or password'
      throw new Error(msg)
    }
  }

  /**
   * Step 2 login — TOTP verification.
   * Uses the mfaSessionToken stored during successful login.
   */
  async function verifyMfa (totpCode, backupCode, deviceId, deviceName) {
    if (!pendingMfaToken.value) throw new Error('No pending MFA session')
    try {
      const data = await authService.verifyMfa({
        mfaSessionToken: pendingMfaToken.value,
        totpCode,
        backupCode,
        deviceId,
        deviceName
      })
      setPendingMfaToken(null)
      _persistUser(data)
      return { status: 'ok', role: user.value.role }
    } catch (err) {
      const payload = err.response?.data || {}
      const msg = payload.message || 'Invalid authentication code'
      const e = new Error(msg)
      if (typeof payload.remainingAttempts === 'number') {
        e.remainingAttempts = payload.remainingAttempts
      }
      if (payload.code) {
        e.code = payload.code
      }
      throw e
    }
  }

  async function register (username, email, password) {
    try {
      const data = await authService.register(username, email, password)
      _persistUser(data)
      return user.value
    } catch (err) {
      const msg = err.response?.data?.message || 'Registration failed'
      throw new Error(msg)
    }
  }

  /** Google Sign-In using Google Identity Services (button popup overlay). */
  async function loginGoogle (deviceId) {
    const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID
    if (!GOOGLE_CLIENT_ID) {
      toast.error('Google login is not configured. Please contact the administrator.')
      throw new Error('GOOGLE_CLIENT_ID not set')
    }
    await loadGoogleScript()
    return new Promise((resolve, reject) => {
      // Build a full-screen overlay with an official Google Sign-In button.
      // This approach works reliably cross-browser, even when One Tap is blocked
      // by third-party cookie restrictions in Chrome 3PCS / FedCM mode.
      const overlay = document.createElement('div')
      overlay.style.cssText = [
        'position:fixed', 'inset:0', 'z-index:99999',
        'display:flex', 'align-items:center', 'justify-content:center',
        'background:rgba(0,0,0,.6)', 'backdrop-filter:blur(4px)'
      ].join(';')

      const card = document.createElement('div')
      card.style.cssText = [
        'background:#1C1917', 'border-radius:16px', 'padding:32px 40px',
        'display:flex', 'flex-direction:column', 'align-items:center',
        'gap:20px', 'min-width:300px', 'box-shadow:0 8px 40px rgba(0,0,0,.5)'
      ].join(';')

      const title = document.createElement('p')
      title.textContent = 'Đăng nhập với Google'
      title.style.cssText = 'color:#F5F5F4;font-size:16px;font-weight:600;margin:0;'

      const btnContainer = document.createElement('div')

      const cancelBtn = document.createElement('button')
      cancelBtn.textContent = 'Hủy'
      cancelBtn.style.cssText = [
        'margin-top:4px', 'background:none', 'border:1px solid #57534E',
        'color:#A8A29E', 'padding:8px 28px', 'border-radius:8px',
        'cursor:pointer', 'font-size:14px'
      ].join(';')

      card.appendChild(title)
      card.appendChild(btnContainer)
      card.appendChild(cancelBtn)
      overlay.appendChild(card)
      document.body.appendChild(overlay)

      const cleanup = () => {
        if (document.body.contains(overlay)) document.body.removeChild(overlay)
      }

      cancelBtn.addEventListener('click', () => {
        cleanup()
        reject(new Error('Google login cancelled'))
      })
      overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
          cleanup()
          reject(new Error('Google login cancelled'))
        }
      })

      /* global google */
      window.google.accounts.id.initialize({
        client_id: GOOGLE_CLIENT_ID,
        // use_fedcm_for_prompt: false keeps One Tap usable in browsers
        // that have not yet fully migrated to FedCM.
        use_fedcm_for_prompt: false,
        callback: async (response) => {
          cleanup()
          try {
            const ctx = getDeviceContext()
            const data = await authService.loginWithGoogle(response.credential, deviceId || ctx.deviceId)
            _persistUser(data)
            resolve({ status: 'ok', role: user.value.role })
          } catch (err) {
            reject(err)
          }
        },
        error_callback: (err) => {
          cleanup()
          reject(new Error(err.type || 'Google login failed'))
        }
      })

      // Render the official Google Sign-In button inside the card
      window.google.accounts.id.renderButton(btnContainer, {
        theme: 'filled_blue',
        size: 'large',
        text: 'continue_with',
        shape: 'rectangular',
        width: 280
      })

      // Also attempt One Tap; if unavailable the overlay button remains visible
      window.google.accounts.id.prompt()
    })
  }

  async function refreshProfile () {
    const { accessToken, legacyToken } = getStoredTokens()
    if (!accessToken && !legacyToken) return
    try {
      const data = await authService.getMe()
      if (user.value) {
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
      }
    } catch {
      logout()
    }
  }

  async function logout () {
    try {
      const { refreshToken } = getStoredTokens()
      if (refreshToken) {
        await authService.logout(refreshToken).catch(() => {}) // best-effort
      }
    } finally {
      _clearAll()
      toast.info('You have been signed out.')
      router.push('/')
    }
  }

  const clearPendingMfa = () => { setPendingMfaToken(null) }

  return {
    user,
    pendingMfaToken,
    isAuthenticated,
    isAdmin,
    currentUser,
    isMfaPending,
    login,
    verifyMfa,
    register,
    loginGoogle,
    logout,
    refreshProfile,
    clearPendingMfa,
    setPendingMfaToken,
    setAuthFromResponse,
    _clearAll
  }
})