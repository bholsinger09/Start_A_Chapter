/**
 * Chapter Service - Following Clean Architecture and Single Responsibility Principle
 * Handles all chapter-related API operations and business logic
 */

import { BaseApiService } from './BaseApiService.js'
import { API_ENDPOINTS } from '@/constants'

/**
 * Chapter Data Transfer Objects
 */
export class ChapterDTO {
    constructor(data = {}) {
        this.id = data.id || null
        this.name = data.name || ''
        this.universityName = data.universityName || ''
        this.city = data.city || ''
        this.state = data.state || ''
        this.description = data.description || ''
        this.active = data.active !== undefined ? data.active : true
        this.members = data.members || []
        this.events = data.events || []
        this.createdAt = data.createdAt || null
        this.updatedAt = data.updatedAt || null
    }

    // Business logic methods for chapter data
    get fullLocation() {
        return `${this.city}, ${this.state}`
    }

    get memberCount() {
        return this.members?.length || 0
    }

    get eventCount() {
        return this.events?.length || 0
    }

    get foundedYear() {
        if (!this.createdAt) return null
        return new Date(this.createdAt).getFullYear()
    }

    get isActive() {
        return this.active === true
    }

    // Helper methods
    hasMembers() {
        return this.memberCount > 0
    }

    hasEvents() {
        return this.eventCount > 0
    }

    isInState(state) {
        return this.state?.toLowerCase() === state?.toLowerCase()
    }

    matchesSearch(searchTerm) {
        if (!searchTerm) return true

        const term = searchTerm.toLowerCase()
        return this.name.toLowerCase().includes(term) ||
            this.universityName.toLowerCase().includes(term) ||
            this.city.toLowerCase().includes(term) ||
            this.state.toLowerCase().includes(term)
    }
}

/**
 * Chapter Service Class
 * Encapsulates all chapter-related business operations
 */
export class ChapterService extends BaseApiService {

    /**
     * Fetch all chapters with optional filtering
     */
    async getAllChapters(filters = {}) {
        const url = this.buildUrl(API_ENDPOINTS.CHAPTERS.LIST, filters)
        const response = await this.get(url)

        // Transform raw data to DTOs
        return response.data.map(chapterData => new ChapterDTO(chapterData))
    }

    /**
     * Fetch chapter by ID with full details
     */
    async getChapterById(id) {
        const response = await this.get(API_ENDPOINTS.CHAPTERS.BY_ID(id))
        return new ChapterDTO(response.data)
    }

    /**
     * Create new chapter
     */
    async createChapter(chapterData) {
        const response = await this.post(API_ENDPOINTS.CHAPTERS.CREATE, chapterData)
        return new ChapterDTO(response.data)
    }

    /**
     * Update existing chapter
     */
    async updateChapter(id, chapterData) {
        const response = await this.put(API_ENDPOINTS.CHAPTERS.BY_ID(id), chapterData)
        return new ChapterDTO(response.data)
    }

    /**
     * Delete chapter
     */
    async deleteChapter(id) {
        await this.delete(API_ENDPOINTS.CHAPTERS.BY_ID(id))
        return { success: true, id }
    }

    /**
     * Get chapter members
     */
    async getChapterMembers(chapterId) {
        const response = await this.get(API_ENDPOINTS.CHAPTERS.MEMBERS(chapterId))
        return response.data
    }

    /**
     * Get chapter events
     */
    async getChapterEvents(chapterId) {
        const response = await this.get(API_ENDPOINTS.CHAPTERS.EVENTS(chapterId))
        return response.data
    }

    /**
     * Search chapters with advanced filtering
     */
    async searchChapters(searchCriteria) {
        const {
            searchTerm = '',
            state = '',
            active = null,
            minMembers = null,
            maxMembers = null,
            sortBy = 'name',
            sortOrder = 'asc',
            page = 1,
            limit = 20
        } = searchCriteria

        const params = {
            search: searchTerm,
            state,
            sortBy,
            sortOrder,
            page,
            limit
        }

        // Add optional filters
        if (active !== null) params.active = active
        if (minMembers !== null) params.minMembers = minMembers
        if (maxMembers !== null) params.maxMembers = maxMembers

        const chapters = await this.getAllChapters(params)

        // Apply client-side filtering for complex criteria
        return this.applyAdvancedFiltering(chapters, searchCriteria)
    }

    /**
     * Apply client-side filtering for complex search criteria
     */
    applyAdvancedFiltering(chapters, criteria) {
        let filtered = chapters

        // Text search across multiple fields
        if (criteria.searchTerm) {
            filtered = filtered.filter(chapter => chapter.matchesSearch(criteria.searchTerm))
        }

        // State filtering
        if (criteria.state) {
            filtered = filtered.filter(chapter => chapter.isInState(criteria.state))
        }

        // Member count filtering
        if (criteria.minMembers !== null) {
            filtered = filtered.filter(chapter => chapter.memberCount >= criteria.minMembers)
        }
        if (criteria.maxMembers !== null) {
            filtered = filtered.filter(chapter => chapter.memberCount <= criteria.maxMembers)
        }

        // Active status filtering
        if (criteria.active !== null) {
            filtered = filtered.filter(chapter => chapter.active === criteria.active)
        }

        return filtered
    }

    /**
     * Get chapter statistics
     */
    async getChapterStatistics() {
        const chapters = await this.getAllChapters()

        return this.calculateStatistics(chapters)
    }

    /**
     * Calculate comprehensive chapter statistics
     */
    calculateStatistics(chapters) {
        const stats = {
            totalChapters: chapters.length,
            activeChapters: chapters.filter(c => c.isActive).length,
            totalMembers: chapters.reduce((sum, c) => sum + c.memberCount, 0),
            totalEvents: chapters.reduce((sum, c) => sum + c.eventCount, 0),
            statesRepresented: [...new Set(chapters.map(c => c.state))].length,
            universitiesCount: [...new Set(chapters.map(c => c.universityName))].length,
            averageMembersPerChapter: 0,
            chaptersWithEvents: chapters.filter(c => c.hasEvents()).length,
            newestChapter: null,
            oldestChapter: null,
            stateDistribution: {},
            membershipDistribution: {}
        }

        // Calculate averages
        if (stats.totalChapters > 0) {
            stats.averageMembersPerChapter = Math.round(stats.totalMembers / stats.totalChapters * 10) / 10
        }

        // Find newest and oldest chapters
        const sortedByDate = chapters
            .filter(c => c.createdAt)
            .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))

        if (sortedByDate.length > 0) {
            stats.newestChapter = sortedByDate[0]
            stats.oldestChapter = sortedByDate[sortedByDate.length - 1]
        }

        // State distribution
        chapters.forEach(chapter => {
            stats.stateDistribution[chapter.state] = (stats.stateDistribution[chapter.state] || 0) + 1
        })

        // Membership distribution
        chapters.forEach(chapter => {
            const range = this.getMembershipRange(chapter.memberCount)
            stats.membershipDistribution[range] = (stats.membershipDistribution[range] || 0) + 1
        })

        return stats
    }

    /**
     * Get membership range category for statistics
     */
    getMembershipRange(memberCount) {
        if (memberCount === 0) return '0 members'
        if (memberCount <= 5) return '1-5 members'
        if (memberCount <= 10) return '6-10 members'
        if (memberCount <= 20) return '11-20 members'
        if (memberCount <= 50) return '21-50 members'
        return '50+ members'
    }

    /**
     * Validate chapter data before submission
     */
    validateChapterData(chapterData) {
        const errors = {}

        // Required field validation
        if (!chapterData.name?.trim()) {
            errors.name = 'Chapter name is required'
        }

        if (!chapterData.universityName?.trim()) {
            errors.universityName = 'University name is required'
        }

        if (!chapterData.city?.trim()) {
            errors.city = 'City is required'
        }

        if (!chapterData.state?.trim()) {
            errors.state = 'State is required'
        }

        // Length validation
        if (chapterData.name?.length > 100) {
            errors.name = 'Chapter name cannot exceed 100 characters'
        }

        if (chapterData.description?.length > 500) {
            errors.description = 'Description cannot exceed 500 characters'
        }

        return {
            isValid: Object.keys(errors).length === 0,
            errors
        }
    }

    /**
     * Get available states from chapters
     */
    async getAvailableStates() {
        const chapters = await this.getAllChapters()
        const states = [...new Set(chapters.map(chapter => chapter.state))]
        return states.sort()
    }

    /**
     * Get available universities from chapters
     */
    async getAvailableUniversities() {
        const chapters = await this.getAllChapters()
        const universities = [...new Set(chapters.map(chapter => chapter.universityName))]
        return universities.sort()
    }
}

// Export singleton instance
export const chapterService = new ChapterService()