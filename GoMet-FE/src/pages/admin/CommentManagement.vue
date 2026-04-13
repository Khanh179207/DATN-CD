<template>
  <div class="page-container animate-enter">
    
    <div class="page-header anim-fade-down">
      <div class="header-content">
        <div class="title-wrapper">
          <div class="icon-box">
            <i class="fa-solid fa-comments"></i>
          </div>
          <div>
            <h2 class="title">{{ t('admin.comments.title') }}</h2>
            <p class="subtitle">{{ t('admin.comments.subtitle') }}</p>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <div class="search-box-lux">
          <i class="fa-solid fa-search search-icon"></i>
          <input v-model="searchQuery" type="text" :placeholder="t('admin.comments.search_placeholder')" />
          <button v-if="searchQuery" @click="searchQuery = ''" class="clear-search">
            <i class="fa-solid fa-xmark"></i>
          </button>
        </div>
        <router-link to="/admin/blacklist" class="btn-open-blacklist">
          <i class="fa-solid fa-shield-halved"></i> <span>{{ t('admin.blacklist.title') }}</span>
        </router-link>
        <button class="btn-refresh" @click="loadComments" :disabled="loading">
          <i class="fa-solid fa-rotate-right" :class="{ 'fa-spin': loading }"></i> <span>{{ t('admin.comments.refresh') }}</span>
        </button>
      </div>
    </div>

    <div class="stats-grid anim-fade-up">
      <div class="stat-card">
        <div class="stat-icon bg-blue-light"><i class="fa-solid fa-comments text-blue"></i></div>
        <div class="stat-info">
          <span class="stat-value">{{ comments.length }}</span>
          <span class="stat-label">{{ t('admin.comments.total_comments') }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-emerald-light"><i class="fa-solid fa-calendar-day text-emerald"></i></div>
        <div class="stat-info">
          <span class="stat-value">{{ todayCommentsCount }}</span>
          <span class="stat-label">{{ t('admin.comments.today_comments') }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-orange-light"><i class="fa-solid fa-images text-orange"></i></div>
        <div class="stat-info">
          <span class="stat-value">{{ attachmentsCount }}</span>
          <span class="stat-label">{{ t('admin.comments.comments_with_images') }}</span>
        </div>
      </div>
    </div>

    <div v-if="loading" class="skel-wrap table-lux-wrapper">
      <div class="skel-row" v-for="n in 5" :key="n">
        <div class="skel-avatar"></div>
        <div class="skel-lines"><div class="skel-line"></div><div class="skel-line short"></div></div>
      </div>
    </div>

    <div v-else-if="error" class="error-banner">
      <div class="error-content">
        <i class="fa-solid fa-triangle-exclamation"></i>
        <span>{{ error }}</span>
      </div>
      <button class="btn-retry" @click="loadComments">{{ t('admin.common.retry') }}</button>
    </div>

    <div v-else class="table-lux-wrapper anim-fade-up" style="--delay: 0.2s">
      <table class="data-table-lux">
        <thead>
          <tr>
            <th width="5%">#</th>
            <th width="20%">{{ t('admin.comments.user_col') }}</th>
            <th width="35%">{{ t('admin.comments.content_col') }}</th>
            <th width="10%">{{ t('admin.comments.post_col') }}</th>
            <th width="15%">{{ t('admin.comments.time_col') }}</th>
            <th width="15%" class="text-right">{{ t('admin.comments.actions_col') }}</th>
          </tr>
        </thead>
        <TransitionGroup tag="tbody" name="list-anim">
          <tr v-for="(cmt, i) in filteredComments" :key="cmt.commentID" class="table-row-lux" :class="{ 'row-hidden': cmt.isActive !== 1 }">
            <td class="idx">{{ i + 1 }}</td>
            <td>
              <div class="user-cell">
                <img :src="cmt.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(cmt.authorName || 'U')}&background=f8fafc&color=0f172a`" 
                     @error="$event.target.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(cmt.authorName || 'U')}&background=f8fafc&color=0f172a`"
                     class="user-avatar">
                <div class="u-info">
                  <span class="user-name">{{ cmt.authorName || t('admin.comments.anonymous') }}</span>
                  <span class="user-id" v-if="cmt.authorID">ID: {{ cmt.authorID }}</span>
                </div>
              </div>
            </td>
            <td class="content-cell">
              <div v-if="cmt.isActive === -1" class="badge-hidden admin-banned">
                <i class="fa-solid fa-shield-halved"></i> {{ t('admin.comments.locked_badge') }}
              </div>
              
              <div v-if="cmt.isActive === 0" class="badge-hidden user-deleted">
                <i class="fa-solid fa-user-xmark"></i> {{ t('admin.comments.user_deleted_badge') }}
              </div>
              
              <div v-if="cmt.parentCommentID" class="badge-reply">
                <i class="fa-solid fa-reply"></i> {{ t('admin.comments.reply_to', { id: cmt.parentCommentID }) }}
              </div>
              
              <span class="comment-text">
                {{ cmt.content || (cmt.hasAttachments ? t('admin.comments.has_attachment') : '') }}
              </span>
              
              <div v-if="cmt.hasAttachments" class="badge-img">
                <i class="fa-solid fa-image"></i> {{ t('admin.comments.image_attached') }}
              </div>
            </td>
            <td>
              <a class="post-link" :href="`/post/${cmt.postID}#comment-${cmt.commentID}`" target="_blank" :title="t('admin.comments.view_on_post')">
                <i class="fa-solid fa-link"></i> Post #{{ cmt.postID }}
              </a>
            </td>
            <td class="date-cell">{{ formatDate(cmt.createdAt, true) }}</td>
            <td>
              <div class="actions">
                <a :href="`/post/${cmt.postID}#comment-${cmt.commentID}`" target="_blank" class="btn-action view" :title="t('admin.comments.view_on_post')" style="text-decoration: none;">
                  <i class="fa-solid fa-eye"></i>
                </a>

                <button v-if="cmt.isActive === 1" class="btn-action delete" 
                  @click="handleDelete(cmt.commentID)" :title="t('admin.comments.hide_comment')">
                  <i class="fa-solid fa-ban"></i>
                </button>
                
                <button v-else class="btn-action restore" 
                  @click="handleRestore(cmt.commentID)" :title="t('admin.comments.restore_comment')">
                  <i class="fa-solid fa-rotate-left"></i>
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="filteredComments.length === 0">
            <td colspan="6" class="empty-state">
              <div class="empty-icon"><i class="fa-regular fa-comment-dots"></i></div>
              <p>{{ t('admin.comments.empty') }}</p>
            </td>
          </tr>
        </TransitionGroup>
      </table>
    </div>

    <Transition name="modal-fade">
      <div v-if="fullImageModal.show" class="lightbox-overlay" @click="fullImageModal.show = false">
        <button class="btn-close-lightbox"><i class="fa-solid fa-xmark"></i></button>
        <img :src="fullImageModal.url" class="lightbox-img" @click.stop>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api.js'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'
import { formatLocaleDateTime } from '@/i18n'

const authStore = useAuthStore()

const comments = ref([])
const loading = ref(true)
const error = ref(null)
const searchQuery = ref('')
const { t } = useI18n()

const fullImageModal = ref({ show: false, url: '' })

// --- THỐNG KÊ ---
const todayCommentsCount = computed(() => {
  const today = new Date().toDateString()
  return comments.value.filter(c => {
    if (!c.createdAt) return false
    return new Date(c.createdAt).toDateString() === today
  }).length
})
const attachmentsCount = computed(() => comments.value.filter(c => c.hasAttachments).length)

// --- TÌM KIẾM ---
const filteredComments = computed(() => {
  const q = searchQuery.value.toLowerCase().trim()
  if (!q) return comments.value
  return comments.value.filter(c =>
    (c.content || '').toLowerCase().includes(q) ||
    (c.authorName || '').toLowerCase().includes(q)
  )
})

// --- API ACTIONS: LOAD DỮ LIỆU ---
const loadComments = async () => {
  loading.value = true
  error.value = null
  try {
    const res = await api.get('/api/admin/comments')
    comments.value = res.data
  } catch (e) {
    console.error('Lỗi khi tải bình luận:', e)
    error.value = t('admin.comments.load_failed')
  } finally {
    loading.value = false
  }
}

// --- API ACTIONS: ADMIN KHÓA BÌNH LUẬN (-1) ---
const handleDelete = async (id) => {
  if (!confirm(t('admin.comments.delete_confirm'))) return
  try {
    const payload = {
      adminId: authStore.user?.accountID || authStore.user?.id || 0,
      adminName: authStore.user?.username || authStore.user?.fullName || 'Admin'
    }
    // Gọi API xóa mềm bên Backend (Sẽ cập nhật isActive = -1)
    await api.delete(`/api/admin/comments/${id}`, { data: payload })
    
    // Cập nhật lại UI không cần reload trang
    const target = comments.value.find(c => c.commentID === id)
    if (target) target.isActive = -1
  } catch (e) {
    alert(t('admin.comments.delete_failed'))
  }
}

// --- API ACTIONS: KHÔI PHỤC BÌNH LUẬN (1) ---
const handleRestore = async (id) => {
  if (!confirm(t('admin.comments.restore_confirm'))) return
  try {
    const payload = {
      adminId: authStore.user?.accountID || authStore.user?.id || 0,
      adminName: authStore.user?.username || authStore.user?.fullName || 'Admin'
    }
    // Gọi API khôi phục bên Backend (Sẽ cập nhật isActive = 1)
    await api.put(`/api/admin/comments/${id}/restore`, payload)
    
    // Cập nhật lại UI không cần reload trang
    const target = comments.value.find(c => c.commentID === id)
    if (target) target.isActive = 1
  } catch (e) {
    alert(t('admin.comments.restore_failed'))
  }
}

// --- XỬ LÝ MODAL ẢNH ---
const viewFullImage = (url) => {
  fullImageModal.value = { show: true, url }
}

// --- FORMATTER ---
const formatDate = (dateStr, includeTime = true) => {
  if (!dateStr) return '—'
  return formatLocaleDateTime(dateStr, includeTime ? undefined : { day: '2-digit', month: '2-digit', year: 'numeric' })
}

onMounted(loadComments)
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Playfair+Display:wght@800&display=swap');
@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap');

.page-container { padding: 32px 40px; font-family: 'Inter', sans-serif; background-color: #f8fafc; min-height: 100vh; color: #0f172a; }

/* ── HEADER VIPRO ── */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px; }
.header-content { display: flex; align-items: center; }
.title-wrapper { display: flex; align-items: center; gap: 16px; }
.icon-box { width: 52px; height: 52px; border-radius: 14px; background: linear-gradient(135deg, #ea580c, #f59e0b); color: white; display: flex; align-items: center; justify-content: center; box-shadow: 0 10px 20px -5px rgba(234, 88, 12, 0.4); font-size: 1.5rem; }
.title { font-family: 'Playfair Display', serif; font-size: 2.2rem; font-weight: 800; color: #0f172a; margin: 0; letter-spacing: -0.5px; }
.subtitle { color: #64748b; margin: 4px 0 0; font-size: 1rem; font-weight: 500; }

.header-actions { display: flex; align-items: center; gap: 16px; }

.search-box-lux { display: flex; align-items: center; background: white; padding: 12px 20px; border-radius: 100px; border: 1px solid #e2e8f0; width: 320px; box-shadow: 0 4px 15px rgba(0,0,0,0.03); transition: 0.3s; position: relative; }
.search-box-lux:focus-within { border-color: #ea580c; box-shadow: 0 4px 20px rgba(234,88,12,0.1); }
.search-icon { color: #94a3b8; }
.search-box-lux input { border: none; outline: none; margin-left: 12px; width: 100%; font-family: inherit; font-size: 0.95rem; color: #0f172a; background: transparent; }
.clear-search { background: none; border: none; color: #94a3b8; cursor: pointer; display: flex; align-items: center; }

.btn-open-blacklist { display: flex; align-items: center; gap: 8px; background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; padding: 12px 20px; border-radius: 100px; font-weight: 700; text-decoration: none; transition: 0.3s; font-size: 0.95rem; }
.btn-open-blacklist:hover { background: #dc2626; color: white; box-shadow: 0 8px 20px -5px rgba(220, 38, 38, 0.4); }

.btn-refresh { background: white; border: 1px solid #e2e8f0; padding: 12px 20px; border-radius: 100px; font-weight: 700; font-size: 0.95rem; color: #475569; cursor: pointer; transition: 0.3s; display: flex; align-items: center; gap: 8px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); }
.btn-refresh:hover:not(:disabled) { background: #f8fafc; color: #0f172a; border-color: #cbd5e1; }

/* ── THỐNG KÊ ── */
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; margin-bottom: 24px; }
.stat-card { background: white; padding: 20px 24px; border-radius: 20px; display: flex; align-items: center; gap: 20px; border: 1px solid rgba(0,0,0,0.03); box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05); transition: 0.3s; }
.stat-card:hover { transform: translateY(-3px); box-shadow: 0 20px 25px -5px rgba(0,0,0,0.06); }
.stat-icon { width: 54px; height: 54px; border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }
.bg-blue-light { background: #eff6ff; color: #3b82f6; } 
.bg-emerald-light { background: #d1fae5; color: #10b981; } 
.bg-orange-light { background: #ffedd5; color: #f97316; } 
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 1.6rem; font-weight: 800; color: #0f172a; line-height: 1.2; }
.stat-label { font-size: 0.85rem; color: #64748b; font-weight: 600; text-transform: uppercase; margin-top: 4px; }

/* ── BẢNG DỮ LIỆU LUXURY ── */
.table-lux-wrapper { background: white; border-radius: 20px; box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05); border: 1px solid rgba(0,0,0,0.03); overflow: hidden; }
.data-table-lux { width: 100%; border-collapse: separate; border-spacing: 0; }
.data-table-lux th { text-align: left; padding: 18px 24px; background: #f8fafc; color: #64748b; font-weight: 700; font-size: 0.8rem; letter-spacing: 1px; border-bottom: 1px solid #e2e8f0; }
.data-table-lux td { padding: 16px 24px; border-bottom: 1px solid #f1f5f9; vertical-align: top; }
.table-row-lux { transition: 0.2s; } .table-row-lux:hover { background: #fafafa; }

.idx { color: #94a3b8; font-weight: 700; font-size: 0.9rem; }
.user-cell { display: flex; align-items: center; gap: 12px; }
.user-avatar { width: 42px; height: 42px; border-radius: 50%; object-fit: cover; border: 2px solid #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
.u-info { display: flex; flex-direction: column; }
.user-name { font-weight: 600; color: #1e293b; font-size: 0.95rem; }
.user-id { font-size: 0.8rem; color: #94a3b8; font-family: 'JetBrains Mono', monospace; }

.content-cell { max-width: 350px; }
.comment-text { color: #334155; font-size: 0.95rem; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.5; margin-bottom: 6px; }
.badge-reply { display: inline-block; background: #eff6ff; color: #3b82f6; padding: 2px 8px; border-radius: 6px; font-size: 0.75rem; font-weight: 700; margin-bottom: 6px; }
.badge-img { display: inline-block; background: #f1f5f9; color: #475569; padding: 4px 10px; border-radius: 6px; font-size: 0.8rem; font-weight: 600; }

.post-link { display: inline-flex; align-items: center; gap: 6px; padding: 6px 12px; border-radius: 8px; background: #fff7ed; color: #ea580c; font-weight: 700; font-size: 0.85rem; text-decoration: none; transition: 0.2s; }
.post-link:hover { background: #ffedd5; }

.date-cell { color: #475569; font-size: 0.9rem; font-weight: 500; font-family: 'JetBrains Mono', monospace; }

.actions { display: flex; justify-content: flex-end; gap: 8px; }
.btn-action { width: 36px; height: 36px; border-radius: 10px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; background: white; font-size: 1rem; }
.btn-action.view { background: #f1f5f9; color: #475569; } .btn-action.view:hover { background: #e2e8f0; color: #0f172a; }
.btn-action.delete { background: #ffedd5; color: #ea580c; } .btn-action.delete:hover { background: #ea580c; color: white; }

/* ── TRẠNG THÁI XÓA MỀM (MỚI) ── */
.row-hidden { opacity: 0.65; background-color: #f8fafc !important; }
.row-hidden .user-avatar { filter: grayscale(1); }
.row-hidden .comment-text { color: #94a3b8; }

.badge-hidden { display: inline-block; padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 700; margin-bottom: 8px; text-transform: uppercase; }
.admin-banned { background: #fef2f2; color: #ef4444; border: 1px solid #fecaca; }
.user-deleted { background: #f1f5f9; color: #64748b; border: 1px solid #cbd5e1; }

.btn-action.restore { background: #e0f2fe; color: #0284c7; }
.btn-action.restore:hover { background: #0284c7; color: white; }

/* LIGHTBOX XEM ẢNH TO */
.lightbox-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.9); z-index: 9999; display: flex; justify-content: center; align-items: center; cursor: zoom-out; }
.lightbox-img { max-width: 90vw; max-height: 90vh; border-radius: 8px; box-shadow: 0 20px 50px rgba(0,0,0,0.5); }
.btn-close-lightbox { position: absolute; top: 20px; right: 20px; background: rgba(255,255,255,0.1); color: white; border: none; width: 44px; height: 44px; border-radius: 50%; font-size: 1.5rem; cursor: pointer; transition: 0.2s; }
.btn-close-lightbox:hover { background: rgba(255,255,255,0.3); }

/* ── SKELETON & ANIMATIONS ── */
.skel-wrap { padding: 24px; display: flex; flex-direction: column; gap: 16px; }
.skel-row { display: flex; gap: 16px; align-items: center; }
.skel-avatar { width: 42px; height: 42px; border-radius: 50%; background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%); background-size: 200% 100%; animation: shimmer 1.5s infinite; }
.skel-lines { flex: 1; display: flex; flex-direction: column; gap: 10px; }
.skel-line { height: 12px; background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%); background-size: 200% 100%; animation: shimmer 1.5s infinite; border-radius: 6px; }
.skel-line.short { width: 30%; }
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }

.error-banner { background: #fef2f2; border: 1px solid #fecaca; padding: 16px 24px; border-radius: 12px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.error-content { display: flex; align-items: center; gap: 12px; color: #dc2626; font-weight: 600; }
.btn-retry { background: white; border: 1px solid #fca5a5; color: #dc2626; padding: 8px 16px; border-radius: 8px; cursor: pointer; font-weight: 700; transition: 0.2s; }
.btn-retry:hover { background: #dc2626; color: white; }

.empty-state { text-align: center; padding: 60px; color: #64748b; }
.empty-icon { font-size: 3rem; color: #cbd5e1; margin-bottom: 12px; }

/* ANIMATIONS */
.anim-fade-down { animation: fadeDown 0.6s cubic-bezier(0.16, 1, 0.3, 1); }
.anim-fade-up { animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) both; animation-delay: var(--delay, 0s); }
@keyframes fadeDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.animate-enter { animation: fadeUp 0.5s ease-out; }
.modal-fade-enter-active, .modal-fade-leave-active { transition: all 0.2s; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; transform: scale(0.95); }
.list-anim-enter-active, .list-anim-leave-active { transition: all 0.4s ease; }
.list-anim-enter-from, .list-anim-leave-to { opacity: 0; transform: translateX(-20px); }
</style>