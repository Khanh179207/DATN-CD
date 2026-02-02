<template>
  <section class="section-plan">
    <div class="container plan-layout">
      <div class="plan-image">
        <img src="https://images.unsplash.com/photo-1498837167922-ddd27525d352?q=80&w=1200&auto=format&fit=crop" alt="Lên thực đơn">
        <div class="floating-box">
          <span>📅</span> Đã lên lịch tuần này!
        </div>
      </div>

      <div class="plan-text">
        <h2 class="plan-title">Gạt bỏ nỗi lo <br>"Hôm nay ăn gì?"</h2>
        <p class="plan-desc">
          Công cụ <strong>Lên Thực Đơn</strong> thông minh của Gomet giúp bạn sắp xếp bữa ăn cho cả tuần chỉ trong vài phút. Tiết kiệm thời gian, chi tiêu hợp lý.
        </p>
        <ul class="plan-features">
          <li>✅ Gợi ý thực đơn theo sở thích</li>
          <li>✅ Tự động tạo danh sách đi chợ</li>
          <li>✅ Tính toán calo dinh dưỡng</li>
        </ul>
        <button class="btn-try">Thử ngay miễn phí</button>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* 👇 SỬA Ở ĐÂY: Thêm min-height và Flex để căn giữa dọc */
.section-plan { 
  background-color: #FFCCBC; 
  padding: 40px 0; /* Giảm padding một chút vì đã có flex căn giữa */
  
  /* Quan trọng: Bắt buộc cao ít nhất bằng 1 màn hình */
  min-height: 100vh;
  
  /* Căn giữa nội dung theo chiều dọc */
  display: flex;
  flex-direction: column;
  justify-content: center; 
}

/* Layout rộng 1440px */
.plan-layout { display: flex; align-items: center; gap: 100px; max-width: 1440px; margin: 0 auto; padding: 0 40px; }

.plan-image { flex: 1; position: relative; }
.plan-image img { width: 100%; border-radius: 60px 0 60px 0; box-shadow: 24px 24px 0 rgba(255,255,255,0.4); }

.floating-box { position: absolute; bottom: 40px; right: -30px; background: white; padding: 20px 30px; border-radius: 20px; box-shadow: 0 15px 40px rgba(0,0,0,0.15); font-weight: 700; font-size: 1.1rem; color: #333; display: flex; align-items: center; gap: 12px; animation: float 3s ease-in-out infinite; }
@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-12px); } }

.plan-text { flex: 1; }
.plan-title { font-size: 4rem; color: #BF360C; line-height: 1.1; margin-bottom: 30px; font-weight: 800; letter-spacing: -1px; }
.plan-desc { font-size: 1.25rem; color: #3E2723; margin-bottom: 40px; line-height: 1.6; }
.plan-features { list-style: none; padding: 0; margin-bottom: 40px; }
.plan-features li { margin-bottom: 16px; font-size: 1.2rem; color: #3E2723; font-weight: 600; display: flex; align-items: center; gap: 10px; }

.btn-try { background-color: #BF360C; color: white; border: none; padding: 18px 40px; border-radius: 12px; font-weight: 700; font-size: 1.1rem; cursor: pointer; transition: 0.3s; }
.btn-try:hover { background-color: #870000; transform: scale(1.05); }

@media (max-width: 1024px) { 
  .plan-layout { flex-direction: column-reverse; gap: 60px; text-align: center; } 
  .plan-image img { width: 100%; max-width: 600px; margin: 0 auto; display: block; } 
  .plan-features li { justify-content: center; }
  .floating-box { right: 10%; } 
}
</style>