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
/* Giữ nguyên 100% CSS cũ của sếp */
.ad-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.7); display: flex; justify-content: center; align-items: center; z-index: 9999; padding: 20px; }
.ad-content { position: relative; max-width: 450px; width: 100%; animation: popIn 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.ad-image { width: 100%; height: auto; border-radius: 12px; box-shadow: 0 20px 40px rgba(0,0,0,0.4); display: block; transition: 0.3s; cursor: pointer; }
.ad-image:hover { transform: scale(1.02); } /* Thêm xíu hiệu ứng khi di chuột vào ảnh */
.close-btn { position: absolute; top: -15px; right: -15px; background: #fff; color: #333; border: 2px solid #333; border-radius: 50%; width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; cursor: pointer; box-shadow: 0 2px 10px rgba(0,0,0,0.2); transition: 0.2s; z-index: 10; }
.close-btn:hover { background: #F97316; color: white; border-color: #F97316; transform: rotate(90deg); }
.pop-fade-enter-active, .pop-fade-leave-active { transition: opacity 0.3s ease; }
.pop-fade-enter-from, .pop-fade-leave-to { opacity: 0; }
@keyframes popIn { 0% { transform: scale(0.8); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }
@media (max-width: 640px) { .close-btn { top: -10px; right: -10px; width: 30px; height: 30px; } .close-btn svg { width: 18px; height: 18px; } }
</style>