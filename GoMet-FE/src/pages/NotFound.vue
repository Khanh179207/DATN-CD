<template>
  <div class="not-found-wrapper" @mousemove="handleMouseMove">
    <div class="ambient-blob blob-1"></div>
    <div class="ambient-blob blob-2"></div>
    <div class="bg-grain"></div>

    <div class="floating-ingredients" ref="ingredientsRef">
      <div class="ingredient item-1">🌿</div>
      <div class="ingredient item-2">🌶️</div>
      <div class="ingredient item-3">🔪</div>
      <div class="ingredient item-4">🍽️</div>
      <div class="ingredient item-5">✨</div>
    </div>

    <div class="glass-card" ref="cardRef">
      <div class="error-code" ref="codeRef">
        <span class="digit">4</span>
        <span class="digit zero">0</span>
        <span class="digit">4</span>
      </div>
      
      <div class="text-group" ref="textGroupRef">
        <h1 class="title">Ối! Bạn đi lạc vào kho nguyên liệu rồi.</h1>
        <p class="subtitle">
          Món ăn bạn đang tìm kiếm không tồn tại, chưa kịp lên bếp, hoặc đã được gỡ khỏi thực đơn tinh hoa của GOMET.
        </p>
      </div>

      <button ref="btnRef" @click="goHome" class="btn-mega">
        <span class="btn-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
            <polyline points="9 22 9 12 15 12 15 22"></polyline>
          </svg>
        </span>
        Trở về Bếp chính
        <div class="btn-glow"></div>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import gsap from 'gsap'

const router = useRouter()

// Refs cho GSAP
const cardRef = ref(null)
const codeRef = ref(null)
const textGroupRef = ref(null)
const btnRef = ref(null)
const ingredientsRef = ref(null)

const goHome = () => {
  router.push('/home') // Đổi lại route trang chủ nếu cần
}

// Hiệu ứng Parallax nhẹ khi rê chuột
const handleMouseMove = (e) => {
  const { clientX, clientY } = e
  const xPos = (clientX / window.innerWidth - 0.5) * 40
  const yPos = (clientY / window.innerHeight - 0.5) * 40

  gsap.to(ingredientsRef.value.children, {
    x: xPos,
    y: yPos,
    stagger: -0.05,
    ease: 'power1.out',
    duration: 1
  })
}

onMounted(() => {
  const tl = gsap.timeline()

  // 1. Box kính trượt lên
  tl.from(cardRef.value, {
    y: 60,
    opacity: 0,
    duration: 1.2,
    ease: 'expo.out'
  })

  // 2. Chữ 404 giật từng số (Stagger)
  .from(codeRef.value.children, {
    y: -80,
    opacity: 0,
    rotationX: 90,
    duration: 1,
    stagger: 0.15,
    ease: 'back.out(1.7)'
  }, '-=0.8')

  // 3. Nội dung chữ hiện ra
  .from(textGroupRef.value.children, {
    y: 20,
    opacity: 0,
    duration: 0.8,
    stagger: 0.2,
    ease: 'power3.out'
  }, '-=0.6')

  // 4. Nút bấm xuất hiện
  .from(btnRef.value, {
    scale: 0.9,
    opacity: 0,
    duration: 0.6,
    ease: 'back.out(1.5)'
  }, '-=0.4')

  // 5. Các icon nguyên liệu bung ra xung quanh
  .from(ingredientsRef.value.children, {
    scale: 0,
    opacity: 0,
    rotation: -180,
    duration: 1,
    stagger: 0.1,
    ease: 'back.out(1.5)'
  }, '-=1.2')

  // --- ANIMATION LƠ LỬNG VÔ HẠN ---
  
  // Lơ lửng số 0 (như cái đĩa/bánh)
  gsap.to(codeRef.value.children[1], {
    y: -15,
    rotation: 5,
    duration: 2.5,
    repeat: -1,
    yoyo: true,
    ease: 'sine.inOut'
  })

  // Lơ lửng các icon nguyên liệu
  gsap.utils.toArray(ingredientsRef.value.children).forEach((el, index) => {
    gsap.to(el, {
      y: `+=${15 + index * 5}`,
      rotation: `+=${10 + index * 5}`,
      duration: 2.5 + index * 0.2,
      repeat: -1,
      yoyo: true,
      ease: 'sine.inOut',
      delay: index * 0.2
    })
  })
})
</script>

<style scoped>
.not-found-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #111111; /* Đổi sang nền Dark mode sang trọng */
  font-family: 'Mulish', sans-serif;
  position: relative;
  overflow: hidden;
  perspective: 1000px;
}

/* --- BACKGROUND HIỆU ỨNG --- */
.ambient-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  opacity: 0.4;
  pointer-events: none;
}
.blob-1 {
  width: 600px; height: 600px;
  background: #EA580C;
  top: -200px; left: -200px;
  animation: float-blob 10s ease-in-out infinite alternate;
}
.blob-2 {
  width: 500px; height: 500px;
  background: #9A3412;
  bottom: -150px; right: -150px;
  animation: float-blob 12s ease-in-out infinite alternate-reverse;
}

@keyframes float-blob {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(50px, 50px) scale(1.1); }
}

.bg-grain {
  position: absolute; inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.7' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='200' height='200' filter='url(%23n)' opacity='0.05'/%3E%3C/svg%3E");
  pointer-events: none;
  z-index: 1;
}

/* --- ICON LƠ LỬNG --- */
.floating-ingredients {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
}
.ingredient {
  position: absolute;
  font-size: 2.5rem;
  filter: drop-shadow(0 10px 15px rgba(0,0,0,0.3));
}
.item-1 { top: 20%; left: 15%; font-size: 3rem; }
.item-2 { bottom: 25%; left: 20%; font-size: 2.5rem; }
.item-3 { top: 15%; right: 18%; font-size: 2.8rem; transform: rotate(45deg); }
.item-4 { bottom: 20%; right: 15%; font-size: 3.5rem; }
.item-5 { top: 40%; right: 10%; font-size: 1.5rem; }

/* --- GLASS CARD --- */
.glass-card {
  position: relative;
  z-index: 10;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 40px;
  padding: 60px 40px;
  max-width: 650px;
  text-align: center;
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

/* --- ERROR CODE 404 --- */
.error-code {
  display: flex;
  justify-content: center;
  gap: 10px;
  font-size: 11rem;
  font-weight: 900;
  line-height: 1;
  margin-bottom: 20px;
  font-family: 'Playfair Display', serif;
}

.digit {
  background: linear-gradient(135deg, #FFFFFF 0%, #D6D3D1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: inline-block;
  text-shadow: 0 20px 40px rgba(0,0,0,0.5);
}

/* Số 0 được làm nổi bật màu cam GOMET */
.digit.zero {
  background: linear-gradient(135deg, #EA580C, #F97316);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* --- TEXT CONTENT --- */
.title {
  font-family: 'Playfair Display', serif;
  font-size: 2.2rem;
  color: #FFFFFF;
  font-weight: 800;
  margin: 0 0 16px 0;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 1.1rem;
  color: #A8A29E;
  line-height: 1.7;
  margin: 0 0 40px 0;
  max-width: 500px;
  margin-inline: auto;
}

/* --- NÚT BẤM MEGA --- */
.btn-mega {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 16px 36px;
  background: #EA580C;
  color: #FFFFFF;
  border: none;
  border-radius: 100px;
  font-size: 1.05rem;
  font-weight: 800;
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 15px 30px -5px rgba(234, 88, 12, 0.4);
}

.btn-icon svg {
  width: 22px;
  height: 22px;
}

.btn-glow {
  position: absolute;
  top: 0; left: -100%;
  width: 100%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: all 0.5s ease;
}

.btn-mega:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 20px 40px -10px rgba(234, 88, 12, 0.6);
}

.btn-mega:hover .btn-glow {
  left: 100%;
}

.btn-mega:active {
  transform: translateY(1px);
}

/* --- RESPONSIVE --- */
@media (max-width: 768px) {
  .glass-card { padding: 40px 20px; border-radius: 30px; margin: 0 20px; }
  .error-code { font-size: 7.5rem; }
  .title { font-size: 1.8rem; }
  .subtitle { font-size: 1rem; }
  .ingredient { font-size: 2rem !important; }
}
</style>