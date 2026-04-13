<template>
  <Transition name="pop-fade">
    <div class="ad-overlay" v-if="shouldShowAd" @click.self="closePopup">
      <div class="ad-content">
        <button class="close-btn" @click="closePopup" :aria-label="t('common.close_ad')">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>
        
        <img :src="imageUrl" :alt="t('common.special_ad')" class="ad-image" />
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth' // 🔥 Import store chứa thông tin user của sếp

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

const emit = defineEmits(['close'])
const authStore = useAuthStore()
const { t } = useI18n()

// 🔥 LOGIC KIỂM TRA ĐẶC QUYỀN
const shouldShowAd = computed(() => {
  // 1. Nếu cha không muốn hiện (isVisible = false) -> Ẩn luôn
  if (!props.isVisible) return false;

  // 2. Lấy thông tin user hiện tại
  const user = authStore.user;

  // 3. Nếu chưa đăng nhập (Khách) -> Bắt xem quảng cáo
  if (!user) return true;

  // 4. Nếu là Admin hoặc Premium (check cả trường hợp số 1 hoặc boolean true) -> Không hiện
  const isAdmin = user.isAdmin === 1 || user.isAdmin === true;
  const isPremium = user.isPremium === 1 || user.isPremium === true;

  if (isAdmin || isPremium) {
    return false; // Phá kén, thoát kiếp xem ads!
  }

  // 5. User bình thường -> Phải xem
  return true;
})

const closePopup = () => {
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