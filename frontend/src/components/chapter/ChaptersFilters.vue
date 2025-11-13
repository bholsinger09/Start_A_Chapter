<!--
  ChaptersFilters Component - Focused Search and Filter Component
  Single Responsibility: Handle search and filtering UI
-->
<template>
  <div class="chapters-filters">
    <div class="row mb-4">
      <!-- Search Input -->
      <div class="col-md-6">
        <SearchInput
          :model-value="searchTerm"
          @update:model-value="handleSearchUpdate"
          @search="handleSearch"
          placeholder="Search chapters by name, university, or location..."
          :search-stats="searchStats"
          :show-stats="true"
          size="default"
          :debounce-delay="300"
        />
      </div>

      <!-- State Filter -->
      <div class="col-md-3">
        <div class="filter-group">
          <label for="state-filter" class="form-label visually-hidden">
            Filter by state
          </label>
          <select
            id="state-filter"
            class="form-select"
            :value="selectedState"
            @change="handleStateChange"
            :disabled="loading"
          >
            <option value="">All States</option>
            <option
              v-for="state in availableStates"
              :key="state"
              :value="state"
            >
              {{ state }}
            </option>
          </select>
        </div>
      </div>

      <!-- Sort Controls -->
      <div class="col-md-3">
        <div class="sort-controls">
          <div class="btn-group" role="group" aria-label="Sort chapters">
            <select
              class="form-select sort-select"
              :value="sortBy"
              @change="handleSortChange"
              :disabled="loading"
              aria-label="Sort by"
            >
              <option value="name">Sort by Name</option>
              <option value="universityName">Sort by University</option>
              <option value="state">Sort by State</option>
              <option value="memberCount">Sort by Members</option>
              <option value="createdAt">Sort by Date</option>
            </select>
            
            <button
              type="button"
              class="btn btn-outline-secondary sort-order-btn"
              @click="toggleSortOrder"
              :disabled="loading"
              :title="sortOrderTitle"
              :aria-label="sortOrderLabel"
            >
              <i :class="sortOrderIcon"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Active Filters Display -->
    <div v-if="hasActiveFilters" class="active-filters mb-3">
      <div class="d-flex align-items-center gap-2 flex-wrap">
        <small class="text-muted">Active filters:</small>
        
        <span
          v-if="searchTerm"
          class="badge bg-primary filter-badge"
        >
          Search: "{{ searchTerm }}"
          <button
            type="button"
            class="btn-close btn-close-white ms-1"
            @click="clearSearch"
            aria-label="Clear search"
          ></button>
        </span>
        
        <span
          v-if="selectedState"
          class="badge bg-info filter-badge"
        >
          State: {{ selectedState }}
          <button
            type="button"
            class="btn-close btn-close-white ms-1"
            @click="clearStateFilter"
            aria-label="Clear state filter"
          ></button>
        </span>
        
        <button
          type="button"
          class="btn btn-sm btn-outline-secondary"
          @click="clearAllFilters"
        >
          <i class="bi bi-x-circle me-1"></i>
          Clear All
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import SearchInput from '@/components/common/SearchInput.vue'

export default {
  name: 'ChaptersFilters',
  
  components: {
    SearchInput
  },

  props: {
    searchTerm: {
      type: String,
      default: ''
    },
    
    selectedState: {
      type: String,
      default: ''
    },
    
    availableStates: {
      type: Array,
      default: () => []
    },
    
    sortBy: {
      type: String,
      default: 'name'
    },
    
    sortOrder: {
      type: String,
      default: 'asc'
    },
    
    searchStats: {
      type: Object,
      default: null
    },
    
    loading: {
      type: Boolean,
      default: false
    }
  },

  emits: [
    'update:searchTerm',
    'update:selectedState',
    'update:sortBy',
    'update:sortOrder',
    'search',
    'filter-change',
    'sort-change'
  ],

  setup(props, { emit }) {
    // Computed properties
    const hasActiveFilters = computed(() => {
      return props.searchTerm || props.selectedState
    })

    const sortOrderIcon = computed(() => {
      return props.sortOrder === 'asc' ? 'bi bi-sort-alpha-down' : 'bi bi-sort-alpha-up'
    })

    const sortOrderTitle = computed(() => {
      return `Currently sorting ${props.sortOrder === 'asc' ? 'ascending' : 'descending'}. Click to toggle.`
    })

    const sortOrderLabel = computed(() => {
      return `Toggle sort order (currently ${props.sortOrder === 'asc' ? 'A-Z' : 'Z-A'})`
    })

    // Event handlers
    const handleSearchUpdate = (value) => {
      emit('update:searchTerm', value)
    }

    const handleSearch = (term) => {
      emit('search', term)
      emit('filter-change')
    }

    const handleStateChange = (event) => {
      const value = event.target.value
      emit('update:selectedState', value)
      emit('filter-change')
    }

    const handleSortChange = (event) => {
      const value = event.target.value
      emit('update:sortBy', value)
      emit('sort-change', { sortBy: value, sortOrder: props.sortOrder })
    }

    const toggleSortOrder = () => {
      const newOrder = props.sortOrder === 'asc' ? 'desc' : 'asc'
      emit('update:sortOrder', newOrder)
      emit('sort-change', { sortBy: props.sortBy, sortOrder: newOrder })
    }

    const clearSearch = () => {
      emit('update:searchTerm', '')
      emit('search', '')
      emit('filter-change')
    }

    const clearStateFilter = () => {
      emit('update:selectedState', '')
      emit('filter-change')
    }

    const clearAllFilters = () => {
      clearSearch()
      clearStateFilter()
    }

    return {
      // Computed properties
      hasActiveFilters,
      sortOrderIcon,
      sortOrderTitle,
      sortOrderLabel,
      
      // Event handlers
      handleSearchUpdate,
      handleSearch,
      handleStateChange,
      handleSortChange,
      toggleSortOrder,
      clearSearch,
      clearStateFilter,
      clearAllFilters
    }
  }
}
</script>

<style scoped>
.chapters-filters {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 0.5rem;
  margin-bottom: 2rem;
}

.filter-group {
  position: relative;
}

.sort-controls .btn-group {
  width: 100%;
}

.sort-select {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  flex: 1;
}

.sort-order-btn {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-left: 0;
  width: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sort-order-btn:hover {
  background-color: var(--bs-secondary);
  color: white;
}

.active-filters {
  padding-top: 1rem;
  border-top: 1px solid #dee2e6;
}

.filter-badge {
  display: inline-flex;
  align-items: center;
  font-size: 0.875rem;
  padding: 0.5rem 0.75rem;
  border-radius: 1rem;
}

.filter-badge .btn-close {
  width: 0.5rem;
  height: 0.5rem;
  opacity: 0.8;
}

.filter-badge .btn-close:hover {
  opacity: 1;
}

/* Responsive design */
@media (max-width: 768px) {
  .chapters-filters .row > div {
    margin-bottom: 1rem;
  }
  
  .chapters-filters .row > div:last-child {
    margin-bottom: 0;
  }
  
  .sort-controls .btn-group {
    display: flex;
  }
  
  .active-filters .d-flex {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 0.5rem !important;
  }
  
  .filter-badge {
    font-size: 0.8rem;
  }
}

@media (max-width: 576px) {
  .chapters-filters {
    padding: 1rem;
  }
  
  .sort-order-btn {
    width: 44px;
  }
  
  .active-filters small {
    margin-bottom: 0.5rem;
    display: block;
  }
}

/* Focus and hover states */
.form-select:focus,
.sort-order-btn:focus {
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

.sort-order-btn {
  transition: all 0.15s ease-in-out;
}

/* Animation for filter badges */
.filter-badge {
  animation: slideIn 0.2s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>