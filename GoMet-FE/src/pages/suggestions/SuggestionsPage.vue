<template>
  <div class="gomet-vault">
    
    <div class="bg-architecture">
      <div class="grid-lines"></div>
      <div class="noise-film"></div>
      <div class="glow-spot top-right"></div>
      <div class="glow-spot bottom-left"></div>
    </div>

    <div class="vault-interface">
      
      <header class="vault-header">
        <div class="header-left">
          <button v-if="activeMode !== 'hub'" class="btn-back" @click="returnToHub">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M5 12L12 19M5 12L12 5"/></svg>
            <span>{{ $t('suggestions.go_back') }}</span>
          </button>
          <div class="brand-box" v-else>
            <span class="brand-sub">{{ $t('suggestions.entertainment') }}</span>
            <h1 class="brand-main">{{ $t('suggestions.arcade_zone') }}</h1>
          </div>
        </div>
        
        <div class="header-right">
          <div class="user-pill">
            <span class="u-name">{{ authStore.currentUser?.name || 'Chef' }}</span>
            <span class="u-status">{{ $t('suggestions.online') }}</span>
          </div>
        </div>
      </header>

      <transition name="fade-up" mode="out-in">
        <div v-if="activeMode === 'hub'" class="hub-container">
          <div class="cards-wrapper">
            
            <div class="game-card" @click="enterGame('spin')">
              <div class="card-icon">
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><path d="M12 6V12L16 14"/></svg>
              </div>
              <div class="card-info">
                <h3>{{ $t('suggestions.spin_title') }}</h3>
                <p>{{ $t('suggestions.spin_desc') }}</p>
              </div>
              <div class="card-arrow">→</div>
            </div>

            <div class="game-card" @click="enterGame('quiz')">
              <div class="card-icon">
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
              </div>
              <div class="card-info">
                <h3>{{ $t('suggestions.quiz_title') }}</h3>
                <p>{{ $t('suggestions.quiz_desc') }}</p>
              </div>
              <div class="card-arrow">→</div>
            </div>

            <div class="game-card locked">
              <div class="card-icon">
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              </div>
              <div class="card-info">
                <h3>{{ $t('suggestions.tarot_title') }}</h3>
                <p>{{ $t('suggestions.tarot_desc') }}</p>
              </div>
            </div>

          </div>
        </div>
      </transition>

      <transition name="fade-scale" mode="out-in">
        <div v-if="activeMode === 'spin'" class="game-stage">
          <div class="machine-container">
            <div class="dish-frame">
              <div class="scan-line" v-if="isSpinning"></div>
              <img :src="currentDisplayDish.image" class="dish-img" :class="{ 'blur-motion': isSpinning }" />
              <div class="dish-tag" v-if="!isSpinning">{{ currentDisplayDish.category }}</div>
            </div>
            
            <h2 class="dish-name-display">
              {{ isSpinning ? $t('suggestions.searching') : currentDisplayDish.name }}
            </h2>

            <button class="btn-spin" @click="spinWheel" :disabled="isSpinning">
              <span v-if="!isSpinning">{{ $t('suggestions.spin_btn') }}</span>
              <span v-else>{{ $t('suggestions.spinning') }}</span>
            </button>
            <p class="hint-text">{{ $t('suggestions.hint') }}</p>
          </div>
        </div>
      </transition>

      <transition name="fade-scale" mode="out-in">
        <div v-if="activeMode === 'quiz'" class="game-stage">
          <div class="quiz-container">
            
            <div class="quiz-progress">
              <div class="progress-bar" :style="{ width: `${(quizStep / 3) * 100}%` }"></div>
            </div>

            <div class="quiz-content">
              <span class="step-label">{{ $t('suggestions.q_step') }} {{ quizStep }}/3</span>
              
              <h2 class="quiz-question" v-if="quizStep === 1" v-html="$t('suggestions.quiz_q1')"></h2>
              <h2 class="quiz-question" v-if="quizStep === 2" v-html="$t('suggestions.quiz_q2')"></h2>
              <h2 class="quiz-question" v-if="quizStep === 3" v-html="$t('suggestions.quiz_q3')"></h2>
              <h2 class="quiz-question" v-if="quizStep === 4">{{ $t('suggestions.quiz_q4') }}</h2>

              <div class="quiz-options" v-if="quizStep < 4">
                <template v-if="quizStep === 1">
                  <button class="opt-btn" @click="answerQuiz('style', 'Dry')">{{ $t('suggestions.opt_dry') }}</button>
                  <button class="opt-btn" @click="answerQuiz('style', 'Soupy')">{{ $t('suggestions.opt_soupy') }}</button>
                </template>
                <template v-if="quizStep === 2">
                  <button class="opt-btn" @click="answerQuiz('flavor', 'Bold')">{{ $t('suggestions.opt_bold') }}</button>
                  <button class="opt-btn" @click="answerQuiz('flavor', 'Mild')">{{ $t('suggestions.opt_mild') }}</button>
                </template>
                <template v-if="quizStep === 3">
                  <button class="opt-btn" @click="answerQuiz('main', 'Meat')">{{ $t('suggestions.opt_meat') }}</button>
                  <button class="opt-btn" @click="answerQuiz('main', 'Seafood')">{{ $t('suggestions.opt_seafood') }}</button>
                </template>
              </div>
            </div>

          </div>
        </div>
      </transition>

    </div>

    <transition name="modal-fade">
      <div v-if="showResult" class="modal-overlay">
        <div class="modal-card">
          <button class="btn-close" @click="closeResult">✕</button>
          
          <div class="modal-body">
            <div class="modal-img-col">
              <img :src="resultDish.image" />
            </div>
            <div class="modal-info-col">
              <div class="info-top">
                <span class="match-badge">{{ $t('suggestions.match_pct') }}</span>
                <h2>{{ resultDish.name }}</h2>
                <p>{{ resultDish.description }}</p>
              </div>
              
              <div class="info-grid">
                <div class="i-item">
                  <label>{{ $t('suggestions.lbl_time') }}</label>
                  <span>{{ resultDish.time }}</span>
                </div>
                <div class="i-item">
                  <label>{{ $t('suggestions.lbl_diff') }}</label>
                  <span>{{ resultDish.difficulty }}</span>
                </div>
                <div class="i-item">
                  <label>{{ $t('suggestions.lbl_flavor') }}</label>
                  <span>{{ resultDish.flavor }}</span>
                </div>
              </div>

              <div class="info-actions">
                <button class="btn-view-recipe" @click="goToDetail">{{ $t('suggestions.view_recipe') }}</button>
                <button class="btn-retry" @click="retryGame">{{ $t('suggestions.try_again') }}</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getSuggestedPosts, normalizePost } from '@/services/postService'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

// Fallback dishes when API has no data
const FALLBACK = [
  { id: 1, name: 'Hanoi Beef Pho', category: 'Vietnamese', time: '4 hr', difficulty: 'Hard', image: 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=600', description: 'A Vietnamese culinary gem with a clear, subtly sweet broth.' },
  { id: 2, name: 'Beef Wellington', category: 'Western', time: '2 hr', difficulty: 'Hard', image: 'https://images.unsplash.com/photo-1546964124-0cce460f38ef?w=600', description: 'Tender beef tenderloin wrapped in a golden, flaky pastry crust.' },
  { id: 3, name: 'Salmon Sashimi', category: 'Japanese', time: '15 min', difficulty: 'Easy', image: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600', description: 'Fresh sashimi-grade salmon that preserves its natural sweetness.' },
  { id: 4, name: 'Pasta Carbonara', category: 'Western', time: '30 min', difficulty: 'Medium', image: 'https://images.unsplash.com/photo-1612874742237-6526221588e3?w=600', description: 'Classic Italian creamy egg and cheese sauce.' }
]

const dishes = ref([...FALLBACK])

async function loadDishes() {
  try {
    const raw = await getSuggestedPosts({ limit: 20 })
    if (raw?.length) {
      dishes.value = raw.map(dto => ({
        id:          dto.postID,
        name:        dto.title,
        category:    dto.categoryName || '',
        time:        dto.cookingTime ? `${dto.cookingTime} min` : '—',
        difficulty:  { 1: 'Easy', 2: 'Medium', 3: 'Hard' }[dto.level] || 'Medium',
        flavor:      '—',
        image:       dto.media || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600',
        description: dto.description || 'A delicious recipe on GoMet.'
      }))
    }
  } catch {
    // keep fallback
  }
}

const activeMode = ref('hub')
const showResult = ref(false)
const resultDish = ref(dishes.value[0])
const isSpinning = ref(false)
const currentDisplayDish = ref(dishes.value[0])
const quizStep = ref(1)
const quizAnswers = ref({})

const enterGame = (mode) => { activeMode.value = mode; if (mode === 'quiz') { quizStep.value = 1; quizAnswers.value = {} } }
const returnToHub = () => { activeMode.value = 'hub'; showResult.value = false }
const closeResult = () => (showResult.value = false)
const retryGame = () => { closeResult(); if (activeMode.value === 'quiz') quizStep.value = 1 }
const goToDetail = () => router.push(`/home/post/${resultDish.value.id}`)

const spinWheel = () => {
  if (isSpinning.value) return
  isSpinning.value = true
  let count = 0
  const interval = setInterval(() => {
    currentDisplayDish.value = dishes.value[Math.floor(Math.random() * dishes.value.length)]
    count++
    if (count > 20) {
      clearInterval(interval)
      isSpinning.value = false
      resultDish.value = currentDisplayDish.value
      showResult.value = true
    }
  }, 80)
}

const answerQuiz = (key, val) => {
  quizAnswers.value[key] = val
  if (quizStep.value < 3) {
    quizStep.value++
  } else {
    quizStep.value = 4
    setTimeout(() => {
      resultDish.value = dishes.value[Math.floor(Math.random() * dishes.value.length)]
      showResult.value = true
    }, 1000)
  }
}

onMounted(loadDishes)
</script>

<style scoped>
/* --- 1. CORE LAYOUT (NO SCROLLBAR) --- */
.gomet-vault {
  width: 100%;
  height: 100vh; /* Lock height to viewport */
  overflow: hidden; /* Hide all scrollbars */
  background: #FBF6F1; /* Elegant cream background */
  color: #1E293B;
  font-family: 'Manrope', sans-serif;
  position: relative;
  display: flex;
  flex-direction: column;
}

/* Background Elements */
.bg-architecture { position: absolute; inset: 0; pointer-events: none; }
.grid-lines {
  position: absolute; inset: 0;
  background-image: linear-gradient(rgba(0,0,0,0.03) 1px, transparent 1px),
  linear-gradient(90deg, rgba(0,0,0,0.03) 1px, transparent 1px);
  background-size: 40px 40px;
}
.noise-film {
  position: absolute; inset: 0; opacity: 0.04;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
}
.glow-spot { position: absolute; width: 50vw; height: 50vw; border-radius: 50%; filter: blur(100px); opacity: 0.4; }
.top-right { top: -20%; right: -20%; background: #FFEDD5; }
.bottom-left { bottom: -20%; left: -20%; background: #E0E7FF; }

/* --- 2. HEADER --- */
.vault-interface {
  flex: 1; /* Fill remaining space */
  display: flex;
  flex-direction: column;
  align-items: center; /* Horizontally centered */
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px;
  box-sizing: border-box;
}

.vault-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(0,0,0,0.06);
}

.brand-sub { font-size: 0.7rem; letter-spacing: 2px; font-weight: 700; color: #94A3B8; text-transform: uppercase; }
.brand-main { font-family: 'Playfair Display', serif; font-size: 2rem; font-weight: 900; margin: 0; color: #1E293B; }

.btn-back {
  background: none; border: none; display: flex; align-items: center; gap: 8px;
  font-size: 0.9rem; font-weight: 600; color: #64748B; cursor: pointer; transition: 0.3s;
}
.btn-back:hover { color: #EA580C; transform: translateX(-5px); }

.user-pill { font-size: 0.8rem; font-weight: 600; color: #64748B; display: flex; align-items: center; gap: 8px; }
.u-status { color: #10B981; font-size: 0.7rem; }

/* --- 3. HUB (LOBBY) --- */
.hub-container {
  width: 100%;
  flex: 1; /* Stretch to vertically center content */
  display: flex;
  align-items: center;
  justify-content: center;
}

.cards-wrapper {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 30px; width: 100%;
}

.game-card {
  background: rgba(255,255,255,0.6); border: 1px solid rgba(255,255,255,0.8);
  border-radius: 20px; padding: 30px; cursor: pointer; position: relative;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  display: flex; flex-direction: column; gap: 20px;
  height: 350px; justify-content: space-between;
}
.game-card:hover {
  background: #FFF; transform: translateY(-8px);
  box-shadow: 0 15px 40px rgba(0,0,0,0.05); border-color: #EA580C;
}
.game-card.locked { opacity: 0.6; cursor: not-allowed; filter: grayscale(1); }

.card-icon { color: #1E293B; transition: 0.3s; }
.game-card:hover .card-icon { color: #EA580C; }

.card-info h3 { font-family: 'Playfair Display', serif; font-size: 1.5rem; margin: 0 0 10px; color: #1E293B; }
.card-info p { font-size: 0.9rem; color: #64748B; line-height: 1.5; margin: 0; }

.card-arrow { align-self: flex-end; font-size: 1.5rem; color: #CBD5E1; transition: 0.3s; }
.game-card:hover .card-arrow { color: #EA580C; transform: translateX(5px); }

/* --- 4. GAME STAGE (SPIN) --- */
.game-stage {
  flex: 1; display: flex; justify-content: center; align-items: center; width: 100%;
}

.machine-container { text-align: center; display: flex; flex-direction: column; align-items: center; }

.dish-frame {
  width: 320px; height: 320px; position: relative;
  border-radius: 20px; overflow: hidden; margin-bottom: 30px;
  box-shadow: 0 20px 50px rgba(0,0,0,0.1); border: 4px solid #FFF;
}
.dish-img { width: 100%; height: 100%; object-fit: cover; transition: 0.2s; }
.blur-motion { filter: blur(10px); transform: scale(1.1); }
.scan-line {
  position: absolute; top: 0; left: 0; width: 100%; height: 2px; background: #EA580C;
  z-index: 5; box-shadow: 0 0 15px #EA580C; animation: scan 1.5s infinite linear;
}
@keyframes scan { 0% { top: 0; opacity: 0; } 50% { opacity: 1; } 100% { top: 100%; opacity: 0; } }

.dish-tag {
  position: absolute; top: 15px; right: 15px;
  background: #1E293B; color: #FFF; padding: 4px 12px;
  border-radius: 20px; font-size: 0.7rem; font-weight: 700;
}

.dish-name-display {
  font-family: 'Playfair Display', serif; font-size: 2.5rem; margin: 0 0 30px;
  color: #1E293B; min-height: 3rem;
}

.btn-spin {
  padding: 16px 40px; border: none; border-radius: 50px;
  background: #EA580C; color: #FFF; font-weight: 800; font-size: 1rem; letter-spacing: 1px;
  cursor: pointer; transition: 0.3s; box-shadow: 0 10px 30px rgba(234, 88, 12, 0.2);
}
.btn-spin:hover:not(:disabled) { transform: translateY(-3px); box-shadow: 0 15px 40px rgba(234, 88, 12, 0.3); }
.btn-spin:disabled { background: #CBD5E1; cursor: wait; box-shadow: none; }

.hint-text { margin-top: 15px; font-size: 0.85rem; color: #94A3B8; font-style: italic; }

/* --- 5. GAME STAGE (QUIZ) --- */
.quiz-container { width: 100%; max-width: 700px; text-align: center; }

.quiz-progress { width: 100%; height: 2px; background: #E2E8F0; margin-bottom: 40px; border-radius: 2px; }
.progress-bar { height: 100%; background: #EA580C; transition: width 0.5s ease; }

.step-label { font-size: 0.7rem; font-weight: 800; color: #EA580C; letter-spacing: 2px; display: block; margin-bottom: 10px; }
.quiz-question { font-family: 'Playfair Display', serif; font-size: 3rem; margin-bottom: 50px; color: #1E293B; line-height: 1.2; }
.quiz-question em { color: #EA580C; font-style: italic; }

.quiz-options { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.opt-btn {
  padding: 25px; border: 1px solid #E2E8F0; background: #FFF; border-radius: 16px;
  font-size: 1.1rem; font-weight: 600; color: #1E293B; cursor: pointer; transition: 0.3s;
}
.opt-btn:hover { border-color: #EA580C; background: #FFF7ED; transform: translateY(-3px); }

/* --- 6. RESULT MODAL --- */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.7); z-index: 100;
  display: flex; align-items: center; justify-content: center; backdrop-filter: blur(8px);
}
.modal-card {
  width: 900px; height: 500px; background: #FFF; display: flex; position: relative;
  border-radius: 4px; overflow: hidden; box-shadow: 0 50px 100px rgba(0,0,0,0.2);
}
.btn-close {
  position: absolute; top: 20px; right: 20px; width: 40px; height: 40px;
  background: #F1F5F9; border: none; border-radius: 50%; cursor: pointer;
  display: flex; align-items: center; justify-content: center; font-size: 1.2rem;
  transition: 0.3s; z-index: 10;
}
.btn-close:hover { background: #E2E8F0; transform: rotate(90deg); }

.modal-body { display: flex; width: 100%; height: 100%; }
.modal-img-col { width: 45%; background: #000; }
.modal-img-col img { width: 100%; height: 100%; object-fit: cover; opacity: 0.9; }

.modal-info-col { width: 55%; padding: 50px; display: flex; flex-direction: column; justify-content: center; }
.match-badge { font-size: 0.75rem; font-weight: 800; color: #EA580C; letter-spacing: 2px; margin-bottom: 10px; display: block; }
.modal-info-col h2 { font-family: 'Playfair Display', serif; font-size: 2.5rem; margin: 0 0 15px; color: #1E293B; line-height: 1.1; }
.modal-info-col p { font-size: 1rem; color: #64748B; line-height: 1.6; margin-bottom: 30px; }

.info-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 40px; border-top: 1px solid #F1F5F9; border-bottom: 1px solid #F1F5F9; padding: 20px 0; }
.i-item { display: flex; flex-direction: column; }
.i-item label { font-size: 0.65rem; color: #94A3B8; font-weight: 700; text-transform: uppercase; margin-bottom: 5px; }
.i-item span { font-size: 0.95rem; font-weight: 700; color: #1E293B; }

.info-actions { display: flex; gap: 15px; }
.btn-view-recipe {
  flex: 1; padding: 14px; background: #1E293B; color: #FFF; border: none; font-weight: 700; cursor: pointer; transition: 0.3s;
}
.btn-view-recipe:hover { background: #EA580C; }
.btn-retry {
  padding: 14px 30px; background: transparent; border: 1px solid #E2E8F0; color: #64748B; font-weight: 700; cursor: pointer; transition: 0.3s;
}
.btn-retry:hover { border-color: #1E293B; color: #1E293B; }

/* Transitions */
.fade-up-enter-active, .fade-up-leave-active { transition: all 0.5s ease; }
.fade-up-enter-from { opacity: 0; transform: translateY(20px); }
.fade-up-leave-to { opacity: 0; transform: translateY(-20px); }

.fade-scale-enter-active, .fade-scale-leave-active { transition: all 0.4s ease; }
.fade-scale-enter-from { opacity: 0; transform: scale(0.95); }
.fade-scale-leave-to { opacity: 0; transform: scale(1.05); }

.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.3s; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
</style>