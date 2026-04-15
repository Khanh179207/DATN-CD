<template>
  <div class="notification-sovereign-wrapper">
    <div class="page-header-lux">
      <div>
        <h1 class="page-title">Quản lý Thông báo</h1>
        <p class="page-subtitle">Trung tâm tạo, theo dõi và kiểm soát thông báo hệ thống của GOMET</p>
      </div>
      <button class="btn-action-lux" @click="openCreateModal">
        <Plus :size="20" stroke-width="3" />
        Tạo Thông báo
      </button>
    </div>

    <div class="stats-grid" v-if="!loading && !error">
      <div class="stat-card">
        <div class="icon-wrap all"><Layers :size="22" /></div>
        <div class="stat-info">
          <span class="label">Tổng Thông báo</span>
          <h3 class="value">{{ notifications.length }}</h3>
        </div>
      </div>

      <div class="stat-card">
        <div class="icon-wrap broadcast"><Send :size="22" /></div>
        <div class="stat-info">
          <span class="label">Broadcast</span>
          <h3 class="value">{{ broadcastCount }}</h3>
        </div>
      </div>

      <div class="stat-card highlight-card">
        <div class="icon-wrap target"><Zap :size="22" /></div>
        <div class="stat-info">
          <span class="label">Nhắm riêng</span>
          <h3 class="value">{{ targetedCount }}</h3>
        </div>
      </div>
    </div>

    <div v-if="loading" class="grid-list">
      <div v-for="n in 6" :key="n" class="lux-noti-card skeleton">
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
      <div
        v-for="notification in notifications"
        :key="notification.notificationID"
        class="lux-noti-card clickable-card"
        @click="openDetailModal(notification.notificationID)"
      >
        <div class="card-img-wrap">
          <div class="hero-icon">
            <Megaphone :size="42" />
          </div>

          <div class="post-count-badge">
            {{ notification.isGlobal ? 'Broadcast' : 'Targeted' }}
          </div>

          <div class="hidden-badge" v-if="notification.type">{{ notification.type }}</div>

          <div class="overlay-actions">
            <button class="btn-act view" @click.stop="openDetailModal(notification.notificationID)" title="Xem chi tiết">
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
            <span class="ratio-pct">{{ getRecipientLabel(notification) }}</span>
          </div>
          <p class="content-preview">{{ notification.content || 'Không có nội dung' }}</p>
          <div class="progress-bar">
            <div class="fill" :style="{ width: notification.isGlobal ? '100%' : '52%' }"></div>
          </div>
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
                <Megaphone :size="18" style="vertical-align:middle;margin-right:8px" />
                Khởi tạo Thông báo
              </h3>
              <button class="btn-x" @click="closeCreateModal"><X :size="24" /></button>
            </div>

            <div class="modal-body-lux">
              <div class="form-group-lux">
                <label>Người nhận</label>
                <div class="recipient-switch">
                  <label><input type="radio" value="all" v-model="form.sendTo" /> Toàn bộ người dùng</label>
                  <label><input type="radio" value="user" v-model="form.sendTo" /> Một tài khoản cụ thể</label>
                </div>
              </div>

              <div v-if="form.sendTo === 'user'" class="form-group-lux">
                <label>Tài khoản đích <span class="req">*</span></label>
                <input v-model="form.targetAccountId" type="number" class="input-lux" placeholder="VD: 123">
              </div>

              <div class="form-group-lux">
                <label>Tiêu đề <span class="req">*</span></label>
                <input v-model="form.title" type="text" class="input-lux" placeholder="VD: Bảo trì hệ thống đêm nay...">
              </div>

              <div class="form-group-lux">
                <label>Nội dung <span class="req">*</span></label>
                <textarea v-model="form.content" rows="5" class="input-lux" placeholder="Nhập nội dung thông báo..."></textarea>
              </div>

              <div class="form-group-lux">
                <label>Loại</label>
                <select v-model="form.type" class="input-lux">
                  <option value="GENERAL">General</option>
                  <option value="MAINTENANCE">Maintenance</option>
                  <option value="PROMOTION">Promotion</option>
                </select>
              </div>

              <div class="form-group-lux">
                <label>Link điều hướng</label>
                <input v-model="form.link" type="text" class="input-lux" placeholder="/post/123 hoặc /admin/...">
              </div>
            </div>

            <div class="modal-footer-lux">
              <button @click="closeCreateModal" class="btn-lux btn-reject">Đóng</button>
              <button @click="submitNotification" class="btn-lux btn-resolve" :disabled="sending">
                <span v-if="sending" class="spinner"></span>
                {{ sending ? 'Đang gửi...' : 'Hoàn tất' }}
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
              <button class="btn-x" @click="closeDetailModal"><X :size="24" /></button>
            </div>

            <div v-if="detailLoading" class="detail-state">
              <Loader2 :size="18" class="spin-icon" />
              <span>Đang tải chi tiết thông báo...</span>
            </div>

            <div v-else-if="detailError" class="detail-state error-msg">
              <AlertTriangle :size="18" />
              <span>{{ detailError }}</span>
            </div>

            <div v-else-if="selectedDetail" class="modal-body-lux detail-body">
              <div class="detail-summary-card">
                <div class="detail-headline">
                  <div>
                    <span class="meta-kicker">Thông tin cơ bản</span>
                    <h4>{{ selectedDetail.title || '(Không có tiêu đề)' }}</h4>
                  </div>
                  <span class="manual-badge" :class="{ targeted: !selectedDetail.isGlobal }">
                    {{ selectedDetail.isGlobal ? 'Broadcast' : 'Targeted' }}
                  </span>
                </div>

                <div class="detail-grid">
                  <div class="detail-box">
                    <span class="label">Loại</span>
                    <strong>{{ selectedDetail.type || 'GENERAL' }}</strong>
                  </div>
                  <div class="detail-box">
                    <span class="label">Thời gian tạo</span>
                    <strong>{{ formatDate(selectedDetail.createdAt) }}</strong>
                  </div>
                  <div class="detail-box">
                    <span class="label">Người nhận</span>
                    <strong>{{ selectedDetail.isGlobal ? 'Toàn bộ người dùng' : (selectedDetail.recipientUsername || 'Một tài khoản cụ thể') }}</strong>
                  </div>
                  <div class="detail-box">
                    <span class="label">Đã đọc</span>
                    <strong>{{ selectedDetail.readCount || 0 }} người</strong>
                  </div>
                </div>

                <div class="detail-content-block">
                  <span class="label">Nội dung</span>
                  <p>{{ selectedDetail.content || 'Không có nội dung' }}</p>
                </div>

                <div class="detail-content-block">
                  <span class="label">Link</span>
                  <p>{{ selectedDetail.link || 'Không có link điều hướng' }}</p>
                </div>
              </div>

              <div class="detail-readers-card">
                <div class="reader-header">
                  <div>
                    <span class="meta-kicker">Người đã đọc</span>
                    <h4>Danh sách tài khoản đã đọc thông báo</h4>
                  </div>
                  <span class="reader-count">{{ selectedDetail.readCount || 0 }}</span>
                </div>

                <div v-if="!selectedDetail.readers?.length" class="empty-reader-state">
                  Chưa có người dùng nào đọc thông báo này.
                </div>

                <div v-else class="reader-list">
                  <div v-for="reader in selectedDetail.readers" :key="reader.accountID" class="reader-item">
                    <div class="reader-avatar-wrap">
                      <img v-if="reader.avatar" :src="reader.avatar" :alt="reader.username || 'User'" class="reader-avatar">
                      <div v-else class="reader-avatar fallback-avatar">{{ getInitial(reader.username) }}</div>
                    </div>
                    <div class="reader-info">
                      <strong>{{ reader.username || 'Unknown user' }}</strong>
                      <p>{{ reader.email || 'Chưa có email' }}</p>
                    </div>
                    <span class="reader-time">{{ formatDate(reader.readAt) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="modal-footer-lux">
              <button @click="closeDetailModal" class="btn-lux btn-reject">Đóng</button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import {
  AlertTriangle,
  Eye,
  Layers,
  Loader2,
  Megaphone,
  Plus,
  Send,
  Trash2,
  X,
  Zap
} from 'lucide-vue-next'
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
const selectedNotificationId = ref(null)

const form = ref({
  sendTo: 'all',
  targetAccountId: null,
  title: '',
  content: '',
  type: 'GENERAL',
  link: null
})

const broadcastCount = computed(() => notifications.value.filter(item => item.isGlobal).length)
const targetedCount = computed(() => notifications.value.filter(item => !item.isGlobal).length)

const formatDate = (value) => {
  if (!value) return 'N/A'
  try {
    return new Date(value).toLocaleString('vi-VN')
  } catch {
    return String(value)
  }
}

const getInitial = (value) => String(value || 'U').trim().charAt(0).toUpperCase()

const getRecipientLabel = (notification) => {
  if (notification.isGlobal) return 'Toàn bộ người dùng'
  if (notification.recipientUsername) return notification.recipientUsername
  if (notification.accountID) return `Tài khoản #${notification.accountID}`
  return 'Nhắm riêng'
}

const normalizeSummary = (notification = {}) => ({
  notificationID: getNotificationId(notification),
  title: notification.title,
  content: notification.content,
  type: notification.type || 'GENERAL',
  createdAt: notification.createdAt,
  isGlobal: notification.isGlobal === true || notification.isGlobal === 1 || notification.isGlobal === '1',
  link: resolveNotificationLink(notification),
  accountID: notification.accountID || null,
  recipientUsername: notification.recipientUsername || null
})

const normalizeDetail = (detail = {}) => ({
  notificationID: detail.notificationID,
  title: detail.title,
  content: detail.content,
  type: detail.type || 'GENERAL',
  createdAt: detail.createdAt,
  isGlobal: detail.isGlobal === true || detail.isGlobal === 1 || detail.isGlobal === '1',
  link: resolveNotificationLink(detail),
  accountID: detail.accountID || null,
  recipientUsername: detail.recipientUsername || null,
  recipientEmail: detail.recipientEmail || null,
  recipientAvatar: detail.recipientAvatar || null,
  readCount: detail.readCount || 0,
  readers: (detail.readers || []).map(reader => ({
    accountID: reader.accountID,
    username: reader.username,
    email: reader.email,
    avatar: reader.avatar,
    readAt: reader.readAt
  }))
})

const fetchNotifications = async () => {
  loading.value = true
  error.value = null

  try {
    const data = await getAdminNotifications()
    notifications.value = (data || []).map(normalizeSummary)
  } catch (e) {
    error.value = 'Hệ thống thông báo đang bận, vui lòng thử lại sau.'
  } finally {
    loading.value = false
  }
}

const openCreateModal = () => {
  form.value = {
    sendTo: 'all',
    targetAccountId: null,
    title: '',
    content: '',
    type: 'GENERAL',
    link: null
  }
  showCreateModal.value = true
}

const closeCreateModal = () => {
  showCreateModal.value = false
}

const openDetailModal = async (notificationID) => {
  selectedNotificationId.value = notificationID
  detailLoading.value = true
  detailError.value = ''
  selectedDetail.value = null
  showDetailModal.value = true

  try {
    const data = await getAdminNotificationDetail(notificationID)
    selectedDetail.value = normalizeDetail(data)
  } catch (e) {
    detailError.value = 'Không thể tải chi tiết thông báo lúc này.'
  } finally {
    detailLoading.value = false
  }
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedDetail.value = null
  detailError.value = ''
  selectedNotificationId.value = null
}

const submitNotification = async () => {
  if (!form.value.title?.trim() || !form.value.content?.trim()) {
    toast.warn('Vui lòng nhập đầy đủ tiêu đề và nội dung thông báo!')
    return
  }

  if (form.value.sendTo === 'user') {
    const accountId = Number(form.value.targetAccountId)
    if (!accountId || Number.isNaN(accountId) || accountId <= 0) {
      toast.warn('Vui lòng nhập Account ID hợp lệ!')
      return
    }
  }

  sending.value = true

  try {
    const payload = {
      title: form.value.title.trim(),
      content: form.value.content.trim(),
      type: form.value.type,
      postID: null,
      link: form.value.link?.trim() || null
    }

    if (form.value.sendTo === 'all') {
      await sendAdminNotificationToAll(payload)
      toast.success('Đã gửi thông báo broadcast thành công!')
    } else {
      const accountId = Number(form.value.targetAccountId)
      await sendAdminNotificationToUser(accountId, payload)
      toast.success(`Đã gửi thông báo cho tài khoản #${accountId}!`)
    }

    closeCreateModal()
    await fetchNotifications()
  } catch (e) {
    toast.error(e.response?.data?.message || 'Gửi thông báo thất bại.')
  } finally {
    sending.value = false
  }
}

const deleteNotificationItem = async (notification) => {
  if (!window.confirm(`Bạn có chắc chắn muốn xóa thông báo "${notification.title || 'không tiêu đề'}" không?`)) {
    return
  }

  try {
    await deleteAdminNotification(notification.notificationID)
    notifications.value = notifications.value.filter(item => item.notificationID !== notification.notificationID)

    if (selectedNotificationId.value === notification.notificationID) {
      closeDetailModal()
    }

    toast.success('Đã xóa thông báo thành công.')
  } catch (e) {
    toast.error(e.response?.data?.message || 'Không thể xóa thông báo lúc này.')
  }
}

const handleRealtimeAdminAlert = (event) => {
  const dto = event.detail || {}
  if (String(dto.type || '').toUpperCase() !== 'ADMIN_MANUAL') return

  const id = getNotificationId(dto)
  if (!id || notifications.value.some(item => item.notificationID === id)) return

  notifications.value.unshift(normalizeSummary(dto))
}

const handleRealtimeAdminNotification = (event) => {
  handleRealtimeAdminAlert(event)
}

onMounted(async () => {
  await fetchNotifications()
  webSocketService.connect()
  window.addEventListener('admin-alert', handleRealtimeAdminAlert)
  window.addEventListener('admin-notification', handleRealtimeAdminNotification)
})

onUnmounted(() => {
  window.removeEventListener('admin-alert', handleRealtimeAdminAlert)
  window.removeEventListener('admin-notification', handleRealtimeAdminNotification)
})
</script>

<style scoped lang="scss" src="./NotificationManagement.scss"></style>
