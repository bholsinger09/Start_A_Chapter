/**
 * Business Objects - Following Clean Code principles
 * These objects hide their data and expose meaningful behavior
 * According to Uncle Bob: "Objects hide their data behind abstractions and expose
 * functions that operate on that data."
 */

import { USER_ROLES, LEADERSHIP_ROLES } from '@/constants'

/**
 * Member Business Object
 * Encapsulates member behavior and business rules
 */
export class MemberObject {
    #data

    constructor(memberData) {
        this.#data = { ...memberData }
    }

    // Behavioral methods - what the member can do
    getFullName() {
        return `${this.#data.firstName} ${this.#data.lastName}`.trim()
    }

    getInitials() {
        const first = this.#data.firstName?.charAt(0)?.toUpperCase() || ''
        const last = this.#data.lastName?.charAt(0)?.toUpperCase() || ''
        return `${first}${last}`
    }

    isLeader() {
        return LEADERSHIP_ROLES.includes(this.#data.role)
    }

    isActive() {
        return this.#data.active === true
    }

    hasRole(role) {
        return this.#data.role === role
    }

    canModifyChapter() {
        return this.isActive() && this.hasRole(USER_ROLES.PRESIDENT)
    }

    belongsToChapter(chapterId) {
        return this.#data.chapterId === chapterId
    }

    isGraduating(year) {
        if (!this.#data.graduationYear) return false
        try {
            return parseInt(this.#data.graduationYear) === year
        } catch {
            return false
        }
    }

    matchesSearch(searchTerm) {
        if (!searchTerm) return true

        const term = searchTerm.toLowerCase()
        return this.getFullName().toLowerCase().includes(term) ||
            this.#data.email.toLowerCase().includes(term) ||
            this.#data.username.toLowerCase().includes(term) ||
            this.#data.major.toLowerCase().includes(term)
    }

    generateAvatarColor() {
        const colors = ['#007bff', '#28a745', '#dc3545', '#ffc107', '#17a2b8', '#6f42c1']
        const hash = this.getFullName().split('').reduce((a, b) => {
            a = ((a << 5) - a) + b.charCodeAt(0)
            return a & a
        }, 0)
        return colors[Math.abs(hash) % colors.length]
    }

    // Limited data access - only what's needed
    getId() {
        return this.#data.id
    }

    getEmail() {
        return this.#data.email
    }

    getRole() {
        return this.#data.role
    }

    getRoleLabel() {
        const roleLabels = {
            [USER_ROLES.PRESIDENT]: 'President',
            [USER_ROLES.VICE_PRESIDENT]: 'Vice President',
            [USER_ROLES.TREASURER]: 'Treasurer',
            [USER_ROLES.SECRETARY]: 'Secretary',
            [USER_ROLES.MEMBER]: 'Member'
        }
        return roleLabels[this.#data.role] || this.#data.role
    }

    getChapterName() {
        return this.#data.chapterName || 'No Chapter'
    }

    // Export data when needed (for API calls, etc.)
    toData() {
        return { ...this.#data }
    }
}

/**
 * Chapter Business Object
 * Encapsulates chapter behavior and business rules
 */
export class ChapterObject {
    #data

    constructor(chapterData) {
        this.#data = { ...chapterData }
    }

    // Behavioral methods - what the chapter can do
    getFullLocation() {
        return `${this.#data.city}, ${this.#data.state}`
    }

    getMemberCount() {
        return this.#data.members?.length || 0
    }

    getActiveMemberCount() {
        if (!this.#data.members) return 0
        return this.#data.members.filter(m => m.active).length
    }

    getLeadershipCount() {
        if (!this.#data.members) return 0
        return this.#data.members.filter(m => 
            m.active && LEADERSHIP_ROLES.includes(m.role)
        ).length
    }

    hasPresident() {
        return this.#data.members?.some(m => 
            m.active && m.role === USER_ROLES.PRESIDENT
        ) || false
    }

    canAcceptNewMembers() {
        return this.#data.active && this.getMemberCount() < 100
    }

    isActive() {
        return this.#data.active === true
    }

    getFoundedYear() {
        if (!this.#data.createdAt) return null
        return new Date(this.#data.createdAt).getFullYear()
    }

    matchesSearch(searchTerm, stateFilter = null) {
        if (!searchTerm && !stateFilter) return true

        const matchesText = !searchTerm || 
            this.#data.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            this.#data.universityName.toLowerCase().includes(searchTerm.toLowerCase()) ||
            this.#data.city.toLowerCase().includes(searchTerm.toLowerCase())

        const matchesState = !stateFilter || this.#data.state === stateFilter

        return matchesText && matchesState
    }

    // Limited data access
    getId() {
        return this.#data.id
    }

    getName() {
        return this.#data.name
    }

    getUniversityName() {
        return this.#data.universityName
    }

    getState() {
        return this.#data.state
    }

    getDescription() {
        return this.#data.description
    }

    // Export data when needed
    toData() {
        return { ...this.#data }
    }
}