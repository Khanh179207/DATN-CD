<template>
  <div class="recipe-comments-module">
    
    <template v-if="isRoot">
      <div v-if="loading" class="loading-state">
        <div v-for="i in 3" :key="i" class="skeleton-comment-premium">
          <div class="sk-avatar"></div>
          <div class="sk-content">
            <div class="sk-name"></div>
            <div class="sk-text"></div>
            <div class="sk-text short"></div>
          </div>
        </div>
      </div>

      <div v-else-if="error" class="error-state-luxury fade-in">
        <div class="icon-circle error">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
        </div>
        <p>{{ errorMsg || $t('comment.connection_retry') }}</p>
        <button @click="$emit('reload')" class="btn-retry">{{ $t('common.retry') }}</button>
      </div>

      <div v-else-if="!commentTree.length" class="empty-state-luxury fade-in">
        <div class="empty-art">
          <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          </svg>
        </div>
        <p>{{ $t('comment.empty_prompt') }}</p>
      </div>

      <div v-else class="comments-list">
        <RecipeComments 
          v-for="root in commentTree" 
          :key="root.id" 
          :comment-data="root" 
          :is-root="false"
          :is-child="false"
          @toggle-like="(data) => $emit('toggle-like', data)"
          @reply-submitted="(data) => $emit('reply-submitted', data)"
          @delete-comment="(id) => $emit('delete-comment', id)"
        />
      </div>
    </template>

    <template v-else>
      <div class="review-item-luxury fade-in" :class="{ 'is-child': isChild }" :id="'comment-' + commentData.id">
        
        <div class="review-header">
          <div class="avatar-wrapper">
            <img :src="avatarUrl" :alt="$t('common.user_avatar')" class="review-avatar" />
          </div>
          <div class="review-meta">
            <div class="review-top">
              <span class="review-author">{{ commentData.authorName || commentData.name || $t('recipe.anonymous_user') }}</span>
              <div v-if="commentData.rating > 0" class="review-stars-pill">
                <span v-for="n in 5" :key="n" :class="['star', { filled: n <= commentData.rating }]">★</span>
              </div>
            </div>
            <div class="review-sub">
              <span class="review-time">{{ formatTime(commentData.createdAt) }}</span>
            </div>
          </div>
        </div>

        <p v-if="commentData.content" 
           class="review-text"
           v-html="formatCommentText(commentData.content)">
        </p>

        <div v-if="commentData.imageUrls && commentData.imageUrls.length" class="review-images">
          <div class="image-grid-premium" :class="'grid-' + Math.min(commentData.imageUrls.length, 3)">
            <div v-for="(img, idx) in commentData.imageUrls.slice(0, 4)" :key="idx" class="image-cell" @click="openImage(img)">
              <img :src="img" :alt="$t('common.review_image')" loading="lazy" />
              <div v-if="idx === 3 && commentData.imageUrls.length > 4" class="more-overlay">
                <span>+{{ commentData.imageUrls.length - 4 }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="review-actions-bar">
          <button class="action-btn like-btn" :class="{ active: commentData.isLiked }" @click="handleToggleLike(commentData)">
            <div class="icon-wrapper">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path>
              </svg>
            </div>
            <span class="like-count">{{ commentData.likes || 0 }}</span>
          </button>
          
          <button class="action-btn reply-btn" @click="openReplyBox">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="9 17 4 12 9 7"></polyline><path d="M20 18v-2a4 4 0 0 0-4-4H4"></path></svg>
            {{ $t('common.reply') }}
          </button>

          <button v-if="canDelete(commentData)" class="action-btn delete-btn" @click="onDeleteRequest(commentData.id)">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
          </button>
        </div>

        <div v-if="isReplying" class="inline-reply-luxury slide-down" :class="{ 'is-child': isChild }">
          <img :src="currentUserAvatar" class="reply-avatar" />
          <div class="reply-input-wrapper">
            <textarea 
              v-model="replyText" 
              :placeholder="t('comment.reply_placeholder', { name: commentData.authorName || t('comment.comment_lower') })"
              v-focus
            ></textarea>
            <div class="reply-actions">
              <button class="btn-cancel-glass" @click="isReplying = false">{{ $t('common.cancel') }}</button>
              <button class="btn-submit-glow" :disabled="!replyText.trim()" @click="submitReply">{{ $t('common.send') }}</button>
            </div>
          </div>
        </div>

        <div v-if="commentData.replies && commentData.replies.length > 0" class="nested-replies-container">
          <div class="thread-line-premium"></div> 
          <div class="nested-replies-content">
             <RecipeComments 
              v-for="child in commentData.replies" 
              :key="child.id" 
              :comment-data="child" 
              :is-root="false"
              :is-child="true"
              @toggle-like="(data) => $emit('toggle-like', data)"
              @reply-submitted="(data) => $emit('reply-submitted', data)"
              @delete-comment="(id) => $emit('delete-comment', id)"
            />
          </div>
        </div>

      </div>
    </template>

    <Teleport to="body">
      <Transition name="fade-scale">
        <div v-if="modalImage" class="modal-backdrop-glass" @click="modalImage = null">
          <div class="modal-content" @click.stop>
            <button class="modal-close-glass" @click="modalImage = null">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
            <img :src="modalImage" :alt="$t('common.zoomed_image')" />
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import { useI18n } from 'vue-i18n'
import { formatTimeAgo } from '@/utils/timeFormatter'

defineOptions({ name: 'RecipeComments' }) 

const props = defineProps({
  isRoot: { type: Boolean, default: true },
  rawComments: { type: Array, default: () => [] }, 
  loading: { type: Boolean, default: false },
  error: { type: Boolean, default: false },
  errorMsg: { type: String, default: '' },
  commentData: { type: Object, default: () => ({}) },
  isChild: { type: Boolean, default: false }
})

const emit = defineEmits(['toggle-like', 'reply-submitted', 'reload', 'delete-comment'])
const authStore = useAuthStore()
const { t } = useI18n()

// --- LOGIC TREE COMMENTS GIỮ NGUYÊN ---
const commentTree = computed(() => {
  if (!props.isRoot || !props.rawComments.length) return []
  const map = {}
  props.rawComments.forEach(c => {
    const id = c.commentID || c.id
    map[id] = { ...c, id: id, replies: [] } 
  })
  const roots = []
  props.rawComments.forEach(c => {
    const id = c.commentID || c.id
    const parentId = c.cmtid || c.parentId || null
    if (parentId && map[parentId]) {
      map[parentId].replies.push(map[id])
    } else {
      roots.push(map[id])
    }
  })
  const flattenReplies = (replies) => {
    let flatArray = []
    replies.forEach(reply => {
      flatArray.push(reply) 
      if (reply.replies && reply.replies.length > 0) {
        flatArray = flatArray.concat(flattenReplies(reply.replies))
        reply.replies = [] 
      }
    })
    return flatArray
  }
  roots.forEach(root => {
    if (root.replies && root.replies.length > 0) {
      root.replies.sort((a, b) => (a.commentID || a.id) - (b.commentID || b.id)) 
      root.replies = flattenReplies(root.replies)
    }
  })
  return roots.sort((a, b) => (b.commentID || b.id) - (a.commentID || a.id)) 
})

const isReplying = ref(false)
const replyText = ref('')
const modalImage = ref(null)

const currentUserAvatar = computed(() => authStore.user?.avatar || `https://ui-avatars.com/api/?name=U&background=EA580C&color=fff`)
const avatarUrl = computed(() => props.commentData.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(props.commentData.authorName || 'U')}&background=EA580C&color=fff`)

const formatTime = (isoString) => {
  if (!isoString) return t('common.just_now')
  const date = new Date(isoString)
  if (isNaN(date.getTime())) return t('common.just_now')
  return formatTimeAgo(date)
}

const formatCommentText = (text) => {
  if (!text) return ''
  let safeText = text.replace(/</g, "&lt;").replace(/>/g, "&gt;")
  return safeText.replace(/(@[^\s]+)/g, '<span class="mention-tag">$1</span>')
}

const canDelete = (cmt) => {
  if (!authStore.isAuthenticated) return false
  const currentUid = authStore.user?.accountID || authStore.user?.id
  const cmtUid = cmt.accountID || cmt.userId
  return cmtUid === currentUid || authStore.user?.isAdmin
}

const onDeleteRequest = (id) => {
  if (confirm(t('comment.delete_confirm'))) emit('delete-comment', id)
}

const openImage = (url) => { modalImage.value = url }
const openReplyBox = () => {
  if (!authStore.isAuthenticated) { toast.warn(t('comment.login_reply')); return }
  isReplying.value = !isReplying.value; replyText.value = ''
}

const submitReply = () => {
  if (!replyText.value.trim()) return
  let finalContent = replyText.value.trim()
  if (props.isChild) finalContent = `@${props.commentData.authorName} ` + finalContent
  const parentId = props.isChild ? (props.commentData.cmtid || props.commentData.parentId) : props.commentData.id
  emit('reply-submitted', { parentId, content: finalContent })
  isReplying.value = false; replyText.value = ''
}

const handleToggleLike = (data) => emit('toggle-like', data)
const vFocus = { mounted: (el) => el.focus() }
</script>

<style scoped lang="scss">
.recipe-comments-module {
  font-family: var(--font-ui, 'Mulish', sans-serif);
  width: 100%;
}

/* --- STATES: EMPTY, ERROR --- */
.empty-state-luxury, .error-state-luxury {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 60px 20px; text-align: center; border-radius: 24px;
  background: var(--color-neutral-0, #fff); border: 1px dashed #e2e8f0;
  p { font-size: 0.95rem; font-weight: 600; color: #64748b; margin-top: 16px; }
}
.empty-art { color: #cbd5e1; }
.btn-retry { margin-top: 20px; padding: 10px 24px; background: #ef4444; color: white; border: none; border-radius: 100px; cursor: pointer; font-weight: 700; transition: 0.2s; box-shadow: 0 4px 12px rgba(239,68,68,0.2); &:hover { transform: translateY(-2px); box-shadow: 0 6px 16px rgba(239,68,68,0.3); } }

/* --- ITEM CONTAINER (LUXURY) --- */
.review-item-luxury {
  padding: 28px 0; border-bottom: 1px solid rgba(226, 232, 240, 0.6);
  &.is-child { border-bottom: none; padding: 16px 0 0 0; }
}

/* HEADER & META */
.review-header { display: flex; gap: 14px; align-items: center; }
.avatar-wrapper {
  position: relative;
  &::after { content: ''; position: absolute; inset: -2px; border-radius: 50%; border: 1px solid rgba(234, 88, 12, 0.1); pointer-events: none; }
}
.review-avatar { width: 48px; height: 48px; border-radius: 50%; object-fit: cover; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
.is-child .review-avatar { width: 36px; height: 36px; }

.review-author { font-weight: 800; color: #0f172a; font-size: 1rem; letter-spacing: -0.2px; }
.review-stars-pill { display: inline-flex; gap: 2px; font-size: 0.75rem; background: #fffbeb; padding: 3px 8px; border-radius: 100px; margin-left: 8px; border: 1px solid #fef3c7; .star { color: #d1d5db; } .star.filled { color: #f59e0b; } }
.review-sub { color: #94a3b8; font-size: 0.8rem; margin-top: 4px; font-weight: 600; }

/* TEXT CONTENT */
.review-text { 
  margin: 14px 0 18px 62px; color: #334155; line-height: 1.65; font-size: 1rem; 
  white-space: pre-wrap; font-weight: 500; letter-spacing: 0.1px;
}
.is-child .review-text { margin-left: 50px; font-size: 0.95rem; margin-bottom: 12px; }

/* HIGHLIGHT TAG MÀU CAM NEON */
:deep(.mention-tag) { 
  color: #ea580c; font-weight: 800; background: #fff4ed; 
  padding: 2px 8px; border-radius: 8px; border: 1px solid #ffedd5;
}

/* GRID ẢNH PREMIUM */
.review-images { margin-left: 62px; margin-bottom: 18px; }
.is-child .review-images { margin-left: 50px; }
.image-grid-premium { display: grid; gap: 10px; max-width: 450px; }
.image-cell { 
  position: relative; aspect-ratio: 1/1; border-radius: 16px; overflow: hidden; 
  cursor: pointer; box-shadow: 0 4px 12px rgba(0,0,0,0.05); border: 1px solid rgba(0,0,0,0.03);
  img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
  &:hover img { transform: scale(1.08); }
}
.more-overlay { 
  position: absolute; inset: 0; background: rgba(0,0,0,0.5); backdrop-filter: blur(4px); 
  display: flex; align-items: center; justify-content: center; 
  span { color: #fff; font-weight: 800; font-size: 1.4rem; text-shadow: 0 2px 4px rgba(0,0,0,0.5); }
}

/* ACTIONS BAR */
.review-actions-bar { margin-left: 62px; display: flex; align-items: center; gap: 20px; }
.is-child .review-actions-bar { margin-left: 50px; }
.action-btn { 
  background: transparent; border: none; font-size: 0.85rem; font-weight: 700; color: #64748b; 
  cursor: pointer; display: flex; align-items: center; gap: 6px; padding: 6px 12px; 
  border-radius: 100px; transition: all 0.2s ease;
  
  .icon-wrapper { display: flex; align-items: center; justify-content: center; transition: 0.2s; }
  
  &:hover { background: #f8fafc; color: #0f172a; }
  
  &.like-btn.active { 
    color: #ea580c; background: #fff4ed; 
    .icon-wrapper { animation: heartBeat 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
    svg { fill: #ea580c; stroke: #ea580c; } 
  }
  
  &.delete-btn { padding: 6px; border-radius: 50%; margin-left: -10px; opacity: 0.5; }
  &.delete-btn:hover { background: #fef2f2; color: #ef4444; opacity: 1; }
}

@keyframes heartBeat { 0% { transform: scale(1); } 50% { transform: scale(1.4); } 100% { transform: scale(1); } }

.like-count { font-size: 0.95rem; font-weight: 800; font-variant-numeric: tabular-nums; }

/* REPLY BOX LUXURY (GLASSMORPHISM) */
.inline-reply-luxury { display: flex; gap: 14px; margin-top: 20px; padding-left: 62px; &.is-child { padding-left: 50px; margin-top: 14px; } }
.reply-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; box-shadow: 0 2px 6px rgba(0,0,0,0.05); }
.reply-input-wrapper { 
  flex: 1; display: flex; flex-direction: column; background: #fff; 
  border: 1px solid #e2e8f0; border-radius: 20px; padding: 6px; 
  box-shadow: 0 4px 12px rgba(0,0,0,0.02); transition: all 0.3s;
  &:focus-within { border-color: #ea580c; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); transform: translateY(-2px); }
  
  textarea { border: none; outline: none; padding: 12px 14px; font-size: 0.95rem; resize: none; min-height: 50px; font-weight: 500; color: #0f172a; &::placeholder { color: #94a3b8; } }
}
.reply-actions { display: flex; justify-content: flex-end; gap: 8px; padding: 8px; }
.btn-cancel-glass { background: transparent; color: #64748b; border: none; font-weight: 700; cursor: pointer; padding: 8px 16px; border-radius: 100px; transition: 0.2s; &:hover { background: #f1f5f9; color: #0f172a; } }
.btn-submit-glow { background: #ea580c; color: white; border: none; font-weight: 800; cursor: pointer; padding: 8px 24px; border-radius: 100px; transition: 0.2s; box-shadow: 0 4px 10px rgba(234, 88, 12, 0.3); &:hover:not(:disabled) { background: #c2410c; transform: translateY(-1px); box-shadow: 0 6px 14px rgba(234, 88, 12, 0.4); } &:disabled { background: #e2e8f0; box-shadow: none; color: #94a3b8; cursor: not-allowed; } }

/* NESTED THREAD PREMIUM */
.nested-replies-container { position: relative; margin-top: 16px; padding-left: 24px; margin-left: 24px; }
.thread-line-premium { position: absolute; top: 0; bottom: 0; left: 0; width: 3px; background: rgba(234, 88, 12, 0.1); border-radius: 10px; }

/* MODAL GLASSMORPHISM */
.modal-backdrop-glass { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.85); backdrop-filter: blur(12px); display: flex; align-items: center; justify-content: center; z-index: 9999; }
.modal-content img { max-width: 90vw; max-height: 90vh; border-radius: 24px; box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5); border: 1px solid rgba(255,255,255,0.1); }
.modal-close-glass { position: absolute; top: 30px; right: 30px; background: rgba(255,255,255,0.1); border: 1px solid rgba(255,255,255,0.2); color: white; cursor: pointer; padding: 12px; border-radius: 50%; transition: 0.3s; &:hover { background: rgba(255,255,255,0.2); transform: scale(1.1) rotate(90deg); } }

/* ANIMATIONS */
.fade-in { animation: fadeIn 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
.slide-down { animation: slideDown 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards; transform-origin: top; }
@keyframes slideDown { from { opacity: 0; transform: scaleY(0.95); } to { opacity: 1; transform: scaleY(1); } }
.fade-scale-enter-active, .fade-scale-leave-active { transition: all 0.3s ease; }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; transform: scale(0.95); }

/* SKELETON PREMIUM */
.skeleton-comment-premium { display: flex; gap: 14px; margin-bottom: 24px; }
.sk-avatar { width: 48px; height: 48px; border-radius: 50%; background: #f1f5f9; position: relative; overflow: hidden; }
.sk-content { flex: 1; display: flex; flex-direction: column; gap: 10px; padding-top: 6px; }
.sk-name { width: 140px; height: 16px; border-radius: 8px; background: #f1f5f9; position: relative; overflow: hidden; }
.sk-text { width: 100%; height: 14px; border-radius: 8px; background: #f1f5f9; position: relative; overflow: hidden; &.short { width: 65%; } }
.sk-avatar::after, .sk-name::after, .sk-text::after { content: ''; position: absolute; inset: 0; background: linear-gradient(90deg, transparent, rgba(255,255,255,0.5), transparent); animation: shimmer 1.5s infinite; }
</style>