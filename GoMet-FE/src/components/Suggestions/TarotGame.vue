<template>
  <div class="tarot-ritual-master">
    
    <div class="divine-light" v-if="gameState !== 'idle'"></div>
    <div class="cosmic-particles" v-if="gameState !== 'idle'">
      <div v-for="n in 50" :key="n" class="particle"></div>
    </div>

    <div class="ritual-wrapper">
      
      <div class="ritual-header-premium" ref="header">
        <span class="ritual-tagline">The Culinary Oracle</span>
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
            :ref="el => cardRefs[index] = el"
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

<div class="oracle-hand-wrap" v-if="gameState === 'dealt'" ref="handWrapRef">
  <div class="hand-inner-float">
    <img src="@/assets/images/hand.png" class="real-hand-img" alt="Oracle Hand" />
    <div class="hand-essence-glow"></div>
  </div>
</div>
      </div>

      <div class="ritual-actions">
        <button v-if="gameState === 'idle'" class="btn-invoke-oracle" @click="startRitual">
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
const handWrapRef = ref(null)

// 🃏 KHO BÀI TAROT (Pool) - Đã mở rộng để random thật sự
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

// 🌌 KHỞI TẠO HẠT VŨ TRỤ (Particles)
const initParticles = () => {
  const particles = document.querySelectorAll('.particle')
  particles.forEach(p => {
    // Random vị trí ban đầu
    gsap.set(p, {
      x: Math.random() * window.innerWidth,
      y: Math.random() * window.innerHeight,
      scale: Math.random() * 1.5 + 0.5,
      opacity: Math.random() * 0.5 + 0.2
    })
    animateParticle(p)
  })
}

const animateParticle = (p) => {
  gsap.to(p, {
    y: `-=${Math.random() * 200 + 100}`,
    x: `+=${Math.random() * 100 - 50}`,
    rotation: Math.random() * 360,
    duration: Math.random() * 10 + 5,
    ease: 'none',
    onComplete: () => {
      // Vòng lặp vô tận: Nếu bay lên khuất màn hình thì rớt lại xuống đáy
      if (p.getBoundingClientRect().top < 0) {
        gsap.set(p, { y: window.innerHeight + 50 })
      }
      animateParticle(p)
    }
  })
}

// ✨ BÀN TAY CHỈ THEO CHUỘT
const trackMouse = (e) => {
  if (gameState.value !== 'dealt' || !handWrapRef.value) return;

  const rect = handWrapRef.value.getBoundingClientRect();
  const handX = rect.left + rect.width / 2;
  const handY = rect.top + rect.height / 2;

  const deltaX = e.clientX - handX;
  const deltaY = e.clientY - handY;

  let angle = Math.atan2(deltaY, deltaX) * (180 / Math.PI);

  gsap.to(handWrapRef.value, {
    x: deltaX * 2, 
    // 🔥 THÊM "+ 80" VÀO ĐÂY ĐỂ KÉO TAY THẤP XUỐNG
    // (Bạn có thể tăng lên +120 hoặc giảm xuống +50 tùy mắt nhìn)
    y: (deltaY * 1) + 120, 
    rotation: angle + 30, 
    duration: 0.4,
    ease: 'power2.out'
  });
}

// Lắng nghe sự kiện chuột toàn màn hình
onMounted(() => window.addEventListener('mousemove', trackMouse))
onUnmounted(() => window.removeEventListener('mousemove', trackMouse))

// HIỆU ỨNG NGHIÊNG BÀI 3D (Tilt)
const handleCardTilt = (e, index) => {
  if (gameState.value === 'revealing') return;
  const card = cardRefs[index];
  const rect = card.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  const centerX = rect.width / 2;
  const centerY = rect.height / 2;
  
  gsap.to(card.querySelector('.card-inner-3d'), {
    rotationX: ((y - centerY) / centerY) * -15,
    rotationY: ((x - centerX) / centerX) * 15,
    duration: 0.4,
    ease: 'power2.out'
  });

  gsap.to(card.querySelectorAll('.shimmer-effect'), {
    x: x - rect.width,
    y: y - rect.height,
    opacity: 0.5,
    duration: 0.1
  });
}

const resetCardTilt = (index) => {
  if (gameState.value === 'revealing') return;
  gsap.to(cardRefs[index].querySelector('.card-inner-3d'), {
    rotationX: 0, rotationY: 0, duration: 1, ease: 'elastic.out(1, 0.3)'
  });
  gsap.to(cardRefs[index].querySelectorAll('.shimmer-effect'), { opacity: 0, duration: 0.5 });
}

// BẮT ĐẦU TRÒ CHƠI
const startRitual = () => {
    
  gameState.value = 'shuffling'
  statusText.value = 'Xáo trộn dòng chảy năng lượng...'
  
  // 🃏 Random thật sự: Trộn kho bài và bốc ra 3 lá duy nhất
  const shuffledPool = [...tarotPool].sort(() => 0.5 - Math.random())
  selectedCards.value = shuffledPool.slice(0, 3)

  nextTick(() => {
    initParticles() // Khởi động vũ trụ

    const tl = gsap.timeline();
    const cards = document.querySelectorAll('.card-artifact');
    tl.to(cards, {
      x: (i) => (i % 2 === 0 ? 150 : -150),
      rotation: (i) => (i % 2 === 0 ? 20 : -20),
      stagger: 0.02, duration: 0.4, yoyo: true, repeat: 1, ease: "sine.inOut"
    })
    .to(cards, { x: 0, rotation: 0, duration: 0.5, ease: "back.in(2)" })
    .add(() => { dealCards() });
  })
}

// CHIA BÀI
const dealCards = () => {
  gameState.value = 'dealt'
  statusText.value = 'Hãy chọn linh cảm của bạn, Master Chef'
  nextTick(() => {
    // Bài rơi xuống
    gsap.from(".tarot-card-v3", {
      y: -800, opacity: 0, rotationX: 90, stagger: 0.2, duration: 1.5, ease: "bounce.out"
    });
    
    // Bàn tay xuất hiện
    gsap.fromTo(handWrapRef.value, 
      { opacity: 0, scale: 0.5 }, 
      { opacity: 1, scale: 1, duration: 1.2, ease: "back.out(1.5)" }
    );

    // Lớp bên trong của bàn tay tự động thở lơ lửng nhẹ nhàng
    gsap.to(".hand-inner-float", {
      y: -15, repeat: -1, yoyo: true, duration: 2, ease: "sine.inOut"
    });
  })
}

// LẬT BÀI
const flipCard = (index) => {
  if (gameState.value === 'revealing') return;
  gameState.value = 'revealing';
  statusText.value = 'Vũ trụ đã phản hồi...';
  const card = cardRefs[index];
  
  // Xóa tay
  gsap.to(handWrapRef.value, { opacity: 0, scale: 0.2, duration: 0.4 });
  
  // Rớt các lá không chọn
  cardRefs.forEach((r, i) => { 
    if (i !== index) gsap.to(r, { opacity: 0, y: 100, scale: 0.8, duration: 0.6, ease: "power3.in" }) 
  });

  const tl = gsap.timeline();
  
  // Phóng to và xoay
  tl.to(card, { x: 0, y: -40, scale: 1.3, zIndex: 100, duration: 0.8, ease: "power3.out" })
    .to(card.querySelector('.card-inner-3d'), { rotationY: 180, duration: 1.2, ease: "back.out(1.4)" })
  
  // 🎴 Cinematic Sweep: Vệt sáng lướt qua khi vừa lật xong
  tl.fromTo(card.querySelector('.cinematic-sweep'), 
    { x: '-150%', opacity: 0 },
    { x: '150%', opacity: 0.8, duration: 0.8, ease: "power1.inOut" },
    "-=0.5" // Chạy gối lên lúc đang lật
  );

  // Kết thúc
  tl.add(() => {
     setTimeout(() => {
       const result = props.dishes[Math.floor(Math.random() * props.dishes.length)]
       emit('finish', result)
     }, 1800)
  });
}
</script>

<style scoped lang="scss" src="@/components/suggestions/TarotGame.scss"></style>