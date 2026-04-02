<template>
  <div class="profile-actions-premium">
    <button 
      class="btn-follow-premium" 
      :class="{ 'is-following': isFollowing, 'is-loading': store.followLoading }" 
      @click="handleFollow"
      :disabled="store.followLoading"
    >
      <div class="inner-wrap">
        <span class="icon" v-if="!store.followLoading">
          <svg v-if="isFollowing" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
          <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
        </span>
        <span v-else class="spinner-sm"></span>
        <span class="text">{{ isFollowing ? 'Đang theo dõi' : 'Theo dõi' }}</span>
      </div>
      <div class="glow-effect"></div>
    </button>

    <button 
      class="btn-message-premium" 
      @click="handleMessage"
      :disabled="store.loading"
    >
      <div class="inner-wrap">
        <span class="icon" v-if="!store.loading">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
        </span>
        <span v-else class="spinner-sm"></span>
        <span class="text">Nhắn tin</span>
      </div>
    </button>
  </div>
</template>

<script setup>
import { useProfileActionsStore } from '@/stores/profileActions'

const props = defineProps({
  targetUser: { type: Object, required: true },
  isFollowing: { type: Boolean, default: false }
})

const emit = defineEmits(['update:isFollowing', 'follow-changed'])
const store = useProfileActionsStore()

const handleFollow = async () => {
  const newStatus = await store.toggleFollow(props.targetUser.id || props.targetUser.accountID, props.isFollowing)
  emit('update:isFollowing', newStatus)
  emit('follow-changed', newStatus)
}

const handleMessage = () => {
  store.startConversation(props.targetUser)
}
</script>

<style scoped lang="scss" src="./ProfileActions.scss"></style>
