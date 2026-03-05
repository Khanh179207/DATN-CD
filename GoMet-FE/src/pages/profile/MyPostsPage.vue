<template>
  <div class="my-posts-page">
    <div class="page-header">
      <h2 class="page-title">
        <BookOpen :size="20" style="vertical-align:middle; margin-right:6px" />
        My Posts
      </h2>
      <router-link to="/create-post" class="btn-create">
        <Plus :size="15" /> New Post
      </router-link>
    </div>

    <!-- Status tabs -->
    <div class="tabs-row">
      <button
        v-for="tab in TABS" :key="tab.status"
        :class="['tab-btn', activeStatus === tab.status ? 'active' : '']"
        @click="switchTab(tab.status)"
      >
        {{ tab.label }}
        <span v-if="counts[tab.status]" class="tab-count">{{ counts[tab.status] }}</span>
      </button>
    </div>

    <!-- Content -->
    <div v-if="loading" class="center-msg">
      <Loader2 :size="20" class="spin" /> Loading your posts…
    </div>
    <div v-else-if="error" class="center-msg error">{{ error }}</div>
    <div v-else-if="!posts.length" class="empty-state">
      <FilePlus :size="40" class="empty-icon" />
      <p>No posts here yet.</p>
      <router-link v-if="activeStatus === 'ALL'" to="/create-post" class="btn-create small">
        Create your first post
      </router-link>
    </div>

    <div v-else class="posts-grid">
      <div v-for="post in posts" :key="post.postID" class="post-card">
        <!-- Thumbnail -->
        <div class="card-media">
          <img v-if="post.imageUrl" :src="post.imageUrl" :alt="post.title" class="card-img" />
          <div v-else class="card-no-img"><ImageOff :size="24" /></div>

          <StatusBadge :status="post.status" class="card-badge" />
        </div>

        <!-- Body -->
        <div class="card-body">
          <h3 class="card-title">{{ post.title }}</h3>
          <p v-if="post.description" class="card-desc">{{ post.description }}</p>

          <!-- Stats row -->
          <div class="card-stats">
            <span title="Rating"><Star :size="13" /> {{ post.avgRating?.toFixed(1) ?? '—' }}</span>
            <span title="Comments"><MessageSquare :size="13" /> {{ post.commentCount ?? 0 }}</span>
            <span title="Favourites"><Heart :size="13" /> {{ post.favoriteCount ?? 0 }}</span>
          </div>

          <!-- Rejection notice -->
          <div v-if="post.status === 'REJECTED'" class="rejection-notice">
            <XCircle :size="14" />
            <div>
              <strong>{{ post.rejectionCode ?? 'Rejected' }}</strong>
              <p v-if="post.rejectionReason">{{ post.rejectionReason }}</p>
            </div>
          </div>

          <!-- Pending notice -->
          <div v-if="post.status === 'PENDING_REVIEW'" class="pending-notice">
            <Clock :size="14" />
            <span>Under review — visible to you only until approved.</span>
          </div>

          <!-- Hidden notice -->
          <div v-if="post.status === 'HIDDEN'" class="hidden-notice">
            <EyeOff :size="14" />
            <span>This post has been hidden by a moderator.</span>
          </div>

          <!-- Date -->
          <p class="card-date">{{ formatDate(post.createdAt) }}</p>

          <!-- Actions -->
          <div class="card-actions">
            <router-link
              v-if="post.status === 'APPROVED'"
              :to="`/post/${post.postID}`"
              class="link-view"
            >
              View post →
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '@/services/api'
import StatusBadge from '@/components/moderation/StatusBadge.vue'
import {
  BookOpen, Plus, Loader2, FilePlus, ImageOff,
  Star, MessageSquare, Heart, XCircle, Clock, EyeOff
} from 'lucide-vue-next'

// ─── State ────────────────────────────────────────────────────────────────────
const allPosts    = ref([])
const loading     = ref(false)
const error       = ref(null)
const activeStatus = ref('ALL')

const TABS = [
  { status: 'ALL',            label: 'All' },
  { status: 'PENDING_REVIEW', label: 'Pending' },
  { status: 'APPROVED',       label: 'Approved' },
  { status: 'REJECTED',       label: 'Rejected' },
  { status: 'HIDDEN',         label: 'Hidden' },
]

// ─── Computed ─────────────────────────────────────────────────────────────────
const posts = computed(() =>
  activeStatus.value === 'ALL'
    ? allPosts.value
    : allPosts.value.filter(p => p.status === activeStatus.value)
)

const counts = computed(() => {
  const c = {}
  for (const p of allPosts.value) {
    c[p.status] = (c[p.status] ?? 0) + 1
  }
  return c
})

// ─── Load ─────────────────────────────────────────────────────────────────────
onMounted(loadAll)

async function loadAll() {
  loading.value = true
  error.value   = null
  try {
    const { data } = await api.get('/api/posts/me')
    allPosts.value = data
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load your posts'
  } finally {
    loading.value = false
  }
}

function switchTab(status) {
  activeStatus.value = status
}

function formatDate(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}
</script>

<style scoped>
.my-posts-page { padding: 24px; max-width: 1100px; }

.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.page-title { font-size: 1.2rem; font-weight: 700; margin: 0; color: #111; }
.btn-create {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 8px 16px; background: #2563eb; color: #fff;
  border-radius: 8px; font-size: .85rem; font-weight: 600;
  text-decoration: none; border: none; cursor: pointer;
}
.btn-create:hover { background: #1d4ed8; }
.btn-create.small { margin-top: 12px; }

/* ─── Tabs ────────────────────────────────────────────────────────────────── */
.tabs-row { display: flex; gap: 6px; margin-bottom: 20px; flex-wrap: wrap; }
.tab-btn {
  padding: 6px 14px; border-radius: 8px; border: 1px solid #e5e7eb;
  background: #fff; color: #374151; cursor: pointer; font-size: .85rem;
  display: flex; align-items: center; gap: 6px;
}
.tab-btn.active { background: #2563eb; color: #fff; border-color: #2563eb; font-weight: 700; }
.tab-count {
  background: rgba(255,255,255,.25); border-radius: 12px;
  padding: 0 6px; font-size: .72rem; font-weight: 700;
}
.tab-btn:not(.active) .tab-count { background: #e5e7eb; color: #374151; }

/* ─── Grid ────────────────────────────────────────────────────────────────── */
.posts-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 18px; }

.post-card {
  border: 1px solid #e5e7eb; border-radius: 12px;
  overflow: hidden; background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,.06);
  transition: box-shadow .2s;
}
.post-card:hover { box-shadow: 0 4px 18px rgba(0,0,0,.12); }

.card-media { position: relative; height: 160px; overflow: hidden; background: #f3f4f6; }
.card-img { width: 100%; height: 100%; object-fit: cover; }
.card-no-img { display: flex; align-items: center; justify-content: center; height: 100%; color: #d1d5db; }
.card-badge { position: absolute; top: 8px; right: 8px; }

.card-body { padding: 14px; display: flex; flex-direction: column; gap: 6px; }
.card-title { font-size: .95rem; font-weight: 700; color: #111; margin: 0; }
.card-desc { font-size: .82rem; color: #6b7280; margin: 0; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.card-stats { display: flex; gap: 12px; font-size: .78rem; color: #6b7280; }
.card-stats span { display: flex; align-items: center; gap: 3px; }
.card-date { font-size: .75rem; color: #9ca3af; margin: 0; }
.card-actions { display: flex; gap: 8px; flex-wrap: wrap; }
.link-view { font-size: .82rem; color: #2563eb; text-decoration: none; font-weight: 600; }
.link-view:hover { text-decoration: underline; }

/* ─── Status notices ──────────────────────────────────────────────────────── */
.rejection-notice, .pending-notice, .hidden-notice {
  display: flex; gap: 8px; align-items: flex-start;
  padding: 8px 10px; border-radius: 8px; font-size: .8rem;
}
.rejection-notice { background: #fee2e2; color: #991b1b; }
.rejection-notice p { margin: 2px 0 0; }
.pending-notice  { background: #fef3c7; color: #92400e; }
.hidden-notice   { background: #f3f4f6; color: #374151; }

/* ─── Misc ────────────────────────────────────────────────────────────────── */
.center-msg { display: flex; align-items: center; gap: 8px; padding: 40px 16px; justify-content: center; color: #6b7280; }
.center-msg.error { color: #dc2626; }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 60px 16px; color: #9ca3af; gap: 8px; }
.empty-icon { color: #d1d5db; }
.spin { animation: spin .8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>
