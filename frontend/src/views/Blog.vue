<template>
  <div class="blog">
    <div class="container">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col-md-8">
          <h2>
            <i class="bi bi-journal-text me-2"></i>
            Chapter Blog
          </h2>
          <p class="text-muted">Share your experiences and connect with the community.</p>
        </div>
        <div class="col-md-4 text-end" v-if="isAuthenticated">
          <button class="btn btn-primary" disabled>
            <i class="bi bi-plus-circle me-2"></i>
            Write Post
          </button>
        </div>
      </div>

      <!-- Authentication Required Message -->
      <div class="row" v-if="!isAuthenticated">
        <div class="col-12">
          <div class="card border-warning">
            <div class="card-body text-center">
              <i class="bi bi-lock display-4 text-warning mb-3"></i>
              <h4>Authentication Required</h4>
              <p class="text-muted">
                You need to be logged in to view and create blog posts.
              </p>
              <div class="d-flex gap-2 justify-content-center">
                <router-link to="/login" class="btn btn-primary">
                  <i class="bi bi-box-arrow-in-right me-2"></i>
                  Sign In
                </router-link>
                <router-link to="/register" class="btn btn-outline-primary">
                  <i class="bi bi-person-plus me-2"></i>
                  Register
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Blog Content (for authenticated users) -->
      <div v-else>
        <!-- Quick Post Form -->
        <div class="row mb-4">
          <div class="col-12">
            <div class="card">
              <div class="card-header">
                <h6 class="mb-0">
                  <i class="bi bi-pencil me-2"></i>
                  Quick Post
                </h6>
              </div>
              <div class="card-body">
                <form>
                  <div class="mb-3">
                    <input 
                      type="text" 
                      class="form-control" 
                      placeholder="What's on your mind?"
                      disabled
                    >
                  </div>
                  <div class="d-flex justify-content-between align-items-center">
                    <small class="text-muted">
                      <i class="bi bi-person-circle me-1"></i>
                      Posting as {{ currentUser?.firstName || 'User' }}
                    </small>
                    <button type="button" class="btn btn-primary btn-sm" disabled>
                      <i class="bi bi-send me-1"></i>
                      Post
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>

        <!-- Blog Posts -->
        <div class="row">
          <div class="col-lg-8">
            <!-- Sample Blog Posts -->
            <div class="card mb-4">
              <div class="card-body">
                <h5 class="card-title">Welcome to the Chapter Blog!</h5>
                <h6 class="card-subtitle mb-2 text-muted">
                  <i class="bi bi-person-circle me-1"></i>
                  System Admin • 2 days ago
                </h6>
                <p class="card-text">
                  Welcome to our new chapter blog platform! Here you can share your experiences, 
                  connect with fellow members, and stay updated on chapter activities.
                </p>
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <button class="btn btn-outline-primary btn-sm me-2" @click="toggleLikes('post-1')">
                      <i class="bi bi-heart me-1"></i>
                      Like (5)
                    </button>
                    <button class="btn btn-outline-secondary btn-sm" @click="toggleComments('post-1')">
                      <i class="bi bi-chat me-1"></i>
                      Comment (2)
                    </button>
                  </div>
                  <small class="text-muted">
                    <i class="bi bi-eye me-1"></i>
                    15 views
                  </small>
                </div>
                
                <!-- Comments & Likes Section for Post 1 -->
                <div v-if="showComments['post-1']" class="border-top mt-3 pt-3">
                  <h6><i class="bi bi-chat me-2"></i>Comments (2)</h6>
                  <div class="mb-3">
                    <div class="d-flex mb-2">
                      <div class="avatar-circle bg-success me-2">JS</div>
                      <div>
                        <strong>John Smith</strong> <small class="text-muted">2h ago</small>
                        <p class="mb-1">Great post! Looking forward to getting more involved this semester.</p>
                      </div>
                    </div>
                    <div class="d-flex">
                      <div class="avatar-circle bg-info me-2">SA</div>
                      <div>
                        <strong>Sarah Anderson</strong> <small class="text-muted">1h ago</small>
                        <p class="mb-1">Thanks for sharing! This really motivates me to participate more.</p>
                      </div>
                    </div>
                  </div>
                  <div class="input-group">
                    <input 
                      type="text" 
                      class="form-control" 
                      placeholder="Write a comment..." 
                      v-model="newComment['post-1']"
                    >
                    <button class="btn btn-outline-primary" @click="addComment('post-1')">
                      <i class="bi bi-send"></i>
                    </button>
                  </div>
                </div>

                <div v-if="showLikes['post-1']" class="border-top mt-3 pt-3">
                  <h6><i class="bi bi-heart me-2"></i>Likes (5)</h6>
                  <div class="d-flex flex-wrap gap-2">
                    <span class="badge bg-primary">John Smith</span>
                    <span class="badge bg-success">Sarah Anderson</span>
                    <span class="badge bg-info">Mike Johnson</span>
                    <span class="badge bg-warning">Lisa Chen</span>
                    <span class="badge bg-secondary">Alex Wilson</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="card mb-4">
              <div class="card-body">
                <h5 class="card-title">Getting Involved in Campus Life</h5>
                <h6 class="card-subtitle mb-2 text-muted">
                  <i class="bi bi-person-circle me-1"></i>
                  {{ currentUser?.firstName || 'Demo User' }} • 1 week ago
                </h6>
                <p class="card-text">
                  Joining a chapter has been one of the best decisions I've made in college. 
                  The networking opportunities and friendships formed here are invaluable...
                </p>
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <button class="btn btn-outline-primary btn-sm me-2" @click="toggleLikes('post-2')">
                      <i class="bi bi-heart me-1"></i>
                      Like (12)
                    </button>
                    <button class="btn btn-outline-secondary btn-sm" @click="toggleComments('post-2')">
                      <i class="bi bi-chat me-1"></i>
                      Comment (7)
                    </button>
                  </div>
                  <small class="text-muted">
                    <i class="bi bi-eye me-1"></i>
                    28 views
                  </small>
                </div>
                
                <!-- Comments & Likes Section for Post 2 -->
                <div v-if="showComments['post-2']" class="border-top mt-3 pt-3">
                  <h6><i class="bi bi-chat me-2"></i>Comments (7)</h6>
                  <div class="mb-3">
                    <div class="d-flex mb-2">
                      <div class="avatar-circle bg-info me-2">EM</div>
                      <div>
                        <strong>Emily Rodriguez</strong> <small class="text-muted">3h ago</small>
                        <p class="mb-1">This is so inspiring! I just joined last month and already feel so welcomed.</p>
                      </div>
                    </div>
                    <div class="d-flex mb-2">
                      <div class="avatar-circle bg-warning me-2">MJ</div>
                      <div>
                        <strong>Mike Johnson</strong> <small class="text-muted">2h ago</small>
                        <p class="mb-1">The leadership opportunities have been amazing for my personal growth.</p>
                      </div>
                    </div>
                    <div class="d-flex mb-2">
                      <div class="avatar-circle bg-success me-2">LC</div>
                      <div>
                        <strong>Lisa Chen</strong> <small class="text-muted">1h ago</small>
                        <p class="mb-1">Great networking events this semester! Looking forward to the next one.</p>
                      </div>
                    </div>
                    <div class="d-flex mb-2">
                      <div class="avatar-circle bg-secondary me-2">AW</div>
                      <div>
                        <strong>Alex Wilson</strong> <small class="text-muted">45min ago</small>
                        <p class="mb-1">The mentorship program has been incredibly valuable.</p>
                      </div>
                    </div>
                    <div class="d-flex mb-2">
                      <div class="avatar-circle bg-primary me-2">KT</div>
                      <div>
                        <strong>Kevin Thompson</strong> <small class="text-muted">30min ago</small>
                        <p class="mb-1">Thanks for sharing your experience! Very motivational.</p>
                      </div>
                    </div>
                    <div class="d-flex mb-2">
                      <div class="avatar-circle bg-danger me-2">SM</div>
                      <div>
                        <strong>Sophia Martinez</strong> <small class="text-muted">15min ago</small>
                        <p class="mb-1">This really captures what it's like to be part of our chapter community.</p>
                      </div>
                    </div>
                    <div class="d-flex">
                      <div class="avatar-circle bg-info me-2">JD</div>
                      <div>
                        <strong>James Davis</strong> <small class="text-muted">10min ago</small>
                        <p class="mb-1">Looking forward to getting more involved in upcoming events!</p>
                      </div>
                    </div>
                  </div>
                  <div class="input-group">
                    <input 
                      type="text" 
                      class="form-control" 
                      placeholder="Write a comment..." 
                      v-model="newComment['post-2']"
                    >
                    <button class="btn btn-outline-primary" @click="addComment('post-2')">
                      <i class="bi bi-send"></i>
                    </button>
                  </div>
                </div>

                <div v-if="showLikes['post-2']" class="border-top mt-3 pt-3">
                  <h6><i class="bi bi-heart me-2"></i>Likes (12)</h6>
                  <div class="d-flex flex-wrap gap-2">
                    <span class="badge bg-primary">Emily Rodriguez</span>
                    <span class="badge bg-success">Mike Johnson</span>
                    <span class="badge bg-info">Lisa Chen</span>
                    <span class="badge bg-warning">Alex Wilson</span>
                    <span class="badge bg-secondary">Kevin Thompson</span>
                    <span class="badge bg-danger">Sophia Martinez</span>
                    <span class="badge bg-primary">James Davis</span>
                    <span class="badge bg-success">Sarah Anderson</span>
                    <span class="badge bg-info">John Smith</span>
                    <span class="badge bg-warning">Maria Garcia</span>
                    <span class="badge bg-secondary">David Brown</span>
                    <span class="badge bg-danger">Jessica Lee</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Load More -->
            <div class="text-center">
              <button class="btn btn-outline-primary" disabled>
                <i class="bi bi-arrow-down-circle me-2"></i>
                Load More Posts
              </button>
            </div>
          </div>

          <!-- Sidebar -->
          <div class="col-lg-4">
            <!-- Recent Activity -->
            <div class="card mb-4">
              <div class="card-header">
                <h6 class="mb-0">
                  <i class="bi bi-activity me-2"></i>
                  Recent Activity
                </h6>
              </div>
              <div class="card-body">
                <ul class="list-unstyled">
                  <li class="mb-2">
                    <small class="text-muted">
                      <i class="bi bi-heart-fill text-danger me-1"></i>
                      John liked your post • 2h ago
                    </small>
                  </li>
                  <li class="mb-2">
                    <small class="text-muted">
                      <i class="bi bi-chat-fill text-primary me-1"></i>
                      Sarah commented on "Campus Life" • 4h ago
                    </small>
                  </li>
                  <li class="mb-2">
                    <small class="text-muted">
                      <i class="bi bi-person-plus-fill text-success me-1"></i>
                      New member joined the chapter • 1d ago
                    </small>
                  </li>
                </ul>
              </div>
            </div>

            <!-- Popular Tags -->
            <div class="card mb-4">
              <div class="card-header">
                <h6 class="mb-0">
                  <i class="bi bi-tags me-2"></i>
                  Popular Topics
                </h6>
              </div>
              <div class="card-body">
                <div class="d-flex flex-wrap gap-1">
                  <span class="badge bg-primary">#campus-life</span>
                  <span class="badge bg-secondary">#networking</span>
                  <span class="badge bg-success">#events</span>
                  <span class="badge bg-info">#leadership</span>
                  <span class="badge bg-warning">#tips</span>
                  <span class="badge bg-danger">#experiences</span>
                </div>
              </div>
            </div>

            <!-- Quick Stats -->
            <div class="card">
              <div class="card-header">
                <h6 class="mb-0">
                  <i class="bi bi-graph-up me-2"></i>
                  Blog Stats
                </h6>
              </div>
              <div class="card-body">
                <div class="text-center">
                  <div class="row">
                    <div class="col-6">
                      <h5 class="text-primary">24</h5>
                      <small class="text-muted">Posts</small>
                    </div>
                    <div class="col-6">
                      <h5 class="text-success">156</h5>
                      <small class="text-muted">Comments</small>
                    </div>
                  </div>
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
import { ref, onMounted } from 'vue'
import { useAuth } from '@/composables/useAuth'

export default {
  name: 'Blog',
  setup() {
    const { currentUser, isAuthenticated, initAuth } = useAuth()
    
    // Reactive state for comments and likes visibility
    const showComments = ref({})
    const showLikes = ref({})
    const newComment = ref({
      'post-1': '',
      'post-2': ''
    })

    // Toggle functions for comments and likes
    const toggleComments = (postId) => {
      showComments.value[postId] = !showComments.value[postId]
      // Close likes if comments are opened
      if (showComments.value[postId]) {
        showLikes.value[postId] = false
      }
    }

    const toggleLikes = (postId) => {
      showLikes.value[postId] = !showLikes.value[postId]
      // Close comments if likes are opened
      if (showLikes.value[postId]) {
        showComments.value[postId] = false
      }
    }

    const addComment = (postId) => {
      if (newComment.value[postId] && newComment.value[postId].trim()) {
        // In a real app, this would make an API call
        console.log(`Adding comment to ${postId}: ${newComment.value[postId]}`)
        
        // Clear the input after adding comment
        newComment.value[postId] = ''
        
        // Show success message (you could add a toast notification here)
        alert('Comment added successfully!')
      }
    }

    onMounted(() => {
      initAuth()
    })

    return {
      isAuthenticated,
      currentUser,
      showComments,
      showLikes,
      newComment,
      toggleComments,
      toggleLikes,
      addComment
    }
  }
}
</script>

<style scoped>
.card {
  border-radius: 0.5rem;
  border: none;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.display-4 {
  font-size: 2.5rem;
}

.badge {
  font-size: 0.7rem;
}

.list-unstyled li {
  border-bottom: 1px solid #f8f9fa;
  padding-bottom: 0.5rem;
}

.list-unstyled li:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.avatar-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.8rem;
  color: white;
  flex-shrink: 0;
}

.btn:hover {
  transform: translateY(-1px);
  transition: all 0.2s ease;
}
</style>
