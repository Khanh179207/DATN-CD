<template>
  <div class="profile-page-wrapper">
    
    <div class="hero-backdrop" :style="{ backgroundImage: `url(${user.cover})` }"></div>
    <div class="hero-overlay"></div>

    <div class="profile-container">
      
      <div class="profile-card-glass anime-fade-up">
        
        <div class="card-cover" :style="{ backgroundImage: `url(${user.cover})` }">
          <button class="btn-edit-cover">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
            <span>Đổi ảnh bìa</span>
          </button>
        </div>

        <div class="card-body">
          <div class="profile-layout">
            
            <div class="profile-left">
              <div class="avatar-wrapper">
                <div class="avatar-frame">
                  <img :src="user.avatar" class="user-avatar" alt="Avatar">
                </div>
                <div class="status-indicator" title="Online"></div>
              </div>
              
              <div class="identity-group mobile-only">
                <h1 class="user-name">{{ user.name }} <span class="verified-icon">✔</span></h1>
                <p class="user-handle">@{{ user.handle }}</p>
              </div>
            </div>

            <div class="profile-right">
              <div class="header-actions">
                <div class="identity-group desktop-only">
                  <h1 class="user-name">{{ user.name }} <span class="verified-icon">✔</span></h1>
                  <p class="user-handle">@{{ user.handle }}</p>
                </div>
                
                <div class="btn-group">
                  <button class="btn-action secondary">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
                  </button>
                  <button class="btn-action primary">Chỉnh sửa hồ sơ</button>
                </div>
              </div>

              <div class="stats-bar">
                <div class="stat">
                  <span class="num">{{ user.postsCount }}</span>
                  <span class="lbl">Bài viết</span>
                </div>
                <div class="stat">
                  <span class="num">{{ user.followers }}</span>
                  <span class="lbl">Người theo dõi</span>
                </div>
                <div class="stat">
                  <span class="num">{{ user.following }}</span>
                  <span class="lbl">Đang theo dõi</span>
                </div>
              </div>

              <div class="bio-section">
                <p>{{ user.bio }}</p>
                <a href="#" class="bio-link">🔗 gomet.vn/chef/khanh</a>
              </div>
            </div>
          </div>
        </div>

        <div class="profile-tabs">
          <button 
            v-for="tab in tabs" 
            :key="tab.id"
            class="tab-item"
            :class="{ active: currentTab === tab.id }"
            @click="currentTab = tab.id"
          >
            {{ tab.label }}
            <div class="active-line" v-if="currentTab === tab.id"></div>
          </button>
        </div>
      </div>

      <div class="content-section anime-fade-up-delay">
        <transition name="fade-slide" mode="out-in">
          
          <div v-if="currentTab === 'posts'" class="recipe-grid">
            <RecipeCard 
              v-for="post in posts" 
              :key="post.id" 
              :post="post" 
              class="profile-recipe-item"
            />
          </div>

          <div v-else-if="currentTab === 'saved'" class="empty-state">
            <div class="icon-empty">🔖</div>
            <h3>Bộ sưu tập còn trống</h3>
            <p>Hãy khám phá và lưu lại những công thức tuyệt vời.</p>
          </div>

          <div v-else class="about-grid">
            <div class="info-box">
              <h3>Thông tin cá nhân</h3>
              <ul>
                <li>📍 Sống tại: <b>Hà Nội</b></li>
                <li>🎂 Sinh nhật: <b>20/10/1995</b></li>
                <li>🍳 Sở trường: <b>Món Âu</b></li>
              </ul>
            </div>
            <div class="info-box">
              <h3>Thành tựu</h3>
              <div class="achievements">
                <span class="badge-achieve">🏆 Top Chef 2023</span>
                <span class="badge-achieve">🌟 100k Likes</span>
              </div>
            </div>
          </div>

        </transition>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import RecipeCard from '@/components/common/RecipeCard.vue' // Đảm bảo import đúng đường dẫn

const user = ref({
  name: 'Nguyễn Khánh',
  handle: 'khanh_chef',
  avatar: 'https://i.pravatar.cc/400?u=khanh',
  cover: 'https://images.unsplash.com/photo-1556910103-1c02745a30bf?q=80&w=2070&auto=format&fit=crop',
  bio: '👨‍🍳 Bếp trưởng Gomet. Chuyên chia sẻ công thức healthy & eat clean. "Nấu ăn là nghệ thuật, người nấu ăn là nghệ sĩ".',
  postsCount: 128,
  followers: '45.2k',
  following: 320
})

const tabs = [
  { id: 'posts', label: 'BÀI VIẾT' },
  { id: 'saved', label: 'ĐÃ LƯU' },
  { id: 'about', label: 'GIỚI THIỆU' },
]
const currentTab = ref('posts')

// Mock Data Bài viết
const posts = ref([
  { id: 1, title: 'Bò Wagyu áp chảo sốt tiêu đen', image: 'https://images.unsplash.com/photo-1544025162-d76694265947?w=800&auto=format&fit=crop', author: { name: 'Nguyễn Khánh', avatar: user.value.avatar }, likes: 1250, time: '45p', difficulty: 'Khó' },
  { id: 2, title: 'Salad ức gà sốt chanh leo', image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800&auto=format&fit=crop', author: { name: 'Nguyễn Khánh', avatar: user.value.avatar }, likes: 890, time: '20p', difficulty: 'Dễ' },
  { id: 3, title: 'Mỳ Ý hải sản sốt kem', image: 'https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=800&auto=format&fit=crop', author: { name: 'Nguyễn Khánh', avatar: user.value.avatar }, likes: 2100, time: '35p', difficulty: 'Trung bình' },
  { id: 4, title: 'Bánh mì nướng bơ tỏi', image: 'https://images.unsplash.com/photo-1550547660-d9450f859349?w=800&auto=format&fit=crop', author: { name: 'Nguyễn Khánh', avatar: user.value.avatar }, likes: 500, time: '15p', difficulty: 'Dễ' },
])
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Playfair+Display:wght@700&display=swap');

.profile-page-wrapper {
  font-family: 'Inter', sans-serif; color: #1C1917;
  min-height: 100vh; position: relative;
  background-color: #FAFAF9;
}

/* Background mờ phía sau tạo mood */
.hero-backdrop {
  position: absolute; top: 0; left: 0; right: 0; height: 400px;
  background-size: cover; background-position: center;
  filter: blur(60px); opacity: 0.4; z-index: 0;
  mask-image: linear-gradient(to bottom, black, transparent);
}

.profile-container {
  max-width: 1000px; margin: 0 auto; width: 100%;
  padding: 40px 20px 80px; position: relative; z-index: 1;
}

/* === 1. PROFILE CARD GLASS (Điểm nhấn chính) === */
.profile-card-glass {
  background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.6);
  border-radius: 24px; overflow: hidden;
  box-shadow: 0 20px 50px -10px rgba(0,0,0,0.1);
  margin-bottom: 40px;
}

.card-cover {
  height: 250px; background-size: cover; background-position: center; position: relative;
}
.btn-edit-cover {
  position: absolute; bottom: 15px; right: 15px;
  background: rgba(0,0,0,0.6); color: white; border: none;
  padding: 8px 16px; border-radius: 20px; cursor: pointer;
  display: flex; align-items: center; gap: 8px; font-size: 0.85rem; font-weight: 600;
  transition: 0.2s; backdrop-filter: blur(4px);
}
.btn-edit-cover:hover { background: rgba(0,0,0,0.8); }

.card-body { padding: 0 40px 30px; position: relative; }

.profile-layout { display: flex; gap: 30px; }

/* Cột Trái: Avatar */
.profile-left { flex-shrink: 0; width: 160px; position: relative; margin-top: -80px; }
.avatar-wrapper { position: relative; width: 160px; height: 160px; }
.avatar-frame {
  width: 100%; height: 100%; border-radius: 50%;
  border: 6px solid white; overflow: hidden;
  box-shadow: 0 10px 25px rgba(0,0,0,0.15);
  background: white;
}
.user-avatar { width: 100%; height: 100%; object-fit: cover; }
.status-indicator {
  position: absolute; bottom: 12px; right: 12px; width: 22px; height: 22px;
  background: #22C55E; border: 4px solid white; border-radius: 50%;
}

/* Cột Phải: Info */
.profile-right { flex: 1; padding-top: 15px; }

.header-actions { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 15px; }
.user-name { font-family: 'Playfair Display', serif; font-size: 2rem; margin: 0; color: #1C1917; display: flex; align-items: center; gap: 8px; }
.verified-icon { font-size: 0.8rem; background: #0EA5E9; color: white; width: 20px; height: 20px; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.user-handle { color: #6B7280; font-weight: 500; font-size: 1rem; }

.btn-group { display: flex; gap: 10px; }
.btn-action { height: 40px; padding: 0 20px; border-radius: 12px; font-weight: 600; cursor: pointer; transition: 0.2s; border: none; display: flex; align-items: center; justify-content: center; }
.btn-action.primary { background: #1C1917; color: white; box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
.btn-action.primary:hover { background: #EA580C; transform: translateY(-2px); }
.btn-action.secondary { background: #F3F4F6; color: #1C1917; width: 40px; padding: 0; }
.btn-action.secondary:hover { background: #E5E7EB; }

.stats-bar { display: flex; gap: 40px; margin-bottom: 20px; }
.stat { display: flex; flex-direction: column; }
.stat .num { font-size: 1.2rem; font-weight: 800; color: #1C1917; }
.stat .lbl { font-size: 0.85rem; color: #6B7280; }

.bio-section p { color: #44403C; line-height: 1.5; margin-bottom: 8px; }
.bio-link { color: #EA580C; font-weight: 600; text-decoration: none; font-size: 0.9rem; }
.bio-link:hover { text-decoration: underline; }

/* Tabs */
.profile-tabs {
  display: flex; border-top: 1px solid #F3F4F6; padding: 0 40px;
}
.tab-item {
  background: none; border: none; padding: 18px 25px;
  font-size: 0.9rem; font-weight: 700; color: #9CA3AF;
  cursor: pointer; position: relative; transition: 0.2s;
  letter-spacing: 0.5px;
}
.tab-item:hover { color: #4B5563; }
.tab-item.active { color: #1C1917; }
.active-line {
  position: absolute; bottom: 0; left: 0; width: 100%; height: 3px;
  background: #1C1917; border-radius: 3px 3px 0 0;
}

/* === 2. CONTENT AREA === */
.content-section { min-height: 400px; }

/* Recipe Grid */
.recipe-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

/* Empty State */
.empty-state { text-align: center; padding: 60px 0; color: #6B7280; }
.icon-empty { font-size: 3rem; margin-bottom: 15px; opacity: 0.5; }
.btn-outline-pro { margin-top: 20px; padding: 10px 24px; border: 2px solid #1C1917; background: transparent; border-radius: 30px; font-weight: 700; cursor: pointer; transition: 0.2s; }
.btn-outline-pro:hover { background: #1C1917; color: white; }

/* About Grid */
.about-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; }
.info-box { background: white; padding: 30px; border-radius: 20px; box-shadow: 0 4px 20px rgba(0,0,0,0.03); }
.info-box h3 { margin-top: 0; font-family: 'Playfair Display', serif; border-bottom: 1px solid #F3F4F6; padding-bottom: 10px; margin-bottom: 15px; }
.info-box ul { list-style: none; padding: 0; }
.info-box li { padding: 8px 0; border-bottom: 1px dashed #F3F4F6; color: #4B5563; }
.achievements { display: flex; flex-wrap: wrap; gap: 10px; }
.badge-achieve { background: #FEF3C7; color: #D97706; padding: 6px 12px; border-radius: 8px; font-weight: 700; font-size: 0.85rem; }

/* Animations */
.anime-fade-up { animation: fadeInUp 0.6s ease forwards; }
.anime-fade-up-delay { opacity: 0; animation: fadeInUp 0.6s ease 0.3s forwards; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

/* Mobile Responsive */
.mobile-only { display: none; }
@media (max-width: 768px) {
  .desktop-only { display: none; }
  .mobile-only { display: block; text-align: center; margin-top: 10px; }
  .card-body { padding: 0 20px 20px; }
  .profile-layout { flex-direction: column; align-items: center; gap: 10px; }
  .profile-left { margin-top: -60px; width: auto; display: flex; flex-direction: column; align-items: center; }
  .avatar-wrapper { width: 120px; height: 120px; }
  .profile-right { width: 100%; text-align: center; padding-top: 0; }
  .header-actions { justify-content: center; margin-bottom: 20px; }
  .stats-bar { justify-content: center; gap: 20px; margin-bottom: 20px; }
  .about-grid { grid-template-columns: 1fr; }
  .profile-tabs { padding: 0 10px; justify-content: center; }
}
</style>