<template>
  <div class="inbox-page">
    <!-- Page header -->
    <div class="page-header">
      <div>
        <h2 class="title">
          <ShieldCheck :size="20" style="vertical-align:middle; margin-right:6px" />
          Moderation Inbox
        </h2>
        <p class="subtitle">Review and act on user-submitted content</p>
      </div>
    </div>

    <!-- Status tabs -->
    <div class="tabs-row">
      <button
        v-for="tab in TABS" :key="tab.status"
        :class="['tab-btn', currentStatus === tab.status ? 'active' : '']"
        @click="switchTab(tab.status)"
      >
        {{ tab.label }}
        <span v-if="tab.status === 'PENDING_REVIEW' && pendingBadge > 0" class="badge">{{ pendingBadge }}</span>
      </button>

      <!-- Search -->
      <div class="search-wrap">
        <Search :size="14" />
        <input v-model="keyword" @input="onSearch" placeholder="Search title or author…" />
      </div>
    </div>

    <!-- Body: list + detail panel -->
    <div class="split-body">
      <!-- ── LEFT: Post list ── -->
      <div class="list-col">
        <div v-if="store.loading" class="center-msg">
          <Loader2 :size="18" class="spin" /> Loading…
        </div>
        <div v-else-if="store.error" class="center-msg error">{{ store.error }}</div>
        <div v-else-if="!store.posts.length" class="center-msg muted">No posts in this queue.</div>

        <div
          v-for="post in store.posts" :key="post.postID"
          :class="['post-card', selectedId === post.postID ? 'selected' : '']"
          @click="selectPost(post)"
        >
          <!-- Thumbnail -->
          <img v-if="post.media" :src="post.media" class="thumb" alt="" />
          <div v-else class="thumb-placeholder"><ImageOff :size="18" /></div>

          <div class="post-info">
            <div class="post-top-row">
              <span class="post-title">{{ post.title }}</span>
              <StatusBadge :status="post.status" />
            </div>
            <div class="post-meta">
              <span>by {{ post.authorName }}</span>
              <span v-if="post.categoryName"> · {{ post.categoryName }}</span>
              <span v-if="post.spamScore > 0" class="spam-score" :class="post.spamScore >= 80 ? 'high' : 'med'">
                spam {{ post.spamScore }}
              </span>
            </div>
            <div class="post-date">{{ formatDate(post.createdAt) }}</div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="store.totalPages > 1" class="pagination">
          <button :disabled="store.currentPage === 0" @click="goPage(store.currentPage - 1)">‹ Prev</button>
          <span>{{ store.currentPage + 1 }} / {{ store.totalPages }}</span>
          <button :disabled="store.currentPage + 1 >= store.totalPages" @click="goPage(store.currentPage + 1)">Next ›</button>
        </div>
      </div>

      <!-- ── RIGHT: Detail panel ── -->
      <div v-if="selected" class="detail-col">
        <!-- Post preview -->
        <div class="detail-header">
          <h3 class="detail-title">{{ selected.title }}</h3>
          <StatusBadge :status="selected.status" />
        </div>

        <div v-if="selected.media" class="detail-img-wrap">
          <img :src="selected.media" class="detail-img" alt="" />
        </div>

        <div class="detail-meta">
          <span>Author: <strong>{{ selected.authorName }}</strong></span>
          <span v-if="selected.authorApprovedPostCount !== undefined">
            ({{ selected.authorApprovedPostCount }} approved posts)
          </span>
          <span v-if="selected.categoryName"> · Category: {{ selected.categoryName }}</span>
        </div>

        <p v-if="selected.description" class="detail-desc">{{ selected.description }}</p>

        <!-- Spam info -->
        <div v-if="selected.spamScore > 0" class="spam-panel">
          <span class="spam-label">Spam Score: <strong>{{ selected.spamScore }}</strong>/100</span>
          <span v-if="selected.spamReasonsParsed?.length" class="spam-reasons">
            {{ selected.spamReasonsParsed.join(' · ') }}
          </span>
        </div>

        <!-- Rejection info (if rejected) -->
        <div v-if="selected.status === 'REJECTED' && selected.rejectionCode" class="rejection-panel">
          <strong>Rejection code:</strong> {{ selected.rejectionCode }}
          <p v-if="selected.rejectionReason">{{ selected.rejectionReason }}</p>
        </div>

        <!-- Action buttons -->
        <div class="actions-bar">
          <button v-if="selected.status !== 'APPROVED'" class="btn btn-approve" :disabled="acting" @click="doApprove">
            <Check :size="14" /> Approve
          </button>
          <button v-if="selected.status === 'PENDING_REVIEW' || selected.status === 'APPROVED'" class="btn btn-reject" :disabled="acting" @click="openRejectModal">
            <X :size="14" /> Reject
          </button>
          <button v-if="selected.status === 'APPROVED'" class="btn btn-hide" :disabled="acting" @click="doHide">
            <EyeOff :size="14" /> Hide
          </button>
          <button v-if="selected.status === 'HIDDEN'" class="btn btn-unhide" :disabled="acting" @click="doUnhide">
            <Eye :size="14" /> Unhide
          </button>
          <button v-if="selected.status !== 'HIDDEN'" class="btn btn-spam" :disabled="acting" @click="doFlagSpam">
            <AlertTriangle :size="14" /> Flag Spam
          </button>
        </div>

        <!-- Action timeline -->
        <div class="timeline-section">
          <h4 class="timeline-title">Action History</h4>
          <div v-if="store.actionsLoading" class="center-msg"><Loader2 :size="14" class="spin" /></div>
          <div v-else-if="!store.actions.length" class="center-msg muted">No actions yet.</div>
          <div v-for="act in store.actions" :key="act.id" class="timeline-item">
            <div class="tl-action-badge" :class="`tl-${act.action.toLowerCase()}`">{{ act.action }}</div>
            <div class="tl-detail">
              <span class="tl-admin">{{ act.adminName }}</span>
              <span class="tl-date">{{ formatDate(act.createdAt) }}</span>
              <p v-if="act.reason" class="tl-reason">{{ act.reason }}</p>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="detail-col empty-detail">
        <ShieldCheck :size="48" class="empty-icon" />
        <p>Select a post to review</p>
      </div>
    </div>

    <!-- Reject modal -->
    <RejectModal
      v-model="showRejectModal"
      :post-id="selected?.postID ?? 0"
      :post-title="selected?.title ?? ''"
      @confirmed="onRejectConfirmed"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useModerationStore } from '@/stores/moderation'
import StatusBadge from '@/components/moderation/StatusBadge.vue'
import RejectModal from '@/components/moderation/RejectModal.vue'
import {
  ShieldCheck, Search, Loader2, Check, X, EyeOff, Eye,
  AlertTriangle, ImageOff
} from 'lucide-vue-next'

// ─── Store ────────────────────────────────────────────────────────────────────
const store = useModerationStore()

// ─── UI state ────────────────────────────────────────────────────────────────
const currentStatus = ref('PENDING_REVIEW')
const keyword       = ref('')
const selected      = ref(null)
const selectedId    = ref(null)
const acting        = ref(false)
const showRejectModal = ref(false)
let   searchTimer   = null

// Badge only for pending
const pendingBadge = computed(() =>
  currentStatus.value === 'PENDING_REVIEW' ? store.total : 0
)

const TABS = [
  { status: 'PENDING_REVIEW', label: 'Pending Review' },
  { status: 'APPROVED',       label: 'Approved' },
  { status: 'REJECTED',       label: 'Rejected' },
  { status: 'HIDDEN',         label: 'Hidden' },
]

// ─── Load ─────────────────────────────────────────────────────────────────────
onMounted(() => loadQueue())

function loadQueue(page = 0) {
  store.fetchQueue({ status: currentStatus.value, keyword: keyword.value, page })
}

function switchTab(status) {
  if (currentStatus.value === status) return
  currentStatus.value = status
  selected.value  = null
  selectedId.value = null
  store.clearActions()
  loadQueue()
}

function onSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadQueue(), 350)
}

function goPage(page) { loadQueue(page) }

// ─── Post selection ───────────────────────────────────────────────────────────
function selectPost(post) {
  if (selectedId.value === post.postID) return
  selected.value  = {
    ...post,
    spamReasonsParsed: parseSpamReasons(post.spamReasons),
  }
  selectedId.value = post.postID
  store.fetchActions(post.postID)
}

function parseSpamReasons(json) {
  if (!json) return []
  try { return JSON.parse(json) } catch { return [json] }
}

// ─── Actions ─────────────────────────────────────────────────────────────────
async function doApprove() {
  acting.value = true
  try {
    await store.approve(selected.value.postID)
    selected.value = { ...selected.value, status: 'APPROVED' }
  } finally { acting.value = false }
}

function openRejectModal() { showRejectModal.value = true }

async function onRejectConfirmed({ id, code, reason }) {
  acting.value = true
  try {
    await store.reject(id, code, reason)
    selected.value = { ...selected.value, status: 'REJECTED', rejectionCode: code, rejectionReason: reason }
  } finally { acting.value = false }
}

async function doHide() {
  acting.value = true
  try {
    await store.hide(selected.value.postID)
    selected.value = { ...selected.value, status: 'HIDDEN' }
  } finally { acting.value = false }
}

async function doUnhide() {
  acting.value = true
  try {
    await store.unhide(selected.value.postID)
    selected.value = { ...selected.value, status: 'APPROVED' }
  } finally { acting.value = false }
}

async function doFlagSpam() {
  if (!confirm('Flag this post as spam? It will be hidden immediately.')) return
  acting.value = true
  try {
    await store.flagSpam(selected.value.postID)
    selected.value = { ...selected.value, status: 'HIDDEN', spamScore: 100 }
  } finally { acting.value = false }
}

// ─── Utils ────────────────────────────────────────────────────────────────────
function formatDate(iso) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('en-GB', { dateStyle: 'medium', timeStyle: 'short' })
}
</script>

<style scoped>
/* ─── Layout ──────────────────────────────────────────────────────────────── */
.inbox-page { padding: 24px; display: flex; flex-direction: column; gap: 16px; height: 100%; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; }
.title { font-size: 1.25rem; font-weight: 700; margin: 0; color: #111; }
.subtitle { margin: 4px 0 0; font-size: .83rem; color: #6b7280; }

/* ─── Tabs ────────────────────────────────────────────────────────────────── */
.tabs-row {
  display: flex; align-items: center; gap: 6px; flex-wrap: wrap;
  border-bottom: 1px solid #e5e7eb; padding-bottom: 10px;
}
.tab-btn {
  padding: 6px 14px; border-radius: 8px; border: none;
  background: #f3f4f6; color: #374151; cursor: pointer; font-size: .85rem;
  position: relative;
}
.tab-btn.active { background: #2563eb; color: #fff; font-weight: 700; }
.badge {
  position: absolute; top: -6px; right: -6px;
  background: #ef4444; color: #fff; border-radius: 999px;
  font-size: .65rem; font-weight: 700; padding: 1px 5px;
  min-width: 16px; text-align: center;
}
.search-wrap {
  margin-left: auto; display: flex; align-items: center;
  gap: 6px; border: 1px solid #d1d5db; border-radius: 8px;
  padding: 6px 10px; background: #f9fafb;
}
.search-wrap input { border: none; background: transparent; outline: none; font-size: .85rem; width: 200px; }

/* ─── Split body ──────────────────────────────────────────────────────────── */
.split-body {
  display: grid; grid-template-columns: 340px 1fr;
  gap: 16px; flex: 1; min-height: 0;
  overflow: hidden;
}

/* ─── Post list ───────────────────────────────────────────────────────────── */
.list-col { overflow-y: auto; display: flex; flex-direction: column; gap: 8px; }
.post-card {
  display: flex; gap: 10px; align-items: flex-start;
  padding: 10px 12px; border-radius: 10px; border: 1px solid #e5e7eb;
  cursor: pointer; transition: border-color .15s, box-shadow .15s;
}
.post-card:hover    { border-color: #2563eb; box-shadow: 0 2px 8px rgba(37,99,235,.12); }
.post-card.selected { border-color: #2563eb; background: #eff6ff; }
.thumb { width: 52px; height: 52px; border-radius: 6px; object-fit: cover; flex-shrink: 0; }
.thumb-placeholder { width: 52px; height: 52px; border-radius: 6px; background: #f3f4f6; display: flex; align-items: center; justify-content: center; color: #9ca3af; flex-shrink: 0; }
.post-info { flex: 1; min-width: 0; }
.post-top-row { display: flex; align-items: flex-start; gap: 6px; justify-content: space-between; }
.post-title { font-weight: 600; font-size: .88rem; color: #111; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 170px; }
.post-meta { font-size: .77rem; color: #6b7280; margin-top: 2px; }
.post-date { font-size: .72rem; color: #9ca3af; margin-top: 2px; }
.spam-score { margin-left: 6px; font-weight: 700; }
.spam-score.high { color: #dc2626; }
.spam-score.med  { color: #d97706; }

/* ─── Pagination ─────────────────────────────────────────────────────────── */
.pagination { display: flex; align-items: center; justify-content: center; gap: 10px; padding: 8px 0; font-size: .85rem; }
.pagination button { padding: 4px 12px; border-radius: 6px; border: 1px solid #d1d5db; background: #fff; cursor: pointer; }
.pagination button:disabled { opacity: .4; cursor: not-allowed; }

/* ─── Detail panel ───────────────────────────────────────────────────────── */
.detail-col { overflow-y: auto; padding: 4px 2px; display: flex; flex-direction: column; gap: 14px; }
.empty-detail { align-items: center; justify-content: center; color: #9ca3af; font-size: .9rem; }
.empty-icon { margin-bottom: 8px; }
.detail-header { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.detail-title { font-size: 1rem; font-weight: 700; color: #111; margin: 0; flex: 1; }
.detail-img-wrap { border-radius: 12px; overflow: hidden; max-height: 260px; }
.detail-img { width: 100%; height: 260px; object-fit: cover; }
.detail-meta { font-size: .83rem; color: #6b7280; display: flex; gap: 6px; flex-wrap: wrap; }
.detail-desc { font-size: .88rem; color: #374151; line-height: 1.6; }

.spam-panel {
  background: #fef3c7; border: 1px solid #fcd34d; border-radius: 8px;
  padding: 10px 14px; font-size: .83rem; color: #92400e; display: flex; gap: 10px; flex-wrap: wrap;
}
.spam-reasons { color: #78350f; }

.rejection-panel {
  background: #fee2e2; border: 1px solid #fca5a5; border-radius: 8px;
  padding: 10px 14px; font-size: .83rem; color: #991b1b;
}

/* ─── Action buttons ──────────────────────────────────────────────────────── */
.actions-bar { display: flex; gap: 8px; flex-wrap: wrap; }
.btn {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 7px 16px; border-radius: 8px; border: none;
  cursor: pointer; font-size: .85rem; font-weight: 600;
  transition: opacity .15s;
}
.btn:disabled { opacity: .45; cursor: not-allowed; }
.btn-approve { background: #16a34a; color: #fff; }
.btn-approve:not(:disabled):hover { background: #15803d; }
.btn-reject  { background: #dc2626; color: #fff; }
.btn-reject:not(:disabled):hover  { background: #b91c1c; }
.btn-hide    { background: #6b7280; color: #fff; }
.btn-hide:not(:disabled):hover    { background: #4b5563; }
.btn-unhide  { background: #2563eb; color: #fff; }
.btn-unhide:not(:disabled):hover  { background: #1d4ed8; }
.btn-spam    { background: #d97706; color: #fff; }
.btn-spam:not(:disabled):hover    { background: #b45309; }

/* ─── Timeline ───────────────────────────────────────────────────────────── */
.timeline-section { border-top: 1px solid #e5e7eb; padding-top: 14px; }
.timeline-title { margin: 0 0 10px; font-size: .88rem; font-weight: 700; color: #374151; }
.timeline-item { display: flex; gap: 10px; align-items: flex-start; margin-bottom: 10px; }
.tl-action-badge {
  padding: 2px 8px; border-radius: 6px; font-size: .7rem; font-weight: 700;
  text-transform: uppercase; white-space: nowrap; flex-shrink: 0;
}
.tl-approve  { background: #dcfce7; color: #166534; }
.tl-reject   { background: #fee2e2; color: #991b1b; }
.tl-hide     { background: #e5e7eb; color: #374151; }
.tl-unhide   { background: #dbeafe; color: #1d4ed8; }
.tl-flag_spam { background: #fef3c7; color: #92400e; }
.tl-detail { flex: 1; }
.tl-admin { font-weight: 600; font-size: .83rem; }
.tl-date  { font-size: .75rem; color: #9ca3af; margin-left: 8px; }
.tl-reason { margin: 2px 0 0; font-size: .82rem; color: #6b7280; }

/* ─── Generic helpers ────────────────────────────────────────────────────── */
.center-msg { display: flex; align-items: center; gap: 8px; padding: 20px; justify-content: center; font-size: .9rem; color: #6b7280; }
.center-msg.error { color: #dc2626; }
.center-msg.muted { color: #9ca3af; }
.spin { animation: spin .8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>
