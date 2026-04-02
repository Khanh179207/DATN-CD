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
            <div class="id-card">
              <div class="avatar-box">
                <img :src="user.avatar" class="avatar-img" alt="Chef">
                <div class="verify-badge">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                </div>
              </div>
              
              <h1 class="user-name">{{ user.name }}</h1>
              <p class="user-handle">@{{ user.handle }}</p>
              
              <div class="bio-box">
                <p v-if="user.bio">{{ user.bio }}</p>
                <p v-else class="bio-placeholder">{{ isOwnProfile ? 'Add a bio to tell others about yourself...' : 'No bio yet.' }}</p>
              </div>

              <div class="action-stack">
                <template v-if="isOwnProfile">
                  <button class="btn-primary" @click="openEditModal">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                    {{ $t('profile.edit') }}
                  </button>
                </template>
                <template v-else>
                  <ProfileActions 
                    :targetUser="{ ...user, id: route.params.id }" 
                    v-model:isFollowing="isFollowing"
                  />
                </template>
              </div>

              <div class="social-links">
                <a href="#" class="s-link">Website</a>
                <a href="#" class="s-link">Instagram</a>
                <a href="#" class="s-link">Facebook</a>
              </div>
            </div>
          </div>
        </aside>

        <main class="col-center">
          
          <div class="content-header">
            <h2 class="section-title">{{ $t('profile.masterpieces') }} <span class="count">{{ filteredPosts.length }}</span></h2>
            <div class="filter-tabs">
              <span 
                v-for="cat in postCategories" 
                :key="cat"
                class="tab" 
                :class="{ active: activeCategory === cat }"
                @click="activeCategory = cat"
              >{{ cat === 'All' ? $t('common.category_all') : cat }}</span>
            </div>
          </div>

          <div class="recipe-feed">
            <div v-if="!postsLoading" v-for="post in filteredPosts" :key="post.id" class="recipe-item-wrapper">
              <RecipeCard 
                :post="post" 
                :isManagement="isOwnProfile"
                class="feed-item"
                @manage-edit="openPostEditModal"
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
          </div>

        </main>

        <aside class="col-right">
          <div class="sticky-wrapper">
            
            <div class="widget-box">
              <h3 class="w-title">{{ $t('profile.stats') }}</h3>
              <div class="stat-grid">
                <div class="stat-cell">
                  <span class="val">{{ user.postsCount }}</span>
                  <span class="lbl">{{ $t('profile.posts') }}</span>
                </div>
                <div class="stat-cell clickable" @click="openFollowList('followers')">
                  <span class="val">{{ user.followers }}</span>
                  <span class="lbl">{{ $t('profile.followers') }}</span>
                </div>
                <div class="stat-cell clickable" @click="openFollowList('following')">
                  <span class="val">{{ user.following }}</span>
                  <span class="lbl">{{ $t('profile.following') }}</span>
                </div>
                <div class="stat-cell">
                  <span class="val">{{ user.totalLikes }}</span>
                  <span class="lbl">{{ $t('profile.total_likes') }}</span>
                </div>
              </div>
            </div>

            <div class="widget-box">
              <h3 class="w-title">{{ $t('profile.achievements') }}</h3>
              <ul class="award-list">
                <li v-if="achievements.length === 0" class="award-empty">
                  <span class="icon">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                  </span>
                  <span>{{ $t('profile.no_achievements') }}</span>
                </li>
                <li v-for="ach in achievements" :key="ach.uaid">
                  <span class="icon">{{ ach.icon }}</span>
                  <div class="award-info">
                    <strong>{{ ach.achievementName }}</strong>
                    <span>{{ ach.description }}</span>
                  </div>
                </li>
              </ul>
            </div>

          </div>
        </aside>

      </div>
    </div>

    <!-- Modals (Edit Profile, Edit Post, Follow List) -->
      <!-- Edit Profile Modal -->
      <transition name="modal-fade">
        <div v-if="showEditModal" class="edit-modal-overlay" @click.self="showEditModal = false">
          <div class="edit-modal-card">
            <div class="edit-modal-header">
              <h2>Edit Profile</h2>
              <button class="btn-close" @click="showEditModal = false">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
              </button>
            </div>

            <div class="edit-modal-body">
              <div class="edit-avatar-section">
                <div class="edit-avatar-wrap">
                  <img :src="editForm.avatarPreview || user.avatar" class="edit-avatar-img" alt="Avatar">
                  <label class="edit-avatar-overlay" title="Change photo">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
                    <input type="file" accept="image/*" class="file-hidden" @change="onAvatarChange">
                  </label>
                </div>
                <p class="avatar-hint">Click to change photo</p>
              </div>

              <div class="edit-field">
                <label>Username</label>
                <input v-model="editForm.username" type="text" placeholder="Your display name" maxlength="50">
              </div>

              <div class="edit-field">
                <label>Bio</label>
                <textarea v-model="editForm.bio" placeholder="Tell others about yourself..." rows="4" maxlength="300"></textarea>
                <span class="char-count">{{ editForm.bio.length }} / 300</span>
              </div>
            </div>

            <div class="edit-modal-footer">
              <button class="btn-cancel" @click="showEditModal = false">Cancel</button>
              <button class="btn-save" :disabled="editSaving" @click="saveProfile">
                <span v-if="editSaving" class="spinner-sm"></span>
                <span>{{ editSaving ? 'Saving...' : 'Save Changes' }}</span>
              </button>
            </div>
          </div>
        </div>
      </transition>

      <!-- VIP Edit Post Modal -->
      <transition name="modal-fade">
        <div v-if="showPostEditModal" class="edit-modal-overlay" @click.self="showPostEditModal = false">
          <div class="edit-modal-card vip-post-modal">
            <div class="edit-modal-header">
              <div class="header-left">
                <h2>Chỉnh sửa tác phẩm</h2>
              </div>
              <button class="btn-close" @click="showPostEditModal = false">✕</button>
            </div>
            
            <div class="edit-modal-body vip-body">
              <div class="vip-col-left">
                <div class="post-preview-card">
                  <div class="preview-img-wrap">
                    <img :src="postEditForm.mediaPreview" class="preview-img" alt="Preview">
                    <div class="img-overlay">
                      <label class="btn-upload-vip">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
                        <span>Đổi ảnh bìa</span>
                        <input type="file" accept="image/*" class="file-hidden" @change="onPostMediaChange">
                      </label>
                    </div>
                  </div>
                  <div class="preview-info">
                    <span class="preview-label">Xem trước ảnh đại diện</span>
                    <p class="preview-hint">Ảnh đẹp giúp món ăn của bạn thu hút nhiều lượt xem hơn sếp nhé!</p>
                  </div>
                </div>
              </div>

              <div class="vip-col-right">
                <div class="edit-field mb-3">
                  <label>Tiêu đề món ăn</label>
                  <input v-model="postEditForm.title" type="text" placeholder="Tên món ăn của bạn" class="premium-input">
                </div>

                <div class="edit-field mb-3">
                  <label>Mô tả ngắn</label>
                  <textarea v-model="postEditForm.description" placeholder="Chia sẻ câu chuyện về món này..." rows="2" class="premium-input"></textarea>
                </div>
                
                <div class="row g-3">
                  <div class="col-6">
                    <div class="edit-field">
                      <label>Danh mục</label>
                      <select v-model="postEditForm.categoryID" class="premium-select">
                        <option v-for="cat in categories" :key="cat.categoryID" :value="cat.categoryID">{{ cat.name }}</option>
                      </select>
                    </div>
                  </div>
                  <div class="col-6">
                    <div class="edit-field">
                      <label>Độ khó</label>
                      <select v-model="postEditForm.level" class="premium-select">
                        <option :value="1">Dễ</option>
                        <option :value="2">Trung bình</option>
                        <option :value="3">Khó</option>
                      </select>
                    </div>
                  </div>
                </div>

                <div class="edit-field my-3">
                  <label>Thời gian nấu (phút)</label>
                  <div class="input-with-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="input-icon"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                    <input v-model.number="postEditForm.cookingTime" type="number" min="1" class="premium-input ps-5">
                  </div>
                </div>

                <div class="edit-field">
                  <label>Nguyên liệu</label>
                  <textarea v-model="postEditForm.ingredients" placeholder="VD: 500g Bột, 2 quả trứng..." rows="3" class="premium-input"></textarea>
                </div>
              </div>
            </div>

            <div class="edit-modal-footer vip-footer">
              <button class="btn-cancel-vip" @click="showPostEditModal = false">Đóng</button>
              <button class="btn-save-vip" :disabled="postSaving" @click="savePostInfo">
                <span v-if="postSaving" class="spinner-sm"></span>
                <span v-else>Cập nhật bài viết</span>
              </button>
            </div>
          </div>
        </div>
      </transition>

      <!-- Follow List Modal (Premium Design) -->
      <transition name="modal-fade">
        <div v-if="showFollowModal" class="vip-follow-overlay" @click.self="showFollowModal = false">
          <div class="vip-follow-container">
            <div class="edit-modal-header">
              <div class="header-left">
                <h2>{{ followModalType === 'followers' ? 'Người theo dõi' : 'Đang theo dõi' }}</h2>
              </div>
              <button class="btn-close" @click="showFollowModal = false">✕</button>
            </div>
            
            <div class="edit-modal-body vip-follow-body">
              <div v-if="followLoadingList" class="follow-loading">
                <span class="spinner-xl"></span>
                <p>Đang tìm kiếm...</p>
              </div>
              
              <div v-else-if="followList.length === 0" class="follow-empty">
                <div class="empty-icon">👥</div>
                <p>Danh sách này hiện đang trống</p>
              </div>
              
              <div v-else class="follow-list-grid">
                <div v-for="item in followList" :key="item.accountID || item.id" 
                     class="follow-user-row" 
                     @click="goToUserProfile(item.accountID || item.id)">
                  <div class="user-avatar-wrap">
                    <img :src="item.avatar || `https://ui-avatars.com/api/?name=${item.username}`" alt="Avatar">
                    <div class="online-indicator"></div>
                  </div>
                  
                  <div class="user-info">
                    <span class="user-name">{{ item.username }}</span>
                    <span class="user-handle">@{{ (item.username || '').toLowerCase().replace(/\s+/g,'_') }}</span>
                  </div>
                  
                  <div class="user-action">
                    <button class="btn-view-profile">
                      <span>Xem</span>
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="9 18 15 12 9 6"></polyline></svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import RecipeCard from '@/components/common/RecipeCard.vue'
import ProfileActions from '@/components/profile/ProfileActions.vue'
import { getUserProfile, updateUserProfile } from '@/services/userService'
import { getPostsByUser, normalizePost, togglePostActive, updatePost } from '@/services/postService'
import { useAuthStore } from '@/stores/auth'
import { getUserAchievements } from '@/services/achievementService'
import { checkFollow, follow, unfollow } from '@/services/socialService'
import { uploadMedia } from '@/services/uploadService'
import { toast } from '@/composables/useToast'
import api from '@/services/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const user = ref({
  name: '', handle: '', avatar: '', bio: '',
  postsCount: 0, followers: '0', following: 0, point: 0, totalLikes: 0
})
const allPosts = ref([])
const postsLoading = ref(true)
const activeCategory = ref('All')
const achievements = ref([])
const isFollowing = ref(false)
const followLoading = ref(false)

// Edit Post state
const showPostEditModal = ref(false)
const postSaving = ref(false)
const postEditForm = ref({ 
  id: null, title: '', description: '', ingredients: '',
  categoryID: null, level: 1, cookingTime: 30,
  mediaFile: null, mediaPreview: ''
})

const categories = ref([])

// Follow list state
const showFollowModal = ref(false)
const followModalType = ref('followers') // 'followers' | 'following'
const followList = ref([])
const followLoadingList = ref(false)

// Derived: unique categories from loaded posts
const postCategories = computed(() => {
  const cats = new Set(allPosts.value.map(p => p.category).filter(Boolean))
  return ['All', ...Array.from(cats)]
})

// Filtered posts based on active category tab
const filteredPosts = computed(() => {
  let list = allPosts.value
  
  // 🔥 CHỈNH SỬA TẦM NHÌN: Chỉ chủ sở hữu mới thấy bài ẩn hoặc chờ duyệt
  if (!isOwnProfile.value) {
    list = list.filter(p => p.isActive === 1 && p.isApproved === 1)
  }

  if (activeCategory.value === 'All') return list
  return list.filter(p => p.category === activeCategory.value)
})

// Edit profile modal state
const showEditModal = ref(false)
const editSaving = ref(false)
const editForm = ref({ username: '', bio: '', avatarFile: null, avatarPreview: '' })

const isOwnProfile = computed(() => {
  const myId = authStore.user?.accountID || authStore.user?.id
  const paramId = route.params.id
  return !paramId || String(paramId) === String(myId)
})

async function loadProfile(forcedId = null) {
  postsLoading.value = true
  // Use forcedId if provided, otherwise route param id, otherwise logged-in user
  const targetId = forcedId || route.params.id || authStore.user?.accountID
  if (!targetId) return
  try {
    const [profile, userPosts, userAch] = await Promise.all([
      getUserProfile(targetId),
      getPostsByUser(targetId),
      getUserAchievements(targetId)
    ])
    user.value = {
      name:       profile.username || 'Chef',
      handle:     (profile.username || 'chef').toLowerCase().replace(/\s+/g, '_'),
      avatar:     profile.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(profile.username||'G')}&background=EA580C&color=fff`,
      bio:        profile.bio || '',
      postsCount: profile.postCount || 0,
      followers:  profile.followerCount > 999
                    ? `${(profile.followerCount/1000).toFixed(1)}k`
                    : `${profile.followerCount || 0}`,
      following:  profile.followingCount || 0,
      point:      profile.point || 0
    }
    allPosts.value = userPosts.map(normalizePost)
    postsLoading.value = false
    const rawLikes = allPosts.value.reduce((s, p) => s + (p.likes || 0), 0)
    user.value.totalLikes = rawLikes > 999999
      ? `${(rawLikes / 1000000).toFixed(1)}M`
      : rawLikes > 999
        ? `${(rawLikes / 1000).toFixed(1)}k`
        : rawLikes
    // Fetch categories for edit modal
    try {
      const catRes = await api.get('/api/categories')
      categories.value = catRes.data
    } catch { console.warn('Failed to load categories') }

    achievements.value = userAch || []
    
    // Check follow status if viewing someone else's profile
    const myId = authStore.user?.accountID || authStore.user?.id
    if (!isOwnProfile.value && myId) {
      try {
        const status = await checkFollow(myId, targetId)
        isFollowing.value = status === true || status?.following === true
      } catch { isFollowing.value = false }
    }
  } catch (err) {
    console.warn('ProfilePage: API error', err)
    postsLoading.value = false
  }
}

// 🔥 FIX: Watcher for route ID to handle internal navigation between different profiles
watch(() => route.params.id, (newID) => {
  if (showFollowModal.value) showFollowModal.value = false;
  loadProfile(newID); // Pass newID directly for robustness
}, { immediate: false });

// 🔥 FIX: Chờ AuthStore sẵn sàng mới nạp Profile (Sửa lỗi F5 bị ẩn bài)
watch(() => authStore.user, (newVal) => {
  if (newVal && !route.params.id) {
    loadProfile()
  }
}, { immediate: true })

async function toggleFollow() {
  const myId = authStore.user?.accountID || authStore.user?.id
  const targetId = route.params.id
  if (!myId || !targetId || followLoading.value) return
  followLoading.value = true
  try {
    if (isFollowing.value) {
      await unfollow(myId, targetId)
      isFollowing.value = false
    } else {
      await follow(myId, targetId)
      isFollowing.value = true
    }
    // Refresh counts
    const profile = await getUserProfile(targetId)
    user.value.followers = profile.followerCount
  } catch (err) {
    toast.error('Action failed, please try again.')
  } finally {
    followLoading.value = false
  }
}

async function goToMessage() {
  const myId = authStore.user?.accountID || authStore.user?.id
  const targetId = route.params.id
  if (!myId || !targetId) return
  try {
    const res = await api.post('/api/conversations/access', { user1Id: myId, user2Id: parseInt(targetId) })
    if (res.data?.conversationID) {
      // Dispatch event to open chatbox (assuming your ChatBox listen for this)
      window.dispatchEvent(new CustomEvent('ui:open-chatbox', { detail: { conversationID: res.data.conversationID } }))
    }
  } catch (err) {
    toast.error('Failed to open message.')
  }
}

async function openFollowList(type) {
  const targetId = route.params.id || authStore.user?.accountID
  if (!targetId) return

  followModalType.value = type
  showFollowModal.value = true
  followLoadingList.value = true
  followList.value = []

  try {
    const endpoint = type === 'followers' ? '/api/follows/followers-list' : '/api/follows/following-list'
    const paramKey = type === 'followers' ? 'followeeID' : 'followerID'
    const res = await api.get(endpoint, { params: { [paramKey]: targetId } })
    followList.value = res.data
  } catch (err) {
    toast.error('Could not load list.')
  } finally {
    followLoadingList.value = false
  }
}

function goToUserProfile(id) {
  showFollowModal.value = false
  // Correct route name is 'Profile' not 'ProfilePage'
  router.push({ name: 'Profile', params: { id } })
}

async function togglePostVisibility(post) {
  try {
    const res = await togglePostActive(post.id)
    post.isActive = res.isActive
    toast.success(post.isActive === 1 ? 'Bài viết đã hiển thị!' : 'Bài viết đã được ẩn.')
  } catch (err) {
    toast.error('Thao tác thất bại.')
  }
}

function openPostEditModal(post) {
  postEditForm.value = {
    id: post.id,
    title: post.title,
    description: post.description,
    ingredients: post.ingredients || '',
    categoryID: post.categoryID || null,
    level: post.difficulty === 'Easy' ? 1 : (post.difficulty === 'Hard' ? 3 : 2),
    cookingTime: parseInt(post.time) || 30,
    mediaFile: null,
    mediaPreview: post.image
  }
  showPostEditModal.value = true
}

function onPostMediaChange(e) {
  const file = e.target.files[0]
  if (!file) return
  postEditForm.value.mediaFile = file
  postEditForm.value.mediaPreview = URL.createObjectURL(file)
}

async function savePostInfo() {
  if (postSaving.value) return
  postSaving.value = true
  try {
    let finalMediaUrl = postEditForm.value.mediaPreview
    if (postEditForm.value.mediaFile) {
        finalMediaUrl = await uploadMedia(postEditForm.value.mediaFile, 'posts')
    }

    const payload = {
      title: postEditForm.value.title,
      description: postEditForm.value.description,
      ingredients: postEditForm.value.ingredients,
      categoryID: postEditForm.value.categoryID,
      level: postEditForm.value.level,
      cookingTime: postEditForm.value.cookingTime,
      media: finalMediaUrl,
      accountID: authStore.user?.accountID
    }
    await updatePost(postEditForm.value.id, payload)
    toast.success('Đã cập nhật bài viết thành công!')
    showPostEditModal.value = false
    loadProfile() // Refresh list
  } catch (err) {
    toast.error('Lỗi khi lưu bài viết.')
  } finally {
    postSaving.value = false
  }
}

function openEditModal() {
  editForm.value = {
    username: user.value.name,
    bio: user.value.bio || '',
    avatarFile: null,
    avatarPreview: ''
  }
  showEditModal.value = true
}

function onAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  editForm.value.avatarFile = file
  editForm.value.avatarPreview = URL.createObjectURL(file)
}

async function saveProfile() {
  if (editSaving.value) return
  
  const targetId = route.params.id || authStore.user?.accountID || authStore.user?.id
  if (!targetId) return

  editSaving.value = true
  try {
    let finalAvatarUrl = user.value.avatar
    if (editForm.value.avatarFile) {
        finalAvatarUrl = await uploadMedia(editForm.value.avatarFile, 'avatars')
    }

    const payload = { 
      username: editForm.value.username, 
      bio: editForm.value.bio,
      avatar: finalAvatarUrl
    }
    
    await updateUserProfile(targetId, payload)
    await loadProfile()
    
    if (authStore.user) {
        authStore.user.avatar = finalAvatarUrl;
        authStore.user.name = editForm.value.username;
        localStorage.setItem('user', JSON.stringify(authStore.user));
    }

    showEditModal.value = false
    toast.success('Hồ sơ đã được lưu! ☁️')
  } catch (err) {
    toast.error('Có lỗi xảy ra khi lưu trữ.')
  } finally {
    editSaving.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped lang="scss" src="./ProfilePage.scss"></style>