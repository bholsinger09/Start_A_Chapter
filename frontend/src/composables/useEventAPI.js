import { ref } from 'vue'
import axios from 'axios'

// API base URL - adjust based on your backend configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// Create axios instance with default config
const apiClient = axios.create({
  baseURL: `${API_BASE_URL}/api`,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
})

// Add request interceptor for auth (if needed)
apiClient.interceptors.request.use(
  (config) => {
    // Add auth token if available
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Add response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle common errors
    if (error.response?.status === 401) {
      // Handle unauthorized - redirect to login
      localStorage.removeItem('authToken')
      window.location.href = '/login'
    }

    return Promise.reject(error)
  }
)

export function useEventAPI() {
  const isLoading = ref(false)
  const error = ref(null)

  // Helper function to handle API calls
  const handleApiCall = async (apiCall) => {
    try {
      isLoading.value = true
      error.value = null

      const response = await apiCall()
      return response
    } catch (err) {
      error.value = err.response?.data?.message || err.message || 'An error occurred'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Event RSVP API Methods
  const fetchEventRSVPs = async (eventId, params = {}) => {
    return handleApiCall(() =>
      apiClient.get(`/events/${eventId}/rsvps`, { params })
    )
  }

  const createRSVP = async (eventId, rsvpData) => {
    return handleApiCall(() =>
      apiClient.post(`/events/${eventId}/rsvps`, rsvpData)
    )
  }

  const updateRSVP = async (eventId, userId, rsvpData) => {
    return handleApiCall(() =>
      apiClient.put(`/events/${eventId}/rsvps/${userId}`, rsvpData)
    )
  }

  const deleteRSVP = async (eventId, userId) => {
    return handleApiCall(() =>
      apiClient.delete(`/events/${eventId}/rsvps/${userId}`)
    )
  }

  const getRSVPStatus = async (eventId, userId) => {
    return handleApiCall(() =>
      apiClient.get(`/events/${eventId}/rsvps/${userId}`)
    )
  }

  // Event API Methods
  const fetchEvents = async (params = {}) => {
    return handleApiCall(() =>
      apiClient.get('/events', { params })
    )
  }

  const fetchEvent = async (eventId) => {
    return handleApiCall(() =>
      apiClient.get(`/events/${eventId}`)
    )
  }

  const createEvent = async (eventData) => {
    return handleApiCall(() =>
      apiClient.post('/events', eventData)
    )
  }

  const updateEvent = async (eventId, eventData) => {
    return handleApiCall(() =>
      apiClient.put(`/events/${eventId}`, eventData)
    )
  }

  const deleteEvent = async (eventId) => {
    return handleApiCall(() =>
      apiClient.delete(`/events/${eventId}`)
    )
  }

  // Recurring Events API Methods
  const fetchRecurringEvents = async (params = {}) => {
    return handleApiCall(() =>
      apiClient.get('/events/recurring', { params })
    )
  }

  const createRecurringEvent = async (recurringEventData) => {
    return handleApiCall(() =>
      apiClient.post('/events/recurring', recurringEventData)
    )
  }

  const updateRecurringEvent = async (recurringEventId, recurringEventData) => {
    return handleApiCall(() =>
      apiClient.put(`/events/recurring/${recurringEventId}`, recurringEventData)
    )
  }

  const deleteRecurringEvent = async (recurringEventId) => {
    return handleApiCall(() =>
      apiClient.delete(`/events/recurring/${recurringEventId}`)
    )
  }

  // Event Search and Filtering
  const searchEvents = async (query, filters = {}) => {
    const params = {
      q: query,
      ...filters
    }

    return handleApiCall(() =>
      apiClient.get('/events/search', { params })
    )
  }

  // Event Analytics
  const getEventAnalytics = async (eventId) => {
    return handleApiCall(() =>
      apiClient.get(`/events/${eventId}/analytics`)
    )
  }

  const getEventsAnalytics = async (params = {}) => {
    return handleApiCall(() =>
      apiClient.get('/events/analytics', { params })
    )
  }

  // Member API Methods
  const fetchMembers = async (params = {}) => {
    return handleApiCall(() =>
      apiClient.get('/members', { params })
    )
  }

  const fetchMember = async (memberId) => {
    return handleApiCall(() =>
      apiClient.get(`/members/${memberId}`)
    )
  }

  // Chapter API Methods
  const fetchChapters = async (params = {}) => {
    return handleApiCall(() =>
      apiClient.get('/chapters', { params })
    )
  }

  const fetchChapter = async (chapterId) => {
    return handleApiCall(() =>
      apiClient.get(`/chapters/${chapterId}`)
    )
  }

  // Monitoring API Methods
  const getSystemHealth = async () => {
    return handleApiCall(() =>
      apiClient.get('/monitoring/health')
    )
  }

  const getMetrics = async (params = {}) => {
    return handleApiCall(() =>
      apiClient.get('/monitoring/metrics', { params })
    )
  }

  const getOperationalMetrics = async (params = {}) => {
    return handleApiCall(() =>
      apiClient.get('/monitoring/operational', { params })
    )
  }

  const getAuditLogs = async (params = {}) => {
    return handleApiCall(() =>
      apiClient.get('/monitoring/audit', { params })
    )
  }

  // Utility Methods
  const uploadFile = async (file, type = 'general') => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)

    return handleApiCall(() =>
      apiClient.post('/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
    )
  }

  const downloadReport = async (reportType, params = {}) => {
    return handleApiCall(() =>
      apiClient.get(`/reports/${reportType}`, {
        params,
        responseType: 'blob'
      })
    )
  }

  // Notification Methods
  const sendNotification = async (notificationData) => {
    return handleApiCall(() =>
      apiClient.post('/notifications', notificationData)
    )
  }

  const markNotificationRead = async (notificationId) => {
    return handleApiCall(() =>
      apiClient.put(`/notifications/${notificationId}/read`)
    )
  }

  return {
    // State
    isLoading,
    error,

    // RSVP Methods
    fetchEventRSVPs,
    createRSVP,
    updateRSVP,
    deleteRSVP,
    getRSVPStatus,

    // Event Methods
    fetchEvents,
    fetchEvent,
    createEvent,
    updateEvent,
    deleteEvent,
    searchEvents,
    getEventAnalytics,
    getEventsAnalytics,

    // Recurring Event Methods
    fetchRecurringEvents,
    createRecurringEvent,
    updateRecurringEvent,
    deleteRecurringEvent,

    // Member Methods
    fetchMembers,
    fetchMember,

    // Chapter Methods
    fetchChapters,
    fetchChapter,

    // Monitoring Methods
    getSystemHealth,
    getMetrics,
    getOperationalMetrics,
    getAuditLogs,

    // Utility Methods
    uploadFile,
    downloadReport,
    sendNotification,
    markNotificationRead,

    // Raw API client for custom calls
    apiClient
  }
}