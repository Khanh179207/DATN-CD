<template>
  <div class="comment-item" :class="{ 'has-children': hasChildren }">
    <!-- Avatar + connector lines -->
    <div class="avatar" :class="{ 'ring-2 ring-blue-400': isAuthor }">
      <img :src="comment.authorAvatar || defaultAvatar" :alt="comment.authorName || 'Avatar'" />
    </div>

    <div class="comment-body">
      <div class="comment-meta">
        <div class="metadata">
          <span class="author">{{ comment.authorName || $t('recipe.anonymous_user') }}</span>
          <span class="timestamp">{{ formattedTime }}</span>
        </div>
        <div class="reactions">
          <span class="reaction"><span>👍</span> {{ likeCount }}</span>
          <span class="reaction"><span>❤️</span> {{ loveCount }}</span>
        </div>
      </div>

      <div class="comment-content">{{ comment.content }}</div>

      <div class="comment-actions">
        <button class="action-link" @click="toggleLike" :class="{ 'text-blue-400': liked }">
          <span class="mr-1">👍</span>
          {{ liked ? $t('comment.liked') : $t('comment.like') }}
        </button>
        <button class="action-link" @click="toggleReply">{{ replying ? $t('common.cancel') : $t('comment.reply') }}</button>
        <button class="action-link" @click="shareComment">{{ $t('comment.share') }}</button>
      </div>

      <transition name="fade">
        <div v-if="replying" class="reply-box">
          <textarea
            v-model="replyText"
            class="reply-input"
            :placeholder="$t('post.add_comment')"
          ></textarea>
          <div class="reply-buttons">
            <button class="btn-secondary" @click="toggleReply">{{ $t('common.cancel') }}</button>
            <button class="btn-primary" @click="submitReply" :disabled="!replyText.trim()">{{ $t('post.submit_review') }}</button>
          </div>
        </div>
      </transition>
    </div>

    <div class="children" v-if="hasChildren">
      <CommentItem
        v-for="child in visibleChildren"
        :key="child.commentID"
        :comment="child"
        :depth="depth + 1"
        :maxDepth="maxDepth"
        :current-user-id="currentUserId"
        @submit-reply="forwardReply"
      />

      <button
        v-if="hasHiddenChildren"
        class="show-more"
        @click="toggleShowMore"
      >
        {{ showAll ? $t('comment.hide_replies') : $t('comment.show_more_replies', { count: hiddenCount }) }}
      </button>
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
const liked = ref(false)
const showAll = ref(false)

const toggleReply = () => {
  replying.value = !replying.value
  if (!replying.value) replyText.value = ''
}

const toggleLike = () => {
  liked.value = !liked.value
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

const shareComment = () => {
  try {
    navigator.clipboard.writeText(window.location.href + `#comment-${props.comment.commentID}`)
  } catch {
    // ignore
  }
}

const formattedTime = computed(() => {
  if (!props.comment.createdAt) return t('recipe.just_posted')
  const date = new Date(props.comment.createdAt)
  if (Number.isNaN(date.getTime())) return t('recipe.just_posted')

  const now = new Date()
  const diff = Math.floor((now - date) / 1000)
  if (diff < 60) return t('recipe.just_posted')
  if (diff < 3600) return `${Math.floor(diff / 60)} ${t('common.minutes')} ago`
  if (diff < 86400) return `${Math.floor(diff / 3600)} ${t('common.hours')} ago`
  if (diff < 2592000) return `${Math.floor(diff / 86400)} days ago`
  return date.toLocaleDateString()
})

const hasChildren = computed(() => Array.isArray(props.comment.children) && props.comment.children.length > 0)

const isAuthor = computed(() => props.currentUserId && props.comment.accountID && String(props.currentUserId) === String(props.comment.accountID))

const likeCount = computed(() => props.comment.likes ?? 0)
const loveCount = computed(() => props.comment.loves ?? 0)

const canRenderChildren = computed(() => props.depth < props.maxDepth || showAll.value)

const visibleChildren = computed(() => {
  if (!hasChildren.value || !canRenderChildren.value) return []
  if (showAll.value || props.depth >= props.maxDepth) return props.comment.children
  return props.comment.children.slice(0, 3)
})

const hiddenCount = computed(() => {
  if (!hasChildren.value) return 0
  return Math.max(0, props.comment.children.length - 3)
})

const hasHiddenChildren = computed(() => {
  if (!hasChildren.value) return false
  if (showAll.value) return false
  if (props.depth >= props.maxDepth) return true
  return hiddenCount.value > 0
})

const toggleShowMore = () => {
  showAll.value = !showAll.value
}

const defaultAvatar = 'https://ui-avatars.com/api/?name=User&background=EA580C&color=fff'
</script>

<style scoped>
.comment-item {
  position: relative;
  padding-left: 3.5rem;
  margin-bottom: 1.25rem;
}

.comment-item.has-children::before {
  content: '';
  position: absolute;
  left: 1.35rem;
  top: 2.25rem;
  bottom: 0;
  width: 1px;
  background: rgba(148, 163, 184, 0.7);
  border-radius: 1px;
}

.comment-body {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding: 0.75rem 1rem 0.75rem 3.5rem;
  border-radius: 18px;
  background: #f0f2f5;
  transition: background 0.2s ease;
  color: rgba(17, 24, 39, 0.95);
}

.dark .comment-body {
  background: #3a3b3c;
  color: rgba(226, 232, 240, 0.95);
}

.comment-body:hover {
  background: #e2e5e8;
}

.dark .comment-body:hover {
  background: #2f2f31;
}

.comment-body::before {
  content: '';
  position: absolute;
  left: -1.2rem;
  top: 1.4rem;
  width: 1.2rem;
  height: 1px;
  background: rgba(148, 163, 184, 0.7);
}

.avatar {
  position: absolute;
  left: 0;
  top: 0.75rem;
  width: 40px;
  height: 40px;
  border-radius: 9999px;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.7);
  background: rgba(243, 244, 246, 1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.children {
  position: relative;
  margin-top: 0.5rem;
  margin-left: 3rem;
  padding-left: 1.25rem;
}

.children::before {
  content: '';
  position: absolute;
  left: 0.75rem;
  top: 0;
  bottom: 0;
  width: 1px;
  background: rgba(203, 213, 225, 0.7);
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: inherit;
}

.metadata {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.author {
  font-weight: 700;
}

.timestamp {
  font-size: 0.75rem;
  color: rgba(107, 114, 128, 0.9);
}

.reactions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.85rem;
  color: rgba(107, 114, 128, 0.95);
}

.reaction {
  display: inline-flex;
  gap: 0.25rem;
  align-items: center;
}

.comment-content {
  line-height: 1.5;
}

.comment-actions {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
}

.action-link {
  cursor: pointer;
  color: rgba(55, 130, 246, 0.9);
  background: transparent;
  border: none;
  padding: 0;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.reply-box {
  margin-top: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.reply-input {
  width: 100%;
  min-height: 72px;
  padding: 0.75rem;
  border: 1px solid rgba(209, 213, 219, 0.9);
  border-radius: 12px;
  resize: vertical;
  font-family: inherit;
  font-size: 0.95rem;
  background: #ffffff;
  color: rgba(17, 24, 39, 0.95);
}

.dark .reply-input {
  background: #2c2d2e;
  border-color: rgba(71, 85, 105, 0.8);
  color: rgba(226, 232, 240, 0.95);
}

.reply-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.btn-primary {
  background: rgba(37, 99, 235, 0.1);
  border: 1px solid rgba(37, 99, 235, 0.75);
  color: rgba(37, 99, 235, 0.95);
  padding: 0.4rem 0.75rem;
  border-radius: 9999px;
  cursor: pointer;
  font-weight: 600;
}

.btn-primary:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.btn-secondary {
  background: transparent;
  border: 1px solid rgba(148, 163, 184, 0.6);
  padding: 0.4rem 0.75rem;
  border-radius: 9999px;
  cursor: pointer;
  font-weight: 600;
}

.show-more {
  margin-left: 3.25rem;
  font-size: 0.85rem;
  color: rgba(107, 114, 128, 0.9);
  cursor: pointer;
  padding: 0.2rem 0;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.show-more {
  margin-left: 3.25rem;
  font-size: 0.85rem;
  color: rgba(107, 114, 128, 0.9);
  cursor: pointer;
  padding: 0.2rem 0;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
