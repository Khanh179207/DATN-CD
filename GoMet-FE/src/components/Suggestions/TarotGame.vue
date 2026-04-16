<template>
  <div class="game-stage premium-game" ref="gameStageRef" @mousemove="trackMouse">
    
    <div class="divine-light" v-if="gameState !== 'idle'"></div>
    <div class="cosmic-particles" v-if="gameState !== 'idle'" ref="particlesContainer">
      <div v-for="n in 50" :key="n" class="particle"></div>
    </div>

    <div class="ritual-wrapper">
      
      <div class="ritual-header-premium" ref="headerRef">
        <span class="ritual-tagline">
          <i class="fas fa-moon"></i> The Culinary Oracle
        </span>
        <h2 class="ritual-main-title" :class="{ 'neon-glow': gameState !== 'idle' }">
          {{ statusText }}
        </h2>
      </div>

      <div class="the-sacred-stage">
        
        <div class="shuffle-deck" v-if="gameState === 'shuffling'">
          <div v-for="i in 18" :key="i" class="card-artifact back-design-mystic"></div>
        </div>

        <div class="hand-of-fate" v-if="gameState === 'dealt' || gameState === 'revealing'">
          <div 
            v-for="(card, index) in selectedCards" 
            :key="index"
            class="tarot-card-v3"
            :ref="el => { if (el) cardRefs[index] = el }"
            @click="flipCard(index)"
            @mousemove="handleCardTilt($event, index)"
            @mouseleave="resetCardTilt(index)"
          >
            <div class="card-under-glow"></div>
            
            <div class="card-inner-3d">
              <div class="face-3d back back-design-mystic">
                <div class="gold-foil-pattern"></div>
                <div class="card-frame-gold"></div>
                <div class="shimmer-effect"></div>
              </div>

              <div class="face-3d front">
                <div class="front-texture"></div>
                <div class="inner-frame"></div>
                
                <div class="content-wrap">
                  <div class="symbol">{{ card.icon }}</div>
                  <div class="name">{{ card.name }}</div>
                  <div class="desc">{{ card.desc }}</div>
                </div>
                
                <div class="shimmer-effect"></div>
                <div class="cinematic-sweep"></div>
              </div>
            </div>
          </div>
        </div>

        <div class="oracle-hand-wrap" v-show="gameState === 'dealt'" ref="handWrapRef">
          <div class="hand-inner-float">
            <img src="@/assets/images/hand.png" class="real-hand-img" alt="Bàn tay tiên tri" />
            <div class="hand-essence-glow"></div>
          </div>
        </div>

      </div>

      <div class="ritual-actions" v-if="gameState === 'idle'">
        <button class="btn-invoke-oracle anim-entrance" @click="startRitual">
          Triệu Hồi Định Mệnh 
          <div class="btn-flare"></div>
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted, onUnmounted } from 'vue'
import { gsap } from 'gsap'

const props = defineProps({ dishes: { type: Array, required: true } })
const emit = defineEmits(['finish'])

const gameState = ref('idle')
const statusText = ref('Chạm để kết nối với năng lượng ẩm thực')
const cardRefs = reactive([])

const gameStageRef = ref(null)
const headerRef = ref(null)
const handWrapRef = ref(null)
const particlesContainer = ref(null)

// 🃏 KHO BÀI TAROT
const tarotPool = [
  { name: 'The Flame', icon: '🔥', desc: 'Mãnh liệt & Cay nồng' },
  { name: 'The Ocean', icon: '🌊', desc: 'Thanh khiết & Tươi mát' },
  { name: 'The Earth', icon: '🌿', desc: 'Cân bằng & Chữa lành' },
  { name: 'The Sky', icon: '☁️', desc: 'Nhẹ nhàng & Thanh tao' },
  { name: 'The Star', icon: '✨', desc: 'Ngọt ngào & Tinh túy' },
  { name: 'The Harvest', icon: '🌾', desc: 'Đậm đà & Dinh dưỡng' },
  { name: 'The Shadow', icon: '🌒', desc: 'Bí ẩn & Độc đáo' }
]

const selectedCards = ref([])

// 🚀 ENTRANCE ANIMATION (Khi mới vào màn hình Tarot)
onMounted(() => {
  gsap.fromTo('.anim-entrance', 
    { opacity: 0, y: 30, scale: 0.9 }, 
    { opacity: 1, y: 0, scale: 1, duration: 1.2, ease: "power3.out", clearProps: "all" }
  )
})

// 🌌 HỆ THỐNG HẠT VŨ TRỤ (Particles)
let particleAnimations = []
const initParticles = () => {
  if (!particlesContainer.value) return
  const particles = particlesContainer.value.querySelectorAll('.particle')
  
  particles.forEach(p => {
    gsap.set(p, {
      x: Math.random() * window.innerWidth,
      y: Math.random() * window.innerHeight,
      scale: Math.random() * 1.2 + 0.3,
      opacity: Math.random() * 0.6 + 0.1
    })
    animateParticle(p)
  })
}

const animateParticle = (p) => {
  const anim = gsap.to(p, {
    y: `-=${Math.random() * 300 + 150}`,
    x: `+=${Math.random() * 150 - 75}`,
    rotation: Math.random() * 360,
    duration: Math.random() * 12 + 8,
    ease: 'none',
    onComplete: () => {
      // Loop: Đưa hạt rớt xuống đáy màn hình khi bay lên quá cao
      if (p.getBoundingClientRect().top < -50) {
        gsap.set(p, { y: window.innerHeight + 100 })
      }
      animateParticle(p)
    }
  })
  particleAnimations.push(anim)
}

// ✨ BÀN TAY CHỈ THEO CHUỘT (Giới hạn trong game-stage)
const trackMouse = (e) => {
  if (gameState.value !== 'dealt' || !handWrapRef.value || !gameStageRef.value) return;

  const stageRect = gameStageRef.value.getBoundingClientRect();
  const handRect = handWrapRef.value.getBoundingClientRect();
  
  const handX = handRect.left + handRect.width / 2;
  const handY = handRect.top + handRect.height / 2;

  // Tính khoảng cách tương đối so với sân khấu, không phải toàn màn hình
  const deltaX = e.clientX - handX;
  const deltaY = e.clientY - handY;

  let angle = Math.atan2(deltaY, deltaX) * (180 / Math.PI);

  gsap.to(handWrapRef.value, {
    x: deltaX * 1.5, 
    y: (deltaY * 0.8) + 100, // Cân bằng độ cao của bàn tay
    rotation: angle + 25, 
    duration: 0.6,
    ease: 'power2.out'
  });
}

// 🎴 HIỆU ỨNG NGHIÊNG BÀI 3D (TILT)
const handleCardTilt = (e, index) => {
  if (gameState.value === 'revealing') return;
  const card = cardRefs[index];
  if (!card) return;

  const rect = card.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  const centerX = rect.width / 2;
  const centerY = rect.height / 2;
  
  // Tối ưu giới hạn góc nghiêng tránh lỗi lật mặt sau
  const rotateX = Math.max(-20, Math.min(20, ((y - centerY) / centerY) * -20));
  const rotateY = Math.max(-20, Math.min(20, ((x - centerX) / centerX) * 20));

  gsap.to(card.querySelector('.card-inner-3d'), {
    rotationX: rotateX,
    rotationY: rotateY,
    duration: 0.4,
    ease: 'power2.out'
  });

  gsap.to(card.querySelectorAll('.shimmer-effect'), {
    x: x - rect.width,
    y: y - rect.height,
    opacity: 0.6,
    duration: 0.1
  });
}

const resetCardTilt = (index) => {
  if (gameState.value === 'revealing') return;
  const card = cardRefs[index];
  if (!card) return;

  gsap.to(card.querySelector('.card-inner-3d'), {
    rotationX: 0, rotationY: 0, duration: 1, ease: 'elastic.out(1, 0.4)'
  });
  gsap.to(card.querySelectorAll('.shimmer-effect'), { opacity: 0, duration: 0.5 });
}

// 🚀 BẮT ĐẦU TRÒ CHƠI
const startRitual = () => {
  gameState.value = 'shuffling'
  statusText.value = 'Xáo trộn dòng chảy năng lượng...'
  
  // Random thật sự: Trộn kho bài và bốc ra 3 lá duy nhất
  const shuffledPool = [...tarotPool].sort(() => 0.5 - Math.random())
  selectedCards.value = shuffledPool.slice(0, 3)

  nextTick(() => {
    initParticles() // Khởi động vũ trụ

    const tl = gsap.timeline();
    const cards = document.querySelectorAll('.card-artifact');
    
    // Animation xáo bài mượt mà hơn
    tl.to(cards, {
      x: (i) => (i % 2 === 0 ? 120 : -120),
      y: (i) => (Math.random() * 40 - 20),
      rotation: (i) => (i % 2 === 0 ? 15 : -15),
      stagger: 0.03, duration: 0.3, yoyo: true, repeat: 1, ease: "power1.inOut"
    })
    .to(cards, { x: 0, y: 0, rotation: 0, duration: 0.4, ease: "back.out(1.5)" })
    .add(() => { dealCards() });
  })
}

// 🃏 CHIA BÀI
const dealCards = () => {
  gameState.value = 'dealt'
  statusText.value = 'Hãy chọn linh cảm của bạn'
  
  nextTick(() => {
    // 3 lá bài rớt xuống
    gsap.fromTo(".tarot-card-v3", 
      { y: -600, opacity: 0, rotationX: 90, scale: 0.5 },
      { y: 0, opacity: 1, rotationX: 0, scale: 1, stagger: 0.15, duration: 1.2, ease: "elastic.out(1, 0.6)" }
    );
    
    // Bàn tay xuất hiện
    if (handWrapRef.value) {
      gsap.fromTo(handWrapRef.value, 
        { opacity: 0, scale: 0.5, y: 200 }, 
        { opacity: 1, scale: 1, y: 0, duration: 1, ease: "back.out(1.2)" }
      );
      // Lớp bên trong của bàn tay lơ lửng nhẹ nhàng
      gsap.to(".hand-inner-float", {
        y: -20, repeat: -1, yoyo: true, duration: 2.5, ease: "sine.inOut"
      });
    }
  })
}

// 🃏 LẬT BÀI
const flipCard = (index) => {
  if (gameState.value === 'revealing') return;
  gameState.value = 'revealing';
  statusText.value = 'Vũ trụ đã phản hồi...';
  
  const card = cardRefs[index];
  if (!card) return;
  
  // Xóa tay
  if (handWrapRef.value) {
    gsap.to(handWrapRef.value, { opacity: 0, scale: 0.2, duration: 0.4 });
  }
  
  // Rớt các lá không chọn
  cardRefs.forEach((r, i) => { 
    if (i !== index && r) {
      gsap.to(r, { opacity: 0, y: 200, scale: 0.8, rotationZ: (i-1)*10, duration: 0.6, ease: "power3.in" }) 
    }
  });

  const tl = gsap.timeline();
  
  // Phóng to và xoay lá được chọn
  tl.to(card, { x: 0, y: -20, scale: 1.4, zIndex: 100, duration: 0.8, ease: "power3.out" })
    .to(card.querySelector('.card-inner-3d'), { rotationY: 180, duration: 1, ease: "back.out(1.2)" }, "-=0.2")
  
  // Vệt sáng lướt qua
  const sweep = card.querySelector('.cinematic-sweep');
  if (sweep) {
    tl.fromTo(sweep, 
      { x: '-150%', opacity: 0 },
      { x: '150%', opacity: 0.6, duration: 0.8, ease: "power2.inOut" },
      "-=0.4"
    );
  }

  // Kết thúc -> Emit để gọi SuggestionCard
  tl.add(() => {
     setTimeout(() => {
       if (props.dishes && props.dishes.length > 0) {
         const result = props.dishes[Math.floor(Math.random() * props.dishes.length)]
         emit('finish', result)
       }
     }, 1500)
  });
}

onUnmounted(() => {
  // Dọn dẹp GSAP Animations để tránh leak memory
  particleAnimations.forEach(anim => anim.kill())
})
</script>

<style scoped lang="scss" src="@/components/suggestions/TarotGame.scss"></style>