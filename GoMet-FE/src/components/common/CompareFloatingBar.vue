<template>
  <transition name="slide-up">
    <div v-if="compareStore.count > 0" class="compare-bar">
      <div class="bar-content">
        <div class="info">
          <span class="count-badge">{{ compareStore.count }}</span>
          <span class="text">{{ $t('compare.selected') }}</span>
          
          <div class="mini-thumbs">
            <div v-for="item in compareStore.items" :key="item.id" class="thumb" :title="item.title">
              <img :src="item.image">
              <button class="btn-remove-tiny" @click="compareStore.toggleItem(item)">✕</button>
            </div>
          </div>
        </div>

        <div class="actions">
          <button class="btn-clear" @click="compareStore.clearAll">{{ $t('compare.cancel') }}</button>
          <button class="btn-compare-now" @click="goToComparePage">{{ $t('compare.compare_now') }}</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { useCompareStore } from '@/stores/compare'
import { useRouter } from 'vue-router' // 1. Import Router

const compareStore = useCompareStore()
const router = useRouter() // 2. Initialize Router

const goToComparePage = () => {
  // 3. Navigate to the Compare page (defined in router/index.js)
  router.push({ name: 'Compare' })
}
</script>

<style scoped>
/* ─── Floating Bar ─── */
.compare-bar {
  position: fixed;
  bottom: var(--space-8);
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  max-width: 600px;
  z-index: var(--z-toast);
  background: var(--color-neutral-0);
  border-radius: var(--radius-full);
  box-shadow: var(--shadow-2xl);
  border: 1px solid var(--color-neutral-200);
  padding: var(--space-2) var(--space-3) var(--space-2) var(--space-5);
}

.bar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* ─── Info Section ─── */
.info {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.count-badge {
  background: var(--color-neutral-900);
  color: var(--color-neutral-0);
  width: 28px;
  height: 28px;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: var(--font-extrabold);
  font-size: var(--text-sm);
}

.text {
  font-weight: var(--font-bold);
  color: var(--color-neutral-900);
  font-size: var(--text-sm);
}

/* ─── Thumbnails ─── */
.mini-thumbs {
  display: flex;
  margin-left: var(--space-3);
}

.thumb {
  position: relative;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  border: 2px solid var(--color-neutral-0);
  overflow: hidden;
  margin-left: -10px;
  transition: var(--transition-fast);
  cursor: pointer;
}
.thumb:first-child { margin-left: 0; }
.thumb:hover { transform: translateY(-5px); z-index: var(--z-base); }
.thumb img { width: 100%; height: 100%; object-fit: cover; }

.btn-remove-tiny {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  color: var(--color-neutral-0);
  border: none;
  opacity: 0;
  transition: var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-sm);
}
.thumb:hover .btn-remove-tiny { opacity: 1; }

/* ─── Actions ─── */
.actions {
  display: flex;
  gap: var(--space-3);
  align-items: center;
}

.btn-clear {
  background: none;
  border: none;
  color: var(--color-neutral-500);
  font-weight: var(--font-semibold);
  cursor: pointer;
  font-size: var(--text-sm);
  transition: var(--transition-fast);
}
.btn-clear:hover { color: var(--color-error); }

.btn-compare-now {
  background: var(--color-primary-600);
  color: var(--color-neutral-0);
  border: none;
  padding: var(--space-3) var(--space-6);
  border-radius: var(--radius-full);
  font-weight: var(--font-bold);
  cursor: pointer;
  box-shadow: var(--shadow-primary-md);
  transition: var(--transition-base);
}

.btn-compare-now:hover {
  background: var(--color-primary-700);
  transform: translateY(-2px);
}

/* ─── Slide Animation ─── */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all var(--duration-slow) var(--ease-spring);
}
.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translate(-50%, 100%);
}

/* =======================================================
   🔥 HỆ THỐNG RESPONSIVE (TỐI ƯU MỌI THIẾT BỊ)
   ======================================================= */

/* --- 1. Màn hình Tablet dọc & Mobile ngang (Dưới 768px) --- */
@media (max-width: 768px) {
  .compare-bar {
    width: 95%;
    padding: var(--space-2) var(--space-3) var(--space-2) var(--space-4);
    bottom: var(--space-6);
  }
  
  .text {
    display: none; /* Ẩn chữ "Đã chọn" để tiết kiệm diện tích bề ngang */
  }
  
  .thumb {
    width: 36px;
    height: 36px;
  }
  
  .btn-compare-now {
    padding: var(--space-2) var(--space-4);
    font-size: var(--text-sm);
  }
}

/* --- 2. Màn hình Mobile nhỏ (Dưới 480px - Vd: iPhone SE) --- */
@media (max-width: 480px) {
  .compare-bar {
    border-radius: 20px; /* Giảm bo tròn lại cho phù hợp với thiết kế xếp chồng */
    padding: var(--space-3);
    bottom: var(--space-4);
  }
  
  .bar-content {
    flex-direction: column; /* Gập thanh thành 2 dòng */
    gap: var(--space-3);
  }
  
  .info {
    width: 100%;
    justify-content: center; /* Căn giữa số lượng và ảnh trên mobile */
  }
  
  .actions {
    width: 100%;
    gap: var(--space-2);
  }
  
  .btn-clear {
    background: var(--color-neutral-100);
    padding: var(--space-3) var(--space-4);
    border-radius: var(--radius-full);
    flex-shrink: 0;
  }
  
  .btn-compare-now {
    flex: 1; /* Nút "So sánh ngay" kéo dãn ra chiếm phần lớn diện tích để dễ bấm */
    padding: var(--space-3);
  }
}
</style>