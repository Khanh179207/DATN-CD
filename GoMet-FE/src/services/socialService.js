import api from './api'

// lấy danh sách bài đã lưu
export const getFavorites = (accountID) =>
  api.get(`/api/favorites/${accountID}`)
     .then(res => res.data)


// lưu bài viết
export const addFavorite = (accountID, postID) =>
  api.post('/api/favorites/add', {
    accountID,
    postID
  }).then(res => res.data)


// bỏ lưu bài viết
export const removeFavorite = (accountID, postID) =>
  api.delete('/api/favorites/remove', {
    params: { accountID, postID }
  }).then(res => res.data)


// kiểm tra đã lưu chưa
export const checkFavorite = (accountID, postID) =>
  api.get('/api/favorites/check', {
    params: { accountID, postID }
  }).then(res => res.data?.isFavorite ?? false)


export const checkFollow = (followerID, followeeID) =>
  api.get('/api/follows/check', { params: { followerID, followeeID } })
     .then(res => res.data?.isFollowing ?? false)

export const follow = (followerID, followeeID) =>
  api.post('/api/follows', null, { params: { followerID, followeeID } })
     .then(res => res.data)

export const unfollow = (followerID, followeeID) =>
  api.delete('/api/follows', { params: { followerID, followeeID } })
     .then(res => res.data)

export const getMyFollows = (followerID) =>
  api.get('/api/follows/my-follows', { params: { followerID } })
     .then(res => res.data)