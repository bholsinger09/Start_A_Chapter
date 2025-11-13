/**
 * Application Constants - Following Clean Code principles
 * Centralized location for all magic numbers and strings
 */

// API Configuration
export const API_CONFIG = {
    TIMEOUT: 10000,
    RETRY_ATTEMPTS: 3,
    DEFAULT_PAGE_SIZE: 20,
}

// User Roles - Single source of truth
export const USER_ROLES = {
    PRESIDENT: 'PRESIDENT',
    VICE_PRESIDENT: 'VICE_PRESIDENT',
    TREASURER: 'TREASURER',
    SECRETARY: 'SECRETARY',
    MEMBER: 'MEMBER',
    ADMIN: 'ADMIN'
}

// Role Display Names
export const ROLE_LABELS = {
    [USER_ROLES.PRESIDENT]: 'President',
    [USER_ROLES.VICE_PRESIDENT]: 'Vice President',
    [USER_ROLES.TREASURER]: 'Treasurer',
    [USER_ROLES.SECRETARY]: 'Secretary',
    [USER_ROLES.MEMBER]: 'Member',
    [USER_ROLES.ADMIN]: 'Administrator'
}

// Leadership roles for authorization
export const LEADERSHIP_ROLES = [
    USER_ROLES.PRESIDENT,
    USER_ROLES.VICE_PRESIDENT,
    USER_ROLES.TREASURER,
    USER_ROLES.SECRETARY,
    USER_ROLES.ADMIN
]

// API Endpoints - Centralized and typed
export const API_ENDPOINTS = {
    // Authentication
    AUTH: {
        LOGIN: '/api/auth/login',
        REGISTER: '/api/auth/register',
        LOGOUT: '/api/auth/logout',
        PROFILE: '/api/auth/profile'
    },

    // Chapters
    CHAPTERS: {
        LIST: '/api/chapters',
        CREATE: '/api/chapters',
        BY_ID: (id) => `/api/chapters/${id}`,
        MEMBERS: (id) => `/api/chapters/${id}/members`,
        EVENTS: (id) => `/api/chapters/${id}/events`
    },

    // Members  
    MEMBERS: {
        LIST: '/api/members',
        CREATE: '/api/members',
        BY_ID: (id) => `/api/members/${id}`,
        BY_EMAIL: (email) => `/api/members/email/${email}`,
        SEARCH: '/api/members/search'
    },

    // Events
    EVENTS: {
        LIST: '/api/events',
        CREATE: '/api/events',
        BY_ID: (id) => `/api/events/${id}`,
        REGISTER: (id) => `/api/events/${id}/register`
    }
}

// UI Constants
export const UI_CONFIG = {
    // Animation timing
    ANIMATION_DURATION: 300,
    DEBOUNCE_DELAY: 300,

    // Pagination
    DEFAULT_PAGE_SIZE: 10,
    PAGE_SIZE_OPTIONS: [10, 25, 50, 100],

    // Search
    MIN_SEARCH_LENGTH: 2,
    MAX_SEARCH_LENGTH: 100,

    // Form validation
    MIN_PASSWORD_LENGTH: 8,
    MAX_NAME_LENGTH: 50,
    MAX_EMAIL_LENGTH: 100
}

// Bootstrap CSS Classes - Reusable utilities
export const CSS_CLASSES = {
    // Loading states
    LOADING_SPINNER: 'spinner-border text-primary',
    LOADING_CONTAINER: 'text-center py-5',

    // Cards and containers
    CARD: 'card',
    CARD_HOVER: 'card h-100 shadow-sm border-0',
    CONTAINER_FLUID: 'container-fluid',

    // Buttons
    BTN_PRIMARY: 'btn btn-primary',
    BTN_SECONDARY: 'btn btn-outline-secondary',
    BTN_SUCCESS: 'btn btn-success',
    BTN_DANGER: 'btn btn-danger',
    BTN_SM: 'btn btn-sm',

    // Form elements
    FORM_CONTROL: 'form-control',
    FORM_SELECT: 'form-select',
    INPUT_GROUP: 'input-group',
    INPUT_GROUP_TEXT: 'input-group-text',

    // Status badges
    BADGE_SUCCESS: 'badge bg-success',
    BADGE_WARNING: 'badge bg-warning',
    BADGE_DANGER: 'badge bg-danger',
    BADGE_PRIMARY: 'badge bg-primary',

    // Text utilities
    TEXT_MUTED: 'text-muted',
    TEXT_SUCCESS: 'text-success',
    TEXT_DANGER: 'text-danger',
    TEXT_PRIMARY: 'text-primary'
}

// Error Messages - Centralized for consistency
export const ERROR_MESSAGES = {
    // Network errors
    NETWORK_ERROR: 'Unable to connect to the server. Please check your connection.',
    TIMEOUT_ERROR: 'Request timed out. Please try again.',
    SERVER_ERROR: 'Server error occurred. Please try again later.',

    // Authentication errors
    INVALID_CREDENTIALS: 'Invalid email or password.',
    SESSION_EXPIRED: 'Your session has expired. Please log in again.',
    UNAUTHORIZED: 'You are not authorized to perform this action.',

    // Validation errors
    REQUIRED_FIELD: 'This field is required.',
    INVALID_EMAIL: 'Please enter a valid email address.',
    PASSWORD_TOO_SHORT: `Password must be at least ${UI_CONFIG.MIN_PASSWORD_LENGTH} characters.`,
    NAME_TOO_LONG: `Name cannot exceed ${UI_CONFIG.MAX_NAME_LENGTH} characters.`,

    // Data errors
    NOT_FOUND: 'The requested item was not found.',
    DUPLICATE_EMAIL: 'An account with this email already exists.',
    INVALID_DATA: 'The provided data is invalid.'
}

// Success Messages
export const SUCCESS_MESSAGES = {
    LOGIN_SUCCESS: 'Welcome back!',
    REGISTRATION_SUCCESS: 'Account created successfully!',
    UPDATE_SUCCESS: 'Changes saved successfully!',
    DELETE_SUCCESS: 'Item deleted successfully!',
    CREATE_SUCCESS: 'Item created successfully!'
}

// Application Metadata
export const APP_CONFIG = {
    NAME: 'Campus Chapter Organizer',
    VERSION: '1.0.0',
    DESCRIPTION: 'Organize and manage student chapters across universities',
    AUTHOR: 'Campus Chapter Team',
    REPOSITORY: 'https://github.com/bholsinger09/Start_A_Chapter'
}

// Feature Flags - For controlling feature rollout
export const FEATURE_FLAGS = {
    ENABLE_BLOG: true,
    ENABLE_EVENTS: true,
    ENABLE_NOTIFICATIONS: false,
    ENABLE_ANALYTICS: false,
    ENABLE_DARK_MODE: false
}

// Date and Time Formats
export const DATE_FORMATS = {
    DISPLAY: 'MMM DD, YYYY',
    INPUT: 'YYYY-MM-DD',
    DATETIME: 'MMM DD, YYYY h:mm A',
    TIME: 'h:mm A',
    ISO: 'YYYY-MM-DDTHH:mm:ss.sssZ'
}

// Storage Keys - For localStorage/sessionStorage
export const STORAGE_KEYS = {
    USER_TOKEN: 'user_token',
    USER_DATA: 'user',
    THEME_PREFERENCE: 'theme',
    LANGUAGE_PREFERENCE: 'language',
    LAST_CHAPTER_FILTER: 'last_chapter_filter'
}

// Export utility functions for constants
export const CONSTANTS_UTILS = {
    /**
     * Get role display name
     */
    getRoleLabel: (role) => ROLE_LABELS[role] || role,

    /**
     * Check if role is leadership
     */
    isLeadershipRole: (role) => LEADERSHIP_ROLES.includes(role),

    /**
     * Get all roles as options for select inputs
     */
    getRoleOptions: () => Object.entries(ROLE_LABELS).map(([value, label]) => ({
        value,
        label
    })),

    /**
     * Format API endpoint with parameters
     */
    formatEndpoint: (endpoint, params = {}) => {
        let formatted = endpoint
        Object.entries(params).forEach(([key, value]) => {
            formatted = formatted.replace(`{${key}}`, value)
        })
        return formatted
    }
}