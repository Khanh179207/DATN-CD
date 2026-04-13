<template>
  <section class="terms-editorial-light">
    <div class="bg-layers-light">
      <div class="bg-paper-texture"></div>
      <div class="bg-dots-soft"></div>
      <div class="aura-light aura-orange"></div>
      <div class="aura-light aura-yellow"></div>
    </div>

    <div class="container">
      <header class="legal-hero-light" data-aos="fade-up">
        <div class="brand-badge-light">
          <span class="dot-vibrant"></span>
          <span class="text">{{ t('terms_page.badge') }}</span>
        </div>
        <h1 class="hero-title">{{ t('terms_page.title') }} <br/> & <span class="accent">{{ t('terms_page.title_accent') }}</span></h1>
        <div class="hero-line"></div>
        <p class="hero-subtitle">
          {{ t('terms_page.updated') }} <span class="date">{{ t('terms_page.updated_date') }}</span> <br/>
          {{ t('terms_page.subtitle') }}
        </p>
      </header>

      <div class="legal-layout-light">
        <aside class="legal-nav-light">
          <nav>
            <div class="nav-header">{{ t('terms_page.nav_title') }}</div>
            <ul class="nav-list">
              <li v-for="(item, index) in legalSections" :key="index">
                <a :href="'#' + item.id" @click.prevent="scrollTo(item.id)" class="nav-link">
                  <span class="num">{{ (index + 1).toString().padStart(2, '0') }}</span>
                  <span class="label">{{ item.title }}</span>
                </a>
              </li>
            </ul>
          </nav>
        </aside>

        <main class="legal-content-light">
          <section v-for="item in legalSections" :id="item.id" :key="item.id" class="content-block-light" data-aos="fade-up">
            <div class="block-header">
              <span class="block-num">{{ (legalSections.indexOf(item) + 1).toString().padStart(2, '0') }}</span>
              <h2>{{ item.title }}</h2>
            </div>
            <div class="block-body" v-html="item.content"></div>
          </section>

          <footer class="legal-footer-light">
            <div class="footer-card-light">
              <h3>{{ t('terms_page.support_title') }}</h3>
              <p>{{ t('terms_page.support_desc') }}</p>
              <a href="mailto:support@gomet.example" class="contact-btn">{{ t('terms_page.support_cta') }}</a>
            </div>
            <div class="final-note">
              <p>{{ t('terms_page.footer_note') }}</p>
              <router-link to="/" class="back-link">
                <span class="arrow">←</span> {{ t('terms_page.back_home') }}
              </router-link>
            </div>
          </footer>
        </main>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const legalSections = computed(() => ([
  {
    id: 'sec-01',
    title: t('terms_page.sections.accept_title'),
    content: t('terms_page.sections.accept_body')
  },
  {
    id: 'sec-02',
    title: t('terms_page.sections.account_title'),
    content: t('terms_page.sections.account_body')
  },
  {
    id: 'sec-03',
    title: t('terms_page.sections.ownership_title'),
    content: t('terms_page.sections.ownership_body')
  },
  {
    id: 'sec-04',
    title: t('terms_page.sections.premium_title'),
    content: t('terms_page.sections.premium_body')
  },
  {
    id: 'sec-05',
    title: t('terms_page.sections.community_title'),
    content: t('terms_page.sections.community_body')
  },
  {
    id: 'sec-06',
    title: t('terms_page.sections.privacy_title'),
    content: t('terms_page.sections.privacy_body')
  }
]))

const scrollTo = (id) => {
  const el = document.getElementById(id)
  if (el) {
    const offset = 100
    const bodyRect = document.body.getBoundingClientRect().top
    const elementRect = el.getBoundingClientRect().top
    window.scrollTo({
      top: elementRect - bodyRect - offset,
      behavior: 'smooth'
    })
  }
}

onMounted(() => {
  document.title = t('terms_page.doc_title')
})
</script>

<style scoped lang="scss">
.terms-editorial-light {
  background-color: #ffffff;
  color: #1c1917;
  min-height: 100vh;
  font-family: 'Inter', sans-serif;
  position: relative;
  overflow: hidden;
  padding-bottom: 120px;
}

/* --- HỆ THỐNG NỀN SÁNG (FIX VỤ TỐI THUI) --- */
.bg-layers-light {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}
.bg-paper-texture {
  position: absolute;
  inset: 0;
  background-color: #fafaf9; /* Màu nền hơi kem nhẹ sang trọng */
}
.bg-dots-soft {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(#e5e7eb 1px, transparent 1px);
  background-size: 30px 30px;
  opacity: 0.5;
}
.aura-light {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.15;
}
.aura-orange { width: 600px; height: 600px; background: #f97316; top: -10%; right: -5%; }
.aura-yellow { width: 500px; height: 500px; background: #fde047; bottom: 5%; left: -5%; }

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px;
  position: relative;
  z-index: 10;
}

/* --- HERO SÁNG --- */
.legal-hero-light {
  padding-top: 100px;
  padding-bottom: 70px;
  text-align: center;

  .brand-badge-light {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    background: #fff;
    padding: 8px 20px;
    border-radius: 100px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.05);
    border: 1px solid #f1f1f1;
    margin-bottom: 30px;
    .dot-vibrant { width: 6px; height: 6px; background: #f97316; border-radius: 50%; }
    .text { font-size: 0.7rem; font-weight: 800; letter-spacing: 2px; color: #444; }
  }

  .hero-title {
    font-family: 'Playfair Display', serif;
    font-size: 4.5rem;
    font-weight: 800;
    color: #1c1917;
    line-height: 1.1;
    margin-bottom: 25px;
    .accent { color: #f97316; font-style: italic; }
  }

  .hero-line { width: 60px; height: 3px; background: #f97316; margin: 0 auto 30px; }

  .hero-subtitle {
    font-size: 1.1rem;
    color: #6b7280;
    max-width: 650px;
    margin: 0 auto;
    line-height: 1.8;
    .date { color: #1c1917; font-weight: 700; border-bottom: 2px solid #f97316; }
  }
}

/* --- LAYOUT --- */
.legal-layout-light {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 80px;
}

.legal-nav-light {
  position: sticky;
  top: 100px;
  height: fit-content;
  .nav-header { font-size: 0.75rem; text-transform: uppercase; color: #9ca3af; margin-bottom: 25px; font-weight: 700; letter-spacing: 1px; }
  .nav-list {
    list-style: none;
    .nav-link {
      display: flex;
      align-items: center;
      gap: 15px;
      text-decoration: none;
      color: #6b7280;
      margin-bottom: 22px;
      transition: 0.3s;
      .num { font-family: 'Playfair Display', serif; font-size: 0.85rem; color: #d1d5db; }
      .label { font-size: 1rem; font-weight: 600; }
      &:hover { color: #f97316; transform: translateX(5px); .num { color: #f97316; } }
    }
  }
}

/* --- CONTENT BLOCKS --- */
.content-block-light {
  background: #ffffff;
  padding: 50px;
  border-radius: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.03);
  border: 1px solid #f1f1f1;
  margin-bottom: 40px;
  transition: 0.3s;
  &:hover { transform: translateY(-5px); box-shadow: 0 20px 50px rgba(0, 0, 0, 0.06); }

  .block-header {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 25px;
    .block-num { font-family: 'Playfair Display', serif; font-size: 1.5rem; color: #f97316; opacity: 0.3; }
    h2 { font-family: 'Playfair Display', serif; font-size: 2rem; color: #1c1917; }
  }

  .block-body {
    color: #4b5563;
    line-height: 1.9;
    font-size: 1.05rem;
    :deep(p) { margin-bottom: 20px; }
    :deep(strong) { color: #1c1917; font-weight: 700; }
  }
}

/* --- FOOTER SÁNG --- */
.footer-card-light {
  background: linear-gradient(135deg, #fff 0%, #fff7ed 100%);
  padding: 60px;
  border-radius: 35px;
  border: 2px solid #ffedd5;
  text-align: center;
  margin-bottom: 60px;
  box-shadow: 0 15px 45px rgba(249, 115, 22, 0.05);

  h3 { font-family: 'Playfair Display', serif; font-size: 2.4rem; margin-bottom: 15px; color: #1c1917; }
  p { color: #6b7280; margin-bottom: 35px; max-width: 450px; margin-inline: auto; }

  .contact-btn {
    display: inline-block;
    background: #1c1917;
    color: #fff;
    padding: 16px 40px;
    text-decoration: none;
    border-radius: 100px;
    font-weight: 700;
    transition: 0.3s;
    &:hover { background: #f97316; transform: scale(1.05); box-shadow: 0 10px 25px rgba(249, 115, 22, 0.2); }
  }
}

.final-note {
  text-align: center;
  color: #9ca3af;
  padding-bottom: 40px;
  .back-link {
    display: inline-block;
    margin-top: 15px;
    color: #f97316;
    text-decoration: none;
    font-weight: 700;
    font-size: 0.95rem;
    .arrow { display: inline-block; transition: 0.3s; margin-right: 5px; }
    &:hover { .arrow { transform: translateX(-5px); } }
  }
}

/* --- RESPONSIVE --- */
@media (max-width: 1024px) {
  .legal-layout-light { grid-template-columns: 1fr; }
  .legal-nav-light { display: none; }
  .hero-title { font-size: 3rem; }
}
</style>