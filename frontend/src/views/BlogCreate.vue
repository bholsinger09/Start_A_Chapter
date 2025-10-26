<template>
  <div class="container-fluid py-4">
    <!-- Header -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="display-5 fw-bold text-primary">
              <i class="bi bi-pencil-square me-3"></i>
              {{ isEditing ? 'Edit Post' : 'Write New Post' }}
            </h1>
            <p class="lead text-muted">Share your thoughts with the community</p>
          </div>
          <router-link to="/blog" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-2"></i>Back to Blog
          </router-link>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading && isEditing" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading post...</span>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
    </div>

    <!-- Blog Form -->
    <div v-else class="row">
      <div class="col-lg-8">
        <div class="card shadow-sm">
          <div class="card-body">
            <form @submit.prevent="saveBlog">
              <!-- Title -->
              <div class="mb-4">
                <label for="title" class="form-label fw-semibold">
                  <i class="bi bi-type me-2"></i>Title *
                </label>
                <input
                  type="text"
                  class="form-control form-control-lg"
                  id="title"
                  v-model="blog.title"
                  placeholder="Enter an engaging title for your post..."
                  required
                  maxlength="255"
                  :class="{ 'is-invalid': titleError }"
                >
                <div class="form-text">
                  {{ blog.title.length }}/255 characters
                </div>
                <div v-if="titleError" class="invalid-feedback">
                  {{ titleError }}
                </div>
              </div>

              <!-- Content -->
              <div class="mb-4">
                <label for="content" class="form-label fw-semibold">
                  <i class="bi bi-text-paragraph me-2"></i>Content *
                </label>
                <textarea
                  class="form-control"
                  id="content"
                  v-model="blog.content"
                  rows="15"
                  placeholder="Write your blog content here..."
                  required
                  :class="{ 'is-invalid': contentError }"
                ></textarea>
                <div class="form-text">
                  {{ blog.content.length.toLocaleString() }} characters
                </div>
                <div v-if="contentError" class="invalid-feedback">
                  {{ contentError }}
                </div>
              </div>

              <!-- Publishing Options -->
              <div class="mb-4">
                <div class="form-check form-switch">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    id="published"
                    v-model="blog.published"
                  >
                  <label class="form-check-label fw-semibold" for="published">
                    <i class="bi bi-eye me-2"></i>Publish immediately
                  </label>
                  <div class="form-text">
                    {{ blog.published ? 'This post will be visible to all users immediately.' : 'Save as draft - you can publish later.' }}
                  </div>
                </div>
              </div>

              <!-- Form Actions -->
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary me-2"
                    @click="previewBlog"
                  >
                    <i class="bi bi-eye me-2"></i>Preview
                  </button>
                  <button 
                    type="button" 
                    class="btn btn-outline-danger"
                    @click="resetForm"
                    v-if="!isEditing"
                  >
                    <i class="bi bi-arrow-clockwise me-2"></i>Reset
                  </button>
                </div>
                <div>
                  <button 
                    type="submit" 
                    class="btn btn-primary btn-lg"
                    :disabled="saving || !isFormValid"
                  >
                    <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                    <i v-else class="bi bi-check-circle me-2"></i>
                    {{ saving ? 'Saving...' : (isEditing ? 'Update Post' : 'Save Post') }}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- Sidebar -->
      <div class="col-lg-4">
        <!-- Writing Tips -->
        <div class="card shadow-sm mb-4">
          <div class="card-header bg-light">
            <h6 class="mb-0 fw-semibold">
              <i class="bi bi-lightbulb me-2"></i>Writing Tips
            </h6>
          </div>
          <div class="card-body">
            <ul class="list-unstyled mb-0">
              <li class="mb-2">
                <i class="bi bi-check2 text-success me-2"></i>
                Keep your title clear and engaging
              </li>
              <li class="mb-2">
                <i class="bi bi-check2 text-success me-2"></i>
                Break up long paragraphs for easier reading
              </li>
              <li class="mb-2">
                <i class="bi bi-check2 text-success me-2"></i>
                Use examples to illustrate your points
              </li>
              <li class="mb-2">
                <i class="bi bi-check2 text-success me-2"></i>
                Proofread before publishing
              </li>
              <li>
                <i class="bi bi-check2 text-success me-2"></i>
                Engage with commenters after posting
              </li>
            </ul>
          </div>
        </div>

        <!-- Author Info -->
        <div class="card shadow-sm" v-if="currentUser">
          <div class="card-header bg-light">
            <h6 class="mb-0 fw-semibold">
              <i class="bi bi-person me-2"></i>Author
            </h6>
          </div>
          <div class="card-body text-center">
            <div class="author-avatar mb-3">
              <i class="bi bi-person-circle display-4 text-secondary"></i>
            </div>
            <h6 class="fw-semibold">{{ currentUser.firstName }} {{ currentUser.lastName }}</h6>
            <p class="text-muted mb-2">@{{ currentUser.username }}</p>
            <small class="text-muted">{{ currentUser.role }}</small>
          </div>
        </div>
      </div>
    </div>

    <!-- Preview Modal -->
    <div class="modal fade" id="previewModal" tabindex="-1">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-eye me-2"></i>Preview
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="blog-preview">
              <div class="mb-3">
                <div class="d-flex align-items-center mb-2">
                  <i class="bi bi-person-circle fs-4 text-secondary me-2"></i>
                  <div>
                    <h6 class="mb-0" v-if="currentUser">
                      {{ currentUser.firstName }} {{ currentUser.lastName }}
                    </h6>
                    <small class="text-muted" v-if="currentUser">
                      @{{ currentUser.username }}
                    </small>
                  </div>
                </div>
              </div>
              
              <h1 class="mb-3">{{ blog.title || 'Untitled Post' }}</h1>
              
              <div class="blog-content" style="white-space: pre-wrap;">
                {{ blog.content || 'No content yet...' }}
              </div>
              
              <div class="mt-4 pt-3 border-top">
                <small class="text-muted">
                  <i class="bi bi-calendar3 me-1"></i>
                  {{ new Date().toLocaleDateString() }} •
                  <span v-if="blog.published" class="badge bg-success ms-2">
                    <i class="bi bi-eye me-1"></i>Published
                  </span>
                  <span v-else class="badge bg-warning text-dark ms-2">
                    <i class="bi bi-eye-slash me-1"></i>Draft
                  </span>
                </small>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              Close Preview
            </button>
            <button 
              type="button" 
              class="btn btn-primary" 
              @click="saveBlog"
              :disabled="saving || !isFormValid"
            >
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              {{ saving ? 'Saving...' : (isEditing ? 'Update Post' : 'Save Post') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { blogService } from '../services/blogService'

export default {
  name: 'BlogCreate',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    // Reactive data
    const blog = ref({
      title: '',
      content: '',
      published: false,
      author: null
    })
    
    const currentUser = ref(null)
    const loading = ref(false)
    const saving = ref(false)
    const error = ref('')
    const isEditing = ref(false)
    const editingBlogId = ref(null)

    // Validation
    const titleError = ref('')
    const contentError = ref('')

    // Computed properties
    const isFormValid = computed(() => {
      return blog.value.title.trim().length > 0 && 
             blog.value.content.trim().length > 0 &&
             !titleError.value && 
             !contentError.value
    })

    // Methods
    const checkAuthState = () => {
      try {
        const storedUser = localStorage.getItem('currentUser')
        if (storedUser) {
          currentUser.value = JSON.parse(storedUser)
          blog.value.author = { id: currentUser.value.id }
        } else {
          currentUser.value = null
          router.push('/login')
        }
      } catch (error) {
        console.error('Error parsing stored user:', error)
        currentUser.value = null
        router.push('/login')
      }
    }

    const validateForm = () => {
      titleError.value = ''
      contentError.value = ''

      if (!blog.value.title.trim()) {
        titleError.value = 'Title is required'
      } else if (blog.value.title.length > 255) {
        titleError.value = 'Title cannot exceed 255 characters'
      }

      if (!blog.value.content.trim()) {
        contentError.value = 'Content is required'
      } else if (blog.value.content.length < 10) {
        contentError.value = 'Content should be at least 10 characters long'
      }

      return !titleError.value && !contentError.value
    }

    const loadBlogForEdit = async (blogId) => {
      loading.value = true
      error.value = ''
      
      try {
        const result = await blogService.getBlogById(blogId)
        
        if (result.success) {
          const blogData = result.data
          
          // Check if user can edit this blog
          if (blogData.author?.id !== currentUser.value?.id) {
            error.value = 'You do not have permission to edit this blog'
            return
          }
          
          blog.value = {
            title: blogData.title,
            content: blogData.content,
            published: blogData.published,
            author: blogData.author
          }
          
          isEditing.value = true
          editingBlogId.value = blogId
        } else {
          error.value = result.error
        }
      } catch (err) {
        error.value = 'Failed to load blog for editing'
        console.error('Error loading blog:', err)
      } finally {
        loading.value = false
      }
    }

    const saveBlog = async () => {
      if (!validateForm()) return

      saving.value = true
      
      try {
        let result
        
        if (isEditing.value) {
          result = await blogService.updateBlog(editingBlogId.value, {
            title: blog.value.title.trim(),
            content: blog.value.content.trim(),
            published: blog.value.published
          })
        } else {
          result = await blogService.createBlog({
            title: blog.value.title.trim(),
            content: blog.value.content.trim(),
            published: blog.value.published,
            author: blog.value.author
          })
        }
        
        if (result.success) {
          // Close preview modal if open
          const previewModal = document.getElementById('previewModal')
          if (previewModal) {
            const modal = bootstrap.Modal.getInstance(previewModal)
            if (modal) {
              modal.hide()
            }
          }
          
          // Redirect to blog detail or list
          if (result.data.blog && result.data.blog.id) {
            router.push(`/blog/${result.data.blog.id}`)
          } else {
            router.push('/blog')
          }
        } else {
          error.value = result.error
        }
      } catch (err) {
        error.value = 'Failed to save blog'
        console.error('Error saving blog:', err)
      } finally {
        saving.value = false
      }
    }

    const previewBlog = () => {
      if (!validateForm()) return
      
      const previewModal = new bootstrap.Modal(document.getElementById('previewModal'))
      previewModal.show()
    }

    const resetForm = () => {
      if (confirm('Are you sure you want to reset the form? All changes will be lost.')) {
        blog.value = {
          title: '',
          content: '',
          published: false,
          author: currentUser.value ? { id: currentUser.value.id } : null
        }
        titleError.value = ''
        contentError.value = ''
      }
    }

    // Watchers
    watch(() => blog.value.title, (newTitle) => {
      if (titleError.value && newTitle.trim()) {
        titleError.value = ''
      }
    })

    watch(() => blog.value.content, (newContent) => {
      if (contentError.value && newContent.trim()) {
        contentError.value = ''
      }
    })

    // Lifecycle
    onMounted(() => {
      checkAuthState()
      
      // Check if we're editing an existing blog
      const editId = route.query.edit
      if (editId) {
        loadBlogForEdit(parseInt(editId))
      }
    })

    return {
      blog,
      currentUser,
      loading,
      saving,
      error,
      isEditing,
      titleError,
      contentError,
      isFormValid,
      saveBlog,
      previewBlog,
      resetForm,
      validateForm
    }
  }
}
</script>

<style scoped>
.form-control:focus {
  border-color: var(--bs-primary);
  box-shadow: 0 0 0 0.25rem rgba(var(--bs-primary-rgb), 0.25);
}

.form-control-lg {
  font-size: 1.25rem;
  font-weight: 600;
}

.blog-preview {
  max-height: 70vh;
  overflow-y: auto;
}

.blog-content {
  line-height: 1.6;
  font-size: 1.1rem;
}

.author-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fc;
  margin: 0 auto;
}

textarea.form-control {
  resize: vertical;
  min-height: 200px;
}

.card {
  border: 1px solid #e3e6f0;
}

.form-check-input:checked {
  background-color: var(--bs-primary);
  border-color: var(--bs-primary);
}

@media (max-width: 768px) {
  .display-5 {
    font-size: 2rem;
  }
  
  .btn-lg {
    padding: 0.5rem 1rem;
    font-size: 1rem;
  }
}
</style>