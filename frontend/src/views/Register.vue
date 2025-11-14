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
import { useErrorHandler } from '@/utils/error-handling/ErrorHandler'
import { 
  UserRegistrationError, 
  InputValidationError 
} from '@/utils/error-handling/ErrorTypes'

export default {
  name: 'Register',
  setup() {
    const router = useRouter()
    const { setUser } = useAuth()
    const { handleAsyncOperation } = useErrorHandler()
    
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
      await handleAsyncOperation(
        // Operation: load chapters from API
        async () => {
          const response = await api.get('/api/chapters')
          chapters.value = response.data
          return response.data
        },
        {
          // Options for error handling
          loadingRef: loadingChapters,
          errorRef: error,
          onSuccess: (data) => {
            console.log(`Loaded ${data.length} chapters successfully`)
          },
          onError: (error) => {
            // Custom error handling for chapter loading
            console.error('Chapter loading failed:', error.name, error.message)
          }
        }
      )
    }

    const validateFormOrThrow = () => {
      // Implement fail-fast validation with specific exceptions
      if (!form.value.firstName.trim()) {
        throw UserRegistrationError.missingRequiredField('First name')
      }
      if (!form.value.lastName.trim()) {
        throw UserRegistrationError.missingRequiredField('Last name')  
      }
      if (!form.value.email.trim()) {
        throw UserRegistrationError.missingRequiredField('Email')
      }
      if (!form.value.password.trim()) {
        throw UserRegistrationError.missingRequiredField('Password')
      }
      
      // Validate email format
      const emailPattern = /^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\.[A-Za-z]{2,})$/
      if (!emailPattern.test(form.value.email)) {
        throw UserRegistrationError.invalidEmailFormat(form.value.email)
      }
      
      // Validate password strength
      if (form.value.password.length < 6) {
        throw UserRegistrationError.weakPassword()
      }
      
      // Validate phone number format if provided
      if (form.value.phoneNumber.trim() && !/^[\d\s\-\(\)\+\.]{10,15}$/.test(form.value.phoneNumber)) {
        throw InputValidationError.invalidPhoneFormat(form.value.phoneNumber)
      }
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



    const handleSubmit = async () => {
      await handleAsyncOperation(
        // Operation: register user with validation
        async () => {
          // Try-catch-finally first approach: validate then execute
          
          // Step 1: Validate form data (fail fast)
          validateFormOrThrow()
          
          // Step 2: Build registration data (happy path)
          const registrationData = buildRegistrationData()
          
          // Step 3: Submit registration
          const response = await api.post('/api/auth/register', registrationData)
          
          // Step 4: Handle successful registration
          if (response.data.status === 'success') {
            success.value = response.data.message
            setUser(response.data.member)
            
            // Navigate after short delay to show success message
            setTimeout(() => {
              router.push('/')
            }, 2000)
          } else {
            // Transform unexpected response format into proper error
            throw new UserRegistrationError(
              response.data.message || 'Registration failed',
              'UNEXPECTED_RESPONSE'
            )
          }
          
          return response.data
        },
        {
          // Error handling configuration
          loadingRef: isLoading,
          errorRef: error,
          onSuccess: (data) => {
            console.log('Registration successful for:', data.user?.email)
          },
          onError: (error) => {
            // Clear success message on error
            success.value = ''
            console.error('Registration failed:', error.name, error.message)
          },
          cleanup: async () => {
            // Cleanup: always clear sensitive data from memory
            // (In production, might clear form password field)
            console.log('Registration operation cleanup completed')
          }
        }
      )
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
