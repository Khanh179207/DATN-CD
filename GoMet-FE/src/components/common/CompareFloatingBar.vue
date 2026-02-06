<template>
  <transition name="slide-up">
    <div v-if="compareStore.count > 0" class="compare-bar">
      <div class="bar-content">
        <div class="info">
          <span class="count-badge">{{ compareStore.count }}</span>
          <span class="text">Đang chọn so sánh</span>
          
          <div class="mini-thumbs">
            <div v-for="item in compareStore.items" :key="item.id" class="thumb" :title="item.title">
              <img :src="item.image">
              <button class="btn-remove-tiny" @click="compareStore.toggleItem(item)">✕</button>
            </div>
          </div>
        </div>

        <div class="actions">
          <button class="btn-clear" @click="compareStore.clearAll">Hủy</button>
          <button class="btn-compare-now" @click="goToComparePage">So sánh ngay</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { useCompareStore } from '@/stores/compare'
import { useRouter } from 'vue-router' // 1. Import Router

const compareStore = useCompareStore()
const router = useRouter() // 2. Khởi tạo Router

const goToComparePage = () => {
  // 3. Chuyển hướng sang trang Compare (đã định nghĩa trong router/index.js)
  router.push({ name: 'Compare' })
}
</script>

<style scoped>
.compare-bar {
  position: fixed; bottom: 30px; left: 50%; transform: translateX(-50%);
  width: 90%; max-width: 600px; 
  /* 👇 4. Tăng z-index lên cao nhất để không bị che */
  z-index: 9999 !important; 
  
  background: white; border-radius: 50px;
  box-shadow: 0 10px 40px -5px rgba(0,0,0,0.25);
  border: 1px solid #E5E7EB;
  padding: 8px 12px 8px 20px;
}

.bar-content { display: flex; justify-content: space-between; align-items: center; }

.info { display: flex; align-items: center; gap: 15px; }
.count-badge { 
  background: #1C1917; color: white; width: 28px; height: 28px; 
  border-radius: 50%; display: flex; align-items: center; justify-content: center; 
  font-weight: 800; font-size: 0.9rem; 
}
.text { font-weight: 700; color: #1C1917; font-size: 0.95rem; }

.mini-thumbs { display: flex; gap: -10px; margin-left: 10px; }
.thumb { position: relative; width: 40px; height: 40px; border-radius: 50%; border: 2px solid white; overflow: hidden; margin-left: -10px; transition: 0.2s; cursor: pointer; }
.thumb:first-child { margin-left: 0; }
.thumb:hover { transform: translateY(-5px); z-index: 2; }
.thumb img { width: 100%; height: 100%; object-fit: cover; }
.btn-remove-tiny {
  position: absolute; inset: 0; background: rgba(0,0,0,0.5); color: white; border: none;
  opacity: 0; transition: 0.2s; display: flex; align-items: center; justify-content: center; font-size: 0.8rem;
}
.thumb:hover .btn-remove-tiny { opacity: 1; }

.actions { display: flex; gap: 10px; align-items: center; }
.btn-clear { background: none; border: none; color: #6B7280; font-weight: 600; cursor: pointer; font-size: 0.9rem; }
.btn-clear:hover { color: #EF4444; }

.btn-compare-now {
  background: #EA580C; color: white; border: none; padding: 10px 24px;
  border-radius: 30px; font-weight: 700; cursor: pointer;
  box-shadow: 0 4px 15px rgba(234, 88, 12, 0.3); transition: 0.2s;
}
.btn-compare-now:hover { background: #C2410C; transform: translateY(-2px); }

/* Animation */
.slide-up-enter-active, .slide-up-leave-active { transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1); }
.slide-up-enter-from, .slide-up-leave-to { opacity: 0; transform: translate(-50%, 100%); }
</style>