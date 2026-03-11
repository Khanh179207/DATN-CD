<template>
  <div class="recipe-guide-container">
    
    <section class="hero-section-full-bleed fade-in-up">
      <div class="hero-container-inner">
        <div class="hero-info-col">
          <div class="top-nav-bar">
            <div class="nav-left">
              <button @click="$router.push('/home')" class="btn-back-simple">
                <div class="icon-circle"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 12H5M12 19l-7-7 7-7"/></svg></div>
                <span>{{ $t('recipe.back') }}</span>
              </button>
              <span class="sep">/</span>
              <span class="category-label">{{ post.category }}</span>
            </div>
            <button class="btn-report-minimal" :title="$t('common.report')">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"></path><line x1="4" y1="22" x2="4" y2="15"></line></svg>
            </button>
          </div>

          <h1 class="recipe-title-main">{{ post.title }}</h1>
          <p class="recipe-desc-main">{{ post.description }}</p>

          <div class="recipe-meta-row">
            <div class="meta-box"><span class="icon">⏱️</span><div class="meta-detail"><span class="label">{{ $t('recipe.time_label') }}</span><span class="val">{{ post.time }}</span></div></div>
            <div class="meta-divider"></div>
            <div class="meta-box"><span class="icon"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/></svg></span><div class="meta-detail"><span class="label">{{ $t('recipe.difficulty_label') }}</span><span class="val">{{ post.difficulty }}</span></div></div>
            <div class="meta-divider"></div>
            <div class="meta-box"><span class="icon"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 22a9 9 0 1 1 18 0"/><path d="M12 13a5 5 0 0 0 5-5A5 5 0 0 0 9.8 4.2"/><path d="M12 13a5 5 0 0 1-3.5-1.5"/></svg></span><div class="meta-detail"><span class="label">{{ $t('recipe.servings_label') }}</span><span class="val">{{ post.servings }}</span></div></div>
          </div>

          <div class="author-action-row">
            <div class="author-block">
              <img :src="post.authorAvatar" class="auth-img">
              <div class="auth-text"><span class="label">{{ $t('recipe.by') }}</span><span class="name">{{ post.author }}</span></div>
              <button class="btn-follow-sm">{{ $t('common.follow') }}</button>
            </div>
            <div class="action-group">
              <button class="btn-save-primary" @click="toggleFavorite">
                <svg width="20" height="20" viewBox="0 0 24 24":fill="isFavorite ? 'currentColor' : 'none'"stroke="currentColor" stroke-width="2">
                  <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path>
                </svg>
                
                <span>{{ isFavorite ? $t('common.saved') : $t('common.save') }}</span>
              
              </button>
              <button class="btn-share-circle"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="5" r="3"></circle><circle cx="6" cy="12" r="3"></circle><circle cx="18" cy="19" r="3"></circle><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"></line><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"></line></svg></button>
            </div>
          </div>
        </div>

        <div class="hero-image-col">
          <div class="image-frame-hero">
            <img :src="post.image" :alt="post.title" class="img-hero-cover">
            <div class="floating-stats"><span>📅 {{ post.publishDate || '06/02/2026' }}</span><span class="sep">•</span><span>👁️ {{ post.views || '1.2k' }} {{ $t('recipe.views_suffix') }}</span></div>
          </div>
        </div>
      </div>
    </section>

    <section class="body-section-premium">
      <div class="body-container-inner">
        
        <aside class="sidebar-left-sticky">
          <div class="sticky-wrapper">
            
            <div class="premium-card ingredients-card slide-in-left">
              <div class="card-header-gradient">
                <div class="header-content">
                  <h3>🛒 {{ $t('post.ingredients') }}</h3>
                  <span class="sub-text">{{ $t('recipe.by') }} <b>{{ post.servings }}</b></span>
                </div>
                <button class="btn-add-mini" title="Chọn tất cả" @click="selectAllIngredients">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"></polyline></svg>
                </button>
              </div>
              
              <div class="ingredients-scroll">
                <ul class="ingredients-list-premium">
                  <li v-for="(item, index) in ingredientsList" :key="index">
                    <label class="premium-checkbox-row">
                      <input type="checkbox" v-model="item.selectedForShopping">
                      <div class="checkbox-visual">
                        <svg viewBox="0 0 24 24"><polyline points="20 6 9 17 4 12"></polyline></svg>
                      </div>
                      <span class="ing-text" :class="{ is_checked: item.selectedForShopping }">{{ item.name }}</span>
                      <span class="line-through-effect"></span>
                    </label>
                  </li>
                </ul>
              </div>

              <button class="btn-shopping-cart-lg" @click="handleGoShopping">
                <span class="icon">🛍️</span> {{ $t('recipe.add_to_cart') }}
              </button>
            </div>

            <div class="premium-card note-card slide-in-left" style="animation-delay: 0.1s;">
              <div class="note-decoration"></div>
              <div class="note-inner">
                <div class="note-title">
                  <span class="icon">✏️</span> {{ $t('recipe.personal_note') }}
                </div>
                <div class="textarea-container">
                  <textarea v-model="userNote" :placeholder="$t('recipe.note_placeholder')"></textarea>
                </div>
                <div class="note-status" style="display: flex; justify-content: space-between; align-items: center; margin-top: 15px;">
                  <span class="status-badge"><span class="dot"></span> {{ $t('recipe.private') }}</span>
                  
                  <button class="btn-save-note" @click="handleSaveNote" :disabled="isSavingNote">
                    <span v-if="isSavingNote" class="loader-small"></span>
                    <svg v-else width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                      <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5z"></path>
                      <polyline points="17 21 17 13 7 13 7 21"></polyline>
                    </svg>
                    {{ isSavingNote ? 'Đang lưu...' : 'Lưu ghi chú' }}
                  </button>
                </div>
              </div>
            </div>

          </div>
        </aside>

        <main class="main-right-content">
          <div class="premium-card steps-card slide-in-up">
            <div class="steps-header-modern">
              <h2>{{ $t('recipe.process') }}</h2>
              <div class="step-counter-badge">{{ post.steps.length }} {{ $t('post.step') }}</div>
            </div>

            <div class="timeline-editorial">
              <div class="timeline-step" v-for="(step, index) in post.steps" :key="index">
                
                <div class="step-marker-col">
                  <div class="step-number-art">{{ index + 1 }}</div>
                  <div class="step-connector" v-if="index !== post.steps.length - 1"></div>
                </div>

                <div class="step-content-col">
                  <h4 class="step-heading">{{ $t('recipe.step_prefix') }} {{ index + 1 }}</h4>
                  <p class="step-desc-text">{{ step.desc }}</p>
                  
                  <div class="step-gallery-floating" v-if="step.images && step.images.length && step.images[0]">
                    <div class="gallery-item" v-for="(img, i) in step.images" :key="i">
                      <div class="img-inner">
                        <img :src="img" loading="lazy" @error="handleImageError">
                        <div class="zoom-icon"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line><line x1="11" y1="8" x2="11" y2="14"></line><line x1="8" y1="11" x2="14" y2="11"></line></svg></div>
                      </div>
                    </div>
                  </div>
                </div>

              </div>
            </div>
          </div>
        </main>

      </div>
    </section>
  </div>
</template>

<script setup>  
import { ref, watch, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import axios from 'axios'
import { useShoppingStore } from '@/stores/shopping'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import { addFavorite, removeFavorite, checkFavorite } from '@/services/socialService'

const { t } = useI18n()
const props = defineProps({
  post: { type: Object, required: true }
})

const authStore = useAuthStore()  
const shoppingStore = useShoppingStore()
const userNote = ref('')
const isSavingNote = ref(false)
const ingredientsList = ref([])

// --- LOGIC 1: GHI CHÚ CÁ NHÂN (DATABASE) ---

const fetchNote = async () => {
  if (!authStore.isAuthenticated) return
  try {
    const res = await axios.get('http://localhost:8080/api/notes', {
      params: { 
        accountId: authStore.currentUser.accountID, 
        postId: props.post.id 
      }
    })
    if (res.data) userNote.value = res.data
  } catch (e) {
    console.log("Sếp chưa có ghi chú cho bài viết này.")
  }
}

const handleSaveNote = async () => {
  if (!authStore.isAuthenticated) {
    toast.warn('Đăng nhập để lưu ghi chú Sếp nhé!')
    return
  }
  isSavingNote.value = true
  try {
    await axios.post('http://localhost:8080/api/notes/save', {
      accountId: authStore.currentUser.accountID,
      postId: props.post.id,
      content: userNote.value
    })
    toast.success('Bí quyết của Sếp đã được lưu an toàn!')
  } catch (e) {
    toast.error('Lỗi hệ thống, Sếp check lại Backend nhé!')
  } finally {
    isSavingNote.value = false
  }
}

// --- LOGIC 2: NGUYÊN LIỆU & GIỎ HÀNG ---

const selectAllIngredients = () => {
  ingredientsList.value.forEach(item => {
    item.selectedForShopping = true
  })
}

watch(() => props.post.ingredientsRaw, (newVal) => {
  if (!newVal) {
    ingredientsList.value = []
    return
  }
  ingredientsList.value = newVal
    .split(',')
    .map(item => item.trim())
    .filter(item => item !== '')
    .map(item => ({ 
      name: item, 
      selectedForShopping: shoppingStore.items.some(i => i.name === item)
    }))
}, { immediate: true })

watch(() => shoppingStore.items.length, (newLength) => {
  if (newLength === 0) {
    ingredientsList.value.forEach(item => item.selectedForShopping = false)
  }
})

const handleGoShopping = () => {
  const selected = ingredientsList.value.filter(i => i.selectedForShopping)
  
  if (selected.length === 0) {
    toast.warn(t('recipe.select_ingredients_first') || 'Vui lòng chọn ít nhất 1 nguyên liệu nhé!')
  } else {
    // SỬA DÒNG NÀY: Chú ý truyền props.post.id (ID là số) CHỨ KHÔNG PHẢI props.post.title (Tên là chữ)
    shoppingStore.addItems(selected, props.post.id) // <--- CHỖ NÀY LÀ id
    
    toast.success(`Đã thêm ${selected.length} món vào giỏ đi chợ trên Header!`)
  }
}

const handleImageError = (e) => {
  e.target.style.display = 'none';
  if(e.target.closest('.gallery-item')) e.target.closest('.gallery-item').style.display = 'none';
}
// Favorite
const isFavorite = ref(false)

const checkFavoriteStatus = async () => {
  if (!authStore.isAuthenticated) return

  try {
    const res = await checkFavorite(
      authStore.currentUser.accountID,
      props.post.id
    )

    isFavorite.value = res   // ✅ đúng

  } catch (e) {
    console.log("Check favorite error", e)
  }
}

onMounted(() => {
  fetchNote()
  checkFavoriteStatus()
})


const toggleFavorite = async () => {

  if (!authStore.isAuthenticated) {
    toast.warn('Đăng nhập để lưu công thức nhé!')
    return
  }

  try {

    if (isFavorite.value) {

      await removeFavorite(
        authStore.currentUser.accountID,
        props.post.id
      )

      isFavorite.value = false
      toast.success('Đã bỏ lưu công thức')

    } else {

      await addFavorite(
        authStore.currentUser.accountID,
        props.post.id
      )

      isFavorite.value = true
      toast.success('Đã lưu công thức')

    }

  } catch (e) {
    toast.error('Lỗi hệ thống')
  }
}
</script>

<style scoped>
.recipe-guide-container { width: 100%; font-family: 'Mulish', sans-serif; color: #1C1917; overflow-x: hidden; }


/* ================= HERO SECTION ================= */
.hero-section-full-bleed { width: calc(100% + 80px); margin-left: -40px; margin-right: -40px; margin-top: -40px; padding: 60px 40px 60px; background-color: #fff; border-bottom: 1px solid #F3F4F6; position: relative; z-index: 1; }
.hero-container-inner { max-width: 1200px; margin: 0 auto; display: grid; grid-template-columns: 1fr 1fr; gap: 60px; align-items: center; }
.top-nav-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.nav-left { display: flex; align-items: center; gap: 15px; }
.btn-back-simple { display: flex; align-items: center; gap: 8px; background: none; border: none; color: #6B7280; font-weight: 700; cursor: pointer; transition: 0.2s; font-size: 0.95rem; }
.btn-back-simple .icon-circle { width: 32px; height: 32px; border-radius: 50%; background: #F3F4F6; display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.btn-back-simple:hover { color: #EA580C; } .btn-back-simple:hover .icon-circle { background: #EA580C; color: white; }
.nav-left .sep { color: #E5E7EB; }
.category-label { font-size: 0.75rem; font-weight: 800; color: #EA580C; text-transform: uppercase; letter-spacing: 1px; }
.btn-report-minimal { width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; border: 1px solid #E5E7EB; border-radius: 50%; background: white; color: #9CA3AF; cursor: pointer; transition: 0.3s; }
.btn-report-minimal:hover { border-color: #EF4444; color: #EF4444; background: #FEF2F2; }
.recipe-title-main { font-family: 'Playfair Display', serif; font-size: 3.5rem; line-height: 1.1; color: #111827; margin-bottom: 16px; }
.recipe-desc-main { font-size: 1.1rem; line-height: 1.6; color: #4B5563; margin-bottom: 32px; }
.recipe-meta-row { display: flex; align-items: center; gap: 24px; margin-bottom: 32px; }
.meta-box { display: flex; align-items: center; gap: 10px; } .meta-box .icon { font-size: 1.4rem; }
.meta-detail { display: flex; flex-direction: column; } .meta-detail .label { font-size: 0.65rem; font-weight: 700; color: #9CA3AF; letter-spacing: 0.5px; } .meta-detail .val { font-size: 1rem; font-weight: 700; color: #1F2937; }
.meta-divider { width: 1px; height: 32px; background: #E5E7EB; }
.author-action-row { display: flex; justify-content: space-between; align-items: center; }
.author-block { display: flex; align-items: center; gap: 12px; } .auth-img { width: 44px; height: 44px; border-radius: 50%; object-fit: cover; }
.auth-text { display: flex; flex-direction: column; } .auth-text .label { font-size: 0.7rem; color: #6B7280; } .auth-text .name { font-weight: 700; color: #111827; }
.btn-follow-sm { background: #1F2937; color: white; border: none; padding: 6px 16px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; margin-left: 12px; cursor: pointer; transition: 0.2s; } .btn-follow-sm:hover { background: #EA580C; }
.action-group { display: flex; gap: 10px; }
.btn-save-primary { background: linear-gradient(135deg, #EA580C, #C2410C); color: white; border: none; padding: 12px 28px; border-radius: 12px; font-weight: 700; display: flex; align-items: center; gap: 8px; cursor: pointer; transition: 0.3s; box-shadow: 0 4px 15px rgba(234, 88, 12, 0.3); } .btn-save-primary:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(234, 88, 12, 0.4); }
.btn-share-circle { width: 44px; height: 44px; border: 1px solid #E5E7EB; border-radius: 50%; background: white; color: #6B7280; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.2s; } .btn-share-circle:hover { border-color: #1F2937; color: #1F2937; }
.image-frame-hero { position: relative; height: 520px; border-radius: 32px; overflow: hidden; box-shadow: 20px 30px 60px -10px rgba(0,0,0,0.15); transform: perspective(1000px) rotateY(-5deg); transition: 0.5s; }
.image-frame-hero:hover { transform: perspective(1000px) rotateY(0deg); }
.img-hero-cover { width: 100%; height: 100%; object-fit: cover; }
.floating-stats { position: absolute; bottom: 20px; left: 50%; transform: translateX(-50%); background: rgba(255,255,255,0.9); backdrop-filter: blur(10px); padding: 8px 24px; border-radius: 30px; display: flex; align-items: center; gap: 12px; font-size: 0.85rem; font-weight: 700; color: #374151; box-shadow: 0 10px 20px rgba(0,0,0,0.1); white-space: nowrap; } .floating-stats .sep { color: #D1D5DB; }

/* ================= BODY PREMIUM ================= */
.body-section-premium { width: 100%; background-color: #F8FAFC; padding: 60px 0; }
.body-container-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; display: grid; grid-template-columns: 380px 1fr; gap: 60px; }
.sticky-wrapper { position: sticky; top: 90px; display: flex; flex-direction: column; gap: 30px; }

/* SHARED CARD STYLE */
.premium-card { background: white; border-radius: 24px; border: 1px solid #E2E8F0; box-shadow: 0 10px 30px -5px rgba(0,0,0,0.03); overflow: hidden; transition: 0.3s; }
.premium-card:hover { box-shadow: 0 20px 40px -5px rgba(0,0,0,0.06); }

/* --- INGREDIENTS CARD --- */
.ingredients-card .card-header-gradient { background: linear-gradient(135deg, #FFF7ED 0%, #FFFFFF 100%); padding: 25px 30px; border-bottom: 1px solid #FED7AA; display: flex; justify-content: space-between; align-items: center; }
.header-content h3 { font-family: 'Playfair Display', serif; font-size: 1.6rem; margin: 0; color: #111827; }
.header-content .sub-text { font-size: 0.85rem; color: #9A3412; }
.btn-add-mini { width: 32px; height: 32px; border-radius: 8px; background: white; border: 1px solid #FDBA74; color: #EA580C; font-size: 1.2rem; cursor: pointer; transition: 0.2s; display: flex; align-items: center; justify-content: center; }
.btn-add-mini:hover { background: #EA580C; color: white; }
.ingredients-scroll { padding: 10px 0; }
.ingredients-list-premium { list-style: none; padding: 0; margin: 0; }
.premium-checkbox-row { display: flex; align-items: center; gap: 16px; padding: 14px 30px; cursor: pointer; transition: 0.2s; position: relative; }
.premium-checkbox-row:hover { background: #FFFAF0; }
.premium-checkbox-row input { display: none; }
.checkbox-visual { width: 26px; height: 26px; border: 2px solid #CBD5E1; border-radius: 8px; display: flex; align-items: center; justify-content: center; transition: 0.3s cubic-bezier(0.4, 0, 0.2, 1); background: white; flex-shrink: 0; }
.checkbox-visual svg { width: 16px; stroke: white; stroke-width: 3; fill: none; opacity: 0; transform: scale(0.5); transition: 0.3s; }
.premium-checkbox-row input:checked ~ .checkbox-visual { background: #EA580C; border-color: #EA580C; box-shadow: 0 4px 10px rgba(234, 88, 12, 0.3); }
.premium-checkbox-row input:checked ~ .checkbox-visual svg { opacity: 1; transform: scale(1); }
.ing-text { font-size: 1.05rem; color: #334155; font-weight: 600; transition: 0.3s; }
.ing-text.is_checked { color: #94A3B8; }
.line-through-effect { position: absolute; left: 70px; right: 30px; top: 50%; height: 2px; background: #94A3B8; transform: scaleX(0); transform-origin: left; transition: transform 0.3s ease; pointer-events: none; }
.premium-checkbox-row input:checked ~ .line-through-effect { transform: scaleX(1); }
.btn-shopping-cart-lg { width: calc(100% - 60px); margin: 10px 30px 30px; padding: 14px; background: #111827; color: white; border: none; border-radius: 12px; font-weight: 700; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 10px; transition: 0.3s; }
.btn-shopping-cart-lg:hover { background: #EA580C; box-shadow: 0 8px 20px rgba(234, 88, 12, 0.3); }

/* --- NOTE CARD --- */
.note-card { background: #FEFCE8; border-color: #FEF08A; padding: 0; }
.note-decoration { height: 8px; background: repeating-linear-gradient(45deg, #FDE047, #FDE047 10px, #FEF08A 10px, #FEF08A 20px); }
.note-inner { padding: 25px 30px; }
.note-title { font-weight: 800; color: #854D0E; margin-bottom: 15px; font-size: 0.95rem; text-transform: uppercase; letter-spacing: 0.5px; }
.textarea-container { background: rgba(255,255,255,0.6); padding: 15px; border-radius: 16px; border: 1px solid #FEF08A; box-shadow: inset 0 2px 4px rgba(0,0,0,0.02); }
.note-card textarea { width: 100%; height: 100px; background: transparent; border: none; outline: none; resize: none; font-size: 1rem; color: #713F12; font-family: 'Mulish', cursive; line-height: 1.6; }

/* CSS MỚI CHO NÚT LƯU VÀ LOADER */
.btn-save-note { background: #A16207; color: white; border: none; padding: 8px 16px; border-radius: 8px; font-family: 'Mulish', sans-serif; font-weight: 700; display: flex; align-items: center; gap: 8px; cursor: pointer; transition: 0.2s; }
.btn-save-note:hover:not(:disabled) { background: #854D0E; transform: translateY(-2px); }
.btn-save-note:disabled { opacity: 0.6; cursor: not-allowed; }
.loader-small { width: 14px; height: 14px; border: 2px solid #FFF; border-bottom-color: transparent; border-radius: 50%; display: inline-block; animation: rotation 1s linear infinite; }
@keyframes rotation { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* --- STEPS CARD --- */
.steps-card { padding: 40px; }
.steps-header-modern { display: flex; justify-content: space-between; align-items: center; margin-bottom: 50px; border-bottom: 2px solid #F1F5F9; padding-bottom: 20px; }
.steps-header-modern h2 { font-family: 'Playfair Display', serif; font-size: 2.2rem; margin: 0; color: #111827; }
.step-counter-badge { background: #111827; color: white; padding: 6px 16px; border-radius: 20px; font-weight: 700; font-size: 0.9rem; letter-spacing: 0.5px; }
.timeline-editorial { display: flex; flex-direction: column; gap: 60px; }
.timeline-step { display: grid; grid-template-columns: 80px 1fr; gap: 30px; }
.step-marker-col { display: flex; flex-direction: column; align-items: center; }
.step-number-art { width: 60px; height: 60px; display: flex; align-items: center; justify-content: center; font-family: 'Playfair Display', serif; font-size: 2rem; font-weight: 900; color: #EA580C; background: #FFF7ED; border-radius: 20px; border: 2px solid #FFEDD5; box-shadow: 4px 4px 0 #FED7AA; z-index: 2; }
.step-connector { width: 2px; flex: 1; border-left: 2px dashed #E2E8F0; margin-top: 20px; }
.step-heading { font-size: 0.9rem; text-transform: uppercase; color: #94A3B8; font-weight: 800; margin: 0 0 12px 0; letter-spacing: 2px; }
.step-desc-text { font-size: 1.2rem; line-height: 1.8; color: #334155; margin-bottom: 25px; }
.step-gallery-floating { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; }
.gallery-item { position: relative; border-radius: 20px; overflow: hidden; height: 200px; cursor: zoom-in; }
.img-inner { width: 100%; height: 100%; position: relative; transition: 0.4s cubic-bezier(0.34, 1.56, 0.64, 1); }
.gallery-item img { width: 100%; height: 100%; object-fit: cover; }
.gallery-item:hover .img-inner { transform: scale(1.05) rotate(1deg); box-shadow: 0 15px 30px rgba(0,0,0,0.15); }

/* ANIMATION */
.fade-in-up { animation: fadeInUp 0.8s ease-out; }
.slide-in-left { animation: slideInLeft 0.7s ease-out backwards; }
.slide-in-up { animation: slideInUp 0.7s ease-out backwards; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
@keyframes slideInLeft { from { opacity: 0; transform: translateX(-30px); } to { opacity: 1; transform: translateX(0); } }
@keyframes slideInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
</style>
