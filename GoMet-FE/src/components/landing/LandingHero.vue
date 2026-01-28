<template>
  <section class="hero-text-section">
    <div class="hero-content-card">
      <h1 class="hero-title">MẠNG XÃ HỘI VỀ ẨM THỰC </h1>
      <p class="hero-subtitle">Lưu và chia sẻ công thức nấu ăn mỗi ngày</p>
      <router-link to="/home" class="btn-explore">Khám phá ngay</router-link>
    </div>

    <div class="yellow-bottom-bar" @click="scrollToNextSection">
      <span class="bar-text">Đăng ký tài khoản ngay</span>
      <span class="arrow-icon">﹀</span>
    </div>

  </section>
</template>

<script setup>
// Hàm xử lý: Khi bấm vào thanh vàng thì trượt xuống 1 màn hình
const scrollToNextSection = () => {
  window.scrollTo({
    top: window.innerHeight, // Trượt xuống đúng bằng chiều cao màn hình
    behavior: 'smooth'       // Trượt mượt mà
  });
}
</script>

<style scoped>
.hero-text-section {
  /* Quan trọng: relative để thanh vàng có thể định vị absolute theo nó */
  position: relative; 
  min-height: 100vh;
  display: flex; align-items: center; justify-content: center;
}

/* --- Style Card nội dung cũ --- */
.hero-content-card {
  text-align: center; max-width: 700px; padding: 50px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(15px);
  border-radius: 40px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  border: 1px solid rgba(255,255,255,0.5);
  animation: fadeIn 1s ease-out;
  /* Đẩy card lên một chút để không bị thanh vàng che mất */
  margin-bottom: 60px; 
}

.hero-title { font-size: 3.5rem; font-weight: 800; color: #222; margin-bottom: 15px; line-height: 1.1; }
.hero-subtitle { font-size: 1.2rem; color: #555; margin-bottom: 35px; }
.btn-explore {
  display: inline-block; background: #F97316; color: white;
  padding: 15px 40px; border-radius: 50px; text-decoration: none;
  font-weight: bold; font-size: 1.1rem;
  box-shadow: 0 10px 25px rgba(249, 115, 22, 0.4); transition: 0.3s;
}
.btn-explore:hover { transform: translateY(-3px) scale(1.05); background: #ea580c; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }


/* --- 👇 STYLE CHO THANH MÀU VÀNG --- */
.yellow-bottom-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: #fffd92; /* Màu vàng chuẩn Pinterest */
  padding: 5px 0;
  text-align: center;
  font-weight: 700;
  color: #111;
  cursor: pointer;
  z-index: 50; /* Nổi lên trên cùng */
  display: flex;
  flex-direction: column; /* Xếp chữ trên, mũi tên dưới */
  align-items: center;
  justify-content: center;
  gap: 5px;
  transition: background 0.2s;
}

.yellow-bottom-bar:hover {
  background-color: #fffb7d; /* Đậm hơn chút khi hover */
}

.arrow-icon {
  font-size: 1.2rem;
  animation: bounce 1.5s infinite; /* Hiệu ứng mũi tên nhún nhảy */
}

/* Animation nhún nhảy */
@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(5px); }
}
</style>