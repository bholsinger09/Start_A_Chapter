/**
 * ChapterService Tests
 * Testing API service layer following Clean Code testing principles
 */

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { chapterService } from '@/services/api/ChapterService.js'
import { API_CONFIG } from '@/config/constants.js'
import { createMockChapter, createMockChapters } from '../utils/testUtils.js'

// Mock fetch globally
global.fetch = vi.fn()

// Mock console methods for testing error logging
const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => { })

describe('ChapterService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    consoleSpy.mockClear()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('getAllChapters', () => {
    it('should fetch all chapters successfully', async () => {
      // Arrange
      const mockChapters = createMockChapters(3)
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockChapters
      })

      // Act
      const result = await chapterService.getAllChapters()

      // Assert
      expect(fetch).toHaveBeenCalledWith(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHAPTERS}`,
        expect.objectContaining({
          method: 'GET',
          headers: expect.objectContaining({
            'Content-Type': 'application/json'
          })
        })
      )
      expect(result).toEqual(mockChapters)
    })

    it('should handle query parameters correctly', async () => {
      // Arrange
      const filters = { state: 'CA', active: true }
      const mockChapters = createMockChapters(2)
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockChapters
      })

      // Act
      const result = await chapterService.getAllChapters(filters)

      // Assert
      expect(fetch).toHaveBeenCalledWith(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHAPTERS}?state=CA&active=true`,
        expect.any(Object)
      )
      expect(result).toEqual(mockChapters)
    })

    it('should handle empty query parameters', async () => {
      // Arrange
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => []
      })

      // Act
      await chapterService.getAllChapters({})

      // Assert
      expect(fetch).toHaveBeenCalledWith(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHAPTERS}`,
        expect.any(Object)
      )
    })

    it('should handle network errors', async () => {
      // Arrange
      fetch.mockRejectedValueOnce(new Error('Network error'))

      // Act & Assert
      await expect(chapterService.getAllChapters()).rejects.toThrow('Network error')
      expect(consoleSpy).toHaveBeenCalledWith(
        'Error fetching chapters:',
        expect.any(Error)
      )
    })

    it('should handle HTTP error responses', async () => {
      // Arrange
      fetch.mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error',
        text: async () => 'Server error details'
      })

      // Act & Assert
      await expect(chapterService.getAllChapters()).rejects.toThrow(
        'HTTP error! status: 500'
      )
    })
  })

  describe('getChapterById', () => {
    it('should fetch a specific chapter successfully', async () => {
      // Arrange
      const mockChapter = createMockChapter({ id: 1 })
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockChapter
      })

      // Act
      const result = await chapterService.getChapterById(1)

      // Assert
      expect(fetch).toHaveBeenCalledWith(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHAPTERS}/1`,
        expect.objectContaining({
          method: 'GET',
          headers: expect.objectContaining({
            'Content-Type': 'application/json'
          })
        })
      )
      expect(result).toEqual(mockChapter)
    })

    it('should handle chapter not found', async () => {
      // Arrange
      fetch.mockResolvedValueOnce({
        ok: false,
        status: 404,
        statusText: 'Not Found',
        text: async () => 'Chapter not found'
      })

      // Act & Assert
      await expect(chapterService.getChapterById(999)).rejects.toThrow(
        'HTTP error! status: 404'
      )
    })

    it('should validate chapter ID parameter', async () => {
      // Act & Assert
      await expect(chapterService.getChapterById(null)).rejects.toThrow()
      await expect(chapterService.getChapterById(undefined)).rejects.toThrow()
      await expect(chapterService.getChapterById('')).rejects.toThrow()
    })
  })

  describe('createChapter', () => {
    it('should create a new chapter successfully', async () => {
      // Arrange
      const newChapterData = { name: 'New Chapter', state: 'NY' }
      const createdChapter = createMockChapter({ id: 4, ...newChapterData })

      fetch.mockResolvedValueOnce({
        ok: true,
        status: 201,
        json: async () => createdChapter
      })

      // Act
      const result = await chapterService.createChapter(newChapterData)

      // Assert
      expect(fetch).toHaveBeenCalledWith(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHAPTERS}`,
        expect.objectContaining({
          method: 'POST',
          headers: expect.objectContaining({
            'Content-Type': 'application/json'
          }),
          body: JSON.stringify(newChapterData)
        })
      )
      expect(result).toEqual(createdChapter)
    })

    it('should handle validation errors', async () => {
      // Arrange
      const invalidData = { name: '' } // Invalid chapter data
      fetch.mockResolvedValueOnce({
        ok: false,
        status: 400,
        statusText: 'Bad Request',
        text: async () => 'Validation failed: Name is required'
      })

      // Act & Assert
      await expect(chapterService.createChapter(invalidData)).rejects.toThrow(
        'HTTP error! status: 400'
      )
    })

    it('should validate required chapter data', async () => {
      // Act & Assert
      await expect(chapterService.createChapter(null)).rejects.toThrow()
      await expect(chapterService.createChapter(undefined)).rejects.toThrow()
    })
  })

  describe('updateChapter', () => {
    it('should update an existing chapter successfully', async () => {
      // Arrange
      const updateData = { name: 'Updated Name' }
      const updatedChapter = createMockChapter({ id: 1, ...updateData })

      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => updatedChapter
      })

      // Act
      const result = await chapterService.updateChapter(1, updateData)

      // Assert
      expect(fetch).toHaveBeenCalledWith(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHAPTERS}/1`,
        expect.objectContaining({
          method: 'PUT',
          headers: expect.objectContaining({
            'Content-Type': 'application/json'
          }),
          body: JSON.stringify(updateData)
        })
      )
      expect(result).toEqual(updatedChapter)
    })

    it('should handle update conflicts', async () => {
      // Arrange
      fetch.mockResolvedValueOnce({
        ok: false,
        status: 409,
        statusText: 'Conflict',
        text: async () => 'Resource has been modified by another user'
      })

      // Act & Assert
      await expect(chapterService.updateChapter(1, {})).rejects.toThrow(
        'HTTP error! status: 409'
      )
    })
  })

  describe('deleteChapter', () => {
    it('should delete a chapter successfully', async () => {
      // Arrange
      fetch.mockResolvedValueOnce({
        ok: true,
        status: 204
      })

      // Act
      const result = await chapterService.deleteChapter(1)

      // Assert
      expect(fetch).toHaveBeenCalledWith(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHAPTERS}/1`,
        expect.objectContaining({
          method: 'DELETE',
          headers: expect.objectContaining({
            'Content-Type': 'application/json'
          })
        })
      )
      expect(result).toEqual({ success: true, id: 1 })
    })

    it('should handle delete errors', async () => {
      // Arrange
      fetch.mockResolvedValueOnce({
        ok: false,
        status: 404,
        statusText: 'Not Found',
        text: async () => 'Chapter not found'
      })

      // Act & Assert
      await expect(chapterService.deleteChapter(999)).rejects.toThrow(
        'HTTP error! status: 404'
      )
    })
  })

  describe('searchChapters', () => {
    it('should search chapters with search term', async () => {
      // Arrange
      const searchCriteria = { searchTerm: 'UCLA' }
      const searchResults = [createMockChapter({ name: 'UCLA Chapter' })]

      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => searchResults
      })

      // Act
      const result = await chapterService.searchChapters(searchCriteria)

      // Assert
      expect(fetch).toHaveBeenCalledWith(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHAPTERS}/search?searchTerm=UCLA`,
        expect.any(Object)
      )
      expect(result).toEqual(searchResults)
    })

    it('should handle multiple search criteria', async () => {
      // Arrange
      const searchCriteria = {
        searchTerm: 'University',
        state: 'CA',
        active: true
      }

      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => []
      })

      // Act
      await chapterService.searchChapters(searchCriteria)

      // Assert
      expect(fetch).toHaveBeenCalledWith(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHAPTERS}/search?searchTerm=University&state=CA&active=true`,
        expect.any(Object)
      )
    })

    it('should handle empty search results', async () => {
      // Arrange
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => []
      })

      // Act
      const result = await chapterService.searchChapters({ searchTerm: 'NonExistent' })

      // Assert
      expect(result).toEqual([])
    })
  })

  describe('calculateStatistics', () => {
    it('should calculate chapter statistics correctly', () => {
      // Arrange
      const chapters = [
        createMockChapter({ id: 1, active: true }),
        createMockChapter({ id: 2, active: false }),
        createMockChapter({ id: 3, active: true })
      ]

      // Act
      const stats = chapterService.calculateStatistics(chapters)

      // Assert
      expect(stats).toEqual({
        totalChapters: 3,
        activeChapters: 2,
        inactiveChapters: 1,
        activationRate: 66.67
      })
    })

    it('should handle empty chapters array', () => {
      // Act
      const stats = chapterService.calculateStatistics([])

      // Assert
      expect(stats).toEqual({
        totalChapters: 0,
        activeChapters: 0,
        inactiveChapters: 0,
        activationRate: 0
      })
    })

    it('should handle null/undefined chapters', () => {
      // Act & Assert
      expect(() => chapterService.calculateStatistics(null)).not.toThrow()
      expect(() => chapterService.calculateStatistics(undefined)).not.toThrow()

      const nullStats = chapterService.calculateStatistics(null)
      expect(nullStats.totalChapters).toBe(0)
    })
  })

  describe('Error Handling', () => {
    it('should handle timeout errors', async () => {
      // Arrange
      const timeoutError = new Error('Request timeout')
      timeoutError.name = 'AbortError'
      fetch.mockRejectedValueOnce(timeoutError)

      // Act & Assert
      await expect(chapterService.getAllChapters()).rejects.toThrow('Request timeout')
      expect(consoleSpy).toHaveBeenCalledWith(
        'Error fetching chapters:',
        expect.any(Error)
      )
    })

    it('should handle JSON parsing errors', async () => {
      // Arrange
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => {
          throw new Error('Invalid JSON')
        }
      })

      // Act & Assert
      await expect(chapterService.getAllChapters()).rejects.toThrow('Invalid JSON')
    })

    it('should log detailed error information', async () => {
      // Arrange
      const detailedError = new Error('Detailed error message')
      detailedError.stack = 'Error stack trace'
      fetch.mockRejectedValueOnce(detailedError)

      // Act
      try {
        await chapterService.getAllChapters()
      } catch (error) {
        // Expected to throw
      }

      // Assert
      expect(consoleSpy).toHaveBeenCalledWith(
        'Error fetching chapters:',
        expect.objectContaining({
          message: 'Detailed error message',
          stack: 'Error stack trace'
        })
      )
    })
  })

  describe('URL Construction', () => {
    it('should construct URLs correctly with parameters', async () => {
      // Arrange
      const params = { state: 'CA', active: true, limit: 10 }
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => []
      })

      // Act
      await chapterService.getAllChapters(params)

      // Assert
      const expectedUrl = `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CHAPTERS}?state=CA&active=true&limit=10`
      expect(fetch).toHaveBeenCalledWith(expectedUrl, expect.any(Object))
    })

    it('should handle special characters in parameters', async () => {
      // Arrange
      const params = { searchTerm: 'University & College' }
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => []
      })

      // Act
      await chapterService.searchChapters(params)

      // Assert
      const calledUrl = fetch.mock.calls[0][0]
      expect(calledUrl).toContain('University%20%26%20College')
    })
  })
})