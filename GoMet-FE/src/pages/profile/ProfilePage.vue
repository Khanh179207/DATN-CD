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
                <img :src="user.avatar" class="avatar-img" :alt="$t('common.profile_avatar')">
                <div class="verify-badge">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                </div>
              </div>
              
              <h1 class="user-name">{{ user.name }}</h1>
              <p class="user-handle">@{{ user.handle }}</p>
              
              <div class="bio-box">
                <p v-if="user.bio">{{ user.bio }}</p>
                <p v-else class="bio-placeholder">{{ isOwnProfile ? $t('profile.no_bio_self') : $t('profile.no_bio_other') }}</p>
              </div>

              <div class="action-stack">
                <button v-if="isOwnProfile" class="btn-primary" @click="openEditModal">{{ $t('profile.edit') }}</button>
                <button v-else class="btn-follow" :class="{ following: isFollowing }" @click="toggleFollow" :disabled="followLoading">
                  <span v-if="followLoading">...</span>
                  <span v-else-if="isFollowing">{{ $t('profile.following_cta') }}</span>
                  <span v-else>{{ $t('profile.follow_cta') }}</span>
                </button>
                <button class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"/><polyline points="16 6 12 2 8 6"/><line x1="12" y1="2" x2="12" y2="15"/></svg>
                </button>
              </div>

              <div class="social-links">
                <a href="#" class="s-link">{{ $t('profile.social_website') }}</a>
                <a href="#" class="s-link">{{ $t('profile.social_instagram') }}</a>
                <a href="#" class="s-link">{{ $t('profile.social_facebook') }}</a>
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
            <RecipeCard 
              v-if="!postsLoading"
              v-for="post in filteredPosts" 
              :key="post.id" 
              :post="post" 
              class="feed-item"
            />
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
                <div class="stat-cell">
                  <span class="val">{{ user.followers }}</span>
                  <span class="lbl">{{ $t('profile.followers') }}</span>
                </div>
                <div class="stat-cell">
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

    <teleport to="body">
      <transition name="modal-fade">
        <div v-if="showEditModal" class="edit-modal-overlay" @click.self="showEditModal = false">
          <div class="edit-modal-card">
            <div class="edit-modal-header">
              <h2>{{ $t('profile.edit_title') }}</h2>
              <button class="btn-close" @click="showEditModal = false">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
              </button>
            </div>

            <div class="edit-modal-body">
              <div class="edit-avatar-section">
                <div class="edit-avatar-wrap">
                  <img :src="editForm.avatarPreview || user.avatar" class="edit-avatar-img" :alt="$t('common.profile_avatar')">
                  <label class="edit-avatar-overlay" :title="$t('profile.edit_avatar')">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
                    <input type="file" accept="image/*" class="file-hidden" @change="onAvatarChange">
                  </label>
                </div>
                <p class="avatar-hint">{{ $t('profile.edit_avatar_hint') }}</p>
              </div>

              <div class="edit-field">
                <label>{{ $t('profile.username') }}</label>
                <input v-model="editForm.username" type="text" :placeholder="$t('profile.username_placeholder')" maxlength="50">
              </div>

              <div class="edit-field">
                <label>{{ $t('profile.bio') }}</label>
                <textarea v-model="editForm.bio" :placeholder="$t('profile.bio_placeholder')" rows="4" maxlength="300"></textarea>
                <span class="char-count">{{ editForm.bio.length }} / 300</span>
              </div>
            </div>

            <div class="edit-modal-footer">
              <button class="btn-cancel" @click="showEditModal = false">{{ $t('common.cancel') }}</button>
              <button class="btn-save" :disabled="editSaving" @click="saveProfile">
                <span v-if="editSaving" class="spinner-sm"></span>
                <span>{{ editSaving ? $t('profile.saving') : $t('profile.save_changes') }}</span>
              </button>
            </div>
          </div>
        </div>
      </transition>
    </teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import RecipeCard from '@/components/common/RecipeCard.vue'
import { getUserProfile, updateUserProfile } from '@/services/userService'
import { getPostsByUser, normalizePost } from '@/services/postService'
import { useAuthStore } from '@/stores/auth'
import { getUserAchievements } from '@/services/achievementService'
import { checkFollow, follow, unfollow } from '@/services/socialService'
import { uploadMedia } from '@/services/uploadService'
import { toast } from '@/composables/useToast'
import { useI18n } from 'vue-i18n'

const route = useRoute()
const authStore = useAuthStore()
const { t } = useI18n()

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

// Derived: unique categories from loaded posts
const postCategories = computed(() => {
  const cats = new Set(allPosts.value.map(p => p.category).filter(Boolean))
  return ['All', ...Array.from(cats)]
})

// Filtered posts based on active category tab
const filteredPosts = computed(() => {
  if (activeCategory.value === 'All') return allPosts.value
  return allPosts.value.filter(p => p.category === activeCategory.value)
})

// Edit modal state
const showEditModal = ref(false)
const editSaving = ref(false)
const editForm = ref({ username: '', bio: '', avatarFile: null, avatarPreview: '' })

const isOwnProfile = computed(() => {
  const myId = authStore.user?.accountID || authStore.user?.id
  const paramId = route.params.id
  return !paramId || String(paramId) === String(myId)
})

async function loadProfile() {
  // Use route param id if present, otherwise use logged-in user
  const targetId = route.params.id || authStore.user?.accountID
  if (!targetId) return
  try {
    const [profile, userPosts, userAch] = await Promise.all([
      getUserProfile(targetId),
      getPostsByUser(targetId),
      getUserAchievements(targetId)
    ])
    user.value = {
      name:       profile.username || t('compare.default_author'),
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
        : `${rawLikes}`
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

async function toggleFollow() {
  const myId = authStore.user?.accountID || authStore.user?.id
  const targetId = route.params.id
  if (!myId || !targetId || followLoading.value) return
  followLoading.value = true
  try {
    if (isFollowing.value) {
      await unfollow(myId, targetId)
      isFollowing.value = false
      if (user.value.followers) {
        const n = parseInt(String(user.value.followers).replace('k', '')) || 0
        user.value.followers = String(Math.max(0, n - 1))
      }
    } else {
      await follow(myId, targetId)
      isFollowing.value = true
      const n = parseInt(String(user.value.followers).replace('k', '')) || 0
      user.value.followers = String(n + 1)
    }
  } catch (err) {
    toast.error(t('toast.error_generic'))
  } finally {
    followLoading.value = false
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
  
  // Lấy ID người dùng (Ưu tiên ID từ route, nếu không có lấy từ store)
  const targetId = route.params.id || authStore.user?.accountID || authStore.user?.id
  if (!targetId) return

  editSaving.value = true
  try {
    let finalAvatarUrl = user.value.avatar; // Giữ avatar cũ làm mặc định

    // 1. Nếu có file mới, đẩy lên thư mục 'avatars' của Cloudinary
    if (editForm.value.avatarFile) {
      try {
        // 🔥 QUAN TRỌNG: Thêm chữ 'avatars' vào tham số thứ 2
        finalAvatarUrl = await uploadMedia(editForm.value.avatarFile, 'avatars')
      } catch (uploadErr) { 
        toast.error(t('support.upload_failed'));
        editSaving.value = false;
        return; // Dừng luôn nếu upload ảnh tạch
      }
    }

    // 2. Gom dữ liệu thành Object JSON sạch sẽ
    const payload = { 
      username: editForm.value.username, 
      bio: editForm.value.bio,
      avatar: finalAvatarUrl // Gửi cái Link HTTPS xịn
    }
    
    // 3. Gửi lên Backend (Đảm bảo backend dùng @RequestBody để nhận)
    await updateUserProfile(targetId, payload)
    
    // 4. Refresh lại dữ liệu và giao diện
// (Đoạn code trong ProfilePage.vue)
    // 4. Refresh lại dữ liệu và giao diện
    await loadProfile()
    
    // 🔥 Cập nhật AuthStore & LocalStorage để Header ăn theo
    if (authStore.user) {
        authStore.user.avatar = finalAvatarUrl;
        authStore.user.name = editForm.value.username; // Hoặc username
        
        // DÒNG QUAN TRỌNG NHẤT: Lưu đè xuống LocalStorage để F5 không bị cũ
        localStorage.setItem('user', JSON.stringify(authStore.user));
    }

    showEditModal.value = false
    toast.success(t('profile.save_changes'))
  } catch (err) {
    toast.error(t('toast.error_generic'))
  } finally {
    editSaving.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped lang="scss" src="./ProfilePage.scss"></style>