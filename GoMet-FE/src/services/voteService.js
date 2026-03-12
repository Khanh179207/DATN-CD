import api from './api'; // Import instance axios của sếp

export const voteApi = {
  // Toggle Vote: Thêm hoặc hủy (Backend sếp đã xử lý logic 3 phiếu)
  toggle: (data) => api.post('/api/votes/toggle', data),
  
  // Lấy danh sách bài dự thi của sự kiện (Dùng API sếp vừa viết trong EventController)
  getEventPosts: (eventId) => api.get(`/api/events/${eventId}/posts`)
};