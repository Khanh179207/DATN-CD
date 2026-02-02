<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="title">Quản Lý Danh Mục</h2>
      <button class="btn-add" @click="openModal()">+ Thêm Danh Mục</button>
    </div>

    <div class="grid-list">
      <div class="item-card" v-for="cat in categories" :key="cat.id">
        <img :src="cat.image" class="cat-img">
        <div class="cat-info">
          <h3>{{ cat.name }}</h3>
          <p>{{ cat.count }} bài viết</p>
        </div>
        <div class="card-actions">
          <button @click="openModal(cat)" class="btn-action edit">✏️</button>
          <button @click="deleteCat(cat.id)" class="btn-action delete">🗑️</button>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay">
      <div class="modal-box">
        <h3>{{ isEditing ? 'Chỉnh Sửa Danh Mục' : 'Thêm Danh Mục Mới' }}</h3>
        <div class="form-group">
          <label>Tên danh mục</label>
          <input v-model="form.name" type="text" placeholder="Nhập tên...">
        </div>
        <div class="form-group">
          <label>Link Ảnh</label>
          <input v-model="form.image" type="text" placeholder="URL hình ảnh...">
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

const categories = ref([
  { id: 1, name: 'Món Ngon', count: 120, image: 'https://source.unsplash.com/random/200x150?food' },
  { id: 2, name: 'Làm Bánh', count: 85, image: 'https://source.unsplash.com/random/200x150?cake' },
  { id: 3, name: 'Healthy', count: 45, image: 'https://source.unsplash.com/random/200x150?salad' },
])

const showModal = ref(false)
const isEditing = ref(false)
const form = reactive({ id: null, name: '', image: '' })

// Mở Modal (Nếu có item truyền vào là Sửa, không là Thêm)
const openModal = (item = null) => {
  showModal.value = true
  if (item) {
    isEditing.value = true
    Object.assign(form, item)
  } else {
    isEditing.value = false
    form.id = Date.now()
    form.name = ''
    form.image = ''
  }
}

// Lưu dữ liệu
const saveData = () => {
  if (isEditing.value) {
    const index = categories.value.findIndex(c => c.id === form.id)
    categories.value[index] = { ...form }
  } else {
    categories.value.push({ ...form, count: 0 })
  }
  showModal.value = false
}

// Xóa
const deleteCat = (id) => {
  if(confirm('Xóa danh mục này?')) categories.value = categories.value.filter(c => c.id !== id)
}
</script>

<style scoped>
/* CSS Page Container giống trên */
.page-container { padding: 20px; font-family: 'Quicksand', sans-serif; }
.page-header { display: flex; justify-content: space-between; margin-bottom: 24px; }
.title { font-size: 1.5rem; font-weight: 800; color: #1E293B; }
.btn-add { background: #F97316; color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: 700; cursor: pointer; }

/* Grid Card */
.grid-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 20px; }
.item-card { background: white; border-radius: 16px; overflow: hidden; border: 1px solid #F1F5F9; position: relative; }
.cat-img { width: 100%; height: 120px; object-fit: cover; }
.cat-info { padding: 15px; }
.cat-info h3 { margin: 0; font-size: 1.1rem; color: #334155; }
.cat-info p { color: #94A3B8; font-size: 0.85rem; margin-top: 5px; }

.card-actions { position: absolute; top: 10px; right: 10px; display: flex; gap: 5px; }
.btn-action { width: 30px; height: 30px; border-radius: 50%; border: none; cursor: pointer; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.btn-action:hover { transform: scale(1.1); }

/* Modal Styles */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-box { background: white; padding: 30px; border-radius: 20px; width: 400px; }
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 5px; font-weight: 600; }
.form-group input { width: 100%; padding: 10px; border: 1px solid #E2E8F0; border-radius: 8px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
.btn-cancel { background: #F1F5F9; border: none; padding: 10px 20px; border-radius: 8px; cursor: pointer; }
.btn-save { background: #F97316; color: white; border: none; padding: 10px 20px; border-radius: 8px; cursor: pointer; font-weight: 700; }
</style>