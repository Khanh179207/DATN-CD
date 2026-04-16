<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2 class="title">
          <Bell :size="22" style="vertical-align:middle;margin-right:8px; color: #3B82F6;" /> 
          Thông Báo Hệ Thống
        </h2>
        <p class="subtitle">Quản lý và phát thông báo đến toàn bộ người dùng</p>
      </div>
      <button @click="openCreate" class="btn-create">
        <Send :size="16" style="margin-right: 6px;" /> Tạo thông báo
      </button>
    </div>

    <div v-if="loading" class="empty-state">
      <Loader2 :size="20" class="spin-icon" style="margin-right: 8px;" /> Đang tải danh sách thông báo...
    </div>
    <div v-else-if="error" class="empty-state error-msg">
      <AlertTriangle :size="20" style="margin-right: 8px;" /> {{ error }}
    </div>

    <div v-else>
      <div v-if="notifs.length === 0" class="empty-state">
        <div class="empty-icon">📭</div>
        Chưa có thông báo nào. Hãy tạo thông báo đầu tiên
      </div>
      
      <div v-for="n in notifs" :key="n.notificationID" class="notif-item">
        <div class="icon-box bg-blue">
          <Megaphone :size="20" />
        </div>
        <div class="content">
          <h4>{{ n.title }}</h4>
          <span class="manual-badge">Thông báo hệ thống</span>
          <p>{{ n.content }}</p>
          <small>{{ formatDate(n.createdAt) }}</small>
        </div>
      </div>
    </div>

    <transition name="modal">
      <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
        <div class="modal-box">
          <button class="btn-close" @click="closeModal">✕</button>
          <h3>
            <Megaphone :size="20" style="vertical-align:middle;margin-right:8px; color: #3B82F6;" /> 
            Phát Thông Báo Mới
          </h3>
          <p class="modal-hint">Thông báo này sẽ được hiển thị với TẤT CẢ người dùng trên hệ thống.</p>

          <div class="form-group">
            <label>Tiêu đề <span class="required">*</span></label>
            <input v-model="form.title" type="text" placeholder="VD: Bảo trì hệ thống lúc 0h đêm nay..." />
          </div>
          <div class="form-group">
            <label>Nội dung <span class="required">*</span></label>
            <textarea v-model="form.content" rows="4" placeholder="Nhập chi tiết nội dung cần thông báo cho người dùng..."></textarea>
          </div>

          <div class="modal-footer">
            <button @click="closeModal" class="btn-cancel">Hủy bỏ</button>
            <button @click="sendNotif" :disabled="sending" class="btn-send">
              <Loader2 v-if="sending" :size="16" class="spin-icon" style="margin-right: 6px;" />
              {{ sending ? 'Đang gửi...' : 'Gửi Toàn Hệ Thống' }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <transition name="toast">
      <div v-if="toast.show" :class="['toast-msg', toast.type]">
        {{ toast.msg }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Bell, Loader2, AlertTriangle, Trash2, Megaphone, Send } from 'lucide-vue-next'
import api from '@/services/api'

const notifs = ref([])
const loading = ref(false)
const error = ref('')
const showModal = ref(false)
const sending = ref(false)

// Mặc định luôn là ADMIN_MANUAL
const form = ref({ title: '', content: '', type: 'ADMIN_MANUAL' })

const toast = ref({ show: false, msg: '', type: 'success' })
const showToast = (msg, type = 'success') => {
  toast.value = { show: true, msg, type }
  setTimeout(() => toast.value.show = false, 3500)
}

// Format ngày tháng chuẩn Việt Nam
const formatDate = (d) => {
  if (!d) return ''
  return new Date(d).toLocaleString('vi-VN', { 
    hour: '2-digit', 
    minute: '2-digit',
    day: '2-digit', 
    month: '2-digit', 
    year: 'numeric' 
  })
}

// 🔥 LẤY VÀ GỘP THÔNG BÁO TRÙNG LẶP
const fetchNotifs = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await api.get('/api/admin/notifications')
    // 🔥 FIX: API có thể trả về { data: [...] } thay vì [...]
    // Thêm logic kiểm tra để đảm bảo rawData luôn là một mảng, đồng bộ với các file khác.
    const rawData = Array.isArray(res.data) ? res.data : (res.data?.data || [])
    
    // Thuật toán gom nhóm (Giữ lại 1 bản ghi duy nhất cho mỗi nội dung)
    const uniqueMap = new Map()
    
    rawData.forEach(n => {
      // Chỉ lấy thông báo hệ thống (tránh lấy thông báo like/comment)
      if (n.type === 'ADMIN_MANUAL' || n.type === 'GENERAL' || n.type === 'MAINTENANCE') {
        // Tạo key gộp nhóm dựa trên tiêu đề và nội dung
        const key = `${n.title}_${n.content}`
        if (!uniqueMap.has(key)) {
          uniqueMap.set(key, n)
        }
      }
    })

    // Chuyển Map về Array và sắp xếp mới nhất lên đầu
    notifs.value = Array.from(uniqueMap.values()).sort((a, b) => {
      return new Date(b.createdAt) - new Date(a.createdAt)
    })

  } catch (e) {
    error.value = 'Lỗi tải danh sách thông báo: ' + (e.response?.data?.message || e.message)
  } finally {
    loading.value = false
  }
}

const deleteNotif = async (item) => {
  if (!confirm('Bạn có chắc chắn muốn xóa thông báo này không?')) return
  try {
    // Tạm gọi xóa bản ghi đại diện
    await api.delete(`/api/admin/notifications/${item.notificationID}`)
    
    // Lọc bỏ khỏi giao diện dựa trên title và content (để ẩn đi cục đã gộp)
    notifs.value = notifs.value.filter(n => n.title !== item.title || n.content !== item.content)
    showToast('Đã ẩn thông báo thành công!')
  } catch (e) {
    showToast('Xóa thất bại: ' + (e.response?.data?.message || e.message), 'error')
  }
}

const openCreate = () => {
  // Luôn gán type = ADMIN_MANUAL
  form.value = { title: '', content: '', type: 'ADMIN_MANUAL' }
  showModal.value = true
}

const closeModal = () => { showModal.value = false }

const sendNotif = async () => {
  if (!form.value.title.trim() || !form.value.content.trim()) {
    showToast('Vui lòng nhập đầy đủ Tiêu đề và Nội dung!', 'error')
    return
  }
  sending.value = true
  try {
    await api.post('/api/admin/notifications/all', form.value)
    showToast('Đã gửi thông báo đến toàn bộ người dùng!')
    closeModal()
    fetchNotifs() // Tải lại danh sách (sẽ tự động gộp nhóm)
  } catch (e) {
    showToast('Gửi thất bại: ' + (e.response?.data?.message || e.message), 'error')
  } finally {
    sending.value = false
  }
}

onMounted(fetchNotifs)
</script>

<style scoped>
.page-container {
  padding: 24px 32px;
  font-family: 'Quicksand', sans-serif;
  color: #334155;
  background-color: #F8FAFC;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.title {
  font-size: 1.75rem;
  font-weight: 800;
  color: #0F172A;
  margin: 0;
  display: flex;
  align-items: center;
}

.subtitle {
  color: #64748B;
  margin: 6px 0 0;
  font-size: 0.95rem;
}

.btn-create {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #3B82F6, #4F46E5);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 700;
  font-family: inherit;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-create:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #94A3B8;
  font-size: 1.05rem;
  background: white;
  border-radius: 16px;
  border: 1px dashed #CBD5E1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 15px;
  opacity: 0.7;
}

.error-msg {
  color: #DC2626 !important;
  border-color: #FECACA;
  background-color: #FEF2F2;
  flex-direction: row;
}

.notif-item {
  display: flex;
  gap: 20px;
  background: white;
  padding: 20px 24px;
  border-radius: 16px;
  margin-bottom: 16px;
  align-items: flex-start;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.02);
  border: 1px solid #E2E8F0;
  transition: all 0.2s ease;
}

.notif-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
  border-color: #CBD5E1;
}

.icon-box {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.bg-blue { background: #EFF6FF; color: #3B82F6; }

.content {
  flex: 1;
}

.content h4 {
  margin: 0 0 6px 0;
  font-size: 1.1rem;
  color: #0F172A;
  font-weight: 700;
}

.content p {
  margin: 8px 0;
  color: #475569;
  font-size: 0.95rem;
  line-height: 1.5;
}

.content small {
  color: #94A3B8;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}

.manual-badge {
  background: #E0E7FF;
  color: #4338CA;
  padding: 3px 10px;
  border-radius: 6px;
  font-weight: 700;
  font-size: 0.75rem;
  display: inline-block;
}

.btn-del {
  background: #FEF2F2;
  border: 1px solid transparent;
  color: #EF4444;
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-del:hover {
  background: #EF4444;
  color: white;
  box-shadow: 0 4px 10px rgba(239, 68, 68, 0.3);
}

/* MODAL */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-box {
  background: white;
  width: 550px;
  max-width: 95%;
  border-radius: 20px;
  padding: 32px;
  position: relative;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
}

.btn-close {
  position: absolute;
  top: 20px;
  right: 20px;
  background: #F1F5F9;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  color: #64748B;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-close:hover {
  background: #EF4444;
  color: white;
}

.modal-box h3 {
  margin: 0 0 8px;
  font-size: 1.4rem;
  color: #0F172A;
  display: flex;
  align-items: center;
}

.modal-hint {
  color: #64748B;
  font-size: 0.9rem;
  margin: 0 0 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-weight: 700;
  font-size: 0.9rem;
  color: #1E293B;
  margin-bottom: 8px;
}

.required {
  color: #EF4444;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #CBD5E1;
  border-radius: 10px;
  font-family: inherit;
  font-size: 0.95rem;
  color: #0F172A;
  background-color: #F8FAFC;
  box-sizing: border-box;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3B82F6;
  background-color: white;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
}

.btn-cancel {
  background: #F1F5F9;
  border: none;
  padding: 12px 24px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 700;
  color: #475569;
  font-family: inherit;
  transition: 0.2s;
}

.btn-cancel:hover {
  background: #E2E8F0;
  color: #0F172A;
}

.btn-send {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #3B82F6, #4F46E5);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 700;
  font-family: inherit;
  transition: 0.2s;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-send:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
}

.btn-send:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* Animation */
.spin-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-box {
  animation: slideUp 0.3s ease-out;
}

.modal-leave-active .modal-box {
  animation: slideDown 0.3s ease-in forwards;
}

@keyframes slideUp {
  from { transform: translateY(30px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

@keyframes slideDown {
  from { transform: translateY(0); opacity: 1; }
  to { transform: translateY(30px); opacity: 0; }
}

/* Toast */
.toast-msg {
  position: fixed;
  bottom: 30px;
  right: 30px;
  padding: 16px 24px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.95rem;
  z-index: 9999;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
}

.toast-msg.success {
  background: #DCFCE7;
  color: #15803D;
  border: 1px solid #86EFAC;
}

.toast-msg.error {
  background: #FEF2F2;
  color: #B91C1C;
  border: 1px solid #FECACA;
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(30px) scale(0.9);
}
</style>