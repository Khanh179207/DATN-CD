<template>
  <div class="page-container animate-enter">
    
    <div class="page-header">
      <div class="title-group">
        <h2 class="title">Quản Lý Người Dùng</h2>
        <p class="subtitle">Tổng số: <span class="badge">{{ users.length }}</span> tài khoản trên hệ thống</p>
      </div>
      <div class="header-actions">
        <button class="btn-refresh" @click="fetchUsers">
          <i class="fa-solid fa-rotate-right" :class="{ 'fa-spin': isLoading }"></i> Làm mới
        </button>
      </div>
    </div>

    <div class="table-wrapper glass-panel">
      <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <span>Đang tải dữ liệu từ máy chủ...</span>
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Người dùng</th>
            <th>Email</th>
            <th>Vai trò</th>
            <th>Trạng thái</th>
            <th class="text-right">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.accountID" class="table-row">
            <td class="col-id">#{{ user.accountID }}</td>
            <td>
              <div class="user-cell">
                <img :src="user.avatar || `https://ui-avatars.com/api/?name=${user.username}&background=random`" class="avatar" loading="lazy">
                <div class="u-info">
                  <span class="name">{{ user.username }}</span>
                  <span class="sub-name">{{ user.fullName || 'Chưa cập nhật tên' }}</span>
                </div>
              </div>
            </td>
            <td class="col-email">{{ user.email }}</td>
            <td>
              <div class="role-wrapper">
                <select 
                  v-model="user.role" 
                  @change="updateUserRole(user)"
                  class="role-select"
                  :class="user.role === 'ADMIN' ? 'role-admin' : 'role-user'"
                >
                  <option value="USER">Thành viên</option>
                  <option value="ADMIN">Quản trị viên</option>
                </select>
              </div>
            </td>
            <td>
              <div class="status-pill" :class="user.isActive ? 'active' : 'banned'">
                <span class="status-dot"></span>
                {{ user.isActive ? 'Hoạt động' : 'Đã khóa' }}
              </div>
            </td>
            <td>
              <div class="actions">
                <button 
                  @click="toggleStatus(user)" 
                  class="btn-action-icon" 
                  :class="user.isActive ? 'btn-warn' : 'btn-success'"
                  :title="user.isActive ? 'Khóa tài khoản' : 'Mở khóa tài khoản'"
                >
                  <i :class="user.isActive ? 'fa-solid fa-lock' : 'fa-solid fa-unlock'"></i>
                </button>
                <button 
                  @click="deleteUser(user.accountID)" 
                  class="btn-action-icon btn-danger"
                  title="Xóa vĩnh viễn"
                >
                  <i class="fa-solid fa-trash-can"></i>
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="users.length === 0">
            <td colspan="6" class="empty-state">
              <i class="fa-regular fa-folder-open"></i>
              <p>Chưa có tài khoản nào trên hệ thống.</p>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios' // Nhớ cài đặt: npm install axios

// States
const users = ref([])
const isLoading = ref(true)

// --- 1. LẤY DANH SÁCH USER TỪ BACKEND (GET) ---
const fetchUsers = async () => {
  isLoading.value = true
  try {
    const response = await axios.get('/api/admin/accounts')
    
    // SỬA Ở ĐÂY: Chuẩn hóa dữ liệu role về IN HOA để khớp với <option>
    users.value = response.data.map(user => {
      return {
        ...user,
        // Ép kiểu chữ hoa. Nếu DB trả null thì mặc định là 'USER'
        role: user.role ? String(user.role).toUpperCase() : 'USER' 
      }
    })
    
  } catch (error) {
    console.error('Lỗi khi tải danh sách người dùng:', error)
    alert('Không thể kết nối đến máy chủ. Vui lòng thử lại!')
  } finally {
    isLoading.value = false
  }
}

// --- 2. CẬP NHẬT VAI TRÒ (PUT) ---
const updateUserRole = async (user) => {
  try {
    // Gửi request update lên Backend
    await axios.put(`/api/admin/accounts/${user.accountID}`, user)
    // Sếp có thể dùng thư viện Toastify ở đây thay vì alert cho chuyên nghiệp
    console.log(`Đã cập nhật quyền của ${user.username} thành ${user.role}`)
  } catch (error) {
    console.error('Lỗi khi cập nhật quyền:', error)
    alert('Cập nhật thất bại!')
    fetchUsers() // Lỗi thì gọi lại data cũ để reset giao diện
  }
}

// --- 3. KHÓA/MỞ KHÓA TÀI KHOẢN (PUT - Cập nhật trạng thái) ---
const toggleStatus = async (user) => {
  const actionText = user.isActive ? 'khóa' : 'mở khóa'
  if (confirm(`Bạn có chắc muốn ${actionText} tài khoản ${user.username}?`)) {
    try {
      // Đảo ngược trạng thái
      const updatedUser = { ...user, isActive: !user.isActive }
      
      // Gọi API update
      await axios.put(`/api/admin/accounts/${user.accountID}`, updatedUser)
      
      // Cập nhật lại UI nếu API thành công
      user.isActive = !user.isActive
    } catch (error) {
      console.error(`Lỗi khi ${actionText} tài khoản:`, error)
      alert(`Không thể ${actionText} tài khoản này.`)
    }
  }
}

// --- 4. XÓA TÀI KHOẢN (DELETE) ---
const deleteUser = async (id) => {
  if (confirm('⚠️ HÀNH ĐỘNG NGUY HIỂM: Bạn có chắc chắn muốn xóa vĩnh viễn tài khoản này? Hành động này không thể hoàn tác!')) {
    try {
      // Gọi API xóa cứng (Dựa theo Controller Sếp gửi)
      await axios.delete(`/api/admin/accounts/hard/${id}`)
      
      // Lọc bỏ user vừa xóa khỏi giao diện
      users.value = users.value.filter(u => u.accountID !== id)
    } catch (error) {
      console.error('Lỗi khi xóa tài khoản:', error)
      alert('Không thể xóa tài khoản. Có thể tài khoản này đang dính dữ liệu ràng buộc (Bài viết, Bình luận...).')
    }
  }
}

// Chạy hàm lấy dữ liệu ngay khi mở trang
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Mulish:wght@400;600;700;800&display=swap');

.page-container { padding: 30px 40px; font-family: 'Mulish', sans-serif; color: #1E293B; }

/* HEADER */
.page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 25px; }
.title { font-size: 1.8rem; font-weight: 800; color: #0F172A; margin: 0 0 5px 0; letter-spacing: -0.5px; }
.subtitle { font-size: 0.9rem; color: #64748B; margin: 0; }
.badge { background: #EA580C; color: white; padding: 2px 8px; border-radius: 6px; font-weight: 800; font-size: 0.8rem; }
.btn-refresh { background: white; border: 1px solid #E2E8F0; padding: 8px 16px; border-radius: 10px; font-weight: 700; color: #475569; cursor: pointer; transition: 0.2s; display: flex; gap: 8px; align-items: center; box-shadow: 0 2px 5px rgba(0,0,0,0.02); }
.btn-refresh:hover { background: #F8FAFC; color: #EA580C; border-color: #CBD5E1; }

/* TABLE WRAPPER (Glassmorphism) */
.glass-panel {
  background: rgba(255, 255, 255, 0.7); backdrop-filter: blur(20px);
  border-radius: 16px; border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 10px 30px rgba(0,0,0,0.03); overflow: hidden;
}

/* DATA TABLE */
.data-table { width: 100%; border-collapse: collapse; font-size: 0.9rem; }
.data-table th { background: rgba(248, 250, 252, 0.8); padding: 15px 20px; text-align: left; font-weight: 700; color: #64748B; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 1px solid #E2E8F0; }
.data-table td { padding: 15px 20px; border-bottom: 1px solid #F1F5F9; vertical-align: middle; }
.text-right { text-align: right !important; }

/* Hover Row Effect */
.table-row { transition: background 0.2s; }
.table-row:hover { background: rgba(255, 247, 237, 0.4); }

.col-id { font-weight: 700; color: #94A3B8; font-size: 0.8rem; }
.col-email { color: #475569; font-weight: 500; }

/* User Info */
.user-cell { display: flex; align-items: center; gap: 12px; }
.avatar { width: 42px; height: 42px; border-radius: 50%; object-fit: cover; border: 2px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.u-info { display: flex; flex-direction: column; }
.u-info .name { font-weight: 800; color: #0F172A; font-size: 0.95rem; }
.u-info .sub-name { font-size: 0.75rem; color: #94A3B8; }

/* Select Role */
.role-wrapper { position: relative; width: max-content; }
.role-select { 
  appearance: none; background: white; border: 1px solid #E2E8F0; 
  padding: 6px 30px 6px 12px; border-radius: 8px; font-weight: 700; font-size: 0.8rem; 
  cursor: pointer; transition: 0.2s; font-family: 'Mulish', sans-serif;
}
.role-select:focus { outline: none; border-color: #EA580C; box-shadow: 0 0 0 3px rgba(234, 88, 12, 0.1); }
/* Custom chevron cho select */
.role-wrapper::after { content: '▼'; font-size: 0.6rem; position: absolute; right: 10px; top: 50%; transform: translateY(-50%); color: #94A3B8; pointer-events: none; }
.role-admin { color: #EA580C; background: #FFF7ED; border-color: #FFEDD5; }
.role-user { color: #3B82F6; }

/* Status Pill */
.status-pill { display: inline-flex; align-items: center; gap: 6px; padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; }
.status-dot { width: 6px; height: 6px; border-radius: 50%; }
.status-pill.active { background: #F0FDF4; color: #16A34A; border: 1px solid #DCFCE7; }
.status-pill.active .status-dot { background: #22C55E; box-shadow: 0 0 5px #22C55E; }
.status-pill.banned { background: #FEF2F2; color: #DC2626; border: 1px solid #FEE2E2; }
.status-pill.banned .status-dot { background: #EF4444; }

/* Action Buttons */
.actions { display: flex; gap: 8px; justify-content: flex-end; }
.btn-action-icon { width: 34px; height: 34px; border-radius: 8px; border: 1px solid transparent; display: flex; align-items: center; justify-content: center; font-size: 0.9rem; cursor: pointer; transition: 0.2s; background: white; }
.btn-warn { color: #F59E0B; border-color: #FEF3C7; }
.btn-warn:hover { background: #FEF3C7; transform: translateY(-2px); }
.btn-success { color: #10B981; border-color: #D1FAE5; }
.btn-success:hover { background: #D1FAE5; transform: translateY(-2px); }
.btn-danger { color: #EF4444; border-color: #FEE2E2; }
.btn-danger:hover { background: #EF4444; color: white; transform: translateY(-2px); }

/* Loading & Empty State */
.loading-state { padding: 50px; display: flex; flex-direction: column; align-items: center; gap: 15px; color: #94A3B8; font-weight: 600; }
.spinner { width: 30px; height: 30px; border: 3px solid #E2E8F0; border-top-color: #EA580C; border-radius: 50%; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state { text-align: center; padding: 50px !important; color: #94A3B8; }
.empty-state i { font-size: 3rem; margin-bottom: 10px; opacity: 0.5; }

/* ANIMATION */
.animate-enter { animation: fadeIn 0.5s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>