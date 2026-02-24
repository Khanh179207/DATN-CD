<template>
  <section v-if="topDish" class="hero-sanctum-supreme" :style="parallaxVars">
    
    <div class="fx-layer">
      <div class="spotlight spotlight-main"></div>
      <div class="spotlight spotlight-accent"></div>
      <div class="vignette"></div>
      <div class="noise-texture"></div>
      <div class="floating-embers">
        <div class="ember" v-for="n in 18" :key="n" :style="emberStyle(n)"></div>
      </div>
      <div class="slash" aria-hidden="true"></div>
    </div>

    <div class="corner-tag anim-fade" style="--d: 0.2s">
      <svg viewBox="0 0 24 24" fill="currentColor"><path d="M5 16L3 5l5.5 5L12 4l3.5 6L21 5l-2 11H5zm14 2c0 .55-.45 1-1 1H6c-.55 0-1-.45-1-1s.45-1 1-1h12c.55 0 1 .45 1 1z"/></svg>
      <span>TOP 1 · PREMIUM</span>
    </div>

    <div class="corner-tabs anim-fade" style="--d: 0.5s">
      <button 
        v-for="cat in categories" 
        :key="cat.id" 
        class="corner-tab" 
        :class="{ active: modelValue === cat.id }"
        @click="$emit('update:modelValue', cat.id)"
      >
        {{ cat.name }}
      </button>
    </div>

    <div class="stage-wrap">
      
      <div class="bg-number" aria-hidden="true">01</div>

      <aside class="side-panel side-left anim-fade" style="--d: 0.3s">
        <template v-if="isTopUser">
          <div class="side-card">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
            <span class="side-val">{{ topDish.postCount ?? 0 }}</span>
            <span class="side-lbl">Bài đăng</span>
          </div>
          <div class="side-card">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>
            <span class="side-val">{{ topDish.joinedAt ?? '—' }}</span>
            <span class="side-lbl">Gia nhập</span>
          </div>
        </template>
        <template v-else>
          <div class="side-card">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 23c-1.1 0-2-.9-2-2h4c0 1.1-.9 2-2 2zm5-5v-6c0-3.07-1.64-5.64-4.5-6.32V2c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v3.68C7.63 6.36 6 8.93 6 12v6H4v2h16v-2h-3z"/></svg>
            <span class="side-val">{{ topDish.views }}</span>
            <span class="side-lbl">Lượt xem</span>
          </div>
          <div class="side-card">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/></svg>
            <span class="side-val">{{ topDish.time }}</span>
            <span class="side-lbl">Thời gian</span>
          </div>
        </template>
      </aside>

      <div class="stage">
        
        <div class="stage-frame anim-scale" style="--d: 0.4s">
          <div class="frame-glow"></div>
          <div class="frame-inner">
            <img :src="topDish.image" :alt="topDish.name" class="stage-img" />
            <div class="frame-overlay"></div>
            <div class="frame-shine"></div>
            <div class="frame-edge frame-edge-tl"></div>
            <div class="frame-edge frame-edge-br"></div>
          </div>
          <div class="stage-badge">
            <span class="stage-badge-num">01</span>
            <span class="stage-badge-txt">{{ isTopUser ? 'TOP USER' : 'TUYỆT TÁC' }}</span>
          </div>
        </div>

        <div class="stage-copy anim-up" style="--d: 0.7s">
          <h1 class="stage-title">{{ topDish.name }}</h1>
          <p class="stage-desc">"{{ topDish.description }}"</p>
        </div>

        <div class="stage-action anim-up" style="--d: 0.8s">
          <button class="btn-hero" @click="goToDetail">
            <span>{{ isTopUser ? 'Xem Hồ Sơ Cá Nhân' : 'Xem Chi Tiết Công Thức' }}</span>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
          </button>
        </div>

        <div class="stage-meta anim-up" style="--d: 0.9s">
          <div class="meta-chef" v-if="!isTopUser && topDish.chefName">
            <img :src="topDish.chefAvatar" :alt="topDish.chefName" class="meta-avt" />
            <div class="meta-chef-txt">
              <span class="meta-chef-name">{{ topDish.chefName }}</span>
              <span class="meta-chef-role">Bậc thầy Elite</span>
            </div>
          </div>
          
          <div class="meta-divider" v-if="!isTopUser && topDish.chefName"></div>

          <div class="meta-item meta-difficulty" v-if="!isTopUser && topDish.difficulty">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"/></svg>
            <span class="difficulty-label" :class="topDish.difficultyClass || 'medium'">{{ topDish.difficulty }}</span>
          </div>

          <div class="meta-item" v-if="isTopUser">
             <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg>
             <span>{{ topDish.followers ?? '0' }} Fans</span>
          </div>
        </div>

        <div class="stage-ingredients anim-up" style="--d: 1s" v-if="!isTopUser && ingredientsList.length">
          <span class="ingredients-label">TINH HOA NGUYÊN LIỆU</span>
          <div class="ingredients-pills">
            <span v-for="(item, i) in ingredientsList" :key="i" class="ingredient-pill">{{ item }}</span>
          </div>
        </div>

      </div>

      <aside class="side-panel side-right anim-fade" style="--d: 0.35s">
        <template v-if="isTopUser">
           <div class="side-card transparent"></div> 
        </template>
        <template v-else>
           <div class="side-card">
             <span class="side-lbl-mini">Đánh giá</span>
             <div class="stars">★★★★★</div>
           </div>
        </template>
      </aside>

    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router' // 1. Import Router

const props = defineProps(['modelValue', 'mousePos', 'categories', 'topDish'])
defineEmits(['update:modelValue'])

const router = useRouter() // 2. Init Router

const parallaxVars = computed(() => ({
  '--mx': `${props.mousePos.x * 0.02}px`,
  '--my': `${props.mousePos.y * 0.02}px`
}))

const isTopUser = computed(() => props.topDish != null && (props.topDish.followers !== undefined || props.topDish.postCount !== undefined))

const ingredientsList = computed(() => {
  const raw = props.topDish?.ingredients
  if (Array.isArray(raw)) return raw.slice(0, 5)
  if (typeof raw === 'string') return raw.split(',').map(s => s.trim()).filter(Boolean).slice(0, 5)
  return []
})

function emberStyle(n) {
  const size = 3 + (n % 5)
  const left = (n * 13 + 5) % 100
  const top = (n * 19 + 10) % 100
  const delay = (n * 0.3) % 5
  const duration = 4 + (n % 4)
  return {
    '--s': `${size}px`,
    '--left': `${left}%`,
    '--top': `${top}%`,
    '--delay': `${delay}s`,
    '--dur': `${duration}s`
  }
}

// 3. Logic điều hướng
const goToDetail = () => {
  if (isTopUser.value) {
    // Nếu là User -> về trang Profile
    router.push(`/profile/${props.topDish.id}`)
  } else {
    // Nếu là Món ăn -> về trang Post Detail
    router.push(`/post/${props.topDish.id}`)
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,700;0,900;1,900&family=Plus+Jakarta+Sans:wght@400;500;600;700;800;900&display=swap');

.hero-sanctum-supreme {
  min-height: 100vh; position: relative; overflow: hidden; background: #050302;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-family: 'Plus Jakarta Sans', sans-serif;
  scrollbar-width: none;
}

/* --- FX LAYER --- */
.fx-layer { position: absolute; inset: 0; pointer-events: none; }
.spotlight-main { position: absolute; top: -40%; left: 50%; width: 120%; height: 140%; background: radial-gradient(ellipse 60% 50% at 50% 40%, rgba(234, 88, 12, 0.12) 0%, transparent 60%); filter: blur(100px); transform: translateX(-50%); animation: breathe 10s ease-in-out infinite alternate; }
.spotlight-accent { position: absolute; bottom: -20%; right: -10%; width: 50%; height: 50%; background: radial-gradient(circle, rgba(251, 146, 60, 0.05) 0%, transparent 70%); filter: blur(80px); }
.vignette { position: absolute; inset: 0; background: radial-gradient(circle at center, transparent 30%, #050302 120%); }
.noise-texture { position: absolute; inset: 0; opacity: 0.03; background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E"); }
.slash { position: absolute; top: 0; right: 20%; width: 1px; height: 100%; background: linear-gradient(to bottom, transparent, rgba(255,255,255,0.05), transparent); transform: skewX(-20deg); }
.ember { position: absolute; width: var(--s); height: var(--s); left: var(--left); top: var(--top); background: #F97316; border-radius: 50%; filter: blur(1px); opacity: 0; animation: emberFloat var(--dur) linear infinite; animation-delay: var(--delay); box-shadow: 0 0 10px #F97316; }
@keyframes breathe { 0% { opacity: 0.8; } 100% { opacity: 1; } }
@keyframes emberFloat { 0% { transform: translateY(100vh) scale(0); opacity: 0; } 20% { opacity: 0.8; } 80% { opacity: 0.6; } 100% { transform: translateY(-10vh) scale(1); opacity: 0; } }

/* --- CORNER UI --- */
.corner-tag { position: absolute; top: 40px; left: 50px; z-index: 20; display: flex; align-items: center; gap: 8px; color: #EA580C; font-size: 0.75rem; font-weight: 800; letter-spacing: 2px; }
.corner-tag svg { width: 16px; }
.corner-tabs { position: absolute; top: 40px; right: 50px; z-index: 20; display: flex; gap: 8px; }
.corner-tab { background: transparent; border: none; color: rgba(255,255,255,0.4); font-size: 0.8rem; font-weight: 700; letter-spacing: 1px; text-transform: uppercase; padding: 8px 0; cursor: pointer; transition: 0.3s; position: relative; }
.corner-tab:hover { color: white; }
.corner-tab.active { color: #EA580C; }
.corner-tab.active::after { content: ''; position: absolute; bottom: 0; left: 0; width: 100%; height: 2px; background: #EA580C; box-shadow: 0 0 10px #EA580C; }

/* --- STAGE --- */
.stage-wrap { position: relative; z-index: 10; width: 100%; max-width: 1400px; display: grid; grid-template-columns: 200px 1fr 200px; gap: 40px; align-items: center; padding: 0 40px; transform: translate(var(--mx), var(--my)); transition: transform 0.1s ease-out; }
.bg-number { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); font-family: 'Playfair Display', serif; font-size: 28rem; font-weight: 900; color: transparent; -webkit-text-stroke: 2px rgba(255,255,255,0.03); pointer-events: none; z-index: -1; }

.side-panel { display: flex; flex-direction: column; gap: 20px; }
.side-left { align-items: flex-end; text-align: right; }
.side-right { align-items: flex-start; text-align: left; }
.side-card { padding: 15px 20px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.05); backdrop-filter: blur(10px); border-radius: 12px; min-width: 140px; display: flex; flex-direction: column; gap: 5px; transition: 0.3s; }
.side-card:hover { background: rgba(255,255,255,0.08); border-color: rgba(234, 88, 12, 0.3); }
.side-card svg { width: 20px; color: #EA580C; margin-bottom: 5px; }
.side-val { font-size: 1.2rem; font-weight: 700; color: white; line-height: 1; }
.side-lbl { font-size: 0.65rem; color: rgba(255,255,255,0.5); text-transform: uppercase; letter-spacing: 1px; }
.side-lbl-mini { font-size: 0.6rem; color: rgba(255,255,255,0.4); text-transform: uppercase; }
.stars { color: #F59E0B; letter-spacing: 2px; font-size: 0.8rem; }

.stage { display: flex; flex-direction: column; align-items: center; width: 100%; position: relative; }
.stage-frame { position: relative; width: 100%; max-width: 500px; margin-bottom: 30px; }
.frame-glow { position: absolute; inset: -20px; border-radius: 50%; background: radial-gradient(circle, rgba(234, 88, 12, 0.3) 0%, transparent 70%); filter: blur(30px); opacity: 0.6; }
.frame-inner { position: relative; aspect-ratio: 1/1; border-radius: 50%; overflow: hidden; border: 1px solid rgba(255,255,255,0.1); background: #000; box-shadow: 0 0 50px rgba(0,0,0,0.5); transition: transform 0.5s cubic-bezier(0.2, 0.8, 0.2, 1); }
.stage-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.7s ease; }
.frame-inner:hover .stage-img { transform: scale(1.1) rotate(2deg); }
.frame-overlay { position: absolute; inset: 0; background: radial-gradient(circle, transparent 50%, rgba(0,0,0,0.4) 100%); pointer-events: none; }
.stage-badge { position: absolute; bottom: 0; right: 0; background: #EA580C; color: white; width: 90px; height: 90px; border-radius: 50%; display: flex; flex-direction: column; align-items: center; justify-content: center; box-shadow: 0 10px 30px rgba(234, 88, 12, 0.4); border: 4px solid #050302; }
.stage-badge-num { font-family: 'Playfair Display', serif; font-size: 2rem; font-weight: 900; line-height: 1; }
.stage-badge-txt { font-size: 0.55rem; font-weight: 800; letter-spacing: 1px; }

.stage-copy { text-align: center; margin-bottom: 15px; }
.stage-title { font-family: 'Playfair Display', serif; font-size: 3.5rem; font-weight: 700; font-style: italic; margin: 0 0 10px; background: linear-gradient(to bottom, #fff, #ccc); -webkit-background-clip: text; color: transparent; }
.stage-desc { font-size: 1rem; color: rgba(255,255,255,0.6); max-width: 500px; margin: 0 auto; line-height: 1.6; }

/* 🚀 BUTTON HERO */
.stage-action { margin-bottom: 25px; }
.btn-hero { background: linear-gradient(135deg, #EA580C, #F97316); color: white; border: none; padding: 12px 32px; border-radius: 100px; font-weight: 800; font-size: 0.9rem; letter-spacing: 1px; text-transform: uppercase; cursor: pointer; display: flex; align-items: center; gap: 10px; box-shadow: 0 8px 30px rgba(234, 88, 12, 0.4); transition: all 0.3s ease; position: relative; overflow: hidden; }
.btn-hero:hover { transform: translateY(-3px); box-shadow: 0 12px 40px rgba(234, 88, 12, 0.6); }
.btn-hero svg { width: 18px; transition: transform 0.3s; }
.btn-hero:hover svg { transform: translateX(4px); }

.stage-meta { display: flex; align-items: center; gap: 20px; background: rgba(255,255,255,0.05); padding: 8px 24px; border-radius: 50px; border: 1px solid rgba(255,255,255,0.1); backdrop-filter: blur(10px); }
.meta-chef { display: flex; align-items: center; gap: 10px; }
.meta-avt { width: 32px; height: 32px; border-radius: 50%; border: 1px solid #EA580C; }
.meta-chef-name { font-weight: 700; font-size: 0.9rem; }
.meta-chef-role { font-size: 0.6rem; color: #EA580C; display: block; }
.meta-divider { width: 1px; height: 20px; background: rgba(255,255,255,0.1); }
.meta-item { display: flex; align-items: center; gap: 6px; font-size: 0.85rem; font-weight: 600; }
.meta-item svg { width: 16px; color: #EA580C; }
.difficulty-label { text-transform: uppercase; font-size: 0.75rem; letter-spacing: 1px; }
.difficulty-label.easy { color: #34d399; } .difficulty-label.medium { color: #fbbf24; } .difficulty-label.hard { color: #f87171; }

.stage-ingredients { margin-top: 20px; text-align: center; }
.ingredients-label { font-size: 0.6rem; font-weight: 800; letter-spacing: 2px; color: #EA580C; display: block; margin-bottom: 10px; text-transform: uppercase; }
.ingredients-pills { display: flex; justify-content: center; gap: 8px; flex-wrap: wrap; }
.ingredient-pill { font-size: 0.75rem; color: rgba(255,255,255,0.8); border: 1px solid rgba(255,255,255,0.15); padding: 4px 12px; border-radius: 20px; }

/* Animations */
.anim-fade { opacity: 0; animation: fadeIn 1s ease forwards; animation-delay: var(--d); }
.anim-up { opacity: 0; transform: translateY(30px); animation: slideUp 1s cubic-bezier(0.2, 1, 0.3, 1) forwards; animation-delay: var(--d); }
.anim-scale { opacity: 0; transform: scale(0.9); animation: scaleIn 1.2s cubic-bezier(0.2, 1, 0.3, 1) forwards; animation-delay: var(--d); }
@keyframes fadeIn { to { opacity: 1; } }
@keyframes slideUp { to { opacity: 1; transform: translateY(0); } }
@keyframes scaleIn { to { opacity: 1; transform: scale(1); } }

/* Responsive */
@media (max-width: 1200px) { .stage-wrap { grid-template-columns: 1fr; gap: 30px; } .side-panel { flex-direction: row; justify-content: center; gap: 20px; } .side-left { text-align: center; align-items: center; } .side-right { text-align: center; align-items: center; } .bg-number { font-size: 15rem; } }
</style>