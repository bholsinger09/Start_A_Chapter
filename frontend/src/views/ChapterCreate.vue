<template>
  <div class="container-fluid py-4">
    <!-- Header -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="display-5 fw-bold text-primary">
              <i class="bi bi-plus-circle me-3"></i>
              Create New Chapter
            </h1>
            <p class="lead text-muted">Start a new chapter at your institution</p>
          </div>
          <router-link to="/chapters" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-2"></i>Back to Chapters
          </router-link>
        </div>
      </div>
    </div>

    <!-- Success Message -->
    <div v-if="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
      <i class="bi bi-check-circle me-2"></i>{{ successMessage }}
      <button type="button" class="btn-close" @click="successMessage = ''"></button>
    </div>

    <!-- Error Message -->
    <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ errorMessage }}
      <button type="button" class="btn-close" @click="errorMessage = ''"></button>
    </div>

    <!-- Chapter Creation Form -->
    <div class="row">
      <div class="col-lg-8 mx-auto">
        <div class="card shadow-sm">
          <div class="card-header bg-primary text-white">
            <h5 class="card-title mb-0">
              <i class="bi bi-building me-2"></i>Chapter Information
            </h5>
          </div>
          <div class="card-body">
            <form @submit.prevent="createChapter">
              <!-- Chapter Name -->
              <div class="mb-3">
                <label for="name" class="form-label fw-semibold required">
                  Chapter Name *
                </label>
                <input
                  type="text"
                  class="form-control"
                  id="name"
                  v-model="chapterForm.name"
                  placeholder="Enter chapter name (e.g., 'Turning Point Chapter')"
                  required
                  :class="{ 'is-invalid': errors.name }"
                >
                <div v-if="errors.name" class="invalid-feedback">
                  {{ errors.name }}
                </div>
              </div>

              <!-- University Name -->
              <div class="mb-3">
                <label for="universityName" class="form-label fw-semibold required">
                  University/Institution Name *
                </label>
                <select
                  class="form-select"
                  id="universityName"
                  v-model="chapterForm.universityName"
                  required
                  :class="{ 'is-invalid': errors.universityName }"
                  @change="onUniversityChange"
                >
                  <option value="">Select a university/institution...</option>
                  <option v-for="institution in institutions" :key="institution.id" :value="institution.name">
                    {{ institution.name }} ({{ institution.location }}, {{ institution.state }})
                  </option>
                </select>
                <div v-if="errors.universityName" class="invalid-feedback">
                  {{ errors.universityName }}
                </div>
              </div>

              <!-- Location Information -->
              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="state" class="form-label fw-semibold required">
                      State *
                    </label>
                    <select
                      class="form-select"
                      id="state"
                      v-model="chapterForm.state"
                      required
                      :class="{ 'is-invalid': errors.state }"
                    >
                      <option value="">Select a state...</option>
                      <option v-for="state in usStates" :key="state.code" :value="state.code">
                        {{ state.name }}
                      </option>
                    </select>
                    <div v-if="errors.state" class="invalid-feedback">
                      {{ errors.state }}
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label for="city" class="form-label fw-semibold required">
                      City *
                    </label>
                    <input
                      type="text"
                      class="form-control"
                      id="city"
                      v-model="chapterForm.city"
                      placeholder="Enter city name"
                      required
                      :class="{ 'is-invalid': errors.city }"
                    >
                    <div v-if="errors.city" class="invalid-feedback">
                      {{ errors.city }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- Description -->
              <div class="mb-4">
                <label for="description" class="form-label fw-semibold">
                  Chapter Description
                </label>
                <textarea
                  class="form-control"
                  id="description"
                  v-model="chapterForm.description"
                  rows="4"
                  placeholder="Describe your chapter's mission, activities, and goals..."
                  :class="{ 'is-invalid': errors.description }"
                ></textarea>
                <div class="form-text">
                  {{ chapterForm.description.length }}/500 characters
                </div>
                <div v-if="errors.description" class="invalid-feedback">
                  {{ errors.description }}
                </div>
              </div>

              <!-- Form Actions -->
              <div class="d-flex justify-content-between align-items-center">
                <div class="text-muted">
                  <small>* Required fields</small>
                </div>
                <div>
                  <button type="button" class="btn btn-outline-secondary me-2" @click="resetForm">
                    <i class="bi bi-arrow-clockwise me-2"></i>Reset
                  </button>
                  <button 
                    type="submit" 
                    class="btn btn-primary btn-lg"
                    :disabled="saving"
                  >
                    <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                    <i v-else class="bi bi-plus-circle me-2"></i>
                    {{ saving ? 'Creating Chapter...' : 'Create Chapter' }}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Information Card -->
    <div class="row mt-4">
      <div class="col-lg-8 mx-auto">
        <div class="card bg-light">
          <div class="card-body">
            <h6 class="card-title">
              <i class="bi bi-info-circle me-2"></i>Getting Started
            </h6>
            <p class="card-text mb-0">
              Once your chapter is created, you'll be able to add members, organize events, 
              and manage your chapter's activities through the dashboard.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChapterCreate',
  data() {
    return {
      chapterForm: {
        name: '',
        universityName: '',
        state: '',
        city: '',
        description: ''
      },
      institutions: [],
      errors: {},
      successMessage: '',
      errorMessage: '',
      saving: false,
      usStates: [
        { code: 'AL', name: 'Alabama' },
        { code: 'AK', name: 'Alaska' },
        { code: 'AZ', name: 'Arizona' },
        { code: 'AR', name: 'Arkansas' },
        { code: 'CA', name: 'California' },
        { code: 'CO', name: 'Colorado' },
        { code: 'CT', name: 'Connecticut' },
        { code: 'DE', name: 'Delaware' },
        { code: 'FL', name: 'Florida' },
        { code: 'GA', name: 'Georgia' },
        { code: 'HI', name: 'Hawaii' },
        { code: 'ID', name: 'Idaho' },
        { code: 'IL', name: 'Illinois' },
        { code: 'IN', name: 'Indiana' },
        { code: 'IA', name: 'Iowa' },
        { code: 'KS', name: 'Kansas' },
        { code: 'KY', name: 'Kentucky' },
        { code: 'LA', name: 'Louisiana' },
        { code: 'ME', name: 'Maine' },
        { code: 'MD', name: 'Maryland' },
        { code: 'MA', name: 'Massachusetts' },
        { code: 'MI', name: 'Michigan' },
        { code: 'MN', name: 'Minnesota' },
        { code: 'MS', name: 'Mississippi' },
        { code: 'MO', name: 'Missouri' },
        { code: 'MT', name: 'Montana' },
        { code: 'NE', name: 'Nebraska' },
        { code: 'NV', name: 'Nevada' },
        { code: 'NH', name: 'New Hampshire' },
        { code: 'NJ', name: 'New Jersey' },
        { code: 'NM', name: 'New Mexico' },
        { code: 'NY', name: 'New York' },
        { code: 'NC', name: 'North Carolina' },
        { code: 'ND', name: 'North Dakota' },
        { code: 'OH', name: 'Ohio' },
        { code: 'OK', name: 'Oklahoma' },
        { code: 'OR', name: 'Oregon' },
        { code: 'PA', name: 'Pennsylvania' },
        { code: 'RI', name: 'Rhode Island' },
        { code: 'SC', name: 'South Carolina' },
        { code: 'SD', name: 'South Dakota' },
        { code: 'TN', name: 'Tennessee' },
        { code: 'TX', name: 'Texas' },
        { code: 'UT', name: 'Utah' },
        { code: 'VT', name: 'Vermont' },
        { code: 'VA', name: 'Virginia' },
        { code: 'WA', name: 'Washington' },
        { code: 'WV', name: 'West Virginia' },
        { code: 'WI', name: 'Wisconsin' },
        { code: 'WY', name: 'Wyoming' },
        { code: 'DC', name: 'District of Columbia' }
      ]
    }
  },
  methods: {
    validateForm() {
      this.errors = {}
      let isValid = true

      // Validate name
      if (!this.chapterForm.name.trim()) {
        this.errors.name = 'Chapter name is required'
        isValid = false
      } else if (this.chapterForm.name.length < 2) {
        this.errors.name = 'Chapter name must be at least 2 characters'
        isValid = false
      } else if (this.chapterForm.name.length > 100) {
        this.errors.name = 'Chapter name cannot exceed 100 characters'
        isValid = false
      }

      // Validate university name
      if (!this.chapterForm.universityName.trim()) {
        this.errors.universityName = 'University name is required'
        isValid = false
      } else if (this.chapterForm.universityName.length < 2) {
        this.errors.universityName = 'University name must be at least 2 characters'
        isValid = false
      } else if (this.chapterForm.universityName.length > 150) {
        this.errors.universityName = 'University name cannot exceed 150 characters'
        isValid = false
      }

      // Validate state
      if (!this.chapterForm.state) {
        this.errors.state = 'State is required'
        isValid = false
      }

      // Validate city
      if (!this.chapterForm.city.trim()) {
        this.errors.city = 'City is required'
        isValid = false
      } else if (this.chapterForm.city.length < 2) {
        this.errors.city = 'City name must be at least 2 characters'
        isValid = false
      } else if (this.chapterForm.city.length > 100) {
        this.errors.city = 'City name cannot exceed 100 characters'
        isValid = false
      }

      // Validate description (optional but has limits)
      if (this.chapterForm.description.length > 500) {
        this.errors.description = 'Description cannot exceed 500 characters'
        isValid = false
      }

      return isValid
    },

    async createChapter() {
      console.log('🔥 Starting chapter creation process...')
      
      if (!this.validateForm()) {
        console.log('❌ Form validation failed:', this.errors)
        return
      }

      this.saving = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        console.log('📝 Preparing chapter data:', this.chapterForm)
        
        // Prepare the request payload
        const chapterData = {
          name: this.chapterForm.name.trim(),
          universityName: this.chapterForm.universityName.trim(),
          state: this.chapterForm.state,
          city: this.chapterForm.city.trim(),
          description: this.chapterForm.description.trim()
        }

        console.log('📤 Sending request to /api/chapters/with-institution:', chapterData)

        // Make the API call to the backend
        const response = await fetch('/api/chapters/with-institution', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(chapterData)
        })

        console.log('📡 API Response status:', response.status)

        if (response.ok) {
          const result = await response.json()
          console.log('✅ Chapter created successfully:', result)
          
          this.successMessage = `Chapter "${result.name}" has been created successfully!`
          this.resetForm()
          
          // Redirect to chapters list after a short delay
          setTimeout(() => {
            this.$router.push('/chapters')
          }, 2000)
        } else {
          const errorData = await response.json()
          console.error('❌ API Error:', errorData)
          this.errorMessage = errorData.error || errorData.message || 'Failed to create chapter. Please try again.'
        }
      } catch (error) {
        console.error('💥 Network/Unexpected Error:', error)
        this.errorMessage = 'Network error. Please check your connection and try again.'
      } finally {
        this.saving = false
      }
    },

    async fetchInstitutions() {
      try {
        console.log('🏫 Fetching institutions from API...')
        const response = await fetch('/api/institutions')
        if (response.ok) {
          this.institutions = await response.json()
          console.log(`✅ Loaded ${this.institutions.length} institutions`)
        } else {
          console.error('❌ Failed to fetch institutions:', response.status)
        }
      } catch (error) {
        console.error('❌ Error fetching institutions:', error)
      }
    },

    onUniversityChange() {
      // Find the selected institution and auto-populate location fields
      const selectedInstitution = this.institutions.find(inst => inst.name === this.chapterForm.universityName)
      if (selectedInstitution) {
        console.log('🏫 Auto-populating location from institution:', selectedInstitution)
        this.chapterForm.state = selectedInstitution.state
        this.chapterForm.city = selectedInstitution.location
        
        // Clear any validation errors for these fields
        delete this.errors.state
        delete this.errors.city
      }
    },

    resetForm() {
      this.chapterForm = {
        name: '',
        universityName: '',
        state: '',
        city: '',
        description: ''
      }
      this.errors = {}
    }
  },

  async mounted() {
    await this.fetchInstitutions()
  }
}
</script>

<style scoped>
.required::after {
  content: ' *';
  color: #dc3545;
}

.form-control:focus,
.form-select:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

.card {
  border: none;
  box-shadow: 0 0.125rem 0.5rem rgba(0, 0, 0, 0.1);
}

.btn-primary {
  background: linear-gradient(45deg, #0d6efd, #0b5ed7);
  border: none;
}

.btn-primary:hover {
  background: linear-gradient(45deg, #0b5ed7, #0a58ca);
}

.alert {
  border-radius: 0.5rem;
}

.bg-light {
  background-color: #f8f9fa !important;
}
</style>