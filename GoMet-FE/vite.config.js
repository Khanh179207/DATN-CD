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
})