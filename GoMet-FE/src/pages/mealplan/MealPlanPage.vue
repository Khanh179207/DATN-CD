<template>
  <div class="gomet-planner-pro">
    <div class="bg-layer">
      <div class="grid-mesh"></div>
      <div class="noise-texture"></div>
      <div class="glow-spot spotlight"></div>
    </div> 

    <div class="planner-wrapper">
      <main class="planner-content">
        
        <header class="planner-header anim-fade-in">
          <div class="header-left">
            <div class="brand-tag">
              <i class="fas fa-calendar-alt"></i> GOMET PLANNER PRO
            </div>
            <h1 class="main-title">Kế hoạch <span class="text-serif">Bữa ăn</span></h1>
          </div>
          
          <div class="header-right">
            <div class="week-selector">
              <button class="btn-arrow" @click="changeWeek(-1)" :disabled="isLoading">
                <i class="fas fa-chevron-left"></i>
              </button>
              <div class="week-info">
                <span class="week-label">LỊCH TRÌNH</span>
                <span class="week-display">{{ currentWeekDisplay }}</span>
              </div>
              <button class="btn-arrow" @click="changeWeek(1)" :disabled="isLoading">
                <i class="fas fa-chevron-right"></i>
              </button>
            </div>
          </div>
        </header>

        <div class="grid-wrapper custom-scroll-x anim-fade-in">
          <div class="blueprint-grid">
            
            <div 
              v-for="(day, dIndex) in weekData" 
              :key="dIndex" 
              class="day-column" 
              :class="{ 'is-today': day.isToday }"
            >
              <div class="col-header">
                <div v-if="day.isToday" class="today-badge">HÔM NAY</div>
                <div class="day-indicator">
                  <span class="day-name">{{ getDayNameVN(day.name) }}</span>
                  <span class="day-date">{{ day.displayDate }}</span>
                </div>
              </div>

              <div class="slots-container">
                <div v-for="mealType in ['BREAKFAST', 'LUNCH', 'DINNER']" :key="mealType" class="meal-slot">
                  <div class="slot-header">
                    <span class="slot-label">{{ getMealLabel(mealType) }}</span>
                    <div class="slot-line"></div>
                  </div>
                  
                  <div 
                    v-if="day.meals[mealType]" 
                    class="dish-card" 
                    @click="showDishDetail(day.meals[mealType])" 
                    :class="{'is-completed': day.meals[mealType].isCompleted}"
                  >
                    <div class="card-bg-wrapper">
                      <img :src="day.meals[mealType].image || 'https://images.unsplash.com/photo-1490818387583-1b5f2223d848?w=600'" class="dish-bg" />
                      <div class="dish-overlay"></div>
                    </div>
                    
                    <div class="dish-content">
                      <span class="dish-cat">{{ day.meals[mealType].category || 'Món tự nhập' }}</span>
                      <h4 class="dish-name">{{ day.meals[mealType].name }}</h4>
                    </div>
                    
                    <button class="btn-action btn-check" @click.stop="markCompleted(dIndex, mealType, day.meals[mealType].planId)" title="Đánh dấu đã ăn">
                      <i class="fas fa-check" v-if="day.meals[mealType].isCompleted"></i>
                      <div v-else class="circle-outline"></div>
                    </button>

                    <button class="btn-action btn-remove" @click.stop="removeDish(dIndex, mealType, day.meals[mealType].planId)" title="Xóa khỏi lịch">
                      <i class="fas fa-times"></i>
                    </button>
                  </div>

                  <button v-else class="btn-add-slot" @click="goToSearch(day.fullDate, mealType)">
                    <i class="fas fa-plus"></i>
                    <span>Thêm Món</span>
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
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { gsap } from 'gsap'

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

const runEntranceAnimation = async () => {
  await nextTick()
  gsap.fromTo('.anim-fade-in', 
    { opacity: 0, y: 20 }, 
    { opacity: 1, y: 0, stagger: 0.15, duration: 0.8, ease: 'power3.out' }
  )
}

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
    console.error(error)
  } finally { 
    isLoading.value = false 
    runEntranceAnimation()
  }
}

const changeWeek = (dir) => { baseDate.value.setDate(baseDate.value.getDate() + (dir * 7)); loadWeekData() }
const removeDish = async (dayIdx, type, id) => {
  if (!confirm('Bạn có chắc muốn xóa món này khỏi lịch?')) return
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
const getMealLabel = (t) => ({ 'BREAKFAST': 'Bữa Sáng', 'LUNCH': 'Bữa Trưa', 'DINNER': 'Bữa Tối' })[t]
const getDayNameVN = (n) => ({ 'MON': 'Thứ Hai', 'TUE': 'Thứ Ba', 'WED': 'Thứ Tư', 'THU': 'Thứ Năm', 'FRI': 'Thứ Sáu', 'SAT': 'Thứ Bảy', 'SUN': 'Chủ Nhật' })[n]

onMounted(loadWeekData)
</script>

<style scoped lang="scss">
/* BẢNG MÀU CHUẨN ĐIỆN ẢNH */
$gold: #D4AF37;
$accent: #EA580C;
$bg-deep: #09090b;
$surface-glass: rgba(255, 255, 255, 0.02);
$border-glass: rgba(255, 255, 255, 0.05);

.gomet-planner-pro {
  width: 100%; height: 100vh; padding-top: 80px; overflow: hidden; position: relative;
  background-color: $bg-deep; color: #F4F0EA; font-family: 'Mulish', sans-serif;
}

/* 1. BACKGROUND LAYER */
.bg-layer { position: absolute; inset: 0; pointer-events: none; z-index: 1;}
.grid-mesh { 
  position: absolute; inset: 0; 
  background-image: linear-gradient(rgba($gold, 0.03) 1px, transparent 1px), linear-gradient(90deg, rgba($gold, 0.03) 1px, transparent 1px); 
  background-size: 40px 40px; mask-image: radial-gradient(circle at 50% 30%, black, transparent 70%); 
}
.spotlight { 
  position: absolute; top: -15%; left: 50%; transform: translateX(-50%); 
  width: 100vw; height: 50vh; background: radial-gradient(circle, rgba($gold, 0.06), transparent 60%); filter: blur(50px); 
}

/* 2. WRAPPER & HEADER KÍNH MỜ */
.planner-wrapper {
  max-width: 1800px; height: calc(100vh - 80px); margin: 0 auto; padding: 20px 40px 40px; position: relative; z-index: 10;
}
.planner-content { height: 100%; display: flex; flex-direction: column; gap: 25px; }

.planner-header {
  display: flex; justify-content: space-between; align-items: flex-end;
  background: linear-gradient(135deg, rgba(20, 20, 22, 0.6), rgba(10, 10, 12, 0.8)); 
  padding: 25px 40px; border-radius: 24px; border: 1px solid $border-glass; 
  box-shadow: 0 15px 35px rgba(0,0,0,0.5); backdrop-filter: blur(20px);
}

.brand-tag { 
  display: inline-flex; align-items: center; gap: 8px; font-size: 0.75rem; font-weight: 800; color: $gold; 
  letter-spacing: 3px; margin-bottom: 12px; text-transform: uppercase;
}
.main-title { font-family: 'Playfair Display', serif; font-size: 3.2rem; font-weight: 900; color: #FFF; margin: 0; line-height: 1; letter-spacing: -1px; }
.text-serif { font-style: italic; color: $gold; font-weight: 400;}

/* Toggle Tuần (Apple Style) */
.week-selector { 
  display: flex; align-items: center; gap: 10px; background: rgba(0,0,0,0.4); 
  padding: 6px; border-radius: 40px; border: 1px solid $border-glass; 
}
.week-info { display: flex; flex-direction: column; align-items: center; padding: 0 30px; min-width: 200px; }
.week-label { font-size: 0.7rem; font-weight: 800; color: #a1a1aa; letter-spacing: 2px; margin-bottom: 2px; }
.week-display { font-weight: 800; font-size: 1.1rem; color: #FFF; font-family: 'Playfair Display', serif; letter-spacing: 1px;}

.btn-arrow { 
  width: 44px; height: 44px; border-radius: 50%; border: 1px solid $border-glass; background: $surface-glass; 
  color: #FFF; cursor: pointer; transition: 0.3s; display: flex; justify-content: center; align-items: center; font-size: 1rem;
}
.btn-arrow:hover:not(:disabled) { background: $gold; color: #000; box-shadow: 0 5px 15px rgba($gold, 0.3); transform: scale(1.05); border-color: $gold;}

/* 3. BENTO GRID LỊCH TRÌNH 7 NGÀY */
.grid-wrapper { 
  flex: 1; overflow-x: auto; overflow-y: hidden;
  padding-bottom: 20px; 
}
.blueprint-grid { 
  display: grid; grid-template-columns: repeat(7, 1fr); gap: 20px; height: 100%; min-width: 1400px; 
}

/* Cột Ngày */
.day-column { 
  display: flex; flex-direction: column; gap: 20px;
  background: $surface-glass; border: 1px solid $border-glass;
  border-radius: 24px; padding: 25px 20px; backdrop-filter: blur(10px);
  transition: 0.4s ease;
  
  &:hover { border-color: rgba($gold, 0.25); background: rgba(255, 255, 255, 0.04); transform: translateY(-3px);}
  &.is-today { 
    background: linear-gradient(180deg, rgba($gold, 0.08) 0%, $surface-glass 100%);
    border-color: rgba($gold, 0.5); box-shadow: 0 10px 30px rgba($gold, 0.08);
  }
}

/* 🌟 TIÊU ĐỀ CỘT CĂN GIỮA TUYỆT ĐỐI */
.col-header { 
  display: flex; flex-direction: column; justify-content: center; align-items: center; 
  padding-bottom: 20px; border-bottom: 1px dashed rgba(255, 255, 255, 0.1); text-align: center;
}

.today-badge { 
  background: transparent; color: $gold; padding: 2px 12px; border-radius: 12px; 
  font-size: 0.65rem; font-weight: 800; letter-spacing: 2px; border: 1px solid rgba($gold, 0.4);
  margin-bottom: 12px; box-shadow: 0 0 10px rgba($gold, 0.1);
}

.day-indicator { display: flex; flex-direction: column; align-items: center; gap: 5px; }
.day-name { font-size: 0.8rem; font-weight: 800; color: #a1a1aa; letter-spacing: 2px; text-transform: uppercase; }
.day-date { font-family: 'Playfair Display', serif; font-size: 3.5rem; font-weight: 900; color: #FFF; line-height: 1; text-shadow: 0 5px 15px rgba(0,0,0,0.5);}

.is-today .day-name { color: $gold; }

/* Danh sách Slot Bữa ăn */
.slots-container { flex: 1; display: flex; flex-direction: column; gap: 25px; overflow-y: auto; padding-right: 5px; }
.slots-container::-webkit-scrollbar { width: 4px; }
.slots-container::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 10px; }

.meal-slot { display: flex; flex-direction: column; gap: 12px; }
.slot-header { display: flex; align-items: center; gap: 10px; }
.slot-label { font-size: 0.75rem; font-weight: 800; color: $gold; text-transform: uppercase; letter-spacing: 2px; }
.slot-line { flex: 1; height: 1px; background: linear-gradient(90deg, rgba($gold, 0.3), transparent); }

/* Thẻ Món Ăn Kính Mờ */
.dish-card { 
  position: relative; height: 160px; border-radius: 20px; overflow: hidden; cursor: pointer; 
  border: 1px solid $border-glass; transition: 0.4s cubic-bezier(0.19, 1, 0.22, 1); 
  background: #000; display: flex; flex-direction: column; justify-content: flex-end;
  
  &:hover { 
    border-color: $gold; transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.8);
    .dish-bg { transform: scale(1.08); filter: brightness(0.8); }
    .btn-remove { opacity: 1; transform: scale(1); }
  }

  &.is-completed {
    border-color: #22c55e;
    .dish-bg { filter: grayscale(0.8) brightness(0.4); }
    .dish-name { text-decoration: line-through; color: #a1a1aa; }
  }
}

.card-bg-wrapper { position: absolute; inset: 0; z-index: 1; overflow: hidden;}
.dish-bg { width: 100%; height: 100%; object-fit: cover; filter: brightness(0.6); transition: 0.7s cubic-bezier(0.19, 1, 0.22, 1); }
.dish-overlay { position: absolute; inset: 0; background: linear-gradient(0deg, rgba(0,0,0,0.95) 0%, rgba(0,0,0,0.3) 60%, transparent 100%); }

.dish-content { position: relative; z-index: 2; padding: 20px; }
.dish-cat { font-size: 0.7rem; font-weight: 800; color: $gold; letter-spacing: 1px; text-transform: uppercase; margin-bottom: 6px; display: block; opacity: 0.9;}
.dish-name { font-size: 1.15rem; font-weight: 700; color: #FFF; margin: 0; line-height: 1.3; font-family: 'Playfair Display', serif;}

/* Nút Action trên Thẻ */
.btn-action { 
  position: absolute; top: 12px; width: 36px; height: 36px; border-radius: 50%; 
  display: flex; align-items: center; justify-content: center; cursor: pointer; 
  transition: 0.3s; border: none; z-index: 10; backdrop-filter: blur(10px);
}
.btn-check { 
  left: 12px; background: rgba(0,0,0,0.5); color: #fff; border: 1px solid rgba(255,255,255,0.2); 
  .circle-outline { width: 16px; height: 16px; border: 2px solid #fff; border-radius: 50%; }
}
.is-completed .btn-check { background: #22c55e; border-color: #22c55e; color: #fff; }

.btn-remove { 
  right: 12px; background: rgba(239, 68, 68, 0.8); color: #FFF; border: 1px solid #ef4444;
  opacity: 0; transform: scale(0.8); 
  &:hover { background: #ef4444; transform: scale(1.1) !important;}
}

/* 🌟 Nút Thêm Món (Thiết kế mới tinh tế hơn) */
.btn-add-slot { 
  width: 100%; height: 90px; 
  border: 1px dashed rgba(255, 255, 255, 0.2); 
  background: rgba(255, 255, 255, 0.015); 
  border-radius: 20px; cursor: pointer; transition: 0.4s ease; color: #a1a1aa; 
  display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px;
  
  i { font-size: 1.2rem; transition: 0.4s; color: rgba(255,255,255,0.4); }
  span { font-size: 0.8rem; font-weight: 700; letter-spacing: 1px; text-transform: uppercase; }
  
  &:hover { 
    background: rgba($gold, 0.05); border-color: rgba($gold, 0.4); color: $gold; 
    transform: translateY(-3px); box-shadow: 0 10px 20px rgba(0,0,0,0.2);
    i { transform: rotate(90deg); color: $gold; }
  }
}

/* =======================================================
   🔥 HỆ THỐNG RESPONSIVE
======================================================= */
@media (max-width: 1440px) {
  .planner-wrapper { padding: 20px; }
  .blueprint-grid { min-width: 1200px; }
  .main-title { font-size: 2.8rem; }
  .day-date { font-size: 3rem; }
}

@media (max-width: 1024px) {
  .gomet-planner-pro { height: auto; min-height: 100vh; overflow-y: auto; }
  .planner-wrapper { height: auto; }
  
  .planner-header { flex-direction: column; align-items: flex-start; gap: 25px; }
  .week-selector { width: 100%; justify-content: space-between; }
  .week-info { flex: 1; min-width: auto; }
}

@media (max-width: 768px) {
  .blueprint-grid { min-width: 1500px; gap: 15px; } 
  .day-column { padding: 20px 15px; border-radius: 20px;}
  .day-date { font-size: 2.8rem; }
  .dish-card { height: 140px; }
}

@media (max-width: 480px) {
  .gomet-planner-pro { padding-top: 60px; }
  .planner-header { padding: 20px; border-radius: 20px; }
  
  .main-title { font-size: 2rem; }
  .btn-arrow { width: 38px; height: 38px; font-size: 0.8rem; }
  .week-display { font-size: 0.95rem; }
  
  .dish-name { font-size: 1rem; }
  .btn-add-slot { height: 80px; }
}

/* Thanh cuộn ngang thanh lịch */
.custom-scroll-x::-webkit-scrollbar { height: 8px; }
.custom-scroll-x::-webkit-scrollbar-track { background: rgba(255, 255, 255, 0.03); border-radius: 10px; margin: 0 20px; }
.custom-scroll-x::-webkit-scrollbar-thumb { background: rgba($gold, 0.3); border-radius: 10px; border: 2px solid #09090b;}
.custom-scroll-x::-webkit-scrollbar-thumb:hover { background: rgba($gold, 0.6); }
</style>