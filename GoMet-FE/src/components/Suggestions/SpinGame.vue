<template>
  <div class="game-stage premium-game">
    <div class="machine-container">
      
      <div class="machine-core">
        
        <div class="mechanical-ring" :class="{ 'is-spinning': isSpinning }">
          <div class="tick" v-for="i in 24" :key="i" :style="{ transform: `rotate(${i * 15}deg)` }"></div>
        </div>

        <div class="dish-frame" :class="{ 'is-revealing': isRevealing }">
          <div class="scan-line" v-if="isSpinning"></div>
          
          <img 
            :src="currentDisplayDish.image" 
            class="dish-img" 
            :class="{ 'blur-motion': isSpinning }" 
            alt=""
          />
          
          <div class="flash-overlay" v-if="isRevealing"></div>
          
          <transition name="fade">
            <div class="dish-category-tag" v-if="!isSpinning && !isRevealing">
              {{ currentDisplayDish.category || 'Hương Vị Mới' }}
            </div>
          </transition>
        </div>
        
        <div class="machine-glow"></div>
      </div>
      
      <div class="text-display-zone">
        <div class="subtitle-hint" v-if="!isSpinning">Định Mệnh Của Bạn Là</div>
        <div class="subtitle-hint pulse" v-else>Hệ Thống Đang Trích Xuất...</div>
        
        <h2 class="dish-name-display" :class="{ 'text-glitch': isSpinning, 'text-gold': !isSpinning }">
          {{ displayName }}
        </h2>
      </div>

      <div class="action-zone">
        <button class="btn-spin-premium" @click="startSpin" :disabled="isSpinning || isRevealing">
          <div class="btn-content">
            <i class="fas fa-fingerprint" v-if="!isSpinning"></i>
            <i class="fas fa-sync-alt fa-spin" v-else></i>
            <span v-if="!isSpinning">Kích Hoạt Vòng Quay</span>
            <span v-else>Đang Phân Tích</span>
          </div>
          <div class="btn-glare"></div>
        </button>
        <p class="hint-text">"Hãy để vũ trụ chọn lựa hương vị cho bạn hôm nay."</p>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted, onMounted } from 'vue'
import { gsap } from 'gsap'

const props = defineProps({
  dishes: { type: Array, required: true }
})
const emit = defineEmits(['finish'])

const isSpinning = ref(false)
const isRevealing = ref(false)
const currentDisplayDish = ref(props.dishes[0] || { name: 'Chưa có dữ liệu', image: '' })
const displayName = ref('Sẵn Sàng Khám Phá')

let spinInterval = null

// Animation xuất hiện ban đầu
onMounted(() => {
  gsap.fromTo('.machine-core', 
    { scale: 0.8, opacity: 0, rotation: -45 }, 
    { scale: 1, opacity: 1, rotation: 0, duration: 1.2, ease: "back.out(1.5)" }
  )
  gsap.fromTo(['.text-display-zone', '.action-zone'], 
    { y: 30, opacity: 0 }, 
    { y: 0, opacity: 1, duration: 1, stagger: 0.2, ease: "power3.out" }
  )
})

const startSpin = () => {
  if (isSpinning.value || props.dishes.length === 0) return
  
  isSpinning.value = true
  isRevealing.value = false
  
  let currentStep = 0
  const totalSteps = 45 
  let baseDelay = 20 

  const performStep = () => {
    // 1. Chọn ngẫu nhiên một món ăn từ props.dishes
    const randomIndex = Math.floor(Math.random() * props.dishes.length)
    const randomDish = props.dishes[randomIndex]
    
    // 2. Cập nhật ảnh và TÊN món ăn (Thay vì ký tự lạ)
    currentDisplayDish.value = randomDish
    displayName.value = randomDish.name 

    currentStep++

    const progress = currentStep / totalSteps
    const nextDelay = baseDelay + (Math.pow(progress, 4) * 500) 

    if (currentStep < totalSteps) {
      spinInterval = setTimeout(performStep, nextDelay)
    } else {
      finalizeSpin()
    }
  }

  performStep()
}

const finalizeSpin = () => {
  isSpinning.value = false
  isRevealing.value = true
  
  // Hiển thị tên món ăn cuối cùng
  // Ở đây chúng ta bỏ hiệu ứng "Decode" ký tự lạ để giữ tính nhất quán
  const finalDish = currentDisplayDish.value
  displayName.value = finalDish.name

  // Hiệu ứng nhẹ cho chữ khi dừng lại
  gsap.fromTo('.dish-name-display', 
    { scale: 0.9, opacity: 0.5 }, 
    { scale: 1, opacity: 1, duration: 0.5, ease: "back.out(2)" }
  )

  // Đợi chớp sáng xong thì gọi Modal Kết Quả
  setTimeout(() => {
    isRevealing.value = false
    emit('finish', finalDish)
  }, 1500)
}

onUnmounted(() => {
  if (spinInterval) clearTimeout(spinInterval)
})
</script>

<style scoped lang="scss">
$gold: #D4AF37;
$orange: #EA580C;
$bg-deep: #09090b;

.premium-game { 
  flex: 1; display: flex; justify-content: center; align-items: center; 
  width: 100%; position: relative; font-family: 'Mulish', sans-serif;
  padding: 20px;
}

.machine-container { 
  text-align: center; display: flex; flex-direction: column; align-items: center; 
  width: 100%; max-width: 800px;
}

/* ================== CỖ MÁY VÒNG QUAY ================== */
.machine-core {
  position: relative; width: 400px; height: 400px; margin-bottom: 50px;
  display: flex; align-items: center; justify-content: center;
}

.machine-glow {
  position: absolute; inset: -20px; border-radius: 50%;
  background: radial-gradient(circle, rgba($gold, 0.15) 0%, transparent 70%);
  filter: blur(20px); z-index: 0; pointer-events: none;
}

/* Vành đai cơ khí */
.mechanical-ring {
  position: absolute; inset: 0; border-radius: 50%;
  border: 2px solid rgba($gold, 0.2); box-shadow: 0 0 30px rgba(0,0,0,0.8);
  z-index: 1; transition: 0.3s;
  
  .tick {
    position: absolute; top: 0; left: 50%; width: 2px; height: 10px;
    background: rgba($gold, 0.5); transform-origin: 0 200px; /* Tâm quay ở giữa */
  }

  &.is-spinning {
    animation: spinRing 2s cubic-bezier(0.2, 0.8, 0.2, 1) infinite;
    border-color: rgba($gold, 0.6); box-shadow: 0 0 50px rgba($gold, 0.2);
    .tick { background: $gold; box-shadow: 0 0 5px $gold;}
  }
}
@keyframes spinRing { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* Khung Ảnh Chính */
.dish-frame {
  width: 340px; height: 340px; position: relative; border-radius: 50%; overflow: hidden;
  box-shadow: inset 0 0 0 4px $bg-deep, inset 0 0 0 6px rgba($gold, 0.5); 
  background: #111; z-index: 5; transition: all 0.5s cubic-bezier(0.19, 1, 0.22, 1);
  
  &.is-revealing {
    transform: scale(1.08); box-shadow: 0 0 80px rgba($gold, 0.5), inset 0 0 0 6px $gold;
  }
}

.dish-img { 
  width: 100%; height: 100%; object-fit: cover; transition: filter 0.1s, transform 0.1s; 
  opacity: 0.9;
}
.blur-motion { 
  filter: blur(8px) contrast(1.3) saturate(1.5); 
  transform: scale(1.15) rotate(5deg); opacity: 0.7; 
}

/* Vạch quét Laser */
.scan-line {
  position: absolute; top: 0; left: 0; width: 100%; height: 3px; background: $gold; z-index: 6;
  box-shadow: 0 0 20px $gold, 0 0 40px $gold; animation: scan 0.8s infinite linear;
}
@keyframes scan { 0% { top: -10%; opacity: 0; } 10% { opacity: 1; } 90% { opacity: 1; } 100% { top: 110%; opacity: 0; } }

/* Flash chớp sáng */
.flash-overlay {
  position: absolute; inset: 0; background: #fff; z-index: 10;
  animation: flash 1.2s cubic-bezier(0.19, 1, 0.22, 1) forwards;
}
@keyframes flash { 0% { opacity: 1; } 100% { opacity: 0; display: none; } }

/* Nhãn Món Ăn */
.dish-category-tag {
  position: absolute; bottom: 30px; left: 50%; transform: translateX(-50%);
  background: rgba(10,10,12,0.85); color: $gold; padding: 8px 25px; backdrop-filter: blur(10px);
  border-radius: 30px; font-size: 0.75rem; font-weight: 800; letter-spacing: 3px; 
  border: 1px solid rgba($gold,0.4); text-transform: uppercase; box-shadow: 0 10px 20px rgba(0,0,0,0.5);
}

/* ================== KHU VỰC HIỂN THỊ CHỮ ================== */
.text-display-zone { margin-bottom: 40px; height: 120px; display: flex; flex-direction: column; justify-content: center;}

.subtitle-hint {
  font-size: 0.85rem; color: rgba(255,255,255,0.5); letter-spacing: 4px; text-transform: uppercase; font-weight: 800; margin-bottom: 10px;
  &.pulse { color: $gold; animation: pulseText 1s infinite alternate; }
}
@keyframes pulseText { 0% { opacity: 0.5; } 100% { opacity: 1; text-shadow: 0 0 10px rgba($gold, 0.5); } }

.dish-name-display { 
  font-family: 'Playfair Display', serif; font-size: 3.5rem; margin: 0; 
  color: #fff; letter-spacing: -1px; transition: color 0.3s; line-height: 1.1;
  
  &.text-glitch { font-family: 'Courier New', Courier, monospace; font-size: 2.2rem; color: rgba($gold, 0.6); letter-spacing: 5px; font-weight: 400;}
  &.text-gold { color: $gold; text-shadow: 0 5px 25px rgba($gold, 0.4); }
}

/* ================== NÚT BẤM ================== */
.action-zone { display: flex; flex-direction: column; align-items: center; gap: 20px; }

.btn-spin-premium {
  position: relative; overflow: hidden;
  padding: 2px; border-radius: 50px; background: linear-gradient(135deg, rgba($gold, 0.5), transparent);
  border: none; cursor: pointer; transition: 0.4s cubic-bezier(0.19, 1, 0.22, 1);
  box-shadow: 0 15px 35px rgba(0,0,0,0.5);
  
  .btn-content {
    background: #111; padding: 18px 50px; border-radius: 48px;
    display: flex; align-items: center; gap: 15px;
    color: $gold; font-weight: 800; font-size: 1rem; letter-spacing: 3px; text-transform: uppercase;
    transition: 0.4s;
  }
  
  .btn-glare {
    position: absolute; top: 0; left: -100%; width: 50%; height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent); 
    transform: skewX(-20deg); transition: 0.5s; z-index: 5; pointer-events: none;
  }
  
  &:hover:not(:disabled) { 
    transform: translateY(-5px); box-shadow: 0 20px 40px rgba($gold, 0.2);
    .btn-content { background: $gold; color: #000; }
    .btn-glare { left: 150%; transition: 0.8s; }
  }
  
  &:disabled { 
    opacity: 0.6; cursor: not-allowed; filter: grayscale(1);
    .btn-content { color: rgba(255,255,255,0.5); }
  }
}

.hint-text { font-size: 1rem; color: rgba(255,255,255,0.4); font-style: italic; font-family: 'Playfair Display', serif; margin: 0;}

/* =======================================================
   🔥 RESPONSIVE
======================================================= */
@media (max-width: 1200px) {
  .machine-core { width: 350px; height: 350px; margin-bottom: 40px; }
  .dish-frame { width: 290px; height: 290px; }
  .mechanical-ring .tick { transform-origin: 0 175px; }
  .dish-name-display { font-size: 3rem; }
}

@media (max-width: 992px) {
  .machine-core { width: 300px; height: 300px; margin-bottom: 30px; }
  .dish-frame { width: 250px; height: 250px; }
  .mechanical-ring .tick { transform-origin: 0 150px; }
  
  .text-display-zone { height: 100px; margin-bottom: 30px; }
  .dish-name-display { font-size: 2.5rem; }
  .dish-name-display.text-glitch { font-size: 1.8rem; letter-spacing: 3px; }
  
  .btn-spin-premium .btn-content { padding: 16px 40px; font-size: 0.9rem; }
}

@media (max-width: 768px) {
  .premium-game { padding: 10px; }
  
  .machine-core { width: 260px; height: 260px; margin-bottom: 25px; }
  .dish-frame { width: 220px; height: 220px; box-shadow: inset 0 0 0 3px $bg-deep, inset 0 0 0 5px rgba($gold, 0.5); }
  .mechanical-ring .tick { transform-origin: 0 130px; height: 6px;}
  
  .text-display-zone { height: 90px; }
  .subtitle-hint { font-size: 0.75rem; letter-spacing: 2px; }
  .dish-name-display { font-size: 2.2rem; }
  .dish-name-display.text-glitch { font-size: 1.5rem; letter-spacing: 2px; }
  
  .dish-category-tag { padding: 6px 15px; font-size: 0.65rem; bottom: 20px; }
  
  .btn-spin-premium { width: 100%; max-width: 320px; }
  .btn-spin-premium .btn-content { padding: 14px 20px; justify-content: center;}
  
  .hint-text { font-size: 0.85rem; padding: 0 15px; }
}

@media (max-width: 480px) {
  .machine-core { width: 220px; height: 220px; margin-bottom: 20px; }
  .dish-frame { width: 180px; height: 180px; }
  .mechanical-ring .tick { transform-origin: 0 110px; }
  
  .text-display-zone { height: 80px; margin-bottom: 20px;}
  .dish-name-display { font-size: 1.8rem; }
  .dish-name-display.text-glitch { font-size: 1.2rem; letter-spacing: 1px; }
  
  .btn-spin-premium .btn-content { font-size: 0.8rem; letter-spacing: 1.5px; }
}
</style>