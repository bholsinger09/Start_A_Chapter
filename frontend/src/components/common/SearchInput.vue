<!--
  SearchInput Component - Reusable Search Input Following Clean Code Principles
  Encapsulates search functionality with consistent UI/UX across the application
-->
<template>
  <div class="search-input-container" :class="containerClasses">
    <!-- Input Group -->
    <div class="input-group" :class="{ 'is-invalid': hasError }">
      <!-- Search Icon -->
      <span class="input-group-text" :class="iconClasses">
        <i :class="searchIcon" v-if="!isSearching"></i>
        <div class="spinner-border spinner-border-sm" role="status" v-else>
          <span class="visually-hidden">Searching...</span>
        </div>
      </span>

      <!-- Input Field -->
      <input
        ref="inputRef"
        type="text"
        :class="inputClasses"
        :placeholder="placeholder"
        :disabled="disabled || loading"
        :value="modelValue"
        @input="handleInput"
        @keydown.enter="handleEnter"
        @keydown.escape="handleEscape"
        @focus="handleFocus"
        @blur="handleBlur"
        :aria-label="ariaLabel"
        :aria-describedby="ariaDescribedBy"
      />

      <!-- Clear Button -->
      <button
        v-if="showClearButton && modelValue"
        type="button"
        class="btn btn-outline-secondary"
        :disabled="disabled || loading"
        @click="handleClear"
        :aria-label="clearButtonLabel"
      >
        <i class="bi bi-x"></i>
      </button>

      <!-- Search Button (optional) -->
      <button
        v-if="showSearchButton"
        type="button"
        :class="searchButtonClasses"
        :disabled="disabled || loading || !isValidSearch"
        @click="handleSearch"
        :aria-label="searchButtonLabel"
      >
        <i class="bi bi-search"></i>
        <span v-if="searchButtonText" class="ms-1">{{ searchButtonText }}</span>
      </button>
    </div>

    <!-- Search Stats/Feedback -->
    <div
      v-if="showStats && searchStats"
      class="search-stats mt-1"
      :class="statsClasses"
    >
      <small class="text-muted">
        <template v-if="searchStats.isFiltered">
          Showing {{ searchStats.filtered }} of {{ searchStats.total }} results
          <span v-if="searchStats.percentage < 100">
            ({{ searchStats.percentage }}%)
          </span>
        </template>
        <template v-else-if="searchStats.total > 0">
          {{ searchStats.total }} total items
        </template>
        <template v-else>
          No items found
        </template>
      </small>
    </div>

    <!-- Validation Error -->
    <div v-if="hasError" class="invalid-feedback d-block">
      {{ errorMessage }}
    </div>

    <!-- Help Text -->
    <div v-if="helpText && !hasError" class="form-text">
      {{ helpText }}
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, nextTick } from 'vue'
import { UI_CONFIG, CSS_CLASSES } from '@/constants'

export default {
  name: 'SearchInput',
  
  props: {
    // v-model support
    modelValue: {
      type: String,
      default: ''
    },
    
    // Configuration
    placeholder: {
      type: String,
      default: 'Search...'
    },
    
    disabled: {
      type: Boolean,
      default: false
    },
    
    loading: {
      type: Boolean,
      default: false
    },
    
    // Search behavior
    minLength: {
      type: Number,
      default: UI_CONFIG.MIN_SEARCH_LENGTH
    },
    
    debounceDelay: {
      type: Number,
      default: UI_CONFIG.DEBOUNCE_DELAY
    },
    
    // UI customization
    size: {
      type: String,
      default: 'default',
      validator: (value) => ['sm', 'default', 'lg'].includes(value)
    },
    
    variant: {
      type: String,
      default: 'default',
      validator: (value) => ['default', 'outline', 'flush'].includes(value)
    },
    
    // Features
    showClearButton: {
      type: Boolean,
      default: true
    },
    
    showSearchButton: {
      type: Boolean,
      default: false
    },
    
    showStats: {
      type: Boolean,
      default: false
    },
    
    // Button configuration
    searchButtonText: {
      type: String,
      default: ''
    },
    
    searchButtonVariant: {
      type: String,
      default: 'primary'
    },
    
    // Validation
    error: {
      type: String,
      default: ''
    },
    
    // Help and accessibility
    helpText: {
      type: String,
      default: ''
    },
    
    ariaLabel: {
      type: String,
      default: 'Search input'
    },
    
    // Search statistics
    searchStats: {
      type: Object,
      default: null
    },
    
    // Auto-focus
    autofocus: {
      type: Boolean,
      default: false
    },
    
    // Immediate search (no debounce)
    immediate: {
      type: Boolean,
      default: false
    }
  },

  emits: [
    'update:modelValue',
    'search',
    'clear',
    'focus',
    'blur',
    'enter'
  ],

  setup(props, { emit }) {
    // Template refs
    const inputRef = ref(null)
    
    // Internal state
    const isFocused = ref(false)
    const isSearching = ref(false)
    const debounceTimer = ref(null)

    // Computed properties
    const hasError = computed(() => Boolean(props.error))
    
    const errorMessage = computed(() => props.error)
    
    const isValidSearch = computed(() => {
      return props.modelValue.length === 0 || props.modelValue.length >= props.minLength
    })

    const containerClasses = computed(() => [
      'search-input',
      `search-input--${props.size}`,
      `search-input--${props.variant}`,
      {
        'search-input--focused': isFocused.value,
        'search-input--loading': props.loading,
        'search-input--disabled': props.disabled,
        'search-input--error': hasError.value
      }
    ])

    const inputClasses = computed(() => [
      CSS_CLASSES.FORM_CONTROL,
      {
        [`form-control-${props.size}`]: props.size !== 'default',
        'is-invalid': hasError.value
      }
    ])

    const iconClasses = computed(() => [
      {
        'text-primary': isFocused.value && !hasError.value,
        'text-danger': hasError.value
      }
    ])

    const searchIcon = computed(() => {
      if (props.loading || isSearching.value) {
        return 'bi bi-hourglass-split'
      }
      return 'bi bi-search'
    })

    const searchButtonClasses = computed(() => [
      'btn',
      `btn-${props.searchButtonVariant}`,
      {
        [`btn-${props.size}`]: props.size !== 'default'
      }
    ])

    const statsClasses = computed(() => ({
      'text-success': props.searchStats?.hasResults,
      'text-muted': !props.searchStats?.hasResults
    }))

    const clearButtonLabel = computed(() => `Clear search: ${props.modelValue}`)
    
    const searchButtonLabel = computed(() => {
      return props.searchButtonText || `Search for: ${props.modelValue}`
    })

    const ariaDescribedBy = computed(() => {
      const parts = []
      if (props.helpText) parts.push('search-help')
      if (hasError.value) parts.push('search-error')
      if (props.searchStats) parts.push('search-stats')
      return parts.join(' ') || undefined
    })

    // Methods
    const handleInput = (event) => {
      const value = event.target.value
      emit('update:modelValue', value)

      // Handle immediate vs debounced search
      if (props.immediate) {
        handleSearch(value)
      } else {
        handleDebouncedSearch(value)
      }
    }

    const handleDebouncedSearch = (value) => {
      // Clear existing timer
      if (debounceTimer.value) {
        clearTimeout(debounceTimer.value)
      }

      // Set searching state for long searches
      if (value && value.length >= props.minLength) {
        isSearching.value = true
      }

      // Create new debounced search
      debounceTimer.value = setTimeout(() => {
        isSearching.value = false
        if (value.length >= props.minLength || value.length === 0) {
          emit('search', value)
        }
      }, props.debounceDelay)
    }

    const handleSearch = (value = props.modelValue) => {
      if (isValidSearch.value) {
        emit('search', value)
      }
    }

    const handleClear = () => {
      emit('update:modelValue', '')
      emit('clear')
      emit('search', '')
      
      // Clear debounce timer
      if (debounceTimer.value) {
        clearTimeout(debounceTimer.value)
      }
      
      isSearching.value = false
      
      // Focus input after clear
      nextTick(() => {
        inputRef.value?.focus()
      })
    }

    const handleEnter = () => {
      emit('enter', props.modelValue)
      handleSearch()
    }

    const handleEscape = () => {
      if (props.modelValue) {
        handleClear()
      } else {
        inputRef.value?.blur()
      }
    }

    const handleFocus = () => {
      isFocused.value = true
      emit('focus')
    }

    const handleBlur = () => {
      isFocused.value = false
      emit('blur')
    }

    // Public methods for parent components
    const focus = () => {
      inputRef.value?.focus()
    }

    const blur = () => {
      inputRef.value?.blur()
    }

    const select = () => {
      inputRef.value?.select()
    }

    // Lifecycle
    watch(() => props.autofocus, (shouldFocus) => {
      if (shouldFocus) {
        nextTick(() => {
          focus()
        })
      }
    }, { immediate: true })

    // Cleanup on unmount
    const cleanup = () => {
      if (debounceTimer.value) {
        clearTimeout(debounceTimer.value)
      }
    }

    return {
      // Template refs
      inputRef,
      
      // Reactive state
      isFocused,
      isSearching,
      
      // Computed properties
      hasError,
      errorMessage,
      isValidSearch,
      containerClasses,
      inputClasses,
      iconClasses,
      searchIcon,
      searchButtonClasses,
      statsClasses,
      clearButtonLabel,
      searchButtonLabel,
      ariaDescribedBy,
      
      // Event handlers
      handleInput,
      handleSearch,
      handleClear,
      handleEnter,
      handleEscape,
      handleFocus,
      handleBlur,
      
      // Public methods
      focus,
      blur,
      select,
      cleanup
    }
  }
}
</script>

<style scoped>
.search-input-container {
  position: relative;
}

.search-input--sm .input-group-text {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

.search-input--lg .input-group-text {
  padding: 0.5rem 1rem;
  font-size: 1.25rem;
}

.search-input--flush .input-group .form-control {
  border-left: 0;
  border-right: 0;
  border-radius: 0;
}

.search-input--flush .input-group .input-group-text {
  border-left: 0;
  border-right: 0;
  border-radius: 0;
}

.input-group-text {
  transition: color 0.15s ease-in-out, border-color 0.15s ease-in-out;
}

.search-input--focused .input-group-text {
  border-color: #86b7fe;
}

.search-input--error .input-group-text {
  border-color: #dc3545;
}

.search-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.spinner-border-sm {
  width: 0.875rem;
  height: 0.875rem;
}

/* Animation for search icon transitions */
.search-input .input-group-text i,
.search-input .input-group-text .spinner-border {
  transition: all 0.2s ease-in-out;
}

/* Hover effects */
.search-input .btn:hover {
  transform: translateY(-1px);
}

/* Focus styles */
.search-input--focused {
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}

/* Responsive design */
@media (max-width: 576px) {
  .search-input--lg .input-group-text {
    padding: 0.375rem 0.75rem;
    font-size: 1rem;
  }
  
  .search-stats {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>