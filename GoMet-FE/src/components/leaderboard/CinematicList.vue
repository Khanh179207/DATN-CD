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
      class="preview-overlay"
      @click.self="closePreview"
    >
      <div class="preview-card">
        <button class="preview-close" @click="closePreview">×</button>

        <div class="preview-main">
          <div class="preview-image-wrap">
            <img
              :src="previewImage"
              :alt="previewTitle"
            />
          </div>

          <div class="preview-content">
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
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  data: { type: Array, required: true },
  category: { type: String, default: 'dishes' },
  // Thứ hạng bắt đầu cho danh sách (ví dụ 2 nếu Top 1 đã nằm ở hero)
  startRank: { type: Number, default: 1 }
})

const router = useRouter()
const carouselRef = ref(null)
const activeIndex = ref(0)

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

  openPreview(item)
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

const openPreview = (item) => {
  previewItem.value = item
}

const closePreview = () => {
  previewItem.value = null
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