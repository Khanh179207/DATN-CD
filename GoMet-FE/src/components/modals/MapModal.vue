<template>
  <Teleport to="body">
    <transition name="fade">
      <div class="map-modal-overlay" @click.self="$emit('close')">
        <div class="map-modal-content">
          
          <div class="map-header">
            <div class="header-left">
              <div class="icon-pulse" :class="{ 'is-active': !isLoading && !locationError, 'is-warning': locationError }">
                <div class="dot"></div>
                <div class="ring"></div>
              </div>
              <h3>{{ isLoading ? 'Đang định vị...' : '📍 Đi chợ cùng Gomet' }}</h3>
            </div>
            <button class="btn-close-map" @click="$emit('close')" title="Đóng">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
          </div>

          <div class="map-body">
            <transition name="fade">
              <div v-if="isLoading" class="loading-overlay">
                <div class="radar-spinner"></div>
                <p>Đang quét các khu chợ quanh Bạn...</p>
                <span class="sub-text">Bạn nhớ bấm "Cho phép" (Allow) quyền vị trí trên trình duyệt nhé!</span>
              </div>
            </transition>

            <iframe 
              v-show="!isLoading"
              class="google-map-iframe"
              :src="mapUrl" 
              allow="geolocation" 
              frameborder="0" 
              allowfullscreen
              referrerpolicy="no-referrer-when-downgrade">
            </iframe>
          </div>

          <div class="map-footer-minimal">
            <div class="location-status" :class="{ 'error': locationError }">
              <svg v-if="!locationError" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
              <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
              <span>{{ locationError ? 'Chưa có quyền GPS - Đang hiển thị vị trí ước tính' : 'Đã đồng bộ vệ tinh GPS - Hiển thị chợ & siêu thị gần nhất' }}</span>
            </div>
          </div>

        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { toast } from '@/composables/useToast'

defineEmits(['close'])

const isLoading = ref(true)
const locationError = ref(false)
const mapUrl = ref('')

onMounted(() => {
  getUserLocation()
})

// HÀM LẤY VỊ TRÍ CHUẨN XÁC
const getUserLocation = () => {
  if ("geolocation" in navigator) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const lat = position.coords.latitude
        const lng = position.coords.longitude

        mapUrl.value = `https://maps.google.com/maps?q=siêu+thị,+chợ+near+${lat},${lng}&hl=vi&z=15&output=embed`
        
        locationError.value = false
        isLoading.value = false
        toast.success('Đã bắt được tọa độ của Bạn! 🚀')
      },
      (error) => {
        console.warn("Lỗi lấy vị trí:", error.message)
        locationError.value = true
        isLoading.value = false
        // Fallback: Chỉ search "siêu thị chợ gần đây" dựa vào IP mạng
        mapUrl.value = 'https://maps.google.com/maps?q=siêu+thị,+chợ+gần+đây&hl=vi&z=14&output=embed'
        toast.warn('Bạn chưa cấp quyền vị trí, hệ thống đang dùng vị trí ước tính nhé!')
      },
      { enableHighAccuracy: true, timeout: 8000, maximumAge: 0 }
    )
  } else {
    // 3. TRÌNH DUYỆT CÙI
    isLoading.value = false
    locationError.value = true
    mapUrl.value = 'https://maps.google.com/maps?q=siêu+thị,+chợ+gần+đây&hl=vi&z=14&output=embed'
    toast.error('Trình duyệt không hỗ trợ định vị GPS!')
  }
}
</script>

<style scoped>
.map-modal-overlay {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(15, 23, 42, 0.8); backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center; z-index: 9999;
}
.map-modal-content {
  width: 95%; max-width: 1100px; height: 88vh; max-height: 850px;
  background: white; border-radius: 24px; display: flex; flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  animation: scaleUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;
}

/* HEADER CAO CẤP */
.map-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px; border-bottom: 1px solid #E2E8F0; background: #FFFFFF;
}
.header-left { display: flex; align-items: center; gap: 12px; }
.map-header h3 { margin: 0; font-family: 'Mulish', sans-serif; color: #0F172A; font-size: 1.3rem; font-weight: 900; }

/* CHẤM ĐỎ NHẤP NHÁY (PULSE) */
.icon-pulse { position: relative; width: 12px; height: 12px; display: flex; align-items: center; justify-content: center; }
.icon-pulse .dot { width: 10px; height: 10px; background: #EA580C; border-radius: 50%; z-index: 2; transition: 0.3s; }
.icon-pulse .ring { position: absolute; width: 100%; height: 100%; border-radius: 50%; border: 2px solid #EA580C; animation: radarPing 1.5s infinite ease-out; }
.icon-pulse.is-active .dot { background: #10B981; }
.icon-pulse.is-active .ring { border-color: #10B981; animation: none; opacity: 0.2; }
.icon-pulse.is-warning .dot { background: #F59E0B; }
.icon-pulse.is-warning .ring { border-color: #F59E0B; animation: none; opacity: 0.2; }

.btn-close-map {
  background: #F1F5F9; border: none; width: 36px; height: 36px;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  cursor: pointer; color: #64748B; transition: 0.2s;
}
.btn-close-map:hover { background: #FEE2E2; color: #EF4444; transform: rotate(90deg); }

/* BODY MAP */
.map-body { flex: 1; width: 100%; position: relative; background: #F8FAFC; }
.google-map-iframe { width: 100%; height: 100%; border: 0; }

/* MÀN HÌNH LOADING RADAR */
.loading-overlay {
  position: absolute; inset: 0; background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(5px);
  display: flex; flex-direction: column; align-items: center; justify-content: center; z-index: 10;
}
.loading-overlay p { font-size: 1.2rem; font-weight: 800; color: #0F172A; margin: 20px 0 8px; }
.loading-overlay .sub-text { font-size: 0.95rem; color: #64748B; font-weight: 600; }

.radar-spinner {
  width: 64px; height: 64px; border: 4px solid #FFEDD5; border-top-color: #EA580C;
  border-radius: 50%; animation: spin 1s linear infinite;
}

/* FOOTER TỐI GIẢN */
.map-footer-minimal {
  padding: 12px 24px; background: #F8FAFC; border-top: 1px solid #E2E8F0;
  display: flex; justify-content: center; align-items: center;
}
.location-status { display: flex; align-items: center; gap: 8px; font-size: 0.85rem; font-weight: 800; color: #10B981; }
.location-status.error { color: #F59E0B; }

/* ANIMATIONS */
@keyframes scaleUp { from { opacity: 0; transform: scale(0.95) translateY(20px); } to { opacity: 1; transform: scale(1) translateY(0); } }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
@keyframes radarPing { 0% { transform: scale(1); opacity: 1; } 100% { transform: scale(3); opacity: 0; } }
</style>