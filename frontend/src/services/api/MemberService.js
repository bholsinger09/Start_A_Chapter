/**
 * Member Service - Following Clean Architecture and Single Responsibility Principle  
 * Handles all member-related API operations and business logic
 */

import { BaseApiService } from './BaseApiService.js'
import { API_ENDPOINTS, USER_ROLES, LEADERSHIP_ROLES } from '@/constants'

/**
 * Member Data Transfer Object
 */
export class MemberDTO {
    constructor(data = {}) {
        this.id = data.id || null
        this.firstName = data.firstName || ''
        this.lastName = data.lastName || ''
        this.email = data.email || ''
        this.username = data.username || ''
        this.phoneNumber = data.phoneNumber || ''
        this.role = data.role || USER_ROLES.MEMBER
        this.active = data.active !== undefined ? data.active : true
        this.major = data.major || ''
        this.graduationYear = data.graduationYear || ''
        this.chapterId = data.chapterId || null
        this.chapter = data.chapter || null
        this.createdAt = data.createdAt || null
        this.updatedAt = data.updatedAt || null
    }

    // Computed properties
    get fullName() {
        return `${this.firstName} ${this.lastName}`.trim()
    }

    get initials() {
        const first = this.firstName?.charAt(0)?.toUpperCase() || ''
        const last = this.lastName?.charAt(0)?.toUpperCase() || ''
        return `${first}${last}`
    }

    get isActive() {
        return this.active === true
    }

    get isLeader() {
        return LEADERSHIP_ROLES.includes(this.role)
    }

    get roleLabel() {
        const roleLabels = {
            [USER_ROLES.PRESIDENT]: 'President',
            [USER_ROLES.VICE_PRESIDENT]: 'Vice President',
            [USER_ROLES.TREASURER]: 'Treasurer',
            [USER_ROLES.SECRETARY]: 'Secretary',
            [USER_ROLES.MEMBER]: 'Member',
            [USER_ROLES.ADMIN]: 'Administrator'
        }
        return roleLabels[this.role] || this.role
    }

    get chapterName() {
        return this.chapter?.name || 'No Chapter'
    }

    get profileUrl() {
        return `/members/${this.id}`
    }

    get avatarColor() {
        // Generate consistent color based on name
        const colors = ['#007bff', '#28a745', '#dc3545', '#ffc107', '#17a2b8', '#6f42c1']
        const hash = this.fullName.split('').reduce((a, b) => {
            a = ((a << 5) - a) + b.charCodeAt(0)
            return a & a
        }, 0)
        return colors[Math.abs(hash) % colors.length]
    }

    // Business logic methods
    hasRole(role) {
        return this.role === role
    }

    belongsToChapter(chapterId) {
        return this.chapterId === chapterId
    }

    matchesSearch(searchTerm) {
        if (!searchTerm) return true

        const term = searchTerm.toLowerCase()
        return this.fullName.toLowerCase().includes(term) ||
            this.email.toLowerCase().includes(term) ||
            this.username.toLowerCase().includes(term) ||
            this.major.toLowerCase().includes(term)
    }

    canManageChapter() {
        return this.hasRole(USER_ROLES.PRESIDENT) || this.hasRole(USER_ROLES.ADMIN)
    }

    canEditFinances() {
        return this.hasRole(USER_ROLES.TREASURER) ||
            this.hasRole(USER_ROLES.PRESIDENT) ||
            this.hasRole(USER_ROLES.ADMIN)
    }

    isGraduating(currentYear = new Date().getFullYear()) {
        return parseInt(this.graduationYear) === currentYear
    }
}

/**
 * Member Service Class
 * Encapsulates all member-related business operations
 */
export class MemberService extends BaseApiService {

    /**
     * Fetch all members with optional filtering
     */
    async getAllMembers(filters = {}) {
        const url = this.buildUrl(API_ENDPOINTS.MEMBERS.LIST, filters)
        const response = await this.get(url)

        // Transform raw data to DTOs
        return response.data.map(memberData => new MemberDTO(memberData))
    }

    /**
     * Fetch member by ID with full details
     */
    async getMemberById(id) {
        const response = await this.get(API_ENDPOINTS.MEMBERS.BY_ID(id))
        return new MemberDTO(response.data)
    }

    /**
     * Fetch member by email
     */
    async getMemberByEmail(email) {
        const response = await this.get(API_ENDPOINTS.MEMBERS.BY_EMAIL(email))
        return new MemberDTO(response.data)
    }

    /**
     * Create new member
     */
    async createMember(memberData) {
        // Validate before sending
        const validation = this.validateMemberData(memberData)
        if (!validation.isValid) {
            throw new Error(`Validation failed: ${Object.values(validation.errors).join(', ')}`)
        }

        const response = await this.post(API_ENDPOINTS.MEMBERS.CREATE, memberData)
        return new MemberDTO(response.data)
    }

    /**
     * Update existing member
     */
    async updateMember(id, memberData) {
        const validation = this.validateMemberData(memberData, false) // Skip required validation for updates
        if (!validation.isValid) {
            throw new Error(`Validation failed: ${Object.values(validation.errors).join(', ')}`)
        }

        const response = await this.put(API_ENDPOINTS.MEMBERS.BY_ID(id), memberData)
        return new MemberDTO(response.data)
    }

    /**
     * Delete member
     */
    async deleteMember(id) {
        await this.delete(API_ENDPOINTS.MEMBERS.BY_ID(id))
        return { success: true, id }
    }

    /**
     * Search members with advanced criteria
     */
    async searchMembers(searchCriteria) {
        const {
            searchTerm = '',
            chapterId = null,
            role = '',
            active = null,
            graduationYear = null,
            sortBy = 'lastName',
            sortOrder = 'asc',
            page = 1,
            limit = 20
        } = searchCriteria

        const params = {
            search: searchTerm,
            sortBy,
            sortOrder,
            page,
            limit
        }

        // Add optional filters
        if (chapterId) params.chapterId = chapterId
        if (role) params.role = role
        if (active !== null) params.active = active
        if (graduationYear) params.graduationYear = graduationYear

        const members = await this.getAllMembers(params)

        // Apply client-side filtering for complex criteria
        return this.applyAdvancedFiltering(members, searchCriteria)
    }

    /**
     * Apply client-side filtering for complex search criteria
     */
    applyAdvancedFiltering(members, criteria) {
        let filtered = members

        // Text search across multiple fields
        if (criteria.searchTerm) {
            filtered = filtered.filter(member => member.matchesSearch(criteria.searchTerm))
        }

        // Chapter filtering
        if (criteria.chapterId) {
            filtered = filtered.filter(member => member.belongsToChapter(criteria.chapterId))
        }

        // Role filtering
        if (criteria.role) {
            filtered = filtered.filter(member => member.hasRole(criteria.role))
        }

        // Active status filtering
        if (criteria.active !== null) {
            filtered = filtered.filter(member => member.active === criteria.active)
        }

        // Graduation year filtering
        if (criteria.graduationYear) {
            filtered = filtered.filter(member => member.graduationYear === criteria.graduationYear)
        }

        // Leadership filter
        if (criteria.leadersOnly) {
            filtered = filtered.filter(member => member.isLeader)
        }

        return filtered
    }

    /**
     * Get member statistics
     */
    async getMemberStatistics() {
        const members = await this.getAllMembers()
        return this.calculateStatistics(members)
    }

    /**
     * Calculate basic member statistics
     */
    calculateBasicStats(members) {
        return {
            totalMembers: members.length,
            activeMembers: members.filter(m => m.isActive).length,
            leadershipMembers: members.filter(m => m.isLeader).length
        }
    }

    /**
     * Calculate distribution statistics
     */
    calculateDistributions(members) {
        const distributions = {
            roleDistribution: {},
            chapterDistribution: {},
            graduationYearDistribution: {},
            majorDistribution: {}
        }

        // Role and chapter distributions
        members.forEach(member => {
            distributions.roleDistribution[member.role] = 
                (distributions.roleDistribution[member.role] || 0) + 1
            
            const chapterName = member.chapterName
            distributions.chapterDistribution[chapterName] = 
                (distributions.chapterDistribution[chapterName] || 0) + 1

            if (member.graduationYear) {
                distributions.graduationYearDistribution[member.graduationYear] =
                    (distributions.graduationYearDistribution[member.graduationYear] || 0) + 1
            }
        })

        // Major distribution (top 10)
        const majorCounts = {}
        members.forEach(member => {
            if (member.major) {
                majorCounts[member.major] = (majorCounts[member.major] || 0) + 1
            }
        })

        distributions.majorDistribution = Object.entries(majorCounts)
            .sort(([, a], [, b]) => b - a)
            .slice(0, 10)
            .reduce((obj, [major, count]) => {
                obj[major] = count
                return obj
            }, {})

        return distributions
    }

    /**
     * Calculate temporal statistics (recent and graduating members)
     */
    calculateTemporalStats(members) {
        const thirtyDaysAgo = new Date()
        thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30)

        const recentMembers = members
            .filter(m => m.createdAt && new Date(m.createdAt) > thirtyDaysAgo)
            .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))

        const currentYear = new Date().getFullYear()
        const graduatingMembers = members.filter(m => m.isGraduating(currentYear))

        return { recentMembers, graduatingMembers }
    }

    /**
     * Calculate comprehensive member statistics
     */
    calculateStatistics(members) {
        const basicStats = this.calculateBasicStats(members)
        const distributions = this.calculateDistributions(members)
        const temporalStats = this.calculateTemporalStats(members)

        return {
            ...basicStats,
            ...distributions,
            ...temporalStats,
            averageClassSize: 0
        }
    }

    /**
     * Validate member data before submission
     */
    validateMemberData(memberData, requireAll = true) {
        const errors = {}

        // Required field validation
        if (requireAll) {
            if (!memberData.firstName?.trim()) {
                errors.firstName = 'First name is required'
            }

            if (!memberData.lastName?.trim()) {
                errors.lastName = 'Last name is required'
            }

            if (!memberData.email?.trim()) {
                errors.email = 'Email is required'
            }
        }

        // Email format validation
        if (memberData.email && !this.isValidEmail(memberData.email)) {
            errors.email = 'Please enter a valid email address'
        }

        // Length validation
        if (memberData.firstName?.length > 50) {
            errors.firstName = 'First name cannot exceed 50 characters'
        }

        if (memberData.lastName?.length > 50) {
            errors.lastName = 'Last name cannot exceed 50 characters'
        }

        if (memberData.email?.length > 100) {
            errors.email = 'Email cannot exceed 100 characters'
        }

        // Role validation
        if (memberData.role && !Object.values(USER_ROLES).includes(memberData.role)) {
            errors.role = 'Invalid role specified'
        }

        // Phone number validation
        if (memberData.phoneNumber && !this.isValidPhoneNumber(memberData.phoneNumber)) {
            errors.phoneNumber = 'Please enter a valid phone number'
        }

        // Graduation year validation
        if (memberData.graduationYear) {
            const year = parseInt(memberData.graduationYear)
            const currentYear = new Date().getFullYear()
            if (year < currentYear - 10 || year > currentYear + 10) {
                errors.graduationYear = 'Graduation year must be within 10 years of current year'
            }
        }

        return {
            isValid: Object.keys(errors).length === 0,
            errors
        }
    }

    /**
     * Email validation helper
     */
    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        return emailRegex.test(email)
    }

    /**
     * Phone number validation helper
     */
    isValidPhoneNumber(phone) {
        const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/
        return phoneRegex.test(phone.replace(/[\s\-\(\)]/g, ''))
    }

    /**
     * Get members by chapter ID
     */
    async getMembersByChapter(chapterId) {
        const members = await this.getAllMembers({ chapterId })
        return members.filter(member => member.belongsToChapter(chapterId))
    }

    /**
     * Get leadership members across all chapters
     */
    async getLeadershipMembers() {
        const members = await this.getAllMembers()
        return members.filter(member => member.isLeader)
    }

    /**
     * Get graduating members by year
     */
    async getGraduatingMembers(year = new Date().getFullYear()) {
        const members = await this.getAllMembers()
        return members.filter(member => member.isGraduating(year))
    }

    /**
     * Update member role
     */
    async updateMemberRole(memberId, newRole) {
        if (!Object.values(USER_ROLES).includes(newRole)) {
            throw new Error('Invalid role specified')
        }

        return await this.updateMember(memberId, { role: newRole })
    }

    /**
     * Activate/deactivate member
     */
    async toggleMemberStatus(memberId, active = true) {
        return await this.updateMember(memberId, { active })
    }

    /**
     * Transfer member to different chapter
     */
    async transferMember(memberId, newChapterId) {
        return await this.updateMember(memberId, { chapterId: newChapterId })
    }
}

// Export singleton instance
export const memberService = new MemberService()