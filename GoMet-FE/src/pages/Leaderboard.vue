<template>
  <div 
    class="gomet-hall-master" 
    :style="{ '--mouse-x': mousePos.x + 'px', '--mouse-y': mousePos.y + 'px' }"
    @mousemove="handleMouseMove"
  >
    <transition name="fade-slow" mode="out-in">
      <div v-if="isLoading" class="loading-state">
        <div class="gomet-spinner"></div>
        <p class="loading-text">Đang tải</p>
      </div>

      <div v-else class="content-wrapper">
        <transition name="slide-fade" mode="out-in">
          <HeroSanctum 
            :key="activeCategory + '-hero'"
            v-model="activeCategory"
            :topDish="leaderboardData.dishes.top1"
            :topUser="leaderboardData.chefs.top1"
          />
        </transition>

        <transition name="slide-fade" mode="out-in">
          <CinematicList 
            :key="activeCategory + timeframe + '-list'"
            :data="leaderboardData[activeCategory].remaining" 
            :category="activeCategory"
            :start-rank="2"
            :current-timeframe="timeframe"
            @update-timeframe="handleTimeChange"
          />
        </transition>
      </div>
      
    </transition>
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
const isLoading = ref(true)

// ── HIỆU NĂNG CAO: Chuột mượt mà 60FPS ───────────────────────
const mousePos = ref({ x: 0, y: 0 })
let ticking = false

const handleMouseMove = (e) => {
  if (!ticking) {
    window.requestAnimationFrame(() => {
      mousePos.value = {
        x: (e.clientX / window.innerWidth - 0.5) * 30, // Biên độ chuyển động
        y: (e.clientY / window.innerHeight - 0.5) * 30
      }
      ticking = false
    })
    ticking = true
  }
}

// ── Dữ liệu ──────────────────────────────────────────────────
const dishesTop10 = ref([])
const usersTop10 = ref([])

const handleTimeChange = (newTime) => {
  timeframe.value = newTime
  loadLeaderboard()
}

// Hàm sinh Avatar thông minh
const getAvatar = (name) => `https://ui-avatars.com/api/?name=${encodeURIComponent(name || 'G')}&background=EA580C&color=fff&bold=true`

function buildDishTop1(list) {
  if (!list?.length) return null
  const p = list[0]
  return {
    id: p.id,
    name: p.title,
    image: p.image || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800',
    authorName: p.authorName || 'GoMet Chef', 
    authorAvatar: p.authorAvatar || getAvatar(p.authorName), 
    time: '—',
    pts: p.pts || 0,
    publishedAt: '—',
    difficulty: 'Medium',
    description: p.description || 'A culinary masterpiece on GoMet.',
    isPremium: p.isPremium || 0
  }
}

function buildUserTop1(list) {
  if (!list?.length) return null
  const u = list[0]
  return {
    id: u.id,
    name: u.name,
    image: u.avatar || getAvatar(u.name),
    description: u.description || 'Top member on GoMet.',
    postCount: u.postCount || 0,
    followers: u.followers > 999 ? `${(u.followers/1000).toFixed(1)}K` : (u.followers || 0), 
    isPremium: u.isPremium || 0,
    pts: u.totalPts || 0
  }
}

const leaderboardData = computed(() => ({
  dishes: {
    top1: buildDishTop1(dishesTop10.value),
    remaining: dishesTop10.value.slice(1).map(p => ({
      id: p.id,
      title: p.title,
      pts: p.pts ?? 0, 
      image: p.image, 
      authorName: p.authorName,
      authorAvatar: p.authorAvatar || getAvatar(p.authorName),
      isPremium: p.isPremium ?? 0
    }))
  },
  chefs: {
    top1: buildUserTop1(usersTop10.value),
    remaining: usersTop10.value.slice(1).map(u => ({
      id: u.id,
      name: u.name,
      avatar: u.avatar || getAvatar(u.name),
      image: u.avatar || getAvatar(u.name), 
      postCount: u.postCount || 0,
      followers: u.followers ?? 0,
      isPremium: u.isPremium ?? 0,
      pts: u.totalPts ?? 0 
    }))
  }
}))

const loadLeaderboard = async () => {
  isLoading.value = true
  try {
    const [posts, users] = await Promise.all([
      getTrendingPosts(timeframe.value, 10),
      getLeaderboardChefs(timeframe.value, 10)
    ])
    dishesTop10.value = posts || []
    usersTop10.value = users || []
  } catch (err) {
    console.warn('Leaderboard: API error', err)
  } finally {
    // Delay nhẹ 400ms để hiệu ứng UI được trơn tru, không chớp tắt
    setTimeout(() => {
      isLoading.value = false
    }, 400)
  }
}

onMounted(() => {
  loadLeaderboard()
})
</script>

<style scoped>
/* =======================================================
   🔥 CORE WRAPPER & PARALLAX BACKGROUND
======================================================= */
.gomet-hall-master {
  background-color: #050505;
  min-height: 100vh;
  width: 100%;
  overflow-x: hidden;
  overflow-y: auto; 
  position: relative;
  /* Ánh sáng chạy theo chuột nền mờ */
  background-image: radial-gradient(
    circle at calc(50% + var(--mouse-x, 0px)) calc(50% + var(--mouse-y, 0px)), 
    rgba(234, 88, 12, 0.08) 0%, 
    transparent 50%
  );
  transition: background 0.1s ease-out;
}

/* =======================================================
   🌀 TRẠNG THÁI LOADING (XỊN XÒ)
======================================================= */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 70vh;
}

.gomet-spinner {
  width: 50px;
  height: 50px;
  border: 3px solid rgba(234, 88, 12, 0.2);
  border-top-color: #EA580C;
  border-radius: 50%;
  animation: spin 1s cubic-bezier(0.55, 0.15, 0.45, 0.85) infinite;
  margin-bottom: 20px;
  box-shadow: 0 0 15px rgba(234, 88, 12, 0.4);
}

.loading-text {
  color: #EA580C;
  font-family: 'Mulish', sans-serif;
  font-weight: 600;
  letter-spacing: 1px;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes pulse {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}

/* =======================================================
   🎬 VUE TRANSITIONS (HIỆU ỨNG CHUYỂN CẢNH MƯỢT MÀ)
======================================================= */
/* Chuyển đổi giữa Loading - Content - Empty */
.fade-slow-enter-active,
.fade-slow-leave-active {
  transition: opacity 0.6s ease;
}
.fade-slow-enter-from,
.fade-slow-leave-to {
  opacity: 0;
}

/* Chuyển đổi trượt lên khi đổi tab (Món ăn / Đầu bếp) */
.slide-fade-enter-active {
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.slide-fade-enter-from {
  opacity: 0;
  transform: translateY(30px) scale(0.98);
}
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.98);
}

</style>