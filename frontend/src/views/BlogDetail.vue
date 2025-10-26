<template>
  <div class="container-fluid py-4">
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading post...</span>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
      <div class="mt-2">
        <router-link to="/blog" class="btn btn-outline-primary">
          <i class="bi bi-arrow-left me-2"></i>Back to Blog
        </router-link>
      </div>
    </div>

    <!-- Blog Content -->
    <div v-else-if="blog" class="row">
      <div class="col-lg-8">
        <!-- Blog Header -->
        <div class="card shadow-sm mb-4">
          <div class="card-header border-0 bg-white">
            <div class="d-flex justify-content-between align-items-start">
              <router-link to="/blog" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-arrow-left me-2"></i>Back to Blog
              </router-link>
              
              <div class="dropdown" v-if="canEditBlog">
                <button 
                  class="btn btn-outline-secondary btn-sm" 
                  type="button" 
                  data-bs-toggle="dropdown"
                >
                  <i class="bi bi-three-dots"></i>
                </button>
                <ul class="dropdown-menu dropdown-menu-end">
                  <li>
                    <router-link 
                      :to="`/blog/create?edit=${blog.id}`" 
                      class="dropdown-item"
                    >
                      <i class="bi bi-pencil me-2"></i>Edit Post
                    </router-link>
                  </li>
                  <li>
                    <button 
                      class="dropdown-item" 
                      @click="togglePublishStatus"
                    >
                      <i :class="blog.published ? 'bi bi-eye-slash' : 'bi bi-eye'" class="me-2"></i>
                      {{ blog.published ? 'Unpublish' : 'Publish' }}
                    </button>
                  </li>
                  <li><hr class="dropdown-divider"></li>
                  <li>
                    <button class="dropdown-item text-danger" @click="deleteBlog">
                      <i class="bi bi-trash me-2"></i>Delete Post
                    </button>
                  </li>
                </ul>
              </div>
            </div>
          </div>
          
          <div class="card-body">
            <!-- Blog Title -->
            <h1 class="display-5 fw-bold mb-4">{{ blog.title }}</h1>
            
            <!-- Author Info and Meta -->
            <div class="d-flex align-items-center mb-4 pb-3 border-bottom">
              <div class="author-avatar me-3">
                <i class="bi bi-person-circle fs-3 text-secondary"></i>
              </div>
              <div class="flex-grow-1">
                <h6 class="mb-1 fw-semibold">
                  {{ blog.author?.firstName }} {{ blog.author?.lastName }}
                </h6>
                <div class="text-muted small">
                  <span class="me-3">@{{ blog.author?.username }}</span>
                  <span class="me-3">
                    <i class="bi bi-calendar3 me-1"></i>
                    {{ formatDate(blog.createdAt) }}
                  </span>
                  <span v-if="blog.updatedAt && blog.updatedAt !== blog.createdAt" class="me-3">
                    <i class="bi bi-pencil-square me-1"></i>
                    Updated {{ formatDate(blog.updatedAt) }}
                  </span>
                  <span>
                    <i class="bi bi-chat me-1"></i>
                    {{ comments.length }} {{ comments.length === 1 ? 'comment' : 'comments' }}
                  </span>
                </div>
              </div>
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
            
            <!-- Blog Content -->
            <div class="blog-content" style="white-space: pre-wrap; line-height: 1.7; font-size: 1.1rem;">
              {{ blog.content }}
            </div>
          </div>
        </div>

        <!-- Comments Section -->
        <div class="card shadow-sm">
          <div class="card-header">
            <h5 class="mb-0 fw-semibold">
              <i class="bi bi-chat-dots me-2"></i>
              Comments ({{ comments.length }})
            </h5>
          </div>
          
          <!-- Add Comment Form (Only for authenticated users) -->
          <div v-if="currentUser && blog.published" class="card-body border-bottom">
            <form @submit.prevent="addComment">
              <div class="mb-3">
                <label for="newComment" class="form-label fw-semibold">Add a comment</label>
                <textarea
                  id="newComment"
                  class="form-control"
                  rows="3"
                  v-model="newComment"
                  placeholder="Share your thoughts..."
                  maxlength="1000"
                  required
                ></textarea>
                <div class="form-text">
                  {{ newComment.length }}/1000 characters
                </div>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <small class="text-muted">
                  Posting as {{ currentUser.firstName }} {{ currentUser.lastName }}
                </small>
                <button 
                  type="submit" 
                  class="btn btn-primary"
                  :disabled="!newComment.trim() || addingComment"
                >
                  <span v-if="addingComment" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="bi bi-send me-2"></i>
                  {{ addingComment ? 'Posting...' : 'Post Comment' }}
                </button>
              </div>
            </form>
          </div>

          <!-- Login prompt for unauthenticated users -->
          <div v-else-if="!currentUser && blog.published" class="card-body border-bottom bg-light">
            <div class="text-center">
              <p class="mb-2 text-muted">
                <i class="bi bi-info-circle me-2"></i>
                Please log in to join the conversation
              </p>
              <router-link to="/login" class="btn btn-outline-primary btn-sm">
                <i class="bi bi-box-arrow-in-right me-2"></i>Log In
              </router-link>
            </div>
          </div>

          <!-- Comments List -->
          <div class="card-body p-0">
            <div v-if="comments.length === 0" class="text-center py-4 text-muted">
              <i class="bi bi-chat display-4 opacity-25"></i>
              <p class="mt-2">No comments yet. Be the first to share your thoughts!</p>
            </div>
            
            <div v-else class="comments-list">
              <div 
                v-for="(comment, index) in comments" 
                :key="comment.id"
                class="comment-item p-4"
                :class="{ 'border-bottom': index < comments.length - 1 }"
              >
                <div class="d-flex align-items-start">
                  <div class="author-avatar me-3">
                    <i class="bi bi-person-circle fs-5 text-secondary"></i>
                  </div>
                  <div class="flex-grow-1">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                      <div>
                        <h6 class="mb-0 fw-semibold">
                          {{ comment.author?.firstName }} {{ comment.author?.lastName }}
                        </h6>
                        <small class="text-muted">
                          @{{ comment.author?.username }} • {{ formatDate(comment.createdAt) }}
                          <span v-if="comment.updatedAt && comment.updatedAt !== comment.createdAt">
                            • edited
                          </span>
                        </small>
                      </div>
                      
                      <!-- Comment actions for comment author or blog author -->
                      <div class="dropdown" v-if="canEditComment(comment)">
                        <button 
                          class="btn btn-link btn-sm text-muted p-0" 
                          type="button" 
                          data-bs-toggle="dropdown"
                        >
                          <i class="bi bi-three-dots"></i>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end">
                          <li v-if="comment.author?.id === currentUser?.id">
                            <button 
                              class="dropdown-item" 
                              @click="editComment(comment)"
                            >
                              <i class="bi bi-pencil me-2"></i>Edit
                            </button>
                          </li>
                          <li>
                            <button 
                              class="dropdown-item text-danger" 
                              @click="deleteComment(comment)"
                            >
                              <i class="bi bi-trash me-2"></i>Delete
                            </button>
                          </li>
                        </ul>
                      </div>
                    </div>
                    
                    <!-- Comment content -->
                    <div 
                      v-if="editingComment?.id !== comment.id"
                      class="comment-content"
                      style="white-space: pre-wrap;"
                    >
                      {{ comment.content }}
                    </div>
                    
                    <!-- Edit comment form -->
                    <div v-else class="edit-comment-form">
                      <textarea
                        class="form-control mb-2"
                        rows="3"
                        v-model="editCommentContent"
                        maxlength="1000"
                      ></textarea>
                      <div class="d-flex justify-content-end gap-2">
                        <button 
                          type="button" 
                          class="btn btn-sm btn-outline-secondary"
                          @click="cancelEditComment"
                        >
                          Cancel
                        </button>
                        <button 
                          type="button" 
                          class="btn btn-sm btn-primary"
                          @click="saveEditComment"
                          :disabled="!editCommentContent.trim() || updatingComment"
                        >
                          <span v-if="updatingComment" class="spinner-border spinner-border-sm me-1"></span>
                          {{ updatingComment ? 'Saving...' : 'Save' }}
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

      <!-- Sidebar -->
      <div class="col-lg-4">
        <!-- Author Info -->
        <div class="card shadow-sm mb-4">
          <div class="card-header">
            <h6 class="mb-0 fw-semibold">
              <i class="bi bi-person me-2"></i>About the Author
            </h6>
          </div>
          <div class="card-body text-center">
            <div class="author-avatar mb-3 mx-auto">
              <i class="bi bi-person-circle display-4 text-secondary"></i>
            </div>
            <h6 class="fw-semibold">{{ blog.author?.firstName }} {{ blog.author?.lastName }}</h6>
            <p class="text-muted mb-2">@{{ blog.author?.username }}</p>
            <small class="text-muted">{{ blog.author?.role }}</small>
            <hr>
            <div class="row text-center">
              <div class="col">
                <h6 class="text-primary">{{ authorStats.totalPosts }}</h6>
                <small class="text-muted">Posts</small>
              </div>
              <div class="col">
                <h6 class="text-primary">{{ authorStats.totalComments }}</h6>
                <small class="text-muted">Comments</small>
              </div>
            </div>
          </div>
        </div>

        <!-- Recent Posts by Author -->
        <div class="card shadow-sm" v-if="recentPosts.length > 0">
          <div class="card-header">
            <h6 class="mb-0 fw-semibold">
              <i class="bi bi-journal-text me-2"></i>More from {{ blog.author?.firstName }}
            </h6>
          </div>
          <div class="card-body p-0">
            <div 
              v-for="post in recentPosts" 
              :key="post.id"
              class="p-3 border-bottom"
            >
              <router-link 
                :to="`/blog/${post.id}`" 
                class="text-decoration-none"
              >
                <h6 class="mb-1 fw-semibold text-dark">{{ post.title }}</h6>
                <small class="text-muted">
                  <i class="bi bi-calendar3 me-1"></i>
                  {{ formatDate(post.createdAt) }}
                </small>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Blog Confirmation Modal -->
    <div class="modal fade" id="deleteBlogModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Delete Blog Post</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p>Are you sure you want to delete "<strong>{{ blog?.title }}</strong>"?</p>
            <p class="text-muted">This action cannot be undone.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              Cancel
            </button>
            <button 
              type="button" 
              class="btn btn-danger" 
              @click="confirmDeleteBlog"
              :disabled="deletingBlog"
            >
              <span v-if="deletingBlog" class="spinner-border spinner-border-sm me-2"></span>
              {{ deletingBlog ? 'Deleting...' : 'Delete' }}
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
  name: 'BlogDetail',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    // Reactive data
    const blog = ref(null)
    const comments = ref([])
    const recentPosts = ref([])
    const currentUser = ref(null)
    const loading = ref(true)
    const error = ref('')
    
    // Comment management
    const newComment = ref('')
    const addingComment = ref(false)
    const editingComment = ref(null)
    const editCommentContent = ref('')
    const updatingComment = ref(false)
    const deletingBlog = ref(false)
    
    // Author stats
    const authorStats = ref({
      totalPosts: 0,
      totalComments: 0
    })

    // Computed properties
    const canEditBlog = computed(() => {
      return currentUser.value && 
             blog.value && 
             currentUser.value.id === blog.value.author?.id
    })

    const canEditComment = (comment) => {
      return currentUser.value && (
        currentUser.value.id === comment.author?.id || 
        currentUser.value.id === blog.value?.author?.id
      )
    }

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

    const loadBlog = async () => {
      loading.value = true
      error.value = ''
      
      try {
        const blogId = route.params.id
        const result = await blogService.getBlogById(blogId)
        
        if (result.success) {
          blog.value = result.data
          await loadComments()
          await loadAuthorStats()
          await loadRecentPosts()
        } else {
          error.value = result.error
        }
      } catch (err) {
        error.value = 'Failed to load blog post'
        console.error('Error loading blog:', err)
      } finally {
        loading.value = false
      }
    }

    const loadComments = async () => {
      if (!blog.value) return
      
      try {
        const result = await blogService.getCommentsByBlogList(blog.value.id)
        if (result.success) {
          comments.value = result.data
        }
      } catch (error) {
        console.error('Error loading comments:', error)
      }
    }

    const loadAuthorStats = async () => {
      if (!blog.value?.author) return
      
      try {
        const [postsResult, commentsResult] = await Promise.all([
          blogService.getBlogsByAuthor(blog.value.author.id),
          blogService.getCommentsByAuthor(blog.value.author.id)
        ])
        
        authorStats.value = {
          totalPosts: postsResult.success ? postsResult.data.blogs?.length || 0 : 0,
          totalComments: commentsResult.success ? commentsResult.data?.length || 0 : 0
        }
      } catch (error) {
        console.error('Error loading author stats:', error)
      }
    }

    const loadRecentPosts = async () => {
      if (!blog.value?.author) return
      
      try {
        const result = await blogService.getBlogsByAuthor(blog.value.author.id, 0, 5)
        if (result.success) {
          // Filter out current blog and limit to 3 posts
          recentPosts.value = (result.data.blogs || [])
            .filter(post => post.id !== blog.value.id)
            .slice(0, 3)
        }
      } catch (error) {
        console.error('Error loading recent posts:', error)
      }
    }

    const addComment = async () => {
      if (!newComment.value.trim() || !currentUser.value || !blog.value) return
      
      addingComment.value = true
      
      try {
        const result = await blogService.createCommentForBlog(
          blog.value.id, 
          newComment.value.trim(), 
          currentUser.value.id
        )
        
        if (result.success) {
          newComment.value = ''
          await loadComments() // Reload comments to show the new one
        } else {
          alert(result.error || 'Failed to add comment')
        }
      } catch (error) {
        console.error('Error adding comment:', error)
        alert('Failed to add comment')
      } finally {
        addingComment.value = false
      }
    }

    const editComment = (comment) => {
      editingComment.value = comment
      editCommentContent.value = comment.content
    }

    const cancelEditComment = () => {
      editingComment.value = null
      editCommentContent.value = ''
    }

    const saveEditComment = async () => {
      if (!editCommentContent.value.trim() || !editingComment.value) return
      
      updatingComment.value = true
      
      try {
        const result = await blogService.updateComment(
          editingComment.value.id, 
          editCommentContent.value.trim()
        )
        
        if (result.success) {
          await loadComments() // Reload comments to show the updated one
          cancelEditComment()
        } else {
          alert(result.error || 'Failed to update comment')
        }
      } catch (error) {
        console.error('Error updating comment:', error)
        alert('Failed to update comment')
      } finally {
        updatingComment.value = false
      }
    }

    const deleteComment = async (comment) => {
      if (!confirm('Are you sure you want to delete this comment?')) return
      
      try {
        const result = await blogService.deleteComment(comment.id)
        
        if (result.success) {
          await loadComments() // Reload comments to reflect the deletion
        } else {
          alert(result.error || 'Failed to delete comment')
        }
      } catch (error) {
        console.error('Error deleting comment:', error)
        alert('Failed to delete comment')
      }
    }

    const togglePublishStatus = async () => {
      if (!blog.value) return
      
      try {
        let result
        if (blog.value.published) {
          result = await blogService.unpublishBlog(blog.value.id)
        } else {
          result = await blogService.publishBlog(blog.value.id)
        }
        
        if (result.success) {
          blog.value = result.data.blog
        } else {
          alert(result.error || 'Failed to update blog status')
        }
      } catch (error) {
        console.error('Error toggling publish status:', error)
        alert('Failed to update blog status')
      }
    }

    const deleteBlog = () => {
      const modal = new bootstrap.Modal(document.getElementById('deleteBlogModal'))
      modal.show()
    }

    const confirmDeleteBlog = async () => {
      if (!blog.value) return
      
      deletingBlog.value = true
      
      try {
        const result = await blogService.deleteBlog(blog.value.id)
        
        if (result.success) {
          // Hide modal
          const modal = bootstrap.Modal.getInstance(document.getElementById('deleteBlogModal'))
          if (modal) {
            modal.hide()
          }
          
          router.push('/blog')
        } else {
          alert(result.error || 'Failed to delete blog')
        }
      } catch (error) {
        console.error('Error deleting blog:', error)
        alert('Failed to delete blog')
      } finally {
        deletingBlog.value = false
      }
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      
      try {
        const date = new Date(dateString)
        const now = new Date()
        const diffInHours = (now - date) / (1000 * 60 * 60)
        
        if (diffInHours < 1) {
          const minutes = Math.floor(diffInHours * 60)
          return `${minutes} minute${minutes !== 1 ? 's' : ''} ago`
        } else if (diffInHours < 24) {
          const hours = Math.floor(diffInHours)
          return `${hours} hour${hours !== 1 ? 's' : ''} ago`
        } else if (diffInHours < 24 * 7) {
          const days = Math.floor(diffInHours / 24)
          return `${days} day${days !== 1 ? 's' : ''} ago`
        } else {
          return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
          })
        }
      } catch (error) {
        console.error('Error formatting date:', error)
        return 'Invalid date'
      }
    }

    // Watchers
    watch(() => route.params.id, (newId) => {
      if (newId) {
        loadBlog()
      }
    })

    // Lifecycle
    onMounted(() => {
      checkAuthState()
      loadBlog()
    })

    return {
      blog,
      comments,
      recentPosts,
      currentUser,
      loading,
      error,
      newComment,
      addingComment,
      editingComment,
      editCommentContent,
      updatingComment,
      deletingBlog,
      authorStats,
      canEditBlog,
      canEditComment,
      addComment,
      editComment,
      cancelEditComment,
      saveEditComment,
      deleteComment,
      togglePublishStatus,
      deleteBlog,
      confirmDeleteBlog,
      formatDate
    }
  }
}
</script>

<style scoped>
.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fc;
}

.blog-content {
  font-family: Georgia, 'Times New Roman', serif;
}

.comment-item {
  transition: background-color 0.2s ease;
}

.comment-item:hover {
  background-color: #f8f9fa;
}

.comment-content {
  margin-top: 0.5rem;
  line-height: 1.6;
}

.comments-list {
  max-height: none;
}

.card {
  border: 1px solid #e3e6f0;
}

.dropdown-toggle::after {
  display: none;
}

.btn-link {
  text-decoration: none;
}

.btn-link:hover {
  color: var(--bs-primary);
}

@media (max-width: 768px) {
  .display-5 {
    font-size: 2rem;
  }
  
  .author-avatar {
    width: 40px;
    height: 40px;
  }
}
</style>