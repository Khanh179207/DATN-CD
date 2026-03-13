import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  define: {
    global: 'window',
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        // Suppress legacy @import deprecation warnings
        silenceDeprecations: ['legacy-js-api', 'global-builtin', 'color-functions', 'import'],
      },
    },
  },
  server: {
    host: true,
    cors: true,
    port: 5173,

    // FIX lỗi Google OAuth popup
// FIX lỗi Google OAuth popup
    headers: {
      'Cross-Origin-Opener-Policy': 'same-origin-allow-popups', // <--- Sửa dòng này
      'Cross-Origin-Embedder-Policy': 'unsafe-none',
    },

    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        // NOTE: No rewrite — keep /api prefix so BE controller mapping matches
      },
    },
  },
})