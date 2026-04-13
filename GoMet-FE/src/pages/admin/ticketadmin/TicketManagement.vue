<template>
  <div class="ticket-sovereign-wrapper">
    
    <div class="page-header-lux">
      <div>
        <h1 class="page-title">{{ t('admin.tickets.title') }}</h1>
        <p class="page-subtitle">{{ t('admin.tickets.subtitle') }}</p>
      </div>
      <button class="btn-refresh-lux" @click="fetchTickets" :disabled="loading">
        <svg :class="{ 'spinning': loading }" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M23 4v6h-6M1 20v-6h6M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
        </svg>
        {{ loading ? t('admin.tickets.syncing') : t('admin.tickets.refresh') }}
      </button>
    </div>

    <div class="stats-grid">
      <div class="stat-card" v-for="stat in stats" :key="stat.label" :class="stat.class">
        <div class="icon-wrap" :class="stat.iconClass" v-html="stat.icon"></div>
        <div class="stat-info">
          <span class="label">{{ stat.label }}</span>
          <h3 class="value">{{ stat.value }}</h3>
        </div>
      </div>
    </div>

    <div class="data-engine-lux">
      <div class="filter-bar">
        <div class="tabs-lux">
          <button v-for="tab in filterTabs" :key="tab.value" 
                  class="tab-btn" :class="{ active: currentStatusFilter === tab.value }"
                  @click="currentStatusFilter = tab.value">
            {{ tab.label }}
          </button>
        </div>
        
        <div class="filter-right-actions">
          <div class="search-box-lux">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
            <input v-model="searchQuery" type="text" :placeholder="t('admin.tickets.search_placeholder')">
          </div>

          <select v-model="currentTypeFilter" class="select-lux">
            <option value="ALL">{{ t('admin.tickets.type_all') }}</option>
            <option value="BUG">{{ t('admin.tickets.type_bug') }}</option>
            <option value="REPORT">{{ t('admin.tickets.type_report') }}</option>
            <option value="FEEDBACK">{{ t('admin.tickets.type_feedback') }}</option>
          </select>
        </div>
      </div>

      <div class="table-responsive">
        <table v-if="!loading" class="lux-table">
          <thead>
            <tr>
              <th width="80">{{ t('admin.tickets.col_id') }}</th>
              <th width="150">{{ t('admin.tickets.col_sender') }}</th>
              <th width="120">{{ t('admin.tickets.col_type') }}</th>
              <th width="120">{{ t('admin.tickets.col_related') }}</th>
              <th>{{ t('admin.tickets.col_title') }}</th>
              <th width="160">{{ t('admin.tickets.col_time') }}</th>
              <th width="140">{{ t('admin.tickets.col_status') }}</th>
              <th width="120" class="text-right">{{ t('admin.tickets.col_actions') }}</th>
            </tr>
          </thead>
          <transition-group name="list-slide" tag="tbody">
            <tr v-for="ticket in filteredTickets" :key="ticket.ticketID || ticket.id" class="lux-row" @click="viewDetail(ticket)">
              <td class="id-col">#{{ ticket.ticketID || ticket.id }}</td>
              <td class="user-name">
                {{ ticket.account?.username || ticket.username || t('admin.tickets.anonymous_user') }}
              </td>
              <td>
                <span class="badge-type" :class="(ticket.ticketType || '').toLowerCase()">
                  {{ formatType(ticket.ticketType) }}
                </span>
              </td>
              <td>
                <span v-if="ticket.targetPostId || ticket.targetPostID" class="target-badge">
                  Post: #{{ ticket.targetPostId || ticket.targetPostID }}
                </span>
                <span v-else class="text-muted">—</span>
              </td>
              <td class="title-cell">{{ truncateText(ticket.title, 50) || '—' }}</td>
              <td class="time-cell">{{ formatDate(ticket.createdAt) }}</td>
              <td>
                <span class="badge-status" :class="'status-' + ticket.status">
                  {{ formatStatus(ticket.status) }}
                </span>
              </td>
              <td class="text-right">
                <button class="btn-refresh-lux btn-sm-action" @click.stop="handleRowAction(ticket)">{{ t('admin.tickets.process') }}</button>
              </td>
            </tr>
          </transition-group>
        </table>

        <div v-if="!loading && filteredTickets.length === 0" class="empty-state-lux">
          <div class="empty-icon">🗂️</div>
          <h3>{{ t('admin.tickets.empty_title') }}</h3>
          <p>{{ t('admin.tickets.empty_desc') }}</p>
          <button class="btn-refresh-lux btn-outline" @click="resetFilters">{{ t('admin.tickets.clear_filters') }}</button>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <transition name="fade-glass">
        <div v-if="showModal && selectedTicket" class="modal-glass-backdrop" @click="closeModal">
          <div class="modal-lux-content" @click.stop>
            <div class="modal-header-lux">
              <h3>{{ t('admin.tickets.detail_title', { id: selectedTicket.ticketID || selectedTicket.id }) }}</h3>
              <button class="btn-x" @click="closeModal">×</button>
            </div>

            <div class="modal-body lux-split-layout custom-scroll">
              <div class="split-left">
                <div class="desc-box-lux highlight">
                  <label>{{ t('admin.tickets.detail_user_title') }}</label>
                  <strong>{{ selectedTicket.title || t('admin.tickets.detail_default_title') }}</strong>
                </div>

                <div class="desc-box-lux">
                  <label>{{ t('admin.tickets.detail_content') }}</label>
                  <div class="detail-text">{{ selectedTicket.description || t('admin.tickets.detail_no_content') }}</div>
                </div>

                <div class="evidence-section">
                  <label class="section-label">{{ t('admin.tickets.evidence_label') }}</label>
                  <div class="evidence-wrapper" v-if="selectedTicket.attachment">
                     <img :src="selectedTicket.attachment" :alt="t('admin.tickets.evidence_alt')" class="img-evidence" @error="(e) => e.target.src = `https://placehold.co/600x400?text=${encodeURIComponent(t('admin.tickets.evidence_load_fail'))}`"/>
                  </div>
                  <div class="no-evidence" v-else>
                    <i class="fas fa-image-slash"></i> {{ t('admin.tickets.no_evidence') }}
                  </div>
                </div>

                <div class="timeline-logs">
                  <div class="timeline-title">{{ t('admin.tickets.timeline_title') }}</div>
                  <div class="log-item">
                    <div class="log-dot gray"></div>
                    <div class="log-content">
                      <span class="log-time">{{ formatDate(selectedTicket.createdAt) }}</span>
                      <span class="log-action">{{ t('admin.tickets.timeline_user_sent', { name: selectedTicket.account?.username || selectedTicket.username || t('admin.tickets.anonymous_user') }) }}</span>
                    </div>
                  </div>
                  <div class="log-item" v-if="selectedTicket.processedAt || selectedTicket.ProcessedAt">
                    <div class="log-dot blue"></div>
                    <div class="log-content">
                      <span class="log-time">{{ formatDate(selectedTicket.processedAt || selectedTicket.ProcessedAt) }}</span>
                      <span class="log-action">{{ t('admin.tickets.timeline_admin_received') }}</span>
                    </div>
                  </div>
                  <div class="log-item" v-if="selectedTicket.status >= 2 && selectedTicket.resolvedAt">
                    <div class="log-dot green"></div>
                    <div class="log-content">
                      <span class="log-time">{{ formatDate(selectedTicket.resolvedAt) }}</span>
                      <span class="log-action">{{ t('admin.tickets.timeline_admin_closed') }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="split-right">
                <div class="meta-card-lux">
                  <div class="meta-item">
                    <label>{{ t('admin.tickets.sender_label') }}</label>
                    <p>👤 {{ selectedTicket.account?.username || selectedTicket.username || t('admin.tickets.unknown') }}</p>
                  </div>
                  <div class="meta-item">
                    <label>{{ t('admin.tickets.created_label') }}</label>
                    <p>🕒 {{ formatDate(selectedTicket.createdAt) }}</p>
                  </div>
                </div>

                <div v-if="selectedTicket.ticketType === 'REPORT'" class="violation-card-lux red-theme">
                  <div class="v-header">
                    <span>{{ t('admin.tickets.reported_content') }}</span>
                  </div>
                  <div class="v-body">
                    <span class="lbl">{{ t('admin.tickets.post_id') }}</span>
                    <span class="v-id">#{{ selectedTicket.targetPostId || selectedTicket.targetPostID }}</span>
                    <button class="btn-go-post-lux" @click="goToPost(selectedTicket.targetPostId || selectedTicket.targetPostID)">
                       {{ t('admin.tickets.view_post_now') }}
                    </button>

                    <div class="quick-mod-bar-vertical">
                      <button class="btn-mod btn-kill-post" @click="openConfirmModal('DELETE_POST', selectedTicket.targetPostId || selectedTicket.targetPostID)" :disabled="isProcessingAction">
                        {{ t('admin.tickets.delete_post_now') }}
                      </button>
                      <button class="btn-mod btn-ban-user" @click="openConfirmModal('BAN_USER', selectedTicket.targetPostId || selectedTicket.targetPostID)" :disabled="isProcessingAction">
                        {{ t('admin.tickets.ban_author_now') }}
                      </button>
                    </div>
                  </div>
                </div>

                <div v-else class="type-info-card">
                  <label>{{ t('admin.tickets.type_label') }}</label>
                  <div class="type-display" :class="selectedTicket.ticketType.toLowerCase()">
                    {{ formatType(selectedTicket.ticketType) }}
                  </div>
                </div>
              </div>
            </div>

            <div class="modal-footer-lux">
              <button @click="closeModal" class="btn-lux btn-close-modal">{{ t('admin.tickets.close') }}</button>
              <div class="main-actions">
                <button v-if="selectedTicket.status === 0" class="btn-lux btn-process" @click="updateStatus(1)">{{ t('admin.tickets.accept') }}</button>
                <button v-if="selectedTicket.status < 2" class="btn-lux btn-reject" @click="updateStatus(3)">{{ t('admin.tickets.reject') }}</button>
                <button v-if="selectedTicket.status === 1" class="btn-lux btn-resolve" @click="updateStatus(2)">{{ t('admin.tickets.resolve') }}</button>
              </div>
            </div>
          </div>
        </div>
      </transition>

      <transition name="fade-glass">
        <div v-if="confirmModal.show" class="modal-glass-backdrop confirm-overlay" @click="closeConfirmModal">
          <div class="confirm-dialog-card" @click.stop>
            <div class="icon-danger-circle">
              <svg v-if="confirmModal.type === 'DELETE_POST'" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2.5"><path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
              <svg v-if="confirmModal.type === 'BAN_USER'" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line></svg>
            </div>
            <h3 class="confirm-title">{{ confirmModal.title }}</h3>
            <p class="confirm-desc">{{ confirmModal.message }}</p>
            
            <div class="confirm-actions">
              <button class="btn-cancel-action" @click="closeConfirmModal" :disabled="isProcessingAction">{{ t('admin.tickets.cancel_action') }}</button>
              <button class="btn-danger-action" @click="executeAction" :disabled="isProcessingAction">
                <span v-if="!isProcessingAction">{{ t('admin.tickets.confirm_action') }}</span>
                <span v-else>{{ t('admin.tickets.processing_action') }}</span>
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

  </div> </template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { toast } from '@/composables/useToast'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'
import { formatLocaleDateTime } from '@/i18n'
import './TicketManagement.scss' // 🔥 IMPORT FILE SCSS Ở ĐÂY SẾP NHÉ

const authStore = useAuthStore()
const router = useRouter()
const { t } = useI18n()

const tickets = ref([])
const loading = ref(false)
const showModal = ref(false)
const selectedTicket = ref(null)
const currentStatusFilter = ref(-1)
const currentTypeFilter = ref('ALL')
const searchQuery = ref('') 
const isProcessingAction = ref(false)

const confirmModal = ref({
  show: false, type: '', targetId: null, title: '', message: ''
})

const openConfirmModal = (type, targetId) => {
  if (!targetId) return toast.error(t('admin.tickets.missing_post_id'));
  confirmModal.value.type = type;
  confirmModal.value.targetId = targetId;
  confirmModal.value.show = true;
  if (type === 'DELETE_POST') {
    confirmModal.value.title = t('admin.tickets.delete_post_title', { id: targetId });
    confirmModal.value.message = t('admin.tickets.delete_post_msg');
  } else if (type === 'BAN_USER') {
    confirmModal.value.title = t('admin.tickets.ban_user_title', { id: targetId });
    confirmModal.value.message = t('admin.tickets.ban_user_msg');
  }
}

const closeConfirmModal = () => {
  if (isProcessingAction.value) return;
  confirmModal.value.show = false;
}

const executeAction = async () => {
  if (confirmModal.value.type === 'DELETE_POST') {
    await deleteViolatingPost(confirmModal.value.targetId);
  } else if (confirmModal.value.type === 'BAN_USER') {
    await banViolatingUser(confirmModal.value.targetId);
  }
}

const deleteViolatingPost = async (postId) => {
  isProcessingAction.value = true;
  try {
    const res = await fetch(`/api/admin/posts/${postId}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${authStore.token}` } });
    if (res.ok) { toast.success(t('admin.tickets.delete_post_ok', { id: postId })); await updateStatus(2); closeConfirmModal(); } 
    else toast.error(t('admin.tickets.delete_post_fail'));
  } catch (error) { toast.error(t('toast.system_error')); }
  finally { isProcessingAction.value = false; }
}

const banViolatingUser = async (postId) => {
  isProcessingAction.value = true;
  try {
    const res = await fetch(`/api/admin/posts/${postId}/ban-author`, { method: 'PUT', headers: { 'Authorization': `Bearer ${authStore.token}`, 'Content-Type': 'application/json' }, body: JSON.stringify({}) });
    if (res.ok) { toast.success(t('admin.tickets.ban_user_ok')); await updateStatus(2); closeConfirmModal(); } 
    else { const err = await res.json().catch(()=>({})); toast.error(err.message || t('admin.tickets.ban_user_fail')); }
  } catch (error) { toast.error(t('admin.common.server_error')); } 
  finally { isProcessingAction.value = false; }
}

const filterTabs = [
  { label: t('admin.common.all'), value: -1 },
  { label: t('admin.tickets.filter_pending'), value: 0 },
  { label: t('admin.tickets.filter_processing'), value: 1 },
  { label: t('admin.tickets.filter_done'), value: 2 },
]

const stats = computed(() => [
  { label: t('admin.tickets.stat_pending'), value: tickets.value.filter(t => t.status === 0).length, iconClass: 'all', icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="3" y1="9" x2="21" y2="9"></line><line x1="9" y1="21" x2="9" y2="9"></line></svg>' },
  { label: t('admin.tickets.stat_bug'), value: tickets.value.filter(t => t.status === 0 && t.ticketType === 'BUG').length, iconClass: 'bug', icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M22 12h-4l-3 9L9 3l-3 9H2"></path></svg>' },
  { label: t('admin.tickets.stat_report'), value: tickets.value.filter(t => t.status === 0 && t.ticketType === 'REPORT').length, iconClass: 'report', class: 'danger-highlight', icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>' },
  { label: t('admin.tickets.stat_feedback'), value: tickets.value.filter(t => t.status === 0 && t.ticketType === 'FEEDBACK').length, iconClass: 'feedback', icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>' }
])

const fetchTickets = async () => {
  loading.value = true
  try {
    const res = await fetch('/api/admin/tickets', { headers: { 'Authorization': `Bearer ${authStore.token}` } })
    if (res.ok) tickets.value = await res.json()
  } catch (e) { toast.error(t('admin.tickets.server_error')) }
  finally { loading.value = false }
}

const updateStatus = async (newStatus) => {
  try {
    const ticketId = selectedTicket.value.ticketID || selectedTicket.value.id
    const res = await fetch(`/api/admin/tickets/${ticketId}/status?status=${newStatus}`, { method: 'PUT', headers: { 'Authorization': `Bearer ${authStore.token}` } })
    if (res.ok) { await fetchTickets(); const u = tickets.value.find(t => (t.ticketID || t.id) === ticketId); if(u) selectedTicket.value = {...u}; toast.success(t('admin.tickets.status_updated')); }
  } catch (e) { toast.error(t('admin.tickets.process_error')) }
}

const goToPost = (id) => { if (id) window.open(`/post/${id}`, '_blank'); }

const filteredTickets = computed(() => {
  return tickets.value.filter(t => {
    const matchS = currentStatusFilter.value === -1 || t.status === currentStatusFilter.value;
    const matchT = currentTypeFilter.value === 'ALL' || t.ticketType === currentTypeFilter.value;
    
    const query = searchQuery.value.toLowerCase().trim();
    const title = (t.title || '').toLowerCase();
    const id = String(t.ticketID || t.id);
    const matchSearch = query === '' || title.includes(query) || id.includes(query);

    return matchS && matchT && matchSearch;
  }).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

const resetFilters = () => {
  currentStatusFilter.value = -1;
  currentTypeFilter.value = 'ALL';
  searchQuery.value = '';
}

const handleRowAction = (ticket) => { selectedTicket.value = ticket; showModal.value = true; }
const viewDetail = (ticket) => { selectedTicket.value = ticket; showModal.value = true; }
const closeModal = () => { showModal.value = false }
const truncateText = (t, l) => t?.length > l ? t.substring(0, l) + '...' : t
const formatDate = (d) => d ? formatLocaleDateTime(d, { hour: '2-digit', minute:'2-digit', day:'2-digit', month:'2-digit', year:'numeric' }) : '—'
const formatType = (value) => ({
  BUG: t('admin.tickets.type_bug'),
  REPORT: t('admin.tickets.type_report'),
  FEEDBACK: t('admin.tickets.type_feedback'),
}[value] || value)
const formatStatus = (value) => ({
  0: t('admin.tickets.status_pending'),
  1: t('admin.tickets.status_processing'),
  2: t('admin.tickets.status_resolved'),
  3: t('admin.tickets.status_rejected'),
}[value])

onMounted(fetchTickets)
</script>