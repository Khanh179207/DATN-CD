<template>
  <div class="category-sovereign-wrapper">
    <div class="page-header-lux">
      <div>
        <h1 class="page-title">{{ $t('admin.categories.title') }}</h1>
        <p class="page-subtitle">{{ $t('admin.categories.subtitle') }}</p>
      </div>
      <button class="btn-action-lux" @click="openModal()">
        <Plus :size="20" stroke-width="3" />
        {{ $t('admin.categories.add') }}
      </button>
    </div>

    <div class="stats-grid" v-if="!loading && !error">
      <div class="stat-card">
        <div class="icon-wrap all"><Layers :size="22" /></div>
        <div class="stat-info">
          <span class="label">{{ $t('admin.categories.total_categories') }}</span>
          <h3 class="value">{{ categories.length }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon-wrap post"><FileText :size="22" /></div>
        <div class="stat-info">
          <span class="label">{{ $t('admin.categories.total_posts') }}</span>
          <h3 class="value">{{ totalPosts }}</h3>
        </div>
      </div>
      <div class="stat-card highlight-card">
        <div class="icon-wrap top"><Zap :size="22" /></div>
        <div class="stat-info">
          <span class="label">{{ $t('admin.categories.busiest') }}</span>
          <h3 class="value text-truncate">{{ topCategory?.categoryName || '—' }}</h3>
        </div>
      </div>
    </div>

    <div v-if="loading" class="grid-list">
      <div class="lux-cat-card skeleton" v-for="n in 6" :key="n">
        <div class="skel-img"></div>
        <div class="skel-body"><div class="skel-line"></div><div class="skel-line short"></div></div>
      </div>
    </div>

    <div v-else-if="error" class="error-banner">
      <AlertTriangle :size="20" /> 
      <span>{{ error }}</span>
      <button @click="loadCategories">{{ $t('admin.common.retry') }}</button>
    </div>

    <div v-else class="grid-list">
      <div 
        class="lux-cat-card clickable-card" 
        v-for="cat in categories" 
        :key="cat.categoryID"
        :class="{ 'is-hidden': cat.isActive === 0 }"
        @click="goToPostsByCategory(cat)"
      >
        <div class="card-img-wrap">
          <img :src="cat.categoryImage || defaultImage" class="cat-img" :alt="cat.categoryName">
          
          <div class="post-count-badge">
            <FileText :size="12" /> {{ $t('admin.categories.posts_count', { count: cat.postCount ?? 0 }) }}
          </div>

          <div v-if="cat.isActive === 0" class="hidden-badge">{{ $t('admin.categories.hidden_badge') }}</div>

          <div class="overlay-actions">
            <button @click.stop="openModal(cat)" class="btn-act edit" :title="t('common.edit')">
              <Edit3 :size="20" />
            </button>
            <button @click.stop="toggleStatus(cat)" class="btn-act toggle" :title="cat.isActive === 1 ? t('admin.categories.hide_title') : t('admin.categories.show_title')">
              <EyeOff v-if="cat.isActive === 1" :size="20" />
              <Eye v-else :size="20" />
            </button>
            <button @click.stop="deleteCat(cat)" class="btn-act delete" :title="t('common.delete')">
              <Trash2 :size="20" />
            </button>
          </div>
        </div>

        <div class="card-content">
          <h3 class="cat-name">{{ cat.categoryName }}</h3>
          <div class="stat-row">
            <span class="ratio-text">{{ $t('admin.categories.ratio') }}</span>
            <span class="ratio-pct">{{ getPercentage(cat.postCount) }}%</span>
          </div>
          <div class="progress-bar">
            <div class="fill" :style="{ width: getPercentage(cat.postCount) + '%' }"></div>
          </div>
          <div class="view-posts-hint">{{ $t('admin.categories.view_posts_hint') }}</div>
        </div>
      </div>

      <div v-if="categories.length === 0" class="empty-state">
        {{ $t('admin.categories.empty_full') }}
      </div>
    </div>

    <Teleport to="body">
      <Transition name="fade-glass">
        <div v-if="showModal" class="modal-glass-backdrop" @click.self="showModal = false">
          
          <div class="modal-lux-content" @click.stop>
            <div class="modal-header-lux">
              <h3>{{ isEditing ? $t('admin.categories.edit_modal_title') : $t('admin.categories.create_modal_title') }}</h3>
              <button class="btn-x" @click="showModal = false"><X :size="24" /></button>
            </div>
            
            <div class="modal-body-lux">
              <div class="form-group-lux">
                <label>{{ $t('admin.categories.slug_name') }} <span class="req">*</span></label>
                <input v-model="form.categoryName" type="text" class="input-lux" :placeholder="$t('admin.categories.name_placeholder')">
              </div>
              
              <div class="form-group-lux">
                <label>{{ $t('admin.categories.cover_image') }}</label>
                <div class="upload-custom-zone" @click="$refs.fileInput.click()">
                  <input type="file" ref="fileInput" @change="handleFileUpload" hidden accept="image/*">
                  <div v-if="!form.imagePreview && !form.categoryImage" class="up-placeholder">
                     <UploadCloud :size="32" />
                     <span>{{ $t('admin.categories.upload_hint') }}</span>
                  </div>
                  <img v-else :src="form.imagePreview || form.categoryImage" class="preview-img-box" />
                  <div class="up-overlay" v-if="form.imagePreview || form.categoryImage">{{ $t('admin.categories.change_image') }}</div>
                </div>
              </div>
            </div>

            <div class="modal-footer-lux">
              <button @click="showModal = false" class="btn-lux btn-reject">{{ $t('common.close') }}</button>
              <button @click="saveData" class="btn-lux btn-resolve" :disabled="saving">
                <span v-if="saving" class="spinner"></span>
                {{ saving ? $t('admin.categories.saving') : $t('admin.categories.complete') }}
              </button>
            </div>
            
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Plus, Edit3, Trash2, Eye, EyeOff, FileText, 
  Layers, Zap, AlertTriangle, UploadCloud, X 
} from 'lucide-vue-next'
import api from '@/services/api.js'
import { uploadMedia } from '@/services/uploadService'
import { toast } from '@/composables/useToast'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t } = useI18n()
const defaultImage = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500'

const categories = ref([])
const loading = ref(true)
const error = ref(null)
const showModal = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const fileInput = ref(null)

const form = reactive({ 
  categoryID: null, 
  categoryName: '', 
  categoryImage: '', 
  imageFile: null, 
  imagePreview: '',
  isActive: 1 
})

// --- COMPUTED STATS ---
const totalPosts = computed(() => categories.value.reduce((sum, cat) => sum + (Number(cat.postCount) || 0), 0))
const topCategory = computed(() => {
  if (!categories.value.length) return null
  return categories.value.reduce((prev, curr) => (prev.postCount || 0) > (curr.postCount || 0) ? prev : curr)
})

const getPercentage = (count) => {
  if (totalPosts.value === 0) return 0
  return ((Number(count) || 0) / totalPosts.value * 100).toFixed(1)
}

// --- LOGIC FUNCTIONS ---
const loadCategories = async () => {
  loading.value = true
  error.value = null
  try {
    const res = await api.get('/api/admin/categories')
    categories.value = res.data
  } catch (e) {
    error.value = t('admin.categories.load_busy')
    toast.error(error.value)
  } finally {
    loading.value = false
  }
}

const toggleStatus = async (cat) => {
  try {
    const res = await api.patch(`/api/admin/categories/${cat.categoryID}/toggle-status`)
    const idx = categories.value.findIndex(c => c.categoryID === cat.categoryID)
    if (idx !== -1) categories.value[idx] = res.data
    toast.success(t('admin.categories.status_updated'))
  } catch (e) {
    toast.error(t('admin.categories.status_update_failed'))
  }
}

const goToPostsByCategory = (cat) => {
  router.push({
    name: 'AdminPosts',
    query: { categoryID: cat.categoryID, categoryName: cat.categoryName }
  })
}

const handleFileUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    form.imageFile = file
    form.imagePreview = URL.createObjectURL(file) 
  }
}

// 🔥 SỬA LỖI MỞ MODAL BỊ NHẬN NHẦM EVENT
const openModal = (item = null) => {
  showModal.value = true
  
  if (item && !(item instanceof Event)) { 
    isEditing.value = true
    Object.assign(form, {
      categoryID: item.categoryID,
      categoryName: item.categoryName,
      categoryImage: item.categoryImage || '',
      imageFile: null,
      imagePreview: '',
      isActive: item.isActive
    })
  } else {
    isEditing.value = false
    Object.assign(form, { 
        categoryID: null, 
        categoryName: '', 
        categoryImage: '', 
        imageFile: null, 
        imagePreview: '', 
        isActive: 1 
    })
  }
}

const saveData = async () => {
  if (!form.categoryName.trim()) return toast.warn(t('admin.categories.name_required'))
  saving.value = true
  
  try {
    let finalUrl = form.categoryImage 
    if (form.imageFile) {
      finalUrl = await uploadMedia(form.imageFile, 'categories')
    }

    const payload = {
      categoryName: form.categoryName,
      categoryImage: finalUrl,
      isActive: form.isActive
    }

    if (isEditing.value) {
      const res = await api.put(`/api/admin/categories/${form.categoryID}`, payload)
      const idx = categories.value.findIndex(c => c.categoryID === form.categoryID)
      if (idx !== -1) categories.value[idx] = res.data
      toast.success(t('admin.categories.update_ok'))
    } else {
      const res = await api.post('/api/admin/categories', payload)
      categories.value.unshift(res.data)
      toast.success(t('admin.categories.create_ok'))
    }
    showModal.value = false
  } catch (e) {
    toast.error(t('admin.categories.save_failed'))
  } finally {
    saving.value = false
  }
}

const deleteCat = async (cat) => {
  if (cat.categoryID === 1) return toast.error(t('admin.categories.default_delete_forbidden'))
  if (cat.postCount > 0) return toast.error(t('admin.categories.posts_delete_blocked'))

  if (!window.confirm(t('admin.categories.delete_confirm', { name: cat.categoryName }))) return

  try {
    await api.delete(`/api/admin/categories/${cat.categoryID}`)
    categories.value = categories.value.filter(c => c.categoryID !== cat.categoryID)
    toast.success(t('admin.categories.delete_ok'))
  } catch (e) {
    toast.error(t('admin.categories.delete_failed'))
  }
}

onMounted(loadCategories)
</script>

<style scoped lang="scss" src="./CategoryManagement.scss"></style>