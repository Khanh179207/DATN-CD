<template>
  <transition name="fade-modal">
    <div v-if="isOpen" class="mealplan-modal-overlay" @click.self="$emit('close')">
      <div class="mealplan-modal">
        <div class="modal-header">
          <h3>{{ $t('mealplan.modal_title') }}</h3>
          <button class="btn-close" @click="$emit('close')">&times;</button>
        </div>

        <div class="modal-body" v-if="recipe">
          <div class="preview-recipe">
            <img :src="recipe.image || 'https://via.placeholder.com/60'" :alt="$t('common.recipe_image')" class="preview-img">
            <span class="preview-title">{{ recipe.title }}</span>
          </div>

          <div class="form-group">
            <label>{{ $t('mealplan.select_date') }}</label>
            <input type="date" v-model="planForm.date" class="input-date" :min="todayDate">
          </div>

          <div class="form-group">
            <label>{{ $t('mealplan.select_meal') }}</label>
            <div class="meal-type-selector">
              <label v-for="type in mealTypes" :key="type.value" 
                     class="type-btn" :class="{ active: planForm.type === type.value }">
                <input type="radio" v-model="planForm.type" :value="type.value" hidden>
                {{ type.label }}
              </label>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="$emit('close')">{{ $t('common.cancel') }}</button>
          <button class="btn-confirm" @click="submitMealPlan" :disabled="isSubmitting">
            {{ isSubmitting ? $t('common.loading') : $t('mealplan.save_plan') }}
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch } from 'vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  isOpen: Boolean,
  recipe: Object
})

const emit = defineEmits(['close', 'success'])
const authStore = useAuthStore()
const { t } = useI18n()

const todayDate = new Date().toISOString().split('T')[0]
const isSubmitting = ref(false)

const mealTypes = [
  { label: t('mealplan.slot_breakfast'), value: 'BREAKFAST' },
  { label: t('mealplan.slot_lunch'), value: 'LUNCH' },
  { label: t('mealplan.slot_dinner'), value: 'DINNER' }
]

const planForm = ref({
  date: todayDate,
  type: 'LUNCH'
})

// Reset form mỗi khi mở modal
watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    planForm.value.date = todayDate
    planForm.value.type = 'LUNCH'
  }
})

const submitMealPlan = async () => {
  // Kiểm tra đăng nhập
  const user = authStore.user
  const accountId = user?.accountID || user?.id
  if (!accountId) {
    toast.error(t('toast.need_login_mealplan'))
    return
  }

  isSubmitting.value = true
  try {
    const payload = {
      postId: props.recipe.id,
      planDate: planForm.value.date,
      mealType: planForm.value.type
    }
    await api.post(`/api/meal-plans/user/${accountId}`, payload)
    toast.success(t('mealplan.save_success'))
    emit('success')
    emit('close')
  } catch (error) {
    console.error(error)
    toast.error(t('toast.mealplan_save_failed'))
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
/* CSS cho Modal (Đã tách riêng) */
.mealplan-modal-overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.5); backdrop-filter: blur(5px); display: flex; align-items: center; justify-content: center; z-index: 9999; }
.mealplan-modal { background: white; width: 90%; max-width: 400px; border-radius: 20px; overflow: hidden; box-shadow: 0 20px 40px rgba(0,0,0,0.2); font-family: 'Mulish', sans-serif; }
.modal-header { padding: 20px 24px; border-bottom: 1px solid #F3F4F6; display: flex; justify-content: space-between; align-items: center; }
.modal-header h3 { margin: 0; font-size: 1.25rem; font-weight: 800; }
.btn-close { background: none; border: none; font-size: 1.5rem; color: #A8A29E; cursor: pointer; }
.modal-body { padding: 24px; display: flex; flex-direction: column; gap: 20px; }
.preview-recipe { display: flex; align-items: center; gap: 15px; padding: 12px; background: #FAFAF9; border-radius: 12px; border: 1px solid #E7E5E4; }
.preview-img { width: 60px; height: 60px; border-radius: 10px; object-fit: cover; }
.preview-title { font-weight: 700; color: #44403C; font-size: 0.95rem; }
.form-group { display: flex; flex-direction: column; gap: 8px; }
.form-group label { font-size: 0.85rem; font-weight: 700; color: #78716C; text-transform: uppercase; }
.input-date { padding: 12px 16px; border: 1px solid #E7E5E4; border-radius: 10px; font-size: 1rem; outline: none; }
.meal-type-selector { display: flex; gap: 10px; }
.type-btn { flex: 1; padding: 10px 0; text-align: center; border: 1px solid #E7E5E4; border-radius: 10px; font-weight: 700; cursor: pointer; }
.type-btn.active { background: #FFF7ED; border-color: #EA580C; color: #EA580C; }
.modal-footer { padding: 16px 24px; border-top: 1px solid #F3F4F6; display: flex; justify-content: flex-end; gap: 12px; background: #FAFAF9; }
.btn-cancel { padding: 10px 20px; border: none; background: transparent; font-weight: 700; color: #78716C; cursor: pointer; }
.btn-confirm { padding: 10px 24px; border: none; background: #EA580C; color: white; font-weight: 700; border-radius: 10px; cursor: pointer; }
.fade-modal-enter-active, .fade-modal-leave-active { transition: opacity 0.3s; }
.fade-modal-enter-from, .fade-modal-leave-to { opacity: 0; }
</style>