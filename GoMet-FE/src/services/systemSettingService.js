import api from './api'

export const getPublicSystemSettings = () =>
  api.get('/api/system/settings/public').then(r => r.data)

export const getAdminSystemSettings = () =>
  api.get('/api/admin/system/settings').then(r => r.data)

export const updateMaintenanceSettings = ({ enabled, message, modules = [] }) =>
  api.put('/api/admin/system/settings/maintenance', { enabled, message, modules }).then(r => r.data)
