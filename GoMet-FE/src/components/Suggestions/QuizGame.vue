<template>
  <div class="game-stage premium-game">
    
    <div class="quiz-container">
      
      <div class="quiz-progress-wrap">
        <span class="step-label" :class="{ 'glow-text-sub': quizStep <= 3 }">
          {{ t('suggestions.quiz_phase') }} {{ quizStep <= 3 ? quizStep : t('suggestions.quiz_final') }} <span class="divider">/</span> 3
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
            <h2 class="quiz-question" v-if="quizStep === 1" v-html="t('suggestions.quiz_q1_premium')"></h2>
            <h2 class="quiz-question" v-if="quizStep === 2" v-html="t('suggestions.quiz_q2_premium')"></h2>
            <h2 class="quiz-question" v-if="quizStep === 3" v-html="t('suggestions.quiz_q3_premium')"></h2>
            <div class="processing-wrapper" v-if="quizStep === 4">
              <h2 class="quiz-question processing-text">{{ t('suggestions.quiz_processing') }}</h2>
            </div>
          </div>

          <div class="quiz-options" v-if="quizStep < 4">
            <template v-if="quizStep === 1">
              <button 
                class="opt-btn-premium gsap-btn" 
                @click="handleAnswer('style', 'Dry')"
                @mousemove="handleTilt($event)" 
                @mouseleave="resetTilt($event)"
              >
                <div class="btn-inner-glass">
                  <span class="btn-text">{{ t('suggestions.quiz_opt_dry_premium') }}</span>
                  <div class="hologram-glare"></div>
                </div>
              </button>
              <button 
                class="opt-btn-premium gsap-btn" 
                @click="handleAnswer('style', 'Soupy')"
                @mousemove="handleTilt($event)" 
                @mouseleave="resetTilt($event)"
              >
                <div class="btn-inner-glass">
                  <span class="btn-text">{{ t('suggestions.quiz_opt_soupy_premium') }}</span>
                  <div class="hologram-glare"></div>
                </div>
              </button>
            </template>

            <template v-if="quizStep === 2">
              <button 
                class="opt-btn-premium gsap-btn" 
                @click="handleAnswer('flavor', 'Bold')"
                @mousemove="handleTilt($event)" 
                @mouseleave="resetTilt($event)"
              >
                <div class="btn-inner-glass">
                  <span class="btn-text">{{ t('suggestions.quiz_opt_bold_premium') }}</span>
                  <div class="hologram-glare"></div>
                </div>
              </button>
              <button 
                class="opt-btn-premium gsap-btn" 
                @click="handleAnswer('flavor', 'Mild')"
                @mousemove="handleTilt($event)" 
                @mouseleave="resetTilt($event)"
              >
                <div class="btn-inner-glass">
                  <span class="btn-text">{{ t('suggestions.quiz_opt_mild_premium') }}</span>
                  <div class="hologram-glare"></div>
                </div>
              </button>
            </template>

            <template v-if="quizStep === 3">
              <button 
                class="opt-btn-premium gsap-btn" 
                @click="handleAnswer('main', 'Meat')"
                @mousemove="handleTilt($event)" 
                @mouseleave="resetTilt($event)"
              >
                <div class="btn-inner-glass">
                  <span class="btn-text">{{ t('suggestions.quiz_opt_meat_premium') }}</span>
                  <div class="hologram-glare"></div>
                </div>
              </button>
              <button 
                class="opt-btn-premium gsap-btn" 
                @click="handleAnswer('main', 'Seafood')"
                @mousemove="handleTilt($event)" 
                @mouseleave="resetTilt($event)"
              >
                <div class="btn-inner-glass">
                  <span class="btn-text">{{ t('suggestions.quiz_opt_seafood_premium') }}</span>
                  <div class="hologram-glare"></div>
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
import { useI18n } from 'vue-i18n'
import { gsap } from 'gsap'

const props = defineProps({ dishes: { type: Array, required: true } })
const emit = defineEmits(['finish'])
const { t } = useI18n()

const quizStep = ref(1)
const quizAnswers = ref({})
const stepContainer = ref(null)

// Hiệu ứng Hover 3D (Đã tinh chỉnh nhẹ lại để mượt hơn)
const handleTilt = (e) => {
  const btn = e.currentTarget;
  const innerGlass = btn.querySelector('.btn-inner-glass');
  const rect = btn.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  const centerX = rect.width / 2;
  const centerY = rect.height / 2;

  gsap.to(innerGlass, {
    rotationX: ((y - centerY) / centerY) * -12,
    rotationY: ((x - centerX) / centerX) * 12,
    duration: 0.4,
    ease: 'power2.out'
  });

  gsap.to(btn.querySelectorAll('.hologram-glare'), {
    x: x - rect.width,
    y: y - rect.height,
    opacity: 0.5,
    duration: 0.1
  });
}

const resetTilt = (e) => {
  const btn = e.currentTarget;
  const innerGlass = btn.querySelector('.btn-inner-glass');
  gsap.to(innerGlass, { rotationX: 0, rotationY: 0, duration: 0.8, ease: 'elastic.out(1, 0.3)' });
  gsap.to(btn.querySelectorAll('.hologram-glare'), { opacity: 0, duration: 0.5 });
}

// Animation xuất hiện
const animateIn = () => {
  const tl = gsap.timeline({ defaults: { ease: "power3.out" } })
  tl.fromTo(".quiz-question", 
      { opacity: 0, y: 30, filter: "blur(8px)", scale: 0.98 },
      { opacity: 1, y: 0, filter: "blur(0px)", scale: 1, duration: 1 }
    )
    .fromTo(".gsap-btn", 
      { opacity: 0, y: 40, rotationX: -15 },
      { opacity: 1, y: 0, rotationX: 0, duration: 0.8, stagger: 0.1, clearProps: "all" },
      "-=0.6"
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
      
      gsap.fromTo(".processing-wrapper", 
        { opacity: 0, scale: 0.9, filter: "blur(5px)" }, 
        { opacity: 1, scale: 1, filter: "blur(0px)", duration: 1, ease: "back.out(1.2)" }
      )
      
      gsap.fromTo(".processing-radar",
        { opacity: 0, scale: 0.5, rotation: -90 },
        { opacity: 1, scale: 1, rotation: 0, duration: 1.2, ease: "power3.out" }
      )
      
      setTimeout(() => {
        const result = props.dishes[Math.floor(Math.random() * props.dishes.length)]
        emit('finish', result)
      }, 3500)
    }
  })
}

onMounted(() => { animateIn() })
</script>

<style scoped lang="scss">
$gold: #D4AF37;
$gold-light: #FCEABB;
$orange: #EA580C;
$deep-bg: #050505;
$cream: #F4F0EA;

.premium-game { 
  flex: 1; display: flex; justify-content: center; align-items: center; 
  width: 100%; position: relative;
  /* Đã bỏ lưới 3D thừa để tránh làm rối nền tổng thể */
}

.quiz-container { 
  width: 100%; max-width: 900px; text-align: center; 
  padding: 0 20px; z-index: 10; position: relative; 
}

/* ================== TIẾN TRÌNH (Tối giản lại) ================== */
.quiz-progress-wrap {
  margin-bottom: 50px;
  display: flex; flex-direction: column; align-items: center; gap: 15px;
}
.step-label { 
  font-size: 0.75rem; font-weight: 800; letter-spacing: 6px; 
  text-transform: uppercase; color: rgba(255,255,255,0.3);
  .divider { margin: 0 5px; opacity: 0.5; }
  &.glow-text-sub { color: $gold; text-shadow: 0 0 10px rgba($gold, 0.4); }
}
.quiz-progress { 
  width: 100px; height: 2px; background: rgba(255,255,255,0.1); 
  border-radius: 4px; position: relative; overflow: hidden;
}
.progress-bar { 
  height: 100%; background: $gold; 
  transition: width 0.8s cubic-bezier(0.19, 1, 0.22, 1); position: relative;
  box-shadow: 0 0 10px $gold;
}

.gsap-step-container { position: relative; will-change: transform, opacity; perspective: 1000px; }

/* ================== CÂU HỎI (Đồng bộ font) ================== */
.question-wrapper { min-height: 160px; display: flex; align-items: center; justify-content: center; margin-bottom: 40px; }
.quiz-question { 
  font-family: 'Playfair Display', serif; font-size: clamp(2.2rem, 4vw, 3.8rem); margin: 0; 
  line-height: 1.4; font-weight: 800; letter-spacing: -0.5px; color: rgba(255,255,255,0.9);
  
  em { 
    color: transparent; font-style: italic; font-weight: 900; 
    background: linear-gradient(135deg, $gold, $gold-light);
    -webkit-background-clip: text;
    background-clip: text;
    filter: drop-shadow(0 2px 10px rgba($gold, 0.3));
  }
}

/* ================== NÚT BẤM KÍNH (Glassmorphism) ================== */
.quiz-options { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; }

.opt-btn-premium {
  background: transparent; border: none; padding: 0; outline: none;
  cursor: pointer; perspective: 1000px;
  
  .btn-inner-glass {
    position: relative; padding: 40px 20px; 
    background: rgba(20, 20, 20, 0.6); 
    border: 1px solid rgba($gold, 0.15); 
    border-radius: 16px; overflow: hidden;
    transform-style: preserve-3d; 
    transition: background 0.4s, border-color 0.4s, box-shadow 0.4s;
    backdrop-filter: blur(12px);
    box-shadow: 0 10px 30px rgba(0,0,0,0.5);
  }

  .btn-text { 
    position: relative; z-index: 2; font-size: 1.15rem; font-weight: 800; color: rgba($cream, 0.6); 
    letter-spacing: 2px; text-transform: uppercase; transition: all 0.3s;
    transform: translateZ(30px); /* Bật chữ lên */
    display: block; font-family: 'Mulish', sans-serif;
  }

  .hologram-glare {
    position: absolute; width: 200%; height: 200%; top: 0; left: 0;
    background: radial-gradient(circle at center, rgba($gold-light, 0.25) 0%, transparent 50%);
    opacity: 0; pointer-events: none; mix-blend-mode: overlay; z-index: 1;
  }

  &:hover .btn-inner-glass {
    background: rgba(30, 30, 30, 0.8);
    border-color: rgba($gold, 0.6);
    box-shadow: 0 15px 40px rgba($gold, 0.15), inset 0 0 20px rgba($gold, 0.05);
  }
  &:hover .btn-text { 
    color: $gold-light; text-shadow: 0 0 15px rgba($gold, 0.5); transform: translateZ(40px) scale(1.05);
  }
}

/* ================== GIAI ĐOẠN 4: RADAR (Tinh tế hơn) ================== */
.processing-wrapper { margin-bottom: 20px; }
.processing-text { 
  font-size: 2.8rem !important; color: $gold-light !important; 
  text-shadow: 0 0 20px rgba($gold, 0.5) !important; 
}

.processing-radar {
  position: relative; width: 140px; height: 140px; margin: 40px auto 0;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
}
.radar-ring.primary {
  position: absolute; inset: 0; border-radius: 50%; border: 1px solid rgba($gold, 0.2);
  border-top-color: $gold; 
  animation: spinRing 2s linear infinite;
}
.radar-ring.secondary {
  position: absolute; inset: 15px; border-radius: 50%; border: 1px dashed rgba($gold, 0.3);
  border-bottom-color: transparent;
  animation: spinRing 3s linear infinite reverse;
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
</style>