<template>
  <div class="event-card" tabindex="0" :aria-label="event.title" @click="goToDetail" @keydown.enter.prevent="goToDetail">
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
/* ─── Card Container ─── */
.event-card {
  background: var(--color-neutral-0);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border: 1px solid rgba(0, 0, 0, 0.02);
  transition: transform var(--duration-normal) var(--ease-out),
              box-shadow var(--duration-normal) var(--ease-out);
  font-family: var(--font-ui);
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.event-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-xl);
}

/* Active press feedback */
.event-card:active {
  transform: translateY(-1px) scale(0.98);
  box-shadow: var(--shadow-md);
  transition: transform var(--duration-instant) var(--ease-out),
              box-shadow var(--duration-instant) var(--ease-out);
}

/* Keyboard focus ring */
.event-card:focus-visible {
  outline: 2px solid var(--color-primary-500);
  outline-offset: 3px;
}

/* ─── SKELETON STATE ─── */
.event-card.is-skeleton {
  pointer-events: none;
}

.event-card.is-skeleton .card-image,
.event-card.is-skeleton .event-title,
.event-card.is-skeleton .meta-row,
.event-card.is-skeleton .attendance,
.event-card.is-skeleton .btn-join {
  background: linear-gradient(
    90deg,
    var(--color-neutral-200) 25%,
    var(--color-neutral-100) 50%,
    var(--color-neutral-200) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.4s var(--ease-in-out) infinite;
  border-radius: var(--radius-sm);
  color: transparent !important;
}

@keyframes shimmer {
  0%   { background-position: -200% 0; }
  100% { background-position:  200% 0; }
}

/* ─── Image ─── */
.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--duration-slower) var(--ease-out);
}

.event-card:hover .card-image img {
  transform: scale(1.08);
}

/* ─── Date Badge ─── */
.date-badge {
  position: absolute;
  top: var(--space-3);
  left: var(--space-3);
  background: var(--color-neutral-0);
  border-radius: var(--radius-md);
  padding: var(--space-1) var(--space-3);
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: var(--shadow-md);
  z-index: var(--z-base);
}

.date-badge .month {
  font-size: var(--text-xs);
  color: var(--color-primary-600);
  font-weight: var(--font-extrabold);
  text-transform: uppercase;
}

.date-badge .day {
  font-size: var(--text-lg);
  color: var(--color-neutral-900);
  font-weight: var(--font-extrabold);
  line-height: var(--leading-none);
}

/* ─── Type Badge ─── */
.type-badge {
  position: absolute;
  top: var(--space-3);
  right: var(--space-3);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  color: var(--color-neutral-0);
  backdrop-filter: blur(4px);
  z-index: var(--z-base);
}

.type-badge.online { background: rgba(59, 130, 246, 0.9); }
.type-badge.offline { background: rgba(234, 88, 12, 0.9); }

/* ─── Card Body ─── */
.card-body {
  padding: var(--space-4);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.event-title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--color-neutral-900);
  margin: 0 0 var(--space-3);
  line-height: var(--leading-snug);
}

/* ─── Meta ─── */
.event-meta { margin-bottom: var(--space-4); }

.meta-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--color-neutral-600);
  margin-bottom: var(--space-1);
}

/* ─── Footer ─── */
.card-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid var(--color-neutral-100);
  padding-top: var(--space-3);
}

/* ─── Attendees ─── */
.attendees { display: flex; align-items: center; gap: var(--space-2); }
.avatars   { display: flex; align-items: center; }

.attendee-avt {
  width: 28px;
  height: 28px;
  border-radius: var(--radius-full);
  border: 2px solid var(--color-neutral-0);
  margin-left: -10px;
  object-fit: cover;
}
.attendee-avt:first-child { margin-left: 0; }

.more-count {
  width: 28px;
  height: 28px;
  border-radius: var(--radius-full);
  background: var(--color-neutral-100);
  border: 2px solid var(--color-neutral-0);
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: -10px;
  color: var(--color-neutral-500);
}

.attend-text {
  font-size: var(--text-xs);
  color: var(--color-neutral-500);
}

/* ─── Join Button ─── */
.btn-join {
  background: var(--color-neutral-100);
  color: var(--color-neutral-900);
  border: none;
  padding: var(--space-1) var(--space-4);
  border-radius: var(--radius-full);
  font-weight: var(--font-bold);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: var(--transition-fast);
}

.btn-join:hover {
  background: var(--color-neutral-200);
}

.btn-join.joined {
  background: var(--color-success-bg);
  color: var(--color-success);
}
</style>