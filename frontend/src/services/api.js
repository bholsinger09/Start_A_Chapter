import axios from 'axios'

// Get API base URL from environment or use current host
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ||
  (window.location.protocol + '//' + window.location.host)

// Create axios instance with base configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
})

// Request interceptor for debugging
api.interceptors.request.use(
  (config) => {
    console.log('API Request:', config.method?.toUpperCase(), config.url)
    return config
  },
  (error) => {
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    const status = error.response?.status
    const data = error.response?.data
    
    console.error('API Error:', status, data || error.message)
    
    // Enhance error with user-friendly messages
    if (status === 409 || status === 400) {
      // Handle registration conflicts and validation errors
      if (data && typeof data === 'object' && data.message) {
        error.userMessage = data.message
        error.message = data.message // Override axios message
        error.userFriendly = true
      } else if (data && typeof data === 'string') {
        try {
          const parsedData = JSON.parse(data)
          if (parsedData.message) {
            error.userMessage = parsedData.message
            error.message = parsedData.message
            error.userFriendly = true
          }
        } catch (parseError) {
          error.userMessage = data
          error.message = data
          error.userFriendly = true
        }
      }
    }
    
    return Promise.reject(error)
  }
)

export default api
export { API_BASE_URL }
