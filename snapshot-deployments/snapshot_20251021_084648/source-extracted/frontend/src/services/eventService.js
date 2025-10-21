import api from './api'

export const eventService = {
  // Get all events
  async getAllEvents() {
    try {
      const response = await api.get('/events')
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get event by ID
  async getEventById(id) {
    try {
      const response = await api.get(`/events/${id}`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get events by chapter
  async getEventsByChapter(chapterId) {
    try {
      const response = await api.get(`/events/chapter/${chapterId}`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get active events by chapter
  async getActiveEventsByChapter(chapterId) {
    try {
      const response = await api.get(`/events/chapter/${chapterId}/active`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get upcoming events by chapter
  async getUpcomingEventsByChapter(chapterId) {
    try {
      const response = await api.get(`/events/chapter/${chapterId}/upcoming`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get past events by chapter
  async getPastEventsByChapter(chapterId) {
    try {
      const response = await api.get(`/events/chapter/${chapterId}/past`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get events by type
  async getEventsByType(chapterId, type) {
    try {
      const response = await api.get(`/events/chapter/${chapterId}/type/${type}`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Create new event
  async createEvent(event) {
    try {
      const response = await api.post('/events', event)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Update event
  async updateEvent(id, event) {
    try {
      const response = await api.put(`/events/${id}`, event)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Delete event
  async deleteEvent(id) {
    try {
      await api.delete(`/events/${id}`)
      return true
    } catch (error) {
      throw error
    }
  },

  // Register for event
  async registerForEvent(id) {
    try {
      const response = await api.put(`/events/${id}/register`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Unregister from event
  async unregisterFromEvent(id) {
    try {
      const response = await api.put(`/events/${id}/unregister`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get upcoming event count
  async getUpcomingEventCount(chapterId) {
    try {
      const response = await api.get(`/events/chapter/${chapterId}/count/upcoming`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get event statistics
  async getEventStats(id) {
    try {
      const response = await api.get(`/events/${id}/stats`)
      return response.data
    } catch (error) {
      throw error
    }
  }
}