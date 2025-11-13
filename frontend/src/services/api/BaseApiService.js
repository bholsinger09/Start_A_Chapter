/**
 * Base API Service - Following Clean Architecture principles
 * Provides common functionality for all API services
 */

import axios from 'axios'
import { API_CONFIG, ERROR_MESSAGES } from '@/constants'

// Create axios instance with enhanced configuration
const createApiInstance = () => {
    const instance = axios.create({
        baseURL: import.meta.env.VITE_API_BASE_URL ||
            (window.location.protocol + '//' + window.location.host),
        timeout: API_CONFIG.TIMEOUT,
        headers: {
            'Content-Type': 'application/json',
        }
    })

    // Request interceptor for authentication and logging
    instance.interceptors.request.use(
        (config) => {
            // Add auth token if available
            const token = localStorage.getItem('user_token')
            if (token) {
                config.headers.Authorization = `Bearer ${token}`
            }

            // Log API requests in development
            if (import.meta.env.DEV) {
                console.log(`🚀 API Request: ${config.method?.toUpperCase()} ${config.url}`)
            }

            return config
        },
        (error) => {
            console.error('❌ Request Error:', error)
            return Promise.reject(error)
        }
    )

    // Response interceptor for error handling and logging
    instance.interceptors.response.use(
        (response) => {
            // Log successful responses in development
            if (import.meta.env.DEV) {
                console.log(`✅ API Response: ${response.status} ${response.config.url}`)
            }
            return response
        },
        (error) => {
            console.error('❌ API Error:', error.response?.status, error.response?.data || error.message)

            // Transform error to standardized format
            const apiError = transformApiError(error)
            return Promise.reject(apiError)
        }
    )

    return instance
}

/**
 * Transform axios errors to standardized API error format
 */
const transformApiError = (axiosError) => {
    const error = {
        message: ERROR_MESSAGES.SERVER_ERROR,
        status: axiosError.response?.status,
        data: axiosError.response?.data,
        originalError: axiosError
    }

    // Handle different error types
    if (axiosError.code === 'ECONNABORTED') {
        error.message = ERROR_MESSAGES.TIMEOUT_ERROR
    } else if (!axiosError.response) {
        error.message = ERROR_MESSAGES.NETWORK_ERROR
    } else {
        // Server responded with error status
        switch (axiosError.response.status) {
            case 401:
                error.message = ERROR_MESSAGES.SESSION_EXPIRED
                // Clear stored auth data
                localStorage.removeItem('user_token')
                localStorage.removeItem('user')
                break
            case 403:
                error.message = ERROR_MESSAGES.UNAUTHORIZED
                break
            case 404:
                error.message = ERROR_MESSAGES.NOT_FOUND
                break
            case 409:
                error.message = axiosError.response.data?.message || ERROR_MESSAGES.INVALID_DATA
                break
            case 422:
                error.message = 'Validation failed'
                error.validationErrors = axiosError.response.data?.errors
                break
            default:
                error.message = axiosError.response.data?.message || ERROR_MESSAGES.SERVER_ERROR
        }
    }

    return error
}

/**
 * Base API Service Class
 * Provides common CRUD operations and utilities
 */
export class BaseApiService {
    constructor() {
        this.api = createApiInstance()
    }

    /**
     * Perform GET request with error handling
     */
    async get(url, config = {}) {
        try {
            const response = await this.api.get(url, config)
            return this.handleSuccessResponse(response)
        } catch (error) {
            throw this.handleErrorResponse(error)
        }
    }

    /**
     * Perform POST request with error handling
     */
    async post(url, data = {}, config = {}) {
        try {
            const response = await this.api.post(url, data, config)
            return this.handleSuccessResponse(response)
        } catch (error) {
            throw this.handleErrorResponse(error)
        }
    }

    /**
     * Perform PUT request with error handling
     */
    async put(url, data = {}, config = {}) {
        try {
            const response = await this.api.put(url, data, config)
            return this.handleSuccessResponse(response)
        } catch (error) {
            throw this.handleErrorResponse(error)
        }
    }

    /**
     * Perform PATCH request with error handling
     */
    async patch(url, data = {}, config = {}) {
        try {
            const response = await this.api.patch(url, data, config)
            return this.handleSuccessResponse(response)
        } catch (error) {
            throw this.handleErrorResponse(error)
        }
    }

    /**
     * Perform DELETE request with error handling
     */
    async delete(url, config = {}) {
        try {
            const response = await this.api.delete(url, config)
            return this.handleSuccessResponse(response)
        } catch (error) {
            throw this.handleErrorResponse(error)
        }
    }

    /**
     * Handle successful API response
     */
    handleSuccessResponse(response) {
        return {
            data: response.data,
            status: response.status,
            headers: response.headers
        }
    }

    /**
     * Handle API error response
     */
    handleErrorResponse(error) {
        // Log error for debugging
        console.error('API Service Error:', error)

        // Return standardized error
        return error
    }

    /**
     * Build query string from parameters
     */
    buildQueryString(params) {
        const query = new URLSearchParams()

        Object.entries(params).forEach(([key, value]) => {
            if (value !== null && value !== undefined && value !== '') {
                query.append(key, value)
            }
        })

        return query.toString()
    }

    /**
     * Create URL with query parameters
     */
    buildUrl(baseUrl, params = {}) {
        const queryString = this.buildQueryString(params)
        return queryString ? `${baseUrl}?${queryString}` : baseUrl
    }

    /**
     * Retry failed requests with exponential backoff
     */
    async withRetry(operation, maxAttempts = API_CONFIG.RETRY_ATTEMPTS) {
        let lastError

        for (let attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return await operation()
            } catch (error) {
                lastError = error

                // Don't retry client errors (4xx) except 408 (timeout)
                if (error.status >= 400 && error.status < 500 && error.status !== 408) {
                    throw error
                }

                // Don't retry on last attempt
                if (attempt === maxAttempts) {
                    throw error
                }

                // Exponential backoff delay
                const delay = Math.min(1000 * Math.pow(2, attempt - 1), 10000)
                await this.sleep(delay)
            }
        }

        throw lastError
    }

    /**
     * Sleep utility for retry delays
     */
    sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms))
    }
}

// Export singleton instance
export const baseApiService = new BaseApiService()

// Export the axios instance for advanced use cases
export const apiClient = baseApiService.api