<template>
  <div class="category-sovereign-wrapper">
    <div class="page-header-lux">
      <div>
        <h1 class="page-title">{{ t('admin.categories.title') }}</h1>
        <p class="page-subtitle">{{ t('admin.categories.subtitle') }}</p>
      </div>
      <button class="btn-action-lux" @click="openModal()">
        <Plus :size="20" stroke-width="3" />
        {{ t('admin.categories.add') }}
      </button>
    </div>

    <div class="stats-grid" v-if="!loading && !error">
      <div class="stat-card">
        <div class="icon-wrap all"><Layers :size="22" /></div>
        <div class="stat-info">
          <span class="label">{{ t('admin.categories.total_categories') }}</span>
          <h3 class="value">{{ categories.length }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="icon-wrap post"><FileText :size="22" /></div>
        <div class="stat-info">
          <span class="label">{{ t('admin.categories.total_posts') }}</span>
          <h3 class="value">{{ totalPosts }}</h3>
        </div>
      </div>
      <div class="stat-card highlight-card">
        <div class="icon-wrap top"><Zap :size="22" /></div>
        <div class="stat-info">
          <span class="label">{{ t('admin.categories.busiest') }}</span>
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
      <button @click="loadCategories">{{ t('admin.categories.retry') }}</button>
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
            <FileText :size="12" /> {{ t('admin.categories.posts_count', { count: cat.postCount ?? 0 }) }}
          </div>

          <div v-if="cat.isActive === 0" class="hidden-badge">{{ t('admin.categories.hidden_badge') }}</div>

          <div class="overlay-actions">
            <button @click.stop="goToPostsByCategory(cat)" class="btn-act view" :title="t('admin.categories.view_posts_hint')">
              <FileText :size="20" />
            </button>
            <button @click.stop="openModal(cat)" class="btn-act edit" :title="t('admin.categories.edit_title')">
              <Edit3 :size="20" />
            </button>
            <button @click.stop="toggleStatus(cat)" class="btn-act toggle" :title="cat.isActive === 1 ? t('admin.categories.hide_title') : t('admin.categories.show_title')">
              <EyeOff v-if="cat.isActive === 1" :size="20" />
              <Eye v-else :size="20" />
            </button>
            <button @click.stop="deleteCat(cat)" class="btn-act delete" :title="t('admin.categories.delete_title')">
              <Trash2 :size="20" />
            </button>
          </div>
        </div>

        <div class="card-content">
          <h3 class="cat-name">{{ cat.categoryName }}</h3>
          <div class="stat-row">
            <span class="ratio-text">{{ t('admin.categories.ratio') }}</span>
            <span class="ratio-pct">{{ getPercentage(cat.postCount) }}%</span>
          </div>
          <div class="progress-bar">
            <div class="fill" :style="{ width: getPercentage(cat.postCount) + '%' }"></div>
          </div>
          <div class="view-posts-hint">{{ t('admin.categories.view_posts_hint') }}</div>
        </div>
      </div>

      <div v-if="categories.length === 0" class="empty-state">
        {{ t('admin.categories.empty_full') }}
      </div>
    </div>

    <Teleport to="body">
      <Transition name="fade-glass">
        <div v-if="showModal" class="modal-glass-backdrop" @click.self="showModal = false">
          
          <div class="modal-lux-content" @click.stop>
            <div class="modal-header-lux">
              <h3>{{ isEditing ? t('admin.categories.edit_modal_title') : t('admin.categories.create_modal_title') }}</h3>
              <button class="btn-x" @click="showModal = false"><X :size="24" /></button>
            </div>
            
            <div class="modal-body-lux">
              <div class="form-group-lux">
                <label>{{ t('admin.categories.slug_name') }} <span class="req">*</span></label>
                <input v-model="form.categoryName" type="text" class="input-lux" :placeholder="t('admin.categories.name_placeholder')">
              </div>
              
              <div class="form-group-lux">
                <label>{{ t('admin.categories.cover_image') }}</label>
                <div class="upload-custom-zone" @click="$refs.fileInput.click()">
                  <input type="file" ref="fileInput" @change="handleFileUpload" hidden accept="image/*">
                  <div v-if="!form.imagePreview && !form.categoryImage" class="up-placeholder">
                     <UploadCloud :size="32" />
                     <span>{{ t('admin.categories.upload_hint') }}</span>
                  </div>
                  <img v-else :src="form.imagePreview || form.categoryImage" class="preview-img-box" />
                  <div class="up-overlay" v-if="form.imagePreview || form.categoryImage">{{ t('admin.categories.change_image') }}</div>
                </div>
              </div>
            </div>

            <div class="modal-footer-lux">
              <button @click="showModal = false" class="btn-lux btn-reject">{{ t('admin.categories.close') }}</button>
              <button @click="saveData" class="btn-lux btn-resolve" :disabled="saving">
                <span v-if="saving" class="spinner"></span>
                {{ saving ? t('admin.categories.saving') : t('admin.categories.complete') }}
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
import { useI18n } from 'vue-i18n'
import { 
  Plus, Edit3, Trash2, Eye, EyeOff, FileText, 
  Layers, Zap, AlertTriangle, UploadCloud, X 
} from 'lucide-vue-next'
import api from '@/services/api.js'
import { uploadMedia } from '@/services/uploadService'
import { toast } from '@/composables/useToast'

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

const totalPosts = computed(() => {
  return categories.value.reduce((sum, cat) => sum + (Number(cat.postCount) || 0), 0)
})

const topCategory = computed(() => {
  if (!categories.value || categories.value.length === 0) return null
  return categories.value.reduce((prev, curr) => (Number(prev.postCount) || 0) > (Number(curr.postCount) || 0) ? prev : curr)
})

const getPercentage = (count) => {
  const safeCount = Number(count) || 0;
  if (totalPosts.value === 0 || safeCount === 0) return '0.0';
  return ((safeCount / totalPosts.value) * 100).toFixed(1);
}

const loadCategories = async () => {
  loading.value = true
  error.value = null
  try {
    const res = await api.get('/api/admin/categories')
    categories.value = res.data.map(cat => {
      let activeCount = Number(cat.postCount || cat.totalPosts || cat.count || 0);
      
      if (activeCount === 0 && Array.isArray(cat.posts)) {
        activeCount = cat.posts.filter(p => p.isActive === 1 || p.isActive === true).length;
      }
      
      return { ...cat, postCount: activeCount };
    })
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
    if (idx !== -1) {
      // Giữ lại postCount vì response toggle có thể không chứa list posts
      categories.value[idx] = { ...res.data, postCount: cat.postCount }
    }
    toast.success(res.data.isActive === 1 ? t('admin.categories.shown_ok') : t('admin.categories.hidden_ok'))
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

const openModal = (item = null) => {
  showModal.value = true
  
  if (item && typeof item === 'object' && 'categoryID' in item) { 
    isEditing.value = true
    form.categoryID = item.categoryID
    form.categoryName = item.categoryName || ''
    form.categoryImage = item.categoryImage || ''
    form.imageFile = null
    form.imagePreview = ''
    form.isActive = item.isActive !== undefined ? item.isActive : 1
  } else {
    isEditing.value = false
    form.categoryID = null
    form.categoryName = ''
    form.categoryImage = ''
    form.imageFile = null
    form.imagePreview = ''
    form.isActive = 1
  }
  
  if (fileInput.value) fileInput.value.value = ''
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
      categoryName: form.categoryName.trim(),
      categoryImage: finalUrl,
      isActive: form.isActive
    }

    if (isEditing.value) {
      const res = await api.put(`/api/admin/categories/${form.categoryID}`, payload)
      const idx = categories.value.findIndex(c => c.categoryID === form.categoryID)
      if (idx !== -1) {
        categories.value[idx] = { ...res.data, postCount: categories.value[idx].postCount }
      }
      toast.success(t('admin.categories.update_ok'))
    } else {
      const res = await api.post('/api/admin/categories', payload)
      categories.value.unshift({ ...res.data, postCount: 0 })
      toast.success(t('admin.categories.create_ok'))
    }
    showModal.value = false
  } catch (e) {
    toast.error(t('admin.categories.save_failed_prefix') + (e.response?.data?.message || ''))
  } finally {
    saving.value = false
  }
}

const deleteCat = async (cat) => {
  if (cat.categoryID === 1) {
    return toast.error(t('admin.categories.default_delete_forbidden'));
  }

  let confirmMessage = t('admin.categories.delete_confirm_empty', { name: cat.categoryName });
  
  if (cat.postCount > 0) {
    confirmMessage = t('admin.categories.delete_confirm_with_posts', { name: cat.categoryName, count: cat.postCount });
  }

  if (!window.confirm(confirmMessage)) return;

  try {
    await api.delete(`/api/admin/categories/${cat.categoryID}`);
    categories.value = categories.value.filter(c => c.categoryID !== cat.categoryID);
    
    if (cat.postCount > 0) {
      await loadCategories(); 
      toast.success(t('admin.categories.delete_ok_with_posts', { count: cat.postCount }));
    } else {
      toast.success(t('admin.categories.delete_ok'));
    }

  } catch (e) {
    toast.error(e.response?.data?.message || t('admin.categories.delete_failed_prefix'));
  }
}

onMounted(loadCategories)
</script>

<style scoped lang="scss" src="./CategoryManagement.scss"></style>