<template>
  <div class="event-card" tabindex="0" :aria-label="event.title" @click="goToDetail" @keydown.enter.prevent="goToDetail">
    
    <div class="card-image">
      <img :src="event.image" :alt="event.title" loading="lazy">
      <div class="image-overlay"></div> <div class="date-calendar">
        <span class="month">{{ event.month }}</span>
        <span class="day">{{ event.day }}</span>
      </div>

      <div class="countdown-badge glass-effect" :class="event.category">
        <svg v-if="event.category !== 'ended'" class="icon-svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M5 22h14"></path><path d="M5 2h14"></path><path d="M17 22v-4.172a2 2 0 0 0-.586-1.414L12 12l-4.414 4.414A2 2 0 0 0 7 17.828V22"></path><path d="M7 2v4.172a2 2 0 0 0 .586 1.414L12 12l4.414-4.414A2 2 0 0 0 17 6.172V2"></path>
        </svg>
        <svg v-else class="icon-svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline>
        </svg>
        <span>{{ timeLeftText }}</span>
      </div>
    </div>

    <div class="card-body">
      <h3 class="event-title" :title="event.title">{{ event.title }}</h3>
      
      <div class="event-meta">
        <div class="meta-row">
          <svg class="icon-svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
          <span>Thời gian: <strong>{{ event.time }}</strong></span>
        </div>
        <div class="meta-row">
          <svg class="icon-svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
          <span>Đã có <strong>{{ event.totalAttendees }}</strong> bài dự thi</span>
        </div>
      </div>

      <div class="card-footer">
        <div class="status-indicator">
          <span class="pulse-dot" :class="event.category"></span>
          <span class="text">{{ statusText }}</span>
        </div>
        
        <button v-if="event.category !== 'ended'" class="btn-action btn-join" :class="{ joined: event.isJoined }" @click.stop="handleJoin">
          <span>{{ event.isJoined ? 'Đã tham gia' : 'Tham gia' }}</span>
          <svg v-if="!event.isJoined" class="arrow-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
        </button>
        
        <button v-else class="btn-action btn-view" @click.stop="goToDetail">
          <span>Xem kết quả</span>
          <svg class="arrow-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
        </button>
      </div>
    </div>
    
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({ event: Object })
const router = useRouter()

const timeLeftText = computed(() => {
  if (props.event.category === 'ended') return 'Đã kết thúc'
  if (props.event.category === 'upcoming') return 'Sắp mở cổng'
  if (!props.event.rawEndAt) return 'Đang diễn ra'

  const end = new Date(props.event.rawEndAt)
  const now = new Date()
  const diffTime = Math.abs(end - now)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  return `Còn lại ${diffDays} ngày`
})

const statusText = computed(() => {
  if (props.event.category === 'active') return 'Đang diễn ra'
  if (props.event.category === 'upcoming') return 'Sắp diễn ra'
  return 'Đã kết thúc'
})

const goToDetail = () => {
  if (props.event?.id) router.push({ name: 'EventDetail', params: { id: props.event.id } })
}

const handleJoin = () => console.log('Toggle join event:', props.event.id)
</script>

<style scoped>
/* ─── Card Container ─── */
.event-card {
  background: #FFFFFF;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
  border: 1px solid #F3F4F6;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  font-family: 'Mulish', sans-serif;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  position: relative;
}

.event-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px -10px rgba(234, 88, 12, 0.15); /* Bóng đổ ngả viền cam nhẹ */
  border-color: #FFEDD5;
}

/* ─── Image Area ─── */
.card-image {
  position: relative;
  height: 220px; /* Cho ảnh cao lên một chút nhìn cho đã */
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

.event-card:hover .card-image img {
  transform: scale(1.08) rotate(1deg); /* Zoom và nghiêng cực nhẹ */
}

.image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.6) 0%, transparent 40%);
  pointer-events: none;
}

/* ─── Calendar Badge (Đẹp như lịch thật) ─── */
.date-calendar {
  position: absolute;
  top: 16px;
  left: 16px;
  width: 50px;
  background: #FFFFFF;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

.date-calendar .month {
  width: 100%;
  background: #EA580C;
  color: #FFFFFF;
  font-size: 0.7rem;
  font-weight: 800;
  text-transform: uppercase;
  text-align: center;
  padding: 4px 0;
  letter-spacing: 1px;
}

.date-calendar .day {
  font-size: 1.3rem;
  color: #111827;
  font-weight: 900;
  padding: 4px 0 6px;
  line-height: 1;
}

/* ─── Countdown Glass Badge ─── */
.countdown-badge {
  position: absolute;
  bottom: 16px;
  right: 16px;
  padding: 6px 14px;
  border-radius: 30px;
  font-size: 0.85rem;
  font-weight: 700;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  gap: 6px;
  z-index: 10;
}

.glass-effect {
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.countdown-badge.active { background: rgba(234, 88, 12, 0.85); }
.countdown-badge.upcoming { background: rgba(2, 132, 199, 0.85); }
.countdown-badge.ended { background: rgba(75, 85, 99, 0.85); color: #F3F4F6;}

/* ─── Card Body ─── */
.card-body {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #FFFFFF;
}

.event-title {
  font-family: 'Playfair Display', serif;
  font-size: 1.3rem;
  font-weight: 800;
  color: #111827;
  margin: 0 0 16px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ─── Meta Info (Icons) ─── */
.event-meta {
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.9rem;
  color: #6B7280;
}

.meta-row strong {
  color: #111827;
  font-weight: 700;
}

.icon-svg {
  width: 16px;
  height: 16px;
  color: #9CA3AF;
}
.countdown-badge .icon-svg {
  color: #FFFFFF;
}

/* ─── Footer ─── */
.card-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px dashed #E5E7EB;
  padding-top: 16px;
}

/* Chấm trạng thái có hiệu ứng nhịp tim (Pulse) */
.status-indicator { display: flex; align-items: center; gap: 8px; }
.pulse-dot { width: 10px; height: 10px; border-radius: 50%; position: relative; }
.pulse-dot.active { background-color: #22C55E; box-shadow: 0 0 8px #22C55E; animation: pulseGreen 2s infinite; }
.pulse-dot.upcoming { background-color: #3B82F6; }
.pulse-dot.ended { background-color: #D1D5DB; }

@keyframes pulseGreen {
  0% { box-shadow: 0 0 0 0 rgba(34, 197, 94, 0.4); }
  70% { box-shadow: 0 0 0 6px rgba(34, 197, 94, 0); }
  100% { box-shadow: 0 0 0 0 rgba(34, 197, 94, 0); }
}

.status-indicator .text { font-size: 0.85rem; font-weight: 800; color: #4B5563; }

/* ─── Nút Hành Động ─── */
.btn-action {
  display: flex;
  align-items: center;
  gap: 6px;
  border: none;
  padding: 8px 18px;
  border-radius: 30px;
  font-weight: 800;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.arrow-icon {
  width: 14px; height: 14px;
  transition: transform 0.3s ease;
}

.btn-action:hover .arrow-icon {
  transform: translateX(4px); /* Hiệu ứng mũi tên bay lên */
}

/* Trạng thái nút */
.btn-join { background: #FFF7ED; color: #EA580C; }
.btn-join:hover { background: #EA580C; color: #FFFFFF; }
.btn-join.joined { background: #DCFCE7; color: #16A34A; }
.btn-join.joined:hover .arrow-icon { transform: none; }

.btn-view { background: #F3F4F6; color: #374151; }
.btn-view:hover { background: #111827; color: #FFFFFF; }

@media (max-width: 1024px) {
  .card-image { height: 200px; }
  .card-body { padding: 20px; }
  .event-title { font-size: 1.2rem; margin-bottom: 12px; }
}

@media (max-width: 768px) {
  .card-image { height: 180px; }
  
  .date-calendar { top: 12px; left: 12px; width: 45px; }
  .date-calendar .month { font-size: 0.65rem; padding: 3px 0; }
  .date-calendar .day { font-size: 1.1rem; padding: 3px 0 5px; }

  .countdown-badge { 
    bottom: 12px; right: 12px; 
    padding: 4px 12px; 
    font-size: 0.75rem; 
  }
  .countdown-badge .icon-svg { width: 14px; height: 14px; }

  .card-body { padding: 16px; }
  .event-title { font-size: 1.15rem; margin-bottom: 10px; line-height: 1.3; }
  
  .event-meta { gap: 8px; margin-bottom: 16px; }
  .meta-row { font-size: 0.85rem; }
  
  .card-footer { padding-top: 12px; flex-wrap: wrap; gap: 10px; }
  
  .status-indicator .text { font-size: 0.8rem; }
  .btn-action { padding: 6px 14px; font-size: 0.8rem; }
}

@media (max-width: 480px) {
  .event-card { border-radius: 16px; }
  .card-image { height: 160px; }
  
  .card-body { padding: 14px; }
  .event-title { font-size: 1.1rem; }
  
  .meta-row { font-size: 0.8rem; gap: 8px; }
  .meta-row .icon-svg { width: 14px; height: 14px; }

  .card-footer { flex-direction: column; align-items: flex-start; gap: 12px; }
  .btn-action { 
    width: 100%; 
    justify-content: center; 
    padding: 10px 15px; 
  }
}
</style>