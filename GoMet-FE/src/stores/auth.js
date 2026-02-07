import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

export const useAuthStore = defineStore('auth', () => {
  const router = useRouter()
  
  // 1. STATE: Lấy từ LocalStorage ra nếu có, không thì là null
  const user = ref(JSON.parse(localStorage.getItem('user')) || null)

  // 2. GETTERS
  const isAuthenticated = computed(() => !!user.value)
  const isAdmin = computed(() => user.value?.role === 'admin')

  // 3. ACTIONS
  function login(email, password) {
    // --- MOCK LOGIC (GIẢ LẬP) ---
    
    // Trường hợp 1: Admin đăng nhập
    if (email === 'admin@gmail.com' && password === '123456') {
      user.value = {
        id: 1,
        name: 'Khánh Admin',
        email: 'admin@gmail.com',
        avatar: 'https://ui-avatars.com/api/?name=Khanh+Admin&background=F97316&color=fff',
        role: 'admin'
      }
    } 
    // Trường hợp 2: User thường
    else {
      user.value = {
        id: 2,
        name: 'Người Dùng Mẫu',
        email: email,
        avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100',
        role: 'user'
      }
    }

    // Lưu vào LocalStorage để F5 không mất
    localStorage.setItem('user', JSON.stringify(user.value))

    return user.value.role // Trả về role để biết đường chuyển trang
  }

  function logout() {
    user.value = null
    localStorage.removeItem('user')
    router.push('/') // Quay về trang landing
  }

  return { user, isAuthenticated, isAdmin, login, logout }
})