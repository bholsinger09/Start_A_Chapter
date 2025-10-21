import { mount, shallowMount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'

// Test utilities for consistent testing across components

/**
 * Creates a mock router for testing
 */
export function createMockRouter(routes = []) {
  return createRouter({
    history: createWebHistory(),
    routes: [
      { path: '/', name: 'Home', component: { template: '<div>Home</div>' } },
      { path: '/login', name: 'Login', component: { template: '<div>Login</div>' } },
      { path: '/dashboard', name: 'Dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/chapters', name: 'Chapters', component: { template: '<div>Chapters</div>' } },
      { path: '/members', name: 'Members', component: { template: '<div>Members</div>' } },
      { path: '/events', name: 'Events', component: { template: '<div>Events</div>' } },
      ...routes
    ]
  })
}

/**
 * Creates default mounting options with common mocks
 */
export function createMountingOptions(overrides = {}) {
  const router = createMockRouter()
  
  return {
    global: {
      plugins: [router],
      mocks: {
        $router: router,
        $route: {
          params: {},
          query: {},
          path: '/',
          name: 'Home'
        }
      },
      stubs: {
        'router-link': {
          template: '<a><slot /></a>',
          props: ['to']
        },
        'router-view': {
          template: '<div><slot /></div>'
        }
      }
    },
    ...overrides
  }
}

/**
 * Helper to mount component with common options
 */
export function mountComponent(component, options = {}) {
  const mountingOptions = createMountingOptions(options)
  return mount(component, mountingOptions)
}

/**
 * Helper to shallow mount component with common options
 */
export function shallowMountComponent(component, options = {}) {
  const mountingOptions = createMountingOptions(options)
  return shallowMount(component, mountingOptions)
}

/**
 * Mock API responses
 */
export const mockApiResponses = {
  chapters: [
    {
      id: 1,
      name: 'Test Chapter',
      universityName: 'Test University',
      city: 'Test City',
      state: 'CA',
      foundedDate: '2023-01-01',
      description: 'A test chapter',
      active: true
    }
  ],
  members: [
    {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@test.com',
      username: 'johndoe',
      role: 'MEMBER',
      active: true,
      chapter: { id: 1, name: 'Test Chapter' }
    }
  ],
  events: [
    {
      id: 1,
      title: 'Test Event',
      description: 'A test event',
      eventDateTime: '2024-01-01T10:00:00',
      location: 'Test Location',
      maxAttendees: 50,
      currentAttendees: 10,
      type: 'MEETING',
      active: true,
      chapter: { id: 1, name: 'Test Chapter' }
    }
  ],
  auth: {
    success: true,
    message: 'Authentication successful',
    action: 'login',
    user: {
      id: 1,
      username: 'testuser',
      email: 'test@example.com',
      firstName: 'Test',
      lastName: 'User'
    }
  }
}

/**
 * Mock fetch implementation for testing API calls
 */
export function mockFetch(data, ok = true, status = 200) {
  return jest.fn().mockResolvedValue({
    ok,
    status,
    json: async () => data,
    text: async () => JSON.stringify(data)
  })
}

/**
 * Helper to wait for DOM updates
 */
export async function nextTick() {
  await new Promise(resolve => setTimeout(resolve, 0))
}

/**
 * Helper to trigger and wait for form submission
 */
export async function submitForm(wrapper, formSelector = 'form') {
  const form = wrapper.find(formSelector)
  await form.trigger('submit')
  await nextTick()
}

/**
 * Helper to fill form inputs
 */
export async function fillForm(wrapper, values) {
  for (const [selector, value] of Object.entries(values)) {
    const input = wrapper.find(selector)
    if (input.exists()) {
      await input.setValue(value)
    }
  }
  await nextTick()
}

/**
 * Setup localStorage with user data for authentication tests
 */
export function setupAuthenticatedUser(userData = mockApiResponses.auth.user) {
  localStorage.setItem('user', JSON.stringify(userData))
  return userData
}

/**
 * Clear authentication state
 */
export function clearAuthState() {
  localStorage.removeItem('user')
}