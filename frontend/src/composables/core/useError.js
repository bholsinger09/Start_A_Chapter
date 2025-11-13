/**
 * useError Composable - Centralized Error Handling
 * Provides consistent error management across the application
 */

import { ref, computed } from 'vue'
import { ERROR_MESSAGES } from '@/constants'

// Global error state (shared across all composables)
const globalError = ref(null)
const errorHistory = ref([])

/**
 * Error handling composable
 */
export function useError() {
    // Local error state (component-specific)
    const localError = ref(null)

    // Computed properties
    const error = computed(() => localError.value || globalError.value)
    const hasError = computed(() => error.value !== null)
    const errorMessage = computed(() => error.value?.message || error.value || null)
    const errorType = computed(() => error.value?.type || 'general')

    // Set error (local scope)
    const setError = (errorData) => {
        const errorObject = normalizeError(errorData)
        localError.value = errorObject

        // Log error for debugging
        console.error('Error set:', errorObject)

        // Add to history
        addToErrorHistory(errorObject)
    }

    // Set global error (affects entire app)
    const setGlobalError = (errorData) => {
        const errorObject = normalizeError(errorData)
        globalError.value = errorObject

        // Log global error
        console.error('Global error set:', errorObject)

        // Add to history
        addToErrorHistory(errorObject)
    }

    // Clear local error
    const clearError = () => {
        localError.value = null
    }

    // Clear global error
    const clearGlobalError = () => {
        globalError.value = null
    }

    // Clear all errors
    const clearAllErrors = () => {
        clearError()
        clearGlobalError()
    }

    // Normalize error to consistent format
    const normalizeError = (errorData) => {
        if (!errorData) return null

        // If it's already an error object with message
        if (typeof errorData === 'object' && errorData.message) {
            return {
                message: errorData.message,
                type: errorData.type || 'api',
                status: errorData.status || null,
                data: errorData.data || null,
                timestamp: new Date().toISOString(),
                id: generateErrorId()
            }
        }

        // If it's a string message
        if (typeof errorData === 'string') {
            return {
                message: errorData,
                type: 'general',
                status: null,
                data: null,
                timestamp: new Date().toISOString(),
                id: generateErrorId()
            }
        }

        // If it's an Error object
        if (errorData instanceof Error) {
            return {
                message: errorData.message,
                type: 'javascript',
                status: null,
                data: null,
                stack: errorData.stack,
                timestamp: new Date().toISOString(),
                id: generateErrorId()
            }
        }

        // Fallback for unknown error types
        return {
            message: 'An unknown error occurred',
            type: 'unknown',
            status: null,
            data: errorData,
            timestamp: new Date().toISOString(),
            id: generateErrorId()
        }
    }

    // Generate unique error ID
    const generateErrorId = () => {
        return Date.now().toString(36) + Math.random().toString(36).substr(2)
    }

    // Add error to history
    const addToErrorHistory = (errorObject) => {
        errorHistory.value.unshift(errorObject)

        // Keep only last 50 errors
        if (errorHistory.value.length > 50) {
            errorHistory.value = errorHistory.value.slice(0, 50)
        }
    }

    // Handle API errors specifically
    const handleApiError = (apiError) => {
        let errorMessage = ERROR_MESSAGES.SERVER_ERROR
        let errorType = 'api'

        if (apiError.status) {
            switch (apiError.status) {
                case 400:
                    errorMessage = apiError.data?.message || 'Invalid request'
                    errorType = 'validation'
                    break
                case 401:
                    errorMessage = ERROR_MESSAGES.SESSION_EXPIRED
                    errorType = 'authentication'
                    // Redirect to login or clear auth state
                    handleAuthenticationError()
                    break
                case 403:
                    errorMessage = ERROR_MESSAGES.UNAUTHORIZED
                    errorType = 'authorization'
                    break
                case 404:
                    errorMessage = ERROR_MESSAGES.NOT_FOUND
                    errorType = 'not_found'
                    break
                case 409:
                    errorMessage = apiError.data?.message || 'Conflict occurred'
                    errorType = 'conflict'
                    break
                case 422:
                    errorMessage = 'Validation failed'
                    errorType = 'validation'
                    break
                case 429:
                    errorMessage = 'Too many requests. Please slow down.'
                    errorType = 'rate_limit'
                    break
                case 500:
                    errorMessage = ERROR_MESSAGES.SERVER_ERROR
                    errorType = 'server'
                    break
                default:
                    errorMessage = apiError.message || ERROR_MESSAGES.SERVER_ERROR
            }
        } else if (apiError.code === 'ECONNABORTED') {
            errorMessage = ERROR_MESSAGES.TIMEOUT_ERROR
            errorType = 'timeout'
        } else if (!apiError.response) {
            errorMessage = ERROR_MESSAGES.NETWORK_ERROR
            errorType = 'network'
        }

        setError({
            message: errorMessage,
            type: errorType,
            status: apiError.status,
            data: apiError.data
        })
    }

    // Handle authentication errors
    const handleAuthenticationError = () => {
        // Clear stored authentication data
        localStorage.removeItem('user_token')
        localStorage.removeItem('user')

        // Redirect to login (if router is available)
        if (window.location.pathname !== '/login') {
            window.location.href = '/login'
        }
    }

    // Handle validation errors
    const handleValidationError = (validationErrors) => {
        if (typeof validationErrors === 'object') {
            const firstError = Object.values(validationErrors)[0]
            setError({
                message: firstError || 'Validation failed',
                type: 'validation',
                data: validationErrors
            })
        } else {
            setError({
                message: validationErrors || 'Validation failed',
                type: 'validation'
            })
        }
    }

    // Retry mechanism for failed operations
    const withRetry = async (operation, maxAttempts = 3, delay = 1000) => {
        let lastError

        for (let attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                clearError()
                return await operation()
            } catch (error) {
                lastError = error

                // Don't retry certain error types
                if (error.status >= 400 && error.status < 500 && error.status !== 408) {
                    throw error
                }

                // Don't retry on last attempt
                if (attempt === maxAttempts) {
                    throw error
                }

                // Wait before retrying
                await new Promise(resolve => setTimeout(resolve, delay * attempt))
            }
        }

        throw lastError
    }

    // Get user-friendly error message
    const getUserFriendlyMessage = (error) => {
        if (!error) return null

        switch (error.type) {
            case 'network':
                return 'Please check your internet connection and try again.'
            case 'timeout':
                return 'The request is taking longer than expected. Please try again.'
            case 'authentication':
                return 'Please log in to continue.'
            case 'authorization':
                return 'You don\'t have permission to perform this action.'
            case 'validation':
                return error.message
            case 'not_found':
                return 'The requested item could not be found.'
            case 'conflict':
                return 'This action conflicts with existing data.'
            case 'rate_limit':
                return 'You\'re making requests too quickly. Please slow down.'
            default:
                return error.message || 'Something went wrong. Please try again.'
        }
    }

    // Get error recovery suggestions
    const getRecoverySuggestions = (error) => {
        if (!error) return []

        switch (error.type) {
            case 'network':
                return [
                    'Check your internet connection',
                    'Try refreshing the page',
                    'Contact support if the problem persists'
                ]
            case 'timeout':
                return [
                    'Try again in a few moments',
                    'Check your connection speed',
                    'Refresh the page if the problem continues'
                ]
            case 'authentication':
                return [
                    'Log in with your credentials',
                    'Reset your password if needed',
                    'Contact support if you can\'t access your account'
                ]
            case 'authorization':
                return [
                    'Contact your administrator',
                    'Verify your role permissions',
                    'Try logging out and back in'
                ]
            case 'validation':
                return [
                    'Check the form for errors',
                    'Ensure all required fields are filled',
                    'Verify the data format is correct'
                ]
            case 'server':
                return [
                    'Try again in a few minutes',
                    'Contact support if the issue persists',
                    'Check the status page for known issues'
                ]
            default:
                return [
                    'Try refreshing the page',
                    'Contact support if needed'
                ]
        }
    }

    return {
        // State
        error,
        hasError,
        errorMessage,
        errorType,
        errorHistory,

        // Methods
        setError,
        setGlobalError,
        clearError,
        clearGlobalError,
        clearAllErrors,

        // Specialized handlers
        handleApiError,
        handleValidationError,

        // Utilities
        withRetry,
        getUserFriendlyMessage,
        getRecoverySuggestions
    }
}