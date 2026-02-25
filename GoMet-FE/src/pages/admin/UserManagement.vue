
<template>
  <div class="page-container animate-enter">
    
    <div class="page-header">
      <div class="title-group">
        <h2 class="title">Quản lý người dùng</h2>
        <p class="subtitle">Tổng: <span class="badge">{{ filteredUsers.length }}</span> / {{ users.length }} tài khoản</p>
      </div>
      <div class="header-actions">
        <div class="search-box">
          <i class="fa-solid fa-search search-icon"></i>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm theo tên, email..."
            class="search-input"
          />
          <button v-if="searchQuery" @click="searchQuery = ''" class="clear-search">
            <i class="fa-solid fa-xmark"></i>
          </button>
        </div>
        <button class="btn-refresh" @click="fetchUsers">
          <i class="fa-solid fa-rotate-right" :class="{ 'fa-spin': isLoading }"></i> Làm mới
        </button>
      </div>
    </div>

    <!-- ══ USER DETAIL MODAL ══ -->
    <Transition name="modal-fade">
      <div v-if="detailModal.show" class="modal-overlay" @click.self="detailModal.show = false">
        <div class="detail-modal-card">
          <button class="detail-close" @click="detailModal.show = false"><i class="fa-solid fa-xmark"></i></button>

          <div v-if="detailModal.loading" class="detail-loading">
            <div class="spinner"></div><span>Đang tải...</span>
          </div>

          <template v-else-if="detailModal.user">
            <!-- Header -->
            <div class="detail-hero">
              <div class="detail-avatar-ring">
                <img :src="detailModal.user.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(detailModal.user.username)}&background=EA580C&color=fff&size=128`" class="detail-avatar" />
                <span class="detail-status-dot" :class="detailModal.user.isActive ? 'dot-active' : 'dot-banned'"></span>
              </div>
              <div class="detail-name-area">
                <h2 class="detail-username">{{ detailModal.user.username }}</h2>
                <p class="detail-email">{{ detailModal.user.email }}</p>
                <div class="detail-badges">
                  <span class="badge-role" :class="detailModal.user.role === 'ADMIN' ? 'badge-admin' : 'badge-user'">
                    <i :class="detailModal.user.role === 'ADMIN' ? 'fa-solid fa-shield-halved' : 'fa-solid fa-user'"></i>
                    {{ detailModal.user.role === 'ADMIN' ? 'Admin' : 'Member' }}
                  </span>
                  <span v-if="detailModal.user.isPremium" class="badge-premium"><i class="fa-solid fa-crown"></i> Premium</span>
                  <span class="badge-status" :class="detailModal.user.isActive ? 'badge-active' : 'badge-banned'">
                    {{ detailModal.user.isActive ? 'Hoạt động' : 'Bị khóa' }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Stats row -->
            <div class="detail-stats-row">
              <div class="d-stat"><span class="d-stat-val"><Star :size="13" style="vertical-align:middle;color:#F59E0B" /> {{ detailModal.user.point ?? 0 }}</span><span class="d-stat-lbl">Điểm</span></div>
              <div class="d-stat-sep"></div>
              <div class="d-stat"><span class="d-stat-val">{{ detailModal.user.postCount ?? detailModal.stats?.totalPosts ?? '—' }}</span><span class="d-stat-lbl">Bài viết</span></div>
              <div class="d-stat-sep"></div>
              <div class="d-stat"><span class="d-stat-val">{{ detailModal.stats?.totalFollowers ?? '—' }}</span><span class="d-stat-lbl">Follower</span></div>
              <div class="d-stat-sep"></div>
              <div class="d-stat"><span class="d-stat-val">{{ detailModal.stats?.totalLikes ?? '—' }}</span><span class="d-stat-lbl">Lượt thích</span></div>
            </div>

            <!-- Info grid -->
            <div class="detail-info-grid">
              <div class="d-info-item">
                <span class="d-info-lbl"><i class="fa-solid fa-id-badge"></i> ID</span>
                <span class="d-info-val">#{{ detailModal.user.accountID }}</span>
              </div>
              <div class="d-info-item">
                <span class="d-info-lbl"><i class="fa-solid fa-calendar"></i> Ngày tham gia</span>
                <span class="d-info-val">{{ detailModal.user.createdAt ? new Date(detailModal.user.createdAt).toLocaleDateString('vi-VN') : '—' }}</span>
              </div>
              <div v-if="detailModal.user.bio" class="d-info-item d-info-full">
                <span class="d-info-lbl"><i class="fa-solid fa-quote-left"></i> Bio</span>
                <span class="d-info-val">{{ detailModal.user.bio }}</span>
              </div>
            </div>

            <!-- Actions -->
            <div class="detail-footer-actions">
              <button
                @click="askBanAction(detailModal.user); detailModal.show = false"
                class="detail-action-btn"
                :class="detailModal.user.isActive ? 'dab-warn' : 'dab-ok'"
              >
                <i :class="detailModal.user.isActive ? 'fa-solid fa-lock' : 'fa-solid fa-unlock'"></i>
                {{ detailModal.user.isActive ? 'Khóa tài khoản' : 'Mở khóa' }}
              </button>
              <button @click="detailModal.show = false" class="detail-action-btn dab-neutral">Đóng</button>
            </div>
          </template>
        </div>
      </div>
    </Transition>

    <!-- Ban confirmation modal -->
    <div v-if="banModal.show" class="modal-overlay" @click.self="banModal.show = false">
      <div class="modal-card">
        <div class="modal-icon" :class="banModal.action === 'ban' ? 'icon-warn' : 'icon-success'">
          <i :class="banModal.action === 'ban' ? 'fa-solid fa-lock' : 'fa-solid fa-unlock'"></i>
        </div>
        <h3 class="modal-title">{{ banModal.action === 'ban' ? 'Khóa tài khoản?' : 'Mở khóa tài khoản?' }}</h3>
        <p class="modal-desc">
          {{ banModal.action === 'ban'
            ? `Tài khoản <strong>${banModal.username}</strong> sẽ bị khóa và không thể đăng nhập.`
            : `Tài khoản <strong>${banModal.username}</strong> sẽ được mở khóa trở lại.`
          }}
        </p>
        <div class="modal-actions">
          <button class="btn-modal-cancel" @click="banModal.show = false">Hủy</button>
          <button
            class="btn-modal-confirm"
            :class="banModal.action === 'ban' ? 'btn-modal-danger' : 'btn-modal-ok'"
            @click="confirmBanAction"
          >
            {{ banModal.action === 'ban' ? 'Khóa ngay' : 'Mở khóa' }}
          </button>
        </div>
      </div>
    </div>

    <div class="table-wrapper glass-panel">
      <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <span>Đang tải dữ liệu...</span>
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Người dùng</th>
            <th>Email</th>
            <th>Quyền</th>
            <th>Trạng thái</th>
            <th>Điểm</th>
            <th class="text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.accountID" class="table-row">
            <td class="col-id">#{{ user.accountID }}</td>
            <td>
              <div class="user-cell">
                <img
                  :src="user.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(user.username)}&background=random`"
                  class="avatar" loading="lazy"
                >
                <div class="u-info">
                  <span class="name">{{ user.username }}</span>
                  <span class="sub-name">{{ user.email }}</span>
                </div>
              </div>
            </td>
            <td class="col-email">{{ user.email }}</td>
            <td>
              <div class="role-wrapper">
                <select
                  v-model="user.role"
                  @change="updateUserRole(user)"
                  class="role-select"
                  :class="user.role === 'ADMIN' ? 'role-admin' : 'role-user'"
                >
                  <option value="USER">Member</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </div>
            </td>
            <td>
              <div class="status-pill" :class="user.isActive ? 'active' : 'banned'">
                <span class="status-dot"></span>
                {{ user.isActive ? 'Hoạt động' : 'Bị khóa' }}
              </div>
            </td>
            <td class="col-point">
              <span class="point-badge"><Star :size="11" style="vertical-align:middle;color:#92400E" /> {{ user.point ?? 0 }}</span>
            </td>
            <td>
              <div class="actions">
                <button
                  @click="openDetail(user)"
                  class="btn-action-icon btn-view"
                  title="Xem chi tiết"
                >
                  <i class="fa-solid fa-eye"></i>
                </button>
                <button
                  @click="askBanAction(user)"
                  class="btn-action-icon"
                  :class="user.isActive ? 'btn-warn' : 'btn-success'"
                  :title="user.isActive ? 'Khóa tài khoản' : 'Mở khóa tài khoản'"
                >
                  <i :class="user.isActive ? 'fa-solid fa-lock' : 'fa-solid fa-unlock'"></i>
                </button>
                <button
                  @click="deleteUser(user.accountID)"
                  class="btn-action-icon btn-danger"
                  title="Xóa vĩnh viễn"
                >
                  <i class="fa-solid fa-trash-can"></i>
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="filteredUsers.length === 0 && !isLoading">
            <td colspan="7" class="empty-state">
              <i class="fa-regular fa-folder-open"></i>
              <p>{{ searchQuery ? 'Không tìm thấy tài khoản nào.' : 'Chưa có tài khoản nào trên hệ thống.' }}</p>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Star } from 'lucide-vue-next'
import api from '@/services/api'
import { toast } from '@/composables/useToast'

const users       = ref([])
const isLoading   = ref(true)
const searchQuery = ref('')

// Ban confirmation modal state
const banModal = ref({ show: false, action: 'ban', accountID: null, username: '', userRef: null })

// Detail modal state
const detailModal = ref({ show: false, loading: false, user: null, stats: null })

const openDetail = async (user) => {
  detailModal.value = { show: true, loading: true, user: null, stats: null }
  try {
    const [userRes, statsRes] = await Promise.allSettled([
      api.get(`/admin/accounts/${user.accountID}`),
      api.get(`/api/users/${user.accountID}/stats`)
    ])
    const u = userRes.status === 'fulfilled' ? userRes.value.data : user
    detailModal.value.user = {
      ...u,
      role: u.role ? String(u.role).toUpperCase() : (u.isAdmin ? 'ADMIN' : 'USER'),
      isActive: u.isActive === 1 || u.isActive === true,
      isPremium: u.isPremium === 1 || u.isPremium === true
    }
    if (statsRes.status === 'fulfilled') {
      detailModal.value.stats = statsRes.value.data
    }
  } catch {
    detailModal.value.user = { ...user, isPremium: user.isPremium === 1 || user.isPremium === true }
  } finally {
    detailModal.value.loading = false
  }
}

// Filtered list for search
const filteredUsers = computed(() => {
  if (!searchQuery.value.trim()) return users.value
  const q = searchQuery.value.toLowerCase()
  return users.value.filter(u =>
    u.username?.toLowerCase().includes(q) ||
    u.email?.toLowerCase().includes(q)
  )
})

// ── 1. FETCH ──────────────────────────────────────────────────────────────────
const fetchUsers = async () => {
  isLoading.value = true
  try {
    const res = await api.get('/admin/accounts')
    users.value = res.data.map(u => ({
      ...u,
      role: u.role ? String(u.role).toUpperCase() : (u.isAdmin ? 'ADMIN' : 'USER'),
      isActive: u.isActive === 1 || u.isActive === true
    }))
  } catch (err) {
    console.error('Lỗi tải danh sách tài khoản:', err)
    toast.error('Không thể kết nối máy chủ. Thử lại sau!')
  } finally {
    isLoading.value = false
  }
}

// ── 2. ROLE UPDATE ────────────────────────────────────────────────────────────
const updateUserRole = async (user) => {
  try {
    await api.put(`/admin/accounts/${user.accountID}`, {
      role:    user.role,
      isAdmin: user.role === 'ADMIN' ? 1 : 0
    })
    toast.success(`Đã cập nhật quyền ${user.username} → ${user.role}`)
  } catch (err) {
    console.error('Lỗi cập nhật quyền:', err)
    toast.error('Cập nhật quyền thất bại!')
    fetchUsers()
  }
}

// ── 3. BAN / UNBAN ────────────────────────────────────────────────────────────
const askBanAction = (user) => {
  banModal.value = {
    show:      true,
    action:    user.isActive ? 'ban' : 'unban',
    accountID: user.accountID,
    username:  user.username,
    userRef:   user
  }
}

const confirmBanAction = async () => {
  const { action, accountID, userRef } = banModal.value
  banModal.value.show = false
  try {
    await api.patch(`/admin/accounts/${accountID}/${action}`)
    userRef.isActive = action === 'unban'
    toast.success(action === 'ban'
      ? `Đã khóa tài khoản ${userRef.username}`
      : `Đã mở khóa tài khoản ${userRef.username}`
    )
  } catch (err) {
    console.error(`Lỗi ${action}:`, err)
    toast.error(`Thao tác thất bại!`)
    fetchUsers()
  }
}

// ── 4. DELETE ─────────────────────────────────────────────────────────────────
const deleteUser = async (id) => {
  const target = users.value.find(u => u.accountID === id)
  if (!confirm(`Xóa vĩnh viễn tài khoản "${target?.username}"? Hành động này không thể hoàn tác!`)) return
  try {
    await api.delete(`/admin/accounts/hard/${id}`)
    users.value = users.value.filter(u => u.accountID !== id)
    toast.success('Đã xóa tài khoản thành công.')
  } catch (err) {
    console.error('Lỗi xóa tài khoản:', err)
    toast.error('Không thể xóa. Tài khoản có thể đang có dữ liệu liên kết (bài viết, bình luận...).')
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
.page-container { padding: 30px 40px; font-family: 'Mulish', sans-serif; color: #1E293B; }

/* HEADER */
.page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 25px; flex-wrap: wrap; gap: 16px; }
.title { font-size: 1.8rem; font-weight: 800; color: #0F172A; margin: 0 0 5px 0; letter-spacing: -0.5px; }
.subtitle { font-size: 0.9rem; color: #64748B; margin: 0; }
.badge { background: #EA580C; color: white; padding: 2px 8px; border-radius: 6px; font-weight: 800; font-size: 0.8rem; }
.header-actions { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; }
.btn-refresh { background: white; border: 1px solid #E2E8F0; padding: 8px 16px; border-radius: 10px; font-weight: 700; color: #475569; cursor: pointer; transition: 0.2s; display: flex; gap: 8px; align-items: center; box-shadow: 0 2px 5px rgba(0,0,0,0.02); }
.btn-refresh:hover { background: #F8FAFC; color: #EA580C; border-color: #CBD5E1; }

/* SEARCH BOX */
.search-box { position: relative; display: flex; align-items: center; }
.search-icon { position: absolute; left: 12px; color: #94A3B8; font-size: 0.85rem; pointer-events: none; }
.search-input { padding: 8px 36px 8px 34px; border: 1px solid #E2E8F0; border-radius: 10px; font-size: 0.9rem; font-family: 'Mulish', sans-serif; width: 240px; outline: none; transition: 0.2s; background: white; }
.search-input:focus { border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234,88,12,0.1); }
.clear-search { position: absolute; right: 10px; background: none; border: none; color: #94A3B8; cursor: pointer; font-size: 0.85rem; padding: 2px; }
.clear-search:hover { color: #EF4444; }

/* TABLE */
.glass-panel { background: rgba(255,255,255,0.7); backdrop-filter: blur(20px); border-radius: 16px; border: 1px solid rgba(255,255,255,0.8); box-shadow: 0 10px 30px rgba(0,0,0,0.03); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; font-size: 0.9rem; }
.data-table th { background: rgba(248,250,252,0.8); padding: 15px 20px; text-align: left; font-weight: 700; color: #64748B; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 1px solid #E2E8F0; }
.data-table td { padding: 15px 20px; border-bottom: 1px solid #F1F5F9; vertical-align: middle; }
.text-right { text-align: right !important; }
.table-row { transition: background 0.2s; }
.table-row:hover { background: rgba(255,247,237,0.4); }
.col-id { font-weight: 700; color: #94A3B8; font-size: 0.8rem; }
.col-email { color: #475569; font-weight: 500; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.col-point { text-align: center; }
.point-badge { font-size: 0.8rem; font-weight: 700; color: #92400E; background: #FEF3C7; padding: 3px 8px; border-radius: 6px; }

/* USER CELL */
.user-cell { display: flex; align-items: center; gap: 12px; }
.avatar { width: 42px; height: 42px; border-radius: 50%; object-fit: cover; border: 2px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.u-info { display: flex; flex-direction: column; }
.u-info .name { font-weight: 800; color: #0F172A; font-size: 0.95rem; }
.u-info .sub-name { font-size: 0.75rem; color: #94A3B8; }

/* ROLE SELECT */
.role-wrapper { position: relative; width: max-content; }
.role-select { appearance: none; background: white; border: 1px solid #E2E8F0; padding: 6px 30px 6px 12px; border-radius: 8px; font-weight: 700; font-size: 0.8rem; cursor: pointer; transition: 0.2s; font-family: 'Mulish', sans-serif; }
.role-select:focus { outline: none; border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234,88,12,0.1); }
.role-wrapper::after { content: '▼'; font-size: 0.6rem; position: absolute; right: 10px; top: 50%; transform: translateY(-50%); color: #94A3B8; pointer-events: none; }
.role-admin { color: #EA580C; background: #FFF7ED; border-color: #FFEDD5; }
.role-user  { color: #3B82F6; }

/* STATUS PILL */
.status-pill { display: inline-flex; align-items: center; gap: 6px; padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; }
.status-dot { width: 6px; height: 6px; border-radius: 50%; }
.status-pill.active { background: #F0FDF4; color: #16A34A; border: 1px solid #DCFCE7; }
.status-pill.active .status-dot { background: #22C55E; box-shadow: 0 0 5px #22C55E; }
.status-pill.banned { background: #FEF2F2; color: #DC2626; border: 1px solid #FEE2E2; }
.status-pill.banned .status-dot { background: #EF4444; }

/* ACTIONS */
.actions { display: flex; gap: 8px; justify-content: flex-end; }
.btn-action-icon { width: 34px; height: 34px; border-radius: 8px; border: 1px solid transparent; display: flex; align-items: center; justify-content: center; font-size: 0.9rem; cursor: pointer; transition: 0.2s; background: white; }
.btn-warn    { color: #F59E0B; border-color: #FEF3C7; }
.btn-warn:hover   { background: #FEF3C7; transform: translateY(-2px); }
.btn-success { color: #10B981; border-color: #D1FAE5; }
.btn-success:hover { background: #D1FAE5; transform: translateY(-2px); }
.btn-danger  { color: #EF4444; border-color: #FEE2E2; }
.btn-danger:hover  { background: #EF4444; color: white; transform: translateY(-2px); }
.btn-view    { color: #3B82F6; border-color: #DBEAFE; }
.btn-view:hover    { background: #DBEAFE; transform: translateY(-2px); }

/* ── DETAIL MODAL ── */
.detail-modal-card { background: white; border-radius: 24px; padding: 0; max-width: 520px; width: 92%; position: relative; box-shadow: 0 30px 70px rgba(0,0,0,0.25); overflow: hidden; animation: modalPop 0.3s cubic-bezier(0.34,1.56,0.64,1); }
.detail-close { position: absolute; top: 16px; right: 16px; width: 34px; height: 34px; border-radius: 50%; border: 1px solid #E2E8F0; background: white; cursor: pointer; z-index: 10; display: flex; align-items: center; justify-content: center; color: #94A3B8; transition: 0.2s; }
.detail-close:hover { background: #FEE2E2; color: #EF4444; border-color: #FEE2E2; }
.detail-loading { padding: 60px; display: flex; flex-direction: column; align-items: center; gap: 15px; color: #94A3B8; font-weight: 600; }
.detail-hero { background: linear-gradient(135deg, #FFF7ED 0%, #FFFBF5 100%); padding: 36px 32px 24px; display: flex; gap: 24px; align-items: center; border-bottom: 1px solid #F1F5F9; }
.detail-avatar-ring { position: relative; flex-shrink: 0; }
.detail-avatar { width: 90px; height: 90px; border-radius: 50%; object-fit: cover; border: 4px solid white; box-shadow: 0 8px 20px rgba(234,88,12,0.2); }
.detail-status-dot { position: absolute; bottom: 4px; right: 4px; width: 16px; height: 16px; border-radius: 50%; border: 3px solid white; }
.dot-active { background: #22C55E; box-shadow: 0 0 8px #22C55E; }
.dot-banned { background: #EF4444; }
.detail-name-area { flex: 1; min-width: 0; }
.detail-username { font-size: 1.4rem; font-weight: 900; color: #0F172A; margin: 0 0 4px; }
.detail-email { font-size: 0.85rem; color: #64748B; margin: 0 0 12px; }
.detail-badges { display: flex; gap: 8px; flex-wrap: wrap; }
.badge-role { display: inline-flex; align-items: center; gap: 5px; padding: 4px 10px; border-radius: 8px; font-size: 0.75rem; font-weight: 800; }
.badge-admin { background: #FFF7ED; color: #EA580C; border: 1px solid #FDBA74; }
.badge-user  { background: #EFF6FF; color: #3B82F6; border: 1px solid #BFDBFE; }
.badge-premium { display: inline-flex; align-items: center; gap: 5px; padding: 4px 10px; border-radius: 8px; font-size: 0.75rem; font-weight: 800; background: #FFF9C4; color: #B45309; border: 1px solid #FDE68A; }
.badge-status { display: inline-flex; align-items: center; gap: 5px; padding: 4px 10px; border-radius: 8px; font-size: 0.75rem; font-weight: 700; }
.badge-active { background: #F0FDF4; color: #16A34A; border: 1px solid #DCFCE7; }
.badge-banned { background: #FEF2F2; color: #DC2626; border: 1px solid #FEE2E2; }
.detail-stats-row { display: flex; align-items: center; padding: 20px 32px; border-bottom: 1px solid #F1F5F9; }
.d-stat { flex: 1; text-align: center; display: flex; flex-direction: column; gap: 2px; }
.d-stat-val { font-size: 1.2rem; font-weight: 900; color: #0F172A; }
.d-stat-lbl { font-size: 0.7rem; color: #94A3B8; text-transform: uppercase; font-weight: 700; letter-spacing: 0.5px; }
.d-stat-sep { width: 1px; background: #E2E8F0; height: 36px; }
.detail-info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0; padding: 16px 24px; }
.d-info-item { display: flex; flex-direction: column; gap: 2px; padding: 12px; border-radius: 10px; }
.d-info-item:hover { background: #F8FAFC; }
.d-info-full { grid-column: span 2; }
.d-info-lbl { font-size: 0.72rem; color: #94A3B8; font-weight: 700; text-transform: uppercase; letter-spacing: 0.3px; display: flex; align-items: center; gap: 5px; }
.d-info-val { font-size: 0.92rem; font-weight: 700; color: #1E293B; word-break: break-word; }
.detail-footer-actions { display: flex; gap: 12px; padding: 16px 32px 24px; border-top: 1px solid #F1F5F9; justify-content: flex-end; }
.detail-action-btn { display: flex; align-items: center; gap: 8px; padding: 10px 20px; border-radius: 12px; border: none; font-weight: 800; font-size: 0.9rem; cursor: pointer; transition: 0.2s; font-family: 'Mulish', sans-serif; }
.dab-warn    { background: #FEF3C7; color: #B45309; }
.dab-warn:hover    { background: #F59E0B; color: white; }
.dab-ok      { background: #D1FAE5; color: #065F46; }
.dab-ok:hover      { background: #10B981; color: white; }
.dab-neutral { background: #F1F5F9; color: #475569; }
.dab-neutral:hover { background: #E2E8F0; }
/* Modal transition */
.modal-fade-enter-active, .modal-fade-leave-active { transition: all 0.25s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-from .detail-modal-card, .modal-fade-leave-to .detail-modal-card { transform: scale(0.92); }

/* LOADING & EMPTY */
.loading-state { padding: 50px; display: flex; flex-direction: column; align-items: center; gap: 15px; color: #94A3B8; font-weight: 600; }
.spinner { width: 30px; height: 30px; border: 3px solid #E2E8F0; border-top-color: #EA580C; border-radius: 50%; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.empty-state { text-align: center; padding: 50px !important; color: #94A3B8; }
.empty-state i { font-size: 3rem; margin-bottom: 10px; opacity: 0.5; }

/* BAN MODAL */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.45); z-index: 9999; display: flex; align-items: center; justify-content: center; backdrop-filter: blur(4px); }
.modal-card { background: white; border-radius: 20px; padding: 36px 32px; max-width: 420px; width: 90%; text-align: center; box-shadow: 0 25px 60px rgba(0,0,0,0.2); animation: modalPop 0.3s cubic-bezier(0.34,1.56,0.64,1); }
@keyframes modalPop { from { opacity: 0; transform: scale(0.85); } to { opacity: 1; transform: scale(1); } }
.modal-icon { width: 64px; height: 64px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; margin: 0 auto 20px; }
.icon-warn    { background: #FEF3C7; color: #F59E0B; }
.icon-success { background: #D1FAE5; color: #10B981; }
.modal-title { font-size: 1.3rem; font-weight: 800; color: #0F172A; margin: 0 0 12px; }
.modal-desc { color: #64748B; font-size: 0.95rem; line-height: 1.6; margin: 0 0 24px; }
.modal-actions { display: flex; gap: 12px; justify-content: center; }
.btn-modal-cancel  { padding: 10px 24px; border: 1px solid #E2E8F0; background: white; border-radius: 10px; font-weight: 700; color: #64748B; cursor: pointer; transition: 0.2s; }
.btn-modal-cancel:hover { background: #F1F5F9; }
.btn-modal-confirm { padding: 10px 24px; border: none; border-radius: 10px; font-weight: 800; cursor: pointer; transition: 0.2s; color: white; }
.btn-modal-danger  { background: #EF4444; }
.btn-modal-danger:hover  { background: #DC2626; transform: translateY(-1px); }
.btn-modal-ok      { background: #10B981; }
.btn-modal-ok:hover      { background: #059669; transform: translateY(-1px); }

/* ANIMATION */
.animate-enter { animation: fadeIn 0.5s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>
