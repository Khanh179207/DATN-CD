<template>
  <div class="stats-page">
    <div class="page-header anim-fade-down">
      <div class="header-left">
        <div class="icon-box-lux">
          <Activity :size="28" stroke-width="2.5" />
        </div>
        <div>
          <h2 class="page-title">{{ t('admin.statistics.title') }}</h2>
          <p class="page-sub">{{ t('admin.statistics.subtitle') }}</p>
        </div>
      </div>
      <div class="header-right">
        <button class="btn-refresh" @click="refreshAll" :disabled="loading">
          <RefreshCcw :size="16" :class="{ 'spinning': loading }" />
          {{ t('admin.common.refresh') }}
        </button>
      </div>
    </div>

    <div class="tabs-lux-wrapper anim-fade-up" style="--delay: 0.1s">
      <div class="tab-bar">
        <button v-for="t in tabs" :key="t.key"
          :class="['tab-btn', { active: activeTab === t.key }]"
          @click="activeTab = t.key; onTabChange(t.key)">
          <component :is="t.icon" :size="16" />
          {{ t.label }}
        </button>
      </div>
    </div>

    <div class="tab-content-wrapper anim-fade-up" style="--delay: 0.2s">
      <transition name="fade-slide" mode="out-in">
        <div v-if="activeTab === 'system'" key="system" class="tab-pane">
          <div class="kpi-grid">
            <div v-for="s in systemStats" :key="s.label" class="kpi-card-v2" :style="{ '--accent': s.color }">
              <div class="kpi-icon-wrap-v2" :style="{ background: s.color + '18', color: s.color }">
                <component :is="s.icon" :size="24" />
              </div>
              <div class="kpi-info-v2">
                <div class="kpi-lbl-v2">{{ s.label }}</div>
                <div class="kpi-val-v2" :style="{ color: s.color }">
                  {{ s.value.toLocaleString('vi-VN') }}<small v-if="s.suffix">{{ s.suffix }}</small>
                </div>
              </div>
              <div class="kpi-decor" :style="{ background: s.color }"></div>
            </div>
          </div>

          <div class="system-charts-grid">
            <div class="chart-card">
              <div class="card-head-v2">
                <div class="head-left">
                  <div class="icon-box" style="background: #EFF6FF; color: #3B82F6"><TrendingUp :size="20"/></div>
                  <div>
                    <h3>{{ t('admin.statistics.post_growth_title') }}</h3>
                    <p>{{ t('admin.statistics.post_growth_subtitle') }}</p>
                  </div>
                </div>
                <span class="live-badge"><span class="pulse-dot"></span> {{ t('admin.statistics.live') }}</span>
              </div>
              <div v-if="loading" class="loading-box"><div class="spinner"></div></div>
              <div v-else class="chart-wrapper">
                <VueApexCharts type="area" height="320" :options="postChartOptions" :series="postChartSeries" />
              </div>
            </div>

            <div class="chart-card">
              <div class="card-head-v2">
                <div class="head-left">
                  <div class="icon-box" style="background: #FAF5FF; color: #8B5CF6"><Users :size="20"/></div>
                  <div>
                    <h3>{{ t('admin.statistics.new_users_title') }}</h3>
                    <p>{{ t('admin.statistics.new_users_subtitle') }}</p>
                  </div>
                </div>
              </div>
              <div v-if="loading" class="loading-box"><div class="spinner"></div></div>
              <div v-else class="chart-wrapper">
                <VueApexCharts type="bar" height="320" :options="userChartOptions" :series="userChartSeries" />
              </div>
            </div>
          </div>
        </div>

      <div v-else-if="activeTab === 'revenue'" key="revenue" class="tab-pane">
          <div class="kpi-grid grid-3">
            <div v-for="s in revenueStats" :key="s.label" class="kpi-card" :style="{ '--accent': s.color }">
              <div class="kpi-icon-wrap" :style="{ background: s.color + '18', color: s.color }">
                <component :is="s.icon" :size="24" />
              </div>
              <div class="kpi-info">
                <div class="kpi-val" :style="{ color: s.color }">
                  {{ s.value.toLocaleString('vi-VN') }}<small v-if="s.suffix">{{ s.suffix }}</small>
                </div>
                <div class="kpi-lbl">{{ s.label }}</div>
              </div>
              <div class="kpi-bg-glow" :style="{ background: s.color }"></div>
            </div>
          </div>

          <div class="chart-card">
            <div class="card-head">
              <h3><Banknote :size="18" style="color: #10B981"/> {{ t('admin.statistics.revenue_chart_title') }}</h3>
            </div>
            <div v-if="loadingRevenue" class="loading-box"><div class="spinner"></div></div>
            <div v-else>
              <VueApexCharts type="area" height="380" :options="revenueChartOptions" :series="revenueChartSeries" />
            </div>
          </div>
        </div>

      <div v-else-if="activeTab === 'content'" key="content" class="tab-pane">
          <div class="kpi-grid">
            <div v-for="s in contentStats" :key="s.label" class="kpi-card" :style="{ '--accent': s.color }">
              <div class="kpi-icon-wrap" :style="{ background: s.color + '18', color: s.color }">
                <component :is="s.icon" :size="24" />
              </div>
              <div class="kpi-info">
                <div class="kpi-val" :style="{ color: s.color }">
                  {{ s.value.toLocaleString('vi-VN') }}
                </div>
                <div class="kpi-lbl">{{ s.label }}</div>
              </div>
              <div class="kpi-bg-glow" :style="{ background: s.color }"></div>
            </div>
          </div>

          <div class="two-col">
            <div class="chart-card">
              <div class="card-head">
                <h3><Layout :size="18" style="color: #F97316"/> {{ t('admin.statistics.category_distribution_title') }}</h3>
              </div>
              <div v-if="loadingContent" class="loading-box"><div class="spinner"></div></div>
              <VueApexCharts v-else type="radar" height="350" :options="categoryChartOptions" :series="categoryChartSeries" />
            </div>
            <div class="chart-card">
              <div class="card-head">
                <h3><MessageSquare :size="18" style="color: #3B82F6"/> {{ t('admin.statistics.community_engagement_title') }}</h3>
              </div>
              <div v-if="loadingContent" class="loading-box"><div class="spinner"></div></div>
              <VueApexCharts v-else type="bar" height="350" :options="engagementChartOptions" :series="engagementChartSeries" />
            </div>
          </div>
        </div>

      <div v-else-if="activeTab === 'ranking'" key="ranking" class="tab-pane">
          <div v-if="loadingRanking" class="loading-state"><div class="spinner"></div><p>{{ t('admin.statistics.compiling_data') }}</p></div>
          <div v-else class="ranking-layout">
            
            <div class="chart-card podium-card">
              <div class="card-head-v2">
                <div class="head-left">
                  <div class="icon-box" style="background: #FEF9C3; color: #EAB308"><Trophy :size="20"/></div>
                  <div>
                    <h3>{{ t('admin.statistics.top_chefs_title') }}</h3>
                    <p>{{ t('admin.statistics.top_chefs_subtitle') }}</p>
                  </div>
                </div>
              </div>

              <div v-if="!topChefs.length" class="state-empty">{{ t('admin.statistics.not_enough_ranking_data') }}</div>
              <div v-else class="podium-area">
                <div class="podium-container" v-if="topChefs.length >= 3">
                  <!-- Rank 2 -->
                  <div class="podium-item rank-2">
                    <div class="avatar-wrap">
                      <img :src="topChefs[1].avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(topChefs[1].name)}&background=CBD5E1&color=fff`" />
                      <div class="medal silver">🥈</div>
                    </div>
                    <div class="p-name">{{ topChefs[1].name }}</div>
                    <div class="p-score">{{ t('admin.statistics.posts_count', { count: topChefs[1].postCount }) }}</div>
                    <div class="pedestal step-2"><span>2</span></div>
                  </div>
                  <!-- Rank 1 -->
                  <div class="podium-item rank-1">
                    <div class="avatar-wrap">
                      <img :src="topChefs[0].avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(topChefs[0].name)}&background=EAB308&color=fff`" />
                      <div class="medal gold">👑</div>
                    </div>
                    <div class="p-name">{{ topChefs[0].name }}</div>
                    <div class="p-score">{{ t('admin.statistics.posts_count', { count: topChefs[0].postCount }) }}</div>
                    <div class="pedestal step-1"><span>1</span></div>
                  </div>
                  <!-- Rank 3 -->
                  <div class="podium-item rank-3">
                    <div class="avatar-wrap">
                      <img :src="topChefs[2].avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(topChefs[2].name)}&background=FDBA74&color=fff`" />
                      <div class="medal bronze">🥉</div>
                    </div>
                    <div class="p-name">{{ topChefs[2].name }}</div>
                    <div class="p-score">{{ t('admin.statistics.posts_count', { count: topChefs[2].postCount }) }}</div>
                    <div class="pedestal step-3"><span>3</span></div>
                  </div>
                </div>

                <div class="other-ranks" v-if="topChefs.length > 3">
                  <div v-for="(u, i) in topChefs.slice(3)" :key="u.name" class="rank-list-item">
                    <span class="r-num">{{ i + 4 }}</span>
                    <img :src="u.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(u.name)}&background=F1F5F9&color=475569`" class="r-ava" />
                    <span class="r-name">{{ u.name }}</span>
                    <span class="r-score">{{ t('admin.statistics.posts_count', { count: u.postCount }) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="chart-card">
              <div class="card-head-v2">
                <div class="head-left">
                  <div class="icon-box" style="background: #ECFEFF; color: #06B6D4"><Sparkles :size="20"/></div>
                  <div>
                    <h3>{{ t('admin.statistics.top_posts_title') }}</h3>
                    <p>{{ t('admin.statistics.top_posts_subtitle') }}</p>
                  </div>
                </div>
              </div>
              <div v-if="!topPosts.length" class="state-empty">{{ t('admin.statistics.not_enough_ranking_data') }}</div>
              <div v-else class="masterpiece-list">
                <div v-for="(p, i) in topPosts" :key="p.id" class="mp-item" :class="'mp-rank-' + (i+1)">
                  <div class="mp-rank-num">0{{ i + 1 }}</div>
                  <div class="mp-info">
                    <h4 class="mp-title" :title="p.title">{{ p.title }}</h4>
                    <span class="mp-author">
                      <Star :size="12" style="color: #F59E0B" v-if="i===0"/>
                      {{ t('admin.statistics.chef_label') }} <b>@{{ p.author }}</b>
                    </span>
                  </div>
                  <div class="mp-views">
                    <span class="v-count">{{ p.views.toLocaleString('vi-VN') }}</span>
                    <span class="v-lbl">{{ t('admin.statistics.views_label') }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

      <div v-else-if="activeTab === 'security'" key="security" class="tab-pane">
          <div class="kpi-grid">
            <div v-for="s in securityStats" :key="s.label" class="kpi-card" :style="{ '--accent': s.color }">
              <div class="kpi-icon-wrap" :style="{ background: s.color + '18', color: s.color }">
                <component :is="s.icon" :size="24" />
              </div>
              <div class="kpi-info">
                <div class="kpi-val" :style="{ color: s.color }">
                  {{ s.value.toLocaleString('vi-VN') }}
                </div>
                <div class="kpi-lbl">{{ s.label }}</div>
              </div>
              <div class="kpi-bg-glow" :style="{ background: s.color }"></div>
            </div>
          </div>

          <div class="two-col">
            <div class="chart-card">
              <div class="card-head"><h3><ShieldAlert :size="18" style="color: #EF4444" /> {{ t('admin.statistics.ticket_breakdown_title') }}</h3></div>
              <div v-if="loadingTickets" class="loading-box"><div class="spinner"></div></div>
              <div v-else-if="!ticketStats.length" class="state-empty">{{ t('admin.statistics.safe_empty_state') }}</div>
              <div v-else>
                 <VueApexCharts type="donut" height="350" :options="ticketChartOptions" :series="ticketChartSeries" />
              </div>
            </div>
            
            <div class="chart-card">
              <div class="card-head"><h3><Flag :size="18" style="color: #F59E0B" /> {{ t('admin.statistics.appeals_status_title') }}</h3></div>
              <div v-if="loadingSecurity" class="loading-box"><div class="spinner"></div></div>
              <VueApexCharts v-else type="bar" height="350" :options="appealsChartOptions" :series="appealsChartSeries" />
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  Activity, RefreshCcw, BarChart2, Users, FileText, AlertTriangle, 
  Diamond, Eye, TrendingUp, Trophy, Banknote, ShieldAlert, 
  Layout, MessageSquare, Calendar, CreditCard, Flag, Heart, Sparkles, Star
} from 'lucide-vue-next'
import VueApexCharts from 'vue3-apexcharts'
import api from '@/services/api' 

const { t } = useI18n()
const activeTab = ref('system')
const loading = ref(false)
const loadingRanking = ref(false)
const loadingTickets = ref(false)
const loadingRevenue = ref(false)
const loadingContent = ref(false)
const loadingSecurity = ref(false)

const tabs = [
  { key: 'system', icon: BarChart2, label: t('admin.statistics.tabs.system') },
  { key: 'revenue', icon: Banknote, label: t('admin.statistics.tabs.revenue') },
  { key: 'content', icon: Layout, label: t('admin.statistics.tabs.content') },
  { key: 'ranking', icon: Trophy, label: t('admin.statistics.tabs.ranking') },
  { key: 'security', icon: ShieldAlert, label: t('admin.statistics.tabs.security') }
]

// --- KPI DATA ---
const systemStats = ref([
  { key: 'totalUsers', label: t('admin.statistics.kpi.total_users'), value: 0, icon: Users, color: '#3B82F6' },
  { key: 'totalPosts', label: t('admin.statistics.kpi.total_posts'), value: 0, icon: FileText, color: '#F97316' },
  { key: 'premiumUsers', label: t('admin.statistics.kpi.premium_users'), value: 0, icon: Diamond, color: '#8B5CF6' },
  { key: 'totalViews', label: t('admin.statistics.kpi.total_views'), value: 0, icon: Eye, color: '#06B6D4' },
])

const revenueStats = ref([
  { key: 'revenue', label: t('admin.statistics.kpi.revenue'), value: 0, icon: Banknote, color: '#10B981', suffix: ' ₫' },
  { key: 'transactions', label: t('admin.statistics.kpi.transactions'), value: 0, icon: CreditCard, color: '#3B82F6' },
  { key: 'refunds', label: t('admin.statistics.kpi.refunds'), value: 0, icon: AlertTriangle, color: '#EF4444' },
])

const contentStats = ref([
  { key: 'categories', label: t('admin.statistics.kpi.categories'), value: 0, icon: Layout, color: '#8B5CF6' },
  { key: 'comments', label: t('admin.statistics.kpi.comments'), value: 0, icon: MessageSquare, color: '#06B6D4' },
  { key: 'events', label: t('admin.statistics.kpi.events'), value: 0, icon: Calendar, color: '#F97316' },
  { key: 'totalLikes', label: t('admin.statistics.kpi.total_likes'), value: 0, icon: Heart, color: '#E42256' },
])

const securityStats = ref([
  { key: 'totalTickets', label: t('admin.statistics.kpi.total_tickets'), value: 0, icon: AlertTriangle, color: '#EF4444' },
  { key: 'appeals', label: t('admin.statistics.kpi.appeals'), value: 0, icon: Flag, color: '#F59E0B' },
  { key: 'blacklist', label: t('admin.statistics.kpi.blacklist'), value: 0, icon: ShieldAlert, color: '#10B981' },
  { key: 'banned', label: t('admin.statistics.kpi.banned'), value: 0, icon: Users, color: '#64748B' },
])

const topChefs = ref([])
const topPosts = ref([])
const ticketStats = ref([])

// ================= CẤU HÌNH APEXCHARTS ĐÃ ĐƯỢC LÀM ĐẸP (POLISHED) =================

// 1. Biểu đồ Bài viết (Area Chart)
const postChartSeries = ref([{ name: t('admin.statistics.series.posts'), data: [] }])
const postChartOptions = ref({
  chart: { type: 'area', toolbar: { show: false }, dropShadow: { enabled: true, top: 4, left: 0, blur: 4, opacity: 0.1 } },
  colors: ['#3B82F6'],
  dataLabels: { enabled: false },
  stroke: { curve: 'smooth', width: 4 },
  fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.45, opacityTo: 0.05, stops: [0, 100] } },
  xaxis: { categories: [], axisBorder: { show: false }, axisTicks: { show: false } },
  yaxis: { labels: { formatter: (val) => Math.round(val) } },
  grid: { borderColor: '#f1f5f9', strokeDashArray: 4 },
  tooltip: { theme: 'light' }
})

// 2. Biểu đồ User mới (Bar Chart)
const userChartSeries = ref([{ name: t('admin.statistics.series.new_users'), data: [] }])
const userChartOptions = ref({
  chart: { type: 'bar', toolbar: { show: false } },
  colors: ['#8B5CF6'],
  plotOptions: { bar: { borderRadius: 6, columnWidth: '40%' } },
  dataLabels: { enabled: false }, // Tắt bớt số trên đỉnh cột cho đỡ rối
  xaxis: { categories: [], axisBorder: { show: false }, axisTicks: { show: false } },
  grid: { borderColor: '#f1f5f9', strokeDashArray: 4 },
  tooltip: { theme: 'light' }
})

// 🔥 3. BIỂU ĐỒ DOANH THU (Real Data Area Chart)
const revenueChartSeries = ref([{ name: t('admin.statistics.series.revenue'), data: [] }])
const revenueChartOptions = ref({
  chart: { type: 'area', toolbar: { show: false }, dropShadow: { enabled: true, top: 5, left: 0, blur: 5, opacity: 0.15 } },
  colors: ['#10B981'],
  dataLabels: { enabled: false },
  stroke: { curve: 'smooth', width: 4 },
  fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.5, opacityTo: 0.01, stops: [0, 100] } },
  xaxis: { categories: ['Th1', 'Th2', 'Th3', 'Th4', 'Th5', 'Th6', 'Th7', 'Th8', 'Th9', 'Th10', 'Th11', 'Th12'], axisBorder: { show: false }, axisTicks: {show: false} },
  yaxis: { labels: { formatter: (val) => val > 0 ? (val / 1000).toLocaleString('vi-VN') + 'k' : '0' } }, // Format 100k, 1M cho gọn
  grid: { borderColor: '#f1f5f9', strokeDashArray: 4 },
  tooltip: { 
    theme: 'light', 
    y: { formatter: (val) => val.toLocaleString('vi-VN') + ' VNĐ' } 
  }
})

// 4. Biểu đồ Phân bổ Danh mục (Radar Chart - Real Data)
const categoryChartSeries = ref([{ name: t('admin.statistics.series.posts'), data: [] }])
const categoryChartOptions = ref({
  chart: { type: 'radar', toolbar: { show: false }, dropShadow: { enabled: true, blur: 3, left: 1, top: 1, opacity: 0.1 } },
  labels: [],
  colors: ['#F97316'],
  stroke: { width: 2 },
  fill: { opacity: 0.2 },
  markers: { size: 5, colors: ['#fff'], strokeColors: '#F97316', strokeWidth: 2 }
})

// 5. Biểu đồ Tương tác (Stacked Bar - Real Data)
const engagementChartSeries = ref([])
const engagementChartOptions = ref({
  chart: { type: 'bar', stacked: true, toolbar: { show: false } },
  colors: ['#E42256', '#3B82F6'],
  plotOptions: { bar: { borderRadius: 4, columnWidth: '45%' } },
  xaxis: { categories: ['Th2', 'Th3', 'Th4', 'Th5', 'Th6', 'Th7', 'Th8'], axisBorder: { show: false } },
  legend: { position: 'top', horizontalAlign: 'right' },
  dataLabels: { enabled: false },
  grid: { borderColor: '#f1f5f9', strokeDashArray: 4 }
})

// 6. Biểu đồ Ticket (Donut Chart - Real Data)
const ticketChartSeries = ref([])
const ticketChartOptions = ref({
  chart: { type: 'donut' },
  labels: [],
  colors: ['#EF4444', '#F59E0B', '#3B82F6', '#10B981', '#8B5CF6'],
  plotOptions: { donut: { size: '75%', labels: { show: true, name: { show: true }, value: { show: true, fontSize: '24px', fontWeight: 800 }, total: { show: true, label: t('admin.statistics.total_tickets_label') } } } },
  legend: { position: 'bottom', horizontalAlign: 'center' },
  stroke: { width: 2, colors: ['#ffffff'] },
  dataLabels: { enabled: false } // Đã tắt datalabels trên miếng bánh cho sang trọng
})

// 7. Biểu đồ Appeals (Bar Chart - Real Data)
const appealsChartSeries = ref([{ name: t('admin.statistics.series.appeals'), data: [] }])
const appealsChartOptions = ref({
  chart: { type: 'bar', toolbar: { show: false } },
  colors: ['#F59E0B', '#3B82F6', '#10B981', '#EF4444'],
  plotOptions: { bar: { borderRadius: 6, columnWidth: '40%', distributed: true } },
  xaxis: { categories: [], axisBorder: { show: false } },
  dataLabels: { enabled: false },
  legend: { show: false },
  grid: { borderColor: '#f1f5f9', strokeDashArray: 4 }
})

function barWidth(list, value) {
  const max = Math.max(...list.map(u => Number(u.postCount)), 1)
  return Math.round((Number(value) / max) * 80) + 10
}

// ================= FETCH DATA TỪ BE =================
async function fetchSystemStats() {
  loading.value = true
  try {
    const [overviewRes, growthRes] = await Promise.allSettled([
      api.get('/api/admin/stats/overview'),
      api.get('/api/admin/stats/growth')
    ])

    if (overviewRes.status === 'fulfilled') {
      const s = overviewRes.value.data
      systemStats.value.forEach(stat => { if(s[stat.key] !== undefined) stat.value = s[stat.key] })
      revenueStats.value.forEach(stat => { if(s[stat.key] !== undefined) stat.value = s[stat.key] })
      contentStats.value.forEach(stat => { if(s[stat.key] !== undefined) stat.value = s[stat.key] })
      securityStats.value.forEach(stat => { if(s[stat.key] !== undefined) stat.value = s[stat.key] })
    }

    if (growthRes.status === 'fulfilled') {
      const { labels, posts, users } = growthRes.value.data
      const shortLabels = labels.map(lbl => lbl.replace('Tháng ', 'T'))
      postChartSeries.value = [{ name: t('admin.statistics.series.posts'), data: posts }]
      postChartOptions.value = { ...postChartOptions.value, xaxis: { categories: shortLabels } }
      userChartSeries.value = [{ name: t('admin.statistics.series.users'), data: users }]
      userChartOptions.value = { ...userChartOptions.value, xaxis: { categories: shortLabels } }
    }
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

async function fetchRevenueChart() {
  loadingRevenue.value = true
  try {
    const { data } = await api.get('/api/admin/stats/revenue-chart')
    // Data từ BE trả về dạng mảng [0, 50000, 100000, ...] (12 tháng)
    revenueChartSeries.value = [{ name: t('admin.statistics.series.revenue'), data: data }]
  } catch (e) { console.error("Lỗi lấy data doanh thu", e) }
  finally { loadingRevenue.value = false }
}

async function fetchRankingStats() {
  if (topChefs.value.length) return
  loadingRanking.value = true
  try { 
    const { data } = await api.get('/api/admin/stats/top-ranking')
    topChefs.value = data.topChefs || []
    topPosts.value = data.topPosts || []
  }
  catch (e) { console.error(e) }
  finally { loadingRanking.value = false }
}

async function fetchTicketStats() {
  if (ticketStats.value.length) return
  loadingTickets.value = true
  try { 
    const { data } = await api.get('/api/admin/stats/tickets-summary')
    const keys = Object.keys(data)
    const values = Object.values(data)
    
    ticketStats.value = keys.map((k, i) => ({ type: k, count: values[i] }))
    ticketChartSeries.value = values
    ticketChartOptions.value = { ...ticketChartOptions.value, labels: keys }
  }
  catch (e) { console.error(e) }
  finally { loadingTickets.value = false }
}

async function fetchContentCharts() {
  if (categoryChartSeries.value[0].data.length > 0) return
  loadingContent.value = true
  try {
    const [catRes, engRes] = await Promise.allSettled([
      api.get('/api/admin/stats/category-distribution'),
      api.get('/api/admin/stats/engagement')
    ])
    if (catRes.status === 'fulfilled') {
      categoryChartOptions.value = { ...categoryChartOptions.value, labels: catRes.value.data.labels }
      categoryChartSeries.value = [{ name: t('admin.statistics.series.posts'), data: catRes.value.data.data }]
    }
    if (engRes.status === 'fulfilled') {
      engagementChartOptions.value = { ...engagementChartOptions.value, xaxis: { categories: engRes.value.data.labels, axisBorder: {show:false} } }
      engagementChartSeries.value = [
        { name: t('admin.statistics.series.likes'), data: engRes.value.data.likes },
        { name: t('admin.statistics.series.comments'), data: engRes.value.data.comments }
      ]
    }
  } catch (e) { console.error("Lỗi data content", e) }
  finally { loadingContent.value = false }
}

async function fetchSecurityCharts() {
  if (appealsChartSeries.value[0].data.length > 0) return
  loadingSecurity.value = true
  try {
    const { data } = await api.get('/api/admin/stats/appeals-chart')
    
    // Lọc bỏ các cột có nhãn liên quan đến trạng thái chờ/đang duyệt
    const filteredLabels = []
    const filteredData = []
    
    data.labels.forEach((label, index) => {
      const lowerLabel = String(label).toLowerCase()
      if (!lowerLabel.includes('chờ duyệt') && !lowerLabel.includes('đang duyệt') && !lowerLabel.includes('pending')) {
        filteredLabels.push(label)
        filteredData.push(data.data[index])
      }
    })

    appealsChartOptions.value = { ...appealsChartOptions.value, xaxis: { categories: filteredLabels, axisBorder: {show:false} } }
    appealsChartSeries.value = [{ name: t('admin.statistics.series.appeals'), data: filteredData }]
  } catch (e) { console.error(e) }
  finally { loadingSecurity.value = false }
}

function onTabChange(tab) {
  if (tab === 'revenue' && revenueChartSeries.value[0].data.length === 0) fetchRevenueChart()
  if (tab === 'content') fetchContentCharts()
  if (tab === 'ranking') fetchRankingStats()
  if (tab === 'security') {
    fetchTicketStats()
    fetchSecurityCharts()
  }
}

function refreshAll() {
  topChefs.value = []; topPosts.value = []; ticketStats.value = []; 
  revenueChartSeries.value[0].data = []; categoryChartSeries.value[0].data = []; 
  engagementChartSeries.value = []; appealsChartSeries.value[0].data = [];
  fetchSystemStats(); onTabChange(activeTab.value)
}

onMounted(() => {
  fetchSystemStats()
  if (activeTab.value === 'revenue') fetchRevenueChart()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;500;600;700;800;900&display=swap');

.stats-page { padding: 32px; font-family: 'Mulish', sans-serif; color: #1E293B; min-height: 100vh; background: transparent; }

/* Header */
.page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 28px; flex-wrap: wrap; gap: 16px; }
.header-left { display: flex; align-items: center; gap: 16px; }
.icon-box-lux { width: 56px; height: 56px; background: linear-gradient(135deg, #10B981, #3B82F6); color: white; border-radius: 16px; display: flex; align-items: center; justify-content: center; box-shadow: 0 10px 25px -5px rgba(16, 185, 129, 0.4); }
.page-title { font-size: 2rem; font-weight: 900; margin: 0 0 6px; letter-spacing: -0.5px; color: #0F172A; }
.page-sub { color: #64748B; margin: 0; font-size: 0.95rem; font-weight: 500; }
.header-right { display: flex; gap: 10px; align-items: center; }
.btn-refresh { display: flex; align-items: center; gap: 8px; padding: 10px 20px; border: 1px solid #E2E8F0; border-radius: 12px; background: white; cursor: pointer; font-weight: 700; font-size: 0.9rem; color: #475569; transition: all 0.3s; box-shadow: 0 2px 8px rgba(0,0,0,0.02); }
.btn-refresh:hover { background: #F1F5F9; color: #0F172A; border-color: #CBD5E1; transform: translateY(-2px); }
.btn-refresh:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }
.spinning { animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* Tabs */
.tabs-lux-wrapper { margin-bottom: 28px; display: flex; }
.tab-bar { display: inline-flex; background: rgba(255, 255, 255, 0.7); backdrop-filter: blur(10px); padding: 6px; border-radius: 16px; border: 1px solid #E2E8F0; box-shadow: 0 4px 15px rgba(0,0,0,0.02); gap: 4px; overflow-x: auto; }
.tab-btn { display: flex; align-items: center; gap: 8px; padding: 10px 22px; border-radius: 12px; border: none; background: transparent; cursor: pointer; font-weight: 700; font-size: 0.9rem; color: #64748B; transition: all 0.3s; font-family: inherit; white-space: nowrap; }
.tab-btn:hover:not(.active) { color: #0F172A; background: rgba(241, 245, 249, 0.5); }
.tab-btn.active { background: white; color: #3B82F6; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }

/* Transitions */
.tab-content-wrapper { position: relative; }
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); }
.fade-slide-enter-from { opacity: 0; transform: translateY(20px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-20px); position: absolute; width: 100%; }

/* KPI Grid */
.kpi-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 24px; }
.kpi-grid.grid-3 { grid-template-columns: repeat(3, 1fr); } /* Căn chỉnh lại cho 3 thẻ Doanh Thu */
.kpi-card { background: white; border-radius: 20px; padding: 22px; display: flex; align-items: center; gap: 18px; box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05); border: 1px solid rgba(255,255,255,0.8); position: relative; overflow: hidden; transition: all 0.3s; cursor: default; }
.kpi-card:hover { transform: translateY(-4px); box-shadow: 0 20px 40px -10px rgba(0,0,0,0.08); }
.kpi-bg-glow { position: absolute; top: -50px; right: -50px; width: 100px; height: 100px; border-radius: 50%; opacity: 0.15; filter: blur(30px); pointer-events: none; transition: 0.3s; }
.kpi-card:hover .kpi-bg-glow { transform: scale(1.5); }
.kpi-icon-wrap { width: 54px; height: 54px; border-radius: 14px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; z-index: 2; position: relative; backdrop-filter: blur(5px); }
.kpi-info { z-index: 2; position: relative; }
.kpi-val { font-size: 1.6rem; font-weight: 900; line-height: 1.2; letter-spacing: -0.5px; }
.kpi-val small { font-size: 1rem; font-weight: 600; }
.kpi-lbl { font-size: 0.85rem; color: #64748B; font-weight: 700; margin-top: 4px; }

/* --- NEW SYSTEM TAB STYLES --- */
.kpi-card-v2 { background: white; border-radius: 20px; padding: 24px; display: flex; align-items: flex-start; gap: 16px; box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05); border: 1px solid #F1F5F9; position: relative; overflow: hidden; transition: 0.3s; }
.kpi-card-v2:hover { transform: translateY(-4px); box-shadow: 0 20px 40px -10px rgba(0,0,0,0.08); border-color: var(--accent); }
.kpi-icon-wrap-v2 { width: 48px; height: 48px; border-radius: 14px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; z-index: 2; position: relative; }
.kpi-info-v2 { z-index: 2; position: relative; flex: 1; }
.kpi-lbl-v2 { font-size: 0.8rem; color: #64748B; font-weight: 700; margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.5px; }
.kpi-val-v2 { font-size: 1.8rem; font-weight: 900; line-height: 1; letter-spacing: -0.5px; }
.kpi-val-v2 small { font-size: 1rem; font-weight: 600; margin-left: 4px; }
.kpi-decor { position: absolute; bottom: 0; right: 0; width: 80px; height: 80px; opacity: 0.05; border-radius: 50%; transform: translate(30%, 30%); pointer-events: none; }

.system-charts-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 24px; }
.card-head-v2 { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; }
.head-left { display: flex; gap: 14px; align-items: center; }
.head-left .icon-box { width: 42px; height: 42px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.head-left h3 { margin: 0 0 4px; font-size: 1.15rem; font-weight: 800; color: #0F172A; }
.head-left p { margin: 0; font-size: 0.85rem; color: #64748B; font-weight: 600; }
.live-badge { display: flex; align-items: center; gap: 6px; background: #ECFDF5; color: #0891B2; padding: 6px 12px; border-radius: 100px; font-size: 0.75rem; font-weight: 800; text-transform: uppercase; }
.pulse-dot { width: 6px; height: 6px; background: #06B6D4; border-radius: 50%; box-shadow: 0 0 0 0 rgba(6, 182, 212, 0.4); animation: pulseDot 2s infinite; }
@keyframes pulseDot { 70% { box-shadow: 0 0 0 6px rgba(6, 182, 212, 0); } 100% { box-shadow: 0 0 0 0 rgba(6, 182, 212, 0); } }

/* --- NEW RANKING TAB STYLES --- */
.ranking-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
@media (max-width: 1200px) { .ranking-layout, .system-charts-grid { grid-template-columns: 1fr; } }
.podium-card { background: linear-gradient(to bottom, #ffffff, #fdfdfd); }

/* Podium */
.podium-area { display: flex; flex-direction: column; height: 100%; justify-content: flex-end; padding-top: 10px;}
.podium-container { display: flex; justify-content: center; align-items: flex-end; gap: 12px; margin-bottom: 24px; height: 210px; }
.podium-item { display: flex; flex-direction: column; align-items: center; width: 30%; position: relative; }
.avatar-wrap { position: relative; margin-bottom: 12px; z-index: 2; }
.avatar-wrap img { width: 60px; height: 60px; border-radius: 50%; object-fit: cover; border: 3px solid white; box-shadow: 0 8px 16px rgba(0,0,0,0.1); background: white; }
.rank-1 .avatar-wrap img { width: 76px; height: 76px; border-color: #FBBF24; box-shadow: 0 10px 25px rgba(251, 191, 36, 0.4); }
.medal { position: absolute; bottom: -8px; left: 50%; transform: translateX(-50%); font-size: 1.2rem; background: white; border-radius: 50%; padding: 2px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.p-name { font-weight: 800; font-size: 0.9rem; color: #0F172A; text-align: center; margin-bottom: 4px; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.rank-1 .p-name { font-size: 1.05rem; color: #B45309; }
.p-score { font-size: 0.8rem; color: #64748B; font-weight: 600; margin-bottom: 12px; }
.rank-1 .p-score { color: #D97706; font-weight: 700; }

.pedestal { width: 100%; background: #F1F5F9; border-radius: 12px 12px 0 0; display: flex; justify-content: center; padding-top: 12px; position: relative; overflow: hidden; }
.pedestal span { font-size: 2.5rem; font-weight: 900; color: rgba(255,255,255,0.6); line-height: 1; }
.step-2 { height: 80px; background: linear-gradient(180deg, #E2E8F0, #CBD5E1); }
.step-1 { height: 110px; background: linear-gradient(180deg, #FDE68A, #F59E0B); }
.step-3 { height: 60px; background: linear-gradient(180deg, #FFEDD5, #FDBA74); }

.other-ranks { display: flex; flex-direction: column; gap: 8px; border-top: 1px dashed #E2E8F0; padding-top: 20px; }
.rank-list-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; background: #F8FAFC; border-radius: 12px; transition: 0.2s; }
.rank-list-item:hover { background: white; box-shadow: 0 4px 12px rgba(0,0,0,0.04); }
.r-num { width: 24px; font-weight: 800; color: #94A3B8; }
.r-ava { width: 36px; height: 36px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.r-name { flex: 1; font-weight: 700; font-size: 0.95rem; color: #1E293B; }
.r-score { font-size: 0.85rem; font-weight: 700; color: #64748B; }

/* Masterpiece (Top Posts) */
.masterpiece-list { display: flex; flex-direction: column; gap: 14px; }
.mp-item { display: flex; align-items: center; gap: 16px; padding: 18px; border-radius: 16px; border: 1px solid #F1F5F9; background: white; transition: 0.3s; position: relative; overflow: hidden; }
.mp-item:hover { border-color: #06B6D4; transform: translateX(4px); box-shadow: 0 10px 20px -5px rgba(6, 182, 212, 0.1); }
.mp-rank-num { font-family: 'Mulish', sans-serif; font-size: 2.2rem; font-weight: 900; color: #E2E8F0; width: 44px; text-align: center; transition: 0.3s; }
.mp-item:hover .mp-rank-num { color: #06B6D4; opacity: 0.3; }
.mp-info { flex: 1; min-width: 0; }
.mp-title { margin: 0 0 6px 0; font-size: 1.05rem; font-weight: 800; color: #0F172A; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.mp-author { font-size: 0.85rem; color: #64748B; display: flex; align-items: center; gap: 6px; }
.mp-author b { color: #334155; }
.mp-views { text-align: right; display: flex; flex-direction: column; }
.v-count { font-size: 1.2rem; font-weight: 900; color: #0891B2; }
.v-lbl { font-size: 0.7rem; font-weight: 700; text-transform: uppercase; color: #94A3B8; margin-top: 2px; }

.mp-rank-1 { background: linear-gradient(to right, #ECFEFF, white); border-color: #A5F3FC; }
.mp-rank-1 .mp-rank-num { color: #0891B2; }
.mp-rank-1 .mp-title { color: #164E63; }

/* Charts & Layout */
.two-col { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.two-col .chart-card.wide { grid-column: span 2; }
.chart-card { background: white; border-radius: 24px; padding: 28px; box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05); border: 1px solid rgba(255,255,255,0.9); display: flex; flex-direction: column; }
.card-head { margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; }
.card-head h3 { margin: 0; font-size: 1.15rem; font-weight: 800; color: #0F172A; display: flex; align-items: center; gap: 10px; }
.card-tag { background: #F1F5F9; color: #475569; padding: 4px 12px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; }
.state-empty { text-align: center; padding: 60px 20px; color: #94A3B8; font-size: 0.95rem; font-weight: 600; background: #F8FAFC; border-radius: 16px; margin-top: auto; margin-bottom: auto; }
.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 100px 20px; gap: 16px; color: #64748B; font-weight: 600; }
.loading-box { display: flex; justify-content: center; padding: 60px; }
.spinner { width: 40px; height: 40px; border: 4px solid #E2E8F0; border-top-color: #3B82F6; border-radius: 50%; animation: spin 0.8s linear infinite; }

/* Animations */
.anim-fade-down { animation: fadeDown 0.6s cubic-bezier(0.16, 1, 0.3, 1); }
.anim-fade-up { animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) both; animation-delay: var(--delay, 0s); }
@keyframes fadeDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

/* Responsive */
@media (max-width: 1200px) { .kpi-grid { grid-template-columns: repeat(2, 1fr); } .kpi-grid.grid-3 { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .kpi-grid { grid-template-columns: 1fr; } .kpi-grid.grid-3 { grid-template-columns: 1fr; } .page-header { flex-direction: column; align-items: flex-start; } .tab-bar { flex-wrap: wrap; } }
</style>