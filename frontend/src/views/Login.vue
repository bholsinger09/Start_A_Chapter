<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-5">
        <div class="card">
          <div class="card-header">
            <h3 class="text-center">
              <i class="bi bi-box-arrow-in-right me-2"></i>
              Sign In
            </h3>
          </div>
          <div class="card-body">
            <form @submit.prevent="handleSubmit">
              <!-- Email -->
              <div class="mb-3">
                <label for="email" class="form-label">Email or Username</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="email"
                  v-model="form.email"
                  required
                  :disabled="isLoading"
                  placeholder="Enter your email or username"
                >
              </div>

              <!-- Password -->
              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="password"
                  v-model="form.password"
                  required
                  :disabled="isLoading"
                  placeholder="Enter your password"
                >
                <small class="form-text text-muted">
                  Enter your password (minimum 6 characters required).
                </small>
              </div>

              <!-- Error Message -->
              <div v-if="error" class="alert alert-danger">
                <i class="bi bi-exclamation-triangle me-2"></i>
                {{ error }}
              </div>

              <!-- Success Message -->
              <div v-if="success" class="alert alert-success">
                <i class="bi bi-check-circle me-2"></i>
                {{ success }}
              </div>

              <!-- Submit Button -->
              <button 
                type="submit" 
                class="btn btn-primary w-100 mb-3"
                :disabled="isLoading"
              >
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="bi bi-box-arrow-in-right me-2"></i>
                {{ isLoading ? 'Signing in...' : 'Sign In' }}
              </button>
            </form>

            <!-- Register Link -->
            <div class="text-center">
              <p class="mb-0">
                Don't have an account? 
                <router-link to="/register" class="text-decoration-none">
                  <i class="bi bi-person-plus me-1"></i>
                  Register here
                </router-link>
              </p>
            </div>
          </div>
        </div>

        <!-- Demo Users Info -->
        <div class="card mt-4">
          <div class="card-header">
            <h6 class="mb-0">
              <i class="bi bi-info-circle me-2"></i>
              Demo Information
            </h6>
          </div>
          <div class="card-body">
            <p class="mb-2"><strong>Try logging in with any registered email:</strong></p>
            <ul class="list-unstyled mb-0">
              <li><i class="bi bi-envelope me-2"></i>Any member email from the Members page</li>
              <li><i class="bi bi-key me-2"></i>Any password (demo mode)</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import { useAuth } from '@/composables/useAuth'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const { setUser } = useAuth()
    
    const form = ref({
      email: '',
      password: ''
    })
    
    const isLoading = ref(false)
    const error = ref('')
    const success = ref('')

    // Handle form submission
    const handleSubmit = async () => {
      try {
        isLoading.value = true
        error.value = ''
        success.value = ''

        // Validate form
        if (!form.value.email.trim()) {
          error.value = 'Email is required'
          return
        }
        if (!form.value.password.trim()) {
          error.value = 'Password is required'
          return
        }

        // Submit login
        const response = await api.post('/api/auth/login', {
          email: form.value.email.trim(),
          password: form.value.password
        })

        if (response.data.status === 'success') {
          success.value = response.data.message
          
          // Use auth composable to set user (handles localStorage and reactivity)
          setUser(response.data.member)
          
          // Redirect to dashboard after successful login
          setTimeout(() => {
            router.push('/')
          }, 1000)
        } else {
          error.value = response.data.message || 'Login failed'
        }

      } catch (err) {
        console.error('Login error:', err)
        if (err.response?.data?.error) {
          error.value = err.response.data.error
        } else {
          error.value = 'Login failed. Please check your credentials and try again.'
        }
      } finally {
        isLoading.value = false
      }
    }

    return {
      form,
      isLoading,
      error,
      success,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.card {
  border-radius: 0.5rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.card-header {
  background-color: #0d6efd;
  color: white;
  border-radius: 0.5rem 0.5rem 0 0;
}

.form-control:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}

.btn-primary {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

.btn-primary:hover {
  background-color: #0b5ed7;
  border-color: #0a58ca;
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
}
</style>
