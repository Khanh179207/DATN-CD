<template>
  <div class="user-card" @click="goToProfile">
    <div class="avatar-wrapper">
      <img :src="displayAvatar" :alt="user.name" class="user-avatar" />
      <div v-if="user.isVerified" class="verify-badge">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
          <polyline points="20 6 9 17 4 12"></polyline>
        </svg>
      </div>
    </div>
    
    <div class="user-info">
      <h3 class="user-name">{{ user.name || user.username || $t('common.user_fallback') }}</h3>
      <p class="user-handle">{{ displayHandle }}</p>
    </div>

    <button class="btn-view-profile">{{ $t('common.view_profile') }}</button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t } = useI18n()

// Định nghĩa Props nhận từ Component cha
const props = defineProps({
  user: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

// Xử lý Avatar: Nếu không có avatar thì dùng ui-avatars giống logic trong ProfilePage
const displayAvatar = computed(() => {
  if (props.user.avatar) return props.user.avatar
  
  const nameToUse = props.user.name || props.user.username || 'U'
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(nameToUse)}&background=EA580C&color=fff`
})

// Xử lý Handle (@username): Nếu backend không trả về handle, tự động tạo từ tên
const displayHandle = computed(() => {
  if (props.user.handle) return `@${props.user.handle.replace('@', '')}` // Đảm bảo chỉ có 1 dấu @
  
  const nameToUse = props.user.name || props.user.username || 'user'
  return '@' + nameToUse.toLowerCase().replace(/\s+/g, '_')
})

// Xử lý chuyển hướng khi bấm vào Card
const goToProfile = () => {
  // Ưu tiên id, sau đó đến accountID (Tùy thuộc vào dữ liệu backend trả về)
  const targetId = props.user.id || props.user.accountID
  if (targetId) {
    router.push(`/profile/${targetId}`)
  }
}
</script>

<style scoped lang="scss">
.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  background: #ffffff;
  border-radius: 20px;
  border: 1px solid #F3F4F6;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
  cursor: pointer;
  text-align: center;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  overflow: hidden;

  // Hiệu ứng khi hover chuột vào Card
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
    border-color: #FED7AA;

    .btn-view-profile {
      background: #EA580C;
      color: white;
    }
  }
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 16px;
}

.user-avatar {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #FFF7ED;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
}

.verify-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #3B82F6; // Màu xanh dương verify
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid white;
}

.user-info {
  margin-bottom: 20px;
  width: 100%;
}

.user-name {
  font-size: 1.15rem;
  font-weight: 800;
  color: #1C1917;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-handle {
  font-size: 0.9rem;
  color: #A8A29E;
  margin: 0;
}

.btn-view-profile {
  background: #FFF7ED;
  color: #EA580C;
  border: 1px solid #FED7AA;
  padding: 8px 24px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  transition: 0.3s ease;
  width: 100%;
}
</style>