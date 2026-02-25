<template>
  <section class="legacy-feed-sanctum">
    <div class="feed-container">
      <div class="feed-header anim-reveal">
        <div class="label-wrapper">
          <h2 class="label-orange">DANH SÁCH DI SẢN ĐANG LÊN</h2>
          <div class="header-line"></div>
        </div>
        <p class="feed-subtitle">
          Khám phá những tuyệt tác ẩm thực mới nhất đang tiệm cận ngưỡng cửa của sự hoàn mỹ.
        </p>
      </div>

      <div class="feed-grid">
        <div 
          v-for="(post, idx) in posts" 
          :key="post.id" 
          class="editorial-card anim-reveal"
          :style="`--d: ${idx * 0.1}s`"
        >
          <div class="card-header">
            <div class="rank-indicator">
              <span class="rank-tag">HẠNG</span>
              <span class="rank-val">#{{ idx + 11 }}</span>
            </div>
            
            <div class="author-profile">
              <div class="avatar-ring">
                <img :src="post.authorAvatar" class="u-avatar" alt="Author" />
              </div>
              <div class="u-meta">
                <span class="u-name">{{ post.authorName }}</span>
                <span class="u-status">THÀNH VIÊN ELITE</span>
              </div>
            </div>

            <div class="pts-badge-light">
              <span class="val">{{ post.pts }}</span>
              <span class="unit">PTS</span>
            </div>
          </div>

          <div class="card-media">
            <img :src="post.image" class="dish-img" loading="lazy" />
            <div class="image-overlay"></div>
            <div class="category-pill">TOP TRENDING</div>
          </div>

          <div class="card-body">
            <h2 class="dish-title">{{ post.title }}</h2>
            <p class="dish-description">{{ post.description }}</p>
            
            <div class="card-actions">
              <div class="interactions-left">
                <button class="action-btn like-btn">
                  <span class="icon">🧡</span>
                  <span class="count">{{ post.likes }}</span>
                </button>
                <button class="action-btn comment-btn">
                  <span class="icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg></span>
                  <span class="count">{{ post.comments }}</span>
                </button>
              </div>
              
              <button class="btn-orange-outline" @click="$emit('view-recipe', post.id)">
                Xem Công Thức
                <span class="arrow">→</span>
              </button>
            </div>
          </div>

          <div class="hover-flare-effect"></div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
/**
 * Props nhận vào mảng 'posts' từ Leaderboard.vue chính.
 * Mỗi post bao gồm: id, title, pts, likes, comments, image, authorName, authorAvatar, description.
 */
defineProps({
  posts: {
    type: Array,
    default: () => []
  }
})

defineEmits(['view-recipe'])
</script>

<style scoped>
/* --- ✨ LAYOUT ARCHITECTURE --- */
.legacy-feed-sanctum {
  padding: 120px 0;
  background: #fcfcfc; /* Nền xám cực nhạt để làm nổi bật card trắng */
  border-top: 1px solid #f3f4f6;
  position: relative;
  z-index: 10;
}

.feed-container {
  max-width: 1300px;
  margin: 0 auto;
  padding: 0 5%;
}

/* --- ✨ HEADER STYLES --- */
.feed-header {
  text-align: center;
  margin-bottom: 80px;
}

.label-wrapper {
  display: inline-block;
  margin-bottom: 20px;
}

.label-orange {
  font-weight: 900;
  letter-spacing: 5px;
  color: #EA580C; /* Màu Cam Gomet rực rỡ */
  font-size: 0.85rem;
}

.header-line {
  width: 60px;
  height: 4px;
  background: #EA580C;
  margin: 15px auto 0;
  border-radius: 10px;
}

.feed-subtitle {
  color: #666;
  font-size: 1.2rem;
  max-width: 600px;
  margin: 0 auto;
  font-weight: 400;
  line-height: 1.7;
}

/* --- ✨ GRID SYSTEM ✨ --- */
.feed-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(420px, 1fr));
  gap: 50px;
  justify-content: center;
}

/* --- ✨ EDITORIAL CARD DESIGN ✨ --- */
.editorial-card {
  background: #ffffff;
  border-radius: 40px; /* Bo góc lớn sang trọng */
  padding: 40px;
  box-shadow: 0 15px 45px rgba(0,0,0,0.03);
  transition: all 0.5s cubic-bezier(0.19, 1, 0.22, 1);
  border: 1px solid #f3f4f6;
  position: relative;
  overflow: hidden;
}

.editorial-card:hover {
  transform: translateY(-15px);
  box-shadow: 0 30px 70px rgba(234, 88, 12, 0.1);
  border-color: #EA580C;
}

/* 1. Header chi tiết */
.card-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.rank-indicator {
  width: 55px;
  height: 55px;
  background: #fef2f2;
  color: #EA580C;
  border-radius: 18px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.rank-tag { font-size: 8px; font-weight: 900; letter-spacing: 1px; }
.rank-val { font-size: 1.4rem; font-weight: 900; }

.avatar-ring {
  padding: 3px;
  border: 2px solid #EA580C;
  border-radius: 50%;
}

.u-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  object-fit: cover;
}

.u-meta .u-name { font-weight: 800; font-size: 1.05rem; display: block; color: #1a1a1a; }
.u-meta .u-status { font-size: 0.7rem; color: #9ca3af; font-weight: 700; letter-spacing: 1px; }

.pts-badge-light {
  margin-left: auto;
  background: #f9fafb;
  padding: 8px 15px;
  border-radius: 10px;
  font-weight: 900;
  color: #EA580C;
}

/* 2. Media chi tiết */
.card-media {
  height: 320px;
  border-radius: 30px;
  overflow: hidden;
  position: relative;
  margin-bottom: 35px;
}

.dish-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: 1s ease;
}

.editorial-card:hover .dish-img { transform: scale(1.1); }

.category-pill {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 8px 20px;
  border-radius: 100px;
  font-weight: 900;
  font-size: 0.65rem;
  color: #EA580C;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

/* 3. Content chi tiết */
.card-body { position: relative; z-index: 2; }

.dish-title {
  font-family: 'Playfair Display', serif;
  font-size: 2.2rem;
  font-weight: 800;
  margin-bottom: 18px;
  line-height: 1.1;
  color: #1a1a1a;
}

.dish-description {
  color: #666;
  font-size: 1.05rem;
  line-height: 1.7;
  margin-bottom: 35px;
  font-weight: 400;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 4. Actions chi tiết */
.card-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 30px;
  border-top: 1px solid #f3f4f6;
}

.interactions-left { display: flex; gap: 25px; }

.action-btn {
  background: none;
  border: none;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: 0.3s;
}

.action-btn .icon { font-size: 1.2rem; }
.action-btn .count { font-weight: 800; font-size: 0.95rem; color: #4b5563; }
.action-btn:hover { transform: scale(1.1); }

.btn-orange-outline {
  background: none;
  border: 2.5px solid #EA580C; /* Đồng bộ viền cam */
  color: #EA580C;
  padding: 12px 30px;
  border-radius: 100px;
  font-weight: 900;
  font-size: 0.85rem;
  cursor: pointer;
  transition: 0.4s;
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-orange-outline:hover {
  background: #EA580C;
  color: #fff;
  box-shadow: 0 10px 25px rgba(234, 88, 12, 0.3);
}

.btn-orange-outline .arrow { transition: 0.3s; }
.btn-orange-outline:hover .arrow { transform: translateX(5px); }

/* --- ✨ ANIMATIONS ✨ --- */
.anim-reveal {
  opacity: 0;
  transform: translateY(40px);
  animation: revealUp 1.2s cubic-bezier(0.19, 1, 0.22, 1) forwards;
  animation-delay: var(--d, 0s);
}

@keyframes revealUp {
  to { opacity: 1; transform: translateY(0); }
}

/* Responsive */
@media (max-width: 1300px) {
  .feed-grid { grid-template-columns: 1fr; }
  .editorial-card { max-width: 700px; margin: 0 auto; }
}
</style>