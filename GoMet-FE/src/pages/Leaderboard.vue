<template>
  <div class="gomet-hall-master" @mousemove="handleMouseMove" style="background: #FAFAF9; min-height: 100vh;">
  
    <HeroSanctum 
      v-model="activeCategory" 
      :mousePos="mousePos" 
      :categories="categories" 
      :topDish="leaderboardData[activeCategory].top1"
    />

    <NetflixRanking :data="leaderboardData[activeCategory].top10" :category="activeCategory" />

    <LegacyFeed :posts="leaderboardData[activeCategory].feed" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import HeroSanctum from '@/components/leaderboard/HeroSanctum.vue'
import NetflixRanking from '@/components/leaderboard/NetflixRanking.vue'
import LegacyFeed from '@/components/leaderboard/LegacyFeed.vue'
import { getLeaderboardPosts, getLeaderboardUsers } from '@/services/leaderboardService'

const { t } = useI18n()
const activeCategory = ref('dishes')
const mousePos = ref({ x: 0, y: 0 })
const categories = computed(() => [
  { id: 'dishes', name: t('leaderboard.top_dishes') },
  { id: 'chefs',  name: t('leaderboard.top_chefs') }
])

const handleMouseMove = (e) => {
  mousePos.value = {
    x: (e.clientX / window.innerWidth - 0.5) * 20,
    y: (e.clientY / window.innerHeight - 0.5) * 20
  }
}

// ── Data ───────────────────────────────────────────────────
const dishesTop10 = ref([])
const usersTop10 = ref([])

function buildDishTop1(list) {
  if (!list?.length) return null
  const p = list[0]
  return {
    name:            p.title,
    image:           p.media || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800',
    chefName:        p.authorName || 'GoMet Chef',
    chefAvatar:      p.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(p.authorName||'G')}&background=EA580C&color=fff`,
    time:            p.cookingTime ? `${p.cookingTime} min` : '—',
    views:           p.views > 999 ? `${(p.views/1000).toFixed(1)}K views` : `${p.views} views`,
    publishedAt:     '—',
    difficulty:      { 1: 'Easy', 2: 'Medium', 3: 'Hard' }[p.level] || 'Medium',
    difficultyClass: { 1: 'easy', 2: 'medium', 3: 'hard' }[p.level] || 'medium',
    description:     p.description || 'A culinary masterpiece on GoMet.',
    ingredients:     (p.ingredients || '').split('•').map(s => s.trim()).filter(Boolean)
  }
}

function buildUserTop1(list) {
  if (!list?.length) return null
  const u = list[0]
  return {
    name:        u.username,
    image:       u.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(u.username)}&background=EA580C&color=fff`,
    chefName:    u.username,
    chefAvatar:  u.avatar || '',
    time:        '—',
    views:       u.followerCount > 999 ? `${(u.followerCount/1000).toFixed(1)}K` : `${u.followerCount}`,
    description: 'Top member on GoMet.',
    postCount:   u.postCount || 0,
    joinedAt:    '—',
    followers:   u.followerCount > 999 ? `${(u.followerCount/1000).toFixed(1)}K` : `${u.followerCount}`
  }
}

const leaderboardData = computed(() => ({
  dishes: {
    top1:  buildDishTop1(dishesTop10.value),
    top10: dishesTop10.value.map((p, i) => ({
      id:           p.postID,
      name:         p.title,
      pts:          p.score ? Math.round(p.score) : 0,
      image:        p.media || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400',
      authorName:   p.authorName || 'GoMet',
      authorAvatar: p.authorAvatar || `https://ui-avatars.com/api/?name=G&background=EA580C&color=fff`
    })),
    feed: dishesTop10.value.slice(5).map(p => ({
      id:          p.postID,
      title:       p.title,
      pts:         p.score ? Math.round(p.score).toLocaleString() : '0',
      likes:       p.favoriteCount > 999 ? `${(p.favoriteCount/1000).toFixed(1)}K` : `${p.favoriteCount}`,
      comments:    `${p.ratingCount || 0}`,
      image:       p.media || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400',
      authorName:  p.authorName || 'GoMet',
      authorAvatar:p.authorAvatar || '',
      description: p.description || ''
    }))
  },
  chefs: {
    top1:  buildUserTop1(usersTop10.value),
    top10: usersTop10.value.map((u, i) => ({
      id:        u.accountID,
      name:      u.username,
      image:     u.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(u.username)}&background=EA580C&color=fff`,
      postCount: u.postCount || 0,
      joinedAt:  '—',
      followers: u.followerCount > 999 ? `${(u.followerCount/1000).toFixed(1)}K` : `${u.followerCount}`
    })),
    feed: []
  }
}))

onMounted(async () => {
  try {
    const [posts, users] = await Promise.all([
      getLeaderboardPosts(10),
      getLeaderboardUsers(10)
    ])
    dishesTop10.value = posts
    usersTop10.value = users
  } catch (err) {
    console.warn('Leaderboard: API error', err)
  }
})
</script>