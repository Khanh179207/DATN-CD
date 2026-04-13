<template>
  <transition name="modal-fade">
    <div v-if="show" class="edit-modal-overlay" @click.self="$emit('close')">
      <div class="edit-modal-card vip-post-modal">
        <div class="edit-modal-header">
          <div class="header-left"><h2>Chỉnh sửa tác phẩm</h2></div>
          <button class="btn-close" @click="$emit('close')">✕</button>
        </div>
        
        <div class="edit-modal-body vip-body">
          <div class="vip-col-left">
            <div class="post-preview-card">
              <div class="preview-img-wrap">
                <img :src="form.mediaPreview" class="preview-img" alt="Preview">
                <div class="img-overlay">
                  <label class="btn-upload-vip">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
                    <span>Đổi ảnh bìa</span>
                    <input type="file" accept="image/*" class="file-hidden" @change="(e) => $emit('media-change', e)">
                  </label>
                </div>
              </div>
              <div class="preview-info">
                <span class="preview-label">Xem trước ảnh đại diện</span>
                <p class="preview-hint">Ảnh đẹp giúp món ăn của bạn thu hút nhiều lượt xem hơn sếp nhé!</p>
              </div>
            </div>
          </div>

          <div class="vip-col-right">
            <div class="edit-field mb-3">
              <label>Tiêu đề món ăn</label>
              <input v-model="form.title" type="text" placeholder="Tên món ăn của bạn" class="premium-input">
            </div>

            <div class="edit-field mb-3">
              <label>Mô tả ngắn</label>
              <textarea v-model="form.description" placeholder="Chia sẻ câu chuyện về món này..." rows="2" class="premium-input"></textarea>
            </div>
            
            <div class="row g-3">
              <div class="col-6">
                <div class="edit-field">
                  <label>Danh mục</label>
                  <select v-model="form.categoryID" class="premium-select">
                    <option v-for="cat in categories" :key="cat.categoryID" :value="cat.categoryID">{{ cat.name }}</option>
                  </select>
                </div>
              </div>
              <div class="col-6">
                <div class="edit-field">
                  <label>Độ khó</label>
                  <select v-model="form.level" class="premium-select">
                    <option :value="1">Dễ</option>
                    <option :value="2">Trung bình</option>
                    <option :value="3">Khó</option>
                  </select>
                </div>
              </div>
            </div>

            <div class="edit-field my-3">
              <label>Thời gian nấu (phút)</label>
              <div class="input-with-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="input-icon"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                <input v-model.number="form.cookingTime" type="number" min="1" class="premium-input ps-5">
              </div>
            </div>

            <div class="edit-field">
              <label>Nguyên liệu</label>
              <textarea v-model="form.ingredients" placeholder="VD: 500g Bột, 2 quả trứng..." rows="3" class="premium-input"></textarea>
            </div>
          </div>
        </div>

        <div class="edit-modal-footer vip-footer">
          <button class="btn-cancel-vip" @click="$emit('close')">Đóng</button>
          <button class="btn-save-vip" :disabled="saving" @click="$emit('save')">
            <span v-if="saving" class="spinner-sm"></span>
            <span v-else>Cập nhật bài viết</span>
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
defineProps({ show: Boolean, form: Object, categories: Array, saving: Boolean })
defineEmits(['close', 'save', 'media-change'])
</script>

<style scoped lang="scss">
.edit-modal-overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.55); backdrop-filter: blur(6px); z-index: 9000; display: flex; align-items: center; justify-content: center; padding: 20px; }
.edit-modal-card { background: #fff; border-radius: 20px; overflow: hidden; }
.vip-post-modal { width: 100%; max-width: 900px; background: rgba(255, 255, 255, 0.94); backdrop-filter: blur(20px) saturate(180%); border: 1px solid rgba(255, 255, 255, 0.3); box-shadow: 0 40px 100px rgba(0, 0, 0, 0.25); }
.edit-modal-header { display: flex; align-items: center; justify-content: space-between; padding: 24px 28px 20px; border-bottom: 1px solid #F1F5F9; h2 { font-size: 1.5rem; margin: 0; color: #1E293B; font-weight: 800; } }
.btn-close { background: #F1F5F9; border: none; width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; color: #64748B; transition: 0.2s; &:hover { background: #E2E8F0; color: #1E293B; } }
.vip-body { display: flex; gap: 30px; padding: 30px 40px; max-height: 75vh; overflow-y: auto; }
.vip-col-left { width: 320px; flex-shrink: 0; }
.vip-col-right { flex: 1; display: flex; flex-direction: column; gap: 12px; }
.post-preview-card { background: #F1F5F9; border-radius: 20px; overflow: hidden; border: 1.5px solid #E2E8F0; transition: 0.3s ease; &:hover { transform: translateY(-4px); box-shadow: 0 12px 25px rgba(0,0,0,0.1); } }
.preview-img-wrap { position: relative; aspect-ratio: 1; background: #CBD5E1; .preview-img { width: 100%; height: 100%; object-fit: cover; } }
.img-overlay { position: absolute; inset: 0; background: rgba(30, 41, 59, 0.4); display: flex; align-items: center; justify-content: center; opacity: 0; transition: opacity 0.3s ease; }
.preview-img-wrap:hover .img-overlay { opacity: 1; }
.btn-upload-vip { background: white; color: #1E293B; padding: 10px 18px; border-radius: 12px; font-weight: 800; font-size: 0.9rem; display: flex; align-items: center; gap: 10px; cursor: pointer; box-shadow: 0 8px 20px rgba(0,0,0,0.2); transition: 0.2s; &:hover { transform: scale(1.05); background: #F8FAFC; } }
.file-hidden { position: absolute; opacity: 0; width: 0; height: 0; pointer-events: none; }
.preview-info { padding: 20px; text-align: center; }
.preview-label { display: block; font-weight: 800; font-size: 0.9rem; color: #1E293B; margin-bottom: 6px; }
.preview-hint { font-size: 0.75rem; color: #64748B; margin: 0; }
.edit-field { display: flex; flex-direction: column; gap: 6px; label { font-size: 0.9rem; font-weight: 700; color: #475569; } }
.premium-input, .premium-select { width: 100%; background: white; border: 1.5px solid #E2E8F0; border-radius: 12px; padding: 12px 18px; font-size: 0.95rem; font-weight: 600; outline: none; transition: 0.2s; &:focus { border-color: #EA580C; box-shadow: 0 0 0 4px rgba(234, 88, 12, 0.1); } }
.input-with-icon { position: relative; .input-icon { position: absolute; left: 18px; top: 50%; transform: translateY(-50%); color: #94A3B8; } .ps-5 { padding-left: 45px; } }
.row { display: flex; flex-wrap: wrap; margin: -6px; } .col-6 { width: 50%; padding: 6px; box-sizing: border-box; }
.vip-footer { padding: 25px 40px; background: #F8FAFC; display: flex; justify-content: flex-end; gap: 12px; border-top: 1px solid #E2E8F0; }
.btn-cancel-vip { border: none; background: transparent; color: #64748B; font-weight: 700; padding: 12px 25px; cursor: pointer; transition: color 0.2s; &:hover { color: #1E293B; } }
.btn-save-vip { background: linear-gradient(135deg, #1E293B, #0F172A); color: white; border: none; padding: 12px 35px; border-radius: 14px; font-weight: 800; font-size: 0.95rem; cursor: pointer; transition: 0.3s; box-shadow: 0 10px 20px rgba(15, 23, 42, 0.15); &:hover:not(:disabled) { background: #EA580C; transform: translateY(-3px); box-shadow: 0 15px 30px rgba(234, 88, 12, 0.3); } &:disabled { opacity: 0.5; cursor: not-allowed; } }
.spinner-sm { width: 16px; height: 16px; border: 2px solid rgba(255,255,255, 0.4); border-top-color: white; border-radius: 50%; animation: spin 0.6s linear infinite; display: inline-block; }
@keyframes spin { to { transform: rotate(360deg); } }

/* =======================================================
   🔥 HỆ THỐNG RESPONSIVE (TỐI ƯU MỌI THIẾT BỊ)
   ======================================================= */

/* --- 1. Màn hình Tablet & Laptop nhỏ (Dưới 1024px) --- */
@media (max-width: 1024px) {
  .vip-body { gap: 20px; padding: 24px; }
  .vip-col-left { width: 280px; } /* Thu nhỏ cột trái lại một chút */
  .edit-modal-header { padding: 20px 24px 16px; }
  .vip-footer { padding: 20px 24px; }
}

/* --- 2. Màn hình Tablet dọc & Mobile ngang (Dưới 900px) --- */
@media (max-width: 900px) { 
  .vip-body { 
    flex-direction: column; 
    align-items: center; 
    max-height: 70vh; /* Giới hạn chiều cao để tránh tràn khi mở bàn phím */
  } 
  .vip-col-left { 
    width: 100%; 
    max-width: 320px; 
    margin: 0 auto; 
  } 
  .vip-col-right { width: 100%; }
}

/* --- 3. Màn hình Mobile lớn (Dưới 600px) --- */
@media (max-width: 600px) {
  .edit-modal-overlay { padding: 12px; }
  .vip-post-modal { border-radius: 16px; }
  
  .edit-modal-header { 
    padding: 16px; 
    h2 { font-size: 1.25rem; }
  }
  .btn-close { width: 30px; height: 30px; }
  
  .vip-body { padding: 16px; gap: 16px; }
  
  .vip-col-left { max-width: 250px; } /* Thu nhỏ ảnh trên điện thoại */
  .preview-info { padding: 12px; }
  .preview-label { font-size: 0.85rem; }
  
  .premium-input, .premium-select { padding: 10px 14px; font-size: 0.9rem; }
  .input-with-icon .ps-5 { padding-left: 38px; }
  .input-with-icon .input-icon { left: 12px; width: 14px; height: 14px; }
  
  /* Nếu màn hình điện thoại chật, chia lưới 50% gập lại thành 100% */
  .row { flex-direction: column; margin: 0; }
  .col-6 { width: 100%; padding: 0; margin-bottom: 12px; }
  
  /* Footer: Hai nút chiếm 50% và dàn đều ngang trên mobile */
  .vip-footer { 
    padding: 16px; 
    justify-content: space-between; 
    gap: 12px;
  }
  .btn-cancel-vip { flex: 1; padding: 12px 10px; background: #F1F5F9; border-radius: 12px; text-align: center; }
  .btn-save-vip { flex: 1; padding: 12px 10px; }
}

/* --- 4. Màn hình Mobile siêu nhỏ (Dưới 400px - Vd: iPhone SE) --- */
@media (max-width: 400px) {
  .vip-col-left { max-width: 200px; }
  .edit-field label { font-size: 0.85rem; }
  .btn-save-vip { font-size: 0.85rem; }
  .btn-cancel-vip { font-size: 0.85rem; }
}

/* --- 5. Xoay ngang màn hình điện thoại (Landscape Mobile) --- */
@media (max-height: 500px) and (orientation: landscape) {
  .vip-body { 
    flex-direction: row; /* Dàn lại 2 cột khi xoay ngang */
    align-items: flex-start;
    max-height: 60vh;
  }
  .vip-col-left { width: 40%; max-width: none; margin: 0; }
  .vip-col-right { width: 60%; }
  
  .post-preview-card {
    /* Ép khung ảnh không chiếm hết chiều dọc khi xoay ngang */
    display: flex; flex-direction: row; align-items: center;
    .preview-img-wrap { width: 120px; flex-shrink: 0; }
    .preview-info { text-align: left; padding: 10px; }
  }
}

/* --- ANIMATION (Giữ nguyên) --- */
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.25s ease; .edit-modal-card { transition: transform 0.25s, opacity 0.25s; } }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; .edit-modal-card { transform: scale(0.94) translateY(16px); opacity: 0; } }
</style>