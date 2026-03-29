<template>
  <div class="create-post-wrapper">
    
    <section class="hero-section-clean fade-in">
      <div class="hero-container-inner">
        
        <div class="hero-info-col">
          
          <div class="top-nav-bar">
            <div class="nav-left">
              <button @click="router.back()" class="btn-back-clean">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
                <span>Hủy bỏ</span>
              </button>
              <span class="nav-sep">/</span>
              <div class="category-badge-selector">
                <select v-model="post.categoryID" class="category-select-clean">
                  <option :value="null">CHỌN DANH MỤC</option>
                  <option v-for="cat in categories" :key="cat.categoryID" :value="cat.categoryID">
                    {{ (cat.categoryName || '').toUpperCase() }}
                  </option>
                </select>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="chevron-down"><path d="M6 9l6 6 6-6"/></svg>
              </div>
            </div>
          </div>

          <textarea v-model="post.title" class="recipe-title-display-input" placeholder="Tên món ăn..." rows="1" @input="autoResize"></textarea>
          <textarea v-model="post.description" class="recipe-desc-text-input" placeholder="Kể câu chuyện về món ăn này..." rows="2" @input="autoResize"></textarea>

          <div class="recipe-stats-group">
            <div class="stat-card-clean">
              <div class="icon-box time-box">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
              </div>
              <div class="stat-info">
                <span class="s-label">THỜI GIAN (PHÚT)</span>
                <input type="number" v-model="post.cookingTime" class="s-val-input" placeholder="30" min="1">
              </div>
            </div>

            <div class="stat-card-clean">
              <div class="icon-box fire-box">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M8.5 14.5A2.5 2.5 0 0011 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 2.4 5.6a4.5 4.5 0 01-1.5 2.3A4.5 4.5 0 0110 21c-2.5 0-4-2-4-4s1.5-3 2.5-2.5z"></path></svg>
              </div>
              <div class="stat-info">
                <span class="s-label">ĐỘ KHÓ</span>
                <select v-model="post.level" class="s-val-select">
                  <option value="Easy">Dễ</option>
                  <option value="Medium">Vừa</option>
                  <option value="Hard">Khó</option>
                </select>
              </div>
            </div>
          </div>

          <div class="hero-footer-actions">
            <div class="author-info-block">
              <div class="avatar-container">
                <img :src="userAvatar" alt="Author" @error="handleAvatarError">
                <div class="tick-badge"><svg width="8" height="8" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="4"><polyline points="20 6 9 17 4 12"></polyline></svg></div>
              </div>
              <div class="auth-text">
                <span class="auth-sub">Đầu bếp thực hiện</span>
                <span class="auth-name">{{ currentUser.fullName || currentUser.username || 'Đầu bếp GOMET' }}</span>
              </div>
            </div>
          </div>

        </div>

        <div class="hero-image-col">
          <div class="clean-image-frame uploader" :class="{'has-img': post.image}" @click="triggerUpload">
            <img v-if="post.image" :src="post.image" class="img-cover">
            <div v-else class="upload-placeholder">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg>
              <span>Nhấn để tải ảnh bìa (16:9)</span>
            </div>
            <div v-if="post.image" class="hover-glass-overlay">Thay đổi ảnh bìa</div>
            <input type="file" ref="fileInput" class="hidden-input" accept="image/*" @change="handleImageUpload">
          </div>
        </div>

      </div>
    </section>

    <section class="cooking-dashboard-premium fade-in">
      <div class="dashboard-container-inner">
        
        <aside class="dashboard-left-col">
          <div class="sticky-content-wrapper">
            
            <div class="video-card-luxury">
              <div class="card-header-dark">
                <div class="h-left">
                  <span class="pulse-dot-neon"></span>
                  <span class="caps-text">MASTERCLASS VIDEO (Tùy chọn)</span>
                </div>
              </div>
              <div class="video-aspect-frame uploader-video" @click="!post.video && triggerVideoUpload()">
                <div v-if="!post.video" class="video-placeholder-dark">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><polygon points="23 7 16 12 23 17 23 7"></polygon><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
                  <span>Tải Video Lên (Max 50MB)</span>
                </div>
                <template v-else>
                  <video :src="post.video" controls playsinline class="html5-player"></video>
                  <button class="btn-remove-media-float" @click.stop="removeVideo">Gỡ Video</button>
                </template>
                <input type="file" ref="videoInput" class="hidden-input" accept="video/*" @change="handleVideoUpload">
              </div>
            </div>

            <div class="widgets-group">
              <div class="premium-widget ingredients-widget">
                <div class="widget-header">
                  <div class="title-with-icon">
                    <div class="icon-box">
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg>
                    </div>
                    <h3>Nguyên liệu cần có</h3>
                  </div>
                </div>
                <div class="ingredients-list-clean">
                  <TransitionGroup name="list-anim">
                    <div v-for="(ing, index) in post.ingredients" :key="index" class="clean-check-row input-mode">
                      <div class="checkbox-visual"></div>
                      <input v-model="ing.name" class="i-name-input" placeholder="VD: 500g thịt bò..." @keyup.enter="addIngredient" ref="ingredientInputs">
                      <button class="btn-remove-ing" @click="removeIngredient(index)" v-if="post.ingredients.length > 1">✕</button>
                    </div>
                  </TransitionGroup>
                </div>
                <button class="btn-gradient-orange w-full" @click="addIngredient">
                  <span>+ Thêm nguyên liệu</span>
                </button>
              </div>
            </div>

          </div>
        </aside>

        <main class="dashboard-right-col">
          <div class="process-editorial-section">
            
            <div class="process-header">
              <h2 class="serif-title">Quy trình thực hiện</h2>
              <div class="header-actions">
                <span class="step-count-badge">{{ post.steps.length }} BƯỚC</span>
              </div>
            </div>

            <div class="modern-timeline">
              <transition-group name="list-anim">
                <div class="timeline-step" v-for="(step, index) in post.steps" :key="step.id">
                  
                  <div class="timeline-marker">
                    <div class="t-circle">{{ index + 1 }}</div>
                    <div v-if="index !== post.steps.length - 1" class="t-line"></div>
                  </div>
                  
                  <div class="timeline-content">
                    <div class="step-header-row">
                      <h4 class="step-subtitle">BƯỚC {{ index + 1 }}</h4>
                      <button class="btn-del-text" @click="removeStep(index)" v-if="post.steps.length > 1">Xóa bước này</button>
                    </div>
                    
                    <textarea v-model="step.desc" class="step-desc-input" placeholder="Chi tiết cách làm ở bước này..." rows="2" @input="autoResize"></textarea>
                    
                    <div class="step-gallery" @click="triggerStepUpload(index)">
                      <div class="gallery-img-box-lg uploader" :class="{'has-img': step.image}">
                        <img v-if="step.image" :src="step.image">
                        <div v-else class="upload-placeholder-small">
                          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg>
                          Tải ảnh minh họa lên
                        </div>
                        <div v-if="step.image" class="hover-zoom"><span>Đổi ảnh</span></div>
                      </div>
                      <input type="file" :ref="el => stepInputRefs[index] = el" class="hidden-input" accept="image/*" @change="handleStepUpload($event, index)">
                    </div>
                  </div>

                </div>
              </transition-group>
            </div>

            <button class="btn-add-step-large" @click="addStep">+ Thêm bước chế biến tiếp theo</button>

          </div>
        </main>

      </div>
    </section>

    <div class="floating-action-bar-center">
      <div class="fab-inner">
        <button class="btn-cancel-lux" @click="router.back()">Hủy bỏ</button>
        <button class="btn-publish-lux" @click="handlePublish">🚀 Xuất bản bài viết</button>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getCategories } from '@/services/categoryService'
import { createPost } from '@/services/postService'
import { uploadMedia } from '@/services/uploadService'
import api from '@/services/api'
import axios from 'axios'
import { toast } from '@/composables/useToast'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const eventId = route.query.eventId
const fileInput = ref(null)
const videoInput = ref(null)
const stepInputRefs = ref([])
const ingredientInputs = ref([])
const categories = ref([])

const coverImageFile = ref(null)
const videoFile = ref(null)
const stepImageFiles = ref({})

// LẤY THÔNG TIN USER
const currentUser = computed(() => {
  if (authStore.user) return authStore.user;
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
const handleAvatarError = (e) => { e.target.src = `https://ui-avatars.com/api/?name=User&background=64748B&color=fff`; }

// CẤU TRÚC DỮ LIỆU (KHỞI TẠO 1 Ô NGUYÊN LIỆU)
const post = ref({
  title: '', description: '', categoryID: null, image: null, video: null,
  cookingTime: '', level: 'Medium',
  ingredients: [{ name: '' }],
  steps: [{ id: Date.now(), desc: '', image: null }],
})

// QUẢN LÝ BỘ NHỚ RAM ẢNH PREVIEW
const localUrls = new Set()
const createSafeUrl = (file) => { const url = URL.createObjectURL(file); localUrls.add(url); return url; }
const cleanupUrls = () => { localUrls.forEach(url => URL.revokeObjectURL(url)); localUrls.clear(); }
onBeforeUnmount(() => cleanupUrls())

onMounted(async () => {
  try { categories.value = await getCategories() } catch (e) { toast.error('Lỗi lấy danh mục!') }
})

// HANDLERS UPLOAD (LOCAL PREVIEW)
const triggerUpload = () => fileInput.value.click()
const handleImageUpload = (e) => {
  const file = e.target.files[0]; if (!file) return;
  coverImageFile.value = file; post.value.image = createSafeUrl(file);
}

const triggerVideoUpload = () => videoInput.value.click()
const handleVideoUpload = (e) => {
  const file = e.target.files[0]; if (!file) return;
  if (file.size > 50 * 1024 * 1024) return toast.warn('Video tối đa 50MB!');
  videoFile.value = file; post.value.video = createSafeUrl(file);
}
const removeVideo = () => { videoFile.value = null; post.value.video = null; }

const triggerStepUpload = (idx) => stepInputRefs.value[idx].click()
const handleStepUpload = (e, idx) => {
  const file = e.target.files[0]; if (!file) return;
  stepImageFiles.value[idx] = file; post.value.steps[idx].image = createSafeUrl(file);
}

// TIỆN ÍCH DỮ LIỆU
const autoResize = (event) => { const el = event.target; el.style.height = 'auto'; el.style.height = el.scrollHeight + 'px'; }

const addIngredient = () => {
  post.value.ingredients.push({ name: '' })
  nextTick(() => {
    if (ingredientInputs.value && ingredientInputs.value.length > 0) {
      ingredientInputs.value[ingredientInputs.value.length - 1].focus();
    }
  });
}
const removeIngredient = (idx) => { if (post.value.ingredients.length > 1) post.value.ingredients.splice(idx, 1) }

const addStep = () => post.value.steps.push({ id: Date.now(), desc: '', image: null })
const removeStep = (idx) => { if (post.value.steps.length > 1) post.value.steps.splice(idx, 1) }
const levelToInt = (lv) => ({ 'Easy': 1, 'Medium': 2, 'Hard': 3 }[lv] ?? 2)

// --- KIỂM TRA TỪ KHÓA CẤM (GỌI BACKEND ĐỂ BẢO MẬT) ---
const checkContentPolicy = async (text) => {
  if (!text) return false;
  try {
    // Chỉ gửi text lên Backend để hỏi, Frontend không hề biết blacklist là gì
    const res = await axios.post('http://localhost:8080/api/blacklist/check', { content: text });
    return res.data.hasBadWord; // Backend sẽ trả về true nếu vi phạm, false nếu an toàn
  } catch (error) {
    console.warn("Lỗi server khi kiểm duyệt, cho phép qua để Backend lớp 2 tự chặn:", error);
    return false;
  }
};

// ==========================================
// ĐĂNG BÀI NỀN (BACKGROUND PUBLISH)
// ==========================================
const handlePublish = async () => {
  if (!post.value.title.trim()) return toast.warn('Vui lòng nhập tên món ăn!')
  if (!post.value.description.trim()) return toast.warn('Vui lòng nhập mô tả cho món ăn!')
  if (!coverImageFile.value && !post.value.image) return toast.warn('Vui lòng thêm ảnh bìa cho bài viết!')

  const validIngredients = post.value.ingredients.filter(i => i.name.trim() !== '')
  if (validIngredients.length === 0) return toast.warn('Vui lòng nhập ít nhất 1 nguyên liệu!')

  const validSteps = post.value.steps.filter(s => s.desc.trim() !== '')
  if (validSteps.length === 0) return toast.warn('Vui lòng nhập ít nhất 1 bước chế biến!')

  const accID = currentUser.value.accountID || currentUser.value.id || currentUser.value.accountId;
  if (!accID) return toast.error('Lỗi phiên đăng nhập!');

  // Kiểm tra từ khóa cấm ở Frontend trước khi cho đăng
  const fullText = [
    post.value.title,
    post.value.description,
    post.value.ingredients.map(i => i.name).join(' '),
    post.value.steps.map(s => s.desc).join(' ')
  ].join(' ');

  const isViolating = await checkContentPolicy(fullText);
  if (isViolating) {
    toast.error('Bài viết vi phạm tiêu chuẩn cộng đồng!');
    return;
  }

  // 1. Gói dữ liệu
  const payloadData = {
    title: post.value.title.trim(),
    description: post.value.description || '',
    categoryID: (post.value.categoryID && !isNaN(post.value.categoryID)) ? parseInt(post.value.categoryID) : 1,
    cookingTime: parseInt(post.value.cookingTime) || 30,
    level: levelToInt(post.value.level),
    ingredients: post.value.ingredients.map(i => i.name).filter(Boolean).join(', '),
    steps: post.value.steps.map(s => ({ desc: s.desc || '', image: s.image || '' }))
  };
  const filesToUpload = { cover: coverImageFile.value, video: videoFile.value, steps: { ...stepImageFiles.value } };
  const currentEventId = eventId;

  // 2. Hất ra trang chủ ngay lập tức
  router.push('/home');
  toast.info('🚀 Đang tải bài viết lên hệ thống trong nền...');

  // 3. Tiến trình upload ngầm
  (async () => {
    try {
      const uploadTasks = [];
      uploadTasks.push(filesToUpload.cover ? uploadMedia(filesToUpload.cover, 'posts') : Promise.resolve(''));
      uploadTasks.push(filesToUpload.video ? uploadMedia(filesToUpload.video, 'videos') : Promise.resolve(''));

      const stepIndices = Object.keys(filesToUpload.steps);
      stepIndices.forEach(idx => uploadTasks.push(uploadMedia(filesToUpload.steps[idx], 'steps')));

      const results = await Promise.all(uploadTasks);
      payloadData.media = results[0];
      payloadData.video = results[1];

      const stepUrls = results.slice(2);
      payloadData.steps = payloadData.steps.map((s, i) => {
        const fileIdx = stepIndices.indexOf(i.toString());
        return { desc: s.desc, image: fileIdx !== -1 ? stepUrls[fileIdx] : s.image }
      });
      payloadData.accountID = parseInt(accID);

      // Gọi API Lưu
      const result = await createPost(payloadData);
      const newPostId = result?.postID || result?.id || result?.data?.postID;

      if (currentEventId && newPostId) {
         await api.post(`/api/events/submit`, { EventID: parseInt(currentEventId), PostID: newPostId });
      }

      // Thông báo kết quả
      if (result?.isApproved === 1 || result?.data?.isApproved === 1) {
         toast.success('🎉 Tuyệt vời! Bài viết đã được tự động duyệt và lên sóng!');
      } else {
         toast.success('✨ Đã tải bài lên thành công! Đang chờ Admin duyệt.');
      }

    } catch (err) {
       const msg = err.response?.data?.message || '❌ Bài viết không hợp lệ hoặc thao tác quá nhanh. Vui lòng thử lại sau!';
       toast.error(msg);
    }
  })();
}
</script>

<style scoped lang="scss">
/* --- 1. HERO SECTION (Giống RecipeHero.vue) --- */
.hero-section-clean { width: 100%; background: var(--color-neutral-0, #ffffff); padding: 40px 0 80px; font-family: var(--font-ui, 'Mulish', sans-serif); }
.hero-container-inner { max-width: 1280px; margin: 0 auto; padding: 0 24px; display: grid; grid-template-columns: 1.15fr 0.85fr; gap: 80px; align-items: start; }

.top-nav-bar { display: flex; align-items: center; margin-bottom: 30px; }
.nav-left { display: flex; align-items: center; gap: 12px; }
.btn-back-clean { display: flex; align-items: center; gap: 6px; background: none; border: none; color: var(--color-neutral-500, #64748b); font-weight: 700; font-size: 0.95rem; cursor: pointer; transition: 0.2s; &:hover { color: var(--color-primary-500, #ea580c); } }
.nav-sep { color: var(--color-neutral-300, #cbd5e1); font-weight: 300; }

/* DANH MỤC NỔI BẬT HƠN */
.category-badge-selector {
  display: flex; align-items: center; gap: 4px;
  background: var(--color-primary-50, #ffedd5);
  padding: 6px 12px;
  border-radius: 100px;
  color: var(--color-primary-600, #ea580c);
  border: 1px solid #fed7aa;
}
.category-select-clean { 
  border: none; outline: none; background: transparent; color: inherit; 
  font-weight: 900; font-size: 0.8rem; letter-spacing: 1px; cursor: pointer; padding: 0; appearance: none; 
}
.chevron-down { pointer-events: none; }

.recipe-title-display-input { width: 100%; border: none; outline: none; background: transparent; font-family: var(--font-serif, 'Playfair Display', serif); font-size: 3.5rem; line-height: 1.15; color: var(--color-neutral-900, #0f172a); margin: 0 0 16px 0; font-weight: 900; letter-spacing: -0.5px; resize: none; overflow: hidden; }
.recipe-title-display-input::placeholder { color: var(--color-neutral-300, #cbd5e1); }

.recipe-desc-text-input { width: 100%; border: none; outline: none; background: transparent; font-size: 1.1rem; line-height: 1.6; color: var(--color-neutral-500, #64748b); margin-bottom: 40px; resize: none; overflow: hidden; font-family: inherit; }

.recipe-stats-group { display: flex; gap: 16px; margin-bottom: 40px; flex-wrap: wrap; }
.stat-card-clean { display: flex; align-items: center; gap: 12px; padding: 10px 20px 10px 10px; background: var(--color-neutral-0, #ffffff); border-radius: var(--radius-pill, 100px); border: 1px solid var(--color-neutral-100, #e2e8f0); box-shadow: var(--shadow-sm, 0 2px 8px rgba(0,0,0,0.02)); }
.icon-box { width: 34px; height: 34px; border-radius: var(--radius-circle, 50%); display: flex; align-items: center; justify-content: center; &.time-box { background: #f3e8ff; color: #9333ea; } &.fire-box { background: var(--color-primary-50, #ffedd5); color: var(--color-primary-600, #ea580c); } }
.stat-info { display: flex; flex-direction: column; justify-content: center; .s-label { font-size: 0.6rem; font-weight: 800; color: var(--color-neutral-500, #64748b); text-transform: uppercase; letter-spacing: 0.5px; } }
.s-val-input, .s-val-select { border: none; outline: none; background: transparent; font-size: 0.95rem; font-weight: 800; color: var(--color-neutral-900, #0f172a); width: 60px; font-family: inherit; }
.s-val-select { width: auto; cursor: pointer; }

.hero-footer-actions { padding-top: 30px; border-top: 1px solid var(--color-border, #f1f5f9); }
.author-info-block { display: flex; align-items: center; gap: 12px; .avatar-container { position: relative; img { width: 46px; height: 46px; border-radius: 50%; object-fit: cover; border: 2px solid #fff; box-shadow: 0 2px 6px rgba(0,0,0,0.1); } .tick-badge { position: absolute; bottom: 0; right: 0; background: #ea580c; width: 14px; height: 14px; border-radius: 50%; display: flex; align-items: center; justify-content: center; border: 2px solid #fff; } } .auth-text { display: flex; flex-direction: column; .auth-sub { font-size: 0.75rem; color: #64748b; } .auth-name { font-weight: 800; color: #0f172a; font-size: 1rem; } } }

.hero-image-col { width: 100%; }
.clean-image-frame { position: relative; height: 600px; border-radius: var(--radius-card, 40px); background: var(--color-neutral-50, #f8fafc); overflow: hidden; box-shadow: var(--shadow-exec, 0 20px 40px -10px rgba(0,0,0,0.08)); cursor: pointer; display: flex; align-items: center; justify-content: center; border: 2px dashed #cbd5e1; transition: 0.3s; }
.clean-image-frame.has-img { border: none; }
.clean-image-frame:hover { border-color: #ea580c; }
.img-cover { width: 100%; height: 100%; object-fit: cover; transition: transform 0.6s ease; }
.clean-image-frame:hover .img-cover { transform: scale(1.02); }
.upload-placeholder { display: flex; flex-direction: column; align-items: center; color: #94a3b8; gap: 12px; font-weight: 600; }
.hover-glass-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.4); color: white; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 1.1rem; opacity: 0; transition: 0.3s; backdrop-filter: blur(2px); }
.clean-image-frame:hover .hover-glass-overlay { opacity: 1; }

/* --- 2. BODY SECTION (Giống RecipeInformation.vue) --- */
.cooking-dashboard-premium { width: 100%; background-color: #fafaf9; padding: 40px 0 120px; font-family: var(--font-ui, 'Mulish', sans-serif); border-top: 1px solid #f1f5f9; }
.dashboard-container-inner { max-width: 1240px; margin: 0 auto; padding: 0 24px; display: grid; grid-template-columns: 420px 1fr; gap: 64px; align-items: start; }
.dashboard-left-col { position: sticky; top: 100px; }

.video-card-luxury { background: #0f172a; border-radius: 20px; overflow: hidden; box-shadow: 0 20px 40px -15px rgba(0,0,0,0.15); margin-bottom: 24px; }
.card-header-dark { padding: 14px 20px; background: #0f172a; .h-left { display: flex; align-items: center; gap: 10px; color: #fff; font-weight: 700; font-size: 0.75rem; letter-spacing: 1.5px; } .pulse-dot-neon { width: 8px; height: 8px; background: #ea580c; border-radius: 50%; box-shadow: 0 0 10px #ea580c; animation: pulse 2s infinite; } }
.video-aspect-frame { position: relative; padding-bottom: 56.25%; height: 0; background: #000; cursor: pointer; }
.video-placeholder-dark { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #64748b; font-size: 0.9rem; font-weight: 600; gap: 8px; border: 2px dashed #334155; margin: 10px; border-radius: 12px; transition: 0.2s; }
.video-aspect-frame:hover .video-placeholder-dark { border-color: #ea580c; color: #fff; }
.html5-player { position: absolute; inset: 0; width: 100%; height: 100%; border: none; }
.btn-remove-media-float { position: absolute; top: 10px; right: 10px; background: rgba(0,0,0,0.6); color: white; border: none; padding: 6px 12px; border-radius: 8px; font-size: 0.8rem; cursor: pointer; }

.widgets-group { background: #ffffff; border-radius: 24px; border: 1px solid #e2e8f0; box-shadow: 0 4px 20px rgba(0,0,0,0.03); }
.premium-widget { padding: 28px; }
.widget-header { margin-bottom: 20px; .title-with-icon { display: flex; align-items: center; gap: 12px; .icon-box { width: 32px; height: 32px; background: #fff4ed; color: #ea580c; border-radius: 10px; display: flex; align-items: center; justify-content: center; } h3 { font-size: 1.05rem; font-weight: 800; color: #0f172a; margin: 0; } } }
.ingredients-list-clean { display: flex; flex-direction: column; gap: 8px; margin-bottom: 20px; }
.clean-check-row.input-mode { display: flex; align-items: center; gap: 14px; padding: 8px 12px; border-radius: 12px; border: 1px solid transparent; transition: 0.2s; &:focus-within { border-color: #e2e8f0; background: #f8fafc; } }
.checkbox-visual { width: 22px; height: 22px; border: 2px solid #cbd5e1; border-radius: 50%; flex-shrink: 0; }
.i-name-input { flex: 1; border: none; outline: none; background: transparent; font-weight: 600; color: #334155; font-size: 0.95rem; font-family: inherit; }
.btn-remove-ing { background: transparent; border: none; color: #cbd5e1; cursor: pointer; font-weight: bold; transition: 0.2s; &:hover { color: #ef4444; } }
.btn-gradient-orange { padding: 14px 24px; border-radius: 16px; border: none; background: linear-gradient(135deg, #ea580c, #f59e0b); color: #fff; font-weight: 800; font-size: 0.95rem; cursor: pointer; box-shadow: 0 8px 20px -6px rgba(234, 88, 12, 0.4); width: 100%; transition: 0.3s; &:hover { transform: translateY(-2px); box-shadow: 0 12px 24px -6px rgba(234, 88, 12, 0.5); } }

.dashboard-right-col { min-width: 0; padding-top: 10px; }
.process-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 40px; .serif-title { font-family: var(--font-serif, 'Playfair Display', serif); font-size: 2.5rem; font-weight: 900; color: #0f172a; margin: 0; line-height: 1.2; letter-spacing: -0.5px; } .step-count-badge { color: #64748b; font-weight: 800; font-size: 0.85rem; letter-spacing: 1px; } }

.modern-timeline { display: flex; flex-direction: column; }
.timeline-step { display: flex; gap: 28px; }
.timeline-marker { display: flex; flex-direction: column; align-items: center; width: 32px; flex-shrink: 0; .t-circle { width: 32px; height: 32px; border-radius: 50%; background: #fff4ed; color: #ea580c; display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 1rem; z-index: 2; border: 2px solid #fff; box-shadow: 0 0 0 1px #ffedd5; } .t-line { width: 2px; flex: 1; background: repeating-linear-gradient(to bottom, #e2e8f0 0, #e2e8f0 4px, transparent 4px, transparent 8px); margin: 8px 0; } }
.timeline-content { flex: 1; padding-bottom: 56px; }
.step-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.step-subtitle { font-size: 0.85rem; font-weight: 800; color: #ea580c; margin: 0; letter-spacing: 0.5px; }
.btn-del-text { background: transparent; border: none; color: #ef4444; font-size: 0.8rem; font-weight: 700; cursor: pointer; }
.step-desc-input { width: 100%; border: none; outline: none; background: transparent; font-size: 1.1rem; line-height: 1.8; color: #334155; font-weight: 500; margin-bottom: 16px; resize: none; font-family: inherit; }

/* 🔥 ẢNH MINH HỌA LỚN HƠN (280px) */
.step-gallery { display: grid; grid-template-columns: 500px; gap: 16px; cursor: pointer; }
.gallery-img-box-lg { position: relative; border-radius: 16px; overflow: hidden; aspect-ratio: 16/9; border: 2px dashed #cbd5e1; display: flex; align-items: center; justify-content: center; transition: 0.2s; &.has-img { border: none; } &:hover { border-color: #ea580c; } img { width: 100%; height: 100%; object-fit: cover; } }
.upload-placeholder-small { display: flex; flex-direction: column; align-items: center; gap: 6px; color: #94a3b8; font-size: 0.85rem; font-weight: 600; }
.hover-zoom { position: absolute; inset: 0; background: rgba(15, 23, 42, 0.5); display: flex; align-items: center; justify-content: center; opacity: 0; transition: 0.3s; color: white; font-weight: 700; }
.gallery-img-box-lg:hover .hover-zoom { opacity: 1; }
.btn-add-step-large { width: 100%; padding: 20px; background: white; border: 2px dashed #cbd5e1; border-radius: 16px; font-size: 1.1rem; font-weight: 700; color: #64748b; cursor: pointer; transition: 0.3s; &:hover { background: #f8fafc; border-color: #ea580c; color: #ea580c; } }

/* 🔥 FLOAT ACTION BAR NÉ GOMET AI */
.floating-action-bar-center { 
  position: fixed; bottom: 30px; left: 0; width: 100%; 
  pointer-events: none; /* Để có thể click xuyên qua div bao ngoài vào web */
  display: flex; justify-content: center; z-index: 1000; 
}
.fab-inner {
  pointer-events: auto; /* Kích hoạt click lại cho các nút bên trong */
  background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(12px);
  padding: 10px 20px; border-radius: 100px;
  display: flex; gap: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  border: 1px solid rgba(255,255,255,0.4);
}
.btn-cancel-lux { padding: 12px 28px; background: white; border: 1px solid #e2e8f0; border-radius: 100px; font-weight: 700; color: #64748b; cursor: pointer; font-size: 0.95rem; transition: 0.2s; &:hover { background: #f1f5f9; color: #0f172a; } }
.btn-publish-lux { padding: 12px 36px; background: linear-gradient(135deg, #ea580c, #f59e0b); color: white; border: none; border-radius: 100px; font-weight: 800; font-size: 1rem; cursor: pointer; box-shadow: 0 8px 20px -6px rgba(234, 88, 12, 0.4); transition: 0.3s; &:hover { transform: translateY(-2px); box-shadow: 0 12px 25px -6px rgba(234, 88, 12, 0.5); } }

/* UTILS */
.hidden-input { display: none; }
.fade-in { opacity: 0; animation: fadeIn 0.6s ease forwards; }
@keyframes fadeIn { to { opacity: 1; transform: translateY(0); } }
@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }
.list-anim-enter-active, .list-anim-leave-active { transition: all 0.3s ease; }
.list-anim-enter-from, .list-anim-leave-to { opacity: 0; transform: translateX(-20px); }

@media (max-width: 1024px) {
  .hero-container-inner, .dashboard-container-inner { grid-template-columns: 1fr; gap: 40px; }
  .recipe-title-display-input { font-size: 2.8rem; }
  .clean-image-frame { height: 400px; border-radius: 24px; }
  .dashboard-left-col { position: static; }
}
</style>