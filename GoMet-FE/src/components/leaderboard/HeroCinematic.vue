<template>
  <section class="hero-cinematic-supreme" ref="heroRef" @mousemove="onMouseMove">
    
    <div class="cinematic-bg-wrapper">
      <div
        :key="mode"
        class="cinematic-bg"
        ref="bgRef"
        :style="{ backgroundImage: backgroundImage ? `url(${backgroundImage})` : 'none' }"
      ></div>
      <div class="cinematic-overlay-gradient"></div>
      <div class="light-leak-effect"></div>
      <div class="film-noise"></div>
    </div>

    <div class="leaderboard-toggle-wrapper anim-premium">
      <div class="leaderboard-toggle">
        <button :class="{ active: mode === 'dish' }" @click="changeMode('dish')">
          <i class="fas fa-utensils"></i> Tuyệt Tác
        </button>
        <button :class="{ active: mode === 'user' }" @click="changeMode('user')">
          <i class="fas fa-crown"></i> Người Xuất Sắc
        </button>
      </div>
    </div>

    <div v-if="displayData" class="cinematic-layout" :key="displayData.id">
      
      <div class="text-column">
        
        <div class="giant-watermark anim-premium">01</div>

        <div class="top-pre-title anim-premium">
          <span class="line"></span>
          <span class="text">GOMET HALL OF FAME</span>
        </div>

        <div class="rank-badge anim-premium">
          <div class="rank-icon-wrapper">
            <i class="fas fa-trophy"></i>
          </div>
          <div class="rank-info">
            <span class="rank-num-main">TOP 1</span>
            <span class="rank-separator">|</span>
            <span class="rank-title">{{ mode === 'user' ? 'Bậc Thầy Ẩm Thực' : 'Tinh Hoa Của GoMet' }}</span>
          </div>
          <span v-if="displayData?.isPremium === 1" class="premium-tag"><i class="fas fa-gem"></i> VIP</span>
        </div>

        <h1 class="hero-title anim-premium">
          <span class="italic-cap">{{ getFirstChar(displayTitle) }}</span>{{ getRestChars(displayTitle) }}
        </h1>

        <p class="hero-desc anim-premium">
          "{{ displayData.description || defaultDescription }}"
        </p>

        <div class="meta-strip anim-premium">
          
          <div v-if="mode === 'dish' && authorData" class="author-pill" @click="goToAuthor">
            <div class="avt-wrap">
              <img :src="authorData.avatar" :alt="authorData.name" />
              <div class="glow-ring"></div>
            </div>
            <div class="author-text">
              <small>Bếp trưởng sáng tạo</small>
              <strong>{{ authorData.name }}</strong>
            </div>
          </div>

          <div class="divider" v-if="mode === 'dish'"></div>

          <div class="stats-group">
            <div class="stat-item" v-for="(val, lbl) in computedMeta" :key="lbl">
              <span class="stat-lbl">{{ lbl }}</span>
              <span class="stat-val">{{ val }}</span>
            </div>
          </div>
        </div>

        <div class="cta-wrapper anim-premium">
          <button class="btn-discover" @click="goToDetail">
            <span>{{ mode === 'user' ? 'Khám Phá Hồ Sơ' : 'Xem Công Thức' }}</span>
            <i class="fas fa-arrow-right icon-arrow"></i>
          </button>
        </div>

      </div>
    </div>

    <div v-else class="cinematic-layout empty-layout">
      <div class="text-column" style="align-items: center; text-align: center; margin: 0 auto;">
        <h1 class="hero-title anim-premium" style="font-size: 3rem;">Chưa có Top 1</h1>
        <p class="hero-desc anim-premium" style="border-left: none; padding-left: 0; max-width: 100%;">
          Hãy là người đầu tiên tương tác để đưa {{ mode === 'user' ? 'đầu bếp' : 'tuyệt tác' }} lên bảng vàng!
        </p>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'

const props = defineProps({
  topDish: { type: Object, default: null },
  topUser: { type: Object, default: null },
  modelValue: { type: String, default: 'dishes' }
})

const emit = defineEmits(['update:modelValue'])
const router = useRouter()
const bgRef = ref(null)
const heroRef = ref(null)

const mode = computed(() => props.modelValue === 'chefs' ? 'user' : 'dish')
const displayData = computed(() => mode.value === 'user' ? props.topUser : props.topDish)
const displayTitle = computed(() => displayData.value?.name || displayData.value?.title || 'Chưa rõ')

const getFirstChar = (s) => s ? String(s).charAt(0) : ''
const getRestChars = (s) => s ? String(s).slice(1) : ''
const backgroundImage = computed(() => displayData.value?.image || displayData.value?.avatar || '')

const defaultDescription = 'Vinh danh tinh hoa ẩm thực và sự cống hiến xuất sắc tại cộng đồng GoMet.'

const computedMeta = computed(() => {
  const d = displayData.value
  if (!d) return {}
  if (mode.value === 'user') {
    return {
      'Tuyệt tác': d.postCount ?? 0,
      'Người hâm mộ': d.followers ?? 0,
      'Điểm cống hiến': (d.pts || d.totalPts) ? `${d.pts || d.totalPts}` : '0'
    }
  }
  return {
    'Điểm tinh hoa': d.pts ? `${d.pts}` : '0', 
    'Độ khó': d.difficulty ?? 'Trung bình'
  }
})

const authorData = computed(() => {
  const d = props.topDish
  if (!d) return null
  return { 
    id: d.authorId, 
    name: d.authorName || 'Đầu Bếp GoMet', 
    avatar: d.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(d.authorName || 'G')}&background=D4AF37&color=000`
  }
})

const goToDetail = () => {
  const d = displayData.value
  if (!d) return
  router.push(mode.value === 'user' ? `/profile/${d.id}` : `/post/${d.id}`)
}

const goToAuthor = () => {
  if (!authorData.value?.id) return
  router.push(`/profile/${authorData.value.id}`)
}

const changeMode = (newMode) => {
  if (mode.value === newMode) return
  emit('update:modelValue', newMode === 'user' ? 'chefs' : 'dishes')
}

// MOUSE PARALLAX
const onMouseMove = (e) => {
  if (!bgRef.value) return
  const x = (e.clientX / window.innerWidth - 0.5) * 40
  const y = (e.clientY / window.innerHeight - 0.5) * 40
  gsap.to(bgRef.value, { x, y, duration: 1.5, ease: 'power2.out' })
}

// ANIMATION MƯỢT MÀ TỐI ƯU HIỆU NĂNG
const runEntranceAnimation = async () => {
  await nextTick()
  if (!heroRef.value) return
  const elements = heroRef.value.querySelectorAll('.anim-premium')
  if (elements.length > 0) {
    gsap.fromTo(elements, 
      { opacity: 0, x: -30 }, 
      { opacity: 1, x: 0, stagger: 0.1, duration: 1, ease: 'power3.out', clearProps: 'all' }
    )
  }
}

onMounted(() => {
  if (!heroRef.value) return
  const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })
  
  const bgEl = heroRef.value.querySelector('.cinematic-bg')
  const overlayEl = heroRef.value.querySelector('.cinematic-overlay-gradient')

  // Đổi từ filter (nặng máy) sang clip-path mượt mà
  if (bgEl) {
    gsap.set(bgEl, { clipPath: 'inset(0 100% 0 0)' })
    tl.to(bgEl, { clipPath: 'inset(0 0% 0 0)', duration: 1.8, ease: 'power4.inOut' }, 0)
      .from(bgEl, { scale: 1.1, duration: 3, ease: 'power2.out' }, 0)
  }
  
  if (overlayEl) tl.to(overlayEl, { opacity: 1, duration: 1.5 }, 0.5)

  runEntranceAnimation()
})

watch(mode, () => runEntranceAnimation())
</script>

<style scoped lang="scss">
/* BẢNG MÀU */
$gold: #D4AF37;
$accent: #EA580C;
$deep: #050505;

.hero-cinematic-supreme {
  position: relative;
  width: 100%;
  height: 100vh;
  min-height: 750px;
  overflow: hidden; 
  background-color: $deep;
  font-family: 'Mulish', sans-serif;
  color: #fff;
}

/* 1. BACKGROUND LAYER */
.cinematic-bg-wrapper { position: absolute; inset: 0; z-index: 1; }
.cinematic-bg {
  position: absolute; inset: -5%; 
  background-size: cover; background-position: right center; 
  transform: scale(1.05); 
  /* Ảnh sáng hơn một chút để rõ món ăn */
  filter: contrast(1.1) brightness(0.75); 
}
.cinematic-overlay-gradient {
  position: absolute; inset: 0;
  background: linear-gradient(90deg, rgba($deep, 0.98) 0%, rgba($deep, 0.85) 45%, rgba($deep, 0) 100%);
  opacity: 0; z-index: 2;
}
.light-leak-effect {
  position: absolute; top: -10%; left: -10%; width: 50vw; height: 50vw;
  background: radial-gradient(circle, rgba($gold, 0.08) 0%, transparent 60%);
  filter: blur(80px); pointer-events: none; z-index: 2;
}
.film-noise {
  position: absolute; inset: 0; z-index: 3; pointer-events: none; opacity: 0.04;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)'/%3E%3C/svg%3E");
}

/* 2. TOGGLE BUTTONS (Được kéo cao lên) */
.leaderboard-toggle-wrapper {
  position: absolute; top: 100px; left: 50%; transform: translateX(-50%); z-index: 50;
}
.leaderboard-toggle {
  display: flex; background: rgba(255,255,255,0.05); padding: 6px; border-radius: 40px;
  backdrop-filter: blur(20px); border: 1px solid rgba(255,255,255,0.1);
  box-shadow: 0 10px 30px rgba(0,0,0,0.5);
  
  button {
    background: transparent; color: rgba(255,255,255,0.5); border: none; padding: 10px 28px;
    border-radius: 30px; font-weight: 700; cursor: pointer; transition: 0.3s;
    display: flex; align-items: center; gap: 8px; letter-spacing: 1px; font-family: 'Mulish', sans-serif;
    
    &:hover { color: #fff; }
    &.active { 
      background: $gold; color: #000; 
      box-shadow: 0 4px 15px rgba($gold, 0.4); 
    }
  }
}

/* 3. FLEX CONTENT */
.cinematic-layout {
  position: relative; z-index: 10;
  display: flex; height: 100%; padding: 0 6%; align-items: center;
}

.text-column {
  position: relative; display: flex; flex-direction: column; justify-content: center;
  width: 100%; max-width: 650px; 
}

/* 4. SỐ 01 WATERMARK (RÕ NÉT & SÁNG HƠN) */
.giant-watermark {
  position: absolute; left: -30px; top: 48%; transform: translateY(-50%);
  font-family: 'Playfair Display', serif; font-size: clamp(200px, 32vw, 420px);
  font-weight: 900; line-height: 1; pointer-events: none; z-index: -1;
  /* Thay đổi: Dùng viền sáng vàng nhạt thay vì trắng đục */
  color: transparent; 
  -webkit-text-stroke: 2px rgba($gold, 0.15);
  /* Phủ thêm một lớp bóng chìm để nổi bật trên nền tối */
  filter: drop-shadow(0 0 30px rgba($gold, 0.1));
}

/* 5. TYPOGRAPHY & TRANG TRÍ PHÍA TRÊN */
.top-pre-title {
  display: flex; align-items: center; gap: 15px; margin-bottom: 20px;
  .line { width: 40px; height: 2px; background: $gold; }
  .text { font-size: 0.8rem; color: $gold; letter-spacing: 4px; font-weight: 800; }
}

.rank-badge {
  display: flex; align-items: center; gap: 15px; margin-bottom: 25px; 
  
  .rank-icon-wrapper {
    width: 45px; height: 45px; border-radius: 50%; background: rgba($gold, 0.1);
    display: flex; align-items: center; justify-content: center; color: $gold;
    border: 1px solid rgba($gold, 0.3); font-size: 1.2rem;
  }

  .rank-info {
    display: flex; align-items: center; gap: 10px;
    .rank-num-main { font-family: 'Playfair Display', serif; font-size: 1.8rem; font-weight: 900; color: #fff; }
    .rank-separator { color: rgba(255,255,255,0.3); }
    .rank-title { color: $gold; text-transform: uppercase; font-size: 0.9rem; letter-spacing: 2px; font-weight: 800;}
  }

  .premium-tag { 
    background: linear-gradient(135deg, #FFD700, #F59E0B); color: #000; padding: 4px 10px; 
    border-radius: 6px; font-weight: 800; font-size: 0.75rem; letter-spacing: 1px;
    box-shadow: 0 4px 10px rgba(245, 158, 11, 0.3);
  }
}

.hero-title {
  font-size: clamp(3.5rem, 6vw, 6rem); 
  font-family: 'Playfair Display', serif; line-height: 1.05; margin: 0 0 25px 0;
  text-shadow: 0 10px 40px rgba(0,0,0,0.8); word-wrap: break-word; letter-spacing: -1px;
  .italic-cap { font-style: italic; color: $accent; font-size: 1.1em; display: inline-block; margin-right: 5px;}
}

.hero-desc {
  font-size: 1.15rem; line-height: 1.7; color: rgba(255, 255, 255, 0.7); font-style: italic; margin-bottom: 45px;
  border-left: 2px solid $gold; padding-left: 20px; max-width: 90%;
}

/* 6. DẢI THÔNG SỐ META KÍNH MỜ */
.meta-strip {
  display: flex; align-items: center; flex-wrap: wrap; gap: 30px; margin-bottom: 45px;
  background: rgba(255, 255, 255, 0.02); border: 1px solid rgba(255, 255, 255, 0.05);
  padding: 12px 30px 12px 12px; border-radius: 50px; backdrop-filter: blur(15px); width: max-content;
  max-width: 100%;
}

.author-pill {
  display: flex; align-items: center; gap: 15px; cursor: pointer; transition: 0.3s;
  &:hover { transform: translateX(5px); .author-text strong { color: $gold; } }
  
  .avt-wrap {
    position: relative; width: 48px; height: 48px; border-radius: 50%;
    img { width: 100%; height: 100%; border-radius: 50%; border: 2px solid $gold; object-fit: cover; position: relative; z-index: 2;}
    .glow-ring { position: absolute; inset: -4px; background: radial-gradient(circle, $gold, transparent 70%); border-radius: 50%; opacity: 0.5; z-index: 1;}
  }
  
  .author-text {
    display: flex; flex-direction: column;
    small { font-size: 0.7rem; color: #a1a1aa; text-transform: uppercase; letter-spacing: 1.5px; margin-bottom: 2px;}
    strong { font-size: 1.1rem; color: #fff; font-family: 'Playfair Display', serif; transition: 0.3s;}
  }
}

.divider { width: 1px; height: 40px; background: rgba(255,255,255,0.1); }

.stats-group {
  display: flex; gap: 35px; flex-wrap: wrap;
  .stat-item {
    display: flex; flex-direction: column;
    .stat-lbl { font-size: 0.7rem; color: $gold; text-transform: uppercase; letter-spacing: 1.5px; margin-bottom: 4px; font-weight: 800;}
    .stat-val { font-size: 1.4rem; font-weight: 700; color: #fff; font-family: 'Playfair Display', serif;}
  }
}

/* 7. NÚT CTA KIỂU GHOST BUTTON ĐẲNG CẤP */
.btn-discover {
  position: relative; overflow: hidden; display: inline-flex; align-items: center; gap: 12px;
  background: rgba(255, 255, 255, 0.1); border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(20px); padding: 18px 45px; border-radius: 40px; cursor: pointer;
  transition: 0.6s cubic-bezier(0.19, 1, 0.22, 1);

  span { font-size: 0.9rem; color: white; font-weight: 800; letter-spacing: 3px; text-transform: uppercase; transition: 0.4s;}
  .icon-arrow { color: $gold; transition: 0.4s ease; font-size: 1.1rem;}

  &::after { 
    content: ''; position: absolute; top: 0; left: -100%; width: 50%; height: 100%; 
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent); 
    transform: skewX(-25deg); transition: 0.75s cubic-bezier(0.19, 1, 0.22, 1); 
  }

  &:hover {
    background: white; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
    span { color: black; }
    .icon-arrow { color: $accent; transform: translateX(8px); }
    &::after { left: 150%; }
  }
}

/* =======================================================
   8. RESPONSIVE (TỰ ĐỘNG THU GỌN THEO CHIỀU DỌC)
======================================================= */
@media (max-width: 992px) {
  .hero-cinematic-supreme { height: auto; min-height: 100vh; padding-top: 80px; }
  
  .cinematic-bg { background-position: center; }
  .cinematic-overlay-gradient { background: linear-gradient(180deg, rgba($deep, 0.9) 0%, rgba($deep, 0.7) 40%, rgba($deep, 0.2) 100%); }
  
  .cinematic-layout { justify-content: center; padding: 120px 5% 50px; }
  .text-column { align-items: center; text-align: center; padding-top: 50px; margin: 0 auto; max-width: 100%; }
  
  /* Căn giữa lại các thành phần */
  .top-pre-title { justify-content: center; }
  .rank-badge { justify-content: center; flex-wrap: wrap;}
  
  .giant-watermark { left: 50%; transform: translate(-50%, -50%); opacity: 0.08; top: 40%; font-size: 35vw; }
  
  .hero-desc { border-left: none; padding-left: 0; margin-bottom: 30px; }
  
  .meta-strip { flex-direction: column; gap: 20px; padding: 25px 40px; border-radius: 30px; width: auto; }
  .divider { width: 80%; height: 1px; }
  .stats-group { justify-content: center; width: 100%; }
}

@media (max-width: 768px) {
  .leaderboard-toggle-wrapper { top: 90px; width: 90%; }
  .leaderboard-toggle { justify-content: center; }
  .leaderboard-toggle button { flex: 1; justify-content: center; padding: 10px 15px; font-size: 0.85rem; }
  
  .hero-title { font-size: clamp(3rem, 10vw, 4rem); letter-spacing: 0; margin-bottom: 20px; }
  .stats-group { gap: 25px; }
}

@media (max-width: 480px) {
  .rank-badge .rank-info .rank-num-main { font-size: 1.5rem; }
  .meta-strip { padding: 20px; }
  .stats-group { flex-direction: column; gap: 15px; }
  .btn-discover { width: 100%; justify-content: center; padding: 16px 20px; }
}
</style>