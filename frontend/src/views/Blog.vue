<template>
  <div class="container-fluid py-4">
    <!-- Blog Header -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="display-4 fw-bold text-primary">
              <i class="bi bi-journal-text me-3"></i>Campus Blog
            </h1>
            <p class="lead text-muted">Share your thoughts and connect with fellow students</p>
          </div>
          <div v-if="currentUser" class="text-end">
            <router-link 
              to="/blog/create" 
              class="btn btn-primary btn-lg shadow-sm"
              style="background: linear-gradient(45deg, #0d6efd, #0a58ca); border: none;"
            >
              <i class="bi bi-pencil-square me-2"></i>Write New Post
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Search and Filters -->
    <div class="row mb-4">
      <div class="col-md-8">
        <div class="input-group">
          <span class="input-group-text">
            <i class="bi bi-search"></i>
          </span>
          <input
            type="text"
            class="form-control"
            placeholder="Search blog posts..."
            v-model="searchQuery"
            @keyup.enter="searchBlogs"
          >
          <button 
            class="btn btn-outline-secondary" 
            type="button" 
            @click="searchBlogs"
            :disabled="!searchQuery.trim()"
          >
            Search
          </button>
          <button 
            class="btn btn-outline-secondary" 
            type="button" 
            @click="clearSearch"
            v-if="isSearchActive"
          >
            Clear
          </button>
        </div>
      </div>
      <div class="col-md-4">
        <div class="btn-group w-100" role="group">
          <button 
            type="button" 
            class="btn btn-outline-primary"
            :class="{ active: viewMode === 'recent' }"
            @click="setViewMode('recent')"
          >
            <i class="bi bi-clock me-1"></i>Recent
          </button>
          <button 
            type="button" 
            class="btn btn-outline-primary"
            :class="{ active: viewMode === 'popular' }"
            @click="setViewMode('popular')"
          >
            <i class="bi bi-fire me-1"></i>Popular
          </button>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2">Loading blog posts...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
    </div>

    <!-- No Posts State -->
    <div v-else-if="blogs.length === 0 && !loading" class="text-center py-5">
      <i class="bi bi-journal-x display-1 text-muted"></i>
      <h3 class="mt-3 text-muted">No blog posts found</h3>
      <p class="text-muted">
        <span v-if="isSearchActive">Try adjusting your search terms.</span>
        <span v-else>Be the first to share your story!</span>
      </p>
      <router-link 
        v-if="currentUser && !isSearchActive" 
        to="/blog/create" 
        class="btn btn-primary"
      >
        <i class="bi bi-plus-circle me-2"></i>Write the First Post
      </router-link>
    </div>

    <!-- Blog Posts Grid -->
    <div v-else class="row">
      <div 
        v-for="blog in blogs" 
        :key="blog.id" 
        class="col-lg-6 col-xl-4 mb-4"
      >
        <BlogCard 
          :blog="blog" 
          @blog-updated="handleBlogUpdated"
          @blog-deleted="handleBlogDeleted"
        />
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="row mt-5">
      <div class="col-12">
        <nav aria-label="Blog pagination">
          <ul class="pagination justify-content-center">
            <li class="page-item" :class="{ disabled: currentPage === 0 }">
              <button 
                class="page-link" 
                @click="changePage(currentPage - 1)"
                :disabled="currentPage === 0"
              >
                <i class="bi bi-chevron-left"></i> Previous
              </button>
            </li>
            
            <li 
              v-for="page in visiblePages" 
              :key="page" 
              class="page-item"
              :class="{ active: page === currentPage }"
            >
              <button 
                class="page-link" 
                @click="changePage(page)"
              >
                {{ page + 1 }}
              </button>
            </li>
            
            <li class="page-item" :class="{ disabled: currentPage >= totalPages - 1 }">
              <button 
                class="page-link" 
                @click="changePage(currentPage + 1)"
                :disabled="currentPage >= totalPages - 1"
              >
                Next <i class="bi bi-chevron-right"></i>
              </button>
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <!-- Blog Statistics -->
    <div class="row mt-5">
      <div class="col-12">
        <div class="card bg-light">
          <div class="card-body text-center">
            <div class="row">
              <div class="col-md-4">
                <h4 class="text-primary">{{ totalBlogs.toLocaleString() }}</h4>
                <p class="text-muted mb-0">Total Posts</p>
              </div>
              <div class="col-md-4">
                <h4 class="text-primary">{{ recentBlogs.length }}</h4>
                <p class="text-muted mb-0">This Week</p>
              </div>
              <div class="col-md-4">
                <h4 class="text-primary">{{ activeAuthors }}</h4>
                <p class="text-muted mb-0">Active Authors</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Floating Action Button for Mobile -->
    <router-link 
      v-if="currentUser"
      to="/blog/create" 
      class="btn btn-primary btn-lg position-fixed d-md-none floating-action-btn"
      style="bottom: 20px; right: 20px; z-index: 1000; border-radius: 50%; width: 60px; height: 60px; display: flex; align-items: center; justify-content: center; box-shadow: 0 4px 12px rgba(13, 110, 253, 0.3);"
      title="Write a new post"
    >
      <i class="bi bi-plus-lg fs-4"></i>
    </router-link>
  </div>
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue'
import { blogService } from '../services/blogService'
import BlogCard from '../components/BlogCard.vue'

export default {
  name: 'Blog',
  components: {
    BlogCard
  },
  setup() {
    // Reactive data
    const blogs = ref([])
    const recentBlogs = ref([])
    const loading = ref(true)
    const error = ref('')
    const searchQuery = ref('')
    const isSearchActive = ref(false)
    const viewMode = ref('recent')
    
    // Pagination
    const currentPage = ref(0)
    const totalPages = ref(0)
    const totalElements = ref(0)
    const pageSize = 6

    // Stats
    const totalBlogs = ref(0)
    const activeAuthors = ref(0)

    // Auth
    const currentUser = ref(null)

    // Computed properties
    const visiblePages = computed(() => {
      const pages = []
      const start = Math.max(0, currentPage.value - 2)
      const end = Math.min(totalPages.value - 1, currentPage.value + 2)
      
      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      return pages
    })

    // Methods
    const checkAuthState = () => {
      try {
        const storedUser = localStorage.getItem('currentUser')
        if (storedUser) {
          currentUser.value = JSON.parse(storedUser)
        } else {
          currentUser.value = null
        }
      } catch (error) {
        console.error('Error parsing stored user:', error)
        currentUser.value = null
      }
    }

    const loadBlogs = async (page = 0) => {
      loading.value = true
      error.value = ''
      
      try {
        const result = await blogService.getAllBlogs(page, pageSize)
        
        if (result.success) {
          blogs.value = result.data.blogs || []
          currentPage.value = result.data.currentPage || 0
          totalPages.value = result.data.totalPages || 0
          totalElements.value = result.data.totalElements || 0
        } else {
          error.value = result.error
          blogs.value = []
        }
      } catch (err) {
        error.value = 'Failed to load blogs'
        console.error('Error loading blogs:', err)
      } finally {
        loading.value = false
      }
    }

    const loadRecentBlogs = async () => {
      try {
        const result = await blogService.getRecentBlogs(7)
        if (result.success) {
          recentBlogs.value = result.data
        }
      } catch (err) {
        console.error('Error loading recent blogs:', err)
      }
    }

    const loadPopularBlogs = async (page = 0) => {
      loading.value = true
      error.value = ''
      
      try {
        const result = await blogService.getPopularBlogs(pageSize)
        
        if (result.success) {
          blogs.value = result.data
          // Reset pagination for popular view since it's a simple list
          currentPage.value = 0
          totalPages.value = 1
          totalElements.value = result.data.length
        } else {
          error.value = result.error
          blogs.value = []
        }
      } catch (err) {
        error.value = 'Failed to load popular blogs'
        console.error('Error loading popular blogs:', err)
      } finally {
        loading.value = false
      }
    }

    const loadBlogStats = async () => {
      try {
        const result = await blogService.getBlogStats()
        if (result.success) {
          totalBlogs.value = result.data.totalPublishedBlogs || 0
        }
      } catch (err) {
        console.error('Error loading blog stats:', err)
      }
    }

    const searchBlogs = async () => {
      if (!searchQuery.value.trim()) return
      
      loading.value = true
      error.value = ''
      isSearchActive.value = true
      
      try {
        const result = await blogService.searchBlogs(searchQuery.value.trim())
        
        if (result.success) {
          blogs.value = result.data
          // Reset pagination for search results
          currentPage.value = 0
          totalPages.value = 1
          totalElements.value = result.data.length
        } else {
          error.value = result.error
          blogs.value = []
        }
      } catch (err) {
        error.value = 'Failed to search blogs'
        console.error('Error searching blogs:', err)
      } finally {
        loading.value = false
      }
    }

    const clearSearch = () => {
      searchQuery.value = ''
      isSearchActive.value = false
      setViewMode('recent')
    }

    const setViewMode = (mode) => {
      viewMode.value = mode
      currentPage.value = 0
      
      if (mode === 'recent') {
        loadBlogs(0)
      } else if (mode === 'popular') {
        loadPopularBlogs(0)
      }
    }

    const changePage = (page) => {
      if (page >= 0 && page < totalPages.value && !isSearchActive.value) {
        currentPage.value = page
        
        if (viewMode.value === 'recent') {
          loadBlogs(page)
        } else if (viewMode.value === 'popular') {
          loadPopularBlogs(page)
        }
      }
    }

    const handleBlogUpdated = (updatedBlog) => {
      const index = blogs.value.findIndex(blog => blog.id === updatedBlog.id)
      if (index !== -1) {
        blogs.value[index] = updatedBlog
      }
    }

    const handleBlogDeleted = (deletedBlogId) => {
      blogs.value = blogs.value.filter(blog => blog.id !== deletedBlogId)
      totalBlogs.value = Math.max(0, totalBlogs.value - 1)
    }

    // Watchers
    watch(() => currentUser.value, () => {
      // Reload data when authentication state changes
      loadBlogs(currentPage.value)
    })

    // Lifecycle
    onMounted(() => {
      checkAuthState()
      loadBlogs()
      loadRecentBlogs()
      loadBlogStats()
      
      // Calculate active authors from unique authors in blogs
      // This is a simple approximation - in a real app you'd get this from the backend
      setTimeout(() => {
        const uniqueAuthors = new Set(blogs.value.map(blog => blog.author?.id).filter(id => id))
        activeAuthors.value = uniqueAuthors.size
      }, 1000)
    })

    return {
      blogs,
      recentBlogs,
      loading,
      error,
      searchQuery,
      isSearchActive,
      viewMode,
      currentPage,
      totalPages,
      totalElements,
      totalBlogs,
      activeAuthors,
      currentUser,
      visiblePages,
      loadBlogs,
      searchBlogs,
      clearSearch,
      setViewMode,
      changePage,
      handleBlogUpdated,
      handleBlogDeleted
    }
  }
}
</script>

<style scoped>
.card {
  transition: transform 0.2s ease-in-out;
}

.card:hover {
  transform: translateY(-2px);
}

.btn-group .btn.active {
  background-color: var(--bs-primary);
  border-color: var(--bs-primary);
  color: white;
}

.pagination .page-link {
  border: 1px solid #dee2e6;
  margin: 0 2px;
  border-radius: 0.375rem;
}

.pagination .page-item.active .page-link {
  background-color: var(--bs-primary);
  border-color: var(--bs-primary);
}

.display-1 {
  font-size: 4rem;
  opacity: 0.3;
}

@media (max-width: 768px) {
  .btn-group {
    display: flex;
    width: 100%;
  }
  
  .btn-group .btn {
    flex: 1;
  }
}

.floating-action-btn {
  transition: all 0.3s ease;
  background: linear-gradient(45deg, #0d6efd, #0a58ca) !important;
  border: none !important;
}

.floating-action-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(13, 110, 253, 0.4) !important;
}
</style>