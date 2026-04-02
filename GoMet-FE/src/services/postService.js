import api from './api'

/**
 * @param {number} limit
 * @returns {Promise<Array>} PublicPostDTO[]
 */
export const getLatestPosts = (limit = 8) =>
  api.get('/api/posts/latest', { params: { limit } }).then(r => r.data)

/**
 * @param {number} page 1-based
 * @param {number} size
 */
export const getPosts = (page = 1, size = 12) =>
  api.get('/api/posts', { params: { page, size } }).then(r => r.data)

/**
 * @param {number|string} id
 * @returns {Promise<Object>} PostDetailDTO
 */
export const getPostById = (id) =>
  api.get(`/api/posts/${id}`).then(r => r.data)

// 🔥 CODE MỚI THÊM VÀO: Hàm ghi nhận lượt xem
/**
 * Ghi nhận 1 lượt xem (View) cho bài viết
 * @param {number|string} id
 */
export const recordPostView = (id) =>
  api.post(`/api/posts/${id}/view`).then(r => r.data)

/**
 * @param {string} keyword
 * @param {number|null} categoryID
 * @param {'newest'|'views'|'rating'} sort
 */
export const searchPosts = (keyword = '', categoryID = null, sort = 'newest') =>
  api.get('/api/posts/search', { params: { keyword, categoryID: categoryID || undefined, sort } }).then(r => r.data)

/**
 * @param {number|string} id
 * @param {number} limit
 */
export const getRelatedPosts = (id, limit = 4) =>
  api.get(`/api/posts/${id}/related`, { params: { limit } }).then(r => r.data)

/**
 * @param {number} accountID
 */
export const getPostsByUser = (accountID) =>
  api.get(`/api/posts/by-user/${accountID}`).then(r => r.data)

/**
 * @param {object} params - categoryID, difficulty (1-3), maxTime, excludeId
 */
export const getSuggestedPosts = (params = {}) =>
  api.get('/api/posts/suggest', { params }).then(r => r.data)

/**
 * Create a new post (pending approval).
 * @param {object} data - { accountID, categoryID, title, description, ingredients, media, video, level, cookingTime, steps }
 * (Đã thêm trường video vào payload)
 */
export const createPost = (data) =>
  api.post('/api/posts', data).then(r => r.data)

/**
 * Update an existing post.
 * @param {number|string} id 
 * @param {object} data - PostDTO
 */
export const updatePost = (id, data) =>
  api.put(`/api/posts/${id}`, data).then(r => r.data)

/**
 * Toggle post visibility (isActive).
 * @param {number|string} id 
 */
export const togglePostActive = (id) =>
  api.patch(`/api/posts/${id}/toggle-active`).then(r => r.data)

/**
 * @param {Array<number|string>} postIds - Mảng các ID bài viết (VD: [1, 2, 3])
 * @returns {Promise<Object>} Map chứa top comment, key là postID
 */
export const getTopCommentsBatch = (postIds = []) => {
  // Tránh gửi request thừa nếu mảng rỗng
  if (!postIds || postIds.length === 0) return Promise.resolve({});
  
  return api.get('/api/posts/top-comments-batch', { 
    params: { postIds: postIds.join(',') } 
  }).then(r => r.data)
}

export const getTrendingPosts = async (timeframe = 'month', limit = 10) => {
  try {
    const response = await api.get('/api/posts/trending', {
      params: { timeframe, limit }
    });
    return response.data;
  } catch (error) {
    console.error("Lỗi khi lấy bảng xếp hạng:", error);
    return [];
  }
}

// ── Helpers: map BE DTO fields to FE-compatible shape ──────────────────────
/**
 * Normalise a PublicPostDTO from BE into the shape RecipeCard expects.
 */
export function normalizePost(dto) {
  if (!dto) return null
  const diffMap = { 1: 'Easy', 2: 'Medium', 3: 'Hard' }
  return {
    id:         dto.postID,
    title:      dto.title,
    // media thường là ảnh bìa
    image:      dto.media || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&q=80',
    // MỚI: Thêm link video để Frontend có thể sử dụng <video> tag
    video:      dto.video || null, 
    time:       dto.cookingTime ? `${dto.cookingTime}p` : '—',
    difficulty: diffMap[dto.level] || 'Medium',
    likes:      dto.favoriteCount ?? 0,
    rating:     dto.avgRating ?? 0,
    views:      dto.views ?? 0,
    authorID:   dto.authorID ?? dto.accountID ?? null,
    author: {
      name:   dto.authorName || 'GoMet Chef',
      avatar: dto.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(dto.authorName||'G')}&background=EA580C&color=fff`
    },
    category: dto.categoryName || '',
    isActive: dto.isActive !== undefined ? dto.isActive : 1, // 🔥 Trạng thái ẩn/hiện
    isApproved: dto.isApproved ?? 1
  }
}

