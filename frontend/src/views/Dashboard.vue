<template>
  <div class="dashboard">
    <div class="container-fluid">
      <!-- Header -->
      <div class="dashboard-header mb-4">
        <div class="row align-items-center">
          <div class="col">
            <h1 class="h2 mb-0">
              <i class="fas fa-tachometer-alt text-primary me-3"></i>
              System Dashboard
            </h1>
            <p class="text-muted mb-0">
              <span v-if="currentUser">Welcome back, {{ currentUser.username }}! | </span>
              Campus Chapter Organizer Management
            </p>
          </div>
          <div class="col-auto">
            <button 
              class="btn btn-outline-primary btn-sm" 
              @click="fetchPublicStats"
              :disabled="loading"
            >
              <i class="fas fa-sync-alt me-2" :class="{ 'fa-spin': loading }"></i>
              {{ loading ? 'Loading...' : 'Refresh' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Error Message -->
      <div v-if="error" class="alert alert-warning alert-dismissible fade show mb-4" role="alert">
        <i class="fas fa-exclamation-triangle me-2"></i>
        {{ error }}
        <button type="button" class="btn-close" @click="error = null"></button>
      </div>

      <!-- Quick Stats -->
      <div class="row g-4 mb-4">
        <div class="col-md-3">
          <div class="card border-0 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div class="flex-shrink-0">
                  <div class="bg-primary bg-gradient rounded-3 p-3">
                    <i class="fas fa-users text-white fa-lg"></i>
                  </div>
                </div>
                <div class="flex-grow-1 ms-3">
                  <h6 class="card-title mb-1">Total Members</h6>
                  <h4 class="mb-0 text-primary">{{ totalMembers }}</h4>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="col-md-3">
          <div class="card border-0 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div class="flex-shrink-0">
                  <div class="bg-success bg-gradient rounded-3 p-3">
                    <i class="fas fa-calendar-alt text-white fa-lg"></i>
                  </div>
                </div>
                <div class="flex-grow-1 ms-3">
                  <h6 class="card-title mb-1">Active Events</h6>
                  <h4 class="mb-0 text-success">{{ activeEvents }}</h4>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="col-md-3">
          <div class="card border-0 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div class="flex-shrink-0">
                  <div class="bg-info bg-gradient rounded-3 p-3">
                    <i class="fas fa-university text-white fa-lg"></i>
                  </div>
                </div>
                <div class="flex-grow-1 ms-3">
                  <h6 class="card-title mb-1">Total Chapters</h6>
                  <h4 class="mb-0 text-info">{{ totalChapters }}</h4>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="col-md-3">
          <div class="card border-0 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div class="flex-shrink-0">
                  <div class="bg-warning bg-gradient rounded-3 p-3">
                    <i class="fas fa-check-circle text-white fa-lg"></i>
                  </div>
                </div>
                <div class="flex-grow-1 ms-3">
                  <h6 class="card-title mb-1">Total RSVPs</h6>
                  <h4 class="mb-0 text-warning">{{ totalRsvps }}</h4>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="row g-4">
        <div class="col-md-8">
          <div class="card border-0 shadow-sm">
            <div class="card-header bg-white border-0">
              <h5 class="card-title mb-0">
                <i class="fas fa-bolt text-primary me-2"></i>
                Quick Actions
              </h5>
            </div>
            <div class="card-body">
              <div class="row g-3">
                <div class="col-md-4">
                  <router-link to="/members" class="btn btn-outline-primary w-100 h-100 d-flex flex-column align-items-center justify-content-center py-3">
                    <i class="fas fa-user-plus fa-2x mb-2"></i>
                    <span>Add Member</span>
                  </router-link>
                </div>
                <div class="col-md-4">
                  <router-link to="/events" class="btn btn-outline-success w-100 h-100 d-flex flex-column align-items-center justify-content-center py-3">
                    <i class="fas fa-calendar-plus fa-2x mb-2"></i>
                    <span>Create Event</span>
                  </router-link>
                </div>
                <div class="col-md-4">
                  <router-link to="/chapters" class="btn btn-outline-info w-100 h-100 d-flex flex-column align-items-center justify-content-center py-3">
                    <i class="fas fa-university fa-2x mb-2"></i>
                    <span>Manage Chapters</span>
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="col-md-4">
          <div class="card border-0 shadow-sm">
            <div class="card-header bg-white border-0">
              <h5 class="card-title mb-0">
                <i class="fas fa-cog text-secondary me-2"></i>
                System Status
              </h5>
            </div>
            <div class="card-body">
              <div class="d-flex align-items-center mb-3">
                <div class="flex-shrink-0">
                  <div class="bg-success rounded-circle p-2">
                    <i class="fas fa-check text-white"></i>
                  </div>
                </div>
                <div class="flex-grow-1 ms-3">
                  <h6 class="mb-0">System Operational</h6>
                  <small class="text-muted">All services running normally</small>
                </div>
              </div>
              
              <div class="progress mb-2" style="height: 6px;">
                <div class="progress-bar bg-success" style="width: 95%"></div>
              </div>
              <small class="text-muted">95% System Health</small>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'Dashboard',
  setup() {
    const totalMembers = ref(0)
    const activeEvents = ref(0)
    const totalChapters = ref(0)
    const totalRsvps = ref(0)
    const loading = ref(true)
    const error = ref(null)
    const currentUser = ref(null)

    // Check for logged in user
    const checkUser = () => {
      const storedUser = localStorage.getItem('user')
      if (storedUser) {
        try {
          currentUser.value = JSON.parse(storedUser)
        } catch (error) {
          console.error('Error parsing stored user:', error)
          currentUser.value = null
        }
      }
    }

    // Check user on component mount
    onMounted(() => {
      checkUser()
    })

    const fetchPublicStats = async () => {
      try {
        loading.value = true
        const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
        const response = await fetch(`${API_BASE_URL}/api/stats/public/overview`)
        
        if (!response.ok) {
          throw new Error('Failed to fetch statistics')
        }
        
        const data = await response.json()
        
        totalMembers.value = data.totalMembers || 0
        activeEvents.value = data.totalEvents || 0
        totalChapters.value = data.totalChapters || 0
        totalRsvps.value = data.totalRsvps || 0
        
        error.value = null
      } catch (err) {
        console.error('Error fetching public stats:', err)
        error.value = 'Unable to load statistics'
        // Set default values on error
        totalMembers.value = 0
        activeEvents.value = 0
        totalChapters.value = 0
        totalRsvps.value = 0
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      fetchPublicStats()
    })
    
    return {
      totalMembers,
      activeEvents,
      totalChapters,
      totalRsvps,
      loading,
      error,
      fetchPublicStats,
      currentUser
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 2rem;
  min-height: 100vh;
  background-color: #f8f9fa;
}

.dashboard-header {
  background: white;
  padding: 1.5rem;
  border-radius: 0.5rem;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}

.btn-outline-primary:hover,
.btn-outline-success:hover,
.btn-outline-info:hover {
  transform: translateY(-1px);
}

.progress {
  border-radius: 3px;
}

.bg-gradient {
  background: linear-gradient(135deg, var(--bs-primary) 0%, var(--bs-primary-dark, #0056b3) 100%);
}

.bg-success.bg-gradient {
  background: linear-gradient(135deg, var(--bs-success) 0%, var(--bs-success-dark, #0f5132) 100%);
}

.bg-info.bg-gradient {
  background: linear-gradient(135deg, var(--bs-info) 0%, var(--bs-info-dark, #055160) 100%);
}

.bg-warning.bg-gradient {
  background: linear-gradient(135deg, var(--bs-warning) 0%, var(--bs-warning-dark, #664d03) 100%);
}

@media (max-width: 768px) {
  .dashboard {
    padding: 1rem;
  }
  
  .dashboard-header {
    text-align: center;
  }
  
  .dashboard-header .row {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>