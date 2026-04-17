<template>
  <div class="search-premium-container" ref="searchWrapper">
    <div class="search-pill" :class="{ 'is-active': isSearchFocused || showSearchHistory }">
      <div class="input-section">
        <div class="icon-prefix">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="8" /><line x1="21" y1="21" x2="16.65" y2="16.65" /></svg>
        </div>

        <input
          ref="inputRef"
          type="text"
          v-model="searchKeyword"
          placeholder="Tìm công thức, người dùng..."
          @focus="openSearchHistory"
          @keyup.enter="handleSearch"
          spellcheck="false"
        />

        <button v-if="searchKeyword" class="btn-clear" @click="clearKeyword" type="button">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" /></svg>
        </button>
      </div>

      <button class="btn-search-action" @click.stop="handleSearch" type="button">
        <span class="btn-label">{{ $t('nav.search') }}</span>
        <svg class="mobile-search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><circle cx="11" cy="11" r="8" /><line x1="21" y1="21" x2="16.65" y2="16.65" /></svg>
      </button>

      <transition name="search-pop">
        <div v-if="showSearchHistory" class="history-card" @mousedown.prevent>
          
          <div v-if="isLoadingHistory" class="history-loading">
            <div class="loading-spinner"></div>
            <span>Đang tải...</span>
          </div>

          <div v-else-if="errorMessage" class="history-error">
            <span>{{ errorMessage }}</span>
            <button class="btn-retry" @click="loadSearchHistory">Thử lại</button>
          </div>

          <div v-else-if="suggestions.length > 0">
            <div class="card-header">
              <span class="label">{{ searchKeyword.trim() ? 'GỢI Ý TÌM KIẾM' : 'TÌM KIẾM GẦN ĐÂY' }}</span>
              <span v-if="!searchKeyword.trim()" class="btn-clear-all" @click.stop="clearAllSearchHistory">Xóa tất cả</span>
            </div>

            <div class="history-list">
              <div v-for="item in suggestions" :key="item.id" class="history-item" :class="{ 'suggest-item': item.type === 'suggest' }" @click.stop="selectSuggestion(item)">
                <div class="item-content">
                  <svg v-if="item.type === 'history'" class="icon-clock" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" /></svg>
                  <svg v-else class="icon-search" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8" /><line x1="21" y1="21" x2="16.65" y2="16.65" /></svg>
                  <span class="keyword-text" v-html="highlightText(item.displayText || item.keyword)"></span>
                </div>
                
                <button v-if="item.type === 'history'" class="btn-delete-single" @click.stop="removeSearchHistoryItem(item.searchId)" type="button">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" /></svg>
                </button>
              </div>
            </div>
          </div>

          <div v-else class="history-empty">
            <span>{{ searchKeyword.trim() ? 'Không có gợi ý nào' : 'Không có lịch sử tìm kiếm' }}</span>
          </div>
          
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const router = useRouter()
const authStore = useAuthStore()

const searchWrapper = ref(null)
const searchKeyword = ref('')
const searchHistory = ref([])
const showSearchHistory = ref(false)
const isSearchFocused = ref(false)
const isLoadingHistory = ref(false)
const errorMessage = ref('')
const inputRef = ref(null)

const getAccountId = () => authStore.user?.accountID || authStore.user?.id

const loadSearchHistory = async () => {
  const accId = getAccountId()
  if (!accId) return;
  isLoadingHistory.value = true
  errorMessage.value = ''
  try {
    const res = await api.get(`/api/search-history/${accId}`)
    searchHistory.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    errorMessage.value = 'Không thể kết nối máy chủ'
    searchHistory.value = []
  } finally {
    isLoadingHistory.value = false
  }
}

const suggestions = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  let filtered = searchHistory.value || []
  if (keyword) {
    filtered = filtered.filter(item => item.keyword.toLowerCase().includes(keyword))
  }
  filtered = filtered.slice(0, 5).map(item => ({ ...item, type: 'history', id: `hist-${item.searchId}` }))
  if (keyword && !filtered.some(item => item.keyword.toLowerCase() === keyword)) {
    if (filtered.length < 5) {
      filtered.push({ type: 'suggest', id: `suggest-${Date.now()}`, keyword: keyword, displayText: `Tìm kiếm "${searchKeyword.value.trim()}"` })
    }
  }
  return filtered
})

const highlightText = (text) => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) return text
  const escapedText = String(text)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
  try {
    const regex = new RegExp(`(${keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi')
    return escapedText.replace(regex, '<strong>$1</strong>')
  } catch (e) {
    return escapedText
  }
}

const handleSearch = () => {
  const query = searchKeyword.value.trim()
  if (!query) return
  const exists = searchHistory.value.find(h => h.keyword === query)
  if (!exists) {
    searchHistory.value = [{ searchId: Date.now(), keyword: query }, ...searchHistory.value]
  }
  const accId = getAccountId()
  if (accId) api.post(`/api/search-history?accountId=${accId}&keyword=${encodeURIComponent(query)}`).catch(() => {})
  closeSearch()
  router.push({ name: 'Search', query: { q: query } })
}

const selectSuggestion = (item) => {
  searchKeyword.value = item.keyword
  handleSearch()
}

const removeSearchHistoryItem = (searchId) => {
  searchHistory.value = searchHistory.value.filter(h => h.searchId !== searchId)
  api.delete(`/api/search-history/${searchId}`).catch(() => {})
}

const clearAllSearchHistory = () => {
  searchHistory.value = []
  const accId = getAccountId()
  if (accId) api.delete(`/api/search-history/clear/${accId}`).catch(() => {})
}

const clearKeyword = () => {
  searchKeyword.value = ''
  inputRef.value?.focus()
}

const openSearchHistory = () => {
  showSearchHistory.value = true
  isSearchFocused.value = true
  if (searchHistory.value.length === 0 && !isLoadingHistory.value) loadSearchHistory()
}

const closeSearch = () => {
  showSearchHistory.value = false
  isSearchFocused.value = false
}

const handleClickOutside = (event) => {
  if ((isSearchFocused.value || showSearchHistory.value) && searchWrapper.value && !searchWrapper.value.contains(event.target)) {
    closeSearch()
  }
}

onMounted(() => {
  document.addEventListener('mousedown', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('mousedown', handleClickOutside)
})

watch(() => authStore.user, (newUser) => {
  if (newUser && (newUser.accountID || newUser.id)) {
    loadSearchHistory()
  } else {
    searchHistory.value = []
    errorMessage.value = ''
  }
}, { immediate: true, deep: true })

</script>

<style scoped lang="scss">
@use '../../assets/styles/variables' as *;

.search-premium-container { flex: 1; display: flex; position: relative; z-index: 1001; min-width: 0; }
.search-pill { display: flex; align-items: center; width: 100%; max-width: 650px; height: 48px; background: var(--color-neutral-100); border-radius: 24px; padding: 4px; border: 1.5px solid transparent; transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); position: relative; z-index: 1002; }
.search-pill.is-active { background: #ffffff; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08); }
.input-section { display: flex; align-items: center; flex: 1; height: 100%; padding-left: 16px; min-width: 0; }
.icon-prefix { color: var(--color-neutral-400); display: flex; margin-right: 8px; flex-shrink: 0; }
.input-section input { flex: 1; border: none; background: transparent; height: 100%; padding: 0 8px; font-size: 15px; font-weight: 500; color: var(--color-neutral-900); outline: none; min-width: 0; }
.input-section input::placeholder { color: var(--color-neutral-400); font-weight: 400; }

.btn-search-action { 
  height: 40px; padding: 0 24px; background: #ea580c; color: white; border: none; 
  border-radius: 20px; font-weight: 700; font-size: 14px; cursor: pointer; 
  transition: all 0.2s; box-shadow: 0 2px 8px rgba(234, 88, 12, 0.2); flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
}
.mobile-search-icon { display: none; } 

.btn-search-action:hover { background: #c2410c; transform: translateY(-1px); }
.btn-search-action:active { transform: scale(0.98); }
.btn-clear { background: none; border: none; color: var(--color-neutral-300); display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: 50%; transition: all 0.2s; flex-shrink: 0; }
.btn-clear:hover { background: var(--color-neutral-200); color: var(--color-neutral-600); }

/* --- CSS LỊCH SỬ TÌM KIẾM (MẶC ĐỊNH TRÊN DESKTOP) --- */
.history-card { 
  position: absolute; top: calc(100% + 8px); left: 0; right: 0; 
  background: #ffffff; border-radius: 16px; 
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15); 
  border: 1px solid rgba(0, 0, 0, 0.05); overflow: hidden; padding: 6px 0; 
}
.card-header { display: flex; justify-content: space-between; align-items: center; padding: 10px 16px 6px; }
.card-header .label { font-size: 10px; font-weight: 800; color: #a1a1aa; letter-spacing: 0.05em; text-transform: uppercase; }
.card-header .btn-clear-all { font-size: 10px; font-weight: 700; color: #ea580c; cursor: pointer; }

.history-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 16px; cursor: pointer; transition: 0.2s; }
.history-item:hover { background-color: #f8fafc; }
.item-content { display: flex; align-items: center; gap: 10px; flex: 1; min-width: 0; }
.item-content .keyword-text { font-size: 14px; font-weight: 500; color: #334155; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.item-content .keyword-text :deep(strong) { font-weight: 700; color: #ea580c; }
.btn-delete-single { background: none; border: none; color: #cbd5e1; width: 24px; height: 24px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; }
.btn-delete-single:hover { background: #f1f5f9; color: #ef4444; }

.history-loading, .history-empty, .history-error { padding: 20px; text-align: center; font-size: 13px; color: #94a3b8; }

@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
.search-pop-enter-active { transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
.search-pop-enter-from, .search-pop-leave-to { opacity: 0; transform: translateY(10px) scale(0.98); }

/* ============================================================
   🌟 RESPONSIVE CHO TÌM KIẾM GẦN ĐÂY (MOBILE & TABLET)
   ============================================================ */

@media (max-width: 768px) {
  /* Ô Search gọn gàng hơn */
  .search-pill { height: 40px; }
  .input-section { padding-left: 12px; }
  .icon-prefix { display: none; } /* Ẩn icon kính lúp đầu dòng cho rộng chỗ */

  /* Nút search chuyển thành icon tròn */
  .btn-search-action {
    width: 32px; height: 32px; padding: 0; border-radius: 50%;
    .btn-label { display: none; }
    .mobile-search-icon { display: block; }
  }

  .history-card {
    position: fixed;
    top: 65px;
    left: 10px;
    right: 10px;
    width: auto;
    max-width: none;
    border-radius: 12px;
    z-index: 2000;
    box-shadow: 0 10px 30px rgba(0,0,0,0.25);
  }

  .item-content .keyword-text { font-size: 14px; }
  .card-header .label, .card-header .btn-clear-all { font-size: 11px; }
}

@media (max-width: 480px) {
  .card-header { padding: 12px 16px; }
  .history-item { padding: 12px 16px; }
  .input-section input::placeholder { font-size: 12px; }
}
</style>