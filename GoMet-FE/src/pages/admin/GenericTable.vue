<template>
  <div class="page-container">
    <div class="header-flex">
      <h2>📂 Quản Lý Danh Mục</h2>
      <button class="btn-create">+ Thêm Mới</button>
    </div>

    <table class="data-table">
      <thead><tr><th>ID</th><th>Tên Danh Mục</th><th>Số bài viết</th><th>Hành động</th></tr></thead>
      <tbody>
        <tr v-for="item in items" :key="item.id">
          <td>{{ item.id }}</td>
          <td><b>{{ item.name }}</b></td>
          <td>{{ item.count }} bài</td>
          <td>
            <button class="btn-sm edit">Sửa</button>
            <button class="btn-sm delete">Xóa</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const items = ref([
  { id: 1, name: 'Ẩm thực', count: 120 },
  { id: 2, name: 'Du lịch', count: 85 },
  { id: 3, name: 'Công nghệ', count: 45 }
])
</script>

<style scoped>
.page-container { padding: 20px; font-family: 'Quicksand', sans-serif; }
.header-flex { display: flex; justify-content: space-between; margin-bottom: 20px; }
.btn-create { background: #3B82F6; color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: bold; cursor: pointer; }
.data-table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #F1F5F9; }
.btn-sm { padding: 5px 10px; border: none; border-radius: 4px; cursor: pointer; margin-right: 5px; }
.edit { background: #E0F2FE; color: #0284C7; }
.delete { background: #FEE2E2; color: #DC2626; }
</style>