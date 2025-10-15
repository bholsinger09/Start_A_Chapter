<template>
  <div class="chart-container">
    <div class="chart-header" v-if="title">
      <h6 class="chart-title">{{ title }}</h6>
      <div class="chart-actions" v-if="showActions">
        <button 
          class="btn btn-sm btn-outline-secondary"
          @click="$emit('refresh')"
          :disabled="isLoading"
        >
          <i class="bi bi-arrow-clockwise"></i>
        </button>
      </div>
    </div>
    
    <!-- Loading State -->
    <div v-if="isLoading" class="chart-loading">
      <div class="d-flex justify-content-center align-items-center" style="height: 200px;">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
    </div>
    
    <!-- Error State -->
    <div v-else-if="error" class="chart-error">
      <div class="alert alert-warning">
        <i class="bi bi-exclamation-triangle me-2"></i>
        Unable to load chart data: {{ error }}
      </div>
    </div>
    
    <!-- Chart Content -->
    <div v-else class="chart-content">
      <!-- Line Chart -->
      <div v-if="type === 'line'" class="line-chart">
        <svg :width="chartWidth" :height="chartHeight" class="chart-svg">
          <!-- Grid lines -->
          <g class="grid">
            <line 
              v-for="(tick, index) in yTicks" 
              :key="`y-${index}`"
              :x1="padding.left" 
              :x2="chartWidth - padding.right"
              :y1="getYPosition(tick)" 
              :y2="getYPosition(tick)"
              class="grid-line"
            />
          </g>
          
          <!-- Axes -->
          <g class="axes">
            <!-- Y-axis -->
            <line 
              :x1="padding.left" 
              :x2="padding.left"
              :y1="padding.top" 
              :y2="chartHeight - padding.bottom"
              class="axis-line"
            />
            
            <!-- X-axis -->
            <line 
              :x1="padding.left" 
              :x2="chartWidth - padding.right"
              :y1="chartHeight - padding.bottom" 
              :y2="chartHeight - padding.bottom"
              class="axis-line"
            />
          </g>
          
          <!-- Data line -->
          <path 
            :d="linePath" 
            class="data-line"
            :stroke="color"
            fill="none"
            stroke-width="2"
          />
          
          <!-- Data points -->
          <circle 
            v-for="(point, index) in processedData" 
            :key="index"
            :cx="getXPosition(index)" 
            :cy="getYPosition(point.value)"
            r="4"
            :fill="color"
            class="data-point"
          />
          
          <!-- Y-axis labels -->
          <text 
            v-for="(tick, index) in yTicks" 
            :key="`y-label-${index}`"
            :x="padding.left - 10" 
            :y="getYPosition(tick) + 4"
            class="axis-label"
            text-anchor="end"
          >
            {{ formatYValue(tick) }}
          </text>
          
          <!-- X-axis labels -->
          <text 
            v-for="(point, index) in processedData" 
            :key="`x-label-${index}`"
            :x="getXPosition(index)" 
            :y="chartHeight - padding.bottom + 16"
            class="axis-label"
            text-anchor="middle"
          >
            {{ formatXValue(point.label) }}
          </text>
        </svg>
      </div>
      
      <!-- Bar Chart -->
      <div v-else-if="type === 'bar'" class="bar-chart">
        <svg :width="chartWidth" :height="chartHeight" class="chart-svg">
          <!-- Grid lines -->
          <g class="grid">
            <line 
              v-for="(tick, index) in yTicks" 
              :key="`y-${index}`"
              :x1="padding.left" 
              :x2="chartWidth - padding.right"
              :y1="getYPosition(tick)" 
              :y2="getYPosition(tick)"
              class="grid-line"
            />
          </g>
          
          <!-- Bars -->
          <rect 
            v-for="(point, index) in processedData" 
            :key="index"
            :x="getXPosition(index) - barWidth / 2" 
            :y="getYPosition(point.value)"
            :width="barWidth"
            :height="chartHeight - padding.bottom - getYPosition(point.value)"
            :fill="getBarColor(point.value, index)"
            class="data-bar"
          />
          
          <!-- Axes -->
          <g class="axes">
            <line 
              :x1="padding.left" 
              :x2="padding.left"
              :y1="padding.top" 
              :y2="chartHeight - padding.bottom"
              class="axis-line"
            />
            <line 
              :x1="padding.left" 
              :x2="chartWidth - padding.right"
              :y1="chartHeight - padding.bottom" 
              :y2="chartHeight - padding.bottom"
              class="axis-line"
            />
          </g>
          
          <!-- Labels -->
          <text 
            v-for="(tick, index) in yTicks" 
            :key="`y-label-${index}`"
            :x="padding.left - 10" 
            :y="getYPosition(tick) + 4"
            class="axis-label"
            text-anchor="end"
          >
            {{ formatYValue(tick) }}
          </text>
          
          <text 
            v-for="(point, index) in processedData" 
            :key="`x-label-${index}`"
            :x="getXPosition(index)" 
            :y="chartHeight - padding.bottom + 16"
            class="axis-label"
            text-anchor="middle"
          >
            {{ formatXValue(point.label) }}
          </text>
        </svg>
      </div>
      
      <!-- Donut Chart -->
      <div v-else-if="type === 'donut'" class="donut-chart">
        <svg :width="chartWidth" :height="chartHeight" class="chart-svg">
          <g :transform="`translate(${chartWidth / 2}, ${chartHeight / 2})`">
            <!-- Donut segments -->
            <path 
              v-for="(segment, index) in donutSegments" 
              :key="index"
              :d="segment.path"
              :fill="segment.color"
              class="donut-segment"
            />
            
            <!-- Center value -->
            <text 
              class="donut-center-value"
              text-anchor="middle"
              dy="0.3em"
            >
              {{ centerValue }}
            </text>
          </g>
        </svg>
        
        <!-- Legend -->
        <div class="chart-legend">
          <div 
            v-for="(point, index) in processedData" 
            :key="index"
            class="legend-item"
          >
            <div 
              class="legend-color"
              :style="{ backgroundColor: getDonutColor(index) }"
            ></div>
            <span class="legend-label">{{ point.label }}: {{ point.value }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, defineProps, defineEmits } from 'vue'

const props = defineProps({
  title: String,
  data: {
    type: Array,
    default: () => []
  },
  type: {
    type: String,
    default: 'line',
    validator: (value) => ['line', 'bar', 'donut'].includes(value)
  },
  color: {
    type: String,
    default: '#0d6efd'
  },
  colors: {
    type: Array,
    default: () => ['#0d6efd', '#198754', '#dc3545', '#ffc107', '#0dcaf0', '#6f42c1']
  },
  width: {
    type: Number,
    default: 400
  },
  height: {
    type: Number,
    default: 200
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
  }
})

const emit = defineEmits(['refresh'])

// Chart dimensions
const padding = {
  top: 20,
  right: 20,
  bottom: 40,
  left: 60
}

const chartWidth = computed(() => props.width)
const chartHeight = computed(() => props.height)

// Process data
const processedData = computed(() => {
  if (!Array.isArray(props.data)) return []
  
  return props.data.map(item => {
    if (typeof item === 'object' && item !== null) {
      return {
        label: item.label || item.x || item.name || '',
        value: item.value || item.y || item.count || 0
      }
    }
    return {
      label: '',
      value: item || 0
    }
  })
})

// Chart calculations
const maxValue = computed(() => {
  const values = processedData.value.map(d => d.value)
  return Math.max(...values, 0)
})

const minValue = computed(() => {
  const values = processedData.value.map(d => d.value)
  return Math.min(...values, 0)
})

const yTicks = computed(() => {
  const tickCount = 5
  const range = maxValue.value - minValue.value
  const step = range / (tickCount - 1)
  
  return Array.from({ length: tickCount }, (_, i) => 
    minValue.value + (step * i)
  )
})

const barWidth = computed(() => {
  if (processedData.value.length === 0) return 0
  const availableWidth = chartWidth.value - padding.left - padding.right
  return (availableWidth / processedData.value.length) * 0.8
})

// Position calculations
const getXPosition = (index) => {
  const availableWidth = chartWidth.value - padding.left - padding.right
  const step = availableWidth / (processedData.value.length - 1 || 1)
  return padding.left + (step * index)
}

const getYPosition = (value) => {
  const availableHeight = chartHeight.value - padding.top - padding.bottom
  const range = maxValue.value - minValue.value || 1
  const normalizedValue = (value - minValue.value) / range
  return chartHeight.value - padding.bottom - (normalizedValue * availableHeight)
}

// Line chart path
const linePath = computed(() => {
  if (processedData.value.length === 0) return ''
  
  const points = processedData.value.map((point, index) => {
    const x = getXPosition(index)
    const y = getYPosition(point.value)
    return `${x},${y}`
  })
  
  return `M ${points.join(' L ')}`
})

// Bar colors
const getBarColor = (value, index) => {
  if (props.colors.length > 0) {
    return props.colors[index % props.colors.length]
  }
  return props.color
}

// Donut chart calculations
const donutSegments = computed(() => {
  const total = processedData.value.reduce((sum, d) => sum + d.value, 0)
  if (total === 0) return []
  
  const radius = Math.min(chartWidth.value, chartHeight.value) / 2 - 20
  const innerRadius = radius * 0.6
  
  let currentAngle = -Math.PI / 2
  
  return processedData.value.map((point, index) => {
    const angle = (point.value / total) * 2 * Math.PI
    const startAngle = currentAngle
    const endAngle = currentAngle + angle
    
    const x1 = Math.cos(startAngle) * radius
    const y1 = Math.sin(startAngle) * radius
    const x2 = Math.cos(endAngle) * radius
    const y2 = Math.sin(endAngle) * radius
    
    const x3 = Math.cos(endAngle) * innerRadius
    const y3 = Math.sin(endAngle) * innerRadius
    const x4 = Math.cos(startAngle) * innerRadius
    const y4 = Math.sin(startAngle) * innerRadius
    
    const largeArcFlag = angle > Math.PI ? 1 : 0
    
    const path = [
      `M ${x1} ${y1}`,
      `A ${radius} ${radius} 0 ${largeArcFlag} 1 ${x2} ${y2}`,
      `L ${x3} ${y3}`,
      `A ${innerRadius} ${innerRadius} 0 ${largeArcFlag} 0 ${x4} ${y4}`,
      'Z'
    ].join(' ')
    
    currentAngle += angle
    
    return {
      path,
      color: getDonutColor(index)
    }
  })
})

const getDonutColor = (index) => {
  return props.colors[index % props.colors.length]
}

const centerValue = computed(() => {
  const total = processedData.value.reduce((sum, d) => sum + d.value, 0)
  return formatYValue(total)
})

// Formatting functions
const formatYValue = (value) => {
  if (value >= 1000000) {
    return (value / 1000000).toFixed(1) + 'M'
  }
  if (value >= 1000) {
    return (value / 1000).toFixed(1) + 'K'
  }
  return Math.round(value).toString()
}

const formatXValue = (label) => {
  if (typeof label === 'string' && label.length > 8) {
    return label.substring(0, 8) + '...'
  }
  return label
}
</script>

<style scoped>
.chart-container {
  background: white;
  border-radius: 12px;
  padding: 1.25rem;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #f8f9fa;
}

.chart-title {
  color: #495057;
  font-weight: 600;
  margin: 0;
}

.chart-actions .btn {
  border: none;
  background: transparent;
  color: #6c757d;
}

.chart-actions .btn:hover {
  color: #495057;
  background: #f8f9fa;
}

.chart-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.chart-svg {
  background: white;
  border-radius: 8px;
}

.grid-line {
  stroke: #f8f9fa;
  stroke-width: 1;
}

.axis-line {
  stroke: #dee2e6;
  stroke-width: 1;
}

.axis-label {
  fill: #6c757d;
  font-size: 12px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.data-line {
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.data-point {
  cursor: pointer;
  transition: r 0.2s ease;
}

.data-point:hover {
  r: 6;
}

.data-bar {
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.data-bar:hover {
  opacity: 0.8;
}

.donut-segment {
  cursor: pointer;
  transition: transform 0.2s ease;
}

.donut-segment:hover {
  transform: scale(1.05);
}

.donut-center-value {
  fill: #495057;
  font-size: 18px;
  font-weight: 600;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.chart-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-top: 1rem;
  justify-content: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-label {
  font-size: 0.875rem;
  color: #6c757d;
}

.chart-loading,
.chart-error {
  padding: 2rem 1rem;
  text-align: center;
}

/* Responsive design */
@media (max-width: 576px) {
  .chart-container {
    padding: 1rem;
  }
  
  .chart-header {
    flex-direction: column;
    gap: 0.5rem;
    align-items: stretch;
  }
  
  .chart-legend {
    flex-direction: column;
    align-items: center;
  }
}
</style>