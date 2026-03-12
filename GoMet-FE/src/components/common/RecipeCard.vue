<template>
  <div
    class="recipe-card-wrapper"
    tabindex="0"
    role="article"
    :aria-label="post.title"
    :class="{ 'is-skeleton': loading }"
    @click="goToDetail"
    @keydown.enter.prevent="goToDetail"
  >
    <div class="card-media">
      <img :src="post.image" :alt="post.title" loading="lazy" class="main-img"
           draggable="false" @contextmenu.prevent @dragstart.prevent>
      
      <div class="media-overlay"></div>

      <span class="category-tag" v-if="post.category">{{ post.category }}</span>

      <div class="quick-actions">
        <button class="btn-action" @click.stop="toggleSave" :title="isSaved ? $t('post.saved') : $t('post.unsaved')" :disabled="isSaving" :class="{ saving: isSaving }">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" :class="{ 'filled': isSaved }">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path>
          </svg>
        </button>

        <button 
          class="btn-action btn-compare" 
          :class="{ active: compareStore.isSelected(post.id) }" 
          @click.stop="compareStore.toggleItem(post)"
          title="Compare"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 3h5v5"></path><path d="M4 20L21 3"></path><path d="M21 16v5h-5"></path><path d="M15 15l5 5"></path><path d="M4 4l5 5"></path></svg>
        </button>
      </div>
    </div>

    <div class="card-content">
      
      <div class="meta-top">
        <div class="rating-box">
          <span class="star">★</span>
          <span class="score">{{ post.rating || 4.5 }}</span>
          <span class="count">({{ post.reviews || 0 }})</span>
        </div>
        <div class="time-badge">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
          {{ post.time || '30p' }}
        </div>
      </div>

      <h3 class="card-title">{{ post.title }}</h3>

      <div class="card-footer">
        <div class="author-info">
          <div class="author-avt">
            <img v-if="post.author && post.author.avatar" :src="post.author.avatar" alt="Author">
            <div v-else class="avt-placeholder">{{ getInitials(post.author?.name) }}</div>
          </div>
          <span class="author-name">{{ post.author?.name || 'Gomet Chef' }}</span>
        </div>

        <div class="stats-like">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
          <span>{{ formatNumber(post.likes) }}</span>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useCompareStore } from '@/stores/compare'
import { useAuthStore } from '@/stores/auth'
import { addFavorite, removeFavorite, checkFavorite } from '@/services/socialService'
import { toast } from '@/composables/useToast'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  post: { type: Object, required: true, default: () => ({}) },
  loading: { type: Boolean, default: false }
})

const { t } = useI18n()
const router = useRouter()
const compareStore = useCompareStore()
const authStore = useAuthStore()
const isSaved = ref(false)
const isSaving = ref(false)

const syncSavedState = async (postId) => {
  if (!authStore.isAuthenticated || !postId) {
    isSaved.value = false
    return
  }
  const uid = authStore.user?.accountID || authStore.user?.id
  if (!uid) {
    isSaved.value = false
    return
  }
  try {
    isSaved.value = await checkFavorite(uid, postId)
  } catch {
    isSaved.value = false
  }
}

watch(
  () => [props.post.id, authStore.isAuthenticated, authStore.user?.accountID, authStore.user?.id],
  ([postId]) => {
    syncSavedState(postId)
  },
  { immediate: true }
)

const getInitials = (name) => {
  if (!name || typeof name !== 'string') return 'GM'; 
  return name.substring(0, 2).toUpperCase();
}

const toggleSave = async () => {
  if (!authStore.isAuthenticated) {
    toast.warn(t('toast.need_login'))
    return
  }
  if (isSaving.value) return
  const uid = authStore.user?.accountID || authStore.user?.id
  if (!uid) return
  isSaving.value = true
  try {
    if (isSaved.value) {
      await removeFavorite(uid, props.post.id)
      isSaved.value = false
      toast.info(t('toast.unsave_ok'))
    } else {
      await addFavorite(uid, props.post.id)
      isSaved.value = true
      toast.success(t('toast.save_ok'))
    }
  } catch {
    toast.error(t('toast.error_generic'))
  } finally {
    isSaving.value = false
  }
}

const goToDetail = () => {
  if (props.post.id) router.push({ name: 'PostDetail', params: { id: props.post.id } })
}

const formatNumber = (num) => {
  if (!num) return 0;
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k';
  return num;
}
</script>

<style scoped lang="scss" src="./RecipeCard.scss"></style>
