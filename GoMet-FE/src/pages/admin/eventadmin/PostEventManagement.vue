<template>
  <div class="pe-admin-wrapper">
    
    <div class="pe-top-bar">
      <button class="btn-back-minimal" @click="goBack">
        <i class="fas fa-arrow-left"></i> <span>Quay lại Danh sách sự kiện</span>
      </button>
      
      <div v-if="eventInfo" class="status-indicator" :class="getEventStage(eventInfo).code">
        <span class="pulse-dot"></span>
        {{ getEventStage(eventInfo).text }}
      </div>
    </div>

    <div class="pe-hero-card" v-if="eventInfo">
      <div class="hero-main">
        <div class="hero-banner">
          <img :src="getImageUrl(eventInfo.bannerImage)" alt="Banner" />
        </div>
        <div class="hero-info">
          <div class="event-id-tag">EVENT ID: #{{ eventID }}</div>
          <h1 class="event-title">{{ eventInfo.eventName }}</h1>
          
          <div class="event-timeline-grid">
            <div class="timeline-item">
              <div class="t-icon"><i class="fas fa-pen-nib"></i></div>
              <div class="t-content">
                <label>Giai đoạn nhận bài</label>
                <p>{{ formatDate(eventInfo.startAt) }} — {{ formatDate(eventInfo.endAt) }}</p>
              </div>
            </div>
            <div class="timeline-item highlight">
              <div class="t-icon"><i class="fas fa-vote-yea"></i></div>
              <div class="t-content">
                <label>Giai đoạn bình chọn (Vote)</label>
                <p>{{ formatDate(eventInfo.voteStartAt) }} — {{ formatDate(eventInfo.voteEndAt) }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="hero-stats-panel">
        <div class="stat-unit">
          <span class="val">{{ posts.length }}</span>
          <span class="lbl">Bài dự thi</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-unit">
          <span class="val text-orange">{{ totalVotes }}</span>
          <span class="lbl">Tổng lượt vote</span>
        </div>
      </div>
    </div>

    <div class="pe-content-card">
      <div class="content-header">
        <div class="search-wrapper">
          <i class="fas fa-search"></i>
          <input v-model="searchQuery" type="text" placeholder="Tìm tên món ăn, tác giả hoặc ID bài thi..." />
        </div>
        
        <div v-if="isEnded" class="locked-badge">
          <i class="fas fa-lock"></i> Kết quả đã được chốt
        </div>
      </div>

      <div v-if="loading" class="loading-zone">
        <div class="loader"></div>
        <p>Đang đồng bộ dữ liệu xếp hạng...</p>
      </div>

      <div v-else-if="posts.length === 0" class="empty-zone">
        <div class="empty-icon-wrap">
          <i class="fas fa-box-open"></i>
        </div>
        <h3>Đường đua còn trống!</h3>
        <p>Sự kiện này hiện chưa nhận được tác phẩm dự thi nào từ cộng đồng.</p>
      </div>

      <div v-else class="pe-table-wrapper">
        <div class="pe-thead">
          <div class="th col-rank">Thứ hạng</div>
          <div class="th col-post">Tác phẩm dự thi</div>
          <div class="th col-author">Tác giả</div>
          <div class="th col-vote text-center">Lượt Vote</div>
          <div class="th col-action text-right">Công cụ</div>
        </div>

        <div class="pe-tbody">
          <div 
            v-for="(post, index) in filteredPosts" 
            :key="post.eventPostID" 
            class="pe-tr"
            :class="{'is-top-row': index < 3, 'top-1': index === 0}"
          >
            <div class="td col-rank">
              <div class="rank-box" :class="'rank-' + (index + 1)">
                <span v-if="index === 0">🥇</span>
                <span v-else-if="index === 1">🥈</span>
                <span v-else-if="index === 2">🥉</span>
                <span v-else>#{{ index + 1 }}</span>
              </div>
            </div>

            <div class="td col-post">
              <div class="post-preview">
                <img :src="getImageUrl(post.postImage)" />
                <div class="post-txt">
                  <span class="p-name">{{ post.postTitle }}</span>
                  <span class="p-id">POST ID: #{{ post.postID }}</span>
                </div>
              </div>
            </div>

            <div class="td col-author">
              <div class="user-info">
                <div class="user-ava">{{ post.username.charAt(0).toUpperCase() }}</div>
                <div class="user-txt">
                  <span class="u-name">{{ post.username }}</span>
                  <span class="u-id">UID: #{{ post.accountID }}</span>
                </div>
              </div>
            </div>

            <div class="td col-vote text-center">
              <div class="vote-badge-pro">
                <i class="fas fa-heart"></i>
                <span>{{ post.voteCount }}</span>
              </div>
            </div>

            <div class="td col-action text-right">
              <button class="btn-tool view" @click="openPostInNewTab(post.postID)" title="Xem bài viết">
                <i class="fas fa-external-link-alt"></i>
              </button>
              
              <button 
                v-if="!isEnded"
                class="btn-tool remove" 
                @click="confirmKick(post)" 
                title="Loại khỏi cuộc thi"
              >
                <i class="fas fa-trash-alt"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <Transition name="zoom-in">
      <div v-if="showKickModal" class="pe-modal-overlay" @click.self="showKickModal = false">
        <div class="pe-modal-content danger">
          <div class="modal-icon"><i class="fas fa-user-slash"></i></div>
          <h2>Loại bỏ bài thi?</h2>
          <p>Bạn đang thực hiện loại bài thi của <strong>{{ postToKick?.username }}</strong> khỏi sự kiện này.</p>
          <div class="modal-warning">
             <i class="fas fa-info-circle"></i> Bài viết gốc của người dùng sẽ không bị ảnh hưởng.
          </div>
          <div class="modal-btns">
            <button class="btn-cancel" @click="showKickModal = false">Quay lại</button>
            <button class="btn-confirm-danger" @click="executeKick">Xác nhận loại</button>
          </div>
        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api.js'
import { toast } from '@/composables/useToast'

const route = useRoute()
const router = useRouter()
const eventID = route.params.id

const eventInfo = ref(null)
const posts = ref([])
const loading = ref(true)
const searchQuery = ref('')
const showKickModal = ref(false)
const postToKick = ref(null)

const getImageUrl = (path) => {
  if (!path) return 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=200';
  return path.startsWith('/uploads') ? `http://localhost:8080${encodeURI(path)}` : path;
}

const getEventStage = (ev) => {
  if (Number(ev.isActive) === 0) return { code: 'deleted', text: 'Đã bị ẩn' };
  if (Number(ev.isForceEnded) === 1) return { code: 'ended', text: 'Đã kết thúc sớm' };
  
  const now = new Date();
  const start = new Date(ev.startAt);
  const end = new Date(ev.endAt);
  const voteStart = new Date(ev.voteStartAt);
  const voteEnd = new Date(ev.voteEndAt);

  if (now < start) return { code: 'upcoming', text: 'Sắp diễn ra' };
  if (now >= start && now <= end) return { code: 'active', text: 'Đang nhận bài' };
  if (now > end && now <= voteEnd) return { code: 'voting', text: 'Đang bình chọn' };
  return { code: 'ended', text: 'Đã kết thúc' };
};

const isEnded = computed(() => {
  if (!eventInfo.value) return false;
  const stage = getEventStage(eventInfo.value).code;
  return stage === 'ended' || stage === 'deleted';
});

const loadData = async () => {
  loading.value = true;
  try {
    const [resPosts, resEvent] = await Promise.all([
      api.get(`/api/admin/events/${eventID}/posts`),
      api.get(`/api/admin/events/${eventID}`)
    ]);
    posts.value = resPosts.data.sort((a, b) => b.voteCount - a.voteCount);
    eventInfo.value = resEvent.data;
  } catch (error) {
    toast.error('Lỗi tải dữ liệu');
  } finally {
    loading.value = false;
  }
}

// 🔥 FIX KÉO LÊN TRÊN CÙNG KHI MỞ TRANG
onMounted(() => {
  window.scrollTo({ top: 0, behavior: 'smooth' }); // Ép trình duyệt cuộn lên Top
  loadData();
})

const totalVotes = computed(() => posts.value.reduce((sum, p) => sum + (p.voteCount || 0), 0))

const filteredPosts = computed(() => {
  const q = searchQuery.value.toLowerCase();
  return posts.value.filter(p => 
    p.postTitle?.toLowerCase().includes(q) || p.username?.toLowerCase().includes(q)
  );
})

const formatDate = (d) => d ? new Date(d).toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' }) : '---';

const goBack = () => {
  router.push('/admin/events'); 
};

const openPostInNewTab = (postID) => {
  const routeData = router.resolve({ path: `/post/${postID}` }); 
  window.open(routeData.href, '_blank');
}

const confirmKick = (post) => {
  postToKick.value = post;
  showKickModal.value = true;
}

const executeKick = async () => {
  try {
    await api.delete(`/api/admin/events/posts/${postToKick.value.eventPostID}`);
    posts.value = posts.value.filter(p => p.eventPostID !== postToKick.value.eventPostID);
    toast.success('Đã loại bài thi thành công');
    showKickModal.value = false;
  } catch (error) {
    toast.error('Lỗi khi thao tác');
  }
}
</script>

<style lang="scss" scoped>
$orange: #ea580c;
$slate-900: #0f172a;
$slate-600: #475569;
$slate-200: #e2e8f0;
$white: #ffffff;

.pe-admin-wrapper {
  padding: 25px 40px; background: #f8fafc; min-height: 100vh; font-family: 'Inter', sans-serif;

  /* 1. TOP BAR */
  .pe-top-bar {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px;
    
    .btn-back-minimal {
      background: none; border: none; color: $slate-600; font-weight: 700; cursor: pointer;
      display: flex; align-items: center; gap: 10px; font-size: 0.95rem; transition: 0.3s;
      &:hover { color: $slate-900; transform: translateX(-5px); }
    }

    .status-indicator {
      display: flex; align-items: center; gap: 10px; padding: 8px 16px; border-radius: 100px;
      font-weight: 800; font-size: 0.85rem; text-transform: uppercase;
      
      &.active { background: #dcfce7; color: #15803d; .pulse-dot { background: #22c55e; } }
      &.voting { background: #fff7ed; color: #c2410c; border: 1px solid #ffedd5; .pulse-dot { background: $orange; animation: pulse-orange 2s infinite; } }
      &.ended { background: #f1f5f9; color: #475569; .pulse-dot { background: #94a3b8; } }
      &.upcoming { background: #eff6ff; color: #1d4ed8; .pulse-dot { background: #3b82f6; } }

      .pulse-dot { width: 8px; height: 8px; border-radius: 50%; }
    }
  }

  /* 2. HERO CARD */
  .pe-hero-card {
    background: $white; border-radius: 30px; border: 1px solid #edf2f7; overflow: hidden;
    display: flex; justify-content: space-between; margin-bottom: 30px; box-shadow: 0 10px 25px rgba(0,0,0,0.02);

    .hero-main {
      display: flex; padding: 35px; gap: 35px; flex: 1;
      .hero-banner {
        width: 130px; height: 130px; border-radius: 24px; overflow: hidden; flex-shrink: 0;
        img { width: 100%; height: 100%; object-fit: cover; }
      }
      .event-id-tag { font-size: 0.75rem; font-weight: 900; color: #94a3b8; margin-bottom: 10px; letter-spacing: 1px; }
      .event-title { font-size: 2.2rem; font-weight: 900; color: $slate-900; margin: 0 0 20px; letter-spacing: -1px; }
      
      .event-timeline-grid {
        display: flex; gap: 40px;
        .timeline-item {
          display: flex; gap: 15px; align-items: flex-start;
          .t-icon { width: 36px; height: 36px; border-radius: 10px; background: #f1f5f9; display: flex; align-items: center; justify-content: center; color: #64748b; font-size: 0.9rem; }
          &.highlight .t-icon { background: #fff7ed; color: $orange; }
          .t-content { 
            label { display: block; font-size: 0.75rem; font-weight: 800; color: #94a3b8; text-transform: uppercase; margin-bottom: 4px; }
            p { font-size: 0.95rem; font-weight: 700; color: $slate-900; margin: 0; }
          }
        }
      }
    }

    .hero-stats-panel {
      width: 300px; background: #fcfcfc; border-left: 1px solid #f1f5f9; display: flex; flex-direction: column; justify-content: center;
      .stat-unit {
        text-align: center; padding: 25px;
        .val { display: block; font-size: 2.2rem; font-weight: 900; color: $slate-900; line-height: 1; margin-bottom: 8px; }
        .lbl { font-size: 0.8rem; font-weight: 700; color: #94a3b8; text-transform: uppercase; }
        .text-orange { color: $orange; }
      }
      .stat-divider { height: 1px; background: #f1f5f9; margin: 0 40px; }
    }
  }

  /* 3. CONTENT CARD */
  .pe-content-card {
    background: $white; border-radius: 30px; border: 1px solid #edf2f7; padding: 35px;

    .content-header {
      display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px;
      .search-wrapper {
        position: relative; width: 450px;
        i { position: absolute; left: 18px; top: 50%; transform: translateY(-50%); color: #cbd5e1; }
        input { width: 100%; padding: 14px 20px 14px 50px; border-radius: 14px; border: 1.5px solid #f1f5f9; background: #f8fafc; outline: none; transition: 0.3s; font-weight: 600; &:focus { border-color: $orange; background: $white; box-shadow: 0 0 0 5px rgba(234, 88, 12, 0.08); } }
      }
      .locked-badge { padding: 10px 20px; border-radius: 12px; background: #fef2f2; color: #b91c1c; font-weight: 800; font-size: 0.85rem; display: flex; align-items: center; gap: 10px; }
    }
  }

  /* 🔥 CSS CHO PHẦN "CHƯA CÓ BÀI THI" (EMPTY STATE) 🔥 */
  .empty-zone {
    text-align: center; padding: 80px 0;
    .empty-icon-wrap {
      width: 100px; height: 100px; background: #f8fafc; border-radius: 50%;
      display: flex; align-items: center; justify-content: center; 
      margin: 0 auto 20px; border: 2px dashed #cbd5e1;
      i { font-size: 2.8rem; color: #94a3b8; }
    }
    h3 { font-size: 1.6rem; color: $slate-900; font-weight: 900; margin-bottom: 10px; letter-spacing: -0.5px;}
    p { font-size: 1.05rem; color: #64748b; }
  }

  /* TABLE STYLE */
  .pe-table-wrapper {
    .pe-thead {
      display: flex; padding: 0 20px 15px; font-weight: 800; font-size: 0.75rem; color: #94a3b8; text-transform: uppercase; letter-spacing: 1px;
    }
    
    .pe-tr {
      display: flex; align-items: center; padding: 20px; border-radius: 18px; border-bottom: 1px solid #f8fafc; transition: 0.3s;
      &:hover { background: #f8fafc; transform: scale(1.002); }
      &.is-top-row { background: #fafafa; border-radius: 20px; margin-bottom: 10px; border: 1px solid #f1f5f9; }
      &.top-1 { background: linear-gradient(to right, #fffef5, $white); border: 1.5px solid #fde68a; }
    }

    .col-rank { width: 100px; }
    .col-post { flex: 1; padding: 0 15px; }
    .col-author { width: 250px; }
    .col-vote { width: 150px; }
    .col-action { width: 120px; }

    .rank-box {
      width: 45px; height: 45px; border-radius: 12px; background: #f1f5f9; display: flex; align-items: center; justify-content: center; font-weight: 900; font-size: 1rem; color: #64748b;
      &.rank-1 { background: #fef3c7; color: #b45309; font-size: 1.5rem; }
      &.rank-2 { background: #f1f5f9; color: #475569; font-size: 1.3rem; }
      &.rank-3 { background: #ffedd5; color: #9a3412; font-size: 1.3rem; }
    }

    .post-preview {
      display: flex; gap: 18px; align-items: center;
      img { width: 60px; height: 60px; border-radius: 12px; object-fit: cover; }
      .p-name { display: block; font-weight: 850; color: $slate-900; font-size: 1.05rem; margin-bottom: 3px; }
      .p-id { font-size: 0.75rem; color: #94a3b8; font-weight: 700; }
    }

    .user-info {
      display: flex; gap: 12px; align-items: center;
      .user-ava { width: 40px; height: 40px; border-radius: 50%; background: #e2e8f0; display: flex; align-items: center; justify-content: center; font-weight: 900; color: #64748b; }
      .u-name { display: block; font-weight: 750; color: $slate-900; font-size: 0.95rem; }
      .u-id { font-size: 0.75rem; color: #94a3b8; font-weight: 600; }
    }

    .vote-badge-pro {
      display: inline-flex; align-items: center; gap: 8px; background: #f8fafc; padding: 10px 20px; border-radius: 100px; font-weight: 900; font-size: 1.1rem; color: $slate-900;
      i { color: #ef4444; }
    }

    .btn-tool {
      width: 40px; height: 40px; border-radius: 12px; border: 1.5px solid #f1f5f9; background: $white; cursor: pointer; transition: 0.2s; color: #64748b; margin-left: 8px;
      &.view:hover { background: #eff6ff; color: #3b82f6; border-color: #bfdbfe; }
      &.remove:hover { background: #fef2f2; color: #ef4444; border-color: #fecaca; }
    }
  }

  /* MODAL */
  .pe-modal-overlay { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.7); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 9999; }
  .pe-modal-content { background: $white; width: 420px; padding: 40px; border-radius: 30px; text-align: center; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25);
    .modal-icon { font-size: 3.5rem; margin-bottom: 20px; color: #ef4444; }
    h2 { font-size: 1.6rem; font-weight: 900; color: $slate-900; margin-bottom: 12px; }
    p { color: $slate-600; line-height: 1.5; margin-bottom: 20px; }
    .modal-warning { background: #fff7ed; padding: 12px; border-radius: 12px; color: #c2410c; font-size: 0.85rem; font-weight: 700; margin-bottom: 30px; }
    .modal-btns { display: flex; gap: 15px; button { flex: 1; padding: 15px; border-radius: 14px; border: none; font-weight: 800; cursor: pointer; transition: 0.3s; }
      .btn-cancel { background: #f1f5f9; color: $slate-600; &:hover { background: #e2e8f0; } }
      .btn-confirm-danger { background: #ef4444; color: $white; &:hover { background: #dc2626; transform: scale(1.02); } }
    }
  }
}

.loading-zone { text-align: center; padding: 100px 0; .loader { border: 4px solid #f3f3f3; border-top: 4px solid $orange; border-radius: 50%; width: 40px; height: 40px; animation: spin 1s linear infinite; margin: 0 auto 20px; } }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
@keyframes pulse-orange { 0% { box-shadow: 0 0 0 0 rgba(234, 88, 12, 0.4); } 70% { box-shadow: 0 0 0 10px rgba(234, 88, 12, 0); } 100% { box-shadow: 0 0 0 0 rgba(234, 88, 12, 0); } }

.text-orange { color: $orange !important; }
.text-center { text-align: center; }
.text-right { text-align: right; }
.text-red { color: #ef4444; }

/* Transitions */
.zoom-in-enter-active, .zoom-in-leave-active { transition: all 0.3s ease; }
.zoom-in-enter-from, .zoom-in-leave-to { opacity: 0; transform: scale(0.95); }
</style>