<template>
  <div class="post-detail-wrapper">
    
    <div class="top-bar">
      <button @click="$router.push('/home')" class="btn-back">⬅ Quay lại danh sách</button>
    </div>

    <div class="container">
      
      <div class="post-header">
        <div class="header-image">
          <img :src="post.image" :alt="post.title">
        </div>

        <div class="header-info">
          <div class="header-top">
            <h1 class="post-title">{{ post.title }}</h1>
            <button class="btn-report" @click="reportPost" title="Báo cáo bài viết">⚠️</button>
          </div>
          
          <div class="rating-row">
            <div class="stars">
              <span v-for="n in 5" :key="n" class="star filled">★</span>
            </div>
            <span class="rating-num">4.8 (120 đánh giá)</span>
            <span class="dot">•</span>
            <span class="meta-tag">🕒 {{ post.time }}</span>
            </div>

          <div class="author-box">
            <img :src="post.authorAvatar" class="avatar">
            <div class="author-meta">
              <span class="author-name">{{ post.author }}</span>
              <span class="post-date">Đăng 2 giờ trước • 📍 Hồ Chí Minh</span>
            </div>
            <button class="btn-follow">Theo dõi</button>
          </div>

          <p class="description">{{ post.description }}</p>

          <div class="action-buttons">
            <button class="btn-action primary" @click="toggleSave">
              <span class="icon">{{ isSaved ? '❤️' : '🤍' }}</span> 
              {{ isSaved ? 'Đã Lưu' : 'Yêu Thích' }}
            </button>
            <button class="btn-action outline" @click="showMenuModal = true">
              <span class="icon">📅</span> Lên Thực Đơn
            </button>
            <button class="btn-action outline">
              <span class="icon">📤</span> Chia sẻ
            </button>
          </div>
        </div>
      </div>

      <div class="divider"></div>

      <div class="content-grid">
        
        <aside class="ingredients-column">
          <div class="ing-header">
            <h3 class="section-title">Nguyên Liệu</h3>
            <button class="btn-add-cart" @click="addToShoppingList">🛒 Thêm vào Giỏ</button>
          </div>
          <div class="servings-info">Khẩu phần: <b>{{ post.servings }}</b></div>
          
          <ul class="ing-list">
            <li v-for="(item, index) in ingredients" :key="index">
              <label class="checkbox-row">
                <input type="checkbox" v-model="item.checked">
                <span class="checkmark"></span>
                <span class="ing-text">{{ item.name }}</span>
              </label>
            </li>
          </ul>
        </aside>

        <main class="steps-column">
          
          <div class="note-box" v-if="post.note">
            <h4>💡 Ghi chú của tác giả:</h4>
            <p>{{ post.note }}</p>
          </div>

          <h3 class="section-title">Hướng dẫn cách làm</h3>

          <div class="steps-list">
            <div class="step-item" v-for="(step, index) in post.steps" :key="index">
              <div class="step-idx">{{ index + 1 }}</div>
              <div class="step-detail">
                <p>{{ step.desc }}</p>
                <div class="step-imgs" v-if="step.images && step.images.length">
                  <img v-for="(img, i) in step.images" :key="i" :src="img">
                </div>
              </div>
            </div>
          </div>

          <div class="reviews-section">
            <h3 class="section-title">Đánh giá & Bình luận</h3>
            
            <div class="rating-input-box">
              <span>Bạn thấy món này thế nào?</span>
              <div class="interactive-stars">
                <span v-for="n in 5" :key="n" class="star-input" :class="{ active: userRating >= n }" @click="userRating = n">★</span>
              </div>
            </div>

            <div class="comment-input">
              <img src="https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop" class="avatar-sm">
              <div class="input-wrapper">
                <textarea placeholder="Chia sẻ cảm nhận hoặc đặt câu hỏi..."></textarea>
                <button class="btn-send">Gửi bình luận</button>
              </div>
            </div>

            <div class="comment-list">
              <div class="comment-item" v-for="cmt in comments" :key="cmt.id">
                <img :src="cmt.avatar" class="avatar-sm">
                <div class="cmt-content">
                  <div class="cmt-header">
                    <span class="cmt-user">{{ cmt.user }}</span>
                    <span class="cmt-time">{{ cmt.time }}</span>
                  </div>
                  <div class="cmt-stars">
                    <span v-for="n in 5" :key="n" class="tiny-star" :class="{filled: n <= cmt.rating}">★</span>
                  </div>
                  <p class="cmt-text">{{ cmt.text }}</p>
                  <div class="cmt-actions">
                    <span>Thích</span> • <span>Trả lời</span>
                  </div>
                </div>
              </div>
            </div>
            
            <button class="btn-load-more">Xem thêm bình luận cũ hơn</button>
          </div>

        </main>
      </div>

      <div class="divider"></div>
      <section class="related-section">
        <h3 class="section-title">Món ngon tương tự</h3>
        <div class="related-grid">
          <div class="related-card" v-for="item in relatedPosts" :key="item.id" @click="goToDetail(item.id)">
            <img :src="item.image">
            <div class="related-info">
              <h4>{{ item.title }}</h4>
              <div class="r-meta">
                <span>⏱️ {{ item.time }} nấu</span>
              </div>
            </div>
          </div>
        </div>
      </section>

    </div>

    <div v-if="showMenuModal" class="modal-overlay" @click.self="showMenuModal = false">
      <div class="modal-content">
        <h3>📅 Thêm vào Thực Đơn</h3>
        <div class="form-group"><label>Ngày:</label><input type="date" class="input-control"></div>
        <div class="form-group"><label>Bữa:</label><select class="input-control"><option>Bữa Sáng</option><option>Bữa Trưa</option><option>Bữa Tối</option></select></div>
        <div class="modal-actions">
          <button @click="showMenuModal = false" class="btn-cancel">Hủy</button>
          <button @click="saveToMenu" class="btn-confirm">Lưu Lịch</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const isSaved = ref(false)
const showMenuModal = ref(false)
const userRating = ref(0)

// Dữ liệu bài viết chi tiết (Ảnh thật, bỏ calo)
const post = ref({
  id: route.params.id,
  title: 'Gà Kho Gừng Thơm Lừng Hao Cơm',
  // Ảnh món ăn thật
  image: 'https://images.unsplash.com/photo-1604908177453-7462950a6a3b?w=1200&q=95&auto=format&fit=crop',
  author: 'Bòn Bon',
  // Ảnh tác giả thật
  authorAvatar: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200&h=200&fit=crop',
  description: 'Thịt gà kho gừng thơm lừng hao cơm lủng nồi. Món này thích hợp cho những ngày mưa lạnh, ăn kèm cơm trắng nóng hổi.',
  note: 'Mẹo nhỏ: Nên chọn gà mái dầu thịt sẽ dai và ngọt hơn gà công nghiệp. Khi kho nhớ để lửa liu riu để gia vị thấm sâu vào từng thớ thịt.',
  time: '45 phút',
  servings: '4 người',
  // ĐÃ XÓA CALO
  ingredientsRaw: '1/2 con gà ta • 1 củ gừng lớn • Hành tím, tỏi • Nước mắm, đường, tiêu, hạt nêm',
  steps: [
    { 
      desc: 'Sơ chế gà sạch sẽ, chặt miếng vừa ăn. Ướp gà với gia vị trong 30 phút.', 
      // Ảnh bước làm thật
      images: ['https://images.unsplash.com/photo-1610057099443-fde8c4d203c3?w=300&h=200&fit=crop'] 
    },
    { 
      desc: 'Phi thơm hành tỏi và gừng. Cho gà vào xào săn lại.', 
      images: [] 
    },
    { 
      desc: 'Kho lửa nhỏ đến khi nước sệt lại, gà chuyển màu nâu cánh gián là được.', 
      // Ảnh bước làm thật (nồi kho)
      images: ['https://images.unsplash.com/photo-1546549032-9571cd6b27df?w=300&h=200&fit=crop'] 
    }
  ]
})

const ingredients = ref(post.value.ingredientsRaw.split('•').map(item => ({ name: item.trim(), checked: false })))

// Dữ liệu Bình luận mẫu (Ảnh avatar thật)
const comments = ref([
  { id: 1, user: 'Minh An', avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop', time: '2 giờ trước', text: 'Nhìn ngon quá, cuối tuần phải thử ngay! Cho mình hỏi dùng gà công nghiệp được không ạ?', rating: 5 },
  { id: 2, user: 'Hoàng Tùng', avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop', time: '1 ngày trước', text: 'Công thức chuẩn vị mẹ nấu luôn. Mình kho thêm chút nghệ cho màu đẹp.', rating: 4 },
  { id: 3, user: 'Lan Chi', avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop', time: '3 ngày trước', text: 'Món này tốn cơm lắm nè :))', rating: 5 },
])

// Dữ liệu Bài viết liên quan (Ảnh thật, bỏ calo)
const relatedPosts = ref([
  { id: 101, title: 'Thịt Kho Tàu Trứng Cút', image: 'https://images.unsplash.com/photo-1603105037880-880cd4edfb0d?w=400&h=300&fit=crop', time: '1h 30p' },
  { id: 102, title: 'Canh Chua Cá Lóc', image: 'https://images.unsplash.com/photo-1626776649725-c85425870e5e?w=400&h=300&fit=crop', time: '30p' },
  { id: 103, title: 'Đậu Hũ Nhồi Thịt Sốt Cà', image: 'https://images.unsplash.com/photo-1626776649779-37d19380fb28?w=400&h=300&fit=crop', time: '45p' },
  { id: 104, title: 'Rau Muống Xào Tỏi', image: 'https://images.unsplash.com/photo-1618449840665-9ed54459646b?w=400&h=300&fit=crop', time: '10p' },
])

const toggleSave = () => { isSaved.value = !isSaved.value; alert(isSaved.value ? 'Đã lưu!' : 'Đã bỏ lưu') }
const addToShoppingList = () => alert('Đã thêm nguyên liệu vào giỏ!')
const saveToMenu = () => { alert('Đã lên lịch!'); showMenuModal.value = false }
const reportPost = () => prompt('Lý do báo cáo?')
const goToDetail = (id) => router.push(`/home/post/${id}`)
</script>

<style scoped>
/* CSS GIỮ NGUYÊN NHƯ CŨ, CHỈ CẬP NHẬT DỮ LIỆU */
.post-detail-wrapper { padding: 30px 40px; font-family: 'Quicksand', sans-serif; color: #334155; }
.top-bar { margin-bottom: 20px; }
.btn-back { background: none; border: none; font-weight: 700; color: #64748B; cursor: pointer; }
.btn-back:hover { color: #F97316; }
.container { max-width: 1000px; margin: 0 auto; background: white; border-radius: 20px; padding: 40px; box-shadow: 0 4px 15px rgba(0,0,0,0.03); }

/* HEADER */
.post-header { display: flex; gap: 40px; }
.header-image { width: 45%; flex-shrink: 0; }
.header-image img { width: 100%; height: 380px; object-fit: cover; border-radius: 16px; }
.header-info { flex: 1; display: flex; flex-direction: column; }
.header-top { display: flex; justify-content: space-between; align-items: flex-start; }
.post-title { margin: 0 0 5px 0; font-size: 2rem; font-weight: 800; color: #0F172A; line-height: 1.2; }
.btn-report { background: none; border: none; font-size: 1.2rem; cursor: pointer; opacity: 0.5; }
.btn-report:hover { opacity: 1; }

.rating-row { display: flex; align-items: center; gap: 8px; margin-bottom: 20px; font-size: 0.9rem; color: #64748B; }
.star { color: #CBD5E1; font-size: 1.1rem; }
.star.filled { color: #F59E0B; }
.rating-num { font-weight: 700; color: #0F172A; }
.meta-tag { background: #F1F5F9; padding: 4px 10px; border-radius: 6px; font-weight: 600; color: #64748B; }

.author-box { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px dashed #E2E8F0; }
.avatar { width: 40px; height: 40px; border-radius: 50%; }
.author-meta { flex: 1; display: flex; flex-direction: column; }
.author-name { font-weight: 700; color: #0F172A; }
.post-date { font-size: 0.75rem; color: #94A3B8; }
.btn-follow { background: #EFF6FF; color: #3B82F6; border: none; padding: 5px 12px; border-radius: 20px; font-weight: 700; cursor: pointer; }
.description { font-style: italic; line-height: 1.6; color: #475569; margin-bottom: 25px; }

.action-buttons { display: flex; gap: 10px; margin-top: auto; }
.btn-action { padding: 12px 20px; border-radius: 8px; font-weight: 700; cursor: pointer; display: flex; gap: 8px; align-items: center; transition: 0.2s; }
.primary { background: #F97316; color: white; border: none; }
.primary:hover { background: #EA580C; }
.outline { background: white; border: 1px solid #E2E8F0; color: #475569; }
.outline:hover { border-color: #F97316; color: #F97316; }

.divider { height: 1px; background: #F1F5F9; margin: 40px 0; }

/* CONTENT GRID */
.content-grid { display: grid; grid-template-columns: 320px 1fr; gap: 50px; }
.section-title { font-size: 1.4rem; font-weight: 800; margin-bottom: 20px; color: #1E293B; }

/* Cột Nguyên liệu */
.ingredients-column { background: #FFF7ED; padding: 25px; border-radius: 16px; height: fit-content; }
.ing-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.ing-header h3 { margin: 0; font-size: 1.2rem; }
.servings-info { margin-bottom: 15px; font-size: 0.9rem; color: #EA580C; }
.btn-add-cart { background: #F97316; color: white; border: none; padding: 6px 12px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; cursor: pointer; }
.ing-list { list-style: none; padding: 0; }
.checkbox-row { display: flex; gap: 10px; margin-bottom: 12px; cursor: pointer; align-items: center; }
.checkbox-row input { accent-color: #F97316; width: 16px; height: 16px; }
.ing-text { font-size: 0.95rem; }

/* Cột Cách làm & Note */
.note-box { background: #FEF9C3; border-left: 4px solid #F59E0B; padding: 15px 20px; border-radius: 8px; margin-bottom: 30px; }
.note-box h4 { margin: 0 0 5px 0; color: #B45309; font-size: 1rem; }
.note-box p { margin: 0; font-size: 0.95rem; color: #78350F; font-style: italic; }

.step-item { display: flex; gap: 20px; margin-bottom: 30px; }
.step-idx { width: 30px; height: 30px; background: #0F172A; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; flex-shrink: 0; }
.step-detail p { margin: 0 0 10px 0; line-height: 1.6; }
.step-imgs img { width: 100px; height: 100px; object-fit: cover; border-radius: 8px; margin-right: 10px; }

/* REVIEWS & COMMENTS */
.reviews-section { margin-top: 50px; border-top: 1px solid #F1F5F9; padding-top: 30px; }
.rating-input-box { display: flex; align-items: center; gap: 15px; margin-bottom: 20px; font-weight: 600; }
.interactive-stars { cursor: pointer; }
.star-input { font-size: 1.5rem; color: #E2E8F0; transition: 0.2s; }
.star-input.active { color: #F59E0B; }
.star-input:hover { color: #FCD34D; }

.comment-input { display: flex; gap: 15px; margin-bottom: 30px; }
.avatar-sm { width: 40px; height: 40px; border-radius: 50%; }
.input-wrapper { flex: 1; }
.input-wrapper textarea { width: 100%; padding: 12px; border: 1px solid #E2E8F0; border-radius: 8px; outline: none; font-family: inherit; resize: vertical; min-height: 80px; }
.btn-send { background: #3B82F6; color: white; border: none; padding: 8px 20px; border-radius: 6px; font-weight: 700; cursor: pointer; margin-top: 8px; float: right; }

.comment-list { display: flex; flex-direction: column; gap: 20px; clear: both; }
.comment-item { display: flex; gap: 15px; }
.cmt-content { background: #F8FAFC; padding: 12px 15px; border-radius: 12px; flex: 1; }
.cmt-header { display: flex; justify-content: space-between; margin-bottom: 4px; }
.cmt-user { font-weight: 700; color: #0F172A; }
.cmt-time { font-size: 0.75rem; color: #94A3B8; }
.cmt-stars .tiny-star { font-size: 0.8rem; color: #CBD5E1; }
.cmt-stars .tiny-star.filled { color: #F59E0B; }
.cmt-text { margin: 8px 0; font-size: 0.95rem; line-height: 1.4; color: #334155; }
.cmt-actions { font-size: 0.8rem; color: #64748B; font-weight: 600; cursor: pointer; }
.btn-load-more { width: 100%; padding: 12px; background: #F1F5F9; border: none; color: #64748B; font-weight: 600; border-radius: 8px; margin-top: 20px; cursor: pointer; }
.btn-load-more:hover { background: #E2E8F0; }

/* RELATED SECTION */
.related-section { margin-top: 40px; }
.related-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.related-card { cursor: pointer; transition: 0.2s; }
.related-card:hover { transform: translateY(-5px); }
.related-card img { width: 100%; height: 180px; object-fit: cover; border-radius: 12px; margin-bottom: 8px; }
.related-info h4 { margin: 0 0 5px 0; font-size: 1rem; color: #1E293B; }
.r-meta { font-size: 0.8rem; color: #64748B; display: flex; gap: 10px; }

/* Modal & Responsive */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-content { background: white; padding: 30px; border-radius: 16px; width: 400px; }
.form-group { margin: 15px 0; }
.input-control { width: 100%; padding: 10px; border: 1px solid #E2E8F0; border-radius: 8px; }
.modal-actions { display: flex; gap: 10px; margin-top: 20px; justify-content: flex-end; }
.btn-cancel { background: #F1F5F9; border: none; padding: 10px 20px; border-radius: 8px; cursor: pointer; }
.btn-confirm { background: #F97316; color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: 700; cursor: pointer; }

@media (max-width: 1024px) {
  .post-header { flex-direction: column; }
  .header-image, .header-info { width: 100%; }
  .content-grid { grid-template-columns: 1fr; }
  .related-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>