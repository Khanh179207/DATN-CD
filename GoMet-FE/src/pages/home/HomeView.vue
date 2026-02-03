<template>
  <div class="home-wrapper">
    
    <header class="hero-premium">
      <div class="bg-decor-blob orange"></div>
      <div class="bg-decor-blob yellow"></div>
      <div class="bg-texture"></div>

      <div class="hero-container">
        <div class="hero-text fade-in-left">
          <div class="pill-badge">
            <span class="icon">✨</span> Công thức trong ngày
          </div>
          
          <h1 class="hero-title">
            Đánh Thức Vị Giác <br>
            Với <span class="highlight-text">Mỳ Ý Sốt Kem Nấm</span>
          </h1>
          
          <p class="hero-desc">
            Sự hòa quyện béo ngậy của kem tươi thượng hạng, nấm hương rừng thơm lừng và chút lá thyme tây. Một bản giao hưởng vị giác chuẩn 5 sao.
          </p>

          <div class="hero-meta-row">
            <div class="meta-pill">🕒 30 Phút</div>
            <div class="meta-pill">🔥 540 Kcal</div>
            <div class="meta-pill">👨‍🍳 Chef Gordon</div>
          </div>

          <div class="cta-group">
            <button class="btn-primary-gradient" @click="goToDetail(1)">
              Xem Công Thức
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
            </button>
            <button class="btn-play-outline">
              <div class="play-icon">▶</div>
              <span>Video hướng dẫn</span>
            </button>
          </div>
        </div>

        <div class="hero-visual fade-in-right">
          <div class="image-stack">
            <img src="https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=1000&q=80" alt="Main Dish" class="main-img floating-anim">
            
            <div class="floating-badge review-badge">
              <span class="star">⭐</span>
              <div>
                <strong>4.9/5</strong>
                <span>(1.2k review)</span>
              </div>
            </div>

            <div class="floating-badge ingredient-badge">
              <span class="icon">🍄</span>
              <div>
                <strong>Nấm Tươi</strong>
                <span>Organic</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <div class="content-body">
      
      <section class="category-section">
        <h2 class="section-heading">Duyệt theo danh mục</h2>
        
        <div class="category-grid">
          <div class="cat-card" v-for="(cat, index) in categories" :key="index" @click="filterBy(cat.slug)">
            <img :src="cat.image" loading="lazy">
            <div class="cat-overlay">
              <h3>{{ cat.name }}</h3>
            </div>
          </div>
        </div>

        <div class="center-btn">
          <button class="btn-view-more">Xem thêm danh mục ▼</button>
        </div>
      </section>

      <section class="section-block">
        <div class="section-header">
          <h2>Công Thức Mới Nhất</h2>
          <div class="tabs">
            <span class="tab active">Tất cả</span>
            <span class="tab">Món mặn</span>
            <span class="tab">Ăn vặt</span>
          </div>
        </div>

        <div class="recipe-grid">
          <div class="recipe-card" v-for="post in posts" :key="post.id" @click="goToDetail(post.id)">
            <div class="card-thumb">
              <img :src="post.image" loading="lazy">
              <span class="tag">{{ post.category }}</span>
              <button class="btn-like">❤️</button>
            </div>
            <div class="card-info">
              <h3>{{ post.title }}</h3>
              <div class="meta">
                <span>⏱️ {{ post.time }}</span>
                <span>🔥 {{ post.author }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

    </div> </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const goToDetail = (id) => {
  try { router.push({ name: 'PostDetail', params: { id: id } }) } 
  catch (e) { router.push(`/post/${id}`) }
}

const filterBy = (slug) => {
  console.log('Filter:', slug)
  router.push('/search')
}

// Dữ liệu Danh mục (Giống ảnh mẫu)
const categories = ref([
  { name: 'Động Vật', slug: 'meat', image: 'https://images.unsplash.com/photo-1607623814075-e51df1bd6562?w=500&q=80' },
  { name: 'Làm Bánh', slug: 'cake', image: 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500&q=80' },
  { name: 'Làm Đẹp', slug: 'beauty', image: 'https://images.unsplash.com/photo-1596472537566-8d460e8d1640?w=500&q=80' },
  { name: 'Healthy', slug: 'healthy', image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500&q=80' },
  { name: 'Ẩm Thực', slug: 'food', image: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500&q=80' },
  { name: 'Trang Trí', slug: 'decor', image: 'https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=500&q=80' },
  { name: 'Đồ Uống', slug: 'drink', image: 'https://images.unsplash.com/photo-1544145945-f90425340c7e?w=500&q=80' },
  { name: 'Châm Ngôn', slug: 'quotes', image: 'https://images.unsplash.com/photo-1555445054-01bfd85f6305?w=500&q=80' },
  { name: 'Hải Sản', slug: 'seafood', image: 'https://images.unsplash.com/photo-1534483852509-1fa33f3792fa?w=500&q=80' },
  { name: 'Du Lịch', slug: 'travel', image: 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=500&q=80' },
])

const posts = ref([
  { id: 1, title: 'Bò Beefsteak Sốt Tiêu Đen', image: 'https://images.unsplash.com/photo-1600891964092-4316c288032e?w=600&q=80', category: 'Món Âu', time: '45m', author: 'Chef Ramsay' },
  { id: 2, title: 'Salad Bơ Trứng Lòng Đào', image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&q=80', category: 'Healthy', time: '15m', author: 'Eat Clean' },
  { id: 3, title: 'Bánh Pancake Dâu Tây', image: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=600&q=80', category: 'Bánh Ngọt', time: '30m', author: 'Sweet Home' },
  { id: 4, title: 'Cà Ri Gà Ấn Độ', image: 'https://images.unsplash.com/photo-1631292784640-2b24be784d5d?w=600&q=80', category: 'Món Á', time: '60m', author: 'Spice King' },
])
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;800;900&family=Quicksand:wght@500;600;700&display=swap');

/* --- GLOBAL SETTINGS --- */
.home-wrapper {
  /* Kéo dãn full màn hình, đè lên padding của layout cha */
  margin: -30px -40px; 
  width: calc(100% + 80px);
  background-color: #FAFAF9;
  font-family: 'Quicksand', sans-serif;
  overflow-x: hidden;
}

.content-body { padding: 40px 60px; position: relative; z-index: 2; }

/* ========================================= */
/* 1. HERO SECTION (NÂNG CẤP VISUAL)         */
/* ========================================= */
.hero-premium {
  position: relative;
  min-height: 90vh; /* Cao gần hết màn hình */
  display: flex; align-items: center; justify-content: center;
  overflow: hidden;
  background: #FFF7ED; /* Nền cam rất nhạt */
}

/* Background Decor Blob (Tạo hiệu ứng nền loang màu) */
.bg-decor-blob { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.6; z-index: 0; animation: floatBlob 10s infinite ease-in-out; }
.bg-decor-blob.orange { width: 500px; height: 500px; background: #FED7AA; top: -100px; right: -100px; }
.bg-decor-blob.yellow { width: 400px; height: 400px; background: #FEF08A; bottom: -50px; left: -50px; animation-delay: 2s; }
.bg-texture {
  position: absolute; inset: 0; opacity: 0.4; z-index: 0;
  background-image: url("data:image/svg+xml,%3Csvg width='20' height='20' viewBox='0 0 20 20' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%23fdba74' fill-opacity='0.2' fill-rule='evenodd'%3E%3Ccircle cx='3' cy='3' r='3'/%3E%3Ccircle cx='13' cy='13' r='3'/%3E%3C/g%3E%3C/svg%3E");
}

.hero-container {
  display: flex; width: 100%; max-width: 1400px; padding: 0 60px; z-index: 1;
  align-items: center;
}

/* Hero Text */
.hero-text { flex: 1; padding-right: 50px; }

.pill-badge {
  display: inline-flex; align-items: center; gap: 8px;
  background: white; padding: 8px 16px; border-radius: 30px;
  font-weight: 700; color: #F97316; font-size: 0.9rem;
  box-shadow: 0 4px 15px rgba(249, 115, 22, 0.1); margin-bottom: 25px;
}

.hero-title {
  font-family: 'Playfair Display', serif;
  font-size: 4.5rem; line-height: 1.1; color: #1C1917; margin-bottom: 20px;
}
.highlight-text {
  color: #F97316; position: relative; display: inline-block;
}
.highlight-text::after {
  content: ''; position: absolute; bottom: 8px; left: 0; width: 100%; height: 12px;
  background: rgba(249, 115, 22, 0.2); z-index: -1; transform: rotate(-1deg);
}

.hero-desc { font-size: 1.15rem; color: #57534E; line-height: 1.7; margin-bottom: 35px; max-width: 550px; }

.hero-meta-row { display: flex; gap: 15px; margin-bottom: 40px; }
.meta-pill {
  background: rgba(255,255,255,0.6); padding: 8px 16px; border-radius: 12px;
  font-weight: 600; color: #44403C; border: 1px solid rgba(0,0,0,0.05);
}

.cta-group { display: flex; gap: 20px; }
.btn-primary-gradient {
  background: linear-gradient(135deg, #F97316 0%, #EA580C 100%);
  color: white; border: none; padding: 16px 36px; border-radius: 50px;
  font-weight: 700; font-size: 1rem; cursor: pointer; display: flex; align-items: center; gap: 10px;
  box-shadow: 0 10px 30px rgba(249, 115, 22, 0.4); transition: 0.3s;
}
.btn-primary-gradient:hover { transform: translateY(-4px); box-shadow: 0 15px 40px rgba(249, 115, 22, 0.5); }

.btn-play-outline {
  background: transparent; border: 2px solid #1C1917; padding: 10px 28px 10px 10px; border-radius: 50px;
  display: flex; align-items: center; gap: 15px; cursor: pointer; transition: 0.3s; color: #1C1917; font-weight: 700;
}
.play-icon { width: 40px; height: 40px; background: #1C1917; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.8rem; }
.btn-play-outline:hover { background: #1C1917; color: white; }
.btn-play-outline:hover .play-icon { background: white; color: #1C1917; }

/* Hero Visual */
.hero-visual { flex: 1.2; position: relative; display: flex; justify-content: flex-end; }
.image-stack { position: relative; width: 100%; max-width: 700px; height: 600px; }
.main-img {
  width: 100%; height: 100%; object-fit: cover; border-radius: 40px;
  /* Mask image để làm mềm cạnh dưới nếu muốn, hoặc bo góc */
  box-shadow: 20px 30px 60px rgba(0,0,0,0.15);
}
.floating-anim { animation: floatImage 6s ease-in-out infinite; }

.floating-badge {
  position: absolute; background: rgba(255,255,255,0.9); backdrop-filter: blur(10px);
  padding: 12px 20px; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  display: flex; align-items: center; gap: 12px; z-index: 2;
}
.review-badge { bottom: 80px; left: -40px; animation: floatBadge 5s ease-in-out infinite; }
.ingredient-badge { top: 60px; right: -20px; animation: floatBadge 7s ease-in-out infinite; }
.floating-badge strong { display: block; color: #1C1917; font-size: 0.9rem; }
.floating-badge span { font-size: 0.75rem; color: #78716C; }
.floating-badge .star, .floating-badge .icon { font-size: 1.5rem; }

/* ========================================= */
/* 2. CATEGORY SECTION (CHUẨN ẢNH MẪU)       */
/* ========================================= */
.category-section { margin-bottom: 80px; }
.section-heading { font-family: 'Playfair Display', serif; font-size: 2.2rem; margin-bottom: 30px; color: #1C1917; }

.category-grid {
  display: grid; 
  /* 5 cột trên màn hình lớn */
  grid-template-columns: repeat(5, 1fr); 
  gap: 20px;
}

.cat-card {
  height: 140px; /* Chiều cao cố định hình chữ nhật */
  border-radius: 20px; overflow: hidden; position: relative; cursor: pointer;
  transition: transform 0.3s ease;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
}
.cat-card:hover { transform: scale(1.03); box-shadow: 0 10px 20px rgba(0,0,0,0.1); }

.cat-card img { width: 100%; height: 100%; object-fit: cover; transition: 0.5s; }
.cat-card:hover img { transform: scale(1.1); }

.cat-overlay {
  position: absolute; inset: 0; 
  background: rgba(0,0,0,0.25); /* Lớp phủ tối để nổi chữ trắng */
  display: flex; align-items: center; justify-content: center;
  transition: 0.3s;
}
.cat-overlay h3 { 
  color: white; font-size: 1.2rem; font-weight: 700; 
  text-shadow: 0 2px 4px rgba(0,0,0,0.3); text-align: center;
}

.center-btn { text-align: center; margin-top: 30px; }
.btn-view-more {
  background: #E5E5E5; color: #1C1917; border: none; padding: 12px 30px; 
  border-radius: 30px; font-weight: 700; cursor: pointer; transition: 0.2s;
}
.btn-view-more:hover { background: #D4D4D4; }

/* ========================================= */
/* 3. RECIPE GRID (GIỮ NGUYÊN)               */
/* ========================================= */
.section-block { margin-top: 40px; }
.section-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 30px; }
.section-header h2 { font-family: 'Playfair Display', serif; font-size: 2.2rem; margin: 0; color: #1C1917; }
.tabs { display: flex; gap: 10px; }
.tab { padding: 8px 20px; border-radius: 20px; border: 1px solid #E5E5E5; color: #57534E; font-weight: 600; cursor: pointer; }
.tab.active { background: #1C1917; color: white; border-color: #1C1917; }

.recipe-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 30px; } /* 4 cột */
.recipe-card { background: white; border-radius: 20px; overflow: hidden; box-shadow: 0 5px 20px rgba(0,0,0,0.05); cursor: pointer; transition: 0.3s; }
.recipe-card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.1); }
.card-thumb { height: 200px; position: relative; }
.card-thumb img { width: 100%; height: 100%; object-fit: cover; }
.tag { position: absolute; top: 15px; left: 15px; background: rgba(255,255,255,0.9); padding: 5px 12px; border-radius: 8px; font-size: 0.75rem; font-weight: 700; color: #F97316; }
.btn-like { position: absolute; top: 15px; right: 15px; width: 35px; height: 35px; border-radius: 50%; border: none; background: white; cursor: pointer; }
.card-info { padding: 20px; }
.card-info h3 { margin: 0 0 10px 0; font-size: 1.1rem; color: #1C1917; font-weight: 700; }
.meta { display: flex; justify-content: space-between; font-size: 0.85rem; color: #78716C; font-weight: 600; }

/* Animation Keyframes */
@keyframes floatImage { 0% { transform: translateY(0); } 50% { transform: translateY(-15px); } 100% { transform: translateY(0); } }
@keyframes floatBadge { 0% { transform: translateY(0); } 50% { transform: translateY(-8px); } 100% { transform: translateY(0); } }
@keyframes floatBlob { 0% { transform: translate(0, 0) scale(1); } 33% { transform: translate(30px, -50px) scale(1.1); } 66% { transform: translate(-20px, 20px) scale(0.9); } 100% { transform: translate(0, 0) scale(1); } }
.fade-in-left { animation: fadeInLeft 0.8s ease-out; }
.fade-in-right { animation: fadeInRight 0.8s ease-out; }
@keyframes fadeInLeft { from { opacity: 0; transform: translateX(-30px); } to { opacity: 1; transform: translateX(0); } }
@keyframes fadeInRight { from { opacity: 0; transform: translateX(30px); } to { opacity: 1; transform: translateX(0); } }

/* Responsive */
@media (max-width: 1200px) {
  .category-grid { grid-template-columns: repeat(3, 1fr); }
  .recipe-grid { grid-template-columns: repeat(3, 1fr); }
  .hero-title { font-size: 3.5rem; }
}
@media (max-width: 900px) {
  .hero-container { flex-direction: column; text-align: center; }
  .hero-text { padding-right: 0; margin-bottom: 50px; }
  .hero-meta-row, .cta-group { justify-content: center; }
  .category-grid { grid-template-columns: repeat(2, 1fr); }
  .recipe-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>