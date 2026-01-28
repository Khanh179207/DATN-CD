import { createRouter, createWebHistory } from 'vue-router'

// Import Layouts
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import LandingLayout from '@/layouts/LandingLayout.vue'

const routes = [
  // --- 1. NHÓM LANDING PAGE (Trang giới thiệu - Pinterest Style) ---
  // Truy cập vào '/' sẽ thấy trang này đầu tiên
  {
    path: '/',
    component: LandingLayout,
    children: [
      {
        path: '',
        name: 'IntroPage',
        component: () => import('@/pages/intro/IntroPage.vue')
      }
    ]
  },

  // --- 2. NHÓM APP CHÍNH (Có Sidebar - DefaultLayout) ---
  // Các trang như Công thức, Sự kiện, Home sẽ nằm trong nhóm này
  {
    path: '/',
    component: DefaultLayout,
    children: [
      {
        path: 'home', // Truy cập: /home
        name: 'Home',
        component: () => import('@/pages/home/HomePage.vue')
      },
      {
        path: 'recipes', // Truy cập: /recipes
        name: 'Recipes',
        component: () => import('@/pages/recipes/RecipeList.vue')
      },
      {
        path: 'recipe/:id', // Truy cập: /recipe/123
        name: 'RecipeDetail',
        component: () => import('@/pages/recipes/RecipeDetail.vue')
      },
      {
        path: 'events', // Truy cập: /events
        name: 'Events',
        component: () => import('@/pages/events/EventList.vue')
      }
    ]
  },

  // --- 3. NHÓM AUTH (Đăng nhập/Đăng ký - Giao diện riêng) ---
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

  // --- 4. NHÓM USER (Cần đăng nhập - Premium - Có Sidebar) ---
  {
    path: '/user',
    component: DefaultLayout,
    meta: { requiresAuth: true }, // Đánh dấu cần login
    children: [
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/pages/user/UserProfile.vue')
      },
      {
        path: 'meal-plan', 
        name: 'MealPlan',
        component: () => import('@/pages/user/MealPlan.vue')
      },
      {
        path: 'shopping-list',
        name: 'ShoppingList',
        component: () => import('@/pages/user/ShoppingList.vue')
      }
    ]
  },

  // --- 5. ADMIN GROUP (Thêm nếu bạn đã làm AdminLayout) ---
  // {
  //   path: '/admin',
  //   component: () => import('@/layouts/AdminLayout.vue'),
  //   children: [ ... ]
  // },
  
  // --- TRANG LỖI 404 ---
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/pages/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router