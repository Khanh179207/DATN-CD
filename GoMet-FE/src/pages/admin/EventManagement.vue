<template>
  <div class="page-container">
    <div class="header-flex">
      <h2>🎉 Quản Lý Sự Kiện</h2>
      <button class="btn-create">+ Thêm Sự Kiện</button>
    </div>

    <table class="data-table">
      <thead><tr><th>ID</th><th>Tên sự kiện</th><th>Thời gian</th><th>Địa điểm</th><th>Hành động</th></tr></thead>
      <tbody>
        <tr v-for="ev in events" :key="ev.id">
          <td>#{{ ev.id }}</td>
          <td><b>{{ ev.name }}</b></td>
          <td>
            <div class="date-tag">{{ ev.date }}</div>
          </td>
          <td>📍 {{ ev.location }}</td>
          <td>
            <button class="btn-icon edit">✏️</button>
            <button class="btn-icon trash">🗑️</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const events = ref([
  { id: 1, name: 'Cuộc thi Top Chef 2026', date: '20/02/2026', location: 'Hội trường A' },
  { id: 2, name: 'Workshop Coding VueJS', date: '25/02/2026', location: 'Online Zoom' },
  { id: 3, name: 'Gặp mặt đầu xuân', date: '01/03/2026', location: 'Sân vận động' }
])
</script>

<style scoped>
.page-container { padding: 20px; font-family: 'Quicksand', sans-serif; }
.header-flex { display: flex; justify-content: space-between; margin-bottom: 20px; align-items: center; }
.btn-create { background: #F97316; color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: bold; cursor: pointer; }
.data-table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #F1F5F9; }
.date-tag { background: #EFF6FF; color: #3B82F6; padding: 4px 8px; border-radius: 4px; font-weight: 600; font-size: 0.85rem; display: inline-block; }
.btn-icon { width: 30px; height: 30px; border-radius: 50%; border: none; cursor: pointer; margin-right: 5px; }
.edit { background: #E0F2FE; color: #0284C7; }
.trash { background: #FEE2E2; color: #DC2626; }
</style>