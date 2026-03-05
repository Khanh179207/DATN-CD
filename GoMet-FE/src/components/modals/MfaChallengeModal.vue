<template>
  <div class="mfa-overlay">
    <div class="mfa-card">
      <!-- Header -->
      <div class="mfa-header">
        <div class="mfa-icon-wrap">
          <svg xmlns="http://www.w3.org/2000/svg" class="mfa-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
          </svg>
        </div>
        <h2 class="mfa-title">Two-Factor Authentication</h2>
        <p class="mfa-subtitle">
          Enter the 6-digit code from your authenticator app.
        </p>
      </div>

      <!-- Error -->
      <div v-if="error" class="mfa-error">
        <p>{{ error }}</p>
        <p v-if="remainingAttempts !== null" class="mfa-error-meta">
          Remaining attempts: {{ remainingAttempts }}
        </p>
      </div>

      <!-- TOTP input -->
      <div v-if="!useBackup" class="mfa-section">
        <div>
          <input
            v-model="totpCode"
            type="text"
            inputmode="numeric"
            pattern="\d{6}"
            maxlength="6"
            placeholder="000000"
            class="mfa-code-input"
            :disabled="loading"
            @keyup.enter="submit"
            autofocus
          />
        </div>
        <button
          @click="submit"
          :disabled="loading || totpCode.length !== 6"
          class="mfa-primary-btn"
        >
          <span v-if="loading" class="mfa-loading-wrap">
            <svg class="mfa-spinner" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
              <path fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
            </svg>
            Verifying…
          </span>
          <span v-else>Verify Code</span>
        </button>
        <button
          @click="useBackup = true"
          class="mfa-link-btn"
        >
          Use a backup code instead
        </button>
      </div>

      <!-- Backup code input -->
      <div v-else class="mfa-section">
        <div>
          <label class="mfa-label">Backup Code</label>
          <input
            v-model="backupCode"
            type="text"
            placeholder="Enter your backup code"
            class="mfa-backup-input"
            :disabled="loading"
            @keyup.enter="submit"
          />
        </div>
        <button
          @click="submit"
          :disabled="loading || !backupCode.trim()"
          class="mfa-primary-btn"
        >
          <span v-if="loading">Verifying…</span>
          <span v-else>Verify Backup Code</span>
        </button>
        <button
          @click="useBackup = false"
          class="mfa-link-btn"
        >
          Use authenticator app instead
        </button>
      </div>

      <!-- Trust device -->
      <div class="mfa-trust-row">
        <input id="trustDevice" type="checkbox" v-model="trustDevice"
               class="mfa-checkbox" />
        <label for="trustDevice" class="mfa-trust-label">Trust this device for 30 days</label>
      </div>

      <!-- Cancel -->
      <button @click="$emit('cancel')"
              class="mfa-close-btn"
              aria-label="Close MFA modal">
        <svg xmlns="http://www.w3.org/2000/svg" class="mfa-close-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getDeviceContext } from '@/services/deviceContext'

const emit = defineEmits(['success', 'cancel'])
const auth = useAuthStore()

const totpCode   = ref('')
const backupCode = ref('')
const useBackup  = ref(false)
const trustDevice = ref(false)
const loading    = ref(false)
const error      = ref('')
const remainingAttempts = ref(null)

const { deviceId, deviceName } = getDeviceContext()

function normalizeTotp (value) {
  return (value || '').replace(/\D/g, '').slice(0, 6)
}

function normalizeBackupCode (value) {
  return (value || '').trim().replace(/\s+/g, '').toUpperCase()
}

async function submit () {
  error.value = ''
  remainingAttempts.value = null
  loading.value = true
  try {
    const cleanTotp = normalizeTotp(totpCode.value)
    const cleanBackup = normalizeBackupCode(backupCode.value)
    const result = await auth.verifyMfa(
      useBackup.value ? undefined : cleanTotp,
      useBackup.value ? cleanBackup : undefined,
      trustDevice.value ? deviceId : undefined,
      trustDevice.value ? deviceName : undefined
    )
    emit('success', result)
  } catch (e) {
    if (e.code === 'MFA_SESSION_INVALID' || e.code === 'MFA_LOCKED') {
      error.value = 'MFA session expired. Please sign in again.'
    } else {
      error.value = e.message || 'Invalid code. Please try again.'
    }
    if (typeof e.remainingAttempts === 'number') {
      remainingAttempts.value = e.remainingAttempts
    }
    totpCode.value = ''
  } finally {
    loading.value = false
  }
}

watch(totpCode, (val) => {
  const clean = normalizeTotp(val)
  if (clean !== val) {
    totpCode.value = clean
  }
})

watch(backupCode, (val) => {
  const clean = normalizeBackupCode(val)
  if (clean !== val) {
    backupCode.value = clean
  }
})
</script>

<style scoped>
.mfa-overlay {
  position: fixed;
  inset: 0;
  z-index: 10050;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  padding: 16px;
}

.mfa-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.35);
  padding: 28px;
  position: relative;
}

.mfa-header { text-align: center; margin-bottom: 20px; }
.mfa-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 999px;
  margin: 0 auto 12px;
  display: grid;
  place-items: center;
  background: #fff7ed;
}
.mfa-icon { width: 28px; height: 28px; color: #f97316; }
.mfa-title { margin: 0; font-size: 1.25rem; color: #1f2937; font-weight: 700; }
.mfa-subtitle { margin: 6px 0 0; color: #6b7280; font-size: .92rem; }

.mfa-error {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #b91c1c;
  border-radius: 10px;
  padding: 10px 12px;
  font-size: .88rem;
  margin-bottom: 14px;
}
.mfa-error-meta { margin-top: 4px; font-size: .78rem; font-weight: 600; }

.mfa-section { display: grid; gap: 12px; }
.mfa-code-input,
.mfa-backup-input {
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 12px;
  padding: 14px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
}
.mfa-code-input {
  text-align: center;
  font-size: 2rem;
  letter-spacing: .45em;
}
.mfa-code-input:focus,
.mfa-backup-input:focus {
  outline: none;
  border-color: #fb923c;
  box-shadow: 0 0 0 2px rgba(251, 146, 60, .25);
}

.mfa-primary-btn {
  width: 100%;
  border: none;
  border-radius: 12px;
  padding: 12px;
  color: #fff;
  font-weight: 600;
  background: #f97316;
  cursor: pointer;
}
.mfa-primary-btn:hover { background: #ea580c; }
.mfa-primary-btn:disabled {
  background: #d1d5db;
  cursor: not-allowed;
}

.mfa-loading-wrap {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.mfa-spinner {
  width: 16px;
  height: 16px;
  animation: spin 1s linear infinite;
}
.mfa-spinner circle { opacity: .25; }
.mfa-spinner path { opacity: .75; }

.mfa-link-btn {
  border: none;
  background: transparent;
  color: #6b7280;
  font-size: .88rem;
  cursor: pointer;
}
.mfa-link-btn:hover { color: #f97316; }

.mfa-label {
  display: block;
  margin-bottom: 6px;
  font-size: .88rem;
  font-weight: 600;
  color: #374151;
}

.mfa-trust-row {
  margin-top: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.mfa-checkbox { width: 16px; height: 16px; cursor: pointer; }
.mfa-trust-label { font-size: .88rem; color: #4b5563; }

.mfa-close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  border: none;
  background: transparent;
  color: #9ca3af;
  cursor: pointer;
}
.mfa-close-btn:hover { color: #4b5563; }
.mfa-close-icon { width: 20px; height: 20px; }

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
