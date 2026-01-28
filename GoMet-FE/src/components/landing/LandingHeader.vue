<template>
  <header class="landing-header">
    <div class="container header-flex">
      <div class="header-left">
        <router-link to="/" class="logo">
          <span style="font-size: 24px;">🍳</span>
          <span style="font-weight: 800; color: #F97316; font-size: 20px;">GOMET</span>
        </router-link>
      </div>

     <div class="header-right">
        <a href="#" class="btn-text" @click.prevent="$emit('open-auth', 'login')">
          Đăng nhập
        </a>
        <a href="#" class="btn-primary" @click.prevent="$emit('open-auth', 'register')">
          Đăng ký
        </a>
      </div>
    </div>
  </header>
</template>

<script setup>
// Khai báo sự kiện để Vue không báo cảnh báo
defineEmits(['open-auth'])
</script>

<style scoped>
.landing-header {
  position: fixed; top: 0; width: 100%; z-index: 50;
  background: rgba(255, 255, 255, 0.9); backdrop-filter: blur(5px);
  padding: 15px 0;
}
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.header-flex { display: flex; justify-content: space-between; align-items: center; }
.logo { text-decoration: none; display: flex; align-items: center; gap: 5px; }
.header-right { display: flex; gap: 15px; align-items: center; }

.btn-text { text-decoration: none; color: #333; font-weight: 600; }
.btn-primary {
  text-decoration: none; background: #F97316; color: white;
  padding: 8px 20px; border-radius: 20px; font-weight: bold;
}
.btn-primary:hover { background: #ea580c; }
</style>