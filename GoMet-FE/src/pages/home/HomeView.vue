<template>
  <div class="home-container">
    
    <section class="discover-section">
      <h2 class="section-title">Khám phá những điều hay nhất trên Gomet</h2>
      <div class="category-grid">
        <div class="category-card">
          <img src="https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=600&q=80" alt="Món Ngon">
          <div class="overlay"><h3>Món Ngon</h3></div>
        </div>
        <div class="category-card">
          <img src="https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600&q=80" alt="Làm Bánh">
          <div class="overlay"><h3>Làm Bánh</h3></div>
        </div>
        <div class="category-card">
          <img src="https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&q=80" alt="Healthy">
          <div class="overlay"><h3>Healthy</h3></div>
        </div>
        <div class="category-card">
          <img src="https://images.unsplash.com/photo-1544145945-f90425340c7e?w=600&q=80" alt="Đồ Uống">
          <div class="overlay"><h3>Đồ Uống</h3></div>
        </div>
      </div>
    </section>

    <section class="posts-section">
      <h3 class="sub-title">Dành cho bạn</h3>
      
      <div class="detailed-grid">
        <div class="post-card-horizontal" v-for="post in posts" :key="post.id">
          
          <div class="card-image">
            <img :src="post.image" :alt="post.title">
          </div>

          <div class="card-content">
            
            <div class="card-header">
              <h4 class="post-title">{{ post.title }}</h4>
              <button class="btn-bookmark">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path>
                </svg>
              </button>
            </div>

            <p class="ingredients-text">
              {{ post.ingredients }}
            </p>

            <div class="meta-info">
              <span class="meta-item">🕒 {{ post.time }}</span>
              <span class="meta-item">👥 {{ post.servings }}</span>
            </div>

            <div class="author-info">
              <img :src="post.authorAvatar" alt="Avatar" class="author-avatar">
              <span class="author-name">{{ post.author }}</span>
            </div>

          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup>
import { ref } from 'vue'

// Dữ liệu mẫu chi tiết hơn (Thêm ingredients, time, servings)
const posts = ref([
  { 
    id: 1, 
    title: 'Sườn Heo Nấu Khoai Tây Đậu', 
    ingredients: 'sườn non • khoai tây • hành tím • đậu trắng • muối hồng', 
    time: '2 tiếng nấu chậm', 
    servings: '2 phần ăn',
    author: 'Rose Truong', 
    authorAvatar: 'https://randomuser.me/api/portraits/women/44.jpg',
    image: 'https://images.unsplash.com/photo-1544025162-d76694265947?w=500&q=80' 
  },
  { 
    id: 2, 
    title: 'Salad Cầu Vồng Ức Gà', 
    ingredients: 'ức gà • xà lách • cà chua bi • bơ sáp • sốt mè rang', 
    time: '15 phút', 
    servings: '1 người',
    author: 'Eat Clean VN', 
    authorAvatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500&q=80' 
  },
  { 
    id: 3, 
    title: 'Canh Chua Cá Lóc Miền Tây', 
    ingredients: 'cá lóc • bạc hà • đậu bắp • me chua • rau om', 
    time: '45 phút', 
    servings: '4 người',
    author: 'Bếp Mẹ Nấu', 
    authorAvatar: 'https://randomuser.me/api/portraits/women/68.jpg',
    image: 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=500&q=80' 
  },
  { 
    id: 4, 
    title: 'Mỳ Ý Sốt Kem Nấm Truffle', 
    ingredients: 'mỳ ý • nấm mỡ • kem tươi • phô mai parmesan • tỏi', 
    time: '30 phút', 
    servings: '2 người',
    author: 'Chef Master', 
    authorAvatar: 'https://randomuser.me/api/portraits/men/85.jpg',
    image: 'https://images.unsplash.com/photo-1555949258-eb67b1ef0ceb?w=500&q=80' 
  }
])
</script>

<style scoped>
/* Import font nếu chưa có ở App.vue */
@import url('https://fonts.googleapis.com/css2?family=Quicksand:wght@500;600;700;800&display=swap');

.home-container {
  padding: 40px 60px;
  /* 👇 MÀU NỀN QUAN TRỌNG: Tạo độ nổi cho Card */
  background-color: #F8FAFC; 
  min-height: 100vh;
  font-family: 'Quicksand', sans-serif;
}

/* --- DANH MỤC --- */
.discover-section { text-align: center; margin-bottom: 60px; }

.section-title {
  font-size: 2rem; font-weight: 800; color: #1e293b; margin-bottom: 40px;
  letter-spacing: -0.5px;
}

.category-grid {
  display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 25px; max-width: 1200px; margin: 0 auto;
}

.category-card {
  height: 220px; border-radius: 24px; overflow: hidden; cursor: pointer;
  position: relative;
  box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}
.category-card:hover { transform: translateY(-8px); }
.category-card img { width: 100%; height: 100%; object-fit: cover; transition: 0.5s; }
.category-card:hover img { transform: scale(1.1); }
.overlay {
  background: linear-gradient(to top, rgba(0,0,0,0.7), transparent); /* Gradient đen từ dưới lên */
  position: absolute; inset: 0; display: flex; align-items: flex-end; justify-content: center; padding-bottom: 20px;
}
.overlay h3 { color: white; font-size: 1.5rem; font-weight: 700; }

/* --- BÀI VIẾT (CARD NGANG) --- */
.posts-section { max-width: 1200px; margin: 0 auto; }
.sub-title { 
  font-size: 1.6rem; font-weight: 700; margin-bottom: 25px; color: #334155;
  border-left: 5px solid #F97316; padding-left: 15px; /* Điểm nhấn gạch dọc màu cam */
}

.detailed-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 30px;
}

.post-card-horizontal {
  display: flex; background: white;
  border-radius: 24px; overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); /* Bóng nhẹ */
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
  height: 200px;
}
.post-card-horizontal:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); /* Bóng đậm khi hover */
  border-color: #fdba74; /* Viền cam nhạt khi hover */
}

.card-image { width: 210px; flex-shrink: 0; }
.card-image img { width: 100%; height: 100%; object-fit: cover; }

.card-content {
  flex: 1; padding: 20px; display: flex; flex-direction: column; justify-content: space-between;
}

.post-title {
  font-size: 1.2rem; font-weight: 700; color: #0f172a; margin: 0;
  line-height: 1.4;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}

.btn-bookmark {
  background: #fff7ed; padding: 8px; border-radius: 50%; border: none; cursor: pointer; color: #F97316;
  display: flex; align-items: center; justify-content: center; transition: 0.2s;
}
.btn-bookmark:hover { background: #ffedd5; transform: scale(1.1); }

.ingredients-text {
  font-size: 0.9rem; color: #64748b; margin: 5px 0;
  line-height: 1.5;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}

.meta-info { display: flex; gap: 15px; font-size: 0.85rem; color: #94a3b8; font-weight: 600; margin-top: 5px; }
.author-info { display: flex; align-items: center; gap: 10px; margin-top: auto; padding-top: 10px; border-top: 1px solid #f1f5f9; }
.author-avatar { width: 28px; height: 28px; border-radius: 50%; object-fit: cover; }
.author-name { font-size: 0.9rem; font-weight: 600; color: #475569; }
</style>