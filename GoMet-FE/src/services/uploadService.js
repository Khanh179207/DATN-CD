import api from './api'

/**
 * CHỐT: Hàm vạn năng để upload Media (Ảnh/Video) lên Cloudinary.
 * Đồng bộ hoàn toàn với Backend @PostMapping("/api/upload").
 * @param {File} file - File từ input
 * @param {String} folder - Thư mục trên mây (posts, avatars, events...)
 */
export const uploadMedia = async (file, folder = 'general') => {
    if (!file) {
        console.error('>>> [uploadService] Không có file để upload!')
        return null
    }

    const formData = new FormData()
    formData.append('file', file)
    formData.append('folder', folder)

    try {
        console.log(`>>> [Frontend] Đang đẩy file ${file.name} lên /api/upload...`)

        // CHỐT: Endpoint là /api/upload (Khớp với Java Controller đã fix)
        const res = await api.post('/api/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
            // Timeout 120s rất quan trọng để tránh lỗi khi mạng yếu hoặc file nặng
            timeout: 120000
        })

        if (res.data && res.data.status === 'success') {
            console.log('>>> [Frontend] Upload thành công! URL:', res.data.url)
            return res.data.url
        }

        return res.data.url // Fallback trả về link trực tiếp

    } catch (error) {
        console.error('>>> [Frontend] Lỗi uploadMedia:', error.response?.data || error.message)
        // Quăng lỗi ra để phía UI (Vue component) có thể bắt được và hiện Toast thông báo
        throw error
    }
}