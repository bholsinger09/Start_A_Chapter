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

      <!-- Enhanced Search and Filter Controls -->
      <EnhancedSearch
        :chapters="allChapters"
        :available-states="availableStates"
        @search="handleSearch"
        @search-results="handleSearchResults"
        @filter-change="handleFilterChange"
      />

      <!-- Stats Row -->
      <div class="row mb-3">
        <div class="col-md-6">
          <div class="d-flex align-items-center">
            <button 
              @click="openAddModal" 
              class="btn btn-primary me-3"
            >
              <i class="bi bi-plus-circle me-2"></i>
              Add Chapter
            </button>
            <button 
              @click="toggleView" 
              class="btn btn-outline-secondary me-3"
            >
              <i :class="currentView === 'table' ? 'bi bi-grid-3x3-gap' : 'bi bi-table'" class="me-2"></i>
              {{ currentView === 'table' ? 'Card View' : 'Table View' }}
            </button>
            <button 
              @click="exportToCSV" 
              class="btn btn-outline-success"
            >
              <i class="bi bi-download me-2"></i>
              Export CSV
            </button>
          </div>
        </div>
        <div class="col-md-6 text-end">
          <div class="chapter-stats">
            <span class="badge bg-primary fs-6 me-2">
              Total: {{ filteredChapters.length }}
            </span>
            <span class="badge bg-success fs-6 me-2">
              Active: {{ activeChapters }}
            </span>
            <span class="badge bg-warning fs-6">
              Avg Health: {{ averageHealth }}%
            </span>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-4">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="text-muted mt-2">Loading chapters...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="alert alert-danger text-center">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>
        {{ error }}
        <button @click="loadChapters" class="btn btn-outline-danger btn-sm ms-2">
          <i class="bi bi-arrow-clockwise me-1"></i>
          Retry
        </button>
      </div>

      <!-- Table View -->
      <div v-else-if="currentView === 'table'" class="table-responsive">
        <table class="table table-hover">
          <thead class="table-light">
            <tr>
              <th @click="sortBy('name')" class="sortable">
                Name 
                <i class="bi" :class="getSortIcon('name')"></i>
              </th>
              <th @click="sortBy('university')" class="sortable">
                University 
                <i class="bi" :class="getSortIcon('university')"></i>
              </th>
              <th @click="sortBy('state')" class="sortable">
                State 
                <i class="bi" :class="getSortIcon('state')"></i>
              </th>
              <th @click="sortBy('memberCount')" class="sortable">
                Members 
                <i class="bi" :class="getSortIcon('memberCount')"></i>
              </th>
              <th @click="sortBy('healthScore')" class="sortable">
                Health 
                <i class="bi" :class="getSortIcon('healthScore')"></i>
              </th>
              <th @click="sortBy('status')" class="sortable">
                Status 
                <i class="bi" :class="getSortIcon('status')"></i>
              </th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="chapter in paginatedChapters" :key="chapter.id" class="chapter-row">
              <td>
                <div class="d-flex align-items-center">
                  <div class="chapter-avatar me-2">
                    {{ chapter.name.charAt(0) }}
                  </div>
                  <strong>{{ chapter.name }}</strong>
                </div>
              </td>
              <td>{{ chapter.university }}</td>
              <td>
                <span class="badge bg-light text-dark">{{ chapter.state }}</span>
              </td>
              <td>
                <div class="d-flex align-items-center">
                  <i class="bi bi-people me-1 text-muted"></i>
                  {{ chapter.memberCount || 0 }}
                </div>
              </td>
              <td>
                <div class="health-score" :class="getHealthClass(chapter.healthScore)">
                  <div class="health-bar">
                    <div class="health-fill" :style="{ width: chapter.healthScore + '%' }"></div>
                  </div>
                  <small>{{ chapter.healthScore || 0 }}%</small>
                </div>
              </td>
              <td>
                <span 
                  class="badge" 
                  :class="{
                    'bg-success': chapter.status === 'Active',
                    'bg-warning': chapter.status === 'Inactive',
                    'bg-danger': chapter.status === 'Suspended',
                    'bg-secondary': !chapter.status || chapter.status === 'Unknown'
                  }"
                >
                  {{ chapter.status || 'Unknown' }}
                </span>
              </td>
              <td>
                <div class="btn-group btn-group-sm">
                  <button 
                    @click="viewChapter(chapter)"
                    class="btn btn-outline-primary"
                    title="View Details"
                  >
                    <i class="bi bi-eye"></i>
                  </button>
                  <button 
                    @click="editChapter(chapter)"
                    class="btn btn-outline-secondary"
                    title="Edit Chapter"
                  >
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button 
                    @click="deleteChapter(chapter)"
                    class="btn btn-outline-danger"
                    title="Delete Chapter"
                  >
                    <i class="bi bi-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination -->
        <nav v-if="totalPages > 1" class="d-flex justify-content-center mt-4">
          <ul class="pagination">
            <li class="page-item" :class="{ disabled: currentPage === 1 }">
              <button @click="currentPage = 1" class="page-link">First</button>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === 1 }">
              <button @click="currentPage--" class="page-link">Previous</button>
            </li>
            <li 
              v-for="page in getVisiblePages()" 
              :key="page"
              class="page-item" 
              :class="{ active: page === currentPage }"
            >
              <button @click="currentPage = page" class="page-link">{{ page }}</button>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === totalPages }">
              <button @click="currentPage++" class="page-link">Next</button>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === totalPages }">
              <button @click="currentPage = totalPages" class="page-link">Last</button>
            </li>
          </ul>
        </nav>
      </div>

      <!-- Card View -->
      <div v-else class="row">
        <div v-for="chapter in paginatedChapters" :key="chapter.id" class="col-md-6 col-lg-4 mb-4">
          <div class="card chapter-card h-100">
            <div class="card-header d-flex justify-content-between align-items-center">
              <div class="chapter-avatar-large">
                {{ chapter.name.charAt(0) }}
              </div>
              <span 
                class="badge" 
                :class="{
                  'bg-success': chapter.status === 'Active',
                  'bg-warning': chapter.status === 'Inactive',
                  'bg-danger': chapter.status === 'Suspended',
                  'bg-secondary': !chapter.status || chapter.status === 'Unknown'
                }"
              >
                {{ chapter.status || 'Unknown' }}
              </span>
            </div>
            <div class="card-body">
              <h5 class="card-title">{{ chapter.name }}</h5>
              <p class="card-text text-muted">{{ chapter.university }}</p>
              <div class="mb-2">
                <small class="text-muted">
                  <i class="bi bi-geo-alt me-1"></i>
                  {{ chapter.city }}, {{ chapter.state }}
                </small>
              </div>
              <div class="row text-center mb-3">
                <div class="col-6">
                  <div class="stat-item">
                    <div class="stat-value">{{ chapter.memberCount || 0 }}</div>
                    <div class="stat-label">Members</div>
                  </div>
                </div>
                <div class="col-6">
                  <div class="stat-item">
                    <div class="stat-value">{{ chapter.healthScore || 0 }}%</div>
                    <div class="stat-label">Health</div>
                  </div>
                </div>
              </div>
              <div class="health-score mb-3" :class="getHealthClass(chapter.healthScore)">
                <div class="health-bar">
                  <div class="health-fill" :style="{ width: chapter.healthScore + '%' }"></div>
                </div>
              </div>
            </div>
            <div class="card-footer">
              <div class="btn-group w-100">
                <button 
                  @click="viewChapter(chapter)"
                  class="btn btn-outline-primary"
                >
                  <i class="bi bi-eye me-1"></i>
                  View
                </button>
                <button 
                  @click="editChapter(chapter)"
                  class="btn btn-outline-secondary"
                >
                  <i class="bi bi-pencil me-1"></i>
                  Edit
                </button>
                <button 
                  @click="deleteChapter(chapter)"
                  class="btn btn-outline-danger"
                >
                  <i class="bi bi-trash me-1"></i>
                  Delete
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Card View Pagination -->
        <nav v-if="totalPages > 1" class="col-12 d-flex justify-content-center mt-4">
          <ul class="pagination">
            <li class="page-item" :class="{ disabled: currentPage === 1 }">
              <button @click="currentPage = 1" class="page-link">First</button>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === 1 }">
              <button @click="currentPage--" class="page-link">Previous</button>
            </li>
            <li 
              v-for="page in getVisiblePages()" 
              :key="page"
              class="page-item" 
              :class="{ active: page === currentPage }"
            >
              <button @click="currentPage = page" class="page-link">{{ page }}</button>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === totalPages }">
              <button @click="currentPage++" class="page-link">Next</button>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === totalPages }">
              <button @click="currentPage = totalPages" class="page-link">Last</button>
            </li>
          </ul>
        </nav>
      </div>

      <!-- Empty State -->
      <div v-if="!loading && !error && filteredChapters.length === 0" class="text-center py-5">
        <i class="bi bi-building display-1 text-muted"></i>
        <h3 class="text-muted mt-3">No chapters found</h3>
        <p class="text-muted">Try adjusting your search criteria or add a new chapter.</p>
        <button @click="openAddModal" class="btn btn-primary">
          <i class="bi bi-plus-circle me-2"></i>
          Add Chapter
        </button>
      </div>

      <!-- Add Chapter Modal -->
      <div 
        class="modal fade" 
        :class="{ show: showAddModal }" 
        :style="{ display: showAddModal ? 'block' : 'none' }"
        tabindex="-1"
      >
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">
                <i class="bi bi-plus-circle me-2"></i>
                Add New Chapter
              </h5>
              <button 
                type="button" 
                class="btn-close" 
                @click="closeModal"
              ></button>
            </div>
            <div class="modal-body">
              <form @submit.prevent="saveChapter">
                <!-- Chapter Information -->
                <div class="row">
                  <div class="col-12 mb-3">
                    <h6 class="text-primary mb-3">Chapter Information</h6>
                  </div>
                  <div class="col-12 mb-3">
                    <label for="chapterName" class="form-label">Chapter Name *</label>
                    <input
                      type="text"
                      class="form-control"
                      id="chapterName"
                      v-model="chapterForm.name"
                      required
                      placeholder="e.g., Turning Point USA - University of Texas"
                    >
                  </div>
                  <div class="col-12 mb-3">
                    <label for="description" class="form-label">Description</label>
                    <textarea
                      class="form-control"
                      id="description"
                      v-model="chapterForm.description"
                      rows="3"
                      placeholder="Brief description of the chapter..."
                    ></textarea>
                  </div>
                </div>

                <!-- Institution Selection -->
                <div class="row">
                  <div class="col-12 mb-3">
                    <h6 class="text-primary mb-3">Institution Association</h6>
                  </div>
                  <div class="col-12 mb-3">
                    <div class="form-check form-check-inline">
                      <input
                        class="form-check-input"
                        type="radio"
                        name="institutionOption"
                        id="existingInstitution"
                        value="existing"
                        v-model="institutionOption"
                      >
                      <label class="form-check-label" for="existingInstitution">
                        Select Existing Institution
                      </label>
                    </div>
                    <div class="form-check form-check-inline">
                      <input
                        class="form-check-input"
                        type="radio"
                        name="institutionOption"
                        id="newInstitution"
                        value="new"
                        v-model="institutionOption"
                      >
                      <label class="form-check-label" for="newInstitution">
                        Add New Institution
                      </label>
                    </div>
                  </div>
                </div>

                <!-- Existing Institution Selection -->
                <div v-if="institutionOption === 'existing'" class="row mb-3">
                  <div class="col-12">
                    <label for="existingInstitutionSelect" class="form-label">Choose Institution *</label>
                    <select
                      class="form-select"
                      id="existingInstitutionSelect"
                      v-model="chapterForm.institutionId"
                      required
                    >
                      <option value="">Select an institution...</option>
                      <option v-for="institution in institutions" :key="institution.id" :value="institution.id">
                        {{ institution.name }} - {{ institution.city }}, {{ institution.state }}
                      </option>
                    </select>
                  </div>
                </div>

                <!-- New Institution Form -->
                <div v-if="institutionOption === 'new'" class="row">
                  <div class="col-12 mb-3">
                    <label class="form-label">Institution Type *</label>
                    <div class="form-check form-check-inline">
                      <input
                        class="form-check-input"
                        type="radio"
                        name="institutionType"
                        id="university"
                        value="UNIVERSITY"
                        v-model="newInstitution.institutionType"
                      >
                      <label class="form-check-label" for="university">
                        University
                      </label>
                    </div>
                    <div class="form-check form-check-inline">
                      <input
                        class="form-check-input"
                        type="radio"
                        name="institutionType"
                        id="church"
                        value="CHURCH"
                        v-model="newInstitution.institutionType"
                      >
                      <label class="form-check-label" for="church">
                        Church
                      </label>
                    </div>
                  </div>
                  
                  <!-- Basic Institution Info -->
                  <div class="col-md-6 mb-3">
                    <label for="institutionName" class="form-label">{{ newInstitution.institutionType === 'UNIVERSITY' ? 'University' : 'Church' }} Name *</label>
                    <input
                      type="text"
                      class="form-control"
                      id="institutionName"
                      v-model="newInstitution.name"
                      required
                      :placeholder="newInstitution.institutionType === 'UNIVERSITY' ? 'e.g., University of Texas at Austin' : 'e.g., First Baptist Church'"
                    >
                  </div>
                  <div class="col-md-6 mb-3">
                    <label for="institutionCity" class="form-label">City *</label>
                    <input
                      type="text"
                      class="form-control"
                      id="institutionCity"
                      v-model="newInstitution.city"
                      required
                      placeholder="e.g., Austin"
                    >
                  </div>
                  <div class="col-md-6 mb-3">
                    <label for="institutionState" class="form-label">State *</label>
                    <select
                      class="form-select"
                      id="institutionState"
                      v-model="newInstitution.state"
                      required
                    >
                      <option value="">Select State</option>
                      <option v-for="state in usStates" :key="state" :value="state">{{ state }}</option>
                    </select>
                  </div>
                  <div class="col-md-6 mb-3">
                    <label for="institutionZipCode" class="form-label">ZIP Code</label>
                    <input
                      type="text"
                      class="form-control"
                      id="institutionZipCode"
                      v-model="newInstitution.zipCode"
                      placeholder="e.g., 78712"
                    >
                  </div>
                  <div class="col-12 mb-3">
                    <label for="institutionAddress" class="form-label">Address</label>
                    <input
                      type="text"
                      class="form-control"
                      id="institutionAddress"
                      v-model="newInstitution.address"
                      placeholder="e.g., 110 Inner Campus Drive"
                    >
                  </div>

                  <!-- University-specific fields -->
                  <template v-if="newInstitution.institutionType === 'UNIVERSITY'">
                    <div class="col-md-6 mb-3">
                      <label for="universityType" class="form-label">University Type</label>
                      <select class="form-select" id="universityType" v-model="newInstitution.universityType">
                        <option value="">Select Type</option>
                        <option value="Public">Public</option>
                        <option value="Private">Private</option>
                        <option value="Community College">Community College</option>
                        <option value="Technical">Technical</option>
                      </select>
                    </div>
                    <div class="col-md-6 mb-3">
                      <label for="studentPopulation" class="form-label">Student Population</label>
                      <input
                        type="number"
                        class="form-control"
                        id="studentPopulation"
                        v-model="newInstitution.studentPopulation"
                        placeholder="e.g., 40000"
                      >
                    </div>
                  </template>

                  <!-- Church-specific fields -->
                  <template v-if="newInstitution.institutionType === 'CHURCH'">
                    <div class="col-md-6 mb-3">
                      <label for="denomination" class="form-label">Denomination</label>
                      <select class="form-select" id="denomination" v-model="newInstitution.denomination">
                        <option value="">Select Denomination</option>
                        <option value="Baptist">Baptist</option>
                        <option value="Methodist">Methodist</option>
                        <option value="Presbyterian">Presbyterian</option>
                        <option value="Catholic">Catholic</option>
                        <option value="Lutheran">Lutheran</option>
                        <option value="Episcopal">Episcopal</option>
                        <option value="Pentecostal">Pentecostal</option>
                        <option value="Non-denominational">Non-denominational</option>
                        <option value="Other">Other</option>
                      </select>
                    </div>
                    <div class="col-md-6 mb-3">
                      <label for="pastorName" class="form-label">Pastor Name</label>
                      <input
                        type="text"
                        class="form-control"
                        id="pastorName"
                        v-model="newInstitution.pastorName"
                        placeholder="e.g., Pastor John Smith"
                      >
                    </div>
                    <div class="col-md-6 mb-3">
                      <label for="membershipSize" class="form-label">Membership Size</label>
                      <input
                        type="number"
                        class="form-control"
                        id="membershipSize"
                        v-model="newInstitution.membershipSize"
                        placeholder="e.g., 500"
                      >
                    </div>
                    <div class="col-md-6 mb-3">
                      <label for="website" class="form-label">Website</label>
                      <input
                        type="url"
                        class="form-control"
                        id="website"
                        v-model="newInstitution.website"
                        placeholder="e.g., https://church.org"
                      >
                    </div>
                  </template>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button 
                type="button" 
                class="btn btn-secondary" 
                @click="closeModal"
              >
                Cancel
              </button>
              <button 
                type="button" 
                class="btn btn-primary" 
                @click="saveChapter"
                :disabled="saving"
              >
                <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                {{ saving ? 'Creating...' : 'Create Chapter' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal backdrop -->
      <div 
        v-if="showAddModal" 
        class="modal-backdrop fade show"
        @click="closeModal"
      ></div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import chapterService from '../services/chapterService.js'
import memberService from '../services/memberService.js'
import EnhancedSearch from '../components/EnhancedSearch.vue'

export default {
  name: 'Chapters',
  components: {
    EnhancedSearch
  },
  setup() {
    // Reactive data
    const chapters = ref([])
    const allChapters = ref([]) // Store all chapters for search component
    const filteredChapters = ref([])
    const loading = ref(true)
    const error = ref(null)
    const searchQuery = ref('')
    const selectedState = ref('')
    const selectedStatus = ref('')
    const currentView = ref('table')
    const searchResults = ref(null) // Store backend search results
    
    // Pagination
    const currentPage = ref(1)
    const itemsPerPage = ref(10)
    
    // Sorting
    const sortField = ref('name')
    const sortDirection = ref('asc')

    // Modal and form data
    const showAddModal = ref(false)
    const saving = ref(false)
    const institutions = ref([])
    const institutionOption = ref('existing')
    
    const chapterForm = ref({
      name: '',
      description: '',
      institutionId: null
    })
    
    const newInstitution = ref({
      institutionType: 'UNIVERSITY',
      name: '',
      state: '',
      city: '',
      zipCode: '',
      address: '',
      description: '',
      // University fields
      universityType: '',
      accreditation: '',
      studentPopulation: null,
      universityFoundedYear: null,
      // Church fields
      denomination: '',
      pastorName: '',
      membershipSize: null,
      churchFoundedYear: null,
      website: ''
    })
    
    // US States for dropdown
    const usStates = ref([
      'Alabama', 'Alaska', 'Arizona', 'Arkansas', 'California', 'Colorado', 'Connecticut', 'Delaware',
      'Florida', 'Georgia', 'Hawaii', 'Idaho', 'Illinois', 'Indiana', 'Iowa', 'Kansas', 'Kentucky',
      'Louisiana', 'Maine', 'Maryland', 'Massachusetts', 'Michigan', 'Minnesota', 'Mississippi',
      'Missouri', 'Montana', 'Nebraska', 'Nevada', 'New Hampshire', 'New Jersey', 'New Mexico',
      'New York', 'North Carolina', 'North Dakota', 'Ohio', 'Oklahoma', 'Oregon', 'Pennsylvania',
      'Rhode Island', 'South Carolina', 'South Dakota', 'Tennessee', 'Texas', 'Utah', 'Vermont',
      'Virginia', 'Washington', 'West Virginia', 'Wisconsin', 'Wyoming'
    ])

    // Computed properties
    const availableStates = computed(() => {
      const states = [...new Set(chapters.value.map(c => c.state).filter(Boolean))]
      return states.sort()
    })

    const activeChapters = computed(() => {
      return filteredChapters.value.filter(c => c.status === 'Active').length
    })

    const averageHealth = computed(() => {
      if (filteredChapters.value.length === 0) return 0
      const sum = filteredChapters.value.reduce((acc, c) => acc + (c.healthScore || 0), 0)
      return Math.round(sum / filteredChapters.value.length)
    })

    const paginatedChapters = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage.value
      const end = start + itemsPerPage.value
      return filteredChapters.value.slice(start, end)
    })

    const totalPages = computed(() => {
      return Math.ceil(filteredChapters.value.length / itemsPerPage.value)
    })

    // Methods
    const loadChapters = async () => {
      try {
        loading.value = true
        error.value = null
        
        console.log('Loading chapters from chapter service...')
        const response = await chapterService.getAllChapters()
        console.log('Chapters response:', response)
        
        if (response && Array.isArray(response)) {
          chapters.value = response
          allChapters.value = [...response] // Store original chapters for search component
          
          // Member counts are now included in the chapter data from backend
          console.log(`Loaded ${chapters.value.length} chapters`)
          
          // Update allChapters with the same data
          allChapters.value = [...chapters.value]
          filteredChapters.value = [...chapters.value]
          console.log(`Loaded ${chapters.value.length} chapters`)
        } else {
          throw new Error('Invalid response format')
        }
        
      } catch (err) {
        console.error('Error loading chapters:', err)
        error.value = 'Failed to load chapters. Please try again.'
      } finally {
        loading.value = false
      }
    }

    const applyFilters = () => {
      let filtered = [...chapters.value]

      // Text search
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filtered = filtered.filter(chapter => 
          chapter.name?.toLowerCase().includes(query) ||
          chapter.university?.toLowerCase().includes(query) ||
          chapter.city?.toLowerCase().includes(query) ||
          chapter.state?.toLowerCase().includes(query)
        )
      }

      // State filter
      if (selectedState.value) {
        filtered = filtered.filter(chapter => chapter.state === selectedState.value)
      }

      // Status filter
      if (selectedStatus.value) {
        filtered = filtered.filter(chapter => chapter.status === selectedStatus.value)
      }

      // Apply sorting
      filtered.sort((a, b) => {
        let aVal = a[sortField.value]
        let bVal = b[sortField.value]

        // Handle null/undefined values
        if (aVal == null) aVal = ''
        if (bVal == null) bVal = ''

        // Convert to string for comparison
        if (typeof aVal === 'string') {
          aVal = aVal.toLowerCase()
          bVal = String(bVal).toLowerCase()
          if (sortDirection.value === 'asc') {
            return aVal.localeCompare(bVal)
          } else {
            return bVal.localeCompare(aVal)
          }
        } else {
          // Numeric comparison
          if (sortDirection.value === 'asc') {
            return aVal - bVal
          } else {
            return bVal - aVal
          }
        }
      })

      filteredChapters.value = filtered
      currentPage.value = 1 // Reset to first page when filtering
    }

    const sortBy = (field) => {
      if (sortField.value === field) {
        sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
      } else {
        sortField.value = field
        sortDirection.value = 'asc'
      }
      applyFilters()
    }

    const getSortIcon = (field) => {
      if (sortField.value !== field) {
        return 'bi-chevron-expand'
      }
      return sortDirection.value === 'asc' ? 'bi-chevron-up' : 'bi-chevron-down'
    }

    const toggleView = () => {
      currentView.value = currentView.value === 'table' ? 'cards' : 'table'
    }

    const getHealthClass = (score) => {
      if (!score) return 'health-poor'
      if (score >= 80) return 'health-excellent'
      if (score >= 60) return 'health-good'
      if (score >= 40) return 'health-fair'
      return 'health-poor'
    }

    const getVisiblePages = () => {
      const pages = []
      const maxVisible = 5
      const start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2))
      const end = Math.min(totalPages.value, start + maxVisible - 1)

      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      return pages
    }

    const exportToCSV = () => {
      const headers = ['Name', 'University', 'City', 'State', 'Status', 'Members', 'Health Score']
      const csvContent = [
        headers.join(','),
        ...filteredChapters.value.map(chapter => [
          `"${chapter.name || ''}"`,
          `"${chapter.university || ''}"`,
          `"${chapter.city || ''}"`,
          `"${chapter.state || ''}"`,
          `"${chapter.status || ''}"`,
          chapter.memberCount || 0,
          chapter.healthScore || 0
        ].join(','))
      ].join('\n')

      const blob = new Blob([csvContent], { type: 'text/csv' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `chapters_${new Date().toISOString().split('T')[0]}.csv`
      link.click()
      URL.revokeObjectURL(url)
    }

    // CRUD operations (placeholders)
    const viewChapter = (chapter) => {
      console.log('View chapter:', chapter)
      // TODO: Implement view modal
    }

    const editChapter = (chapter) => {
      console.log('Edit chapter:', chapter)
      // TODO: Implement edit modal
    }

    const deleteChapter = (chapter) => {
      console.log('Delete chapter:', chapter)
      // TODO: Implement delete confirmation
    }

    // Enhanced search handlers
    const handleSearch = (searchParams) => {
      console.log('Search triggered:', searchParams)
      
      // If we have backend search results, use them
      if (searchResults.value && searchResults.value.chapters) {
        filteredChapters.value = searchResults.value.chapters
        currentPage.value = 1
        return
      }
      
      // Fallback to local search
      searchQuery.value = searchParams.query || ''
      selectedState.value = searchParams.state || ''
      selectedStatus.value = searchParams.active === 'true' ? 'Active' : 
                              searchParams.active === 'false' ? 'Inactive' : ''
      applyFilters()
    }

    const handleSearchResults = (results) => {
      console.log('Search results received:', results)
      searchResults.value = results
      
      if (results && results.chapters) {
        filteredChapters.value = results.chapters
        currentPage.value = 1
      } else {
        // Reset to show all chapters if no results
        filteredChapters.value = [...chapters.value]
      }
    }

    const handleFilterChange = (filters) => {
      console.log('Filters changed:', filters)
      // The enhanced search component will handle the filtering
      // This is just for monitoring filter changes
    }

    // Modal methods
    const loadInstitutions = async () => {
      try {
        const response = await chapterService.getAllInstitutions()
        institutions.value = response || []
      } catch (error) {
        console.error('Error loading institutions:', error)
        institutions.value = []
      }
    }

    const openAddModal = () => {
      // Reset form
      chapterForm.value = {
        name: '',
        description: '',
        institutionId: null
      }
      
      newInstitution.value = {
        institutionType: 'UNIVERSITY',
        name: '',
        state: '',
        city: '',
        zipCode: '',
        address: '',
        description: '',
        universityType: '',
        accreditation: '',
        studentPopulation: null,
        universityFoundedYear: null,
        denomination: '',
        pastorName: '',
        membershipSize: null,
        churchFoundedYear: null,
        website: ''
      }
      
      institutionOption.value = 'existing'
      showAddModal.value = true
      
      // Load institutions for selection
      loadInstitutions()
    }

    const closeModal = () => {
      showAddModal.value = false
      saving.value = false
    }

    const saveChapter = async () => {
      try {
        saving.value = true
        
        const requestData = {
          name: chapterForm.value.name,
          description: chapterForm.value.description
        }

        if (institutionOption.value === 'existing') {
          if (!chapterForm.value.institutionId) {
            alert('Please select an institution')
            return
          }
          requestData.institutionId = chapterForm.value.institutionId
        } else {
          if (!newInstitution.value.name || !newInstitution.value.state || !newInstitution.value.city) {
            alert('Please fill in all required institution fields')
            return
          }
          requestData.newInstitution = { ...newInstitution.value }
        }

        const createdChapter = await chapterService.createChapterWithInstitution(requestData)
        
        // Reload chapters to include the new one
        await loadChapters()
        
        closeModal()
        alert('Chapter created successfully!')
        
      } catch (error) {
        console.error('Error creating chapter:', error)
        alert('Error creating chapter. Please try again.')
      } finally {
        saving.value = false
      }
    }

    // Watchers
    watch([sortField, sortDirection], () => {
      applyFilters()
    })

    // Lifecycle
    onMounted(() => {
      loadChapters()
    })

    return {
      // Data
      chapters,
      allChapters,
      filteredChapters,
      loading,
      error,
      searchQuery,
      selectedState,
      selectedStatus,
      currentView,
      currentPage,
      itemsPerPage,
      sortField,
      sortDirection,
      searchResults,

      // Computed
      availableStates,
      activeChapters,
      averageHealth,
      paginatedChapters,
      totalPages,

      // Methods
      loadChapters,
      applyFilters,
      sortBy,
      getSortIcon,
      toggleView,
      getHealthClass,
      getVisiblePages,
      exportToCSV,
      viewChapter,
      editChapter,
      deleteChapter,
      handleSearch,
      handleSearchResults,
      handleFilterChange,
      
      // Modal methods and data
      showAddModal,
      saving,
      institutions,
      institutionOption,
      chapterForm,
      newInstitution,
      usStates,
      openAddModal,
      closeModal,
      saveChapter
    }
  }
}
</script>

<style scoped>
.chapters {
  padding: 2rem 0;
}

.chapter-stats .badge {
  margin: 0 0.25rem;
}

.sortable {
  cursor: pointer;
  user-select: none;
  transition: all 0.2s ease;
}

.sortable:hover {
  background-color: var(--bs-gray-100);
}

.chapter-row {
  transition: all 0.2s ease;
}

.chapter-row:hover {
  background-color: var(--bs-gray-50);
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.chapter-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007bff, #0056b3);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.9rem;
}

.chapter-avatar-large {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007bff, #0056b3);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.2rem;
}

.health-score {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.health-bar {
  width: 60px;
  height: 8px;
  background-color: var(--bs-gray-200);
  border-radius: 4px;
  overflow: hidden;
}

.health-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.health-excellent .health-fill {
  background: linear-gradient(90deg, #28a745, #20c997);
}

.health-good .health-fill {
  background: linear-gradient(90deg, #ffc107, #28a745);
}

.health-fair .health-fill {
  background: linear-gradient(90deg, #fd7e14, #ffc107);
}

.health-poor .health-fill {
  background: linear-gradient(90deg, #dc3545, #fd7e14);
}

.chapter-card {
  transition: all 0.3s ease;
  border: 1px solid var(--bs-border-color);
}

.chapter-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
  border-color: var(--bs-primary);
}

.stat-item {
  padding: 0.5rem;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--bs-primary);
}

.stat-label {
  font-size: 0.8rem;
  color: var(--bs-secondary);
  text-transform: uppercase;
}

.btn-group .btn {
  flex: 1;
}

.pagination {
  --bs-pagination-active-bg: var(--bs-primary);
  --bs-pagination-active-border-color: var(--bs-primary);
}

.table th {
  border-top: none;
  font-weight: 600;
  color: var(--bs-gray-700);
}

/* Dark mode support */
@media (prefers-color-scheme: dark) {
  .chapter-row:hover {
    background-color: var(--bs-gray-800);
  }
  
  .sortable:hover {
    background-color: var(--bs-gray-800);
  }
  
  .chapter-card {
    background-color: var(--bs-gray-900);
    border-color: var(--bs-gray-700);
  }
  
  .chapter-card:hover {
    border-color: var(--bs-primary);
    background-color: var(--bs-gray-800);
  }
}

/* Loading animation */
@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.spinner-border {
  animation: pulse 1.5s ease-in-out infinite;
}

/* Responsive design */
@media (max-width: 768px) {
  .chapter-stats {
    text-align: center !important;
    margin-top: 1rem;
  }
  
  .btn-group {
    flex-direction: column;
  }
  
  .btn-group .btn {
    border-radius: 0.375rem !important;
    margin-bottom: 0.25rem;
  }
  
  .table-responsive {
    font-size: 0.875rem;
  }
  
  .chapter-avatar {
    width: 24px;
    height: 24px;
    font-size: 0.7rem;
  }
}
</style>
