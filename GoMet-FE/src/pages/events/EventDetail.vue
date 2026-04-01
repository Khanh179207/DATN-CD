<template>
  <div class="event-detail-page">
    <div v-if="loading" class="post-loading">
      <div class="spinner-ring"></div>
    </div>

    <template v-else-if="eventData">
      <div class="event-hero">
        <div class="hero-bg" :style="{ backgroundImage: `url(${eventData.cover})` }"></div>
        <div class="hero-overlay"></div>

        <div class="hero-content container">
          <div class="status-badge glass-effect" :class="eventData.category">
            <span class="pulse-dot" v-if="eventData.category === 'active'"></span>
            {{ statusText }}
          </div>

          <h1 class="event-title">{{ eventData.title }}</h1>

          <div class="event-meta">
            <span class="meta-pill">📅 {{ eventData.date }}</span>
            <span class="meta-pill">👥 {{ eventData.participants }} bài dự thi</span>
            <span class="meta-pill">🗳️ {{ totalEventVotes }} lượt bầu chọn</span>
          </div>

          <div class="hero-actions">
            <button v-if="eventData.category === 'active'" class="btn-submit-entry" @click="handleOpenModal">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="17 8 12 3 7 8"></polyline>
                <line x1="12" y1="3" x2="12" y2="15"></line>
              </svg>
              <span>{{ hasSubmitted ? 'Gửi thêm bài dự thi' : 'Nộp bài dự thi ngay' }}</span>
            </button>
            <button v-else class="btn-disabled" disabled>
              {{ eventData.category === 'upcoming' ? 'Sự kiện chưa mở cổng nộp bài' : 'Sự kiện đã kết thúc' }}
            </button>
          </div>
        </div>
      </div>

      <div class="sticky-tabs">
        <div class="container tabs-inner">
          <button v-for="tab in tabs" :key="tab.id" class="tab-item" :class="{ active: currentTab === tab.id }"
            @click="currentTab = tab.id">
            {{ tab.label }}
          </button>
        </div>
      </div>

      <div class="container content-body">
        <transition name="fade-slide" mode="out-in">

          <div v-if="currentTab === 'info'" class="info-layout">
            <div class="info-main">

              <div class="card-box prizes-section shadow-sm">
                <h3>🏆 Cơ cấu giải thưởng</h3>

                <div class="real-reward-display">
                  <div class="reward-card-inner">
                    <div class="reward-icon-main">🎁</div>
                    <div class="reward-info">
                      <span class="reward-label">Phần thưởng cho người thắng cuộc:</span>
                      <p class="reward-text-real">{{ eventData.reward }}</p>
                    </div>
                  </div>
                </div>

                <div class="prizes-grid mt-6">
                  <div class="prize-card gold small-card">
                    <div class="prize-icon">🥇</div>
                    <p>Hạng Nhất</p>
                  </div>
                  <div class="prize-card silver small-card">
                    <div class="prize-icon">🥈</div>
                    <p>Hạng Nhì</p>
                  </div>
                  <div class="prize-card bronze small-card">
                    <div class="prize-icon">🥉</div>
                    <p>Hạng Ba</p>
                  </div>
                </div>
              </div>
            </div>


            <div class="info-sidebar">
              <div class="card-box shadow-sm sticky-card">
                <h3>⏰ Cổng bình chọn (Voting)</h3>
                <div class="voting-timeline">
                  <div class="time-item">
                    <span class="label">Mở cổng:</span>
                    <span class="value">{{ formatDateTime(eventData.voteStartAt) }}</span>
                  </div>
                  <div class="time-item">
                    <span class="label">Đóng cổng:</span>
                    <span class="value">{{ formatDateTime(eventData.voteEndAt) }}</span>
                  </div>

                  <div class="vote-status-msg" :class="votingStatus.type">
                    <i class="fas" :class="votingStatus.type === 'active' ? 'fa-unlock' : 'fa-lock'"></i>
                    {{ votingStatus.message }}
                  </div>
                </div>

                <p class="vote-note" v-if="votingStatus.type === 'active'">
                  * Sếp có tối đa <strong>3 phiếu bầu</strong> cho mỗi sự kiện. Hãy chọn kỹ nhé!
                </p>
              </div>
            </div>
          </div>

          <div v-else class="entries-layout">

            <div v-if="topEntries.length > 0" class="ranking-section">
              <h3 class="section-title">🏆 Bảng xếp hạng tạm thời</h3>
              <div class="entries-grid">
                <ContestEntryCard v-for="(entry, index) in topEntries" :key="entry.eventPostID" :post="entry"
                  :rank="index + 1" :can-vote="isInVotingPeriod" />
              </div>
            </div>

            <div class="all-entries-section mt-12">
              <h3 class="section-title">🎨 Tác phẩm tham gia ({{ entries.length }})</h3>
              <div v-if="entries.length > 0" class="entries-grid">
                <ContestEntryCard v-for="entry in entries" :key="entry.eventPostID" :post="entry"
                  :can-vote="isInVotingPeriod" />
              </div>

              <div v-else class="empty-entries">
                <div class="icon">📭</div>
                <p>Chưa có bài dự thi nào. Sếp nộp bài để nhận giải ngay!</p>
              </div>
            </div>
          </div>

        </transition>
      </div>

      <SubmitEntryModal :is-open="isModalOpen" :event-id="eventData.id" @close="isModalOpen = false"
        @submit-success="onEntrySubmitted" />

    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineEmits } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import api from '@/services/api'
import { getEventById } from '@/services/eventService'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import SubmitEntryModal from '@/components/modals/SubmitEntryModal.vue'
import ContestEntryCard from '@/components/common/EntryCard.vue' // Sửa đúng đường dẫn file của sếp

const emit = defineEmits(['ui:open-login'])

const route = useRoute()
const authStore = useAuthStore()
const { t } = useI18n()

// --- STATES ---
const eventData = ref(null)
const loading = ref(true)
const currentTab = ref('info')
const hasSubmitted = ref(false)
const entries = ref([])
const isModalOpen = ref(false)

const tabs = computed(() => [
  { id: 'info', label: t('event_detail.tab_info') },
  { id: 'entries', label: 'Bài dự thi' },
])

// --- LOGIC RANKING ---
const topEntries = computed(() => {
  // Sắp xếp theo phiếu bầu giảm dần, lấy 3 ông cao nhất
  return [...entries.value].sort((a, b) => b.voteCount - a.voteCount).slice(0, 3)
})

const totalEventVotes = computed(() => {
  return entries.value.reduce((sum, item) => sum + (item.voteCount || 0), 0)
})

// --- LOGIC THỜI GIAN VOTE (VÔ CÙNG QUAN TRỌNG) ---
const isInVotingPeriod = computed(() => {
  if (!eventData.value?.voteStartAt || !eventData.value?.voteEndAt) return false
  const now = new Date()
  return now >= new Date(eventData.value.voteStartAt) && now <= new Date(eventData.value.voteEndAt)
})

const votingStatus = computed(() => {
  if (!eventData.value) return { type: '', message: '' }
  const now = new Date()
  const start = new Date(eventData.value.voteStartAt)
  const end = new Date(eventData.value.voteEndAt)

  if (now < start) return { type: 'upcoming', message: 'Bình chọn chưa mở' }
  if (now > end) return { type: 'ended', message: 'Bình chọn đã đóng' }
  return { type: 'active', message: 'Đang mở bình chọn' }
})

// --- HELPERS ---
const formatDate = (dateStr) => {
  if (!dateStr) return '...'
  const d = new Date(dateStr)
  return d.toLocaleDateString('vi-VN')
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return 'Chưa cập nhật'
  return new Date(dateStr).toLocaleString('vi-VN', {
    hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric'
  })
}

const loadAllData = async () => {
  const eventId = route.params.id
  loading.value = true
  try {
    const data = await getEventById(eventId)

    const now = new Date()
    let currentStatus = 'upcoming'
    if (now > new Date(data.endAt)) currentStatus = 'ended'
    else if (now >= new Date(data.startAt) && now <= new Date(data.endAt)) currentStatus = 'active'

    // 🔥 ĐỔ DỮ LIỆU THẬT TỪ DATABASE VÀO ĐÂY
    eventData.value = {
      id: data.eventID,
      title: data.eventName,
      cover: data.bannerImage?.startsWith('/uploads')
        ? `http://localhost:8080${encodeURI(data.bannerImage)}`
        : (data.bannerImage || 'https://images.unsplash.com/photo-1556761175-5973dc0f32e7?q=80'),
      category: currentStatus,
      date: `${formatDate(data.startAt)} → ${formatDate(data.endAt)}`,
      voteStartAt: data.voteStartAt,
      voteEndAt: data.voteEndAt,
      participants: data.postCount || 0,

      // Lấy từ SQL:
      description: data.description || 'Sự kiện này chưa có mô tả chi tiết.',

      // Tách chuỗi rules bằng dấu xuống dòng để hiển thị dạng list (li)
      rules: data.rules
        ? data.rules.split('\n').filter(r => r.trim() !== '')
        : ['Chưa có quy định cụ thể.'],

      // Lấy giải thưởng thật
      reward: data.reward || 'Thông tin giải thưởng đang được cập nhật.'
    }

    await fetchEventEntries(eventId)
  } catch (err) {
    toast.error('Không thể tải thông tin sự kiện!')
  } finally {
    loading.value = false
  }
}

const fetchEventEntries = async (eventId) => {
  try {
    const res = await api.get(`/api/events/${eventId}/posts`)
    entries.value = res.data

    if (authStore.user) {
      // Check xem user hiện tại đã nộp bài chưa
      hasSubmitted.value = entries.value.some(e => e.authorName === authStore.user.username)
    }
  } catch (e) {
    entries.value = []
  }
}

const handleOpenModal = () => {
  if (!authStore.isAuthenticated) {
    emit('ui:open-login');
    return;
  }
  isModalOpen.value = true;
}

const onEntrySubmitted = () => {
  isModalOpen.value = false
  fetchEventEntries(eventData.value.id)
  currentTab.value = 'entries'
  toast.success('Gửi bài dự thi thành công! Chúc sếp giật giải!')
}

onMounted(loadAllData)
</script>

<style lang="scss">
@import './EventDetail.scss';

.section-title {
  font-size: 1.5rem;
  font-weight: 800;
  margin-bottom: 24px;
  color: #1a1a1a;
  border-left: 6px solid #EA580C;
  padding-left: 15px;
}

.vote-status-msg {
  margin-top: 20px;
  padding: 12px;
  border-radius: 12px;
  font-weight: 700;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  &.active {
    background: #dcfce7;
    color: #15803d;
    border: 1px solid #86efac;
  }

  &.upcoming {
    background: #fef9c3;
    color: #a16207;
    border: 1px solid #fde047;
  }

  &.ended {
    background: #fee2e2;
    color: #b91c1c;
    border: 1px solid #fca5a5;
  }
}

.vote-note {
  margin-top: 15px;
  font-size: 0.85rem;
  color: #64748b;
  font-style: italic;
  line-height: 1.4;
}

.voting-timeline {
  .time-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 0.95rem;

    .label {
      color: #64748b;
    }

    .value {
      font-weight: 700;
      color: #1e293b;
    }
  }
}

.mt-12 {
  margin-top: 3rem;
}

.mb-12 {
  margin-bottom: 3rem;
}
</style>