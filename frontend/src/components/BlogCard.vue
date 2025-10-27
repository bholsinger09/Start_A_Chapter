<template>
  <div class="card h-100 blog-card">
    <div class="card-header border-0 bg-transparent">
      <div class="d-flex justify-content-between align-items-start">
        <div class="d-flex align-items-center">
          <div class="author-avatar me-2">
            <i class="bi bi-person-circle fs-4 text-secondary"></i>
          </div>
          <div>
            <h6 class="mb-0 fw-semibold">{{ blog.author?.firstName }} {{ blog.author?.lastName }}</h6>
            <small class="text-muted">@{{ blog.author?.username }}</small>
          </div>
        </div>
        <div class="dropdown" v-if="canEditBlog">
          <button 
            class="btn btn-link btn-sm text-muted" 
            type="button" 
            data-bs-toggle="dropdown"
          >
            <i class="bi bi-three-dots"></i>
          </button>
          <ul class="dropdown-menu dropdown-menu-end">
            <li>
              <button class="dropdown-item" @click="editBlog">
                <i class="bi bi-pencil me-2"></i>Edit
              </button>
            </li>
            <li>
              <button 
                class="dropdown-item" 
                @click="togglePublishStatus"
                v-if="blog.published"
              >
                <i class="bi bi-eye-slash me-2"></i>Unpublish
              </button>
              <button 
                class="dropdown-item" 
                @click="togglePublishStatus"
                v-else
              >
                <i class="bi bi-eye me-2"></i>Publish
              </button>
            </li>
            <li><hr class="dropdown-divider"></li>
            <li>
              <button class="dropdown-item text-danger" @click="deleteBlog">
                <i class="bi bi-trash me-2"></i>Delete
              </button>
            </li>
          </ul>
        </div>
      </div>
    </div>
    
    <div class="card-body">
      <h5 class="card-title">
        <router-link 
          :to="`/blog/${blog.id}`" 
          class="text-decoration-none text-dark"
        >
          {{ blog.title }}
        </router-link>
      </h5>
      
      <p class="card-text text-muted">
        {{ truncatedContent }}
        <span v-if="isContentTruncated">...</span>
      </p>
      
      <div class="blog-meta d-flex justify-content-between align-items-center text-muted">
        <small>
          <i class="bi bi-calendar3 me-1"></i>
          {{ formatDate(blog.createdAt) }}
        </small>
        <div class="d-flex align-items-center">
          <small class="me-3">
            <i class="bi bi-chat me-1"></i>
            {{ commentCount }} {{ commentCount === 1 ? 'comment' : 'comments' }}
          </small>
          <small v-if="blog.updatedAt && blog.updatedAt !== blog.createdAt">
            <i class="bi bi-pencil-square me-1"></i>
            Updated
          </small>
        </div>
      </div>
    </div>
    
    <div class="card-footer border-0 bg-transparent">
      <div class="d-flex justify-content-between align-items-center">
        <router-link 
          :to="`/blog/${blog.id}`" 
          class="btn btn-outline-primary btn-sm"
        >
          <i class="bi bi-arrow-right me-1"></i>Read More
        </router-link>
        
        <div class="blog-status">
          <span 
            v-if="!blog.published" 
            class="badge bg-warning text-dark"
          >
            <i class="bi bi-eye-slash me-1"></i>Draft
          </span>
          <span 
            v-else 
            class="badge bg-success"
          >
            <i class="bi bi-eye me-1"></i>Published
          </span>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div 
      class="modal fade" 
      :id="`deleteModal-${blog.id}`" 
      tabindex="-1"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Delete Blog Post</h5>
            <button 
              type="button" 
              class="btn-close" 
              data-bs-dismiss="modal"
            ></button>
          </div>
          <div class="modal-body">
            <p>Are you sure you want to delete "<strong>{{ blog.title }}</strong>"?</p>
            <p class="text-muted">This action cannot be undone.</p>
          </div>
          <div class="modal-footer">
            <button 
              type="button" 
              class="btn btn-secondary" 
              data-bs-dismiss="modal"
            >
              Cancel
            </button>
            <button 
              type="button" 
              class="btn btn-danger" 
              @click="confirmDelete"
              :disabled="deleting"
            >
              <span v-if="deleting" class="spinner-border spinner-border-sm me-2"></span>
              {{ deleting ? 'Deleting...' : 'Delete' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { blogService } from '../services/blogService'

export default {
  name: 'BlogCard',
  props: {
    blog: {
      type: Object,
      required: true
    }
  },
  emits: ['blog-updated', 'blog-deleted'],
  setup(props, { emit }) {
    const router = useRouter()
    const currentUser = ref(null)
    const commentCount = ref(0)
    const deleting = ref(false)
    const publishing = ref(false)

    // Computed properties
    const canEditBlog = computed(() => {
      return currentUser.value && 
             currentUser.value.id === props.blog.author?.id
    })

    const truncatedContent = computed(() => {
      if (!props.blog.content) return ''
      const maxLength = 150
      return props.blog.content.length > maxLength 
        ? props.blog.content.substring(0, maxLength)
        : props.blog.content
    })

    const isContentTruncated = computed(() => {
      return props.blog.content && props.blog.content.length > 150
    })

    // Methods
    const checkAuthState = () => {
      try {
        const storedUser = localStorage.getItem('user')
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

    const loadCommentCount = async () => {
      try {
        const result = await blogService.getCommentCount(props.blog.id)
        if (result.success) {
          commentCount.value = result.data.count || 0
        }
      } catch (error) {
        console.error('Error loading comment count:', error)
        commentCount.value = 0
      }
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      
      try {
        const date = new Date(dateString)
        const now = new Date()
        const diffInHours = (now - date) / (1000 * 60 * 60)
        
        if (diffInHours < 24) {
          return date.toLocaleTimeString('en-US', {
            hour: 'numeric',
            minute: '2-digit',
            hour12: true
          })
        } else if (diffInHours < 24 * 7) {
          return date.toLocaleDateString('en-US', {
            weekday: 'short',
            month: 'short',
            day: 'numeric'
          })
        } else {
          return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
          })
        }
      } catch (error) {
        console.error('Error formatting date:', error)
        return 'Invalid date'
      }
    }

    const editBlog = () => {
      router.push(`/blog/create?edit=${props.blog.id}`)
    }

    const togglePublishStatus = async () => {
      publishing.value = true
      
      try {
        let result
        if (props.blog.published) {
          result = await blogService.unpublishBlog(props.blog.id)
        } else {
          result = await blogService.publishBlog(props.blog.id)
        }
        
        if (result.success) {
          emit('blog-updated', result.data.blog)
        } else {
          alert(result.error || 'Failed to update blog status')
        }
      } catch (error) {
        console.error('Error toggling publish status:', error)
        alert('Failed to update blog status')
      } finally {
        publishing.value = false
      }
    }

    const deleteBlog = () => {
      // Show delete confirmation modal
      const modalElement = document.getElementById(`deleteModal-${props.blog.id}`)
      if (modalElement) {
        const modal = new bootstrap.Modal(modalElement)
        modal.show()
      }
    }

    const confirmDelete = async () => {
      deleting.value = true
      
      try {
        const result = await blogService.deleteBlog(props.blog.id)
        
        if (result.success) {
          // Hide modal
          const modalElement = document.getElementById(`deleteModal-${props.blog.id}`)
          if (modalElement) {
            const modal = bootstrap.Modal.getInstance(modalElement)
            if (modal) {
              modal.hide()
            }
          }
          
          emit('blog-deleted', props.blog.id)
        } else {
          alert(result.error || 'Failed to delete blog')
        }
      } catch (error) {
        console.error('Error deleting blog:', error)
        alert('Failed to delete blog')
      } finally {
        deleting.value = false
      }
    }

    // Lifecycle
    onMounted(() => {
      checkAuthState()
      loadCommentCount()
    })

    return {
      currentUser,
      commentCount,
      deleting,
      publishing,
      canEditBlog,
      truncatedContent,
      isContentTruncated,
      formatDate,
      editBlog,
      togglePublishStatus,
      deleteBlog,
      confirmDelete
    }
  }
}
</script>

<style scoped>
.blog-card {
  transition: all 0.3s ease;
  border: 1px solid #e3e6f0;
}

.blog-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fc;
}

.card-title a:hover {
  color: var(--bs-primary) !important;
}

.blog-meta {
  font-size: 0.875rem;
}

.card-footer {
  padding: 1rem;
}

.dropdown-toggle::after {
  display: none;
}

.btn-link {
  border: none;
  background: none;
  padding: 0.25rem 0.5rem;
}

.btn-link:hover {
  color: var(--bs-primary);
}

.badge {
  font-size: 0.75rem;
  padding: 0.375rem 0.75rem;
}
</style>