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

        <div class="grid-wrapper custom-scroll-x">
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
// logic giữ nguyên 100% như cũ
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
  if (!accountId.value) return
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
          weekData.value[index].meals[plan.mealType] = {
            planId: plan.planId,
            postId: plan.postId,
            isCompleted: plan.isCompleted,
            name: plan.postTitle || plan.customMealName || `Món ăn #${plan.postId}`,
            image: plan.postMedia || null,
            category: plan.categoryName
          }
        }
      })
    })
  } catch (error) { 
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
  } catch (e) { }
}
const markCompleted = async (dayIdx, type, id) => {
  try {
    await api.put(`/api/meal-plans/${id}/complete`)
    weekData.value[dayIdx].meals[type].isCompleted = true
  } catch (e) { }
}
const goToSearch = (date, type) => router.push({ path: '/search', query: { planDate: date, mealType: type } })
const showDishDetail = (dish) => dish.postId ? router.push(`/post/${dish.postId}`) : null
const getMealLabel = (t) => ({ 'BREAKFAST': 'Sáng', 'LUNCH': 'Trưa', 'DINNER': 'Tối' })[t]
const getDayNameVN = (n) => ({ 'MON': 'THỨ 2', 'TUE': 'THỨ 3', 'WED': 'THỨ 4', 'THU': 'THỨ 5', 'FRI': 'THỨ 6', 'SAT': 'THỨ 7', 'SUN': 'CN' })[n]

onMounted(loadWeekData)
</script>

<style scoped lang="scss">
$gold: #D4AF37;
$dark-gray: #111111;
$panel-bg: rgba(20, 20, 20, 0.7);

/* GIỮ NGUYÊN CSS CŨ VÀ THÊM PHẦN RESPONSIVE XUỐNG DƯỚI */
.gomet-planner-pro {
  width: 100%; height: 100vh; padding-top: 80px; overflow: hidden; position: relative;
  background: radial-gradient(circle at 50% 20%, #1a1815 0%, #080706 50%, #000000 100%);
  color: #F4F0EA; font-family: 'Mulish', sans-serif;
}

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

.week-selector { 
  display: flex; align-items: center; background: rgba(0,0,0,0.6); 
  padding: 8px; border-radius: 100px; border: 1px solid rgba($gold, 0.2); 
}
.week-info { display: flex; flex-direction: column; align-items: center; padding: 0 25px; min-width: 220px; }
.week-label { font-size: 0.65rem; font-weight: 900; color: rgba($gold, 0.7); letter-spacing: 3px; margin-bottom: 4px; }
.week-display { font-weight: 800; font-size: 1.05rem; color: #FFF; }
.btn-arrow { width: 42px; height: 42px; border-radius: 50%; border: 1px solid rgba(255,255,255,0.1); background: rgba(255,255,255,0.03); color: #FFF; cursor: pointer; transition: 0.3s; display: flex; justify-content: center; align-items: center;}
.btn-arrow:hover:not(:disabled) { background: $gold; color: #000; border-color: $gold; transform: scale(1.1); }

.grid-wrapper { 
  flex: 1; border-radius: 24px; border: 1px solid rgba($gold, 0.15); 
  background: rgba(10, 10, 10, 0.6); backdrop-filter: blur(25px); 
  overflow-x: auto; box-shadow: 0 20px 40px rgba(0,0,0,0.4);
}
.blueprint-grid { display: grid; grid-template-columns: repeat(7, 1fr); height: 100%; min-width: 1200px; }
.day-column { padding: 30px 18px; display: flex; flex-direction: column; border-right: 1px solid rgba(255,255,255,0.03); }
.day-column.today { background: linear-gradient(to bottom, rgba($gold, 0.08), transparent); position: relative; }
.day-column.today::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 3px; background: $gold; box-shadow: 0 0 15px $gold; }

.day-name { font-size: 0.75rem; font-weight: 800; color: rgba(255,255,255,0.5); letter-spacing: 2px; margin-bottom: 8px; text-align: center; }
.day-date { font-family: 'Playfair Display', serif; font-size: 2.8rem; font-weight: 900; color: #FFF; text-align: center; line-height: 1; }
.header-line { width: 30px; height: 3px; background: rgba($gold, 0.3); margin: 15px auto 35px; border-radius: 10px; }

.slots-container { flex: 1; display: flex; flex-direction: column; gap: 24px; }
.meal-slot { display: flex; flex-direction: column; gap: 10px; }
.slot-label { font-size: 0.75rem; font-weight: 900; color: rgba($gold, 0.5); text-transform: uppercase; text-align: center; }

.dish-card { position: relative; height: 150px; border-radius: 18px; overflow: hidden; cursor: pointer; border: 1px solid rgba(255,255,255,0.08); transition: 0.4s; }
.dish-card:hover { border-color: $gold; transform: translateY(-6px); }
.dish-bg { width: 100%; height: 100%; object-fit: cover; opacity: 0.7; }
.dish-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.95) 0%, rgba(0,0,0,0.4) 60%, transparent 100%); padding: 18px 15px; display: flex; flex-direction: column; justify-content: flex-end; }
.dish-name { font-size: 1rem; font-weight: 800; color: #FFF; margin: 0; line-height: 1.3; }

.btn-check, .btn-remove { position: absolute; width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.3s; border: none; z-index: 2; }
.btn-check { top: 12px; left: 12px; background: rgba(0,0,0,0.5); color: #FFF; border: 1px solid rgba(255,255,255,0.2); }
.btn-remove { top: 12px; right: 12px; background: rgba(0,0,0,0.6); color: #FFF; opacity: 0; transform: translateX(10px); }
.dish-card:hover .btn-remove { opacity: 1; transform: translateX(0); }

.btn-add-slot { width: 100%; height: 80px; border: 1.5px dashed rgba(255,255,255,0.15); background: rgba(0,0,0,0.2); border-radius: 18px; cursor: pointer; transition: 0.3s; color: rgba(255,255,255,0.3); display: flex; align-items: center; justify-content: center; }

/* =======================================================
   🔥 HỆ THỐNG RESPONSIVE (MỚI)
   ======================================================= */

/* 1. MÀN HÌNH LAPTOP VỪA (Dưới 1440px) */
@media (max-width: 1440px) {
  .planner-wrapper { padding: 0 20px; }
  .main-title { font-size: 2.5rem; }
  .blueprint-grid { min-width: 1000px; } /* Giảm nhẹ min-width */
}

/* 2. MÀN HÌNH TABLET (Dưới 1024px) */
@media (max-width: 1024px) {
  .gomet-planner-pro { height: auto; min-height: 100vh; overflow-y: auto; }
  .planner-wrapper { height: auto; padding-bottom: 50px; }
  .planner-header { padding: 20px; border-radius: 16px; }
  
  .blueprint-grid { min-width: 900px; }
  .day-date { font-size: 2.2rem; }
  .dish-card { height: 130px; }
}

/* 3. MÀN HÌNH MOBILE LỚN (Dưới 768px) */
@media (max-width: 768px) {
  .planner-header {
    flex-direction: column;
    gap: 20px;
    align-items: flex-start;
    text-align: left;
  }
  
  .week-selector {
    width: 100%;
    justify-content: space-between;
  }
  
  .week-info { min-width: auto; flex: 1; padding: 0 10px; }
  .week-display { font-size: 0.9rem; }

  /* Ép chiều rộng cho lưới cuộn ngang trên mobile */
  .blueprint-grid { 
    min-width: 1300px; 
    padding-bottom: 20px;
  }
  
  .day-column { padding: 20px 12px; }
  .header-line { margin-bottom: 25px; }
}

/* 4. MÀN HÌNH MOBILE NHỎ (Dưới 480px) */
@media (max-width: 480px) {
  .gomet-planner-pro { padding-top: 60px; }
  .main-title { font-size: 1.8rem; }
  .brand-tag { font-size: 0.6rem; letter-spacing: 2px; }
  
  .planner-header { padding: 15px; }
  
  .btn-arrow { width: 36px; height: 36px; }
  .week-display { font-size: 0.85rem; }

  .day-date { font-size: 1.8rem; }
  .slot-label { font-size: 0.65rem; }
  
  .dish-card { height: 110px; border-radius: 12px; }
  .dish-name { font-size: 0.85rem; }
  
  .btn-add-slot { height: 60px; border-radius: 12px; }
}

/* Custom Scrollbar cho phần lưới cuộn ngang */
.custom-scroll-x::-webkit-scrollbar {
  height: 6px;
}
.custom-scroll-x::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
}
.custom-scroll-x::-webkit-scrollbar-thumb {
  background: rgba($gold, 0.2);
  border-radius: 10px;
}
.custom-scroll-x::-webkit-scrollbar-thumb:hover {
  background: rgba($gold, 0.4);
}
</style>