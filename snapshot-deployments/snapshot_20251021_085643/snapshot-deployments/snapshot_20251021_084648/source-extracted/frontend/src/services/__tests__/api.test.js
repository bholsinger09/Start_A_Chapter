import { describe, it, expect, beforeEach, vi } from 'vitest'

// This test validates that the API module exports exist and are configured correctly
describe('API Service', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should export an API instance', async () => {
    const api = await import('../api')
    expect(api.default).toBeDefined()
    // API instance could be a function in some axios configurations
    expect(['object', 'function']).toContain(typeof api.default)
  })

  it('should have axios methods available', async () => {
    const api = await import('../api')
    expect(api.default.get).toBeDefined()
    expect(api.default.post).toBeDefined()
    expect(api.default.put).toBeDefined()
    expect(api.default.delete).toBeDefined()
  })

  it('should be configured with interceptors', async () => {
    const api = await import('../api')
    expect(api.default.interceptors).toBeDefined()
    expect(api.default.interceptors.request).toBeDefined()
    expect(api.default.interceptors.response).toBeDefined()
  })
})