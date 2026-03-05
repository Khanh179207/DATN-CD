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

function isAdminUser () {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
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
  } catch {
    // ignore transient network errors
  }
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
  syncMaintenanceAndRedirect(true)
  maintenanceTimer = setInterval(() => {
    syncMaintenanceAndRedirect(true)
  }, 5000)

  window.addEventListener('system:maintenance-on', onMaintenanceSignal)
  window.addEventListener('system:maintenance-module-on', onModuleMaintenanceSignal)
  window.addEventListener('system:maintenance-updated', onMaintenanceSignal)
  window.addEventListener('storage', onStorageChange)
})

onUnmounted(() => {
  if (maintenanceTimer) {
    clearInterval(maintenanceTimer)
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