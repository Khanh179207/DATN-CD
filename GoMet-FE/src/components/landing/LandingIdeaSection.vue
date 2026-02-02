<template>
  <section class="idea-section">
    <div class="container idea-layout">
      <div class="idea-text">
        <h2 class="idea-title">Cảm hứng <br>nấu ăn mỗi ngày</h2>
        <p class="idea-desc">
          Lưu lại công thức tâm đắc và <br>xây dựng bộ sưu tập món ngon của riêng bạn.
        </p>
        <button class="btn-khampha">Khám phá ngay</button>
      </div>

      <div class="idea-grid">
        <div class="idea-card card-main">
          <img src="https://images.unsplash.com/photo-1547592180-85f173990554?q=80&w=800&auto=format&fit=crop" alt="Cơm nhà">
          <div class="card-content"><span class="card-label">Cơm nhà ấm cúng</span></div>
        </div>
        <div class="idea-card card-tall">
          <img src="https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=800&auto=format&fit=crop" alt="Healthy">
          <div class="card-content"><span class="card-label">Eat Clean</span></div>
        </div>
        <div class="idea-card">
          <img src="https://images.unsplash.com/photo-1578985545062-69928b1d9587?q=80&w=800&auto=format&fit=crop" alt="Bánh">
          <div class="card-content"><span class="card-label">Làm bánh</span></div>
        </div>
        <div class="idea-card">
          <img src="https://images.unsplash.com/photo-1544145945-f90425340c7e?q=80&w=800&auto=format&fit=crop" alt="Đồ uống">
          <div class="card-content"><span class="card-label">Pha chế</span></div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* 👇 ĐÃ SỬA: Full màn hình + Căn giữa */
.idea-section { 
  background-color: #FDFD96; 
  padding: 40px 0; 
  min-height: 100vh; /* Cao ít nhất 1 màn hình */
  display: flex;
  flex-direction: column;
  justify-content: center; /* Căn giữa dọc */
}

/* Các phần bên dưới giữ nguyên */
.idea-layout { display: flex; align-items: center; gap: 80px; max-width: 1440px; margin: 0 auto; padding: 0 40px; }

.idea-text { flex: 0.8; min-width: 350px; }
.idea-title { font-size: 4rem; font-weight: 800; color: #004d40; line-height: 1.1; margin-bottom: 24px; letter-spacing: -1px; }
.idea-desc { font-size: 1.25rem; color: #004d40; margin-bottom: 40px; line-height: 1.6; opacity: 0.9; }

.btn-khampha { background-color: #D32F2F; color: white; border: none; padding: 16px 40px; border-radius: 60px; font-weight: 700; font-size: 1.1rem; cursor: pointer; transition: 0.3s; }
.btn-khampha:hover { background-color: #b71c1c; transform: translateY(-4px); box-shadow: 0 10px 20px rgba(211, 47, 47, 0.3); }

.idea-grid { flex: 1.2; display: grid; grid-template-columns: repeat(3, 1fr); grid-template-rows: repeat(2, 220px); gap: 24px; }

.idea-card { position: relative; border-radius: 32px; overflow: hidden; cursor: pointer; transition: 0.4s; }
.idea-card:hover { transform: scale(1.03); }
.idea-card img { width: 100%; height: 100%; object-fit: cover; }
.card-content { position: absolute; bottom: 0; left: 0; width: 100%; padding: 24px; background: linear-gradient(transparent, rgba(0,0,0,0.8)); color: white; font-weight: 700; font-size: 1.2rem; }

.card-main { grid-column: 1 / 3; grid-row: 1 / 3; }
.card-tall { grid-column: 3 / 4; grid-row: 1 / 2; }

@media (max-width: 1024px) {
  .idea-layout { flex-direction: column; text-align: center; }
  .idea-grid { width: 100%; grid-template-columns: 1fr 1fr; grid-template-rows: auto; }
  .card-main { grid-column: 1 / -1; height: 350px; }
}
</style>