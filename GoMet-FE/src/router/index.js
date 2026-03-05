import { createRouter, createWebHistory } from 'vue-router'

// --- 1. IMPORT LAYOUTS ---
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import LandingLayout from '@/layouts/LandingLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

// --- 2. IMPORT VIEWS ---
import SecuritySettingsPage from '@/pages/profile/SecuritySettingsPage.vue'
import MyPostsPage from '@/pages/profile/MyPostsPage.vue'
import HomeView from '@/pages/home/HomeView.vue'
import SearchPage from '@/pages/search/SearchPage.vue'
import PostDetail from '@/pages/home/PostDetail.vue'
import CreatePost from '@/pages/CreatePost.vue'

// --- 3. IMPORT NEW PAGES (EDITORIAL LUXURY) ---
import ProfilePage from '@/pages/profile/ProfilePage.vue'
import EventList from '@/pages/events/EventList.vue'
import EventDetail from '@/pages/events/EventDetail.vue'
import ComparePage from '@/pages/compare/ComparePage.vue'

// 🚀 NEW PREMIUM FEATURES RECENTLY ADDED
import Leaderboard from '@/pages/Leaderboard.vue'
// Note: If the two files below haven't been created yet, create empty placeholder files in the pages folder to avoid import errors
import Suggestions from '@/pages/suggestions/SuggestionsPage.vue' 
import MealPlan from '@/pages/mealplan/MealPlanPage.vue'

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
import ModerationInbox from '@/pages/admin/ModerationInbox.vue'
import EmailJobsPage from '@/pages/admin/EmailJobsPage.vue'
import AuditLogPage from '@/pages/admin/AuditLogPage.vue'
import SystemMaintenancePage from '@/pages/admin/SystemMaintenancePage.vue'
import WeeklyCertificatesPage from '@/pages/admin/WeeklyCertificatesPage.vue'
import { useSystemSettingsStore } from '@/stores/systemSettings'

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

  // 2. MAIN APP (Default Layout) - CORE USER FEATURES
  {
    path: '/', 
    component: DefaultLayout,
    children: [
      { path: 'home', name: 'Home', component: HomeView },
      { path: 'search', name: 'Search', component: SearchPage },
      { path: 'post/:id', name: 'PostDetail', component: PostDetail, props: true },
      
      // ✅ Events Routes
      { path: 'events', name: 'Events', component: EventList },
      { path: 'events/:id', name: 'EventDetail', component: EventDetail },
      
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
        component: ProfilePage
      },
      {
        path: 'settings/security',
        name: 'SecuritySettings',
        component: SecuritySettingsPage,
        meta: { requiresAuth: true }
      },
      {
        path: 'my-posts',
        name: 'MyPosts',
        component: MyPostsPage,
        meta: { requiresAuth: true }
      },
      {
        path: 'profile/:id',
        name: 'ProfileById',
        component: ProfilePage
      },
      {
        path: 'storage',
        name: 'Storage',
        component: () => import('@/pages/storage/StoragePage.vue') // Lazy load for lighter bundle
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
        component: Leaderboard
      },
      {
        path: 'suggestions',
        name: 'Suggestions',
        component: Suggestions
      },
      {
        path: 'meal-plan',
        name: 'MealPlan',
        component: MealPlan
      },
      {
        path: 'premium',
        name: 'Premium',
        component: () => import('@/pages/PremiumPage.vue')
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
      { path: 'dashboard',     name: 'AdminDashboard',      component: AdminDashboard },
      { path: 'statistics',    name: 'AdminStatistics',     component: Statistics },
      { path: 'posts',         name: 'AdminPosts',          component: PostManagement },
      { path: 'categories',    name: 'AdminCategories',     component: CategoryManagement },
      { path: 'users',         name: 'AdminUsers',          component: UserManagement },
      { path: 'events',        name: 'AdminEvents',         component: EventManagement },
      { path: 'comments',      name: 'AdminComments',       component: CommentManagement },
      { path: 'reports',       name: 'AdminReports',        component: ReportManagement },
      { path: 'achievements',  name: 'AdminAchievements',   component: AchievementManagement },
      { path: 'notifications', name: 'AdminNotifications',  component: NotificationManagement },
      { path: 'moderation',    name: 'AdminModeration',     component: ModerationInbox },
      { path: 'email-jobs',    name: 'AdminEmailJobs',      component: EmailJobsPage },
      { path: 'audit-log',     name: 'AdminAuditLog',       component: AuditLogPage },
      { path: 'maintenance',   name: 'AdminMaintenance',    component: SystemMaintenancePage },
      { path: 'weekly-certs',  name: 'AdminWeeklyCerts',    component: WeeklyCertificatesPage }
    ]
  },

  {
    path: '/maintenance',
    name: 'MaintenancePage',
    component: () => import('@/pages/MaintenancePage.vue'),
    meta: { public: true }
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

  // 5. AUTH UTILITY PAGES (standalone, no layout)
  {
    path: '/auth/verify-login',
    name: 'DeviceVerification',
    component: () => import('@/pages/DeviceVerificationPage.vue'),
    meta: { public: true }
  },

  // 6. NOT FOUND (404 page)
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
router.beforeEach(async (to, from, next) => {
  const userStr = localStorage.getItem('user')
  let user = null
  if (userStr) {
    try {
      user = JSON.parse(userStr)
    } catch {
      user = null
      localStorage.removeItem('user')
    }
  }
  // Check both the dedicated key and the value stored inside the user object
  const accessToken = localStorage.getItem('accessToken') || user?.accessToken
  const isLoggedIn = !!(user?.token || accessToken)
  const isAdmin    = isLoggedIn && (user?.isAdmin === true || user?.isAdmin === 1 || user?.role === 'admin')
  const matchedRoutes = Array.isArray(to?.matched) ? to.matched : []

  const systemSettingsStore = useSystemSettingsStore()
  try {
    await systemSettingsStore.fetchPublicSettings()
    const maintenanceOn = !!systemSettingsStore.maintenanceMode
    const isMaintenancePage = to.path === '/maintenance'

    if (maintenanceOn && !isAdmin && !isMaintenancePage) {
      return next({ path: '/maintenance' })
    }
    if (!maintenanceOn && isMaintenancePage) {
      return next(isLoggedIn ? '/home' : '/')
    }
  } catch {
    // Fail-open on settings fetch errors to avoid blocking navigation on transient network failures.
  }

  // 1. Admin-only routes: must be logged in AND be an admin
  if (matchedRoutes.some(r => r.meta?.requiresAdmin)) {
    if (!isLoggedIn) {
      // Not logged in → go to home (landing), show a flash in query
      return next({ path: '/', query: { redirect: to.fullPath } })
    }
    if (!isAdmin) {
      // Logged in but not admin → redirect to main home
      return next({ path: '/home' })
    }
  }

  // 2. Auth-required routes
  if (matchedRoutes.some(r => r.meta?.requiresAuth)) {
    if (!isLoggedIn) {
      return next({ path: '/home', query: { login: '1' } })
    }
  }

  next()
})

export default router