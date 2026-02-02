import { createRouter, createWebHistory } from 'vue-router'

// --- 1. IMPORT LAYOUTS (Bộ khung) ---
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import LandingLayout from '@/layouts/LandingLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

// --- 2. IMPORT VIEWS (Trang nội dung) ---
// Home & Intro
import HomeView from '@/pages/home/HomeView.vue'

// Admin Pages (Đã thêm mới)
import AdminDashboard from '@/pages/admin/Dashboard.vue'
import PostManagement from '@/pages/admin/PostManagement.vue'       // 👈 Mới
import CategoryManagement from '@/pages/admin/CategoryManagement.vue' // 👈 Mới
import UserManagement from '@/pages/admin/UserManagement.vue'       // 👈 Mới

const routes = [
  // =======================================================
  // 1. LANDING PAGE (Trang khách vào đầu tiên)
  // URL: http://localhost:5173/
  // =======================================================
  {
    path: '/',
    component: LandingLayout,
    children: [
      {
        path: '',
        name: 'IntroPage',
        // Nếu chưa có file IntroPage, trỏ tạm về HomeView để test
        component: () => import('@/pages/intro/IntroPage.vue') 
      }
    ]
  },

  // =======================================================
  // 2. MAIN APP (Trang chủ Gomet - Giao diện Pinterest)
  // URL: http://localhost:5173/home
  // =======================================================
  {
    path: '/home',
    component: DefaultLayout, // Có Sidebar + Header tìm kiếm
    children: [
      {
        path: '', 
        name: 'Home',
        component: HomeView
      },
      // Các trang con khác
      {
        path: 'recipes',
        name: 'Recipes',
        component: () => import('@/pages/recipes/RecipeList.vue')
      },
      {
        path: 'events',
        name: 'Events',
        component: () => import('@/pages/events/EventList.vue')
      }
    ]
  },

  // =======================================================
  // 3. AUTHENTICATION (Đăng nhập / Đăng ký)
  // URL: http://localhost:5173/auth/login
  // =======================================================
  {
    path: '/auth',
    component: AuthLayout,
    children: [
      {
        path: 'login',
        name: 'Login',
        component: () => import('@/pages/auth/LoginPage.vue')
      },
      {
        path: 'register',
        name: 'Register',
        component: () => import('@/pages/auth/RegisterPage.vue')
      }
    ]
  },

  // =======================================================
  // 4. ADMIN DASHBOARD (Trang quản trị)
  // URL: http://localhost:5173/admin
  // =======================================================
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {
        path: '', // Mặc định vào Dashboard
        name: 'AdminDashboard',
        component: AdminDashboard
      },
      
      // --- CÁC TRANG QUẢN LÝ CHÍNH (Đã kết nối file thật) ---
      {
        path: 'posts',
        name: 'AdminPosts',
        component: PostManagement // Quản lý bài đăng (Duyệt/Xóa)
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: CategoryManagement // Quản lý danh mục (Thêm/Sửa/Xóa)
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: UserManagement // Quản lý tài khoản (Ban/Unban)
      },

      // --- CÁC TRANG PHỤ (Tái sử dụng mẫu giao diện có sẵn) ---
      // Dùng tạm PostManagement cho các trang có tính chất "Duyệt/Danh sách"
      { path: 'comments', component: PostManagement }, 
      { path: 'reports', component: PostManagement },

      // Dùng tạm CategoryManagement cho các trang có tính chất "Thêm/Sửa/Xóa đơn giản"
      { path: 'events', component: CategoryManagement },
      { path: 'achievements', component: CategoryManagement },
      { path: 'notifications', component: CategoryManagement },
    ]
  },

  // =======================================================
  // 5. NOT FOUND (Trang 404)
  // =======================================================
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/pages/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  // Tự động cuộn lên đầu trang khi chuyển route
  scrollBehavior(to, from, savedPosition) {
    return { top: 0 }
  }
})

export default router