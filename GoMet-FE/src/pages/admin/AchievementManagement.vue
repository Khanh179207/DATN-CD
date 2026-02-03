<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="title">Quản Lý Thành Tích (Badges)</h2>
      <button class="btn-add" @click="openModal()">+ Thêm Huy Hiệu</button>
    </div>

    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th>Icon</th>
            <th>Tên Thành Tích</th>
            <th>Mô Tả / Điều Kiện</th>
            <th>Điểm Thưởng</th>
            <th>Hành Động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="badge in badges" :key="badge.id">
            <td><img :src="badge.icon" class="badge-icon"></td>
            <td>
              <span class="badge-name">{{ badge.name }}</span>
            </td>
            <td class="desc-cell">{{ badge.description }}</td>
            <td><span class="point-tag">+{{ badge.point }} exp</span></td>
            <td>
              <div class="actions">
                <button @click="openModal(badge)" class="btn-icon edit">✏️</button>
                <button @click="deleteItem(badge.id)" class="btn-icon delete">🗑️</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showModal" class="modal-overlay">
      <div class="modal-box">
        <h3>{{ isEditing ? 'Sửa Thành Tích' : 'Thêm Thành Tích Mới' }}</h3>
        <div class="form-group">
          <label>Tên Huy Hiệu</label>
          <input v-model="form.name" type="text" placeholder="VD: Ong chăm chỉ...">
        </div>
        <div class="form-group">
          <label>Link Icon</label>
          <input v-model="form.icon" type="text" placeholder="https://...">
        </div>
        <div class="form-group">
          <label>Mô tả (Cách đạt được)</label>
          <input v-model="form.description" type="text" placeholder="VD: Đăng 10 bài viết...">
        </div>
        <div class="form-group">
          <label>Điểm thưởng</label>
          <input v-model="form.point" type="number" placeholder="100">
        </div>
        <div class="modal-actions">
          <button @click="showModal = false" class="btn-cancel">Hủy</button>
          <button @click="saveData" class="btn-save">Lưu lại</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const badges = ref([
  { id: 1, name: 'Người Mới Bắt Đầu', description: 'Đăng bài viết đầu tiên', point: 50, icon: 'https://cdn-icons-png.flaticon.com/512/2995/2995458.png' },
  { id: 2, name: 'Đầu Bếp Tài Ba', description: 'Nhận được 100 lượt yêu thích', point: 200, icon: 'https://cdn-icons-png.flaticon.com/512/2995/2995662.png' },
  { id: 3, name: 'Thánh Bình Luận', description: 'Bình luận 50 lần', point: 100, icon: 'https://cdn-icons-png.flaticon.com/512/2995/2995536.png' }
])

const showModal = ref(false)
const isEditing = ref(false)
const form = reactive({ id: null, name: '', description: '', point: 0, icon: '' })

const openModal = (item = null) => {
  showModal.value = true
  if (item) {
    isEditing.value = true
    Object.assign(form, item)
  } else {
    isEditing.value = false
    form.id = Date.now()
    form.name = ''; form.description = ''; form.point = 0; form.icon = ''
  }
}

const saveData = () => {
  if (!form.name) return alert('Vui lòng nhập tên!')
  
  if (isEditing.value) {
    const index = badges.value.findIndex(b => b.id === form.id)
    if (index !== -1) badges.value[index] = { ...form }
  } else {
    badges.value.push({ ...form })
  }
  showModal.value = false
}

const deleteItem = (id) => {
  if(confirm('Xóa huy hiệu này?')) badges.value = badges.value.filter(b => b.id !== id)
}
</script>

<style scoped>
/* Tận dụng lại class chung, chỉ thêm phần riêng cho bảng */
.page-container { padding: 20px; font-family: 'Quicksand', sans-serif; }
.page-header { display: flex; justify-content: space-between; margin-bottom: 24px; }
.title { font-size: 1.5rem; font-weight: 800; color: #1E293B; }
.btn-add { background: #8B5CF6; /* Màu tím cho Achievement */ color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: 700; cursor: pointer; }

.table-wrapper { background: white; border-radius: 12px; border: 1px solid #E2E8F0; overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 16px; text-align: left; border-bottom: 1px solid #F1F5F9; vertical-align: middle; }
.data-table th { background: #F8FAFC; color: #64748B; font-weight: 600; font-size: 0.9rem; }

.badge-icon { width: 40px; height: 40px; object-fit: contain; }
.badge-name { font-weight: 700; color: #334155; }
.desc-cell { color: #64748B; font-size: 0.9rem; }
.point-tag { background: #FFF7ED; color: #EA580C; padding: 4px 8px; border-radius: 6px; font-weight: 600; font-size: 0.8rem; border: 1px solid #FFEDD5; }

.actions { display: flex; gap: 8px; }
.btn-icon { width: 32px; height: 32px; border-radius: 6px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.edit { background: #EFF6FF; color: #3B82F6; }
.delete { background: #FEF2F2; color: #EF4444; }

/* Modal CSS giống file trên */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 50; }
.modal-box { background: white; padding: 24px; border-radius: 16px; width: 400px; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 600; }
.form-group input { width: 100%; padding: 10px; border: 1px solid #CBD5E1; border-radius: 8px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; }
.btn-cancel { background: #F1F5F9; padding: 10px 20px; border-radius: 8px; border: none; cursor: pointer; }
.btn-save { background: #8B5CF6; color: white; padding: 10px 20px; border-radius: 8px; border: none; cursor: pointer; font-weight: 700; }
</style>