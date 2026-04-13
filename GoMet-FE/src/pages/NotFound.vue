<template>
  <div class="not-found-container">
    <h1>{{ $t('not_found.title') }}</h1>
    <p>{{ $t('not_found.subtitle') }}</p>
    <button @click="$router.push('/home')" class="btn-home">
      {{ $t('not_found.back_home') }}
    </button>
  </div>
</template>

<style scoped>
.not-found-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  background: #F8FAFC;
  font-family: 'Quicksand', sans-serif;
}
h1 { font-size: 6rem; color: #CBD5E1; margin: 0; font-weight: 800; }
p { font-size: 1.2rem; color: #64748B; margin: 10px 0 30px; }
.btn-home {
  padding: 10px 20px;
  background: #3B82F6;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}
.btn-home:hover { background: #2563EB; }
</style>