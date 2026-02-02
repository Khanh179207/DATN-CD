<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="title">Quản Lý Bài Đăng</h2>
      <div class="tabs">
        <button class="tab-btn" :class="{ active: currentTab === 'pending' }" @click="currentTab = 'pending'">⏳ Chờ Duyệt</button>
        <button class="tab-btn" :class="{ active: currentTab === 'active' }" @click="currentTab = 'active'">✅ Đã Duyệt</button>
      </div>
    </div>

    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th>Bài viết</th>
            <th>Tác giả</th>
            <th>Ngày đăng</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="post in filteredPosts" :key="post.id">
            <td>
              <div class="post-info">
                <img :src="post.image" class="thumb">
                <div>
                  <div class="post-title">{{ post.title }}</div>
                  <div class="post-desc">{{ post.desc }}</div>
                </div>
              </div>
            </td>
            <td>
              <div class="author">
                <img :src="post.avatar" class="avatar-sm">
                <span>{{ post.author }}</span>
              </div>
            </td>
            <td>{{ post.date }}</td>
            <td>
              <span class="status-badge" :class="post.status">
                {{ post.status === 'pending' ? 'Chờ duyệt' : 'Hiển thị' }}
              </span>
            </td>
            <td>
              <div class="actions">
                <button class="btn-icon view" title="Xem chi tiết">👁️</button>
                
                <button v-if="post.status === 'pending'" @click="approvePost(post.id)" class="btn-icon approve" title="Duyệt">✓</button>
                
                <button @click="deletePost(post.id)" class="btn-icon delete" title="Xóa">✕</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const currentTab = ref('pending')

// Dữ liệu giả lập
const posts = ref([
  { id: 1, title: 'Cách làm Sườn Xào Chua Ngọt', desc: 'Công thức gia truyền...', image: 'https://source.unsplash.com/random/100x100?food=1', author: 'Nguyễn Văn A', avatar: 'https://i.pravatar.cc/150?img=1', date: '02/02/2026', status: 'pending' },
  { id: 2, title: 'Bánh Mì Chảo Hà Nội', desc: 'Ăn là nghiền...', image: 'https://source.unsplash.com/random/100x100?food=2', author: 'Trần Thị B', avatar: 'https://i.pravatar.cc/150?img=2', date: '01/02/2026', status: 'active' },
  { id: 3, title: 'Canh Chua Cá Lóc', desc: 'Đậm đà miền Tây...', image: 'https://source.unsplash.com/random/100x100?food=3', author: 'Lê C', avatar: 'https://i.pravatar.cc/150?img=3', date: '02/02/2026', status: 'pending' },
])

// Lọc bài viết theo Tab
const filteredPosts = computed(() => posts.value.filter(p => p.status === currentTab.value))

// Hành động Duyệt
const approvePost = (id) => {
  const post = posts.value.find(p => p.id === id)
  if (post) post.status = 'active'
}

// Hành động Xóa
const deletePost = (id) => {
  if(confirm('Bạn có chắc chắn muốn xóa bài viết này?')) {
    posts.value = posts.value.filter(p => p.id !== id)
  }
}
</script>

<style scoped>
/* CSS chung cho Admin (Dùng lại cho các trang sau) */
.page-container { padding: 20px; font-family: 'Quicksand', sans-serif; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.title { font-size: 1.5rem; font-weight: 800; color: #1E293B; margin: 0; }

.tabs { display: flex; gap: 10px; background: white; padding: 5px; border-radius: 10px; border: 1px solid #E2E8F0; }
.tab-btn { border: none; background: none; padding: 8px 16px; font-weight: 700; color: #64748B; cursor: pointer; border-radius: 6px; transition: 0.2s; }
.tab-btn.active { background: #F97316; color: white; }

.table-wrapper { background: white; border-radius: 16px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); overflow: hidden; border: 1px solid #F1F5F9; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { background: #F8FAFC; text-align: left; padding: 16px; font-size: 0.85rem; color: #475569; border-bottom: 2px solid #E2E8F0; }
.data-table td { padding: 16px; border-bottom: 1px solid #F1F5F9; vertical-align: middle; }

.post-info { display: flex; gap: 12px; align-items: center; }
.thumb { width: 50px; height: 50px; border-radius: 8px; object-fit: cover; }
.post-title { font-weight: 700; color: #334155; }
.post-desc { font-size: 0.8rem; color: #94A3B8; }

.author { display: flex; align-items: center; gap: 8px; font-weight: 600; color: #475569; font-size: 0.9rem; }
.avatar-sm { width: 28px; height: 28px; border-radius: 50%; }

.status-badge { padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; }
.status-badge.pending { background: #FFF7ED; color: #C2410C; }
.status-badge.active { background: #F0FDF4; color: #15803D; }

.actions { display: flex; gap: 8px; }
.btn-icon { width: 32px; height: 32px; border-radius: 8px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.view { background: #F1F5F9; color: #334155; }
.approve { background: #DCFCE7; color: #16A34A; }
.delete { background: #FEE2E2; color: #DC2626; }
</style>