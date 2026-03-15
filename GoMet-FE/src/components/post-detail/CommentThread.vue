<template>
  <div class="comment-thread">
    <div v-if="loading" class="text-center text-sm text-gray-500">{{ $t('common.loading') }}</div>
    <div v-else-if="error" class="text-center text-sm text-red-500">{{ $t('common.error_generic') }}</div>
    <div v-else-if="!comments.length" class="text-center text-sm text-gray-500">
      {{ $t('post.no_comments_yet') }}
    </div>

    <div v-else class="space-y-4">
      <CommentItem
        v-for="(comment, index) in comments"
        :key="comment.commentID"
        :comment="comment"
        :depth="0"
        :maxDepth="maxDepth"
        :is-last="index === comments.length - 1"
        :current-user-id="currentUserId"
        @submit-reply="handleReply"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { getComments, addComment } from '@/services/interactionService'
import { toast } from '@/composables/useToast'
import CommentItem from './CommentItem.vue'

const props = defineProps({
  postId: { type: [Number, String], required: true },
  currentUserId: { type: [Number, String], required: false },
  refreshKey: { type: [Number, String], default: 0 },
  maxDepth: { type: Number, default: 5 },
})

const { t } = useI18n()

const comments = ref([])
const loading = ref(false)
const error = ref(null)

const loadComments = async () => {
  if (!props.postId) return
  loading.value = true
  error.value = null
  try {
    const data = await getComments(props.postId)
    comments.value = Array.isArray(data) ? data : []
  } catch (err) {
    error.value = err
  } finally {
    loading.value = false
  }
}

watch(() => [props.postId, props.refreshKey], loadComments, { immediate: true })

const handleReply = async ({ parentId, content }) => {
  if (!props.currentUserId) {
    toast.warn(t('toast.need_login'))
    return
  }
  try {
    await addComment(props.postId, props.currentUserId, content, parentId)
    await loadComments()
    toast.success(t('toast.comment_ok'))
  } catch (err) {
    toast.error(t('toast.error_generic'))
  }
}
</script>

<style scoped>
.comment-thread {
  position: relative;
}
</style>
