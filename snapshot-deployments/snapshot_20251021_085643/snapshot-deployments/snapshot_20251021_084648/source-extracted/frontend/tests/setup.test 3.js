import { describe, it, expect } from 'vitest'

describe('Test Setup', () => {
  it('should run basic test', () => {
    expect(1 + 1).toBe(2)
  })

  it('should have access to DOM', () => {
    expect(document).toBeDefined()
    expect(window).toBeDefined()
  })

  it('should have localStorage mock', () => {
    expect(localStorage.setItem).toBeDefined()
    expect(localStorage.getItem).toBeDefined()
  })
})