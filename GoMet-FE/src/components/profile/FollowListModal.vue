<template>
  <transition name="modal-premium-fade">
    <div v-if="show" class="vip-follow-overlay" @click.self="$emit('close')">
      <div class="vip-follow-container">
        
        <div class="edit-modal-header">
          <div class="header-left">
            <h2>{{ type === 'followers' ? 'Người theo dõi' : 'Đang theo dõi' }}</h2>
          </div>
          <button class="btn-close-premium" @click="$emit('close')">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
        
        <div class="edit-modal-body vip-follow-body">
          
          <div v-if="loading" class="follow-state follow-loading">
            <span class="spinner-xl"></span>
            <p>Đang tải danh sách...</p>
          </div>
          
          <div v-else-if="list.length === 0" class="follow-state follow-empty">
            <div class="empty-icon-wrap">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
            </div>
            <p>Danh sách này hiện đang trống</p>
          </div>
          
          <div v-else class="follow-list-grid">
            <div 
              v-for="item in list" 
              :key="item.accountID || item.id" 
              class="follow-user-row" 
              @click="$emit('go-to-profile', item.accountID || item.id)"
            >
              <div class="user-avatar-wrap">
                <img :src="item.avatar || `https://ui-avatars.com/api/?name=${item.username}&background=F1F5F9&color=0F172A`" alt="Avatar">
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
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

/* Overlay & Container */
.vip-follow-overlay { 
  position: fixed; 
  inset: 0; 
  z-index: 9500; 
  background: rgba(15, 23, 42, 0.4); 
  backdrop-filter: blur(8px); 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  padding: 16px; 
  font-family: 'Inter', sans-serif;
}

.vip-follow-container { 
  background: #ffffff; 
  width: 100%; 
  max-width: 480px; 
  border-radius: 24px; 
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25); 
  overflow: hidden; 
  display: flex;
  flex-direction: column;
}

/* Header */
.edit-modal-header { 
  padding: 24px 32px 16px; 
  border-bottom: 1px solid #F1F5F9; 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  
  h2 { 
    font-size: 1.3rem; 
    font-weight: 800; 
    color: #0F172A; 
    margin: 0; 
    letter-spacing: -0.5px;
  } 

  .btn-close-premium { 
    background: #F8FAFC; 
    border: none; 
    width: 36px; 
    height: 36px; 
    border-radius: 50%; 
    display: flex;
    align-items: center;
    justify-content: center;
    color: #64748B; 
    cursor: pointer; 
    transition: 0.2s; 

    &:hover { 
      background: #E2E8F0; 
      color: #0F172A; 
      transform: scale(1.05);
    } 
  } 
}

/* Body */
.vip-follow-body { 
  padding: 16px 24px 24px; 
  max-height: 60vh; 
  overflow-y: auto; 
  
  &::-webkit-scrollbar { width: 6px; } 
  &::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; } 
}

/* State: Loading & Empty */
.follow-state { 
  padding: 60px 0; 
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center; 
  color: #64748B; 

  .empty-icon-wrap { 
    background: #F8FAFC;
    color: #94A3B8;
    width: 80px;
    height: 80px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px; 
  } 

  p { 
    font-weight: 600; 
    font-size: 1.05rem;
    margin: 0; 
    color: #475569;
  } 
}

.spinner-xl { 
  width: 40px; 
  height: 40px; 
  border: 3px solid #E2E8F0; 
  border-top-color: #EA580C; 
  border-radius: 50%; 
  animation: spin 0.8s infinite linear; 
  margin-bottom: 16px; 
  display: block; 
}

@keyframes spin { 
  to { transform: rotate(360deg); } 
}

/* User List Grid */
.follow-list-grid { 
  display: flex; 
  flex-direction: column; 
  gap: 8px; 
}

/* User Row - Premium Card Style */
.follow-user-row { 
  display: flex; 
  align-items: center; 
  gap: 16px; 
  padding: 12px 16px; 
  border-radius: 16px; 
  background: #ffffff;
  border: 1px solid transparent;
  cursor: pointer; 
  transition: all 0.25s ease; 

  &:hover { 
    background: #F8FAFC; 
    border-color: #E2E8F0;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.03);

    .btn-view-profile {
      background: #EA580C;
      color: #ffffff;
    }
  } 

  /* Avatar with Online Dot */
  .user-avatar-wrap { 
    position: relative;
    width: 52px; 
    height: 52px; 
    flex-shrink: 0; 

    img { 
      width: 100%; 
      height: 100%; 
      border-radius: 50%; 
      object-fit: cover; 
      border: 2px solid #F1F5F9;
    } 

    .online-indicator {
      position: absolute;
      bottom: 2px;
      right: 2px;
      width: 12px;
      height: 12px;
      background: #22C55E; /* Green color */
      border: 2px solid #ffffff;
      border-radius: 50%;
    }
  } 

  /* Info */
  .user-info { 
    flex-grow: 1; 
    display: flex; 
    flex-direction: column; 
    min-width: 0; /* Prevent text overflow */
    
    .user-name { 
      font-weight: 700; 
      color: #0F172A; 
      font-size: 1rem; 
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    } 
    .user-handle { 
      color: #64748B; 
      font-size: 0.85rem; 
      font-weight: 500;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    } 
  } 

  /* Action Button */
  .btn-view-profile { 
    display: flex; 
    align-items: center; 
    gap: 4px; 
    background: #F1F5F9; 
    color: #475569; 
    border: none; 
    padding: 8px 16px;
    border-radius: 100px;
    font-size: 0.85rem; 
    font-weight: 700; 
    cursor: pointer; 
    transition: all 0.25s ease;
  } 
}

/* Animations */
.modal-premium-fade-enter-active, .modal-premium-fade-leave-active { 
  transition: opacity 0.25s ease; 
  .vip-follow-container { transition: transform 0.25s cubic-bezier(0.175, 0.885, 0.32, 1.275); } 
}
.modal-premium-fade-enter-from, .modal-premium-fade-leave-to { 
  opacity: 0; 
  .vip-follow-container { transform: scale(0.95) translateY(16px); } 
}

/* Mobile Responsive */
@media (max-width: 576px) {
  .vip-follow-overlay { padding: 12px; }
  .vip-follow-container { border-radius: 20px; }
  .edit-modal-header {
    padding: 20px 24px 16px;
    h2 { font-size: 1.2rem; }
  }
  .vip-follow-body { padding: 16px 20px 20px; }
  .follow-user-row {
    padding: 10px 12px;
    .user-avatar-wrap { width: 46px; height: 46px; }
    .user-info .user-name { font-size: 0.95rem; }
    .btn-view-profile { padding: 6px 12px; font-size: 0.8rem; }
  }
}
</style>