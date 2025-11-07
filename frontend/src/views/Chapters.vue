<template>
  <div class="container-fluid py-4">
    <!-- Header -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="display-4 fw-bold text-primary">
              <i class="bi bi-building me-3"></i>Chapters
            </h1>
            <p class="lead text-muted">Manage and view all campus chapters</p>
          </div>
          <router-link to="/chapters/create" class="btn btn-primary btn-lg">
            <i class="bi bi-plus-circle me-2"></i>Create Chapter
          </router-link>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading chapters...</span>
      </div>
      <p class="mt-3">Loading chapters...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
    </div>

    <!-- No Chapters State -->
    <div v-else-if="chapters.length === 0" class="text-center py-5">
      <i class="bi bi-building display-1 text-muted opacity-25"></i>
      <h3 class="mt-3 text-muted">No chapters found</h3>
      <p class="text-muted">Get started by creating your first chapter</p>
      <router-link to="/chapters/create" class="btn btn-primary btn-lg">
        <i class="bi bi-plus-circle me-2"></i>Create Your First Chapter
      </router-link>
    </div>

    <!-- Chapters List -->
    <div v-else class="row">
      <div v-for="chapter in chapters" :key="chapter.id" class="col-lg-4 col-md-6 mb-4">
        <div class="card h-100 chapter-card">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start mb-3">
              <h5 class="card-title mb-0">{{ chapter.name }}</h5>
              <span v-if="chapter.active" class="badge bg-success">Active</span>
              <span v-else class="badge bg-secondary">Inactive</span>
            </div>
            
            <div class="mb-3">
              <p class="text-muted mb-1">
                <i class="bi bi-mortarboard me-2"></i>{{ chapter.universityName }}
              </p>
              <p class="text-muted mb-1">
                <i class="bi bi-geo-alt me-2"></i>{{ chapter.city }}, {{ chapter.state }}
              </p>
              <p v-if="chapter.description" class="card-text">{{ chapter.description }}</p>
            </div>
            
            <div class="row text-center">
              <div class="col-4">
                <div class="border-end">
                  <strong class="d-block">{{ chapter.members ? chapter.members.length : 0 }}</strong>
                  <small class="text-muted">Members</small>
                </div>
              </div>
              <div class="col-4">
                <div class="border-end">
                  <strong class="d-block">{{ chapter.events ? chapter.events.length : 0 }}</strong>
                  <small class="text-muted">Events</small>
                </div>
              </div>
              <div class="col-4">
                <strong class="d-block text-success">{{ formatDate(chapter.createdAt) }}</strong>
                <small class="text-muted">Founded</small>
              </div>
            </div>
          </div>
          
          <div class="card-footer bg-transparent">
            <div class="d-flex gap-2">
              <button class="btn btn-outline-primary btn-sm flex-fill" @click="viewChapter(chapter)">
                <i class="bi bi-eye me-1"></i>View
              </button>
              <button class="btn btn-outline-secondary btn-sm flex-fill" @click="editChapter(chapter)">
                <i class="bi bi-pencil me-1"></i>Edit
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Chapters',
  data() {
    return {
      chapters: [],
      loading: true,
      error: ''
    }
  },
  async mounted() {
    await this.loadChapters()
  },
  methods: {
    async loadChapters() {
      this.loading = true
      this.error = ''
      
      try {
        const response = await fetch('/api/chapters')
        
        if (response.ok) {
          this.chapters = await response.json()
          console.log('✅ Loaded chapters:', this.chapters)
        } else {
          this.error = `Failed to load chapters (Status: ${response.status})`
        }
      } catch (error) {
        console.error('Error loading chapters:', error)
        this.error = 'Network error. Please check your connection and try again.'
      } finally {
        this.loading = false
      }
    },
    
    viewChapter(chapter) {
      console.log('Viewing chapter:', chapter)
      // Future: Navigate to chapter detail page
      alert(`Viewing chapter: ${chapter.name}`)
    },
    
    editChapter(chapter) {
      console.log('Editing chapter:', chapter)
      // Future: Navigate to edit page or show edit modal
      alert(`Editing chapter: ${chapter.name}`)
    },
    
    formatDate(dateString) {
      if (!dateString) return 'N/A'
      
      try {
        return new Date(dateString).toLocaleDateString('en-US', {
          year: 'numeric',
          month: 'short',
          day: 'numeric'
        })
      } catch (error) {
        return 'N/A'
      }
    }
  }
}
</script>

<style scoped>
.chapter-card {
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 0.125rem 0.5rem rgba(0, 0, 0, 0.1);
}

.chapter-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.opacity-25 {
  opacity: 0.25;
}

.display-1 {
  font-size: 6rem;
}

.border-end {
  border-right: 1px solid #dee2e6;
}

.card-footer {
  padding: 1rem;
}
</style>
