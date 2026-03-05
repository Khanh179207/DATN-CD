<template>
  <div class="post-page-container">
    <div v-if="!post" class="post-loading">
      <div class="loading-spinner"></div>
    </div>
    <template v-else>
      <RecipeGuide :post="post" />
      <RecipeInteraction :post="post" />
      <RelatedSuggestions :items="relatedPosts" @click-post="goToDetail" />
    </template>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import RecipeGuide from '@/components/post-detail/RecipeGuide.vue'
import RecipeInteraction from '@/components/post-detail/RecipeInteraction.vue'
import RelatedSuggestions from '@/components/post-detail/RelatedSuggestions.vue'
import { getPostById, getRelatedPosts, normalizePost } from '@/services/postService'
import { recordHistory } from '@/services/interactionService'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const diffMap = { 1: 'Easy', 2: 'Medium', 3: 'Hard' }

const post = ref(null)
const relatedPosts = ref([])

async function loadPost(id) {
  try {
    const dto = await getPostById(id)
    post.value = {
      id:            dto.postID,
      postID:        dto.postID,
      title:         dto.title,
      image:         dto.media || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=1200&q=95',
      author:        dto.authorName || 'GoMet Chef',
      authorID:      dto.authorID,
      authorAvatar:  dto.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(dto.authorName||'G')}&background=EA580C&color=fff`,
      description:   dto.description || '',
      time:          dto.cookingTime ? `${dto.cookingTime} min` : '—',
      difficulty:    diffMap[dto.level] || 'Medium',
      servings:      dto.servings ? `${dto.servings} servings` : '—',
      category:      dto.categoryName || '',
      ingredientsRaw: dto.ingredients || '',
      avgRating:     dto.avgRating || 0,
      ratingCount:   dto.ratingCount || 0,
      favoriteCount: dto.favoriteCount || 0,
      views:         dto.views || 0,
      steps: (dto.steps || []).map(s => ({
        stepNumber: s.stepNumber,
        desc:   s.content || '',
        images: s.image ? [s.image] : [],
        video:  s.video || null
      })),
      comments: dto.comments || []
    }

    // Load related posts
    const related = await getRelatedPosts(id, 4)
    relatedPosts.value = related.map(normalizePost)

    // Record browsing history (silent — never throws)
    const user = authStore.currentUser
    if (user?.accountID) {
      recordHistory(user.accountID, Number(id)).catch(() => {})
    }
  } catch (err) {
    console.warn('PostDetail: load error', err)
  }
}

watch(() => route.params.id, id => { if (id) loadPost(id) }, { immediate: true })

// Scroll to a specific comment anchor after page loads (e.g. /post/5#comment-12)
watch(post, async (val) => {
  if (!val || !route.hash) return
  await new Promise(r => setTimeout(r, 600)) // wait for comments to render
  const el = document.querySelector(route.hash)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'center' })
    el.style.outline = '2px solid #EA580C'
    el.style.borderRadius = '12px'
    setTimeout(() => { el.style.outline = ''; el.style.borderRadius = '' }, 3000)
  }
})

const goToDetail = (id) => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
  router.push(`/home/post/${id}`)
}
</script>

<style scoped>
.post-page-container { background-color: #F8FAFC; min-height: 100vh; font-family: 'Mulish', sans-serif; }
.sticky-toolbar { position: sticky; top: 0; z-index: 100; background: rgba(255,255,255,0.9); backdrop-filter: blur(10px); padding: 10px 0; border-bottom: 1px solid #eee; }
.toolbar-inner { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.btn-back-vip { border: none; background: none; font-weight: 700; cursor: pointer; font-size: 1rem; color: #333; }
</style>