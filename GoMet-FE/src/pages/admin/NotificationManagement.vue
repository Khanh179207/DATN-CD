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

    <!-- Stats Dashboard -->
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

    <!-- Content List -->
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
      <div v-for="notification in notifications" :key="notification.notificationID" class="lux-noti-card clickable-card"
        @click="openDetailModal(notification.notificationID)">
        <div class="card-img-wrap">
          <div class="hero-icon">
            <Megaphone :size="42" />
          </div>

          <div class="post-count-badge" :class="{ 'is-global': notification.isGlobal }">
            {{ notification.isGlobal ? 'Toàn hệ thống' : 'Nhắm riêng' }}
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
            <span class="ratio-pct text-truncate">{{ getRecipientLabel(notification) }}</span>
          </div>
          <p class="content-preview">{{ notification.content || 'Không có nội dung' }}</p>
          <div class="progress-bar">
            <div class="fill" :style="{ width: notification.isGlobal ? '100%' : '52%' }"></div>
          </div>
          <div class="view-posts-hint">{{ formatDate(notification.createdAt) }} →</div>
        </div>
      </div>

      <div v-if="notifications.length === 0" class="empty-state-lux">
        <div class="empty-icon">📭</div>
        <p>Hệ thống chưa có thông báo thủ công nào. Hãy tạo thông báo đầu tiên Sếp nhé!</p>
      </div>
    </div>

    <!-- Create Modal -->
    <Teleport to="body">
      <Transition name="fade-glass">
        <div v-if="showCreateModal" class="modal-glass-backdrop" @click.self="closeCreateModal">
          <div class="modal-lux-content" @click.stop>
            <div class="modal-header-lux">
              <h3>
                <Plus :size="18" style="vertical-align:middle;margin-right:8px" />
                Khởi tạo Thông báo
              </h3>
              <button class="btn-x" @click="closeCreateModal"><X :size="24" /></button>
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
                    <span>Cá nhân</span>
                  </label>
                </div>
              </div>

              <div v-if="form.sendTo === 'user'" class="form-group-lux animate-in">
                <label>Account ID người nhận <span class="req">*</span></label>
                <input v-model="form.targetAccountId" type="number" class="input-lux" placeholder="Ví dụ: 101">
              </div>

              <div class="form-group-lux">
                <label>Tiêu đề thông báo <span class="req">*</span></label>
                <input v-model="form.title" type="text" class="input-lux" placeholder="VD: Lịch bảo trì hệ thống GoMet...">
              </div>

              <div class="form-group-lux">
                <label>Nội dung chi tiết <span class="req">*</span></label>
                <textarea v-model="form.content" rows="4" class="input-lux" placeholder="Nhập nội dung thông báo..."></textarea>
              </div>

              <div class="form-grid-lux">
                <div class="form-group-lux">
                  <label>Phân loại</label>
                  <select v-model="form.type" class="input-lux">
                    <option value="GENERAL">General</option>
                    <option value="MAINTENANCE">Maintenance</option>
                    <option value="PROMOTION">Promotion</option>
                    <option value="ADMIN_MANUAL">System Alert</option>
                  </select>
                </div>
                <div class="form-group-lux">
                  <label>Đường dẫn điều hướng</label>
                  <input v-model="form.link" type="text" class="input-lux" placeholder="/post/10 hoặc /news">
                </div>
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

    <!-- Detail Modal -->
    <Teleport to="body">
      <Transition name="fade-glass">
        <div v-if="showDetailModal" class="modal-glass-backdrop" @click.self="closeDetailModal">
          <div class="modal-lux-content detail-modal" @click.stop>
            <div class="modal-header-lux">
              <h3>Chi tiết Thông báo</h3>
              <button class="btn-x" @click="closeDetailModal"><X :size="24" /></button>
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
                    {{ selectedDetail.isGlobal ? 'Broadcast' : 'Targeted' }}
                  </span>
                </div>

                <div class="detail-grid-v2">
                  <div class="detail-box">
                    <span class="lbl">Loại</span>
                    <span class="val">{{ selectedDetail.type }}</span>
                  </div>
                  <div class="detail-box">
                    <span class="lbl">Ngày tạo</span>
                    <span class="val">{{ formatDate(selectedDetail.createdAt) }}</span>
                  </div>
                  <div class="detail-box">
                    <span class="lbl">Phạm vi</span>
                    <span class="val">{{ selectedDetail.isGlobal ? 'Toàn hệ thống' : 'Người dùng cụ thể' }}</span>
                  </div>
                  <div class="detail-box">
                    <span class="lbl">Lượt xem</span>
                    <span class="val count">{{ selectedDetail.readCount || 0 }}</span>
                  </div>
                </div>

                <div class="detail-block">
                  <label>Nội dung</label>
                  <p>{{ selectedDetail.content }}</p>
                </div>

                <div v-if="selectedDetail.link" class="detail-block">
                  <label>Link đích</label>
                  <code>{{ selectedDetail.link }}</code>
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

                <div v-else class="reader-list-lux">
                  <div v-for="reader in selectedDetail.readers" :key="reader.accountID" class="reader-item-lux">
                    <img :src="reader.avatar || 'https://ui-avatars.com/api/?name='+reader.username" class="r-avatar">
                    <div class="r-info">
                      <strong>{{ reader.username }}</strong>
                      <span>{{ reader.email }}</span>
                    </div>
                    <div class="r-time">{{ formatDate(reader.readAt) }}</div>
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
  type: 'ADMIN_MANUAL',
  link: null
})

const broadcastCount = computed(() => notifications.value.filter(n => n.isGlobal).length)
const targetedCount = computed(() => notifications.value.filter(n => !n.isGlobal).length)

const formatDate = (val) => {
  if (!val) return 'N/A'
  return new Date(val).toLocaleString('vi-VN', { hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric' })
}

const getRecipientLabel = (n) => {
  if (n.isGlobal) return 'Toàn bộ GoMeters'
  if (n.recipientUsername) return n.recipientUsername
  return `ID: ${n.accountID || '?'}`
}

const normalizeSummary = (n = {}) => ({
  notificationID: getNotificationId(n),
  title: n.title,
  content: n.content,
  type: n.type || 'ADMIN_MANUAL',
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

const openCreateModal = () => {
  form.value = { sendTo: 'all', targetAccountId: null, title: '', content: '', type: 'ADMIN_MANUAL', link: null }
  showCreateModal.value = true
}
const closeCreateModal = () => { showCreateModal.value = false }

const openDetailModal = async (id) => {
  selectedNotificationId.value = id
  detailLoading.value = true
  detailError.value = ''
  selectedDetail.value = null
  showDetailModal.value = true
  try {
    const data = await getAdminNotificationDetail(id)
    selectedDetail.value = data
  } catch (e) {
    detailError.value = 'Không thể tải chi tiết thông báo này.'
  } finally {
    detailLoading.value = false
  }
}
const closeDetailModal = () => { showDetailModal.value = false }

const submitNotification = async () => {
  if (!form.value.title.trim() || !form.value.content.trim()) {
    toast.warn('Vui lòng nhập Tiêu đề và Nội dung!')
    return
  }
  if (form.value.sendTo === 'user' && !form.value.targetAccountId) {
    toast.warn('Vui lòng nhập ID người nhận!')
    return
  }

  sending.value = true
  try {
    const payload = {
      title: form.value.title.trim(),
      content: form.value.content.trim(),
      type: form.value.type,
      link: form.value.link?.trim() || null
    }

    if (form.value.sendTo === 'all') {
      await sendAdminNotificationToAll(payload)
      toast.success('Đã phát hành thông báo toàn hệ thống!')
    } else {
      await sendAdminNotificationToUser(form.value.targetAccountId, payload)
      toast.success('Đã gửi thông báo cho người dùng thành công!')
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

onMounted(() => {
  fetchNotifications()
  webSocketService.connect()
  window.addEventListener('admin-alert', handleRealtimeAlert)
})

onUnmounted(() => {
  window.removeEventListener('admin-alert', handleRealtimeAlert)
})
</script>

<style scoped lang="scss" src="./NotificationManagement.scss"></style>
