<template>
  <Transition name="pop-fade">
    <div class="ad-overlay" v-if="shouldShowAd" @click.self="closePopup">
      <div class="ad-content">
        <button class="close-btn" @click="closePopup" aria-label="Đóng quảng cáo">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>
        
        <a :href="adsUrl || '#'" :target="adsUrl ? '_blank' : '_self'" rel="noopener noreferrer">
          <img :src="adsImage" alt="Quảng cáo đặc biệt" class="ad-image" />
        </a>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api' // 🔥 Gọi API để lấy cấu hình

const props = defineProps({
  isVisible: {
    type: Boolean,
    required: true
  }
  // 🔥 Đã xóa prop imageUrl vì giờ mình lấy động từ Database
})

const emit = defineEmits(['close'])
const authStore = useAuthStore()

// --- BIẾN LƯU DỮ LIỆU QUẢNG CÁO ĐỘNG ---
const adsImage = ref('')
const adsUrl = ref('')

// --- LẤY CẤU HÌNH TỪ BACKEND ---
const fetchAdsConfig = async () => {
  try {
    const res = await api.get('/api/system-config')
    const configs = res.data
    
    // Tìm đúng mã Key của Quảng cáo
    const imgConfig = configs.find(c => c.configKey === 'ADS_BANNER_IMG')
    const urlConfig = configs.find(c => c.configKey === 'ADS_TARGET_URL')

    if (imgConfig && imgConfig.configValue) adsImage.value = imgConfig.configValue
    if (urlConfig && urlConfig.configValue) adsUrl.value = urlConfig.configValue
  } catch (error) {
    console.error("Lỗi khi tải quảng cáo động:", error)
  }
}

// Chạy ngay khi Popup được gọi
onMounted(() => {
  fetchAdsConfig()
})

// --- LOGIC KIỂM TRA ĐẶC QUYỀN (GIỮ NGUYÊN CỦA SẾP) ---
const shouldShowAd = computed(() => {
  if (!props.isVisible) return false;
  
  // 🔥 NẾU ADMIN CHƯA CÀI ẢNH QUẢNG CÁO -> KHÔNG HIỆN POPUP
  if (!adsImage.value) return false;

  const user = authStore.user;
  if (!user) return true;

  const isAdmin = user.isAdmin === 1 || user.isAdmin === true || user.role === 'ADMIN';
  const isPremium = user.isPremium === 1 || user.isPremium === true;

  if (isAdmin || isPremium) {
    return false; // Phá kén, thoát kiếp xem ads!
  }

  return true; // User thường thì phải xem
})

const closePopup = () => {
  emit('close')
}
</script>

<style scoped>
/* =======================================================
   1. CORE STYLE (GIỮ NGUYÊN CSS GỐC CỦA SẾP)
   ======================================================= */
.ad-overlay { 
  position: fixed; top: 0; left: 0; width: 100%; height: 100%; 
  background-color: rgba(15, 23, 42, 0.75); /* Tối ưu màu nền mờ sang trọng hơn */
  backdrop-filter: blur(4px); /* Thêm hiệu ứng mờ kính */
  display: flex; justify-content: center; align-items: center; 
  z-index: 9999; padding: 20px; 
}

.ad-content { 
  position: relative; 
  width: 100%; 
  max-width: 480px; /* Nới rộng một chút cho cân đối */
  animation: popIn 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); 
}

.ad-image { 
  width: 100%; 
  height: auto; 
  max-height: 85vh; /* QUAN TRỌNG: Không để ảnh dài quá màn hình */
  object-fit: contain; /* Giữ nguyên tỷ lệ ảnh thật */
  border-radius: 16px; 
  box-shadow: 0 20px 50px -10px rgba(0,0,0,0.5); 
  display: block; 
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); 
}
.ad-image:hover { transform: scale(1.02); }

.close-btn { 
  position: absolute; top: -15px; right: -15px; 
  background: #fff; color: #1e293b; border: 2px solid #e2e8f0; 
  border-radius: 50%; width: 36px; height: 36px; 
  display: flex; align-items: center; justify-content: center; 
  cursor: pointer; box-shadow: 0 4px 12px rgba(0,0,0,0.15); 
  transition: all 0.3s ease; 
  z-index: 10; 
}
.close-btn:hover { 
  background: #ea580c; color: white; border-color: #ea580c; 
  transform: rotate(90deg) scale(1.1); 
}

.pop-fade-enter-active, .pop-fade-leave-active { transition: opacity 0.3s ease; }
.pop-fade-enter-from, .pop-fade-leave-to { opacity: 0; }
@keyframes popIn { 0% { transform: scale(0.8); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }

/* =======================================================
   🔥 HỆ THỐNG RESPONSIVE (TỐI ƯU MỌI THIẾT BỊ)
   ======================================================= */

/* --- 1. Màn hình Tablet dọc & Mobile ngang (Dưới 768px) --- */
@media (max-width: 768px) {
  .ad-overlay { padding: 16px; }
  .ad-content { max-width: 400px; }
  
  /* Đưa nút đóng lùi vào trong một xíu để tránh chạm viền đt */
  .close-btn { 
    top: -12px; right: -12px; 
    width: 32px; height: 32px; 
  }
  .close-btn svg { width: 18px; height: 18px; }
  .ad-image { border-radius: 12px; }
}

/* --- 2. Màn hình Mobile Lớn (Dưới 480px) --- */
@media (max-width: 480px) {
  .ad-overlay { padding: 12px; }
  .ad-content { max-width: 320px; }
  
  /* Nếu ảnh chạm sát mép màn hình, kéo nút đóng nhích vào trong viền ảnh */
  .close-btn { 
    top: -10px; right: 0px; 
    width: 30px; height: 30px; 
    box-shadow: 0 2px 8px rgba(0,0,0,0.2);
  }
  .close-btn svg { width: 16px; height: 16px; }
}

/* --- 3. Xử lý khi xoay ngang điện thoại (Landscape) --- */
@media (max-height: 500px) and (orientation: landscape) {
  .ad-image { max-height: 90vh; width: auto; margin: 0 auto; }
  .ad-content { display: flex; justify-content: center; }
  /* Nút đóng phải bám sát theo chiều cao ảnh khi xoay ngang */
  .close-btn { top: -10px; right: auto; left: calc(50% + (90vh * 0.5) - 10px); }
}
</style>