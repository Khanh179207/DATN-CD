<template>
  <div class="gomet-profile-full">
    
    <div class="bg-layer">
      <div class="grid-mesh"></div>
      <div class="noise-texture"></div>
    </div>

    <div class="profile-layout-wide">
      
      <aside class="col-left">
        <div class="sticky-wrapper">
          <div class="id-card">
            <div class="avatar-box" :class="{ 'avatar-premium': user.isPremium }">
              <img :src="user.avatar" class="avatar-img" alt="Chef">
              <div v-if="user.isPremium" class="premium-ring-anim" aria-hidden="true"></div>
              <div v-if="user.isPremium" class="premium-crown-badge" title="Premium Member">👑</div>
              <div v-else class="verify-badge">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
              </div>
            </div>
            
            <h1 class="user-name">{{ user.name }}</h1>
            <div v-if="user.isPremium" class="premium-title-badge">✦ GoMet Premium</div>
            <p class="user-handle">@{{ user.handle }}</p>
            
            <div class="bio-box">
              <p v-if="user.bio">{{ user.bio }}</p>
              <p v-else class="bio-placeholder">{{ isOwnProfile ? $t('profile.bio_empty_own') : $t('profile.bio_empty_other') }}</p>
            </div>

            <div v-if="user.createdAt" class="joined-info">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>
              {{ $t('profile.joined') }} {{ user.createdAt }}
            </div>

            <div class="action-stack">
              <button v-if="isOwnProfile" class="btn-primary" @click="openEditModal">{{ $t('profile.edit') }}</button>
              <button v-else class="btn-follow" :class="{ following: isFollowing }" @click="toggleFollow" :disabled="followLoading">
                <span v-if="followLoading">...</span>
                <span v-else-if="isFollowing">✓ {{ $t('common.following') }}</span>
                <span v-else>+ {{ $t('common.follow') }}</span>
              </button>
              <button class="btn-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"/><polyline points="16 6 12 2 8 6"/><line x1="12" y1="2" x2="12" y2="15"/></svg>
              </button>
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
              <div class="stat-cell">
                <span class="val">{{ user.totalViews }}</span>
                <span class="lbl">{{ $t('profile.total_views') }}</span>
              </div>
              <div class="stat-cell">
                <span class="val">{{ user.point }}</span>
                <span class="lbl">{{ $t('profile.points') }}</span>
              </div>
            </div>
          </div>

          <!-- Account Info widget -->
          <div class="widget-box">
            <h3 class="w-title">{{ $t('profile.account_info') }}</h3>
            <ul class="info-list">
              <li v-if="user.createdAt" class="info-row">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>
                <span>{{ $t('profile.joined') }}</span>
                <strong>{{ user.createdAt }}</strong>
              </li>
              <li v-if="user.totalViews" class="info-row">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                <span>{{ $t('profile.total_views') }}</span>
                <strong>{{ user.totalViews }}</strong>
              </li>
              <template v-if="isOwnProfile">
                <li v-if="user.lastLoginAt" class="info-row">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                  <span>{{ $t('profile.last_login') }}</span>
                  <strong>{{ user.lastLoginAt }}</strong>
                </li>
                <li v-if="user.lastLoginIp" class="info-row">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/></svg>
                  <span>{{ $t('profile.last_ip') }}</span>
                  <strong class="mono-text">{{ user.lastLoginIp }}</strong>
                </li>
                <li class="info-row">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                  <span>{{ $t('profile.mfa_status') }}</span>
                  <router-link to="/settings/security" class="mfa-badge" :class="user.mfaEnabled ? 'mfa-on' : 'mfa-off'">
                    {{ user.mfaEnabled ? $t('profile.mfa_on') : $t('profile.mfa_off') }}
                  </router-link>
                </li>
              </template>
            </ul>
          </div>

          <div class="widget-box">
            <h3 class="w-title">{{ $t('profile.achievements') }}</h3>
            <div v-if="achievements.length === 0" class="award-empty">
              <span class="icon">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              </span>
              <span>{{ $t('profile.no_achievements') }}</span>
            </div>
            <div v-else class="achievement-grid">
              <div v-for="ach in achievements" :key="ach.uaid" class="ach-card" :title="ach.description">
                <div class="ach-icon-wrap">
                  <span class="ach-icon">{{ ach.icon || '🏆' }}</span>
                </div>
                <div class="ach-info">
                  <strong>{{ ach.achievementName }}</strong>
                  <span>{{ ach.description }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="widget-box" v-if="isOwnProfile">
            <h3 class="w-title">Bằng/Chứng nhận</h3>
            <div v-if="certificatesLoading" class="award-empty">
              <span class="icon">⏳</span>
              <span>Đang tải chứng nhận...</span>
            </div>
            <div v-else-if="certificates.length === 0" class="award-empty">
              <span class="icon">📄</span>
              <span>Chưa có chứng nhận tuần.</span>
            </div>
            <div v-else class="achievement-grid">
              <div v-for="cert in certificates" :key="cert.id" class="ach-card">
                <div class="ach-icon-wrap"><span class="ach-icon">🏅</span></div>
                <div class="ach-info">
                  <strong>#{{ cert.rank }} - {{ cert.title }}</strong>
                  <span>{{ cert.weekStart }} → {{ cert.weekEnd }} • Score: {{ cert.score }}</span>
                  <span>{{ cert.certificateCode }}</span>
                </div>
              </div>
            </div>
          </div>

        </div>
      </aside>

    </div>
  </div>

  <!-- Edit Profile Modal -->
  <teleport to="body">
    <transition name="modal-fade">
      <div v-if="showEditModal" class="edit-modal-overlay" @click.self="showEditModal = false">
        <div class="edit-modal-card">
          <div class="edit-modal-header">
            <h2>{{ $t('profile.edit') }}</h2>
            <button class="btn-close" @click="showEditModal = false">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
          </div>

          <div class="edit-modal-body">
            <!-- Avatar preview -->
            <div class="edit-avatar-section">
              <div class="edit-avatar-wrap">
                <img :src="editForm.avatarPreview || user.avatar" class="edit-avatar-img" alt="Avatar">
                <label class="edit-avatar-overlay" title="Change photo">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
                  <input type="file" accept="image/*" class="file-hidden" @change="onAvatarChange">
                </label>
              </div>
              <p class="avatar-hint">{{ $t('profile.hint_avatar') }}</p>
            </div>

            <div class="edit-field">
              <label>{{ $t('profile.label_name') }}</label>
              <input v-model="editForm.username" type="text" :placeholder="$t('profile.label_name')" maxlength="50">
            </div>

            <div class="edit-field">
              <label>{{ $t('profile.label_bio') }}</label>
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
import { getMyCertificates } from '@/services/certificateService'

const route = useRoute()
const authStore = useAuthStore()

const user = ref({
  name: '', handle: '', avatar: '', bio: '',
  isPremium: false,
  postsCount: 0, followers: '0', following: 0, point: 0, totalLikes: 0,
  totalViews: 0, createdAt: '', lastLoginAt: '', lastLoginIp: '', mfaEnabled: 0
})
const allPosts = ref([])
const postsLoading = ref(true)
const activeCategory = ref('All')
const achievements = ref([])
const certificates = ref([])
const certificatesLoading = ref(false)
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
      name:       profile.username || 'Chef',
      handle:     (profile.username || 'chef').toLowerCase().replace(/\s+/g, '_'),
      avatar:     profile.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(profile.username||'G')}&background=EA580C&color=fff`,
      bio:        profile.bio || '',
      isPremium:  profile.isPremium === 1 || profile.isPremium === true,
      postsCount: profile.postCount || 0,
      followers:  profile.followerCount > 999
                    ? `${(profile.followerCount/1000).toFixed(1)}k`
                    : `${profile.followerCount || 0}`,
      following:  profile.followingCount || 0,
      point:      profile.point || 0,
      totalViews: profile.totalViews > 999
                    ? `${(profile.totalViews/1000).toFixed(1)}k`
                    : `${profile.totalViews || 0}`,
      createdAt:  profile.createdAt
                    ? new Date(profile.createdAt).toLocaleDateString('vi-VN', { year: 'numeric', month: 'long', day: 'numeric' })
                    : '',
      lastLoginAt: profile.lastLoginAt
                    ? new Date(profile.lastLoginAt).toLocaleString('vi-VN', { dateStyle: 'short', timeStyle: 'short' })
                    : '',
      lastLoginIp:  profile.lastLoginIp || '',
      mfaEnabled:   profile.mfaEnabled || 0
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
    if (isOwnProfile.value) {
      certificatesLoading.value = true
      try {
        certificates.value = await getMyCertificates()
      } catch {
        certificates.value = []
      } finally {
        certificatesLoading.value = false
      }
    }
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
    toast.error('Action failed, please try again.')
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
  const targetId = route.params.id || authStore.user?.accountID
  if (!targetId) return
  editSaving.value = true
  try {
    const payload = { username: editForm.value.username, bio: editForm.value.bio }
    // Upload new avatar if user selected a file
    if (editForm.value.avatarFile) {
      try {
        const avatarUrl = await uploadMedia(editForm.value.avatarFile)
        payload.avatar = avatarUrl
      } catch { /* avatar upload failed — skip updating avatar */ }
    }
    await updateUserProfile(targetId, payload)
    // Refresh profile
    await loadProfile()
    showEditModal.value = false
    toast.success('Profile updated!')
  } catch (err) {
    toast.error('Failed to save. Please try again.')
  } finally {
    editSaving.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped lang="scss" src="./ProfilePage.scss"></style>
