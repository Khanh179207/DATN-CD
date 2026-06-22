import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api' 

// Biến global để cache trạng thái ngày lễ cho toàn ứng dụng trong 1 phiên làm việc
const isHolidayEventActive = ref(null) 

export function usePostViewLimit() {
  const authStore = useAuthStore()

  // 1. Hàm kiểm tra chế độ Ngày lễ từ Backend (ĐÃ TỐI ƯU)
  async function checkGlobalHolidayStatus() {
    // Nếu đã check rồi thì trả về kết quả cũ luôn cho nhanh
    if (isHolidayEventActive.value !== null) return isHolidayEventActive.value
    
    try {
      // 🔥 GỌI TRỰC TIẾP API CHUYÊN BIỆT CHÚNG TA VỪA TẠO Ở BACKEND
      const res = await api.get('/api/system-config/holiday-status')
      
      // Backend đã tính toán hết logic công tắc & thời gian, chỉ cần lấy kết quả
      isHolidayEventActive.value = res.data.isHolidayActive === true;
      
      return isHolidayEventActive.value
    } catch (e) {
      console.warn("Lỗi check ngày lễ từ server, dùng mặc định FALSE", e);
      isHolidayEventActive.value = false;
      return false
    }
  }

  // 2. Số lượt xem còn lại 
  const remainingViews = computed(() => {
    if (isHolidayEventActive.value === true) return 99 
    
    const val = authStore.user?.remainingPostViews
    if (val === undefined || val === null || isNaN(val)) return 3 
    return Number(val)
  })

  const isPremium = computed(() => authStore.user && (String(authStore.user.isPremium) === "1" || authStore.user.role === 'premium'));
  const isAdmin = computed(() => authStore.user?.role === 'admin')

  const canViewPost = computed(() => {
    if (isHolidayEventActive.value === true) return true 
    if (isAdmin.value || isPremium.value) return true
    return remainingViews.value > 0
  })

  // 3. Hàm kiểm tra và trừ lượt xem
  async function checkAndChargeView(postId, authorId) {
    // Kiểm tra Ngày lễ trước tiên
    const holidayStatus = await checkGlobalHolidayStatus()
    if (holidayStatus === true) {
      console.log("🎊 Sự kiện đang diễn ra: Mọi bài viết đều MIỄN PHÍ!");
      return true 
    }

    // Logic bình thường (Admin, VIP, Chính mình...)
    if (isAdmin.value || isPremium.value) return true 

    const user = authStore.user
    if (!user) return true 

    if (user.remainingPostViews === undefined || user.remainingPostViews === null || isNaN(user.remainingPostViews)) {
      user.remainingPostViews = 3
    }
    user.remainingPostViews = Number(user.remainingPostViews)

    // Nếu là bài chính mình viết -> Không mất lượt
    if (String(user.id) === String(authorId) || String(user.accountID) === String(authorId)) {
      return true
    }

    // Nếu bài này đã xem trong ngày hôm nay -> Không mất lượt
    const viewedIds = user.viewedPostIds || []
    if (viewedIds.includes(postId)) {
      return true
    }

    // Nếu hết lượt
    if (user.remainingPostViews <= 0) {
      return false
    }

    // Trừ lượt và lưu
    const now = new Date();
    const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
    
    user.remainingPostViews--
    if (!user.viewedPostIds) user.viewedPostIds = []
    user.viewedPostIds.push(postId)
    user.lastViewDate = today
    
    localStorage.setItem('user', JSON.stringify(user))

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
    checkAndChargeView,
    isHolidayEventActive, 
    checkGlobalHolidayStatus 
  }
}