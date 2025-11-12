import { ref, computed } from 'vue'

// Global reactive state for authentication
const currentUser = ref(null)
const isAuthenticated = computed(() => currentUser.value !== null)

/**
 * Composable for authentication state management.
 * Centralizes user authentication logic across components.
 */
export function useAuth() {

    /**
     * Initialize authentication state from localStorage
     */
    const initAuth = () => {
        try {
            const storedUser = localStorage.getItem('user')
            if (storedUser) {
                currentUser.value = JSON.parse(storedUser)
            }
        } catch (error) {
            console.error('Error parsing stored user:', error)
            localStorage.removeItem('user') // Clean up invalid data
        }
    }

    /**
     * Set the current user and persist to localStorage
     */
    const setUser = (user) => {
        currentUser.value = user
        if (user) {
            localStorage.setItem('user', JSON.stringify(user))
        } else {
            localStorage.removeItem('user')
        }
    }

    /**
     * Log out the current user
     */
    const logout = () => {
        setUser(null)
    }

    /**
     * Update user profile information
     */
    const updateUserProfile = (updates) => {
        if (currentUser.value) {
            currentUser.value = { ...currentUser.value, ...updates }
            localStorage.setItem('user', JSON.stringify(currentUser.value))
        }
    }

    /**
     * Check if user has specific role
     */
    const hasRole = (role) => {
        return currentUser.value?.role === role
    }

    /**
     * Check if user is admin/president
     */
    const isAdmin = computed(() => {
        return hasRole('PRESIDENT') || hasRole('ADMIN')
    })

    return {
        // State
        currentUser: computed(() => currentUser.value),
        isAuthenticated,
        isAdmin,

        // Methods  
        initAuth,
        setUser,
        logout,
        updateUserProfile,
        hasRole
    }
}