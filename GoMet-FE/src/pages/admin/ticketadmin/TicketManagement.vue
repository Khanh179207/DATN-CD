<template>
  <div class="ticket-sovereign-wrapper">
    <div class="page-header-lux">
      <div>
        <h1 class="page-title">{{ t('admin.tickets.title') }}</h1>
        <p class="page-subtitle">{{ t('admin.tickets.subtitle') }}</p>
      </div>
      <button class="btn-refresh-lux" @click="loadTickets" :disabled="loading">
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
            <tr v-for="ticket in filteredTickets" :key="ticket.ticketID" class="lux-row" @click="viewDetail(ticket)">
              <td class="id-col">#{{ ticket.ticketID }}</td>
              <td class="user-name">{{ ticket.username || t('admin.tickets.anonymous_user') }}</td>
              <td>
                <span class="badge-type" :class="(ticket.ticketType || '').toLowerCase()">
                  {{ formatType(ticket.ticketType) }}
                </span>
              </td>
              <td>
                <span v-if="ticket.targetPostId" class="target-badge">{{ t('admin.tickets.post_short') }} #{{ ticket.targetPostId }}</span>
                <span v-else class="text-muted">{{ t('admin.tickets.unknown') }}</span>
              </td>
              <td class="title-cell">{{ truncateText(ticket.title, 50) }}</td>
              <td class="time-cell">{{ formatDate(ticket.createdAt) }}</td>
              <td>
                <span class="badge-status" :class="'status-' + ticket.status">
                  {{ formatStatus(ticket.status) }}
                </span>
              </td>
              <td class="text-right">
                <button class="btn-refresh-lux btn-sm-action" @click.stop="viewDetail(ticket)">{{ t('admin.tickets.process') }}</button>
              </td>
            </tr>
          </transition-group>
        </table>

        <div v-if="!loading && filteredTickets.length === 0" class="empty-state-lux">
          <div class="empty-icon">🗂️</div>
          <h3>{{ t('admin.tickets.empty_title') }}</h3>
          <button class="btn-refresh-lux btn-outline" @click="resetFilters">{{ t('admin.tickets.clear_filters') }}</button>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <transition name="fade-glass">
        <div v-if="showModal && selectedTicket" class="modal-glass-backdrop" @click="closeModal">
          <div class="modal-lux-content" @click.stop>
            <div class="modal-header-lux">
              <h3>{{ t('admin.tickets.detail_title', { id: selectedTicket.ticketID }) }}</h3>
              <button class="btn-x" @click="closeModal">×</button>
            </div>

            <div class="modal-body lux-split-layout custom-scroll">
              <div class="split-left">
                <div class="desc-box-lux highlight">
                  <label>{{ t('admin.tickets.detail_user_title') }}</label>
                  <strong>{{ selectedTicket.title }}</strong>
                </div>

                <div class="desc-box-lux">
                  <label>{{ t('admin.tickets.detail_content') }}</label>
                  <div class="detail-text">{{ selectedTicket.description || t('admin.tickets.detail_no_content') }}</div>
                </div>

                <div class="evidence-section">
                  <label class="section-label">{{ t('admin.tickets.evidence_label') }}</label>
                  <div class="evidence-wrapper" v-if="selectedTicket.attachment">
                    <img :src="selectedTicket.attachment" :alt="t('admin.tickets.evidence_alt')" class="img-evidence" @click="window.open(selectedTicket.attachment, '_blank')"/>
                  </div>
                  <div class="no-evidence" v-else>{{ t('admin.tickets.no_evidence') }}</div>
                </div>

                <div class="timeline-logs">
                  <div class="timeline-title">{{ t('admin.tickets.timeline_title') }}</div>
                  <div class="log-item">
                    <div class="log-dot gray"></div>
                    <div class="log-content">
                      <span class="log-time">{{ formatDate(selectedTicket.createdAt) }}</span>
                      <span class="log-action">{{ t('admin.tickets.timeline_user_sent', { name: selectedTicket.username || t('admin.tickets.anonymous_user') }) }}</span>
                    </div>
                  </div>
                  <div class="log-item" v-if="selectedTicket.processedAt">
                    <div class="log-dot blue"></div>
                    <div class="log-content">
                      <span class="log-time">{{ formatDate(selectedTicket.processedAt) }}</span>
                      <span class="log-action">
                        {{ t('admin.tickets.timeline_admin_received') }}
                      </span>
                    </div>
                  </div>
                  <div class="log-item" v-if="selectedTicket.status >= 2">
                    <div class="log-dot" :class="selectedTicket.status === 2 ? 'green' : 'red'"></div>
                    <div class="log-content">
                      <span class="log-time">{{ formatDate(selectedTicket.resolvedAt) }}</span>
                      <span class="log-action">
                        {{ selectedTicket.status === 2 ? t('admin.tickets.status_resolved') : t('admin.tickets.status_rejected') }}
                        <span class="log-actor">{{ selectedTicket.adminName || t('admin.tickets.unknown') }}</span>.
                      </span>
                      <div v-if="selectedTicket.adminNote" class="admin-note-reply" :class="{ 'rejected': selectedTicket.status === 3 }">
                        <strong>{{ t('admin.tickets.reject') }}:</strong> {{ selectedTicket.adminNote }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="split-right">
                <div class="meta-card-lux">
                  <div class="meta-item">
                    <label>{{ t('admin.tickets.sender_label') }}</label>
                    <p>👤 {{ selectedTicket.username || t('admin.tickets.anonymous_user') }}</p>
                  </div>
                  <div class="meta-item">
                    <label>{{ t('admin.tickets.created_label') }}</label>
                    <p>🕒 {{ formatDate(selectedTicket.createdAt) }}</p>
                  </div>
                </div>

                <div v-if="selectedTicket.ticketType === 'REPORT'" class="violation-card-lux red-theme">
                  <div class="v-header"><span>{{ t('admin.tickets.reported_content') }}</span></div>
                  <div class="v-body">
                    <span class="lbl">{{ t('admin.tickets.post_id') }}</span>
                    <span class="v-id">#{{ selectedTicket.targetPostId }}</span>
                    <button class="btn-go-post-lux" @click="goToPost(selectedTicket.targetPostId)">{{ t('admin.tickets.view_post_now') }}</button>
                    
                    <div class="quick-mod-bar-vertical">
                      <div v-if="selectedTicket.status === 0" class="action-warning">
                         {{ t('admin.tickets.accept_hint') }}
                      </div>
                      <button class="btn-mod btn-kill-post" 
                              @click="openActionModal('DELETE_POST', selectedTicket.targetPostId)" 
                              :disabled="isProcessingAction || selectedTicket.status !== 1">
                        {{ t('admin.tickets.delete_post_now') }}
                      </button>
                      <button class="btn-mod btn-ban-user" 
                              @click="openActionModal('BAN_USER', selectedTicket.targetPostId)" 
                              :disabled="isProcessingAction || selectedTicket.status !== 1">
                        {{ t('admin.tickets.ban_author_now') }}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="modal-footer-lux">
              <button @click="closeModal" class="btn-lux btn-close-modal">{{ t('admin.tickets.close') }}</button>
              <div class="main-actions">
                <button v-if="selectedTicket.status === 0" class="btn-lux btn-process" @click="handleUpdateStatus(1)">{{ t('admin.tickets.accept') }}</button>
                <button v-if="selectedTicket.status < 2" class="btn-lux btn-reject" @click="openActionModal('REJECT_TICKET', selectedTicket.ticketID)">{{ t('admin.tickets.reject') }}</button>
                <button v-if="selectedTicket.status === 1" class="btn-lux btn-resolve" @click="handleUpdateStatus(2, t('admin.tickets.resolve_note'))">{{ t('admin.tickets.resolve') }}</button>
              </div>
            </div>
          </div>
        </div>
      </transition>

      <transition name="fade-glass">
        <div v-if="actionModal.show" class="modal-glass-backdrop confirm-overlay" @click="closeActionModal">
          <div class="confirm-dialog-card luxury-reject-form" @click.stop>
            <div class="form-header">
               <div class="icon-danger-circle">
                 <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2.5"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
               </div>
               <h3 class="confirm-title">{{ actionModal.title }}</h3>
            </div>
            
            <p class="confirm-desc">{{ actionModal.message }}</p>
            
            <div class="input-group">
                <label>{{ t('admin.tickets.reason_label') }} <span class="required">*</span></label>
                <textarea v-model="actionModal.reason" class="note-input-lux" :placeholder="t('admin.tickets.reason_placeholder')" rows="4"></textarea>
                <span class="char-count" :class="{'text-red-500': actionModal.reason.length > 500}">{{ actionModal.reason.length }}/500</span>
            </div>

            <div class="confirm-actions">
                <button class="btn-cancel-action" @click="closeActionModal" :disabled="isProcessingAction">{{ t('admin.tickets.cancel_action') }}</button>
              <button class="btn-danger-action" @click="executeAdminAction" :disabled="!actionModal.reason.trim() || isProcessingAction || actionModal.reason.length > 500">
                  <span v-if="!isProcessingAction">{{ t('admin.tickets.confirm_action') }}</span>
                  <span v-else>{{ t('admin.tickets.processing_action') }}</span>
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { toast } from '@/composables/useToast'
import { useAuthStore } from '@/stores/auth'
import ticketService from '@/services/ticketService'
import api from '@/services/api' 
import './TicketManagement.scss'

const { t } = useI18n()
const authStore = useAuthStore()
const tickets = ref([])
const loading = ref(false)
const showModal = ref(false)
const selectedTicket = ref(null)
const currentStatusFilter = ref(-1)
const currentTypeFilter = ref('ALL')
const searchQuery = ref('') 
const isProcessingAction = ref(false)

const actionModal = ref({ show: false, type: '', targetId: null, title: '', message: '', reason: '' })

// 1. DATA LOADING
const loadTickets = async () => {
  loading.value = true
  try {
    const data = await ticketService.getAllTickets()
    tickets.value = data
  } catch (e) {
    toast.error(t('admin.tickets.server_error'))
  } finally {
    loading.value = false
  }
}

// 2. STATUS UPDATE TICKET
const handleUpdateStatus = async (status, note = "") => {
  if (!selectedTicket.value) return
  const id = selectedTicket.value.ticketID
  
  isProcessingAction.value = true
  try {
    await ticketService.updateTicketStatus(id, status, note)
    toast.success(t('admin.tickets.status_updated'))
    
    await loadTickets()
    const freshTicket = tickets.value.find(t => t.ticketID === id)
    if (freshTicket) selectedTicket.value = { ...freshTicket }

  } catch (e) {
    toast.error(t('admin.tickets.process_error'))
  } finally {
    isProcessingAction.value = false
  }
}

// 3. MỞ POPUP NHẬP LÝ DO
const openActionModal = (type, targetId) => {
  actionModal.value = {
    show: true,
    type,
    targetId,
    reason: '',
    title: type === 'DELETE_POST' ? t('admin.tickets.delete_post_title', { id: targetId }) : (type === 'BAN_USER' ? t('admin.tickets.ban_user_title', { id: targetId }) : t('admin.tickets.reject')),
    message: type === 'DELETE_POST' 
      ? t('admin.tickets.delete_post_msg') 
      : (type === 'BAN_USER' ? t('admin.tickets.ban_user_msg') : t('admin.tickets.reject_message'))
  }
}

const closeActionModal = () => { actionModal.value.show = false }

// 4. XỬ LÝ VI PHẠM (ĐÃ BỎ GỌI AUDIT LOG Ở FRONTEND)
const executeAdminAction = async () => {
  const { type, targetId, reason } = actionModal.value
  isProcessingAction.value = true
  
  try {
    if (type === 'REJECT_TICKET') {
      await handleUpdateStatus(3, reason.trim())
      closeActionModal()
      return; 
    }

    const ticketRef = `#${selectedTicket.value.ticketID}`;
    const fullReason = `${reason.trim()} (Xử lý từ Ticket ${ticketRef})`;

    const payload = {
      adminId: authStore.user?.accountID || authStore.user?.id || 0,
      adminName: authStore.user?.username || authStore.user?.fullName || 'Admin',
      reason: fullReason
    };

    if (type === 'DELETE_POST') {
      // Gọi API Gỡ Bài Viết (Backend tự ghi log)
      await api.put(`/api/admin/posts/${targetId}/reject`, payload); 
      
      await handleUpdateStatus(2, `Đã gỡ bài viết vi phạm. Lý do: ${reason.trim()}`);

    } else if (type === 'BAN_USER') {
      // 1. Tìm ID Tác Giả
      const postDetail = await api.get(`/api/posts/${targetId}`);
      const authorId = postDetail.data?.accountID || postDetail.data?.authorID || postDetail.data?.authorId;

      if (!authorId) throw new Error(t('admin.tickets.missing_author'));

      // 2. Khóa Account (Backend tự ghi log)
      await api.patch(`/admin/accounts/${authorId}/ban`, payload);

      // 3. Tự động gỡ luôn Bài Viết của nó
      await api.put(`/api/admin/posts/${targetId}/reject`, payload).catch(() => {}); 

      await handleUpdateStatus(2, `Đã khóa tài khoản và gỡ bài viết. Lý do: ${reason.trim()}`);
    }

    toast.success(t('admin.tickets.action_success'))
    closeActionModal()

  } catch (e) {
    toast.error(`${t('admin.tickets.server_error')} ${e.response?.data?.message || e.message}`)
  } finally {
    isProcessingAction.value = false
  }
}

// 5. COMPUTED & HELPERS
const filterTabs = computed(() => [
  { label: t('common.all'), value: -1 },
  { label: t('admin.tickets.filter_pending'), value: 0 },
  { label: t('admin.tickets.filter_processing'), value: 1 },
  { label: t('admin.tickets.filter_done'), value: 2 },
])

const filteredTickets = computed(() => {
  return tickets.value.filter(t => {
    const matchS = currentStatusFilter.value === -1 || t.status === currentStatusFilter.value
    const matchT = currentTypeFilter.value === 'ALL' || t.ticketType === currentTypeFilter.value
    const query = searchQuery.value.toLowerCase().trim()
    const matchSearch = String(t.ticketID).includes(query) || (t.title && t.title.toLowerCase().includes(query))
    return matchS && matchT && matchSearch
  }).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

const stats = computed(() => [
  { label: t('admin.tickets.stat_pending'), value: tickets.value.length, iconClass: 'all', icon: '📝' },
  { label: 'Báo Lỗi (BUG)', value: tickets.value.filter(t => t.ticketType === 'BUG').length, iconClass: 'bug', icon: '🐞' },
  { label: 'Vi Phạm', value: tickets.value.filter(t => t.ticketType === 'REPORT').length, iconClass: 'report', class: 'danger-highlight', icon: '🚩' },
  { label: 'Góp ý', value: tickets.value.filter(t => t.ticketType === 'FEEDBACK').length, iconClass: 'feedback', icon: '💡' }
])

const viewDetail = (ticket) => { selectedTicket.value = ticket; showModal.value = true }
const closeModal = () => { showModal.value = false; closeActionModal() }
const resetFilters = () => { currentStatusFilter.value = -1; currentTypeFilter.value = 'ALL'; searchQuery.value = '' }
const goToPost = (id) => window.open(`/post/${id}`, '_blank')
const truncateText = (t, l) => t?.length > l ? t.substring(0, l) + '...' : t
const formatDate = (d) => d ? new Date(d).toLocaleString('vi-VN') : t('admin.tickets.unknown')
const formatType = (ticketType) => ({ BUG: t('admin.tickets.type_bug'), REPORT: t('admin.tickets.type_report'), FEEDBACK: t('admin.tickets.type_feedback') }[ticketType] || ticketType)
const formatStatus = (status) => ({ 0: t('admin.tickets.status_pending'), 1: t('admin.tickets.status_processing'), 2: t('admin.tickets.status_resolved'), 3: t('admin.tickets.status_rejected') }[status])

onMounted(loadTickets)
</script>