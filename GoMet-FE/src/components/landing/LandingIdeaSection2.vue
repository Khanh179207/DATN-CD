<template>
  <section class="section-community">
    <div class="bg-pattern"></div>

    <div class="container community-layout">
      
      <div class="community-header fade-in-up">
        <span class="sub-tag">Our Community</span>
        <h2>Kết Nối Đam Mê <br> <span class="highlight-text">Ẩm Thực Việt</span></h2>
        <p>Tham gia Gomet để chia sẻ thành quả, học hỏi bí quyết và trở thành nguồn cảm hứng cho hàng ngàn đầu bếp tại gia.</p>
        <button class="btn-join">
          Tham gia ngay <span class="arrow">→</span>
        </button>
      </div>

      <div class="cards-wrapper fade-in-up delay">
        
        <div class="user-card">
          <div class="card-cover">
            <img src="https://images.unsplash.com/photo-1556911220-e15b29be8c8f?q=80&w=600" alt="Kitchen">
            <div class="card-overlay"></div>
          </div>
          <div class="card-body">
            <img src="https://i.pravatar.cc/150?u=10" class="user-avt">
            <h3>Góc Bếp Nhỏ</h3>
            <span class="handle">@gocbepnho</span>
            <div class="stats">
              <div><strong>12k</strong> Followers</div>
              <div><strong>45</strong> Recipes</div>
            </div>
            <button class="btn-follow">Theo dõi</button>
          </div>
        </div>

        <div class="user-card center-card">
          <div class="badge-top">🏆 Top Creator</div>
          <div class="card-cover">
            <img src="https://images.unsplash.com/photo-1507048331197-7d4ac70811cf?q=80&w=600" alt="Chef">
            <div class="card-overlay"></div>
          </div>
          <div class="card-body">
            <img src="https://i.pravatar.cc/150?u=20" class="user-avt">
            <h3>MasterChef Tại Gia</h3>
            <span class="handle">@masterchef_vn</span>
            <div class="stats">
              <div><strong>85k</strong> Followers</div>
              <div><strong>120</strong> Recipes</div>
            </div>
            <button class="btn-follow primary">Đang theo dõi</button>
          </div>
        </div>

        <div class="user-card">
          <div class="card-cover">
            <img src="https://images.unsplash.com/photo-1466637574441-749b8f19452f?q=80&w=600" alt="Green">
            <div class="card-overlay"></div>
          </div>
          <div class="card-body">
            <img src="https://i.pravatar.cc/150?u=30" class="user-avt">
            <h3>Sống Xanh</h3>
            <span class="handle">@eatclean_daily</span>
            <div class="stats">
              <div><strong>34k</strong> Followers</div>
              <div><strong>88</strong> Recipes</div>
            </div>
            <button class="btn-follow">Theo dõi</button>
          </div>
        </div>

      </div>
    </div>
  </section>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;800&family=Quicksand:wght@500;600;700&display=swap');

.section-community { 
  background-color: #FAFAF9; /* Nền kem sáng */
  padding: 100px 0; 
  min-height: 100vh;
  display: flex; flex-direction: column; justify-content: center;
  position: relative; overflow: hidden;
  font-family: 'Quicksand', sans-serif;
}

/* Background Pattern */
.bg-pattern {
  position: absolute; inset: 0; opacity: 0.05;
  background-image: radial-gradient(#1C1917 1px, transparent 1px);
  background-size: 20px 20px;
}

.community-layout { 
  text-align: center; max-width: 1200px; margin: 0 auto; padding: 0 20px; 
  position: relative; z-index: 2;
}

/* HEADER STYLE */
.sub-tag {
  font-size: 0.85rem; font-weight: 700; color: #A8A29E; text-transform: uppercase; letter-spacing: 2px;
  margin-bottom: 15px; display: block;
}

.community-header h2 { 
  font-family: 'Playfair Display', serif; font-size: 3.5rem; 
  color: #1C1917; margin-bottom: 20px; line-height: 1.1;
}
.highlight-text { color: #F97316; }

.community-header p { 
  font-size: 1.1rem; color: #57534E; opacity: 0.9; max-width: 600px; margin: 0 auto 40px; line-height: 1.6;
}

.btn-join {
  background: #1C1917; color: white; padding: 14px 35px; border-radius: 50px;
  font-weight: 700; border: none; cursor: pointer; transition: 0.3s;
  display: inline-flex; align-items: center; gap: 8px; font-size: 1rem;
}
.btn-join:hover { background: #F97316; transform: translateY(-3px); box-shadow: 0 10px 25px rgba(249, 115, 22, 0.3); }
.btn-join .arrow { transition: 0.3s; }
.btn-join:hover .arrow { transform: translateX(5px); }

/* CARDS WRAPPER */
.cards-wrapper { 
  display: flex; justify-content: center; align-items: center; gap: 30px; 
  margin-top: 60px; flex-wrap: wrap;
}

/* USER CARD STYLE */
.user-card { 
  background: white; border-radius: 24px; overflow: hidden; width: 320px; 
  box-shadow: 0 10px 30px rgba(0,0,0,0.05); transition: all 0.4s ease;
  position: relative; border: 1px solid #F5F5F4;
}
.user-card:hover { transform: translateY(-10px); box-shadow: 0 20px 50px rgba(0,0,0,0.1); }

/* Center Card Highlight */
.center-card { 
  transform: scale(1.1); z-index: 10; border-color: #FED7AA;
  box-shadow: 0 25px 60px rgba(249, 115, 22, 0.15);
}
.center-card:hover { transform: scale(1.12); }
.badge-top {
  position: absolute; top: 15px; right: 15px; z-index: 5;
  background: #F97316; color: white; font-weight: 700; font-size: 0.75rem;
  padding: 4px 12px; border-radius: 20px; box-shadow: 0 4px 10px rgba(249, 115, 22, 0.4);
}

/* Cover Image */
.card-cover { height: 140px; position: relative; }
.card-cover img { width: 100%; height: 100%; object-fit: cover; }
.card-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.6), transparent); }

/* Card Body */
.card-body { 
  padding: 0 25px 30px; text-align: center; position: relative; 
  display: flex; flex-direction: column; align-items: center;
}

.user-avt { 
  width: 80px; height: 80px; border-radius: 50%; border: 4px solid white; 
  margin-top: -40px; object-fit: cover; box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  position: relative; z-index: 2;
}

.card-body h3 { margin: 15px 0 5px; color: #1C1917; font-size: 1.2rem; font-weight: 700; }
.handle { color: #A8A29E; font-size: 0.9rem; margin-bottom: 20px; display: block; font-weight: 600; }

.stats { 
  display: flex; gap: 30px; margin-bottom: 25px; padding: 15px 0; 
  border-top: 1px solid #F5F5F4; border-bottom: 1px solid #F5F5F4; width: 100%; justify-content: center;
}
.stats div { display: flex; flex-direction: column; font-size: 0.8rem; color: #78716C; }
.stats strong { font-size: 1.1rem; color: #1C1917; }

.btn-follow { 
  background: white; border: 2px solid #E7E5E4; color: #1C1917; 
  padding: 8px 30px; border-radius: 20px; font-weight: 700; cursor: pointer; transition: 0.2s;
}
.btn-follow:hover { border-color: #1C1917; }
.btn-follow.primary { background: #1C1917; color: white; border-color: #1C1917; }
.btn-follow.primary:hover { background: #F97316; border-color: #F97316; }

/* Animation */
.fade-in-up { animation: fadeInUp 1s ease-out; }
.delay { animation-delay: 0.3s; opacity: 0; animation-fill-mode: forwards; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(40px); } to { opacity: 1; transform: translateY(0); } }

/* Responsive */
@media (max-width: 1024px) {
  .center-card { transform: scale(1); }
  .center-card:hover { transform: translateY(-10px); }
  .cards-wrapper { flex-direction: column; align-items: center; }
  .user-card { width: 100%; max-width: 350px; }
}
</style>