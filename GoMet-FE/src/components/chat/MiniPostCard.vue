<script setup>
import { defineProps, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const props = defineProps({
  postID: {
    type: [Number, String],
    default: null
  },
  post: {
    type: Object,
    default: null
  }
})

const router = useRouter()
const postData = ref(props.post || {
  id: props.postID,
  title: 'Đang tải...',
  image: '',
  author: 'GoMet',
  hasVideo: false
})

const fetchPost = async () => {
  if (!props.postID) return
  try {
    const res = await api.get(`/api/posts/${props.postID}`)
    const p = res.data
    postData.value = {
      id: p.postID,
      title: p.title,
      image: p.media,
      author: p.authorName || 'Đầu bếp GoMet',
      hasVideo: !!p.video
    }
  } catch (error) {
    console.error("Lỗi khi tải thông tin bài viết:", error)
    postData.value.title = "Bài viết không tồn tại"
  }
}

onMounted(() => {
  if (props.postID && !props.post) {
    fetchPost()
  }
})

const goToPost = () => {
  if (postData.value.id) {
    router.push(`/post/${postData.value.id}`)
  }
}
</script>

<template>
  <div class="mini-post-card" @click="goToPost">
    <div class="thumbnail">
      <img :src="postData.image || 'https://via.placeholder.com/150x150?text=GoMet'" :alt="postData.title">
      <div class="play-overlay" v-if="postData.hasVideo">
        <div class="play-btn">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
        </div>
      </div>
    </div>
    
    <div class="info">
      <h5 class="title" :title="postData.title">{{ postData.title }}</h5>
      <div class="author-wrap">
        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" class="feather feather-user"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
        <span class="author">Bởi <b>{{ postData.author }}</b></span>
      </div>
    </div>

    <div class="action-hint">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
    </div>
  </div>
</template>

<style scoped lang="scss">
.mini-post-card {
  display: flex;
  align-items: center; // Căn giữa theo chiều dọc
  background: #ffffff;
  border-radius: 16px;
  padding: 8px; // Thêm padding để ảnh không bị tràn sát viền
  gap: 12px; // Khoảng cách giữa ảnh và text
  border: 1px solid rgba(234, 88, 12, 0.15);
  margin: 6px 0;
  cursor: pointer;
  width: 260px;
  max-width: 100%;
  transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
  position: relative;
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 12px 24px rgba(234, 88, 12, 0.12);
    border-color: rgba(234, 88, 12, 0.4);
    
    .thumbnail img { transform: scale(1.1); }
    
    .action-hint { 
      background: rgba(234, 88, 12, 0.1);
      color: #ea580c; 
      transform: translateX(2px); 
    }
  }

  .thumbnail {
    width: 60px; // Thu nhỏ ảnh lại một chút để cân đối với padding
    height: 60px;
    border-radius: 10px; // Bo góc ảnh ở bên trong
    overflow: hidden;
    position: relative;
    flex-shrink: 0;
    background: #f1f5f9;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.4s ease;
    }
    
    .play-overlay {
      position: absolute;
      inset: 0;
      background: rgba(0,0,0,0.15);
      display: flex;
      align-items: center;
      justify-content: center;

      .play-btn {
        width: 24px;
        height: 24px;
        background: rgba(255, 255, 255, 0.3);
        backdrop-filter: blur(4px); // Hiệu ứng kính mờ (Glassmorphism)
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
      }
    }
  }

  .info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 4px; // Khoảng cách đều giữa title và author
    min-width: 0;
    padding-right: 28px; // Để chữ không đè lên nút mũi tên

    .title {
      margin: 0;
      font-size: 0.9rem;
      line-height: 1.3;
      font-weight: 700;
      color: #1e293b;
      // Cho phép rớt xuống 2 dòng thay vì bị cắt ngang
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .author-wrap {
      display: flex;
      align-items: center;
      gap: 4px;
      color: #64748b;
      
      svg { opacity: 0.7; }

      .author {
        font-size: 0.75rem;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        
        b { 
          color: #ea580c; 
          font-weight: 600; 
        }
      }
    }
  }
  
  .action-hint {
      position: absolute;
      right: 10px;
      top: 50%;
      margin-top: -14px; // Căn giữa chính xác
      width: 28px;
      height: 28px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #94a3b8;
      transition: all 0.25s ease;
  }
}
</style>