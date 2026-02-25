<template>
  <div id="app">
    <router-view />
    <ToastContainer />
  </div>
</template>

<script setup>
import ToastContainer from '@/components/common/ToastContainer.vue'
</script>

<style>
/* Global styles are now handled by:
   - assets/styles/design-tokens.css (CSS custom properties)
   - assets/styles/base.css (resets, scrollbar, utilities)
   Both imported in main.js
*/
</style>