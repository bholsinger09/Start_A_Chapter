import api from './api'

// Mock data for when backend is not available
const mockMembers = [
  {
    id: 1,
    firstName: "Alice",
    lastName: "Johnson",
    email: "alice.johnson@berkeley.edu",
    username: "alicej",
    chapterId: 1,
    role: "PRESIDENT",
    active: true,
    graduationYear: 2025,
    major: "Computer Science",
    phoneNumber: "(555) 123-4567"
  },
  {
    id: 2,
    firstName: "Bob",
    lastName: "Smith",
    email: "bob.smith@harvard.edu",
    username: "bobsmith",
    chapterId: 2,
    role: "VICE_PRESIDENT",
    active: true,
    graduationYear: 2024,
    major: "Business Administration",
    phoneNumber: "(555) 234-5678"
  },
  {
    id: 3,
    firstName: "Carol",
    lastName: "Davis",
    email: "carol.davis@utexas.edu",
    username: "carold",
    chapterId: 3,
    role: "MEMBER",
    active: true,
    graduationYear: 2026,
    major: "Engineering",
    phoneNumber: "(555) 345-6789"
  }
]

export const memberService = {
  // Get all members
  async getAllMembers() {
    try {
      const response = await api.get('/members')
      return response.data
    } catch (error) {
      console.warn('Backend not available, using mock member data:', error.message)
      // Return mock data when backend is not available
      return mockMembers
    }
  },

  // Get member by ID
  async getMemberById(id) {
    try {
      const response = await api.get(`/members/${id}`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get members by chapter
  async getMembersByChapter(chapterId) {
    try {
      const response = await api.get(`/members/chapter/${chapterId}`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get active members by chapter
  async getActiveMembersByChapter(chapterId) {
    try {
      const response = await api.get(`/members/chapter/${chapterId}/active`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get members by role
  async getMembersByRole(chapterId, role) {
    try {
      const response = await api.get(`/members/chapter/${chapterId}/role/${role}`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get chapter officers
  async getChapterOfficers(chapterId) {
    try {
      const response = await api.get(`/members/chapter/${chapterId}/officers`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get chapter president
  async getChapterPresident(chapterId) {
    try {
      const response = await api.get(`/members/chapter/${chapterId}/president`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Create new member
  async createMember(member) {
    try {
      const response = await api.post('/members', member)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Update member (using flexible endpoint)
  async updateMember(id, member) {
    // Create a clean update request without any problematic fields
    const updateRequest = {
      firstName: member.firstName,
      lastName: member.lastName,
      email: member.email,
      phoneNumber: member.phoneNumber || null,
      major: member.major || null,
      graduationYear: member.graduationYear || null,
      role: member.role,
      active: member.active !== undefined ? member.active : true,
      // Send chapter ID instead of full chapter object
      chapterId: member.chapter ? member.chapter.id : null
    }
    
    console.log('🔍 Updating member with flexible request:', updateRequest)
    
    try {
      const response = await api.put(`/members/${id}/flexible`, updateRequest)
      return response.data
    } catch (error) {
      console.error('❌ Flexible member update failed, trying original endpoint:', error)
      // Fallback to original endpoint if flexible fails
      try {
        const response = await api.put(`/members/${id}`, updateRequest)
        return response.data
      } catch (fallbackError) {
        console.error('❌ Both endpoints failed:', fallbackError)
        throw fallbackError
      }
    }
  },

  // Update member role
  async updateMemberRole(id, role) {
    try {
      const response = await api.put(`/members/${id}/role`, { role })
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Delete member
  async deleteMember(id) {
    try {
      await api.delete(`/members/${id}`)
      return true
    } catch (error) {
      throw error
    }
  },

  // Activate member
  async activateMember(id) {
    try {
      const response = await api.put(`/members/${id}/activate`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get member count by chapter
  async getMemberCount(chapterId) {
    try {
      const response = await api.get(`/members/chapter/${chapterId}/count`)
      return response.data
    } catch (error) {
      throw error
    }
  }
}

export default memberService