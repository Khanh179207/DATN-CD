<template>
  <footer class="landing-footer">
    <div class="container footer-content">
      
      <div class="footer-col brand-col">
        <div class="brand-wrapper">
          <img src="@/assets/images/gomet.jpg" alt="Gomet Logo" class="brand-logo" />
          <span class="brand-name">GOMET</span>
        </div>
        <p class="brand-desc">
          Mạng xã hội chia sẻ công thức nấu ăn lớn nhất dành cho sinh viên. Khơi nguồn cảm hứng vào bếp mỗi ngày.
        </p>
        
        <div class="socials">
          <a href="#" class="social-link fb">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path></svg>
          </a>
          <a href="#" class="social-link ig">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path><line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line></svg>
          </a>
          <a href="#" class="social-link yt">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22.54 6.42a2.78 2.78 0 0 0-1.94-2C18.88 4 12 4 12 4s-6.88 0-8.6.46a2.78 2.78 0 0 0-1.94 2A29 29 0 0 0 1 11.75a29 29 0 0 0 .46 5.33A2.78 2.78 0 0 0 3.4 19c1.72.46 8.6.46 8.6.46s6.88 0 8.6-.46a2.78 2.78 0 0 0 1.94-2 29 29 0 0 0 .46-5.25 29 29 0 0 0-.46-5.33z"></path><polygon points="9.75 15.02 15.5 11.75 9.75 8.48 9.75 15.02"></polygon></svg>
          </a>
        </div>
      </div>

      <div class="footer-col">
        <h4>Khám phá</h4>
        <ul class="footer-links">
          <li><a href="#">Công thức mới</a></li>
          <li><a href="#">Bảng xếp hạng</a></li>
          <li><a href="#">Thử thách tuần</a></li>
          <li><a href="#">Blog ẩm thực</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>Về GOMET</h4>
        <ul class="footer-links">
          <li><a href="#">Giới thiệu</a></li>
          <li><a href="#">Tuyển dụng</a></li>
          <li><a href="#">Điều khoản</a></li>
          <li><a href="#">Bảo mật</a></li>
        </ul>
      </div>

      <div class="footer-col contact-col">
        <h4>Liên hệ</h4>
        <div class="contact-item">
          <span class="icon">📧</span>
          <div class="info">
            <span class="label">Email hỗ trợ</span>
            <span class="value">Đặng Công Minh Khánh</span>
          </div>
        </div>
        <div class="contact-item">
          <span class="icon">📞</span>
          <div class="info">
            <span class="label">Hotline</span>
            <span class="value">0823-024-745</span>
          </div>
        </div>
      </div>

    </div>
    
    <div class="footer-bottom">
      <div class="container bottom-flex">
        <p>&copy; 2026 GOMET. All rights reserved.</p>
        <div class="bottom-links">
          <a href="#">Quyền riêng tư</a>
          <span class="dot">•</span>
          <a href="#">Cookie</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;800&family=Quicksand:wght@500;600;700&display=swap');

.landing-footer {
  background-color: #FAFAF9; /* Nền kem sáng sang trọng */
  padding: 80px 0 0;
  border-top: 1px solid #E7E5E4;
  font-family: 'Quicksand', sans-serif;
  color: #1C1917;
}

.container { max-width: 1200px; margin: 0 auto; padding: 0 30px; }

.footer-content {
  display: grid; grid-template-columns: 1.5fr 1fr 1fr 1.2fr; gap: 60px;
  margin-bottom: 60px;
}

/* --- Brand Column --- */
.brand-wrapper { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }

.brand-logo {
  width: 45px; height: 45px; object-fit: cover;
  border-radius: 12px; /* Bo góc logo */
  box-shadow: 0 4px 10px rgba(249, 115, 22, 0.2); /* Bóng cam nhẹ */
}

.brand-name {
  font-family: 'Playfair Display', serif; font-size: 1.8rem; font-weight: 800;
  color: #1C1917; letter-spacing: 1px;
}

.brand-desc {
  color: #57534E; line-height: 1.6; margin-bottom: 25px; font-size: 0.95rem;
}

/* Socials */
.socials { display: flex; gap: 12px; }
.social-link {
  width: 40px; height: 40px; border-radius: 50%;
  border: 1px solid #E7E5E4; background: white;
  display: flex; align-items: center; justify-content: center;
  color: #57534E; transition: 0.3s;
}
.social-link:hover {
  background: #1C1917; color: white; border-color: #1C1917; transform: translateY(-3px);
}
.social-link.fb:hover { background: #1877F2; border-color: #1877F2; color: white; }
.social-link.ig:hover { background: #E4405F; border-color: #E4405F; color: white; }
.social-link.yt:hover { background: #FF0000; border-color: #FF0000; color: white; }

/* --- Link Columns --- */
h4 {
  font-family: 'Playfair Display', serif; font-size: 1.1rem; font-weight: 700;
  margin-bottom: 25px; color: #1C1917;
}

.footer-links { list-style: none; padding: 0; margin: 0; }
.footer-links li { margin-bottom: 12px; }
.footer-links a {
  text-decoration: none; color: #57534E; font-size: 0.95rem; font-weight: 500;
  transition: 0.2s; display: inline-block;
}
.footer-links a:hover { color: #F97316; transform: translateX(5px); }

/* --- Contact Column --- */
.contact-item { display: flex; align-items: center; gap: 15px; margin-bottom: 20px; }
.contact-item .icon { font-size: 1.2rem; }
.contact-item .info { display: flex; flex-direction: column; }
.contact-item .label { font-size: 0.75rem; color: #A8A29E; font-weight: 600; text-transform: uppercase; }
.contact-item .value { font-size: 0.95rem; color: #1C1917; font-weight: 700; }

/* --- Bottom --- */
.footer-bottom {
  border-top: 1px solid #E5E5E4; background: #F5F5F4;
  padding: 20px 0; font-size: 0.85rem; color: #78716C;
}
.bottom-flex { display: flex; justify-content: space-between; align-items: center; }
.bottom-links a { text-decoration: none; color: #78716C; transition: 0.2s; }
.bottom-links a:hover { color: #1C1917; }
.dot { margin: 0 10px; opacity: 0.5; }

/* Responsive */
@media (max-width: 900px) {
  .footer-content { grid-template-columns: 1fr 1fr; gap: 40px; }
}
@media (max-width: 600px) {
  .footer-content { grid-template-columns: 1fr; text-align: center; }
  .brand-wrapper, .socials { justify-content: center; }
  .contact-item { justify-content: center; }
  .contact-item .info { text-align: left; }
  .bottom-flex { flex-direction: column; gap: 10px; }
}
</style>