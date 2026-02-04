import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // <--- Thêm dòng này

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'), // <--- Thêm đoạn này để hiểu @ là src
    },
  },
  server: {
    host: true,
    cors: true,
    proxy: {
      // Gửi các request `/api/*` tới backend Spring Boot mặc định chạy ở 8080
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
})