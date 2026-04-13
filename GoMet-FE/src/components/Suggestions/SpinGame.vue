<template>
  <div class="game-stage premium-game">
    <div class="machine-container">
      
      <div class="dish-frame" :class="{ 'is-revealing': isRevealing }">
        <div class="scan-line" v-if="isSpinning"></div>
        <img :src="currentDisplayDish.image" class="dish-img" :class="{ 'blur-motion': isSpinning }" />
        
        <div class="flash-overlay" v-if="isRevealing"></div>
        
        <transition name="fade">
          <div class="dish-tag" v-if="!isSpinning && !isRevealing">
            {{ currentDisplayDish.category }}
          </div>
        </transition>
      </div>
      
      <h2 class="dish-name-display" :class="{ 'text-glitch': isSpinning, 'text-gold': !isSpinning }">
        {{ scrambledText }}
      </h2>

      <button class="btn-spin-premium" @click="spinWheel" :disabled="isSpinning || isRevealing">
        <span v-if="!isSpinning">{{ $t('suggestions.spin_btn') }}</span>
        <span v-else class="spinning-text">
          {{ $t('suggestions.spin_searching_premium') }} <span class="dots">...</span>
        </span>
      </button>
      
      <p class="hint-text">"{{ $t('suggestions.spin_hint_premium') }}"</p>
      
      <audio ref="tickSound" src="/sounds/tick.mp3" preload="auto"></audio>
      <audio ref="revealSound" src="/sounds/reveal.mp3" preload="auto"></audio>
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import i18n from '@/i18n'

const props = defineProps({
  dishes: { type: Array, required: true }
})
const emit = defineEmits(['finish'])

const isSpinning = ref(false)
const isRevealing = ref(false) // Trạng thái chớp sáng khi dừng
const currentDisplayDish = ref(props.dishes[0] || {})
const scrambledText = ref(currentDisplayDish.value.name || i18n.global.t('suggestions.spin_ready'))

let spinTimeout = null

// Ký tự ngẫu nhiên để làm hiệu ứng Scramble Text
const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%&*!'

const generateScramble = (length) => {
  let res = ''
  for (let i = 0; i < length; i++) res += chars[Math.floor(Math.random() * chars.length)]
  return res
}

const spinWheel = () => {
  if (isSpinning.value || props.dishes.length === 0) return
  
  isSpinning.value = true
  isRevealing.value = false
  
  let currentStep = 0
  const totalSteps = 40 // Tổng số lần lật ảnh
  let baseDelay = 30 // Tốc độ ban đầu (rất nhanh)

  const step = () => {
    // Đổi món ngẫu nhiên
    currentDisplayDish.value = props.dishes[Math.floor(Math.random() * props.dishes.length)]
    
    // Hiệu ứng chữ nhiễu loạn
    scrambledText.value = generateScramble(currentDisplayDish.value.name.length || 10)

    currentStep++

    // Thuật toán làm chậm dần (Easing ease-out)
    // Càng về cuối, delay càng tăng mạnh tạo sự hồi hộp
    const progress = currentStep / totalSteps
    let nextDelay = baseDelay + (Math.pow(progress, 3) * 400) 

    if (currentStep < totalSteps) {
      spinTimeout = setTimeout(step, nextDelay)
    } else {
      // HOÀN THÀNH VÒNG QUAY
      isSpinning.value = false
      isRevealing.value = true // Kích hoạt chớp sáng
      scrambledText.value = currentDisplayDish.value.name // Hiển thị tên thật

      // Đợi hiệu ứng chớp sáng (flash) xong rồi mới emit kết quả mở Modal
      setTimeout(() => {
        isRevealing.value = false
        emit('finish', currentDisplayDish.value)
      }, 1200)
    }
  }

  // Bắt đầu quay
  step()
}

// Xóa timeout nếu user rời trang giữa chừng
onUnmounted(() => {
  if (spinTimeout) clearTimeout(spinTimeout)
})
</script>

<style scoped lang="scss">
$gold: #D4AF37;
$orange: #EA580C;

.premium-game { flex: 1; display: flex; justify-content: center; align-items: center; width: 100%; }
.machine-container { text-align: center; display: flex; flex-direction: column; align-items: center; }

.dish-frame {
  width: 350px; height: 350px; position: relative; border-radius: 50%; overflow: hidden; margin-bottom: 40px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.8), inset 0 0 0 2px rgba(212, 175, 55, 0.4); 
  transition: all 0.5s cubic-bezier(0.19, 1, 0.22, 1);
  
  &.is-revealing {
    transform: scale(1.05);
    box-shadow: 0 0 80px rgba(212, 175, 55, 0.6), inset 0 0 0 4px $gold;
  }
}

.dish-img { width: 100%; height: 100%; object-fit: cover; transition: filter 0.1s, transform 0.1s; }
.blur-motion { filter: blur(8px) contrast(1.2); transform: scale(1.1) rotate(5deg); opacity: 0.8; }

/* Lớp phủ chớp sáng khi chốt kết quả */
.flash-overlay {
  position: absolute; inset: 0; background: white; z-index: 10;
  animation: flash 1s cubic-bezier(0.19, 1, 0.22, 1) forwards;
  border-radius: 50%;
}
@keyframes flash { 0% { opacity: 1; } 100% { opacity: 0; display: none; } }

.scan-line {
  position: absolute; top: 0; left: 0; width: 100%; height: 4px; background: $gold; z-index: 5;
  box-shadow: 0 0 30px $gold, 0 0 10px white; animation: scan 1s infinite linear;
}
@keyframes scan { 0% { top: -10%; opacity: 0; } 10% { opacity: 1; } 90% { opacity: 1; } 100% { top: 110%; opacity: 0; } }

.dish-tag {
  position: absolute; bottom: 25px; left: 50%; transform: translateX(-50%);
  background: rgba(5,5,5,0.85); color: $gold; padding: 6px 20px; backdrop-filter: blur(8px);
  border-radius: 30px; font-size: 0.75rem; font-weight: 800; letter-spacing: 3px; 
  border: 1px solid rgba(212,175,55,0.5); text-transform: uppercase;
}

.dish-name-display { 
  font-family: 'Playfair Display', serif; font-size: 3rem; margin: 0 0 40px; 
  color: #F4F0EA; min-height: 4rem; letter-spacing: 1px; transition: color 0.3s;
  
  &.text-glitch { font-family: monospace; font-size: 2.2rem; color: rgba(255,255,255,0.4); letter-spacing: 5px; }
  &.text-gold { color: $gold; text-shadow: 0 0 20px rgba(212, 175, 55, 0.3); }
}

.btn-spin-premium {
  padding: 20px 60px; border: 1px solid $gold; background: rgba(212, 175, 55, 0.05); border-radius: 50px;
  color: $gold; font-weight: 800; font-size: 1rem; letter-spacing: 3px; text-transform: uppercase;
  cursor: pointer; transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1); backdrop-filter: blur(10px); position: relative; overflow: hidden;
  
  &::before {
    content: ''; position: absolute; top: 0; left: -100%; width: 50%; height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent); transform: skewX(-20deg); transition: 0.5s;
  }
  
  &:hover:not(:disabled) { 
    background: $gold; color: #000; box-shadow: 0 10px 40px rgba(212, 175, 55, 0.4); transform: translateY(-3px); 
    &::before { left: 150%; }
  }
  
  &:disabled { background: transparent; border-color: rgba(255,255,255,0.1); color: rgba(255,255,255,0.3); cursor: wait; }
}

.spinning-text .dots { animation: dots 1.5s steps(4, end) infinite; }
@keyframes dots { 0%, 20% { color: transparent; text-shadow: .25em 0 0 transparent, .5em 0 0 transparent;} 40% { color: inherit; text-shadow: .25em 0 0 transparent, .5em 0 0 transparent;} 60% { text-shadow: .25em 0 0 inherit, .5em 0 0 transparent;} 80%, 100% { text-shadow: .25em 0 0 inherit, .5em 0 0 inherit;} }

.hint-text { margin-top: 25px; font-size: 0.95rem; color: rgba(255,255,255,0.3); font-style: italic; font-family: 'Playfair Display', serif; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.5s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>