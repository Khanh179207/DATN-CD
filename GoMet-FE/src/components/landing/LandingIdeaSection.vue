<template>
  <section class="master-pinned-wrapper" ref="masterSection">
    <div class="bg-visuals">
      <div class="orb orb-1 gsap-orb"></div>
      <div class="orb orb-2 gsap-orb"></div>
      <div class="orb orb-3 gsap-orb"></div>
    </div>

    <div class="horizontal-track" ref="horizontalTrack">
      
      <div class="slide-section section-idea">
        <div class="container idea-layout">
          
          <div class="floating-deco d1 gsap-float-slow">🌿</div>
          <div class="floating-deco d2 gsap-float-fast">🍅</div>

          <div class="idea-text">
            <div class="reveal-wrapper">
              <span class="sub-tag gsap-idea-reveal">{{ $t('landing.idea_tag', 'Nghệ Thuật Ẩm Thực') }}</span>
            </div>
            <h2 class="idea-title gsap-idea-text">
              Đánh Thức <br>
              <span class="text-gradient shine-effect">Mọi Giác Quan</span>
            </h2>
            <div class="reveal-wrapper">
              <p class="idea-desc gsap-idea-reveal">
                GoMet là bảo tàng lưu giữ di sản ẩm thực đa quốc gia. Nơi mỗi nguyên liệu được thổi hồn thành những kiệt tác độc bản trên bàn tiệc gia đình bạn.
              </p>
            </div>
            
            <div class="idea-stats gsap-idea-reveal">
              <div class="stat-item">
                <span class="stat-num"><span class="gsap-counter-recipes">15</span>k+</span>
                <span class="stat-label">Bài viết</span>
              </div>
              <div class="stat-sep"></div>
              <div class="stat-item">
                <span class="stat-num"><span class="gsap-counter-topics">500</span>+</span>
                <span class="stat-label">Danh mục</span>
              </div>
            </div>
          </div>

          <div class="idea-grid-wrapper">
            <div class="bento-gallery">
              <div class="bento-item bento-main gsap-bento" data-speed="0.2">
                <div class="bento-mask"><img src="https://images.unsplash.com/photo-1547592180-85f173990554?w=800" alt="Món chính"></div>
                <div class="bento-overlay">
                  <span class="b-tag">Signature</span>
                  <h3>Tinh Hoa Bếp Việt</h3>
                </div>
              </div>
              <div class="bento-item bento-tr gsap-bento" data-speed="0.5">
                <div class="bento-mask"><img src="https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600" alt="Healthy"></div>
                <div class="bento-overlay"><h3>Lối Sống Xanh</h3></div>
              </div>
              <div class="bento-item bento-br gsap-bento" data-speed="0.7">
                <div class="bento-mask"><img src="https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600" alt="Bánh"></div>
                <div class="bento-overlay"><h3>Bánh Ngọt</h3></div>
              </div>
              <div class="bento-item bento-float gsap-bento" data-speed="0.9">
                <div class="bento-mask"><img src="https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=800" alt="Thịt nướng"></div>
                <div class="bento-overlay"><h3>Fine Dining</h3></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="slide-section section-community">
        <div class="container community-layout">
          
          <div class="floating-deco d3 gsap-float-fast">🍷</div>
          <div class="floating-deco d4 gsap-float-slow">🧀</div>

          <div class="community-header gsap-comm-reveal-group">
            <span class="sub-tag">GoMet Elite Community</span>
            <h2 class="community-title">Nơi Tâm Hồn <br><span class="text-gradient shine-effect">Tìm Thấy Nhau</span></h2>
            <p class="community-desc">Kết nối cùng hơn 100.000 chuyên gia ẩm thực. Kiến tạo một cộng đồng văn minh, nơi đam mê được tôn vinh và lan tỏa.</p>
          </div>

          <div class="cards-container">
            <div class="user-card card-side gsap-comm-card" data-x="-300" data-rotate="-15" data-y="40">
              <div class="card-cover"><img src="https://images.unsplash.com/photo-1556911220-e15b29be8c8f?q=80&w=400"></div>
              <div class="card-body">
                <img src="https://i.pravatar.cc/150?u=10" class="user-avt">
                <h3>Góc Bếp Nhỏ</h3>
                <span class="handle">@gocbepnho</span>
                <div class="user-stats">
                  <div class="u-stat"><strong>12k</strong><span>Follow</span></div>
                  <div class="u-stat"><strong>45</strong><span>Bài viết</span></div>
                </div>
                <button class="btn-follow">Theo dõi</button>
              </div>
            </div>
            
            <div class="user-card card-center gsap-comm-card" data-x="0" data-rotate="0" data-y="0">
              <div class="badge-top">Top Creator</div>
              <div class="card-cover"><img src="https://images.unsplash.com/photo-1507048331197-7d4ac70811cf?q=80&w=400"></div>
              <div class="card-body">
                <img src="https://i.pravatar.cc/150?u=20" class="user-avt">
                <h3>Chef de Cuisine <span class="verified">✓</span></h3>
                <span class="handle">@masterchef_vn</span>
                <div class="user-stats">
                  <div class="u-stat"><strong>85k</strong><span>Follow</span></div>
                  <div class="u-stat"><strong>120</strong><span>Bài viết</span></div>
                </div>
                <button class="btn-follow primary">Đang theo dõi</button>
              </div>
            </div>
            
            <div class="user-card card-side gsap-comm-card" data-x="300" data-rotate="15" data-y="40">
              <div class="card-cover"><img src="https://images.unsplash.com/photo-1556911220-e15b29be8c8f?q=80&w=400"></div>
              <div class="card-body">
                <img src="https://i.pravatar.cc/150?u=30" class="user-avt">
                <h3>Sống Lành Mạnh</h3>
                <span class="handle">@healthy_life</span>
                <div class="user-stats">
                  <div class="u-stat"><strong>34k</strong><span>Follow</span></div>
                  <div class="u-stat"><strong>88</strong><span>Bài viết</span></div>
                </div>
                <button class="btn-follow">Theo dõi</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="slide-section section-plan">
        <div class="container plan-layout">
          
          <div class="floating-deco d6 gsap-float-fast">🍤</div>

          <div class="plan-visual">
            <div class="image-wrapper gsap-plan-main">
              <div class="circle-bg"></div>
              <div class="img-container">
                <div class="img-shine"></div> 
                <img src="https://images.unsplash.com/photo-1498837167922-ddd27525d352?q=80&w=800" alt="Meal Prep" class="main-img">
              </div>
              
              <div class="float-card calendar-card gsap-plan-float" data-x="80" data-y="-30">
                <div class="icon-box green-box">🚫</div>
                <div class="text-box">
                  <span class="label">Trải nghiệm</span>
                  <span class="value text-green">Không quảng cáo</span>
                </div>
                <div class="status-badge check-mark">✓</div>
              </div>
              
              <div class="float-card calories-card gsap-plan-float" data-x="-80" data-y="30">
                <div class="icon-box orange-box">📊</div>
                <div class="text-box">
                  <span class="label">Phân Tích</span>
                  <span class="value">Dinh dưỡng chuyên sâu</span>
                </div>
              </div>
            </div>
          </div>

          <div class="plan-content">
            <div class="reveal-wrapper"><span class="sub-tag gsap-plan-reveal">GoMet Premium</span></div>
            <h2 class="plan-title gsap-plan-text">
              Mở Khóa Đặc Quyền <br>
              <span class="text-gradient shine-effect">Thành Viên Cao Cấp</span>
            </h2>
            <div class="reveal-wrapper">
              <p class="plan-desc gsap-plan-reveal">
                Nâng tầm trải nghiệm ẩm thực của bạn. Tận hưởng không gian sáng tạo không giới hạn, loại bỏ hoàn toàn quảng cáo và nhận đặc quyền hỗ trợ từ chuyên gia.
              </p>
            </div>
            
            <div class="features-list">
              <div class="feature-item gsap-plan-reveal">
                <div class="f-icon">🚫</div>
                <div class="f-text">
                  <h4>Sạch bóng quảng cáo</h4>
                  <p>Tập trung 100% vào đam mê nấu nướng, không bị làm phiền.</p>
                </div>
              </div>
              <div class="feature-item gsap-plan-reveal">
                <div class="f-icon">📊</div>
                <div class="f-text">
                  <h4>Phân tích dinh dưỡng thông minh</h4>
                  <p>Theo dõi chi tiết lượng calo, macro và vi chất cho từng bữa ăn.</p>
                </div>
              </div>
            </div>
            
            <div class="action-wrapper gsap-plan-reveal" style="margin-top: 40px;">
              <button class="btn-plan-action">Nâng Cấp Premium Ngay <span class="arrow">→</span></button>
            </div>
          </div>
        </div>
      </div>

    </div>
  </section>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

import './LandingIdeaSection.scss'

gsap.registerPlugin(ScrollTrigger)

const masterSection = ref(null)
const horizontalTrack = ref(null)
const ctx = ref(null)

onMounted(() => {
  ctx.value = gsap.context(() => {
    
    const mainTl = gsap.timeline({
      scrollTrigger: {
        trigger: masterSection.value,
        start: "top top",
        end: "+=900%", 
        scrub: 1.2,
        pin: true,
        anticipatePin: 1
      }
    });

    // --- HỒI 1 ---
    mainTl.fromTo(masterSection.value, 
      { borderRadius: "100px 100px 0 0", y: 150, filter: "blur(5px)" },
      { borderRadius: "0px", y: 0, filter: "blur(0px)", duration: 0.8 }
    );
    mainTl.from(".gsap-idea-text", { yPercent: 60, rotation: 2, opacity: 0, duration: 0.8 }, 0.2);
    mainTl.from(".gsap-idea-reveal", { opacity: 0, y: 30, stagger: 0.1, duration: 0.6 }, 0.4);
    
    mainTl.from(".gsap-mask", { clipPath: "inset(100% 0% 0% 0%)", stagger: 0.15, duration: 1.2, ease: "power3.inOut" }, 0.3);
    gsap.utils.toArray(".gsap-bento").forEach((card) => {
      const speed = card.getAttribute("data-speed");
      mainTl.from(card, { y: 300 * speed, opacity: 0, scale: 0.9, duration: 1.2 }, 0.2);
    });

    mainTl.from(".gsap-counter-recipes", { innerText: 0, duration: 1.5, snap: { innerText: 1 } }, 0.5);
    mainTl.from(".gsap-counter-topics", { innerText: 0, duration: 1.5, snap: { innerText: 1 } }, 0.6);

    // --- HỒI 2 ---
    mainTl.to(horizontalTrack.value, { xPercent: -33.333, ease: "none", duration: 2.5 }, 2);
    mainTl.from(".gsap-comm-header", { y: 40, opacity: 0, duration: 1 }, 3);
    
    gsap.utils.toArray(".gsap-comm-card").forEach((card) => {
      const xVal = card.getAttribute("data-x");
      const rVal = card.getAttribute("data-rotate");
      
      mainTl.fromTo(card, 
        { 
          x: 0, y: 150, 
          rotationZ: 0, rotationY: 180, 
          opacity: 0, scale: 0.5,
          transformOrigin: "50% 50% -200px" 
        }, 
        { 
          x: xVal, y: 0, 
          rotationZ: rVal, rotationY: 0, 
          opacity: 1, 
          scale: card.classList.contains('card-center') ? 1.05 : 0.85, 
          duration: 1.8, 
          ease: "back.out(1.2)" 
        }, 
      3.2);
    });

    mainTl.to(".card-center", { 
      scale: 1.1, 
      boxShadow: "0 35px 80px rgba(234, 88, 12, 0.35)", 
      border: "3px solid rgba(234, 88, 12, 0.4)", 
      duration: 0.8,
      ease: "power2.out"
    }, 4.8);

    // --- HỒI 3 ---
    mainTl.to(horizontalTrack.value, { xPercent: -66.666, ease: "none", duration: 2.5 }, 6);
    mainTl.from(".gsap-plan-main", { scale: 0.8, opacity: 0, rotation: -5, duration: 1.5, ease: "elastic.out(1, 0.8)" }, 7.5);
    mainTl.from(".gsap-plan-text", { opacity: 0, y: 40, duration: 0.8 }, 7.8);
    mainTl.from(".gsap-plan-reveal", { opacity: 0, x: 40, stagger: 0.1, duration: 0.6 }, 8.2);
    
    gsap.utils.toArray(".gsap-plan-float").forEach((card) => {
      const xVal = card.getAttribute("data-x");
      const yVal = card.getAttribute("data-y");
      mainTl.from(card, { x: xVal, y: yVal, opacity: 0, scale: 0.5, duration: 1, ease: "back.out(1.5)" }, 8.5);
    });


    // --- IDLE ANIMATIONS ---
    gsap.to(".orb-1", { x: "15vw", y: "-5vh", duration: 10, repeat: -1, yoyo: true, ease: "sine.inOut" });
    gsap.to(".orb-2", { x: "-15vw", y: "10vh", duration: 12, repeat: -1, yoyo: true, ease: "sine.inOut" });
    
    gsap.to(".gsap-float-slow", { y: -25, rotation: 10, duration: 4, repeat: -1, yoyo: true, ease: "sine.inOut" });
    gsap.to(".gsap-float-fast", { y: 20, rotation: -15, duration: 3, repeat: -1, yoyo: true, ease: "sine.inOut" });

  }, masterSection.value);
});

onUnmounted(() => { if (ctx.value) ctx.value.revert(); });
</script>