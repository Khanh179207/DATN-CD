<template>
  <div class="create-post-container">
    <div v-if="publishing" class="loading-overlay">
      <div class="loader-content">
        <div class="spinner"></div>
        <p class="status-text">{{ publishStatus || 'Đang xử lý bài viết...' }}</p>
        <small>Hệ thống đang đồng bộ media chất lượng cao, vui lòng giữ kết nối.</small>
      </div>
    </div>

    <section class="hero-section-full-bleed fade-in-up">
      <div class="hero-container-inner">
        
        <div class="hero-info-col">
          <div class="top-nav-bar">
            <div class="nav-left">
              <button @click="$router.back()" class="btn-back-simple">
                <div class="icon-circle">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
                </div>
                <span>Hủy bỏ</span>
              </button>
              <span class="sep">/</span>
              <div class="category-wrapper">
                <select v-model="post.categoryID" class="category-select">
                  <option :value="null">MẶC ĐỊNH (TỔNG HỢP)</option>
                  <option v-for="cat in categories" :key="cat.categoryID" :value="cat.categoryID">
                    {{ cat.categoryName }}
                  </option>
                </select>
              </div>
            </div>
          </div>

          <textarea 
            v-model="post.title" 
            class="recipe-title-input" 
            placeholder="Tên món ăn của bạn?..." 
            rows="1"
            @input="autoResize"
          ></textarea>
          
          <textarea 
            v-model="post.description" 
            class="recipe-desc-input" 
            placeholder="Kể một chút về câu chuyện của món ăn này..." 
            rows="2"
            @input="autoResize"
          ></textarea>

          <div class="recipe-meta-row">
            <div class="meta-box">
              <span class="icon">⏱️</span>
              <div class="meta-detail">
                <span class="label">THỜI GIAN</span>
                <input type="number" v-model="post.cookingTime" placeholder="Phút" class="meta-inp" min="1">
              </div>
            </div>
            <div class="meta-divider"></div>
            <div class="meta-box">
              <span class="icon">🔥</span>
              <div class="meta-detail">
                <span class="label">ĐỘ KHÓ</span>
                <select v-model="post.level" class="meta-select">
                  <option value="Easy">Dễ</option>
                  <option value="Medium">Trung bình</option>
                  <option value="Hard">Khó</option>
                </select>
              </div>
            </div>
          </div>

          <div class="author-action-row">
            <div class="author-block">
              <img 
                :src="userAvatar" 
                class="auth-img"
                alt="Chef Avatar"
                @error="handleAvatarError"
              >
              <div class="auth-text">
                <span class="label">Người thực hiện</span>
                <span class="name">{{ currentUser.fullName || currentUser.username || 'Đầu bếp GOMET' }}</span> 
              </div>
            </div>
          </div>
        </div>

        <div class="hero-image-col">
          <div class="image-frame-hero uploader-box" :class="{ 'has-image': post.image }" @click="triggerUpload">
            <img v-if="post.image" :src="post.image" class="img-hero-cover">
            <div v-else class="upload-placeholder">
              <div class="icon-camera">📷</div>
              <span class="up-text">Tải ảnh bìa chất lượng cao</span>
              <small>(Nên dùng ảnh ngang 16:9)</small>
            </div>
            <div v-if="post.image" class="edit-overlay"><span>Thay đổi ảnh bìa</span></div>
            <input type="file" ref="fileInput" class="hidden-input" accept="image/*" @change="handleImageUpload">
          </div>
        </div>
      </div>
    </section>

    <section class="body-section-premium">
      <div class="video-full-width-wrapper">
        <div class="interaction-style-card">
          <div class="interaction-card-header">
            <h3>🎬 Video hướng dẫn <small>(Tùy chọn)</small></h3>
          </div>
          <div class="video-content-body">
            <div v-if="!post.video" class="video-placeholder-box" @click="triggerVideoUpload">
              <span class="icon-camera">🎥</span>
              <span>Kéo thả hoặc tải Video lên (Max 50MB)</span>
            </div>
            <div v-else class="video-player-wrapper">
              <video :src="post.video" controls class="interaction-video-tag"></video>
              <div class="video-action-overlay">
                <button class="btn-remove-media-float" @click.stop="removeVideo">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
                  Gỡ bỏ Video
                </button>
              </div>
            </div>
            <input type="file" ref="videoInput" class="hidden-input" accept="video/*" @change="handleVideoUpload">
          </div>
        </div>
      </div>

      <div class="body-container-inner">
        <aside class="sidebar-left-sticky">
          <div class="sticky-wrapper">
            <div class="premium-card ingredients-card">
              <div class="card-header-gradient">
                <div class="header-content">
                  <h3>🛒 Nguyên liệu</h3>
                  <span class="sub-text">Cần chuẩn bị những gì?</span>
                </div>
              </div>
              <div class="ingredients-list-editor">
                <div v-for="(ing, index) in post.ingredients" :key="index" class="ing-row-edit">
                  <div class="checkbox-visual"><svg viewBox="0 0 24 24"><polyline points="20 6 9 17 4 12"></polyline></svg></div>
                  <input v-model="ing.name" class="ing-input-text" placeholder="VD: 500g thịt bò thăn..." @keyup.enter="addIngredient">
                  <button class="btn-remove" @click="removeIngredient(index)" v-if="post.ingredients.length > 1">×</button>
                </div>
              </div>
              <button class="btn-add-dashed" @click="addIngredient">+ Thêm dòng nguyên liệu</button>
            </div>
          </div>
        </aside>

        <main class="main-right-content">
          <div class="premium-card steps-card">
            <div class="steps-header-modern">
              <h2>Quy trình chế biến</h2>
              <div class="step-counter-badge">{{ post.steps.length }} Bước</div>
            </div>
            <div class="timeline-editorial">
              <transition-group name="list">
                <div class="timeline-step" v-for="(step, index) in post.steps" :key="step.id">
                  <div class="step-marker-col">
                    <div class="step-number-art">{{ index + 1 }}</div>
                    <div class="step-connector" v-if="index !== post.steps.length - 1"></div>
                  </div>
                  <div class="step-content-col">
                    <div class="step-top-row">
                      <h4 class="step-heading">BƯỚC {{ index + 1 }}</h4>
                      <button class="btn-del-step" @click="removeStep(index)" v-if="post.steps.length > 1">Xóa bước</button>
                    </div>
                    <textarea 
                      v-model="step.desc" 
                      class="step-desc-input" 
                      placeholder="Chi tiết cách thực hiện bước này..."
                      rows="3"
                      @input="autoResize"
                    ></textarea>
                    <div class="step-media-upload">
                      <div v-if="step.image" class="media-preview" @click="triggerStepUpload(index)">
                        <img :src="step.image">
                        <div class="hover-change">Thay đổi ảnh minh họa</div>
                      </div>
                      <div v-else class="upload-trigger-small" @click="triggerStepUpload(index)">
                        <span class="icon">📷</span> Thêm ảnh minh họa cho bước {{ index + 1 }}
                      </div>
                      <input type="file" :ref="el => stepInputRefs[index] = el" class="hidden-input" accept="image/*" @change="handleStepUpload($event, index)">
                    </div>
                  </div>
                </div>
              </transition-group>
            </div>
            <button class="btn-add-step-large" @click="addStep">+ Thêm bước nấu ăn tiếp theo</button>
          </div>
        </main>
      </div>
    </section>

    <div class="action-footer">
      <div class="footer-container">
        <div class="left">
          <button class="btn-preview">👁️ Xem trước bài viết</button>
        </div>
        <div class="right">
          <button class="btn-draft">Lưu bản nháp</button>
          <button class="btn-publish" :disabled="publishing" @click="handlePublish">
              {{ publishing ? 'Đang xuất bản...' : 'Xuất bản bài viết' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getCategories } from '@/services/categoryService'
import { createPost } from '@/services/postService'
import { uploadMedia } from '@/services/uploadService'
import api from '@/services/api'
import { toast } from '@/composables/useToast'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const eventId = route.query.eventId
const fileInput = ref(null)
const videoInput = ref(null)
const stepInputRefs = ref([])
const categories = ref([])
const publishing = ref(false)
const publishStatus = ref('')

const coverImageFile = ref(null) 
const videoFile = ref(null) 
const stepImageFiles = ref({}) 

// --- XỬ LÝ CURRENT USER TRÁNH UNDEFINED ---
const currentUser = computed(() => {
  if (authStore.user) return authStore.user;
  // Fallback lấy từ localStorage nếu sếp có lưu
  const savedUser = localStorage.getItem('user');
  return savedUser ? JSON.parse(savedUser) : {};
});

const userAvatar = computed(() => {
  const user = currentUser.value;
  const photo = user.avatar || user.image || user.photo || user.avatarUrl;
  if (photo) return photo;
  const nameForAvatar = user.fullName || user.username || 'Chef';
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(nameForAvatar)}&background=EA580C&color=fff&bold=true`;
})

const handleAvatarError = (e) => {
  e.target.src = `https://ui-avatars.com/api/?name=User&background=64748B&color=fff`;
}

const post = ref({
  title: '',
  description: '',
  categoryID: null, 
  image: null,
  video: null,
  cookingTime: '',
  level: 'Medium',
  ingredients: [{ name: '' }, { name: '' }, { name: '' }],
  steps: [{ id: Date.now(), desc: '', image: null }],
})

// --- QUẢN LÝ BỘ NHỚ ---
const localUrls = new Set()
const createSafeUrl = (file) => {
  const url = URL.createObjectURL(file)
  localUrls.add(url)
  return url
}
const cleanupUrls = () => {
  localUrls.forEach(url => URL.revokeObjectURL(url))
  localUrls.clear()
}
onBeforeUnmount(() => cleanupUrls())

onMounted(async () => {
  try {
    const res = await getCategories()
    categories.value = res 
  } catch (e) {
    toast.error('Không thể lấy danh mục từ hệ thống!')
  }
})

// --- HANDLERS MEDIA ---
const triggerUpload = () => fileInput.value.click()
const handleImageUpload = (e) => {
  const file = e.target.files[0]
  if (!file) return
  coverImageFile.value = file
  post.value.image = createSafeUrl(file) 
}

const triggerVideoUpload = () => videoInput.value.click()
const handleVideoUpload = (e) => {
  const file = e.target.files[0]
  if (!file) return
  if (file.size > 50 * 1024 * 1024) return toast.warn('Video phải nhẹ hơn 50MB!')
  videoFile.value = file
  post.value.video = createSafeUrl(file)
}
const removeVideo = () => { videoFile.value = null; post.value.video = null; }

const triggerStepUpload = (idx) => stepInputRefs.value[idx].click()
const handleStepUpload = (e, idx) => {
  const file = e.target.files[0]
  if (!file) return
  stepImageFiles.value[idx] = file
  post.value.steps[idx].image = createSafeUrl(file) 
}

const autoResize = (event) => {
  const element = event.target
  element.style.height = 'auto'
  element.style.height = element.scrollHeight + 'px'
}

const addIngredient = () => post.value.ingredients.push({ name: '' })
const removeIngredient = (idx) => { if(post.value.ingredients.length > 1) post.value.ingredients.splice(idx, 1) }

const addStep = () => post.value.steps.push({ id: Date.now(), desc: '', image: null })
const removeStep = (idx) => { if(post.value.steps.length > 1) post.value.steps.splice(idx, 1) }

const levelToInt = (lv) => ({ 'Easy': 1, 'Medium': 2, 'Hard': 3 }[lv] ?? 2)

// --- 🔥 HANDLE PUBLISH TỐI ƯU ---
const handlePublish = async () => {
  if (!post.value.title.trim()) return toast.warn('Sếp quên nhập tên món ăn rồi!')
  if (!coverImageFile.value && !post.value.image) return toast.warn('Thêm ảnh bìa cho bài viết thêm xịn nhé sếp!')

  // 🔥 Lấy chính xác Account ID (Kiểm tra mọi trường hợp)
  const accID = currentUser.value.accountID || currentUser.value.id || currentUser.value.accountId;
  if (!accID) {
    toast.error('Lỗi phiên đăng nhập! Vui lòng F5 trang hoặc đăng nhập lại.');
    console.error("Account ID bị rỗng. Dữ liệu currentUser hiện tại:", currentUser.value);
    return;
  }

  publishing.value = true
  publishStatus.value = '🚀 Đang chuẩn bị media...'

  try {
    const uploadTasks = []
    uploadTasks.push(coverImageFile.value ? uploadMedia(coverImageFile.value, 'posts') : Promise.resolve(post.value.image || ''))
    uploadTasks.push(videoFile.value ? uploadMedia(videoFile.value, 'videos') : Promise.resolve(post.value.video || ''))

    const stepIndices = Object.keys(stepImageFiles.value)
    stepIndices.forEach(idx => {
      uploadTasks.push(uploadMedia(stepImageFiles.value[idx], 'steps'))
    })
    
    publishStatus.value = '☁️ Đang đẩy dữ liệu lên Cloudinary...'
    const results = await Promise.all(uploadTasks)
    
    const coverMediaUrl = results[0] || ''
    const videoMediaUrl = results[1] || ''
    const stepUrls = results.slice(2)

    publishStatus.value = '💾 Đang lưu bài viết vào Database...'
    const ingredientsStr = post.value.ingredients.map(i => i.name).filter(Boolean).join(', ')
    
// 1. Chốt cứng số 1 nếu sếp không chọn danh mục (Không gửi null nữa)
    const finalCatID = (post.value.categoryID && !isNaN(post.value.categoryID)) ? parseInt(post.value.categoryID) : 1;
    
    // 2. Đảm bảo ID người dùng là số chuẩn
    const finalAccID = parseInt(accID);

    // 3. Đóng gói Payload "Bao vây" 360 độ
    const payload = {
      accountID: finalAccID, 
      categoryID: finalCatID,
      title: post.value.title.trim(),
      description: post.value.description || '',
      ingredients: ingredientsStr,
      media: coverMediaUrl,
      video: videoMediaUrl, 
      level: levelToInt(post.value.level),
      cookingTime: parseInt(post.value.cookingTime) || 30,
      
      // 🔥 MỞ KHÓA STEPS: Khớp 100% với StepRequestDTO (desc, image) của sếp
      steps: post.value.steps.map((s, i) => {
        const fileIdx = stepIndices.indexOf(i.toString());
        return {
          desc: s.desc || '', // Bắn vào biến 'desc' của Java
          image: fileIdx !== -1 ? stepUrls[fileIdx] : (s.image || '') // Bắn vào biến 'image' của Java
        }
      })
    };

    console.log("Payload gửi đi (Đã có Steps):", payload);
    
    const result = await createPost(payload)
    const newPostId = result?.postID || result?.id || result?.data?.postID 

    if (eventId && newPostId) {
        publishStatus.value = '🏆 Đang nộp bài vào sự kiện...'
        await api.post(`/api/events/submit`, { EventID: parseInt(eventId), PostID: newPostId })
        toast.success('🎉 Đã xuất bản và nộp bài dự thi thành công!')
    } else {
        toast.success('✨ Xuất bản bài viết thành công!')
    }

    cleanupUrls()
    router.push(newPostId ? `/post/${newPostId}` : '/home')

  } catch (err) {
    console.error("Lỗi xuất bản chi tiết:", err.response?.data || err)
    
    // Xử lý lỗi 400 thông minh
    const errorMsg = err.response?.data?.message || err.response?.data?.error || 'Có lỗi khi lưu! Hãy kiểm tra dữ liệu.';
    toast.error(errorMsg);
  } finally {
    publishing.value = false
  }
}
</script>

<style scoped lang="scss" src="./CreatePost.scss"></style>