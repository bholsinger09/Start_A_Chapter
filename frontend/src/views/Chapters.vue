<template>
  <div class="chapters">
    <div class="container-fluid">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col-md-8">
          <h2>
            <i class="bi bi-building me-2"></i>
            Campus Chapters
          </h2>
          <p class="text-muted">Explore student organizations across universities.</p>
        </div>
        <div class="col-md-4 text-end">
          <button class="btn btn-primary" @click="navigateToCreateChapter">
            <i class="bi bi-plus-circle me-2"></i>
            Create Chapter
          </button>
        </div>
      </div>

      <!-- Search and Filters -->
      <div class="row mb-4">
        <div class="col-md-6">
          <div class="input-group">
            <span class="input-group-text">
              <i class="bi bi-search"></i>
            </span>
            <input 
              type="text" 
              class="form-control" 
              placeholder="Search chapters..."
              v-model="searchTerm"
            >
          </div>
        </div>
        <div class="col-md-6">
          <select class="form-select" v-model="selectedState">
            <option value="">All States</option>
            <option v-for="state in availableStates" :key="state" :value="state">
              {{ state }}
            </option>
          </select>
        </div>
      </div>

      <!-- Loading State -->
      <div class="row" v-if="loading">
        <div class="col-12">
          <div class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-3 text-muted">Loading chapters...</p>
          </div>
        </div>
      </div>

      <!-- Chapters Grid -->
      <div class="row" v-else-if="filteredChapters.length > 0">
        <div class="col-md-6 col-lg-4 mb-4" v-for="chapter in filteredChapters" :key="chapter.id">
          <div class="card h-100 chapter-card">
            <div class="card-body">
              <h5 class="card-title">{{ chapter.name }}</h5>
              <h6 class="card-subtitle mb-2 text-muted">
                <i class="bi bi-geo-alt me-1"></i>
                {{ chapter.universityName }}
              </h6>
              <p class="card-text">
                <small class="text-muted">
                  <i class="bi bi-pin-map me-1"></i>
                  {{ chapter.city }}, {{ chapter.state }}
                </small>
              </p>
              <div class="d-flex justify-content-between align-items-end">
                <div>
                  <span class="badge bg-primary me-1">Active</span>
                  <small class="text-muted">
                    Founded {{ formatDate(chapter.createdAt) }}
                  </small>
                </div>
                <button class="btn btn-outline-primary btn-sm" disabled>
                  <i class="bi bi-info-circle me-1"></i>
                  View Details
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- No Results -->
      <div class="row" v-else>
        <div class="col-12">
          <div class="text-center py-5">
            <i class="bi bi-search display-1 text-muted mb-3"></i>
            <h4 class="text-muted">No chapters found</h4>
            <p class="text-muted">
              {{ 
                searchTerm || selectedState 
                  ? 'Try adjusting your search criteria.' 
                  : 'No chapters available at the moment.' 
              }}
            </p>
          </div>
        </div>
      </div>

      <!-- Stats -->
      <div class="row mt-5" v-if="chapters.length > 0">
        <div class="col-12">
          <div class="card bg-light">
            <div class="card-body">
              <div class="row text-center">
                <div class="col-md-3">
                  <h4 class="text-primary">{{ chapters.length }}</h4>
                  <p class="text-muted mb-0">Total Chapters</p>
                </div>
                <div class="col-md-3">
                  <h4 class="text-success">{{ availableStates.length }}</h4>
                  <p class="text-muted mb-0">States Represented</p>
                </div>
                <div class="col-md-3">
                  <h4 class="text-info">{{ uniqueUniversities }}</h4>
                  <p class="text-muted mb-0">Universities</p>
                </div>
                <div class="col-md-3">
                  <h4 class="text-warning">Active</h4>
                  <p class="text-muted mb-0">Status</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

export default {
  name: 'Chapters',
  setup() {
    const chapters = ref([])
    const loading = ref(true)
    const searchTerm = ref('')
    const selectedState = ref('')
    const router = useRouter()

    // Load chapters from API
    const loadChapters = async () => {
      try {
        loading.value = true
        const response = await api.get('/api/chapters')
        chapters.value = response.data
      } catch (error) {
        console.error('Error loading chapters:', error)
      } finally {
        loading.value = false
      }
    }

    // Computed properties for filtering and stats
    const filteredChapters = computed(() => {
      let filtered = chapters.value

      if (searchTerm.value) {
        const search = searchTerm.value.toLowerCase()
        filtered = filtered.filter(chapter => 
          chapter.name.toLowerCase().includes(search) ||
          chapter.universityName.toLowerCase().includes(search) ||
          chapter.city.toLowerCase().includes(search)
        )
      }

      if (selectedState.value) {
        filtered = filtered.filter(chapter => 
          chapter.state === selectedState.value
        )
      }

      return filtered
    })

    const availableStates = computed(() => {
      const states = [...new Set(chapters.value.map(chapter => chapter.state))]
      return states.sort()
    })

    const uniqueUniversities = computed(() => {
      const universities = new Set(chapters.value.map(chapter => chapter.universityName))
      return universities.size
    })

    // Utility functions
    const formatDate = (dateString) => {
      if (!dateString) return 'Unknown'
      try {
        return new Date(dateString).getFullYear()
      } catch {
        return 'Unknown'
      }
    }

    // Navigation function
    const navigateToCreateChapter = () => {
      router.push('/chapters/create')
    }

    // Load data on component mount
    onMounted(() => {
      loadChapters()
    })

    return {
      chapters,
      loading,
      searchTerm,
      selectedState,
      filteredChapters,
      availableStates,
      uniqueUniversities,
      formatDate,
      navigateToCreateChapter
    }
  }
}
</script>

<style scoped>
.chapter-card {
  transition: transform 0.2s ease-in-out;
  border: none;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.chapter-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.card {
  border-radius: 0.5rem;
}

.display-1 {
  font-size: 4rem;
}

.input-group-text {
  border-radius: 0.375rem 0 0 0.375rem;
}

.badge {
  font-size: 0.7rem;
}
</style>
