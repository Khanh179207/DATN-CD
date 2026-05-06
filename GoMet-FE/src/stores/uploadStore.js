// File: src/stores/uploadStore.js
import { defineStore } from 'pinia'

export const useUploadStore = defineStore('upload', {
  state: () => ({
    isVisible: false,     // Có đang hiện khung báo cáo không
    status: 'uploading',  // Trạng thái: 'uploading' | 'success' | 'error'
    postTitle: '',        // Tên bài viết đang tải
    postImage: '',        // Ảnh bìa thu nhỏ
    message: 'Đang xử lý...' // Lời nhắn
  }),
  actions: {
    startUpload(title, image) {
      this.isVisible = true;
      this.status = 'uploading';
      this.postTitle = title;
      this.postImage = image || 'https://via.placeholder.com/60?text=GoMet';
      this.message = 'Đang tải bài viết lên...';
    },
    setSuccess(msg) {
      this.status = 'success';
      this.message = msg || 'Tải lên thành công!';
      // Tự động tắt khung thông báo sau 4 giây
      setTimeout(() => this.close(), 4000); 
    },
    setError(msg) {
      this.status = 'error';
      this.message = msg || 'Đã xảy ra lỗi!';
      // Lỗi thì để hiển thị lâu hơn một chút (6 giây)
      setTimeout(() => this.close(), 6000);
    },
    close() {
      this.isVisible = false;
    }
  }
})