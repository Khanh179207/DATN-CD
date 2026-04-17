<template>
  <div class="cooking-dashboard-premium fade-in" :class="{ 'theater-mode-on': isTheater }">
    <div class="dashboard-container-inner">
      
      <aside class="dashboard-left-col">
        <div class="sticky-content-wrapper">
          
          <div v-if="parsedVideo" class="video-card-luxury slide-in-left">
            <div class="card-header-dark">
              <div class="h-left">
                <span class="pulse-dot-neon"></span>
                <span class="caps-text">{{ isTheater ? 'CHẾ ĐỘ RẠP PHIM' : 'MASTERCLASS VIDEO' }}</span>
              </div>
              <button class="btn-theater-toggle" @click="isTheater = !isTheater" :title="isTheater ? 'Thu nhỏ' : 'Mở rộng'">
                <svg v-if="!isTheater" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7"/></svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M4 14h6v6M20 10h-6V4M14 10l7-7M10 14l-7 7"/></svg>
              </button>
            </div>
            <div class="video-aspect-frame">
              <iframe v-if="parsedVideo.type === 'youtube'" :src="parsedVideo.url" title="Recipe Video" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
              <video v-else-if="parsedVideo.type === 'html5'" :src="parsedVideo.url" controls playsinline class="html5-player"></video>
            </div>
          </div>

          <div class="widgets-group slide-in-left" style="animation-delay: 0.1s;">
            
            <div class="premium-widget ingredients-widget">
              <div class="widget-header">
                <div class="title-with-icon">
                  <div class="icon-box"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg></div>
                  <h3>Giỏ nguyên liệu</h3>
                </div>
                <button class="text-btn-soft" @click="selectAllIngredients">Chọn tất cả</button>
              </div>

              <div class="ingredients-list-clean">
                <label v-for="(item, index) in ingredientsList" :key="index" class="clean-check-row">
                  <input type="checkbox" v-model="item.selectedForShopping">
                  <div class="checkbox-visual">
                    <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                  </div>
                  <span class="i-name" :class="{ strike: item.selectedForShopping }">{{ item.name }}</span>
                </label>
              </div>

              <button class="btn-gradient-orange w-full" @click="handleGoShopping">
                <span>{{ $t('recipe.add_to_cart') || 'Thêm vào giỏ đi chợ' }}</span>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="9" cy="21" r="1"></circle><circle cx="20" cy="21" r="1"></circle><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path></svg>
              </button>
            </div>

            <div class="widget-divider"></div>

            <div class="premium-widget note-widget">
              <div class="widget-header">
                <div class="title-with-icon">
                  <div class="icon-box note-icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg></div>
                  <h3>Ghi chú cá nhân</h3>
                </div>
              </div>
              <div class="textarea-wrapper">
                <textarea v-model="userNote" placeholder="Ghi chú lại bí quyết gia giảm của riêng bạn..."></textarea>
              </div>
              <div class="note-actions">
                <span class="lock-text"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg> Chỉ mình bạn thấy</span>
                <button class="btn-text-save" @click="handleSaveNote" :disabled="isSavingNote">
                   {{ isSavingNote ? 'Đang lưu...' : 'Lưu lại' }}
                </button>
              </div>
            </div>

          </div>
        </div>
      </aside>

      <main class="dashboard-right-col">
        <div class="process-editorial-section slide-in-up">
          
          <div class="process-header">
            <h2 class="serif-title">Quy trình thực hiện</h2>
            <div class="header-actions">
              <span class="step-count-badge">{{ post.steps?.length || 0 }} BƯỚC</span>
              <button class="btn-gradient-dark" @click="cookingCardRef?.openModal(0)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7"/></svg>
                CHẾ ĐỘ NẤU
              </button>
            </div>
          </div>

          <div class="modern-timeline">
            <div class="timeline-step" v-for="(step, index) in post.steps" :key="index">
              
              <div class="timeline-marker">
                <div class="t-circle">{{ index + 1 }}</div>
                <div v-if="index !== post.steps.length - 1" class="t-line"></div>
              </div>
              
              <div class="timeline-content">
                <h4 class="step-subtitle">BƯỚC {{ index + 1 }}</h4>
                <p class="step-desc">{{ step.desc }}</p>
                
                <div class="step-gallery" v-if="step.images && step.images.length && step.images[0]">
                  <div class="gallery-img-box" v-for="(img, i) in step.images" :key="i" @click="cookingCardRef?.openModal(index)">
                    <img :src="img" loading="lazy" @error="handleImageError">
                    <div class="hover-zoom">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                    </div>
                  </div>
                </div>
              </div>

            </div>
          </div>

        </div>
      </main>

    </div>

    <CookingCard ref="cookingCardRef" :steps="post.steps || []" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import api from '@/services/api'
import { useShoppingStore } from '@/stores/shopping'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import CookingCard from '@/components/common/CookingCard.vue'

const { t } = useI18n()
const props = defineProps({
  post: { type: Object, required: true, default: () => ({ steps: [] }) }
})

const authStore = useAuthStore()
const shoppingStore = useShoppingStore()
const userNote = ref('')
const isSavingNote = ref(false)
const ingredientsList = ref([])
const cookingCardRef = ref(null)
const isTheater = ref(false)

const parsedVideo = computed(() => {
  const url = props.post?.video;
  if (!url) return null;
  if (url.includes('youtube.com') || url.includes('youtu.be')) {
    let videoId = '';
    if (url.includes('v=')) videoId = url.split('v=')[1]?.split('&')[0];
    else if (url.includes('youtu.be/')) videoId = url.split('youtu.be/')[1]?.split('?')[0];
    return { type: 'youtube', url: `https://www.youtube.com/embed/${videoId}` };
  }
  return { type: 'html5', url: url };
})

const fetchNote = async () => {
  if (!authStore.isAuthenticated) return
  try {
    const res = await api.get('/api/notes', { params: { accountId: authStore.user.accountID, postId: props.post.id } })
    if (res.data) userNote.value = res.data
  } catch (e) { /* ignore */ }
}

const handleSaveNote = async () => {
  if (!authStore.isAuthenticated) {
    window.dispatchEvent(new CustomEvent('ui:open-login'))
    return
  }
  isSavingNote.value = true
  try {
    await api.post('/api/notes/save', { 
      accountId: authStore.user.accountID, 
      postId: props.post.id, 
      content: userNote.value 
    })
    toast.success('Bí quyết đã được lưu!');
  } catch (e) { 
    toast.error('Lỗi hệ thống!'); 
  } 
  finally { isSavingNote.value = false }
}
const selectAllIngredients = () => { ingredientsList.value.forEach(item => item.selectedForShopping = true) }

watch(() => props.post.ingredientsRaw, (newVal) => {
  if (!newVal) { ingredientsList.value = []; return; }
  ingredientsList.value = newVal.split(',').map(item => item.trim()).filter(i => i !== '').map(i => ({ name: i, selectedForShopping: shoppingStore.items?.some(si => si.name === i) || false }))
}, { immediate: true })

const handleGoShopping = () => {
  if (!authStore.isAuthenticated) {
    window.dispatchEvent(new CustomEvent('ui:open-login'))
    return
  }

  // 🔥 ĐÃ SỬA: Kiểm tra quyền Premium thông minh hơn
  const user = authStore.user;
  const role = String(user?.role || '').toUpperCase();
  
  // Kiểm tra đa dạng kiểu dữ liệu (boolean, string, number) để không bị sót
  const isPremiumVal = [true, 'true', 1, '1'].includes(user?.isPremium) || [true, 'true', 1, '1'].includes(user?.IsPremium);
  const isAdminVal = [true, 'true', 1, '1'].includes(user?.isAdmin) || role === 'ADMIN';
  
  const hasPremiumAccess = isPremiumVal || role === 'PREMIUM' || isAdminVal;

  if (!hasPremiumAccess) {
    toast.warn('Tính năng Giỏ đi chợ là đặc quyền chỉ dành cho tài khoản Premium.');
    window.dispatchEvent(new CustomEvent('ui:open-premium'));
    return;
  }

  const selected = ingredientsList.value.filter(i => i.selectedForShopping)
  if (!selected.length) return toast.warn('Bạn chưa chọn nguyên liệu nào!');
  shoppingStore.addItems(selected, props.post.id)
  toast.success(`Đã thêm ${selected.length} món vào giỏ!`)
}

const handleImageError = (e) => { e.target.style.display = 'none'; }
onMounted(() => fetchNote())
</script>

<style scoped lang="scss">
/* --- LAYOUT CHÍNH --- */
.cooking-dashboard-premium {
  width: 100%;
  background-color: #fafaf9; 
  padding: 40px 0 100px;
  font-family: var(--font-ui, 'Mulish', sans-serif);
}

.dashboard-container-inner {
  max-width: 1240px;
  margin: 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 420px 1fr; 
  gap: 64px;
  align-items: start;
  transition: all 0.4s ease;
}

.theater-mode-on {
  .dashboard-container-inner { grid-template-columns: 1fr; max-width: 1000px; gap: 40px; }
  .dashboard-left-col { max-width: 1000px; margin: 0 auto; width: 100%; }
}

.dashboard-left-col { position: sticky; top: 100px; }

.video-card-luxury {
  background: #0f172a; 
  border-radius: 20px;
  overflow: hidden; 
  box-shadow: 0 20px 40px -15px rgba(0,0,0,0.15); 
  margin-bottom: 24px;
  
  .card-header-dark {
    padding: 14px 20px; display: flex; justify-content: space-between; align-items: center;
    background: #0f172a;
    .h-left { display: flex; align-items: center; gap: 10px; color: #fff; font-weight: 700; font-size: 0.75rem; letter-spacing: 1.5px; }
    .pulse-dot-neon { width: 8px; height: 8px; background: #ea580c; border-radius: 50%; box-shadow: 0 0 10px #ea580c; animation: pulse 2s infinite; }
  }
  .btn-theater-toggle { background: transparent; border: none; color: #94a3b8; padding: 4px; cursor: pointer; transition: 0.2s; &:hover { color: #fff; } }
  .video-aspect-frame { position: relative; padding-bottom: 56.25%; height: 0; background: #000; iframe, .html5-player { position: absolute; inset: 0; width: 100%; height: 100%; border: none; } }
}

.widgets-group {
  background: #ffffff;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
  overflow: hidden; 
}

.premium-widget { padding: 28px; }
.widget-divider { height: 1px; background: #f1f5f9; margin: 0 28px; }

.widget-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;
  .title-with-icon { 
    display: flex; align-items: center; gap: 12px; 
    .icon-box { width: 32px; height: 32px; background: #fff4ed; color: #ea580c; border-radius: 10px; display: flex; align-items: center; justify-content: center; }
    .note-icon { background: #f8fafc; color: #3b82f6; }
    h3 { font-size: 1.05rem; font-weight: 800; color: #0f172a; margin: 0; } 
  }
}

.text-btn-soft { background: transparent; border: none; color: #64748b; font-weight: 700; font-size: 0.85rem; cursor: pointer; padding: 4px 8px; transition: 0.2s; &:hover { color: #ea580c; } }
.btn-gradient-orange {
  padding: 14px 24px; border-radius: 16px; border: none;
  background: linear-gradient(135deg, #ea580c, #f59e0b); color: #fff; font-weight: 800; font-size: 0.95rem;
  display: flex; align-items: center; justify-content: space-between;
  cursor: pointer; transition: all 0.3s ease;
  box-shadow: 0 8px 20px -6px rgba(234, 88, 12, 0.4);
  &:hover { transform: translateY(-2px); box-shadow: 0 12px 24px -6px rgba(234, 88, 12, 0.5); }
  &.w-full { width: 100%; }
}

.ingredients-list-clean {
  display: flex; flex-direction: column; gap: 4px; margin-bottom: 24px;
  .clean-check-row {
    display: flex; align-items: center; gap: 14px; cursor: pointer; padding: 8px 12px; border-radius: 12px; transition: 0.2s;
    &:hover { background: #f8fafc; }
    input { display: none; }
    .checkbox-visual {
      width: 22px; height: 22px; border: 2px solid #cbd5e1; border-radius: 50%; 
      display: flex; align-items: center; justify-content: center; transition: 0.2s;
      svg { width: 12px; opacity: 0; transform: scale(0.5); transition: 0.2s; }
    }
    input:checked + .checkbox-visual { background: #ea580c; border-color: #ea580c; svg { opacity: 1; transform: scale(1); } }
    .i-name { font-weight: 600; color: #334155; font-size: 0.95rem; transition: 0.2s; &.strike { color: #94a3b8; text-decoration: line-through; } }
  }
}

.note-widget {
  .textarea-wrapper {
    background: #f8fafc; border: 1px solid transparent; border-radius: 16px; padding: 16px; transition: 0.3s;
    &:focus-within { border-color: #ea580c; background: #fff; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.05); }
    textarea { width: 100%; height: 70px; background: transparent; border: none; outline: none; font-size: 0.95rem; color: #334155; resize: none; font-family: inherit; line-height: 1.5; }
  }
  .note-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 16px; }
  .lock-text { font-size: 0.8rem; font-weight: 600; color: #94a3b8; display: flex; align-items: center; gap: 6px; }
  .btn-text-save { background: transparent; border: none; color: #3b82f6; font-weight: 800; font-size: 0.9rem; cursor: pointer; transition: 0.2s; &:hover { color: #2563eb; transform: translateY(-1px); } }
}

.dashboard-right-col { min-width: 0; padding-top: 10px; }

.process-header {
  display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 20px; margin-bottom: 40px;
  .serif-title { font-family: var(--font-serif, 'Playfair Display', serif); font-size: 2.5rem; font-weight: 900; color: #0f172a; margin: 0; line-height: 1.2; letter-spacing: -0.5px; }
  .header-actions { 
    display: flex; align-items: center; gap: 16px; 
    .step-count-badge { color: #64748b; font-weight: 800; font-size: 0.85rem; letter-spacing: 1px; }
  }
}

.btn-gradient-dark {
  padding: 10px 20px; border-radius: 100px; border: none;
  background: #0f172a; color: #fff; font-weight: 700; font-size: 0.85rem;
  display: flex; align-items: center; gap: 8px; cursor: pointer; transition: 0.3s;
  &:hover { background: #ea580c; transform: translateY(-2px); box-shadow: 0 10px 20px -5px rgba(234, 88, 12, 0.3); }
}

.modern-timeline { display: flex; flex-direction: column; }

.timeline-step {
  display: flex; gap: 28px;
  
  .timeline-marker {
    display: flex; flex-direction: column; align-items: center; width: 32px; flex-shrink: 0;
    .t-circle { width: 32px; height: 32px; border-radius: 50%; background: #fff4ed; color: #ea580c; display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 1rem; z-index: 2; border: 2px solid #fff; box-shadow: 0 0 0 1px #ffedd5; }
    .t-line { width: 2px; flex: 1; background: repeating-linear-gradient(to bottom, #e2e8f0 0, #e2e8f0 4px, transparent 4px, transparent 8px); margin: 8px 0; }
  }

  .timeline-content {
    flex: 1; padding-bottom: 56px;
    
    .step-subtitle { font-size: 0.85rem; font-weight: 800; color: #ea580c; margin: 6px 0 12px; letter-spacing: 0.5px; }
    .step-desc { font-size: 1.1rem; line-height: 1.8; color: #334155; font-weight: 500; margin-bottom: 24px; white-space: pre-wrap; }
    
    .step-gallery {
      display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px;
      .gallery-img-box {
        position: relative; border-radius: 16px; overflow: hidden; aspect-ratio: 16/9; cursor: zoom-in; box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        img { width: 100%; height: 100%; object-fit: cover; transition: 0.5s ease; }
        .hover-zoom { position: absolute; inset: 0; background: rgba(15, 23, 42, 0.4); display: flex; align-items: center; justify-content: center; opacity: 0; transition: 0.3s; }
        &:hover { img { transform: scale(1.05); } .hover-zoom { opacity: 1; } }
      }
    }
  }
}

@keyframes pulse { 0% { opacity: 0.7; } 50% { opacity: 1; box-shadow: 0 0 15px #ea580c; } 100% { opacity: 0.7; } }
.slide-in-left { animation: slideInLeft 0.6s cubic-bezier(0.16, 1, 0.3, 1) backwards; }
.slide-in-up { animation: slideInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) backwards; }
@keyframes slideInLeft { from { opacity: 0; transform: translateX(-20px); } to { opacity: 1; transform: translateX(0); } }
@keyframes slideInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 1200px) {
  .dashboard-container-inner {
    grid-template-columns: 350px 1fr; 
    gap: 40px;
  }
}

@media (max-width: 1024px) {
  .dashboard-container-inner { 
    grid-template-columns: 1fr; 
    gap: 40px; 
    padding: 0 20px; 
  }
  
  .dashboard-left-col { 
    position: static; 
    max-width: 100%; 
  }
  
  .process-header { 
    flex-direction: column; 
    align-items: flex-start; 
    gap: 16px; 
    
    .serif-title { font-size: 2.2rem; } 
  }
  
  .premium-widget { padding: 24px; }
  .widget-divider { margin: 0 24px; }
}

@media (max-width: 768px) {
  .cooking-dashboard-premium { padding: 30px 0 80px; }
  .dashboard-container-inner { gap: 30px; padding: 0 16px; }

  .video-card-luxury { border-radius: 16px; margin-bottom: 20px; }
  .card-header-dark { padding: 12px 16px; }
  
  .widgets-group { border-radius: 16px; }
  .premium-widget { padding: 20px; }
  .widget-divider { margin: 0 20px; }
  
  .widget-header .title-with-icon h3 { font-size: 0.95rem; }
  .btn-gradient-orange { padding: 12px 20px; font-size: 0.9rem; }
  .clean-check-row { padding: 6px 8px; }
  
  .process-header .serif-title { font-size: 1.8rem; }
  .header-actions { 
    width: 100%; 
    justify-content: space-between; 
  }

  .timeline-step { gap: 16px; } 
  
  .timeline-marker { width: 28px; }
  .timeline-marker .t-circle { width: 28px; height: 28px; font-size: 0.9rem; }
  
  .timeline-content { padding-bottom: 32px; }
  .timeline-content .step-subtitle { margin: 4px 0 8px; }
  .timeline-content .step-desc { font-size: 1rem; line-height: 1.6; margin-bottom: 16px; }
  
  .timeline-content .step-gallery { 
    grid-template-columns: 1fr; 
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .process-header .serif-title { font-size: 1.6rem; }
  
  .header-actions { 
    flex-direction: column; 
    align-items: flex-start; 
    gap: 12px; 
  }
  .btn-gradient-dark { 
    width: 100%; 
    justify-content: center; 
    padding: 12px; 
  }

  .clean-check-row .i-name { font-size: 0.9rem; }
  .timeline-content .step-desc { font-size: 0.95rem; }
}
</style>