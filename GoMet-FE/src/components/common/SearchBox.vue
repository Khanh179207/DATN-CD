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
        <span>{{ $t('nav.search') }}</span>
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
import { ref, computed, watch, onMounted, onUnmounted } from 'vue' // Đã thêm onMounted, onUnmounted
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const router = useRouter()
const authStore = useAuthStore()

// Ref cho wrapper bao bọc ngoài cùng
const searchWrapper = ref(null)

const searchKeyword = ref('')
const searchHistory = ref([])
const showSearchHistory = ref(false)
const isSearchFocused = ref(false)
const isLoadingHistory = ref(false)
const errorMessage = ref('')
const inputRef = ref(null)

const getAccountId = () => authStore.user?.accountID || authStore.user?.id

// Load dữ liệu
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

// Xử lý gợi ý
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

  // 1. TẨY TRẦN (Escape HTML): Chặn đứng mọi loại Script độc hại
  const escapedText = String(text)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");

  try {
    // 2. BÔI ĐẬM: Tìm từ khóa (không phân biệt hoa thường) và bọc trong <strong>
    const regex = new RegExp(`(${keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi')
    return escapedText.replace(regex, '<strong>$1</strong>')
  } catch (e) {
    // Phòng trường hợp user nhập ký tự regex lỗi
    return escapedText
  }
}
// Xử lý Actions
const handleSearch = () => {
  const query = searchKeyword.value.trim()
  if (!query) return

  // Cập nhật Optimistic UI
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

// ==========================================
// XỬ LÝ CLICK OUTSIDE (BẤM RA NGOÀI ĐỂ ĐÓNG)
// ==========================================
const handleClickOutside = (event) => {
  // Nếu khung tìm kiếm đang mở VÀ click chuột không nằm trong searchWrapper
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
// ==========================================

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

.search-premium-container { flex: 1; display: flex; justify-content: center; position: relative; z-index: 1001; }
.search-pill { display: flex; align-items: center; width: 100%; max-width: 500px; height: 48px; background: var(--color-neutral-100); border-radius: 24px; padding: 4px; border: 1.5px solid transparent; transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); position: relative; z-index: 1002; }
.search-pill.is-active { background: #ffffff; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08); }
.input-section { display: flex; align-items: center; flex: 1; height: 100%; padding-left: 16px; }
.icon-prefix { color: var(--color-neutral-400); display: flex; margin-right: 8px; }
.input-section input { flex: 1; border: none; background: transparent; height: 100%; padding: 0 8px; font-size: 15px; font-weight: 500; color: var(--color-neutral-900); outline: none; }
.input-section input::placeholder { color: var(--color-neutral-400); font-weight: 400; }
.btn-search-action { height: 40px; padding: 0 24px; background: #ea580c; color: white; border: none; border-radius: 20px; font-weight: 700; font-size: 14px; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 8px rgba(234, 88, 12, 0.2); }
.btn-search-action:hover { background: #c2410c; transform: translateY(-1px); }
.btn-search-action:active { transform: scale(0.98); }
.btn-clear { background: none; border: none; color: var(--color-neutral-300); display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: 50%; transition: all 0.2s; }
.btn-clear:hover { background: var(--color-neutral-200); color: var(--color-neutral-600); }
.history-card { position: absolute; top: calc(100% + 12px); left: 0; width: 100%; background: #ffffff; border-radius: 20px; box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12); border: 1px solid rgba(0, 0, 0, 0.04); overflow: hidden; padding: 8px 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; padding: 12px 20px 8px; }
.card-header .label { font-size: 11px; font-weight: 800; color: #a1a1aa; letter-spacing: 0.05em; }
.card-header .btn-clear-all { font-size: 11px; font-weight: 700; color: #ea580c; cursor: pointer; }
.card-header .btn-clear-all:hover { text-decoration: underline; }
.history-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 20px; cursor: pointer; transition: all 0.2s ease; }
.history-item:hover { background-color: #fdf4f1; }
.history-item:hover .keyword-text { color: #ea580c; }
.history-item:hover .btn-delete-single { opacity: 1; }
.history-item.suggest-item:hover { background-color: #f0f0f0; }
.history-item.suggest-item:hover .keyword-text { color: #333; }
.item-content { display: flex; align-items: center; gap: 12px; flex: 1; min-width: 0; }
.item-content .icon-clock, .item-content .icon-search { color: #d4d4d8; flex-shrink: 0; }
.item-content .keyword-text { font-size: 14px; font-weight: 500; color: #3f3f46; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.item-content .keyword-text :deep(strong) { font-weight: 700; color: #ea580c; }
.btn-delete-single { background: rgba(0, 0, 0, 0.03); border: none; color: #a1a1aa; width: 26px; height: 26px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; opacity: 0; transition: all 0.2s; }
.btn-delete-single:hover { background: #fee2e2; color: #ef4444; }
.history-loading, .history-empty, .history-error { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 24px; font-size: 14px; gap: 12px; }
.history-loading, .history-empty { color: #a1a1aa; }
.history-error { color: #ef4444; }
.btn-retry { padding: 6px 16px; background: #f3f4f6; border: 1px solid #d1d5db; border-radius: 20px; font-size: 12px; font-weight: 500; cursor: pointer; transition: all 0.2s; }
.btn-retry:hover { background: #e5e7eb; }
.loading-spinner { width: 24px; height: 24px; border: 2px solid #f3f3f3; border-top: 2px solid #ea580c; border-radius: 50%; animation: spin 0.8s linear infinite; }

@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

.search-pop-enter-active { transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1); }
.search-pop-enter-from, .search-pop-leave-to { opacity: 0; transform: translateY(10px) scale(0.98); }
</style>