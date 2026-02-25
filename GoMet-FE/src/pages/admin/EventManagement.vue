<template>
  <div class="gomet-admin-pro">
    
    <div class="header-zone">
      <div class="header-content">
        <h1 class="page-title">Event & Workshop Management</h1>
        <p class="sub-text">Create world-class culinary experiences</p>
      </div>
      <button class="btn-create-mega" @click="openCreateModal">
        <span class="icon-plus">+</span> Create New Event
      </button>
    </div>

    <div class="stats-bar">
      <div class="stat-item">
        <span class="s-label">Total events</span>
        <span class="s-val">{{ events.length }}</span>
      </div>
      <div class="divider"></div>
      <div class="stat-item">
        <span class="s-label">Ongoing</span>
        <span class="s-val text-green">{{ ongoingCount }}</span>
      </div>
      <div class="divider"></div>
      <div class="stat-item">
        <span class="s-label">Upcoming</span>
        <span class="s-val text-orange">{{ upcomingCount }}</span>
      </div>
    </div>

    <div class="toolbar">
      <div class="search-wrapper">
        <svg class="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <input v-model="searchQuery" type="text" placeholder="Search event name..." />
      </div>
      <div class="filter-group">
        <select v-model="statusFilter">
          <option value="">All statuses</option>
          <option value="active">Ongoing</option>
          <option value="upcoming">Upcoming</option>
          <option value="ended">Ended</option>
        </select>
      </div>
    </div>

    <!-- Loading skeleton -->
    <div v-if="loading" class="event-list-container">
      <div v-for="n in 3" :key="n" class="event-row-card">
        <div class="skel-img"></div>
        <div class="skel-body"><div class="skel-line"></div><div class="skel-line short"></div></div>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="error-banner">âš ï¸ {{ error }} <button @click="loadEvents">Retry</button></div>

    <!-- Event list -->
    <div v-else class="event-list-container">
      <div v-for="ev in filteredEvents" :key="ev.eventID" class="event-row-card">
        
        <div class="card-main">
          <div class="img-frame">
            <img :src="ev.cover || 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=300'" :alt="ev.eventName" />
            <div class="id-tag">#{{ ev.eventID }}</div>
          </div>
          <div class="info-block">
            <h3 class="ev-name">{{ ev.eventName }}</h3>
            <div class="ev-meta">
              <span>{{ formatDate(ev.startAt) }} â†’ {{ formatDate(ev.endAt) }}</span>
            </div>
          </div>
        </div>

        <div class="card-stats">
          <div class="time-box">
            <span class="lbl">Start</span>
            <span class="val">{{ formatDate(ev.startAt) }}</span>
          </div>
          <div class="participant-box">
            <span class="lbl">End</span>
            <span class="val">{{ formatDate(ev.endAt) }}</span>
          </div>
        </div>

        <div class="card-status">
          <div class="status-pill" :class="getStatus(ev)">{{ getStatusText(ev) }}</div>
        </div>

        <div class="card-actions">
          <button class="action-btn edit" @click="openEditModal(ev)" title="Edit event">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
          </button>
          <button class="action-btn delete" @click="deleteEvent(ev.eventID)" title="Delete event">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
          </button>
        </div>
      </div>

      <div v-if="filteredEvents.length === 0" class="empty-state">No events found.</div>
    </div>

    <!-- Create / Edit Modal -->
    <Transition name="zoom-in">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal-form">
          <div class="mf-header">
            <h3>{{ isEditing ? 'Edit Event' : 'Create New Event' }}</h3>
            <button class="btn-x" @click="showModal = false">âœ•</button>
          </div>
          <div class="form-group">
            <label>Event Name <span class="req">*</span></label>
            <input v-model="form.eventName" type="text" placeholder="E.g.: Top Chef: Vietnamese Flavors">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Start Date</label>
              <input v-model="form.startAt" type="date">
            </div>
            <div class="form-group">
              <label>End Date</label>
              <input v-model="form.endAt" type="date">
            </div>
          </div>
          <div class="form-group">
            <label>Winner Account ID</label>
            <input v-model.number="form.winner" type="number" placeholder="Leave empty if none">
          </div>
          <div class="mf-actions">
            <button @click="showModal = false" class="btn-cancel">Cancel</button>
            <button @click="saveEvent" class="btn-save" :disabled="saving">
              <span v-if="saving">Savingâ€¦</span><span v-else>{{ isEditing ? 'Update' : 'Create' }}</span>
            </button>
          </div>
        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/services/api.js'

const events = ref([])
const loading = ref(true)
const error = ref(null)
const searchQuery = ref('')
const statusFilter = ref('')
const showModal = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const form = reactive({ eventID: null, eventName: '', startAt: '', endAt: '', winner: null })

const ongoingCount = computed(() => events.value.filter(e => getStatus(e) === 'active').length)
const upcomingCount = computed(() => events.value.filter(e => getStatus(e) === 'upcoming').length)

const filteredEvents = computed(() => {
  return events.value.filter(ev => {
    const matchSearch = !searchQuery.value || ev.eventName?.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchStatus = !statusFilter.value || getStatus(ev) === statusFilter.value
    return matchSearch && matchStatus
  })
})

const loadEvents = async () => {
  loading.value = true; error.value = null
  try {
    const res = await api.get('/api/admin/events')
    events.value = res.data
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to load events'
  } finally {
    loading.value = false
  }
}

onMounted(loadEvents)

const getStatus = (ev) => {
  const now = new Date()
  const start = ev.startAt ? new Date(ev.startAt) : null
  const end = ev.endAt ? new Date(ev.endAt) : null
  if (end && now > end) return 'ended'
  if (start && now < start) return 'upcoming'
  return 'active'
}

const getStatusText = (ev) => ({ ended: 'Ended', upcoming: 'Upcoming', active: 'Ongoing' }[getStatus(ev)])

const formatDate = (d) => { if (!d) return 'â€”'; return new Date(d).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }) }

const openCreateModal = () => {
  isEditing.value = false
  Object.assign(form, { eventID: null, eventName: '', startAt: '', endAt: '', winner: null })
  showModal.value = true
}

const openEditModal = (ev) => {
  isEditing.value = true
  form.eventID = ev.eventID; form.eventName = ev.eventName
  form.startAt = ev.startAt?.substring(0, 10) || ''; form.endAt = ev.endAt?.substring(0, 10) || ''
  form.winner = ev.winner || null
  showModal.value = true
}

const saveEvent = async () => {
  if (!form.eventName.trim()) return alert('Event name is required!')
  saving.value = true
  try {
    const payload = { eventName: form.eventName, startAt: form.startAt || null, endAt: form.endAt || null, winner: form.winner || null }
    if (isEditing.value) {
      const res = await api.put(`/api/admin/events/${form.eventID}`, payload)
      const idx = events.value.findIndex(e => e.eventID === form.eventID)
      if (idx !== -1) events.value[idx] = res.data
    } else {
      const res = await api.post('/api/admin/events', payload)
      events.value.unshift(res.data)
    }
    showModal.value = false
  } catch (e) {
    alert(e.response?.data?.message || 'Save failed')
  } finally { saving.value = false }
}

const deleteEvent = async (id) => {
  if (!confirm('Delete this event permanently?')) return
  try {
    await api.delete(`/api/admin/events/${id}`)
    events.value = events.value.filter(e => e.eventID !== id)
  } catch (e) { alert(e.response?.data?.message || 'Delete failed') }
}
</script>

<style scoped>
.gomet-admin-pro { padding: 30px 40px; background: #FFF7ED; min-height: 100vh; font-family: 'Manrope', sans-serif; color: #1E293B; }
.header-zone { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 25px; }
.page-title { font-size: 2rem; font-weight: 800; color: #0F172A; margin: 0; letter-spacing: -0.5px; }
.sub-text { color: #EA580C; font-weight: 600; margin-top: 5px; opacity: 0.8; }
.btn-create-mega { background: linear-gradient(135deg, #EA580C, #C2410C); color: white; border: none; padding: 12px 28px; border-radius: 12px; font-weight: 700; font-size: 1rem; cursor: pointer; box-shadow: 0 8px 20px rgba(234,88,12,0.25); transition: 0.3s; display: flex; align-items: center; gap: 8px; }
.btn-create-mega:hover { transform: translateY(-2px); box-shadow: 0 12px 25px rgba(234,88,12,0.35); }
.stats-bar { display: flex; gap: 20px; margin-bottom: 30px; background: white; padding: 15px 25px; border-radius: 16px; width: fit-content; box-shadow: 0 4px 15px rgba(0,0,0,0.02); }
.stat-item { display: flex; flex-direction: column; padding: 0 10px; }
.s-label { font-size: 0.75rem; color: #64748B; font-weight: 600; text-transform: uppercase; }
.s-val { font-size: 1.2rem; font-weight: 800; color: #0F172A; }
.text-green { color: #16A34A; } .text-orange { color: #F97316; }
.divider { width: 1px; background: #E2E8F0; }
.toolbar { display: flex; justify-content: space-between; margin-bottom: 20px; gap: 12px; }
.search-wrapper { position: relative; flex: 1; max-width: 400px; }
.search-icon { position: absolute; left: 12px; top: 50%; transform: translateY(-50%); color: #94A3B8; }
.search-wrapper input { width: 100%; padding: 12px 12px 12px 40px; border: 1.5px solid #E2E8F0; background: white; border-radius: 12px; font-size: 0.95rem; box-sizing: border-box; transition: 0.2s; }
.search-wrapper input:focus { outline: none; border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234,88,12,0.1); }
.filter-group select { padding: 12px 16px; border: 1.5px solid #E2E8F0; background: white; border-radius: 12px; font-size: 0.9rem; cursor: pointer; }
.event-list-container { display: flex; flex-direction: column; gap: 16px; }
.event-row-card { display: grid; grid-template-columns: 2.5fr 1.5fr 1fr 1fr; gap: 20px; align-items: center; background: white; padding: 16px; border-radius: 20px; box-shadow: 0 4px 6px rgba(0,0,0,0.01); border: 1px solid white; transition: all 0.3s ease; }
.event-row-card:hover { transform: translateY(-3px); box-shadow: 0 15px 30px rgba(0,0,0,0.05); border-color: #FFEDD5; }
.card-main { display: flex; gap: 16px; align-items: center; }
.img-frame { position: relative; width: 80px; height: 80px; border-radius: 12px; overflow: hidden; flex-shrink: 0; }
.img-frame img { width: 100%; height: 100%; object-fit: cover; }
.id-tag { position: absolute; top: 0; left: 0; background: rgba(0,0,0,0.6); color: white; font-size: 0.6rem; padding: 2px 6px; border-bottom-right-radius: 8px; }
.info-block { display: flex; flex-direction: column; }
.ev-name { margin: 0 0 5px; font-size: 1rem; font-weight: 800; color: #1E293B; }
.ev-meta { font-size: 0.75rem; color: #EA580C; font-weight: 700; }
.card-stats { display: flex; gap: 20px; }
.time-box, .participant-box { display: flex; flex-direction: column; }
.lbl { font-size: 0.7rem; color: #94A3B8; font-weight: 600; text-transform: uppercase; margin-bottom: 2px; }
.val { font-size: 0.85rem; font-weight: 700; color: #334155; }
.status-pill { padding: 6px 14px; border-radius: 30px; font-size: 0.8rem; font-weight: 700; text-align: center; }
.status-pill.active { background: #DCFCE7; color: #15803D; }
.status-pill.upcoming { background: #FFEDD5; color: #C2410C; }
.status-pill.ended { background: #F1F5F9; color: #64748B; }
.card-actions { display: flex; gap: 10px; justify-content: flex-end; }
.action-btn { width: 40px; height: 40px; border-radius: 12px; border: none; background: #F8FAFC; color: #64748B; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.2s; }
.action-btn.edit:hover { background: #FFEDD5; color: #EA580C; }
.action-btn.delete:hover { background: #FEE2E2; color: #EF4444; }
.skel-img { width: 80px; height: 80px; border-radius: 12px; background: linear-gradient(90deg, #F1F5F9 25%, #E2E8F0 50%, #F1F5F9 75%); background-size: 200% 100%; animation: shimmer 1.4s infinite; }
.skel-body { flex: 1; display: flex; flex-direction: column; gap: 8px; }
.skel-line { height: 14px; background: linear-gradient(90deg, #F1F5F9 25%, #E2E8F0 50%, #F1F5F9 75%); background-size: 200% 100%; animation: shimmer 1.4s infinite; border-radius: 6px; }
.skel-line.short { width: 50%; }
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }
.error-banner { background: #FEF2F2; border: 1px solid #FECACA; border-radius: 12px; padding: 16px; color: #DC2626; font-weight: 600; display: flex; gap: 12px; align-items: center; }
.error-banner button { background: #DC2626; color: white; border: none; padding: 6px 14px; border-radius: 8px; cursor: pointer; font-weight: 700; }
.empty-state { text-align: center; padding: 60px; color: #94A3B8; }
.modal-overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.6); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-form { background: white; border-radius: 24px; padding: 32px; width: 480px; max-width: 90vw; box-shadow: 0 30px 60px -15px rgba(0,0,0,0.3); }
.mf-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.mf-header h3 { margin: 0; font-size: 1.3rem; font-weight: 800; color: #0F172A; }
.btn-x { background: #F1F5F9; border: none; width: 32px; height: 32px; border-radius: 50%; cursor: pointer; transition: 0.2s; }
.btn-x:hover { background: #FEE2E2; color: #EF4444; }
.form-group { margin-bottom: 16px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 700; color: #334155; font-size: 0.9rem; }
.req { color: #EF4444; }
.form-group input { width: 100%; padding: 11px 14px; border: 1.5px solid #E2E8F0; border-radius: 10px; font-size: 0.95rem; box-sizing: border-box; transition: 0.2s; }
.form-group input:focus { outline: none; border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234,88,12,0.1); }
.mf-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 24px; }
.btn-cancel { background: #F1F5F9; border: none; padding: 11px 22px; border-radius: 10px; cursor: pointer; font-weight: 600; color: #475569; }
.btn-save { background: linear-gradient(135deg, #EA580C, #C2410C); color: white; border: none; padding: 11px 24px; border-radius: 10px; cursor: pointer; font-weight: 700; transition: 0.2s; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-save:not(:disabled):hover { transform: translateY(-1px); }
.zoom-in-enter-active, .zoom-in-leave-active { transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.zoom-in-enter-from, .zoom-in-leave-to { opacity: 0; transform: scale(0.9); }
</style>
