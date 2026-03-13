import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  define: {
    global: 'window',
  },
  build: {
    chunkSizeWarningLimit: 650,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes('node_modules')) return

          if (id.includes('/vue/') || id.includes('/pinia/') || id.includes('/vue-router/') || id.includes('/vue-i18n/')) {
            return 'framework'
          }

          if (id.includes('/gsap/')) {
            return 'motion'
          }

          if (id.includes('/socket.io-client/') || id.includes('/sockjs-client/') || id.includes('/stompjs/')) {
            return 'realtime'
          }

          if (id.includes('/@google/generative-ai/')) {
            return 'ai'
          }

          return 'vendor'
        },
      },
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      stompjs: path.resolve(__dirname, './node_modules/stompjs/lib/stomp.js'),
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
    headers: {
      'Cross-Origin-Opener-Policy': 'unsafe-none',
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