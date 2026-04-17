<template>
  <footer class="landing-footer">
    <div class="container footer-content">
      
      <div class="footer-col brand-col">
        <div class="brand-wrapper">
          <img src="@/assets/images/gomet.jpg" alt="Gomet Logo" class="brand-logo" />
          <span class="brand-name">GOMET</span>
        </div>
        <p class="brand-desc">
          Mạng xã hội chia sẻ công thức nấu ăn lớn nhất dành cho những tâm hồn đam mê ẩm thực. Khơi nguồn cảm hứng vào bếp mỗi ngày.
        </p>
        
        <div class="socials">
          <a href="https://facebook.com" target="_blank" rel="noopener" class="social-link fb" title="Facebook">
            <i class="fab fa-facebook-f"></i>
          </a>
          <a href="https://instagram.com" target="_blank" rel="noopener" class="social-link ig" title="Instagram">
            <i class="fab fa-instagram"></i>
          </a>
          <a href="https://youtube.com" target="_blank" rel="noopener" class="social-link yt" title="YouTube">
            <i class="fab fa-youtube"></i>
          </a>
        </div>
      </div>

      <div class="footer-col">
        <h4>Khám Phá</h4>
        <ul class="footer-links">
          <li><router-link to="/search">Công Thức Mới</router-link></li>
          <li><router-link to="/leaderboard">Bảng Xếp Hạng</router-link></li>
          <li><router-link to="/events">Sự Kiện Ẩm Thực</router-link></li>
          <li><router-link to="/home">Blog Ẩm Thực</router-link></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>Về GOMET</h4>
        <ul class="footer-links">
          <li><router-link to="/">Giới Thiệu</router-link></li>
          <li><a href="#" @click.prevent="handleCareersClick">Tuyển Dụng</a></li>
          <li><router-link to="/terms-and-policy">Chính Sách & Điều Khoản</router-link></li>
          <li><a href="#" @click.prevent="showAppealModal = true">Khiếu Nại</a></li>
        </ul>
      </div>

      <div class="footer-col contact-col">
        <h4>Liên Hệ</h4>
        
        <a href="mailto:contact@gomet.vn" class="contact-item">
          <span class="icon"><i class="fas fa-envelope"></i></span>
          <div class="info">
            <span class="label">Email Hỗ Trợ</span>
            <span class="value">contact@gomet.vn</span>
          </div>
        </a>
        
        <a href="tel:0823024745" class="contact-item">
          <span class="icon"><i class="fas fa-phone-alt"></i></span>
          <div class="info">
            <span class="label">Hotline (24/7)</span>
            <span class="value">0823-024-745</span>
          </div>
        </a>
      </div>

    </div>
    
    <div class="footer-bottom">
      <div class="container bottom-flex">
        <p>&copy; 2024 GOMET. Đã đăng ký bản quyền.</p>
      </div>
    </div>
  </footer>

  <Teleport to="body">
    <AppealModal v-if="showAppealModal" @close="showAppealModal = false" />
  </Teleport>
</template>

<script setup>
import { ref } from 'vue'
import AppealModal from '@/components/modals/AppealModal.vue'
import { toast } from '@/composables/useToast' // Tái sử dụng hệ thống Toast của dự án

const showAppealModal = ref(false)

const handleCareersClick = () => {
  // Thay thế alert() bằng toast cho xịn xò và đồng bộ UX
  toast.info('GoMet hiện tại chưa có nhu cầu tuyển dụng. Vui lòng quay lại sau!')
}
</script>

<style scoped>
.landing-footer {
  background-color: #FAFAF9;
  padding: 80px 0 0;
  border-top: 1px solid #E7E5E4;
  font-family: 'Quicksand', sans-serif;
  color: #1C1917;
}

.container { max-width: 1200px; margin: 0 auto; padding: 0 30px; }

.footer-content {
  display: grid; 
  grid-template-columns: 1.5fr 1fr 1fr 1.2fr; 
  gap: 60px;
  margin-bottom: 60px;
}

.brand-wrapper { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }

.brand-logo {
  width: 45px; height: 45px; object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(249, 115, 22, 0.2);
}

.brand-name {
  font-family: 'Playfair Display', serif; font-size: 1.8rem; font-weight: 900;
  color: #1C1917; letter-spacing: 1px;
}

.brand-desc {
  color: #57534E; line-height: 1.6; margin-bottom: 25px; font-size: 0.95rem; font-weight: 500;
}

/* Socials Icons FontAwesome */
.socials { display: flex; gap: 12px; }
.social-link {
  width: 40px; height: 40px; border-radius: 50%;
  border: 1px solid #E7E5E4; background: white;
  display: flex; align-items: center; justify-content: center; font-size: 1.1rem;
  color: #57534E; text-decoration: none; transition: all 0.3s ease;
}
.social-link:hover {
  background: #1C1917; color: white; border-color: #1C1917; transform: translateY(-4px); box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}
.social-link.fb:hover { background: #1877F2; border-color: #1877F2; color: white; }
.social-link.ig:hover { 
  background: radial-gradient(circle at 30% 107%, #fdf497 0%, #fdf497 5%, #fd5949 45%, #d6249f 60%, #285AEB 90%); 
  border-color: transparent; color: white; 
}
.social-link.yt:hover { background: #FF0000; border-color: #FF0000; color: white; }

h4 {
  font-family: 'Playfair Display', serif; font-size: 1.2rem; font-weight: 800;
  margin-bottom: 25px; color: #1C1917; letter-spacing: 0.5px;
}

.footer-links { list-style: none; padding: 0; margin: 0; }
.footer-links li { margin-bottom: 12px; }
.footer-links a, .footer-links :deep(a) {
  text-decoration: none; color: #57534E; font-size: 0.95rem; font-weight: 600;
  transition: all 0.2s ease; display: inline-block; cursor: pointer;
}
.footer-links a:hover, .footer-links :deep(a):hover { color: #F97316; transform: translateX(5px); }

/* Liên Hệ Bấm Được (Clickable Contact) */
.contact-item { 
  display: flex; align-items: center; gap: 15px; margin-bottom: 20px; text-decoration: none;
  padding: 10px; border-radius: 12px; transition: all 0.3s ease; border: 1px solid transparent;
}
.contact-item:hover { background: #fff; border-color: #E7E5E4; box-shadow: 0 4px 10px rgba(0,0,0,0.05); transform: translateX(5px);}
.contact-item .icon { font-size: 1.4rem; color: #F97316; }
.contact-item .info { display: flex; flex-direction: column; }
.contact-item .label { font-size: 0.75rem; color: #A8A29E; font-weight: 700; text-transform: uppercase; letter-spacing: 1px;}
.contact-item .value { font-size: 1rem; color: #1C1917; font-weight: 800; transition: color 0.2s;}
.contact-item:hover .value { color: #F97316; }

.footer-bottom {
  border-top: 1px solid #E5E5E4; background: #F5F5F4;
  padding: 20px 0; font-size: 0.85rem; color: #78716C;
}
/* Căn giữa nội dung Copyright do đã bỏ đi các Link */
.bottom-flex { display: flex; justify-content: center; align-items: center; }

/* Responsive */
@media (max-width: 1024px) {
  .footer-content { gap: 30px; }
  .container { padding: 0 20px; }
}

@media (max-width: 768px) {
  .landing-footer { padding-top: 50px; }
  .footer-content {
    grid-template-columns: 1fr 1fr; 
    gap: 40px 20px;
  }
  .brand-col { grid-column: span 2; margin-bottom: 10px; }
}

@media (max-width: 480px) {
  .footer-content { grid-template-columns: 1fr; text-align: center; gap: 40px; }
  .brand-col { grid-column: span 1; }
  .brand-wrapper, .socials, .contact-item { justify-content: center; }
  
  .contact-item:hover { transform: none; } 
  .contact-item .info { text-align: left; }
  .footer-links a:hover { transform: none; color: #F97316; }
}
</style>