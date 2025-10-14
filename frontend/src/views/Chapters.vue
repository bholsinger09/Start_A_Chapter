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

      <!-- Actions Bar -->
      <div class="row mb-4">
        <div class="col-md-4">
          <button 
            class="btn btn-primary" 
            @click="showCreateModal = true"
          >
            <i class="bi bi-plus-circle me-2"></i>
            Add New Chapter
          </button>
        </div>
        <div class="col-md-4">
          <div class="input-group">
            <span class="input-group-text">
              <i class="bi bi-search"></i>
            </span>
            <input
              type="text"
              class="form-control"
              placeholder="Search chapters..."
              v-model="searchTerm"
              @input="performSearch"
            >
          </div>
        </div>
        <div class="col-md-4">
          <div class="input-group">
            <span class="input-group-text">
              <i class="bi bi-geo-alt"></i>
            </span>
            <select 
              class="form-select" 
              v-model="selectedState"
              @change="performSearch"
            >
              <option value="">All States</option>
              <option v-for="state in availableStates" :key="state" :value="state">
                {{ state }}
              </option>
            </select>
          </div>
        </div>
      </div>

      <!-- Advanced Search Card -->
      <div class="row mb-4" v-if="showAdvancedSearch">
        <div class="col">
          <div class="card">
            <div class="card-header bg-light">
              <h6 class="card-title mb-0">
                <i class="bi bi-funnel me-2"></i>
                Advanced Search Filters
              </h6>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-3">
                  <label class="form-label">University Name</label>
                  <input
                    type="text"
                    class="form-control"
                    placeholder="University name..."
                    v-model="advancedFilters.university"
                    @input="performSearch"
                  >
                </div>
                <div class="col-md-3">
                  <label class="form-label">City</label>
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
                  <label class="form-label">Status</label>
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
                <div class="col-md-3 d-flex align-items-end">
                  <button 
                    class="btn btn-outline-secondary w-100"
                    @click="clearFilters"
                  >
                    <i class="bi bi-x-circle me-1"></i>
                    Clear Filters
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Search Toggle -->
      <div class="row mb-3">
        <div class="col">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <span v-if="searchResults.length !== chapters.length" class="text-muted">
                Showing {{ searchResults.length }} of {{ chapters.length }} chapters
                <span v-if="selectedState" class="badge bg-primary ms-2">
                  State: {{ selectedState }}
                </span>
                <span v-if="searchTerm" class="badge bg-info ms-2">
                  Search: "{{ searchTerm }}"
                </span>
              </span>
            </div>
            <button 
              class="btn btn-sm btn-outline-secondary"
              @click="showAdvancedSearch = !showAdvancedSearch"
            >
              <i class="bi bi-funnel me-1"></i>
              {{ showAdvancedSearch ? 'Hide' : 'Show' }} Advanced Filters
            </button>
          </div>
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
          <div v-else class="table-responsive">
            <table class="table table-hover mb-0">
              <thead class="table-light">
                <tr>
                  <th>Chapter Name</th>
                  <th>University</th>
                  <th>Location</th>
                  <th>Founded</th>
                  <th>Members</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="chapter in searchResults" :key="chapter.id">
                  <td>
                    <strong>{{ chapter.name }}</strong>
                  </td>
                  <td>{{ chapter.universityName }}</td>
                  <td>{{ chapter.city }}, {{ chapter.state }}</td>
                  <td>{{ formatDate(chapter.foundedDate) }}</td>
                  <td>
                    <span class="badge bg-info">
                      {{ getMemberCount(chapter.id) }}
                    </span>
                  </td>
                  <td>
                    <div class="btn-group" role="group">
                      <router-link 
                        :to="`/chapters/${chapter.id}`" 
                        class="btn btn-sm btn-outline-primary"
                      >
                        <i class="bi bi-eye"></i>
                      </router-link>
                      <button 
                        class="btn btn-sm btn-outline-secondary"
                        @click="editChapter(chapter)"
                      >
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button 
                        class="btn btn-sm btn-outline-danger"
                        @click="confirmDelete(chapter)"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
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

    <!-- Modal Backdrop -->
    <div 
      v-if="showCreateModal || showEditModal || showDeleteModal"
      class="modal-backdrop fade show"
      @click="closeModal"
    ></div>
  </div>
</template>

<script>
import { chapterService } from '../services/chapterService'
import { memberService } from '../services/memberService'

export default {
  name: 'Chapters',
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
      }
    }
  },
  computed: {
    hasActiveFilters() {
      return this.searchTerm || 
             this.selectedState || 
             this.advancedFilters.university || 
             this.advancedFilters.city || 
             this.advancedFilters.active !== ''
    }
  },
  async mounted() {
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
        
        // If no search parameters, show all chapters
        if (Object.keys(searchParams).length === 0) {
          this.searchResults = this.chapters
          return
        }
        
        // Perform backend search
        this.searchResults = await chapterService.searchChapters(searchParams)
        
      } catch (error) {
        console.error('Error searching chapters:', error)
        // Fallback to local filtering if backend search fails
        this.performLocalSearch()
      }
    },

    performLocalSearch() {
      let results = this.chapters
      
      if (this.searchTerm) {
        const term = this.searchTerm.toLowerCase()
        results = results.filter(chapter =>
          chapter.name.toLowerCase().includes(term) ||
          chapter.universityName.toLowerCase().includes(term)
        )
      }
      
      if (this.selectedState) {
        results = results.filter(chapter => chapter.state === this.selectedState)
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
      this.searchResults = this.chapters
    },
    getMemberCount(chapterId) {
      return this.members.filter(member => member.chapter?.id === chapterId).length
    },
    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
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
</style>