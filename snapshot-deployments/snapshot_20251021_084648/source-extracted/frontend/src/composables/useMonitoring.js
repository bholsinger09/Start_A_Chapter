import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useEventAPI } from './useEventAPI'

export function useMonitoring(options = {}) {
  const {
    autoRefresh = true,
    refreshInterval = 30000, // 30 seconds
    enableRealTime = false
  } = options

  // Get API functions
  const {
    getSystemHealth,
    getMetrics,
    getOperationalMetrics,
    getAuditLogs
  } = useEventAPI()

  // State
  const isLoading = ref(false)
  const error = ref(null)
  const lastUpdated = ref(new Date())

  // Health data
  const healthData = ref({
    status: 'UNKNOWN',
    uptime: 0,
    database: null,
    memory: null,
    disk: null,
    api: null,
    services: {}
  })

  // Metrics data
  const metricsData = reactive({
    system: {
      activeEvents: 0,
      totalMembers: 0,
      totalChapters: 0,
      totalRsvps: 0,
      systemLoad: 0
    },
    events: {
      todayEvents: 0,
      weeklyEvents: 0,
      monthlyEvents: 0,
      averageAttendance: 0
    },
    rsvps: {
      todayRsvps: 0,
      pendingRsvps: 0,
      confirmedRsvps: 0,
      waitlistCount: 0
    },
    performance: {
      avgResponseTime: 0,
      requestsPerMinute: 0,
      errorRate: 0,
      uptime: 100
    }
  })

  // Operational metrics for charts
  const operationalData = ref({
    rsvpTrends: [],
    eventCapacityData: [],
    userActivityData: [],
    systemPerformanceData: []
  })

  // Audit logs
  const auditLogs = ref([])

  // Auto-refresh timer
  let refreshTimer = null
  let realTimeConnection = null

  // Computed properties
  const systemHealthStatus = computed(() => {
    const status = healthData.value.status?.toUpperCase()

    switch (status) {
      case 'UP':
      case 'HEALTHY':
        return {
          status: 'healthy',
          text: 'All systems operational',
          color: 'success'
        }
      case 'DOWN':
      case 'UNHEALTHY':
        return {
          status: 'unhealthy',
          text: 'System issues detected',
          color: 'danger'
        }
      case 'DEGRADED':
      case 'WARNING':
        return {
          status: 'degraded',
          text: 'Performance issues',
          color: 'warning'
        }
      default:
        return {
          status: 'unknown',
          text: 'Status unknown',
          color: 'secondary'
        }
    }
  })

  const criticalMetrics = computed(() => [
    {
      title: 'Active Events',
      value: metricsData.system.activeEvents,
      icon: 'bi bi-calendar-event',
      color: 'primary',
      trend: calculateTrend('activeEvents'),
      format: 'number'
    },
    {
      title: 'Total RSVPs',
      value: metricsData.system.totalRsvps,
      icon: 'bi bi-people-fill',
      color: 'success',
      trend: calculateTrend('totalRsvps'),
      format: 'number'
    },
    {
      title: 'System Load',
      value: metricsData.system.systemLoad,
      icon: 'bi bi-speedometer2',
      color: metricsData.system.systemLoad > 80 ? 'danger' : 'info',
      trend: calculateTrend('systemLoad'),
      format: 'percentage',
      showProgress: true,
      maxValue: 100
    },
    {
      title: 'Error Rate',
      value: metricsData.performance.errorRate,
      icon: 'bi bi-exclamation-triangle',
      color: metricsData.performance.errorRate > 5 ? 'danger' : 'success',
      trend: calculateTrend('errorRate'),
      format: 'percentage'
    }
  ])

  const rsvpTrendsChartData = computed(() => {
    return operationalData.value.rsvpTrends.map(item => ({
      label: formatDateForChart(item.date),
      value: item.count
    }))
  })

  const eventCapacityChartData = computed(() => {
    return operationalData.value.eventCapacityData.map(item => ({
      label: item.eventName,
      value: item.capacityUsage
    }))
  })

  const recentAuditLogs = computed(() => {
    return auditLogs.value.slice(0, 10) // Show last 10 logs
  })

  // Methods
  const loadSystemHealth = async () => {
    try {
      const response = await getSystemHealth()
      healthData.value = {
        status: response.data.status || 'UNKNOWN',
        uptime: response.data.uptime || 0,
        database: response.data.components?.database || null,
        memory: response.data.components?.memory || null,
        disk: response.data.components?.disk || null,
        api: response.data.components?.api || null,
        services: response.data.components?.services || {}
      }
    } catch (err) {
      console.error('Failed to load system health:', err)
      healthData.value.status = 'UNKNOWN'
      throw err
    }
  }

  const loadMetrics = async () => {
    try {
      const response = await getMetrics()
      const data = response.data

      // Update system metrics
      metricsData.system = {
        activeEvents: data.activeEvents || 0,
        totalMembers: data.totalMembers || 0,
        totalChapters: data.totalChapters || 0,
        totalRsvps: data.totalRsvps || 0,
        systemLoad: data.systemLoad || 0
      }

      // Update event metrics
      metricsData.events = {
        todayEvents: data.todayEvents || 0,
        weeklyEvents: data.weeklyEvents || 0,
        monthlyEvents: data.monthlyEvents || 0,
        averageAttendance: data.averageAttendance || 0
      }

      // Update RSVP metrics
      metricsData.rsvps = {
        todayRsvps: data.todayRsvps || 0,
        pendingRsvps: data.pendingRsvps || 0,
        confirmedRsvps: data.confirmedRsvps || 0,
        waitlistCount: data.waitlistCount || 0
      }

      // Update performance metrics
      metricsData.performance = {
        avgResponseTime: data.avgResponseTime || 0,
        requestsPerMinute: data.requestsPerMinute || 0,
        errorRate: data.errorRate || 0,
        uptime: data.uptime || 100
      }

    } catch (err) {
      console.error('Failed to load metrics:', err)
      throw err
    }
  }

  const loadOperationalMetrics = async (timeRange = '24h') => {
    try {
      const response = await getOperationalMetrics({ timeRange })
      const data = response.data

      operationalData.value = {
        rsvpTrends: data.rsvpTrends || [],
        eventCapacityData: data.eventCapacityData || [],
        userActivityData: data.userActivityData || [],
        systemPerformanceData: data.systemPerformanceData || []
      }

    } catch (err) {
      console.error('Failed to load operational metrics:', err)
      throw err
    }
  }

  const loadAuditLogs = async (params = {}) => {
    try {
      const defaultParams = {
        limit: 50,
        sortBy: 'timestamp',
        sortOrder: 'desc',
        ...params
      }

      const response = await getAuditLogs(defaultParams)
      auditLogs.value = response.data || []

    } catch (err) {
      console.error('Failed to load audit logs:', err)
      throw err
    }
  }

  const loadAllData = async () => {
    try {
      isLoading.value = true
      error.value = null

      await Promise.all([
        loadSystemHealth(),
        loadMetrics(),
        loadOperationalMetrics(),
        loadAuditLogs()
      ])

      lastUpdated.value = new Date()

    } catch (err) {
      error.value = err.message || 'Failed to load monitoring data'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const refreshData = async () => {
    await loadAllData()
  }

  const refreshMetrics = async () => {
    try {
      await Promise.all([
        loadMetrics(),
        loadOperationalMetrics()
      ])
      lastUpdated.value = new Date()
    } catch (err) {
      error.value = err.message || 'Failed to refresh metrics'
    }
  }

  const refreshHealth = async () => {
    try {
      await loadSystemHealth()
      lastUpdated.value = new Date()
    } catch (err) {
      error.value = err.message || 'Failed to refresh health data'
    }
  }

  // Utility functions
  const calculateTrend = (metricName) => {
    // This would calculate trend based on historical data
    // For now, return a placeholder
    const trendMap = {
      activeEvents: '+12%',
      totalRsvps: '+24%',
      systemLoad: '-5%',
      errorRate: '-15%'
    }
    return trendMap[metricName] || null
  }

  const formatDateForChart = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric'
    })
  }

  const getMetricsByCategory = (category) => {
    return metricsData[category] || {}
  }

  const getHealthComponentStatus = (componentName) => {
    return healthData.value[componentName] || null
  }

  // Real-time functionality (if enabled)
  const setupRealTimeUpdates = () => {
    if (!enableRealTime) return

    // This would set up WebSocket or Server-Sent Events
    // For now, just use polling
    if (refreshTimer) {
      clearInterval(refreshTimer)
    }

    refreshTimer = setInterval(refreshMetrics, 10000) // 10 seconds for real-time
  }

  const teardownRealTimeUpdates = () => {
    if (refreshTimer) {
      clearInterval(refreshTimer)
      refreshTimer = null
    }

    if (realTimeConnection) {
      realTimeConnection.close()
      realTimeConnection = null
    }
  }

  // Lifecycle management
  const startMonitoring = () => {
    loadAllData()

    if (autoRefresh) {
      refreshTimer = setInterval(refreshData, refreshInterval)
    }

    if (enableRealTime) {
      setupRealTimeUpdates()
    }
  }

  const stopMonitoring = () => {
    teardownRealTimeUpdates()
  }

  // Auto-start on mount
  onMounted(() => {
    startMonitoring()
  })

  // Cleanup on unmount
  onUnmounted(() => {
    stopMonitoring()
  })

  // Return the monitoring interface
  return {
    // State
    isLoading,
    error,
    lastUpdated,
    healthData,
    metricsData,
    operationalData,
    auditLogs,

    // Computed
    systemHealthStatus,
    criticalMetrics,
    rsvpTrendsChartData,
    eventCapacityChartData,
    recentAuditLogs,

    // Methods
    loadAllData,
    refreshData,
    refreshMetrics,
    refreshHealth,
    loadSystemHealth,
    loadMetrics,
    loadOperationalMetrics,
    loadAuditLogs,

    // Utilities
    getMetricsByCategory,
    getHealthComponentStatus,

    // Control
    startMonitoring,
    stopMonitoring
  }
}

// Additional composable for specific monitoring scenarios
export function useSystemAlerts() {
  const alerts = ref([])

  const addAlert = (alert) => {
    alerts.value.push({
      id: Date.now(),
      timestamp: new Date(),
      ...alert
    })
  }

  const removeAlert = (id) => {
    const index = alerts.value.findIndex(alert => alert.id === id)
    if (index > -1) {
      alerts.value.splice(index, 1)
    }
  }

  const clearAlerts = () => {
    alerts.value = []
  }

  const criticalAlerts = computed(() => {
    return alerts.value.filter(alert => alert.severity === 'critical')
  })

  const warningAlerts = computed(() => {
    return alerts.value.filter(alert => alert.severity === 'warning')
  })

  return {
    alerts,
    criticalAlerts,
    warningAlerts,
    addAlert,
    removeAlert,
    clearAlerts
  }
}

export function useMetricsHistory() {
  const history = ref({})

  const addMetricPoint = (metricName, value, timestamp = new Date()) => {
    if (!history.value[metricName]) {
      history.value[metricName] = []
    }

    history.value[metricName].push({
      value,
      timestamp
    })

    // Keep only last 100 points
    if (history.value[metricName].length > 100) {
      history.value[metricName] = history.value[metricName].slice(-100)
    }
  }

  const getMetricHistory = (metricName) => {
    return history.value[metricName] || []
  }

  const getMetricTrend = (metricName, periods = 5) => {
    const data = getMetricHistory(metricName)
    if (data.length < periods) return null

    const recent = data.slice(-periods)
    const older = data.slice(-(periods * 2), -periods)

    const recentAvg = recent.reduce((sum, p) => sum + p.value, 0) / recent.length
    const olderAvg = older.reduce((sum, p) => sum + p.value, 0) / older.length

    const change = ((recentAvg - olderAvg) / olderAvg) * 100

    return {
      change: change.toFixed(1),
      direction: change > 0 ? 'up' : change < 0 ? 'down' : 'stable'
    }
  }

  return {
    history,
    addMetricPoint,
    getMetricHistory,
    getMetricTrend
  }
}