<template>
  <div class="gomet-planner-pro">
    <div class="bg-layer">
      <div class="grid-mesh"></div>
      <div class="noise-texture"></div>
      <div class="glow-spot spotlight"></div>
    </div> 

    <div class="planner-wrapper">
      <main class="planner-content">
        <header class="planner-header">
          <div class="header-left">
            <div class="brand-tag">GOMET PLANNER /// V.PRO</div>
            <h1 class="main-title">Kế hoạch <span class="text-serif">Bữa ăn</span></h1>
          </div>
          
          <div class="header-right">
            <div class="week-selector">
              <button class="btn-arrow" @click="changeWeek(-1)" :disabled="isLoading">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"></polyline></svg>
              </button>
              <div class="week-info">
                <span class="week-label">TUẦN HIỆN TẠI</span>
                <span class="week-display">{{ currentWeekDisplay }}</span>
              </div>
              <button class="btn-arrow" @click="changeWeek(1)" :disabled="isLoading">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"></polyline></svg>
              </button>
            </div>
          </div>
        </header>

        <div class="grid-wrapper hide-scrollbar">
          <div class="blueprint-grid">
            <div v-for="(day, dIndex) in weekData" :key="dIndex" class="day-column" :class="{ 'today': day.isToday }">
              <div class="col-header">
                <span class="day-name">{{ getDayNameVN(day.name) }}</span>
                <span class="day-date">{{ day.displayDate }}</span>
                <div class="header-line"></div>
              </div>

              <div class="slots-container">
                <div v-for="mealType in ['BREAKFAST', 'LUNCH', 'DINNER']" :key="mealType" class="meal-slot">
                  <span class="slot-label">{{ getMealLabel(mealType) }}</span>
                  
                  <div v-if="day.meals[mealType]" class="dish-card" @click="showDishDetail(day.meals[mealType])" :class="{'is-completed': day.meals[mealType].isCompleted}">
                    <img :src="day.meals[mealType].image || 'https://images.unsplash.com/photo-1490818387583-1b5f2223d848?w=600'" class="dish-bg" />
                    
                    <div class="dish-overlay">
                      <span class="dish-cat">{{ day.meals[mealType].category || 'Món tự nhập' }}</span>
                      <h4 class="dish-name">{{ day.meals[mealType].name }}</h4>
                    </div>
                    
                    <button class="btn-check" @click.stop="markCompleted(dIndex, mealType, day.meals[mealType].planId)" title="Đánh dấu đã ăn">
                      <svg v-if="day.meals[mealType].isCompleted" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#000" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                      <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle></svg>
                    </button>

                    <button class="btn-remove" @click.stop="removeDish(dIndex, mealType, day.meals[mealType].planId)" title="Xóa món">
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
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'

const router = useRouter()
const authStore = useAuthStore()

const baseDate = ref(new Date()) 
const weekData = ref([])
const isLoading = ref(false)
const accountId = computed(() => authStore.user?.accountID || authStore.user?.id)

const generateWeekDays = (date) => {
  const dayOfWeek = date.getDay() === 0 ? 6 : date.getDay() - 1 
  const startOfWeek = new Date(date)
  startOfWeek.setDate(date.getDate() - dayOfWeek)
  const days = []
  const dayNames = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']
  for (let i = 0; i < 7; i++) {
    const current = new Date(startOfWeek)
    current.setDate(startOfWeek.getDate() + i)
    const fullDate = `${current.getFullYear()}-${String(current.getMonth() + 1).padStart(2, '0')}-${String(current.getDate()).padStart(2, '0')}`
    days.push({
      name: dayNames[i],
      displayDate: current.getDate(),
      fullDate: fullDate,
      isToday: current.toDateString() === new Date().toDateString(),
      meals: { BREAKFAST: null, LUNCH: null, DINNER: null }
    })
  }
  return days
}

const currentWeekDisplay = computed(() => {
  if (!weekData.value.length) return ''
  const start = new Date(weekData.value[0].fullDate)
  const end = new Date(weekData.value[6].fullDate)
  const monthNames = ["Thg 1", "Thg 2", "Thg 3", "Thg 4", "Thg 5", "Thg 6", "Thg 7", "Thg 8", "Thg 9", "Thg 10", "Thg 11", "Thg 12"]
  return `${String(start.getDate()).padStart(2,'0')} ${monthNames[start.getMonth()]} — ${String(end.getDate()).padStart(2,'0')} ${monthNames[end.getMonth()]}, ${end.getFullYear()}`
})

const loadWeekData = async () => {
  if (!accountId.value) { showToast("Vui lòng đăng nhập!"); return }
  isLoading.value = true
  const days = generateWeekDays(baseDate.value)
  weekData.value = days
  try {
    const responses = await Promise.all(
      days.map(day => api.get(`/api/meal-plans/user/${accountId.value}/date/${day.fullDate}`))
    )
    
    responses.forEach((res, index) => {
      res.data.forEach(plan => {
        if (weekData.value[index].meals[plan.mealType] !== undefined) {
          // 🔥 ĐÃ FIX: Mapping dữ liệu sạch sẽ từ Backend mới
          weekData.value[index].meals[plan.mealType] = {
            planId: plan.planId,
            postId: plan.postId,
            isCompleted: plan.isCompleted,
            name: plan.postTitle || plan.customMealName || `Món ăn #${plan.postId}`,
            image: plan.postMedia || null,
            category: plan.categoryName // Nhận tên category từ DTO
          }
        }
      })
    })
  } catch (error) { 
    showToast("Lỗi kết nối máy chủ!") 
  } finally { 
    isLoading.value = false 
  }
}

const changeWeek = (dir) => { baseDate.value.setDate(baseDate.value.getDate() + (dir * 7)); loadWeekData() }

const removeDish = async (dayIdx, type, id) => {
  if (!confirm('Xóa món này khỏi lịch?')) return
  try {
    await api.delete(`/api/meal-plans/${id}`)
    weekData.value[dayIdx].meals[type] = null
    showToast('Đã xóa món ăn!')
  } catch (e) { showToast('Lỗi khi xóa.') }
}

const markCompleted = async (dayIdx, type, id) => {
  try {
    await api.put(`/api/meal-plans/${id}/complete`)
    weekData.value[dayIdx].meals[type].isCompleted = true
    showToast('Bữa ăn hoàn thành!')
  } catch (e) { showToast('Lỗi cập nhật.') }
}

const goToSearch = (date, type) => router.push({ path: '/search', query: { planDate: date, mealType: type } })
const showDishDetail = (dish) => dish.postId ? router.push(`/post/${dish.postId}`) : showToast(`Món: ${dish.name}`)

const getMealLabel = (t) => ({ 'BREAKFAST': 'Sáng', 'LUNCH': 'Trưa', 'DINNER': 'Tối' })[t]
const getDayNameVN = (n) => ({ 'MON': 'THỨ 2', 'TUE': 'THỨ 3', 'WED': 'THỨ 4', 'THU': 'THỨ 5', 'FRI': 'THỨ 6', 'SAT': 'THỨ 7', 'SUN': 'CN' })[n]

const showToast = (msg) => {
  const el = document.createElement('div')
  el.textContent = msg
  Object.assign(el.style, {
    position: 'fixed', bottom: '30px', left: '50%', transform: 'translateX(-50%)',
    background: '#D4AF37', color: '#050505', padding: '12px 28px', borderRadius: '50px',
    fontSize: '0.95rem', fontWeight: '800', zIndex: '10000', fontFamily: 'Mulish,sans-serif',
    boxShadow: '0 15px 40px rgba(212,175,55,0.4)', transition: 'all 0.4s ease', opacity: '0'
  })
  document.body.appendChild(el)
  setTimeout(() => { el.style.opacity = '1'; el.style.bottom = '40px' }, 10)
  setTimeout(() => { el.style.opacity = '0'; setTimeout(() => el.remove(), 400) }, 2500)
}

onMounted(loadWeekData)
</script>

<style scoped lang="scss">
$gold: #D4AF37;
$dark-gray: #111111;
$panel-bg: rgba(20, 20, 20, 0.7);

.gomet-planner-pro {
  width: 100%; height: 100vh; padding-top: 80px; overflow: hidden; position: relative;
  background: radial-gradient(circle at 50% 20%, #1a1815 0%, #080706 50%, #000000 100%);
  color: #F4F0EA; font-family: 'Mulish', sans-serif;
}

/* BACKGROUND EFFECTS */
.bg-layer { position: absolute; inset: 0; pointer-events: none; }
.grid-mesh { position: absolute; inset: 0; background-image: linear-gradient(rgba($gold, 0.03) 1px, transparent 1px), linear-gradient(90deg, rgba($gold, 0.03) 1px, transparent 1px); background-size: 60px 60px; mask-image: radial-gradient(circle, black, transparent 80%); }
.spotlight { position: absolute; top: -10%; left: 50%; transform: translateX(-50%); width: 100vw; height: 60vh; background: radial-gradient(circle, rgba($gold, 0.07), transparent 70%); filter: blur(60px); }

.planner-wrapper {
  max-width: 1600px; height: calc(100vh - 120px); margin: 0 auto; padding: 0 40px; position: relative; z-index: 10;
}

.planner-content { height: 100%; display: flex; flex-direction: column; }

.planner-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px;
  background: linear-gradient(135deg, rgba(20, 20, 20, 0.8), rgba(10, 10, 10, 0.9)); 
  padding: 24px 40px; border-radius: 24px; border: 1px solid rgba($gold, 0.2); 
  box-shadow: 0 10px 30px rgba(0,0,0,0.5); backdrop-filter: blur(20px);
}
.brand-tag { font-size: 0.7rem; font-weight: 800; color: $gold; letter-spacing: 4px; margin-bottom: 8px; text-shadow: 0 0 10px rgba($gold, 0.3); }
.main-title { font-family: 'Playfair Display', serif; font-size: 3rem; font-weight: 900; color: #FFF; margin: 0; line-height: 1; letter-spacing: 1px; }
.text-serif { font-style: italic; color: $gold; }

.header-right { display: flex; align-items: center; }
.week-selector { 
  display: flex; align-items: center; background: rgba(0,0,0,0.6); 
  padding: 8px; border-radius: 100px; border: 1px solid rgba($gold, 0.2); 
  box-shadow: inset 0 0 20px rgba(0,0,0,0.8), 0 5px 15px rgba(0,0,0,0.3);
}
.week-info { display: flex; flex-direction: column; align-items: center; padding: 0 25px; min-width: 220px; }
.week-label { font-size: 0.65rem; font-weight: 900; color: rgba($gold, 0.7); letter-spacing: 3px; margin-bottom: 4px; }
.week-display { font-weight: 800; font-size: 1.05rem; color: #FFF; text-shadow: 0 0 10px rgba(255,255,255,0.2); }
.btn-arrow { width: 42px; height: 42px; border-radius: 50%; border: 1px solid rgba(255,255,255,0.1); background: rgba(255,255,255,0.03); color: #FFF; cursor: pointer; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); display: flex; justify-content: center; align-items: center;}
.btn-arrow:hover:not(:disabled) { background: $gold; color: #000; border-color: $gold; transform: scale(1.1); box-shadow: 0 0 15px rgba($gold, 0.5); }
.btn-arrow:disabled { opacity: 0.5; cursor: not-allowed; }

/* GRID SYSTEM */
.grid-wrapper { 
  flex: 1; border-radius: 24px; border: 1px solid rgba($gold, 0.15); 
  background: rgba(10, 10, 10, 0.6); backdrop-filter: blur(25px); 
  overflow-x: auto; box-shadow: 0 20px 40px rgba(0,0,0,0.4);
}
.blueprint-grid { display: grid; grid-template-columns: repeat(7, 1fr); height: 100%; min-width: 1200px; }

.day-column { padding: 30px 18px; display: flex; flex-direction: column; transition: 0.4s ease; border-right: 1px solid rgba(255,255,255,0.03); }
.day-column:last-child { border-right: none; }
.day-column:hover { background: rgba(255,255,255,0.02); }
.day-column.today { background: linear-gradient(to bottom, rgba($gold, 0.08), transparent); position: relative; }
.day-column.today::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 3px; background: $gold; box-shadow: 0 0 15px $gold; }

.day-name { font-size: 0.75rem; font-weight: 800; color: rgba(255,255,255,0.5); letter-spacing: 2px; display: block; margin-bottom: 8px; text-align: center; }
.day-date { font-family: 'Playfair Display', serif; font-size: 2.8rem; font-weight: 900; color: #FFF; display: block; text-align: center; line-height: 1; }
.header-line { width: 30px; height: 3px; background: rgba($gold, 0.3); margin: 15px auto 35px; border-radius: 10px; transition: 0.3s; }
.today .header-line { background: $gold; box-shadow: 0 0 10px $gold; width: 50px; }
.today .day-name { color: $gold; }

.slots-container { flex: 1; display: flex; flex-direction: column; gap: 24px; }
.meal-slot { display: flex; flex-direction: column; gap: 10px; }
.slot-label { font-size: 0.75rem; font-weight: 900; color: rgba($gold, 0.5); text-transform: uppercase; letter-spacing: 1.5px; text-align: center; }

.dish-card { 
  position: relative; height: 150px; border-radius: 18px; overflow: hidden; cursor: pointer; 
  border: 1px solid rgba(255,255,255,0.08); transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); 
  box-shadow: 0 10px 20px rgba(0,0,0,0.3);
}
.dish-card:hover { border-color: $gold; transform: translateY(-6px); box-shadow: 0 15px 30px rgba(0,0,0,0.6), 0 0 15px rgba($gold, 0.2); }
.dish-bg { width: 100%; height: 100%; object-fit: cover; opacity: 0.7; transition: 0.6s cubic-bezier(0.16, 1, 0.3, 1); }
.dish-card:hover .dish-bg { transform: scale(1.15); opacity: 1; }

.dish-overlay { 
  position: absolute; inset: 0; 
  background: linear-gradient(to top, rgba(0,0,0,0.95) 0%, rgba(0,0,0,0.4) 60%, transparent 100%); 
  padding: 18px 15px; display: flex; flex-direction: column; justify-content: flex-end; 
}
.dish-cat { font-size: 0.65rem; color: $gold; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 4px; text-shadow: 0 2px 4px rgba(0,0,0,0.8); }
.dish-name { font-size: 1rem; font-weight: 800; color: #FFF; margin: 0; line-height: 1.3; text-shadow: 0 2px 4px rgba(0,0,0,0.8); display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

.btn-check, .btn-remove { position: absolute; width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.3s; border: none; backdrop-filter: blur(8px); z-index: 2; }
.btn-check { top: 12px; left: 12px; background: rgba(0,0,0,0.5); color: #FFF; border: 1px solid rgba(255,255,255,0.2); }
.btn-check:hover { background: $gold; color: #000; border-color: $gold; transform: scale(1.1); }
.is-completed .btn-check { background: $gold; color: #000; border-color: $gold; box-shadow: 0 0 10px rgba($gold, 0.5); }
.is-completed .dish-bg { filter: grayscale(0.8) brightness(0.5); }
.is-completed .dish-name { text-decoration: line-through; color: rgba(255,255,255,0.5); }
.is-completed .dish-cat { color: rgba($gold, 0.5); }

.btn-remove { top: 12px; right: 12px; background: rgba(0,0,0,0.6); color: #FFF; opacity: 0; border: 1px solid rgba(255,255,255,0.1); transform: translateX(10px); }
.dish-card:hover .btn-remove { opacity: 1; transform: translateX(0); }
.btn-remove:hover { background: #EF4444; border-color: #EF4444; transform: rotate(90deg); }

.btn-add-slot { 
  width: 100%; height: 80px; border: 1.5px dashed rgba(255,255,255,0.15); 
  background: rgba(0,0,0,0.2); border-radius: 18px; cursor: pointer; 
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); color: rgba(255,255,255,0.3); display: flex; align-items: center; justify-content: center;
}
.btn-add-slot:hover { border-color: $gold; border-style: solid; color: $gold; background: rgba($gold, 0.05); transform: translateY(-2px); box-shadow: inset 0 0 20px rgba($gold, 0.1), 0 5px 15px rgba(0,0,0,0.2); }
</style>