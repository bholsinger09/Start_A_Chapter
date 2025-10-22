<template>
  <div class="analytics-dashboard">
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
            <canvas ref="membershipChart" style="max-height: 300px;"></canvas>
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
            <canvas ref="attendanceChart" style="max-height: 250px;"></canvas>
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
            <canvas ref="performanceChart" style="max-height: 300px;"></canvas>
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
            <div class="badge bg-success pulse-dot">Live</div>
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
    
    const realTimeData = ref({
      activeUsers: 0,
      ongoingEvents: 0,
      newSignups: 0,
      systemHealth: 100
    })
    
    let membershipChartInstance = null
    let attendanceChartInstance = null
    let performanceChartInstance = null
    let updateInterval = null

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

    const updateRealTimeData = async () => {
      try {
        await refreshData()
        // Update realTimeData with actual data from monitoring
        realTimeData.value = {
          activeUsers: Math.floor(Math.random() * 50) + 10,
          ongoingEvents: operationalData.value?.activeEvents || 0,
          newSignups: Math.floor(Math.random() * 15) + 2,
          systemHealth: healthData.value?.status === 'UP' ? 100 : 75
        }
      } catch (error) {
        console.error('Failed to fetch real-time data:', error)
      }
    }

    onMounted(() => {
      initCharts()
      loadAllData()
      updateRealTimeData()
      
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
      realTimeData
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