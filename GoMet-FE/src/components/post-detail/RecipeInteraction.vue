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
            <p class="auth-quote">"{{ post.authorQuote || 'Nấu ăn là cách tôi trao gửi yêu thương.' }}"</p>
            <div class="auth-stats">
              <div class="stat"><b>{{ authorStats.posts }}</b> <span>{{ $t('leaderboard.recipes') }}</span></div>
              <div class="stat"><b>{{ authorStats.followers }}</b> <span>Follower</span></div>
              <div class="stat"><b>{{ avgRating || '-' }}</b> <span>{{ $t('recipe.good_rating') }}</span></div>
            </div>
          </div>
        </div>
      </section>

      <section class="reviews-section fade-in">
        <div class="reviews-header">
          <h3 class="section-title">{{ $t('post.community_reviews') }}</h3>
          <div class="review-summary-card">
            <div class="score-box">
              <span class="big-score">{{ avgRating || '-' }}</span>
              <div class="stars-display">★★★★★</div>
              <span class="total-reviews">{{ totalRatings }} {{ $t('post.ratings') }}</span>
            </div>
            <div class="rating-bars">
              <div class="bar-row" v-for="i in 5" :key="i">
                <span class="star-label">{{ 6-i }} <span class="s">★</span></span>
                <div class="progress-bg"><div class="progress-fill" :style="{ width: ratingDistribution[i-1] + '%' }"></div></div>
              </div>
            </div>
          </div>
        </div>

        <div class="review-input-wrapper">
          <div class="input-header">
            <img :src="authStore.user?.avatar || 'https://ui-avatars.com/api/?name=U&background=EA580C&color=fff'" class="current-user-avt">
            <div class="rating-selector">
              <span>{{ $t('post.rating_prompt') }}</span>
              <div class="star-rating-input">
                <span v-for="star in 5" :key="star" @click="userRating = star" :class="{ active: star <= userRating }">★</span>
              </div>
            </div>
          </div>
          <div class="textarea-box">
            <textarea v-model="newComment" :placeholder="$t('post.add_comment')"></textarea>
            <div class="input-footer">
              <button class="btn-attach">{{ $t('recipe.add_photo') }}</button>
              <button class="btn-submit-review" @click="submitComment">{{ $t('post.submit_review') }}</button>
            </div>
          </div>
        </div>

        <div class="comments-feed">
          <div class="comment-card" v-for="cmt in commentsList" :key="cmt.id">
            <img :src="cmt.avatar" class="cmt-avatar" :alt="cmt.name">
            <div class="cmt-content-wrapper">
              <div class="cmt-bubble">
                <span class="cmt-author">{{ cmt.name || $t('recipe.anonymous_user') }}</span>
                <span class="verified-badge" v-if="cmt.verified">✓ Bếp trưởng</span>
                <div class="stars-inline" v-if="cmt.rating">
                  <span class="stars" v-for="n in 5" :key="n" :class="{ filled: n <= cmt.rating }">★</span>
                </div>
                <p class="cmt-text">{{ cmt.content }}</p>
                <div class="cmt-images" v-if="cmt.images && cmt.images.length">
                  <img v-for="(img, idx) in cmt.images" :key="idx" :src="img">
                </div>
              </div>
              
              <div class="cmt-actions">
                <button class="action-link" :class="{ 'liked': cmt.isLiked }" @click="toggleLike(cmt)">
                  {{ $t('recipe.helpful') }} ({{ cmt.likes }})
                </button>
                <button class="action-link" @click="setReply(cmt)">{{ $t('recipe.reply') }}</button>
                <span class="cmt-date">{{ formatTime(cmt.time) }}</span>
              </div>

              <!-- Inline Reply Box cho Root Comment -->
              <div v-if="activeReplyId === cmt.id" class="inline-reply-box">
                <img :src="authStore.user?.avatar || 'https://ui-avatars.com/api/?name=U&background=EA580C&color=fff'" class="reply-user-avt">
                <div class="reply-input-area">
                  <textarea v-model="replyBoxContent" :placeholder="'Viết bình luận công khai cho ' + cmt.name + '...'"></textarea>
                  <div class="reply-controls">
                    <button @click="cancelReply" class="btn-cancel">Hủy</button>
                    <button @click="submitInlineReply(cmt)" class="btn-post" :disabled="!replyBoxContent.trim()">Đăng</button>
                  </div>
                </div>
              </div>

              <!-- Nested Replies (Cây trả lời) -->
              <div class="nested-replies-fb" v-if="cmt.replies && cmt.replies.length > 0">
                <div class="comment-card nested-card" v-for="reply in cmt.replies" :key="reply.id">
                  <img :src="reply.avatar" class="cmt-avatar nested-avt" :alt="reply.name">
                  <div class="cmt-content-wrapper">
                    <div class="cmt-bubble">
                      <span class="cmt-author">
                         {{ reply.name || $t('recipe.anonymous_user') }}
                         <span v-if="reply.replyToName" class="reply-indicator"> ▶ {{ reply.replyToName }}</span>
                      </span>
                      <p class="cmt-text">
                        {{ reply.content }}
                      </p>
                    </div>

                    <div class="cmt-actions">
                      <button class="action-link" :class="{ 'liked': reply.isLiked }" @click="toggleLike(reply)">
                        {{ $t('recipe.helpful') }} ({{ reply.likes || 0 }})
                      </button>
                      <button class="action-link" @click="setReply(reply)">{{ $t('recipe.reply') }}</button>
                      <span class="cmt-date">{{ formatTime(reply.time) }}</span>
                    </div>

                    <!-- Inline Reply Box cho Bình luận con -->
                    <div v-if="activeReplyId === reply.id" class="inline-reply-box">
                      <img :src="authStore.user?.avatar || 'https://ui-avatars.com/api/?name=U&background=EA580C&color=fff'" class="reply-user-avt">
                      <div class="reply-input-area">
                        <textarea v-model="replyBoxContent" :placeholder="'Viết câu trả lời cho ' + reply.name + '...'"></textarea>
                        <div class="reply-controls">
                          <button @click="cancelReply" class="btn-cancel">Hủy</button>
                          <button @click="submitInlineReply(reply)" class="btn-post" :disabled="!replyBoxContent.trim()">Đăng</button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat' // Import Store Chat
import { getComments, addComment, ratePost } from '@/services/interactionService'
import { checkFollow, follow, unfollow, checkFavorite, addFavorite, removeFavorite } from '@/services/socialService'
import { getUserStats } from '@/services/userService'
import { toast } from '@/composables/useToast'
import axios from 'axios'

const props = defineProps({ post: Object })
const { t } = useI18n()

const authStore = useAuthStore()
const chatStore = useChatStore() // Khởi tạo Store Chat

const userRating = ref(0)
const newComment = ref('')
const commentsList = ref([])
const avgRating = ref(0)
const totalRatings = ref(0)
const isFollowing = ref(false)
const isFavorite = ref(false)
const authorStats = ref({ posts: 0, followers: 0 })

const activeReplyId = ref(null)
const replyBoxContent = ref('')

const setReply = (cmt) => {
  activeReplyId.value = cmt.id
  replyBoxContent.value = ''
}

const cancelReply = () => {
  activeReplyId.value = null
  replyBoxContent.value = ''
}

// Logic tính toán phân phối rating
const ratingDistribution = computed(() => {
  const counts = [0, 0, 0, 0, 0]
  commentsList.value.forEach(c => {
    if (c.rating >= 1 && c.rating <= 5) counts[c.rating - 1]++
  })
  const total = counts.reduce((s, n) => s + n, 0)
  return counts.map(n => total > 0 ? Math.round(n / total * 100) : 0).reverse()
})

const normalizeComment = (c) => ({
  id: c.commentID,
  name: c.authorName || '',
  avatar: c.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(c.authorName || 'U')}&background=EA580C&color=fff`,
  time: c.createdAt || null,
  rating: c.rating || 0,
  verified: false,
  content: c.content,
  likes: 0,
  isLiked: false,
  cmtid: c.cmtid || null,
  replies: [],
  replyToName: null 
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

    const commentMap = {}
    const rootComments = []

    normalizedData.forEach(c => {
      commentMap[c.id] = c
    })

    normalizedData.forEach(c => {
      if (c.cmtid && commentMap[c.cmtid]) {
        // LUÔN LUÔN tag tên người mà comment này trực tiếp Repy lại (dù là Root hay Child)
        c.replyToName = commentMap[c.cmtid].name;
        
        // Tìm lên thằng ông tổ Root đệ quy dồn chung tất cả con cháu vào 1 mảng replies để làm phẳng layout Facebook
        let currentRoot = commentMap[c.cmtid]
        while (currentRoot.cmtid && commentMap[currentRoot.cmtid]) {
          currentRoot = commentMap[currentRoot.cmtid]
        }
        currentRoot.replies.push(c)
      } else {
        rootComments.push(c)
      }
    })

    commentsList.value = rootComments

    const rated = normalizedData.filter(c => c.rating > 0)
    if (rated.length) {
      avgRating.value = (rated.reduce((s, c) => s + c.rating, 0) / rated.length).toFixed(1)
      totalRatings.value = rated.length
    }
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
  if (!content) return
  if (!authStore.isAuthenticated) { toast.warn(t('toast.need_login')); return }
  const accountID = authStore.user.accountID
  const postID = props.post?.postID
  if (!postID) return
  try {
    await addComment(postID, accountID, content, null)
    if (userRating.value > 0) {
      await ratePost(accountID, postID, userRating.value)
    }
    newComment.value = ''
    userRating.value = 0
    await loadComments(postID)
    toast.success(t('toast.comment_ok'))
  } catch (err) {
    toast.error(t('toast.error_generic'))
  }
}

const submitInlineReply = async (target) => {
  const content = replyBoxContent.value.trim()
  if (!content) return
  if (!authStore.isAuthenticated) { toast.warn(t('toast.need_login')); return }
  const accountID = authStore.user.accountID
  const postID = props.post?.postID
  if (!postID) return
  try {
    const cmtid = target.id
    await addComment(postID, accountID, content, cmtid)
    replyBoxContent.value = ''
    activeReplyId.value = null
    await loadComments(postID)
    toast.success(t('toast.comment_ok'))
  } catch (err) {
    toast.error(t('toast.error_generic'))
  }
}
</script>

<style scoped lang="scss" src="./RecipeInteraction.scss"></style>