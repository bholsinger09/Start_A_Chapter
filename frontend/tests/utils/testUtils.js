/**
 * Test Utilities - Helper functions for testing Vue components and composables
 * Following Clean Code principles for readable and maintainable tests
 */

import { mount, shallowMount } from '@vue/test-utils'
import { nextTick } from 'vue'
import { vi } from 'vitest'

/**
 * Factory function to create mock chapter data
 */
export function createMockChapter(overrides = {}) {
  return {
    id: 1,
    name: 'UCLA',
    universityName: 'University of California, Los Angeles',
    city: 'Los Angeles',
    state: 'CA',
    description: 'Test chapter description',
    active: true,
    members: [],
    events: [],
    createdAt: '2023-01-01T00:00:00.000Z',
    updatedAt: '2023-01-01T00:00:00.000Z',
    ...overrides
  }
}

/**
 * Factory function to create mock member data
 */
export function createMockMember(overrides = {}) {
  return {
    id: 1,
    firstName: 'John',
    lastName: 'Smith',
    email: 'john.smith@ucla.edu',
    username: 'johnsmith',
    phoneNumber: '310-555-0101',
    role: 'PRESIDENT',
    active: true,
    major: 'Computer Science',
    graduationYear: '2024',
    chapterId: 1,
    createdAt: '2023-01-01T00:00:00.000Z',
    updatedAt: '2023-01-01T00:00:00.000Z',
    ...overrides
  }
}

/**
 * Factory function to create multiple mock chapters
 */
export function createMockChapters(count = 3) {
  const universities = [
    { name: 'UCLA', city: 'Los Angeles', state: 'CA' },
    { name: 'Stanford', city: 'Stanford', state: 'CA' },
    { name: 'USC', city: 'Los Angeles', state: 'CA' },
    { name: 'Berkeley', city: 'Berkeley', state: 'CA' },
    { name: 'Harvard', city: 'Cambridge', state: 'MA' }
  ]

  return Array.from({ length: count }, (_, index) => {
    const uni = universities[index % universities.length]
    return createMockChapter({
      id: index + 1,
      name: uni.name,
      universityName: `${uni.name} University`,
      city: uni.city,
      state: uni.state
    })
  })
}

/**
 * Factory function to create multiple mock members
 */
export function createMockMembers(count = 5) {
  const names = [
    { firstName: 'John', lastName: 'Smith', role: 'PRESIDENT' },
    { firstName: 'Sarah', lastName: 'Johnson', role: 'VICE_PRESIDENT' },
    { firstName: 'Mike', lastName: 'Davis', role: 'TREASURER' },
    { firstName: 'Emily', lastName: 'Wilson', role: 'SECRETARY' },
    { firstName: 'Alex', lastName: 'Garcia', role: 'MEMBER' }
  ]

  return Array.from({ length: count }, (_, index) => {
    const person = names[index % names.length]
    return createMockMember({
      id: index + 1,
      firstName: person.firstName,
      lastName: person.lastName,
      email: `${person.firstName.toLowerCase()}.${person.lastName.toLowerCase()}@test.edu`,
      role: person.role
    })
  })
}

/**
 * Mock API service responses
 */
export function createMockApiService() {
  return {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    patch: vi.fn()
  }
}

/**
 * Mock router for component testing
 */
export function createMockRouter() {
  return {
    push: vi.fn(),
    replace: vi.fn(),
    go: vi.fn(),
    back: vi.fn(),
    forward: vi.fn(),
    currentRoute: {
      value: {
        path: '/',
        params: {},
        query: {},
        meta: {}
      }
    }
  }
}

/**
 * Helper to mount components with common options
 */
export function mountComponent(component, options = {}) {
  const defaultOptions = {
    global: {
      stubs: {
        'router-link': true,
        'router-view': true,
        transition: true
      },
      mocks: {
        $router: createMockRouter(),
        $route: {
          path: '/',
          params: {},
          query: {},
          meta: {}
        }
      }
    }
  }

  return mount(component, {
    ...defaultOptions,
    ...options,
    global: {
      ...defaultOptions.global,
      ...options.global
    }
  })
}

/**
 * Helper to shallow mount components with common options
 */
export function shallowMountComponent(component, options = {}) {
  const defaultOptions = {
    global: {
      stubs: {
        'router-link': true,
        'router-view': true
      }
    }
  }

  return shallowMount(component, {
    ...defaultOptions,
    ...options
  })
}

/**
 * Helper to wait for async operations in tests
 */
export async function waitForAsync(fn, timeout = 1000) {
  const start = Date.now()

  while (Date.now() - start < timeout) {
    try {
      const result = fn()
      if (result) return result
    } catch (error) {
      // Continue waiting
    }

    await nextTick()
    await new Promise(resolve => setTimeout(resolve, 10))
  }

  throw new Error(`Timeout waiting for condition after ${timeout}ms`)
}

/**
 * Helper to simulate user input events
 */
export async function simulateUserInput(wrapper, selector, value) {
  const input = wrapper.find(selector)
  await input.setValue(value)
  await input.trigger('input')
  await nextTick()
}

/**
 * Helper to simulate user clicks
 */
export async function simulateUserClick(wrapper, selector) {
  const element = wrapper.find(selector)
  await element.trigger('click')
  await nextTick()
}

/**
 * Helper to simulate keyboard events
 */
export async function simulateKeyboardEvent(wrapper, selector, key, type = 'keydown') {
  const element = wrapper.find(selector)
  await element.trigger(type, { key })
  await nextTick()
}

/**
 * Helper to test composable functions
 */
export function testComposable(composableFn, ...args) {
  let result
  let error

  const TestComponent = {
    setup() {
      try {
        result = composableFn(...args)
      } catch (err) {
        error = err
      }
      return () => null
    }
  }

  const wrapper = mount(TestComponent)

  return {
    result,
    error,
    wrapper,
    unmount: () => wrapper.unmount()
  }
}

/**
 * Helper to create mock fetch responses
 */
export function createMockFetchResponse(data, status = 200, statusText = 'OK') {
  return Promise.resolve({
    ok: status >= 200 && status < 300,
    status,
    statusText,
    json: () => Promise.resolve(data),
    text: () => Promise.resolve(JSON.stringify(data)),
    headers: new Headers(),
    url: 'http://localhost/api/test'
  })
}

/**
 * Helper to create mock error responses
 */
export function createMockErrorResponse(message = 'API Error', status = 500) {
  const error = new Error(message)
  error.status = status
  error.response = {
    status,
    data: { message },
    statusText: status === 404 ? 'Not Found' : 'Internal Server Error'
  }
  return Promise.reject(error)
}

/**
 * Test assertion helpers
 */
export const assertions = {
  /**
   * Assert that an element is visible
   */
  toBeVisible(wrapper, selector) {
    const element = wrapper.find(selector)
    expect(element.exists()).toBe(true)
    expect(element.isVisible()).toBe(true)
  },

  /**
   * Assert that an element has specific text
   */
  toHaveText(wrapper, selector, expectedText) {
    const element = wrapper.find(selector)
    expect(element.exists()).toBe(true)
    expect(element.text()).toBe(expectedText)
  },

  /**
   * Assert that an element contains text
   */
  toContainText(wrapper, selector, expectedText) {
    const element = wrapper.find(selector)
    expect(element.exists()).toBe(true)
    expect(element.text()).toContain(expectedText)
  },

  /**
   * Assert that a form field has a specific value
   */
  toHaveValue(wrapper, selector, expectedValue) {
    const element = wrapper.find(selector)
    expect(element.exists()).toBe(true)
    expect(element.element.value).toBe(expectedValue)
  },

  /**
   * Assert that an event was emitted
   */
  toHaveEmitted(wrapper, eventName, expectedPayload) {
    const emitted = wrapper.emitted(eventName)
    expect(emitted).toBeTruthy()
    if (expectedPayload !== undefined) {
      expect(emitted[emitted.length - 1]).toEqual([expectedPayload])
    }
  }
}

/**
 * Performance testing helpers
 */
export const performance = {
  /**
   * Measure execution time of a function
   */
  async measureTime(fn) {
    const start = performance.now()
    const result = await fn()
    const end = performance.now()
    return {
      result,
      duration: end - start
    }
  },

  /**
   * Assert that a function executes within a time limit
   */
  async expectWithinTime(fn, maxDuration) {
    const { duration } = await this.measureTime(fn)
    expect(duration).toBeLessThan(maxDuration)
  }
}