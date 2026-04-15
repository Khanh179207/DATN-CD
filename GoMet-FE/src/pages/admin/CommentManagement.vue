<template>
  <div class="comment-sovereign-wrapper">
    
    <div class="page-header-lux">
      <div>
        <h1 class="page-title">{{ t('admin.comments.title') }}</h1>
        <p class="page-subtitle">{{ t('admin.comments.subtitle') }}</p>
      </div>
      <div class="header-actions">
        <router-link to="/admin/blacklist" class="btn-action-lux warning-style">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
          {{ t('admin.posts.blacklist') }}
        </router-link>
        <button class="btn-refresh-lux" @click="loadComments" :disabled="loading">
          <svg :class="{ 'spinning': loading }" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M23 4v6h-6M1 20v-6h6M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>
          {{ loading ? t('admin.comments.refreshing') : t('admin.comments.refresh') }}
        </button>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="icon-wrap all">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
        </div>
        <div class="stat-info">
          <span class="label">{{ t('admin.comments.total_comments') }}</span>
          <h3 class="value">{{ comments.length }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon-wrap feedback">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
        </div>
        <div class="stat-info">
          <span class="label">{{ t('admin.comments.today_comments') }}</span>
          <h3 class="value">{{ todayCommentsCount }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon-wrap bug">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg>
        </div>
        <div class="stat-info">
          <span class="label">{{ t('admin.comments.comments_with_images') }}</span>
          <h3 class="value">{{ attachmentsCount }}</h3>
        </div>
      </div>
    </div>

    <div class="data-engine-lux">
      <div class="filter-bar">
        <div class="filter-right-actions" style="margin-left: auto;">
          <div class="search-box-lux">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
            <input v-model="searchQuery" type="text" :placeholder="t('admin.comments.search_placeholder')">
            <button v-if="searchQuery" @click="searchQuery = ''" class="clear-search" style="background: none; border: none; cursor: pointer; color: #94a3b8;">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
          </div>
        </div>
      </div>

      <div class="table-responsive">
        <div v-if="loading" class="loading-state-lux">
          <svg class="spinning" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#ea580c" stroke-width="3"><path d="M21 12a9 9 0 1 1-6.219-8.56"></path></svg>
          <span>{{ t('admin.dashboard.loading') }}</span>
        </div>

        <div v-else-if="error" class="error-banner">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
          <span>{{ error }}</span>
          <button @click="loadComments">{{ t('comment.retry_now') }}</button>
        </div>

        <table v-else class="lux-table">
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
          <TransitionGroup tag="tbody" name="list-slide">
            <tr v-for="(cmt, i) in filteredComments" :key="cmt.commentID" class="lux-row" :class="{ 'row-hidden': cmt.isActive !== 1 }">
              <td class="id-col">{{ i + 1 }}</td>
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
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg> {{ t('admin.comments.locked_badge') }}
                </div>
                
                <div v-if="cmt.isActive === 0" class="badge-hidden user-deleted">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="M16 21v-2a4 4 0 0 0-4-4H5c-1.1 0-2 .9-2 2v2"></path><circle cx="8.5" cy="7" r="4"></circle><line x1="18" y1="8" x2="23" y2="13"></line><line x1="23" y1="8" x2="18" y2="13"></line></svg> {{ t('admin.comments.user_deleted_badge') }}
                </div>
                
                <div v-if="cmt.parentCommentID" class="badge-reply">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="9 14 4 9 9 4"></polyline><path d="M20 20v-7a4 4 0 0 0-4-4H4"></path></svg> {{ t('admin.comments.reply_to', { id: cmt.parentCommentID }) }}
                </div>
                
                <span class="comment-text">
                  {{ cmt.content || (cmt.hasAttachments ? t('admin.comments.has_attachment') : '') }}
                </span>
                
                <div v-if="cmt.hasAttachments" class="badge-img">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg> {{ t('admin.comments.image_attached') }}
                </div>
              </td>
              <td>
                <a class="post-link" :href="`/post/${cmt.postID}#comment-${cmt.commentID}`" target="_blank" :title="t('admin.comments.view_on_post')">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"></path><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"></path></svg>
                  Post #{{ cmt.postID }}
                </a>
              </td>
              <td class="time-cell">{{ formatDate(cmt.createdAt, true) }}</td>
              <td class="text-right">
                <div class="actions">
                  <a :href="`/post/${cmt.postID}#comment-${cmt.commentID}`" target="_blank" class="btn-sm-action view" :title="t('admin.comments.view_on_post')" style="text-decoration: none;">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                  </a>

                  <button v-if="cmt.isActive === 1" class="btn-sm-action delete" 
                          @click="handleDelete(cmt.commentID)" :title="t('admin.comments.hide_comment')">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line></svg>
                  </button>
                  
                  <button v-else class="btn-sm-action restore" 
                          @click="handleRestore(cmt.commentID)" :title="t('admin.comments.restore_comment')">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path><polyline points="3 3 3 8 8 8"></polyline></svg>
                  </button>
                </div>
              </td>
            </tr>
          </TransitionGroup>
        </table>

        <div v-if="!loading && filteredComments.length === 0" class="empty-state-lux">
          <div class="empty-icon">💬</div>
          <h3>{{ t('admin.comments.empty_title') }}</h3>
          <p>{{ t('admin.comments.empty') }}</p>
        </div>
      </div>
    </div>

    <!-- Lightbox Ảnh Kèm Bình Luận -->
    <Teleport to="body">
      <Transition name="fade-glass">
        <div v-if="fullImageModal.show" class="modal-glass-backdrop" @click="fullImageModal.show = false">
          <button class="btn-close-lightbox" @click.stop="fullImageModal.show = false">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
          <img :src="fullImageModal.url" class="lightbox-img" @click.stop>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import api from '@/services/api.js'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const { t, locale } = useI18n()

const comments = ref([])
const loading = ref(true)
const error = ref(null)
const searchQuery = ref('')

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
    console.error('Comment load failed:', e)
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
    alert(e.response?.data?.message || t('admin.comments.delete_failed'))
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
  const d = new Date(dateStr)
  const options = { day: '2-digit', month: '2-digit', year: 'numeric' }
  const resolvedLocale = locale.value === 'vi' ? 'vi-VN' : 'en-US'
  const date = d.toLocaleDateString(resolvedLocale, options)
  
  if (includeTime) {
    const time = d.toLocaleTimeString(resolvedLocale, { hour: '2-digit', minute: '2-digit' })
    return `${time} - ${date}`
  }
  return date
}

onMounted(loadComments)
</script>

<style scoped lang="scss">
// ==========================================
// 🎨 GOMET ADMIN - COMMENTS (ULTRA LUXURY FULL-WIDTH)
// ==========================================

$orange: #ea580c;
$orange-hover: #c2410c;
$orange-light: #fff7ed;
$orange-gradient: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
$text-main: #0f172a;
$text-sub: #64748b;
$white: #ffffff;
$bg-light: #f8fafc;
$border-soft: rgba(0, 0, 0, 0.05);
$shadow-lux: 0 15px 40px rgba(15, 23, 42, 0.06);

.comment-sovereign-wrapper {
  width: 100%; max-width: 100%; box-sizing: border-box; padding: 25px 35px; overflow-x: hidden; 
  font-family: 'Inter', -apple-system, sans-serif; color: $text-main; min-height: 100vh; 
  background: $bg-light;
  background-image: radial-gradient(circle at 95% 5%, rgba(234, 88, 12, 0.05), transparent 40%), radial-gradient(circle at 5% 95%, rgba(59, 130, 246, 0.02), transparent 30%);

  .page-header-lux {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 35px; 
    .page-title { font-size: 2rem; font-weight: 950; margin: 0; letter-spacing: -1px; background: linear-gradient(to right, #0f172a, #334155); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
    .page-subtitle { color: $text-sub; margin: 6px 0 0; font-weight: 600; font-size: 0.95rem; }
    .header-actions { display: flex; gap: 12px; }
  }
}

.btn-action-lux {
  display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 10px 24px; 
  border: none; border-radius: 14px; font-weight: 800; text-decoration: none; font-size: 0.85rem;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  &.warning-style { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
  &.warning-style:hover { background: #dc2626; color: white; box-shadow: 0 6px 15px rgba(220, 38, 38, 0.3); transform: translateY(-3px); }
}

.btn-refresh-lux {
  display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 10px 24px; 
  background: $orange-gradient; border: none; border-radius: 14px; font-weight: 800; color: $white; cursor: pointer; white-space: nowrap; font-size: 0.85rem;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.25); transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  &:hover:not(:disabled) { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(234, 88, 12, 0.45); }
  .spinning { animation: spin 1s linear infinite; }
  &:disabled { opacity: 0.6; cursor: not-allowed; }
}

.stats-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 25px; margin-bottom: 40px; 
  .stat-card {
    background: $white; padding: 25px; border-radius: 24px; display: flex; align-items: center; gap: 15px;
    border: 1px solid $border-soft; box-shadow: $shadow-lux; transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); position: relative; overflow: hidden;
    &::after { content: ''; position: absolute; bottom: 0; left: 0; width: 100%; height: 4px; background: transparent; transition: 0.3s; }
    &:hover { transform: translateY(-4px); box-shadow: 0 10px 30px rgba(0,0,0,0.05); border-color: $orange; &::after { background: $orange; } }
    
    .icon-wrap { width: 55px; height: 55px; border-radius: 16px; display: flex; align-items: center; justify-content: center; font-size: 1.3rem; transition: 0.3s; flex-shrink: 0;}
    &:hover .icon-wrap { transform: scale(1.1) rotate(-5deg); }
    .icon-wrap.all { background: #eff6ff; color: #3b82f6; }
    .icon-wrap.feedback { background: #d1fae5; color: #10b981; }
    .icon-wrap.bug { background: #ffedd5; color: #f97316; }
    
    .stat-info { .label { font-size: 0.8rem; font-weight: 800; color: $text-sub; text-transform: uppercase; letter-spacing: 0.5px; } .value { font-size: 2rem; font-weight: 950; margin: 4px 0 0; color: $text-main; line-height: 1; } }
  }
}

.data-engine-lux {
  background: $white; border-radius: 30px; border: 1px solid #f1f5f9; box-shadow: $shadow-lux; width: 100%; box-sizing: border-box; overflow: hidden; display: flex; flex-direction: column;
  
  .filter-bar {
    padding: 15px 35px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; background: rgba(255,255,255,0.8);
    
    .filter-right-actions { display: flex; gap: 12px; align-items: center; flex-wrap: wrap;}
    .search-box-lux {
      display: flex; align-items: center; gap: 8px; background: #f8fafc; padding: 10px 15px; border-radius: 14px; border: 2px solid transparent; transition: 0.3s;
      svg { color: #94a3b8; flex-shrink: 0; width: 16px; height: 16px;}
      input { background: transparent; border: none; outline: none; font-weight: 600; font-size: 0.9rem; color: $text-main; width: 250px; transition: 0.3s; }
      &:focus-within { border-color: $orange; background: white; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); svg { color: $orange; } input { width: 300px; } }
    }
  }
  .table-responsive { width: 100%; overflow-x: auto; padding: 0 35px;}
}

.loading-state-lux { padding: 60px; text-align: center; color: $orange; font-weight: 700; display: flex; flex-direction: column; align-items: center; gap: 10px; }

.lux-table {
  width: 100%; min-width: 950px; border-collapse: separate; border-spacing: 0 8px; padding: 10px 0 25px;
  th { padding: 12px 20px; text-align: left; color: #94a3b8; font-size: 0.75rem; text-transform: uppercase; font-weight: 900; letter-spacing: 1px; white-space: nowrap; border-bottom: 2px solid #f1f5f9; }
  .text-right { text-align: right !important; }

  .lux-row {
    background: #ffffff; transition: all 0.3s ease; 
    &:hover { background: #f8fafc; transform: scale(1.002); box-shadow: 0 8px 25px rgba(0,0,0,0.03); }
    td { padding: 15px 20px; border-top: 1px solid #f8fafc; border-bottom: 1px solid #f8fafc;
      &:first-child { border-radius: 16px 0 0 16px; border-left: 1px solid #f8fafc; font-weight: 900; color: #94a3b8; font-size: 0.9rem;} 
      &:last-child { border-radius: 0 16px 16px 0; border-right: 1px solid #f8fafc; } 
    }
    .time-cell { color: #64748b; font-size: 0.85rem; font-weight: 600; font-family: monospace; }
  }
}

/* User Cell Styles */
.user-cell { display: flex; align-items: center; gap: 12px; }
.user-avatar { width: 42px; height: 42px; border-radius: 50%; object-fit: cover; border: 2px solid #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.1); flex-shrink: 0; }
.u-info { display: flex; flex-direction: column; }
.user-name { font-weight: 700; color: #1e293b; font-size: 0.95rem; }
.user-id { font-size: 0.75rem; color: #94a3b8; font-family: monospace; }

/* Comment Content Styles */
.content-cell { max-width: 350px; }
.comment-text { color: #334155; font-size: 0.95rem; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.5; margin-bottom: 6px; font-weight: 500; }
.badge-reply { display: inline-flex; align-items: center; gap: 4px; background: #eff6ff; color: #3b82f6; padding: 4px 8px; border-radius: 6px; font-size: 0.75rem; font-weight: 700; margin-bottom: 6px; }
.badge-img { display: inline-flex; align-items: center; gap: 4px; background: #f1f5f9; color: #475569; padding: 4px 10px; border-radius: 6px; font-size: 0.8rem; font-weight: 600; }

/* Post Link */
.post-link { display: inline-flex; align-items: center; gap: 6px; padding: 6px 12px; border-radius: 8px; background: #fff7ed; color: #ea580c; font-weight: 700; font-size: 0.85rem; text-decoration: none; transition: 0.2s; border: 1px solid transparent; }
.post-link:hover { background: #ffedd5; border-color: #fdba74; }

/* Actions */
.actions { display: flex; justify-content: flex-end; gap: 8px; }
.btn-sm-action { width: 36px; height: 36px; border-radius: 10px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; background: white; border: 1px solid transparent; }
.btn-sm-action.view { background: #f1f5f9; color: #475569; } .btn-sm-action.view:hover { background: #e2e8f0; color: #0f172a; border-color: #cbd5e1; }
.btn-sm-action.delete { background: #ffedd5; color: #ea580c; } .btn-sm-action.delete:hover { background: #ea580c; color: white; }
.btn-sm-action.restore { background: #e0f2fe; color: #0284c7; } .btn-sm-action.restore:hover { background: #0284c7; color: white; }

/* Hidden State */
.row-hidden { opacity: 0.65; background-color: #f8fafc !important; }
.row-hidden .user-avatar { filter: grayscale(1); }
.row-hidden .comment-text { color: #94a3b8; }
.badge-hidden { display: inline-flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 800; margin-bottom: 8px; text-transform: uppercase; }
.admin-banned { background: #fef2f2; color: #ef4444; border: 1px solid #fecaca; }
.user-deleted { background: #f1f5f9; color: #64748b; border: 1px solid #cbd5e1; }

/* Error & Empty */
.error-banner { background: #fef2f2; border: 1px solid #fecaca; padding: 16px 24px; border-radius: 12px; display: flex; gap: 12px; align-items: center; margin-bottom: 20px; color: #dc2626; font-weight: 600; button { margin-left: auto; background: white; border: 1px solid #fca5a5; color: #dc2626; padding: 8px 16px; border-radius: 8px; cursor: pointer; font-weight: 700; transition: 0.2s; } button:hover { background: #dc2626; color: white; } }
.empty-state-lux { padding: 60px 30px; text-align: center; background: white; .empty-icon { font-size: 3.5rem; margin-bottom: 15px; animation: float 3s ease-in-out infinite; } h3 { font-size: 1.3rem; font-weight: 900; color: $text-main; margin: 0 0 8px; } p { color: $text-sub; font-size: 0.9rem; margin: 0 0 25px; font-weight: 500; } }

/* Modals */
.modal-glass-backdrop { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.85); backdrop-filter: blur(12px); z-index: 999999; display: flex; align-items: center; justify-content: center; padding: 20px; cursor: zoom-out; }
.lightbox-img { max-width: 90vw; max-height: 90vh; border-radius: 12px; box-shadow: 0 30px 80px rgba(0,0,0,0.5); object-fit: contain; cursor: default; }
.btn-close-lightbox { position: absolute; top: 20px; right: 20px; background: rgba(255,255,255,0.1); color: white; border: none; width: 44px; height: 44px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.2s; }
.btn-close-lightbox:hover { background: rgba(255,255,255,0.3); transform: rotate(90deg); }

/* Animations */
@keyframes spin { to { transform: rotate(360deg); } }
@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-10px); } }
.fade-glass-enter-active { animation: fadeIn 0.3s ease; .lightbox-img { animation: zoomIn 0.3s cubic-bezier(0.16, 1, 0.3, 1); } }
.fade-glass-leave-active { transition: opacity 0.2s ease; opacity: 0; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes zoomIn { from { opacity: 0; transform: scale(0.9); } to { opacity: 1; transform: scale(1); } }
.list-slide-enter-active, .list-slide-leave-active { transition: all 0.4s ease; }
.list-slide-enter-from { opacity: 0; transform: translateX(-20px); }
.list-slide-leave-to { opacity: 0; transform: translateX(20px); }
</style>