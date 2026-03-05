import api from './api'

export const getWeeklyLeaderboard = ({ weekStart, weekEnd, top = 10 }) =>
  api.get('/api/admin/leaderboard/weekly', {
    params: { weekStart, weekEnd, top }
  }).then(r => r.data)

export const getWeeklyCertificates = ({ weekStart, weekEnd }) =>
  api.get('/api/admin/certificates', {
    params: { weekStart, weekEnd }
  }).then(r => r.data)

export const generateWeeklyCertificates = ({ weekStart, weekEnd, top = 3 }) =>
  api.post('/api/admin/certificates/generate', null, {
    params: { weekStart, weekEnd, top }
  }).then(r => r.data)
