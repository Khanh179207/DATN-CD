<template>
  <div class="review-summary-luxury">
    <div class="score-box">
      <div class="score-glow"></div>
      <span class="big-score">{{ formattedRating }}</span>
      <div class="stars-display">
        <span 
          v-for="n in 5" 
          :key="n" 
          :class="['s-icon', { active: n <= Math.round(Number(avgRating || 0)) }]"
        >★</span>
      </div>
      <span class="total-reviews">{{ totalRatings }} {{ $t('post.ratings') || 'ĐÁNH GIÁ' }}</span>
    </div>
    
    <div class="rating-bars">
      <div class="bar-row" v-for="i in 5" :key="i">
        <span class="star-label">
          {{ 6 - i }} 
          <svg class="s" width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
        </span>
        <div class="progress-bg">
          <div 
            class="progress-fill" 
            :style="{ width: `${ratingDistribution[i - 1] || 0}%` }"
          ></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  avgRating: { 
    type: [Number, String], 
    required: true 
  },
  totalRatings: { 
    type: Number, 
    required: true 
  },
  ratingDistribution: { 
    type: Array, 
    required: true 
  }
})

// Đảm bảo luôn hiển thị 1 chữ số thập phân (VD: 4.0 thay vì 4)
const formattedRating = computed(() => {
  const rating = Number(props.avgRating) || 0;
  return rating > 0 ? rating.toFixed(1) : '0.0';
})
</script>

<style scoped lang="scss">

.review-summary-luxury { 
  display: flex; 
  align-items: center; 
  gap: 48px; 
  background: linear-gradient(145deg, #ffffff, #fafafa); 
  padding: 36px 40px; 
  border-radius: var(--radius-xl, 24px); 
  border: 1px solid rgba(234, 88, 12, 0.08); 
  margin-bottom: 32px; 
  box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05); 
  position: relative;
  overflow: hidden;
}

/* --- PHẦN ĐIỂM SỐ (TRÁI) --- */
.score-box { 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  min-width: 150px; 
  position: relative;
  border-right: 1px solid rgba(226, 232, 240, 0.8); 
  padding-right: 48px; 
  z-index: 2;
}

/* Ánh sáng mờ phía sau điểm số (tạo chiều sâu) */
.score-glow {
  position: absolute;
  top: 50%; left: 40%;
  transform: translate(-50%, -50%);
  width: 80px; height: 80px;
  background: var(--color-primary-500, #ea580c);
  filter: blur(40px);
  opacity: 0.15;
  z-index: -1;
  border-radius: 50%;
}

.big-score { 
  font-family: var(--font-serif, 'Playfair Display', serif);
  font-size: 4.5rem; 
  font-weight: 900; 
  /* 1. Tăng line-height lên 1 chút để không bị ép sát */
  line-height: 1.1; 
  
  /* 2. Thêm khoảng không trên dưới để chữ không bị cắt */
  padding: 10px 0; 
  
  /* 3. Phải có display inline-block thì padding mới có tác dụng giữ khung */
  display: inline-block; 
  
  background: linear-gradient(135deg, #ea580c, #f59e0b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -1px;
}
.stars-display { 
  display: flex; 
  gap: 4px; 
  font-size: 1.3rem; 
  margin: 12px 0 8px; 

  .s-icon { 
    color: #e2e8f0; 
  } 
  .s-icon.active { 
    color: #f59e0b; 
    filter: drop-shadow(0 2px 4px rgba(245, 158, 11, 0.3));
  } 
}

.total-reviews { 
  font-size: 0.8rem; 
  color: #64748b; 
  font-weight: 800; 
  text-transform: uppercase; 
  letter-spacing: 1.5px; 
}

/* --- PHẦN BAR PROGRESS (PHẢI) --- */
.rating-bars { 
  flex: 1; 
  display: flex; 
  flex-direction: column; 
  gap: 12px; 
}

.bar-row { 
  display: flex; 
  align-items: center; 
  gap: 16px; 
}

.star-label { 
  width: 40px; 
  font-weight: 800; 
  display: flex; 
  align-items: center; 
  justify-content: flex-end; 
  gap: 6px; 
  font-size: 0.95rem; 
  color: #334155; 

  .s { 
    color: #f59e0b; 
  } 
}

.progress-bg { 
  flex: 1; 
  height: 8px; 
  background: #f1f5f9; 
  border-radius: 10px; 
  overflow: hidden; 
  box-shadow: inset 0 1px 2px rgba(0,0,0,0.05);
}

.progress-fill { 
  height: 100%; 
  background: linear-gradient(90deg, #f59e0b, #ea580c); 
  border-radius: 10px; 
  /* Chuyển động width trơn tru, không nhảy giật */
  transition: width 0.4s ease-out; 
  box-shadow: 0 0 10px rgba(234, 88, 12, 0.4);
}

/* --- RESPONSIVE MOBILE --- */
@media (max-width: 768px) {
  .review-summary-luxury { 
    flex-direction: column; 
    gap: 24px; 
    padding: 24px; 
  }
  .score-box { 
    border-right: none; 
    border-bottom: 1px solid rgba(226, 232, 240, 0.8); 
    padding-right: 0; 
    padding-bottom: 24px; 
    width: 100%; 
  }
  .score-glow { left: 50%; }
  .rating-bars { width: 100%; }
}
</style>