<template>
  <footer class="dashboard-footer" :class="{ 'premium-dark': isDarkTheme }">
    <div class="container footer-content">
      
      <div class="footer-col brand-col">
        <div class="brand-wrapper">
          <img src="@/assets/images/gomet.jpg" alt="Logo GoMet" class="brand-logo" />
          <span class="brand-name">GOMET<span>.</span></span>
        </div>
        <p class="brand-desc">
          Mạng xã hội chia sẻ công thức nấu ăn lớn nhất dành cho những tâm hồn đam mê ẩm thực.
        </p>
        
        <div class="socials">
          <a href="https://facebook.com" target="_blank" rel="noopener" class="social-link" title="Facebook">
            <i class="fab fa-facebook-f"></i>
          </a>
          <a href="https://instagram.com" target="_blank" rel="noopener" class="social-link" title="Instagram">
            <i class="fab fa-instagram"></i>
          </a>
          <a href="https://youtube.com" target="_blank" rel="noopener" class="social-link" title="YouTube">
            <i class="fab fa-youtube"></i>
          </a>
        </div>
      </div>

      <div class="footer-col">
        <h4>Khám Phá</h4>
        <ul class="footer-links">
          <li><router-link to="/home">Trang Chủ</router-link></li>
          <li><router-link to="/events">Sự Kiện Ẩm Thực</router-link></li>
          <li><router-link to="/leaderboard">Bảng xếp hạng</router-link></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>Tiện Ích</h4>
        <ul class="footer-links">
          <li><router-link to="/meal-plan">Lịch Trình Bữa Ăn</router-link></li>
          <li><router-link to="/suggestions">Khu Vui Chơi Định Mệnh</router-link></li>
          <li><router-link to="/terms-and-policy">Điều Khoản Dịch Vụ</router-link></li>
        </ul>
      </div>

      <div class="footer-col contact-col">
        <h4>Liên Hệ</h4>
        <a href="mailto:contact@gomet.vn" class="contact-item">
          <span class="label"><i class="fas fa-envelope"></i> Email</span>
          <span class="value">contact@gomet.vn</span>
        </a>
        <a href="tel:0823024745" class="contact-item">
          <span class="label"><i class="fas fa-phone-alt"></i> Hotline</span>
          <span class="value">0823-024-745</span>
        </a>
      </div>

    </div>
    
    <div class="footer-bottom">
      <p>&copy; 2024 GOMET. Đã đăng ký bản quyền.</p>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// Kiểm tra theme tối dựa vào cờ isDark trong router meta
const isDarkTheme = computed(() => route.meta.isDark === true)
</script>

<style scoped>
/* ─── BASE (LIGHT THEME) ─── */
.dashboard-footer {
  background-color: var(--color-neutral-0, #ffffff);
  background-image: radial-gradient(circle at 100% 0%, rgba(234, 88, 12, 0.04) 0%, transparent 45%);
  padding: 80px 0 40px;
  border-top: 1px solid var(--color-neutral-100, #f3f4f6);
  font-family: var(--font-ui, 'Mulish', sans-serif);
  color: var(--color-neutral-900, #111827);
  position: relative;
  z-index: 1;
  transition: all 0.5s ease;
}

.container { max-width: 1300px; margin: 0 auto; padding: 0 40px; }

.footer-content {
  display: grid; grid-template-columns: 1.5fr 1fr 1fr 1.2fr; gap: 40px;
  margin-bottom: 50px;
}

/* Brand */
.brand-wrapper { display: flex; align-items: center; gap: 12px; margin-bottom: 15px; }
.brand-logo { width: 45px; height: 45px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
.brand-name { font-family: var(--font-display, 'Playfair Display', serif); font-size: 1.8rem; font-weight: 900; transition: color 0.3s; letter-spacing: 1px;}
.brand-name span { color: var(--color-primary-600, #EA580C); }
.brand-desc { color: var(--color-neutral-500, #6b7280); line-height: 1.6; font-size: 0.9rem; margin-bottom: 20px; transition: color 0.3s; }

/* Socials */
.socials { display: flex; gap: 12px; }
.social-link {
  width: 44px; height: 44px; border-radius: 12px; background: var(--color-neutral-0, #fff); border: 1px solid var(--color-neutral-200, #e5e7eb);
  display: flex; align-items: center; justify-content: center; font-size: 1rem; color: var(--color-neutral-900, #111);
  text-decoration: none; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.social-link:hover { background: var(--color-primary-600, #EA580C); color: #fff; border-color: var(--color-primary-600, #EA580C); transform: translateY(-3px); box-shadow: 0 5px 15px rgba(234, 88, 12, 0.3);}

/* Links */
h4 { font-size: 1rem; font-weight: 800; margin-bottom: 20px; text-transform: uppercase; letter-spacing: 1px; transition: color 0.3s; }
.footer-links { list-style: none; padding: 0; margin: 0;}
.footer-links li { margin-bottom: 12px; }
.footer-links a { text-decoration: none; color: var(--color-neutral-500, #6b7280); font-size: 0.9rem; font-weight: 700; transition: all 0.3s ease; display: inline-block;}
.footer-links a:hover, .footer-links a.router-link-active { color: var(--color-primary-600, #EA580C); transform: translateX(5px); }

/* Contact */
.contact-item { margin-bottom: 18px; display: flex; flex-direction: column; text-decoration: none; transition: 0.3s; cursor: pointer; padding: 8px; border-radius: 8px;}
.contact-item:hover { background: rgba(234, 88, 12, 0.05); transform: translateX(5px);}
.contact-item .label { font-size: 0.75rem; color: var(--color-neutral-400, #9ca3af); font-weight: 800; text-transform: uppercase; margin-bottom: 4px; display: flex; align-items: center; gap: 6px;}
.contact-item .value { font-size: 1rem; font-weight: 800; color: var(--color-neutral-900, #111); transition: color 0.3s; }

/* Bottom */
.footer-bottom { 
  border-top: 1px solid var(--color-neutral-200, #e5e7eb); padding-top: 25px; text-align: center; 
  font-size: 0.85rem; color: var(--color-neutral-400, #9ca3af); font-weight: 700; transition: 0.3s; letter-spacing: 1px;
}

/* ─── PREMIUM DARK THEME OVERRIDES ─── */
.dashboard-footer.premium-dark {
  background-color: #050505;
  background-image: radial-gradient(circle at 100% 0%, rgba(234, 88, 12, 0.1) 0%, transparent 45%);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  color: #F4F0EA;
}

.dashboard-footer.premium-dark .brand-name { color: #F4F0EA; }
.dashboard-footer.premium-dark .brand-name span { color: #EA580C; }
.dashboard-footer.premium-dark .brand-desc { color: rgba(244, 240, 234, 0.6); }

.dashboard-footer.premium-dark h4 { color: #D4AF37; } /* Tiêu đề màu vàng Gold */
.dashboard-footer.premium-dark .footer-links a { color: rgba(244, 240, 234, 0.5); }
.dashboard-footer.premium-dark .footer-links a:hover, 
.dashboard-footer.premium-dark .footer-links a.router-link-active { color: #EA580C; }

.dashboard-footer.premium-dark .social-link {
  background: rgba(255, 255, 255, 0.03);
  border-color: rgba(255, 255, 255, 0.1);
  color: #F4F0EA;
}
.dashboard-footer.premium-dark .social-link:hover {
  background: #EA580C;
  border-color: #EA580C;
  color: white;
}

.dashboard-footer.premium-dark .contact-item:hover { background: rgba(212, 175, 55, 0.05); }
.dashboard-footer.premium-dark .contact-item .label { color: rgba(212, 175, 55, 0.7); } /* Label màu Gold mờ */
.dashboard-footer.premium-dark .contact-item .value { color: #F4F0EA; }

.dashboard-footer.premium-dark .footer-bottom {
  border-top-color: rgba(255, 255, 255, 0.05);
  color: rgba(244, 240, 234, 0.4);
}

/* Responsive */
@media (max-width: 992px) {
  .dashboard-footer { padding-top: 60px; }
  .footer-content { grid-template-columns: 1fr 1fr; }
  .brand-col { grid-column: span 2; margin-bottom: 20px;}
}

@media (max-width: 576px) {
  .footer-content { grid-template-columns: 1fr; text-align: center; gap: 30px;}
  .brand-col { grid-column: span 1; }
  .brand-wrapper, .socials { justify-content: center; }
  .contact-item { align-items: center; }
  .footer-links a:hover { transform: translateY(-3px); }
}
</style>