<template>
  <div class="system-health-card">
    <div class="health-header">
      <h6 class="card-title mb-0">
        <i class="bi bi-heart-pulse me-2"></i>
        System Health
      </h6>
      
      <div class="health-actions">
        <button 
          class="btn btn-sm btn-outline-secondary"
          @click="refreshHealth"
          :disabled="isLoading"
        >
          <i class="bi bi-arrow-clockwise" :class="{ 'spin': isLoading }"></i>
        </button>
      </div>
    </div>

    <!-- Overall Status -->
    <div class="overall-status">
      <div class="status-indicator" :class="overallStatusClass">
        <div class="status-dot"></div>
        <span class="status-text">{{ overallStatusText }}</span>
      </div>
      
      <div class="uptime-info" v-if="healthData.uptime">
        <small class="text-muted">
          Uptime: {{ formatUptime(healthData.uptime) }}
        </small>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="loading-state">
      <div class="d-flex justify-content-center">
        <div class="spinner-border spinner-border-sm text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-state">
      <div class="alert alert-warning">
        <i class="bi bi-exclamation-triangle me-2"></i>
        <strong>Unable to fetch health data</strong>
        <br>
        <small>{{ error }}</small>
      </div>
    </div>

    <!-- Health Details -->
    <div v-else class="health-details">
      <!-- Database Status -->
      <div class="health-item">
        <div class="health-item-header">
          <div class="item-status" :class="getStatusClass(healthData.database?.status)">
            <i :class="getStatusIcon(healthData.database?.status)"></i>
          </div>
          <span class="item-name">Database</span>
        </div>
        <div class="item-details" v-if="healthData.database">
          <small class="text-muted">
            Response: {{ healthData.database.responseTime || 'N/A' }}ms
          </small>
        </div>
      </div>

      <!-- Memory Status -->
      <div class="health-item" v-if="healthData.memory">
        <div class="health-item-header">
          <div class="item-status" :class="getMemoryStatusClass(healthData.memory.usage)">
            <i class="bi bi-memory"></i>
          </div>
          <span class="item-name">Memory</span>
        </div>
        <div class="item-details">
          <div class="memory-usage">
            <div class="progress mb-1">
              <div 
                class="progress-bar" 
                :class="getMemoryProgressClass(healthData.memory.usage)"
                :style="{ width: healthData.memory.usage + '%' }"
              ></div>
            </div>
            <small class="text-muted">
              {{ formatBytes(healthData.memory.used) }} / 
              {{ formatBytes(healthData.memory.total) }}
              ({{ healthData.memory.usage }}%)
            </small>
          </div>
        </div>
      </div>

      <!-- Disk Status -->
      <div class="health-item" v-if="healthData.disk">
        <div class="health-item-header">
          <div class="item-status" :class="getDiskStatusClass(healthData.disk.usage)">
            <i class="bi bi-hdd"></i>
          </div>
          <span class="item-name">Disk Space</span>
        </div>
        <div class="item-details">
          <div class="disk-usage">
            <div class="progress mb-1">
              <div 
                class="progress-bar" 
                :class="getDiskProgressClass(healthData.disk.usage)"
                :style="{ width: healthData.disk.usage + '%' }"
              ></div>
            </div>
            <small class="text-muted">
              {{ formatBytes(healthData.disk.used) }} / 
              {{ formatBytes(healthData.disk.total) }}
              ({{ healthData.disk.usage }}%)
            </small>
          </div>
        </div>
      </div>

      <!-- API Status -->
      <div class="health-item">
        <div class="health-item-header">
          <div class="item-status" :class="getStatusClass(healthData.api?.status)">
            <i :class="getStatusIcon(healthData.api?.status)"></i>
          </div>
          <span class="item-name">API Services</span>
        </div>
        <div class="item-details" v-if="healthData.api">
          <small class="text-muted">
            {{ healthData.api.activeConnections || 0 }} active connections
          </small>
        </div>
      </div>

      <!-- Additional Services -->
      <div 
        v-for="(service, name) in healthData.services" 
        :key="name"
        class="health-item"
      >
        <div class="health-item-header">
          <div class="item-status" :class="getStatusClass(service.status)">
            <i :class="getServiceIcon(name)"></i>
          </div>
          <span class="item-name">{{ formatServiceName(name) }}</span>
        </div>
        <div class="item-details" v-if="service.message">
          <small class="text-muted">{{ service.message }}</small>
        </div>
      </div>
    </div>

    <!-- Last Updated -->
    <div class="health-footer" v-if="!isLoading && !error">
      <small class="text-muted">
        Last updated: {{ formatLastUpdated(lastUpdated) }}
      </small>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, defineProps, defineEmits } from 'vue'

const props = defineProps({
  healthData: {
    type: Object,
    default: () => ({})
  },
  isLoading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: null
  },
  autoRefresh: {
    type: Boolean,
    default: true
  },
  refreshInterval: {
    type: Number,
    default: 30000 // 30 seconds
  }
})

const emit = defineEmits(['refresh'])

// Local state
const lastUpdated = ref(new Date())
let refreshTimer = null

// Computed properties
const overallStatusClass = computed(() => {
  const status = props.healthData.status || 'UNKNOWN'
  
  switch (status.toUpperCase()) {
    case 'UP':
    case 'HEALTHY':
      return 'status-healthy'
    case 'DOWN':
    case 'UNHEALTHY':
      return 'status-unhealthy'
    case 'DEGRADED':
    case 'WARNING':
      return 'status-degraded'
    default:
      return 'status-unknown'
  }
})

const overallStatusText = computed(() => {
  const status = props.healthData.status || 'Unknown'
  
  switch (status.toUpperCase()) {
    case 'UP':
      return 'System Online'
    case 'DOWN':
      return 'System Offline'
    case 'DEGRADED':
      return 'Performance Issues'
    case 'HEALTHY':
      return 'All Systems Operational'
    case 'UNHEALTHY':
      return 'System Issues Detected'
    default:
      return 'Status Unknown'
  }
})

// Methods
const refreshHealth = () => {
  lastUpdated.value = new Date()
  emit('refresh')
}

const getStatusClass = (status) => {
  if (!status) return 'status-unknown'
  
  switch (status.toUpperCase()) {
    case 'UP':
    case 'HEALTHY':
    case 'OK':
      return 'status-healthy'
    case 'DOWN':
    case 'UNHEALTHY':
    case 'ERROR':
      return 'status-unhealthy'
    case 'DEGRADED':
    case 'WARNING':
      return 'status-degraded'
    default:
      return 'status-unknown'
  }
}

const getStatusIcon = (status) => {
  if (!status) return 'bi bi-question-circle'
  
  switch (status.toUpperCase()) {
    case 'UP':
    case 'HEALTHY':
    case 'OK':
      return 'bi bi-check-circle-fill'
    case 'DOWN':
    case 'UNHEALTHY':
    case 'ERROR':
      return 'bi bi-x-circle-fill'
    case 'DEGRADED':
    case 'WARNING':
      return 'bi bi-exclamation-triangle-fill'
    default:
      return 'bi bi-question-circle'
  }
}

const getMemoryStatusClass = (usage) => {
  if (usage >= 90) return 'status-unhealthy'
  if (usage >= 75) return 'status-degraded'
  return 'status-healthy'
}

const getMemoryProgressClass = (usage) => {
  if (usage >= 90) return 'bg-danger'
  if (usage >= 75) return 'bg-warning'
  return 'bg-success'
}

const getDiskStatusClass = (usage) => {
  if (usage >= 95) return 'status-unhealthy'
  if (usage >= 85) return 'status-degraded'
  return 'status-healthy'
}

const getDiskProgressClass = (usage) => {
  if (usage >= 95) return 'bg-danger'
  if (usage >= 85) return 'bg-warning'
  return 'bg-success'
}

const getServiceIcon = (serviceName) => {
  const iconMap = {
    redis: 'bi bi-speedometer2',
    elasticsearch: 'bi bi-search',
    email: 'bi bi-envelope',
    storage: 'bi bi-cloud',
    cache: 'bi bi-lightning'
  }
  
  return iconMap[serviceName.toLowerCase()] || 'bi bi-gear'
}

const formatServiceName = (name) => {
  return name.charAt(0).toUpperCase() + name.slice(1).replace(/([A-Z])/g, ' $1')
}

const formatBytes = (bytes) => {
  if (!bytes) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatUptime = (uptime) => {
  if (!uptime) return 'N/A'
  
  const seconds = Math.floor(uptime / 1000)
  const days = Math.floor(seconds / 86400)
  const hours = Math.floor((seconds % 86400) / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  
  if (days > 0) {
    return `${days}d ${hours}h ${minutes}m`
  } else if (hours > 0) {
    return `${hours}h ${minutes}m`
  } else {
    return `${minutes}m`
  }
}

const formatLastUpdated = (date) => {
  const now = new Date()
  const diff = Math.floor((now - date) / 1000)
  
  if (diff < 60) return 'Just now'
  if (diff < 3600) return `${Math.floor(diff / 60)} minutes ago`
  return date.toLocaleTimeString()
}

// Lifecycle
onMounted(() => {
  if (props.autoRefresh) {
    refreshTimer = setInterval(refreshHealth, props.refreshInterval)
  }
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.system-health-card {
  background: white;
  border-radius: 12px;
  padding: 1.25rem;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.health-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #f8f9fa;
}

.card-title {
  color: #495057;
  font-weight: 600;
}

.health-actions .btn {
  border: none;
  background: transparent;
  color: #6c757d;
}

.health-actions .btn:hover {
  color: #495057;
  background: #f8f9fa;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.overall-status {
  margin-bottom: 1.25rem;
  text-align: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.status-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.status-healthy .status-dot {
  background: #198754;
}

.status-unhealthy .status-dot {
  background: #dc3545;
}

.status-degraded .status-dot {
  background: #ffc107;
}

.status-unknown .status-dot {
  background: #6c757d;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.status-text {
  font-weight: 600;
  font-size: 1rem;
}

.status-healthy .status-text {
  color: #198754;
}

.status-unhealthy .status-text {
  color: #dc3545;
}

.status-degraded .status-text {
  color: #f57c00;
}

.status-unknown .status-text {
  color: #6c757d;
}

.uptime-info {
  margin-top: 0.5rem;
}

.loading-state,
.error-state {
  padding: 2rem 1rem;
  text-align: center;
}

.health-details {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.health-item {
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #e9ecef;
}

.health-item-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
}

.item-status {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  font-size: 0.875rem;
}

.status-healthy {
  color: #198754;
  background: rgba(25, 135, 84, 0.1);
}

.status-unhealthy {
  color: #dc3545;
  background: rgba(220, 53, 69, 0.1);
}

.status-degraded {
  color: #f57c00;
  background: rgba(245, 124, 0, 0.1);
}

.status-unknown {
  color: #6c757d;
  background: rgba(108, 117, 125, 0.1);
}

.item-name {
  font-weight: 500;
  color: #495057;
}

.item-details {
  margin-left: 2.5rem;
}

.memory-usage,
.disk-usage {
  margin-top: 0.5rem;
}

.progress {
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
}

.progress-bar {
  border-radius: 3px;
  transition: width 0.3s ease;
}

.health-footer {
  margin-top: 1rem;
  padding-top: 0.75rem;
  border-top: 1px solid #f8f9fa;
  text-align: center;
}

/* Responsive design */
@media (max-width: 576px) {
  .system-health-card {
    padding: 1rem;
  }
  
  .health-header {
    flex-direction: column;
    gap: 0.5rem;
    align-items: stretch;
  }
  
  .overall-status {
    padding: 0.75rem;
  }
  
  .health-item {
    padding: 0.5rem;
  }
  
  .item-details {
    margin-left: 2rem;
  }
}
</style>