<template>
  <header 
    class="landing-header" 
    :class="{ 'is-scrolled': isScrolled }"
  >
    <div class="scroll-progress" :style="{ width: scrollProgress + '%' }"></div>

    <div class="container header-flex">
      <router-link to="/" class="logo-area">
        <div class="logo-circle-wrapper">
          <img src="@/assets/images/gomet.jpg" :alt="$t('common.gomet_logo')" class="header-logo" />
        </div>
        <div class="logo-brand">
          <span class="brand-name">GOMET</span>
          <span class="brand-dot"></span>
        </div>
      </router-link>

      <div class="header-right">
        <nav class="nav-links">
          <LangSwitcher class="premium-lang" />
          <a href="#" class="nav-link-item" @click.prevent="handleAuthAction('login')">
            {{ $t('auth.login') }}
          </a>
        </nav>

        <a href="#" class="btn-premium-cta" @click.prevent="handleAuthAction('register')">
          <span>{{ $t('auth.register') }}</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
        </a>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import LangSwitcher from '@/components/common/LangSwitcher.vue'

const emit = defineEmits(['open-auth'])
const route = useRoute()

const isScrolled = ref(false)
const scrollProgress = ref(0)

const handleScroll = () => {
  const scrollTop = window.scrollY
  const docHeight = document.documentElement.scrollHeight - window.innerHeight
  scrollProgress.value = (scrollTop / docHeight) * 100
  isScrolled.value = scrollTop > 20
}

const handleAuthAction = (type) => {
  if (route.path === '/') {
    const section = document.getElementById('sectionsigninlanding')
    if (section) {
      window.dispatchEvent(new CustomEvent('switch-auth-tab', { detail: type }))
      section.scrollIntoView({ behavior: 'smooth', block: 'center' })
      return
    }
  }
  emit('open-auth', type)
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped lang="scss" src="./LandingHeader.scss"></style>