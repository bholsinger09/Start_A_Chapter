/**
 * API utility functions for HTTP requests.
 * Fixes: Duplicated Code smell across Vue components.
 * Single Responsibility: Handle all HTTP communication with backend.
 */

const API_BASE_URL = process.env.VUE_APP_API_URL || 'http://localhost:8080';

/**
 * Error handling utility.
 * Fixes: Duplicated error handling code.
 */
class ApiError extends Error {
  constructor(message, status, data) {
    super(message);
    this.status = status;
    this.data = data;
  }
}

/**
 * Common HTTP request configuration.
 */
const defaultHeaders = {
  'Content-Type': 'application/json',
};

/**
 * Generic request handler with error handling.
 * Fixes: Duplicated fetch() calls and error handling.
 */
async function makeRequest(url, options = {}) {
  try {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      headers: { ...defaultHeaders, ...options.headers },
      ...options,
    });

    if (!response.ok) {
      const errorData = await response.text();
      throw new ApiError(
        `Request failed: ${response.status} ${response.statusText}`,
        response.status,
        errorData
      );
    }

    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      return await response.json();
    }
    
    return await response.text();
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError('Network error or server unavailable', 0, error.message);
  }
}

/**
 * GET request utility.
 */
export async function get(url, params = {}) {
  const searchParams = new URLSearchParams(params);
  const urlWithParams = searchParams.toString() ? `${url}?${searchParams}` : url;
  
  return makeRequest(urlWithParams, {
    method: 'GET',
  });
}

/**
 * POST request utility.
 */
export async function post(url, data = {}) {
  return makeRequest(url, {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

/**
 * PUT request utility.
 */
export async function put(url, data = {}) {
  return makeRequest(url, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

/**
 * DELETE request utility.
 */
export async function del(url) {
  return makeRequest(url, {
    method: 'DELETE',
  });
}

/**
 * Authentication API calls.
 * Fixes: Duplicated authentication request code.
 */
export const authApi = {
  login: (credentials) => post('/api/auth/login', credentials),
  register: (userData) => post('/api/auth/register', userData),
  logout: () => post('/api/auth/logout'),
  checkAuth: () => get('/api/auth/check'),
};

/**
 * Chapter API calls.
 * Fixes: Duplicated chapter request code.
 */
export const chapterApi = {
  getAll: (params) => get('/api/chapters', params),
  getById: (id) => get(`/api/chapters/${id}`),
  create: (chapter) => post('/api/chapters', chapter),
  update: (id, chapter) => put(`/api/chapters/${id}`, chapter),
  delete: (id) => del(`/api/chapters/${id}`),
  search: (query) => get('/api/chapters/search', { q: query }),
};

/**
 * Member API calls.
 * Fixes: Duplicated member request code.
 */
export const memberApi = {
  getAll: (params) => get('/api/members', params),
  getById: (id) => get(`/api/members/${id}`),
  create: (member) => post('/api/members', member),
  update: (id, member) => put(`/api/members/${id}`, member),
  delete: (id) => del(`/api/members/${id}`),
  getByChapter: (chapterId, params) => get(`/api/chapters/${chapterId}/members`, params),
};

/**
 * University API calls.
 * Fixes: Duplicated university request code.
 */
export const universityApi = {
  getAll: (params) => get('/api/universities', params),
  search: (query) => get('/api/universities/search', { q: query }),
};

/**
 * Blog API calls.
 * Fixes: Duplicated blog request code.
 */
export const blogApi = {
  getAll: (params) => get('/api/blog', params),
  getById: (id) => get(`/api/blog/${id}`),
  create: (post) => post('/api/blog', post),
  update: (id, post) => put(`/api/blog/${id}`, post),
  delete: (id) => del(`/api/blog/${id}`),
};

/**
 * Handle API errors consistently.
 * Fixes: Duplicated error handling across components.
 */
export function handleApiError(error, defaultMessage = 'An error occurred') {
  console.error('API Error:', error);
  
  if (error instanceof ApiError) {
    if (error.status === 401) {
      return 'Authentication required. Please login.';
    }
    if (error.status === 403) {
      return 'Access denied. You do not have permission.';
    }
    if (error.status === 404) {
      return 'Resource not found.';
    }
    if (error.status >= 500) {
      return 'Server error. Please try again later.';
    }
    return error.data || error.message || defaultMessage;
  }
  
  return defaultMessage;
}

export { ApiError };