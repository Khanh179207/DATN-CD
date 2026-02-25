import api from './api'

// ── Achievement catalogue & user achievements ─────────────────────────────

/** Get the full achievements catalogue (all possible achievements). */
export const getAllAchievements = () =>
  api.get('/api/achievements').then(r => r.data)

/** Get achievements earned by a specific user. */
export const getUserAchievements = (accountID) =>
  api.get(`/api/achievements/user/${accountID}`).then(r => r.data)
