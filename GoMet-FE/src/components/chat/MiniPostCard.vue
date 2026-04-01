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
  author: 'GoMet'
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
      <img :src="postData.image || 'https://via.placeholder.com/150x100?text=GoMet'" :alt="postData.title">
      <div class="play-overlay" v-if="postData.hasVideo">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="white"><path d="M8 5v14l11-7z"/></svg>
      </div>
    </div>
    <div class="info">
      <h5 class="title">{{ postData.title }}</h5>
      <span class="author">Bởi <b>{{ postData.author }}</b></span>
    </div>
    <div class="action-hint">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
    </div>
  </div>
</template>

<style scoped lang="scss">
.mini-post-card {
  display: flex;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid rgba(234, 88, 12, 0.1);
  margin: 8px 0;
  cursor: pointer;
  width: 240px;
  transition: all 0.3s cubic-bezier(0.19, 1, 0.22, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  position: relative;
  
  &:hover {
    transform: translateY(-2px) scale(1.02);
    box-shadow: 0 10px 25px rgba(234, 88, 12, 0.15);
    border-color: rgba(234, 88, 12, 0.3);
    
    .thumbnail img { transform: scale(1.1); }
    .action-hint { color: #ea580c; transform: translateX(2px); }
  }

  .thumbnail {
    width: 80px;
    height: 80px;
    overflow: hidden;
    position: relative;
    flex-shrink: 0;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.4s ease;
    }
    
    .play-overlay {
      position: absolute;
      inset: 0;
      background: rgba(0,0,0,0.2);
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .info {
    flex: 1;
    padding: 10px 12px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    min-width: 0;

    .title {
      margin: 0 0 4px 0;
      font-size: 0.9rem;
      font-weight: 800;
      color: #1e293b;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .author {
      font-size: 0.7rem;
      color: #64748b;
      b { color: #ea580c; }
    }
  }
  
  .action-hint {
      position: absolute;
      right: 10px;
      bottom: 8px;
      color: #cbd5e1;
      transition: 0.2s;
  }
}
</style>
