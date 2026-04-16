<template>
  <div class="compare-page">
    <div class="container">
      
      <div class="page-header">
        <div class="header-left">
          <button class="btn-back" @click="$router.push('/home')">
            <i class="fas fa-arrow-left"></i> Trở về
          </button>
          <h1>So sánh món ăn <i class="fas fa-balance-scale text-primary"></i></h1>
        </div>
        <p class="header-desc" v-if="compareStore.items.length > 0">
          So sánh chi tiết để tìm ra chân ái cho bữa ăn hôm nay của bạn.
        </p>
      </div>

      <div v-if="compareStore.items.length === 0" class="empty-state">
        <div class="icon-box">
          <i class="fas fa-balance-scale-left"></i>
        </div>
        <h3>Chưa có món ăn nào trên bàn cân</h3>
        <p>Bạn hãy dạo quanh và chọn ít nhất 2 tuyệt tác để so sánh nhé!</p>
        <button class="btn-primary" @click="$router.push('/home')">
          Khám Phá Ngay <i class="fas fa-compass"></i>
        </button>
      </div>

      <div v-else class="compare-grid-wrapper custom-scrollbar">
        
        <div class="grid-col labels-col">
          <div class="cell header-cell empty-label"></div> 
          <div class="cell label"><i class="fas fa-star l-icon"></i> Đánh giá</div>
          <div class="cell label"><i class="fas fa-eye l-icon"></i> Lượt xem</div>
          <div class="cell label"><i class="fas fa-heart l-icon"></i> Lượt thích</div>
          <div class="cell label"><i class="fas fa-user-chef l-icon"></i> Tác giả</div>
          <div class="cell label"><i class="fas fa-calendar-alt l-icon"></i> Ngày đăng</div>
          <div class="cell label"><i class="fas fa-clock l-icon"></i> Thời gian nấu</div>
          <div class="cell label"><i class="fas fa-fire l-icon"></i> Độ khó</div>
          <div class="cell label action-label"></div>
        </div>

        <div 
          v-for="item in compareStore.items" 
          :key="item.id" 
          class="grid-col item-col"
        >
          <div class="cell header-cell">
            <div class="img-wrap">
              <img :src="item.image || 'https://images.unsplash.com/photo-1490818387583-1b5f2223d848?w=600'" loading="lazy" :alt="item.title">
              <div class="img-overlay"></div>
              <button class="btn-remove" @click="compareStore.toggleItem(item)" title="Xóa khỏi bàn cân">
                <i class="fas fa-times"></i>
              </button>
            </div>
            <h3 class="item-title" @click="goToDetail(item.id)">{{ item.title }}</h3>
          </div>

          <div class="cell" :class="{ 'winner': isBest('rating', getRating(item)) }" data-label="Đánh giá">
            <div class="stat-box">
              <b class="text-lg">{{ getRating(item).toFixed(1) }}</b>
              <span class="text-sm text-gray">/ 5.0</span>
            </div>
            <span class="win-badge" v-if="isBest('rating', getRating(item))">
              <i class="fas fa-crown"></i> Đỉnh nhất
            </span>
          </div>

          <div class="cell" :class="{ 'winner': isBest('views', getViews(item)) }" data-label="Lượt xem">
            <div class="stat-box">
              <b>{{ formatNumber(getViews(item)) }}</b>
            </div>
            <span class="win-badge" v-if="isBest('views', getViews(item))">
              <i class="fas fa-fire-alt"></i> Hot nhất
            </span>
          </div>

          <div class="cell" :class="{ 'winner': isBest('likes', getLikes(item)) }" data-label="Lượt thích">
            <div class="stat-box">
              <b>{{ formatNumber(getLikes(item)) }}</b>
            </div>
            <span class="win-badge" v-if="isBest('likes', getLikes(item))">
              <i class="fas fa-heart"></i> Thích nhất
            </span>
          </div>

          <div class="cell" data-label="Tác giả">
            <div class="author-row" @click="$router.push(`/profile/${item.author?.id || ''}`)">
              <img :src="item.author?.avatar || 'https://ui-avatars.com/api/?name=' + (item.author?.name || 'G') + '&background=EA580C&color=fff'" class="avt-small">
              <span class="line-clamp-1 author-name">{{ item.author?.name || 'Đầu bếp GoMet' }}</span>
            </div>
          </div>

          <div class="cell" :class="{ 'winner': isBest('date', getDateTs(item)) }" data-label="Ngày đăng">
            <span class="value">{{ formatDate(item) }}</span>
            <span class="win-badge" v-if="isBest('date', getDateTs(item))">Mới nhất</span>
          </div>

          <div class="cell" :class="{ 'winner': isBest('time', parseTime(item.time)) }" data-label="Thời gian nấu">
            <span class="value">{{ item.time || '30 phút' }}</span>
            <span class="win-badge" v-if="isBest('time', parseTime(item.time))">
              <i class="fas fa-bolt"></i> Nhanh nhất
            </span>
          </div>

          <div class="cell" :class="{ 'winner': isBest('difficulty', getDifficultyWeight(item.difficulty)) }" data-label="Độ khó">
            <span class="tag-difficulty" :class="getDifficultyClass(item.difficulty)">
              {{ formatDifficulty(item.difficulty) }}
            </span>
            <span class="win-badge" v-if="isBest('difficulty', getDifficultyWeight(item.difficulty))">Dễ nhất</span>
          </div>

          <div class="cell action-cell">
            <button class="btn-view-recipe" @click="goToDetail(item.id)">
              Xem Ngay <i class="fas fa-arrow-right"></i>
            </button>
          </div>
        </div>

        <div v-if="compareStore.items.length < 3" class="grid-col add-col">
          <div class="add-placeholder" @click="$router.push('/search')">
            <div class="plus-circle">
              <i class="fas fa-plus"></i>
            </div>
            <span>Thêm món ăn khác</span>
            <small>Tối đa 3 món</small>
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

// --- DATA HELPERS ---
const formatNumber = (num) => {
  if (!num) return 0;
  if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
  if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
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
    day: '2-digit', month: '2-digit', year: 'numeric'
  });
}

const parseTime = (t) => {
  if (!t) return 999;
  if (typeof t === 'number') return t;
  const str = String(t).toLowerCase();
  if (str.includes('h')) return parseInt(str) * 60 + (parseInt(str.split('h')[1]) || 0);
  return parseInt(str);
}

const formatDifficulty = (diff) => {
  const d = String(diff || '').toLowerCase();
  if (d.includes('easy') || d.includes('dễ') || d === '1') return 'Dễ dàng';
  if (d.includes('hard') || d.includes('khó') || d === '3') return 'Thử thách';
  return 'Trung bình';
}

const getDifficultyWeight = (diff) => {
  const d = String(diff || '').toLowerCase();
  if (d.includes('easy') || d.includes('dễ') || d === '1') return 1;
  if (d.includes('hard') || d.includes('khó') || d === '3') return 3;
  return 2; // Medium
}

const getDifficultyClass = (diff) => {
  const w = getDifficultyWeight(diff);
  if (w === 1) return 'easy';
  if (w === 3) return 'hard';
  return 'medium';
}

// --- LOGIC TÌM KẺ CHIẾN THẮNG ---
const isBest = (criteria, value) => {
  if (compareStore.items.length < 2) return false; 

  const items = compareStore.items;

  if (criteria === 'rating') {
    const max = Math.max(...items.map(i => getRating(i)));
    return max > 0 && value === max;
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

<style scoped lang="scss">
/* --- BIẾN MÀU SẮC ĐỒNG BỘ GOMET --- */
$primary: #EA580C;         /* Cam chủ đạo */
$primary-light: #FFF7ED;   /* Nền cam rất nhạt */
$primary-hover: #C2410C;   /* Cam đậm khi hover */
$text-main: #0F172A;
$text-muted: #64748B;
$border-color: #E2E8F0;
$bg-main: #FAFAF9;

.compare-page {
  font-family: 'Mulish', sans-serif; 
  padding: 40px 0 80px; 
  min-height: 100vh;
  background-color: $bg-main; 
  color: $text-main;
}
.container { max-width: 1250px; margin: 0 auto; padding: 0 20px; }

/* ================== HEADER ================== */
.page-header { 
  display: flex; flex-direction: column; gap: 10px; margin-bottom: 40px; 
  border-bottom: 2px dashed $border-color; padding-bottom: 20px;
}
.header-left { display: flex; flex-direction: column; align-items: flex-start; gap: 15px;}
.page-header h1 { font-size: 2.5rem; font-family: 'Playfair Display', serif; font-weight: 900; color: $text-main; margin: 0; letter-spacing: -0.5px;}
.text-primary { color: $primary; }
.header-desc { color: $text-muted; font-size: 1.05rem; margin: 0;}

.btn-back { 
  background: transparent; border: 1px solid $border-color; padding: 8px 20px; border-radius: 30px;
  font-weight: 700; color: $text-muted; cursor: pointer; font-size: 0.9rem; transition: 0.3s ease; 
  display: flex; align-items: center; gap: 8px;
  &:hover { color: $primary; border-color: $primary; transform: translateX(-5px); background: #fff; box-shadow: 0 5px 15px rgba($primary, 0.1);}
}

/* ================== TRẠNG THÁI TRỐNG ================== */
.empty-state { 
  text-align: center; padding: 80px 20px; max-width: 500px; margin: 0 auto; 
  background: #fff; border-radius: 24px; box-shadow: 0 10px 40px rgba(0,0,0,0.03); border: 1px solid $border-color;
}
.icon-box { 
  margin-bottom: 25px; display: flex; justify-content: center; 
  i { font-size: 4rem; color: #CBD5E1; }
}
.empty-state h3 { font-size: 1.8rem; font-family: 'Playfair Display', serif; font-weight: 900; margin-bottom: 15px; color: $text-main;}
.empty-state p { color: $text-muted; margin-bottom: 35px; font-size: 1.05rem; line-height: 1.6; }

.btn-primary { 
  padding: 16px 40px; background: $primary; color: white; border: none; border-radius: 50px; 
  font-weight: 800; cursor: pointer; font-size: 1rem; transition: 0.3s; 
  display: inline-flex; align-items: center; gap: 10px; text-transform: uppercase; letter-spacing: 1px;
  box-shadow: 0 10px 25px rgba($primary, 0.3); 
  &:hover { background: $primary-hover; transform: translateY(-3px); box-shadow: 0 15px 35px rgba($primary, 0.4);}
}

/* ================== GRID BẢNG SO SÁNH ================== */
.compare-grid-wrapper {
  display: flex; gap: 20px; overflow-x: auto; padding-bottom: 30px; padding-top: 10px; scroll-snap-type: x mandatory;
}
.custom-scrollbar::-webkit-scrollbar { height: 8px; }
.custom-scrollbar::-webkit-scrollbar-track { background: #F1F5F9; border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; border: 2px solid #F1F5F9;}
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94A3B8; }

.grid-col { flex: 1; min-width: 300px; display: flex; flex-direction: column; gap: 12px; scroll-snap-align: start; }

/* --- CỘT TIÊU CHÍ (LABELS) --- */
.labels-col { flex: 0 0 200px; min-width: 200px; }
.labels-col .header-cell { background: transparent; border: none; box-shadow: none; min-height: 280px; }
.labels-col .cell.label {
  justify-content: flex-start; font-weight: 800; color: #475569; 
  background: transparent; border: none; padding-left: 10px; font-size: 0.95rem; text-transform: uppercase; letter-spacing: 0.5px;
}
.l-icon { margin-right: 12px; font-size: 1.1rem; width: 20px; text-align: center; color: $primary; } /* ĐỒNG BỘ ICON MÀU CAM */
.action-label { height: 70px !important; }

/* --- CÁC Ô DỮ LIỆU --- */
.cell {
  background: #FFFFFF; border-radius: 16px; padding: 0 20px; height: 64px;
  display: flex; align-items: center; justify-content: center;
  border: 1px solid $border-color; color: $text-main; font-weight: 600; font-size: 1.05rem;
  position: relative; transition: all 0.3s ease; box-shadow: 0 4px 6px rgba(0,0,0,0.02);
}

/* 🌟 HIỆU ỨNG Ô CHIẾN THẮNG (TÔNG CAM BRANDING) */
.cell.winner { 
  background: $primary-light; border-color: $primary; color: $primary-hover; 
  box-shadow: 0 5px 20px rgba($primary, 0.15); transform: scale(1.02); z-index: 5; 
}
.win-badge {
  position: absolute; top: -12px; right: 15px; 
  background: linear-gradient(135deg, $primary, #F97316); color: white;
  font-size: 0.7rem; padding: 4px 12px; border-radius: 20px; font-weight: 800; letter-spacing: 0.5px; text-transform: uppercase;
  box-shadow: 0 4px 12px rgba($primary, 0.4); border: 2px solid white; display: flex; align-items: center; gap: 4px;
  animation: popBadge 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
@keyframes popBadge { 0% { transform: scale(0) translateY(10px); opacity: 0;} 100% { transform: scale(1) translateY(0); opacity: 1;} }

/* --- HEADER CELL (ẢNH VÀ TÊN MÓN) --- */
.header-cell { height: auto; min-height: 280px; flex-direction: column; align-items: center; padding: 20px; position: relative; background: #fff;}
.img-wrap { 
  width: 100%; height: 180px; border-radius: 16px; overflow: hidden; position: relative; margin-bottom: 16px; 
  box-shadow: 0 8px 20px rgba(0,0,0,0.08); background: #eee;
  &:hover .img-overlay { opacity: 1; }
  &:hover img { transform: scale(1.05); }
}
.img-wrap img { width: 100%; height: 100%; object-fit: cover; transition: 0.5s ease; }
.img-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba($primary, 0.4), transparent); opacity: 0; transition: 0.3s; pointer-events: none;}

.btn-remove { 
  position: absolute; top: 12px; right: 12px; width: 36px; height: 36px; 
  background: rgba(255, 255, 255, 0.9); color: #EF4444; border: none; border-radius: 50%; 
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); font-size: 1.1rem; box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  &:hover { background: #EF4444; color: white; transform: scale(1.1) rotate(90deg); }
}

.item-title { 
  margin: 0; font-size: 1.2rem; line-height: 1.4; font-weight: 800; cursor: pointer; transition: 0.2s; text-align: center;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; font-family: 'Playfair Display', serif;
  &:hover { color: $primary; }
}

/* --- SPECIFIC CELLS --- */
.stat-box { display: flex; align-items: baseline; gap: 4px; }
.text-lg { font-size: 1.3rem; }
.text-sm { font-size: 0.85rem; }
.text-gray { color: #94A3B8; }

/* Tác giả */
.author-row { display: flex; align-items: center; gap: 10px; width: 100%; justify-content: center; cursor: pointer; transition: 0.2s;}
.author-row:hover .author-name { color: $primary; }
.avt-small { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; border: 2px solid #F1F5F9; box-shadow: 0 2px 5px rgba(0,0,0,0.1);}
.line-clamp-1 { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 150px; font-weight: 700;}

/* Độ Khó (Đã cập nhật sắc độ Cam) */
.tag-difficulty { padding: 8px 20px; border-radius: 100px; font-size: 0.85rem; font-weight: 800; text-transform: uppercase; letter-spacing: 0.5px; }
.tag-difficulty.easy { background: #FFEDD5; color: #C2410C; } /* Cam nhạt */
.tag-difficulty.medium { background: #FDBA74; color: #9A3412; } /* Cam vừa */
.tag-difficulty.hard { background: $primary; color: #FFF; } /* Cam đậm */

/* Nút Xem Công Thức */
.action-cell { height: 70px; padding: 0; background: transparent; border: none; box-shadow: none; }
.btn-view-recipe {
  width: 100%; height: 100%; background: $text-main; color: white; border: none;
  border-radius: 16px; font-weight: 800; cursor: pointer; transition: 0.3s; font-size: 1rem;
  display: flex; justify-content: center; align-items: center; gap: 8px; text-transform: uppercase; letter-spacing: 1px;
  &:hover { background: $primary; transform: translateY(-3px); box-shadow: 0 10px 25px rgba($primary, 0.4); gap: 12px;}
}

/* --- ADD PLACEHOLDER (CỘT THÊM MÓN) --- */
.add-col { opacity: 0.6; transition: 0.3s; }
.add-col:hover { opacity: 1; }
.add-placeholder {
  height: 100%; border: 2px dashed #CBD5E1; border-radius: 24px; background: rgba(255,255,255,0.4);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  cursor: pointer; color: #64748B; gap: 15px; min-height: 400px; transition: 0.4s ease;
  span { font-weight: 800; font-size: 1.1rem; color: $text-main;}
  small { font-weight: 600; color: #94A3B8; text-transform: uppercase; letter-spacing: 1px;}
  
  &:hover { border-color: $primary; background: $primary-light; span {color: $primary;} }
}
.plus-circle {
  width: 64px; height: 64px; border-radius: 50%; background: #F1F5F9; color: #94A3B8; font-size: 1.5rem;
  display: flex; align-items: center; justify-content: center; transition: 0.4s; box-shadow: 0 5px 15px rgba(0,0,0,0.05);
}
.add-placeholder:hover .plus-circle { background: $primary; color: white; transform: scale(1.1) rotate(90deg); box-shadow: 0 10px 20px rgba($primary, 0.3);}


/* ================== RESPONSIVE ================== */
@media (max-width: 1024px) {
  .labels-col { flex: 0 0 160px; min-width: 160px; }
  .grid-col { min-width: 260px; }
}

@media (max-width: 768px) {
  .page-header { flex-direction: row; justify-content: space-between; align-items: center; border-bottom: none; margin-bottom: 20px;}
  .header-left { gap: 10px; }
  .page-header h1 { font-size: 2rem; }
  .header-desc { display: none; } 
  
  .labels-col { display: none; } 
  .grid-col { min-width: 85vw; gap: 16px;} 
  .compare-grid-wrapper { padding: 0 5px 30px; gap: 15px;}
  
  .cell { 
    height: auto; min-height: 60px; padding: 15px 20px; 
    justify-content: space-between; flex-direction: row; 
    border-radius: 12px;
  }
  .cell.header-cell { flex-direction: column; align-items: flex-start; justify-content: flex-start; background: transparent; padding: 0; border: none; box-shadow: none;}
  
  .cell[data-label]::before { 
    content: attr(data-label); 
    font-weight: 800; color: #64748B; font-size: 0.9rem; 
    display: block; text-transform: uppercase; letter-spacing: 0.5px;
  } 
  
  .stat-box, .value, .tag-difficulty, .author-row { margin-left: auto; justify-content: flex-end;}
  
  .win-badge { position: relative; top: auto; right: auto; margin-left: 10px; transform: none; animation: none;}
  .cell.winner { transform: none; border-width: 2px;}
  .action-cell { padding: 0; height: 60px; background: transparent; border: none; box-shadow: none;}
  .btn-view-recipe { border-radius: 12px; }
}

@media (max-width: 480px) {
  .page-header h1 { font-size: 1.6rem; }
  .grid-col { min-width: 90vw; }
  .cell { padding: 12px 15px; font-size: 0.95rem; }
  .cell[data-label]::before { font-size: 0.8rem; }
  .win-badge { padding: 4px 8px; font-size: 0.6rem; margin-left: 5px; i {display: none;} }
  .tag-difficulty { padding: 6px 12px; font-size: 0.75rem; }
}
</style>