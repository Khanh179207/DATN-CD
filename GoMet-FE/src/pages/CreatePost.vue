<template>
  <div class="create-post-container">
    
    <section class="hero-section-full-bleed fade-in-up">
      <div class="hero-container-inner">
        
        <div class="hero-info-col">
          <div class="top-nav-bar">
            <div class="nav-left">
              <button @click="$router.back()" class="btn-back-simple">
                <div class="icon-circle"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 12H5M12 19l-7-7 7-7"/></svg></div>
                <span>Cancel</span>
              </button>
              <span class="sep">/</span>
              <div class="category-wrapper">
                <select v-model="post.categoryID" class="category-select">
                  <option value="" disabled>SELECT CATEGORY</option>
                  <option v-for="cat in categories" :key="cat.categoryID" :value="cat.categoryID">{{ cat.categoryName }}</option>
                </select>
              </div>
            </div>
          </div>

          <textarea 
            v-model="post.title" 
            class="recipe-title-input" 
            placeholder="Enter dish name..." 
            rows="1"
            @input="autoResize"
          ></textarea>
          
          <textarea 
            v-model="post.description" 
            class="recipe-desc-input" 
            placeholder="Write a short, enticing description of this dish..." 
            rows="2"
            @input="autoResize"
          ></textarea>

          <div class="recipe-meta-row">
            <div class="meta-box">
              <span class="icon">⏱️</span>
              <div class="meta-detail">
                <span class="label">TIME</span>
                <input v-model="post.cookingTime" placeholder="e.g. 45min" class="meta-inp">
              </div>
            </div>
            <div class="meta-divider"></div>
            <div class="meta-box">
              <span class="icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/></svg>
              </span>
              <div class="meta-detail">
                <span class="label">DIFFICULTY</span>
                <select v-model="post.level" class="meta-select">
                  <option>Easy</option>
                  <option>Medium</option>
                  <option>Hard</option>
                </select>
              </div>
            </div>
            <div class="meta-divider"></div>
            <div class="meta-box">
              <span class="icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 22a9 9 0 1 1 18 0"/><path d="M1 22h22"/><path d="M12 13a5 5 0 0 0 5-5A5 5 0 0 0 9.8 4.2"/><path d="M12 13a5 5 0 0 1-3.5-1.5"/></svg>
              </span>
              <div class="meta-detail">
                <span class="label">SERVINGS</span>
                <input v-model="post.servings" placeholder="2 servings" class="meta-inp">
              </div>
            </div>
          </div>

          <div class="author-action-row">
            <div class="author-block">
              <img src="https://ui-avatars.com/api/?name=Me&background=random" class="auth-img">
              <div class="auth-text">
                <span class="label">Chef</span>
                <span class="name">Me (You)</span>
              </div>
            </div>
          </div>
        </div>

        <div class="hero-image-col">
          <div class="image-frame-hero uploader-box" :class="{ 'has-image': post.image }" @click="triggerUpload">
            <img v-if="post.image" :src="post.image" class="img-hero-cover">
            <div v-else class="upload-placeholder">
              <div class="icon-camera">📷</div>
              <span>Upload Cover Photo (HD)</span>
            </div>
            <div v-if="post.image" class="edit-overlay"><span>Change Photo</span></div>
            <input type="file" ref="fileInput" class="hidden-input" @change="handleImageUpload">
          </div>
        </div>

      </div>
    </section>

    <section class="body-section-premium">
      <div class="body-container-inner">
        
        <aside class="sidebar-left-sticky">
          <div class="sticky-wrapper">
            <div class="premium-card ingredients-card">
              <div class="card-header-gradient">
                <div class="header-content">
                  <h3>🛒 Ingredients</h3>
                  <span class="sub-text">What do you need?</span>
                </div>
              </div>
              
              <div class="ingredients-list-editor">
                <div v-for="(ing, index) in post.ingredients" :key="index" class="ing-row-edit">
                  <div class="checkbox-visual"><svg viewBox="0 0 24 24"><polyline points="20 6 9 17 4 12"></polyline></svg></div>
                  <input v-model="ing.name" class="ing-input-text" placeholder="e.g.: 500g beef belly..." @keyup.enter="addIngredient">
                  <button class="btn-remove" @click="removeIngredient(index)" v-if="post.ingredients.length > 1">×</button>
                </div>
              </div>

              <button class="btn-add-dashed" @click="addIngredient">+ Add ingredient row</button>
            </div>
          </div>
        </aside>

        <main class="main-right-content">
          <div class="premium-card steps-card">
            <div class="steps-header-modern">
              <h2>Recipe Steps</h2>
              <div class="step-counter-badge">{{ post.steps.length }} Steps</div>
            </div>

            <div class="timeline-editorial">
              <transition-group name="list">
                <div class="timeline-step" v-for="(step, index) in post.steps" :key="step.id">
                  
                  <div class="step-marker-col">
                    <div class="step-number-art">{{ index + 1 }}</div>
                    <div class="step-connector" v-if="index !== post.steps.length - 1"></div>
                  </div>

                  <div class="step-content-col">
                    <div class="step-top-row">
                      <h4 class="step-heading">STEP {{ index + 1 }}</h4>
                      <button class="btn-del-step" @click="removeStep(index)" v-if="post.steps.length > 1">Remove</button>
                    </div>
                    
                    <textarea 
                      v-model="step.desc" 
                      class="step-desc-input" 
                      placeholder="Describe this step in detail..."
                      rows="3"
                      @input="autoResize"
                    ></textarea>
                    
                    <div class="step-media-upload">
                      <div v-if="step.image" class="media-preview" @click="triggerStepUpload(index)">
                        <img :src="step.image">
                        <div class="hover-change">Change image</div>
                      </div>
                      <div v-else class="upload-trigger-small" @click="triggerStepUpload(index)">
                        <span class="icon">📷</span> Add photo
                      </div>
                      <input type="file" :ref="el => stepInputRefs[index] = el" class="hidden-input" @change="handleStepUpload($event, index)">
                    </div>
                  </div>

                </div>
              </transition-group>
            </div>

            <button class="btn-add-step-large" @click="addStep">Add next step</button>
          </div>
        </main>

      </div>
    </section>

    <div class="action-footer">
      <div class="footer-container">
        <div class="left">
          <button class="btn-preview">👁️ Preview</button>
        </div>
        <div class="right">
          <button class="btn-draft">Save Draft</button>
          <button class="btn-publish" :disabled="publishing" @click="handlePublish">
              {{ publishing ? 'Publishing...' : 'Publish Post' }}
            </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getCategories } from '@/services/categoryService'
import { createPost } from '@/services/postService'
import { uploadMedia } from '@/services/uploadService'
import { toast } from '@/composables/useToast'

const router = useRouter()
const authStore = useAuthStore()
const fileInput = ref(null)
const stepInputRefs = ref([])
const categories = ref([])
const publishing = ref(false)
const coverImageFile = ref(null)          // actual File for upload
const stepImageFiles = ref({})             // { [stepIndex]: File }

const currentUser = computed(() => authStore.user || {})

const post = ref({
  title: '',
  description: '',
  categoryID: '',
  image: null,
  cookingTime: '',
  level: 'Medium',
  servings: '',
  ingredients: [{ name: '' }, { name: '' }, { name: '' }],
  steps: [{ id: 1, desc: '', image: null }],
})

// Load categories from API
onMounted(async () => {
  try {
    categories.value = await getCategories()
  } catch {
    // fallback to static
    categories.value = [
      { categoryID: 1, categoryName: 'Main Dish' },
      { categoryID: 2, categoryName: 'Breakfast' },
      { categoryID: 3, categoryName: 'Healthy' },
      { categoryID: 4, categoryName: 'Dessert' },
    ]
  }
})

// --- FIX: Use function instead of child component ---
const autoResize = (event) => {
  const element = event.target
  element.style.height = 'auto'
  element.style.height = element.scrollHeight + 'px'
}

const triggerUpload = () => fileInput.value.click()
const handleImageUpload = (e) => {
  const file = e.target.files[0]
  if (!file) return
  coverImageFile.value = file
  post.value.image = URL.createObjectURL(file)  // preview only
}

const triggerStepUpload = (idx) => stepInputRefs.value[idx].click()
const handleStepUpload = (e, idx) => {
  const file = e.target.files[0]
  if (!file) return
  stepImageFiles.value[idx] = file
  post.value.steps[idx].image = URL.createObjectURL(file)  // preview only
}

const addIngredient = () => post.value.ingredients.push({ name: '' })
const removeIngredient = (idx) => { if(post.value.ingredients.length > 1) post.value.ingredients.splice(idx, 1) }

const addStep = () => post.value.steps.push({ id: Date.now(), desc: '', image: null })
const removeStep = (idx) => { if(post.value.steps.length > 1) post.value.steps.splice(idx, 1) }

const levelToInt = (lv) => ({ 'Easy': 1, 'Medium': 2, 'Hard': 3 }[lv] ?? 2)

const handlePublish = async () => {
  if (!post.value.title.trim()) { toast.warn('Please enter a dish name!'); return }
  if (!post.value.categoryID) { toast.warn('Please select a category!'); return }
  if (publishing.value) return

  publishing.value = true
  try {
    const accountID = currentUser.value.accountID || currentUser.value.id
    const ingredientsStr = post.value.ingredients.map(i => i.name).filter(Boolean).join(', ')
    const cookingTimeInt = parseInt(post.value.cookingTime) || 30

    // Upload cover image if user selected one
    let coverMediaUrl = ''
    if (coverImageFile.value) {
      try {
        coverMediaUrl = await uploadMedia(coverImageFile.value)
      } catch { /* cover upload failed — continue without image */ }
    }

    // Upload each step image that has a file
    const stepUrls = {}
    for (const [idx, file] of Object.entries(stepImageFiles.value)) {
      try {
        stepUrls[idx] = await uploadMedia(file)
      } catch { /* step upload failed — continue without image */ }
    }

    const payload = {
      accountID,
      categoryID: parseInt(post.value.categoryID),
      title: post.value.title,
      description: post.value.description,
      ingredients: ingredientsStr,
      media: coverMediaUrl,
      level: levelToInt(post.value.level),
      cookingTime: cookingTimeInt,
      steps: post.value.steps.map((s, i) => ({
        desc: s.desc,
        image: stepUrls[i] || null
      }))
    }

    const result = await createPost(payload)
    toast.success('Post submitted for review!')
    const newId = result?.postID
    if (newId) {
      router.push(`/post/${newId}`)
    } else {
      router.push('/home')
    }
  } catch (err) {
    toast.error('Failed to publish. Please try again.')
  } finally {
    publishing.value = false
  }
}
</script>

<style scoped>
.create-post-container { width: 100%; font-family: 'Mulish', sans-serif; color: #1C1917; overflow-x: hidden; background: #F8FAFC; display: flex; flex-direction: column; min-height: 100vh; }

/* HERO SECTION */
.hero-section-full-bleed { width: calc(100% + 80px); margin-left: -40px; margin-right: -40px; margin-top: -40px; padding: 60px 40px 60px; background-color: #fff; border-bottom: 1px solid #F3F4F6; position: relative; z-index: 1; }
.hero-container-inner { max-width: 1200px; margin: 0 auto; display: grid; grid-template-columns: 1fr 1fr; gap: 60px; align-items: start; }

/* Typography Inputs */
.recipe-title-input { font-family: 'Playfair Display', serif; font-size: 3.5rem; font-weight: 800; line-height: 1.1; color: #111827; margin-bottom: 16px; border: none; outline: none; width: 100%; background: transparent; resize: none; overflow: hidden; min-height: 80px; }
.recipe-title-input::placeholder { color: #E5E7EB; }
.recipe-desc-input { font-family: 'Mulish', sans-serif; font-size: 1.15rem; line-height: 1.8; color: #4B5563; margin-bottom: 32px; border: none; outline: none; width: 100%; resize: none; background: transparent; min-height: 80px; font-weight: 400; }

/* Nav */
.top-nav-bar { display: flex; margin-bottom: 20px; align-items: center; }
.nav-left { display: flex; align-items: center; gap: 15px; }
.btn-back-simple { display: flex; align-items: center; gap: 8px; background: none; border: none; color: #6B7280; font-weight: 700; cursor: pointer; }
.btn-back-simple .icon-circle { width: 32px; height: 32px; border-radius: 50%; background: #F3F4F6; display: flex; align-items: center; justify-content: center; }
.sep { color: #E5E7EB; }
.category-select { border: none; background: #FFF7ED; color: #EA580C; font-weight: 800; padding: 6px 12px; border-radius: 8px; cursor: pointer; outline: none; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 1px; }

/* Meta */
.recipe-meta-row { display: flex; align-items: center; gap: 24px; margin-bottom: 32px; }
.meta-box { display: flex; align-items: center; gap: 10px; }
.meta-box .icon { font-size: 1.4rem; }
.meta-detail { display: flex; flex-direction: column; }
.meta-detail .label { font-size: 0.65rem; font-weight: 700; color: #9CA3AF; letter-spacing: 0.5px; }
.meta-inp, .meta-select { border: none; background: #F9FAFB; padding: 4px 8px; border-radius: 6px; font-weight: 700; color: #1F2937; font-size: 1rem; width: 100px; outline: none; }
.meta-divider { width: 1px; height: 32px; background: #E5E7EB; }

/* Author */
.author-action-row { display: flex; align-items: center; }
.author-block { display: flex; align-items: center; gap: 12px; }
.auth-img { width: 44px; height: 44px; border-radius: 50%; object-fit: cover; }
.auth-text .label { font-size: 0.7rem; color: #6B7280; }
.auth-text .name { font-weight: 700; color: #111827; }

/* Image Uploader */
.image-frame-hero { position: relative; height: 520px; border-radius: 32px; overflow: hidden; box-shadow: 20px 30px 60px -10px rgba(0,0,0,0.1); background: #F3F4F6; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.3s; }
.image-frame-hero:hover { background: #E5E7EB; }
.img-hero-cover { width: 100%; height: 100%; object-fit: cover; }
.upload-placeholder { text-align: center; color: #9CA3AF; display: flex; flex-direction: column; align-items: center; gap: 10px; }
.icon-camera { font-size: 3rem; opacity: 0.5; }
.hidden-input { display: none; }
.edit-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; opacity: 0; transition: 0.2s; color: white; font-weight: 700; }
.image-frame-hero:hover .edit-overlay { opacity: 1; }

/* BODY */
.body-section-premium { width: 100%; background-color: #F8FAFC; padding: 60px 0; flex: 1; padding-bottom: 100px; }
.body-container-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; display: grid; grid-template-columns: 380px 1fr; gap: 60px; }
.sticky-wrapper { position: sticky; top: 100px; }

/* Card */
.premium-card { background: white; border-radius: 24px; border: 1px solid #E2E8F0; box-shadow: 0 10px 30px -5px rgba(0,0,0,0.03); overflow: hidden; }
.card-header-gradient { background: linear-gradient(135deg, #FFF7ED 0%, #FFFFFF 100%); padding: 25px 30px; border-bottom: 1px solid #FED7AA; }
.header-content h3 { font-family: 'Playfair Display', serif; font-size: 1.6rem; margin: 0; color: #111827; }
.header-content .sub-text { font-size: 0.85rem; color: #9A3412; }

/* Ingredients */
.ingredients-list-editor { padding: 10px 0; }
.ing-row-edit { display: flex; align-items: center; gap: 12px; padding: 12px 30px; border-bottom: 1px dashed #E5E5E5; transition: 0.2s; }
.checkbox-visual { width: 22px; height: 22px; border: 2px solid #E2E8F0; border-radius: 6px; display: flex; align-items: center; justify-content: center; }
.checkbox-visual svg { width: 14px; stroke: #E2E8F0; }
.ing-input-text { flex: 1; border: none; outline: none; font-size: 1.05rem; color: #334155; font-weight: 500; background: transparent; }
.btn-remove { color: #EF4444; background: none; border: none; cursor: pointer; font-size: 1.2rem; opacity: 0; transition: 0.2s; }
.ing-row-edit:hover .btn-remove { opacity: 1; }
.btn-add-dashed { width: calc(100% - 60px); margin: 10px 30px 30px; padding: 14px; border: 2px dashed #CBD5E1; background: #F8FAFC; color: #64748B; border-radius: 12px; font-weight: 700; cursor: pointer; transition: 0.2s; }
.btn-add-dashed:hover { border-color: #EA580C; color: #EA580C; background: #FFF7ED; }

/* Steps */
.steps-card { padding: 40px; }
.steps-header-modern { display: flex; justify-content: space-between; align-items: center; margin-bottom: 40px; border-bottom: 2px solid #F1F5F9; padding-bottom: 20px; }
.steps-header-modern h2 { font-family: 'Playfair Display', serif; font-size: 2.2rem; margin: 0; color: #111827; }
.step-counter-badge { background: #111827; color: white; padding: 6px 16px; border-radius: 20px; font-weight: 700; font-size: 0.9rem; }

.timeline-editorial { display: flex; flex-direction: column; gap: 50px; margin-bottom: 40px; }
.timeline-step { display: grid; grid-template-columns: 80px 1fr; gap: 30px; }
.step-marker-col { display: flex; flex-direction: column; align-items: center; }
.step-number-art { width: 60px; height: 60px; display: flex; align-items: center; justify-content: center; font-family: 'Playfair Display', serif; font-size: 2rem; font-weight: 900; color: #EA580C; background: #FFF7ED; border-radius: 20px; border: 2px solid #FFEDD5; box-shadow: 4px 4px 0 #FED7AA; }
.step-connector { width: 2px; flex: 1; border-left: 2px dashed #E2E8F0; margin-top: 20px; }

.step-content-col { padding-top: 5px; }
.step-top-row { display: flex; justify-content: space-between; margin-bottom: 10px; }
.step-heading { font-size: 0.9rem; text-transform: uppercase; color: #94A3B8; font-weight: 800; letter-spacing: 2px; margin: 0; }
.btn-del-step { color: #EF4444; background: none; border: none; font-size: 0.8rem; cursor: pointer; text-decoration: underline; }

.step-desc-input { width: 100%; border: 1px solid #E2E8F0; padding: 20px; border-radius: 16px; font-size: 1.1rem; line-height: 1.6; color: #334155; resize: none; outline: none; background: white; transition: 0.2s; font-family: 'Mulish', sans-serif; }
.step-desc-input:focus { border-color: #EA580C; background: #FFF7ED; }

.step-media-upload { margin-top: 15px; }
.upload-trigger-small { width: 100%; border: 2px dashed #E2E8F0; padding: 15px; border-radius: 12px; color: #94A3B8; font-weight: 600; cursor: pointer; text-align: center; display: flex; align-items: center; justify-content: center; gap: 8px; transition: 0.2s; }
.upload-trigger-small:hover { border-color: #EA580C; color: #EA580C; background: #FFF7ED; }
.media-preview { position: relative; border-radius: 16px; overflow: hidden; max-width: 300px; cursor: pointer; }
.media-preview img { width: 100%; display: block; }
.hover-change { position: absolute; inset: 0; background: rgba(0,0,0,0.5); color: white; display: flex; align-items: center; justify-content: center; opacity: 0; transition: 0.2s; font-weight: 700; }
.media-preview:hover .hover-change { opacity: 1; }

.btn-add-step-large { width: 100%; padding: 16px; background: #F1F5F9; color: #64748B; font-weight: 700; border: none; border-radius: 16px; cursor: pointer; transition: 0.2s; font-size: 1rem; }
.btn-add-step-large:hover { background: #E2E8F0; color: #1E293B; }

/* ACTION FOOTER */
.action-footer { position: sticky; bottom: 0; background: white; border-top: 1px solid #E2E8F0; padding: 15px 0; z-index: 99; margin-top: auto; box-shadow: 0 -4px 20px rgba(0,0,0,0.05); }
.footer-container { max-width: 1200px; margin: 0 auto; padding: 0 40px; display: flex; justify-content: space-between; align-items: center; }
.btn-preview { background: none; border: none; font-weight: 600; color: #64748B; cursor: pointer; }
.right { display: flex; gap: 12px; }
.btn-draft { padding: 10px 24px; border: 1px solid #E2E8F0; background: white; border-radius: 30px; font-weight: 700; color: #475569; cursor: pointer; }
.btn-publish { padding: 10px 30px; border: none; background: #EA580C; border-radius: 30px; font-weight: 700; color: white; cursor: pointer; box-shadow: 0 4px 12px rgba(234, 88, 12, 0.3); }
.btn-publish:hover { background: #C2410C; transform: translateY(-2px); }

/* Animation List */
.list-enter-active, .list-leave-active { transition: all 0.3s ease; }
.list-enter-from, .list-leave-to { opacity: 0; transform: translateX(-20px); }

@media (max-width: 1024px) {
  .hero-section-full-bleed { margin: -20px -20px 0; width: calc(100% + 40px); padding: 40px 20px; }
  .hero-container-inner { grid-template-columns: 1fr; gap: 40px; text-align: center; }
  .hero-info-col { order: 2; } .hero-image-col { order: 1; }
  .top-nav-bar, .recipe-meta-row, .author-action-row { justify-content: center; }
  .image-frame-hero { height: 350px; }
  .body-container-inner { grid-template-columns: 1fr; gap: 40px; }
  .sticky-wrapper { position: static; }
  .step-gallery-floating { grid-template-columns: 1fr; }
}
</style>