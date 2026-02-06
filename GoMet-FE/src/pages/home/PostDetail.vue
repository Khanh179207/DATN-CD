<template>
  <div class="post-page-container">


    <RecipeGuide :post="post" />

    <RecipeInteraction :post="post" />

    <RelatedSuggestions :items="relatedPosts" @click-post="goToDetail" />

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// Import các "mảnh ghép"
import RecipeGuide from '@/components/post-detail/RecipeGuide.vue'
import RecipeInteraction from '@/components/post-detail/RecipeInteraction.vue'
import RelatedSuggestions from '@/components/post-detail/RelatedSuggestions.vue'

const route = useRoute()
const router = useRouter()

// Dữ liệu Demo (Vẫn giữ nguyên ở đây để truyền xuống)
const post = ref({
  id: route.params.id,
  title: 'Gà Kho Gừng Thơm Lừng Hao Cơm',
  image: 'https://images.unsplash.com/photo-1604908177453-7462950a6a3b?w=1200&q=95&auto=format&fit=crop',
  author: 'Nguyễn Khánh',
  authorAvatar: 'https://ui-avatars.com/api/?name=Khanh+Nguyen&background=EA580C&color=fff',
  description: 'Thịt gà kho gừng là món ăn kinh điển...',
  time: '45 phút', difficulty: 'Trung bình', servings: '4 người', category: 'Món Chính',
  ingredientsRaw: '1/2 con gà ta • 1 củ gừng lớn • 3 củ hành tím',
  steps: [
    { desc: 'Sơ chế gà sạch sẽ...', images: ['https://images.unsplash.com/photo-1610057099443-fde8c4d203c3?w=400'] },
    { desc: 'Ướp gà với hành tỏi...', images: [] },
    { desc: 'Kho lửa liu riu...', images: [] }
  ]
})

const relatedPosts = ref([
  { id: 101, title: 'Thịt Kho Tàu', image: 'https://images.unsplash.com/photo-1603105037880-880cd4edfb0d?w=400', time: '1h 30p' },
  { id: 102, title: 'Canh Chua Cá', image: 'https://images.unsplash.com/photo-1626776649725-c85425870e5e?w=400', time: '30p' },
  { id: 103, title: 'Đậu Hũ Nhồi', image: 'https://images.unsplash.com/photo-1626776649779-37d19380fb28?w=400', time: '45p' },
  { id: 104, title: 'Rau Muống Xào', image: 'https://images.unsplash.com/photo-1618449840665-9ed54459646b?w=400', time: '10p' },
])

const goToDetail = (id) => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
  router.push(`/home/post/${id}`)
}
</script>

<style scoped>
.post-page-container { background-color: #F8FAFC; min-height: 100vh; font-family: 'Mulish', sans-serif; }
.sticky-toolbar { position: sticky; top: 0; z-index: 100; background: rgba(255,255,255,0.9); backdrop-filter: blur(10px); padding: 10px 0; border-bottom: 1px solid #eee; }
.toolbar-inner { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.btn-back-vip { border: none; background: none; font-weight: 700; cursor: pointer; font-size: 1rem; color: #333; }
</style>