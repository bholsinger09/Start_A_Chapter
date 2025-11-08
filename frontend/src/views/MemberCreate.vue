<template>
  <div class="container">
    <!-- Header -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="display-6 fw-bold text-primary">
              <i class="bi bi-person-plus me-3"></i>{{ isEditing ? 'Edit Member' : 'Add New Member' }}
            </h1>
            <p class="lead text-muted">{{ isEditing ? 'Update member information' : 'Create a new member profile' }}</p>
          </div>
          <router-link to="/members" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-2"></i>Back to Members
          </router-link>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
    </div>

    <!-- Member Form -->
    <div v-else class="row">
      <div class="col-lg-8">
        <div class="card shadow-sm">
          <div class="card-body">
            <form @submit.prevent="saveMember">
              <!-- Personal Information Section -->
              <div class="mb-4">
                <h5 class="text-primary mb-3">
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
                      v-model="member.firstName"
                      required
                      maxlength="50"
                      :class="{ 'is-invalid': errors.firstName }"
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
                      v-model="member.lastName"
                      required
                      maxlength="50"
                      :class="{ 'is-invalid': errors.lastName }"
                    >
                    <div v-if="errors.lastName" class="invalid-feedback">
                      {{ errors.lastName }}
                    </div>
                  </div>

                  <div class="col-md-6">
                    <label for="email" class="form-label fw-semibold">
                      Email Address *
                    </label>
                    <input
                      type="email"
                      class="form-control"
                      id="email"
                      v-model="member.email"
                      required
                      maxlength="100"
                      :class="{ 'is-invalid': errors.email }"
                    >
                    <div v-if="errors.email" class="invalid-feedback">
                      {{ errors.email }}
                    </div>
                  </div>

                  <div class="col-md-6">
                    <label for="username" class="form-label fw-semibold">
                      Username
                    </label>
                    <input
                      type="text"
                      class="form-control"
                      id="username"
                      v-model="member.username"
                      maxlength="30"
                      :class="{ 'is-invalid': errors.username }"
                      placeholder="Optional - for login access"
                    >
                    <div v-if="errors.username" class="invalid-feedback">
                      {{ errors.username }}
                    </div>
                  </div>

                  <div class="col-md-6">
                    <label for="phoneNumber" class="form-label fw-semibold">
                      Phone Number
                    </label>
                    <input
                      type="tel"
                      class="form-control"
                      id="phoneNumber"
                      v-model="member.phoneNumber"
                      maxlength="15"
                      placeholder="e.g., (555) 123-4567"
                    >
                  </div>
                </div>
              </div>

              <!-- Chapter & Role Section -->
              <div class="mb-4">
                <h5 class="text-primary mb-3">
                  <i class="bi bi-building me-2"></i>Chapter Assignment
                </h5>
                
                <div class="row g-3">
                  <div class="col-md-6">
                    <label for="chapterId" class="form-label fw-semibold">
                      Chapter
                    </label>
                    <select
                      class="form-select"
                      id="chapterId"
                      v-model="member.chapterId"
                      :class="{ 'is-invalid': errors.chapterId }"
                    >
                      <option value="">No Chapter (Unassigned)</option>
                      <option v-for="chapter in chapters" :key="chapter.id" :value="chapter.id">
                        {{ chapter.name }} - {{ chapter.state }}
                      </option>
                    </select>
                    <div class="form-text">
                      Members can be created without a chapter and assigned later
                    </div>
                    <div v-if="errors.chapterId" class="invalid-feedback">
                      {{ errors.chapterId }}
                    </div>
                  </div>

                  <div class="col-md-6">
                    <label for="role" class="form-label fw-semibold">
                      Role *
                    </label>
                    <select
                      class="form-select"
                      id="role"
                      v-model="member.role"
                      required
                      :class="{ 'is-invalid': errors.role }"
                    >
                      <option value="">Select Role</option>
                      <option value="PRESIDENT">President</option>
                      <option value="VICE_PRESIDENT">Vice President</option>
                      <option value="TREASURER">Treasurer</option>
                      <option value="SECRETARY">Secretary</option>
                      <option value="MEMBER">Member</option>
                    </select>
                    <div v-if="errors.role" class="invalid-feedback">
                      {{ errors.role }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- Academic Information Section -->
              <div class="mb-4">
                <h5 class="text-primary mb-3">
                  <i class="bi bi-mortarboard me-2"></i>Academic Information
                </h5>
                
                <div class="row g-3">
                  <div class="col-md-6">
                    <label for="major" class="form-label fw-semibold">
                      Major / Field of Study
                    </label>
                    <input
                      type="text"
                      class="form-control"
                      id="major"
                      v-model="member.major"
                      maxlength="100"
                      placeholder="e.g., Political Science, Business Administration"
                    >
                  </div>

                  <div class="col-md-6">
                    <label for="graduationYear" class="form-label fw-semibold">
                      Expected Graduation Year
                    </label>
                    <input
                      type="number"
                      class="form-control"
                      id="graduationYear"
                      v-model="member.graduationYear"
                      min="2020"
                      max="2035"
                      placeholder="e.g., 2025"
                    >
                  </div>
                </div>
              </div>

              <!-- Status Section -->
              <div class="mb-4">
                <h5 class="text-primary mb-3">
                  <i class="bi bi-toggle-on me-2"></i>Status
                </h5>
                
                <div class="form-check form-switch">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    id="active"
                    v-model="member.active"
                  >
                  <label class="form-check-label fw-semibold" for="active">
                    Active Member
                  </label>
                  <div class="form-text">
                    {{ member.active ? 'Member is active and can access the system.' : 'Member is inactive and cannot access the system.' }}
                  </div>
                </div>
              </div>

              <!-- Form Actions -->
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary me-2"
                    @click="resetForm"
                    v-if="!isEditing"
                  >
                    <i class="bi bi-arrow-clockwise me-2"></i>Reset Form
                  </button>
                </div>
                <div>
                  <button 
                    type="submit" 
                    class="btn btn-primary btn-lg"
                    :disabled="saving"
                  >
                    <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                    <i v-else class="bi bi-check-circle me-2"></i>
                    {{ saving ? 'Saving...' : (isEditing ? 'Update Member' : 'Create Member') }}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- Sidebar -->
      <div class="col-lg-4">
        <!-- Help Card -->
        <div class="card shadow-sm mb-4">
          <div class="card-header bg-light">
            <h6 class="mb-0 fw-semibold">
              <i class="bi bi-info-circle me-2"></i>Member Information
            </h6>
          </div>
          <div class="card-body">
            <ul class="list-unstyled mb-0">
              <li class="mb-2">
                <i class="bi bi-check2 text-success me-2"></i>
                <strong>Required fields:</strong> First name, last name, email, and role
              </li>
              <li class="mb-2">
                <i class="bi bi-check2 text-success me-2"></i>
                <strong>Chapter assignment:</strong> Optional - members can join chapters later
              </li>
              <li class="mb-2">
                <i class="bi bi-check2 text-success me-2"></i>
                <strong>Username:</strong> Only needed if member will have login access
              </li>
              <li>
                <i class="bi bi-check2 text-success me-2"></i>
                <strong>Status:</strong> Active members can access the system and participate
              </li>
            </ul>
          </div>
        </div>

        <!-- Chapter Info -->
        <div class="card shadow-sm" v-if="selectedChapter">
          <div class="card-header bg-light">
            <h6 class="mb-0 fw-semibold">
              <i class="bi bi-building me-2"></i>Selected Chapter
            </h6>
          </div>
          <div class="card-body">
            <h6 class="fw-semibold">{{ selectedChapter.name }}</h6>
            <p class="text-muted mb-2">{{ selectedChapter.university }}</p>
            <p class="text-muted mb-0">
              <i class="bi bi-geo-alt me-1"></i>{{ selectedChapter.city }}, {{ selectedChapter.state }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from '../services/api'

export default {
  name: 'MemberCreate',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const member = ref({
      firstName: '',
      lastName: '',
      email: '',
      username: '',
      phoneNumber: '',
      chapterId: '',
      role: '',
      major: '',
      graduationYear: '',
      active: true
    })
    
    const chapters = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const error = ref('')
    const errors = ref({})
    const isEditing = ref(false)
    const memberId = ref(null)

    // Computed properties
    const selectedChapter = computed(() => {
      if (!member.value.chapterId) return null
      return chapters.value.find(chapter => chapter.id == member.value.chapterId)
    })

    // Methods
    const loadChapters = async () => {
      try {
        const response = await axios.get('/api/chapters')
        chapters.value = response.data
      } catch (err) {
        console.error('Error loading chapters:', err)
      }
    }

    const loadMemberForEdit = async (id) => {
      try {
        loading.value = true
        const response = await axios.get(`/api/members/${id}`)
        const memberData = response.data
        
        member.value = {
          firstName: memberData.firstName,
          lastName: memberData.lastName,
          email: memberData.email,
          username: memberData.username || '',
          phoneNumber: memberData.phoneNumber || '',
          chapterId: memberData.chapterId || '',
          role: memberData.role,
          major: memberData.major || '',
          graduationYear: memberData.graduationYear || '',
          active: memberData.active
        }
        
        error.value = ''
      } catch (err) {
        error.value = 'Failed to load member for editing'
        console.error('Error loading member:', err)
      } finally {
        loading.value = false
      }
    }

    const validateForm = () => {
      errors.value = {}

      if (!member.value.firstName.trim()) {
        errors.value.firstName = 'First name is required'
      }

      if (!member.value.lastName.trim()) {
        errors.value.lastName = 'Last name is required'
      }

      if (!member.value.email.trim()) {
        errors.value.email = 'Email is required'
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(member.value.email)) {
        errors.value.email = 'Please enter a valid email address'
      }

      if (!member.value.role) {
        errors.value.role = 'Role is required'
      }

      if (member.value.username && member.value.username.length < 3) {
        errors.value.username = 'Username must be at least 3 characters'
      }

      return Object.keys(errors.value).length === 0
    }

    const saveMember = async () => {
      if (!validateForm()) return

      saving.value = true
      
      try {
        const memberData = {
          firstName: member.value.firstName.trim(),
          lastName: member.value.lastName.trim(),
          email: member.value.email.trim(),
          username: member.value.username.trim() || null,
          phoneNumber: member.value.phoneNumber.trim() || null,
          chapterId: member.value.chapterId || null,
          role: member.value.role,
          major: member.value.major.trim() || null,
          graduationYear: member.value.graduationYear || null,
          active: member.value.active
        }

        let response
        if (isEditing.value) {
          response = await axios.put(`/api/members/${memberId.value}`, memberData)
        } else {
          response = await axios.post('/api/members', memberData)
        }
        
        // Redirect to member detail or list page
        if (response.data.id) {
          router.push(`/members/${response.data.id}`)
        } else {
          router.push('/members')
        }
      } catch (err) {
        if (err.response?.status === 400) {
          error.value = err.response.data.message || 'Invalid member data'
        } else {
          error.value = 'Failed to save member'
        }
        console.error('Error saving member:', err)
      } finally {
        saving.value = false
      }
    }

    const resetForm = () => {
      if (confirm('Are you sure you want to reset the form? All changes will be lost.')) {
        member.value = {
          firstName: '',
          lastName: '',
          email: '',
          username: '',
          phoneNumber: '',
          chapterId: '',
          role: '',
          major: '',
          graduationYear: '',
          active: true
        }
        errors.value = {}
      }
    }

    // Lifecycle
    onMounted(() => {
      loadChapters()
      
      // Check if we're editing an existing member
      const editId = route.params.id
      if (editId) {
        isEditing.value = true
        memberId.value = parseInt(editId)
        loadMemberForEdit(memberId.value)
      }
    })

    return {
      member,
      chapters,
      loading,
      saving,
      error,
      errors,
      isEditing,
      selectedChapter,
      saveMember,
      resetForm,
      validateForm
    }
  }
}
</script>

<style scoped>
.form-control:focus,
.form-select:focus {
  border-color: var(--bs-primary);
  box-shadow: 0 0 0 0.25rem rgba(var(--bs-primary-rgb), 0.25);
}

.card {
  border: 1px solid #e3e6f0;
}

.form-check-input:checked {
  background-color: var(--bs-primary);
  border-color: var(--bs-primary);
}

@media (max-width: 768px) {
  .display-6 {
    font-size: 2rem;
  }
}
</style>