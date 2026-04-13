<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="isOpen" class="step-modal-overlay" @click.self="closeModal">
        
        <div class="step-modal-content slide-in-up">
          
          <div class="modal-header-sticky">
            <div class="modal-step-badge">
              <span>{{ t('create_post.step_label', { count: currentIndex + 1 }) }}</span>
            </div>
            
            <button class="btn-close-modern" @click="closeModal" :title="t('common.close')">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
          </div>

          <div class="modal-body-split" v-if="currentStepData">
            
            <div class="modal-left-col">
              <div class="modal-image-hero" v-if="currentStepData.images && currentStepData.images.length && currentStepData.images[0]">
                <img :src="currentStepData.images[0]" :alt="t('cooking_card.step_image_alt', { count: currentIndex + 1 })" loading="lazy" @error="handleImageError" />
              </div>
              <div class="modal-image-empty" v-else>
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg>
                <p>{{ t('cooking_card.no_image') }}</p>
              </div>
            </div>

            <div class="modal-right-col">
              <div class="modal-desc-box">
                <h3 class="desc-title">{{ t('recipe.process') }}</h3>
                <div class="desc-text-scroll">
                  <p>{{ currentStepData.desc }}</p>
                </div>
              </div>
            </div>
            
          </div>

          <div class="modal-footer-sticky">
            <button class="btn-nav btn-nav-outline" @click="prevStep" :disabled="currentIndex === 0">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="15 18 9 12 15 6"></polyline></svg>
              {{ t('cooking_card.previous') }}
            </button>
            <button class="btn-nav btn-nav-primary" @click="nextStep" :disabled="currentIndex === steps.length - 1">
              {{ t('cooking_card.next') }}
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="9 18 15 12 9 6"></polyline></svg>
            </button>
          </div>

        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  steps: {
    type: Array,
    required: true,
    default: () => []
  }
})

const isOpen = ref(false)
const currentIndex = ref(0)
const { t } = useI18n()

const currentStepData = computed(() => {
  if (!props.steps || props.steps.length === 0) return null
  return props.steps[currentIndex.value]
})

const openModal = (startIndex = 0) => {
  currentIndex.value = startIndex
  isOpen.value = true
  document.body.style.overflow = 'hidden'
}

const closeModal = () => {
  isOpen.value = false
  document.body.style.overflow = ''
}

const nextStep = () => {
  if (currentIndex.value < props.steps.length - 1) {
    currentIndex.value++
  }
}

const prevStep = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

const handleImageError = (e) => {
  e.target.style.display = 'none';
  // Nếu ảnh lỗi, có thể thêm class để ép nó hiện box "empty" ở trên, 
  // nhưng ở mức đơn giản ta chỉ ẩn nó đi.
}

defineExpose({
  openModal,
  closeModal
})
</script>

<style scoped>
* { box-sizing: border-box; }

.step-modal-overlay {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(15, 23, 42, 0.85); 
  backdrop-filter: blur(8px); 
  -webkit-backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center;
  z-index: 99999; padding: 20px;
}

.step-modal-content {
  background: #FFFFFF;
  /* ĐÃ MỞ RỘNG ĐỂ CHỨA 2 CỘT */
  width: 100%; max-width: 1000px; 
  height: 85vh; /* Fix cứng chiều cao để quản lý cuộn bên trong */
  border-radius: 28px; 
  display: flex; flex-direction: column;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.4), 0 0 0 1px rgba(255,255,255,0.1); 
}

/* ================= STICKY HEADER ================= */
.modal-header-sticky {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 30px;
  background: white;
  border-bottom: 1px solid #F1F5F9;
  z-index: 10;
}

.modal-step-badge {
  background: #FFF7ED; color: #EA580C;
  padding: 8px 20px; border-radius: 14px;
  font-weight: 800; font-family: 'Mulish', sans-serif; font-size: 1.1rem;
  border: 1px solid #FFEDD5;
}
.modal-step-badge span { color: #111827; }

.btn-close-modern {
  width: 40px; height: 40px; border-radius: 12px;
  background: #F8FAFC; border: none; color: #64748B;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.2s ease;
}
.btn-close-modern:hover { background: #FEE2E2; color: #EF4444; transform: scale(1.05); }


/* ================= BODY CHIA 2 CỘT ================= */
.modal-body-split {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr; /* Chia thành 2 cột bằng nhau */
  gap: 30px;
  padding: 30px;
  background: #FAFAF9; 
  overflow: hidden; /* Không cho body ngoài cuộn, chỉ cuộn text bên trong */
}

/* Responsive: Nếu màn hình nhỏ (Mobile/Tablet) thì xếp dọc lại */
@media (max-width: 768px) {
  .modal-body-split {
    grid-template-columns: 1fr; /* Thành 1 cột */
    overflow-y: auto; /* Cho phép cuộn toàn bộ body trên mobile */
  }
  .desc-text-scroll { overflow-y: visible !important; padding-right: 0 !important; }
}

/* CỘT TRÁI (IMAGE) */
.modal-left-col {
  display: flex; flex-direction: column;
  height: 100%;
}
.modal-image-hero {
  width: 100%;
  height: 100%; /* Cố gắng lấp đầy cột */
  border-radius: 20px;
  overflow: hidden;
  background: #E2E8F0;
  box-shadow: 0 10px 25px -5px rgba(0,0,0,0.1);
}
.modal-image-hero img {
  width: 100%; height: 100%;
  object-fit: cover; /* Cắt ảnh lấp đầy khung */
  transition: transform 0.5s ease;
}
.modal-image-hero:hover img { transform: scale(1.03); }

/* Khung hiển thị khi không có ảnh */
.modal-image-empty {
  width: 100%; height: 100%; min-height: 300px;
  border-radius: 20px;
  background: #F1F5F9; border: 2px dashed #CBD5E1;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 10px; color: #94A3B8; font-family: 'Mulish', sans-serif; font-weight: 600;
}

/* CỘT PHẢI (DESC) */
.modal-right-col {
  height: 100%;
  display: flex; flex-direction: column;
}
.modal-desc-box {
  background: white;
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.02);
  border: 1px solid #F1F5F9;
  display: flex; flex-direction: column;
  height: 100%; /* Lấp đầy chiều cao cột phải */
}

.desc-title {
  font-family: 'Playfair Display', serif;
  font-size: 1.4rem; color: #1E293B;
  margin: 0 0 20px 0;
  display: flex; align-items: center; gap: 10px;
  flex-shrink: 0; /* Không bị ép nhỏ lại */
}
.desc-title::before {
  content: ''; display: inline-block; width: 4px; height: 20px;
  background: #EA580C; border-radius: 4px;
}

/* Khung Text Cuộn Độc Lập */
.desc-text-scroll {
  flex: 1; /* Chiếm hết chiều cao còn lại của Box */
  overflow-y: auto; /* Sinh ra thanh cuộn ở đây nếu text quá dài */
  padding-right: 15px; /* Chừa không gian cho thanh cuộn */
}
.desc-text-scroll p {
  font-family: 'Mulish', sans-serif;
  font-size: 1.15rem; line-height: 1.8; color: #334155;
  margin: 0;
}

/* Custom Scrollbar cho phần text */
.desc-text-scroll::-webkit-scrollbar { width: 6px; }
.desc-text-scroll::-webkit-scrollbar-track { background: transparent; }
.desc-text-scroll::-webkit-scrollbar-thumb { background-color: #CBD5E1; border-radius: 10px; }


/* ================= STICKY FOOTER NAVIGATION ================= */
.modal-footer-sticky {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 30px;
  background: white;
  border-top: 1px solid #F1F5F9;
  z-index: 10;
}

.btn-nav {
  display: flex; align-items: center; justify-content: center; gap: 8px;
  padding: 12px 24px;
  border-radius: 14px;
  font-weight: 800; font-size: 1rem; font-family: 'Mulish', sans-serif;
  cursor: pointer; transition: all 0.2s ease;
  min-width: 140px;
}

.btn-nav-outline { background: transparent; color: #64748B; border: 2px solid #E2E8F0; }
.btn-nav-outline:hover:not(:disabled) { border-color: #94A3B8; color: #0F172A; }

.btn-nav-primary {
  background: linear-gradient(135deg, #F97316 0%, #EA580C 100%);
  color: white; border: none;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.25);
}
.btn-nav-primary:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 6px 16px rgba(234, 88, 12, 0.4); }

.btn-nav:disabled { opacity: 0.3; cursor: not-allowed; box-shadow: none; transform: none; }

/* ================= ANIMATIONS ================= */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.slide-in-up { animation: slideInUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes slideInUp {
  from { opacity: 0; transform: translateY(40px) scale(0.98); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
</style>