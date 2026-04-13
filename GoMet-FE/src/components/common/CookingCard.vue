<template>
  <Teleport to="body">
    <Transition name="fade-modal">
      <div v-if="isOpen" class="cook-modal-overlay" @click.self="closeModal">
        
        <div class="cook-modal-lux" ref="modalRef">
          <div class="progress-track-lux">
            <div class="progress-fill-lux" :style="{ width: progressWidth + '%' }"></div>
          </div>

          <div class="modal-header-lux">
            <div class="header-left">
              <div class="step-pill">
                <span class="step-label">BƯỚC THỰC HIỆN</span>
                <span class="step-current">{{ currentIndex + 1 }}</span>
                <span class="step-total">/ {{ steps.length }}</span>
              </div>
              
              <div class="divider-v"></div>

              <div class="size-controls">
                <span class="ctrl-label">Cỡ chữ:</span>
                <button @click="changeFontSize(-2)" class="size-btn" title="Thu nhỏ">A-</button>
                <div class="size-indicator">
                   <div class="dot" v-for="i in 5" :key="i" :class="{active: fontSize >= 16 + (i*4)}"></div>
                </div>
                <button @click="changeFontSize(2)" class="size-btn" title="Phóng to">A+</button>
              </div>
            </div>

            <div class="header-right">
              <button 
                @click="isAutoRead = !isAutoRead" 
                class="tool-btn-lux" 
                :class="{ 'is-active': isAutoRead }"
                title="Tự động đọc bước tiếp theo"
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M2 11v3a4 4 0 0 0 4 4h12a4 4 0 0 0 4-4v-3"></path><path d="M5 8V5a3 3 0 0 1 3-3h8a3 3 0 0 1 3 3v3"></path><circle cx="12" cy="12" r="3"></circle></svg>
                <span>ĐỌC TỰ ĐỘNG</span>
              </button>

              <button 
                @click="toggleVoiceControl" 
                class="tool-btn-lux mic-btn" 
                :class="{ 'listening': isListening }"
                title="Điều khiển bằng giọng nói"
              >
                <div v-if="isListening" class="listening-ring"></div>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"></path><path d="M19 10v2a7 7 0 0 1-14 0v-2"></path><line x1="12" y1="19" x2="12" y2="23"></line></svg>
                <span>{{ isListening ? 'ĐANG LẮNG NGHE' : 'GIỌNG NÓI' }}</span>
              </button>

              <div class="divider-v"></div>

              <button @click="toggleFullscreen" class="btn-close-lux" :title="isFullscreen ? 'Thu nhỏ' : 'Toàn màn hình'">
                <svg v-if="!isFullscreen" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M8 3H5a2 2 0 0 0-2 2v3m18 0V5a2 2 0 0 0-2-2h-3m0 18h3a2 2 0 0 0 2-2v-3M3 16v3a2 2 0 0 0 2 2h3"></path></svg>
                <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M8 3v3a2 2 0 0 1-2 2H3m18 0h-3a2 2 0 0 1-2-2V3m0 18v-3a2 2 0 0 1 2-2h3M3 16h3a2 2 0 0 1 2 2v3"></path></svg>
              </button>
              
              <div class="divider-v"></div>

              <button @click="closeModal" class="btn-close-lux" title="Đóng">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
              </button>
            </div>
          </div>

          <div class="modal-body-lux">
            <Transition :name="transitionName" mode="out-in">
              <div :key="currentIndex" class="content-grid-lux">
                
                <div class="image-section">
                  <div class="image-card-luxury">
                    <img v-if="currentStepData?.images?.length" :src="currentStepData.images[0]" alt="Cooking Step" />
                    <div v-else class="image-placeholder">
                      <div class="placeholder-icon">🍳</div>
                      <p>Chưa có hình ảnh minh họa cho bước này</p>
                    </div>
                  </div>
                </div>

                <div class="text-section">
                  <div class="instruction-container">
                    <h4 class="instruction-title">CHI TIẾT THỰC HIỆN</h4>
                    <div class="instruction-text custom-scroll">
                      <p :style="{ fontSize: fontSize + 'px' }">{{ currentStepData?.desc }}</p>
                    </div>
                  </div>
                </div>

              </div>
            </Transition>
          </div>

          <div class="modal-footer-lux">
            <button class="nav-btn btn-prev" @click="prevStep" :disabled="currentIndex === 0">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="15 18 9 12 15 6"></polyline></svg>
              BƯỚC TRƯỚC
            </button>

            <div class="voice-status" v-if="isListening">
               <div class="sound-wave">
                  <div class="bar" v-for="i in 5" :key="i"></div>
               </div>
               <span class="voice-command-text">Hô <strong>"Tiếp theo"</strong> hoặc <strong>"Quay lại"</strong></span>
            </div>

            <button v-if="!isLastStep" class="nav-btn btn-next" @click="nextStep">
              BƯỚC TIẾP THEO
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="9 18 15 12 9 6"></polyline></svg>
            </button>

            <button v-else class="nav-btn btn-finish" @click="finishCooking">
              HOÀN THÀNH MÓN NÀY
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, onBeforeUnmount, onMounted } from 'vue'
import { toast } from '@/composables/useToast'

const props = defineProps({
  steps: { type: Array, required: true, default: () => [] },
  recipeName: { type: String, default: 'Món ăn' }
})

const isOpen = ref(false)
const currentIndex = ref(0)
const fontSize = ref(22)
const isListening = ref(false)
const isAutoRead = ref(false)
const isSpeaking = ref(false)
const transitionName = ref('slide-right')
const isFullscreen = ref(false)
const modalRef = ref(null)

let recognition = null
let wakeLock = null
const synth = window.speechSynthesis

const currentStepData = computed(() => props.steps[currentIndex.value] || null)
const progressWidth = computed(() => ((currentIndex.value + 1) / props.steps.length) * 100)
const isLastStep = computed(() => currentIndex.value === props.steps.length - 1)

// 🔥 TÍNH NĂNG WAKE LOCK: Giữ màn hình luôn sáng khi nấu ăn
const requestWakeLock = async () => {
  try {
    if ('wakeLock' in navigator) {
      wakeLock = await navigator.wakeLock.request('screen')
    }
  } catch (err) {
    console.warn('Wake Lock API not supported or denied:', err)
  }
}
const releaseWakeLock = async () => {
  if (wakeLock !== null) {
    await wakeLock.release()
    wakeLock = null
  }
}

// Lắng nghe sự kiện toàn màn hình
document.addEventListener('fullscreenchange', () => {
  isFullscreen.value = !!document.fullscreenElement
})

const changeFontSize = (delta) => {
  fontSize.value = Math.max(16, Math.min(42, fontSize.value + delta))
}

const speak = () => {
  synth.cancel()
  const text = currentStepData.value?.desc
  if (!text) return
  const utter = new SpeechSynthesisUtterance(text)
  utter.lang = 'vi-VN'
  utter.onstart = () => isSpeaking.value = true
  utter.onend = () => isSpeaking.value = false
  synth.speak(utter)
}

// 🔥 VOICE CORE 3.0: Ổn định và chính xác
const initVoice = () => {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
  if (!SpeechRecognition) return
  
  recognition = new SpeechRecognition()
  recognition.lang = 'vi-VN'
  recognition.continuous = false 
  recognition.interimResults = false

  recognition.onresult = (event) => {
    const result = event.results[0][0].transcript.toLowerCase().trim()
    console.log("🎤 Lệnh nhận diện:", result)
    
    // Logic ưu tiên lùi bước để tránh nhận nhầm lệnh "Tiếp"
    if (result.includes('lại') || result.includes('gô bắc') || result.includes('lùi') || result.includes('bách')) {
      prevStep()
    } else if (result.includes('tiếp') || result.includes('sau') || result.includes('nét') || result.includes('qua')) {
      nextStep()
    }
  }

  recognition.onend = () => {
    if (isListening.value) recognition.start() // Tự động bật lại Mic
  }
}

const toggleVoiceControl = () => {
  if (!recognition) initVoice()
  isListening.value = !isListening.value
  isListening.value ? recognition.start() : recognition.stop()
}

const nextStep = () => {
  if (currentIndex.value < props.steps.length - 1) {
    transitionName.value = 'slide-right'
    currentIndex.value++
    if (isAutoRead.value) setTimeout(speak, 500)
  }
}

const prevStep = () => {
  if (currentIndex.value > 0) {
    transitionName.value = 'slide-left'
    currentIndex.value--
    if (isAutoRead.value) setTimeout(speak, 500)
  }
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    modalRef.value?.requestFullscreen().catch(err => {
      toast.error('Trình duyệt không hỗ trợ toàn màn hình!')
    })
  } else {
    document.exitFullscreen()
  }
}

const finishCooking = () => {
  toast.success(`Chúc mừng! Bạn đã hoàn thành ${props.recipeName} 🎉`)
  closeModal()
}

const openModal = (idx = 0) => {
  currentIndex.value = idx
  isOpen.value = true
  requestWakeLock() // Bật giữ sáng màn hình
}

const closeModal = () => {
  isOpen.value = false
  isListening.value = false
  if (recognition) recognition.stop()
  if (document.fullscreenElement) document.exitFullscreen() // Thoát toàn màn hình
  synth.cancel()
  releaseWakeLock() // Tắt giữ sáng màn hình
}

onBeforeUnmount(() => { closeModal() })
defineExpose({ openModal, closeModal })
</script>

<style scoped lang="scss">
// ==========================================
// 🎨 GOMET - COOKING MODE (LUXURY EDITORIAL)
// ==========================================
$brand-orange: #ea580c;
$brand-gradient: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
$success-gradient: linear-gradient(135deg, #16a34a 0%, #22c55e 100%);
$text-main: #0f172a;
$text-sub: #64748b;
$bg-soft: #f8fafc;
$border-light: #e2e8f0;
$white: #ffffff;
$shadow-soft: 0 10px 40px rgba(0, 0, 0, 0.08);
$shadow-lux: 0 25px 60px rgba(15, 23, 42, 0.15);

/* --- OVERLAY & MODAL --- */
.cook-modal-overlay {
  position: fixed; inset: 0; background: rgba(15, 23, 42, 0.7);
  backdrop-filter: blur(12px); display: flex; align-items: center; justify-content: center; z-index: 999999;
}

.cook-modal-lux {
  background: $white; width: 96vw; max-width: 1280px; height: 92vh;
  border-radius: 32px; display: flex; flex-direction: column; overflow: hidden;
  box-shadow: $shadow-lux;
  border: 1px solid rgba(255,255,255,0.4);
  position: relative;
  font-family: 'Inter', -apple-system, sans-serif;

  /* Style khi bật Fullscreen */
  &:fullscreen {
    width: 100vw;
    height: 100vh;
    max-width: none;
    border-radius: 0;
    border: none;
  }
}

/* --- PROGRESS BAR --- */
.progress-track-lux { height: 6px; background: #f1f5f9; width: 100%; position: relative; }
.progress-fill-lux { 
  height: 100%; background: $brand-gradient;
  transition: width 0.6s cubic-bezier(0.22, 1, 0.36, 1);
  background-size: 200% 200%;
  animation: gradientMove 3s ease infinite;
}

/* --- HEADER LUXURY --- */
.modal-header-lux {
  padding: 20px 40px; display: flex; justify-content: space-between; align-items: center;
  border-bottom: 1px solid $border-light; background: $white;
}

.header-left, .header-right { display: flex; align-items: center; gap: 24px; }

.step-pill { 
  background: #fff7ed; color: $brand-orange; padding: 8px 20px; border-radius: 100px;
  display: flex; align-items: baseline; gap: 10px; border: 1px solid #ffedd5;
}
.step-label { font-size: 11px; font-weight: 800; letter-spacing: 1.5px; color: #c2410c; }
.step-current { font-size: 26px; font-weight: 900; color: $brand-orange; line-height: 1; }
.step-total { font-size: 15px; font-weight: 700; color: #f97316; opacity: 0.8; }

.divider-v { width: 1px; height: 32px; background: $border-light; }

/* Font Zoom */
.size-controls { display: flex; align-items: center; gap: 12px; }
.ctrl-label { font-size: 13px; font-weight: 700; color: $text-sub; }
.size-btn { 
  width: 34px; height: 34px; border-radius: 10px; border: 1px solid $border-light; background: $white;
  color: $text-main; cursor: pointer; font-weight: 800; transition: 0.2s; display: flex; align-items: center; justify-content: center;
  &:hover { background: $bg-soft; color: $brand-orange; border-color: #cbd5e1; }
}
.size-indicator { display: flex; gap: 5px; }
.size-indicator .dot { width: 5px; height: 5px; border-radius: 50%; background: #cbd5e1; transition: 0.3s; }
.size-indicator .dot.active { background: $brand-orange; transform: scale(1.2); }

/* Tool Buttons */
.tool-btn-lux {
  padding: 10px 20px; border-radius: 12px; border: 1.5px solid $border-light;
  background: $white; color: $text-sub; cursor: pointer; transition: 0.3s;
  display: flex; align-items: center; gap: 8px; font-weight: 800; font-size: 12px; letter-spacing: 0.5px;
  &:hover { border-color: $brand-orange; color: $brand-orange; background: #fff7ed; }
  &.is-active { background: $brand-gradient; color: $white; border-color: transparent; box-shadow: 0 6px 15px rgba(234, 88, 12, 0.25); }
}

.mic-btn.listening { 
  border-color: #ef4444; color: #ef4444; background: #fef2f2; position: relative; 
  .listening-ring {
    position: absolute; inset: -4px; border: 2px solid #ef4444; border-radius: 16px;
    animation: ripple 1.5s infinite; opacity: 0.5;
  }
}

.btn-close-lux { 
  width: 40px; height: 40px; border-radius: 50%; background: $bg-soft; border: none; color: $text-sub; 
  cursor: pointer; transition: 0.3s; display: flex; align-items: center; justify-content: center;
  &:hover { background: #fee2e2; color: #ef4444; transform: scale(1.1); }
}

/* --- BODY SPLIT VIEW --- */
.modal-body-lux { flex: 1; background: $bg-soft; overflow: hidden; }
.content-grid-lux { 
  display: grid; grid-template-columns: 1fr 1fr; gap: 40px; padding: 40px; height: 100%;
}

.image-section { height: 100%; display: flex; align-items: center; justify-content: center; }
.image-card-luxury { 
  width: 100%; height: 100%; max-height: 60vh; border-radius: 24px; overflow: hidden;
  box-shadow: $shadow-soft; background: $white; border: 1px solid rgba(0,0,0,0.03);
  img { width: 100%; height: 100%; object-fit: cover; }
}
.image-placeholder { 
  height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center;
  background: $bg-soft; color: $text-sub; font-weight: 600;
  .placeholder-icon { font-size: 50px; margin-bottom: 15px; opacity: 0.5; animation: float 3s infinite ease-in-out; }
}

.text-section { height: 100%; display: flex; flex-direction: column; }
.instruction-container { 
  height: 100%; display: flex; flex-direction: column; background: $white; 
  padding: 40px; border-radius: 24px; border: 1px solid $border-light; box-shadow: 0 5px 20px rgba(0,0,0,0.02);
}
.instruction-title { 
  font-size: 13px; font-weight: 900; color: #94a3b8; letter-spacing: 2px; margin-bottom: 25px; text-transform: uppercase;
}
.instruction-text { flex: 1; overflow-y: auto; padding-right: 20px; }
.instruction-text p { 
  line-height: 1.7; color: $text-main; font-weight: 600; white-space: pre-line; transition: font-size 0.3s ease;
}

/* --- FOOTER LUXURY --- */
.modal-footer-lux { 
  padding: 20px 40px; display: flex; justify-content: space-between; align-items: center;
  background: $white; border-top: 1px solid $border-light;
}

.nav-btn {
  padding: 14px 32px; border-radius: 16px; border: none; font-weight: 800; font-size: 14px;
  cursor: pointer; transition: 0.3s; display: flex; align-items: center; gap: 12px; letter-spacing: 0.5px;
}
.btn-next { 
  background: $brand-gradient; color: $white; box-shadow: 0 8px 20px rgba(234, 88, 12, 0.25); 
  &:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 12px 25px rgba(234, 88, 12, 0.4); }
}
.btn-finish {
  background: $success-gradient; color: $white; box-shadow: 0 8px 20px rgba(22, 163, 74, 0.25);
  &:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 12px 25px rgba(22, 163, 74, 0.4); }
}
.btn-prev { 
  background: $bg-soft; color: $text-sub; border: 1px solid $border-light;
  &:hover:not(:disabled) { background: #e2e8f0; color: $text-main; }
}
.nav-btn:disabled { opacity: 0.5; cursor: not-allowed; }

/* Sound Wave Animation */
.voice-status { display: flex; align-items: center; gap: 15px; background: #fff7ed; padding: 10px 20px; border-radius: 100px; border: 1px solid #ffedd5;}
.sound-wave { display: flex; align-items: center; gap: 3px; height: 16px; }
.sound-wave .bar { width: 3px; height: 8px; background: $brand-orange; border-radius: 10px; animation: wave 1s infinite ease-in-out; }
.sound-wave .bar:nth-child(2) { animation-delay: 0.1s; }
.sound-wave .bar:nth-child(3) { animation-delay: 0.2s; }
.voice-command-text { font-size: 13px; font-weight: 500; color: #c2410c; strong { font-weight: 800; }}

/* --- ANIMATIONS --- */
@keyframes ripple { 0% { transform: scale(1); opacity: 1; } 100% { transform: scale(1.4); opacity: 0; } }
@keyframes wave { 0%, 100% { height: 6px; } 50% { height: 16px; } }
@keyframes gradientMove { 0% {background-position: 0% 50%} 50% {background-position: 100% 50%} 100% {background-position: 0% 50%} }
@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-10px); } }

/* Transition Animations */
.slide-right-enter-from { opacity: 0; transform: translateX(30px); }
.slide-right-leave-to { opacity: 0; transform: translateX(-30px); }

.slide-left-enter-from { opacity: 0; transform: translateX(-30px); }
.slide-left-leave-to { opacity: 0; transform: translateX(30px); }

.slide-right-enter-active, .slide-right-leave-active, .slide-left-enter-active, .slide-left-leave-active { 
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.fade-modal-enter-from, .fade-modal-leave-to { opacity: 0; transform: scale(0.98) translateY(10px); }
.fade-modal-enter-active, .fade-modal-leave-active { transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); }

/* Custom Scroll */
.custom-scroll::-webkit-scrollbar { width: 6px; }
.custom-scroll::-webkit-scrollbar-track { background: transparent; }
.custom-scroll::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.custom-scroll::-webkit-scrollbar-thumb:hover { background: #94a3b8; }

/* --- RESPONSIVE --- */
@media (max-width: 1024px) {
  .content-grid-lux { grid-template-columns: 1fr; overflow-y: auto; padding: 25px; gap: 25px; }
  .cook-modal-lux { height: 95vh; }
  .image-card-luxury { max-height: 40vh; }
  .modal-header-lux { flex-direction: column; align-items: flex-start; gap: 15px; padding: 20px 25px; }
  .header-right { width: 100%; justify-content: space-between; }
  .divider-v { display: none; }
  .modal-footer-lux { padding: 20px 25px; flex-wrap: wrap; gap: 15px; justify-content: center; }
  .voice-status { order: -1; width: 100%; justify-content: center; }
}
</style>