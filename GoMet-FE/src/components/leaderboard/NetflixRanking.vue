<template>
  <section class="sanctum-leaderboard" ref="containerRef" @mousemove="handleMouseMove">
    
    <div class="bg-engine">
      <div class="spotlight-core"></div>
      <div class="noise-overlay"></div>
      <div class="floating-particles">
        <div class="particle" v-for="n in 20" :key="n" :style="getParticleStyle(n)"></div>
      </div>
    </div>

    <div class="sanctum-header animate-slide-down">
      <div class="sh-left">
        <span class="sub-tag">BẢNG XẾP HẠNG TUẦN 42</span>
        <h2 class="main-heading">
            GOMET <span class="text-gradient">{{ category === 'chefs' ? 'ELITE CHEFS' : 'MASTERPIECES' }}</span>
        </h2>
      </div>
      <div class="sh-right">
        <div class="nav-pill">
          <button class="nav-btn prev" @click="scroll('left')">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 18l-6-6 6-6"/></svg>
          </button>
          <div class="nav-divider"></div>
          <button class="nav-btn next" @click="scroll('right')">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
          </button>
        </div>
      </div>
    </div>

    <div class="card-stage hide-scrollbar" ref="scrollRef">
      
      <template v-if="category === 'dishes'">
        <div v-for="(item, index) in data" :key="item.id" class="master-card-wrapper" :class="{ 'top-1': index === 0 }">
            
            <div class="rank-badge-3d">
                <span class="rank-val">{{ index + 1 }}</span>
                <span class="rank-shadow">{{ index + 1 }}</span>
            </div>

            <div class="master-card">
                <div class="card-visual">
                    <div class="img-glow"></div>
                    <img :src="item.image" loading="lazy" :alt="item.name" class="dish-img" />
                    
                    <div class="visual-overlay">
                        <div class="overlay-top">
                            <span class="badge-pts">
                            <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                            {{ item.pts?.toLocaleString() || 0 }} PTS
                            </span>
                        </div>
                    </div>
                </div>

                <div class="card-info glass-panel">
                    <h3 class="dish-title">{{ item.name }}</h3>
                    
                    <div class="chef-block">
                        <div class="chef-avt-frame">
                            <img :src="item.authorAvatar" class="chef-img" />
                            <div class="crown" v-if="index === 0">👑</div>
                        </div>
                        <div class="chef-meta">
                            <span class="c-label">Sáng tạo bởi</span>
                            <span class="c-name">{{ item.authorName }}</span>
                        </div>
                    </div>

                    <button class="btn-explore">
                        Khám phá
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
                    </button>
                </div>
            </div>
        </div>
      </template>

      <template v-else>
        <div v-for="(user, index) in data" :key="user.id" class="master-card-wrapper chef-mode" :class="{ 'top-1': index === 0 }">
            
            <div class="rank-badge-3d">
                <span class="rank-val">{{ index + 1 }}</span>
                <span class="rank-shadow">{{ index + 1 }}</span>
            </div>

            <div class="master-card chef-card-style">
                <div class="chef-big-avt">
                    <img :src="user.image" :alt="user.name" />
                    <div class="crown-big" v-if="index === 0">👑</div>
                </div>

                <div class="chef-info-core">
                    <h3 class="chef-big-name">{{ user.name }}</h3>
                    <div class="chef-stats-grid">
                        <div class="stat-box">
                            <span class="sb-val">{{ user.postCount ?? 0 }}</span>
                            <span class="sb-lbl">Bài viết</span>
                        </div>
                        <div class="stat-divider"></div>
                        <div class="stat-box">
                            <span class="sb-val">{{ user.followers ?? 0 }}</span>
                            <span class="sb-lbl">Followers</span>
                        </div>
                    </div>
                    <button class="btn-follow-glow">Theo dõi +</button>
                </div>
            </div>
        </div>
      </template>

    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  data: { type: Array, default: () => [] },
  category: { type: String, default: 'dishes' }
})

const scrollRef = ref(null)

// Tối ưu Scroll Snap
const scroll = (dir) => {
  if (!scrollRef.value) return
  // Tính toán width card + gap để scroll chính xác 1 item
  const itemWidth = 320 + 60 // 320px width + 60px gap
  const amt = dir === 'left' ? -itemWidth : itemWidth
  scrollRef.value.scrollBy({ left: amt, behavior: 'smooth' })
}

const getParticleStyle = (n) => {
  const size = Math.random() * 4 + 2
  return {
    width: `${size}px`, height: `${size}px`,
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 5}s`,
    opacity: Math.random() * 0.5 + 0.2
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,700;1,700;1,900&family=Manrope:wght@400;600;800&display=swap');

/* --- 1. CORE LAYOUT --- */
.sanctum-leaderboard {
  position: relative; padding: 60px 0 100px;
  background: #050302; overflow: hidden; color: #FFF;
  font-family: 'Manrope', sans-serif;
  min-height: 100vh;
}

/* Background FX */
.bg-engine { position: absolute; inset: 0; pointer-events: none; }
.spotlight-core {
  position: absolute; top: -50%; left: 50%; transform: translateX(-50%);
  width: 80%; height: 100%;
  background: radial-gradient(ellipse at center, rgba(234, 88, 12, 0.15) 0%, transparent 70%);
  filter: blur(120px);
}
.noise-overlay {
  position: absolute; inset: 0; opacity: 0.05;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
}
.particle {
  position: absolute; background: #EA580C; border-radius: 50%;
  box-shadow: 0 0 10px #EA580C;
  animation: floatParticle 10s infinite linear;
}
@keyframes floatParticle {
  0% { transform: translateY(0) scale(1); opacity: 0; }
  100% { transform: translateY(-100px) scale(0); opacity: 0; }
}

/* --- 2. HEADER --- */
.sanctum-header {
  display: flex; justify-content: space-between; align-items: flex-end;
  max-width: 1400px; margin: 0 auto 50px; padding: 0 40px;
  position: relative; z-index: 10;
}
.sub-tag {
  display: block; font-size: 0.75rem; letter-spacing: 4px; font-weight: 800;
  color: #EA580C; margin-bottom: 10px; text-transform: uppercase;
}
.main-heading {
  font-family: 'Playfair Display', serif; font-size: 3.5rem; margin: 0; line-height: 1; color: #FFF;
}
.text-gradient {
  background: linear-gradient(135deg, #FFF 30%, #94A3B8 100%);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; font-style: italic;
}

.nav-pill {
  display: flex; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1);
  border-radius: 50px; padding: 4px; backdrop-filter: blur(10px);
}
.nav-btn {
  width: 48px; height: 48px; border-radius: 50%; border: none; background: transparent;
  color: #FFF; cursor: pointer; transition: 0.3s; display: flex; align-items: center; justify-content: center;
}
.nav-btn:hover { background: #EA580C; color: #FFF; box-shadow: 0 0 15px rgba(234, 88, 12, 0.4); }
.nav-divider { width: 1px; height: 24px; background: rgba(255,255,255,0.1); align-self: center; }

/* --- 3. CARDS STAGE & SCROLL SNAP --- */
.card-stage {
  display: flex; gap: 60px; 
  overflow-x: auto; padding: 60px 60px 100px; /* Padding top lớn để chứa số hạng */
  scroll-behavior: smooth; max-width: 1600px; margin: 0 auto;
  
  /* SCROLL SNAP MAGIC */
  scroll-snap-type: x mandatory;
  scroll-padding-left: 60px; /* Căn chỉnh điểm bắt đầu */
}

/* Ẩn thanh cuộn hoàn toàn */
.hide-scrollbar {
    -ms-overflow-style: none;  /* IE and Edge */
    scrollbar-width: none;  /* Firefox */
}
.hide-scrollbar::-webkit-scrollbar {
    display: none; /* Chrome, Safari, Opera */
}

.master-card-wrapper {
  position: relative; flex-shrink: 0; width: 320px;
  transition: transform 0.4s ease;
  scroll-snap-align: start; /* Snap vào đầu mỗi thẻ */
}
.master-card-wrapper:hover { transform: translateY(-10px); z-index: 20; }
.master-card-wrapper.top-1 { width: 360px; }

/* --- 🔥 RANK BADGE 3D (FIXED VISIBILITY) --- */
.rank-badge-3d {
    position: absolute;
    top: -50px; left: -30px; /* Đẩy hẳn ra ngoài góc trái trên */
    z-index: 30; /* Cao hơn tất cả */
    pointer-events: none;
    font-family: 'Playfair Display', serif;
    font-weight: 900;
    line-height: 0.8;
}

.rank-val {
    position: relative; z-index: 2;
    font-size: 8rem; 
    /* Gradient Vàng Kim Loại */
    background: linear-gradient(135deg, #FFFFFF 20%, #94A3B8 80%);
    -webkit-background-clip: text; -webkit-text-fill-color: transparent;
    filter: drop-shadow(0 10px 20px rgba(0,0,0,0.8));
    font-style: italic;
}

.top-1 .rank-val {
    /* Top 1 màu Vàng Rực */
    background: linear-gradient(135deg, #FFD700 0%, #F59E0B 100%);
    -webkit-background-clip: text; -webkit-text-fill-color: transparent;
    font-size: 10rem;
}

.rank-shadow {
    position: absolute; top: 5px; left: 5px; z-index: 1;
    font-size: 8rem; color: transparent;
    -webkit-text-stroke: 2px rgba(234, 88, 12, 0.5);
    opacity: 0.5;
}
.top-1 .rank-shadow { font-size: 10rem; }


/* --- CARD VISUAL --- */
.master-card { position: relative; z-index: 5; background: #1a1a1a; border-radius: 20px; box-shadow: 0 20px 50px rgba(0,0,0,0.5); }
.card-visual {
  height: 420px; border-radius: 20px; overflow: hidden; position: relative;
}
.dish-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.7s ease; }
.master-card:hover .dish-img { transform: scale(1.1); }

.visual-overlay {
  position: absolute; inset: 0; padding: 20px;
  background: linear-gradient(to bottom, rgba(0,0,0,0.2), transparent 40%, rgba(0,0,0,0.9));
  display: flex; flex-direction: column; justify-content: space-between;
}
.badge-pts {
  display: inline-flex; align-items: center; gap: 5px;
  background: rgba(0,0,0,0.6); backdrop-filter: blur(4px); border: 1px solid rgba(255,255,255,0.2);
  color: #EA580C; font-weight: 800; font-size: 0.75rem; padding: 6px 12px; border-radius: 30px;
}

/* --- CARD INFO --- */
.card-info {
  position: absolute; bottom: 20px; left: 20px; right: 20px;
  background: rgba(255,255,255,0.1); backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.2); border-radius: 16px;
  padding: 20px; transform: translateY(0); transition: 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
}
.master-card-wrapper:hover .card-info { transform: translateY(-10px); background: rgba(255,255,255,0.15); border-color: rgba(255,255,255,0.4); }

.dish-title {
  font-family: 'Playfair Display', serif; font-size: 1.5rem; margin: 0 0 15px; color: #FFF; line-height: 1.2;
}

.chef-block { display: flex; align-items: center; gap: 12px; }
.chef-avt-frame { position: relative; width: 40px; height: 40px; }
.chef-img { width: 100%; height: 100%; border-radius: 50%; border: 2px solid #EA580C; object-fit: cover; }
.crown { position: absolute; top: -12px; right: -5px; font-size: 1.2rem; transform: rotate(15deg); filter: drop-shadow(0 2px 4px rgba(0,0,0,0.5)); }

.chef-meta { display: flex; flex-direction: column; }
.c-label { font-size: 0.6rem; color: rgba(255,255,255,0.6); text-transform: uppercase; letter-spacing: 1px; }
.c-name { font-size: 0.9rem; font-weight: 700; color: #FFF; }

.btn-explore {
  position: absolute; right: 20px; bottom: 20px;
  background: #EA580C; color: white; border: none; padding: 8px 16px;
  border-radius: 30px; font-weight: 700; font-size: 0.8rem; cursor: pointer;
  display: flex; align-items: center; gap: 6px; opacity: 0; transform: translateX(20px);
  transition: 0.4s;
}
.master-card-wrapper:hover .btn-explore { opacity: 1; transform: translateX(0); }

/* --- CHEF CARD STYLE --- */
.chef-card-style {
    background: linear-gradient(135deg, #1E1E1E 0%, #0F0F0F 100%);
    border: 1px solid rgba(255,255,255,0.1);
    height: 420px; display: flex; flex-direction: column; align-items: center; justify-content: center;
}
.chef-big-avt { position: relative; width: 140px; height: 140px; margin-bottom: 25px; }
.chef-big-avt img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; border: 4px solid rgba(255,255,255,0.1); box-shadow: 0 0 30px rgba(234,88,12,0.2); }
.crown-big { position: absolute; top: -20px; right: -10px; font-size: 2.5rem; transform: rotate(15deg); filter: drop-shadow(0 4px 10px rgba(0,0,0,0.6)); }

.chef-info-core { text-align: center; width: 100%; padding: 0 30px; }
.chef-big-name { font-family: 'Playfair Display', serif; font-size: 1.8rem; color: #FFF; margin: 0 0 20px; }
.chef-stats-grid { display: flex; justify-content: center; gap: 20px; margin-bottom: 25px; }
.stat-box { display: flex; flex-direction: column; }
.sb-val { font-size: 1.4rem; font-weight: 800; color: #EA580C; }
.sb-lbl { font-size: 0.7rem; color: #94A3B8; text-transform: uppercase; letter-spacing: 1px; }
.stat-divider { width: 1px; height: 30px; background: rgba(255,255,255,0.1); }
.btn-follow-glow {
    background: transparent; border: 1px solid #EA580C; color: #EA580C;
    padding: 10px 30px; border-radius: 50px; font-weight: 700; text-transform: uppercase; letter-spacing: 2px; font-size: 0.75rem;
    cursor: pointer; transition: 0.3s;
}
.btn-follow-glow:hover { background: #EA580C; color: #FFF; box-shadow: 0 0 20px rgba(234, 88, 12, 0.5); }

/* Animation */
.animate-slide-down { animation: slideDown 0.8s ease-out; }
@keyframes slideDown { from { opacity: 0; transform: translateY(-30px); } to { opacity: 1; transform: translateY(0); } }

/* Responsive */
@media (max-width: 768px) {
  .sanctum-header { flex-direction: column; align-items: flex-start; gap: 20px; }
  .main-heading { font-size: 2.5rem; }
  .card-stage { padding: 40px 20px; gap: 30px; }
  .master-card-wrapper { width: 280px; }
}
</style>