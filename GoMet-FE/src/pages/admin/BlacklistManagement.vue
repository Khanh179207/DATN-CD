<template>
  <div class="page-container">
    
    <div class="page-header anim-fade-down">
      <div class="header-content">
        <div class="title-wrapper">
          <div class="icon-box danger-gradient">
            <ShieldAlert :size="24" stroke-width="2.5" />
          </div>
          <div>
            <h2 class="title">Bộ lọc Từ khóa</h2>
            <p class="subtitle">Quản lý danh sách từ ngữ cấm trên toàn hệ thống</p>
          </div>
        </div>
      </div>
    </div>

    <div class="add-word-section anim-fade-up">
      <div class="add-card-lux">
        <div class="input-wrapper">
          <div class="icon-input"><AlertOctagon :size="18" /></div>
          <input 
            v-model="newWord" 
            @keyup.enter="addWord"
            type="text" 
            placeholder="Nhập từ khóa muốn cấm (VD: cờ bạc, 18+...)" 
            :disabled="isAdding"
          />
        </div>
        <button class="btn-lux-add" @click="addWord" :disabled="!newWord.trim() || isAdding">
          <Loader2 v-if="isAdding" :size="18" class="spin-icon" />
          <Plus v-else :size="18" />
          <span>Thêm vào Blacklist</span>
        </button>
      </div>
    </div>

    <div v-if="loading" class="empty-state-lux"><Loader2 :size="32" class="spin-icon" /> <p>Đang tải danh sách...</p></div>
    <div v-else-if="error" class="empty-state-lux error"><AlertTriangle :size="32" /> <p>{{ error }}</p></div>

    <div v-else class="words-grid anim-fade-up" style="--delay: 0.1s">
      <TransitionGroup name="list-anim">
        <div v-for="item in words" :key="item.wordID" class="word-card-lux">
          <div class="word-content">
            <span class="word-text">"{{ item.word }}"</span>
            <small class="word-date">Thêm ngày: {{ formatDate(item.createdAt) }}</small>
          </div>
          <button class="btn-remove" @click="removeWord(item.wordID)" title="Gỡ bỏ lệnh cấm">
            <Trash2 :size="16" />
          </button>
        </div>

        <div v-if="words.length === 0" class="empty-state-lux full-width" key="empty">
          <div class="empty-icon-box success-bg"><ShieldCheck :size="32" /></div>
          <p>Hệ thống hiện tại chưa có từ khóa cấm nào. Môi trường đang rất trong sạch!</p>
        </div>
      </TransitionGroup>
    </div>

    <transition name="toast-anim">
      <div v-if="toast.show" :class="['toast-lux', toast.type]">
        <CheckCircle v-if="toast.type === 'success'" :size="20" />
        <AlertTriangle v-else :size="20" />
        <span>{{ toast.msg }}</span>
      </div>
    </transition>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ShieldAlert, Plus, Trash2, Loader2, AlertTriangle, CheckCircle, AlertOctagon, ShieldCheck } from 'lucide-vue-next'
import api from '@/services/api'

// --- STATE ---
const words = ref([])
const newWord = ref('')
const loading = ref(false)
const isAdding = ref(false)
const error = ref('')

const formatDate = (d) => {
  if (!d) return 'Vừa xong'
  const dt = new Date(d)
  return dt.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

// --- FETCH DATA ---
const fetchWords = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await api.get('/api/admin/blacklist')
    words.value = res.data
  } catch (e) {
    error.value = 'Lỗi tải danh sách từ cấm: ' + (e.response?.data?.message || e.message)
  } finally {
    loading.value = false
  }
}

onMounted(fetchWords)

// --- ACTIONS ---
const addWord = async () => {
  const word = newWord.value.trim()
  if (!word) return
  
  isAdding.value = true
  try {
    const res = await api.post('/api/admin/blacklist', { word: word })
    words.value.unshift(res.data) // Thêm lên đầu danh sách
    newWord.value = ''
    showToast('Đã thêm từ khóa vào Blacklist!')
  } catch (e) {
    showToast('Thêm thất bại (Từ này có thể đã tồn tại).', 'error')
  } finally {
    isAdding.value = false
  }
}

const removeWord = async (id) => {
  if (!confirm('Bạn có chắc muốn gỡ lệnh cấm cho từ khóa này?')) return
  try {
    await api.delete(`/api/admin/blacklist/${id}`)
    words.value = words.value.filter(w => w.wordID !== id)
    showToast('Đã gỡ từ khóa thành công!')
  } catch (e) {
    showToast('Lỗi khi xóa từ khóa.', 'error')
  }
}

// --- TOAST ---
const toast = ref({ show: false, msg: '', type: 'success' })
const showToast = (msg, type = 'success') => {
  toast.value = { show: true, msg, type }
  setTimeout(() => toast.value.show = false, 3000)
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Playfair+Display:wght@800&display=swap');

.page-container { padding: 32px 40px; font-family: 'Inter', sans-serif; background-color: #f8fafc; min-height: 100vh; }

/* HEADER */
.page-header { margin-bottom: 32px; }
.title-wrapper { display: flex; align-items: center; gap: 16px; }
.icon-box.danger-gradient { 
  width: 52px; height: 52px; border-radius: 14px; 
  background: linear-gradient(135deg, #ef4444, #b91c1c); 
  color: white; display: flex; align-items: center; justify-content: center;
  box-shadow: 0 10px 20px -5px rgba(239, 68, 68, 0.4);
}
.title { font-family: 'Playfair Display', serif; font-size: 2.2rem; font-weight: 800; color: #0f172a; margin: 0; letter-spacing: -0.5px; }
.subtitle { color: #64748b; margin: 4px 0 0; font-size: 1rem; font-weight: 500; }

/* ADD WORD SECTION */
.add-word-section { margin-bottom: 40px; }
.add-card-lux { 
  display: flex; gap: 16px; background: white; padding: 20px; 
  border-radius: 20px; box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05); 
  border: 1px solid rgba(0,0,0,0.03); align-items: center;
}
.input-wrapper { flex: 1; position: relative; display: flex; align-items: center; }
.icon-input { position: absolute; left: 16px; color: #ef4444; }
.input-wrapper input { 
  width: 100%; padding: 14px 16px 14px 44px; border: 2px solid #f1f5f9; 
  border-radius: 12px; font-size: 1.05rem; font-weight: 600; color: #0f172a; 
  transition: 0.3s; background: #f8fafc; outline: none;
}
.input-wrapper input:focus { border-color: #fca5a5; background: white; box-shadow: 0 4px 15px rgba(239, 68, 68, 0.1); }
.input-wrapper input::placeholder { font-weight: 500; color: #94a3b8; }

.btn-lux-add { 
  background: linear-gradient(135deg, #0f172a, #1e293b); color: white; border: none; 
  padding: 0 24px; height: 52px; border-radius: 12px; font-weight: 700; font-size: 1rem; 
  display: flex; align-items: center; gap: 8px; cursor: pointer; transition: 0.3s;
  box-shadow: 0 8px 20px -6px rgba(15, 23, 42, 0.4); flex-shrink: 0;
}
.btn-lux-add:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 12px 25px -6px rgba(15, 23, 42, 0.5); }
.btn-lux-add:disabled { opacity: 0.7; cursor: not-allowed; }

/* GRID WORDS */
.words-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
.word-card-lux { 
  background: white; border-radius: 16px; padding: 20px; display: flex; justify-content: space-between; 
  align-items: center; border: 1px solid #fecaca; position: relative; overflow: hidden;
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.05); transition: 0.3s;
}
.word-card-lux::before { content: ''; position: absolute; left: 0; top: 0; bottom: 0; width: 4px; background: #ef4444; }
.word-card-lux:hover { transform: translateY(-4px); box-shadow: 0 10px 25px rgba(239, 68, 68, 0.15); border-color: #f87171; }

.word-content { display: flex; flex-direction: column; gap: 4px; }
.word-text { font-size: 1.2rem; font-weight: 800; color: #7f1d1d; }
.word-date { font-size: 0.85rem; color: #94a3b8; font-weight: 500; }

.btn-remove { 
  width: 36px; height: 36px; border-radius: 10px; background: #fef2f2; color: #ef4444; 
  border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; 
}
.btn-remove:hover { background: #ef4444; color: white; }

/* EMPTY / LOADING */
.empty-state-lux { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 60px 20px; color: #94a3b8; font-weight: 500; }
.empty-state-lux.full-width { grid-column: 1 / -1; }
.empty-icon-box { background: #f1f5f9; padding: 20px; border-radius: 50%; margin-bottom: 16px; color: #cbd5e1; }
.empty-icon-box.success-bg { background: #dcfce7; color: #16a34a; }
.spin-icon { animation: spin 1s linear infinite; }

/* TOAST */
.toast-lux { position: fixed; bottom: 32px; right: 32px; padding: 16px 24px; border-radius: 16px; font-weight: 600; z-index: 10000; box-shadow: 0 20px 40px -10px rgba(0,0,0,0.2); display: flex; align-items: center; gap: 12px; font-size: 1rem; color: white; }
.toast-lux.success { background: #1f2937; border-left: 4px solid #10b981; }
.toast-lux.error { background: #7f1d1d; border-left: 4px solid #ef4444; }

/* ANIMATIONS */
.anim-fade-down { animation: fadeDown 0.6s cubic-bezier(0.16, 1, 0.3, 1); }
.anim-fade-up { animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) both; animation-delay: var(--delay, 0s); }
@keyframes fadeDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes spin { 100% { transform: rotate(360deg); } }

.list-anim-enter-active, .list-anim-leave-active { transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); }
.list-anim-enter-from, .list-anim-leave-to { opacity: 0; transform: scale(0.9); }
.toast-anim-enter-active, .toast-anim-leave-active { transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); }
.toast-anim-enter-from, .toast-anim-leave-to { opacity: 0; transform: translateY(30px) scale(0.9); }
</style>