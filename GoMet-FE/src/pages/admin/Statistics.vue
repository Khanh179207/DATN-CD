<template>
  <div class="stats-page">
    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h2 class="page-title"><BarChart2 :size="22" style="vertical-align:middle;margin-right:8px" /> Thống Kê & Phân Tích</h2>
        <p class="page-sub">Dữ liệu thực từ hệ thống GoMet</p>
      </div>
      <div class="header-right">
        <select v-model="selectedRange" @change="fetchSystemStats" class="range-select">
          <option value="week">7 ngày qua</option>
          <option value="month">Tháng này</option>
          <option value="year">Năm nay</option>
        </select>
        <button class="btn-refresh" @click="refreshAll" :disabled="loading">
          <span :class="['icon-refresh', { spinning: loading }]">↻</span>
          Làm mới
        </button>
      </div>
    </div>
    <!-- Tabs -->
    <div class="tab-bar">
      <button v-for="t in tabs" :key="t.key"
        :class="['tab-btn', { active: activeTab === t.key }]"
        @click="activeTab = t.key; onTabChange(t.key)">
        <component :is="t.icon" :size="15" />
        {{ t.label }}
      </button>
    </div>
    <!-- SYSTEM TAB -->
    <div v-show="activeTab === 'system'">
      <div class="kpi-grid">
        <div v-for="s in overviewStats" :key="s.label" class="kpi-card" :style="{ '--accent': s.color }">
          <div class="kpi-icon-wrap" :style="{ background: s.color + '18' }"><component :is="s.icon" :size="22" :style="{ color: s.color }" /></div>
          <div>
            <div class="kpi-val" :style="{ color: s.color }">{{ s.value.toLocaleString('vi-VN') }}</div>
            <div class="kpi-lbl">{{ s.label }}</div>
          </div>
        </div>
      </div>
      <div class="two-col">
        <div class="chart-card wide">
          <div class="card-head"><h3><TrendingUp :size="15" /> Bài viết theo tháng</h3></div>
          <div v-if="loading" class="state-empty"><Clock :size="15" /> Đang tải...</div>
          <div v-else-if="!chartData.length" class="state-empty">Không có dữ liệu</div>
          <div v-else class="bar-chart">
            <div class="bar-wrap" v-for="d in chartData" :key="d.label">
              <div class="bar-col">
                <span class="bar-tip">{{ d.rawPosts }}</span>
                <div class="bar-fill blue" :style="{ height: d.views + '%' }"></div>
              </div>
              <span class="bar-lbl">{{ d.label }}</span>
            </div>
          </div>
        </div>
        <div class="chart-card">
          <div class="card-head"><h3>👥 Người dùng mới theo tháng</h3></div>
          <div v-if="loading" class="state-empty"><Clock :size="15" /> Đang tải...</div>
          <div v-else-if="!userChartData.length" class="state-empty">Không có dữ liệu</div>
          <div v-else class="mini-bars">
            <div v-for="u in userChartData" :key="u.label" class="mini-row">
              <span class="mini-lbl">{{ u.label }}</span>
              <div class="mini-bg"><div class="mini-fill purple" :style="{ width: u.height + '%' }"></div></div>
              <span class="mini-val">{{ u.rawUsers }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- USERS TAB -->
    <div v-show="activeTab === 'users'">
      <div v-if="loadingUsers" class="loading-state"><div class="spinner"></div><p>Đang tải...</p></div>
      <div v-else>
        <div class="metric-tabs">
          <button v-for="m in userMetrics" :key="m.key" :class="['metric-btn', { active: userMetric === m.key }]" @click="userMetric = m.key">
            <component :is="m.icon" :size="13" /> {{ m.label }}
          </button>
        </div>
        <div class="chart-card">
          <div class="card-head"><h3><component :is="currentUserMetric.icon" :size="15" /> {{ currentUserMetric.label }}</h3></div>
          <div v-if="!currentUserList.length" class="state-empty">Không có dữ liệu</div>
          <div v-else class="rank-list">
            <div v-for="(u, i) in currentUserList" :key="u.accountID" class="rank-row">
              <div class="rank-badge" :class="'rank-' + (i+1)">{{ i + 1 }}</div>
              <img :src="u.avatar || ('https://ui-avatars.com/api/?name=' + encodeURIComponent(u.username) + '&background=EA580C&color=fff')" class="rank-avatar" alt="" />
              <div class="rank-info"><span class="rank-name">{{ u.username }}</span></div>
              <div class="rank-bar-wrap">
                <div class="rank-bar-bg"><div class="rank-bar-fill" :style="{ width: barWidth(currentUserList, u.value) + '%', background: currentUserMetric.color }"></div></div>
              </div>
              <div class="rank-value" :style="{ color: currentUserMetric.color }">{{ Number(u.value).toLocaleString('vi-VN') }}<small> {{ currentUserMetric.unit }}</small></div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- POSTS TAB -->
    <div v-show="activeTab === 'posts'">
      <div v-if="loadingPosts" class="loading-state"><div class="spinner"></div><p>Đang tải...</p></div>
      <div v-else>
        <div class="two-col">
          <div class="chart-card">
            <div class="card-head"><h3><Target :size="15" /> Phân bổ theo Độ khó</h3></div>
            <div v-if="!postStats.byLevel?.length" class="state-empty">Không có dữ liệu</div>
            <div v-else class="level-bars">
              <div v-for="lv in postStats.byLevel" :key="lv.level" class="level-row">
                <div class="level-label">
                  <span class="level-badge" :class="'lv-' + lv.level">Cấp {{ lv.level }}</span>
                  <span class="level-count">{{ lv.count }} bài</span>
                </div>
                <div class="level-bg"><div class="level-fill" :style="{ width: levelBar(lv.count) + '%', background: levelColor(lv.level) }"></div></div>
              </div>
            </div>
          </div>
          <div class="chart-card">
            <div class="card-head"><h3><Star :size="15" /> Top Đánh giá cao</h3></div>
            <div v-if="!postStats.topByRating?.length" class="state-empty">Chưa có đánh giá</div>
            <div v-else class="post-rank-list">
              <div v-for="(p, i) in postStats.topByRating" :key="p.postID" class="post-rank-row">
                <span class="post-rank-num">{{ i + 1 }}</span>
                <div class="post-info"><span class="post-title">{{ p.title }}</span><span class="post-author">@{{ p.author }}</span></div>
                <div class="post-stars">
                  <span v-for="s in 5" :key="s" :class="s <= Math.round(p.value) ? 'star on' : 'star'">★</span>
                  <span class="star-val">{{ Number(p.value).toFixed(1) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="two-col mt-20">
          <div class="chart-card">
            <div class="card-head"><h3><MessageCircle :size="15" /> Top bài viết nhiều bình luận</h3></div>
            <div v-if="!postStats.topByComments?.length" class="state-empty">Không có dữ liệu</div>
            <div v-else class="post-rank-list">
              <div v-for="(p, i) in postStats.topByComments" :key="p.postID" class="post-rank-row">
                <span class="post-rank-num">{{ i + 1 }}</span>
                <div class="post-info"><span class="post-title">{{ p.title }}</span><span class="post-author">@{{ p.author }}</span></div>
                <span class="post-stat orange">{{ p.value }} <MessageCircle :size="13" /></span>
              </div>
            </div>
          </div>
          <div class="chart-card">
            <div class="card-head"><h3><Heart :size="15" /> Top bài viết nhiều lượt thích</h3></div>
            <div v-if="!postStats.topByLikes?.length" class="state-empty">Không có dữ liệu</div>
            <div v-else class="post-rank-list">
              <div v-for="(p, i) in postStats.topByLikes" :key="p.postID" class="post-rank-row">
                <span class="post-rank-num">{{ i + 1 }}</span>
                <div class="post-info"><span class="post-title">{{ p.title }}</span><span class="post-author">@{{ p.author }}</span></div>
                <span class="post-stat red">{{ p.value }} <Heart :size="13" /></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- EVENTS TAB -->
    <div v-show="activeTab === 'events'">
      <div v-if="loadingEvents" class="loading-state"><div class="spinner"></div><p>Đang tải...</p></div>
      <div v-else>
        <div class="kpi-grid kpi-3">
          <div class="kpi-card" style="--accent: #8B5CF6"><div class="kpi-icon-wrap" style="background:#8B5CF618"><Calendar :size="22" style="color:#8B5CF6" /></div><div><div class="kpi-val" style="color:#8B5CF6">{{ eventStats.totalEvents ?? 0 }}</div><div class="kpi-lbl">Tổng sự kiện</div></div></div>
          <div class="kpi-card" style="--accent: #10B981"><div class="kpi-icon-wrap" style="background:#10B98118"><CheckCircle :size="22" style="color:#10B981" /></div><div><div class="kpi-val" style="color:#10B981">{{ eventStats.activeEvents ?? 0 }}</div><div class="kpi-lbl">Đang diễn ra</div></div></div>
          <div class="kpi-card" style="--accent: #F97316"><div class="kpi-icon-wrap" style="background:#F9731618"><Mail :size="22" style="color:#F97316" /></div><div><div class="kpi-val" style="color:#F97316">{{ eventStats.totalEntries ?? 0 }}</div><div class="kpi-lbl">Tổng lượt tham gia</div></div></div>
        </div>
        <div class="chart-card mt-20">
          <div class="card-head"><h3><Trophy :size="15" /> Sự kiện có nhiều lượt tham gia nhất</h3></div>
          <div v-if="!eventStats.topByParticipations?.length" class="state-empty">Không có dữ liệu</div>
          <div v-else class="event-table">
            <div class="event-table-head"><span>#</span><span>Tên Sự Kiện</span><span>Thời gian</span><span>Bài tham gia</span><span>Người tham dự</span><span>Trạng thái</span></div>
            <div v-for="(e, i) in eventStats.topByParticipations" :key="e.eventID" class="event-table-row">
              <span class="e-rank" :class="'rank-' + (i+1)">{{ i + 1 }}</span>
              <div class="e-name"><strong>{{ e.eventName }}</strong></div>
              <span class="e-date">{{ formatDate(e.startAt) }} → {{ formatDate(e.endAt) }}</span>
              <span class="e-val orange">{{ e.totalPosts }}</span>
              <span class="e-val blue">{{ e.uniqueUsers }}</span>
              <span :class="['e-status', isEventActive(e) ? 'active' : 'ended']">{{ isEventActive(e) ? 'Đang diễn ra' : 'Đã kết thúc' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  BarChart2, Users, FileText, Calendar, Clock, AlertTriangle, CheckCircle,
  Diamond, Eye, UserPlus, UserCheck, Heart, Trophy, Medal, Send, Pen,
  TrendingUp, Target, MessageCircle, Star, Award, Mail
} from 'lucide-vue-next'
import api from '@/services/api'
const activeTab     = ref('system')
const selectedRange = ref('year')
const loading       = ref(false)
const loadingUsers  = ref(false)
const loadingPosts  = ref(false)
const loadingEvents = ref(false)
const tabs = [
  { key: 'system', icon: BarChart2, label: 'Hệ thống' },
  { key: 'users',  icon: Users,     label: 'Người dùng' },
  { key: 'posts',  icon: FileText,  label: 'Bài viết' },
  { key: 'events', icon: Calendar,  label: 'Sự kiện' },
]
const overviewStats = ref([
  { label: 'Tổng người dùng',    value: 0, icon: Users,         color: '#3B82F6' },
  { label: 'Tổng bài viết',      value: 0, icon: FileText,      color: '#F97316' },
  { label: 'Chờ duyệt',          value: 0, icon: Clock,         color: '#EAB308' },
  { label: 'Báo cáo',            value: 0, icon: AlertTriangle, color: '#EF4444' },
  { label: 'User hoạt động',     value: 0, icon: CheckCircle,   color: '#10B981' },
  { label: 'Premium',            value: 0, icon: Diamond,       color: '#8B5CF6' },
  { label: 'Tổng lượt xem',      value: 0, icon: Eye,           color: '#06B6D4' },
  { label: 'User mới hôm nay',   value: 0, icon: UserPlus,      color: '#F43F5E' },
])
const chartData     = ref([])
const userChartData = ref([])
const userMetric = ref('topByFollowers')
const userStatsData = ref({})
const userMetrics = [
  { key: 'topByFollowers',           icon: Users,    label: 'Được follow nhiều nhất',       unit: 'followers',  color: '#3B82F6' },
  { key: 'topByLikes',               icon: Heart,    label: 'Được like nhiều nhất',          unit: 'likes',      color: '#EF4444' },
  { key: 'topByEventWins',           icon: Trophy,   label: 'Thắng sự kiện nhiều nhất',     unit: 'lần thắng',  color: '#F59E0B' },
  { key: 'topByAchievements',        icon: Medal,    label: 'Nhiều danh hiệu nhất',         unit: 'danh hiệu',  color: '#8B5CF6' },
  { key: 'topByEventParticipations', icon: Calendar, label: 'Tham gia sự kiện nhiều nhất',  unit: 'lần',        color: '#F97316' },
  { key: 'topByPostCount',           icon: Pen,      label: 'Đăng bài nhiều nhất',          unit: 'bài viết',   color: '#10B981' },
]
const currentUserMetric = computed(() => userMetrics.find(m => m.key === userMetric.value) || userMetrics[0])
const currentUserList   = computed(() => userStatsData.value[userMetric.value] || [])
const postStats  = ref({})
const eventStats = ref({})
function barWidth(list, value) {
  const max = Math.max(...list.map(u => Number(u.value)), 1)
  return Math.round((Number(value) / max) * 80) + 10
}
function levelColor(lv) {
  return ['#10B981','#3B82F6','#8B5CF6','#F59E0B','#EF4444'][(lv-1)%5] || '#94A3B8'
}
function levelBar(count) {
  const all = postStats.value.byLevel || []
  const max = Math.max(...all.map(l => l.count), 1)
  return Math.round((count / max) * 85) + 10
}
function formatDate(d) {
  if (!d) return '?'
  return new Date(d).toLocaleDateString('vi-VN', { day:'2-digit', month:'2-digit', year:'numeric' })
}
function isEventActive(e) {
  if (!e.startAt || !e.endAt) return false
  const now = new Date(), s = new Date(e.startAt), en = new Date(e.endAt)
  return now >= s && now <= en
}
async function fetchSystemStats() {
  loading.value = true
  try {
    const [ov, pm, um] = await Promise.allSettled([
      api.get('/admin/stats'),
      api.get('/admin/stats/posts-by-month'),
      api.get('/admin/stats/users-by-month'),
    ])
    if (ov.status === 'fulfilled') {
      const s = ov.value.data
      const vals = [s.totalUsers, s.totalPosts, s.pendingPosts, s.totalReports, s.activeUsers, s.premiumUsers, s.totalViews, s.newUsersToday]
      vals.forEach((v, i) => { overviewStats.value[i].value = v ?? 0 })
    }
    if (pm.status === 'fulfilled') {
      const raw = pm.value.data || []; const mx = Math.max(...raw.map(d => d.posts), 1)
      chartData.value = raw.map(d => ({ label: d.month, views: Math.round((d.posts/mx)*90), rawPosts: d.posts }))
    }
    if (um.status === 'fulfilled') {
      const raw = um.value.data || []; const mx = Math.max(...raw.map(d => d.users), 1)
      userChartData.value = raw.map(d => ({ label: d.month, height: Math.round((d.users/mx)*90), rawUsers: d.users }))
    }
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}
async function fetchUserStats() {
  if (Object.keys(userStatsData.value).length) return
  loadingUsers.value = true
  try { const { data } = await api.get('/admin/stats/users-detail'); userStatsData.value = data }
  catch (e) { console.error(e) }
  finally { loadingUsers.value = false }
}
async function fetchPostStats() {
  if (Object.keys(postStats.value).length) return
  loadingPosts.value = true
  try { const { data } = await api.get('/admin/stats/posts-detail'); postStats.value = data }
  catch (e) { console.error(e) }
  finally { loadingPosts.value = false }
}
async function fetchEventStats() {
  if (Object.keys(eventStats.value).length) return
  loadingEvents.value = true
  try { const { data } = await api.get('/admin/stats/events-detail'); eventStats.value = data }
  catch (e) { console.error(e) }
  finally { loadingEvents.value = false }
}
function onTabChange(tab) {
  if (tab === 'users')  fetchUserStats()
  if (tab === 'posts')  fetchPostStats()
  if (tab === 'events') fetchEventStats()
}
function refreshAll() {
  userStatsData.value = {}; postStats.value = {}; eventStats.value = {}
  fetchSystemStats(); onTabChange(activeTab.value)
}
onMounted(fetchSystemStats)
</script>
<style scoped>
.stats-page { padding: 24px; font-family: 'Mulish', sans-serif; color: #1E293B; min-height: 100%; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.page-title { font-size: 1.75rem; font-weight: 800; margin: 0; }
.page-sub { color: #64748B; margin: 4px 0 0; font-size: 0.9rem; }
.header-right { display: flex; gap: 10px; align-items: center; }
.range-select { padding: 9px 14px; border-radius: 10px; border: 1.5px solid #E2E8F0; font-family: inherit; font-size: 0.875rem; background: white; cursor: pointer; }
.btn-refresh { display: flex; align-items: center; gap: 6px; padding: 9px 16px; border: 1.5px solid #E2E8F0; border-radius: 10px; background: white; cursor: pointer; font-weight: 600; font-size: 0.875rem; transition: all 0.2s; }
.btn-refresh:hover { background: #F97316; color: white; border-color: #F97316; }
.btn-refresh:disabled { opacity: 0.5; cursor: not-allowed; }
.icon-refresh { font-size: 1.1rem; display: inline-block; }
.icon-refresh.spinning { animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.tab-bar { display: flex; gap: 6px; background: #F1F5F9; border-radius: 14px; padding: 6px; margin-bottom: 24px; width: fit-content; flex-wrap: wrap; }
.tab-btn { display: flex; align-items: center; gap: 6px; padding: 10px 20px; border-radius: 10px; border: none; background: transparent; cursor: pointer; font-weight: 600; font-size: 0.875rem; color: #64748B; transition: all 0.2s; font-family: inherit; }
.tab-btn.active { background: white; color: #EA580C; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
.kpi-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 24px; }
.kpi-grid.kpi-3 { grid-template-columns: repeat(3, 1fr); margin-bottom: 0; }
.kpi-card { background: white; border-radius: 14px; padding: 16px; display: flex; align-items: center; gap: 14px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); border: 1px solid #F1F5F9; border-top: 3px solid var(--accent, #94A3B8); transition: transform 0.2s, box-shadow 0.2s; }
.kpi-card:hover { transform: translateY(-3px); box-shadow: 0 6px 20px rgba(0,0,0,0.08); }
.kpi-icon-wrap { width: 42px; height: 42px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.kpi-emoji { font-size: 1.3rem; }
.kpi-val { font-size: 1.5rem; font-weight: 900; line-height: 1; }
.kpi-lbl { font-size: 0.78rem; color: #64748B; font-weight: 600; margin-top: 3px; }
.two-col { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.two-col .chart-card.wide { grid-column: span 2; }
.mt-20 { margin-top: 20px; }
.chart-card { background: white; border-radius: 16px; padding: 22px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); border: 1px solid #F1F5F9; }
.card-head { margin-bottom: 18px; }
.card-head h3 { margin: 0; font-size: 1rem; font-weight: 800; color: #1E293B; }
.state-empty { text-align: center; padding: 40px; color: #94A3B8; font-size: 0.9rem; }
.bar-chart { display: flex; align-items: flex-end; gap: 8px; height: 200px; padding: 0 4px; overflow-x: auto; }
.bar-wrap { display: flex; flex-direction: column; align-items: center; flex: 1; min-width: 28px; height: 100%; justify-content: flex-end; }
.bar-col { display: flex; flex-direction: column; align-items: center; width: 100%; justify-content: flex-end; height: 85%; }
.bar-tip { font-size: 0.72rem; font-weight: 700; color: #475569; margin-bottom: 3px; }
.bar-fill { width: 100%; border-radius: 6px 6px 0 0; transition: height 0.8s ease; }
.bar-fill.blue { background: linear-gradient(180deg, #60A5FA, #3B82F6); }
.bar-lbl { margin-top: 6px; font-size: 0.68rem; font-weight: 600; color: #94A3B8; text-align: center; }
.mini-bars { display: flex; flex-direction: column; gap: 10px; }
.mini-row { display: flex; align-items: center; gap: 10px; }
.mini-lbl { font-size: 0.78rem; font-weight: 700; color: #64748B; min-width: 38px; }
.mini-bg { flex: 1; height: 8px; border-radius: 10px; background: #F1F5F9; overflow: hidden; }
.mini-fill.purple { height: 100%; border-radius: 10px; background: linear-gradient(90deg, #818CF8, #6366F1); transition: width 0.8s ease; }
.mini-val { font-size: 0.8rem; font-weight: 700; color: #475569; min-width: 22px; text-align: right; }
.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 80px 20px; gap: 16px; color: #64748B; }
.spinner { width: 36px; height: 36px; border: 3px solid #E2E8F0; border-top-color: #EA580C; border-radius: 50%; animation: spin 0.7s linear infinite; }
.metric-tabs { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 18px; }
.metric-btn { padding: 8px 14px; border-radius: 10px; border: 1.5px solid #E2E8F0; background: white; cursor: pointer; font-size: 0.82rem; font-weight: 600; color: #64748B; transition: all 0.2s; font-family: inherit; }
.metric-btn.active { background: #EA580C; color: white; border-color: #EA580C; }
.rank-list { display: flex; flex-direction: column; gap: 10px; }
.rank-row { display: flex; align-items: center; gap: 12px; padding: 10px 14px; border-radius: 12px; background: #F8FAFC; transition: background 0.2s; }
.rank-row:hover { background: #F1F5F9; }
.rank-badge { width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 0.82rem; color: #475569; background: #E2E8F0; flex-shrink: 0; }
.rank-badge.rank-1 { background: linear-gradient(135deg,#FCD34D,#F59E0B); color:white; }
.rank-badge.rank-2 { background: linear-gradient(135deg,#CBD5E1,#94A3B8); color:white; }
.rank-badge.rank-3 { background: linear-gradient(135deg,#FDBA74,#EA580C); color:white; }
.rank-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; flex-shrink: 0; }
.rank-info { flex-shrink: 0; min-width: 110px; }
.rank-name { font-weight: 700; font-size: 0.88rem; }
.rank-bar-wrap { flex: 1; }
.rank-bar-bg { background: #E2E8F0; height: 8px; border-radius: 10px; overflow: hidden; }
.rank-bar-fill { height: 100%; border-radius: 10px; transition: width 0.8s ease; }
.rank-value { font-weight: 800; font-size: 0.95rem; white-space: nowrap; min-width: 80px; text-align: right; }
.rank-value small { font-size: 0.7rem; font-weight: 600; color: #94A3B8; margin-left: 2px; }
.level-bars { display: flex; flex-direction: column; gap: 14px; }
.level-row { display: flex; flex-direction: column; gap: 6px; }
.level-label { display: flex; justify-content: space-between; align-items: center; }
.level-badge { padding: 3px 10px; border-radius: 20px; font-weight: 700; font-size: 0.8rem; color: white; }
.lv-1 { background:#10B981; } .lv-2 { background:#3B82F6; } .lv-3 { background:#8B5CF6; } .lv-4 { background:#F59E0B; } .lv-5 { background:#EF4444; }
.level-count { font-size: 0.85rem; font-weight: 700; color: #475569; }
.level-bg { background: #F1F5F9; height: 10px; border-radius: 10px; overflow: hidden; }
.level-fill { height: 100%; border-radius: 10px; transition: width 0.8s ease; }
.post-rank-list { display: flex; flex-direction: column; gap: 10px; max-height: 320px; overflow-y: auto; }
.post-rank-row { display: flex; align-items: center; gap: 10px; padding: 10px 12px; border-radius: 12px; background: #F8FAFC; }
.post-rank-num { width: 24px; height: 24px; border-radius: 50%; background: #E2E8F0; color: #475569; font-weight: 800; font-size: 0.78rem; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.post-info { flex: 1; min-width: 0; }
.post-title { display: block; font-weight: 700; font-size: 0.875rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.post-author { font-size: 0.78rem; color: #94A3B8; }
.post-stars { display: flex; align-items: center; gap: 2px; white-space: nowrap; }
.star { color: #D1D5DB; font-size: 1rem; }
.star.on { color: #F59E0B; }
.star-val { font-weight: 700; font-size: 0.85rem; color: #F59E0B; margin-left: 4px; }
.post-stat { font-weight: 800; font-size: 0.9rem; white-space: nowrap; }
.post-stat.orange { color: #F97316; }
.post-stat.red { color: #EF4444; }
.event-table { overflow-x: auto; }
.event-table-head,.event-table-row { display: grid; grid-template-columns: 40px 1fr 160px 90px 100px 140px; gap: 10px; align-items: center; padding: 10px 12px; font-size: 0.85rem; }
.event-table-head { background: #F1F5F9; border-radius: 10px; font-weight: 800; color: #64748B; margin-bottom: 8px; }
.event-table-row { background: #F8FAFC; border-radius: 12px; margin-bottom: 6px; transition: background 0.2s; }
.event-table-row:hover { background: #F1F5F9; }
.e-rank { width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 0.8rem; background: #E2E8F0; color: #475569; }
.e-rank.rank-1 { background: linear-gradient(135deg,#FCD34D,#F59E0B); color:white; }
.e-rank.rank-2 { background: linear-gradient(135deg,#CBD5E1,#94A3B8); color:white; }
.e-rank.rank-3 { background: linear-gradient(135deg,#FDBA74,#EA580C); color:white; }
.e-date { color: #64748B; font-size: 0.78rem; }
.e-val { font-weight: 700; }
.e-val.orange { color: #F97316; }
.e-val.blue { color: #3B82F6; }
.e-status { font-size: 0.8rem; font-weight: 600; }
.e-status.active { color: #10B981; }
.e-status.ended { color: #94A3B8; }
@media (max-width: 1200px) { .kpi-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 1024px) { .two-col { grid-template-columns: 1fr; } .two-col .chart-card.wide { grid-column: span 1; } }
@media (max-width: 768px) { .kpi-grid { grid-template-columns: 1fr 1fr; } .event-table-head,.event-table-row { grid-template-columns: 36px 1fr 80px 80px; } }
</style>
