/**
 * useChapters Composable Tests
 * Testing data management composable following Clean Code testing principles
 */

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { nextTick } from 'vue'
import { useChapters } from '@/composables/data/useChapters.js'
import { chapterService } from '@/services/api/ChapterService.js'
import { testComposable, createMockChapters, createMockChapter } from '../utils/testUtils.js'

// Mock the chapter service
vi.mock('@/services/api/ChapterService.js', () => ({
  chapterService: {
    getAllChapters: vi.fn(),
    getChapterById: vi.fn(),
    createChapter: vi.fn(),
    updateChapter: vi.fn(),
    deleteChapter: vi.fn(),
    searchChapters: vi.fn(),
    calculateStatistics: vi.fn()
  }
}))

// Mock the useError composable
vi.mock('@/composables/core/useError.js', () => ({
  useError: () => ({
    error: { value: null },
    setError: vi.fn(),
    clearError: vi.fn()
  })
}))

describe('useChapters Composable', () => {
  let composableResult
  let unmount

  beforeEach(() => {
    // Reset all mocks before each test
    vi.clearAllMocks()

    // Setup composable
    const { result, unmount: composableUnmount } = testComposable(useChapters)
    composableResult = result
    unmount = composableUnmount
  })

  afterEach(() => {
    unmount()
  })

  describe('Initial State', () => {
    it('should initialize with empty chapters array', () => {
      expect(composableResult.chapters.value).toEqual([])
    })

    it('should initialize with loading false', () => {
      expect(composableResult.loading.value).toBe(false)
    })

    it('should initialize with no current chapter', () => {
      expect(composableResult.currentChapter.value).toBe(null)
    })

    it('should have computed properties with correct initial values', () => {
      expect(composableResult.isLoading.value).toBe(false)
      expect(composableResult.hasChapters.value).toBe(false)
      expect(composableResult.availableStates.value).toEqual([])
    })
  })

  describe('Loading Chapters', () => {
    it('should load chapters successfully', async () => {
      // Arrange
      const mockChapters = createMockChapters(3)
      chapterService.getAllChapters.mockResolvedValue(mockChapters)

      // Act
      await composableResult.loadChapters()
      await nextTick()

      // Assert
      expect(chapterService.getAllChapters).toHaveBeenCalledWith({})
      expect(composableResult.chapters.value).toEqual(mockChapters)
      expect(composableResult.loading.value).toBe(false)
      expect(composableResult.hasChapters.value).toBe(true)
    })

    it('should handle loading state correctly', async () => {
      // Arrange
      chapterService.getAllChapters.mockImplementation(() => {
        expect(composableResult.loading.value).toBe(true)
        return Promise.resolve([])
      })

      // Act
      await composableResult.loadChapters()

      // Assert
      expect(composableResult.loading.value).toBe(false)
    })

    it('should handle API errors gracefully', async () => {
      // Arrange
      const errorMessage = 'Failed to load chapters'
      chapterService.getAllChapters.mockRejectedValue(new Error(errorMessage))

      // Act
      await composableResult.loadChapters()

      // Assert
      expect(composableResult.loading.value).toBe(false)
      expect(composableResult.chapters.value).toEqual([])
    })

    it('should pass filters to the service', async () => {
      // Arrange
      const filters = { state: 'CA', active: true }
      chapterService.getAllChapters.mockResolvedValue([])

      // Act
      await composableResult.loadChapters(filters)

      // Assert
      expect(chapterService.getAllChapters).toHaveBeenCalledWith(filters)
    })
  })

  describe('Load Chapter By ID', () => {
    it('should load a specific chapter successfully', async () => {
      // Arrange
      const mockChapter = createMockChapter({ id: 1 })
      chapterService.getChapterById.mockResolvedValue(mockChapter)

      // Act
      const result = await composableResult.loadChapterById(1)

      // Assert
      expect(chapterService.getChapterById).toHaveBeenCalledWith(1)
      expect(composableResult.currentChapter.value).toEqual(mockChapter)
      expect(result).toEqual(mockChapter)
    })

    it('should handle errors when loading chapter by ID', async () => {
      // Arrange
      chapterService.getChapterById.mockRejectedValue(new Error('Chapter not found'))

      // Act
      const result = await composableResult.loadChapterById(999)

      // Assert
      expect(result).toBe(null)
      expect(composableResult.currentChapter.value).toBe(null)
    })
  })

  describe('Create Chapter', () => {
    it('should create a new chapter successfully', async () => {
      // Arrange
      const newChapterData = { name: 'New Chapter', state: 'NY' }
      const createdChapter = createMockChapter({ id: 4, ...newChapterData })

      composableResult.chapters.value = createMockChapters(3)
      chapterService.createChapter.mockResolvedValue(createdChapter)

      // Act
      const result = await composableResult.createChapter(newChapterData)

      // Assert
      expect(chapterService.createChapter).toHaveBeenCalledWith(newChapterData)
      expect(result.success).toBe(true)
      expect(result.chapter).toEqual(createdChapter)
      expect(composableResult.chapters.value).toHaveLength(4)
      expect(composableResult.chapters.value).toContain(createdChapter)
    })

    it('should handle creation errors', async () => {
      // Arrange
      const errorMessage = 'Validation failed'
      chapterService.createChapter.mockRejectedValue(new Error(errorMessage))

      // Act
      const result = await composableResult.createChapter({})

      // Assert
      expect(result.success).toBe(false)
      expect(result.error).toBe(errorMessage)
    })
  })

  describe('Update Chapter', () => {
    it('should update an existing chapter successfully', async () => {
      // Arrange
      const existingChapters = createMockChapters(3)
      const updatedData = { name: 'Updated Name' }
      const updatedChapter = { ...existingChapters[0], ...updatedData }

      composableResult.chapters.value = existingChapters
      composableResult.currentChapter.value = existingChapters[0]
      chapterService.updateChapter.mockResolvedValue(updatedChapter)

      // Act
      const result = await composableResult.updateChapter(1, updatedData)

      // Assert
      expect(chapterService.updateChapter).toHaveBeenCalledWith(1, updatedData)
      expect(result.success).toBe(true)
      expect(composableResult.chapters.value[0]).toEqual(updatedChapter)
      expect(composableResult.currentChapter.value).toEqual(updatedChapter)
    })

    it('should handle update errors', async () => {
      // Arrange
      chapterService.updateChapter.mockRejectedValue(new Error('Update failed'))

      // Act
      const result = await composableResult.updateChapter(1, {})

      // Assert
      expect(result.success).toBe(false)
      expect(result.error).toBe('Update failed')
    })
  })

  describe('Delete Chapter', () => {
    it('should delete a chapter successfully', async () => {
      // Arrange
      const chapters = createMockChapters(3)
      composableResult.chapters.value = chapters
      composableResult.currentChapter.value = chapters[0]
      chapterService.deleteChapter.mockResolvedValue({ success: true, id: 1 })

      // Act
      const result = await composableResult.deleteChapter(1)

      // Assert
      expect(chapterService.deleteChapter).toHaveBeenCalledWith(1)
      expect(result.success).toBe(true)
      expect(composableResult.chapters.value).toHaveLength(2)
      expect(composableResult.chapters.value.find(c => c.id === 1)).toBeUndefined()
      expect(composableResult.currentChapter.value).toBe(null)
    })
  })

  describe('Computed Properties', () => {
    beforeEach(() => {
      const mockChapters = [
        createMockChapter({ id: 1, state: 'CA' }),
        createMockChapter({ id: 2, state: 'NY' }),
        createMockChapter({ id: 3, state: 'CA' })
      ]
      composableResult.chapters.value = mockChapters
    })

    it('should compute available states correctly', () => {
      expect(composableResult.availableStates.value).toEqual(['CA', 'NY'])
    })

    it('should compute hasChapters correctly', () => {
      expect(composableResult.hasChapters.value).toBe(true)

      composableResult.chapters.value = []
      expect(composableResult.hasChapters.value).toBe(false)
    })

    it('should compute chapter statistics', async () => {
      // Arrange
      const mockStats = { totalChapters: 3, activeChapters: 3 }
      chapterService.calculateStatistics.mockReturnValue(mockStats)

      // Act & Assert
      expect(composableResult.chapterStatistics.value).toEqual(mockStats)
    })
  })

  describe('Search and Filtering', () => {
    it('should search chapters with criteria', async () => {
      // Arrange
      const searchCriteria = { searchTerm: 'UCLA', state: 'CA' }
      const searchResults = [createMockChapter({ name: 'UCLA' })]
      chapterService.searchChapters.mockResolvedValue(searchResults)

      // Act
      const result = await composableResult.searchChapters(searchCriteria)

      // Assert
      expect(chapterService.searchChapters).toHaveBeenCalledWith(searchCriteria)
      expect(result).toEqual(searchResults)
      expect(composableResult.chapters.value).toEqual(searchResults)
    })
  })

  describe('Utility Methods', () => {
    beforeEach(() => {
      const mockChapters = createMockChapters(5)
      composableResult.chapters.value = mockChapters
    })

    it('should find chapter by ID', () => {
      const chapter = composableResult.findChapterById(2)
      expect(chapter).toBeDefined()
      expect(chapter.id).toBe(2)
    })

    it('should return undefined for non-existent ID', () => {
      const chapter = composableResult.findChapterById(999)
      expect(chapter).toBeUndefined()
    })

    it('should find chapters by state', () => {
      const caChapters = composableResult.findChaptersByState('CA')
      expect(caChapters.length).toBeGreaterThan(0)
      caChapters.forEach(chapter => {
        expect(chapter.state).toBe('CA')
      })
    })
  })

  describe('State Management', () => {
    it('should reset state correctly', () => {
      // Arrange
      composableResult.chapters.value = createMockChapters(3)
      composableResult.currentChapter.value = createMockChapter()
      composableResult.searchTerm.value = 'test'

      // Act
      composableResult.resetState()

      // Assert
      expect(composableResult.chapters.value).toEqual([])
      expect(composableResult.currentChapter.value).toBe(null)
      expect(composableResult.loading.value).toBe(false)
      expect(composableResult.searchTerm.value).toBe('')
    })

    it('should clear filters correctly', () => {
      // Arrange
      composableResult.searchTerm.value = 'test search'
      composableResult.selectedState.value = 'CA'

      // Act
      composableResult.clearFilters()

      // Assert
      expect(composableResult.searchTerm.value).toBe('')
      expect(composableResult.selectedState.value).toBe('')
    })
  })

  describe('Error Handling', () => {
    it('should handle network errors gracefully', async () => {
      // Arrange
      const networkError = new Error('Network error')
      networkError.code = 'ECONNABORTED'
      chapterService.getAllChapters.mockRejectedValue(networkError)

      // Act
      await composableResult.loadChapters()

      // Assert
      expect(composableResult.loading.value).toBe(false)
      expect(composableResult.chapters.value).toEqual([])
    })

    it('should clear errors when operations succeed', async () => {
      // Arrange
      chapterService.getAllChapters.mockResolvedValue(createMockChapters(2))

      // Act
      await composableResult.loadChapters()

      // Assert - should not have any errors
      expect(composableResult.hasError.value).toBe(false)
    })
  })
})