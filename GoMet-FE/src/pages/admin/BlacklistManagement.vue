<template>
  <div class="page-container">
    
    <div class="page-header anim-fade-down">
      <div class="header-content">
        <div class="title-wrapper">
          <div class="icon-box danger-gradient">
            <ShieldAlert :size="28" stroke-width="2.5" />
          </div>
          <div class="header-text">
            <h2 class="title">Bộ lọc Từ khóa</h2>
            <p class="subtitle">Quản lý danh sách từ ngữ cấm trên toàn hệ thống GoMet</p>
          </div>
        </div>
        <div class="stats-badge" v-if="!loading && !error">
          <span class="stats-number">{{ words.length }}</span>
          <span class="stats-label">Từ khóa</span>
        </div>
      </div>
    </div>

    <div class="add-word-section anim-fade-up">
      <div class="add-card-lux">
        <div class="input-wrapper">
          <div class="icon-input"><AlertOctagon :size="20" stroke-width="2.5" /></div>
          <input 
            v-model="newWord" 
            @keyup.enter="addWord"
            type="text" 
            placeholder="Nhập từ khóa muốn cấm (VD: cờ bạc, 18+, vcl...)" 
            :disabled="isAdding"
            autocomplete="off"
          />
        </div>
        <button class="btn-lux-add" @click="addWord" :disabled="!newWord.trim() || isAdding">
          <Loader2 v-if="isAdding" :size="20" class="spin-icon" />
          <Plus v-else :size="20" stroke-width="3" />
          <span>Thêm vào Blacklist</span>
        </button>
      </div>
    </div>

    <div v-if="loading" class="empty-state-lux"><Loader2 :size="40" class="spin-icon text-orange" /> <p>Đang tải bộ lọc từ khóa...</p></div>
    <div v-else-if="error" class="empty-state-lux error"><AlertTriangle :size="40" /> <p>{{ error }}</p></div>

    <div v-else class="words-grid anim-fade-up" style="--delay: 0.1s">
      <TransitionGroup name="list-anim">
        <div v-for="item in words" :key="item.id" class="word-card-lux">
          <div class="word-content">
            <span class="word-text">"{{ item.word }}"</span>
            <small class="word-date">Đã thêm: {{ formatDate(item.createdAt) }}</small>
          </div>
          <button class="btn-remove" @click="removeWord(item.id)" title="Gỡ bỏ lệnh cấm">
            <Trash2 :size="18" stroke-width="2.5" />
          </button>
        </div>

        <div v-if="words.length === 0" class="empty-state-lux full-width" key="empty">
          <div class="empty-icon-box success-bg"><ShieldCheck :size="48" stroke-width="2" /></div>
          <h3 class="empty-title">Môi trường trong sạch!</h3>
          <p class="empty-desc">Hệ thống hiện tại chưa có từ khóa cấm nào.</p>
        </div>
      </TransitionGroup>
    </div>

    <transition name="toast-anim">
      <div v-if="toast.show" :class="['toast-lux', toast.type]">
        <CheckCircle v-if="toast.type === 'success'" :size="24" stroke-width="2.5" />
        <AlertTriangle v-else :size="24" stroke-width="2.5" />
        <div class="toast-content">
          <span class="toast-title">{{ toast.type === 'success' ? 'Thành công' : 'Thất bại' }}</span>
          <span class="toast-desc">{{ toast.msg }}</span>
        </div>
      </div>
    </transition>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ShieldAlert, Plus, Trash2, Loader2, AlertTriangle, CheckCircle, AlertOctagon, ShieldCheck } from 'lucide-vue-next'
import api from '@/services/api' // Sếp nhớ đảm bảo import này đúng đường dẫn axios của sếp nhé

// --- STATE ---
const words = ref([])
const newWord = ref('')
const loading = ref(false)
const isAdding = ref(false)
const error = ref('')

const formatDate = (d) => {
  if (!d) return 'Vừa xong'
  const dt = new Date(d)
  return dt.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

// --- LẤY DANH SÁCH TỪ KHÓA TỪ BACKEND ---
const fetchWords = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await api.get('/api/admin/blacklist')
    // Sắp xếp ID mới nhất lên đầu nếu backend chưa sort
    words.value = res.data.sort((a, b) => b.id - a.id)
  } catch (e) {
    error.value = 'Lỗi tải danh sách từ cấm: ' + (e.response?.data?.message || e.message)
  } finally {
    loading.value = false
  }
}

onMounted(fetchWords)

// --- THÊM TỪ KHÓA MỚI ---
const addWord = async () => {
  const word = newWord.value.trim()
  if (!word) return
  
  isAdding.value = true
  try {
    const res = await api.post('/api/admin/blacklist', { word: word })
    words.value.unshift(res.data) // Đẩy từ mới lên đầu danh sách ngay lập tức
    newWord.value = ''
    showToast('Đã thêm từ khóa vào Blacklist!')
  } catch (e) {
    showToast(e.response?.data?.message || 'Từ khóa này đã tồn tại!', 'error')
  } finally {
    isAdding.value = false
  }
}

// --- XÓA TỪ KHÓA ---
const removeWord = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn gỡ lệnh cấm cho từ khóa này? Hành động này sẽ cho phép người dùng bình luận từ này.')) return
  try {
    await api.delete(`/api/admin/blacklist/${id}`)
    // 🔥 Đã sửa w.wordID thành w.id
    words.value = words.value.filter(w => w.id !== id)
    showToast('Đã gỡ từ khóa thành công!')
  } catch (e) {
    showToast(e.response?.data?.message || 'Lỗi khi xóa từ khóa.', 'error')
  }
}

// --- HỆ THỐNG TOAST ---
const toast = ref({ show: false, msg: '', type: 'success' })
const showToast = (msg, type = 'success') => {
  toast.value = { show: true, msg, type }
  setTimeout(() => toast.value.show = false, 4000) // Kéo dài thời gian hiện Toast lên 4s
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Playfair+Display:wght@800&display=swap');

.page-container { 
  padding: 40px 50px; 
  font-family: 'Inter', sans-serif; 
  background-color: #f8fafc; 
  min-height: 100vh; 
}

/* ================= HEADER ================= */
.page-header { margin-bottom: 40px; }
.header-content { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 20px; }
.title-wrapper { display: flex; align-items: center; gap: 20px; }

.icon-box.danger-gradient { 
  width: 60px; height: 60px; border-radius: 16px; 
  background: linear-gradient(135deg, #ef4444, #991b1b); 
  color: white; display: flex; align-items: center; justify-content: center;
  box-shadow: 0 15px 25px -5px rgba(239, 68, 68, 0.4);
}

.header-text { display: flex; flex-direction: column; }
.title { font-family: 'Playfair Display', serif; font-size: 2.5rem; font-weight: 800; color: #0f172a; margin: 0; letter-spacing: -0.5px; }
.subtitle { color: #64748b; margin: 6px 0 0; font-size: 1.05rem; font-weight: 500; }

.stats-badge {
  background: white; padding: 12px 24px; border-radius: 100px;
  display: flex; align-items: center; gap: 10px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05); border: 1px solid #f1f5f9;
}
.stats-number { font-size: 1.5rem; font-weight: 900; color: #ea580c; line-height: 1; }
.stats-label { font-size: 0.95rem; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: 1px; }

/* ================= ADD SECTION ================= */
.add-word-section { margin-bottom: 50px; }
.add-card-lux { 
  display: flex; gap: 16px; background: white; padding: 12px 12px 12px 24px; 
  border-radius: 100px; box-shadow: 0 15px 35px -10px rgba(0,0,0,0.08); 
  border: 1px solid rgba(0,0,0,0.03); align-items: center; transition: all 0.3s ease;
}
.add-card-lux:focus-within {
  box-shadow: 0 20px 40px -10px rgba(234, 88, 12, 0.15);
  border-color: rgba(234, 88, 12, 0.3);
}

.input-wrapper { flex: 1; position: relative; display: flex; align-items: center; }
.icon-input { position: absolute; left: 0; color: #ef4444; }
.input-wrapper input { 
  width: 100%; padding: 16px 16px 16px 36px; border: none; 
  font-size: 1.1rem; font-weight: 600; color: #0f172a; 
  background: transparent; outline: none;
}
.input-wrapper input::placeholder { font-weight: 500; color: #94a3b8; }

.btn-lux-add { 
  background: linear-gradient(135deg, #0f172a, #1e293b); color: white; border: none; 
  padding: 0 32px; height: 56px; border-radius: 100px; font-weight: 800; font-size: 1.05rem; 
  display: flex; align-items: center; gap: 10px; cursor: pointer; transition: 0.3s;
  box-shadow: 0 8px 20px -6px rgba(15, 23, 42, 0.5); flex-shrink: 0;
}
.btn-lux-add:hover:not(:disabled) { transform: translateY(-2px); background: linear-gradient(135deg, #ea580c, #c2410c); box-shadow: 0 12px 25px -6px rgba(234, 88, 12, 0.5); }
.btn-lux-add:disabled { opacity: 0.6; cursor: not-allowed; }

/* ================= GRID WORDS ================= */
.words-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 24px; }
.word-card-lux { 
  background: white; border-radius: 20px; padding: 24px; display: flex; justify-content: space-between; 
  align-items: center; border: 1px solid #fecaca; position: relative; overflow: hidden;
  box-shadow: 0 4px 20px rgba(239, 68, 68, 0.05); transition: 0.3s;
}
.word-card-lux::before { content: ''; position: absolute; left: 0; top: 0; bottom: 0; width: 6px; background: #ef4444; border-radius: 6px 0 0 6px; }
.word-card-lux:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(239, 68, 68, 0.15); border-color: #f87171; }

.word-content { display: flex; flex-direction: column; gap: 6px; }
.word-text { font-size: 1.3rem; font-weight: 900; color: #7f1d1d; letter-spacing: -0.5px; }
.word-date { font-size: 0.85rem; color: #64748b; font-weight: 600; }

.btn-remove { 
  width: 44px; height: 44px; border-radius: 14px; background: #fef2f2; color: #ef4444; 
  border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; 
}
.btn-remove:hover { background: #ef4444; color: white; transform: scale(1.05) rotate(5deg); }

/* ================= EMPTY / LOADING ================= */
.empty-state-lux { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 80px 20px; color: #64748b; font-weight: 600; text-align: center; }
.empty-state-lux.full-width { grid-column: 1 / -1; }
.empty-icon-box { background: #f1f5f9; padding: 24px; border-radius: 50%; margin-bottom: 24px; color: #cbd5e1; }
.empty-icon-box.success-bg { background: #dcfce7; color: #16a34a; box-shadow: 0 20px 40px -10px rgba(22, 163, 74, 0.2); }
.empty-title { font-size: 1.8rem; font-weight: 800; color: #0f172a; margin: 0 0 8px; font-family: 'Playfair Display', serif; }
.empty-desc { font-size: 1.1rem; color: #64748b; margin: 0; }
.text-orange { color: #ea580c; }
.spin-icon { animation: spin 1s linear infinite; }

/* ================= TOAST NOTIFICATION ================= */
.toast-lux { 
  position: fixed; bottom: 40px; right: 40px; padding: 20px 28px; 
  border-radius: 20px; z-index: 10000; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.3); 
  display: flex; align-items: center; gap: 16px; color: white; 
}
.toast-content { display: flex; flex-direction: column; }
.toast-title { font-weight: 800; font-size: 1.05rem; line-height: 1.2; }
.toast-desc { font-weight: 500; font-size: 0.95rem; opacity: 0.9; margin-top: 4px; }

.toast-lux.success { background: #0f172a; border-left: 6px solid #10b981; }
.toast-lux.error { background: #7f1d1d; border-left: 6px solid #ef4444; }

/* ================= ANIMATIONS ================= */
.anim-fade-down { animation: fadeDown 0.6s cubic-bezier(0.16, 1, 0.3, 1); }
.anim-fade-up { animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) both; animation-delay: var(--delay, 0s); }
@keyframes fadeDown { from { opacity: 0; transform: translateY(-30px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
@keyframes spin { 100% { transform: rotate(360deg); } }

.list-anim-enter-active, .list-anim-leave-active { transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.list-anim-enter-from, .list-anim-leave-to { opacity: 0; transform: scale(0.9) translateY(20px); }
.list-anim-leave-active { position: absolute; } /* Giúp các item khác trượt lên mượt mà khi xóa */

.toast-anim-enter-active, .toast-anim-leave-active { transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.toast-anim-enter-from, .toast-anim-leave-to { opacity: 0; transform: translateY(40px) scale(0.9); }
</style>