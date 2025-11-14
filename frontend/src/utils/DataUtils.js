/**
 * Data Utilities - Following Clean Code principles
 * Pure functions that operate on data structures
 * These follow the functional programming paradigm for data manipulation
 */

import { USER_ROLES, LEADERSHIP_ROLES } from '@/constants'

/**
 * Member Data Utilities
 * Pure functions that operate on member data
 */
export const MemberUtils = {
    /**
     * Create full name from member data
     */
    getFullName(memberData) {
        if (!memberData) return ''
        return `${memberData.firstName || ''} ${memberData.lastName || ''}`.trim()
    },

    /**
     * Generate initials from member data
     */
    getInitials(memberData) {
        if (!memberData) return ''
        const first = memberData.firstName?.charAt(0)?.toUpperCase() || ''
        const last = memberData.lastName?.charAt(0)?.toUpperCase() || ''
        return `${first}${last}`
    },

    /**
     * Check if member has leadership role
     */
    isLeader(memberData) {
        return memberData && LEADERSHIP_ROLES.includes(memberData.role)
    },

    /**
     * Check if member is active
     */
    isActive(memberData) {
        return memberData && memberData.active === true
    },

    /**
     * Check if member has specific role
     */
    hasRole(memberData, role) {
        return memberData && memberData.role === role
    },

    /**
     * Check if member can modify chapter
     */
    canModifyChapter(memberData) {
        return this.isActive(memberData) && this.hasRole(memberData, USER_ROLES.PRESIDENT)
    },

    /**
     * Check if member is graduating in specific year
     */
    isGraduating(memberData, year) {
        if (!memberData?.graduationYear) return false
        try {
            return parseInt(memberData.graduationYear) === year
        } catch {
            return false
        }
    },

    /**
     * Filter members by search term
     */
    matchesSearch(memberData, searchTerm) {
        if (!searchTerm || !memberData) return true

        const term = searchTerm.toLowerCase()
        const fullName = this.getFullName(memberData).toLowerCase()

        return fullName.includes(term) ||
            memberData.email?.toLowerCase().includes(term) ||
            memberData.username?.toLowerCase().includes(term) ||
            memberData.major?.toLowerCase().includes(term)
    },

    /**
     * Generate consistent avatar color
     */
    generateAvatarColor(memberData) {
        if (!memberData) return '#007bff'

        const colors = ['#007bff', '#28a745', '#dc3545', '#ffc107', '#17a2b8', '#6f42c1']
        const fullName = this.getFullName(memberData)
        const hash = fullName.split('').reduce((a, b) => {
            a = ((a << 5) - a) + b.charCodeAt(0)
            return a & a
        }, 0)
        return colors[Math.abs(hash) % colors.length]
    }
}

/**
 * Chapter Data Utilities
 * Pure functions that operate on chapter data
 */
export const ChapterUtils = {
    /**
     * Create full location from chapter data
     */
    getFullLocation(chapterData) {
        if (!chapterData) return ''
        return `${chapterData.city || ''}, ${chapterData.state || ''}`.trim()
    },

    /**
     * Get member count from chapter data
     */
    getMemberCount(chapterData) {
        return chapterData?.members?.length || 0
    },

    /**
     * Get active member count
     */
    getActiveMemberCount(chapterData) {
        if (!chapterData?.members) return 0
        return chapterData.members.filter(m => MemberUtils.isActive(m)).length
    },

    /**
     * Get leadership member count
     */
    getLeadershipCount(chapterData) {
        if (!chapterData?.members) return 0
        return chapterData.members.filter(m =>
            MemberUtils.isActive(m) && MemberUtils.isLeader(m)
        ).length
    },

    /**
     * Check if chapter has president
     */
    hasPresident(chapterData) {
        return chapterData?.members?.some(m =>
            MemberUtils.isActive(m) && MemberUtils.hasRole(m, USER_ROLES.PRESIDENT)
        ) || false
    },

    /**
     * Check if chapter can accept new members
     */
    canAcceptNewMembers(chapterData) {
        return chapterData?.active && this.getMemberCount(chapterData) < 100
    },

    /**
     * Check if chapter is active
     */
    isActive(chapterData) {
        return chapterData && chapterData.active === true
    },

    /**
     * Get founded year from chapter data
     */
    getFoundedYear(chapterData) {
        if (!chapterData?.createdAt) return null
        return new Date(chapterData.createdAt).getFullYear()
    },

    /**
     * Filter chapters by search criteria
     */
    matchesSearch(chapterData, searchTerm, stateFilter = null) {
        if (!chapterData) return false
        if (!searchTerm && !stateFilter) return true

        const matchesText = !searchTerm ||
            chapterData.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            chapterData.universityName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            chapterData.city?.toLowerCase().includes(searchTerm.toLowerCase())

        const matchesState = !stateFilter || chapterData.state === stateFilter

        return matchesText && matchesState
    }
}

/**
 * Collection Utilities
 * Pure functions for working with collections of data
 */
export const CollectionUtils = {
    /**
     * Sort members by various criteria
     */
    sortMembers(members, sortBy = 'name') {
        if (!Array.isArray(members)) return []

        return [...members].sort((a, b) => {
            switch (sortBy) {
                case 'name':
                    return MemberUtils.getFullName(a).localeCompare(MemberUtils.getFullName(b))
                case 'role':
                    return (a.role || '').localeCompare(b.role || '')
                case 'email':
                    return (a.email || '').localeCompare(b.email || '')
                case 'graduationYear':
                    return (a.graduationYear || '').localeCompare(b.graduationYear || '')
                default:
                    return 0
            }
        })
    },

    /**
     * Sort chapters by various criteria
     */
    sortChapters(chapters, sortBy = 'name') {
        if (!Array.isArray(chapters)) return []

        return [...chapters].sort((a, b) => {
            switch (sortBy) {
                case 'name':
                    return (a.name || '').localeCompare(b.name || '')
                case 'university':
                    return (a.universityName || '').localeCompare(b.universityName || '')
                case 'state':
                    return (a.state || '').localeCompare(b.state || '')
                case 'memberCount':
                    return ChapterUtils.getMemberCount(b) - ChapterUtils.getMemberCount(a)
                default:
                    return 0
            }
        })
    },

    /**
     * Group data by specified field
     */
    groupBy(items, keyFunction) {
        if (!Array.isArray(items)) return {}

        return items.reduce((groups, item) => {
            const key = keyFunction(item)
            if (!groups[key]) {
                groups[key] = []
            }
            groups[key].push(item)
            return groups
        }, {})
    }
}