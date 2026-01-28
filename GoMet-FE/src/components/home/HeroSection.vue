<template>
  <section class="hero-section">
    <div class="container">
      <div class="hero-card">
        <h1 class="hero-title">Hôm nay ăn gì?</h1>
        <p class="hero-subtitle">
          Khám phá hàng nghìn công thức nấu ăn từ cộng đồng đam mê ẩm thực
        </p>
        
        <router-link to="/recipes" class="btn-explore">
          🍲 Khám phá công thức
        </router-link>
      </div>
    </div>
  </section>
</template>

<style scoped>
.hero-section {
  padding: 30px 0; /* Tạo khoảng cách trên dưới so với Header/Footer */
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Đây là lớp áo mới giúp giao diện "mềm" hơn */
.hero-card {
  background-color: #fffcf2; /* Màu vàng nhạt */
  border-radius: 30px;       /* 🔥 QUAN TRỌNG: Bo tròn góc banner 30px */
  padding: 80px 20px;        /* Khoảng cách bên trong cho thoáng */
  text-align: center;
  /* (Tùy chọn) Thêm chút bóng nhẹ cho nổi */
  /* box-shadow: 0 10px 30px -10px rgba(0,0,0,0.05); */
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  color: #1f2937; /* Màu đen xám đậm cho sang hơn đen tuyền */
  margin-bottom: 15px;
  letter-spacing: -0.02em; /* Thu hẹp khoảng cách chữ xíu cho hiện đại */
}

.hero-subtitle {
  font-size: 1.1rem;
  color: #6b7280; /* Màu xám trung tính */
  margin-bottom: 40px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.btn-explore {
  display: inline-block;
  background-color: #F97316;
  color: white;
  padding: 16px 40px;
  border-radius: 50px; /* 🔥 Bo tròn nút bấm tối đa (hình viên thuốc) */
  text-decoration: none;
  font-weight: bold;
  font-size: 1.1rem;
  box-shadow: 0 4px 15px rgba(249, 115, 22, 0.4); /* Bóng màu cam */
  transition: all 0.3s ease;
}

.btn-explore:hover {
  background-color: #ea580c;
  transform: translateY(-3px); /* Hiệu ứng nhấc nút lên khi di chuột */
  box-shadow: 0 8px 25px rgba(249, 115, 22, 0.5);
}
</style>