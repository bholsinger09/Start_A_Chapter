<template>
  <div class="chapters">
    <div class="container">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col">
          <h1 class="display-4 text-center mb-3">
            <i class="bi bi-building text-primary me-3"></i>
            Chapters
          </h1>
          <p class="lead text-center text-muted">
            Manage university chapters across the network
          </p>
        </div>
      </div>

      <!-- Enhanced Actions Bar -->
      <div class="row mb-4">
        <div class="col-lg-3 col-md-6 mb-3">
          <div class="d-flex gap-2">
            <button 
              class="btn btn-primary flex-fill" 
              @click="showCreateModal = true"
            >
              <i class="bi bi-plus-circle me-2"></i>
              Add New Chapter
            </button>
            <button 
              class="btn btn-outline-primary" 
              @click="toggleComparisonMode"
              :class="{ 'active': comparisonMode }"
              title="Compare Chapters"
            >
              <i class="bi bi-arrow-left-right"></i>
            </button>
          </div>
        </div>
        <div class="col-lg-4 col-md-6 mb-3">
          <div class="input-group">
            <span class="input-group-text">
              <i class="bi bi-search"></i>
            </span>
            <input
              type="text"
              class="form-control"
              placeholder="Search chapters, universities, cities..."
              v-model="searchTerm"
              @input="performSearch"
              list="searchSuggestions"
            >
            <datalist id="searchSuggestions">
              <option v-for="suggestion in searchSuggestions" :key="suggestion" :value="suggestion"></option>
            </datalist>
            <button 
              class="btn btn-outline-secondary"
              @click="showSavedSearches = !showSavedSearches"
              title="Saved Searches"
            >
              <i class="bi bi-bookmark"></i>
            </button>
          </div>
        </div>
        <div class="col-lg-2 col-md-6 mb-3">
          <select 
            class="form-select" 
            v-model="selectedState"
            @change="performSearch"
          >
            <option value="">All States ({{ availableStates.length }})</option>
            <option v-for="state in availableStates" :key="state" :value="state">
              {{ state }} ({{ getStateChapterCount(state) }})
            </option>
          </select>
        </div>
        <div class="col-lg-3 col-md-6 mb-3">
          <div class="d-flex gap-2">
            <select 
              class="form-select" 
              v-model="sortCriteria"
              @change="applySorting"
            >
              <option value="name-asc">Name (A-Z)</option>
              <option value="name-desc">Name (Z-A)</option>
              <option value="members-desc">Most Members</option>
              <option value="members-asc">Least Members</option>
              <option value="health-desc">Healthiest</option>
              <option value="health-asc">Needs Attention</option>
              <option value="founded-desc">Newest</option>
              <option value="founded-asc">Oldest</option>
            </select>
            <button 
              class="btn btn-outline-secondary"
              @click="toggleViewMode"
              :title="viewMode === 'table' ? 'Switch to Card View' : 'Switch to Table View'"
            >
              <i :class="viewMode === 'table' ? 'bi bi-grid-3x3-gap' : 'bi bi-table'"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Enhanced Advanced Search Card -->
      <div class="row mb-4" v-if="showAdvancedSearch">
        <div class="col">
          <div class="card border-primary">
            <div class="card-header bg-primary text-white">
              <div class="d-flex justify-content-between align-items-center">
                <h6 class="card-title mb-0">
                  <i class="bi bi-funnel me-2"></i>
                  Advanced Search & Filters
                </h6>
                <button 
                  class="btn btn-sm btn-outline-light"
                  @click="saveCurrentSearch"
                  :disabled="!hasActiveFilters"
                >
                  <i class="bi bi-bookmark-plus me-1"></i>
                  Save Search
                </button>
              </div>
            </div>
            <div class="card-body">
              <!-- Basic Filters Row -->
              <div class="row mb-3">
                <div class="col-md-3">
                  <label class="form-label fw-semibold">University Name</label>
                  <input
                    type="text"
                    class="form-control"
                    placeholder="Search universities..."
                    v-model="advancedFilters.university"
                    @input="performSearch"
                    list="universitySuggestions"
                  >
                  <datalist id="universitySuggestions">
                    <option v-for="uni in uniqueUniversities" :key="uni" :value="uni"></option>
                  </datalist>
                </div>
                <div class="col-md-3">
                  <label class="form-label fw-semibold">City</label>
                  <select 
                    class="form-select" 
                    v-model="advancedFilters.city"
                    @change="performSearch"
                  >
                    <option value="">All Cities</option>
                    <option v-for="city in availableCities" :key="city" :value="city">
                      {{ city }}
                    </option>
                  </select>
                </div>
                <div class="col-md-3">
                  <label class="form-label fw-semibold">Status</label>
                  <select 
                    class="form-select" 
                    v-model="advancedFilters.active"
                    @change="performSearch"
                  >
                    <option value="">All Status</option>
                    <option :value="true">Active</option>
                    <option :value="false">Inactive</option>
                  </select>
                </div>
                <div class="col-md-3">
                  <label class="form-label fw-semibold">Health Score</label>
                  <select 
                    class="form-select" 
                    v-model="advancedFilters.healthScore"
                    @change="performSearch"
                  >
                    <option value="">All Health Levels</option>
                    <option value="excellent">Excellent (90-100%)</option>
                    <option value="good">Good (70-89%)</option>
                    <option value="average">Average (50-69%)</option>
                    <option value="needs-attention">Needs Attention (<50%)</option>
                  </select>
                </div>
              </div>

              <!-- Advanced Filters Row -->
              <div class="row mb-3">
                <div class="col-md-3">
                  <label class="form-label fw-semibold">Member Count</label>
                  <div class="d-flex gap-2">
                    <input
                      type="number"
                      class="form-control form-control-sm"
                      placeholder="Min"
                      v-model.number="advancedFilters.memberCountMin"
                      @input="performSearch"
                      min="0"
                    >
                    <input
                      type="number"
                      class="form-control form-control-sm"
                      placeholder="Max"
                      v-model.number="advancedFilters.memberCountMax"
                      @input="performSearch"
                      min="0"
                    >
                  </div>
                </div>
                <div class="col-md-3">
                  <label class="form-label fw-semibold">Founded Date</label>
                  <select 
                    class="form-select" 
                    v-model="advancedFilters.foundedPeriod"
                    @change="performSearch"
                  >
                    <option value="">Any Time</option>
                    <option value="last-year">Last Year</option>
                    <option value="last-2-years">Last 2 Years</option>
                    <option value="last-5-years">Last 5 Years</option>
                    <option value="over-5-years">Over 5 Years Ago</option>
                  </select>
                </div>
                <div class="col-md-3">
                  <label class="form-label fw-semibold">Activity Level</label>
                  <select 
                    class="form-select" 
                    v-model="advancedFilters.activityLevel"
                    @change="performSearch"
                  >
                    <option value="">All Activity Levels</option>
                    <option value="high">High Activity</option>
                    <option value="medium">Medium Activity</option>
                    <option value="low">Low Activity</option>
                    <option value="inactive">Inactive</option>
                  </select>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                  <button 
                    class="btn btn-outline-danger w-100"
                    @click="clearFilters"
                    :disabled="!hasActiveFilters"
                  >
                    <i class="bi bi-x-circle me-1"></i>
                    Clear All Filters
                  </button>
                </div>
              </div>

              <!-- Active Filters Display -->
              <div v-if="hasActiveFilters" class="border-top pt-3">
                <div class="d-flex flex-wrap gap-2 align-items-center">
                  <small class="text-muted fw-semibold">Active Filters:</small>
                  <span v-if="searchTerm" class="badge bg-primary">
                    Search: "{{ searchTerm }}"
                    <button class="btn-close btn-close-white ms-1" @click="searchTerm = ''; performSearch()"></button>
                  </span>
                  <span v-if="selectedState" class="badge bg-info">
                    State: {{ selectedState }}
                    <button class="btn-close btn-close-white ms-1" @click="selectedState = ''; performSearch()"></button>
                  </span>
                  <span v-if="advancedFilters.university" class="badge bg-success">
                    University: {{ advancedFilters.university }}
                    <button class="btn-close btn-close-white ms-1" @click="advancedFilters.university = ''; performSearch()"></button>
                  </span>
                  <span v-if="advancedFilters.healthScore" class="badge bg-warning">
                    Health: {{ getHealthScoreLabel(advancedFilters.healthScore) }}
                    <button class="btn-close btn-close-white ms-1" @click="advancedFilters.healthScore = ''; performSearch()"></button>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Saved Searches Panel -->
      <div class="row mb-3" v-if="showSavedSearches">
        <div class="col">
          <div class="card border-secondary">
            <div class="card-header">
              <h6 class="card-title mb-0">
                <i class="bi bi-bookmarks me-2"></i>
                Saved Searches
              </h6>
            </div>
            <div class="card-body">
              <div v-if="savedSearches.length === 0" class="text-muted text-center py-2">
                No saved searches yet. Apply filters and click "Save Search" to create one.
              </div>
              <div v-else class="d-flex flex-wrap gap-2">
                <div 
                  v-for="search in savedSearches" 
                  :key="search.id"
                  class="saved-search-item"
                >
                  <button 
                    class="btn btn-sm btn-outline-secondary flex-grow-1 text-start"
                    @click="loadSavedSearch(search)"
                  >
                    {{ search.name }}
                  </button>
                  <button 
                    class="btn btn-sm btn-outline-danger ms-1" 
                    @click="deleteSavedSearch(search.id)"
                    title="Delete search"
                  >
                    <i class="bi bi-trash3"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Comparison Mode Banner -->
      <div v-if="comparisonMode" class="row mb-3">
        <div class="col">
          <div class="alert alert-info d-flex justify-content-between align-items-center">
            <div>
              <i class="bi bi-info-circle me-2"></i>
              <strong>Comparison Mode:</strong> Select chapters to compare ({{ selectedChapters.length }}/3 selected)
            </div>
            <div class="d-flex gap-2">
              <button 
                class="btn btn-success btn-sm"
                @click="compareSelectedChapters"
                :disabled="selectedChapters.length < 2"
              >
                <i class="bi bi-bar-chart me-1"></i>
                Compare ({{ selectedChapters.length }})
              </button>
              <button 
                class="btn btn-outline-secondary btn-sm"
                @click="exitComparisonMode"
              >
                Exit Comparison
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Results Summary -->
      <div class="row mb-3">
        <div class="col">
          <div class="d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center gap-3">
              <span class="text-muted">
                Showing <strong>{{ searchResults.length }}</strong> of <strong>{{ chapters.length }}</strong> chapters
              </span>
              <div class="d-flex gap-2">
                <span v-if="healthStats.excellent > 0" class="badge bg-success" title="Excellent Health">
                  {{ healthStats.excellent }} Excellent
                </span>
                <span v-if="healthStats.good > 0" class="badge bg-primary" title="Good Health">
                  {{ healthStats.good }} Good
                </span>
                <span v-if="healthStats.needsAttention > 0" class="badge bg-warning" title="Needs Attention">
                  {{ healthStats.needsAttention }} Needs Attention
                </span>
              </div>
            </div>
            <div class="d-flex gap-2">
              <button 
                class="btn btn-sm btn-outline-secondary"
                @click="showAdvancedSearch = !showAdvancedSearch"
              >
                <i class="bi bi-funnel me-1"></i>
                {{ showAdvancedSearch ? 'Hide' : 'Show' }} Filters
              </button>
              <button 
                class="btn btn-sm btn-outline-secondary"
                @click="exportResults"
                :disabled="searchResults.length === 0"
              >
                <i class="bi bi-download me-1"></i>
                Export
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Analytics Dashboard -->
      <div v-if="showAnalytics && !loading" class="mb-4">
        <ChapterAnalytics 
          :chapters="searchResults" 
          :is-dark-mode="isDarkMode"
        />
      </div>

      <!-- Analytics Toggle -->
      <div class="row mb-3">
        <div class="col-12">
          <button 
            class="btn btn-outline-info btn-sm"
            @click="toggleAnalytics"
            :class="{ 'active': showAnalytics }"
          >
            <i :class="showAnalytics ? 'bi bi-eye-slash' : 'bi bi-bar-chart'"></i>
            {{ showAnalytics ? 'Hide Analytics' : 'Show Analytics' }}
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-4">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <!-- Chapters Table -->
      <div v-else class="card">
        <div class="card-header bg-light">
          <h5 class="card-title mb-0">
            <i class="bi bi-table me-2"></i>
            Chapters ({{ searchResults.length }})
          </h5>
        </div>
        <div class="card-body p-0">
          <div v-if="searchResults.length === 0" class="text-center py-4 text-muted">
            <i class="bi bi-inbox display-4 mb-3"></i>
            <p>No chapters found matching your criteria</p>
            <button 
              v-if="hasActiveFilters" 
              class="btn btn-outline-primary btn-sm"
              @click="clearFilters"
            >
              Clear Filters
            </button>
          </div>
          <!-- Table View -->
          <div v-else-if="viewMode === 'table'" class="table-responsive">
            <table class="table table-hover mb-0">
              <thead class="table-light sticky-top">
                <tr>
                  <th v-if="comparisonMode" width="40">
                    <input 
                      type="checkbox" 
                      class="form-check-input"
                      @change="toggleAllSelection"
                      :checked="allSelected"
                    >
                  </th>
                  <th class="sortable" @click="setSortCriteria('name')">
                    Chapter Name
                    <i class="bi" :class="getSortIcon('name')"></i>
                  </th>
                  <th class="sortable" @click="setSortCriteria('university')">
                    University
                    <i class="bi" :class="getSortIcon('university')"></i>
                  </th>
                  <th>Location</th>
                  <th class="text-center">Health Score</th>
                  <th class="sortable text-center" @click="setSortCriteria('members')">
                    Members
                    <i class="bi" :class="getSortIcon('members')"></i>
                  </th>
                  <th class="sortable text-center" @click="setSortCriteria('founded')">
                    Founded
                    <i class="bi" :class="getSortIcon('founded')"></i>
                  </th>
                  <th class="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr 
                  v-for="chapter in searchResults" 
                  :key="chapter.id"
                  :class="{ 'table-active': selectedChapters.includes(chapter.id) }"
                >
                  <td v-if="comparisonMode">
                    <input 
                      type="checkbox" 
                      class="form-check-input"
                      :checked="selectedChapters.includes(chapter.id)"
                      @change="toggleChapterSelection(chapter.id)"
                      :disabled="!selectedChapters.includes(chapter.id) && selectedChapters.length >= 3"
                    >
                  </td>
                  <td>
                    <div class="d-flex align-items-center">
                      <div>
                        <strong>{{ chapter.name }}</strong>
                        <small v-if="chapter.description" class="text-muted d-block">
                          {{ truncateText(chapter.description, 50) }}
                        </small>
                      </div>
                    </div>
                  </td>
                  <td>{{ chapter.universityName }}</td>
                  <td>
                    <div>
                      <i class="bi bi-geo-alt text-muted me-1"></i>
                      {{ chapter.city }}, {{ chapter.state }}
                    </div>
                  </td>
                  <td class="text-center">
                    <div class="d-flex flex-column align-items-center">
                      <span 
                        class="badge"
                        :class="getHealthScoreBadgeClass(getChapterHealthScore(chapter))"
                      >
                        {{ getChapterHealthScore(chapter) }}%
                      </span>
                      <small class="text-muted">
                        {{ getHealthScoreStatus(getChapterHealthScore(chapter)) }}
                      </small>
                    </div>
                  </td>
                  <td class="text-center">
                    <span 
                      class="badge"
                      :class="getMemberCountBadgeClass(memberCounts[chapter.id] || 0)"
                    >
                      {{ memberCounts[chapter.id] || 0 }}
                    </span>
                  </td>
                  <td class="text-center">
                    <small class="text-muted">
                      {{ formatDate(chapter.foundedDate) }}
                    </small>
                  </td>
                  <td>
                    <div class="btn-group" role="group">
                      <router-link 
                        :to="`/chapters/${chapter.id}`" 
                        class="btn btn-sm btn-outline-primary"
                        title="View Details"
                      >
                        <i class="bi bi-eye"></i>
                      </router-link>
                      <a 
                        :href="getCampusLabsUrl(chapter)" 
                        target="_blank"
                        rel="noopener noreferrer"
                        class="btn btn-sm btn-outline-success"
                        :title="getButtonTooltip(chapter)"
                        :aria-label="`${getButtonText(chapter)} for ${chapter.universityName} (opens in new tab)`"
                        v-if="getCampusLabsUrl(chapter)"
                        @click="trackLinkClick(chapter)"
                      >
                        <i class="bi bi-link-45deg me-1"></i>
                        <small>{{ getButtonText(chapter) }}</small>
                      </a>
                      <button 
                        class="btn btn-sm btn-outline-secondary"
                        @click="editChapter(chapter)"
                        title="Edit Chapter"
                      >
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button 
                        class="btn btn-sm btn-outline-danger"
                        @click="confirmDelete(chapter)"
                        title="Delete Chapter"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Card View -->
          <div v-else class="row g-4">
            <div 
              v-for="chapter in searchResults" 
              :key="chapter.id" 
              class="col-lg-4 col-md-6"
            >
              <div 
                class="card h-100 chapter-card"
                :class="{ 
                  'border-primary': selectedChapters.includes(chapter.id),
                  'shadow-sm': !selectedChapters.includes(chapter.id)
                }"
                @click="comparisonMode ? toggleChapterSelection(chapter.id) : null"
                :style="{ cursor: comparisonMode ? 'pointer' : 'default' }"
              >
                <!-- Card Header with Health Score -->
                <div class="card-header d-flex justify-content-between align-items-center">
                  <div class="d-flex align-items-center">
                    <input 
                      v-if="comparisonMode"
                      type="checkbox" 
                      class="form-check-input me-2"
                      :checked="selectedChapters.includes(chapter.id)"
                      :disabled="!selectedChapters.includes(chapter.id) && selectedChapters.length >= 3"
                    >
                    <h6 class="card-title mb-0">{{ chapter.name }}</h6>
                  </div>
                  <span 
                    class="badge"
                    :class="getHealthScoreBadgeClass(getChapterHealthScore(chapter))"
                  >
                    {{ getChapterHealthScore(chapter) }}%
                  </span>
                </div>

                <!-- Card Body -->
                <div class="card-body">
                  <!-- University Info -->
                  <div class="mb-3">
                    <h6 class="text-primary mb-1">{{ chapter.universityName }}</h6>
                    <p class="text-muted small mb-0">
                      <i class="bi bi-geo-alt me-1"></i>
                      {{ chapter.city }}, {{ chapter.state }}
                    </p>
                  </div>

                  <!-- Stats Row -->
                  <div class="row text-center mb-3">
                    <div class="col-4">
                      <div class="stat-item">
                        <span 
                          class="badge d-block"
                          :class="getMemberCountBadgeClass(memberCounts[chapter.id] || 0)"
                        >
                          {{ memberCounts[chapter.id] || 0 }}
                        </span>
                        <small class="text-muted">Members</small>
                      </div>
                    </div>
                    <div class="col-4">
                      <div class="stat-item">
                        <span class="badge bg-info d-block">
                          {{ getChapterEventCount(chapter.id) }}
                        </span>
                        <small class="text-muted">Events</small>
                      </div>
                    </div>
                    <div class="col-4">
                      <div class="stat-item">
                        <span class="badge bg-secondary d-block">
                          {{ getYearsSinceFounded(chapter.foundedDate) }}y
                        </span>
                        <small class="text-muted">Age</small>
                      </div>
                    </div>
                  </div>

                  <!-- Health Indicators -->
                  <div class="health-indicators mb-3">
                    <small class="text-muted d-block mb-1">Health Indicators:</small>
                    <div class="d-flex gap-1">
                      <span 
                        class="health-dot"
                        :class="getHealthDotClass('members', chapter)"
                        title="Member Count Health"
                      ></span>
                      <span 
                        class="health-dot"
                        :class="getHealthDotClass('activity', chapter)"
                        title="Activity Level Health"
                      ></span>
                      <span 
                        class="health-dot"
                        :class="getHealthDotClass('growth', chapter)"
                        title="Growth Health"
                      ></span>
                      <span 
                        class="health-dot"
                        :class="getHealthDotClass('engagement', chapter)"
                        title="Engagement Health"
                      ></span>
                    </div>
                  </div>

                  <!-- Description -->
                  <p v-if="chapter.description" class="text-muted small mb-3">
                    {{ truncateText(chapter.description, 100) }}
                  </p>
                </div>

                <!-- Card Footer -->
                <div class="card-footer bg-transparent">
                  <div class="d-flex justify-content-between align-items-center">
                    <small class="text-muted">
                      Founded {{ formatDate(chapter.foundedDate) }}
                    </small>
                    <div class="btn-group btn-group-sm" role="group">
                      <button 
                        class="btn btn-outline-primary"
                        @click.stop="viewChapterDetails(chapter)"
                        title="View Details"
                      >
                        <i class="bi bi-eye"></i>
                      </button>
                      <button 
                        class="btn btn-outline-secondary"
                        @click.stop="editChapter(chapter)"
                        title="Edit Chapter"
                      >
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button 
                        class="btn btn-outline-danger"
                        @click.stop="confirmDelete(chapter)"
                        title="Delete Chapter"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div 
      class="modal fade" 
      :class="{ show: showCreateModal || showEditModal }"
      :style="{ display: showCreateModal || showEditModal ? 'block' : 'none' }"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ showEditModal ? 'Edit Chapter' : 'Create New Chapter' }}
            </h5>
            <button 
              type="button" 
              class="btn-close" 
              @click="closeModal"
            ></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveChapter">
              <div class="mb-3">
                <label for="chapterName" class="form-label">Chapter Name *</label>
                <input
                  type="text"
                  class="form-control"
                  id="chapterName"
                  v-model="chapterForm.name"
                  required
                >
              </div>
              <div class="mb-3">
                <label for="universityName" class="form-label">University Name *</label>
                <input
                  type="text"
                  class="form-control"
                  id="universityName"
                  v-model="chapterForm.universityName"
                  required
                >
              </div>
              <div class="row">
                <div class="col-md-8 mb-3">
                  <label for="city" class="form-label">City *</label>
                  <input
                    type="text"
                    class="form-control"
                    id="city"
                    v-model="chapterForm.city"
                    required
                  >
                </div>
                <div class="col-md-4 mb-3">
                  <label for="state" class="form-label">State *</label>
                  <input
                    type="text"
                    class="form-control"
                    id="state"
                    v-model="chapterForm.state"
                    required
                    maxlength="2"
                    placeholder="CA"
                  >
                </div>
              </div>
              <div class="mb-3">
                <label for="foundedDate" class="form-label">Founded Date *</label>
                <input
                  type="date"
                  class="form-control"
                  id="foundedDate"
                  v-model="chapterForm.foundedDate"
                  required
                >
              </div>
              <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea
                  class="form-control"
                  id="description"
                  v-model="chapterForm.description"
                  rows="3"
                ></textarea>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">
              Cancel
            </button>
            <button 
              type="button" 
              class="btn btn-primary" 
              @click="saveChapter"
              :disabled="saving"
            >
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              {{ showEditModal ? 'Update Chapter' : 'Create Chapter' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div 
      class="modal fade" 
      :class="{ show: showDeleteModal }"
      :style="{ display: showDeleteModal ? 'block' : 'none' }"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Confirm Delete</h5>
            <button type="button" class="btn-close" @click="showDeleteModal = false"></button>
          </div>
          <div class="modal-body">
            <p>Are you sure you want to delete the chapter <strong>{{ chapterToDelete?.name }}</strong>?</p>
            <p class="text-danger">This action cannot be undone.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showDeleteModal = false">
              Cancel
            </button>
            <button type="button" class="btn btn-danger" @click="deleteChapter">
              Delete Chapter
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Chapter Comparison Modal -->
    <div v-if="showComparisonModal" class="modal fade show d-block" tabindex="-1">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-bar-chart me-2"></i>
              Chapter Comparison
            </h5>
            <button type="button" class="btn-close" @click="closeComparisonModal"></button>
          </div>
          <div class="modal-body">
            <div class="row">
              <div 
                v-for="chapter in comparisonChapters" 
                :key="chapter.id"
                class="col-md-4"
              >
                <div class="card h-100">
                  <div class="card-header text-center">
                    <h6 class="card-title mb-1">{{ chapter.name }}</h6>
                    <small class="text-muted">{{ chapter.universityName }}</small>
                  </div>
                  <div class="card-body">
                    <!-- Health Score -->
                    <div class="text-center mb-3">
                      <div 
                        class="health-score-circle mx-auto mb-2"
                        :style="{ 
                          '--score': getChapterHealthScore(chapter),
                          background: `conic-gradient(${getHealthScoreColor(getChapterHealthScore(chapter))} ${getChapterHealthScore(chapter)}%, #e9ecef 0%)` 
                        }"
                      >
                        <span class="h4 mb-0">{{ getChapterHealthScore(chapter) }}%</span>
                      </div>
                      <small class="text-muted">Overall Health Score</small>
                    </div>

                    <!-- Key Metrics -->
                    <div class="comparison-metrics">
                      <div class="metric-row">
                        <span class="metric-label">Members</span>
                        <span class="metric-value">{{ memberCounts[chapter.id] || 0 }}</span>
                      </div>
                      <div class="metric-row">
                        <span class="metric-label">Events</span>
                        <span class="metric-value">{{ getChapterEventCount(chapter.id) }}</span>
                      </div>
                      <div class="metric-row">
                        <span class="metric-label">Founded</span>
                        <span class="metric-value">{{ formatDate(chapter.foundedDate) }}</span>
                      </div>
                      <div class="metric-row">
                        <span class="metric-label">Location</span>
                        <span class="metric-value">{{ chapter.city }}, {{ chapter.state }}</span>
                      </div>
                      <div class="metric-row">
                        <span class="metric-label">Age</span>
                        <span class="metric-value">{{ getYearsSinceFounded(chapter.foundedDate) }} years</span>
                      </div>
                    </div>

                    <!-- Performance Indicators -->
                    <div class="mt-3">
                      <small class="text-muted d-block mb-2">Performance Indicators</small>
                      <div class="performance-bars">
                        <div class="performance-item">
                          <span class="small">Member Growth</span>
                          <div class="progress progress-sm">
                            <div 
                              class="progress-bar bg-success"
                              :style="{ width: getPerformanceMetric(chapter, 'growth') + '%' }"
                            ></div>
                          </div>
                        </div>
                        <div class="performance-item">
                          <span class="small">Activity Level</span>
                          <div class="progress progress-sm">
                            <div 
                              class="progress-bar bg-info"
                              :style="{ width: getPerformanceMetric(chapter, 'activity') + '%' }"
                            ></div>
                          </div>
                        </div>
                        <div class="performance-item">
                          <span class="small">Engagement</span>
                          <div class="progress progress-sm">
                            <div 
                              class="progress-bar bg-warning"
                              :style="{ width: getPerformanceMetric(chapter, 'engagement') + '%' }"
                            ></div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeComparisonModal">
              Close
            </button>
            <button type="button" class="btn btn-primary" @click="exportComparison">
              <i class="bi bi-download me-1"></i>
              Export Comparison
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Backdrop -->
    <div 
      v-if="showCreateModal || showEditModal || showDeleteModal || showComparisonModal"
      class="modal-backdrop fade show"
      @click="closeModal"
    ></div>
  </div>
</template>

<script>
import { chapterService } from '../services/chapterService'
import { memberService } from '../services/memberService'
import { useChapterLinks } from '../composables/useChapterLinks'
import { useTheme } from '../composables/useTheme'
import ChapterAnalytics from '../components/ChapterAnalytics.vue'
import { computed } from 'vue'

export default {
  name: 'Chapters',
  components: {
    ChapterAnalytics
  },
  setup() {
    // Use the chapter links composable for consistent functionality
    const { getCampusLabsUrl, getButtonText, getButtonTooltip, trackLinkClick } = useChapterLinks()
    
    // Use theme composable
    const { currentTheme } = useTheme()
    
    return {
      getCampusLabsUrl,
      getButtonText,
      getButtonTooltip,
      trackLinkClick,
      isDarkModeComputed: computed(() => currentTheme.value === 'dark')
    }
  },
  data() {
    return {
      loading: true,
      saving: false,
      chapters: [],
      members: [],
      searchResults: [],
      availableStates: [],
      availableCities: [],
      searchTerm: '',
      selectedState: '',
      showAdvancedSearch: false,
      showCreateModal: false,
      showEditModal: false,
      showDeleteModal: false,
      chapterToDelete: null,
      advancedFilters: {
        university: '',
        city: '',
        active: ''
      },
      chapterForm: {
        id: null,
        name: '',
        universityName: '',
        city: '',
        state: '',
        foundedDate: '',
        description: ''
      },
      // New properties for enhanced features
      currentView: 'table',
      sortBy: 'name',
      sortOrder: 'asc',
      comparisonMode: false,
      selectedChapters: [],
      showComparisonModal: false,
      savedSearches: [],
      showAnalytics: false,
      isDarkMode: false,
      searchSuggestions: [],
      showSavedSearches: false,
      advancedSearchFilters: {
        university: '',
        city: '',
        healthScoreMin: 0,
        healthScoreMax: 100,
        memberCountMin: 0,
        memberCountMax: 1000,
        foundedDateStart: '',
        foundedDateEnd: '',
        activityLevel: ''
      },
      activeFilters: []
    }
  },
  computed: {
    hasActiveFilters() {
      return this.searchTerm || 
             this.selectedState || 
             this.advancedFilters.university || 
             this.advancedFilters.city || 
             this.advancedFilters.active !== ''
    },
    // Performance optimization: pre-calculate member counts to avoid repeated filtering
    memberCounts() {
      const counts = {}
      this.members.forEach(member => {
        if (member.chapter?.id) {
          counts[member.chapter.id] = (counts[member.chapter.id] || 0) + 1
        }
      })
      return counts
    },
    
    comparisonChapters() {
      return this.chapters.filter(chapter => this.selectedChapters.includes(chapter.id))
    },
    
    sortedAndFilteredChapters() {
      let results = [...this.searchResults]
      
      // Apply sorting
      results.sort((a, b) => {
        let aValue, bValue
        
        switch (this.sortBy) {
          case 'name':
            aValue = a.name.toLowerCase()
            bValue = b.name.toLowerCase()
            break
          case 'university':
            aValue = a.universityName.toLowerCase()
            bValue = b.universityName.toLowerCase()
            break
          case 'members':
            aValue = this.memberCounts[a.id] || 0
            bValue = this.memberCounts[b.id] || 0
            break
          case 'founded':
            aValue = new Date(a.foundedDate)
            bValue = new Date(b.foundedDate)
            break
          case 'health':
            aValue = this.getChapterHealthScore(a)
            bValue = this.getChapterHealthScore(b)
            break
          case 'location':
            aValue = `${a.city}, ${a.state}`.toLowerCase()
            bValue = `${b.city}, ${b.state}`.toLowerCase()
            break
          default:
            return 0
        }
        
        if (aValue < bValue) return this.sortOrder === 'asc' ? -1 : 1
        if (aValue > bValue) return this.sortOrder === 'asc' ? 1 : -1
        return 0
      })
      
      return results
    },

    uniqueUniversities() {
      return [...new Set(this.chapters.map(c => c.universityName).filter(Boolean))].sort()
    },

    healthStats() {
      const stats = {
        excellent: 0,
        good: 0,
        needsAttention: 0
      }
      
      this.searchResults.forEach(chapter => {
        const score = this.getChapterHealthScore(chapter)
        if (score >= 80) {
          stats.excellent++
        } else if (score >= 60) {
          stats.good++
        } else {
          stats.needsAttention++
        }
      })
      
      return stats
    },

    allSelected() {
      return this.selectedChapters.length > 0 && 
             this.selectedChapters.length === Math.min(3, this.searchResults.length)
    }
  },
  async mounted() {
    // Check if there are route query parameters
    if (this.$route.query.state) {
      this.selectedState = this.$route.query.state
    }
    
    // Load saved searches from localStorage
    const saved = localStorage.getItem('savedSearches')
    if (saved) {
      this.savedSearches = JSON.parse(saved)
    }
    
    await this.loadData()
  },
  methods: {
    async loadData() {
      try {
        this.loading = true
        const [chaptersData, membersData] = await Promise.all([
          chapterService.getAllChapters(),
          memberService.getAllMembers()
        ])
        console.log('Loaded chapters:', chaptersData.length)
        this.chapters = chaptersData
        this.members = membersData
        this.searchResults = chaptersData
        
        // Extract unique states and cities for filters
        this.availableStates = [...new Set(chaptersData.map(c => c.state).filter(Boolean))].sort()
        this.availableCities = [...new Set(chaptersData.map(c => c.city).filter(Boolean))].sort()
        
      } catch (error) {
        console.error('Error loading chapters:', error)
      } finally {
        this.loading = false
      }
    },

    async performSearch() {
      try {
        // Build search parameters
        const searchParams = {}
        
        if (this.searchTerm) searchParams.name = this.searchTerm
        if (this.selectedState) searchParams.state = this.selectedState
        if (this.advancedFilters.university) searchParams.university = this.advancedFilters.university
        if (this.advancedFilters.city) searchParams.city = this.advancedFilters.city
        if (this.advancedFilters.active !== '') searchParams.active = this.advancedFilters.active
        
        console.log('Search parameters:', searchParams)
        console.log('Available chapters:', this.chapters.length)
        console.log('Chapters by state:', this.chapters.map(c => ({ name: c.name, state: c.state })))
        
        // If no search parameters, show all chapters
        if (Object.keys(searchParams).length === 0) {
          this.searchResults = this.chapters
          return
        }
        
        // Perform backend search
        console.log('Calling backend search with params:', searchParams)
        this.searchResults = await chapterService.searchChapters(searchParams)
        console.log('Backend search returned:', this.searchResults.length, 'chapters')
        console.log('Search results:', this.searchResults.map(c => ({ name: c.name, state: c.state })))
        
        // If backend returns empty but we know chapters exist for this state, use fallback
        if (this.searchResults.length === 0 && this.selectedState) {
          const expectedCount = this.getStateChapterCount(this.selectedState)
          if (expectedCount > 0) {
            console.log(`Backend returned 0 results but ${expectedCount} chapters exist for ${this.selectedState}, using fallback`)
            this.performLocalSearch()
            return
          }
        }
        
      } catch (error) {
        console.error('Error searching chapters:', error)
        console.log('Backend search failed, falling back to local search')
        // Fallback to local filtering if backend search fails
        this.performLocalSearch()
      }
    },

    performLocalSearch() {
      console.log('Performing local search...')
      let results = this.chapters
      console.log('Starting with', results.length, 'chapters')
      
      if (this.searchTerm) {
        const term = this.searchTerm.toLowerCase()
        results = results.filter(chapter =>
          chapter.name.toLowerCase().includes(term) ||
          chapter.universityName.toLowerCase().includes(term)
        )
        console.log('After search term filter:', results.length, 'chapters')
      }
      
      if (this.selectedState) {
        console.log('Filtering by state:', this.selectedState)
        console.log('Available states in chapters:', [...new Set(results.map(c => c.state))])
        results = results.filter(chapter => chapter.state === this.selectedState)
        console.log('After state filter:', results.length, 'chapters')
      }
      
      if (this.advancedFilters.university) {
        const university = this.advancedFilters.university.toLowerCase()
        results = results.filter(chapter =>
          chapter.universityName.toLowerCase().includes(university)
        )
      }
      
      if (this.advancedFilters.city) {
        results = results.filter(chapter => chapter.city === this.advancedFilters.city)
      }
      
      if (this.advancedFilters.active !== '') {
        results = results.filter(chapter => chapter.active === this.advancedFilters.active)
      }
      
      console.log('Local search completed, setting results:', results.length, 'chapters')
      this.searchResults = results
    },

    clearFilters() {
      this.searchTerm = ''
      this.selectedState = ''
      this.advancedFilters = {
        university: '',
        city: '',
        active: ''
      }
      this.advancedSearchFilters = {
        university: '',
        city: '',
        healthScoreMin: 0,
        healthScoreMax: 100,
        memberCountMin: 0,
        memberCountMax: 1000,
        foundedDateStart: '',
        foundedDateEnd: '',
        activityLevel: ''
      }
      this.activeFilters = []
      this.searchResults = this.chapters
    },

    // New methods for enhanced features
    toggleView(view) {
      this.currentView = view
    },

    toggleSort(column) {
      if (this.sortBy === column) {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc'
      } else {
        this.sortBy = column
        this.sortOrder = 'asc'
      }
    },

    toggleComparisonMode() {
      this.comparisonMode = !this.comparisonMode
      if (!this.comparisonMode) {
        this.selectedChapters = []
      }
    },

    toggleChapterSelection(chapterId) {
      const index = this.selectedChapters.indexOf(chapterId)
      if (index > -1) {
        this.selectedChapters.splice(index, 1)
      } else if (this.selectedChapters.length < 3) { // Limit to 3 chapters for comparison
        this.selectedChapters.push(chapterId)
      }
    },

    openComparisonModal() {
      if (this.selectedChapters.length > 0) {
        this.showComparisonModal = true
      }
    },

    closeComparisonModal() {
      this.showComparisonModal = false
    },

    getChapterHealthScore(chapter) {
      const memberCount = this.memberCounts[chapter.id] || 0
      const age = this.getYearsSinceFounded(chapter.foundedDate)
      const eventCount = this.getChapterEventCount(chapter.id)
      
      // Calculate health score based on multiple factors
      let score = 0
      
      // Member count factor (30% of score)
      if (memberCount >= 50) score += 30
      else if (memberCount >= 25) score += 20
      else if (memberCount >= 10) score += 15
      else score += 5
      
      // Activity factor (40% of score) - based on events
      if (eventCount >= 10) score += 40
      else if (eventCount >= 5) score += 30
      else if (eventCount >= 2) score += 20
      else if (eventCount >= 1) score += 10
      
      // Longevity factor (20% of score)
      if (age >= 3) score += 20
      else if (age >= 2) score += 15
      else if (age >= 1) score += 10
      else score += 5
      
      // Status factor (10% of score)
      if (chapter.active) score += 10
      
      return Math.min(score, 100)
    },

    getHealthScoreColor(score) {
      if (score >= 80) return '#28a745'      // Green
      if (score >= 60) return '#ffc107'      // Yellow
      if (score >= 40) return '#fd7e14'      // Orange
      return '#dc3545'                       // Red
    },

    getHealthScoreLabel(score) {
      if (score >= 80) return 'Excellent'
      if (score >= 60) return 'Good'
      if (score >= 40) return 'Fair'
      return 'Needs Attention'
    },

    getHealthScoreBadgeClass(score) {
      if (score >= 80) return 'bg-success'      // Green
      if (score >= 60) return 'bg-primary'      // Blue  
      if (score >= 40) return 'bg-warning'      // Yellow
      return 'bg-danger'                        // Red
    },

    getHealthScoreStatus(score) {
      if (score >= 80) return 'Excellent'
      if (score >= 60) return 'Good'
      if (score >= 40) return 'Fair'
      return 'Needs Attention'
    },

    getHealthDotClass(metric, chapter) {
      const score = this.getPerformanceMetric(chapter, metric)
      if (score >= 80) return 'bg-success'
      if (score >= 60) return 'bg-primary'
      if (score >= 40) return 'bg-warning'
      return 'bg-danger'
    },

    getMemberCountBadgeClass(memberCount) {
      if (memberCount >= 100) return 'badge bg-success'    // Green for large chapters
      if (memberCount >= 50) return 'badge bg-primary'     // Blue for medium chapters
      if (memberCount >= 20) return 'badge bg-warning'     // Yellow for small chapters
      if (memberCount >= 1) return 'badge bg-info'         // Light blue for new chapters
      return 'badge bg-secondary'                          // Gray for empty chapters
    },

    getYearsSinceFounded(foundedDate) {
      const now = new Date()
      const founded = new Date(foundedDate)
      return Math.floor((now - founded) / (365.25 * 24 * 60 * 60 * 1000))
    },

    getChapterEventCount(chapterId) {
      // This would typically come from events data
      // For now, return a calculated estimate
      const memberCount = this.memberCounts[chapterId] || 0
      return Math.floor(memberCount / 5) + Math.floor(Math.random() * 5)
    },

    getPerformanceMetric(chapter, type) {
      const memberCount = this.memberCounts[chapter.id] || 0
      const age = this.getYearsSinceFounded(chapter.foundedDate)
      
      switch (type) {
        case 'growth':
          // Estimate growth based on member count and age
          if (age === 0) return 50
          return Math.min((memberCount / age) * 2, 100)
        
        case 'activity':
          // Estimate activity based on events and members
          const eventCount = this.getChapterEventCount(chapter.id)
          return Math.min((eventCount / Math.max(memberCount / 10, 1)) * 20, 100)
        
        case 'engagement':
          // Estimate engagement based on member-to-event ratio
          const events = this.getChapterEventCount(chapter.id)
          if (memberCount === 0) return 0
          return Math.min((events / memberCount) * 100, 100)
        
        default:
          return 0
      }
    },

    applyAdvancedSearch() {
      let results = this.chapters
      
      // Apply all advanced filters
      if (this.advancedSearchFilters.university) {
        const term = this.advancedSearchFilters.university.toLowerCase()
        results = results.filter(c => 
          c.universityName.toLowerCase().includes(term)
        )
      }
      
      if (this.advancedSearchFilters.city) {
        results = results.filter(c => c.city === this.advancedSearchFilters.city)
      }
      
      // Health score range
      results = results.filter(c => {
        const score = this.getChapterHealthScore(c)
        return score >= this.advancedSearchFilters.healthScoreMin && 
               score <= this.advancedSearchFilters.healthScoreMax
      })
      
      // Member count range
      results = results.filter(c => {
        const count = this.memberCounts[c.id] || 0
        return count >= this.advancedSearchFilters.memberCountMin && 
               count <= this.advancedSearchFilters.memberCountMax
      })
      
      // Founded date range
      if (this.advancedSearchFilters.foundedDateStart) {
        const startDate = new Date(this.advancedSearchFilters.foundedDateStart)
        results = results.filter(c => new Date(c.foundedDate) >= startDate)
      }
      
      if (this.advancedSearchFilters.foundedDateEnd) {
        const endDate = new Date(this.advancedSearchFilters.foundedDateEnd)
        results = results.filter(c => new Date(c.foundedDate) <= endDate)
      }
      
      // Activity level
      if (this.advancedSearchFilters.activityLevel) {
        results = results.filter(c => {
          const score = this.getChapterHealthScore(c)
          const level = this.advancedSearchFilters.activityLevel
          
          switch (level) {
            case 'high': return score >= 80
            case 'medium': return score >= 40 && score < 80
            case 'low': return score < 40
            default: return true
          }
        })
      }
      
      this.searchResults = results
      this.updateActiveFilters()
    },

    updateActiveFilters() {
      this.activeFilters = []
      
      if (this.searchTerm) {
        this.activeFilters.push({
          type: 'search',
          label: `Search: "${this.searchTerm}"`,
          value: this.searchTerm
        })
      }
      
      if (this.selectedState) {
        this.activeFilters.push({
          type: 'state',
          label: `State: ${this.selectedState}`,
          value: this.selectedState
        })
      }
      
      if (this.advancedSearchFilters.university) {
        this.activeFilters.push({
          type: 'university',
          label: `University: ${this.advancedSearchFilters.university}`,
          value: this.advancedSearchFilters.university
        })
      }
      
      if (this.advancedSearchFilters.city) {
        this.activeFilters.push({
          type: 'city',
          label: `City: ${this.advancedSearchFilters.city}`,
          value: this.advancedSearchFilters.city
        })
      }
      
      const healthMin = this.advancedSearchFilters.healthScoreMin
      const healthMax = this.advancedSearchFilters.healthScoreMax
      if (healthMin > 0 || healthMax < 100) {
        this.activeFilters.push({
          type: 'health',
          label: `Health Score: ${healthMin}-${healthMax}%`,
          value: `${healthMin}-${healthMax}`
        })
      }
      
      const memberMin = this.advancedSearchFilters.memberCountMin
      const memberMax = this.advancedSearchFilters.memberCountMax
      if (memberMin > 0 || memberMax < 1000) {
        this.activeFilters.push({
          type: 'members',
          label: `Members: ${memberMin}-${memberMax}`,
          value: `${memberMin}-${memberMax}`
        })
      }
    },

    removeFilter(filter) {
      switch (filter.type) {
        case 'search':
          this.searchTerm = ''
          break
        case 'state':
          this.selectedState = ''
          break
        case 'university':
          this.advancedSearchFilters.university = ''
          break
        case 'city':
          this.advancedSearchFilters.city = ''
          break
        case 'health':
          this.advancedSearchFilters.healthScoreMin = 0
          this.advancedSearchFilters.healthScoreMax = 100
          break
        case 'members':
          this.advancedSearchFilters.memberCountMin = 0
          this.advancedSearchFilters.memberCountMax = 1000
          break
      }
      this.applyAdvancedSearch()
    },

    saveSearch() {
      const searchName = prompt('Enter a name for this search:')
      if (searchName) {
        const search = {
          id: Date.now(), // Simple ID generation
          name: searchName,
          filters: { ...this.advancedSearchFilters },
          searchTerm: this.searchTerm,
          selectedState: this.selectedState,
          timestamp: new Date().toISOString()
        }
        this.savedSearches.push(search)
        localStorage.setItem('savedSearches', JSON.stringify(this.savedSearches))
      }
    },

    loadSavedSearch(search) {
      this.advancedSearchFilters = { ...search.filters }
      this.searchTerm = search.searchTerm
      this.selectedState = search.selectedState
      this.applyAdvancedSearch()
    },

    deleteSavedSearch(searchId) {
      const index = this.savedSearches.findIndex(s => s.id === searchId)
      if (index > -1) {
        this.savedSearches.splice(index, 1)
        localStorage.setItem('savedSearches', JSON.stringify(this.savedSearches))
      }
    },

    exportComparison() {
      const data = this.comparisonChapters.map(chapter => ({
        name: chapter.name,
        university: chapter.universityName,
        location: `${chapter.city}, ${chapter.state}`,
        members: this.memberCounts[chapter.id] || 0,
        healthScore: this.getChapterHealthScore(chapter),
        founded: chapter.foundedDate,
        age: this.getYearsSinceFounded(chapter.foundedDate)
      }))
      
      const csv = [
        ['Name', 'University', 'Location', 'Members', 'Health Score', 'Founded', 'Age (Years)'],
        ...data.map(row => Object.values(row))
      ].map(row => row.join(',')).join('\n')
      
      const blob = new Blob([csv], { type: 'text/csv' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'chapter-comparison.csv'
      a.click()
      window.URL.revokeObjectURL(url)
    },

    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    },

    
    getAlternateCampusLabsSearch(chapter) {
      // Provide a fallback search URL if the direct link doesn't work
      const searchTerm = encodeURIComponent(`${chapter.universityName} turning point usa`)
      return `https://www.google.com/search?q=site:campuslabs.com+${searchTerm}`
    },
    editChapter(chapter) {
      this.chapterForm = {
        id: chapter.id,
        name: chapter.name,
        universityName: chapter.universityName,
        city: chapter.city,
        state: chapter.state,
        foundedDate: chapter.foundedDate,
        description: chapter.description || ''
      }
      this.showEditModal = true
    },
    confirmDelete(chapter) {
      this.chapterToDelete = chapter
      this.showDeleteModal = true
    },
    async saveChapter() {
      try {
        this.saving = true
        
        if (this.showEditModal) {
          await chapterService.updateChapter(this.chapterForm.id, this.chapterForm)
        } else {
          await chapterService.createChapter(this.chapterForm)
        }
        
        await this.loadData()
        await this.performSearch() // Refresh search results
        this.closeModal()
      } catch (error) {
        console.error('Error saving chapter:', error)
        alert('Error saving chapter. Please try again.')
      } finally {
        this.saving = false
      }
    },
    async deleteChapter() {
      try {
        await chapterService.deleteChapter(this.chapterToDelete.id)
        await this.loadData()
        await this.performSearch() // Refresh search results
        this.showDeleteModal = false
        this.chapterToDelete = null
      } catch (error) {
        console.error('Error deleting chapter:', error)
        alert('Error deleting chapter. Please try again.')
      }
    },

    getStateChapterCount(state) {
      return this.chapters.filter(chapter => chapter.state === state).length
    },

    getSortIcon(column) {
      if (this.sortBy !== column) {
        return 'bi-chevron-expand'
      }
      return this.sortOrder === 'asc' ? 'bi-chevron-up' : 'bi-chevron-down'
    },

    setSortCriteria(column) {
      if (this.sortBy === column) {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc'
      } else {
        this.sortBy = column
        this.sortOrder = 'asc'
      }
    },

    truncateText(text, maxLength) {
      if (!text) return ''
      if (text.length <= maxLength) return text
      return text.substring(0, maxLength) + '...'
    },

    toggleAllSelection() {
      if (this.allSelected) {
        this.selectedChapters = []
      } else {
        this.selectedChapters = this.searchResults.slice(0, 3).map(chapter => chapter.id)
      }
    },

    viewChapterDetails(chapter) {
      // Navigate to chapter detail view or open modal with detailed information
      this.$router.push(`/chapters/${chapter.id}`)
    },

    closeModal() {
      this.showCreateModal = false
      this.showEditModal = false
      this.showDeleteModal = false
      this.chapterForm = {
        id: null,
        name: '',
        universityName: '',
        city: '',
        state: '',
        foundedDate: '',
        description: ''
      }
    },

    toggleAnalytics() {
      this.showAnalytics = !this.showAnalytics
    }
  },

  computed: {
    isDarkMode() {
      return this.isDarkModeComputed
    }
  }
}
</script>

<style scoped>
.modal.show {
  animation: fadeIn 0.15s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Enhanced Chapter Management Styles */

/* Actions Bar */
.actions-bar {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 0.5rem;
  padding: 1rem;
  margin-bottom: 1.5rem;
}

.actions-bar .form-control,
.actions-bar .form-select {
  border-radius: 0.375rem;
}

/* Advanced Search */
.advanced-search-card {
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  background: #ffffff;
}

.advanced-search-card .card-header {
  background: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
}

.filter-row {
  display: flex;
  gap: 1rem;
  align-items: end;
  margin-bottom: 1rem;
}

.filter-row .form-group {
  flex: 1;
}

.range-inputs {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.range-inputs input {
  flex: 1;
}

/* Active Filters */
.active-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.filter-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: #e3f2fd;
  color: #1976d2;
  border: 1px solid #bbdefb;
  border-radius: 1rem;
  padding: 0.25rem 0.75rem;
  font-size: 0.875rem;
}

.filter-badge .btn-close {
  --bs-btn-close-bg: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16' fill='%231976d2'%3e%3cpath d='m.293.293.707.707L8 7.586 15.293 1.293l.707.707L8.707 9.5l7.293 7.293-.707.707L8 9.707.707 16A.707.707 0 010 15.293L7.293 8 .293.707A.707.707 0 01.293.293z'/%3e%3c/svg%3e");
  width: 0.75em;
  height: 0.75em;
}

/* Saved Searches */
.saved-searches {
  border-top: 1px solid #dee2e6;
  padding-top: 1rem;
  margin-top: 1rem;
}

.saved-search-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.saved-search-item button {
  border-radius: 0.375rem;
}

/* Comparison Mode */
.comparison-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 0.5rem;
  padding: 1rem;
  margin-bottom: 1rem;
}

/* Health Score Indicators */
.health-indicator {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.health-score-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 1rem;
  font-weight: 500;
  font-size: 0.75rem;
  color: white;
}

.health-score-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border: 3px solid #e9ecef;
}

.health-score-circle::before {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  border-radius: 50%;
  background: var(--bg, #e9ecef);
}

.health-score-circle span {
  position: relative;
  z-index: 1;
  font-weight: bold;
}

/* Sortable Table Headers */
.sortable-header {
  cursor: pointer;
  user-select: none;
  position: relative;
  padding-right: 1.5rem !important;
}

.sortable-header:hover {
  background-color: #f8f9fa;
}

.sort-icon {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0.5;
}

.sortable-header.active .sort-icon {
  opacity: 1;
}

/* Card View */
.chapter-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.chapter-card {
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  transition: all 0.2s ease;
  position: relative;
  background: white;
}

.chapter-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.chapter-card.selected {
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.card-selection-checkbox {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  z-index: 2;
}

.chapter-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0.75rem 0;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-value {
  font-size: 1.25rem;
  font-weight: 600;
  color: #495057;
}

.stat-label {
  font-size: 0.75rem;
  color: #6c757d;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Comparison Modal */
.comparison-metrics {
  border: 1px solid #e9ecef;
  border-radius: 0.375rem;
  overflow: hidden;
}

.metric-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0.75rem;
  border-bottom: 1px solid #f8f9fa;
}

.metric-row:last-child {
  border-bottom: none;
}

.metric-label {
  font-weight: 500;
  color: #495057;
}

.metric-value {
  font-weight: 600;
  color: #212529;
}

/* Performance Bars */
.performance-bars {
  background: #f8f9fa;
  border-radius: 0.375rem;
  padding: 0.75rem;
}

.performance-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.performance-item:last-child {
  margin-bottom: 0;
}

.performance-item span {
  min-width: 80px;
  font-size: 0.75rem;
}

.progress-sm {
  height: 4px;
  flex: 1;
  margin-left: 0.75rem;
}

/* View Toggle Buttons */
.view-toggle-btn {
  border: 1px solid #dee2e6;
  background: white;
  color: #6c757d;
  transition: all 0.2s ease;
}

.view-toggle-btn:hover {
  background: #f8f9fa;
  color: #495057;
}

.view-toggle-btn.active {
  background: #007bff;
  border-color: #007bff;
  color: white;
}

/* Responsive Design */
@media (max-width: 768px) {
  .actions-bar {
    padding: 0.75rem;
  }
  
  .filter-row {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .chapter-cards {
    grid-template-columns: 1fr;
  }
  
  .comparison-banner {
    text-align: center;
  }
  
  .health-score-circle {
    width: 60px;
    height: 60px;
  }
  
  .performance-item {
    flex-direction: column;
    align-items: stretch;
    gap: 0.25rem;
  }
  
  .performance-item .progress-sm {
    margin-left: 0;
  }
}

/* Health Score Colors */
.health-excellent { background-color: #28a745 !important; }
.health-good { background-color: #ffc107 !important; }
.health-fair { background-color: #fd7e14 !important; }
.health-poor { background-color: #dc3545 !important; }

/* Animation for loading states */
.loading-pulse {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}
</style>