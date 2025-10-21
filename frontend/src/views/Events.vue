<template>
  <div class="events">
    <div class="container">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col">
          <h1 class="display-4 text-center mb-3">
            <i class="bi bi-calendar-event text-info me-3"></i>
            Events
          </h1>
          <p class="lead text-center text-muted">
            Manage chapter events and activities
          </p>
        </div>
      </div>

      <!-- Actions Bar -->
      <div class="row mb-4">
        <div class="col-md-6">
          <button 
            class="btn btn-info" 
            @click="showCreateModal = true"
          >
            <i class="bi bi-calendar-plus me-2"></i>
            Create New Event
          </button>
        </div>
        <div class="col-md-6">
          <div class="input-group">
            <span class="input-group-text">
              <i class="bi bi-search"></i>
            </span>
            <input
              type="text"
              class="form-control"
              placeholder="Search events..."
              v-model="searchTerm"
            >
          </div>
        </div>
      </div>

      <!-- Filters -->
      <div class="row mb-4">
        <div class="col-md-4">
          <select class="form-select" v-model="selectedChapter">
            <option value="">All Chapters</option>
            <option v-for="chapter in chapters" :key="chapter.id" :value="chapter.id">
              {{ chapter.name }}
            </option>
          </select>
        </div>
        <div class="col-md-4">
          <select class="form-select" v-model="selectedType">
            <option value="">All Types</option>
            <option value="MEETING">Meeting</option>
            <option value="SOCIAL">Social</option>
            <option value="FUNDRAISER">Fundraiser</option>
            <option value="WORKSHOP">Workshop</option>
            <option value="VOLUNTEER">Volunteer</option>
          </select>
        </div>
        <div class="col-md-4">
          <select class="form-select" v-model="selectedTimeframe">
            <option value="">All Time</option>
            <option value="upcoming">Upcoming</option>
            <option value="past">Past</option>
            <option value="today">Today</option>
            <option value="this-week">This Week</option>
            <option value="this-month">This Month</option>
          </select>
        </div>
      </div>

      <!-- Loading State with Skeleton -->
      <div v-if="loading" class="card">
        <div class="card-header bg-light">
          <h5 class="card-title mb-0">
            <i class="bi bi-calendar-event me-2"></i>
            Loading Events...
          </h5>
        </div>
        <div class="card-body p-0">
          <SkeletonLoader type="list" :rows="4" />
        </div>
      </div>

      <!-- View Toggle -->
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div class="view-toggle btn-group" role="group">
          <input type="radio" class="btn-check" name="viewMode" id="cardView" v-model="viewMode" value="cards">
          <label class="btn btn-outline-secondary" for="cardView">
            <i class="bi bi-grid-3x3-gap"></i> Cards
          </label>
          
          <input type="radio" class="btn-check" name="viewMode" id="tableView" v-model="viewMode" value="table">
          <label class="btn btn-outline-secondary" for="tableView">
            <i class="bi bi-table"></i> Table
          </label>
        </div>
        
        <div class="results-info">
          <span class="text-muted">{{ filteredEvents.length }} events found</span>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="filteredEvents.length === 0" class="empty-state text-center py-5">
        <i class="bi bi-calendar-x display-1 text-muted mb-3"></i>
        <h4 class="text-muted">No events found</h4>
        <p class="text-muted">Try adjusting your filters or create a new event.</p>
        <button class="btn btn-info" @click="showCreateModal = true">
          <i class="bi bi-plus-circle me-2"></i>
          Create New Event
        </button>
      </div>

      <!-- Card View -->
      <div v-else-if="viewMode === 'cards'" class="events-grid">
        <div 
          v-for="event in filteredEvents" 
          :key="event.id"
          class="event-card-container"
        >
          <div class="event-card">
            <!-- Event Header -->
            <div class="event-header">
              <div class="event-title-section">
                <h5 class="event-title">{{ event.title }}</h5>
                <div class="event-meta">
                  <span class="badge" :class="getTypeBadgeClass(event.type)">
                    {{ formatType(event.type) }}
                  </span>
                  <span class="badge" :class="getStatusBadgeClass(event.eventDateTime)">
                    {{ getEventStatus(event.eventDateTime) }}
                  </span>
                </div>
              </div>
              
              <div class="event-actions">
                <div class="dropdown">
                  <button 
                    class="btn btn-sm btn-outline-secondary dropdown-toggle"
                    type="button"
                    :id="`eventMenu${event.id}`"
                    data-bs-toggle="dropdown"
                  >
                    <i class="bi bi-three-dots"></i>
                  </button>
                  <ul class="dropdown-menu">
                    <li>
                      <button class="dropdown-item" @click="editEvent(event)">
                        <i class="bi bi-pencil me-2"></i>Edit Event
                      </button>
                    </li>
                    <li>
                      <button class="dropdown-item text-danger" @click="confirmDelete(event)">
                        <i class="bi bi-trash me-2"></i>Delete Event
                      </button>
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            <!-- Event Details -->
            <div class="event-details">
              <div class="event-info-grid">
                <div class="info-item">
                  <i class="bi bi-calendar3 text-primary"></i>
                  <span>{{ formatDateTime(event.eventDateTime) }}</span>
                </div>
                
                <div class="info-item" v-if="event.location">
                  <i class="bi bi-geo-alt text-danger"></i>
                  <span>{{ event.location }}</span>
                </div>
                
                <div class="info-item" v-if="event.chapter">
                  <i class="bi bi-building text-info"></i>
                  <router-link 
                    :to="`/chapters/${event.chapter.id}`"
                    class="text-decoration-none"
                  >
                    {{ event.chapter.name }}
                  </router-link>
                </div>
              </div>
              
              <div v-if="event.description" class="event-description">
                {{ event.description }}
              </div>
            </div>

            <!-- RSVP Section -->
            <div class="rsvp-section">
              <EventRSVPCard 
                :event="event"
                :rsvps="eventRsvps[event.id] || []"
                :current-user-id="currentUserId"
                @rsvp-updated="handleRSVPUpdate"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Table View (Original) -->
      <div v-else class="card">
        <div class="card-header bg-light">
          <h5 class="card-title mb-0">
            <i class="bi bi-table me-2"></i>
            All Events ({{ filteredEvents.length }})
          </h5>
        </div>
        <div class="card-body p-0">
          <div class="table-responsive">
            <table class="table table-hover mb-0">
              <thead class="table-light">
                <tr>
                  <th>Event Title</th>
                  <th>Date & Time</th>
                  <th>Type</th>
                  <th>Chapter</th>
                  <th>Location</th>
                  <th>RSVPs</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="event in filteredEvents" :key="event.id">
                  <td>
                    <strong>{{ event.title }}</strong>
                    <br>
                    <small class="text-muted" v-if="event.description">
                      {{ truncateDescription(event.description) }}
                    </small>
                  </td>
                  <td>
                    {{ formatDateTime(event.eventDateTime) }}
                  </td>
                  <td>
                    <span class="badge" :class="getTypeBadgeClass(event.type)">
                      {{ formatType(event.type) }}
                    </span>
                  </td>
                  <td>
                    <router-link 
                      v-if="event.chapter"
                      :to="`/chapters/${event.chapter.id}`"
                      class="text-decoration-none"
                    >
                      {{ event.chapter.name }}
                    </router-link>
                    <span v-else class="text-muted">No Chapter</span>
                  </td>
                  <td>{{ event.location || 'TBD' }}</td>
                  <td>
                    <div class="rsvp-summary">
                      <span class="badge bg-success me-1">
                        {{ getRSVPCount(event.id, 'ATTENDING') }}
                      </span>
                      <span class="badge bg-warning me-1" v-if="getRSVPCount(event.id, 'MAYBE')">
                        {{ getRSVPCount(event.id, 'MAYBE') }} maybe
                      </span>
                      <span class="badge bg-info" v-if="getRSVPCount(event.id, 'WAITLIST')">
                        {{ getRSVPCount(event.id, 'WAITLIST') }} waitlist
                      </span>
                    </div>
                  </td>
                  <td>
                    <span class="badge" :class="getStatusBadgeClass(event.eventDateTime)">
                      {{ getEventStatus(event.eventDateTime) }}
                    </span>
                  </td>
                  <td>
                    <div class="btn-group" role="group">
                      <button 
                        class="btn btn-sm btn-outline-secondary"
                        @click="editEvent(event)"
                      >
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button 
                        class="btn btn-sm btn-outline-danger"
                        @click="confirmDelete(event)"
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
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ showEditModal ? 'Edit Event' : 'Create New Event' }}
            </h5>
            <button 
              type="button" 
              class="btn-close" 
              @click="closeModal"
            ></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveEvent">
              <div class="mb-3">
                <label for="title" class="form-label">Event Title *</label>
                <input
                  type="text"
                  class="form-control"
                  id="title"
                  v-model="eventForm.title"
                  required
                >
              </div>
              <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea
                  class="form-control"
                  id="description"
                  v-model="eventForm.description"
                  rows="3"
                ></textarea>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="eventDateTime" class="form-label">Date & Time *</label>
                  <input
                    type="datetime-local"
                    class="form-control"
                    id="eventDateTime"
                    v-model="eventForm.eventDateTime"
                    required
                  >
                </div>
                <div class="col-md-6 mb-3">
                  <label for="type" class="form-label">Event Type *</label>
                  <select
                    class="form-select"
                    id="type"
                    v-model="eventForm.type"
                    required
                  >
                    <option value="">Select Type</option>
                    <option value="MEETING">Meeting</option>
                    <option value="SOCIAL">Social</option>
                    <option value="FUNDRAISER">Fundraiser</option>
                    <option value="WORKSHOP">Workshop</option>
                    <option value="VOLUNTEER">Volunteer</option>
                  </select>
                </div>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="chapter" class="form-label">Chapter *</label>
                  <select
                    class="form-select"
                    id="chapter"
                    v-model="eventForm.chapterId"
                    required
                  >
                    <option value="">Select Chapter</option>
                    <option v-for="chapter in chapters" :key="chapter.id" :value="chapter.id">
                      {{ chapter.name }}
                    </option>
                  </select>
                </div>
                <div class="col-md-6 mb-3">
                  <label for="location" class="form-label">Location</label>
                  <input
                    type="text"
                    class="form-control"
                    id="location"
                    v-model="eventForm.location"
                    placeholder="Event location or 'Virtual'"
                  >
                </div>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="capacity" class="form-label">Event Capacity</label>
                  <input
                    type="number"
                    class="form-control"
                    id="capacity"
                    v-model="eventForm.capacity"
                    placeholder="Leave empty for unlimited"
                    min="1"
                  >
                  <div class="form-text">
                    Set a maximum number of attendees (optional)
                  </div>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">
              Cancel
            </button>
            <button 
              type="button" 
              class="btn btn-info" 
              @click="saveEvent"
              :disabled="saving"
            >
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              {{ showEditModal ? 'Update Event' : 'Create Event' }}
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
            <p>Are you sure you want to delete the event <strong>{{ eventToDelete?.title }}</strong>?</p>
            <p class="text-danger">This action cannot be undone.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showDeleteModal = false">
              Cancel
            </button>
            <button type="button" class="btn btn-danger" @click="deleteEvent">
              Delete Event
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
import { ref, reactive, computed, onMounted } from 'vue'
import { eventService } from '../services/eventService'
import { chapterService } from '../services/chapterService'
import EventRSVPCard from '../components/EventRSVPCard.vue'
import SkeletonLoader from '../components/SkeletonLoader.vue'
import { useEventAPI } from '../composables/useEventAPI'

export default {
  name: 'Events',
  components: {
    EventRSVPCard,
    SkeletonLoader
  },
  setup() {
    // API composable
    const { fetchEventRSVPs } = useEventAPI()
    
    // Reactive state
    const events = ref([])
    const chapters = ref([])
    const eventRsvps = ref({})
    const loading = ref(false)
    const saving = ref(false)
    
    // View state
    const viewMode = ref('cards')
    const searchTerm = ref('')
    const selectedChapter = ref('')
    const selectedType = ref('')
    const selectedTimeframe = ref('')
    
    // Modal state
    const showCreateModal = ref(false)
    const showEditModal = ref(false)
    const showDeleteModal = ref(false)
    const eventToDelete = ref(null)
    
    // Form state
    const eventForm = reactive({
      id: null,
      title: '',
      description: '',
      eventDateTime: '',
      type: '',
      chapterId: '',
      location: '',
      capacity: null
    })
    
    // Current user (this should come from auth system)
    const currentUserId = ref(1) // TODO: Get from auth context
    
    return {
      // State
      events,
      chapters,
      eventRsvps,
      loading,
      saving,
      viewMode,
      searchTerm,
      selectedChapter,
      selectedType,
      selectedTimeframe,
      showCreateModal,
      showEditModal,
      showDeleteModal,
      eventToDelete,
      eventForm,
      currentUserId,
      
      // API
      fetchEventRSVPs
    }
  },
  computed: {
    filteredEvents() {
      let filtered = this.events

      // Filter by search term
      if (this.searchTerm) {
        const term = this.searchTerm.toLowerCase()
        filtered = filtered.filter(event =>
          event.title.toLowerCase().includes(term) ||
          (event.description && event.description.toLowerCase().includes(term)) ||
          (event.location && event.location.toLowerCase().includes(term)) ||
          (event.chapter && event.chapter.name.toLowerCase().includes(term))
        )
      }

      // Filter by chapter
      if (this.selectedChapter) {
        filtered = filtered.filter(event => 
          event.chapter && event.chapter.id === parseInt(this.selectedChapter)
        )
      }

      // Filter by type
      if (this.selectedType) {
        filtered = filtered.filter(event => event.type === this.selectedType)
      }

      // Filter by timeframe
      if (this.selectedTimeframe) {
        const now = new Date()
        const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
        const weekFromNow = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)
        const monthFromNow = new Date(now.getFullYear(), now.getMonth() + 1, now.getDate())

        filtered = filtered.filter(event => {
          const eventDate = new Date(event.eventDateTime)
          
          switch (this.selectedTimeframe) {
            case 'upcoming':
              return eventDate >= now
            case 'past':
              return eventDate < now
            case 'today':
              return eventDate >= today && eventDate < new Date(today.getTime() + 24 * 60 * 60 * 1000)
            case 'this-week':
              return eventDate >= now && eventDate <= weekFromNow
            case 'this-month':
              return eventDate >= now && eventDate <= monthFromNow
            default:
              return true
          }
        })
      }

      // Sort by event date
      return filtered.sort((a, b) => new Date(a.eventDateTime) - new Date(b.eventDateTime))
    }
  },
  data() {
    return {
      eventRsvps: {},
      viewMode: 'cards',
      currentUserId: 1 // TODO: Get from auth context
    }
  },
  async mounted() {
    await this.loadData()
  },
  methods: {
    async loadData() {
      try {
        this.loading = true
        const [eventsData, chaptersData] = await Promise.all([
          eventService.getAllEvents(),
          chapterService.getAllChapters()
        ])
        this.events = eventsData
        this.chapters = chaptersData
        
        // Load RSVPs for all events
        await this.loadAllRSVPs()
      } catch (error) {
        console.error('Error loading events:', error)
      } finally {
        this.loading = false
      }
    },
    formatDateTime(dateString) {
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    formatType(type) {
      return type.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase())
    },
    truncateDescription(description) {
      return description.length > 50 ? description.substring(0, 50) + '...' : description
    },
    getTypeBadgeClass(type) {
      const classes = {
        'MEETING': 'bg-primary',
        'SOCIAL': 'bg-success',
        'FUNDRAISER': 'bg-warning text-dark',
        'WORKSHOP': 'bg-info',
        'VOLUNTEER': 'bg-secondary'
      }
      return classes[type] || 'bg-secondary'
    },
    getEventStatus(eventDateTime) {
      const now = new Date()
      const eventDate = new Date(eventDateTime)
      
      if (eventDate < now) {
        return 'Past'
      } else if (eventDate.toDateString() === now.toDateString()) {
        return 'Today'
      } else {
        return 'Upcoming'
      }
    },
    getStatusBadgeClass(eventDateTime) {
      const status = this.getEventStatus(eventDateTime)
      const classes = {
        'Past': 'bg-secondary',
        'Today': 'bg-warning text-dark',
        'Upcoming': 'bg-success'
      }
      return classes[status] || 'bg-secondary'
    },
    editEvent(event) {
      // Format datetime for input field
      const eventDate = new Date(event.eventDateTime)
      const formattedDateTime = eventDate.toISOString().slice(0, 16)
      
      this.eventForm = {
        id: event.id,
        title: event.title,
        description: event.description || '',
        eventDateTime: formattedDateTime,
        type: event.type,
        chapterId: event.chapter ? event.chapter.id : '',
        location: event.location || ''
      }
      this.showEditModal = true
    },
    confirmDelete(event) {
      this.eventToDelete = event
      this.showDeleteModal = true
    },
    async saveEvent() {
      try {
        this.saving = true
        
        const eventData = {
          ...this.eventForm,
          chapter: this.eventForm.chapterId ? { id: this.eventForm.chapterId } : null
        }
        delete eventData.chapterId
        
        if (this.showEditModal) {
          await eventService.updateEvent(this.eventForm.id, eventData)
        } else {
          await eventService.createEvent(eventData)
        }
        
        await this.loadData()
        this.closeModal()
      } catch (error) {
        console.error('Error saving event:', error)
        alert('Error saving event. Please try again.')
      } finally {
        this.saving = false
      }
    },
    async deleteEvent() {
      try {
        await eventService.deleteEvent(this.eventToDelete.id)
        await this.loadData()
        this.showDeleteModal = false
        this.eventToDelete = null
      } catch (error) {
        console.error('Error deleting event:', error)
        alert('Error deleting event. Please try again.')
      }
    },
    
    // RSVP Methods
    async loadEventRSVPs(eventId) {
      try {
        const response = await this.fetchEventRSVPs(eventId)
        this.eventRsvps[eventId] = response.data || []
      } catch (error) {
        console.error(`Error loading RSVPs for event ${eventId}:`, error)
      }
    },
    
    async loadAllRSVPs() {
      // Load RSVPs for all events
      const promises = this.events.map(event => this.loadEventRSVPs(event.id))
      await Promise.all(promises)
    },
    
    handleRSVPUpdate(data) {
      // Refresh RSVPs for the updated event
      this.loadEventRSVPs(data.eventId)
    },
    
    getRSVPCount(eventId, status) {
      const rsvps = this.eventRsvps[eventId] || []
      return rsvps.filter(rsvp => rsvp.status === status).length
    },
    
    closeModal() {
      this.showCreateModal = false
      this.showEditModal = false
      this.showDeleteModal = false
      this.eventForm = {
        id: null,
        title: '',
        description: '',
        eventDateTime: '',
        type: '',
        chapterId: '',
        location: '',
        capacity: null
      }
    }
  }
}
</script>

<style scoped>
/* Modal animations */
.modal.show {
  animation: fadeIn 0.15s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Events grid layout */
.events-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.event-card-container {
  display: flex;
}

.event-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
  overflow: hidden;
  width: 100%;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.event-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* Event header */
.event-header {
  padding: 1.25rem 1.25rem 0.75rem;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  border-bottom: 1px solid #f8f9fa;
}

.event-title-section {
  flex: 1;
  min-width: 0;
}

.event-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
  color: #212529;
  line-height: 1.3;
}

.event-meta {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.event-meta .badge {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
}

.event-actions {
  flex-shrink: 0;
  margin-left: 1rem;
}

/* Event details */
.event-details {
  padding: 0.75rem 1.25rem;
  flex: 1;
}

.event-info-grid {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #495057;
}

.info-item i {
  width: 16px;
  flex-shrink: 0;
}

.event-description {
  font-size: 0.875rem;
  color: #6c757d;
  line-height: 1.5;
  border-top: 1px solid #f8f9fa;
  padding-top: 0.75rem;
  margin-top: 0.75rem;
}

/* RSVP section */
.rsvp-section {
  padding: 1rem 1.25rem 1.25rem;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
}

/* RSVP summary for table view */
.rsvp-summary {
  display: flex;
  gap: 0.25rem;
  flex-wrap: wrap;
}

.rsvp-summary .badge {
  font-size: 0.7rem;
  padding: 0.2rem 0.4rem;
}

/* View toggle */
.view-toggle {
  border-radius: 8px;
  overflow: hidden;
}

.view-toggle .btn {
  border-radius: 0;
  border-color: #dee2e6;
}

.view-toggle .btn:first-child {
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
}

.view-toggle .btn:last-child {
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
}

/* Empty state */
.empty-state {
  background: white;
  border-radius: 12px;
  padding: 3rem 2rem;
  border: 2px dashed #dee2e6;
}

/* Results info */
.results-info {
  font-size: 0.875rem;
}

/* Responsive design */
@media (max-width: 768px) {
  .events-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .event-header {
    flex-direction: column;
    gap: 0.75rem;
    align-items: stretch;
  }
  
  .event-actions {
    margin-left: 0;
    align-self: flex-end;
  }
  
  .event-info-grid {
    gap: 0.75rem;
  }
  
  .view-toggle {
    order: 2;
    justify-self: center;
  }
  
  .results-info {
    order: 1;
    text-align: center;
  }
  
  .d-flex.justify-content-between {
    flex-direction: column;
    gap: 1rem;
    align-items: center;
  }
}

/* Loading and error states */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

/* Enhanced table styling */
.table th {
  border-top: none;
  font-weight: 600;
  font-size: 0.875rem;
  color: #495057;
  padding: 1rem 0.75rem;
}

.table td {
  padding: 1rem 0.75rem;
  vertical-align: middle;
}

.table-hover tbody tr:hover {
  background-color: rgba(0, 123, 255, 0.05);
}

/* Form enhancements */
.form-control:focus,
.form-select:focus {
  border-color: #0dcaf0;
  box-shadow: 0 0 0 0.2rem rgba(13, 202, 240, 0.25);
}

/* Button enhancements */
.btn-info {
  background-color: #0dcaf0;
  border-color: #0dcaf0;
}

.btn-info:hover {
  background-color: #0bb5d6;
  border-color: #0aa2c0;
}
</style>