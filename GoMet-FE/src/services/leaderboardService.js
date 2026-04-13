import api from './api'

export const getLeaderboardPosts = (limit = 10) =>
  api.get('/api/leaderboard/posts', { params: { limit } }).then(r => r.data)

export const getLeaderboardUsers = (limit = 10) =>
  api.get('/api/leaderboard/users', { params: { limit } }).then(r => r.data)
