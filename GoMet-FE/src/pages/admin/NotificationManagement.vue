<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2 class="title">
          <Bell :size="20" style="vertical-align:middle;margin-right:6px" /> {{ t('admin.notifications.title') }}
        </h2>
        <p class="subtitle">{{ t('admin.notifications.subtitle') }}</p>
      </div>
      <button @click="openCreate" class="btn-create">+ {{ t('admin.notifications.create') }}</button>
    </div>

    <!-- Loading / Error -->
    <div v-if="loading" class="empty-state">
      <Loader2 :size="16" class="spin-icon" /> {{ t('admin.notifications.loading') }}
    </div>
    <div v-else-if="error" class="empty-state error-msg">
      <AlertTriangle :size="16" /> {{ error }}
    </div>

    <div v-else>
      <div v-if="notifs.length === 0" class="empty-state">{{ t('admin.notifications.empty') }}</div>
      <div v-for="n in notifs" :key="n.notificationID" class="notif-item">
        <div class="icon-box" :class="getTypeClass(n.type)">
          <component
            :is="{ MAINTENANCE: Wrench, PROMOTION: PartyPopper, ACHIEVEMENT: Trophy, GENERAL: Megaphone }[n.type] || Megaphone"
            :size="18" />
        </div>
        <div class="content">
          <h4>{{ n.title }}</h4>
          <span class="manual-badge">{{ t('admin.notifications.manual_badge') }}</span>
          <p>{{ n.content }}</p>
          <small>{{ formatDate(n.createdAt) }} · <span class="type-tag">{{ n.type || 'GENERAL' }}</span></small>
        </div>
        <button @click="deleteNotif(n.notificationID)" class="btn-del" :title="t('admin.notifications.delete_title')">
          <Trash2 :size="15" />
        </button>
      </div>
    </div>

    <!-- CREATE MODAL -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-box">
        <button class="btn-close" @click="closeModal">✕</button>
        <h3>
          <Megaphone :size="18" style="vertical-align:middle;margin-right:6px" /> {{ t('admin.notifications.broadcast_title') }}
        </h3>
        <p class="modal-hint">{{ t('admin.notifications.broadcast_hint') }}</p>

        <div class="form-group">
          <label>{{ t('admin.notifications.title_label') }} *</label>
          <input v-model="form.title" type="text" :placeholder="t('admin.notifications.title_placeholder')" />
        </div>
        <div class="form-group">
          <label>{{ t('admin.notifications.content_label') }} *</label>
          <textarea v-model="form.content" rows="4" :placeholder="t('admin.notifications.content_placeholder')"></textarea>
        </div>
        <div class="form-group">
          <label>{{ t('admin.notifications.type_label') }}</label>
          <select v-model="form.type">
            <option value="GENERAL">{{ t('admin.notifications.types.general') }}</option>
            <option value="MAINTENANCE">{{ t('admin.notifications.types.maintenance') }}</option>
            <option value="PROMOTION">{{ t('admin.notifications.types.promotion') }}</option>
            <option value="ACHIEVEMENT">{{ t('admin.notifications.types.achievement') }}</option>
          </select>
        </div>

        <div class="modal-footer">
          <button @click="closeModal" class="btn-cancel">{{ t('common.cancel') }}</button>
          <button @click="sendNotif" :disabled="sending" class="btn-send">
            {{ sending ? t('admin.notifications.sending') : t('admin.notifications.send_all') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <transition name="toast">
      <div v-if="toast.show" :class="['toast-msg', toast.type]">{{ toast.msg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Bell, Loader2, AlertTriangle, Trash2, Wrench, PartyPopper, Trophy, Megaphone, Send } from 'lucide-vue-next'
import api from '@/services/api'
import { useI18n } from 'vue-i18n'
import { formatLocaleDateTime } from '@/i18n'

const notifs = ref([])
const loading = ref(false)
const error = ref('')
const showModal = ref(false)
const sending = ref(false)
const { t } = useI18n()

const form = ref({ title: '', content: '', type: 'ADMIN_MANUAL' })

const toast = ref({ show: false, msg: '', type: 'success' })
const showToast = (msg, type = 'success') => {
  toast.value = { show: true, msg, type }
  setTimeout(() => toast.value.show = false, 3500)
}

const formatDate = (d) => {
  if (!d) return ''
  return formatLocaleDateTime(d)
}

const getTypeIcon = (type) => {
  return type || 'GENERAL'
}

const getTypeClass = (type) => {
  const map = { MAINTENANCE: 'bg-orange', PROMOTION: 'bg-green', ACHIEVEMENT: 'bg-yellow', GENERAL: 'bg-blue' }
  return map[type] || 'bg-blue'
}

const fetchNotifs = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await api.get('/api/admin/notifications')
    notifs.value = (res.data || []).filter(n => n.type === 'ADMIN_MANUAL')
  } catch (e) {
    error.value = t('admin.notifications.load_failed_prefix').replace(/[:：]\s*$/, '')
  } finally {
    loading.value = false
  }
}

const deleteNotif = async (id) => {
  if (!confirm(t('admin.notifications.delete_confirm'))) return
  try {
    await api.delete(`/api/admin/notifications/${id}`)
    notifs.value = notifs.value.filter(n => n.notificationID !== id)
    showToast(t('admin.notifications.delete_success'))
  } catch (e) {
    showToast(t('admin.notifications.delete_failed_prefix').replace(/[:：]\s*$/, ''), 'error')
  }
}

const openCreate = () => {
  form.value = { title: '', content: '', type: 'GENERAL' }
  showModal.value = true
}

const closeModal = () => { showModal.value = false }

const sendNotif = async () => {
  if (!form.value.title.trim() || !form.value.content.trim()) {
    showToast(t('admin.notifications.required'), 'error')
    return
  }
  sending.value = true
  try {
    await api.post('/api/admin/notifications/all', form.value)
    showToast(t('admin.notifications.send_success'))
    closeModal()
    fetchNotifs()
  } catch (e) {
    showToast(t('admin.notifications.send_failed_prefix').replace(/[:：]\s*$/, ''), 'error')
  } finally {
    sending.value = false
  }
}

onMounted(fetchNotifs)
</script>

<style scoped>
.page-container {
  padding: 25px;
  font-family: 'Quicksand', sans-serif;
  color: #334155;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 25px;
}

.title {
  font-size: 1.8rem;
  font-weight: 800;
  color: #1E293B;
  margin: 0;
}

.subtitle {
  color: #64748B;
  margin: 5px 0 0;
}

.btn-create {
  background: linear-gradient(135deg, #3B82F6, #6366F1);
  color: white;
  border: none;
  padding: 11px 22px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 700;
  font-family: inherit;
  font-size: 0.95rem;
  transition: 0.2s;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.btn-create:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(99, 102, 241, 0.4);
}

.empty-state {
  text-align: center;
  padding: 50px;
  color: #94A3B8;
  font-style: italic;
  font-size: 1.05rem;
  background: white;
  border-radius: 12px;
}

.error-msg {
  color: #DC2626 !important;
}

.notif-item {
  display: flex;
  gap: 18px;
  background: white;
  padding: 18px 20px;
  border-radius: 14px;
  margin-bottom: 12px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #F1F5F9;
  transition: 0.2s;
}

.notif-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.07);
}

.icon-box {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.3rem;
  flex-shrink: 0;
}

.bg-blue {
  background: #EFF6FF;
}

.bg-orange {
  background: #FFF7ED;
}

.bg-green {
  background: #F0FDF4;
}

.bg-yellow {
  background: #FEFCE8;
}

.content {
  flex: 1;
}

.content h4 {
  margin: 0 0 4px 0;
  font-size: 1rem;
  color: #0F172A;
}

.content p {
  margin: 0 0 6px 0;
  color: #64748B;
  font-size: 0.9rem;
}

.content small {
  color: #94A3B8;
  font-size: 0.8rem;
}

.type-tag,
.manual-badge {
  background: #F1F5F9;
  color: #475569;
  padding: 1px 8px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 0.72rem;
}

.manual-badge {
  background: #DBEAFE;
  color: #1E40AF;
}

.btn-del {
  background: transparent;
  border: 1px solid #EF4444;
  color: #EF4444;
  padding: 7px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s;
  font-size: 1rem;
}

.btn-del:hover {
  background: #EF4444;
  color: white;
}

/* MODAL */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(3px);
}

.modal-box {
  background: white;
  width: 500px;
  max-width: 95%;
  border-radius: 20px;
  padding: 30px;
  position: relative;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  animation: slideUp 0.3s ease;
}

.btn-close {
  position: absolute;
  top: 15px;
  right: 15px;
  background: #F1F5F9;
  border: none;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  font-weight: bold;
  transition: 0.2s;
}

.btn-close:hover {
  background: #EF4444;
  color: white;
}

.modal-box h3 {
  margin: 0 0 6px;
  font-size: 1.3rem;
  color: #0F172A;
}

.modal-hint {
  color: #64748B;
  font-size: 0.875rem;
  margin: 0 0 22px;
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: block;
  font-weight: 700;
  font-size: 0.875rem;
  color: #374151;
  margin-bottom: 6px;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #CBD5E1;
  border-radius: 8px;
  font-family: inherit;
  font-size: 0.95rem;
  color: #0F172A;
  box-sizing: border-box;
  transition: 0.2s;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #3B82F6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-group textarea {
  resize: vertical;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.btn-cancel {
  background: #F1F5F9;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 700;
  color: #64748B;
  font-family: inherit;
  transition: 0.2s;
}

.btn-cancel:hover {
  background: #E2E8F0;
}

.btn-send {
  background: linear-gradient(135deg, #3B82F6, #6366F1);
  color: white;
  border: none;
  padding: 10px 22px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 700;
  font-family: inherit;
  transition: 0.2s;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.btn-send:hover:not(:disabled) {
  transform: translateY(-2px);
}

.btn-send:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Toast */
.toast-msg {
  position: fixed;
  bottom: 30px;
  right: 30px;
  padding: 14px 24px;
  border-radius: 12px;
  font-weight: 700;
  z-index: 9999;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.12);
}

.toast-msg.success {
  background: #DCFCE7;
  color: #15803D;
  border: 1px solid #86EFAC;
}

.toast-msg.error {
  background: #FEE2E2;
  color: #B91C1C;
  border: 1px solid #FCA5A5;
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>