<template>
  <div class="recipe-card-wrapper" @click="goToDetail">
    
    <div class="card-media">
      <img :src="post.image" :alt="post.title" loading="lazy" class="main-img">
      
      <div class="media-overlay"></div>

      <span class="category-tag" v-if="post.category">{{ post.category }}</span>

      <div class="quick-actions">
        <button class="btn-action" @click.stop="toggleSave" title="Lưu công thức">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" :class="{ 'filled': isSaved }">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path>
          </svg>
        </button>

        <button 
          class="btn-action btn-compare" 
          :class="{ active: compareStore.isSelected(post.id) }" 
          @click.stop="compareStore.toggleItem(post)"
          title="So sánh"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 3h5v5"></path><path d="M4 20L21 3"></path><path d="M21 16v5h-5"></path><path d="M15 15l5 5"></path><path d="M4 4l5 5"></path></svg>
        </button>
      </div>
    </div>

    <div class="card-content">
      
      <div class="meta-top">
        <div class="rating-box">
          <span class="star">★</span>
          <span class="score">{{ post.rating || 4.5 }}</span>
          <span class="count">({{ post.reviews || 0 }})</span>
        </div>
        <div class="time-badge">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
          {{ post.time || '30p' }}
        </div>
      </div>

      <h3 class="card-title">{{ post.title }}</h3>

      <div class="card-footer">
        <div class="author-info">
          <div class="author-avt">
            <img v-if="post.author && post.author.avatar" :src="post.author.avatar" alt="Author">
            <div v-else class="avt-placeholder">{{ getInitials(post.author?.name) }}</div>
          </div>
          <span class="author-name">{{ post.author?.name || 'Gomet Chef' }}</span>
        </div>

        <div class="stats-like">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
          <span>{{ formatNumber(post.likes) }}</span>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCompareStore } from '@/stores/compare'

const props = defineProps({
  post: { type: Object, required: true, default: () => ({}) }
})

const router = useRouter()
const compareStore = useCompareStore()
const isSaved = ref(false)

const getInitials = (name) => {
  if (!name || typeof name !== 'string') return 'GM'; 
  return name.substring(0, 2).toUpperCase();
}

const toggleSave = () => { isSaved.value = !isSaved.value }

const goToDetail = () => {
  if (props.post.id) router.push({ name: 'PostDetail', params: { id: props.post.id } })
}

const formatNumber = (num) => {
  if (!num) return 0;
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k';
  return num;
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;500;600;700;800&display=swap');

.recipe-card-wrapper {
  background: white; border-radius: 24px; overflow: hidden;
  box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05); /* Bóng mờ tinh tế */
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  cursor: pointer; position: relative; font-family: 'Mulish', sans-serif;
  border: 1px solid transparent;
  display: flex; flex-direction: column; height: 100%;
}

.recipe-card-wrapper:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 50px -15px rgba(0,0,0,0.1);
}

/* --- MEDIA --- */
.card-media { position: relative; height: 200px; overflow: hidden; }
.main-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.7s ease; }
.recipe-card-wrapper:hover .main-img { transform: scale(1.08); }

.media-overlay {
  position: absolute; inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.3) 0%, transparent 50%);
  opacity: 0.5; transition: 0.3s; pointer-events: none;
}

/* Category Tag (Glassmorphism) */
.category-tag {
  position: absolute; top: 15px; left: 15px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  padding: 6px 14px; border-radius: 12px;
  font-size: 0.75rem; font-weight: 800; color: #EA580C;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  z-index: 2; letter-spacing: 0.5px;
}

/* Quick Actions */
.quick-actions {
  position: absolute; top: 15px; right: 15px; z-index: 2;
  display: flex; flex-direction: column; gap: 8px;
}

.btn-action {
  width: 38px; height: 38px; border-radius: 50%; border: none;
  background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  color: #4B5563; box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  opacity: 0; transform: translateX(10px);
}

.recipe-card-wrapper:hover .btn-action { opacity: 1; transform: translateX(0); }
.recipe-card-wrapper:hover .btn-action:nth-child(2) { transition-delay: 0.05s; }

.btn-action:hover { background: #EA580C; color: white; transform: scale(1.1) !important; }
.btn-action svg.filled { fill: #EF4444; stroke: #EF4444; }
.btn-compare.active { opacity: 1; transform: translateX(0); background: #1C1917; color: #EA580C; }

/* --- CONTENT --- */
.card-content { padding: 20px; flex: 1; display: flex; flex-direction: column; gap: 10px; }

/* Meta Top: Rating & Time */
.meta-top { display: flex; justify-content: space-between; align-items: center; font-size: 0.8rem; }
.rating-box { display: flex; align-items: center; gap: 4px; font-weight: 700; color: #F59E0B; }
.star { font-size: 1rem; margin-top: -2px; }
.count { color: #9CA3AF; font-weight: 500; font-size: 0.75rem; }
.time-badge { display: flex; align-items: center; gap: 5px; color: #6B7280; font-weight: 600; background: #F3F4F6; padding: 4px 10px; border-radius: 8px; }

/* Title */
.card-title {
  font-size: 1.1rem; font-weight: 800; color: #1C1917; margin: 5px 0 10px;
  line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2;
  -webkit-box-orient: vertical; overflow: hidden; transition: color 0.2s;
}
.recipe-card-wrapper:hover .card-title { color: #EA580C; }

/* Footer */
.card-footer {
  display: flex; justify-content: space-between; align-items: center;
  margin-top: auto; padding-top: 15px; border-top: 1px solid #F3F4F6;
}

.author-info { display: flex; align-items: center; gap: 10px; }
.author-avt { width: 28px; height: 28px; border-radius: 50%; overflow: hidden; background: #E5E5E5; border: 2px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.author-avt img { width: 100%; height: 100%; object-fit: cover; }
.avt-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 0.65rem; font-weight: 800; color: #9CA3AF; }
.author-name { font-size: 0.85rem; font-weight: 700; color: #4B5563; max-width: 100px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.stats-like { display: flex; align-items: center; gap: 6px; font-size: 0.85rem; font-weight: 700; color: #9CA3AF; transition: 0.2s; }
.recipe-card-wrapper:hover .stats-like { color: #EF4444; }
</style>