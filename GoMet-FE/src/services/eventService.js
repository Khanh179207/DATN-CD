import api from './api'

export const getEvents = () =>
  api.get('/api/events').then(r => r.data)

export const getEventById = (id) =>
  api.get(`/api/events/${id}`).then(r => r.data)

export const getActiveEvents = () =>
  api.get('/api/events/active').then(r => r.data)
