<template>
  <div class="events-page">
    
    <section class="events-hero">
      <div class="hero-content">
        <span class="hero-tag">Sự kiện nổi bật</span>
        <h1>Workshop: Nghệ Thuật Bánh Ngọt Pháp</h1>
        <p>Học hỏi bí quyết từ các chuyên gia hàng đầu và giao lưu với cộng đồng yêu bánh.</p>
        <div class="hero-meta">
          <span>📅 25 Tháng 10</span>
          <span>📍 Hà Nội</span>
        </div>
        <button class="btn-hero-join">Đăng ký tham gia ngay</button>
      </div>
      <div class="hero-overlay"></div>
    </section>

    <div class="events-toolbar">
      <div class="tabs-group">
        <button 
          v-for="tab in filters" :key="tab.id"
          class="filter-tab" 
          :class="{ active: currentFilter === tab.id }"
          @click="currentFilter = tab.id"
        >
          {{ tab.label }}
        </button>
      </div>

      <div class="search-wrap">
        <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
        <input type="text" placeholder="Tìm sự kiện...">
      </div>
    </div>

    <div class="events-grid">
      <EventCard 
        v-for="evt in filteredEvents" 
        :key="evt.id" 
        :event="evt" 
      />
    </div>

    <div class="load-more">
      <button class="btn-outline">Xem thêm sự kiện</button>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import EventCard from '@/components/common/EventCard.vue'

// --- DỮ LIỆU MẪU ---
const currentFilter = ref('all')
const filters = [
  { id: 'all', label: 'Tất cả' },
  { id: 'upcoming', label: 'Sắp diễn ra' },
  { id: 'workshop', label: 'Workshop' },
  { id: 'contest', label: 'Cuộc thi' },
]

const events = ref([
  { 
    id: 1, title: 'Workshop Làm Bánh Trung Thu Hiện Đại', 
    image: 'https://images.unsplash.com/photo-1529383568366-2244276722d3?w=800&auto=format&fit=crop',
    month: 'TH8', day: '15', time: '09:00 AM - 12:00 PM', location: 'Quận 1, TP.HCM',
    type: 'offline', typeLabel: 'Offline', isJoined: false,
    attendees: ['https://i.pravatar.cc/150?u=1', 'https://i.pravatar.cc/150?u=2', 'https://i.pravatar.cc/150?u=3'],
    totalAttendees: 45, category: 'workshop'
  },
  { 
    id: 2, title: 'Cuộc Thi: Master Chef Tại Gia Mùa 3', 
    image: 'https://images.unsplash.com/photo-1556910103-1c02745a30bf?w=800&auto=format&fit=crop',
    month: 'TH9', day: '02', time: 'Cả ngày', location: 'Online qua Zoom',
    type: 'online', typeLabel: 'Online', isJoined: true,
    attendees: ['https://i.pravatar.cc/150?u=4', 'https://i.pravatar.cc/150?u=5', 'https://i.pravatar.cc/150?u=6', 'https://i.pravatar.cc/150?u=7'],
    totalAttendees: 120, category: 'contest'
  },
  { 
    id: 3, title: 'Coffee Talk: Xu Hướng Ẩm Thực 2024', 
    image: 'https://images.unsplash.com/photo-1511920170033-f8396924c348?w=800&auto=format&fit=crop',
    month: 'TH9', day: '10', time: '14:00 - 16:00', location: 'Cầu Giấy, Hà Nội',
    type: 'offline', typeLabel: 'Offline', isJoined: false,
    attendees: ['https://i.pravatar.cc/150?u=8', 'https://i.pravatar.cc/150?u=9'],
    totalAttendees: 28, category: 'meetup'
  },
  { 
    id: 4, title: 'Lớp Học Nấu Ăn Chay Vì Sức Khỏe', 
    image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800&auto=format&fit=crop',
    month: 'TH9', day: '25', time: '18:00 - 20:00', location: 'Đà Nẵng',
    type: 'offline', typeLabel: 'Offline', isJoined: false,
    attendees: ['https://i.pravatar.cc/150?u=10'],
    totalAttendees: 15, category: 'workshop'
  },
])

// Filter Logic
const filteredEvents = computed(() => {
  if (currentFilter.value === 'all') return events.value
  if (currentFilter.value === 'upcoming') return events.value // Giả lập
  return events.value.filter(e => e.category === currentFilter.value)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700&family=Mulish:wght@400;600;700;800&display=swap');

.events-page {
  font-family: 'Mulish', sans-serif; color: #1C1917;
  padding-bottom: 60px;
}

/* --- 1. HERO BANNER --- */
.events-hero {
  position: relative; height: 350px; border-radius: 24px; overflow: hidden;
  background-image: url('https://images.unsplash.com/photo-1556761175-5973dc0f32e7?q=80&w=2032&auto=format&fit=crop');
  background-size: cover; background-position: center;
  display: flex; align-items: center; padding: 40px 60px;
  box-shadow: 0 10px 30px -10px rgba(0,0,0,0.2);
  margin-bottom: 40px;
}
.hero-overlay {
  position: absolute; inset: 0;
  background: linear-gradient(to right, rgba(0,0,0,0.8), rgba(0,0,0,0.3));
}
.hero-content { position: relative; z-index: 2; max-width: 600px; color: white; }
.hero-tag { background: #EA580C; color: white; font-weight: 800; font-size: 0.75rem; padding: 4px 12px; border-radius: 20px; text-transform: uppercase; letter-spacing: 1px; }
.hero-content h1 { font-family: 'Playfair Display', serif; font-size: 2.8rem; line-height: 1.2; margin: 15px 0; }
.hero-content p { font-size: 1.1rem; opacity: 0.9; margin-bottom: 25px; line-height: 1.6; }
.hero-meta { display: flex; gap: 20px; font-weight: 700; margin-bottom: 25px; font-size: 1rem; }
.btn-hero-join {
  background: white; color: #1C1917; border: none; padding: 12px 30px;
  border-radius: 30px; font-weight: 800; cursor: pointer; transition: 0.2s;
  box-shadow: 0 4px 15px rgba(255,255,255,0.2);
}
.btn-hero-join:hover { background: #EA580C; color: white; }

/* --- 2. TOOLBAR --- */
.events-toolbar {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 30px; flex-wrap: wrap; gap: 20px;
}
.tabs-group {
  display: flex; gap: 10px; background: #E5E5E5; padding: 4px; border-radius: 12px;
}
.filter-tab {
  background: transparent; border: none; padding: 8px 20px; border-radius: 8px;
  font-weight: 700; color: #57534E; cursor: pointer; transition: 0.2s;
}
.filter-tab.active { background: white; color: #1C1917; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }

.search-wrap {
  position: relative; display: flex; align-items: center;
  background: white; border: 1px solid #E5E5E5; border-radius: 30px; padding: 8px 15px; width: 300px;
}
.search-wrap .icon { color: #A8A29E; margin-right: 10px; }
.search-wrap input { border: none; outline: none; width: 100%; font-size: 0.95rem; }

/* --- 3. GRID --- */
.events-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 30px;
}

/* --- 4. LOAD MORE --- */
.load-more { text-align: center; margin-top: 50px; }
.btn-outline {
  background: transparent; border: 2px solid #D6D3D1; color: #57534E;
  padding: 12px 30px; border-radius: 30px; font-weight: 700; cursor: pointer; transition: 0.2s;
}
.btn-outline:hover { border-color: #1C1917; color: #1C1917; }

/* Responsive */
@media (max-width: 768px) {
  .events-hero { padding: 30px; height: auto; min-height: 300px; }
  .hero-content h1 { font-size: 2rem; }
  .events-toolbar { flex-direction: column; align-items: flex-start; }
  .tabs-group, .search-wrap { width: 100%; }
}
</style>