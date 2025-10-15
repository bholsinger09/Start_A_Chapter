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
            <p class="text-muted mb-0">Campus Chapter Organizer Management</p>
          </div>
          <div class="col-auto">
            <button class="btn btn-outline-primary btn-sm">
              <i class="fas fa-sync-alt me-2"></i>
              Refresh
            </button>
          </div>
        </div>
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
import { ref } from 'vue'

export default {
  name: 'Dashboard',
  setup() {
    const totalMembers = ref(156)
    const activeEvents = ref(12)
    const totalChapters = ref(8)
    const totalRsvps = ref(342)
    
    return {
      totalMembers,
      activeEvents,
      totalChapters,
      totalRsvps
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