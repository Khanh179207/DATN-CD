import api from './api'

// ================= FAVORITES (LƯU BÀI VIẾT) =================

// Lấy danh sách bài đã lưu
export const getFavorites = async (accountID) => {
  const res = await api.get(`/api/favorites/${accountID}`)
  return res.data
}

// Lưu bài viết
export const addFavorite = async (accountID, postID) => {
  const res = await api.post('/api/favorites/add', { accountID, postID })
  return res.data
}

// Bỏ lưu bài viết (Dùng params cho method DELETE là hợp lý)
export const removeFavorite = async (accountID, postID) => {
  const res = await api.delete('/api/favorites/remove', { 
    params: { accountID, postID } 
  })
  return res.data
}

// Kiểm tra đã lưu chưa
export const checkFavorite = async (accountID, postID) => {
  const res = await api.get('/api/favorites/check', { 
    params: { accountID, postID } 
  })
  return res.data?.isFavorite ?? false
}


// ================= FOLLOWS (THEO DÕI USER) =================

// Kiểm tra đã follow chưa (Lấy logic trả về isFollowing của bạn sếp)
export const checkFollow = async (followerID, followeeID) => {
  const res = await api.get('/api/follows/check', { 
    params: { followerID, followeeID } 
  })
  return res.data?.isFollowing ?? false
}

// Follow (Sử dụng params để khớp với Backend, tránh lỗi 400 Bad Request)
export const follow = async (followerID, followeeID) => {
  const res = await api.post('/api/follows', null, { 
    params: { followerID, followeeID } 
  })
  return res.data
}

// Unfollow (DELETE thường dùng params)
export const unfollow = async (followerID, followeeID) => {
  const res = await api.delete('/api/follows', { 
    params: { followerID, followeeID } 
  })
  return res.data
}

// Lấy danh sách đang follow (Tính năng mới của bạn sếp, viết lại bằng async/await cho đồng bộ)
export const getMyFollows = async (followerID) => {
  const res = await api.get('/api/follows/my-follows', { 
    params: { followerID } 
  })
  return res.data
}