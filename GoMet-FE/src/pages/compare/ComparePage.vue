<template>
  <div class="compare-page">
    <div class="container">
      
      <div class="page-header">
        <h1>Compare Dishes ⚖️</h1>
        <button class="btn-back" @click="$router.push('/')">← Back to Home</button>
      </div>

      <div v-if="compareStore.items.length === 0" class="empty-state">
        <div class="icon-box">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M2 22a9 9 0 1 1 18 0"/><path d="M12 13a5 5 0 0 0 5-5A5 5 0 0 0 9.8 4.2"/><path d="M12 13a5 5 0 0 1-3.5-1.5"/></svg>
        </div>
        <h3>No dishes to compare yet</h3>
        <p>Browse around and pick at least 2 dishes to compare!</p>
        <button class="btn-primary" @click="$router.push('/')">Explore Now</button>
      </div>

      <div v-else class="compare-grid-wrapper">
        <div class="grid-col labels-col">
          <div class="cell header-cell"></div> <div class="cell label">Rating</div>
          <div class="cell label">Author</div>
          <div class="cell label">Cook Time</div>
          <div class="cell label">Difficulty</div>
          <div class="cell label">Calories (Est.)</div>
          <div class="cell label">Actions</div>
        </div>

        <div 
          v-for="item in compareStore.items" 
          :key="item.id" 
          class="grid-col item-col"
        >
          <div class="cell header-cell">
            <div class="img-wrap">
              <img :src="item.image" loading="lazy">
              <button class="btn-remove" @click="compareStore.toggleItem(item)" title="Remove">✕</button>
            </div>
            <h3 class="item-title" @click="goToDetail(item.id)">{{ item.title }}</h3>
          </div>

          <div class="cell" :class="{ 'winner': isBest('likes', item.likes) }">
            <div class="stat-box">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg> 
              <b>{{ formatNumber(item.likes) }}</b>
              <span class="win-badge" v-if="isBest('likes', item.likes)">Highest</span>
            </div>
          </div>

          <div class="cell">
            <div class="author-row">
              <img :src="item.author?.avatar || 'https://i.pravatar.cc/100'" class="avt-small">
              <span>{{ item.author?.name || 'Gomet Chef' }}</span>
            </div>
          </div>

          <div class="cell" :class="{ 'winner': isBest('time', item.time) }">
            <span class="value">{{ item.time || '30p' }}</span>
              <span class="win-badge" v-if="isBest('time', item.time)">Fastest</span>
          </div>

          <div class="cell">
            <span class="tag-difficulty" :class="getDifficultyClass(item.difficulty)">
              {{ item.difficulty || 'Medium' }}
            </span>
          </div>

          <div class="cell">
            <span class="value">{{ getRandomCalo() }} Kcal</span>
          </div>

          <div class="cell action-cell">
            <button class="btn-view" @click="goToDetail(item.id)">View Recipe</button>
          </div>
        </div>

        <div v-if="compareStore.items.length < 3" class="grid-col add-col">
          <div class="add-placeholder" @click="$router.push('/')">
            <div class="plus-circle">+</div>
            <span>Add another dish</span>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import { useCompareStore } from '@/stores/compare'
import { useRouter } from 'vue-router'

const compareStore = useCompareStore()
const router = useRouter()

const goToDetail = (id) => {
  router.push({ name: 'PostDetail', params: { id } })
}

// Format number (1200 -> 1.2k)
const formatNumber = (num) => {
  if (!num) return 0;
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k';
  return num;
}

// Logic to find the winner (Highlight)
const isBest = (criteria, value) => {
  if (compareStore.items.length < 2) return false; // Only compare when there are >= 2 dishes

  if (criteria === 'likes') {
    // Find highest likes
    const max = Math.max(...compareStore.items.map(i => i.likes || 0))
    return value === max
  }
  
  if (criteria === 'time') {
    // Parse string '30p', '1h' into minutes for comparison
    const parseTime = (t) => {
      if (!t) return 999;
      if (t.includes('h')) return parseInt(t) * 60;
      return parseInt(t);
    }
    const min = Math.min(...compareStore.items.map(i => parseTime(i.time)))
    return parseTime(value) === min
  }
  return false
}

const getDifficultyClass = (diff) => {
  if (diff === 'Easy') return 'easy'
  if (diff === 'Hard') return 'hard'
  return 'medium'
}

// Demo random calories since source data doesn't include them yet
const getRandomCalo = () => Math.floor(Math.random() * (800 - 300) + 300)
</script>

<style scoped>
.compare-page {
  font-family: 'Mulish', sans-serif; padding: 40px 0 80px; min-height: 80vh;
  background: #FAFAF9;
}
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }

/* HEADER */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.page-header h1 { font-size: 2rem; font-weight: 800; color: #1C1917; margin: 0; }
.btn-back { background: none; border: none; font-weight: 600; color: #57534E; cursor: pointer; font-size: 1rem; transition: 0.2s; }
.btn-back:hover { color: #EA580C; transform: translateX(-5px); }

/* GRID LAYOUT */
.compare-grid-wrapper {
  display: flex; gap: 20px; overflow-x: auto; padding-bottom: 20px;
}

.grid-col {
  flex: 1; min-width: 280px; display: flex; flex-direction: column; gap: 15px;
}

/* COL 1: LABELS */
.labels-col { flex: 0 0 200px; min-width: 200px; }
.labels-col .header-cell { background: transparent; border: none; box-shadow: none; }
.labels-col .cell.label {
  justify-content: flex-start; font-weight: 700; color: #78716C; 
  background: transparent; border: none; padding-left: 0; font-size: 0.95rem;
}

/* CELLS STYLE */
.cell {
  background: white; border-radius: 16px; padding: 20px; height: 80px;
  display: flex; align-items: center; justify-content: center;
  border: 1px solid #E5E7EB; color: #1C1917; font-weight: 600;
  position: relative; transition: 0.2s;
}
.cell.winner { background: #ECFDF5; border-color: #10B981; color: #065F46; } /* Highlight xanh */
.win-badge {
  position: absolute; top: -8px; right: 10px; background: #10B981; color: white;
  font-size: 0.6rem; padding: 2px 8px; border-radius: 10px; font-weight: 800;
  box-shadow: 0 2px 5px rgba(16, 185, 129, 0.3);
}

/* HEADER CELL (IMAGE) */
.header-cell { height: auto; min-height: 280px; flex-direction: column; align-items: flex-start; padding: 15px; position: relative; }
.img-wrap { width: 100%; height: 180px; border-radius: 12px; overflow: hidden; position: relative; margin-bottom: 15px; }
.img-wrap img { width: 100%; height: 100%; object-fit: cover; }
.btn-remove { 
  position: absolute; top: 10px; right: 10px; width: 30px; height: 30px; 
  background: rgba(0,0,0,0.6); color: white; border: none; border-radius: 50%; 
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: 0.2s;
}
.btn-remove:hover { background: #EF4444; }
.item-title { margin: 0; font-size: 1.1rem; line-height: 1.4; cursor: pointer; transition: 0.2s; }
.item-title:hover { color: #EA580C; }

/* SPECIFIC CELLS */
.author-row { display: flex; align-items: center; gap: 10px; }
.avt-small { width: 32px; height: 32px; border-radius: 50%; }

.tag-difficulty { padding: 6px 16px; border-radius: 20px; font-size: 0.85rem; }
.tag-difficulty.easy { background: #DCFCE7; color: #166534; }
.tag-difficulty.medium { background: #FEF3C7; color: #B45309; }
.tag-difficulty.hard { background: #FEE2E2; color: #991B1B; }

.btn-view {
  width: 100%; padding: 12px; background: #1C1917; color: white; border: none;
  border-radius: 12px; font-weight: 700; cursor: pointer; transition: 0.2s;
}
.btn-view:hover { background: #EA580C; transform: translateY(-2px); }

/* ADD PLACEHOLDER */
.add-col { opacity: 0.6; transition: 0.2s; }
.add-col:hover { opacity: 1; }
.add-placeholder {
  height: 100%; border: 2px dashed #D6D3D1; border-radius: 20px;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  cursor: pointer; color: #78716C; gap: 15px; min-height: 400px;
}
.plus-circle {
  width: 60px; height: 60px; border-radius: 50%; background: #E5E5E5;
  display: flex; align-items: center; justify-content: center; font-size: 2rem;
}

/* EMPTY STATE */
.empty-state { text-align: center; padding: 100px 0; }
.icon-box { font-size: 4rem; margin-bottom: 20px; }
.empty-state h3 { font-size: 1.5rem; margin-bottom: 10px; }
.empty-state p { color: #78716C; margin-bottom: 30px; }
.btn-primary { padding: 12px 30px; background: #EA580C; color: white; border: none; border-radius: 30px; font-weight: 700; cursor: pointer; }

@media (max-width: 768px) {
  .labels-col { display: none; } /* Hide labels on mobile for more space */
  .grid-col { min-width: 85vw; } /* Each dish takes up most of screen, swipe horizontally */
  .compare-grid-wrapper { padding: 0 10px 20px; }
  .cell { height: auto; padding: 15px; justify-content: flex-start; }
  /* Add pseudo labels inside cells on mobile */
  .cell::before { content: attr(data-label); font-weight: 700; color: #9CA3AF; margin-right: 10px; display: none; } 
}
</style>