<template>
  <div class="gomet-planner-pro">
    <div class="bg-layer">
      <div class="grid-mesh"></div>
      <div class="noise-texture"></div>
    </div>

    <div class="planner-wrapper">
      <aside class="planner-sidebar">
        <div class="sidebar-sticky">
          <button class="btn-tool primary" @click="showToast($t('mealplan.shopping_list'))">
            <div class="icon-box"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg></div>
            <div class="text-group">
              <span class="btn-title">{{ $t('mealplan.shopping_list') }}</span>
              <span class="btn-sub">{{ $t('mealplan.auto_generated') }}</span>
            </div>
            <div class="arrow-indicator">→</div>
          </button>

          <button class="btn-tool secondary" @click="showToast($t('mealplan.saved_recipes'))">
            <div class="icon-box"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path></svg></div>
            <div class="text-group">
              <span class="btn-title">{{ $t('mealplan.saved_recipes') }}</span>
              <span class="btn-sub">12 {{ $t('mealplan.fav_count') }}</span>
            </div>
          </button>
        </div>
      </aside>

      <main class="planner-content">
        <header class="planner-header">
          <div class="header-left">
            <div class="brand-tag">GOMET PLANNER /// V.PRO</div>
            <h1 class="main-title">Culinary <span class="text-serif">Blueprint</span></h1>
          </div>
          
          <div class="header-right">
            <div class="week-selector">
              <button class="btn-arrow" @click="changeWeek(-1)" :disabled="isLoading">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"></polyline></svg>
              </button>
              <div class="week-info">
                <span class="week-label">{{ $t('mealplan.current_week') }}</span>
                <span class="week-display">{{ currentWeekDisplay }}</span>
              </div>
              <button class="btn-arrow" @click="changeWeek(1)" :disabled="isLoading">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"></polyline></svg>
              </button>
            </div>

            <div class="actions">
              <button class="btn-ai-gen" @click="autoFillAI" :disabled="isLoading" :class="{ loading: isLoading }">
                <span class="icon-sparkle">
                  <svg v-if="isLoading" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="spin-icon"><line x1="12" y1="2" x2="12" y2="6"/><line x1="12" y1="18" x2="12" y2="22"/><line x1="4.93" y1="4.93" x2="7.76" y2="7.76"/><line x1="16.24" y1="16.24" x2="19.07" y2="19.07"/><line x1="2" y1="12" x2="6" y2="12"/><line x1="18" y1="12" x2="22" y2="12"/><line x1="4.93" y1="19.07" x2="7.76" y2="16.24"/><line x1="16.24" y1="7.76" x2="19.07" y2="4.93"/></svg>
                  <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                </span>
                <div class="btn-col">
                  <span class="main-text">{{ isLoading ? 'Đang tải...' : $t('mealplan.auto_fill') }}</span>
                  <span class="sub-text">{{ $t('mealplan.ai_powered') }}</span>
                </div>
              </button>
            </div>
          </div>
        </header>

        <div class="grid-wrapper hide-scrollbar">
          <div class="blueprint-grid">
            <div v-for="(day, dIndex) in weekData" :key="dIndex" class="day-column" :class="{ 'today': day.isToday }">
              
              <div class="col-header">
                <span class="day-name">{{ day.name }}</span>
                <span class="day-date">{{ day.displayDate }}</span>
                <div class="header-line"></div>
              </div>

              <div class="slots-container">
                <div v-for="mealType in ['BREAKFAST', 'LUNCH', 'DINNER']" :key="mealType" class="meal-slot">
                  <span class="slot-label">{{ getMealLabel(mealType) }}</span>
                  
                  <div v-if="day.meals[mealType]" class="dish-card" @click="showDishDetail(day.meals[mealType])" :class="{'is-completed': day.meals[mealType].isCompleted}">
                    <img :src="day.meals[mealType].image || 'https://images.unsplash.com/photo-1490818387583-1b5f2223d848?w=600'" class="dish-bg" />
                    <div class="dish-overlay">
                      <span class="dish-cat">{{ day.meals[mealType].postId ? 'Hệ thống' : 'Món tự nhập' }}</span>
                      <h4 class="dish-name">{{ day.meals[mealType].name }}</h4>
                    </div>
                    
                    <button class="btn-check" @click.stop="markCompleted(dIndex, mealType, day.meals[mealType].planId)" title="Đánh dấu đã ăn">
                      <svg v-if="day.meals[mealType].isCompleted" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#22c55e" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                      <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle></svg>
                    </button>

                    <button class="btn-remove" @click.stop="removeDish(dIndex, mealType, day.meals[mealType].planId)" title="Xóa món này">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                    </button>
                  </div>

                  <button v-else class="btn-add-slot" @click="goToSearch(day.fullDate, mealType)">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                  </button>
                </div>
              </div>

            </div>
          </div>
        </div>

      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth' // Giả sử bạn dùng Pinia lưu user
import api from '@/services/api' // Giả sử bạn có cấu hình Axios (hoặc dùng import axios từ 'axios')

const router = useRouter()
const { t } = useI18n()
const authStore = useAuthStore()

// --- QUẢN LÝ THỜI GIAN ---
const baseDate = ref(new Date()) // Ngày làm mốc để tính toán tuần
const weekData = ref([])
const isLoading = ref(false)

// Lấy accountId từ store (Nhớ đảm bảo biến này khớp với cách bạn lưu trong Login)
const accountId = computed(() => authStore.user?.accountID || authStore.user?.id)

// Sinh mảng 7 ngày cho tuần hiện tại
const generateWeekDays = (date) => {
  const dayOfWeek = date.getDay() === 0 ? 6 : date.getDay() - 1 // Đưa T2 thành 0, CN thành 6
  const startOfWeek = new Date(date)
  startOfWeek.setDate(date.getDate() - dayOfWeek)

  const days = []
  const dayNames = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']
  
  for (let i = 0; i < 7; i++) {
    const current = new Date(startOfWeek)
    current.setDate(startOfWeek.getDate() + i)
    
    // Định dạng YYYY-MM-DD để gửi lên Backend
    const year = current.getFullYear()
    const month = String(current.getMonth() + 1).padStart(2, '0')
    const day = String(current.getDate()).padStart(2, '0')
    const fullDate = `${year}-${month}-${day}`
    
    // Kiểm tra xem có phải hôm nay không
    const today = new Date()
    const isToday = current.toDateString() === today.toDateString()

    days.push({
      name: dayNames[i],
      displayDate: day,
      fullDate: fullDate,
      isToday: isToday,
      meals: { BREAKFAST: null, LUNCH: null, DINNER: null } // Khởi tạo rỗng
    })
  }
  return days
}

// Hiển thị text: Vd "Mar 02 — 08, 2026"
const currentWeekDisplay = computed(() => {
  if (!weekData.value.length) return ''
  const start = new Date(weekData.value[0].fullDate)
  const end = new Date(weekData.value[6].fullDate)
  const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
  return `${monthNames[start.getMonth()]} ${String(start.getDate()).padStart(2,'0')} — ${String(end.getDate()).padStart(2,'0')}, ${end.getFullYear()}`
})

// --- TƯƠNG TÁC API BACKEND ---

// 1. Tải toàn bộ dữ liệu tuần
const loadWeekData = async () => {
  if (!accountId.value) {
    showToast("Vui lòng đăng nhập để xem kế hoạch ăn uống!")
    return
  }

  isLoading.value = true
  const days = generateWeekDays(baseDate.value)
  weekData.value = days

  try {
    // Gọi API song song (Promise.all) cho 7 ngày để tối ưu tốc độ
    const promises = days.map(day => 
      api.get(`/api/meal-plans/user/${accountId.value}/date/${day.fullDate}`)
    )
    
    const responses = await Promise.all(promises)
    
    // Lắp dữ liệu API vào mảng weekData
    responses.forEach((res, index) => {
      const plans = res.data // Mảng các món ăn trong ngày đó
      
      plans.forEach(plan => {
        // Backend trả về MealType là 'BREAKFAST', 'LUNCH'...
        if (weekData.value[index].meals[plan.mealType] !== undefined) {
          weekData.value[index].meals[plan.mealType] = {
            planId: plan.planId,
            postId: plan.postId,
            name: plan.customMealName || `Món ăn #${plan.postId}`, // Nếu ăn ngoài hiện tên tự nhập
            isCompleted: plan.isCompleted,
            image: null // Sau này nếu bạn join bảng Post ở BE thì lấy ảnh từ plan.postImage
          }
        }
      })
    })

  } catch (error) {
    console.error("Lỗi tải lịch ăn:", error)
    showToast("Không thể kết nối máy chủ!")
  } finally {
    isLoading.value = false
  }
}

// 2. Chuyển tuần
const changeWeek = (direction) => {
  const newDate = new Date(baseDate.value)
  newDate.setDate(baseDate.value.getDate() + (direction * 7))
  baseDate.value = newDate
  loadWeekData() // Gọi lại API cho tuần mới
}

// 3. Xóa món ăn khỏi lịch
const removeDish = async (dayIndex, mealType, planId) => {
  if (!confirm(t('mealplan.remove_confirm') || 'Bạn có chắc muốn xóa món này?')) return

  try {
    // Gọi API DELETE
    await api.delete(`/api/meal-plans/${planId}`)
    
    // Xóa thành công thì dọn dẹp UI
    weekData.value[dayIndex].meals[mealType] = null
    showToast('Đã xóa món ăn khỏi lịch!')
  } catch (error) {
    showToast('Lỗi khi xóa món ăn.')
  }
}

// 4. Tick đã ăn xong
const markCompleted = async (dayIndex, mealType, planId) => {
  try {
    // Gọi API PUT
    await api.put(`/api/meal-plans/${planId}/complete`)
    
    // Cập nhật UI
    weekData.value[dayIndex].meals[mealType].isCompleted = true
    showToast('Tuyệt vời! Đã hoàn thành bữa ăn.')
  } catch (error) {
    showToast('Lỗi cập nhật trạng thái.')
  }
}

// 5. Chuyển hướng sang trang tìm kiếm (Để thêm món mới)
const goToSearch = (fullDate, mealType) => {
  // Đẩy ngày và buổi ăn qua URL để trang Search biết mà gọi API POST lưu lại
  router.push({ 
    path: '/search', 
    query: { 
      planDate: fullDate, 
      mealType: mealType 
    } 
  })
}

// --- UTILS ---
const showDishDetail = (dish) => {
  if (dish.postId) {
    router.push(`/recipe/${dish.postId}`) // Vào xem chi tiết bài viết
  } else {
    showToast(`Món tự nhập: ${dish.name}`)
  }
}

const getMealLabel = (type) => ({
  'BREAKFAST': t('mealplan.slot_breakfast', 'Sáng'),
  'LUNCH':     t('mealplan.slot_lunch', 'Trưa'),
  'DINNER':    t('mealplan.slot_dinner', 'Tối')
})[type]

const showToast = (msg) => {
  const el = document.createElement('div')
  el.textContent = msg
  Object.assign(el.style, {
    position: 'fixed', bottom: '24px', left: '50%', transform: 'translateX(-50%)',
    background: '#1C1917', color: '#fff', padding: '12px 24px', borderRadius: '12px',
    fontSize: '0.9rem', zIndex: '99999', fontFamily: 'Mulish,sans-serif',
    boxShadow: '0 4px 20px rgba(0,0,0,0.25)', transition: 'opacity 0.4s', pointerEvents: 'none',
  })
  document.body.appendChild(el)
  setTimeout(() => { el.style.opacity = '0'; setTimeout(() => el.remove(), 400) }, 2600)
}

// Chạy lần đầu khi load trang
onMounted(() => {
  loadWeekData()
})

const autoFillAI = () => showToast('Tính năng AI đang bảo trì')
</script>

<style scoped>
/* --- 1. CORE LAYOUT --- */
.gomet-planner-pro {
  width: 100%; height: 100vh; background: #FBF6F1; color: #1E293B;
  font-family: 'Manrope', sans-serif; position: relative; overflow: hidden;
}

/* Background */
.bg-layer { position: absolute; inset: 0; pointer-events: none; z-index: 0; }
.grid-mesh {
  position: absolute; inset: 0;
  background-image: linear-gradient(rgba(0,0,0,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(0,0,0,0.03) 1px, transparent 1px);
  background-size: 40px 40px;
}
.noise-texture {
  position: absolute; inset: 0; opacity: 0.03;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
}

/* 🟢 MAIN LAYOUT */
.planner-wrapper {
  display: flex; width: 100%; height: 100%; max-width: 1800px;
  margin: 0 auto; padding: 30px; gap: 30px; position: relative; z-index: 10;
}

/* --- LEFT SIDEBAR --- */
.planner-sidebar { width: 280px; flex-shrink: 0; display: flex; flex-direction: column; }
.sidebar-sticky { display: flex; flex-direction: column; gap: 20px; margin-top: 20px; }

/* Sidebar Pro Buttons */
.btn-tool {
  display: flex; align-items: center; gap: 16px; padding: 20px; border-radius: 20px;
  border: none; cursor: pointer; transition: 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  text-align: left; position: relative; overflow: hidden; width: 100%;
}

.btn-tool.primary {
  background: #1E293B; color: #FFF;
  box-shadow: 0 15px 40px rgba(30, 41, 59, 0.2);
}
.btn-tool.primary:hover { background: #0F172A; transform: translateY(-3px) scale(1.02); }

.btn-tool.secondary {
  background: #FFF; color: #1E293B; border: 1px solid rgba(0,0,0,0.05);
  box-shadow: 0 5px 20px rgba(0,0,0,0.02);
}
.btn-tool.secondary:hover { border-color: #EA580C; background: #FFF7ED; transform: translateY(-3px); }

.icon-box {
  width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.primary .icon-box { background: rgba(255,255,255,0.15); color: #EA580C; }
.secondary .icon-box { background: #F1F5F9; color: #64748B; }

.text-group { display: flex; flex-direction: column; flex: 1; }
.btn-title { font-size: 0.8rem; font-weight: 800; letter-spacing: 0.5px; }
.btn-sub { font-size: 0.7rem; opacity: 0.7; margin-top: 4px; font-weight: 500; }

.arrow-indicator { font-size: 1.2rem; opacity: 0.5; transition: 0.3s; }
.btn-tool:hover .arrow-indicator { opacity: 1; transform: translateX(5px); }

/* --- MAIN CONTENT --- */
.planner-content { flex: 1; display: flex; flex-direction: column; overflow: hidden; }

.planner-header {
  display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 25px;
  background: #FFF; padding: 25px 35px; border-radius: 24px; border: 1px solid rgba(0,0,0,0.05);
  flex-shrink: 0; box-shadow: 0 5px 20px rgba(0,0,0,0.02);
}

.brand-tag { font-size: 0.7rem; font-weight: 800; color: #EA580C; letter-spacing: 2px; margin-bottom: 5px; }
.main-title { font-family: 'Manrope', sans-serif; font-size: 2.5rem; font-weight: 800; color: #1E293B; margin: 0; line-height: 1; }
.text-serif { font-family: 'Playfair Display', serif; font-style: italic; color: #EA580C; }

.header-right { display: flex; gap: 30px; align-items: center; }

/* Week Selector */
.week-selector { display: flex; align-items: center; gap: 5px; background: #F8FAFC; padding: 5px; border-radius: 50px; border: 1px solid #E2E8F0; }
.week-info { display: flex; flex-direction: column; align-items: center; padding: 0 15px; }
.week-label { font-size: 0.55rem; font-weight: 700; color: #94A3B8; letter-spacing: 1px; }
.week-display { font-weight: 800; font-size: 0.85rem; color: #1E293B; }
.btn-arrow { width: 36px; height: 36px; border-radius: 50%; border: none; background: #FFF; cursor: pointer; transition: 0.2s; display: flex; align-items: center; justify-content: center; color: #64748B; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.btn-arrow:hover { background: #1E293B; color: #FFF; transform: scale(1.1); }

/* Actions */
.actions { display: flex; gap: 12px; }
.btn-ai-gen {
  background: linear-gradient(135deg, #FFF, #F0F9FF); border: 1px solid #E0F2FE; color: #0EA5E9; padding: 10px 24px; border-radius: 12px; display: flex; align-items: center; gap: 12px; cursor: pointer; transition: 0.3s;
}
.btn-ai-gen:hover { border-color: #0EA5E9; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(14, 165, 233, 0.15); }
.btn-ai-gen.loading { opacity: 0.7; cursor: not-allowed; pointer-events: none; }
.btn-col { display: flex; flex-direction: column; align-items: flex-start; }
.main-text { font-weight: 800; font-size: 0.75rem; }
.sub-text { font-size: 0.6rem; opacity: 0.8; }

.btn-export {
  background: #EA580C; color: #FFF; border: none; padding: 0 24px; border-radius: 12px; font-weight: 800; font-size: 0.75rem; cursor: pointer; transition: 0.2s; box-shadow: 0 8px 20px rgba(234,88,12,0.25); display: flex; align-items: center; gap: 8px;
}
.btn-export:hover { background: #C2410C; transform: translateY(-2px); }

/* --- GRID SYSTEM --- */
.grid-wrapper {
  flex: 1; overflow-y: auto; overflow-x: hidden; border-radius: 20px; border: 1px solid #E2E8F0; background: #FFF;
}
.hide-scrollbar::-webkit-scrollbar { display: none; }

.blueprint-grid { display: grid; grid-template-columns: repeat(7, 1fr); gap: 1px; background: #E2E8F0; width: 100%; min-height: 100%; }

.day-column {
  background: #FFF; min-height: 100%; padding: 25px 12px; display: flex; flex-direction: column; transition: 0.3s; position: relative;
}
.day-column:hover { background: #FAFAFA; }
.day-column.today { background: #FFF7ED; }

.col-header { text-align: center; margin-bottom: 30px; }
.day-name { font-size: 0.65rem; font-weight: 800; color: #94A3B8; letter-spacing: 1px; display: block; margin-bottom: 5px; }
.day-date { font-family: 'Playfair Display', serif; font-size: 2rem; font-weight: 700; color: #1E293B; }
.header-line { width: 30px; height: 3px; background: #F1F5F9; margin: 10px auto 0; border-radius: 2px; }
.today .header-line { background: #EA580C; width: 50px; }
.today .day-name { color: #EA580C; }

.slots-container { flex: 1; display: flex; flex-direction: column; gap: 20px; }
.meal-slot { display: flex; flex-direction: column; gap: 8px; }
.slot-label { font-size: 0.6rem; font-weight: 800; color: #CBD5E1; text-transform: uppercase; padding-left: 4px; }

/* Dish Card */
.dish-card {
  position: relative; height: 120px; border-radius: 14px; overflow: hidden; cursor: pointer; box-shadow: 0 4px 10px rgba(0,0,0,0.05); transition: 0.3s;
}
.dish-card:hover { transform: translateY(-3px); box-shadow: 0 12px 25px rgba(234,88,12,0.15); }
.dish-bg { width: 100%; height: 100%; object-fit: cover; transition: 0.3s; }
.dish-card:hover .dish-bg { transform: scale(1.05); }
.dish-overlay {
  position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.8), rgba(0,0,0,0.1)); padding: 12px; display: flex; flex-direction: column; justify-content: flex-end;
}
.dish-cat { font-size: 0.55rem; color: #EA580C; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
.dish-name { color: #FFF; font-size: 0.85rem; font-weight: 700; margin: 2px 0 0; line-height: 1.25; text-shadow: 0 2px 4px rgba(0,0,0,0.5); display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

.btn-remove {
  position: absolute; top: 8px; right: 8px; width: 22px; height: 22px; background: rgba(255,255,255,0.2); border: none; border-radius: 50%; color: #FFF; cursor: pointer; opacity: 0; transition: 0.2s; display: flex; align-items: center; justify-content: center; backdrop-filter: blur(4px);
}
.dish-card:hover .btn-remove { opacity: 1; }
.btn-remove:hover { background: #EF4444; transform: rotate(90deg); }

/* Add Button */
.btn-add-slot {
  width: 100%; height: 50px; border: 1px dashed #E2E8F0; background: transparent; border-radius: 12px; cursor: pointer; transition: 0.3s; color: #CBD5E1; display: flex; align-items: center; justify-content: center;
}
.btn-add-slot:hover { border-color: #EA580C; color: #EA580C; background: rgba(234,88,12,0.03); transform: scale(1.02); }

/* Responsive */
@media (max-width: 1200px) {
  .planner-wrapper { flex-direction: column; overflow-y: auto; }
  .planner-sidebar { width: 100%; flex-direction: row; }
  .sidebar-sticky { flex-direction: row; width: 100%; }
  .btn-tool { flex: 1; }
  .grid-wrapper { overflow-x: auto; }
  .blueprint-grid { min-width: 1100px; } /* Ensure grid is not distorted */
}
.btn-check {
  position: absolute; top: 8px; left: 8px; width: 26px; height: 26px;
  background: rgba(255,255,255,0.85); border: none; border-radius: 50%;
  color: #94A3B8; cursor: pointer; transition: 0.2s;
  display: flex; align-items: center; justify-content: center; backdrop-filter: blur(4px);
  box-shadow: 0 2px 5px rgba(0,0,0,0.1); z-index: 2;
}
.btn-check:hover { background: #FFF; transform: scale(1.1); color: #22c55e; }
.is-completed .btn-check { background: #DCFCE7; } /* Xanh lá nhạt khi đã tick */
.is-completed .dish-bg { filter: grayscale(40%) opacity(0.8); } /* Làm mờ ảnh khi đã ăn xong */
.is-completed .dish-name { text-decoration: line-through; color: #E2E8F0; }
</style>