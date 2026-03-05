<template>
  <section class="idea-section">
    <div class="container idea-layout">
      
      <div class="idea-text fade-in-left">
        <span class="sub-tag">{{ $t('landing.idea_tag') }}</span>
        <h2 class="idea-title">
          {{ $t('landing.idea_title') }} <br> 
          <span class="highlight-text">{{ $t('landing.idea_title_hl') }}</span>
        </h2>
        <p class="idea-desc">
          {{ $t('landing.idea_desc') }}
        </p>
        
        <button class="btn-discover">
          {{ $t('landing.idea_cta') }}
          <span class="icon-arrow">→</span>
        </button>
      </div>

      <div class="idea-grid fade-in-right">
        
        <div class="idea-card card-main">
          <img src="https://images.unsplash.com/photo-1547592180-85f173990554?w=800&q=80" alt="Cơm nhà">
          <div class="card-overlay">
            <span class="card-count">1.2k món</span>
            <h3>Cơm Nhà Ấm Cúng</h3>
          </div>
        </div>

        <div class="idea-card card-tall">
          <img src="https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800&q=80" alt="Healthy">
          <div class="card-overlay">
            <span class="card-count">850 món</span>
            <h3>Eat Clean</h3>
          </div>
        </div>

        <div class="idea-card">
          <img src="https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&q=80" alt="Bánh">
          <div class="card-overlay">
            <h3>Làm Bánh</h3>
          </div>
        </div>

        <div class="idea-card">
          <img src="https://images.unsplash.com/photo-1544145945-f90425340c7e?w=800&q=80" alt="Đồ uống">
          <div class="card-overlay">
            <h3>Pha Chế</h3>
          </div>
        </div>

      </div>
    </div>
  </section>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
</script>

<style scoped>
.idea-section {
  background-color: white; /* Nền trắng sang trọng */
  padding: 100px 0;
  min-height: 90vh;
  display: flex; flex-direction: column; justify-content: center;
  font-family: 'Quicksand', sans-serif;
  overflow: hidden;
}

.container { max-width: 1200px; margin: 0 auto; padding: 0 30px; width: 100%; }

.idea-layout {
  display: flex; align-items: center; gap: 80px;
}

/* --- TEXT CONTENT --- */
.idea-text { flex: 0.8; min-width: 350px; }

.sub-tag {
  font-size: 0.85rem; font-weight: 700; color: #A8A29E; text-transform: uppercase; letter-spacing: 2px;
  margin-bottom: 15px; display: block;
}

.idea-title {
  font-family: 'Playfair Display', serif;
  font-size: 4rem; font-weight: 800; color: #1C1917;
  line-height: 1.1; margin-bottom: 25px;
}

.highlight-text {
  color: #F97316; /* Cam chủ đạo */
  position: relative; white-space: nowrap;
}
/* Gạch chân nghệ thuật */
.highlight-text::after {
  content: ''; position: absolute; bottom: 8px; left: 0; width: 100%; height: 12px;
  background: rgba(249, 115, 22, 0.15); z-index: -1; transform: skewX(-10deg);
}

.idea-desc {
  font-size: 1.15rem; color: #57534E; margin-bottom: 40px; line-height: 1.6; max-width: 450px;
}

/* Button Pro */
.btn-discover {
  background: white; border: 2px solid #E7E5E4; color: #1C1917;
  padding: 14px 35px; border-radius: 50px; font-weight: 700; font-size: 1rem;
  cursor: pointer; transition: all 0.3s ease;
  display: inline-flex; align-items: center; gap: 10px;
}
.btn-discover:hover {
  border-color: #F97316; color: #F97316; padding-right: 25px; /* Hiệu ứng kéo dài nút */
}
.icon-arrow { transition: 0.3s; }

/* --- BENTO GRID --- */
.idea-grid {
  flex: 1.2;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, 240px); /* Tăng chiều cao ô lên chút cho thoáng */
  gap: 20px;
}

.idea-card {
  position: relative; border-radius: 24px; overflow: hidden; cursor: pointer;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05); transition: all 0.4s ease;
}
.idea-card:hover { transform: translateY(-8px); box-shadow: 0 20px 40px rgba(249, 115, 22, 0.15); }

.idea-card img {
  width: 100%; height: 100%; object-fit: cover; transition: transform 0.6s ease;
}
.idea-card:hover img { transform: scale(1.1); }

.card-overlay {
  position: absolute; inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, rgba(0,0,0,0) 60%);
  display: flex; flex-direction: column; justify-content: flex-end; padding: 25px;
  color: white; opacity: 0.9; transition: 0.3s;
}
.card-overlay h3 { font-size: 1.3rem; font-weight: 700; margin: 0; font-family: 'Playfair Display', serif; }
.card-count { font-size: 0.8rem; background: rgba(255,255,255,0.2); padding: 4px 10px; border-radius: 20px; align-self: flex-start; margin-bottom: 8px; backdrop-filter: blur(4px); }

/* Grid Positioning */
.card-main { grid-column: 1 / 3; grid-row: 1 / 3; } /* Ô to nhất */
.card-tall { grid-column: 3 / 4; grid-row: 1 / 2; } /* Ô dọc bên phải trên */

/* Animation */
.fade-in-left { animation: fadeInLeft 1s ease-out; }
.fade-in-right { animation: fadeInRight 1s ease-out; }

@keyframes fadeInLeft { from { opacity: 0; transform: translateX(-30px); } to { opacity: 1; transform: translateX(0); } }
@keyframes fadeInRight { from { opacity: 0; transform: translateX(30px); } to { opacity: 1; transform: translateX(0); } }

/* Responsive */
@media (max-width: 1024px) {
  .idea-layout { flex-direction: column; text-align: center; }
  .idea-text { margin-bottom: 40px; }
  .idea-desc { margin-left: auto; margin-right: auto; }
  .idea-grid { width: 100%; grid-template-columns: 1fr 1fr; }
  .card-main { grid-column: 1 / -1; height: 300px; }
  .card-tall { grid-column: auto; grid-row: auto; height: 200px; }
}
</style>