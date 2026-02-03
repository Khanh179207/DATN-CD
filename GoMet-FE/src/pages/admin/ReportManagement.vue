<template>
  <div class="page-container">
    <h2 class="page-title">🚨 Trung Tâm Phản Hồi</h2>

    <div class="tabs">
      <button :class="['tab-btn', currentTab === 'violation' ? 'active' : '']" @click="currentTab = 'violation'">
        ⚠️ Báo Cáo Vi Phạm
      </button>
      <button :class="['tab-btn', currentTab === 'feedback' ? 'active' : '']" @click="currentTab = 'feedback'">
        💌 Hòm Thư Góp Ý
      </button>
    </div>

    <div v-if="currentTab === 'violation'" class="tab-content">
      <table class="data-table">
        <thead><tr><th>Đối tượng</th><th>Lý do</th><th>Người báo</th><th>Hành động</th></tr></thead>
        <tbody>
          <tr v-for="item in violations" :key="item.id">
            <td><b>{{ item.target }}</b><br><small>ID: #{{ item.targetId }}</small></td>
            <td><span class="tag red">{{ item.reason }}</span></td>
            <td>{{ item.reporter }}</td>
            <td>
              <button class="btn-sm delete">Xóa bài</button>
              <button class="btn-sm view">Xem chi tiết</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="tab-content">
      <div class="feedback-list">
        <div v-for="fb in feedbacks" :key="fb.id" class="feedback-item">
          <div class="fb-header">
            <span class="fb-user">👤 {{ fb.user }}</span>
            <span class="fb-time">{{ fb.time }}</span>
          </div>
          <h4 class="fb-subject">{{ fb.subject }}</h4>
          <p class="fb-body">{{ fb.content }}</p>
          <div class="fb-actions">
            <button class="btn-reply">Trả lời qua Email</button>
            <button class="btn-done">Đánh dấu đã đọc</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const currentTab = ref('violation')

// Dữ liệu giả
const violations = ref([
  { id: 1, target: 'Bài viết "Cách hack game"', targetId: 99, reason: 'Nội dung cấm', reporter: 'user1' },
  { id: 2, target: 'User: NguyenVanA', targetId: 102, reason: 'Spam comment', reporter: 'user2' }
])

const feedbacks = ref([
  { id: 1, user: 'Minh Tuấn', time: '10:30 AM', subject: 'Lỗi đăng nhập', content: 'Mình không thể đăng nhập bằng Google...' },
  { id: 2, user: 'Lan Anh', time: 'Yesterday', subject: 'Giao diện khó dùng', content: 'Nút bấm trên mobile hơi bé...' }
])

// Tự động chuyển tab nếu URL có ?tab=feedback
onMounted(() => {
  if (route.query.tab === 'feedback') currentTab.value = 'feedback'
})
</script>

<style scoped>
.page-container { padding: 20px; font-family: 'Quicksand', sans-serif; }
.tabs { display: flex; gap: 10px; margin-bottom: 20px; }
.tab-btn { padding: 10px 20px; border: none; background: #E2E8F0; border-radius: 8px; font-weight: 600; cursor: pointer; }
.tab-btn.active { background: #3B82F6; color: white; }
.data-table { width: 100%; border-collapse: collapse; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.data-table th, .data-table td { padding: 15px; text-align: left; border-bottom: 1px solid #F1F5F9; }
.tag.red { background: #FEE2E2; color: #DC2626; padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; font-weight: bold; }
.btn-sm { margin-right: 5px; padding: 5px 10px; border-radius: 4px; border: none; cursor: pointer; }
.delete { background: #FEE2E2; color: #DC2626; }
.view { background: #E0F2FE; color: #0284C7; }

/* Feedback Style */
.feedback-item { background: white; padding: 20px; border-radius: 10px; margin-bottom: 15px; border: 1px solid #E2E8F0; }
.fb-header { display: flex; justify-content: space-between; color: #64748B; font-size: 0.9rem; margin-bottom: 10px; }
.fb-subject { margin: 0 0 10px 0; color: #1E293B; }
.btn-reply { background: #3B82F6; color: white; border: none; padding: 8px 15px; border-radius: 6px; cursor: pointer; margin-right: 10px; }
</style>