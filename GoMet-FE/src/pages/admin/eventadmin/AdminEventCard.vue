<template>
  <div class="event-row-card" :class="{'is-deleted': getStatus(ev) === 'deleted'}">
    <div class="event-id-badge">{{ $t('admin.events.event_id') }}: {{ ev.eventID }}</div>

    <div class="card-main">
      <div class="img-frame">
        <img :src="getImageUrl(ev.bannerImage)" :alt="ev.eventName" />
      </div>
      <div class="info-block">
        <h3 class="ev-name">{{ ev.eventName }}</h3>
        
        <div class="visual-timeline">
          <div class="step" :class="phaseClass(ev.startAt, ev.endAt)">
            <span class="dot"></span>
            <span class="t-label">{{ $t('admin.events.submit_phase') }}</span>
            <span class="t-date">{{ formatDate(ev.startAt) }} — {{ formatDate(ev.endAt) }}</span>
            <span class="phase-badge">{{ phaseLabel(ev.startAt, ev.endAt) }}</span>
          </div>
          <div class="step" :class="phaseClass(ev.voteStartAt, ev.voteEndAt)">
            <span class="dot"></span>
            <span class="t-label">{{ $t('admin.events.voting_phase') }}</span>
            <span class="t-date">{{ formatDate(ev.voteStartAt) }} — {{ formatDate(ev.voteEndAt) }}</span>
            <span class="phase-badge">{{ phaseLabel(ev.voteStartAt, ev.voteEndAt) }}</span>
          </div>
        </div>
        
        <div v-if="getStatus(ev) === 'ended' || getStatus(ev) === 'deleted'" class="winner-quick-view mt-2">
          <template v-if="ev.winnerData">
            <span class="badge-crown">👑 {{ $t('admin.events.winner') }}</span> 
            <strong>{{ ev.winnerData.username }}</strong> 
            <span class="text-sm text-gray-500">({{ $t('admin.events.account_id') }}: #{{ ev.winnerData.accountID }})</span>
          </template>
          <template v-else>
            <span class="badge-pending">{{ $t('admin.events.no_winner_data') }}</span>
          </template>
        </div>

      </div>
    </div>

    <div class="card-stats">
      <div class="stat-pill">
        <span class="s-lbl">{{ $t('admin.events.entries') }}</span>
        <span class="s-val">{{ ev.postCount || 0 }}</span>
      </div>
      <div class="stat-pill">
        <span class="s-lbl">{{ $t('admin.events.votes') }}</span>
        <span class="s-val">{{ ev.totalVotes || 0 }}</span>
      </div>
    </div>

    <div class="card-status-box">
      <div class="status-pill" :class="getStatus(ev)">{{ getStatusText(ev) }}</div>
      <div class="visibility-pill" :class="getVisibilityClass(ev)">{{ getVisibilityText(ev) }}</div>
    </div>

    <div class="card-actions">
      <button v-if="getStatus(ev) === 'ended' || getStatus(ev) === 'deleted'" class="btn-circle winner" @click="$emit('viewWinner', ev)" :title="t('admin.events.view_winner')">
        <i class="fas fa-crown"></i>
      </button>
      
      <button v-if="getStatus(ev) !== 'ended' && getStatus(ev) !== 'deleted'" class="btn-circle edit" @click="$emit('openEditModal', ev)" :title="t('admin.events.edit_event')">
        <i class="fas fa-edit"></i>
      </button>
      <button v-else class="btn-circle view" @click="$emit('openViewModal', ev)" :title="t('admin.events.view_info')">
        <i class="fas fa-eye"></i>
      </button>
      
      <button v-if="getStatus(ev) !== 'deleted'" class="btn-circle delete" @click="$emit('deleteEvent', ev.eventID, true)" :title="t('admin.events.hide_event')">
        <i class="fas fa-trash-alt"></i>
      </button>
      <button v-else class="btn-circle restore" @click="$emit('deleteEvent', ev.eventID, false)" :title="t('admin.events.restore_event')">
        <i class="fas fa-redo"></i>
      </button>

      <button class="btn-circle view-posts" @click="$emit('goToPostManagement', ev.eventID)" :title="t('admin.events.manage_entries')">
        <i class="fas fa-list-ul"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
import { formatLocaleDateTime } from '@/i18n'

const props = defineProps({
  ev: {
    type: Object,
    required: true
  }
})

const emit = defineEmits([
  'viewWinner', 
  'openEditModal', 
  'openViewModal', 
  'deleteEvent', 
  'goToPostManagement'
])

const { t, locale } = useI18n()

// --- Helper Methods chạy riêng cho từng thẻ ---
const getImageUrl = (path) => {
  if (!path) return 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=300';
  return path.startsWith('/uploads') ? `http://localhost:8080${encodeURI(path)}` : path;
}

const getStatus = (ev) => {
  const activeStatus = Number(ev.isActive);
  const forceStatus = Number(ev.isForceEnded);

  if (activeStatus === 0) return 'deleted'; 
  if (forceStatus === 1) return 'ended'; 

  if (!ev.startAt || !ev.voteEndAt) return 'upcoming';

  const now = new Date();
  const start = new Date(ev.startAt);
  const end = new Date(ev.voteEndAt); 

  if (now < start) return 'upcoming';
  if (now > end) return 'ended';
  return 'active';
};

const getStatusText = (ev) => ({
  ended: t('admin.events.status_ended'),
  upcoming: t('admin.events.status_upcoming'),
  active: t('admin.events.status_active'),
  deleted: t('admin.events.status_hidden')
}[getStatus(ev)])
const getVisibilityText = (ev) => ev.isActive === 0 ? t('admin.events.visibility_hidden') : t('admin.events.visibility_visible')
const getVisibilityClass = (ev) => ev.isActive === 0 ? 'hidden' : 'opened'

const phaseClass = (startDate, endDate) => {
  if (!startDate || !endDate) return 'step-upcoming';
  const now = new Date();
  if (now > new Date(endDate)) return 'step-ended';
  if (now >= new Date(startDate) && now <= new Date(endDate)) return 'step-active';
  return 'step-upcoming';
}

const phaseLabel = (startDate, endDate) => {
  if (!startDate || !endDate) return '';
  const now = new Date();
  if (now > new Date(endDate)) return `(${t('admin.events.phase_past')})`;
  if (now >= new Date(startDate) && now <= new Date(endDate)) return `(${t('admin.events.phase_open')})`;
  return `(${t('admin.events.phase_soon')})`;
}

const formatDate = (d) => d ? formatLocaleDateTime(d, locale.value) : t('admin.common.empty_value')
</script>

<style lang="scss" scoped>
$orange: #ea580c;
$orange-light: #fff7ed;
$slate-900: #0f172a;
$slate-500: #64748b;
$slate-200: #e2e8f0;
$slate-100: #f1f5f9;
$green-500: #15803d;
$green-300: #dcfce7;
$red-500: #b91c1c;
$red-100: #fef2f2;
$white: #ffffff;

.event-row-card {
  background: $white; border-radius: 32px; border: 1px solid rgba(0,0,0,0.03); 
  padding: 25px; position: relative; transition: 0.5s cubic-bezier(0.2, 0.8, 0.2, 1); 
  box-shadow: 0 10px 30px rgba(0,0,0,0.02); 
  display: grid; grid-template-columns: 1fr 180px 150px; align-items: center; gap: 40px;
  
  &:hover { 
    transform: translateY(-5px); box-shadow: 0 30px 60px -15px rgba(0,0,0,0.08); 
    border-color: rgba(234, 88, 12, 0.15); 
  }

  &.is-deleted {
    opacity: 0.6; filter: grayscale(80%); background: #f8fafc !important;
    border: 1px dashed #cbd5e1 !important; transition: 0.3s;
    &:hover { opacity: 0.9; filter: grayscale(0%); }
  }
  
  .event-id-badge { position: absolute; bottom: 20px; right: 30px; font-size: 0.75rem; font-weight: 900; color: #cbd5e1; letter-spacing: 1px; }

  /* --- CỘT 1: Ảnh + Timeline --- */
  .card-main {
    display: flex; gap: 30px; align-items: center;
    
    .img-frame { 
      width: 160px; height: 160px; border-radius: 24px; overflow: hidden; 
      box-shadow: 0 15px 35px rgba(0,0,0,0.08); position: relative; flex-shrink: 0;
      img { width: 100%; height: 100%; object-fit: cover; transition: 0.8s ease; }
      &:hover img { transform: scale(1.08); }
    }
    
    .info-block {
      display: flex; flex-direction: column;
      .ev-name { font-size: 1.6rem; font-weight: 900; color: $slate-900; margin: 0 0 20px; letter-spacing: -0.5px; }
      
      .visual-timeline {
        display: flex; flex-direction: column; gap: 14px;
        .step { 
          display: flex; align-items: center; gap: 16px; 
          .dot { 
            width: 10px; height: 10px; border-radius: 50%; background: #cbd5e1; position: relative; 
            &::after { content: ''; position: absolute; top: 10px; left: 50%; transform: translateX(-50%); width: 2px; height: 14px; background: #e2e8f0; } 
          } 
          .t-label { font-size: 0.8rem; font-weight: 800; color: $slate-500; text-transform: uppercase; width: 85px; letter-spacing: 0.5px; } 
          .t-date { font-size: 0.95rem; font-weight: 600; color: $slate-900; } 
          .phase-badge { font-size: 0.72rem; font-weight: 700; color: #475569; background: #eff6ff; padding: 2px 8px; border-radius: 8px; margin-left: auto; font-style: italic;}
          
          &.step-active { 
            .dot { background: $orange; box-shadow: 0 0 0 5px rgba(234, 88, 12, 0.15); } 
            .t-label, .t-date { color: $orange; font-weight: 800; } 
            .phase-badge { background: #fff7ed; color: $orange; }
          }
          &.step-ended, &.past-step {
            .dot { background: #94a3b8; box-shadow: 0 0 0 5px rgba(148,163,184,0.15); }
            .t-label, .t-date { color: #94a3b8; font-weight: 600; text-decoration: line-through;}
            .phase-badge { background: #e2e8f0; color: #64748b; }
          }
          &.step-upcoming {
            .dot { background: #cbd5e1; }
            .t-date { color: #64748b; font-weight: 600; }
            .phase-badge { background: #f8fafc; color: #64748b; }
          }
        }
        .step:last-child .dot::after { display: none; }
      }

      .winner-quick-view {
        background: #FFF7ED; border: 1px solid #FFEDD5; padding: 6px 12px; border-radius: 8px; font-size: 0.85rem; margin-top: 10px; display: inline-block;
        .badge-crown { font-weight: 800; color: #EA580C; margin-right: 6px; }
        strong { color: #0F172A; }
        .badge-pending { color: #64748B; font-style: italic; }
      }
    }
  }

  /* --- CỘT 2: Stats Grid --- */
  .card-stats { 
    display: grid; grid-template-columns: 1fr; gap: 12px;
    .stat-pill { 
      background: #f8fafc; border: 1px solid transparent; padding: 12px 18px; border-radius: 16px; 
      display: flex; justify-content: space-between; align-items: center; transition: 0.3s;
      &:hover { background: $white; border-color: #e2e8f0; box-shadow: 0 5px 15px rgba(0,0,0,0.03); }
      .s-lbl { font-size: 0.75rem; font-weight: 800; color: $slate-500; text-transform: uppercase; } 
      .s-val { font-size: 1.1rem; font-weight: 900; color: $slate-900; } 
    } 
  }

  /* --- CỘT 3: Trạng Thái --- */
  .card-status-box {
    display: flex; flex-direction: column; align-items: center; gap: 8px;
    .status-pill, .visibility-pill { font-size: 0.85rem; font-weight: 900; text-align: center; border-radius: 14px; text-transform: uppercase; letter-spacing: 0.5px; width: 100%; padding: 8px 0; }
    .status-pill {
      &.active { background: $green-300; color: $green-500; }
      &.upcoming { background: $orange-light; color: $orange; border: 1px solid #ffedd5; }
      &.ended { background: $slate-100; color: $slate-500; }
      &.deleted { background: $red-100; color: $red-500; border: 1px solid #fecaca; }
    }
    .visibility-pill {
      font-size: 0.75rem; font-weight: 800; padding: 6px 14px; letter-spacing: 0.4px;
      &.opened { background: #e0f2fe; color: #2563eb; border: 1px solid #bfdbfe;}
      &.hidden { background: $slate-100; color: $slate-500; border: 1px solid $slate-200; }
    }
  }

  /* --- FLOATING ACTIONS --- */
  .card-actions { 
    position: absolute; top: 25px; right: 25px; display: flex; gap: 8px; opacity: 0; transform: translateX(10px); transition: 0.4s ease; 
    .btn-circle { 
      width: 42px; height: 42px; border-radius: 50%; border: none; background: #f8fafc; color: $slate-500; 
      cursor: pointer; box-shadow: 0 5px 15px rgba(0,0,0,0.05); display: flex; align-items: center; justify-content: center; font-size: 1.1rem; transition: 0.2s; 
      
      &:hover { background: $slate-900; color: $white; transform: scale(1.15) rotate(5deg); } 
      &.delete:hover { background: #ef4444; } 
      &.winner:hover { background: #fcd34d; color: $slate-900; } 
      &.restore { color: #10b981; &:hover { background: #d1fae5; color: #047857; } }
      &.view { color: #2563eb; &:hover { background: #dbeafe; color: #1d4ed8; } }
      &.view-posts { color: #8b5cf6; &:hover { background: #ede9fe; color: #6d28d9; } }
    } 
  }
  &:hover .card-actions { opacity: 1; transform: translateX(0); }
}

@media (max-width: 1200px) {
  .event-row-card {
    grid-template-columns: 1fr; gap: 20px;
    .card-main { flex-direction: column; }
    .card-stats { grid-template-columns: repeat(2, 1fr); }
    .event-id-badge { bottom: auto; top: 20px; right: 20px; }
  }
}
@media (max-width: 768px) {
  .event-row-card { padding: 18px; }
  .card-main { gap: 16px; }
}
</style>