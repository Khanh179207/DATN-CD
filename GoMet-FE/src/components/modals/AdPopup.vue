<template>
  <Transition name="pop-fade">
    <div class="ad-overlay" v-if="isVisible" @click.self="closePopup">
      <div class="ad-content">
        <button class="close-btn" @click="closePopup" aria-label="Đóng quảng cáo">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>
        
        <img :src="imageUrl" alt="Quảng cáo đặc biệt" class="ad-image" />
      </div>
    </div>
  </Transition>
</template>

<script setup>
// Nhận props từ cha (đường dẫn ảnh và trạng thái hiển thị)
const props = defineProps({
  isVisible: {
    type: Boolean,
    required: true
  },
  imageUrl: {
    type: String,
    required: true
  }
})

// Định nghĩa sự kiện để báo cho cha biết cần đóng popup
const emit = defineEmits(['close'])

const closePopup = () => {
  // Phát sự kiện 'close' ra ngoài
  emit('close')
}
</script>

<style scoped>
/* Lớp nền mờ phủ kín màn hình */
.ad-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background-color: rgba(0, 0, 0, 0.7); /* Màu đen mờ 70% giống Shopee */
  display: flex;
  justify-content: center; /* Căn giữa ngang */
  align-items: center; /* Căn giữa dọc */
  z-index: 9999; /* Luôn nổi trên cùng */
  padding: 20px;
}

/* Khung chứa nội dung quảng cáo */
.ad-content {
  position: relative; /* Để nút đóng định vị tuyệt đối theo khung này */
  max-width: 450px; /* Chiều rộng tối đa */
  width: 100%;
  animation: popIn 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); /* Hiệu ứng nẩy nhẹ */
}

/* Ảnh quảng cáo */
.ad-image {
  width: 100%;
  height: auto;
  border-radius: 12px; /* Bo góc cho đẹp */
  box-shadow: 0 20px 40px rgba(0,0,0,0.4);
  display: block;
}

/* Nút đóng (X) */
.close-btn {
  position: absolute;
  top: -15px;
  right: -15px;
  background: #fff;
  color: #333;
  border: 2px solid #333;
  border-radius: 50%;
  width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0,0,0,0.2);
  transition: 0.2s;
  z-index: 10; /* Nổi trên ảnh */
}

.close-btn:hover {
  background: #F97316; /* Màu cam thương hiệu */
  color: white;
  border-color: #F97316;
  transform: rotate(90deg);
}

/* --- Vue Transition Styles (Hiệu ứng Fade) --- */
.pop-fade-enter-active,
.pop-fade-leave-active {
  transition: opacity 0.3s ease;
}

.pop-fade-enter-from,
.pop-fade-leave-to {
  opacity: 0;
}

/* Animation nẩy nhẹ khi hiện khung nội dung */
@keyframes popIn {
  0% { transform: scale(0.8); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

/* Responsive cho mobile */
@media (max-width: 640px) {
  .close-btn {
    top: -10px; right: -10px;
    width: 30px; height: 30px;
  }
  .close-btn svg {
    width: 18px; height: 18px;
  }
}
</style>