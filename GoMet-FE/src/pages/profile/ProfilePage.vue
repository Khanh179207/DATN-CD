<template>
  <div>
    <div class="gomet-profile-full">
      <div class="bg-layer">
        <div class="grid-mesh"></div>
        <div class="noise-texture"></div>
      </div>

      <div class="profile-layout-wide">
        
        <aside class="col-left">
          <div class="sticky-wrapper">
            
            <div class="id-card" :class="{ 'is-premium-profile': user.isPremium === 1 }">
              <div class="avatar-box">
                <img :src="user.avatar" class="avatar-img" alt="Chef">
                <div class="verify-badge" :class="{ 'is-premium': user.isPremium === 1 }">
                  <i class="fas fa-crown crown-icon" v-if="user.isPremium === 1"></i>
                  <i class="fas fa-check" v-else></i>
                </div>
              </div>
              
              <div class="name-badge-row">
                <h1 class="user-name">{{ user.name }}</h1>
                <span v-if="user.isPremium === 1" class="luxury-badge-vip">PREMIUM</span>
              </div>
              <p class="user-handle">{{ user.email || '@' + user.handle }}</p>
              
              <div class="bio-box">
                <p v-if="user.bio">{{ user.bio }}</p>
                <p v-else class="bio-placeholder">
                  {{ isOwnProfile ? 'Thêm một đoạn mô tả ngắn để mọi người hiểu thêm về bạn nhé...' : 'Đầu bếp này khá kín tiếng.' }}
                </p>
              </div>

              <div class="stat-bento-grid">
                <div class="stat-row">
                  <div class="stat-cell">
                    <i class="fas fa-utensils stat-icon text-orange"></i>
                    <div class="stat-info">
                      <span class="val">{{ user.postsCount }}</span>
                      <span class="lbl">Công Thức</span>
                    </div>
                  </div>
                  
                  <div class="stat-cell">
                    <i class="fas fa-heart stat-icon text-red"></i>
                    <div class="stat-info">
                      <span class="val">{{ user.totalLikes }}</span>
                      <span class="lbl">Lượt Thích</span>
                    </div>
                  </div>
                </div>

                <div class="stat-row follow-row">
                  <div class="stat-cell clickable flex-1" @click="openFollowList('followers')">
                    <div class="stat-info text-center">
                      <span class="val">{{ user.followers }}</span>
                      <span class="lbl">Người Theo Dõi</span>
                    </div>
                  </div>
                  <div class="stat-divider"></div>
                  <div class="stat-cell clickable flex-1" @click="openFollowList('following')">
                    <div class="stat-info text-center">
                      <span class="val">{{ user.following }}</span>
                      <span class="lbl">Đang Theo Dõi</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="action-stack">
                <template v-if="isOwnProfile">
                  <button class="btn-primary-glow" @click="openEditModal">
                    <i class="fas fa-user-edit"></i> Chỉnh Sửa Hồ Sơ
                  </button>
                </template>
                <template v-else>
                  <div class="profile-actions-premium">
                    <button 
                      class="btn-follow-premium" 
                      :class="{ 'is-following': isFollowing, 'is-loading': profileActionsStore?.followLoading }" 
                      @click="handleFollow"
                      :disabled="profileActionsStore?.followLoading"
                    >
                      <div class="inner-wrap">
                        <span class="icon" v-if="!profileActionsStore?.followLoading">
                          <svg v-if="isFollowing" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
                          <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                        </span>
                        <span v-else class="spinner-sm"></span>
                        <span class="text">{{ isFollowing ? 'Đang theo dõi' : 'Theo dõi' }}</span>
                      </div>
                      <div class="glow-effect"></div>
                    </button>

                    <button 
                      class="btn-message-premium" 
                      @click="handleMessage"
                      :disabled="profileActionsStore?.loading"
                    >
                      <div class="inner-wrap">
                        <span class="icon" v-if="!profileActionsStore?.loading">
                          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
                        </span>
                        <span v-else class="spinner-sm"></span>
                        <span class="text">Nhắn tin</span>
                      </div>
                    </button>
                  </div>
                </template>
              </div>
            </div>
            
          </div>
        </aside>

        <main class="col-center">
          
          <div class="profile-nav-tabs">
            <button class="nav-tab" :class="{ 'active': activeMainTab === 'posts' }" @click="activeMainTab = 'posts'">
              <i class="fas fa-border-all"></i> Tuyệt Tác
            </button>
            
            <button class="nav-tab" v-if="isOwnProfile" :class="{ 'active': activeMainTab === 'comments' }" @click="activeMainTab = 'comments'">
              <i class="fas fa-comments"></i> Bình Luận
            </button>
            
            <button class="nav-tab" v-if="isOwnProfile" :class="{ 'active': activeMainTab === 'tickets' }" @click="activeMainTab = 'tickets'">
              <i class="fas fa-ticket-alt"></i> Hỗ Trợ / Khiếu Nại
            </button>
          </div>

          <div v-if="activeMainTab === 'posts'" class="tab-content anim-fade-in">
            
            <div class="content-header" v-if="filteredPosts.length > 0 || postsLoading">
              <h2 class="section-title">Danh sách <span class="count">{{ filteredPosts.length }}</span></h2>
              <div class="filter-tabs">
                <span 
                  v-for="cat in postCategories" 
                  :key="cat"
                  class="tab" 
                  :class="{ active: activeCategory === cat }"
                  @click="activeCategory = cat"
                >{{ cat === 'All' ? 'Tất Cả' : cat }}</span>
              </div>
            </div>

            <div class="recipe-feed">
              <div v-if="!postsLoading && filteredPosts.length > 0" v-for="post in filteredPosts" :key="post.id" class="recipe-item-wrapper">
                <RecipeCard 
                  :post="post" 
                  :isManagement="isOwnProfile"
                  class="feed-item"
                  @manage-edit="goToEditPost"
                  @manage-toggle-visibility="togglePostVisibility"
                />
              </div>

              <RecipeCard 
                v-if="postsLoading"
                v-for="n in 6" 
                :key="'sk-' + n" 
                :post="{}" 
                :loading="true"
                class="feed-item"
              />

              <div class="empty-feed" v-if="!postsLoading && filteredPosts.length === 0">
                <i class="fas fa-utensils empty-icon"></i>
                <p>Chưa có tuyệt tác nào được chia sẻ ở đây.</p>
              </div>
            </div>
          </div>

          <div v-else-if="activeMainTab === 'comments' && isOwnProfile" class="tab-content anim-fade-in">
            <div v-if="commentsLoading" class="feed-loading">
              <div class="spinner-lg"></div>
            </div>
            <div v-else-if="userComments.length > 0" class="activity-list">
              <div v-for="comment in userComments" :key="comment.commentID" class="comment-card clickable" @click="goToComment(comment)">
                <div class="comment-card-header">
                  <span class="post-link">Tại bài viết: <strong>{{ comment.postTitle || 'Bài viết đã bị ẩn' }}</strong></span>
                  <span class="date">{{ formatDate(comment.createdAt) }}</span>
                </div>
                
                <p class="comment-content">{{ comment.content }}</p>

                <div v-if="comment.attachments && comment.attachments.length > 0" class="comment-attachments">
                  <div v-for="(img, idx) in comment.attachments" :key="idx" class="attachment-thumb">
                    <img :src="img" alt="Ảnh đính kèm">
                  </div>
                </div>
                
              </div>
            </div>
            <div v-else class="empty-feed">
              <i class="fas fa-comment-slash empty-icon"></i>
              <p>Bạn chưa có bình luận nào.</p>
            </div>
          </div>

          <div v-else-if="activeMainTab === 'tickets' && isOwnProfile" class="tab-content anim-fade-in">
            <div v-if="ticketsLoading" class="feed-loading">
              <div class="spinner-lg"></div>
            </div>
            <div v-else-if="userTickets.length > 0" class="activity-list">
              <div v-for="ticket in userTickets" :key="ticket.ticketID" class="ticket-card">
                <div class="ticket-header">
                  <span class="ticket-title">{{ ticket.title }}</span>
                  <span class="status-badge" :class="(ticket.status || '').toLowerCase()">{{ getTicketStatusText(ticket.status) }}</span>
                </div>
                <p class="ticket-content">{{ ticket.content }}</p>
                
                <div v-if="ticket.adminNote" class="admin-reply">
                  <strong><i class="fas fa-headset"></i> Phản hồi từ Admin:</strong>
                  <p>{{ ticket.adminNote }}</p>
                </div>
                
                <div class="ticket-footer">
                  <span>Ngày tạo: {{ formatDate(ticket.createdAt) }}</span>
                </div>
              </div>
            </div>
            <div v-else class="empty-feed">
              <i class="fas fa-clipboard-list empty-icon"></i>
              <p>Bạn chưa có yêu cầu hỗ trợ nào.</p>
              <span class="empty-hint">Để gửi yêu cầu, vui lòng truy cập mục Liên hệ.</span>
            </div>
          </div>

        </main>

      </div>
    </div>

    <ProfileEditModal 
      :show="showEditModal" 
      :form="editForm" 
      :user="user" 
      :saving="editSaving"
      :sendingOtp="sendingOtp"
      :changingPwd="changingPwd"
      @close="showEditModal = false" 
      @save="saveProfile" 
      @avatar-change="onAvatarChange" 
      @open-deactivate="showDeactivateModal = true"
      @send-pwd-otp="handleSendPwdOtp"
      @change-password="handleChangePassword"
    />

    <FollowListModal 
      :show="showFollowModal" :type="followModalType" :list="followList" :loading="followLoadingList"
      @close="showFollowModal = false" @go-to-profile="goToUserProfile"
    />

    <DeactivateAccountModal v-model="showDeactivateModal" :user="user" />

  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router'
import RecipeCard from '@/components/common/RecipeCard.vue'
import DeactivateAccountModal from '@/components/modals/DeactivateAccountModal.vue'

import ProfileEditModal from '@/components/profile/ProfileEditModal.vue'
import FollowListModal from '@/components/profile/FollowListModal.vue'

import { getUserProfile, updateUserProfile } from '@/services/userService'
import { getPostsByUser, normalizePost, togglePostActive } from '@/services/postService'
import { useAuthStore } from '@/stores/auth'
import { useProfileActionsStore } from '@/stores/profileActions'
import { checkFollow } from '@/services/socialService'
import { uploadMedia } from '@/services/uploadService'
import { toast } from '@/composables/useToast'
import api from '@/services/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const profileActionsStore = useProfileActionsStore()
const { t } = useI18n()

// State
const user = ref({ name: '', handle: '', email: '', avatar: '', bio: '', postsCount: 0, followers: '0', following: 0, point: 0, totalLikes: 0, isPremium: 0, createdAt: '' })
const allPosts = ref([]); const postsLoading = ref(true); const activeCategory = ref('All'); const isFollowing = ref(false);
const activeMainTab = ref('posts')
const categories = ref([])
const showFollowModal = ref(false); const followModalType = ref('followers'); const followList = ref([]); const followLoadingList = ref(false)
const showDeactivateModal = ref(false)
const showEditModal = ref(false); const editSaving = ref(false); const editForm = ref({ username: '', bio: '', avatarFile: null, avatarPreview: '' })

// State for new tabs
const userComments = ref([]);
const commentsLoading = ref(false);
const userTickets = ref([]);
const ticketsLoading = ref(false);

// 🔥 NEW: State for Change Password
const sendingOtp = ref(false);
const changingPwd = ref(false);

// Computed
const isOwnProfile = computed(() => !route.params.id || String(route.params.id) === String(authStore.user?.accountID || authStore.user?.id))
const postCategories = computed(() => ['All', ...Array.from(new Set(allPosts.value.map(p => p.category).filter(Boolean)))])
const filteredPosts = computed(() => {
  let list = allPosts.value
  if (!isOwnProfile.value) list = list.filter(p => p.isActive === 1 && p.isApproved === 1)
  if (activeCategory.value === 'All') return list
  return list.filter(p => p.category === activeCategory.value)
})

// Helper Format Date
const formatDate = (dateStr) => {
  if (!dateStr) return 'Thành viên mới'
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return 'Thành viên mới'
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

// Helper Ticket Status
const getTicketStatusText = (status) => {
  if (!status) return 'Không rõ';
  const key = `admin.tickets.status_${status.toLowerCase()}`;
  const translated = t(key);
  return translated === key ? status.charAt(0).toUpperCase() + status.slice(1).toLowerCase() : translated;
}

// Methods
async function loadProfile(forcedId = null) {
  postsLoading.value = true
  const targetId = forcedId || route.params.id || authStore.user?.accountID
  if (!targetId) return
  
  try {
    const [profile, userPosts] = await Promise.all([
      getUserProfile(targetId), 
      getPostsByUser(targetId)
    ])

    // Cập nhật thông tin User từ API profile trả về
    user.value = {
      name: profile.username || 'Chef', 
      handle: (profile.username || 'chef').toLowerCase().replace(/\s+/g, '_'), 
      email: profile.email || '',
      avatar: profile.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(profile.username||'G')}&background=EA580C&color=fff`,
      bio: profile.bio || '', 
      postsCount: profile.postCount || 0,
      followers: profile.followerCount > 999 ? `${(profile.followerCount/1000).toFixed(1)}k` : `${profile.followerCount || 0}`,
      following: profile.followingCount || 0, 
      point: profile.point || 0, 
      isPremium: profile.isPremium || 0,
      createdAt: formatDate(profile.createdAt || profile.createdDate || profile.created_at),
      // 🔥 ĐÃ CẬP NHẬT: Lấy provider từ API để các Modal biết đây là acc Google hay Local
      provider: profile.provider 
    }

    allPosts.value = userPosts.map(normalizePost)
    postsLoading.value = false
    
    // Tính toán lượt like hiển thị
    const rawLikes = allPosts.value.reduce((s, p) => s + (p.likes || 0), 0)
    user.value.totalLikes = rawLikes > 999999 ? `${(rawLikes / 1000000).toFixed(1)}M` : rawLikes > 999 ? `${(rawLikes / 1000).toFixed(1)}k` : `${rawLikes}`
        
    try { 
      const catRes = await api.get('/api/categories'); 
      categories.value = catRes.data 
    } catch (e) {
      console.error("Lỗi load categories", e)
    }
    
    if (!isOwnProfile.value && authStore.user) {
      try { 
        const status = await checkFollow(authStore.user?.accountID, targetId); 
        isFollowing.value = status === true || status?.following === true 
      } catch { 
        isFollowing.value = false 
      }
    }
  } catch (err) {
    if (err.response?.status === 404) { 
      toast.error('Tài khoản không tồn tại.'); 
      router.push('/') 
    }
    postsLoading.value = false
  }
}
async function handleFollow() {
  const targetId = route.params.id;
  if (!targetId) return;
  const newStatus = await profileActionsStore.toggleFollow(targetId, isFollowing.value);
  isFollowing.value = newStatus;
  loadProfile(targetId);
}

function handleMessage() {
  const targetId = route.params.id;
  if (!targetId) return;
  profileActionsStore.startConversation({ ...user.value, id: targetId });
}

async function loadUserComments() {
  if (!isOwnProfile.value) return;
  commentsLoading.value = true;
  try {
    const targetId = route.params.id || authStore.user?.accountID;
    const res = await api.get(`/api/users/${targetId}/comments`);
    userComments.value = res.data || [];
  } catch (error) {
    toast.error('Không thể tải lịch sử bình luận.');
  } finally {
    commentsLoading.value = false;
  }
}

async function loadUserTickets() {
  if (!isOwnProfile.value) return;
  ticketsLoading.value = true;
  try {
    const targetId = route.params.id || authStore.user?.accountID;
    const res = await api.get(`/api/users/${targetId}/tickets`);
    userTickets.value = res.data || [];
  } catch (error) {
    toast.error('Không thể tải lịch sử hỗ trợ.');
  } finally {
    ticketsLoading.value = false;
  }
}

watch(() => route.params.id, (newID) => { showFollowModal.value = false; activeMainTab.value = 'posts'; loadProfile(newID) }, { immediate: false });
watch(() => authStore.user, (newVal) => { if (newVal && !route.params.id) loadProfile() }, { immediate: true })

async function openFollowList(type) {
  const targetId = route.params.id || authStore.user?.accountID; if (!targetId) return
  followModalType.value = type; showFollowModal.value = true; followLoadingList.value = true; followList.value = []
  try {
    const endpoint = type === 'followers' ? '/api/follows/followers-list' : '/api/follows/following-list'
    const paramKey = type === 'followers' ? 'followeeID' : 'followerID'
    const res = await api.get(endpoint, { params: { [paramKey]: targetId } }); followList.value = res.data
  } catch (err) { toast.error('Lỗi tải danh sách.') } finally { followLoadingList.value = false }
}

function goToUserProfile(id) { showFollowModal.value = false; router.push({ name: 'Profile', params: { id } }) }

function goToComment(comment) {
  if (comment.postID && comment.commentID) {
    router.push({
      name: 'PostDetail',
      params: { id: comment.postID },
      hash: `#comment-${comment.commentID}`
    });
  } else {
    toast.error('Không thể điều hướng đến bình luận này.');
  }
}

async function togglePostVisibility(post) {
  try { const res = await togglePostActive(post.id); post.isActive = res.isActive; toast.success(post.isActive === 1 ? 'Đã hiển thị!' : 'Đã ẩn.') } catch { toast.error('Lỗi.') }
}

function goToEditPost(post) {
  const postId = post.id || post.postId;
  if (postId) { router.push(`/edit-post/${postId}`); } else { toast.error('Không tìm thấy ID bài viết!'); }
}

function openEditModal() { editForm.value = { username: user.value.name, bio: user.value.bio || '', avatarFile: null, avatarPreview: '' }; showEditModal.value = true }

function onAvatarChange(e) { const file = e.target.files[0]; if (!file) return; editForm.value.avatarFile = file; editForm.value.avatarPreview = URL.createObjectURL(file) }

async function saveProfile() {
  if (editSaving.value) return; const targetId = route.params.id || authStore.user?.accountID; if (!targetId) return
  editSaving.value = true
  try {
    let finalAvatarUrl = user.value.avatar
    if (editForm.value.avatarFile) finalAvatarUrl = await uploadMedia(editForm.value.avatarFile, 'avatars')
    await updateUserProfile(targetId, { username: editForm.value.username, bio: editForm.value.bio, avatar: finalAvatarUrl })
    await loadProfile()
    if (authStore.user) { authStore.user.avatar = finalAvatarUrl; authStore.user.name = editForm.value.username; localStorage.setItem('user', JSON.stringify(authStore.user)) }
    showEditModal.value = false; toast.success('Đã lưu hồ sơ!')
  } catch { toast.error('Lỗi lưu trữ.') } finally { editSaving.value = false }
}

// 🔥 NEW: Logic gửi OTP đổi mật khẩu
async function handleSendPwdOtp(pwdForm) {
  const targetId = authStore.user?.accountID;
  if (!targetId) return;
  
  sendingOtp.value = true;
  try {
    await api.post(`/api/users/${targetId}/send-pwd-otp`);
    toast.success('Mã OTP đã được gửi đến email của bạn!');
  } catch (error) {
    toast.error(error.response?.data?.message || 'Lỗi khi gửi mã OTP.');
  } finally {
    sendingOtp.value = false;
  }
}

// 🔥 NEW: Logic Xác nhận đổi mật khẩu
async function handleChangePassword(pwdForm) {
  const targetId = authStore.user?.accountID;
  if (!targetId) return;
  
  changingPwd.value = true;
  try {
    await api.put(`/api/users/${targetId}/change-password`, {
      oldPassword: pwdForm.current || "",
      newPassword: pwdForm.new,
      otp: pwdForm.otp,
      isGoogleUser: pwdForm.isGoogleUser
    });
    
    toast.success('Đổi mật khẩu thành công! Vui lòng đăng nhập lại.');
    showEditModal.value = false;
    
    authStore.logout();
    router.push('/');
  } catch (error) {
    toast.error(error.response?.data?.message || 'Mật khẩu hiện tại hoặc mã OTP không chính xác.');
  } finally {
    changingPwd.value = false;
  }
}

// Lazy loading for tabs
watch(activeMainTab, (newTab) => {
  if (newTab === 'comments' && userComments.value.length === 0) {
    loadUserComments();
  } else if (newTab === 'tickets' && userTickets.value.length === 0) {
    loadUserTickets();
  }
});

onMounted(loadProfile)
</script>

<style scoped lang="scss" src="./ProfilePage.scss"></style>
<style scoped lang="scss">

</style>