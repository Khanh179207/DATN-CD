<template>
  <transition name="modal-fade">
    <div v-if="show" class="vip-follow-overlay" @click.self="$emit('close')">
      <div class="vip-follow-container">
        <div class="edit-modal-header">
          <div class="header-left">
            <h2>{{ type === 'followers' ? 'Người theo dõi' : 'Đang theo dõi' }}</h2>
          </div>
          <button class="btn-close" @click="$emit('close')">✕</button>
        </div>
        
        <div class="edit-modal-body vip-follow-body">
          <div v-if="loading" class="follow-loading">
            <span class="spinner-xl"></span>
            <p>Đang tìm kiếm...</p>
          </div>
          
          <div v-else-if="list.length === 0" class="follow-empty">
            <div class="empty-icon">👥</div>
            <p>Danh sách này hiện đang trống</p>
          </div>
          
          <div v-else class="follow-list-grid">
            <div v-for="item in list" :key="item.accountID || item.id" 
                 class="follow-user-row" 
                 @click="$emit('go-to-profile', item.accountID || item.id)">
              <div class="user-avatar-wrap">
                <img :src="item.avatar || `https://ui-avatars.com/api/?name=${item.username}`" alt="Avatar">
                <div class="online-indicator"></div>
              </div>
              
              <div class="user-info">
                <span class="user-name">{{ item.username }}</span>
                <span class="user-handle">@{{ (item.username || '').toLowerCase().replace(/\s+/g,'_') }}</span>
              </div>
              
              <div class="user-action">
                <button class="btn-view-profile">
                  <span>Xem</span>
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="9 18 15 12 9 6"></polyline></svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
defineProps({ show: Boolean, type: String, list: Array, loading: Boolean })
defineEmits(['close', 'go-to-profile'])
</script>

<style scoped lang="scss">
.vip-follow-overlay { position: fixed; inset: 0; z-index: 1500; background: rgba(0, 0, 0, 0.6); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; padding: 20px; }
.vip-follow-container { background: #FFFFFF; width: 100%; max-width: 500px; border-radius: 24px; box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25); overflow: hidden; }
.edit-modal-header { padding: 20px 24px; border-bottom: 1px solid #E2E8F0; display: flex; justify-content: space-between; align-items: center; h2 { font-size: 1.25rem; font-weight: 800; color: #1E293B; margin: 0; } .btn-close { background: #F1F5F9; border: none; width: 32px; height: 32px; border-radius: 50%; color: #64748B; cursor: pointer; transition: 0.2s; &:hover { background: #E2E8F0; color: #1E293B; } } }
.vip-follow-body { padding: 16px 24px; max-height: 450px; overflow-y: auto; &::-webkit-scrollbar { width: 5px; } &::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; } }
.follow-list-grid { display: flex; flex-direction: column; gap: 12px; }
.follow-user-row { display: flex; align-items: center; gap: 16px; padding: 12px 16px; border-radius: 16px; cursor: pointer; transition: background 0.2s; &:hover { background: #F8FAFC; } .user-avatar-wrap { width: 50px; height: 50px; flex-shrink: 0; img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; box-shadow: 0 2px 8px rgba(0,0,0,0.1); } } .user-info { flex-grow: 1; display: flex; flex-direction: column; .user-name { font-weight: 700; color: #1E293B; font-size: 0.95rem; } .user-handle { color: #64748B; font-size: 0.8rem; } } .btn-view-profile { display: flex; align-items: center; gap: 4px; background: transparent; color: #64748B; border: none; font-size: 0.75rem; font-weight: 700; cursor: pointer; } }
.follow-loading, .follow-empty { padding: 50px 0; text-align: center; color: #94A3B8; .empty-icon { font-size: 3rem; margin-bottom: 10px; opacity: 0.5; } p { font-weight: 600; margin: 0; } }
.spinner-xl { width: 36px; height: 36px; border: 3px solid #F1F5F9; border-top-color: #1E293B; border-radius: 50%; animation: spin 1s infinite linear; margin: 0 auto 10px; display: block; }
@keyframes spin { to { transform: rotate(360deg); } }
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.25s ease; .vip-follow-container { transition: transform 0.25s, opacity 0.25s; } }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; .vip-follow-container { transform: scale(0.94) translateY(16px); opacity: 0; } }
</style>