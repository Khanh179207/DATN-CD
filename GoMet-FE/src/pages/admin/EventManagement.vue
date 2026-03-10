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

    <div v-if="loading" class="event-list-container">
      <div v-for="n in 3" :key="n" class="event-row-card">
        <div class="skel-img"></div>
        <div class="skel-body"><div class="skel-line"></div><div class="skel-line short"></div></div>
      </div>
    </div>

    <div v-else-if="error" class="error-banner">⚠️ {{ error }} <button @click="loadEvents">Retry</button></div>

    <div v-else class="event-list-container">
      <div v-for="ev in filteredEvents" :key="ev.eventID" class="event-row-card">
        
        <div class="card-main">
          <div class="img-frame">
            <img :src="getImageUrl(ev.bannerImage)" :alt="ev.eventName" />
            <div class="id-tag">#{{ ev.eventID }}</div>
          </div>
          <div class="info-block">
            <h3 class="ev-name">{{ ev.eventName }}</h3>
            <div class="ev-meta">
              <span>Nộp: {{ formatDate(ev.startAt) }} → {{ formatDate(ev.endAt) }}</span>
            </div>
            <div class="ev-meta vote-meta">
              <span>Vote: {{ formatDate(ev.voteStartAt) }} → {{ formatDate(ev.voteEndAt) }}</span>
            </div>
          </div>
        </div>

        <div class="card-stats">
          <div class="time-box">
            <span class="lbl">Posts</span>
            <span class="val">{{ ev.postCount || 0 }}</span>
          </div>
          <div class="participant-box">
            <span class="lbl">Total Votes</span>
            <span class="val">{{ ev.totalVotes || 0 }}</span>
          </div>
        </div>

        <div class="card-status">
          <div class="status-pill" :class="getStatus(ev)">{{ getStatusText(ev) }}</div>
        </div>

        <div class="card-actions">
          <button v-if="getStatus(ev) === 'ended'" class="action-btn winner" @click="viewWinner(ev)" title="View Winner">
            <i class="fas fa-crown"></i>
          </button>
          
          <button v-if="getStatus(ev) === 'active'" class="action-btn end-early" @click="forceEndEvent(ev)" title="End Event Now">
            <i class="fas fa-bolt"></i>
          </button>

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

    <Transition name="zoom-in">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal-form wide-modal">
          <div class="mf-header">
            <h3>{{ isEditing ? 'Edit Event' : 'Create New Event' }}</h3>
            <button class="btn-x" @click="showModal = false">✕</button>
          </div>

          <div class="modal-scroll-area">
            <div class="form-group">
              <label>Event Name <span class="req">*</span></label>
              <input v-model="form.eventName" type="text" placeholder="E.g.: Top Chef: Vietnamese Flavors" :class="{ 'input-error': errors.eventName }">
              <span v-if="errors.eventName" class="error-text">{{ errors.eventName }}</span>
            </div>

            <div class="form-group">
              <label>Description</label>
              <textarea v-model="form.description" rows="3" placeholder="Giới thiệu ngắn về sự kiện..."></textarea>
            </div>

            <div class="form-group">
              <label>Banner Image</label>
              <div class="upload-zone" :class="{ 'has-file': imagePreview }">
                <input type="file" id="fileUpload" accept="image/*" @change="handleFileUpload" hidden>
                <div v-if="!imagePreview" class="upload-placeholder">
                  <label for="fileUpload" class="btn-upload">Upload File</label>
                </div>
                <div v-else class="preview-container">
                  <img :src="imagePreview" class="preview-img">
                  <button class="btn-remove-img" @click="removeImage" title="Xóa ảnh">✕</button>
                  <label for="fileUpload" class="btn-change-img" title="Thay ảnh khác">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg>
                    Thay ảnh
                  </label>
                </div>
              </div>
            </div>

            <div class="form-grid">
               <div class="form-column">
                 <h4 class="column-title">📜 Rules & Rewards</h4>
                 
                 <div class="form-group">
                    <label>Số phiếu Vote tối đa/User <span class="req">*</span></label>
                    <input v-model="form.maxVotes" type="number" min="1" placeholder="Ví dụ: 3">
                 </div>

                 <div class="form-group">
                    <label>Event Rules</label>
                    <textarea v-model="form.rules" rows="4" placeholder="Thể lệ tham gia..."></textarea>
                 </div>
                 <div class="form-group">
                    <label>Reward</label>
                    <input v-model="form.reward" type="text" placeholder="Ví dụ: Huy hiệu Top Chef + 1 Tháng Premium">
                 </div>
               </div>

               <div class="form-column">
                  <h4 class="column-title">⏳ Timeline Settings</h4>
                  <div class="form-group">
                    <label>Submit Start</label>
                    <input v-model="form.startAt" type="datetime-local" @change="validateDates">
                  </div>
                  <div class="form-group">
                    <label>Submit End</label>
                    <input v-model="form.endAt" type="datetime-local" @change="validateDates" :class="{ 'input-error': errors.submitDate }">
                    <span v-if="errors.submitDate" class="error-text">{{ errors.submitDate }}</span>
                  </div>
                  <div class="form-group">
                    <label>Vote Start</label>
                    <input v-model="form.voteStartAt" type="datetime-local" @change="validateDates">
                  </div>
                  <div class="form-group">
                    <label>Vote End</label>
                    <input v-model="form.voteEndAt" type="datetime-local" @change="validateDates" :class="{ 'input-error': errors.voteDate }">
                    <span v-if="errors.voteDate" class="error-text">{{ errors.voteDate }}</span>
                  </div>
               </div>
            </div>
          </div>

          <div class="mf-actions">
            <button @click="showModal = false" class="btn-cancel">Cancel</button>
            <button @click="saveEvent" class="btn-save" :disabled="saving || hasErrors">
              <span v-if="saving">Saving…</span><span v-else>{{ isEditing ? 'Update' : 'Create' }}</span>
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <Transition name="zoom-in">
      <div v-if="showWinnerModal" class="modal-overlay" @click.self="showWinnerModal = false">
        <div class="winner-modal-card">
          <div class="winner-header">
            <h3>👑 Event Winner</h3>
            <button class="btn-x" @click="showWinnerModal = false">✕</button>
          </div>
          <div v-if="loadingWinner" class="winner-loading">Calculating results...</div>
          <div v-else-if="!winnerData" class="winner-empty">No valid entries found for this event.</div>
          <div v-else class="winner-content">
            <div class="winner-medal">🥇</div>
            <img :src="getImageUrl(winnerData.postImage)" alt="Winning Recipe" class="winner-recipe-img" />
            <h2 class="winner-title">{{ winnerData.postTitle }}</h2>
            <p class="winner-author">By: <strong>{{ winnerData.username }}</strong></p>
            <div class="winner-votes">
              <span class="v-count">{{ winnerData.voteCount }}</span>
              <span class="v-lbl">Total Votes</span>
            </div>
          </div>
        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/services/api.js'
import { toast } from '@/composables/useToast'

const events = ref([])
const loading = ref(true)
const error = ref(null)
const searchQuery = ref('')
const statusFilter = ref('')
const showModal = ref(false)
const isEditing = ref(false)
const saving = ref(false)

const imagePreview = ref(null)
const showWinnerModal = ref(false)
const winnerData = ref(null)
const loadingWinner = ref(false)

const form = reactive({ 
  eventID: null, eventName: '', bannerFile: null, bannerImage: '',
  description: '', rules: '', reward: '', maxVotes: 3, // Thêm field mới mặc định là 3
  startAt: '', endAt: '', voteStartAt: '', voteEndAt: '' 
})

const errors = reactive({ eventName: '', submitDate: '', voteDate: '' })

const getImageUrl = (path) => {
  if (!path) return 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=300';
  if (path.startsWith('/uploads')) {
    return `http://localhost:8080${encodeURI(path)}`; 
  }
  return path;
}
const ongoingCount = computed(() => events.value.filter(e => getStatus(e) === 'active').length)
const upcomingCount = computed(() => events.value.filter(e => getStatus(e) === 'upcoming').length)

const filteredEvents = computed(() => {
  return events.value.filter(ev => {
    const matchSearch = !searchQuery.value || ev.eventName?.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchStatus = !statusFilter.value || getStatus(ev) === statusFilter.value
    return matchSearch && matchStatus
  })
})

const hasErrors = computed(() => !!errors.eventName || !!errors.submitDate || !!errors.voteDate)

const validateDates = () => {
  errors.submitDate = ''
  errors.voteDate = ''
  if (form.startAt && form.endAt) {
    if (new Date(form.endAt) <= new Date(form.startAt)) errors.submitDate = 'End date must be after start date!'
  }
  if (form.voteStartAt && form.voteEndAt) {
    if (new Date(form.voteEndAt) <= new Date(form.voteStartAt)) errors.voteDate = 'Vote end date must be after vote start date!'
  }
}

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
  const end = ev.endAt ? new Date(ev.endAt) : null
  const voteEnd = ev.voteEndAt ? new Date(ev.voteEndAt) : end
  const finalEndDate = new Date(Math.max(end?.getTime() || 0, voteEnd?.getTime() || 0))
  if (finalEndDate && now > finalEndDate) return 'ended'
  const start = ev.startAt ? new Date(ev.startAt) : null
  if (start && now < start) return 'upcoming'
  return 'active'
}

const getStatusText = (ev) => ({ ended: 'Ended', upcoming: 'Upcoming', active: 'Ongoing' }[getStatus(ev)])

const formatDate = (d) => { 
  if (!d) return '—'; 
  return new Date(d).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }) 
}

const formatForInput = (dateStr) => {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return '';
  const z = (n) => (n < 10 ? '0' : '') + n;
  return `${d.getFullYear()}-${z(d.getMonth() + 1)}-${z(d.getDate())}T${z(d.getHours())}:${z(d.getMinutes())}`;
}

const handleFileUpload = (event) => {
  const file = event.target.files[0]
  if (!file) return
  form.bannerFile = file
  imagePreview.value = URL.createObjectURL(file)
}

const removeImage = () => {
  form.bannerFile = null
  form.bannerImage = ''
  imagePreview.value = null
  const input = document.getElementById('fileUpload')
  if (input) input.value = ''
}

const openCreateModal = () => {
  isEditing.value = false; removeImage()
  Object.assign(form, { eventID: null, eventName: '', bannerFile: null, bannerImage: '', description: '', rules: '', reward: '', maxVotes: 3, startAt: '', endAt: '', voteStartAt: '', voteEndAt: '' })
  showModal.value = true
}

const openEditModal = (ev) => {
  isEditing.value = true; removeImage()
  form.eventID = ev.eventID
  form.eventName = ev.eventName
  form.description = ev.description || ''
  form.rules = ev.rules || ''
  form.reward = ev.reward || ''
  form.maxVotes = ev.maxVotes || 3 // Lấy data cũ, nếu k có mặc định 3
  form.bannerImage = ev.bannerImage || ''
  imagePreview.value = getImageUrl(ev.bannerImage)
  form.startAt = formatForInput(ev.startAt)
  form.endAt = formatForInput(ev.endAt)
  form.voteStartAt = formatForInput(ev.voteStartAt)
  form.voteEndAt = formatForInput(ev.voteEndAt)
  showModal.value = true
}

const forceEndEvent = async (ev) => {
  if (!confirm(`Are you sure you want to FORCE END "${ev.eventName}"?`)) return
  try {
    const now = new Date(); now.setMinutes(now.getMinutes() - 1);
    const pastTimeStr = formatForInput(now.toISOString());
    const payload = new FormData()
    payload.append('eventName', ev.eventName)
    payload.append('endAt', pastTimeStr)
    payload.append('voteEndAt', pastTimeStr)
    const res = await api.put(`/api/admin/events/${ev.eventID}`, payload, { headers: { 'Content-Type': 'multipart/form-data' }})
    const idx = events.value.findIndex(e => e.eventID === ev.eventID)
    if (idx !== -1) events.value[idx] = res.data
    toast.success('Event ended!')
  } catch(e) { toast.error('Failed to end event!') }
}

const viewWinner = async (ev) => {
  showWinnerModal.value = true; loadingWinner.value = true
  try {
    const res = await api.get(`/api/admin/events/${ev.eventID}/posts`)
    if (res.data && res.data.length > 0) {
      winnerData.value = res.data.sort((a, b) => b.voteCount - a.voteCount)[0]
    }
  } catch (e) { toast.error('Failed to load winner') } finally { loadingWinner.value = false }
}

const saveEvent = async () => {
  if (!form.eventName.trim()) { toast.error('Name required!'); return }
  validateDates()
  if (hasErrors.value) return
  saving.value = true
  try {
    const payload = new FormData()
    payload.append('eventName', form.eventName)
    payload.append('description', form.description || '')
    payload.append('rules', form.rules || '')
    payload.append('reward', form.reward || '')
    payload.append('maxVotes', form.maxVotes) // Gửi maxVotes xuống backend
    if (form.startAt) payload.append('startAt', form.startAt)
    if (form.endAt) payload.append('endAt', form.endAt)
    if (form.voteStartAt) payload.append('voteStartAt', form.voteStartAt)
    if (form.voteEndAt) payload.append('voteEndAt', form.voteEndAt)
    if (form.bannerFile) payload.append('imageFile', form.bannerFile)
    else if (form.bannerImage) payload.append('bannerImage', form.bannerImage)

    let res;
    if (isEditing.value) {
      res = await api.put(`/api/admin/events/${form.eventID}`, payload, { headers: { 'Content-Type': 'multipart/form-data' } })
      const idx = events.value.findIndex(e => e.eventID === form.eventID)
      if (idx !== -1) events.value[idx] = res.data
    } else {
      res = await api.post('/api/admin/events', payload, { headers: { 'Content-Type': 'multipart/form-data' } })
      events.value.unshift(res.data)
    }
    showModal.value = false; toast.success('Saved!'); loadEvents();
  } catch (e) { toast.error('Save failed') } finally { saving.value = false }
}

const deleteEvent = async (id) => {
  if (!confirm('Delete event?')) return
  try {
    await api.delete(`/api/admin/events/${id}`)
    events.value = events.value.filter(e => e.eventID !== id)
    toast.success('Deleted!')
  } catch (e) { toast.error('Delete failed') }
}
</script>

<style scoped>
/* Toàn bộ Style giữ nguyên 100% như cũ */
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
.ev-meta { font-size: 0.75rem; color: #64748B; font-weight: 600; }
.vote-meta { color: #EA580C; margin-top: 2px; }
.card-stats { display: flex; gap: 20px; }
.lbl { font-size: 0.7rem; color: #94A3B8; font-weight: 600; text-transform: uppercase; margin-bottom: 2px; }
.val { font-size: 0.85rem; font-weight: 700; color: #334155; }
.status-pill { padding: 6px 14px; border-radius: 30px; font-size: 0.8rem; font-weight: 700; text-align: center; }
.status-pill.active { background: #DCFCE7; color: #15803D; }
.status-pill.upcoming { background: #FFEDD5; color: #C2410C; }
.status-pill.ended { background: #F1F5F9; color: #64748B; }

.card-actions { display: flex; gap: 8px; justify-content: flex-end; }
.action-btn { width: 38px; height: 38px; border-radius: 10px; border: none; background: #F8FAFC; color: #64748B; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.2s; }
.action-btn.edit:hover { background: #DBEAFE; color: #2563EB; }
.action-btn.delete:hover { background: #FEE2E2; color: #EF4444; }
.action-btn.end-early { color: #D97706; }
.action-btn.end-early:hover { background: #FEF3C7; color: #B45309; }
.action-btn.winner { color: #059669; }
.action-btn.winner:hover { background: #D1FAE5; color: #047857; }

.modal-overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.6); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-form { background: white; border-radius: 24px; padding: 32px; width: 480px; max-width: 90vw; box-shadow: 0 30px 60px -15px rgba(0,0,0,0.3); }
.wide-modal { width: 750px !important; }
.modal-scroll-area { max-height: 65vh; overflow-y: auto; padding-right: 10px; }

.form-group { margin-bottom: 16px; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 700; color: #334155; font-size: 0.9rem; }
.req { color: #EF4444; }
.form-group input, .form-group textarea { 
    width: 100%; padding: 11px 14px; border: 1.5px solid #E2E8F0; border-radius: 10px; font-size: 0.95rem; box-sizing: border-box; transition: 0.2s; font-family: inherit; 
}
.form-group input:focus, .form-group textarea:focus { outline: none; border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234,88,12,0.1); }
.input-error { border-color: #EF4444 !important; }
.error-text { font-size: 0.8rem; color: #EF4444; margin-top: 4px; display: block; font-weight: 600; }

.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; background: #F8FAFC; padding: 20px; border-radius: 16px; margin-top: 20px; }
.column-title { font-size: 0.95rem; color: #EA580C; margin-top: 0; margin-bottom: 15px; font-weight: 800; border-bottom: 1px solid #ffedd5; padding-bottom: 10px; }

.upload-zone { border: 2px dashed #CBD5E1; border-radius: 12px; padding: 20px; text-align: center; transition: 0.2s; background: #F8FAFC; }
.btn-upload { display: inline-flex; align-items: center; gap: 8px; background: white; border: 1px solid #E2E8F0; padding: 8px 16px; border-radius: 8px; cursor: pointer; font-weight: 600; color: #475569; }
.preview-img { width: 100%; height: 180px; object-fit: cover; border-radius: 8px; display: block; }
.preview-container { position: relative; width: 100%; display: flex; flex-direction: column; gap: 10px; }

/* CSS Mới cho nút Đổi Ảnh & Xóa Ảnh */
.btn-remove-img { position: absolute; top: 10px; right: 10px; background: rgba(0,0,0,0.6); color: white; border: none; width: 30px; height: 30px; border-radius: 50%; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.btn-remove-img:hover { background: #ef4444; }

.btn-change-img { 
  display: inline-flex; justify-content: center; align-items: center; gap: 8px; 
  background: #EA580C; color: white; font-weight: 700; padding: 8px; 
  border-radius: 8px; cursor: pointer; transition: 0.2s; 
}
.btn-change-img:hover { background: #c2410c; }


.mf-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 24px; }
.btn-cancel { background: #F1F5F9; border: none; padding: 11px 22px; border-radius: 10px; cursor: pointer; font-weight: 600; color: #475569; }
.btn-save { background: linear-gradient(135deg, #EA580C, #C2410C); color: white; border: none; padding: 11px 24px; border-radius: 10px; cursor: pointer; font-weight: 700; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }

.winner-modal-card { background: white; border-radius: 24px; padding: 30px; width: 400px; text-align: center; position: relative; }
.winner-medal { font-size: 3rem; margin-bottom: 10px; animation: bounce 2s infinite; }
.winner-recipe-img { width: 120px; height: 120px; border-radius: 50%; object-fit: cover; border: 4px solid #FCD34D; margin-bottom: 15px; }
.winner-title { font-size: 1.2rem; font-weight: 800; color: #0F172A; }
.winner-votes { background: #FEF3C7; color: #D97706; padding: 8px 20px; border-radius: 20px; display: inline-flex; gap: 8px; align-items: center; margin-top: 15px;}

@keyframes bounce { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-10px); } }
.zoom-in-enter-active, .zoom-in-leave-active { transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.zoom-in-enter-from, .zoom-in-leave-to { opacity: 0; transform: scale(0.9); }
</style>