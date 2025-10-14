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

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-4">
        <div class="spinner-border text-info" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <!-- Events Table -->
      <div v-else class="card">
        <div class="card-header bg-light">
          <h5 class="card-title mb-0">
            <i class="bi bi-table me-2"></i>
            All Events ({{ filteredEvents.length }})
          </h5>
        </div>
        <div class="card-body p-0">
          <div v-if="filteredEvents.length === 0" class="text-center py-4 text-muted">
            <i class="bi bi-calendar-x display-4 mb-3"></i>
            <p>No events found</p>
          </div>
          <div v-else class="table-responsive">
            <table class="table table-hover mb-0">
              <thead class="table-light">
                <tr>
                  <th>Event Title</th>
                  <th>Date & Time</th>
                  <th>Type</th>
                  <th>Chapter</th>
                  <th>Location</th>
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
import { eventService } from '../services/eventService'
import { chapterService } from '../services/chapterService'

export default {
  name: 'Events',
  data() {
    return {
      loading: true,
      saving: false,
      events: [],
      chapters: [],
      searchTerm: '',
      selectedChapter: '',
      selectedType: '',
      selectedTimeframe: '',
      showCreateModal: false,
      showEditModal: false,
      showDeleteModal: false,
      eventToDelete: null,
      eventForm: {
        id: null,
        title: '',
        description: '',
        eventDateTime: '',
        type: '',
        chapterId: '',
        location: ''
      }
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
        location: ''
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