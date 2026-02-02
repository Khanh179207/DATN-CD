<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="title">Quản Lý Tài Khoản</h2>
    </div>

    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th>Người dùng</th>
            <th>Email</th>
            <th>Vai trò</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>
              <div class="user-cell">
                <img :src="user.avatar" class="avatar">
                <span class="name">{{ user.name }}</span>
              </div>
            </td>
            <td>{{ user.email }}</td>
            <td>
              <select v-model="user.role" class="role-select">
                <option value="user">Thành viên</option>
                <option value="admin">Quản trị viên</option>
              </select>
            </td>
            <td>
              <span class="status-dot" :class="user.status"></span>
              {{ user.status === 'active' ? 'Hoạt động' : 'Đã khóa' }}
            </td>
            <td>
              <div class="actions">
                <button 
                  @click="toggleStatus(user)" 
                  class="btn-action" 
                  :class="user.status === 'active' ? 'ban' : 'unban'"
                >
                  {{ user.status === 'active' ? '🔒 Khóa' : '🔓 Mở' }}
                </button>
                <button @click="deleteUser(user.id)" class="btn-action delete">🗑️</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const users = ref([
  { id: 1, name: 'Nguyễn Khánh', email: 'khanh@gmail.com', role: 'admin', status: 'active', avatar: 'https://i.pravatar.cc/150?img=11' },
  { id: 2, name: 'Trần Văn B', email: 'btran@gmail.com', role: 'user', status: 'active', avatar: 'https://i.pravatar.cc/150?img=12' },
  { id: 3, name: 'Lê Thị C', email: 'cle@gmail.com', role: 'user', status: 'banned', avatar: 'https://i.pravatar.cc/150?img=5' },
])

const toggleStatus = (user) => {
  user.status = user.status === 'active' ? 'banned' : 'active'
}

const deleteUser = (id) => {
  if(confirm('Hành động này không thể hoàn tác. Xóa user này?')) {
    users.value = users.value.filter(u => u.id !== id)
  }
}
</script>

<style scoped>
/* Kế thừa style chung từ các file trên, chỉ thêm phần riêng */
.page-container { padding: 20px; font-family: 'Quicksand', sans-serif; }
.table-wrapper { background: white; border-radius: 16px; border: 1px solid #F1F5F9; overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 15px; text-align: left; border-bottom: 1px solid #F1F5F9; }

.user-cell { display: flex; align-items: center; gap: 10px; font-weight: 600; color: #334155; }
.avatar { width: 36px; height: 36px; border-radius: 50%; }

.role-select { padding: 5px; border-radius: 6px; border: 1px solid #CBD5E1; }

.status-dot { display: inline-block; width: 8px; height: 8px; border-radius: 50%; margin-right: 5px; }
.status-dot.active { background: #22C55E; }
.status-dot.banned { background: #EF4444; }

.btn-action { border: none; padding: 6px 12px; border-radius: 6px; cursor: pointer; font-size: 0.85rem; font-weight: 600; margin-right: 5px; }
.ban { background: #FFF7ED; color: #C2410C; }
.unban { background: #F0FDF4; color: #15803D; }
.delete { background: #FEF2F2; color: #DC2626; }
</style>