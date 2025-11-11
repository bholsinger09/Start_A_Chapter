<template>
  <div class="dashboard">
    <div class="container-fluid">
      <!-- Welcome Section -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="card bg-primary text-white">
            <div class="card-body">
              <h2 class="card-title">
                <i class="bi bi-speedometer2 me-2"></i>
                Welcome to Campus Chapter Organizer
              </h2>
              <p class="card-text" v-if="isAuthenticated">
                Hello {{ currentUser?.firstName || 'User' }}! Manage your campus chapter activities and connect with members.
              </p>
              <p class="card-text" v-else>
                Discover campus chapters, connect with student organizations, and manage your academic community.
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="row mb-4">
        <div class="col-md-3 mb-3">
          <div class="card text-center">
            <div class="card-body">
              <i class="bi bi-building display-4 text-primary mb-2"></i>
              <h5 class="card-title">{{ stats.chapters }}</h5>
              <p class="card-text text-muted">Active Chapters</p>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="card text-center">
            <div class="card-body">
              <i class="bi bi-people display-4 text-success mb-2"></i>
              <h5 class="card-title">{{ stats.members }}</h5>
              <p class="card-text text-muted">Total Members</p>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="card text-center">
            <div class="card-body">
              <i class="bi bi-calendar-event display-4 text-warning mb-2"></i>
              <h5 class="card-title">{{ stats.events }}</h5>
              <p class="card-text text-muted">Upcoming Events</p>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="card text-center">
            <div class="card-body">
              <i class="bi bi-mortarboard display-4 text-info mb-2"></i>
              <h5 class="card-title">{{ stats.universities }}</h5>
              <p class="card-text text-muted">Universities</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions (for authenticated users) -->
      <div class="row mb-4" v-if="isAuthenticated">
        <div class="col-12">
          <h4><i class="bi bi-lightning me-2"></i>Quick Actions</h4>
          <div class="row">
            <div class="col-md-4 mb-3">
              <router-link to="/chapters" class="text-decoration-none">
                <div class="card h-100 action-card">
                  <div class="card-body text-center">
                    <i class="bi bi-building-add display-5 text-primary mb-2"></i>
                    <h6 class="card-title">Explore Chapters</h6>
                    <p class="card-text text-muted">Browse and join campus chapters</p>
                  </div>
                </div>
              </router-link>
            </div>
            <div class="col-md-4 mb-3">
              <router-link to="/members" class="text-decoration-none">
                <div class="card h-100 action-card">
                  <div class="card-body text-center">
                    <i class="bi bi-person-plus display-5 text-success mb-2"></i>
                    <h6 class="card-title">Connect with Members</h6>
                    <p class="card-text text-muted">Find and network with other members</p>
                  </div>
                </div>
              </router-link>
            </div>
            <div class="col-md-4 mb-3">
              <router-link to="/blog" class="text-decoration-none">
                <div class="card h-100 action-card">
                  <div class="card-body text-center">
                    <i class="bi bi-journal-text display-5 text-info mb-2"></i>
                    <h6 class="card-title">Write Blog Post</h6>
                    <p class="card-text text-muted">Share your experiences and insights</p>
                  </div>
                </div>
              </router-link>
            </div>
          </div>
        </div>
      </div>

      <!-- Registration Call-to-Action (for non-authenticated users) -->
      <div class="row mb-4" v-if="!isAuthenticated">
        <div class="col-md-8 mx-auto">
          <div class="card">
            <div class="card-body text-center">
              <h4 class="card-title">
                <i class="bi bi-person-plus me-2"></i>
                Join the Community
              </h4>
              <p class="card-text">
                Connect with student organizations across universities. Create your profile, join chapters, and engage with the campus community.
              </p>
              <div class="d-flex gap-3 justify-content-center">
                <router-link to="/register" class="btn btn-primary">
                  <i class="bi bi-person-plus me-2"></i>
                  Register Now
                </router-link>
                <router-link to="/login" class="btn btn-outline-primary">
                  <i class="bi bi-box-arrow-in-right me-2"></i>
                  Sign In
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Activity (placeholder) -->
      <div class="row" v-if="isAuthenticated">
        <div class="col-12">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-clock-history me-2"></i>
                Recent Activity
              </h5>
            </div>
            <div class="card-body">
              <div class="text-center text-muted py-4">
                <i class="bi bi-activity display-4 mb-3"></i>
                <p>No recent activity to display.</p>
                <small>Start by exploring chapters or connecting with members!</small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'

export default {
  name: 'Dashboard',
  setup() {
    const stats = ref({
      chapters: 0,
      members: 0,
      events: 0,
      universities: 0
    })

    const loading = ref(true)

    // Check authentication status
    const isAuthenticated = computed(() => {
      try {
        const user = localStorage.getItem('user')
        return user !== null
      } catch {
        return false
      }
    })

    const currentUser = computed(() => {
      try {
        const user = localStorage.getItem('user')
        return user ? JSON.parse(user) : null
      } catch {
        return null
      }
    })

    // Load dashboard statistics
    const loadStats = async () => {
      try {
        loading.value = true

        // Load chapters
        const chaptersResponse = await api.get('/api/chapters')
        stats.value.chapters = chaptersResponse.data.length

        // Calculate unique universities
        const universities = new Set()
        chaptersResponse.data.forEach(chapter => {
          if (chapter.universityName) {
            universities.add(chapter.universityName)
          }
        })
        stats.value.universities = universities.size

        // Load members
        try {
          const membersResponse = await api.get('/api/members')
          stats.value.members = membersResponse.data.length
        } catch (err) {
          console.log('Members endpoint not accessible')
          stats.value.members = 0
        }

        // Load events (placeholder)
        stats.value.events = 0

      } catch (error) {
        console.error('Error loading dashboard stats:', error)
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      loadStats()
    })

    return {
      stats,
      loading,
      isAuthenticated,
      currentUser
    }
  }
}
</script>

<style scoped>
.action-card {
  transition: transform 0.2s ease-in-out;
  cursor: pointer;
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.display-4 {
  font-size: 2.5rem;
}

.display-5 {
  font-size: 2rem;
}

.card {
  border-radius: 0.5rem;
  border: none;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.card-body {
  padding: 1.5rem;
}

.bg-primary {
  background: linear-gradient(135deg, #0d6efd 0%, #0b5ed7 100%) !important;
}
</style>
