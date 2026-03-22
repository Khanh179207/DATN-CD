import { createRouter, createWebHistory } from 'vue-router'
import { toast } from '@/composables/useToast'
// --- 1. IMPORT LAYOUTS ---
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import LandingLayout from '@/layouts/LandingLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

// --- 2. IMPORT VIEWS ---
import HomeView from '@/pages/home/HomeView.vue'
import SearchPage from '@/pages/search/SearchPage.vue'
import PostDetail from '@/pages/home/PostDetail.vue'
import CreatePost from '@/pages/CreatePost.vue'

// --- 3. IMPORT NEW PAGES (EDITORIAL LUXURY) ---
import ProfilePage from '@/pages/profile/ProfilePage.vue'
import EventList from '@/pages/events/EventPage.vue'
import EventDetail from '@/pages/events/EventDetail.vue'
import ComparePage from '@/pages/compare/ComparePage.vue'

// 🚀 NEW PREMIUM FEATURES RECENTLY ADDED
import Leaderboard from '@/pages/Leaderboard.vue'
// Note: If the two files below haven't been created yet, create empty placeholder files in the pages folder to avoid import errors
import Suggestions from '@/pages/suggestions/SuggestionsPage.vue'
import MealPlan from '@/pages/mealplan/MealPlanPage.vue'
import TermsAndPolicy from '@/pages/terms/TermsAndPolicy.vue'

// --- ADMIN PAGES ---
import AdminDashboard from '@/pages/admin/Dashboard.vue'
import PostManagement from '@/pages/admin/postadmin/PostManagement.vue'
import CategoryManagement from '@/pages/admin/categoryadmin/CategoryManagement.vue'
import UserManagement from '@/pages/admin/UserManagement.vue'
import CommentManagement from '@/pages/admin/CommentManagement.vue'
import NotificationManagement from '@/pages/admin/NotificationManagement.vue'
import AchievementManagement from '@/pages/admin/AchievementManagement.vue'
import Statistics from '@/pages/admin/Statistics.vue'
import TicketManagement from '@/pages/admin/ticketadmin/TicketManagement.vue'
import EventManagement from '@/pages/admin/eventadmin/EventManagement.vue'
import PostEventManagement from '@/pages/admin/eventadmin/PostEventManagement.vue'
import AppealManagement from '@/pages/admin/AppealManagement.vue'
// 🔥 IMPORT TRANG BLACKLIST MỚI TẠO Ở ĐÂY
import BlacklistManagement from '@/pages/admin/BlacklistManagement.vue'

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
      },
      {
        path: 'terms-and-policy',
        name: 'TermsAndPolicyLanding',
        component: () => import('@/pages/terms/TermsAndPolicy.vue')
      }
    ]
  },

  // 2. MAIN APP (Default Layout) - CORE USER FEATURES
  {
    path: '/',
    component: DefaultLayout,
    children: [
      { path: 'home', name: 'Home', component: HomeView },
      { path: 'search', name: 'Search', component: SearchPage },
      
      // 🔒 KHÓA: Xem chi tiết bài viết phải đăng nhập
      { 
        path: 'post/:id', 
        name: 'PostDetail', 
        component: PostDetail, 
        props: true,
        meta: { requiresAuth: true } 
      },

      // ✅ Events Routes
      { path: 'events', name: 'Events', component: EventList }, // Danh sách sự kiện thì cho xem thoải mái
      
      // 🔒 KHÓA: Xem chi tiết sự kiện phải đăng nhập
      { 
        path: 'events/:id', 
        name: 'EventDetail', 
        component: EventDetail,
        meta: { requiresAuth: true } 
      },

      {
        path: 'create-post',
        name: 'CreatePost',
        component: CreatePost,
        meta: { requiresAuth: true }
      },

      // ✅ Profile & Storage Routes
      {
        path: 'profile',
        name: 'Profile',
        component: ProfilePage,
        meta: { requiresAuth: true } // Em khóa luôn trang cá nhân lại cho hợp logic
      },
      {
        path: 'profile/:id',
        name: 'ProfileById',
        component: ProfilePage,
        meta: { requiresAuth: true } // Khóa luôn xem trang cá nhân người khác
      },
      {
        path: 'storage',
        name: 'Storage',
        component: () => import('@/pages/storage/StoragePage.vue'),
        meta: { requiresAuth: true } // Khóa luôn Kho lưu trữ
      },

      // ✅ Compare Routes
      {
        path: 'compare',
        name: 'Compare',
        component: ComparePage
      },

      // ✨✨✨ NEW PREMIUM ROUTES (SYNCED WITH SIDEBAR) ✨✨✨
      {
        path: 'leaderboard',
        name: 'Leaderboard',
        component: Leaderboard,
        meta: { requiresPremium: true, isDark: true }
      },
      {
        path: 'suggestions',
        name: 'Suggestions',
        component: Suggestions,
        meta: { requiresPremium: true, isDark: true }
      },
      {
        path: 'meal-plan',
        name: 'MealPlan',
        component: MealPlan,
        meta: { requiresPremium: true, isDark: true }
      }
    ]
  },

  // 3. ADMIN DASHBOARD
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    meta: { requiresAdmin: true },
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: AdminDashboard },
      { path: 'statistics', name: 'AdminStatistics', component: Statistics },
      { path: 'posts', name: 'AdminPosts', component: PostManagement },
      { path: 'categories', name: 'AdminCategories', component: CategoryManagement },
      { path: 'users', name: 'AdminUsers', component: UserManagement },
      { path: 'events', name: 'AdminEvents', component: EventManagement },
      { path: 'comments', name: 'AdminComments', component: CommentManagement },
      { path: 'achievements', name: 'AdminAchievements', component: AchievementManagement },
      { path: 'notifications', name: 'AdminNotifications', component: NotificationManagement },
      { path: 'tickets', name: 'AdminTickets', component: TicketManagement },
      { path: 'events/:id/posts', name: 'AdminPostEventManagement', component: PostEventManagement },
      { path: 'appeals', name: 'AdminAppeals', component: AppealManagement },
      // 🔥 ĐĂNG KÝ TRANG TỪ KHÓA CẤM VÀO ĐÂY
      { path: 'blacklist', name: 'AdminBlacklist', component: BlacklistManagement }
    ]
  },

  // 4. STANDALONE PAGES (no layout wrapper)
  {
    path: '/verify-email',
    name: 'VerifyEmail',
    component: () => import('@/pages/VerifyEmailPage.vue')
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/pages/ForgotPasswordPage.vue')
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/pages/ResetPasswordPage.vue')
  },

  // 5. NOT FOUND (404 page)
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

// ─── NAVIGATION GUARDS ────────────────────────────────────────────────────────
router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null
  const isLoggedIn = !!user?.token

  const isPremium = isLoggedIn && (
    String(user?.isPremium) === "true" || 
    String(user?.isPremium) === "1" || 
    user?.role === 'premium'
  )
  
  const isAdmin = isLoggedIn && (
    String(user?.isAdmin) === "true" || 
    String(user?.isAdmin) === "1" || 
    user?.role === 'admin'
  )

  // 1. Admin-only routes: must be logged in AND be an admin
  if (to.matched.some(r => r.meta?.requiresAdmin)) {
    if (!isLoggedIn) {
      toast.error('Vui lòng đăng nhập để truy cập trang này')
      return next({ path: '/', query: { redirect: to.fullPath } })
    }
    if (!isAdmin) {
      toast.error('Bạn không có quyền truy cập trang này')
      return next({ path: '/home' })
    }
  }

  // 2. Auth-required routes (Khóa các trang yêu cầu đăng nhập)
  if (to.matched.some(r => r.meta?.requiresAuth)) {
    if (!isLoggedIn) {
      toast.error('Vui lòng đăng nhập để xem chi tiết')
      return next({ path: '/home', query: { login: '1' } }) 
    }
  }

  // 3. Premium-only routes
  if (to.matched.some(r => r.meta?.requiresPremium)) {
    if (!isLoggedIn) {
      toast.warn('Vui lòng đăng nhập để sử dụng tính năng Premium')
      return next({ path: '/home', query: { login: '1' } })
    }

    if (!isPremium && !isAdmin) { 
      toast.warn('Tính năng này chỉ dành cho Premium users')
      return next({ path: '/home' })
    }
  }

  next()
})

export default router