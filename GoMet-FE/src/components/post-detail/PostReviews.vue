<template>
  <section class="reviews-section fade-in">
    <div class="reviews-header">
      <h3 class="section-title">{{ $t('post.community_reviews') }}</h3>
      <div class="review-summary-card shopee-style">
        <div class="score-box">
          <div class="average-row">
            <div class="avg-value">{{ summary.average || '-' }}</div>
            <div class="avg-text">{{ $t('post.out_of') }}</div>
          </div>
          <div class="stars-display">
            <span v-for="n in 5" :key="n" :class="['star', { active: n <= Math.round(summary.average || 0) }]">★</span>
          </div>
          <div class="total-reviews">{{ summary.total }} {{ $t('post.ratings') }}</div>
        </div>

        <div class="filter-tags">
          <button
            class="filter-tag"
            :class="{ active: activeFilter === 'all' }"
            @click="setFilter('all')"
          >
            {{ $t('post.all') }}
          </button>

          <button
            v-for="star in 5"
            :key="star"
            class="filter-tag"
            :class="{ active: activeFilter === `star-${star}` }"
            @click="setFilter(`star-${star}`)"
          >
            {{ star }} {{ $t('post.star') }} ({{ summary.distribution[star] || 0 }})
          </button>


        </div>
      </div>
    </div>

    <div class="write-review-row">
      <button class="btn-write-review" @click="openRatingModal">
        {{ hasRated ? $t('post.edit_review') || 'Chỉnh sửa đánh giá' : $t('post.write_review') }}
      </button>
    </div>

    <div class="review-list">
      <div v-if="reviews.length === 0" class="empty">
        {{ $t('post.no_reviews_yet') }}
      </div>
      <RatingItem
        v-for="review in reviews"
        :key="review.ratingID"
        :review="review"
      />

      <div class="load-more" v-if="page + 1 < totalPages">
        <button class="btn-load-more" @click="loadMore" :disabled="loadingMore">
          {{ loadingMore ? $t('common.loading') : $t('common.view_more') }}
        </button>
      </div>
    </div>

    <div v-if="showWriteModal" class="modal-backdrop" @click="showWriteModal = false">
      <div class="modal-content" @click.stop>
        <button class="modal-close" @click="showWriteModal = false">×</button>
        <div class="modal-header">
          <h3>{{ $t('post.write_review') }}</h3>
        </div>
        <div class="modal-body">
          <div class="write-review-form">
            <div class="star-picker">
              <span v-for="star in 5" :key="star" class="star" :class="{ active: star <= rating }" @click="rating = star">★</span>
            </div>

            <div class="modal-actions">
              <button class="btn-secondary" @click="showWriteModal = false">{{ $t('common.cancel') }}</button>
              <button class="btn-primary" :disabled="isSubmitting || rating === 0" @click="handleSubmitReview">
                {{ isSubmitting ? $t('common.loading') : $t('post.submit_review') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal-backdrop" v-if="modalImage" @click="modalImage = null">
      <div class="modal-content" @click.stop>
        <button class="modal-close" @click="modalImage = null">×</button>
        <img :src="modalImage" alt="" />
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import RatingItem from './RatingItem.vue'
import { getRatingSummary, getRatings, submitReview as apiSubmitReview } from '@/services/interactionService'
import { uploadMedia } from '@/services/uploadService'

const props = defineProps({
  postId: Number,
})

const { t } = useI18n()
const authStore = useAuthStore()

const summary = ref({ average: 0, total: 0, distribution: { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 }, withComments: 0, withImages: 0 })
const reviews = ref([])
const page = ref(0)
const size = ref(8)
const totalPages = ref(1)
const loading = ref(false)
const loadingMore = ref(false)
const hasRated = ref(false)
const userRating = ref(null)

const rating = ref(0)
const reviewText = ref('')
const selectedFiles = ref([])
const isSubmitting = ref(false)
const modalImage = ref(null)
const showWriteModal = ref(false)
const activeFilter = ref('all')

const canSubmit = computed(() => rating.value > 0 && (reviewText.value.trim().length > 0 || selectedFiles.value.length > 0))

const activeFilterParams = computed(() => {
  if (activeFilter.value === 'all') return {}
  if (activeFilter.value === 'withComment') return { hasComment: true }
  if (activeFilter.value === 'withImages') return { hasImage: true }
  const starMatch = activeFilter.value.match(/^star-(\d+)$/)
  if (starMatch) return { star: Number(starMatch[1]) }
  return {}
})

const defaultAvatar = (name) =>
  `https://ui-avatars.com/api/?name=${encodeURIComponent(name || 'U')}&background=EA580C&color=fff`

const pctForStar = (star) => {
  const total = summary.value.total || 0
  if (!total) return 0
  return Math.round((summary.value.distribution[star] || 0) / total * 100)
}

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

const loadSummary = async () => {
  if (!props.postId) return
  try {
    const data = await getRatingSummary(props.postId)
    summary.value = {
      average: data.average ?? 0,
      total: data.total ?? 0,
      distribution: data.distribution ?? { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 },
      withComments: data.withComments ?? 0,
      withImages: data.withImages ?? 0,
    }
  } catch (err) {
    console.warn('PostReviews: load summary error', err)
  }

  // Check if current user rated
  if (authStore.isAuthenticated && props.postId) {
    try {
      const { checkRating } = await import('@/services/interactionService')
      const data = await checkRating(authStore.user.accountID, props.postId)
      hasRated.value = data.rated
      if (data.rated) {
        userRating.value = data.rate
      }
    } catch { /* ignore */ }
  }
}

const loadReviews = async (reset = false) => {
  if (!props.postId) return
  try {
    loading.value = true
    if (reset) page.value = 0
    if (reset) reviews.value = []

    const currentPage = page.value
    const data = await getRatings(props.postId, currentPage, size.value, activeFilterParams.value)
    totalPages.value = data.totalPages || 1

    if (reset) {
      reviews.value = data.items || []
    } else {
      reviews.value = reviews.value.concat(data.items || [])
    }
  } catch (err) {
    console.warn('PostReviews: load ratings error', err)
  } finally {
    loading.value = false
  }
}

const loadMore = async () => {
  if (loadingMore.value) return
  if (page.value + 1 >= totalPages.value) return
  loadingMore.value = true
  page.value += 1
  await loadReviews()
  loadingMore.value = false
}

const onFilesSelected = (event) => {
  const files = Array.from(event.target.files || [])
  if (!files.length) return
  // Keep up to 4 images
  const allowed = files.slice(0, 4 - selectedFiles.value.length)
  allowed.forEach(file => {
    const preview = URL.createObjectURL(file)
    selectedFiles.value.push({ file, preview })
  })
  event.target.value = null
}

const removeFile = (idx) => {
  const removed = selectedFiles.value.splice(idx, 1)
  if (removed.length) URL.revokeObjectURL(removed[0].preview)
}

const openRatingModal = () => {
  if (!authStore.isAuthenticated) {
    toast.warn(t('toast.need_login'))
    return
  }
  // Nếu đã đánh giá, pre-fill rating
  if (hasRated.value && userRating.value) {
    rating.value = userRating.value
  } else {
    rating.value = 0
  }
  reviewText.value = ''
  selectedFiles.value = []
  showWriteModal.value = true
}

const openImage = (url) => {
  modalImage.value = url
}

const handleSubmitReview = async () => {
  if (!authStore.isAuthenticated) {
    toast.warn(t('toast.need_login'))
    return
  }
  if (rating.value === 0) {
    toast.warn(t('toast.error_generic'))
    return
  }

  isSubmitting.value = true
  try {
    await apiSubmitReview(authStore.user.accountID, props.postId, rating.value, '', [])

    toast.success(hasRated.value ? 'Cập nhật đánh giá thành công' : t('toast.comment_ok'))

    await loadSummary()
    await loadReviews(true)
    
    showWriteModal.value = false
  } catch (err) {
    console.error(err)
    toast.error(t('toast.error_generic'))
  } finally {
    isSubmitting.value = false
  }
}

const setFilter = (filter) => {
  activeFilter.value = filter
  loadReviews(true)
}

watch(() => props.postId, (id) => {
  if (!id) return
  loadSummary()
  loadReviews(true)
}, { immediate: true })

watch(activeFilter, () => {
  loadReviews(true)
})
</script>

<style scoped lang="scss">
@use '../../assets/styles/variables' as *;

.review-summary-card.shopee-style {
  display: flex;
  flex-wrap: wrap;
  gap: $space-6;
  background: #fffbf8;
  padding: 18px 18px 12px;
  border-radius: $radius-xl;
  margin-bottom: $space-10;
  border: 1px solid #f9ede5;
}

.review-summary-card .score-box {
  min-width: 220px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.average-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.avg-value {
  font-size: 2.6rem;
  font-weight: 800;
  color: #ee4d2d;
  line-height: 1;
}

.avg-text {
  font-size: 0.95rem;
  color: #7f7f7f;
}

.stars-display {
  display: flex;
  gap: 1px;
  font-size: 1.2rem;
  color: #f4f4f4;
}

.stars-display .star.active {
  color: #ee4d2d;
}

.total-reviews {
  font-size: 0.85rem;
  color: #6b7280;
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding-top: 6px;
  align-items: center;
}

.filter-tag {
  border: 1px solid #e2e8f0;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 0.85rem;
  background: #fff;
  color: #374151;
  cursor: pointer;
  transition: all 0.15s;
}

.filter-tag.active {
  border-color: #ee4d2d;
  color: #ee4d2d;
}

.write-review-row {
  display: flex;
  justify-content: flex-end;
  margin-bottom: $space-5;
}

.btn-write-review {
  border: 1px solid #ee4d2d;
  background: #fff;
  color: #ee4d2d;
  padding: 10px 16px;
  border-radius: $radius-full;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.btn-write-review:hover {
  background: rgba(238, 77, 45, 0.08);
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal-content {
  width: min(92vw, 620px);
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 22px 46px rgba(0,0,0,0.25);
}

.modal-header {
  padding: 16px 18px;
  border-bottom: 1px solid #f3f4f6;
}

.modal-body {
  padding: 16px 18px 22px;
}

.modal-close {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  border-radius: 999px;
  border: none;
  background: rgba(0,0,0,0.45);
  color: #fff;
  font-size: 18px;
  cursor: pointer;
}

.write-review-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.star-picker {
  display: flex;
  gap: 4px;
  font-size: 1.6rem;
  color: #d1d5db;
}

.star-picker .star {
  cursor: pointer;
}

.star-picker .star.active {
  color: #ee4d2d;
}

.write-review-form textarea {
  width: 100%;
  min-height: 112px;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  resize: vertical;
  font-size: 0.95rem;
  line-height: 1.5;
}

.upload-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.upload-button {
  border: 1px dashed #d1d5db;
  border-radius: 999px;
  padding: 8px 14px;
  font-size: 0.9rem;
  color: #374151;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.upload-button input {
  display: none;
}

.selected-count {
  font-size: 0.85rem;
  color: #6b7280;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}

.btn-secondary {
  padding: 10px 16px;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  color: #374151;
}

.btn-primary {
  padding: 10px 16px;
  border-radius: 999px;
  border: none;
  background: #ee4d2d;
  color: #fff;
  cursor: pointer;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.load-more {
  display: flex;
  justify-content: center;
  margin-top: $space-6;
}

.btn-load-more {
  border: 1px solid $neutral-300;
  background: $neutral-0;
  padding: $space-3 $space-6;
  border-radius: $radius-full;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: $neutral-100;
  }
}

.empty {
  padding: $space-8;
  text-align: center;
  color: $neutral-500;
}
.image-previews {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 8px;
}

.preview-item {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #eee;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .remove-btn {
    position: absolute;
    top: 4px;
    right: 4px;
    width: 20px;
    height: 20px;
    background: rgba(0,0,0,0.5);
    color: #fff;
    border: none;
    border-radius: 999px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-size: 14px;
  }
}
</style>
