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
              <form>
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label for="firstName" class="form-label">First Name</label>
                    <input 
                      type="text" 
                      class="form-control" 
                      id="firstName"
                      :value="currentUser.firstName"
                      readonly
                    >
                  </div>
                  <div class="col-md-6 mb-3">
                    <label for="lastName" class="form-label">Last Name</label>
                    <input 
                      type="text" 
                      class="form-control" 
                      id="lastName"
                      :value="currentUser.lastName"
                      readonly
                    >
                  </div>
                </div>
                <div class="mb-3">
                  <label for="email" class="form-label">Email Address</label>
                  <input 
                    type="email" 
                    class="form-control" 
                    id="email"
                    :value="currentUser.email"
                    readonly
                  >
                </div>
                <div class="mb-3" v-if="currentUser.chapter">
                  <label for="chapter" class="form-label">Chapter</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="chapter"
                    :value="`${currentUser.chapter.name} - ${currentUser.chapter.university}`"
                    readonly
                  >
                </div>
                <div class="text-muted">
                  <i class="bi bi-info-circle me-2"></i>
                  Profile editing will be available in a future update.
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
                <button class="btn btn-outline-primary" disabled>
                  <i class="bi bi-pencil me-2"></i>
                  Edit Profile
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
import { computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'Profile',
  setup() {
    const router = useRouter()

    const currentUser = computed(() => {
      try {
        const user = localStorage.getItem('user')
        return user ? JSON.parse(user) : null
      } catch {
        return null
      }
    })

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

    return {
      currentUser,
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