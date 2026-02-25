import api from './api'

/**
 * Upload a single file to the media server.
 * @param {File} file - The file to upload (image or video)
 * @returns {Promise<string>} The URL to access the uploaded media, e.g. "/api/media/{token}"
 */
export const uploadMedia = async (file) => {
  const formData = new FormData()
  formData.append('file', file)
  const res = await api.post('/api/media/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  // Returns { url: "/api/media/{token}", token: "..." }
  return res.data.url
}

/**
 * Upload multiple files concurrently.
 * @param {File[]} files - Array of files to upload
 * @returns {Promise<string[]>} Array of uploaded URLs in the same order
 */
export const uploadMultiple = async (files) => {
  return Promise.all(files.map(uploadMedia))
}
