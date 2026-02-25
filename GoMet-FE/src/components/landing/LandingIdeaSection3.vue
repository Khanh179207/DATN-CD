<template>
  <section class="section-plan">
    <div class="container plan-layout">
      
      <div class="plan-visual fade-in-right">
        <div class="image-wrapper">
          <div class="circle-bg"></div>
          
          <img src="https://images.unsplash.com/photo-1498837167922-ddd27525d352?q=80&w=800&auto=format&fit=crop" alt="Meal Prep" class="main-img">
          
          <div class="float-card calendar-card">
            <div class="icon-box">📅</div>
            <div class="text-box">
              <span class="label">Kế hoạch tuần</span>
              <span class="value">Đã hoàn tất</span>
            </div>
            <div class="check-mark">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="4"><polyline points="20 6 9 17 4 12"></polyline></svg>
            </div>
          </div>

          <div class="float-card calories-card">
            <span class="icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M2 22a9 9 0 1 1 18 0"/><path d="M12 13a5 5 0 0 0 5-5A5 5 0 0 0 9.8 4.2"/><path d="M12 13a5 5 0 0 1-3.5-1.5"/></svg>
            </span>
            <div class="text-box">
              <span class="label">Dinh dưỡng</span>
              <span class="value">Heathly & Balance</span>
            </div>
          </div>
        </div>
      </div>

      <div class="plan-content fade-in-left">
        <span class="sub-tag">Smart Meal Planner</span>
        <h2 class="plan-title">
          Gạt Bỏ Nỗi Lo <br>
          <span class="highlight-text">"Hôm Nay Ăn Gì?"</span>
        </h2>
        <p class="plan-desc">
          Công cụ <strong>Lên Thực Đơn</strong> thông minh của Gomet giúp bạn sắp xếp bữa ăn cho cả tuần chỉ trong vài phút. Tiết kiệm thời gian, chi tiêu hợp lý và đảm bảo dinh dưỡng cho cả gia đình.
        </p>

        <div class="features-list">
          <div class="feature-item">
            <div class="f-icon">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
            </div>
            <div class="f-text">
              <h4>Gợi ý thông minh</h4>
              <p>Đề xuất món ăn dựa trên sở thích của bạn.</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="f-icon">🛒</div>
            <div class="f-text">
              <h4>Danh sách đi chợ</h4>
              <p>Tự động tổng hợp nguyên liệu cần mua.</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="f-icon">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/></svg>
            </div>
            <div class="f-text">
              <h4>Kiểm soát Calo</h4>
              <p>Tính toán dinh dưỡng cho từng bữa ăn.</p>
            </div>
          </div>
        </div>

        <button class="btn-try">
          Tạo thực đơn ngay
          <span class="arrow">→</span>
        </button>
      </div>

    </div>
  </section>
</template>

<style scoped>
.section-plan { 
  background-color: white; /* Nền trắng sạch sẽ */
  padding: 80px 0; 
  min-height: 100vh;
  display: flex; flex-direction: column; justify-content: center;
  font-family: 'Quicksand', sans-serif;
  overflow: hidden;
}

.container { width: 100%; max-width: 1200px; margin: 0 auto; padding: 0 30px; }

.plan-layout { 
  display: flex; align-items: center; gap: 80px; 
}

/* --- VISUAL SIDE (LEFT) --- */
.plan-visual { flex: 1; display: flex; justify-content: center; }

.image-wrapper { position: relative; width: 450px; height: 550px; }

.circle-bg {
  position: absolute; top: -40px; left: -40px; width: 300px; height: 300px;
  background: #FEF3C7; border-radius: 50%; z-index: 0; opacity: 0.7;
}

.main-img {
  width: 100%; height: 100%; object-fit: cover; border-radius: 40px;
  box-shadow: 20px 20px 0px #F97316; /* Bóng cứng màu cam style Magazine */
  position: relative; z-index: 1; transition: 0.5s;
}
.image-wrapper:hover .main-img { transform: translateY(-10px); box-shadow: 20px 30px 0px #F97316; }

/* Floating Cards */
.float-card {
  position: absolute; background: white; padding: 15px 20px; border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.15); z-index: 2;
  display: flex; align-items: center; gap: 15px;
  animation: float 4s ease-in-out infinite; border: 1px solid #F3F4F6;
}

.calendar-card { top: 60px; right: -40px; animation-delay: 0s; }
.calories-card { bottom: 60px; left: -40px; animation-delay: 2s; }

.icon-box, .float-card .icon { 
  width: 40px; height: 40px; background: #FFF7ED; border-radius: 12px; 
  display: flex; align-items: center; justify-content: center; font-size: 1.2rem; 
}
.text-box { display: flex; flex-direction: column; }
.text-box .label { font-size: 0.75rem; color: #9CA3AF; font-weight: 600; text-transform: uppercase; }
.text-box .value { font-size: 0.95rem; color: #1F2937; font-weight: 700; }

.check-mark {
  width: 24px; height: 24px; background: #10B981; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; margin-left: 10px;
}

/* --- CONTENT SIDE (RIGHT) --- */
.plan-content { flex: 1; }

.sub-tag {
  font-size: 0.85rem; font-weight: 700; color: #F97316; text-transform: uppercase; letter-spacing: 2px;
  margin-bottom: 15px; display: block;
}

.plan-title {
  font-family: 'Playfair Display', serif; font-size: 3.5rem; color: #1C1917; 
  line-height: 1.15; margin-bottom: 25px;
}
.highlight-text { 
  color: #F97316; position: relative; white-space: nowrap;
}
.highlight-text::after {
  content: ''; position: absolute; bottom: 5px; left: 0; width: 100%; height: 10px;
  background: rgba(249, 115, 22, 0.2); z-index: -1; transform: rotate(-1deg);
}

.plan-desc {
  font-size: 1.1rem; color: #57534E; line-height: 1.6; margin-bottom: 40px;
}

/* Feature List */
.features-list { display: flex; flex-direction: column; gap: 25px; margin-bottom: 40px; }

.feature-item { display: flex; align-items: flex-start; gap: 20px; }
.f-icon {
  width: 50px; height: 50px; background: #FFF7ED; border-radius: 16px;
  display: flex; align-items: center; justify-content: center; font-size: 1.5rem;
  color: #F97316; flex-shrink: 0; transition: 0.3s;
}
.feature-item:hover .f-icon { background: #F97316; color: white; transform: rotate(10deg); }

.f-text h4 { font-size: 1.1rem; font-weight: 700; color: #1C1917; margin: 0 0 5px; }
.f-text p { font-size: 0.95rem; color: #6B7280; margin: 0; line-height: 1.4; }

/* Button */
.btn-try {
  background: #1C1917; color: white; padding: 16px 40px; border-radius: 50px;
  font-weight: 700; font-size: 1rem; border: none; cursor: pointer;
  display: inline-flex; align-items: center; gap: 10px; transition: 0.3s;
}
.btn-try:hover { background: #F97316; padding-right: 30px; }
.arrow { transition: 0.3s; }
.btn-try:hover .arrow { transform: translateX(5px); }

/* Animation */
@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-10px); } }
.fade-in-left { animation: fadeInLeft 1s ease-out; }
.fade-in-right { animation: fadeInRight 1s ease-out; }
@keyframes fadeInLeft { from { opacity: 0; transform: translateX(30px); } to { opacity: 1; transform: translateX(0); } }
@keyframes fadeInRight { from { opacity: 0; transform: translateX(-30px); } to { opacity: 1; transform: translateX(0); } }

/* Responsive */
@media (max-width: 1024px) {
  .plan-layout { flex-direction: column-reverse; text-align: center; gap: 60px; }
  .features-list { align-items: center; text-align: left; }
  .image-wrapper { width: 100%; max-width: 400px; height: 400px; margin: 0 auto; }
  .main-img { box-shadow: 15px 15px 0px #F97316; }
  .calendar-card { right: -10px; }
  .calories-card { left: -10px; }
}
</style>