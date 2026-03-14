import api from './api'

/**
 * Hàm vạn năng để up media (Ảnh/Video) lên Cloudinary
 * @param {File} file - File từ input (image hoặc video)
 * @param {String} folder - Thư mục trên mây (posts, avatars, steps, videos...)
 */
export const uploadMedia = async (file, folder = 'general') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('folder', folder)

  // Sửa endpoint từ /api/upload/image thành /api/upload 
  // để backend hiểu đây là một request upload đa phương tiện nói chung
  const res = await api.post('/api/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 120000
  })
  
  return res.data.url // Trả về link https://res.cloudinary.com/...
}