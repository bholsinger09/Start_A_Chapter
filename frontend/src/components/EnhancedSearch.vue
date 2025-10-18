<template>
  <div class="advanced-search-component">
    <!-- Enhanced Search Input with Suggestions -->
    <div class="search-input-container position-relative">
      <div class="input-group">
        <span class="input-group-text">
          <i class="bi bi-search"></i>
        </span>
        <input
          ref="searchInput"
          type="text"
          class="form-control"
          placeholder="Search chapters, universities, cities, or states..."
          v-model="searchQuery"
          @input="handleSearchInput"
          @keydown="handleKeydown"
          @focus="showSuggestions = true"
          @blur="hideSuggestionsDelayed"
          autocomplete="off"
        >
        <button 
          v-if="searchQuery"
          class="btn btn-outline-secondary"
          type="button"
          @click="clearSearch"
          title="Clear search"
        >
          <i class="bi bi-x-lg"></i>
        </button>
      </div>
      
      <!-- Search Suggestions Dropdown -->
      <div 
        v-if="showSuggestions && (filteredSuggestions.length > 0 || recentSearches.length > 0)"
        class="search-suggestions dropdown-menu show position-absolute w-100 mt-1"
        style="max-height: 300px; overflow-y: auto; z-index: 1050;"
        @mousedown.prevent
      >
        <!-- Quick Suggestions -->
        <div v-if="filteredSuggestions.length > 0">
          <h6 class="dropdown-header">
            <i class="bi bi-lightbulb me-2"></i>
            Suggestions
          </h6>
          <button
            v-for="(suggestion, index) in filteredSuggestions.slice(0, 8)"
            :key="`suggestion-${index}`"
            class="dropdown-item d-flex align-items-center"
            :class="{ 'active': selectedSuggestionIndex === index }"
            @click.stop="selectSuggestion(suggestion)"
            @mousedown.prevent
          >
            <i :class="getSuggestionIcon(suggestion.type)" class="me-2"></i>
            <span class="flex-grow-1">{{ suggestion.text }}</span>
            <small class="text-muted ms-2">{{ suggestion.type }}</small>
          </button>
        </div>
        
        <!-- Recent Searches -->
        <div v-if="recentSearches.length > 0">
          <hr class="dropdown-divider" v-if="filteredSuggestions.length > 0">
          <h6 class="dropdown-header">
            <i class="bi bi-clock-history me-2"></i>
            Recent Searches
          </h6>
          <button
            v-for="(recent, index) in recentSearches.slice(0, 5)"
            :key="`recent-${index}`"
            class="dropdown-item d-flex align-items-center"
            @click.stop="selectRecentSearch(recent)"
            @mousedown.prevent
          >
            <i class="bi bi-clock me-2"></i>
            <span class="flex-grow-1">{{ recent.query }}</span>
            <button
              class="btn btn-sm btn-link text-muted p-0 ms-2"
              @click.stop="removeRecentSearch(index)"
              title="Remove"
            >
              <i class="bi bi-x"></i>
            </button>
          </button>
        </div>
      </div>
    </div>

    <!-- Advanced Filters Toggle -->
    <div class="mt-3">
      <button
        class="btn btn-outline-secondary btn-sm"
        @click="showAdvancedFilters = !showAdvancedFilters"
        :class="{ 'active': showAdvancedFilters }"
      >
        <i class="bi bi-sliders me-2"></i>
        Advanced Filters
        <i :class="showAdvancedFilters ? 'bi bi-chevron-up' : 'bi bi-chevron-down'" class="ms-2"></i>
      </button>
    </div>

    <!-- Advanced Filters Panel -->
    <div v-if="showAdvancedFilters" class="advanced-filters mt-3">
      <div class="card">
        <div class="card-body">
          <div class="row g-3">
            <!-- State Filter -->
            <div class="col-md-6 col-lg-3">
              <label class="form-label fw-semibold">State</label>
              <select v-model="filters.state" class="form-select">
                <option value="">All States</option>
                <option v-for="state in availableStates" :key="state" :value="state">
                  {{ state }}
                </option>
              </select>
            </div>

            <!-- City Filter -->
            <div class="col-md-6 col-lg-3">
              <label class="form-label fw-semibold">City</label>
              <input 
                v-model="filters.city"
                type="text"
                class="form-control"
                placeholder="Enter city name"
              >
            </div>

            <!-- Health Score Range -->
            <div class="col-md-6 col-lg-3">
              <label class="form-label fw-semibold">
                Health Score: {{ filters.healthScoreMin }}-{{ filters.healthScoreMax }}
              </label>
              <div class="d-flex gap-2">
                <input
                  v-model.number="filters.healthScoreMin"
                  type="range"
                  class="form-range"
                  min="0"
                  max="100"
                  step="5"
                >
                <input
                  v-model.number="filters.healthScoreMax"
                  type="range"
                  class="form-range"
                  min="0"
                  max="100"
                  step="5"
                >
              </div>
            </div>

            <!-- Active Status -->
            <div class="col-md-6 col-lg-3">
              <label class="form-label fw-semibold">Status</label>
              <select v-model="filters.active" class="form-select">
                <option value="">All Chapters</option>
                <option value="true">Active Only</option>
                <option value="false">Inactive Only</option>
              </select>
            </div>

            <!-- Founded Date Range -->
            <div class="col-md-6 col-lg-3">
              <label class="form-label fw-semibold">Founded After</label>
              <input
                v-model="filters.foundedAfter"
                type="date"
                class="form-control"
              >
            </div>

            <!-- Member Count Range -->
            <div class="col-md-6 col-lg-3">
              <label class="form-label fw-semibold">
                Min Members: {{ filters.memberCountMin }}
              </label>
              <input
                v-model.number="filters.memberCountMin"
                type="range"
                class="form-range"
                min="0"
                max="500"
                step="10"
              >
            </div>
          </div>

          <!-- Filter Actions -->
          <div class="row mt-3">
            <div class="col-12">
              <div class="d-flex gap-2 flex-wrap">
                <button 
                  class="btn btn-primary btn-sm"
                  @click="applyFilters"
                >
                  <i class="bi bi-funnel me-1"></i>
                  Apply Filters
                </button>
                <button 
                  class="btn btn-outline-secondary btn-sm"
                  @click="clearAllFilters"
                >
                  <i class="bi bi-x-circle me-1"></i>
                  Clear All
                </button>
                <button 
                  class="btn btn-outline-info btn-sm"
                  @click="saveCurrentSearch"
                >
                  <i class="bi bi-bookmark me-1"></i>
                  Save Search
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Active Filters Display -->
    <div v-if="hasActiveFilters" class="active-filters mt-3">
      <div class="d-flex align-items-center flex-wrap gap-2">
        <span class="text-muted fw-semibold">Active filters:</span>
        <span
          v-for="filter in activeFiltersList"
          :key="filter.key"
          class="badge bg-primary d-flex align-items-center gap-1"
        >
          {{ filter.label }}
          <button
            class="btn-close btn-close-white"
            style="font-size: 0.7em;"
            @click="removeFilter(filter.key)"
            :title="`Remove ${filter.label}`"
          ></button>
        </span>
        <button
          class="btn btn-outline-danger btn-sm"
          @click="clearAllFilters"
        >
          Clear All
        </button>
      </div>
    </div>

    <!-- Saved Searches -->
    <div v-if="savedSearches.length > 0" class="saved-searches mt-3">
      <h6 class="fw-semibold mb-2">
        <i class="bi bi-bookmarks me-2"></i>
        Saved Searches
      </h6>
      <div class="d-flex flex-wrap gap-2">
        <button
          v-for="(search, index) in savedSearches"
          :key="`saved-${index}`"
          class="btn btn-outline-secondary btn-sm d-flex align-items-center gap-2"
          @click="loadSavedSearch(search)"
          :title="search.description"
        >
          <i class="bi bi-bookmark"></i>
          {{ search.name }}
          <button
            class="btn-close"
            style="font-size: 0.6em;"
            @click.stop="removeSavedSearch(index)"
          ></button>
        </button>
      </div>
    </div>

    <!-- Keyboard Shortcuts Help -->
    <div class="keyboard-shortcuts mt-2">
      <small class="text-muted">
        <i class="bi bi-keyboard me-1"></i>
        <strong>Shortcuts:</strong> 
        <kbd>Ctrl+K</kbd> Focus search • 
        <kbd>↑/↓</kbd> Navigate suggestions • 
        <kbd>Enter</kbd> Select • 
        <kbd>Esc</kbd> Close
      </small>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import searchService from '../services/searchService'

export default {
  name: 'EnhancedSearch',
  props: {
    chapters: {
      type: Array,
      default: () => []
    },
    availableStates: {
      type: Array,
      default: () => []
    }
  },
  emits: ['search', 'filter-change', 'search-results'],
  setup(props, { emit }) {
    // Reactive data
    const searchQuery = ref('')
    const showSuggestions = ref(false)
    const showAdvancedFilters = ref(false)
    const selectedSuggestionIndex = ref(-1)
    const searchInput = ref(null)
    const isSearching = ref(false)
    
    // Filters
    const filters = ref({
      state: '',
      city: '',
      healthScoreMin: 0,
      healthScoreMax: 100,
      memberCountMin: 0,
      active: '',
      foundedAfter: ''
    })

    // Recent searches and saved searches
    const recentSearches = ref([])
    const savedSearches = ref([])
    
    // Advanced search features
    const smartSuggestions = ref([])
    const recommendations = ref([])
    const trendingChapters = ref([])
    const searchResults = ref(null)

    // Debounced functions
    const debouncedSearch = ref(null)
    const debouncedSuggestions = ref(null)

    // Load saved data on mount
    onMounted(() => {
      loadStoredData()
      setupKeyboardShortcuts()
      setupDebouncedFunctions()
      loadRecommendations()
      loadTrendingChapters()
    })

    onUnmounted(() => {
      removeKeyboardShortcuts()
    })

    const setupDebouncedFunctions = () => {
      debouncedSearch.value = searchService.debounce(performBackendSearch, 500)
      debouncedSuggestions.value = searchService.debounce(fetchSmartSuggestions, 300)
    }

    const loadStoredData = () => {
      const recent = localStorage.getItem('recentSearches')
      const saved = localStorage.getItem('savedSearches')
      
      if (recent) {
        recentSearches.value = JSON.parse(recent)
      }
      if (saved) {
        savedSearches.value = JSON.parse(saved)
      }
    }

    // Fetch smart suggestions from backend
    const fetchSmartSuggestions = async () => {
      if (searchQuery.value.length < 2) {
        smartSuggestions.value = []
        return
      }

      try {
        const suggestions = await searchService.getSuggestions(searchQuery.value)
        smartSuggestions.value = suggestions.map(text => ({
          type: 'smart',
          text,
          value: text
        }))
      } catch (error) {
        console.error('Error fetching smart suggestions:', error)
        smartSuggestions.value = []
      }
    }

    // Load personalized recommendations
    const loadRecommendations = async () => {
      try {
        // TODO: Get actual member ID from auth context
        recommendations.value = await searchService.getRecommendations(null, 3)
      } catch (error) {
        console.error('Error loading recommendations:', error)
      }
    }

    // Load trending chapters
    const loadTrendingChapters = async () => {
      try {
        trendingChapters.value = await searchService.getTrendingChapters(5)
      } catch (error) {
        console.error('Error loading trending chapters:', error)
      }
    }

    // Perform backend search using our new search service
    const performBackendSearch = async () => {
      if (!searchQuery.value.trim() && !hasActiveFilters.value) {
        searchResults.value = null
        emit('search-results', null)
        return
      }

      isSearching.value = true
      try {
        // Convert our filters to the backend format
        const backendFilters = {
          states: filters.value.state ? [filters.value.state] : [],
          statuses: filters.value.active ? [filters.value.active === 'true' ? 'Active' : 'Inactive'] : [],
          minMembers: filters.value.memberCountMin > 0 ? filters.value.memberCountMin : null,
          maxMembers: null,
          minHealthScore: filters.value.healthScoreMin > 0 ? filters.value.healthScoreMin : null,
          maxHealthScore: filters.value.healthScoreMax < 100 ? filters.value.healthScoreMax : null
        }

        const results = await searchService.globalSearch(
          searchQuery.value.trim(),
          backendFilters,
          { page: 0, size: 20 }
        )
        
        searchResults.value = results
        emit('search-results', results)
        emit('search', {
          query: searchQuery.value,
          ...filters.value,
          results: results
        })
      } catch (error) {
        console.error('Error performing backend search:', error)
        // Fallback to local search for backward compatibility
        performLocalSearch()
      } finally {
        isSearching.value = false
      }
    }

    // Fallback local search (existing implementation)
    const performLocalSearch = () => {
      const searchParams = {
        query: searchQuery.value,
        ...filters.value
      }
      
      if (searchQuery.value) {
        addToRecentSearches(searchQuery.value)
      }
      
      emit('search', searchParams)
    }

    // Generate search suggestions (combining local and smart suggestions)
    const filteredSuggestions = computed(() => {
      if (!searchQuery.value || searchQuery.value.length < 2) {
        return smartSuggestions.value
      }

      const query = searchQuery.value.toLowerCase()
      const localSuggestions = []

      // Chapter name suggestions
      props.chapters.forEach(chapter => {
        if (chapter.name.toLowerCase().includes(query)) {
          localSuggestions.push({
            type: 'chapter',
            text: chapter.name,
            value: chapter.name
          })
        }
      })

      // University suggestions
      const universities = [...new Set(props.chapters.map(c => c.universityName))]
      universities.forEach(uni => {
        if (uni && uni.toLowerCase().includes(query)) {
          localSuggestions.push({
            type: 'university',
            text: uni,
            value: uni
          })
        }
      })

      // State suggestions
      props.availableStates.forEach(state => {
        if (state.toLowerCase().includes(query)) {
          localSuggestions.push({
            type: 'state',
            text: state,
            value: state
          })
        }
      })

      // City suggestions
      const cities = [...new Set(props.chapters.map(c => c.city))]
      cities.forEach(city => {
        if (city && city.toLowerCase().includes(query)) {
          localSuggestions.push({
            type: 'city',
            text: city,
            value: city
          })
        }
      })

      // Combine smart suggestions with local suggestions
      const combined = [...smartSuggestions.value, ...localSuggestions]
      
      // Remove duplicates and limit
      const unique = combined.filter((item, index, self) => 
        index === self.findIndex(t => t.text === item.text)
      )
      
      return unique.slice(0, 12)
    })

    const getSuggestionIcon = (type) => {
      const icons = {
        chapter: 'bi bi-building',
        university: 'bi bi-mortarboard',
        state: 'bi bi-geo-alt',
        city: 'bi bi-geo',
        smart: 'bi bi-lightbulb'
      }
      return icons[type] || 'bi bi-search'
    }

    // Active filters
    const hasActiveFilters = computed(() => {
      return searchQuery.value || 
             filters.value.state || 
             filters.value.city ||
             filters.value.healthScoreMin > 0 ||
             filters.value.healthScoreMax < 100 ||
             filters.value.memberCountMin > 0 ||
             filters.value.active ||
             filters.value.foundedAfter
    })

    const activeFiltersList = computed(() => {
      const list = []
      
      if (searchQuery.value) {
        list.push({ key: 'search', label: `Search: "${searchQuery.value}"` })
      }
      if (filters.value.state) {
        list.push({ key: 'state', label: `State: ${filters.value.state}` })
      }
      if (filters.value.city) {
        list.push({ key: 'city', label: `City: ${filters.value.city}` })
      }
      if (filters.value.healthScoreMin > 0 || filters.value.healthScoreMax < 100) {
        list.push({ 
          key: 'healthScore', 
          label: `Health: ${filters.value.healthScoreMin}-${filters.value.healthScoreMax}` 
        })
      }
      if (filters.value.memberCountMin > 0) {
        list.push({ key: 'memberCount', label: `Min Members: ${filters.value.memberCountMin}` })
      }
      if (filters.value.active) {
        list.push({ 
          key: 'active', 
          label: `Status: ${filters.value.active === 'true' ? 'Active' : 'Inactive'}` 
        })
      }
      if (filters.value.foundedAfter) {
        list.push({ key: 'foundedAfter', label: `Founded after: ${filters.value.foundedAfter}` })
      }
      
      return list
    })

    // Methods
    const handleSearchInput = () => {
      selectedSuggestionIndex.value = -1
      
      // Trigger smart suggestions
      if (debouncedSuggestions.value) {
        debouncedSuggestions.value()
      }
      
      // Trigger search
      if (debouncedSearch.value) {
        debouncedSearch.value()
      }
    }

    const handleKeydown = (event) => {
      // Handle Escape key regardless of suggestions state
      if (event.key === 'Escape') {
        event.preventDefault()
        showSuggestions.value = false
        selectedSuggestionIndex.value = -1
        searchInput.value?.blur()
        return
      }

      if (!showSuggestions.value) return

      const suggestionsLength = filteredSuggestions.value.length + recentSearches.value.length
      
      switch (event.key) {
        case 'ArrowDown':
          event.preventDefault()
          selectedSuggestionIndex.value = Math.min(
            selectedSuggestionIndex.value + 1, 
            suggestionsLength - 1
          )
          break
        case 'ArrowUp':
          event.preventDefault()
          selectedSuggestionIndex.value = Math.max(selectedSuggestionIndex.value - 1, -1)
          break
        case 'Enter':
          event.preventDefault()
          if (selectedSuggestionIndex.value >= 0) {
            if (selectedSuggestionIndex.value < filteredSuggestions.value.length) {
              selectSuggestion(filteredSuggestions.value[selectedSuggestionIndex.value])
            } else {
              const recentIndex = selectedSuggestionIndex.value - filteredSuggestions.value.length
              selectRecentSearch(recentSearches.value[recentIndex])
            }
          }
          break
      }
    }

    const selectSuggestion = (suggestion) => {
      searchQuery.value = suggestion.text
      showSuggestions.value = false
      selectedSuggestionIndex.value = -1
      addToRecentSearches(suggestion.text)
      performBackendSearch()
      // Keep focus on search input for better UX
      nextTick(() => {
        if (searchInput.value) {
          searchInput.value.focus()
        }
      })
    }

    const selectRecentSearch = (recent) => {
      searchQuery.value = recent.query
      showSuggestions.value = false
      selectedSuggestionIndex.value = -1
      performBackendSearch()
      // Keep focus on search input for better UX
      nextTick(() => {
        if (searchInput.value) {
          searchInput.value.focus()
        }
      })
    }

    const hideSuggestionsDelayed = () => {
      // Use a shorter delay and ensure we don't interfere with selection
      setTimeout(() => {
        showSuggestions.value = false
        selectedSuggestionIndex.value = -1
      }, 100)
    }

    const hideSuggestionsImmediately = () => {
      showSuggestions.value = false
      selectedSuggestionIndex.value = -1
    }

    const clearSearch = () => {
      searchQuery.value = ''
      performBackendSearch()
    }

    const applyFilters = () => {
      performBackendSearch()
      emit('filter-change', filters.value)
    }

    const clearAllFilters = () => {
      searchQuery.value = ''
      filters.value = {
        state: '',
        city: '',
        healthScoreMin: 0,
        healthScoreMax: 100,
        memberCountMin: 0,
        active: '',
        foundedAfter: ''
      }
      performBackendSearch()
    }

    const removeFilter = (filterKey) => {
      switch (filterKey) {
        case 'search':
          searchQuery.value = ''
          break
        case 'state':
          filters.value.state = ''
          break
        case 'city':
          filters.value.city = ''
          break
        case 'healthScore':
          filters.value.healthScoreMin = 0
          filters.value.healthScoreMax = 100
          break
        case 'memberCount':
          filters.value.memberCountMin = 0
          break
        case 'active':
          filters.value.active = ''
          break
        case 'foundedAfter':
          filters.value.foundedAfter = ''
          break
      }
      performBackendSearch()
    }

    const addToRecentSearches = (query) => {
      if (!query.trim()) return
      
      // Remove if already exists
      const index = recentSearches.value.findIndex(r => r.query === query)
      if (index >= 0) {
        recentSearches.value.splice(index, 1)
      }
      
      // Add to beginning
      recentSearches.value.unshift({
        query,
        timestamp: Date.now()
      })
      
      // Keep only last 10
      recentSearches.value = recentSearches.value.slice(0, 10)
      
      // Save to localStorage
      localStorage.setItem('recentSearches', JSON.stringify(recentSearches.value))
    }

    const removeRecentSearch = (index) => {
      recentSearches.value.splice(index, 1)
      localStorage.setItem('recentSearches', JSON.stringify(recentSearches.value))
    }

    const saveCurrentSearch = async () => {
      if (!hasActiveFilters.value) {
        alert('No active filters to save!')
        return
      }
      
      const name = prompt('Enter a name for this search:')
      if (name) {
        try {
          const searchParams = {
            query: searchQuery.value,
            filters: filters.value
          }
          
          // Save to backend (when implemented)
          await searchService.saveSearch(null, name, searchParams)
          
          // Also save locally for immediate use
          const search = {
            name,
            query: searchQuery.value,
            filters: { ...filters.value },
            timestamp: Date.now(),
            description: activeFiltersList.value.map(f => f.label).join(', ')
          }
          
          savedSearches.value.push(search)
          localStorage.setItem('savedSearches', JSON.stringify(savedSearches.value))
        } catch (error) {
          console.error('Error saving search:', error)
          alert('Failed to save search. Please try again.')
        }
      }
    }

    const loadSavedSearch = (search) => {
      searchQuery.value = search.query
      filters.value = { ...search.filters }
      performBackendSearch()
    }

    const removeSavedSearch = (index) => {
      savedSearches.value.splice(index, 1)
      localStorage.setItem('savedSearches', JSON.stringify(savedSearches.value))
    }

    // Keyboard shortcuts
    const setupKeyboardShortcuts = () => {
      document.addEventListener('keydown', globalKeyHandler)
    }

    const removeKeyboardShortcuts = () => {
      document.removeEventListener('keydown', globalKeyHandler)
    }

    const globalKeyHandler = (event) => {
      // Ctrl+K or Cmd+K to focus search
      if ((event.ctrlKey || event.metaKey) && event.key === 'k') {
        event.preventDefault()
        searchInput.value?.focus()
      }
      
      // Escape to close suggestions (global handler)
      if (event.key === 'Escape' && showSuggestions.value) {
        event.preventDefault()
        showSuggestions.value = false
        selectedSuggestionIndex.value = -1
        // Don't blur if the search input has focus, just close suggestions
        if (document.activeElement !== searchInput.value) {
          searchInput.value?.blur()
        }
      }
    }

    // Watch for filter changes
    watch(filters, () => {
      if (hasActiveFilters.value) {
        if (debouncedSearch.value) {
          debouncedSearch.value()
        }
      }
    }, { deep: true })

    return {
      searchQuery,
      showSuggestions,
      showAdvancedFilters,
      selectedSuggestionIndex,
      searchInput,
      isSearching,
      filters,
      recentSearches,
      savedSearches,
      smartSuggestions,
      recommendations,
      trendingChapters,
      searchResults,
      filteredSuggestions,
      hasActiveFilters,
      activeFiltersList,
      getSuggestionIcon,
      handleSearchInput,
      handleKeydown,
      selectSuggestion,
      selectRecentSearch,
      hideSuggestionsDelayed,
      hideSuggestionsImmediately,
      clearSearch,
      applyFilters,
      clearAllFilters,
      removeFilter,
      removeRecentSearch,
      saveCurrentSearch,
      loadSavedSearch,
      removeSavedSearch,
      performBackendSearch,
      loadRecommendations,
      loadTrendingChapters
    }
  }
}
</script>

<style scoped>
.search-input-container {
  position: relative;
}

.search-suggestions {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
  border: 1px solid var(--color-border);
}

.dropdown-item.active {
  background-color: var(--bs-primary);
  color: white;
}

.dropdown-item:not(.active):hover {
  background-color: var(--color-bg-secondary);
}

.badge {
  font-size: 0.8rem;
}

.btn-close-white {
  filter: invert(1) grayscale(100%) brightness(200%);
}

.keyboard-shortcuts kbd {
  font-size: 0.7em;
  padding: 0.1rem 0.3rem;
}

.advanced-filters .form-range {
  height: 1.2rem;
}

.form-label.fw-semibold {
  font-size: 0.9rem;
  margin-bottom: 0.3rem;
}

@media (max-width: 768px) {
  .advanced-filters .row .col-md-6 {
    margin-bottom: 1rem;
  }
  
  .d-flex.gap-2.flex-wrap button {
    margin-bottom: 0.5rem;
  }
}
</style>