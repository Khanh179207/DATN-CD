<template>
  <header class="landing-header" :class="{ 'is-scrolled': isScrolled }">
    <div class="container header-flex">
      
      <router-link to="/" class="logo-area">
        <div class="logo-wrapper">
          <img src="@/assets/images/gomet.jpg" alt="Gomet" class="header-logo" />
        </div>
        <span class="logo-text">GOMET<span class="dot">.</span></span>
      </router-link>

      <div class="header-right">
        <a href="#" class="btn-text" @click.prevent="$emit('open-auth', 'login')">
          Đăng nhập
        </a>
        <a href="#" class="btn-primary" @click.prevent="$emit('open-auth', 'register')">
          <span>Đăng ký ngay</span>
          <div class="arrow-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
          </div>
        </a>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

defineEmits(['open-auth'])

const isScrolled = ref(false)

const handleScroll = () => {
  // Nếu cuộn quá 50px thì kích hoạt hiệu ứng đổi màu nền
  isScrolled.value = window.scrollY > 50
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;800&family=Quicksand:wght@500;600;700&display=swap');

/* --- ✨ DYNAMIC HEADER CONTAINER ✨ --- */
.landing-header {
  position: fixed; top: 0; left: 0; width: 100%; z-index: 1000;
  padding: 20px 0;
  font-family: 'Quicksand', sans-serif;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: transparent; /* Ban đầu trong suốt */
}

/* Khi cuộn xuống: Hiện nền kính mờ và thu hẹp padding */
.landing-header.is-scrolled {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  padding: 12px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.03);
}

.container { max-width: 1200px; margin: 0 auto; padding: 0 30px; }
.header-flex { display: flex; justify-content: space-between; align-items: center; }

/* --- ✨ LOGO AREA ✨ --- */
.logo-area { text-decoration: none; display: flex; align-items: center; gap: 14px; }

.logo-wrapper {
  position: relative;
  width: 44px; height: 44px;
}

.header-logo {
  width: 100%; height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #F97316;
  transition: all 0.5s ease;
  z-index: 2;
  position: relative;
}

/* Hiệu ứng hào quang nhỏ sau logo khi hover */
.logo-area:hover .header-logo {
  transform: rotate(15deg) scale(1.1);
  box-shadow: 0 0 20px rgba(249, 115, 22, 0.4);
}

.logo-text { 
  font-family: 'Playfair Display', serif; font-weight: 800; font-size: 26px; 
  color: #1C1917; letter-spacing: 1px;
  display: flex; align-items: baseline;
}
.logo-text .dot { color: #F97316; font-size: 32px; line-height: 0; margin-left: 2px; }

/* --- ✨ BUTTONS ✨ --- */
.header-right { display: flex; gap: 25px; align-items: center; }

.btn-text { 
  text-decoration: none; color: #1C1917; font-weight: 700; 
  transition: 0.3s; font-size: 0.95rem;
  position: relative;
}

/* Underline hiệu ứng cho Đăng nhập */
.btn-text::after {
  content: '';
  position: absolute; bottom: -4px; left: 0; width: 0; height: 2px;
  background: #F97316; transition: width 0.3s ease;
}
.btn-text:hover::after { width: 100%; }
.btn-text:hover { color: #F97316; }

.btn-primary {
  text-decoration: none; background: #1C1917; color: white;
  padding: 10px 28px; border-radius: 50px; font-weight: 700; font-size: 0.95rem;
  display: flex; align-items: center; gap: 10px; transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
}

.btn-primary:hover { 
  background: #F97316; 
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 10px 25px rgba(249, 115, 22, 0.4); 
}

/* Animation cho mũi tên */
.arrow-icon { transition: transform 0.3s ease; }
.btn-primary:hover .arrow-icon { transform: translateX(4px); }

/* Hiệu ứng loé sáng (Shine) khi hover vào nút */
.btn-primary::before {
  content: "";
  position: absolute; top: 0; left: -100%; width: 50%; height: 100%;
  background: linear-gradient(to right, transparent, rgba(255,255,255,0.2), transparent);
  transition: 0.5s;
}
.btn-primary:hover::before { left: 150%; }

/* Responsive */
@media (max-width: 640px) {
  .btn-text { display: none; } /* Mobile ẩn bớt text để gọn */
  .logo-text { font-size: 20px; }
  .container { padding: 0 20px; }
}
</style>