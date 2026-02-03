<template>
  <div class="page-container">
    <div class="header-flex">
      <h2>🔔 Thông Báo Hệ Thống</h2>
      <button class="btn-create">+ Tạo Thông Báo</button>
    </div>
    <div class="notif-list">
      <div v-for="n in notifs" :key="n.id" class="notif-item">
        <div class="icon-box">📢</div>
        <div class="content">
          <h4>{{ n.title }}</h4>
          <p>{{ n.desc }}</p>
          <small>{{ n.time }}</small>
        </div>
        <button class="btn-del">Xóa</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const notifs = ref([
  { id: 1, title: 'Bảo trì hệ thống', desc: 'Hệ thống sẽ bảo trì vào 12h đêm nay.', time: '20/02/2026' },
  { id: 2, title: 'Chào mừng năm mới', desc: 'Sự kiện lì xì đầu xuân đã bắt đầu!', time: '01/01/2026' }
])
</script>

<style scoped>
.page-container { padding: 20px; font-family: 'Quicksand', sans-serif; }
.header-flex { display: flex; justify-content: space-between; margin-bottom: 20px; }
.btn-create { background: #3B82F6; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; }
.notif-item { display: flex; gap: 15px; background: white; padding: 15px; border-radius: 10px; margin-bottom: 10px; align-items: center; }
.icon-box { width: 40px; height: 40px; background: #EFF6FF; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.2rem; }
.content { flex: 1; }
.content h4 { margin: 0 0 5px 0; }
.content p { margin: 0; color: #64748B; font-size: 0.9rem; }
.content small { color: #94A3B8; font-size: 0.8rem; }
.btn-del { background: transparent; border: 1px solid #EF4444; color: #EF4444; padding: 5px 10px; border-radius: 4px; cursor: pointer; }
</style>