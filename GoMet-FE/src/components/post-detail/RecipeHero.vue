<template>
  <section class="hero-section-full-bleed fade-in-up">
    <div class="hero-container-inner">
      <div class="hero-info-col">
        <div class="top-nav-bar">
          <div class="nav-left">
            <button @click="router.push('/home')" class="btn-back-simple">
              <div class="icon-circle"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 12H5M12 19l-7-7 7-7"/></svg></div>
              <span>{{ $t('recipe.back') }}</span>
            </button>
            <span class="sep">/</span>
            <span class="category-label">{{ post.category }}</span>
          </div>

          <button 
            class="btn-report-minimal" 
            :title="$t('common.report')"
            @click="openReportModal"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"></path><line x1="4" y1="22" x2="4" y2="15"></line></svg>
          </button>
        </div>

        <h1 class="recipe-title-main">{{ post.title }}</h1>
        <p class="recipe-desc-main">{{ post.description }}</p>

        <div class="recipe-meta-row">
          <div class="meta-box"><span class="icon">⏱️</span><div class="meta-detail"><span class="label">{{ $t('recipe.time_label') }}</span><span class="val">{{ post.time }}</span></div></div>
          <div class="meta-divider"></div>
          <div class="meta-box"><span class="icon"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/></svg></span><div class="meta-detail"><span class="label">{{ $t('recipe.difficulty_label') }}</span><span class="val">{{ post.difficulty }}</span></div></div>
          <div class="meta-divider"></div>
          <div class="meta-box"><span class="icon"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 22a9 9 0 1 1 18 0"/><path d="M12 13a5 5 0 0 0 5-5A5 5 0 0 0 9.8 4.2"/><path d="M12 13a5 5 0 0 1-3.5-1.5"/></svg></span><div class="meta-detail"><span class="label">{{ $t('recipe.servings_label') }}</span><span class="val">{{ post.servings }}</span></div></div>
        </div>

        <div class="author-action-row">
          <div class="author-block">
            <img :src="post.authorAvatar" class="auth-img">
            <div class="auth-text"><span class="label">{{ $t('recipe.by') }}</span><span class="name">{{ post.author }}</span></div>
            <button class="btn-follow-sm">{{ $t('common.follow') }}</button>
          </div>
          <div class="action-group">
            <button class="btn-save-primary" @click="toggleFavorite">
              <svg width="20" height="20" viewBox="0 0 24 24" :fill="isFavorite ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path>
              </svg>
              <span>{{ isFavorite ? $t('common.saved') : $t('common.save') }}</span>
            </button>
            <button class="btn-share-circle"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="5" r="3"></circle><circle cx="6" cy="12" r="3"></circle><circle cx="18" cy="19" r="3"></circle><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"></line><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"></line></svg></button>
          </div>
        </div>
      </div>

      <div class="hero-image-col">
        <div class="image-frame-hero">
          <img :src="post.image" :alt="post.title" class="img-hero-cover">
          <div class="floating-stats"><span>📅 {{ post.publishDate || '06/02/2026' }}</span><span class="sep">•</span><span>👁️ {{ post.views || '1.2k' }} {{ $t('recipe.views_suffix') }}</span></div>
        </div>
      </div>
    </div>

    <FeedbackModal 
      v-if="showReportModal" 
      :form="reportForm" 
      @close="showReportModal = false" 
    />
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import { addFavorite, removeFavorite, checkFavorite } from '@/services/socialService'
// 1. Import FeedbackModal
import FeedbackModal from '@/components/modals/FeedbackModal.vue'

const props = defineProps({ post: { type: Object, required: true } })
const router = useRouter()
const authStore = useAuthStore()

// 2. Logic điều khiển Report Modal
const showReportModal = ref(false)
const reportForm = ref({
  ticketType: 'REPORT',
  title: '',
  description: '',
  attachment: null,
  targetPostID: props.post.PostID // Gán ID bài viết hiện tại vào đây
})

const openReportModal = () => {
  if (!authStore.isAuthenticated) {
    toast.warn('Đăng nhập để báo cáo bài viết vi phạm sếp nhé!')
    return
  }

  // 🔥 CẬP NHẬT ID NGAY TẠI ĐÂY (Thử mọi trường hợp tên biến)
  reportForm.value.targetPostID = props.post.postID || props.post.id || props.post.PostID;
  
  console.log("ID bài viết gửi đi:", reportForm.value.targetPostID); // Sếp check log ở F12 xem có ID chưa nhé

  showReportModal.value = true
}

// --- GIỮ NGUYÊN CODE CŨ CỦA SẾP ---
const isFavorite = ref(false)

const checkFavoriteStatus = async () => {
  if (!authStore.isAuthenticated) return
  try {
    const res = await checkFavorite(authStore.user.accountID, props.post.id)
    isFavorite.value = res
  } catch (e) { console.log("Check favorite error", e) }
}

const toggleFavorite = async () => {
  if (!authStore.isAuthenticated) {
    toast.warn('Đăng nhập để lưu công thức nhé!')
    return
  }
  try {
    if (isFavorite.value) {
      await removeFavorite(authStore.user.accountID, props.post.id)
      isFavorite.value = false
      toast.success('Đã bỏ lưu công thức')
    } else {
      await addFavorite(authStore.user.accountID, props.post.id)
      isFavorite.value = true
      toast.success('Đã lưu công thức')
    }
  } catch (e) {
    toast.error('Lỗi hệ thống')
  }
}

onMounted(() => checkFavoriteStatus())
</script>

<style scoped>
/* ================= HERO SECTION CSS ================= */
.hero-section-full-bleed { width: calc(100% + 80px); margin-left: -40px; margin-right: -40px; margin-top: -40px; padding: 60px 40px 60px; background-color: #fff; border-bottom: 1px solid #F3F4F6; position: relative; z-index: 1; }
.hero-container-inner { max-width: 1200px; margin: 0 auto; display: grid; grid-template-columns: 1fr 1fr; gap: 60px; align-items: center; }
.top-nav-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.nav-left { display: flex; align-items: center; gap: 15px; }
.btn-back-simple { display: flex; align-items: center; gap: 8px; background: none; border: none; color: #6B7280; font-weight: 700; cursor: pointer; transition: 0.2s; font-size: 0.95rem; }
.btn-back-simple .icon-circle { width: 32px; height: 32px; border-radius: 50%; background: #F3F4F6; display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.btn-back-simple:hover { color: #EA580C; } .btn-back-simple:hover .icon-circle { background: #EA580C; color: white; }
.nav-left .sep { color: #E5E7EB; }
.category-label { font-size: 0.75rem; font-weight: 800; color: #EA580C; text-transform: uppercase; letter-spacing: 1px; }
.btn-report-minimal { width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; border: 1px solid #E5E7EB; border-radius: 50%; background: white; color: #9CA3AF; cursor: pointer; transition: 0.3s; }
.btn-report-minimal:hover { border-color: #EF4444; color: #EF4444; background: #FEF2F2; }
.recipe-title-main { font-family: 'Playfair Display', serif; font-size: 3.5rem; line-height: 1.1; color: #111827; margin-bottom: 16px; }
.recipe-desc-main { font-size: 1.1rem; line-height: 1.6; color: #4B5563; margin-bottom: 32px; }
.recipe-meta-row { display: flex; align-items: center; gap: 24px; margin-bottom: 32px; }
.meta-box { display: flex; align-items: center; gap: 10px; } .meta-box .icon { font-size: 1.4rem; }
.meta-detail { display: flex; flex-direction: column; } .meta-detail .label { font-size: 0.65rem; font-weight: 700; color: #9CA3AF; letter-spacing: 0.5px; } .meta-detail .val { font-size: 1rem; font-weight: 700; color: #1F2937; }
.meta-divider { width: 1px; height: 32px; background: #E5E7EB; }
.author-action-row { display: flex; justify-content: space-between; align-items: center; }
.author-block { display: flex; align-items: center; gap: 12px; } .auth-img { width: 44px; height: 44px; border-radius: 50%; object-fit: cover; }
.auth-text { display: flex; flex-direction: column; } .auth-text .label { font-size: 0.7rem; color: #6B7280; } .auth-text .name { font-weight: 700; color: #111827; }
.btn-follow-sm { background: #1F2937; color: white; border: none; padding: 6px 16px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; margin-left: 12px; cursor: pointer; transition: 0.2s; } .btn-follow-sm:hover { background: #EA580C; }
.action-group { display: flex; gap: 10px; }
.btn-save-primary { background: linear-gradient(135deg, #EA580C, #C2410C); color: white; border: none; padding: 12px 28px; border-radius: 12px; font-weight: 700; display: flex; align-items: center; gap: 8px; cursor: pointer; transition: 0.3s; box-shadow: 0 4px 15px rgba(234, 88, 12, 0.3); } .btn-save-primary:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(234, 88, 12, 0.4); }
.btn-share-circle { width: 44px; height: 44px; border: 1px solid #E5E7EB; border-radius: 50%; background: white; color: #6B7280; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.2s; } .btn-share-circle:hover { border-color: #1F2937; color: #1F2937; }
.image-frame-hero { position: relative; height: 520px; border-radius: 32px; overflow: hidden; box-shadow: 20px 30px 60px -10px rgba(0,0,0,0.15); transform: perspective(1000px) rotateY(-5deg); transition: 0.5s; }
.image-frame-hero:hover { transform: perspective(1000px) rotateY(0deg); }
.img-hero-cover { width: 100%; height: 100%; object-fit: cover; }
.floating-stats { position: absolute; bottom: 20px; left: 50%; transform: translateX(-50%); background: rgba(255,255,255,0.9); backdrop-filter: blur(10px); padding: 8px 24px; border-radius: 30px; display: flex; align-items: center; gap: 12px; font-size: 0.85rem; font-weight: 700; color: #374151; box-shadow: 0 10px 20px rgba(0,0,0,0.1); white-space: nowrap; } .floating-stats .sep { color: #D1D5DB; }

.fade-in-up { animation: fadeInUp 0.8s ease-out; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
</style>