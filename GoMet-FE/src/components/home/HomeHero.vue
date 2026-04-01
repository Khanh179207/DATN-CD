<template>
  <header class="vogue-hero-wrapper" :style="cssVars">
    
    <div v-if="isLoading" class="hero-loader">
      <div class="spinner"></div>
    </div>

    <div class="ambient-bg">
      <div class="glow-orb orb-1"></div>
      <div class="glow-orb orb-2"></div>
      <div class="noise-texture"></div>
    </div>

    <transition name="hero-fade" mode="out-in">
      <div class="container hero-content" 
           v-if="!isLoading && currentSlideData" 
           :key="currentSlideData.id"
           @touchstart="handleTouchStart" 
           @touchend="handleTouchEnd"
      >
        
        <div class="text-column">
          
          <div class="anim-slide-up delay-1">
            <div class="lux-badge">
              <span class="live-dot"></span>
              {{ currentSlideData.badge.toUpperCase() }}
            </div>
          </div>
          
          <div class="anim-slide-up delay-2">
            <h1 class="main-title">
              <span class="t-prefix">{{ currentSlideData.titleTop }}</span><br>
              <span class="t-core">{{ currentSlideData.titleHighlight }}</span>
            </h1>
          </div>
          
          <div class="meta-pills anim-slide-up delay-3">
            <div class="meta-pill">
              <div class="m-icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg></div>
              <div class="m-info">
                <span class="m-val">{{ currentSlideData.time }}</span>
                <span class="m-lbl">Thời gian</span>
              </div>
            </div>
            <div class="meta-pill">
              <div class="m-icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"></path></svg></div>
              <div class="m-info">
                <span class="m-val">{{ currentSlideData.difficulty }}</span>
                <span class="m-lbl">Độ khó</span>
              </div>
            </div>
            <div class="meta-pill">
              <div class="m-icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="8" y1="6" x2="21" y2="6"></line><line x1="8" y1="12" x2="21" y2="12"></line><line x1="8" y1="18" x2="21" y2="18"></line><line x1="3" y1="6" x2="3.01" y2="6"></line><line x1="3" y1="12" x2="3.01" y2="12"></line><line x1="3" y1="18" x2="3.01" y2="18"></line></svg></div>
              <div class="m-info">
                <span class="m-val">{{ currentSlideData.stepCount }} Bước</span>
                <span class="m-lbl">Quy trình</span>
              </div>
            </div>
          </div>

          <div class="anim-slide-up delay-4">
            <p class="description">{{ currentSlideData.desc }}</p>
          </div>

          <div class="action-row anim-slide-up delay-5">
            <div class="btn-glow-wrap">
              <div class="btn-glow"></div>
              <button class="btn-primary" @click="$emit('view-detail', currentSlideData.id)">
                Khám Phá Ngay
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
              </button>
            </div>

            <button v-if="currentSlideData.video" class="btn-video" @click="$emit('view-detail', currentSlideData.id)">
              <div class="play-circle"><svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><polygon points="5 3 19 12 5 21 5 3"></polygon></svg></div>
              <span>Xem Video</span>
            </button>
          </div>
        </div>

        <div class="visual-column">
          <div class="visual-stage">
            
            <div class="media-frame" 
                 @click="$emit('view-detail', currentSlideData.id)"
                 @mouseenter="isPaused = true"
                 @mouseleave="isPaused = false"
            >
              <video 
                v-if="currentSlideData.video" 
                :src="currentSlideData.video" 
                class="media-asset cinematic-zoom" 
                autoplay loop muted playsinline
              ></video>
              <img v-else :src="currentSlideData.mainImg" class="media-asset cinematic-zoom" :alt="currentSlideData.titleHighlight">
              
              <div class="glass-border"></div>
              <div class="inner-shadow"></div>
            </div>

            <div class="glass-card stat-card float-anim-1">
              <div class="gc-inner">
                <div class="stat-top">
                  <span class="s-val" :class="{'text-new': currentSlideData.rating === 'Mới'}">{{ currentSlideData.rating }}</span>
                  <svg v-if="currentSlideData.rating !== 'Mới'" width="18" height="18" viewBox="0 0 24 24" fill="#F59E0B" class="star"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                  <span v-else class="sparkle">✨</span>
                </div>
                <div class="s-lbl">{{ currentSlideData.rating === 'Mới' ? 'Chưa có đánh giá' : currentSlideData.views + ' Lượt xem' }}</div>
              </div>
            </div>

            <div class="glass-card author-card float-anim-2">
              <div class="gc-inner row-layout">
                <img :src="currentSlideData.review.avatar" alt="Author" class="a-avt">
                <div class="a-info-col">
                  <div class="a-name">{{ currentSlideData.review.name }}</div>
                  <div class="a-likes">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="#ef4444" stroke="#ef4444" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
                    <span>{{ currentSlideData.likes }} Lượt thích</span>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="currentSlideData.topComment" class="glass-card comment-card float-anim-3">
              <div class="c-head">
                <img :src="currentSlideData.topComment.avatar" alt="User">
                <div class="c-user">
                  <span class="c-name">{{ currentSlideData.topComment.author }}</span>
                  <span class="c-tag">Bình luận nổi bật</span>
                </div>
              </div>
              <p class="c-text">"{{ currentSlideData.topComment.content }}"</p>
            </div>

          </div>
        </div>

      </div>
    </transition>

    <div class="dock-nav" v-if="slides.length > 1">
      <button class="d-btn" @click="prevSlide"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M15 18l-6-6 6-6"/></svg></button>
      <div class="d-dots">
        <div v-for="(slide, index) in slides" :key="index" class="d-wrap" @click="goToSlide(index)">
          <div class="d-core" :class="{ active: index === currentIndex }"></div>
          <svg v-if="index === currentIndex" class="d-ring" :class="{ 'is-paused': isPaused }" viewBox="0 0 36 36">
            <circle class="ring-bg" cx="18" cy="18" r="16"></circle>
            <circle class="ring-progress" cx="18" cy="18" r="16" @animationend="nextSlide"></circle>
          </svg>
        </div>
      </div>
      <button class="d-btn" @click="nextSlide"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M9 18l6-6-6-6"/></svg></button>
    </div>

  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import api from '@/services/api' 

defineEmits(['view-detail'])

const slides = ref([])
const currentIndex = ref(0)
const isLoading = ref(true)
const isPaused = ref(false)

const colorTemplates = [
  { themeColor: "#EA580C", titleTop: "Gợi ý" },
  { themeColor: "#059669", titleTop: "Thanh Mát" },
  { themeColor: "#BE123C", titleTop: "Đậm Đà" }
]

const currentSlideData = computed(() => slides.value[currentIndex.value] || null)

const cssVars = computed(() => {
  const cSlide = currentSlideData.value;
  return {
    '--theme-color': cSlide?.themeColor || '#EA580C',
    '--theme-color-rgb': hexToRgb(cSlide?.themeColor || '#EA580C')
  }
})

const hexToRgb = (hex) => {
  let result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  return result ? `${parseInt(result[1], 16)}, ${parseInt(result[2], 16)}, ${parseInt(result[3], 16)}` : '234, 88, 12';
}

const truncateText = (text, length) => {
  if (!text) return 'Khám phá công thức tuyệt hảo được chọn lọc dành riêng cho bạn.'
  return text.length > length ? text.substring(0, length) + '...' : text;
}

// --- LOGIC GỌI API & MAP DATA ---
const fetchHeroData = async () => {
  isLoading.value = true;
  try {
    let fetchedPosts = [];

    // 1. Cố gắng lấy 3 bài từ cấu hình hệ thống
    try {
      const configRes = await api.get('/api/system-config');
      const configs = Array.isArray(configRes.data) ? configRes.data : (configRes.data?.data || []);
      const ids = ['HERO_POST_1', 'HERO_POST_2', 'HERO_POST_3']
        .map(key => configs.find(c => c.configKey === key || c.ConfigKey === key)?.configValue)
        .filter(id => id && String(id).trim() !== '' && !isNaN(Number(id)));

      if (ids.length > 0) {
        const promises = ids.map(id => api.get(`/api/posts/${id}`).then(res => res.data?.data || res.data).catch(() => null));
        const results = await Promise.all(promises);
        fetchedPosts = results.filter(p => p !== null && (p.postID || p.id));
      }
    } catch (e) {}

    // 2. Nếu thiếu thì bù thêm bài mới nhất
    if (fetchedPosts.length < 3) {
      try {
        const latestRes = await api.get('/api/posts'); 
        const latest = Array.isArray(latestRes.data) ? latestRes.data : (latestRes.data?.content || latestRes.data?.data || []);
        const needed = 3 - fetchedPosts.length;
        
        // Lấy ra các bài tóm tắt còn thiếu
        const extraSummaries = latest
          .filter(lp => !fetchedPosts.find(fp => (fp.postID || fp.id) === (lp.postID || lp.id)))
          .slice(0, needed);

        // 🔥 FIX LỖI SỐ BƯỚC: Gọi API lấy "Chi Tiết" cho các bài bù vào để lấy được mảng steps
        const extraPromises = extraSummaries.map(summary => {
            const id = summary.postID || summary.id;
            return api.get(`/api/posts/${id}`)
                      .then(res => res.data?.data || res.data)
                      .catch(() => summary); // Lỗi thì dùng lại bản tóm tắt
        });
        
        const extraFullDetails = await Promise.all(extraPromises);
        fetchedPosts = [...fetchedPosts, ...extraFullDetails];

      } catch (e) { 
        console.warn("Không tải được bài mới nhất", e);
      }
    }

    // 3. GỌI API BATCH LẤY TOP COMMENT TỪ BACKEND
    if (fetchedPosts.length > 0) {
      try {
        const postIds = fetchedPosts.map(p => p.postID || p.id).filter(Boolean);
        
        if (postIds.length > 0) {
          const cmtRes = await api.get('/api/posts/top-comments-batch', {
            params: { postIds: postIds.join(',') }
          });
          
          const topCommentsMap = cmtRes.data || {};
          
          // Đắp Top Comment tương ứng vào từng Post
          fetchedPosts.forEach(p => {
            const cmt = topCommentsMap[p.postID || p.id];
            if (cmt && cmt.content && cmt.content !== "[Người dùng đã xóa bình luận này]" && cmt.content.trim() !== '') {
                 p.topComment = {
                     content: truncateText(cmt.content, 75),
                     author: cmt.author || 'Thành viên',
                     avatar: cmt.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(cmt.author || 'G')}&background=EA580C&color=fff`
                 };
            } else {
                 p.topComment = null;
            }
          });
        }
      } catch (e) {
        console.error('Lỗi khi lấy Top Comments:', e);
      }
    }

    // 4. MAP DATA
    if(fetchedPosts.length > 0) {
        slides.value = fetchedPosts.map((p, index) => {
          const template = colorTemplates[index % 3]; 
          const authorName = p.authorName || p.author || p.account?.username || 'GoMet Chef';
          
          let stepCount = 0;
          if (Array.isArray(p.steps)) stepCount = p.steps.length;
          else if (Array.isArray(p.cookingSteps)) stepCount = p.cookingSteps.length;
          else if (typeof p.stepCount === 'number') stepCount = p.stepCount; 

          const ratingValue = (p.avgRating && Number(p.avgRating) > 0) ? Number(p.avgRating).toFixed(1) : 'Mới';

          return {
            id: p.postID || p.id,
            badge: (p.categoryName || p.category || 'MÓN NỔI BẬT'),
            titleTop: template.titleTop,
            titleHighlight: p.title, 
            desc: truncateText(p.description || p.desc, 140),
            time: p.cookingTime ? `${p.cookingTime} Phút` : (p.time || '30 Phút'),
            difficulty: p.level === 1 ? 'Dễ' : (p.level === 3 ? 'Khó' : 'Vừa'),
            stepCount: stepCount, 
            views: p.views || 0,
            likes: p.favoriteCount || p.likeCount || p.likes || 0,
            rating: ratingValue,
            topComment: p.topComment, 
            video: p.video || p.videoUrl || null,
            mainImg: p.media || p.image || p.thumbnail || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80',
            themeColor: template.themeColor,
            review: { name: authorName, avatar: p.authorAvatar || p.account?.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(authorName)}&background=EA580C&color=fff` }
          }
        });
    } else {
        slides.value = [{ id: 1, titleHighlight: 'Khám Phá', titleTop: 'Mới', desc: 'Không tải được dữ liệu', mainImg: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80', badge: 'Lỗi', themeColor: '#EA580C', time: '0', difficulty: 'N/A', stepCount: 0, views: 0, likes: 0, rating: 'Mới', topComment: null, review: { name: 'Admin', avatar: '' } }];
    }

  } catch (error) {
  } finally {
    isLoading.value = false;
  }
}

// --- ĐIỀU HƯỚNG ---
const nextSlide = () => { currentIndex.value = (currentIndex.value + 1) % slides.value.length }
const prevSlide = () => { currentIndex.value = (currentIndex.value - 1 + slides.value.length) % slides.value.length }
const goToSlide = (index) => { currentIndex.value = index; }

// SWIPE MOBILE
const touchStartX = ref(0)
const touchStartY = ref(0)
const handleTouchStart = (e) => { touchStartX.value = e.touches[0].clientX; touchStartY.value = e.touches[0].clientY; isPaused.value = true; }
const handleTouchEnd = (e) => {
  const dx = e.changedTouches[0].clientX - touchStartX.value
  const dy = e.changedTouches[0].clientY - touchStartY.value
  if (Math.abs(dx) > 50 && Math.abs(dx) > Math.abs(dy)) { dx < 0 ? nextSlide() : prevSlide() }
  isPaused.value = false;
}

onMounted(() => { fetchHeroData(); })
onUnmounted(() => { })
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800;900&family=Playfair+Display:ital,wght@0,700;0,900;1,700&display=swap');

/* ================= BỐ CỤC CHÍNH ================= */
.vogue-hero-wrapper {
  position: relative; width: 100%; min-height: 760px; 
  display: flex; align-items: center; justify-content: center; 
  overflow: hidden; background-color: #FAFAFA; 
  font-family: 'Plus Jakarta Sans', sans-serif;
  border-bottom: 1px solid #F1F5F9;
}

.ambient-bg { position: absolute; inset: 0; z-index: 1; pointer-events: none; overflow: hidden;}
.glow-orb { position: absolute; border-radius: 50%; filter: blur(100px); opacity: 0.12; animation: orbit 20s infinite alternate linear; transition: background 1s ease; }
.orb-1 { width: 60vw; height: 60vw; top: -20%; right: -10%; background: var(--theme-color); }
.orb-2 { width: 50vw; height: 50vw; bottom: -20%; left: -10%; background: #cbd5e1; animation-direction: alternate-reverse; }
.noise-texture { position: absolute; inset: 0; background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E"); opacity: 0.25; mix-blend-mode: soft-light; }
@keyframes orbit { 0% { transform: translate(0, 0) scale(1); } 100% { transform: translate(-3%, 3%) scale(1.05); } }

.hero-loader { position: absolute; z-index: 100; display: flex; align-items: center; justify-content: center; width: 100%; height: 100%; background: #FAFAFA; }
.spinner { width: 44px; height: 44px; border: 4px solid #f1f5f9; border-left-color: var(--theme-color); border-radius: 50%; animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }

.hero-content { position: relative; z-index: 10; width: 100%; max-width: 1350px; margin: 0 auto; padding: 60px 40px 100px; display: grid; grid-template-columns: 1fr 1fr; gap: 70px; align-items: center; }

/* ================= CỘT TRÁI (TEXT) ================= */
.text-column { display: flex; flex-direction: column; padding-right: 20px; }

.lux-badge { display: inline-flex; align-items: center; gap: 8px; background: rgba(var(--theme-color-rgb), 0.06); border: 1px solid rgba(var(--theme-color-rgb), 0.15); padding: 8px 20px; border-radius: 100px; font-size: 0.75rem; font-weight: 800; letter-spacing: 1.5px; color: var(--theme-color); width: fit-content; margin-bottom: 24px; }
.live-dot { width: 6px; height: 6px; background: var(--theme-color); border-radius: 50%; animation: pulse 2s infinite; }
@keyframes pulse { 0%, 100% { opacity: 1; transform: scale(1); } 50% { opacity: 0.5; transform: scale(1.5); } }

.main-title { font-family: 'Playfair Display', serif; font-size: 4.8rem; font-weight: 900; line-height: 1.05; margin: 0 0 30px 0; letter-spacing: -1.5px; color: #0f172a; }
.t-prefix { font-size: 2.2rem; font-weight: 700; font-style: italic; color: #475569; display: block; margin-bottom: -8px; }
.t-core { color: var(--theme-color); background: linear-gradient(135deg, var(--theme-color) 0%, #f97316 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; text-shadow: 0px 10px 30px rgba(var(--theme-color-rgb), 0.2); }

/* BỘ THÔNG SỐ (META PILLS CHUẨN XỊN) */
.meta-pills { display: flex; gap: 16px; margin-bottom: 24px; flex-wrap: wrap; }
.meta-pill { display: flex; align-items: center; gap: 12px; padding: 10px 16px 10px 10px; background: white; border: 1px solid #e2e8f0; border-radius: 100px; box-shadow: 0 4px 15px rgba(0,0,0,0.02); }
.m-icon { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: #f8fafc; color: var(--theme-color); }
.m-info { display: flex; flex-direction: column; }
.m-val { font-weight: 800; color: #0f172a; font-size: 0.95rem; line-height: 1.1; }
.m-lbl { font-size: 0.65rem; color: #64748b; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }

.description { font-size: 1.15rem; line-height: 1.7; color: #475569; max-width: 90%; margin-bottom: 40px; font-weight: 500; }

/* NÚT VÀ TÁC GIẢ */
.action-row { display: flex; align-items: center; gap: 20px; flex-wrap: wrap; }
.btn-glow-wrap { position: relative; }
.btn-glow { position: absolute; inset: 0; background: var(--theme-color); filter: blur(15px); opacity: 0.4; border-radius: 100px; transition: 0.3s; }
.btn-glow-wrap:hover .btn-glow { opacity: 0.7; }
.btn-primary { position: relative; z-index: 2; background: var(--theme-color); color: white; border: none; padding: 18px 36px; border-radius: 100px; font-weight: 800; font-size: 1.05rem; cursor: pointer; display: flex; align-items: center; gap: 10px; transition: 0.3s; box-shadow: inset 0 2px 5px rgba(255,255,255,0.2); }
.btn-primary:hover { transform: translateY(-3px); }

.btn-video { background: white; border: 1px solid #e2e8f0; color: #0f172a; padding: 14px 28px; border-radius: 100px; font-weight: 700; font-size: 1rem; cursor: pointer; display: flex; align-items: center; gap: 12px; transition: 0.3s; }
.btn-video:hover { border-color: var(--theme-color); transform: translateY(-2px); }
.play-circle { width: 30px; height: 30px; border-radius: 50%; background: rgba(var(--theme-color-rgb), 0.1); color: var(--theme-color); display: flex; align-items: center; justify-content: center; }

/* ================= CỘT PHẢI (VISUAL & GLASS CARDS NẰM GỌN GÀNG) ================= */
.visual-column { position: relative; display: flex; justify-content: center; align-items: center; height: 560px; }
.visual-stage { position: relative; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }

/* KHUNG ẢNH CHÍNH (KHÔNG CÒN LẮC LƯ 3D CHÓNG MẶT) */
.media-frame { 
  width: 90%; aspect-ratio: 4/5; max-height: 560px; 
  border-radius: 36px; overflow: hidden; position: relative; z-index: 5; cursor: pointer; 
  background: white; box-shadow: 0 40px 80px -20px rgba(0,0,0,0.2); 
  border: 8px solid white; transition: transform 0.4s ease, box-shadow 0.4s ease;
}
.media-frame:hover { transform: translateY(-5px); box-shadow: 0 50px 100px -20px rgba(var(--theme-color-rgb), 0.3); }
/* Hiệu ứng điện ảnh Slow Zoom */
.cinematic-zoom { width: 100%; height: 100%; object-fit: cover; transform: scale(1.01); animation: slowZoom 15s linear infinite alternate; }
@keyframes slowZoom { from { transform: scale(1.01); } to { transform: scale(1.08); } }

.glass-border { position: absolute; inset: 0; border: 1px solid rgba(255,255,255,0.4); border-radius: 36px; pointer-events: none; z-index: 2; }
.inner-shadow { position: absolute; inset: 0; box-shadow: inset 0 0 50px rgba(0,0,0,0.15); border-radius: 36px; pointer-events: none; z-index: 3; }

/* 🌟 THẺ KÍNH (GLASSMORPHISM) LƠ LỬNG BẰNG CSS, KHÔNG DÙNG CHUỘT */
.glass-card {
  position: absolute; z-index: 10;
  background: rgba(255, 255, 255, 0.85); 
  backdrop-filter: blur(24px) saturate(180%); -webkit-backdrop-filter: blur(24px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 1); border-radius: 20px;
  box-shadow: 0 20px 40px -10px rgba(0,0,0,0.1), inset 0 2px 10px rgba(255,255,255,0.8);
  pointer-events: none; overflow: hidden;
}
/* Vệt sáng lướt qua thẻ (Glossy Sweep) */
.glass-card::before {
  content: ''; position: absolute; top: 0; left: -150%; width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.9), transparent);
  transform: skewX(-20deg); animation: sweep 6s infinite;
}
@keyframes sweep { 0%, 50% { left: -150%; } 100% { left: 200%; } }

/* ANIMATION BỒNG BỀNH CHẬM RÃI */
.float-anim-1 { animation: levitate 6s ease-in-out infinite; }
.float-anim-2 { animation: levitate 8s ease-in-out infinite reverse; }
.float-anim-3 { animation: levitate 7s ease-in-out infinite 1s; }
@keyframes levitate { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-12px); } }

/* CSS Nội dung trong thẻ */
.stat-card { top: 5%; left: -8%; padding: 14px 20px; }
.gc-inner { display: flex; flex-direction: column; position: relative; z-index: 2; }
.stat-top { display: flex; align-items: center; gap: 6px; }
.s-val { font-size: 1.5rem; font-weight: 900; color: #0f172a; line-height: 1; }
.text-new { color: #f59e0b; font-size: 1.2rem; }
.s-lbl { font-size: 0.75rem; color: #64748b; font-weight: 700; text-transform: uppercase; margin-top: 4px; }

.author-card { bottom: 8%; right: -12%; padding: 12px 20px; border-radius: 100px; }
.row-layout { flex-direction: row; gap: 12px; align-items: center; }
.a-avt { width: 44px; height: 44px; border-radius: 50%; object-fit: cover; border: 2px solid white; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
.a-info-col { display: flex; flex-direction: column; }
.a-name { font-weight: 800; font-size: 0.95rem; color: #0f172a; }
.a-likes { display: flex; align-items: center; gap: 4px; font-size: 0.75rem; font-weight: 700; color: #64748b; margin-top: 2px; }

.comment-card { bottom: 15%; left: -15%; width: 280px; padding: 18px; border-radius: 24px; }
.c-head { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; position: relative; z-index: 2; }
.c-head img { width: 36px; height: 36px; border-radius: 50%; border: 2px solid white; }
.c-user { display: flex; flex-direction: column; }
.c-name { font-weight: 800; font-size: 0.9rem; color: #0f172a; }
.c-tag { font-size: 0.65rem; color: var(--theme-color); font-weight: 700; background: rgba(var(--theme-color-rgb), 0.1); padding: 2px 8px; border-radius: 100px; margin-top: 2px; width: fit-content; }
.c-text { font-size: 0.9rem; color: #475569; font-style: italic; line-height: 1.5; margin: 0; position: relative; z-index: 2; }

/* ================= NAV DOCK (THANH TRƯỢT DƯỚI ĐẸP MẮT) ================= */
.dock-nav { position: absolute; bottom: 30px; left: 50%; transform: translateX(-50%); display: flex; align-items: center; gap: 30px; z-index: 20; background: rgba(255,255,255,0.9); backdrop-filter: blur(12px); padding: 10px 24px; border-radius: 100px; box-shadow: 0 10px 30px rgba(0,0,0,0.06); border: 1px solid rgba(255,255,255,0.8); }
.d-btn { background: transparent; border: none; color: #64748b; cursor: pointer; display: flex; transition: 0.2s; padding: 4px; }
.d-btn:hover { color: #0f172a; transform: scale(1.2); }
.d-dots { display: flex; gap: 16px; align-items: center; }
.d-wrap { position: relative; width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; cursor: pointer; }
.d-core { width: 8px; height: 8px; border-radius: 50%; background: #cbd5e1; transition: 0.3s; }
.d-core.active { background: transparent; }
.d-ring { position: absolute; width: 100%; height: 100%; transform: rotate(-90deg); }
.ring-bg { fill: none; stroke: #f1f5f9; stroke-width: 3; }
.ring-progress { fill: none; stroke: var(--theme-color); stroke-width: 3; stroke-linecap: round; stroke-dasharray: 100; stroke-dashoffset: 100; animation: drawRing 8s linear forwards; }
.d-ring.is-paused .ring-progress { animation-play-state: paused !important; }
@keyframes drawRing { 100% { stroke-dashoffset: 0; } }

/* ANIMATION HIỂN THỊ CHỮ MƯỢT MÀ */
.hero-fade-enter-active, .hero-fade-leave-active { transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1); }
.hero-fade-enter-from { opacity: 0; transform: translateY(15px) scale(0.98); }
.hero-fade-leave-to { opacity: 0; transform: translateY(-15px) scale(1.02); }

.anim-slide-up { opacity: 0; animation: slideUpAnim 0.6s cubic-bezier(0.2, 0.8, 0.2, 1) forwards; }
.delay-1 { animation-delay: 0s; } .delay-2 { animation-delay: 0.1s; } .delay-3 { animation-delay: 0.2s; } .delay-4 { animation-delay: 0.3s; } .delay-5 { animation-delay: 0.4s; }
@keyframes slideUpAnim { from { opacity: 0; transform: translateY(25px); } to { opacity: 1; transform: translateY(0); } }

/* RESPONSIVE */
@media (max-width: 1024px) { 
  .hero-content { grid-template-columns: 1fr; text-align: center; gap: 50px; padding: 40px 24px 120px; } 
  .text-column { align-items: center; padding-right: 0; } 
  .meta-pills { justify-content: center; }
  .action-row { flex-direction: column; width: 100%; }
  .btn-glow-wrap, .btn-video { width: 100%; }
  .btn-primary { width: 100%; justify-content: center; }
  .visual-column { height: auto; } 
  .media-frame { max-width: 100%; aspect-ratio: 1/1; transform: none !important; } 
  .glass-card { display: none; } 
}
</style>