<template>
  <div class="recipe-guide-container">
    
    <div v-if="parsedVideo" class="top-video-container" style="margin-top: 20px;">
      <div class="premium-card video-card slide-in-up">
        <div class="video-header">
          <h2>🎬 {{ $t('Video hướng dẫn') || 'Video Hướng Dẫn' }}</h2>
        </div>
        <div class="video-wrapper">
          <iframe v-if="parsedVideo.type === 'youtube'" :src="parsedVideo.url" title="Recipe Video" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
          <video v-else-if="parsedVideo.type === 'html5'" :src="parsedVideo.url" controls playsinline preload="metadata" class="html5-video-player"></video>
        </div>
      </div>
    </div>

    <section class="body-section-premium">
      <div class="body-container-inner">
        
        <aside class="sidebar-left-sticky">
          <div class="sticky-wrapper">
            <div class="premium-card ingredients-card slide-in-left">
              <div class="card-header-gradient">
                <div class="header-content">
                  <h3>🛒 {{ $t('post.ingredients') || 'Nguyên liệu' }}</h3>
                  <span class="sub-text">{{ $t('recipe.by') || 'Khẩu phần:' }} <b>{{ post.servings }}</b></span>
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
                <span class="icon">🛍️</span> {{ $t('recipe.add_to_cart') || 'Thêm vào giỏ đi chợ' }}
              </button>
            </div>

            <div class="premium-card note-card slide-in-left" style="animation-delay: 0.1s;">
              <div class="note-decoration"></div>
              <div class="note-inner">
                <div class="note-title">
                  <span class="icon">✏️</span> {{ $t('recipe.personal_note') || 'GHI CHÚ CÁ NHÂN' }}
                </div>
                <div class="textarea-container">
                  <textarea v-model="userNote" :placeholder="$t('recipe.note_placeholder') || 'Viết ghi chú của bạn tại đây...'"></textarea>
                </div>
                <div class="note-status" style="display: flex; justify-content: space-between; align-items: center; margin-top: 15px;">
                  <span class="status-badge"><span class="dot"></span> {{ $t('recipe.private') || 'Riêng tư' }}</span>
                  <button class="btn-save-note" @click="handleSaveNote" :disabled="isSavingNote">
                    <span v-if="isSavingNote" class="loader-small"></span>
                    <svg v-else width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5z"></path><polyline points="17 21 17 13 7 13 7 21"></polyline></svg>
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
              <div class="header-left-group">
                <h2>{{ $t('recipe.process') || 'Quy trình thực hiện' }}</h2>
              </div>
              
              <button class="btn-focus-mode" @click="cookingCardRef?.openModal(0)">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7"/></svg>
                {{ $t('Xem chi tiết') || 'Chế độ xem chi tiết' }}
              </button>
            </div>

            <div class="timeline-editorial">
              <div class="timeline-step" v-for="(step, index) in post.steps" :key="index">
                <div class="step-marker-col">
                  <div class="step-number-art">{{ index + 1 }}</div>
                  <div class="step-connector" v-if="index !== post.steps.length - 1"></div>
                </div>
                <div class="step-content-col">
                  <h4 class="step-heading">{{ $t('recipe.step_prefix') || 'Bước' }} {{ index + 1 }}</h4>
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

    <CookingCard ref="cookingCardRef" :steps="post.steps || []" />
    
  </div>
</template>

<script setup>  
import { ref, watch, onMounted, computed } from 'vue' 
import { useI18n } from 'vue-i18n'
import axios from 'axios'
import { useShoppingStore } from '@/stores/shopping' 
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'

// CHÚ Ý IMPORT COMPONENT MỚI Ở ĐÂY (Sửa lại đường dẫn nếu cần)
import CookingCard from '@/components/common/CookingCard.vue'

const { t } = useI18n()
const props = defineProps({
  post: { 
    type: Object, 
    required: true,
    default: () => ({ steps: [] })
  }
})

const authStore = useAuthStore()  
const shoppingStore = useShoppingStore()
const userNote = ref('')
const isSavingNote = ref(false)
const ingredientsList = ref([])

// Khai báo ref để điều khiển component CookingCard
const cookingCardRef = ref(null)

// --- LOGIC VIDEO ---
const parsedVideo = computed(() => {
  const url = props.post?.video;
  if (!url) return null;
  if (url.includes('youtube.com') || url.includes('youtu.be')) {
    let embedUrl = url;
    if (url.includes('youtube.com/watch?v=')) {
      const videoId = url.split('v=')[1]?.split('&')[0];
      if (videoId) embedUrl = `https://www.youtube.com/embed/${videoId}`;
    } else if (url.includes('youtu.be/')) {
      const videoId = url.split('youtu.be/')[1]?.split('?')[0];
      if (videoId) embedUrl = `https://www.youtube.com/embed/${videoId}`;
    }
    return { type: 'youtube', url: embedUrl };
  }
  return { type: 'html5', url: url };
})

// --- LOGIC NOTE ---
const fetchNote = async () => {
  if (!authStore.isAuthenticated) return
  try {
    const res = await axios.get('http://localhost:8080/api/notes', {
      params: { accountId: authStore.currentUser.accountID, postId: props.post.id }
    })
    if (res.data) userNote.value = res.data
  } catch (e) {
    console.log("Chưa có ghi chú cho bài viết này.")
  }
}

const handleSaveNote = async () => {
  if (!authStore.isAuthenticated) {
    toast.warn('Đăng nhập để lưu ghi chú nhé!')
    return
  }
  isSavingNote.value = true
  try {
    await axios.post('http://localhost:8080/api/notes/save', {
      accountId: authStore.currentUser.accountID,
      postId: props.post.id,
      content: userNote.value
    })
    toast.success('Bí quyết của bạn đã được lưu an toàn!')
  } catch (e) {
    toast.error('Lỗi hệ thống, vui lòng thử lại sau!')
  } finally {
    isSavingNote.value = false
  }
}

// --- LOGIC INGREDIENTS ---
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
      selectedForShopping: shoppingStore.items?.some(i => i.name === item) || false
    }))
}, { immediate: true })

watch(() => shoppingStore.items?.length, (newLength) => {
  if (newLength === 0) {
    ingredientsList.value.forEach(item => item.selectedForShopping = false)
  }
})

const handleGoShopping = () => {
  const selected = ingredientsList.value.filter(i => i.selectedForShopping)
  if (selected.length === 0) {
    toast.warn(t('recipe.select_ingredients_first') || 'Vui lòng chọn ít nhất 1 nguyên liệu nhé!')
  } else {
    shoppingStore.addItems(selected, props.post.id) 
    toast.success(`Đã thêm ${selected.length} món vào giỏ đi chợ trên Header!`)
  }
}

const handleImageError = (e) => {
  e.target.style.display = 'none';
  if(e.target.closest('.gallery-item')) e.target.closest('.gallery-item').style.display = 'none';
}

onMounted(() => {
  fetchNote()
})
</script>

<style scoped>
/* Reset */
.recipe-guide-container *, .recipe-guide-container *::before, .recipe-guide-container *::after { box-sizing: border-box; }
.recipe-guide-container { width: 100%; max-width: 100vw; font-family: 'Mulish', sans-serif; color: #1C1917; overflow-x: hidden; }

/* Video */
.top-video-container { max-width: 1200px; margin: 0 auto 50px auto; padding: 0 24px; }
.video-card { padding: 30px; }
.video-header h2 { font-family: 'Playfair Display', serif; font-size: 1.8rem; margin: 0 0 20px 0; color: #111827; }
.video-wrapper { position: relative; padding-bottom: 56.25%; height: 0; overflow: hidden; border-radius: 16px; background: #000; box-shadow: inset 0 0 20px rgba(0,0,0,0.5); }
.video-wrapper iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none; }
.html5-video-player { position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: contain; background-color: #000; border-radius: 16px; }

/* Body Layout */
.body-section-premium { width: 100%; background-color: #F8FAFC; padding: 60px 0; }
.body-container-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; display: grid; grid-template-columns: 380px minmax(0, 1fr); gap: 60px; }
.sticky-wrapper { position: sticky; top: 90px; display: flex; flex-direction: column; gap: 30px; }

/* Card Style */
.premium-card { background: white; border-radius: 24px; border: 1px solid #E2E8F0; box-shadow: 0 10px 30px -5px rgba(0,0,0,0.03); overflow: hidden; transition: 0.3s; }
.premium-card:hover { box-shadow: 0 20px 40px -5px rgba(0,0,0,0.06); }

/* Ingredients */
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

/* Note */
.note-card { background: #FEFCE8; border-color: #FEF08A; padding: 0; }
.note-decoration { height: 8px; background: repeating-linear-gradient(45deg, #FDE047, #FDE047 10px, #FEF08A 10px, #FEF08A 20px); }
.note-inner { padding: 25px 30px; }
.note-title { font-weight: 800; color: #854D0E; margin-bottom: 15px; font-size: 0.95rem; text-transform: uppercase; letter-spacing: 0.5px; }
.textarea-container { background: rgba(255,255,255,0.6); padding: 15px; border-radius: 16px; border: 1px solid #FEF08A; box-shadow: inset 0 2px 4px rgba(0,0,0,0.02); }
.note-card textarea { width: 100%; height: 100px; background: transparent; border: none; outline: none; resize: none; font-size: 1rem; color: #713F12; font-family: 'Mulish', cursive; line-height: 1.6; }
.btn-save-note { background: #A16207; color: white; border: none; padding: 8px 16px; border-radius: 8px; font-family: 'Mulish', sans-serif; font-weight: 700; display: flex; align-items: center; gap: 8px; cursor: pointer; transition: 0.2s; }
.btn-save-note:hover:not(:disabled) { background: #854D0E; transform: translateY(-2px); }
.btn-save-note:disabled { opacity: 0.6; cursor: not-allowed; }
.loader-small { width: 14px; height: 14px; border: 2px solid #FFF; border-bottom-color: transparent; border-radius: 50%; display: inline-block; animation: rotation 1s linear infinite; }
@keyframes rotation { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* Steps */
.main-right-content { min-width: 0; }
.steps-card { padding: 40px; }

/* Header của Bước nấu ăn */
.steps-header-modern { display: flex; justify-content: space-between; align-items: center; margin-bottom: 40px; border-bottom: 1px solid #E2E8F0; padding-bottom: 20px; flex-wrap: wrap; gap: 15px; }
.header-left-group { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.steps-header-modern h2 { font-family: 'Playfair Display', serif; font-size: 2rem; margin: 0; color: #111827; line-height: 1; }
.step-counter-badge { background: #111827; color: white; padding: 4px 12px; border-radius: 20px; font-weight: 700; font-size: 0.85rem; letter-spacing: 0.5px; display: flex; align-items: center; justify-content: center; height: 28px; transform: translateY(-1px); }

/* Nút Xem chi tiết */
.btn-focus-mode { display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 10px 18px; background: linear-gradient(135deg, #F97316 0%, #EA580C 100%); color: white; border: none; border-radius: 10px; font-weight: 700; font-family: 'Mulish', sans-serif; font-size: 0.95rem; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 4px 12px rgba(234, 88, 12, 0.25); margin: 0; white-space: nowrap; }
.btn-focus-mode svg { stroke: white; } 
.btn-focus-mode:hover { transform: translateY(-2px); box-shadow: 0 6px 16px rgba(234, 88, 12, 0.4); }

/* Timeline */
.timeline-editorial { display: flex; flex-direction: column; gap: 40px; }
.timeline-step { display: grid; grid-template-columns: 60px minmax(0, 1fr); gap: 24px; }
.step-marker-col { display: flex; flex-direction: column; align-items: center; }
.step-number-art { width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; font-family: 'Playfair Display', serif; font-size: 1.5rem; font-weight: 900; color: #EA580C; background: #FFF7ED; border-radius: 16px; border: 2px solid #FFEDD5; box-shadow: 3px 3px 0 #FED7AA; z-index: 2; }
.step-connector { width: 2px; flex: 1; border-left: 2px dashed #E2E8F0; margin-top: 10px; }
.step-heading { font-size: 0.85rem; text-transform: uppercase; color: #94A3B8; font-weight: 800; margin: 0 0 8px 0; letter-spacing: 1.5px; }
.step-desc-text { font-size: 1.1rem; line-height: 1.7; color: #334155; margin-bottom: 20px; word-wrap: break-word; overflow-wrap: break-word; }

/* Image Gallery */
.step-gallery-floating { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; }
.gallery-item { position: relative; border-radius: 20px; overflow: hidden; height: 200px; cursor: zoom-in; }
.img-inner { width: 100%; height: 100%; position: relative; transition: 0.4s cubic-bezier(0.34, 1.56, 0.64, 1); }
.gallery-item img { width: 100%; height: 100%; object-fit: cover; }
.gallery-item:hover .img-inner { transform: scale(1.05) rotate(1deg); box-shadow: 0 15px 30px rgba(0,0,0,0.15); }

/* Animation */
.slide-in-left { animation: slideInLeft 0.7s ease-out backwards; }
.slide-in-up { animation: slideInUp 0.7s ease-out backwards; }
@keyframes slideInLeft { from { opacity: 0; transform: translateX(-30px); } to { opacity: 1; transform: translateX(0); } }
@keyframes slideInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
</style>