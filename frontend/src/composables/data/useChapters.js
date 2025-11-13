/**
 * useChapters Composable - Following Clean Code and Composition API principles
 * Provides reactive chapter data management and operations
 */

import { ref, computed, reactive, toRefs } from 'vue'
import { chapterService } from '@/services/api/ChapterService.js'
import { useError } from './useError.js'

/**
 * Chapters composable state and methods
 */
export function useChapters() {
    const state = reactive({
        chapters: [],
        currentChapter: null,
        loading: false,
        searchTerm: '',
        selectedState: '',
        sortBy: 'name',
        sortOrder: 'asc'
    })

    const { error, setError, clearError } = useError()

    const filteredChapters = computed(() => {
        let filtered = state.chapters

        // Apply search filter
        if (state.searchTerm) {
            filtered = filtered.filter(chapter =>
                chapter.matchesSearch(state.searchTerm)
            )
        }

        // Apply state filter
        if (state.selectedState) {
            filtered = filtered.filter(chapter =>
                chapter.isInState(state.selectedState)
            )
        }

        // Apply sorting
        filtered = sortChapters(filtered, state.sortBy, state.sortOrder)

        return filtered
    })

    const availableStates = computed(() => {
        const states = [...new Set(state.chapters.map(chapter => chapter.state))]
        return states.sort()
    })

    const chapterStatistics = computed(() => {
        if (state.chapters.length === 0) return null
        return chapterService.calculateStatistics(state.chapters)
    })

    const isLoading = computed(() => state.loading)
    const hasChapters = computed(() => state.chapters.length > 0)
    const hasError = computed(() => error.value !== null)

    // Methods
    const loadChapters = async (filters = {}) => {
        try {
            state.loading = true
            clearError()

            state.chapters = await chapterService.getAllChapters(filters)
        } catch (err) {
            setError(err.message || 'Failed to load chapters')
            console.error('Error loading chapters:', err)
        } finally {
            state.loading = false
        }
    }

    const loadChapterById = async (id) => {
        try {
            state.loading = true
            clearError()

            state.currentChapter = await chapterService.getChapterById(id)
            return state.currentChapter
        } catch (err) {
            setError(err.message || 'Failed to load chapter')
            console.error('Error loading chapter:', err)
            return null
        } finally {
            state.loading = false
        }
    }

    const createChapter = async (chapterData) => {
        try {
            state.loading = true
            clearError()

            const newChapter = await chapterService.createChapter(chapterData)

            // Add to local state
            state.chapters.push(newChapter)

            return { success: true, chapter: newChapter }
        } catch (err) {
            setError(err.message || 'Failed to create chapter')
            console.error('Error creating chapter:', err)
            return { success: false, error: err.message }
        } finally {
            state.loading = false
        }
    }

    const updateChapter = async (id, chapterData) => {
        try {
            state.loading = true
            clearError()

            const updatedChapter = await chapterService.updateChapter(id, chapterData)

            // Update local state
            const index = state.chapters.findIndex(c => c.id === id)
            if (index !== -1) {
                state.chapters[index] = updatedChapter
            }

            if (state.currentChapter?.id === id) {
                state.currentChapter = updatedChapter
            }

            return { success: true, chapter: updatedChapter }
        } catch (err) {
            setError(err.message || 'Failed to update chapter')
            console.error('Error updating chapter:', err)
            return { success: false, error: err.message }
        } finally {
            state.loading = false
        }
    }

    const deleteChapter = async (id) => {
        try {
            state.loading = true
            clearError()

            await chapterService.deleteChapter(id)

            // Remove from local state
            state.chapters = state.chapters.filter(c => c.id !== id)

            if (state.currentChapter?.id === id) {
                state.currentChapter = null
            }

            return { success: true }
        } catch (err) {
            setError(err.message || 'Failed to delete chapter')
            console.error('Error deleting chapter:', err)
            return { success: false, error: err.message }
        } finally {
            state.loading = false
        }
    }

    const searchChapters = async (searchCriteria) => {
        try {
            state.loading = true
            clearError()

            const results = await chapterService.searchChapters(searchCriteria)
            state.chapters = results

            return results
        } catch (err) {
            setError(err.message || 'Search failed')
            console.error('Error searching chapters:', err)
            return []
        } finally {
            state.loading = false
        }
    }

    const refreshChapters = () => {
        return loadChapters()
    }

    const setSearchTerm = (term) => {
        state.searchTerm = term
    }

    const setStateFilter = (stateValue) => {
        state.selectedState = stateValue
    }

    const setSorting = (sortBy, sortOrder = 'asc') => {
        state.sortBy = sortBy
        state.sortOrder = sortOrder
    }

    const clearFilters = () => {
        state.searchTerm = ''
        state.selectedState = ''
        state.sortBy = 'name'
        state.sortOrder = 'asc'
    }

    const resetState = () => {
        state.chapters = []
        state.currentChapter = null
        state.loading = false
        clearFilters()
        clearError()
    }

    // Utility functions
    const sortChapters = (chapters, sortBy, sortOrder) => {
        return [...chapters].sort((a, b) => {
            let valueA, valueB

            switch (sortBy) {
                case 'name':
                    valueA = a.name
                    valueB = b.name
                    break
                case 'universityName':
                    valueA = a.universityName
                    valueB = b.universityName
                    break
                case 'state':
                    valueA = a.state
                    valueB = b.state
                    break
                case 'memberCount':
                    valueA = a.memberCount
                    valueB = b.memberCount
                    break
                case 'createdAt':
                    valueA = new Date(a.createdAt || 0)
                    valueB = new Date(b.createdAt || 0)
                    break
                default:
                    valueA = a.name
                    valueB = b.name
            }

            // Handle string comparison
            if (typeof valueA === 'string' && typeof valueB === 'string') {
                valueA = valueA.toLowerCase()
                valueB = valueB.toLowerCase()
            }

            let comparison = 0
            if (valueA > valueB) comparison = 1
            if (valueA < valueB) comparison = -1

            return sortOrder === 'desc' ? -comparison : comparison
        })
    }

    const findChapterById = (id) => {
        return state.chapters.find(chapter => chapter.id === id)
    }

    const findChaptersByState = (stateValue) => {
        return state.chapters.filter(chapter => chapter.isInState(stateValue))
    }

    const getChaptersByUniversity = (universityName) => {
        return state.chapters.filter(chapter =>
            chapter.universityName.toLowerCase() === universityName.toLowerCase()
        )
    }

    // Return reactive state and methods
    return {
        // State
        ...toRefs(state),

        // Computed properties
        filteredChapters,
        availableStates,
        chapterStatistics,
        isLoading,
        hasChapters,
        hasError,
        error,

        // Methods
        loadChapters,
        loadChapterById,
        createChapter,
        updateChapter,
        deleteChapter,
        searchChapters,
        refreshChapters,

        // Filter methods
        setSearchTerm,
        setStateFilter,
        setSorting,
        clearFilters,
        resetState,

        // Utility methods
        findChapterById,
        findChaptersByState,
        getChaptersByUniversity,

        // Error handling
        clearError
    }
}