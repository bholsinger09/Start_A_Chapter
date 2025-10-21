<template>
  <div class="attendee-list">
    <!-- Attendee Summary Header -->
    <div class="attendee-header">
      <h6 class="mb-0">
        <i class="bi bi-people text-primary"></i>
        Attendees
        <span class="badge bg-primary ms-2">{{ totalAttendees }}</span>
      </h6>
      
      <button 
        class="btn btn-sm btn-outline-secondary"
        @click="toggleDetails"
      >
        <i :class="showDetails ? 'bi bi-chevron-up' : 'bi bi-chevron-down'"></i>
        {{ showDetails ? 'Hide' : 'Show' }} Details
      </button>
    </div>

    <!-- Quick Status Summary -->
    <div class="status-summary">
      <div 
        v-for="(count, status) in statusCounts" 
        :key="status"
        v-if="count > 0"
        class="status-chip"
        :class="getStatusClass(status)"
      >
        <i :class="getStatusIcon(status)"></i>
        <span>{{ getStatusLabel(status) }} ({{ count }})</span>
      </div>
    </div>

    <!-- Detailed Attendee List -->
    <div v-if="showDetails" class="attendee-details">
      <!-- Attending Members -->
      <div v-if="attendingMembers.length" class="attendee-section">
        <h6 class="section-title">
          <i class="bi bi-check-circle-fill text-success"></i>
          Attending ({{ attendingMembers.length }})
        </h6>
        <div class="attendee-grid">
          <AttendeeCard 
            v-for="rsvp in attendingMembers" 
            :key="rsvp.id"
            :rsvp="rsvp"
            status="attending"
          />
        </div>
      </div>

      <!-- Maybe Members -->
      <div v-if="maybeMembers.length" class="attendee-section">
        <h6 class="section-title">
          <i class="bi bi-question-circle-fill text-warning"></i>
          Maybe ({{ maybeMembers.length }})
        </h6>
        <div class="attendee-grid">
          <AttendeeCard 
            v-for="rsvp in maybeMembers" 
            :key="rsvp.id"
            :rsvp="rsvp"
            status="maybe"
          />
        </div>
      </div>

      <!-- Waitlist Members -->
      <div v-if="waitlistMembers.length" class="attendee-section">
        <h6 class="section-title">
          <i class="bi bi-clock-history text-info"></i>
          Waitlist ({{ waitlistMembers.length }})
        </h6>
        <div class="attendee-grid">
          <AttendeeCard 
            v-for="(rsvp, index) in waitlistMembers" 
            :key="rsvp.id"
            :rsvp="rsvp"
            :position="index + 1"
            status="waitlist"
          />
        </div>
      </div>

      <!-- Not Attending (collapsed by default) -->
      <div v-if="notAttendingMembers.length" class="attendee-section">
        <button 
          class="btn btn-sm btn-link section-title p-0"
          @click="showNotAttending = !showNotAttending"
        >
          <i class="bi bi-x-circle-fill text-danger"></i>
          Not Attending ({{ notAttendingMembers.length }})
          <i :class="showNotAttending ? 'bi bi-chevron-up' : 'bi bi-chevron-down'"></i>
        </button>
        
        <div v-if="showNotAttending" class="attendee-grid">
          <AttendeeCard 
            v-for="rsvp in notAttendingMembers" 
            :key="rsvp.id"
            :rsvp="rsvp"
            status="not-attending"
          />
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="!totalAttendees && showDetails" class="empty-state">
      <i class="bi bi-calendar-x text-muted"></i>
      <p class="text-muted mb-0">No RSVPs yet</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue'
import AttendeeCard from './AttendeeCard.vue'

const props = defineProps({
  rsvps: {
    type: Array,
    default: () => []
  },
  showDetails: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toggle-details'])

// Local state
const showNotAttending = ref(false)

// Computed properties
const attendingMembers = computed(() => 
  props.rsvps.filter(rsvp => rsvp.status === 'ATTENDING')
)

const maybeMembers = computed(() => 
  props.rsvps.filter(rsvp => rsvp.status === 'MAYBE')
)

const waitlistMembers = computed(() => 
  props.rsvps
    .filter(rsvp => rsvp.status === 'WAITLIST')
    .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
)

const notAttendingMembers = computed(() => 
  props.rsvps.filter(rsvp => rsvp.status === 'NOT_ATTENDING')
)

const totalAttendees = computed(() => 
  attendingMembers.value.length + maybeMembers.value.length + waitlistMembers.value.length
)

const statusCounts = computed(() => ({
  ATTENDING: attendingMembers.value.length,
  MAYBE: maybeMembers.value.length,
  WAITLIST: waitlistMembers.value.length,
  NOT_ATTENDING: notAttendingMembers.value.length
}))

// Methods
const toggleDetails = () => {
  emit('toggle-details')
}

const getStatusIcon = (status) => {
  const iconMap = {
    'ATTENDING': 'bi bi-check-circle-fill',
    'MAYBE': 'bi bi-question-circle-fill',
    'WAITLIST': 'bi bi-clock-history',
    'NOT_ATTENDING': 'bi bi-x-circle-fill'
  }
  return iconMap[status]
}

const getStatusLabel = (status) => {
  const labelMap = {
    'ATTENDING': 'Going',
    'MAYBE': 'Maybe',
    'WAITLIST': 'Waitlist',
    'NOT_ATTENDING': 'Not Going'
  }
  return labelMap[status]
}

const getStatusClass = (status) => {
  const classMap = {
    'ATTENDING': 'status-attending',
    'MAYBE': 'status-maybe', 
    'WAITLIST': 'status-waitlist',
    'NOT_ATTENDING': 'status-not-attending'
  }
  return classMap[status]
}
</script>

<style scoped>
.attendee-list {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 1rem;
  border: 1px solid #e9ecef;
}

.attendee-header {
  display: flex;
  justify-content: between;
  align-items: center;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.status-summary {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 1rem;
}

.status-chip {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.status-attending {
  background: rgba(25, 135, 84, 0.1);
  color: #198754;
  border: 1px solid rgba(25, 135, 84, 0.2);
}

.status-maybe {
  background: rgba(255, 193, 7, 0.1);
  color: #ffc107;
  border: 1px solid rgba(255, 193, 7, 0.2);
}

.status-waitlist {
  background: rgba(13, 202, 240, 0.1);
  color: #0dcaf0;
  border: 1px solid rgba(13, 202, 240, 0.2);
}

.status-not-attending {
  background: rgba(220, 53, 69, 0.1);
  color: #dc3545;
  border: 1px solid rgba(220, 53, 69, 0.2);
}

.attendee-details {
  margin-top: 1rem;
}

.attendee-section {
  margin-bottom: 1.5rem;
}

.attendee-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #495057;
  text-decoration: none;
}

.section-title:hover {
  color: #212529;
}

.attendee-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 0.75rem;
}

.empty-state {
  text-align: center;
  padding: 2rem 1rem;
}

.empty-state i {
  font-size: 2rem;
  display: block;
  margin-bottom: 0.5rem;
}

@media (max-width: 768px) {
  .attendee-header {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
  }
  
  .attendee-grid {
    grid-template-columns: 1fr;
  }
}
</style>