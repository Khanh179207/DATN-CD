<template>
  <div class="gomet-hall-master" @mousemove="handleMouseMove">
  
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
import { ref } from 'vue'
import HeroSanctum from '@/components/leaderboard/HeroSanctum.vue'
import NetflixRanking from '@/components/leaderboard/NetflixRanking.vue'
import LegacyFeed from '@/components/leaderboard/LegacyFeed.vue'

const activeCategory = ref('dishes')
const mousePos = ref({ x: 0, y: 0 })
const categories = [
  { id: 'dishes', name: 'BÀI VIẾT TUYỆT TÁC' },
  { id: 'chefs', name: 'NGƯỜI DÙNG CHẤT LƯỢNG' }
]

const handleMouseMove = (e) => {
  mousePos.value = {
    x: (e.clientX / window.innerWidth - 0.5) * 20,
    y: (e.clientY / window.innerHeight - 0.5) * 20
  }
}

const leaderboardData = {
  dishes: {
    top1: {
      name: 'Tuyệt Tác Mỳ Ý Carbonara',
      image: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c',
      chefName: 'Khánh Nguyễn',
      chefAvatar: 'https://i.pravatar.cc/100?u=1',
      time: '30 phút',
      views: '12.5K lượt xem',
      publishedAt: '15/01/2026',
      difficulty: 'Trung bình',
      difficultyClass: 'medium',
      ingredients: ['Mỳ Ý Spaghetti', 'Trứng gà', 'Phô mai Parmigiano', 'Pancetta', 'Kem tươi', 'Tiêu đen', 'Tỏi', 'Dầu ô liu'],
      description: 'Sự cân bằng hoàn hảo giữa kỹ thuật cổ điển Ý và nguyên liệu thượng hạng Gomet Elite.'
    },
    top10: Array.from({ length: 10 }, (_, i) => ({
      id: i + 1, name: ['Mỳ Ý Carbonara', 'Sashimi Cá Hồi', 'Bò Wellington', 'Tôm Hùm Phô Mai', 'Gan Ngỗng Pháp'][i] || 'Tuyệt Tác Gomet',
      pts: 12500 - (i * 900),
      image: `https://images.unsplash.com/photo-${['1546069901-ba9599a7e63c', '1467003909585-2f8a72700288', '1544025162-d76694265947', '1551218808-94e220e084d2', '1546241072-48010ad28c2c'][i] || '1511690656952-34342bb7c2f2'}`,
      authorName: 'Khánh Nguyễn', authorAvatar: 'https://i.pravatar.cc/100?u=1'
    })),
    feed: [{ id: 11, title: 'Ức Vịt Sốt Cam', pts: '8,450', likes: '1.2K', comments: '142', image: 'https://images.unsplash.com/photo-1511690656952-34342bb7c2f2', authorName: 'Hoàng Nam', authorAvatar: 'https://i.pravatar.cc/100?u=11', description: 'Hương vị béo ngậy tan chảy của thịt vịt thượng hạng.' }]
  },
  chefs: {
    top1: {
      name: 'Alexander',
      image: 'https://images.unsplash.com/photo-1583394838336-acd977736f90',
      chefName: 'Alexander',
      chefAvatar: 'https://i.pravatar.cc/100?u=alex',
      time: '15 Năm Exp',
      views: '450K',
      description: 'Bậc thầy về nghệ thuật Fine-Dining.',
      postCount: 128,
      joinedAt: '12/08/2024',
      followers: '24.5K'
    },
    top10: [
      { id: 1, name: 'Alexander', image: 'https://i.pravatar.cc/100?u=alex', postCount: 128, joinedAt: '12/08/2024', followers: '24.5K' },
      { id: 2, name: 'Khánh Nguyễn', image: 'https://i.pravatar.cc/100?u=1', postCount: 96, joinedAt: '05/09/2024', followers: '18.2K' },
      { id: 3, name: 'Mai Linh', image: 'https://i.pravatar.cc/100?u=ml', postCount: 84, joinedAt: '20/10/2024', followers: '15.8K' },
      { id: 4, name: 'Hoàng Nam', image: 'https://i.pravatar.cc/100?u=11', postCount: 72, joinedAt: '01/11/2024', followers: '12.3K' },
      { id: 5, name: 'Thu Hà', image: 'https://i.pravatar.cc/100?u=th', postCount: 65, joinedAt: '14/11/2024', followers: '10.1K' },
      { id: 6, name: 'Đức Anh', image: 'https://i.pravatar.cc/100?u=da', postCount: 58, joinedAt: '22/12/2024', followers: '9.2K' },
      { id: 7, name: 'Hương Giang', image: 'https://i.pravatar.cc/100?u=hg', postCount: 51, joinedAt: '08/01/2025', followers: '7.8K' },
      { id: 8, name: 'Minh Tuấn', image: 'https://i.pravatar.cc/100?u=mt', postCount: 47, joinedAt: '15/01/2025', followers: '6.5K' },
      { id: 9, name: 'Lan Anh', image: 'https://i.pravatar.cc/100?u=la', postCount: 42, joinedAt: '20/01/2025', followers: '5.9K' },
      { id: 10, name: 'Quang Huy', image: 'https://i.pravatar.cc/100?u=qh', postCount: 38, joinedAt: '28/01/2025', followers: '5.2K' }
    ],
    feed: []
  }
}
</script>