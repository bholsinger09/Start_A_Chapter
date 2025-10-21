<template>
  <div class="attendee-card" :class="cardClass">
    <!-- Member Avatar and Info -->
    <div class="member-info">
      <div class="avatar">
        <img 
          v-if="rsvp.member?.profileImage" 
          :src="rsvp.member.profileImage" 
          :alt="memberName"
          class="avatar-img"
        >
        <div v-else class="avatar-placeholder">
          {{ memberInitials }}
        </div>
      </div>
      
      <div class="member-details">
        <h6 class="member-name">{{ memberName }}</h6>
        <p class="member-role" v-if="rsvp.member?.role">
          {{ formatRole(rsvp.member.role) }}
        </p>
        <p class="member-email" v-if="showEmail">
          {{ rsvp.member?.email }}
        </p>
      </div>
    </div>

    <!-- RSVP Status and Timing -->
    <div class="rsvp-info">
      <div class="status-badge" :class="statusBadgeClass">
        <i :class="statusIcon"></i>
        <span>{{ statusLabel }}</span>
      </div>
      
      <!-- Waitlist Position -->
      <div v-if="status === 'waitlist' && position" class="waitlist-position">
        Position #{{ position }}
      </div>
      
      <!-- RSVP Timestamp -->
      <div class="rsvp-time">
        RSVPed {{ formatTimeAgo(rsvp.createdAt) }}
      </div>
      
      <!-- Additional Info -->
      <div v-if="rsvp.notes" class="rsvp-notes">
        <i class="bi bi-chat-left-text"></i>
        <span>{{ rsvp.notes }}</span>
      </div>
    </div>

    <!-- Action Buttons (for admin/organizer) -->
    <div v-if="showActions" class="attendee-actions">
      <button 
        v-if="status === 'waitlist'"
        class="btn btn-sm btn-success"
        @click="promoteFromWaitlist"
        title="Move to attending"
      >
        <i class="bi bi-arrow-up-circle"></i>
      </button>
      
      <button 
        class="btn btn-sm btn-outline-secondary"
        @click="contactMember"
        title="Send message"
      >
        <i class="bi bi-envelope"></i>
      </button>
      
      <button 
        v-if="canRemove"
        class="btn btn-sm btn-outline-danger"
        @click="removeRsvp"
        title="Remove RSVP"
      >
        <i class="bi bi-trash"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, defineProps, defineEmits } from 'vue'

const props = defineProps({
  rsvp: {
    type: Object,
    required: true
  },
  status: {
    type: String,
    required: true,
    validator: (value) => ['attending', 'maybe', 'waitlist', 'not-attending'].includes(value)
  },
  position: {
    type: Number,
    default: null
  },
  showEmail: {
    type: Boolean,
    default: false
  },
  showActions: {
    type: Boolean,
    default: false
  },
  canRemove: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['promote-waitlist', 'contact-member', 'remove-rsvp'])

// Computed properties
const memberName = computed(() => {
  const member = props.rsvp.member
  if (!member) return 'Unknown Member'
  
  if (member.firstName && member.lastName) {
    return `${member.firstName} ${member.lastName}`
  }
  
  return member.name || member.email || 'Unknown Member'
})

const memberInitials = computed(() => {
  const name = memberName.value
  const words = name.split(' ')
  
  if (words.length >= 2) {
    return `${words[0][0]}${words[1][0]}`.toUpperCase()
  }
  
  return name.substring(0, 2).toUpperCase()
})

const cardClass = computed(() => ({
  'card-attending': props.status === 'attending',
  'card-maybe': props.status === 'maybe',
  'card-waitlist': props.status === 'waitlist',
  'card-not-attending': props.status === 'not-attending'
}))

const statusBadgeClass = computed(() => ({
  'badge-attending': props.status === 'attending',
  'badge-maybe': props.status === 'maybe',
  'badge-waitlist': props.status === 'waitlist',
  'badge-not-attending': props.status === 'not-attending'
}))

const statusIcon = computed(() => {
  const iconMap = {
    'attending': 'bi bi-check-circle-fill',
    'maybe': 'bi bi-question-circle-fill', 
    'waitlist': 'bi bi-clock-history',
    'not-attending': 'bi bi-x-circle-fill'
  }
  return iconMap[props.status]
})

const statusLabel = computed(() => {
  const labelMap = {
    'attending': 'Going',
    'maybe': 'Maybe',
    'waitlist': 'Waitlist',
    'not-attending': 'Not Going'
  }
  return labelMap[props.status]
})

// Methods
const formatRole = (role) => {
  return role.replace(/_/g, ' ').toLowerCase()
    .split(' ')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ')
}

const formatTimeAgo = (timestamp) => {
  const now = new Date()
  const time = new Date(timestamp)
  const diffInHours = Math.floor((now - time) / (1000 * 60 * 60))
  
  if (diffInHours < 1) return 'just now'
  if (diffInHours < 24) return `${diffInHours}h ago`
  
  const diffInDays = Math.floor(diffInHours / 24)
  if (diffInDays === 1) return 'yesterday'
  if (diffInDays < 7) return `${diffInDays}d ago`
  
  const diffInWeeks = Math.floor(diffInDays / 7)
  if (diffInWeeks === 1) return 'last week'
  if (diffInWeeks < 4) return `${diffInWeeks}w ago`
  
  return time.toLocaleDateString()
}

const promoteFromWaitlist = () => {
  emit('promote-waitlist', props.rsvp)
}

const contactMember = () => {
  emit('contact-member', props.rsvp.member)
}

const removeRsvp = () => {
  emit('remove-rsvp', props.rsvp)
}
</script>

<style scoped>
.attendee-card {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 1rem;
  transition: all 0.2s ease;
  position: relative;
}

.attendee-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

/* Card Type Styling */
.card-attending {
  border-left: 4px solid #198754;
}

.card-maybe {
  border-left: 4px solid #ffc107;
}

.card-waitlist {
  border-left: 4px solid #0dcaf0;
}

.card-not-attending {
  border-left: 4px solid #dc3545;
  opacity: 0.7;
}

.member-info {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}

.avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #6c757d;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.875rem;
}

.member-details {
  flex: 1;
  min-width: 0;
}

.member-name {
  font-size: 0.875rem;
  font-weight: 600;
  margin: 0 0 0.25rem 0;
  color: #212529;
  line-height: 1.2;
}

.member-role,
.member-email {
  font-size: 0.75rem;
  color: #6c757d;
  margin: 0;
  line-height: 1.2;
}

.member-email {
  word-break: break-word;
}

.rsvp-info {
  margin-bottom: 0.75rem;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.badge-attending {
  background: rgba(25, 135, 84, 0.1);
  color: #198754;
}

.badge-maybe {
  background: rgba(255, 193, 7, 0.1);
  color: #ff8a00;
}

.badge-waitlist {
  background: rgba(13, 202, 240, 0.1);
  color: #0dcaf0;
}

.badge-not-attending {
  background: rgba(220, 53, 69, 0.1);
  color: #dc3545;
}

.waitlist-position {
  font-size: 0.75rem;
  font-weight: 600;
  color: #0dcaf0;
  margin-bottom: 0.25rem;
}

.rsvp-time {
  font-size: 0.6875rem;
  color: #6c757d;
  margin-bottom: 0.25rem;
}

.rsvp-notes {
  font-size: 0.75rem;
  color: #495057;
  display: flex;
  align-items: flex-start;
  gap: 0.25rem;
}

.rsvp-notes i {
  margin-top: 0.125rem;
  flex-shrink: 0;
}

.attendee-actions {
  display: flex;
  gap: 0.25rem;
  justify-content: flex-end;
}

.attendee-actions .btn {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
}

@media (max-width: 576px) {
  .attendee-card {
    padding: 0.75rem;
  }
  
  .member-info {
    gap: 0.5rem;
  }
  
  .avatar {
    width: 32px;
    height: 32px;
  }
  
  .member-name {
    font-size: 0.8125rem;
  }
}
</style>