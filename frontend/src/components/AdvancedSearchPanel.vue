<template>
  <div class="advanced-search-panel">
    <!-- Search Header -->
    <div class="search-header">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="mb-0">
          <i class="bi bi-funnel me-2"></i>
          Advanced Search & Filters
        </h5>
        <button 
          class="btn btn-outline-secondary btn-sm"
          @click="resetFilters"
          :disabled="!hasActiveFilters"
        >
          <i class="bi bi-arrow-clockwise me-1"></i>
          Reset
        </button>
      </div>
    </div>

    <!-- Main Search -->
    <div class="search-input-wrapper mb-4">
      <div class="input-group input-group-lg">
        <span class="input-group-text">
          <i class="bi bi-search"></i>
        </span>
        <input
          ref="mainSearchInput"
          type="text"
          class="form-control"
          placeholder="Search across all content..."
          v-model="searchQuery"
          @input="performSearch"
          @keyup.enter="performSearch"
        >
        <button 
          v-if="searchQuery"
          class="btn btn-outline-secondary"
          type="button"
          @click="clearMainSearch"
        >
          <i class="bi bi-x-lg"></i>
        </button>
      </div>
      
      <!-- Quick Suggestions -->
      <div v-if="showSuggestions && suggestions.length" class="suggestions-dropdown">
        <div 
          v-for="(suggestion, index) in suggestions" 
          :key="index"
          class="suggestion-item"
          :class="{ active: selectedSuggestionIndex === index }"
          @click="selectSuggestion(suggestion)"
          @mouseenter="selectedSuggestionIndex = index"
        >
          <i class="bi" :class="suggestion.icon"></i>
          <span class="suggestion-text">{{ suggestion.text }}</span>
          <span class="suggestion-category">{{ suggestion.category }}</span>
        </div>
      </div>
    </div>

    <!-- Filter Tabs -->
    <div class="filter-tabs mb-4">
      <ul class="nav nav-pills nav-fill" role="tablist">
        <li class="nav-item" role="presentation">
          <button 
            class="nav-link"
            :class="{ active: activeTab === 'members' }"
            @click="activeTab = 'members'"
            type="button"
          >
            <i class="bi bi-people me-2"></i>
            Members
            <span v-if="memberFilters.active" class="badge bg-primary ms-2">{{ memberFilters.count }}</span>
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button 
            class="nav-link"
            :class="{ active: activeTab === 'events' }"
            @click="activeTab = 'events'"
            type="button"
          >
            <i class="bi bi-calendar-event me-2"></i>
            Events
            <span v-if="eventFilters.active" class="badge bg-primary ms-2">{{ eventFilters.count }}</span>
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button 
            class="nav-link"
            :class="{ active: activeTab === 'chapters' }"
            @click="activeTab = 'chapters'"
            type="button"
          >
            <i class="bi bi-building me-2"></i>
            Chapters
            <span v-if="chapterFilters.active" class="badge bg-primary ms-2">{{ chapterFilters.count }}</span>
          </button>
        </li>
      </ul>
    </div>

    <!-- Member Filters -->
    <div v-show="activeTab === 'members'" class="filter-section">
      <div class="row g-3">
        <div class="col-md-4">
          <label class="form-label fw-semibold">Role</label>
          <select class="form-select" v-model="memberFilters.role" @change="applyFilters">
            <option value="">All Roles</option>
            <option value="PRESIDENT">President</option>
            <option value="VICE_PRESIDENT">Vice President</option>
            <option value="SECRETARY">Secretary</option>
            <option value="TREASURER">Treasurer</option>
            <option value="MEMBER">Member</option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label fw-semibold">Chapter</label>
          <select class="form-select" v-model="memberFilters.chapter" @change="applyFilters">
            <option value="">All Chapters</option>
            <option v-for="chapter in chapters" :key="chapter.id" :value="chapter.id">
              {{ chapter.name }}
            </option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label fw-semibold">Status</label>
          <select class="form-select" v-model="memberFilters.active" @change="applyFilters">
            <option value="">All</option>
            <option value="true">Active</option>
            <option value="false">Inactive</option>
          </select>
        </div>
        <div class="col-md-6">
          <label class="form-label fw-semibold">Join Date Range</label>
          <div class="input-group">
            <input 
              type="date" 
              class="form-control" 
              v-model="memberFilters.joinDateFrom"
              @change="applyFilters"
            >
            <span class="input-group-text">to</span>
            <input 
              type="date" 
              class="form-control" 
              v-model="memberFilters.joinDateTo"
              @change="applyFilters"
            >
          </div>
        </div>
        <div class="col-md-6">
          <label class="form-label fw-semibold">Tags</label>
          <div class="tag-input-container">
            <div class="selected-tags">
              <span 
                v-for="tag in memberFilters.tags" 
                :key="tag"
                class="badge bg-secondary me-1 mb-1"
              >
                {{ tag }}
                <button 
                  type="button" 
                  class="btn-close btn-close-white ms-1" 
                  @click="removeTag('member', tag)"
                  style="font-size: 0.6rem;"
                ></button>
              </span>
            </div>
            <input
              type="text"
              class="form-control"
              placeholder="Add tags..."
              @keyup.enter="addTag('member', $event.target.value); $event.target.value = ''"
            >
          </div>
        </div>
      </div>
    </div>

    <!-- Event Filters -->
    <div v-show="activeTab === 'events'" class="filter-section">
      <div class="row g-3">
        <div class="col-md-3">
          <label class="form-label fw-semibold">Event Type</label>
          <select class="form-select" v-model="eventFilters.type" @change="applyFilters">
            <option value="">All Types</option>
            <option value="MEETING">Meeting</option>
            <option value="SOCIAL">Social</option>
            <option value="COMMUNITY_SERVICE">Community Service</option>
            <option value="FUNDRAISER">Fundraiser</option>
            <option value="WORKSHOP">Workshop</option>
          </select>
        </div>
        <div class="col-md-3">
          <label class="form-label fw-semibold">Status</label>
          <select class="form-select" v-model="eventFilters.status" @change="applyFilters">
            <option value="">All Status</option>
            <option value="UPCOMING">Upcoming</option>
            <option value="ONGOING">Ongoing</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>
        <div class="col-md-6">
          <label class="form-label fw-semibold">Date Range</label>
          <div class="input-group">
            <input 
              type="datetime-local" 
              class="form-control" 
              v-model="eventFilters.dateFrom"
              @change="applyFilters"
            >
            <span class="input-group-text">to</span>
            <input 
              type="datetime-local" 
              class="form-control" 
              v-model="eventFilters.dateTo"
              @change="applyFilters"
            >
          </div>
        </div>
        <div class="col-md-6">
          <label class="form-label fw-semibold">Attendance Range</label>
          <div class="range-slider">
            <input 
              type="range" 
              class="form-range" 
              v-model="eventFilters.minAttendance"
              :min="0" 
              :max="500"
              @input="applyFilters"
            >
            <div class="range-labels d-flex justify-content-between">
              <small>{{ eventFilters.minAttendance }}+</small>
              <small>500</small>
            </div>
          </div>
        </div>
        <div class="col-md-6">
          <label class="form-label fw-semibold">Chapter</label>
          <select class="form-select" v-model="eventFilters.chapter" @change="applyFilters">
            <option value="">All Chapters</option>
            <option v-for="chapter in chapters" :key="chapter.id" :value="chapter.id">
              {{ chapter.name }}
            </option>
          </select>
        </div>
      </div>
    </div>

    <!-- Chapter Filters -->
    <div v-show="activeTab === 'chapters'" class="filter-section">
      <div class="row g-3">
        <div class="col-md-4">
          <label class="form-label fw-semibold">Institution Type</label>
          <select class="form-select" v-model="chapterFilters.institutionType" @change="applyFilters">
            <option value="">All Types</option>
            <option value="UNIVERSITY">University</option>
            <option value="CHURCH">Church</option>
            <option value="COMMUNITY">Community</option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label fw-semibold">State</label>
          <select class="form-select" v-model="chapterFilters.state" @change="applyFilters">
            <option value="">All States</option>
            <option v-for="state in states" :key="state" :value="state">
              {{ state }}
            </option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label fw-semibold">Member Count Range</label>
          <div class="range-slider">
            <input 
              type="range" 
              class="form-range" 
              v-model="chapterFilters.minMembers"
              :min="0" 
              :max="200"
              @input="applyFilters"
            >
            <div class="range-labels d-flex justify-content-between">
              <small>{{ chapterFilters.minMembers }}+</small>
              <small>200</small>
            </div>
          </div>
        </div>
        <div class="col-12">
          <label class="form-label fw-semibold">Active Filters</label>
          <div class="active-filters">
            <span 
              v-for="filter in activeFilters" 
              :key="filter.key"
              class="badge bg-primary me-2 mb-2"
            >
              {{ filter.label }}: {{ filter.value }}
              <button 
                type="button" 
                class="btn-close btn-close-white ms-1" 
                @click="removeFilter(filter.key)"
                style="font-size: 0.6rem;"
              ></button>
            </span>
            <span v-if="!activeFilters.length" class="text-muted">No active filters</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Search Results Summary -->
    <div v-if="searchResults.length || searchQuery" class="search-results-summary mt-4 p-3 bg-light rounded">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <strong>{{ searchResults.length }}</strong> 
          <span class="text-muted">results found</span>
          <span v-if="searchQuery" class="text-muted">for "{{ searchQuery }}"</span>
        </div>
        <div class="search-actions">
          <button class="btn btn-sm btn-outline-primary me-2" @click="exportResults">
            <i class="bi bi-download me-1"></i>
            Export
          </button>
          <button class="btn btn-sm btn-outline-secondary" @click="saveSearch">
            <i class="bi bi-bookmark me-1"></i>
            Save Search
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'AdvancedSearchPanel',
  emits: ['search-results', 'filters-changed'],
  setup(props, { emit }) {
    const router = useRouter()
    
    // Main search
    const searchQuery = ref('')
    const showSuggestions = ref(false)
    const suggestions = ref([])
    const selectedSuggestionIndex = ref(-1)
    const mainSearchInput = ref(null)
    
    // Tabs
    const activeTab = ref('members')
    
    // Sample data
    const chapters = ref([
      { id: 1, name: 'University of California - Berkeley' },
      { id: 2, name: 'Stanford University' },
      { id: 3, name: 'MIT' }
    ])
    
    const states = ref(['CA', 'NY', 'TX', 'FL', 'IL', 'PA', 'OH', 'GA', 'NC', 'MI'])
    
    // Filter states
    const memberFilters = reactive({
      role: '',
      chapter: '',
      active: '',
      joinDateFrom: '',
      joinDateTo: '',
      tags: [],
      active: false,
      count: 0
    })
    
    const eventFilters = reactive({
      type: '',
      status: '',
      dateFrom: '',
      dateTo: '',
      minAttendance: 0,
      chapter: '',
      active: false,
      count: 0
    })
    
    const chapterFilters = reactive({
      institutionType: '',
      state: '',
      minMembers: 0,
      active: false,
      count: 0
    })
    
    const searchResults = ref([])
    
    // Computed properties
    const hasActiveFilters = computed(() => {
      return memberFilters.active || eventFilters.active || chapterFilters.active
    })
    
    const activeFilters = computed(() => {
      const filters = []
      
      if (activeTab.value === 'members') {
        if (memberFilters.role) filters.push({ key: 'memberRole', label: 'Role', value: memberFilters.role })
        if (memberFilters.chapter) filters.push({ key: 'memberChapter', label: 'Chapter', value: getChapterName(memberFilters.chapter) })
        if (memberFilters.active) filters.push({ key: 'memberActive', label: 'Status', value: memberFilters.active === 'true' ? 'Active' : 'Inactive' })
      } else if (activeTab.value === 'events') {
        if (eventFilters.type) filters.push({ key: 'eventType', label: 'Type', value: eventFilters.type })
        if (eventFilters.status) filters.push({ key: 'eventStatus', label: 'Status', value: eventFilters.status })
      } else if (activeTab.value === 'chapters') {
        if (chapterFilters.institutionType) filters.push({ key: 'institutionType', label: 'Type', value: chapterFilters.institutionType })
        if (chapterFilters.state) filters.push({ key: 'chapterState', label: 'State', value: chapterFilters.state })
      }
      
      return filters
    })
    
    // Methods
    const performSearch = async () => {
      // Implement search logic
      console.log('Performing search:', searchQuery.value)
      console.log('Active filters:', activeFilters.value)
      
      // Emit search event
      emit('search-results', {
        query: searchQuery.value,
        filters: getCurrentFilters(),
        tab: activeTab.value
      })
    }
    
    const applyFilters = () => {
      updateFilterCounts()
      performSearch()
    }
    
    const updateFilterCounts = () => {
      // Update filter counts (mock implementation)
      memberFilters.count = activeFilters.value.filter(f => f.key.startsWith('member')).length
      eventFilters.count = activeFilters.value.filter(f => f.key.startsWith('event')).length
      chapterFilters.count = activeFilters.value.filter(f => f.key.startsWith('chapter')).length
      
      memberFilters.active = memberFilters.count > 0
      eventFilters.active = eventFilters.count > 0
      chapterFilters.active = chapterFilters.count > 0
    }
    
    const getCurrentFilters = () => {
      if (activeTab.value === 'members') return { ...memberFilters }
      if (activeTab.value === 'events') return { ...eventFilters }
      if (activeTab.value === 'chapters') return { ...chapterFilters }
      return {}
    }
    
    const resetFilters = () => {
      Object.keys(memberFilters).forEach(key => {
        if (Array.isArray(memberFilters[key])) {
          memberFilters[key] = []
        } else if (typeof memberFilters[key] === 'boolean') {
          memberFilters[key] = false
        } else {
          memberFilters[key] = ''
        }
      })
      
      Object.keys(eventFilters).forEach(key => {
        if (typeof eventFilters[key] === 'boolean') {
          eventFilters[key] = false
        } else if (typeof eventFilters[key] === 'number') {
          eventFilters[key] = 0
        } else {
          eventFilters[key] = ''
        }
      })
      
      Object.keys(chapterFilters).forEach(key => {
        if (typeof chapterFilters[key] === 'boolean') {
          chapterFilters[key] = false
        } else if (typeof chapterFilters[key] === 'number') {
          chapterFilters[key] = 0
        } else {
          chapterFilters[key] = ''
        }
      })
      
      searchQuery.value = ''
      applyFilters()
    }
    
    const clearMainSearch = () => {
      searchQuery.value = ''
      showSuggestions.value = false
      performSearch()
    }
    
    const addTag = (type, value) => {
      if (!value.trim()) return
      
      if (type === 'member' && !memberFilters.tags.includes(value.trim())) {
        memberFilters.tags.push(value.trim())
        applyFilters()
      }
    }
    
    const removeTag = (type, tag) => {
      if (type === 'member') {
        const index = memberFilters.tags.indexOf(tag)
        if (index > -1) {
          memberFilters.tags.splice(index, 1)
          applyFilters()
        }
      }
    }
    
    const removeFilter = (filterKey) => {
      // Remove specific filter
      console.log('Removing filter:', filterKey)
      applyFilters()
    }
    
    const getChapterName = (chapterId) => {
      const chapter = chapters.value.find(c => c.id == chapterId)
      return chapter ? chapter.name : chapterId
    }
    
    const exportResults = () => {
      // Implement export functionality
      console.log('Exporting results...')
    }
    
    const saveSearch = () => {
      // Implement save search functionality
      console.log('Saving search...')
    }
    
    const selectSuggestion = (suggestion) => {
      searchQuery.value = suggestion.text
      showSuggestions.value = false
      performSearch()
    }
    
    onMounted(() => {
      // Initialize
      updateFilterCounts()
    })
    
    return {
      // Template refs
      mainSearchInput,
      
      // Reactive data
      searchQuery,
      showSuggestions,
      suggestions,
      selectedSuggestionIndex,
      activeTab,
      memberFilters,
      eventFilters,
      chapterFilters,
      searchResults,
      chapters,
      states,
      
      // Computed
      hasActiveFilters,
      activeFilters,
      
      // Methods
      performSearch,
      applyFilters,
      resetFilters,
      clearMainSearch,
      addTag,
      removeTag,
      removeFilter,
      exportResults,
      saveSearch,
      selectSuggestion
    }
  }
}
</script>

<style scoped>
.advanced-search-panel {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  margin-bottom: 2rem;
}

.search-input-wrapper {
  position: relative;
}

.suggestions-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  max-height: 300px;
  overflow-y: auto;
}

.suggestion-item {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #f8f9fa;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: background-color 0.2s;
}

.suggestion-item:hover,
.suggestion-item.active {
  background-color: #f8f9fa;
}

.suggestion-item i {
  margin-right: 0.75rem;
  color: #6c757d;
}

.suggestion-text {
  flex-grow: 1;
}

.suggestion-category {
  font-size: 0.8rem;
  color: #6c757d;
  background: #e9ecef;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
}

.filter-section {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  margin-top: 1rem;
}

.nav-pills .nav-link {
  border-radius: 8px;
  transition: all 0.2s;
}

.nav-pills .nav-link.active {
  background-color: #0d6efd;
}

.tag-input-container {
  border: 1px solid #ced4da;
  border-radius: 6px;
  padding: 0.5rem;
  min-height: 38px;
}

.selected-tags {
  margin-bottom: 0.5rem;
}

.range-slider {
  padding: 0.5rem 0;
}

.range-labels {
  font-size: 0.8rem;
  color: #6c757d;
  margin-top: 0.25rem;
}

.active-filters {
  min-height: 2rem;
  padding: 0.5rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.search-results-summary {
  border-left: 4px solid #0d6efd;
}

[data-theme="dark"] .advanced-search-panel {
  background: var(--bs-dark);
  color: var(--bs-light);
}

[data-theme="dark"] .filter-section {
  background: rgba(255, 255, 255, 0.05);
}

[data-theme="dark"] .suggestions-dropdown {
  background: var(--bs-dark);
  border-color: var(--bs-gray-700);
}

[data-theme="dark"] .suggestion-item:hover,
[data-theme="dark"] .suggestion-item.active {
  background-color: rgba(255, 255, 255, 0.1);
}
</style>