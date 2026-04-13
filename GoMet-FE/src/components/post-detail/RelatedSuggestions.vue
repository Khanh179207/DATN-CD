<template>
  <aside class="related-sidebar-wrapper">
    <div class="aura-glow top"></div>
    <div class="aura-glow bottom"></div>

    <div class="related-container">
      <div class="sidebar-header">
        <div class="header-badge">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
            <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
          </svg>
          Gợi ý cho bạn
        </div>
        <h3 class="main-title">Khám phá <span class="highlight">vị ngon</span> mới</h3>
      </div>
      
      <div class="list-content-wrapper">
        <div v-if="loading" class="vertical-list">
          <div v-for="n in 3" :key="n" class="skeleton-card-premium">
            <div class="sk-img"></div>
            <div class="sk-body">
              <div class="sk-line title"></div>
              <div class="sk-line meta"></div>
            </div>
          </div>
        </div>

        <template v-else>
          <TransitionGroup 
            name="stagger-list" 
            tag="div" 
            class="vertical-list real-list"
            v-if="items.length > 0"
          >
            <div 
              v-for="(item, index) in items" 
              :key="item.id" 
              class="list-item-wrapper"
              :style="{ '--delay': index * 0.08 + 's' }"
              @click="$emit('click-post', item.id)"
            >
              <RecipeCard :post="item" class="premium-card" />
              <div class="card-shine"></div>
            </div>
          </TransitionGroup>

          <div v-else class="empty-state-luxury">
            <div class="empty-art">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M2 21a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V3a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2v18z"/>
                <path d="M12 8v4M12 16h.01"/>
              </svg>
            </div>
            <p>Đang tìm kiếm món ngon cho sếp...</p>
          </div>
        </template>
        
        <button v-if="items.length > 0 && !loading" class="btn-see-all-luxury" @click="$emit('see-all')">
          <span class="btn-text">Xem tất cả gợi ý</span>
          <div class="icon-circle">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </div>
        </button>

      </div>
    </div>
  </aside>
</template>

<script setup>
import RecipeCard from '@/components/common/RecipeCard.vue'

defineProps({ 
  items: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false }
})

defineEmits(['click-post', 'see-all'])
</script>

<style scoped lang="scss">
.related-sidebar-wrapper { 
  position: relative;
  /* Chuyển sang gradient siêu nhẹ thay vì trắng toát */
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid rgba(241, 245, 249, 0.9);
  border-radius: 36px;
  padding: 36px 28px;
  font-family: var(--font-ui, 'Mulish', sans-serif);
  overflow: hidden;
  box-shadow: 
    0 20px 40px -15px rgba(15, 23, 42, 0.05),
    0 0 0 1px rgba(255, 255, 255, 0.5) inset; /* Inner glow */
  width: 100%;
}

/* Aura trang trí Soft Glow */
.aura-glow {
  position: absolute; width: 250px; height: 250px; 
  filter: blur(90px); opacity: 0.12; z-index: 1; pointer-events: none;
  border-radius: 50%;
  &.top { top: -120px; right: -80px; background: var(--color-primary-500, #ea580c); }
  &.bottom { bottom: -120px; left: -80px; background: #60a5fa; }
}

.related-container { position: relative; z-index: 2; }

/* HEADER */
.sidebar-header { margin-bottom: 36px; }
.header-badge {
  display: inline-flex; align-items: center; gap: 8px;
  background: var(--color-primary-50, #fff4ed);
  color: var(--color-primary-500, #ea580c);
  padding: 8px 16px; border-radius: 100px;
  font-size: 0.8rem; font-weight: 800;
  text-transform: uppercase; letter-spacing: 1.5px;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.08);
}

.main-title { 
  font-family: var(--font-serif, 'Playfair Display', serif); 
  font-size: 2rem; color: #0f172a; margin: 0; line-height: 1.3;
  letter-spacing: -0.5px;
  .highlight {
    color: var(--color-primary-500, #ea580c);
    position: relative;
    display: inline-block;
    &::after {
      content: ''; position: absolute; bottom: 6px; left: -2px; width: calc(100% + 4px); height: 10px;
      background: var(--color-primary-100, #ffedd5); z-index: -1; opacity: 0.6;
      border-radius: 4px;
    }
  }
}

/* LIST & CARD */
.vertical-list { display: flex; flex-direction: column; gap: 28px; }

.list-item-wrapper {
  position: relative; cursor: pointer; border-radius: 24px; 
  transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
  
  &:hover {
    transform: translateY(-6px);
    .premium-card { box-shadow: 0 25px 35px -10px rgba(0, 0, 0, 0.08); border-color: rgba(234, 88, 12, 0.1); }
    .card-shine { opacity: 1; transform: translateX(150%); transition-duration: 0.8s; }
  }
}

.premium-card {
  width: 100%; border: 1px solid transparent; transition: all 0.4s ease;
  border-radius: 24px; background: #fff;
}

/* Tia sáng */
.card-shine {
  position: absolute; top: 0; left: -100%; width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent);
  transform: skewX(-20deg); /* Nghiêng tia sáng cho ngầu */
  transition: 0s; opacity: 0; pointer-events: none; z-index: 3;
}

/* BUTTON XEM TẤT CẢ (Soft Dark Mode) */
.btn-see-all-luxury {
  width: 100%; margin-top: 40px; padding: 8px 8px 8px 32px;
  display: flex; align-items: center; justify-content: space-between;
  background: #1e293b; color: white; border: none;
  border-radius: 100px; cursor: pointer;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 10px 25px -5px rgba(30, 41, 59, 0.3);

  .btn-text { font-weight: 800; font-size: 0.95rem; letter-spacing: 0.5px; }

  .icon-circle {
    width: 44px; height: 44px; border-radius: 50%; background: rgba(255,255,255,0.1);
    display: flex; align-items: center; justify-content: center; 
    transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  }

  &:hover {
    background: var(--color-primary-500, #ea580c);
    transform: translateY(-4px);
    box-shadow: 0 15px 30px -5px rgba(234, 88, 12, 0.4);
    .icon-circle { 
      transform: rotate(45deg) scale(1.1); /* Xoay thuận chiều ngầu hơn */
      background: white; color: var(--color-primary-500); 
    }
  }
}

/* EMPTY STATE */
.empty-state-luxury {
  text-align: center; padding: 70px 0; color: #94a3b8;
  .empty-art { margin-bottom: 24px; color: #cbd5e1; }
  p { font-size: 1rem; font-weight: 700; }
}

/* SKELETON SOFT */
.skeleton-card-premium {
  border-radius: 24px; overflow: hidden; border: 1px solid rgba(241, 245, 249, 0.8);
  background: #fff;
  .sk-img { height: 180px; background: #f1f5f9; position: relative; overflow: hidden;
    &::after { content: ''; position: absolute; inset: 0; background: linear-gradient(90deg, transparent, rgba(255,255,255,0.8), transparent); animation: shimmer 1.8s infinite; }
  }
  .sk-body { padding: 20px; display: flex; flex-direction: column; gap: 10px; }
  .sk-line { height: 14px; background: #f1f5f9; border-radius: 8px; 
    &.title { width: 85%; } &.meta { width: 50%; }
  }
}

@keyframes shimmer { 0% { transform: translateX(-100%); } 100% { transform: translateX(100%); } }

/* STAGGER ANIMATION */
.stagger-list-enter-active {
  transition: all 0.7s cubic-bezier(0.34, 1.56, 0.64, 1);
  transition-delay: var(--delay);
}
.stagger-list-enter-from { opacity: 0; transform: translateY(40px) scale(0.95); }
.stagger-list-enter-to { opacity: 1; transform: translateY(0) scale(1); }

/* =======================================================
   🔥 HỆ THỐNG RESPONSIVE (TỐI ƯU MỌI THIẾT BỊ)
   ======================================================= */

/* --- 1. Màn hình Laptop nhỏ & Tablet ngang (Dưới 1200px) --- */
@media (max-width: 1200px) {
  .related-sidebar-wrapper { padding: 30px 20px; }
  .main-title { font-size: 1.8rem; }
  .vertical-list { gap: 20px; }
}

/* --- 2. Màn hình Tablet dọc & Mobile ngang (Dưới 1024px) --- */
/* Thường ở mốc này, Sidebar sẽ rớt xuống dưới bài viết chính, nên chuyển thành Grid ngang */
@media (max-width: 1024px) {
  .related-sidebar-wrapper {
    border-radius: 24px;
    padding: 30px 24px;
    width: 100%;
  }
  
  .sidebar-header { 
    margin-bottom: 24px; 
    text-align: center; /* Căn giữa tiêu đề cho cân đối */
  }
  .header-badge { margin: 0 auto 16px; }

  .vertical-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr); /* Tách thành 2 cột hiển thị ngang */
    gap: 20px;
  }

  .btn-see-all-luxury { 
    max-width: 400px; 
    margin: 30px auto 0; /* Căn giữa nút */
  }
}

/* --- 3. Màn hình Mobile Lớn (Dưới 768px) --- */
@media (max-width: 768px) {
  .related-sidebar-wrapper {
    padding: 32px 20px;
    border-radius: 0;
    border: none;
    border-top: 1px solid rgba(241, 245, 249, 0.9);
    box-shadow: none;
    background: #fff; /* Trả về nền trắng để hòa nhập với giao diện Mobile */
  }

  .main-title { font-size: 1.6rem; }

  .vertical-list {
    grid-template-columns: 1fr; /* Trở về 1 cột dọc trên Mobile */
    gap: 24px;
  }

  .btn-see-all-luxury {
    padding: 6px 6px 6px 24px;
    max-width: 100%;
  }
}

/* --- 4. Màn hình Mobile Cực Nhỏ (Dưới 480px - Vd: iPhone SE) --- */
@media (max-width: 480px) {
  .related-sidebar-wrapper { padding: 24px 16px; }
  
  .sidebar-header { margin-bottom: 20px; }
  .main-title { font-size: 1.4rem; }
  .header-badge { font-size: 0.75rem; padding: 6px 12px; }

  .vertical-list { gap: 16px; }
  
  .btn-see-all-luxury .btn-text { font-size: 0.9rem; }
}
</style>