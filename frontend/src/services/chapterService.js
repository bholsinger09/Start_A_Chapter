import api from './api'

// Mock data for when backend is not available
const mockChapters = [
  {
    id: 1,
    name: "University of California - Berkeley",
    university: "UC Berkeley",
    state: "California",
    city: "Berkeley",
    active: true,
    foundedDate: "2020-09-01",
    memberCount: 45,
    healthScore: 85,
    lastActivity: "2024-10-15",
    description: "Leading innovation in the Bay Area with a focus on technology and entrepreneurship."
  },
  {
    id: 2,
    name: "Harvard University",
    university: "Harvard University",
    state: "Massachusetts",
    city: "Cambridge",
    active: true,
    foundedDate: "2019-08-15",
    memberCount: 67,
    healthScore: 92,
    lastActivity: "2024-10-16",
    description: "Prestigious chapter focusing on leadership development and academic excellence."
  },
  {
    id: 3,
    name: "University of Texas - Austin",
    university: "UT Austin",
    state: "Texas",
    city: "Austin",
    active: true,
    foundedDate: "2021-01-20",
    memberCount: 38,
    healthScore: 78,
    lastActivity: "2024-10-14",
    description: "Dynamic chapter promoting innovation and community engagement in the heart of Texas."
  },
  {
    id: 4,
    name: "Stanford University",
    university: "Stanford University",
    state: "California",
    city: "Stanford",
    active: true,
    foundedDate: "2020-02-10",
    memberCount: 52,
    healthScore: 88,
    lastActivity: "2024-10-17",
    description: "Silicon Valley chapter at the forefront of technology and entrepreneurship."
  },
  {
    id: 5,
    name: "University of Florida",
    university: "University of Florida",
    state: "Florida",
    city: "Gainesville",
    active: false,
    foundedDate: "2021-03-05",
    memberCount: 23,
    healthScore: 45,
    lastActivity: "2024-09-20",
    description: "Growing chapter with focus on community service and leadership development."
  }
]

export const chapterService = {
  // Get all chapters
  async getAllChapters() {
    console.log('🔍 SERVICE DEBUG: getAllChapters() called')
    try {
      const response = await api.get('/chapters')
      return response.data
    } catch (error) {
      console.warn('Backend not available, using mock data:', error.message)
      // Return mock data when backend is not available
      console.log('🔍 SERVICE DEBUG: Returning mock chapters:', mockChapters.length, 'items')
      return mockChapters
    }
  },

  // Get chapter by ID
  async getChapterById(id) {
    try {
      const response = await api.get(`/chapters/${id}`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Create new chapter
  async createChapter(chapter) {
    try {
      const response = await api.post('/chapters', chapter)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Update chapter
  async updateChapter(id, chapter) {
    try {
      const response = await api.put(`/chapters/${id}`, chapter)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Delete chapter
  async deleteChapter(id) {
    try {
      await api.delete(`/chapters/${id}`)
      return true
    } catch (error) {
      throw error
    }
  },

  // Search chapters
  async searchChapters(params) {
    try {
      const response = await api.get('/chapters/search', { params })
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get chapters by state
  async getChaptersByState(state) {
    try {
      const response = await api.get(`/chapters/state/${state}`)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Create chapter with institution
  async createChapterWithInstitution(chapterData) {
    try {
      const response = await api.post('/chapters/with-institution', chapterData)
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get all institutions
  async getAllInstitutions() {
    try {
      const response = await api.get('/chapters/institutions')
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get all universities
  async getAllUniversities() {
    try {
      const response = await api.get('/chapters/institutions/universities')
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Get all churches
  async getAllChurches() {
    try {
      const response = await api.get('/chapters/institutions/churches')
      return response.data
    } catch (error) {
      throw error
    }
  },

  // Search institutions
  async searchInstitutions(query) {
    try {
      const response = await api.get('/chapters/institutions/search', { 
        params: { query }
      })
      return response.data
    } catch (error) {
      throw error
    }
  }
}

export default chapterService