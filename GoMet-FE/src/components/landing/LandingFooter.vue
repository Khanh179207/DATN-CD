<template>
  <footer class="landing-footer">
    <div class="container footer-content">
      
      <div class="footer-col brand-col">
        <div class="brand">
          <span class="icon">🍳</span> GOMET
        </div>
        <p>Mạng xã hội chia sẻ công thức nấu ăn lớn nhất dành cho sinh viên.</p>
        <div class="socials">
          <a href="#">Fb</a> <a href="#">Ig</a> <a href="#">Yt</a> <a href="#">Tt</a>
        </div>
      </div>

      <div class="footer-col">
        <h4>Khám phá</h4>
        <a href="#">Công thức mới</a>
        <a href="#">Bảng xếp hạng</a>
        <a href="#">Thử thách tuần</a>
        <a href="#">Blog ẩm thực</a>
      </div>

      <div class="footer-col">
        <h4>Về GOMET</h4>
        <a href="#">Giới thiệu</a>
        <a href="#">Tuyển dụng</a>
        <a href="#">Điều khoản</a>
        <a href="#">Bảo mật</a>
      </div>

      <div class="footer-col">
        <h4>Liên hệ</h4>
        <p>📍 Cập nhật sau</p>
        <p>📧 Đặng Công Minh Khánh</p>
        <p>📞 0823-024-745</p>
      </div>

    </div>
    
    <div class="footer-bottom">
      <p>&copy; 2026 GOMET. All rights reserved.</p>
    </div>
  </footer>
</template>

<style scoped>
.landing-footer { background-color: #2e2e2e; padding: 60px 0 20px; border-top: 1px solid #eee; margin-top: auto; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }

.footer-content { display: grid; grid-template-columns: 2fr 1fr 1fr 1.5fr; gap: 40px; margin-bottom: 40px; }

.brand { font-size: 1.8rem; font-weight: 800; color: #F97316; margin-bottom: 15px; display: flex; align-items: center; gap: 10px; }
.footer-col p { color: #ffffff; line-height: 1.6; margin-bottom: 15px; }
.footer-col h4 { font-size: 1.1rem; margin-bottom: 20px; color: #f97000; }
.footer-col a { display: block; color: #ffffff; text-decoration: none; margin-bottom: 10px; transition: 0.2s; }
.footer-col a:hover { color: #F97316; padding-left: 5px; }

.socials { display: flex; gap: 10px; }
.socials a {
  width: 36px; height: 36px; background: white; border: 1px solid #ddd;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-weight: bold; color: #333; margin: 0;
}
.socials a:hover { background: #F97316; color: white; border-color: #F97316; }

.footer-bottom { border-top: 1px solid #eee; padding-top: 20px; text-align: center; color: #999; font-size: 0.9rem; }

@media (max-width: 768px) {
  .footer-content { grid-template-columns: 1fr; gap: 30px; }
}
</style>