<template>
  <div class="page-container">
    <div class="header-flex">
      <div>
        <h2 class="page-title">📈 Thống Kê & Phân Tích</h2>
        <p class="sub-title">Dữ liệu tăng trưởng trong tháng 02/2026</p>
      </div>
      <div class="date-filter">
        <select>
          <option>7 ngày qua</option>
          <option>Tháng này</option>
          <option>Năm nay</option>
        </select>
      </div>
    </div>

    <div class="chart-section">
      <div class="chart-header">
        <h3>Doanh Thu & Lượt Truy Cập</h3>
        <div class="legend">
          <span class="dot blue"></span> Lượt xem
          <span class="dot orange"></span> Doanh thu (Triệu đ)
        </div>
      </div>
      
      <div class="bar-chart-container">
        <div class="chart-grid">
          <div class="grid-line"><span>100</span></div>
          <div class="grid-line"><span>75</span></div>
          <div class="grid-line"><span>50</span></div>
          <div class="grid-line"><span>25</span></div>
          <div class="grid-line"><span>0</span></div>
        </div>
        
        <div class="bars-wrapper">
          <div class="bar-group" v-for="day in chartData" :key="day.label">
            <div class="bars">
              <div class="bar blue" :style="{ height: day.views + '%' }" :title="'Views: ' + day.views"></div>
              <div class="bar orange" :style="{ height: day.revenue + '%' }" :title="'Doanh thu: ' + day.revenue"></div>
            </div>
            <span class="label">{{ day.label }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="stats-row">
      
      <div class="panel">
        <div class="panel-head">
          <h3>🔥 Top Món Ăn Yêu Thích</h3>
        </div>
        <div class="top-list">
          <div class="top-item" v-for="(item, index) in topRecipes" :key="index">
            <span class="rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
            <img :src="item.image" class="thumb">
            <div class="info">
              <h4>{{ item.name }}</h4>
              <small>{{ item.author }}</small>
            </div>
            <div class="score">
              {{ item.likes }} ❤️
            </div>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-head">
          <h3>📂 Phân Bổ Danh Mục</h3>
        </div>
        <div class="category-stats">
          <div class="cat-stat-row" v-for="cat in categories" :key="cat.name">
            <div class="cat-info">
              <span>{{ cat.name }}</span>
              <span>{{ cat.percent }}%</span>
            </div>
            <div class="progress-bar-bg">
              <div class="progress-bar-fill" :style="{ width: cat.percent + '%', background: cat.color }"></div>
            </div>
            <small>{{ cat.count }} bài viết</small>
          </div>
        </div>
      </div>

    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'

// Dữ liệu biểu đồ (Thứ 2 -> Chủ Nhật)
const chartData = ref([
  { label: 'T2', views: 40, revenue: 25 },
  { label: 'T3', views: 60, revenue: 45 },
  { label: 'T4', views: 35, revenue: 30 },
  { label: 'T5', views: 80, revenue: 65 },
  { label: 'T6', views: 55, revenue: 50 },
  { label: 'T7', views: 90, revenue: 85 },
  { label: 'CN', views: 70, revenue: 60 },
])

// Top món ăn
const topRecipes = ref([
  { name: 'Sườn Xào Chua Ngọt', author: 'Rose Truong', likes: 1250, image: 'https://images.unsplash.com/photo-1544025162-d76694265947?w=100&q=80' },
  { name: 'Canh Chua Cá Lóc', author: 'Bếp Mẹ Nấu', likes: 980, image: 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=100&q=80' },
  { name: 'Salad Cầu Vồng', author: 'Eat Clean', likes: 850, image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=100&q=80' },
])

// Danh mục
const categories = ref([
  { name: 'Món Ngon', percent: 45, count: 120, color: '#F97316' },
  { name: 'Làm Bánh', percent: 30, count: 85, color: '#3B82F6' },
  { name: 'Healthy', percent: 15, count: 40, color: '#10B981' },
  { name: 'Đồ Uống', percent: 10, count: 25, color: '#8B5CF6' },
])
</script>

<style scoped>
.page-container { padding: 25px; font-family: 'Quicksand', sans-serif; color: #334155; }
.header-flex { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 30px; }
.page-title { font-size: 1.8rem; font-weight: 800; color: #1E293B; margin: 0; }
.sub-title { color: #64748B; margin: 5px 0 0; }
.date-filter select { padding: 10px 15px; border-radius: 8px; border: 1px solid #CBD5E1; font-family: inherit; cursor: pointer; }

/* CHART SECTION */
.chart-section { background: white; padding: 25px; border-radius: 16px; box-shadow: 0 4px 10px rgba(0,0,0,0.03); margin-bottom: 30px; }
.chart-header { display: flex; justify-content: space-between; margin-bottom: 20px; }
.chart-header h3 { margin: 0; color: #1E293B; }
.legend { display: flex; gap: 15px; font-size: 0.85rem; }
.dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; margin-right: 5px; }
.dot.blue { background: #3B82F6; }
.dot.orange { background: #F97316; }

/* CSS BAR CHART */
.bar-chart-container { position: relative; height: 300px; padding-left: 30px; padding-bottom: 30px; }
.chart-grid { position: absolute; inset: 0 0 30px 30px; display: flex; flex-direction: column; justify-content: space-between; z-index: 1; }
.grid-line { border-bottom: 1px dashed #E2E8F0; width: 100%; height: 1px; position: relative; }
.grid-line span { position: absolute; left: -30px; top: -8px; font-size: 0.75rem; color: #94A3B8; }

.bars-wrapper { position: absolute; inset: 0 0 30px 30px; display: flex; justify-content: space-around; align-items: flex-end; z-index: 2; padding: 0 20px; }
.bar-group { display: flex; flex-direction: column; align-items: center; height: 100%; justify-content: flex-end; width: 40px; }
.bars { display: flex; gap: 4px; align-items: flex-end; height: 100%; width: 100%; }
.bar { width: 50%; border-radius: 4px 4px 0 0; transition: height 1s ease; }
.bar.blue { background: #3B82F6; opacity: 0.8; }
.bar.orange { background: #F97316; }
.label { margin-top: 10px; font-size: 0.85rem; font-weight: 600; color: #64748B; }

/* STATS ROW */
.stats-row { display: grid; grid-template-columns: 1.5fr 1fr; gap: 30px; }
.panel { background: white; border-radius: 16px; box-shadow: 0 4px 10px rgba(0,0,0,0.03); padding: 20px; }
.panel-head h3 { margin: 0 0 20px 0; font-size: 1.1rem; color: #1E293B; }

/* Top List */
.top-item { display: flex; align-items: center; gap: 15px; margin-bottom: 15px; border-bottom: 1px dashed #F1F5F9; padding-bottom: 10px; }
.rank { width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; font-weight: 800; border-radius: 50%; font-size: 0.8rem; background: #F1F5F9; color: #64748B; }
.rank-1 { background: #FFEDD5; color: #C2410C; } /* Vàng cam */
.rank-2 { background: #E0F2FE; color: #0369A1; } /* Xanh */
.rank-3 { background: #F1F5F9; color: #475569; }
.thumb { width: 40px; height: 40px; border-radius: 8px; object-fit: cover; }
.info { flex: 1; }
.info h4 { margin: 0; font-size: 0.9rem; }
.info small { font-size: 0.75rem; color: #94A3B8; }
.score { font-weight: 700; font-size: 0.9rem; }

/* Category Stats */
.cat-stat-row { margin-bottom: 20px; }
.cat-info { display: flex; justify-content: space-between; font-weight: 600; font-size: 0.9rem; margin-bottom: 5px; }
.progress-bar-bg { background: #F1F5F9; height: 8px; border-radius: 10px; overflow: hidden; }
.progress-bar-fill { height: 100%; border-radius: 10px; transition: width 1s ease; }
.cat-stat-row small { font-size: 0.75rem; color: #94A3B8; margin-top: 4px; display: block; }

@media (max-width: 1024px) {
  .stats-row { grid-template-columns: 1fr; }
}
</style>