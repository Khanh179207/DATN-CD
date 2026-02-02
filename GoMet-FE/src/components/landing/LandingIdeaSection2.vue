<template>
  <section class="section-community">
    <div class="container community-layout">
      <div class="community-header">
        <h2>Kết nối đam mê</h2>
        <p>Tham gia cộng đồng Gomet để chia sẻ thành quả và học hỏi từ hàng ngàn đầu bếp tại gia.</p>
      </div>

      <div class="cards-wrapper">
        <div class="user-card">
          <div class="card-img">
            <img src="https://images.unsplash.com/photo-1556911220-e15b29be8c8f?q=80&w=800&auto=format&fit=crop" alt="Bếp">
          </div>
          <div class="card-info">
            <h3>Góc Bếp Nhỏ</h3>
            <p>Chia sẻ mẹo vặt nấu ăn hàng ngày.</p>
          </div>
        </div>
        <div class="user-card center-card">
          <div class="card-img">
            <img src="https://images.unsplash.com/photo-1507048331197-7d4ac70811cf?q=80&w=800&auto=format&fit=crop" alt="Đầu bếp">
          </div>
          <div class="card-info">
            <h3>MasterChef Tại Gia</h3>
            <p>Thử thách nấu món ngon cuối tuần.</p>
          </div>
        </div>
        <div class="user-card">
          <div class="card-img">
            <img src="https://images.unsplash.com/photo-1466637574441-749b8f19452f?q=80&w=800&auto=format&fit=crop" alt="Rau củ">
          </div>
          <div class="card-info">
            <h3>Sống Xanh</h3>
            <p>Công thức Eat Clean & Healthy.</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* 👇 ĐÃ SỬA: Full màn hình + Căn giữa */
.section-community { 
  background-color: #E0F2F1; 
  padding: 40px 0; 
  min-height: 100vh; /* Cao ít nhất 1 màn hình */
  display: flex;
  flex-direction: column;
  justify-content: center; /* Căn giữa dọc */
}

/* Các phần bên dưới giữ nguyên */
.community-layout { text-align: center; max-width: 1440px; margin: 0 auto; padding: 0 40px; }

.community-header h2 { font-size: 3.5rem; color: #00695C; margin-bottom: 16px; font-weight: 800; }
.community-header p { font-size: 1.2rem; color: #00695C; opacity: 0.8; max-width: 700px; margin: 0 auto 60px; }

.cards-wrapper { display: flex; justify-content: center; gap: 40px; flex-wrap: wrap; }

.user-card { background: white; border-radius: 24px; overflow: hidden; width: 380px; box-shadow: 0 15px 40px rgba(0,0,0,0.06); transition: 0.3s; }
.user-card:hover { transform: translateY(-15px); box-shadow: 0 25px 60px rgba(0,0,0,0.12); }

.card-img { height: 240px; overflow: hidden; }
.card-img img { width: 100%; height: 100%; object-fit: cover; transition: 0.5s; }
.user-card:hover .card-img img { transform: scale(1.1); }

.card-info { padding: 30px; text-align: left; }
.card-info h3 { margin: 0 0 12px; color: #333; font-size: 1.5rem; font-weight: 700; }
.card-info p { margin: 0; color: #666; font-size: 1.1rem; line-height: 1.5; }

@media (max-width: 768px) { .user-card { width: 100%; } }
</style>