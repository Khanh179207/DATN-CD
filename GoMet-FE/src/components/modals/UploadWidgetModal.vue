<!-- File: src/components/UploadWidget.vue -->
<template>
  <transition name="slide-up">
    <div v-if="uploadStore.isVisible" class="upload-widget-wrapper">
      <div class="upload-card">
        
        <!-- Ảnh thu nhỏ của bài viết -->
        <div class="image-box">
          <img :src="uploadStore.postImage" alt="Preview">
          <!-- Hiệu ứng xoay tròn khi đang tải -->
          <div v-if="uploadStore.status === 'uploading'" class="loading-overlay">
            <span class="spinner"></span>
          </div>
        </div>

        <!-- Thông tin trạng thái -->
        <div class="info-box">
          <h4 class="title">{{ uploadStore.postTitle }}</h4>
          <p class="status-msg" :class="uploadStore.status">
            <span v-if="uploadStore.status === 'success'">✅</span>
            <span v-else-if="uploadStore.status === 'error'">❌</span>
            <span v-else>🚀</span>
            {{ uploadStore.message }}
          </p>
        </div>

        <!-- Nút đóng thủ công -->
        <button class="btn-close" @click="uploadStore.close()">✕</button>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { useUploadStore } from '@/stores/uploadStore'
const uploadStore = useUploadStore()
</script>

<style scoped>
.upload-widget-wrapper {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 9999;
}

.upload-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 12px;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(234, 88, 12, 0.2);
  width: 320px;
}

.image-box {
  position: relative;
  width: 48px;
  height: 48px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
}

.image-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.loading-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.info-box {
  flex: 1;
  min-width: 0;
}

.title {
  margin: 0 0 4px 0;
  font-size: 0.9rem;
  font-weight: 800;
  color: #0f172a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.status-msg {
  margin: 0;
  font-size: 0.8rem;
  font-weight: 600;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-msg.success { color: #16a34a; }
.status-msg.error { color: #ef4444; }
.status-msg.uploading { color: #ea580c; }

.btn-close {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
}
.btn-close:hover { color: #0f172a; background: #f1f5f9; }

@keyframes spin { to { transform: rotate(360deg); } }

/* Hiệu ứng trượt lên */
.slide-up-enter-active, .slide-up-leave-active { transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1); }
.slide-up-enter-from, .slide-up-leave-to { opacity: 0; transform: translateY(30px); }
</style>