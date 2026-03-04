<template>
  <div class="gomet-hall-master" @mousemove="handleMouseMove">
    <HeroSanctum 
      v-if="leaderboardData.dishes.top1 || leaderboardData.chefs.top1"
      v-model="activeCategory"
      :topDish="leaderboardData.dishes.top1"
      :topUser="leaderboardData.chefs.top1"
    />

    <CinematicList 
      v-if="leaderboardData[activeCategory].remaining.length > 0"
      :data="leaderboardData[activeCategory].remaining" 
      :category="activeCategory"
      :start-rank="2"
    />

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'

// Import 2 component chính
import HeroSanctum from '@/components/leaderboard/HeroCinematic.vue'
import CinematicList from '@/components/leaderboard/CinematicList.vue' // Import file mới tạo

import { getLeaderboardPosts, getLeaderboardUsers } from '@/services/leaderboardService'

const { t } = useI18n()
const activeCategory = ref('dishes')
const mousePos = ref({ x: 0, y: 0 })
const categories = computed(() => [
  { id: 'dishes', name: t('leaderboard.top_dishes', 'Tuyệt tác ẩm thực') },
  { id: 'chefs',  name: t('leaderboard.top_chefs', 'Đầu bếp Elite') }
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
    id:              p.postID,
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
    id:          u.accountID,
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

// Cấu trúc lại Computed: Chỉ giữ Top 1 và Remaining (Top 2-10)
const leaderboardData = computed(() => ({
  dishes: {
    top1: buildDishTop1(dishesTop10.value),
    // Cắt từ phần tử số 1 (Top 2) trở đi
    remaining: dishesTop10.value.slice(1).map(p => ({
      id:           p.postID,
      title:        p.title,
      pts:          p.score ? Math.round(p.score).toLocaleString() : '0',
      image:        p.media || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400',
      authorName:   p.authorName || 'GoMet',
      authorAvatar: p.authorAvatar || `https://ui-avatars.com/api/?name=G&background=EA580C&color=fff`
    }))
  },
  chefs: {
    top1: buildUserTop1(usersTop10.value),
    // Cắt từ phần tử số 1 (Top 2) trở đi
    remaining: usersTop10.value.slice(1).map(u => ({
      id:        u.accountID,
      name:      u.username,
      image:     u.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(u.username)}&background=EA580C&color=fff`,
      postCount: u.postCount || 0,
      followers: u.followerCount > 999 ? `${(u.followerCount/1000).toFixed(1)}K` : `${u.followerCount}`
    }))
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

<style scoped>
/* Chìa khóa mấu chốt để ghép nối mượt mà */
.gomet-hall-master {
  background-color: #050505; /* Màu đen sâu thẳm chuẩn Cinematic */
  min-height: 100vh;
  overflow: hidden; /* Ngăn scroll ngang nếu có lỗi CSS tràn viền */
}
</style>