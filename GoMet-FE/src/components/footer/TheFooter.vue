<template>
  <footer class="dashboard-footer">
    <div class="container footer-content">
      
      <div class="footer-col brand-col">
        <div class="brand-wrapper">
          <img src="@/assets/images/gomet.jpg" alt="Gomet Logo" class="brand-logo" />
          <span class="brand-name">GOMET<span>.</span></span>
        </div>
        <p class="brand-desc">
          Mạng xã hội chia sẻ công thức nấu ăn lớn nhất dành cho sinh viên.
        </p>
        
        <div class="socials">
          <a href="#" class="social-link">Fb</a>
          <a href="#" class="social-link">Ig</a>
          <a href="#" class="social-link">Yt</a>
        </div>
      </div>

      <div class="footer-col">
        <h4>Khám phá</h4>
        <ul class="footer-links">
          <li><a href="#">Công thức mới</a></li>
          <li><a href="#">Bảng xếp hạng</a></li>
          <li><a href="#">Thử thách tuần</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>Hỗ trợ</h4>
        <ul class="footer-links">
          <li><a href="#">Góp ý</a></li>
          <li><a href="#">Điều khoản</a></li>
          <li><a href="#">Trợ giúp</a></li>
        </ul>
      </div>

      <div class="footer-col contact-col">
        <h4>Liên hệ</h4>
        <div class="contact-item">
          <span class="label">Email</span>
          <span class="value">contact@gomet.vn</span>
        </div>
        <div class="contact-item">
          <span class="label">Hotline</span>
          <span class="value">0823-024-745</span>
        </div>
      </div>

    </div>
    
    <div class="footer-bottom">
      <p>&copy; 2026 GOMET. All rights reserved.</p>
    </div>
  </footer>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@500;600;700;800&family=Playfair+Display:wght@800&display=swap');

.dashboard-footer {
  background-color: #FAFAF9;
  /* 👇 FIX LỖI Ở ĐÂY: Đẩy nội dung xuống 200px để nhường chỗ cho Newsletter 👇 */
  padding: 220px 0 40px; 
  /* ---------------------------------------------------------------------- */
  border-top: 1px solid #F3F4F6;
  font-family: 'Mulish', sans-serif;
  color: #1C1917;
  position: relative;
  z-index: 1;
}

.container { max-width: 1300px; margin: 0 auto; padding: 0 40px; }

.footer-content {
  display: grid; grid-template-columns: 1.5fr 1fr 1fr 1.2fr; gap: 40px;
  margin-bottom: 50px;
}

/* Brand */
.brand-wrapper { display: flex; align-items: center; gap: 12px; margin-bottom: 15px; }
.brand-logo { width: 45px; height: 45px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
.brand-name { font-family: 'Playfair Display', serif; font-size: 1.8rem; font-weight: 800; }
.brand-name span { color: #EA580C; }
.brand-desc { color: #6B7280; line-height: 1.6; font-size: 0.9rem; margin-bottom: 20px; }

/* Socials */
.socials { display: flex; gap: 10px; }
.social-link {
  width: 36px; height: 36px; border-radius: 10px; background: white; border: 1px solid #E5E7EB;
  display: flex; align-items: center; justify-content: center; font-size: 0.8rem; font-weight: 700; color: #1C1917; transition: 0.3s;
}
.social-link:hover { background: #EA580C; color: white; border-color: #EA580C; }

/* Links */
h4 { font-size: 1rem; font-weight: 800; margin-bottom: 20px; text-transform: uppercase; letter-spacing: 0.5px; }
.footer-links { list-style: none; padding: 0; }
.footer-links li { margin-bottom: 10px; }
.footer-links a { text-decoration: none; color: #6B7280; font-size: 0.9rem; font-weight: 600; transition: 0.2s; }
.footer-links a:hover { color: #EA580C; }

/* Contact */
.contact-item { margin-bottom: 15px; display: flex; flex-direction: column; }
.contact-item .label { font-size: 0.75rem; color: #9CA3AF; font-weight: 700; text-transform: uppercase; }
.contact-item .value { font-size: 0.95rem; font-weight: 700; color: #1C1917; }

/* Bottom */
.footer-bottom { 
  border-top: 1px solid #E5E7EB; padding-top: 25px; text-align: center; 
  font-size: 0.85rem; color: #9CA3AF; font-weight: 600;
}

/* Responsive */
@media (max-width: 900px) {
  .dashboard-footer { padding-top: 180px; } /* Mobile có thể giảm chút */
  .footer-content { grid-template-columns: 1fr 1fr; }
  .brand-col { grid-column: span 2; }
}
@media (max-width: 600px) {
  .footer-content { grid-template-columns: 1fr; text-align: center; }
  .brand-col { grid-column: span 1; }
  .brand-wrapper, .socials { justify-content: center; }
}
</style>