/**
 * Pure Data Structures - Following Clean Code principles
 * These are simple data containers without behavior (data structures)
 * According to Uncle Bob: "Objects hide their data behind abstractions and expose
 * functions that operate on that data. Data structures expose their data and have no meaningful functions."
 */

/**
 * Member Data Structure - Pure data container
 * No behavior, just data access
 */
export class MemberData {
    constructor(data = {}) {
        this.id = data.id || null
        this.firstName = data.firstName || ''
        this.lastName = data.lastName || ''
        this.email = data.email || ''
        this.username = data.username || ''
        this.phoneNumber = data.phoneNumber || ''
        this.role = data.role || 'MEMBER'
        this.active = data.active !== undefined ? data.active : true
        this.major = data.major || ''
        this.graduationYear = data.graduationYear || ''
        this.chapterId = data.chapterId || null
        this.chapterName = data.chapterName || ''
        this.universityName = data.universityName || ''
        this.createdAt = data.createdAt || null
        this.updatedAt = data.updatedAt || null
    }
}

/**
 * Chapter Data Structure - Pure data container
 * No behavior, just data access
 */
export class ChapterData {
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
}

/**
 * Contact Information Data Structure
 * Immutable value object for contact details
 */
export class ContactData {
    constructor(email, phoneNumber = null) {
        Object.freeze(Object.assign(this, {
            email: email || '',
            phoneNumber: phoneNumber || ''
        }))
    }
}

/**
 * Academic Information Data Structure
 * Immutable value object for academic details
 */
export class AcademicData {
    constructor(major = '', graduationYear = '') {
        Object.freeze(Object.assign(this, {
            major: major || '',
            graduationYear: graduationYear || ''
        }))
    }
}

/**
 * Location Data Structure
 * Immutable value object for location information
 */
export class LocationData {
    constructor(city = '', state = '') {
        Object.freeze(Object.assign(this, {
            city: city || '',
            state: state || ''
        }))
    }
}