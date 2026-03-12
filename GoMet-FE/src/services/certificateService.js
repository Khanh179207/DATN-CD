import api from './api'

export const getMyCertificates = (year) =>
  api.get('/api/certificates/me', { params: year ? { year } : {} }).then(r => r.data)
