<template>
  <section class="cinematic-horizontal-showcase">
    <div class="bg-layer">
      <transition-group name="fade-bg" tag="div">
        <div
          v-for="(item, index) in top10Data"
          v-show="activeIndex === index"
          :key="'bg-' + item.id"
          class="bg-image"
          :style="{ backgroundImage: `url(${item.image || item.avatar || item.authorAvatar})` }"
        ></div>
      </transition-group>
      <div class="bg-overlay"></div>
    </div>

    <div class="cinematic-header-container">
      <div class="header-titles">
        <div class="sub-tag">Đại Sảnh Danh Vọng GoMet</div>
        <h2 class="main-heading">
          Bảng Vàng <span class="italic-first">{{ timeLabel }}</span>
        </h2>
      </div>
      
      <div class="glass-tabs">
        <button 
          v-for="t in ['day', 'month', 'year']" 
          :key="t"
          :class="{ active: currentTimeframe === t }"
          @click.stop="handleTimeClick(t)" 
        >
          {{ t === 'day' ? 'Hôm nay' : t === 'month' ? 'Tháng này' : 'Năm nay' }}
        </button>
      </div>
    </div>

    <div class="slider-wrapper" v-if="top10Data.length > 0">
      
      <button class="nav-btn prev-btn" @click="prevCard" :class="{ hidden: activeIndex === 0 }">
        <i class="fas fa-chevron-left"></i>
      </button>

      <div 
        class="carousel-viewport" 
        ref="viewportRef"
        @mousedown="onDragStart"
        @touchstart="onDragStart"
        @mousemove="onDragMove"
        @touchmove="onDragMove"
        @mouseup="onDragEnd"
        @mouseleave="onDragEnd"
        @touchend="onDragEnd"
      >
        <div class="carousel-track" ref="trackRef">
          <div
            v-for="(item, index) in top10Data"
            :key="item.id"
            class="carousel-item"
            :ref="el => { if(el) cardRefs[index] = el }"
            @click="handleItemClick(item, index)"
          >
            <div v-if="item.isPremium === 1" class="premium-badge-float">👑 VIP</div>

            <div class="item-top-info">
              <span class="rank-number">TOP {{ startRank + index }}</span>
            </div>

            <div class="item-image-wrapper">
              <img :src="item.image || item.avatar || item.authorAvatar" :alt="item.title || item.name" draggable="false" />
              <div class="image-gradient"></div>
            </div>

            <div class="item-bottom-info">
              <h3 class="item-title">{{ item.title || item.name }}</h3>
              <div class="item-meta">
                <span class="meta-author" v-if="category === 'dishes'">
                  Bởi {{ item.authorName || 'Đầu Bếp GoMet' }}
                </span>
                <span class="meta-author" v-else>
                  {{ item.postCount || 0 }} Tuyệt tác
                </span>
                <span class="meta-dot">•</span>
                <span class="meta-score">{{ item.pts || item.totalPts || 0 }} Điểm</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <button class="nav-btn next-btn" @click="nextCard" :class="{ hidden: activeIndex === top10Data.length - 1 }">
        <i class="fas fa-chevron-right"></i>
      </button>
    </div>

    <div v-else class="empty-state-container">
      <div class="empty-content">
        <i class="fas fa-crown empty-icon"></i>
        <h3>Chưa có dữ liệu {{ timeLabel }}</h3>
        <p>Hãy tương tác để ghi danh lên bảng vàng!</p>
      </div>
    </div>

    <div v-if="previewItem" ref="previewOverlayRef" class="preview-overlay" @click.self="closePreview">
      <button class="preview-close" @click="closePreview"><i class="fas fa-times"></i></button>

      <div ref="fxImageWrapRef" class="fx-image-wrap">
        <img :src="previewImage" :alt="previewTitle" />
      </div>

      <div ref="fxPanelRef" class="fx-panel">
        <span class="preview-tag">
          {{ category === 'dishes' ? 'Tuyệt tác nổi bật' : 'Thành viên xuất sắc' }}
          <span v-if="previewItem.isPremium === 1" class="text-gold"> | 👑 VIP</span>
        </span>
        
        <h3 class="preview-title">{{ previewTitle }}</h3>

        <div class="panel-stats">
          <template v-if="category === 'dishes'">
            <div class="stat-box">
              <span class="stat-label">Bếp trưởng</span>
              <span class="stat-value">{{ previewItem.authorName || 'Đầu Bếp GoMet' }}</span>
            </div>
            <div class="stat-box highlight">
              <span class="stat-label">Điểm đánh giá</span>
              <span class="stat-value">{{ previewItem.pts || 0 }} Điểm</span>
            </div>
          </template>
          <template v-else>
            <div class="stat-box">
              <span class="stat-label">Hồ sơ</span>
              <span class="stat-value" style="font-size: 1rem;">
                {{ previewItem.postCount || 0 }} bài viết <br>
                {{ previewItem.followers || 0 }} người theo dõi
              </span>
            </div>
            <div class="stat-box highlight">
              <span class="stat-label">Tổng tương tác</span>
              <span class="stat-value">{{ previewItem.pts || previewItem.totalPts || 0 }} Điểm</span>
            </div>
          </template>
        </div>

        <p v-if="previewItem.description" class="preview-desc">
          "{{ previewItem.description }}"
        </p>

        <button class="preview-cta" @click="goToDetail">
          {{ category === 'dishes' ? 'Xem chi tiết công thức' : 'Ghé thăm hồ sơ' }}
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, nextTick, watch, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'

const props = defineProps({
  data: { type: Array, required: true },
  category: { type: String, default: 'dishes' },
  startRank: { type: Number, default: 1 },
  currentTimeframe: { type: String, default: 'month' } 
})

const emit = defineEmits(['update-timeframe'])
const router = useRouter()

// --- Refs cho Slider ---
const viewportRef = ref(null)
const trackRef = ref(null)
const cardRefs = ref([])
const activeIndex = ref(0)

// --- Dữ liệu ---
const top10Data = computed(() => props.data.slice(0, 10))
const timeLabel = computed(() => props.currentTimeframe === 'day' ? 'Hôm nay' : props.currentTimeframe === 'month' ? 'Tháng này' : 'Năm nay')
const handleTimeClick = (time) => {
  activeIndex.value = 0 
  emit('update-timeframe', time)
}

// --- GSAP CAROUSEL ANIMATION ---
const updateSliderAnimation = () => {
  if (!viewportRef.value || !trackRef.value || !cardRefs.value.length) return
  
  const viewportW = viewportRef.value.clientWidth
  const cardW = cardRefs.value[0].clientWidth
  const gap = window.innerWidth > 1200 ? 60 : (window.innerWidth > 900 ? 40 : 20)
  
  const centerOffset = (viewportW - cardW) / 2
  const targetX = -(activeIndex.value * (cardW + gap)) + centerOffset

  gsap.to(trackRef.value, { x: targetX, duration: 0.8, ease: "power3.out", overwrite: "auto" })

  cardRefs.value.forEach((card, index) => {
    const isActive = index === activeIndex.value
    gsap.to(card, {
      scale: isActive ? 1 : 0.8,
      opacity: isActive ? 1 : 0.25,
      zIndex: isActive ? 10 : 1,
      duration: 0.8, ease: "power3.out", overwrite: "auto"
    })
  })
}

watch(activeIndex, updateSliderAnimation)
watch(() => props.data, async () => { await nextTick(); updateSliderAnimation() }, { deep: true })

// --- DRAG / SWIPE LOGIC ---
let isDragging = false
let startX = 0, currentX = 0

const onDragStart = (e) => { isDragging = true; startX = e.touches ? e.touches[0].clientX : e.clientX; currentX = startX }
const onDragMove = (e) => { if (!isDragging) return; currentX = e.touches ? e.touches[0].clientX : e.clientX }
const onDragEnd = () => {
  if (!isDragging) return
  isDragging = false
  const diff = startX - currentX
  if (diff > 50 && activeIndex.value < top10Data.value.length - 1) activeIndex.value++
  else if (diff < -50 && activeIndex.value > 0) activeIndex.value--
  else updateSliderAnimation()
}

const nextCard = () => { if (activeIndex.value < top10Data.value.length - 1) activeIndex.value++ }
const prevCard = () => { if (activeIndex.value > 0) activeIndex.value-- }

const handleItemClick = (item, index) => {
  if (activeIndex.value !== index) { activeIndex.value = index; return }
  openPreview(item, index)
}

onMounted(() => { window.addEventListener('resize', updateSliderAnimation); setTimeout(updateSliderAnimation, 100) })
onBeforeUnmount(() => { window.removeEventListener('resize', updateSliderAnimation) })

// --- TỐI ƯU HÓA GSAP PREVIEW OVERLAY ---
const previewItem = ref(null)
const previewOverlayRef = ref(null), fxImageWrapRef = ref(null), fxPanelRef = ref(null)
const isPreviewAnimating = ref(false)
let previewTl = null, previewOriginRect = null

const previewImage = computed(() => previewItem.value?.image || previewItem.value?.avatar || previewItem.value?.authorAvatar || '')
const previewTitle = computed(() => previewItem.value?.title || previewItem.value?.name || '')

const openPreview = async (item, originIndex) => {
  if (isPreviewAnimating.value) return
  const originImgEl = cardRefs.value[originIndex]?.querySelector('.item-image-wrapper img')
  previewOriginRect = originImgEl?.getBoundingClientRect() ?? null

  previewItem.value = item
  await nextTick()

  const overlayEl = previewOverlayRef.value, imgWrapEl = fxImageWrapRef.value, panelEl = fxPanelRef.value
  if (!overlayEl || !imgWrapEl || !panelEl) return

  isPreviewAnimating.value = true
  if (previewTl) previewTl.kill()

  const isNarrow = window.innerWidth < 1024
  // 🚀 Tỷ lệ xịn: Ảnh to 45% chiều rộng, Panel vắt ngang qua ảnh
  const targetImgW = isNarrow ? window.innerWidth * 0.85 : window.innerWidth * 0.45 
  const targetImgH = isNarrow ? targetImgW * 1.2 : window.innerHeight * 0.75 
  const targetImgLeft = isNarrow ? (window.innerWidth - targetImgW) / 2 : window.innerWidth * 0.1 
  const targetImgTop = (window.innerHeight - targetImgH) / 2
  
  const panelW = isNarrow ? window.innerWidth * 0.85 : 460
  // Khối chữ đè lên ảnh 120px trên Desktop
  const panelLeft = isNarrow ? targetImgLeft : targetImgLeft + targetImgW - 120 
  const panelTop = isNarrow ? targetImgTop + targetImgH - 60 : (window.innerHeight - 400) / 2

  const startRect = previewOriginRect || { left: window.innerWidth/2, top: window.innerHeight/2, width: 0, height: 0 }
  
  gsap.set(overlayEl, { opacity: 0 })
  gsap.set(imgWrapEl, { opacity: 1, left: startRect.left, top: startRect.top, width: startRect.width, height: startRect.height, borderRadius: 26, zIndex: 1001 })
  gsap.set(panelEl, { opacity: 0, x: isNarrow ? 0 : 100, y: isNarrow ? 50 : 0, left: panelLeft, top: panelTop, width: panelW, zIndex: 1002 })

  // Animation bay ra mượt mà
  previewTl = gsap.timeline({ defaults: { ease: 'power4.out' }, onComplete: () => { isPreviewAnimating.value = false } })
  previewTl.to(overlayEl, { opacity: 1, duration: 0.5, backdropFilter: "blur(20px)" }, 0)
    .to(imgWrapEl, { left: targetImgLeft, top: targetImgTop, width: targetImgW, height: targetImgH, borderRadius: 30, duration: 0.8 }, 0)
    .to(panelEl, { opacity: 1, x: 0, y: 0, duration: 0.8 }, 0.15)
}

const closePreview = () => {
  if (!previewItem.value || isPreviewAnimating.value) return
  isPreviewAnimating.value = true
  gsap.to([previewOverlayRef.value, fxImageWrapRef.value, fxPanelRef.value], {
    opacity: 0, duration: 0.3, onComplete: () => { previewItem.value = null; isPreviewAnimating.value = false; }
  })
}

const goToDetail = () => {
  if (!previewItem.value) return
  router.push(props.category === 'chefs' ? `/profile/${previewItem.value.id}` : `/post/${previewItem.value.id}`)
}
</script>

<style scoped lang="scss">
/* 1. CONTAINER CHÍNH */
.cinematic-horizontal-showcase {
  position: relative;
  width: 100%;
  height: 100vh;
  min-height: 700px;
  background-color: #000;
  overflow: hidden;
  font-family: 'Playfair Display', serif;
  padding-top: 100px;
}

/* 2. LỚP NỀN (BACKGROUND PARALLAX) */
.bg-layer { position: absolute; inset: 0; z-index: 1; }
.bg-image {
  position: absolute; inset: -5%; background-size: cover; background-position: center;
  background-repeat: no-repeat; filter: blur(28px) brightness(0.62) saturate(1.08); transform: scale(1.05);
}
.bg-overlay {
  position: absolute; inset: 0; background: radial-gradient(circle at center, rgba(0, 0, 0, 0.18), rgba(0, 0, 0, 0.95) 78%); z-index: 2;
}
.fade-bg-enter-active, .fade-bg-leave-active { transition: opacity 1s ease-in-out; }
.fade-bg-enter-from, .fade-bg-leave-to { opacity: 0; }

/* 3. HEADER & TABS */
.cinematic-header-container {
  position: relative; z-index: 10;
  display: flex; justify-content: space-between; align-items: flex-end;
  padding: 0 5%; margin-bottom: 20px;
}
.header-titles {
  color: #fff;
  .sub-tag { font-family: 'Mulish', sans-serif; font-size: 0.8rem; font-weight: 700; letter-spacing: 4px; color: #ea580c; text-transform: uppercase; }
  .main-heading { font-size: 3rem; margin: 5px 0 0; font-weight: 400; letter-spacing: 2px; }
  .italic-first { font-style: italic; font-size: 1.2em; color: #fed7aa; }
}

.glass-tabs {
  display: flex; gap: 8px; background: rgba(255, 255, 255, 0.05); padding: 6px; border-radius: 30px; border: 1px solid rgba(255, 255, 255, 0.1); backdrop-filter: blur(10px);
  button {
    font-family: 'Mulish', sans-serif; background: transparent; color: #d6d3d1; border: none; padding: 8px 20px; border-radius: 20px; font-weight: 600; cursor: pointer; transition: 0.3s;
    &:hover { color: #fff; }
    &.active { background: linear-gradient(135deg, #ea580c, #f97316); color: #fff; box-shadow: 0 4px 15px rgba(234, 88, 12, 0.4); }
  }
}

/* 4. CAROUSEL & TRACK (DÙNG GSAP) */
.slider-wrapper { position: relative; z-index: 5; width: 100%; display: flex; align-items: center; justify-content: center; }
.carousel-viewport {
  width: 100%; overflow: hidden; padding: 20px 0; touch-action: pan-y; cursor: grab;
  &:active { cursor: grabbing; }
}
.carousel-track { display: flex; gap: 60px; width: max-content; }

/* Nút điều hướng PC */
.nav-btn {
  position: absolute; top: 50%; transform: translateY(-50%); z-index: 20;
  width: 60px; height: 60px; border-radius: 50%;
  background: rgba(255, 255, 255, 0.05); border: 1px solid rgba(255, 255, 255, 0.1);
  color: #fff; font-size: 1.5rem; cursor: pointer; backdrop-filter: blur(10px); transition: all 0.3s ease;
  &:hover { background: #ea580c; border-color: #ea580c; box-shadow: 0 0 20px rgba(234, 88, 12, 0.5); transform: translateY(-50%) scale(1.1); }
  &.hidden { opacity: 0; pointer-events: none; transform: translateY(-50%) scale(0.8); }
  &.prev-btn { left: 2%; } &.next-btn { right: 2%; }
}

/* 5. ITEM BÊN TRONG */
.carousel-item {
  position: relative; flex: 0 0 360px; display: flex; flex-direction: column; align-items: center;
  transition: all 0.55s cubic-bezier(0.25, 1, 0.5, 1); cursor: pointer;
}

.premium-badge-float {
  position: absolute; top: 35px; right: 0; z-index: 10;
  background: linear-gradient(135deg, #ffd700, #ff8c00); color: black; padding: 4px 10px; border-radius: 10px; font-weight: bold; font-size: 0.75rem;
}

.item-top-info { margin-bottom: 18px; opacity: 0; transform: translateY(20px); transition: all 0.4s ease; }
.carousel-item[style*="opacity: 1"] .item-top-info { opacity: 1; transform: translateY(0); }

.rank-number { font-family: 'Mulish', sans-serif; color: #fed7aa; font-size: 0.95rem; font-weight: 800; letter-spacing: 4px; }

.item-image-wrapper {
  position: relative; width: 100%; height: 460px; border-radius: 26px; overflow: hidden; box-shadow: 0 20px 55px rgba(0, 0, 0, 0.75); transition: all 0.55s ease;
  img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.9s ease; }
  .image-gradient { position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, transparent 40%); opacity: 0; transition: 0.5s; }
}

.carousel-item[style*="opacity: 1"] .item-image-wrapper { box-shadow: 0 28px 70px rgba(234, 88, 12, 0.45); }
.carousel-item[style*="opacity: 1"]:hover .item-image-wrapper img { transform: scale(1.05); }
.carousel-item[style*="opacity: 1"]:hover .image-gradient { opacity: 1; }

.item-bottom-info { margin-top: 22px; text-align: center; color: #fff; opacity: 0; transform: translateY(-18px); transition: all 0.45s ease; }
.carousel-item[style*="opacity: 1"] .item-bottom-info { opacity: 1; transform: translateY(0); }

.item-title { font-size: 1.9rem; font-weight: 700; margin: 0 0 8px; line-height: 1.2; }
.item-meta { font-family: 'Mulish', sans-serif; font-size: 0.85rem; color: #d6d3d1; font-weight: 600; }
.meta-author { text-transform: uppercase; letter-spacing: 2px; }
.meta-dot { margin: 0 8px; color: #78716c; }
.meta-score { color: #fed7aa; }

/* 6. EMPTY STATE */
.empty-state-container {
  display: flex; justify-content: center; align-items: center; height: 400px; width: 100%; color: #888; text-align: center; z-index: 10; position: relative;
  .empty-content { background: rgba(255, 255, 255, 0.05); padding: 40px; border-radius: 20px; border: 1px dashed rgba(255, 255, 255, 0.1); backdrop-filter: blur(5px); }
  .empty-icon { font-size: 3rem; color: #ea580c; margin-bottom: 15px; opacity: 0.5; }
}

/* =======================================================
   💎 7. GSAP PREVIEW OVERLAY (PREMIUM EDITION MỚI)
======================================================= */
.preview-overlay {
  position: fixed; inset: 0; z-index: 9999;
  background: radial-gradient(circle at center, rgba(15, 15, 17, 0.4), rgba(0, 0, 0, 0.9));
}

.preview-close {
  position: absolute; top: 40px; right: 40px; z-index: 10003;
  width: 55px; height: 55px; border-radius: 50%;
  background: rgba(255, 255, 255, 0.05); border: 1px solid rgba(255, 255, 255, 0.1);
  color: #fff; font-size: 1.5rem; cursor: pointer; transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
  backdrop-filter: blur(15px);
  display: flex; align-items: center; justify-content: center;
  
  &:hover { 
    background: rgba(234, 88, 12, 0.8); border-color: #ea580c; 
    transform: rotate(90deg) scale(1.1); box-shadow: 0 0 30px rgba(234, 88, 12, 0.5);
  }
}

.fx-image-wrap {
  position: fixed; overflow: hidden; 
  box-shadow: 0 40px 100px rgba(0,0,0,0.9); /* Bóng đổ khổng lồ tạo độ sâu */
  border: 1px solid rgba(255, 255, 255, 0.05);
  img { width: 100%; height: 100%; object-fit: cover; }
}

.fx-panel {
  position: fixed; display: flex; flex-direction: column; justify-content: center;
  /* Hiệu ứng kính mờ tối thượng */
  background: linear-gradient(135deg, rgba(20, 20, 22, 0.85) 0%, rgba(10, 10, 12, 0.95) 100%);
  backdrop-filter: blur(30px) saturate(1.5);
  border: 1px solid rgba(255,255,255,0.08); 
  border-radius: 32px; padding: 50px 40px;
  box-shadow: -20px 20px 80px rgba(0, 0, 0, 0.8), inset 0 0 0 1px rgba(255, 255, 255, 0.05);
  font-family: 'Mulish', sans-serif;
  color: #e7e5e4;
  
  .preview-tag { 
    color: #a8a29e; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 3px; margin-bottom: 15px; font-weight: 700;
  }
  .text-gold { color: #FFD700; text-shadow: 0 0 10px rgba(255, 215, 0, 0.3); }
  
  .preview-title { 
    font-size: 2.8rem; font-family: 'Playfair Display', serif; margin: 0 0 35px 0; line-height: 1.15; color: #fff;
    text-shadow: 0 4px 20px rgba(0,0,0,0.5);
  }
  
  /* Grid chỉ số thay vì chuỗi text dài */
  .panel-stats {
    display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 35px;
    .stat-box {
      background: rgba(255, 255, 255, 0.03); padding: 18px 20px; border-radius: 16px; 
      border: 1px solid rgba(255, 255, 255, 0.02);
      display: flex; flex-direction: column; transition: 0.3s;
      
      &:hover { background: rgba(255, 255, 255, 0.05); transform: translateY(-3px); }
      
      .stat-label { font-size: 0.8rem; color: #a8a29e; margin-bottom: 8px; text-transform: uppercase; letter-spacing: 1px;}
      .stat-value { font-size: 1.3rem; font-weight: bold; color: #fff; line-height: 1.4;}
      
      &.highlight {
        background: rgba(234, 88, 12, 0.08); border-color: rgba(234, 88, 12, 0.2);
        .stat-value { color: #ea580c; font-size: 1.6rem; text-shadow: 0 0 15px rgba(234, 88, 12, 0.3); }
      }
    }
  }

  .preview-desc { 
    color: #d6d3d1; font-style: italic; line-height: 1.7; margin: 0 0 40px 0; 
    border-left: 4px solid #ea580c; padding-left: 20px; font-size: 1.05rem;
  }
  
  .preview-cta {
    background: linear-gradient(135deg, #ea580c, #f97316); color: #fff; border: none; padding: 18px 35px; 
    border-radius: 16px; font-size: 1.15rem; font-weight: 800; letter-spacing: 1px; cursor: pointer; text-transform: uppercase;
    box-shadow: 0 15px 35px rgba(234, 88, 12, 0.35); transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
    align-self: flex-start; /* Giữ nút không bị kéo dài hết bề ngang */
    
    &:hover { 
      transform: translateY(-5px) scale(1.02); 
      box-shadow: 0 20px 45px rgba(234, 88, 12, 0.5); 
    }
  }
}

/* 8. RESPONSIVE SYSTEM */
@media (min-width: 1440px) {
  .carousel-item { flex: 0 0 400px; }
  .item-image-wrapper { height: 520px; }
}
@media (max-width: 1200px) {
  .cinematic-horizontal-showcase { padding-top: 100px; }
  .header-titles .main-heading { font-size: 2.6rem; }
  .carousel-item { flex: 0 0 320px; }
  .carousel-track { gap: 40px; }
  .item-image-wrapper { height: 420px; }
}
@media (max-width: 1024px) {
  .fx-panel {
    padding: 30px; border-radius: 24px;
    .preview-title { font-size: 2.2rem; margin-bottom: 25px; }
    .panel-stats { gap: 15px; margin-bottom: 25px; }
  }
}
@media (max-width: 900px) {
  .cinematic-header-container { flex-direction: column; align-items: flex-start; gap: 20px; }
  .header-titles .main-heading { font-size: 2.2rem; }
  .carousel-item { flex: 0 0 280px; }
  .carousel-track { gap: 30px; }
  .item-image-wrapper { height: 380px; border-radius: 20px; }
  .item-title { font-size: 1.6rem; }
  .nav-btn { display: none; }
}
@media (max-width: 768px) {
  .preview-close { top: 20px; right: 20px; width: 45px; height: 45px; font-size: 1.2rem;}
  .fx-panel { 
    padding: 25px; border-radius: 20px;
    box-shadow: 0 -10px 40px rgba(0,0,0,0.5); /* Đảo bóng lên trên vì nằm dưới ảnh */
    .preview-title { font-size: 1.8rem; }
    .preview-cta { padding: 15px 20px; font-size: 1rem; width: 100%; text-align: center;} /* Nút full ngang trên mobile */
    .panel-stats { display: flex; flex-direction: column; gap: 10px; } /* Đưa stat thành cột dọc */
  }
}
@media (max-width: 640px) {
  .cinematic-horizontal-showcase { padding-top: 80px; min-height: 600px; }
  .header-titles .main-heading { font-size: 1.8rem; }
  .header-titles .sub-tag { font-size: 0.7rem; letter-spacing: 2px; }
  .carousel-item { flex: 0 0 240px; }
  .carousel-track { gap: 20px; }
  .item-image-wrapper { height: 320px; border-radius: 18px; }
  .item-title { font-size: 1.4rem; }
}
@media (max-width: 380px) {
  .carousel-item { flex: 0 0 200px; }
  .item-image-wrapper { height: 280px; }
  .header-titles .main-heading { font-size: 1.5rem; }
  .item-meta { font-size: 0.75rem; }
}
@media (max-height: 500px) {
  .cinematic-horizontal-showcase { padding-top: 60px; }
  .item-image-wrapper { height: 200px; }
  .item-top-info, .item-bottom-info { margin: 8px 0; }
}
</style>