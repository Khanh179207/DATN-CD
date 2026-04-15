import { createRouter, createWebHistory } from 'vue-router'
import { toast } from '@/composables/useToast'
import i18n from '@/i18n'

// --- 1. LAYOUTS (Giữ DefaultLayout nạp trực tiếp để trang chủ hiện ngay lập tức) ---
import DefaultLayout from '@/layouts/DefaultLayout.vue'
const LandingLayout = () => import('@/layouts/LandingLayout.vue')
const AdminLayout = () => import('@/layouts/AdminLayout.vue')

// --- 2. CORE VIEWS (Lazy Loading để tối ưu tốc độ) ---
const HomeView = () => import('@/pages/home/HomeView.vue')
const SearchPage = () => import('@/pages/search/SearchPage.vue')
const PostDetail = () => import('@/pages/home/PostDetail.vue')
const CreatePost = () => import('@/pages/CreatePost.vue')

// --- 3. USER PAGES ---
const ProfilePage = () => import('@/pages/profile/ProfilePage.vue')
const EventList = () => import('@/pages/events/EventPage.vue')
const EventDetail = () => import('@/pages/events/EventDetail.vue')
const ComparePage = () => import('@/pages/compare/ComparePage.vue')
const VideoCall = () => import('@/pages/VideoCall.vue')

// --- 4. PREMIUM FEATURES ---
const Leaderboard = () => import('@/pages/Leaderboard.vue')
const Suggestions = () => import('@/pages/suggestions/SuggestionsPage.vue')
const MealPlan = () => import('@/pages/mealplan/MealPlanPage.vue')
const PaymentSuccess = () => import('@/pages/PaymentSuccess.vue')
const TermsAndPolicy = () => import('@/pages/terms/TermsAndPolicy.vue')

// --- 5. ADMIN PAGES (Gom nhóm vào một chunk tên là 'admin' để không làm chậm trang chủ) ---
const AdminDashboard = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/Dashboard.vue')
const Statistics = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/Statistics.vue')
const PostManagement = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/postadmin/PostManagement.vue')
const CategoryManagement = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/categoryadmin/CategoryManagement.vue')
const UserManagement = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/UserManagement.vue')
const CommentManagement = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/CommentManagement.vue')
const NotificationManagement = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/NotificationManagement.vue')
const TicketManagement = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/ticketadmin/TicketManagement.vue')
const EventManagement = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/eventadmin/EventManagement.vue')
const PostEventManagement = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/eventadmin/PostEventManagement.vue')
const AppealManagement = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/AppealManagement.vue')
const BlacklistManagement = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/BlacklistManagement.vue')
const TransactionManagement = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/TransactionManagement.vue')
const SystemLogs = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/SystemLogs.vue')
const SystemSettings = () => import(/* webpackChunkName: "admin" */ '@/pages/admin/SystemSettings.vue')

const routes = [
  // 1. LANDING PAGE
  {
    path: '/',
    component: LandingLayout,
    children: [
      { path: '', name: 'IntroPage', component: () => import('@/pages/intro/IntroPage.vue') },
      { path: 'terms-and-policy', name: 'TermsAndPolicyLanding', component: TermsAndPolicy }
    ]
  },

  // 2. MAIN APP
  {
    path: '/',
    component: DefaultLayout,
    children: [
      { path: 'home', name: 'Home', component: HomeView },
      { path: 'search', name: 'Search', component: SearchPage },
      {
        path: 'post/:id',
        name: 'PostDetail',
        component: PostDetail,
        props: true,
        meta: { requiresAuth: true }
      },
      { path: 'events', name: 'Events', component: EventList },
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
      {
        path: 'profile/:id?', // Dùng ? để id là optional, gộp chung trang Profile cá nhân và người khác
        name: 'Profile',
        component: ProfilePage,
        meta: { requiresAuth: true }
      },
      {
        path: 'storage',
        name: 'Storage',
        component: () => import('@/pages/storage/StoragePage.vue'),
        meta: { requiresAuth: true }
      },
      { path: 'compare', name: 'Compare', component: ComparePage },
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
      },
      {
        path: 'payment-success',
        name: 'PaymentSuccess',
        component: PaymentSuccess,
        meta: { requiresAuth: true }
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
      { path: 'notifications', name: 'AdminNotifications', component: NotificationManagement },
      { path: 'tickets', name: 'AdminTickets', component: TicketManagement },
      { path: 'events/:id/posts', name: 'AdminPostEventManagement', component: PostEventManagement },
      { path: 'appeals', name: 'AdminAppeals', component: AppealManagement },
      { path: 'blacklist', name: 'AdminBlacklist', component: BlacklistManagement },
      { path: 'transactions', name: 'AdminTransactions', component: TransactionManagement },
      { path: 'system-logs', name: 'AdminSystemLogs', component: SystemLogs },
      { path: 'system-settings', name: 'AdminSystemSettings', component: SystemSettings }
    ]
  },

  // 4. STANDALONE PAGES
  { path: '/verify-email', component: () => import('@/pages/VerifyEmailPage.vue') },
  { path: '/reset-password', component: () => import('@/pages/ResetPasswordPage.vue') },
  { 
    path: '/call/:roomID', 
    name: 'VideoCall', 
    component: VideoCall,
    meta: { requiresAuth: true } // Bắt buộc đăng nhập mới được gọi
  },
  // 5. NOT FOUND
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('@/pages/NotFound.vue') }
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
  
  // 🔥 FIXED: Tối ưu hóa kiểm tra quyền (Chấp nhận mọi format: 1, true, "true", "admin", "ADMIN")
  const isPremium = isLoggedIn && (
    String(user?.role).toLowerCase() === 'premium' || 
    ['true', '1', 1, true].includes(user?.isPremium)
  );

  const isAdmin = isLoggedIn && (
    String(user?.role).toLowerCase() === 'admin' || 
    ['true', '1', 1, true].includes(user?.isAdmin)
  );

// 1. Admin-only routes
  if (to.matched.some(r => r.meta?.requiresAdmin)) {
    if (!isLoggedIn) {
      toast.error(i18n.global.t('route_guard.login_for_page'))
      return next({ path: '/', query: { redirect: to.fullPath } })
    }
    if (!isAdmin) {
      toast.error(i18n.global.t('route_guard.no_access'))
      return next({ path: '/home' })
    }

    // 🔥 BỨC TƯỜNG LỬA SUPER ADMIN (Chỉ ID = 1 mới qua được)
    // Danh sách các trang tuyệt mật: Doanh thu, Nhật ký hệ thống, Cài đặt hệ thống
    const superAdminRoutes = ['AdminTransactions', 'AdminSystemLogs', 'AdminSystemSettings'];
    
    // Kiểm tra tên route hiện tại có nằm trong danh sách cấm không, và ID có phải là 1 không
    // (Dùng Number() để đề phòng trường hợp ID lưu dưới dạng chuỗi '1')
    if (superAdminRoutes.includes(to.name) && Number(user?.accountID) !== 1 && Number(user?.id) !== 1) {
      toast.error(i18n.global.t('route_guard.super_admin_only'));
      return next({ path: '/admin/dashboard' }); // Đá văng về trang Dashboard chung của Admin
    }
  }
  // 2. Auth-required routes
  if (to.matched.some(r => r.meta?.requiresAuth)) {
    if (to.name === 'PaymentSuccess') {
      return next(); // Ngoại lệ cho VNPAY Callback
    }
    if (!isLoggedIn) {
      toast.error(i18n.global.t('route_guard.login_for_detail'))
      return next({ path: '/home', query: { login: '1' } })
    }
  }

  // 3. Premium-only routes
  if (to.matched.some(r => r.meta?.requiresPremium)) {
    if (!isLoggedIn) {
      toast.warn(i18n.global.t('route_guard.login_for_premium'))
      return next({ path: '/home', query: { login: '1' } })
    }
    if (!isPremium && !isAdmin) { // Admin cũng được phép xem Premium
      toast.warn(i18n.global.t('route_guard.premium_only'))
      return next({ path: '/home' })
    }
  }

  next()
})

export default router