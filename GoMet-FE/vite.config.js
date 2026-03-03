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