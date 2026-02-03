<template>
  <div class="page-container">
    <h2>💬 Quản Lý Bình Luận</h2>
    <table class="data-table">
      <thead><tr><th>Người dùng</th><th>Nội dung bình luận</th><th>Bài viết</th><th>Hành động</th></tr></thead>
      <tbody>
        <tr v-for="cmt in comments" :key="cmt.id">
          <td><b>{{ cmt.user }}</b></td>
          <td>"{{ cmt.content }}"</td>
          <td><a href="#">#{{ cmt.postId }}</a></td>
          <td><button class="btn-icon trash">🗑️</button></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const comments = ref([
  { id: 1, user: 'Minh An', content: 'Bài viết rất hay!', postId: 101 },
  { id: 2, user: 'Thái Bình', content: 'Code đoạn này bị lỗi rồi admin ơi.', postId: 105 }
])
</script>

<style scoped>
/* Copy CSS từ file trên xuống */
.page-container { padding: 20px; font-family: 'Quicksand', sans-serif; }
.data-table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; margin-top: 15px; }
th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #F1F5F9; }
.btn-icon { width: 30px; height: 30px; border-radius: 50%; border: none; cursor: pointer; }
.trash { background: #FEE2E2; color: #DC2626; }
</style>