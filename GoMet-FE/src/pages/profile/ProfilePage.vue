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
                  <svg v-if="user.isPremium === 1" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="crown-icon"><path d="M2 4l3 12h14l3-12-6 7-4-7-4 7-6-7z"></path><path d="M5 20h14"></path></svg>
                  <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                </div>
              </div>
              
              <div class="name-badge-row">
                <h1 class="user-name">{{ user.name }}</h1>
                <span v-if="user.isPremium === 1" class="luxury-badge-vip">{{ $t('profile.premium_badge') }}</span>
              </div>
              <p class="user-handle">@{{ user.handle }}</p>
              
              <div class="bio-box">
                <p v-if="user.bio">{{ user.bio }}</p>
                <p v-else class="bio-placeholder">{{ isOwnProfile ? $t('profile.no_bio_self') : $t('profile.no_bio_other') }}</p>
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
          </div>
        </aside>

      </div>
    </div>

    <ProfileEditModal 
      :show="showEditModal" :form="editForm" :user="user" :saving="editSaving"
      @close="showEditModal = false" @save="saveProfile" @avatar-change="onAvatarChange" @open-deactivate="showDeactivateModal = true"
    />

    <PostEditModal 
      :show="showPostEditModal" :form="postEditForm" :categories="categories" :saving="postSaving"
      @close="showPostEditModal = false" @save="savePostInfo" @media-change="onPostMediaChange"
    />

    <FollowListModal 
      :show="showFollowModal" :type="followModalType" :list="followList" :loading="followLoadingList"
      @close="showFollowModal = false" @go-to-profile="goToUserProfile"
    />

    <DeactivateAccountModal v-model="showDeactivateModal" />

  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import RecipeCard from '@/components/common/RecipeCard.vue'
import ProfileActions from '@/components/profile/ProfileActions.vue'
import DeactivateAccountModal from '@/components/modals/DeactivateAccountModal.vue'

// Import 3 Modal vừa tách (Sửa đường dẫn nếu sếp lưu ở thư mục khác)
import ProfileEditModal from '@/components/profile/ProfileEditModal.vue'
import PostEditModal from '@/components/profile/PostEditModal.vue'
import FollowListModal from '@/components/profile/FollowListModal.vue'

import { getUserProfile, updateUserProfile } from '@/services/userService'
import { getPostsByUser, normalizePost, togglePostActive, updatePost } from '@/services/postService'
import { useAuthStore } from '@/stores/auth'
import { checkFollow } from '@/services/socialService'
import { uploadMedia } from '@/services/uploadService'
import { toast } from '@/composables/useToast'
import api from '@/services/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

// State
const user = ref({ name: '', handle: '', avatar: '', bio: '', postsCount: 0, followers: '0', following: 0, point: 0, totalLikes: 0, isPremium: 0 })
const allPosts = ref([]); const postsLoading = ref(true); const activeCategory = ref('All'); const isFollowing = ref(false);
const showPostEditModal = ref(false); const postSaving = ref(false); const postEditForm = ref({ id: null, title: '', description: '', ingredients: '', categoryID: null, level: 1, cookingTime: 30, mediaFile: null, mediaPreview: '' })
const categories = ref([])
const showFollowModal = ref(false); const followModalType = ref('followers'); const followList = ref([]); const followLoadingList = ref(false)
const showDeactivateModal = ref(false)
const showEditModal = ref(false); const editSaving = ref(false); const editForm = ref({ username: '', bio: '', avatarFile: null, avatarPreview: '' })

// Computed
const isOwnProfile = computed(() => !route.params.id || String(route.params.id) === String(authStore.user?.accountID || authStore.user?.id))
const postCategories = computed(() => ['All', ...Array.from(new Set(allPosts.value.map(p => p.category).filter(Boolean)))])
const filteredPosts = computed(() => {
  let list = allPosts.value
  if (!isOwnProfile.value) list = list.filter(p => p.isActive === 1 && p.isApproved === 1)
  if (activeCategory.value === 'All') return list
  return list.filter(p => p.category === activeCategory.value)
})

// Methods
async function loadProfile(forcedId = null) {
  postsLoading.value = true
  const targetId = forcedId || route.params.id || authStore.user?.accountID
  if (!targetId) return
  try {
    const [profile, userPosts] = await Promise.all([getUserProfile(targetId), getPostsByUser(targetId)])
    user.value = {
      name: profile.username || t('profile.chef_fallback'), handle: (profile.username || 'chef').toLowerCase().replace(/\s+/g, '_'),
      avatar: profile.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(profile.username||'G')}&background=EA580C&color=fff`,
      bio: profile.bio || '', postsCount: profile.postCount || 0,
      followers: profile.followerCount > 999 ? `${(profile.followerCount/1000).toFixed(1)}k` : `${profile.followerCount || 0}`,
      following: profile.followingCount || 0, point: profile.point || 0, isPremium: profile.isPremium || 0
    }
    allPosts.value = userPosts.map(normalizePost)
    postsLoading.value = false
    const rawLikes = allPosts.value.reduce((s, p) => s + (p.likes || 0), 0)
    user.value.totalLikes = rawLikes > 999999 ? `${(rawLikes / 1000000).toFixed(1)}M` : rawLikes > 999 ? `${(rawLikes / 1000).toFixed(1)}k` : `${rawLikes}`
        
    try { const catRes = await api.get('/api/categories'); categories.value = catRes.data } catch {}
    
    if (!isOwnProfile.value && authStore.user) {
      try { const status = await checkFollow(authStore.user?.accountID, targetId); isFollowing.value = status === true || status?.following === true } catch { isFollowing.value = false }
    }
  } catch (err) {
    if (err.response?.status === 404) { toast.error(t('profile.account_missing')); router.push('/home') }
    postsLoading.value = false
  }
}

watch(() => route.params.id, (newID) => { showFollowModal.value = false; loadProfile(newID) }, { immediate: false });
watch(() => authStore.user, (newVal) => { if (newVal && !route.params.id) loadProfile() }, { immediate: true })

async function openFollowList(type) {
  const targetId = route.params.id || authStore.user?.accountID; if (!targetId) return
  followModalType.value = type; showFollowModal.value = true; followLoadingList.value = true; followList.value = []
  try {
    const endpoint = type === 'followers' ? '/api/follows/followers-list' : '/api/follows/following-list'
    const paramKey = type === 'followers' ? 'followeeID' : 'followerID'
    const res = await api.get(endpoint, { params: { [paramKey]: targetId } }); followList.value = res.data
  } catch (err) { toast.error(t('profile.follow_list_failed')) } finally { followLoadingList.value = false }
}

function goToUserProfile(id) { showFollowModal.value = false; router.push({ name: 'Profile', params: { id } }) }

async function togglePostVisibility(post) {
  try { const res = await togglePostActive(post.id); post.isActive = res.isActive; toast.success(post.isActive === 1 ? t('profile.post_visible') : t('profile.post_hidden')) } catch { toast.error(t('profile.generic_error')) }
}

function openPostEditModal(post) {
  postEditForm.value = { id: post.id, title: post.title, description: post.description, ingredients: post.ingredients || '', categoryID: post.categoryID || null, level: post.difficulty === 'Easy' ? 1 : (post.difficulty === 'Hard' ? 3 : 2), cookingTime: parseInt(post.time) || 30, mediaFile: null, mediaPreview: post.image }
  showPostEditModal.value = true
}

function onPostMediaChange(e) { const file = e.target.files[0]; if (!file) return; postEditForm.value.mediaFile = file; postEditForm.value.mediaPreview = URL.createObjectURL(file) }

async function savePostInfo() {
  if (postSaving.value) return; postSaving.value = true
  try {
    let finalMediaUrl = postEditForm.value.mediaPreview
    if (postEditForm.value.mediaFile) finalMediaUrl = await uploadMedia(postEditForm.value.mediaFile, 'posts')
    await updatePost(postEditForm.value.id, { ...postEditForm.value, media: finalMediaUrl, accountID: authStore.user?.accountID })
    toast.success(t('profile.post_updated')); showPostEditModal.value = false; loadProfile() 
  } catch { toast.error(t('profile.generic_error')) } finally { postSaving.value = false }
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
    showEditModal.value = false; toast.success(t('profile.profile_saved'))
  } catch { toast.error(t('profile.save_failed')) } finally { editSaving.value = false }
}

onMounted(loadProfile)
</script>

<style scoped lang="scss" src="./ProfilePage.scss"></style>