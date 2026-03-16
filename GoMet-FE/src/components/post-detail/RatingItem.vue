<template>
  <div class="review-card">
    <div class="review-header">
      <img :src="avatar" alt="avatar" class="review-avatar" />
      <div class="review-meta">
        <div class="review-top">
          <span class="review-author">{{ maskedName }}</span>
          <span class="review-stars">
            <span v-for="n in 5" :key="n" :class="['star', { filled: n <= review.rate }]">★</span>
          </span>
        </div>
        <div class="review-sub">
          <span class="review-time">{{ formatTime(review.createdAt) }}</span>
        </div>
      </div>
    </div>

    <!-- <p v-if="review.comment" class="review-text">{{ review.comment }}</p> -->

    <div v-if="review.imageUrls && review.imageUrls.length" class="review-images">
      <div class="image-grid">
        <div
          v-for="(img, idx) in review.imageUrls.slice(0, 4)"
          :key="idx"
          class="image-cell"
          @click="openImage(img)">
          <img :src="img" alt="review" />
          <div v-if="idx === 3 && review.imageUrls.length > 4" class="more-overlay">
            +{{ review.imageUrls.length - 4 }}
          </div>
        </div>
      </div>
    </div>

    <div v-if="modalImage" class="modal-backdrop" @click="modalImage = null">
      <div class="modal-content" @click.stop>
        <button class="modal-close" @click="modalImage = null">×</button>
        <img :src="modalImage" alt="" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  review: Object,
})

const { t } = useI18n()

const modalImage = ref(null)

const avatar = computed(() => {
  return props.review.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(props.review.authorName || 'U')}&background=EA580C&color=fff`
})

const maskedName = computed(() => {
  const name = props.review.authorName || ''
  if (!name) return t('common.unknown')
  if (name.length <= 2) return name[0] + '*'
  return name[0] + '*'.repeat(name.length - 2) + name[name.length - 1]
})

const formatTime = (isoString) => {
  if (!isoString) return t('recipe.just_posted')
  const date = new Date(isoString)
  if (isNaN(date.getTime())) return t('recipe.just_posted')

  const now = new Date()
  const diffInSeconds = Math.floor((now - date) / 1000)

  if (diffInSeconds < 60) return t('recipe.just_posted')
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)} phút trước`
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)} giờ trước`
  if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 86400)} ngày trước`
  if (diffInSeconds < 31536000) return `${Math.floor(diffInSeconds / 2592000)} tháng trước`
  return `${Math.floor(diffInSeconds / 31536000)} năm trước`
}

const openImage = (url) => {
  modalImage.value = url
}
</script>

<style scoped>
.review-card {
  background: #fff;
  border: 1px solid #f1f1f1;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
  margin-bottom: 14px;
}

.review-header {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.review-avatar {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  object-fit: cover;
  border: 1px solid #eee;
}

.review-meta {
  flex: 1;
}

.review-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}

.review-author {
  font-weight: 700;
  color: #222;
  font-size: 0.94rem;
}

.review-stars {
  display: flex;
  gap: 2px;
  font-size: 0.9rem;
  color: #f4f4f4;
}

.star {
  color: #f4f4f4;
}

.star.filled {
  color: #ee4d2d;
}

.review-sub {
  color: #6b7280;
  font-size: 0.82rem;
  margin-top: 4px;
}

.review-text {
  margin: 12px 0;
  color: #2b2b2b;
  line-height: 1.5;
}

.review-images {
  margin-top: 10px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: 8px;
}

.image-cell {
  position: relative;
  height: 80px;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #eee;
}

.image-cell img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.more-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.45);
  color: #fff;
  font-weight: 700;
  font-size: 0.9rem;
}

.review-actions {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.like-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid #ececec;
  background: #fff;
  cursor: pointer;
  font-size: 0.85rem;
  color: #555;
  transition: all 0.2s;
}

.like-btn.active {
  border-color: #ee4d2d;
  color: #ee4d2d;
  background: rgba(238, 77, 45, 0.04);
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
}

.modal-content {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  max-width: 90vw;
  max-height: 90vh;
  position: relative;
}

.modal-content img {
  width: 100%;
  height: auto;
  display: block;
}

.modal-close {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  border-radius: 999px;
  border: none;
  background: rgba(0,0,0,0.55);
  color: #fff;
  cursor: pointer;
  font-size: 18px;
}
</style>
