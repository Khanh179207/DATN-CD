<template>
  <div id="app">
    <router-view />
    <ToastContainer />
  </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import ToastContainer from '@/components/common/ToastContainer.vue'
import { useSystemSettingsStore } from '@/stores/systemSettings'

const router = useRouter()
const systemSettingsStore = useSystemSettingsStore()

let maintenanceTimer = null
let maintenanceFailureCount = 0

function isAdminUser () {
  let user = null
  try {
    user = JSON.parse(localStorage.getItem('user') || 'null')
  } catch {
    user = null
  }
  return !!(user && (user.isAdmin === true || user.isAdmin === 1 || user.role === 'admin'))
}

async function syncMaintenanceAndRedirect (force = false) {
  try {
    await systemSettingsStore.fetchPublicSettings(force)
    const maintenanceOn = !!systemSettingsStore.maintenanceMode
    const isAdmin = isAdminUser()
    const currentPath = router.currentRoute.value.path

    if (maintenanceOn && !isAdmin && currentPath !== '/maintenance') {
      router.replace('/maintenance').catch(() => {})
      return
    }

    if (!maintenanceOn && currentPath === '/maintenance') {
      const user = JSON.parse(localStorage.getItem('user') || 'null')
      router.replace(user ? '/home' : '/').catch(() => {})
    }
    return true
  } catch {
    return false
  }
}

function scheduleMaintenanceSync (delayMs) {
  if (maintenanceTimer) {
    clearTimeout(maintenanceTimer)
  }
  maintenanceTimer = setTimeout(async () => {
    const ok = await syncMaintenanceAndRedirect(true)
    maintenanceFailureCount = ok ? 0 : Math.min(maintenanceFailureCount + 1, 3)
    const nextDelay = ok ? 30000 : 30000 * (maintenanceFailureCount + 1)
    scheduleMaintenanceSync(nextDelay)
  }, delayMs)
}

function onMaintenanceSignal () {
  syncMaintenanceAndRedirect(true)
}

function onModuleMaintenanceSignal () {
  systemSettingsStore.fetchPublicSettings(true).catch(() => {})
}

function onStorageChange (e) {
  if (e.key === 'system:maintenance-updated-at') {
    syncMaintenanceAndRedirect(true)
  }
}

onMounted(() => {
  syncMaintenanceAndRedirect(true).then(ok => {
    maintenanceFailureCount = ok ? 0 : 1
    scheduleMaintenanceSync(ok ? 30000 : 60000)
  })

  window.addEventListener('system:maintenance-on', onMaintenanceSignal)
  window.addEventListener('system:maintenance-module-on', onModuleMaintenanceSignal)
  window.addEventListener('system:maintenance-updated', onMaintenanceSignal)
  window.addEventListener('storage', onStorageChange)
})

onUnmounted(() => {
  if (maintenanceTimer) {
    clearTimeout(maintenanceTimer)
    maintenanceTimer = null
  }
  window.removeEventListener('system:maintenance-on', onMaintenanceSignal)
  window.removeEventListener('system:maintenance-module-on', onModuleMaintenanceSignal)
  window.removeEventListener('system:maintenance-updated', onMaintenanceSignal)
  window.removeEventListener('storage', onStorageChange)
})
</script>

<style>
/* Global styles are now handled by:
   - assets/styles/design-tokens.css (CSS custom properties)
   - assets/styles/base.css (resets, scrollbar, utilities)
   Both imported in main.js
*/
</style>