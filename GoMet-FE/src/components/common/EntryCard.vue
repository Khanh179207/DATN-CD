<template>
  <div class="gomet-entry-card" :class="{ 'is-top-3': rank > 0 && rank <= 3 }">
    <div v-if="rank > 0 && rank <= 3" class="rank-badge" :class="'rank-' + rank">
      <span class="medal-icon">{{ getMedalIcon(rank) }}</span>
      <span class="rank-text">Top {{ rank }}</span>
    </div>

    <div class="entry-media" @click="goToDetail">
      <img :src="getImageUrl(post.postImage)" :alt="post.postTitle" class="entry-img" />
      <div class="img-overlay">
        <div class="view-recipe">Xem công thức <i class="fas fa-arrow-right"></i></div>
      </div>
      <div class="entry-id-tag">#{{ String(post.postID).padStart(3, '0') }}</div>
    </div>

    <div class="entry-content">
      <h3 class="entry-title" @click="goToDetail">{{ post.postTitle }}</h3>
      
      <div class="author-info">
        <img :src="post.authorAvatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + post.username" class="author-avt" />
        <span class="author-name">bởi <strong>{{ post.username }}</strong></span>
      </div>

      <div class="vote-footer">
        <div class="vote-count-group">
          <span class="count-num">{{ formatNumber(localVoteCount) }}</span>
          <span class="count-label">phiếu bầu</span>
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
            <span>{{ isVoted ? 'Đã bình chọn' : 'Bình chọn' }}</span>
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

const props = defineProps({
  post: Object,
  rank: { type: Number, default: 0 },
  canVote: { type: Boolean, default: false },
  limitReached: { type: Boolean, default: false }
});

const emit = defineEmits(['vote-toggled']);

const router = useRouter();
const authStore = useAuthStore();

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
  if (!authStore.isAuthenticated) return toast.info("Vui lòng đăng nhập để bình chọn!");
  
  // Vét cạn các trường hợp tên biến ID của User
  const uid = authStore.user?.accountID || authStore.user?.accountId || authStore.user?.id;
  
  if (!uid) {
    toast.error("Không tìm thấy thông tin tài khoản, vui lòng thử đăng nhập lại!");
    return;
  }

  // CHẶN: NẾU CHƯA VOTE BÀI NÀY VÀ ĐÃ ĐẠT GIỚI HẠN VOTE TOÀN SỰ KIỆN
  if (!isVoted.value && props.limitReached) {
    toast.warn("Sếp đã dùng hết hạn mức phiếu bầu cho sự kiện này!");
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
      toast.success(res.data.message || "Đã ghi nhận phiếu bầu của bạn!");
    } else {
      toast.warn(res.data.message || "Đã hủy bình chọn!");
    }
    
    // Thông báo cho component cha (EventDetail) cập nhật Progress Bar
    emit('vote-toggled', isVoted.value);
  } catch (err) {
    toast.error(err.response?.data?.message || "Không thể bình chọn lúc này!");
  } finally {
    isProcessing.value = false;
  }
};

const goToDetail = () => {
  router.push(`/post/${props.post.postID}`);
};

const getMedalIcon = (r) => ['','🥇','🥈','🥉'][r];
const formatNumber = (n) => n.toLocaleString();
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
      display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
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

/* =======================================================
   🔥 HỆ THỐNG RESPONSIVE (TỐI ƯU MỌI THIẾT BỊ)
   ======================================================= */

/* --- 1. Màn hình Tablet & Laptop nhỏ (Dưới 1024px) --- */
@media (max-width: 1024px) {
  .gomet-entry-card {
    .entry-media { height: 200px; }
    .entry-content { padding: 16px; }
  }
}

/* --- 2. Màn hình Mobile lớn (Dưới 768px) --- */
@media (max-width: 768px) {
  .gomet-entry-card {
    .entry-media { height: 180px; }
    
    .entry-content {
      .entry-title { font-size: 1.05rem; margin-bottom: 10px; }
      .author-info { margin-bottom: 16px; }
      
      .vote-footer {
        .vote-count-group .count-num { font-size: 1.15rem; }
        .btn-gomet-vote { padding: 8px 16px; font-size: 0.8rem; }
      }
    }
  }
}

/* --- 3. Màn hình Mobile nhỏ (Dưới 480px - Vd: iPhone SE) --- */
@media (max-width: 480px) {
  .gomet-entry-card {
    border-radius: 16px;

    .rank-badge { padding: 4px 10px; font-size: 0.65rem; }
    .entry-media { height: 160px; }
    .entry-media .img-overlay .view-recipe { padding: 6px 12px; font-size: 0.75rem; }
    
    .entry-content {
      padding: 12px;
      .entry-title { font-size: 0.95rem; }
      
      .author-info {
        gap: 8px; margin-bottom: 12px;
        .author-avt { width: 24px; height: 24px; }
        .author-name { font-size: 0.75rem; }
      }
      
      .vote-footer {
        padding-top: 12px;
        .vote-count-group .count-num { font-size: 1.05rem; }
        .btn-gomet-vote { padding: 6px 12px; font-size: 0.75rem; border-radius: 10px; }
      }
    }
  }
}

/* --- 4. Màn hình siêu nhỏ (Dưới 360px) --- */
@media (max-width: 360px) {
  .gomet-entry-card {
    .entry-content .vote-footer {
      flex-direction: column; /* Gập nút Vote xuống dưới số phiếu nếu quá chật */
      align-items: flex-start;
      gap: 10px;
      
      .btn-gomet-vote { 
        width: 100%; 
        justify-content: center; 
      }
    }
  }
}
</style>