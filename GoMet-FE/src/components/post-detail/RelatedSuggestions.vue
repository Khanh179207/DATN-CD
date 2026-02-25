<template>
  <section class="related-wrapper">
    <div class="bg-glow"></div>

    <div class="related-container">
      <div class="section-header fade-in">
        <div class="header-left">
          <span class="sub-title">Khám phá thêm</span>
          <h3 class="main-title">Có thể bạn sẽ thích</h3>
        </div>
        <button class="btn-see-all" @click="$emit('see-all')">
          Xem tất cả
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"></path><path d="M12 5l7 7-7 7"></path></svg>
        </button>
      </div>
      
      <div class="grid-content-wrapper">
        
        <div v-if="loading" class="grid-layout skeleton-grid">
          <div v-for="n in 4" :key="n" class="skeleton-card">
            <div class="sk-img"></div>
            <div class="sk-content">
              <div class="sk-line w-80"></div>
              <div class="sk-line w-60"></div>
            </div>
          </div>
        </div>

        <template v-else>
          <TransitionGroup 
            name="stagger-list" 
            tag="div" 
            class="grid-layout real-grid"
            v-if="items.length > 0"
          >
            <RecipeCard 
              v-for="(item, index) in items" 
              :key="item.id" 
              :post="item"
              class="grid-item"
              :style="{ '--delay': index * 0.1 + 's' }" 
              @click="$emit('click-post', item.id)"
            />
          </TransitionGroup>

          <div v-else class="empty-state">
            <span class="empty-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1"><path d="M3 11l19-9-9 19-2-8-8-2z"/></svg>
            </span>
            <p>Chưa có bài viết liên quan nào.</p>
          </div>
        </template>
      </div>
    </div>
  </section>
</template>

<script setup>
import RecipeCard from '@/components/common/RecipeCard.vue'

defineProps({ 
  items: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false }
})

defineEmits(['click-post', 'see-all'])
</script>

<style scoped>
/* --- LAYOUT CHÍNH --- */
.related-wrapper { 
  position: relative;
  background: #FAFAF9; 
  padding: 80px 20px 100px;
  border-top: 1px solid rgba(0,0,0,0.05);
  font-family: 'Mulish', sans-serif;
  overflow: hidden;
  width: 100%; /* Full width */
}

.related-container { 
  max-width: 1200px; margin: 0 auto; position: relative; z-index: 2; width: 100%;
}

/* Background Glow */
.bg-glow {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 1;
  background: radial-gradient(circle at 10% 10%, rgba(234, 88, 12, 0.03) 0%, transparent 40%),
              radial-gradient(circle at 90% 90%, rgba(251, 191, 36, 0.05) 0%, transparent 40%);
  pointer-events: none;
}

/* --- HEADER --- */
.section-header { 
  display: flex; justify-content: space-between; align-items: flex-end; 
  margin-bottom: 50px; 
}
.sub-title { 
  display: block; font-size: 0.85rem; font-weight: 800; color: #EA580C; 
  text-transform: uppercase; letter-spacing: 2px; margin-bottom: 8px;
}
.main-title { 
  font-family: 'Playfair Display', serif; font-size: 2.5rem; 
  color: #1C1917; margin: 0; line-height: 1.1;
}
.btn-see-all {
  display: flex; align-items: center; gap: 8px;
  background: transparent; border: 1px solid #E5E7EB;
  padding: 10px 20px; border-radius: 30px;
  font-weight: 700; color: #57534E; cursor: pointer;
  transition: all 0.3s ease; white-space: nowrap;
}
.btn-see-all:hover {
  border-color: #EA580C; color: #EA580C; background: white;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.1);
}

/* --- GRID LAYOUT (FIXED) --- */
.grid-content-wrapper {
  width: 100%;
  display: block;
}

/* Class chung cho Grid */
.grid-layout {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* Ép cứng 4 cột */
  gap: 30px;
  width: 100%;
}

.grid-item {
  width: 100%; /* Card chiếm full chiều rộng ô grid */
}

/* --- EMPTY STATE --- */
.empty-state {
  text-align: center; padding: 60px 0; color: #A8A29E;
}
.empty-icon { font-size: 3rem; display: block; margin-bottom: 10px; opacity: 0.5; }

/* --- SKELETON LOADING --- */
.skeleton-card {
  background: white; border-radius: 20px; overflow: hidden;
  box-shadow: 0 4px 10px rgba(0,0,0,0.03);
  height: 320px; display: flex; flex-direction: column;
  width: 100%;
}
.sk-img { width: 100%; height: 200px; background: #E5E7EB; animation: pulse 1.5s infinite; }
.sk-content { padding: 15px; flex: 1; display: flex; flex-direction: column; gap: 10px; }
.sk-line { height: 12px; background: #E5E7EB; border-radius: 6px; animation: pulse 1.5s infinite; }
.w-80 { width: 80%; } .w-60 { width: 60%; }

@keyframes pulse { 0% { opacity: 0.6; } 50% { opacity: 1; } 100% { opacity: 0.6; } }

/* --- ANIMATION --- */
.stagger-list-enter-active {
  transition: all 0.6s cubic-bezier(0.2, 0.8, 0.2, 1);
  transition-delay: var(--delay);
}
.stagger-list-enter-from { opacity: 0; transform: translateY(30px); }
.stagger-list-enter-to { opacity: 1; transform: translateY(0); }

/* --- RESPONSIVE --- */
@media (max-width: 1024px) {
  .grid-layout { grid-template-columns: repeat(2, 1fr); gap: 20px; } /* Tablet 2 cột */
  .section-header { flex-direction: column; align-items: flex-start; gap: 15px; }
  .btn-see-all { width: 100%; justify-content: center; }
}

@media (max-width: 640px) {
  .grid-layout { grid-template-columns: 1fr; } /* Mobile 1 cột */
  .main-title { font-size: 2rem; }
  .related-wrapper { padding: 50px 20px; }
}
</style>