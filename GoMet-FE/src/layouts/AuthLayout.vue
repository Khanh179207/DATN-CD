<template>
  <div class="auth-layout min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white p-8 rounded shadow-md w-full max-w-md">
      <div class="mb-4">
        <router-link to="/" class="text-sm text-gray-500 hover:underline">← Về trang chủ</router-link>
      </div>
      
      <router-view />
    </div>
  </div>
</template>