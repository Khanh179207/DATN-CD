<template>
  <div class="compare-page">
    <div class="container">
      
      <div class="page-header">
        <h1>So sánh món ăn ⚖️</h1>
        <button class="btn-back" @click="$router.push('/home')">← Quay lại</button>
      </div>

      <div v-if="compareStore.items.length === 0" class="empty-state">
        <div class="icon-box">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5"><path d="M2 22a9 9 0 1 1 18 0"/><path d="M12 13a5 5 0 0 0 5-5A5 5 0 0 0 9.8 4.2"/><path d="M12 13a5 5 0 0 1-3.5-1.5"/></svg>
        </div>
        <h3>Chưa có món ăn nào để so sánh</h3>
        <p>Sếp hãy dạo quanh và chọn ít nhất 2 món ăn để lên bàn cân nhé!</p>
        <button class="btn-primary" @click="$router.push('/')">Khám Phá Ngay</button>
      </div>

      <div v-else class="compare-grid-wrapper">
        
        <div class="grid-col labels-col">
          <div class="cell header-cell empty-label"></div> 
          <div class="cell label"><span class="l-icon">⭐</span> Đánh giá</div>
          <div class="cell label"><span class="l-icon">👁️</span> Lượt xem</div>
          <div class="cell label"><span class="l-icon">❤️</span> Lượt thích</div>
          <div class="cell label"><span class="l-icon">👨‍🍳</span> Tác giả</div>
          <div class="cell label"><span class="l-icon">📅</span> Ngày đăng</div>
          <div class="cell label"><span class="l-icon">⏱️</span> Thời gian nấu</div>
          <div class="cell label"><span class="l-icon">🔥</span> Độ khó</div>
          <div class="cell label action-label"></div>
        </div>

        <div 
          v-for="item in compareStore.items" 
          :key="item.id" 
          class="grid-col item-col"
        >
          <div class="cell header-cell">
            <div class="img-wrap">
              <img :src="item.image" loading="lazy">
              <button class="btn-remove" @click="compareStore.toggleItem(item)" title="Bỏ so sánh">✕</button>
            </div>
            <h3 class="item-title" @click="goToDetail(item.id)">{{ item.title }}</h3>
          </div>

          <div class="cell" :class="{ 'winner': isBest('rating', getRating(item)) }" data-label="Đánh giá:">
            <div class="stat-box">
              <b class="text-lg">{{ getRating(item).toFixed(1) }}</b>
              <span class="text-sm text-gray">/ 5.0</span>
              <span class="win-badge" v-if="isBest('rating', getRating(item))">Highest</span>
            </div>
          </div>

          <div class="cell" :class="{ 'winner': isBest('views', getViews(item)) }" data-label="Lượt xem:">
            <div class="stat-box">
              <b>{{ formatNumber(getViews(item)) }}</b>
              <span class="win-badge" v-if="isBest('views', getViews(item))">Most</span>
            </div>
          </div>

          <div class="cell" :class="{ 'winner': isBest('likes', getLikes(item)) }" data-label="Lượt thích:">
            <div class="stat-box">
              <b>{{ formatNumber(getLikes(item)) }}</b>
              <span class="win-badge" v-if="isBest('likes', getLikes(item))">Most</span>
            </div>
          </div>

          <div class="cell" data-label="Tác giả:">
            <div class="author-row">
              <img :src="item.author?.avatar || 'https://ui-avatars.com/api/?name=' + (item.author?.name || 'G')" class="avt-small">
              <span class="line-clamp-1">{{ item.author?.name || 'Gomet Chef' }}</span>
            </div>
          </div>

          <div class="cell" :class="{ 'winner': isBest('date', getDateTs(item)) }" data-label="Ngày đăng:">
            <span class="value">{{ formatDate(item) }}</span>
            <span class="win-badge" v-if="isBest('date', getDateTs(item))">Newest</span>
          </div>

          <div class="cell" :class="{ 'winner': isBest('time', parseTime(item.time)) }" data-label="Thời gian nấu:">
            <span class="value">{{ item.time || '30 min' }}</span>
            <span class="win-badge" v-if="isBest('time', parseTime(item.time))">Fastest</span>
          </div>

          <div class="cell" :class="{ 'winner': isBest('difficulty', getDifficultyWeight(item.difficulty)) }" data-label="Độ khó:">
            <span class="tag-difficulty" :class="getDifficultyClass(item.difficulty)">
              {{ item.difficulty || 'Medium' }}
            </span>
            <span class="win-badge" v-if="isBest('difficulty', getDifficultyWeight(item.difficulty))">Easiest</span>
          </div>

          <div class="cell action-cell">
            <button class="btn-view" @click="goToDetail(item.id)">Xem Công Thức</button>
          </div>
        </div>

        <div v-if="compareStore.items.length < 3" class="grid-col add-col">
          <div class="add-placeholder" @click="$router.push('/')">
            <div class="plus-circle">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
            </div>
            <span>Thêm món ăn khác</span>
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

// --- DATA HELPERS (Chống Null/Undefined) ---
const formatNumber = (num) => {
  if (!num) return 0;
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k';
  return num;
}

const getRating = (item) => Number(item.rating || item.avgRating || 0);
const getViews = (item) => Number(item.views || item.viewCount || 0);
const getLikes = (item) => Number(item.likes || item.likeCount || 0);

const getDateTs = (item) => {
  const d = item.createdAt || item.publishDate || item.date;
  if (!d) return 0;
  return new Date(d).getTime();
}

const formatDate = (item) => {
  const ts = getDateTs(item);
  if (!ts) return 'N/A';
  return new Date(ts).toLocaleString('vi-VN', { 
    day: '2-digit', 
    month: '2-digit', 
    year: 'numeric', 
    hour: '2-digit', 
    minute: '2-digit' 
  });
}

const parseTime = (t) => {
  if (!t) return 999;
  if (typeof t === 'number') return t;
  const str = String(t).toLowerCase();
  if (str.includes('h')) return parseInt(str) * 60 + (parseInt(str.split('h')[1]) || 0);
  return parseInt(str);
}

const getDifficultyWeight = (diff) => {
  const d = String(diff || '').toLowerCase();
  if (d.includes('easy') || d.includes('dễ')) return 1;
  if (d.includes('hard') || d.includes('khó')) return 3;
  return 2; // Medium
}

const getDifficultyClass = (diff) => {
  const w = getDifficultyWeight(diff);
  if (w === 1) return 'easy';
  if (w === 3) return 'hard';
  return 'medium';
}

// --- LOGIC TÌM WINNER ---
const isBest = (criteria, value) => {
  if (compareStore.items.length < 2) return false; 

  const items = compareStore.items;

  if (criteria === 'rating') {
    const max = Math.max(...items.map(i => getRating(i)));
    return max > 0 && value === max; // Không win nếu tất cả = 0
  }
  
  if (criteria === 'views') {
    const max = Math.max(...items.map(i => getViews(i)));
    return max > 0 && value === max;
  }
  
  if (criteria === 'likes') {
    const max = Math.max(...items.map(i => getLikes(i)));
    return max > 0 && value === max;
  }
  
  if (criteria === 'date') {
    const max = Math.max(...items.map(i => getDateTs(i)));
    return max > 0 && value === max;
  }
  
  if (criteria === 'time') {
    const min = Math.min(...items.map(i => parseTime(i.time)));
    return min < 999 && value === min;
  }

  if (criteria === 'difficulty') {
    const min = Math.min(...items.map(i => getDifficultyWeight(i.difficulty)));
    return value === min;
  }

  return false;
}
</script>

<style scoped>
.compare-page {
  font-family: 'Mulish', sans-serif; padding: 40px 0 80px; min-height: 80vh;
  background: #FAFAF9; color: #0F172A;
}
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }

/* HEADER */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 40px; }
.page-header h1 { font-size: 2.2rem; font-family: 'Playfair Display', serif; font-weight: 900; color: #0F172A; margin: 0; }
.btn-back { background: none; border: none; font-weight: 700; color: #64748B; cursor: pointer; font-size: 0.95rem; transition: 0.2s; display: flex; align-items: center; gap: 6px; }
.btn-back:hover { color: #EA580C; transform: translateX(-5px); }

/* GRID LAYOUT */
.compare-grid-wrapper {
  display: flex; gap: 24px; overflow-x: auto; padding-bottom: 20px; scroll-snap-type: x mandatory;
}
.compare-grid-wrapper::-webkit-scrollbar { height: 8px; }
.compare-grid-wrapper::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }

.grid-col { flex: 1; min-width: 280px; display: flex; flex-direction: column; gap: 16px; scroll-snap-align: start; }

/* COL 1: LABELS */
.labels-col { flex: 0 0 220px; min-width: 220px; }
.labels-col .header-cell { background: transparent; border: none; box-shadow: none; min-height: 260px; }
.labels-col .cell.label {
  justify-content: flex-start; font-weight: 700; color: #475569; 
  background: transparent; border: none; padding-left: 10px; font-size: 0.95rem;
}
.l-icon { margin-right: 8px; font-size: 1.1rem; }
.action-label { height: 60px !important; }

/* CELLS STYLE */
.cell {
  background: #FFFFFF; border-radius: 16px; padding: 0 20px; height: 64px;
  display: flex; align-items: center; justify-content: center;
  border: 1px solid #E2E8F0; color: #1E293B; font-weight: 600;
  position: relative; transition: all 0.3s ease; box-shadow: 0 2px 10px rgba(0,0,0,0.01);
}
/* WINNER HIGHLIGHT */
.cell.winner { background: #ECFDF5; border-color: #10B981; color: #065F46; box-shadow: 0 4px 15px rgba(16, 185, 129, 0.1); transform: scale(1.02); z-index: 2; }
.win-badge {
  position: absolute; top: -10px; right: 10px; background: #10B981; color: white;
  font-size: 0.65rem; padding: 4px 10px; border-radius: 100px; font-weight: 800; letter-spacing: 0.5px; text-transform: uppercase;
  box-shadow: 0 4px 10px rgba(16, 185, 129, 0.3); animation: popBadge 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
@keyframes popBadge { 0% { transform: scale(0); } 100% { transform: scale(1); } }

/* HEADER CELL (IMAGE) */
.header-cell { height: auto; min-height: 260px; flex-direction: column; align-items: flex-start; padding: 16px; position: relative; }
.img-wrap { width: 100%; height: 160px; border-radius: 12px; overflow: hidden; position: relative; margin-bottom: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.img-wrap img { width: 100%; height: 100%; object-fit: cover; }
.btn-remove { 
  position: absolute; top: 10px; right: 10px; width: 32px; height: 32px; 
  background: rgba(15, 23, 42, 0.6); color: white; border: none; border-radius: 50%; 
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: 0.2s; backdrop-filter: blur(4px); font-size: 14px;
}
.btn-remove:hover { background: #EF4444; transform: scale(1.1); }
.item-title { margin: 0; font-size: 1.15rem; line-height: 1.4; font-weight: 800; cursor: pointer; transition: 0.2s; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.item-title:hover { color: #EA580C; }

/* SPECIFIC CELLS */
.stat-box { display: flex; align-items: baseline; gap: 4px; }
.text-lg { font-size: 1.2rem; }
.text-sm { font-size: 0.85rem; }
.text-gray { color: #94A3B8; }

.author-row { display: flex; align-items: center; gap: 10px; width: 100%; }
.avt-small { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; border: 2px solid #F8FAFC; }
.line-clamp-1 { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 140px; }

.tag-difficulty { padding: 6px 16px; border-radius: 100px; font-size: 0.85rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
.tag-difficulty.easy { background: #DCFCE7; color: #166534; }
.tag-difficulty.medium { background: #FEF3C7; color: #B45309; }
.tag-difficulty.hard { background: #FEE2E2; color: #991B1B; }

.action-cell { height: 60px; padding: 0; background: transparent; border: none; box-shadow: none; }
.btn-view {
  width: 100%; height: 100%; background: #0F172A; color: white; border: none;
  border-radius: 14px; font-weight: 800; cursor: pointer; transition: 0.3s;
}
.btn-view:hover { background: #EA580C; transform: translateY(-3px); box-shadow: 0 10px 20px -5px rgba(234, 88, 12, 0.4); }

/* ADD PLACEHOLDER */
.add-col { opacity: 0.7; transition: 0.3s; }
.add-col:hover { opacity: 1; }
.add-placeholder {
  height: 100%; border: 2px dashed #CBD5E1; border-radius: 20px; background: rgba(255,255,255,0.5);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  cursor: pointer; color: #64748B; gap: 16px; min-height: 400px; transition: 0.3s;
}
.add-placeholder:hover { border-color: #EA580C; color: #EA580C; background: #FFF7ED; }
.plus-circle {
  width: 56px; height: 56px; border-radius: 50%; background: #F1F5F9; color: inherit;
  display: flex; align-items: center; justify-content: center; transition: 0.3s;
}
.add-placeholder:hover .plus-circle { background: #FFEDD5; transform: scale(1.1); }

/* EMPTY STATE */
.empty-state { text-align: center; padding: 100px 0; max-width: 500px; margin: 0 auto; }
.icon-box { margin-bottom: 24px; display: flex; justify-content: center; }
.empty-state h3 { font-size: 1.6rem; font-weight: 800; margin-bottom: 12px; }
.empty-state p { color: #64748B; margin-bottom: 32px; font-size: 1.05rem; line-height: 1.6; }
.btn-primary { padding: 14px 36px; background: #EA580C; color: white; border: none; border-radius: 100px; font-weight: 800; cursor: pointer; font-size: 1.05rem; transition: 0.3s; box-shadow: 0 10px 25px -5px rgba(234, 88, 12, 0.4); }
.btn-primary:hover { background: #C2410C; transform: translateY(-2px); }

/* RESPONSIVE MOBILE */
@media (max-width: 768px) {
  .page-header h1 { font-size: 1.8rem; }
  .labels-col { display: none; } 
  .grid-col { min-width: 85vw; } 
  .compare-grid-wrapper { padding: 0 10px 20px; }
  .cell { height: auto; min-height: 56px; padding: 12px 16px; justify-content: space-between; flex-direction: row; }
  .cell.header-cell { flex-direction: column; align-items: flex-start; justify-content: flex-start; }
  
  /* Bật nhãn giả trên Mobile */
  .cell[data-label]::before { content: attr(data-label); font-weight: 700; color: #94A3B8; font-size: 0.9rem; display: block; } 
  .stat-box { margin-left: auto; }
  .win-badge { position: relative; top: auto; right: auto; margin-left: 8px; }
}
</style>