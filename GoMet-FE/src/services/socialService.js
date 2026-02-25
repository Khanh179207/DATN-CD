import api from './api'

export const getFavorites = (accountID) =>
  api.get(`/api/favorites/${accountID}`).then(r => r.data)

export const addFavorite = (accountID, postID) =>
  api.post('/api/favorites', null, { params: { accountID, postID } }).then(r => r.data)

export const removeFavorite = (accountID, postID) =>
  api.delete('/api/favorites', { params: { accountID, postID } })

export const checkFavorite = (accountID, postID) =>
  api.get('/api/favorites/check', { params: { accountID, postID } }).then(r => r.data)

export const checkFollow = (followerID, followeeID) =>
  api.get('/api/follows/check', { params: { followerID, followeeID } }).then(r => r.data)

export const follow = (followerID, followeeID) =>
  api.post('/api/follows', null, { params: { followerID, followeeID } })

export const unfollow = (followerID, followeeID) =>
  api.delete('/api/follows', { params: { followerID, followeeID } })
