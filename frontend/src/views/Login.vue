<template>
  <div class="container-fluid min-vh-100 d-flex">
    <div class="row g-0 flex-fill">
      <!-- Left Side - Branding -->
      <div class="col-lg-6 d-none d-lg-flex align-items-center justify-content-center bg-primary text-white">
        <div class="text-center px-4">
          <div class="mb-4">
            <i class="bi bi-people-fill display-1 opacity-75"></i>
          </div>
          <h1 class="display-4 fw-bold mb-3">StartAChapter</h1>
          <p class="lead mb-4">Empowering Student Organizations</p>
          <div class="mb-4">
            <div class="d-flex align-items-center justify-content-center mb-3">
              <i class="bi bi-check-circle-fill me-3 text-success"></i>
              <span>Manage chapters and members</span>
            </div>
            <div class="d-flex align-items-center justify-content-center mb-3">
              <i class="bi bi-check-circle-fill me-3 text-success"></i>
              <span>Organize events and activities</span>
            </div>
            <div class="d-flex align-items-center justify-content-center mb-3">
              <i class="bi bi-check-circle-fill me-3 text-success"></i>
              <span>Share updates and news</span>
            </div>
          </div>
          <p class="text-light opacity-75">
            Join thousands of student leaders building stronger campus communities.
          </p>
        </div>
      </div>

      <!-- Right Side - Login Form -->
      <div class="col-lg-6 d-flex align-items-center justify-content-center bg-light">
        <div class="w-100 px-4" style="max-width: 400px;">
          <!-- Mobile Logo -->
          <div class="text-center mb-4 d-lg-none">
            <h2 class="text-primary fw-bold">
              <i class="bi bi-people-fill me-2"></i>StartAChapter
            </h2>
          </div>

          <!-- Login Form -->
          <div class="card shadow border-0">
            <div class="card-body p-4">
              <div class="text-center mb-4">
                <h3 class="fw-bold text-dark mb-2">Welcome Back</h3>
                <p class="text-muted">Sign in to access your dashboard</p>
              </div>

              <!-- Error Message -->
              <div v-if="error" class="alert alert-danger" role="alert">
                <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
              </div>

              <!-- Success Message -->
              <div v-if="success" class="alert alert-success" role="alert">
                <i class="bi bi-check-circle me-2"></i>{{ success }}
              </div>

              <form @submit.prevent="handleLogin">
                <div class="mb-3">
                  <label for="usernameOrEmail" class="form-label fw-semibold">
                    <i class="bi bi-person me-2"></i>Username or Email
                  </label>
                  <input
                    type="text"
                    class="form-control form-control-lg"
                    id="usernameOrEmail"
                    v-model="credentials.usernameOrEmail"
                    required
                    :class="{ 'is-invalid': errors.usernameOrEmail }"
                    placeholder="Enter username or email"
                    autocomplete="username"
                  >
                  <div v-if="errors.usernameOrEmail" class="invalid-feedback">
                    {{ errors.usernameOrEmail }}
                  </div>
                </div>

                <div class="mb-3">
                  <label for="password" class="form-label fw-semibold">
                    <i class="bi bi-lock me-2"></i>Password
                  </label>
                  <div class="position-relative">
                    <input
                      :type="showPassword ? 'text' : 'password'"
                      class="form-control form-control-lg"
                      id="password"
                      v-model="credentials.password"
                      required
                      :class="{ 'is-invalid': errors.password }"
                      placeholder="Enter password"
                      autocomplete="current-password"
                    >
                    <button
                      type="button"
                      class="btn btn-outline-secondary position-absolute top-50 end-0 translate-middle-y me-2"
                      @click="showPassword = !showPassword"
                      style="border: none; background: none;"
                    >
                      <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                    </button>
                  </div>
                  <div v-if="errors.password" class="invalid-feedback">
                    {{ errors.password }}
                  </div>
                </div>

                <div class="mb-3 form-check">
                  <input
                    type="checkbox"
                    class="form-check-input"
                    id="rememberMe"
                    v-model="credentials.rememberMe"
                  >
                  <label class="form-check-label" for="rememberMe">
                    Remember me for 30 days
                  </label>
                </div>

                <button
                  type="submit"
                  class="btn btn-primary btn-lg w-100 mb-3"
                  :disabled="loading"
                >
                  <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="bi bi-box-arrow-in-right me-2"></i>
                  {{ loading ? 'Signing in...' : 'Sign In' }}
                </button>
              </form>

              <div class="text-center">
                <a href="#" class="text-decoration-none" @click.prevent="handleForgotPassword">
                  Forgot your password?
                </a>
              </div>
            </div>
          </div>

          <!-- Register Link -->
          <div class="text-center mt-4">
            <p class="text-muted">
              Don't have an account?
              <router-link to="/register" class="text-primary text-decoration-none fw-semibold">
                Sign up here
              </router-link>
            </p>
          </div>

          <!-- Demo Account Info -->
          <div class="card bg-info text-white mt-4" v-if="showDemoInfo">
            <div class="card-body p-3">
              <h6 class="fw-semibold mb-2">
                <i class="bi bi-info-circle me-2"></i>Demo Account
              </h6>
              <p class="small mb-2">Use these credentials to explore:</p>
              <div class="d-flex justify-content-between small">
                <span>Username: demo</span>
                <span>Password: demo123</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from '../services/api'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const credentials = ref({
      usernameOrEmail: '',
      password: '',
      rememberMe: false
    })
    
    const loading = ref(false)
    const error = ref('')
    const success = ref('')
    const errors = ref({})
    const showPassword = ref(false)
    const showDemoInfo = ref(process.env.NODE_ENV === 'development')

    // Check if user is already logged in
    const checkExistingAuth = () => {
      const token = localStorage.getItem('authToken')
      const user = localStorage.getItem('user')
      
      if (token && user) {
        // User is already authenticated, redirect to dashboard
        const redirectTo = route.query.redirect || '/dashboard'
        router.push(redirectTo)
      }
    }

    const validateForm = () => {
      errors.value = {}

      if (!credentials.value.usernameOrEmail.trim()) {
        errors.value.usernameOrEmail = 'Username or email is required'
      }

      if (!credentials.value.password) {
        errors.value.password = 'Password is required'
      } else if (credentials.value.password.length < 6) {
        errors.value.password = 'Password must be at least 6 characters'
      }

      return Object.keys(errors.value).length === 0
    }

    const handleLogin = async () => {
      if (!validateForm()) return

      loading.value = true
      error.value = ''
      success.value = ''

      try {
        const loginData = {
          usernameOrEmail: credentials.value.usernameOrEmail.trim(),
          password: credentials.value.password
        }

        const response = await axios.post('/api/auth/login', loginData)
        
        if (response.data.token && response.data.user) {
          // Store authentication data
          const tokenExpiry = credentials.value.rememberMe 
            ? Date.now() + (30 * 24 * 60 * 60 * 1000) // 30 days
            : Date.now() + (24 * 60 * 60 * 1000) // 24 hours
          
          localStorage.setItem('authToken', response.data.token)
          localStorage.setItem('user', JSON.stringify(response.data.user))
          localStorage.setItem('tokenExpiry', tokenExpiry.toString())
          
          // Set axios default header
          axios.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`
          
          success.value = 'Login successful! Redirecting...'
          
          // Trigger storage event for other components
          window.dispatchEvent(new Event('storage'))
          
          // Redirect after short delay
          setTimeout(() => {
            const redirectTo = route.query.redirect || '/dashboard'
            router.push(redirectTo)
          }, 1000)
        }
      } catch (err) {
        console.error('Login error:', err)
        
        if (err.response?.status === 401) {
          error.value = 'Invalid username/email or password'
        } else if (err.response?.status === 403) {
          error.value = 'Your account has been disabled. Please contact support.'
        } else if (err.response?.data?.message) {
          error.value = err.response.data.message
        } else {
          error.value = 'Login failed. Please try again.'
        }
      } finally {
        loading.value = false
      }
    }

    const handleForgotPassword = () => {
      // TODO: Implement forgot password functionality
      alert('Forgot password functionality coming soon!\n\nFor now, please contact your chapter administrator for password reset assistance.')
    }

    const fillDemoCredentials = () => {
      credentials.value.usernameOrEmail = 'demo'
      credentials.value.password = 'demo123'
    }

    // Initialize authentication check
    onMounted(() => {
      checkExistingAuth()
      
      // Check for success message from registration
      if (route.query.registered === 'true') {
        success.value = 'Registration successful! Please sign in with your credentials.'
      }
    })

    return {
      credentials,
      loading,
      error,
      success,
      errors,
      showPassword,
      showDemoInfo,
      handleLogin,
      handleForgotPassword,
      fillDemoCredentials,
      validateForm
    }
  }
}
</script>

<style scoped>
.min-vh-100 {
  min-height: 100vh;
}

.form-control:focus {
  border-color: var(--bs-primary);
  box-shadow: 0 0 0 0.25rem rgba(var(--bs-primary-rgb), 0.25);
}

.btn-primary {
  background-color: var(--bs-primary);
  border-color: var(--bs-primary);
}

.btn-primary:hover {
  background-color: #0056b3;
  border-color: #0056b3;
}

.card {
  border-radius: 12px;
}

.bg-primary {
  background: linear-gradient(135deg, var(--bs-primary) 0%, #0056b3 100%);
}

@media (max-width: 991px) {
  .container-fluid {
    padding: 2rem 1rem;
  }
}

/* Password toggle button styling */
.position-relative .btn {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
  height: auto;
}
</style>
