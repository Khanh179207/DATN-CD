import api from './api'
import { useAuthStore } from '@/stores/auth'

/**
 * Helper để lấy Token từ Store (Dùng cho Admin)
 */
const getAuthConfig = () => {
    const authStore = useAuthStore()
    return {
        headers: {
            'Authorization': `Bearer ${authStore.token}`
        }
    }
}

export const ticketService = {
    // 1. Lấy tất cả Ticket (Admin)
    getAllTickets: async () => {
        const response = await api.get('/api/admin/tickets', getAuthConfig())
        return response.data
    },

    // 2. Cập nhật trạng thái Ticket (Tiếp nhận/Hoàn thành/Từ chối)
    // 🔥 ĐÂY LÀ CHỖ FIX LỖI 400 CỦA SẾP
    updateTicketStatus: async (id, status, note = "") => {
        const url = `/api/admin/tickets/${id}/status?status=${status}`
        
        // Tham số 1: URL
        // Tham số 2: Body (Gửi note vào đây, kể cả note rỗng cũng phải gửi {})
        // Tham số 3: Config (Chứa Token)
        const response = await api.put(url, { note: note }, getAuthConfig())
        
        return response.data
    },

    // 3. Lấy Ticket theo trạng thái (0: Pending, 2: Resolved...)
    getTicketsByStatus: async (status) => {
        const response = await api.get(`/api/admin/tickets/status/${status}`, getAuthConfig())
        return response.data
    }
}

export default ticketService