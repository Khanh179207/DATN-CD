<template>
    <div class="page-container">
        <div class="page-header">
            <div>
                <h2 class="title">Achievement Management</h2>
                <p class="sub-text">{{ achievements.length }} badges in the catalogue</p>
            </div>
            <button class="btn-add" @click="openModal()">+ Add Badge</button>
        </div>

        <!-- Loading skeleton -->
        <div v-if="loading" class="table-wrapper">
            <div class="skel-row" v-for="n in 5" :key="n">
                <div class="skel-icon"></div>
                <div class="skel-lines">
                    <div class="skel-line"></div>
                    <div class="skel-line short"></div>
                </div>
            </div>
        </div>

        <!-- Error -->
        <div v-else-if="error" class="error-banner">
            <AlertTriangle :size="15" /> {{ error }} <button @click="loadAchievements">Retry</button>
        </div>

        <!-- Table -->
        <div v-else class="table-wrapper">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Icon</th>
                        <th>Badge Name</th>
                        <th>Description / Condition</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="badge in achievements" :key="badge.achievementID">
                        <td>
                            <div class="icon-cell">
                                <img v-if="badge.icon" :src="badge.icon" class="badge-icon" :alt="badge.achievementName"
                                    @error="e => e.target.style.display = 'none'">
                                <span v-else class="icon-placeholder">
                                    <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#F59E0B"
                                        stroke-width="1.5">
                                        <path
                                            d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                                    </svg>
                                </span>
                            </div>
                        </td>
                        <td><span class="badge-name">{{ badge.achievementName }}</span></td>
                        <td class="desc-cell">{{ badge.description || '—' }}</td>
                        <td>
                            <div class="actions">
                                <button @click="openModal(badge)" class="btn-icon edit" title="Edit">
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                        stroke-width="2">
                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                                    </svg>
                                </button>
                                <button @click="deleteItem(badge.achievementID)" class="btn-icon delete" title="Delete">
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                        stroke-width="2">
                                        <polyline points="3 6 5 6 21 6" />
                                        <path
                                            d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                                    </svg>
                                </button>
                            </div>
                        </td>
                    </tr>
                    <tr v-if="achievements.length === 0">
                        <td colspan="4" class="empty-state">No achievements found.</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- Add/Edit Modal -->
        <Transition name="fade-up">
            <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
                <div class="modal-box">
                    <div class="modal-header">
                        <h3>{{ isEditing ? 'Edit Badge' : 'Add New Badge' }}</h3>
                        <button class="btn-x" @click="showModal = false">✕</button>
                    </div>
                    <div class="form-group">
                        <label>Badge Name <span class="req">*</span></label>
                        <input v-model="form.achievementName" type="text" placeholder="E.g.: Busy Bee...">
                    </div>
                    <div class="form-group">
                        <label>Icon URL</label>
                        <input v-model="form.icon" type="text" placeholder="https://...">
                        <div v-if="form.icon" class="icon-preview">
                            <img :src="form.icon" alt="Preview" @error="e => e.target.style.display = 'none'">
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Description (How to earn)</label>
                        <input v-model="form.description" type="text" placeholder="E.g.: Publish your first recipe...">
                    </div>
                    <div class="modal-actions">
                        <button @click="showModal = false" class="btn-cancel">Cancel</button>
                        <button @click="saveData" class="btn-save" :disabled="saving">
                            <span v-if="saving">Saving…</span><span v-else>{{ isEditing ? 'Update' : 'Create' }}</span>
                        </button>
                    </div>
                </div>
            </div>
        </Transition>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { AlertTriangle } from 'lucide-vue-next'
import api from '@/services/api.js'

const achievements = ref([])
const loading = ref(true)
const error = ref(null)
const showModal = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const form = reactive({ achievementID: null, achievementName: '', description: '', icon: '' })

const loadAchievements = async () => {
    loading.value = true; error.value = null
    try {
        const res = await api.get('/api/achievements')
        achievements.value = res.data
    } catch (e) {
        error.value = e.response?.data?.message || 'Failed to load achievements'
    } finally { loading.value = false }
}

onMounted(loadAchievements)

const openModal = (item = null) => {
    showModal.value = true
    if (item) {
        isEditing.value = true
        form.achievementID = item.achievementID
        form.achievementName = item.achievementName
        form.description = item.description || ''
        form.icon = item.icon || ''
    } else {
        isEditing.value = false
        form.achievementID = null; form.achievementName = ''; form.description = ''; form.icon = ''
    }
}

const saveData = async () => {
    if (!form.achievementName.trim()) return alert('Please enter a badge name!')
    saving.value = true
    try {
        const payload = { achievementName: form.achievementName, description: form.description, icon: form.icon }
        if (isEditing.value) {
            const res = await api.put(`/api/achievements/${form.achievementID}`, payload)
            const idx = achievements.value.findIndex(b => b.achievementID === form.achievementID)
            if (idx !== -1) achievements.value[idx] = res.data
        } else {
            const res = await api.post('/api/achievements', payload)
            achievements.value.push(res.data)
        }
        showModal.value = false
    } catch (e) {
        alert(e.response?.data?.message || 'Save failed')
    } finally { saving.value = false }
}

const deleteItem = async (id) => {
    if (!confirm('Delete this badge?')) return
    try {
        await api.delete(`/api/achievements/${id}`)
        achievements.value = achievements.value.filter(b => b.achievementID !== id)
    } catch (e) {
        alert(e.response?.data?.message || 'Delete failed')
    }
}
</script>

<style scoped>
.page-container {
    padding: 30px 40px;
    font-family: 'Manrope', sans-serif;
    background: #F5F3FF;
    min-height: 100vh;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 28px;
}

.title {
    font-size: 1.8rem;
    font-weight: 800;
    color: #0F172A;
    margin: 0;
}

.sub-text {
    color: #7C3AED;
    font-size: 0.9rem;
    font-weight: 600;
    margin: 4px 0 0;
}

.btn-add {
    background: linear-gradient(135deg, #7C3AED, #6D28D9);
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: 12px;
    font-weight: 700;
    cursor: pointer;
    font-size: 0.95rem;
    box-shadow: 0 4px 15px rgba(124, 58, 237, 0.25);
    transition: 0.2s;
}

.btn-add:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(124, 58, 237, 0.35);
}

.table-wrapper {
    background: white;
    border-radius: 16px;
    border: 1px solid #EDE9FE;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
}

.data-table {
    width: 100%;
    border-collapse: collapse;
}

.data-table th {
    padding: 14px 16px;
    background: #F5F3FF;
    color: #64748B;
    font-weight: 700;
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    text-align: left;
    border-bottom: 1px solid #EDE9FE;
}

.data-table td {
    padding: 14px 16px;
    border-bottom: 1px solid #F8FAFC;
    vertical-align: middle;
}

.data-table tr:last-child td {
    border-bottom: none;
}

.data-table tr:hover td {
    background: #FAFAFA;
}

.icon-cell {
    display: flex;
    align-items: center;
}

.badge-icon {
    width: 44px;
    height: 44px;
    object-fit: contain;
    border-radius: 10px;
    border: 1px solid #EDE9FE;
    padding: 4px;
}

.icon-placeholder {
    font-size: 1.8rem;
}

.badge-name {
    font-weight: 700;
    color: #334155;
}

.desc-cell {
    color: #64748B;
    font-size: 0.9rem;
    max-width: 350px;
}

.empty-state {
    text-align: center;
    padding: 40px;
    color: #94A3B8;
}

.actions {
    display: flex;
    gap: 8px;
}

.btn-icon {
    width: 34px;
    height: 34px;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: 0.2s;
}

.edit {
    background: #EEF2FF;
    color: #4F46E5;
}

.edit:hover {
    background: #4F46E5;
    color: white;
}

.delete {
    background: #FEF2F2;
    color: #EF4444;
}

.delete:hover {
    background: #EF4444;
    color: white;
}

/* Skeleton */
.skel-row {
    display: flex;
    gap: 12px;
    align-items: center;
    padding: 14px 16px;
    border-bottom: 1px solid #F8FAFC;
}

.skel-icon {
    width: 44px;
    height: 44px;
    border-radius: 10px;
    background: linear-gradient(90deg, #F1F5F9 25%, #E2E8F0 50%, #F1F5F9 75%);
    background-size: 200% 100%;
    animation: shimmer 1.4s infinite;
    flex-shrink: 0;
}

.skel-lines {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.skel-line {
    height: 12px;
    background: linear-gradient(90deg, #F1F5F9 25%, #E2E8F0 50%, #F1F5F9 75%);
    background-size: 200% 100%;
    animation: shimmer 1.4s infinite;
    border-radius: 6px;
}

.skel-line.short {
    width: 45%;
}

@keyframes shimmer {
    0% {
        background-position: 200% 0;
    }

    100% {
        background-position: -200% 0;
    }
}

.error-banner {
    background: #FEF2F2;
    border: 1px solid #FECACA;
    border-radius: 12px;
    padding: 16px;
    color: #DC2626;
    font-weight: 600;
    display: flex;
    gap: 12px;
    align-items: center;
}

.error-banner button {
    background: #DC2626;
    color: white;
    border: none;
    padding: 6px 14px;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 700;
}

/* Modal */
.modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(15, 23, 42, 0.6);
    backdrop-filter: blur(6px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 100;
}

.modal-box {
    background: white;
    padding: 32px;
    border-radius: 24px;
    width: 440px;
    max-width: 90vw;
    box-shadow: 0 25px 60px -15px rgba(0, 0, 0, 0.25);
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
}

.modal-header h3 {
    margin: 0;
    font-size: 1.3rem;
    font-weight: 800;
    color: #0F172A;
}

.btn-x {
    background: #F1F5F9;
    border: none;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    cursor: pointer;
    transition: 0.2s;
}

.btn-x:hover {
    background: #FEE2E2;
    color: #EF4444;
}

.form-group {
    margin-bottom: 18px;
}

.form-group label {
    display: block;
    margin-bottom: 6px;
    font-weight: 700;
    color: #334155;
    font-size: 0.9rem;
}

.req {
    color: #EF4444;
}

.form-group input {
    width: 100%;
    padding: 11px 14px;
    border: 1.5px solid #E2E8F0;
    border-radius: 10px;
    font-size: 0.95rem;
    box-sizing: border-box;
    transition: 0.2s;
}

.form-group input:focus {
    outline: none;
    border-color: #7C3AED;
    box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.12);
}

.icon-preview {
    margin-top: 10px;
}

.icon-preview img {
    width: 48px;
    height: 48px;
    object-fit: contain;
    border-radius: 10px;
    border: 1px solid #EDE9FE;
    padding: 4px;
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 24px;
}

.btn-cancel {
    background: #F1F5F9;
    border: none;
    padding: 11px 22px;
    border-radius: 10px;
    cursor: pointer;
    font-weight: 600;
    color: #475569;
}

.btn-save {
    background: linear-gradient(135deg, #7C3AED, #6D28D9);
    color: white;
    border: none;
    padding: 11px 24px;
    border-radius: 10px;
    cursor: pointer;
    font-weight: 700;
    transition: 0.2s;
}

.btn-save:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.btn-save:not(:disabled):hover {
    transform: translateY(-1px);
}

.fade-up-enter-active,
.fade-up-leave-active {
    transition: all 0.25s ease;
}

.fade-up-enter-from,
.fade-up-leave-to {
    opacity: 0;
    transform: scale(0.95);
}
</style>