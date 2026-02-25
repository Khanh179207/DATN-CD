<template>
  <footer class="dashboard-footer">
    <div class="container footer-content">
      
      <div class="footer-col brand-col">
        <div class="brand-wrapper">
          <img src="@/assets/images/gomet.jpg" alt="Gomet Logo" class="brand-logo" />
          <span class="brand-name">GOMET<span>.</span></span>
        </div>
        <p class="brand-desc">
          The largest recipe-sharing social network for food lovers and culinary enthusiasts.
        </p>
        
        <div class="socials">
          <a href="#" class="social-link">Fb</a>
          <a href="#" class="social-link">Ig</a>
          <a href="#" class="social-link">Yt</a>
        </div>
      </div>

      <div class="footer-col">
        <h4>Explore</h4>
        <ul class="footer-links">
          <li><a href="#">New Recipes</a></li>
          <li><a href="#">Leaderboard</a></li>
          <li><a href="#">Weekly Challenge</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>Support</h4>
        <ul class="footer-links">
          <li><a href="#">Feedback</a></li>
          <li><a href="#">Terms of Use</a></li>
          <li><a href="#">Help Center</a></li>
        </ul>
      </div>

      <div class="footer-col contact-col">
        <h4>Contact</h4>
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
.dashboard-footer {
  background-color: var(--color-neutral-0);
  background-image: radial-gradient(circle at 100% 0%, rgba(234, 88, 12, 0.04) 0%, transparent 45%);
  padding: 80px 0 40px;
  border-top: 1px solid var(--color-neutral-100);
  font-family: var(--font-ui);
  color: var(--color-neutral-900);
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
.brand-name { font-family: var(--font-display); font-size: 1.8rem; font-weight: 800; }
.brand-name span { color: var(--color-primary-600); }
.brand-desc { color: var(--color-neutral-500); line-height: 1.6; font-size: 0.9rem; margin-bottom: 20px; }

/* Socials */
.socials { display: flex; gap: 10px; }
.social-link {
  width: 44px; height: 44px; border-radius: var(--radius-md); background: var(--color-neutral-0); border: 1px solid var(--color-neutral-200);
  display: flex; align-items: center; justify-content: center; font-size: 0.8rem; font-weight: 700; color: var(--color-neutral-900);
  transition: background var(--duration-fast) var(--ease-out),
              color     var(--duration-fast) var(--ease-out),
              border-color var(--duration-fast) var(--ease-out);
}
.social-link:hover { background: var(--color-primary-600); color: var(--color-neutral-0); border-color: var(--color-primary-600); }

/* Links */
h4 { font-size: 1rem; font-weight: 800; margin-bottom: 20px; text-transform: uppercase; letter-spacing: 0.5px; }
.footer-links { list-style: none; padding: 0; }
.footer-links li { margin-bottom: 10px; }
.footer-links a { text-decoration: none; color: var(--color-neutral-500); font-size: 0.9rem; font-weight: 600; transition: color var(--duration-fast) var(--ease-out); }
.footer-links a:hover { color: var(--color-primary-600); }

/* Contact */
.contact-item { margin-bottom: 15px; display: flex; flex-direction: column; }
.contact-item .label { font-size: 0.75rem; color: var(--color-neutral-400); font-weight: 700; text-transform: uppercase; }
.contact-item .value { font-size: 0.95rem; font-weight: 700; color: var(--color-neutral-900); }

/* Bottom */
.footer-bottom { 
  border-top: 1px solid #E5E7EB; padding-top: 25px; text-align: center; 
  font-size: 0.85rem; color: #9CA3AF; font-weight: 600;
}

/* Responsive */
@media (max-width: 900px) {
  .dashboard-footer { padding-top: 80px; } /* Mobile — reduce top padding */
  .footer-content { grid-template-columns: 1fr 1fr; }
  .brand-col { grid-column: span 2; }
}
@media (max-width: 600px) {
  .footer-content { grid-template-columns: 1fr; text-align: center; }
  .brand-col { grid-column: span 1; }
  .brand-wrapper, .socials { justify-content: center; }
}
</style>