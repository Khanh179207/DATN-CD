<template>
  <section class="cinematic-horizontal-showcase">
    <!-- Background mờ theo item đang active -->
    <div class="bg-layer">
      <transition-group name="fade-bg" tag="div">
        <div
          v-for="(item, index) in top10Data"
          v-show="activeIndex === index"
          :key="'bg-' + item.id"
          class="bg-image"
          :style="{ backgroundImage: `url(${item.image || item.authorAvatar || item.avatar})` }"
        ></div>
      </transition-group>
      <div class="bg-overlay"></div>
    </div>

    <!-- Header -->
    <div class="cinematic-header">
      <span class="sub-tag">
        {{ category === 'dishes' ? '// TOP 10 TUYỆT TÁC' : '// TOP 10 THÀNH VIÊN' }}
      </span>
      <h2 class="main-heading">
        <span class="italic-first">T</span>op
        <span class="italic-first">G</span>allery
      </h2>
    </div>

    <!-- Centered carousel -->
    <div
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
        <div class="item-top-info">
          <span class="rank-number">
            TOP {{ startRank + index }}
          </span>
        </div>

        <div class="item-image-wrapper">
          <img
            :src="item.image || item.authorAvatar || item.avatar"
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
              <template v-if="category === 'dishes'">
                {{ item.pts || 0 }} PTS
              </template>
              <template v-else>
                {{ item.followers || 0 }} Followers
              </template>
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Preview overlay khi click item đang ở giữa -->
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
        </span>
        <h3 class="preview-title">{{ previewTitle }}</h3>

        <p class="preview-meta">
          <template v-if="category === 'dishes'">
            By {{ previewItem.authorName || 'GoMet Chef' }}
            · {{ previewItem.pts || 0 }} PTS
          </template>
          <template v-else>
            {{ previewItem.postCount || 0 }} bài viết
            · {{ previewItem.followers || 0 }} followers
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
  // Thứ hạng bắt đầu cho danh sách (ví dụ 2 nếu Top 1 đã nằm ở hero)
  startRank: { type: Number, default: 1 }
})

const router = useRouter()
const carouselRef = ref(null)
const activeIndex = ref(0)
const previewOverlayRef = ref(null)
const fxImageWrapRef = ref(null)
const fxPanelRef = ref(null)
const isPreviewAnimating = ref(false)
let previewTl = null
let previewOriginRect = null

// dùng computed để truy cập startRank trong template
const startRank = computed(() => props.startRank || 1)

// lấy tối đa 10 phần tử đầu tiên
const top10Data = computed(() => props.data.slice(0, 10))

// xác định item center theo scroll
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
    if (diff < minDiff) {
      minDiff = diff
      closestIndex = index
    }
  })

  activeIndex.value = closestIndex
}

// drag to scroll
const isDragging = ref(false)
let startX = 0
let scrollLeft = 0

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

// click item: nếu chưa center thì đưa vào giữa, nếu rồi thì mở preview
const handleItemClick = (item, index) => {
  if (activeIndex.value !== index) {
    const container = carouselRef.value
    const children = container.querySelectorAll('.carousel-item')
    const target = children[index]

    container.scrollTo({
      left:
        target.offsetLeft -
        container.clientWidth / 2 +
        target.clientWidth / 2,
      behavior: 'smooth'
    })
    activeIndex.value = index
    return
  }

  openPreview(item, index)
}

// preview overlay
const previewItem = ref(null)

const previewImage = computed(() => {
  if (!previewItem.value) return ''
  return (
    previewItem.value.image ||
    previewItem.value.authorAvatar ||
    previewItem.value.avatar ||
    ''
  )
})

const previewTitle = computed(() => {
  if (!previewItem.value) return ''
  return previewItem.value.title || previewItem.value.name || ''
})

const openPreview = async (item, originIndex) => {
  if (isPreviewAnimating.value) return

  // lấy vị trí ảnh gốc để tạo hiệu ứng “phóng to từ list”
  const container = carouselRef.value
  const children = container?.querySelectorAll?.('.carousel-item')
  const originImgEl =
    children?.[originIndex]?.querySelector?.('.item-image-wrapper img')
  previewOriginRect = originImgEl?.getBoundingClientRect?.() ?? null

  previewItem.value = item
  await nextTick()

  const overlayEl = previewOverlayRef.value
  const imgWrapEl = fxImageWrapRef.value
  const panelEl = fxPanelRef.value
  if (!overlayEl || !imgWrapEl || !panelEl) return

  isPreviewAnimating.value = true
  if (previewTl) previewTl.kill()

  // overlay fade
  gsap.set(overlayEl, { opacity: 0 })

  // start rect fallback nếu không lấy được vị trí ảnh gốc
  const fallbackW = Math.min(window.innerWidth * 0.42, 520)
  const fallbackH = fallbackW * 1.25
  const startRect = previewOriginRect || {
    left: window.innerWidth / 2 - fallbackW / 2,
    top: window.innerHeight / 2 - fallbackH / 2,
    width: fallbackW,
    height: fallbackH
  }

  // target: ảnh bay về một phía, panel trượt ra
  const isNarrow = window.innerWidth < 900
  const imgAspect =
    startRect.height && startRect.width
      ? startRect.height / startRect.width
      : 1.25

  const maxImgW = isNarrow
    ? Math.min(window.innerWidth * 0.78, 440)
    : Math.min(window.innerWidth * 0.36, 560)

  const targetImgW = maxImgW
  const targetImgH = targetImgW * imgAspect

  const targetImgLeft = isNarrow
    ? (window.innerWidth - targetImgW) / 2
    : Math.max(48, Math.floor(window.innerWidth * 0.10))

  const targetImgTop = Math.max(84, (window.innerHeight - targetImgH) / 2)

  const panelGap = 28
  const panelW = isNarrow
    ? Math.min(window.innerWidth * 0.88, 720)
    : Math.min(
        520,
        window.innerWidth - (targetImgLeft + targetImgW + panelGap + 48)
      )

  const panelLeft = isNarrow
    ? (window.innerWidth - panelW) / 2
    : targetImgLeft + targetImgW + panelGap

  const panelTop = isNarrow
    ? Math.min(targetImgTop + targetImgH + 18, window.innerHeight - 240)
    : targetImgTop + 10

  // set start
  gsap.set(imgWrapEl, {
    opacity: 1,
    left: startRect.left,
    top: startRect.top,
    width: startRect.width,
    height: startRect.height,
    borderRadius: 26
  })

  gsap.set(panelEl, {
    opacity: 0,
    x: 70,
    left: panelLeft,
    top: panelTop,
    width: panelW
  })

  const imgEl = imgWrapEl.querySelector('img')
  if (imgEl) gsap.set(imgEl, { scale: 1.06 })

  previewTl = gsap.timeline({
    defaults: { ease: 'power3.out' },
    onComplete: () => {
      isPreviewAnimating.value = false
    }
  })

  previewTl
    .to(overlayEl, { opacity: 1, duration: 0.26 }, 0)
    .to(
      imgWrapEl,
      {
        left: targetImgLeft,
        top: targetImgTop,
        width: targetImgW,
        height: targetImgH,
        duration: 0.85,
        ease: 'power4.out'
      },
      0.02
    )
    .to(imgEl, { scale: 1, duration: 0.85 }, 0.02)
    .to(panelEl, { opacity: 1, x: 0, duration: 0.55 }, 0.58)
}

const closePreview = async () => {
  if (!previewItem.value || isPreviewAnimating.value) return

  const overlayEl = previewOverlayRef.value
  const imgWrapEl = fxImageWrapRef.value
  const panelEl = fxPanelRef.value
  if (!overlayEl || !imgWrapEl || !panelEl) {
    previewItem.value = null
    previewOriginRect = null
    return
  }

  isPreviewAnimating.value = true
  if (previewTl) previewTl.kill()

  const backRect = previewOriginRect
  const hasBack = !!backRect
  const imgEl = imgWrapEl.querySelector('img')

  previewTl = gsap.timeline({
    defaults: { ease: 'power2.inOut' },
    onComplete: () => {
      previewItem.value = null
      previewOriginRect = null
      isPreviewAnimating.value = false
    }
  })

  previewTl
    .to(panelEl, { opacity: 0, x: 40, duration: 0.2 }, 0)
    .to(imgEl, { scale: 1.08, duration: 0.35 }, 0)
    .to(
      imgWrapEl,
      hasBack
        ? {
            left: backRect.left,
            top: backRect.top,
            width: backRect.width,
            height: backRect.height,
            duration: 0.5,
            ease: 'power3.inOut'
          }
        : { opacity: 0, duration: 0.25 },
      0.06
    )
    .to(overlayEl, { opacity: 0, duration: 0.25 }, 0.28)
}

const goToDetail = () => {
  if (!previewItem.value) return
  const id = previewItem.value.id
  const route =
    props.category === 'chefs' ? `/profile/${id}` : `/post/${id}`
  router.push(route)
}
</script>

<style scoped lang="scss" src="@/components/leaderboard/CinematicList.scss"></style>