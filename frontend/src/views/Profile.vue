<template>
  <div class="profile">
    <div class="container">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col-12">
          <h2>
            <i class="bi bi-person-circle me-2"></i>
            My Profile
          </h2>
          <p class="text-muted">Manage your personal information and preferences.</p>
        </div>
      </div>

      <div class="row">
        <!-- Profile Information -->
        <div class="col-md-8">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-person me-2"></i>
                Personal Information
              </h5>
            </div>
            <div class="card-body" v-if="currentUser">
              <form @submit.prevent="saveProfile">
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label for="firstName" class="form-label">First Name</label>
                    <input 
                      type="text" 
                      class="form-control" 
                      id="firstName"
                      v-model="profileForm.firstName"
                      :readonly="!editMode"
                      :class="{ 'form-control-plaintext': !editMode }"
                    >
                  </div>
                  <div class="col-md-6 mb-3">
                    <label for="lastName" class="form-label">Last Name</label>
                    <input 
                      type="text" 
                      class="form-control" 
                      id="lastName"
                      v-model="profileForm.lastName"
                      :readonly="!editMode"
                      :class="{ 'form-control-plaintext': !editMode }"
                    >
                  </div>
                </div>
                <div class="mb-3">
                  <label for="email" class="form-label">Email Address</label>
                  <input 
                    type="email" 
                    class="form-control" 
                    id="email"
                    v-model="profileForm.email"
                    :readonly="!editMode"
                    :class="{ 'form-control-plaintext': !editMode }"
                  >
                </div>
                
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label for="username" class="form-label">Username</label>
                    <input 
                      type="text" 
                      class="form-control" 
                      id="username"
                      v-model="profileForm.username"
                      :readonly="!editMode"
                      :class="{ 'form-control-plaintext': !editMode }"
                      placeholder="Optional"
                    >
                  </div>
                  <div class="col-md-6 mb-3">
                    <label for="phoneNumber" class="form-label">Phone Number</label>
                    <input 
                      type="tel" 
                      class="form-control" 
                      id="phoneNumber"
                      v-model="profileForm.phoneNumber"
                      :readonly="!editMode"
                      :class="{ 'form-control-plaintext': !editMode }"
                      placeholder="Optional"
                    >
                  </div>
                </div>

                <div class="mb-3">
                  <label for="chapter" class="form-label">Chapter</label>
                  <select 
                    v-if="editMode"
                    class="form-select" 
                    id="chapter"
                    v-model="profileForm.chapterId"
                  >
                    <option value="">No Chapter</option>
                    <option v-for="chapter in availableChapters" :key="chapter.id" :value="chapter.id">
                      {{ chapter.name }} - {{ chapter.universityName }}
                    </option>
                  </select>
                  <input 
                    v-else
                    type="text" 
                    class="form-control-plaintext" 
                    id="chapter"
                    :value="getChapterDisplayName()"
                    readonly
                  >
                </div>

                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label for="major" class="form-label">Major</label>
                    <input 
                      type="text" 
                      class="form-control" 
                      id="major"
                      v-model="profileForm.major"
                      :readonly="!editMode"
                      :class="{ 'form-control-plaintext': !editMode }"
                      placeholder="Optional"
                    >
                  </div>
                  <div class="col-md-6 mb-3">
                    <label for="graduationYear" class="form-label">Graduation Year</label>
                    <input 
                      type="text" 
                      class="form-control" 
                      id="graduationYear"
                      v-model="profileForm.graduationYear"
                      :readonly="!editMode"
                      :class="{ 'form-control-plaintext': !editMode }"
                      placeholder="e.g., 2024"
                    >
                  </div>
                </div>

                <div v-if="profileError" class="alert alert-danger">
                  <i class="bi bi-exclamation-triangle me-2"></i>
                  {{ profileError }}
                </div>

                <div v-if="profileSuccess" class="alert alert-success">
                  <i class="bi bi-check-circle me-2"></i>
                  {{ profileSuccess }}
                </div>

                <div class="text-muted" v-if="!editMode">
                  <i class="bi bi-info-circle me-2"></i>
                  Click "Edit Profile" to modify your information and chapter association.
                </div>
              </form>
            </div>
            <div class="card-body text-center py-5" v-else>
              <i class="bi bi-person-x display-4 text-muted mb-3"></i>
              <h5 class="text-muted">No Profile Data</h5>
              <p class="text-muted">Please log in to view your profile.</p>
            </div>
          </div>
        </div>

        <!-- Profile Actions -->
        <div class="col-md-4">
          <div class="card">
            <div class="card-header">
              <h6 class="mb-0">
                <i class="bi bi-gear me-2"></i>
                Quick Actions
              </h6>
            </div>
            <div class="card-body">
              <div class="d-grid gap-2">
                <button class="btn btn-outline-primary" @click="toggleEdit" :disabled="!currentUser">
                  <i class="bi bi-pencil me-2" v-if="!editMode"></i>
                  <i class="bi bi-check2 me-2" v-else></i>
                  {{ editMode ? 'Save Changes' : 'Edit Profile' }}
                </button>
                <button class="btn btn-outline-secondary" disabled>
                  <i class="bi bi-key me-2"></i>
                  Change Password
                </button>
                <hr>
                <router-link to="/settings" class="btn btn-outline-info">
                  <i class="bi bi-gear me-2"></i>
                  Settings
                </router-link>
                <button class="btn btn-outline-danger" @click="logout">
                  <i class="bi bi-box-arrow-right me-2"></i>
                  Logout
                </button>
              </div>
            </div>
          </div>

          <!-- Account Info -->
          <div class="card mt-3" v-if="currentUser">
            <div class="card-header">
              <h6 class="mb-0">
                <i class="bi bi-info me-2"></i>
                Account Information
              </h6>
            </div>
            <div class="card-body">
              <small class="text-muted d-block mb-2">
                <strong>User ID:</strong> {{ currentUser.id }}
              </small>
              <small class="text-muted d-block mb-2">
                <strong>Login Time:</strong> {{ formatLoginTime(currentUser.loginTime) }}
              </small>
              <small class="text-muted d-block">
                <strong>Action:</strong> {{ currentUser.action }}
              </small>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import { useAuth } from '@/composables/useAuth'

export default {
  name: 'Profile',
  setup() {
    const router = useRouter()
    const { currentUser, updateUserProfile, initAuth } = useAuth()
    
    const editMode = ref(false)
    const profileLoading = ref(false)
    const profileError = ref('')
    const profileSuccess = ref('')
    const availableChapters = ref([])

    const profileForm = ref({
      firstName: '',
      lastName: '',
      email: '',
      username: '',
      phoneNumber: '',
      chapterId: '',
      major: '',
      graduationYear: ''
    })

    // Initialize form when user changes
    const initializeForm = () => {
      if (currentUser.value) {
        profileForm.value = {
          firstName: currentUser.value.firstName || '',
          lastName: currentUser.value.lastName || '',
          email: currentUser.value.email || '',
          username: currentUser.value.username || '',
          phoneNumber: currentUser.value.phoneNumber || '',
          chapterId: currentUser.value.chapter?.id || '',
          major: currentUser.value.major || '',
          graduationYear: currentUser.value.graduationYear || ''
        }
      }
    }

    // Load chapters for selection
    const loadChapters = async () => {
      try {
        const response = await api.get('/api/chapters')
        availableChapters.value = response.data
      } catch (error) {
        console.error('Error loading chapters:', error)
      }
    }

    const toggleEdit = () => {
      if (editMode.value) {
        saveProfile()
      } else {
        editMode.value = true
        profileError.value = ''
        profileSuccess.value = ''
      }
    }

    const saveProfile = async () => {
      if (!currentUser.value) return

      try {
        profileLoading.value = true
        profileError.value = ''
        profileSuccess.value = ''

        // Find the member in the backend and update
        const memberResponse = await api.get(`/api/members/email/${currentUser.value.email}`)
        const memberId = memberResponse.data.id

        const updateData = {
          firstName: profileForm.value.firstName.trim(),
          lastName: profileForm.value.lastName.trim(),
          email: profileForm.value.email.trim(),
          username: profileForm.value.username?.trim() || null,
          phoneNumber: profileForm.value.phoneNumber?.trim() || null,
          major: profileForm.value.major?.trim() || null,
          graduationYear: profileForm.value.graduationYear?.trim() || null
        }

        // Update member information
        await api.put(`/api/members/${memberId}`, updateData)

        // If chapter changed, transfer member
        if (profileForm.value.chapterId && profileForm.value.chapterId !== currentUser.value.chapter?.id) {
          await api.put(`/api/members/${memberId}/transfer/${profileForm.value.chapterId}`)
        }

        // Update localStorage with new data
        const updatedUser = {
          ...currentUser.value,
          firstName: updateData.firstName,
          lastName: updateData.lastName,
          email: updateData.email,
          username: updateData.username,
          phoneNumber: updateData.phoneNumber,
          major: updateData.major,
          graduationYear: updateData.graduationYear
        }

        // Update chapter info if changed
        if (profileForm.value.chapterId) {
          const chapter = availableChapters.value.find(c => c.id == profileForm.value.chapterId)
          if (chapter) {
            updatedUser.chapter = chapter
          }
        } else {
          updatedUser.chapter = null
        }

        // Use auth composable to update user profile
        updateUserProfile(updatedUser)

        profileSuccess.value = 'Profile updated successfully!'
        editMode.value = false

      } catch (error) {
        console.error('Error updating profile:', error)
        profileError.value = error.response?.data?.message || 'Failed to update profile. Please try again.'
      } finally {
        profileLoading.value = false
      }
    }

    const getChapterDisplayName = () => {
      if (!currentUser.value?.chapter) return 'No Chapter'
      return `${currentUser.value.chapter.name} - ${
        currentUser.value.chapter.universityName || currentUser.value.chapter.university
      }`
    }

    const logout = () => {
      localStorage.removeItem('user')
      router.push('/login')
    }

    const formatLoginTime = (timestamp) => {
      if (!timestamp) return 'Unknown'
      try {
        return new Date(timestamp).toLocaleString()
      } catch {
        return 'Invalid date'
      }
    }

    // Initialize on mount
    onMounted(() => {
      initAuth()
      initializeForm()
      loadChapters()
    })

    // Watch for user changes
    watch(currentUser, initializeForm, { immediate: true })

    return {
      currentUser,
      editMode,
      profileForm,
      profileLoading,
      profileError,
      profileSuccess,
      availableChapters,
      toggleEdit,
      saveProfile,
      getChapterDisplayName,
      logout,
      formatLoginTime
    }
  }
}
</script>

<style scoped>
.card {
  border-radius: 0.5rem;
  border: none;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.form-control[readonly] {
  background-color: #f8f9fa;
}

.display-4 {
  font-size: 2.5rem;
}
</style>