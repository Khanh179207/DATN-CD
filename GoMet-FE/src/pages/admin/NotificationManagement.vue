<template>
  <div class="category-sovereign-wrapper notification-sync-page">
    <div class="page-header-lux">
      <div>
        <h1 class="page-title">Quản lý Thông báo</h1>
        <p class="page-subtitle">Hệ thống tạo, theo dõi và kiểm soát thông báo của GOMET</p>
      </div>
      <button class="btn-action-lux" @click="openCreateModal">
        <Plus :size="20" stroke-width="3" />
        Tạo Thông báo
      </button>
    </div>

    <div class="stats-grid" v-if="!loading && !error">
      <div class="stat-card">
        <div class="icon-wrap all">
          <Layers :size="22" />
        </div>
        <div class="stat-info">
          <span class="label">Tổng Thông báo</span>
          <h3 class="value">{{ notifications.length }}</h3>
        </div>
      </div>

      <div class="stat-card">
        <div class="icon-wrap broadcast">
          <Send :size="22" />
        </div>
        <div class="stat-info">
          <span class="label">Toàn hệ thống</span>
          <h3 class="value">{{ broadcastCount }}</h3>
        </div>
      </div>

      <div class="stat-card highlight-card">
        <div class="icon-wrap target">
          <Users :size="22" />
        </div>
        <div class="stat-info">
          <span class="label">Nhắn riêng</span>
          <h3 class="value">{{ targetedCount }}</h3>
        </div>
      </div>
    </div>

    <div v-if="loading" class="grid-list">
      <div v-for="n in 6" :key="n" class="lux-cat-card skeleton">
        <div class="skel-img"></div>
        <div class="skel-body">
          <div class="skel-line"></div>
          <div class="skel-line short"></div>
        </div>
      </div>
    </div>

    <div v-else-if="error" class="error-banner">
      <AlertTriangle :size="20" />
      <span>{{ error }}</span>
      <button @click="fetchNotifications">Thử lại</button>
    </div>

    <div v-else class="grid-list">
      <div v-for="notification in notifications" :key="notification.notificationID" class="lux-cat-card clickable-card"
        @click="openDetailModal(notification.notificationID)">
        <div class="card-img-wrap">
          <div class="hero-icon">
            <Megaphone :size="42" />
          </div>

          <div class="post-count-badge" :class="{ 'is-global': notification.isGlobal }">
            {{ notification.isGlobal ? 'Toàn hệ thống' : 'Nhắn riêng' }}
          </div>

          <div class="hidden-badge" v-if="notification.type">{{ notification.type }}</div>

          <div class="overlay-actions">
            <button class="btn-act view" @click.stop="openDetailModal(notification.notificationID)"
              title="Xem chi tiết">
              <Eye :size="20" />
            </button>
            <button class="btn-act delete" @click.stop="deleteNotificationItem(notification)" title="Xóa thông báo">
              <Trash2 :size="20" />
            </button>
          </div>
        </div>

        <div class="card-content">
          <h3 class="cat-name">{{ notification.title || '(Không có tiêu đề)' }}</h3>
          <div class="meta-row">
            <span class="ratio-text">Người nhận</span>
            <span class="ratio-pct text-truncate">{{ getRecipientLabel(notification) }}</span>
          </div>
          <p class="content-preview">{{ notification.content || 'Không có nội dung' }}</p>
          <div class="view-posts-hint">{{ formatDate(notification.createdAt) }} →</div>
        </div>
      </div>

      <div v-if="notifications.length === 0" class="empty-state">
        Hệ thống chưa có thông báo thủ công nào.
      </div>
    </div>

    <Teleport to="body">
      <Transition name="fade-glass">
        <div v-if="showCreateModal" class="modal-glass-backdrop" @click.self="closeCreateModal">
          <div class="modal-lux-content" @click.stop>
            <div class="modal-header-lux">
              <h3>
                <Plus :size="18" style="vertical-align:middle;margin-right:8px" />
                Khởi tạo Thông báo
              </h3>
              <button class="btn-x" @click="closeCreateModal">
                <X :size="24" />
              </button>
            </div>

            <div class="modal-body-lux custom-scroll">
              <div class="form-group-lux">
                <label>Đối tượng người nhận</label>
                <div class="recipient-switch">
                  <label class="radio-tab" :class="{ active: form.sendTo === 'all' }">
                    <input type="radio" value="all" v-model="form.sendTo" />
                    <span>Tất cả</span>
                  </label>
                  <label class="radio-tab" :class="{ active: form.sendTo === 'user' }">
                    <input type="radio" value="user" v-model="form.sendTo" />
                    <span>Cá nhân / Nhiều người</span>
                  </label>
                </div>
              </div>

              <Transition name="recipient-panel">
                <div v-if="form.sendTo === 'user'" class="form-group-lux animate-in">
                  <label>Chọn người nhận <span class="req">*</span></label>
                  <div ref="recipientPickerRef">
                    <div class="selected-chip-list" v-if="selectedRecipients.length">
                      <button v-for="member in selectedRecipients" :key="member.accountID" class="selected-chip"
                        type="button" @click="removeRecipient(member.accountID)">
                        {{ member.username || ('ID ' + member.accountID) }}
                        <X :size="14" />
                      </button>
                    </div>

                    <div class="recipient-picker" @click="showRecipientDropdown = true">
                      <Search :size="16" />
                      <input v-model="recipientKeyword" type="text" class="input-lux recipient-input"
                        placeholder="Tìm theo ID, username, email..." @focus="showRecipientDropdown = true" />
                    </div>

                    <Transition name="recipient-dropdown-anim">
                      <div v-if="showRecipientDropdown" class="recipient-dropdown custom-scroll">
                        <button v-for="member in filteredRecipients" :key="member.accountID" type="button"
                          class="recipient-option" @click="addRecipient(member)">
                          <img :src="member.avatar || '/logogoc.jpg'" alt="avatar" @error="handleAvatarError" />
                          <div>
                            <strong>{{ member.username || ('ID ' + member.accountID) }}</strong>
                            <span>{{ member.email || 'Không có email' }}</span>
                          </div>
                        </button>
                        <p v-if="membersLoading" class="recipient-empty">Đang tải danh sách...</p>
                        <p v-else-if="!filteredRecipients.length" class="recipient-empty">Không tìm thấy người dùng phù
                          hợp.</p>
                      </div>
                    </Transition>
                  </div>
                </div>
              </Transition>

              <div class="form-group-lux">
                <label>Tiêu đề thông báo <span class="req">*</span></label>
                <input v-model="form.title" type="text" class="input-lux"
                  placeholder="VD: Lịch bảo trì hệ thống GoMet...">
              </div>

              <div class="form-group-lux">
                <label>Nội dung chi tiết <span class="req">*</span></label>
                <textarea v-model="form.content" rows="4" class="input-lux"
                  placeholder="Nhập nội dung thông báo..."></textarea>
              </div>

              <div class="form-group-lux">
                <label>Đường dẫn điều hướng</label>
                <input v-model="form.link" type="text" class="input-lux" placeholder="/post/10 hoặc /news">
              </div>
            </div>

            <div class="modal-footer-lux">
              <button @click="closeCreateModal" class="btn-lux btn-reject">Hủy bỏ</button>
              <button @click="submitNotification" class="btn-lux btn-resolve" :disabled="sending">
                <Loader2 v-if="sending" class="spin-icon" :size="16" />
                {{ sending ? 'Đang gửi...' : 'Phát hành ngay' }}
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <Teleport to="body">
      <Transition name="fade-glass">
        <div v-if="showDetailModal" class="modal-glass-backdrop" @click.self="closeDetailModal">
          <div class="modal-lux-content detail-modal" @click.stop>
            <div class="modal-header-lux">
              <h3>Chi tiết Thông báo</h3>
              <button class="btn-x" @click="closeDetailModal">
                <X :size="24" />
              </button>
            </div>

            <div v-if="detailLoading" class="detail-state">
              <Loader2 :size="24" class="spin-icon" />
              <p>Đang truy xuất dữ liệu...</p>
            </div>

            <div v-else-if="detailError" class="detail-state error-msg">
              <AlertTriangle :size="32" />
              <p>{{ detailError }}</p>
            </div>

            <div v-else-if="selectedDetail" class="modal-body-lux detail-body custom-scroll">
              <div class="detail-summary-card">
                <div class="detail-headline">
                  <div>
                    <span class="meta-kicker">THÔNG TIN TỔNG QUAN</span>
                    <h4>{{ selectedDetail.title || '(Không tiêu đề)' }}</h4>
                  </div>
                  <span class="manual-badge" :class="{ targeted: !selectedDetail.isGlobal }">
                    {{ selectedDetail.isGlobal ? 'Toàn hệ thống' : 'Nhắn riêng' }}
                  </span>
                </div>

                <div class="detail-form">
                  <div class="form-group-lux">
                    <label>Tiêu đề thông báo</label>
                    <div class="input-lux readonly-input">
                      {{ selectedDetail.title || '(Không tiêu đề)' }}
                    </div>
                  </div>

                  <div class="detail-row">
                    <div class="form-group-lux">
                      <label>Loại</label>
                      <div class="input-lux readonly-input">
                        {{ selectedDetail.type }}
                      </div>
                    </div>
                    <div class="form-group-lux">
                      <label>Ngày tạo</label>
                      <div class="input-lux readonly-input">
                        {{ formatDate(selectedDetail.createdAt) }}
                      </div>
                    </div>
                  </div>

                  <div class="form-group-lux">
                    <label>Phạm vi</label>
                    <div class="input-lux readonly-input">
                      {{ selectedDetail.isGlobal ? 'Toàn hệ thống' : 'Người dùng cụ thể' }}
                    </div>
                  </div>

                  <div class="form-group-lux">
                    <label>Nội dung</label>
                    <div class="input-lux readonly-input multiline">
                      {{ selectedDetail.content }}
                    </div>
                  </div>

                  <div v-if="selectedDetail.link" class="form-group-lux">
                    <label>Link đích</label>
                    <div class="input-lux readonly-input">
                      {{ selectedDetail.link }}
                    </div>
                  </div>
                </div>
              </div>

              <div class="detail-readers-card">
                <div class="reader-header">
                  <h4>Danh sách người đã đọc</h4>
                  <span class="reader-count">{{ selectedDetail.readCount }} lượt</span>
                </div>

                <div v-if="!selectedDetail.readers?.length" class="empty-reader-state">
                  Trống. Chưa có ai mở xem thông báo này.
                </div>

                <div v-else class="reader-list">
                  <div v-for="reader in selectedDetail.readers" :key="reader.accountID" class="reader-item">
                    <div class="reader-avatar-wrap">
                      <img v-if="reader.avatar" class="reader-avatar" :src="reader.avatar"
                        :alt="reader.username || 'User'" @error="handleAvatarError" />
                      <span v-else class="fallback-avatar">{{ getInitials(reader.username) }}</span>
                    </div>
                    <div class="reader-info">
                      <strong>{{ reader.username || ('ID ' + reader.accountID) }}</strong>
                      <p>{{ reader.email || 'Không có email' }}</p>
                    </div>
                    <span class="reader-time">{{ formatDate(reader.readAt) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="modal-footer-lux">
              <button @click="closeDetailModal" class="btn-lux btn-reject">Đóng lại</button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import {
  AlertTriangle,
  Eye,
  Layers,
  Loader2,
  Megaphone,
  Plus,
  Search,
  Send,
  Trash2,
  Users,
  X,
} from 'lucide-vue-next'
import api from '@/services/api'
import webSocketService from '@/services/webSocketService'
import {
  deleteAdminNotification,
  getAdminNotificationDetail,
  getAdminNotifications,
  getNotificationId,
  resolveNotificationLink,
  sendAdminNotificationToAll,
  sendAdminNotificationToUser
} from '@/services/notificationService'
import { toast } from '@/composables/useToast'

const notifications = ref([])
const loading = ref(true)
const error = ref(null)
const showCreateModal = ref(false)
const showDetailModal = ref(false)
const sending = ref(false)
const detailLoading = ref(false)
const detailError = ref('')
const selectedDetail = ref(null)
const members = ref([])
const membersLoading = ref(false)
const recipientPickerRef = ref(null)
const recipientKeyword = ref('')
const showRecipientDropdown = ref(false)

const form = reactive({
  sendTo: 'all',
  targetAccountIds: [],
  title: '',
  content: '',
  link: ''
})

const broadcastCount = computed(() => notifications.value.filter(n => n.isGlobal).length)
const targetedCount = computed(() => notifications.value.filter(n => !n.isGlobal).length)
const selectedRecipients = computed(() => {
  if (!Array.isArray(form.targetAccountIds)) return []
  return form.targetAccountIds
    .map(id => members.value.find(member => member.accountID === id))
    .filter(Boolean)
})

const filteredRecipients = computed(() => {
  const keyword = recipientKeyword.value.trim().toLowerCase()
  const selectedSet = new Set(form.targetAccountIds)
  return members.value
    .filter(member => !selectedSet.has(member.accountID))
    .filter(member => {
      if (!keyword) return true
      const haystack = `${member.accountID} ${member.username || ''} ${member.email || ''}`.toLowerCase()
      return haystack.includes(keyword)
    })
    .slice(0, 40)
})

const formatDate = (val) => {
  if (!val) return 'N/A'
  return new Date(val).toLocaleString('vi-VN', { hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric' })
}

const getRecipientLabel = (n) => {
  if (n.isGlobal) return 'Toàn bộ GoMeters'
  if (n.recipientUsername) return n.recipientUsername
  return `ID: ${n.accountID || '?'}`
}

const getInitials = (name = '') => {
  const chunks = String(name).trim().split(/\s+/).filter(Boolean)
  if (!chunks.length) return 'U'
  return chunks.slice(0, 2).map(chunk => chunk[0].toUpperCase()).join('')
}

const handleAvatarError = (event) => {
  if (event?.target) {
    event.target.src = '/logogoc.jpg'
  }
}

const normalizeSummary = (n = {}) => ({
  notificationID: getNotificationId(n),
  title: n.title,
  content: n.content,
  type: n.type || 'GENERAL',
  createdAt: n.createdAt,
  isGlobal: [true, 1, 'true', '1'].includes(n.isGlobal),
  link: resolveNotificationLink(n),
  accountID: n.accountID || null,
  recipientUsername: n.recipientUsername || null
})

const fetchNotifications = async () => {
  loading.value = true
  error.value = null
  try {
    const data = await getAdminNotifications()
    notifications.value = (data || []).map(normalizeSummary)
  } catch (e) {
    error.value = 'Hệ thống đang bận. Vui lòng thử lại sau.'
  } finally {
    loading.value = false
  }
}

const loadMemberAccounts = async () => {
  membersLoading.value = true
  try {
    const res = await api.get('/api/admin/member-accounts')
    members.value = (res.data || [])
      .filter(item => ![1, '1', true].includes(item.isAdmin))
      .map(item => ({
        accountID: Number(item.accountID),
        username: item.username || null,
        email: item.email || null,
        avatar: item.avatar || null
      }))
      .filter(item => Number.isFinite(item.accountID))
  } catch (e) {
    toast.error('Không tải được danh sách tài khoản.')
  } finally {
    membersLoading.value = false
  }
}

const openCreateModal = () => {
  form.sendTo = 'all'
  form.targetAccountIds = []
  form.title = ''
  form.content = ''
  form.link = ''
  recipientKeyword.value = ''
  showRecipientDropdown.value = false
  showCreateModal.value = true
  if (!members.value.length && !membersLoading.value) {
    loadMemberAccounts()
  }
}
const closeCreateModal = () => {
  showCreateModal.value = false
  showRecipientDropdown.value = false
}

const addRecipient = (member) => {
  if (!member?.accountID) return
  if (!form.targetAccountIds.includes(member.accountID)) {
    form.targetAccountIds.push(member.accountID)
  }
  recipientKeyword.value = ''
}

const removeRecipient = (accountID) => {
  form.targetAccountIds = form.targetAccountIds.filter(id => id !== accountID)
}

const openDetailModal = async (id) => {
  detailLoading.value = true
  detailError.value = ''
  selectedDetail.value = null
  showDetailModal.value = true
  try {
    const data = await getAdminNotificationDetail(id)
    selectedDetail.value = { ...data, link: resolveNotificationLink(data) }
  } catch (e) {
    detailError.value = 'Không thể tải chi tiết thông báo này.'
  } finally {
    detailLoading.value = false
  }
}
const closeDetailModal = () => { showDetailModal.value = false }

const submitNotification = async () => {
  if (!form.title.trim() || !form.content.trim()) {
    toast.warn('Vui lòng nhập Tiêu đề và Nội dung!')
    return
  }
  if (form.sendTo === 'user' && form.targetAccountIds.length === 0) {
    toast.warn('Vui lòng chọn ít nhất 1 người nhận!')
    return
  }

  sending.value = true
  try {
    const payload = {
      title: form.title.trim(),
      content: form.content.trim(),
      type: 'GENERAL',
      link: form.link?.trim() || null
    }

    if (form.sendTo === 'all') {
      await sendAdminNotificationToAll(payload)
      toast.success('Đã phát hành thông báo toàn hệ thống!')
    } else {
      const queue = [...form.targetAccountIds]
      const results = await Promise.allSettled(queue.map(accountID => sendAdminNotificationToUser(accountID, payload)))
      const successCount = results.filter(result => result.status === 'fulfilled').length
      const failedCount = results.length - successCount

      if (successCount === 0) {
        throw new Error('Không gửi được thông báo cho người nhận nào.')
      }

      if (failedCount > 0) {
        toast.warn(`Đã gửi ${successCount}/${results.length} người nhận. Có ${failedCount} người gửi thất bại.`)
      } else {
        toast.success(`Đã gửi thông báo cho ${successCount} người dùng.`)
      }
    }
    closeCreateModal()
    fetchNotifications()
  } catch (e) {
    toast.error('Gửi thất bại. Vui lòng kiểm tra lại.')
  } finally {
    sending.value = false
  }
}

const deleteNotificationItem = async (n) => {
  if (!confirm(`Xác nhận xóa thông báo: "${n.title}"?`)) return
  try {
    await deleteAdminNotification(n.notificationID)
    notifications.value = notifications.value.filter(item => item.notificationID !== n.notificationID)
    toast.success('Xóa thông báo thành công.')
  } catch (e) {
    toast.error('Không thể xóa thông báo.')
  }
}

const handleRealtimeAlert = (event) => {
  const dto = event.detail
  if (String(dto.type || '').toUpperCase() === 'ADMIN_MANUAL') {
    const id = getNotificationId(dto)
    if (id && !notifications.value.some(n => n.notificationID === id)) {
      notifications.value.unshift(normalizeSummary(dto))
    }
  }
}

const handleOutsideRecipient = (event) => {
  if (!recipientPickerRef.value) return
  if (!recipientPickerRef.value.contains(event.target)) {
    showRecipientDropdown.value = false
  }
}

onMounted(() => {
  fetchNotifications()
  webSocketService.connect()
  window.addEventListener('admin-alert', handleRealtimeAlert)
  document.addEventListener('mousedown', handleOutsideRecipient)
})

onUnmounted(() => {
  window.removeEventListener('admin-alert', handleRealtimeAlert)
  document.removeEventListener('mousedown', handleOutsideRecipient)
})
</script>

<style scoped lang="scss" src="./NotificationManagement.scss"></style>
