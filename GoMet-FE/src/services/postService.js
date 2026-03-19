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
 * @param {number} limit
 */
export const getTrendingPosts = (limit = 10) =>
  api.get('/api/posts/trending', { params: { limit } }).then(r => r.data)

/**
 * Create a new post (pending approval).
 * @param {object} data - { accountID, categoryID, title, description, ingredients, media, video, level, cookingTime, steps }
 * (Đã thêm trường video vào payload)
 */
export const createPost = (data) =>
  api.post('/api/posts', data).then(r => r.data)


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
    category: dto.categoryName || ''
  }
}