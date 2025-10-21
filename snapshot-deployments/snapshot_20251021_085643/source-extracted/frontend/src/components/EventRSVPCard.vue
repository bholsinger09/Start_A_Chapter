<template>
  <div class="rsvp-card">
    <!-- Event Capacity Display -->
    <div class="capacity-section">
      <div class="capacity-header">
        <h6 class="mb-2">
          <i class="bi bi-people-fill text-info"></i>
          Event Capacity
        </h6>
      </div>
      
      <div class="capacity-display">
        <div class="attendee-stats">
          <span class="attendee-count">
            {{ attendingCount }}/{{ event.capacity || 'Unlimited' }}
          </span>
          <span class="attendee-label">attending</span>
        </div>
        
        <div v-if="event.capacity" class="capacity-progress">
          <div class="progress">
            <div 
              class="progress-bar" 
              :class="progressBarClass"
              :style="{ width: capacityPercentage + '%' }"
              role="progressbar"
            ></div>
          </div>
          <small class="text-muted">{{ capacityPercentage }}% full</small>
        </div>
      </div>
    </div>

    <!-- RSVP Status Section -->
    <div class="rsvp-section">
      <div class="rsvp-header">
        <h6 class="mb-3">
          <i class="bi bi-calendar-check text-success"></i>
          Your RSVP Status
        </h6>
      </div>

      <div class="current-status" v-if="currentUserRsvp">
        <div class="status-badge">
          <i :class="getStatusIcon(currentUserRsvp.status)"></i>
          <span>{{ getStatusLabel(currentUserRsvp.status) }}</span>
        </div>
        <small class="text-muted d-block mt-1">
          Updated {{ formatTimeAgo(currentUserRsvp.createdAt) }}
        </small>
      </div>

      <!-- RSVP Action Buttons -->
      <div class="rsvp-actions mt-3">
        <button
          v-for="option in rsvpOptions"
          :key="option.status"
          :class="['btn', 'rsvp-btn', option.class, {
            'active': currentUserRsvp?.status === option.status,
            'disabled': isLoading || (!canAttend && option.status === 'ATTENDING')
          }]"
          @click="updateRSVP(option.status)"
          :disabled="isLoading"
        >
          <div class="btn-content">
            <i :class="option.icon"></i>
            <span>{{ option.label }}</span>
          </div>
          <span v-if="getRsvpCount(option.status)" class="badge bg-light text-dark ms-2">
            {{ getRsvpCount(option.status) }}
          </span>
        </button>
      </div>

      <!-- Waitlist Information -->
      <div v-if="isOnWaitlist" class="waitlist-info alert alert-info mt-3">
        <i class="bi bi-clock-history"></i>
        <strong>You're on the waitlist!</strong>
        <div class="waitlist-details">
          Position #{{ waitlistPosition }} - We'll notify you if a spot opens up.
        </div>
      </div>

      <!-- Event Full Message -->
      <div v-if="isEventFull && !currentUserRsvp" class="event-full-info alert alert-warning mt-3">
        <i class="bi bi-exclamation-triangle"></i>
        <strong>Event is at capacity</strong>
        <div>You can still RSVP to join the waitlist.</div>
      </div>
    </div>

    <!-- Attendee Summary -->
    <div class="attendee-summary mt-4">
      <AttendeeList 
        :rsvps="rsvps" 
        :show-details="showAttendeeDetails"
        @toggle-details="showAttendeeDetails = !showAttendeeDetails"
      />
    </div>

    <!-- Loading Overlay -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="spinner-border text-info" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue'
import AttendeeList from './AttendeeList.vue'
import { useEventRSVP } from '../composables/useEventRSVP'

const props = defineProps({
  event: {
    type: Object,
    required: true
  },
  rsvps: {
    type: Array,
    default: () => []
  },
  currentUserId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['rsvp-updated'])

// Local state
const showAttendeeDetails = ref(false)

// Use RSVP composable
const {
  currentUserRsvp,
  isLoading,
  updateRSVP: updateRSVPStatus,
  rsvpCounts
} = useEventRSVP(props.event.id, props.currentUserId, props.rsvps)

// RSVP Options Configuration
const rsvpOptions = [
  {
    status: 'ATTENDING',
    label: 'Going',
    icon: 'bi bi-check-circle-fill',
    class: 'btn-success'
  },
  {
    status: 'NOT_ATTENDING',
    label: 'Not Going',
    icon: 'bi bi-x-circle-fill',
    class: 'btn-danger'
  },
  {
    status: 'MAYBE',
    label: 'Maybe',
    icon: 'bi bi-question-circle-fill',
    class: 'btn-warning'
  }
]

// Computed properties
const attendingCount = computed(() => {
  return rsvpCounts.value.ATTENDING + rsvpCounts.value.WAITLIST
})

const capacityPercentage = computed(() => {
  if (!props.event.capacity) return 0
  return Math.min(Math.round((attendingCount.value / props.event.capacity) * 100), 100)
})

const progressBarClass = computed(() => {
  const percentage = capacityPercentage.value
  if (percentage >= 90) return 'bg-danger'
  if (percentage >= 75) return 'bg-warning'
  return 'bg-success'
})

const isEventFull = computed(() => {
  return props.event.capacity && attendingCount.value >= props.event.capacity
})

const canAttend = computed(() => {
  return !isEventFull.value || currentUserRsvp.value?.status === 'ATTENDING'
})

const isOnWaitlist = computed(() => {
  return currentUserRsvp.value?.status === 'WAITLIST'
})

const waitlistPosition = computed(() => {
  // This would be calculated based on RSVP timestamps for waitlisted users
  if (!isOnWaitlist.value) return null
  const waitlistRsvps = props.rsvps
    .filter(rsvp => rsvp.status === 'WAITLIST')
    .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
  
  return waitlistRsvps.findIndex(rsvp => rsvp.userId === props.currentUserId) + 1
})

// Methods
const updateRSVP = async (status) => {
  try {
    await updateRSVPStatus(status)
    emit('rsvp-updated', { eventId: props.event.id, status })
  } catch (error) {
    console.error('Failed to update RSVP:', error)
  }
}

const getRsvpCount = (status) => {
  return rsvpCounts.value[status] || 0
}

const getStatusIcon = (status) => {
  const iconMap = {
    'ATTENDING': 'bi bi-check-circle-fill text-success',
    'NOT_ATTENDING': 'bi bi-x-circle-fill text-danger',
    'MAYBE': 'bi bi-question-circle-fill text-warning',
    'WAITLIST': 'bi bi-clock-history text-info',
    'PENDING': 'bi bi-hourglass-split text-muted'
  }
  return iconMap[status] || 'bi bi-circle text-muted'
}

const getStatusLabel = (status) => {
  const labelMap = {
    'ATTENDING': 'Going',
    'NOT_ATTENDING': 'Not Going',
    'MAYBE': 'Maybe',
    'WAITLIST': 'On Waitlist',
    'PENDING': 'Pending'
  }
  return labelMap[status] || status
}

const formatTimeAgo = (timestamp) => {
  const now = new Date()
  const time = new Date(timestamp)
  const diffInHours = Math.floor((now - time) / (1000 * 60 * 60))
  
  if (diffInHours < 1) return 'Just now'
  if (diffInHours < 24) return `${diffInHours}h ago`
  const diffInDays = Math.floor(diffInHours / 24)
  if (diffInDays < 7) return `${diffInDays}d ago`
  return time.toLocaleDateString()
}
</script>

<style scoped>
.rsvp-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e9ecef;
  position: relative;
  overflow: hidden;
}

.capacity-section {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f8f9fa;
}

.capacity-display {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.attendee-stats {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.attendee-count {
  font-size: 1.5rem;
  font-weight: 600;
  color: #0dcaf0;
}

.attendee-label {
  font-size: 0.875rem;
  color: #6c757d;
}

.capacity-progress {
  flex: 1;
  min-width: 200px;
}

.progress {
  height: 8px;
  border-radius: 4px;
}

.rsvp-section {
  margin-bottom: 1.5rem;
}

.current-status {
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
}

.rsvp-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.rsvp-btn {
  flex: 1;
  min-width: 120px;
  padding: 0.75rem;
  border: 2px solid transparent;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
  position: relative;
}

.rsvp-btn:hover:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.rsvp-btn.active {
  border-color: currentColor;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.btn-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  justify-content: center;
}

.waitlist-info,
.event-full-info {
  border-radius: 8px;
  border: none;
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
}

.waitlist-details {
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

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

@media (max-width: 768px) {
  .capacity-display {
    flex-direction: column;
    align-items: stretch;
  }
  
  .rsvp-actions {
    flex-direction: column;
  }
  
  .rsvp-btn {
    min-width: auto;
  }
}
</style>