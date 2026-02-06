<template>
  <div class="interaction-container">
    <div class="divider-section"></div>

    <div class="grid-interaction">
      
      <section class="author-section fade-in">
        <div class="author-vip-card">
          <div class="auth-left">
            <div class="avatar-ring">
              <img :src="post.authorAvatar" class="auth-img">
            </div>
          </div>
          <div class="auth-right">
            <div class="auth-header">
              <h3 class="auth-name">{{ post.author }} <span class="check-icon">✓</span></h3>
              <button class="btn-connect">Kết Bạn Bếp</button>
            </div>
            <p class="auth-quote">"{{ post.authorQuote || 'Nấu ăn là cách tôi trao gửi yêu thương.' }}"</p>
            <div class="auth-stats">
              <div class="stat"><b>125</b> <span>Công thức</span></div>
              <div class="stat"><b>3.4k</b> <span>Follower</span></div>
              <div class="stat"><b>98%</b> <span>Đánh giá tốt</span></div>
            </div>
          </div>
        </div>
      </section>

      <section class="reviews-section fade-in">
        
        <div class="reviews-header">
          <h3 class="section-title">Đánh giá từ cộng đồng</h3>
          <div class="review-summary-card">
            <div class="score-box">
              <span class="big-score">4.8</span>
              <div class="stars-display">★★★★★</div>
              <span class="total-reviews">120 đánh giá</span>
            </div>
            <div class="rating-bars">
              <div class="bar-row" v-for="i in 5" :key="i">
                <span class="star-label">{{ 6-i }} <span class="s">★</span></span>
                <div class="progress-bg"><div class="progress-fill" :style="{ width: (Math.random() * 80 + 10) + '%' }"></div></div>
              </div>
            </div>
          </div>
        </div>

        <div class="review-input-wrapper">
          <div class="input-header">
            <img src="https://ui-avatars.com/api/?name=User&background=random" class="current-user-avt">
            <div class="rating-selector">
              <span>Bạn chấm món này mấy sao?</span>
              <div class="star-rating-input">
                <span v-for="star in 5" :key="star" 
                      @click="userRating = star" 
                      :class="{ active: star <= userRating }">★</span>
              </div>
            </div>
          </div>
          
          <div class="textarea-box">
            <textarea placeholder="Chia sẻ cảm nhận hoặc mẹo nấu ăn của bạn..."></textarea>
            <div class="input-footer">
              <button class="btn-attach">📷 Thêm ảnh</button>
              <button class="btn-submit-review">Gửi Đánh Giá</button>
            </div>
          </div>
        </div>

        <div class="comments-feed">
          <div class="comment-card" v-for="cmt in commentsList" :key="cmt.id">
            <img :src="cmt.avatar" class="cmt-avatar">
            <div class="cmt-body">
              <div class="cmt-meta">
                <span class="cmt-author">{{ cmt.name }}</span>
                <span class="cmt-date">{{ cmt.time }}</span>
              </div>
              
              <div class="cmt-rating">
                <span class="stars" v-for="n in 5" :key="n" :class="{ filled: n <= cmt.rating }">★</span>
                <span class="verified-badge" v-if="cmt.verified">✓ Đã nấu thử</span>
              </div>

              <p class="cmt-text">{{ cmt.content }}</p>

              <div class="cmt-images" v-if="cmt.images">
                <img v-for="(img, idx) in cmt.images" :key="idx" :src="img">
              </div>

              <div class="cmt-actions">
                <button class="action-link"><span class="icon">👍</span> Hữu ích ({{ cmt.likes }})</button>
                <button class="action-link">Trả lời</button>
              </div>
            </div>
          </div>
        </div>

      </section>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({ post: Object })

const userRating = ref(0) // Biến lưu số sao người dùng chọn

// Dữ liệu giả lập các bình luận
const commentsList = ref([
  {
    id: 1,
    name: "Minh An",
    avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop",
    time: "2 giờ trước",
    rating: 5,
    verified: true,
    content: "Công thức rất chuẩn, mình làm thử cả nhà đều khen ngon! Gà thấm vị, màu lên đẹp y như hình. Cảm ơn chủ thớt nhé!",
    likes: 12,
    images: ["https://images.unsplash.com/photo-1604908177453-7462950a6a3b?w=200&h=150&fit=crop"]
  },
  {
    id: 2,
    name: "Lan Ngọc",
    avatar: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop",
    time: "1 ngày trước",
    rating: 4,
    verified: true,
    content: "Món này ngon nhưng mình nghĩ nên bớt đường một chút sẽ vừa miệng hơn với khẩu vị miền Bắc. Gừng phi thơm nức mũi luôn.",
    likes: 5
  },
  {
    id: 3,
    name: "Tuấn Chef",
    avatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop",
    time: "3 ngày trước",
    rating: 5,
    verified: false,
    content: "Tuyệt vời! Đã lưu lại để cuối tuần trổ tài.",
    likes: 2
  }
])
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;500;600;700;800&family=Playfair+Display:wght@700&display=swap');

.interaction-container { max-width: 1000px; margin: 0 auto; padding: 0 20px 80px; font-family: 'Mulish', sans-serif; color: #1C1917; }
.divider-section { height: 1px; background: linear-gradient(to right, transparent, #E5E5E5, transparent); margin: 60px 0; }

/* --- AUTHOR CARD (GIỮ NGUYÊN) --- */
.author-vip-card { display: flex; align-items: center; gap: 40px; background: #fff; padding: 40px; border-radius: 24px; box-shadow: 0 10px 40px -10px rgba(0,0,0,0.08); border: 1px solid #F3F4F6; margin-bottom: 60px; }
.avatar-ring { padding: 5px; border: 2px dashed #EA580C; border-radius: 50%; }
.auth-img { width: 100px; height: 100px; border-radius: 50%; object-fit: cover; border: 4px solid white; }
.auth-right { flex: 1; }
.auth-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.auth-name { font-family: 'Playfair Display', serif; font-size: 1.8rem; margin: 0; color: #1C1917; display: flex; align-items: center; gap: 8px; }
.check-icon { background: #EA580C; color: white; font-size: 0.7rem; width: 18px; height: 18px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 900; }
.btn-connect { background: #1C1917; color: white; border: none; padding: 10px 24px; border-radius: 30px; font-weight: 700; cursor: pointer; transition: 0.2s; }
.btn-connect:hover { background: #EA580C; box-shadow: 0 5px 15px rgba(234, 88, 12, 0.3); }
.auth-quote { color: #57534E; font-style: italic; margin-bottom: 20px; line-height: 1.5; }
.auth-stats { display: flex; gap: 30px; border-top: 1px solid #F3F4F6; padding-top: 15px; }
.stat { display: flex; flex-direction: column; }
.stat b { font-size: 1.1rem; color: #1C1917; }
.stat span { font-size: 0.8rem; color: #9CA3AF; }

/* --- REVIEWS SECTION (VIP PRO) --- */
.reviews-section { margin-top: 40px; }
.section-title { font-family: 'Playfair Display', serif; font-size: 2rem; margin-bottom: 30px; color: #111827; }

/* 1. Review Summary Card */
.review-summary-card { 
  display: flex; gap: 40px; background: #FAFAF9; padding: 30px; 
  border-radius: 20px; margin-bottom: 40px; border: 1px solid #E7E5E4;
}
.score-box { display: flex; flex-direction: column; align-items: center; justify-content: center; min-width: 150px; text-align: center; }
.big-score { font-size: 3.5rem; font-weight: 800; line-height: 1; color: #1C1917; }
.stars-display { color: #F59E0B; font-size: 1.2rem; letter-spacing: 2px; margin: 5px 0; }
.total-reviews { font-size: 0.9rem; color: #6B7280; }

.rating-bars { flex: 1; display: flex; flex-direction: column; justify-content: center; gap: 8px; }
.bar-row { display: flex; align-items: center; gap: 10px; font-size: 0.85rem; color: #4B5563; }
.star-label { width: 35px; font-weight: 700; display: flex; align-items: center; gap: 4px; }
.star-label .s { color: #F59E0B; font-size: 0.7rem; }
.progress-bg { flex: 1; height: 8px; background: #E5E7EB; border-radius: 4px; overflow: hidden; }
.progress-fill { height: 100%; background: #F59E0B; border-radius: 4px; }

/* 2. Review Input */
.review-input-wrapper { background: white; border: 1px solid #E5E7EB; border-radius: 20px; padding: 25px; box-shadow: 0 4px 20px rgba(0,0,0,0.03); margin-bottom: 50px; }
.input-header { display: flex; align-items: center; gap: 15px; margin-bottom: 20px; }
.current-user-avt { width: 48px; height: 48px; border-radius: 50%; }
.rating-selector { display: flex; flex-direction: column; }
.rating-selector span { font-size: 0.9rem; font-weight: 700; color: #374151; }
.star-rating-input { font-size: 1.5rem; color: #D1D5DB; cursor: pointer; display: flex; gap: 5px; }
.star-rating-input span { transition: 0.2s; }
.star-rating-input span.active, .star-rating-input span:hover { color: #F59E0B; transform: scale(1.1); }

.textarea-box textarea { width: 100%; min-height: 100px; padding: 15px; border: 1px solid #E5E5E5; border-radius: 12px; font-family: inherit; font-size: 1rem; resize: vertical; margin-bottom: 15px; outline: none; transition: 0.2s; }
.textarea-box textarea:focus { border-color: #EA580C; background: #FFF7ED; }
.input-footer { display: flex; justify-content: space-between; align-items: center; }
.btn-attach { background: none; border: none; color: #6B7280; font-weight: 600; cursor: pointer; display: flex; align-items: center; gap: 6px; }
.btn-submit-review { background: #EA580C; color: white; border: none; padding: 10px 24px; border-radius: 8px; font-weight: 700; cursor: pointer; transition: 0.2s; }
.btn-submit-review:hover { background: #C2410C; }

/* 3. Comment List */
.comments-feed { display: flex; flex-direction: column; gap: 30px; }
.comment-card { display: flex; gap: 20px; }
.cmt-avatar { width: 50px; height: 50px; border-radius: 50%; object-fit: cover; border: 2px solid #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
.cmt-body { flex: 1; border-bottom: 1px solid #F3F4F6; padding-bottom: 30px; }
.comment-card:last-child .cmt-body { border-bottom: none; padding-bottom: 0; }

.cmt-meta { display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px; }
.cmt-author { font-weight: 800; font-size: 1rem; color: #111827; }
.cmt-date { font-size: 0.8rem; color: #9CA3AF; }

.cmt-rating { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.stars { color: #D1D5DB; font-size: 0.9rem; letter-spacing: 1px; }
.stars.filled { color: #F59E0B; }
.verified-badge { font-size: 0.75rem; color: #10B981; font-weight: 700; background: #ECFDF5; padding: 2px 8px; border-radius: 10px; }

.cmt-text { font-size: 1rem; line-height: 1.6; color: #4B5563; margin-bottom: 15px; }
.cmt-images { display: flex; gap: 10px; margin-bottom: 15px; }
.cmt-images img { height: 80px; border-radius: 8px; cursor: pointer; transition: 0.2s; border: 1px solid #E5E5E5; }
.cmt-images img:hover { transform: scale(1.05); }

.cmt-actions { display: flex; gap: 20px; }
.action-link { background: none; border: none; color: #6B7280; font-size: 0.85rem; font-weight: 600; cursor: pointer; display: flex; align-items: center; gap: 5px; transition: 0.2s; }
.action-link:hover { color: #EA580C; }

.fade-in { animation: fadeIn 0.8s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

/* Responsive */
@media (max-width: 768px) {
  .author-vip-card { flex-direction: column; text-align: center; }
  .auth-header { justify-content: center; flex-direction: column; gap: 15px; }
  .auth-stats { justify-content: center; }
  .review-summary-card { flex-direction: column; align-items: center; text-align: center; }
  .rating-bars { width: 100%; }
}
</style>