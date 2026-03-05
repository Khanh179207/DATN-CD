import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as svc from '@/services/moderationService'

export const useModerationStore = defineStore('moderation', () => {
  // ─── State ───────────────────────────────────────────────────────────────────
  const posts       = ref([])          // current page of posts
  const total       = ref(0)           // total matching records
  const currentPage = ref(0)
  const pageSize    = ref(20)
  const loading     = ref(false)
  const error       = ref(null)
  const actions     = ref([])          // action timeline for the selected post
  const actionsLoading = ref(false)

  // ─── Computed ────────────────────────────────────────────────────────────────
  const pendingCount = computed(() =>
    posts.value.filter(p => p.status === 'PENDING_REVIEW').length
  )
  const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

  // ─── Actions ─────────────────────────────────────────────────────────────────

  /**
   * Load posts from the moderation queue.
   * @param {object} opts  { status, keyword, page, size }
   */
  async function fetchQueue({ status = 'PENDING_REVIEW', keyword = '', page = 0, size = 20 } = {}) {
    loading.value = true
    error.value   = null
    try {
      const { data } = await svc.getModerationQueue({ status, keyword, page, size })
      posts.value   = data.content ?? data
      total.value   = data.totalElements ?? (data.content?.length ?? data.length)
      currentPage.value = page
      pageSize.value    = size
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to load moderation queue'
    } finally {
      loading.value = false
    }
  }

  /** Approve a post and refresh queue in-place. */
  async function approve(id, reason = '') {
    await svc.approvePost(id, reason)
    _removeOrUpdatePost(id, 'APPROVED')
  }

  /** Reject a post (code + reason required). */
  async function reject(id, code, reason) {
    await svc.rejectPost(id, code, reason)
    _removeOrUpdatePost(id, 'REJECTED')
  }

  /** Hide an approved post. */
  async function hide(id, reason = '') {
    await svc.hidePost(id, reason)
    _removeOrUpdatePost(id, 'HIDDEN')
  }

  /** Unhide a post. */
  async function unhide(id, reason = '') {
    await svc.unhidePost(id, reason)
    _removeOrUpdatePost(id, 'APPROVED')
  }

  /** Flag a post as spam. */
  async function flagSpam(id, reason = '') {
    await svc.flagSpam(id, reason)
    _removeOrUpdatePost(id, 'HIDDEN')
  }

  /** Load action timeline for a specific post. */
  async function fetchActions(postId) {
    actionsLoading.value = true
    try {
      const { data } = await svc.getPostActions(postId)
      actions.value = data
    } catch (_) {
      actions.value = []
    } finally {
      actionsLoading.value = false
    }
  }

  function clearActions() {
    actions.value = []
  }

  // ─── Private helpers ─────────────────────────────────────────────────────────

  /** Remove the post from the current view (it moved to a different status tab). */
  function _removeOrUpdatePost(id, newStatus) {
    const idx = posts.value.findIndex(p => p.postID === id)
    if (idx !== -1) {
      posts.value[idx] = { ...posts.value[idx], status: newStatus }
    }
  }

  return {
    posts, total, currentPage, pageSize, loading, error,
    actions, actionsLoading,
    pendingCount, totalPages,
    fetchQueue, approve, reject, hide, unhide, flagSpam,
    fetchActions, clearActions,
  }
})
