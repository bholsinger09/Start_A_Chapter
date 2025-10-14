import api from './api'

export const memberService = {
  // Get all members
  async getAllMembers() {
    try {
      const response = await api.get('/members')
      return response.data
    } catch (error) {
      throw error
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

  // Update member
  async updateMember(id, member) {
    try {
      const response = await api.put(`/members/${id}`, member)
      return response.data
    } catch (error) {
      throw error
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