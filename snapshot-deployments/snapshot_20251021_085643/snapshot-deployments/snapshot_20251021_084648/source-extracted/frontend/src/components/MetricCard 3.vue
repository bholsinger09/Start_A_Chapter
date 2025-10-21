<template>
  <div class="metric-card" :class="cardClass">
    <div class="metric-header">
      <div class="metric-icon" :class="iconClass">
        <i :class="icon"></i>
      </div>
      <div class="metric-actions" v-if="showActions">
        <button 
          class="btn btn-sm btn-outline-secondary"
          @click="$emit('refresh')"
          :disabled="isLoading"
        >
          <i class="bi bi-arrow-clockwise"></i>
        </button>
      </div>
    </div>
    
    <div class="metric-content">
      <div class="metric-value">
        <span v-if="isLoading" class="loading-placeholder">
          <div class="placeholder-glow">
            <span class="placeholder col-8"></span>
          </div>
        </span>
        <span v-else class="value-display">
          {{ formattedValue }}
        </span>
      </div>
      
      <div class="metric-label">
        {{ title }}
      </div>
      
      <div class="metric-trend" v-if="trend && !isLoading">
        <div class="trend-container" :class="trendClass">
          <i :class="trendIcon"></i>
          <span class="trend-value">{{ trend }}</span>
          <span class="trend-period" v-if="trendPeriod">{{ trendPeriod }}</span>
        </div>
      </div>
      
      <div class="metric-subtitle" v-if="subtitle && !isLoading">
        <small class="text-muted">{{ subtitle }}</small>
      </div>
    </div>
    
    <!-- Progress bar for percentage metrics -->
    <div v-if="showProgress && !isLoading" class="metric-progress">
      <div class="progress">
        <div 
          class="progress-bar" 
          :class="progressBarClass"
          :style="{ width: progressPercentage + '%' }"
          role="progressbar"
        ></div>
      </div>
      <div class="progress-labels">
        <small class="text-muted">{{ progressLabel }}</small>
      </div>
    </div>
    
    <!-- Error state -->
    <div v-if="error" class="metric-error">
      <i class="bi bi-exclamation-triangle text-warning"></i>
      <small class="text-muted">{{ error }}</small>
    </div>
  </div>
</template>

<script setup>
import { computed, defineProps, defineEmits } from 'vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: [Number, String],
    required: true
  },
  icon: {
    type: String,
    required: true
  },
  color: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'success', 'danger', 'warning', 'info', 'secondary'].includes(value)
  },
  trend: {
    type: String,
    default: null
  },
  trendPeriod: {
    type: String,
    default: 'vs last month'
  },
  subtitle: {
    type: String,
    default: null
  },
  isLoading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: null
  },
  showActions: {
    type: Boolean,
    default: false
  },
  format: {
    type: String,
    default: 'number',
    validator: (value) => ['number', 'currency', 'percentage', 'decimal'].includes(value)
  },
  showProgress: {
    type: Boolean,
    default: false
  },
  maxValue: {
    type: Number,
    default: 100
  },
  progressLabel: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: 'normal',
    validator: (value) => ['small', 'normal', 'large'].includes(value)
  }
})

const emit = defineEmits(['refresh', 'click'])

// Computed properties
const cardClass = computed(() => ({
  'metric-card-small': props.size === 'small',
  'metric-card-large': props.size === 'large',
  'metric-card-error': props.error,
  'metric-card-loading': props.isLoading
}))

const iconClass = computed(() => `icon-${props.color}`)

const formattedValue = computed(() => {
  if (props.error) return 'N/A'
  
  const value = props.value
  
  switch (props.format) {
    case 'currency':
      return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
      }).format(value)
      
    case 'percentage':
      return `${value}%`
      
    case 'decimal':
      return parseFloat(value).toFixed(2)
      
    case 'number':
    default:
      if (typeof value === 'number') {
        return new Intl.NumberFormat('en-US').format(value)
      }
      return value
  }
})

const trendClass = computed(() => {
  if (!props.trend) return ''
  
  const trendValue = props.trend.replace(/[^0-9.-]/g, '')
  const numericTrend = parseFloat(trendValue)
  
  if (numericTrend > 0) return 'trend-positive'
  if (numericTrend < 0) return 'trend-negative'
  return 'trend-neutral'
})

const trendIcon = computed(() => {
  const trendClass = trendClass.value
  
  switch (trendClass) {
    case 'trend-positive':
      return 'bi bi-trending-up'
    case 'trend-negative':
      return 'bi bi-trending-down'
    default:
      return 'bi bi-dash'
  }
})

const progressPercentage = computed(() => {
  if (!props.showProgress) return 0
  
  const value = typeof props.value === 'number' ? props.value : parseFloat(props.value)
  return Math.min(Math.max((value / props.maxValue) * 100, 0), 100)
})

const progressBarClass = computed(() => {
  const percentage = progressPercentage.value
  
  if (percentage >= 90) return 'bg-danger'
  if (percentage >= 75) return 'bg-warning'
  if (percentage >= 50) return 'bg-info'
  return 'bg-success'
})
</script>

<style scoped>
.metric-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.metric-card-small {
  padding: 1rem;
}

.metric-card-large {
  padding: 2rem;
}

.metric-card-error {
  border-color: #ffc107;
  background: #fff9e6;
}

.metric-card-loading {
  pointer-events: none;
}

.metric-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  color: white;
}

.icon-primary {
  background: linear-gradient(135deg, #0d6efd, #0b5ed7);
}

.icon-success {
  background: linear-gradient(135deg, #198754, #157347);
}

.icon-danger {
  background: linear-gradient(135deg, #dc3545, #bb2d3b);
}

.icon-warning {
  background: linear-gradient(135deg, #ffc107, #ffca2c);
  color: #212529;
}

.icon-info {
  background: linear-gradient(135deg, #0dcaf0, #31d2f2);
}

.icon-secondary {
  background: linear-gradient(135deg, #6c757d, #5c636a);
}

.metric-content {
  margin-bottom: 1rem;
}

.metric-value {
  margin-bottom: 0.5rem;
}

.value-display {
  font-size: 2rem;
  font-weight: 700;
  color: #212529;
  line-height: 1.2;
}

.metric-card-small .value-display {
  font-size: 1.5rem;
}

.metric-card-large .value-display {
  font-size: 2.5rem;
}

.loading-placeholder {
  display: block;
  height: 2rem;
}

.metric-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #6c757d;
  margin-bottom: 0.75rem;
  line-height: 1.3;
}

.metric-trend {
  margin-bottom: 0.5rem;
}

.trend-container {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;
  font-weight: 500;
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  width: fit-content;
}

.trend-positive {
  color: #198754;
  background: rgba(25, 135, 84, 0.1);
}

.trend-negative {
  color: #dc3545;
  background: rgba(220, 53, 69, 0.1);
}

.trend-neutral {
  color: #6c757d;
  background: rgba(108, 117, 125, 0.1);
}

.trend-value {
  font-weight: 600;
}

.trend-period {
  color: #6c757d;
  margin-left: 0.25rem;
}

.metric-subtitle {
  font-size: 0.75rem;
}

.metric-progress {
  margin-top: 1rem;
}

.progress {
  height: 6px;
  border-radius: 3px;
  background: #f8f9fa;
}

.progress-bar {
  border-radius: 3px;
  transition: width 0.3s ease;
}

.progress-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 0.5rem;
  font-size: 0.75rem;
}

.metric-error {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
  margin-top: 1rem;
}

.metric-actions .btn {
  border: none;
  background: transparent;
  color: #6c757d;
}

.metric-actions .btn:hover {
  color: #495057;
  background: #f8f9fa;
}

/* Responsive design */
@media (max-width: 576px) {
  .metric-card {
    padding: 1rem;
  }
  
  .metric-icon {
    width: 40px;
    height: 40px;
    font-size: 1rem;
  }
  
  .value-display {
    font-size: 1.5rem;
  }
  
  .metric-label {
    font-size: 0.8125rem;
  }
}

/* Animation for loading */
.placeholder-glow .placeholder {
  animation: placeholder-glow 2s ease-in-out infinite alternate;
  background: linear-gradient(90deg, #f8f9fa, #e9ecef, #f8f9fa);
  border-radius: 4px;
  height: 2rem;
}

@keyframes placeholder-glow {
  0% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}
</style>