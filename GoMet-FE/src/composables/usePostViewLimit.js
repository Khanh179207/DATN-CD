import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

export function usePostViewLimit() {
  const authStore = useAuthStore()
  
  // Lấy data từ user trong store. Nếu không có User thì mặc định là vô hạn (hoặc xử lý ở login)
  const remainingViews = computed(() => {
    const val = authStore.user?.remainingPostViews
    if (val === undefined || val === null || isNaN(val)) return 3 // Dự phòng an toàn
    return Number(val)
  })
  const isPremium = computed(() => authStore.user && (String(authStore.user.isPremium) === "1" || authStore.user.role === 'premium'));
  const isAdmin = computed(() => authStore.user?.role === 'admin')

  // Kiểm tra xem có được xem bài viết không
  const canViewPost = computed(() => {
    if (isAdmin.value || isPremium.value) return true
    return remainingViews.value > 0
  })

  // Hàm kiểm tra và trừ lượt xem
  function checkAndChargeView(postId, authorId) {
    if (isAdmin.value || isPremium.value) return true // Admin/VIP luôn OK

    const user = authStore.user
    if (!user) return true // Chưa login thì có thể xử lý khác, nhưng ở đây giả định đã login

    // Đảm bảo kiểu dữ liệu là số
    if (user.remainingPostViews === undefined || user.remainingPostViews === null || isNaN(user.remainingPostViews)) {
      user.remainingPostViews = 3
    }
    user.remainingPostViews = Number(user.remainingPostViews)

    // 1. Nếu là bài chính mình viết -> Không mất lượt
    if (String(user.id) === String(authorId) || String(user.accountID) === String(authorId)) {
      return true
    }

    // 2. Nếu bài này đã xem trong ngày hôm nay -> Không mất lượt
    const viewedIds = user.viewedPostIds || []
    if (viewedIds.includes(postId)) {
      return true
    }

    // 3. Nếu hết lượt -> Trả về false để UI xử lý redirect
    if (user.remainingPostViews <= 0) {
      return false
    }

    // 4. Trừ lượt và lưu vào danh sách đã xem
    const now = new Date();
    const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
    
    user.remainingPostViews--
    if (!user.viewedPostIds) user.viewedPostIds = []
    user.viewedPostIds.push(postId)
    user.lastViewDate = today
    
    // Cập nhật localStorage cho phiên hiện tại
    localStorage.setItem('user', JSON.stringify(user))

    // Cập nhật Persistent Cache (không bị xóa khi logout)
    const limitsCache = JSON.parse(localStorage.getItem('gomet_view_limits') || '{}');
    const uid = String(user.accountID || user.id);
    limitsCache[uid] = {
      count: user.remainingPostViews,
      ids: user.viewedPostIds,
      date: user.lastViewDate
    };
    localStorage.setItem('gomet_view_limits', JSON.stringify(limitsCache));

    return true
  }

  return {
    remainingViews,
    isPremium,
    isAdmin,
    canViewPost,
    checkAndChargeView
  }
}
