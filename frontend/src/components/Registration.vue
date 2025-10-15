<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card">
          <div class="card-header">
            <h3 class="text-center">Join a Chapter</h3>
            <p class="text-center text-muted">Create your account to get started</p>
          </div>
          <div class="card-body">
            <form @submit.prevent="handleSubmit">
              <!-- Success/Error Messages -->
              <div v-if="successMessage" class="alert alert-success">
                {{ successMessage }}
              </div>
              <div v-if="errorMessage" class="alert alert-danger">
                {{ errorMessage }}
              </div>

              <!-- Personal Information -->
              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="firstName" class="form-label">First Name *</label>
                    <input
                      type="text"
                      class="form-control"
                      id="firstName"
                      v-model="form.firstName"
                      :class="{ 'is-invalid': errors.firstName }"
                      required
                    />
                    <div v-if="errors.firstName" class="invalid-feedback">
                      {{ errors.firstName }}
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="lastName" class="form-label">Last Name *</label>
                    <input
                      type="text"
                      class="form-control"
                      id="lastName"
                      v-model="form.lastName"
                      :class="{ 'is-invalid': errors.lastName }"
                      required
                    />
                    <div v-if="errors.lastName" class="invalid-feedback">
                      {{ errors.lastName }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- Email and Username -->
              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="email" class="form-label">Email *</label>
                    <input
                      type="email"
                      class="form-control"
                      id="email"
                      v-model="form.email"
                      :class="{ 'is-invalid': errors.email }"
                      required
                    />
                    <div v-if="errors.email" class="invalid-feedback">
                      {{ errors.email }}
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="username" class="form-label">Username *</label>
                    <input
                      type="text"
                      class="form-control"
                      id="username"
                      v-model="form.username"
                      :class="{ 'is-invalid': errors.username }"
                      placeholder="At least 3 characters"
                      required
                    />
                    <div v-if="errors.username" class="invalid-feedback">
                      {{ errors.username }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- Password -->
              <div class="mb-3">
                <label for="password" class="form-label">Password *</label>
                <input
                  type="password"
                  class="form-control"
                  id="password"
                  v-model="form.password"
                  :class="{ 'is-invalid': errors.password }"
                  placeholder="At least 8 characters"
                  required
                />
                <div v-if="errors.password" class="invalid-feedback">
                  {{ errors.password }}
                </div>
              </div>

              <!-- Chapter Selection -->
              <div class="mb-3">
                <label for="chapterId" class="form-label">Select Chapter *</label>
                <select
                  class="form-select"
                  id="chapterId"
                  v-model="form.chapterId"
                  :class="{ 'is-invalid': errors.chapterId }"
                  required
                >
                  <option value="">Choose a chapter...</option>
                  <option
                    v-for="chapter in chapters"
                    :key="chapter.id"
                    :value="chapter.id"
                  >
                    {{ chapter.name }} - {{ chapter.university }}
                  </option>
                </select>
                <div v-if="errors.chapterId" class="invalid-feedback">
                  {{ errors.chapterId }}
                </div>
                <div v-if="chaptersError" class="form-text text-danger">
                  Error loading chapters: {{ chaptersError }}
                </div>
              </div>

              <!-- Optional Information -->
              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="major" class="form-label">Major</label>
                    <input
                      type="text"
                      class="form-control"
                      id="major"
                      v-model="form.major"
                      placeholder="Your field of study"
                    />
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="graduationYear" class="form-label">Graduation Year</label>
                    <input
                      type="text"
                      class="form-control"
                      id="graduationYear"
                      v-model="form.graduationYear"
                      placeholder="e.g., 2025"
                    />
                  </div>
                </div>
              </div>

              <!-- Phone Number -->
              <div class="mb-3">
                <label for="phoneNumber" class="form-label">Phone Number</label>
                <input
                  type="tel"
                  class="form-control"
                  id="phoneNumber"
                  v-model="form.phoneNumber"
                  placeholder="(optional)"
                />
              </div>

              <!-- Submit Button -->
              <div class="d-grid">
                <button
                  type="submit"
                  class="btn btn-primary btn-lg"
                  :disabled="isSubmitting"
                >
                  <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2"></span>
                  {{ isSubmitting ? 'Creating Account...' : 'Create Account' }}
                </button>
              </div>
            </form>

            <div class="text-center mt-3">
              <p>Already have an account? <router-link to="/login">Login here</router-link></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Registration',
  data() {
    return {
      form: {
        firstName: '',
        lastName: '',
        email: '',
        username: '',
        password: '',
        chapterId: '',
        major: '',
        graduationYear: '',
        phoneNumber: ''
      },
      chapters: [],
      errors: {},
      successMessage: '',
      errorMessage: '',
      chaptersError: '',
      isSubmitting: false
    }
  },
  async mounted() {
    await this.loadChapters()
  },
  methods: {
    async loadChapters() {
      try {
        const response = await fetch('http://localhost:8080/api/chapters')
        if (response.ok) {
          this.chapters = await response.json()
        } else {
          this.chaptersError = 'Failed to load chapters'
        }
      } catch (error) {
        console.error('Error loading chapters:', error)
        this.chaptersError = 'Network error loading chapters'
      }
    },
    validateForm() {
      this.errors = {}
      let isValid = true

      // Required field validation
      if (!this.form.firstName.trim()) {
        this.errors.firstName = 'First name is required'
        isValid = false
      } else if (this.form.firstName.length > 50) {
        this.errors.firstName = 'First name must be 50 characters or less'
        isValid = false
      }

      if (!this.form.lastName.trim()) {
        this.errors.lastName = 'Last name is required'
        isValid = false
      } else if (this.form.lastName.length > 50) {
        this.errors.lastName = 'Last name must be 50 characters or less'
        isValid = false
      }

      if (!this.form.email.trim()) {
        this.errors.email = 'Email is required'
        isValid = false
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) {
        this.errors.email = 'Please enter a valid email address'
        isValid = false
      }

      if (!this.form.username.trim()) {
        this.errors.username = 'Username is required'
        isValid = false
      } else if (this.form.username.length < 3) {
        this.errors.username = 'Username must be at least 3 characters'
        isValid = false
      } else if (this.form.username.length > 50) {
        this.errors.username = 'Username must be 50 characters or less'
        isValid = false
      }

      if (!this.form.password) {
        this.errors.password = 'Password is required'
        isValid = false
      } else if (this.form.password.length < 8) {
        this.errors.password = 'Password must be at least 8 characters'
        isValid = false
      }

      if (!this.form.chapterId) {
        this.errors.chapterId = 'Please select a chapter'
        isValid = false
      }

      return isValid
    },
    async handleSubmit() {
      if (!this.validateForm()) {
        return
      }

      this.isSubmitting = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        const response = await fetch('http://localhost:8080/api/auth/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.form)
        })

        const data = await response.json()

        if (response.ok) {
          this.successMessage = `Registration successful! Welcome ${data.username}!`
          this.resetForm()
          
          // Redirect to login page after 2 seconds
          setTimeout(() => {
            this.$router.push('/login')
          }, 2000)
        } else {
          if (data.message) {
            this.errorMessage = data.message
          } else if (data.errors) {
            // Handle validation errors from backend
            this.errors = data.errors
          } else {
            this.errorMessage = 'Registration failed. Please try again.'
          }
        }
      } catch (error) {
        console.error('Registration error:', error)
        this.errorMessage = 'Network error. Please check your connection and try again.'
      } finally {
        this.isSubmitting = false
      }
    },
    resetForm() {
      this.form = {
        firstName: '',
        lastName: '',
        email: '',
        username: '',
        password: '',
        chapterId: '',
        major: '',
        graduationYear: '',
        phoneNumber: ''
      }
      this.errors = {}
    }
  }
}
</script>

<style scoped>
.card {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
  border: none;
}

.card-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom: none;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-1px);
}

.form-control:focus, .form-select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
}

.alert {
  border: none;
  border-radius: 10px;
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
}
</style>