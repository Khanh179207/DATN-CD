<template>
  <div class="gomet-hall-master" @mousemove="handleMouseMove">
    <template v-if="dishesTop10.length || usersTop10.length">
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
        :current-timeframe="timeframe"
        @update-timeframe="handleTimeChange"
      />
    </template>
    
    <div v-else class="empty-state" style="color: white; text-align: center; padding-top: 20vh;">
       <h2>Chưa có dữ liệu xếp hạng 🏆</h2>
       <p>Hãy là người đầu tiên tương tác để đưa các Tuyệt tác lên Top!</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'

import HeroSanctum from '@/components/leaderboard/HeroCinematic.vue'
import CinematicList from '@/components/leaderboard/CinematicList.vue'

// Sử dụng service
import { getTrendingPosts } from '@/services/postService'
import { getLeaderboardChefs } from '@/services/userService'

const { t } = useI18n()
const activeCategory = ref('dishes')
const timeframe = ref('month') 
const mousePos = ref({ x: 0, y: 0 })

const handleMouseMove = (e) => {
  mousePos.value = {
    x: (e.clientX / window.innerWidth - 0.5) * 20,
    y: (e.clientY / window.innerHeight - 0.5) * 20
  }
}

// ── Data ───────────────────────────────────────────────────
const dishesTop10 = ref([])
const usersTop10 = ref([])

const handleTimeChange = (newTime) => {
  timeframe.value = newTime
  loadLeaderboard()
}

function buildDishTop1(list) {
  if (!list?.length) return null
  const p = list[0]
  return {
    id:              p.id,
    name:            p.title,
    image:           p.image || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800',
    authorName:      p.authorName || 'GoMet Chef', 
    authorAvatar:    `https://ui-avatars.com/api/?name=${encodeURIComponent(p.authorName||'G')}&background=EA580C&color=fff`, 
    time:            '—',
    pts:             p.pts || 0, // Cột pts từ bảng Món ăn
    publishedAt:     '—',
    difficulty:      'Medium',
    description:     p.description || 'A culinary masterpiece on GoMet.',
    isPremium:       p.isPremium || 0
  }
}

function buildUserTop1(list) {
  if (!list?.length) return null
  const u = list[0]
  return {
    id:          u.id,
    name:        u.name,
    image:       u.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(u.name)}&background=EA580C&color=fff`,
    description: u.description || 'Top member on GoMet.',
    postCount:   u.postCount || 0,
    followers:   u.followers > 999 ? `${(u.followers/1000).toFixed(1)}K` : u.followers, 
    isPremium:   u.isPremium || 0,
    pts:         u.totalPts || 0 // 🔥 THÊM totalPts ĐỂ HIỂN THỊ ĐIỂM CỦA ĐẦU BẾP TOP 1
  }
}

const leaderboardData = computed(() => ({
  dishes: {
    top1: buildDishTop1(dishesTop10.value),
    remaining: dishesTop10.value.slice(1).map(p => ({
      id:           p.id,
      title:        p.title,
      pts:          p.pts ?? 0, 
      image:        p.image, 
      authorName:   p.authorName,
      authorAvatar: `https://ui-avatars.com/api/?name=${encodeURIComponent(p.authorName||'G')}&background=EA580C&color=fff`,
      isPremium:    p.isPremium ?? 0
    }))
  },
  chefs: {
    top1: buildUserTop1(usersTop10.value),
    remaining: usersTop10.value.slice(1).map(u => ({
      id:        u.id,
      name:      u.name,
      avatar:    u.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(u.name)}&background=EA580C&color=fff`,
      image:     u.avatar, 
      postCount: u.postCount || 0,
      followers: u.followers ?? 0,
      isPremium: u.isPremium ?? 0,
      pts:       u.totalPts ?? 0 // 🔥 THÊM totalPts ĐỂ HIỂN THỊ ĐIỂM CHO DANH SÁCH BÊN DƯỚI
    }))
  }
}))

const loadLeaderboard = async () => {
  try {
    const [posts, users] = await Promise.all([
      getTrendingPosts(timeframe.value, 10),
      getLeaderboardChefs(timeframe.value, 10)
    ])
    // Gán dữ liệu trả về từ API vào ref
    dishesTop10.value = posts || []
    usersTop10.value = users || []
  } catch (err) {
    console.warn('Leaderboard: API error', err)
  }
}

onMounted(() => {
  loadLeaderboard()
})
</script>

<style scoped>
.gomet-hall-master {
  background-color: #050505;
  min-height: 100vh;
  overflow-x: hidden;
  overflow-y: auto; 
}
</style>