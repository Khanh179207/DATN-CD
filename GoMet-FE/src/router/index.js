import { createRouter, createWebHistory } from 'vue-router'

// --- 1. IMPORT LAYOUTS (Bộ khung) ---
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import LandingLayout from '@/layouts/LandingLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

// --- 2. IMPORT VIEWS (Trang nội dung) ---
import HomeView from '@/pages/home/HomeView.vue'
import SearchPage from '@/pages/search/SearchPage.vue'
import PostDetail from '@/pages/home/PostDetail.vue'

// --- ADMIN PAGES ---
import AdminDashboard from '@/pages/admin/Dashboard.vue'
import PostManagement from '@/pages/admin/PostManagement.vue'
import CategoryManagement from '@/pages/admin/CategoryManagement.vue'
import UserManagement from '@/pages/admin/UserManagement.vue'
import EventManagement from '@/pages/admin/EventManagement.vue'
import CommentManagement from '@/pages/admin/CommentManagement.vue'
import ReportManagement from '@/pages/admin/ReportManagement.vue'
import NotificationManagement from '@/pages/admin/NotificationManagement.vue'
import AchievementManagement from '@/pages/admin/AchievementManagement.vue'
import Statistics from '@/pages/admin/Statistics.vue'

const routes = [
  // =======================================================
  // 1. LANDING PAGE (Trang giới thiệu)
  // URL: http://localhost:5173/
  // =======================================================
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

  // =======================================================
  // 2. MAIN APP (Có Header + Sidebar + Footer)
  // URL: /home, /search, /post/123
  // =======================================================
  {
    path: '/', // Dùng chung layout cho các trang con
    component: DefaultLayout,
    children: [
      // Trang chủ
      {
        path: 'home', 
        name: 'Home',
        component: HomeView
      },
      // 👇 Trang tìm kiếm (Đưa vào đây để có Header)
      {
        path: 'search',
        name: 'Search',
        component: SearchPage
      },
      // 👇 Trang chi tiết bài viết
      {
        path: 'post/:id',
        name: 'PostDetail',
        component: PostDetail
      },
      // Các trang khác (nếu có)
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
  // 4. ADMIN DASHBOARD
  // URL: /admin/dashboard
  // =======================================================
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: AdminDashboard },
      { path: 'statistics', name: 'AdminStatistics', component: Statistics },
      { path: 'posts', name: 'AdminPosts', component: PostManagement },
      { path: 'categories', name: 'AdminCategories', component: CategoryManagement },
      { path: 'users', name: 'AdminUsers', component: UserManagement },
      { path: 'events', name: 'AdminEvents', component: EventManagement },
      { path: 'comments', name: 'AdminComments', component: CommentManagement },
      { path: 'reports', name: 'AdminReports', component: ReportManagement },
      { path: 'achievements', name: 'AdminAchievements', component: AchievementManagement },
      { path: 'notifications', name: 'AdminNotifications', component: NotificationManagement }
    ]
  },

  // =======================================================
  // 5. NOT FOUND (404)
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
  scrollBehavior(to, from, savedPosition) {
    // Luôn cuộn lên đầu trang khi chuyển route
    return { top: 0 }
  }
})

export default router