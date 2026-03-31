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
      <div class="time-tabs">
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

    <div
      v-if="top10Data.length > 0"
      class="carousel-container"
      ref="carouselRef"
      :class="{ 'is-dragging': isDragging }"
      @scroll="handleScroll"
      @mousedown="startDrag"
      @mousemove="onDrag"
      @mouseup="stopDrag"
      @mouseleave="stopDrag"
    >
      <div
        v-for="(item, index) in top10Data"
        :key="item.id"
        class="carousel-item"
        :class="{ 'is-active': activeIndex === index }"
        @click="handleItemClick(item, index)"
      >
        <div v-if="item.isPremium === 1" class="premium-badge-float">
          👑 Premium
        </div>

        <div class="item-top-info">
          <span class="rank-number">
            TOP {{ startRank + index }}
          </span>
        </div>

        <div class="item-image-wrapper">
          <img
            :src="item.image || item.avatar || item.authorAvatar"
            :alt="item.title || item.name"
            draggable="false"
          />
        </div>

        <div class="item-bottom-info">
          <h3 class="item-title">
            {{ item.title || item.name }}
          </h3>
          <div class="item-meta">
            <span class="meta-author" v-if="category === 'dishes'">
              By {{ item.authorName || 'GoMet Chef' }}
            </span>
            <span class="meta-author" v-else>
              {{ item.postCount || 0 }} Tuyệt tác
            </span>
            
            <span class="meta-dot">•</span>
            
            <span class="meta-score">
              {{ item.pts || item.totalPts || 0 }} PTS
            </span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="empty-state-container">
      <div class="empty-content">
        <i class="fas fa-crown empty-icon"></i>
        <h3>Chưa có bảng xếp hạng {{ currentTimeframe === 'day' ? 'Hôm nay' : currentTimeframe === 'month' ? 'Tháng này' : 'Năm nay' }}</h3>
        <p>Hãy tương tác để ghi danh lên bảng vàng!</p>
      </div>
    </div>

    <div
      v-if="previewItem"
      ref="previewOverlayRef"
      class="preview-overlay"
      @click.self="closePreview"
    >
      <button class="preview-close" @click="closePreview">×</button>

      <div ref="fxImageWrapRef" class="fx-image-wrap" aria-hidden="true">
        <img :src="previewImage" :alt="previewTitle" />
      </div>

      <div ref="fxPanelRef" class="fx-panel" role="dialog" aria-modal="true">
        <span class="preview-tag">
          {{ category === 'dishes' ? 'Tuyệt tác nổi bật' : 'Thành viên xuất sắc' }}
          <span v-if="previewItem.isPremium === 1" style="color: #ffd700"> | 👑 VIP</span>
        </span>
        <h3 class="preview-title">{{ previewTitle }}</h3>

        <p class="preview-meta">
          <template v-if="category === 'dishes'">
            By {{ previewItem.authorName || 'GoMet Chef' }}
            · <span class="highlight-pts">{{ previewItem.pts || 0 }} PTS</span>
          </template>
          <template v-else>
            {{ previewItem.postCount || 0 }} bài viết
            · {{ previewItem.followers || 0 }} followers
            <br>
            <span class="highlight-pts">Tổng tương tác: {{ previewItem.pts || previewItem.totalPts || 0 }} PTS</span>
          </template>
        </p>

        <p v-if="previewItem.description" class="preview-desc">
          {{ previewItem.description }}
        </p>

        <button class="preview-cta" @click="goToDetail">
          {{ category === 'dishes' ? 'Xem chi tiết công thức' : 'Xem hồ sơ' }}
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'

const props = defineProps({
  data: { type: Array, required: true },
  category: { type: String, default: 'dishes' },
  startRank: { type: Number, default: 1 },
  // 🔥 Đặt mặc định là 'month' để khớp với logic default trong hàm Java của sếp
  currentTimeframe: { type: String, default: 'month' } 
})

const emit = defineEmits(['update-timeframe'])
const router = useRouter()

// --- BIẾN ĐIỀU KHIỂN ---
const carouselRef = ref(null)
const activeIndex = ref(0)
const isDragging = ref(false)
let startX = 0
let scrollLeft = 0

const previewOverlayRef = ref(null)
const fxImageWrapRef = ref(null)
const fxPanelRef = ref(null)
const isPreviewAnimating = ref(false)
const previewItem = ref(null)
let previewTl = null
let previewOriginRect = null

const top10Data = computed(() => props.data.slice(0, 10))

// XỬ LÝ CLICK THỜI GIAN
const handleTimeClick = (time) => {
  emit('update-timeframe', time)
}

// --- LOGIC DRAG & SCROLL ---
const handleScroll = () => {
  if (!carouselRef.value || isDragging.value) return
  const container = carouselRef.value
  const centerPosition = container.scrollLeft + container.clientWidth / 2
  let closestIndex = 0
  let minDiff = Infinity
  const children = container.querySelectorAll('.carousel-item')
  children.forEach((child, index) => {
    const childCenter = child.offsetLeft + child.clientWidth / 2
    const diff = Math.abs(centerPosition - childCenter)
    if (diff < minDiff) { minDiff = diff; closestIndex = index; }
  })
  activeIndex.value = closestIndex
}

const startDrag = (e) => {
  isDragging.value = true
  startX = e.pageX - carouselRef.value.offsetLeft
  scrollLeft = carouselRef.value.scrollLeft
}

const onDrag = (e) => {
  if (!isDragging.value) return
  e.preventDefault()
  const x = e.pageX - carouselRef.value.offsetLeft
  const walk = (x - startX) * 1.5
  carouselRef.value.scrollLeft = scrollLeft - walk
}

const stopDrag = () => {
  isDragging.value = false
  setTimeout(() => handleScroll(), 50)
}

const handleItemClick = (item, index) => {
  if (activeIndex.value !== index) {
    const container = carouselRef.value
    const target = container.querySelectorAll('.carousel-item')[index]
    container.scrollTo({
      left: target.offsetLeft - container.clientWidth / 2 + target.clientWidth / 2,
      behavior: 'smooth'
    })
    activeIndex.value = index
    return
  }
  openPreview(item, index)
}

// --- LOGIC PREVIEW GSAP ---
const previewImage = computed(() => {
  if (!previewItem.value) return ''
  return previewItem.value.image || previewItem.value.avatar || previewItem.value.authorAvatar || ''
})
const previewTitle = computed(() => {
  if (!previewItem.value) return ''
  return previewItem.value.title || previewItem.value.name || ''
})

const openPreview = async (item, originIndex) => {
  if (isPreviewAnimating.value) return
  const container = carouselRef.value
  const children = container?.querySelectorAll?.('.carousel-item')
  const originImgEl = children?.[originIndex]?.querySelector?.('.item-image-wrapper img')
  previewOriginRect = originImgEl?.getBoundingClientRect?.() ?? null

  previewItem.value = item
  await nextTick()

  const overlayEl = previewOverlayRef.value
  const imgWrapEl = fxImageWrapRef.value
  const panelEl = fxPanelRef.value
  if (!overlayEl || !imgWrapEl || !panelEl) return

  isPreviewAnimating.value = true
  if (previewTl) previewTl.kill()

  const isNarrow = window.innerWidth < 1024
  const targetImgW = isNarrow ? window.innerWidth * 0.85 : window.innerWidth * 0.4
  const targetImgH = targetImgW * 1.2 
  const targetImgLeft = isNarrow ? (window.innerWidth - targetImgW) / 2 : window.innerWidth * 0.12
  const targetImgTop = isNarrow ? 80 : (window.innerHeight - targetImgH) / 2
  const panelW = isNarrow ? window.innerWidth * 0.85 : 420
  const panelLeft = isNarrow ? (window.innerWidth - panelW) / 2 : targetImgLeft + targetImgW + 40
  const panelTop = isNarrow ? targetImgTop + targetImgH + 20 : targetImgTop + 40

  const startRect = previewOriginRect || { left: window.innerWidth/2, top: window.innerHeight/2, width: 0, height: 0 }
  
  gsap.set(overlayEl, { opacity: 0 })
  gsap.set(imgWrapEl, {
    opacity: 1, left: startRect.left, top: startRect.top, width: startRect.width, height: startRect.height, borderRadius: 20, zIndex: 1001
  })
  gsap.set(panelEl, {
    opacity: 0, x: 50, left: panelLeft, top: panelTop, width: panelW, zIndex: 1002
  })

  previewTl = gsap.timeline({ defaults: { ease: 'power4.out' }, onComplete: () => { isPreviewAnimating.value = false } })
  previewTl.to(overlayEl, { opacity: 1, duration: 0.4 }, 0)
    .to(imgWrapEl, { left: targetImgLeft, top: targetImgTop, width: targetImgW, height: targetImgH, duration: 0.8 }, 0)
    .to(panelEl, { opacity: 1, x: 0, duration: 0.6 }, 0.3)
}

const closePreview = async () => {
  if (!previewItem.value || isPreviewAnimating.value) return
  isPreviewAnimating.value = true
  gsap.to([previewOverlayRef.value, fxImageWrapRef.value, fxPanelRef.value], {
    opacity: 0, duration: 0.3, onComplete: () => { previewItem.value = null; isPreviewAnimating.value = false; }
  })
}

const goToDetail = () => {
  if (!previewItem.value) return
  const id = previewItem.value.id
  const route = props.category === 'chefs' ? `/profile/${id}` : `/post/${id}`
  router.push(route)
}
</script>

<style scoped lang="scss">
.cinematic-header-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: 0 5%;
  margin-bottom: 20px;
  position: relative; 
  z-index: 100;
  pointer-events: auto;
}

.time-tabs {
  display: flex;
  gap: 10px;
  position: relative;
  z-index: 101;
  
  button {
    background: rgba(255,255,255,0.05);
    border: 1px solid rgba(255,255,255,0.1);
    color: #888;
    padding: 6px 18px;
    border-radius: 20px;
    cursor: pointer;
    transition: 0.3s;
    pointer-events: auto;
    
    &:hover { background: rgba(255,255,255,0.1); color: #fff; }
    &.active { background: #ea580c; color: white; border-color: #ea580c; }
  }
}

.highlight-pts {
  color: #ea580c;
  font-weight: 800;
}

.premium-badge-float {
  position: absolute;
  top: 15px; right: 15px;
  background: linear-gradient(45deg, #ffd700, #ff8c00);
  color: black; padding: 4px 10px; border-radius: 10px;
  font-weight: bold; font-size: 0.75rem; z-index: 5;
}

.empty-state-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  width: 100%;
  color: #888;
  text-align: center;
  
  .empty-content {
    background: rgba(255, 255, 255, 0.05);
    padding: 40px;
    border-radius: 20px;
    border: 1px dashed rgba(255, 255, 255, 0.1);
  }

  .empty-icon {
    font-size: 3rem;
    color: #ea580c;
    margin-bottom: 15px;
    opacity: 0.5;
  }
}

@import "@/components/leaderboard/CinematicList.scss";
</style>