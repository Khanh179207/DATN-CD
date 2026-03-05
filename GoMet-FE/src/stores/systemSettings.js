import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getPublicSystemSettings,
  getAdminSystemSettings,
  updateMaintenanceSettings
} from '@/services/systemSettingService'

export const useSystemSettingsStore = defineStore('systemSettings', () => {
  const maintenanceMode = ref(false)
  const message = ref('Hệ thống đang bảo trì. Vui lòng quay lại sau.')
  const modules = ref([])
  const loading = ref(false)
  const loadedAt = ref(0)
  const cacheTtlMs = 30 * 1000

  async function fetchPublicSettings (force = false) {
    const now = Date.now()
    if (!force && loadedAt.value && (now - loadedAt.value) < cacheTtlMs) {
      return { maintenanceMode: maintenanceMode.value, message: message.value }
    }

    const data = await getPublicSystemSettings()
    maintenanceMode.value = !!data.maintenanceMode
    message.value = data.message || message.value
    modules.value = Array.isArray(data.modules) ? data.modules : modules.value
    loadedAt.value = now
    return data
  }

  async function fetchAdminSettings () {
    loading.value = true
    try {
      const data = await getAdminSystemSettings()
      maintenanceMode.value = !!data.maintenanceMode
      message.value = data.message || message.value
      modules.value = Array.isArray(data.modules) ? data.modules : []
      loadedAt.value = Date.now()
      return data
    } finally {
      loading.value = false
    }
  }

  async function saveMaintenanceSettings ({ enabled, message: newMessage, modules: newModules = [] }) {
    loading.value = true
    try {
      const data = await updateMaintenanceSettings({ enabled, message: newMessage, modules: newModules })
      maintenanceMode.value = !!data.maintenanceMode
      message.value = data.message || message.value
      modules.value = Array.isArray(data.modules) ? data.modules : modules.value
      loadedAt.value = Date.now()
      window.dispatchEvent(new CustomEvent('system:maintenance-updated', {
        detail: { maintenanceMode: maintenanceMode.value, message: message.value, modules: modules.value }
      }))
      localStorage.setItem('system:maintenance-updated-at', String(Date.now()))
      return data
    } finally {
      loading.value = false
    }
  }

  return {
    maintenanceMode,
    message,
    modules,
    loading,
    fetchPublicSettings,
    fetchAdminSettings,
    saveMaintenanceSettings
  }
})
