import api from './api';

export const voteApi = {
  toggle: (data) => api.post('/api/votes/toggle', data),
  getEventPosts: (eventId) => api.get(`/api/events/${eventId}/posts`)
};