<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">
            <h3 class="text-center">
              <i class="bi bi-person-plus me-2"></i>
              Register for Campus Chapter Organizer
            </h3>
          </div>
          <div class="card-body">
            <form @submit.prevent="handleSubmit">
              <div class="mb-3">
                <label for="firstName" class="form-label">First Name *</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="firstName"
                  v-model="form.firstName"
                  required
                  :disabled="isLoading"
                >
              </div>

              <div class="mb-3">
                <label for="lastName" class="form-label">Last Name *</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="lastName"
                  v-model="form.lastName"
                  required
                  :disabled="isLoading"
                >
              </div>

              <div class="mb-3">
                <label for="email" class="form-label">Email Address *</label>
                <input 
                  type="email" 
                  class="form-control" 
                  id="email"
                  v-model="form.email"
                  required
                  :disabled="isLoading"
                >
              </div>

              <div class="mb-3">
                <label for="password" class="form-label">Password *</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="password"
                  v-model="form.password"
                  required
                  :disabled="isLoading"
                  minlength="6"
                  placeholder="Minimum 6 characters"
                >
              </div>

              <div class="mb-3">
                <label for="phoneNumber" class="form-label">Phone Number</label>
                <input 
                  type="tel" 
                  class="form-control" 
                  id="phoneNumber"
                  v-model="form.phoneNumber"
                  :disabled="isLoading"
                >
              </div>

              <div class="mb-4">
                <h6 class="text-primary">
                  <i class="bi bi-mortarboard me-2"></i>Academic Information
                </h6>
                
                <div class="mb-3">
                  <label for="major" class="form-label">Major / Field of Study</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="major"
                    v-model="form.major"
                    placeholder="e.g., Computer Science, Business Administration"
                    :disabled="isLoading"
                  >
                </div>

                <div class="mb-3">
                  <label for="graduationYear" class="form-label">Expected Graduation Year</label>
                  <select 
                    class="form-select" 
                    id="graduationYear"
                    v-model="form.graduationYear"
                    :disabled="isLoading"
                  >
                    <option value="">Select graduation year</option>
                    <option v-for="year in graduationYears" :key="year" :value="year">
                      {{ year }}
                    </option>
                  </select>
                </div>
              </div>

              <div class="mb-4">
                <label for="chapterId" class="form-label">Chapter (Optional)</label>
                <select 
                  class="form-select" 
                  id="chapterId"
                  v-model="form.chapterId"
                  :disabled="isLoading || loadingChapters"
                >
                  <option value="">
                    {{ loadingChapters ? 'Loading chapters...' : 'Select a chapter to join (optional)' }}
                  </option>
                  <option v-for="chapter in chapters" :key="chapter.id" :value="chapter.id">
                    {{ chapter.name }} - {{ chapter.universityName }}
                  </option>
                </select>
                <small class="form-text text-muted">
                  You can join a chapter later if you prefer
                </small>
              </div>

              <div v-if="error" class="alert alert-danger">
                <i class="bi bi-exclamation-triangle me-2"></i>
                {{ error }}
              </div>

              <div v-if="success" class="alert alert-success">
                <i class="bi bi-check-circle me-2"></i>
                {{ success }}
              </div>

              <button 
                type="submit" 
                class="btn btn-primary w-100"
                :disabled="isLoading"
              >
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="bi bi-person-plus me-2"></i>
                {{ isLoading ? 'Registering...' : 'Register' }}
              </button>
            </form>

            <div class="text-center mt-3">
              <p class="mb-0">
                Already have an account? 
                <router-link to="/login" class="text-decoration-none">
                  <i class="bi bi-box-arrow-in-right me-1"></i>
                  Sign in here
                </router-link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import { useAuth } from '@/composables/useAuth'

export default {
  name: 'Register',
  setup() {
    const router = useRouter()
    const { setUser } = useAuth()
    
    const form = ref({
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      phoneNumber: '',
      major: '',
      graduationYear: '',
      chapterId: ''
    })
    
    const chapters = ref([])
    const isLoading = ref(false)
    const loadingChapters = ref(true)
    const error = ref('')
    const success = ref('')

    const currentYear = new Date().getFullYear()
    const graduationYears = ref([])
    for (let year = currentYear; year <= currentYear + 10; year++) {
      graduationYears.value.push(year)
    }

    const loadChapters = async () => {
      try {
        loadingChapters.value = true
        const response = await api.get('/api/chapters')
        chapters.value = response.data
      } catch (err) {
        console.error('Error loading chapters:', err)
        error.value = 'Failed to load chapters. Please refresh the page.'
      } finally {
        loadingChapters.value = false
      }
    }

    const validateForm = () => {
      if (!form.value.firstName.trim()) {
        error.value = 'First name is required'
        return false
      }
      if (!form.value.lastName.trim()) {
        error.value = 'Last name is required'
        return false
      }
      if (!form.value.email.trim()) {
        error.value = 'Email is required'
        return false
      }
      if (!form.value.password.trim()) {
        error.value = 'Password is required'
        return false
      }
      if (form.value.password.length < 6) {
        error.value = 'Password must be at least 6 characters'
        return false
      }
      return true
    }

    const buildRegistrationData = () => {
      const registrationData = {
        firstName: form.value.firstName.trim(),
        lastName: form.value.lastName.trim(),
        email: form.value.email.trim(),
        password: form.value.password.trim(),
        phoneNumber: form.value.phoneNumber.trim() || null,
        major: form.value.major.trim() || null,
        graduationYear: form.value.graduationYear || null
      }

      if (form.value.chapterId) {
        registrationData.chapterId = form.value.chapterId
      }

      return registrationData
    }

    const handleRegistrationSuccess = (response) => {
      success.value = response.data.message
      setUser(response.data.user)
      
      setTimeout(() => {
        router.push('/')
      }, 2000)
    }

    const handleRegistrationError = (err) => {
      console.error('Registration error:', err)
      if (err.response?.data?.error) {
        error.value = err.response.data.error
      } else {
        error.value = 'Registration failed. Please try again.'
      }
    }

    const handleSubmit = async () => {
      try {
        isLoading.value = true
        error.value = ''
        success.value = ''

        if (!validateForm()) {
          return
        }

        const registrationData = buildRegistrationData()
        const response = await api.post('/api/auth/register', registrationData)

        if (response.data.success) {
          handleRegistrationSuccess(response)
        } else {
          error.value = response.data.error || 'Registration failed'
        }

      } catch (err) {
        handleRegistrationError(err)
      } finally {
        isLoading.value = false
      }
    }

    // Load chapters when component mounts
    onMounted(() => {
      loadChapters()
    })

    return {
      form,
      chapters,
      graduationYears,
      isLoading,
      loadingChapters,
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

.form-control:focus, .form-select:focus {
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
