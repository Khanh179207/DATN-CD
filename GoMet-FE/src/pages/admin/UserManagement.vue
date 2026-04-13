<template>
  <div class="page-container animate-enter">
    
    <div class="page-header">
      <div class="title-group">
        <h2 class="title">{{ $t('admin.user_management.title') }}</h2>
        <p class="subtitle">{{ $t('admin.user_management.subtitle') }}</p>
      </div>
      <div class="header-actions">
        <button class="btn-refresh" @click="fetchUsers" :disabled="isLoading">
          <i class="fa-solid fa-rotate-right" :class="{ 'fa-spin': isLoading }"></i> {{ $t('admin.common.sync') }}
        </button>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon-wrapper bg-blue-light">
          <i class="fa-solid fa-users text-blue"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ users.length }}</span>
          <span class="stat-label">{{ $t('admin.user_management.total_users') }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon-wrapper bg-orange-light">
          <i class="fa-solid fa-crown text-orange"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ premiumCount }}</span>
          <span class="stat-label">{{ $t('admin.user_management.premium_accounts') }}</span>
        </div>
      </div>
      <div class="stat-card stat-danger">
        <div class="stat-icon-wrapper bg-red-light">
          <i class="fa-solid fa-user-lock text-red"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value text-red">{{ bannedCount }}</span>
          <span class="stat-label">{{ $t('admin.user_management.banned_accounts') }}</span>
        </div>
      </div>
    </div>

    <div class="filter-bar">
      <div class="tabs">
        <button v-for="tab in filterTabs" :key="tab.value" 
                :class="['filter-tab', currentFilter === tab.value ? 'active' : '']"
                @click="currentFilter = tab.value">
          {{ tab.label }}
        </button>
      </div>
      
      <div class="search-box">
        <i class="fa-solid fa-search search-icon"></i>
        <input v-model="searchQuery" type="text" :placeholder="$t('admin.common.search_accounts')" class="search-input" />
        <button v-if="searchQuery" @click="searchQuery = ''" class="clear-search">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>
    </div>

    <div class="table-wrapper">
      <div v-if="isLoading" class="loading-state">
        <div class="spinner-modern"></div>
        <span>{{ $t('admin.common.loading_data') }}</span>
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th width="8%">ID</th>
            <th width="35%">{{ $t('admin.user_management.user_col') }}</th>
            <th width="15%">{{ $t('admin.user_management.role_col') }}</th>
            <th width="15%">{{ $t('admin.user_management.status_col') }}</th>
            <th width="12%" class="text-center">{{ $t('admin.user_management.points_col') }}</th>
            <th width="15%" class="text-center">{{ $t('admin.user_management.actions_col') }}</th>
          </tr>
        </thead>
        <TransitionGroup tag="tbody" name="list">
          <tr v-for="user in filteredUsers" :key="user.accountID" class="table-row">
            <td class="col-id">#{{ user.accountID }}</td>
            <td>
              <div class="user-cell">
                
                <div class="avatar-ring" :class="getAvatarClass(user)">
                  <img :src="user.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(user.username)}&background=f1f5f9&color=475569`" class="avatar" loading="lazy">
                  <div v-if="user.role === 'ADMIN'" class="badge-corner corner-admin"><i class="fa-solid fa-shield-halved"></i></div>
                  <div v-else-if="user.isPremium" class="badge-corner corner-premium"><i class="fa-solid fa-crown"></i></div>
                </div>
                
                <div class="u-info">
                  <span class="name" :class="getTextGradientClass(user)">{{ user.username }}</span>
                  <span class="sub-name">{{ user.email }}</span>
                </div>
              </div>
            </td>
            <td>
              <span class="role-badge" :class="user.role === 'ADMIN' ? 'role-admin' : 'role-user'">
                <i :class="user.role === 'ADMIN' ? 'fa-solid fa-shield-halved' : 'fa-regular fa-user'"></i>
                {{ user.role === 'ADMIN' ? $t('admin.user_management.role_admin') : $t('admin.user_management.role_member') }}
              </span>
            </td>
            <td>
              <div class="status-pill" :class="user.isActive ? 'active' : 'banned'">
                <span class="status-dot"></span> {{ user.isActive ? $t('admin.user_management.status_active') : $t('admin.user_management.status_banned') }}
              </div>
            </td>
            <td class="text-center">
              <span class="point-badge"><i class="fa-solid fa-star icon-star"></i> {{ user.point ?? 0 }}</span>
            </td>
            <td>
              <div class="actions">
                <button @click="openDetail(user)" class="btn-action view" :title="t('admin.user_management.view_profile')">
                  <i class="fa-solid fa-eye"></i>
                </button>
                
                <button @click="askBanAction(user)" class="btn-action" 
                        :class="[user.isActive ? 'ban' : 'unban', { 'disabled': user.role === 'ADMIN' }]" 
                        :disabled="user.role === 'ADMIN'"
                        :title="user.role === 'ADMIN' ? t('admin.user_management.admin_lock_forbidden') : (user.isActive ? t('admin.user_management.lock_account') : t('admin.user_management.unlock_account'))">
                  <i :class="user.isActive ? 'fa-solid fa-lock' : 'fa-solid fa-unlock'"></i>
                </button>

                <button @click="deleteUser(user)" class="btn-action delete" 
                        :class="{ 'disabled': user.role === 'ADMIN' }"
                        :disabled="user.role === 'ADMIN'"
                        :title="t('admin.user_management.delete_permanently')">
                  <i class="fa-regular fa-trash-can"></i>
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="filteredUsers.length === 0">
            <td colspan="6" class="empty-state">
              <div class="empty-icon"><i class="fa-solid fa-folder-open"></i></div>
              <p>{{ $t('admin.user_management.empty') }}</p>
            </td>
          </tr>
        </TransitionGroup>
      </table>
    </div>

    <Transition name="modal-fade">
      <div v-if="detailModal.show" class="modal-overlay" @click.self="detailModal.show = false">
        <div class="profile-card">
          <button class="detail-close" @click="detailModal.show = false"><i class="fa-solid fa-xmark"></i></button>

          <div v-if="detailModal.loading" class="detail-loading"><div class="spinner-modern"></div></div>

          <template v-else-if="detailModal.user">
            <div class="profile-cover" :class="getCoverClass(detailModal.user)">
              <span class="absolute-status" :class="detailModal.user.isActive ? 'bg-green' : 'bg-red'">
                {{ detailModal.user.isActive ? $t('admin.user_management.status_active') : $t('admin.user_management.status_banned') }}
              </span>
            </div>

            <div class="profile-body">
              <div class="profile-avatar-ring" :class="getAvatarClass(detailModal.user)">
                <img :src="detailModal.user.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(detailModal.user.username)}&background=fff&color=0f172a`" class="profile-avatar" />
              </div>
              
              <div class="profile-header-text">
                <h2 class="profile-name" :class="getTextGradientClass(detailModal.user)">{{ detailModal.user.username }}</h2>
                <p class="profile-email">{{ detailModal.user.email }}</p>
                <div class="profile-tags">
                  <span class="tag" :class="detailModal.user.role === 'ADMIN' ? 'tag-admin' : 'tag-user'">
                    {{ detailModal.user.role === 'ADMIN' ? $t('admin.user_management.role_admin') : $t('admin.user_management.role_member') }}
                  </span>
                  <span v-if="detailModal.user.isPremium" class="tag tag-vip"><i class="fa-solid fa-crown"></i> {{ $t('admin.user_management.premium_tag') }}</span>
                </div>
              </div>

              <div class="profile-stats-box">
                <div class="p-stat"><strong>{{ detailModal.user.point ?? 0 }}</strong><span>{{ $t('admin.user_management.points') }}</span></div>
                <div class="stat-divider"></div>
                <div class="p-stat"><strong>{{ detailModal.user.postCount ?? 0 }}</strong><span>{{ $t('admin.user_management.posts') }}</span></div>
                <div class="stat-divider"></div>
                <div class="p-stat"><strong>{{ detailModal.user.totalLikes ?? 0 }}</strong><span>{{ $t('admin.user_management.likes') }}</span></div>
                <div class="stat-divider"></div>
                <div class="p-stat"><strong>{{ detailModal.user.followerCount ?? 0 }}</strong><span>{{ $t('admin.user_management.followers') }}</span></div>
              </div>

              <div class="profile-details">
                <div class="p-detail-item">
                  <span class="lbl"><i class="fa-solid fa-fingerprint"></i> {{ $t('admin.user_management.account_id') }}</span>
                  <strong class="val">#{{ detailModal.user.accountID }}</strong>
                </div>
                <div class="p-detail-item">
                  <span class="lbl"><i class="fa-regular fa-calendar"></i> {{ $t('admin.user_management.joined_at') }}</span>
                  <strong class="val">{{ detailModal.user.createdAt ? formatLocaleDate(detailModal.user.createdAt, locale) : $t('admin.common.empty_value') }}</strong>
                </div>
                <div v-if="detailModal.user.bio" class="p-detail-bio">
                  <span class="lbl"><i class="fa-solid fa-quote-left"></i> {{ $t('admin.user_management.bio') }}</span>
                  <p>{{ detailModal.user.bio }}</p>
                </div>
                
                <div v-if="!detailModal.user.isActive" class="p-detail-ban-reason">
                  <div class="ban-header">
                    <i class="fa-solid fa-triangle-exclamation"></i> 
                    <span>{{ $t('admin.user_management.ban_info') }}</span>
                  </div>
                  <div class="ban-info-grid">
                    <div class="ban-info-item">
                      <span class="info-lbl">{{ $t('admin.user_management.ban_reason') }}</span>
                      <span class="info-val reason-text">{{ detailModal.user.banReason || $t('admin.user_management.no_ban_reason') }}</span>
                    </div>
                    <div v-if="detailModal.user.bannedAt" class="ban-info-item">
                       <span class="info-lbl">{{ $t('admin.user_management.banned_at') }}</span>
                       <span class="info-val time-text">{{ formatLocaleDateTime(detailModal.user.bannedAt, locale) }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <router-link :to="`/profile/${detailModal.user.accountID}`" target="_blank" class="btn-full-profile">
                <i class="fa-solid fa-arrow-up-right-from-square"></i> {{ $t('admin.user_management.view_public_profile') }}
              </router-link>

              <div class="profile-actions" v-if="detailModal.user.role !== 'ADMIN'">
                <button @click="askBanAction(detailModal.user); detailModal.show = false" class="btn-profile-ban" :class="detailModal.user.isActive ? 'warn' : 'ok'">
                  <i :class="detailModal.user.isActive ? 'fa-solid fa-lock' : 'fa-solid fa-unlock'"></i>
                  {{ detailModal.user.isActive ? $t('admin.user_management.lock_this_account') : $t('admin.user_management.restore_account') }}
                </button>
              </div>
              <div v-else class="admin-protect-msg">
                <i class="fa-solid fa-shield-halved"></i> {{ $t('admin.user_management.admin_status_locked') }}
              </div>
            </div>
          </template>
        </div>
      </div>
    </Transition>

    <Transition name="modal-fade">
      <div v-if="banModal.show" class="modal-overlay" @click.self="banModal.show = false">
        <div class="action-modal">
          <div class="action-icon" :class="banModal.action === 'ban' ? 'bg-red-light text-red' : 'bg-green-light text-green'">
            <i :class="banModal.action === 'ban' ? 'fa-solid fa-lock' : 'fa-solid fa-unlock'"></i>
          </div>
          <h3>{{ banModal.action === 'ban' ? $t('admin.user_management.ban_title') : $t('admin.user_management.unban_title') }}</h3>
          <p>{{ banModal.action === 'ban' ? $t('admin.user_management.ban_confirm', { name: banModal.username }) : $t('admin.user_management.unban_confirm', { name: banModal.username }) }}</p>
          
          <div v-if="banModal.action === 'ban'" class="reason-input-group">
            <label for="banReason">{{ $t('admin.user_management.ban_reason_required_label') }}</label>
            <textarea 
              id="banReason" 
              v-model="banModal.reason" 
              :placeholder="t('admin.user_management.ban_reason_placeholder')" 
              rows="3" 
              class="reason-textarea"
            ></textarea>
            <span v-if="banModal.showError" class="error-msg">{{ $t('admin.user_management.ban_reason_required') }}</span>
          </div>

          <div class="action-btns">
            <button class="btn-cancel" @click="banModal.show = false">{{ $t('admin.common.cancel') }}</button>
            <button class="btn-confirm" :class="banModal.action === 'ban' ? 'btn-danger' : 'btn-success'" @click="confirmBanAction">
              {{ $t('admin.common.confirm') }}
            </button>
          </div>
        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'
import { toast } from '@/composables/useToast'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'
import { formatLocaleDate, formatLocaleDateTime } from '@/i18n'

const authStore = useAuthStore()
const { t, locale } = useI18n()

const users = ref([])
const isLoading = ref(true)
const searchQuery = ref('')
const currentFilter = ref('ALL')

const filterTabs = [
  { label: t('admin.common.all'), value: 'ALL' },
  { label: t('admin.user_management.filter_admin'), value: 'ADMIN' },
  { label: t('admin.user_management.filter_premium'), value: 'PREMIUM' },
  { label: t('admin.user_management.filter_banned'), value: 'BANNED' }
]

const banModal = ref({ show: false, action: 'ban', accountID: null, username: '', userRef: null, reason: '', showError: false })
const detailModal = ref({ show: false, loading: false, user: null })

const getAvatarClass = (user) => {
  if (user.role === 'ADMIN') return 'ring-admin'
  if (user.isPremium === 1 || user.isPremium === true) return 'ring-premium'
  return 'ring-normal'
}

const getTextGradientClass = (user) => {
  if (user.role === 'ADMIN') return 'text-admin'
  return ''
}

const getCoverClass = (user) => {
  if (user.role === 'ADMIN') return 'cover-admin'
  if (user.isPremium === 1 || user.isPremium === true) return 'cover-premium'
  return 'cover-normal'
}

const premiumCount = computed(() => users.value.filter(u => u.isPremium === 1 || u.isPremium === true).length)
const bannedCount = computed(() => users.value.filter(u => u.isActive === 0 || u.isActive === false).length)

const filteredUsers = computed(() => {
  let result = users.value
  if (currentFilter.value === 'ADMIN') result = result.filter(u => u.role === 'ADMIN')
  if (currentFilter.value === 'PREMIUM') result = result.filter(u => u.isPremium === 1 || u.isPremium === true)
  if (currentFilter.value === 'BANNED') result = result.filter(u => u.isActive === 0 || u.isActive === false)

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
      isActive: u.isActive === 1 || u.isActive === true,
      isPremium: u.isPremium === 1 || u.isPremium === true
    }))
  } catch (err) {
    toast.error(t('admin.user_management.load_failed'))
  } finally {
    isLoading.value = false
  }
}

// ĐÃ XÓA HÀM updateUserRole() VÌ KHÔNG CHO PHÉP ĐỔI QUYỀN TRÊN GIAO DIỆN NỮA

const askBanAction = (user) => {
  if (user.role === 'ADMIN') {
    toast.error(t('admin.user_management.admin_action_forbidden'));
    return;
  }
  banModal.value = { show: true, action: user.isActive ? 'ban' : 'unban', accountID: user.accountID, username: user.username, userRef: user, reason: '', showError: false }
}

const confirmBanAction = async () => {
  const { action, accountID, userRef, reason } = banModal.value
  
  if (action === 'ban' && !reason.trim()) {
    banModal.value.showError = true;
    return;
  }

  try {
    const payload = {
      adminId: authStore.user?.accountID || authStore.user?.id || 0,
      adminName: authStore.user?.username || authStore.user?.fullName || 'Admin',
      reason: action === 'ban' ? reason.trim() : ''
    };

    if (action === 'ban') {
      await api.patch(`/admin/accounts/${accountID}/ban`, payload);
      userRef.isActive = false;
    } else {
      await api.patch(`/admin/accounts/${accountID}/unban`, payload);
      userRef.isActive = true;
    }
    
    banModal.value.show = false;
    toast.success(t('admin.user_management.status_updated'))
    fetchUsers() 
  } catch (err) {
    toast.error(t('admin.user_management.action_failed'))
  }
}

const deleteUser = async (user) => {
  if (user.role === 'ADMIN') {
    toast.error(t('admin.user_management.admin_delete_forbidden'));
    return;
  }
  if (!confirm(t('admin.user_management.delete_confirm', { name: user.username }))) return
  try {
    await api.delete(`/admin/accounts/hard/${user.accountID}`)
    users.value = users.value.filter(u => u.accountID !== user.accountID)
    toast.success(t('admin.user_management.account_deleted'))
  } catch (err) {
    toast.error(t('admin.user_management.account_delete_blocked'))
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
      isActive: u.isActive === 1 || u.isActive === true,
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
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.page-container { padding: 32px; font-family: 'Inter', sans-serif; background: #fafafa; min-height: 100vh; color: #1e293b; }

.page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 24px; flex-wrap: wrap; gap: 16px; }
.title { font-size: 1.5rem; font-weight: 700; color: #0f172a; margin: 0 0 6px; }
.subtitle { color: #64748b; margin: 0; font-size: 0.9rem; }
.btn-refresh { background: white; border: 1px solid #e2e8f0; padding: 8px 16px; border-radius: 8px; font-weight: 600; font-size: 0.85rem; color: #475569; cursor: pointer; transition: 0.2s; display: flex; align-items: center; gap: 8px; box-shadow: 0 1px 2px rgba(0,0,0,0.05); }
.btn-refresh:hover:not(:disabled) { background: #f8fafc; color: #0f172a; }

.stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 24px; }
.stat-card { background: white; padding: 20px; border-radius: 12px; display: flex; align-items: center; gap: 16px; border: 1px solid #e2e8f0; box-shadow: 0 2px 8px rgba(0,0,0,0.02); transition: 0.2s; }
.stat-icon-wrapper { width: 52px; height: 52px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.4rem; }
.bg-blue-light { background: #eff6ff; } .text-blue { color: #3b82f6; }
.bg-orange-light { background: #fffbeb; } .text-orange { color: #f59e0b; }
.bg-red-light { background: #fef2f2; } .text-red { color: #ef4444; }  
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 1.6rem; font-weight: 700; color: #0f172a; line-height: 1.2; }
.stat-label { font-size: 0.85rem; color: #64748b; font-weight: 500; }
.stat-danger { border: 1px solid #fecaca; background: #fffcfc; }
.stat-danger .stat-value { color: #dc2626; }

.filter-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 16px; }
.tabs { display: flex; flex-wrap: wrap; gap: 4px; background: #f1f5f9; padding: 4px; border-radius: 10px; border: 1px solid #e2e8f0; }
.filter-tab { background: transparent; border: none; padding: 8px 18px; border-radius: 8px; font-weight: 600; color: #475569; cursor: pointer; transition: 0.2s; font-size: 0.85rem; font-family: inherit; }
.filter-tab.active { background: white; color: #0f172a; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }

.search-box { position: relative; display: flex; align-items: center; width: 300px; max-width: 100%; flex: 1 1 300px; }
.search-icon { position: absolute; left: 14px; color: #94a3b8; font-size: 0.9rem; }
.search-input { width: 100%; padding: 10px 32px 10px 38px; border: 1px solid #cbd5e1; border-radius: 8px; outline: none; background: white; font-family: inherit; font-size: 0.9rem; }
.search-input:focus { border-color: #ea580c; box-shadow: 0 0 0 3px rgba(234, 88, 12, 0.1); }

.table-wrapper { background: white; border-radius: 12px; border: 1px solid #e2e8f0; box-shadow: 0 4px 12px rgba(0,0,0,0.03); overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; min-width: 900px; }
.data-table th { background: #f8fafc; padding: 14px 20px; text-align: left; font-size: 0.75rem; font-weight: 600; color: #64748b; text-transform: uppercase; border-bottom: 1px solid #e2e8f0; }
.data-table td { padding: 14px 20px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.table-row:hover { background: #f8fafc; }
.col-id { font-weight: 600; color: #94a3b8; font-size: 0.85rem; }

.user-cell { display: flex; align-items: center; gap: 12px; }

.avatar-ring {
  position: relative;
  display: inline-flex; 
  padding: 3px; 
  border-radius: 50%;
}
.avatar { width: 40px; height: 40px; border-radius: 50%; border: 2px solid white; object-fit: cover; }

.ring-admin { background: linear-gradient(135deg, #3b82f6, #8b5cf6); box-shadow: 0 2px 8px rgba(139, 92, 246, 0.3); }
.ring-premium { background: linear-gradient(135deg, #f59e0b, #ea580c); box-shadow: 0 2px 8px rgba(234, 88, 12, 0.3); }
.ring-normal { background: transparent; padding: 0; }
.ring-normal .avatar { border: 1px solid #e2e8f0; } 

.badge-corner { position: absolute; bottom: -2px; right: -2px; width: 16px; height: 16px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.55rem; color: white; border: 2px solid white; }
.corner-admin { background: #8b5cf6; }
.corner-premium { background: #ea580c; }

.u-info { display: flex; flex-direction: column; }
.name { font-weight: 600; font-size: 0.9rem; color: #0f172a; }
.sub-name { font-size: 0.8rem; color: #64748b; }
.text-admin { background: linear-gradient(135deg, #3b82f6, #8b5cf6); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }

/* CSS MỚI CHO BADGE ROLE (THAY VÌ DROPDOWN) */
.role-badge { display: inline-flex; align-items: center; gap: 6px; padding: 6px 12px; border-radius: 8px; font-size: 0.75rem; font-weight: 700; letter-spacing: 0.5px; }
.role-admin { background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; }
.role-user { background: #f8fafc; color: #64748b; border: 1px solid #e2e8f0; }

.status-pill { display: inline-flex; align-items: center; gap: 6px; padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 600; }
.status-dot { width: 6px; height: 6px; border-radius: 50%; }
.status-pill.active { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; } .status-pill.active .status-dot { background: #16a34a; }
.status-pill.banned { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; } .status-pill.banned .status-dot { background: #dc2626; }

.point-badge { color: #64748b; font-weight: 600; font-size: 0.85rem; }
.icon-star { color: #f59e0b; font-size: 0.8rem; }

.actions { display: flex; gap: 8px; justify-content: center; }
.btn-action { width: 34px; height: 34px; border-radius: 8px; border: 1px solid #e2e8f0; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 0.95rem; transition: 0.2s; background: white; color: #64748b; }
.btn-action:hover:not(.disabled) { transform: translateY(-2px); box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.view { color: #3b82f6; } .view:hover { background: #eff6ff; border-color: #bfdbfe; }
.ban { color: #ea580c; } .ban:hover:not(.disabled) { background: #fff7ed; border-color: #fed7aa; }
.unban { color: #16a34a; } .unban:hover:not(.disabled) { background: #f0fdf4; border-color: #bbf7d0; }
.delete { color: #ef4444; } .delete:hover:not(.disabled) { background: #fef2f2; border-color: #fecaca; }
.btn-action.disabled { opacity: 0.4; cursor: not-allowed; background: #f8fafc; }

.profile-card { background: white; width: 420px; max-width: 95vw; border-radius: 16px; overflow-x: hidden; overflow-y: auto; max-height: 90vh; position: relative; box-shadow: 0 25px 50px rgba(0,0,0,0.25); }
.detail-close { position: absolute; top: 12px; right: 12px; width: 30px; height: 30px; border-radius: 8px; background: rgba(0,0,0,0.4); color: white; border: none; cursor: pointer; z-index: 10; }
.profile-cover { height: 110px; position: relative; }
.cover-normal { background: #cbd5e1; }
.cover-premium { background: linear-gradient(135deg, #f59e0b, #ea580c); }
.cover-admin { background: linear-gradient(135deg, #3b82f6, #8b5cf6); }
.absolute-status { position: absolute; top: 12px; left: 12px; padding: 4px 12px; border-radius: 6px; font-size: 0.75rem; font-weight: 600; color: white; }
.bg-green { background: #16a34a; } .bg-red { background: #dc2626; }

.profile-body { padding: 0 28px 28px; text-align: center; margin-top: -45px; position: relative; z-index: 2; }
.profile-avatar-ring { display: inline-flex; padding: 4px; border-radius: 50%; background: white; margin-bottom: 8px; }
.profile-avatar { width: 80px; height: 80px; border-radius: 50%; border: 2px solid white; object-fit: cover; }

.profile-name { font-size: 1.3rem; font-weight: 700; color: #0f172a; margin: 0 0 4px; }
.profile-email { color: #64748b; font-size: 0.9rem; margin: 0 0 16px; }
.profile-tags { display: flex; justify-content: center; gap: 8px; margin-bottom: 24px; }
.tag { padding: 4px 12px; border-radius: 6px; font-size: 0.75rem; font-weight: 600; }
.tag-admin { background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; } 
.tag-user { background: #f1f5f9; color: #475569; border: 1px solid #e2e8f0; } 
.tag-vip { background: #fffbeb; color: #d97706; border: 1px solid #fde68a; }

.profile-stats-box { display: flex; justify-content: space-around; background: #f8fafc; padding: 14px; border-radius: 12px; margin-bottom: 20px; border: 1px solid #e2e8f0; }
.p-stat { display: flex; flex-direction: column; }
.p-stat strong { font-size: 1.15rem; color: #0f172a; font-weight: 700; }
.p-stat span { font-size: 0.75rem; color: #64748b; font-weight: 500; text-transform: uppercase; }
.stat-divider { width: 1px; background: #e2e8f0; }

.profile-details { text-align: left; margin-bottom: 20px; }
.p-detail-item { display: flex; gap: 12px; padding: 10px 0; border-bottom: 1px solid #f1f5f9; font-size: 0.9rem; align-items: center; }
.p-detail-item .lbl { color: #64748b; width: 140px; }
.p-detail-item .val { color: #0f172a; font-weight: 600; }
.p-detail-bio { margin-top: 12px; background: #f8fafc; padding: 12px; border-radius: 8px; font-size: 0.85rem; color: #475569; border: 1px dashed #cbd5e1; }
.p-detail-bio .lbl { display: block; color: #64748b; font-weight: 600; margin-bottom: 4px; font-size: 0.8rem; }

/* --- HIỂN THỊ THÔNG TIN BAN RÕ RÀNG HƠN --- */
.p-detail-ban-reason { 
  margin-top: 16px; 
  background: #fff5f5; 
  border-radius: 12px; 
  border: 1px solid #fecaca; 
  overflow: hidden;
}
.ban-header { 
  background: #fef2f2; 
  padding: 10px 14px; 
  color: #dc2626; 
  font-weight: 700; 
  font-size: 0.85rem; 
  display: flex; 
  align-items: center; 
  gap: 8px; 
  border-bottom: 1px solid #fee2e2;
}
.ban-info-grid { padding: 12px 14px; display: flex; flex-direction: column; gap: 12px; }
.ban-info-item { display: flex; flex-direction: column; gap: 4px; }
.info-lbl { font-size: 0.75rem; color: #991b1b; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
.info-val { font-size: 0.9rem; color: #7f1d1d; line-height: 1.4; }
.reason-text { font-style: italic; background: #fff; padding: 8px 10px; border-radius: 6px; border: 1px dashed #fca5a5; }
.time-text { font-weight: 600; color: #991b1b; font-size: 0.85rem; }

.btn-full-profile {
  display: flex; justify-content: center; align-items: center; gap: 8px;
  width: 100%; padding: 12px; margin-bottom: 12px;
  border-radius: 10px; background: #f8fafc; color: #3b82f6;
  font-weight: 600; text-decoration: none; border: 1px solid #e2e8f0; transition: 0.2s;
}
.btn-full-profile:hover { background: #eff6ff; border-color: #bfdbfe; color: #2563eb; }

.btn-profile-ban { width: 100%; padding: 12px; border-radius: 10px; border: 1px solid transparent; font-weight: 600; font-size: 0.95rem; cursor: pointer; display: flex; justify-content: center; align-items: center; gap: 8px; }
.btn-profile-ban.warn { background: #fff7ed; color: #ea580c; border-color: #ffedd5; } .btn-profile-ban.warn:hover { background: #ea580c; color: white; }
.btn-profile-ban.ok { background: #f0fdf4; color: #16a34a; border-color: #dcfce7; } .btn-profile-ban.ok:hover { background: #16a34a; color: white; }
.admin-protect-msg { color: #64748b; font-size: 0.85rem; padding: 12px; background: #f1f5f9; border-radius: 8px; display: flex; align-items: center; justify-content: center; gap: 8px; }

.action-modal { background: white; padding: 28px; border-radius: 16px; width: 380px; max-width: 95vw; text-align: center; box-shadow: 0 25px 50px rgba(0,0,0,0.15); }
.action-icon { width: 56px; height: 56px; border-radius: 50%; margin: 0 auto 16px; display: flex; align-items: center; justify-content: center; font-size: 1.4rem; }
.bg-red-light { background: #fef2f2; } .text-red { color: #dc2626; }
.bg-green-light { background: #f0fdf4; } .text-green { color: #16a34a; }
.action-modal h3 { font-size: 1.2rem; color: #0f172a; margin: 0 0 8px; font-weight: 700; }
.action-modal p { color: #64748b; font-size: 0.95rem; margin: 0 0 24px; }
.action-btns { display: flex; gap: 12px; }
.btn-cancel, .btn-confirm { flex: 1; padding: 10px; border-radius: 8px; font-weight: 600; cursor: pointer; border: none; font-size: 0.95rem; }
.btn-cancel { background: #f1f5f9; color: #475569; }
.btn-danger { background: #ef4444; color: white; }
.btn-success { background: #10b981; color: white; }

.spinner-modern { width: 28px; height: 28px; border: 3px solid #e2e8f0; border-top-color: #3b82f6; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto; }
@keyframes spin { to { transform: rotate(360deg); } }
.modal-overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.6); display: flex; align-items: center; justify-content: center; z-index: 9999; backdrop-filter: blur(2px); }
.animate-enter { animation: fadeUp 0.4s ease-out; }
@keyframes fadeUp { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }

.reason-input-group {
  text-align: left;
  margin-bottom: 20px;
}
.reason-input-group label {
  display: block;
  font-size: 0.85rem;
  font-weight: 600;
  color: #475569;
  margin-bottom: 6px;
}
.reason-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  outline: none;
  font-family: inherit;
  font-size: 0.9rem;
  resize: none;
  background: #f8fafc;
  transition: 0.2s;
}
.reason-textarea:focus {
  border-color: #ef4444;
  background: white;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}
.error-msg {
  display: block;
  color: #dc2626;
  font-size: 0.8rem;
  font-weight: 500;
  margin-top: 4px;
}

@media (max-width: 768px) {
  .page-container { padding: 16px; }
  .profile-stats-box { flex-wrap: wrap; gap: 12px; justify-content: space-between; }
  .p-stat { flex: 1 1 40%; align-items: center; }
  .stat-divider { display: none; }
  .p-detail-item { flex-direction: column; align-items: flex-start; gap: 4px; }
}
</style>