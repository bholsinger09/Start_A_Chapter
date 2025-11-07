<template>
  <div class="container-fluid py-4">
    <!-- Header -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="display-4 fw-bold text-primary">
              <i class="bi bi-speedometer2 me-3"></i>Dashboard
            </h1>
            <p class="lead text-muted">Welcome to Campus Chapter Organizer</p>
          </div>
          <router-link to="/chapters/create" class="btn btn-primary btn-lg">
            <i class="bi bi-plus-circle me-2"></i>Create Chapter
          </router-link>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="row mb-4">
      <div class="col-lg-3 col-md-6 mb-3">
        <div class="card text-center bg-primary text-white">
          <div class="card-body">
            <i class="bi bi-building display-4 mb-2"></i>
            <h3>{{ totalChapters }}</h3>
            <p class="mb-0">Total Chapters</p>
          </div>
        </div>
      </div>
      <div class="col-lg-3 col-md-6 mb-3">
        <div class="card text-center bg-success text-white">
          <div class="card-body">
            <i class="bi bi-people-fill display-4 mb-2"></i>
            <h3>{{ totalMembers }}</h3>
            <p class="mb-0">Total Members</p>
          </div>
        </div>
      </div>
      <div class="col-lg-3 col-md-6 mb-3">
        <div class="card text-center bg-info text-white">
          <div class="card-body">
            <i class="bi bi-calendar-event display-4 mb-2"></i>
            <h3>{{ totalEvents }}</h3>
            <p class="mb-0">Upcoming Events</p>
          </div>
        </div>
      </div>
      <div class="col-lg-3 col-md-6 mb-3">
        <div class="card text-center bg-warning">
          <div class="card-body">
            <i class="bi bi-graph-up display-4 mb-2"></i>
            <h3>{{ activeChapters }}</h3>
            <p class="mb-0">Active Chapters</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activity -->
    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-header">
            <h5 class="card-title mb-0">
              <i class="bi bi-activity me-2"></i>Recent Activity
            </h5>
          </div>
          <div class="card-body">
            <div v-if="loading" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
              <p class="mt-2">Loading dashboard data...</p>
            </div>
            <div v-else-if="recentActivity.length === 0" class="text-center py-4 text-muted">
              <i class="bi bi-inbox display-1 opacity-25"></i>
              <p class="mt-3">No recent activity to display</p>
              <router-link to="/chapters/create" class="btn btn-primary">
                <i class="bi bi-plus-circle me-2"></i>Create Your First Chapter
              </router-link>
            </div>
            <div v-else>
              <div v-for="activity in recentActivity" :key="activity.id" class="border-bottom pb-3 mb-3">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <h6 class="mb-1">{{ activity.title }}</h6>
                    <p class="text-muted mb-1">{{ activity.description }}</p>
                    <small class="text-muted">{{ formatDate(activity.date) }}</small>
                  </div>
                  <span :class="getActivityBadgeClass(activity.type)">
                    {{ activity.type }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  data() {
    return {
      loading: true,
      totalChapters: 0,
      totalMembers: 0,
      totalEvents: 0,
      activeChapters: 0,
      recentActivity: []
    }
  },
  async mounted() {
    await this.loadDashboardData()
  },
  methods: {
    async loadDashboardData() {
      this.loading = true
      try {
        // Load chapters data
        const response = await fetch('/api/chapters')
        if (response.ok) {
          const chapters = await response.json()
          this.totalChapters = chapters.length
          this.activeChapters = chapters.filter(chapter => chapter.active).length
          
          // Mock some additional data for demo
          this.totalMembers = Math.floor(Math.random() * 500) + 100
          this.totalEvents = Math.floor(Math.random() * 50) + 10
          
          // Mock recent activity
          this.recentActivity = [
            {
              id: 1,
              title: 'New Chapter Created',
              description: 'A new chapter was created at University of Example',
              date: new Date().toISOString(),
              type: 'Chapter'
            }
          ]
        }
      } catch (error) {
        console.error('Error loading dashboard data:', error)
      } finally {
        this.loading = false
      }
    },
    
    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    
    getActivityBadgeClass(type) {
      const classes = {
        'Chapter': 'badge bg-primary',
        'Member': 'badge bg-success',
        'Event': 'badge bg-info'
      }
      return classes[type] || 'badge bg-secondary'
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

.display-1 {
  font-size: 6rem;
}

.opacity-25 {
  opacity: 0.25;
}
</style>
