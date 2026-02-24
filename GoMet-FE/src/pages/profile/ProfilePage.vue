<template>
  <div class="gomet-profile-full">
    
    <div class="bg-layer">
      <div class="grid-mesh"></div>
      <div class="noise-texture"></div>
    </div>

    <div class="profile-layout-wide">
      
      <aside class="col-left">
        <div class="sticky-wrapper">
          <div class="id-card">
            <div class="avatar-box">
              <img :src="user.avatar" class="avatar-img" alt="Chef">
              <div class="verify-badge">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
              </div>
            </div>
            
            <h1 class="user-name">{{ user.name }}</h1>
            <p class="user-handle">@{{ user.handle }}</p>
            
            <div class="bio-box">
              <p>{{ user.bio }}</p>
            </div>

            <div class="action-stack">
              <button class="btn-primary">CHỈNH SỬA</button>
              <button class="btn-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"/><polyline points="16 6 12 2 8 6"/><line x1="12" y1="2" x2="12" y2="15"/></svg>
              </button>
            </div>

            <div class="social-links">
              <a href="#" class="s-link">Website</a>
              <a href="#" class="s-link">Instagram</a>
              <a href="#" class="s-link">Facebook</a>
            </div>
          </div>
        </div>
      </aside>

      <main class="col-center">
        
        <div class="content-header">
          <h2 class="section-title">Masterpieces <span class="count">{{ posts.length }}</span></h2>
          <div class="filter-tabs">
            <span class="tab active">Tất cả</span>
            <span class="tab">Món Âu</span>
            <span class="tab">Món Á</span>
          </div>
        </div>

        <div class="recipe-feed">
          <RecipeCard 
            v-for="post in posts" 
            :key="post.id" 
            :post="post" 
            class="feed-item"
          />
        </div>

      </main>

      <aside class="col-right">
        <div class="sticky-wrapper">
          
          <div class="widget-box">
            <h3 class="w-title">THỐNG KÊ</h3>
            <div class="stat-grid">
              <div class="stat-cell">
                <span class="val">{{ user.postsCount }}</span>
                <span class="lbl">Bài viết</span>
              </div>
              <div class="stat-cell">
                <span class="val">{{ user.followers }}</span>
                <span class="lbl">Followers</span>
              </div>
              <div class="stat-cell">
                <span class="val">{{ user.following }}</span>
                <span class="lbl">Following</span>
              </div>
              <div class="stat-cell">
                <span class="val">1.2M</span>
                <span class="lbl">Total Likes</span>
              </div>
            </div>
          </div>

          <div class="widget-box">
            <h3 class="w-title">THÀNH TỰU</h3>
            <ul class="award-list">
              <li>
                <span class="icon">🏆</span>
                <div class="award-info">
                  <strong>Top Chef 2023</strong>
                  <span>Giải thưởng Gomet năm</span>
                </div>
              </li>
              <li>
                <span class="icon">🌟</span>
                <div class="award-info">
                  <strong>Creator of Month</strong>
                  <span>Tháng 12/2025</span>
                </div>
              </li>
              <li>
                <span class="icon">🍳</span>
                <div class="award-info">
                  <strong>500 Công thức</strong>
                  <span>Cột mốc cống hiến</span>
                </div>
              </li>
            </ul>
          </div>

        </div>
      </aside>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import RecipeCard from '@/components/common/RecipeCard.vue'

const user = ref({
  name: 'Nguyễn Khánh',
  handle: 'khanh_chef',
  avatar: 'https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800&auto=format&fit=crop&q=60', 
  bio: 'Creative Chef tại Gomet. Theo đuổi phong cách ẩm thực tối giản, tôn trọng nguyên bản của từng nguyên liệu. "Less is more".',
  postsCount: 128,
  followers: '45.2k',
  following: 320
})

const posts = ref([
  { id: 1, title: 'Bò Wagyu A5 Sốt Tiêu Đen', image: 'https://images.unsplash.com/photo-1544025162-d76694265947?w=800', author: { name: 'Nguyễn Khánh', avatar: user.value.avatar }, likes: 1200, time: '45p', difficulty: 'Khó' },
  { id: 2, title: 'Salad Địa Trung Hải', image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800', author: { name: 'Nguyễn Khánh', avatar: user.value.avatar }, likes: 890, time: '15p', difficulty: 'Dễ' },
  { id: 3, title: 'Mỳ Ý Hải Sản Sốt Kem', image: 'https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=800', author: { name: 'Nguyễn Khánh', avatar: user.value.avatar }, likes: 2100, time: '30p', difficulty: 'Vừa' },
  { id: 4, title: 'Bánh Mì Nướng Bơ Tỏi', image: 'https://images.unsplash.com/photo-1550547660-d9450f859349?w=800', author: { name: 'Nguyễn Khánh', avatar: user.value.avatar }, likes: 500, time: '10p', difficulty: 'Dễ' },
  { id: 5, title: 'Cá Hồi Áp Chảo', image: 'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=800', author: { name: 'Nguyễn Khánh', avatar: user.value.avatar }, likes: 1500, time: '25p', difficulty: 'Vừa' },
])
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;900&family=Manrope:wght@400;500;600;700;800&display=swap');

/* --- 1. CORE LAYOUT --- */
.gomet-profile-full {
  width: 100%; 
  /* KHÔNG set height: 100vh để cho phép body cuộn */
  background: #FBF6F1; color: #1E293B;
  font-family: 'Manrope', sans-serif;
  position: relative;
}

/* Background */
.bg-layer { position: fixed; inset: 0; pointer-events: none; z-index: 0; }
.grid-mesh {
  position: absolute; inset: 0;
  background-image: linear-gradient(rgba(0,0,0,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(0,0,0,0.03) 1px, transparent 1px);
  background-size: 40px 40px;
}
.noise-texture {
  position: absolute; inset: 0; opacity: 0.04;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
}

/* --- 2. 3-COLUMN LAYOUT --- */
.profile-layout-wide {
  display: flex; gap: 40px;
  max-width: 1600px; margin: 0 auto; padding: 40px;
  position: relative; z-index: 10;
}

/* --- LEFT COLUMN: IDENTITY --- */
.col-left { width: 300px; flex-shrink: 0; }
.sticky-wrapper { position: sticky; top: 100px; display: flex; flex-direction: column; gap: 30px; }

.id-card { text-align: center; }
.avatar-box { position: relative; width: 180px; height: 180px; margin: 0 auto 20px; }
.avatar-img {
  width: 100%; height: 100%; object-fit: cover; border-radius: 50%;
  border: 4px solid #FFF; box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}
.verify-badge {
  position: absolute; bottom: 10px; right: 10px; background: #EA580C; color: #FFF;
  width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center;
  border: 3px solid #FFF;
}

.user-name { font-family: 'Playfair Display', serif; font-size: 2rem; margin: 0 0 5px; line-height: 1.1; }
.user-handle { color: #64748B; font-weight: 600; font-size: 0.9rem; margin-bottom: 20px; }

.bio-box { font-size: 0.9rem; color: #334155; line-height: 1.6; margin-bottom: 25px; }

.action-stack { display: flex; gap: 10px; justify-content: center; margin-bottom: 30px; }
.btn-primary {
  flex: 1; background: #1E293B; color: #FFF; border: none; padding: 12px; border-radius: 8px;
  font-weight: 700; cursor: pointer; transition: 0.3s;
}
.btn-primary:hover { background: #EA580C; }
.btn-icon {
  width: 44px; background: #FFF; border: 1px solid #E2E8F0; border-radius: 8px;
  display: flex; align-items: center; justify-content: center; cursor: pointer; color: #1E293B;
}
.btn-icon:hover { border-color: #EA580C; color: #EA580C; }

.social-links { display: flex; flex-direction: column; gap: 10px; align-items: center; border-top: 1px solid #E2E8F0; padding-top: 20px; }
.s-link { font-size: 0.8rem; font-weight: 700; color: #64748B; text-decoration: none; transition: 0.2s; text-transform: uppercase; letter-spacing: 1px; }
.s-link:hover { color: #EA580C; }

/* --- CENTER COLUMN: CONTENT --- */
.col-center { flex: 1; }

.content-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 30px; border-bottom: 2px solid #E2E8F0; padding-bottom: 15px; }
.section-title { font-family: 'Playfair Display', serif; font-size: 2.5rem; margin: 0; line-height: 1; }
.count { font-family: 'Manrope', sans-serif; font-size: 1rem; color: #EA580C; vertical-align: super; }

.filter-tabs { display: flex; gap: 20px; }
.tab { font-size: 0.9rem; font-weight: 700; color: #94A3B8; cursor: pointer; transition: 0.2s; }
.tab.active { color: #1E293B; }
.tab:hover { color: #EA580C; }

.recipe-feed {
  display: grid; grid-template-columns: repeat(2, 1fr); gap: 30px;
}

/* --- RIGHT COLUMN: STATS --- */
.col-right { width: 280px; flex-shrink: 0; }

.widget-box {
  background: #FFF; border: 1px solid rgba(0,0,0,0.05); padding: 25px; border-radius: 16px; margin-bottom: 30px;
}
.w-title { font-size: 0.7rem; font-weight: 800; color: #94A3B8; letter-spacing: 2px; margin: 0 0 20px; border-bottom: 1px solid #F1F5F9; padding-bottom: 10px; }

.stat-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.stat-cell { display: flex; flex-direction: column; }
.stat-cell .val { font-size: 1.2rem; font-weight: 800; color: #1E293B; }
.stat-cell .lbl { font-size: 0.75rem; color: #64748B; margin-top: 4px; }

.award-list { list-style: none; padding: 0; margin: 0; }
.award-list li { display: flex; gap: 15px; margin-bottom: 20px; align-items: center; }
.award-list li:last-child { margin-bottom: 0; }
.award-list .icon { font-size: 1.5rem; background: #FFF7ED; width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; border-radius: 8px; }
.award-info { display: flex; flex-direction: column; }
.award-info strong { font-size: 0.9rem; color: #1E293B; }
.award-info span { font-size: 0.75rem; color: #64748B; }

/* RESPONSIVE */
@media (max-width: 1200px) {
  .profile-layout-wide { flex-direction: column; }
  .col-left, .col-right { width: 100%; }
  .sticky-wrapper { position: static; flex-direction: row; gap: 40px; flex-wrap: wrap; }
  .id-card { text-align: left; display: flex; gap: 30px; align-items: flex-start; flex: 1; }
  .recipe-feed { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 768px) {
  .id-card { flex-direction: column; }
  .recipe-feed { grid-template-columns: 1fr; }
  .sticky-wrapper { flex-direction: column; }
}
</style>