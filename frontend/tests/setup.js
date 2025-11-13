/**
 * Vitest Global Test Setup
 * Configures testing environment for Vue 3 components and composables
 */

import { vi } from 'vitest'
import { config } from '@vue/test-utils'

// Configure Vue Test Utils globally
config.global.stubs = {
  // Stub router-link and router-view for routing tests
  'router-link': true,
  'router-view': true,
  // Stub transitions and keep-alive
  transition: true,
  'keep-alive': true
}

// Global mocks for browser APIs
Object.defineProperty(window, 'localStorage', {
  value: {
    getItem: vi.fn(() => null),
    setItem: vi.fn(() => null),
    removeItem: vi.fn(() => null),
    clear: vi.fn(() => null),
  },
  writable: true,
})

Object.defineProperty(window, 'sessionStorage', {
  value: {
    getItem: vi.fn(() => null),
    setItem: vi.fn(() => null),
    removeItem: vi.fn(() => null),
    clear: vi.fn(() => null),
  },
  writable: true,
})

// Mock console methods to reduce noise in tests
global.console = {
  ...console,
  // Uncomment to silence console outputs
  // log: vi.fn(),
  // debug: vi.fn(),
  // info: vi.fn(),
  warn: vi.fn(),
  error: vi.fn(),
}

// Mock window.location
delete window.location
window.location = {
  href: 'http://localhost:3000',
  origin: 'http://localhost:3000',
  protocol: 'http:',
  host: 'localhost:3000',
  hostname: 'localhost',
  port: '3000',
  pathname: '/',
  search: '',
  hash: '',
  assign: vi.fn(),
  replace: vi.fn(),
  reload: vi.fn(),
}

// Mock window.navigator
Object.defineProperty(window, 'navigator', {
  value: {
    userAgent: 'Mozilla/5.0 (jsdom test environment)',
    language: 'en-US',
    languages: ['en-US', 'en'],
    clipboard: {
      writeText: vi.fn(() => Promise.resolve()),
      readText: vi.fn(() => Promise.resolve('')),
    },
  },
  writable: true,
})

// Mock ResizeObserver
global.ResizeObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn(),
}))

// Mock IntersectionObserver
global.IntersectionObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn(),
}))

// Clean up after each test
afterEach(() => {
  vi.clearAllMocks()
  localStorage.clear()
  sessionStorage.clear()
})