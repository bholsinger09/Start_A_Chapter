<template>
  <div class="activity-feed">
    <div class="activity-feed-header">
      <h3 class="activity-feed-title">
        <i class="fas fa-activity-feed" style="margin-right: 8px;"></i>
        Recent Activity
      </h3>
      <div class="activity-controls">
        <select v-model="selectedFilter" @change="applyFilter" class="form-select">
          <option value="all">All Activities</option>
          <option value="chapter">My Chapter</option>
          <option value="user">My Activities</option>
          <option value="critical">Critical Only</option>
        </select>
        <button @click="refreshFeed" class="btn btn-outline-primary btn-sm">
          <i class="fas fa-refresh" :class="{ 'fa-spin': loading }"></i>
          Refresh
        </button>
      </div>
    </div>

    <div v-if="loading && activities.length === 0" class="loading-state">
      <div class="spinner-border text-primary" role="status">
        <span class="sr-only">Loading activities...</span>
      </div>
      <p>Loading recent activities...</p>
    </div>

    <div v-else-if="error" class="error-state alert alert-danger">
      <i class="fas fa-exclamation-triangle"></i>
      {{ error }}
      <button @click="refreshFeed" class="btn btn-sm btn-outline-danger">
        Try Again
      </button>
    </div>

    <div v-else-if="activities.length === 0" class="empty-state">
      <div class="empty-icon">
        <i class="fas fa-clock"></i>
      </div>
      <h5>No Recent Activity</h5>
      <p class="text-muted">Activity will appear here when members interact with the chapter.</p>
    </div>

    <div v-else class="activity-list">
      <div
        v-for="activity in activities"
        :key="activity.id"
        class="activity-item"
        :class="[
          'priority-' + activity.priority.toLowerCase(),
          { 'activity-critical': activity.priority === 'CRITICAL' }
        ]"
      >
        <div class="activity-icon" :class="getActivityIconClass(activity.type)">
          <span class="activity-emoji">{{ activity.emoji }}</span>
        </div>
        
        <div class="activity-content">
          <div class="activity-header">
            <span class="activity-type">{{ activity.displayName }}</span>
            <span class="activity-time" :title="formatFullDate(activity.createdAt)">
              {{ formatRelativeTime(activity.createdAt) }}
            </span>
          </div>
          
          <div class="activity-description">
            {{ activity.description }}
          </div>
          
          <div v-if="activity.user || activity.chapter" class="activity-meta">
            <span v-if="activity.user" class="activity-user">
              <i class="fas fa-user"></i>
              {{ activity.user.firstName }} {{ activity.user.lastName }}
            </span>
            <span v-if="activity.chapter" class="activity-chapter">
              <i class="fas fa-university"></i>
              {{ activity.chapter.name }}
              <span v-if="activity.chapter.location" class="chapter-location">
                ({{ activity.chapter.location }})
              </span>
            </span>
          </div>
          
          <div v-if="activity.priority === 'HIGH' || activity.priority === 'CRITICAL'" class="priority-badge">
            <span :class="'badge badge-' + getPriorityClass(activity.priority)">
              {{ activity.priority }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="hasMore && !loading" class="load-more-container">
      <button @click="loadMore" class="btn btn-outline-primary">
        Load More Activities
      </button>
    </div>

    <div v-if="loading && activities.length > 0" class="loading-more text-center">
      <div class="spinner-border spinner-border-sm text-primary" role="status">
        <span class="sr-only">Loading more...</span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import api from '@/api'

export default {
  name: 'ActivityFeed',
  props: {
    chapterId: {
      type: Number,
      default: null
    },
    limit: {
      type: Number,
      default: 20
    },
    autoRefresh: {
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const activities = ref([])
    const loading = ref(false)
    const error = ref(null)
    const selectedFilter = ref('all')
    const pagination = reactive({
      currentPage: 0,
      totalPages: 0,
      totalElements: 0,
      hasMore: false
    })
    
    let refreshInterval = null

    const loadActivities = async (page = 0, append = false) => {
      try {
        loading.value = true
        error.value = null
        
        let response
        const params = { page, size: props.limit }
        
        switch (selectedFilter.value) {
          case 'chapter':
            if (props.chapterId) {
              response = await api.get(`/api/activities/chapter/${props.chapterId}`, { params })
            } else {
              // Fallback to global feed if no chapter ID
              response = await api.get('/api/activities/feed', { params })
            }
            break
          case 'user':
            response = await api.get('/api/activities/my-activities', { params })
            break
          case 'critical':
            response = await api.get('/api/activities/critical')
            break
          default:
            response = await api.get('/api/activities/feed', { params })
        }

        if (selectedFilter.value === 'critical') {
          // Critical activities endpoint returns array directly
          if (append) {
            activities.value = [...activities.value, ...response.data]
          } else {
            activities.value = response.data
          }
          pagination.hasMore = false
        } else {
          // Other endpoints return paginated response
          if (append) {
            activities.value = [...activities.value, ...response.data.activities]
          } else {
            activities.value = response.data.activities
          }
          
          pagination.currentPage = response.data.currentPage
          pagination.totalPages = response.data.totalPages
          pagination.totalElements = response.data.totalElements
          pagination.hasMore = pagination.currentPage < pagination.totalPages - 1
        }
        
      } catch (err) {
        console.error('Failed to load activities:', err)
        error.value = err.response?.data?.error || 'Failed to load activities'
      } finally {
        loading.value = false
      }
    }

    const refreshFeed = () => {
      loadActivities(0, false)
    }

    const loadMore = () => {
      if (pagination.hasMore && !loading.value) {
        loadActivities(pagination.currentPage + 1, true)
      }
    }

    const applyFilter = () => {
      activities.value = []
      pagination.currentPage = 0
      pagination.hasMore = false
      loadActivities(0, false)
    }

    const formatRelativeTime = (dateTime) => {
      const now = new Date()
      const activityDate = new Date(dateTime)
      const diffInSeconds = Math.floor((now - activityDate) / 1000)

      if (diffInSeconds < 60) {
        return 'Just now'
      } else if (diffInSeconds < 3600) {
        const minutes = Math.floor(diffInSeconds / 60)
        return `${minutes} minute${minutes !== 1 ? 's' : ''} ago`
      } else if (diffInSeconds < 86400) {
        const hours = Math.floor(diffInSeconds / 3600)
        return `${hours} hour${hours !== 1 ? 's' : ''} ago`
      } else if (diffInSeconds < 604800) {
        const days = Math.floor(diffInSeconds / 86400)
        return `${days} day${days !== 1 ? 's' : ''} ago`
      } else {
        return activityDate.toLocaleDateString()
      }
    }

    const formatFullDate = (dateTime) => {
      return new Date(dateTime).toLocaleString()
    }

    const getActivityIconClass = (type) => {
      const typeClasses = {
        MEMBER_JOINED: 'icon-member',
        EVENT_CREATED: 'icon-event',
        BLOG_CREATED: 'icon-blog',
        COMMENT_ADDED: 'icon-comment',
        CHAPTER_UPDATED: 'icon-chapter',
        USER_LOGIN: 'icon-login',
        MEMBER_ROLE_CHANGED: 'icon-role',
        EVENT_UPDATED: 'icon-event-update',
        EVENT_CANCELLED: 'icon-event-cancel',
        BLOG_PUBLISHED: 'icon-blog-publish',
        MEMBER_ACTIVATED: 'icon-member-active',
        MEMBER_DEACTIVATED: 'icon-member-inactive',
        SYSTEM_BACKUP: 'icon-system',
        DATA_EXPORT: 'icon-export',
        SECURITY_ALERT: 'icon-security'
      }
      
      return typeClasses[type] || 'icon-default'
    }

    const getPriorityClass = (priority) => {
      const priorityClasses = {
        LOW: 'secondary',
        NORMAL: 'primary',
        HIGH: 'warning',
        CRITICAL: 'danger'
      }
      
      return priorityClasses[priority] || 'primary'
    }

    const hasMore = computed(() => pagination.hasMore)

    onMounted(() => {
      loadActivities()
      
      if (props.autoRefresh) {
        refreshInterval = setInterval(refreshFeed, 60000) // Refresh every minute
      }
    })

    // Cleanup interval on component unmount
    const cleanup = () => {
      if (refreshInterval) {
        clearInterval(refreshInterval)
      }
    }

    return {
      activities,
      loading,
      error,
      selectedFilter,
      hasMore,
      loadActivities,
      refreshFeed,
      loadMore,
      applyFilter,
      formatRelativeTime,
      formatFullDate,
      getActivityIconClass,
      getPriorityClass,
      cleanup
    }
  },
  beforeUnmount() {
    this.cleanup()
  }
}
</script>

<style scoped>
.activity-feed {
  background: var(--bg-color);
  border-radius: 8px;
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.activity-feed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.25rem;
  background: var(--header-bg);
  border-bottom: 1px solid var(--border-color);
}

.activity-feed-title {
  margin: 0;
  color: var(--text-color);
  font-size: 1.1rem;
  font-weight: 600;
}

.activity-controls {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.form-select {
  min-width: 150px;
  font-size: 0.875rem;
}

.loading-state, .error-state, .empty-state {
  padding: 2rem;
  text-align: center;
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.empty-state {
  color: var(--text-muted);
}

.empty-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.activity-list {
  max-height: 600px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid var(--border-color);
  transition: background-color 0.2s ease;
}

.activity-item:hover {
  background: var(--hover-bg);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item.activity-critical {
  border-left: 3px solid var(--danger-color);
  background: rgba(220, 53, 69, 0.05);
}

.activity-item.priority-high {
  border-left: 3px solid var(--warning-color);
}

.activity-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 0.75rem;
  background: var(--primary-color);
  color: white;
}

.activity-emoji {
  font-size: 1.2rem;
}

.icon-member { background: var(--success-color); }
.icon-event { background: var(--info-color); }
.icon-blog { background: var(--warning-color); }
.icon-comment { background: var(--secondary-color); }
.icon-chapter { background: var(--primary-color); }
.icon-login { background: var(--dark-color); }
.icon-role { background: var(--danger-color); }
.icon-security { background: var(--danger-color); }
.icon-system { background: var(--info-color); }

.activity-content {
  flex: 1;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.25rem;
}

.activity-type {
  font-weight: 600;
  color: var(--text-color);
}

.activity-time {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.activity-description {
  color: var(--text-color);
  margin-bottom: 0.5rem;
  line-height: 1.4;
}

.activity-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.75rem;
  color: var(--text-muted);
}

.activity-user, .activity-chapter {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.chapter-location {
  font-style: italic;
}

.priority-badge {
  margin-top: 0.5rem;
}

.priority-badge .badge {
  font-size: 0.7rem;
}

.load-more-container {
  padding: 1rem;
  text-align: center;
  border-top: 1px solid var(--border-color);
}

.loading-more {
  padding: 1rem;
}

/* Dark mode styles */
.theme-dark {
  --bg-color: #1a1a1a;
  --header-bg: #2a2a2a;
  --border-color: #333;
  --text-color: #ffffff;
  --text-muted: #888;
  --hover-bg: #2a2a2a;
  --primary-color: #0d6efd;
  --success-color: #198754;
  --info-color: #0dcaf0;
  --warning-color: #ffc107;
  --danger-color: #dc3545;
  --secondary-color: #6c757d;
  --dark-color: #495057;
}

/* Light mode styles */
.theme-light {
  --bg-color: #ffffff;
  --header-bg: #f8f9fa;
  --border-color: #dee2e6;
  --text-color: #212529;
  --text-muted: #6c757d;
  --hover-bg: #f8f9fa;
  --primary-color: #0d6efd;
  --success-color: #198754;
  --info-color: #0dcaf0;
  --warning-color: #ffc107;
  --danger-color: #dc3545;
  --secondary-color: #6c757d;
  --dark-color: #495057;
}

@media (max-width: 768px) {
  .activity-feed-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .activity-controls {
    justify-content: space-between;
  }
  
  .activity-meta {
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .activity-item {
    padding: 0.75rem 1rem;
  }
}
</style>