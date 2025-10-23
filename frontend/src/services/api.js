import axios from 'axios'

// Get API base URL from environment variables with better production detection
const getApiBaseUrl = () => {
  // Check Vite environment variables first (production)
  if (import.meta?.env?.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL
  }

  // Check Vue CLI environment variables (fallback)
  if (process.env.VUE_APP_API_BASE_URL) {
    return process.env.VUE_APP_API_BASE_URL
  }

  // Production detection - if running on onrender.com, use production backend
  if (typeof window !== 'undefined' && window.location.hostname.includes('onrender.com')) {
    return 'https://chapter-organizer-backend.onrender.com'
  }

  // Development fallback
  return 'http://localhost:8080'
}

const API_BASE_URL = getApiBaseUrl()

// Log the resolved API base URL for debugging
console.log('API Configuration:', {
  baseUrl: API_BASE_URL,
  environment: import.meta?.env?.MODE || 'unknown',
  hostname: typeof window !== 'undefined' ? window.location.hostname : 'server'
})

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

// Export the API base URL function for use in other modules
export { getApiBaseUrl }

export default api