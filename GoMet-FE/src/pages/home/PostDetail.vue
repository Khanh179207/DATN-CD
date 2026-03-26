<template>
  <div class="post-page-container">
    
    <div v-if="!post" class="luxury-loading">
      <div class="sk-container-inner">
        <div class="sk-hero-col">
          <div class="sk-badge"></div>
          <div class="sk-title"></div>
          <div class="sk-title short"></div>
          <div class="sk-desc-group">
            <div class="sk-desc w-100"></div>
            <div class="sk-desc w-80"></div>
            <div class="sk-desc w-60"></div>
          </div>
          <div class="sk-meta-row">
            <div class="sk-avatar"></div>
            <div class="sk-meta-text"></div>
          </div>
        </div>
        <div class="sk-image-col">
          <div class="sk-img-box"></div>
        </div>
      </div>
    </div>

    <template v-else>
      <div class="post-content-wrapper fade-in">
        
        <RecipeHero :post="post" />
        <RecipeInformation :post="post" />
        
        <div class="author-full-width-section">
          <div class="global-container">
            <RecipeAuthor :post="post" /> 
          </div>
        </div>
        
        <div class="bottom-split-section">
          <div class="split-container-inner">
            
            <div class="interaction-col">
              
              <div class="reviews-header">
                <h3 class="section-title">Cộng đồng GoMet</h3>
                <p class="section-subtitle">Chia sẻ cảm nhận và hình ảnh thực tế của bạn</p>
              </div>

              <ReviewSummary 
                :avgRating="avgRating" 
                :totalRatings="totalRatings" 
                :ratingDistribution="ratingDistribution" 
              />

              <div class="review-input-luxury">
                <div class="input-header">
                  <img :src="currentUserAvatar" class="current-user-avt" alt="User">
                  
                  <div class="rating-selector" v-if="!hasUserRated">
                    <span class="prompt-text">Bạn chấm món này mấy sao?</span>
                    <div class="star-rating-input">
                      <span v-for="star in 5" :key="star" @click="userRating = star" :class="{ active: star <= userRating }">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                      </span>
                    </div>
                  </div>

                  <div class="rating-selector locked" v-else>
                    <span class="prompt-text text-muted">Đánh giá của bạn:</span>
                    <div class="star-rating-locked">
                      <span v-for="star in 5" :key="star" :class="{ 'filled': star <= (currentUserReview.rating || currentUserReview.rate) }">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                      </span>
                    </div>
                  </div>
                </div>
                
                <div class="textarea-box">
                  <textarea 
                    ref="commentInputRef" 
                    v-model="newComment" 
                    @input="autoResize"
                    :placeholder="userRating > 0 && userRating < 3 ? 'Vui lòng chia sẻ lý do bạn chưa hài lòng về món ăn này (* Bắt buộc)' : 'Góp ý thêm về gia vị, cách làm hoặc chia sẻ thành quả...'"
                  ></textarea>
                  
                  <div class="comment-image-previews" v-if="selectedPhotos.length">
                    <div v-for="(file, idx) in selectedPhotos" :key="idx" class="preview-item">
                      <img :src="file.preview" />
                      <button @click="removePhoto(idx)" class="remove-btn">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                      </button>
                    </div>
                  </div>

                  <div class="input-footer">
                    <label class="comment-upload-btn" title="Đính kèm hình ảnh thành quả">
                      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg>
                      <span class="upload-text">Thêm ảnh</span>
                      <input type="file" hidden accept="image/*" multiple @change="onPhotosSelected" />
                    </label>
                    
                    <div class="action-buttons">
                      <button v-if="newComment.trim() || selectedPhotos.length || userRating > 0" class="btn-cancel-review" @click="clearForm" :disabled="isUploading">Hủy</button>
                      <button class="btn-submit-review" @click="submitComment" :disabled="isSubmitDisabled">
                        <svg v-if="isUploading" class="spinner-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12a9 9 0 1 1-6.219-8.56"></path></svg>
                      <span>{{ isUploading ? 'Đang gửi...' : (hasUserRated ? 'Gửi bình luận' : 'Gửi đánh giá') }}</span>
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <RecipeComments 
                :is-root="true"
                :raw-comments="rawCommentsList"
                :loading="isCommentsLoading"
                :post-id="post.id"
                @reply-submitted="handleSubmitReply"
                @toggle-like="handleToggleLikeComment"
                @delete-comment="handleDeleteComment"
                @reload="fetchComments(post.id)"
              />

            </div>

            <div class="related-sidebar-col">
              <div class="sticky-sidebar">
                <RelatedSuggestions :items="relatedPosts" @click-post="goToDetail" />
              </div>
            </div>

          </div>
        </div>
        
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { toast } from '@/composables/useToast'
import { useAuthStore } from '@/stores/auth'

// --- IMPORT COMPONENTS ---
import RecipeHero from '@/components/post-detail/RecipeHero.vue'
import RecipeInformation from '@/components/post-detail/RecipeInformation.vue'
import RecipeAuthor from '@/components/post-detail/RecipeAuthor.vue'
import ReviewSummary from '@/components/post-detail/ReviewSummary.vue'
import RecipeComments from '@/components/post-detail/RecipeComments.vue' 
import RelatedSuggestions from '@/components/post-detail/RelatedSuggestions.vue'

// --- IMPORT SERVICES ---
import { getPostById, getRelatedPosts, normalizePost } from '@/services/postService'
import { uploadMedia } from '@/services/uploadService'
import { recordHistory } from '@/services/interactionService'

/* START: Daily View Limit */
import { usePostViewLimit } from '@/composables/usePostViewLimit'
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

// --- STATE QUẢN LÝ COMMENTS & FORM ---
const rawCommentsList = ref([])
const isCommentsLoading = ref(false)
const commentInputRef = ref(null)
const newComment = ref('')
const userRating = ref(0)
const selectedPhotos = ref([])
const isUploading = ref(false)

const currentUserAvatar = computed(() => authStore.user?.avatar || 'https://ui-avatars.com/api/?name=U&background=EA580C&color=fff')

// --- COMPUTED: CHỐNG SPAM RATING & THỐNG KÊ ---
const currentUserReview = computed(() => {
  if (!authStore.isAuthenticated || !rawCommentsList.value.length) return null;
  const uid = authStore.user.accountID || authStore.user.id;
  return rawCommentsList.value.find(c => (c.accountID === uid || c.userId === uid) && c.rating > 0);
});

const hasUserRated = computed(() => !!currentUserReview.value);
const ratedComments = computed(() => rawCommentsList.value.filter(c => c.rating > 0))

const avgRating = computed(() => {
  if (!ratedComments.value.length) return 0
  const sum = ratedComments.value.reduce((acc, curr) => acc + curr.rating, 0)
  return (sum / ratedComments.value.length).toFixed(1)
})

const totalRatings = computed(() => ratedComments.value.length)

const ratingDistribution = computed(() => {
  const counts = [0, 0, 0, 0, 0]
  ratedComments.value.forEach(c => { if (c.rating >= 1 && c.rating <= 5) counts[c.rating - 1]++ })
  const total = totalRatings.value || 1 
  return [ (counts[4]/total)*100, (counts[3]/total)*100, (counts[2]/total)*100, (counts[1]/total)*100, (counts[0]/total)*100 ]
})

const isSubmitDisabled = computed(() => {
  if (isUploading.value) return true;
  const hasText = !!newComment.value.trim();
  const hasPhoto = selectedPhotos.value.length > 0;
  
  if (userRating.value > 0 && userRating.value < 3 && !hasText) return true; // Dưới 3 sao bắt buộc phải nhập text
  if (!hasText && !hasPhoto && userRating.value === 0) return true; // Trống rỗng thì không cho gửi
  return false;
});

// --- TIỆN ÍCH FORM ---
const autoResize = () => {
  if (commentInputRef.value) {
    commentInputRef.value.style.height = 'auto';
    commentInputRef.value.style.height = (commentInputRef.value.scrollHeight) + 'px';
  }
}
const clearForm = () => {
  newComment.value = ''; userRating.value = 0;
  selectedPhotos.value.forEach(f => URL.revokeObjectURL(f.preview)); selectedPhotos.value = [];
  if (commentInputRef.value) commentInputRef.value.style.height = 'auto';
}
const onPhotosSelected = (e) => {
  if (!authStore.isAuthenticated) { toast.warn('Vui lòng đăng nhập!'); return }
  Array.from(e.target.files || []).forEach(file => {
    selectedPhotos.value.push({ file, preview: URL.createObjectURL(file) })
  })
}
const removePhoto = (idx) => {
  const removed = selectedPhotos.value.splice(idx, 1)
  if (removed.length) URL.revokeObjectURL(removed[0].preview)
}

const fetchComments = async (postId) => {
  isCommentsLoading.value = true
  try {
    const uid = authStore.user?.accountID || authStore.user?.id || '';
    const { data } = await axios.get(`http://localhost:8080/api/comments/post/${postId}`, { params: { currentAccountID: uid } });
    rawCommentsList.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error('Lỗi tải bình luận:', err)
    rawCommentsList.value = []
  } finally {
    isCommentsLoading.value = false

    // Cuộn đến và làm nổi bật bình luận nếu có hash trên URL
    const targetHash = route.hash || window.location.hash;
    if (targetHash && targetHash.startsWith('#comment-')) {
      let attempts = 0
      const findAndScroll = setInterval(() => {
        const element = document.querySelector(targetHash)
        if (element) {
          clearInterval(findAndScroll)
          element.scrollIntoView({ behavior: 'smooth', block: 'center' })
          element.style.transition = 'all 0.8s cubic-bezier(0.16, 1, 0.3, 1)'
          element.style.backgroundColor = '#fff7ed' // Màu cam siêu nhạt highlight
          element.style.borderRadius = '16px'
          element.style.boxShadow = '0 0 0 12px #fff7ed' // Phủ tràn viền bo góc
          setTimeout(() => {
            element.style.backgroundColor = 'transparent'
            element.style.boxShadow = '0 0 0 0 transparent'
          }, 3000) // Tắt highlight sau 3s
        }
        attempts++
        if (attempts > 10) { // Thử tìm tối đa 10 lần (5 giây), nếu không có thì bỏ qua
          clearInterval(findAndScroll)
        }
      }, 500)
    }
  }
}

async function loadPost(id) {
  try {
    const dto = await getPostById(id)

    /* START: Daily View Limit Check */
    const canAccess = checkAndChargeView(Number(id), dto.authorID)
    if (!canAccess) {
      toast.warn('Bạn đã hết lượt xem bài viết miễn phí trong ngày hôm nay! Vui lòng nâng cấp Premium.')
      router.push('/home')
      window.dispatchEvent(new CustomEvent('ui:open-premium'))
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
      authorAvatar: dto.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(dto.authorName||'G')}&background=EA580C&color=fff`,
      authorBio: dto.authorBio || '', 
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
      }))
    }

    const related = await getRelatedPosts(id, 4)
    relatedPosts.value = related.map(normalizePost)

    await fetchComments(id)

    const user = authStore.user || authStore.currentUser;
    if (user && (user.accountID || user.id)) {
      const uid = user.accountID || user.id;
      recordHistory(uid, Number(id)).catch(() => { })
    }
  } catch (err) {
    console.warn('PostDetail: load error', err)
  }
}

// --- KIỂM TRA TỪ KHÓA CẤM (GỌI BACKEND ĐỂ BẢO MẬT) ---
const checkContentPolicy = async (text) => {
  if (!text) return false;
  try {
    const res = await axios.post('http://localhost:8080/api/admin/blacklist/check', { content: text });
    return res.data.hasBadWord; 
  } catch (error) {
    console.warn("Lỗi kiểm duyệt nội dung:", error);
    return false;
  }
};

// --- LOGIC API ---
const submitComment = async () => {
  let content = newComment.value.trim()
  
  if (userRating.value > 0 && userRating.value < 3 && !content) {
    toast.warn('Vui lòng cho biết lý do bạn đánh giá dưới 3 sao!');
    return;
  }

  if (!content && selectedPhotos.value.length === 0 && userRating.value === 0) return
  if (!authStore.isAuthenticated) { toast.warn('Vui lòng đăng nhập!'); return }
  
  isUploading.value = true

  // Kiểm tra từ khóa cấm ở Frontend
  const isViolating = await checkContentPolicy(content);
  if (isViolating) {
    toast.error('Bình luận vi phạm tiêu chuẩn cộng đồng!');
    isUploading.value = false;
    return;
  }

  try {
    const accountID = authStore.user.accountID || authStore.user.id; 
    const postID = post.value?.postID || post.value?.id;

    const imageUrls = []
    if (selectedPhotos.value.length > 0) {
      for (const item of selectedPhotos.value) { imageUrls.push(await uploadMedia(item.file, 'comments')) }
    }

    const payload = {
      postID: postID, accountID: accountID, content: content,
      rating: (userRating.value > 0 && !hasUserRated.value) ? userRating.value : null,
      imageUrls: imageUrls
    };

    await axios.post('http://localhost:8080/api/comments', payload);
    clearForm(); 
    await fetchComments(postID); 
    toast.success('Đã gửi đánh giá thành công!');
  } catch (err) { 
    const msg = err.response?.data?.message || 'Gửi đánh giá thất bại!';
    toast.error(msg);
  } finally { isUploading.value = false }
}

const handleSubmitReply = async ({ parentId, content }) => {
  let textContent = content;
  if (!textContent || !textContent.trim()) return
  if (!authStore.isAuthenticated) { toast.warn('Vui lòng đăng nhập!'); return }

  // Kiểm tra từ khóa cấm cho các Phản hồi (Reply)
  const isViolating = await checkContentPolicy(textContent);
  if (isViolating) {
    toast.error('Nội dung vi phạm tiêu chuẩn cộng đồng!');
    return;
  }

  try {
    const payload = {
      postID: post.value?.postID || post.value?.id, accountID: authStore.user.accountID || authStore.user.id,
      content: textContent.trim(), cmtid: parentId, rating: null, imageUrls: []
    };
    await axios.post('http://localhost:8080/api/comments', payload);
    await fetchComments(payload.postID); 
    toast.success('Đã gửi câu trả lời!');
  } catch (err) { 
    const msg = err.response?.data?.message || 'Lỗi khi gửi câu trả lời!';
    toast.error(msg);
  }
}

const handleDeleteComment = async (commentId) => {
  if (!authStore.isAuthenticated) { toast.warn('Vui lòng đăng nhập!'); return; }
  try {
    // Lấy ID của người dùng đang đăng nhập
    const userId = authStore.user.accountID || authStore.user.id;
    const isAdmin = authStore.user.isAdmin || authStore.user.role === 'ADMIN' || authStore.user.role === 'admin';
    
    // Tìm bình luận mục tiêu
    const targetComment = rawCommentsList.value.find(c => (c.commentID || c.id) === commentId);
    const isOwnComment = targetComment && (targetComment.accountID === userId || targetComment.userId === userId);

    if (isAdmin && !isOwnComment) {
      // Admin xóa bình luận của người khác -> Gọi API Admin và ẩn nội dung
      await axios.delete(`http://localhost:8080/api/comments/${commentId}`);
      if (targetComment) {
        targetComment.content = "Đã ẩn do vi phạm quy tắc cộng đồng";
        targetComment.imageUrls = []; // Ẩn luôn hình ảnh vi phạm (nếu có)
      }
      toast.success('Đã ẩn bình luận vi phạm!');
    } else {
      // 🔥 Gọi API dành cho User tự xóa
      await axios.delete(`http://localhost:8080/api/comments/user/${commentId}?currentAccountID=${userId}`);
      
      // Xóa khỏi giao diện
      rawCommentsList.value = rawCommentsList.value.filter(c => (c.commentID || c.id) !== commentId);
      toast.success('Đã xóa bình luận!');
    }
  } catch (err) { 
    toast.error(err.response?.data?.message || 'Lỗi khi xóa bình luận!'); 
  }
}

const handleToggleLikeComment = async (comment) => {
  if (!authStore.isAuthenticated) { toast.warn('Vui lòng đăng nhập để thả tim!'); return; }
  const accountID = authStore.user.accountID || authStore.user.id;
  const commentID = comment.commentID || comment.id;

  const targetComment = rawCommentsList.value.find(c => (c.commentID || c.id) === commentID);
  if (!targetComment) return;

  const previousIsLiked = targetComment.isLiked;
  const previousLikesCount = targetComment.likes || 0;

  targetComment.isLiked = !previousIsLiked;
  targetComment.likes = targetComment.isLiked ? previousLikesCount + 1 : Math.max(0, previousLikesCount - 1);
  comment.isLiked = targetComment.isLiked; comment.likes = targetComment.likes;

  try {
    await axios.post(`http://localhost:8080/api/comments/${commentID}/like`, { accountID: accountID });
  } catch (err) {
    targetComment.isLiked = previousIsLiked; targetComment.likes = previousLikesCount;
    comment.isLiked = previousIsLiked; comment.likes = previousLikesCount;
    toast.error('Máy chủ đang bận, bạn thử lại sau nhé!');
  }
}

watch(() => route.params.id, (id) => { 
  if (id) { 
    post.value = null; 
    loadPost(id); 
    if (!window.location.hash) {
      window.scrollTo({ top: 0, behavior: 'smooth' }) 
    }
  } 
}, { immediate: true })

onMounted(() => {
  const targetHash = route.hash || window.location.hash;
  if (targetHash && !targetHash.startsWith('#comment-')) {
    const element = document.querySelector(targetHash)
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' })
    }
  }
})

const goToDetail = (id) => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
  router.push({ name: 'PostDetail', params: { id: id } })
}
</script>

<style scoped lang="scss">
/* --- LAYOUT CHÍNH --- */
.post-page-container {
  background-color: #ffffff;
  min-height: 100vh;
  font-family: var(--font-ui, 'Mulish', sans-serif);
  width: 100%;
  max-width: 100vw;
  overflow-x: hidden;
  padding-bottom: 120px; 
}


/* --- TÁC GIẢ BUNG XÕA FULL WIDTH (ĐÃ FIX MÀU LUXURY) --- */
.author-full-width-section {
  width: 100%;
  background-color: #ffffff; /* 🔥 Đổi về trắng tinh khôi cho đồng bộ */
  padding: 40px 0; /* Thu gọn lại cho đỡ trống trải */
  border-top: 1px solid rgba(0, 0, 0, 0.04); /* Viền mờ tinh tế */
  border-bottom: 1px solid rgba(0, 0, 0, 0.04); 
  margin: 20px 0; 
  
  /* BÙA CHỐNG TRÔI: Giữ nguyên để không đè layout */
  clear: both;
  display: block;
  position: relative;
  z-index: 10;
}
.global-container { max-width: 1240px; margin: 0 auto; padding: 0 24px; }

/* --- KHU VỰC BOTTOM (2/3 - 1/3) --- */
.bottom-split-section { width: 100%; background-color: #ffffff; padding: 60px 0 100px; }
.split-container-inner { max-width: 1240px; margin: 0 auto; padding: 0 24px; display: grid; grid-template-columns: 2fr 1fr; gap: 80px; align-items: start; }
.related-sidebar-col { position: relative; }
.sticky-sidebar { position: sticky; top: 120px; transition: top 0.3s ease; }

/* =========================================
   CỘT TRÁI: FORM ĐÁNH GIÁ & BÌNH LUẬN 
========================================= */
.interaction-col { min-width: 0; display: flex; flex-direction: column; }

/* HEADER TITLE */
.reviews-header { margin-bottom: 32px; }
.section-title { 
  font-family: var(--font-serif, 'Playfair Display', serif); 
  font-size: 2.2rem; color: #0f172a; 
  margin: 0 0 8px 0; font-weight: 900; letter-spacing: -0.5px;
}
.section-subtitle {
  color: #64748b; font-size: 1.05rem; font-weight: 500; margin: 0;
}

/* --- FORM NHẬP BÌNH LUẬN LUXURY --- */
.review-input-luxury {
  background: #ffffff;
  border: 1px solid rgba(234, 88, 12, 0.15); /* Viền cam siêu nhạt */
  border-radius: 24px;
  padding: 28px;
  margin: 40px 0 48px 0;
  box-shadow: 0 10px 30px -10px rgba(234, 88, 12, 0.05);
  transition: all 0.3s ease;
  
  &:focus-within { 
    border-color: rgba(234, 88, 12, 0.4); 
    box-shadow: 0 15px 35px -10px rgba(234, 88, 12, 0.1);
  }
}

/* User & Rating */
.input-header { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; }
.current-user-avt { width: 48px; height: 48px; border-radius: 50%; object-fit: cover; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }

.rating-selector { 
  display: flex; flex-direction: column; gap: 4px; 
  .prompt-text { font-weight: 800; color: #1e293b; font-size: 0.95rem; } 
  &.locked .prompt-text { color: #64748b; font-weight: 600; }
}

.star-rating-input { 
  display: flex; gap: 6px; 
  span { color: #e2e8f0; cursor: pointer; transition: 0.2s; 
    &.active, &:hover { color: #f59e0b; filter: drop-shadow(0 2px 4px rgba(245, 158, 11, 0.3)); } 
  } 
}
.star-rating-locked { display: flex; gap: 6px; span { color: #f1f5f9; cursor: default; &.filled { color: #f59e0b; } } }

/* Textarea & Preview */
.textarea-box { flex: 1; }
.textarea-box textarea { 
  width: 100%; min-height: 80px; border: none; outline: none; padding: 12px 0; 
  font-size: 1.05rem; resize: none; background: transparent; font-family: inherit; 
  color: #0f172a; line-height: 1.5; font-weight: 500;
  &::placeholder { color: #94a3b8; font-weight: 400; } 
}

.comment-image-previews { display: flex; flex-wrap: wrap; gap: 12px; margin-bottom: 20px; }
.preview-item { 
  position: relative; width: 80px; height: 80px; border-radius: 12px; overflow: hidden; border: 1px solid #e2e8f0; box-shadow: 0 4px 10px rgba(0,0,0,0.05);
  img { width: 100%; height: 100%; object-fit: cover; } 
  .remove-btn { 
    position: absolute; top: 4px; right: 4px; width: 22px; height: 22px; 
    background: rgba(15, 23, 42, 0.7); color: #fff; border: none; border-radius: 50%; 
    display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.2s; backdrop-filter: blur(4px);
    &:hover { background: #ef4444; transform: scale(1.1); } 
  } 
}

/* Footer & Buttons */
.input-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px dashed #e2e8f0; padding-top: 20px; }
.comment-upload-btn { 
  color: #64748b; cursor: pointer; padding: 8px 16px; border-radius: 100px; 
  display: flex; align-items: center; gap: 8px; font-weight: 700; font-size: 0.9rem; transition: 0.2s; background: #f8fafc;
  &:hover { background: #fff4ed; color: #ea580c; } 
}

.action-buttons { display: flex; gap: 12px; align-items: center; }
.btn-cancel-review { background: transparent; color: #64748b; border: none; font-weight: 700; cursor: pointer; padding: 10px 20px; border-radius: 100px; transition: 0.2s; font-size: 0.95rem; &:hover { background: #f1f5f9; color: #0f172a; } }
.btn-submit-review { 
  background: linear-gradient(135deg, #ea580c, #f59e0b); color: #fff; border: none; padding: 10px 28px; 
  border-radius: 100px; font-weight: 800; cursor: pointer; transition: 0.3s; display: flex; align-items: center; gap: 8px; font-size: 0.95rem;
  box-shadow: 0 8px 20px -6px rgba(234, 88, 12, 0.4);
  &:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 12px 25px -6px rgba(234, 88, 12, 0.5); } 
  &:disabled { background: #e2e8f0; box-shadow: none; color: #94a3b8; cursor: not-allowed; } 
}
.spinner-icon { animation: spin 1s linear infinite; }

/* --- ANIMATION TỔNG --- */
.post-content-wrapper { opacity: 0; animation: fadeIn 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes spin { 100% { transform: rotate(360deg); } }

/* --- SKELETON LUXURY --- */
.luxury-loading { width: 100%; padding: 80px 0; background: #fff; }
.sk-container-inner { max-width: 1240px; margin: 0 auto; padding: 0 24px; display: grid; grid-template-columns: 1.2fr 0.8fr; gap: 60px; align-items: center; }
.sk-hero-col { display: flex; flex-direction: column; gap: 24px; }
.sk-badge { width: 100px; height: 28px; border-radius: 100px; background: #f1f5f9; position: relative; overflow: hidden; }
.sk-title { width: 90%; height: 50px; border-radius: 16px; background: #f1f5f9; position: relative; overflow: hidden; &.short { width: 60%; } }
.sk-desc-group { display: flex; flex-direction: column; gap: 12px; margin-top: 10px; }
.sk-desc { height: 14px; border-radius: 8px; background: #f1f5f9; position: relative; overflow: hidden; &.w-100 { width: 100%; } &.w-80 { width: 80%; } &.w-60 { width: 60%; } }
.sk-meta-row { display: flex; align-items: center; gap: 16px; margin-top: 24px; }
.sk-avatar { width: 48px; height: 48px; border-radius: 50%; background: #f1f5f9; position: relative; overflow: hidden; }
.sk-meta-text { width: 120px; height: 16px; border-radius: 6px; background: #f1f5f9; position: relative; overflow: hidden; }

.sk-image-col { width: 100%; }
.sk-img-box { width: 100%; height: 600px; border-radius: 32px; background: #f1f5f9; position: relative; overflow: hidden; }

.sk-badge::after, .sk-title::after, .sk-desc::after, .sk-avatar::after, .sk-meta-text::after, .sk-img-box::after {
  content: ''; position: absolute; inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.8), transparent);
  animation: shimmer 1.5s infinite;
}
@keyframes shimmer { 0% { transform: translateX(-100%); } 100% { transform: translateX(100%); } }

@media (max-width: 1024px) {
  .split-container-inner { grid-template-columns: 1fr; gap: 60px; }
  .sticky-sidebar { position: static; } 
  .sk-container-inner { grid-template-columns: 1fr; gap: 40px; }
  .sk-img-box { height: 400px; }
  .review-input-luxury { padding: 20px; margin-top: 24px; }
  .input-header { flex-direction: column; align-items: flex-start; }
  .action-buttons { width: 100%; flex-direction: column; }
  .btn-submit-review, .btn-cancel-review { width: 100%; justify-content: center; }
}
</style>