<template>
  <div class="chapter-detail">
    <div class="container">
      <!-- Loading State -->
      <div v-if="loading" class="text-center py-4">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <!-- Chapter Not Found -->
      <div v-else-if="!chapter" class="text-center py-4">
        <div class="alert alert-danger">
          <h4>Chapter Not Found</h4>
          <p>The chapter you're looking for doesn't exist.</p>
          <router-link to="/chapters" class="btn btn-primary">
            Back to Chapters
          </router-link>
        </div>
      </div>

      <!-- Chapter Details -->
      <div v-else>
        <!-- Header -->
        <div class="row mb-4">
          <div class="col">
            <nav aria-label="breadcrumb">
              <ol class="breadcrumb">
                <li class="breadcrumb-item">
                  <router-link to="/">Home</router-link>
                </li>
                <li class="breadcrumb-item">
                  <router-link to="/chapters">Chapters</router-link>
                </li>
                <li class="breadcrumb-item active" aria-current="page">
                  {{ chapter.name }}
                </li>
              </ol>
            </nav>
            
            <div class="d-flex justify-content-between align-items-start">
              <div>
                <h1 class="display-4 mb-2">
                  <i class="bi bi-building text-primary me-3"></i>
                  {{ chapter.name }}
                </h1>
                <p class="lead text-muted">{{ chapter.universityName }}</p>
              </div>
              <div>
                <button class="btn btn-outline-secondary me-2" @click="editChapter">
                  <i class="bi bi-pencil me-2"></i>
                  Edit Chapter
                </button>
                <router-link to="/chapters" class="btn btn-secondary">
                  <i class="bi bi-arrow-left me-2"></i>
                  Back to Chapters
                </router-link>
              </div>
            </div>
          </div>
        </div>

        <!-- Chapter Information Card -->
        <div class="row mb-4">
          <div class="col-lg-8">
            <div class="card">
              <div class="card-header bg-light">
                <h5 class="card-title mb-0">
                  <i class="bi bi-info-circle text-primary me-2"></i>
                  Chapter Information
                </h5>
              </div>
              <div class="card-body">
                <div class="row">
                  <div class="col-md-6">
                    <p><strong>University:</strong> {{ chapter.universityName }}</p>
                    <p><strong>Location:</strong> {{ chapter.city }}, {{ chapter.state }}</p>
                    <p><strong>Founded:</strong> {{ formatDate(chapter.foundedDate) }}</p>
                  </div>
                  <div class="col-md-6">
                    <p><strong>Total Members:</strong> {{ chapterMembers.length }}</p>
                    <p><strong>Active Members:</strong> {{ activeMembers.length }}</p>
                    <p><strong>Upcoming Events:</strong> {{ upcomingEvents.length }}</p>
                  </div>
                </div>
                <div v-if="chapter.description" class="mt-3">
                  <strong>Description:</strong>
                  <p class="mt-2">{{ chapter.description }}</p>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Quick Stats -->
          <div class="col-lg-4">
            <div class="card bg-primary text-white mb-3">
              <div class="card-body text-center">
                <i class="bi bi-people-fill display-4 mb-2"></i>
                <h3>{{ chapterMembers.length }}</h3>
                <p class="mb-0">Total Members</p>
              </div>
            </div>
            <div class="card bg-success text-white mb-3">
              <div class="card-body text-center">
                <i class="bi bi-calendar-event display-4 mb-2"></i>
                <h3>{{ upcomingEvents.length }}</h3>
                <p class="mb-0">Upcoming Events</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Leadership Section -->
        <div class="row mb-4">
          <div class="col">
            <div class="card">
              <div class="card-header bg-light d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">
                  <i class="bi bi-award text-warning me-2"></i>
                  Chapter Leadership
                </h5>
                <router-link to="/members" class="btn btn-sm btn-outline-success">
                  <i class="bi bi-person-plus me-1"></i>
                  Add Member
                </router-link>
              </div>
              <div class="card-body">
                <div v-if="leadership.length === 0" class="text-center text-muted py-4">
                  <i class="bi bi-person-x display-4 mb-3"></i>
                  <p>No leadership positions assigned</p>
                </div>
                <div v-else class="row">
                  <div 
                    v-for="leader in leadership" 
                    :key="leader.id"
                    class="col-md-6 mb-3"
                  >
                    <div class="card border-0 bg-light">
                      <div class="card-body">
                        <h6 class="card-title text-primary">
                          {{ formatRole(leader.role) }}
                        </h6>
                        <p class="card-text mb-1">
                          <strong>{{ leader.firstName }} {{ leader.lastName }}</strong>
                        </p>
                        <p class="card-text">
                          <small class="text-muted">
                            <i class="bi bi-envelope me-1"></i>
                            {{ leader.email }}
                          </small>
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Members Section -->
        <div class="row mb-4">
          <div class="col">
            <div class="card">
              <div class="card-header bg-light d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">
                  <i class="bi bi-people text-success me-2"></i>
                  All Members ({{ chapterMembers.length }})
                </h5>
                <div>
                  <button 
                    class="btn btn-sm btn-outline-secondary me-2"
                    @click="showInactiveMembers = !showInactiveMembers"
                  >
                    {{ showInactiveMembers ? 'Hide' : 'Show' }} Inactive
                  </button>
                  <router-link to="/members" class="btn btn-sm btn-outline-success">
                    View All Members
                  </router-link>
                </div>
              </div>
              <div class="card-body p-0">
                <div v-if="displayedMembers.length === 0" class="text-center text-muted py-4">
                  <i class="bi bi-person-x display-4 mb-3"></i>
                  <p>No members found</p>
                </div>
                <div v-else class="table-responsive">
                  <table class="table table-hover mb-0">
                    <thead class="table-light">
                      <tr>
                        <th>Name</th>
                        <th>Role</th>
                        <th>Email</th>
                        <th>Joined</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="member in displayedMembers" :key="member.id">
                        <td>
                          <strong>{{ member.firstName }} {{ member.lastName }}</strong>
                        </td>
                        <td>
                          <span class="badge" :class="getRoleBadgeClass(member.role)">
                            {{ formatRole(member.role) }}
                          </span>
                        </td>
                        <td>{{ member.email }}</td>
                        <td>{{ formatDate(member.joinDate) }}</td>
                        <td>
                          <span class="badge" :class="member.active ? 'bg-success' : 'bg-secondary'">
                            {{ member.active ? 'Active' : 'Inactive' }}
                          </span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Events Section -->
        <div class="row">
          <div class="col">
            <div class="card">
              <div class="card-header bg-light d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">
                  <i class="bi bi-calendar-event text-info me-2"></i>
                  Chapter Events
                </h5>
                <router-link to="/events" class="btn btn-sm btn-outline-info">
                  <i class="bi bi-calendar-plus me-1"></i>
                  Create Event
                </router-link>
              </div>
              <div class="card-body p-0">
                <div v-if="chapterEvents.length === 0" class="text-center text-muted py-4">
                  <i class="bi bi-calendar-x display-4 mb-3"></i>
                  <p>No events scheduled</p>
                </div>
                <div v-else class="table-responsive">
                  <table class="table table-hover mb-0">
                    <thead class="table-light">
                      <tr>
                        <th>Event Title</th>
                        <th>Date & Time</th>
                        <th>Type</th>
                        <th>Location</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="event in chapterEvents.slice(0, 10)" :key="event.id">
                        <td>
                          <strong>{{ event.title }}</strong>
                          <br>
                          <small class="text-muted" v-if="event.description">
                            {{ truncateDescription(event.description) }}
                          </small>
                        </td>
                        <td>{{ formatDateTime(event.eventDateTime) }}</td>
                        <td>
                          <span class="badge" :class="getTypeBadgeClass(event.type)">
                            {{ formatType(event.type) }}
                          </span>
                        </td>
                        <td>{{ event.location || 'TBD' }}</td>
                        <td>
                          <span class="badge" :class="getStatusBadgeClass(event.eventDateTime)">
                            {{ getEventStatus(event.eventDateTime) }}
                          </span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                  <div v-if="chapterEvents.length > 10" class="text-center p-3 border-top">
                    <router-link to="/events" class="btn btn-sm btn-outline-info">
                      View All {{ chapterEvents.length }} Events
                    </router-link>
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
import { chapterService } from '../services/chapterService'
import { memberService } from '../services/memberService'
import { eventService } from '../services/eventService'

export default {
  name: 'ChapterDetail',
  data() {
    return {
      loading: true,
      chapter: null,
      chapterMembers: [],
      chapterEvents: [],
      showInactiveMembers: false
    }
  },
  computed: {
    activeMembers() {
      return this.chapterMembers.filter(member => member.active)
    },
    leadership() {
      return this.chapterMembers.filter(member => 
        ['PRESIDENT', 'VICE_PRESIDENT', 'SECRETARY', 'TREASURER'].includes(member.role)
      ).sort((a, b) => {
        const roleOrder = { 'PRESIDENT': 1, 'VICE_PRESIDENT': 2, 'SECRETARY': 3, 'TREASURER': 4 }
        return roleOrder[a.role] - roleOrder[b.role]
      })
    },
    displayedMembers() {
      return this.showInactiveMembers 
        ? this.chapterMembers 
        : this.activeMembers
    },
    upcomingEvents() {
      const now = new Date()
      return this.chapterEvents.filter(event => 
        new Date(event.eventDateTime) >= now
      )
    }
  },
  async mounted() {
    await this.loadChapterData()
  },
  methods: {
    async loadChapterData() {
      try {
        this.loading = true
        const chapterId = parseInt(this.$route.params.id)
        
        // Load chapter details and related data
        const [chapter, allMembers, allEvents] = await Promise.all([
          chapterService.getChapterById(chapterId),
          memberService.getAllMembers(),
          eventService.getAllEvents()
        ])
        
        this.chapter = chapter
        this.chapterMembers = allMembers.filter(member => 
          member.chapter && member.chapter.id === chapterId
        )
        this.chapterEvents = allEvents.filter(event => 
          event.chapter && event.chapter.id === chapterId
        ).sort((a, b) => new Date(a.eventDateTime) - new Date(b.eventDateTime))
        
      } catch (error) {
        console.error('Error loading chapter data:', error)
        this.chapter = null
      } finally {
        this.loading = false
      }
    },
    editChapter() {
      this.$router.push('/chapters')
      // Note: In a full implementation, you might want to pass chapter data
      // or implement direct editing functionality here
    },
    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
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
    formatRole(role) {
      return role.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase())
    },
    formatType(type) {
      return type.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase())
    },
    truncateDescription(description) {
      return description.length > 100 ? description.substring(0, 100) + '...' : description
    },
    getRoleBadgeClass(role) {
      const classes = {
        'PRESIDENT': 'bg-danger',
        'VICE_PRESIDENT': 'bg-warning text-dark',
        'SECRETARY': 'bg-info',
        'TREASURER': 'bg-success',
        'MEMBER': 'bg-secondary'
      }
      return classes[role] || 'bg-secondary'
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
    }
  },
  watch: {
    '$route'() {
      // Reload data when route changes (e.g., different chapter ID)
      this.loadChapterData()
    }
  }
}
</script>