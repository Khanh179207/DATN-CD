<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2 class="title">Category Management</h2>
        <p class="sub-text">{{ categories.length }} categories total</p>
      </div>
      <button class="btn-add" @click="openModal()">+ Add Category</button>
    </div>

    <!-- Loading skeleton -->
    <div v-if="loading" class="grid-list">
      <div class="item-card skeleton" v-for="n in 6" :key="n">
        <div class="skel-img"></div>
        <div class="skel-body"><div class="skel-line"></div><div class="skel-line short"></div></div>
      </div>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="error-banner">
      <AlertTriangle :size="15" /> {{ error }} <button @click="loadCategories">Retry</button>
    </div>

    <!-- Grid -->
    <div v-else class="grid-list">
      <div class="item-card" v-for="cat in categories" :key="cat.categoryID">
        <div class="img-frame">
          <img :src="cat.image || 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=300'" class="cat-img" :alt="cat.categoryName">
          <div class="img-overlay">
            <button @click="openModal(cat)" class="btn-action edit" title="Edit">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
            </button>
            <button @click="deleteCat(cat.categoryID)" class="btn-action delete" title="Delete">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
            </button>
          </div>
        </div>
        <div class="cat-info">
          <h3>{{ cat.categoryName }}</h3>
          <p>{{ cat.postCount ?? 0 }} posts</p>
        </div>
      </div>
      <div v-if="categories.length === 0" class="empty-state">No categories found.</div>
    </div>

    <!-- Add/Edit Modal -->
    <Transition name="fade-up">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal-box">
          <div class="modal-header">
            <h3>{{ isEditing ? 'Edit Category' : 'Add New Category' }}</h3>
            <button class="btn-x" @click="showModal = false">✕</button>
          </div>
          <div class="form-group">
            <label>Category name <span class="req">*</span></label>
            <input v-model="form.categoryName" type="text" placeholder="E.g.: Vietnamese Cuisine...">
          </div>
          <div class="form-group">
            <label>Image URL</label>
            <input v-model="form.image" type="text" placeholder="https://...">
            <div v-if="form.image" class="img-preview">
              <img :src="form.image" alt="Preview" @error="e => e.target.style.display='none'">
            </div>
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

const categories = ref([])
const loading = ref(true)
const error = ref(null)
const showModal = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const form = reactive({ categoryID: null, categoryName: '', image: '' })

const loadCategories = async () => {
  loading.value = true
  error.value = null
  try {
    const res = await api.get('/api/admin/categories')
    categories.value = res.data
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to load categories'
  } finally {
    loading.value = false
  }
}

onMounted(loadCategories)

const openModal = (item = null) => {
  showModal.value = true
  if (item) {
    isEditing.value = true
    form.categoryID = item.categoryID
    form.categoryName = item.categoryName
    form.image = item.image || ''
  } else {
    isEditing.value = false
    form.categoryID = null
    form.categoryName = ''
    form.image = ''
  }
}

const saveData = async () => {
  if (!form.categoryName.trim()) return alert('Please enter a category name!')
  saving.value = true
  try {
    const payload = { categoryName: form.categoryName, image: form.image }
    if (isEditing.value) {
      const res = await api.put(`/api/admin/categories/${form.categoryID}`, payload)
      const idx = categories.value.findIndex(c => c.categoryID === form.categoryID)
      if (idx !== -1) categories.value[idx] = res.data
    } else {
      const res = await api.post('/api/admin/categories', payload)
      categories.value.unshift(res.data)
    }
    showModal.value = false
  } catch (e) {
    alert(e.response?.data?.message || 'Save failed')
  } finally {
    saving.value = false
  }
}

const deleteCat = async (id) => {
  if (!confirm('Delete this category? All posts using it will lose their category.')) return
  try {
    await api.delete(`/api/admin/categories/${id}`)
    categories.value = categories.value.filter(c => c.categoryID !== id)
  } catch (e) {
    alert(e.response?.data?.message || 'Delete failed')
  }
}
</script>

<style scoped>
.page-container { padding: 30px 40px; font-family: 'Manrope', sans-serif; background: #FFF7ED; min-height: 100vh; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 28px; }
.title { font-size: 1.8rem; font-weight: 800; color: #0F172A; margin: 0; }
.sub-text { color: #EA580C; font-size: 0.9rem; font-weight: 600; margin: 4px 0 0; }
.btn-add { background: linear-gradient(135deg, #EA580C, #C2410C); color: white; border: none; padding: 12px 24px; border-radius: 12px; font-weight: 700; cursor: pointer; font-size: 0.95rem; box-shadow: 0 4px 15px rgba(234,88,12,0.25); transition: 0.2s; }
.btn-add:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(234,88,12,0.35); }

/* Grid */
.grid-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 20px; }
.item-card { background: white; border-radius: 20px; overflow: hidden; border: 1px solid #F1F5F9; box-shadow: 0 4px 12px rgba(0,0,0,0.03); transition: 0.3s; }
.item-card:hover { transform: translateY(-4px); box-shadow: 0 12px 30px rgba(0,0,0,0.08); }
.img-frame { position: relative; overflow: hidden; }
.cat-img { width: 100%; height: 140px; object-fit: cover; display: block; transition: 0.3s; }
.item-card:hover .cat-img { transform: scale(1.05); }
.img-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; gap: 10px; opacity: 0; transition: 0.2s; }
.item-card:hover .img-overlay { opacity: 1; }
.btn-action { width: 36px; height: 36px; border-radius: 50%; border: none; cursor: pointer; background: white; box-shadow: 0 2px 8px rgba(0,0,0,0.2); font-size: 1rem; display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.btn-action:hover { transform: scale(1.15); }
.cat-info { padding: 16px; }
.cat-info h3 { margin: 0 0 4px; font-size: 1rem; font-weight: 700; color: #1E293B; }
.cat-info p { color: #94A3B8; font-size: 0.82rem; margin: 0; }

/* Skeleton */
.skeleton { pointer-events: none; }
.skel-img { height: 140px; background: linear-gradient(90deg, #F1F5F9 25%, #E2E8F0 50%, #F1F5F9 75%); background-size: 200% 100%; animation: shimmer 1.4s infinite; }
.skel-body { padding: 16px; }
.skel-line { height: 14px; background: #F1F5F9; border-radius: 6px; margin-bottom: 8px; animation: shimmer 1.4s infinite; }
.skel-line.short { width: 60%; }
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }

.error-banner { background: #FEF2F2; border: 1px solid #FECACA; border-radius: 12px; padding: 16px 20px; color: #DC2626; font-weight: 600; display: flex; gap: 12px; align-items: center; }
.error-banner button { background: #DC2626; color: white; border: none; padding: 6px 14px; border-radius: 8px; cursor: pointer; font-weight: 700; }

.empty-state { grid-column: 1/-1; text-align: center; padding: 60px; color: #94A3B8; font-size: 1rem; }

/* Modal */
.modal-overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.6); backdrop-filter: blur(6px); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-box { background: white; padding: 32px; border-radius: 24px; width: 440px; max-width: 90vw; box-shadow: 0 25px 60px -15px rgba(0,0,0,0.25); }
.modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.modal-header h3 { margin: 0; font-size: 1.3rem; font-weight: 800; color: #0F172A; }
.btn-x { background: #F1F5F9; border: none; width: 32px; height: 32px; border-radius: 50%; cursor: pointer; font-size: 0.9rem; transition: 0.2s; }
.btn-x:hover { background: #FEE2E2; color: #EF4444; }
.form-group { margin-bottom: 18px; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 700; color: #334155; font-size: 0.9rem; }
.req { color: #EF4444; }
.form-group input { width: 100%; padding: 11px 14px; border: 1.5px solid #E2E8F0; border-radius: 10px; font-size: 0.95rem; box-sizing: border-box; transition: 0.2s; }
.form-group input:focus { outline: none; border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234,88,12,0.12); }
.img-preview { margin-top: 10px; border-radius: 12px; overflow: hidden; border: 1px solid #E2E8F0; }
.img-preview img { width: 100%; height: 100px; object-fit: cover; display: block; }
.modal-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 24px; }
.btn-cancel { background: #F1F5F9; border: none; padding: 11px 22px; border-radius: 10px; cursor: pointer; font-weight: 600; color: #475569; transition: 0.2s; }
.btn-cancel:hover { background: #E2E8F0; }
.btn-save { background: linear-gradient(135deg, #EA580C, #C2410C); color: white; border: none; padding: 11px 24px; border-radius: 10px; cursor: pointer; font-weight: 700; transition: 0.2s; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-save:not(:disabled):hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(234,88,12,0.3); }

.fade-up-enter-active, .fade-up-leave-active { transition: all 0.25s ease; }
.fade-up-enter-from, .fade-up-leave-to { opacity: 0; transform: scale(0.95); }
</style>