<template>
  <div class="interaction-container">
    <div class="divider-section"></div>

    <div class="grid-interaction">
      
      <section class="author-section fade-in">
        <div class="author-vip-card">
          <div class="auth-left">
            <div class="avatar-ring">
              <img :src="post.authorAvatar" class="auth-img" alt="Author Avatar">
            </div>
          </div>
          <div class="auth-right">
            <div class="auth-header">
              <h3 class="auth-name">{{ post.author }} <span class="check-icon">✓</span></h3>
              <div class="btn-group-header">
                <button class="btn-favorite" :class="{ 'is-saved': isFavorite }" @click="toggleFavorite" :title="isFavorite ? $t('post.saved') : $t('post.unsaved')">
                  {{ isFavorite ? '♥' : '♡' }}
                </button>

                <button class="btn-message-chef" @click="handleContactChef" :title="$t('common.message')">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                  </svg>
                </button>

                <button class="btn-connect" :class="{ 'is-following': isFollowing }" @click="toggleFollow">
                  {{ isFollowing ? $t('profile.following') : $t('recipe.connect') }}
                </button>
              </div>
            </div>
            <p class="auth-quote">"{{ post.authorBio || 'Nấu ăn là cách tôi trao gửi yêu thương.' }}"</p>
            <div class="auth-stats">
              <div class="stat"><b>{{ authorStats.posts }}</b> <span>{{ $t('leaderboard.recipes') }}</span></div>
              <div class="stat"><b>{{ authorStats.followers }}</b> <span>Follower</span></div>
              <div class="stat"><b>{{ post.avgRating || '-' }}</b> <span>{{ $t('recipe.good_rating') }}</span></div>
            </div>
          </div>
        </div>
      </section>

      <section class="reviews-section fade-in">
        <h3 class="section-title">{{ $t('post.qa_discussion') || 'Hỏi đáp & Thảo luận' }}</h3>

        <div class="review-input-wrapper">
          <div class="input-header">
            <img :src="authStore.user?.avatar || 'https://ui-avatars.com/api/?name=U&background=EA580C&color=fff'" class="current-user-avt">
            <div class="rating-selector">
              <span>{{ $t('post.add_comment') }}</span>
            </div>
          </div>
          <div class="textarea-box">
            <textarea v-model="newComment" :placeholder="$t('post.add_comment')"></textarea>
            
            <!-- Preview images -->
            <div class="comment-image-previews" v-if="selectedPhotos.length">
              <div v-for="(file, idx) in selectedPhotos" :key="idx" class="preview-item">
                <img :src="file.preview" />
                <button @click="removePhoto(idx)" class="remove-btn">&times;</button>
              </div>
            </div>

            <div class="input-footer">
              <label class="comment-upload-btn">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                  <circle cx="8.5" cy="8.5" r="1.5"></circle>
                  <polyline points="21 15 16 10 5 21"></polyline>
                </svg>
                <input type="file" hidden accept="image/*" multiple @change="onPhotosSelected" />
              </label>
              <button class="btn-submit-review" @click="submitComment" :disabled="!newComment.trim() && selectedPhotos.length === 0">
                {{ $t('post.submit_comment') || 'Gửi bình luận' }}
              </button>
            </div>
          </div>
        </div>

        <div class="comments-feed">
          <CommentThread
            :postId="post.postID"
            :currentUserId="authStore.user?.accountID"
            :refreshKey="commentVersion"
          />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat' // Import Store Chat
import CommentThread from './CommentThread.vue'
import { getComments, addComment } from '@/services/interactionService'
import { checkFollow, follow, unfollow, checkFavorite, addFavorite, removeFavorite } from '@/services/socialService'
import { getUserStats } from '@/services/userService'
import { uploadMedia } from '@/services/uploadService'
import { toast } from '@/composables/useToast'
import axios from 'axios'

const props = defineProps({ post: Object })
const { t } = useI18n()

const authStore = useAuthStore()
const chatStore = useChatStore() // Khởi tạo Store Chat

const newComment = ref('')
const commentVersion = ref(0) // increment to refresh thread view
const commentsList = ref([])
const isFollowing = ref(false)
const isFavorite = ref(false)
const authorStats = ref({ posts: 0, followers: 0 })
const selectedPhotos = ref([])
const isUploading = ref(false)

const onPhotosSelected = (e) => {
  const files = Array.from(e.target.files || [])
  files.forEach(file => {
    selectedPhotos.value.push({ file, preview: URL.createObjectURL(file) })
  })
}

const removePhoto = (idx) => {
  const removed = selectedPhotos.value.splice(idx, 1)
  if (removed.length) URL.revokeObjectURL(removed[0].preview)
}


const normalizeComment = (c) => ({
  commentID: c.commentID,
  accountID: c.accountID,
  authorName: c.authorName || '',
  authorAvatar: c.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(c.authorName || 'U')}&background=EA580C&color=fff`,
  createdAt: c.createdAt || null,
  content: c.content,
  imageUrls: c.imageUrls || [],
  children: (c.children || []).map(normalizeComment),
})

// Format thời gian sang chuỗi hiển thị tương đối
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

const toggleLike = (cmt) => {
  if (!authStore.isAuthenticated) {
    toast.error(t('auth.login_required'))
    return
  }
  
  if (cmt.isLiked) {
    cmt.likes--
    cmt.isLiked = false
  } else {
    cmt.likes++
    cmt.isLiked = true
  }
}

const loadComments = async (postID) => {
  if (!postID) return
  try {
    const data = await getComments(postID)
    const normalizedData = (data || []).map(normalizeComment)
    commentsList.value = normalizedData
  } catch (err) {
    console.warn('RecipeInteraction: load comments error', err)
  }
}

const loadSocialState = async (post) => {
  if (!post) return
  const uid = authStore.user?.accountID
  const authorID = post.authorID
  if (authorID) {
    try {
      const stats = await getUserStats(authorID)
      authorStats.value = { posts: stats.postCount || stats.posts || 0, followers: stats.followerCount || stats.followers || 0 }
    } catch { /* ignore */ }
  }
  if (uid && uid !== authorID) {
    try {
      const [followRes, favRes] = await Promise.allSettled([
        checkFollow(uid, authorID),
        checkFavorite(uid, post.postID)
      ])
      if (followRes.status === 'fulfilled') isFollowing.value = !!followRes.value
      if (favRes.status === 'fulfilled') isFavorite.value = !!favRes.value
    } catch { /* ignore */ }
  }
}

// LOGIC NHẮN TIN CHO ĐẦU BẾP
const handleContactChef = async () => {
  if (!authStore.isAuthenticated) {
    toast.warn(t('toast.need_login'));
    return;
  }

  const currentUserId = authStore.user?.accountID;
  const chefId = props.post?.authorID;

  if (currentUserId === chefId) {
    toast.info('Sếp không thể tự nhắn tin cho chính mình đâu nha!');
    return;
  }

  try {
    // Gọi API lấy hoặc tạo mới cuộc hội thoại
    const res = await axios.post('http://localhost:8080/api/conversations/access', {
      user1Id: currentUserId,
      user2Id: chefId
    });

    const conversationData = {
      id: res.data.conversationID,
      name: props.post.author,
      avatar: props.post.authorAvatar,
      online: true 
    };

    // Mở khung chat mini và đẩy Sidebar danh sách chat ra
    chatStore.openChat(conversationData);
    chatStore.isMessengerOpen = true; 
    
    toast.success(`Đang kết nối với đầu bếp ${props.post.author}...`);
  } catch (err) {
    console.error("Lỗi chat:", err);
    toast.error('Không thể kết nối lúc này, Sếp vui lòng thử lại sau nhé!');
  }
}

const toggleFollow = async () => {
  if (!authStore.isAuthenticated) { toast.warn(t('toast.need_login')); return }
  const uid = authStore.user.accountID
  const authorID = props.post?.authorID
  if (!authorID || uid === authorID) return
  try {
    if (isFollowing.value) {
      await unfollow(uid, authorID)
      isFollowing.value = false
      authorStats.value.followers = Math.max(0, (authorStats.value.followers || 1) - 1)
      toast.success(t('toast.unfollow_ok'))
    } else {
      await follow(uid, authorID)
      isFollowing.value = true
      authorStats.value.followers = (authorStats.value.followers || 0) + 1
      toast.success(t('toast.follow_ok'))
    }
  } catch (err) {
    toast.error(t('toast.error_generic'))
  }
}

const toggleFavorite = async () => {
  if (!authStore.isAuthenticated) { toast.warn(t('toast.need_login')); return }
  const uid = authStore.user.accountID
  const postID = props.post?.postID
  if (!postID) return
  try {
    if (isFavorite.value) {
      await removeFavorite(uid, postID)
      isFavorite.value = false
      toast.success(t('toast.unsave_ok'))
    } else {
      await addFavorite(uid, postID)
      isFavorite.value = true
      toast.success(t('toast.save_ok'))
    }
  } catch (err) {
    toast.error(t('toast.error_generic'))
  }
}

watch(() => props.post, (post) => {
  if (post?.postID) {
    loadComments(post.postID)
    loadSocialState(post)
  }
}, { immediate: true })

const submitComment = async () => {
  const content = newComment.value.trim()
  if (!content && selectedPhotos.value.length === 0) return
  if (!authStore.isAuthenticated) { toast.warn(t('toast.need_login')); return }
  
  const accountID = authStore.user.accountID
  const postID = props.post?.postID
  if (!postID) return

  isUploading.value = true
  try {
    const imageUrls = []
    for (const item of selectedPhotos.value) {
      const url = await uploadMedia(item.file, 'comments')
      imageUrls.push(url)
    }

    await addComment(postID, accountID, content, null, imageUrls)
    
    newComment.value = ''
    selectedPhotos.value.forEach(f => URL.revokeObjectURL(f.preview))
    selectedPhotos.value = []
    
    await loadComments(postID)
    commentVersion.value++
    toast.success(t('toast.comment_ok'))
  } catch (err) {
    toast.error(t('toast.error_generic'))
  } finally {
    isUploading.value = false
  }
}

const handleSubmitReply = async ({ parentId, content }) => {
  if (!content || !content.trim()) return
  if (!authStore.isAuthenticated) { toast.warn(t('toast.need_login')); return }
  const accountID = authStore.user.accountID
  const postID = props.post?.postID
  if (!postID) return

  try {
    await addComment(postID, accountID, content.trim(), parentId, [])
    await loadComments(postID)
    commentVersion.value++
    toast.success(t('toast.comment_ok'))
  } catch (err) {
    toast.error(t('toast.error_generic'))
  }
}
</script>

<style scoped lang="scss" src="./RecipeInteraction.scss"></style>