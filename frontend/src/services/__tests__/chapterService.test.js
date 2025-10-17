import { describe, it, expect, beforeEach, vi } from 'vitest'
import { chapterService } from '../chapterService'
import api from '../api'

// Mock the API module
vi.mock('../api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
}))

describe('ChapterService', () => {
  const mockChapter = {
    id: 1,
    name: 'Test Chapter',
    universityName: 'Test University',
    city: 'Test City',
    state: 'CA',
    foundedDate: '2023-01-01',
    description: 'A test chapter',
    active: true
  }

  const mockChapters = [mockChapter, { ...mockChapter, id: 2, name: 'Chapter 2' }]

  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getAllChapters', () => {
    it('returns all chapters successfully', async () => {
      api.get.mockResolvedValue({ data: mockChapters })

      const result = await chapterService.getAllChapters()

      expect(api.get).toHaveBeenCalledWith('/chapters')
      expect(result).toEqual(mockChapters)
    })

    it('throws error when API call fails', async () => {
      const error = new Error('API Error')
      api.get.mockRejectedValue(error)

      await expect(chapterService.getAllChapters()).rejects.toThrow('API Error')
      expect(api.get).toHaveBeenCalledWith('/chapters')
    })
  })

  describe('getChapterById', () => {
    it('returns chapter by ID successfully', async () => {
      api.get.mockResolvedValue({ data: mockChapter })

      const result = await chapterService.getChapterById(1)

      expect(api.get).toHaveBeenCalledWith('/chapters/1')
      expect(result).toEqual(mockChapter)
    })

    it('throws error when chapter not found', async () => {
      const error = new Error('Chapter not found')
      api.get.mockRejectedValue(error)

      await expect(chapterService.getChapterById(999)).rejects.toThrow('Chapter not found')
      expect(api.get).toHaveBeenCalledWith('/chapters/999')
    })
  })

  describe('createChapter', () => {
    it('creates chapter successfully', async () => {
      const newChapter = { ...mockChapter }
      delete newChapter.id
      api.post.mockResolvedValue({ data: mockChapter })

      const result = await chapterService.createChapter(newChapter)

      expect(api.post).toHaveBeenCalledWith('/chapters', newChapter)
      expect(result).toEqual(mockChapter)
    })

    it('throws error when creation fails', async () => {
      const newChapter = { name: 'Test Chapter' }
      const error = new Error('Validation failed')
      api.post.mockRejectedValue(error)

      await expect(chapterService.createChapter(newChapter)).rejects.toThrow('Validation failed')
      expect(api.post).toHaveBeenCalledWith('/chapters', newChapter)
    })
  })

  describe('updateChapter', () => {
    it('updates chapter successfully', async () => {
      const updatedChapter = { ...mockChapter, name: 'Updated Chapter' }
      api.put.mockResolvedValue({ data: updatedChapter })

      const result = await chapterService.updateChapter(1, updatedChapter)

      expect(api.put).toHaveBeenCalledWith('/chapters/1', updatedChapter)
      expect(result).toEqual(updatedChapter)
    })

    it('throws error when update fails', async () => {
      const error = new Error('Update failed')
      api.put.mockRejectedValue(error)

      await expect(chapterService.updateChapter(1, mockChapter)).rejects.toThrow('Update failed')
      expect(api.put).toHaveBeenCalledWith('/chapters/1', mockChapter)
    })
  })

  describe('deleteChapter', () => {
    it('deletes chapter successfully', async () => {
      api.delete.mockResolvedValue({ data: true })

      const result = await chapterService.deleteChapter(1)

      expect(api.delete).toHaveBeenCalledWith('/chapters/1')
      expect(result).toBe(true)
    })

    it('throws error when deletion fails', async () => {
      const error = new Error('Delete failed')
      api.delete.mockRejectedValue(error)

      await expect(chapterService.deleteChapter(1)).rejects.toThrow('Delete failed')
      expect(api.delete).toHaveBeenCalledWith('/chapters/1')
    })
  })

  describe('searchChapters', () => {
    it('searches chapters with parameters', async () => {
      const searchParams = { name: 'Test', state: 'CA' }
      api.get.mockResolvedValue({ data: [mockChapter] })

      const result = await chapterService.searchChapters(searchParams)

      expect(api.get).toHaveBeenCalledWith('/chapters/search', { params: searchParams })
      expect(result).toEqual([mockChapter])
    })

    it('handles empty search results', async () => {
      api.get.mockResolvedValue({ data: [] })

      const result = await chapterService.searchChapters({ name: 'NonExistent' })

      expect(result).toEqual([])
    })
  })
})