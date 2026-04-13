<template>
  <Teleport to="body">
    <transition name="fade">
      <div class="map-modal-overlay" @click.self="$emit('close')">
        <div class="map-modal-content">
          
          <div class="map-header">
            <h3>{{ $t('map.title') }}</h3>
            <button class="btn-close-map" @click="$emit('close')">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
          </div>

          <div class="map-body">
            <iframe 
              width="100%" 
              height="100%" 
              frameborder="0" 
              style="border:0;" 
              referrerpolicy="no-referrer-when-downgrade"
              src="https://maps.google.com/maps?q=siêu+thị,+chợ+tươi+sống&t=&z=14&ie=UTF8&iwloc=&output=embed" 
              allowfullscreen>
            </iframe>
          </div>

          <div class="map-footer">
            <button class="btn-action outline" @click="handleShare">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="5" r="3"></circle><circle cx="6" cy="12" r="3"></circle><circle cx="18" cy="19" r="3"></circle><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"></line><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"></line></svg>
              {{ $t('map.share_link') }}
            </button>
            <button class="btn-action solid" @click="handleSave">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path></svg>
              {{ $t('map.save_map') }}
            </button>
          </div>

        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { onMounted } from 'vue'
import { toast } from '@/composables/useToast' // Kéo súng ống (Toast) vào đây
import { useI18n } from 'vue-i18n'

defineEmits(['close'])
const { t } = useI18n()

// TÍCH HỢP 1: Vừa mở Popup là bắn thông báo màu cam (Info)
onMounted(() => {
  toast.info(t('map.searching_nearby'))
})

// TÍCH HỢP 2: Bấm nút Lưu -> Bắn thông báo màu xanh (Success)
const handleSave = () => {
  toast.success(t('map.save_ok'))
}

// TÍCH HỢP 3: Bấm nút Chia sẻ -> Bắn thông báo màu xanh (Success)
const handleShare = () => {
  toast.success(t('map.share_ok'))
}
</script>

<style scoped>
.map-modal-overlay {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(17, 24, 39, 0.75); backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center; z-index: 9999;
}
.map-modal-content {
  width: 90%; max-width: 900px; height: 85vh; max-height: 750px;
  background: white; border-radius: 20px; display: flex; flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  animation: scaleUp 0.35s cubic-bezier(0.34, 1.56, 0.64, 1); /* Animation nẩy giống Toast của Sếp */
}
.map-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 18px 24px; border-bottom: 1px solid #E5E7EB; background: #FFF7ED;
  border-radius: 20px 20px 0 0;
}
.map-header h3 { margin: 0; font-family: 'Mulish', sans-serif; color: #EA580C; font-size: 1.25rem; font-weight: 800; }
.btn-close-map {
  background: white; border: 1px solid #D1D5DB; width: 36px; height: 36px;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  cursor: pointer; color: #4B5563; transition: 0.2s;
}
.btn-close-map:hover { background: #FEE2E2; color: #EF4444; border-color: #EF4444; transform: rotate(90deg); }

.map-body { flex: 1; width: 100%; overflow: hidden; background: #E5E7EB; }

/* CSS MỚI CHO FOOTER CHỨA NÚT TEST TOAST */
.map-footer {
  padding: 16px 24px;
  background: #ffffff;
  border-top: 1px solid #F3F4F6;
  border-radius: 0 0 20px 20px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
.btn-action {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 20px; border-radius: 10px;
  font-family: 'Mulish', sans-serif; font-weight: 700; font-size: 0.95rem;
  cursor: pointer; transition: 0.2s;
}
.btn-action.outline {
  background: white; border: 1.5px solid #E5E7EB; color: #374151;
}
.btn-action.outline:hover { background: #F3F4F6; border-color: #D1D5DB; }

.btn-action.solid {
  background: #111827; border: 1.5px solid #111827; color: white;
}
.btn-action.solid:hover { background: #EA580C; border-color: #EA580C; box-shadow: 0 4px 12px rgba(234, 88, 12, 0.3); transform: translateY(-2px); }

@keyframes scaleUp {
  from { opacity: 0; transform: scale(0.95) translateY(20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
</style>