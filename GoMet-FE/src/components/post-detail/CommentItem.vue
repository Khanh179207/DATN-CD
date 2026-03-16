<template>
  <div class="comment-item-yt">
    <div class="yt-avatar">
      <img :src="comment.authorAvatar || defaultAvatar" alt="Avatar" />
    </div>

    <div class="yt-content-wrapper">
      <div class="yt-header">
        <span class="yt-author">{{ comment.authorName || $t('recipe.anonymous_user') }}</span>
        <span class="yt-time">{{ formattedTime }}</span>
      </div>

      <div class="yt-content">{{ comment.content }}</div>

      <!-- Comment images -->
      <div v-if="comment.imageUrls && comment.imageUrls.length" class="yt-comment-images">
        <img v-for="(img, idx) in comment.imageUrls" :key="idx" :src="img" class="yt-img" @click="openImage(img)" />
      </div>

      <div class="yt-actions">
        <button class="yt-action-btn" @click="toggleLike" :class="{ active: liked }">
          <svg width="16" height="16" viewBox="0 0 24 24" :fill="liked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
            <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path>
          </svg>
          <span v-if="internalLikeCount > 0">{{ internalLikeCount }}</span>
        </button>
        <button class="yt-reply-btn" @click="toggleReply">{{ $t('comment.reply') }}</button>
      </div>

      <div v-if="replying" class="yt-reply-input-wrapper">
        <textarea v-model="replyText" :placeholder="$t('post.add_comment')"></textarea>
        <div class="yt-reply-actions">
          <button class="yt-cancel" @click="toggleReply">{{ $t('common.cancel') }}</button>
          <button class="yt-submit" @click="submitReply" :disabled="!replyText.trim()">{{ $t('comment.reply') }}</button>
        </div>
      </div>

      <!-- Nested replies toggle -->
      <div class="yt-replies-container" v-if="hasChildren">
        <button v-if="!showAll" class="yt-show-replies" @click="toggleShowMore">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="6 9 12 15 18 9"></polyline>
          </svg>
          {{ $t('comment.show_more_replies', { count: comment.children.length }) }}
        </button>

        <div v-if="showAll" class="yt-children">
          <CommentItem
            v-for="child in comment.children"
            :key="child.commentID"
            :comment="child"
            :depth="depth + 1"
            :current-user-id="currentUserId"
            @submit-reply="forwardReply"
          />
          <button class="yt-hide-replies" @click="toggleShowMore">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="18 15 12 9 6 15"></polyline>
            </svg>
            {{ $t('comment.hide_replies') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Image Modal -->
    <div v-if="modalImage" class="yt-modal-backdrop" @click="modalImage = null">
      <div class="yt-modal-content" @click.stop>
        <img :src="modalImage" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

defineOptions({ name: 'CommentItem' })

const props = defineProps({
  comment: { type: Object, required: true },
  depth: { type: Number, default: 0 },
  maxDepth: { type: Number, default: 5 },
  isLast: { type: Boolean, default: false },
  currentUserId: { type: [Number, String], required: false },
})

const emit = defineEmits(['submit-reply'])

const { t } = useI18n()

const replyText = ref('')
const replying = ref(false)
const liked = ref(props.comment.isLiked || false)
const internalLikeCount = ref(props.comment.likeCount || 0)
const showAll = ref(false)
const modalImage = ref(null)

const toggleReply = () => {
  replying.value = !replying.value
  if (!replying.value) replyText.value = ''
}

const toggleLike = async () => {
  try {
    const res = await import('@/services/interactionService').then(m => m.toggleCommentLike(props.comment.commentID))
    internalLikeCount.value = res.likeCount
    liked.value = !liked.value
  } catch (err) {
    console.error('Failed to toggle comment like:', err)
  }
}

const openImage = (url) => {
  modalImage.value = url
}

const submitReply = () => {
  const trimmed = replyText.value.trim()
  if (!trimmed) return
  emit('submit-reply', { parentId: props.comment.commentID, content: trimmed })
  replyText.value = ''
  replying.value = false
}

const forwardReply = (payload) => {
  emit('submit-reply', payload)
}

const formattedTime = computed(() => {
  if (!props.comment.createdAt) return t('recipe.just_posted')
  const date = new Date(props.comment.createdAt)
  if (Number.isNaN(date.getTime())) return t('recipe.just_posted')

  const now = new Date()
  const diff = Math.floor((now - date) / 1000)
  if (diff < 60) return t('recipe.just_posted')
  if (diff < 3600) return `${Math.floor(diff / 60)} phút trước`
  if (diff < 86400) return `${Math.floor(diff / 3600)} giờ trước`
  if (diff < 2592000) return `${Math.floor(diff / 86400)} ngày trước`
  return date.toLocaleDateString()
})

const hasChildren = computed(() => Array.isArray(props.comment.children) && props.comment.children.length > 0)
const likeCount = computed(() => props.comment.likes ?? 0)

const toggleShowMore = () => {
  showAll.value = !showAll.value
}

const defaultAvatar = 'https://ui-avatars.com/api/?name=User&background=EA580C&color=fff'
</script>

<style scoped>
.comment-item-yt {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.yt-avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
}

.yt-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.yt-content-wrapper {
  flex: 1;
}

.yt-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 2px;
}

.yt-author {
  font-size: 13px;
  font-weight: 700;
  color: #0f0f0f;
}

.yt-time {
  font-size: 12px;
  color: #606060;
}

.yt-content {
  font-size: 14px;
  line-height: 1.5;
  color: #0f0f0f;
  white-space: pre-wrap;
  word-break: break-word;
}

.yt-comment-images {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 8px;
}

.yt-img {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  border: 1px solid #e5e5e5;
}

.yt-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 4px;
}

.yt-action-btn, .yt-reply-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  font-size: 12px;
  color: #0f0f0f;
  border-radius: 18px;
  transition: background 0.2s;
}

.yt-action-btn:hover, .yt-reply-btn:hover {
  background: rgba(0,0,0,0.05);
}

.active {
  color: #065fd4 !important;
}

.yt-reply-input-wrapper {
  margin-top: 8px;
}

.yt-reply-input-wrapper textarea {
  width: 100%;
  border: none;
  border-bottom: 1px solid #e5e5e5;
  padding: 4px 0;
  font-size: 14px;
  outline: none;
  resize: none;
  background: transparent;
}

.yt-reply-input-wrapper textarea:focus {
  border-bottom: 2px solid #0f0f0f;
}

.yt-reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.yt-cancel, .yt-submit {
  padding: 6px 12px;
  border-radius: 18px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  border: none;
}

.yt-cancel {
  background: none;
  color: #0f0f0f;
}

.yt-cancel:hover {
  background: rgba(0,0,0,0.05);
}

.yt-submit {
  background: #065fd4;
  color: #fff;
}

.yt-submit:disabled {
  background: #f2f2f2;
  color: #909090;
}

.yt-replies-container {
  margin-top: 4px;
}

.yt-show-replies, .yt-hide-replies {
  background: none;
  border: none;
  color: #065fd4;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
}

.yt-children {
  margin-left: 20px;
  border-left: 1px solid transparent;
  padding-left: 12px;
}

.yt-modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.yt-modal-content img {
  max-width: 90vw;
  max-height: 90vh;
  object-fit: contain;
}
</style>
