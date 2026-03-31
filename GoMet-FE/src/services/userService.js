import api from './api'

export const getUserProfile = (id) =>
  api.get(`/api/users/${id}`).then(r => r.data)

export const updateUserProfile = (id, data) =>
  api.put(`/api/users/${id}`, data).then(r => r.data)

export const getUserStats = (id) =>
  api.get(`/api/users/${id}/stats`).then(r => r.data)

export const getLeaderboardChefs = (timeframe = 'month', limit = 10) =>
  api.get(`/api/users/leaderboard?timeframe=${timeframe}&limit=${limit}`).then(r => r.data)
