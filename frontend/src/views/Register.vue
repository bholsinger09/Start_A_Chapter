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
          <p class="lead mb-4">Join the Movement</p>
          <div class="mb-4">
            <div class="d-flex align-items-center justify-content-center mb-3">
              <i class="bi bi-award-fill me-3 text-warning"></i>
              <span>Build Leadership Skills</span>
            </div>
            <div class="d-flex align-items-center justify-content-center mb-3">
              <i class="bi bi-people-fill me-3 text-info"></i>
              <span>Connect with Like-minded Students</span>
            </div>
            <div class="d-flex align-items-center justify-content-center mb-3">
              <i class="bi bi-trophy-fill me-3 text-success"></i>
              <span>Make a Lasting Impact</span>
            </div>
          </div>
          <p class="text-light opacity-75">
            Start your journey as a student leader today.
          </p>
        </div>
      </div>

      <!-- Right Side - Registration Form -->
      <div class="col-lg-6 d-flex align-items-center justify-content-center bg-light">
        <div class="w-100 px-4" style="max-width: 500px;">
          <!-- Mobile Logo -->
          <div class="text-center mb-4 d-lg-none">
            <h2 class="text-primary fw-bold">
              <i class="bi bi-people-fill me-2"></i>StartAChapter
            </h2>
          </div>

          <!-- Registration Form -->
          <div class="card shadow border-0">
            <div class="card-body p-4">
              <div class="text-center mb-4">
                <h3 class="fw-bold text-dark mb-2">Create Account</h3>
                <p class="text-muted">Join our community of student leaders</p>
              </div>

              <!-- Progress Indicator -->
              <div class="progress mb-4" style="height: 4px;">
                <div 
                  class="progress-bar bg-primary" 
                  :style="{ width: `${(currentStep / totalSteps) * 100}%` }"
                ></div>
              </div>

              <!-- Error Message -->
              <div v-if="error" class="alert alert-danger" role="alert">
                <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
              </div>

              <!-- Success Message -->
              <div v-if="success" class="alert alert-success" role="alert">
                <i class="bi bi-check-circle me-2"></i>{{ success }}
              </div>

              <form @submit.prevent="handleRegistration">
                <!-- Step 1: Personal Information -->
                <div v-show="currentStep === 1">
                  <h5 class="mb-3 text-primary">
                    <i class="bi bi-person me-2"></i>Personal Information
                  </h5>
                  
                  <div class="row g-3">
                    <div class="col-md-6">
                      <label for="firstName" class="form-label fw-semibold">
                        First Name *
                      </label>
                      <input
                        type="text"
                        class="form-control"
                        id="firstName"
                        v-model="formData.firstName"
                        required
                        maxlength="50"
                        :class="{ 'is-invalid': errors.firstName }"
                        placeholder="Enter first name"
                      >
                      <div v-if="errors.firstName" class="invalid-feedback">
                        {{ errors.firstName }}
                      </div>
                    </div>

                    <div class="col-md-6">
                      <label for="lastName" class="form-label fw-semibold">
                        Last Name *
                      </label>
                      <input
                        type="text"
                        class="form-control"
                        id="lastName"
                        v-model="formData.lastName"
                        required
                        maxlength="50"
                        :class="{ 'is-invalid': errors.lastName }"
                        placeholder="Enter last name"
                      >
                      <div v-if="errors.lastName" class="invalid-feedback">
                        {{ errors.lastName }}
                      </div>
                    </div>
                  </div>

                  <div class="mt-3">
                    <label for="email" class="form-label fw-semibold">
                      Email Address *
                    </label>
                    <input
                      type="email"
                      class="form-control"
                      id="email"
                      v-model="formData.email"
                      required
                      maxlength="100"
                      :class="{ 'is-invalid': errors.email }"
                      placeholder="Enter email address"
                    >
                    <div v-if="errors.email" class="invalid-feedback">
                      {{ errors.email }}
                    </div>
                    <div class="form-text">We'll use this for account verification and communications</div>
                  </div>

                  <div class="mt-3">
                    <label for="phoneNumber" class="form-label fw-semibold">
                      Phone Number
                    </label>
                    <input
                      type="tel"
                      class="form-control"
                      id="phoneNumber"
                      v-model="formData.phoneNumber"
                      maxlength="15"
                      placeholder="e.g., (555) 123-4567"
                    >
                    <div class="form-text">Optional - for important notifications</div>
                  </div>
                </div>

                <!-- Step 2: Account Setup -->
                <div v-show="currentStep === 2">
                  <h5 class="mb-3 text-primary">
                    <i class="bi bi-key me-2"></i>Account Setup
                  </h5>
                  
                  <div class="mb-3">
                    <label for="username" class="form-label fw-semibold">
                      Username *
                    </label>
                    <input
                      type="text"
                      class="form-control"
                      id="username"
                      v-model="formData.username"
                      required
                      minlength="3"
                      maxlength="30"
                      :class="{ 'is-invalid': errors.username }"
                      placeholder="Choose a username"
                    >
                    <div v-if="errors.username" class="invalid-feedback">
                      {{ errors.username }}
                    </div>
                    <div class="form-text">3-30 characters, letters, numbers, and underscores only</div>
                  </div>

                  <div class="mb-3">
                    <label for="password" class="form-label fw-semibold">
                      Password *
                    </label>
                    <div class="position-relative">
                      <input
                        :type="showPassword ? 'text' : 'password'"
                        class="form-control"
                        id="password"
                        v-model="formData.password"
                        required
                        minlength="8"
                        :class="{ 'is-invalid': errors.password }"
                        placeholder="Create a strong password"
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
                    
                    <!-- Password strength indicator -->
                    <div class="mt-2" v-if="formData.password">
                      <div class="progress" style="height: 4px;">
                        <div 
                          class="progress-bar"
                          :class="passwordStrengthClass"
                          :style="{ width: `${passwordStrengthWidth}%` }"
                        ></div>
                      </div>
                      <small class="form-text" :class="passwordStrengthTextClass">
                        {{ passwordStrengthText }}
                      </small>
                    </div>
                  </div>

                  <div class="mb-3">
                    <label for="confirmPassword" class="form-label fw-semibold">
                      Confirm Password *
                    </label>
                    <input
                      type="password"
                      class="form-control"
                      id="confirmPassword"
                      v-model="formData.confirmPassword"
                      required
                      :class="{ 'is-invalid': errors.confirmPassword }"
                      placeholder="Confirm your password"
                    >
                    <div v-if="errors.confirmPassword" class="invalid-feedback">
                      {{ errors.confirmPassword }}
                    </div>
                  </div>
                </div>

                <!-- Step 3: Academic & Chapter Information -->
                <div v-show="currentStep === 3">
                  <h5 class="mb-3 text-primary">
                    <i class="bi bi-mortarboard me-2"></i>Academic Information
                  </h5>
                  
                  <div class="mb-3">
                    <label for="major" class="form-label fw-semibold">
                      Major / Field of Study
                    </label>
                    <input
                      type="text"
                      class="form-control"
                      id="major"
                      v-model="formData.major"
                      maxlength="100"
                      placeholder="e.g., Political Science, Business Administration"
                    >
                  </div>

                  <div class="mb-3">
                    <label for="graduationYear" class="form-label fw-semibold">
                      Expected Graduation Year
                    </label>
                    <input
                      type="number"
                      class="form-control"
                      id="graduationYear"
                      v-model="formData.graduationYear"
                      min="2020"
                      max="2035"
                      placeholder="e.g., 2025"
                    >
                  </div>

                  <div class="mb-3">
                    <label for="chapterId" class="form-label fw-semibold">
                      Chapter (Optional)
                    </label>
                    <select
                      class="form-select"
                      id="chapterId"
                      v-model="formData.chapterId"
                    >
                      <option value="">Select a chapter to join (optional)</option>
                      <option v-for="chapter in chapters" :key="chapter.id" :value="chapter.id">
                        {{ chapter.name }} - {{ chapter.university }}
                      </option>
                    </select>
                    <div class="form-text">You can join a chapter later if you prefer</div>
                  </div>

                  <div class="mb-3">
                    <div class="form-check">
                      <input
                        class="form-check-input"
                        type="checkbox"
                        id="agreeTerms"
                        v-model="formData.agreeTerms"
                        required
                        :class="{ 'is-invalid': errors.agreeTerms }"
                      >
                      <label class="form-check-label" for="agreeTerms">
                        I agree to the 
                        <a href="#" class="text-primary">Terms of Service</a> and 
                        <a href="#" class="text-primary">Privacy Policy</a>
                      </label>
                      <div v-if="errors.agreeTerms" class="invalid-feedback">
                        {{ errors.agreeTerms }}
                      </div>
                    </div>
                  </div>

                  <div class="form-check">
                    <input
                      class="form-check-input"
                      type="checkbox"
                      id="subscribeNewsletter"
                      v-model="formData.subscribeNewsletter"
                    >
                    <label class="form-check-label" for="subscribeNewsletter">
                      Subscribe to newsletter for updates and tips
                    </label>
                  </div>
                </div>

                <!-- Form Navigation -->
                <div class="d-flex justify-content-between mt-4">
                  <button
                    type="button"
                    class="btn btn-outline-secondary"
                    @click="previousStep"
                    v-if="currentStep > 1"
                    :disabled="loading"
                  >
                    <i class="bi bi-arrow-left me-2"></i>Previous
                  </button>
                  <div v-else></div>

                  <button
                    v-if="currentStep < totalSteps"
                    type="button"
                    class="btn btn-primary"
                    @click="nextStep"
                    :disabled="loading"
                  >
                    Next<i class="bi bi-arrow-right ms-2"></i>
                  </button>
                  
                  <button
                    v-else
                    type="submit"
                    class="btn btn-success btn-lg"
                    :disabled="loading || !canSubmit"
                  >
                    <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                    <i v-else class="bi bi-check-circle me-2"></i>
                    {{ loading ? 'Creating Account...' : 'Create Account' }}
                  </button>
                </div>
              </form>
            </div>
          </div>

          <!-- Login Link -->
          <div class="text-center mt-4">
            <p class="text-muted">
              Already have an account?
              <router-link to="/login" class="text-primary text-decoration-none fw-semibold">
                Sign in here
              </router-link>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../services/api'

export default {
  name: 'Register',
  setup() {
    const router = useRouter()
    
    const formData = ref({
      firstName: '',
      lastName: '',
      email: '',
      phoneNumber: '',
      username: '',
      password: '',
      confirmPassword: '',
      major: '',
      graduationYear: '',
      chapterId: '',
      agreeTerms: false,
      subscribeNewsletter: false
    })
    
    const chapters = ref([])
    const loading = ref(false)
    const error = ref('')
    const success = ref('')
    const errors = ref({})
    const currentStep = ref(1)
    const totalSteps = ref(3)
    const showPassword = ref(false)

    // Computed properties
    const passwordStrength = computed(() => {
      const password = formData.value.password
      if (!password) return 0
      
      let strength = 0
      if (password.length >= 8) strength += 25
      if (/[a-z]/.test(password)) strength += 25
      if (/[A-Z]/.test(password)) strength += 25
      if (/[0-9]/.test(password)) strength += 12.5
      if (/[^A-Za-z0-9]/.test(password)) strength += 12.5
      
      return Math.min(strength, 100)
    })

    const passwordStrengthClass = computed(() => {
      const strength = passwordStrength.value
      if (strength < 30) return 'bg-danger'
      if (strength < 60) return 'bg-warning'
      if (strength < 90) return 'bg-info'
      return 'bg-success'
    })

    const passwordStrengthWidth = computed(() => passwordStrength.value)

    const passwordStrengthText = computed(() => {
      const strength = passwordStrength.value
      if (strength < 30) return 'Weak password'
      if (strength < 60) return 'Fair password'
      if (strength < 90) return 'Good password'
      return 'Strong password'
    })

    const passwordStrengthTextClass = computed(() => {
      const strength = passwordStrength.value
      if (strength < 30) return 'text-danger'
      if (strength < 60) return 'text-warning'
      if (strength < 90) return 'text-info'
      return 'text-success'
    })

    const canSubmit = computed(() => {
      return formData.value.agreeTerms && 
             formData.value.firstName.trim() &&
             formData.value.lastName.trim() &&
             formData.value.email.trim() &&
             formData.value.username.trim() &&
             formData.value.password &&
             formData.value.confirmPassword &&
             passwordStrength.value >= 30
    })

    // Methods
    const loadChapters = async () => {
      try {
        console.log('🏫 Loading chapters from /api/chapters...')
        const response = await axios.get('/api/chapters')
        console.log('✅ Chapters loaded successfully:', response.data)
        chapters.value = response.data || []
      } catch (err) {
        console.error('❌ Error loading chapters:', err)
        console.error('❌ Error details:', {
          message: err.message,
          status: err.response?.status,
          statusText: err.response?.statusText,
          url: err.config?.url,
          method: err.config?.method
        })
        // Don't let chapter loading failure prevent the form from working
        chapters.value = []
      }
    }

    const validateStep = (step) => {
      errors.value = {}

      if (step === 1) {
        if (!formData.value.firstName.trim()) {
          errors.value.firstName = 'First name is required'
        }
        if (!formData.value.lastName.trim()) {
          errors.value.lastName = 'Last name is required'
        }
        if (!formData.value.email.trim()) {
          errors.value.email = 'Email is required'
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.value.email)) {
          errors.value.email = 'Please enter a valid email address'
        }
      }

      if (step === 2) {
        if (!formData.value.username.trim()) {
          errors.value.username = 'Username is required'
        } else if (formData.value.username.length < 3) {
          errors.value.username = 'Username must be at least 3 characters'
        } else if (!/^[a-zA-Z0-9_]+$/.test(formData.value.username)) {
          errors.value.username = 'Username can only contain letters, numbers, and underscores'
        }

        if (!formData.value.password) {
          errors.value.password = 'Password is required'
        } else if (formData.value.password.length < 8) {
          errors.value.password = 'Password must be at least 8 characters'
        } else if (passwordStrength.value < 30) {
          errors.value.password = 'Password is too weak'
        }

        if (!formData.value.confirmPassword) {
          errors.value.confirmPassword = 'Please confirm your password'
        } else if (formData.value.password !== formData.value.confirmPassword) {
          errors.value.confirmPassword = 'Passwords do not match'
        }
      }

      if (step === 3) {
        if (!formData.value.agreeTerms) {
          errors.value.agreeTerms = 'You must agree to the terms to continue'
        }
      }

      return Object.keys(errors.value).length === 0
    }

    const nextStep = () => {
      if (validateStep(currentStep.value) && currentStep.value < totalSteps.value) {
        currentStep.value++
      }
    }

    const previousStep = () => {
      if (currentStep.value > 1) {
        currentStep.value--
      }
    }

    const handleRegistration = async () => {
      // Validate all steps
      for (let i = 1; i <= totalSteps.value; i++) {
        if (!validateStep(i)) {
          currentStep.value = i
          return
        }
      }

      loading.value = true
      error.value = ''
      success.value = ''

      try {
        const registrationData = {
          firstName: formData.value.firstName.trim(),
          lastName: formData.value.lastName.trim(),
          email: formData.value.email.trim(),
          username: formData.value.username.trim(),
          password: formData.value.password,
          phoneNumber: formData.value.phoneNumber.trim() || null,
          major: formData.value.major.trim() || null,
          graduationYear: formData.value.graduationYear || null,
          chapterId: formData.value.chapterId || null,
          subscribeNewsletter: formData.value.subscribeNewsletter,
          role: 'MEMBER' // Default role for new registrations
        }

        console.log('🚀 Sending registration request:', registrationData)
        const response = await axios.post('/auth/register', registrationData)
        console.log('✅ Registration response:', response.data)
        
        success.value = 'Account created successfully! Redirecting to login...'
        
        setTimeout(() => {
          router.push('/login?registered=true')
        }, 2000)
        
      } catch (err) {
        console.error('❌ Registration error:', err)
        console.error('❌ Error response:', err.response)
        console.error('❌ Error message:', err.message)
        console.error('❌ Error code:', err.code)
        
        if (err.response?.status === 400) {
          console.log('🔍 Validation error details:', err.response.data)
          if (err.response.data.field === 'username') {
            errors.value.username = err.response.data.message || 'Username already exists'
            currentStep.value = 2
          } else if (err.response.data.field === 'email') {
            errors.value.email = err.response.data.message || 'Email already exists'
            currentStep.value = 1
          } else {
            error.value = err.response.data.message || 'Registration failed'
          }
        } else if (err.code === 'ERR_NETWORK') {
          error.value = 'Network error. Please check your connection.'
        } else if (err.code === 'ECONNABORTED') {
          error.value = 'Request timeout. Please try again.'
        } else {
          error.value = `Registration failed: ${err.message || 'Please try again.'}`
        }
      } finally {
        loading.value = false
      }
    }

    // Lifecycle
    onMounted(() => {
      console.log('🚀 Register component mounted, loading chapters...')
      // Load chapters but don't block the component if it fails
      setTimeout(loadChapters, 500) // Add small delay to ensure backend is ready
    })

    return {
      formData,
      chapters,
      loading,
      error,
      success,
      errors,
      currentStep,
      totalSteps,
      showPassword,
      passwordStrength,
      passwordStrengthClass,
      passwordStrengthWidth,
      passwordStrengthText,
      passwordStrengthTextClass,
      canSubmit,
      nextStep,
      previousStep,
      handleRegistration,
      validateStep
    }
  }
}
</script>

<style scoped>
.min-vh-100 {
  min-height: 100vh;
}

.form-control:focus,
.form-select:focus {
  border-color: var(--bs-primary);
  box-shadow: 0 0 0 0.25rem rgba(var(--bs-primary-rgb), 0.25);
}

.card {
  border-radius: 12px;
}

.bg-primary {
  background: linear-gradient(135deg, var(--bs-primary) 0%, #0056b3 100%);
}

.progress {
  border-radius: 2px;
}

.form-check-input:checked {
  background-color: var(--bs-primary);
  border-color: var(--bs-primary);
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