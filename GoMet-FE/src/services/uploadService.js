import api from './api'

/**
 * Hàm vạn năng để up ảnh lên Cloudinary
 * @param {File} file - File từ input
 * @param {String} folder - Thư mục trên mây (posts, avatars, events, reports)
 */
export const uploadMedia = async (file, folder = 'general') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('folder', folder)

  const res = await api.post('/api/upload/image', formData)
  return res.data.url // Trả về link https://res.cloudinary.com/...
}