<template>
  <div class="event-detail-page">
    
    <div v-if="loading" class="post-loading"><div class="spinner-ring"></div></div>

    <template v-else-if="eventData">
    <div class="event-hero">
      <div class="hero-bg" :style="{ backgroundImage: `url(${eventData.cover})` }"></div>
      <div class="hero-overlay"></div>
      
      <div class="hero-content container">
        <span class="status-badge" :class="eventData.status">
          {{ eventData.status === 'ongoing' ? $t('event_detail.status_ongoing') : $t('event_detail.status_ended') }}
        </span>
        <h1 class="event-title">{{ eventData.title }}</h1>
        <div class="event-meta">
          <span>📅 {{ eventData.date }}</span>
          <span>📍 {{ eventData.location }}</span>
          <span>👥 {{ eventData.participants }} {{ $t('event_detail.participants') }}</span>
        </div>

        <div class="hero-actions">
          <button 
            v-if="!isRegistered" 
            class="btn-register"
            @click="handleRegister"
          >
            {{ $t('event_detail.register_now') }}
          </button>
          <button 
            v-else 
            class="btn-submit-entry"
            @click="openSubmitModal"
          >
            <span class="icon">📤</span> {{ $t('event_detail.submit_entry') }}
          </button>
        </div>
      </div>
    </div>

    <div class="sticky-tabs">
      <div class="container tabs-inner">
        <button 
          v-for="tab in tabs" :key="tab.id"
          class="tab-item"
          :class="{ active: currentTab === tab.id }"
          @click="currentTab = tab.id"
        >
          {{ tab.label }}
        </button>
      </div>
    </div>

    <div class="container content-body">
      <transition name="fade-slide" mode="out-in">
        
        <div v-if="currentTab === 'info'" class="info-layout">
          
          <div class="info-main">
            <div class="card-box">
              <h3>{{ $t('event_detail.intro') }}</h3>
              <p>{{ eventData.description }}</p>
              
              <h3>{{ $t('event_detail.rules') }}</h3>
              <ul class="rules-list">
                <li v-for="(rule, i) in eventData.rules" :key="i">
                  <span class="bullet">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                  </span> {{ rule }}
                </li>
              </ul>
            </div>

            <div class="card-box prizes-section">
              <h3>{{ $t('event_detail.prizes') }}</h3>
              <div class="prizes-grid">
                <div class="prize-card gold">
                  <div class="prize-icon">🥇</div>
                  <h4>{{ $t('event_detail.prize_1st') }}</h4>
                  <p>10,000,000 VND</p>
                  <span>{{ $t('event_detail.prize_1_bonus') }}</span>
                </div>
                <div class="prize-card silver">
                  <div class="prize-icon">🥈</div>
                  <h4>{{ $t('event_detail.prize_2nd') }}</h4>
                  <p>5,000,000 VND</p>
                  <span>{{ $t('event_detail.prize_2_bonus') }}</span>
                </div>
                <div class="prize-card bronze">
                  <div class="prize-icon">🥉</div>
                  <h4>{{ $t('event_detail.prize_3rd') }}</h4>
                  <p>2,000,000 VND</p>
                  <span>{{ $t('event_detail.prize_3_bonus') }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="info-sidebar">
            <div class="card-box">
              <h3>{{ $t('event_detail.judges') }}</h3>
              <div class="judges-list">
                <div class="judge-item" v-for="judge in eventData.judges" :key="judge.name">
                  <img :src="judge.avatar" class="judge-avt">
                  <div>
                    <div class="judge-name">{{ judge.name }}</div>
                    <div class="judge-role">{{ judge.role }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="entries-layout">
          
          <div class="filter-bar">
            <h3>{{ $t('event_detail.featured') }}</h3>
            <div class="sort-options">
              <button class="active">{{ $t('event_detail.most_voted') }}</button>
              <button>{{ $t('event_detail.newest') }}</button>
            </div>
          </div>

          <div class="entries-grid">
            <div v-for="entry in entries" :key="entry.id" class="contest-entry-card">
              <div class="entry-image">
                <img :src="entry.image">
                <div class="rank-badge" v-if="entry.rank <= 3">
                  {{ $t('event_detail.top_rank') }} {{ entry.rank }} 👑
                </div>
              </div>
              <div class="entry-body">
                <h4>{{ entry.title }}</h4>
                <div class="author">
                  <img :src="entry.author.avatar">
                  <span>{{ entry.author.name }}</span>
                </div>
                
                <div class="vote-action">
                  <button 
                    class="btn-vote" 
                    :class="{ voted: entry.isVoted }"
                    @click="handleVote(entry)"
                  >
                    <span class="icon">{{ entry.isVoted ? '💖' : '🤍' }}</span>
                    {{ entry.isVoted ? $t('event_detail.voted') : $t('event_detail.vote') }}
                  </button>
                  <div class="vote-count">
                    <b>{{ entry.votes }}</b> {{ $t('event_detail.votes_sfx') }}
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>

      </transition>
    </div>

    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getEventById } from '@/services/eventService'
import { toast } from '@/composables/useToast'

const route = useRoute()
const { t } = useI18n()

const eventData = ref(null)
const loading = ref(true)

const tabs = computed(() => [
  { id: 'info',    label: t('event_detail.tab_info') },
  { id: 'entries', label: t('event_detail.tab_entries') },
])
const currentTab = ref('info')
const isRegistered = ref(false)
const entries = ref([])

onMounted(async () => {
  const id = route.params.id
  try {
    const data = await getEventById(id)
    eventData.value = {
      title: data.eventName || 'GoMet Event',
      cover: 'https://images.unsplash.com/photo-1556761175-5973dc0f32e7?q=80&w=2032&auto=format&fit=crop',
      status: data.status || 'upcoming',
      date: `${data.startAt || '?'} → ${data.endAt || '?'}`,
      location: 'Online',
      participants: Number(data.participantCount) || 0,
      description: 'Share your recipe and join the GoMet community!',
      rules: [
        'Entries must include real photos/videos you created yourself.',
        'The recipe must have detailed, clear step-by-step instructions.',
        'Do not copy images from the internet.',
        'Each account may submit up to 3 entries.'
      ],
      judges: [
        { name: 'Admin Gomet', role: 'Organizers', avatar: 'https://ui-avatars.com/api/?name=Admin&background=EA580C&color=fff' }
      ],
      winnerPostTitle: data.winnerPostTitle || null
    }
  } catch (err) {
    console.warn('EventDetail: load error', err)
    // Fallback to placeholder
    eventData.value = {
      title: 'This event does not exist',
      cover: 'https://images.unsplash.com/photo-1556761175-5973dc0f32e7?q=80&w=2032&auto=format&fit=crop',
      status: 'ended', date: '---', location: '---', participants: 0,
      description: '', rules: [], judges: [], winnerPostTitle: null
    }
  } finally {
    loading.value = false
  }
})

const handleRegister = () => {
  isRegistered.value = true
  toast.success('Registration successful! Get your ingredients ready')
}

const openSubmitModal = () => {
  alert('Open submission form (Similar to CreatePost but tagged with Event)')
}

const handleVote = (entry) => {
  entry.isVoted = !entry.isVoted
  entry.votes += entry.isVoted ? 1 : -1
}
</script>

<style scoped>
.event-detail-page {
  font-family: 'Mulish', sans-serif; color: #1C1917; padding-bottom: 80px;
}
.container { max-width: 1100px; margin: 0 auto; width: 100%; padding: 0 20px; }

/* 1. HERO BANNER */
.event-hero { position: relative; height: 400px; display: flex; align-items: flex-end; padding-bottom: 40px; margin-bottom: 0; }
.hero-bg { position: absolute; inset: 0; background-size: cover; background-position: center; }
.hero-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.9), transparent); }
.hero-content { position: relative; z-index: 2; color: white; width: 100%; }

.status-badge { display: inline-block; padding: 4px 12px; border-radius: 20px; font-weight: 700; font-size: 0.8rem; margin-bottom: 10px; background: rgba(255,255,255,0.2); backdrop-filter: blur(5px); }
.status-badge.ongoing { color: #FCD34D; border: 1px solid #FCD34D; }

.event-title { font-family: 'Playfair Display', serif; font-size: 2.8rem; line-height: 1.2; margin: 0 0 15px; text-shadow: 0 2px 10px rgba(0,0,0,0.5); }
.event-meta { display: flex; gap: 25px; font-size: 1rem; opacity: 0.9; margin-bottom: 25px; }

.hero-actions button {
  padding: 12px 30px; border-radius: 30px; font-weight: 800; font-size: 1rem; cursor: pointer; border: none; transition: 0.2s;
}
.btn-register { background: #EA580C; color: white; box-shadow: 0 4px 15px rgba(234, 88, 12, 0.4); }
.btn-register:hover { background: #C2410C; transform: translateY(-2px); }
.btn-submit-entry { background: white; color: #1C1917; display: flex; align-items: center; gap: 8px; }
.btn-submit-entry:hover { background: #F5F5F4; }

/* 2. TABS */
.sticky-tabs { position: sticky; top: 70px; background: white; border-bottom: 1px solid #E5E5E5; z-index: 99; }
.tabs-inner { display: flex; gap: 40px; }
.tab-item { background: none; border: none; padding: 15px 0; font-size: 1rem; font-weight: 600; color: #78716C; cursor: pointer; border-bottom: 3px solid transparent; transition: 0.2s; }
.tab-item.active { color: #EA580C; border-bottom-color: #EA580C; }

/* 3. CONTENT LAYOUT */
.content-body { padding-top: 40px; min-height: 500px; }

/* Info Tab */
.info-layout { display: grid; grid-template-columns: 2fr 1fr; gap: 30px; }
.card-box { background: white; padding: 30px; border-radius: 20px; border: 1px solid #E5E5E5; margin-bottom: 30px; }
.card-box h3 { margin-top: 0; font-size: 1.3rem; border-bottom: 1px solid #F3F4F6; padding-bottom: 12px; margin-bottom: 20px; }
.rules-list { list-style: none; padding: 0; }
.rules-list li { margin-bottom: 12px; line-height: 1.5; color: #44403C; }

/* Prizes */
.prizes-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 15px; }
.prize-card { text-align: center; padding: 15px; border-radius: 12px; background: #F9FAFB; border: 1px solid #E5E5E5; }
.prize-icon { font-size: 2rem; margin-bottom: 5px; }
.prize-card h4 { margin: 0 0 5px; color: #1C1917; }
.prize-card p { color: #EA580C; font-weight: 800; margin: 0; }
.prize-card span { font-size: 0.75rem; color: #78716C; }
.prize-card.gold { border-color: #FCD34D; background: #FFFBEB; }

/* Judges */
.judge-item { display: flex; align-items: center; gap: 12px; margin-bottom: 15px; }
.judge-avt { width: 50px; height: 50px; border-radius: 50%; object-fit: cover; }
.judge-name { font-weight: 700; color: #1C1917; }
.judge-role { font-size: 0.85rem; color: #78716C; }

/* Entries Tab (Voting) */
.filter-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.sort-options button { background: white; border: 1px solid #E5E5E5; padding: 6px 16px; border-radius: 20px; margin-left: 10px; cursor: pointer; color: #57534E; }
.sort-options button.active { background: #1C1917; color: white; border-color: #1C1917; }

.entries-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 25px; }

/* Contest Card Style */
.contest-entry-card { background: white; border-radius: 16px; overflow: hidden; border: 1px solid #E5E5E5; transition: 0.3s; }
.contest-entry-card:hover { transform: translateY(-5px); box-shadow: 0 10px 30px rgba(0,0,0,0.1); }
.entry-image { height: 200px; position: relative; overflow: hidden; }
.entry-image img { width: 100%; height: 100%; object-fit: cover; }
.rank-badge { position: absolute; top: 10px; left: 10px; background: #FFD700; color: #92400E; font-weight: 800; font-size: 0.75rem; padding: 4px 10px; border-radius: 12px; box-shadow: 0 2px 5px rgba(0,0,0,0.2); }

.entry-body { padding: 15px; }
.entry-body h4 { margin: 0 0 10px; font-size: 1rem; line-height: 1.4; height: 42px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.author { display: flex; align-items: center; gap: 8px; font-size: 0.85rem; color: #57534E; margin-bottom: 15px; }
.author img { width: 24px; height: 24px; border-radius: 50%; }

.vote-action { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #F3F4F6; padding-top: 12px; }
.btn-vote { background: #F3F4F6; border: none; padding: 6px 14px; border-radius: 20px; font-weight: 700; font-size: 0.85rem; cursor: pointer; display: flex; align-items: center; gap: 6px; transition: 0.2s; color: #57534E; }
.btn-vote.voted { background: #FFE4E6; color: #E11D48; }
.vote-count { font-size: 0.85rem; color: #1C1917; }

/* Responsive */
@media (max-width: 768px) {
  .info-layout { grid-template-columns: 1fr; }
  .event-title { font-size: 2rem; }
  .prizes-grid { grid-template-columns: 1fr; }
}
</style>