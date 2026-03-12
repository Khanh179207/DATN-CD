import api from './api'

let categoriesCache = null
let categoriesCacheAt = 0
let categoriesRequest = null
const CATEGORIES_TTL_MS = 5 * 60 * 1000

export const getCategories = async ({ force = false } = {}) => {
  const now = Date.now()
  if (!force && categoriesCache && (now - categoriesCacheAt) < CATEGORIES_TTL_MS) {
    return categoriesCache
  }
  if (!force && categoriesRequest) {
    return categoriesRequest
  }

  categoriesRequest = api.get('/api/categories')
    .then(r => {
      categoriesCache = Array.isArray(r.data) ? r.data : []
      categoriesCacheAt = Date.now()
      return categoriesCache
    })
    .finally(() => {
      categoriesRequest = null
    })

  return categoriesRequest
}

export const getCategoryById = (id) =>
  api.get(`/api/categories/${id}`).then(r => r.data)
