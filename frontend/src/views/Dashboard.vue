<template>
  <div class="enhanced-dashboard">
    <div class="container-fluid">
      <!-- Header -->
      <div class="dashboard-header">
        <div class="header-content">
          <div class="header-text">
            <h1 class="dashboard-title">
              <i class="bi bi-speedometer2 text-primary me-3"></i>
              System Dashboard
            </h1>
            <p class="dashboard-subtitle">
              Real-time monitoring and analytics for Campus Chapter Organizer
            </p>
          </div>
          
          <div class="header-actions">
            <div class="last-updated">
              <small class="text-muted">
                <i class="bi bi-clock me-1"></i>
                Last updated: {{ formatLastUpdated(lastUpdated) }}
              </small>
            </div>
            <button 
              class="btn btn-outline-primary btn-sm"
              @click="refreshData"
              :disabled="isLoading"
            >
              <i class="bi bi-arrow-clockwise" :class="{ 'spin': isLoading }"></i>
              Refresh
            </button>
          </div>
        </div>
      </div>

      <!-- System Status Alert -->
      <div v-if="systemHealthStatus.status !== 'healthy'" class="system-alert">
        <div class="alert" :class="`alert-${systemHealthStatus.color}`">
          <div class="d-flex align-items-center">
            <i class="bi bi-exclamation-triangle me-2"></i>
            <strong>System Status:</strong>
            <span class="ms-2">{{ systemHealthStatus.text }}</span>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading && !lastUpdated" class="loading-state">
        <div class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading dashboard...</span>
          </div>
          <p class="text-muted mt-3">Loading system metrics...</p>
        </div>
      </div>

      <!-- Critical Metrics Row -->
      <div v-else class="metrics-grid">
        <MetricCard
          v-for="metric in criticalMetrics"
          :key="metric.title"
          v-bind="metric"
          :is-loading="isLoading"
          :show-actions="true"
          @refresh="refreshMetrics"
        />
      </div>

      <!-- Main Dashboard Grid -->
      <div class="dashboard-grid">
        <!-- System Health Panel -->
        <div class="dashboard-panel health-panel">
          <SystemHealth
            :health-data="healthData"
            :is-loading="isLoading"
            :error="error"
            @refresh="refreshHealth"
          />
        </div>

        <!-- RSVP Trends Chart -->
        <div class="dashboard-panel chart-panel">
          <SimpleChart
            title="RSVP Trends"
            :data="rsvpTrendsChartData"
            type="line"
            color="#0d6efd"
            :width="400"
            :height="250"
            :is-loading="isLoading"
            :show-actions="true"
            @refresh="refreshData"
          />
        </div>

        <!-- Event Capacity Overview -->
        <div class="dashboard-panel chart-panel">
          <SimpleChart
            title="Event Capacity Usage"
            :data="eventCapacityChartData"
            type="bar"
            :colors="['#198754', '#ffc107', '#dc3545']"
            :width="400"
            :height="250"
            :is-loading="isLoading"
            :show-actions="true"
            @refresh="refreshData"
          />
        </div>

        <!-- Activity Feed -->
        <div class="dashboard-panel activity-panel">
          <div class="activity-feed">
            <div class="activity-header">
              <h6 class="mb-0">
                <i class="bi bi-activity me-2"></i>
                Recent Activity
              </h6>
              <button 
                class="btn btn-sm btn-outline-secondary"
                @click="loadAuditLogs"
                :disabled="isLoading"
              >
                <i class="bi bi-arrow-clockwise"></i>
              </button>
            </div>

            <div class="activity-list">
              <div 
                v-for="log in recentAuditLogs" 
                :key="log.id"
                class="activity-item"
              >
                <div class="activity-icon">
                  <i :class="getActivityIcon(log.action)"></i>
                </div>
                <div class="activity-content">
                  <div class="activity-text">
                    <strong>{{ log.userName || 'System' }}</strong>
                    {{ getActivityDescription(log) }}
                  </div>
                  <div class="activity-time">
                    {{ formatTimeAgo(log.timestamp) }}
                  </div>
                </div>
              </div>
              
              <div v-if="recentAuditLogs.length === 0" class="empty-activity">
                <i class="bi bi-inbox text-muted"></i>
                <p class="text-muted mb-0">No recent activity</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Performance Metrics -->
        <div class="dashboard-panel performance-panel">
          <div class="performance-overview">
            <div class="performance-header">
              <h6 class="mb-0">
                <i class="bi bi-graph-up me-2"></i>
                Performance Metrics
              </h6>
            </div>

            <div class="performance-stats">
              <div class="perf-stat">
                <div class="perf-value">{{ metricsData.performance.avgResponseTime }}ms</div>
                <div class="perf-label">Avg Response Time</div>
              </div>
              
              <div class="perf-stat">
                <div class="perf-value">{{ metricsData.performance.requestsPerMinute }}</div>
                <div class="perf-label">Requests/Min</div>
              </div>
              
              <div class="perf-stat">
                <div class="perf-value">{{ metricsData.performance.uptime }}%</div>
                <div class="perf-label">Uptime</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Quick Stats -->
        <div class="dashboard-panel stats-panel">
          <div class="quick-stats">
            <div class="stats-header">
              <h6 class="mb-0">
                <i class="bi bi-bar-chart me-2"></i>
                System Overview
              </h6>
            </div>

            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-icon">
                  <i class="bi bi-building"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ metricsData.system.totalChapters }}</div>
                  <div class="stat-label">Chapters</div>
                </div>
              </div>

              <div class="stat-item">
                <div class="stat-icon">
                  <i class="bi bi-people-fill"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ metricsData.system.totalMembers }}</div>
                  <div class="stat-label">Members</div>
                </div>
              </div>

              <div class="stat-item">
                <div class="stat-icon">
                  <i class="bi bi-calendar-check"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ metricsData.rsvps.todayRsvps }}</div>
                  <div class="stat-label">Today's RSVPs</div>
                </div>
              </div>

              <div class="stat-item">
                <div class="stat-icon">
                  <i class="bi bi-clock-history"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ metricsData.rsvps.pendingRsvps }}</div>
                  <div class="stat-label">Pending RSVPs</div>
                </div>
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
                    <small class="text-muted">{{ chapter.universityName }}</small><br>
                    <small class="text-muted">{{ chapter.city }}, {{ chapter.state }}</small>
                  </div>
                  <div class="text-end">
                    <div class="btn-group" role="group">
                      <router-link 
                        :to="`/chapters/${chapter.id}`" 
                        class="btn btn-sm btn-outline-primary"
                        title="View Details"
                      >
                        <i class="bi bi-eye"></i>
                      </router-link>
                      <a 
                        :href="getCampusLabsUrl(chapter)" 
                        target="_blank"
                        rel="noopener noreferrer"
                        class="btn btn-sm btn-outline-success"
                        :title="getButtonTooltip(chapter)"
                        :aria-label="`${getButtonText(chapter)} for ${chapter.universityName} (opens in new tab)`"
                        v-if="getCampusLabsUrl(chapter)"
                        @click="trackLinkClick(chapter)"
                      >
                        <i class="bi bi-link-45deg me-1"></i>
                        <small>{{ getButtonText(chapter) }}</small>
                      </a>
                    </div>
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
                            <div class="btn-group" role="group">
                              <router-link 
                                :to="`/chapters/${chapter.id}`" 
                                class="btn btn-sm btn-outline-primary"
                                title="View Details"
                              >
                                <i class="bi bi-eye"></i>
                              </router-link>
                              <a 
                                :href="getCampusLabsUrl(chapter)" 
                                target="_blank"
                                rel="noopener noreferrer"
                                class="btn btn-sm btn-outline-success"
                                :title="getButtonTooltip(chapter)"
                                :aria-label="`${getButtonText(chapter)} for ${chapter.universityName} (opens in new tab)`"
                                v-if="getCampusLabsUrl(chapter)"
                                @click="trackLinkClick(chapter)"
                              >
                                <i class="bi bi-link-45deg me-1"></i>
                                <small>{{ getButtonText(chapter) }}</small>
                              </a>
                            </div>
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
</template>

<script>
import { computed } from 'vue'
import MetricCard from '../components/MetricCard.vue'
import SystemHealth from '../components/SystemHealth.vue'
import SimpleChart from '../components/SimpleChart.vue'
import { useMonitoring } from '../composables/useMonitoring'

export default {
  name: 'EnhancedDashboard',
  components: {
    MetricCard,
    SystemHealth,
    SimpleChart
  },
  setup() {
    // Initialize monitoring with auto-refresh
    const {
      isLoading,
      error,
      lastUpdated,
      healthData,
      metricsData,
      operationalData,
      recentAuditLogs,
      systemHealthStatus,
      criticalMetrics,
      rsvpTrendsChartData,
      eventCapacityChartData,
      refreshData,
      refreshMetrics,
      refreshHealth,
      loadAuditLogs
    } = useMonitoring({
      autoRefresh: true,
      refreshInterval: 30000 // 30 seconds
    })

    return {
      // State
      isLoading,
      error,
      lastUpdated,
      healthData,
      metricsData,
      operationalData,
      recentAuditLogs,
      
      // Computed
      systemHealthStatus,
      criticalMetrics,
      rsvpTrendsChartData,
      eventCapacityChartData,
      
      // Methods
      refreshData,
      refreshMetrics,
      refreshHealth,
      loadAuditLogs
    }
  },
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
      // Pass the state filter to the Chapters page via query parameters
      this.$router.push({
        path: '/chapters',
        query: { state: this.selectedState }
      })
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
  },
  methods: {
    // Time formatting methods
    formatLastUpdated(date) {
      if (!date) return 'Never'
      
      const now = new Date()
      const diff = Math.floor((now - date) / 1000)
      
      if (diff < 60) return 'Just now'
      if (diff < 3600) return `${Math.floor(diff / 60)} minutes ago`
      if (diff < 86400) return `${Math.floor(diff / 3600)} hours ago`
      
      return date.toLocaleDateString()
    },

    formatTimeAgo(timestamp) {
      const now = new Date()
      const time = new Date(timestamp)
      const diffInHours = Math.floor((now - time) / (1000 * 60 * 60))
      
      if (diffInHours < 1) return 'Just now'
      if (diffInHours < 24) return `${diffInHours}h ago`
      
      const diffInDays = Math.floor(diffInHours / 24)
      if (diffInDays === 1) return 'Yesterday'
      if (diffInDays < 7) return `${diffInDays}d ago`
      
      return time.toLocaleDateString()
    },

    // Activity feed helpers
    getActivityIcon(action) {
      const iconMap = {
        CREATE_EVENT: 'bi bi-calendar-plus text-success',
        UPDATE_EVENT: 'bi bi-calendar-check text-primary',
        DELETE_EVENT: 'bi bi-calendar-x text-danger',
        CREATE_MEMBER: 'bi bi-person-plus text-success',
        UPDATE_MEMBER: 'bi bi-person-check text-primary',
        DELETE_MEMBER: 'bi bi-person-x text-danger',
        CREATE_CHAPTER: 'bi bi-building text-success',
        UPDATE_CHAPTER: 'bi bi-building text-primary',
        RSVP_CREATE: 'bi bi-check-circle text-success',
        RSVP_UPDATE: 'bi bi-arrow-repeat text-primary',
        LOGIN: 'bi bi-box-arrow-in-right text-info',
        LOGOUT: 'bi bi-box-arrow-right text-secondary'
      }
      
      return iconMap[action] || 'bi bi-circle text-muted'
    },

    getActivityDescription(log) {
      const actionMap = {
        CREATE_EVENT: 'created a new event',
        UPDATE_EVENT: 'updated an event',
        DELETE_EVENT: 'deleted an event',
        CREATE_MEMBER: 'added a new member',
        UPDATE_MEMBER: 'updated member details',
        DELETE_MEMBER: 'removed a member',
        CREATE_CHAPTER: 'created a new chapter',
        UPDATE_CHAPTER: 'updated chapter details',
        RSVP_CREATE: 'RSVPed to an event',
        RSVP_UPDATE: 'changed their RSVP',
        LOGIN: 'logged into the system',
        LOGOUT: 'logged out'
      }
      
      const baseDescription = actionMap[log.action] || 'performed an action'
      
      if (log.entityName) {
        return `${baseDescription}: "${log.entityName}"`
      }
      
      return baseDescription
    }
  }
}
</script>

<style scoped>
/* Enhanced Dashboard Styles */
.enhanced-dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
}

.container-fluid {
  padding: 0;
}

/* Dashboard Header */
.dashboard-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 2rem 0;
  margin-bottom: 2rem;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.dashboard-title {
  color: white;
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.dashboard-subtitle {
  color: rgba(255, 255, 255, 0.8);
  font-size: 1.1rem;
  margin: 0.5rem 0 0 0;
  font-weight: 300;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-direction: column;
  text-align: right;
}

.last-updated {
  color: rgba(255, 255, 255, 0.7);
}

.btn-outline-primary {
  border-color: rgba(255, 255, 255, 0.3);
  color: white;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.btn-outline-primary:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
  color: white;
}

/* System Alert */
.system-alert {
  max-width: 1400px;
  margin: 0 auto 2rem;
  padding: 0 1rem;
}

/* Metrics Grid */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  max-width: 1400px;
  margin: 0 auto 2rem;
  padding: 0 1rem;
}

/* Dashboard Grid */
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 1.5rem;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 1rem 2rem;
}

.dashboard-panel {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.dashboard-panel:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

/* Panel layouts */
.health-panel {
  grid-column: span 4;
}

.chart-panel {
  grid-column: span 4;
}

.activity-panel {
  grid-column: span 4;
  grid-row: span 2;
}

.performance-panel {
  grid-column: span 4;
}

.stats-panel {
  grid-column: span 4;
}

/* Activity Feed */
.activity-feed {
  padding: 1.25rem;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #f8f9fa;
}

.activity-header h6 {
  color: #495057;
  font-weight: 600;
  margin: 0;
}

.activity-list {
  flex: 1;
  overflow-y: auto;
  max-height: 400px;
}

.activity-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem 0;
  border-bottom: 1px solid #f8f9fa;
  transition: background-color 0.2s ease;
}

.activity-item:hover {
  background: #f8f9fa;
  margin: 0 -0.75rem;
  padding-left: 0.75rem;
  padding-right: 0.75rem;
  border-radius: 8px;
}

.activity-icon {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  background: #f8f9fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-text {
  font-size: 0.875rem;
  color: #495057;
  line-height: 1.4;
  margin-bottom: 0.25rem;
}

.activity-time {
  font-size: 0.75rem;
  color: #6c757d;
}

.empty-activity {
  text-align: center;
  padding: 3rem 1rem;
  color: #6c757d;
}

.empty-activity i {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  display: block;
}

/* Performance Panel */
.performance-overview {
  padding: 1.25rem;
}

.performance-header {
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #f8f9fa;
}

.performance-header h6 {
  color: #495057;
  font-weight: 600;
  margin: 0;
}

.performance-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
}

.perf-stat {
  text-align: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.perf-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #0d6efd;
  margin-bottom: 0.25rem;
}

.perf-label {
  font-size: 0.75rem;
  color: #6c757d;
  font-weight: 500;
}

/* Quick Stats Panel */
.quick-stats {
  padding: 1.25rem;
}

.stats-header {
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #f8f9fa;
}

.stats-header h6 {
  color: #495057;
  font-weight: 600;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.stat-item:hover {
  background: #e9ecef;
}

.stat-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #0d6efd, #0b5ed7);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.125rem;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.25rem;
  font-weight: 700;
  color: #212529;
  line-height: 1.2;
}

.stat-label {
  font-size: 0.75rem;
  color: #6c757d;
  font-weight: 500;
}

/* Loading state */
.loading-state {
  grid-column: 1 / -1;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* Animations */
.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* Responsive Design */
@media (max-width: 1200px) {
  .dashboard-grid {
    grid-template-columns: repeat(8, 1fr);
  }
  
  .health-panel,
  .chart-panel {
    grid-column: span 4;
  }
  
  .activity-panel {
    grid-column: span 8;
    grid-row: span 1;
  }
  
  .performance-panel,
  .stats-panel {
    grid-column: span 4;
  }
}

@media (max-width: 768px) {
  .dashboard-title {
    font-size: 2rem;
  }
  
  .header-content {
    flex-direction: column;
    text-align: center;
  }
  
  .header-actions {
    flex-direction: row;
    justify-content: center;
  }
  
  .dashboard-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .dashboard-panel {
    grid-column: span 1 !important;
    grid-row: span 1 !important;
  }
  
  .metrics-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .performance-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .dashboard-header {
    padding: 1rem 0;
  }
  
  .dashboard-title {
    font-size: 1.75rem;
  }
  
  .performance-stats {
    grid-template-columns: 1fr;
  }
  
  .header-actions {
    width: 100%;
  }
  
  .last-updated {
    text-align: center;
  }
}

/* Enhanced scrollbars */
.activity-list::-webkit-scrollbar {
  width: 4px;
}

.activity-list::-webkit-scrollbar-track {
  background: #f8f9fa;
  border-radius: 2px;
}

.activity-list::-webkit-scrollbar-thumb {
  background: #dee2e6;
  border-radius: 2px;
}

.activity-list::-webkit-scrollbar-thumb:hover {
  background: #adb5bd;
}
</style>