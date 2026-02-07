<template>
  <div class="event-card" @click="goToDetail">
    <div class="card-image">
      <img :src="event.image" loading="lazy">
      <div class="date-badge">
        <span class="month">{{ event.month }}</span>
        <span class="day">{{ event.day }}</span>
      </div>
      <div class="type-badge" :class="event.type">
        {{ event.typeLabel }}
      </div>
    </div>

    <div class="card-body">
      <h3 class="event-title">{{ event.title }}</h3>
      
      <div class="event-meta">
        <div class="meta-row">
          <span class="icon">⏰</span> {{ event.time }}
        </div>
        <div class="meta-row">
          <span class="icon">📍</span> {{ event.location }}
        </div>
      </div>

      <div class="card-footer">
        <div class="attendees">
          <div class="avatars">
            <img v-for="(avt, i) in event.attendees" :key="i" :src="avt" class="attendee-avt">
            <div class="more-count" v-if="event.totalAttendees > 3">+{{ event.totalAttendees - 3 }}</div>
          </div>
          <span class="attend-text">sẽ tham gia</span>
        </div>
        
        <button class="btn-join" :class="{ joined: event.isJoined }" @click.stop="handleJoin">
          {{ event.isJoined ? 'Đã tham gia' : 'Tham gia' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  event: Object
})

const router = useRouter()

// Hàm chuyển hướng sang trang chi tiết
const goToDetail = () => {
  // Giả sử mỗi event có id, chuyển hướng tới /events/:id
  if (props.event?.id) {
    router.push({ name: 'EventDetail', params: { id: props.event.id } })
  }
}

// Xử lý nút tham gia nhanh (không chuyển trang)
const handleJoin = () => {
  // Logic gọi API tham gia ở đây
  console.log('Toggle join event:', props.event.id)
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;600;700;800&display=swap');

.event-card {
  background: white; border-radius: 16px; overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.04); border: 1px solid rgba(0,0,0,0.02);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  font-family: 'Mulish', sans-serif; cursor: pointer; display: flex; flex-direction: column;
}
.event-card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.1); }

.card-image { position: relative; height: 180px; overflow: hidden; }
.card-image img { width: 100%; height: 100%; object-fit: cover; transition: 0.5s; }
.event-card:hover .card-image img { transform: scale(1.1); }

/* Date Badge */
.date-badge {
  position: absolute; top: 12px; left: 12px;
  background: white; border-radius: 10px; padding: 6px 10px;
  display: flex; flex-direction: column; align-items: center;
  box-shadow: 0 4px 10px rgba(0,0,0,0.15); z-index: 2;
}
.date-badge .month { font-size: 0.7rem; color: #EA580C; font-weight: 800; text-transform: uppercase; }
.date-badge .day { font-size: 1.1rem; color: #1C1917; font-weight: 800; line-height: 1; }

.type-badge {
  position: absolute; top: 12px; right: 12px;
  padding: 4px 12px; border-radius: 20px; font-size: 0.75rem; font-weight: 700;
  color: white; backdrop-filter: blur(4px); z-index: 2;
}
.type-badge.online { background: rgba(59, 130, 246, 0.9); } /* Blue */
.type-badge.offline { background: rgba(234, 88, 12, 0.9); } /* Orange */

/* Body */
.card-body { padding: 16px; flex: 1; display: flex; flex-direction: column; }
.event-title { font-size: 1.1rem; font-weight: 700; color: #1C1917; margin: 0 0 10px; line-height: 1.4; }

.event-meta { margin-bottom: 15px; }
.meta-row { display: flex; align-items: center; gap: 8px; font-size: 0.85rem; color: #57534E; margin-bottom: 6px; }

.card-footer { margin-top: auto; display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #F5F5F4; padding-top: 12px; }

/* Attendees Stack */
.attendees { display: flex; align-items: center; gap: 8px; }
.avatars { display: flex; align-items: center; }
.attendee-avt { width: 28px; height: 28px; border-radius: 50%; border: 2px solid white; margin-left: -10px; object-fit: cover; }
.attendee-avt:first-child { margin-left: 0; }
.more-count { width: 28px; height: 28px; border-radius: 50%; background: #F3F4F6; border: 2px solid white; font-size: 0.7rem; font-weight: 700; display: flex; align-items: center; justify-content: center; margin-left: -10px; color: #6B7280; }
.attend-text { font-size: 0.75rem; color: #78716C; }

.btn-join {
  background: #F3F4F6; color: #1C1917; border: none; padding: 6px 16px;
  border-radius: 20px; font-weight: 700; font-size: 0.85rem; cursor: pointer; transition: 0.2s;
}
.btn-join:hover { background: #E5E5E5; }
.btn-join.joined { background: #DCFCE7; color: #166534; }
</style>