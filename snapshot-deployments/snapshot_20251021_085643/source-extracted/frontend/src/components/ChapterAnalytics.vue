<template>
  <div class="chapter-analytics">
    <div class="row mb-4">
      <!-- Summary Cards -->
      <div class="col-lg-3 col-md-6 mb-3">
        <div class="card h-100 border-0 shadow-sm">
          <div class="card-body text-center">
            <div class="display-6 text-primary mb-2">
              <i class="bi bi-building"></i>
            </div>
            <h3 class="h4 mb-1">{{ totalChapters }}</h3>
            <p class="text-muted mb-0">Total Chapters</p>
          </div>
        </div>
      </div>
      <div class="col-lg-3 col-md-6 mb-3">
        <div class="card h-100 border-0 shadow-sm">
          <div class="card-body text-center">
            <div class="display-6 text-success mb-2">
              <i class="bi bi-check-circle"></i>
            </div>
            <h3 class="h4 mb-1">{{ activeChapters }}</h3>
            <p class="text-muted mb-0">Active Chapters</p>
          </div>
        </div>
      </div>
      <div class="col-lg-3 col-md-6 mb-3">
        <div class="card h-100 border-0 shadow-sm">
          <div class="card-body text-center">
            <div class="display-6 text-info mb-2">
              <i class="bi bi-geo-alt"></i>
            </div>
            <h3 class="h4 mb-1">{{ uniqueStates }}</h3>
            <p class="text-muted mb-0">States Covered</p>
          </div>
        </div>
      </div>
      <div class="col-lg-3 col-md-6 mb-3">
        <div class="card h-100 border-0 shadow-sm">
          <div class="card-body text-center">
            <div class="display-6 text-warning mb-2">
              <i class="bi bi-star"></i>
            </div>
            <h3 class="h4 mb-1">{{ averageHealthScore }}</h3>
            <p class="text-muted mb-0">Avg Health Score</p>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <!-- Chapter Distribution by State -->
      <div class="col-lg-6 mb-4">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent">
            <h5 class="card-title mb-0">
              <i class="bi bi-bar-chart me-2"></i>
              Chapter Distribution by State
            </h5>
          </div>
          <div class="card-body">
            <canvas ref="stateDistributionChart" style="max-height: 300px;"></canvas>
          </div>
        </div>
      </div>

      <!-- Health Score Distribution -->
      <div class="col-lg-6 mb-4">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent">
            <h5 class="card-title mb-0">
              <i class="bi bi-pie-chart me-2"></i>
              Health Score Distribution
            </h5>
          </div>
          <div class="card-body">
            <canvas ref="healthScoreChart" style="max-height: 300px;"></canvas>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <!-- Top Performing States -->
      <div class="col-lg-8 mb-4">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent">
            <h5 class="card-title mb-0">
              <i class="bi bi-trophy me-2"></i>
              Top Performing States
            </h5>
          </div>
          <div class="card-body">
            <canvas ref="topStatesChart" style="max-height: 350px;"></canvas>
          </div>
        </div>
      </div>

      <!-- Chapter Status Overview -->
      <div class="col-lg-4 mb-4">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent">
            <h5 class="card-title mb-0">
              <i class="bi bi-activity me-2"></i>
              Chapter Status
            </h5>
          </div>
          <div class="card-body">
            <canvas ref="statusChart" style="max-height: 350px;"></canvas>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activity Timeline -->
    <div class="row">
      <div class="col-12 mb-4">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent">
            <h5 class="card-title mb-0">
              <i class="bi bi-clock-history me-2"></i>
              Chapter Activity Timeline
            </h5>
          </div>
          <div class="card-body">
            <canvas ref="timelineChart" style="max-height: 250px;"></canvas>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { Chart, registerables } from 'chart.js'
import 'chartjs-adapter-date-fns'

// Register Chart.js components
Chart.register(...registerables)

export default {
  name: 'ChapterAnalytics',
  props: {
    chapters: {
      type: Array,
      default: () => []
    },
    isDarkMode: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    // Template refs
    const stateDistributionChart = ref(null)
    const healthScoreChart = ref(null)
    const topStatesChart = ref(null)
    const statusChart = ref(null)
    const timelineChart = ref(null)

    // Chart instances
    let chartInstances = {}

    // Computed properties for summary stats
    const totalChapters = computed(() => props.chapters.length)
    const activeChapters = computed(() => props.chapters.filter(c => c.active).length)
    const uniqueStates = computed(() => [...new Set(props.chapters.map(c => c.state))].length)
    const averageHealthScore = computed(() => {
      if (props.chapters.length === 0) return 0
      const total = props.chapters.reduce((sum, c) => sum + (c.healthScore || 0), 0)
      return Math.round(total / props.chapters.length)
    })

    // Chart colors based on theme
    const getChartColors = () => {
      if (props.isDarkMode) {
        return {
          primary: '#3b82f6',
          success: '#10b981',
          warning: '#f59e0b',
          danger: '#ef4444',
          info: '#06b6d4',
          text: '#f8fafc',
          grid: '#374151',
          background: 'rgba(59, 130, 246, 0.1)'
        }
      }
      return {
        primary: '#0d6efd',
        success: '#198754',
        warning: '#ffc107',
        danger: '#dc3545',
        info: '#0dcaf0',
        text: '#495057',
        grid: '#dee2e6',
        background: 'rgba(13, 110, 253, 0.1)'
      }
    }

    // State distribution data
    const getStateDistributionData = () => {
      const stateCounts = {}
      props.chapters.forEach(chapter => {
        stateCounts[chapter.state] = (stateCounts[chapter.state] || 0) + 1
      })
      
      // Sort and take top 10 states
      const sortedStates = Object.entries(stateCounts)
        .sort(([,a], [,b]) => b - a)
        .slice(0, 10)

      return {
        labels: sortedStates.map(([state]) => state),
        data: sortedStates.map(([,count]) => count)
      }
    }

    // Health score distribution data
    const getHealthScoreData = () => {
      const ranges = {
        'Excellent (90-100)': 0,
        'Good (70-89)': 0,
        'Fair (50-69)': 0,
        'Poor (0-49)': 0
      }

      props.chapters.forEach(chapter => {
        const score = chapter.healthScore || 0
        if (score >= 90) ranges['Excellent (90-100)']++
        else if (score >= 70) ranges['Good (70-89)']++
        else if (score >= 50) ranges['Fair (50-69)']++
        else ranges['Poor (0-49)']++
      })

      return {
        labels: Object.keys(ranges),
        data: Object.values(ranges)
      }
    }

    // Top performing states data
    const getTopStatesData = () => {
      const statePerformance = {}
      
      props.chapters.forEach(chapter => {
        if (!statePerformance[chapter.state]) {
          statePerformance[chapter.state] = {
            total: 0,
            count: 0,
            active: 0
          }
        }
        
        statePerformance[chapter.state].total += (chapter.healthScore || 0)
        statePerformance[chapter.state].count++
        if (chapter.active) statePerformance[chapter.state].active++
      })

      // Calculate average scores and sort
      const stateAverages = Object.entries(statePerformance)
        .map(([state, data]) => ({
          state,
          avgScore: data.total / data.count,
          activeRate: (data.active / data.count) * 100
        }))
        .sort((a, b) => b.avgScore - a.avgScore)
        .slice(0, 8)

      return {
        labels: stateAverages.map(s => s.state),
        healthScores: stateAverages.map(s => Math.round(s.avgScore)),
        activeRates: stateAverages.map(s => Math.round(s.activeRate))
      }
    }

    // Status chart data
    const getStatusData = () => {
      const active = props.chapters.filter(c => c.active).length
      const inactive = props.chapters.length - active

      return {
        labels: ['Active', 'Inactive'],
        data: [active, inactive]
      }
    }

    // Timeline data (simulated based on founded dates)
    const getTimelineData = () => {
      const monthCounts = {}
      const now = new Date()
      
      // Generate last 12 months
      for (let i = 11; i >= 0; i--) {
        const date = new Date(now.getFullYear(), now.getMonth() - i, 1)
        const key = date.toISOString().substring(0, 7) // YYYY-MM format
        monthCounts[key] = 0
      }

      // Count chapters founded in each month (simulated)
      props.chapters.forEach(chapter => {
        if (chapter.foundedDate) {
          const date = new Date(chapter.foundedDate)
          const key = date.toISOString().substring(0, 7)
          if (monthCounts.hasOwnProperty(key)) {
            monthCounts[key]++
          }
        }
      })

      return {
        labels: Object.keys(monthCounts).map(key => {
          const date = new Date(key + '-01')
          return date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' })
        }),
        data: Object.values(monthCounts)
      }
    }

    // Create charts
    const createCharts = async () => {
      await nextTick()
      const colors = getChartColors()

      // Destroy existing charts
      Object.values(chartInstances).forEach(chart => chart.destroy())
      chartInstances = {}

      // State Distribution Chart
      if (stateDistributionChart.value) {
        const stateData = getStateDistributionData()
        chartInstances.stateDistribution = new Chart(stateDistributionChart.value, {
          type: 'bar',
          data: {
            labels: stateData.labels,
            datasets: [{
              label: 'Chapters',
              data: stateData.data,
              backgroundColor: colors.primary,
              borderColor: colors.primary,
              borderWidth: 1,
              borderRadius: 4
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: { display: false }
            },
            scales: {
              y: {
                beginAtZero: true,
                ticks: { color: colors.text },
                grid: { color: colors.grid }
              },
              x: {
                ticks: { color: colors.text },
                grid: { color: colors.grid }
              }
            }
          }
        })
      }

      // Health Score Distribution Chart
      if (healthScoreChart.value) {
        const healthData = getHealthScoreData()
        chartInstances.healthScore = new Chart(healthScoreChart.value, {
          type: 'doughnut',
          data: {
            labels: healthData.labels,
            datasets: [{
              data: healthData.data,
              backgroundColor: [colors.success, colors.info, colors.warning, colors.danger],
              borderWidth: 2,
              borderColor: props.isDarkMode ? '#1f2937' : '#ffffff'
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'bottom',
                labels: { color: colors.text }
              }
            }
          }
        })
      }

      // Top States Chart
      if (topStatesChart.value) {
        const topStatesData = getTopStatesData()
        chartInstances.topStates = new Chart(topStatesChart.value, {
          type: 'bar',
          data: {
            labels: topStatesData.labels,
            datasets: [
              {
                label: 'Avg Health Score',
                data: topStatesData.healthScores,
                backgroundColor: colors.success,
                borderColor: colors.success,
                borderWidth: 1,
                borderRadius: 4,
                yAxisID: 'y'
              },
              {
                label: 'Active Rate %',
                data: topStatesData.activeRates,
                backgroundColor: colors.info,
                borderColor: colors.info,
                borderWidth: 1,
                borderRadius: 4,
                yAxisID: 'y1'
              }
            ]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              y: {
                type: 'linear',
                display: true,
                position: 'left',
                max: 100,
                ticks: { color: colors.text },
                grid: { color: colors.grid }
              },
              y1: {
                type: 'linear',
                display: true,
                position: 'right',
                max: 100,
                ticks: { color: colors.text },
                grid: { drawOnChartArea: false, color: colors.grid }
              },
              x: {
                ticks: { color: colors.text },
                grid: { color: colors.grid }
              }
            },
            plugins: {
              legend: {
                labels: { color: colors.text }
              }
            }
          }
        })
      }

      // Status Chart
      if (statusChart.value) {
        const statusData = getStatusData()
        chartInstances.status = new Chart(statusChart.value, {
          type: 'pie',
          data: {
            labels: statusData.labels,
            datasets: [{
              data: statusData.data,
              backgroundColor: [colors.success, colors.danger],
              borderWidth: 2,
              borderColor: props.isDarkMode ? '#1f2937' : '#ffffff'
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'bottom',
                labels: { color: colors.text }
              }
            }
          }
        })
      }

      // Timeline Chart
      if (timelineChart.value) {
        const timelineData = getTimelineData()
        chartInstances.timeline = new Chart(timelineChart.value, {
          type: 'line',
          data: {
            labels: timelineData.labels,
            datasets: [{
              label: 'New Chapters',
              data: timelineData.data,
              borderColor: colors.primary,
              backgroundColor: colors.background,
              borderWidth: 2,
              fill: true,
              tension: 0.4
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              y: {
                beginAtZero: true,
                ticks: { color: colors.text },
                grid: { color: colors.grid }
              },
              x: {
                ticks: { color: colors.text },
                grid: { color: colors.grid }
              }
            },
            plugins: {
              legend: {
                labels: { color: colors.text }
              }
            }
          }
        })
      }
    }

    // Watch for theme changes and chapter data changes
    watch([() => props.isDarkMode, () => props.chapters], () => {
      createCharts()
    }, { deep: true })

    onMounted(() => {
      createCharts()
    })

    return {
      stateDistributionChart,
      healthScoreChart,
      topStatesChart,
      statusChart,
      timelineChart,
      totalChapters,
      activeChapters,
      uniqueStates,
      averageHealthScore
    }
  }
}
</script>

<style scoped>
.chapter-analytics {
  padding: 1rem 0;
}

.card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-2px);
}

.display-6 {
  font-size: 2rem;
}

@media (max-width: 768px) {
  .chapter-analytics .row .col-lg-6,
  .chapter-analytics .row .col-lg-8,
  .chapter-analytics .row .col-lg-4 {
    margin-bottom: 1.5rem;
  }
}
</style>