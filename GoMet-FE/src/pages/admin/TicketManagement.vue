<template>
  <div class="ticket-sovereign-wrapper">
    <div class="page-header-lux">
      <div>
        <h1 class="page-title">Trung tâm Phản hồi</h1>
        <p class="page-subtitle">Hệ thống xử lý khiếu nại và tối ưu hóa cộng đồng GOMET</p>
      </div>
      <button class="btn-refresh-lux" @click="fetchTickets" :disabled="loading">
        <svg :class="{ 'spinning': loading }" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M23 4v6h-6M1 20v-6h6M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
        </svg>
        {{ loading ? 'Đang đồng bộ...' : 'Làm mới dữ liệu' }}
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
        <select v-model="currentTypeFilter" class="select-lux">
          <option value="ALL">Tất cả phân loại</option>
          <option value="BUG">🛠️ Báo Lỗi</option>
          <option value="REPORT">🚩 Vi phạm</option>
          <option value="FEEDBACK">💡 Góp ý</option>
        </select>
      </div>

      <div class="table-responsive">
        <table v-if="!loading" class="lux-table">
          <thead>
            <tr>
              <th>Mã ID</th>
              <th>Người Gửi</th>
              <th>Phân Loại</th>
              <th>Liên Quan</th>
              <th>Tiêu đề</th>
              <th>Thời gian</th>
              <th>Trạng thái</th>
              <th class="text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="ticket in filteredTickets" :key="ticket.ticketID || ticket.id" class="lux-row" @click="viewDetail(ticket)">
              <td class="id-col">#{{ ticket.ticketID || ticket.id }}</td>
              <td class="user-name">
                {{ ticket.account?.username || ticket.username || ticket.account?.fullname || 'Người dùng ẩn' }}
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
              <td>{{ truncateText(ticket.title, 30) }}</td>
              <td>{{ formatDate(ticket.createdAt) }}</td>
              <td>
                <span class="badge-status" :class="'status-' + ticket.status">
                  {{ formatStatus(ticket.status) }}
                </span>
              </td>
              <td class="text-right">
                <button class="btn-refresh-lux" style="padding: 6px 12px;">Xử lý</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <transition name="fade-glass">
      <div v-if="showModal && selectedTicket" class="modal-glass-backdrop" @click="closeModal">
        <div class="modal-lux-content" @click.stop>
          <div class="modal-header-lux">
            <h3>Chi tiết Ticket #{{ selectedTicket.ticketID || selectedTicket.id }}</h3>
            <button class="btn-refresh-lux" @click="closeModal" style="padding: 5px 10px;">×</button>
          </div>

          <div class="modal-body custom-scroll">
            
            <div v-if="selectedTicket.ticketType === 'REPORT'" class="violation-card">
              <div class="v-header">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"/><line x1="4" y1="22" x2="4" y2="15"/></svg>
                <span>Nội dung bị báo cáo vi phạm</span>
              </div>
              <div class="v-body">
                <div class="v-info-group">
                  <span class="v-label">Mã bài viết</span>
                  <span class="v-id">#{{ selectedTicket.targetPostId || selectedTicket.targetPostID }}</span>
                </div>
                <button class="btn-go-post" @click="goToPost(selectedTicket.targetPostId || selectedTicket.targetPostID)">
                  Xem bài viết ngay
                </button>
              </div>
              
              <div class="quick-mod-bar">
                <button class="btn-mod btn-kill-post" @click="openConfirmModal('DELETE_POST', selectedTicket.targetPostId || selectedTicket.targetPostID)" :disabled="isProcessingAction">
                  🗑️ Xóa bài viết ngay
                </button>
                <button class="btn-mod btn-ban-user" @click="openConfirmModal('BAN_USER', selectedTicket.targetPostId || selectedTicket.targetPostID)" :disabled="isProcessingAction">
                  🚫 Khóa tài khoản Tác giả
                </button>
              </div>
            </div>

            <div class="info-grid">
              <div class="info-item">
                <label>Người gửi phản hồi</label>
                <p>{{ selectedTicket.account?.username || selectedTicket.username || 'Không rõ' }}</p>
              </div>
              <div class="info-item">
                <label>Thời gian tạo</label>
                <p>{{ formatDate(selectedTicket.createdAt) }}</p>
              </div>
            </div>

            <div class="desc-box-lux">
              <label>Tiêu đề người dùng nhập</label>
              <div style="margin-bottom: 10px;"><strong>{{ selectedTicket.title }}</strong></div>
              
              <label>Nội dung chi tiết</label>
              <div style="white-space: pre-wrap;">{{ selectedTicket.description }}</div>
            </div>

            <div v-if="selectedTicket.attachment" style="margin-top: 30px;">
              <label style="font-size: 0.75rem; font-weight: 800; color: #94a3b8;">ẢNH BẰNG CHỨNG</label>
              <img 
                :src="selectedTicket.attachment" 
                style="width:100%; border-radius: 20px; margin-top: 15px; border: 1px solid #eee; display: block;"
                @error="(e) => e.target.src = 'https://placehold.co/600x400?text=Khong+tai+duoc+anh'"
              />
            </div>

            <div class="timeline-logs">
              <div class="timeline-title">Nhật ký hệ thống</div>
              <div class="log-item">
                <div class="log-dot" style="background: #94a3b8; box-shadow: 0 0 0 1px #94a3b8;"></div>
                <div class="log-content">
                  <span class="log-time">{{ formatDate(selectedTicket.createdAt) }}</span>
                  <span class="log-action">Người dùng <span class="log-actor">{{ selectedTicket.account?.username || selectedTicket.username || 'Ẩn danh' }}</span> đã gửi yêu cầu.</span>
                </div>
              </div>
              <div class="log-item" v-if="selectedTicket.processedAt || selectedTicket.ProcessedAt">
                <div class="log-dot" style="background: #3b82f6; box-shadow: 0 0 0 1px #3b82f6;"></div>
                <div class="log-content">
                  <span class="log-time">{{ formatDate(selectedTicket.processedAt || selectedTicket.ProcessedAt) }}</span>
                  <span class="log-action">Admin đã <span class="log-actor">Tiếp nhận xử lý</span>.</span>
                </div>
              </div>
              <div class="log-item" v-if="selectedTicket.status >= 2 && selectedTicket.resolvedAt">
                <div class="log-dot" style="background: #16a34a; box-shadow: 0 0 0 1px #16a34a;"></div>
                <div class="log-content">
                  <span class="log-time">{{ formatDate(selectedTicket.resolvedAt) }}</span>
                  <span class="log-action">Ticket đã được đóng bởi <span class="log-actor">Quản trị viên</span>.</span>
                </div>
              </div>
            </div>

          </div>

          <div class="modal-footer-lux">
            <button v-if="selectedTicket.status === 0" class="btn-lux btn-process" @click="updateStatus(1)">Tiếp nhận</button>
            <button v-if="selectedTicket.status < 2" class="btn-lux btn-reject" @click="updateStatus(3)">Từ chối</button>
            <button v-if="selectedTicket.status === 1" class="btn-lux btn-resolve" @click="updateStatus(2)" style="background: #1a110d; color: white;">Giải quyết xong</button>
          </div>
        </div>
      </div>
    </transition>

    <transition name="fade-glass">
      <div v-if="confirmModal.show" class="modal-glass-backdrop" style="z-index: 10000;" @click="closeConfirmModal">
        <div class="confirm-dialog-card" @click.stop>
          <div class="icon-danger-circle">
            <svg v-if="confirmModal.type === 'DELETE_POST'" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2.5"><path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
            <svg v-if="confirmModal.type === 'BAN_USER'" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line></svg>
          </div>
          <h3 class="confirm-title">{{ confirmModal.title }}</h3>
          <p class="confirm-desc">{{ confirmModal.message }}</p>
          
          <div class="confirm-actions">
            <button class="btn-cancel-action" @click="closeConfirmModal" :disabled="isProcessingAction">Hủy bỏ</button>
            <button class="btn-danger-action" @click="executeAction" :disabled="isProcessingAction">
              <span v-if="!isProcessingAction">Xác nhận</span>
              <span v-else>Đang xử lý...</span>
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { toast } from '@/composables/useToast'
import { useAuthStore } from '@/stores/auth'
import './TicketManagement.scss'

const authStore = useAuthStore()
const router = useRouter()

const tickets = ref([])
const loading = ref(false)
const showModal = ref(false)
const selectedTicket = ref(null)
const currentStatusFilter = ref(-1)
const currentTypeFilter = ref('ALL')
const isProcessingAction = ref(false)

// --- LOGIC POPUP XÁC NHẬN ---
const confirmModal = ref({
  show: false,
  type: '', // 'DELETE_POST' hoặc 'BAN_USER'
  targetId: null,
  title: '',
  message: ''
})

const openConfirmModal = (type, targetId) => {
  if (!targetId) return toast.error('Không tìm thấy ID bài viết!');
  
  confirmModal.value.type = type;
  confirmModal.value.targetId = targetId;
  confirmModal.value.show = true;

  if (type === 'DELETE_POST') {
    confirmModal.value.title = `Xóa bài viết #${targetId}?`;
    confirmModal.value.message = 'Bài viết này sẽ bị ẩn khỏi cộng đồng nhưng vẫn được lưu trong hệ thống để đối soát.';
  } else if (type === 'BAN_USER') {
    confirmModal.value.title = `Khóa tác giả bài viết #${targetId}?`;
    confirmModal.value.message = 'Hành động này sẽ đóng băng tài khoản người dùng ngay lập tức. Họ sẽ không thể đăng nhập GOMET được nữa.';
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
// ----------------------------

const deleteViolatingPost = async (postId) => {
  isProcessingAction.value = true;
  try {
    const res = await fetch(`/api/admin/posts/${postId}`, {
      method: 'DELETE', 
      headers: { 'Authorization': `Bearer ${authStore.token}` }
    });
    if (res.ok) {
      toast.success(`Đã trảm bài viết #${postId}!`);
      await updateStatus(2);
      closeConfirmModal();
    } else {
      toast.error('Xóa thất bại! Có thể bài viết không tồn tại.');
    }
  } catch (error) { toast.error('Lỗi hệ thống khi trảm bài!'); }
  finally { isProcessingAction.value = false; }
}

const banViolatingUser = async (postId) => {
  isProcessingAction.value = true;
  try {
    const res = await fetch(`/api/admin/posts/${postId}/ban-author`, {
      method: 'PUT', // Phương thức PUT
      headers: { 
        'Authorization': `Bearer ${authStore.token}`,
        'Content-Type': 'application/json' // 🔥 Thêm dòng này để báo là gửi JSON
      },
      body: JSON.stringify({}) // 🔥 Gửi một body rỗng để tránh lỗi 400 "Required request body is missing"
    });

    if (res.ok) {
      toast.success('Thi hành lệnh cấm thành công! Tài khoản đã bị khóa.');
      await updateStatus(2); // Tự động hoàn thành ticket
      closeConfirmModal();
    } else {
      // Đọc lỗi chi tiết từ server nếu có
      const errorData = await res.json().catch(() => ({}));
      toast.error(errorData.message || 'Khóa thất bại, sếp kiểm tra lại nhé!');
    }
  } catch (error) { 
    toast.error('Lỗi máy chủ khi thực hiện lệnh cấm!'); 
  } finally { 
    isProcessingAction.value = false; 
  }
}

const filterTabs = [
  { label: 'Tất cả', value: -1 },
  { label: 'Chờ duyệt', value: 0 },
  { label: 'Đang xử lý', value: 1 },
  { label: 'Đã hoàn thành', value: 2 },
]

const stats = computed(() => [
  { label: 'Tổng Ticket Chờ', value: tickets.value.filter(t => t.status === 0).length, iconClass: 'all', icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="3" y1="9" x2="21" y2="9"></line><line x1="9" y1="21" x2="9" y2="9"></line></svg>' },
  { label: 'Báo Lỗi (Bug)', value: tickets.value.filter(t => t.status === 0 && t.ticketType === 'BUG').length, iconClass: 'bug', icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M22 12h-4l-3 9L9 3l-3 9H2"></path></svg>' },
  { label: 'Vi Phạm', value: tickets.value.filter(t => t.status === 0 && t.ticketType === 'REPORT').length, iconClass: 'report', class: 'danger-highlight', icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>' },
  { label: 'Góp ý', value: tickets.value.filter(t => t.status === 0 && t.ticketType === 'FEEDBACK').length, iconClass: 'feedback', icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>' }
])

const fetchTickets = async () => {
  loading.value = true
  try {
    const res = await fetch('/api/admin/tickets', {
      headers: { 'Authorization': `Bearer ${authStore.token}` }
    })
    if (res.ok) tickets.value = await res.json()
  } catch (e) { toast.error('Lỗi server!') }
  finally { loading.value = false }
}

const updateStatus = async (newStatus) => {
  try {
    const ticketId = selectedTicket.value.ticketID || selectedTicket.value.id
    const res = await fetch(`/api/admin/tickets/${ticketId}/status?status=${newStatus}`, { 
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${authStore.token}` }
    })
    
    if (res.ok) {
      await fetchTickets();
      const updatedData = tickets.value.find(t => (t.ticketID || t.id) === ticketId);
      if (updatedData) selectedTicket.value = { ...updatedData };
      toast.success('Cập nhật trạng thái thành công!');
    }
  } catch (e) { toast.error('Lỗi xử lý!') }
}

const goToPost = (id) => { if (id) window.open(`/post/${id}`, '_blank'); }

const filteredTickets = computed(() => {
  return tickets.value.filter(t => {
    const matchS = currentStatusFilter.value === -1 || t.status === currentStatusFilter.value
    const matchT = currentTypeFilter.value === 'ALL' || t.ticketType === currentTypeFilter.value
    return matchS && matchT
  }).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

const viewDetail = (ticket) => { selectedTicket.value = ticket; showModal.value = true; }
const closeModal = () => { showModal.value = false }
const truncateText = (t, l) => t?.length > l ? t.substring(0, l) + '...' : t
const formatDate = (d) => d ? new Date(d).toLocaleString('vi-VN') : '—'
const formatType = (t) => ({ BUG: 'Báo Lỗi', REPORT: 'Vi Phạm', FEEDBACK: 'Góp Ý' }[t] || t)
const formatStatus = (s) => ({ 0: 'Chờ duyệt', 1: 'Đang xử lý', 2: 'Đã giải quyết', 3: 'Đã từ chối' }[s])

onMounted(fetchTickets)
</script>

<style scoped lang="scss">
/* THÊM CSS CHO POPUP XÁC NHẬN VÀO CUỐI FILE SCSS CỦA SẾP */
.confirm-dialog-card {
  background: white; width: 100%; max-width: 400px;
  border-radius: 24px; padding: 30px; text-align: center;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  transform: scale(0.95); animation: popIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
}

@keyframes popIn {
  to { transform: scale(1); }
}

.icon-danger-circle {
  width: 64px; height: 64px; background: #fef2f2;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  margin: 0 auto 20px; box-shadow: 0 0 0 8px #fef2f2;
}

.confirm-title { font-size: 1.25rem; font-weight: 800; color: #1e293b; margin: 0 0 10px 0; }
.confirm-desc { font-size: 0.9rem; color: #64748b; margin: 0 0 25px 0; line-height: 1.5; }

.confirm-actions {
  display: flex; gap: 12px;
}

.btn-cancel-action {
  flex: 1; padding: 12px 0; border: none; background: #f1f5f9;
  color: #475569; font-weight: 700; border-radius: 12px; cursor: pointer; transition: 0.2s;
  &:hover { background: #e2e8f0; }
}

.btn-danger-action {
  flex: 1; padding: 12px 0; border: none; background: #ef4444;
  color: white; font-weight: 700; border-radius: 12px; cursor: pointer; transition: 0.2s;
  &:hover { background: #dc2626; transform: translateY(-2px); }
  &:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }
}
</style>