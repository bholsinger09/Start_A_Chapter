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
    console.error('API Error:', error.response?.status, error.response?.data || error.message)
    return Promise.reject(error)
  }
)

export default api
export { API_BASE_URL }
