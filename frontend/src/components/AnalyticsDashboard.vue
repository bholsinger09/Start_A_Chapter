<template>
  <div class="analytics-dashboard">
    <!-- Error Alert -->
    <div v-if="error" class="alert alert-warning alert-dismissible fade show mb-4" role="alert">
      <i class="fas fa-exclamation-triangle me-2"></i>
      {{ error }}
      <button type="button" class="btn-close" @click="error = null"></button>
    </div>
    
    <div class="row g-4">
      <!-- Membership Growth Chart -->
      <div class="col-lg-8">
        <div class="card h-100">
          <div class="card-header">
            <h5 class="mb-0">
              <i class="bi bi-graph-up text-success me-2"></i>
              Membership Growth
            </h5>
          </div>
          <div class="card-body">
            <div v-if="loading" class="text-center py-5">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
            </div>
            <canvas v-else ref="membershipChart" style="max-height: 300px;"></canvas>
          </div>
        </div>
      </div>

      <!-- Event Attendance Pie Chart -->
      <div class="col-lg-4">
        <div class="card h-100">
          <div class="card-header">
            <h5 class="mb-0">
              <i class="bi bi-pie-chart text-info me-2"></i>
              Event Attendance
            </h5>
          </div>
          <div class="card-body">
            <div v-if="loading" class="text-center py-4">
              <div class="spinner-border text-info" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
            </div>
            <canvas v-else ref="attendanceChart" style="max-height: 250px;"></canvas>
          </div>
        </div>
      </div>

      <!-- Chapter Performance Radar -->
      <div class="col-lg-6">
        <div class="card h-100">
          <div class="card-header">
            <h5 class="mb-0">
              <i class="bi bi-radar text-warning me-2"></i>
              Chapter Performance
            </h5>
          </div>
          <div class="card-body">
            <div v-if="loading" class="text-center py-5">
              <div class="spinner-border text-warning" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
            </div>
            <canvas v-else ref="performanceChart" style="max-height: 300px;"></canvas>
          </div>
        </div>
      </div>

      <!-- Real-time Metrics -->
      <div class="col-lg-6">
        <div class="card h-100">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">
              <i class="bi bi-activity text-danger me-2"></i>
              Real-time Metrics
            </h5>
            <div class="d-flex align-items-center gap-2">
              <button 
                class="btn btn-outline-primary btn-sm" 
                @click="fetchAnalyticsData"
                :disabled="loading"
              >
                <i class="fas fa-sync-alt" :class="{ 'fa-spin': loading }"></i>
              </button>
              <div class="badge bg-success pulse-dot">Live</div>
            </div>
          </div>
          <div class="card-body">
            <div class="row g-3">
              <div class="col-6">
                <div class="metric-item">
                  <div class="metric-value">{{ realTimeData.activeUsers }}</div>
                  <div class="metric-label">Active Users</div>
                </div>
              </div>
              <div class="col-6">
                <div class="metric-item">
                  <div class="metric-value">{{ realTimeData.ongoingEvents }}</div>
                  <div class="metric-label">Ongoing Events</div>
                </div>
              </div>
              <div class="col-6">
                <div class="metric-item">
                  <div class="metric-value">{{ realTimeData.newSignups }}</div>
                  <div class="metric-label">Today's Signups</div>
                </div>
              </div>
              <div class="col-6">
                <div class="metric-item">
                  <div class="metric-value">{{ realTimeData.systemHealth }}%</div>
                  <div class="metric-label">System Health</div>
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
import { ref, onMounted, onUnmounted } from 'vue'
import Chart from 'chart.js/auto'
import { useMonitoring } from '../composables/useMonitoring'
import api from '../services/api'

// Chart.js auto imports all necessary components including controllers

export default {
  name: 'AnalyticsDashboard',
  setup() {
    const membershipChart = ref(null)
    const attendanceChart = ref(null)
    const performanceChart = ref(null)
    
    const { 
      healthData, 
      metricsData, 
      operationalData, 
      loadAllData,
      refreshData 
    } = useMonitoring({ autoRefresh: true })
    
    const analyticsData = ref({
      statistics: {},
      growth: {},
      activity: [],
      upcomingEvents: []
    })
    
    const realTimeData = ref({
      activeUsers: 0,
      ongoingEvents: 0,
      newSignups: 0,
      systemHealth: 100
    })
    
    const loading = ref(false)
    const error = ref(null)
    
    let membershipChartInstance = null
    let attendanceChartInstance = null
    let performanceChartInstance = null
    let updateInterval = null

    // Fetch analytics data from API
    const fetchAnalyticsData = async () => {
      loading.value = true
      error.value = null
      
      try {
        const [statisticsResponse, growthResponse, activityResponse, eventsResponse] = await Promise.all([
          api.get('/dashboard/statistics'),
          api.get('/dashboard/growth'),
          api.get('/dashboard/activity', { params: { limit: 10 } }),
          api.get('/dashboard/upcoming-events', { params: { limit: 5 } })
        ])
        
        analyticsData.value = {
          statistics: statisticsResponse.data,
          growth: growthResponse.data,
          activity: activityResponse.data,
          upcomingEvents: eventsResponse.data
        }
        
        // Update real-time data from statistics
        realTimeData.value = {
          activeUsers: analyticsData.value.statistics.totalActiveMembers || 0,
          ongoingEvents: analyticsData.value.statistics.totalEvents || 0,
          newSignups: analyticsData.value.statistics.recentMembers || 0,
          systemHealth: 100
        }
        
        // Update charts with new data
        updateChartsWithRealData()
        
      } catch (err) {
        console.error('Failed to fetch analytics data:', err)
        error.value = 'Failed to load analytics data'
      } finally {
        loading.value = false
      }
    }

    const initCharts = () => {
      // Membership Growth Line Chart
      const membershipCtx = membershipChart.value.getContext('2d')
      membershipChartInstance = new Chart(membershipCtx, {
        type: 'line',
        data: {
          labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
          datasets: [{
            label: 'New Members',
            data: [65, 59, 80, 81, 56, 89],
            borderColor: '#28a745',
            backgroundColor: 'rgba(40, 167, 69, 0.1)',
            tension: 0.4,
            fill: true
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              grid: {
                color: 'rgba(0,0,0,0.1)'
              }
            },
            x: {
              grid: {
                display: false
              }
            }
          }
        }
      })

      // Attendance Pie Chart
      const attendanceCtx = attendanceChart.value.getContext('2d')
      attendanceChartInstance = new Chart(attendanceCtx, {
        type: 'doughnut',
        data: {
          labels: ['Present', 'Absent', 'Late'],
          datasets: [{
            data: [75, 20, 5],
            backgroundColor: [
              '#28a745',
              '#dc3545',
              '#ffc107'
            ],
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom',
              labels: {
                padding: 20,
                usePointStyle: true
              }
            }
          }
        }
      })

      // Performance Radar Chart
      const performanceCtx = performanceChart.value.getContext('2d')
      performanceChartInstance = new Chart(performanceCtx, {
        type: 'radar',
        data: {
          labels: ['Events', 'Attendance', 'Engagement', 'Growth', 'Retention'],
          datasets: [{
            label: 'Chapter Performance',
            data: [85, 75, 90, 70, 88],
            backgroundColor: 'rgba(255, 193, 7, 0.2)',
            borderColor: '#ffc107',
            borderWidth: 2,
            pointBackgroundColor: '#ffc107',
            pointBorderColor: '#fff',
            pointBorderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            r: {
              beginAtZero: true,
              max: 100,
              ticks: {
                stepSize: 20
              }
            }
          },
          plugins: {
            legend: {
              display: false
            }
          }
        }
      })
    }

    const updateChartsWithRealData = () => {
      // Update membership chart with growth data
      if (membershipChartInstance && analyticsData.value.growth.memberGrowth) {
        const growthData = analyticsData.value.growth.memberGrowth
        const labels = growthData.map(item => item.month)
        const data = growthData.map(item => item.count)
        
        membershipChartInstance.data.labels = labels
        membershipChartInstance.data.datasets[0].data = data
        membershipChartInstance.update()
      }
      
      // Update attendance chart with event statistics
      if (attendanceChartInstance && analyticsData.value.statistics.totalEvents) {
        const totalEvents = analyticsData.value.statistics.totalEvents
        const recentEvents = analyticsData.value.statistics.recentEvents || 0
        const upcomingCount = analyticsData.value.upcomingEvents.length
        
        attendanceChartInstance.data.datasets[0].data = [
          recentEvents,
          totalEvents - recentEvents - upcomingCount,
          upcomingCount
        ]
        attendanceChartInstance.update()
      }
      
      // Update performance radar with chapter statistics
      if (performanceChartInstance && analyticsData.value.statistics) {
        const stats = analyticsData.value.statistics
        const performanceData = [
          Math.min((stats.totalEvents || 0) / 10 * 100, 100), // Events score
          Math.min((stats.totalActiveMembers || 0) / 50 * 100, 100), // Membership score  
          Math.min((stats.totalBlogs || 0) / 20 * 100, 100), // Engagement score
          Math.min((stats.recentMembers || 0) / 10 * 100, 100), // Growth score
          Math.min((stats.totalActiveMembers || 0) / (stats.totalMembers || 1) * 100, 100) // Retention score
        ]
        
        performanceChartInstance.data.datasets[0].data = performanceData
        performanceChartInstance.update()
      }
    }

    const updateRealTimeData = async () => {
      try {
        await Promise.all([refreshData(), fetchAnalyticsData()])
      } catch (error) {
        console.error('Failed to fetch real-time data:', error)
      }
    }

    onMounted(async () => {
      initCharts()
      loadAllData()
      await fetchAnalyticsData()
      
      // Update real-time data every 30 seconds
      updateInterval = setInterval(updateRealTimeData, 30000)
    })

    onUnmounted(() => {
      if (membershipChartInstance) membershipChartInstance.destroy()
      if (attendanceChartInstance) attendanceChartInstance.destroy()
      if (performanceChartInstance) performanceChartInstance.destroy()
      if (updateInterval) clearInterval(updateInterval)
    })

    return {
      membershipChart,
      attendanceChart,
      performanceChart,
      realTimeData,
      analyticsData,
      loading,
      error,
      fetchAnalyticsData
    }
  }
}
</script>

<style scoped>
.metric-item {
  text-align: center;
  padding: 1rem;
  background: rgba(0, 123, 255, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(0, 123, 255, 0.1);
}

.metric-value {
  font-size: 2rem;
  font-weight: bold;
  color: var(--bs-primary);
  line-height: 1;
}

.metric-label {
  font-size: 0.875rem;
  color: var(--bs-text-muted);
  margin-top: 0.25rem;
}

.pulse-dot {
  position: relative;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

[data-theme="dark"] .metric-item {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
}
</style>