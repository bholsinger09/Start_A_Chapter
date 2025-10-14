<template>
  <div class="dashboard">
    <div class="container">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col">
          <h1 class="display-4 text-center mb-3">
            <i class="bi bi-speedometer2 text-primary me-3"></i>
            Dashboard
          </h1>
          <p class="lead text-center text-muted">
            Campus Chapter Management System Overview
          </p>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <!-- Statistics Cards -->
      <div v-else class="row mb-4">
        <div class="col-md-4 mb-3">
          <div class="card bg-primary text-white h-100">
            <div class="card-body d-flex align-items-center">
              <div class="flex-grow-1">
                <h5 class="card-title">
                  <i class="bi bi-building me-2"></i>
                  Total Chapters
                </h5>
                <h2 class="mb-0">{{ stats.totalChapters }}</h2>
              </div>
              <div class="ms-3">
                <i class="bi bi-building display-4 opacity-75"></i>
              </div>
            </div>
          </div>
        </div>
        
        <div class="col-md-4 mb-3">
          <div class="card bg-success text-white h-100">
            <div class="card-body d-flex align-items-center">
              <div class="flex-grow-1">
                <h5 class="card-title">
                  <i class="bi bi-people-fill me-2"></i>
                  Total Members
                </h5>
                <h2 class="mb-0">{{ stats.totalMembers }}</h2>
              </div>
              <div class="ms-3">
                <i class="bi bi-people-fill display-4 opacity-75"></i>
              </div>
            </div>
          </div>
        </div>
        
        <div class="col-md-4 mb-3">
          <div class="card bg-info text-white h-100">
            <div class="card-body d-flex align-items-center">
              <div class="flex-grow-1">
                <h5 class="card-title">
                  <i class="bi bi-calendar-event me-2"></i>
                  Total Events
                </h5>
                <h2 class="mb-0">{{ stats.totalEvents }}</h2>
              </div>
              <div class="ms-3">
                <i class="bi bi-calendar-event display-4 opacity-75"></i>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Activity -->
      <div class="row">
        <div class="col-lg-6 mb-4">
          <div class="card h-100">
            <div class="card-header bg-light">
              <h5 class="card-title mb-0">
                <i class="bi bi-building text-primary me-2"></i>
                Recent Chapters
              </h5>
            </div>
            <div class="card-body">
              <div v-if="recentChapters.length === 0" class="text-muted text-center py-4">
                No chapters found
              </div>
              <div v-else>
                <div 
                  v-for="chapter in recentChapters.slice(0, 5)" 
                  :key="chapter.id"
                  class="d-flex justify-content-between align-items-center py-2 border-bottom"
                >
                  <div>
                    <strong>{{ chapter.name }}</strong><br>
                    <small class="text-muted">{{ chapter.universityName }}</small>
                  </div>
                  <div class="text-end">
                    <small class="text-muted">{{ chapter.city }}, {{ chapter.state }}</small>
                  </div>
                </div>
              </div>
              <div class="text-center mt-3">
                <router-link to="/chapters" class="btn btn-outline-primary btn-sm">
                  View All Chapters
                </router-link>
              </div>
            </div>
          </div>
        </div>

        <div class="col-lg-6 mb-4">
          <div class="card h-100">
            <div class="card-header bg-light">
              <h5 class="card-title mb-0">
                <i class="bi bi-calendar-event text-info me-2"></i>
                Upcoming Events
              </h5>
            </div>
            <div class="card-body">
              <div v-if="upcomingEvents.length === 0" class="text-muted text-center py-4">
                No upcoming events
              </div>
              <div v-else>
                <div 
                  v-for="event in upcomingEvents.slice(0, 5)" 
                  :key="event.id"
                  class="d-flex justify-content-between align-items-center py-2 border-bottom"
                >
                  <div>
                    <strong>{{ event.title }}</strong><br>
                    <small class="text-muted">{{ formatDate(event.eventDateTime) }}</small>
                  </div>
                  <div class="text-end">
                    <span class="badge bg-secondary">{{ event.type }}</span>
                  </div>
                </div>
              </div>
              <div class="text-center mt-3">
                <router-link to="/events" class="btn btn-outline-info btn-sm">
                  View All Events
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- State-based Search -->
      <div class="row mb-4">
        <div class="col">
          <div class="card">
            <div class="card-header bg-light">
              <h5 class="card-title mb-0">
                <i class="bi bi-geo-alt text-primary me-2"></i>
                Search Chapters by State
              </h5>
            </div>
            <div class="card-body">
              <div class="row align-items-end">
                <div class="col-md-4 mb-2">
                  <label class="form-label">Select State</label>
                  <select 
                    class="form-select" 
                    v-model="selectedState"
                    @change="searchByState"
                  >
                    <option value="">Choose a state...</option>
                    <option v-for="state in availableStates" :key="state" :value="state">
                      {{ state }}
                    </option>
                  </select>
                </div>
                <div class="col-md-4 mb-2">
                  <button 
                    class="btn btn-primary w-100"
                    @click="searchByState"
                    :disabled="!selectedState"
                  >
                    <i class="bi bi-search me-2"></i>
                    Search Chapters
                  </button>
                </div>
                <div class="col-md-4 mb-2">
                  <button 
                    class="btn btn-outline-secondary w-100"
                    @click="clearStateSearch"
                    v-if="selectedState"
                  >
                    <i class="bi bi-x-circle me-2"></i>
                    Clear Search
                  </button>
                </div>
              </div>
              
              <!-- Search Results -->
              <div v-if="stateSearchResults.length > 0" class="mt-3">
                <h6 class="text-primary">
                  <i class="bi bi-pin-map me-1"></i>
                  Chapters in {{ selectedState }} ({{ stateSearchResults.length }})
                </h6>
                <div class="row">
                  <div 
                    v-for="chapter in stateSearchResults.slice(0, 6)" 
                    :key="chapter.id"
                    class="col-md-6 mb-2"
                  >
                    <div class="card border-0 bg-light">
                      <div class="card-body py-2">
                        <div class="d-flex justify-content-between align-items-center">
                          <div>
                            <strong>{{ chapter.name }}</strong><br>
                            <small class="text-muted">{{ chapter.universityName }}</small>
                          </div>
                          <div class="text-end">
                            <router-link 
                              :to="`/chapters/${chapter.id}`" 
                              class="btn btn-sm btn-outline-primary"
                            >
                              <i class="bi bi-eye"></i>
                            </router-link>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-if="stateSearchResults.length > 6" class="text-center mt-2">
                  <router-link 
                    to="/chapters" 
                    class="btn btn-sm btn-outline-primary"
                    @click="navigateToChaptersWithState"
                  >
                    View All {{ stateSearchResults.length }} Chapters in {{ selectedState }}
                  </router-link>
                </div>
              </div>
              
              <!-- No Results -->
              <div v-else-if="selectedState && searchPerformed" class="mt-3 text-center text-muted">
                <i class="bi bi-search display-6 mb-2"></i>
                <p>No chapters found in {{ selectedState }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="row">
        <div class="col">
          <div class="card">
            <div class="card-header bg-light">
              <h5 class="card-title mb-0">
                <i class="bi bi-lightning-charge text-warning me-2"></i>
                Quick Actions
              </h5>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-3 mb-2">
                  <router-link to="/chapters" class="btn btn-outline-primary w-100">
                    <i class="bi bi-plus-circle me-2"></i>
                    Add Chapter
                  </router-link>
                </div>
                <div class="col-md-3 mb-2">
                  <router-link to="/members" class="btn btn-outline-success w-100">
                    <i class="bi bi-person-plus me-2"></i>
                    Add Member
                  </router-link>
                </div>
                <div class="col-md-3 mb-2">
                  <router-link to="/events" class="btn btn-outline-info w-100">
                    <i class="bi bi-calendar-plus me-2"></i>
                    Create Event
                  </router-link>
                </div>
                <div class="col-md-3 mb-2">
                  <a href="http://localhost:8080/h2-console" target="_blank" class="btn btn-outline-secondary w-100">
                    <i class="bi bi-database me-2"></i>
                    Database
                  </a>
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
import { chapterService } from '../services/chapterService'
import { memberService } from '../services/memberService'
import { eventService } from '../services/eventService'

export default {
  name: 'Dashboard',
  data() {
    return {
      loading: true,
      stats: {
        totalChapters: 0,
        totalMembers: 0,
        totalEvents: 0
      },
      recentChapters: [],
      upcomingEvents: [],
      availableStates: [],
      selectedState: '',
      stateSearchResults: [],
      searchPerformed: false
    }
  },
  async mounted() {
    await this.loadDashboardData()
  },
  methods: {
    async loadDashboardData() {
      try {
        this.loading = true
        
        // Load all data in parallel
        const [chapters, members, events] = await Promise.all([
          chapterService.getAllChapters(),
          memberService.getAllMembers(),
          eventService.getAllEvents()
        ])

        // Update statistics
        this.stats.totalChapters = chapters.length
        this.stats.totalMembers = members.length
        this.stats.totalEvents = events.length

        // Store recent chapters and upcoming events
        this.recentChapters = chapters
        this.upcomingEvents = events.filter(event => 
          new Date(event.eventDateTime) >= new Date()
        ).sort((a, b) => new Date(a.eventDateTime) - new Date(b.eventDateTime))

        // Extract available states for search
        this.availableStates = [...new Set(chapters.map(c => c.state).filter(Boolean))].sort()

      } catch (error) {
        console.error('Error loading dashboard data:', error)
      } finally {
        this.loading = false
      }
    },

    async searchByState() {
      if (!this.selectedState) return
      
      try {
        this.stateSearchResults = await chapterService.getChaptersByState(this.selectedState)
        this.searchPerformed = true
      } catch (error) {
        console.error('Error searching by state:', error)
        // Fallback to local filtering
        this.stateSearchResults = this.recentChapters.filter(
          chapter => chapter.state === this.selectedState
        )
        this.searchPerformed = true
      }
    },

    clearStateSearch() {
      this.selectedState = ''
      this.stateSearchResults = []
      this.searchPerformed = false
    },

    navigateToChaptersWithState() {
      // This would pass the state filter to the Chapters page
      // For now, just navigate to chapters page
      this.$router.push('/chapters')
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
    }
  }
}
</script>