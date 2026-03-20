<template>
  <div class="post-page-container">
    <div v-if="!post" class="post-loading">
      <div class="loading-spinner"></div>
    </div>
    <template v-else>
      <RecipeHero :post="post" />
      <RecipeInformation :post="post" />
      <RecipeInteraction :post="post" />
      <RelatedSuggestions :items="relatedPosts" @click-post="goToDetail" />
    </template>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 1. Nhớ import 2 file mới tách
import RecipeHero from '@/components/post-detail/RecipeHero.vue'
import RecipeInformation from '@/components/post-detail/RecipeInformation.vue'

import RecipeInteraction from '@/components/post-detail/RecipeInteraction.vue'
import RelatedSuggestions from '@/components/post-detail/RelatedSuggestions.vue'
import { getPostById, getRelatedPosts, normalizePost } from '@/services/postService'
import { recordHistory } from '@/services/interactionService'
import { useAuthStore } from '@/stores/auth'

/* START: Daily View Limit */
import { usePostViewLimit } from '@/composables/usePostViewLimit'
import { toast } from '@/composables/useToast'
/* END */

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

/* START: Daily View Limit */
const { checkAndChargeView, remainingViews } = usePostViewLimit()
/* END */
const diffMap = { 1: 'Easy', 2: 'Medium', 3: 'Hard' }

const post = ref(null)
const relatedPosts = ref([])

async function loadPost(id) {
  try {
    const dto = await getPostById(id)

    /* START: Daily View Limit Check (Check after load to know authorId) */
    const canAccess = checkAndChargeView(Number(id), dto.authorID)
    if (!canAccess) {
      toast.warn('Bạn đã hết lượt xem bài viết miễn phí trong ngày hôm nay! Vui lòng nâng cấp Premium.')
      router.push('/home') // Nhảy về trang Home
      window.dispatchEvent(new CustomEvent('ui:open-premium')) // Mở modal
      return
    }
    /* END */

    post.value = {
      id: dto.postID,
      postID: dto.postID,
      title: dto.title,
      image: dto.media || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=1200&q=95',
      video: dto.video || null,
      author: dto.authorName || 'GoMet Chef',
      authorID: dto.authorID,
      authorAvatar: dto.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(dto.authorName || 'G')}&background=EA580C&color=fff`,
      description: dto.description || '',
      time: dto.cookingTime ? `${dto.cookingTime} min` : '—',
      difficulty: diffMap[dto.level] || 'Medium',
      servings: dto.servings ? `${dto.servings} servings` : '—',
      category: dto.categoryName || '',
      ingredientsRaw: dto.ingredients || '',
      avgRating: dto.avgRating || 0,
      ratingCount: dto.ratingCount || 0,
      favoriteCount: dto.favoriteCount || 0,
      views: dto.views || 0,
      steps: (dto.steps || []).map(s => ({
        stepNumber: s.stepNumber,
        desc: s.content || '',
        images: s.image ? [s.image] : [],
        video: s.video || null
      })),
      comments: dto.comments || []
    }

    // Load related posts
    const related = await getRelatedPosts(id, 4)
    relatedPosts.value = related.map(normalizePost)

    // Record browsing history (silent — never throws)
    const user = authStore.currentUser
    if (user?.accountID) {
      recordHistory(user.accountID, Number(id)).catch(() => { })
    }
  } catch (err) {
    console.warn('PostDetail: load error', err)
  }
}

watch(() => route.params.id, id => { if (id) loadPost(id) }, { immediate: true })

// Scroll to comment if hash is present in URL
import { onMounted } from 'vue'
onMounted(() => {
  if (window.location.hash) {
    const element = document.querySelector(window.location.hash)
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' })
    }
  }
})

const goToDetail = (id) => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
  router.push(`/home/post/${id}`)
}
</script>

<style scoped>
/* Thêm reset box-sizing cho chắc chắn */
.post-page-container *,
.post-page-container *::before,
.post-page-container *::after {
  box-sizing: border-box;
}

.post-page-container {
  background-color: #F8FAFC;
  min-height: 100vh;
  font-family: 'Mulish', sans-serif;

  /* 2 Dòng quan trọng nhất để trị dứt điểm thanh cuộn ngang toàn trang */
  width: 100%;
  max-width: 100vw;
  overflow-x: hidden;
}

.sticky-toolbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.toolbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.btn-back-vip {
  border: none;
  background: none;
  font-weight: 700;
  cursor: pointer;
  font-size: 1rem;
  color: #333;
}
</style>