<!--
  LoadingSpinner Component - Reusable Loading State Component
  Provides consistent loading UI/UX across the application
-->
<template>
  <div :class="containerClasses" :style="containerStyle">
    <!-- Spinner -->
    <div
      :class="spinnerClasses"
      :style="spinnerStyle"
      role="status"
      :aria-label="ariaLabel"
    >
      <span class="visually-hidden">{{ accessibleText }}</span>
    </div>

    <!-- Loading Message -->
    <div
      v-if="message || $slots.default"
      :class="messageClasses"
      :style="messageStyle"
    >
      <slot>
        {{ message }}
      </slot>
    </div>

    <!-- Loading Progress (optional) -->
    <div
      v-if="showProgress && progress !== null"
      class="loading-progress mt-2"
    >
      <div class="progress" :style="{ height: progressHeight }">
        <div
          class="progress-bar"
          :class="progressBarClasses"
          role="progressbar"
          :style="{ width: `${progress}%` }"
          :aria-valuenow="progress"
          aria-valuemin="0"
          aria-valuemax="100"
        >
          {{ showProgressText ? `${progress}%` : '' }}
        </div>
      </div>
    </div>

    <!-- Additional Content -->
    <div v-if="$slots.content" class="loading-content mt-3">
      <slot name="content"></slot>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { CSS_CLASSES } from '@/constants'

export default {
  name: 'LoadingSpinner',
  
  props: {
    // Loading state
    loading: {
      type: Boolean,
      default: true
    },
    
    // Spinner configuration
    size: {
      type: String,
      default: 'default',
      validator: (value) => ['sm', 'default', 'lg', 'xl'].includes(value)
    },
    
    variant: {
      type: String,
      default: 'primary',
      validator: (value) => [
        'primary', 'secondary', 'success', 'danger', 
        'warning', 'info', 'light', 'dark'
      ].includes(value)
    },
    
    type: {
      type: String,
      default: 'border',
      validator: (value) => ['border', 'grow'].includes(value)
    },
    
    // Layout
    centered: {
      type: Boolean,
      default: true
    },
    
    fullHeight: {
      type: Boolean,
      default: false
    },
    
    overlay: {
      type: Boolean,
      default: false
    },
    
    // Content
    message: {
      type: String,
      default: ''
    },
    
    messageSize: {
      type: String,
      default: 'default',
      validator: (value) => ['sm', 'default', 'lg'].includes(value)
    },
    
    // Progress bar
    showProgress: {
      type: Boolean,
      default: false
    },
    
    progress: {
      type: Number,
      default: null,
      validator: (value) => value === null || (value >= 0 && value <= 100)
    },
    
    progressHeight: {
      type: String,
      default: '4px'
    },
    
    showProgressText: {
      type: Boolean,
      default: false
    },
    
    progressVariant: {
      type: String,
      default: 'primary'
    },
    
    // Accessibility
    ariaLabel: {
      type: String,
      default: 'Loading'
    },
    
    accessibleText: {
      type: String,
      default: 'Loading...'
    },
    
    // Styling
    padding: {
      type: String,
      default: ''
    },
    
    minHeight: {
      type: String,
      default: ''
    },
    
    backgroundColor: {
      type: String,
      default: ''
    },
    
    // Animation
    animated: {
      type: Boolean,
      default: true
    },
    
    duration: {
      type: String,
      default: '0.75s'
    }
  },

  setup(props) {
    // Computed classes and styles
    const containerClasses = computed(() => [
      'loading-spinner',
      {
        'text-center': props.centered,
        'd-flex flex-column align-items-center justify-content-center': props.centered,
        'position-absolute w-100 h-100': props.overlay,
        'min-vh-100': props.fullHeight && !props.overlay,
        'py-5': !props.padding && props.centered,
        [`py-${props.size}`]: props.size !== 'default' && !props.padding
      }
    ])

    const containerStyle = computed(() => {
      const style = {}
      
      if (props.padding) {
        style.padding = props.padding
      }
      
      if (props.minHeight) {
        style.minHeight = props.minHeight
      }
      
      if (props.backgroundColor) {
        style.backgroundColor = props.backgroundColor
      }
      
      if (props.overlay) {
        style.top = '0'
        style.left = '0'
        style.zIndex = '1050'
        style.backgroundColor = style.backgroundColor || 'rgba(255, 255, 255, 0.8)'
      }
      
      return style
    })

    const spinnerClasses = computed(() => {
      const classes = [
        `spinner-${props.type}`,
        `text-${props.variant}`
      ]
      
      // Size classes
      switch (props.size) {
        case 'sm':
          classes.push(`spinner-${props.type}-sm`)
          break
        case 'lg':
          classes.push('spinner-lg')
          break
        case 'xl':
          classes.push('spinner-xl')
          break
      }
      
      return classes
    })

    const spinnerStyle = computed(() => {
      const style = {}
      
      if (!props.animated) {
        style.animation = 'none'
      } else if (props.duration !== '0.75s') {
        style.animationDuration = props.duration
      }
      
      // Custom sizes
      if (props.size === 'lg') {
        style.width = '3rem'
        style.height = '3rem'
      } else if (props.size === 'xl') {
        style.width = '4rem'
        style.height = '4rem'
      }
      
      return style
    })

    const messageClasses = computed(() => [
      'loading-message',
      'mt-3',
      CSS_CLASSES.TEXT_MUTED,
      {
        'fs-6': props.messageSize === 'sm',
        'fs-5': props.messageSize === 'lg',
        'fw-medium': props.messageSize !== 'sm'
      }
    ])

    const messageStyle = computed(() => {
      const style = {}
      
      if (props.size === 'sm') {
        style.fontSize = '0.875rem'
      } else if (props.size === 'lg') {
        style.fontSize = '1.125rem'
      }
      
      return style
    })

    const progressBarClasses = computed(() => [
      `bg-${props.progressVariant}`,
      {
        'progress-bar-striped': props.animated,
        'progress-bar-animated': props.animated
      }
    ])

    // Helper methods for parent components
    const show = () => {
      // Method to programmatically show spinner
      // Could be used with v-show or conditional rendering
      return true
    }

    const hide = () => {
      // Method to programmatically hide spinner
      return false
    }

    return {
      // Computed properties
      containerClasses,
      containerStyle,
      spinnerClasses,
      spinnerStyle,
      messageClasses,
      messageStyle,
      progressBarClasses,
      
      // Methods
      show,
      hide
    }
  }
}
</script>

<style scoped>
.loading-spinner {
  position: relative;
}

/* Custom spinner sizes */
.spinner-lg {
  width: 3rem;
  height: 3rem;
}

.spinner-xl {
  width: 4rem;
  height: 4rem;
}

/* Overlay styles */
.loading-spinner.position-absolute {
  backdrop-filter: blur(1px);
}

/* Animation customizations */
.loading-spinner .spinner-border,
.loading-spinner .spinner-grow {
  transition: all 0.2s ease-in-out;
}

/* Message styling */
.loading-message {
  max-width: 300px;
  line-height: 1.4;
}

/* Progress bar styling */
.loading-progress {
  width: 100%;
  max-width: 300px;
}

.loading-progress .progress {
  border-radius: 10px;
  overflow: hidden;
}

.loading-progress .progress-bar {
  transition: width 0.3s ease;
  border-radius: 10px;
}

/* Responsive adjustments */
@media (max-width: 576px) {
  .spinner-xl {
    width: 3rem;
    height: 3rem;
  }
  
  .loading-message {
    font-size: 0.9rem !important;
  }
  
  .loading-progress {
    max-width: 250px;
  }
}

/* Dark mode support */
@media (prefers-color-scheme: dark) {
  .loading-spinner.position-absolute {
    background-color: rgba(33, 37, 41, 0.8) !important;
  }
}

/* Accessibility improvements */
@media (prefers-reduced-motion: reduce) {
  .loading-spinner .spinner-border,
  .loading-spinner .spinner-grow,
  .loading-spinner .progress-bar {
    animation-duration: 2s !important;
  }
}

/* Focus management for accessibility */
.loading-spinner:focus-within {
  outline: 2px solid var(--bs-primary);
  outline-offset: 2px;
}
</style>