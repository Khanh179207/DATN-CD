<template>
  <header class="landing-header">
    <div class="container header-flex">
      
      <router-link to="/" class="logo-area">
        <img src="@/assets/images/gomet.jpg" alt="Gomet" class="header-logo" />
        <span class="logo-text">GOMET</span>
      </router-link>

      <div class="header-right">
        <a href="#" class="btn-text" @click.prevent="$emit('open-auth', 'login')">
          Đăng nhập
        </a>
        <a href="#" class="btn-primary" @click.prevent="$emit('open-auth', 'register')">
          Đăng ký ngay
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
        </a>
      </div>
    </div>
  </header>
</template>

<script setup>
defineEmits(['open-auth'])
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;800&family=Quicksand:wght@500;600;700&display=swap');

/* --- HEADER CONTAINER (GLASSMORPHISM) --- */
.landing-header {
  position: fixed; top: 0; width: 100%; z-index: 100;
  background: rgba(255, 255, 255, 0.85); /* Tăng độ mờ nền chút để logo rõ hơn */
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0,0,0,0.05);
  padding: 15px 0;
  font-family: 'Quicksand', sans-serif;
  transition: all 0.3s ease;
}

.container { max-width: 1200px; margin: 0 auto; padding: 0 30px; }
.header-flex { display: flex; justify-content: space-between; align-items: center; }

/* --- LOGO STYLE --- */
.logo-area { text-decoration: none; display: flex; align-items: center; gap: 12px; }

.header-logo {
  width: 40px; height: 40px; 
  border-radius: 50%; /* Bo tròn hoàn toàn */
  object-fit: cover;
  border: 2px solid #F97316; /* Viền cam nhận diện thương hiệu */
  box-shadow: 0 4px 10px rgba(249, 115, 22, 0.2);
  transition: transform 0.3s ease;
}

.logo-area:hover .header-logo { transform: rotate(10deg); }

.logo-text { 
  font-family: 'Playfair Display', serif; font-weight: 800; font-size: 24px; 
  color: #1C1917; letter-spacing: 1px;
}

/* --- BUTTONS --- */
.header-right { display: flex; gap: 20px; align-items: center; }

.btn-text { 
  text-decoration: none; color: #57534E; font-weight: 700; transition: 0.2s; font-size: 0.95rem;
}
.btn-text:hover { color: #F97316; }

.btn-primary {
  text-decoration: none; background: #1C1917; color: white;
  padding: 10px 24px; border-radius: 30px; font-weight: 700; font-size: 0.95rem;
  display: flex; align-items: center; gap: 8px; transition: 0.3s;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}
.btn-primary:hover { 
  background: #F97316; 
  transform: translateY(-2px); 
  box-shadow: 0 10px 20px rgba(249, 115, 22, 0.3); 
}
</style>