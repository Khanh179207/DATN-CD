
<template>
  <div class="gomet-entry-card" :class="{ 'is-top-3': rank > 0 && rank <= 3 }">
    <div v-if="rank > 0 && rank <= 3" class="rank-badge" :class="'rank-' + rank">
      <span class="medal-icon">{{ getMedalIcon(rank) }}</span>
      <span class="rank-text">Top {{ rank }}</span>
    </div>

    <div class="entry-media" @click="goToDetail">
      <img :src="getImageUrl(post.postImage)" :alt="post.postTitle" class="entry-img" />
      <div class="img-overlay">
        <div class="view-recipe">{{ $t('home.view_recipe_btn') }} <i class="fas fa-arrow-right"></i></div>
      </div>
      <div class="entry-id-tag">#{{ String(post.postID).padStart(3, '0') }}</div>
    </div>

    <div class="entry-content">
      <h3 class="entry-title" @click="goToDetail">{{ post.postTitle }}</h3>
      
      <div class="author-info">
        <img :src="post.authorAvatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + post.username" class="author-avt" />
        <span class="author-name">{{ $t('recipe.by') }} <strong>{{ post.username }}</strong></span>
      </div>

      <div class="vote-footer">
        <div class="vote-count-group">
          <span class="count-num">{{ formatNumber(localVoteCount) }}</span>
          <span class="count-label">{{ $t('events.votes_sfx') }}</span>
        </div>

        <button 
          @click.stop="handleVote" 
          class="btn-gomet-vote" 
          :class="{ 'voted': isVoted, 'loading': isProcessing }"
          :disabled="!canVote || isProcessing"
        >
          <div v-if="isProcessing" class="mini-spinner"></div>
          <template v-else>
            <i class="fas fa-heart" v-if="isVoted"></i>
            <i class="far fa-heart" v-else></i>
            <span>{{ isVoted ? $t('events.voted') : $t('events.vote') }}</span>
          </template>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/services/api';
import { toast } from '@/composables/useToast';
import { useAuthStore } from '@/stores/auth';
import { useI18n } from 'vue-i18n';

const props = defineProps({
  post: Object,
  rank: { type: Number, default: 0 },
  canVote: { type: Boolean, default: false }
});

const router = useRouter();
const authStore = useAuthStore();
const { t, locale } = useI18n();

// Khởi tạo giá trị từ post.isVoted
const isVoted = ref(props.post.voted || false); 
const isProcessing = ref(false);
const localVoteCount = ref(props.post.voteCount || 0);

// Đồng bộ lại nếu props thay đổi
watch(() => props.post.voted, (newVal) => isVoted.value = newVal);
watch(() => props.post.voteCount, (newVal) => localVoteCount.value = newVal);

const getImageUrl = (path) => {
  if (!path) return 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400';
  return path.startsWith('/uploads') ? `http://localhost:8080${path}` : path;
};

// 🔥 ĐÃ FIX HÀM VOTE: THÊM ĐẦY ĐỦ ACCOUNT ID
const handleVote = async () => {
  if (!authStore.isAuthenticated) return toast.info(t('toast.need_login_vote'));
  
  // Vét cạn các trường hợp tên biến ID của User
  const uid = authStore.user?.accountID || authStore.user?.accountId || authStore.user?.id;
  
  if (!uid) {
    toast.error(t('toast.account_info_missing'));
    return;
  }

  isProcessing.value = true;
  try {
    // 🔥 GỬI ĐẦY ĐỦ 2 TRƯỜNG LÊN CHO JAVA
    const payload = {
      accountID: Number(uid),
      eventPostID: Number(props.post.eventPostID)
    };

    const res = await api.post('/api/votes/toggle', payload);
    
    // Cập nhật trạng thái trực tiếp từ kết quả server trả về
    isVoted.value = res.data.isVoted;
    localVoteCount.value = res.data.newVoteCount;
    
    if (isVoted.value) {
      toast.success(t('toast.vote_registered'));
    } else {
      toast.warn(t('toast.vote_removed'));
    }
  } catch (err) {
    toast.error(t('toast.vote_failed'));
  } finally {
    isProcessing.value = false;
  }
};

const goToDetail = () => {
  router.push(`/post/${props.post.postID}`);
};

const getMedalIcon = (r) => ['','🥇','🥈','🥉'][r];
const formatNumber = (n) => Number(n || 0).toLocaleString(locale.value === 'en' ? 'en-US' : 'vi-VN');
</script>

<style scoped lang="scss">
/* Toàn bộ Style giữ nguyên 100% */
.gomet-entry-card {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0,0,0,0.05);
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  position: relative;
  border: 1px solid #f1f5f9;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 40px rgba(234, 88, 12, 0.1);
    border-color: #ffedd5;
    
    .img-overlay { opacity: 1; }
    .entry-img { transform: scale(1.1); }
  }

  &.is-top-3 {
    border: 2px solid #ffedd5;
    background: linear-gradient(to bottom, #ffffff, #fffaf5);
  }

  .rank-badge {
    position: absolute;
    top: 12px; left: 12px;
    z-index: 10;
    display: flex; align-items: center; gap: 6px;
    padding: 6px 14px;
    border-radius: 30px;
    color: #fff; font-weight: 800; font-size: 0.75rem;
    box-shadow: 0 4px 10px rgba(0,0,0,0.15);

    &.rank-1 { background: linear-gradient(135deg, #fbbf24, #d97706); }
    &.rank-2 { background: linear-gradient(135deg, #94a3b8, #475569); }
    &.rank-3 { background: linear-gradient(135deg, #a8a29e, #57534e); }
  }

  .entry-media {
    height: 220px;
    overflow: hidden;
    position: relative;
    cursor: pointer;

    .entry-img {
      width: 100%; height: 100%; object-fit: cover;
      transition: transform 0.6s ease;
    }

    .img-overlay {
      position: absolute; inset: 0;
      background: rgba(234, 88, 12, 0.4);
      display: flex; align-items: center; justify-content: center;
      opacity: 0; transition: 0.3s;
      
      .view-recipe {
        background: #fff; color: #EA580C;
        padding: 8px 16px; border-radius: 30px;
        font-weight: 800; font-size: 0.85rem;
      }
    }

    .entry-id-tag {
      position: absolute; top: 12px; right: 12px;
      background: rgba(15, 23, 42, 0.7);
      backdrop-filter: blur(4px);
      color: #fff; padding: 4px 10px;
      border-radius: 8px; font-size: 0.7rem; font-weight: 700;
    }
  }

  .entry-content {
    padding: 20px;

    .entry-title {
      font-size: 1.15rem; font-weight: 800; color: #0f172a;
      margin: 0 0 12px; cursor: pointer;
      display: -webkit-box; line-clamp: 2; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
      line-height: 1.4; transition: color 0.2s;
      &:hover { color: #EA580C; }
    }

    .author-info {
      display: flex; align-items: center; gap: 10px; margin-bottom: 20px;
      .author-avt { width: 28px; height: 28px; border-radius: 50%; border: 2px solid #f1f5f9; }
      .author-name { font-size: 0.85rem; color: #64748b; }
    }

    .vote-footer {
      display: flex; justify-content: space-between; align-items: center;
      padding-top: 15px; border-top: 1px dashed #e2e8f0;

      .vote-count-group {
        display: flex; flex-direction: column;
        .count-num { font-size: 1.3rem; font-weight: 900; color: #EA580C; line-height: 1; }
        .count-label { font-size: 0.65rem; color: #94a3b8; text-transform: uppercase; font-weight: 700; }
      }

      .btn-gomet-vote {
        border: none; outline: none;
        padding: 10px 20px; border-radius: 12px;
        background: #fff7ed; color: #ea580c;
        font-weight: 800; font-size: 0.85rem;
        display: flex; align-items: center; gap: 8px;
        cursor: pointer; transition: all 0.3s;
        border: 1px solid #ffedd5;

        &:hover:not(:disabled) { background: #ea580c; color: #fff; transform: scale(1.05); }
        
        &.voted {
          background: #ea580c; color: #fff; border-color: #ea580c;
          box-shadow: 0 4px 12px rgba(234, 88, 12, 0.3);
        }

        &:disabled { opacity: 0.6; cursor: not-allowed; }
      }
    }
  }
}

.mini-spinner {
  width: 16px; height: 16px; border: 2px solid currentColor;
  border-right-color: transparent; border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>