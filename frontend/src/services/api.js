import axios from 'axios'

// Get API base URL from environment variables
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// Create axios instance with base configuration
const api = axios.create({
  baseURL: `${API_BASE_URL}/api`,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30 second timeout
})

// Request interceptor for logging
api.interceptors.request.use(
  (config) => {
    console.log(`Making ${config.method?.toUpperCase()} request to ${config.url}`)
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // Log client errors (400-499) as info since they're often user validation errors
    // Log server errors (500+) as errors since they indicate system problems
    if (error.response?.status >= 400 && error.response?.status < 500) {
      console.info('API Client Error:', error.response?.data || error.message)
    } else {
      console.error('API Error:', error.response?.data || error.message)
    }
    return Promise.reject(error)
  }
)

export default api