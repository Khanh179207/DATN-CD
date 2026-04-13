<template>
  <div class="page-container animate-enter">
    
    <div class="page-header">
      <div class="title-group">
        <div class="icon-box-lux">
          <UserCog :size="28" stroke-width="2.5" />
        </div>
        <div>
          <h2 class="title">Quản lý Hội viên</h2>
          <p class="subtitle">Giám sát và phân quyền cộng đồng Gomet</p>
        </div>
      </div>
      <div class="header-actions">
        <button class="btn-refresh-lux" @click="fetchUsers" :disabled="isLoading">
          <RefreshCw :size="18" :class="{ 'spin-icon': isLoading }" />
          <span>Đồng bộ</span>
        </button>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card-lux">
        <div class="stat-icon-wrapper bg-blue-light text-blue">
          <Users :size="28" />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ users.length }}</span>
          <span class="stat-label">Tổng người dùng</span>
        </div>
      </div>
      <div class="stat-card-lux">
        <div class="stat-icon-wrapper bg-orange-light text-orange">
          <Crown :size="28" />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ premiumCount }}</span>
          <span class="stat-label">Thành viên Premium</span>
        </div>
      </div>
      <div class="stat-card-lux danger">
        <div class="stat-icon-wrapper bg-red-light text-red">
          <UserX :size="28" />
        </div>
        <div class="stat-info">
          <span class="stat-value text-red">{{ bannedCount }}</span>
          <span class="stat-label">Tài khoản bị khóa</span>
        </div>
      </div>
    </div>

    <div class="filter-bar-lux">
      <div class="tabs-lux">
        <button v-for="tab in filterTabs" :key="tab.value" 
                :class="['filter-tab-lux', currentFilter === tab.value ? 'active' : '']"
                @click="currentFilter = tab.value">
          {{ tab.label }}
        </button>
      </div>
      
      <div class="search-box-lux">
        <Search :size="18" class="search-icon" />
        <input v-model="searchQuery" type="text" placeholder="Tìm tên, email, ID..." class="search-input" />
        <button v-if="searchQuery" @click="searchQuery = ''" class="clear-search">
          <XCircle :size="18" />
        </button>
      </div>
    </div>

    <div class="table-lux-wrapper">
      <div v-if="isLoading" class="empty-state-lux">
        <div class="spinner-modern"></div>
        <p>Đang tải dữ liệu hội viên...</p>
      </div>

      <table v-else class="data-table-lux">
        <thead>
          <tr>
            <th width="8%">ID</th>
            <th width="32%">NGƯỜI DÙNG</th>
            <th width="15%">VAI TRÒ</th>
            <th width="15%">TRẠNG THÁI</th>
            <th width="10%" class="text-center">ĐIỂM</th>
            <th width="20%" class="text-right">THAO TÁC</th>
          </tr>
        </thead>
        <TransitionGroup tag="tbody" name="list-anim">
          <tr v-for="user in filteredUsers" :key="user.accountID" class="table-row-lux">
            <td class="col-id font-bold text-gray-500">#{{ user.accountID }}</td>
            <td>
              <div class="user-cell">
                <div class="avatar-ring-lux" :class="getAvatarClass(user)">
                  <img :src="user.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(user.username)}&background=f1f5f9&color=475569`" class="avatar" loading="lazy">
                  <div v-if="user.role === 'ADMIN'" class="badge-corner corner-admin"><Shield :size="10" /></div>
                  <div v-else-if="user.isPremium" class="badge-corner corner-premium"><Crown :size="10" /></div>
                </div>
                <div class="u-info">
                  <span class="name" :class="getTextGradientClass(user)">{{ user.username }}</span>
                  <span class="sub-name">{{ user.email }}</span>
                </div>
              </div>
            </td>
            <td>
              <span class="role-badge" :class="user.role === 'ADMIN' ? 'role-admin' : 'role-user'">
                <Shield v-if="user.role === 'ADMIN'" :size="14" />
                <User v-else :size="14" />
                {{ user.role === 'ADMIN' ? 'Quản trị viên' : 'Thành viên' }}
              </span>
            </td>
            <td>
              <span class="status-pill" :class="user.isActive === 1 ? 'active' : (user.isActive === 0 ? 'hidden' : 'banned')">
                <span class="status-dot"></span> {{ user.isActive === 1 ? 'Hoạt động' : (user.isActive === 0 ? 'Tạm ẩn' : 'Bị khóa') }}
              </span>
            </td>
            <td class="text-center">
              <span class="point-badge"><Star :size="14" class="icon-star" fill="currentColor" /> {{ user.point ?? 0 }}</span>
            </td>
            <td class="text-right">
              <div class="action-group">
                <button @click="openDetail(user)" class="btn-action view" title="Xem hồ sơ">
                  <Eye :size="16" />
                </button>
                
                <button @click="askBanAction(user)" class="btn-action" 
                        :class="[user.isActive !== -1 ? 'ban' : 'unban', { 'disabled': !canBanUser(user) }]" 
                        :disabled="!canBanUser(user)"
                        :title="!canBanUser(user) ? 'Không thể khóa tài khoản này' : (user.isActive !== -1 ? 'Khóa tài khoản' : 'Mở khóa')">
                  <Lock v-if="user.isActive !== -1" :size="16" />
                  <Unlock v-else :size="16" />
                </button>
                
                <button v-if="isSuperAdmin && user.role !== 'ADMIN'" 
                        @click="askPromoteAdmin(user)" 
                        class="btn-action promote" 
                        title="Thăng cấp Quản trị viên">
                  <ArrowUpRight :size="16" />
                </button>

                </div>
            </td>
          </tr>

          <tr v-if="filteredUsers.length === 0">
            <td colspan="6">
              <div class="empty-state-lux">
                <div class="empty-icon-box"><Search :size="48" /></div>
                <p>Không tìm thấy hội viên nào phù hợp.</p>
              </div>
            </td>
          </tr>
        </TransitionGroup>
      </table>
    </div>

    <Transition name="fade-scale">
      <div v-if="detailModal.show" class="modal-overlay-lux" @click.self="detailModal.show = false">
        <div class="modal-card-lux profile-card">
          <button class="btn-close-lux" @click="detailModal.show = false"><X :size="20" /></button>

          <div v-if="detailModal.loading" class="empty-state-lux"><div class="spinner-modern"></div></div>

          <template v-else-if="detailModal.user">
            <div class="profile-cover" :class="getCoverClass(detailModal.user)">
              <span class="absolute-status" :class="detailModal.user.isActive === 1 ? 'bg-green' : (detailModal.user.isActive === 0 ? 'bg-gray' : 'bg-red')">
                {{ detailModal.user.isActive === 1 ? 'Đang hoạt động' : (detailModal.user.isActive === 0 ? 'Tạm ẩn' : 'Bị khóa') }}
              </span>
            </div>

            <div class="profile-body">
              <div class="profile-avatar-wrapper">
                <div class="profile-avatar-ring" :class="getAvatarClass(detailModal.user)">
                  <img :src="detailModal.user.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(detailModal.user.username)}&background=fff&color=0f172a`" class="profile-avatar" />
                </div>
              </div>
              
              <div class="profile-header-text">
                <h2 class="profile-name" :class="getTextGradientClass(detailModal.user)">{{ detailModal.user.username }}</h2>
                <p class="profile-email">{{ detailModal.user.email }}</p>
                <div class="profile-tags">
                  <span class="tag" :class="detailModal.user.role === 'ADMIN' ? 'tag-admin' : 'tag-user'">
                    <Shield v-if="detailModal.user.role === 'ADMIN'" :size="14" />
                    <User v-else :size="14" />
                    {{ detailModal.user.role === 'ADMIN' ? 'Quản trị viên' : 'Thành viên' }}
                  </span>
                  <span v-if="detailModal.user.isPremium" class="tag tag-vip"><Crown :size="14" /> Premium</span>
                </div>
              </div>

              <div class="profile-stats-box">
                <div class="p-stat"><strong>{{ detailModal.user.point ?? 0 }}</strong><span>Điểm số</span></div>
                <div class="stat-divider"></div>
                <div class="p-stat"><strong>{{ detailModal.user.postCount ?? 0 }}</strong><span>Bài viết</span></div>
                <div class="stat-divider"></div>
                <div class="p-stat"><strong>{{ detailModal.user.totalLikes ?? 0 }}</strong><span>Lượt thích</span></div>
                <div class="stat-divider"></div>
                <div class="p-stat"><strong>{{ detailModal.user.followerCount ?? 0 }}</strong><span>Followers</span></div>
              </div>

              <div class="profile-details-lux">
                <div class="p-detail-item">
                  <span class="lbl"><Hash :size="16" /> ID Hệ thống</span>
                  <strong class="val">#{{ detailModal.user.accountID }}</strong>
                </div>
                <div class="p-detail-item">
                  <span class="lbl"><Calendar :size="16" /> Ngày tham gia</span>
                  <strong class="val">{{ detailModal.user.createdAt ? new Date(detailModal.user.createdAt).toLocaleDateString('vi-VN') : '—' }}</strong>
                </div>
                
                <div v-if="detailModal.user.isActive === -1" class="p-detail-ban-reason">
                  <div class="ban-header">
                    <AlertTriangle :size="16" /> 
                    <span>Thông tin khóa tài khoản</span>
                  </div>
                  <div class="ban-info-grid">
                    <div class="ban-info-item">
                      <span class="info-lbl">Lý do khóa:</span>
                      <span class="info-val reason-text">{{ detailModal.user.banReason || 'Chưa cập nhật lý do' }}</span>
                    </div>
                    <div v-if="detailModal.user.bannedAt" class="ban-info-item">
                       <span class="info-lbl">Thời gian xử lý:</span>
                       <span class="info-val time-text">{{ new Date(detailModal.user.bannedAt).toLocaleString('vi-VN') }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="profile-actions-grid">
                <button v-if="isSuperAdmin && detailModal.user.role !== 'ADMIN'" 
                        @click="askPromoteAdmin(detailModal.user); detailModal.show = false" 
                        class="btn-lux-promote">
                  <Shield :size="18" /> Cấp quyền Quản trị
                </button>

                <button v-if="canBanUser(detailModal.user)" 
                        @click="askBanAction(detailModal.user); detailModal.show = false" 
                        class="btn-lux-ban" :class="detailModal.user.isActive !== -1 ? 'warn' : 'ok'">
                  <Lock v-if="detailModal.user.isActive !== -1" :size="18" />
                  <Unlock v-else :size="18" />
                  {{ detailModal.user.isActive !== -1 ? 'Khóa tài khoản' : 'Khôi phục tài khoản' }}
                </button>

                <div v-else class="admin-protect-msg full-width">
                  <Shield :size="18" /> Không thể thay đổi trạng thái tài khoản này
                </div>

                <router-link :to="`/profile/${detailModal.user.accountID}`" target="_blank" class="btn-lux-view-post full-width">
                  <span>Xem hồ sơ công khai</span>
                  <ExternalLink :size="18" />
                </router-link>
              </div>
            </div>
          </template>
        </div>
      </div>
    </Transition>

    <Transition name="fade-scale">
      <div v-if="actionModal.show" class="modal-overlay-lux" @click.self="actionModal.show = false">
        <div class="action-modal-card">
          <div class="action-icon" :class="actionModal.styleClass">
            <component :is="actionModal.icon" :size="32" />
          </div>
          <h3>{{ actionModal.title }}</h3>
          <p v-html="actionModal.message"></p>
          
          <div v-if="actionModal.type === 'ban'" class="reason-input-group">
            <label for="banReason">Lý do khóa (bắt buộc):</label>
            <textarea 
              id="banReason" 
              v-model="actionModal.reason" 
              placeholder="Ví dụ: Spam, lừa đảo, vi phạm ngôn từ..." 
              rows="3" 
              class="reason-textarea"
            ></textarea>
            <span v-if="actionModal.showError" class="error-msg">Bạn phải nhập lý do khóa!</span>
          </div>

          <div class="action-btns">
            <button class="btn-cancel" @click="actionModal.show = false">Hủy bỏ</button>
            <button class="btn-confirm" :class="actionModal.btnClass" @click="executeAction">
              Xác nhận
            </button>
          </div>
        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, markRaw } from 'vue'
import { UserCog, RefreshCw, Users, Crown, UserX, Search, XCircle, Shield, User, Star, Eye, Lock, Unlock, ArrowUpRight, X, Hash, Calendar, AlertTriangle, ExternalLink } from 'lucide-vue-next'
import api from '@/services/api'
import { toast } from '@/composables/useToast'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 🔥 ĐIỀU KIỆN SẾP TỔNG: Chỉ user có ID = 1 mới được cấp quyền Admin cho người khác
const isSuperAdmin = computed(() => {
  return authStore.user && (String(authStore.user.accountID) === '1' || String(authStore.user.id) === '1');
});

const canBanUser = (targetUser) => {
  if (String(targetUser.accountID) === '1') return false; // Không ai được khóa Super Admin (ID = 1)
  if (targetUser.role === 'ADMIN') return isSuperAdmin.value; // Chỉ Super Admin mới được khóa Admin khác
  return true; 
}

const users = ref([])
const isLoading = ref(true)
const searchQuery = ref('')
const currentFilter = ref('ALL')

const filterTabs = [
  { label: 'Tất cả', value: 'ALL' },
  { label: 'Quản trị viên', value: 'ADMIN' },
  { label: 'Premium', value: 'PREMIUM' },
  { label: 'Tạm ẩn', value: 'HIDDEN' },
  { label: 'Bị khóa', value: 'BANNED' }
]

// Dùng chung 1 Modal cho Ban, Unban và Promote
const actionModal = ref({ 
  show: false, type: '', accountID: null, userRef: null, 
  title: '', message: '', icon: null, styleClass: '', btnClass: '',
  reason: '', showError: false 
})

const detailModal = ref({ show: false, loading: false, user: null })

const getAvatarClass = (user) => {
  if (user.role === 'ADMIN') return 'ring-admin'
  if (user.isPremium === 1 || user.isPremium === true) return 'ring-premium'
  return 'ring-normal'
}

const getTextGradientClass = (user) => user.role === 'ADMIN' ? 'text-admin' : ''

const getCoverClass = (user) => {
  if (user.role === 'ADMIN') return 'cover-admin'
  if (user.isPremium === 1 || user.isPremium === true) return 'cover-premium'
  return 'cover-normal'
}

const premiumCount = computed(() => users.value.filter(u => u.isPremium === 1 || u.isPremium === true).length)
const bannedCount = computed(() => users.value.filter(u => u.isActive === -1).length)

const filteredUsers = computed(() => {
  let result = users.value
  if (currentFilter.value === 'ADMIN') result = result.filter(u => u.role === 'ADMIN')
  if (currentFilter.value === 'PREMIUM') result = result.filter(u => u.isPremium === 1 || u.isPremium === true)
  if (currentFilter.value === 'HIDDEN') result = result.filter(u => u.isActive === 0)
  if (currentFilter.value === 'BANNED') result = result.filter(u => u.isActive === -1)

  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(u => 
      u.username?.toLowerCase().includes(q) || 
      u.email?.toLowerCase().includes(q) ||
      String(u.accountID).includes(q)
    )
  }
  return result
})

const fetchUsers = async () => {
  isLoading.value = true
  try {
    const res = await api.get('/admin/accounts')
    users.value = res.data.map(u => ({
      ...u,
      role: u.role ? String(u.role).toUpperCase() : (u.isAdmin ? 'ADMIN' : 'USER'),
      isActive: Number(u.isActive ?? 1),
      isPremium: u.isPremium === 1 || u.isPremium === true
    }))
  } catch (err) {
    toast.error('Không thể kết nối đến máy chủ!')
  } finally {
    isLoading.value = false
  }
}

// --- HÀM GỌI MODAL THAO TÁC ---
const askBanAction = (user) => {
  if (!canBanUser(user)) return;
  const isBanned = user.isActive === -1;
  actionModal.value = {
    show: true, type: !isBanned ? 'ban' : 'unban', accountID: user.accountID, userRef: user,
    title: !isBanned ? 'Khóa tài khoản?' : 'Mở khóa tài khoản?',
    message: `Xác nhận ${!isBanned ? 'khóa' : 'khôi phục'} quyền truy cập của <strong>${user.username}</strong>?`,
    icon: !isBanned ? markRaw(Lock) : markRaw(Unlock),
    styleClass: !isBanned ? 'bg-red-light text-red' : 'bg-green-light text-green',
    btnClass: !isBanned ? 'btn-danger' : 'btn-success',
    reason: '', showError: false
  }
}

const askPromoteAdmin = (user) => {
  actionModal.value = {
    show: true, type: 'promote', accountID: user.accountID, userRef: user,
    title: 'Cấp quyền Quản trị viên?',
    message: `Bạn đang chuẩn bị cấp quyền Admin cho <strong>${user.username}</strong>. Người này sẽ có quyền quản lý toàn bộ hệ thống. Tiếp tục?`,
    icon: markRaw(Shield),
    styleClass: 'bg-blue-light text-blue',
    btnClass: 'btn-primary',
    reason: '', showError: false
  }
}

// --- THỰC THI THAO TÁC (TỪ MODAL) ---
const executeAction = async () => {
  const { type, accountID, userRef, reason } = actionModal.value
  
  if (type === 'ban' && !reason.trim()) {
    actionModal.value.showError = true; return;
  }

  try {
    if (type === 'ban' || type === 'unban') {
      const payload = {
        adminId: authStore.user?.accountID || authStore.user?.id || 0,
        adminName: authStore.user?.username || authStore.user?.fullName || 'Admin',
        reason: type === 'ban' ? reason.trim() : ''
      };
      if (type === 'ban') {
        await api.patch(`/admin/accounts/${accountID}/ban`, payload);
        userRef.isActive = -1;
      } else {
        await api.patch(`/admin/accounts/${accountID}/unban`, payload);
        userRef.isActive = 1;
      }
      toast.success('Cập nhật trạng thái thành công!')
    } 
    
    // 🔥 CALL API THĂNG CẤP ADMIN (Cần cập nhật Backend bằng hàm Save)
    else if (type === 'promote') {
      const dto = {
        accountID: accountID,
        role: "ADMIN",
        isAdmin: 1
      }
      await api.put(`/admin/accounts/${accountID}`, dto);
      userRef.role = 'ADMIN';
      toast.success(`Đã thăng cấp ${userRef.username} làm Quản trị viên!`);
    }
    
    actionModal.value.show = false;
    fetchUsers() 
  } catch (err) {
    toast.error(`Thao tác thất bại! ${err.response?.data?.message || ''}`)
  }
}

const openDetail = async (user) => {
  detailModal.value = { show: true, loading: true, user: null }
  try {
    const res = await api.get(`/admin/accounts/${user.accountID}`)
    const u = res.data;
    detailModal.value.user = {
      ...u,
      role: u.role ? String(u.role).toUpperCase() : (u.isAdmin ? 'ADMIN' : 'USER'),
      isActive: Number(u.isActive ?? 1),
      isPremium: u.isPremium === 1 || u.isPremium === true
    }
  } catch {
    detailModal.value.user = { ...user }
  } finally {
    detailModal.value.loading = false
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Playfair+Display:wght@800&display=swap');

/* --- CORE & LAYOUT --- */
.page-container { padding: 40px 48px; font-family: 'Inter', sans-serif; background: #F8F9FA; min-height: 100vh; color: #1E293B; }

/* --- HEADER LUXURY --- */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px; }
.title-group { display: flex; align-items: center; gap: 20px; }
.icon-box-lux { width: 56px; height: 56px; border-radius: 16px; background: linear-gradient(135deg, #1E293B, #0F172A); color: white; display: flex; align-items: center; justify-content: center; font-size: 1.4rem; box-shadow: 0 10px 20px -5px rgba(15, 23, 42, 0.4); }
.title { font-family: 'Playfair Display', serif; font-size: 2.2rem; font-weight: 800; color: #0F172A; margin: 0; }
.subtitle { color: #64748B; margin: 4px 0 0; font-size: 1rem; font-weight: 500; }

.btn-refresh-lux { background: white; border: 1px solid #E2E8F0; padding: 12px 24px; border-radius: 14px; font-weight: 700; color: #475569; cursor: pointer; transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); display: flex; align-items: center; gap: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.02); }
.btn-refresh-lux:hover:not(:disabled) { border-color: #0F172A; background: #0F172A; color: white; transform: translateY(-2px); box-shadow: 0 8px 20px rgba(15, 23, 42, 0.15); }
.btn-refresh-lux:active { transform: scale(0.95); }

/* --- STATS CARDS --- */
.stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 24px; margin-bottom: 32px; }
.stat-card-lux { background: white; padding: 24px; border-radius: 20px; display: flex; align-items: center; gap: 20px; border: 1px solid #E2E8F0; box-shadow: 0 4px 15px rgba(0,0,0,0.03); transition: 0.3s; }
.stat-card-lux:hover { transform: translateY(-4px); box-shadow: 0 12px 25px rgba(0,0,0,0.06); border-color: #CBD5E1; }
.stat-icon-wrapper { width: 60px; height: 60px; border-radius: 16px; display: flex; align-items: center; justify-content: center; font-size: 1.6rem; }
.bg-blue-light { background: #EFF6FF; } .text-blue { color: #3B82F6; }
.bg-orange-light { background: #FFF7ED; } .text-orange { color: #EA580C; }
.bg-red-light { background: #FEF2F2; } .text-red { color: #EF4444; } 
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 1.8rem; font-weight: 800; color: #0F172A; line-height: 1.2; }
.stat-label { font-size: 0.9rem; color: #64748B; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }
.stat-card-lux.danger { border-color: #FECACA; background: #FFFCFC; }

/* --- FILTER BAR --- */
.filter-bar-lux { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.tabs-lux { display: flex; background: white; padding: 6px; border-radius: 16px; border: 1px solid #E2E8F0; box-shadow: 0 4px 10px rgba(0,0,0,0.02); }
.filter-tab-lux { background: transparent; border: none; padding: 10px 24px; border-radius: 12px; font-weight: 700; color: #64748B; cursor: pointer; transition: 0.3s; font-size: 0.95rem; }
.filter-tab-lux:hover { color: #0F172A; }
.filter-tab-lux.active { background: #1C1917; color: white; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }

.search-box-lux { position: relative; display: flex; align-items: center; width: 340px; background: white; border: 1.5px solid #E2E8F0; border-radius: 16px; transition: 0.3s; box-shadow: 0 4px 10px rgba(0,0,0,0.02); }
.search-box-lux:focus-within { border-color: #EA580C; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); }
.search-icon { position: absolute; left: 16px; color: #94A3B8; font-size: 1rem; }
.search-input { width: 100%; padding: 14px 40px; border: none; background: transparent; outline: none; font-weight: 600; color: #0F172A; }
.clear-search { position: absolute; right: 12px; background: none; border: none; color: #94A3B8; cursor: pointer; font-size: 1.1rem; transition: 0.2s; }
.clear-search:hover { color: #EF4444; }

/* --- DATA TABLE LUXURY --- */
.table-lux-wrapper { background: white; border-radius: 24px; border: 1px solid #E2E8F0; box-shadow: 0 10px 40px -10px rgba(0,0,0,0.06); overflow: hidden; min-height: 400px; }
.data-table-lux { width: 100%; border-collapse: separate; border-spacing: 0; }
.data-table-lux th { background: #F8FAFC; padding: 20px 24px; text-align: left; font-size: 0.8rem; font-weight: 800; color: #64748B; text-transform: uppercase; letter-spacing: 1px; border-bottom: 1px solid #E2E8F0; }
.data-table-lux td { padding: 16px 24px; border-bottom: 1px solid #F1F5F9; vertical-align: middle; }
.table-row-lux { transition: 0.3s ease; }
.table-row-lux:hover { background: #FAFAFA; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(0,0,0,0.02); }

.user-cell { display: flex; align-items: center; gap: 14px; }
.avatar-ring-lux { position: relative; display: inline-flex; padding: 3px; border-radius: 50%; background: white; }
.avatar { width: 44px; height: 44px; border-radius: 50%; border: 2px solid white; object-fit: cover; }
.ring-admin { background: linear-gradient(135deg, #3B82F6, #8B5CF6); box-shadow: 0 4px 10px rgba(139, 92, 246, 0.3); }
.ring-premium { background: linear-gradient(135deg, #F59E0B, #EA580C); box-shadow: 0 4px 10px rgba(234, 88, 12, 0.3); }
.ring-normal .avatar { border: 1px solid #E2E8F0; } 
.badge-corner { position: absolute; bottom: 0; right: 0; width: 18px; height: 18px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.6rem; color: white; border: 2px solid white; }
.corner-admin { background: #8B5CF6; } .corner-premium { background: #EA580C; }

.u-info { display: flex; flex-direction: column; }
.name { font-weight: 700; font-size: 1rem; color: #0F172A; }
.sub-name { font-size: 0.85rem; color: #64748B; font-weight: 500; }
.text-admin { background: linear-gradient(135deg, #3B82F6, #8B5CF6); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }

.role-badge { display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px; border-radius: 10px; font-size: 0.8rem; font-weight: 800; }
.role-admin { background: #EFF6FF; color: #2563EB; border: 1px solid #BFDBFE; }
.role-user { background: #F8FAFC; color: #475569; border: 1px solid #E2E8F0; }

.status-pill { display: inline-flex; align-items: center; gap: 8px; padding: 6px 14px; border-radius: 100px; font-size: 0.8rem; font-weight: 700; }
.status-dot { width: 8px; height: 8px; border-radius: 50%; }
.status-pill.active { background: #F0FDF4; color: #16A34A; } .status-pill.active .status-dot { background: #16A34A; }
.status-pill.banned { background: #FEF2F2; color: #DC2626; } .status-pill.banned .status-dot { background: #DC2626; }
.status-pill.hidden { background: #F8FAFC; color: #64748B; } .status-pill.hidden .status-dot { background: #94A3B8; }

.point-badge { font-weight: 700; color: #475569; font-size: 0.95rem; }
.icon-star { color: #F59E0B; }

.action-group { display: flex; justify-content: flex-end; gap: 10px; }
.btn-action { width: 38px; height: 38px; border-radius: 12px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 1rem; transition: 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); background: #F1F5F9; color: #64748B; }
.btn-action:hover:not(.disabled) { transform: translateY(-3px); box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
.btn-action:active:not(.disabled) { transform: scale(0.9); }
.view:hover { background: #0F172A; color: white; }
.ban:hover { background: #EA580C; color: white; box-shadow: 0 4px 10px rgba(234, 88, 12, 0.3); }
.unban:hover { background: #16A34A; color: white; box-shadow: 0 4px 10px rgba(22, 163, 74, 0.3); }
.promote:hover { background: #3B82F6; color: white; box-shadow: 0 4px 10px rgba(59, 130, 246, 0.3); }
.btn-action.disabled { opacity: 0.4; cursor: not-allowed; }

/* --- PROFILE MODAL LUXURY --- */
.modal-overlay-lux { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.75); display: flex; align-items: center; justify-content: center; z-index: 9999; backdrop-filter: blur(8px); }
.modal-card-lux { background: white; border-radius: 28px; position: relative; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.5); overflow: hidden; }
.profile-card { width: 480px; max-width: 95vw; max-height: 90vh; overflow-y: auto; }
.profile-card::-webkit-scrollbar { width: 6px; } .profile-card::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }
.btn-close-lux { position: absolute; top: 16px; right: 16px; background: rgba(0,0,0,0.4); color: white; border: none; border-radius: 50%; width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; font-size: 1.2rem; cursor: pointer; z-index: 10; transition: 0.3s; backdrop-filter: blur(4px); }
.btn-close-lux:hover { background: #EF4444; transform: rotate(90deg); }

.profile-cover { height: 140px; position: relative; }
.cover-normal { background: #CBD5E1; }
.cover-premium { background: linear-gradient(135deg, #F59E0B, #EA580C); }
.cover-admin { background: linear-gradient(135deg, #3B82F6, #8B5CF6); }
.absolute-status { position: absolute; top: 16px; left: 16px; padding: 6px 14px; border-radius: 8px; font-size: 0.8rem; font-weight: 700; color: white; box-shadow: 0 4px 10px rgba(0,0,0,0.2); }
.bg-green { background: #16A34A; } .bg-red { background: #DC2626; } .bg-gray { background: #64748B; }

.profile-body { padding: 0 32px 32px; text-align: center; margin-top: -50px; position: relative; z-index: 2; }
.profile-avatar-wrapper { display: flex; justify-content: center; margin-bottom: 12px; }
.profile-avatar-ring { display: inline-flex; padding: 6px; border-radius: 50%; background: white; }
.profile-avatar { width: 90px; height: 90px; border-radius: 50%; border: 2px solid white; object-fit: cover; }

.profile-name { font-size: 1.6rem; font-weight: 800; color: #0F172A; margin: 0 0 4px; }
.profile-email { color: #64748B; font-size: 0.95rem; margin: 0 0 20px; font-weight: 500; }
.profile-tags { display: flex; justify-content: center; gap: 10px; margin-bottom: 28px; }
.tag { padding: 6px 16px; border-radius: 100px; font-size: 0.8rem; font-weight: 700; display: flex; align-items: center; gap: 6px; }
.tag-admin { background: #EFF6FF; color: #2563EB; border: 1px solid #BFDBFE; } 
.tag-user { background: #F8FAFC; color: #475569; border: 1px solid #E2E8F0; } 
.tag-vip { background: #FFFBEB; color: #D97706; border: 1px solid #FDE68A; }

.profile-stats-box { display: flex; justify-content: space-around; background: #F8FAFC; padding: 20px; border-radius: 20px; margin-bottom: 28px; border: 1px solid #E2E8F0; }
.p-stat { display: flex; flex-direction: column; gap: 4px; }
.p-stat strong { font-size: 1.3rem; color: #0F172A; font-weight: 800; line-height: 1; }
.p-stat span { font-size: 0.75rem; color: #64748B; font-weight: 700; text-transform: uppercase; }
.stat-divider { width: 1px; background: #E2E8F0; }

.profile-details-lux { text-align: left; margin-bottom: 28px; }
.p-detail-item { display: flex; justify-content: space-between; padding: 14px 0; border-bottom: 1px solid #F1F5F9; font-size: 0.95rem; }
.p-detail-item .lbl { color: #64748B; font-weight: 600; display: flex; align-items: center; gap: 8px; }
.p-detail-item .val { color: #0F172A; font-weight: 800; }

/* THÔNG TIN BAN */
.p-detail-ban-reason { margin-top: 20px; background: #FFF5F5; border-radius: 16px; border: 1.5px solid #FECACA; overflow: hidden; }
.ban-header { background: #FEF2F2; padding: 12px 16px; color: #DC2626; font-weight: 800; font-size: 0.9rem; display: flex; align-items: center; gap: 8px; border-bottom: 1px solid #FEE2E2; }
.ban-info-grid { padding: 16px; display: flex; flex-direction: column; gap: 12px; }
.ban-info-item { display: flex; flex-direction: column; gap: 6px; }
.info-lbl { font-size: 0.8rem; color: #991B1B; font-weight: 800; text-transform: uppercase; }
.reason-text { font-style: italic; background: white; padding: 12px; border-radius: 10px; border: 1px dashed #FCA5A5; color: #7F1D1D; font-weight: 600; }
.time-text { font-weight: 700; color: #991B1B; font-size: 0.95rem; }

/* ACTION BUTTONS TRONG MODAL */
.profile-actions-grid { display: flex; flex-direction: column; gap: 12px; }
.btn-lux-promote, .btn-lux-ban, .btn-lux-view-post { width: 100%; padding: 14px; border-radius: 14px; font-weight: 800; font-size: 1rem; cursor: pointer; display: flex; justify-content: center; align-items: center; gap: 10px; transition: 0.3s; border: none; text-decoration: none; }
.btn-lux-promote { background: linear-gradient(135deg, #3B82F6, #2563EB); color: white; box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3); }
.btn-lux-promote:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4); }
.btn-lux-ban.warn { background: #FFF7ED; color: #EA580C; border: 1.5px solid #FED7AA; } .btn-lux-ban.warn:hover { background: #EA580C; color: white; }
.btn-lux-ban.ok { background: #F0FDF4; color: #16A34A; border: 1.5px solid #BBF7D0; } .btn-lux-ban.ok:hover { background: #16A34A; color: white; }
.btn-lux-view-post { background: #F1F5F9; color: #0F172A; border: 1.5px solid #E2E8F0; } .btn-lux-view-post:hover { background: #0F172A; color: white; }
.admin-protect-msg { background: #F8FAFC; color: #64748B; padding: 14px; border-radius: 14px; font-weight: 600; font-size: 0.9rem; text-align: center; border: 1px dashed #CBD5E1; display: flex; justify-content: center; align-items: center; gap: 8px; }

/* --- ACTION MODAL (CONFIRM) --- */
.action-modal-card { background: white; padding: 32px; border-radius: 24px; width: 420px; max-width: 95vw; text-align: center; }
.action-icon { width: 64px; height: 64px; border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center; font-size: 1.6rem; }
.action-modal-card h3 { font-size: 1.3rem; color: #0F172A; margin: 0 0 10px; font-weight: 800; }
.action-modal-card p { color: #64748B; font-size: 1rem; margin: 0 0 24px; line-height: 1.5; }
.reason-input-group { text-align: left; margin-bottom: 24px; }
.reason-input-group label { display: block; font-size: 0.9rem; font-weight: 700; color: #475569; margin-bottom: 8px; }
.reason-textarea { width: 100%; padding: 14px; border: 1.5px solid #CBD5E1; border-radius: 12px; outline: none; font-family: inherit; font-size: 0.95rem; resize: none; background: #F8FAFC; transition: 0.3s; }
.reason-textarea:focus { border-color: #EA580C; background: white; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); }
.error-msg { display: block; color: #DC2626; font-size: 0.85rem; font-weight: 600; margin-top: 6px; }
.action-btns { display: flex; gap: 16px; }
.btn-cancel, .btn-confirm { flex: 1; padding: 14px; border-radius: 12px; font-weight: 800; font-size: 1rem; cursor: pointer; border: none; transition: 0.3s; }
.btn-cancel { background: #F1F5F9; color: #475569; } .btn-cancel:hover { background: #E2E8F0; color: #0F172A; }
.btn-danger { background: #EF4444; color: white; box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3); } .btn-danger:hover { background: #DC2626; transform: translateY(-2px); box-shadow: 0 8px 20px rgba(239, 68, 68, 0.4); }
.btn-success { background: #10B981; color: white; box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3); } .btn-success:hover { background: #059669; transform: translateY(-2px); box-shadow: 0 8px 20px rgba(16, 185, 129, 0.4); }
.btn-primary { background: linear-gradient(135deg, #3B82F6, #2563EB); color: white; box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3); } .btn-primary:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4); }

.spinner-modern { width: 36px; height: 36px; border: 3px solid #E2E8F0; border-top-color: #EA580C; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto 16px; }
.spin-icon { animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.empty-state-lux { padding: 80px 20px; text-align: center; color: #94A3B8; font-weight: 600; font-size: 1.1rem; }
.empty-icon-box { font-size: 3rem; margin-bottom: 16px; color: #CBD5E1; }

.anim-fade-down { animation: fadeDown 0.6s cubic-bezier(0.2, 0.8, 0.2, 1); }
.anim-fade-up { animation: fadeUp 0.6s cubic-bezier(0.2, 0.8, 0.2, 1) both; animation-delay: var(--delay, 0s); }
@keyframes fadeDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.list-anim-enter-active, .list-anim-leave-active { transition: all 0.5s cubic-bezier(0.2, 0.8, 0.2, 1); }
.list-anim-enter-from, .list-anim-leave-to { opacity: 0; transform: translateY(20px) scale(0.98); }
.fade-scale-enter-active, .fade-scale-leave-active { transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1); }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; transform: scale(0.95); }
</style>