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
            <img :src="cmt.avatar" class="cmt-avatar">
            <div class="cmt-body">
              <div class="cmt-meta">
                <span class="cmt-author">{{ cmt.name || $t('recipe.anonymous_user') }}</span>
                <span class="cmt-date">{{ cmt.time || $t('recipe.just_posted') }}</span>
              </div>
              <div class="cmt-rating">
                <span class="stars" v-for="n in 5" :key="n" :class="{ filled: n <= cmt.rating }">★</span>
                <span class="verified-badge" v-if="cmt.verified">✓ {{ $t('recipe.verified_cook') }}</span>
              </div>
              <p class="cmt-text">{{ cmt.content }}</p>
              <div class="cmt-images" v-if="cmt.images">
                <img v-for="(img, idx) in cmt.images" :key="idx" :src="img">
              </div>
              <div class="cmt-actions">
                <button class="action-link"><span class="icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3H14z"/><path d="M7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg></span> {{ $t('recipe.helpful') }} ({{ cmt.likes }})</button>
                <button class="action-link">{{ $t('recipe.reply') }}</button>
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
  time: null,
  rating: c.rating || 0,
  verified: false,
  content: c.content,
  likes: 0
})

const loadComments = async (postID) => {
  if (!postID) return
  try {
    const data = await getComments(postID)
    commentsList.value = (data || []).map(normalizeComment)
    const rated = commentsList.value.filter(c => c.rating > 0)
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
    await addComment(postID, accountID, content)
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
</script>

<style scoped lang="scss" src="./RecipeInteraction.scss"></style>