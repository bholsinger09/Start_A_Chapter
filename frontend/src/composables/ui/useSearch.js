/**
 * useSearch Composable - Reusable Search and Filtering Logic
 * Following Clean Code principles for DRY and separation of concerns
 */

import { ref, computed, reactive, watch } from 'vue'
import { UI_CONFIG } from '@/constants'

/**
 * Search composable for flexible searching and filtering
 */
export function useSearch(options = {}) {
    // Configuration with defaults
    const config = {
        debounceDelay: options.debounceDelay || UI_CONFIG.DEBOUNCE_DELAY,
        minSearchLength: options.minSearchLength || UI_CONFIG.MIN_SEARCH_LENGTH,
        caseSensitive: options.caseSensitive || false,
        exactMatch: options.exactMatch || false,
        searchFields: options.searchFields || ['name'], // Default search fields
        ...options
    }

    // Reactive state
    const state = reactive({
        searchTerm: '',
        debouncedSearchTerm: '',
        filters: {},
        sortBy: config.defaultSortBy || '',
        sortOrder: config.defaultSortOrder || 'asc',
        isSearching: false
    })

    // Refs for debouncing
    const debounceTimer = ref(null)

    // Computed properties
    const hasSearchTerm = computed(() =>
        state.debouncedSearchTerm.length >= config.minSearchLength
    )

    const hasFilters = computed(() =>
        Object.keys(state.filters).some(key => state.filters[key] !== null && state.filters[key] !== '')
    )

    const hasActiveSearch = computed(() =>
        hasSearchTerm.value || hasFilters.value
    )

    const isValidSearch = computed(() =>
        state.searchTerm.length === 0 || state.searchTerm.length >= config.minSearchLength
    )

    // Watchers
    watch(() => state.searchTerm, (newTerm) => {
        // Clear existing timer
        if (debounceTimer.value) {
            clearTimeout(debounceTimer.value)
        }

        // Set new timer for debounced search
        debounceTimer.value = setTimeout(() => {
            state.debouncedSearchTerm = newTerm
            state.isSearching = false
        }, config.debounceDelay)

        // Set searching state immediately for UI feedback
        if (newTerm !== state.debouncedSearchTerm) {
            state.isSearching = newTerm.length >= config.minSearchLength
        }
    })

    // Methods
    const setSearchTerm = (term) => {
        state.searchTerm = term || ''
    }

    const clearSearch = () => {
        state.searchTerm = ''
        state.debouncedSearchTerm = ''
        state.isSearching = false

        if (debounceTimer.value) {
            clearTimeout(debounceTimer.value)
            debounceTimer.value = null
        }
    }

    const setFilter = (key, value) => {
        state.filters[key] = value
    }

    const removeFilter = (key) => {
        delete state.filters[key]
    }

    const clearFilters = () => {
        state.filters = {}
    }

    const clearAll = () => {
        clearSearch()
        clearFilters()
        resetSorting()
    }

    const setSorting = (sortBy, sortOrder = 'asc') => {
        state.sortBy = sortBy
        state.sortOrder = sortOrder
    }

    const toggleSortOrder = () => {
        state.sortOrder = state.sortOrder === 'asc' ? 'desc' : 'asc'
    }

    const resetSorting = () => {
        state.sortBy = config.defaultSortBy || ''
        state.sortOrder = config.defaultSortOrder || 'asc'
    }

    // Core search function
    const searchItems = (items, searchTerm = state.debouncedSearchTerm) => {
        if (!searchTerm || searchTerm.length < config.minSearchLength) {
            return items
        }

        const term = config.caseSensitive ? searchTerm : searchTerm.toLowerCase()

        return items.filter(item => {
            // If item has a custom search method, use it
            if (typeof item.matchesSearch === 'function') {
                return item.matchesSearch(searchTerm)
            }

            // Search across configured fields
            return config.searchFields.some(field => {
                const value = getNestedValue(item, field)
                if (value === null || value === undefined) return false

                const searchableValue = config.caseSensitive ?
                    String(value) : String(value).toLowerCase()

                return config.exactMatch ?
                    searchableValue === term :
                    searchableValue.includes(term)
            })
        })
    }

    // Filter function
    const filterItems = (items, filters = state.filters) => {
        if (!hasFilters.value && Object.keys(filters).length === 0) {
            return items
        }

        return items.filter(item => {
            return Object.entries(filters).every(([key, value]) => {
                if (value === null || value === undefined || value === '') {
                    return true // Skip empty filters
                }

                const itemValue = getNestedValue(item, key)

                // Handle different filter types
                if (Array.isArray(value)) {
                    return value.includes(itemValue)
                }

                if (typeof value === 'boolean') {
                    return Boolean(itemValue) === value
                }

                if (typeof value === 'string') {
                    return String(itemValue).toLowerCase().includes(value.toLowerCase())
                }

                return itemValue === value
            })
        })
    }

    // Sort function
    const sortItems = (items, sortBy = state.sortBy, sortOrder = state.sortOrder) => {
        if (!sortBy) return items

        return [...items].sort((a, b) => {
            let valueA = getNestedValue(a, sortBy)
            let valueB = getNestedValue(b, sortBy)

            // Handle null/undefined values
            if (valueA === null || valueA === undefined) valueA = ''
            if (valueB === null || valueB === undefined) valueB = ''

            // Handle different data types
            if (typeof valueA === 'string' && typeof valueB === 'string') {
                valueA = valueA.toLowerCase()
                valueB = valueB.toLowerCase()
            }

            if (typeof valueA === 'number' && typeof valueB === 'number') {
                return sortOrder === 'desc' ? valueB - valueA : valueA - valueB
            }

            if (valueA instanceof Date && valueB instanceof Date) {
                return sortOrder === 'desc' ? valueB - valueA : valueA - valueB
            }

            // String comparison
            let comparison = 0
            if (valueA > valueB) comparison = 1
            if (valueA < valueB) comparison = -1

            return sortOrder === 'desc' ? -comparison : comparison
        })
    }

    // Combined search, filter, and sort
    const processItems = (items) => {
        let processed = items

        // Apply search
        if (hasSearchTerm.value) {
            processed = searchItems(processed)
        }

        // Apply filters
        if (hasFilters.value) {
            processed = filterItems(processed)
        }

        // Apply sorting
        if (state.sortBy) {
            processed = sortItems(processed)
        }

        return processed
    }

    // Utility function to get nested object values
    const getNestedValue = (obj, path) => {
        return path.split('.').reduce((current, key) => {
            return current && current[key] !== undefined ? current[key] : null
        }, obj)
    }

    // Advanced search with custom function
    const advancedSearch = (items, searchFunction) => {
        if (typeof searchFunction !== 'function') {
            console.warn('advancedSearch requires a function')
            return items
        }

        return items.filter(searchFunction)
    }

    // Highlight matches in text (for UI)
    const highlightMatches = (text, searchTerm = state.debouncedSearchTerm) => {
        if (!searchTerm || !text) return text

        const term = config.caseSensitive ? searchTerm : searchTerm.toLowerCase()
        const textToSearch = config.caseSensitive ? text : text.toLowerCase()

        if (!textToSearch.includes(term)) return text

        const regex = new RegExp(`(${escapeRegExp(searchTerm)})`, config.caseSensitive ? 'g' : 'gi')
        return text.replace(regex, '<mark>$1</mark>')
    }

    // Escape special regex characters
    const escapeRegExp = (string) => {
        return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    }

    // Get search statistics
    const getSearchStats = (originalCount, filteredCount) => {
        return {
            total: originalCount,
            filtered: filteredCount,
            hidden: originalCount - filteredCount,
            percentage: originalCount > 0 ? Math.round((filteredCount / originalCount) * 100) : 0,
            hasResults: filteredCount > 0,
            isFiltered: originalCount !== filteredCount
        }
    }

    // Export search configuration for external use
    const getConfig = () => ({ ...config })

    // Cleanup function
    const cleanup = () => {
        if (debounceTimer.value) {
            clearTimeout(debounceTimer.value)
        }
    }

    return {
        // State (reactive)
        searchTerm: computed({
            get: () => state.searchTerm,
            set: setSearchTerm
        }),
        debouncedSearchTerm: computed(() => state.debouncedSearchTerm),
        filters: computed(() => state.filters),
        sortBy: computed(() => state.sortBy),
        sortOrder: computed(() => state.sortOrder),
        isSearching: computed(() => state.isSearching),

        // Computed properties
        hasSearchTerm,
        hasFilters,
        hasActiveSearch,
        isValidSearch,

        // Methods
        setSearchTerm,
        clearSearch,
        setFilter,
        removeFilter,
        clearFilters,
        clearAll,
        setSorting,
        toggleSortOrder,
        resetSorting,

        // Processing methods
        searchItems,
        filterItems,
        sortItems,
        processItems,
        advancedSearch,

        // Utility methods
        highlightMatches,
        getSearchStats,
        getConfig,
        cleanup
    }
}