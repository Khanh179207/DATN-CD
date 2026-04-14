<template>
  <transition name="modal-fade" @enter="onEnter" @leave="onLeave">
    <div v-if="isVisible" class="modal-overlay" @click.self="closeModal">
      
      <div class="particles-bg"></div>

      <div class="modal-card-glass" ref="modalCard">
        <button class="btn-close-glass anim-item" @click="closeModal">
          <i class="fas fa-times"></i>
        </button>
        
        <div class="modal-body">
          <div class="modal-img-col anim-item">
            <img :src="dish.image" :alt="dish.name" class="dish-hero-img" />
            <div class="img-vignette"></div>
            <div class="img-glow-bottom"></div>
          </div>
          
          <div class="modal-info-col">
            <div class="info-top">
              <span class="match-badge anim-item">
                <i class="fas fa-magic"></i> Chân Ái Của Bạn
              </span>
              <h2 class="result-title anim-item">{{ dish.name }}</h2>
              <p class="result-desc anim-item">"{{ dish.description }}"</p>
            </div>
            
            <div class="info-bento-grid">
              <div class="bento-box anim-item" style="--stagger: 1;">
                <div class="bento-icon"><i class="fas fa-clock"></i></div>
                <div class="bento-text">
                  <span class="bento-lbl">Thời gian</span>
                  <span class="bento-val">{{ dish.time }}</span>
                </div>
              </div>
              
              <div class="bento-box anim-item" style="--stagger: 2;">
                <div class="bento-icon"><i class="fas fa-fire"></i></div>
                <div class="bento-text">
                  <span class="bento-lbl">Độ khó</span>
                  <span class="bento-val">{{ dish.difficulty }}</span>
                </div>
              </div>
              
              <div class="bento-box anim-item" style="--stagger: 3;">
                <div class="bento-icon"><i class="fas fa-leaf"></i></div>
                <div class="bento-text">
                  <span class="bento-lbl">Phong cách</span>
                  <span class="bento-val">{{ dish.flavor || 'Tinh Tế' }}</span>
                </div>
              </div>
            </div>
            
            <div class="info-actions anim-item">
              <button class="btn-primary-glow" @click="goToDetail">
                <span>Vào Bếp Ngay</span> <i class="fas fa-chevron-right"></i>
              </button>
              <button class="btn-outline-glass" @click="closeModal">
                Thử Lại <i class="fas fa-redo"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'

const props = defineProps({
  isVisible: { type: Boolean, default: false },
  dish: { type: Object, required: true }
})

const emit = defineEmits(['close'])
const router = useRouter()
const modalCard = ref(null)

const closeModal = () => { emit('close') }
const goToDetail = () => {
  if (props.dish && props.dish.id) {
    router.push(`/post/${props.dish.id}`)
  }
}

// 🚀 GSAP ANIMATION KHI MODAL XUẤT HIỆN
const onEnter = (el, done) => {
  // Chờ DOM cập nhật
  nextTick(() => {
    const card = el.querySelector('.modal-card-glass')
    const items = el.querySelectorAll('.anim-item')
    const img = el.querySelector('.dish-hero-img')

    // Timeline hiệu ứng
    const tl = gsap.timeline({ onComplete: done })

    // 1. Phóng to mượt thẻ Modal chính
    gsap.set(card, { scale: 0.8, opacity: 0, rotationX: 10 })
    tl.to(card, { 
      scale: 1, opacity: 1, rotationX: 0, 
      duration: 0.6, ease: "back.out(1.2)" 
    })

    // 2. Zoom nhẹ hình ảnh món ăn
    gsap.set(img, { scale: 1.2 })
    tl.to(img, { scale: 1, duration: 1.5, ease: "power3.out" }, "-=0.4")

    // 3. Stagger (Lần lượt) hiện chữ và nút
    gsap.set(items, { y: 30, opacity: 0 })
    tl.to(items, { 
      y: 0, opacity: 1, 
      duration: 0.5, stagger: 0.08, ease: "power2.out" 
    }, "-=1.2")
  })
}

// 🚀 GSAP ANIMATION KHI ĐÓNG MODAL
const onLeave = (el, done) => {
  const card = el.querySelector('.modal-card-glass')
  gsap.to(card, { 
    scale: 0.9, opacity: 0, y: 30, 
    duration: 0.3, ease: "power2.in", onComplete: done 
  })
}
</script>

<style scoped lang="scss">
/* BẢNG MÀU CHUẨN XỊN */
$gold: #D4AF37;
$orange: #EA580C;
$bg-deep: #09090b;
$border-glass: rgba(255, 255, 255, 0.08);
$ease-bounce: cubic-bezier(0.34, 1.56, 0.64, 1);

/* LỚP PHỦ NGOÀI CÙNG */
.modal-overlay { 
  position: fixed; inset: 0; z-index: 2000;
  display: flex; align-items: center; justify-content: center; 
  background: radial-gradient(circle at center, rgba(0,0,0,0.7), rgba(0,0,0,0.95));
  backdrop-filter: blur(15px); 
}

/* HẠT BỤI ÁNH SÁNG BAY LƠ LỬNG TRONG NỀN */
.particles-bg {
  position: absolute; inset: 0; z-index: 1; pointer-events: none; opacity: 0.5;
  background-image: radial-gradient($gold 1px, transparent 1px);
  background-size: 50px 50px;
  animation: moveStars 100s linear infinite;
}
@keyframes moveStars { from { background-position: 0 0; } to { background-position: -1000px 1000px; } }

/* KHUNG MODAL CHÍNH */
.modal-card-glass { 
  position: relative; z-index: 10;
  width: 95%; max-width: 1050px; height: 600px; 
  background: linear-gradient(135deg, rgba(25, 25, 28, 0.85), rgba(10, 10, 12, 0.95)); 
  border: 1px solid $border-glass; display: flex; 
  border-radius: 30px; overflow: hidden; 
  box-shadow: 0 40px 100px rgba(0,0,0,0.9), inset 0 0 20px rgba($gold, 0.05); 
  backdrop-filter: blur(40px); font-family: 'Mulish', sans-serif; color: #fff;
}

/* NÚT CLOSE */
.btn-close-glass {
  position: absolute; top: 25px; right: 25px; 
  background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); 
  color: #fff; width: 45px; height: 45px; border-radius: 50%;
  font-size: 1.2rem; cursor: pointer; transition: 0.4s $ease-bounce; z-index: 20;
  display: flex; align-items: center; justify-content: center; backdrop-filter: blur(10px);
  &:hover { background: #ef4444; border-color: #ef4444; transform: rotate(90deg) scale(1.1); box-shadow: 0 0 20px rgba(239, 68, 68, 0.4);}
}

.modal-body { display: flex; width: 100%; height: 100%; }

/* --- CỘT HÌNH ẢNH TRÁI --- */
.modal-img-col { width: 45%; position: relative; overflow: hidden; background: #000;}
.dish-hero-img { width: 100%; height: 100%; object-fit: cover; opacity: 0.9; }

/* Gradient làm mờ viền ảnh tiếp giáp với chữ */
.img-vignette { 
  position: absolute; inset: 0; 
  background: linear-gradient(to right, transparent 0%, rgba($bg-deep, 0.5) 70%, rgba($bg-deep, 1) 100%),
              linear-gradient(to top, rgba($bg-deep, 1) 0%, transparent 40%); 
}
/* Ánh sáng vàng hắt từ dưới lên */
.img-glow-bottom {
  position: absolute; bottom: -20px; left: 50%; transform: translateX(-50%);
  width: 80%; height: 100px; background: $gold; filter: blur(50px); opacity: 0.2;
}

/* --- CỘT THÔNG TIN PHẢI --- */
.modal-info-col { 
  width: 55%; padding: 50px 60px; display: flex; flex-direction: column; justify-content: center; 
}

.match-badge { 
  font-size: 0.8rem; font-weight: 800; color: $gold; letter-spacing: 3px; 
  margin-bottom: 25px; display: inline-flex; align-items: center; gap: 8px; text-transform: uppercase; 
  background: rgba($gold, 0.08); padding: 8px 20px; border-radius: 30px; border: 1px solid rgba($gold, 0.3);
  box-shadow: 0 0 20px rgba($gold, 0.1);
}

.result-title { 
  font-family: 'Playfair Display', serif; font-size: 4rem; margin: 0 0 20px; color: #fff; 
  line-height: 1.05; letter-spacing: -1px; text-shadow: 0 10px 30px rgba(0,0,0,0.8);
}
.result-desc { 
  font-size: 1.15rem; color: rgba(255,255,255,0.6); line-height: 1.6; margin-bottom: 45px; 
  font-style: italic; border-left: 3px solid $gold; padding-left: 20px; 
}

/* BENTO GRID SIÊU CẤP */
.info-bento-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; margin-bottom: 45px; }
.bento-box {
  background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.05); padding: 18px; border-radius: 20px;
  display: flex; align-items: center; gap: 15px; transition: 0.4s $ease-bounce; cursor: default;
  
  &:hover { background: rgba(255,255,255,0.05); border-color: rgba($gold, 0.3); transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.5);}
  
  .bento-icon { font-size: 1.5rem; color: rgba($gold, 0.7); }
  .bento-text { display: flex; flex-direction: column; gap: 2px;}
  .bento-lbl { font-size: 0.65rem; color: #a1a1aa; font-weight: 800; text-transform: uppercase; letter-spacing: 1px;}
  .bento-val { font-size: 1.1rem; font-weight: 800; color: #fff; font-family: 'Playfair Display', serif; }
}

/* NÚT BẤM (CTA) */
.info-actions { display: flex; gap: 20px; }
.btn-primary-glow {
  flex: 1.5; padding: 18px 30px; background: linear-gradient(135deg, $orange, #f97316); border: none;
  color: #fff; font-weight: 800; text-transform: uppercase; letter-spacing: 2px; font-size: 0.95rem; 
  cursor: pointer; transition: 0.4s $ease-bounce; border-radius: 20px; display: flex; justify-content: center; align-items: center; gap: 12px;
  box-shadow: 0 10px 30px rgba($orange, 0.4); position: relative; overflow: hidden;
  
  /* Hiệu ứng chớp sáng trên nút */
  &::after { content: ''; position: absolute; top: 0; left: -100%; width: 50%; height: 100%; background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent); transform: skewX(-20deg); transition: 0.7s; }
  
  &:hover { transform: translateY(-5px); box-shadow: 0 15px 40px rgba($orange, 0.6); gap: 18px; &::after { left: 150%; } }
}

.btn-outline-glass {
  flex: 1; padding: 18px 20px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.1); 
  color: rgba(255,255,255,0.7); font-weight: 800; font-size: 0.9rem; text-transform: uppercase; 
  letter-spacing: 2px; cursor: pointer; transition: 0.4s $ease-bounce; border-radius: 20px; display: flex; justify-content: center; align-items: center; gap: 10px;
  &:hover { border-color: #fff; color: #fff; background: rgba(255,255,255,0.1); transform: translateY(-5px);}
}

/* =======================================================
   🔥 RESPONSIVE (HOÀN HẢO CHO MỌI THIẾT BỊ)
======================================================= */
@media (max-width: 1200px) {
  .result-title { font-size: 3rem; }
  .modal-info-col { padding: 40px; }
  .bento-box { flex-direction: column; align-items: flex-start; gap: 8px; .bento-icon { font-size: 1.2rem; } }
}

@media (max-width: 992px) {
  .modal-card-glass { height: auto; max-height: 90vh; overflow-y: auto; flex-direction: column; width: 90%; }
  
  .modal-img-col { width: 100%; height: 350px; flex-shrink: 0; }
  .img-vignette { background: linear-gradient(to bottom, transparent, rgba($bg-deep, 1)); }
  .img-glow-bottom { bottom: 0; height: 50px; }
  
  .modal-info-col { width: 100%; padding: 40px; }
  .info-bento-grid { grid-template-columns: repeat(3, 1fr); }
  .bento-box { flex-direction: row; align-items: center; } /* Trả lại dạng ngang trên tablet */
}

@media (max-width: 768px) {
  .modal-info-col { padding: 30px 25px; }
  .result-title { font-size: 2.5rem; margin-bottom: 15px; }
  .result-desc { font-size: 1rem; margin-bottom: 30px; }
  
  .info-bento-grid { grid-template-columns: 1fr; gap: 12px; margin-bottom: 30px; }
  .bento-box { padding: 15px 20px; }
  
  .info-actions { flex-direction: column; gap: 15px; }
  .btn-primary-glow, .btn-outline-glass { width: 100%; justify-content: center; padding: 16px;}
}

@media (max-width: 480px) {
  .modal-img-col { height: 250px; }
  .result-title { font-size: 2.2rem; }
  .btn-close-glass { top: 15px; right: 15px; width: 40px; height: 40px; font-size: 1rem;}
}
</style>