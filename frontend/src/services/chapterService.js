import api from './api'

export const chapterService = {
  // Get all chapters
  async getAllChapters() {
    try {
      const response = await api.get('/chapters')
      return response.data
    } catch (error) {
      throw error
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
  }
}