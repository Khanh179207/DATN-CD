<template>
  <div class="game-stage premium-game">
    
    <div class="quiz-container">
      
      <div class="quiz-progress-wrap">
        <span class="step-label" :class="{ 'glow-text-sub': quizStep <= 3 }">
          GIAI ĐOẠN {{ quizStep <= 3 ? quizStep : 'KẾT QUẢ' }} <span class="divider">/</span> 3
        </span>
        <div class="quiz-progress">
          <div class="progress-bar" :style="{ width: `${(quizStep / 3) * 100}%` }">
            <div class="progress-glow"></div>
          </div>
        </div>
      </div>

      <div class="quiz-content">
        <div class="gsap-step-container" ref="stepContainer">
          
          <div class="question-wrapper">
            <h2 class="quiz-question" v-if="quizStep === 1">Bạn đang thèm <br><em>nước dùng</em> hay món <em>khô</em>?</h2>
            <h2 class="quiz-question" v-if="quizStep === 2">Hương vị nào <br>sẽ đánh thức <em>vị giác</em> của bạn?</h2>
            <h2 class="quiz-question" v-if="quizStep === 3">Thành phần <br>chính <em>hôm nay</em> là gì?</h2>
            
            <div class="processing-wrapper" v-if="quizStep === 4">
              <h2 class="quiz-question processing-text">Đang trích xuất tuyệt tác...</h2>
            </div>
          </div>

          <div class="quiz-options" v-if="quizStep < 4">
            <template v-if="quizStep === 1">
              <button class="opt-btn-premium gsap-btn" @click="handleAnswer('style', 'Dry')">
                <div class="btn-inner-glass">
                  <div class="opt-icon"><i class="fas fa-fire"></i></div>
                  <span class="btn-text">Món Khô Đậm Vị</span>
                </div>
              </button>
              <button class="opt-btn-premium gsap-btn" @click="handleAnswer('style', 'Soupy')">
                <div class="btn-inner-glass">
                  <div class="opt-icon"><i class="fas fa-water"></i></div>
                  <span class="btn-text">Nước Dùng Thanh Tao</span>
                </div>
              </button>
            </template>

            <template v-if="quizStep === 2">
              <button class="opt-btn-premium gsap-btn" @click="handleAnswer('flavor', 'Bold')">
                <div class="btn-inner-glass">
                  <div class="opt-icon"><i class="fas fa-pepper-hot"></i></div>
                  <span class="btn-text">Đậm Đà & Cay Nồng</span>
                </div>
              </button>
              <button class="opt-btn-premium gsap-btn" @click="handleAnswer('flavor', 'Mild')">
                <div class="btn-inner-glass">
                  <div class="opt-icon"><i class="fas fa-leaf"></i></div>
                  <span class="btn-text">Nhẹ Nhàng & Tinh Tế</span>
                </div>
              </button>
            </template>

            <template v-if="quizStep === 3">
              <button class="opt-btn-premium gsap-btn" @click="handleAnswer('main', 'Meat')">
                <div class="btn-inner-glass">
                  <div class="opt-icon"><i class="fas fa-drumstick-bite"></i></div>
                  <span class="btn-text">Thịt Đỏ Tươi Mềm</span>
                </div>
              </button>
              <button class="opt-btn-premium gsap-btn" @click="handleAnswer('main', 'Seafood')">
                <div class="btn-inner-glass">
                  <div class="opt-icon"><i class="fas fa-fish"></i></div>
                  <span class="btn-text">Hải Sản Tươi Ngọt</span>
                </div>
              </button>
            </template>
          </div>

          <div class="processing-radar" v-if="quizStep === 4">
            <div class="radar-core"></div>
            <div class="radar-sweep"></div>
            <div class="radar-ring primary"></div>
            <div class="radar-ring secondary"></div>
            <div class="data-particles">
              <span v-for="i in 12" :key="i" class="p-dot" :style="{ '--delay': `${i * 0.15}s`, '--angle': `${i * 30}deg` }"></span>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { gsap } from 'gsap'

const props = defineProps({ dishes: { type: Array, required: true } })
const emit = defineEmits(['finish'])

const quizStep = ref(1)
const quizAnswers = ref({})
const stepContainer = ref(null)

// Animation xuất hiện mượt mà
const animateIn = () => {
  const tl = gsap.timeline({ defaults: { ease: "power3.out" } })
  tl.fromTo(".quiz-question", 
      { opacity: 0, y: 20, filter: "blur(5px)" },
      { opacity: 1, y: 0, filter: "blur(0px)", duration: 0.8 }
    )
    .fromTo(".gsap-btn", 
      { opacity: 0, y: 30, scale: 0.95 },
      { opacity: 1, y: 0, scale: 1, duration: 0.6, stagger: 0.1, clearProps: "all" },
      "-=0.4"
    )
}

// Animation ẩn đi
const animateOut = (callback) => {
  const tl = gsap.timeline({
    onComplete: () => {
      gsap.set(".gsap-step-container", { opacity: 1, y: 0 })
      callback()
    }
  })
  
  tl.to(".gsap-btn", { opacity: 0, y: -20, scale: 0.95, stagger: 0.05, duration: 0.3, ease: "power2.in" })
    .to(".quiz-question", { opacity: 0, y: -20, filter: "blur(5px)", duration: 0.3, ease: "power2.in" }, "-=0.2")
}

const handleAnswer = (key, val) => {
  quizAnswers.value[key] = val
  
  animateOut(async () => {
    if (quizStep.value < 3) {
      quizStep.value++
      await nextTick()
      animateIn()
    } else {
      quizStep.value = 4
      await nextTick()
      
      // Animation Radar xử lý
      gsap.fromTo(".processing-wrapper", 
        { opacity: 0, scale: 0.95, filter: "blur(5px)" }, 
        { opacity: 1, scale: 1, filter: "blur(0px)", duration: 0.8, ease: "power2.out" }
      )
      gsap.fromTo(".processing-radar",
        { opacity: 0, scale: 0.8, rotation: -90 },
        { opacity: 1, scale: 1, rotation: 0, duration: 1, ease: "power3.out" }
      )
      
      // Xử lý và Emit kết quả ra ngoài (Để file cha gọi SuggestionCard)
      setTimeout(() => {
        if (props.dishes && props.dishes.length > 0) {
           const result = props.dishes[Math.floor(Math.random() * props.dishes.length)]
           emit('finish', result) // Bắn tín hiệu ra ngoài
        }
      }, 2500)
    }
  })
}

onMounted(() => { animateIn() })
</script>

<style scoped lang="scss">
$gold: #D4AF37;
$gold-light: #FCEABB;
$orange: #EA580C;
$cream: #F4F0EA;

.premium-game { 
  flex: 1; display: flex; justify-content: center; align-items: center; 
  width: 100%; position: relative; font-family: 'Mulish', sans-serif;
}

.quiz-container { 
  width: 100%; max-width: 900px; text-align: center; 
  padding: 0 20px; z-index: 10; position: relative; 
}

/* ================== TIẾN TRÌNH ================== */
.quiz-progress-wrap {
  margin-bottom: 50px; display: flex; flex-direction: column; align-items: center; gap: 15px;
}
.step-label { 
  font-size: 0.75rem; font-weight: 800; letter-spacing: 6px; 
  text-transform: uppercase; color: rgba(255,255,255,0.3);
  .divider { margin: 0 5px; opacity: 0.5; }
  &.glow-text-sub { color: $gold; text-shadow: 0 0 10px rgba($gold, 0.4); }
}
.quiz-progress { 
  width: 120px; height: 3px; background: rgba(255,255,255,0.1); 
  border-radius: 4px; position: relative; overflow: hidden;
}
.progress-bar { 
  height: 100%; background: $gold; transition: width 0.8s cubic-bezier(0.19, 1, 0.22, 1); 
  box-shadow: 0 0 10px $gold;
}

.gsap-step-container { position: relative; will-change: transform, opacity; }

/* ================== CÂU HỎI ================== */
.question-wrapper { min-height: 160px; display: flex; align-items: center; justify-content: center; margin-bottom: 50px; }
.quiz-question { 
  font-family: 'Playfair Display', serif; font-size: clamp(2.2rem, 4vw, 3.8rem); margin: 0; 
  line-height: 1.4; font-weight: 800; letter-spacing: -0.5px; color: rgba(255,255,255,0.9);
  
  em { 
    color: transparent; font-style: italic; font-weight: 900; 
    background: linear-gradient(135deg, $gold, $gold-light);
    -webkit-background-clip: text; background-clip: text;
    filter: drop-shadow(0 2px 10px rgba($gold, 0.3));
  }
}

/* ================== NÚT LỰA CHỌN KÍNH MỜ ================== */
.quiz-options { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; }

.opt-btn-premium {
  background: transparent; border: none; padding: 0; outline: none; cursor: pointer;
  
  .btn-inner-glass {
    display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 15px;
    padding: 40px 20px; background: rgba(20, 20, 22, 0.6); 
    border: 1px solid rgba(255,255,255,0.1); border-radius: 20px; 
    transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1);
    backdrop-filter: blur(15px); box-shadow: 0 10px 30px rgba(0,0,0,0.3);
  }

  .opt-icon {
    font-size: 2.2rem; color: rgba(255,255,255,0.3); transition: 0.4s ease;
  }

  .btn-text { 
    font-size: 1.1rem; font-weight: 800; color: rgba($cream, 0.7); 
    letter-spacing: 2px; text-transform: uppercase; transition: all 0.4s;
    display: block; font-family: 'Mulish', sans-serif;
  }

  &:hover .btn-inner-glass {
    background: rgba(30, 30, 32, 0.8); border-color: rgba($gold, 0.5);
    transform: translateY(-8px); box-shadow: 0 15px 40px rgba(0,0,0,0.6), inset 0 0 20px rgba($gold, 0.1);
  }
  
  &:hover .opt-icon { color: $gold; transform: scale(1.1) translateY(-5px); filter: drop-shadow(0 0 10px rgba($gold, 0.4));}
  &:hover .btn-text { color: #fff; text-shadow: 0 0 15px rgba(255,255,255,0.3); }
}

/* ================== GIAI ĐOẠN 4: RADAR ================== */
.processing-wrapper { margin-bottom: 20px; }
.processing-text { font-size: 2.2rem !important; color: $gold-light !important; text-shadow: 0 0 20px rgba($gold, 0.5) !important; }

.processing-radar {
  position: relative; width: 140px; height: 140px; margin: 40px auto 0;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
}
.radar-ring.primary {
  position: absolute; inset: 0; border-radius: 50%; border: 1px solid rgba($gold, 0.2);
  border-top-color: $gold; animation: spinRing 2s linear infinite;
}
.radar-ring.secondary {
  position: absolute; inset: 15px; border-radius: 50%; border: 1px dashed rgba($gold, 0.3);
  border-bottom-color: transparent; animation: spinRing 3s linear infinite reverse;
}
.radar-core { 
  width: 20px; height: 20px; background: $gold-light; border-radius: 50%; 
  box-shadow: 0 0 30px $gold, 0 0 50px $gold; animation: pulseCore 1s infinite alternate; z-index: 2;
}
.radar-sweep {
  position: absolute; inset: -5px; border-radius: 50%;
  background: conic-gradient(from 0deg, transparent 70%, rgba($gold, 0.4) 100%);
  animation: sweepRadar 1.5s linear infinite; z-index: 1; filter: blur(2px);
}

.data-particles .p-dot {
  position: absolute; top: 50%; left: 50%; width: 3px; height: 3px;
  background: $gold-light; border-radius: 50%; box-shadow: 0 0 8px $gold;
  transform: translate(-50%, -50%);
  animation: shootOut 2s var(--delay) infinite cubic-bezier(0.19, 1, 0.22, 1);
}

@keyframes pulseCore { 0% { transform: scale(0.8); opacity: 0.6; } 100% { transform: scale(1.5); opacity: 1; } }
@keyframes sweepRadar { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
@keyframes spinRing { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
@keyframes shootOut {
  0% { transform: translate(-50%, -50%) scale(1); opacity: 1; }
  100% { transform: translate(calc(-50% + cos(var(--angle)) * 100px), calc(-50% + sin(var(--angle)) * 100px)) scale(0); opacity: 0; }
}

/* =======================================================
   🔥 RESPONSIVE
======================================================= */
@media (max-width: 992px) {
  .question-wrapper { min-height: 120px; margin-bottom: 40px; }
  .quiz-question { font-size: clamp(2rem, 3vw, 2.8rem); br { display: none; } } 
  .opt-btn-premium .btn-inner-glass { padding: 30px 15px; }
}

@media (max-width: 768px) {
  .question-wrapper { margin-bottom: 30px; min-height: auto; }
  .quiz-options { grid-template-columns: 1fr; gap: 20px; max-width: 400px; margin: 0 auto; }
  
  .opt-btn-premium .btn-inner-glass { 
    flex-direction: row; justify-content: flex-start; padding: 20px 25px; border-radius: 16px;
  }
  .opt-btn-premium .opt-icon { margin-bottom: 0; font-size: 1.8rem; }
  .opt-btn-premium .btn-text { font-size: 1rem; text-align: left;}

  .processing-radar { width: 100px; height: 100px; margin: 30px auto 0; }
  .processing-text { font-size: 1.8rem !important; }
}

@media (max-width: 480px) {
  .quiz-progress-wrap { margin-bottom: 30px; gap: 10px; }
  .quiz-question { font-size: 1.6rem; }
  .opt-btn-premium .btn-inner-glass { padding: 15px 20px; }
  .opt-btn-premium .btn-text { font-size: 0.85rem; }
}
</style>