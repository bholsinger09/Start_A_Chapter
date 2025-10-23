import api from './api'

// Search service for chapter discovery and recommendations
class SearchService {
  // Global search with intelligent filtering
  async globalSearch(query, filters = {}, pagination = {}) {
    try {
      const params = new URLSearchParams()

      if (query) params.append('query', query)
      if (filters.states?.length) params.append('states', filters.states.join(','))
      if (filters.statuses?.length) params.append('statuses', filters.statuses.join(','))
      if (filters.minMembers) params.append('minMembers', filters.minMembers)
      if (filters.maxMembers) params.append('maxMembers', filters.maxMembers)
      if (filters.minHealthScore) params.append('minHealthScore', filters.minHealthScore)
      if (filters.maxHealthScore) params.append('maxHealthScore', filters.maxHealthScore)
      if (pagination.page) params.append('page', pagination.page)
      if (pagination.size) params.append('size', pagination.size)

      const response = await api.get(`/search/global?${params.toString()}`)
      return response.data
    } catch (error) {
      console.error('Error performing global search:', error)
      throw error
    }
  }

  // Get personalized chapter recommendations
  async getRecommendations(memberId, limit = 5) {
    try {
      const params = new URLSearchParams()
      if (memberId) params.append('memberId', memberId)
      if (limit) params.append('limit', limit)

      const response = await api.get(`/search/recommendations?${params.toString()}`)
      return response.data
    } catch (error) {
      console.error('Error getting recommendations:', error)
      throw error
    }
  }

  // Find similar chapters
  async getSimilarChapters(chapterId, limit = 5) {
    try {
      const params = new URLSearchParams()
      if (limit) params.append('limit', limit)

      const response = await api.get(`/search/similar/${chapterId}?${params.toString()}`)
      return response.data
    } catch (error) {
      console.error('Error getting similar chapters:', error)
      throw error
    }
  }

  // Get search suggestions for auto-complete
  async getSuggestions(partial) {
    try {
      const response = await api.get(`/search/suggestions?partial=${encodeURIComponent(partial)}`)
      return response.data
    } catch (error) {
      console.error('Error getting search suggestions:', error)
      throw error
    }
  }

  // Get trending chapters
  async getTrendingChapters(limit = 10) {
    try {
      const response = await api.get(`/search/trending?limit=${limit}`)
      return response.data
    } catch (error) {
      console.error('Error getting trending chapters:', error)
      throw error
    }
  }

  // Advanced faceted search
  async facetedSearch(facets = {}) {
    try {
      const params = new URLSearchParams()

      Object.entries(facets).forEach(([key, value]) => {
        if (value) {
          params.append(key, value)
        }
      })

      const response = await api.get(`/search/faceted?${params.toString()}`)
      return response.data
    } catch (error) {
      console.error('Error performing faceted search:', error)
      throw error
    }
  }

  // Geographic search
  async geographicSearch(latitude, longitude, radiusKm = 50, limit = 10) {
    try {
      const params = new URLSearchParams({
        latitude: latitude.toString(),
        longitude: longitude.toString(),
        radiusKm: radiusKm.toString(),
        limit: limit.toString()
      })

      const response = await api.get(`/search/geographic?${params.toString()}`)
      return response.data
    } catch (error) {
      console.error('Error performing geographic search:', error)
      throw error
    }
  }

  // Get saved searches (mock implementation for now)
  async getSavedSearches(memberId) {
    try {
      // TODO: Implement backend endpoint for saved searches
      return []
    } catch (error) {
      console.error('Error getting saved searches:', error)
      throw error
    }
  }

  // Save a search (mock implementation for now)
  async saveSearch(memberId, searchName, searchParams) {
    try {
      // TODO: Implement backend endpoint for saving searches
      return { success: true, message: 'Search saved successfully' }
    } catch (error) {
      console.error('Error saving search:', error)
      throw error
    }
  }

  // Helper method to debounce search requests
  debounce(func, wait) {
    let timeout
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout)
        func(...args)
      }
      clearTimeout(timeout)
      timeout = setTimeout(later, wait)
    }
  }
}

export default new SearchService()