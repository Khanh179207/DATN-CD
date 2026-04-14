<template>
  <div class="gomet-vault-premium">
    <div class="bg-architecture">
      <div class="grid-lines"></div>
      <div class="noise-film"></div>
      <div class="glow-spot spotlight"></div>
    </div>

    <div class="vault-interface">
      <header class="vault-header">
        <div class="brand-box">
          <span class="brand-sub">GoMet Premium</span>
          <h1 class="brand-main">
            {{ activeMode === 'hub' ? 'Khu Vui Chơi' : 
               (activeMode === 'spin' ? 'Vòng Số Phận' : 
               (activeMode === 'quiz' ? 'Khám Phá Khẩu Vị' : 'Bài Tarot Ẩm Thực')) }}
          </h1>
        </div>
        
        <button v-if="activeMode !== 'hub'" class="btn-back-elegant" @click="returnToHub">
          <span>Trở về</span>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      </header>

      <transition name="fade-up" mode="out-in">
        <div v-if="activeMode === 'hub'" class="hub-container">
          <div class="hub-intro">
            <p>Khám phá định mệnh ẩm thực của bạn qua những thẻ bài ma thuật.</p>
          </div>

          <div class="cards-wrapper">
            <div class="flip-card" :class="{ 'is-flipped': flippedCard === 'spin' }" @click="toggleFlip('spin')">
              <div class="flip-card-inner">
                <div class="flip-card-front card-design-mystic">
                  <div class="card-visual">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><path d="M12 6V12L16 14"/></svg>
                  </div>
                  <h3>Vòng Số Phận</h3>
                  <div class="tap-hint">Chạm để xem chi tiết</div>
                </div>
                <div class="flip-card-back">
                  <div class="back-content">
                    <p>Để vũ trụ xoay vần và chọn ra món ăn hoàn hảo cho bạn một cách ngẫu nhiên nhất.</p>
                    <button class="btn-play-now" @click.stop="enterGame('spin')">Bắt đầu quay</button>
                  </div>
                </div>
              </div>
            </div>

            <div class="flip-card" :class="{ 'is-flipped': flippedCard === 'quiz' }" @click="toggleFlip('quiz')">
              <div class="flip-card-inner">
                <div class="flip-card-front card-design-mystic">
                  <div class="card-visual">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
                  </div>
                  <h3>Khám Phá Khẩu Vị</h3>
                  <div class="tap-hint">Chạm để xem chi tiết</div>
                </div>
                <div class="flip-card-back">
                  <div class="back-content">
                    <p>Trả lời 3 câu hỏi tinh tế về tâm trạng và sở thích để tìm ra hương vị chân ái.</p>
                    <button class="btn-play-now" @click.stop="enterGame('quiz')">Bắt đầu giải đố</button>
                  </div>
                </div>
              </div>
            </div>

            <div class="flip-card" :class="{ 'is-flipped': flippedCard === 'tarot' }" @click="toggleFlip('tarot')">
              <div class="flip-card-inner">
                <div class="flip-card-front card-design-mystic">
                  <div class="card-visual">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                  </div>
                  <h3>Bài Tarot Ẩm Thực</h3>
                  <div class="tap-hint">Chạm để xem chi tiết</div>
                </div>
                <div class="flip-card-back">
                  <div class="back-content">
                    <p>Kết nối năng lượng cá nhân với vũ trụ để nhận thông điệp qua những lá bài định mệnh.</p>
                    <button class="btn-play-now" @click.stop="enterGame('tarot')">Trải bài ngay</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <SpinGame v-else-if="activeMode === 'spin'" :dishes="dishes" @finish="handleGameFinish" />
        <QuizGame v-else-if="activeMode === 'quiz'" :dishes="dishes" @finish="handleGameFinish" />
        <TarotGame v-else-if="activeMode === 'tarot'" :dishes="dishes" @finish="handleGameFinish" />
      </transition>
    </div>

    <transition name="modal-fade">
      <div v-if="showResult" class="modal-overlay">
        <div class="modal-card-premium">
          <button class="btn-close" @click="closeResult">✕</button>
          <div class="modal-body">
            <div class="modal-img-col">
              <img :src="resultDish.image" />
              <div class="img-vignette"></div>
            </div>
            <div class="modal-info-col">
              <div class="info-top">
                <span class="match-badge">Tuyệt tác được chọn</span>
                <h2>{{ resultDish.name }}</h2>
                <p>"{{ resultDish.description }}"</p>
              </div>
              <div class="info-grid">
                <div class="i-item"><label>Thời gian</label><span>{{ resultDish.time }}</span></div>
                <div class="i-item"><label>Độ khó</label><span>{{ resultDish.difficulty }}</span></div>
                <div class="i-item"><label>Phong cách</label><span>{{ resultDish.flavor || 'Tinh tế' }}</span></div>
              </div>
              <div class="info-actions">
                <button class="btn-premium-action" @click="goToDetail">Xem Công Thức</button>
                <button class="btn-outline-action" @click="closeResult">Thử Lại</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, defineAsyncComponent } from 'vue'
import { useRouter } from 'vue-router'
import { getSuggestedPosts } from '@/services/postService'

const SpinGame = defineAsyncComponent(() => import('@/components/suggestions/SpinGame.vue'))
const QuizGame = defineAsyncComponent(() => import('@/components/suggestions/QuizGame.vue'))
const TarotGame = defineAsyncComponent(() => import('@/components/suggestions/TarotGame.vue'))

const router = useRouter()
const dishes = ref([])
const activeMode = ref('hub')
const flippedCard = ref(null) // Quản lý thẻ nào đang lật
const showResult = ref(false)
const resultDish = ref(null)

const toggleFlip = (cardName) => {
  flippedCard.value = flippedCard.value === cardName ? null : cardName
}

const enterGame = (mode) => {
  flippedCard.value = null // Reset lật thẻ trước khi vào game
  activeMode.value = mode
}

const returnToHub = () => { activeMode.value = 'hub'; showResult.value = false }
const closeResult = () => { showResult.value = false }
const handleGameFinish = (dish) => { resultDish.value = dish; showResult.value = true }
const goToDetail = () => router.push(`/post/${resultDish.value.id}`)

onMounted(async () => {
  try {
    const raw = await getSuggestedPosts({ limit: 20 })
    dishes.value = raw.map(dto => ({
      id: dto.postID, name: dto.title, category: dto.categoryName || 'Premium',
      time: dto.cookingTime ? `${dto.cookingTime} min` : '—',
      difficulty: { 1: 'Dễ', 2: 'Vừa', 3: 'Khó' }[dto.level] || 'Vừa',
      image: dto.media, description: dto.description || 'Tuyệt tác ẩm thực GoMet.'
    }))
  } catch (e) {}
})
</script>

<style scoped lang="scss">
/* --- Biến Tùy Chỉnh Màu Sắc --- */
$deep-black: #050505;
$dark-gray: #111111;
$cream: #F4F0EA;
$gold: #D4AF37;
$orange: #EA580C;

/* ================== TỔNG THỂ TRANG (KHẮC PHỤC LỖI HEADER) ================== */
.gomet-vault-premium {
  width: 100%; 
  min-height: 100vh; 
  background: radial-gradient(circle at 50% -10%, #1c1a17 0%, #0a0908 40%, #000000 100%);
  color: $cream;
  font-family: 'Mulish', sans-serif;
  position: relative; 
  display: flex; 
  flex-direction: column;
  padding-top: 80px; 
  padding-bottom: 40px; 
  overflow-x: hidden;
}

/* ================== NỀN ĐIỆN ẢNH CÓ CHIỀU SÂU VÀ ÁNH SÁNG ================== */
.bg-architecture { 
  position: absolute; inset: 0; pointer-events: none; z-index: 0; overflow: hidden; 
}

/* Lưới không gian (Grid Lines) */
.grid-lines {
  position: absolute; inset: 0;
  background-image: 
    linear-gradient(rgba($gold, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba($gold, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: radial-gradient(ellipse at top, black 30%, transparent 80%);
  -webkit-mask-image: radial-gradient(ellipse at top, black 30%, transparent 80%);
}

.noise-film { 
  position: absolute; inset: 0; opacity: 0.05; 
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E"); 
  mix-blend-mode: overlay;
}

/* Spotlight từ trên xuống */
.spotlight { 
  position: absolute; top: -10%; left: 50%; transform: translateX(-50%);
  width: 120vw; height: 60vh; 
  background: radial-gradient(ellipse at top, rgba($gold, 0.12) 0%, rgba($gold, 0.02) 40%, transparent 70%);
  filter: blur(50px);
  animation: breatheLight 5s ease-in-out infinite alternate;
}

/* Ánh sáng hắt từ dưới lên */
.ambient-glow {
  position: absolute; bottom: -20%; right: -10%;
  width: 80vw; height: 60vh;
  background: radial-gradient(circle at center, rgba($orange, 0.05) 0%, transparent 60%);
  filter: blur(80px);
  animation: breatheLight 8s ease-in-out infinite alternate-reverse;
}

@keyframes breatheLight {
  0% { opacity: 0.6; transform: translateX(-50%) scale(0.95); }
  100% { opacity: 1; transform: translateX(-50%) scale(1.05); }
}

/* ================== KHU VỰC GIAO DIỆN (NỘI DUNG CHÍNH) ================== */
.vault-interface {
  flex: 1; display: flex; flex-direction: column; 
  width: 100%; max-width: 1200px; margin: 0 auto; 
  padding: 0 40px; 
  z-index: 10;
  position: relative;
}

/* Header Nội Bộ */
.vault-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 40px;
  padding-bottom: 20px; 
  border-bottom: 1px solid rgba($gold, 0.15);
}
.brand-sub { 
  font-size: 0.75rem; letter-spacing: 5px; font-weight: 900; 
  color: $gold; text-transform: uppercase; display: block; margin-bottom: 5px;
  text-shadow: 0 0 10px rgba($gold, 0.4);
}
.brand-main { 
  font-family: 'Playfair Display', serif; font-size: 2.8rem; 
  font-weight: 800; margin: 0; color: #fff; letter-spacing: -0.5px;
}

.btn-back-elegant {
  background: rgba(255,255,255,0.03); 
  border: 1px solid rgba($gold, 0.2); border-radius: 8px; 
  padding: 10px 20px; color: $cream; display: flex; align-items: center; gap: 10px;
  font-size: 0.8rem; font-weight: 700; text-transform: uppercase; letter-spacing: 2px; 
  cursor: pointer; transition: all 0.3s cubic-bezier(0.19, 1, 0.22, 1);
  backdrop-filter: blur(5px);
  &:hover { 
    background: $gold; color: #000; border-color: $gold; 
    transform: translateX(-5px); box-shadow: 0 0 20px rgba($gold, 0.4); 
  }
}

/* ================== THẺ LẬT CHỌN GAME ================== */
.hub-container { 
  width: 100%; flex: 1; display: flex; flex-direction: column; 
  align-items: center; justify-content: center; 
}
.hub-intro { 
  text-align: center; margin-bottom: 40px; max-width: 600px; 
  p { font-size: 1.05rem; color: rgba(255,255,255,0.6); line-height: 1.6; font-style: italic; } 
}

.cards-wrapper { 
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 40px; width: 100%; perspective: 1200px; 
}

/* 3D FLIP CARD LOGIC */
.flip-card {
  height: 420px; cursor: pointer; perspective: 1200px;
  &.is-flipped .flip-card-inner { transform: rotateY(180deg); }
  
  &:hover .flip-card-front {
    border-color: rgba($gold, 0.6);
    box-shadow: 0 20px 50px rgba(0,0,0,0.8), inset 0 0 30px rgba($gold, 0.05);
    .card-visual { transform: scale(1.1) translateY(-10px); color: #fff; filter: drop-shadow(0 0 15px $gold); }
    h3 { text-shadow: 0 0 15px rgba(255,255,255,0.3); }
  }
}

.flip-card-inner {
  position: relative; width: 100%; height: 100%; text-align: center;
  transition: transform 0.8s cubic-bezier(0.2, 0.8, 0.2, 1);
  transform-style: preserve-3d;
  box-shadow: 0 10px 30px rgba(0,0,0,0.5);
  border-radius: 16px;
}

.flip-card-front, .flip-card-back {
  position: absolute; width: 100%; height: 100%; backface-visibility: hidden;
  border-radius: 16px; border: 1px solid rgba($gold, 0.25);
  display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 40px 30px;
}

.flip-card-front {
  background: linear-gradient(145deg, rgba(20,20,20,0.9), rgba(5,5,5,0.95)); 
  backdrop-filter: blur(10px);
  transition: all 0.4s ease;
  
  .card-visual { 
    color: $gold; margin-bottom: 30px; transition: all 0.5s cubic-bezier(0.2, 0.8, 0.2, 1);
    svg { width: 70px; height: 70px; stroke-width: 1.2; } 
  }
  h3 { font-family: 'Playfair Display', serif; font-size: 2.2rem; color: #fff; margin: 0; transition: text-shadow 0.4s; }
  .tap-hint { 
    font-size: 0.75rem; color: rgba(255,255,255,0.3); text-transform: uppercase; 
    letter-spacing: 3px; margin-top: auto; font-weight: 700;
  }
}

.flip-card-back {
  background: linear-gradient(145deg, #151515, #050505); transform: rotateY(180deg);
  border-color: rgba($orange, 0.4);
  .back-content {
    display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%;
    p { font-size: 1.05rem; color: rgba(255,255,255,0.7); line-height: 1.6; font-style: italic; margin-bottom: 40px; }
    .btn-play-now {
      padding: 16px 45px; background: $gold; color: #000; border: none; border-radius: 50px;
      font-weight: 900; text-transform: uppercase; letter-spacing: 2px; font-size: 0.9rem;
      cursor: pointer; transition: all 0.3s cubic-bezier(0.19, 1, 0.22, 1);
      box-shadow: 0 10px 20px rgba($gold, 0.2);
      &:hover { transform: translateY(-5px) scale(1.05); box-shadow: 0 15px 30px rgba($gold, 0.5); }
    }
  }
}

/* ================== MODAL KẾT QUẢ ================== */
.modal-overlay { 
  position: fixed; inset: 0; background: rgba(0,0,0,0.92); z-index: 2000;
  display: flex; align-items: center; justify-content: center; backdrop-filter: blur(15px); 
}
.modal-card-premium { 
  width: 950px; height: 550px; background: $dark-gray; 
  border: 1px solid rgba($gold, 0.3); display: flex; position: relative; 
  border-radius: 12px; overflow: hidden; box-shadow: 0 40px 100px rgba(0,0,0,1), 0 0 50px rgba($gold, 0.05); 
}
.btn-close {
  position: absolute; top: 20px; right: 20px; background: rgba(255,255,255,0.05); 
  border: none; color: rgba(255,255,255,0.6); width: 40px; height: 40px; border-radius: 50%;
  font-size: 1.2rem; cursor: pointer; transition: 0.3s; z-index: 10;
  display: flex; align-items: center; justify-content: center;
  &:hover { color: #fff; background: rgba($orange, 0.8); transform: rotate(90deg); }
}

.modal-body { display: flex; width: 100%; height: 100%; }
.modal-img-col { width: 45%; position: relative; background: #000; }
.modal-img-col img { width: 100%; height: 100%; object-fit: cover; opacity: 0.9; }
.img-vignette { position: absolute; inset: 0; background: linear-gradient(to right, transparent, $dark-gray); }

.modal-info-col { width: 55%; padding: 50px; display: flex; flex-direction: column; justify-content: center; }
.match-badge { 
  font-size: 0.7rem; font-weight: 900; color: $gold; letter-spacing: 3px; 
  margin-bottom: 15px; display: block; text-transform: uppercase; 
}
.modal-info-col h2 { font-family: 'Playfair Display', serif; font-size: 3rem; margin: 0 0 15px; color: #fff; line-height: 1.1; }
.modal-info-col p { 
  font-size: 1.05rem; color: rgba(255,255,255,0.5); line-height: 1.6; 
  margin-bottom: 35px; font-style: italic; border-left: 3px solid $gold; padding-left: 20px; 
}

.info-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 40px; }
.i-item label { font-size: 0.65rem; color: rgba(255,255,255,0.4); font-weight: 800; text-transform: uppercase; margin-bottom: 8px; letter-spacing: 1.5px; display: block;}
.i-item span { font-size: 1.1rem; font-weight: 700; color: $cream; font-family: 'Playfair Display', serif; }

.info-actions { display: flex; gap: 20px; }
.btn-premium-action {
  flex: 1; padding: 18px; background: linear-gradient(135deg, rgba($gold, 0.1), transparent); 
  border: 1px solid rgba($gold, 0.5); color: $gold; font-weight: 900; text-transform: uppercase; 
  letter-spacing: 2px; font-size: 0.8rem; cursor: pointer; transition: 0.4s; border-radius: 8px;
  &:hover { background: $gold; color: #000; box-shadow: 0 10px 30px rgba($gold, 0.3); }
}
.btn-outline-action {
  padding: 18px 35px; background: transparent; border: 1px solid rgba(255,255,255,0.1); 
  color: rgba(255,255,255,0.5); font-weight: 800; font-size: 0.8rem; text-transform: uppercase; 
  letter-spacing: 2px; cursor: pointer; transition: 0.3s; border-radius: 8px;
  &:hover { color: #fff; border-color: rgba(255,255,255,0.4); background: rgba(255,255,255,0.05); }
}

/* Animations */
.fade-up-enter-active, .fade-up-leave-active { transition: all 0.6s cubic-bezier(0.19, 1, 0.22, 1); }
.fade-up-enter-from { opacity: 0; transform: translateY(30px); }
.fade-up-leave-to { opacity: 0; transform: translateY(-30px); }
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.4s; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }

/* =========================================================================
   🔥 HỆ THỐNG RESPONSIVE (ĐẢM BẢO HOÀN HẢO CHO MỌI THIẾT BỊ)
========================================================================= */

/* --- 1. MÀN HÌNH NHỎ & TABLET NGANG (MAX 1024px) --- */
@media (max-width: 1024px) {
  .cards-wrapper { gap: 20px; }
  .modal-card-premium { width: 90vw; height: 600px; }
  .modal-info-col { padding: 30px; }
  .modal-info-col h2 { font-size: 2.4rem; }
}

/* --- 2. TABLET DỌC & MOBILE NGANG (MAX 992px) --- */
@media (max-width: 992px) {
  .vault-interface { padding: 0 30px; }
  
  /* Lưới Thẻ: 2 cột, thẻ thứ 3 canh giữa */
  .cards-wrapper { 
    grid-template-columns: repeat(2, 1fr); 
  }
  .flip-card:last-child { 
    grid-column: 1 / -1; 
    max-width: 400px; 
    justify-self: center; 
    width: 100%; 
  }
  
  /* Xoay Dọc Modal Kết Quả */
  .modal-card-premium {
    height: auto; 
    max-height: 90vh;
    overflow-y: auto;
  }
  .modal-body { flex-direction: column; }
  .modal-img-col { width: 100%; height: 350px; flex-shrink: 0; }
  
  /* Đổi hướng đổ bóng Vignette */
  .img-vignette { background: linear-gradient(to bottom, transparent, $dark-gray); }
  
  .modal-info-col { width: 100%; padding: 30px; }
}

/* --- 3. MOBILE LỚN (MAX 768px) --- */
@media (max-width: 768px) {
  .gomet-vault-premium { padding-top: 40px; }
  .vault-interface { padding: 0 20px; }
  
  .vault-header {
    flex-direction: column; 
    align-items: flex-start; 
    gap: 15px; 
    margin-bottom: 30px;
  }
  .brand-main { font-size: 2.2rem; }
  
  /* Đưa lưới thẻ về 1 cột trên Mobile */
  .cards-wrapper { grid-template-columns: 1fr; }
  .flip-card:last-child { max-width: 100%; }
  
  .flip-card { height: 380px; }
  .flip-card-front h3 { font-size: 1.8rem; }
}

/* --- 4. MOBILE NHỎ (MAX 480px) --- */
@media (max-width: 480px) {
  .flip-card { height: 320px; }
  .brand-main { font-size: 1.8rem; }
  .hub-intro p { font-size: 0.95rem; }
  
  /* Nút Play Now thu gọn */
  .flip-card-back .back-content .btn-play-now {
    padding: 12px 30px !important;
    font-size: 0.8rem !important;
  }
  .flip-card-back .back-content p { font-size: 0.9rem; margin-bottom: 25px; }

  /* Modal tối ưu diện tích cho điện thoại nhỏ */
  .modal-img-col { height: 220px; }
  .modal-info-col { padding: 25px 20px; }
  .modal-info-col h2 { font-size: 1.8rem; }
  .modal-info-col p { font-size: 0.95rem; margin-bottom: 25px; }
  
  /* Dàn lưới thông tin món ăn dọc */
  .info-grid {
    grid-template-columns: 1fr; 
    gap: 10px; 
    margin-bottom: 25px;
  }
  
  /* Dàn dọc cụm nút bấm */
  .info-actions { flex-direction: column; gap: 10px; }
  .btn-premium-action, .btn-outline-action {
    width: 100%; 
    padding: 14px; 
    font-size: 0.75rem; 
    text-align: center;
  }
}
</style>