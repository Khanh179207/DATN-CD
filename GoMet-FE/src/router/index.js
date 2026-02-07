import { createRouter, createWebHistory } from 'vue-router'

// --- 1. IMPORT LAYOUTS ---
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import LandingLayout from '@/layouts/LandingLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

// --- 2. IMPORT VIEWS ---
import HomeView from '@/pages/home/HomeView.vue'
import SearchPage from '@/pages/search/SearchPage.vue'
import PostDetail from '@/pages/home/PostDetail.vue'
import CreatePost from '@/pages/CreatePost.vue'

// --- 3. IMPORT CÁC TRANG MỚI ---

import ProfilePage from '@/pages/profile/ProfilePage.vue'
import EventList from '@/pages/events/EventList.vue'
import EventDetail from '@/pages/events/EventDetail.vue'
import ComparePage from '@/pages/compare/ComparePage.vue'

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
  // 1. LANDING PAGE
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

  // 2. MAIN APP (Default Layout)
  {
    path: '/', 
    component: DefaultLayout,
    children: [
      { path: 'home', name: 'Home', component: HomeView },
      { path: 'search', name: 'Search', component: SearchPage },
      { path: 'post/:id', name: 'PostDetail', component: PostDetail, props: true },
      
      // ✅ Route Sự Kiện
      { path: 'events', name: 'Events', component: EventList },
      { path: 'events/:id', name: 'EventDetail', component: EventDetail },
      
      { 
        path: 'create-post', 
        name: 'CreatePost', 
        component: CreatePost 
      },

      // ✅ Route Trang cá nhân
      {
        path: 'profile',
        name: 'Profile',
        component: ProfilePage
      },

      // ✅ Route So Sánh (Đưa vào trong children của DefaultLayout)
      {
        path: 'compare',
        name: 'Compare',
        component: ComparePage
      }
    ]
  },

  // 3. ADMIN DASHBOARD
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

  // 4. NOT FOUND
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
    if (savedPosition) return savedPosition
    if (to.hash) return { el: to.hash, behavior: 'smooth', top: 80 }
    return { top: 0, behavior: 'smooth' }
  }
})

export default router